package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de una pestaña, armados una vez por tamaño: el frente del cajoncito, sus cajas de texto, el tirador y el lazo. */
private class FormasPestanaNavidad(val w: Float, val h: Float, u: Float) {
    val frente = Rect(3f * u, 3f * u, w - 3f * u, h - 5f * u)
    val filete = Rect(frente.left + 2.5f * u, frente.top + 2.5f * u, frente.right - 2.5f * u, frente.bottom - 2.5f * u)
    val cajaSemana = Rect(frente.left + 2f * u, frente.top + 3.5f * u, frente.right - 2f * u, frente.top + 12f * u)
    val cajaDia = Rect(frente.left + 1f * u, frente.top + 11f * u, frente.right - 1f * u, frente.top + 32f * u)
    val tirador = Offset(frente.center.x, frente.bottom - 12.5f * u)
    /** El lazo de cinta atado al tirador: las dos orejas y las dos colas. */
    val orejas = Path().apply {
        val n = Offset(tirador.x, tirador.y + 4.2f * u)
        for (l in listOf(-1f, 1f)) {
            moveTo(n.x, n.y)
            cubicTo(n.x + l * 3.5f * u, n.y - 5.5f * u, n.x + l * 11f * u, n.y - 4f * u, n.x + l * 9f * u, n.y + 1.8f * u)
            close()
        }
    }
    val colas = Path().apply {
        val n = Offset(tirador.x, tirador.y + 4.2f * u)
        moveTo(n.x - 1f * u, n.y); lineTo(n.x - 6f * u, n.y + 7.5f * u); lineTo(n.x - 3.8f * u, n.y + 6.6f * u); lineTo(n.x - 2.8f * u, n.y + 8.2f * u); lineTo(n.x + 0.8f * u, n.y); close()
        moveTo(n.x + 1f * u, n.y); lineTo(n.x + 4.6f * u, n.y + 8.2f * u); lineTo(n.x + 5.4f * u, n.y + 6.4f * u); lineTo(n.x + 7.6f * u, n.y + 7f * u); lineTo(n.x - 0.8f * u, n.y); close()
    }
    val nudo = Offset(tirador.x, tirador.y + 4.2f * u)
}

/**
 * El índice de la libreta de Navidad: cada día, el frente de un cajoncito de la cajonera de adviento (pino pintado de crema con su
 * filete verde), con la letra de la semana arriba, el número del día pintado y el tirador de porcelana abajo. Con ánimo, el
 * tirador lleva atado un lazo de cinta del color del ánimo (los mismos tintes que las entradas de la biblioteca); sin ánimo, el
 * tirador queda desnudo, sin lazo: la silueta lo dice, no solo el color.
 */
internal object IndiceLibretaNavidad {
    private val P = PaletaNavidad
    private var formas: FormasPestanaNavidad? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaNavidad =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaNavidad(w, h, u).also { formas = it }

    /** Los textos ya medidos (el medidor de la pantalla guarda solo 8 medidas): se vacían si cambian la escala o el tamaño. */
    private val medidas = HashMap<String, TextLayoutResult>()
    private val claveMedidas = FloatArray(3)

    private fun DrawScope.texto(medidor: TextMeasurer, texto: String, caja: Rect, letra: FontFamily, tinta: Color, tam: Float, peso: FontWeight) {
        val k = claveMedidas
        val ancho = formas?.w ?: 0f
        if (k[0] != density || k[1] != fontScale || k[2] != ancho) { medidas.clear(); k[0] = density; k[1] = fontScale; k[2] = ancho }
        val clave = "$tam:$texto"
        val capa = medidas[clave] ?: (capaEn(medidor, texto, caja, letra, tinta, tam, peso = peso, minimo = tam * 0.55f) ?: return).also { medidas[clave] = it }
        drawText(capa, color = tinta, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /** La pintura crema del frente y su filete: en oscuro se hunden un poco hacia el verde azulado de la cocina, sin dejar de leerse. */
    private fun crema(oscuro: Boolean) = if (oscuro) mezcla(P.CREMA, P.NOCHE, 0.32f) else P.CREMA
    private fun filete(oscuro: Boolean) = if (oscuro) mezcla(P.FILETE, P.NOCHE, 0.25f) else P.FILETE
    private val TINTA_DIA = mezcla(P.CARMIN, P.TINTA, 0.35f)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val fr = f.frente
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else Color(0xFF2A2436).copy(alpha = 0.28f)
        drawRect(sombra, fr.topLeft + Offset(1.2f * u, 1.6f * u), fr.size)
        val cr = crema(oscuro)
        drawRect(cr, fr.topLeft, fr.size)
        drawRect(filete(oscuro), f.filete.topLeft, f.filete.size, style = Stroke(1f * u))
        drawRect(mezcla(cr, Color.White, 0.35f), fr.topLeft, Size(fr.width, 1f * u))
        drawRect(mezcla(cr, P.TINTA, 0.25f), Offset(fr.left, fr.bottom - 1.2f * u), Size(fr.width, 1.2f * u))
        texto(medidor, e.semana, f.cajaSemana, P.LETRA, filete(oscuro), 8f, FontWeight.Bold)
        texto(medidor, e.dia, f.cajaDia, P.LETRA, TINTA_DIA, 18f, FontWeight.Bold)
        // El tirador de porcelana con su sombra.
        val t = f.tirador
        drawCircle(sombra, 3.4f * u, t + Offset(0.9f * u, 1f * u))
        drawCircle(if (oscuro) mezcla(P.LOZA, P.NOCHE, 0.25f) else P.LOZA, 3.1f * u, t)
        drawCircle(Color.White.copy(alpha = 0.8f), 1f * u, t - Offset(1f * u, 1f * u))
        val tinte = tonoDeDiaNavidad(e.tono, oscuro) ?: return
        // Con ánimo: el lazo de cinta del color del ánimo, atado al tirador.
        val c = if (oscuro) mezcla(tinte, Color.Black, 0.15f) else tinte
        val hondo = mezcla(c, Color.Black, 0.3f)
        drawPath(f.colas, hondo)
        drawPath(f.orejas, c)
        drawPath(f.orejas, hondo, style = Stroke(0.6f * u))
        drawCircle(hondo, 1.6f * u, f.nudo)
    }
}
