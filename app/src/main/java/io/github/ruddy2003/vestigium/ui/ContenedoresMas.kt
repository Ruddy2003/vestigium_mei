package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Las libretas del lote 2: el manuscrito andalusí con lacería, el libro plegado en acordeón de la Ruta de la Seda, el
 * rollo de órdenes del yam, el diario de a bordo de los colonos, el herbario de plantas prensadas, el calendario de
 * adviento (cada día escrito abre una casilla) y el libro de mapas con broche de hierro de Tierras Antiguas.
 */
object ContenedoresMas {
    private val PERGAMINO = Color(0xFFF1E6C8)
    private val ORO = Color(0xFFD4AF37)
    private val HIERRO = Color(0xFF4A4E54)
    private val CUERO = Color(0xFF5A3A22)

    fun papel(c: Contenedor, cs: ColorScheme, oscuro: Boolean): Color {
        val claro = when (c) {
            Contenedor.MANUSCRITO -> Color(0xFFF3EAD6)
            Contenedor.ACORDEON -> Color(0xFFF4EEDF)
            Contenedor.ORDENES -> Color(0xFFEFE6D0)
            Contenedor.BITACORA -> Color(0xFFF0E7D2)
            Contenedor.HERBARIO -> Color(0xFFEFEADB)
            Contenedor.ADVIENTO -> Color(0xFFFBF7EE)
            Contenedor.MAPAS -> Color(0xFFE9DCC0)
            Contenedor.EXPEDIENTE -> Color(0xFFF2EFE6)
            // La pantalla: al oscurecerse queda casi negra (Terminal solo existe en oscuro).
            Contenedor.CONSOLA -> Color(0xFF22303A)
            // El papel térmico de los tickets, con el tinte magenta del letrero de enfrente.
            Contenedor.TICKETS -> Color(0xFFEFE3EA)
            Contenedor.CARPETA -> Color(0xFFF6EFD9)
            Contenedor.TECNICO -> Color(0xFFEDEDE8)
            Contenedor.CUADERNO -> Color(0xFFFBFCFD)
            Contenedor.VUELO -> Color(0xFFF4F6FA)
            // Papel sintético impermeable, gris verdoso, de las bitácoras que se mojan.
            Contenedor.INMERSION -> Color(0xFFEDF2EE)
            Contenedor.TETRAD -> Color(0xFFF7F3E6)
            // Las libretas de [Contenedores] tienen su papel allá.
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.LEGAJO, Contenedor.CAHIER -> return Contenedores.papel(c, cs, oscuro)
        }
        return if (oscuro) lerp(claro, Color.Black, 0.76f) else claro
    }

    private fun DrawScope.sombra(o: Offset, s: Size, r: Float) = drawRoundRect(Color.Black.copy(alpha = 0.18f), o, s, CornerRadius(r))

    /** El canto de hojas de un libro cerrado. */
    private fun DrawScope.canto(w: Float, h: Float, grosor: Float, d: Float, papel: Color) {
        drawRoundRect(papel, Offset(grosor, grosor), Size(w, h), CornerRadius(3 * d))
        val n = (grosor / (1.6f * d)).toInt().coerceAtLeast(2)
        for (k in 1..n) { val o = k * grosor / (n + 1); drawLine(Color(0x55806A40), Offset(w + o, grosor + 6 * d), Offset(w + o, h + o - 3 * d), 0.8f * d); drawLine(Color(0x55806A40), Offset(grosor + 6 * d, h + o), Offset(w + o - 3 * d, h + o), 0.8f * d) }
    }

    // ================================================================== cerrado

