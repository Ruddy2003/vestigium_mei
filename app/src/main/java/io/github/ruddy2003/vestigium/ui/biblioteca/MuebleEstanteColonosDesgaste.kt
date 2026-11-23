package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE_BOTICA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/*
 * El desgaste de la alacena de Colonos, hecho para que se lea de lejos y no solo de cerca: los cantos pelados hasta el pino, los
 * saltones grandes donde asoma la mano de pintura de antes (verde botica) y, en medio, la madera; los nudos que se marcan bajo
 * la pintura; y las rozaduras finas.
 */

/**
 * La pintura gastada de una pieza: rozaduras finas a lo largo de la veta, por donde asoma lo de debajo ([col]); la mitad
 * pegadas a los cantos, que es donde más roza. Un solo camino para todas.
 */
internal fun DrawScope.gastadoColonos(r: Rect, col: Color, semilla: Int, cuantas: Int, vertical: Boolean, alfa: Float = 0.7f) {
    val u = density
    val p = Path()
    for (k in 0 until cuantas) {
        val largo = (6f + 26f * azar(semilla, 3 * k)) * u
        val grosor = (0.8f + 2.2f * azar(semilla, 3 * k + 1)) * u
        val t = azar(semilla, 3 * k + 2)
        val b = azar(semilla + 7, k)
        val orilla = if (k % 2 == 0) (if (b < 0.5f) b * 0.22f else 1f - (1f - b) * 0.22f) else b
        val rr = if (vertical) Rect(r.left + r.width * orilla - grosor / 2, r.top + r.height * t - largo / 2, r.left + r.width * orilla + grosor / 2, r.top + r.height * t + largo / 2)
        else Rect(r.left + r.width * t - largo / 2, r.top + r.height * orilla - grosor / 2, r.left + r.width * t + largo / 2, r.top + r.height * orilla + grosor / 2)
        p.addRoundRect(RoundRect(rr, CornerRadius(grosor / 2)))
    }
    clipRect(r.left, r.top, r.right, r.bottom) { drawPath(p, col.copy(alpha = alfa)) }
}

/**
 * Un canto pelado: a lo largo de [r] (una franja angosta pegada al canto), tiras largas y desparejas donde la pintura se fue
 * hasta el pino, con su borde de pintura levantada. [vertical]: el canto corre de arriba abajo. [cuanto], de 0 a 1, qué tanto
 * del canto está pelado.
 */
internal fun DrawScope.cantoPeladoColonos(g: MarcoColonos, r: Rect, semilla: Int, vertical: Boolean, cuanto: Float = 0.5f) {
    val u = density
    if (r.width < 1f || r.height < 1f) return
    val largoTotal = if (vertical) r.height else r.width
    val ancho = if (vertical) r.width else r.height
    val tiras = Path()
    var t = largoTotal * 0.05f * azar(semilla, 0)
    var i = 0
    while (t < largoTotal && i < 60) {
        val largo = largoTotal * (0.04f + 0.12f * azar(semilla, 2 * i + 1)) * (0.5f + cuanto)
        if (azar(semilla, 2 * i + 2) < 0.35f + 0.6f * cuanto) {
            val g0 = ancho * (0.45f + 0.55f * azar(semilla + 3, i))
            val rr = if (vertical) Rect(r.left, r.top + t, r.left + g0, r.top + t + largo) else Rect(r.left + t, r.top, r.left + t + largo, r.top + g0)
            tiras.addRoundRect(RoundRect(rr, CornerRadius(minOf(g0, largo) * 0.5f)))
        }
        t += largo * (1.2f + 1.4f * azar(semilla + 5, i)); i++
    }
    val c = r.center
    clipRect(r.left, r.top, r.right, r.bottom) {
        drawPath(tiras, g.c(PINO, c))
        drawPath(tiras, g.a.sombra(0.25f), style = Stroke(0.7f * u))
    }
}

