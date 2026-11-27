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

/** Los caminos de una pestaña, armados una vez por tamaño: el frente del cajoncito, su panel, sus cajas de texto, el tirador y el lazo. */
private class FormasPestanaNavidad(val w: Float, val h: Float, u: Float) {
    val frente = Rect(3f * u, 3f * u, w - 3f * u, h - 5f * u)
    /** El panel de dentro del frente (pintado del color del ánimo) bajo la franja crema de arriba, con la letra de la semana. */
    val panel = Rect(frente.left + 3f * u, frente.top + 11f * u, frente.right - 3f * u, frente.bottom - 3f * u)
    val cajaSemana = Rect(frente.left + 2f * u, frente.top + 2f * u, frente.right - 2f * u, frente.top + 10.5f * u)
    val cajaDia = Rect(panel.left - 1f * u, panel.top + 1f * u, panel.right + 1f * u, panel.top + 22f * u)
    val tirador = Offset(frente.center.x, frente.bottom - 10.5f * u)
    /** El lazo de cinta atado al tirador: las dos orejas y las dos colas. */
    val nudo = Offset(tirador.x, tirador.y + 3.6f * u)
    val orejas = Path().apply {
        val n = nudo
        for (l in listOf(-1f, 1f)) {
            moveTo(n.x, n.y)
            cubicTo(n.x + l * 3f * u, n.y - 4.6f * u, n.x + l * 9.5f * u, n.y - 3.4f * u, n.x + l * 7.8f * u, n.y + 1.5f * u)
            close()
        }
    }
    val colas = Path().apply {
        val n = nudo
        moveTo(n.x - 1f * u, n.y); lineTo(n.x - 5f * u, n.y + 6f * u); lineTo(n.x - 3.2f * u, n.y + 5.3f * u); lineTo(n.x - 2.3f * u, n.y + 6.6f * u); lineTo(n.x + 0.8f * u, n.y); close()
        moveTo(n.x + 1f * u, n.y); lineTo(n.x + 3.8f * u, n.y + 6.6f * u); lineTo(n.x + 4.5f * u, n.y + 5.1f * u); lineTo(n.x + 6.3f * u, n.y + 5.6f * u); lineTo(n.x - 0.8f * u, n.y); close()
    }
    /** Las vetas del pino sin pintar (el día sin ánimo): tres líneas a lo alto. */
    val vetas = floatArrayOf(frente.left + frente.width * 0.24f, frente.left + frente.width * 0.55f, frente.left + frente.width * 0.8f)
}

