package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * El índice de la libreta de momentos de Helénico: cada día es una tablilla chica de las mismas del políptico (el marco de
 * boj, la cera oscura con el día y la letra de la semana incisos) atada con una vuelta de lana teñida del color del ánimo de
 * ese día (sin ánimo, un cordón de lana cruda, delgado); cada mes, una tablilla larga con el mes inciso y atada por los dos
 * cabos. Las pestañas son decenas: todo lo que no depende del día se arma una vez por tamaño y se comparte.
 */
internal object IndiceLibretaHeleno {
    private val P = PaletaLibretaHeleno
    private val LETRA: FontFamily = Letra.garamond
    /** Los tres boj de las tablillas: el claro, el de la tapa y uno más tostado. */
    private val BOJES = listOf(Color(0xFFD8BA80), Color(0xFFCCAA6C), Color(0xFFC09A5E))
    /** La penumbra del índice en modo oscuro: hondo, apenas frío. */
    private val PENUMBRA = Color(0xFF14111D)

    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.42f): Color = if (oscuro) mezcla(c, PENUMBRA, cuanto) else c

    // ---------------------------------------------------------------- lo compartido por todas las pestañas

    /** Los caminos de una pestaña de [w] × [h]: la veta, el bisel del marco y las vueltas de la lana. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val marco = Rect(w * 0.08f, h * 0.1f, w * 0.92f, h * 0.97f)
        val cera = Rect(marco.left + 3.4f * u, marco.top + 3.4f * u, marco.right - 3.4f * u, marco.top + marco.height * 0.68f)
        val lana = Rect(marco.left - 1.2f * u, cera.bottom + 2.6f * u, marco.right + 1.2f * u, (cera.bottom + 11f * u).coerceAtMost(marco.bottom - 1.6f * u))
        val veta = Path()
        val luz = Path()
        val sombra = Path()
        val vueltas = Path()
        val cordon = Path()

        init {
            for (j in 0 until 4) {
                val x = marco.left + marco.width * (0.12f + 0.25f * j + 0.06f * azar(781, j))
                veta.moveTo(x, marco.top); veta.cubicTo(x + 1.2f * u, marco.top + marco.height * 0.3f, x - 1.2f * u, marco.top + marco.height * 0.7f, x + 0.4f * u, marco.bottom)
            }
            val b = 1.3f * u
            luz.addRect(Rect(marco.left, marco.top, marco.right, marco.top + b)); luz.addRect(Rect(marco.left, marco.top, marco.left + b, marco.bottom))
            sombra.addRect(Rect(marco.left, marco.bottom - b, marco.right, marco.bottom)); sombra.addRect(Rect(marco.right - b, marco.top, marco.right, marco.bottom))
            // La cera hundida: su sombra arriba y a la izquierda.
            sombra.addRect(Rect(cera.left, cera.top, cera.right, cera.top + 1.6f * u)); sombra.addRect(Rect(cera.left, cera.top, cera.left + 1.2f * u, cera.bottom))
            var x = lana.left - lana.height
            while (x < lana.right) {
                vueltas.moveTo(x, lana.bottom); vueltas.lineTo(x + lana.height * 0.7f, lana.top)
                x += 2.2f * u
            }
            // El cordón de colgar: el agujero y la gaza que sale por arriba.
            cordon.moveTo(w * 0.5f, marco.top + 2.4f * u)
            cordon.cubicTo(w * 0.36f, -1f * u, w * 0.64f, -1f * u, w * 0.52f, marco.top + 2.2f * u)
        }
    }

    private var cachePestana: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cachePestana?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cachePestana = it }

    // ---------------------------------------------------------------- la pestaña de un día

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val m = g.marco
        val boj = t(BOJES[Math.floorMod(e.semilla, BOJES.size)], oscuro, 0.4f)
        // La sombra en la página, corrida abajo a la derecha.
        drawRect(sombraMueble(if (oscuro) 0.6f else 0.3f), Offset(m.left + 1.6f * u, m.top + 2f * u), m.size)
        drawRect(boj, m.topLeft, m.size)
        drawPath(g.veta, mezcla(boj, P.MADERA_OSC, 0.5f).copy(alpha = 0.3f), style = Stroke(0.6f * u))
        drawPath(g.luz, luzMueble(if (oscuro) 0.12f else 0.45f))
        drawPath(g.sombra, sombraMueble(0.4f))
        // La cera, con el día y la letra de la semana incisos (dejan ver la cera clara de abajo).
        val c = g.cera
        drawRect(t(P.CERA, oscuro, 0.2f), c.topLeft, Size(c.width, c.height))
        drawRect(sombraMueble(0.5f), c.topLeft, Size(c.width, 1.4f * u))
        val inciso = if (oscuro) P.INCISO else mezcla(P.INCISO, Color.White, 0.25f)
        textoEn(medidor, e.dia, Rect(c.left, c.top + 1.5f * u, c.right, c.top + c.height * 0.7f), LETRA, inciso, 19f, peso = FontWeight.Bold, minimo = 10f)
        textoEn(medidor, e.semana, Rect(c.left, c.top + c.height * 0.66f, c.right, c.bottom - 1f * u), LETRA, inciso.copy(alpha = 0.72f), 9f, minimo = 7f)
        // La vuelta de lana del color del ánimo; sin ánimo, el cordón de lana cruda.
        val l = g.lana
        val tono = e.tono
        if (tono != null) {
            val lana = t(tono, oscuro, 0.06f)
            drawRect(sombraMueble(0.35f), Offset(l.left + 1f * u, l.top + 1.2f * u), l.size)
            drawRect(lana, l.topLeft, l.size)
            clipRect(l.left, l.top, l.right, l.bottom) { drawPath(g.vueltas, sombraMueble(0.28f), style = Stroke(0.7f * u)) }
            drawRect(luzMueble(if (oscuro) 0.3f else 0.35f), l.topLeft, Size(l.width, 1.1f * u))
            // De noche, la orilla de la lana a la luz, para que el color no se pierda en la madera.
            if (oscuro) drawRect(luzMueble(0.22f), l.topLeft, l.size, style = Stroke(0.7f * u))
            // El cabo del nudo, que cuelga por la derecha.
            drawLine(lana, Offset(l.right - 3f * u, l.bottom), Offset(l.right - 1.5f * u + (azar(e.semilla, 5) - 0.5f) * 2f * u, minOf(h - 0.5f * u, l.bottom + 5f * u)), 1.6f * u, StrokeCap.Round)
        } else {
            val y = l.center.y
            val cruda = t(P.LANA, oscuro, 0.35f)
            drawLine(sombraMueble(0.3f), Offset(l.left, y + 1.2f * u), Offset(l.right, y + 1.2f * u), 2f * u)
            drawLine(cruda, Offset(l.left, y), Offset(l.right, y), 2f * u)
        }
        // El cordón de colgar.
        drawCircle(sombraMueble(0.6f), 1.5f * u, Offset(w * 0.5f, m.top + 2.4f * u))
        drawPath(g.cordon, t(mezcla(P.LANA, P.TIERRA, 0.3f), oscuro, 0.3f), style = Stroke(1.1f * u))
    }

    // ---------------------------------------------------------------- el rótulo de un mes

    /** El rótulo de un mes: una tablilla larga de las mismas, con el mes inciso en la cera y atada por los dos cabos. */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f || h < 4f) return
        val r = Rect(2f * u, 3f * u, w - 2f * u, h - 3f * u)
        val boj = t(BOJES[0], oscuro, 0.4f)
        drawRect(sombraMueble(if (oscuro) 0.6f else 0.28f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
        drawRect(boj, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (j in 0 until 3) {
                val y = r.top + r.height * (0.2f + 0.3f * j)
                drawLine(mezcla(boj, P.MADERA_OSC, 0.5f).copy(alpha = 0.25f), Offset(r.left, y), Offset(r.right, y + 1f * u), 0.6f * u)
            }
        }
        drawRect(luzMueble(if (oscuro) 0.12f else 0.45f), r.topLeft, Size(r.width, 1.3f * u))
        drawRect(sombraMueble(0.4f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        // Los dos cabos atados con su vuelta de lana: cruda con una hebra púrpura.
        val ata = (h * 0.32f).coerceAtLeast(6f * u)
        val cera = Rect(r.left + ata + 6f * u, r.top + 3.4f * u, r.right - ata - 6f * u, r.bottom - 3.4f * u)
        if (cera.width > 4f * u) {
            drawRect(t(P.CERA, oscuro, 0.2f), cera.topLeft, cera.size)
            drawRect(sombraMueble(0.5f), cera.topLeft, Size(cera.width, 1.4f * u))
        }
        for (x in listOf(r.left + 3f * u, r.right - 3f * u - ata)) {
            drawRect(t(P.LANA, oscuro, 0.35f), Offset(x, r.top - 1f * u), Size(ata, r.height + 2f * u))
            drawRect(t(P.PURPURA, oscuro, 0.2f), Offset(x + ata * 0.4f, r.top - 1f * u), Size(ata * 0.2f, r.height + 2f * u))
            var y = r.top - 1f * u
            val vueltas = Path()
            while (y < r.bottom + 1f * u) { vueltas.moveTo(x, y + 2f * u); vueltas.lineTo(x + ata, y); y += 2.2f * u }
            drawPath(vueltas, sombraMueble(0.25f), style = Stroke(0.6f * u))
        }
        val inciso = if (oscuro) P.INCISO else mezcla(P.INCISO, Color.White, 0.25f)
        textoEn(medidor, texto, Rect(cera.left + 6f * u, 0f, cera.right - 6f * u, h), LETRA, inciso, 15f, peso = FontWeight.SemiBold, espaciado = 0.5f, minimo = 8f)
    }
}
