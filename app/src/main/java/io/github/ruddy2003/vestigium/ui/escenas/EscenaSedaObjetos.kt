package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Los doce objetos del día de Nanai-vandak, que valen para los dos cuadros: en el camino van sobre la alfombra y en
 * Chang'an, sobre el mostrador de la tienda. Cada uno se dibuja apoyado en [objeto]`.c` (su centro, abajo) y mide unas
 * 26 unidades de ancho con `k` = 1. Sin letras: las monedas y el sello llevan solo su forma.
 */
internal object ObjetosSeda {
    private val C = ColorSeda

    /** Los objetos que el calendario puede dar. Lo vigila `EscenaSedaPrueba`. */
    val TODOS = listOf("seda", "monedas", "almizcle", "vidrio", "vino", "carta", "balanza", "campana", "laud", "sello", "fieltro", "te")

    fun DrawScope.objeto(id: String?, c: Offset, k: Float, s: SolSeda) {
        fun p(x: Float, y: Float) = c + Offset(x * k, y * k)
        fun caja(x: Float, y: Float, w: Float, h: Float, r: Float) = Path().apply { addRoundRect(RoundRect(Rect(p(x, y), Size(w * k, h * k)), CornerRadius(r * k))) }
        fun bulto(forma: Path, y0: Float, y1: Float, color: Color) = drawPath(forma, Brush.verticalGradient(0f to s.luz(color), 0.55f to s.medio(color), 1f to s.sombra(color), startY = c.y + y0 * k, endY = c.y + y1 * k))
        when (id) {
            "seda" -> {
                // Un rollo de seda, con un largo suelto que cae por delante: rosa, con su orillo de oro.
                val rosa = lerp(C.FIELTRO, C.CAL, 0.55f)
                val suelta = Path().apply { moveTo(p(-10f, -4f)); cubicTo(p(-4f, 2f), p(6f, -2f), p(15f, 2f)); lineTo(p(17f, -1f)); cubicTo(p(8f, -6f), p(0f, -2f), p(-8f, -8f)); close() }
                drawPath(suelta, s.luz(rosa)); drawPath(suelta, s.medio(C.BRONCE), style = Stroke(0.5f * k))
                bulto(caja(-13f, -11f, 24f, 8f, 4f), -11f, -3f, rosa)
                for (x in listOf(-6f, 0f, 6f)) drawLine(s.luz(C.CAL).copy(alpha = 0.6f), p(x, -10.6f), p(x - 1f, -3.4f), 0.5f * k)
                drawOval(s.sombra(rosa), p(8f, -11f), Size(5f * k, 8f * k)); drawOval(s.medio(C.BRONCE), p(9.2f, -9.4f), Size(2.6f * k, 4.8f * k), style = Stroke(0.5f * k))
            }
            "monedas" -> {
                // Una sarta de monedas de cobre, redondas con su agujero cuadrado, y tres de plata sueltas.
                drawLine(s.medio(C.FIELTRO), p(-14f, -2f), p(7f, -4f), 0.9f * k)
                for (j in 0..9) { val q = p(-11f + j * 1.9f, -5f - (j % 2) * 0.4f); drawOval(s.sombra(C.BRONCE), q + Offset(-2.6f * k, -3f * k), Size(5.2f * k, 6f * k)); drawOval(s.luz(C.BRONCE), q + Offset(-2.6f * k, -3.4f * k), Size(4.4f * k, 5.6f * k)); drawRect(s.sombra(C.HOLLIN).copy(alpha = 0.7f), q + Offset(-1f * k, -1.4f * k), Size(1.4f * k, 1.6f * k)) }
                for ((x, y) in listOf(10f to -1.6f, 15f to -2.6f, 12.4f to -5f)) { drawOval(s.sombra(C.TEJA), p(x - 2.6f, y - 0.6f), Size(5.4f * k, 2.6f * k)); drawOval(s.luz(lerp(C.TEJA, C.CAL, 0.6f)), p(x - 2.6f, y - 1.2f), Size(5.2f * k, 2.4f * k)) }
            }
            "almizcle" -> {
                // La bolsa de cuero del almizcle, atada con su cordón rojo, y unos granos fuera.
                val bolsa = Path().apply { moveTo(p(-7f, 0f)); cubicTo(p(-11f, -6f), p(-6f, -11f), p(-2.6f, -12f)); lineTo(p(-4f, -15.6f)); lineTo(p(4f, -15.6f)); lineTo(p(2.6f, -12f)); cubicTo(p(6f, -11f), p(11f, -6f), p(7f, 0f)); close() }
                bulto(bolsa, -15.6f, 0f, lerp(C.PELO_OSC, C.PELO, 0.3f)); drawLine(s.medio(C.FIELTRO), p(-3.4f, -12.4f), p(3.4f, -12.4f), 1.2f * k); drawLine(s.medio(C.FIELTRO), p(3f, -12.4f), p(6f, -8f), 0.7f * k)
                for ((x, y) in listOf(11f to -1f, 13.6f to -1.6f, 12f to -3f, 15f to -0.6f)) drawCircle(s.medio(C.MADERA_OSC), 1f * k, p(x, y))
            }
            "vidrio" -> {
                // Un vaso de vidrio tallado, verde azulado, en su cama de paja: se ve a través.
                for (j in -6..6) drawLine(s.luz(C.LOESS), p(j * 2.2f, 0f), p(j * 2.8f + 1f, -3.4f - (j * j % 3)), 0.6f * k, StrokeCap.Round)
                val vaso = Path().apply { moveTo(p(-7f, -14f)); lineTo(p(7f, -14f)); cubicTo(p(7f, -6f), p(4f, -2f), p(3f, -1f)); lineTo(p(-3f, -1f)); cubicTo(p(-4f, -2f), p(-7f, -6f), p(-7f, -14f)); close() }
                val verde = lerp(C.ANIL, C.VERDE, 0.5f)
                drawPath(vaso, s.medio(lerp(verde, C.CAL, 0.45f)).copy(alpha = 0.82f)); clipPath(vaso) { for (j in 0..2) for (i in -2..2) drawOval(s.luz(C.CAL).copy(alpha = 0.55f), p(i * 3.2f - 1.2f + (j % 2) * 1.6f, -12f + j * 3.6f), Size(2.4f * k, 2.4f * k), style = Stroke(0.4f * k)) }
                drawOval(s.luz(lerp(verde, C.CAL, 0.7f)), p(-7f, -15.4f), Size(14f * k, 3f * k), style = Stroke(0.7f * k)); drawLine(Color.White.copy(alpha = 0.7f), p(-5f, -12f), p(-4.4f, -5f), 0.7f * k, StrokeCap.Round)
            }
            "vino" -> {
                // Un odre de vino y un cuenco de plata.
                val odre = Path().apply { moveTo(p(-14f, -1f)); cubicTo(p(-17f, -10f), p(-6f, -14f), p(2f, -10f)); lineTo(p(6f, -14f)); lineTo(p(8.4f, -12f)); lineTo(p(5f, -7f)); cubicTo(p(6f, -2f), p(-8f, 2f), p(-14f, -1f)); close() }
                bulto(odre, -14f, 0f, lerp(C.MADERA_OSC, C.FIELTRO, 0.3f)); drawLine(s.medio(C.PELO), p(4f, -10.4f), p(6.6f, -8.4f), 1.1f * k)
                drawPath(Path().apply { moveTo(p(10f, -6f)); cubicTo(p(10f, 0f), p(19f, 0f), p(19f, -6f)); close() }, s.medio(lerp(C.TEJA, C.CAL, 0.5f))); drawOval(s.medio(lerp(C.FIELTRO, C.HOLLIN, 0.3f)), p(10f, -7.2f), Size(9f * k, 2.4f * k)); drawOval(s.luz(C.CAL), p(10f, -7.2f), Size(9f * k, 2.4f * k), style = Stroke(0.5f * k))
            }
            "carta" -> {
                // Una carta doblada, atada con su hilo, sobre su funda de seda.
                drawPath(poligono(p(-14f, -1f), p(8f, -4f), p(10f, 0f), p(-11f, 2f)), s.medio(C.ANIL)); drawPath(poligono(p(-14f, -1f), p(8f, -4f), p(8.6f, -3f), p(-13.4f, 0f)), s.luz(C.BRONCE))
                val papel = poligono(p(-8f, -4f), p(13f, -7f), p(15f, -2f), p(-6f, 1f))
                drawPath(papel, s.luz(C.CAL)); drawPath(poligono(p(-8f, -4f), p(2f, -5.4f), p(4f, -0.4f), p(-6f, 1f)), s.medio(lerp(C.CAL, C.ARENA, 0.4f)))
                drawLine(s.medio(C.FIELTRO), p(3f, -5.6f), p(5f, -0.6f), 0.8f * k); drawLine(s.medio(C.FIELTRO), p(-7f, -1.6f), p(14f, -4.6f), 0.6f * k); drawCircle(s.medio(lerp(C.ADOBE, C.FIELTRO, 0.5f)), 1.6f * k, p(4f, -3f))
            }
            "balanza" -> {
                // La balanza de mercader: la barra con sus marcas, el platillo colgado de tres hilos y la pesa que corre.
                drawLine(s.medio(lerp(C.CAL, C.ARENA, 0.3f)), p(-15f, -13f), p(15f, -17f), 1.4f * k, StrokeCap.Round); for (j in 0..9) drawLine(s.sombra(C.HOLLIN), p(-4f + j * 1.8f, -14.6f - j * 0.24f), p(-4f + j * 1.8f, -13.4f - j * 0.24f), 0.3f * k)
                drawLine(s.medio(C.FIELTRO), p(-9f, -13.8f), p(-9f, -19f), 0.7f * k); drawCircle(s.medio(C.BRONCE), 1.1f * k, p(-9f, -19.6f))
                for (x in listOf(-17f, -13f, -9f)) drawLine(s.medio(C.HOLLIN), p(-13f, -13.2f), p(x, -3f), 0.3f * k)
                drawPath(Path().apply { moveTo(p(-19f, -3f)); cubicTo(p(-18f, 1f), p(-8f, 1f), p(-7f, -3f)); close() }, s.medio(C.BRONCE)); drawOval(s.luz(C.BRONCE), p(-19f, -4f), Size(12f * k, 2.2f * k))
                drawLine(s.medio(C.HOLLIN), p(9f, -16.2f), p(9f, -9f), 0.3f * k); bulto(Path().apply { addOval(Rect(p(6.4f, -9f), Size(5.2f * k, 6f * k))) }, -9f, -3f, C.TEJA)
            }
            "campana" -> {
                // La campana de camello, de bronce, con su correa y su badajo.
                drawPath(Path().apply { moveTo(p(-4f, -15f)); cubicTo(p(-6f, -22f), p(6f, -22f), p(4f, -15f)) }, s.medio(C.FIELTRO), style = Stroke(1.6f * k))
                val cuerpo = Path().apply { moveTo(p(-9f, -1f)); cubicTo(p(-9f, -12f), p(-5f, -16f), p(0f, -16f)); cubicTo(p(5f, -16f), p(9f, -12f), p(9f, -1f)); close() }
                bulto(cuerpo, -16f, -1f, C.BRONCE); clipPath(cuerpo) { drawRect(s.sombra(C.BRONCE).copy(alpha = 0.6f), p(if (s.solALaDerecha) -9f else 3f, -16f), Size(6f * k, 16f * k)); for (y in listOf(-5f, -3f)) drawLine(s.sombra(C.MADERA_OSC), p(-9f, y), p(9f, y), 0.5f * k) }
                drawOval(s.sombra(C.HOLLIN), p(-9f, -2f), Size(18f * k, 2.4f * k)); drawCircle(s.medio(C.TEJA), 1.6f * k, p(0f, 0.6f))
            }
            "laud" -> {
                // El laúd de los sogdianos: el cuerpo de pera, el mástil con el clavijero doblado hacia atrás y cuatro cuerdas.
                val cuerpo = Path().apply { moveTo(p(-16f, -3f)); cubicTo(p(-18f, -11f), p(-6f, -14f), p(2f, -10.6f)); lineTo(p(2.6f, -8f)); cubicTo(p(-4f, 1f), p(-14f, 3f), p(-16f, -3f)); close() }
                bulto(cuerpo, -13f, 2f, lerp(C.MADERA, C.CAL, 0.35f)); drawPath(cuerpo, s.sombra(C.MADERA_OSC), style = Stroke(0.6f * k))
                drawPath(poligono(p(1.6f, -10.8f), p(14f, -15.4f), p(14.6f, -13.4f), p(2.6f, -8f)), s.medio(C.MADERA_OSC)); drawPath(poligono(p(14f, -15.4f), p(18.4f, -11f), p(17f, -10f), p(13.4f, -13.4f)), s.sombra(C.MADERA_OSC))
                for (j in 0..3) drawLine(s.luz(C.CAL).copy(alpha = 0.8f), p(-12f + j * 0.5f, -3.4f - j * 0.9f), p(14f, -14.8f + j * 0.34f), 0.25f * k)
                drawLine(s.sombra(C.MADERA_OSC), p(-13f, -2f), p(-10.4f, -7f), 1.1f * k); for (j in 0..1) drawCircle(s.sombra(C.HOLLIN), 0.9f * k, p(-7f + j * 4f, -8f + j * 2.6f))
            }
            "sello" -> {
                // El sello de piedra, con su asa, y a su lado el pegote de barro con su marca, en su cordón.
                bulto(caja(-9f, -9f, 10f, 9f, 1.4f), -9f, 0f, lerp(C.ROCA, C.FIELTRO, 0.4f)); bulto(Path().apply { addOval(Rect(p(-7f, -15f), Size(6f * k, 7f * k))) }, -15f, -8f, lerp(C.ROCA, C.FIELTRO, 0.4f)); drawOval(s.sombra(C.HOLLIN).copy(alpha = 0.6f), p(-5.4f, -13f), Size(2.8f * k, 2.8f * k))
                drawLine(s.medio(C.PELO_OSC), p(3f, -1f), p(18f, -3f), 0.7f * k)
                drawOval(s.sombra(C.ADOBE), p(6f, -5f), Size(11f * k, 6.4f * k)); drawOval(s.luz(C.ADOBE), p(6.4f, -5.6f), Size(10f * k, 5.4f * k)); drawOval(s.sombra(C.MADERA_OSC), p(8.6f, -4.6f), Size(5.6f * k, 3.2f * k), style = Stroke(0.5f * k)); drawCircle(s.sombra(C.MADERA_OSC), 0.6f * k, p(11.4f, -3f))
            }
            "fieltro" -> {
                // Una manta de fieltro doblada en cuatro, gris clara, con su borde rojo.
                val gris = lerp(C.CAL, C.ROCA, 0.3f)
                for (j in 0..3) { val y = -3.4f * j; val capa = caja(-14f + j * 0.6f, y - 4f, 28f - j * 1.2f, 4.4f, 1.6f); bulto(capa, y - 4f, y + 0.4f, gris); drawLine(s.medio(C.FIELTRO), p(-14f + j * 0.6f, y - 0.6f), p(14f - j * 0.6f, y - 0.6f), 0.8f * k) }
                drawPath(poligono(p(-13f, -14.2f), p(6f, -14.2f), p(9f, -11f), p(-12f, -11f)), s.luz(gris)); for (x in listOf(-8f, -3f, 2f)) drawLine(s.sombra(gris).copy(alpha = 0.5f), p(x, -14f), p(x + 2f, -11.2f), 0.4f * k)
            }
            "te" -> {
                // Una torta de té prensado, con un trozo partido, y un cuenco.
                drawOval(s.sombra(lerp(C.VERDE_OSC, C.HOLLIN, 0.5f)), p(-15f, -5f), Size(18f * k, 6f * k)); drawOval(s.medio(lerp(C.VERDE_OSC, C.MADERA_OSC, 0.5f)), p(-15f, -6.6f), Size(18f * k, 6f * k)); drawOval(s.luz(C.VERDE_OSC).copy(alpha = 0.5f), p(-11f, -5.6f), Size(10f * k, 3f * k), style = Stroke(0.5f * k))
                drawPath(poligono(p(-1f, -1f), p(3f, -3f), p(5f, 0f), p(1f, 1f)), s.medio(lerp(C.VERDE_OSC, C.MADERA_OSC, 0.5f)))
                drawPath(Path().apply { moveTo(p(7f, -7f)); cubicTo(p(7f, 0f), p(17f, 0f), p(17f, -7f)); close() }, s.medio(C.CAL)); drawOval(s.medio(lerp(C.LOESS, C.MADERA, 0.5f)), p(7f, -8.2f), Size(10f * k, 2.6f * k)); drawOval(s.luz(C.CAL), p(7f, -8.2f), Size(10f * k, 2.6f * k), style = Stroke(0.5f * k))
            }
            else -> Unit
        }
    }
}
