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
    private val BOJES = listOf(Color(0xFFD8BA80), Color(0xFFD0B074), Color(0xFFC8A66A))
    /** La penumbra del índice en modo oscuro: hondo, apenas frío. */
    private val PENUMBRA = Color(0xFF14111D)
    /** La cera de la rendija, pardo verdosa (la misma de la portada). */
    private val CERA_VERDE = Color(0xFF33301F)

    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.42f): Color = if (oscuro) mezcla(c, PENUMBRA, cuanto) else c

    // ---------------------------------------------------------------- lo compartido por todas las pestañas

    /**
     * Los caminos de una pestaña de [w] × [h], que comparten todas: tres vetas distintas (una por semilla), el canto a la luz y
     * en sombra, la ranura tallada, la rendija de cera, las vueltas de la lana y dos formas del cordón de colgar.
     */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val marco = Rect(w * 0.08f, h * 0.1f, w * 0.92f, h * 0.97f)
        /** La rendija de la derecha, donde la tablilla entreabierta deja ver la cera de la de dentro. */
        val rendija = Rect(marco.right - 4.6f * u, marco.top + 2.4f * u, marco.right - 1.3f * u, marco.bottom - 2.4f * u)
        val campo = Rect(marco.left + 2.4f * u, marco.top + 2.6f * u, rendija.left - 1.4f * u, marco.top + marco.height * 0.7f)
        val lana = Rect(marco.left - 1.2f * u, campo.bottom + 2.4f * u, marco.right + 1.2f * u, (campo.bottom + 11.4f * u).coerceAtMost(marco.bottom - 1.2f * u))
        val vetas = List(3) { Path() }
        val luz = Path()
        val sombra = Path()
        val ranura = Path()
        val vueltas = Path()
        val cordones = List(2) { Path() }

        init {
            for (v in 0 until 3) for (j in 0 until 4) {
                val x = marco.left + marco.width * (0.1f + 0.22f * j + 0.08f * azar(781 + v, j))
                val ond = (azar(784 + v, j) - 0.5f) * 3f * u
                vetas[v].moveTo(x, marco.top); vetas[v].cubicTo(x + ond, marco.top + marco.height * 0.3f, x - ond, marco.top + marco.height * 0.7f, x + ond * 0.4f, marco.bottom)
            }
            val b = 1.2f * u
            luz.addRect(Rect(marco.left, marco.top, marco.right, marco.top + b)); luz.addRect(Rect(marco.left, marco.top, marco.left + b, marco.bottom))
            sombra.addRect(Rect(marco.left, marco.bottom - b, marco.right, marco.bottom)); sombra.addRect(Rect(marco.right - b, marco.top, marco.right, marco.bottom))
            ranura.addRect(campo)
            var x = lana.left - lana.height
            while (x < lana.right) {
                vueltas.moveTo(x, lana.bottom - 0.6f * u); vueltas.lineTo(x + lana.height * 0.7f, lana.top + 0.6f * u)
                x += 2.2f * u
            }
            // El cordón de colgar: el agujero y la gaza que sale por arriba, en dos formas.
            cordones[0].moveTo(w * 0.5f, marco.top + 2.4f * u); cordones[0].cubicTo(w * 0.36f, -1f * u, w * 0.64f, -1f * u, w * 0.52f, marco.top + 2.2f * u)
            cordones[1].moveTo(w * 0.5f, marco.top + 2.4f * u); cordones[1].cubicTo(w * 0.62f, 0f, w * 0.3f, 0.5f * u, w * 0.47f, marco.top + 2.2f * u)
        }
    }

    private var cachePestana: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cachePestana?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cachePestana = it }

    // ---------------------------------------------------------------- la pestaña de un día

    /**
     * La pestaña de un día: una tablilla chica de boj, entreabierta como el políptico (por la rendija de la derecha asoma la
     * cera pardo verdosa con su brillo), con el día y la letra de la semana a tinta en el campo tallado, la vuelta de lana del
     * color del ánimo (con la luz de sus hebras) y el cordón de colgar. Cambian con la semilla el boj, la veta, el nudo y la
     * gaza del cordón. Sin ánimo, un cordón delgado de lana cruda.
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val m = g.marco
        val s = e.semilla and 0x7fffffff
        val boj = t(BOJES[s % BOJES.size], oscuro, 0.22f)
        // La sombra en la página, corrida abajo a la derecha.
        drawRect(sombraMueble(if (oscuro) 0.6f else 0.28f), Offset(m.left + 1.6f * u, m.top + 2f * u), m.size)
        drawRect(boj, m.topLeft, m.size)
        drawPath(g.vetas[(s / 3) % 3], mezcla(boj, P.MADERA_OSC, 0.5f).copy(alpha = 0.32f), style = Stroke(0.6f * u))
        // Un nudo de la madera, en un sitio distinto en cada una.
        val nudo = Offset(m.left + m.width * (0.15f + 0.6f * azar(s, 7)), m.top + m.height * (0.15f + 0.7f * azar(s, 8)))
        drawOval(mezcla(boj, P.MADERA_OSC, 0.45f).copy(alpha = 0.4f), Offset(nudo.x - 1.6f * u, nudo.y - 1f * u), Size(3.2f * u, 2f * u))
        drawPath(g.luz, luzMueble(if (oscuro) 0.14f else 0.5f))
        drawPath(g.sombra, sombraMueble(0.38f))
        // La rendija: la cera de la tablilla de dentro, con su brillo rasante.
        val r = g.rendija
        drawRect(t(CERA_VERDE, oscuro, 0.12f), r.topLeft, r.size)
        drawLine(mezcla(P.INCISO, CERA_VERDE, 0.4f).copy(alpha = 0.6f), Offset(r.left + 1f * u, r.top + r.height * 0.55f), Offset(r.left + 1f * u, r.bottom - 2f * u), 0.7f * u)
        // La ranura tallada del campo, con el día y la letra de la semana a tinta.
        drawPath(g.ranura, sombraMueble(0.32f), style = Stroke(0.8f * u))
        val c = g.campo
        val tinta = if (oscuro) mezcla(P.TINTA, PENUMBRA, 0.1f) else P.TINTA
        textoEn(medidor, e.dia, Rect(c.left, c.top + 1.2f * u, c.right, c.top + c.height * 0.7f), LETRA, tinta, 19f, peso = FontWeight.Bold, minimo = 10f)
        textoEn(medidor, e.semana, Rect(c.left, c.top + c.height * 0.66f, c.right, c.bottom - 0.6f * u), LETRA, tinta.copy(alpha = 0.72f), 9f, minimo = 7f)
        // La vuelta de lana del color del ánimo, con la luz de sus hebras; sin ánimo, el cordón de lana cruda.
        val l = g.lana
        val tono = e.tono
        if (tono != null) {
            val lana = t(tono, oscuro, 0.04f)
            drawRect(sombraMueble(0.35f), Offset(l.left + 1f * u, l.top + 1.2f * u), l.size)
            drawRect(lana, l.topLeft, l.size)
            // Las hebras: más claras que la lana (de noche se ven igual: el azul marino y el morado no se pierden en la madera).
            clipRect(l.left, l.top, l.right, l.bottom) { drawPath(g.vueltas, mezcla(lana, Color.White, if (oscuro) 0.45f else 0.3f).copy(alpha = 0.7f), style = Stroke(0.7f * u)) }
            drawRect(sombraMueble(0.3f), Offset(l.left, l.bottom - 1f * u), Size(l.width, 1f * u))
            // El cabo del nudo, que cuelga por la derecha.
            drawLine(lana, Offset(l.right - 3f * u, l.bottom), Offset(l.right - 1.5f * u + (azar(s, 5) - 0.5f) * 2f * u, minOf(h - 0.5f * u, l.bottom + 5f * u)), 1.6f * u, StrokeCap.Round)
        } else {
            val y = l.center.y
            val cruda = t(P.LANA, oscuro, 0.3f)
            drawLine(sombraMueble(0.3f), Offset(l.left, y + 1.2f * u), Offset(l.right, y + 1.2f * u), 2f * u)
            drawLine(cruda, Offset(l.left, y), Offset(l.right, y), 2f * u)
        }
        // El cordón de colgar.
        drawCircle(sombraMueble(0.6f), 1.5f * u, Offset(w * 0.5f, m.top + 2.4f * u))
        drawPath(g.cordones[s % 2], t(mezcla(P.LANA, P.TIERRA, 0.3f), oscuro, 0.3f), style = Stroke(1.1f * u))
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
            drawRect(t(CERA_VERDE, oscuro, 0.15f), cera.topLeft, cera.size)
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
