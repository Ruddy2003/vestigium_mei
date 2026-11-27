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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.floor

/**
 * El pie del estante: el zócalo metido hacia dentro, con la rejilla del archivero y las patas niveladoras; el linóleo en losetas de
 * dos verdes grises con sus juntas en fuga y sus rozaduras; las cajas de archivo apiladas a la izquierda (cortadas por el borde),
 * la estufa eléctrica de dos resistencias en medio (de noche, al rojo) y, a la derecha, el saco de yute con los papeles rotos para
 * la incineradora.
 */
internal object PieEstanteDistopia {
    fun DrawScope.pie(g: MarcoDistopia) {
        val w = g.w; val h = g.h; val u = density
        val y0 = g.fondo
        if (h - y0 < 4f) return
        val piso = y0 + 0.075f * w
        // Debajo de la última balda, la fila de la junta, de píxel entero.
        drawRect(g.luz(w * 0.5f, y0, 0.03f).c(P.ACERO_OSC), Offset(0f, floor(y0)), Size(w, 2f))
        // Debajo de todo el pie, el suelo entero de píxel entero: entre el zócalo y las losetas, y entre una fila de losetas y la
        // siguiente (que caen a medio píxel), no asoma el fondo.
        drawRect(g.luz(w * 0.5f, (piso + h) / 2, 0.01f).c(P.LINO_OSC), Offset(0f, floor(y0) + 1f), Size(w, h - floor(y0) - 1f))
        zocalo(g, y0, piso)
        suelo(g, piso)
        // De noche, el resplandor de la estufa en el suelo y en el zócalo.
        if (g.a.estufa > 0f) {
            mancha(Offset(g.estufa.x, h - 0.02f * w), w * 0.34f, P.RESISTENCIA.copy(alpha = 0.3f * g.a.estufa))
            mancha(Offset(g.estufa.x, piso), w * 0.22f, P.RESISTENCIA.copy(alpha = 0.18f * g.a.estufa))
        }
        cajas(g)
        saco(g)
        estufa(g)
        pinceladas(Rect(0f, piso, w, h), if (g.a.oscuro) g.a.hondo else P.LINO_OSC, 8501, 10, u, 0.1f)
    }

    // ---------------------------------------------------------------- el zócalo

