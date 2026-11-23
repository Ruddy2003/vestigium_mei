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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.ALMAGRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.GRANATE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HOJALATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que hay en los huecos libres de las repisas de Vapor (ver [MuebleEstanteVapor]), según la repisa: el silbato de locomotora
 * de latón, tumbado; la rueda de maqueta con su manivela, apoyada contra la pared, y una caja de cerillas; la pila de cuadernos
 * tumbados con el compás encima; y el secante de balancín con el tintero. Si el hueco es muy ancho, van dos.
 */
internal object HuecosEstanteVapor {

    fun DrawScope.huecoLibreVapor(k: Int, r: Rect, oc: Float, a: AireVapor, g: MarcoVapor, kk: Float, f: Float, br: Float) {
        val w = g.w
        val x0 = oc + w * 0.02f
        val x1 = g.x1 - w * 0.03f
        val libre = x1 - x0
        if (libre < w * 0.1f) return
        val pie = r.bottom - w * 0.006f
        val hacia = if (a.oscuro && (x0 + x1) / 2 < MuebleEstanteVapor.GAS_X * w) 1f else -1f
        val tipo = Math.floorMod(k * 3 + 1, 4)
        val ancho = minOf(libre, w * 0.2f)
        val c1 = if (libre > w * 0.42f) x0 + libre * 0.25f else x0 + libre * 0.5f
        pieza(tipo, c1, pie, ancho, r.height, a, kk, f, br, hacia)
        if (libre > w * 0.42f) pieza(Math.floorMod(tipo + 2, 4), x0 + libre * 0.72f, pie, ancho, r.height, a, g.cerca(Offset(x0 + libre * 0.72f, r.center.y)), f, br, hacia)
    }

    private fun DrawScope.pieza(tipo: Int, cx: Float, pie: Float, ancho: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        when (tipo) {
            0 -> silbato(cx, pie, ancho, a, k, f, br, hacia)
            1 -> rueda(cx, pie, alto, a, k, f, br, hacia)
            2 -> cuadernos(cx, pie, ancho, a, k, f, br, hacia)
            else -> secante(cx, pie, ancho, a, k, f, br, hacia)
        }
    }

    /** El silbato de locomotora, de latón, tumbado: la campana con su boca, el vástago, la válvula y la palanca. */
    private fun DrawScope.silbato(cx: Float, pie: Float, ancho: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val l = ancho * 0.85f
        val x0 = cx - l / 2
        val rr = l * 0.15f
        val yc = pie - rr
        contactoVapor(Offset(cx, pie), l * 1.05f, rr * 0.45f, a, 0.4f, -hacia)
        // La válvula (un cuerpo de latón) y la palanca con su bola.
        val val0 = Rect(x0 + l * 0.58f, yc - rr * 0.55f, x0 + l * 0.9f, yc + rr * 0.55f)
        drawRect(a.luz(LATON_OSC, k, f, br), val0.topLeft, val0.size)
        drawRect(a.brillo(0.4f, k, br), Offset(val0.left, val0.top + val0.height * 0.15f), Size(val0.width, val0.height * 0.15f))
        drawLine(a.luz(LATON, k, f, br), Offset(val0.right, yc - rr * 0.3f), Offset(x0 + l, yc - rr * 1.3f), 1.6f * u, StrokeCap.Round)
        drawCircle(a.luz(LATON_LUZ, k, f, br), rr * 0.22f, Offset(x0 + l, yc - rr * 1.3f))
        // El vástago y la campana.
        drawRect(a.luz(LATON, k, f, br), Offset(x0 + l * 0.42f, yc - rr * 0.25f), Size(l * 0.18f, rr * 0.5f))
        val camp = Rect(x0, yc - rr, x0 + l * 0.44f, yc + rr)
        drawRect(a.luz(LATON, k, f, br), camp.topLeft, camp.size)
        drawRect(a.luz(LATON_LUZ, k, f, br), Offset(camp.left, camp.top + camp.height * 0.15f), Size(camp.width, camp.height * 0.16f))
        drawRect(a.luz(LATON_OSC, k, f, br), Offset(camp.left, camp.top + camp.height * 0.65f), Size(camp.width, camp.height * 0.35f))
        drawRect(Color.White.copy(alpha = if (a.oscuro) 0.5f * k else 0.6f), Offset(camp.left + camp.width * 0.2f, camp.top + camp.height * 0.2f), Size(camp.width * 0.5f, 1f * u))
        // La boca de la campana y la ranura por donde silba.
        drawOval(a.luz(NEGRO, k, f, br), Offset(camp.left - rr * 0.2f, camp.top), Size(rr * 0.4f, camp.height))
        drawOval(a.luz(LATON, k, f, br), Offset(camp.left - rr * 0.2f, camp.top), Size(rr * 0.4f, camp.height), style = Stroke(1.2f * u))
        drawRect(a.luz(NEGRO, k, f, br), Offset(camp.right - l * 0.02f, yc - rr * 0.6f), Size(l * 0.025f, rr * 1.2f))
        if (a.oscuro && k > 0.08f) drawRect(a.brillo(0.6f, k, br), Offset(camp.left, camp.top), Size(camp.width, 1f * u))
    }