/**
 * Un saltón grande de la pintura, en [c] y de radio [rx] × [ry]: la mano de antes (verde botica) que asoma debajo del maíz y,
 * en medio, donde también saltó, el pino; con el borde de la pintura de encima que hace grueso (luz abajo, sombra arriba).
 */
internal fun DrawScope.saltadoColonos(g: MarcoColonos, c: Offset, rx: Float, ry: Float, semilla: Int, recorte: Rect) {
    val u = density; val a = g.a
    fun mancha(fr: Float, s0: Int): Path = Path().apply {
        // Un borde quebrado y desparejo (la pintura salta en lascas, no en manchas redondas), sin picos regulares.
        val n = 17
        for (i in 0 until n) {
            val ang = i * 6.283f / n + 0.25f * azar(s0 + 1, i)
            val rr = fr * (0.55f + 0.45f * azar(s0, i)) * (if (azar(s0 + 2, i) > 0.8f) 0.6f else 1f)
            val x = c.x + rx * rr * kotlin.math.cos(ang); val y = c.y + ry * rr * kotlin.math.sin(ang)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    val fuera = mancha(1f, semilla)
    val dentro = Path().apply { addPath(mancha(0.5f, semilla + 11), Offset(rx * 0.12f, ry * 0.1f)) }
    clipRect(recorte.left, recorte.top, recorte.right, recorte.bottom) {
        drawPath(corridoColonos(fuera, 0.6f * u, 0.8f * u), a.claro(0.3f))
        drawPath(fuera, g.c(VERDE_BOTICA, c))
        clipPath(fuera) {
            drawPath(dentro, g.c(PINO, c))
            drawPath(corridoColonos(fuera, 1.3f * u, 1.5f * u), a.sombra(0.3f), style = Stroke(2.2f * u))
        }
        drawPath(fuera, g.c(mezcla(MAIZ, PINO_OSC, 0.4f), c).copy(alpha = 0.6f), style = Stroke(0.7f * u))
    }
}

/**
 * Un nudo de la madera que se marca bajo la pintura: una mancha alargada a lo largo de la veta, más oscura en un lado, y dos
 * vetas que la rodean.
 */
internal fun DrawScope.nudoColonos(g: MarcoColonos, c: Offset, r: Float, vertical: Boolean) {
    val u = density
    val col = g.c(mezcla(MAIZ, PINO_OSC, 0.55f), c)
    val rx = if (vertical) r * 0.55f else r * 1.6f; val ry = if (vertical) r * 1.6f else r * 0.55f
    drawOval(col.copy(alpha = 0.75f), Offset(c.x - rx, c.y - ry), Size(rx * 2f, ry * 2f))
    drawOval(g.c(PINO_OSC, c).copy(alpha = 0.35f), Offset(c.x - rx * 0.2f, c.y - ry * 0.7f), Size(rx * 1.1f, ry * 1.5f))
    val vetas = Path().apply {
        if (vertical) {
            moveTo(c.x - rx * 1.8f, c.y - ry * 2.6f); quadraticTo(c.x - rx * 2.4f, c.y, c.x - rx * 1.8f, c.y + ry * 2.6f)
            moveTo(c.x + rx * 1.8f, c.y - ry * 2.6f); quadraticTo(c.x + rx * 2.4f, c.y, c.x + rx * 1.8f, c.y + ry * 2.6f)
        } else {
            moveTo(c.x - rx * 2.6f, c.y - ry * 1.8f); quadraticTo(c.x, c.y - ry * 2.4f, c.x + rx * 2.6f, c.y - ry * 1.8f)
            moveTo(c.x - rx * 2.6f, c.y + ry * 1.8f); quadraticTo(c.x, c.y + ry * 2.4f, c.x + rx * 2.6f, c.y + ry * 1.8f)
        }
    }
    drawPath(vetas, col.copy(alpha = 0.45f), style = Stroke(0.6f * u))
}
