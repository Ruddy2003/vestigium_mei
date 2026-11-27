package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Los caminos de una pestaña, armados una vez por tamaño: la camisa de cartón con su pestaña en uno de tres sitios (como las de
 * verdad, cortadas por tercios) y, sin ánimo, el folio suelto con la esquina doblada y su clip.
 */
private class FormasPestanaDistopia(val w: Float, val h: Float, u: Float) {
    val cuerpo = Rect(2f * u, 12f * u, w - 4f * u, h - 4f * u)
    /** La camisa con su pestaña a la izquierda, en medio o a la derecha. */
    val camisas: List<Path>
    val pestanas: List<Rect>
    /** El folio que asoma por encima de la camisa. */
    val folio = Rect(4f * u, 9.5f * u, w - 7f * u, 13f * u)
    /** Sin ánimo: el folio suelto con la esquina de arriba a la derecha doblada, el doblez y el clip. */
    val suelto: Path
    val doblez: Path
    val clip = Path()
    /** Dónde van la letra de la semana (en la pestaña o, en el folio suelto, arriba) y el día (en su etiqueta). */
    val etiqueta = Rect(6f * u, 21f * u, w - 8f * u, 44f * u)
    val cajaDia = Rect(7f * u, 21.5f * u, w - 9f * u, 43.5f * u)
    val cajaSuelto = Rect(14f * u, 4.5f * u, w - 16f * u, 13f * u)

    init {
        val c = cuerpo
        val ancho = (c.width - 4f * u) / 3f
        pestanas = List(3) { k -> Rect(c.left + 2f * u + ancho * k, 2.5f * u, c.left + 2f * u + ancho * (k + 1), c.top + 0.5f * u) }
        camisas = pestanas.map { p ->
            Path().apply {
                moveTo(c.left, c.top)
                lineTo(p.left - 1.5f * u, c.top)
                lineTo(p.left + 1f * u, p.top + 1.5f * u)
                quadraticTo(p.left + 1.4f * u, p.top, p.left + 3f * u, p.top)
                lineTo(p.right - 3f * u, p.top)
                quadraticTo(p.right - 1.4f * u, p.top, p.right - 1f * u, p.top + 1.5f * u)
                lineTo(p.right + 1.5f * u, c.top)
                lineTo(c.right, c.top)
                lineTo(c.right, c.bottom)
                lineTo(c.left, c.bottom)
                close()
            }
        }
        val l = 3f * u; val r = w - 4f * u; val t = 3f * u; val b = h - 4f * u; val d = 9f * u
        suelto = poligonoDistopia(listOf(Offset(l, t), Offset(r - d, t), Offset(r, t + d), Offset(r, b), Offset(l, b)))
        doblez = poligonoDistopia(Offset(r - d, t), Offset(r - d - 0.5f * u, t + d + 0.5f * u), Offset(r, t + d))
        val x0 = 7f * u; val y0 = 1f * u
        clip.apply {
            moveTo(x0, y0 + 12f * u); lineTo(x0, y0 + 2f * u)
            cubicTo(x0, y0 - 0.4f * u, x0 + 4.2f * u, y0 - 0.4f * u, x0 + 4.2f * u, y0 + 2f * u)
            lineTo(x0 + 4.2f * u, y0 + 15f * u)
            cubicTo(x0 + 4.2f * u, y0 + 17f * u, x0 + 1f * u, y0 + 17f * u, x0 + 1f * u, y0 + 15f * u)
            lineTo(x0 + 1f * u, y0 + 4f * u)
            cubicTo(x0 + 1f * u, y0 + 2.8f * u, x0 + 3.1f * u, y0 + 2.8f * u, x0 + 3.1f * u, y0 + 4f * u)
            lineTo(x0 + 3.1f * u, y0 + 11f * u)
        }
    }
}

/**
 * El índice de la libreta de Distopía: cada día, una camisa de cartón (la carpetilla de un folio del expediente) con su pestaña
 * cortada en uno de tres sitios, la letra de la semana en la pestaña y el día en la etiqueta del formulario, del papel hueso;
 * el folio asoma por arriba. Con ánimo, la camisa es del cartón del color del ánimo (los mismos que las camisas de la
 * biblioteca: rojo, manila, azul). Sin ánimo no hay camisa: el folio suelto, con la esquina doblada y un clip (lo dice la
 * forma, no solo el color).
 */
internal object IndiceLibretaDistopia {
    private var formas: FormasPestanaDistopia? = null

    private fun formas(w: Float, h: Float, u: Float): FormasPestanaDistopia =
        formas?.takeIf { it.w == w && it.h == h } ?: FormasPestanaDistopia(w, h, u).also { formas = it }

    /**
     * Los textos ya medidos, con su tinta (el medidor de la pantalla guarda solo 8 medidas): uno por modo, y se vacían si cambian
     * la escala o el tamaño de la pestaña.
     */
    private val medidas = arrayOf(HashMap<String, TextLayoutResult>(), HashMap<String, TextLayoutResult>())
    private val claveMedidas = FloatArray(3)

