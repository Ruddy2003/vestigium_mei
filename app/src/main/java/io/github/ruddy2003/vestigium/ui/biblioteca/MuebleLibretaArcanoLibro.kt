package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El diario de recortes de la libreta de Horror cósmico ([MuebleLibretaArcano]), de pie en su atril: tapas de tela sangre de buey
 * con el lomo y las puntas de piel, la tapa algo combada por lo que lleva dentro, el bloque de hojas que engorda con los recortes
 * (se ve la cabeza y el corte, con los recortes que asoman), la marca de la humedad abajo, la cinta de hilo que lo ata y la
 * etiqueta de papel con el nombre y la cuenta. Al abrirse, la cinta cae y la tapa gira sobre el lomo: dentro, la primera hoja
 * con un recorte pegado, una foto y unas líneas a mano.
 */
internal object LibroLibretaArcano {
    private val P = PaletaArcano
    /** La tela de las tapas (sangre de buey), la piel del lomo y de las puntas, la cinta de hilo crudo. */
    private val TELA = Color(0xFF6E2622)
    private val PIEL = Color(0xFF4A2A1A)
    private val CINTA = Color(0xFFD8CCA8)

    fun DrawScope.diario(e: EstadoLibreta, s: SitioLibretaArcano, am: AireArcano, medidor: TextMeasurer) {
        val U = s.U
        val b = s.libro
        val v = s.v
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla
        val k = MuebleLibretaArcano.cerca(b.center, s, am)
        val kd = MuebleLibretaArcano.cerca(Offset(b.right + v.x, b.center.y), s, am)
        val f = MuebleLibretaArcano.frio(b.center, s)
        fun c(col: Color, kk: Float = k, fl: Float = 0f) = am.dia(col, fl, kk)
        // La sombra del diario en la pared: de día a la derecha; de noche, lejos de la lámpara (a la izquierda).
        val sombra = poligonoArcano(b.left, b.bottom, b.left, b.top, b.right + v.x, b.top + v.y, b.right + v.x, b.bottom + v.y)
        translate(if (am.oscuro) -14f * U else 12f * U, if (am.oscuro) -2f * U else 6f * U) { drawPath(sombra, am.sombra(if (am.oscuro) 0.5f else 0.28f)) }
        // Los recortes que asoman por la cabeza y por el corte (más con más hojas), detrás del bloque.
        val asoman = (2 + e.hojas / 9).coerceAtMost(11)
        for (j in 0 until asoman) {
            val porArriba = j % 3 == 0
            val t = 0.12f + 0.76f * azar(sem, 200 + j)
            val l = (10f + 14f * azar(sem, 210 + j)) * U; val an = (16f + 18f * azar(sem, 220 + j)) * U
            val r = if (porArriba) Rect(b.left + b.width * t, b.top + v.y - l, b.left + b.width * t + an, b.top + v.y + 6f * U)
                else Rect(b.right + v.x - 6f * U, b.top + b.height * t, b.right + v.x + l, b.top + b.height * t + an)
            rotate((azar(sem, 230 + j) - 0.5f) * 16f, r.center) { recorte(r, am, kd, sem + j, j % 2 == 0) }
        }
        // El bloque de las hojas: la cabeza (arriba) y el corte (a la derecha), en escorzo, con las hojas onduladas por la humedad.
        val cabeza = poligonoArcano(b.left + 8f * U, b.top, b.right, b.top, b.right + v.x, b.top + v.y, b.left + 8f * U + v.x, b.top + v.y)
        val corte = poligonoArcano(b.right, b.top, b.right + v.x, b.top + v.y, b.right + v.x, b.bottom + v.y, b.right, b.bottom)
        drawPath(cabeza, c(P.PAPEL, maxOf(k, kd * 0.8f), 0.05f))
        // Las hojas que se ven en la cabeza: líneas a lo largo, onduladas por la humedad.
        clipPath(cabeza) {
            var t = 0.12f; var j = 0
            while (t < 1f) {
                val y0 = b.top + v.y * t
                drawLine(c(P.PAPEL_OSC, kd).copy(alpha = 0.55f), Offset(b.left + 8f * U + v.x * t, y0), Offset(b.right + v.x * t, y0 + (azar(sem, 320 + j) - 0.5f) * 1.2f * U), 0.7f * U)
                t += 0.14f + 0.08f * azar(sem, 330 + j); j++
            }
        }
        drawPath(corte, c(mezcla(P.PAPEL, P.MANILA, 0.4f), kd))
        clipPath(corte) {
            var x = b.right + 2f * U; var j = 0
            while (x < b.right + v.x) { drawLine(c(P.PAPEL_OSC, kd).copy(alpha = 0.5f), Offset(x, b.top + v.y * ((x - b.right) / v.x)), Offset(x, b.bottom + v.y * ((x - b.right) / v.x)), 0.7f * U); x += (1.6f + 1.4f * azar(sem, 300 + j)) * U; j++ }
            // La marca de agua abajo y el sucio del canto.
            drawRect(c(P.SALOBRE, kd).copy(alpha = 0.35f), Offset(b.right, b.bottom - b.height * 0.22f + v.y), Size(v.x + 2f, b.height * 0.22f))
            if (am.oscuro) drawRect(P.LLAMA.copy(alpha = (0.45f * kd).coerceAtMost(0.4f)), Offset(b.right, b.top + v.y), Size(v.x + 2f, b.height))
        }
        drawPath(corte, am.sombra(0.3f), style = Stroke(0.8f * U))
        // La tapa de atrás, que asoma un poco por fuera del bloque.
        drawLine(c(TELA, kd, -0.2f), Offset(b.right + v.x, b.top + v.y), Offset(b.right + v.x, b.bottom + v.y), 2.4f * U)
        drawLine(c(TELA, k, -0.2f), Offset(b.left + 8f * U + v.x, b.top + v.y), Offset(b.right + v.x, b.top + v.y), 2.4f * U)
        // La primera hoja (se ve al abrir la tapa).
        if (a > 0.02f) primeraHoja(b, am, k, sem)
        // La tapa: gira sobre el lomo (a la izquierda) y, pasado el canto, se ve su forro.
        val ang = Math.toRadians(100.0 * a)
        val cosA = cos(ang).toFloat(); val sinA = sin(ang).toFloat()
        val m = Matrix()
        m.values[Matrix.ScaleX] = cosA
        m.values[Matrix.SkewY] = -sinA * 0.3f
        m.values[Matrix.TranslateX] = b.left * (1 - cosA)
        m.values[Matrix.TranslateY] = sinA * 0.3f * b.left
        withTransform({ transform(m) }) {
            if (cosA > 0f) tapa(e, b, s, am, k, kd, f, medidor) else {
                drawRect(c(mezcla(P.PAPEL, P.SALOBRE, 0.25f)), b.topLeft, b.size)
                drawRect(c(TELA, k, -0.1f), b.topLeft, b.size, style = Stroke(4f * U))
            }
        }
        // La cinta de hilo: atada mientras está cerrado; al abrir, caída en la mesa.
        val cinta = c(CINTA, kd)
        if (a < 0.08f) {
            val y = b.top + b.height * 0.64f
            drawLine(am.sombra(0.35f), Offset(b.left, y + 2.6f * U), Offset(b.right + v.x, y + v.y * 0.4f + 2.6f * U), 3f * U)
            drawLine(cinta, Offset(b.left, y), Offset(b.right + v.x, y + v.y * 0.4f), 3f * U)
            drawLine(am.brillo(0.4f, kd), Offset(b.left, y - 1f * U), Offset(b.right + v.x, y + v.y * 0.4f - 1f * U), 0.8f * U)
            // El nudo llano en el corte y los dos cabos que cuelgan.
            val nudo = Offset(b.right + v.x * 0.4f, y + v.y * 0.15f)
            drawLine(cinta, nudo, nudo + Offset(-6f * U, 22f * U), 2.4f * U, StrokeCap.Round)
            drawLine(cinta, nudo, nudo + Offset(5f * U, 17f * U), 2.4f * U, StrokeCap.Round)
            drawCircle(am.sombra(0.35f), 4.2f * U, nudo + Offset(1f * U, 1.4f * U))
            drawCircle(cinta, 4f * U, nudo)
            drawLine(am.sombra(0.4f), nudo + Offset(-2.4f * U, -1f * U), nudo + Offset(2.4f * U, 1f * U), 0.8f * U)
        } else {
            val p = Path().apply { moveTo(b.right + v.x, b.bottom - 20f * U); cubicTo(b.right + 30f * U, b.bottom + 6f * U, b.left + b.width * 0.6f, s.pisa + 6f * U, b.left + b.width * 0.25f, s.pisa + 2f * U) }
            drawPath(p, cinta, style = Stroke(3f * U, cap = StrokeCap.Round))
        }
    }

