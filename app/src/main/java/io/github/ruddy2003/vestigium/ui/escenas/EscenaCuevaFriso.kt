package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El friso que Ama pinta en la pared del abrigo, a la entrada de la galería: lo que el arco `friso` va llenando de la
 * temporada 1 a la 5. Cada figura se pinta por pasos, como se pintaba: primero el contorno, con carbón; después el
 * relleno, con ocre, que entra desde la cabeza; al final la crin, la cola, los cascos y el ojo.
 *
 * **[Diseño propio]**: las figuras son inventadas. Tienen el aire del arte de esos milenios (contorno negro, relleno
 * de ocre rojo o amarillo, el vientre claro, las patas cortas, la crin tiesa), pero no copian ningún panel de ninguna
 * cueva. El orden en que aparecen también es mío: el calendario solo fija que el primer caballo es de la temporada 1,
 * que el friso llega a la mitad en la 3 y que se termina en la 5.
 */
internal object FrisoCueva {
    private val C = ColorCueva

    /** La parte lisa y clara de la pared donde va el friso. */
    val PANEL = Rect(14f, 56f, 144f, 158f)

    internal enum class Clase { CABALLO, POTRO, CIERVO }

    /** Una figura del friso: [centro] y [largo] en el lienzo, hacia dónde [mira], y entre qué valores del arco se pinta. */
    internal class Figura(val clase: Clase, val centro: Offset, val largo: Float, val mira: Float, val desde: Float, val hasta: Float, val relleno: Color, val crin: Color)

    /** Las seis figuras, en el orden en que Ama las pinta. La primera es el caballo rojo que da nombre a su primer año. */
    val FIGURAS = listOf(
        Figura(Clase.CABALLO, Offset(50f, 106f), 54f, 1f, 0.0f, 0.14f, C.OCRE_ROJO, C.HOLLIN),
        Figura(Clase.CABALLO, Offset(108f, 97f), 46f, 1f, 0.14f, 0.32f, C.OCRE_AMARILLO, C.HOLLIN),
        Figura(Clase.POTRO, Offset(86f, 137f), 30f, 1f, 0.32f, 0.44f, lerp(C.OCRE_AMARILLO, C.CALIZA, 0.4f), C.HOLLIN),
        Figura(Clase.CABALLO, Offset(118f, 136f), 40f, 1f, 0.5f, 0.68f, lerp(C.HOLLIN, C.PIEL_OSC, 0.3f), C.HOLLIN),
        Figura(Clase.CIERVO, Offset(40f, 143f), 36f, 1f, 0.68f, 0.86f, lerp(C.OCRE_ROJO, C.OCRE_AMARILLO, 0.35f), C.HOLLIN),
        Figura(Clase.CABALLO, Offset(112f, 68f), 30f, -1f, 0.86f, 1.0f, lerp(C.OCRE_ROJO, C.HOLLIN, 0.3f), C.HOLLIN),
    )

    /** Cuánto de la figura [i] está pintado, de 0 a 1, con el friso en [friso]. Lo vigila `EscenaCuevaPrueba`. */
    fun avance(i: Int, friso: Float): Float { val f = FIGURAS[i]; return ((friso - f.desde) / (f.hasta - f.desde)).coerceIn(0f, 1f) }

    /** Cuántas figuras están terminadas con el friso en [friso]. */
    fun terminadas(friso: Float): Int = FIGURAS.indices.count { avance(it, friso) >= 1f }

    /** La hilera de puntos rojos: aparecen uno tras otro entre la tercera y la cuarta figura. */
    fun puntos(friso: Float): Int = (((friso - 0.44f) / 0.06f) * 12f).toInt().coerceIn(0, 12)

