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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de una pestaña, armados una vez por tamaño: el recorte con su pie rasgado, sus renglones y el alfiler. */
private class FormasPestanaArcano(val w: Float, val h: Float, u: Float) {
    val recorte: Path
    val renglones = Path()
    val alfiler = Path()
    /** Dónde van el día y la letra de la semana. */
    val cajaDia = Rect(4f * u, 13f * u, w - 4f * u, h - 16f * u)
    val cajaSemana = Rect(4f * u, h - 17f * u, w - 4f * u, h - 8f * u)

    init {
        val l = 3f * u; val r = w - 3f * u; val t = 5f * u; val b = h - 3f * u
        recorte = Path().apply {
            moveTo(l, t); lineTo(r, t); lineTo(r, b - 3f * u)
            // El pie rasgado: dientes desiguales.
            var x = r; var j = 0
            while (x > l) { x -= (2.5f + 2f * (j % 3)) * u; lineTo(maxOf(x, l), b - (if (j % 2 == 0) 0f else 2.4f) * u); j++ }
            close()
        }
        // Los renglones de la columna del periódico, que asoman abajo, en dos columnas.
        var y = h - 9f * u
        while (y < h - 5f * u) {
            renglones.moveTo(l + 3f * u, y); renglones.lineTo(w / 2 - 1.5f * u, y)
            renglones.moveTo(w / 2 + 1.5f * u, y); renglones.lineTo(r - 3f * u, y)
            y += 2f * u
        }
        // Sin ánimo, el alfiler que lo prende por arriba: la caña de través y la cabeza.
        alfiler.moveTo(w * 0.24f, 9f * u); alfiler.lineTo(w * 0.78f, 6.4f * u)
    }
}

/**
 * El índice de la libreta de Horror cósmico: cada día, un recorte de periódico de pie rasgado, como los del diario, con el día en
 * la letra del archivo y la letra de la semana. Con ánimo, una lengüeta de la misma tela de las tapas, teñida del color del ánimo
 * (la de los libros de la biblioteca), doblada sobre el canto de arriba; sin ánimo no lleva lengüeta sino un alfiler que lo prende
 * de través (la silueta lo dice, no solo el color). El rótulo de cada mes sigue siendo el titular recortado de siempre.
 */
internal object IndiceLibretaArcano {
    private val P = PaletaArcano
    private var formas: FormasPestanaArcano? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaArcano =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaArcano(w, h, u).also { formas = it }

    /** Las telas del ánimo, de lo bajo a lo alto (las mismas de los libros de registro de la biblioteca). */
    private val TELAS = listOf(0f to Color(0xFF5A1218), 0.28f to Color(0xFF902E28), 0.44f to Color(0xFFB98A6A), 0.5f to Color(0xFFD2C4A0),
        0.56f to Color(0xFFA2B190), 0.72f to Color(0xFF3C7860), 1f to Color(0xFF15463A))

    /** Los números de los días ya medidos (el medidor de la pantalla guarda solo 8 medidas): se vacía si cambia la escala. */
    private val numeros = HashMap<String, TextLayoutResult>()
    private var escala = 0f to 0f

    private fun DrawScope.numeroDia(medidor: TextMeasurer, dia: String, caja: Rect, tinta: Color) {
        if (escala.first != density || escala.second != fontScale) { numeros.clear(); escala = density to fontScale }
        val capa = numeros.getOrPut(dia) {
            medidor.measure(dia, TextStyle(fontFamily = P.LETRA, fontSize = 19.sp, lineHeight = (19f * 1.05f).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                maxLines = 1, softWrap = false)
        }
        if (capa.size.width > caja.width || capa.size.height > caja.height) { textoEn(medidor, dia, caja, P.LETRA, tinta, 19f, peso = FontWeight.Bold, minimo = 10f); return }
        drawText(capa, color = tinta, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    private val SOMBRA = Color(0xFF2A2436)
    private val TINTA = Color(0xFF1C2622)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val s = e.semilla and 0x7fffffff
        val sombra = SOMBRA.copy(alpha = if (oscuro) 0.6f else 0.34f)
        // El recorte: papel de periódico, unos más amarillos que otros; de noche, hundido en verde sin dejar de leerse.
        val base = mezcla(P.PAPEL, P.MANILA, 0.2f + 0.5f * azar(s, 2))
        val papel = if (oscuro) mezcla(base, Color(0xFF243A36), 0.3f) else base
        translate(1.2f * u, 1.6f * u) { drawPath(f.recorte, sombra) }
        drawPath(f.recorte, papel)
        drawRect(mezcla(papel, P.PAPEL_OSC, 0.5f), Offset(3f * u, h - 12f * u), Size(w - 6f * u, 1.2f * u))
        drawPath(f.renglones, TINTA.copy(alpha = 0.28f), style = Stroke(0.7f * u))
        drawPath(f.recorte, sombra.copy(alpha = 0.35f), style = Stroke(0.6f * u))
        val x = puntoAnimoArcano(e.tono, oscuro)
        val tela = if (x != null) rampaArcano(x, TELAS) else e.tono
        if (tela != null) {
            // La lengüeta de tela del color del ánimo, doblada sobre el canto de arriba: la cara de delante y el doblez en sombra.
            val col = if (oscuro) mezcla(tela, Color.Black, 0.15f) else tela
            val l = w * 0.3f; val r = w * 0.7f
            drawRect(sombra, Offset(l + 1.2f * u, 1.4f * u), Size(r - l, 11f * u))
            drawRect(mezcla(col, Color.Black, 0.35f), Offset(l, 0f), Size(r - l, 5f * u))
            drawRect(col, Offset(l, 4f * u), Size(r - l, 7.5f * u))
            drawRect(mezcla(col, Color.White, 0.25f), Offset(l, 4f * u), Size(r - l, 1f * u))
            drawRect(mezcla(col, Color.Black, 0.25f), Offset(r - 2f * u, 4f * u), Size(2f * u, 7.5f * u))
        } else {
            // Sin ánimo: el alfiler de través, con su cabeza y el brillo.
            drawPath(f.alfiler, sombra, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            drawPath(f.alfiler, if (oscuro) Color(0xFF8A9294) else Color(0xFFB8BEC0), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawCircle(if (oscuro) Color(0xFF6A3A30) else P.LACRE, 2.2f * u, Offset(w * 0.24f, 9f * u))
            drawCircle(Color.White.copy(alpha = 0.5f), 0.7f * u, Offset(w * 0.24f - 0.6f * u, 8.4f * u))
        }
        numeroDia(medidor, e.dia, f.cajaDia, TINTA)
        textoEn(medidor, e.semana, f.cajaSemana, P.LETRA, TINTA.copy(alpha = 0.75f), 8.5f, peso = FontWeight.SemiBold, minimo = 6f)
    }
}
