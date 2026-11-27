package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El índice de la libreta de Frente: cada día es un cartucho chico del mismo aluminio que el de la grabadora, de frente, con
 * la cabecera pintada del color del ánimo de ese día (con la letra de la semana) y, asomando por arriba como remate, la pestaña
 * de escritura del mismo color; debajo, su etiqueta de cinta de papel con el día grande a tinta y la ventanilla con sus dos
 * bobinas. Sin ánimo, la cabecera queda en el aluminio desnudo y la pestaña falta, con su hueco: cambia la silueta, no solo el
 * color. Las pestañas son decenas: lo que no depende del día se mide una vez por tamaño
 * y los números y las letras se miden una sola vez ([capaEn]).
 */
internal object IndiceLibretaFrente {
    private val P = PaletaFrente
    private val LETRA: FontFamily = LetraExtra.chivoMono
    /** El hondo del índice en modo oscuro: la penumbra verde de la base. */
    private val HONDO = Color(0xFF0C1813)

    /** Dónde va cada cosa en una pestaña de [w] × [h]: lo comparten todas. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val caja = Rect(w * 0.08f, h * 0.13f, w * 0.92f, h * 0.965f)
        val lengua = Rect(caja.right - w * 0.33f, h * 0.035f, caja.right - w * 0.1f, caja.top + 3f * u)
        val hueco = Rect(lengua.left, caja.top, lengua.right, caja.top + 3.6f * u)
        /** La cabecera, donde va el color del ánimo: con la pestaña, cerca de un tercio de la pestaña del índice. */
        val cabecera = Rect(caja.left, caja.top, caja.right, caja.top + 14.5f * u)
        val letra = Rect(caja.left + 2f * u, caja.top + 2f * u, caja.left + w * 0.4f, cabecera.bottom - 2f * u)
        val etiqueta = Rect(caja.left + 3f * u, cabecera.bottom + 1.6f * u, caja.right - 3f * u, caja.top + caja.height * 0.78f)
        val numero = Rect(etiqueta.left, etiqueta.top + 0.6f * u, etiqueta.right, etiqueta.bottom - 0.2f * u)
        val ventana = Rect(caja.left + 4f * u, etiqueta.bottom + 2.6f * u, caja.right - 4f * u, caja.bottom - 2.6f * u)
    }

    private var cache: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cache?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cache = it }

    /** Los números y las letras ya medidos (uno para el claro y otro para el oscuro: la tinta va en la medida). */
    private val medidas = List(2) { HashMap<String, TextLayoutResult>() }
    private val clave = FloatArray(3)

    /** [texto] centrado en [caja], igual que lo dibujaría [textoEn], pero medido una sola vez por modo y tamaño. */
    private fun DrawScope.medido(medidor: TextMeasurer, texto: String, caja: Rect, tinta: Color, tam: Float, peso: FontWeight, minimo: Float, oscuro: Boolean, tipo: Char) {
        if (clave[0] != density || clave[1] != fontScale || clave[2] != caja.width) {
            medidas.forEach { it.clear() }; clave[0] = density; clave[1] = fontScale; clave[2] = caja.width
        }
        val mapa = medidas[if (oscuro) 1 else 0]
        val llave = "$tipo$texto"
        val capa = mapa[llave] ?: (capaEn(medidor, texto, caja, LETRA, tinta, tam, peso = peso, minimo = minimo) ?: return).also { mapa[llave] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /**
     * La pestaña de un día: el cartucho chico de aluminio con su sombra, su luz arriba a la izquierda y su borde; la pestaña de
     * escritura del color del ánimo asomando (o, sin ánimo, su hueco); la letra de la semana grabada; la etiqueta de cinta con su
     * franja (del color del ánimo, o parda) y el día; la ventanilla con sus dos bobinas.
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val c = g.caja
        val alu = if (oscuro) mezcla(P.ALUMINIO, HONDO, 0.42f) else P.ALUMINIO
        val tono = materialFrente(e.tono, oscuro)?.let { if (oscuro) mezcla(it, HONDO, 0.08f) else it }
        // La sombra, la caja y su luz.
        drawRoundRect(P.SOMBRA.copy(alpha = if (oscuro) 0.6f else 0.3f), Offset(c.left + 1.4f * u, c.top + 1.8f * u), c.size, CornerRadius(3.5f * u))
        if (tono != null) {
            // La pestaña de escritura, que asoma por arriba, del color del ánimo.
            val l = g.lengua
            drawRoundRect(tono, l.topLeft, l.size, CornerRadius(1.2f * u))
            drawRect(mezcla(tono, Color.White, 0.35f), Offset(l.left + 1f * u, l.top + 0.6f * u), Size(l.width - 2f * u, 0.9f * u))
        }
        drawRoundRect(alu, c.topLeft, c.size, CornerRadius(3.5f * u))
        // La cabecera pintada del color del ánimo (sin ánimo, el aluminio desnudo), con la junta que la separa de la caja.
        val cab = g.cabecera
        if (tono != null) {
            drawRoundRect(tono, cab.topLeft, cab.size, CornerRadius(3.5f * u))
            drawRect(tono, Offset(cab.left, cab.bottom - 3.5f * u), Size(cab.width, 3.5f * u))
        }
        drawRect(P.SOMBRA.copy(alpha = 0.35f), Offset(cab.left + 0.5f * u, cab.bottom), Size(cab.width - 1f * u, 0.8f * u))
        drawRect(mezcla(tono ?: alu, Color.White, if (oscuro) 0.1f else 0.4f), Offset(c.left + 2.5f * u, c.top + 0.5f * u), Size(c.width - 5f * u, 1f * u))
        drawRect(mezcla(alu, P.SOMBRA, 0.25f), Offset(c.right - 3f * u, c.top + 3f * u), Size(2.5f * u, c.height - 6f * u))
        drawRoundRect(P.SOMBRA.copy(alpha = 0.55f), c.topLeft, c.size, CornerRadius(3.5f * u), style = Stroke(0.7f * u))
        if (tono == null) {
            // Sin ánimo: la pestaña falta y queda su hueco en el canto.
            val hh = g.hueco
            drawRect(if (oscuro) Color(0xFF040806) else mezcla(P.CONSOLA_OSC, alu, 0.15f), hh.topLeft, hh.size)
            drawRect(mezcla(alu, Color.White, if (oscuro) 0.06f else 0.35f), Offset(hh.left, hh.bottom), Size(hh.width, 0.7f * u))
        }
        // La letra de la semana, en la cabecera: oscura sobre los colores claros, clara sobre los hondos; sin ánimo, grabada.
        val claro = tono != null && tono.red * 0.3f + tono.green * 0.59f + tono.blue * 0.11f < 0.45f
        val letra = when {
            tono == null -> if (oscuro) mezcla(P.ALUMINIO, HONDO, 0.82f) else mezcla(P.ALUMINIO, P.TINTA, 0.62f)
            claro -> Color(0xFFF2EDE1)
            else -> Color(0xFF14110C)
        }
        medido(medidor, e.semana.uppercase(), g.letra, letra, 8f, FontWeight.Medium, 5f, oscuro, if (tono == null) 'g' else if (claro) 'c' else 'o')
        // La etiqueta de cinta de papel, con su franja y el día.
        val et = g.etiqueta
        drawRect(P.SOMBRA.copy(alpha = 0.3f), Offset(et.left + 0.6f * u, et.top + 0.9f * u), et.size)
        drawRect(if (oscuro) mezcla(P.CINTA, HONDO, 0.18f) else P.CINTA, et.topLeft, et.size)
        drawRect(tono ?: (if (oscuro) mezcla(P.CINTA_OSC, HONDO, 0.25f) else P.CINTA_OSC), et.topLeft, Size(et.width, 2.4f * u))
        medido(medidor, e.dia, g.numero, if (oscuro) Color(0xFF0B0F0D) else P.TINTA, 16f, FontWeight.Medium, 9f, oscuro, 'n')
        // La ventanilla ahumada con sus dos bobinas.
        val v = g.ventana
        if (v.height > 3f * u) {
            drawRoundRect(if (oscuro) Color(0xFF0B0D0B) else Color(0xFF2A2620), v.topLeft, v.size, CornerRadius(1.6f * u))
            val r = v.height * 0.42f
            val pel = if (oscuro) mezcla(P.MIEL, HONDO, 0.35f) else mezcla(P.MIEL, P.PELICULA, 0.35f)
            val cubo = if (oscuro) mezcla(P.CROMO_OSC, HONDO, 0.3f) else P.CROMO_OSC
            drawLine(pel, Offset(v.left + 1.6f * u, v.bottom - 1.1f * u), Offset(v.right - 1.6f * u, v.bottom - 1.1f * u), 0.8f * u)
            listOf(v.left + v.width * 0.27f to 1f, v.left + v.width * 0.73f to 0.72f).forEach { (x, carga) ->
                drawCircle(pel, r * carga, Offset(x, v.center.y))
                drawCircle(cubo, r * 0.34f, Offset(x, v.center.y))
            }
        }
    }
}
