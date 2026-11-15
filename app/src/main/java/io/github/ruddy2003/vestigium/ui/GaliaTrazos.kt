package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trazos que se repiten en las figuras de Galia (`docs/diseno/temas/galia.md`): sus colores de dibujo y tres figuras
 * que comparten todas las superficies: el torques de oro, la hoguera y la hoja de roble. Lo demás (el caballo de las
 * monedas, el carnyx, el oppidum, el muérdago) lo dibuja cada superficie con estos colores.
 */
object TrazosGalia {
    /** El roble, a la luz y en sombra; el muérdago y sus bayas. */
    val ROBLE = Color(0xFF4E6B2E)
    val ROBLE_SOMBRA = Color(0xFF2E4219)
    val MUERDAGO = Color(0xFF9DB560)
    val BAYA = Color(0xFFF2EEDC)
    /** El barro y la paja de las casas. */
    val BARRO = Color(0xFFB89A62)
    val PAJA = Color(0xFFD8C48A)
    /** La madera de la empalizada. */
    val MADERA = Color(0xFF6B4B2A)
    /** El oro de los torques y de las monedas, y su brillo. */
    val ORO = Color(0xFFC9962B)
    val ORO_BRILLO = Color(0xFFF0D27A)
    /** El bronce del carnyx y del caldero, y su verdín. */
    val BRONCE = Color(0xFF9A6A3A)
    val VERDIN = Color(0xFF5F8C76)
    /** El hierro de las espadas y de la fragua. */
    val HIERRO = Color(0xFF5A5C5E)
    /** La lana a cuadros de los mantos. */
    val LANA_ROJA = Color(0xFF8E3B2A)
    val LANA_VERDE = Color(0xFF3F5E2A)
    val LANA_OCRE = Color(0xFFC9A24A)
    /** El humo y la niebla. */
    val HUMO = Color(0xFFB9B4A6)
    val NIEBLA = Color(0xFFDDE0D8)
    /** La luna y la hoguera, con su halo. */
    val LUNA = Color(0xFFE9E6D6)
    val FUEGO = Color(0xFFF2A040)
    val FUEGO_HALO = Color(0xFFFFD28A)
    /** Roma: solo en lo que es de Roma (el campamento, el cerco, lo que se ve a lo lejos). */
    val ROMA = Color(0xFFA33A2A)

    /**
     * El torques: un aro de oro torcido, abierto por abajo, con sus dos remates de disco que se miran. [r] es el radio
     * del aro y [grueso] el del cuerpo. La torsión se marca con trazos cortos en diagonal.
     */
    fun DrawScope.torques(c: Offset, r: Float, grueso: Float, oro: Color = ORO, brillo: Color = ORO_BRILLO) {
        val hueco = 46f                            // los grados del hueco de abajo
        val inicio = 90f + hueco / 2
        val barrido = 360f - hueco
        drawArc(oro, inicio, barrido, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(grueso, cap = StrokeCap.Round))
        // La torsión: trazos cortos en diagonal a lo largo del aro.
        var a = inicio + 6f
        while (a < inicio + barrido - 6f) {
            val rad = Math.toRadians(a.toDouble())
            val p = Offset(c.x + r * cos(rad).toFloat(), c.y + r * sin(rad).toFloat())
            val tan = Offset(-sin(rad).toFloat(), cos(rad).toFloat())
            val nor = Offset(cos(rad).toFloat(), sin(rad).toFloat())
            val d = grueso * 0.42f
            drawLine(brillo.copy(alpha = 0.7f), Offset(p.x - nor.x * d - tan.x * d * 0.6f, p.y - nor.y * d - tan.y * d * 0.6f),
                Offset(p.x + nor.x * d + tan.x * d * 0.6f, p.y + nor.y * d + tan.y * d * 0.6f), grueso * 0.16f, StrokeCap.Round)
            a += 11f
        }
        // Los dos remates: un disco a cada lado del hueco.
        for (lado in listOf(inicio, inicio + barrido)) {
            val rad = Math.toRadians(lado.toDouble())
            val p = Offset(c.x + r * cos(rad).toFloat(), c.y + r * sin(rad).toFloat())
            drawCircle(oro, grueso * 1.05f, p)
            drawCircle(brillo.copy(alpha = 0.55f), grueso * 0.45f, Offset(p.x - grueso * 0.25f, p.y - grueso * 0.25f))
        }
    }

