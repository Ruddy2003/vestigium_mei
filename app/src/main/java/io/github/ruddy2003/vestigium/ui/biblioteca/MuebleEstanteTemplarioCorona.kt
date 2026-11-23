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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El bodegón de la corona de Templario ([MuebleEstanteTemplario]): en la pared, la bocina de guardia colgada de su tiracol y el
 * brazo de hierro con la lámpara de vidrio de tres cadenas (de noche, la luz que manda); encima del armario, la jarra vidriada
 * de la costa con el cuenco de dátiles y granadas bajo el ajimez, el aguamanil de latón delante de la jofaina puesta de canto
 * (lo que manda, en la mancha del sol), la toalla listada que cuelga por la cornisa, los dos códices tumbados con la sobreveste
 * blanca doblada encima, el cuerno de tinta con su cálamo y los rollos atados con un sello que cuelga.
 */
internal object CoronaEstanteTemplario {
    private val P = PaletaTemplario

    /** La sombra de [p] en la pared: de día, corrida abajo a la derecha (más franca en la mancha del sol); al ocaso, larga a la
     * derecha; de noche, hacia fuera de la lámpara. No baja de [hasta]. */
    private fun DrawScope.sombraPared(p: Path, desde: Offset, hasta: Float, a: AireTemplario, m: ManchaTemplario?, fuerza: Float = 0.24f) {
        val w = size.width
        clipRect(w * MuroEstanteTemplario.ESQUINA, 0f, w, hasta) {
            when {
                a.oscuro -> {
                    val k = MuebleEstanteTemplario.lamparaArriba(desde.x / w - MuebleEstanteTemplario.LAMP_X, desde.y / w - MuebleEstanteTemplario.LAMP_Y, a.noche)
                    if (k > 0.12f) {
                        val dx = ((desde.x / w - MuebleEstanteTemplario.LAMP_X) / 0.25f).coerceIn(-1f, 1f)
                        val dy = ((desde.y / w - MuebleEstanteTemplario.LAMP_Y) / 0.25f).coerceIn(-1f, 1f)
                        translate(w * 0.035f * dx, w * 0.018f * dy + w * 0.006f) { drawPath(p, a.sombra((0.6f * k).coerceAtMost(0.5f))) }
                    }
                }
                a.noche -> translate(w * 0.05f, w * 0.008f) { drawPath(p, a.sombra(fuerza * 1.3f)) }
                else -> {
                    val enSol = m?.toca(desde.x / w + 0.02f, desde.y / w + 0.012f) ?: 0f
                    translate(w * 0.02f, w * 0.012f) { drawPath(p, a.sombra(if (enSol > 0f) fuerza * 1.7f else fuerza)) }
                }
            }
        }
    }


    // ---------------------------------------------------------------- en la pared

    fun DrawScope.colgado(a: AireTemplario, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        with(ColgadoEstanteTemplario) {
            bocina(a, apoyo, cerca, m)
            lampara(a, apoyo, cerca, m)
        }
    }

    // ---------------------------------------------------------------- encima del armario

    fun DrawScope.encima(a: AireTemplario, apoyo: Float, ch: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width
        val pie = apoyo + w * 0.003f
        jofaina(a, Offset(w * 0.345f, pie - w * 0.002f), apoyo, cerca, m)
        jarra(a, Offset(w * 0.088f, pie + w * 0.002f), apoyo, cerca, m)
        cuenco(a, Offset(w * 0.165f, pie + w * 0.004f), apoyo, cerca, m)
        aguamanil(a, Offset(w * 0.3f, pie + w * 0.006f), apoyo, cerca, m)
        toalla(a, w * 0.405f, apoyo, ch, cerca, m)
        codices(a, Offset(w * 0.5f, pie + w * 0.002f), apoyo, cerca, m)
        tintero(a, Offset(w * 0.69f, pie + w * 0.003f), apoyo, cerca, m)
        rollos(a, Offset(w * 0.735f, pie + w * 0.002f), apoyo, ch, cerca, m)
    }

