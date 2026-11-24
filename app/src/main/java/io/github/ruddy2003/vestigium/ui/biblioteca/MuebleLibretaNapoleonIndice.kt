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
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de una pestaña, armados una vez por tamaño: el cabo de cinta que sale del ojal (sin ánimo). */
private class FormasPestanaNapoleon(val w: Float, val h: Float, u: Float) {
    val tarjeta = Rect(3f * u, 3f * u, w - 3f * u, h - 6f * u)
    val banda = Rect(tarjeta.left, tarjeta.top, tarjeta.right, tarjeta.top + 12f * u)
    val cajaSemana = Rect(banda.left + 2f * u, banda.top + 1.5f * u, banda.right - 2f * u, banda.bottom - 1f * u)
    val cajaDia = Rect(tarjeta.left + 1f * u, banda.bottom + 1f * u, tarjeta.right - 1f * u, tarjeta.bottom - 12f * u)
    val sello = Offset(tarjeta.right - 9f * u, tarjeta.bottom - 5f * u)
    val ojal = Offset(tarjeta.left + 8f * u, tarjeta.bottom - 7f * u)
    /** El cabo de cinta que sale del ojal hacia la izquierda y asoma por el canto de la tarjeta, cortado en cola de golondrina. */
    val cabo = Path().apply {
        val o = ojal; val g = 1.5f * u
        moveTo(o.x, o.y - g); lineTo(1f * u, o.y - g); lineTo(2.6f * u, o.y); lineTo(1f * u, o.y + g); lineTo(o.x, o.y + g); close()
    }
}

/**
 * El índice de la libreta de Napoleónico: cada día, una tarjeta de cartulina del color de la vitela del legajo, con su cabecera de
 * papel pizarra (el de las carpetas del archivo) y la letra de la semana; el día, en la letra de la casa. Con ánimo, el sello de lacre del color
 * del ánimo (los mismos tintes que las entradas de la biblioteca), abajo a la derecha, montado en el borde; sin ánimo no lleva
 * sello sino un ojal de latón con un cabo de la cinta carmesí que asoma por la izquierda (la silueta lo dice, no solo el
 * color). El rótulo de cada mes, una tira de papel pizarra con sus puntas de becerro y, en medio, la cartulina con el mes.
 */
internal object IndiceLibretaNapoleon {
    private val P = PaletaNapoleon
    private val L = LegajoLibretaNapoleon
    private var formas: FormasPestanaNapoleon? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaNapoleon =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaNapoleon(w, h, u).also { formas = it }

    /**
     * Los textos ya medidos, con su tinta (el medidor de la pantalla guarda solo 8 medidas; y pintar una medida con otro color
     * obliga a rehacerla): uno por modo, y se vacían si cambian la escala o el tamaño de la pestaña.
     */
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

    /** La cartulina y el papel pizarra del índice: en oscuro se hunden hacia el pardo de la noche, sin dejar de leerse. */
    private fun cartulina(oscuro: Boolean) = if (oscuro) mezcla(P.PAPEL, Color(0xFF241A12), 0.3f) else P.PAPEL
    private fun pizarra(oscuro: Boolean) = if (oscuro) mezcla(L.PIZARRA, Color(0xFF0C1014), 0.3f) else L.PIZARRA