    fun DrawScope.cerradoMas(c: Contenedor, hojas: Int, tapa: Color, oscuro: Boolean) {
        val d = density
        val grosor = (3f + min(11f, hojas / 12f)) * d
        val w = size.width - grosor; val h = size.height - grosor
        when (c) {
            Contenedor.MANUSCRITO -> {
                // Cuero verde con marco de oro, la estrella de lazo al centro y estrellitas en las esquinas.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 5 * d); canto(w, h, grosor, d, PERGAMINO)
                val piel = Color(0xFF1E4A3A)
                drawRoundRect(Brush.linearGradient(listOf(lerp(piel, Color.White, 0.12f), piel, lerp(piel, Color.Black, 0.25f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(4 * d))
                val m = w * 0.08f
                drawRect(ORO.copy(alpha = 0.7f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.4f * d))
                drawRect(ORO.copy(alpha = 0.4f), Offset(m * 1.6f, m * 1.6f), Size(w - 3.2f * m, h - 3.2f * m), style = Stroke(0.8f * d))
                with(TrofeosMas) {
                    estrellaLazo(Offset(w / 2, h * 0.45f), w * 0.2f, Brush.radialGradient(listOf(lerp(ORO, Color.White, 0.3f), ORO), Offset(w / 2, h * 0.45f), w * 0.2f), Color(0xFF6B4A1A), 1 * d)
                    estrellaLazo(Offset(w / 2, h * 0.45f), w * 0.1f, Brush.linearGradient(listOf(Color(0xFF9E2B25), Color(0xFF6B1A12))), null, 0f)
                    listOf(Offset(m * 2.4f, m * 2.4f), Offset(w - m * 2.4f, m * 2.4f), Offset(m * 2.4f, h - m * 2.4f), Offset(w - m * 2.4f, h - m * 2.4f)).forEach { estrellaLazo(it, w * 0.045f, Brush.linearGradient(listOf(ORO, ORO)), null, 0f) }
                }
                // La solapa (como en los libros de la época) a la derecha.
                drawPath(Path().apply { moveTo(w, h * 0.18f); lineTo(w + grosor + 10 * d, h * 0.3f); lineTo(w + grosor + 10 * d, h * 0.7f); lineTo(w, h * 0.82f); close() }, lerp(piel, Color.Black, 0.2f))
            }
            Contenedor.ACORDEON -> {
                // Paneles plegados en zigzag (más pliegues cuanto más escrito), tapa de seda y la etiqueta vertical.
                val pliegues = (3 + hojas / 10).coerceAtMost(14)
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d)
                for (k in pliegues downTo 1) { val o = k * grosor / (pliegues + 1); drawRect(if (k % 2 == 0) Color(0xFFF4EEDF) else Color(0xFFE6DCC4), Offset(o, o), Size(w, h)) }
                val seda = Color(0xFF1F3A8A)
                drawRect(Brush.linearGradient(listOf(lerp(seda, Color.White, 0.18f), seda, lerp(seda, Color.Black, 0.25f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
                // El brocado: un patrón de nubes en oro tenue.
                for (i in 0 until 4) for (j in 0 until 6) { val cc = Offset(w * (0.15f + i * 0.24f) + if (j % 2 == 0) 0f else w * 0.12f, h * (0.1f + j * 0.16f))
                    drawArc(ORO.copy(alpha = 0.25f), 180f, 180f, false, Offset(cc.x - 6 * d, cc.y - 4 * d), Size(12 * d, 8 * d), style = Stroke(1 * d)) }
                drawRect(Color(0xFFF4EEDF), Offset(w * 0.62f, h * 0.1f), Size(w * 0.18f, h * 0.55f))
                drawRect(Color(0xFFC0392B), Offset(w * 0.62f + 2 * d, h * 0.1f + 2 * d), Size(w * 0.18f - 4 * d, h * 0.55f - 4 * d), style = Stroke(1 * d))
                drawRect(Color(0xFFC0392B), Offset(w * 0.66f, h * 0.52f), Size(w * 0.1f, w * 0.1f))
            }
            Contenedor.ORDENES -> {
                // Un rollo horizontal atado con la bufanda azul; engorda con lo escrito.
                val alto = h * (0.32f + min(0.24f, hojas / 400f))
                val y0 = (size.height - alto) / 2
                sombra(Offset(8 * d, y0 + 8 * d), Size(size.width - 16 * d, alto), alto / 2)
                drawRect(Brush.verticalGradient(listOf(lerp(PERGAMINO, Color.Black, 0.25f), PERGAMINO, lerp(PERGAMINO, Color.White, 0.3f), PERGAMINO, lerp(PERGAMINO, Color.Black, 0.3f)), y0, y0 + alto),
                    Offset(14 * d, y0), Size(size.width - 28 * d, alto))
                listOf(6 * d, size.width - 14 * d).forEach { x -> drawRoundRect(Color(0xFF8A5A2B), Offset(x, y0 - 6 * d), Size(8 * d, alto + 12 * d), CornerRadius(4 * d)); drawCircle(Color(0xFF5A3A22), 5 * d, Offset(x + 4 * d, y0 - 6 * d)); drawCircle(Color(0xFF5A3A22), 5 * d, Offset(x + 4 * d, y0 + alto + 6 * d)) }
                // La bufanda (khadag) azul que lo ata.
                val cielo = Color(0xFF2E6DB4)
                drawRect(cielo, Offset(size.width * 0.44f, y0 - 2 * d), Size(16 * d, alto + 4 * d))
                drawPath(Path().apply { moveTo(size.width * 0.44f + 8 * d, y0 + alto); cubicTo(size.width * 0.5f, y0 + alto + 30 * d, size.width * 0.4f, y0 + alto + 40 * d, size.width * 0.48f, size.height - 4 * d); lineTo(size.width * 0.52f, size.height - 8 * d); cubicTo(size.width * 0.46f, y0 + alto + 36 * d, size.width * 0.56f, y0 + alto + 24 * d, size.width * 0.44f + 16 * d, y0 + alto); close() }, cielo.copy(alpha = 0.85f))
            }
            Contenedor.BITACORA -> {
                // El diario de a bordo: cuero marrón, rosa de los vientos grabada, correa con hebilla.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 4 * d); canto(w, h, grosor, d, PERGAMINO)
                val cuero = Color(0xFF7A4A28)
                drawRoundRect(Brush.linearGradient(listOf(lerp(cuero, Color.White, 0.1f), cuero, lerp(cuero, Color.Black, 0.25f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(4 * d))
                drawRoundRect(Color(0xFF3A2414).copy(alpha = 0.5f), Offset(w * 0.06f, w * 0.06f), Size(w * 0.88f, h - w * 0.12f), CornerRadius(3 * d), style = Stroke(1 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 3 * d))))
                val c = Offset(w / 2, h * 0.4f); val r = w * 0.22f
                for (k in 0 until 8) { val a = k * PI / 4 - PI / 2; val rr = if (k % 2 == 0) r else r * 0.5f
                    drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + (rr * cos(a - 0.2)).toFloat() * 0.3f, c.y + (rr * sin(a - 0.2)).toFloat() * 0.3f); lineTo(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()); lineTo(c.x + (rr * cos(a + 0.2)).toFloat() * 0.3f, c.y + (rr * sin(a + 0.2)).toFloat() * 0.3f); close() }, Color(0xFF3A2414).copy(alpha = 0.45f)) }
                drawCircle(Color(0xFF3A2414).copy(alpha = 0.45f), r * 1.1f, c, style = Stroke(1 * d))
                drawRect(Color(0xFF4A2A14), Offset(w * 0.7f, -2 * d), Size(w * 0.1f, h + grosor + 2 * d))
                drawRect(Color(0xFF4A2A14), Offset(w * 0.7f, h * 0.72f), Size(w * 0.3f + grosor, h * 0.07f))
                drawRect(Color(0xFFB8A070), Offset(w * 0.68f, h * 0.705f), Size(w * 0.14f, h * 0.1f), style = Stroke(2 * d))
            }
            Contenedor.HERBARIO -> {
                // Carpeta de cartón atada con cinta, con una hoja prensada y su etiqueta en la tapa.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d)
                val n = (grosor / (1.8f * d)).toInt().coerceAtLeast(2)
                for (k in n downTo 1) { val o = k * grosor / (n + 1); drawRect(lerp(Color(0xFFEFEADB), Color.Black, 0.04f * (k % 3)), Offset(o, o), Size(w, h)) }
                val carton = Color(0xFF6E7A4E)
                drawRect(Brush.linearGradient(listOf(lerp(carton, Color.White, 0.12f), carton, lerp(carton, Color.Black, 0.2f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
                drawRect(Color(0xFFEFEADB), Offset(w * 0.18f, h * 0.14f), Size(w * 0.64f, h * 0.5f))
                val c = Offset(w / 2, h * 0.39f)
                drawPath(Path().apply { moveTo(c.x, c.y + h * 0.2f); cubicTo(c.x - 4 * d, c.y, c.x + 4 * d, c.y - h * 0.1f, c.x, c.y - h * 0.2f) }, Color(0xFF5A6A3A), style = Stroke(1.2f * d))
                for (k in 0 until 4) { val y = c.y + h * (0.12f - k * 0.08f); listOf(-1f, 1f).forEach { s ->
                    drawPath(Path().apply { moveTo(c.x, y); quadraticTo(c.x + s * w * 0.1f, y - h * 0.05f, c.x + s * w * 0.18f, y - h * 0.01f); quadraticTo(c.x + s * w * 0.08f, y + h * 0.02f, c.x, y); close() }, Color(0xFF6E8A4A).copy(alpha = 0.8f)) } }
                drawRect(Color(0xFFEFEADB), Offset(w * 0.3f, h * 0.7f), Size(w * 0.4f, h * 0.1f))
                drawRect(Color(0xFF3A2A14).copy(alpha = 0.4f), Offset(w * 0.3f, h * 0.7f), Size(w * 0.4f, h * 0.1f), style = Stroke(0.8f * d))
                drawLine(Color(0xFFEFE2C0), Offset(-2 * d, h * 0.56f), Offset(w + grosor, h * 0.56f), 2.4f * d)
                drawCircle(Color(0xFFEFE2C0), 3 * d, Offset(w, h * 0.56f))
            }
            Contenedor.ADVIENTO -> {
                // El calendario de adviento: una casita con 24 casillas; cada día escrito abre una (hasta las 24).
                sombra(Offset(6 * d, 8 * d), Size(size.width - 6 * d, size.height - 6 * d), 4 * d)
                val W = size.width - 6 * d; val H = size.height - 6 * d
                val techo = H * 0.22f
                val casa = Path().apply { moveTo(0f, techo); lineTo(W / 2, 0f); lineTo(W, techo); lineTo(W, H); lineTo(0f, H); close() }
                drawPath(casa, Brush.verticalGradient(listOf(Color(0xFFB3122E), Color(0xFF8E0E24)), 0f, H))
                drawPath(Path().apply { moveTo(0f, techo); lineTo(W / 2, 0f); lineTo(W, techo) }, Color.White.copy(alpha = 0.85f), style = Stroke(4 * d))
                val abiertas = hojas.coerceIn(0, 24)
                val cols = 4; val filas = 6
                val gx = W * 0.1f; val gy = techo + H * 0.04f; val cw = (W - 2 * gx) / cols; val ch = (H - gy - H * 0.06f) / filas
                val orden = listOf(13, 2, 21, 7, 18, 10, 4, 23, 15, 1, 9, 20, 6, 17, 12, 24, 3, 14, 8, 22, 11, 5, 19, 16)
                for (i in 0 until 24) {
                    val col = i % cols; val fila = i / cols
                    val o = Offset(gx + col * cw + cw * 0.08f, gy + fila * ch + ch * 0.08f); val s = Size(cw * 0.84f, ch * 0.84f)
                    val num = orden[i]
                    if (num <= abiertas) {
                        drawRect(Color(0xFF2A1A10), o, s)
                        drawCircle(listOf(Color(0xFFFFD27A), Color(0xFF6FB3E0), Color(0xFFE85A6A))[num % 3], min(s.width, s.height) * 0.2f, Offset(o.x + s.width / 2, o.y + s.height / 2))
                        drawRect(Color(0xFF1F6B3A), Offset(o.x - s.width * 0.18f, o.y), Size(s.width * 0.2f, s.height))
                    } else {
                        drawRect(Color(0xFF1F6B3A), o, s)
                        drawRect(ORO.copy(alpha = 0.7f), o, s, style = Stroke(0.8f * d))
                        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; textSize = min(s.width, s.height) * 0.5f; textAlign = android.graphics.Paint.Align.CENTER; typeface = android.graphics.Typeface.SERIF }
                        drawContext.canvas.nativeCanvas.drawText("$num", o.x + s.width / 2, o.y + s.height * 0.68f, p)
                    }
                }
                val az = java.util.Random(5)
                repeat(14) { drawCircle(Color.White.copy(alpha = 0.7f), (1 + az.nextFloat() * 1.6f) * d, Offset(az.nextFloat() * W, az.nextFloat() * techo * 1.2f)) }
            }
            Contenedor.MAPAS -> {
                // El libro de mapas: cuero gastado, esquinas y broche de hierro con una gema, y una rosa de los vientos.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 4 * d); canto(w, h, grosor, d, Color(0xFFE9DCC0))
                drawRoundRect(Brush.linearGradient(listOf(lerp(CUERO, Color.White, 0.1f), CUERO, lerp(CUERO, Color.Black, 0.3f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(4 * d))
                val e = w * 0.14f
                listOf(Offset(0f, 0f) to 0f, Offset(w, 0f) to 90f, Offset(w, h) to 180f, Offset(0f, h) to 270f).forEach { (o, a) ->
                    val r = Math.toRadians(a.toDouble())
                    fun pt(x: Float, y: Float) = Offset(o.x + (x * cos(r) - y * sin(r)).toFloat(), o.y + (x * sin(r) + y * cos(r)).toFloat())
                    drawPath(Path().apply { moveTo(pt(0f, 0f).x, pt(0f, 0f).y); lineTo(pt(e, 0f).x, pt(e, 0f).y); lineTo(pt(0f, e).x, pt(0f, e).y); close() }, HIERRO)
                }
                val c = Offset(w / 2, h * 0.42f); val r = w * 0.24f
                drawCircle(Color(0xFFC9A04A).copy(alpha = 0.6f), r, c, style = Stroke(1.2f * d))
                for (k in 0 until 4) { val a = k * PI / 2 - PI / 2; drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + (r * 0.2f * cos(a - 0.6)).toFloat(), c.y + (r * 0.2f * sin(a - 0.6)).toFloat()); lineTo(c.x + (r * 1.1f * cos(a)).toFloat(), c.y + (r * 1.1f * sin(a)).toFloat()); lineTo(c.x + (r * 0.2f * cos(a + 0.6)).toFloat(), c.y + (r * 0.2f * sin(a + 0.6)).toFloat()); close() }, Color(0xFFC9A04A).copy(alpha = 0.65f)) }
                with(TrofeosMas) { runas(Offset(w * 0.24f, h * 0.78f), w * 0.52f, h * 0.05f, Color(0xFFC9A04A).copy(alpha = 0.6f), 1.2f * d, 5, 2) }
                drawRect(HIERRO, Offset(w - 6 * d, h * 0.44f), Size(14 * d + grosor, 20 * d))
                drawCircle(Color(0xFF1E7A5A), 5 * d, Offset(w + 2 * d, h * 0.44f + 10 * d))
            }
            Contenedor.EXPEDIENTE -> {
                // La carpeta de cartón con pestaña, la etiqueta mecanografiada, el sello rojo y la grapa.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 2 * d); canto(w, h, grosor, d, Color(0xFFF2EFE6))
                val carton = Color(0xFFD8C08A)
                drawPath(Path().apply { moveTo(0f, h * 0.06f); lineTo(w * 0.36f, h * 0.06f); lineTo(w * 0.42f, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(0f, h); close() },
                    Brush.linearGradient(listOf(lerp(carton, Color.White, 0.12f), carton, lerp(carton, Color.Black, 0.18f)), Offset.Zero, Offset(w, h)))
                drawRect(Color(0xFF141414).copy(alpha = 0.6f), Offset(w * 0.12f, h * 0.18f), Size(w * 0.76f, h * 0.12f), style = Stroke(1.2f * d))
                drawRect(Color(0xFFF2EFE6), Offset(w * 0.13f, h * 0.185f), Size(w * 0.74f, h * 0.11f))
                for (k in 0 until 2) drawLine(Color(0xFF141414).copy(alpha = 0.55f), Offset(w * 0.18f, h * (0.215f + k * 0.045f)), Offset(w * (0.7f - k * 0.18f), h * (0.215f + k * 0.045f)), 1.6f * d)
                val c = Offset(w * 0.64f, h * 0.62f); val r = w * 0.16f
                drawCircle(Color(0xFFC8102E).copy(alpha = 0.75f), r, c, style = Stroke(2.2f * d))
                drawCircle(Color(0xFFC8102E).copy(alpha = 0.55f), r * 0.78f, c, style = Stroke(1f * d))
                drawLine(Color(0xFFC8102E).copy(alpha = 0.75f), Offset(c.x - r * 0.9f, c.y + r * 0.15f), Offset(c.x + r * 0.9f, c.y - r * 0.15f), 2.4f * d)
                drawRect(Color(0xFF8A8D91), Offset(w * 0.06f, h * 0.05f), Size(w * 0.12f, 3 * d))
                drawRect(Color(0xFFC8102E), Offset(0f, h * 0.9f), Size(w, h * 0.03f))
            }
            Contenedor.CONSOLA -> {
                // La consola de registro: el monitor con su pantalla y, a la derecha, el papel continuo de la impresora,
                // que cuelga del alimentador y se apila en pliegues; la pila crece con lo escrito.
                val W = size.width; val H = size.height
                val cian = Color(0xFF00E5FF); val negro = Color(0xFF05070B)
                val papelC = Color(0xFFD9DEE3)
                // El papel continuo: la tira que baja con sus perforaciones y la pila de pliegues en el piso.
                val px = W * 0.74f; val pw = W * 0.24f
                val pliegues = (3 + hojas / 8).coerceAtMost(18)
                val pisoY = H * 0.97f
                sombra(Offset(px + 3 * d, pisoY - pliegues * 2.6f * d + 3 * d), Size(pw, pliegues * 2.6f * d), 1 * d)
                for (k in 0 until pliegues) { val y = pisoY - (k + 1) * 2.6f * d
                    drawRect(if (k % 2 == 0) papelC else lerp(papelC, Color.Black, 0.18f), Offset(px + (if (k % 2 == 0) 0f else 1.5f * d), y), Size(pw, 2.6f * d)) }
                val tope = pisoY - pliegues * 2.6f * d
                drawRect(papelC, Offset(px + pw * 0.1f, H * 0.2f), Size(pw * 0.8f, tope - H * 0.2f))
                var yy = H * 0.22f; while (yy < tope - 3 * d) { listOf(px + pw * 0.18f, px + pw * 0.82f).forEach { xx -> drawCircle(negro.copy(alpha = 0.55f), 1.3f * d, Offset(xx, yy)) }; yy += 7 * d }
                yy = H * 0.26f; var k = 0; while (yy < tope - 6 * d) { drawLine(negro.copy(alpha = 0.35f), Offset(px + pw * 0.3f, yy), Offset(px + pw * (if (k % 3 == 2) 0.55f else 0.72f), yy), 0.8f * d); yy += 5 * d; k++ }
                // El monitor: caja, pantalla negra con el registro en cian y el cursor, y su base.
                sombra(Offset(6 * d, 8 * d), Size(W * 0.74f, H * 0.62f), 6 * d)
                drawRoundRect(Brush.linearGradient(listOf(Color(0xFF2A3644), Color(0xFF1B2430), Color(0xFF111925)), Offset.Zero, Offset(W * 0.74f, H * 0.62f)), Offset.Zero, Size(W * 0.74f, H * 0.62f), CornerRadius(6 * d))
                drawRoundRect(Color(0xFF15404A), Offset.Zero, Size(W * 0.74f, H * 0.62f), CornerRadius(6 * d), style = Stroke(1.2f * d))
                val sx = W * 0.06f; val sy = H * 0.05f; val sw = W * 0.62f; val sh = H * 0.46f
                drawRoundRect(negro, Offset(sx, sy), Size(sw, sh), CornerRadius(4 * d))
                drawRoundRect(Brush.radialGradient(listOf(cian.copy(alpha = 0.14f), Color.Transparent), Offset(sx + sw / 2, sy + sh / 2), sw * 0.7f), Offset(sx, sy), Size(sw, sh), CornerRadius(4 * d))
                with(TrofeosMas) {
                    val tam = sh / 9
                    mono("> registro", sx + 4 * d, sy + tam * 1.4f, tam, cian, negrita = true)
                    listOf(0.82f, 0.64f, 0.9f, 0.5f, 0.72f).forEachIndexed { i, f -> mono("[ok] " + "-".repeat((f * 12).toInt()), sx + 4 * d, sy + tam * (2.8f + i * 1.25f), tam * 0.85f, cian.copy(alpha = 0.7f)) }
                    drawRect(cian, Offset(sx + 4 * d, sy + tam * 9.1f - tam), Size(tam * 0.55f, tam))
                }
                var ys = sy; while (ys < sy + sh) { drawLine(Color.Black.copy(alpha = 0.35f), Offset(sx, ys), Offset(sx + sw, ys), 0.6f * d); ys += 2.4f * d }
                // La ranura del alimentador del papel y la luz de encendido.
                drawRect(negro, Offset(W * 0.7f, H * 0.18f), Size(W * 0.08f, 3 * d))
                drawCircle(cian, 2 * d, Offset(W * 0.64f, H * 0.56f))
                drawPath(Path().apply { moveTo(W * 0.26f, H * 0.62f); lineTo(W * 0.48f, H * 0.62f); lineTo(W * 0.54f, H * 0.7f); lineTo(W * 0.2f, H * 0.7f); close() }, Color(0xFF111925))
            }
            Contenedor.TICKETS -> {
                // El pincho de tickets del puesto: la base de metal, la púa y los tickets ensartados, cada uno un poco
                // girado; la pila crece con lo escrito. Arriba, el reflejo magenta del letrero.
                val W = size.width; val H = size.height
                val papelT = Color(0xFFEFE3EA); val metal = Color(0xFF8E8A96)
                val cx = W * 0.5f; val pisoY = H * 0.94f
                sombra(Offset(cx - W * 0.22f + 3 * d, pisoY - 6 * d), Size(W * 0.44f, 10 * d), 5 * d)
                drawRoundRect(Brush.verticalGradient(listOf(lerp(metal, Color.White, 0.3f), metal, lerp(metal, Color.Black, 0.4f)), pisoY - 8 * d, pisoY + 4 * d),
                    Offset(cx - W * 0.22f, pisoY - 8 * d), Size(W * 0.44f, 12 * d), CornerRadius(6 * d))
                val n = (3 + hojas / 6).coerceAtMost(24)
                val az = java.util.Random(5)
                for (k in 0 until n) {
                    val y = pisoY - 10 * d - k * 3.2f * d
                    val giro = (az.nextFloat() - 0.5f) * 16f
                    rotate(giro, Offset(cx, y)) {
                        val tw = W * 0.6f; val th = 3.2f * d
                        drawRect(if (k % 2 == 0) papelT else lerp(papelT, Color.Black, 0.12f), Offset(cx - tw / 2, y - th), Size(tw, th))
                    }
                }
                // El ticket de arriba, de frente, con su borde dentado y sus renglones.
                val top = pisoY - 10 * d - n * 3.2f * d
                val tx0 = cx - W * 0.3f; val tx1 = cx + W * 0.3f; val ty0 = (top - H * 0.42f).coerceAtLeast(H * 0.06f)
                drawPath(Path().apply {
                    moveTo(tx0, ty0); lineTo(tx1, ty0); lineTo(tx1, top)
                    var x = tx1; var abajo = true; while (x > tx0) { x -= 6 * d; lineTo(maxOf(x, tx0), if (abajo) top - 4 * d else top); abajo = !abajo }
                    close()
                }, papelT)
                with(TrofeosMas) {
                    mono("屋台 · TICKET", cx, ty0 + 14 * d, 10 * d, Color(0xFF3A0D27), android.graphics.Paint.Align.CENTER, negrita = true)
                    var yy = ty0 + 28 * d; var k = 0
                    while (yy < top - 12 * d) { drawLine(Color(0xFF3A0D27).copy(alpha = 0.45f), Offset(tx0 + 10 * d, yy), Offset(tx1 - (if (k % 3 == 2) 40 else 14) * d, yy), 1 * d); yy += 9 * d; k++ }
                }
                // La púa que lo atraviesa.
                drawLine(metal, Offset(cx, pisoY - 8 * d), Offset(cx, ty0 - 10 * d), 2.4f * d)
                drawCircle(lerp(metal, Color.White, 0.4f), 2 * d, Offset(cx, ty0 - 10 * d))
                drawRect(Brush.verticalGradient(listOf(Color(0xFFFF3D9A).copy(alpha = 0.25f), Color.Transparent), 0f, H * 0.3f), Offset.Zero, Size(W, H * 0.3f))
            }
            Contenedor.CARPETA -> {
                // La carpeta del supervisor: cartón verde azulado con su pestaña naranja, las hojas que asoman, el broche
                // de metal arriba y la etiqueta amarilla con el átomo.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d)
                val n = (grosor / (1.8f * d)).toInt().coerceAtLeast(2)
                for (k in n downTo 1) { val o = k * grosor / (n + 1); drawRect(lerp(Color(0xFFF6EFD9), Color.Black, 0.05f * (k % 3)), Offset(o, o), Size(w, h)) }
                val carton = Color(0xFF1C6E6A)
                drawRect(Color(0xFFD9572B), Offset(w * 0.08f, -6 * d), Size(w * 0.24f, 10 * d))
                drawRoundRect(Brush.linearGradient(listOf(lerp(carton, Color.White, 0.14f), carton, lerp(carton, Color.Black, 0.22f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(3 * d))
                // Las esquinas reforzadas.
                listOf(Offset(0f, 0f) to 0f, Offset(w, 0f) to 90f, Offset(w, h) to 180f, Offset(0f, h) to 270f).forEach { (o, a) ->
                    val r = Math.toRadians(a.toDouble()); val e = w * 0.1f
                    fun pt(x: Float, y: Float) = Offset(o.x + (x * cos(r) - y * sin(r)).toFloat(), o.y + (x * sin(r) + y * cos(r)).toFloat())
                    drawPath(Path().apply { moveTo(pt(0f, 0f).x, pt(0f, 0f).y); lineTo(pt(e, 0f).x, pt(e, 0f).y); lineTo(pt(0f, e).x, pt(0f, e).y); close() }, Color(0xFF0C3431))
                }
                // El broche de presión de metal, arriba al centro.
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFD5D8DC), Color(0xFF8A8D91))), Offset(w * 0.3f, -4 * d), Size(w * 0.4f, 16 * d), CornerRadius(4 * d))
                drawCircle(Color(0xFF5A5E64), 3 * d, Offset(w * 0.5f, 4 * d))
                // La etiqueta amarilla: el átomo y los renglones del nombre y del nivel.
                drawRoundRect(Color(0xFFE8B83A), Offset(w * 0.16f, h * 0.2f), Size(w * 0.68f, h * 0.3f), CornerRadius(4 * d))
                drawRoundRect(Color(0xFF1F2A2A).copy(alpha = 0.6f), Offset(w * 0.16f, h * 0.2f), Size(w * 0.68f, h * 0.3f), CornerRadius(4 * d), style = Stroke(1.2f * d))
                with(TrofeosMas) { atomo(Offset(w * 0.3f, h * 0.35f), w * 0.09f, Color(0xFF1F2A2A), 1.2f * d, Color(0xFFD9572B)) }
                for (k in 0 until 3) drawLine(Color(0xFF1F2A2A).copy(alpha = 0.55f), Offset(w * 0.44f, h * (0.28f + k * 0.07f)), Offset(w * (0.76f - k * 0.08f), h * (0.28f + k * 0.07f)), 1.4f * d)
                // La franja de clasificación abajo.
                drawRect(Color(0xFFD9572B), Offset(0f, h * 0.62f), Size(w, h * 0.04f))
            }
            Contenedor.TECNICO -> {
                // El códice técnico: tapas de chapa de acero con el borde remachado, lomo de cuero carmesí, esquineras de
                // latón, el escudo con el engrane al centro y dos bisagras que lo cierran.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d); canto(w, h, grosor, d, Color(0xFFEDEDE8))
                val acero = Color(0xFF6E757D); val carmesi = Color(0xFF9E1B22); val laton = Color(0xFFD6B25A)
                drawRect(Brush.linearGradient(listOf(lerp(acero, Color.White, 0.25f), acero, lerp(acero, Color.Black, 0.3f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
                drawRect(Brush.horizontalGradient(listOf(lerp(carmesi, Color.Black, 0.3f), carmesi, lerp(carmesi, Color.Black, 0.3f)), 0f, w * 0.12f), Offset.Zero, Size(w * 0.12f, h))
                val m = w * 0.06f
                var x = w * 0.18f; while (x < w - m) { drawCircle(Color(0xFF2A2E33), 1.6f * d, Offset(x, m)); drawCircle(Color(0xFF2A2E33), 1.6f * d, Offset(x, h - m)); x += 10 * d }
                var y = m + 10 * d; while (y < h - m) { drawCircle(Color(0xFF2A2E33), 1.6f * d, Offset(w - m, y)); y += 10 * d }
                listOf(Offset(w * 0.12f, 0f) to 0f, Offset(w, 0f) to 90f, Offset(w, h) to 180f, Offset(w * 0.12f, h) to 270f).forEach { (o, a) ->
                    val r = Math.toRadians(a.toDouble()); val e = w * 0.12f
                    fun pt(x: Float, y: Float) = Offset(o.x + (x * cos(r) - y * sin(r)).toFloat(), o.y + (x * sin(r) + y * cos(r)).toFloat())
                    drawPath(Path().apply { moveTo(pt(0f, 0f).x, pt(0f, 0f).y); lineTo(pt(e, 0f).x, pt(e, 0f).y); lineTo(pt(0f, e).x, pt(0f, e).y); close() }, laton)
                }
                // El escudo con el engrane.
                val c = Offset(w * 0.56f, h * 0.4f); val R = w * 0.2f
                val escudo = Path().apply { moveTo(c.x - R, c.y - R); lineTo(c.x + R, c.y - R); lineTo(c.x + R, c.y); quadraticTo(c.x + R, c.y + R * 0.8f, c.x, c.y + R * 1.25f); quadraticTo(c.x - R, c.y + R * 0.8f, c.x - R, c.y); close() }
                drawPath(escudo, carmesi); drawPath(escudo, laton, style = Stroke(1.6f * d))
                for (k in 0 until 8) { val a = k * PI / 4; drawLine(laton, c, Offset(c.x + (R * 0.55f * cos(a)).toFloat(), c.y + (R * 0.55f * sin(a)).toFloat()), 3 * d) }
                drawCircle(laton, R * 0.42f, c); drawCircle(carmesi, R * 0.16f, c)
                // Las bisagras de acero que lo cierran por el canto.
                listOf(0.26f, 0.74f).forEach { f ->
                    drawRect(Color(0xFF4B525A), Offset(w - 8 * d, h * f - 5 * d), Size(10 * d + grosor, 10 * d))
                    drawCircle(Color(0xFF2A2E33), 2 * d, Offset(w - 3 * d, h * f))
                }
            }
            Contenedor.CUADERNO -> {
                // El cuaderno de laboratorio: tapa dura blanca con lomo de tela cian, la etiqueta con sus campos, la molécula
                // impresa y las tiras de cinta de muestras de colores pegadas en el canto.
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 3 * d); canto(w, h, grosor, d, Color(0xFFFBFCFD))
                val cian = Color(0xFF0B8FA1); val violeta = Color(0xFF5E4FD1); val verde = Color(0xFF1E8E5A); val tinta = Color(0xFF12202B)
                drawRect(Brush.linearGradient(listOf(Color.White, Color(0xFFE6EDF2), Color(0xFFCBD6DF)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h))
                drawRect(Brush.horizontalGradient(listOf(lerp(cian, Color.Black, 0.25f), cian, lerp(cian, Color.Black, 0.2f)), 0f, w * 0.14f), Offset.Zero, Size(w * 0.14f, h))
                // La etiqueta: proyecto, número y fechas.
                drawRect(Color.White, Offset(w * 0.24f, h * 0.12f), Size(w * 0.64f, h * 0.3f))
                drawRect(tinta.copy(alpha = 0.6f), Offset(w * 0.24f, h * 0.12f), Size(w * 0.64f, h * 0.3f), style = Stroke(1 * d))
                for (k in 1..3) drawLine(tinta.copy(alpha = 0.3f), Offset(w * 0.27f, h * (0.12f + k * 0.075f)), Offset(w * 0.85f, h * (0.12f + k * 0.075f)), 0.8f * d)
                drawRect(cian, Offset(w * 0.24f, h * 0.12f), Size(w * 0.64f, h * 0.04f))
                // La molécula de la portada.
                val a = Offset(w * 0.46f, h * 0.56f); val b = Offset(w * 0.66f, h * 0.52f); val c = Offset(w * 0.58f, h * 0.68f)
                listOf(a to b, b to c, c to a).forEach { (u, v) -> drawLine(tinta.copy(alpha = 0.5f), u, v, 1.6f * d) }
                drawCircle(cian, w * 0.05f, a); drawCircle(violeta, w * 0.06f, b); drawCircle(verde, w * 0.045f, c)
                // La cinta de muestras: tiras de colores que asoman por el canto, con su rayita escrita.
                listOf(0.2f to cian, 0.34f to violeta, 0.48f to verde, 0.8f to Color(0xFFD9572B)).forEach { (f, col) ->
                    drawRect(col, Offset(w * 0.86f, h * f), Size(w * 0.14f + grosor + 4 * d, h * 0.05f))
                    drawLine(Color.White.copy(alpha = 0.8f), Offset(w * 0.9f, h * (f + 0.025f)), Offset(w * 0.98f, h * (f + 0.025f)), 1 * d)
                }
            }
            Contenedor.VUELO -> {
                // La bitácora de vuelo: carpeta de tela azul marino con anillos arriba, la franja reflejante, la cinta de
                // velcro y el parche bordado de la misión con su cohete.
                val marino = Color(0xFF1B3A8C); val naranja = Color(0xFFE8601C)
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 4 * d)
                val n = (grosor / (1.8f * d)).toInt().coerceAtLeast(2)
                for (k in n downTo 1) { val o = k * grosor / (n + 1); drawRect(lerp(Color(0xFFF4F6FA), Color.Black, 0.05f * (k % 3)), Offset(o, o), Size(w, h)) }
                drawRoundRect(Brush.linearGradient(listOf(lerp(marino, Color.White, 0.12f), marino, lerp(marino, Color.Black, 0.3f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(5 * d))
                // La textura de la tela.
                var y = 0f; while (y < h) { drawLine(Color.White.copy(alpha = 0.04f), Offset(0f, y), Offset(w, y), 1f); y += 2.5f * d }
                // Los anillos de la encuadernación de arriba.
                listOf(0.3f, 0.7f).forEach { f -> drawArc(Color(0xFFC3CCDA), 180f, 180f, false, Offset(w * f - 6 * d, -6 * d), Size(12 * d, 12 * d), style = Stroke(2.4f * d)) }
                drawRect(Brush.horizontalGradient(listOf(Color(0xFFC3CCDA), Color.White, Color(0xFF8E9AB8))), Offset(0f, h * 0.12f), Size(w, h * 0.04f))
                // La cinta de velcro y el parche de la misión.
                drawRoundRect(Color(0xFF26324F), Offset(w * 0.18f, h * 0.26f), Size(w * 0.64f, h * 0.38f), CornerRadius(4 * d))
                var x = w * 0.2f; while (x < w * 0.8f) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(x, h * 0.27f), Offset(x, h * 0.63f), 1f); x += 2 * d }
                val c = Offset(w / 2, h * 0.45f); val R = w * 0.18f
                drawCircle(naranja, R, c); drawCircle(Color(0xFF0A0F1E), R * 0.84f, c)
                val az = java.util.Random(8); repeat(10) { drawCircle(Color.White.copy(alpha = 0.8f), (0.6f + az.nextFloat()) * d, Offset(c.x + (az.nextFloat() - 0.5f) * R * 1.3f, c.y + (az.nextFloat() - 0.5f) * R * 1.3f)) }
                drawPath(Path().apply { moveTo(c.x, c.y - R * 0.6f); lineTo(c.x + R * 0.14f, c.y - R * 0.3f); lineTo(c.x + R * 0.14f, c.y + R * 0.3f); lineTo(c.x - R * 0.14f, c.y + R * 0.3f); lineTo(c.x - R * 0.14f, c.y - R * 0.3f); close() }, Color.White)
                rotate(-20f, c) { drawOval(Color(0xFF8AB4FF), Offset(c.x - R * 0.7f, c.y - R * 0.15f), Size(R * 1.4f, R * 0.4f), style = Stroke(1.2f * d)) }
                // La lengüeta naranja para tirar.
                drawRect(naranja, Offset(w - 4 * d, h * 0.74f), Size(8 * d + grosor, 14 * d))
            }
            Contenedor.INMERSION -> {
                // La bitácora del batiscafo: tapas de lona engomada azul petróleo con cantoneras de latón, el batiscafo
                // estampado (el flotador largo y, debajo, la esfera de la tripulación con su ojo de buey), la regla de
                // profundidad en el lomo y la correa de goma que la mantiene cerrada bajo el agua.
                val lona = Color(0xFF1F4A59); val laton = Color(0xFFB8913E); val crema = Color(0xFFE6DFC6)
                sombra(Offset(grosor + 4 * d, grosor + 6 * d), Size(w, h), 4 * d); canto(w, h, grosor, d, Color(0xFFEDF2EE))
                drawRoundRect(Brush.linearGradient(listOf(lerp(lona, Color.White, 0.1f), lona, lerp(lona, Color.Black, 0.35f)), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), CornerRadius(4 * d))
                // El grano de la lona.
                var y = 0f; while (y < h) { drawLine(Color.Black.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y), 1f); y += 3 * d }
                // La regla de profundidad en el lomo: una raya cada 100 m y una más larga cada 500.
                drawRect(lerp(lona, Color.Black, 0.3f), Offset.Zero, Size(w * 0.12f, h))
                var k = 0; y = h * 0.06f
                while (y < h * 0.94f) { drawLine(crema.copy(alpha = 0.6f), Offset(0f, y), Offset(w * (if (k % 5 == 0) 0.09f else 0.05f), y), 0.9f * d); y += h * 0.03f; k++ }
                // Las cuatro cantoneras de latón.
                listOf(Offset(0f, 0f) to 0, Offset(w, 0f) to 1, Offset(w, h) to 2, Offset(0f, h) to 3).forEach { (p, i) ->
                    val sx = if (i == 1 || i == 2) -1f else 1f; val sy = if (i >= 2) -1f else 1f; val l = w * 0.13f
                    drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x + sx * l, p.y); lineTo(p.x, p.y + sy * l); close() }, Brush.linearGradient(listOf(lerp(laton, Color.White, 0.35f), laton, lerp(laton, Color.Black, 0.3f)), p, Offset(p.x + sx * l, p.y + sy * l)))
                    drawCircle(lerp(laton, Color.Black, 0.4f), 1.4f * d, Offset(p.x + sx * l * 0.3f, p.y + sy * l * 0.3f))
                }
                // El batiscafo estampado en crema: el flotador, la torreta, la esfera colgada y su ojo de buey.
                val cx = w * 0.56f; val cy = h * 0.42f; val L = w * 0.3f
                val tinta = crema.copy(alpha = 0.8f)
                drawRoundRect(tinta, Offset(cx - L, cy - L * 0.18f), Size(L * 2, L * 0.36f), CornerRadius(L * 0.18f), style = Stroke(1.4f * d))
                drawRect(tinta, Offset(cx - L * 0.12f, cy - L * 0.36f), Size(L * 0.24f, L * 0.18f), style = Stroke(1.2f * d))
                drawLine(tinta, Offset(cx, cy + L * 0.18f), Offset(cx, cy + L * 0.3f), 1.2f * d)
                drawCircle(tinta, L * 0.26f, Offset(cx, cy + L * 0.56f), style = Stroke(1.4f * d))
                drawCircle(tinta, L * 0.09f, Offset(cx - L * 0.06f, cy + L * 0.6f))
                listOf(-0.7f, 0.7f).forEach { f -> drawLine(tinta, Offset(cx + f * L, cy + L * 0.18f), Offset(cx + f * L * 1.1f, cy + L * 0.34f), 1 * d) }
                // La placa con el número de la inmersión, en latón.
                drawRoundRect(laton, Offset(w * 0.32f, h * 0.72f), Size(w * 0.48f, h * 0.1f), CornerRadius(2 * d))
                with(TrofeosMas) { mono("INMERSIÓN", w * 0.56f, h * 0.79f, h * 0.05f, lerp(lona, Color.Black, 0.4f), android.graphics.Paint.Align.CENTER, negrita = true) }
                // La correa de goma negra que la cierra.
                drawRect(Color(0xFF111A1E), Offset(w * 0.84f, -2 * d), Size(w * 0.05f, h + grosor + 4 * d))
            }
            Contenedor.TETRAD -> {
                // El cuaderno escolar (тетрадь): tapa delgada de papel verde con la etiqueta enmarcada y su palabra en
                // ruso, la estrella roja, el lomo con las grapas y, como engorda poco, pocas hojas en el canto.
                val verde = Color(0xFF4E7A5A); val rojo = Color(0xFFC1121F); val tinta = Color(0xFF161414)
                val g = min(grosor, 6 * d)
                sombra(Offset(g + 4 * d, g + 6 * d), Size(size.width - g, size.height - g), 2 * d)
                drawRect(Color(0xFFF7F3E6), Offset(g, g), Size(size.width - g, size.height - g))
                val W = size.width - g; val H = size.height - g
                drawRect(Brush.linearGradient(listOf(lerp(verde, Color.White, 0.15f), verde, lerp(verde, Color.Black, 0.15f)), Offset.Zero, Offset(W, H)), Offset.Zero, Size(W, H))
                // El patrón impreso de la tapa: rombos tenues.
                var y = 0f; var k = 0; while (y < H) { var x = if (k % 2 == 0) 0f else 7 * d; while (x < W) { drawCircle(Color.White.copy(alpha = 0.08f), 1.4f * d, Offset(x, y)); x += 14 * d }; y += 7 * d; k++ }
                // La etiqueta con marco doble, la palabra impresa y los renglones para el nombre.
                drawRect(Color(0xFFF7F3E6), Offset(W * 0.16f, H * 0.16f), Size(W * 0.68f, H * 0.38f))
                drawRect(tinta.copy(alpha = 0.7f), Offset(W * 0.16f + 3 * d, H * 0.16f + 3 * d), Size(W * 0.68f - 6 * d, H * 0.38f - 6 * d), style = Stroke(1 * d))
                val pt = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(22, 20, 20); textSize = W * 0.1f; textAlign = android.graphics.Paint.Align.CENTER; typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD); letterSpacing = 0.12f }
                drawContext.canvas.nativeCanvas.drawText("ТЕТРАДЬ", W / 2, H * 0.27f, pt)
                for (j in 0 until 3) drawLine(tinta.copy(alpha = 0.45f), Offset(W * 0.24f, H * (0.34f + j * 0.06f)), Offset(W * 0.76f, H * (0.34f + j * 0.06f)), 0.8f * d)
                with(TrofeosTema) { estrella(Offset(W * 0.5f, H * 0.66f), W * 0.09f, W * 0.036f, 5, Brush.linearGradient(listOf(rojo, rojo))) }
                // El lomo con sus dos grapas.
                drawRect(lerp(verde, Color.Black, 0.25f), Offset(0f, 0f), Size(W * 0.05f, H))
                listOf(0.3f, 0.7f).forEach { f -> drawRect(Color(0xFFC3C6CA), Offset(W * 0.015f, H * f - 6 * d), Size(2 * d, 12 * d)) }
            }
            // Estas las dibuja [Contenedores].
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.LEGAJO, Contenedor.CAHIER -> {}
        }
    }

    // ================================================================== abierto

    fun DrawScope.fondoHojaMas(c: Contenedor, cs: ColorScheme, oscuro: Boolean, renglon: Float, margen: Float) {
        val d = density
        val tinta = if (oscuro) Color.White else Color.Black
        when (c) {
            Contenedor.MANUSCRITO -> {
                // Marco de doble filete en oro y la estrella al centro de la cabecera.
                drawRect(ORO.copy(alpha = 0.5f), Offset(margen - 14 * d, 70 * d), Size(size.width - margen + 4 * d, size.height - 80 * d), style = Stroke(1.2f * d))
                drawRect(ORO.copy(alpha = 0.3f), Offset(margen - 10 * d, 74 * d), Size(size.width - margen - 4 * d, size.height - 88 * d), style = Stroke(0.6f * d))
                with(TrofeosMas) { estrellaLazo(Offset(size.width / 2 + margen / 2, 70 * d), 8 * d, Brush.linearGradient(listOf(ORO, ORO)), null, 0f) }
                var y = 96 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFF8A6A44).copy(alpha = 0.1f), Offset(margen, y), Offset(size.width - 16 * d, y), 0.6f * d); y += renglon }
            }
            Contenedor.ACORDEON -> {
                // Los pliegues verticales y un sello de cinabrio en la esquina.
                var x = margen + 70 * d; while (x < size.width) { drawLine(tinta.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, size.height), 1.2f * d); drawRect(Brush.horizontalGradient(listOf(tinta.copy(alpha = 0.05f), Color.Transparent), x, x + 12 * d), Offset(x, 0f), Size(12 * d, size.height)); x += 90 * d }
                drawRect(Color(0xFFC0392B).copy(alpha = 0.35f), Offset(size.width - 34 * d, size.height - 40 * d), Size(20 * d, 20 * d))
            }
            Contenedor.ORDENES -> {
                // Columnas verticales tenues (como la escritura vertical) y el margen de fieltro azul.
                var x = margen; while (x < size.width) { drawLine(Color(0xFF2E6DB4).copy(alpha = 0.08f), Offset(x, 96 * d), Offset(x, size.height), 0.8f * d); x += renglon }
                var y = 96 * d; while (y < size.height) { drawLine(tinta.copy(alpha = 0.05f), Offset(margen, y), Offset(size.width, y), 0.5f * d); y += renglon }
                drawRect(Color(0xFF2E6DB4).copy(alpha = 0.18f), Offset(0f, 0f), Size(size.width, 6 * d))
            }
            Contenedor.BITACORA -> {
                // Renglones azules, la columna de la hora en rojo y la cabecera.
                var y = 96 * d; while (y < size.height) { drawLine(Color(0xFF3A5A8A).copy(alpha = 0.18f), Offset(0f, y), Offset(size.width, y), 0.8f * d); y += renglon }
                drawLine(Color(0xFFB0281E).copy(alpha = 0.4f), Offset(margen - 8 * d, 0f), Offset(margen - 8 * d, size.height), 1 * d)
                drawLine(Color(0xFFB0281E).copy(alpha = 0.25f), Offset(margen + 40 * d, 80 * d), Offset(margen + 40 * d, size.height), 0.8f * d)
            }
            Contenedor.HERBARIO -> {
                // Una hoja como marca de agua y la etiqueta del espécimen abajo a la derecha.
                val c = Offset(size.width * 0.72f, size.height * 0.45f)
                drawPath(Path().apply { moveTo(c.x, c.y + 120 * d); cubicTo(c.x - 60 * d, c.y + 20 * d, c.x - 40 * d, c.y - 80 * d, c.x, c.y - 120 * d); cubicTo(c.x + 40 * d, c.y - 80 * d, c.x + 60 * d, c.y + 20 * d, c.x, c.y + 120 * d); close() },
                    Color(0xFF5A7A3A).copy(alpha = if (oscuro) 0.08f else 0.06f))
                drawLine(Color(0xFF5A7A3A).copy(alpha = 0.08f), Offset(c.x, c.y - 120 * d), Offset(c.x, c.y + 140 * d), 1.4f * d)
                val e = Rect(size.width - 140 * d, size.height - 70 * d, size.width - 16 * d, size.height - 16 * d)
                drawRect(tinta.copy(alpha = 0.12f), e.topLeft, e.size, style = Stroke(0.8f * d))
                for (k in 1..3) drawLine(tinta.copy(alpha = 0.08f), Offset(e.left + 6 * d, e.top + k * e.height / 4), Offset(e.right - 6 * d, e.top + k * e.height / 4), 0.6f * d)
                var y = 96 * d; while (y < size.height - 80 * d) { drawLine(tinta.copy(alpha = 0.04f), Offset(margen, y), Offset(size.width - 16 * d, y), 0.5f * d); y += renglon }
            }
            Contenedor.ADVIENTO -> {
                // Una cenefa de copos y pinos arriba, y la cinta roja y verde abajo.
                var x = 8 * d; var k = 0
                while (x < size.width) {
                    if (k % 2 == 0) drawPath(Path().apply { moveTo(x - 6 * d, 30 * d); lineTo(x, 14 * d); lineTo(x + 6 * d, 30 * d); close() }, Color(0xFF1F6B3A).copy(alpha = 0.35f))
                    else for (j in 0 until 3) { val a = j * PI / 3; drawLine(Color(0xFF6FB3E0).copy(alpha = 0.45f), Offset(x - (6 * d * cos(a)).toFloat(), 22 * d - (6 * d * sin(a)).toFloat()), Offset(x + (6 * d * cos(a)).toFloat(), 22 * d + (6 * d * sin(a)).toFloat()), 1 * d) }
                    x += 22 * d; k++
                }
                drawRect(Color(0xFFB3122E).copy(alpha = 0.35f), Offset(0f, size.height - 10 * d), Size(size.width, 4 * d))
                drawRect(Color(0xFF1F6B3A).copy(alpha = 0.35f), Offset(0f, size.height - 6 * d), Size(size.width, 4 * d))
                var y = 96 * d; while (y < size.height - 20 * d) { drawLine(Color(0xFFB3122E).copy(alpha = 0.07f), Offset(margen, y), Offset(size.width - 12 * d, y), 0.6f * d); y += renglon }
            }
            Contenedor.MAPAS -> {
                // Curvas de nivel y una rosa de los vientos tenue.
                val az = java.util.Random(21)
                repeat(4) { k ->
                    val c = Offset(size.width * (0.3f + az.nextFloat() * 0.5f), size.height * (0.2f + az.nextFloat() * 0.6f))
                    for (j in 1..4) drawOval(Color(0xFF6B5A44).copy(alpha = if (oscuro) 0.12f else 0.08f), Offset(c.x - j * 22 * d, c.y - j * 14 * d), Size(j * 44 * d, j * 28 * d), style = Stroke(0.8f * d))
                }
                val r = 30 * d; val c = Offset(size.width - 50 * d, 130 * d)
                for (k in 0 until 8) { val a = k * PI / 4; val rr = if (k % 2 == 0) r else r * 0.5f; drawLine(Color(0xFF6B5A44).copy(alpha = 0.18f), c, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()), 1 * d) }
                drawCircle(Color(0xFF6B5A44).copy(alpha = 0.15f), r * 0.7f, c, style = Stroke(0.8f * d))
            }
            Contenedor.EXPEDIENTE -> {
                // Formulario oficial: renglones grises, casillas en el margen, el folio arriba y la franja roja.
                drawRect(Color(0xFFC8102E).copy(alpha = 0.45f), Offset(0f, 0f), Size(size.width, 4 * d))
                drawRect(tinta.copy(alpha = 0.15f), Offset(margen - 10 * d, 74 * d), Size(size.width - margen - 6 * d, size.height - 90 * d), style = Stroke(0.8f * d))
                var y = 96 * d; var k = 0
                while (y < size.height - 24 * d) {
                    drawLine(tinta.copy(alpha = 0.07f), Offset(margen, y), Offset(size.width - 20 * d, y), 0.6f * d)
                    if (k % 4 == 0) drawRect(tinta.copy(alpha = 0.18f), Offset(margen - 26 * d, y - 12 * d), Size(10 * d, 10 * d), style = Stroke(0.8f * d))
                    y += renglon; k++
                }
            }
            Contenedor.CONSOLA -> {
                // La pantalla de registro: líneas de barrido, la columna con el número de cada línea y la barra de estado arriba.
                val cian = Color(0xFF00E5FF)
                var y = 0f; while (y < size.height) { drawLine(cian.copy(alpha = 0.03f), Offset(0f, y), Offset(size.width, y), 1f); y += 3 * d }
                drawRect(cian.copy(alpha = 0.1f), Offset(0f, 0f), Size(size.width, 12 * d))
                with(TrofeosMas) {
                    mono("registro.log  -- solo lectura --", 26 * d, 9 * d, 8 * d, cian.copy(alpha = 0.5f))
                    drawLine(cian.copy(alpha = 0.2f), Offset(24 * d, 12 * d), Offset(24 * d, size.height), 0.8f * d)
                    var yy = 96 * d; var k = 1
                    while (yy < size.height - 8 * d) { mono("%03d".format(k), 21 * d, yy, 8 * d, cian.copy(alpha = 0.35f), android.graphics.Paint.Align.RIGHT); yy += renglon; k++ }
                }
            }
            Contenedor.TICKETS -> {
                // El ticket térmico: la cabecera del puesto, el número de orden, renglones de puntos y el borde dentado abajo.
                val tinta2 = if (oscuro) Color(0xFFFF8AC2) else Color(0xFF3A0D27)
                with(TrofeosMas) {
                    mono("屋台 · PUESTO DE FIDEOS · NIVEL −3", size.width / 2, 22 * d, 9 * d, tinta2.copy(alpha = 0.7f), android.graphics.Paint.Align.CENTER, negrita = true)
                    mono("ORDEN Nº ${(size.height / 7).toInt() % 900 + 100}", size.width / 2, 38 * d, 8 * d, tinta2.copy(alpha = 0.5f), android.graphics.Paint.Align.CENTER)
                }
                drawLine(tinta2.copy(alpha = 0.4f), Offset(12 * d, 50 * d), Offset(size.width - 12 * d, 50 * d), 1 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d)))
                var y = 96 * d
                while (y < size.height - 16 * d) { drawLine(tinta.copy(alpha = 0.08f), Offset(margen, y), Offset(size.width - 12 * d, y), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * d, 3 * d))); y += renglon }
                var x = 0f; var arriba = true
                val diente = Path().apply { moveTo(0f, size.height); while (x < size.width) { x += 6 * d; lineTo(x, if (arriba) size.height - 5 * d else size.height); arriba = !arriba }; lineTo(size.width, size.height); close() }
                drawPath(diente, Color.Black.copy(alpha = 0.35f))
            }
            Contenedor.CARPETA -> {
                // El formato del refugio: cabecera con casillas de turno y nivel, renglones verde azulado y la franja naranja.
                val linea = if (oscuro) Color(0xFF3DFF7F) else Color(0xFF1C6E6A)
                drawRect(Color(0xFFD9572B).copy(alpha = 0.5f), Offset(0f, 0f), Size(size.width, 5 * d))
                drawRect(linea.copy(alpha = 0.35f), Offset(margen - 10 * d, 72 * d), Size(size.width - margen - 2 * d, 18 * d), style = Stroke(0.9f * d))
                listOf(0.45f, 0.7f).forEach { f -> val x = margen - 10 * d + (size.width - margen - 2 * d) * f; drawLine(linea.copy(alpha = 0.35f), Offset(x, 72 * d), Offset(x, 90 * d), 0.9f * d) }
                var y = 96 * d + renglon; while (y < size.height - 16 * d) { drawLine(linea.copy(alpha = 0.14f), Offset(margen - 10 * d, y), Offset(size.width - 12 * d, y), 0.7f * d); y += renglon }
                with(TrofeosMas) { atomo(Offset(size.width - 40 * d, size.height - 50 * d), 26 * d, linea.copy(alpha = if (oscuro) 0.1f else 0.07f), 2 * d) }
            }
            Contenedor.TECNICO -> {
                // La lámina técnica: cuadrícula fina de dibujo, el margen carmesí y el cajetín de rótulo abajo a la derecha.
                val linea = if (oscuro) Color(0xFF8FB3C8) else Color(0xFF2F4858)
                var x = margen - 10 * d; while (x < size.width) { drawLine(linea.copy(alpha = 0.06f), Offset(x, 70 * d), Offset(x, size.height), 0.5f * d); x += renglon / 3 }
                var y = 70 * d; var k = 0; while (y < size.height) { drawLine(linea.copy(alpha = if (k % 3 == 0) 0.12f else 0.05f), Offset(margen - 10 * d, y), Offset(size.width, y), 0.5f * d); y += renglon / 3; k++ }
                drawLine(Color(0xFF9E1B22).copy(alpha = 0.45f), Offset(margen - 10 * d, 0f), Offset(margen - 10 * d, size.height), 1.2f * d)
                val r = Rect(size.width - 150 * d, size.height - 56 * d, size.width - 12 * d, size.height - 12 * d)
                drawRect(linea.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(1f * d))
                drawLine(linea.copy(alpha = 0.3f), Offset(r.left, r.top + r.height / 2), Offset(r.right, r.top + r.height / 2), 0.8f * d)
                drawLine(linea.copy(alpha = 0.3f), Offset(r.left + r.width * 0.6f, r.top), Offset(r.left + r.width * 0.6f, r.bottom), 0.8f * d)
            }
            Contenedor.CUADERNO -> {
                // La hoja cuadriculada del cuaderno de laboratorio, con el pie de «firma y testigo» y una tira de cinta de muestras.
                val cuadro = if (oscuro) Color(0xFF3CD2E5) else Color(0xFF0B8FA1)
                val paso = renglon / 2
                var x = 0f; while (x < size.width) { drawLine(cuadro.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, size.height), 0.5f * d); x += paso }
                var y = 0f; while (y < size.height) { drawLine(cuadro.copy(alpha = 0.08f), Offset(0f, y), Offset(size.width, y), 0.5f * d); y += paso }
                val pie = size.height - 30 * d
                listOf(0.08f to 0.46f, 0.54f to 0.92f).forEach { (a, b) -> drawLine(tinta.copy(alpha = 0.3f), Offset(size.width * a, pie), Offset(size.width * b, pie), 0.8f * d) }
                drawRect(Color(0xFF5E4FD1).copy(alpha = 0.35f), Offset(size.width - 70 * d, 18 * d), Size(56 * d, 12 * d))
                drawLine(Color.White.copy(alpha = 0.7f), Offset(size.width - 64 * d, 24 * d), Offset(size.width - 22 * d, 24 * d), 1 * d)
            }
            Contenedor.VUELO -> {
                // La hoja de la lista de vuelo: la barra marina con la franja naranja, la casilla de cada renglón y la marca de tiempo.
                val marino = if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)
                drawRect(marino.copy(alpha = 0.22f), Offset(0f, 0f), Size(size.width, 10 * d))
                drawRect(Color(0xFFE8601C).copy(alpha = 0.6f), Offset(0f, 10 * d), Size(size.width, 2 * d))
                with(TrofeosMas) { mono("T+ 00:00:00", size.width - 12 * d, 30 * d, 8 * d, marino.copy(alpha = 0.5f), android.graphics.Paint.Align.RIGHT) }
                var y = 96 * d; while (y < size.height - 12 * d) {
                    drawLine(marino.copy(alpha = 0.1f), Offset(margen - 10 * d, y), Offset(size.width - 12 * d, y), 0.7f * d)
                    drawRect(marino.copy(alpha = 0.25f), Offset(13 * d, y - 12 * d), Size(9 * d, 9 * d), style = Stroke(0.8f * d))
                    y += renglon
                }
            }
            Contenedor.INMERSION -> {
                // La hoja de inmersión: el encabezado con cota y presión por llenar, la regla de profundidad en el margen
                // (cada renglón, diez metros más abajo) y, de fondo, el perfil del fondo marino que trazó el sonar.
                val tinta = if (oscuro) Color(0xFF5CF2C6) else Color(0xFF0A5C8C)
                with(TrofeosMas) { mono("COTA ____ m   PRESIÓN ____ bar", margen, 30 * d, 8 * d, tinta.copy(alpha = 0.5f)) }
                drawLine(tinta.copy(alpha = 0.3f), Offset(margen, 38 * d), Offset(size.width - 12 * d, 38 * d), 0.8f * d)
                val perfil = Path(); var x = margen; perfil.moveTo(margen, size.height * 0.7f)
                while (x <= size.width) { perfil.lineTo(x, size.height * 0.7f + 18 * d * sin(x / (47 * d)) + 9 * d * sin(x / (17 * d))); x += 4 * d }
                drawPath(perfil, tinta.copy(alpha = if (oscuro) 0.12f else 0.08f), style = Stroke(1.4f * d))
                var y = 96 * d; var k = 1
                while (y < size.height - 12 * d) {
                    drawLine(tinta.copy(alpha = 0.1f), Offset(margen - 6 * d, y), Offset(size.width - 12 * d, y), 0.7f * d)
                    drawLine(tinta.copy(alpha = 0.3f), Offset(margen - 18 * d, y), Offset(margen - 8 * d, y), 0.8f * d)
                    if (k % 2 == 0) with(TrofeosMas) { mono("${k * 10}", margen - 20 * d, y - 2 * d, 6 * d, tinta.copy(alpha = 0.35f), android.graphics.Paint.Align.RIGHT) }
                    y += renglon; k++
                }
            }
            Contenedor.TETRAD -> {
                // La hoja «en cuadritos» del cuaderno escolar ruso, con el margen rojo del lado de afuera (a la derecha).
                val cuadro = if (oscuro) Color(0xFF7A9AC8) else Color(0xFF5A7AB0)
                val paso = renglon / 2
                var x = 0f; while (x < size.width) { drawLine(cuadro.copy(alpha = 0.12f), Offset(x, 0f), Offset(x, size.height), 0.5f * d); x += paso }
                var y = 0f; while (y < size.height) { drawLine(cuadro.copy(alpha = 0.12f), Offset(0f, y), Offset(size.width, y), 0.5f * d); y += paso }
                drawLine(Color(0xFFC1121F).copy(alpha = 0.55f), Offset(size.width - 26 * d, 0f), Offset(size.width - 26 * d, size.height), 1.2f * d)
            }
            // Estas las dibuja [Contenedores].
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.LEGAJO, Contenedor.CAHIER -> {}
        }
    }

    fun DrawScope.encuadernacionMas(c: Contenedor, cs: ColorScheme, oscuro: Boolean) {
        val d = density
        when (c) {
            Contenedor.MANUSCRITO -> {
                // El pliegue del cuadernillo con la cabezada de seda: un zigzag de puntadas rojas y verdes y puntos de oro.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), 10 * d, 34 * d), Offset(10 * d, 4 * d), Size(24 * d, size.height - 8 * d))
                var y = 14 * d; var k = 0
                while (y < size.height - 14 * d) {
                    val col = if (k % 2 == 0) Color(0xFF9E2B25) else Color(0xFF1E6B5C)
                    drawLine(col.copy(alpha = 0.8f), Offset(12 * d, y), Offset(17 * d, y + 5 * d), 1.4f * d, StrokeCap.Round)
                    drawLine(col.copy(alpha = 0.8f), Offset(17 * d, y + 5 * d), Offset(22 * d, y), 1.4f * d, StrokeCap.Round)
                    if (k % 3 == 0) drawCircle(ORO.copy(alpha = 0.8f), 1.3f * d, Offset(17 * d, y + 9 * d))
                    y += 12 * d; k++
                }
            }
            Contenedor.MAPAS -> {
                // La tira de cuero clavada al lomo, con sus anillas de hierro y los clavos.
                val cuero = if (oscuro) lerp(CUERO, Color.Black, 0.35f) else CUERO
                drawRect(Brush.horizontalGradient(listOf(lerp(cuero, Color.White, 0.1f), cuero, lerp(cuero, Color.Black, 0.3f)), 6 * d, 26 * d), Offset(6 * d, 0f), Size(20 * d, size.height))
                var y = 30 * d
                while (y < size.height - 20 * d) {
                    drawCircle(HIERRO, 5 * d, Offset(24 * d, y), style = Stroke(2 * d))
                    listOf(-12f, 12f).forEach { dy -> drawCircle(Color(0xFF8A8D91), 1.5f * d, Offset(12 * d, y + dy * d)) }
                    y += 70 * d
                }
            }
            Contenedor.EXPEDIENTE -> {
                // Las perforaciones del archivador y el broche metálico.
                var y = 40 * d; while (y < size.height - 20 * d) { drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 4 * d, Offset(18 * d, y)); y += 110 * d }
                drawRect(Color(0xFF8A8D91).copy(alpha = 0.8f), Offset(12 * d, 30 * d), Size(4 * d, size.height - 60 * d))
            }
            Contenedor.ACORDEON -> {
                // El zigzag del plegado a la izquierda.
                val p = Path(); var y = 0f; var k = 0
                p.moveTo(10 * d, 0f)
                while (y < size.height) { y += 18 * d; p.lineTo(if (k % 2 == 0) 26 * d else 10 * d, y); k++ }
                drawPath(p, Color(0xFF8A6A44).copy(alpha = 0.6f), style = Stroke(1.4f * d))
            }
            Contenedor.ORDENES -> {
                drawRoundRect(Color(0xFF8A5A2B), Offset(10 * d, 0f), Size(10 * d, size.height), CornerRadius(5 * d))
                if (oscuro) drawRect(Color.Black.copy(alpha = 0.4f), Offset(10 * d, 0f), Size(10 * d, size.height))
            }
            Contenedor.BITACORA -> {
                drawRect(Color(0xFF7A4A28), Offset(8 * d, 0f), Size(14 * d, size.height))
                var y = 20 * d; while (y < size.height - 10 * d) { drawLine(Color(0xFFE9DCC0).copy(alpha = 0.7f), Offset(12 * d, y), Offset(18 * d, y + 6 * d), 1.2f * d); y += 26 * d }
            }
            Contenedor.HERBARIO -> listOf(0.2f, 0.8f).forEach { f -> val y = size.height * f; drawLine(Color(0xFFEFE2C0), Offset(0f, y), Offset(24 * d, y), 3 * d, StrokeCap.Round); drawCircle(Color(0xFF6E7A4E), 3 * d, Offset(22 * d, y)) }
            Contenedor.ADVIENTO -> {
                drawRect(Color(0xFFB3122E), Offset(12 * d, 0f), Size(8 * d, size.height))
                val y = 60 * d
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(16 * d, y); quadraticTo(16 * d + s * 14 * d, y - 12 * d, 16 * d + s * 16 * d, y + 4 * d); close() }, Color(0xFFB3122E)) }
                drawCircle(Color(0xFFD4AF37), 3 * d, Offset(16 * d, y))
            }
            Contenedor.CONSOLA -> {
                // La tira de arrastre del papel continuo: los agujeros del tractor y la línea perforada.
                drawRect(Color(0xFFD9DEE3).copy(alpha = if (oscuro) 0.16f else 0.6f), Offset(2 * d, 0f), Size(16 * d, size.height))
                var y = 8 * d; while (y < size.height) { drawCircle(Color.Black.copy(alpha = 0.6f), 2.4f * d, Offset(10 * d, y)); y += 13 * d }
                drawLine(Color(0xFF00E5FF).copy(alpha = 0.3f), Offset(18 * d, 0f), Offset(18 * d, size.height), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 2 * d)))
            }
            Contenedor.TICKETS -> {
                // El agujero por donde la púa atravesó los tickets, con el desgarre alrededor, y la línea de corte.
                val y = size.height * 0.12f
                drawCircle(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.35f), 3.2f * d, Offset(20 * d, y))
                for (k in 0 until 6) { val a = k * PI / 3; drawLine(Color.Black.copy(alpha = 0.3f), Offset(20 * d, y), Offset(20 * d + (6 * d * cos(a)).toFloat(), y + (6 * d * sin(a)).toFloat()), 0.7f * d) }
                drawLine(Color(0xFFFF3D9A).copy(alpha = 0.35f), Offset(30 * d, 0f), Offset(30 * d, size.height), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 3 * d)))
            }
            Contenedor.CARPETA -> {
                // El broche de dos patas que pasa por los agujeros de las hojas, con su placa de metal.
                val metal = if (oscuro) Color(0xFF6E7478) else Color(0xFF9EA3A8)
                listOf(0.2f, 0.8f).forEach { f -> val y = size.height * f; drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 4 * d, Offset(20 * d, y)); drawLine(metal, Offset(20 * d, y), Offset(20 * d, y + if (f < 0.5f) 24 * d else -24 * d), 2.4f * d) }
                drawRoundRect(metal, Offset(14 * d, size.height * 0.2f - 6 * d), Size(12 * d, size.height * 0.6f + 12 * d), CornerRadius(3 * d), style = Stroke(1.4f * d))
            }
            Contenedor.TECNICO -> {
                // La bisagra de piano de acero del lomo, con sus nudillos y el pasador.
                val acero = if (oscuro) Color(0xFF5A626B) else Color(0xFF8A929B)
                var y = 0f; var k = 0
                while (y < size.height) { drawRoundRect(if (k % 2 == 0) acero else lerp(acero, Color.Black, 0.2f), Offset(10 * d, y), Size(12 * d, 18 * d), CornerRadius(3 * d)); y += 18 * d; k++ }
                drawLine(lerp(acero, Color.White, 0.3f), Offset(16 * d, 0f), Offset(16 * d, size.height), 1f * d)
            }
            Contenedor.CUADERNO -> {
                // El lomo cosido con tela cian y el hilo de la costura.
                val cian = if (oscuro) Color(0xFF0C3640) else Color(0xFF0B8FA1)
                drawRect(cian, Offset(8 * d, 0f), Size(14 * d, size.height))
                var y = 14 * d; while (y < size.height - 10 * d) { drawLine(Color.White.copy(alpha = 0.7f), Offset(15 * d, y), Offset(15 * d, y + 8 * d), 1.2f * d); y += 20 * d }
            }
            Contenedor.VUELO -> {
                // La tira de velcro a lo largo del borde (así se sujetaba la bitácora en la cabina).
                drawRect(Color(0xFF26324F).copy(alpha = if (oscuro) 0.9f else 0.7f), Offset(6 * d, 0f), Size(16 * d, size.height))
                var y = 0f; while (y < size.height) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(6 * d, y), Offset(22 * d, y), 1f); y += 2 * d }
                drawLine(Color(0xFFE8601C).copy(alpha = 0.7f), Offset(22 * d, 0f), Offset(22 * d, size.height), 1.2f * d)
            }
            Contenedor.INMERSION -> {
                // Los ollaos de latón del borde, con el cabo encerado que pasa de uno a otro en zigzag.
                val laton = if (oscuro) Color(0xFF8C6E2E) else Color(0xFFB8913E)
                val cabo = Color(0xFFCDB88C).copy(alpha = if (oscuro) 0.6f else 0.9f)
                val paso = 34 * d; var y = 18 * d; var k = 0
                val zig = Path().apply { moveTo(16 * d, 18 * d) }
                while (y < size.height - 10 * d) {
                    drawCircle(laton, 4.4f * d, Offset(16 * d, y)); drawCircle(Color.Black.copy(alpha = 0.5f), 2 * d, Offset(16 * d, y))
                    if (k > 0) zig.lineTo(if (k % 2 == 0) 16 * d else 4 * d, y)
                    y += paso; k++
                }
                drawPath(zig, cabo, style = Stroke(1.6f * d, cap = StrokeCap.Round))
            }
            Contenedor.TETRAD -> {
                // El pliegue del cuadernillo con sus dos grapas.
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), 10 * d, 30 * d), Offset(10 * d, 4 * d), Size(20 * d, size.height - 8 * d))
                listOf(0.3f, 0.7f).forEach { f -> val y = size.height * f; drawRect(Color(0xFFC3C6CA), Offset(14 * d, y - 9 * d), Size(3 * d, 18 * d)); drawRect(Color.Black.copy(alpha = 0.25f), Offset(17 * d, y - 9 * d), Size(1 * d, 18 * d)) }
            }
            // Estas las dibuja [Contenedores].
            Contenedor.ROLLO, Contenedor.TABLILLA, Contenedor.CODICE, Contenedor.HORAS, Contenedor.TALLER, Contenedor.LEGAJO, Contenedor.CAHIER -> {}
        }
    }
}
