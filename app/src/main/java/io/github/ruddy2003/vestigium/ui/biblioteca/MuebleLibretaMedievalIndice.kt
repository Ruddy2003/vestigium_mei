package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Los caminos de una pestaña del índice, armados una vez por tamaño y compartidos por todas: tres cortes de la hoja de
 * pergamino, tres de la lengüeta de cuero (redonda, ochavada y de medio punto), la costura y la regla roja.
 */
private class FormasPestanaMedieval(val w: Float, val h: Float, u: Float) {
    val hojas: List<Path>
    val lenguetas: List<Path>
    val costura = Path()
    val regla = Path()
    /** Dónde van la letra de la semana (en el cuero) y el día (en el pergamino). */
    val cajaSemana = Rect(4f * u, 2.5f * u, w - 4f * u, 15f * u)
    val cajaDia = Rect(3f * u, 20f * u, w - 3f * u, h - 6f * u)
    /** Sin ánimo no hay lengüeta: la letra va en la orilla de arriba de la hoja. */
    val cajaSemanaSola = Rect(4f * u, 11.5f * u, w - 4f * u, 20.5f * u)

    init {
        val l = 3f * u; val r = w - 3f * u; val t = 11f * u; val b = h - 2.5f * u
        hojas = listOf(
            poligonoMedieval(l, t, r, t, r - 0.4f * u, b, l + 0.6f * u, b - 0.5f * u),
            poligonoMedieval(l + 0.5f * u, t, r, t + 0.4f * u, r, b - 3f * u, r - 3f * u, b, l, b),
            poligonoMedieval(l, t + 0.5f * u, r - 0.5f * u, t, r, b - 0.6f * u, l + 4f * u, b, l, b - 2f * u),
        )
        val lt = 1.5f * u; val lb = 18f * u; val ll = 4f * u; val lr = w - 4f * u
        lenguetas = listOf(
            Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(ll, lt, lr, lb, 6f * u, 6f * u)) },
            poligonoMedieval(ll + 3.5f * u, lt, lr - 3.5f * u, lt, lr, lt + 3.5f * u, lr, lb, ll, lb, ll, lt + 3.5f * u),
            Path().apply {
                val rad = (lr - ll) / 2
                moveTo(ll, lb); lineTo(ll, lt + minOf(rad, 9f * u))
                cubicTo(ll, lt - 1f * u, lr, lt - 1f * u, lr, lt + minOf(rad, 9f * u)); lineTo(lr, lb); close()
            },
        )
        var x = ll + 3f * u
        while (x < lr - 3f * u) { costura.moveTo(x, lb - 3f * u); costura.lineTo(x + 1.6f * u, lb + 1.2f * u); x += 4.2f * u }
        regla.moveTo(l + 5f * u, b - 4.5f * u); regla.lineTo(r - 5f * u, b - 4.5f * u)
    }
}

/**
 * El índice de la libreta de Scriptorium: la pestaña de cada día y el rótulo de cada mes, del mismo pergamino y el mismo cuero
 * que el códice de momentos ([MuebleLibretaMedieval]).
 */
internal object IndiceLibretaMedieval {
    private val P = PaletaMedieval
    private var formas: FormasPestanaMedieval? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaMedieval =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaMedieval(w, h, u).also { formas = it }

    /** El pergamino de las pestañas y del rótulo: en oscuro se hunde un poco, sin dejar de leerse. */
    private fun pergamino(oscuro: Boolean, sem: Int): Color {
        val base = mezcla(P.PERGAMINO, P.PERG_OSC, 0.12f + 0.2f * azar(sem, 2))
        return if (oscuro) mezcla(base, P.NOCHE, 0.22f) else base
    }