/**
 * El índice de la libreta de Navidad: cada día, el frente de un cajoncito de la cajonera de adviento. Con ánimo, el frente crema
 * con el panel de dentro pintado del color del ánimo (los mismos tintes que las entradas de la biblioteca), la letra de la semana en
 * la franja crema de arriba, el número en la tinta que se lea sobre ese color (oscura sobre los claros, crema sobre los hondos) y el
 * tirador de porcelana con su lazo de cinta. Sin ánimo, el frente de pino sin pintar, con sus vetas, sin panel ni lazo: el tirador
 * desnudo (la silueta lo dice, no solo el color).
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

    /** La pintura crema del frente: en oscuro se hunde un poco hacia el verde azulado de la cocina, sin dejar de leerse. */
    private fun crema(oscuro: Boolean) = if (oscuro) mezcla(P.CREMA, P.NOCHE, 0.32f) else P.CREMA
    private fun filete(oscuro: Boolean) = if (oscuro) mezcla(P.FILETE, P.NOCHE, 0.25f) else P.FILETE
    private val TINTA_OSC = P.TINTA
    private val TINTA_CLARA = Color(0xFFF8F0DC)

    /** La luminancia relativa de un color (la de las pautas de contraste). */
    private fun lum(c: Color): Float {
        fun l(v: Float) = if (v <= 0.03928f) v / 12.92f else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
        return 0.2126f * l(c.red) + 0.7152f * l(c.green) + 0.0722f * l(c.blue)
    }

    /**
     * El color del panel y la tinta del número sobre él, con 4.5 a 1 o más: los claros llevan tinta oscura; los hondos, crema; los de
     * en medio (donde ninguna de las dos llega) se ahondan un poco, sin cambiar de tono, y llevan crema.
     */
    private fun panelYTinta(c: Color): Pair<Color, Color> {
        val l = lum(c)
        return when {
            l >= 0.25f -> c to TINTA_OSC
            l <= 0.13f -> c to TINTA_CLARA
            else -> mezcla(c, Color(0xFF0E1410), 0.36f) to TINTA_CLARA
        }
    }

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val fr = f.frente
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else Color(0xFF2A2436).copy(alpha = 0.28f)
        drawRect(sombra, fr.topLeft + Offset(1.2f * u, 1.6f * u), fr.size)
        val tinte = tonoDeDiaNavidad(e.tono, oscuro)
        if (tinte == null) {
            // Sin ánimo: el frente de pino sin pintar, con sus vetas, y el tirador desnudo.
            val pino = if (oscuro) mezcla(P.PINO, P.NOCHE, 0.22f) else P.PINO
            val (cara, tinta) = panelYTinta(pino)
            drawRect(cara, fr.topLeft, fr.size)
            val veta = mezcla(cara, P.PINO_OSC, 0.55f)
            f.vetas.forEach { x -> drawLine(veta, Offset(x, fr.top + 1f * u), Offset(x + 1.2f * u, fr.bottom - 1f * u), 0.7f * u) }
            drawRect(mezcla(cara, Color.White, 0.3f), fr.topLeft, Size(fr.width, 1f * u))
            drawRect(mezcla(cara, P.TINTA, 0.35f), Offset(fr.left, fr.bottom - 1.2f * u), Size(fr.width, 1.2f * u))
            texto(medidor, e.semana, f.cajaSemana, P.LETRA, tinta, 8f, FontWeight.Bold)
            texto(medidor, e.dia, f.cajaDia, P.LETRA, tinta, 18f, FontWeight.Bold)
            tiradorPestana(f, oscuro, sombra)
            return
        }
        // Con ánimo: el frente crema, el panel del color del ánimo con su filete y la sombra del marco, y el lazo en el tirador.
        val cr = crema(oscuro)
        drawRect(cr, fr.topLeft, fr.size)
        drawRect(mezcla(cr, Color.White, 0.35f), fr.topLeft, Size(fr.width, 1f * u))
        drawRect(mezcla(cr, P.TINTA, 0.25f), Offset(fr.left, fr.bottom - 1.2f * u), Size(fr.width, 1.2f * u))
        texto(medidor, e.semana, f.cajaSemana, P.LETRA, filete(oscuro), 8f, FontWeight.Bold)
        val (panel, tinta) = panelYTinta(if (oscuro) mezcla(tinte, Color.Black, 0.12f) else tinte)
        val pn = f.panel
        drawRect(panel, pn.topLeft, pn.size)
        drawRect(mezcla(panel, Color.Black, 0.3f), pn.topLeft, Size(pn.width, 1.6f * u))
        drawRect(filete(oscuro), pn.topLeft, pn.size, style = Stroke(1f * u))
        texto(medidor, e.dia, f.cajaDia, P.LETRA, tinta, 18f, FontWeight.Bold)
        tiradorPestana(f, oscuro, sombra)
        // El lazo de cinta, crema sobre los paneles hondos y rojo sobre los claros, como remate.
        val cinta = if (tinta == TINTA_OSC) (if (oscuro) mezcla(P.CARMIN, Color.Black, 0.15f) else P.CARMIN) else cr
        val hondo = mezcla(cinta, Color.Black, 0.3f)
        drawPath(f.colas, hondo)
        drawPath(f.orejas, cinta)
        drawPath(f.orejas, hondo, style = Stroke(0.6f * u))
        drawCircle(hondo, 1.6f * u, f.nudo)
    }

    /** El tirador de porcelana con su sombra y su brillo. */
    private fun DrawScope.tiradorPestana(f: FormasPestanaNavidad, oscuro: Boolean, sombra: Color) {
        val u = density
        val t = f.tirador
        drawCircle(sombra, 3.4f * u, t + Offset(0.9f * u, 1f * u))
        drawCircle(if (oscuro) mezcla(P.LOZA, P.NOCHE, 0.25f) else P.LOZA, 3.1f * u, t)
        drawCircle(Color.White.copy(alpha = 0.8f), 1f * u, t - Offset(1f * u, 1f * u))
    }
}
