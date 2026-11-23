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

/** Los caminos de una pestaña, armados una vez por tamaño: la cédula con su pie rasgado y la punta del registro. */
private class FormasPestanaTemplario(val w: Float, val h: Float, u: Float) {
    val cedula: Path
    val punta: Path
    val cordon = Path()
    /** Dónde van el día y la letra de la semana. */
    val cajaDia = Rect(10f * u, 14f * u, w - 3f * u, h - 12f * u)
    val cajaSemana = Rect(w - 15f * u, 4f * u, w - 4f * u, 13f * u)

    init {
        val l = 4f * u; val r = w - 3f * u; val t = 3f * u; val b = h - 6f * u
        cedula = Path().apply {
            moveTo(l, t); lineTo(r, t); lineTo(r, b - 2f * u)
            var x = r; var j = 0
            while (x > l) { x -= 4f * u; lineTo(maxOf(x, l), b - (if (j % 2 == 0) 0f else 2f) * u); j++ }
            close()
        }
        // La punta en cola de golondrina del registro, abajo a la izquierda, fuera de la cédula.
        val rx = 5f * u; val rw = 8f * u; val rb = h - 1f * u
        punta = poligonoTemplario(rx, b - 4f * u, rx + rw, b - 4f * u, rx + rw, rb, rx + rw / 2, rb - 3f * u, rx, rb)
        // Sin ánimo: el cordón de lino que pasa por el agujero de arriba y cuelga con su nudo.
        cordon.moveTo(9f * u, 8f * u); cordon.quadraticTo(4f * u, 14f * u, 6f * u, 24f * u); cordon.lineTo(5f * u, h - 4f * u)
    }
}

/**
 * El índice de la libreta de Templario: cada día, una cédula de pergamino clavada con una tachuela de latón, como las que asoman
 * de la regla, con el día en la letra de la casa y la letra de la semana de rúbrica. Con ánimo, un registro de seda del color del
 * ánimo (el mismo cuero teñido que los volúmenes de la biblioteca) que baja por la izquierda y acaba en cola de golondrina; sin
 * ánimo no lleva registro sino un cordón de lino crudo por un agujero (la silueta lo dice, no solo el color). El rótulo de cada
 * mes, una cédula larga clavada en un listón de cedro.
 */
internal object IndiceLibretaTemplario {
    private val P = PaletaTemplario
    private var formas: FormasPestanaTemplario? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaTemplario =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaTemplario(w, h, u).also { formas = it }

    /** Los números de los días ya medidos (el medidor de la pantalla guarda solo 8 medidas): se vacía si cambia la escala. */
    private val numeros = HashMap<String, TextLayoutResult>()
    private var escala = 0f to 0f