    /**
     * La pestaña de un día: una hoja de pergamino con la lengüeta de cuero cosida arriba (la de los registros de los códices).
     * El cuero va teñido del color del ánimo, de lado a lado, con la letra de la semana; sin ánimo no lleva lengüeta (la silueta
     * ya lo dice, sin depender del color) y la letra va de rúbrica en la orilla de la hoja. En el pergamino, el día en letra
     * gótica sobre su regla roja. Cada una se ladea un poco, con uno de tres cortes de hoja y tres de lengüeta.
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val s = e.semilla and 0x7fffffff
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.45f) else SOMBRA_INDICE
        rotate(-2.2f + 4.4f * azar(s, 1), Offset(w / 2, h / 2)) {
            val hoja = f.hojas[s % 3]
            translate1(hoja, 1.2f * u, 1.6f * u) { drawPath(it, sombra) }
            drawPath(hoja, pergamino(oscuro, s))
            drawPath(f.regla, P.RUBRICA.copy(alpha = if (oscuro) 0.5f else 0.42f), style = Stroke(0.6f * u))
            drawPath(hoja, sombra.copy(alpha = 0.35f), style = Stroke(0.6f * u))
            val tono = e.tono
            if (tono != null) {
                // Con ánimo: la lengüeta de cuero teñida, cosida, con la letra de la semana.
                val cuero = if (oscuro) mezcla(tono, P.PERGAMINO, 0.06f) else mezcla(tono, P.TINTE_HONDO, 0.08f)
                val leng = f.lenguetas[(s / 3) % 3]
                translate1(leng, 0.8f * u, 1f * u) { drawPath(it, sombra.copy(alpha = sombra.alpha * 0.7f)) }
                drawPath(leng, cuero)
                drawPath(f.costura, (if (cuero.luminance() > 0.4f) P.CUERO else P.BADANA).copy(alpha = 0.7f), style = Stroke(0.7f * u))
                // Dos tintas fijas (no una por pestaña), para que el medidor de texto reutilice lo ya medido.
                val letra = if (cuero.luminance() > 0.42f) P.TINTA else P.PERGAMINO
                textoEn(medidor, e.semana, f.cajaSemana, P.LETRA, letra, 9.5f, peso = FontWeight.Bold)
            } else {
                // Sin ánimo: sin lengüeta; la letra de la semana, de rúbrica, en la orilla de la hoja.
                textoEn(medidor, e.semana, f.cajaSemanaSola, P.LETRA, P.RUBRICA.copy(alpha = if (oscuro) 0.85f else 0.9f), 8.5f, peso = FontWeight.Bold)
            }
            textoEn(medidor, e.dia, f.cajaDia, LetraExtra.gotica, P.TINTA, 19f, minimo = 10f)
        }
    }

    /** Dibuja [p] corrido ([dx], [dy]) sin armar otro camino. */
    private inline fun DrawScope.translate1(p: Path, dx: Float, dy: Float, bloque: DrawScope.(Path) -> Unit) {
        drawContext.transform.translate(dx, dy)
        bloque(p)
        drawContext.transform.translate(-dx, -dy)
    }

    /** La sombra violeta del índice en claro (la de los muebles, sin negro). */
    private val SOMBRA_INDICE = Color(0xFF2A2436).copy(alpha = 0.3f)

    /**
     * El rótulo de un mes: una tira de pergamino de orillas desiguales, con su doble regla roja y los picados de la pauta, el mes
     * en letra gótica de rúbrica y, en cada punta, una lengüeta de badana cosida, como las de los días.
     */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val tira = Rect(6f * u, 4f * u, w - 6f * u, h - 4f * u)
        val sombra = if (oscuro) Color.Black.copy(alpha = 0.45f) else SOMBRA_INDICE
        val contorno = contornoIrregular(tira, 7, 40, 0.035f)
        translate1(contorno, 1.2f * u, 1.6f * u) { drawPath(it, sombra) }
        drawPath(contorno, pergamino(oscuro, 7))
        val reglas = Path()
        for (y in listOf(tira.top + 4.5f * u, tira.bottom - 4.5f * u)) { reglas.moveTo(tira.left + 22f * u, y); reglas.lineTo(tira.right - 22f * u, y) }
        var x = tira.left + 26f * u
        while (x < tira.right - 24f * u) { reglas.addOval(Rect(x - 0.5f * u, tira.bottom - 2.5f * u, x + 0.5f * u, tira.bottom - 1.5f * u)); x += 9f * u }
        drawPath(reglas, P.RUBRICA.copy(alpha = if (oscuro) 0.6f else 0.5f), style = Stroke(0.6f * u))
        // Las dos lengüetas de badana cosidas en las puntas.
        val badana = if (oscuro) mezcla(P.BADANA, P.NOCHE, 0.25f) else P.BADANA
        for (izq in listOf(true, false)) {
            val l = if (izq) Rect(tira.left - 2f * u, tira.top - 2f * u, tira.left + 14f * u, tira.bottom + 2f * u)
                else Rect(tira.right - 14f * u, tira.top - 2f * u, tira.right + 2f * u, tira.bottom + 2f * u)
            drawRoundRect(sombra.copy(alpha = sombra.alpha * 0.8f), l.topLeft + Offset(1f * u, 1.4f * u), l.size, androidx.compose.ui.geometry.CornerRadius(4f * u))
            drawRoundRect(badana, l.topLeft, l.size, androidx.compose.ui.geometry.CornerRadius(4f * u))
            val xc = if (izq) l.right - 3.5f * u else l.left + 3.5f * u
            drawLine(P.CUERO.copy(alpha = 0.6f), Offset(xc, l.top + 4f * u), Offset(xc, l.bottom - 4f * u), 0.7f * u)
            drawCircle(P.RUBRICA.copy(alpha = 0.8f), 1.6f * u, Offset(if (izq) l.left + 6f * u else l.right - 6f * u, l.center.y))
        }
        val tinta = if (oscuro) mezcla(P.RUBRICA, P.PERGAMINO, 0.08f) else P.RUBRICA
        textoEn(medidor, texto, Rect(tira.left + 26f * u, tira.top + 5f * u, tira.right - 26f * u, tira.bottom - 4f * u), LetraExtra.gotica, tinta, 16f, minimo = 9f)
    }
}
