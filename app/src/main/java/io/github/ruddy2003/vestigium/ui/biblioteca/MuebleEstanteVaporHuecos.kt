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
 * Lo que hay en los huecos libres de las repisas de Vapor (ver [MuebleEstanteVapor]), según la repisa y en grupos que llenan de
 * 70 a 90 % del alto del hueco: el silbato de locomotora de latón, de pie sobre su válvula; la rueda de maqueta con su manivela,
 * apoyada contra la pared, y una caja de cerillas; la pila de cuadernos tumbados con el compás encima; el secante de balancín con
 * el tintero; la prensa de fechar billetes de la taquilla; y las de [CosasEstanteVapor]. Si el hueco es ancho, van dos grupos.
 */
internal object HuecosEstanteVapor {

    fun DrawScope.huecoLibreVapor(k: Int, r: Rect, oc: Float, a: AireVapor, g: MarcoVapor, f: Float) {
        val w = g.w
        val x0 = oc + w * 0.02f
        val x1 = g.x1 - w * 0.035f
        val libre = x1 - x0
        if (libre < w * 0.08f) return
        val pie = r.bottom - w * 0.006f
        val hacia = if (a.oscuro && (x0 + x1) / 2 < MuebleEstanteVapor.GAS_X * w) 1f else -1f
        val tipo = Math.floorMod(k * 3 + 1, 5)
        fun luz(x: Float) = Offset(x, r.center.y)
        if (libre < w * 0.17f) {
            // Un hueco chico: una sola cosa alta.
            val c = x0 + libre * 0.5f
            if (tipo % 2 == 0) with(CosasEstanteVapor) { rollos(c, pie, r.height, a, g.cerca(luz(c)), f, g.brasa(luz(c)), hacia) }
            else silbato(c, pie, r.height, a, g.cerca(luz(c)), f, g.brasa(luz(c)), hacia)
            return
        }
        if (libre < w * 0.5f) { grupo(tipo, x0 + libre * 0.5f, pie, minOf(libre, w * 0.3f), r.height, a, g, f, hacia); return }
        grupo(tipo, x0 + libre * 0.28f, pie, minOf(libre * 0.48f, w * 0.3f), r.height, a, g, f, hacia)
        grupo(Math.floorMod(tipo + 2, 5), x0 + libre * 0.76f, pie, minOf(libre * 0.44f, w * 0.28f), r.height, a, g, f, hacia)
    }

    /**
     * Un grupo de cosas juntas, de 70 a 90 % del alto del hueco, una delante de otra: los planos enrollados y el manómetro en su
     * caja; la rueda de maqueta y la aceitera; la pila de guías con el silbato encima; la prensa de fechar con el secante; los
     * cuadernos con el compás y la aceitera detrás.
     */
    private fun DrawScope.grupo(tipo: Int, cx: Float, pie: Float, ancho: Float, alto: Float, a: AireVapor, g: MarcoVapor, f: Float, hacia: Float) {
        fun k(x: Float) = g.cerca(Offset(x, pie - alto * 0.4f))
        fun br(x: Float) = g.brasa(Offset(x, pie - alto * 0.4f))
        val izq = cx - ancho * 0.22f; val der = cx + ancho * 0.24f
        with(CosasEstanteVapor) {
            when (tipo) {
                0 -> { rollos(izq, pie, alto, a, k(izq), f, br(izq), hacia); manometro(der, pie, alto, a, k(der), f, br(der), hacia) }
                1 -> { rueda(izq, pie, alto, a, k(izq), f, br(izq), hacia); aceitera(der + ancho * 0.06f, pie, alto, a, k(der), f, br(der), hacia) }
                2 -> {
                    val arriba = guias(cx, pie, ancho, alto, a, k(cx), f, br(cx), hacia)
                    silbato(cx + ancho * 0.08f, arriba, alto * 0.75f, a, k(cx), f, br(cx), hacia)
                }
                3 -> { fechadora(izq + ancho * 0.05f, pie, alto, a, k(izq), f, br(izq), hacia); secante(der, pie, ancho * 0.55f, a, k(der), f, br(der), hacia) }
                else -> { aceitera(der, pie, alto, a, k(der), f, br(der), hacia); cuadernos(izq + ancho * 0.05f, pie, ancho * 0.75f, a, k(izq), f, br(izq), hacia) }
            }
        }
    }