    private fun DrawScope.numeroDia(medidor: TextMeasurer, dia: String, caja: Rect, tinta: Color) {
        if (escala.first != density || escala.second != fontScale) { numeros.clear(); escala = density to fontScale }
        val capa = numeros.getOrPut(dia) {
            medidor.measure(dia, TextStyle(fontFamily = P.LETRA, fontSize = 18.sp, lineHeight = (18f * 1.05f).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold),
                maxLines = 1, softWrap = false)
        }
        if (capa.size.width > caja.width || capa.size.height > caja.height) { textoEn(medidor, dia, caja, P.LETRA, tinta, 18f, peso = FontWeight.SemiBold, minimo = 10f); return }
        drawText(capa, color = tinta, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /** El pergamino del índice: en oscuro se hunde un poco hacia el añil, sin dejar de leerse. */
    private fun pergamino(oscuro: Boolean, s: Int): Color {
        val base = mezcla(P.PERGAMINO, P.PERG_OSC, 0.1f + 0.25f * azar(s, 2))
        return if (oscuro) mezcla(base, Color(0xFF2A2230), 0.28f) else base
    }

    private val SOMBRA = Color(0xFF2A2436).copy(alpha = 0.32f)
    private val TINTA = Color(0xFF26180E)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val s = e.semilla and 0x7fffffff
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else SOMBRA
        translate1(f.cedula, 1.2f * u, 1.6f * u, sombra)
        drawPath(f.cedula, pergamino(oscuro, s))
        drawRect(Color(0xFFB09A6E).copy(alpha = if (oscuro) 0.3f else 0.35f), Offset(4f * u, h - 14f * u), Size(w - 7f * u, 6f * u))
        drawPath(f.cedula, sombra.copy(alpha = 0.4f), style = Stroke(0.6f * u))
        val cuero = cueroTemplario(e.tono, oscuro)
        if (cuero != null) {
            // El registro de seda del color del ánimo, que baja por la izquierda y asoma por debajo.
            val col = if (oscuro) mezcla(cuero, Color.Black, 0.12f) else cuero
            drawRect(sombra, Offset(6.2f * u, 3f * u), Size(8f * u, h - 10f * u))
            drawRect(col, Offset(5f * u, 1.6f * u), Size(8f * u, h - 9f * u))
            drawPath(f.punta, col)
            drawRect(mezcla(col, Color.Black, 0.3f), Offset(5f * u, 1.6f * u), Size(8f * u, 3f * u))
            drawRect(mezcla(col, Color.White, 0.25f), Offset(6f * u, 5f * u), Size(1.2f * u, h - 14f * u))
        } else {
            // Sin ánimo: el agujero y el cordón de lino crudo con su nudo.
            drawCircle(sombra.copy(alpha = 0.6f), 1.6f * u, Offset(9f * u, 8f * u))
            drawPath(f.cordon, Color(0xFFCDB884), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            drawCircle(Color(0xFFCDB884), 1.8f * u, Offset(5.6f * u, 26f * u))
        }
        // La tachuela de latón de arriba.
        drawCircle(sombra, 2.2f * u, Offset(w / 2 + 2.6f * u, 6.4f * u))
        drawCircle(P.LATON, 2f * u, Offset(w / 2 + 2f * u, 5.6f * u))
        drawCircle(P.LATON_LUZ, 0.8f * u, Offset(w / 2 + 1.4f * u, 5f * u))
        textoEn(medidor, e.semana, f.cajaSemana, P.LETRA, if (oscuro) Color(0xFFB8402E) else P.CRUZ, 8.5f, peso = FontWeight.Bold)
        numeroDia(medidor, e.dia, f.cajaDia, TINTA)
    }

    private fun DrawScope.translate1(p: Path, dx: Float, dy: Float, c: Color) {
        translate(dx, dy) { drawPath(p, c) }
    }

    /** El rótulo de un mes: el listón de cedro con sus clavos y, en medio, la cédula larga de pergamino con el mes. */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val cedro = if (oscuro) mezcla(P.CEDRO, Color.Black, 0.35f) else P.CEDRO
        val list = Rect(4f * u, h * 0.5f - 6f * u, w - 4f * u, h * 0.5f + 6f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), list.topLeft + Offset(1.4f * u, 2f * u), list.size)
        drawRect(cedro, list.topLeft, list.size)
        drawRect(mezcla(cedro, Color.White, 0.2f), list.topLeft, Size(list.width, 1.4f * u))
        drawRect(mezcla(cedro, Color.Black, 0.35f), Offset(list.left, list.bottom - 2f * u), Size(list.width, 2f * u))
        var x = list.left + 10f * u
        while (x < list.right - 6f * u) { drawCircle(mezcla(P.HIERRO, cedro, 0.2f), 1.3f * u, Offset(x, list.center.y)); x += maxOf(1f, 36f * u) }
        val ancho = minOf(w - 30f * u, 330f * u)
        val c = Rect(w / 2 - ancho / 2, 3f * u, w / 2 + ancho / 2, h - 3f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), c.topLeft + Offset(1.4f * u, 1.8f * u), c.size)
        drawRect(pergamino(oscuro, 7), c.topLeft, c.size)
        drawRect(Color(0xFFB09A6E).copy(alpha = 0.35f), Offset(c.left, c.bottom - 5f * u), Size(c.width, 5f * u))
        drawRect(SOMBRA.copy(alpha = 0.3f), c.topLeft, c.size, style = Stroke(0.6f * u))
        listOf(c.left + 6f * u, c.right - 6f * u).forEach { cx ->
            drawCircle(P.LATON, 2f * u, Offset(cx, c.center.y)); drawCircle(P.LATON_LUZ, 0.8f * u, Offset(cx - 0.6f * u, c.center.y - 0.6f * u))
        }
        textoEn(medidor, texto, Rect(c.left + 14f * u, c.top + 2f * u, c.right - 14f * u, c.bottom - 2f * u), P.LETRA, TINTA, 13f, peso = FontWeight.SemiBold, minimo = 7f)
    }
}