    // ---- Los contornos, en una caja de 1 de largo: la x va de la cola (0) al hocico (1) y la y sube hacia arriba (negativa).
    private val CABALLO = listOf(
        0.03f to -0.42f, 0.10f to -0.50f, 0.26f to -0.50f, 0.42f to -0.47f, 0.55f to -0.50f, 0.66f to -0.60f, 0.76f to -0.68f, 0.80f to -0.74f, 0.83f to -0.67f, 0.92f to -0.58f,
        0.985f to -0.50f, 0.97f to -0.455f, 0.90f to -0.47f, 0.82f to -0.50f, 0.76f to -0.44f, 0.72f to -0.32f, 0.69f to -0.22f, 0.665f to -0.10f, 0.67f to -0.01f, 0.63f to -0.01f,
        0.62f to -0.12f, 0.60f to -0.21f, 0.48f to -0.15f, 0.34f to -0.15f, 0.24f to -0.20f, 0.215f to -0.10f, 0.24f to -0.01f, 0.195f to -0.01f, 0.165f to -0.10f, 0.12f to -0.22f, 0.04f to -0.32f,
    )
    private val CIERVO = listOf(
        0.04f to -0.44f, 0.12f to -0.50f, 0.30f to -0.49f, 0.46f to -0.47f, 0.56f to -0.52f, 0.64f to -0.66f, 0.70f to -0.80f, 0.74f to -0.87f, 0.80f to -0.87f, 0.90f to -0.81f,
        0.955f to -0.76f, 0.93f to -0.72f, 0.84f to -0.74f, 0.78f to -0.70f, 0.74f to -0.56f, 0.72f to -0.38f, 0.69f to -0.24f, 0.665f to -0.10f, 0.67f to -0.01f, 0.635f to -0.01f,
        0.625f to -0.12f, 0.60f to -0.22f, 0.48f to -0.18f, 0.34f to -0.18f, 0.24f to -0.22f, 0.215f to -0.10f, 0.235f to -0.01f, 0.195f to -0.01f, 0.17f to -0.10f, 0.12f to -0.24f, 0.05f to -0.34f,
    )

    /** Muchos puntos sobre la curva suave y cerrada que pasa por [puntos]: para trazar solo una parte del contorno. */
    private fun denso(puntos: List<Offset>, cada: Int = 5): List<Offset> {
        val n = puntos.size; val out = ArrayList<Offset>(n * cada + 1)
        for (i in 0 until n) {
            val a = puntos[(i - 1 + n) % n]; val b = puntos[i]; val c = puntos[(i + 1) % n]; val d = puntos[(i + 2) % n]
            for (j in 0 until cada) {
                val t = j / cada.toFloat(); val t2 = t * t; val t3 = t2 * t
                out.add(Offset(
                    0.5f * (2f * b.x + (c.x - a.x) * t + (2f * a.x - 5f * b.x + 4f * c.x - d.x) * t2 + (3f * b.x - a.x - 3f * c.x + d.x) * t3),
                    0.5f * (2f * b.y + (c.y - a.y) * t + (2f * a.y - 5f * b.y + 4f * c.y - d.y) * t2 + (3f * b.y - a.y - 3f * c.y + d.y) * t3),
                ))
            }
        }
        out.add(out.first())
        return out
    }

