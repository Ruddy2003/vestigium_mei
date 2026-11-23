package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo de arriba del estante de Mongol: el bodegón de la cocina sobre la cornisa (manda el caldero de cobre martillado que el
 * pastor trajo del oeste, con el cucharón metido; delante, el balde de ordeñar; a la derecha, la tabla de cuajadas secándose y
 * dos cuencos boca abajo; a la izquierda, el vellón doblado con el huso y su ovillo) y lo colgado (de una vara del techo, el
 * cordel con las tiras de carne seca; de una estaca del khana, el colador de crin). Todo con su sombra en el tapiz.
 */
internal object CoronaEstanteMongol {

    /** Hacia dónde cae la sombra de algo en la pared: de día abajo a la derecha; de noche, la luna la echa abajo y el hogar arriba. */
    private fun caida(g: MarcoMongol, p: Offset, largo: Float): Offset {
        if (!g.a.oscuro) return Offset(largo * 0.9f, largo * 0.55f)
        return Offset(-largo * 0.25f, largo * 0.8f)
    }

    // ---------------------------------------------------------------- lo colgado

    fun DrawScope.colgado(g: MarcoMongol) {
        carneSeca(g)
        colador(g, Offset(g.w * 0.945f, g.w * 0.215f))
        lampara(g)
    }

    /**
     * La lámpara de aceite de bronce que el pastor trajo de las ciudades del oeste, colgada de una vara del techo por su cadena
     * y sus tres ramales: el cuerpo panzón con su banda de puntos grabados (sin letras), la tapa con su perilla, el pie anular y
     * el pico largo con la mecha. De noche arde: la llama, su halo y el filo de luz en el bronce; de día, la mecha tiznada.
     */
    private fun DrawScope.lampara(g: MarcoMongol) {
        val u = density
        val w = g.w
        val a = g.a
        val llama = g.lampara
        val c = Offset(w * 0.8f, w * 0.235f)
        val l = g.luz(c)
        val an = w * 0.085f; val al = w * 0.045f
        val anillo = Offset(c.x, w * 0.135f)
        val sc = caida(g, c, w * 0.018f)
        // La cadena de la vara al anillo y los tres ramales al borde del cuerpo.
        val cadena = Path()
        var y = 0f
        while (y < anillo.y - w * 0.006f) { cadena.addOval(Rect(anillo.x - w * 0.003f, y, anillo.x + w * 0.003f, y + w * 0.009f)); y += w * 0.008f }
        val ramales = listOf(c.x - an * 0.42f, c.x, c.x + an * 0.42f)
        val lineas = Path().apply { ramales.forEach { x -> moveTo(anillo.x, anillo.y); lineTo(x, c.y - al * 0.42f) } }
        drawPath(corridoMongol(cadena, sc.x, sc.y), l.s(0.25f), style = Stroke(w * 0.0025f))
        drawPath(corridoMongol(lineas, sc.x, sc.y), l.s(0.25f), style = Stroke(w * 0.0025f))
        drawPath(cadena, l.c(P.BRONCE_OSC), style = Stroke(w * 0.0028f))
        drawPath(lineas, l.c(P.BRONCE_OSC), style = Stroke(w * 0.0028f))
        drawCircle(l.c(P.BRONCE), w * 0.008f, anillo, style = Stroke(w * 0.003f))
        // El pico, la mecha y el cuerpo.
        val pico = Path().apply {
            moveTo(c.x - an * 0.32f, c.y - al * 0.05f)
            cubicTo(c.x - an * 0.5f, c.y - al * 0.1f, llama.x + w * 0.012f, llama.y + w * 0.012f, llama.x - w * 0.002f, llama.y + w * 0.004f)
            lineTo(llama.x + w * 0.004f, llama.y + w * 0.014f)
            cubicTo(llama.x + w * 0.02f, llama.y + w * 0.02f, c.x - an * 0.45f, c.y + al * 0.25f, c.x - an * 0.3f, c.y + al * 0.25f)
            close()
        }
        val cuerpo = Path().apply {
            moveTo(c.x - an * 0.42f, c.y - al * 0.35f)
            cubicTo(c.x - an * 0.56f, c.y + al * 0.1f, c.x - an * 0.36f, c.y + al * 0.5f, c.x, c.y + al * 0.52f)
            cubicTo(c.x + an * 0.36f, c.y + al * 0.5f, c.x + an * 0.56f, c.y + al * 0.1f, c.x + an * 0.42f, c.y - al * 0.35f)
            close()
        }
        drawPath(corridoMongol(cuerpo, sc.x, sc.y), l.s(0.28f))
        drawPath(corridoMongol(pico, sc.x, sc.y), l.s(0.28f))
        val metal = if (!a.oscuro) listOf(P.BRONCE_LUZ, P.BRONCE, P.BRONCE_OSC) else listOf(P.BRONCE, mezcla(P.BRONCE, P.BRONCE_OSC, 0.5f), P.BRONCE_OSC)
        val claro = if (!a.oscuro) Offset(c.x - an * 0.2f, c.y - al * 0.2f) else Offset(llama.x, llama.y)
        drawPath(pico, Brush.radialGradient(metal.map { l.c(it) }, claro, an * 0.7f))
        drawPath(cuerpo, Brush.radialGradient(metal.map { l.c(it) }, claro, an * 0.75f))
        clipPath(cuerpo) {
            // La banda grabada: una hilera de puntos entre dos filetes (sin letras ni signos).
            val yb = c.y + al * 0.05f
            drawLine(l.s(0.45f), Offset(c.x - an, yb - al * 0.12f), Offset(c.x + an, yb - al * 0.12f), 0.8f * u)
            drawLine(l.s(0.45f), Offset(c.x - an, yb + al * 0.12f), Offset(c.x + an, yb + al * 0.12f), 0.8f * u)
            var x = c.x - an * 0.45f
            while (x < c.x + an * 0.46f) { drawCircle(l.s(0.4f), 0.9f * u, Offset(x, yb)); x += an * 0.09f }
            drawOval(if (a.oscuro) l.filo(0.8f) else luzDuraMongol(0.75f), Offset(c.x - an * 0.3f, c.y - al * 0.25f), Size(an * 0.18f, al * 0.18f))
        }
        // La tapa con su perilla y el pie anular.
        drawOval(l.c(P.BRONCE_OSC), Offset(c.x - an * 0.44f, c.y - al * 0.48f), Size(an * 0.88f, al * 0.26f))
        drawArc(l.c(P.BRONCE), 180f, 180f, true, Offset(c.x - an * 0.24f, c.y - al * 0.78f), Size(an * 0.48f, al * 0.62f))
        drawCircle(l.c(P.BRONCE), w * 0.005f, Offset(c.x, c.y - al * 0.82f))
        drawArc(if (a.oscuro) l.filo(0.8f) else l.b(0.6f), 200f, 100f, false, Offset(c.x - an * 0.24f, c.y - al * 0.78f), Size(an * 0.48f, al * 0.62f), style = Stroke(1f * u))
        drawRoundRect(l.c(P.BRONCE_OSC), Offset(c.x - an * 0.16f, c.y + al * 0.46f), Size(an * 0.32f, al * 0.16f), androidx.compose.ui.geometry.CornerRadius(al * 0.06f))
        // La mecha: tiznada de día; de noche, la llama con su halo.
        drawLine(Color(0xFF241812), Offset(llama.x, llama.y + w * 0.008f), Offset(llama.x - w * 0.002f, llama.y + w * 0.001f), 1.6f * u, StrokeCap.Round)
        if (a.lampara > 0f) llamaLampara(llama, w, a.lampara)
    }

