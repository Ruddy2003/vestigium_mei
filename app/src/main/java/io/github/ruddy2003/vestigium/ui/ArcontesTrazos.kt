package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trazos que se repiten en las figuras de Arcontes (`docs/diseno/temas/arcontes.md`): sus colores de dibujo y las
 * cuatro figuras del tema que comparten todas las superficies: el ouroboros, las siete esferas, la grieta de luz y la
 * lámpara de aceite. Lo demás (Abraxas, la jarra, el códice, las barcas) lo dibuja cada superficie con estos colores.
 */
object TrazosArcontes {
    /** La caliza de Galilea, al sol y en sombra. */
    val CALIZA = Color(0xFFD9CBA6)
    val CALIZA_SOMBRA = Color(0xFF9C8A68)
    /** El lino. */
    val LINO = Color(0xFFEFE6D0)
    /** El barro de las lámparas y las jarras, y el cocido claro. */
    val BARRO = Color(0xFF8A4E2B)
    val BARRO_CLARO = Color(0xFFB9774A)
    /** El oro de más del mundo visible: solo en lo que es del mundo de abajo (el sol, el sello de los arcontes). */
    val ORO_DEMIURGO = Color(0xFFD4A62A)
    /** El lago, y su hondura. */
    val LAGO = Color(0xFF3F7A8A)
    val LAGO_HONDO = Color(0xFF1F4652)
    /** El cielo antiguo de la noche y los círculos finos de las esferas. */
    val CIELO_ANTIGUO = Color(0xFF0B0D1C)
    val ESFERA = Color(0xFF6E6A9E)
    /** La grieta: la única luz que no viene de una llama ni del sol. Blanca, con su halo violeta. */
    val GRIETA = Color(0xFFF4F1FF)
    val GRIETA_HALO = Color(0xFF9C7BFF)
    /** La llama de la lámpara y su halo. */
    val LLAMA = Color(0xFFF0B24A)
    val LLAMA_HALO = Color(0xFFFFD98A)
    /** La sombra de un arconte: se intuye, nunca entera. */
    val SOMBRA = Color(0xFF1A1424)
    /** El jaspe de las gemas, verde y rojo, y la hematita. */
    val JASPE_VERDE = Color(0xFF3E6B4E)
    val JASPE_ROJO = Color(0xFF8E3A2E)
    val HEMATITA = Color(0xFF3A3A40)
    /** El cuero de la tapa del códice y su correa. */
    val CUERO = Color(0xFF6B4A2E)
    val CORREA = Color(0xFF4A321E)

    /**
     * El ouroboros: la serpiente que se muerde la cola, en un anillo de radio [r] y cuerpo de grosor [grueso]. La cabeza
     * va arriba a la derecha (a [cabeza] grados, medidos como en `drawArc`: 0 a la derecha, en el sentido del reloj) y
     * muerde la punta de la cola; el cuerpo se adelgaza hacia la cola. Las escamas son marcas cortas a lo largo del lomo.
     */
    fun DrawScope.ouroboros(c: Offset, r: Float, grueso: Float, color: Color, cabeza: Float = -60f, escamas: Color? = null) {
        val pasos = 72
        val hueco = 16f                          // los grados que ocupa la cabeza
        val inicio = cabeza + hueco / 2          // la cola empieza justo detrás de la cabeza
        // El cuerpo, de la cola (fina) a la nuca (gruesa), en tramos de grosor creciente.
        for (i in 0 until pasos) {
            val t0 = i / pasos.toFloat()
            val a0 = inicio + t0 * (360f - hueco)
            val barrido = (360f - hueco) / pasos + 0.6f
            val g = grueso * (0.35f + 0.65f * t0)
            drawArc(color, a0, barrido, false, Offset(c.x - r, c.y - r), androidx.compose.ui.geometry.Size(r * 2, r * 2), style = Stroke(g, cap = StrokeCap.Butt))
        }
        // Las escamas: marcas cortas en el lomo, cada vez más separadas hacia la cola.
        if (escamas != null) {
            var a = inicio + 10f
            while (a < inicio + 360f - hueco - 6f) {
                val rad = Math.toRadians(a.toDouble())
                val p = Offset(c.x + r * cos(rad).toFloat(), c.y + r * sin(rad).toFloat())
                val tan = Offset(-sin(rad).toFloat(), cos(rad).toFloat())
                val d = grueso * 0.22f
                drawLine(escamas, Offset(p.x - tan.x * d, p.y - tan.y * d), Offset(p.x + tan.x * d, p.y + tan.y * d), grueso * 0.08f, StrokeCap.Round)
                a += 9f
            }
        }
        // La cabeza: un óvalo un poco más grueso que el cuerpo, con la boca abierta sobre la punta de la cola y un ojo.
        val radC = Math.toRadians(cabeza.toDouble())
        val pc = Offset(c.x + r * cos(radC).toFloat(), c.y + r * sin(radC).toFloat())
        val tan = Offset(-sin(radC).toFloat(), cos(radC).toFloat())   // hacia donde avanza (hacia la cola)
        val largo = grueso * 1.5f
        val ancho = grueso * 0.62f
        val cab = Path().apply {
            moveTo(pc.x - tan.x * largo * 0.6f, pc.y - tan.y * largo * 0.6f)
            quadraticTo(pc.x + (cos(radC).toFloat()) * ancho, pc.y + (sin(radC).toFloat()) * ancho, pc.x + tan.x * largo * 0.55f, pc.y + tan.y * largo * 0.55f)
            quadraticTo(pc.x - (cos(radC).toFloat()) * ancho, pc.y - (sin(radC).toFloat()) * ancho, pc.x - tan.x * largo * 0.6f, pc.y - tan.y * largo * 0.6f)
            close()
        }
        drawPath(cab, color)
        val ojo = Offset(pc.x + cos(radC).toFloat() * ancho * 0.35f, pc.y + sin(radC).toFloat() * ancho * 0.35f)
        drawCircle(escamas ?: color.copy(alpha = 0f), grueso * 0.09f, ojo)
    }