    /**
     * La hoguera: troncos cruzados y su llama, que se mueve con [llama] (0 a 1 la hace más alta). [w] es el ancho de
     * los troncos; el halo cálido alumbra alrededor.
     */
    fun DrawScope.hoguera(c: Offset, w: Float, llama: Float = 1f, madera: Color = MADERA) {
        val alto = w * 0.9f * (0.75f + 0.25f * llama)
        drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = 0.4f), FUEGO_HALO.copy(alpha = 0f)), Offset(c.x, c.y - alto * 0.4f), w * 1.3f),
            w * 1.3f, Offset(c.x, c.y - alto * 0.4f))
        // Los troncos, en aspa.
        drawLine(madera, Offset(c.x - w * 0.5f, c.y + w * 0.04f), Offset(c.x + w * 0.42f, c.y - w * 0.16f), w * 0.13f, StrokeCap.Round)
        drawLine(madera, Offset(c.x + w * 0.5f, c.y + w * 0.04f), Offset(c.x - w * 0.42f, c.y - w * 0.16f), w * 0.13f, StrokeCap.Round)
        // La llama: tres lenguas.
        fun lengua(dx: Float, h: Float, ancho: Float, color: Color) {
            val base = Offset(c.x + dx, c.y - w * 0.08f)
            val p = Path().apply {
                moveTo(base.x - ancho, base.y)
                quadraticTo(base.x - ancho * 1.1f, base.y - h * 0.55f, base.x + ancho * 0.1f, base.y - h)
                quadraticTo(base.x + ancho * 1.1f, base.y - h * 0.5f, base.x + ancho, base.y)
                close()
            }
            drawPath(p, color)
        }
        lengua(-w * 0.14f, alto * 0.7f, w * 0.14f, FUEGO.copy(alpha = 0.85f))
        lengua(w * 0.15f, alto * 0.62f, w * 0.12f, FUEGO.copy(alpha = 0.85f))
        lengua(0f, alto, w * 0.17f, FUEGO)
        lengua(0f, alto * 0.55f, w * 0.08f, FUEGO_HALO)
    }

    /**
     * La hoja de roble, de lóbulos redondos, de largo [largo], girada [angulo] grados (0 = hacia arriba), con su
     * nervio central.
     */
    fun DrawScope.hojaRoble(c: Offset, largo: Float, angulo: Float, color: Color = ROBLE, nervio: Color = ROBLE_SOMBRA) {
        rotate(angulo, c) {
            val l = largo
            val p = Path().apply {
                moveTo(c.x, c.y + l * 0.5f)
                // Lado izquierdo: cuatro lóbulos que crecen hacia arriba y se cierran en la punta.
                var y = c.y + l * 0.42f
                val lobulos = 4
                for (i in 0 until lobulos) {
                    val paso = l * 0.82f / lobulos
                    val ancho = l * (0.16f + 0.08f * i / (lobulos - 1).toFloat())
                    quadraticTo(c.x - ancho * 1.3f, y - paso * 0.25f, c.x - ancho * 0.45f, y - paso * 0.55f)
                    quadraticTo(c.x - ancho * 0.85f, y - paso * 0.8f, c.x - ancho * 0.3f, y - paso)
                    y -= paso
                }
                quadraticTo(c.x, c.y - l * 0.52f, c.x + l * 0.06f, c.y - l * 0.42f)
                // Lado derecho, en espejo.
                y = c.y - l * 0.4f
                for (i in lobulos - 1 downTo 0) {
                    val paso = l * 0.82f / lobulos
                    val ancho = l * (0.16f + 0.08f * i / (lobulos - 1).toFloat())
                    quadraticTo(c.x + ancho * 0.85f, y + paso * 0.2f, c.x + ancho * 0.45f, y + paso * 0.45f)
                    quadraticTo(c.x + ancho * 1.3f, y + paso * 0.75f, c.x + ancho * 0.3f, y + paso)
                    y += paso
                }
                close()
            }
            drawPath(p, color)
            drawLine(nervio, Offset(c.x, c.y + l * 0.55f), Offset(c.x, c.y - l * 0.4f), l * 0.035f, StrokeCap.Round)
        }
    }
}
