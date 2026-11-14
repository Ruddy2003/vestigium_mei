package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor

/** Los doce objetos del día del funcionario, sobre el banco de la plaza (el abrigo, colgado del respaldo). */
internal object ObjetosDistopia {
    private val C = ColorDistopia

    /**
     * El objeto de hoy, con el pie en [sobre] (el asiento del banco) y agrandado [k] veces. [respaldo]: los listones de
     * los que cuelga el abrigo.
     */
    fun DrawScope.objetoDelDia(m: Momento, l: Luces, r: Rayo, a: Alumbrado, sobre: Offset, k: Float, respaldo: Rect) {
        fun c(propio: Color, mira: Float = 0f): Color {
            val hacia = if (mira == 0f) CiudadDistopia.frente(r) else CiudadDistopia.lado(r, mira)
            return a.en(sobre, if (hacia >= 0f) lerp(propio, C.LUZ, 0.3f * hacia * r.contraste) else mult(propio, r.color, 0.9f * -hacia * r.contraste), mira)
        }
        val luzDer = r.lat >= 0f
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx, sobre.y + dy)
        if (m.ficha.objeto == "abrigo") { abrigo(::c, respaldo, luzDer); return }
        scale(k, k, sobre) {
            when (m.ficha.objeto) {
                "pantalla" -> {
                    // La pantalla de un cuarto, descolgada y apoyada en el respaldo: la caja gris, el cristal y, detrás, la cámara.
                    val caja = Rect(sobre.x - 13f, sobre.y - 19f, sobre.x + 13f, sobre.y)
                    drawRoundRect(c(C.HIERRO), caja.topLeft, caja.size, CornerRadius(2f)); drawRoundRect(c(lerp(C.HIERRO, C.HUESO, 0.3f), if (luzDer) 1f else -1f), Offset(if (luzDer) caja.right - 2.4f else caja.left, caja.top), Size(2.4f, caja.height), CornerRadius(1f))
                    val cristal = Rect(caja.left + 2.6f, caja.top + 2.4f, caja.right - 2.6f, caja.bottom - 5f)
                    val viva = EscenaDistopia.corriente(m) && EscenaDistopia.lugar(m) == EscenaDistopia.Lugar.CAPITAL
                    drawRect(if (viva) lerp(C.PANTALLA, Color(0xFF2A4A4A), 0.4f) else c(lerp(C.CRISTAL, C.HOLLIN, 0.5f)), cristal.topLeft, cristal.size)
                    if (viva) { for (i in 0..2) drawRect(lerp(C.PANTALLA, Color.White, 0.6f).copy(alpha = 0.8f - 0.2f * i), Offset(cristal.left + 2f, cristal.top + 2f + i * 3.2f), Size(cristal.width * (0.8f - 0.2f * i), 1.4f)); resplandor(cristal.center, 22f, 0.2f + 0.4f * l.noche, C.PANTALLA) }
                    else drawLine(Color.White.copy(alpha = 0.3f), cristal.topLeft + Offset(1.6f, 1.6f), cristal.topLeft + Offset(7f, 1.6f), 0.5f)
                    drawCircle(c(C.HOLLIN), 1.1f, Offset(caja.center.x, caja.bottom - 2.6f)); drawCircle(if (viva) C.PILOTO else c(C.OXIDO), 0.5f, Offset(caja.right - 4f, caja.bottom - 2.6f))
                    drawPath(Path().apply { moveTo(caja.left + 3f, caja.bottom); cubicTo(caja.left - 6f, caja.bottom + 3f, caja.left - 14f, caja.bottom - 4f, caja.left - 19f, caja.bottom + 1f) }, c(C.HOLLIN), style = Stroke(0.8f, cap = StrokeCap.Round))
                }
                "cartilla" -> {
                    // La cartilla de racionamiento, abierta: dos hojas de cupones de colores, unos ya cortados.
                    val hoja = poligono(p(-15f, -1f), p(-13f, -9f), p(13f, -9f), p(15f, -1f))
                    drawPath(hoja, c(C.HUESO)); drawLine(c(C.HORMIGON_OSC), p(0f, -9f), p(0f, -1f), 0.5f)
                    drawPath(poligono(p(-15f, -1f), p(15f, -1f), p(15f, 0f), p(-15f, 0f)), c(C.SACO))
                    val colores = listOf(C.ROJO, C.VERDE_GRIS, C.SACO, Color(0xFF8E9AA8), C.OXIDO)
                    for (f in 0..2) for (i in 0..7) { if ((i * 3 + f * 5) % 7 == 0) continue; val x = -13f + i * 3.3f + (if (i > 3) 1.2f else 0f) + f * 0.5f; drawRect(c(colores[(i + f * 2) % 5]), p(x, -8f + f * 2.3f), Size(2.4f, 1.6f)) }
                    drawCircle(c(C.ROJO_OSC).copy(alpha = 0.6f), 2f, p(9f, -3.4f), style = Stroke(0.5f))
                }
                "lapiz" -> {
                    // Un lápiz del ministerio, con su número grabado (una muesca) y las virutas de sacarle punta.
                    val a0 = p(-15f, -1.6f); val b0 = p(12f, -3.6f)
                    drawLine(c(C.HOLLIN).copy(alpha = 0.3f), a0 + Offset(0.6f, 1.2f), b0 + Offset(4f, 1f), 1.4f)
                    drawLine(c(C.SACO), a0, b0, 2.4f); drawLine(c(lerp(C.SACO, C.LUZ, 0.4f)), a0 + Offset(0f, -0.7f), b0 + Offset(0f, -0.7f), 0.6f)
                    drawPath(poligono(b0 + Offset(0f, -1.2f), b0 + Offset(4.6f, 0.5f), b0 + Offset(0f, 1.2f)), c(C.MADERA)); drawPath(poligono(b0 + Offset(3.2f, 0f), b0 + Offset(4.8f, 0.5f), b0 + Offset(3.2f, 0.9f)), c(C.HOLLIN))
                    drawLine(c(C.ROJO), a0, a0 + Offset(2.6f, -0.2f), 2.4f); drawLine(c(C.HOLLIN).copy(alpha = 0.5f), a0 + Offset(8f, -0.4f), a0 + Offset(13f, -0.8f), 0.4f)
                    for ((dx, dy) in listOf(16f to -1f, 18.4f to -0.4f, 14.6f to 0.2f)) drawArc(c(C.MADERA), 200f, 200f, false, p(dx, dy - 1.4f), Size(2.6f, 2f), style = Stroke(0.6f))
                }
                "cuaderno" -> {
                    // Un cuaderno de tapas negras, de los de antes, sin numerar: el lomo de tela y la cinta.
                    val tapa = poligono(p(-12f, -1.6f), p(-10f, -11f), p(11f, -11f), p(13f, -1.6f))
                    drawPath(poligono(p(-12f, -1.6f), p(13f, -1.6f), p(13f, 0f), p(-12f, 0f)), c(C.HUESO)); for (i in 0..2) drawLine(c(C.HORMIGON_OSC).copy(alpha = 0.6f), p(-11.6f, -1.2f + i * 0.5f), p(12.6f, -1.2f + i * 0.5f), 0.2f)
                    drawPath(tapa, Brush.verticalGradient(listOf(c(lerp(C.HOLLIN, C.HIERRO, 0.4f)), c(C.HOLLIN)), sobre.y - 11f, sobre.y - 1.6f))
                    drawPath(poligono(p(-12f, -1.6f), p(-10f, -11f), p(-7.2f, -11f), p(-8.8f, -1.6f)), c(C.ROJO_OSC))
                    drawLine(c(C.ROJO), p(4f, -11f), p(5.4f, 1.6f), 0.8f); drawLine(l.tono(C.SODIO).copy(alpha = 0.3f + 0.4f * l.noche), p(-6f, -10.6f), p(10.4f, -10.6f), 0.4f)
                }
                "ginebra" -> {
                    // La botella sin etiqueta, con su tapón de lacre, y el vaso.
                    val bot = Path().apply { moveTo(sobre.x - 4.4f, sobre.y); lineTo(sobre.x - 4.4f, sobre.y - 15f); cubicTo(sobre.x - 4.4f, sobre.y - 19f, sobre.x - 1.6f, sobre.y - 19f, sobre.x - 1.6f, sobre.y - 22f); lineTo(sobre.x - 1.6f, sobre.y - 26f); lineTo(sobre.x + 1.6f, sobre.y - 26f); lineTo(sobre.x + 1.6f, sobre.y - 22f); cubicTo(sobre.x + 1.6f, sobre.y - 19f, sobre.x + 4.4f, sobre.y - 19f, sobre.x + 4.4f, sobre.y - 15f); lineTo(sobre.x + 4.4f, sobre.y); close() }
                    drawPath(bot, Brush.horizontalGradient(if (luzDer) listOf(c(Color(0xFF8AA69A)), c(Color(0xFFCFE0D4)), c(Color(0xFFEAF4EC))) else listOf(c(Color(0xFFEAF4EC)), c(Color(0xFFCFE0D4)), c(Color(0xFF8AA69A))), sobre.x - 4.4f, sobre.x + 4.4f), alpha = 0.86f)
                    drawRect(c(Color(0xFFDDE8DC)).copy(alpha = 0.6f), p(-3.8f, -9f), Size(7.6f, 8.6f)); drawLine(Color.White.copy(alpha = 0.6f), p(if (luzDer) 2.6f else -2.6f, -14f), p(if (luzDer) 2.6f else -2.6f, -3f), 0.6f)
                    drawRect(c(C.ROJO_OSC), p(-2f, -27.6f), Size(4f, 2.4f)); drawPath(bot, c(C.HIERRO).copy(alpha = 0.3f), style = Stroke(0.4f))
                    drawPath(poligono(p(9f, 0f), p(8.2f, -7f), p(14.8f, -7f), p(14f, 0f)), c(Color(0xFFDDE8DC)).copy(alpha = 0.7f)); drawRect(c(Color(0xFFF2F6EE)).copy(alpha = 0.7f), p(8.9f, -3f), Size(5.2f, 2.8f)); drawOval(c(C.HUESO), p(8.2f, -7.8f), Size(6.6f, 1.6f), style = Stroke(0.4f))
                }
                "pase" -> {
                    // El pase del ministerio: la tarjeta con el recuadro de la foto (gris, con su brillo: sin cara) y su banda de colores, y el cordón.
                    drawPath(Path().apply { moveTo(sobre.x - 7f, sobre.y - 11f); cubicTo(sobre.x - 16f, sobre.y - 16f, sobre.x - 22f, sobre.y - 4f, sobre.x - 15f, sobre.y - 1f); cubicTo(sobre.x - 9f, sobre.y + 1f, sobre.x - 3f, sobre.y - 4f, sobre.x - 6f, sobre.y - 10.4f) }, c(C.ROJO_OSC), style = Stroke(0.9f, cap = StrokeCap.Round))
                    val t = poligono(p(-9f, -1f), p(-7.4f, -11.6f), p(9.6f, -11.6f), p(11f, -1f))
                    drawPath(poligono(p(-9f, -1f), p(11f, -1f), p(11f, -0.2f), p(-9f, -0.2f)), c(C.HORMIGON_OSC)); drawPath(t, c(C.HUESO))
                    drawRect(c(C.HORMIGON_OSC), p(-6.2f, -9.6f), Size(5.4f, 6.6f)); drawLine(c(C.HORMIGON), p(-5.6f, -4.2f), p(-1.6f, -8.8f), 1.2f); drawLine(c(C.HORMIGON).copy(alpha = 0.6f), p(-3.4f, -3.6f), p(-1.2f, -6f), 0.6f)
                    for ((i, col) in listOf(C.ROJO, C.VERDE_GRIS, C.SACO).withIndex()) drawRect(c(col), p(1f + i * 2.9f, -10.2f), Size(2.4f, 3f))
                    for (i in 0..2) drawLine(c(C.HOLLIN).copy(alpha = 0.4f), p(1f, -5.4f + i * 1.5f), p(8.6f - i * 1.4f, -5.4f + i * 1.5f), 0.4f)
                    drawCircle(c(C.HIERRO), 0.9f, p(-6.6f, -10.8f))
                }
                "tubo" -> {
                    // Un tubo de carpetas, de los que suben por la pared: el cartucho de latón, tumbado, con una carpeta asomando.
                    val cuerpo = Rect(sobre.x - 15f, sobre.y - 9f, sobre.x + 11f, sobre.y)
                    drawRoundRect(Brush.verticalGradient(listOf(c(Color(0xFFE2C87E)), c(Color(0xFFB8963E)), c(Color(0xFF7A6228))), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size, CornerRadius(4.4f))
                    for (x in listOf(cuerpo.left + 3f, cuerpo.right - 5f)) drawRoundRect(Brush.verticalGradient(listOf(c(C.HUESO), c(C.HIERRO), c(C.HOLLIN)), cuerpo.top - 0.6f, cuerpo.bottom + 0.6f), Offset(x, cuerpo.top - 0.6f), Size(3f, cuerpo.height + 1.2f), CornerRadius(1.4f))
                    drawLine(Color.White.copy(alpha = 0.5f), Offset(cuerpo.left + 8f, cuerpo.top + 2f), Offset(cuerpo.right - 8f, cuerpo.top + 2f), 0.6f)
                    drawPath(poligono(Offset(cuerpo.right - 1f, cuerpo.top + 1.4f), Offset(cuerpo.right + 9f, cuerpo.top - 1f), Offset(cuerpo.right + 10f, cuerpo.bottom - 2.6f), Offset(cuerpo.right - 1f, cuerpo.bottom - 1.4f)), c(C.SACO)); drawLine(c(C.ROJO), Offset(cuerpo.right + 4f, cuerpo.top + 0.4f), Offset(cuerpo.right + 4.6f, cuerpo.bottom - 2f), 0.8f)
                }
                "cigarrillo" -> {
                    // El paquete blando, sin marca, dos cigarrillos y la caja de cerillas; uno, encendido en el borde del banco.
                    val paq = poligono(p(-13f, 0f), p(-12f, -11f), p(-3f, -11.6f), p(-2f, -0.4f))
                    drawPath(paq, c(C.HUESO)); drawPath(poligono(p(-12f, -11f), p(-3f, -11.6f), p(-3.4f, -8.6f), p(-11.8f, -8f)), c(C.SACO)); drawRect(c(C.ROJO), p(-11f, -6f), Size(7f, 2f))
                    for (i in 0..1) drawRect(c(Color(0xFFF4EEE0)), p(-10f + i * 3f, -13.6f - i * 0.8f), Size(1.8f, 3.4f))
                    drawRect(c(C.MADERA), p(2f, -3f), Size(7f, 3f)); drawRect(c(C.ROJO_OSC), p(2f, -3f), Size(7f, 0.9f))
                    drawLine(c(Color(0xFFF4EEE0)), p(4f, -0.6f), p(13f, -2.4f), 1.6f, StrokeCap.Butt); drawLine(c(C.SACO), p(4f, -0.6f), p(6.4f, -1.1f), 1.6f)
                    drawCircle(Color(0xFFFF7A3A), 0.9f, p(13.2f, -2.5f)); drawLine(c(Color(0xFF8E8880)), p(11.8f, -2.2f), p(13f, -2.4f), 1.6f)
                }
                "gafas" -> {
                    // Las gafas de montura negra, con la patilla pegada con cinta.
                    val montura = c(C.HOLLIN)
                    for (s in listOf(-1f, 1f)) { drawOval(c(lerp(C.CRISTAL, Color.White, 0.5f)).copy(alpha = 0.5f), p(s * 6.6f - 5f, -8.6f), Size(10f, 7.6f)); drawOval(montura, p(s * 6.6f - 5f, -8.6f), Size(10f, 7.6f), style = Stroke(1.3f)); drawLine(Color.White.copy(alpha = 0.5f), p(s * 6.6f - 2.6f, -6.8f), p(s * 6.6f - 0.6f, -7.4f), 0.5f) }
                    drawArc(montura, 190f, 160f, false, p(-2.4f, -7.6f), Size(4.8f, 3.4f), style = Stroke(1.2f))
                    drawLine(montura, p(-11.4f, -6f), p(-16f, -1f), 1.2f, StrokeCap.Round); drawLine(montura, p(11.4f, -6f), p(17f, -0.6f), 1.2f, StrokeCap.Round)
                    drawLine(c(C.HUESO), p(12.6f, -4.8f), p(14.6f, -3f), 2.2f)
                    drawOval(Color.Black.copy(alpha = 0.16f), p(-11f, -1f), Size(22f, 2f))
                }
                "periodico" -> {
                    // El periódico del Estado, doblado: la cabecera roja, las columnas de rayas grises y la foto sin nadie.
                    val hoja = poligono(p(-15f, -0.6f), p(-13f, -12f), p(13f, -12f), p(15f, -0.6f))
                    drawPath(poligono(p(-15f, -0.6f), p(15f, -0.6f), p(15.4f, 1f), p(-15.4f, 1f)), c(lerp(C.HUESO, C.HORMIGON_OSC, 0.4f))); drawPath(hoja, c(lerp(C.HUESO, C.HORMIGON, 0.25f)))
                    drawPath(poligono(p(-12.6f, -11f), p(12.6f, -11f), p(12.9f, -8.8f), p(-12.9f, -8.8f)), c(C.ROJO))
                    for (col in 0..2) for (i in 0..3) drawLine(c(C.HOLLIN).copy(alpha = 0.45f), p(-12f + col * 8.6f, -7.4f + i * 1.6f), p(-5.6f + col * 8.6f - (if ((i + col) % 3 == 0) 2f else 0f), -7.4f + i * 1.6f), 0.5f)
                    drawRect(c(C.HORMIGON_OSC), p(5.4f, -7.6f), Size(6.6f, 4.4f)); drawLine(c(C.HORMIGON_OSC).copy(alpha = 0.5f), p(-14f, -6f), p(14f, -6f).copy(y = sobre.y - 5.6f), 0.3f)
                }
                "llave" -> {
                    // La llave de la vivienda, igual a todas, con su chapa numerada (una muesca) en la anilla.
                    val hierro = c(C.HIERRO); val claro = c(lerp(C.HIERRO, C.HUESO, 0.5f))
                    drawOval(Color.Black.copy(alpha = 0.16f), p(-13f, -1.4f), Size(28f, 2.6f))
                    drawCircle(hierro, 4.6f, p(-9f, -5f), style = Stroke(1.8f)); drawLine(hierro, p(-4.6f, -4.6f), p(13f, -3f), 2f); drawLine(claro, p(-4.4f, -5.2f), p(12.6f, -3.6f), 0.5f)
                    for ((dx, h) in listOf(8f to 3.4f, 10.6f to 2.4f, 13f to 3.6f)) drawRect(hierro, p(dx - 0.8f, -3f), Size(1.6f, h))
                    drawCircle(hierro, 5.6f, p(-13.6f, -6.6f), style = Stroke(0.7f))
                    drawRoundRect(c(Color(0xFFC8A65A)), p(-21f, -4.6f), Size(7f, 4.6f), CornerRadius(1f)); drawCircle(c(C.HOLLIN), 0.6f, p(-15.4f, -2.4f)); drawLine(c(C.HOLLIN).copy(alpha = 0.6f), p(-19.6f, -3f), p(-17f, -3f), 0.5f); drawLine(c(C.HOLLIN).copy(alpha = 0.6f), p(-19.6f, -1.6f), p(-18f, -1.6f), 0.5f)
                }
                else -> Unit
            }
        }
    }