    private fun DrawScope.zocalo(g: MarcoDistopia, y0: Float, piso: Float) {
        val w = g.w; val u = density
        val xs = List(9) { w * it / 8f }
        // Las patas: los costados que bajan hasta el suelo.
        for ((a, b) in listOf(0f to g.x0, g.x1 to w)) {
            val l = g.luz((a + b) / 2, (y0 + piso) / 2, 0.04f)
            drawRect(l.c(P.ACERO), Offset(a, y0), Size(b - a, piso - y0))
            val canto = if (a < 1f) b - (b - a) * 0.2f else a
            drawRect(l.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.45f)), Offset(canto, y0), Size((b - a) * 0.2f, piso - y0))
            drawRect(l.b(0.6f), Offset(canto + (b - a) * 0.03f, y0), Size(1.2f * u, piso - y0))
            drawRect(l.s(0.4f), Offset(if (a < 1f) a else b - (b - a) * 0.12f, y0), Size((b - a) * 0.12f, piso - y0))
            // La pata niveladora: el tornillo y su disco en el suelo.
            val cx = (a + b) / 2
            drawRect(l.c(P.HIERRO), Offset(cx - 0.006f * w, piso - 0.004f * w), Size(0.012f * w, 0.01f * w))
            drawOval(l.c(P.ACERO_OSC), Offset(cx - 0.018f * w, piso + 0.003f * w), Size(0.036f * w, 0.009f * w))
            drawOval(l.b(0.4f), Offset(cx - 0.014f * w, piso + 0.003f * w), Size(0.02f * w, 0.003f * w))
            saltadoDistopia(Offset(cx, piso - 0.012f * w), 0.008f * w, 8510 + a.toInt(), l.c(P.HIERRO), l.b(0.45f), l.c(P.OXIDO))
        }
        // El tablero del zócalo, metido hacia dentro: en sombra arriba (lo tapa la balda), con sus dos rejillas de ranuras.
        val z = Rect(g.x0, y0, g.x1, piso)
        drawRect(Brush.horizontalGradient(xs.map { g.luz(it, (y0 + piso) / 2, 0.02f).c(P.ACERO_OSC) }, z.left, z.right), z.topLeft, z.size)
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(0.55f), g.a.sombra(0.1f)), y0, y0 + (piso - y0) * 0.45f), z.topLeft, Size(z.width, (piso - y0) * 0.45f))
        drawRect(g.luz(w * 0.5f, piso, 0.02f).b(0.3f), Offset(z.left, piso - 1.2f * u), Size(z.width, 1.2f * u))
        val ranuras = Path(); val bordes = Path()
        for ((fa, fb) in listOf(0.14f to 0.36f, 0.64f to 0.86f)) {
            var x = w * fa
            var j = 0
            while (x < w * fb && j < 60) {
                val r = Rect(x, y0 + (piso - y0) * 0.36f, x + 0.006f * w, piso - (piso - y0) * 0.22f)
                ranuras.addRoundRect(RoundRect(r, CornerRadius(0.003f * w)))
                bordes.addRect(Rect(r.left, r.bottom, r.right, r.bottom + 0.8f * u))
                x += 0.016f * w; j++
            }
        }
        drawPath(ranuras, mezcla(g.luz(w * 0.5f, piso, 0.02f).c(P.BAQUELITA), g.a.hondo, if (g.a.oscuro) 0.3f else 0f))
        drawPath(bordes, g.luz(w * 0.5f, piso, 0.02f).b(0.3f))
        // La sombra de la estantería en el suelo, pegada al zócalo.
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(if (g.a.oscuro) 0.6f else 0.38f), g.a.sombra(0f)), piso, piso + 0.03f * w), Offset(0f, piso), Size(w, 0.03f * w))
    }

    // ---------------------------------------------------------------- el linóleo

    /** Las losetas en fuga, de dos verdes grises, cada una con la luz de su sitio; las juntas, en un solo camino; las rozaduras. */
    private fun DrawScope.suelo(g: MarcoDistopia, piso: Float) {
        val w = g.w; val h = g.h; val u = density
        val fondo = h - piso
        if (fondo < 2f) return
        val fx = w * 0.5f
        val filas = listOf(0f, 0.2f, 0.48f, 0.86f, 1.3f)
        val t0 = 0.62f
        fun xEn(xb: Float, f: Float) = fx + (xb - fx) * (t0 + (1f - t0) * f)
        val juntas = Path()
        for (fi in 0 until filas.size - 1) {
            val fa = filas[fi]; val fb = filas[fi + 1]
            val ya = piso + fondo * fa; val yb = minOf(piso + fondo * fb, h + 2f)
            for (c in -4..4) {
                val xa0 = w * (0.5f + c * 0.2f) - w * 0.1f; val xa1 = xa0 + w * 0.2f
                val q = Path().apply {
                    moveTo(xEn(xa0, fa), ya); lineTo(xEn(xa1, fa), ya); lineTo(xEn(xa1, fb), yb); lineTo(xEn(xa0, fb), yb); close()
                }
                val cx = (xEn(xa0, (fa + fb) / 2) + xEn(xa1, (fa + fb) / 2)) / 2
                if (cx < -w * 0.15f || cx > w * 1.15f) continue
                val col = if (Math.floorMod(c + fi, 2) == 0) P.LINO else P.LINO_OSC
                drawPath(q, g.luz(cx, (ya + yb) / 2, 0.01f).c(col))
            }
            juntas.moveTo(0f, ya); juntas.lineTo(w, ya)
        }
        for (c in -4..5) {
            val xb = w * (0.5f + c * 0.2f) - w * 0.1f
            juntas.moveTo(xEn(xb, 0f), piso); juntas.lineTo(xEn(xb, 1.3f), piso + fondo * 1.3f)
        }
        clipRect(0f, piso, w, h) {
            drawPath(juntas, g.a.sombra(0.3f), style = Stroke(0.9f * u))
            // La cera vieja y las rozaduras: rayas claras sueltas y una mancha de agua.
            val roces = Path()
            for (j in 0 until 7) {
                val x = w * azar(8520, j); val y = piso + fondo * (0.25f + 0.65f * azar(8521, j))
                roces.moveTo(x, y); roces.lineTo(x + w * (0.03f + 0.05f * azar(8522, j)), y + 1f * u)
            }
            drawPath(roces, g.luz(w * 0.3f, piso + fondo * 0.5f, 0f).b(0.22f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            drawOval(g.luz(w * 0.62f, h - fondo * 0.3f, 0f).c(P.OXIDO).copy(alpha = 0.12f), Offset(w * 0.56f, h - fondo * 0.42f), Size(w * 0.14f, fondo * 0.18f))
        }
    }

    // ---------------------------------------------------------------- las cajas de archivo

    /**
     * Dos cajas de archivo de cartón tumbadas una sobre otra, cortadas por el borde de la izquierda: la de arriba con la tapa corrida
     * y un legajo atado con balduque encima; sus agujeros para la mano y sus etiquetas.
     */
    private fun DrawScope.cajas(g: MarcoDistopia) {
        val w = g.w; val h = g.h; val u = density
        val pie = h - 0.022f * w
        val c1 = Rect(-0.06f * w, pie - 0.088f * w, 0.215f * w, pie)
        val c2 = Rect(-0.045f * w, c1.top - 0.08f * w, 0.19f * w, c1.top)
        val l1 = g.luz(c1.center.x, c1.center.y, 0.08f)
        val hacia = g.hacia(c1.center.x, c1.center.y)
        // La sombra en el suelo.
        drawOval(l1.s(0.55f), Offset(c1.left, pie - 0.01f * w), Size(c1.width + 0.04f * w, 0.024f * w))
        fun caja(r: Rect, l: LuzDistopia, semilla: Int) {
            val f = Path().apply { addRect(r) }
            planosDistopia(f, l.c(mezcla(P.CARTON, P.OXIDO, 0.1f + 0.08f * (semilla % 2))), l, hacia, 0.12f, 0.2f)
            drawLine(l.b(0.5f), Offset(r.left, r.top + 0.8f * u), Offset(r.right, r.top + 0.8f * u), 1f * u)
            drawRect(l.s(0.35f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            // El agujero para la mano, en la punta de la derecha, y la etiqueta.
            drawRoundRect(l.c(Color(0xFF241C14)), Offset(r.right - r.width * 0.2f, r.top + r.height * 0.3f), Size(r.width * 0.13f, r.height * 0.18f), CornerRadius(r.height * 0.09f))
            val et = Rect(r.left + r.width * 0.42f, r.top + r.height * 0.24f, r.left + r.width * 0.68f, r.top + r.height * 0.74f)
            drawRect(l.s(0.25f), et.topLeft + Offset(1f * u, 1.2f * u), et.size)
            drawRect(l.c(P.HUESO), et.topLeft, et.size)
            drawRect(l.c(P.CARTEL), et.topLeft, Size(et.width, et.height * 0.15f))
            drawPath(renglonesDistopia(Rect(et.left + et.width * 0.1f, et.top + et.height * 0.3f, et.right - et.width * 0.1f, et.bottom - et.height * 0.1f), 0.0024f * w, 0.0078f * w, semilla),
                l.c(P.BAQUELITA).copy(alpha = 0.5f))
            // El cartón gastado en la esquina y una mancha de humedad.
            drawOval(l.c(P.OXIDO).copy(alpha = 0.2f), Offset(r.left + r.width * 0.1f, r.bottom - r.height * 0.4f), Size(r.width * 0.22f, r.height * 0.3f))
            if (l.k > 0.06f) drawPath(bordeDistopia(f, -hacia * 1.4f * u, 0f), l.filo(0.7f))
        }
        caja(c1, l1, 8531)
        val l2 = g.luz(c2.center.x, c2.center.y, 0.08f)
        caja(c2, l2, 8532)
        // La tapa de la de arriba, corrida y un poco girada.
        val tapa = Rect(c2.left + 0.012f * w, c2.top - 0.02f * w, c2.right + 0.016f * w, c2.top + 0.004f * w)
        rotate(-3f, Offset(tapa.right, tapa.bottom)) {
            drawRect(l2.s(0.4f), tapa.topLeft + Offset(2f * u, 2.6f * u), tapa.size)
            drawRect(l2.c(mezcla(P.CARTON, P.HUESO, 0.18f)), tapa.topLeft, tapa.size)
            drawLine(l2.b(0.55f), Offset(tapa.left, tapa.top + 0.8f * u), Offset(tapa.right, tapa.top + 0.8f * u), 1f * u)
            drawRect(l2.s(0.3f), Offset(tapa.right - tapa.width * 0.08f, tapa.top), Size(tapa.width * 0.08f, tapa.height))
        }
        // El legajo encima, tumbado, atado con balduque.
        val lg = Rect(c2.left + 0.03f * w, tapa.top - 0.03f * w, c2.left + 0.17f * w, tapa.top + 0.002f * w)
        val ll = g.luz(lg.center.x, lg.center.y, 0.08f)
        drawRect(ll.s(0.4f), lg.topLeft + Offset(2f * u, 2.4f * u), lg.size)
        // La camisa de abajo (cartón), el fajo de papeles a la vista entre las dos tapas, algo desigual, y la tapa de arriba (manila).
        drawRect(ll.c(P.CARTON), Offset(lg.left, lg.bottom - lg.height * 0.22f), Size(lg.width, lg.height * 0.22f))
        drawRect(ll.c(P.HUESO), Offset(lg.left + 1.6f * u, lg.top + lg.height * 0.2f), Size(lg.width + 1.4f * u, lg.height * 0.6f))
        drawLine(ll.s(0.2f), Offset(lg.left + 2f * u, lg.top + lg.height * 0.5f), Offset(lg.right, lg.top + lg.height * 0.52f), 0.6f * u)
        drawRect(ll.c(P.CARPETA), lg.topLeft, Size(lg.width, lg.height * 0.22f))
        drawRect(ll.c(P.CARTEL), Offset(lg.left + lg.width * 0.62f, lg.top), Size(0.008f * w, lg.height))
        drawLine(ll.b(0.5f), Offset(lg.left, lg.top + 0.8f * u), Offset(lg.right, lg.top + 0.8f * u), 0.9f * u)
    }

    // ---------------------------------------------------------------- el saco de papeles para la incineradora

    /**
     * El saco de yute lleno de papeles rotos, cortado por el borde de la derecha, aflojado y con la boca abierta: las tiras y las
     * bolas de papel que rebosan, la trama del yute y la costura; una etiqueta de cartón con la franja roja, atada a la boca.
     */
    private fun DrawScope.saco(g: MarcoDistopia) {
        val w = g.w; val h = g.h; val u = density
        val pie = h - 0.018f * w
        val l = g.luz(0.86f * w, pie - 0.1f * w, 0.08f)
        val hacia = g.hacia(0.86f * w, pie - 0.1f * w)
        val forma = cerradaDistopia(listOf(
            Offset(0.745f * w, pie - 0.02f * w), Offset(0.735f * w, pie - 0.1f * w), Offset(0.765f * w, pie - 0.19f * w), Offset(0.81f * w, pie - 0.225f * w),
            Offset(0.9f * w, pie - 0.235f * w), Offset(0.99f * w, pie - 0.22f * w), Offset(1.08f * w, pie - 0.2f * w), Offset(1.1f * w, pie), Offset(0.9f * w, pie + 0.004f * w)))
        drawOval(l.s(0.55f), Offset(0.73f * w, pie - 0.012f * w), Size(0.4f * w, 0.026f * w))
        // Lo que rebosa por la boca: tiras de papel y bolas arrugadas (detrás del borde del saco).
        val boca = Offset(0.9f * w, pie - 0.225f * w)
        val tiras = Path()
        for (j in 0 until 12) {
            val x = boca.x + (azar(8541, j) - 0.5f) * 0.16f * w
            val alto = (0.02f + 0.04f * azar(8542, j)) * w
            tiras.moveTo(x, boca.y + 0.01f * w)
            tiras.cubicTo(x - 0.008f * w, boca.y - alto * 0.4f, x + 0.012f * w, boca.y - alto * 0.7f, x + (azar(8543, j) - 0.5f) * 0.02f * w, boca.y - alto)
        }
        drawPath(tiras, l.c(P.HUESO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(tiras, l.s(0.2f), style = Stroke(0.6f * u))
        listOf(Triple(0.86f, -0.012f, 0.024f), Triple(0.94f, -0.018f, 0.02f), Triple(0.9f, -0.03f, 0.018f)).forEachIndexed { i, (fx, dy, r) ->
            val c = Offset(fx * w, boca.y + dy * w)
            val bola = escamaDistopia(c, r * w, r * w * 0.8f, 8550 + i, 11)
            drawPath(corridoDistopia(bola, 1.6f * u, 2f * u), l.s(0.3f))
            drawPath(bola, l.c(if (i == 1) mezcla(P.HUESO, P.CARPETA, 0.4f) else P.HUESO))
            clipPath(bola) {
                drawRect(l.s(0.18f), Offset(c.x, c.y - r * w), Size(r * w, r * w * 2))
                for (j in 0 until 3) drawLine(l.s(0.25f), Offset(c.x - r * w * (0.6f - 0.4f * j), c.y - r * w * 0.6f), Offset(c.x - r * w * (0.3f - 0.4f * j), c.y + r * w * 0.6f), 0.7f * u)
            }
        }
        // El saco: el yute con su volumen, la trama y la costura.
        planosDistopia(forma, l.c(P.YUTE), l, hacia, 0.18f, 0.3f)
        clipPath(forma) {
            val trama = Path()
            var y = pie - 0.24f * w; var j = 0
            while (y < pie && j < 40) { trama.moveTo(0.72f * w, y); trama.lineTo(1.1f * w, y + 0.01f * w); y += 0.011f * w; j++ }
            var x = 0.74f * w; j = 0
            while (x < 1.1f * w && j < 40) { trama.moveTo(x, pie - 0.24f * w); trama.lineTo(x - 0.006f * w, pie); x += 0.013f * w; j++ }
            drawPath(trama, l.s(0.14f), style = Stroke(0.7f * u))
            // Los pliegues del saco aflojado.
            val pl = Path().apply {
                moveTo(0.79f * w, pie - 0.2f * w); quadraticTo(0.8f * w, pie - 0.11f * w, 0.78f * w, pie - 0.02f * w)
                moveTo(0.86f * w, pie - 0.21f * w); quadraticTo(0.84f * w, pie - 0.14f * w, 0.88f * w, pie - 0.06f * w)
            }
            drawPath(pl, l.s(0.35f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(pl, l.b(0.25f), style = Stroke(0.8f * u))
            // La costura de arriba (el dobladillo de la boca).
            drawLine(l.s(0.35f), Offset(0.78f * w, pie - 0.205f * w), Offset(1.08f * w, pie - 0.18f * w), 1.4f * u)
        }
        if (l.k > 0.06f) drawPath(bordeDistopia(forma, -hacia * 1.6f * u, 0f), l.filo(0.7f))
        // La etiqueta de cartón con la franja roja, atada a la boca con un cordel (sin letras).
        val nudo = Offset(0.8f * w, pie - 0.2f * w)
        val et = Rect(0.765f * w, pie - 0.15f * w, 0.815f * w, pie - 0.1f * w)
        drawLine(l.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), nudo, Offset(et.center.x, et.top + 2f * u), 1f * u)
        rotate(-8f, Offset(et.center.x, et.top)) {
            drawRect(l.s(0.35f), et.topLeft + Offset(1.4f * u, 1.6f * u), et.size)
            drawRect(l.c(P.MANILA), et.topLeft, et.size)
            drawRect(l.c(P.CARTEL), Offset(et.left, et.top + et.height * 0.3f), Size(et.width, 0.8f * u))
            drawPath(renglonesDistopia(Rect(et.left + et.width * 0.12f, et.top + et.height * 0.46f, et.right - et.width * 0.12f, et.bottom - et.height * 0.12f), 0.0022f * w, 0.007f * w, 8555),
                l.c(P.BAQUELITA).copy(alpha = 0.45f))
            drawCircle(l.c(P.CARTON), 1.4f * u, Offset(et.center.x, et.top + 2.4f * u))
        }
    }

    // ---------------------------------------------------------------- la estufa eléctrica

    /**
     * La estufa eléctrica de dos resistencias, de pie: el copete de esmalte crema con sus dos costados, el reflector cromado y curvo
     * a la vista, las dos barras de cerámica con su espiral, unos pocos alambres de protección, las patas de tubo abiertas y el
     * piloto. De día, apagada (las barras grises, el cromo frío); de noche, al rojo, y lo más claro del pie.
     */
    private fun DrawScope.estufa(g: MarcoDistopia) {
        val w = g.w; val h = g.h; val u = density
        val arde = g.a.estufa
        val cx = g.estufa.x
        val pie = h - 0.026f * w
        val an = 0.27f * w; val al = 0.105f * w
        val patas = 0.035f * w
        val r = Rect(cx - an / 2, pie - patas - al, cx + an / 2, pie - patas)
        val l = g.luz(cx - an * 0.3f, r.center.y, 0.09f)
        val hacia = -1f
        drawOval(l.s(if (arde > 0f) 0.3f else 0.55f), Offset(r.left - 0.01f * w, pie - 0.008f * w), Size(an + 0.05f * w, 0.02f * w))
        // El cordón, que se va por el suelo hacia la izquierda.
        val cordon = abiertaDistopia(listOf(Offset(r.left + an * 0.04f, r.bottom), Offset(r.left - 0.02f * w, pie - 0.004f * w), Offset(r.left - 0.07f * w, pie + 0.002f * w), Offset(r.left - 0.1f * w, h + 0.01f * w)))
        drawPath(corridoDistopia(cordon, 1.2f * u, 1.6f * u), l.s(0.4f), style = Stroke(1.6f * u))
        drawPath(cordon, l.c(mezcla(P.BURDEOS, P.BAQUELITA, 0.4f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // Las patas de tubo cromado, abiertas, con sus tacos de goma.
        for (s in listOf(-1f, 1f)) {
            val a = Offset(cx + s * an * 0.36f, r.bottom - 2f * u)
            val b = Offset(cx + s * an * 0.47f, pie)
            drawLine(l.s(0.35f), a + Offset(1.4f * u, 1.4f * u), b + Offset(1.4f * u, 0f), 2.4f * u)
            drawLine(l.c(P.ACERO_LUZ), a, b, 2.4f * u, StrokeCap.Round)
            drawLine(l.b(0.6f), a - Offset(0.6f * u, 0f), b - Offset(0.6f * u, 0f), 0.7f * u)
            drawRoundRect(l.c(P.BAQUELITA), Offset(b.x - 3.4f * u, pie - 1.6f * u), Size(6.8f * u, 3f * u), CornerRadius(1.4f * u))
        }
        // El reflector cromado: una media caña acostada, con sus bandas de brillo (de noche, encendido de naranja).
        val boca = Rect(r.left + an * 0.07f, r.top + al * 0.16f, r.right - an * 0.07f, r.bottom - al * 0.04f)
        val reflector = Path().apply { addRoundRect(RoundRect(boca, CornerRadius(boca.height * 0.16f))) }
        val cromo = if (arde > 0f) listOf(mezcla(P.BRASA, P.BAQUELITA, 0.55f), mezcla(P.RESISTENCIA, P.FLEXO, 0.25f), Color(0xFFFFE2A8), mezcla(P.RESISTENCIA, P.FLEXO, 0.2f), mezcla(P.BRASA, P.BAQUELITA, 0.5f))
            else listOf(l.c(P.ACERO_OSC), l.c(P.ACERO_LUZ), mezcla(l.c(P.ACERO_LUZ), Color.White, 0.5f), l.c(P.ACERO), l.c(P.HIERRO))
        drawPath(reflector, Brush.verticalGradient(cromo, boca.top, boca.bottom))
        if (arde <= 0f) {
            drawRect(l.b(0.8f), Offset(boca.left + 3f * u, boca.top + boca.height * 0.44f), Size(boca.width - 6f * u, 1.4f * u))
            drawRect(l.s(0.35f), Offset(boca.left, boca.top), Size(boca.width, boca.height * 0.1f))
        } else mancha(boca.center, boca.width * 0.6f, P.RESISTENCIA.copy(alpha = 0.5f * arde))
        // Las dos barras de cerámica con su espiral; al rojo de noche.
        listOf(boca.top + boca.height * 0.3f, boca.top + boca.height * 0.7f).forEach { yb ->
            val b0 = Rect(boca.left + boca.width * 0.03f, yb - 0.008f * w, boca.right - boca.width * 0.03f, yb + 0.008f * w)
            if (arde > 0f) {
                drawRoundRect(mezcla(P.RESISTENCIA, P.FLEXO, 0.45f * arde), b0.topLeft, b0.size, CornerRadius(b0.height / 2))
                drawRoundRect(Color(0xFFFFF2CC).copy(alpha = 0.95f * arde), Offset(b0.left + 2f * u, yb - 0.002f * w), Size(b0.width - 4f * u, 0.004f * w), CornerRadius(0.002f * w))
            } else {
                drawRoundRect(l.c(mezcla(P.HUESO, P.ACERO, 0.4f)), b0.topLeft, b0.size, CornerRadius(b0.height / 2))
                drawRect(l.s(0.3f), Offset(b0.left, b0.center.y), Size(b0.width, b0.height * 0.5f))
                val espiral = Path()
                var x = b0.left + 1.5f * u; var j = 0
                while (x < b0.right - 1.5f * u && j < 90) { espiral.moveTo(x, b0.top + 0.5f * u); espiral.lineTo(x + 1.3f * u, b0.bottom - 0.5f * u); x += 2.3f * u; j++ }
                drawPath(espiral, l.c(P.HIERRO).copy(alpha = 0.6f), style = Stroke(0.7f * u))
                drawLine(l.b(0.6f), Offset(b0.left + 2f * u, b0.top + 1.2f * u), Offset(b0.right - 2f * u, b0.top + 1.2f * u), 0.8f * u)
            }
        }
        // Unos pocos alambres de protección, de arriba abajo y separados.
        val rejilla = Path()
        for (j in 1..6) { val x = boca.left + boca.width * j / 7f; rejilla.moveTo(x, boca.top + 1f * u); rejilla.lineTo(x, boca.bottom - 1f * u) }
        drawPath(rejilla, if (arde > 0f) mezcla(P.BAQUELITA, P.BRASA, 0.3f) else l.c(P.HIERRO), style = Stroke(0.9f * u))
        // El copete de esmalte crema y los dos costados, que abrazan el reflector.
        val copete = Path().apply {
            moveTo(r.left, r.top + al * 0.34f)
            cubicTo(r.left, r.top + al * 0.02f, r.left + an * 0.1f, r.top - al * 0.12f, r.center.x, r.top - al * 0.12f)
            cubicTo(r.right - an * 0.1f, r.top - al * 0.12f, r.right, r.top + al * 0.02f, r.right, r.top + al * 0.34f)
            lineTo(r.right - an * 0.07f, r.top + al * 0.34f)
            lineTo(r.right - an * 0.07f, boca.top)
            lineTo(r.left + an * 0.07f, boca.top)
            lineTo(r.left + an * 0.07f, r.top + al * 0.34f)
            close()
        }
        val costados = Path().apply {
            addRoundRect(RoundRect(Rect(r.left, r.top + al * 0.2f, r.left + an * 0.075f, r.bottom), CornerRadius(2f * u)))
            addRoundRect(RoundRect(Rect(r.right - an * 0.075f, r.top + al * 0.2f, r.right, r.bottom), CornerRadius(2f * u)))
        }
        val crema = l.c(mezcla(P.HUESO, P.CARPETA, 0.35f))
        drawPath(corridoDistopia(copete, 0.008f * w, 0.006f * w), l.s(0.3f))
        planosDistopia(copete, crema, l, hacia, 0.16f, 0.22f)
        drawPath(bordeDistopia(copete, 0f, 1.8f * u), l.b(0.6f))
        planosDistopia(costados, mezcla(crema, P.CARPETA, 0.2f), l, hacia, 0.3f, 0.3f)
        // La lama del copete, por donde sale el aire, y lo saltado del esmalte.
        drawLine(l.s(0.4f), Offset(r.center.x - an * 0.22f, r.top + al * 0.06f), Offset(r.center.x + an * 0.22f, r.top + al * 0.06f), 1.4f * u)
        drawLine(l.b(0.4f), Offset(r.center.x - an * 0.22f, r.top + al * 0.06f + 1.4f * u), Offset(r.center.x + an * 0.22f, r.top + al * 0.06f + 1.4f * u), 0.7f * u)
        saltadoDistopia(Offset(r.left + an * 0.2f, r.top - al * 0.04f), 0.006f * w, 8560, l.c(P.HIERRO), l.b(0.5f))
        // El piloto rojo en el costado derecho y el interruptor de palanca en el izquierdo.
        val pil = Offset(r.right - an * 0.0375f, r.top + al * 0.42f)
        drawCircle(l.c(P.ACERO_LUZ), 0.008f * w, pil)
        drawCircle(if (arde > 0f) P.PILOTO else l.c(P.BURDEOS), 0.005f * w, pil)
        if (arde > 0f) mancha(pil, 0.03f * w, P.PILOTO.copy(alpha = 0.5f))
        drawCircle(Color.White.copy(alpha = if (arde > 0f) 0.8f else 0.4f), 0.0016f * w, pil - Offset(0.0016f * w, 0.0016f * w))
        drawRoundRect(l.c(P.BAQUELITA), Offset(r.left + an * 0.022f, r.top + al * 0.38f), Size(an * 0.03f, al * 0.16f), CornerRadius(1.2f * u))
        // De noche, el filo cálido del copete.
        if (arde > 0f) drawPath(bordeDistopia(copete, 0f, -2f * u), P.RESISTENCIA.copy(alpha = 0.4f * arde))
    }
}
