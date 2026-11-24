package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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

/** Los caminos de una pestaña, armados una vez por tamaño: el tiento, las vueltas de lana con sus surcos, el nudo sin lana. */
private class FormasPestanaGalia(val w: Float, val h: Float, u: Float) {
    val tabla = Rect(4f * u, 12f * u, w - 4f * u, h - 3f * u)
    val cera = tabla.deflate(3.6f * u)
    val agujero = Offset(w / 2, tabla.top + 1.9f * u)
    val tiento = Path().apply {
        moveTo(agujero.x - 1.2f * u, agujero.y); quadraticTo(w / 2 - 7f * u, 4f * u, w / 2, 1.2f * u)
        quadraticTo(w / 2 + 7f * u, 4f * u, agujero.x + 1.2f * u, agujero.y)
    }
    /** La lana teñida (con ánimo): unas vueltas apretadas que juntan los dos cabos del tiento sobre la tablilla, con sus surcos. */
    val vueltas = androidx.compose.ui.geometry.RoundRect(w / 2 - 8f * u, 4.6f * u, w / 2 + 8f * u, 10.4f * u, 2.6f * u, 2.6f * u)
    val surcos = Path().apply {
        for (j in 0 until 4) { val x = w / 2 - 5.4f * u + j * 3.6f * u; moveTo(x, 5f * u); lineTo(x + 1.6f * u, 10f * u) }
    }
    /** Sin ánimo: solo el nudo del tiento crudo, chico, sin lana. */
    val nudo = Rect(w / 2 - 2.4f * u, 0.4f * u, w / 2 + 2.4f * u, 3.6f * u)
    /** Unos rayones de lo alisado en la cera. */
    val rayas = Path().apply {
        for (j in 0 until 3) {
            val y = cera.top + cera.height * (0.2f + 0.3f * j)
            moveTo(cera.left + 1.5f * u, y); lineTo(cera.left + cera.width * (0.3f + 0.4f * azar(9400, j)), y + 0.4f * u)
        }
    }
    val cajaDia = Rect(cera.left, cera.top + 6f * u, cera.right, cera.bottom - 1f * u)
    val cajaSemana = Rect(cera.right - 11f * u, cera.top + 0.2f * u, cera.right - 0.5f * u, cera.top + 8f * u)
}

/**
 * El índice de la libreta de Galia: cada día, una tablillita de haya encerada como las del haz, colgada de su tiento de cuero
 * crudo, con el día rayado en la cera y la letra de la semana en el rincón. Con ánimo, unas vueltas de lana teñida del color
 * del ánimo (los mismos tintes que la lana y el mimbre de la biblioteca) que juntan los dos cabos del tiento; sin ánimo, el
 * tiento abierto con su nudito crudo (la silueta lo dice: sin el bulto de la lana). El rótulo de cada mes, una tablilla larga atada con
 * tientos a un listón de roble.
 */
internal object IndiceLibretaGalia {
    private val P = PaletaGalia
    private val CERA = Color(0xFFC9A257)
    private val SURCO = Color(0xFF3A2812)
    private val LABIO = Color(0xFFF0DCA8)
    private val TIENTO = mezcla(P.CUERO, P.CUERNO, 0.38f)
    private val SOMBRA = Color(0xFF2E1E16)

    private var formas: FormasPestanaGalia? = null
    private fun formas(w: Float, h: Float, u: Float): FormasPestanaGalia =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaGalia(w, h, u).also { formas = it }

    /** Los números de los días y los rótulos ya medidos (el medidor de la pantalla guarda solo 8 medidas). */
    private val medidas = HashMap<String, TextLayoutResult>()
    private var escala = 0f to 0f

    private fun DrawScope.medida(medidor: TextMeasurer, texto: String, tam: Float, peso: FontWeight, rotulo: Boolean): TextLayoutResult {
        if (escala.first != density || escala.second != fontScale) { medidas.clear(); escala = density to fontScale }
        if (medidas.size > 120) medidas.clear()
        return medidas.getOrPut(texto + if (rotulo) "|r" else "") {
            medidor.measure(texto, TextStyle(fontFamily = if (rotulo) P.LETRA else P.TITULO, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, textAlign = TextAlign.Center, fontWeight = peso),
                maxLines = 1, softWrap = false)
        }
    }

