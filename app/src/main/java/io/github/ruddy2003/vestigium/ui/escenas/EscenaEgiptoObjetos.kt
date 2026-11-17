package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen

/**
 * Los doce objetos del calendario de Pay, sobre el banco de la azotea. Cada uno se dibuja de pie en [c] (el punto del
 * banco donde se apoya), a escala [k]. Ninguno lleva signos que se lean: los renglones son rayas.
 *
 * **[Aproximación]**: son los objetos que nombra el cuento (la lasca escrita, la paleta de dos tintas, las mechas de
 * lino, la jarra de cerveza con su tapón de barro…); su forma exacta no se comprobó contra ninguna pieza y no copia
 * ninguna.
 */
internal object ObjetosEgipto {
    private val C = ColorEgipto

    val TODOS = listOf("ostracon", "papiro", "paleta", "pincel", "cincel", "mecha", "cerveza", "pan", "cantaro", "escarabajo", "sandalias", "lampara")

    fun DrawScope.objeto(id: String?, c: Offset, k: Float, s: SolEgipto, noche: Float) {
        fun p(x: Float, y: Float) = c + Offset(x * k, y * k)
        // La sombra de contacto, igual para todos.
        if (id != null) drawOval(s.sombra(C.BARRO).copy(alpha = 0.4f), p(-10f, -1f), Size(20f * k, 3f * k))
        when (id) {
            "ostracon" -> { // Una lasca de caliza, apoyada en otra, con sus renglones de tinta negra y uno rojo.
                val detras = poligono(listOf(p(2f, 0f), p(4f, -9f), p(9f, -12f), p(13f, -7f), p(12f, 0f))); drawPath(detras, s.sombra(C.CALIZA)); drawPath(detras, s.sombra(C.ROCA), style = Stroke(0.4f))
                val lasca = poligono(listOf(p(-11f, 0f), p(-12f, -8f), p(-8f, -15f), p(-1f, -17f), p(6f, -13f), p(8f, -5f), p(6f, 0f))); drawPath(lasca, s.frente(C.CALIZA)); drawPath(lasca, s.sombra(C.ROCA).copy(alpha = 0.7f), style = Stroke(0.5f))
                clipPath(lasca) { drawPath(poligono(p(-12f, -8f), p(-8f, -15f), p(-1f, -17f), p(-6f, -10f)), s.luz(C.LINO).copy(alpha = 0.6f))
                    for (j in 0..4) { var x = -9.4f + j * 0.3f; while (x < 5f - j * 0.4f) { val w = 1f + ((x * 7f + j * 3f) % 2.2f + 2.2f) % 2.2f; drawLine(s.sombra(if (j == 2) C.TERRACOTA else C.HOLLIN), p(x, -12.4f + j * 2.4f), p(x + w, -12.5f + j * 2.4f), 0.6f * k); x += w + 0.9f } } }
            }
            "papiro" -> { // Un rollo a medio abrir: la hoja, con sus fibras y sus renglones, y el rollo atado con su cinta.
                val hoja = poligono(p(-11f, 0f), p(-9f, -7f), p(5f, -8f), p(4f, 0f)); drawPath(hoja, s.arriba(lerp(C.TRIGO, C.CALIZA, 0.5f))); drawPath(hoja, s.sombra(C.MADERA).copy(alpha = 0.6f), style = Stroke(0.4f))
                clipPath(hoja) { for (j in 0..6) drawLine(s.sombra(C.ESTERA).copy(alpha = 0.5f), p(-11f + j * 2.4f, 0f), p(-9.4f + j * 2.4f, -8f), 0.3f); for (j in 0..3) drawLine(s.sombra(C.HOLLIN).copy(alpha = 0.8f), p(-8.6f + j * 0.3f, -6f + j * 1.5f), p(2f - (j % 2) * 3f, -6.6f + j * 1.5f), 0.5f * k); drawLine(s.medio(C.TERRACOTA), p(-8.4f, -6.6f), p(-5f, -6.8f), 0.5f * k) }
                val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p(3f, -10f), p(9f, 0.6f)), androidx.compose.ui.geometry.CornerRadius(2.6f * k))) }
                volumen(s.l, s.r, rollo, c.x + 3f * k, c.x + 9f * k, lerp(C.TRIGO, C.CALIZA, 0.4f)); drawOval(s.sombra(C.MADERA), p(4f, -11f), Size(4f * k, 2f * k)); drawLine(s.medio(C.TERRACOTA), p(3f, -4.6f), p(9f, -4.6f), 0.9f * k)
            }
            "paleta" -> { // La paleta de escriba, apoyada en su estuche: los dos pocillos de tinta y los cálamos en la ranura.
                val tabla = poligono(p(-12f, -1f), p(-10f, -4.6f), p(11f, -9.6f), p(12f, -6f)); drawPath(tabla, s.arriba(lerp(C.MADERA, C.OCRE, 0.2f))); drawPath(poligono(p(-12f, -1f), p(12f, -6f), p(12f, -4.6f), p(-12f, 0.4f)), s.frente(C.MADERA_OSC))
                drawCircle(s.sombra(C.HOLLIN), 1.7f * k, p(-7.4f, -3.4f)); drawCircle(s.medio(C.TERRACOTA), 1.7f * k, p(-3f, -4.4f)); drawCircle(s.luz(C.LINO).copy(alpha = 0.5f), 0.5f * k, p(-7.8f, -3.8f))
                drawPath(poligono(p(0.6f, -5.6f), p(10f, -7.8f), p(10.2f, -6.8f), p(0.8f, -4.6f)), s.sombra(C.MADERA_OSC)); for (j in 0..2) drawLine(s.luz(C.ESTERA), p(2f + j * 0.6f, -5.6f + j * 0.3f), p(13.6f + j, -9.6f + j * 0.8f), 0.6f * k, StrokeCap.Round)
                drawLine(s.sombra(C.HOLLIN), p(13.6f, -9.6f), p(14.8f, -10f), 0.7f * k)
            }
            "pincel" -> { // Los cálamos de junco en su vaso, con las puntas mascadas, y la piedra de moler la tinta.
                val vaso = poligono(p(-7f, 0f), p(-8f, -9f), p(-1f, -9f), p(-2f, 0f)); volumen(s.l, s.r, vaso, c.x - 8f * k, c.x - 1f * k, C.LOZA); drawLine(s.sombra(C.NILO), p(-7.8f, -7f), p(-1.2f, -7f), 0.5f * k)
                for ((j, dx) in listOf(-3f, -1f, 1.4f, 3.4f).withIndex()) { drawLine(s.luz(C.ESTERA), p(-4.6f + j * 0.5f, -9f), p(-4.6f + dx * 1.3f, -19f + j % 2 * 2f), 0.8f * k, StrokeCap.Round); drawLine(s.sombra(if (j == 1) C.TERRACOTA else C.HOLLIN), p(-4.6f + dx * 1.3f, -19f + j % 2 * 2f), p(-4.6f + dx * 1.36f, -20.4f + j % 2 * 2f), 0.9f * k) }
                drawOval(s.arriba(lerp(C.ROCA_OSC, C.HOLLIN, 0.4f)), p(2f, -3f), Size(11f * k, 4f * k)); drawOval(s.sombra(C.HOLLIN), p(4.6f, -2.4f), Size(5f * k, 2f * k)); drawOval(s.medio(C.ROCA_OSC), p(9f, -5f), Size(3.4f * k, 3f * k))
            }
            "cincel" -> { // El cincel de cobre, gastado, y el mazo de madera, de una pieza.
                drawLine(s.sombra(C.MADERA_OSC), p(-12f, -0.6f), p(3f, -2f), 3f * k, StrokeCap.Round); drawLine(s.luz(lerp(C.TERRACOTA, C.OCRE, 0.5f)), p(-12f, -1.4f), p(2f, -2.8f), 2f * k, StrokeCap.Round); drawPath(poligono(p(2f, -4.4f), p(7f, -3f), p(2.4f, -0.6f)), s.luz(lerp(C.TERRACOTA, C.OCRE, 0.6f))); drawLine(Color.White.copy(alpha = 0.5f), p(-9f, -2.2f), p(-2f, -2.9f), 0.4f)
                val mazo = Path().apply { moveTo(c.x + 3f * k, c.y - 6f * k); cubicTo(c.x + 3f * k, c.y - 16f * k, c.x + 13f * k, c.y - 16f * k, c.x + 13f * k, c.y - 6f * k); cubicTo(c.x + 12f * k, c.y - 4f * k, c.x + 10f * k, c.y - 3.6f * k, c.x + 9.4f * k, c.y); lineTo(c.x + 6.6f * k, c.y); cubicTo(c.x + 6f * k, c.y - 3.6f * k, c.x + 4f * k, c.y - 4f * k, c.x + 3f * k, c.y - 6f * k); close() }
                volumen(s.l, s.r, mazo, c.x + 3f * k, c.x + 13f * k, C.MADERA); clipPath(mazo) { for (j in 0..3) drawLine(s.sombra(C.MADERA_OSC).copy(alpha = 0.5f), p(4f + j * 2.6f, -15f), p(4.6f + j * 2.4f, -5f), 0.4f) }
            }
            "mecha" -> { // Las mechas de lino retorcido, contadas en su manojo, y el cuenco de la grasa con sal.
                for (j in 0..4) { val x = -11f + j * 2.6f; val mecha = Path().apply { moveTo(c.x + x * k, c.y - 0.4f * k); cubicTo(c.x + (x + 1.6f) * k, c.y - 5f * k, c.x + (x - 1f) * k, c.y - 9f * k, c.x + (x + 1f) * k, c.y - 14f * k) }
                    drawPath(mecha, s.sombra(C.ESTERA), style = Stroke(2f * k, cap = StrokeCap.Round)); drawPath(mecha, s.luz(C.LINO), style = Stroke(1.3f * k, cap = StrokeCap.Round)); for (i in 1..5) drawLine(s.sombra(C.MADERA).copy(alpha = 0.5f), p(x - 0.4f, -i * 2.4f), p(x + 1.2f, -i * 2.4f - 1f), 0.3f) }
                drawLine(s.medio(C.TERRACOTA), p(-12f, -7f), p(1.4f, -7f), 1f * k)
                val cuenco = Path().apply { moveTo(c.x + 3f * k, c.y - 5f * k); cubicTo(c.x + 4f * k, c.y + 0.6f * k, c.x + 12f * k, c.y + 0.6f * k, c.x + 13f * k, c.y - 5f * k); close() }; volumen(s.l, s.r, cuenco, c.x + 3f * k, c.x + 13f * k, C.TERRACOTA); drawOval(s.luz(C.LINO), p(3.6f, -6.2f), Size(8.8f * k, 2.4f * k)); drawOval(s.medio(lerp(C.LINO, C.OCRE, 0.3f)), p(5f, -5.8f), Size(6f * k, 1.6f * k))
            }
            "cerveza" -> { // La jarra de cerveza, con su tapón de barro, y el cuenco de beber.
                val jarra = Path().apply { moveTo(c.x - 7f * k, c.y - 17f * k); cubicTo(c.x - 12f * k, c.y - 11f * k, c.x - 9f * k, c.y - 1f * k, c.x - 5f * k, c.y); lineTo(c.x - 1f * k, c.y); cubicTo(c.x + 3f * k, c.y - 1f * k, c.x + 6f * k, c.y - 11f * k, c.x + 1f * k, c.y - 17f * k); close() }
                volumen(s.l, s.r, jarra, c.x - 11f * k, c.x + 5f * k, lerp(C.TERRACOTA, C.ARENA, 0.3f)); drawLine(s.sombra(C.BARRO).copy(alpha = 0.5f), p(-10f, -9f), p(4f, -9f), 0.5f); drawLine(s.sombra(C.BARRO).copy(alpha = 0.5f), p(-9.6f, -11f), p(3.6f, -11f), 0.5f)
                drawPath(poligono(p(-7f, -17f), p(-5.4f, -22f), p(-0.6f, -22f), p(1f, -17f)), s.medio(lerp(C.BARRO, C.ADOBE, 0.5f))); drawOval(s.luz(C.ADOBE), p(-5.4f, -23f), Size(4.8f * k, 2f * k))
                val cuenco = Path().apply { moveTo(c.x + 5f * k, c.y - 5f * k); cubicTo(c.x + 6f * k, c.y + 0.6f * k, c.x + 13f * k, c.y + 0.6f * k, c.x + 14f * k, c.y - 5f * k); close() }; volumen(s.l, s.r, cuenco, c.x + 5f * k, c.x + 14f * k, C.TERRACOTA); drawOval(s.medio(lerp(C.OCRE, C.BARRO, 0.4f)), p(5.6f, -6.2f), Size(7.8f * k, 2.2f * k)); drawOval(s.luz(C.LINO).copy(alpha = 0.7f), p(7f, -6f), Size(3f * k, 1f * k))
            }
            "pan" -> { // Tres panes sobre su estera: uno redondo, otro en cono y otro partido.
                drawPath(poligono(p(-13f, 0f), p(-11f, -3f), p(12f, -3f), p(13.6f, 0f)), s.arriba(C.ESTERA)); for (j in 0..8) drawLine(s.sombra(C.MADERA).copy(alpha = 0.4f), p(-11.6f + j * 3f, -0.2f), p(-10f + j * 2.7f, -3f), 0.3f)
                val redondo = Path().apply { addOval(Rect(p(-11f, -8f), p(-1f, -1f))) }; volumen(s.l, s.r, redondo, c.x - 11f * k, c.x - 1f * k, lerp(C.TRIGO, C.ADOBE, 0.4f)); for (j in 0..2) drawLine(s.sombra(C.BARRO).copy(alpha = 0.6f), p(-8.6f + j * 2.4f, -6.6f), p(-7.6f + j * 2.4f, -3.4f), 0.5f)
                val cono = poligono(p(0f, -1f), p(3.6f, -13f), p(7.4f, -1f)); volumen(s.l, s.r, cono, c.x, c.x + 7.4f * k, lerp(C.TRIGO, C.ROCA, 0.3f)); drawLine(s.sombra(C.BARRO).copy(alpha = 0.5f), p(1.6f, -5f), p(6f, -5f), 0.4f)
                val medio = Path().apply { moveTo(c.x + 7f * k, c.y - 1f * k); cubicTo(c.x + 7f * k, c.y - 7f * k, c.x + 13f * k, c.y - 7f * k, c.x + 13.4f * k, c.y - 1f * k); close() }; volumen(s.l, s.r, medio, c.x + 7f * k, c.x + 13.4f * k, lerp(C.TRIGO, C.ADOBE, 0.5f)); drawLine(s.luz(C.LINO), p(7.6f, -1.6f), p(13f, -1.6f), 1f * k)
            }
            "cantaro" -> { // El cántaro del agua: panzudo, de cuello estrecho, con dos asas y una cenefa azul pintada.
                val cuerpo = Path().apply { moveTo(c.x - 3f * k, c.y - 20f * k); lineTo(c.x - 3.4f * k, c.y - 16f * k); cubicTo(c.x - 13f * k, c.y - 12f * k, c.x - 9f * k, c.y - 1f * k, c.x - 3f * k, c.y); lineTo(c.x + 3f * k, c.y); cubicTo(c.x + 9f * k, c.y - 1f * k, c.x + 13f * k, c.y - 12f * k, c.x + 3.4f * k, c.y - 16f * k); lineTo(c.x + 3f * k, c.y - 20f * k); close() }
                for (lado in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(c.x + lado * 3.4f * k, c.y - 18.6f * k); cubicTo(c.x + lado * 9f * k, c.y - 19f * k, c.x + lado * 10f * k, c.y - 14f * k, c.x + lado * 7.6f * k, c.y - 12f * k) }, s.medio(C.TERRACOTA), style = Stroke(1.4f * k, cap = StrokeCap.Round))
                volumen(s.l, s.r, cuerpo, c.x - 10f * k, c.x + 10f * k, lerp(C.TERRACOTA, C.ARENA, 0.4f))
                clipPath(cuerpo) { drawRect(s.medio(C.LOZA).copy(alpha = 0.85f), p(-12f, -12.6f), Size(24f * k, 3f * k)); for (j in 0..9) drawPath(poligono(p(-10f + j * 2.2f, -9.6f), p(-9f + j * 2.2f, -7.4f), p(-8f + j * 2.2f, -9.6f)), s.medio(C.LOZA).copy(alpha = 0.85f)); drawLine(s.sombra(C.HOLLIN).copy(alpha = 0.6f), p(-12f, -13f), p(12f, -13f), 0.4f) }
                drawOval(s.medio(C.TERRACOTA), p(-4f, -21.4f), Size(8f * k, 2.6f * k)); drawOval(s.sombra(C.HOLLIN), p(-2.8f, -21f), Size(5.6f * k, 1.6f * k))
            }
            "escarabajo" -> { // Un escarabajo de loza azul, en su cordón, sobre un trozo de lino.
                drawPath(poligono(p(-12f, 0f), p(-9f, -4f), p(11f, -4.6f), p(13f, 0f)), s.arriba(C.LINO)); drawLine(s.sombra(C.LINO).copy(alpha = 0.6f), p(-6f, -3.8f), p(-8f, 0f), 0.4f); drawLine(s.sombra(C.LINO).copy(alpha = 0.6f), p(5f, -4.4f), p(7f, 0f), 0.4f)
                val cordon = Path().apply { moveTo(c.x - 10f * k, c.y - 2f * k); cubicTo(c.x - 8f * k, c.y - 12f * k, c.x + 6f * k, c.y - 13f * k, c.x + 9f * k, c.y - 3f * k) }; drawPath(cordon, s.medio(lerp(C.TERRACOTA, C.BARRO, 0.3f)), style = Stroke(0.7f * k))
                val lomo = Path().apply { addOval(Rect(p(-6f, -8f), p(6f, -1f))) }; volumen(s.l, s.r, lomo, c.x - 6f * k, c.x + 6f * k, C.LOZA)
                clipPath(lomo) { drawLine(s.sombra(C.NILO), p(0f, -7f), p(0f, -1f), 0.5f * k); drawLine(s.sombra(C.NILO), p(-6f, -5.4f), p(6f, -5.4f), 0.5f * k); drawLine(s.sombra(C.NILO), p(-2.6f, -8f), p(-3.4f, -5.4f), 0.4f); drawLine(s.sombra(C.NILO), p(2.6f, -8f), p(3.4f, -5.4f), 0.4f) }
                drawOval(Color.White.copy(alpha = 0.5f), p(-3.6f, -4.6f), Size(2.4f * k, 1.2f * k)); for (lado in listOf(-1f, 1f)) for (j in 0..2) drawLine(s.sombra(C.NILO), p(lado * 5f, -3.6f + j * 1f), p(lado * 7.4f, -3f + j * 1.2f), 0.5f * k)
            }
            "sandalias" -> { // Las sandalias de fibra de palma, una apoyada en la otra, con sus tiras.
                for ((j, dx) in listOf(-9f, 1f).withIndex()) { val suela = Path().apply { moveTo(c.x + dx * k, c.y - 1f * k); cubicTo(c.x + (dx - 1f) * k, c.y - (5f + j * 3f) * k, c.x + (dx + 7f) * k, c.y - (7f + j * 5f) * k, c.x + (dx + 11f) * k, c.y - (4f + j * 4f) * k); cubicTo(c.x + (dx + 12f) * k, c.y - (2f + j * 2f) * k, c.x + (dx + 8f) * k, c.y, c.x + dx * k, c.y - 1f * k); close() }
                    drawPath(suela, s.arriba(C.ESTERA)); clipPath(suela) { for (i in 0..7) drawLine(s.sombra(C.MADERA).copy(alpha = 0.5f), p(dx + i * 1.6f, 0f), p(dx + 1f + i * 1.6f, -12f), 0.3f) }; drawPath(suela, s.sombra(C.MADERA), style = Stroke(0.5f))
                    val nudo = p(dx + 8.4f, -4f - j * 3.4f); drawLine(s.medio(lerp(C.MADERA, C.TERRACOTA, 0.3f)), nudo, p(dx + 3f, -1.4f - j * 1.4f), 0.7f * k); drawLine(s.medio(lerp(C.MADERA, C.TERRACOTA, 0.3f)), nudo, p(dx + 4f, -5.6f - j * 4f), 0.7f * k); drawCircle(s.luz(C.ESTERA), 0.7f * k, nudo) }
            }
            "lampara" -> { // La lámpara de aceite de la tumba: un cuenco con pie, la grasa y la mecha; de noche, encendida.
                val pie = poligono(p(-4f, 0f), p(-2f, -5f), p(2f, -5f), p(4f, 0f)); volumen(s.l, s.r, pie, c.x - 4f * k, c.x + 4f * k, C.TERRACOTA)
                val cuenco = Path().apply { moveTo(c.x - 9f * k, c.y - 11f * k); cubicTo(c.x - 8f * k, c.y - 3f * k, c.x + 8f * k, c.y - 3f * k, c.x + 10f * k, c.y - 11.6f * k); close() }; volumen(s.l, s.r, cuenco, c.x - 9f * k, c.x + 10f * k, lerp(C.TERRACOTA, C.ARENA, 0.2f))
                drawOval(s.sombra(C.HOLLIN), p(-8.4f, -12.6f), Size(17.6f * k, 3f * k)); drawOval(s.medio(lerp(C.OCRE, C.LINO, 0.4f)), p(-6.6f, -12f), Size(13f * k, 1.9f * k)); drawLine(s.luz(C.LINO), p(6f, -11.6f), p(10.6f, -13.4f), 1.2f * k, StrokeCap.Round); drawLine(s.sombra(C.HOLLIN), p(10.6f, -13.4f), p(11.4f, -13.8f), 1f * k)
                if (noche > 0.3f) { resplandor(p(11f, -16f), 12f * k, 0.6f * noche, C.LUMBRE); drawPath(Path().apply { moveTo(c.x + 10.4f * k, c.y - 13.6f * k); cubicTo(c.x + 9f * k, c.y - 17f * k, c.x + 12f * k, c.y - 18f * k, c.x + 11.4f * k, c.y - 21f * k); cubicTo(c.x + 13.6f * k, c.y - 18f * k, c.x + 13f * k, c.y - 15f * k, c.x + 11.6f * k, c.y - 13.6f * k); close() }, lerp(C.LUMBRE, Color.White, 0.4f)) }
            }
            null -> Unit
            else -> { // Un objeto que el calendario todavía no tenía: una vasija cualquiera.
                val vasija = Path().apply { addOval(Rect(p(-6f, -12f), p(6f, 0f))) }; volumen(s.l, s.r, vasija, c.x - 6f * k, c.x + 6f * k, C.TERRACOTA)
            }
        }
    }
}
