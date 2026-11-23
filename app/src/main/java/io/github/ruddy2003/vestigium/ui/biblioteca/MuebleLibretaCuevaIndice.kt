package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El índice de la libreta de Prehistoria: cada día es una de las pieles de la bolsa, un trozo de piel raspada colgado de su tiento
 * por el agujero de arriba, con el día pintado con ocre y la letra de la semana con carbón. El ánimo del día es un toque del
 * pigmento de la cueva (el del estante) dado con el dedo en la esquina de abajo; sin ánimo, la piel queda sin acabar de raspar:
 * la orilla de abajo conserva el pelo (se distingue por la forma, no solo por el color). Todo lo que no depende del día se arma
 * una vez por tamaño y se comparte.
 */
internal object IndiceLibretaCueva {
    /** La piel raspada (la misma del parche de la bolsa), su hondo, el tiento y la penumbra del modo oscuro. */
    private val RASPADA = Color(0xFFE6D3AE)
    private val RASPADA_HONDA = Color(0xFFB59A72)
    private val TIENTO = Color(0xFF9A7650)
    private val PENUMBRA = Color(0xFF1E1016)

    /** Los caminos de una pestaña de [w] × [h], que comparten todas: tres contornos de piel, su sombra propia, los poros y el pelo. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val r = Rect(w * 0.1f, h * 0.12f, w * 0.9f, h * 0.93f)
        val formas = List(3) { v -> forma(r, 8300 + v) }
        /** La sombra propia del lado de abajo a la derecha (la luz de la pantalla viene de arriba a la izquierda). */
        val sombras = formas.map { f -> Path().apply { op(f, corridoCueva(f, -w * 0.07f, -h * 0.05f), androidx.compose.ui.graphics.PathOperation.Difference) } }
        val poros = Path().apply {
            for (j in 0 until 9) {
                val c = Offset(r.left + r.width * (0.1f + 0.8f * azar(8310, j)), r.top + r.height * (0.12f + 0.8f * azar(8311, j)))
                val rr = (0.5f + 0.5f * azar(8312, j)) * u
                addOval(Rect(c.x - rr, c.y - rr * 0.7f, c.x + rr, c.y + rr * 0.7f))
            }
        }
        /** El pelo que queda en la orilla de abajo de una piel sin ánimo: la franja y los mechones que salen de ella. */
        val franjaPelo = Path().apply {
            moveTo(r.left - w * 0.02f, r.bottom - h * 0.13f)
            var x = r.left; var j = 0
            while (x < r.right) { lineTo(x + w * 0.05f, r.bottom - h * (0.14f + 0.02f * azar(8320, j))); x += w * 0.1f; j++ }
            lineTo(r.right + w * 0.02f, r.bottom - h * 0.12f); lineTo(r.right + w * 0.02f, r.bottom + h * 0.02f); lineTo(r.left - w * 0.02f, r.bottom + h * 0.02f); close()
        }
        val mechones = Path().apply {
            var x = r.left + w * 0.02f; var j = 0
            while (x < r.right) {
                val lg = h * (0.05f + 0.035f * azar(8321, j))
                moveTo(x, r.bottom - h * 0.06f); lineTo(x + w * 0.03f * (azar(8322, j) - 0.5f), r.bottom + lg * 0.6f)
                x += w * 0.075f; j++
            }
        }
        val agujero = Offset(w * 0.5f, r.top + h * 0.075f)
        val dia = Rect(r.left, r.top + h * 0.13f, r.right, r.top + h * 0.5f)
        val semana = Rect(r.left, r.top + h * 0.48f, r.right, r.top + h * 0.64f)

