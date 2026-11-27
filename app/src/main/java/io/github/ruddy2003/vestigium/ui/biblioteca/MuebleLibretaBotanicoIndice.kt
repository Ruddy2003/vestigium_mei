package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de una pestaña, armados una vez por tamaño: la hoja prensada y el sobrecito de semillas (sin ánimo). */
private class FormasPestanaBotanico(val w: Float, val h: Float, u: Float) {
    val tarjeta = Rect(3f * u, 3f * u, w - 3f * u, h - 4f * u)
    val banda = Rect(tarjeta.left, tarjeta.top, tarjeta.right, tarjeta.top + 11f * u)
    val cajaSemana = Rect(banda.left + 2f * u, banda.top + 1f * u, banda.right - 9f * u, banda.bottom - 0.5f * u)
    val cajaDia = Rect(tarjeta.left + 1f * u, banda.bottom + 1f * u, tarjeta.right - 1f * u, banda.bottom + 22f * u)
    /** Donde nace la hoja (abajo a la izquierda) y hacia dónde va (a la derecha, algo arriba): llena el ancho de la mitad de abajo. */
    val base = Offset(tarjeta.left + 2.5f * u, tarjeta.bottom - 6.5f * u)
    /** La hoja prensada, con la base en el origen y la punta hacia la derecha. */
    val hoja = Path().apply {
        val l = 34f * u; val a = 7.6f * u
        moveTo(0f, 0f)
        cubicTo(l * 0.25f, -a * 1.1f, l * 0.7f, -a * 0.9f, l, 0f)
        cubicTo(l * 0.7f, a * 0.8f, l * 0.25f, a * 1.05f, 0f, 0f)
        close()
    }
    /** El sobrecito de papel para las semillas: un rectángulo con su solapa en pico. */
    val sobre = Path().apply {
        val x0 = tarjeta.left + 9f * u; val x1 = tarjeta.right - 9f * u
        val y0 = tarjeta.bottom - 15f * u; val y1 = tarjeta.bottom - 3.5f * u
        moveTo(x0, y0); lineTo(x1, y0); lineTo(x1, y1); lineTo(x0, y1); close()
    }
    val solapa = Path().apply {
        val x0 = tarjeta.left + 9f * u; val x1 = tarjeta.right - 9f * u
        val y0 = tarjeta.bottom - 15f * u
        moveTo(x0, y0); lineTo((x0 + x1) / 2, y0 + 6f * u); lineTo(x1, y0)
    }
}

/**
 * El índice de la libreta de Botánico: cada día, un pliego en miniatura del herbario, del mismo material que la carpeta: el
 * papel crema, la franja de la tela verde arriba con la letra de la semana y la punta de piel, el día en la letra de la casa en
 * negrita, y abajo la hoja prensada del color del ánimo (los mismos tintes que las entradas de la biblioteca), sujeta con su
 * tira engomada. Sin ánimo no hay hoja sino el sobrecito de papel cerrado de las semillas: la forma lo dice, no solo el color.
 */
internal object IndiceLibretaBotanico {
    private val P = PaletaBotanico
    private var formas: FormasPestanaBotanico? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaBotanico =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaBotanico(w, h, u).also { formas = it }

    /** Los textos ya medidos, uno por modo; se vacían si cambian la escala o el tamaño de la pestaña. */
    private val medidas = arrayOf(HashMap<String, TextLayoutResult>(), HashMap<String, TextLayoutResult>())
    private val claveMedidas = FloatArray(3)