    /** La llama de la lámpara: una gota en tres capas que se inclina un poco, con su hilo de humo. */
    private fun DrawScope.llamaLampara(c: Offset, w: Float, fuerza: Float) {
        val u = density
        fun gota(alto: Float, ancho: Float) = Path().apply {
            moveTo(c.x + ancho * 0.12f, c.y - alto * 0.7f)
            cubicTo(c.x + ancho * 0.35f, c.y - alto * 0.3f, c.x + ancho * 0.55f, c.y + alto * 0.05f, c.x, c.y + alto * 0.3f)
            cubicTo(c.x - ancho * 0.55f, c.y + alto * 0.05f, c.x - ancho * 0.25f, c.y - alto * 0.35f, c.x + ancho * 0.12f, c.y - alto * 0.7f)
            close()
        }
        val s = 0.7f + 0.3f * fuerza
        drawPath(Path().apply { moveTo(c.x + w * 0.003f, c.y - w * 0.03f * s); cubicTo(c.x + w * 0.012f, c.y - w * 0.05f, c.x - w * 0.006f, c.y - w * 0.065f, c.x + w * 0.004f, c.y - w * 0.09f) },
            Color(0xFF2A1A14).copy(alpha = 0.22f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawPath(gota(w * 0.04f * s, w * 0.022f * s), Color(0xFFE8822E).copy(alpha = 0.9f))
        drawPath(gota(w * 0.028f * s, w * 0.015f * s), P.LLAMA)
        drawPath(gota(w * 0.016f * s, w * 0.008f * s), Color(0xFFFFF0C4))
    }

    /** El cordel de crin tendido entre dos varas del techo, con las tiras de carne seca (borts) colgando, oscuras y retorcidas. */
    private fun DrawScope.carneSeca(g: MarcoMongol) {
        val u = density
        val w = g.w
        val a0 = Offset(w * 0.02f, w * 0.098f); val a1 = Offset(w * 0.34f, w * 0.118f)
        val l = g.luz(Offset(w * 0.18f, w * 0.18f))
        val comba = w * 0.05f
        val cordel = Path().apply { moveTo(a0.x, a0.y); quadraticTo((a0.x + a1.x) / 2, a0.y + comba * 2f, a1.x, a1.y) }
        val cd = caida(g, a0, w * 0.014f)
        drawPath(corridoMongol(cordel, cd.x, cd.y), l.s(0.3f), style = Stroke(1.6f * u))
        drawPath(cordel, l.c(P.CRIN), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        // Las tiras: cada una echada sobre el cordel (la punta de atrás asoma corta) y colgando delante, ancha arriba, retorcida
        // y encogida abajo, con sus vetas de grasa seca.
        val n = 6
        for (i in 0 until n) {
            val f = (i + 0.35f + 0.3f * azar(905, i)) / n
            val x = a0.x + (a1.x - a0.x) * f
            val y = a0.y + (a1.y - a0.y) * f + comba * 4f * f * (1f - f)
            val largo = w * (0.1f + 0.09f * azar(901, i))
            val ancho = w * (0.017f + 0.009f * azar(902, i))
            val tuerce = (azar(903, i) - 0.5f) * w * 0.03f
            val lt = g.luz(Offset(x, y + largo / 2))
            val col = mezcla(P.CARNE, P.HOLLIN, 0.15f + 0.3f * azar(904, i))
            // La punta de atrás, corta y en sombra.
            val atras = poligonoMongol(x - ancho * 0.5f, y, x + ancho * 0.5f, y, x + ancho * 0.4f + w * 0.004f, y + largo * 0.22f, x - ancho * 0.3f + w * 0.004f, y + largo * 0.25f)
            drawPath(atras, lt.c(mezcla(col, P.HOLLIN, 0.45f)))
            val tira = Path().apply {
                moveTo(x - ancho * 0.55f, y - w * 0.006f)
                cubicTo(x - ancho * 0.75f, y + largo * 0.35f, x + tuerce - ancho * 0.6f, y + largo * 0.62f, x + tuerce - ancho * 0.25f, y + largo * 0.92f)
                quadraticTo(x + tuerce, y + largo * 1.04f, x + tuerce + ancho * 0.25f, y + largo * 0.9f)
                cubicTo(x + tuerce + ancho * 0.5f, y + largo * 0.6f, x + ancho * 0.75f, y + largo * 0.32f, x + ancho * 0.55f, y - w * 0.006f)
                quadraticTo(x, y - w * 0.013f, x - ancho * 0.55f, y - w * 0.006f)
                close()
            }
            val sc = caida(g, Offset(x, y), w * 0.016f)
            drawPath(corridoMongol(tira, sc.x, sc.y), lt.s(0.3f))
            drawPath(tira, lt.c(col))
            clipPath(tira) {
                // Las vetas de grasa seca, a lo largo y torcidas; la luz de un lado, la sombra del otro y el doblez sobre el cordel.
                for (q in 0 until 2) {
                    val dx = (q - 0.5f) * ancho * 0.5f
                    drawPath(Path().apply { moveTo(x + dx, y + largo * 0.05f); quadraticTo(x + dx - ancho * 0.3f, y + largo * 0.5f, x + tuerce + dx * 0.4f, y + largo * 0.95f) },
                        lt.c(mezcla(P.CARNE, P.CRUDO, 0.5f)).copy(alpha = 0.55f), style = Stroke(0.9f * u))
                }
                drawRect(lt.b(0.25f), Offset(x - ancho, y - w * 0.01f), Size(ancho * 0.6f, largo * 1.1f))
                drawRect(lt.s(0.32f), Offset(x + ancho * 0.18f + tuerce * 0.4f, y - w * 0.01f), Size(ancho, largo * 1.1f))
                drawRect(lt.b(0.35f), Offset(x - ancho, y - w * 0.008f), Size(ancho * 2, 1.2f * u))
            }
        }
        // Las dos estacas que sujetan el cordel, en las varas.
        listOf(a0, a1).forEach { p -> clavoMongol(p, 2.4f * u, l, P.ALERCE) }
    }

    /** El colador de crin: un aro de madera curvada con su malla de crin tejida, colgado de una estaca del khana por su lazo. */
    private fun DrawScope.colador(g: MarcoMongol, c: Offset) {
        val u = density
        val w = g.w
        val r = w * 0.058f
        val l = g.luz(c)
        val clavo = Offset(c.x + r * 0.1f, c.y - r * 1.55f)
        val sc = caida(g, c, w * 0.016f)
        // Lazo, sombra y aro.
        drawLine(l.s(0.3f), clavo + sc * 0.5f, Offset(c.x, c.y - r) + sc, 1.4f * u)
        drawLine(l.c(P.CRUDO), clavo, Offset(c.x - r * 0.15f, c.y - r * 0.98f), 1.4f * u)
        drawLine(l.c(P.CRUDO), clavo, Offset(c.x + r * 0.25f, c.y - r * 0.96f), 1.4f * u)
        rotate(-8f, c) {
            drawCircle(l.s(0.3f), r, c + sc, style = Stroke(w * 0.012f))
            drawCircle(l.c(mezcla(P.CRIN, P.CREMA, 0.12f)).copy(alpha = 0.85f), r * 0.94f, c)
            // La malla: hilos cruzados.
            val malla = Path()
            var k = -r
            while (k <= r) {
                malla.moveTo(c.x + k, c.y - r); malla.lineTo(c.x + k, c.y + r)
                malla.moveTo(c.x - r, c.y + k); malla.lineTo(c.x + r, c.y + k)
                k += r * 0.16f
            }
            clipPath(Path().apply { addOval(Rect(c, r * 0.94f)) }) {
                drawPath(malla, l.c(mezcla(P.CRIN, P.CRUDO, 0.4f)), style = Stroke(0.6f * u))
                drawCircle(l.b(0.12f), r * 0.6f, c - Offset(r * 0.3f, r * 0.3f))
            }
            drawCircle(l.c(P.ALERCE), r, c, style = Stroke(w * 0.012f))
            drawArc(l.b(0.5f), 150f, 120f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(w * 0.004f))
            drawArc(l.s(0.4f), -30f, 120f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(w * 0.005f))
            // La costura de crudo que cierra el aro.
            for (q in 0 until 4) {
                val ang = Math.toRadians(70.0 + q * 7).toFloat()
                val p = Offset(c.x + r * cos(ang), c.y + r * sin(ang))
                drawLine(l.c(P.CRUDO), p - Offset(w * 0.006f, 0f), p + Offset(w * 0.006f, 0f), 1.1f * u)
            }
        }
        clavoMongol(clavo, 2.2f * u, l, P.ALERCE)
    }

    // ---------------------------------------------------------------- encima de la cornisa

    fun DrawScope.encima(g: MarcoMongol) {
        val w = g.w
        val y = g.apoyo
        vellon(g, Rect(-w * 0.02f, y - w * 0.07f, w * 0.15f, y - w * 0.004f))
        caldero(g, Offset(w * 0.49f, y - w * 0.014f), w * 0.37f)
        balde(g, Offset(w * 0.25f, y - w * 0.002f), w * 0.14f)
        tablaCuajadas(g, w * 0.66f, w * 0.905f, y - w * 0.006f)
        cuencos(g, Offset(w * 0.955f, y - w * 0.006f), w * 0.1f)
    }

    /**
     * El caldero grande de cobre martillado, sentado en su rodete de fieltro: el cuerpo hondo y panzón modelado como metal (el
     * claro arriba a la izquierda, el núcleo de sombra, el reflejo cálido del borde y el brillo duro del toono), las facetas
     * del martillo, el labio grueso enrollado con la boca oscura, las dos asas de anilla y el cucharón de madera metido que
     * asoma en diagonal. De noche, el fuego del hogar se refleja en la panza abajo a la derecha y la luna roza el labio.
     */
    private fun DrawScope.caldero(g: MarcoMongol, pie: Offset, ancho: Float) {
        val u = density
        val w = g.w
        val a = g.a
        val alto = ancho * 0.6f
        val l = g.luz(Offset(pie.x, pie.y - alto / 2))
        val boca = pie.y - alto
        val x0 = pie.x - ancho / 2; val x1 = pie.x + ancho / 2
        val cuerpo = Path().apply {
            moveTo(pie.x - ancho * 0.45f, boca + alto * 0.05f)
            cubicTo(pie.x - ancho * 0.52f, boca + alto * 0.3f, pie.x - ancho * 0.5f, pie.y - alto * 0.22f, pie.x - ancho * 0.3f, pie.y - alto * 0.04f)
            quadraticTo(pie.x, pie.y + alto * 0.04f, pie.x + ancho * 0.3f, pie.y - alto * 0.04f)
            cubicTo(pie.x + ancho * 0.5f, pie.y - alto * 0.22f, pie.x + ancho * 0.52f, boca + alto * 0.3f, pie.x + ancho * 0.45f, boca + alto * 0.05f)
            close()
        }
        // El cucharón, detrás del labio: el mango sale de la boca hacia arriba a la derecha.
        val m0 = Offset(pie.x + ancho * 0.1f, boca + alto * 0.04f)
        val m1 = Offset(pie.x + ancho * 0.44f, boca - alto * 0.72f)
        val sc = caida(g, m1, w * 0.02f)
        drawLine(l.s(0.28f), m0 + sc, m1 + sc, w * 0.012f, StrokeCap.Round)
        drawLine(l.c(P.ALERCE), m0, m1, w * 0.012f, StrokeCap.Round)
        drawLine(l.b(0.45f), m0 + Offset(-w * 0.004f, 0f), m1 + Offset(-w * 0.004f, 0f), w * 0.003f)
        drawLine(l.s(0.35f), m0 + Offset(w * 0.004f, w * 0.002f), m1 + Offset(w * 0.004f, w * 0.002f), w * 0.003f)
        drawCircle(l.c(P.ALERCE_OSC), w * 0.004f, m1 - (m1 - m0) * 0.06f)
        // La sombra del caldero en el tapiz y en la cornisa.
        val sh = caida(g, pie, w * 0.03f)
        drawPath(corridoMongol(cuerpo, sh.x, sh.y * 0.6f), l.s(if (a.oscuro) 0.4f else 0.24f))
        drawOval(l.s(0.5f), Offset(x0 + ancho * 0.1f, pie.y - w * 0.008f), Size(ancho * 0.9f, w * 0.018f))
        // El rodete de fieltro, bajo el culo del caldero.
        val rod = Rect(pie.x - ancho * 0.26f, pie.y - w * 0.016f, pie.x + ancho * 0.26f, pie.y + w * 0.004f)
        drawRoundRect(l.c(P.BERMELLON), rod.topLeft, rod.size, androidx.compose.ui.geometry.CornerRadius(rod.height / 2))
        drawRoundRect(l.b(0.3f), rod.topLeft, Size(rod.width, rod.height * 0.4f), androidx.compose.ui.geometry.CornerRadius(rod.height / 2))
        drawLine(l.c(P.CREMA).copy(alpha = 0.7f), Offset(rod.left + rod.height * 0.4f, rod.center.y), Offset(rod.right - rod.height * 0.4f, rod.center.y), 1f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u), 0f))
        // El cuerpo como metal: el claro, el tono, el núcleo de sombra y el reflejo del borde (de día, del suelo alumbrado;
        // de noche todo cae a lo hondo y el reflejo es el del fuego).
        // De día el claro está arriba a la izquierda (el toono); de noche, del lado de la lámpara, y el resto cae a lo hondo.
        val claro = if (!a.oscuro) Offset(x0 + ancho * 0.3f, boca + alto * 0.3f) else Offset(x1 - ancho * 0.2f, boca + alto * 0.25f)
        val metal = if (!a.oscuro) listOf(P.COBRE_LUZ, P.COBRE, mezcla(P.COBRE, P.COBRE_OSC, 0.55f), P.COBRE_OSC, mezcla(P.COBRE_OSC, P.COBRE, 0.55f))
            else listOf(P.COBRE, mezcla(P.COBRE, P.COBRE_OSC, 0.6f), P.COBRE_OSC, mezcla(P.COBRE_OSC, P.HOLLIN, 0.55f), mezcla(P.COBRE_OSC, P.HOLLIN, 0.65f))
        val lm = if (!a.oscuro) l else a.en(l.k * 0.55f, l.f)
        drawPath(cuerpo, Brush.radialGradient(metal.map { lm.c(it) }, claro, ancho * 0.85f))
        clipPath(cuerpo) {
            // Las facetas del martillo: hileras de huellas chicas y tenues que siguen la panza.
            for (fila in 0 until 7) {
                val yy = boca + alto * (0.12f + 0.12f * fila)
                val n = 11 - kotlin.math.abs(fila - 3)
                val semi = ancho * 0.48f * (1f - 0.35f * ((fila - 2f) / 5f).coerceAtLeast(0f))
                for (j in 0 until n) {
                    val t = (j + 0.5f * (fila % 2)) / n
                    val xx = pie.x - semi + 2f * semi * t
                    val rr = w * (0.0055f + 0.003f * azar(910 + fila, j))
                    drawOval(l.s(0.1f), Offset(xx - rr + rr * 0.35f, yy - rr * 0.45f + rr * 0.3f), Size(rr * 2f, rr * 1.1f))
                    drawOval(l.b(0.12f), Offset(xx - rr, yy - rr * 0.45f), Size(rr * 1.3f, rr * 0.55f))
                }
            }
            // La soldadura del fondo, con su fila de remaches.
            val sold = Path().apply { moveTo(x0, pie.y - alto * 0.32f); quadraticTo(pie.x, pie.y - alto * 0.14f, x1, pie.y - alto * 0.32f) }
            drawPath(sold, l.s(0.35f), style = Stroke(1.3f * u))
            drawPath(corridoMongol(sold, 0f, 1.3f * u), l.b(0.2f), style = Stroke(0.8f * u))
            for (q in 0 until 9) {
                val t = (q + 0.5f) / 9f
                val xx = x0 + ancho * t
                val yy = pie.y - alto * 0.32f + (alto * 0.36f) * t * (1f - t) - 2.4f * u
                drawCircle(l.c(mezcla(P.COBRE_OSC, P.COBRE, 0.5f)), 1.5f * u, Offset(xx, yy))
                drawCircle(l.b(0.4f), 0.6f * u, Offset(xx - 0.5f * u, yy - 0.5f * u))
            }
            if (!a.oscuro) {
                // El reflejo cálido del borde de la derecha y de abajo (lo que alumbra el sol del suelo, rebotado).
                drawPath(bordeMongol(cuerpo, -ancho * 0.07f, -alto * 0.05f), l.c(mezcla(P.COBRE, P.COBRE_LUZ, 0.25f)).copy(alpha = 0.75f))
                // El brillo duro del toono: la mancha clara arriba a la izquierda y su raya (al anochecer, rosada y más baja: la
                // lámpara recién encendida brilla más que el cobre).
                val dura = if (a.noche) mezcla(Color(0xFFFFF6E2), P.CREPUSCULO, 0.45f) else Color(0xFFFFF6E2)
                val fuerza = if (a.noche) 0.45f else 0.9f
                drawOval(l.b(0.5f), Offset(x0 + ancho * 0.14f, boca + alto * 0.12f), Size(ancho * 0.22f, alto * 0.24f))
                drawRoundRect(dura.copy(alpha = fuerza), Offset(x0 + ancho * 0.19f, boca + alto * 0.14f), Size(ancho * 0.05f, alto * 0.36f), androidx.compose.ui.geometry.CornerRadius(ancho * 0.025f))
                drawCircle(dura.copy(alpha = fuerza * 1.05f), ancho * 0.02f, Offset(x0 + ancho * 0.3f, boca + alto * 0.2f))
            } else {
                // De noche: el fuego reflejado en la panza abajo a la derecha (una media luna que sigue la forma) y su punto.
                // De noche: la lámpara colgada se refleja en el hombro de la derecha (un charco cálido y su brillo duro) y el hogar,
                // abajo, apenas tiñe el borde que lo mira.
                val fr = a.fuego
                val brillo = Offset(x1 - ancho * 0.22f, boca + alto * 0.28f)
                if (a.lampara > 0f) {
                    // El reflejo es menos que la llama: cálido, chico y sin blanco.
                    drawCircle(Brush.radialGradient(listOf(fr.copy(alpha = 0.42f * a.lampara), fr.copy(alpha = 0f)), brillo, ancho * 0.22f), ancho * 0.22f, brillo)
                    drawRoundRect(mezcla(P.LLAMA, Color(0xFFFFE6B0), 0.4f).copy(alpha = 0.55f * a.lampara), Offset(brillo.x - ancho * 0.013f, brillo.y - alto * 0.12f), Size(ancho * 0.026f, alto * 0.2f),
                        androidx.compose.ui.geometry.CornerRadius(ancho * 0.013f))
                }
                val abajo = Offset(x1 - ancho * 0.12f, pie.y - alto * 0.05f)
                drawPath(bordeMongol(cuerpo, -ancho * 0.05f, -alto * 0.06f),
                    Brush.radialGradient(listOf(fr.copy(alpha = if (a.llamas) 0.4f else 0.22f), fr.copy(alpha = 0f)), abajo, ancho * 0.4f))
                drawOval(l.frio(0.35f), Offset(x0 + ancho * 0.18f, boca + alto * 0.1f), Size(ancho * 0.3f, alto * 0.12f))
            }
        }
        // Las asas de anilla, prendidas de sus orejetas bajo el labio.
        listOf(-1f, 1f).forEach { s ->
            val o = Offset(pie.x + s * ancho * 0.46f, boca + alto * 0.17f)
            val ra = ancho * 0.065f
            val c = o + Offset(s * ra * 0.55f, ra * 0.9f)
            drawCircle(l.s(0.4f), ra, c + Offset(2f * u, 2f * u), style = Stroke(w * 0.007f))
            drawCircle(l.c(mezcla(P.COBRE_OSC, P.HIERRO, 0.35f)), ra, c, style = Stroke(w * 0.007f))
            drawArc(if (a.oscuro) l.filo(0.8f) else l.b(0.6f), if (a.oscuro) 0f else 180f, 100f, false, c - Offset(ra, ra), Size(ra * 2, ra * 2), style = Stroke(w * 0.0025f))
            drawRoundRect(l.c(P.COBRE_OSC), Offset(o.x - w * 0.008f, o.y - w * 0.01f), Size(w * 0.016f, w * 0.02f), androidx.compose.ui.geometry.CornerRadius(w * 0.004f))
            clavoMongol(o, 1.4f * u, l, P.COBRE)
        }
        // El labio: un rollo grueso de cobre enrollado; la boca oscura con el cobre de dentro (se ve desde un poco arriba).
        val lab = Rect(pie.x - ancho * 0.47f, boca - alto * 0.08f, pie.x + ancho * 0.47f, boca + alto * 0.1f)
        drawOval(l.c(mezcla(P.COBRE_OSC, P.HOLLIN, 0.55f)), lab.topLeft, lab.size)
        drawOval(l.c(P.COBRE_OSC), Offset(lab.left + ancho * 0.05f, lab.center.y - alto * 0.005f), Size(lab.width * 0.6f, lab.height * 0.42f))
        drawOval(l.c(mezcla(P.COBRE, P.COBRE_OSC, 0.3f)), Offset(lab.left + ancho * 0.08f, lab.center.y + alto * 0.012f), Size(lab.width * 0.36f, lab.height * 0.2f))
        drawOval(l.c(P.COBRE), lab.topLeft, lab.size, style = Stroke(w * 0.011f))
        drawArc(if (a.oscuro) l.frio(0.9f) else luzDuraMongol(0.85f), 175f, 95f, false, lab.topLeft, lab.size, style = Stroke(w * 0.0035f))
        drawArc(l.s(0.4f), 10f, 150f, false, Offset(lab.left, lab.top + w * 0.004f), lab.size, style = Stroke(w * 0.003f))
        if (a.oscuro) drawArc(l.filo(0.9f), 10f, 70f, false, lab.topLeft, lab.size, style = Stroke(w * 0.003f))
        // El cucharón que se ve dentro, por encima del labio de atrás.
        drawLine(l.c(P.ALERCE), m0, m0 + (m1 - m0) * 0.1f, w * 0.012f, StrokeCap.Round)
    }

    /**
     * El balde de ordeñar: duelas de alerce un poco abiertas hacia arriba, dos aros de cuero crudo cosidos, el asa de madera
     * curvada, la leche dentro con su brillo; delante del caldero, más abajo (está más cerca).
     */
    private fun DrawScope.balde(g: MarcoMongol, pie: Offset, ancho: Float) {
        val u = density
        val w = g.w
        val alto = ancho * 0.86f
        val l = g.luz(Offset(pie.x, pie.y - alto / 2))
        val top = pie.y - alto
        val cuerpo = poligonoMongol(pie.x - ancho * 0.5f, top, pie.x + ancho * 0.5f, top, pie.x + ancho * 0.42f, pie.y, pie.x - ancho * 0.42f, pie.y)
        val sh = caida(g, pie, w * 0.024f)
        drawPath(corridoMongol(cuerpo, sh.x, sh.y * 0.5f), l.s(0.24f))
        drawOval(l.s(0.45f), Offset(pie.x - ancho * 0.44f, pie.y - w * 0.007f), Size(ancho * 0.96f, w * 0.014f))
        // El asa, detrás del borde de atrás: un arco de madera curvada con sus pasadores.
        val asa = Path().apply { moveTo(pie.x - ancho * 0.46f, top + alto * 0.08f); cubicTo(pie.x - ancho * 0.4f, top - alto * 0.75f, pie.x + ancho * 0.4f, top - alto * 0.75f, pie.x + ancho * 0.46f, top + alto * 0.08f) }
        drawPath(corridoMongol(asa, sh.x * 0.6f, sh.y * 0.4f), l.s(0.25f), style = Stroke(w * 0.007f))
        drawPath(asa, l.c(mezcla(P.ALERCE, P.ALERCE_OSC, 0.2f)), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(corridoMongol(asa, -w * 0.002f, -w * 0.002f), l.b(0.4f), style = Stroke(w * 0.002f))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(l.c(P.ALERCE_LUZ), l.c(P.ALERCE), l.c(P.ALERCE), l.c(P.ALERCE_OSC)), pie.x - ancho / 2, pie.x + ancho / 2))
        clipPath(cuerpo) {
            // Las duelas.
            for (j in 1 until 6) {
                val fx = j / 6f
                val xt = pie.x - ancho * 0.5f + ancho * fx; val xb = pie.x - ancho * 0.42f + ancho * 0.84f * fx
                drawLine(l.s(0.45f), Offset(xt, top), Offset(xb, pie.y), 1.1f * u)
                drawLine(l.b(0.2f), Offset(xt + 1.1f * u, top), Offset(xb + 1.1f * u, pie.y), 0.6f * u)
            }
            vetas(Rect(pie.x - ancho / 2, top, pie.x + ancho / 2, pie.y), l.c(P.ALERCE_OSC).copy(alpha = 0.3f), 920, 0.6f, vertical = true, grosor = 0.6f)
            // Los aros de cuero crudo, con su costura.
            listOf(0.22f, 0.76f).forEach { f ->
                val y = top + alto * f
                drawRect(l.c(P.CRUDO), Offset(pie.x - ancho, y - w * 0.007f), Size(ancho * 2, w * 0.014f))
                drawRect(l.s(0.3f), Offset(pie.x - ancho, y + w * 0.005f), Size(ancho * 2, w * 0.003f))
                drawLine(l.c(P.CUERO), Offset(pie.x - ancho, y), Offset(pie.x + ancho, y), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u), 0f))
            }
        }
        // La boca: el borde de las duelas y la leche.
        val boca = Rect(pie.x - ancho * 0.5f, top - alto * 0.09f, pie.x + ancho * 0.5f, top + alto * 0.09f)
        drawOval(l.c(P.ALERCE_OSC), boca.topLeft, boca.size)
        drawOval(l.c(P.CUAJADA), Offset(boca.left + ancho * 0.05f, boca.top + alto * 0.03f), Size(boca.width - ancho * 0.1f, boca.height - alto * 0.05f))
        drawOval(l.b(0.6f), Offset(boca.left + ancho * 0.2f, boca.top + alto * 0.05f), Size(ancho * 0.24f, alto * 0.04f))
        drawOval(l.c(P.ALERCE), boca.topLeft, boca.size, style = Stroke(w * 0.004f))
    }

    /**
     * La tabla de cuajadas: una tabla de alerce sobre dos tacos con los trozos de requesón (aaruul) secándose en dos hileras,
     * cada uno con su cara de luz y su sombra.
     */
    private fun DrawScope.tablaCuajadas(g: MarcoMongol, x0: Float, x1: Float, pie: Float) {
        val u = density
        val w = g.w
        val l = g.luz(Offset((x0 + x1) / 2, pie - w * 0.03f))
        val tacoH = w * 0.016f
        val tab = Rect(x0, pie - tacoH - w * 0.014f, x1, pie - tacoH)
        val sh = caida(g, Offset(x1, pie), w * 0.02f)
        drawRect(l.s(0.24f), Offset(tab.left + sh.x, tab.top - w * 0.03f + sh.y), Size(tab.width, tab.height + w * 0.03f))
        drawOval(l.s(0.45f), Offset(x0, pie - w * 0.006f), Size(x1 - x0, w * 0.012f))
        listOf(x0 + w * 0.02f, x1 - w * 0.05f).forEach { x ->
            drawRect(l.c(P.ALERCE_OSC), Offset(x, pie - tacoH), Size(w * 0.03f, tacoH))
            drawRect(l.b(0.3f), Offset(x, pie - tacoH), Size(1.2f * u, tacoH))
        }
        // Las cuajadas de atrás (más chicas, un poco más arriba) y las de delante.
        for (fila in 0..1) {
            val n = if (fila == 0) 7 else 6
            for (j in 0 until n) {
                val f = (j + 0.5f + 0.3f * fila) / (n + 0.3f)
                val cx = x0 + (x1 - x0) * f + (azar(930 + fila, j) - 0.5f) * w * 0.01f
                val aw = w * (0.026f + 0.01f * azar(931 + fila, j)) * (if (fila == 0) 0.85f else 1f)
                val ah = aw * (0.5f + 0.2f * azar(932 + fila, j))
                val base = tab.top + (if (fila == 0) -w * 0.008f else w * 0.002f)
                val pz = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(cx - aw / 2, base - ah, cx + aw / 2, base), androidx.compose.ui.geometry.CornerRadius(aw * 0.3f))) }
                drawPath(corridoMongol(pz, aw * 0.18f, ah * 0.12f), l.s(0.3f))
                drawPath(pz, l.c(mezcla(P.CUAJADA, P.CRUDO, 0.12f + 0.18f * azar(933 + fila, j))))
                drawRect(l.b(0.5f), Offset(cx - aw * 0.4f, base - ah), Size(aw * 0.5f, ah * 0.3f))
                drawRect(l.s(0.18f), Offset(cx + aw * 0.1f, base - ah * 0.7f), Size(aw * 0.4f, ah * 0.7f))
            }
        }
        // El canto de la tabla, delante de las cuajadas de abajo.
        drawRect(l.c(P.ALERCE), tab.topLeft, tab.size)
        vetas(tab, l.c(P.ALERCE_OSC).copy(alpha = 0.45f), 934, 0.8f, grosor = 0.7f)
        drawRect(l.b(0.45f), tab.topLeft, Size(tab.width, 1.2f * u))
        drawRect(l.s(0.4f), Offset(tab.left, tab.bottom - 1.2f * u), Size(tab.width, 1.2f * u))
    }

    /** Dos cuencos de madera boca abajo, uno sobre otro, cortados por el borde de la derecha. */
    private fun DrawScope.cuencos(g: MarcoMongol, pie: Offset, ancho: Float) {
        val u = density
        val w = g.w
        val l = g.luz(Offset(pie.x, pie.y - ancho * 0.3f))
        for (i in 0..1) {
            val an = ancho * (if (i == 0) 1f else 0.86f)
            val al = an * 0.4f
            val b = pie.y - i * ancho * 0.34f
            val cx = pie.x + i * ancho * 0.04f
            val c = Path().apply {
                moveTo(cx - an / 2, b)
                cubicTo(cx - an / 2, b - al * 0.8f, cx - an * 0.25f, b - al, cx - an * 0.18f, b - al)
                lineTo(cx + an * 0.18f, b - al)
                cubicTo(cx + an * 0.25f, b - al, cx + an / 2, b - al * 0.8f, cx + an / 2, b)
                close()
            }
            val sh = caida(g, pie, w * 0.016f)
            drawPath(corridoMongol(c, sh.x, sh.y * 0.4f), l.s(0.25f))
            drawPath(c, Brush.horizontalGradient(listOf(l.c(P.ALERCE_LUZ), l.c(P.ALERCE), l.c(P.ALERCE_OSC)), cx - an / 2, cx + an / 2))
            clipPath(c) { vetas(c.getBounds(), l.c(P.ALERCE_OSC).copy(alpha = 0.35f), 940 + i, 0.6f) }
            drawLine(l.c(P.ALERCE_OSC), Offset(cx - an / 2, b), Offset(cx + an / 2, b), 1.6f * u)
            drawArc(l.b(0.5f), 200f, 60f, false, Offset(cx - an * 0.45f, b - al * 0.95f), Size(an * 0.9f, al * 1.6f), style = Stroke(1.4f * u))
        }
    }

    /**
     * El vellón de lana cruda amontonado contra el borde de la izquierda: mechones sueltos (cada uno, tres copos redondos con su
     * luz y su sombra, y la punta enroscada y más sucia). Clavado en el montón, el huso de madera de pie y algo ladeado, con la
     * tortera de hueso medio hundida en la lana y la husada de hilo ya hilado; al lado, el ovillo teñido y la hebra que baja.
     */
    private fun DrawScope.vellon(g: MarcoMongol, r: Rect) {
        val u = density
        val w = g.w
        val l = g.luz(r.center)
        val lana = mezcla(P.CREMA, P.CRUDO, 0.18f)
        val sucia = mezcla(P.CRUDO, P.FIELTRO_GRIS, 0.55f)
        val sh = caida(g, r.center, w * 0.018f)
        drawOval(l.s(0.3f), Offset(r.left + sh.x, r.bottom - r.height * 0.4f), Size(r.width * 1.08f, r.height * 0.5f))
        fun mechon(c: Offset, rr: Float, q: Int) {
            val ang = (azar(953, q) - 0.5f) * 1.1f
            val d = Offset(cos(ang) * rr * 0.85f, sin(ang) * rr * 0.45f)
            val copos = listOf(c - d, c, c + d)
            copos.forEach { p -> drawCircle(l.s(0.28f), rr * 0.72f, p + Offset(rr * 0.25f, rr * 0.32f)) }
            copos.forEachIndexed { k, p -> drawCircle(l.c(if (k == 2 && q % 3 == 0) mezcla(lana, sucia, 0.45f) else lana), rr * 0.7f, p) }
            copos.forEach { p -> drawCircle(l.b(0.5f), rr * 0.34f, p - Offset(rr * 0.24f, rr * 0.28f)) }
            drawArc(l.s(0.32f), 20f, 140f, false, Offset(c.x - rr * 0.45f, c.y - rr * 0.15f), Size(rr * 0.9f, rr * 0.55f), style = Stroke(0.8f * u))
            val punta = c + d * 1.9f
            drawArc(l.c(sucia), 200f, 250f, false, Offset(punta.x - rr * 0.38f, punta.y - rr * 0.38f), Size(rr * 0.76f, rr * 0.76f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        // Los de atrás, más arriba.
        for (q in 0 until 7) {
            val c = Offset(r.left + r.width * (0.06f + 0.15f * q + 0.04f * azar(954, q)), r.top + r.height * (0.3f + 0.22f * azar(955, q)))
            mechon(c, w * (0.016f + 0.006f * azar(956, q)), q)
        }
        // El huso, clavado en el montón: la vara, la tortera medio hundida, la husada de hilo y el gancho de arriba.
        val h0 = Offset(r.left + r.width * 0.58f, r.top + r.height * 0.6f)
        val h1 = h0 + Offset(w * 0.03f, -w * 0.15f)
        val eje = h1 - h0
        drawLine(l.s(0.3f), h0 + Offset(2f * u, 1f * u), h1 + Offset(2f * u, 1f * u), w * 0.0065f)
        drawLine(l.c(P.ALERCE), h0, h1, w * 0.0062f, StrokeCap.Round)
        drawLine(l.b(0.45f), h0 + Offset(-w * 0.0015f, 0f), h1 + Offset(-w * 0.0015f, 0f), w * 0.0014f)
        val tor = h0 + eje * 0.14f
        drawOval(l.s(0.35f), Offset(tor.x - w * 0.02f, tor.y - w * 0.004f), Size(w * 0.044f, w * 0.016f))
        drawOval(l.c(P.HUESO), Offset(tor.x - w * 0.022f, tor.y - w * 0.008f), Size(w * 0.044f, w * 0.016f))
        drawArc(l.s(0.4f), 0f, 180f, false, Offset(tor.x - w * 0.022f, tor.y - w * 0.008f), Size(w * 0.044f, w * 0.016f), style = Stroke(1f * u))
        val c0 = h0 + eje * 0.3f; val c1 = h0 + eje * 0.66f
        val mitad = (c0 + c1) / 2f
        val husada = Path().apply {
            moveTo(c0.x, c0.y)
            quadraticTo(mitad.x - w * 0.024f, mitad.y + w * 0.005f, c1.x, c1.y)
            quadraticTo(mitad.x + w * 0.024f, mitad.y - w * 0.005f, c0.x, c0.y)
            close()
        }
        drawPath(corridoMongol(husada, 1.6f * u, 1.2f * u), l.s(0.3f))
        drawPath(husada, l.c(mezcla(P.CREMA, P.ORO, 0.12f)))
        clipPath(husada) {
            val vueltas = Path()
            for (q in 0 until 9) {
                val p = c0 + (c1 - c0) * (q / 8f)
                vueltas.moveTo(p.x - w * 0.02f, p.y + w * 0.006f); vueltas.lineTo(p.x + w * 0.02f, p.y - w * 0.004f)
            }
            drawPath(vueltas, l.s(0.28f), style = Stroke(0.7f * u))
            drawRect(l.s(0.25f), Offset(mitad.x, c1.y), Size(w * 0.03f, c0.y - c1.y))
        }
        drawArc(l.c(P.ALERCE_OSC), 180f, 200f, false, Offset(h1.x - w * 0.004f, h1.y - w * 0.006f), Size(w * 0.008f, w * 0.008f), style = Stroke(1f * u))
        // Los de delante, que tapan el pie del huso.
        for (q in 0 until 6) {
            val c = Offset(r.left + r.width * (0.02f + 0.18f * q + 0.05f * azar(957, q)), r.top + r.height * (0.66f + 0.2f * azar(958, q)))
            mechon(c, w * (0.017f + 0.006f * azar(959, q)), q + 7)
        }
        drawRect(l.s(0.18f), Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f))
        val ov = Offset(r.right - r.width * 0.05f, r.top + r.height * 0.32f)
        drawCircle(l.s(0.3f), w * 0.022f, ov + Offset(w * 0.006f, w * 0.004f))
        drawCircle(l.c(mezcla(P.CREMA, P.BERMELLON, 0.55f)), w * 0.022f, ov)
        clipPath(Path().apply { addOval(Rect(ov, w * 0.022f)) }) {
            for (q in 0 until 6) {
                val ang = q * 30f
                rotate(ang, ov) { drawLine(l.c(P.BERMELLON_OSC).copy(alpha = 0.5f), ov - Offset(w * 0.03f, w * 0.004f * q - w * 0.01f), ov + Offset(w * 0.03f, -w * 0.004f * q + w * 0.012f), 0.8f * u) }
            }
            drawCircle(l.b(0.3f), w * 0.012f, ov - Offset(w * 0.008f, w * 0.008f))
        }
        // La hebra suelta que baja del ovillo por la cornisa.
        drawPath(Path().apply { moveTo(ov.x + w * 0.01f, ov.y + w * 0.02f); cubicTo(ov.x + w * 0.03f, ov.y + w * 0.05f, ov.x + w * 0.01f, ov.y + w * 0.08f, ov.x + w * 0.035f, ov.y + w * 0.11f) },
            l.c(mezcla(P.CREMA, P.BERMELLON, 0.55f)), style = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