    /**
     * El silbato de locomotora, de latón, de pie sobre su válvula: el cuerpo de la válvula, la palanca con su bola, el vástago y
     * la campana con su aro y la boca por donde silba; el brillo del latón pulido.
     */
    private fun DrawScope.silbato(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val h = alto * 0.72f
        val rw = h * 0.22f
        val kk = maxOf(k, 0.3f)
        contactoVapor(Offset(cx, pie), rw * 3.2f, rw * 0.45f, a, 0.42f, -hacia)
        // Su sombra en la pared del fondo.
        drawRect(a.sombra(0.3f), Offset(cx - rw * 0.6f - hacia * rw * 0.5f, pie - h * 0.98f), Size(rw * 1.2f, h * 0.7f))
        // La válvula, con su tuerca abajo.
        val val0 = Rect(cx - rw * 0.75f, pie - h * 0.26f, cx + rw * 0.75f, pie)
        drawRect(a.luz(LATON_OSC, k, f, br), val0.topLeft, val0.size)
        drawRect(a.luz(LATON, k, f, br), Offset(val0.left + val0.width * 0.2f, val0.top), Size(val0.width * 0.35f, val0.height))
        drawRect(a.brillo(0.5f, kk, br), Offset(val0.left + val0.width * 0.28f, val0.top), Size(val0.width * 0.08f, val0.height))
        drawRect(a.sombra(0.35f), Offset(val0.left, val0.top), Size(val0.width, 1.2f * u))
        // La palanca, de lado, con su bola.
        drawLine(a.luz(LATON, k, f, br), Offset(val0.right, val0.top + val0.height * 0.3f), Offset(val0.right + rw * 1.2f, val0.top - h * 0.12f), 1.8f * u, StrokeCap.Round)
        drawCircle(a.luz(LATON_LUZ, k, f, br), rw * 0.22f, Offset(val0.right + rw * 1.2f, val0.top - h * 0.12f))
        // El vástago y la campana.
        drawRect(a.luz(LATON, k, f, br), Offset(cx - rw * 0.2f, pie - h * 0.38f), Size(rw * 0.4f, h * 0.14f))
        val camp = Rect(cx - rw * 0.62f, pie - h, cx + rw * 0.62f, pie - h * 0.36f)
        drawRect(a.luz(LATON, k, f, br), camp.topLeft, camp.size)
        val claro = if (hacia < 0) camp.left + camp.width * 0.15f else camp.right - camp.width * 0.4f
        drawRect(a.luz(LATON_LUZ, k, f, br), Offset(claro, camp.top), Size(camp.width * 0.22f, camp.height))
        drawRect(Color.White.copy(alpha = if (a.oscuro) 0.55f * k else 0.65f), Offset(claro + camp.width * 0.06f, camp.top + 1f * u), Size(1.2f * u, camp.height * 0.8f))
        drawRect(a.luz(LATON_OSC, k, f, br), Offset(if (hacia < 0) camp.right - camp.width * 0.3f else camp.left, camp.top), Size(camp.width * 0.3f, camp.height))
        drawRect(a.luz(LATON_OSC, k, f, br), Offset(camp.left - 0.8f * u, camp.top), Size(camp.width + 1.6f * u, h * 0.06f))
        drawRect(a.brillo(0.6f, kk, br), Offset(camp.left - 0.8f * u, camp.top), Size(camp.width + 1.6f * u, 0.8f * u))
        // La boca: la ranura oscura abajo de la campana.
        drawRect(a.luz(NEGRO, k, f, br), Offset(camp.left, camp.bottom - h * 0.05f), Size(camp.width, h * 0.05f))
        if (a.oscuro && k > 0.08f) drawRect(a.brillo(0.7f, k, br), Offset(if (hacia > 0) camp.right - 1.2f * u else camp.left, camp.top), Size(1.2f * u, camp.height))
    }

