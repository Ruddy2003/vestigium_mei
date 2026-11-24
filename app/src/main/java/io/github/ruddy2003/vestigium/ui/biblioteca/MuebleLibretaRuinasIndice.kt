package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de una pestaña, armados una vez por tamaño: la hoja de herbario de canto barbado, la lengüeta, el lazo y la hoja prensada. */
private class FormasPestanaRuinas(val w: Float, val h: Float, u: Float) {
    val hoja = Path()
    val lengueta: Path
    val lazo = Path()
    val planta: Path
    /** Dónde van el día y la letra de la semana. */
    val cajaDia = Rect(5f * u, 15f * u, w - 7f * u, h - 16f * u)
    val cajaSemana = Rect(5f * u, 4f * u, 16f * u, 13f * u)

    init {
        val l = 3f * u; val r = w - 7f * u; val t = 3f * u; val b = h - 4f * u
        // El papel de herbario, grueso, con el canto de arriba y el de abajo barbados (hecho a mano).
        hoja.moveTo(l, t)
        var x = l; var j = 0
        while (x < r) { x += 3f * u; hoja.lineTo(minOf(x, r), t + (if (j % 2 == 0) 0.9f else 0f) * u); j++ }
        hoja.lineTo(r, b)
        x = r; j = 0
        while (x > l) { x -= 3f * u; hoja.lineTo(maxOf(x, l), b - (if (j % 2 == 0) 0.9f else 0f) * u); j++ }
        hoja.close()
        // La lengüeta de tela del ánimo, que asoma por el canto derecho, arriba (como un registro).
        lengueta = poligonoRuinas(Offset(r - 3f * u, 7f * u), Offset(w - 0.5f * u, 8f * u), Offset(w - 0.5f * u, 30f * u), Offset(r - 3f * u, 31f * u))
        // Sin ánimo: el agujero de arriba y el lazo de bramante que pasa por él.
        lazo.moveTo(r - 5f * u, 7f * u); lazo.cubicTo(w + 1f * u, 2f * u, w + 1f * u, 16f * u, r - 3f * u, 12f * u)
        // La hojita prensada del pie: una hoja ovalada con su peciolo, siempre igual.
        planta = cerradaRuinas(listOf(Offset(7f * u, b - 3f * u), Offset(10f * u, b - 9f * u), Offset(15f * u, b - 11f * u), Offset(14f * u, b - 6f * u)))
    }
}

/**
 * El índice de la libreta de la Ciudad verde: cada día, una hoja del herbario (papel grueso de canto barbado) con el día a lápiz,
 * la letra de la semana y una hojita prensada al pie. Con ánimo, una lengüeta de tela del color del ánimo (la misma tela que los
 * libros de la biblioteca) asoma por el canto, como un registro; sin ánimo no hay lengüeta sino un lazo de bramante por un
 * agujero (lo dice la forma, no solo el color).
 */
internal object IndiceLibretaRuinas {
    private var formas: FormasPestanaRuinas? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaRuinas =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaRuinas(w, h, u).also { formas = it }

    /** Los números de los días ya medidos (el medidor de la pantalla guarda solo 8 medidas): se vacía si cambia la escala. */
    private val numeros = HashMap<String, TextLayoutResult>()
    private var escala = 0f to 0f

    private fun DrawScope.numeroDia(medidor: TextMeasurer, dia: String, caja: Rect, tinta: Color) {
        if (escala.first != density || escala.second != fontScale) { numeros.clear(); escala = density to fontScale }
        val capa = numeros.getOrPut(dia) {
            medidor.measure(dia, TextStyle(fontFamily = P.LETRA, fontSize = 19.sp, lineHeight = (19f * 1.05f).sp, textAlign = TextAlign.Center), maxLines = 1, softWrap = false)
        }
        if (capa.size.width > caja.width || capa.size.height > caja.height) { textoEn(medidor, dia, caja, P.LETRA, tinta, 19f, minimo = 10f); return }
        drawText(capa, color = tinta, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    private val SOMBRA = Color(0xFF2A2436).copy(alpha = 0.3f)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val s = e.semilla and 0x7fffffff
        val sombra = if (oscuro) Color(0xFF050A07).copy(alpha = 0.55f) else SOMBRA
        val papel = mezcla(P.PAPEL, P.KRAFT, 0.15f + 0.25f * azar(s, 2)).let { if (oscuro) mezcla(it, Color(0xFF1C2A22), 0.3f) else it }
        val tono = tonoDeDiaRuinas(e.tono, oscuro)
        if (tono != null) {
            val tela = materialDelAnimoRuinas(tono, P.TELAS).let { if (oscuro) mezcla(it, Color.Black, 0.12f) else it }
            translate(1f * u, 1.4f * u) { drawPath(f.lengueta, sombra) }
            drawPath(f.lengueta, tela)
            drawRect(mezcla(tela, Color.White, 0.25f), Offset(w - 7f * u, 9f * u), Size(6f * u, 1.4f * u))
        }
        translate(1.2f * u, 1.6f * u) { drawPath(f.hoja, sombra) }
        drawPath(f.hoja, papel)
        // El papel con su mancha de agua al pie y el canto, y la hojita prensada.
        drawRect(mezcla(papel, P.KRAFT_OSC, 0.3f).copy(alpha = 0.35f), Offset(3f * u, h - 13f * u), Size(w - 10f * u, 8f * u))
        drawPath(f.hoja, sombra.copy(alpha = 0.45f), style = Stroke(0.6f * u))
        drawPath(f.planta, mezcla(if (s % 2 == 0) P.HELECHO else P.HIEDRA_LUZ, P.KRAFT, 0.35f).let { if (oscuro) mezcla(it, Color.Black, 0.2f) else it })
        if (tono == null) {
            // Sin ánimo: el agujero y el lazo de bramante.
            drawCircle(sombra.copy(alpha = 0.7f), 1.5f * u, Offset(w - 12f * u, 9.5f * u))
            drawPath(f.lazo, P.BRAMANTE.let { if (oscuro) mezcla(it, Color.Black, 0.2f) else it }, style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        val tinta = if (oscuro) Color(0xFF101410) else P.LAPIZ
        textoEn(medidor, e.semana, f.cajaSemana, P.LETRA_MES, tinta.copy(alpha = 0.75f), 8f, peso = FontWeight.SemiBold, minimo = 5f)
        numeroDia(medidor, e.dia, f.cajaDia, tinta)
    }
}
