package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp

/**
 * Los doce objetos del día de Iacopo, sobre el extremo de la mesa de moler. Cada uno se pinta alrededor del origen (el
 * punto de la mesa donde se apoya, con unas 30 unidades de ancho) y pide sus colores a [tono], que es la luz del sitio.
 * Ninguno lleva letras: el libro enseña figuras de medida, y el florín, un lirio.
 */
internal object ObjetosRenacimiento {
    private val C = ColorRenacimiento

    /** Los ids del calendario: lo vigila la prueba. */
    val TODOS = listOf("pincel", "losa", "ultramar", "huevo", "yeso", "tabla", "carboncillo", "carton", "pan_oro", "libro", "florin", "espejo")

    fun DrawScope.objeto(id: String?, en: Offset, k: Float, tono: (Color) -> Color) {
        if (id == null || id !in TODOS) return
        translate(en.x, en.y) { scale(k, k, Offset.Zero) {
            // La sombra con que se asienta en la mesa.
            drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(-14f, -1.4f), Size(28f, 4.2f))
            when (id) {
                "pincel" -> {
                    // Tres pinceles de pelo de ardilla en cañón de pluma, y uno más gordo, de cerda.
                    for ((i, x) in listOf(-11f, -7f, -3f).withIndex()) { drawLine(tono(lerp(C.MADERA, C.YESO, 0.2f)), Offset(x, -1.2f - i * 0.5f), Offset(x + 15f, -4.6f - i * 0.4f), 0.9f, StrokeCap.Round); drawLine(tono(lerp(C.YESO, C.OCRE, 0.3f)), Offset(x + 15f, -4.6f - i * 0.4f), Offset(x + 18f, -5.3f - i * 0.4f), 1.1f); drawLine(tono(listOf(C.ULTRAMAR, C.CINABRIO, lerp(C.VIGA, C.CARBON, 0.3f))[i]), Offset(x + 18f, -5.3f - i * 0.4f), Offset(x + 20.4f, -5.9f - i * 0.4f), 1.2f, StrokeCap.Round) }
                    drawLine(tono(C.MADERA), Offset(-12f, -0.6f), Offset(4f, -0.4f), 1.6f, StrokeCap.Round); drawRect(tono(lerp(C.PIEDRA, C.CARBON, 0.2f)), Offset(4f, -1.4f), Size(3f, 2f)); drawPath(Path().apply { moveTo(7f, -1.6f); lineTo(12.6f, -2f); lineTo(12.6f, 0.8f); lineTo(7f, 0.6f); close() }, tono(lerp(C.OCRE, C.YESO, 0.4f)))
                }
                "losa" -> {
                    // Una losa pequeña de pórfido, con su moleta y un cerco de tierra verde.
                    drawPath(Path().apply { moveTo(-12f, -4.6f); lineTo(10f, -4.6f); lineTo(13f, -0.6f); lineTo(-14f, -0.6f); close() }, tono(C.PORFIDO)); drawRect(tono(lerp(C.PORFIDO, C.CARBON, 0.35f)), Offset(-14f, -0.6f), Size(27f, 1.8f))
                    drawOval(tono(lerp(C.VERDE, C.YESO, 0.2f)), Offset(-9f, -3.8f), Size(12f, 2.4f))
                    drawPath(Path().apply { moveTo(3f, -2.6f); cubicTo(2.4f, -8f, 4f, -11.6f, 6.6f, -11.6f); cubicTo(9.2f, -11.6f, 10.8f, -8f, 10.2f, -2.6f); close() }, tono(lerp(C.PORFIDO, C.PIEDRA, 0.45f))); drawLine(tono(lerp(C.PIEDRA, C.YESO, 0.5f)).copy(alpha = 0.7f), Offset(4.6f, -9f), Offset(4.2f, -4f), 0.7f, StrokeCap.Round)
                }
                "ultramar" -> {
                    // La piedra azul, con sus vetas doradas, y una concha con el polvo ya lavado.
                    drawPath(Path().apply { moveTo(-12f, -0.6f); lineTo(-13f, -4.6f); lineTo(-9.4f, -8f); lineTo(-4.6f, -7.4f); lineTo(-2.4f, -3.4f); lineTo(-4f, -0.6f); close() }, tono(C.ULTRAMAR))
                    drawPath(Path().apply { moveTo(-9.4f, -8f); lineTo(-4.6f, -7.4f); lineTo(-6.6f, -4.6f); close() }, tono(lerp(C.ULTRAMAR, C.YESO, 0.3f)))
                    for ((x, y) in listOf(-10.6f to -3.4f, -7.4f to -5.4f, -5.2f to -2.4f)) drawCircle(tono(C.ORO), 0.5f, Offset(x, y))
                    drawOval(tono(lerp(C.YESO, C.CARNE, 0.25f)), Offset(1f, -4.4f), Size(12.4f, 4.6f)); drawOval(tono(C.ULTRAMAR), Offset(2.6f, -3.8f), Size(9.2f, 3f)); drawOval(tono(lerp(C.ULTRAMAR, C.YESO, 0.45f)), Offset(4f, -3.5f), Size(4f, 1.2f))
                    for (k2 in 0..4) drawLine(tono(lerp(C.YESO, C.PIEDRA, 0.4f)), Offset(2f + k2 * 2.6f, -0.8f), Offset(2.6f + k2 * 2.4f, 0.4f), 0.4f)
                }
                "huevo" -> {
                    // Una escudilla con tres huevos y, al lado, un platito con la yema para el temple.
                    for ((dx, dy) in listOf(-9f to -5.6f, -5f to -6.6f, -7f to -8.4f)) drawOval(tono(lerp(C.YESO, C.CARNE, 0.3f)), Offset(dx - 2.2f, dy - 1.6f), Size(4.4f, 3.4f))
                    drawPath(Path().apply { moveTo(-13f, -5f); lineTo(-1f, -5f); cubicTo(-2f, 0.4f, -12f, 0.4f, -13f, -5f); close() }, tono(lerp(C.COTTO, C.MURO, 0.3f)))
                    drawOval(tono(C.YESO), Offset(2f, -3.4f), Size(11f, 3.6f)); drawOval(tono(lerp(C.OCRE, C.ORO, 0.6f)), Offset(4.6f, -2.8f), Size(5.4f, 2.2f)); drawCircle(Color.White.copy(alpha = 0.6f), 0.5f, Offset(6.4f, -2.2f))
                    drawLine(tono(lerp(C.PIEDRA, C.CARBON, 0.3f)), Offset(9f, -5.6f), Offset(14f, -6.6f), 0.5f)
                }
                "yeso" -> {
                    // Un cuenco de yeso apagado, con su brocha, y unas gotas en la mesa.
                    drawPath(Path().apply { moveTo(-10f, -7f); lineTo(6f, -7f); cubicTo(5f, 0.4f, -9f, 0.4f, -10f, -7f); close() }, tono(lerp(C.MADERA, C.VIGA, 0.25f)))
                    drawOval(tono(C.YESO), Offset(-10f, -8.6f), Size(16f, 3.4f)); drawLine(tono(C.YESO), Offset(-4f, -6.6f), Offset(-3.6f, -2f), 1.2f, StrokeCap.Round)
                    drawLine(tono(C.MADERA), Offset(2f, -8f), Offset(12f, -14f), 1.5f, StrokeCap.Round); drawRect(tono(lerp(C.OCRE, C.YESO, 0.5f)), Offset(-0.6f, -9.4f), Size(3.6f, 2.6f))
                    for (x in listOf(8f, 11f, 13f)) drawOval(tono(C.YESO), Offset(x, -1.6f + x % 2f), Size(2f, 1f))
                }
                "tabla" -> {
                    // Una tabla de álamo, con la tela pegada y la mitad ya enyesada.
                    val t = Path().apply { moveTo(-13f, -1f); lineTo(-10f, -15f); lineTo(9f, -15f); lineTo(12f, -1f); close() }
                    drawPath(t, tono(lerp(C.MADERA, C.MURO, 0.25f))); drawPath(Path().apply { moveTo(-13f, -1f); lineTo(-10f, -15f); lineTo(0f, -15f); lineTo(0.6f, -1f); close() }, tono(C.YESO))
                    for (y in listOf(-12f, -8f, -4f)) drawLine(tono(lerp(C.MADERA, C.VIGA, 0.4f)).copy(alpha = 0.5f), Offset(1.6f, y), Offset(10.6f + (y + 15f) * 0.1f, y), 0.4f)
                    drawPath(t, tono(lerp(C.VIGA, C.CARBON, 0.2f)).copy(alpha = 0.6f), style = Stroke(0.5f)); drawRect(tono(lerp(C.MADERA, C.VIGA, 0.45f)), Offset(-13f, -1f), Size(25f, 1.6f))
                }
                "carboncillo" -> {
                    // Unos palitos de sauce quemado, un papel con un dibujo a medias y la miga de pan de borrar.
                    drawPath(Path().apply { moveTo(-13f, -0.6f); lineTo(-10f, -6.6f); lineTo(6f, -6.6f); lineTo(8f, -0.6f); close() }, tono(C.PAPEL))
                    drawPath(Path().apply { moveTo(-8f, -2f); cubicTo(-6f, -6f, -2f, -6f, -1f, -3f); cubicTo(0.4f, -5.6f, 3f, -5.4f, 4f, -2.4f) }, tono(lerp(C.CARBON, C.VIGA, 0.3f)).copy(alpha = 0.8f), style = Stroke(0.6f, cap = StrokeCap.Round))
                    for ((i, x) in listOf(6f, 8.6f, 11f).withIndex()) drawLine(tono(C.CARBON), Offset(x, -1f - i * 0.6f), Offset(x + 5f, -5.6f - i * 0.5f), 1.1f, StrokeCap.Round)
                    drawOval(tono(lerp(C.YESO, C.OCRE, 0.25f)), Offset(-13.6f, -3.4f), Size(4f, 2.6f)); drawOval(tono(C.CARBON).copy(alpha = 0.3f), Offset(-12.6f, -2.8f), Size(2f, 1.2f))
                }
                "carton" -> {
                    // Un cartón enrollado a medias, con el dibujo agujereado, y la muñequilla de carbón.
                    drawPath(Path().apply { moveTo(-8f, -1f); lineTo(-6f, -10f); lineTo(10f, -10f); lineTo(13f, -1f); close() }, tono(C.PAPEL))
                    drawPath(Path().apply { moveTo(-2f, -2.6f); cubicTo(-1f, -8f, 5f, -8.6f, 6f, -5f); lineTo(8.6f, -2.6f) }, tono(lerp(C.CARBON, C.VIGA, 0.3f)).copy(alpha = 0.7f), style = Stroke(0.5f))
                    for (k2 in 0..9) drawCircle(tono(C.CARBON).copy(alpha = 0.7f), 0.3f, Offset(-2f + k2 * 1.1f, -3.4f - (k2 % 4) * 1.1f))
                    drawRoundRect(tono(lerp(C.PAPEL, C.VIGA, 0.2f)), Offset(-13f, -11f), Size(7f, 11f), CornerRadius(3.2f)); drawOval(tono(lerp(C.PAPEL, C.VIGA, 0.45f)), Offset(-12.4f, -11.6f), Size(5.8f, 2f))
                    drawCircle(tono(lerp(C.CARBON, C.PIEDRA, 0.2f)), 2.3f, Offset(10f, -12.6f)); drawLine(tono(C.CARBON), Offset(10f, -14.6f), Offset(10.6f, -16.4f), 0.8f)
                }
                "pan_oro" -> {
                    // El librillo de los batidores, con una hoja de oro levantada, y el pincel plano de cogerla.
                    drawPath(Path().apply { moveTo(-13f, -0.6f); lineTo(-11f, -6f); lineTo(1f, -6f); lineTo(3f, -0.6f); close() }, tono(lerp(C.PAPEL, C.CINABRIO, 0.2f)))
                    drawPath(Path().apply { moveTo(-11f, -1.2f); lineTo(-9.6f, -5.4f); lineTo(0f, -5.4f); lineTo(1.4f, -1.2f); close() }, tono(C.ORO))
                    drawPath(Path().apply { moveTo(-5f, -5.4f); cubicTo(-3f, -10f, 1f, -11f, 3f, -8.6f); lineTo(0f, -5.4f); close() }, tono(lerp(C.ORO, C.YESO, 0.45f)))
                    drawCircle(Color.White.copy(alpha = 0.7f), 0.6f, Offset(-6f, -3.4f))
                    drawLine(tono(C.MADERA), Offset(5f, -1.4f), Offset(13f, -4f), 1f, StrokeCap.Round); drawPath(Path().apply { moveTo(3f, -3f); lineTo(6f, -1.6f); lineTo(5f, 0.6f); lineTo(1.6f, -0.6f); close() }, tono(lerp(C.YESO, C.OCRE, 0.3f)))
                }
                "libro" -> {
                    // El libro de medidas y figuras, abierto: en las hojas, un círculo, un triángulo y unas rayas, sin letras.
                    drawPath(Path().apply { moveTo(-14f, -1f); lineTo(-11f, -9f); lineTo(0f, -8f); lineTo(11f, -9f); lineTo(14f, -1f); lineTo(0f, 0.4f); close() }, tono(lerp(C.VIGA, C.CINABRIO, 0.3f)))
                    drawPath(Path().apply { moveTo(-12.6f, -1.6f); lineTo(-10f, -8.6f); cubicTo(-6f, -9.6f, -2f, -8.8f, 0f, -7.6f); lineTo(0f, -0.4f); close() }, tono(C.PAPEL)); drawPath(Path().apply { moveTo(12.6f, -1.6f); lineTo(10f, -8.6f); cubicTo(6f, -9.6f, 2f, -8.8f, 0f, -7.6f); lineTo(0f, -0.4f); close() }, tono(lerp(C.PAPEL, C.YESO, 0.3f)))
                    val tinta = tono(lerp(C.VIGA, C.CARBON, 0.4f)).copy(alpha = 0.8f)
                    drawCircle(tinta, 1.9f, Offset(-6f, -5f), style = Stroke(0.4f)); drawLine(tinta, Offset(-8f, -5f), Offset(-4f, -5f), 0.3f)
                    drawPath(Path().apply { moveTo(3.4f, -3f); lineTo(6.4f, -7.4f); lineTo(9.4f, -3f); close() }, tinta, style = Stroke(0.4f)); for (y in listOf(-2f, -1.2f)) drawLine(tinta.copy(alpha = 0.5f), Offset(3f, y), Offset(10f, y - 0.2f), 0.3f)
                    drawLine(tono(C.CINABRIO), Offset(0f, -7.6f), Offset(1.4f, 1.4f), 0.6f)
                }
                "florin" -> {
                    // Tres florines de oro fuera de su bolsita de cuero: en uno se ve el lirio.
                    drawPath(Path().apply { moveTo(-13f, -0.6f); cubicTo(-14f, -6f, -11f, -9f, -9.6f, -10f); lineTo(-5.4f, -10f); cubicTo(-4f, -9f, -1f, -6f, -2f, -0.6f); close() }, tono(lerp(C.VIGA, C.MADERA, 0.4f))); drawLine(tono(C.CARBON), Offset(-10f, -9.6f), Offset(-5f, -9.6f), 0.9f)
                    for ((i, o) in listOf(Offset(3f, -1.6f), Offset(8f, -2.6f), Offset(11.4f, -0.8f)).withIndex()) { drawOval(tono(lerp(C.ORO, C.VIGA, 0.35f)), Offset(o.x - 3f, o.y - 0.8f), Size(6f, 2.6f)); drawOval(tono(C.ORO), Offset(o.x - 3f, o.y - 1.4f), Size(6f, 2.6f)); if (i == 1) { drawLine(tono(lerp(C.ORO, C.VIGA, 0.5f)), Offset(o.x, o.y - 0.9f), Offset(o.x, o.y + 0.7f), 0.5f); drawLine(tono(lerp(C.ORO, C.VIGA, 0.5f)), Offset(o.x - 1.3f, o.y - 0.5f), Offset(o.x + 1.3f, o.y - 0.5f), 0.4f) } }
                    drawCircle(Color.White.copy(alpha = 0.7f), 0.5f, Offset(7f, -3f))
                }
                else -> {
                    // El espejo de metal pulido, pequeño y algo abombado, apoyado en un taco: devuelve la claridad de la ventana.
                    drawPath(Path().apply { moveTo(-4f, -1f); lineTo(-2f, -5f); lineTo(4f, -5f); lineTo(6f, -1f); close() }, tono(lerp(C.MADERA, C.VIGA, 0.3f)))
                    drawOval(tono(lerp(C.VIGA, C.CARBON, 0.2f)), Offset(-8f, -19.6f), Size(17f, 16.6f)); drawOval(tono(lerp(C.PIEDRA, C.YESO, 0.45f)), Offset(-6.4f, -18.2f), Size(13.8f, 13.6f))
                    drawOval(tono(lerp(C.YESO, C.ULTRAMAR, 0.2f)), Offset(-4.4f, -16.6f), Size(6.6f, 7f)); drawOval(tono(lerp(C.PIEDRA, C.CARBON, 0.3f)).copy(alpha = 0.5f), Offset(0.6f, -11f), Size(5.6f, 5.2f))
                    drawCircle(Color.White.copy(alpha = 0.8f), 0.8f, Offset(-2.4f, -14.6f))
                }
            }
        } }
    }
}