    /** La rueda de maqueta, de radios, apoyada de canto contra la pared, con su manivela; y la caja de cerillas delante. */
    private fun DrawScope.rueda(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val r = alto * 0.3f
        val c = Offset(cx, pie - r)
        rotate(-8f * hacia, Offset(cx, pie)) {
            // Su sombra en la pared, corrida.
            drawCircle(a.sombra(0.35f), r, c + Offset(-hacia * r * 0.3f, -r * 0.05f))
            drawCircle(a.luz(Color(0xFF1A1618), k, f, br), r * 0.86f, c)
            val ray = Path()
            for (i in 0 until 10) {
                val t = 0.3f + i * (2f * Math.PI.toFloat() / 10)
                ray.moveTo(c.x + r * 0.22f * cos(t), c.y + r * 0.22f * sin(t)); ray.lineTo(c.x + r * 0.82f * cos(t), c.y + r * 0.82f * sin(t))
            }
            drawPath(ray, a.luz(GRANATE, k, f, br), style = Stroke(r * 0.1f))
            drawCircle(a.luz(GRANATE, k, f, br), r * 0.84f, c, style = Stroke(r * 0.1f))
            drawCircle(a.luz(HIERRO_LUZ, k, f, br), r * 0.95f, c, style = Stroke(r * 0.12f))
            drawArc(a.brillo(0.65f, maxOf(k, 0.3f), br), if (hacia < 0) 150f else -30f, 110f, false, Offset(c.x - r, c.y - r), Size(r * 2f, r * 2f), style = Stroke(1f * u))
            drawCircle(a.luz(GRANATE, k, f, br), r * 0.26f, c)
            drawCircle(a.luz(HOJALATA, k, f, br), r * 0.12f, c)
            // La manivela y su muñón.
            val m = Offset(c.x + r * 0.5f, c.y - r * 0.3f)
            drawLine(a.luz(HIERRO_LUZ, k, f, br), c, m, r * 0.16f, StrokeCap.Round)
            drawCircle(a.luz(LATON, k, f, br), r * 0.1f, m)
        }
        contactoVapor(Offset(cx, pie), r * 1.4f, r * 0.2f, a, 0.4f, -hacia)
        // La caja de cerillas, de cartón con su raspador.
        val cj = Rect(cx + r * 0.55f, pie - r * 0.34f, cx + r * 1.35f, pie)
        drawRect(a.sombra(0.3f), Offset(cj.left - hacia * r * 0.1f, cj.top + r * 0.05f), cj.size)
        drawRect(a.luz(mezcla(ALMAGRE, PAPEL_VIEJO, 0.25f), k, f, br), cj.topLeft, cj.size)
        drawRect(a.luz(Color(0xFF4A3426), k, f, br), Offset(cj.left, cj.top + cj.height * 0.35f), Size(cj.width, cj.height * 0.3f))
        drawRect(a.brillo(0.45f, k, br), cj.topLeft, Size(cj.width, 0.9f * u))
    }

