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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.pow

/**
 * El pie del estante de Orden: el zócalo del mueble (el junquillo de latón, los paneles hundidos remachados y la banda de franjas
 * de peligro gastadas del zócalo del Ideario) y el suelo de chapa estriada del Ideario, con lo que hay en él: el farol de ronda
 * de acero con su jaula y su vela (de noche, la tercera luz), la aceitera de latón del Ideario con su cerco de aceite y, a la
 * derecha, el bidón carmesí abollado con un trapo, cortado por el borde.
 */
internal object PieEstanteOrden {
    private val P = PaletaOrden
    private val M = MuebleEstanteOrden

    /** El color de [c] en [p] con la luz del momento. */
    private fun en(c: Color, f: Float, p: Offset, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float): Color =
        if (a.oscuro) { val b = a.luz(c, cerca(p), frio(p)); if (f < 0f) mezcla(b, P.NOCHE, -f * 0.45f) else b } else a.dia(c, f, cerca(p))

    /** Una franja horizontal en tramos lisos con la luz cambiando a lo ancho. */
    private fun DrawScope.franjaH(r: Rect, c: Color, f: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        if (r.width <= 0f || r.height <= 0f) return
        val n = if (a.lumbre) 12 else 3
        val paso = r.width / n
        for (i in 0 until n) {
            val x0 = r.left + paso * i
            drawRect(en(c, f, Offset(x0 + paso / 2, r.center.y), a, cerca, frio), Offset(x0, r.top), Size(paso + (if (i < n - 1) 1f else 0f), r.height))
        }
    }

