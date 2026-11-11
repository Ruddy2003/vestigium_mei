package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.ACERO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.AMARILLO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.AZUL
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.CIAN
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.CREMA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.NEGRO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.ROJO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNeon.VERDE
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las cosas del primer plano del callejón de Ciudad neón (`EscenaNeon`), cada una con su silueta (para la sombra que
 * arroja), dónde pisa y cómo se pinta; y los veinticuatro objetos del día de Kei y de Amaru, que van sobre el cajón.
 * Todo pide su color al [Alumbrado] del momento: por eso cada cosa tiene un filo magenta del lado del letrero y otro
 * verde agua o ámbar del otro.
 */
internal object CosasNeon {
    /** La misma cosa, [k] veces más grande, sin moverla de donde pisa. */
    fun grande(c: Cosa, k: Float): Cosa {
        val f = Path().apply { addPath(c.forma); transform(Matrix().apply { translate(c.pie.x, c.pie.y); scale(k, k); translate(-c.pie.x, -c.pie.y) }) }
        return Cosa(f, c.pie, c.bajoElFollaje) { scale(k, k, c.pie) { c.pintar(this) } }
    }

    private fun redondo(x0: Float, y0: Float, x1: Float, y1: Float, r: Float) = Path().apply { addRoundRect(RoundRect(x0, y0, x1, y1, CornerRadius(r))) }

