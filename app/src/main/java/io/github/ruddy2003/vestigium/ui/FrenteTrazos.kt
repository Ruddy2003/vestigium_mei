package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trazos que se repiten en las figuras de Frente exterior (`docs/diseno/temas/frente.md`): sus colores de dibujo, el
 * emblema de la red, la retícula y el barrido de las pantallas redondas, el piloto de consola y la ruta de salto de los
 * mapas estelares. Los usan los trofeos, el hall, la sala de trofeos, los mapas y las pantallas del tema.
 */
object TrazosFrente {
    /** La baquelita de las palancas y las perillas: un negro cálido. */
    val BAQUELITA = Color(0xFF2A2622)
    /** El cromo gastado de los biseles. */
    val CROMO = Color(0xFFB9B6AE)
    /** El trazo verde pálido de la pantalla de radar… */
    val RADAR = Color(0xFF9FD8A8)
    /** …sobre su pantalla, casi negra. */
    val PANTALLA = Color(0xFF0F1A16)
    val PILOTO_ROJO = Color(0xFFD9372B)
    val PILOTO_AMBAR = Color(0xFFF2A33A)
    /** El mármol del Directorio y su bronce: solo para lo que es del régimen. */
    val MARMOL = Color(0xFFF3F1EC)
    val BRONCE = Color(0xFF9A7442)
    /** La consola blanca, el acero pizarra verdoso de los paneles y el verde casi negro de las consolas oscuras. */
    val HUESO = Color(0xFFE6E5DD)
    val PIZARRA = Color(0xFF3A4A44)
    val CONSOLA_OSCURA = Color(0xFF1E2A26)
    /** La piedra de las ruinas que esconden la base (a la luz y en sombra) y las raíces que cuelgan de ella. */
    val PIEDRA = Color(0xFF8C8F84)
    val PIEDRA_SOMBRA = Color(0xFF3B4440)
    val RAIZ = Color(0xFF4A4636)
    /** El vidrio grabado: el trazo iluminado por el canto y el vidrio mismo (en penumbra y a la luz). */
    val VIDRIO_TRAZO = Color(0xFF8FF0D2)
    val VIDRIO = Color(0xFF123A33)
    val VIDRIO_LUZ = Color(0xFF2F6F62)
    /** El amarillo de las franjas de peligro del suelo y el óxido del sillón de la cabina. */
    val PELIGRO = Color(0xFFD9A62B)
    val OXIDO = Color(0xFFA44A1C)

    /**
     * El emblema de la red: la chispa de tres trazos dentro del círculo roto. El hueco del círculo queda arriba a la
     * derecha y por él sale una pizca de la chispa: es la señal que se escapa del canal.
     */
    fun DrawScope.emblemaRed(c: Offset, r: Float, color: Color, grosor: Float) {
        // El aro, roto arriba a la derecha (el hueco mira a −50°).
        drawArc(color, -20f, 300f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(grosor, cap = StrokeCap.Round))
        // La célula, en el centro, y la chispa: tres trazos que salen de ella y se escapan por el hueco.
        drawCircle(color, grosor * 1.05f, c)
        listOf(-68.0 to 1.16f, -50.0 to 1.42f, -32.0 to 1.16f).forEach { (g, largo) ->
            val a = Math.toRadians(g)
            drawLine(color, Offset(c.x + (r * 0.44f * cos(a)).toFloat(), c.y + (r * 0.44f * sin(a)).toFloat()),
                Offset(c.x + (r * largo * cos(a)).toFloat(), c.y + (r * largo * sin(a)).toFloat()), grosor, StrokeCap.Round)
        }
    }

    /**
     * La retícula de una pantalla redonda: los [anillos] de distancia, la cruz y, en el borde, una marca cada diez grados
     * (más larga cada treinta).
     */
    fun DrawScope.reticula(c: Offset, r: Float, color: Color, grosor: Float, anillos: Int = 3) {
        for (k in 1..anillos) drawCircle(color.copy(alpha = color.alpha * (if (k == anillos) 1f else 0.6f)), r * k / anillos, c, style = Stroke(grosor))
        drawLine(color.copy(alpha = color.alpha * 0.6f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), grosor * 0.8f)
        drawLine(color.copy(alpha = color.alpha * 0.6f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), grosor * 0.8f)
        for (k in 0 until 36) {
            val a = Math.toRadians(k * 10.0); val l = if (k % 3 == 0) 0.1f else 0.05f
            drawLine(color, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()),
                Offset(c.x + (r * (1 - l) * cos(a)).toFloat(), c.y + (r * (1 - l) * sin(a)).toFloat()), grosor * 0.8f)
        }
    }

    /** El barrido del radar: la línea que apunta a [angulo] grados y la estela que deja detrás, apagándose. */
    fun DrawScope.barrido(c: Offset, r: Float, angulo: Float, color: Color, grosor: Float) {
        rotate(angulo, c) {
            drawCircle(Brush.sweepGradient(0f to Color.Transparent, 0.8f to Color.Transparent, 1f to color.copy(alpha = color.alpha * 0.42f), center = c), r, c)
        }
        val a = Math.toRadians(angulo.toDouble())
        drawLine(color, c, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), grosor, StrokeCap.Round)
    }

    /** Un piloto de consola: el bisel de cromo, la lente y, si está [encendido], su halo. */
    fun DrawScope.piloto(c: Offset, r: Float, color: Color, encendido: Boolean, bisel: Color = CROMO) {
        if (encendido) drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.45f), Color.Transparent), c, r * 2.6f), r * 2.6f, c)
        drawCircle(bisel, r * 1.35f, c)
        drawCircle(BAQUELITA, r * 1.12f, c)
        drawCircle(if (encendido) color else color.copy(alpha = 0.35f), r, c)
        if (encendido) drawCircle(Color.White.copy(alpha = 0.55f), r * 0.32f, Offset(c.x - r * 0.3f, c.y - r * 0.32f))
    }

    /** Una ruta de salto entre estrellas: el trazo punteado y, en cada parada, el anillo de llegada. */
    fun DrawScope.rutaSalto(puntos: List<Offset>, color: Color, grosor: Float, anillo: Float) {
        if (puntos.size < 2) return
        val p = Path().apply { moveTo(puntos[0].x, puntos[0].y); puntos.drop(1).forEach { lineTo(it.x, it.y) } }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(grosor * 0.2f, grosor * 2.4f))))
        puntos.forEach { drawCircle(color, anillo, it, style = Stroke(grosor * 0.7f)) }
    }
}
