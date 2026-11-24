package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo de atrás del bodegón de arriba del estante de Tierras Antiguas ([CoronaEstanteAntiguas]), lo que hace que no vaya en fila:
 * el tapiz de lana enrollado y recostado contra la pared, detrás de la drusa y de los tomos, y la bota de vino colgada de su clavo
 * por la correa, que cruza la pared encima de los tomos. Fuera de la mancha de sol de la ventana.
 */
internal object TrasfondoCoronaAntiguas {
    private val P = PaletaAntiguas
    /** Las bandas de la lana del tapiz, en el orden en que dan la vuelta al rollo. */
    private val BANDAS = listOf(P.RUBIA, P.CRUDO, P.ANIL, P.OCRE, P.RUBIA, P.ANIL)

    /** La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la vela, más marcada cerca. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, g: MarcoAntiguas, fuerza: Float) {
        val w = g.w
        val a = g.a
        if (!a.oscuro) {
            translate(w * (if (a.noche) 0.045f else 0.026f), w * 0.012f) { drawPath(p, a.sombra(fuerza)) }
        } else {
            val k = g.cerca(desde)
            if (k < 0.12f) return
            val dx = ((desde.x - g.vela.x) / w).coerceIn(-0.3f, 0.3f) / 0.3f
            translate(w * 0.04f * dx, w * 0.014f) { drawPath(p, a.sombra((0.5f * k).coerceAtMost(0.42f))) }
        }
    }

    /**
     * El tapiz de lana enrollado, recostado contra la pared con el pie en la cornisa: las bandas de rubia, crudo, añil y ocre que
     * dan la vuelta al rollo, con un rombo escalonado en las anchas, dos cordones que lo atan, la boca de arriba con la espiral de
     * la lana y los flecos.
     */
    fun DrawScope.tapiz(pie: Offset, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val k = g.cerca(pie - Offset(0f, w * 0.18f))
        val largo = w * 0.36f; val r = w * 0.034f
        val giro = 16f
        val cuerpo = Rect(pie.x - r, pie.y - largo, pie.x + r, pie.y)
        rotate(giro, pie) {
            sombraEnPared(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(cuerpo, CornerRadius(r))) }, pie - Offset(0f, w * 0.18f), g, 0.3f)
            // Las bandas, de abajo arriba, y su rombo escalonado.
            var y = cuerpo.bottom
            var i = 0
            while (y > cuerpo.top) {
                val alto = w * (if (i % 2 == 0) 0.05f else 0.022f)
                val y0 = maxOf(cuerpo.top, y - alto)
                val col = BANDAS[Math.floorMod(i, BANDAS.size)]
                drawRect(am.luz(col, k), Offset(cuerpo.left, y0), Size(cuerpo.width, y - y0 + 0.6f))
                if (i % 2 == 0 && y - y0 > w * 0.03f) {
                    // Un rombo partido en dos colores, crema y ocre (no una cruz).
                    val c = Offset(cuerpo.center.x, (y0 + y) / 2)
                    val rx = w * 0.013f; val ry = w * 0.017f
                    drawPath(poligonoAntiguas(c.x, c.y - ry, c.x, c.y + ry, c.x - rx, c.y), am.luz(P.CRUDO, k))
                    drawPath(poligonoAntiguas(c.x, c.y - ry, c.x + rx, c.y, c.x, c.y + ry), am.luz(P.OCRE, k))
                }
                y = y0; i++
            }
            // El cuerpo redondo: la luz y la sombra, y el pelo de la lana.
            clipPath(Path().apply { addRect(cuerpo) }) { cilindroAntiguas(cuerpo, am, k, if (am.oscuro && pie.x > g.vela.x) 1f else -1f, 1.2f) }
            for (j in 0 until 10) {
                val yy = cuerpo.top + cuerpo.height * azar(8610, j)
                drawLine(am.sombra(0.15f), Offset(cuerpo.left + r * 0.3f, yy), Offset(cuerpo.right - r * 0.4f, yy + 1f * u), 0.6f * u)
            }
            // Los dos cordones.
            listOf(0.2f, 0.78f).forEach { f ->
                val yy = cuerpo.top + cuerpo.height * f
                drawRect(am.sombra(0.4f), Offset(cuerpo.left, yy + 1.6f * u), Size(cuerpo.width, 1.2f * u))
                drawRect(am.luz(P.CUERDA, k), Offset(cuerpo.left - 0.6f * u, yy - 1f * u), Size(cuerpo.width + 1.2f * u, 2f * u))
            }
            // La boca de arriba: las vueltas de la lana en espiral, en sus colores, y los flecos que asoman.
            val boca = Rect(cuerpo.left, cuerpo.top - r * 0.45f, cuerpo.right, cuerpo.top + r * 0.45f)
            drawOval(am.luz(P.CRUDO, k), boca.topLeft, boca.size)
            for (j in 1..3) {
                val f = 1f - j * 0.24f
                drawOval(am.luz(BANDAS[j], k), Offset(boca.center.x - boca.width * f / 2, boca.center.y - boca.height * f / 2), Size(boca.width * f, boca.height * f), style = Stroke(1.4f * u))
            }
            drawOval(am.sombra(0.6f), Offset(boca.center.x - r * 0.18f, boca.center.y - r * 0.08f), Size(r * 0.36f, r * 0.16f))
            val flecos = Path()
            for (j in 0 until 7) {
                val fx = boca.left + boca.width * (0.1f + 0.13f * j)
                flecos.moveTo(fx, boca.center.y - boca.height * 0.3f)
                flecos.lineTo(fx - 1.2f * u + 2.4f * u * azar(8611, j), boca.top - w * (0.012f + 0.01f * azar(8612, j)))
            }
            drawPath(flecos, am.luz(P.CRUDO, k), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            if (am.oscuro && k > 0.1f) drawRect(P.LLAMA.copy(alpha = (0.5f * k).coerceAtMost(0.45f)), Offset(cuerpo.right - 1.4f * u, cuerpo.top), Size(1.4f * u, cuerpo.height))
        }
    }

    /**
     * La bota de vino colgada de un clavo por su correa: el cuero empegado, panzudo y algo vencido, con su costura pespunteada, la
     * boquilla de cuerno con su tapón y el brillo de la pez.
     */
    fun DrawScope.bota(clavo: Offset, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val c = clavo + Offset(-w * 0.004f, w * 0.15f)
        val k = g.cerca(c)
        val an = w * 0.085f; val al = w * 0.1f
        val cuerpo = Path().apply {
            moveTo(c.x - an * 0.1f, c.y - al * 0.45f)
            cubicTo(c.x + an * 0.35f, c.y - al * 0.5f, c.x + an * 0.62f, c.y - al * 0.05f, c.x + an * 0.5f, c.y + al * 0.28f)
            cubicTo(c.x + an * 0.38f, c.y + al * 0.6f, c.x - an * 0.42f, c.y + al * 0.62f, c.x - an * 0.52f, c.y + al * 0.25f)
            cubicTo(c.x - an * 0.62f, c.y - al * 0.08f, c.x - an * 0.45f, c.y - al * 0.42f, c.x - an * 0.1f, c.y - al * 0.45f)
            close()
        }
        val cuello = Offset(c.x - an * 0.06f, c.y - al * 0.5f)
        val silueta = Path().apply { addPath(cuerpo); addRect(Rect(cuello.x - w * 0.01f, clavo.y, cuello.x + w * 0.01f, cuello.y)) }
        sombraEnPared(silueta, c, g, 0.32f)
        // El clavo y la correa, que baja en dos ramales hasta el cuello.
        drawRect(am.luz(P.HIERRO, k), Offset(clavo.x - 2.4f * u, clavo.y - 2.4f * u), Size(4.8f * u, 4.8f * u))
        drawRect(am.brillo(0.5f, k), Offset(clavo.x - 2.4f * u, clavo.y - 2.4f * u), Size(4.8f * u, 1f * u))
        val correa = am.luz(P.CUERO_OSC, k)
        drawLine(correa, clavo, Offset(cuello.x - w * 0.026f, cuello.y + w * 0.012f), 2.2f * u, StrokeCap.Round)
        drawLine(correa, clavo, Offset(cuello.x + w * 0.03f, cuello.y + w * 0.016f), 2.2f * u, StrokeCap.Round)
        // El cuero: oscuro y empegado, con su luz a la izquierda y el brillo de la pez.
        val cuero = Color(0xFF4A2A18)
        drawPath(cuerpo, am.luz(cuero, k))
        clipPath(cuerpo) {
            drawOval(am.luz(mezcla(cuero, P.CUERO, 0.6f), k), Offset(c.x - an * 0.55f, c.y - al * 0.4f), Size(an * 0.6f, al * 0.85f))
            drawOval(am.sombra(0.35f), Offset(c.x + an * 0.05f, c.y - al * 0.1f), Size(an * 0.6f, al * 0.8f))
            drawOval(am.brillo(0.55f, k), Offset(c.x - an * 0.36f, c.y - al * 0.26f), Size(an * 0.14f, al * 0.3f))
        }
        // La costura que da la vuelta al canto.
        drawPath(cuerpo, am.luz(P.CUERDA, k).copy(alpha = 0.7f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.8f * u))))
        drawPath(cuerpo, am.sombra(0.4f), style = Stroke(0.8f * u))
        // La boquilla de cuerno con su tapón.
        val boq = Rect(cuello.x - w * 0.012f, cuello.y - w * 0.022f, cuello.x + w * 0.012f, cuello.y + w * 0.006f)
        drawRoundRect(am.luz(Color(0xFFD9C08C), k), boq.topLeft, boq.size, CornerRadius(w * 0.004f))
        drawRect(am.sombra(0.3f), Offset(boq.right - boq.width * 0.35f, boq.top), Size(boq.width * 0.35f, boq.height))
        drawRoundRect(am.luz(P.ROBLE, k), Offset(boq.left + boq.width * 0.15f, boq.top - w * 0.01f), Size(boq.width * 0.7f, w * 0.012f), CornerRadius(w * 0.003f))
        if (am.oscuro && k > 0.1f) drawPath(cuerpo, P.LLAMA.copy(alpha = (0.45f * k).coerceAtMost(0.4f)), style = Stroke(1.2f * u))
    }
}
