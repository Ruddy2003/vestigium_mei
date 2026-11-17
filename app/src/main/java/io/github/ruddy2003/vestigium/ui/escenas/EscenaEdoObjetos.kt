package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de Ochiyo (los del cuento: `PiezasOchiyo.OBJETOS`). Van sobre el banco, junto a la bandeja;
 * el palanquín, grande, espera en el camino delante del banco. La tetera grande de hierro es la del fogón, que está
 * siempre: ese día, además, la tetera pequeña de hierro de los huéspedes va sobre el banco.
 */
internal object ObjetosEdo {
    private val C = ColorEdo

    val TODOS = listOf("tetera", "taza", "farol", "sombrero", "sandalias", "abanico", "dango", "ryo", "palanquin", "pipa", "peine", "furoshiki")

    /** El palanquín de camino: una cesta de bambú colgada de su vara, posada en el camino delante del banco. */
    fun grande(m: Momento, p: Pincel): Cosa? {
        if (m.ficha.objeto != "palanquin") return null
        val pie = Offset(150f, 250f)
        val forma = poligono(listOf(Offset(pie.x - 22f, pie.y), Offset(pie.x - 24f, pie.y - 24f), Offset(pie.x + 24f, pie.y - 24f), Offset(pie.x + 22f, pie.y)))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - 30f, pie.y - 3f), Size(60f, 6f))
            // La cesta: el fondo, el asiento y el respaldo de bambú trenzado, con su toldillo de estera.
            val cesta = Path().apply { moveTo(pie.x - 20f, pie.y - 2f); cubicTo(pie.x - 22f, pie.y - 12f, pie.x - 18f, pie.y - 22f, pie.x - 12f, pie.y - 24f); lineTo(pie.x + 12f, pie.y - 24f); cubicTo(pie.x + 18f, pie.y - 22f, pie.x + 22f, pie.y - 12f, pie.x + 20f, pie.y - 2f); close() }
            volumen(p.l, p.r, cesta, pie.x - 22f, pie.x + 22f, Color(0xFFB09A5A))
            clipPath(cesta) { var y = pie.y - 24f; while (y < pie.y) { drawLine(p.sombra(Color(0xFFB09A5A)).copy(alpha = 0.5f), Offset(pie.x - 24f, y), Offset(pie.x + 24f, y + 1f), 0.5f); y += 1.8f }; var x = pie.x - 24f; while (x < pie.x + 24f) { drawLine(p.luz(Color(0xFFB09A5A)).copy(alpha = 0.3f), Offset(x, pie.y - 24f), Offset(x, pie.y), 0.4f); x += 3f } }
            drawRect(p.medio(C.INDIGO), Offset(pie.x - 10f, pie.y - 18f), Size(20f, 6f))                                           // el cojín
            drawPath(Path().apply { moveTo(pie.x - 18f, pie.y - 24f); cubicTo(pie.x - 6f, pie.y - 30f, pie.x + 6f, pie.y - 30f, pie.x + 18f, pie.y - 24f); close() }, p.lado(C.PAJA))
            // La vara, larga, que sobresale por los dos lados, con sus correas.
            drawLine(p.lado(Color(0xFF9A9452)), Offset(pie.x - 60f, pie.y - 34f), Offset(pie.x + 60f, pie.y - 32f), 2.4f, StrokeCap.Round)
            for (dx in listOf(-14f, 14f)) drawLine(p.medio(C.MADERA_OSC), Offset(pie.x + dx, pie.y - 33f), Offset(pie.x + dx, pie.y - 22f), 0.9f)
            drawRect(p.medio(C.MADERA_OSC), Offset(pie.x - 58f, pie.y - 2f), Size(4f, 2f))
        }
    }

    /** Lo que va encima del banco, centrado en [s] (el sitio del objeto). */
    fun DrawScope.sobreElBanco(m: Momento, p: Pincel, s: Offset) {
        when (m.ficha.objeto) {
            "tetera" -> teteraPequena(p, s)
            "taza" -> tazas(p, s)
            "farol" -> farolPlegado(p, s)
            "sombrero" -> sombrero(p, s)
            "sandalias" -> sandalias(p, s)
            "abanico" -> abanico(p, s)
            "dango" -> dangos(p, s)
            "ryo" -> monedas(p, s)
            "pipa" -> pipa(m, p, s)
            "peine" -> peine(p, s)
            "furoshiki" -> furoshiki(p, s)
            else -> Unit
        }
    }

    /** La tetera pequeña de hierro de los huéspedes, en su salvamanteles de paja. */
    private fun DrawScope.teteraPequena(p: Pincel, s: Offset) {
        drawOval(p.medio(C.PAJA), Offset(s.x - 14f, s.y - 3f), Size(28f, 5f))
        val c = Offset(s.x, s.y - 10f)
        val cuerpo = Path().apply { moveTo(c.x - 11f, c.y); cubicTo(c.x - 13f, c.y + 8f, c.x - 8f, c.y + 10f, c.x, c.y + 10f); cubicTo(c.x + 8f, c.y + 10f, c.x + 13f, c.y + 8f, c.x + 11f, c.y); cubicTo(c.x + 6f, c.y - 3f, c.x - 6f, c.y - 3f, c.x - 11f, c.y); close() }
        volumen(p.l, p.r, cuerpo, c.x - 12f, c.x + 12f, Color(0xFF3A3638))
        clipPath(cuerpo) { var y = c.y; while (y < c.y + 10f) { var x = c.x - 12f; while (x < c.x + 12f) { drawCircle(p.luz(Color(0xFF6A6468)).copy(alpha = 0.6f), 0.4f, Offset(x + (y % 2f), y)); x += 2f }; y += 2f } }
        drawLine(p.medio(Color(0xFF3A3638)), Offset(c.x + 10f, c.y + 3f), Offset(c.x + 16f, c.y - 2f), 1.8f, StrokeCap.Round)
        drawArc(p.medio(C.SUMI), 180f, 180f, false, Offset(c.x - 8f, c.y - 10f), Size(16f, 18f), style = Stroke(1.1f))
        drawCircle(p.luz(Color(0xFF7A7478)), 1.4f, Offset(c.x, c.y - 2f))
    }

    /** Una pila de tazas de barro sin asa, y una rajada, aparte, la de los porteadores. */
    private fun DrawScope.tazas(p: Pincel, s: Offset) {
        for (k in 0 until 4) { val y = s.y - 1f - k * 3.6f; drawPath(Path().apply { moveTo(s.x - 6f + k * 0.2f, y - 4f); lineTo(s.x - 4.6f, y); lineTo(s.x + 4.6f, y); lineTo(s.x + 6f - k * 0.2f, y - 4f); close() }, p.lado(Color(0xFFB8A888), alReves = k % 2 == 0)) }
        val q = Offset(s.x + 18f, s.y - 1f)
        drawPath(Path().apply { moveTo(q.x - 6f, q.y - 5f); lineTo(q.x - 4.6f, q.y); lineTo(q.x + 4.6f, q.y); lineTo(q.x + 6f, q.y - 5f); close() }, p.lado(Color(0xFF9A8A6A)))
        drawLine(p.sombra(C.SUMI), Offset(q.x - 1f, q.y - 5f), Offset(q.x + 1f, q.y - 1.4f), 0.5f)
        drawOval(p.medio(Color(0xFF6E8A4A)), Offset(q.x - 5f, q.y - 6f), Size(10f, 2f))
    }

    /** El farol de papel plegado, que se está volviendo a empapelar: el aro, los pliegues, el pincel y el cazo de engrudo. */
    private fun DrawScope.farolPlegado(p: Pincel, s: Offset) {
        val c = Offset(s.x - 4f, s.y - 6f)
        drawOval(p.lado(C.WASHI), Offset(c.x - 12f, c.y - 5f), Size(24f, 10f))
        for (k in -4..4) drawLine(p.sombra(C.WASHI), Offset(c.x - 11f, c.y + k * 1.1f), Offset(c.x + 11f, c.y + k * 1.1f), 0.4f)
        drawRect(p.medio(C.SUMI), Offset(c.x - 12f, c.y - 6f), Size(24f, 2f)); drawRect(p.medio(C.SUMI), Offset(c.x - 12f, c.y + 4f), Size(24f, 2f))
        drawCircle(p.medio(C.BERMELLON), 2.6f, c, style = Stroke(0.7f))
        drawLine(p.lado(Color(0xFF9A9452)), Offset(s.x + 12f, s.y - 2f), Offset(s.x + 26f, s.y - 6f), 1f, StrokeCap.Round); drawCircle(p.medio(C.SUMI), 1.4f, Offset(s.x + 11f, s.y - 1.6f))
        drawOval(p.lado(C.WASHI), Offset(s.x + 18f, s.y - 3f), Size(8f, 3f))
    }

    /** Un sombrero de viaje de junco, ancho y plano, que alguien dejó en el banco, con su cordón. */
    private fun DrawScope.sombrero(p: Pincel, s: Offset) {
        val c = Offset(s.x, s.y - 4f)
        drawOval(p.sombra(C.PAJA), Offset(c.x - 24f, c.y - 2f), Size(48f, 9f))
        drawPath(Path().apply { moveTo(c.x - 24f, c.y + 2f); cubicTo(c.x - 14f, c.y - 10f, c.x + 14f, c.y - 10f, c.x + 24f, c.y + 2f); close() }, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.PAJA), p.luz(C.PAJA)) else listOf(p.luz(C.PAJA), p.sombra(C.PAJA)), c.x - 24f, c.x + 24f))
        for (k in -5..5) drawLine(p.sombra(C.PAJA).copy(alpha = 0.5f), Offset(c.x, c.y - 7f), Offset(c.x + k * 4.4f, c.y + 2f), 0.4f)
        drawPath(Path().apply { moveTo(c.x - 6f, c.y + 3f); cubicTo(c.x - 4f, c.y + 8f, c.x + 6f, c.y + 8f, c.x + 10f, c.y + 6f) }, p.medio(C.INDIGO), style = Stroke(0.7f))
    }

    /** Un par de sandalias de paja nuevas, con sus cordones, y una vieja rota al lado. */
    private fun DrawScope.sandalias(p: Pincel, s: Offset) {
        for ((dx, ang) in listOf(-8f to -0.1f, 4f to 0.12f)) rotate(ang * 57f, Offset(s.x + dx, s.y - 2f)) {
            drawOval(p.lado(C.PAJA), Offset(s.x + dx - 4f, s.y - 12f), Size(8f, 18f))
            for (k in 0 until 6) drawLine(p.sombra(C.PAJA).copy(alpha = 0.6f), Offset(s.x + dx - 3f, s.y - 9f + k * 2.4f), Offset(s.x + dx + 3f, s.y - 9f + k * 2.4f), 0.4f)
            drawLine(p.medio(C.WASHI), Offset(s.x + dx - 3f, s.y - 4f), Offset(s.x + dx, s.y - 10f), 0.6f); drawLine(p.medio(C.WASHI), Offset(s.x + dx + 3f, s.y - 4f), Offset(s.x + dx, s.y - 10f), 0.6f)
        }
        drawOval(p.medio(lerp(C.PAJA, C.TIERRA, 0.5f)), Offset(s.x + 16f, s.y - 4f), Size(12f, 5f)); drawLine(p.sombra(C.TIERRA), Offset(s.x + 18f, s.y - 3f), Offset(s.x + 24f, s.y - 1f), 0.5f)
    }

    /** El abanico plegable de papel y bambú, abierto, con unas olas pintadas. */
    private fun DrawScope.abanico(p: Pincel, s: Offset) {
        val c = Offset(s.x, s.y - 1f)
        val forma = Path().apply { moveTo(c.x, c.y); for (k in 0..10) { val a = Math.toRadians(200.0 + k * 14.0); lineTo(c.x + cos(a).toFloat() * 20f, c.y + sin(a).toFloat() * 11f) }; close() }
        drawPath(forma, p.lado(C.WASHI))
        clipPath(forma) { for (k in 0..10) { val a = Math.toRadians(200.0 + k * 14.0); drawLine((if (k % 2 == 0) p.sombra(C.WASHI) else p.luz(C.WASHI)).copy(alpha = 0.6f), c, c + Offset(cos(a).toFloat() * 20f, sin(a).toFloat() * 11f), 0.5f) }; for (y in listOf(-6f, -8f)) drawArc(p.medio(C.INDIGO), 200f, 140f, false, Offset(c.x - 14f, c.y + y), Size(28f, 6f), style = Stroke(0.7f)) }
        drawCircle(p.medio(C.SUMI), 0.8f, c)
    }

    /** Un plato con tres palitos de bolitas de arroz asadas, con su salsa dulce brillante. */
    private fun DrawScope.dangos(p: Pincel, s: Offset) {
        drawOval(p.sombra(Color(0xFFB8A888)), Offset(s.x - 18f, s.y - 3f), Size(36f, 6f)); drawOval(p.arriba(Color(0xFFD8C8A8)), Offset(s.x - 17f, s.y - 4f), Size(34f, 5f))
        for (k in 0 until 3) {
            val a = Offset(s.x - 14f + k * 2f, s.y - 3f - k * 1.2f); val b = a + Offset(26f, -3f)
            drawLine(p.medio(Color(0xFF9A9452)), a, b, 0.6f)
            for (j in 0 until 4) { val q = a + (b - a) * (0.18f + j * 0.2f); drawCircle(p.medio(Color(0xFFB07A44)), 2.6f, q); drawCircle(p.luz(Color(0xFFE0B070)), 1f, q + Offset(-0.8f, -0.9f)) }
        }
    }

    /** Las monedas de cobre ensartadas en su cordel, una ristra entera y unas sueltas, con su agujero cuadrado. */
    private fun DrawScope.monedas(p: Pincel, s: Offset) {
        val cobre = Color(0xFFA06A3A)
        for (k in 0 until 12) { val q = Offset(s.x - 18f + k * 2.2f, s.y - 3f + sin(k * 0.6f) * 1f); drawOval(p.lado(cobre, alReves = k % 2 == 0), Offset(q.x - 1.2f, q.y - 2.4f), Size(2.4f, 4.8f)) }
        drawLine(p.medio(C.INDIGO), Offset(s.x - 20f, s.y - 3f), Offset(s.x + 10f, s.y - 2f), 0.5f)
        for ((i, q) in listOf(Offset(s.x + 14f, s.y - 2f), Offset(s.x + 20f, s.y - 1f), Offset(s.x + 17f, s.y - 4f)).withIndex()) { drawOval(p.lado(cobre, alReves = i == 1), Offset(q.x - 3f, q.y - 1.4f), Size(6f, 2.8f)); drawRect(p.sombra(C.SUMI), Offset(q.x - 0.6f, q.y - 0.5f), Size(1.2f, 1f)) }
    }

    /** La pipa larga de bambú y metal, junto a la caja del tabaco con su braserito y su tubo para la ceniza. */
    private fun DrawScope.pipa(m: Momento, p: Pincel, s: Offset) {
        val caja = Offset(s.x + 10f, s.y)
        drawRect(Brush.horizontalGradient(listOf(p.lado(C.MADERA, alReves = true), p.lado(C.MADERA)), caja.x - 9f, caja.x + 9f), Offset(caja.x - 9f, caja.y - 10f), Size(18f, 10f))
        drawRect(p.arriba(C.MADERA_OSC), Offset(caja.x - 9f, caja.y - 11f), Size(18f, 1.4f))
        drawPath(Path().apply { moveTo(caja.x - 7f, caja.y - 10f); cubicTo(caja.x - 7f, caja.y - 16f, caja.x + 1f, caja.y - 16f, caja.x + 1f, caja.y - 10f); close() }, p.lado(Color(0xFF8A7A6A)))
        drawCircle(Color(0xFFE2662A).copy(alpha = 0.8f), 1.4f, Offset(caja.x - 3f, caja.y - 12f))
        drawRect(p.lado(Color(0xFF9A9452)), Offset(caja.x + 3f, caja.y - 17f), Size(4f, 7f))
        drawLine(p.medio(Color(0xFF6E5A3E)), Offset(s.x - 30f, s.y - 2f), Offset(s.x + 2f, s.y - 5f), 1.1f, StrokeCap.Round)
        drawRect(p.luz(Color(0xFFB8A070)), Offset(s.x - 32f, s.y - 3.4f), Size(4f, 2f)); drawRect(p.luz(Color(0xFFB8A070)), Offset(s.x + 1f, s.y - 6.4f), Size(3f, 2f))
    }

    /** El peine de boj, amarillo, con su frasquito de aceite de camelia. */
    private fun DrawScope.peine(p: Pincel, s: Offset) {
        val boj = Color(0xFFD8B060)
        val forma = Path().apply { moveTo(s.x - 10f, s.y - 2f); cubicTo(s.x - 10f, s.y - 10f, s.x + 10f, s.y - 10f, s.x + 10f, s.y - 2f); close() }
        drawPath(forma, p.lado(boj))
        clipPath(forma) { var x = s.x - 10f; while (x < s.x + 10f) { drawLine(p.sombra(boj), Offset(x, s.y - 6f), Offset(x, s.y - 2f), 0.4f); x += 1f } }
        drawLine(p.luz(boj), Offset(s.x - 7f, s.y - 7.6f), Offset(s.x + 7f, s.y - 7.6f), 0.5f)
        val b = Offset(s.x + 18f, s.y)
        drawPath(Path().apply { moveTo(b.x - 3f, b.y); cubicTo(b.x - 4f, b.y - 6f, b.x - 2f, b.y - 7f, b.x - 1f, b.y - 9f); lineTo(b.x + 1f, b.y - 9f); cubicTo(b.x + 2f, b.y - 7f, b.x + 4f, b.y - 6f, b.x + 3f, b.y); close() }, p.lado(Color(0xFF8AA0A0)))
        drawRect(p.medio(C.BERMELLON), Offset(b.x - 1.2f, b.y - 11f), Size(2.4f, 2f))
    }

    /** Un pañuelo de envolver de índigo con su dibujo de ondas, atado por las esquinas, con lo de un viajero dentro. */
    private fun DrawScope.furoshiki(p: Pincel, s: Offset) {
        val forma = Path().apply { moveTo(s.x - 14f, s.y); cubicTo(s.x - 16f, s.y - 10f, s.x - 8f, s.y - 14f, s.x, s.y - 14f); cubicTo(s.x + 8f, s.y - 14f, s.x + 16f, s.y - 10f, s.x + 14f, s.y); close() }
        volumen(p.l, p.r, forma, s.x - 15f, s.x + 15f, C.INDIGO)
        clipPath(forma) { for (y in listOf(-4f, -8f, -12f)) for (k in -2..2) drawArc(p.luz(C.INDIGO).copy(alpha = 0.6f), 180f, 180f, false, Offset(s.x + k * 6f - 3f, s.y + y), Size(6f, 3f), style = Stroke(0.5f)) }
        // El nudo, con sus dos orejas.
        drawPath(Path().apply { moveTo(s.x - 2f, s.y - 14f); cubicTo(s.x - 8f, s.y - 20f, s.x - 6f, s.y - 22f, s.x - 1f, s.y - 16f); close() }, p.lado(C.INDIGO, alReves = true))
        drawPath(Path().apply { moveTo(s.x + 2f, s.y - 14f); cubicTo(s.x + 8f, s.y - 20f, s.x + 6f, s.y - 22f, s.x + 1f, s.y - 16f); close() }, p.lado(C.INDIGO))
        drawCircle(p.medio(C.INDIGO), 2f, Offset(s.x, s.y - 14.6f))
    }

}