    private val SOMBRA = Color(0xFF2A1A10).copy(alpha = 0.3f)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val t = f.tarjeta
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else SOMBRA
        drawRect(sombra, t.topLeft + Offset(1.2f * u, 1.6f * u), t.size)
        drawRect(cartulina(oscuro), t.topLeft, t.size)
        // La cabecera de papel pizarra, con su canto claro, y el filete impreso de la tarjeta.
        val pz = pizarra(oscuro)
        drawRect(pz, f.banda.topLeft, f.banda.size)
        drawRect(mezcla(pz, Color.White, 0.22f), f.banda.topLeft, Size(f.banda.width, 1f * u))
        drawRect(mezcla(pz, Color.Black, 0.35f), Offset(f.banda.left, f.banda.bottom - 1f * u), Size(f.banda.width, 1f * u))
        drawRect(P.TINTA.copy(alpha = 0.4f), Offset(t.left + 2.5f * u, f.banda.bottom + 2f * u), Size(t.width - 5f * u, t.bottom - f.banda.bottom - 4.5f * u), style = Stroke(0.6f * u))
        texto(medidor, e.semana, f.cajaSemana, mezcla(P.PAPEL, pz, 0.12f), 8.5f, FontWeight.Bold, oscuro)
        texto(medidor, e.dia, f.cajaDia, P.TINTA, 18f, FontWeight.SemiBold, oscuro)
        val tinte = tonoDeDiaNapoleon(e.tono, oscuro)
        if (tinte != null) {
            // El sello de lacre del color del ánimo, montado en el borde: el charco de cera, el disco estampado y su filo claro.
            val c = if (oscuro) mezcla(tinte, Color.Black, 0.15f) else tinte
            val s = f.sello
            drawCircle(sombra, 6.6f * u, s + Offset(0.8f * u, 1f * u))
            drawCircle(mezcla(c, Color.Black, 0.25f), 6.6f * u, s)
            drawCircle(c, 5f * u, s)
            drawCircle(mezcla(c, Color.Black, 0.3f), 3.2f * u, s, style = Stroke(0.8f * u))
            drawCircle(mezcla(c, Color.White, 0.45f), 1.1f * u, s - Offset(2.6f * u, 2.6f * u))
        } else {
            // Sin ánimo: el ojal de latón y el cabo de cinta carmesí que asoma por la izquierda.
            val cinta = if (oscuro) mezcla(L.CINTA, Color.Black, 0.2f) else L.CINTA
            drawPath(f.cabo, cinta)
            drawCircle(P.LATON, 3f * u, f.ojal)
            drawCircle(P.LATON_OSC, 3f * u, f.ojal, style = Stroke(0.6f * u))
            drawCircle(Color(0xFF1A0E08), 1.3f * u, f.ojal)
        }
    }

    /** El rótulo de un mes: la tira de papel pizarra con sus puntas de becerro y sus ojales, y en medio la cartulina con el mes. */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val pz = pizarra(oscuro)
        val becerro = if (oscuro) mezcla(P.BECERRO, Color.Black, 0.3f) else P.BECERRO
        val tira = Rect(4f * u, h * 0.5f - 7f * u, w - 4f * u, h * 0.5f + 7f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), tira.topLeft + Offset(1.4f * u, 1.8f * u), tira.size)
        drawRect(pz, tira.topLeft, tira.size)
        drawRect(mezcla(pz, Color.White, 0.2f), tira.topLeft, Size(tira.width, 1.2f * u))
        drawRect(becerro, tira.topLeft, Size(12f * u, tira.height))
        drawRect(becerro, Offset(tira.right - 12f * u, tira.top), Size(12f * u, tira.height))
        val ancho = minOf(w - 44f * u, 330f * u)
        val c = Rect(w / 2 - ancho / 2, 3f * u, w / 2 + ancho / 2, h - 3f * u)
        listOf(c.left - 8f * u, c.right + 8f * u).forEach { x ->
            drawCircle(P.LATON, 2.6f * u, Offset(x, tira.center.y))
            drawCircle(Color(0xFF1A0E08), 1.1f * u, Offset(x, tira.center.y))
        }
        drawRect(Color.Black.copy(alpha = 0.22f), c.topLeft + Offset(1.2f * u, 1.6f * u), c.size)
        drawRect(cartulina(oscuro), c.topLeft, c.size)
        drawRect(P.TINTA.copy(alpha = 0.6f), c.deflate(2.5f * u).topLeft, c.deflate(2.5f * u).size, style = Stroke(0.8f * u))
        textoEn(medidor, texto, Rect(c.left + 10f * u, c.top + 3f * u, c.right - 10f * u, c.bottom - 3f * u), P.LETRA, P.TINTA, 13f, peso = FontWeight.SemiBold, espaciado = 0.8f, minimo = 7f)
    }
}