    private fun DrawScope.figura(z: LuzCueva, f: Figura, p: Float) {
        if (p <= 0f) return
        val l = f.largo
        fun en(x: Float, y: Float) = Offset(f.centro.x + (x - 0.5f) * l * f.mira, f.centro.y + (y + 0.36f) * l)
        val contorno = denso((if (f.clase == Clase.CIERVO) CIERVO else CABALLO).map { (x, y) -> en(x, y) })
        val trazo = (p / 0.4f).coerceIn(0f, 1f); val lleno = ((p - 0.3f) / 0.45f).coerceIn(0f, 1f); val detalle = ((p - 0.75f) / 0.25f).coerceIn(0f, 1f)
        val carbon = z.en(f.centro, C.HOLLIN).copy(alpha = 0.86f); val ocre = z.en(f.centro, f.relleno); val claro = z.en(f.centro, lerp(f.relleno, C.CALIZA, 0.62f))
        val g = (l / 46f).coerceIn(0.6f, 1.3f)
        // Las patas del otro lado, apenas apuntadas.
        if (detalle > 0f) for ((a, b) in listOf((0.63f to -0.2f) to (0.74f to -0.03f), (0.2f to -0.2f) to (0.12f to -0.03f))) drawLine(carbon.copy(alpha = 0.6f * detalle), en(a.first, a.second), en(b.first, b.second), 1.4f * g, StrokeCap.Round)
        // El relleno: entra desde la cabeza hacia la grupa; el vientre se deja claro.
        if (lleno > 0f) {
            val forma = poligono(contorno)
            val x0 = minOf(en(0f, 0f).x, en(1f, 0f).x); val x1 = maxOf(en(0f, 0f).x, en(1f, 0f).x); val hasta = if (f.mira > 0f) x1 - (x1 - x0) * lleno else x0 + (x1 - x0) * lleno
            clipPath(forma) {
                clipRect(if (f.mira > 0f) hasta else x0 - 2f, f.centro.y - l, if (f.mira > 0f) x1 + 2f else hasta, f.centro.y + l) {
                    drawRect(Brush.verticalGradient(0f to ocre.copy(alpha = 0.92f), 0.6f to ocre.copy(alpha = 0.88f), 0.82f to claro.copy(alpha = 0.7f), 1f to ocre.copy(alpha = 0.8f), startY = en(0f, -0.74f).y, endY = en(0f, 0f).y), Offset(x0 - 2f, f.centro.y - l), Size(x1 - x0 + 4f, 2f * l))
                    // La pintura no tapa la roca: se le ven las calvas donde la pared hace hoyo.
                    val az = Azar(Semilla.de("cueva", "friso", f.centro.x.toInt()))
                    repeat(7) { mancha(Offset(az.entre(x0, x1), az.entre(en(0f, -0.6f).y, en(0f, -0.1f).y)), az.entre(1.6f, 4f) * g, az.entre(1f, 2.2f) * g, z.en(f.centro, C.CALIZA), 0.4f) }
                }
            }
        }
        // El contorno de carbón: la parte que ya está trazada.
        val n = (contorno.size * trazo).toInt().coerceIn(2, contorno.size)
        drawPoints(contorno.subList(0, n), PointMode.Polygon, carbon, 1.15f * g, StrokeCap.Round)
        if (detalle > 0f) {
            val tinta = z.en(f.centro, f.crin).copy(alpha = 0.9f * detalle)
            when (f.clase) {
                Clase.CABALLO, Clase.POTRO -> {
                    // La crin, tiesa; la cola; los cascos y las cañas, oscuros.
                    for (j in 0..7) { val t = j / 7f; val a = en(0.56f + 0.2f * t, -0.52f - 0.17f * t); drawLine(tinta, a, a + Offset(-0.5f * g * f.mira, -3.2f * g), 1.5f * g, StrokeCap.Round) }
                    drawPath(Path().apply { val a = en(0.03f, -0.42f); val b = en(-0.05f, -0.1f); moveTo(a.x, a.y); cubicTo(a.x - 3f * g * f.mira, a.y + 2f, b.x - 1f * f.mira, b.y - 6f * g, b.x, b.y) }, tinta, style = Stroke(1.7f * g, cap = StrokeCap.Round))
                    for ((x0, x1) in listOf(0.665f to 0.65f, 0.215f to 0.218f)) drawLine(tinta, en(x0, -0.1f), en(x1, -0.015f), 2.2f * g, StrokeCap.Round)
                }
                Clase.CIERVO -> {
                    // Las astas: dos varas hacia atrás con sus puntas hacia delante.
                    val base = en(0.75f, -0.87f)
                    for (k in 0..1) {
                        val d = Offset(k * 1.6f * g * f.mira, k * 0.6f * g)
                        val punta = en(0.6f, -1.2f) + d; val codo = en(0.62f, -0.98f) + d
                        drawPath(Path().apply { moveTo(base.x + d.x, base.y + d.y); cubicTo(codo.x, codo.y, codo.x - 2f * f.mira, codo.y - 3f, punta.x, punta.y) }, tinta, style = Stroke(1.1f * g, cap = StrokeCap.Round))
                        for ((u, v) in listOf(0.69f to -0.97f, 0.65f to -1.06f, 0.62f to -1.14f)) drawLine(tinta, en(u, v) + d, en(u + 0.09f, v - 0.08f) + d, 0.9f * g, StrokeCap.Round)
                    }
                    drawLine(tinta, en(0.04f, -0.44f), en(0.0f, -0.36f), 1.3f * g, StrokeCap.Round)
                    for ((x0, x1) in listOf(0.665f to 0.652f, 0.215f to 0.215f)) drawLine(tinta, en(x0, -0.09f), en(x1, -0.015f), 1.9f * g, StrokeCap.Round)
                }
            }
            drawCircle(tinta, 0.8f * g, en(if (f.clase == Clase.CIERVO) 0.84f else 0.86f, if (f.clase == Clase.CIERVO) -0.81f else -0.62f))
        }
    }

