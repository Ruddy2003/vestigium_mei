package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que acompaña en los huecos libres de las repisas de Orden, en grupos de dos o tres cosas solapadas que cambian de un hueco
 * a otro: el estuche del micrómetro abierto (el del Ideario, aquí en la repisa) con el cartabón de latón apoyado detrás; los
 * rollos de plano atados, tumbados en pirámide, con otro de pie y una pesa; el candado grande con su cadena y su llave delante
 * del estandarte enrollado en su asta; y, donde arde la vela de media altura, la palmatoria con su vela, dos manuales tumbados y la
 * lupa apoyada.
 */
internal object HuecosEstanteOrden {
    private val P = PaletaOrden
    private val M = MuebleEstanteOrden

    /** El color de [c] en [p] con la luz del momento. */
    private fun en(c: Color, f: Float, p: Offset, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float): Color =
        if (a.oscuro) { val b = a.luz(c, cerca(p), frio(p)); if (f < 0f) mezcla(b, P.NOCHE, -f * 0.45f) else b } else a.dia(c, f, cerca(p))

    /**
     * Llena el hueco de la repisa [r] desde [ocupado] hasta [derecha] con grupos; [n] es cuántos se pusieron antes (para no
     * repetir) y [xVela], dónde arde la vela de este hueco (o -1). Devuelve cuántos grupos puso.
     */
    fun DrawScope.hueco(n: Int, r: Rect, ocupado: Float, derecha: Float, xVela: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float): Int {
        val w = size.width
        val u = density
        var x = ocupado + w * 0.02f
        val fin = derecha - 8f * u
        var puestos = 0
        if (xVela >= 0f && fin - x >= w * 0.16f) {
            vela(x, r, a, cerca, frio)
            x += w * 0.2f
            puestos++
        }
        var i = 0
        while (i < 3) {
            val clase = Math.floorMod(n + puestos + r.top.toInt() / 7, 3)
            val ancho = when (clase) { 0 -> w * 0.2f; 1 -> w * 0.2f; else -> w * 0.19f }
            if (fin - x < ancho) break
            when (clase) {
                0 -> estuche(x, r, a, cerca, frio)
                1 -> rollos(x, r, a, cerca, frio, n + puestos)
                else -> candado(x, r, a, cerca, frio)
            }
            x += ancho + w * 0.025f
            puestos++; i++
        }
        return puestos
    }

    // ---------------------------------------------------------------- el estuche del micrómetro y el cartabón