    /** Un recorte de periódico en [r]: el papel, la cabeza en negro y las columnas de renglones. */
    private fun DrawScope.recorte(r: Rect, am: AireArcano, k: Float, sem: Int, claro: Boolean) {
        val u = r.height.coerceAtLeast(1f) / 30f
        drawRect(am.sombra(0.3f), Offset(r.left + 1.2f * u, r.top + 1.4f * u), r.size)
        val papel = am.dia(if (claro) P.PAPEL else mezcla(P.MANILA, P.PAPEL, 0.4f), 0f, k)
        drawRect(papel, r.topLeft, r.size)
        val tinta = am.dia(P.TINTA, 0f, k).copy(alpha = 0.6f)
        drawRect(tinta, Offset(r.left + r.width * 0.1f, r.top + r.height * 0.12f), Size(r.width * (0.5f + 0.35f * azar(sem, 1)), r.height * 0.1f))
        var y = r.top + r.height * 0.34f
        while (y < r.bottom - r.height * 0.06f) {
            drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + r.width * 0.1f, y), Offset(r.left + r.width * 0.46f, y), r.height * 0.03f)
            drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + r.width * 0.54f, y), Offset(r.right - r.width * 0.1f, y), r.height * 0.03f)
            y += r.height * 0.09f
        }
    }

    /**
     * La tapa de delante: la tela sangre de buey algo combada, con su roce, la marca de la humedad y el cerco de una taza; el lomo
     * de piel redondo con sus nervios, las puntas de piel, el filete de oro gastado y la etiqueta de papel con el nombre y la cuenta.
     */
    private fun DrawScope.tapa(e: EstadoLibreta, b: Rect, s: SitioLibretaArcano, am: AireArcano, k: Float, kd: Float, f: Float, medidor: TextMeasurer) {
        val U = s.U
        val sem = e.semilla
        fun c(col: Color, kk: Float = k, fl: Float = 0f) = am.dia(col, fl, kk)
        // La tela, con el canto de delante algo combado hacia fuera (lo hincha lo que lleva dentro).
        val forma = Path().apply {
            moveTo(b.left, b.top); lineTo(b.right, b.top)
            quadraticTo(b.right + 3f * U, b.center.y, b.right, b.bottom)
            lineTo(b.left, b.bottom); close()
        }
        drawPath(forma, c(TELA))
        clipPath(forma) {
            // La luz: de día, de la ventana (izquierda); de noche, la lámpara por la derecha.
            if (!am.oscuro) {
                drawRect(Brush.horizontalGradient(listOf(am.claro(0.16f), Color.Transparent, am.sombra(0.18f)), b.left, b.right), b.topLeft, b.size)
            } else {
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = (0.35f * kd).coerceAtMost(0.3f))), b.left + b.width * 0.3f, b.right), b.topLeft, b.size)
            }
            // La trama de la tela y el roce de las manos, más claro.
            var y = b.top + 2f * U
            while (y < b.bottom) { drawLine(am.sombra(0.07f), Offset(b.left, y), Offset(b.right + 4f * U, y), 0.6f * U); y += 2.4f * U }
            // La marca de la humedad, abajo: dos cercos finos y ondulados, el de arriba más oscuro, y la tela algo más pálida
            // solo justo bajo el cerco (como queda la tela que se mojó y secó).
            listOf(0.17f to 0.55f, 0.11f to 0.3f).forEachIndexed { i, (fy, al) ->
                val ola = Path().apply {
                    var x = b.left; var y = b.bottom - b.height * fy
                    moveTo(x, y)
                    var j = 0
                    while (x < b.right + 4f * U) {
                        val nx = x + b.width / 6f
                        val ny = b.bottom - b.height * (fy + (azar(sem, 60 + 10 * i + j) - 0.5f) * 0.035f)
                        quadraticTo(x + b.width / 12f, (y + ny) / 2f + (azar(sem, 80 + 10 * i + j) - 0.5f) * 6f * U, nx, ny)
                        x = nx; y = ny; j++
                    }
                }
                if (i == 0) translate(0f, 1.6f * U) { drawPath(ola, c(TELA, k, 0.12f).copy(alpha = 0.6f), style = Stroke(2.4f * U)) }
                drawPath(ola, c(mezcla(TELA, P.TECA_HONDA, 0.5f), k, -0.1f).copy(alpha = al), style = Stroke(1.1f * U))
            }
            // El cerco de una taza, abajo a la derecha.
            drawOval(c(P.TECA_HONDA).copy(alpha = 0.4f), Offset(b.right - b.width * 0.3f, b.bottom - b.height * 0.42f), Size(b.width * 0.2f, b.width * 0.19f), style = Stroke(1.6f * U))
        }
        // Las puntas de piel, a la derecha, y el filete de oro gastado.
        val pt = minOf(b.width * 0.1f, 16f * U)
        listOf(true, false).forEach { arriba ->
            val y = if (arriba) b.top else b.bottom
            val sg = if (arriba) 1f else -1f
            val punta = poligonoArcano(b.right - pt, y, b.right + 1f * U, y, b.right + 1f * U, y + sg * pt)
            drawPath(punta, c(PIEL, kd, -0.15f))
            drawLine(c(PIEL, kd, 0.35f), Offset(b.right - pt, y), Offset(b.right + 1f * U, y + sg * pt), 1.2f * U)
        }
        val lw = b.width * 0.17f
        val dentro = Rect(b.left + lw + 6f * U, b.top + 6f * U, b.right - 6f * U, b.bottom - 6f * U)
        drawRect(c(P.ORO, k, -0.1f).copy(alpha = 0.5f), dentro.topLeft, dentro.size, style = Stroke(0.9f * U))
        // El lomo de piel, redondo, con sus nervios y el roce del canto.
        val lomo = Rect(b.left, b.top, b.left + lw, b.bottom)
        drawRect(c(PIEL), lomo.topLeft, lomo.size)
        drawRect(c(PIEL, k, 0.22f), Offset(lomo.left + lw * 0.15f, lomo.top), Size(lw * 0.22f, lomo.height))
        drawRect(am.sombra(0.35f), Offset(lomo.right - lw * 0.22f, lomo.top), Size(lw * 0.22f, lomo.height))
        listOf(0.2f, 0.4f, 0.6f, 0.8f).forEach { fy ->
            val y = b.top + b.height * fy
            drawRect(am.sombra(0.4f), Offset(lomo.left, y + 1.6f * U), Size(lw, 1.2f * U))
            drawRect(c(PIEL, k, 0.25f), Offset(lomo.left, y - 0.6f * U), Size(lw, 1.6f * U))
        }
        if (am.oscuro && f > 0.05f) drawRect(am.frio(0.8f * f), lomo.topLeft, Size(1.4f * U, lomo.height))
        // La etiqueta de papel pegada, con su doble filete impreso, el nombre y la cuenta.
        val et = Rect(dentro.left + dentro.width * 0.08f, b.top + b.height * 0.14f, dentro.right - dentro.width * 0.06f, b.top + b.height * 0.5f)
        val kp = if (am.oscuro) maxOf(k, 0.62f) else k
        val papel = am.etiqueta(P.MANILA, kp)
        drawRect(am.sombra(0.35f), Offset(et.left + (if (am.oscuro) -1.6f else 1.6f) * U, et.top + 2f * U), et.size)
        drawRect(papel, et.topLeft, et.size)
        drawRect(am.etiqueta(P.PAPEL_OSC, kp).copy(alpha = 0.4f), Offset(et.left, et.bottom - et.height * 0.14f), Size(et.width, et.height * 0.14f))
        drawPath(irregularArcano(Offset(et.right - et.width * 0.15f, et.top + et.height * 0.25f), et.width * 0.12f, et.height * 0.15f, sem + 7, 9, 0.35f), am.etiqueta(P.PAPEL_OSC, kp).copy(alpha = 0.35f))
        val tinta = if (am.oscuro) Color(0xFF120D08) else P.TINTA
        drawRect(tinta.copy(alpha = 0.55f), et.deflate(3f * U).topLeft, et.deflate(3f * U).size, style = Stroke(0.8f * U))
        drawRect(tinta.copy(alpha = 0.3f), et.deflate(5f * U).topLeft, et.deflate(5f * U).size, style = Stroke(0.5f * U))
        // La esquina despegada de la etiqueta.
        drawPath(poligonoArcano(et.right - 8f * U, et.top, et.right, et.top, et.right, et.top + 7f * U), c(TELA, k, -0.2f))
        drawPath(poligonoArcano(et.right - 8f * U, et.top, et.right, et.top + 7f * U, et.right - 6.6f * U, et.top + 5.6f * U), am.etiqueta(mezcla(P.MANILA, Color.White, 0.25f), kp))
        textoEn(medidor, e.nombre, Rect(et.left + 8f * U, et.top + et.height * 0.14f, et.right - 8f * U, et.top + et.height * 0.6f), P.LETRA, tinta, 15f, peso = FontWeight.Bold, minimo = 7f)
        textoEn(medidor, e.cuenta, Rect(et.left + 8f * U, et.top + et.height * 0.58f, et.right - 8f * U, et.bottom - et.height * 0.12f), P.LETRA, tinta.copy(alpha = 0.85f), 10f, minimo = 6.5f)
        // Los cantos: la luz arriba, la sombra abajo; de noche, el filo de la lámpara en el canto de la derecha.
        drawLine(am.brillo(0.4f, k), Offset(b.left, b.top + 0.6f * U), Offset(b.right, b.top + 0.6f * U), 1.2f * U)
        drawLine(am.sombra(0.4f), Offset(b.left, b.bottom - 0.6f * U), Offset(b.right, b.bottom - 0.6f * U), 1.2f * U)
        if (am.oscuro) drawLine(P.LLAMA.copy(alpha = (0.9f * kd).coerceAtMost(0.8f)), Offset(b.right + 1.4f * U, b.top + 2f * U), Offset(b.right + 1.4f * U, b.bottom - 2f * U), 1.6f * U)
    }

    /** La primera hoja, que se ve al abrir: el papel, un recorte pegado algo torcido, una foto con su esquinero y líneas a mano. */
    private fun DrawScope.primeraHoja(b: Rect, am: AireArcano, k: Float, sem: Int) {
        val u = b.height / 135f
        val hoja = Rect(b.left + 2f * u, b.top + 2f * u, b.right - 2f * u, b.bottom - 2f * u)
        drawRect(am.dia(P.PAPEL, 0f, k), hoja.topLeft, hoja.size)
        clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
            rotate(-3f, hoja.center) { recorte(Rect(hoja.left + hoja.width * 0.1f, hoja.top + hoja.height * 0.08f, hoja.left + hoja.width * 0.55f, hoja.top + hoja.height * 0.5f), am, k, sem + 50, true) }
            val foto = Rect(hoja.left + hoja.width * 0.6f, hoja.top + hoja.height * 0.12f, hoja.right - hoja.width * 0.08f, hoja.top + hoja.height * 0.4f)
            rotate(4f, foto.center) {
                drawRect(am.sombra(0.3f), Offset(foto.left + 1.4f * u, foto.top + 1.6f * u), foto.size)
                drawRect(am.dia(mezcla(P.PAPEL, Color.White, 0.3f), 0f, k), foto.topLeft, foto.size)
                val img = foto.deflate(foto.width * 0.08f)
                drawRect(am.dia(mezcla(P.SALOBRE, P.PAPEL, 0.3f), 0f, k), img.topLeft, img.size)
                drawRect(am.dia(mezcla(P.TECA_OSC, P.SALOBRE, 0.4f), 0f, k), Offset(img.left, img.top + img.height * 0.6f), Size(img.width, img.height * 0.4f))
                drawPath(poligonoArcano(img.left, img.top + img.height * 0.6f, img.left + img.width * 0.6f, img.top + img.height * 0.52f, img.left + img.width * 0.62f, img.top + img.height * 0.6f), am.dia(P.TECA_HONDA, 0f, k))
            }
            val tinta = am.dia(P.TINTA, 0f, k).copy(alpha = 0.7f)
            for (j in 0 until 4) {
                val y = hoja.top + hoja.height * (0.62f + 0.08f * j)
                val p = Path().apply {
                    moveTo(hoja.left + hoja.width * 0.1f, y); var x = hoja.left + hoja.width * 0.1f; var q = 0
                    while (x < hoja.left + hoja.width * (0.55f + 0.3f * azar(sem, 60 + j))) { quadraticTo(x + 3f * u, y + (if (q % 2 == 0) -2f else 2f) * u, x + 6f * u, y); x += 6f * u; q++ }
                }
                drawPath(p, tinta, style = Stroke(0.9f * u))
            }
        }
    }
}