    /** Una mano soplada: la nube de ocre alrededor y, dentro, la mano del color de la pared. [talla]: 1, la de Ama. */
    private fun DrawScope.mano(z: LuzCueva, centro: Offset, talla: Float) {
        val ocre = z.en(centro, C.OCRE_ROJO); val pared = z.en(centro, lerp(C.CALIZA, Color.White, 0.12f))
        mancha(centro, 9.6f * talla, 10.6f * talla, ocre, 0.62f); mancha(centro + Offset(0f, -1f), 6.6f * talla, 7.6f * talla, ocre, 0.5f)
        val az = Azar(Semilla.de("cueva", "mano", centro.x.toInt()))
        drawPoints((0 until 46).map { centro + Offset(az.entre(-12f, 12f) * talla, az.entre(-13f, 13f) * talla) }, PointMode.Points, ocre.copy(alpha = 0.5f), 0.7f, StrokeCap.Round)
        drawOval(pared, Offset(centro.x - 2.9f * talla, centro.y - 1.2f * talla), Size(5.8f * talla, 6.4f * talla))
        for ((dx, largo, abre) in listOf(Triple(-2.2f, 4.6f, -0.5f), Triple(-0.8f, 5.6f, -0.2f), Triple(0.6f, 5.8f, 0.05f), Triple(2.0f, 5.0f, 0.36f))) drawLine(pared, centro + Offset(dx * talla, 0.4f * talla), centro + Offset((dx + abre * 2.2f) * talla, -largo * talla), 1.5f * talla, StrokeCap.Round)
        drawLine(pared, centro + Offset(-2.4f * talla, 2.6f * talla), centro + Offset(-5.4f * talla, 0.4f * talla), 1.7f * talla, StrokeCap.Round)
    }

    /** El friso entero, como va hoy: las manos, los puntos y las figuras. */
    fun DrawScope.friso(m: Momento, z: LuzCueva) {
        val k = EscenaCueva.friso(m)
        if (EscenaCueva.manoDeAma(m)) mano(z, Offset(74f, 73f), 1f)
        if (EscenaCueva.manoDeLin(m)) mano(z, Offset(90f, 77f), 0.64f)
        val cuantos = puntos(k)
        if (cuantos > 0) {
            val rojo = z.en(Offset(34f, 74f), C.OCRE_ROJO).copy(alpha = 0.9f)
            drawPoints((0 until cuantos).map { j -> Offset(22f + (j % 7) * 4.6f + (j / 7) * 2.2f, 72f + (j / 7) * 5.4f + (j % 2) * 0.5f) }, PointMode.Points, rojo, 2.3f, StrokeCap.Round)
        }
        for (i in FIGURAS.indices) figura(z, FIGURAS[i], avance(i, k))
    }
}
