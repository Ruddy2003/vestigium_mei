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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sqrt

/**
 * Lo colgado en la pared de la corona de Templario ([MuebleEstanteTemplario]): la bocina de guardia (un cuerno de buey con sus
 * virolas de latón, colgado por la punta de una estaquilla, con el tiracol de cuero que cae en comba) y el brazo de hierro con
 * la lámpara de aceite de vidrio soplado de tres cadenas, que de noche es la luz que manda.
 */
internal object ColgadoEstanteTemplario {
    private val P = PaletaTemplario

    /** La sombra de [p] en la pared, como la de lo de encima del armario (ver [CoronaEstanteTemplario]). */
    private fun DrawScope.sombra(p: Path, desde: Offset, hasta: Float, a: AireTemplario, m: ManchaTemplario?, fuerza: Float, trazo: Float = 0f) {
        val w = size.width
        val estilo = if (trazo > 0f) Stroke(trazo, cap = StrokeCap.Round) else androidx.compose.ui.graphics.drawscope.Fill
        clipRect(w * MuroEstanteTemplario.ESQUINA, 0f, w, hasta) {
            when {
                a.oscuro -> {
                    val k = MuebleEstanteTemplario.lamparaArriba(desde.x / w - MuebleEstanteTemplario.LAMP_X, desde.y / w - MuebleEstanteTemplario.LAMP_Y, a.noche)
                    if (k > 0.1f) {
                        val dx = ((desde.x / w - MuebleEstanteTemplario.LAMP_X) / 0.2f).coerceIn(-1f, 1f)
                        val dy = ((desde.y / w - MuebleEstanteTemplario.LAMP_Y) / 0.2f).coerceIn(-1f, 1f)
                        translate(w * 0.03f * dx, w * 0.018f * dy + w * 0.006f) { drawPath(p, a.sombra((0.7f * k).coerceAtMost(0.55f)), style = estilo) }
                    }
                }
                a.noche -> translate(w * 0.045f, w * 0.008f) { drawPath(p, a.sombra(fuerza * 1.3f), style = estilo) }
                else -> {
                    val enSol = m?.toca(desde.x / w + 0.02f, desde.y / w + 0.012f) ?: 0f
                    translate(w * 0.02f, w * 0.013f) { drawPath(p, a.sombra(if (enSol > 0f) fuerza * 1.7f else fuerza), style = estilo) }
                }
            }
        }
    }

    // ---------------------------------------------------------------- la bocina

