package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.RAYA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El índice de la libreta de momentos de Terminal: cada día es un trozo del papel continuo de la consola, arrancado por su línea
 * de corte (los márgenes de arrastre con sus agujeros a los lados, una banda verde arriba con la letra de la semana), con el día
 * impreso grande y, abajo, una marca de rotulador del color del ánimo de ese día; sin ánimo, la línea de corte punteada (otra
 * forma, no otro color). El tema es solo de noche: el mismo con [oscuro] o sin él. Lo que no depende del día se arma una vez.
 */
internal object IndiceLibretaCiber {
    /** El papel del índice: el de la consola, apenas hundido en la penumbra de la página. */
    private val PAPELES = listOf(mezcla(PAPEL, NOCHE, 0.1f), mezcla(mezcla(PAPEL, RAYA, 0.18f), NOCHE, 0.1f))
    private val TINTA_INDICE = mezcla(TINTA, NOCHE, 0.1f)

    /** Lo que comparten todas las pestañas de [w] × [h]: el papel, su sombra, los agujeros, la banda y las cajas de los textos. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val r = Rect(4f * u, 3f * u, w - 4f * u, h - 4f * u)
        val sombra = r.translate(1.6f * u, 2f * u)
        val banda = Rect(r.left, r.top + 2f * u, r.right, r.top + 11f * u)
        val semana = Rect(r.left + 6f * u, banda.top, r.right - 6f * u, banda.bottom)
        val dia = Rect(r.left + 5f * u, r.top + 13f * u, r.right - 5f * u, r.top + 33f * u)
        val marca = Rect(r.left + 8f * u, r.bottom - 11f * u, r.right - 8f * u, r.bottom - 5f * u)
        val agujeros: List<Offset> = buildList {
            var y = r.top + 4f * u
            while (y < r.bottom - 2f * u) { add(Offset(r.left + 2.6f * u, y)); add(Offset(r.right - 2.6f * u, y)); y += 6f * u }
        }
        val corte: List<Offset> = buildList {
            var x = marca.left
            while (x < marca.right) { add(Offset(x, marca.center.y)); x += 3.2f * u }
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
        val capa = capas[llave] ?: (capaEn(medidor, texto, caja, Letra.terminal, TINTA_INDICE.copy(alpha = alfa), tam, peso = peso, minimo = 5f)
            ?: return).also { capas[llave] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /**
     * La pestaña de un día: el trozo de papel, un poco girado (según la semilla), con su sombra, los agujeros de arrastre, la banda
     * verde con la letra de la semana, el día grande y la marca de rotulador del ánimo (o, sin ánimo, la línea de corte punteada).
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val s = e.semilla and 0x7fffffff
        rotate(-3f + 6f * azar(s, 1), Offset(w / 2, h / 2)) {
            drawRect(NOCHE.copy(alpha = 0.55f), g.sombra.topLeft, g.sombra.size)
            drawRect(PAPELES[s % PAPELES.size], g.r.topLeft, g.r.size)
            drawRect(mezcla(RAYA, NOCHE, 0.12f), g.banda.topLeft, g.banda.size)
            drawPoints(g.agujeros, PointMode.Points, NOCHE.copy(alpha = 0.75f), 2.2f * u, StrokeCap.Round)
            drawRect(PAPEL.copy(alpha = 0.5f), g.r.topLeft, Size(g.r.width, 0.8f * u))
            impreso(medidor, e.semana, g.semana, 8f, FontWeight.Normal, 0.85f)
            impreso(medidor, e.dia, g.dia, 19f, FontWeight.Bold, 1f)
            val tono = e.tono
            if (tono != null) {
                // La marca de rotulador: una franja gruesa con las puntas redondas, del color del ánimo, y su borde más hondo.
                drawRoundRect(mezcla(tono, NOCHE, 0.35f), g.marca.topLeft, g.marca.size, CornerRadius(g.marca.height / 2))
                drawRoundRect(tono, Offset(g.marca.left, g.marca.top), Size(g.marca.width, g.marca.height * 0.8f), CornerRadius(g.marca.height / 2))
            } else {
                // Sin ánimo: la línea de corte del papel, punteada.
                drawPoints(g.corte, PointMode.Points, TINTA_INDICE.copy(alpha = 0.6f), 1.4f * u, StrokeCap.Round)
            }
        }
    }
}
