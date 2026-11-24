package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de una pestaña, armados una vez por tamaño: la hoja, la hoja con la esquina cortada y la lengüeta de cuero. */
private class FormasPestanaRevolucion(val w: Float, val h: Float, u: Float) {
    val hoja: Path
    val cortada: Path
    val lengueta: Path
    val marco: Rect
    val cajaDia = Rect(5f * u, 18f * u, w - 5f * u, h - 9f * u)
    val cajaSemana = Rect(w - 17f * u, 9f * u, w - 6f * u, 18f * u)

    init {
        val l = 3f * u; val r = w - 3f * u; val t = 8f * u; val b = h - 3f * u
        hoja = poligonoRevolucion(l, t, r, t, r, b, l, b)
        val e = 9f * u
        cortada = poligonoRevolucion(l + e, t, r, t, r, b, l, b, l, t + e)
        // La lengüeta de cuero pegada arriba a la izquierda, que asoma por encima de la hoja (redondeada arriba).
        lengueta = Path().apply {
            moveTo(l + 4f * u, t + 6f * u); lineTo(l + 4f * u, 3f * u)
            quadraticTo(l + 4f * u, 1f * u, l + 7f * u, 1f * u); lineTo(l + 15f * u, 1f * u)
            quadraticTo(l + 18f * u, 1f * u, l + 18f * u, 3f * u); lineTo(l + 18f * u, t + 6f * u); close()
        }
        marco = Rect(l + 2.6f * u, t + 2.6f * u, r - 2.6f * u, b - 2.6f * u)
    }
}

/**
 * El índice de la libreta de Revolución: cada día, una hoja de papel de trapo cortada del registro, con el doble filete de
 * imprenta, el día en la letra de la casa y la letra de la semana arriba a la derecha. Con ánimo, una lengüeta de cuero del
 * color del ánimo (el mismo papel teñido de la biblioteca, en cuero) pegada arriba a la izquierda, que asoma; sin ánimo no
 * lleva lengüeta y la hoja tiene la esquina cortada (la silueta lo dice, no solo el color). El rótulo de cada mes, la etiqueta
 * impresa clavada en el listón de roble de la galería del pupitre.
 */
internal object IndiceLibretaRevolucion {
    private val P = PaletaRevolucion
    private var formas: FormasPestanaRevolucion? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaRevolucion =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaRevolucion(w, h, u).also { formas = it }

    /** Los números de los días ya medidos (el medidor de la pantalla guarda solo 8 medidas): se vacía si cambia la escala. */
    private val numeros = HashMap<String, TextLayoutResult>()
    private var escala = 0f to 0f