    /** La bocina de guardia, colgada por la punta de su estaquilla, con el tiracol en comba. */
    fun DrawScope.bocina(a: AireTemplario, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val est = Offset(w * 0.6f, w * 0.128f)
        val k = cerca(Offset(w * 0.58f, w * 0.24f))
        // El cuerno: de la boquilla (arriba, colgada) a la boca (abajo a la izquierda, ancha), con la panza a la derecha.
        val boq = Offset(w * 0.612f, w * 0.17f); val boca = Offset(w * 0.515f, w * 0.335f)
        val ctrl = Offset(w * 0.665f, w * 0.315f)
        fun punto(t: Float): Offset {
            val i = 1f - t
            return Offset(i * i * boca.x + 2 * i * t * ctrl.x + t * t * boq.x, i * i * boca.y + 2 * i * t * ctrl.y + t * t * boq.y)
        }
        fun grueso(t: Float) = w * (0.05f * (1f - t) * (1f - t * 0.3f) + 0.007f * t)
        val n = 18
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for (j in 0..n) {
            val t = j / n.toFloat()
            val p0 = punto(t); val p1 = punto((t + 0.01f).coerceAtMost(1f)); val pm = punto((t - 0.01f).coerceAtLeast(0f))
            val dx = p1.x - pm.x; val dy = p1.y - pm.y
            val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1e-3f)
            val nx = -dy / l; val ny = dx / l
            val g = grueso(t) / 2
            izq.add(Offset(p0.x + nx * g, p0.y + ny * g)); der.add(Offset(p0.x - nx * g, p0.y - ny * g))
        }
        val cuerno = Path().apply {
            moveTo(izq[0].x, izq[0].y); izq.forEach { lineTo(it.x, it.y) }
            for (j in der.indices.reversed()) lineTo(der[j].x, der[j].y); close()
        }
        // El tiracol: de la virola de la boca a la de la punta, en comba por debajo del cuerno.
        val v1 = punto(0.16f); val v2 = punto(0.82f)
        val tiracol = Path().apply { moveTo(v1.x, v1.y); cubicTo(v1.x + w * 0.04f, v1.y + w * 0.08f, v2.x + w * 0.09f, v2.y + w * 0.17f, v2.x + w * 0.004f, v2.y) }
        // El cordón corto de la estaquilla a la boquilla.
        val cordon = Path().apply { moveTo(est.x, est.y); quadraticTo(est.x + w * 0.008f, est.y + w * 0.02f, boq.x, boq.y) }
        sombra(cuerno, Offset(w * 0.58f, w * 0.25f), apoyo, a, m, 0.28f)
        sombra(tiracol, Offset(w * 0.6f, w * 0.3f), apoyo, a, m, 0.22f, w * 0.007f)
        drawPath(cordon, a.dia(P.CUERO_OSC, 0f, k), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // El cuerno, crema veteado, oscuro hacia la punta.
        drawPath(cuerno, a.dia(P.CUERNO, 0f, k))
        clipPath(cuerno) {
            for (j in 0..n) {
                val t = j / n.toFloat()
                drawCircle(a.dia(mezcla(P.CUERNO, P.CUERNO_OSC, (t * 1.5f - 0.45f).coerceIn(0f, 1f)), 0f, k), grueso(t) * 0.6f, punto(t))
            }
            // La sombra del lado de la panza (a la derecha) y el brillo largo del otro.
            val panza = Path().apply { moveTo(der[0].x, der[0].y); der.forEach { lineTo(it.x, it.y) } }
            drawPath(panza, a.sombra(0.38f), style = Stroke(w * 0.016f))
            val lomo = Path().apply { moveTo(izq[2].x, izq[2].y); for (j in 2 until n - 2) lineTo(izq[j].x + w * 0.005f, izq[j].y) }
            drawPath(lomo, a.brillo(0.55f, k), style = Stroke(w * 0.0035f, cap = StrokeCap.Round))
            // Las vetas del cuerno, de través.
            for (j in 1 until 10) {
                val t = j / 11f
                val p0 = punto(t); val g = grueso(t) * 0.5f
                drawLine(a.dia(P.CUERNO_OSC, 0f, k).copy(alpha = 0.3f), Offset(p0.x - g, p0.y + w * 0.002f), Offset(p0.x + g, p0.y - w * 0.003f), 0.8f * u)
            }
        }
        // La boca abierta: el hueco oscuro y el canto del cuerno.
        val bm = Offset((izq[0].x + der[0].x) / 2, (izq[0].y + der[0].y) / 2)
        val ancho = sqrt((izq[0].x - der[0].x) * (izq[0].x - der[0].x) + (izq[0].y - der[0].y) * (izq[0].y - der[0].y))
        val ang = Math.toDegrees(kotlin.math.atan2((der[0].y - izq[0].y).toDouble(), (der[0].x - izq[0].x).toDouble())).toFloat()
        rotate(ang, bm) {
            drawOval(a.dia(P.CUERNO_OSC, -0.15f, k), Offset(bm.x - ancho / 2, bm.y - w * 0.008f), Size(ancho, w * 0.016f))
            drawOval(a.dia(Color(0xFF2E2014), 0f, k), Offset(bm.x - ancho * 0.38f, bm.y - w * 0.005f), Size(ancho * 0.76f, w * 0.01f))
        }
        // Las virolas de latón, con su luz, y las anillas del tiracol.
        listOf(0.06f, 0.16f, 0.82f, 0.96f).forEach { t ->
            val p0 = punto(t); val p1 = punto((t + 0.02f).coerceAtMost(1f))
            val a0 = Math.toDegrees(kotlin.math.atan2((p1.y - p0.y).toDouble(), (p1.x - p0.x).toDouble())).toFloat()
            val g = grueso(t) * 0.56f
            rotate(a0, p0) {
                drawRect(a.dia(P.LATON, 0f, k), Offset(p0.x - w * 0.005f, p0.y - g), Size(w * 0.01f, g * 2))
                drawRect(a.brillo(0.55f, k), Offset(p0.x - w * 0.005f, p0.y - g), Size(w * 0.01f, g * 0.45f))
                drawRect(a.sombra(0.3f), Offset(p0.x - w * 0.005f, p0.y + g * 0.5f), Size(w * 0.01f, g * 0.5f))
            }
        }
        drawPath(tiracol, a.dia(P.CUERO_OSC, 0f, k), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(tiracol, a.brillo(0.22f, k), style = Stroke(w * 0.0015f))
        listOf(v1, v2).forEach { drawCircle(a.dia(P.LATON_OSC, 0f, k), w * 0.005f, it, style = Stroke(1.3f * u)) }
        luzDelAjimez(cuerno, a, m, 0f)
        // La estaquilla de madera en la cal.
        drawCircle(a.sombra(0.35f), w * 0.007f, est + Offset(1.2f * u, 1.4f * u))
        drawCircle(a.dia(P.CEDRO, 0f, k), w * 0.007f, est)
        drawCircle(a.brillo(0.4f, k), w * 0.0025f, est - Offset(w * 0.002f, w * 0.002f))
    }

    // ---------------------------------------------------------------- la lámpara

    /**
     * La lámpara de aceite de vidrio soplado, colgada de tres cadenas del brazo de hierro: la panza redonda con el aceite y la
     * mecha, el cuello que se abre en boca ancha y el pie. De día, vidrio verdoso con su brillo; de noche arde (la llama se pinta
     * aquí; el charco, en la cal).
     */
    fun DrawScope.lampara(a: AireTemplario, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val lx = w * MuebleEstanteTemplario.LAMP_X; val ly = w * MuebleEstanteTemplario.LAMP_Y
        val k = cerca(Offset(lx, ly))
        // El brazo: una pletina de hierro empotrada a la derecha, con su tornapunta en voluta y el gancho.
        val raiz = Offset(w * 0.962f, w * 0.1f)
        val gancho = Offset(lx, w * 0.108f)
        val brazo = Path().apply {
            moveTo(raiz.x, raiz.y); lineTo(gancho.x, gancho.y)
            moveTo(raiz.x, raiz.y + w * 0.045f)
            cubicTo(raiz.x - w * 0.03f, raiz.y + w * 0.03f, raiz.x - w * 0.07f, raiz.y + w * 0.012f, raiz.x - w * 0.1f, raiz.y + w * 0.002f)
        }
        val voluta = Path().apply { addOval(Rect(raiz.x - w * 0.125f, raiz.y + w * 0.002f, raiz.x - w * 0.1f, raiz.y + w * 0.027f)) }
        sombra(Path().apply { addPath(brazo) }, Offset(w * 0.88f, w * 0.12f), apoyo, a, m, 0.24f, w * 0.006f)
        drawPath(brazo, a.dia(P.HIERRO, 0f, k), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
        drawPath(voluta, a.dia(P.HIERRO, 0f, k), style = Stroke(w * 0.0035f))
        drawLine(a.brillo(0.45f, k), Offset(raiz.x, raiz.y - w * 0.002f), Offset(gancho.x, gancho.y - w * 0.002f), 0.8f * u)
        drawRect(a.dia(P.HIERRO, 0f, k), Offset(raiz.x - w * 0.007f, raiz.y - w * 0.016f), Size(w * 0.014f, w * 0.07f))
        drawRect(a.brillo(0.35f, k), Offset(raiz.x - w * 0.007f, raiz.y - w * 0.016f), Size(w * 0.003f, w * 0.07f))
        clavoTemplario(Offset(raiz.x, raiz.y - w * 0.008f), 1.5f * u, a, k, P.HIERRO_LUZ)
        clavoTemplario(Offset(raiz.x, raiz.y + w * 0.046f), 1.5f * u, a, k, P.HIERRO_LUZ)
        drawArc(a.dia(P.HIERRO, 0f, k), 0f, 200f, false, Offset(gancho.x - w * 0.006f, gancho.y - w * 0.002f), Size(w * 0.012f, w * 0.012f), style = Stroke(1.6f * u))
        // El vaso: el pie, la panza redonda, el cuello que se abre en boca ancha.
        val boca = ly - w * 0.062f
        val cintura = ly - w * 0.03f
        val panzaR = w * 0.032f
        val panzaC = ly + w * 0.006f
        val vaso = Path().apply {
            moveTo(lx - w * 0.038f, boca)
            cubicTo(lx - w * 0.03f, boca + w * 0.012f, lx - w * 0.018f, cintura - w * 0.006f, lx - w * 0.022f, cintura)
            cubicTo(lx - panzaR * 1.25f, cintura + w * 0.008f, lx - panzaR * 1.15f, panzaC + panzaR * 0.9f, lx - w * 0.008f, panzaC + panzaR)
            lineTo(lx - w * 0.006f, panzaC + panzaR + w * 0.012f); lineTo(lx + w * 0.006f, panzaC + panzaR + w * 0.012f); lineTo(lx + w * 0.008f, panzaC + panzaR)
            cubicTo(lx + panzaR * 1.15f, panzaC + panzaR * 0.9f, lx + panzaR * 1.25f, cintura + w * 0.008f, lx + w * 0.022f, cintura)
            cubicTo(lx + w * 0.018f, cintura - w * 0.006f, lx + w * 0.03f, boca + w * 0.012f, lx + w * 0.038f, boca)
            close()
        }
        // Las tres cadenas: del gancho a la anilla y de la anilla a las tres asitas de la cintura.
        val anilla = Offset(lx, boca - w * 0.06f)
        val cad = Path()
        fun cadena(p0: Offset, p1: Offset) {
            val nn = 8
            for (j in 0 until nn) {
                val q0 = Offset(p0.x + (p1.x - p0.x) * j / nn, p0.y + (p1.y - p0.y) * j / nn)
                cad.addOval(Rect(q0.x - w * 0.0025f, q0.y, q0.x + w * 0.0025f, q0.y + (p1.y - p0.y) / nn * 1.3f))
            }
        }
        val asas = listOf(Offset(lx - w * 0.025f, cintura + w * 0.002f), Offset(lx + w * 0.025f, cintura + w * 0.002f), Offset(lx + w * 0.004f, cintura + w * 0.006f))
        cadena(Offset(gancho.x, gancho.y + w * 0.008f), anilla)
        asas.forEach { cadena(anilla, it) }
        sombra(Path().apply { addPath(vaso) }, Offset(lx, ly), apoyo, a, m, 0.14f)
        sombra(Path().apply { addPath(cad) }, Offset(lx, ly - w * 0.1f), apoyo, a, m, 0.2f, 0.9f * u)
        drawPath(cad, a.dia(P.LATON_OSC, 0f, k), style = Stroke(0.9f * u))
        drawCircle(a.dia(P.LATON, 0f, k), w * 0.007f, anilla, style = Stroke(1.6f * u))
        val ardiendo = a.oscuro || a.noche
        val fuerte = if (a.oscuro && !a.noche) 1f else if (a.oscuro) 0.75f else 0.55f
        if (a.oscuro) {
            // De noche el vidrio se enciende desde dentro: la panza casi blanca, el cuello ámbar.
            drawPath(vaso, mezcla(P.LLAMA, P.BRASA, 0.25f).copy(alpha = 0.85f))
            clipPath(vaso) {
                drawCircle(Brush.radialGradient(listOf(P.FUEGO, P.LLAMA.copy(alpha = 0.9f), P.LLAMA.copy(alpha = 0.4f)), Offset(lx, panzaC - w * 0.004f), panzaR * 1.3f), panzaR * 1.3f, Offset(lx, panzaC - w * 0.004f))
                drawRect(P.BRASA.copy(alpha = 0.35f), Offset(lx - w * 0.05f, panzaC + panzaR * 0.5f), Size(w * 0.1f, w * 0.04f))
            }
            drawPath(vaso, P.FUEGO.copy(alpha = 0.7f * fuerte), style = Stroke(1f * u))
        } else {
            drawPath(vaso, a.dia(mezcla(P.VIDRIO, P.VIDRIADO_VERDE, 0.3f), 0f, k).copy(alpha = 0.8f))
            clipPath(vaso) {
                // El aceite ámbar en la panza y el agua clara debajo; el reflejo del sol.
                drawRect(a.dia(P.VIDRIADO, 0.05f, k).copy(alpha = 0.62f), Offset(lx - w * 0.05f, panzaC - panzaR * 0.4f), Size(w * 0.1f, panzaR * 0.7f))
                drawRect(a.dia(Color(0xFFB8C8B0), 0f, k).copy(alpha = 0.5f), Offset(lx - w * 0.05f, panzaC + panzaR * 0.3f), Size(w * 0.1f, w * 0.05f))
                drawRect(a.sombra(0.18f), Offset(lx + w * 0.012f, boca), Size(w * 0.04f, w * 0.1f))
                drawRect(a.claro(0.75f), Offset(lx - w * 0.024f, panzaC - panzaR * 0.6f), Size(w * 0.004f, panzaR * 1.1f))
                drawRect(a.claro(0.5f), Offset(lx - w * 0.026f, boca + w * 0.004f), Size(w * 0.003f, w * 0.022f))
            }
            drawPath(vaso, a.dia(mezcla(P.VIDRIO, P.VIDRIADO_VERDE, 0.5f), -0.1f, k), style = Stroke(1f * u))
        }
        drawOval(if (a.oscuro) P.FUEGO.copy(alpha = 0.8f) else a.dia(P.VIDRIO, 0.2f, k), Offset(lx - w * 0.038f, boca - w * 0.004f), Size(w * 0.076f, w * 0.008f), style = Stroke(1.2f * u))
        asas.forEach { drawCircle(a.dia(P.VIDRIO, -0.1f, k), w * 0.003f, it, style = Stroke(0.9f * u)) }
        // La mecha y la llama, sobre el aceite.
        val base = Offset(lx, panzaC - panzaR * 0.45f)
        if (ardiendo) {
            val f = fuerte
            drawCircle(P.LLAMA.copy(alpha = 0.28f * f), w * 0.032f * f, base - Offset(0f, w * 0.014f))
            val llama = Path().apply {
                moveTo(base.x, base.y - w * 0.036f * f)
                cubicTo(base.x + w * 0.011f, base.y - w * 0.018f * f, base.x + w * 0.009f, base.y, base.x, base.y + w * 0.002f)
                cubicTo(base.x - w * 0.009f, base.y, base.x - w * 0.011f, base.y - w * 0.016f * f, base.x, base.y - w * 0.036f * f)
                close()
            }
            drawPath(llama, Color(0xFFFFA248))
            drawOval(P.FUEGO, Offset(base.x - w * 0.004f, base.y - w * 0.018f * f), Size(w * 0.008f, w * 0.016f * f))
        } else drawLine(Color(0xFF2A2018), base, Offset(base.x + w * 0.002f, base.y - w * 0.01f), 1f * u, StrokeCap.Round)
    }
}