    /**
     * Lo de abajo en [r] (desde el píxel entero de encima de la base): el zócalo de [arriba] a [zocalo], el suelo hasta el final y
     * lo que pisa el suelo.
     */
    fun DrawScope.alPie(r: Rect, arriba: Float, zocalo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val w = size.width; val h = r.bottom
        // La junta con la última tabla: una franja opaca del color del zócalo desde el píxel entero de encima.
        drawRect(en(P.PAVON, 0f, Offset(w / 2, arriba), a, cerca, frio), Offset(0f, r.top), Size(w, arriba - r.top + 2f))
        suelo(Rect(0f, zocalo, w, h), a, cerca, frio)
        zocalo(Rect(0f, arriba, w, zocalo), a, cerca, frio)
        // La sombra del zócalo en el suelo, corrida a la derecha y abajo de día; de noche, honda.
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.5f), a.sombra(0f)), zocalo, zocalo + w * 0.05f), Offset(0f, zocalo), Size(w, w * 0.05f))
        bidon(w, h, a, cerca, frio)
        aceitera(Offset(w * 0.285f, h - w * 0.032f), w, a, cerca, frio)
        farol(w, h, a, cerca, frio)
    }

    /** El zócalo: el junquillo de latón, los paneles hundidos con sus remaches y la banda de franjas de peligro gastadas. */
    private fun DrawScope.zocalo(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val h = r.height
        val z = Rect(w * 0.004f, r.top, w * 0.996f, r.bottom)
        franjaH(z, P.PAVON, 0f, a, cerca, frio)
        // El junquillo de latón y la moldura de arriba.
        franjaH(Rect(z.left, z.top, z.right, z.top + w * 0.008f), P.LATON, 0f, a, cerca, frio)
        drawRect(a.brillo(0.5f, cerca(Offset(w * 0.2f, z.top))), Offset(z.left, z.top + 0.6f * u), Size(z.width, 1f * u))
        drawRect(a.sombra(0.45f), Offset(z.left, z.top + w * 0.008f), Size(z.width, 1.6f * u))
        // Los paneles hundidos, con su canto de luz abajo y a la derecha (están hundidos) y un remache en cada esquina.
        val pa = Rect(z.left + w * 0.02f, z.top + h * 0.17f, z.right - w * 0.02f, z.top + h * 0.6f)
        val n = 6
        val paso = pa.width / n
        for (i in 0 until n) {
            val pr = Rect(pa.left + paso * i + w * 0.008f, pa.top, pa.left + paso * (i + 1) - w * 0.008f, pa.bottom)
            val c = pr.center
            drawRect(en(P.PAVON_HONDO, -0.05f, c, a, cerca, frio), pr.topLeft, pr.size)
            drawRect(a.sombra(0.45f), pr.topLeft, Size(pr.width, 1.6f * u))
            drawRect(a.sombra(0.35f), pr.topLeft, Size(1.6f * u, pr.height))
            drawRect(a.destello(0.3f, cerca(c), frio(c)), Offset(pr.left, pr.bottom - 1f * u), Size(pr.width, 1f * u))
            drawRect(a.destello(0.25f, cerca(c), frio(c)), Offset(pr.right - 1f * u, pr.top), Size(1f * u, pr.height))
            pinceladas(pr, if (a.oscuro) P.NOCHE else P.SOMBRA_FRIA, 960 + i, 3, u, 0.1f)
        }
        var x = z.left + w * 0.012f
        while (x < z.right) {
            val p = Offset(x, z.top + h * 0.1f)
            roblonOrden(p, w * 0.0042f, a, cerca(p), P.ACERO_MEDIO, frio(p))
            x += paso
        }
        // La banda de las franjas de peligro, gastada: desconchones donde asoma el acero y el roce de las botas.
        val fr = Rect(z.left, z.top + h * 0.67f, z.right, z.bottom - h * 0.07f)
        drawRect(a.sombra(0.45f), Offset(fr.left, fr.top - 1.4f * u), Size(fr.width, 1.4f * u))
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            franjaH(fr, P.BETUN, 0f, a, cerca, frio)
            val franjas = Path()
            var xx = fr.left - fr.height
            val pasoF = w * 0.055f
            while (xx < fr.right) {
                franjas.moveTo(xx, fr.bottom); franjas.lineTo(xx + fr.height, fr.top); franjas.lineTo(xx + fr.height + pasoF / 2, fr.top); franjas.lineTo(xx + pasoF / 2, fr.bottom); franjas.close()
                xx += pasoF
            }
            drawPath(franjas, pincelFranjas(fr, a, cerca, frio))
            val gastes = Path()
            for (q in 0 until 24) {
                val p = Offset(fr.left + fr.width * azar(990, q), fr.top + fr.height * (0.15f + 0.85f * azar(991, q).pow(0.6f)))
                val s = w * (0.003f + 0.008f * azar(992, q))
                gastes.addOval(Rect(p.x - s, p.y - s * 0.45f, p.x + s, p.y + s * 0.45f))
            }
            drawPath(gastes, en(mezcla(P.ACERO_MEDIO, P.ACERO_HONDO, 0.3f), 0f, fr.center, a, cerca, frio))
            for (q in 0 until 6) drawOval(a.sombra(0.25f), Offset(fr.left + fr.width * azar(993, q), fr.bottom - fr.height * 0.45f), Size(w * 0.06f, fr.height * 0.45f))
        }
        drawRect(a.sombra(0.55f), Offset(z.left, z.bottom - h * 0.07f), Size(z.width, h * 0.07f))
    }

    /** El pincel de las franjas amarillas: con la luz cambiando a lo ancho (un degradado de cinco paradas). */
    private fun DrawScope.pincelFranjas(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float): Brush {
        val paradas = Array(5) { i -> val x = r.left + r.width * i / 4f; (i / 4f) to en(P.PELIGRO, 0f, Offset(x, r.center.y), a, cerca, frio) }
        return Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right)
    }

    /** El suelo de chapa estriada en [r]: lágrimas en relieve, más juntas al fondo, en dos caminos, con su mugre. */
    private fun DrawScope.suelo(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val base = mezcla(P.ACERO_HONDO, P.ACERO_MEDIO, 0.45f)
        franjaH(r, base, 0f, a, cerca, frio)
        if (a.lumbre) {
            // El charco del farol en la chapa, que se abre a lo ancho.
            val c = Offset(w * M.FAROL_X, r.bottom - w * 0.03f)
            val rr = w * 0.42f
            val f = if (a.dormida) 0.5f else if (a.oscuro) 1f else 0.45f
            drawOval(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.3f * f), P.LLAMA.copy(alpha = 0f)), c, rr), Offset(c.x - rr, c.y - rr * 0.45f), Size(rr * 2, rr * 0.9f))
        }
        val sombras = Path(); val luces = Path()
        var y = r.top + w * 0.012f; var fila = 0
        val alto = maxOf(1f, r.height)
        while (y < r.bottom && fila < 40) {
            val paso = w * (0.022f + 0.014f * (y - r.top) / alto)
            var x = -w * 0.01f + (if (fila % 2 == 0) 0f else paso * 0.75f)
            var j = 0
            while (x < w && j < 80) {
                val s = if ((fila + j) % 2 == 0) 1f else -1f
                val pa = Offset(x - paso * 0.22f, y + s * paso * 0.1f); val pb = Offset(x + paso * 0.22f, y - s * paso * 0.1f)
                sombras.moveTo(pa.x + 0.6f * u, pa.y + 0.8f * u); sombras.lineTo(pb.x + 0.6f * u, pb.y + 0.8f * u)
                luces.moveTo(pa.x, pa.y); luces.lineTo(pb.x, pb.y)
                x += paso * 1.5f; j++
            }
            y += paso * 0.75f; fila++
        }
        drawPath(sombras, a.sombra(0.45f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        if (a.oscuro) {
            val paradas = Array(5) { i -> val x = w * i / 4f; (i / 4f) to a.destello(0.3f, cerca(Offset(x, r.center.y)), frio(Offset(x, r.center.y))) }
            drawPath(luces, Brush.horizontalGradient(*paradas, startX = 0f, endX = w), style = Stroke(1f * u, cap = StrokeCap.Round))
        } else drawPath(luces, a.destello(0.22f, 0f), style = Stroke(1f * u, cap = StrokeCap.Round))
        pinceladas(r, if (a.oscuro) P.NOCHE else P.BETUN, 931, 12, u, 0.09f)
        drawOval(a.luz(P.BETUN, 0f).copy(alpha = 0.2f), Offset(w * 0.45f, r.top + r.height * 0.55f), Size(w * 0.16f, r.height * 0.14f))
    }

    // ---------------------------------------------------------------- el farol de ronda

    /** El farol de ronda: la base, los cuatro postes, los vidrios con su reja, la vela dentro, el tejadillo con sus respiraderos y el aro. */
    private fun DrawScope.farol(w: Float, h: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val cx = w * M.FAROL_X
        val pisa = h - w * 0.024f
        val ancho = w * 0.11f
        val caja = Rect(cx - ancho / 2, pisa - w * 0.13f, cx + ancho / 2, pisa - w * 0.016f)
        val llamaY = h - w * (0.3f * (1f - M.FAROL_Y))
        val k = if (a.lumbre) 0.9f else 0f
        val fr = frio(caja.center)
        // Su sombra en el suelo, larga hacia la derecha de día.
        contactoOrden(Offset(cx, pisa), ancho * 1.5f, w * 0.022f, a, 0.5f)
        if (!a.oscuro) drawOval(a.sombra(0.22f), Offset(cx, pisa - w * 0.012f), Size(w * 0.16f, w * 0.022f))
        // Dentro: el resplandor de la vela (de noche), la vela y la llama.
        val dentro = Rect(caja.left + w * 0.008f, caja.top + w * 0.004f, caja.right - w * 0.008f, caja.bottom)
        drawRect(if (a.lumbre) mezcla(en(P.ACERO_HONDO, 0f, caja.center, a, cerca, frio), P.LLAMA, if (a.oscuro && !a.dormida) 0.55f else 0.3f) else a.dia(P.PAVON_HONDO, -0.1f, 0f), dentro.topLeft, dentro.size)
        val vela = Rect(cx - w * 0.011f, llamaY + w * 0.01f, cx + w * 0.011f, caja.bottom - w * 0.004f)
        val cera = en(P.CERA, 0f, vela.center, a, cerca, frio)
        drawRect(cera, vela.topLeft, vela.size)
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.3f, vela.top), Size(vela.width * 0.3f, vela.height))
        drawLine(en(P.BETUN, 0f, vela.center, a, cerca, frio), Offset(cx, vela.top + 0.5f * u), Offset(cx, vela.top - w * 0.005f), 1.1f * u, StrokeCap.Round)
        if (a.lumbre) with(CoronaEstanteOrden) { llama(Offset(cx, vela.top - w * 0.002f), w * (if (a.oscuro && !a.dormida) 0.009f else 0.0075f), a) }
        // Los vidrios: un velo frío de día, con su reflejo; la reja de alambre por fuera.
        if (!a.lumbre) {
            drawRect(P.LUZ_FRIA.copy(alpha = 0.18f), dentro.topLeft, dentro.size)
            drawRect(P.LUZ_FRIA.copy(alpha = 0.35f), Offset(dentro.left + dentro.width * 0.1f, dentro.top), Size(dentro.width * 0.12f, dentro.height))
        }
        val reja = en(P.ACERO_HONDO, 0f, caja.center, a, cerca, frio)
        val rej = Path()
        listOf(0.33f, 0.66f).forEach { f -> rej.moveTo(dentro.left + dentro.width * f, dentro.top); rej.lineTo(dentro.left + dentro.width * f, dentro.bottom) }
        listOf(0.3f, 0.62f).forEach { f -> rej.moveTo(dentro.left, dentro.top + dentro.height * f); rej.lineTo(dentro.right, dentro.top + dentro.height * f) }
        drawPath(rej, reja, style = Stroke(1.4f * u))
        // Los postes y la base.
        listOf(caja.left, caja.right - w * 0.008f).forEachIndexed { i, x ->
            val pr = Rect(x, caja.top, x + w * 0.008f, caja.bottom)
            drawRect(en(P.PAVON, if (i == 0) 0.05f else -0.15f, pr.center, a, cerca, frio), pr.topLeft, pr.size)
            drawRect(if (a.lumbre && i == 0) a.filo(0.6f) else a.destello(0.4f, k, fr), pr.topLeft, Size(1f * u, pr.height))
        }
        val base = Rect(caja.left - w * 0.006f, caja.bottom, caja.right + w * 0.006f, pisa)
        drawRect(en(P.PAVON, 0f, base.center, a, cerca, frio), base.topLeft, base.size)
        drawRect(a.destello(0.4f, k, fr), base.topLeft, Size(base.width, 1f * u))
        drawRect(a.sombra(0.35f), Offset(base.right - base.width * 0.2f, base.top), Size(base.width * 0.2f, base.height))
        // El tejadillo en pirámide con sus respiraderos y el aro para colgarlo.
        val ala = Rect(caja.left - w * 0.008f, caja.top - w * 0.008f, caja.right + w * 0.008f, caja.top)
        drawRect(en(P.PAVON_LUZ, 0f, ala.center, a, cerca, frio), ala.topLeft, ala.size)
        drawRect(a.sombra(0.4f), Offset(ala.left, ala.bottom - 1f * u), Size(ala.width, 1f * u))
        val punta = Offset(cx, ala.top - w * 0.034f)
        drawPath(poligonoOrden(ala.left + w * 0.004f, ala.top, cx, ala.top, cx, punta.y), en(P.PAVON_LUZ, 0.05f, ala.center, a, cerca, frio))
        drawPath(poligonoOrden(cx, ala.top, ala.right - w * 0.004f, ala.top, cx, punta.y), en(P.PAVON, -0.2f, ala.center, a, cerca, frio))
        listOf(-1f, 1f).forEach { s -> drawRect(a.sombra(0.6f), Offset(cx + s * w * 0.012f - w * 0.003f, ala.top - w * 0.014f), Size(w * 0.006f, w * 0.006f)) }
        val aro = Offset(cx, punta.y - w * 0.01f)
        drawCircle(en(P.ACERO_MEDIO, 0f, aro, a, cerca, frio), w * 0.012f, aro, style = Stroke(1.8f * u))
        drawArc(a.destello(0.55f, k, fr), 180f, 90f, false, Offset(aro.x - w * 0.012f, aro.y - w * 0.012f), Size(w * 0.024f, w * 0.024f), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- la aceitera

    /** La aceitera de latón del Ideario, más grande, en el suelo: el cuerpo bajo, el cono, el pico largo, la palanca; su cerco de aceite. */
    private fun DrawScope.aceitera(pie: Offset, w: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val s = w * 1.5f
        val ox = pie.x - s * 0.655f; val oy = pie.y - s * 0.5f
        fun p(x: Float, y: Float) = Offset(ox + s * x, oy + s * y)
        val k = cerca(pie); val fr = frio(pie)
        drawOval(en(mezcla(P.BETUN, P.OXIDO, 0.25f), 0f, pie, a, cerca, frio).copy(alpha = 0.35f), p(0.612f, 0.494f), Size(s * 0.1f, s * 0.012f))
        contactoOrden(pie, s * 0.068f, s * 0.008f, a, 0.5f)
        // El pico: largo, fino, en diagonal hacia arriba a la izquierda.
        val pico = Path().apply { val a0 = p(0.652f, 0.444f); moveTo(a0.x, a0.y); p(0.659f, 0.447f).let { lineTo(it.x, it.y) }; p(0.6035f, 0.3725f).let { lineTo(it.x, it.y) }; p(0.6005f, 0.3745f).let { lineTo(it.x, it.y) }; close() }
        drawPath(pico, en(P.LATON_HONDO, 0f, pie, a, cerca, frio))
        drawLine(en(P.LATON_BRILLO, 0f, pie, a, cerca, frio), p(0.652f, 0.444f), p(0.601f, 0.374f), 0.9f * u)
        // El cono y su collar.
        val cono = Path().apply { val q = listOf(p(0.641f, 0.47f), p(0.669f, 0.47f), p(0.661f, 0.441f), p(0.649f, 0.441f)); moveTo(q[0].x, q[0].y); q.drop(1).forEach { lineTo(it.x, it.y) }; close() }
        drawPath(cono, en(P.LATON, 0f, pie, a, cerca, frio))
        clipPath(cono) { drawRect(a.sombra(0.35f), p(0.658f, 0.44f), Size(s * 0.012f, s * 0.03f)) }
        drawOval(en(P.LATON_HONDO, 0f, pie, a, cerca, frio), p(0.647f, 0.437f), Size(s * 0.016f, s * 0.007f))
        // El cuerpo abombado, con su abolladura.
        val cuerpo = Rect(p(0.626f, 0.464f).x, p(0.626f, 0.464f).y, p(0.684f, 0.5f).x, p(0.684f, 0.5f).y)
        val cu = Path().apply { addOval(cuerpo) }
        drawPath(cu, en(P.LATON, 0f, pie, a, cerca, frio))
        clipPath(cu) {
            drawRect(a.sombra(0.4f), Offset(cuerpo.right - cuerpo.width * 0.3f, cuerpo.top), Size(cuerpo.width * 0.3f, cuerpo.height))
            drawRect(a.sombra(0.3f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.3f), Size(cuerpo.width, cuerpo.height * 0.3f))
            drawOval(a.brillo(0.45f, maxOf(k, 0.2f)), Offset(cuerpo.left + cuerpo.width * 0.12f, cuerpo.top + cuerpo.height * 0.12f), Size(cuerpo.width * 0.38f, cuerpo.height * 0.28f))
            drawOval(a.sombra(0.28f), Offset(cuerpo.left + cuerpo.width * 0.66f, cuerpo.top + cuerpo.height * 0.45f), Size(cuerpo.width * 0.2f, cuerpo.height * 0.25f))
            drawLine(en(P.LATON_HONDO, 0f, pie, a, cerca, frio), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.72f), Offset(cuerpo.right, cuerpo.top + cuerpo.height * 0.72f), 0.8f * u)
        }
        if (a.lumbre) drawLine(a.filo(0.5f * k), Offset(cuerpo.left + 1f * u, cuerpo.top + cuerpo.height * 0.2f), Offset(cuerpo.left + 1f * u, cuerpo.bottom - cuerpo.height * 0.2f), 1.2f * u)
        // La palanca de la bomba.
        drawPath(Path().apply { val a0 = p(0.682f, 0.478f); val b0 = p(0.694f, 0.472f); val c0 = p(0.698f, 0.462f); val d0 = p(0.694f, 0.452f); moveTo(a0.x, a0.y); cubicTo(b0.x, b0.y, c0.x, c0.y, d0.x, d0.y) },
            en(P.ACERO_MEDIO, 0f, pie, a, cerca, frio), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawCircle(en(P.ACERO_MEDIO, 0f, pie, a, cerca, frio), 1.8f * u, p(0.694f, 0.452f))
    }

    // ---------------------------------------------------------------- el bidón

    /** El bidón carmesí abollado, cortado por el borde derecho: los aros rodados, la boca con su tapón, el trapo y la pintura gastada. */
    private fun DrawScope.bidon(w: Float, h: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val cuerpo = Rect(w * 0.79f, h - w * 0.235f, w * 1.07f, h - w * 0.022f)
        val c = cuerpo.center
        val k = cerca(c); val fr = frio(c)
        contactoOrden(Offset(c.x - w * 0.02f, cuerpo.bottom), cuerpo.width * 1.15f, w * 0.03f, a, 0.55f, -1f)
        // Su sombra en el zócalo, a la izquierda de noche (la vela del farol) y a la derecha de día (fuera del cuadro).
        val grana = en(P.GRANA, 0f, c, a, cerca, frio)
        redondoOrden(cuerpo, grana, if (a.oscuro) mezcla(grana, P.LLAMA, 0.25f * k) else mezcla(grana, P.LUZ_FRIA, 0.3f), mezcla(grana, P.NOCHE, 0.6f), -1f)
        // La tapa en escorzo con su borde y su tapón.
        val tapa = Rect(cuerpo.left, cuerpo.top - w * 0.018f, cuerpo.right, cuerpo.top + w * 0.018f)
        drawOval(en(mezcla(P.GRANA, P.GRANA_HONDA, 0.4f), 0f, c, a, cerca, frio), tapa.topLeft, tapa.size)
        drawOval(en(P.ACERO_MEDIO, 0f, c, a, cerca, frio), tapa.topLeft, tapa.size, style = Stroke(2.2f * u))
        drawOval(en(P.ACERO_HONDO, 0f, c, a, cerca, frio), Offset(cuerpo.left + cuerpo.width * 0.28f, tapa.top + tapa.height * 0.3f), Size(w * 0.03f, tapa.height * 0.35f))
        drawOval(a.destello(0.4f, k, fr), Offset(cuerpo.left + cuerpo.width * 0.29f, tapa.top + tapa.height * 0.3f), Size(w * 0.012f, tapa.height * 0.12f))
        // Los dos aros rodados.
        listOf(0.36f, 0.7f).forEach { f ->
            val y = cuerpo.top + cuerpo.height * f
            drawRect(a.sombra(0.4f), Offset(cuerpo.left, y + 1.8f * u), Size(cuerpo.width, 1.8f * u))
            drawRect(en(mezcla(P.GRANA, P.GRANA_LUZ, 0.3f), 0f, Offset(c.x, y), a, cerca, frio), Offset(cuerpo.left, y), Size(cuerpo.width, 1.8f * u))
            drawRect(a.brillo(0.35f, k), Offset(cuerpo.left, y), Size(cuerpo.width * 0.3f, 0.9f * u))
        }
        // La pintura gastada donde asoma el acero, la abolladura y el óxido de abajo.
        val gastes = Path()
        for (q in 0 until 12) {
            val x = cuerpo.left + cuerpo.width * 0.7f * azar(1201, q)
            val y = if (q % 3 == 0) cuerpo.top + 2f * u else cuerpo.top + cuerpo.height * (0.3f + 0.65f * azar(1202, q))
            val s = w * (0.004f + 0.01f * azar(1203, q))
            gastes.addOval(Rect(x, y, x + s * 1.6f, y + s * 0.6f))
        }
        drawPath(gastes, en(P.ACERO_MEDIO, 0f, c, a, cerca, frio).copy(alpha = 0.85f))
        drawOval(a.sombra(0.3f), Offset(cuerpo.left + cuerpo.width * 0.15f, cuerpo.top + cuerpo.height * 0.45f), Size(cuerpo.width * 0.18f, cuerpo.height * 0.16f))
        drawOval(a.brillo(0.25f, k), Offset(cuerpo.left + cuerpo.width * 0.13f, cuerpo.top + cuerpo.height * 0.43f), Size(cuerpo.width * 0.1f, cuerpo.height * 0.05f))
        drawRect(en(P.OXIDO, 0f, c, a, cerca, frio).copy(alpha = 0.5f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.08f), Size(cuerpo.width, cuerpo.height * 0.08f))
        if (a.lumbre) drawRect(a.filo(0.5f * k), Offset(cuerpo.left, cuerpo.top), Size(1.6f * u, cuerpo.height))
        // El trapo gris colgado del borde, con sus pliegues.
        val t0 = cuerpo.left + cuerpo.width * 0.08f
        val trapo = Path().apply {
            moveTo(t0, tapa.center.y - w * 0.004f); lineTo(t0 + w * 0.09f, tapa.center.y + w * 0.002f)
            cubicTo(t0 + w * 0.088f, tapa.bottom + w * 0.03f, t0 + w * 0.075f, tapa.bottom + w * 0.06f, t0 + w * 0.07f, tapa.bottom + w * 0.075f)
            lineTo(t0 + w * 0.05f, tapa.bottom + w * 0.065f); lineTo(t0 + w * 0.035f, tapa.bottom + w * 0.08f); lineTo(t0 + w * 0.012f, tapa.bottom + w * 0.06f)
            cubicTo(t0 + w * 0.004f, tapa.bottom + w * 0.03f, t0 - w * 0.004f, tapa.bottom + w * 0.01f, t0, tapa.center.y - w * 0.004f)
            close()
        }
        translate(w * 0.006f, w * 0.005f) { drawPath(trapo, a.sombra(0.35f)) }
        val tela = en(Color(0xFF8E9298), 0f, c, a, cerca, frio)
        drawPath(trapo, tela)
        clipPath(trapo) {
            listOf(0.025f, 0.055f).forEach { f -> drawLine(a.sombra(0.35f), Offset(t0 + w * f, tapa.center.y), Offset(t0 + w * (f + 0.006f), tapa.bottom + w * 0.08f), 2.4f * u) }
            drawLine(a.brillo(0.3f, k), Offset(t0 + w * 0.014f, tapa.center.y), Offset(t0 + w * 0.02f, tapa.bottom + w * 0.06f), 1.6f * u)
            drawRect(mezcla(tela, P.BETUN, 0.4f).copy(alpha = 0.4f), Offset(t0 + w * 0.03f, tapa.bottom + w * 0.03f), Size(w * 0.03f, w * 0.02f))
        }
    }
}