    private fun DrawScope.numeroDia(medidor: TextMeasurer, dia: String, caja: Rect, tinta: Color) {
        if (escala.first != density || escala.second != fontScale) { numeros.clear(); escala = density to fontScale }
        val capa = numeros.getOrPut(dia) {
            medidor.measure(dia, TextStyle(fontFamily = P.LETRA, fontSize = 18.sp, lineHeight = (18f * 1.05f).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                maxLines = 1, softWrap = false)
        }
        if (capa.size.width > caja.width || capa.size.height > caja.height) { textoEn(medidor, dia, caja, P.LETRA, tinta, 18f, peso = FontWeight.Bold, minimo = 10f); return }
        drawText(capa, color = tinta, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /** El papel del índice: en oscuro se hunde hacia el añil de la casa sin dejar de leerse. */
    private fun papel(oscuro: Boolean, s: Int): Color {
        val base = mezcla(P.PAPEL, P.TOSTADO, 0.15f + 0.3f * azar(s, 2))
        return if (oscuro) mezcla(base, Color(0xFF3A2418), 0.3f) else base
    }

    private val SOMBRA = Color(0xFF2A2436).copy(alpha = 0.32f)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val s = e.semilla and 0x7fffffff
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else SOMBRA
        val cuero = papelRevolucion(e.tono, oscuro)
        val hoja = if (cuero != null) f.hoja else f.cortada
        if (cuero != null) {
            // La lengüeta de cuero del color del ánimo, con su canto de luz y la sombra que deja en la hoja.
            val col = if (oscuro) mezcla(cuero, Color.Black, 0.2f) else cuero
            translate(1f * u, 1.4f * u) { drawPath(f.lengueta, sombra) }
            drawPath(f.lengueta, col)
            drawRect(mezcla(col, Color.White, 0.3f), Offset(f.marco.left + 2f * u, 2.4f * u), Size(1.2f * u, 8f * u))
            drawRect(mezcla(col, Color.Black, 0.3f), Offset(f.marco.left + 13f * u, 2f * u), Size(1.6f * u, 9f * u))
        }
        translate(1.2f * u, 1.6f * u) { drawPath(hoja, sombra) }
        drawPath(hoja, papel(oscuro, s))
        drawRect(Color(0xFFB09A6E).copy(alpha = if (oscuro) 0.25f else 0.3f), Offset(3f * u, h - 10f * u), Size(w - 6f * u, 7f * u))
        val tinta = if (oscuro) Color(0xFF140C08) else P.TINTA
        drawRect(tinta.copy(alpha = 0.55f), f.marco.topLeft, f.marco.size, style = Stroke(0.8f * u))
        drawPath(hoja, sombra.copy(alpha = 0.45f), style = Stroke(0.6f * u))
        if (cuero == null) {
            // Sin ánimo: la esquina cortada deja ver su canto.
            drawLine(Color(0xFFB09A6E), Offset(3f * u, 17f * u), Offset(12f * u, 8f * u), 1f * u)
        }
        textoEn(medidor, e.semana, f.cajaSemana, P.LETRA, if (oscuro) Color(0xFF8E2A26) else P.ROJO, 8.5f, peso = FontWeight.Bold)
        numeroDia(medidor, e.dia, f.cajaDia, tinta)
    }

    /** El rótulo de un mes: el listón de roble de la galería con sus clavos y, en medio, la etiqueta impresa con el mes. */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val roble = if (oscuro) mezcla(P.ROBLE, Color.Black, 0.4f) else P.ROBLE
        val list = Rect(4f * u, h * 0.5f - 6f * u, w - 4f * u, h * 0.5f + 6f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), list.topLeft + Offset(1.4f * u, 2f * u), list.size)
        drawRect(roble, list.topLeft, list.size)
        drawRect(mezcla(roble, Color.White, 0.22f), list.topLeft, Size(list.width, 1.4f * u))
        drawRect(mezcla(roble, Color.Black, 0.35f), Offset(list.left, list.bottom - 2f * u), Size(list.width, 2f * u))
        var x = list.left + 10f * u
        while (x < list.right - 6f * u) { drawCircle(mezcla(P.HIERRO, roble, 0.2f), 1.3f * u, Offset(x, list.center.y)); x += maxOf(1f, 36f * u) }
        val ancho = minOf(w - 30f * u, 330f * u)
        val c = Rect(w / 2 - ancho / 2, 3f * u, w / 2 + ancho / 2, h - 3f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), c.topLeft + Offset(1.4f * u, 1.8f * u), c.size)
        drawRect(papel(oscuro, 7), c.topLeft, c.size)
        drawRect(Color(0xFFB09A6E).copy(alpha = 0.35f), Offset(c.left, c.bottom - 5f * u), Size(c.width, 5f * u))
        val tinta = if (oscuro) Color(0xFF140C08) else P.TINTA
        drawRect(tinta.copy(alpha = 0.6f), c.deflate(2f * u).topLeft, c.deflate(2f * u).size, style = Stroke(1f * u))
        drawRect(tinta.copy(alpha = 0.4f), c.deflate(3.6f * u).topLeft, c.deflate(3.6f * u).size, style = Stroke(0.5f * u))
        listOf(c.left + 7f * u, c.right - 7f * u).forEach { cx ->
            drawCircle(P.LATON, 2f * u, Offset(cx, c.center.y)); drawCircle(P.LATON_LUZ, 0.8f * u, Offset(cx - 0.6f * u, c.center.y - 0.6f * u))
        }
        textoEn(medidor, texto, Rect(c.left + 14f * u, c.top + 3f * u, c.right - 14f * u, c.bottom - 3f * u), P.LETRA, tinta, 13f, peso = FontWeight.Bold, minimo = 7f)
    }
}
