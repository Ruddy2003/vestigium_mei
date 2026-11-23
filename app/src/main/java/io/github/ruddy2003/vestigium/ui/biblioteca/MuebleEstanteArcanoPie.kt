package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * Lo del pie del estante de Horror cósmico ([MuebleEstanteArcano]), sobre la baldosa: la caja de pescado con los legajos mojados
 * puestos a secar (cortada por la izquierda), el cubo de cinc donde cae la gotera, con su agua, su anillo y la gota que baja, y el
 * farol de tormenta de lata pintada de rojo, el que se lleva al muelle: de noche arde y es la luz de abajo.
 */
internal object PieEstanteArcano {
    private val P = PaletaArcano

    fun DrawScope.alPie(g: MarcoArcano) {
        caja(g)
        cubo(g)
        farol(g)
    }

    // ---------------------------------------------------------------- la caja de pescado

    private fun DrawScope.caja(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.asu
        val y1 = g.h - w * 0.018f
        val r = Rect(-w * 0.04f, y1 - w * 0.115f, w * 0.27f, y1)
        val k = g.cerca(r.center)
        val madera = mezcla(P.TECA_GRIS, P.TECA, 0.3f)
        // Los legajos que asoman por arriba, mojados y ondulados, uno doblado sobre el canto.
        listOf(Triple(0.0f, 0.1f, -5f), Triple(0.07f, 0.11f, 3f), Triple(0.15f, 0.1f, -2f)).forEachIndexed { i, (fx, fw, giro) ->
            val hoja = Rect(w * fx, r.top - w * 0.045f, w * (fx + fw), r.top + w * 0.02f)
            rotate(giro, Offset(hoja.center.x, hoja.bottom)) {
                drawRect(a.sombra(0.3f), Offset(hoja.left + 1.6f * u, hoja.top + 1.6f * u), hoja.size)
                drawRect(a.luz(if (i == 1) P.MANILA else P.PAPEL, k), hoja.topLeft, hoja.size)
                drawPath(irregularArcano(Offset(hoja.center.x, hoja.top + hoja.height * 0.35f), hoja.width * 0.4f, hoja.height * 0.22f, 8500 + i, 9, 0.35f), a.luz(P.SALOBRE, k).copy(alpha = 0.3f))
                drawLine(a.sombra(0.25f), Offset(hoja.left, hoja.top + hoja.height * 0.6f), Offset(hoja.right, hoja.top + hoja.height * 0.55f), 0.8f * u)
                drawRect(a.brillo(0.35f, k), hoja.topLeft, Size(hoja.width, 1f * u))
            }
        }
        contactoArcano(Offset(r.center.x, y1), r.width * 1.05f, w * 0.022f, a, 0.5f)
        // Las tablillas del frente, con su rendija, y los cantoneros de los lados.
        val tablillas = 3
        for (j in 0 until tablillas) {
            val t0 = r.top + r.height * j / tablillas; val t1 = r.top + r.height * (j + 1) / tablillas - w * 0.006f
            val t = Rect(r.left, t0, r.right, t1)
            drawRect(a.luz(madera, k), t.topLeft, t.size)
            vetas(t, a.luz(P.TECA_OSC, k).copy(alpha = 0.35f), 8510 + j, densidad = 0.6f, grosor = 0.6f, ondulacion = 0.4f)
            drawRect(a.brillo(0.35f, k), t.topLeft, Size(t.width, 1.2f * u))
            drawRect(a.sombra(0.3f), Offset(t.left, t.bottom - 1.6f * u), Size(t.width, 1.6f * u))
            drawRect(a.sombra(0.7f), Offset(t.left, t.bottom), Size(t.width, w * 0.006f))
        }
        listOf(r.left + w * 0.04f, r.right - w * 0.025f).forEach { x ->
            drawRect(a.luz(mezcla(madera, P.TECA_OSC, 0.3f), k), Offset(x, r.top), Size(w * 0.022f, r.height))
            drawRect(a.sombra(0.3f), Offset(x + w * 0.016f, r.top), Size(w * 0.006f, r.height))
            listOf(0.17f, 0.5f, 0.83f).forEach { f -> clavoArcano(Offset(x + w * 0.011f, r.top + r.height * f), 1.3f * u, a, k, P.HIERRO_LUZ) }
        }
        // La sal y el agua que dejó la caja en el suelo.
        drawPath(irregularArcano(Offset(r.center.x + w * 0.02f, y1 + w * 0.006f), r.width * 0.55f, w * 0.01f, 8520, 12, 0.3f), a.luz(P.YESO, k).copy(alpha = 0.22f), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- el cubo de cinc

    private fun DrawScope.cubo(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.asu
        val cx = w * 0.5f
        val y1 = g.h - w * 0.036f
        val alto = w * 0.125f
        val ra = w * 0.074f; val rb = w * 0.06f
        val y0 = y1 - alto
        val k = g.cerca(Offset(cx, y0 + alto / 2))
        val cuerpo = Path().apply {
            moveTo(cx - ra, y0); lineTo(cx + ra, y0); lineTo(cx + rb, y1)
            quadraticTo(cx, y1 + w * 0.012f, cx - rb, y1); close()
        }
        contactoArcano(Offset(cx, y1 + w * 0.004f), rb * 2.6f, w * 0.022f, a, 0.5f)
        drawPath(cuerpo, a.luz(P.CINC, k))
        clipPath(cuerpo) {
            cilindroArcano(Rect(cx - ra, y0, cx + ra, y1 + w * 0.012f), a, maxOf(k, if (a.oscuro) 0f else 1f), if (a.oscuro) 1f else -1f, 1.3f)
            // Las aguas del cinc (el florecido del galvanizado), en manchas claras y hondas.
            for (j in 0 until 7) {
                val p = Offset(cx - ra + 2 * ra * azar(8600, j), y0 + alto * azar(8601, j))
                drawPath(irregularArcano(p, w * (0.008f + 0.01f * azar(8602, j)), w * 0.006f, 8603 + j, 7, 0.4f), a.luz(if (j % 2 == 0) P.CINC_LUZ else P.CINC_OSC, k).copy(alpha = 0.3f))
            }
            // Los dos aros del cuerpo y una abolladura.
            listOf(0.3f, 0.72f).forEach { f ->
                val y = y0 + alto * f
                val arco = Path().apply { moveTo(cx - ra, y); quadraticTo(cx, y + w * 0.012f, cx + ra, y) }
                drawPath(arco, a.sombra(0.35f), style = Stroke(1.6f * u))
                drawPath(Path().apply { addPath(arco, Offset(0f, -1.6f * u)) }, a.luz(P.CINC_LUZ, k).copy(alpha = 0.6f), style = Stroke(0.9f * u))
            }
            drawOval(a.sombra(0.25f), Offset(cx + ra * 0.15f, y0 + alto * 0.42f), Size(ra * 0.45f, alto * 0.18f))
            drawOval(a.luz(P.CINC_LUZ, k).copy(alpha = 0.4f), Offset(cx + ra * 0.12f, y0 + alto * 0.4f), Size(ra * 0.3f, alto * 0.06f))
        }
        // La boca: el reborde y el agua, honda y quieta, con el anillo de la gota que acaba de caer.
        val boca = Rect(cx - ra, y0 - w * 0.016f, cx + ra, y0 + w * 0.016f)
        drawOval(a.luz(P.CINC_OSC, k), boca.topLeft, boca.size)
        val agua = Rect(boca.left + w * 0.006f, boca.top + w * 0.004f, boca.right - w * 0.006f, boca.bottom - w * 0.002f)
        drawOval(a.luz(mezcla(P.MAR_OSC, P.HIERRO, 0.4f), k), agua.topLeft, agua.size)
        val anillo = Rect(cx - w * 0.026f, agua.center.y - w * 0.006f, cx + w * 0.022f, agua.center.y + w * 0.008f)
        drawOval(a.luz(P.CINC_LUZ, k).copy(alpha = 0.55f), anillo.topLeft, anillo.size, style = Stroke(1f * u))
        drawOval(a.luz(P.CINC_LUZ, k).copy(alpha = 0.3f), anillo.topLeft - Offset(w * 0.012f, w * 0.004f), Size(anillo.width + w * 0.024f, anillo.height + w * 0.008f), style = Stroke(0.8f * u))
        if (a.oscuro && k > 0.15f) drawOval(P.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), Offset(cx + w * 0.01f, agua.top + agua.height * 0.2f), Size(w * 0.03f, agua.height * 0.3f))
        drawArc(a.brillo(0.55f, k), 180f, 90f, false, boca.topLeft, boca.size, style = Stroke(1.2f * u))
        // El asa de alambre, caída hacia delante, con sus orejas.
        val asa = Path().apply { moveTo(cx - ra, y0 + w * 0.004f); cubicTo(cx - ra * 0.8f, y0 + alto * 0.55f, cx + ra * 0.8f, y0 + alto * 0.55f, cx + ra, y0 + w * 0.004f) }
        translate(1.4f * u, 2f * u) { drawPath(asa, a.sombra(0.3f), style = Stroke(1.4f * u)) }
        drawPath(asa, a.luz(P.HIERRO_LUZ, k), style = Stroke(1.4f * u))
        listOf(-1f, 1f).forEach { s -> drawCircle(a.luz(P.CINC_OSC, k), w * 0.007f, Offset(cx + s * ra * 0.98f, y0 + w * 0.006f)) }
        // La gota que cae al cubo desde el borde del zócalo, debajo del cajón clavado, y la que se está formando en el borde.
        val gota = Offset(cx - w * 0.002f, (g.suelo + y0) / 2)
        drawOval((if (a.oscuro) P.HAZ.copy(alpha = 0.25f + 0.3f * k) else mezcla(P.VIDRIO_LUZ, P.SALOBRE, 0.3f).copy(alpha = 0.7f)), Offset(cx - w * 0.004f, g.suelo - w * 0.002f), Size(w * 0.008f, w * 0.009f))
        val gp = Path().apply {
            moveTo(gota.x, gota.y - w * 0.009f)
            quadraticTo(gota.x + w * 0.0045f, gota.y, gota.x, gota.y + w * 0.0035f)
            quadraticTo(gota.x - w * 0.0045f, gota.y, gota.x, gota.y - w * 0.009f)
        }
        drawPath(gp, (if (a.oscuro) P.HAZ.copy(alpha = 0.35f + 0.4f * k) else mezcla(P.VIDRIO_LUZ, P.MAR_LUZ, 0.3f).copy(alpha = 0.85f)))
        drawCircle(Color.White.copy(alpha = if (a.oscuro) 0.3f * k else 0.8f), w * 0.0012f, gota - Offset(w * 0.0012f, 0f))
    }

    // ---------------------------------------------------------------- el farol de tormenta

    /**
     * El farol de tormenta: el depósito de lata pintada de rojo (gastada hasta la lata), los dos tubos de los lados, el globo de
     * vidrio con su guarda de alambre, el capuchón y el asa. De noche arde; de día, el globo tiznado y la mecha apagada.
     */
    private fun DrawScope.farol(g: MarcoArcano) {
        val w = g.w
        val cx = g.farol.x
        val y1 = g.h - w * 0.045f
        // Se dibuja a su tamaño de siempre y se agranda desde donde pisa (la llama cae en [MarcoArcano.farol]).
        scale(1.3f, Offset(cx, y1)) { farolDentro(g, cx, y1) }
    }

    private fun DrawScope.farolDentro(g: MarcoArcano, cx: Float, y1: Float) {
        val w = g.w; val u = density
        val a = g.asu
        val k = g.cerca(Offset(cx, y1 - w * 0.05f))
        val rojo = mezcla(P.MINIO, P.LACRE, 0.4f)
        val lata = P.CINC
        contactoArcano(Offset(cx, y1 + w * 0.004f), w * 0.11f, w * 0.018f, a, 0.5f, if (a.oscuro) 1f else -1f)
        // El depósito: un tambor chato con su borde, la pintura roja gastada.
        val dep = Rect(cx - w * 0.04f, y1 - w * 0.03f, cx + w * 0.04f, y1)
        drawRect(a.luz(rojo, k), dep.topLeft, dep.size)
        cilindroArcano(dep, a, maxOf(k, if (a.oscuro) 0f else 1f), if (a.oscuro) -1f else -1f, 1.2f)
        drawOval(a.luz(rojo, k), Offset(dep.left, y1 - w * 0.006f), Size(dep.width, w * 0.012f))
        drawOval(a.luz(mezcla(rojo, Color.White, 0.15f), k), Offset(dep.left, dep.top - w * 0.007f), Size(dep.width, w * 0.014f))
        for (j in 0 until 4) {
            val p = Offset(dep.left + dep.width * azar(8700, j), dep.top + dep.height * (0.2f + 0.6f * azar(8701, j)))
            drawPath(irregularArcano(p, w * 0.007f, w * 0.004f, 8702 + j, 7, 0.4f), a.luz(lata, k))
        }
        // Los dos tubos de los lados, del depósito al capuchón.
        val arriba = y1 - w * 0.135f
        listOf(-1f, 1f).forEach { s ->
            val x = cx + s * w * 0.036f
            drawLine(a.sombra(0.35f), Offset(x + 1.4f * u, dep.top), Offset(x + 1.4f * u, arriba + w * 0.01f), w * 0.007f)
            drawLine(a.luz(rojo, k), Offset(x, dep.top), Offset(x, arriba + w * 0.01f), w * 0.007f)
            drawLine(a.brillo(0.3f, k), Offset(x - w * 0.002f, dep.top), Offset(x - w * 0.002f, arriba + w * 0.01f), 0.7f * u)
        }
        // El globo de vidrio, panzudo, con la llama o la mecha.
        val gl = Rect(cx - w * 0.028f, y1 - w * 0.1f, cx + w * 0.028f, dep.top - w * 0.002f)
        val globo = Path().apply {
            moveTo(gl.left + gl.width * 0.25f, gl.top)
            cubicTo(gl.left - gl.width * 0.08f, gl.top + gl.height * 0.3f, gl.left - gl.width * 0.08f, gl.bottom - gl.height * 0.2f, gl.left + gl.width * 0.2f, gl.bottom)
            lineTo(gl.right - gl.width * 0.2f, gl.bottom)
            cubicTo(gl.right + gl.width * 0.08f, gl.bottom - gl.height * 0.2f, gl.right + gl.width * 0.08f, gl.top + gl.height * 0.3f, gl.right - gl.width * 0.25f, gl.top)
            close()
        }
        if (a.lumbre) {
            mancha(Offset(cx, gl.bottom - w * 0.02f), w * 0.08f, P.LLAMA.copy(alpha = if (a.oscuro) 0.5f else 0.25f))
            drawPath(globo, P.LLAMA.copy(alpha = if (a.oscuro) 0.5f else 0.3f))
            val alto = w * (if (a.oscuro && !a.noche) 0.028f else 0.02f)
            val f = Path().apply {
                moveTo(cx - w * 0.007f, gl.bottom - w * 0.008f)
                cubicTo(cx - w * 0.009f, gl.bottom - w * 0.008f - alto * 0.5f, cx - w * 0.002f, gl.bottom - w * 0.008f - alto * 0.85f, cx, gl.bottom - w * 0.008f - alto)
                cubicTo(cx + w * 0.002f, gl.bottom - w * 0.008f - alto * 0.85f, cx + w * 0.009f, gl.bottom - w * 0.008f - alto * 0.5f, cx + w * 0.007f, gl.bottom - w * 0.008f)
                close()
            }
            drawPath(f, mezcla(P.LLAMA, P.BRASA, 0.15f))
            drawOval(P.FUEGO, Offset(cx - w * 0.0035f, gl.bottom - w * 0.008f - alto * 0.62f), Size(w * 0.007f, alto * 0.5f))
        } else {
            drawPath(globo, a.luz(mezcla(P.VIDRIO, Color.White, 0.3f), k).copy(alpha = 0.4f))
            clipPath(globo) { drawRect(Color(0xFF2A2622).copy(alpha = 0.35f), Offset(gl.left, gl.top), Size(gl.width, gl.height * 0.35f)) }
            drawRect(a.luz(P.TINTA, k), Offset(cx - w * 0.004f, gl.bottom - w * 0.01f), Size(w * 0.008f, w * 0.006f))
        }
        val brillo = Path().apply { moveTo(gl.left + gl.width * 0.18f, gl.top + gl.height * 0.2f); quadraticTo(gl.left + gl.width * 0.02f, gl.center.y, gl.left + gl.width * 0.2f, gl.bottom - gl.height * 0.15f) }
        drawPath(brillo, if (a.oscuro) P.FUEGO.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.75f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawPath(globo, a.luz(P.VIDRIO_OSC, k).copy(alpha = 0.6f), style = Stroke(0.9f * u))
        // La guarda de alambre: tres varillas que abrazan el globo y un aro.
        val alambre = a.luz(P.HIERRO, k)
        listOf(-0.75f, 0f, 0.75f).forEach { s ->
            val p = Path().apply { moveTo(cx + s * gl.width * 0.35f, gl.top); quadraticTo(cx + s * gl.width * 0.78f, gl.center.y, cx + s * gl.width * 0.4f, gl.bottom) }
            drawPath(p, alambre, style = Stroke(1.1f * u))
        }
        drawLine(alambre, Offset(gl.left - gl.width * 0.02f, gl.center.y), Offset(gl.right + gl.width * 0.02f, gl.center.y), 1.1f * u)
        // El capuchón con su chimenea, las rejillas, y el asa.
        val cap = poligonoArcano(cx - w * 0.04f, arriba + w * 0.016f, cx + w * 0.04f, arriba + w * 0.016f, cx + w * 0.022f, arriba, cx - w * 0.022f, arriba)
        drawPath(cap, a.luz(rojo, k))
        drawRect(a.brillo(0.35f, k), Offset(cx - w * 0.03f, arriba + w * 0.004f), Size(w * 0.025f, 1f * u))
        drawRect(a.luz(lata, k), Offset(cx - w * 0.011f, arriba - w * 0.012f), Size(w * 0.022f, w * 0.012f))
        for (j in 0 until 3) drawLine(a.sombra(0.45f), Offset(cx - w * 0.008f + j * w * 0.008f, arriba - w * 0.009f), Offset(cx - w * 0.008f + j * w * 0.008f, arriba - w * 0.003f), 0.9f * u)
        drawRect(a.luz(rojo, k), Offset(cx - w * 0.008f, gl.top - w * 0.006f), Size(w * 0.016f, w * 0.008f))
        val asa = Path().apply { moveTo(cx - w * 0.036f, arriba + w * 0.008f); cubicTo(cx - w * 0.04f, arriba - w * 0.05f, cx + w * 0.04f, arriba - w * 0.05f, cx + w * 0.036f, arriba + w * 0.008f) }
        translate(1.4f * u, 1.6f * u) { drawPath(asa, a.sombra(0.3f), style = Stroke(1.2f * u)) }
        drawPath(asa, alambre, style = Stroke(1.2f * u))
        if (a.oscuro) drawCircle(P.LLAMA.copy(alpha = 0.25f), w * 0.035f, Offset(cx, dep.top))
    }
}
