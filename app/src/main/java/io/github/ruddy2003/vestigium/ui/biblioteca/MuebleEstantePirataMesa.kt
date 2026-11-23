package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
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
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La mitad derecha del bodegón de encima del estante de Pirata ([CoronaEstantePirata]): los dos libros de cuentas tumbados con
 * la pistola de chispa encima, el tintero de barro con su pluma, el carretel de la corredera con su barquilla y el candelero
 * de cardán en su brazo de hierro, que de noche arde.
 */
internal object MesaEstantePirata {
    private val P = PaletaPirata

    /**
     * Los dos libros de cuentas tumbados uno sobre otro (el de abajo en piel roja, el de arriba en vitela), con sus lomos y sus
     * nervios hacia quien mira, y encima la pistola de chispa.
     */
    fun DrawScope.libros(a: AirePirata, pie: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(pie - Offset(0f, w * 0.04f))
        val ha = haciaPirata(pie.x, a)
        contactoPirata(Offset(pie.x, pie.y - 1f * u), w * 0.22f, 6f * u, a, hacia = -ha)
        val tomos = listOf(Triple(w * 0.2f, w * 0.036f, Color(0xFF7A2A20)), Triple(w * 0.17f, w * 0.03f, Color(0xFFD8C9A2)))
        var y = pie.y
        tomos.forEachIndexed { i, (l, g, piel) ->
            val x = pie.x - l / 2 + (if (i == 1) w * 0.008f else 0f)
            val r = Rect(x, y - g, x + l, y)
            if (i == 1) drawRect(a.sombra(0.35f), Offset(r.left, r.bottom), Size(r.width, 2f * u))
            drawRoundRect(a.luz(piel, k), r.topLeft, r.size, CornerRadius(g * 0.3f))
            drawRect(a.brillo(0.3f, k), Offset(r.left + g * 0.2f, r.top + g * 0.12f), Size(r.width - g * 0.4f, g * 0.22f))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - g * 0.3f), Size(r.width, g * 0.3f))
            for (j in 1..4) {
                val nx = r.left + r.width * j / 5f
                drawRect(a.luz(mezcla(piel, P.TINTA, 0.35f), k), Offset(nx - 1f * u, r.top), Size(2f * u, r.height))
                drawRect(a.brillo(0.3f, k), Offset(nx - 1.4f * u, r.top), Size(0.7f * u, r.height))
            }
            drawRect(a.dia(P.PAPEL, 0f, k), Offset(r.left + r.width * 0.42f, r.top + g * 0.25f), Size(r.width * 0.16f, g * 0.5f))
            y -= g
        }
        pistola(a, Offset(pie.x - w * 0.11f, y + 1f * u), w, k)
    }

    /**
     * La pistola de chispa tumbada de lado: la caja de nogal con su culata curva y su cantonera de latón, el cañón de hierro,
     * la llave con su pie de gato y el rastrillo, el guardamonte de latón y la baqueta bajo el cañón.
     */
    private fun DrawScope.pistola(a: AirePirata, pie: Offset, w: Float, k: Float) {
        val u = density
        val nogal = Color(0xFF5A3420)
        rotate(-4f, pie) {
            val l = w * 0.23f
            val y = pie.y - w * 0.016f
            val x0 = pie.x
            contactoPirata(Offset(x0 + l * 0.5f, pie.y - 0.5f * u), l * 0.95f, 4f * u, a)
            // El cañón y su boca.
            val canon = Rect(x0 + l * 0.36f, y - w * 0.01f, x0 + l, y + w * 0.005f)
            drawRoundRect(a.luz(P.HIERRO, k), canon.topLeft, canon.size, CornerRadius(1.5f * u))
            drawRect(a.brillo(0.5f, k), Offset(canon.left, canon.top + canon.height * 0.15f), Size(canon.width, canon.height * 0.2f))
            drawRect(a.luz(P.LATON, k), Offset(canon.right - w * 0.01f, canon.top - 0.6f * u), Size(w * 0.007f, canon.height + 1.2f * u))
            // La caja de nogal con su culata.
            val caja = Path().apply {
                moveTo(x0 + l * 0.82f, y + w * 0.005f)
                lineTo(x0 + l * 0.36f, y + w * 0.007f)
                lineTo(x0 + l * 0.28f, y - w * 0.008f)
                cubicTo(x0 + l * 0.17f, y - w * 0.006f, x0 + l * 0.06f, y + w * 0.008f, x0, y + w * 0.03f)
                lineTo(x0 + l * 0.055f, y + w * 0.046f)
                cubicTo(x0 + l * 0.14f, y + w * 0.034f, x0 + l * 0.22f, y + w * 0.019f, x0 + l * 0.34f, y + w * 0.016f)
                lineTo(x0 + l * 0.82f, y + w * 0.012f)
                close()
            }
            drawPath(caja, a.luz(nogal, k))
            clipPath(caja) {
                drawRect(a.brillo(0.3f, k), Offset(x0, y - w * 0.012f), Size(l, w * 0.009f))
                drawRect(a.sombra(0.35f), Offset(x0, y + w * 0.018f), Size(l, w * 0.04f))
            }
            drawLine(a.luz(Color(0xFF3A2A1E), k), Offset(x0 + l * 0.4f, y + w * 0.01f), Offset(x0 + l * 0.9f, y + w * 0.008f), 1.3f * u)
            // La cantonera de latón de la culata.
            drawLine(a.luz(P.LATON, k), Offset(x0 + l * 0.004f, y + w * 0.031f), Offset(x0 + l * 0.052f, y + w * 0.047f), w * 0.009f, StrokeCap.Round)
            // La llave: la pletina, el pie de gato y el rastrillo.
            val ll = Offset(x0 + l * 0.32f, y + w * 0.002f)
            drawRoundRect(a.luz(P.HIERRO_LUZ, k), Offset(ll.x - w * 0.02f, ll.y - w * 0.005f), Size(w * 0.04f, w * 0.012f), CornerRadius(2f * u))
            val gato = Path().apply { moveTo(ll.x - w * 0.013f, ll.y - w * 0.004f); quadraticTo(ll.x - w * 0.025f, ll.y - w * 0.023f, ll.x - w * 0.009f, ll.y - w * 0.025f); lineTo(ll.x - w * 0.004f, ll.y - w * 0.018f); quadraticTo(ll.x - w * 0.013f, ll.y - w * 0.014f, ll.x - w * 0.006f, ll.y - w * 0.004f); close() }
            drawPath(gato, a.luz(P.HIERRO, k))
            drawLine(a.luz(P.HIERRO, k), Offset(ll.x + w * 0.007f, ll.y - w * 0.005f), Offset(ll.x + w * 0.01f, ll.y - w * 0.018f), 1.8f * u)
            destelloPirata(Offset(ll.x - w * 0.007f, ll.y - w * 0.001f), 1.1f * u, a, k)
            // El guardamonte de latón y el gatillo.
            drawArc(a.luz(P.LATON, k), 0f, 180f, false, Offset(ll.x - w * 0.013f, y + w * 0.005f), Size(w * 0.034f, w * 0.026f), style = Stroke(1.6f * u))
            drawLine(a.luz(P.HIERRO, k), Offset(ll.x + w * 0.002f, y + w * 0.01f), Offset(ll.x, y + w * 0.019f), 1.3f * u)
        }
    }

    /** El tintero de barro vidriado de verde, con su pluma de ave metida y el plumín manchado. */
    fun DrawScope.tintero(a: AirePirata, pie: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val an = w * 0.052f; val al = w * 0.04f
        val k = cerca(pie - Offset(0f, al))
        val ha = haciaPirata(pie.x, a)
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 1.5f, 5f * u, a, hacia = -ha)
        // La pluma: el cañón y las barbas, inclinada hacia la izquierda.
        val base = Offset(pie.x - an * 0.05f, pie.y - al * 0.95f)
        val punta = base + Offset(-w * 0.045f, -w * 0.11f)
        val pluma = Path().apply {
            moveTo(base.x, base.y - w * 0.02f)
            quadraticTo(base.x - w * 0.022f, base.y - w * 0.07f, punta.x, punta.y)
            quadraticTo(base.x - w * 0.002f, base.y - w * 0.075f, base.x + w * 0.006f, base.y - w * 0.035f)
            close()
        }
        sombraEnParedPirata(Path().apply { addPath(pluma) }, base, w * 0.012f, a, k, 0.22f)
        drawLine(a.luz(Color(0xFFCFC6B0), k), base, base + Offset(-w * 0.004f, -w * 0.025f), 1.1f * u)
        drawPath(pluma, a.dia(Color(0xFFF2EEE4), 0f, k))
        val barbas = Path()
        for (j in 1 until 8) {
            val f = j / 8f
            val p = Offset(base.x + (punta.x - base.x) * f - w * 0.006f * (1 - f), base.y - w * 0.02f + (punta.y - base.y + w * 0.02f) * f)
            barbas.moveTo(p.x, p.y); barbas.lineTo(p.x + w * 0.011f, p.y + w * 0.004f)
        }
        drawPath(barbas, a.sombra(0.22f), style = Stroke(0.5f * u))
        drawLine(a.luz(Color(0xFFBFB49A), k), base + Offset(0f, -w * 0.02f), punta, 0.9f * u)
        // El cuerpo del tintero.
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.4f, pie.y - al * 0.78f)
            cubicTo(pie.x - an * 0.62f, pie.y - al * 0.4f, pie.x - an * 0.55f, pie.y, pie.x, pie.y)
            cubicTo(pie.x + an * 0.55f, pie.y, pie.x + an * 0.62f, pie.y - al * 0.4f, pie.x + an * 0.4f, pie.y - al * 0.78f)
            close()
        }
        drawPath(cuerpo, a.luz(P.VIDRIADO, k))
        clipPath(cuerpo) {
            drawRect(a.brillo(0.32f, k), Offset(if (ha < 0f) pie.x - an * 0.5f else pie.x + an * 0.15f, pie.y - al), Size(an * 0.3f, al))
            drawRect(a.sombra(0.36f), Offset(if (ha < 0f) pie.x + an * 0.1f else pie.x - an * 0.6f, pie.y - al), Size(an * 0.5f, al))
            drawRect(a.luz(P.BARRO, k), Offset(pie.x - an, pie.y - al * 0.16f), Size(an * 2, al * 0.2f))
        }
        drawRoundRect(a.luz(mezcla(P.VIDRIADO, P.VERDE_HONDO, 0.4f), k), Offset(pie.x - an * 0.3f, pie.y - al), Size(an * 0.6f, al * 0.28f), CornerRadius(2f * u))
        drawOval(a.luz(P.TINTA, k), Offset(pie.x - an * 0.22f, pie.y - al * 1.03f), Size(an * 0.44f, al * 0.14f))
        destelloPirata(Offset(pie.x + ha * an * 0.28f, pie.y - al * 0.55f), 1.3f * u, a, k)
    }

    /**
     * El carretel de la corredera: el carrete de madera con el cordel enrollado entre sus dos platos, el eje que asoma por los
     * lados, la barquilla (la tabla en cuarto de círculo con su canto de plomo) apoyada delante, y el cordel con sus nudos que
     * cuelga por el canto de la cornisa.
     */
    fun DrawScope.carretel(a: AirePirata, pie: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val r = w * 0.04f
        val c = pie - Offset(0f, r + w * 0.004f)
        val k = cerca(c)
        val ha = haciaPirata(c.x, a)
        val l = w * 0.042f
        contactoPirata(Offset(c.x, pie.y - 1f * u), l * 2.6f, 5f * u, a, hacia = -ha)
        // El eje y su mango.
        drawLine(a.luz(P.ROBLE_OSC, k), Offset(c.x - l - w * 0.018f, c.y), Offset(c.x + l + w * 0.03f, c.y), w * 0.009f, StrokeCap.Round)
        // El cordel enrollado.
        val rollo = Rect(c.x - l, c.y - r * 0.74f, c.x + l, c.y + r * 0.74f)
        drawRect(a.luz(P.CABO, k), rollo.topLeft, rollo.size)
        val vueltas = Path()
        var y = rollo.top + 1.5f * u
        while (y < rollo.bottom) { vueltas.moveTo(rollo.left, y); vueltas.lineTo(rollo.right, y - 1.2f * u); y += 2.2f * u }
        drawPath(vueltas, a.luz(P.CABO_OSC, k).copy(alpha = 0.75f), style = Stroke(0.7f * u))
        drawRect(a.brillo(0.32f, k), Offset(rollo.left, rollo.top + rollo.height * 0.12f), Size(rollo.width, rollo.height * 0.18f))
        drawRect(a.sombra(0.32f), Offset(rollo.left, rollo.bottom - rollo.height * 0.32f), Size(rollo.width, rollo.height * 0.32f))
        // Los dos platos.
        listOf(c.x - l, c.x + l).forEach { x ->
            drawOval(a.luz(P.ROBLE_OSC, k), Offset(x - r * 0.24f, c.y - r), Size(r * 0.48f, r * 2))
            drawOval(a.luz(P.ROBLE, k), Offset(x - r * 0.17f, c.y - r * 0.96f), Size(r * 0.34f, r * 1.92f))
            drawLine(a.brillo(0.4f, k), Offset(x - r * 0.07f, c.y - r * 0.8f), Offset(x - r * 0.07f, c.y + r * 0.3f), 1.1f * u)
        }
        // El cordel que cuelga por el canto, con sus nudos.
        val cae = Path().apply { moveTo(c.x - l * 0.5f, c.y + r * 0.6f); cubicTo(c.x - l * 0.7f, pie.y + w * 0.01f, c.x - l * 1.0f, pie.y + w * 0.03f, c.x - l * 0.9f, pie.y + w * 0.08f) }
        drawPath(cae, a.luz(P.CABO_OSC, k), style = Stroke(1.8f * u))
        drawPath(cae, a.luz(P.CABO, k), style = Stroke(1f * u))
        listOf(0.45f, 0.85f).forEach { f ->
            val p = Offset(c.x - l * (0.55f + 0.4f * f), pie.y + w * 0.08f * f - w * 0.004f)
            drawCircle(a.luz(P.CABO_OSC, k), 2.2f * u, p); drawCircle(a.luz(P.CABO, k), 1.3f * u, p - Offset(0.4f * u, 0.4f * u))
        }
        // La barquilla, apoyada delante: un cuarto de círculo de tabla con su canto de plomo.
        val b0 = Offset(c.x + l * 0.2f, pie.y - 1f * u)
        val br = w * 0.04f
        val barquilla = Path().apply { moveTo(b0.x, b0.y); lineTo(b0.x, b0.y - br); arcTo(Rect(b0.x - br, b0.y - br, b0.x + br, b0.y + br), -90f, 90f, false); close() }
        drawPath(barquilla, a.luz(P.ROBLE_LUZ, k))
        drawPath(poligonoPirata(b0.x, b0.y, b0.x, b0.y - br * 0.3f, b0.x + br, b0.y), a.sombra(0.2f))
        drawArc(a.luz(Color(0xFF5E6066), k), -90f, 90f, false, Offset(b0.x - br, b0.y - br), Size(br * 2, br * 2), style = Stroke(2.6f * u))
        drawLine(a.brillo(0.35f, k), Offset(b0.x + 1f * u, b0.y - br + 2f * u), Offset(b0.x + 1f * u, b0.y - 2f * u), 1.1f * u)
    }

    /**
     * El candelero de cardán: el brazo de hierro clavado al mamparo, los aros de latón que dejan la vela siempre derecha con el
     * balance, la copa con su contrapeso, la vela de sebo y el guardabrisa de vidrio; de día apagada, de noche con su llama.
     */
    fun DrawScope.candelero(a: AirePirata, w: Float, cerca: (Offset) -> Float) {
        val l0 = Offset(w * MuebleEstantePirata.LAMP_X, w * MuebleEstantePirata.LAMP_Y)
        scale(1.28f, l0) { candeleroEnSuSitio(a, w, cerca) }
    }

    private fun DrawScope.candeleroEnSuSitio(a: AirePirata, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val l = Offset(w * MuebleEstantePirata.LAMP_X, w * MuebleEstantePirata.LAMP_Y)
        val k = if (a.lumbre) 1f else cerca(l)
        val aro = Offset(l.x, l.y + w * 0.06f)
        // El brazo: de la placa en el mamparo a la horquilla.
        val placa = Offset(w * 0.978f, aro.y - w * 0.012f)
        val brazo = Path().apply { moveTo(placa.x, placa.y); cubicTo(placa.x - w * 0.03f, placa.y + w * 0.025f, aro.x + w * 0.06f, aro.y + w * 0.025f, aro.x + w * 0.04f, aro.y) }
        sombraEnParedPirata(Path().apply { addPath(brazo) }, placa, w * 0.007f, a, 0.5f, 0.3f)
        drawPath(brazo, a.luz(P.HIERRO, k), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(brazo, a.brillo(0.3f, k), style = Stroke(w * 0.002f))
        drawRoundRect(a.luz(P.HIERRO, k), Offset(placa.x - w * 0.007f, placa.y - w * 0.028f), Size(w * 0.016f, w * 0.056f), CornerRadius(2f * u))
        remachePirata(Offset(placa.x + w * 0.001f, placa.y - w * 0.018f), 1.4f * u, a, k)
        remachePirata(Offset(placa.x + w * 0.001f, placa.y + w * 0.018f), 1.4f * u, a, k)
        // El aro de fuera, de canto, con sus pivotes; la copa con su contrapeso.
        drawOval(a.luz(P.LATON_OSC, k), Offset(aro.x - w * 0.042f, aro.y - w * 0.01f), Size(w * 0.084f, w * 0.02f), style = Stroke(w * 0.006f))
        drawOval(a.luz(P.LATON, k), Offset(aro.x - w * 0.042f, aro.y - w * 0.011f), Size(w * 0.084f, w * 0.02f), style = Stroke(w * 0.003f))
        listOf(-1f, 1f).forEach { s -> drawCircle(a.luz(P.LATON_OSC, k), w * 0.005f, Offset(aro.x + s * w * 0.042f, aro.y)) }
        drawPath(poligonoPirata(aro.x - w * 0.02f, aro.y, aro.x + w * 0.02f, aro.y, aro.x + w * 0.011f, aro.y + w * 0.024f, aro.x - w * 0.011f, aro.y + w * 0.024f), a.luz(P.LATON, k))
        drawCircle(a.luz(P.LATON_OSC, k), w * 0.012f, Offset(aro.x, aro.y + w * 0.033f))
        drawCircle(a.brillo(0.6f, k), w * 0.0035f, Offset(aro.x - w * 0.004f, aro.y + w * 0.028f))
        // La vela y el guardabrisa.
        val vela = Rect(l.x - w * 0.008f, l.y + w * 0.007f, l.x + w * 0.008f, aro.y)
        drawRect(a.dia(Color(0xFFF1E8CE), 0f, k), vela.topLeft, vela.size)
        drawRect(a.sombra(0.2f), Offset(vela.right - w * 0.005f, vela.top), Size(w * 0.005f, vela.height))
        drawLine(a.luz(P.TINTA, k), Offset(l.x, vela.top), Offset(l.x, vela.top - w * 0.005f), 1.1f * u)
        val g = Path().apply {
            moveTo(l.x - w * 0.02f, aro.y - w * 0.002f)
            cubicTo(l.x - w * 0.04f, aro.y - w * 0.035f, l.x - w * 0.034f, l.y - w * 0.025f, l.x - w * 0.017f, l.y - w * 0.042f)
            lineTo(l.x + w * 0.017f, l.y - w * 0.042f)
            cubicTo(l.x + w * 0.034f, l.y - w * 0.025f, l.x + w * 0.04f, aro.y - w * 0.035f, l.x + w * 0.02f, aro.y - w * 0.002f)
            close()
        }
        if (a.lumbre) {
            val f = if (a.oscuro) 1f else 0.75f
            drawPath(g, P.LLAMA.copy(alpha = 0.22f * f))
            mancha(l, w * 0.045f, P.FUEGO.copy(alpha = 0.7f * f))
            val llama = Path().apply {
                moveTo(l.x, l.y - w * 0.026f)
                quadraticTo(l.x + w * 0.009f, l.y - w * 0.004f, l.x, l.y + w * 0.007f)
                quadraticTo(l.x - w * 0.009f, l.y - w * 0.004f, l.x, l.y - w * 0.026f)
                close()
            }
            drawPath(llama, P.LLAMA)
            drawOval(P.FUEGO, Offset(l.x - w * 0.0035f, l.y - w * 0.012f), Size(w * 0.007f, w * 0.015f))
        } else {
            drawPath(g, a.claro(0.2f))
        }
        drawPath(g, a.luz(P.VIDRIO, k).copy(alpha = 0.8f), style = Stroke(1.3f * u))
        drawLine(Color.White.copy(alpha = if (a.oscuro) 0.6f else 0.8f), Offset(l.x - w * 0.025f, aro.y - w * 0.036f), Offset(l.x - w * 0.02f, l.y - w * 0.022f), 1.8f * u, StrokeCap.Round)
        drawOval(a.luz(P.LATON, k), Offset(l.x - w * 0.019f, l.y - w * 0.046f), Size(w * 0.038f, w * 0.008f), style = Stroke(1.4f * u))
    }
}
