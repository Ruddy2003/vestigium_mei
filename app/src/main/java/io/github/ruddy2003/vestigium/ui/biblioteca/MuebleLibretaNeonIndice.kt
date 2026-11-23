package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El índice de la libreta de momentos de Ciudad neón: cada día es un ticket térmico chico, de los mismos del pincho (el papel
 * blanco o una copia rosa o amarilla, el agujero de la púa arriba, el borde de abajo rasgado en dientes), con el día y la letra
 * de la semana impresos y una pegatina redonda del color del ánimo de ese día; sin ánimo, una grapa (otra forma, no otro
 * color). El tema es solo de noche: el mismo con [oscuro] o sin él. Todo lo que no depende del día se arma una vez por tamaño.
 */
internal object IndiceLibretaNeon {
    private val P = PaletaLibretaNeon
    /** El papel del índice: el del pincho, apenas hundido en la penumbra de la página. */
    private val PAPELES = listOf(P.PAPEL, P.COPIAS[1], P.PAPEL, P.COPIAS[3]).map { mezcla(it, PaletaNeon.HONDO, 0.12f) }
    private val TINTA = P.IMPRESO

    /** Los caminos de una pestaña de [w] × [h], que comparten todas: el papel con sus dientes, su sombra y el filete punteado. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val r = Rect(4f * u, 3f * u, w - 4f * u, h - 4f * u)
        val papel = Path()
        val sombra = Path()
        val filete = Path()
        val esquina = Path()
        val semana = Rect(r.left + 2f * u, r.top + 1f * u, r.left + 11f * u, r.top + 10f * u)
        val dia = Rect(r.left + 1f * u, r.top + 12f * u, r.right - 1f * u, r.top + 31f * u)
        val sello = Offset(w * 0.5f, r.top + 39.5f * u)

        init {
            papel.moveTo(r.left, r.top); papel.lineTo(r.right, r.top); papel.lineTo(r.right, r.bottom - 3f * u)
            var x = r.right; var abajo = true
            while (x > r.left) { x -= 4f * u; papel.lineTo(maxOf(x, r.left), if (abajo) r.bottom else r.bottom - 3f * u); abajo = !abajo }
            papel.close()
            sombra.addPath(papel, Offset(1.6f * u, 2f * u))
            var fx = r.left + 3f * u
            while (fx < r.right - 3f * u) { filete.moveTo(fx, r.top + 11f * u); filete.lineTo(minOf(fx + 1.6f * u, r.right - 3f * u), r.top + 11f * u); fx += 2.8f * u }
            // La esquina de arriba a la derecha, doblada (en algunas).
            esquina.moveTo(r.right - 6f * u, r.top); esquina.lineTo(r.right, r.top + 6f * u); esquina.lineTo(r.right - 6f * u, r.top + 6f * u); esquina.close()
        }
    }

    private var cache: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cache?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cache = it }

    /**
     * Los textos ya medidos (los números del 1 al 31 y las letras de la semana): se miden una vez con [capaEn] y se guardan; se
     * vacían si cambian la densidad, la escala de la letra o el tamaño de la pestaña.
     */
    private val capas = HashMap<String, TextLayoutResult>()
    private val clave = FloatArray(4)

    private fun DrawScope.impreso(medidor: TextMeasurer, texto: String, caja: Rect, tam: Float, peso: FontWeight, alfa: Float) {
        val k = clave
        if (k[0] != density || k[1] != fontScale || k[2] != size.width || k[3] != size.height) {
            capas.clear(); k[0] = density; k[1] = fontScale; k[2] = size.width; k[3] = size.height
        }
        val llave = "$tam|$texto"
        val capa = capas[llave] ?: (capaEn(medidor, texto, caja, if (tam > 12f) Letra.letrero else Letra.terminal, TINTA.copy(alpha = alfa), tam, peso = peso, minimo = 5f)
            ?: return).also { capas[llave] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /**
     * La pestaña de un día: el ticket, un poco girado (según la semilla), con su sombra, el agujero de la púa, la letra de la
     * semana y el filete punteado, el día grande, y la pegatina del ánimo (con su brillo y su canto) o, sin ánimo, la grapa.
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val s = e.semilla and 0x7fffffff
        rotate(-3.5f + 7f * azar(s, 1), Offset(w / 2, h / 2)) {
            drawPath(g.sombra, PaletaNeon.SOMBRA.copy(alpha = 0.55f))
            val papel = PAPELES[s % PAPELES.size]
            drawPath(g.papel, papel)
            drawRect(Color.White.copy(alpha = 0.22f), g.r.topLeft, Size(g.r.width, 1f * u))
            if (azar(s, 2) > 0.6f) {
                drawPath(g.esquina, mezcla(papel, PaletaNeon.SOMBRA, 0.3f))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(g.r.right - 6f * u, g.r.top), Offset(g.r.right - 6f * u, g.r.top + 6f * u), 0.6f * u)
            }
            drawCircle(PaletaNeon.SOMBRA.copy(alpha = 0.75f), 1.8f * u, Offset(w * 0.5f, g.r.top + 5f * u))
            impreso(medidor, e.semana, g.semana, 8f, FontWeight.Normal, 0.8f)
            drawPath(g.filete, TINTA.copy(alpha = 0.5f), style = Stroke(0.6f * u))
            impreso(medidor, e.dia, g.dia, 19f, FontWeight.SemiBold, 1f)
            val c = g.sello
            val tono = e.tono
            if (tono != null) {
                // La pegatina redonda del color del ánimo: su sombra, el color, el brillo arriba a la izquierda y el canto.
                drawCircle(PaletaNeon.SOMBRA.copy(alpha = 0.4f), 4.6f * u, c + Offset(0.8f * u, 1f * u))
                drawCircle(tono, 4.4f * u, c)
                drawCircle(mezcla(tono, Color.White, 0.55f).copy(alpha = 0.8f), 1.4f * u, c + Offset(-1.5f * u, -1.5f * u))
                drawCircle(mezcla(tono, PaletaNeon.SOMBRA, 0.4f), 4.4f * u, c, style = Stroke(0.6f * u))
            } else {
                // La grapa: un puente de alambre con sus dos patas, y la sombra.
                val l = 5f * u
                drawLine(PaletaNeon.SOMBRA.copy(alpha = 0.4f), Offset(c.x - l + 0.6f * u, c.y + 0.8f * u), Offset(c.x + l + 0.6f * u, c.y + 0.8f * u), 1.3f * u)
                drawLine(Color(0xFF8E939E), Offset(c.x - l, c.y), Offset(c.x + l, c.y), 1.3f * u, StrokeCap.Round)
                drawLine(Color(0xFF5E636E), Offset(c.x - l, c.y), Offset(c.x - l, c.y + 1.8f * u), 1f * u)
                drawLine(Color(0xFF5E636E), Offset(c.x + l, c.y), Offset(c.x + l, c.y + 1.8f * u), 1f * u)
                drawLine(Color.White.copy(alpha = 0.6f), Offset(c.x - l * 0.6f, c.y - 0.4f * u), Offset(c.x + l * 0.2f, c.y - 0.4f * u), 0.5f * u)
            }
        }
    }
}