    /** El abrigo gris de los funcionarios, colgado del respaldo del banco: los hombros arriba, las mangas caídas y los faldones hasta el asiento. */
    private fun DrawScope.abrigo(c: (Color, Float) -> Color, respaldo: Rect, luzDer: Boolean) {
        val x = respaldo.center.x - 6f; val y = respaldo.top - 1.6f
        val gris = Color(0xFF8E9088)
        val cuerpo = Path().apply {
            moveTo(x - 15f, y + 1f); cubicTo(x - 12f, y - 2.4f, x - 5f, y - 3f, x, y - 1.6f); cubicTo(x + 5f, y - 3f, x + 12f, y - 2.4f, x + 15f, y + 1f)
            lineTo(x + 17.6f, y + 16f); lineTo(x + 13f, y + 17f); lineTo(x + 13.4f, y + 30f); cubicTo(x + 6f, y + 32.4f, x - 6f, y + 32.4f, x - 13.4f, y + 30f); lineTo(x - 13f, y + 17f); lineTo(x - 17.6f, y + 16f); close()
        }
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(x - 14f, y + 29f), Size(30f, 4f))
        drawPath(cuerpo, Brush.horizontalGradient(if (luzDer) listOf(c(lerp(gris, C.HOLLIN, 0.35f), -1f), c(gris, 0f), c(lerp(gris, C.HUESO, 0.25f), 1f)) else listOf(c(lerp(gris, C.HUESO, 0.25f), -1f), c(gris, 0f), c(lerp(gris, C.HOLLIN, 0.35f), 1f)), x - 17.6f, x + 17.6f))
        // Las solapas, la botonadura, los pliegues y el número de la etiqueta del cuello (un recuadro claro).
        drawPath(poligono(Offset(x, y - 1.6f), Offset(x - 5f, y + 1f), Offset(x - 2f, y + 9f), Offset(x, y + 6f)), c(lerp(gris, C.HOLLIN, 0.3f), 0f)); drawPath(poligono(Offset(x, y - 1.6f), Offset(x + 5f, y + 1f), Offset(x + 2f, y + 9f), Offset(x, y + 6f)), c(lerp(gris, C.HOLLIN, 0.15f), 0f))
        drawLine(c(C.HOLLIN, 0f).copy(alpha = 0.5f), Offset(x, y + 6f), Offset(x + 0.6f, y + 31f), 0.5f)
        for (i in 0..3) drawCircle(c(C.HOLLIN, 0f), 0.9f, Offset(x + 2.4f, y + 11f + i * 5f))
        for (dx in listOf(-9f, -5f, 8f)) drawLine(c(lerp(gris, C.HOLLIN, 0.4f), 0f).copy(alpha = 0.5f), Offset(x + dx, y + 8f), Offset(x + dx * 1.1f, y + 30f), 0.6f)
        drawLine(c(lerp(gris, C.HOLLIN, 0.4f), 0f), Offset(x - 13f, y + 17f), Offset(x - 11f, y + 5f), 0.5f); drawLine(c(lerp(gris, C.HOLLIN, 0.4f), 0f), Offset(x + 13f, y + 17f), Offset(x + 11f, y + 5f), 0.5f)
        drawRect(c(C.HUESO, 0f), Offset(x - 1.6f, y - 1f), Size(3.2f, 1.6f))
        // El periódico doblado que se lleva debajo contra el frío asoma del bolsillo.
        drawPath(poligono(Offset(x - 11f, y + 20f), Offset(x - 6f, y + 19f), Offset(x - 5.6f, y + 24f), Offset(x - 10.6f, y + 25f)), c(C.HUESO, 0f)); drawLine(c(C.HOLLIN, 0f).copy(alpha = 0.6f), Offset(x - 12f, y + 23.4f), Offset(x - 5f, y + 22f), 0.6f)
    }
}
