package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.PaletaBasica
import io.github.ruddy2003.vestigium.ui.TonosBasicos
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Básico: los logros sin diseño
// =====================================================================

/**
 * Básico: los logros y el viaje sin diseño, a propósito. Los pedestales son bloques lisos de esquinas redondas, con una
 * raya del metal del nivel; la placa, una tarjeta con su borde; el salón, el fondo de la pantalla tal cual (sin
 * arquitectura); el marcador de la campaña, un alfiler de mapa; la victoria, una bandera; y los objetos de Tu año, siluetas
 * simples (la luna, la huella, la pesa). Colores de [PaletaBasica].
 */
object LogrosBasico : DisenoLogros {
    private fun tonos(oscuro: Boolean): TonosBasicos = PaletaBasica.de(oscuro)

    /** El color de un pedestal: hecho, la variante teñida del metal; si no, una silueta tenue. */
    private fun piedra(e: EstadoPedestal, c: TonosBasicos): Color = if (e.hecho) mezcla(c.variante, e.base, 0.22f) else c.bordeSuave.copy(alpha = 0.5f)

    /** El pedestal del trofeo: un bloque liso en la franja de abajo, con la raya del metal arriba. */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val r = Rect(w * 0.14f, h * 0.78f, w * 0.86f, h * 0.96f)
        drawRoundRect(piedra(e, c), r.topLeft, r.size, CornerRadius(r.height * 0.18f))
        if (e.hecho) drawLine(e.base, Offset(r.left + r.width * 0.1f, r.top + r.height * 0.22f), Offset(r.right - r.width * 0.1f, r.top + r.height * 0.22f),
            maxOf(1f, r.height * 0.08f), StrokeCap.Round)
    }

    /** El pedestal del hall: un bloque bajo con la raya del metal del nivel. */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val r = Rect(w * 0.1f, h * 0.15f, w * 0.9f, h * 0.95f)
        drawRoundRect(piedra(e, c), r.topLeft, r.size, CornerRadius(4 * u))
        drawLine(if (e.hecho) e.base else c.bordeSuave, Offset(r.left + 4 * u, r.top + 3 * u), Offset(r.right - 4 * u, r.top + 3 * u), 2 * u, StrokeCap.Round)
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = tonos(oscuro).texto
    override fun rotuloPlaca(oscuro: Boolean): Color = tonos(oscuro).tenue

    /** La placa del hall: una tarjeta con el borde del metal del nivel. */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        drawRoundRect(c.superficie, cornerRadius = CornerRadius(8 * u))
        drawRoundRect(if (e.hecho) e.base.copy(alpha = 0.8f) else c.bordeSuave, Offset(u / 2, u / 2), Size(size.width - u, size.height - u), CornerRadius(8 * u),
            style = Stroke(u))
    }

    /** El salón: nada detrás de los trofeos; el fondo de la pantalla queda tal cual. */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {}

    /** El marcador: un alfiler de mapa del color de acento, con su sombra en el suelo; brinca con [EstadoMarcador.salto]. */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val p = e.punto
        val alto = 6 * u * e.salto.coerceIn(0f, 1f)
        drawOval(Color.Black.copy(alpha = 0.18f), Offset(p.x - 5 * u, p.y - 1.5f * u), Size(10 * u, 3 * u))
        val cabeza = Offset(p.x, p.y - 15 * u - alto)
        val r = 7 * u
        drawPath(Path().apply {
            moveTo(p.x, p.y - alto)
            lineTo(cabeza.x - r * 0.75f, cabeza.y + r * 0.6f)
            lineTo(cabeza.x + r * 0.75f, cabeza.y + r * 0.6f)
            close()
        }, e.acento)
        drawCircle(e.acento, r, cabeza)
        drawCircle(c.superficie, r * 0.42f, cabeza)
    }

    /** La victoria: una bandera del color de acento plantada arriba de la meta. */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val base = Offset(e.meta.x, e.meta.y - 8 * u)
        val tope = Offset(base.x, base.y - 34 * u)
        drawLine(c.tenue, base, tope, 2 * u, StrokeCap.Round)
        drawRoundRect(e.acento, Offset(tope.x + 1 * u, tope.y), Size(20 * u, 13 * u), CornerRadius(2 * u))
        drawPath(Path().apply { moveTo(tope.x + 7 * u, tope.y + 7 * u); lineTo(tope.x + 10 * u, tope.y + 10 * u); lineTo(tope.x + 16 * u, tope.y + 3.5f * u) },
            c.superficie, style = Stroke(1.6f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Los objetos de Tu año: una luna creciente, una huella (la planta y cuatro dedos) o una pesa (la barra y dos discos). */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val c = e.centro; val r = e.radio
        when (e.forma) {
            "luna" -> {
                drawCircle(e.color, r * 0.85f, c)
                drawCircle(e.fondo, r * 0.72f, Offset(c.x + r * 0.38f, c.y - r * 0.22f))
            }
            "huella" -> {
                drawOval(e.color, Offset(c.x - r * 0.42f, c.y - r * 0.35f), Size(r * 0.84f, r * 1.25f))
                listOf(-0.36f, -0.12f, 0.12f, 0.34f).forEachIndexed { i, dx ->
                    drawCircle(e.color, r * (0.17f - i * 0.02f), Offset(c.x + dx * r, c.y - r * (0.62f + if (i == 1 || i == 2) 0.1f else 0f)))
                }
            }
            "pesa" -> {
                drawLine(e.color, Offset(c.x - r * 0.8f, c.y), Offset(c.x + r * 0.8f, c.y), r * 0.18f, StrokeCap.Round)
                listOf(-1f, 1f).forEach { s ->
                    drawRoundRect(e.color, Offset(c.x + s * r * 0.55f - r * 0.14f, c.y - r * 0.5f), Size(r * 0.28f, r), CornerRadius(r * 0.08f))
                }
            }
            else -> drawCircle(e.color, r * 0.7f, c)
        }
    }
}