    /** La jofaina de cobre, grande, puesta de canto contra la cal: se ve su cara de dentro, con el ala, las bandas labradas y el ombligo. */
    private fun DrawScope.jofaina(a: AireTemplario, pie: Offset, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val rx = w * 0.112f; val ry = w * 0.104f
        val c = Offset(pie.x, pie.y - ry)
        val k = cerca(c)
        val disco = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }
        sombraPared(disco, c, apoyo, a, m, 0.28f)
        val laton = a.dia(P.COBRE, 0f, k)
        drawPath(disco, laton)
        // El ala: un anillo claro arriba a la izquierda y en sombra abajo a la derecha.
        val ala = Rect(c.x - rx * 0.8f, c.y - ry * 0.8f, c.x + rx * 0.8f, c.y + ry * 0.8f)
        drawOval(a.dia(P.COBRE_OSC, 0f, k), ala.topLeft, ala.size)
        clipPath(Path().apply { addOval(ala) }) {
            // El cuenco por dentro: la luz entra por arriba a la izquierda y se refleja abajo a la derecha.
            drawOval(a.dia(P.COBRE, -0.05f, k), Offset(ala.left + ala.width * 0.12f, ala.top + ala.height * 0.12f), Size(ala.width * 0.95f, ala.height * 0.95f))
            drawOval(a.dia(P.COBRE_LUZ, 0f, k).copy(alpha = 0.6f), Offset(ala.left + ala.width * 0.45f, ala.top + ala.height * 0.5f), Size(ala.width * 0.45f, ala.height * 0.42f))
            drawRect(a.sombra(0.25f), ala.topLeft, Size(ala.width, ala.height * 0.22f))
        }
        // Las bandas labradas: un anillo de zigzag en el ala y otro de puntos en el fondo.
        val zig = Path()
        val nz = 36
        for (j in 0..nz) {
            val t = j / nz.toFloat() * 6.2832f
            val rr = if (j % 2 == 0) 0.88f else 0.94f
            val p = Offset(c.x + kotlin.math.cos(t) * rx * rr, c.y + kotlin.math.sin(t) * ry * rr)
            if (j == 0) zig.moveTo(p.x, p.y) else zig.lineTo(p.x, p.y)
        }
        drawPath(zig, a.dia(P.COBRE_OSC, -0.1f, k).copy(alpha = 0.8f), style = Stroke(0.9f * u))
        for (j in 0 until 18) {
            val t = j / 18f * 6.2832f
            drawCircle(a.dia(P.COBRE_OSC, -0.1f, k), 0.9f * u, Offset(c.x + kotlin.math.cos(t) * rx * 0.55f, c.y + kotlin.math.sin(t) * ry * 0.55f))
        }
        drawOval(a.dia(P.COBRE_OSC, -0.1f, k), Offset(c.x - rx * 0.36f, c.y - ry * 0.36f), Size(rx * 0.72f, ry * 0.72f), style = Stroke(0.9f * u))
        // El ombligo del centro.
        drawCircle(a.dia(P.COBRE, 0.1f, k), rx * 0.12f, c)
        drawCircle(a.sombra(0.3f), rx * 0.12f, c + Offset(rx * 0.03f, ry * 0.03f), style = Stroke(1f * u))
        // El borde: luz arriba a la izquierda (el sol del ajimez), sombra abajo a la derecha.
        drawArc(a.brillo(0.7f, k), 150f, 120f, false, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(1.6f * u))
        drawArc(a.sombra(0.4f), -30f, 120f, false, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(2f * u))
        destelloTemplario(Offset(c.x - rx * 0.6f, c.y - ry * 0.55f), 1.8f * u, a, k)
        contactoTemplario(Offset(pie.x + w * 0.01f, pie.y), rx * 0.9f, w * 0.008f, a, 0.4f)
        luzDelAjimez(disco, a, m)
    }

    /** La jarra vidriada de la costa: el cuerpo panzudo en amarillo con chorreones verdes y la labor a punzón, el cuello, el pico y el asa. */
    private fun DrawScope.jarra(a: AireTemplario, pie: Offset, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - w * 0.08f))
        val alto = w * 0.165f
        val x = pie.x; val y = pie.y
        val cuerpo = Path().apply {
            moveTo(x - w * 0.022f, y)
            cubicTo(x - w * 0.05f, y - w * 0.02f, x - w * 0.056f, y - alto * 0.52f, x - w * 0.02f, y - alto * 0.72f)
            lineTo(x - w * 0.017f, y - alto * 0.92f)
            quadraticTo(x - w * 0.028f, y - alto * 0.98f, x - w * 0.03f, y - alto)
            lineTo(x + w * 0.024f, y - alto)
            quadraticTo(x + w * 0.022f, y - alto * 0.96f, x + w * 0.017f, y - alto * 0.92f)
            lineTo(x + w * 0.02f, y - alto * 0.72f)
            cubicTo(x + w * 0.056f, y - alto * 0.52f, x + w * 0.05f, y - w * 0.02f, x + w * 0.022f, y)
            close()
        }
        // El asa, a la derecha.
        val asa = Path().apply { moveTo(x + w * 0.019f, y - alto * 0.86f); cubicTo(x + w * 0.06f, y - alto * 0.88f, x + w * 0.064f, y - alto * 0.6f, x + w * 0.036f, y - alto * 0.5f) }
        drawPath(asa, a.dia(P.VIDRIADO, -0.15f, k), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, a.brillo(0.3f, k), style = Stroke(w * 0.002f))
        drawPath(cuerpo, a.dia(P.VIDRIADO, 0f, k))
        clipPath(cuerpo) {
            // Lo de abajo sin vidriar (el barro) y el borde del vidriado que chorrea.
            val vid = Path().apply {
                moveTo(x - w * 0.06f, y - alto * 0.3f)
                for (j in 0..8) lineTo(x - w * 0.06f + w * 0.015f * j, y - alto * (0.28f - 0.06f * azar(9010, j)))
                lineTo(x + w * 0.07f, y + w * 0.01f); lineTo(x - w * 0.06f, y + w * 0.01f); close()
            }
            drawPath(vid, a.dia(P.BARRO, 0f, k))
            // Los chorreones verdes y la labor a punzón (espirales y rayas), más oscura.
            listOf(-0.03f, 0.004f, 0.03f).forEachIndexed { i, dx ->
                drawRoundRect(a.dia(P.VIDRIADO_VERDE, 0f, k).copy(alpha = 0.8f), Offset(x + w * dx - w * 0.006f, y - alto * 1.0f), Size(w * 0.012f, alto * (0.5f + 0.15f * i)), CornerRadius(w * 0.006f))
            }
            val labor = Path()
            for (j in 0 until 3) {
                val cy = y - alto * (0.48f + 0.06f * (j % 2)); val cx = x + w * (-0.025f + 0.025f * j)
                labor.addOval(Rect(cx - w * 0.008f, cy - w * 0.008f, cx + w * 0.008f, cy + w * 0.008f))
                labor.addOval(Rect(cx - w * 0.004f, cy - w * 0.004f, cx + w * 0.004f, cy + w * 0.004f))
            }
            labor.moveTo(x - w * 0.05f, y - alto * 0.62f); labor.lineTo(x + w * 0.05f, y - alto * 0.62f)
            labor.moveTo(x - w * 0.05f, y - alto * 0.36f); labor.lineTo(x + w * 0.05f, y - alto * 0.36f)
            drawPath(labor, a.dia(P.BARRO_OSC, 0f, k).copy(alpha = 0.7f), style = Stroke(0.8f * u))
            // El volumen: luz a la izquierda (el ajimez), sombra a la derecha; el brillo del vidriado.
            cilindroTemplario(Rect(x - w * 0.056f, y - alto, x + w * 0.056f, y), a, k, -1f, 1.3f)
            drawRoundRect(a.claro(0.75f), Offset(x - w * 0.032f, y - alto * 0.66f), Size(w * 0.005f, alto * 0.24f), CornerRadius(w * 0.003f))
        }
        drawOval(a.dia(P.BARRO_OSC, -0.2f, k), Offset(x - w * 0.026f, y - alto - w * 0.004f), Size(w * 0.05f, w * 0.008f))
        luzDelAjimez(cuerpo, a, m)
        contactoTemplario(Offset(x + w * 0.006f, y), w * 0.08f, w * 0.009f, a, 0.42f)
    }

    /** El cuenco vidriado de verde con los dátiles y dos granadas. */
    private fun DrawScope.cuenco(a: AireTemplario, pie: Offset, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(pie)
        val bw = w * 0.085f; val bh = w * 0.03f
        // Las granadas y los dátiles, encima del borde.
        listOf(Triple(-0.018f, 0.036f, 0), Triple(0.016f, 0.032f, 1)).forEach { (dx, r, i) ->
            val c = Offset(pie.x + w * dx, pie.y - bh - w * r * 0.5f + w * 0.006f)
            val rr = w * r * 0.55f
            drawCircle(a.dia(P.GRANADA, 0f, k), rr, c)
            drawCircle(a.dia(mezcla(P.GRANADA, P.LACRE, 0.5f), -0.25f, k), rr * 0.7f, c + Offset(rr * 0.3f, rr * 0.25f))
            drawCircle(a.brillo(0.5f, k), rr * 0.2f, c - Offset(rr * 0.42f, rr * 0.4f))
            // La corona de la granada.
            val co = Offset(c.x + rr * 0.1f, c.y - rr * 0.95f)
            drawPath(poligonoTemplario(co.x - rr * 0.25f, co.y + rr * 0.2f, co.x - rr * 0.3f, co.y - rr * 0.25f, co.x - rr * 0.08f, co.y,
                co.x, co.y - rr * 0.32f, co.x + rr * 0.08f, co.y, co.x + rr * 0.3f, co.y - rr * 0.25f, co.x + rr * 0.25f, co.y + rr * 0.2f),
                a.dia(mezcla(P.GRANADA, P.CUERO_OSC, 0.4f), 0f, k))
        }
        for (j in 0 until 5) {
            val c = Offset(pie.x + w * (-0.036f + 0.018f * j), pie.y - bh + w * (0.002f - 0.004f * (j % 2)))
            rotate(-20f + 15f * j, c) {
                drawOval(a.dia(P.DATIL, 0f, k), Offset(c.x - w * 0.011f, c.y - w * 0.006f), Size(w * 0.022f, w * 0.012f))
                drawOval(a.brillo(0.4f, k), Offset(c.x - w * 0.007f, c.y - w * 0.004f), Size(w * 0.008f, w * 0.003f))
            }
        }
        // El cuenco.
        val cuenco = Path().apply {
            moveTo(pie.x - bw / 2, pie.y - bh)
            cubicTo(pie.x - bw * 0.48f, pie.y - bh * 0.2f, pie.x - bw * 0.25f, pie.y, pie.x - bw * 0.15f, pie.y)
            lineTo(pie.x + bw * 0.15f, pie.y)
            cubicTo(pie.x + bw * 0.25f, pie.y, pie.x + bw * 0.48f, pie.y - bh * 0.2f, pie.x + bw / 2, pie.y - bh); close()
        }
        drawPath(cuenco, a.dia(P.VIDRIADO_VERDE, 0f, k))
        clipPath(cuenco) { cilindroTemplario(Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y), a, k, -1f, 1.2f) }
        drawLine(a.dia(P.VIDRIADO_VERDE, 0.25f, k), Offset(pie.x - bw / 2, pie.y - bh), Offset(pie.x + bw / 2, pie.y - bh), 1.6f * u)
        luzDelAjimez(cuenco, a, m)
        contactoTemplario(Offset(pie.x + w * 0.006f, pie.y), bw * 0.9f, w * 0.007f, a, 0.4f)
    }

    /** El aguamanil de latón: el pie, el cuerpo de pera con sus bandas labradas, el cuello, la tapa, el pico largo y el asa. */
    private fun DrawScope.aguamanil(a: AireTemplario, pie: Offset, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val alto = w * 0.25f
        val k = cerca(Offset(x, y - alto * 0.5f))
        fun c(col: Color, f: Float = 0f) = a.dia(col, f, k)
        val cuerpo = Path().apply {
            // El pie.
            moveTo(x - w * 0.028f, y); lineTo(x + w * 0.028f, y); lineTo(x + w * 0.018f, y - w * 0.012f)
            // El cuerpo de pera, a la derecha.
            cubicTo(x + w * 0.06f, y - w * 0.03f, x + w * 0.064f, y - alto * 0.42f, x + w * 0.03f, y - alto * 0.55f)
            // El cuello.
            cubicTo(x + w * 0.014f, y - alto * 0.6f, x + w * 0.012f, y - alto * 0.72f, x + w * 0.016f, y - alto * 0.82f)
            lineTo(x + w * 0.02f, y - alto * 0.86f); lineTo(x - w * 0.02f, y - alto * 0.86f); lineTo(x - w * 0.016f, y - alto * 0.82f)
            cubicTo(x - w * 0.012f, y - alto * 0.72f, x - w * 0.014f, y - alto * 0.6f, x - w * 0.03f, y - alto * 0.55f)
            cubicTo(x - w * 0.064f, y - alto * 0.42f, x - w * 0.06f, y - w * 0.03f, x - w * 0.018f, y - w * 0.012f)
            close()
        }
        // El pico: sale del hombro a la izquierda y sube largo y fino.
        val pico = Path().apply {
            moveTo(x - w * 0.04f, y - alto * 0.42f)
            cubicTo(x - w * 0.07f, y - alto * 0.5f, x - w * 0.085f, y - alto * 0.7f, x - w * 0.105f, y - alto * 0.86f)
            lineTo(x - w * 0.098f, y - alto * 0.875f)
            cubicTo(x - w * 0.078f, y - alto * 0.74f, x - w * 0.06f, y - alto * 0.57f, x - w * 0.03f, y - alto * 0.5f)
            close()
        }
        // El asa: del cuello al hombro, a la derecha.
        val asa = Path().apply { moveTo(x + w * 0.016f, y - alto * 0.8f); cubicTo(x + w * 0.08f, y - alto * 0.84f, x + w * 0.09f, y - alto * 0.55f, x + w * 0.05f, y - alto * 0.42f) }
        val todo = Path().apply { addPath(cuerpo); addPath(pico) }
        sombraPared(todo, Offset(x, y - alto * 0.5f), apoyo, a, m, 0.3f)
        if (!a.oscuro) translate(w * 0.02f, w * 0.012f) { clipRect(0f, 0f, w, apoyo) { drawPath(asa, a.sombra(0.25f), style = Stroke(w * 0.007f)) } }
        drawPath(asa, c(P.LATON_OSC, 0.05f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, a.brillo(0.45f, k), style = Stroke(w * 0.002f))
        drawPath(pico, c(P.LATON))
        clipPath(pico) { drawPath(pico, a.sombra(0.35f), style = Stroke(w * 0.006f)) }
        drawPath(pico, a.brillo(0.5f, k), style = Stroke(0.7f * u))
        drawPath(cuerpo, c(P.LATON))
        clipPath(cuerpo) {
            // El volumen de un cuerpo de revolución, con la luz a la izquierda.
            cilindroTemplario(Rect(x - w * 0.064f, y - alto, x + w * 0.064f, y), a, k, -1f, 1.4f)
            // El canto de la derecha, hondo, con la luz que devuelve la jofaina de cobre.
            drawRect(a.sombra(0.3f), Offset(x + w * 0.03f, y - alto), Size(w * 0.04f, alto))
            drawRect(c(P.COBRE_LUZ).copy(alpha = 0.35f), Offset(x + w * 0.038f, y - alto * 0.5f), Size(w * 0.006f, alto * 0.42f))
            // La sombra que el hombro echa sobre la panza.
            drawOval(a.sombra(0.22f), Offset(x - w * 0.06f, y - alto * 0.56f), Size(w * 0.12f, alto * 0.08f))
            // Las bandas labradas: zigzag en la panza, puntos en el hombro y filetes.
            val bandas = Path()
            listOf(0.2f, 0.34f, 0.5f).forEach { fy -> bandas.moveTo(x - w * 0.07f, y - alto * fy); bandas.lineTo(x + w * 0.07f, y - alto * fy) }
            var xz = x - w * 0.07f
            var arriba = true
            bandas.moveTo(xz, y - alto * 0.22f)
            while (xz < x + w * 0.07f) { xz += w * 0.008f; bandas.lineTo(xz, y - alto * (if (arriba) 0.32f else 0.22f)); arriba = !arriba }
            drawPath(bandas, c(P.LATON_OSC, -0.15f).copy(alpha = 0.85f), style = Stroke(0.9f * u))
            for (j in 0 until 9) drawCircle(c(P.LATON_OSC, -0.15f), 0.8f * u, Offset(x - w * 0.048f + w * 0.012f * j, y - alto * 0.42f))
            // El brillo largo del metal pulido y el reflejo cálido abajo.
            drawRoundRect(a.claro(0.85f), Offset(x - w * 0.036f, y - alto * 0.46f), Size(w * 0.006f, alto * 0.3f), CornerRadius(w * 0.003f))
            drawRoundRect(a.claro(0.4f), Offset(x - w * 0.006f, y - alto * 0.8f), Size(w * 0.004f, alto * 0.18f), CornerRadius(w * 0.002f))
            drawOval(c(P.LATON_LUZ, 0.1f).copy(alpha = 0.4f), Offset(x + w * 0.02f, y - alto * 0.14f), Size(w * 0.03f, alto * 0.08f))
        }
        // La tapa con su botón y la bisagra.
        val tapa = Path().apply {
            moveTo(x - w * 0.022f, y - alto * 0.86f); quadraticTo(x, y - alto * 0.97f, x + w * 0.022f, y - alto * 0.86f); close()
        }
        drawPath(tapa, c(P.LATON, 0.05f))
        drawCircle(c(P.LATON, 0.1f), w * 0.006f, Offset(x, y - alto * 0.975f))
        drawCircle(a.brillo(0.6f, k), w * 0.002f, Offset(x - w * 0.002f, y - alto * 0.98f))
        drawLine(c(P.LATON_OSC), Offset(x - w * 0.022f, y - alto * 0.86f), Offset(x + w * 0.022f, y - alto * 0.86f), 1.2f * u)
        destelloTemplario(Offset(x - w * 0.034f, y - alto * 0.36f), 2f * u, a, k)
        luzDelAjimez(Path().apply { addPath(todo); addPath(tapa) }, a, m)
        contactoTemplario(Offset(x + w * 0.008f, y), w * 0.08f, w * 0.009f, a, 0.45f)
    }

    /** La toalla de lino con sus listas de grana, tendida sobre el tablero y colgando por la cornisa, con los flecos. */
    private fun DrawScope.toalla(a: AireTemplario, x0: Float, apoyo: Float, ch: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val ancho = w * 0.07f
        val k = cerca(Offset(x0 + ancho / 2, apoyo + w * 0.04f))
        val baja = ch - w * 0.004f
        val forma = Path().apply {
            moveTo(x0 + w * 0.006f, apoyo - w * 0.004f); lineTo(x0 + ancho + w * 0.004f, apoyo - w * 0.006f)
            lineTo(x0 + ancho + w * 0.008f, apoyo + w * 0.012f)
            cubicTo(x0 + ancho + w * 0.012f, apoyo + w * 0.04f, x0 + ancho - w * 0.004f, baja - w * 0.02f, x0 + ancho + w * 0.002f, baja)
            lineTo(x0 - w * 0.002f, baja - w * 0.006f)
            cubicTo(x0 + w * 0.004f, baja - w * 0.03f, x0 - w * 0.008f, apoyo + w * 0.04f, x0, apoyo + w * 0.012f)
            close()
        }
        translate(w * 0.008f, w * 0.004f) { clipRect(0f, apoyo + w * 0.012f, w, ch) { drawPath(forma, a.sombra(0.45f)) } }
        drawPath(forma, a.dia(P.LINO, 0f, k))
        clipPath(forma) {
            // Lo de arriba, tendido sobre el tablero, más claro; el pliegue del canto.
            drawRect(a.dia(P.LINO, 0.15f, k), Offset(x0 - w * 0.01f, apoyo - w * 0.01f), Size(ancho + w * 0.03f, w * 0.022f))
            drawRect(a.sombra(0.3f), Offset(x0 - w * 0.01f, apoyo + w * 0.012f), Size(ancho + w * 0.03f, w * 0.006f))
            // Las listas de grana, a lo largo.
            listOf(0.16f, 0.24f, 0.76f, 0.84f).forEach { fx -> drawRect(a.dia(P.CRUZ, -0.05f, k), Offset(x0 + ancho * fx, apoyo - w * 0.01f), Size(ancho * 0.045f, ch)) }
            listOf(0.5f).forEach { fx -> drawRect(a.dia(P.CUERO_OSC, -0.05f, k), Offset(x0 + ancho * fx - ancho * 0.02f, apoyo - w * 0.01f), Size(ancho * 0.04f, ch)) }
            // Los pliegues de la caída: hondos que se abren hacia abajo y sus crestas de luz.
            listOf(0.22f, 0.55f, 0.86f).forEachIndexed { i, fx ->
                val xt = x0 + ancho * fx
                val hondo = Path().apply {
                    moveTo(xt - w * 0.002f, apoyo + w * 0.016f); lineTo(xt + w * 0.002f, apoyo + w * 0.016f)
                    lineTo(xt + w * 0.009f, baja + w * 0.004f); lineTo(xt - w * 0.005f, baja + w * 0.004f); close()
                }
                drawPath(hondo, a.sombra(if (i == 2) 0.32f else 0.22f))
                drawLine(a.claro(0.35f), Offset(xt - w * 0.006f, apoyo + w * 0.018f), Offset(xt - w * 0.011f, baja), 1.4f * u)
            }
            drawRect(a.sombra(0.2f), Offset(x0 + ancho * 0.9f, apoyo + w * 0.014f), Size(ancho * 0.2f, ch))
        }
        // Los flecos.
        val flecos = Path()
        for (j in 0 until 12) {
            val fx = x0 + ancho * (j + 0.5f) / 12f
            val y0 = baja - w * 0.006f * (1f - j / 12f)
            flecos.moveTo(fx, y0); flecos.lineTo(fx + (azar(9030, j) - 0.5f) * 1.5f * u, y0 + w * (0.008f + 0.006f * azar(9031, j)))
        }
        drawPath(flecos, a.dia(P.LINO_OSC, 0f, k), style = Stroke(0.8f * u))
        luzDelAjimez(forma, a, m, ManchaTemplario.FRENTE)
    }

    /** Dos códices tumbados, uno sobre otro, con la sobreveste blanca doblada encima (asoma un trozo de la cruz). */
    private fun DrawScope.codices(a: AireTemplario, pie: Offset, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x + w * 0.08f, pie.y - w * 0.04f))
        val prof = w * 0.014f
        var y = pie.y
        val libros = listOf(Triple(0f, 0.165f, mezcla(P.CUERO, P.LACRE, 0.25f)), Triple(0.012f, 0.145f, Color(0xFF34486E)))
        val sil = Path()
        libros.forEachIndexed { i, (dx, largo, cuero) ->
            val x0 = pie.x + w * dx; val x1 = x0 + w * largo
            val g = w * 0.025f
            val top = y - g
            sil.addRect(Rect(x0, top - prof, x1 + prof, y))
            // La tapa de arriba (vista desde encima), el canto de las hojas y la tapa de abajo.
            drawPath(poligonoTemplario(x0, top, x1, top, x1 + prof, top - prof, x0 + prof, top - prof), a.dia(cuero, 0.1f, k))
            drawPath(poligonoTemplario(x1, top, x1 + prof, top - prof, x1 + prof, y - prof, x1, y), a.dia(cuero, -0.3f, k))
            drawRect(a.dia(cuero, 0f, k), Offset(x0, top), Size(x1 - x0, g * 0.24f))
            drawRect(a.dia(cuero, -0.1f, k), Offset(x0, y - g * 0.24f), Size(x1 - x0, g * 0.24f))
            drawRect(a.dia(P.PERGAMINO, -0.05f, k), Offset(x0 + w * 0.002f, top + g * 0.24f), Size(x1 - x0 - w * 0.004f, g * 0.52f))
            for (j in 0 until 4) drawLine(a.sombra(0.16f), Offset(x0 + w * 0.003f, top + g * (0.3f + 0.12f * j)), Offset(x1 - w * 0.003f, top + g * (0.3f + 0.12f * j)), 0.5f * u)
            drawRect(a.brillo(0.45f, k), Offset(x0, top), Size(x1 - x0, 0.9f * u))
            // Los bullones de latón en las esquinas de la tapa.
            listOf(x0 + w * 0.012f, x1 - w * 0.01f).forEach { bx -> drawCircle(a.dia(P.LATON, 0.05f, k), w * 0.004f, Offset(bx + prof * 0.4f, top - prof * 0.5f)) }
            // El broche que cruza el canto.
            drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(x0 + (x1 - x0) * 0.66f, top), Size(w * 0.008f, g))
            y = top - prof * 0.2f
        }
        // La sobreveste doblada encima: un fardo blando con su doblez, y un trozo de la cruz roja en el frente.
        val sx0 = pie.x + w * 0.006f; val sx1 = pie.x + w * 0.15f
        val sy1 = y + w * 0.002f; val sy0 = sy1 - w * 0.034f
        val tela = Path().apply {
            moveTo(sx0, sy1); cubicTo(sx0 - w * 0.006f, sy1 - w * 0.012f, sx0 - w * 0.002f, sy0 + w * 0.004f, sx0 + w * 0.01f, sy0)
            lineTo(sx1 - w * 0.008f, sy0 - w * 0.003f)
            cubicTo(sx1 + w * 0.006f, sy0 + w * 0.004f, sx1 + w * 0.006f, sy1 - w * 0.01f, sx1, sy1); close()
        }
        sil.addPath(tela)
        sombraPared(sil, Offset(pie.x + w * 0.08f, pie.y - w * 0.04f), apoyo, a, m, 0.26f)
        drawPath(tela, a.dia(P.LINO, 0f, k))
        clipPath(tela) {
            drawRect(a.dia(P.LINO, 0.12f, k), Offset(sx0, sy0 - w * 0.004f), Size(sx1 - sx0, w * 0.01f))
            drawRect(a.sombra(0.18f), Offset(sx0, sy0 + w * 0.012f), Size(sx1 - sx0, w * 0.003f))
            // La cruz, cortada por el doblez: solo el brazo y medio del frente.
            drawPath(cruzPatada(Offset(sx0 + (sx1 - sx0) * 0.62f, sy0 + w * 0.034f), w * 0.026f), a.dia(P.CRUZ, 0f, k))
            drawRect(a.sombra(0.22f), Offset(sx0, sy1 - w * 0.008f), Size(sx1 - sx0, w * 0.008f))
            drawRect(a.sombra(0.18f), Offset(sx1 - w * 0.02f, sy0), Size(w * 0.02f, sy1 - sy0))
        }
        drawLine(a.brillo(0.35f, k), Offset(sx0 + w * 0.01f, sy0 + 0.6f * u), Offset(sx1 - w * 0.01f, sy0 - w * 0.002f), 0.9f * u)
        luzDelAjimez(sil, a, m)
        contactoTemplario(Offset(pie.x + w * 0.09f, pie.y), w * 0.19f, w * 0.009f, a, 0.42f)
    }

    /** El cuerno de tinta, de pie en su taco de madera, con el cálamo de caña metido. */
    private fun DrawScope.tintero(a: AireTemplario, pie: Offset, apoyo: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - w * 0.03f))
        val taco = Rect(pie.x - w * 0.016f, pie.y - w * 0.014f, pie.x + w * 0.016f, pie.y)
        val cuerno = Path().apply {
            moveTo(pie.x - w * 0.01f, taco.top); lineTo(pie.x - w * 0.012f, taco.top - w * 0.03f)
            quadraticTo(pie.x, taco.top - w * 0.036f, pie.x + w * 0.012f, taco.top - w * 0.03f)
            lineTo(pie.x + w * 0.008f, taco.top); close()
        }
        sombraPared(Path().apply { addPath(cuerno); addRect(taco) }, Offset(pie.x, pie.y - w * 0.03f), apoyo, a, m, 0.24f)
        // El cálamo, inclinado.
        drawLine(a.dia(P.PALMA, 0.05f, k), Offset(pie.x + w * 0.002f, taco.top - w * 0.02f), Offset(pie.x + w * 0.03f, taco.top - w * 0.085f), w * 0.004f, StrokeCap.Round)
        drawLine(a.dia(P.PALMA_OSC, 0f, k), Offset(pie.x + w * 0.004f, taco.top - w * 0.02f), Offset(pie.x + w * 0.031f, taco.top - w * 0.083f), w * 0.0015f)
        drawRect(a.dia(P.CEDRO, 0f, k), taco.topLeft, taco.size)
        drawRect(a.brillo(0.4f, k), taco.topLeft, Size(taco.width, 0.9f * u))
        drawRect(a.sombra(0.3f), Offset(taco.right - taco.width * 0.3f, taco.top), Size(taco.width * 0.3f, taco.height))
        drawPath(cuerno, a.dia(P.CUERNO, 0f, k))
        clipPath(cuerno) { cilindroTemplario(Rect(pie.x - w * 0.012f, taco.top - w * 0.04f, pie.x + w * 0.012f, taco.top), a, k, -1f, 1.2f) }
        drawOval(a.dia(P.TINTA, 0f, k), Offset(pie.x - w * 0.009f, taco.top - w * 0.034f), Size(w * 0.018f, w * 0.005f))
        luzDelAjimez(Path().apply { addPath(cuerno); addRect(taco) }, a, m)
        contactoTemplario(Offset(pie.x + w * 0.004f, pie.y), w * 0.04f, w * 0.006f, a, 0.42f)
    }

    /** Tres rollos de pergamino atados con cordel de grana, dos abajo y uno encima, y un sello de lacre que cuelga por la cornisa. */
    private fun DrawScope.rollos(a: AireTemplario, pie: Offset, apoyo: Float, ch: Float, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x + w * 0.08f, pie.y - w * 0.03f))
        val d = w * 0.03f
        val pos = listOf(Offset(pie.x, pie.y - d / 2), Offset(pie.x + w * 0.035f, pie.y - d / 2), Offset(pie.x + w * 0.016f, pie.y - d * 1.4f))
        val largos = listOf(w * 0.15f, w * 0.13f, w * 0.14f)
        val sil = Path()
        pos.forEachIndexed { i, p -> sil.addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p.x, p.y - d / 2, p.x + largos[i], p.y + d / 2), CornerRadius(d / 2))) }
        sombraPared(sil, Offset(pie.x + w * 0.08f, pie.y - w * 0.03f), apoyo, a, m, 0.26f)
        pos.forEachIndexed { i, p ->
            val r = Rect(p.x, p.y - d / 2, p.x + largos[i], p.y + d / 2)
            val col = mezcla(P.PERGAMINO, P.PERG_OSC, 0.15f * i)
            drawRoundRect(a.dia(col, 0f, k), r.topLeft, r.size, CornerRadius(d * 0.2f))
            // El volumen del cilindro tumbado: luz arriba, sombra abajo.
            drawRect(a.brillo(0.35f, k), Offset(r.left + d * 0.3f, r.top + d * 0.15f), Size(r.width - d * 0.5f, d * 0.18f))
            drawRect(a.sombra(0.28f), Offset(r.left, r.bottom - d * 0.3f), Size(r.width, d * 0.3f))
            // La testa con su espiral, a la izquierda.
            drawOval(a.dia(col, 0.08f, k), Offset(r.left - d * 0.12f, r.top), Size(d * 0.3f, d))
            drawOval(a.dia(P.PERG_OSC, -0.1f, k), Offset(r.left - d * 0.06f, r.top + d * 0.25f), Size(d * 0.17f, d * 0.5f), style = Stroke(0.8f * u))
            // El cordel de grana.
            drawRect(a.dia(P.CRUZ, -0.05f, k), Offset(r.left + r.width * 0.55f, r.top), Size(w * 0.005f, d))
        }
        luzDelAjimez(sil, a, m)
        // El sello que cuelga del rollo de abajo, por delante de la cornisa.
        val cuelga = Offset(pie.x + w * 0.11f, pie.y)
        val sello = Offset(cuelga.x + w * 0.006f, apoyo + w * 0.05f)
        val kk = cerca(sello)
        drawLine(a.dia(P.CRUZ, -0.1f, kk), cuelga, sello, 1.2f * u)
        drawLine(a.dia(P.CRUZ, -0.1f, kk), cuelga + Offset(w * 0.004f, 0f), sello, 1.2f * u)
        // El sello: un disco de cera aplanado, algo vuelto, con su reborde y la cola de pergamino que lo atraviesa.
        val sr = Rect(sello.x - w * 0.017f, sello.y - w * 0.013f, sello.x + w * 0.017f, sello.y + w * 0.013f)
        drawOval(a.sombra(0.45f), sr.topLeft + Offset(w * 0.004f, w * 0.004f), sr.size)
        drawOval(a.dia(mezcla(P.LACRE, P.CUERO_OSC, 0.25f), 0f, kk), sr.topLeft, sr.size)
        drawOval(a.dia(mezcla(P.LACRE, P.CUERO_OSC, 0.25f), -0.25f, kk), sr.deflate(w * 0.005f).topLeft, sr.deflate(w * 0.005f).size, style = Stroke(1.2f * u))
        drawRect(a.dia(P.PERGAMINO, -0.05f, kk), Offset(sello.x - w * 0.003f, sr.bottom - w * 0.004f), Size(w * 0.006f, w * 0.014f))
        drawOval(a.brillo(0.45f, kk), Offset(sr.left + w * 0.006f, sr.top + w * 0.004f), Size(w * 0.01f, w * 0.004f))
        contactoTemplario(Offset(pie.x + w * 0.09f, pie.y), w * 0.19f, w * 0.009f, a, 0.4f)
    }
}
