package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El índice de la libreta de momentos de Espacio: cada día es un separador de plástico de la carpeta de anillas de la mesa
 * del comedor ([MuebleLibretaEspacio]), del color del ánimo del día, con su tarjetita de papel donde va el día y, abajo, la
 * letra de la semana y el agujero de las anillas; cada mes, la hoja divisoria de cartulina con sus agujeros reforzados.
 * La pestaña se dibuja decenas de veces: unas quince operaciones, sin caminos ni pinceles nuevos.
 */
internal object IndiceLibretaEspacio {
    private val P = PaletaEstanteEspacio
    /** El hondo de la noche del índice (en oscuro) y el plástico escarchado de los días sin ánimo. */
    private val NOCHE = Color(0xFF0C1124)
    private val ESCARCHA = Color(0xFFB8BEC8)

    /**
     * La pestaña de un día: la lengüeta del separador (plástico del color del ánimo; escarchado si no hay) con la tarjetita
     * del día, sobre el hombro del separador, más oscuro, con la letra de la semana y el agujero de las anillas. Cada una un
     * poco más arriba o más abajo, como quedan metidas a mano.
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f * u || h < 8f * u) return
        val dy = (azar(e.semilla, 1) - 0.5f) * 2.4f * u
        val lengua = Rect(w * 0.15f, 3f * u + dy, w * 0.85f, h * 0.66f + dy)
        val hombro = Rect(3f * u, h * 0.6f, w - 3f * u, h - 3f * u)
        val base = e.tono ?: if (oscuro) mezcla(ESCARCHA, NOCHE, 0.62f) else ESCARCHA
        val plastico = if (oscuro) base else mezcla(base, Color.White, 0.1f)
        val sombra = if (oscuro) NOCHE.copy(alpha = 0.7f) else P.TINTA.copy(alpha = 0.22f)
        val hondo = mezcla(base, if (oscuro) NOCHE else P.TINTA, if (oscuro) 0.38f else 0.22f)
        // Las sombras, abajo a la derecha (la luz viene de arriba a la izquierda).
        drawRoundRect(sombra, lengua.topLeft + Offset(1.2f * u, 1.6f * u), lengua.size, CornerRadius(5f * u))
        drawRoundRect(sombra, hombro.topLeft + Offset(1.2f * u, 1.6f * u), hombro.size, CornerRadius(3f * u))
        // La lengüeta, con su luz arriba y su lado de sombra.
        drawRoundRect(plastico, lengua.topLeft, lengua.size, CornerRadius(5f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), Offset(lengua.left + 4f * u, lengua.top + 1f * u), Size(lengua.width - 8f * u, 1f * u))
        drawRect(sombra.copy(alpha = sombra.alpha * 0.5f), Offset(lengua.right - 3f * u, lengua.top + 4f * u), Size(3f * u, lengua.height - 4f * u))
        // El hombro del separador, que entra en la carpeta.
        drawRoundRect(hondo, hombro.topLeft, hombro.size, CornerRadius(3f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f), Offset(hombro.left + 3f * u, hombro.top), Size(hombro.width - 6f * u, 0.8f * u))
        // La tarjetita de papel con el día, metida en su ranura (el labio de plástico encima).
        val tarjeta = Rect(lengua.left + 3.5f * u, lengua.top + 4f * u, lengua.right - 3.5f * u, minOf(lengua.top + 25f * u, lengua.bottom - 6f * u))
        val papel = if (oscuro) mezcla(P.PAPEL, NOCHE, 0.18f) else P.PAPEL
        drawRect(papel, tarjeta.topLeft, tarjeta.size)
        drawRect(mezcla(plastico, Color.White, 0.3f).copy(alpha = 0.7f), tarjeta.topLeft, Size(tarjeta.width, 2.2f * u))
        textoEn(medidor, e.dia, Rect(tarjeta.left, tarjeta.top + 2f * u, tarjeta.right, tarjeta.bottom), LetraExtra.orbita, P.TINTA, 15f, peso = FontWeight.Medium, minimo = 7f)
        // La letra de la semana y el agujero de las anillas, en el hombro.
        val letra = if (hondo.luminance() > 0.42f) P.TINTA else P.PAPEL
        textoEn(medidor, e.semana, Rect(hombro.left + 4f * u, lengua.bottom - 1f * u, hombro.center.x + 4f * u, hombro.bottom - 1f * u), LetraExtra.grotesk, letra, 8.5f, peso = FontWeight.Medium, minimo = 5f)
        val ag = Offset(hombro.right - w * 0.24f, (lengua.bottom + hombro.bottom) / 2f)
        drawCircle(if (oscuro) NOCHE else mezcla(P.TINTA, base, 0.3f), 2.4f * u, ag)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.15f else 0.45f), 2.4f * u, ag, style = Stroke(0.8f * u))
    }

    /**
     * El rótulo de un mes: la hoja divisoria de cartulina cruda, a lo ancho, con sus tres agujeros reforzados con arandelas de
     * papel a la izquierda, una tira de cinta naranja en la otra punta y el mes impreso.
     */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f * u || h < 8f * u) return
        val r = Rect(2f * u, 5f * u, w - 2f * u, h - 4f * u)
        val carton = if (oscuro) mezcla(mezcla(P.PAPEL, P.TELA, 0.4f), NOCHE, 0.36f) else mezcla(P.PAPEL, P.TELA, 0.4f)
        drawRoundRect(if (oscuro) NOCHE.copy(alpha = 0.7f) else P.TINTA.copy(alpha = 0.2f), r.topLeft + Offset(1.2f * u, 1.8f * u), r.size, CornerRadius(2.5f * u))
        drawRoundRect(carton, r.topLeft, r.size, CornerRadius(2.5f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.4f), Offset(r.left + 3f * u, r.top + 0.8f * u), Size(r.width - 6f * u, 0.8f * u))
        pinceladas(r, P.TINTA, 1601, 6, u, if (oscuro) 0.05f else 0.04f)
        // Los agujeros, con su arandela de refuerzo.
        for (k in 0 until 3) {
            val c = Offset(r.left + 10f * u + k * 13f * u, r.center.y)
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.3f else 0.75f), 4.4f * u, c)
            drawCircle(if (oscuro) NOCHE else mezcla(P.TINTA, P.FONDO, 0.4f), 2.2f * u, c)
        }
        // La cinta naranja de la punta, doblada por el canto.
        val cinta = Rect(r.right - 26f * u, r.top - 1.5f * u, r.right - 12f * u, r.bottom + 1.5f * u)
        drawRect(if (oscuro) mezcla(P.NARANJA, NOCHE, 0.3f) else P.NARANJA, cinta.topLeft, cinta.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), cinta.topLeft, Size(1.2f * u, cinta.height))
        textoEn(medidor, texto.uppercase(), Rect(r.left + 44f * u, r.top, cinta.left - 8f * u, r.bottom), LetraExtra.orbita, P.TINTA, 12f,
            peso = FontWeight.Medium, espaciado = 1.4f, minimo = 6.5f)
    }
}