    private fun DrawScope.estuche(x0: Float, r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val hh = r.height
        val y = r.bottom - 2.5f * u
        // El cartabón de latón apoyado contra el muro, detrás: un triángulo con su calado.
        val c0 = Offset(x0 + w * 0.09f, y)
        rotate(8f, c0) {
            val t = poligonoOrden(x0 + w * 0.06f, y, x0 + w * 0.185f, y, x0 + w * 0.06f, y - hh * 0.86f)
            val hueco = poligonoOrden(x0 + w * 0.078f, y - hh * 0.1f, x0 + w * 0.142f, y - hh * 0.1f, x0 + w * 0.078f, y - hh * 0.55f)
            translate(w * 0.012f, w * 0.004f) { drawPath(t, a.sombra(0.3f)) }
            val lat = en(P.LATON, 0f, Offset(x0 + w * 0.1f, y - hh * 0.4f), a, cerca, frio)
            drawPath(t, lat)
            drawPath(hueco, en(P.PARED, -0.45f, Offset(x0 + w * 0.1f, y - hh * 0.3f), a, cerca, frio))
            drawPath(hueco, a.sombra(0.4f), style = Stroke(1f * u))
            drawLine(a.destello(0.6f, cerca(c0), frio(c0)), Offset(x0 + w * 0.06f, y), Offset(x0 + w * 0.06f, y - hh * 0.86f), 1.2f * u)
            drawLine(a.sombra(0.4f), Offset(x0 + w * 0.185f, y), Offset(x0 + w * 0.06f, y - hh * 0.86f), 1f * u)
            // Las marcas de la graduación, sin cifras.
            var yy = y - hh * 0.06f
            var q = 0
            while (yy > y - hh * 0.8f && q < 40) {
                drawLine(en(P.LATON_HONDO, 0f, c0, a, cerca, frio), Offset(x0 + w * 0.06f, yy), Offset(x0 + w * (if (q % 4 == 0) 0.07f else 0.065f), yy), 0.6f * u)
                yy -= hh * 0.035f; q++
            }
        }
        // El estuche: la caja de cuero con su cierre y la tapa abierta forrada de fieltro carmesí, con el micrómetro en su hueco.
        val caja = Rect(x0, y - hh * 0.2f, x0 + w * 0.15f, y)
        val tapa = Rect(caja.left + w * 0.004f, caja.top - hh * 0.4f, caja.right - w * 0.004f, caja.top)
        val k = cerca(tapa.center); val fr = frio(tapa.center)
        contactoOrden(Offset(caja.center.x, y), caja.width * 1.1f, 4f * u, a, 0.45f)
        translate(w * 0.008f, w * 0.004f) { drawPath(poligonoOrden(tapa.left, tapa.bottom, tapa.right, tapa.bottom, tapa.right - w * 0.006f, tapa.top, tapa.left + w * 0.006f, tapa.top), a.sombra(0.25f)) }
        drawPath(poligonoOrden(tapa.left, tapa.bottom, tapa.right, tapa.bottom, tapa.right - w * 0.006f, tapa.top, tapa.left + w * 0.006f, tapa.top), en(P.CUERO, 0f, tapa.center, a, cerca, frio))
        val fieltro = Rect(tapa.left + w * 0.007f, tapa.top + hh * 0.03f, tapa.right - w * 0.007f, tapa.bottom - hh * 0.02f)
        drawRect(en(mezcla(P.GRANA, P.GRANA_HONDA, 0.35f), 0f, fieltro.center, a, cerca, frio), fieltro.topLeft, fieltro.size)
        drawRect(a.sombra(0.3f), fieltro.topLeft, Size(fieltro.width, 2.4f * u))
        drawRect(a.sombra(0.25f), fieltro.topLeft, Size(2.4f * u, fieltro.height))
        drawLine(a.destello(0.35f, k, fr), Offset(tapa.left + w * 0.006f, tapa.top + 0.5f * u), Offset(tapa.right - w * 0.006f, tapa.top + 0.5f * u), 0.9f * u)
        // El micrómetro: el arco, el yunque, el husillo, el tambor graduado y el trinquete.
        val my = fieltro.top + fieltro.height * 0.45f
        val ax = fieltro.left + fieltro.width * 0.1f
        val s = fieltro.width
        val arco = Path().apply {
            moveTo(ax, my - s * 0.04f); lineTo(ax + s * 0.07f, my - s * 0.04f)
            cubicTo(ax + s * 0.07f, my + s * 0.16f, ax + s * 0.36f, my + s * 0.18f, ax + s * 0.38f, my - s * 0.04f)
            lineTo(ax + s * 0.45f, my - s * 0.04f)
            cubicTo(ax + s * 0.44f, my + s * 0.28f, ax + s * 0.01f, my + s * 0.28f, ax, my - s * 0.04f)
            close()
        }
        translate(1f * u, 1.2f * u) { drawPath(arco, a.sombra(0.45f)) }
        drawPath(arco, en(mezcla(P.ACERO_HONDO, P.CUERO, 0.2f), 0f, fieltro.center, a, cerca, frio))
        drawRect(en(P.ACERO_CLARO, 0f, fieltro.center, a, cerca, frio), Offset(ax, my - s * 0.08f), Size(s * 0.07f, s * 0.06f))
        drawRect(en(P.ACERO_CLARO, 0f, fieltro.center, a, cerca, frio), Offset(ax + s * 0.14f, my - s * 0.065f), Size(s * 0.31f, s * 0.04f))
        val tambor = Rect(ax + s * 0.45f, my - s * 0.09f, ax + s * 0.62f, my + s * 0.005f)
        redondoOrden(Rect(tambor.left, tambor.top, tambor.right, tambor.bottom), en(P.ACERO_CLARO, 0f, fieltro.center, a, cerca, frio), en(P.LUZ_FRIA, 0f, fieltro.center, a, cerca, frio), en(P.ACERO_HONDO, 0f, fieltro.center, a, cerca, frio), -1f, 0.8f)
        for (q in 0 until 5) drawLine(en(P.ACERO_HONDO, 0f, fieltro.center, a, cerca, frio), Offset(tambor.left + s * 0.03f * (q + 0.5f), tambor.center.y), Offset(tambor.left + s * 0.03f * (q + 0.5f), tambor.top + 0.5f * u), 0.5f * u)
        val dedal = Rect(tambor.right, my - s * 0.11f, tambor.right + s * 0.14f, my + s * 0.025f)
        redondoOrden(dedal, en(P.ACERO_MEDIO, 0f, fieltro.center, a, cerca, frio), en(P.ACERO_CLARO, 0f, fieltro.center, a, cerca, frio), en(P.ACERO_HONDO, 0f, fieltro.center, a, cerca, frio), -1f)
        for (q in 0 until 4) drawLine(a.sombra(0.4f), Offset(dedal.left + dedal.width * (q + 0.6f) / 4.4f, dedal.top), Offset(dedal.left + dedal.width * (q + 0.6f) / 4.4f, dedal.bottom), 0.5f * u)
        // La caja, con su canto de luz, su cierre de latón y su desgaste.
        drawRect(en(P.CUERO, 0f, caja.center, a, cerca, frio), caja.topLeft, caja.size)
        drawRect(en(mezcla(P.CUERO, P.LUZ_FRIA, 0.12f), 0.05f, caja.center, a, cerca, frio), caja.topLeft, Size(caja.width, caja.height * 0.16f))
        drawRect(a.sombra(0.4f), Offset(caja.right - caja.width * 0.08f, caja.top), Size(caja.width * 0.08f, caja.height))
        drawRect(a.sombra(0.35f), Offset(caja.left, caja.bottom - 1.4f * u), Size(caja.width, 1.4f * u))
        val cierre = Rect(caja.center.x - w * 0.008f, caja.top + caja.height * 0.2f, caja.center.x + w * 0.008f, caja.top + caja.height * 0.56f)
        drawRect(en(P.LATON, 0f, cierre.center, a, cerca, frio), cierre.topLeft, cierre.size)
        drawRect(a.destello(0.6f, k, fr), cierre.topLeft, Size(cierre.width, 0.9f * u))
        drawRect(a.sombra(0.4f), Offset(cierre.left, cierre.bottom - 1f * u), Size(cierre.width, 1f * u))
        listOf(caja.left, caja.right - w * 0.014f).forEach { xc ->
            drawRect(en(P.LATON, 0f, caja.center, a, cerca, frio), Offset(xc, caja.bottom - w * 0.014f), Size(w * 0.014f, w * 0.014f))
        }
        for (q in 0 until 4) {
            val xx = caja.left + caja.width * azar(771, q); val yy = caja.top + caja.height * (0.3f + 0.6f * azar(772, q))
            drawLine(a.destello(0.25f, k, fr), Offset(xx, yy), Offset(xx + w * 0.012f, yy + 0.3f * u), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- los rollos atados

    /** Un rollo de plano tumbado a lo largo, de [r]: el cuerpo redondo de arriba abajo, la boca con su espiral y su atadura. */
    private fun DrawScope.rolloTumbado(r: Rect, papel: Color, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val c = en(papel, 0f, r.center, a, cerca, frio)
        val luz = if (a.oscuro) mezcla(c, P.LLAMA, 0.2f * cerca(r.center)) else mezcla(c, P.LUZ_FRIA, 0.35f)
        // Por franjas de arriba abajo: la luz arriba, la sombra abajo.
        val bandas = floatArrayOf(0f, 0.15f, 0.35f, 0.6f, 0.82f, 1f)
        val tonos = floatArrayOf(0.4f, 0.12f, -0.1f, -0.35f, -0.6f)
        for (i in 0 until 5) {
            val m = tonos[i]
            drawRect(if (m >= 0f) mezcla(c, luz, m) else mezcla(c, mezcla(c, P.NOCHE, 0.7f), -m), Offset(r.left, r.top + r.height * bandas[i]), Size(r.width, r.height * (bandas[i + 1] - bandas[i]) + 1f))
        }
        drawLine(a.luz(P.TRAZO, cerca(r.center), frio(r.center)).copy(alpha = 0.35f), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.42f), Offset(r.right - r.width * 0.2f, r.top + r.height * 0.42f), 0.6f * u)
        // La atadura.
        val ax = r.left + r.width * 0.62f
        drawRect(en(P.PAPEL, 0f, r.center, a, cerca, frio), Offset(ax, r.top), Size(1.8f * u, r.height))
        drawRect(a.sombra(0.35f), Offset(ax + 1.8f * u, r.top), Size(1f * u, r.height))
        // La boca a la izquierda, con su espiral.
        val bo = Rect(r.left - r.height * 0.18f, r.top, r.left + r.height * 0.18f, r.bottom)
        drawOval(en(mezcla(papel, P.TRAZO, 0.45f), 0.05f, r.center, a, cerca, frio), bo.topLeft, bo.size)
        for (q in 1..3) {
            val f = 1f - q * 0.24f
            drawOval(c.copy(alpha = 0.85f), Offset(bo.center.x - bo.width / 2 * f, bo.center.y - bo.height / 2 * f), Size(bo.width * f, bo.height * f), style = Stroke(0.7f * u))
        }
        drawOval(a.sombra(0.35f), bo.topLeft, bo.size, style = Stroke(0.7f * u))
    }

    private fun DrawScope.rollos(x0: Float, r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, n: Int) {
        val u = density
        val w = size.width
        val hh = r.height
        val y = r.bottom - 2.5f * u
        val d = hh * 0.2f
        // Uno de pie, apoyado contra el muro, detrás.
        val pie = Offset(x0 + w * 0.165f, y)
        rotate(-9f, pie) {
            val cuerpo = Rect(pie.x - d * 0.42f, y - hh * 0.84f, pie.x + d * 0.42f, y)
            translate(w * 0.012f, w * 0.004f) { drawRect(a.sombra(0.28f), cuerpo.topLeft, cuerpo.size) }
            val c = en(mezcla(P.AZUL_PLANO, P.TRAZO, 0.06f), 0f, cuerpo.center, a, cerca, frio)
            redondoOrden(cuerpo, c, if (a.oscuro) mezcla(c, P.LLAMA, 0.2f * cerca(cuerpo.center)) else mezcla(c, P.LUZ_FRIA, 0.35f), mezcla(c, P.NOCHE, 0.55f), -1f)
            listOf(0.2f, 0.75f).forEach { f -> drawRect(en(P.PAPEL, 0f, cuerpo.center, a, cerca, frio), Offset(cuerpo.left, cuerpo.top + cuerpo.height * f), Size(cuerpo.width, 1.6f * u)) }
            val bo = Rect(cuerpo.left, cuerpo.top - d * 0.15f, cuerpo.right, cuerpo.top + d * 0.15f)
            drawOval(en(mezcla(P.AZUL_PLANO, P.TRAZO, 0.45f), 0f, cuerpo.center, a, cerca, frio), bo.topLeft, bo.size)
            drawOval(c, Offset(bo.center.x - bo.width * 0.3f, bo.center.y - bo.height * 0.3f), Size(bo.width * 0.6f, bo.height * 0.6f), style = Stroke(0.7f * u))
        }
        // La pirámide de tres rollos tumbados, atados.
        contactoOrden(Offset(x0 + w * 0.08f, y), w * 0.16f, 4.5f * u, a, 0.5f)
        val papelA = P.AZUL_PLANO; val papelB = mezcla(P.AZUL_PLANO, P.TRAZO, 0.12f); val papelC = if (n % 2 == 0) P.PAPEL else mezcla(P.AZUL_PLANO, P.NOCHE, 0.15f)
        rolloTumbado(Rect(x0 + w * 0.012f, y - d, x0 + w * 0.08f, y), papelA, a, cerca, frio)
        rolloTumbado(Rect(x0 + w * 0.012f + d * 0.4f + w * 0.068f - d * 0.36f, y - d, x0 + w * 0.15f, y), papelB, a, cerca, frio)
        rolloTumbado(Rect(x0 + w * 0.04f, y - d * 1.86f, x0 + w * 0.125f, y - d * 0.86f), papelC, a, cerca, frio)
        // El cordón que los ata juntos.
        drawLine(en(P.PAPEL, 0f, Offset(x0, y), a, cerca, frio), Offset(x0 + w * 0.1f, y - d * 1.85f), Offset(x0 + w * 0.1f, y), 1.6f * u)
        // La pesa de latón delante.
        val px = x0 + w * 0.175f
        contactoOrden(Offset(px, y), w * 0.04f, 3.5f * u, a, 0.45f)
        val cuerpo = Rect(px - w * 0.014f, y - hh * 0.13f, px + w * 0.014f, y)
        redondoOrden(cuerpo, en(P.LATON, 0f, cuerpo.center, a, cerca, frio), en(P.LATON_BRILLO, 0f, cuerpo.center, a, cerca, frio), en(P.LATON_HONDO, 0f, cuerpo.center, a, cerca, frio), -1f)
        drawOval(en(P.LATON_BRILLO, 0f, cuerpo.center, a, cerca, frio), Offset(cuerpo.left, cuerpo.top - w * 0.004f), Size(cuerpo.width, w * 0.008f))
        drawOval(en(P.LATON, 0f, cuerpo.center, a, cerca, frio), Offset(px - w * 0.0055f, cuerpo.top - w * 0.012f), Size(w * 0.011f, w * 0.01f))
        drawCircle(a.destello(0.7f, cerca(cuerpo.center), frio(cuerpo.center)), w * 0.002f, Offset(px - w * 0.002f, cuerpo.top - w * 0.008f))
    }

    // ---------------------------------------------------------------- el candado y el estandarte enrollado

    private fun DrawScope.candado(x0: Float, r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val hh = r.height
        val y = r.bottom - 2.5f * u
        // El estandarte de la orden, enrollado en su asta y apoyado contra el muro: no se le ve el bordado.
        val pie = Offset(x0 + w * 0.03f, y)
        rotate(9f, pie) {
            val asta = Rect(pie.x - w * 0.0045f, y - hh * 0.95f, pie.x + w * 0.0045f, y)
            translate(w * 0.012f, w * 0.004f) { drawRect(a.sombra(0.28f), asta.topLeft, asta.size) }
            redondoOrden(asta, en(P.ACERO_MEDIO, 0f, asta.center, a, cerca, frio), en(P.ACERO_CLARO, 0f, asta.center, a, cerca, frio), en(P.ACERO_HONDO, 0f, asta.center, a, cerca, frio), -1f)
            drawRect(en(P.LATON, 0f, asta.center, a, cerca, frio), Offset(asta.left - w * 0.002f, y - hh * 0.06f), Size(asta.width + w * 0.004f, hh * 0.06f))
            // La tela enrollada: un huso carmesí con sus vueltas en espiral y el fleco de latón que asoma.
            val t0 = y - hh * 0.88f; val t1 = y - hh * 0.22f
            val ancho = w * 0.026f
            val huso = Path().apply {
                moveTo(pie.x - w * 0.006f, t0)
                cubicTo(pie.x - ancho, t0 + (t1 - t0) * 0.25f, pie.x - ancho, t0 + (t1 - t0) * 0.7f, pie.x - w * 0.007f, t1)
                lineTo(pie.x + w * 0.007f, t1)
                cubicTo(pie.x + ancho, t0 + (t1 - t0) * 0.7f, pie.x + ancho, t0 + (t1 - t0) * 0.25f, pie.x + w * 0.006f, t0)
                close()
            }
            translate(w * 0.012f, w * 0.004f) { drawPath(huso, a.sombra(0.3f)) }
            val tela = en(P.GRANA, 0f, Offset(pie.x, (t0 + t1) / 2), a, cerca, frio)
            drawPath(huso, tela)
            clipPath(huso) {
                drawRect(en(P.GRANA_LUZ, 0f, Offset(pie.x, t0), a, cerca, frio).copy(alpha = 0.6f), Offset(pie.x - ancho, t0), Size(ancho * 0.6f, t1 - t0))
                drawRect(en(P.GRANA_HONDA, 0f, Offset(pie.x, t0), a, cerca, frio).copy(alpha = 0.75f), Offset(pie.x + ancho * 0.35f, t0), Size(ancho * 0.7f, t1 - t0))
                var yy = t0 + (t1 - t0) * 0.08f
                var q = 0
                while (yy < t1 && q < 20) {
                    drawLine(en(P.GRANA_HONDA, 0f, Offset(pie.x, yy), a, cerca, frio), Offset(pie.x - ancho, yy + w * 0.012f), Offset(pie.x + ancho, yy - w * 0.006f), 1.4f * u)
                    drawLine(en(P.LATON, 0f, Offset(pie.x, yy), a, cerca, frio).copy(alpha = 0.8f), Offset(pie.x - ancho, yy + w * 0.012f + 1.6f * u), Offset(pie.x + ancho, yy - w * 0.006f + 1.6f * u), 0.9f * u)
                    yy += (t1 - t0) * 0.16f; q++
                }
            }
            // El cordón de latón que lo ata, con su borla colgando.
            val cy = t0 + (t1 - t0) * 0.55f
            drawLine(en(P.LATON, 0f, Offset(pie.x, cy), a, cerca, frio), Offset(pie.x - ancho * 0.95f, cy), Offset(pie.x + ancho * 0.95f, cy - w * 0.003f), 1.8f * u)
            val b0 = Offset(pie.x + ancho * 0.8f, cy)
            drawLine(en(P.LATON, 0f, b0, a, cerca, frio), b0, b0 + Offset(w * 0.004f, hh * 0.12f), 1f * u)
            drawPath(poligonoOrden(b0.x + w * 0.001f, b0.y + hh * 0.11f, b0.x + w * 0.007f, b0.y + hh * 0.11f, b0.x + w * 0.011f, b0.y + hh * 0.2f, b0.x - w * 0.003f, b0.y + hh * 0.2f), en(P.LATON, 0f, b0, a, cerca, frio))
            // El remate: una bola de latón.
            val bc = Offset(pie.x, y - hh * 0.95f - w * 0.006f)
            drawCircle(en(P.LATON, 0f, bc, a, cerca, frio), w * 0.008f, bc)
            drawCircle(a.destello(0.8f, cerca(bc), frio(bc)), w * 0.0028f, bc - Offset(w * 0.003f, w * 0.003f))
        }
        // La cadena enrollada a su pie, en un óvalo de eslabones.
        val cc = Offset(x0 + w * 0.1f, y - hh * 0.05f)
        contactoOrden(Offset(x0 + w * 0.1f, y), w * 0.17f, 5f * u, a, 0.5f)
        val acero = en(P.ACERO_MEDIO, 0f, cc, a, cerca, frio)
        val luz = a.destello(0.55f, cerca(cc), frio(cc))
        var t = 0.0
        var q = 0
        while (t < Math.PI * 2 && q < 40) {
            val p = Offset(cc.x + w * 0.065f * kotlin.math.cos(t).toFloat(), cc.y + hh * 0.045f * kotlin.math.sin(t).toFloat())
            rotate(Math.toDegrees(t + Math.PI / 2).toFloat(), p) {
                if (q % 2 == 0) {
                    drawOval(a.sombra(0.4f), Offset(p.x - w * 0.007f + 1f * u, p.y - w * 0.004f + 1.2f * u), Size(w * 0.014f, w * 0.008f), style = Stroke(1.6f * u))
                    drawOval(acero, Offset(p.x - w * 0.007f, p.y - w * 0.004f), Size(w * 0.014f, w * 0.008f), style = Stroke(1.6f * u))
                    drawArc(luz, 190f, 90f, false, Offset(p.x - w * 0.007f, p.y - w * 0.004f), Size(w * 0.014f, w * 0.008f), style = Stroke(0.7f * u))
                } else drawLine(acero, Offset(p.x - w * 0.006f, p.y), Offset(p.x + w * 0.006f, p.y), 2f * u, StrokeCap.Round)
            }
            t += 0.42; q++
        }
        // El candado grande, de pie: el cuerpo de acero con su remache, el arco y la bocallave.
        val cuerpo = Rect(x0 + w * 0.05f, y - hh * 0.42f, x0 + w * 0.15f, y - hh * 0.04f)
        val k = cerca(cuerpo.center); val fr = frio(cuerpo.center)
        contactoOrden(Offset(cuerpo.center.x, y - hh * 0.04f), cuerpo.width * 1.1f, 4f * u, a, 0.4f)
        val arco = Path().apply {
            val l = cuerpo.left + cuerpo.width * 0.16f; val rr = cuerpo.right - cuerpo.width * 0.16f
            moveTo(l, cuerpo.top); lineTo(l, cuerpo.top - hh * 0.12f)
            cubicTo(l, cuerpo.top - hh * 0.3f, rr, cuerpo.top - hh * 0.3f, rr, cuerpo.top - hh * 0.12f)
            lineTo(rr, cuerpo.top)
        }
        translate(1.4f * u, 1.6f * u) { drawPath(arco, a.sombra(0.4f), style = Stroke(w * 0.012f)) }
        drawPath(arco, en(P.ACERO_CLARO, 0f, cuerpo.center, a, cerca, frio), style = Stroke(w * 0.012f))
        drawPath(arco, a.destello(0.55f, k, fr), style = Stroke(1f * u))
        val cu = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(cuerpo.width * 0.16f))) }
        drawPath(cu, en(P.ACERO_MEDIO, 0f, cuerpo.center, a, cerca, frio))
        clipPath(cu) {
            drawRect(a.brillo(0.3f, k), cuerpo.topLeft, Size(cuerpo.width * 0.22f, cuerpo.height))
            drawRect(a.sombra(0.4f), Offset(cuerpo.right - cuerpo.width * 0.25f, cuerpo.top), Size(cuerpo.width * 0.25f, cuerpo.height))
            drawRect(a.sombra(0.25f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.15f), Size(cuerpo.width, cuerpo.height * 0.15f))
        }
        // El escudete de latón de la bocallave y la bocallave.
        val bc = Offset(cuerpo.center.x, cuerpo.top + cuerpo.height * 0.56f)
        drawCircle(en(P.LATON, 0f, bc, a, cerca, frio), cuerpo.width * 0.2f, bc)
        drawCircle(a.destello(0.5f, k, fr), cuerpo.width * 0.06f, bc - Offset(cuerpo.width * 0.08f, cuerpo.width * 0.08f))
        drawCircle(en(P.BETUN, 0f, bc, a, cerca, frio), cuerpo.width * 0.055f, bc - Offset(0f, cuerpo.width * 0.03f))
        drawRect(en(P.BETUN, 0f, bc, a, cerca, frio), Offset(bc.x - cuerpo.width * 0.025f, bc.y - cuerpo.width * 0.03f), Size(cuerpo.width * 0.05f, cuerpo.width * 0.13f))
        listOf(cuerpo.left + cuerpo.width * 0.2f, cuerpo.right - cuerpo.width * 0.2f).forEach { xx ->
            val p = Offset(xx, cuerpo.top + cuerpo.height * 0.16f)
            roblonOrden(p, 1.6f * u, a, cerca(p), P.ACERO_CLARO, frio(p))
        }
        // La llave tumbada delante: el anillo, la caña y el paletón.
        val ky = y - 1.6f * u
        val kx = x0 + w * 0.12f
        drawLine(a.sombra(0.4f), Offset(kx + 1f * u, ky + 1.2f * u), Offset(kx + w * 0.07f, ky + 1.2f * u), 2.2f * u)
        val hierro = en(mezcla(P.ACERO_HONDO, P.OXIDO, 0.2f), 0f, Offset(kx, ky), a, cerca, frio)
        drawOval(hierro, Offset(kx - w * 0.012f, ky - w * 0.007f), Size(w * 0.024f, w * 0.014f), style = Stroke(1.8f * u))
        drawLine(hierro, Offset(kx + w * 0.012f, ky), Offset(kx + w * 0.07f, ky), 2.2f * u)
        drawRect(hierro, Offset(kx + w * 0.058f, ky - w * 0.012f), Size(w * 0.012f, w * 0.012f))
        drawLine(a.destello(0.45f, cerca(Offset(kx, ky)), frio(Offset(kx, ky))), Offset(kx + w * 0.014f, ky - 0.8f * u), Offset(kx + w * 0.066f, ky - 0.8f * u), 0.6f * u)
    }

    // ---------------------------------------------------------------- la vela de media altura

    /** La palmatoria con su vela (la luz de este hueco), los dos manuales tumbados detrás y la lupa apoyada en ellos. */
    private fun DrawScope.vela(x0: Float, r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val hh = r.height
        val y = r.bottom - 2.5f * u
        // Los dos manuales tumbados, uno sobre otro.
        val m1 = Rect(x0 + w * 0.07f, y - hh * 0.13f, x0 + w * 0.2f, y)
        val m2 = Rect(x0 + w * 0.08f, m1.top - hh * 0.11f, x0 + w * 0.19f, m1.top)
        contactoOrden(Offset(m1.center.x, y), m1.width * 1.08f, 4f * u, a, 0.45f)
        listOf(m1 to P.CUERO_PARDO, m2 to P.GRANA_HONDA).forEach { (m, cuero) ->
            val c = en(cuero, 0f, m.center, a, cerca, frio)
            drawRect(c, m.topLeft, m.size)
            drawRect(mezcla(c, if (a.oscuro) P.LLAMA else P.LUZ_FRIA, if (a.oscuro) 0.25f * cerca(m.center) else 0.2f), m.topLeft, Size(m.width, m.height * 0.25f))
            drawRect(mezcla(c, P.NOCHE, 0.4f), Offset(m.left, m.bottom - m.height * 0.25f), Size(m.width, m.height * 0.25f))
            for (q in 1..2) {
                val xx = m.left + m.width * q / 3f
                drawRect(a.sombra(0.35f), Offset(xx + 1.2f * u, m.top), Size(1.2f * u, m.height))
                drawRect(mezcla(c, P.LUZ_FRIA, if (a.oscuro) 0.05f else 0.22f), Offset(xx - 1.2f * u, m.top), Size(2.2f * u, m.height))
            }
            listOf(m.left, m.right - w * 0.016f).forEach { xc ->
                drawRect(en(P.ACERO_MEDIO, 0f, m.center, a, cerca, frio), Offset(xc, m.top), Size(w * 0.016f, m.height))
                drawRect(a.destello(0.4f, cerca(m.center), frio(m.center)), Offset(xc, m.top), Size(w * 0.016f, 0.9f * u))
            }
        }
        // La lupa de latón apoyada en los manuales.
        val lc = Offset(x0 + w * 0.175f, m2.top - hh * 0.12f)
        val lr = hh * 0.13f
        rotate(18f, lc) {
            drawLine(en(P.CUERO, 0f, lc, a, cerca, frio), Offset(lc.x, lc.y + lr), Offset(lc.x, lc.y + lr + hh * 0.17f), w * 0.011f, StrokeCap.Round)
            drawLine(a.destello(0.3f, cerca(lc), frio(lc)), Offset(lc.x - w * 0.003f, lc.y + lr + 2f * u), Offset(lc.x - w * 0.003f, lc.y + lr + hh * 0.15f), 0.7f * u)
        }
        drawCircle(a.luz(P.VIDRIO, cerca(lc), frio(lc)).copy(alpha = 0.2f), lr, lc)
        drawCircle(en(P.LATON, 0f, lc, a, cerca, frio), lr, lc, style = Stroke(w * 0.006f))
        drawArc(a.brillo(0.7f, maxOf(0.3f, cerca(lc))), 200f, 70f, false, Offset(lc.x - lr * 0.7f, lc.y - lr * 0.7f), Size(lr * 1.4f, lr * 1.4f), style = Stroke(1.4f * u))
        // La palmatoria: el plato de acero con su asa de anillo y la vela.
        val cx = x0 + w * 0.055f
        val plato = Rect(cx - w * 0.04f, y - hh * 0.05f, cx + w * 0.04f, y)
        contactoOrden(Offset(cx, y), w * 0.1f, 4f * u, a, 0.45f)
        val k = cerca(Offset(cx, plato.top)); val fr = frio(Offset(cx, plato.top))
        drawCircle(en(P.ACERO_HONDO, 0f, plato.center, a, cerca, frio), w * 0.013f, Offset(plato.right + w * 0.006f, plato.center.y), style = Stroke(1.8f * u))
        drawOval(en(P.ACERO_MEDIO, 0f, plato.center, a, cerca, frio), Offset(plato.left, plato.top), Size(plato.width, plato.height * 0.6f))
        drawRect(en(P.ACERO_HONDO, 0f, plato.center, a, cerca, frio), Offset(plato.left + w * 0.006f, plato.top + plato.height * 0.3f), Size(plato.width - w * 0.012f, plato.height * 0.7f))
        drawOval(a.brillo(0.5f, maxOf(k, 0.2f)), Offset(plato.left + w * 0.006f, plato.top + 0.5f * u), Size(plato.width * 0.5f, plato.height * 0.25f))
        val topeVela = r.bottom - hh * M.VELA_HUECO_Y + w * 0.012f
        val vela = Rect(cx - w * 0.011f, topeVela, cx + w * 0.011f, plato.top + plato.height * 0.2f)
        val cera = en(P.CERA, 0f, vela.center, a, cerca, frio)
        drawRect(cera, vela.topLeft, vela.size)
        if (a.lumbre) drawRect(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(P.LLAMA.copy(alpha = (if (a.oscuro) 0.45f else 0.25f) * k), Color.Transparent), vela.top, vela.bottom), vela.topLeft, vela.size)
        else drawRect(P.LUZ_FRIA.copy(alpha = 0.4f), vela.topLeft, Size(vela.width * 0.3f, vela.height))
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.3f, vela.top), Size(vela.width * 0.3f, vela.height))
        listOf(0.2f to 0.5f, 0.75f to 0.3f).forEach { (fx, fl) ->
            val xx = vela.left + vela.width * fx
            drawLine(mezcla(cera, P.LUZ_FRIA, 0.2f), Offset(xx, vela.top + 0.6f * u), Offset(xx + 0.3f * u, vela.top + vela.height * fl), vela.width * 0.2f, StrokeCap.Round)
        }
        drawLine(en(P.BETUN, 0f, vela.center, a, cerca, frio), Offset(cx, vela.top + 0.6f * u), Offset(cx + w * 0.001f, vela.top - w * 0.006f), 1.1f * u, StrokeCap.Round)
        if (a.lumbre) with(CoronaEstanteOrden) { llama(Offset(cx, vela.top - w * 0.002f), w * (if (a.oscuro && !a.dormida) 0.0092f else 0.0075f), a) }
    }
}
