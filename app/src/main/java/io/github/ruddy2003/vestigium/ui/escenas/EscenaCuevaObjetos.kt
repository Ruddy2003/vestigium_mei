package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Los doce objetos del día de Ama, tal como los nombra su calendario: se dibujan sobre la laja donde ella deja sus
 * cosas (en el abrigo, junto al lecho; en la meseta, a la puerta de la tienda), más grandes de lo que serían, para
 * que se lean a tamaño de teléfono.
 *
 * **[Aproximación]**: el propulsor, la azagaya, la aguja de hueso con su ojo, la lámpara de piedra con mango, la
 * flauta de hueso y el collar de conchas son cosas que se han encontrado de esos milenios; su forma exacta aquí es un
 * dibujo libre, no la copia de ninguna pieza.
 */
internal object ObjetosCueva {
    private val C = ColorCueva

    /** Cómo se tiñe un color según le dé la luz, quede de frente o en sombra, en el sitio donde está el objeto. */
    internal class Tinte(val luz: (Color) -> Color, val medio: (Color) -> Color, val sombra: (Color) -> Color)

    val TODOS = listOf("propulsor", "azagaya", "aguja", "lampara", "ocre", "carbon", "piel", "collar", "flauta", "pedernal", "asta", "odre")

    /** El objeto [id] con el centro de su base en [o] (encima de la laja). [noche]: de 0 a 1, para la llama de la lámpara. */
    fun DrawScope.objeto(id: String?, t: Tinte, o: Offset, noche: Float) {
        fun p(x: Float, y: Float) = o + Offset(x, y)
        // La sombra de contacto, para que no flote.
        if (id != null && id in TODOS) drawOval(t.sombra(C.HOLLIN).copy(alpha = 0.3f), Offset(o.x - 20f, o.y - 2.2f), Size(40f, 4.4f))
        when (id) {
            "propulsor" -> {
                // Una vara con su gancho de asta, tallado como una cabeza, y la empuñadura liada con cuero.
                drawLine(t.sombra(C.MADERA), p(-25f, -3f), p(23f, -21f), 3.6f, StrokeCap.Round); drawLine(t.luz(C.MADERA), p(-25f, -4f), p(22f, -22f), 1.6f, StrokeCap.Round)
                for (j in 0..5) drawLine(t.medio(C.PIEL_OSC), p(-24f + j * 2.6f, -5.4f - j * 1f), p(-22.4f + j * 2.6f, -0.8f - j * 1f), 1.4f)
                drawPath(Path().apply { val a = p(20f, -21f); moveTo(a.x, a.y + 2f); cubicTo(a.x + 5f, a.y - 1f, a.x + 9f, a.y - 7f, a.x + 6f, a.y - 11f); cubicTo(a.x + 3f, a.y - 13f, a.x - 1f, a.y - 10f, a.x + 1f, a.y - 7f); cubicTo(a.x + 2f, a.y - 4f, a.x - 1f, a.y - 3f, a.x - 2f, a.y - 1.4f); close() }, t.luz(C.HUESO))
                drawCircle(t.medio(C.HOLLIN), 0.8f, p(25f, -30f)); drawLine(t.medio(C.OCRE_ROJO), p(17f, -18.6f), p(19f, -23.6f), 1.6f)
            }
            "azagaya" -> {
                // El astil largo, la punta de asta con su bisel y la ligadura de tendón teñida de ocre.
                drawLine(t.sombra(C.MADERA), p(-31f, 0f), p(28f, -40f), 2.6f, StrokeCap.Round); drawLine(t.luz(C.MADERA), p(-31f, -0.8f), p(28f, -40.8f), 1.1f, StrokeCap.Round)
                drawPath(poligono(p(24f, -36f), p(27f, -41.6f), p(40f, -50f), p(30f, -38f)), t.luz(C.HUESO)); drawLine(t.sombra(C.HUESO), p(27f, -39.6f), p(39f, -49f), 0.5f)
                for (j in 0..3) drawLine(t.medio(C.OCRE_ROJO), p(22f + j * 1.5f, -37.6f - j * 1f), p(24.4f + j * 1.5f, -34.4f - j * 1f), 1.2f)
            }
            "aguja" -> {
                // Un retal de piel con la costura a medias, la aguja de hueso con su ojo y el hilo de tendón.
                drawPath(poligono(p(-20f, -1f), p(-16f, -13f), p(8f, -15f), p(16f, -2f)), t.medio(C.PIEL)); drawPath(poligono(p(-20f, -1f), p(-16f, -13f), p(-6f, -14f), p(-9f, -1f)), t.luz(C.PIEL).copy(alpha = 0.6f))
                for (j in 0..5) drawLine(t.medio(C.HUESO), p(-10f + j * 3.4f, -6.6f), p(-8.6f + j * 3.4f, -8.8f), 0.9f)
                drawLine(t.sombra(C.HUESO), p(-2f, -3f), p(24f, -19f), 2.4f, StrokeCap.Round); drawLine(t.luz(C.HUESO), p(-2f, -3.6f), p(24f, -19.6f), 1.2f, StrokeCap.Round)
                drawCircle(t.sombra(C.HOLLIN), 0.8f, p(-0.4f, -4.2f))
                drawPath(Path().apply { val a = p(-0.4f, -4.2f); moveTo(a.x, a.y); cubicTo(a.x - 8f, a.y - 10f, a.x - 16f, a.y + 2f, a.x - 22f, a.y - 6f); cubicTo(a.x - 26f, a.y - 10f, a.x - 24f, a.y - 16f, a.x - 20f, a.y - 14f) }, t.luz(C.HUESO), style = Stroke(0.6f))
            }
            "lampara" -> {
                // La lámpara de piedra con mango: el cuenco con su grasa y la mecha encendida.
                val cuenco = Path().apply { moveTo(o.x - 14f, o.y - 8f); cubicTo(o.x - 13f, o.y + 1f, o.x + 9f, o.y + 1f, o.x + 10f, o.y - 8f); close() }
                drawPath(cuenco, Brush.horizontalGradient(listOf(t.luz(C.CALIZA), t.medio(C.ROCA), t.sombra(C.ROCA)), o.x - 14f, o.x + 10f))
                drawPath(poligono(p(8f, -8f), p(28f, -6.4f), p(28f, -2.6f), p(8f, -3f)), t.medio(C.ROCA)); drawLine(t.luz(C.CALIZA), p(8f, -8f), p(28f, -6.4f), 0.8f)
                drawOval(t.medio(lerp(C.HUESO, C.OCRE_AMARILLO, 0.3f)), Offset(o.x - 13f, o.y - 10.4f), Size(22f, 4.6f)); drawLine(t.medio(C.HOLLIN), p(-2f, -9f), p(-2f, -12f), 0.9f)
                drawPath(Path().apply { val a = p(-2f, -11f); moveTo(a.x - 2.2f, a.y); cubicTo(a.x - 2.6f, a.y - 4f, a.x - 0.4f, a.y - 6f, a.x, a.y - 10f); cubicTo(a.x + 0.8f, a.y - 6f, a.x + 2.6f, a.y - 4f, a.x + 2.2f, a.y); close() }, lerp(C.LUMBRE, Color.White, 0.35f))
            }
            "ocre" -> {
                // La piedra de moler con su polvo rojo, la mano de moler y tres terrones.
                drawOval(t.sombra(C.ROCA), Offset(o.x - 22f, o.y - 6f), Size(30f, 7f)); drawOval(t.medio(C.CALIZA), Offset(o.x - 22f, o.y - 7.6f), Size(30f, 6.4f)); drawOval(t.medio(C.OCRE_ROJO), Offset(o.x - 17f, o.y - 7f), Size(18f, 4.4f))
                drawOval(t.luz(C.ROCA), Offset(o.x - 6f, o.y - 12f), Size(10f, 6f))
                for ((x, y, w, c) in listOf(Cuatro(10f, -3f, 9f, C.OCRE_ROJO), Cuatro(18f, -2f, 7f, C.OCRE_AMARILLO), Cuatro(14f, -8f, 6.4f, lerp(C.OCRE_ROJO, C.HOLLIN, 0.3f)))) {
                    drawPath(poligono(p(x - w * 0.5f, y + 2f), p(x - w * 0.3f, y - w * 0.5f), p(x + w * 0.3f, y - w * 0.6f), p(x + w * 0.55f, y + 2f)), t.medio(c)); drawPath(poligono(p(x - w * 0.5f, y + 2f), p(x - w * 0.3f, y - w * 0.5f), p(x, y + 2f)), t.luz(c).copy(alpha = 0.7f))
                }
                drawPoints(listOf(p(-24f, -1f), p(-20f, 0.4f), p(4f, -1f), p(8f, 0.6f), p(-12f, 0.8f)), PointMode.Points, t.medio(C.OCRE_ROJO), 1.3f, StrokeCap.Round)
            }
            "carbon" -> {
                // Unos tizones para dibujar, y la mancha que dejan.
                drawOval(t.medio(C.HOLLIN).copy(alpha = 0.4f), Offset(o.x - 18f, o.y - 3f), Size(34f, 4.6f))
                for ((a, b) in listOf((p(-18f, -2f) to p(6f, -7f)), (p(-12f, -5f) to p(14f, -3f)), (p(-4f, -8f) to p(18f, -12f)), (p(2f, -2f) to p(22f, -6f)))) { drawLine(t.medio(C.HOLLIN), a, b, 3.4f, StrokeCap.Round); drawLine(t.luz(lerp(C.HOLLIN, C.ROCA, 0.4f)), a + Offset(0f, -0.8f), b + Offset(0f, -0.8f), 0.8f, StrokeCap.Round) }
                drawCircle(lerp(C.LUMBRE, C.OCRE_ROJO, 0.5f).copy(alpha = 0.5f + 0.4f * noche), 1.2f, p(18f, -12f))
            }
            "piel" -> {
                // Una piel de reno doblada: el pelo hacia fuera, el borde claro y el envés.
                val piel = Path().apply { moveTo(o.x - 24f, o.y); cubicTo(o.x - 26f, o.y - 10f, o.x - 14f, o.y - 15f, o.x, o.y - 14f); cubicTo(o.x + 14f, o.y - 15f, o.x + 26f, o.y - 10f, o.x + 24f, o.y); close() }
                drawPath(piel, Brush.verticalGradient(listOf(t.luz(C.PELO), t.medio(C.PELO), t.sombra(C.PIEL_OSC)), o.y - 15f, o.y))
                drawPath(Path().apply { moveTo(o.x - 24f, o.y); cubicTo(o.x - 12f, o.y - 6f, o.x + 12f, o.y - 6f, o.x + 24f, o.y) }, t.luz(C.HUESO), style = Stroke(1.6f))
                for (j in 0..16) { val x = -20f + j * 2.5f; drawLine(t.sombra(C.PIEL_OSC).copy(alpha = 0.6f), p(x, -8f - (j % 3)), p(x + 1f, -4.6f - (j % 3)), 0.5f) }
                drawPath(poligono(p(10f, -14f), p(22f, -20f), p(26f, -10f)), t.medio(C.PIEL)); drawLine(t.luz(C.HUESO), p(22f, -20f), p(26f, -10f), 0.8f)
            }
            "collar" -> {
                // Conchas del mar y dientes ensartados en un cordón, dejado en un arco.
                val cordon = Path().apply { moveTo(o.x - 20f, o.y - 12f); cubicTo(o.x - 16f, o.y + 2f, o.x + 16f, o.y + 2f, o.x + 20f, o.y - 12f) }
                drawPath(cordon, t.medio(C.PIEL_OSC), style = Stroke(0.8f))
                for (j in 0..8) {
                    val k = j / 8f; val u = 1f - k
                    val q = Offset(u * u * u * (o.x - 20f) + 3f * u * u * k * (o.x - 16f) + 3f * u * k * k * (o.x + 16f) + k * k * k * (o.x + 20f), u * u * u * (o.y - 12f) + 3f * u * u * k * (o.y + 2f) + 3f * u * k * k * (o.y + 2f) + k * k * k * (o.y - 12f))
                    if (j % 4 == 2) drawPath(poligono(q + Offset(-1.6f, 0f), q + Offset(1.6f, 0f), q + Offset(0.4f, 6f)), t.luz(C.HUESO))
                    else { drawOval(t.luz(lerp(C.HUESO, C.OCRE_AMARILLO, 0.15f * (j % 3))), Offset(q.x - 2.4f, q.y - 1f), Size(4.8f, 5f)); drawLine(t.sombra(C.PIEL).copy(alpha = 0.6f), q + Offset(0f, 0f), q + Offset(0f, 3.6f), 0.4f) }
                }
            }
            "flauta" -> {
                // Un hueso de ave con cuatro agujeros.
                drawLine(t.sombra(C.HUESO), p(-22f, -3f), p(22f, -9f), 4.4f, StrokeCap.Round); drawLine(t.luz(C.HUESO), p(-22f, -4.2f), p(22f, -10.2f), 1.8f, StrokeCap.Round)
                for (j in 0..3) drawCircle(t.sombra(C.HOLLIN), 1.1f, p(-8f + j * 6.4f, -5.4f - j * 0.88f))
                drawLine(t.sombra(C.PIEL).copy(alpha = 0.7f), p(-20f, -5.6f), p(-19f, -1.2f), 0.6f); drawLine(t.sombra(C.PIEL).copy(alpha = 0.7f), p(19f, -11.2f), p(20f, -6.8f), 0.6f)
            }
            "pedernal" -> {
                // El núcleo, con su corteza clara; el percutor redondo y las lascas.
                drawPath(poligono(p(-18f, 0f), p(-20f, -9f), p(-10f, -15f), p(0f, -12f), p(2f, 0f)), t.medio(lerp(C.HOLLIN, C.PIEL_OSC, 0.4f)))
                drawPath(poligono(p(-20f, -9f), p(-10f, -15f), p(-8f, -7f)), t.luz(C.HUESO)); drawPath(poligono(p(-10f, -15f), p(0f, -12f), p(-4f, -5f), p(-8f, -7f)), t.luz(lerp(C.HOLLIN, C.ROCA, 0.5f)))
                drawOval(t.sombra(C.ROCA), Offset(o.x + 6f, o.y - 9f), Size(13f, 10f)); drawOval(t.luz(C.ROCA), Offset(o.x + 7f, o.y - 9f), Size(8f, 5f))
                for ((x, y, w) in listOf(Triple(20f, -1f, 6f), Triple(24f, -4f, 4.4f), Triple(-24f, -1f, 4.4f), Triple(14f, 0.6f, 3.6f))) drawPath(poligono(p(x - w * 0.5f, y), p(x, y - w * 0.56f), p(x + w * 0.5f, y + 0.4f)), t.luz(lerp(C.HOLLIN, C.CALIZA, 0.55f)))
            }
            "asta" -> {
                // Un asta de reno: la vara, que se curva, la corona de la base y sus puntas.
                val vara = Path().apply { moveTo(o.x - 24f, o.y - 2f); cubicTo(o.x - 10f, o.y - 6f, o.x + 4f, o.y - 10f, o.x + 14f, o.y - 24f); cubicTo(o.x + 18f, o.y - 31f, o.x + 26f, o.y - 34f, o.x + 30f, o.y - 30f) }
                drawPath(vara, t.sombra(lerp(C.HUESO, C.PIEL, 0.4f)), style = Stroke(4.2f, cap = StrokeCap.Round)); drawPath(Path().apply { addPath(vara, Offset(0f, -0.9f)) }, t.luz(C.HUESO), style = Stroke(1.6f, cap = StrokeCap.Round))
                for ((a, b) in listOf(p(-12f, -5.4f) to p(-10f, -17f), p(4f, -11f) to p(-4f, -22f), p(14f, -24f) to p(6f, -34f), p(22f, -33f) to p(20f, -42f))) drawLine(t.medio(lerp(C.HUESO, C.PIEL, 0.25f)), a, b, 2.4f, StrokeCap.Round)
                drawCircle(t.medio(lerp(C.HUESO, C.PIEL, 0.5f)), 3.2f, p(-24f, -2f))
            }
            "odre" -> {
                // Un odre de piel, lleno: el cuerpo panzudo, el cuello atado y su correa.
                val cuerpo = Path().apply { moveTo(o.x - 15f, o.y); cubicTo(o.x - 22f, o.y - 12f, o.x - 12f, o.y - 26f, o.x - 3f, o.y - 27f); cubicTo(o.x + 12f, o.y - 26f, o.x + 22f, o.y - 12f, o.x + 15f, o.y); close() }
                drawPath(cuerpo, Brush.horizontalGradient(listOf(t.luz(C.PIEL), t.medio(C.PIEL_OSC), t.sombra(C.PIEL_OSC)), o.x - 18f, o.x + 18f))
                drawPath(poligono(p(-6f, -26f), p(-4f, -34f), p(3f, -34f), p(2f, -26f)), t.medio(C.PIEL)); for (j in 0..2) drawLine(t.luz(C.HUESO), p(-6f, -28f - j * 1.6f), p(3f, -28.6f - j * 1.6f), 0.7f)
                drawPath(Path().apply { moveTo(o.x - 4f, o.y - 34f); cubicTo(o.x - 22f, o.y - 44f, o.x - 30f, o.y - 20f, o.x - 22f, o.y - 2f) }, t.medio(C.PIEL_OSC), style = Stroke(1.2f))
                drawLine(t.sombra(C.PIEL_OSC).copy(alpha = 0.6f), p(-2f, -26f), p(0f, -1f), 0.6f); drawOval(t.luz(C.PIEL).copy(alpha = 0.5f), Offset(o.x - 12f, o.y - 20f), Size(6f, 10f))
            }
            else -> Unit
        }
    }

    private data class Cuatro(val a: Float, val b: Float, val c: Float, val d: Color)
}