    /** La bombona de gas del puesto: alta, azulada, con su aro, su llave y el óxido de abajo. */
    fun bombona(a: Alumbrado): Cosa {
        val pie = Offset(24f, 277f); val forma = redondo(pie.x - 12f, pie.y - 50f, pie.x + 12f, pie.y, 7f)
        return Cosa(forma, pie) {
            cuerpo(a, forma, pie.x - 12f, pie.x + 12f, pie.y - 26f, Color(0xFF6E8CB0))
            clipPath(forma) {
                drawRect(a.en(pie, 0xFFE2D8C4).copy(alpha = 0.7f), Offset(pie.x - 12f, pie.y - 34f), Size(24f, 7f))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.en(pie, 0xFF9A5A3E).copy(alpha = 0.75f)), pie.y - 14f, pie.y), Offset(pie.x - 12f, pie.y - 14f), Size(24f, 14f))
                val az = Azar(Semilla.de("neon", "bombona")); repeat(7) { mancha(Offset(pie.x + az.entre(-10f, 10f), pie.y - az.entre(4f, 46f)), az.entre(2f, 5f), az.entre(2f, 6f), if (it % 2 == 0) Color(0xFF9A5A3E) else Color.Black, 0.2f) }
                for (y in listOf(-41f, -8f)) drawLine(Color.Black.copy(alpha = 0.28f), Offset(pie.x - 12f, pie.y + y), Offset(pie.x + 12f, pie.y + y), 0.7f)
            }
            // El aro de arriba y la llave, con su manguera hacia el puesto.
            drawRoundRect(a.en(pie + Offset(0f, -54f), 0xFF56708E), Offset(pie.x - 8f, pie.y - 57f), Size(16f, 8f), CornerRadius(2f), style = Stroke(1.6f))
            drawRect(a.en(pie + Offset(0f, -54f), 0xFFC8A040), Offset(pie.x - 2f, pie.y - 55f), Size(4f, 5f)); drawCircle(a.en(pie + Offset(0f, -56f), ROJO), 2.2f, Offset(pie.x, pie.y - 56.4f))
            drawPath(Path().apply { moveTo(pie.x + 2f, pie.y - 53f); cubicTo(pie.x + 16f, pie.y - 62f, pie.x + 4f, pie.y - 44f, pie.x - 4f, pie.y - 56f); cubicTo(pie.x - 8f, pie.y - 62f, pie.x - 2f, pie.y - 58f, pie.x - 5f, pie.y - 55f) }, a.en(pie + Offset(0f, -54f), 0xFFD8843A), style = Stroke(1.1f, cap = StrokeCap.Round))
        }
    }

    /** Dos cajas de botellas apiladas, la de arriba atravesada; asoman los cuellos de los envases. */
    fun cajas(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(66f, 282f)
        val forma = Path().apply { addRect(Rect(pie.x - 17f, pie.y - 18f, pie.x + 17f, pie.y)); addPath(poligono(Offset(pie.x - 13f, pie.y - 18f), Offset(pie.x + 20f, pie.y - 20f), Offset(pie.x + 20f, pie.y - 36f), Offset(pie.x - 13f, pie.y - 34f))) }
        fun DrawScope.caja(x0: Float, y0: Float, w: Float, h: Float, c: Color, ladeo: Float) {
            val f = poligono(Offset(x0, y0), Offset(x0 + w, y0 - ladeo), Offset(x0 + w, y0 - ladeo - h), Offset(x0, y0 - h))
            cuerpoDe(a, f, x0, x0 + w, y0 - h / 2f, c)
            clipPath(f) {
                for (i in 0..4) for (k in 0..1) drawRoundRect(Color.Black.copy(alpha = 0.42f), Offset(x0 + 2.6f + i * (w - 3f) / 5f, y0 - h + 3f + k * (h - 4f) / 2f - ladeo * i / 5f), Size((w - 3f) / 5f - 2.2f, (h - 4f) / 2f - 2.4f), CornerRadius(0.8f))
                drawLine(Color.White.copy(alpha = 0.16f), Offset(x0, y0 - h + 0.8f), Offset(x0 + w, y0 - h - ladeo + 0.8f), 0.8f)
            }
        }
        return Cosa(forma, pie) {
            caja(pie.x - 17f, pie.y, 34f, 18f, ROJO, 0f)
            // Los cuellos de las botellas vacías.
            for (i in 0..5) { val x = pie.x - 10f + i * 5.4f; drawRoundRect(a.en(Offset(x, pie.y - 38f), 0xFF4A8A6A), Offset(x, pie.y - 40f - (i % 2)), Size(2.6f, 6f), CornerRadius(1f)); drawLine(Color.White.copy(alpha = 0.3f), Offset(x + 0.6f, pie.y - 39.6f - (i % 2)), Offset(x + 0.6f, pie.y - 35f), 0.4f) }
            caja(pie.x - 13f, pie.y - 18f, 33f, 16f, Color(0xFFCFA83C), 2f)
        }
    }

    private fun DrawScope.cuerpoDe(a: Alumbrado, f: Path, x0: Float, x1: Float, y: Float, c: Color) = cuerpo(a, f, x0, x1, y, c)

    /** El cubo de fregar con su fregona, apoyada en las cajas. */
    fun cubo(a: Alumbrado): Cosa {
        val pie = Offset(106f, 283f)
        val forma = Path().apply { addPath(poligono(Offset(pie.x - 9f, pie.y - 15f), Offset(pie.x + 9f, pie.y - 15f), Offset(pie.x + 7f, pie.y), Offset(pie.x - 7f, pie.y))); addPath(poligono(Offset(pie.x + 2f, pie.y - 14f), Offset(pie.x + 4.4f, pie.y - 14f), Offset(pie.x - 13f, pie.y - 70f), Offset(pie.x - 15f, pie.y - 70f))) }
        return Cosa(forma, pie) {
            // El palo, por detrás del cubo.
            drawLine(a.en(pie + Offset(-6f, -42f), 0xFFB09068), pie + Offset(3f, -12f), pie + Offset(-14f, -70f), 2f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.2f), pie + Offset(2.4f, -12f), pie + Offset(-14.6f, -70f), 0.5f)
            val balde = poligono(Offset(pie.x - 9f, pie.y - 15f), Offset(pie.x + 9f, pie.y - 15f), Offset(pie.x + 7f, pie.y), Offset(pie.x - 7f, pie.y))
            cuerpo(a, balde, pie.x - 9f, pie.x + 9f, pie.y - 8f, Color(0xFF4A8AC0))
            drawOval(a.en(pie + Offset(0f, -15f), 0xFF22304E), Offset(pie.x - 9f, pie.y - 17f), Size(18f, 4f)); drawOval(a.en(pie + Offset(0f, -15f), 0xFF8AB8E0), Offset(pie.x - 9f, pie.y - 17f), Size(18f, 4f), style = Stroke(0.7f))
            // Los flecos de la fregona, que asoman.
            for (i in 0..5) drawLine(a.en(pie + Offset(0f, -18f), 0xFFD8D0C0), pie + Offset(1f, -15f), pie + Offset(-5f + i * 2.4f, -20f - (i % 2) * 1.6f), 1.1f, StrokeCap.Round)
            drawArc(a.en(pie + Offset(0f, -12f), ACERO), 180f, 180f, false, Offset(pie.x - 9f, pie.y - 22f), Size(18f, 14f), style = Stroke(0.6f))
        }
    }

    /** Un banco de plástico rojo. [k]: su tamaño. [cojea]: cuánto se ladea, en grados. */
    fun banco(a: Alumbrado, pie: Offset, k: Float, cojea: Float): Cosa {
        val w = 8.4f * k; val h = 21f * k
        val forma = poligono(listOf(pie + Offset(-w - 1.6f * k, 0f), pie + Offset(-w + 1.6f * k, -h), pie + Offset(w - 1.6f * k, -h), pie + Offset(w + 1.6f * k, 0f)))
        return Cosa(forma, pie) {
            rotate(cojea, pie) {
                val rojo = Color(0xFFD8463E)
                // Las patas de atrás, las de delante y el travesaño.
                for (dx in listOf(-0.62f, 0.62f)) drawLine(a.en(pie, lerp(rojo, Color.Black, 0.45f)), pie + Offset(w * dx, -h + 2f * k), pie + Offset(w * dx * 1.2f, -2.4f * k), 2f * k, StrokeCap.Round)
                drawLine(a.en(pie + Offset(-w, -h / 2f), rojo, -1f), pie + Offset(-w + 1.4f * k, -h + 2f * k), pie + Offset(-w - 1.4f * k, 0f), 2.4f * k, StrokeCap.Round)
                drawLine(a.en(pie + Offset(w, -h / 2f), rojo, 1f), pie + Offset(w - 1.4f * k, -h + 2f * k), pie + Offset(w + 1.4f * k, 0f), 2.4f * k, StrokeCap.Round)
                drawLine(a.en(pie + Offset(0f, -h * 0.4f), lerp(rojo, Color.Black, 0.25f)), pie + Offset(-w, -h * 0.42f), pie + Offset(w, -h * 0.42f), 1.6f * k)
                // El asiento: su canto y su tapa, con el agujero para cogerlo.
                drawOval(a.en(pie + Offset(0f, -h), lerp(rojo, Color.Black, 0.3f)), pie + Offset(-w, -h - 1.4f * k), Size(w * 2f, 5.6f * k))
                val tapa = Path().apply { addOval(Rect(pie.x - w, pie.y - h - 3f * k, pie.x + w, pie.y - h + 2f * k)) }
                cuerpo(a, tapa, pie.x - w, pie.x + w, pie.y - h, lerp(rojo, Color.White, 0.12f))
                drawOval(Color.Black.copy(alpha = 0.4f), pie + Offset(-1.6f * k, -h - 1.2f * k), Size(3.2f * k, 1.4f * k))
            }
        }
    }

    /** Tres bancos apilados y encadenados: el puesto está cerrado. */
    fun pilaDeBancos(a: Alumbrado, pie: Offset, k: Float = 1f): Cosa {
        val w = 8.4f * k; val h = 21f * k
        val forma = poligono(listOf(pie + Offset(-w - 1.6f, 0f), pie + Offset(-w + 1.6f, -h - 12f * k), pie + Offset(w - 1.6f, -h - 12f * k), pie + Offset(w + 1.6f, 0f)))
        return Cosa(forma, pie) {
            for (i in 0..2) with(banco(a, pie + Offset(0f, -6f * k * i), k, 0f)) { pintar() }
            drawLine(a.en(pie, ACERO), pie + Offset(-w - 1f, -6f * k), pie + Offset(w + 1f, -22f * k), 0.9f); drawCircle(a.en(pie, 0xFFC8A040), 1.6f, pie + Offset(-w - 1f, -6f * k))
        }
    }

    /** Las cajas de verdura de la cosecha de las torres-granja: lo único verde del callejón. */
    fun verdura(a: Alumbrado): Cosa {
        val pie = Offset(150f, 262f); val forma = redondo(pie.x - 15f, pie.y - 16f, pie.x + 15f, pie.y, 1f)
        return Cosa(forma, pie) {
            cuerpo(a, forma, pie.x - 15f, pie.x + 15f, pie.y - 6f, Color(0xFFB09068))
            for (y in listOf(-4f, -9f)) drawLine(Color.Black.copy(alpha = 0.3f), pie + Offset(-15f, y), pie + Offset(15f, y), 0.6f)
            val az = Azar(Semilla.de("neon", "verdura"))
            repeat(14) { val c = pie + Offset(az.entre(-13f, 13f), az.entre(-20f, -14f)); drawOval(a.en(c, lerp(VERDE, Color(0xFFB8E07A), az.real())), c + Offset(-3f, -2.4f), Size(6f, 4.8f)) }
            for (i in 0..3) drawLine(a.en(pie + Offset(0f, -22f), 0xFFE8663A), pie + Offset(-8f + i * 5f, -15f), pie + Offset(-9f + i * 5.6f, -22f), 1.6f, StrokeCap.Round)
        }
    }

    /** El cajón de plástico puesto boca abajo, delante: encima va el objeto del día. */
    fun cajon(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(200f, 283f); val forma = poligono(listOf(pie + Offset(-25f, 0f), pie + Offset(-23f, -26f), pie + Offset(23f, -26f), pie + Offset(25f, 0f)))
        val grande = m.ficha.objeto in setOf("impermeable", "delantal")
        return Cosa(forma, pie) {
            cuerpo(a, forma, pie.x - 25f, pie.x + 25f, pie.y - 13f, Color(0xFF4A7AA8))
            clipPath(forma) {
                for (i in 0..5) for (k in 0..1) drawRoundRect(Color.Black.copy(alpha = 0.4f), Offset(pie.x - 21.6f + i * 7.4f, pie.y - 21f + k * 10f), Size(5f, 7.4f), CornerRadius(1f))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)), pie.y - 8f, pie.y), pie + Offset(-25f, -8f), Size(50f, 8f))
                val az = Azar(Semilla.de("neon", "cajon")); repeat(5) { mancha(pie + Offset(az.entre(-22f, 22f), az.entre(-24f, -2f)), az.entre(2f, 6f), az.entre(2f, 4f), Color.Black, 0.2f) }
            }
            // La tapa (el fondo del cajón), vista un poco desde arriba.
            val tapa = poligono(listOf(pie + Offset(-23f, -26f), pie + Offset(-19f, -31f), pie + Offset(19f, -31f), pie + Offset(23f, -26f)))
            drawPath(tapa, a.en(pie + Offset(0f, -30f), lerp(Color(0xFF4A7AA8), Color.White, 0.22f)))
            drawLine(Color.White.copy(alpha = 0.25f), pie + Offset(-23f, -26f), pie + Offset(23f, -26f), 0.6f)
            if (!grande) objeto(a, m.ficha.objeto, pie + Offset(0f, -28.4f)) else colgado(a, m.ficha.objeto, pie)
        }
    }

    /** Las bolsas de basura y el cartón apoyado en la pared. */
    fun bolsas(a: Alumbrado, hay: Boolean = true): Cosa {
        val pie = Offset(354f, 283f)
        val forma = if (hay) Path().apply { moveTo(pie.x - 22f, pie.y); cubicTo(pie.x - 26f, pie.y - 16f, pie.x - 12f, pie.y - 24f, pie.x - 4f, pie.y - 18f); cubicTo(pie.x + 2f, pie.y - 34f, pie.x + 22f, pie.y - 30f, pie.x + 24f, pie.y - 12f); lineTo(pie.x + 26f, pie.y); close() }
            else poligono(listOf(pie + Offset(2f, -6f), pie + Offset(6f, -52f), pie + Offset(26f, -50f), pie + Offset(26f, -4f)))
        return Cosa(forma, pie) {
            // El cartón, detrás.
            val carton = poligono(listOf(pie + Offset(2f, -6f), pie + Offset(6f, -52f), pie + Offset(26f, -50f), pie + Offset(26f, -4f)))
            drawPath(carton, a.en(pie + Offset(14f, -30f), 0xFFA8845A)); drawLine(Color.Black.copy(alpha = 0.25f), pie + Offset(4f, -30f), pie + Offset(26f, -28f), 0.6f); drawLine(Color.White.copy(alpha = 0.12f), pie + Offset(6f, -52f), pie + Offset(2f, -6f), 0.8f)
            fun bolsa(c: Offset, rx: Float, ry: Float) {
                val f = Path().apply { moveTo(c.x - rx, c.y); cubicTo(c.x - rx * 1.1f, c.y - ry * 1.2f, c.x - rx * 0.3f, c.y - ry * 1.5f, c.x, c.y - ry * 1.2f); cubicTo(c.x + rx * 0.5f, c.y - ry * 1.6f, c.x + rx * 1.15f, c.y - ry, c.x + rx, c.y); close() }
                cuerpo(a, f, c.x - rx, c.x + rx, c.y - ry * 0.7f, Color(0xFF3A3650))
                clipPath(f) { drawLine(Color.White.copy(alpha = 0.16f), c + Offset(-rx * 0.5f, -ry * 1.1f), c + Offset(-rx * 0.2f, -ry * 0.2f), 0.8f); drawLine(Color.White.copy(alpha = 0.1f), c + Offset(rx * 0.3f, -ry * 1.2f), c + Offset(rx * 0.5f, -ry * 0.3f), 0.7f) }
                drawPath(Path().apply { moveTo(c.x - 2f, c.y - ry * 1.25f); lineTo(c.x - 3.4f, c.y - ry * 1.25f - 4f); moveTo(c.x + 1f, c.y - ry * 1.25f); lineTo(c.x + 3f, c.y - ry * 1.25f - 3.4f) }, a.en(c + Offset(0f, -ry), 0xFF55506E), style = Stroke(1.2f, cap = StrokeCap.Round))
            }
            if (hay) { bolsa(pie + Offset(10f, -2f), 14f, 16f); bolsa(pie + Offset(-10f, 0f), 12f, 13f) }
        }
    }

    /** La toma de agua: roja, chata, con su tapón y su cadena. */
    fun tomaDeAgua(a: Alumbrado): Cosa {
        val pie = Offset(342f, 268f); val forma = redondo(pie.x - 6f, pie.y - 22f, pie.x + 6f, pie.y, 3f)
        return Cosa(forma, pie) {
            drawRoundRect(a.en(pie, lerp(ROJO, Color.Black, 0.3f)), pie + Offset(-8f, -4f), Size(16f, 4f), CornerRadius(1f))
            cuerpo(a, forma, pie.x - 6f, pie.x + 6f, pie.y - 12f, ROJO)
            drawRoundRect(a.en(pie + Offset(0f, -23f), lerp(ROJO, Color.White, 0.1f)), pie + Offset(-7f, -25f), Size(14f, 5f), CornerRadius(2.4f))
            drawCircle(a.en(pie + Offset(-8f, -13f), ACERO, -1f), 3f, pie + Offset(-7.4f, -13f)); drawCircle(Color.Black.copy(alpha = 0.3f), 1.2f, pie + Offset(-7.8f, -13f))
            drawArc(a.en(pie, ACERO), 0f, 180f, false, pie + Offset(-8f, -14f), Size(9f, 9f), style = Stroke(0.5f))
        }
    }

    /**
     * La bici de Kei: eléctrica, azul, con la batería bajo el sillín y el portabultos de la caja térmica. [faro]: si
     * espera entre pedidos, con el faro encendido. Con el cuadro soldado se le ve el cordón de soldadura, sin pintar.
     */
    fun bici(a: Alumbrado, pie: Offset, soldada: Boolean, faro: Boolean): Cosa {
        val r = 16f; val d = Offset(pie.x - 33f, pie.y - r); val tras = Offset(pie.x + 31f, pie.y - r)     // los ejes: la rueda de delante y la de atrás
        val pedal = Offset(pie.x + 3f, pie.y - 15f); val sillin = Offset(pie.x + 15f, pie.y - 46f); val pipa = Offset(pie.x - 23f, pie.y - 47f); val manillar = Offset(pie.x - 28f, pie.y - 55f)
        val forma = Path().apply {
            addOval(Rect(d.x - r, d.y - r, d.x + r, d.y + r)); addOval(Rect(tras.x - r, tras.y - r, tras.x + r, tras.y + r))
            addPath(poligono(listOf(d, pipa, manillar + Offset(-4f, -2f), sillin + Offset(-6f, -4f), Offset(tras.x + 12f, sillin.y - 14f), Offset(tras.x + 12f, tras.y - 14f), tras, pedal)))
        }
        return Cosa(forma, pie) {
            fun rueda(c: Offset) {
                drawCircle(a.en(c, 0xFF2A2638), r, c, style = Stroke(3f)); drawCircle(a.en(c + Offset(0f, -r), 0xFF8A86A8), r - 2.4f, c, style = Stroke(0.7f))
                for (i in 0 until 12) { val an = (PI * i / 6).toFloat(); drawLine(a.en(c, ACERO).copy(alpha = 0.55f), c, c + Offset(cos(an), sin(an)) * (r - 2.6f), 0.35f) }
                drawCircle(a.en(c, ACERO), 2f, c)
                // El filo de luz de la llanta, del lado de cada foco.
                drawArc(a.en(c + Offset(-r, 0f), 0xFFB8B4D8, -1f).copy(alpha = 0.8f), 120f, 120f, false, c + Offset(-r, -r), Size(r * 2f, r * 2f), style = Stroke(0.9f))
                drawArc(a.en(c + Offset(r, 0f), 0xFFB8B4D8, 1f).copy(alpha = 0.8f), -60f, 120f, false, c + Offset(-r, -r), Size(r * 2f, r * 2f), style = Stroke(0.9f))
            }
            // La pata de cabra, las ruedas y los guardabarros.
            drawLine(a.en(pie, ACERO), pedal + Offset(6f, 4f), Offset(pie.x + 13f, pie.y), 1.2f, StrokeCap.Round)
            rueda(d); rueda(tras)
            drawArc(a.en(tras + Offset(0f, -r), NEGRO), 200f, 130f, false, tras + Offset(-r - 2f, -r - 2f), Size(r * 2f + 4f, r * 2f + 4f), style = Stroke(1.6f, cap = StrokeCap.Round))
            // El cuadro: tubo de arriba, tubo de abajo, tubo del sillín y vainas.
            val azul = a.en(pie + Offset(-4f, -34f), AZUL); val azulLuz = a.en(pie + Offset(-10f, -40f), lerp(AZUL, Color.White, 0.35f), -1f)
            val ancho = Stroke(3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val cuadro = Path().apply { moveTo(pipa.x, pipa.y + 3f); lineTo(sillin.x - 3f, sillin.y + 7f); lineTo(pedal.x, pedal.y); lineTo(pipa.x + 1f, pipa.y + 9f); moveTo(pedal.x, pedal.y); lineTo(tras.x, tras.y); lineTo(sillin.x - 3f, sillin.y + 7f) }
            drawPath(cuadro, azul, style = ancho); drawPath(Path().apply { moveTo(pipa.x, pipa.y + 2.4f); lineTo(sillin.x - 3f, sillin.y + 6.4f) }, azulLuz, style = Stroke(0.8f, cap = StrokeCap.Round))
            drawLine(azul, pipa + Offset(0f, 2f), d, 3f, StrokeCap.Round); drawLine(azulLuz, pipa + Offset(-1f, 2f), d + Offset(-1f, 0f), 0.7f)
            if (soldada) {
                // La soldadura de Dalia: un tramo sin pintar en el tubo de abajo, con su cordón.
                val p0 = pedal + (Offset(pipa.x + 1f, pipa.y + 9f) - pedal) * 0.38f; val p1 = pedal + (Offset(pipa.x + 1f, pipa.y + 9f) - pedal) * 0.72f
                drawLine(a.en(p0, 0xFFA8A4B8), p0, p1, 3.6f, StrokeCap.Butt); drawLine(a.en(p0, 0xFF8A4A2A), p0, p1, 1f)
                for (i in 0..5) { val q = p0 + (p1 - p0) * (i / 5f); drawCircle(a.en(q, 0xFFE8E0D0), 0.8f, q) }
            }
            // La tija, el sillín, la batería, el manillar y el portabotellas.
            drawLine(a.en(sillin, ACERO), sillin + Offset(-3f, 7f), sillin + Offset(-1f, 1f), 1.6f)
            drawRoundRect(a.en(sillin, NEGRO), sillin + Offset(-8f, -2.6f), Size(15f, 4f), CornerRadius(2f)); drawLine(Color.White.copy(alpha = 0.16f), sillin + Offset(-7f, -2f), sillin + Offset(5f, -2f), 0.6f)
            val bat = poligono(listOf(pedal + Offset(-1f, -6f), pedal + Offset(8f, -24f), pedal + Offset(13.4f, -22f), pedal + Offset(5f, -3f)))
            drawPath(bat, a.en(pedal + Offset(6f, -14f), 0xFF34304A)); drawLine(a.en(pedal, 0xFF8A86A8), pedal + Offset(0f, -6f), pedal + Offset(8.4f, -23f), 0.6f)
            drawCircle(Color(0xFF7CFFB8), 0.8f, pedal + Offset(8.6f, -18f)); drawCircle(Color(0xFF7CFFB8).copy(alpha = 0.5f), 0.8f, pedal + Offset(7.2f, -15f))
            drawLine(a.en(manillar, ACERO), pipa, manillar, 1.8f, StrokeCap.Round); drawLine(a.en(manillar, NEGRO), manillar + Offset(-5f, -2f), manillar + Offset(4f, 1f), 2.4f, StrokeCap.Round)
            drawRoundRect(a.en(pedal + Offset(-8f, -20f), VERDE), pedal + Offset(-11f, -24f), Size(3.4f, 8f), CornerRadius(1f))
            // Los pedales y el plato.
            drawCircle(a.en(pedal, ACERO), 3.4f, pedal, style = Stroke(1f)); drawLine(a.en(pedal, ACERO), pedal + Offset(-4f, 5f), pedal + Offset(4f, -5f), 1.2f); drawLine(a.en(pedal, NEGRO), pedal + Offset(-6.4f, 5.4f), pedal + Offset(-2f, 5.4f), 1.8f)
            // El portabultos y la caja térmica, con su correa y su logo medio arrancado.
            drawLine(a.en(tras, ACERO), tras, Offset(tras.x + 6f, tras.y - 16f), 1f); drawLine(a.en(tras, ACERO), Offset(tras.x - 8f, tras.y - 16f), Offset(tras.x + 14f, tras.y - 16f), 1.4f)
            val caja = redondo(tras.x - 9f, tras.y - 40f, tras.x + 14f, tras.y - 16.6f, 2.4f)
            cuerpo(a, caja, tras.x - 9f, tras.x + 14f, tras.y - 28f, Color(0xFF3E4A78))
            clipPath(caja) { drawRect(a.en(tras + Offset(2f, -30f), CIAN).copy(alpha = 0.7f), Offset(tras.x - 4f, tras.y - 34f), Size(9f, 8f)); drawPath(poligono(Offset(tras.x + 1f, tras.y - 34f), Offset(tras.x + 5f, tras.y - 34f), Offset(tras.x + 5f, tras.y - 29f)), a.en(tras, 0xFF3E4A78)); drawLine(Color.Black.copy(alpha = 0.35f), Offset(tras.x - 9f, tras.y - 36f), Offset(tras.x + 14f, tras.y - 36f), 0.7f) }
            drawLine(a.en(tras + Offset(0f, -40f), 0xFFE8E0D0).copy(alpha = 0.6f), Offset(tras.x + 9f, tras.y - 40f), Offset(tras.x + 9f, tras.y - 17f), 1f)
            // El faro: encendido si espera entre pedidos.
            val luz = pipa + Offset(-4f, 4f)
            drawRoundRect(a.en(luz, NEGRO), luz + Offset(-2f, -2f), Size(4.4f, 4f), CornerRadius(1f))
            if (faro) { drawCircle(Color(0xFFF8FBFF), 1.6f, luz + Offset(-1.6f, 0f)); resplandor(luz + Offset(-2f, 0f), 13f, 0.7f, Color(0xFFEFF4FF)); drawPath(poligono(luz + Offset(-2f, -1f), luz + Offset(-46f, 18f), luz + Offset(-30f, 30f)), Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFFEFF4FF).copy(alpha = 0.22f)), luz.x - 46f, luz.x)) }
            else drawCircle(a.en(luz, 0xFF8A86A8), 1.4f, luz + Offset(-1.6f, 0f))
        }
    }

    // ---------------------------------------------------------------- Los objetos del día

    /** El impermeable de Kei o el delantal de Amaru, echados sobre el cajón: son demasiado grandes para ir encima. */
    private fun DrawScope.colgado(a: Alumbrado, id: String?, pie: Offset) {
        val amarillo = id == "impermeable"; val c = if (amarillo) AMARILLO else Color(0xFF5A5672)
        val tela = Path().apply { moveTo(pie.x - 22f, pie.y - 32f); cubicTo(pie.x - 8f, pie.y - 36f, pie.x + 10f, pie.y - 35f, pie.x + 21f, pie.y - 31f); lineTo(pie.x + 26f, pie.y - 8f); cubicTo(pie.x + 18f, pie.y - 3f, pie.x + 12f, pie.y - 9f, pie.x + 4f, pie.y - 4f); cubicTo(pie.x - 4f, pie.y - 1f, pie.x - 12f, pie.y - 8f, pie.x - 25f, pie.y - 5f); close() }
        cuerpo(a, tela, pie.x - 25f, pie.x + 26f, pie.y - 20f, c)
        clipPath(tela) {
            for (x in listOf(-12f, 2f, 14f)) drawPath(Path().apply { moveTo(pie.x + x, pie.y - 34f); cubicTo(pie.x + x - 3f, pie.y - 22f, pie.x + x + 4f, pie.y - 14f, pie.x + x - 1f, pie.y - 2f) }, Color.Black.copy(alpha = 0.22f), style = Stroke(1.4f))
            drawLine(Color.White.copy(alpha = 0.22f), pie + Offset(-20f, -31f), pie + Offset(18f, -31f), 0.8f)
        }
        if (amarillo) {
            // La capucha y el remiendo de cinta en el codo.
            drawPath(Path().apply { moveTo(pie.x - 9f, pie.y - 34f); cubicTo(pie.x - 8f, pie.y - 44f, pie.x + 7f, pie.y - 44f, pie.x + 8f, pie.y - 34f); close() }, a.en(pie + Offset(0f, -40f), lerp(AMARILLO, Color.White, 0.18f)))
            drawOval(a.en(pie + Offset(0f, -38f), lerp(AMARILLO, Color.Black, 0.45f)), pie + Offset(-5f, -40f), Size(10f, 5f))
            drawRect(a.en(pie + Offset(16f, -18f), ACERO), pie + Offset(13f, -21f), Size(7f, 4.4f)); drawLine(Color.Black.copy(alpha = 0.2f), pie + Offset(13f, -19f), pie + Offset(20f, -19f), 0.4f)
        } else {
            // El peto, las cintas y los dos bolsillos, con sus manchas de caldo.
            drawLine(a.en(pie + Offset(0f, -34f), 0xFFB0A8C8), pie + Offset(-8f, -34f), pie + Offset(-14f, -42f), 1.2f, StrokeCap.Round); drawLine(a.en(pie + Offset(0f, -34f), 0xFFB0A8C8), pie + Offset(8f, -34f), pie + Offset(15f, -41f), 1.2f, StrokeCap.Round)
            for (x in listOf(-14f, 4f)) drawRoundRect(a.en(pie + Offset(x + 5f, -18f), 0xFFB0A8C8), pie + Offset(x, -22f), Size(11f, 9f), CornerRadius(1f), style = Stroke(0.7f))
            drawLine(a.en(pie + Offset(8f, -24f), 0xFFE8C83A), pie + Offset(8f, -22f), pie + Offset(10f, -28f), 1f)
            val az = Azar(Semilla.de("neon", "delantal")); repeat(6) { mancha(pie + Offset(az.entre(-18f, 18f), az.entre(-28f, -8f)), az.entre(1.5f, 4f), az.entre(1.5f, 3f), Color(0xFFB89A6A), 0.35f) }
        }
    }

    /** El objeto del día [id], apoyado en [s] (el centro de la tapa del cajón). Sin objeto, no dibuja nada. */
    fun DrawScope.objeto(a: Alumbrado, id: String?, s: Offset) {
        fun tin(c: Long, mira: Float = 0f) = a.en(s, Color(c), mira)
        fun tin(c: Color, mira: Float = 0f) = a.en(s, c, mira)
        fun asiento(w: Float) = drawOval(Color.Black.copy(alpha = 0.34f), s + Offset(-w / 2f, -1.4f), Size(w, 3.4f))
        when (id) {
            // ---------------- Los de Kei
            "casco" -> { asiento(24f)
                val f = Path().apply { moveTo(s.x - 12f, s.y); cubicTo(s.x - 14f, s.y - 18f, s.x + 12f, s.y - 20f, s.x + 13f, s.y - 2f); lineTo(s.x + 13f, s.y); close() }
                cuerpo(a, f, s.x - 13f, s.x + 13f, s.y - 9f, Color(0xFFE6E2E8))
                clipPath(f) { for (i in 0..3) drawLine(Color.Black.copy(alpha = 0.22f), s + Offset(-9f + i * 5f, -15f + i), s + Offset(-5f + i * 5.6f, -6f - i), 0.5f); for (i in 0..2) drawOval(Color.Black.copy(alpha = 0.5f), s + Offset(-6f + i * 6f, -16f + (i % 2)), Size(3.4f, 1.6f)) }
                drawCircle(tin(0xFFE8843A), 2f, s + Offset(6f, -6f)); drawPath(poligono(s + Offset(4.4f, -7.4f), s + Offset(5f, -9.6f), s + Offset(6f, -7.8f)), tin(0xFFE8843A)); drawPath(poligono(s + Offset(7.6f, -7.4f), s + Offset(7f, -9.6f), s + Offset(6f, -7.8f)), tin(0xFFE8843A))
                drawLine(tin(NEGRO), s + Offset(-12f, -1f), s + Offset(13f, -1f), 1.4f); drawLine(tin(NEGRO), s + Offset(-8f, 0f), s + Offset(-3f, 3f), 0.8f) }
            "bateria" -> { asiento(24f)
                val f = redondo(s.x - 11f, s.y - 12f, s.x + 11f, s.y, 2f); cuerpo(a, f, s.x - 11f, s.x + 11f, s.y - 6f, Color(0xFF4A4862))
                drawRoundRect(tin(0xFF8A86A8), s + Offset(-5f, -15.4f), Size(10f, 4.4f), CornerRadius(2f), style = Stroke(1.2f))
                for (i in 0..3) drawRect(if (i < 3) Color(0xFF7CFFB8) else tin(0xFF2A2638), s + Offset(-7f + i * 3.6f, -8.6f), Size(2.4f, 3f)); drawRect(tin(0xFFC8A040), s + Offset(7f, -4f), Size(3f, 2.4f)); drawLine(Color.Black.copy(alpha = 0.3f), s + Offset(-11f, -3.4f), s + Offset(11f, -3.4f), 0.5f) }
            "candado" -> { asiento(26f)
                for (i in 0..6) { val c = s + Offset(-10f + i * 3.4f, -2.6f - (if (i % 2 == 0) 1.6f else 0f)); drawOval(tin(ACERO), c + Offset(-2.4f, -1.8f), Size(4.8f, 3.6f), style = Stroke(1.3f)) }
                for (i in 0..3) { val c = s + Offset(-4f + i * 3.2f, -6.4f); drawOval(tin(0xFF7A7E96), c + Offset(-2.2f, -1.6f), Size(4.4f, 3.2f), style = Stroke(1.2f)) }
                drawRoundRect(tin(0xFFC8A040), s + Offset(7f, -8f), Size(8f, 7.4f), CornerRadius(1.2f)); drawArc(tin(ACERO), 180f, 180f, false, s + Offset(8.4f, -12f), Size(5.2f, 7f), style = Stroke(1.3f)); drawCircle(Color.Black.copy(alpha = 0.5f), 0.8f, s + Offset(11f, -4.4f)) }
            "caja" -> { asiento(28f)
                val f = redondo(s.x - 12f, s.y - 22f, s.x + 12f, s.y, 2.6f); cuerpo(a, f, s.x - 12f, s.x + 12f, s.y - 11f, Color(0xFF3E4A78))
                clipPath(f) { drawRect(tin(CIAN).copy(alpha = 0.7f), s + Offset(-6f, -15f), Size(11f, 9f)); drawPath(poligono(s + Offset(0f, -15f), s + Offset(5f, -15f), s + Offset(5f, -9f)), tin(0xFF3E4A78)); drawLine(Color.Black.copy(alpha = 0.4f), s + Offset(-12f, -18f), s + Offset(12f, -18f), 0.7f) }
                for (x in listOf(-8f, 8f)) drawLine(tin(NEGRO), s + Offset(x, -22f), s + Offset(x * 1.3f, 1f), 1.6f); drawLine(Color.White.copy(alpha = 0.2f), s + Offset(-11f, -21.4f), s + Offset(11f, -21.4f), 0.6f) }
            "guantes" -> { asiento(24f)
                for ((dx, giro) in listOf(-6f to -14f, 6f to 12f)) rotate(giro, s + Offset(dx, 0f)) {
                    drawRoundRect(tin(0xFF3A6A8A), s + Offset(dx - 4.4f, -9f), Size(8.8f, 9f), CornerRadius(2f)); drawRect(tin(0xFF2A4A66), s + Offset(dx - 4.4f, -2.4f), Size(8.8f, 2.4f))
                    for (i in 0..3) { drawRoundRect(tin(0xFF3A6A8A), s + Offset(dx - 4.2f + i * 2.2f, -13f + (if (i == 0 || i == 3) 1f else 0f)), Size(1.9f, 5f), CornerRadius(0.9f)); drawRect(tin(0xFFD8B0A0), s + Offset(dx - 4.2f + i * 2.2f, -14f + (if (i == 0 || i == 3) 1f else 0f)), Size(1.9f, 1.4f)) } } }
            "lata" -> { asiento(10f)
                val f = redondo(s.x - 3.6f, s.y - 13f, s.x + 3.6f, s.y, 1.2f); cuerpo(a, f, s.x - 3.6f, s.x + 3.6f, s.y - 6f, Color(0xFF4CB86A))
                drawRect(tin(0xFFE8F0D0).copy(alpha = 0.8f), s + Offset(-3.6f, -9f), Size(7.2f, 3f)); drawOval(tin(ACERO), s + Offset(-3.4f, -14f), Size(6.8f, 2.2f)); drawLine(Color.White.copy(alpha = 0.4f), s + Offset(-2.2f, -12f), s + Offset(-2.2f, -1f), 0.5f) }
            "linterna" -> { asiento(22f)
                drawPath(Path().apply { moveTo(s.x - 10f, s.y - 2f); cubicTo(s.x - 12f, s.y - 9f, s.x + 8f, s.y - 10f, s.x + 10f, s.y - 3f); cubicTo(s.x + 6f, s.y + 1f, s.x - 6f, s.y + 1f, s.x - 10f, s.y - 2f) }, tin(0xFF3A3650), style = Stroke(2f))
                drawRoundRect(tin(0xFF55506E), s + Offset(-4.4f, -9f), Size(9f, 7f), CornerRadius(1.6f)); drawCircle(Color(0xFFF4F8FF), 2.4f, s + Offset(0f, -5.6f)); resplandor(s + Offset(0f, -5.6f), 12f, 0.6f, Color(0xFFEFF4FF)) }
            "parche" -> { asiento(18f)
                drawOval(tin(0xFF8A2E2A), s + Offset(-8f, -3.4f), Size(16f, 4.6f)); drawOval(tin(ROJO), s + Offset(-8f, -5.6f), Size(16f, 4.6f)); drawOval(tin(CREMA).copy(alpha = 0.85f), s + Offset(-4.4f, -4.6f), Size(8.8f, 2.4f))
                drawOval(tin(0xFF2A2638), s + Offset(8f, -2.6f), Size(6f, 2.6f)); drawOval(tin(0xFFE8843A), s + Offset(9f, -2.2f), Size(4f, 1.6f)); drawOval(tin(0xFF2A2638), s + Offset(-14f, -2f), Size(5f, 2.2f)) }
            "auricular" -> { asiento(18f)
                drawPath(Path().apply { moveTo(s.x + 2f, s.y - 3f); cubicTo(s.x + 12f, s.y - 12f, s.x + 14f, s.y + 2f, s.x + 4f, s.y - 1f); cubicTo(s.x - 6f, s.y - 4f, s.x - 14f, s.y + 2f, s.x - 11f, s.y - 3f) }, tin(0xFFD8D0E0), style = Stroke(0.6f))
                drawOval(tin(0xFFE6E2E8), s + Offset(-1.6f, -6.4f), Size(5f, 5f)); drawOval(tin(0xFF8A86A8), s + Offset(0f, -5f), Size(2f, 2.2f)); drawRoundRect(tin(0xFFE6E2E8), s + Offset(1f, -3f), Size(2f, 3.4f), CornerRadius(0.8f)); drawRect(tin(0xFF55506E), s + Offset(-12.6f, -4.4f), Size(2.6f, 2.6f)) }
            "llave" -> { asiento(22f)
                drawPath(Path().apply { moveTo(s.x - 10f, s.y - 1f); cubicTo(s.x - 4f, s.y - 12f, s.x + 6f, s.y - 12f, s.x + 11f, s.y - 2f) }, tin(0xFFB84A4C), style = Stroke(0.7f))
                val c = s + Offset(0f, -3.4f); for (i in 0 until 6) { val an = (PI * i / 3 + 0.3).toFloat(); val q = c + Offset(cos(an) * 6.4f, sin(an) * 2.6f); drawLine(tin(ACERO), c, q, 1.6f, StrokeCap.Round); drawCircle(tin(0xFF55506E), 0.9f, q) }
                drawCircle(tin(0xFFD8DCEA), 2f, c); drawCircle(Color.Black.copy(alpha = 0.4f), 0.8f, c) }
            "barra" -> { asiento(22f)
                val f = poligono(listOf(s + Offset(-11f, -1f), s + Offset(-9f, -6f), s + Offset(9f, -6.6f), s + Offset(11f, -1.6f))); cuerpo(a, f, s.x - 11f, s.x + 11f, s.y - 4f, Color(0xFFC4C8DA))
                for (x in listOf(-9.6f, 9.6f)) for (k in 0..2) drawLine(Color.Black.copy(alpha = 0.3f), s + Offset(x, -5.6f + k * 1.6f), s + Offset(x + (if (x < 0) -2f else 2f), -5.2f + k * 1.6f), 0.4f)
                drawRect(tin(0xFF7A4ACA).copy(alpha = 0.8f), s + Offset(-5f, -5.6f), Size(9f, 3.6f)); drawLine(Color.White.copy(alpha = 0.5f), s + Offset(-8f, -5.4f), s + Offset(7f, -5.9f), 0.5f) }
            // ---------------- Los de Amaru
            "olla" -> { asiento(30f)
                val f = redondo(s.x - 12f, s.y - 21f, s.x + 12f, s.y, 2.4f); cuerpo(a, f, s.x - 12f, s.x + 12f, s.y - 10f, Color(0xFFB4B8CC))
                clipPath(f) { for (y in listOf(-7f, -14f)) drawLine(Color.Black.copy(alpha = 0.16f), s + Offset(-12f, y), s + Offset(12f, y), 0.5f); drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF4A2A1A).copy(alpha = 0.4f)), s.y - 6f, s.y), s + Offset(-12f, -6f), Size(24f, 6f)) }
                drawRoundRect(tin(0xFFD8DCEA), s + Offset(-13.4f, -23f), Size(26.8f, 3f), CornerRadius(1.5f)); for (x in listOf(-15.4f, 12.4f)) drawRoundRect(tin(0xFF7A7E96), s + Offset(x, -17f), Size(3f, 4.6f), CornerRadius(1f))
                drawRoundRect(tin(0xFF8A8EA6), s + Offset(-2.4f, -25.4f), Size(4.8f, 2.6f), CornerRadius(1.2f)) }
            "cucharon" -> { asiento(26f)
                drawLine(tin(0xFFC8A040), s + Offset(-3f, -3f), s + Offset(12f, -7f), 1.5f, StrokeCap.Round); drawLine(tin(0xFF3A3650), s + Offset(5f, -5.2f), s + Offset(12.4f, -7.2f), 2.4f, StrokeCap.Round)
                for (i in 0..3) drawLine(Color.White.copy(alpha = 0.2f), s + Offset(6f + i * 1.7f, -6.6f), s + Offset(6.6f + i * 1.7f, -4.6f), 0.4f)
                drawOval(tin(0xFF8A6A22), s + Offset(-12f, -6f), Size(11f, 6f)); drawOval(tin(0xFFE2C060), s + Offset(-12f, -7.4f), Size(11f, 5f)); drawOval(tin(0xFF7A5A1A), s + Offset(-10.4f, -6.6f), Size(7.8f, 3f)) }
            "tazon" -> { asiento(22f)
                val f = Path().apply { moveTo(s.x - 10f, s.y - 9f); cubicTo(s.x - 9f, s.y + 1f, s.x + 9f, s.y + 1f, s.x + 10f, s.y - 9f); close() }
                cuerpo(a, f, s.x - 10f, s.x + 10f, s.y - 5f, Color(0xFFEDE8E0)); drawOval(tin(0xFFEDE8E0), s + Offset(-10f, -11f), Size(20f, 4.4f)); drawOval(tin(0xFFB89A6A), s + Offset(-8.4f, -10.4f), Size(16.8f, 3.2f))
                drawPath(poligono(s + Offset(6f, -11f), s + Offset(8.4f, -10.6f), s + Offset(7.4f, -8.6f)), tin(0xFF8A86A0)); drawRect(tin(0xFFD8D0C4), s + Offset(-4f, -1.6f), Size(8f, 1.6f)) }
            "palillos" -> { asiento(26f)
                drawRoundRect(tin(0xFFEDE8E0), s + Offset(-10f, -3.4f), Size(6f, 3.4f), CornerRadius(1.4f))
                drawLine(tin(0xFFD8B880), s + Offset(-12f, -4f), s + Offset(13f, -2f), 1.1f, StrokeCap.Round); drawLine(tin(0xFFC8A870), s + Offset(-12f, -5.4f), s + Offset(13f, -4.6f), 1.1f, StrokeCap.Round)
                for (x in listOf(10f, 11.6f)) drawLine(Color.Black.copy(alpha = 0.3f), s + Offset(x, -4.8f), s + Offset(x, -2f), 0.3f) }
            "radio" -> { asiento(26f)
                val f = redondo(s.x - 11f, s.y - 13f, s.x + 11f, s.y, 2f); cuerpo(a, f, s.x - 11f, s.x + 11f, s.y - 6f, Color(0xFF8A5A46))
                drawCircle(tin(0xFF2A2020), 4f, s + Offset(-5f, -6.4f)); for (rr in listOf(1.4f, 2.8f)) drawCircle(tin(0xFF6A4A3E), rr, s + Offset(-5f, -6.4f), style = Stroke(0.4f))
                drawRect(tin(CREMA), s + Offset(1f, -10.6f), Size(8f, 3f)); drawLine(tin(ROJO), s + Offset(6f, -10.6f), s + Offset(6f, -7.6f), 0.5f); for (x in listOf(2.6f, 7f)) drawCircle(tin(0xFFC8A040), 1.1f, s + Offset(x, -3.6f))
                drawLine(tin(ACERO), s + Offset(9f, -13f), s + Offset(15f, -24f), 0.6f); drawCircle(tin(ACERO), 0.7f, s + Offset(15f, -24f)) }
            "trapo" -> { asiento(24f)
                val f = Path().apply { moveTo(s.x - 11f, s.y - 1f); cubicTo(s.x - 9f, s.y - 7f, s.x - 2f, s.y - 3f, s.x + 2f, s.y - 6.4f); cubicTo(s.x + 6f, s.y - 9f, s.x + 10f, s.y - 4f, s.x + 12f, s.y - 1f); cubicTo(s.x + 4f, s.y + 1f, s.x - 4f, s.y + 1f, s.x - 11f, s.y - 1f); close() }
                cuerpo(a, f, s.x - 11f, s.x + 12f, s.y - 3f, Color(0xFFB89A6A)); for (x in listOf(-5f, 1f, 7f)) drawLine(Color.Black.copy(alpha = 0.22f), s + Offset(x, -5f), s + Offset(x + 1f, 0f), 0.5f); drawLine(tin(0xFF8A3A2A).copy(alpha = 0.6f), s + Offset(-9f, -1.4f), s + Offset(10f, -1.4f), 0.5f) }
            "cebollin" -> { asiento(26f)
                for (i in 0..6) { val y = -1f - i * 0.5f; drawLine(tin(lerp(VERDE, Color(0xFFB8E07A), i / 7f)), s + Offset(-3f, y), s + Offset(13f - i * 0.8f, y - 3f + i * 0.9f), 1.1f, StrokeCap.Round); drawLine(tin(0xFFEDE8E0), s + Offset(-12f, y - 0.4f * i + 1f), s + Offset(-3f, y), 1.2f, StrokeCap.Round) }
                drawLine(tin(0xFFB84A4C), s + Offset(-4f, -5.4f), s + Offset(-3.4f, 0.6f), 1.2f); for (i in 0..3) drawLine(tin(0xFFD8D0C0), s + Offset(-12f, -1f - i * 0.6f), s + Offset(-14.4f, -2.4f + i * 1.3f), 0.4f) }
            "termo" -> { asiento(12f)
                val f = redondo(s.x - 4.4f, s.y - 20f, s.x + 4.4f, s.y, 1.6f); cuerpo(a, f, s.x - 4.4f, s.x + 4.4f, s.y - 10f, Color(0xFF9AA4BC))
                drawRoundRect(tin(0xFF55607E), s + Offset(-3.6f, -24f), Size(7.2f, 5f), CornerRadius(1.4f)); drawOval(Color.Black.copy(alpha = 0.3f), s + Offset(-1f, -13f), Size(4f, 5f)); drawOval(Color.Black.copy(alpha = 0.22f), s + Offset(-4f, -6f), Size(3f, 3f))
                drawArc(tin(0xFF55607E), -90f, 180f, false, s + Offset(2f, -16f), Size(5.4f, 9f), style = Stroke(1.1f)) }
            "cuchillo" -> { asiento(26f)
                drawPath(poligono(listOf(s + Offset(-13f, -2.4f), s + Offset(2f, -5.6f), s + Offset(3f, -1.6f), s + Offset(-9f, -0.6f))), Brush.verticalGradient(listOf(tin(0xFFE8ECF6), tin(0xFF8A8EA6)), s.y - 5.6f, s.y))
                drawRoundRect(tin(0xFF7A4A2E), s + Offset(2.6f, -5.4f), Size(10f, 3.6f), CornerRadius(1.4f)); for (x in listOf(5f, 9.6f)) drawCircle(tin(0xFFC8A040), 0.6f, s + Offset(x, -3.6f)); drawLine(Color.White.copy(alpha = 0.55f), s + Offset(-12f, -2.4f), s + Offset(1f, -5f), 0.4f) }
            "ficha" -> { asiento(14f)
                drawOval(tin(0xFF8A2E2A), s + Offset(-6f, -3f), Size(12f, 4.4f)); drawOval(tin(0xFFE8663A), s + Offset(-6f, -4.6f), Size(12f, 4.4f)); drawOval(tin(CREMA), s + Offset(-3.4f, -3.9f), Size(6.8f, 2.6f))
                drawLine(tin(NEGRO), s + Offset(-0.4f, -3.4f), s + Offset(1f, -1.8f), 0.7f); drawLine(tin(NEGRO), s + Offset(-1.6f, -3.3f), s + Offset(0.8f, -3.5f), 0.7f) }
            else -> Unit
        }
    }
}
