package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor

/** Los doce objetos del día de Marie-Jeanne, sobre el banco de la lavandería. Van grandes, para que se lean en el teléfono. */
internal object ObjetosRevolucion {
    private val C = ColorRevolucion

    /** Los que tienen dibujo. */
    internal val TODOS = listOf("paleta", "tabla", "jabon", "ceniza", "cesta", "pan", "escarapela", "periodico", "sueldo", "farol", "pinzas", "sueco")

    /** El objeto del día, con el pie en [en] (el centro del asiento del banco). */
    fun DrawScope.objeto(m: Momento, l: Luces, r: Rayo, a: Alumbrado, en: Offset) = scale(1.25f, 1.25f, en) { dibujo(m, l, r, a, en) }

    private fun DrawScope.dibujo(m: Momento, l: Luces, r: Rayo, a: Alumbrado, en: Offset) {
        val k = r.contraste * r.dia; val der = r.lat > 0f
        fun c(col: Color) = a.en(en, col)
        fun claro(col: Color) = a.en(en, lerp(col, Color(0xFFFFF2D2), 0.32f * k + 0.1f * l.noche))
        fun osc(col: Color) = lerp(a.en(en, mult(col, r.color, 0.75f * k)), l.tono(mult(col, C.SOMBRA, 0.75f)), 0.6f * l.noche)
        fun redondo(col: Color, x0: Float, x1: Float) = Brush.horizontalGradient(if (der) listOf(osc(col), c(col), claro(col)) else listOf(claro(col), c(col), osc(col)), x0, x1)
        // La sombra corta que deja en el asiento.
        fun sombra(ancho: Float) { if (r.fuerza > 0.05f) drawOval(r.color, Offset(en.x - ancho / 2f + (if (der) -3f else 3f), en.y - 1.4f), Size(ancho, 3f), alpha = (0.6f * r.fuerza).coerceIn(0f, 1f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply) }
        when (m.ficha.objeto) {
            "paleta" -> { // la paleta de batir: una tabla corta y gruesa con su mango
                sombra(34f)
                val pala = poligono(en + Offset(-17f, -1f), en + Offset(3f, -3.4f), en + Offset(4f, -9.4f), en + Offset(-16f, -7.6f))
                drawPath(pala, redondo(lerp(C.MADERA, C.OCRE, 0.35f), en.x - 17f, en.x + 4f)); drawPath(poligono(en + Offset(-17f, -1f), en + Offset(3f, -3.4f), en + Offset(3f, -1.4f), en + Offset(-17f, 1f)), osc(C.MADERA))
                clipPath(pala) { for (i in 0..3) drawLine(osc(C.MADERA_OSC).copy(alpha = 0.4f), en + Offset(-16f, -2.4f - i * 1.6f), en + Offset(4f, -4.6f - i * 1.6f), 0.4f) }
                drawLine(c(lerp(C.MADERA, C.OCRE, 0.35f)), en + Offset(3.4f, -6.2f), en + Offset(18f, -8f), 3f, StrokeCap.Round); drawLine(claro(C.OCRE).copy(alpha = 0.7f), en + Offset(4f, -7.2f), en + Offset(17.6f, -8.8f), 0.7f)
            }
            "tabla" -> { // la tabla de lavar, de pie contra el banco: sus estrías y el jabón que le queda
                sombra(20f)
                val t = poligono(en + Offset(-8f, 0f), en + Offset(8f, 0f), en + Offset(6f, -30f), en + Offset(-6f, -30f))
                drawPath(t, redondo(lerp(C.MADERA, C.OCRE, 0.2f), en.x - 8f, en.x + 8f))
                clipPath(t) { for (i in 0..9) { drawLine(osc(C.MADERA_OSC).copy(alpha = 0.6f), en + Offset(-8f, -4f - i * 2.4f), en + Offset(8f, -4f - i * 2.4f), 0.7f); drawLine(claro(C.OCRE).copy(alpha = 0.5f), en + Offset(-8f, -4.8f - i * 2.4f), en + Offset(8f, -4.8f - i * 2.4f), 0.4f) } }
                drawOval(c(C.CAL).copy(alpha = 0.7f), en + Offset(-5f, -8f), Size(8f, 3f))
            }
            "jabon" -> { // el jabón negro, en su cuenco, con la espuma
                sombra(22f)
                drawPath(Path().apply { moveTo(en.x - 10f, en.y - 6f); cubicTo(en.x - 9f, en.y + 1f, en.x + 9f, en.y + 1f, en.x + 10f, en.y - 6f); close() }, redondo(C.TEJA, en.x - 10f, en.x + 10f))
                drawOval(osc(C.TEJA), en + Offset(-10f, -8.4f), Size(20f, 5f))
                drawPath(Path().apply { moveTo(en.x - 7f, en.y - 6.4f); cubicTo(en.x - 6f, en.y - 12f, en.x + 4f, en.y - 12.6f, en.x + 6.6f, en.y - 6.4f); close() }, redondo(lerp(C.MADERA_OSC, C.OCRE, 0.3f), en.x - 7f, en.x + 7f))
                val burbujas = listOf(Offset(5f, -9f), Offset(8f, -7.6f), Offset(6.4f, -11f), Offset(-8f, -8f), Offset(11f, -3f), Offset(13f, -1f))
                for (b in burbujas) drawCircle(c(Color.White).copy(alpha = 0.85f), 1.5f, en + b)
            }
            "ceniza" -> { // el saco de la ceniza, atado, con la que se ha salido
                sombra(26f)
                val saco = Path().apply { moveTo(en.x - 11f, en.y); cubicTo(en.x - 14f, en.y - 8f, en.x - 10f, en.y - 16f, en.x - 3f, en.y - 17f); lineTo(en.x + 3f, en.y - 17f); cubicTo(en.x + 10f, en.y - 16f, en.x + 14f, en.y - 8f, en.x + 11f, en.y); close() }
                drawPath(saco, redondo(lerp(C.PIEDRA, C.PIEDRA_OSC, 0.5f), en.x - 13f, en.x + 13f))
                clipPath(saco) { for (i in -2..2) drawLine(osc(C.PIEDRA_OSC).copy(alpha = 0.4f), en + Offset(i * 4f, -17f), en + Offset(i * 5.4f, 0f), 0.5f) }
                drawPath(poligono(en + Offset(-4f, -16.6f), en + Offset(4f, -16.6f), en + Offset(6f, -22f), en + Offset(-5f, -21.4f)), c(lerp(C.PIEDRA, C.PIEDRA_OSC, 0.3f))); drawLine(c(C.MADERA_OSC), en + Offset(-4f, -17f), en + Offset(4f, -17f), 1.2f)
                drawOval(c(lerp(C.PIEDRA_OSC, C.PIZARRA, 0.4f)).copy(alpha = 0.8f), en + Offset(10f, -2f), Size(11f, 2.6f))
            }
            "cesta" -> { // la cesta de entregar, con su asa y el paño
                sombra(28f)
                val cesta = Path().apply { moveTo(en.x - 10f, en.y); lineTo(en.x - 13f, en.y - 11f); lineTo(en.x + 13f, en.y - 11f); lineTo(en.x + 10f, en.y); close() }
                drawPath(Path().apply { moveTo(en.x - 11f, en.y - 11f); cubicTo(en.x - 9f, en.y - 26f, en.x + 9f, en.y - 26f, en.x + 11f, en.y - 11f) }, c(C.MADERA), style = Stroke(1.6f))
                drawPath(cesta, redondo(C.MIMBRE, en.x - 13f, en.x + 13f))
                clipPath(cesta) { for (i in 0..4) drawLine(osc(C.MADERA).copy(alpha = 0.5f), en + Offset(-14f, -1.6f - i * 2.2f), en + Offset(14f, -1.6f - i * 2.2f), 0.5f); for (i in -4..4) drawLine(osc(C.MADERA_OSC).copy(alpha = 0.4f), en + Offset(i * 3f, -11f), en + Offset(i * 2.4f, 0f), 0.5f) }
                drawPath(Path().apply { moveTo(en.x - 12f, en.y - 11f); cubicTo(en.x - 8f, en.y - 17f, en.x + 6f, en.y - 17f, en.x + 12f, en.y - 11f); lineTo(en.x + 14f, en.y - 7f); lineTo(en.x + 9f, en.y - 10.4f); close() }, redondo(C.CAL, en.x - 12f, en.x + 14f))
            }
            "pan" -> { // la hogaza de cuatro libras, con sus cortes, y el cuchillo
                sombra(28f)
                val pan = Path().apply { moveTo(en.x - 12f, en.y - 1f); cubicTo(en.x - 13f, en.y - 12f, en.x + 13f, en.y - 12f, en.x + 12f, en.y - 1f); cubicTo(en.x + 8f, en.y + 1f, en.x - 8f, en.y + 1f, en.x - 12f, en.y - 1f); close() }
                drawPath(pan, redondo(lerp(C.OCRE, C.TEJA, 0.4f), en.x - 13f, en.x + 13f))
                clipPath(pan) { for (i in -1..1) drawLine(c(lerp(C.CAL, C.OCRE, 0.3f)), en + Offset(i * 6f - 2f, -9f), en + Offset(i * 6f + 2f, -4f), 1f, StrokeCap.Round); drawOval(c(lerp(C.CAL, C.OCRE, 0.2f)).copy(alpha = 0.3f), en + Offset(-8f, -9.6f), Size(10f, 3f)) }
                drawLine(c(C.PIZARRA), en + Offset(13f, -1.4f), en + Offset(22f, -3f), 1.3f); drawLine(c(C.MADERA_OSC), en + Offset(22f, -3f), en + Offset(26f, -3.8f), 2f, StrokeCap.Round)
            }
            "escarapela" -> { // la escarapela de tres colores, con sus dos cintas
                sombra(22f)
                val o = en + Offset(0f, -9f)
                drawLine(c(C.ANIL), o + Offset(-3f, 5f), en + Offset(-8f, 0f), 3f); drawLine(c(C.GRANZA), o + Offset(3f, 5f), en + Offset(8f, 0.4f), 3f)
                for ((rr, col) in listOf(8.6f to C.ANIL, 5.8f to C.CAL, 3f to C.GRANZA)) { drawCircle(c(col), rr, o); drawCircle(osc(col).copy(alpha = 0.5f), rr, o, style = Stroke(0.5f)) }
                for (i in 0..11) { val ang = i * Math.PI.toFloat() / 6f; drawLine(osc(C.ANIL).copy(alpha = 0.5f), o + Offset(kotlin.math.cos(ang) * 5.8f, kotlin.math.sin(ang) * 5.8f), o + Offset(kotlin.math.cos(ang) * 8.6f, kotlin.math.sin(ang) * 8.6f), 0.4f) }
                drawCircle(claro(C.CAL).copy(alpha = 0.5f), 2f, o + Offset(if (der) 3f else -3f, -4f))
            }
            "periodico" -> { // el periódico doblado: renglones sin letras
                sombra(30f)
                val hoja = poligono(en + Offset(-14f, -1f), en + Offset(10f, -3f), en + Offset(13f, -11f), en + Offset(-11f, -9.6f))
                drawPath(poligono(en + Offset(-14f, -1f), en + Offset(10f, -3f), en + Offset(10.4f, -1.2f), en + Offset(-14f, 0.6f)), osc(C.CAL)); drawPath(hoja, c(lerp(C.CAL, C.OCRE, 0.18f)))
                clipPath(hoja) { drawRect(c(C.PIZARRA_OSC).copy(alpha = 0.75f), en + Offset(-11f, -9.4f), Size(22f, 1.5f)); for (i in 0..3) for (col in 0..1) drawLine(c(C.PIZARRA_OSC).copy(alpha = 0.5f), en + Offset(-12f + col * 12f + i * 0.5f, -3f - i * 1.5f + col * -1f), en + Offset(-2.4f + col * 12f + i * 0.5f, -3.8f - i * 1.5f + col * -1f), 0.5f) }
                drawLine(osc(C.PIEDRA_OSC).copy(alpha = 0.6f), en + Offset(-1.4f, -2f), en + Offset(1f, -10.4f), 0.4f)
            }
            "sueldo" -> { // la paga: unas monedas de cobre sobre el pañuelo
                sombra(26f)
                drawPath(poligono(en + Offset(-13f, -1f), en + Offset(2f, 1f), en + Offset(13f, -3f), en + Offset(-1f, -6f)), c(lerp(C.CAL, C.ANIL, 0.25f))); drawPath(poligono(en + Offset(-13f, -1f), en + Offset(-1f, -6f), en + Offset(-4f, -2f)), osc(lerp(C.CAL, C.ANIL, 0.3f)).copy(alpha = 0.6f))
                for ((i, p) in listOf(Offset(-4f, -3f), Offset(1f, -2.4f), Offset(4.6f, -3.6f), Offset(0f, -4.4f), Offset(-1f, -5.6f)).withIndex()) { drawOval(osc(C.COBRE), en + p + Offset(-2.8f, -0.6f), Size(5.6f, 2.6f)); drawOval(if (i % 2 == 0) c(C.COBRE) else claro(C.COBRE), en + p + Offset(-2.8f, -1.2f), Size(5.6f, 2.6f)); drawOval(claro(C.OCRE).copy(alpha = 0.6f), en + p + Offset(if (der) 0f else -2f, -1f), Size(2f, 0.9f)) }
            }
            "farol" -> { // el farol de mano: de noche, encendido
                sombra(18f)
                val encendido = l.noche > 0.3f
                if (encendido) resplandor(en + Offset(0f, -10f), 24f, 0.6f * l.noche, C.ACEITE)
                drawRect(c(C.HIERRO), en + Offset(-6f, -2f), Size(12f, 2f))
                val caja = Path().apply { moveTo(en.x - 5f, en.y - 2f); lineTo(en.x + 5f, en.y - 2f); lineTo(en.x + 5.6f, en.y - 15f); lineTo(en.x - 5.6f, en.y - 15f); close() }
                drawPath(caja, if (encendido) lerp(C.ACEITE, Color.White, 0.3f) else c(lerp(C.PIZARRA, C.CAL, 0.5f)))
                if (encendido) drawOval(Color(0xFFFFF6D8), en + Offset(-1.4f, -11f), Size(2.8f, 6f)) else drawRect(c(C.CAL), en + Offset(-0.8f, -7f), Size(1.6f, 5f))
                drawPath(caja, c(C.HIERRO), style = Stroke(0.9f)); drawLine(c(C.HIERRO), en + Offset(0f, -2f), en + Offset(0f, -15f), 0.5f)
                drawPath(poligono(en + Offset(-6.6f, -15f), en + Offset(6.6f, -15f), en + Offset(0f, -20f)), c(C.HIERRO)); drawCircle(c(C.HIERRO), 2.4f, en + Offset(0f, -22f), style = Stroke(0.8f))
            }
            "pinzas" -> { // las pinzas de tender, de madera hendida, en su montón
                sombra(26f)
                for ((p, d) in listOf(Offset(-10f, -1f) to Offset(9f, -3f), Offset(-4f, -2f) to Offset(10f, 1f), Offset(2f, -1f) to Offset(8f, -4.6f), Offset(-8f, -4f) to Offset(10f, -1f), Offset(-2f, -5f) to Offset(9f, -2.6f))) {
                    drawLine(osc(C.MADERA), en + p + Offset(0f, 0.6f), en + p + d + Offset(0f, 0.6f), 2.2f, StrokeCap.Round); drawLine(c(lerp(C.MADERA, C.OCRE, 0.45f)), en + p, en + p + d, 2f, StrokeCap.Round)
                    drawLine(osc(C.MADERA_OSC), en + p + d * 0.45f, en + p + d, 0.5f); drawCircle(claro(C.OCRE), 1.1f, en + p)
                }
            }
            "sueco" -> { // los zuecos, de una pieza, con su paja dentro
                sombra(30f)
                for ((dx, dy) in listOf(-9f to -1f, 5f to 0f)) {
                    val o = en + Offset(dx, dy)
                    val z = Path().apply { moveTo(o.x - 8f, o.y); cubicTo(o.x - 9f, o.y - 7f, o.x - 2f, o.y - 8f, o.x + 2f, o.y - 6f); cubicTo(o.x + 7f, o.y - 6f, o.x + 11f, o.y - 4f, o.x + 12f, o.y - 2f); cubicTo(o.x + 10f, o.y + 0.6f, o.x - 4f, o.y + 0.6f, o.x - 8f, o.y); close() }
                    drawPath(z, redondo(lerp(C.MADERA, C.OCRE, 0.4f), o.x - 9f, o.x + 12f))
                    drawOval(c(C.MADERA_OSC), o + Offset(-7f, -7f), Size(8f, 3.6f)); drawOval(c(C.PAJA), o + Offset(-6f, -6.8f), Size(6f, 2.4f))
                    drawLine(claro(C.OCRE).copy(alpha = 0.6f), o + Offset(3f, -5.6f), o + Offset(10f, -2.6f), 0.6f)
                }
            }
            else -> Unit
        }
        if (m.ficha.objeto == "pinzas") drawPoints(listOf(en + Offset(13f, -1f), en + Offset(16f, -0.4f)), PointMode.Points, c(lerp(C.MADERA, C.OCRE, 0.45f)), 2f, StrokeCap.Round)
    }
}
