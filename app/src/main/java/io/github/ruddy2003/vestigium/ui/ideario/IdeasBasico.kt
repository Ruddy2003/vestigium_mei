package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.PaletaBasica
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TonosBasicos

// =====================================================================
//  Básico: el Ideario sin diseño
// =====================================================================

/**
 * Básico: el Ideario sin diseño, a propósito. El mueble es una superficie lisa con su borde; cada tema, una tarjeta con su
 * nombre y una fila de puntos (uno por idea, hasta ocho) que se tiñe con el acento al abrirse; el hueco sin tema, un contorno
 * de guiones. Por dentro, el fondo liso. Las fichas son tarjetas de esquinas redondas con un borde fino. Colores de
 * [PaletaBasica], sin texturas ni ornamentos.
 */
object IdeasBasico : DisenoIdeas {
    private fun tonos(oscuro: Boolean): TonosBasicos = PaletaBasica.de(oscuro)

    override val cols = 2
    override val proporcion = 1.7f
    override val corona = 0.04f
    override val base = 0.09f
    override val lado = 0.04f
    override val hueco = 0.03f

    /** El mueble: la superficie con su borde y, si hay más compartimentos de los que se ven, la placa de «y N más» en la base. */
    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        drawRoundRect(c.contenedor, cornerRadius = CornerRadius(12 * u))
        drawRoundRect(c.bordeSuave, Offset(u / 2, u / 2), Size(size.width - u, size.height - u), CornerRadius(12 * u), style = Stroke(u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRoundRect(c.acentoSuave, r.topLeft, r.size, CornerRadius(r.height / 2))
        }
    }

    /** Un compartimento: una tarjeta con una fila de puntos (las ideas que guarda); al abrirse, el acento. */
    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val t = tonos(oscuro)
        val w = size.width; val h = size.height
        val r = Rect(u, u, w - u, h - u)
        if (c.vacio) {
            drawRoundRect(t.borde.copy(alpha = 0.5f), r.topLeft, r.size, CornerRadius(10 * u),
                style = Stroke(u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 4 * u))))
            return
        }
        val ab = c.abierto.coerceIn(0f, 1f)
        drawRoundRect(t.superficie, r.topLeft, r.size, CornerRadius(10 * u))
        if (ab > 0f) drawRoundRect(t.acento.copy(alpha = 0.12f * ab), r.topLeft, r.size, CornerRadius(10 * u))
        drawRoundRect(if (ab > 0f) t.acento else t.bordeSuave, Offset(r.left + u / 2, r.top + u / 2), Size(r.width - u, r.height - u), CornerRadius(10 * u),
            style = Stroke(u))
        // Una idea, un punto; las sin tema, en el tono tenue.
        val n = c.n.coerceIn(0, 8)
        val color = if (c.sinTema) t.tenue else t.acento
        for (i in 0 until n) drawCircle(color.copy(alpha = 0.8f), 2.2f * u, Offset(r.left + 12 * u + i * 8 * u, r.top + h * 0.18f))
    }

    override val rotulo = Rect(0.08f, 0.32f, 0.92f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean): Color = tonos(oscuro).texto
    override val letraRotulo: FontFamily = FontFamily.Default
    override val salida = 0f
    override val placa = Rect(0.3f, 0.18f, 0.7f, 0.82f)
    override fun tintaPlaca(oscuro: Boolean): Color = tonos(oscuro).sobreAcentoSuave

    /** Por dentro: el fondo liso. */
    override fun DrawScope.interior(oscuro: Boolean) {
        drawRect(tonos(oscuro).fondo)
    }

    override fun tintaInterior(oscuro: Boolean): Color = tonos(oscuro).texto
    override val pared: Dp = 12.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(8.dp)

    /** Sobre el papel de la ficha, solo un borde fino. */
    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        drawRoundRect(p.tenue.copy(alpha = 0.35f), Offset(u / 2, u / 2), Size(size.width - u, size.height - u), CornerRadius(8 * u), style = Stroke(u))
    }

    override val margenes = MARGEN_SIMPLE
    override val proporcionFicha = 1.2f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    /** La postura: una palomita, tres puntos, un círculo abierto o una equis, en los colores del papel. */
    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        val trazo = Stroke(s * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (id) {
            "hecho" -> selloHecho(p.acento, p.color)
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.32f, c.y); lineTo(c.x - s * 0.08f, c.y + s * 0.24f); lineTo(c.x + s * 0.34f, c.y - s * 0.26f) }, p.acento, style = trazo)
            "piensa" -> for (k in -1..1) drawCircle(p.acento, s * 0.08f, Offset(c.x + k * s * 0.28f, c.y))
            "duda" -> drawCircle(p.tenue, s * 0.3f, c, style = Stroke(s * 0.1f))
            "desacuerdo" -> {
                drawLine(p.tenue, Offset(c.x - s * 0.26f, c.y - s * 0.26f), Offset(c.x + s * 0.26f, c.y + s * 0.26f), s * 0.12f, StrokeCap.Round)
                drawLine(p.tenue, Offset(c.x + s * 0.26f, c.y - s * 0.26f), Offset(c.x - s * 0.26f, c.y + s * 0.26f), s * 0.12f, StrokeCap.Round)
            }
            else -> {}
        }
    }

    /** La idea fijada: un punto lleno del acento. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        drawCircle(p.acento, size.minDimension * 0.3f, center)
    }

    override val desorden = 0f
}