    /**
     * La prensa de fechar billetes de la taquilla, de hierro fundido pintado de negro con su filete dorado: la base, el cuerpo con
     * la boca por donde entra el billete, la palanca de latón con su pomo de madera. Sin letras.
     */
    private fun DrawScope.fechadora(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val h = alto * 0.62f
        val bw = h * 0.9f
        val kk = maxOf(k, 0.3f)
        contactoVapor(Offset(cx, pie), bw * 1.4f, h * 0.08f, a, 0.42f, -hacia)
        val base = Rect(cx - bw / 2, pie - h * 0.16f, cx + bw / 2, pie)
        val cuerpo = poligonoVapor(cx - bw * 0.36f, base.top, cx - bw * 0.26f, pie - h * 0.86f, cx + bw * 0.2f, pie - h * 0.86f, cx + bw * 0.34f, base.top)
        drawPath(corridoVapor(cuerpo, -hacia * bw * 0.2f, -h * 0.02f), a.sombra(0.3f))
        drawRect(a.luz(HIERRO, k, f, br), base.topLeft, base.size)
        drawRect(a.brillo(0.35f, kk, br), base.topLeft, Size(base.width, 0.9f * u))
        drawPath(cuerpo, a.luz(Color(0xFF1E1C22), k, f, br))
        clipPath(cuerpo) { drawRect(a.brillo(0.2f, kk, br), Offset(cx - bw * (if (hacia < 0) 0.28f else -0.1f), pie - h), Size(bw * 0.14f, h)) }
        val oro = a.luz(PaletaVapor.FILETE, maxOf(k, 0.2f), f, br)
        drawPath(poligonoVapor(cx - bw * 0.3f, base.top - h * 0.04f, cx - bw * 0.23f, pie - h * 0.8f, cx + bw * 0.17f, pie - h * 0.8f, cx + bw * 0.28f, base.top - h * 0.04f), oro, style = Stroke(0.6f * u))
        // La boca donde entra el billete.
        drawRect(a.luz(NEGRO, k, f, br), Offset(cx - bw * 0.16f, pie - h * 0.36f), Size(bw * 0.3f, h * 0.06f))
        drawRect(a.luz(LATON, k, f, br), Offset(cx - bw * 0.18f, pie - h * 0.3f), Size(bw * 0.34f, h * 0.025f))
        // La palanca de latón que sube y su pomo de madera.
        val eje = Offset(cx + bw * 0.12f, pie - h * 0.8f)
        val pomo = Offset(cx + bw * 0.55f, pie - h * 1.05f)
        drawLine(a.luz(LATON_OSC, k, f, br), eje, pomo, 2.2f * u, StrokeCap.Round)
        drawLine(a.brillo(0.5f, kk, br), eje - Offset(0f, 0.8f * u), pomo - Offset(0f, 0.8f * u), 0.7f * u)
        drawOval(a.luz(NOGAL, k, f, br), Offset(pomo.x - h * 0.06f, pomo.y - h * 0.1f), Size(h * 0.12f, h * 0.2f))
        drawCircle(a.luz(LATON, k, f, br), h * 0.05f, eje)
    }

    /** La rueda de maqueta, de radios, apoyada de canto contra la pared, con su manivela; y la caja de cerillas delante. */
    private fun DrawScope.rueda(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val r = alto * 0.4f
        val c = Offset(cx, pie - r)
        rotate(-8f * hacia, Offset(cx, pie)) {
            // Su sombra en la pared, corrida.
            drawCircle(a.sombra(0.22f), r, c + Offset(-hacia * r * 0.3f, -r * 0.05f))
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