        private fun forma(r: Rect, s: Int): Path = cerradaCueva(listOf(
            Offset(r.left + r.width * (0.06f + 0.06f * azar(s, 1)), r.top + r.height * 0.06f), Offset(r.center.x, r.top - r.height * 0.02f * azar(s, 2)),
            Offset(r.right - r.width * (0.04f + 0.06f * azar(s, 3)), r.top + r.height * 0.05f), Offset(r.right + r.width * 0.02f * azar(s, 4), r.center.y),
            Offset(r.right - r.width * 0.05f * azar(s, 5), r.bottom - r.height * 0.03f), Offset(r.center.x + r.width * 0.1f * (azar(s, 6) - 0.5f), r.bottom + r.height * 0.015f),
            Offset(r.left + r.width * 0.05f * azar(s, 7), r.bottom - r.height * 0.04f), Offset(r.left - r.width * 0.02f * azar(s, 8), r.center.y + r.height * 0.05f)))
    }

    private var cache: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cache?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cache = it }

    /** Los números de los días ya medidos, uno por modo (la tinta va en la medida); se vacían si cambia la densidad o el tamaño. */
    private val numeros = List(2) { HashMap<String, TextLayoutResult>() }
    private val clave = FloatArray(4)

    private fun DrawScope.numeroDia(medidor: TextMeasurer, dia: String, caja: Rect, tinta: Color, oscuro: Boolean) {
        val k = clave
        if (k[0] != density || k[1] != fontScale || k[2] != caja.width || k[3] != caja.height) {
            numeros.forEach { it.clear() }; k[0] = density; k[1] = fontScale; k[2] = caja.width; k[3] = caja.height
        }
        val mapa = numeros[if (oscuro) 1 else 0]
        val capa = mapa[dia] ?: (capaEn(medidor, dia, caja, LetraExtra.trazo, tinta, 20f, peso = FontWeight.Bold, minimo = 10f) ?: return).also { mapa[dia] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /**
     * La pestaña de un día: la piel raspada colgada de su tiento, un poco ladeada, con su sombra en la página, su sombra propia y
     * sus poros; el día con ocre y la letra de la semana con carbón; el toque del pigmento del ánimo o, sin ánimo, el pelo.
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val s = e.semilla and 0x7fffffff
        val v = s % 3
        val piel = if (oscuro) mezcla(RASPADA, PENUMBRA, 0.62f) else mezcla(RASPADA, RASPADA_HONDA, 0.25f * azar(s, 2))
        val honda = if (oscuro) mezcla(RASPADA_HONDA, PENUMBRA, 0.5f) else RASPADA_HONDA
        rotate((azar(s, 1) - 0.5f) * 6f, g.agujero) {
            val f = g.formas[v]
            drawPath(corridoCueva(f, 1.6f * u, 2.2f * u), Color.Black.copy(alpha = if (oscuro) 0.45f else 0.24f))
            drawPath(f, piel)
            drawPath(g.sombras[v], honda.copy(alpha = 0.55f))
            drawPath(g.poros, honda.copy(alpha = 0.5f))
            val tono = e.tono
            if (tono == null) {
                // Sin ánimo: la orilla de abajo sin raspar, con el pelo.
                drawPath(g.franjaPelo, if (oscuro) mezcla(P.PELO, PENUMBRA, 0.35f) else P.PELO)
                drawPath(g.mechones, if (oscuro) mezcla(P.PELO, PENUMBRA, 0.2f) else mezcla(P.PELO, P.HUESO, 0.25f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            } else {
                // El toque del pigmento del ánimo, dado con la yema: el pegote y su orilla más cargada.
                val pig = pigmentoDelAnimoCueva(tonoDeDiaCueva(tono, oscuro) ?: tono)
                val c = Offset(g.r.right - g.r.width * 0.3f, g.r.bottom - g.r.height * 0.15f)
                drawOval(pig, Offset(c.x - w * 0.17f, c.y - h * 0.075f), Size(w * 0.34f, h * 0.14f))
                drawOval(mezcla(pig, Color.White, 0.18f).copy(alpha = 0.6f), Offset(c.x - w * 0.1f, c.y - h * 0.05f), Size(w * 0.13f, h * 0.05f))
            }
            drawPath(f, if (oscuro) mezcla(TIENTO, PENUMBRA, 0.3f) else TIENTO, style = Stroke(0.9f * u))
            // El agujero y el tiento de colgar.
            drawLine(if (oscuro) mezcla(TIENTO, PENUMBRA, 0.2f) else TIENTO, Offset(g.agujero.x, -h * 0.05f), g.agujero, 1.5f * u)
            drawCircle(Color(0xFF2A1C14), 1.8f * u, g.agujero)
            val ocre = if (oscuro) mezcla(P.OCRE_AMAR, P.CAOLIN, 0.25f) else Color(0xFF8E2A16)
            numeroDia(medidor, e.dia, g.dia, ocre, oscuro)
            textoEn(medidor, e.semana, g.semana, P.LETRA, if (oscuro) P.CAOLIN.copy(alpha = 0.85f) else P.CARBON, 10f, minimo = 7f)
        }
    }
}