    /** La pila de cuadernos tumbados (tapas de distintos colores, el canto de las hojas) y el compás abierto encima. */
    private fun DrawScope.cuadernos(cx: Float, pie: Float, ancho: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val l = ancho * 0.82f
        val tapas = listOf(Color(0xFF2E4566), ALMAGRE, VERDE, Color(0xFF5A3A22))
        var y = pie
        contactoVapor(Offset(cx, pie), l * 1.1f, l * 0.06f, a, 0.4f, -hacia)
        tapas.forEachIndexed { i, col ->
            val grueso = l * (0.07f + 0.015f * (i % 2))
            val dx = l * 0.04f * (if (i % 2 == 0) 1f else -1f)
            val b = Rect(cx - l / 2 + dx, y - grueso, cx + l / 2 + dx, y)
            drawRect(a.sombra(0.3f), Offset(b.left, b.bottom - 1f * u), Size(b.width, 1f * u))
            drawRect(a.luz(col, k, f, br), b.topLeft, b.size)
            val hojas = Rect(b.left + l * 0.02f, b.top + grueso * 0.25f, b.right - l * 0.01f, b.bottom - grueso * 0.25f)
            drawRect(a.luz(PAPEL_VIEJO, k, f, br), hojas.topLeft, hojas.size)
            drawRect(a.brillo(0.4f, k, br), b.topLeft, Size(b.width, 0.8f * u))
            y -= grueso
        }
        // El compás abierto encima, con sus dos puntas de acero y la cabeza de latón.
        val cab = Offset(cx - l * 0.05f, y - l * 0.03f)
        drawLine(a.luz(HOJALATA, k, f, br), cab, Offset(cx - l * 0.4f, y - 0.5f * u), 1.4f * u, StrokeCap.Round)
        drawLine(a.luz(HOJALATA, k, f, br), cab, Offset(cx + l * 0.3f, y - 0.5f * u), 1.4f * u, StrokeCap.Round)
        drawCircle(a.luz(LATON, k, f, br), 2f * u, cab)
        drawCircle(a.brillo(0.6f, k, br), 0.8f * u, cab - Offset(0.5f * u, 0.5f * u))
    }

    /** El secante de balancín (madera oscura, el papel rosa y el pomo de latón) y el tintero redondo de cristal. */
    private fun DrawScope.secante(cx: Float, pie: Float, ancho: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val l = ancho * 0.6f
        val x0 = cx - ancho * 0.42f
        contactoVapor(Offset(x0 + l / 2, pie), l * 1.1f, l * 0.08f, a, 0.4f, -hacia)
        val curva = Path().apply {
            moveTo(x0, pie - l * 0.12f); quadraticTo(x0 + l / 2, pie + l * 0.08f, x0 + l, pie - l * 0.12f)
            lineTo(x0 + l, pie - l * 0.2f); quadraticTo(x0 + l / 2, pie - l * 0.02f, x0, pie - l * 0.2f); close()
        }
        drawPath(curva, a.luz(Color(0xFFD9A3A0), k, f, br))
        val lomo = Path().apply {
            moveTo(x0 + l * 0.05f, pie - l * 0.2f); quadraticTo(x0 + l / 2, pie - l * 0.04f, x0 + l * 0.95f, pie - l * 0.2f)
            lineTo(x0 + l * 0.95f, pie - l * 0.3f); quadraticTo(x0 + l / 2, pie - l * 0.14f, x0 + l * 0.05f, pie - l * 0.3f); close()
        }
        drawPath(lomo, a.luz(NOGAL, k, f, br))
        drawPath(lomo, a.brillo(0.35f, k, br), style = Stroke(0.7f * u))
        val pomo = Offset(x0 + l / 2, pie - l * 0.2f)
        drawLine(a.luz(LATON_OSC, k, f, br), pomo, pomo - Offset(0f, l * 0.14f), l * 0.05f)
        drawCircle(a.luz(LATON, k, f, br), l * 0.07f, pomo - Offset(0f, l * 0.18f))
        drawCircle(a.brillo(0.6f, k, br), l * 0.025f, pomo - Offset(l * 0.02f, l * 0.2f))
        // El tintero redondo de cristal con su tapón.
        val tc = Offset(cx + ancho * 0.28f, pie)
        val rt = ancho * 0.13f
        contactoVapor(tc, rt * 2.6f, rt * 0.4f, a, 0.4f, -hacia)
        val vaso = Path().apply { addOval(Rect(tc.x - rt, tc.y - rt * 1.6f, tc.x + rt, tc.y)) }
        drawPath(vaso, a.luz(Color(0xFF9CB0AE), k, f, br).copy(alpha = 0.9f))
        clipPath(vaso) { drawRect(a.luz(Color(0xFF141420), k, f, br), Offset(tc.x - rt, tc.y - rt * 0.8f), Size(rt * 2, rt)) }
        drawOval(a.brillo(0.75f, maxOf(k, 0.3f), br), Offset(tc.x - rt * 0.6f, tc.y - rt * 1.3f), Size(rt * 0.3f, rt * 0.7f))
        drawRect(a.luz(HIERRO, k, f, br), Offset(tc.x - rt * 0.35f, tc.y - rt * 2f), Size(rt * 0.7f, rt * 0.45f))
    }
}