    /** La haya del marco y la cera, de día o a la luz de la lámpara (de noche, ámbar, con la tinta clara encima). */
    private fun haya(oscuro: Boolean) = if (oscuro) Color(0xFF6E5034) else P.HAYA
    private fun cera(oscuro: Boolean) = if (oscuro) Color(0xFF5A3E1C) else CERA
    private fun tinta(oscuro: Boolean) = if (oscuro) LABIO else SURCO

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val s = e.semilla and 0x7fffffff
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.55f) else SOMBRA.copy(alpha = 0.3f)
        val lana = tonoDeDiaGalia(e.tono, oscuro)?.let { if (oscuro) mezcla(it, Color(0xFF1D150B), 0.28f) else it }
        rotate(-2.5f + 5f * azar(s, 1), Offset(w / 2, 1.2f * u)) {
            val marco = haya(oscuro)
            translate(1.2f * u, 1.6f * u) { drawRoundRect(sombra, f.tabla.topLeft, f.tabla.size, CornerRadius(1.6f * u)) }
            drawRoundRect(mezcla(marco, Color.Black, 0.06f * azar(s, 2)), f.tabla.topLeft, f.tabla.size, CornerRadius(1.6f * u))
            drawRect((if (oscuro) P.LLAMA.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.35f)), f.tabla.topLeft, Size(f.tabla.width, 1f * u))
            drawRect(sombra.copy(alpha = 0.45f), Offset(f.tabla.left, f.tabla.bottom - 1.2f * u), Size(f.tabla.width, 1.2f * u))
            drawRect(cera(oscuro), f.cera.topLeft, f.cera.size)
            drawRect(sombra.copy(alpha = 0.35f), f.cera.topLeft, Size(1.6f * u, f.cera.height))
            drawRect(sombra.copy(alpha = 0.25f), f.cera.topLeft, Size(f.cera.width, 1.2f * u))
            drawPath(f.rayas, SURCO.copy(alpha = if (oscuro) 0.3f else 0.14f), style = Stroke(0.6f * u))
            drawCircle(sombra.copy(alpha = 0.8f), 1.3f * u, f.agujero)
            // El tiento de cuero crudo y, con ánimo, el mechón de lana teñida con sus hebras; sin ánimo, el nudito crudo.
            val cuero = if (oscuro) mezcla(TIENTO, Color(0xFF1D150B), 0.45f) else TIENTO
            drawPath(f.tiento, cuero, style = Stroke(1.3f * u, cap = StrokeCap.Round))
            if (lana != null) {
                val v = f.vueltas
                drawRoundRect(sombra, Offset(v.left + 0.6f * u, v.top + 0.9f * u), Size(v.width, v.height), CornerRadius(2.6f * u))
                drawRoundRect(lana, Offset(v.left, v.top), Size(v.width, v.height), CornerRadius(2.6f * u))
                drawPath(f.surcos, mezcla(lana, Color.Black, 0.35f), style = Stroke(0.7f * u))
                drawRect(Color.White.copy(alpha = if (oscuro) 0.16f else 0.32f), Offset(v.left + 2f * u, v.top + 0.8f * u), Size(v.width - 4f * u, 1f * u))
            } else {
                drawOval(cuero, f.nudo.topLeft, f.nudo.size)
                drawOval(sombra.copy(alpha = 0.5f), f.nudo.topLeft, f.nudo.size, style = Stroke(0.5f * u))
            }
            val sem = medida(medidor, e.semana, 7.5f, FontWeight.Bold, true)
            drawText(sem, color = if (oscuro) P.LLAMA else Color(0xFF7E590C), topLeft = Offset(f.cajaSemana.center.x - sem.size.width / 2f, f.cajaSemana.center.y - sem.size.height / 2f))
            val dia = medida(medidor, e.dia, 17f, FontWeight.ExtraBold, false)
            if (dia.size.width > f.cajaDia.width || dia.size.height > f.cajaDia.height) textoEn(medidor, e.dia, f.cajaDia, P.TITULO, tinta(oscuro), 17f, peso = FontWeight.ExtraBold, minimo = 9f)
            else drawText(dia, color = tinta(oscuro), topLeft = Offset(f.cajaDia.center.x - dia.size.width / 2f, f.cajaDia.center.y - dia.size.height / 2f))
        }
    }

    /** El rótulo de un mes: el listón de roble con sus clavos y, en medio, la tablilla larga atada con dos tientos. */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.5f) else SOMBRA.copy(alpha = 0.28f)
        val roble = if (oscuro) mezcla(P.ROBLE, Color(0xFF1D150B), 0.5f) else P.ROBLE
        val liston = Rect(4f * u, h * 0.5f - 5f * u, w - 4f * u, h * 0.5f + 5f * u)
        drawRect(sombra, liston.topLeft + Offset(1.2f * u, 1.8f * u), liston.size)
        drawRect(roble, liston.topLeft, liston.size)
        drawRect(mezcla(roble, Color.White, 0.2f), liston.topLeft, Size(liston.width, 1.2f * u))
        drawRect(mezcla(roble, Color.Black, 0.35f), Offset(liston.left, liston.bottom - 1.6f * u), Size(liston.width, 1.6f * u))
        listOf(liston.left + 7f * u, liston.right - 7f * u).forEach { x ->
            drawCircle(sombra, 1.9f * u, Offset(x + 0.5f * u, liston.center.y + 0.6f * u))
            drawCircle(mezcla(P.HIERRO, roble, 0.15f), 1.7f * u, Offset(x, liston.center.y))
            drawCircle(Color.White.copy(alpha = 0.3f), 0.6f * u, Offset(x - 0.5f * u, liston.center.y - 0.5f * u))
        }
        val capa = medida(medidor, texto, 12.5f, FontWeight.SemiBold, true)
        val ancho = (capa.size.width + 34f * u).coerceAtMost(w - 40f * u)
        val t = Rect(w / 2 - ancho / 2, 2f * u, w / 2 + ancho / 2, h - 3f * u)
        drawRoundRect(sombra, t.topLeft + Offset(1.2f * u, 1.6f * u), t.size, CornerRadius(1.6f * u))
        drawRoundRect(haya(oscuro), t.topLeft, t.size, CornerRadius(1.6f * u))
        drawRect(if (oscuro) P.LLAMA.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.35f), t.topLeft, Size(t.width, 1f * u))
        val c = t.deflate(3.4f * u)
        drawRect(cera(oscuro), c.topLeft, c.size)
        drawRect(sombra.copy(alpha = 0.35f), c.topLeft, Size(1.6f * u, c.height))
        drawRect(sombra.copy(alpha = 0.25f), c.topLeft, Size(c.width, 1.2f * u))
        // Los dos tientos que atan la tablilla al listón, por fuera de la cera.
        val cuero = if (oscuro) mezcla(TIENTO, Color(0xFF1D150B), 0.45f) else TIENTO
        listOf(t.left + 1.8f * u, t.right - 1.8f * u).forEach { x ->
            drawLine(sombra, Offset(x + 0.6f * u, t.top - 0.4f * u), Offset(x + 0.6f * u, t.bottom + 1.4f * u), 2f * u)
            drawLine(cuero, Offset(x, t.top - 1f * u), Offset(x, t.bottom + 1f * u), 1.8f * u, StrokeCap.Round)
        }
        if (capa.size.width <= c.width - 8f * u) {
            drawText(capa, color = tinta(oscuro), topLeft = Offset(c.center.x - capa.size.width / 2f, c.center.y - capa.size.height / 2f))
        } else {
            textoEn(medidor, texto, Rect(c.left + 4f * u, c.top, c.right - 4f * u, c.bottom), P.LETRA, tinta(oscuro), 12.5f, peso = FontWeight.SemiBold, minimo = 7f)
        }
    }
}
