package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * El índice de la libreta de momentos de Tierras Antiguas: cada día es una lengüeta del libro de mapas, del mismo cordobán rojo
 * de la tapa, pespunteada, con su clavo de latón arriba, el día a tinta en una cartela de pergamino y la letra de la semana en
 * oro; abajo, el ánimo de ese día en una gema engastada en un chatón de latón en rombo. Sin ánimo no hay chatón: dos clavos
 * de latón. Lo que no depende del día se arma una vez por tamaño.
 */
internal object IndiceLibretaAntiguas {
    private val P = PaletaAntiguas
    /** Tres cordobanes, uno por semilla: el de la tapa, uno más vivo y uno más hondo. */
    private val CUEROS = listOf(Color(0xFF7E2A1E), Color(0xFF8E3424), Color(0xFF6C2418))
    /** La penumbra del índice en modo oscuro: honda, rojiza. */
    private val PENUMBRA = Color(0xFF160A08)

    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.4f): Color = if (oscuro) mezcla(c, PENUMBRA, cuanto) else c

    /** Los caminos de una pestaña de [w] × [h], que comparten todas. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val cuero = Path()
        val sombra = Path()
        val pespunte = Path()
        val chaton = Path()
        val cartela = Rect(w * 0.2f, h * 0.15f, w * 0.8f, h * 0.55f)
        val semana = Rect(w * 0.16f, h * 0.56f, w * 0.84f, h * 0.7f)
        val gema = Offset(w * 0.5f, h * 0.82f)
        val costura: PathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.8f * u))

        init {
            val r = Rect(w * 0.08f, h * 0.05f, w * 0.92f, h * 0.97f)
            val curva = w * 0.2f
            cuero.addRoundRect(RoundRect(r, CornerRadius.Zero, CornerRadius.Zero, CornerRadius(curva), CornerRadius(curva)))
            sombra.addPath(cuero, Offset(1.4f * u, 2f * u))
            val d = 2.4f * u
            pespunte.addRoundRect(RoundRect(Rect(r.left + d, r.top + d, r.right - d, r.bottom - d), CornerRadius.Zero, CornerRadius.Zero, CornerRadius(curva - d), CornerRadius(curva - d)))
            val g = gema; val q = w * 0.2f
            chaton.moveTo(g.x, g.y - q); chaton.lineTo(g.x + q * 0.9f, g.y); chaton.lineTo(g.x, g.y + q); chaton.lineTo(g.x - q * 0.9f, g.y); chaton.close()
        }
    }

    private var cache: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cache?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cache = it }

    /** Los números de los días ya medidos, uno por modo (la tinta va en la medida); se vacía si cambia el tamaño o la letra. */
    private val numeros = List(2) { HashMap<String, TextLayoutResult>() }
    private val claveNumeros = FloatArray(4)

    private fun DrawScope.numeroDia(medidor: TextMeasurer, dia: String, caja: Rect, tinta: Color, oscuro: Boolean) {
        val k = claveNumeros
        if (k[0] != density || k[1] != fontScale || k[2] != caja.width || k[3] != caja.height) {
            numeros.forEach { it.clear() }; k[0] = density; k[1] = fontScale; k[2] = caja.width; k[3] = caja.height
        }
        val mapa = numeros[if (oscuro) 1 else 0]
        val capa = mapa[dia] ?: (capaEn(medidor, dia, caja, MuebleEstanteAntiguas.LETRA, tinta, 18f, peso = FontWeight.Bold, minimo = 9f) ?: return).also { mapa[dia] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val s = e.semilla and 0x7fffffff
        val cuero = t(CUEROS[s % CUEROS.size], oscuro, 0.3f)
        drawPath(g.sombra, sombraMueble(if (oscuro) 0.6f else 0.3f))
        drawPath(g.cuero, cuero)
        // La luz del cuero: el canto de la izquierda y el de arriba a la luz; el de la derecha, en sombra.
        drawRect(luzMueble(if (oscuro) 0.08f else 0.22f), Offset(w * 0.08f, h * 0.05f), Size(w * 0.1f, h * 0.8f))
        drawRect(sombraMueble(0.28f), Offset(w * 0.8f, h * 0.05f), Size(w * 0.12f, h * 0.8f))
        drawPath(g.pespunte, t(P.CUERDA, oscuro, 0.35f).copy(alpha = 0.75f), style = Stroke(0.8f * u, pathEffect = g.costura))
        // La cartela de pergamino con el día a tinta.
        val c = g.cartela
        drawRect(sombraMueble(0.35f), Offset(c.left + 0.8f * u, c.top + 1f * u), c.size)
        drawRect(t(P.VITELA, oscuro, 0.22f), c.topLeft, c.size)
        drawRect(t(P.VITELA_OSC, oscuro, 0.2f).copy(alpha = 0.5f), Offset(c.left, c.bottom - c.height * 0.2f), Size(c.width, c.height * 0.2f))
        drawRect(t(P.LATON_OSC, oscuro, 0.2f), c.topLeft, c.size, style = Stroke(0.8f * u))
        val tinta = if (oscuro) mezcla(P.TINTA, PENUMBRA, 0.1f) else P.TINTA
        numeroDia(medidor, e.dia, Rect(c.left, c.top + 0.6f * u, c.right, c.bottom - 0.4f * u), tinta, oscuro)
        // La letra de la semana, en oro sobre el cuero.
        val oro = if (oscuro) mezcla(P.ORO_LUZ, PENUMBRA, 0.15f) else P.ORO_LUZ
        textoEn(medidor, e.semana, g.semana, MuebleEstanteAntiguas.LETRA, oro, 9f, minimo = 7f)
        // El clavo de latón de arriba.
        drawCircle(t(P.LATON_OSC, oscuro, 0.2f), 1.9f * u, Offset(w * 0.5f, h * 0.1f))
        drawCircle(t(P.LATON_LUZ, oscuro, 0.2f), 0.8f * u, Offset(w * 0.5f - 0.5f * u, h * 0.1f - 0.5f * u))
        // El ánimo: la gema en su chatón de latón en rombo; sin ánimo, dos clavos y nada más.
        val tono = e.tono
        val q = g.gema
        if (tono != null) {
            drawPath(g.chaton, t(P.LATON, oscuro, 0.2f))
            drawPath(g.chaton, t(P.LATON_OSC, oscuro, 0.2f), style = Stroke(0.8f * u))
            drawCircle(tono, w * 0.12f, q)
            drawCircle(sombraMueble(0.3f), w * 0.12f, q, style = Stroke(0.7f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), w * 0.036f, q - Offset(w * 0.036f, w * 0.036f))
        } else {
            listOf(-1f, 1f).forEach { sg ->
                drawCircle(t(P.LATON_OSC, oscuro, 0.2f), 1.6f * u, q + Offset(sg * w * 0.13f, 0f))
                drawCircle(t(P.LATON_LUZ, oscuro, 0.2f), 0.6f * u, q + Offset(sg * w * 0.13f - 0.4f * u, -0.4f * u))
            }
        }
    }
}