    /**
     * Las siete esferas: siete círculos finos uno dentro de otro, como el diagrama de los ofitas (recurso: así se
     * dibujan). [giro] (de 0 a 1) mueve una marca pequeña en cada círculo, cada uno a su paso, para el fondo vivo.
     */
    fun DrawScope.esferas(c: Offset, r: Float, color: Color, grueso: Float, giro: Float = 0f, n: Int = 7) {
        for (i in 0 until n) {
            val ri = r * (0.32f + 0.68f * i / (n - 1).coerceAtLeast(1))
            drawCircle(color.copy(alpha = color.alpha * (0.55f + 0.45f * (1f - i / n.toFloat()))), ri, c, style = Stroke(grueso))
            val a = (giro * 360f * (1f + i * 0.37f) + i * 51f) * (Math.PI / 180.0)
            drawCircle(color, grueso * 1.6f, Offset(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat()))
        }
    }

    /**
     * La grieta de luz: una raja fina entre [desde] y [hasta] que quiebra apenas su camino (nunca en zigzag de rayo),
     * blanca, con su halo violeta. [luz] (0 a 1) la abre y la cierra.
     */
    fun DrawScope.grieta(desde: Offset, hasta: Offset, ancho: Float, luz: Float = 1f, semilla: Int = 7) {
        if (luz <= 0f) return
        val rnd = java.util.Random(semilla.toLong())
        val tramos = 5
        val dx = hasta.x - desde.x
        val dy = hasta.y - desde.y
        val largo = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val nx = -dy / largo
        val ny = dx / largo
        val camino = Path().apply {
            moveTo(desde.x, desde.y)
            for (k in 1 until tramos) {
                val t = k / tramos.toFloat()
                val lado = (rnd.nextFloat() - 0.5f) * largo * 0.06f
                lineTo(desde.x + dx * t + nx * lado, desde.y + dy * t + ny * lado)
            }
            lineTo(hasta.x, hasta.y)
        }
        drawPath(camino, GRIETA_HALO.copy(alpha = 0.22f * luz), style = Stroke(ancho * 7f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(camino, GRIETA_HALO.copy(alpha = 0.45f * luz), style = Stroke(ancho * 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(camino, GRIETA.copy(alpha = luz), style = Stroke(ancho, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /**
     * La lámpara de aceite de barro, de pico, la de las casas de Galilea, vista de lado: el cuerpo redondo, el pico a la
     * derecha con su llama y el asa a la izquierda. [w] es el ancho total; [llama] (0 a 1) su tamaño, para que tiemble.
     */
    fun DrawScope.lampara(c: Offset, w: Float, barro: Color = BARRO_CLARO, llama: Float = 1f) {
        val h = w * 0.34f
        val cuerpo = Path().apply {
            moveTo(c.x - w * 0.42f, c.y)
            cubicTo(c.x - w * 0.42f, c.y - h * 0.95f, c.x + w * 0.12f, c.y - h * 0.95f, c.x + w * 0.2f, c.y - h * 0.42f)
            lineTo(c.x + w * 0.46f, c.y - h * 0.3f)
            quadraticTo(c.x + w * 0.5f, c.y - h * 0.05f, c.x + w * 0.42f, c.y + h * 0.02f)
            lineTo(c.x + w * 0.16f, c.y + h * 0.18f)
            cubicTo(c.x + w * 0.04f, c.y + h * 0.5f, c.x - w * 0.36f, c.y + h * 0.48f, c.x - w * 0.42f, c.y)
            close()
        }
        drawPath(cuerpo, barro)
        // El agujero de llenar, arriba, y el asa.
        drawCircle(barro.copy(red = barro.red * 0.55f, green = barro.green * 0.55f, blue = barro.blue * 0.55f), w * 0.05f, Offset(c.x - w * 0.12f, c.y - h * 0.62f))
        drawArc(barro, 120f, 150f, false, Offset(c.x - w * 0.58f, c.y - h * 0.5f), androidx.compose.ui.geometry.Size(w * 0.24f, h * 0.9f), style = Stroke(w * 0.045f, cap = StrokeCap.Round))
        if (llama > 0f) {
            val base = Offset(c.x + w * 0.46f, c.y - h * 0.32f)
            val alto = w * 0.34f * llama
            drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = 0.45f), LLAMA_HALO.copy(alpha = 0f)), Offset(base.x, base.y - alto * 0.45f), alto * 1.4f), alto * 1.4f, Offset(base.x, base.y - alto * 0.45f))
            val fuego = Path().apply {
                moveTo(base.x - w * 0.045f, base.y)
                quadraticTo(base.x - w * 0.06f, base.y - alto * 0.55f, base.x + w * 0.01f, base.y - alto)
                quadraticTo(base.x + w * 0.06f, base.y - alto * 0.5f, base.x + w * 0.04f, base.y)
                close()
            }
            drawPath(fuego, LLAMA)
            drawPath(fuego, Color.White.copy(alpha = 0.35f), style = Stroke(w * 0.008f))
        }
    }
}