    private fun DrawScope.texto(medidor: TextMeasurer, texto: String, caja: Rect, letra: FontFamily, tinta: Color, tam: Float, peso: FontWeight, oscuro: Boolean) {
        val k = claveMedidas
        val ancho = formas?.w ?: 0f
        if (k[0] != density || k[1] != fontScale || k[2] != ancho) { medidas.forEach { it.clear() }; k[0] = density; k[1] = fontScale; k[2] = ancho }
        val mapa = medidas[if (oscuro) 1 else 0]
        val clave = "$tam:${tinta.value}:$texto"
        val capa = mapa[clave] ?: (capaEn(medidor, texto, caja, letra, tinta, tam, peso = peso, minimo = tam * 0.55f) ?: return).also { mapa[clave] = it }
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /** El papel y el cartón en oscuro: hundidos hacia el gris azulado del sótano, sin dejar de leerse. */
    private fun hondo(c: Color, oscuro: Boolean, f: Float = 0.3f) = if (oscuro) mezcla(c, P.NOCHE, f) else c

    private val SOMBRA = Color(0xFF243030).copy(alpha = 0.3f)
    private val TINTA = Color(0xFF17181A)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val f = formas(w, h, u)
        val sombra = if (oscuro) Color(0xFF05080B).copy(alpha = 0.55f) else SOMBRA
        val papel = hondo(P.HUESO, oscuro, 0.22f)
        val tono = tonoDeDiaDistopia(e.tono, oscuro)
        if (tono != null) {
            val k = Math.floorMod(e.semilla, 3)
            val camisa = f.camisas[k]
            val carton = hondo(materialDelAnimoDistopia(tono, P.CAMISAS), oscuro, 0.26f)
            // El folio que asoma, la sombra, la camisa con su canto de luz y su pie más hondo, y la raya del doblez.
            drawRect(hondo(mezcla(P.HUESO, P.CARPETA, 0.2f), oscuro), f.folio.topLeft, f.folio.size)
            translate(1.2f * u, 1.6f * u) { drawPath(camisa, sombra) }
            drawPath(camisa, carton)
            drawRect(mezcla(carton, Color.White, 0.22f), Offset(f.cuerpo.left, f.cuerpo.top), Size(f.cuerpo.width, 1f * u))
            drawRect(mezcla(carton, Color.Black, 0.25f), Offset(f.cuerpo.left, f.cuerpo.bottom - 4f * u), Size(f.cuerpo.width, 4f * u))
            drawLine(mezcla(carton, Color.Black, 0.3f), Offset(f.cuerpo.left + 3.5f * u, f.cuerpo.top + 2f * u), Offset(f.cuerpo.left + 3.5f * u, f.cuerpo.bottom - 1f * u), 0.7f * u)
            // La etiqueta del formulario con el día; la letra de la semana, en la pestaña.
            drawRect(papel, f.etiqueta.topLeft, f.etiqueta.size)
            drawRect(TINTA.copy(alpha = 0.5f), f.etiqueta.topLeft, f.etiqueta.size, style = Stroke(0.7f * u))
            val p = f.pestanas[k]
            texto(medidor, e.semana, Rect(p.left + 1f * u, p.top + 0.5f * u, p.right - 1f * u, p.bottom - 0.5f * u), P.LETRA, tintaSobre(carton), 8f, FontWeight.Medium, oscuro)
        } else {
            // Sin ánimo: el folio suelto, con la esquina doblada (su revés y la sombra del doblez) y el clip.
            translate(1.2f * u, 1.6f * u) { drawPath(f.suelto, sombra) }
            drawPath(f.suelto, papel)
            drawPath(f.doblez, mezcla(papel, P.KRAFT, 0.28f))
            drawPath(f.suelto, TINTA.copy(alpha = 0.18f), style = Stroke(0.6f * u))
            drawRect(TINTA.copy(alpha = 0.5f), f.etiqueta.topLeft, f.etiqueta.size, style = Stroke(0.7f * u))
            drawPath(f.clip, hondo(P.ACERO, oscuro, 0.2f), style = Stroke(0.8f * u))
            texto(medidor, e.semana, f.cajaSuelto, P.LETRA, TINTA.copy(alpha = 0.8f), 8f, FontWeight.Medium, oscuro)
        }
        texto(medidor, e.dia, f.cajaDia, P.LETRA, TINTA, 18f, FontWeight.SemiBold, oscuro)
    }

    /** La tinta de la letra de la semana sobre el cartón: clara sobre el rojo y el azul, oscura sobre la manila. */
    private fun tintaSobre(c: Color): Color = if (luminanciaDistopia(c) < 0.2f) Color(0xFFEDE6D0) else TINTA
}