    private fun DrawScope.texto(medidor: TextMeasurer, texto: String, caja: Rect, tinta: Color, tam: Float, peso: FontWeight, oscuro: Boolean) {
        val k = claveMedidas
        val ancho = formas?.w ?: 0f
        if (k[0] != density || k[1] != fontScale || k[2] != ancho) { medidas.forEach { it.clear() }; k[0] = density; k[1] = fontScale; k[2] = ancho }
        val mapa = medidas[if (oscuro) 1 else 0]
        val clave = "$tam:$texto"
        val capa = mapa[clave] ?: (capaEn(medidor, texto, caja, P.LETRA, tinta, tam, peso = peso, minimo = tam * 0.55f) ?: return).also { mapa[clave] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /** El papel y la tela del índice: en oscuro se hunden hacia el verde petróleo, sin dejar de leerse. */
    private fun papel(oscuro: Boolean) = if (oscuro) mezcla(P.PAPEL, Color(0xFF16201C), 0.3f) else P.PAPEL
    private fun tela(oscuro: Boolean) = if (oscuro) mezcla(HerbarioLibretaBotanico.TELA_TAPA, P.NOCHE, 0.3f) else HerbarioLibretaBotanico.TELA_TAPA

    private val SOMBRA = Color(0xFF2A2436).copy(alpha = 0.3f)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val t = f.tarjeta
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else SOMBRA
        val pa = papel(oscuro)
        drawRect(sombra, t.topLeft + Offset(1.2f * u, 1.6f * u), t.size)
        drawRect(pa, t.topLeft, t.size)
        // La franja de tela verde, con su canto claro, y la punta de piel a la derecha.
        val te = tela(oscuro)
        drawRect(te, f.banda.topLeft, f.banda.size)
        drawRect(mezcla(te, Color.White, 0.18f), f.banda.topLeft, Size(f.banda.width, 0.9f * u))
        drawRect(mezcla(te, Color.Black, 0.35f), Offset(f.banda.left, f.banda.bottom - 0.9f * u), Size(f.banda.width, 0.9f * u))
        drawRect(if (oscuro) mezcla(P.CUERO, Color.Black, 0.25f) else P.CUERO, Offset(f.banda.right - 7f * u, f.banda.top), Size(7f * u, f.banda.height))
        drawRect(P.TINTA.copy(alpha = 0.22f), Offset(t.right - 0.8f * u, t.top), Size(0.8f * u, t.height))
        texto(medidor, e.semana, f.cajaSemana, mezcla(P.PAPEL, te, 0.1f), 8f, FontWeight.Bold, oscuro)
        texto(medidor, e.dia, f.cajaDia, P.TINTA, 18f, FontWeight.Bold, oscuro)
        val tinte = tonoDeDiaBotanico(e.tono, oscuro)
        if (tinte != null) {
            // La hoja prensada del color del ánimo, algo girada según la semilla, y la tira engomada que la sujeta.
            val c = if (oscuro) mezcla(tinte, Color.Black, 0.18f) else tinte
            val giro = -8f - 8f * ((e.semilla ushr 3) and 7) / 7f
            translate(f.base.x, f.base.y) {
                rotate(giro, Offset.Zero) {
                    translate(0.8f * u, 1.1f * u) { drawPath(f.hoja, sombra) }
                    drawPath(f.hoja, c)
                    drawLine(mezcla(c, Color.Black, 0.35f), Offset(-2f * u, 0f), Offset(30f * u, 0f), 0.8f * u)
                    drawLine(mezcla(c, Color.White, 0.3f), Offset(7f * u, -2.2f * u), Offset(20f * u, -3.4f * u), 0.7f * u)
                    drawRect(if (oscuro) mezcla(P.KRAFT, Color.Black, 0.2f) else P.KRAFT, Offset(4f * u, -2.8f * u), Size(5f * u, 5.6f * u))
                }
            }
        } else {
            // Sin ánimo: el sobrecito de papel cerrado, con su solapa en pico (la silueta lo dice).
            translate(0.8f * u, 1.1f * u) { drawPath(f.sobre, sombra) }
            drawPath(f.sobre, mezcla(pa, P.KRAFT, 0.35f))
            drawPath(f.solapa, P.TINTA.copy(alpha = 0.45f), style = Stroke(0.7f * u))
            drawPath(f.sobre, P.TINTA.copy(alpha = 0.35f), style = Stroke(0.6f * u))
        }
    }
}
