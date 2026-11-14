package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los animales y los árboles del valle y de la meseta de Ama: lo que comparten los dos cuadros.
 *
 * **[Aproximación]**: el caballo salvaje, el reno, el bisonte de la estepa, el saiga, el mamut lanudo y el león de las
 * cavernas son los que nombra su calendario (con su fuente en `AnioG3.kt`). Que en el fondo de un valle abrigado del
 * suroeste de Europa hubiera abedules, pinos y enebros sueltos en esos años es lo que se suele contar de esos
 * paisajes, pero no se comprobó contra ninguna fuente en esta sesión; la estepa de arriba va sin árboles, como dice
 * el cuento.
 */
internal object FaunaCueva {
    private val C = ColorCueva

    /** Una línea suave y cerrada que pasa por todos los [puntos] (curvas de Catmull-Rom). */
    fun cerrada(puntos: List<Offset>): Path {
        val p = Path(); val n = puntos.size
        if (n < 3) return p
        p.moveTo(puntos[0].x, puntos[0].y)
        for (i in 0 until n) {
            val a = puntos[(i - 1 + n) % n]; val b = puntos[i]; val c = puntos[(i + 1) % n]; val d = puntos[(i + 2) % n]
            p.cubicTo(b.x + (c.x - a.x) / 6f, b.y + (c.y - a.y) / 6f, c.x - (d.x - b.x) / 6f, c.y - (d.y - b.y) / 6f, c.x, c.y)
        }
        p.close()
        return p
    }

    /** Una mancha de borde irregular alrededor de [centro]: una masa de hojas, una peña, un ventisquero. */
    fun masa(centro: Offset, rx: Float, ry: Float, az: Azar, puntas: Int = 9, quiebro: Float = 0.28f): Path =
        cerrada((0 until puntas).map { i -> val a = i * 6.2832f / puntas + az.entre(-0.2f, 0.2f); val k = 1f + az.entre(-quiebro, quiebro); centro + Offset(cos(a) * rx * k, sin(a) * ry * k) })

    // ---------------------------------------------------------------- Los animales

    internal enum class Bicho { CABALLO, RENO, BISONTE, SAIGA, MAMUT, LEON }

    private class Figura(val cuerpo: List<Pair<Float, Float>>, val patas: List<List<Pair<Float, Float>>>, val grueso: Float, val pelo: Color, val oscuro: Color, val claro: Color)

    private val CABALLO = Figura(
        listOf(0.08f to -0.98f, 0.45f to -0.93f, 0.80f to -1.0f, 1.02f to -1.24f, 1.16f to -1.36f, 1.26f to -1.30f, 1.42f to -1.08f, 1.36f to -1.0f, 1.20f to -1.08f, 1.08f to -0.96f, 1.0f to -0.66f, 0.84f to -0.52f, 0.5f to -0.48f, 0.22f to -0.54f, 0.02f to -0.74f),
        listOf(listOf(0.92f to -0.6f, 0.95f to -0.3f, 0.93f to 0f), listOf(0.80f to -0.56f, 0.76f to -0.28f, 0.79f to 0f), listOf(0.14f to -0.64f, 0.06f to -0.32f, 0.13f to 0f), listOf(0.26f to -0.58f, 0.31f to -0.3f, 0.28f to 0f)),
        0.085f, Color(0xFFC09A62), Color(0xFF3A2C24), Color(0xFFE6D2A6))
    private val RENO = Figura(
        listOf(0.06f to -0.98f, 0.5f to -0.96f, 0.82f to -1.0f, 1.04f to -1.12f, 1.18f to -1.22f, 1.28f to -1.16f, 1.44f to -1.02f, 1.38f to -0.96f, 1.16f to -1.0f, 1.04f to -0.76f, 0.94f to -0.64f, 0.5f to -0.56f, 0.2f to -0.6f, 0.0f to -0.78f),
        listOf(listOf(0.9f to -0.62f, 0.93f to -0.32f, 0.9f to 0f), listOf(0.78f to -0.6f, 0.72f to -0.3f, 0.76f to 0f), listOf(0.12f to -0.66f, 0.04f to -0.34f, 0.1f to 0f), listOf(0.24f to -0.62f, 0.3f to -0.32f, 0.26f to 0f)),
        0.062f, Color(0xFF8E7860), Color(0xFF4A3A30), Color(0xFFE8E0D0))
    private val BISONTE = Figura(
        listOf(0.06f to -0.9f, 0.45f to -1.0f, 0.86f to -1.34f, 1.1f to -1.14f, 1.24f to -0.98f, 1.34f to -0.84f, 1.36f to -0.6f, 1.26f to -0.56f, 1.16f to -0.42f, 1.0f to -0.5f, 0.5f to -0.5f, 0.2f to -0.52f, 0.0f to -0.7f),
        listOf(listOf(0.96f to -0.54f, 0.98f to -0.26f, 0.96f to 0f), listOf(0.84f to -0.52f, 0.8f to -0.26f, 0.83f to 0f), listOf(0.12f to -0.58f, 0.06f to -0.3f, 0.12f to 0f), listOf(0.24f to -0.54f, 0.29f to -0.28f, 0.26f to 0f)),
        0.09f, Color(0xFF6E5038), Color(0xFF2E2420), Color(0xFF9A7A56))
    private val MAMUT = Figura(
        listOf(0.1f to -1.0f, 0.5f to -1.22f, 0.86f to -1.5f, 1.04f to -1.42f, 1.2f to -1.6f, 1.36f to -1.4f, 1.42f to -1.1f, 1.46f to -0.5f, 1.38f to -0.2f, 1.34f to -0.6f, 1.26f to -0.9f, 1.12f to -0.6f, 0.6f to -0.5f, 0.2f to -0.5f, 0.02f to -0.7f),
        listOf(listOf(1.02f to -0.6f, 1.04f to -0.3f, 1.03f to 0f), listOf(0.86f to -0.56f, 0.84f to -0.28f, 0.86f to 0f), listOf(0.14f to -0.56f, 0.12f to -0.28f, 0.15f to 0f), listOf(0.32f to -0.54f, 0.34f to -0.28f, 0.33f to 0f)),
        0.14f, Color(0xFF7A5438), Color(0xFF34261E), Color(0xFFA47C54))
    private val LEON = Figura(
        listOf(0.04f to -0.86f, 0.5f to -0.88f, 0.9f to -0.94f, 1.1f to -1.0f, 1.24f to -1.08f, 1.32f to -1.0f, 1.44f to -0.86f, 1.38f to -0.76f, 1.14f to -0.74f, 1.02f to -0.56f, 0.5f to -0.52f, 0.2f to -0.5f, 0.0f to -0.66f),
        listOf(listOf(0.96f to -0.56f, 0.99f to -0.28f, 0.98f to 0f), listOf(0.82f to -0.54f, 0.78f to -0.28f, 0.82f to 0f), listOf(0.12f to -0.56f, 0.05f to -0.28f, 0.12f to 0f), listOf(0.26f to -0.52f, 0.31f to -0.26f, 0.28f to 0f)),
        0.085f, Color(0xFFC8A870), Color(0xFF5A4630), Color(0xFFEADBB6))

    private fun figura(b: Bicho): Figura = when (b) {
        Bicho.CABALLO -> CABALLO
        Bicho.RENO -> RENO
        Bicho.BISONTE -> BISONTE
        Bicho.SAIGA -> RENO
        Bicho.MAMUT -> MAMUT
        Bicho.LEON -> LEON
    }

    /**
     * Un animal que pisa en [pie], con [alto] de altura hasta la cruz. [mira]: 1, hacia la derecha; −1, a la izquierda.
     * [paso], de 0 a 1: cómo lleva las patas. [hundido], de 0 a 1: cuánto de las patas tapa el agua o la nieve.
     * [pasta]: con la cabeza baja, comiendo.
     */
    fun DrawScope.animal(s: SolCueva, b: Bicho, pie: Offset, alto: Float, mira: Float, lejos: Float, paso: Float = 0f, hundido: Float = 0f, pasta: Boolean = false) {
        val f = figura(b); val h = alto
        fun p(x: Float, y: Float) = Offset(pie.x + (x - 0.7f) * h * mira, pie.y + y * h)
        // La cabeza baja de quien pasta: los puntos de delante de la cruz giran hacia el suelo.
        fun q(x: Float, y: Float): Offset = if (!pasta || x <= 0.95f || b == Bicho.MAMUT) p(x, y) else { val dx = x - 0.95f; val dy = y + 0.9f; p(0.95f + dx * 0.72f - dy * 0.5f, -0.9f + dy * 0.5f + dx * 1.25f) }
        val pelo = if (b == Bicho.SAIGA) Color(0xFFC6A678) else f.pelo
        val medio = s.medio(pelo, lejos); val luz = s.luz(lerp(pelo, f.claro, 0.3f), lejos); val sombra = s.sombra(lerp(pelo, f.oscuro, 0.45f), lejos); val osc = s.medio(f.oscuro, lejos)
        // La sombra en el suelo, corta, hacia el lado contrario al sol.
        if (s.r.fuerza > 0.1f && hundido < 0.5f) drawOval(s.l.tono(C.SOMBRA, lejos).copy(alpha = 0.3f * s.r.fuerza), Offset(pie.x - h * 0.7f + s.r.ax.coerceIn(-1.4f, 1.4f) * h * 0.3f, pie.y - h * 0.07f), Size(h * 1.4f, h * 0.16f))
        // Las patas: las del otro lado, más oscuras; cada par se cruza según el paso.
        val g = f.grueso * h; val corta = 1f - hundido
        for ((j, pata) in f.patas.withIndex()) {
            val lejana = j % 2 == 1; val corre = (if (j == 0 || j == 3) 1f else -1f) * (paso - 0.5f) * 0.2f
            val a = p(pata[0].first, pata[0].second); val rod = p(pata[1].first + corre * 0.5f, pata[1].second); val c = p(pata[2].first + corre, pata[2].second * corta - (1f - corta) * 0.02f)
            val fin = if (hundido > 0f) Offset(rod.x + (c.x - rod.x) * corta, rod.y + (c.y - rod.y) * corta) else c
            val color = if (lejana) sombra else if (b == Bicho.CABALLO) lerp(medio, osc, 0.45f) else medio
            drawLine(color, a, rod, g * (if (lejana) 0.85f else 1f), StrokeCap.Round); drawLine(if (b == Bicho.CABALLO || b == Bicho.BISONTE) osc else color, rod, fin, g * 0.62f, StrokeCap.Round)
        }
        val cuerpo = cerrada(f.cuerpo.map { (x, y) -> q(x, y) })
        drawPath(cuerpo, medio)
        clipPath(cuerpo) {
            drawRect(Brush.verticalGradient(0f to luz, 0.4f to luz.copy(alpha = 0f), 0.62f to sombra.copy(alpha = 0f), 1f to sombra, startY = pie.y - h * 1.5f, endY = pie.y - h * 0.45f), Offset(pie.x - h * 1.2f, pie.y - h * 1.7f), Size(h * 2.4f, h * 1.4f))
            when (b) {
                // El vientre y el hocico claros del caballo de la estepa.
                Bicho.CABALLO -> { drawOval(s.luz(f.claro, lejos).copy(alpha = 0.7f), Offset(p(0.3f, 0f).x.coerceAtMost(p(0.9f, 0f).x), pie.y - h * 0.62f), Size(h * 0.6f, h * 0.2f)); drawCircle(s.luz(f.claro, lejos).copy(alpha = 0.8f), h * 0.1f, q(1.38f, -1.04f)) }
                // El cuello y el pecho blancos del reno, y su grupa.
                Bicho.RENO -> { drawOval(s.luz(f.claro, lejos).copy(alpha = 0.85f), Offset(minOf(p(0.9f, 0f).x, p(1.2f, 0f).x), pie.y - h * 1.1f), Size(h * 0.3f, h * 0.5f)); drawCircle(s.luz(f.claro, lejos).copy(alpha = 0.7f), h * 0.12f, p(0.04f, -0.84f)) }
                // La delantera lanuda y oscura del bisonte.
                Bicho.BISONTE -> drawOval(osc.copy(alpha = 0.75f), Offset(minOf(p(0.78f, 0f).x, p(1.4f, 0f).x), pie.y - h * 1.4f), Size(h * 0.62f, h * 1.0f))
                Bicho.SAIGA -> drawOval(s.luz(f.claro, lejos).copy(alpha = 0.6f), Offset(minOf(p(0.3f, 0f).x, p(0.9f, 0f).x), pie.y - h * 0.66f), Size(h * 0.6f, h * 0.16f))
                // El pelo largo del mamut, que le cuelga del vientre.
                Bicho.MAMUT -> for (k in 0..9) { val x = 0.16f + k * 0.1f; drawLine(osc.copy(alpha = 0.6f), p(x, -0.9f + 0.04f * (k % 3)), p(x - 0.03f, -0.46f), g * 0.3f) }
                Bicho.LEON -> drawOval(s.luz(f.claro, lejos).copy(alpha = 0.6f), Offset(minOf(p(0.3f, 0f).x, p(1.0f, 0f).x), pie.y - h * 0.66f), Size(h * 0.7f, h * 0.16f))
            }
        }
        when (b) {
            Bicho.CABALLO -> {
                // La crin tiesa y corta, y la cola.
                drawPath(Path().apply { val a = q(0.8f, -1.0f); val c = q(1.02f, -1.26f); val d = q(1.16f, -1.38f); moveTo(a.x, a.y); lineTo(c.x - 0.02f * h * mira, c.y - 0.07f * h); lineTo(d.x - 0.02f * h * mira, d.y - 0.07f * h); lineTo(d.x, d.y); lineTo(c.x, c.y); close() }, osc)
                drawPath(Path().apply { val a = p(0.05f, -0.92f); moveTo(a.x, a.y); cubicTo(a.x - 0.14f * h * mira, a.y + 0.1f * h, a.x - 0.12f * h * mira, a.y + 0.4f * h, a.x - 0.06f * h * mira, a.y + 0.6f * h) }, osc, style = Stroke(g * 0.9f, cap = StrokeCap.Round))
            }
            Bicho.RENO -> {
                // Las astas: dos varas que se van hacia atrás y vuelven hacia delante, con sus puntas.
                val base = q(1.17f, -1.22f)
                for (k in 0..1) {
                    val d = k * 0.05f * h * mira
                    val vara = Path().apply { moveTo(base.x + d, base.y); cubicTo(base.x - 0.26f * h * mira + d, base.y - 0.3f * h, base.x - 0.3f * h * mira + d, base.y - 0.66f * h, base.x + 0.08f * h * mira + d, base.y - 0.8f * h) }
                    drawPath(vara, s.medio(lerp(C.HUESO, f.oscuro, 0.4f + 0.2f * k), lejos), style = Stroke(g * 0.42f, cap = StrokeCap.Round))
                    for ((u, v) in listOf(-0.2f to -0.36f, -0.22f to -0.56f, -0.06f to -0.76f)) drawLine(s.medio(lerp(C.HUESO, f.oscuro, 0.4f + 0.2f * k), lejos), Offset(base.x + u * h * mira + d, base.y + v * h), Offset(base.x + (u - 0.12f) * h * mira + d, base.y + (v - 0.14f) * h), g * 0.32f, StrokeCap.Round)
                    drawLine(s.medio(lerp(C.HUESO, f.oscuro, 0.4f), lejos), base, Offset(base.x + 0.2f * h * mira, base.y - 0.16f * h), g * 0.34f, StrokeCap.Round)
                }
            }
            Bicho.BISONTE -> {
                val cuerno = q(1.22f, -1.02f)
                drawPath(Path().apply { moveTo(cuerno.x, cuerno.y); cubicTo(cuerno.x + 0.02f * h * mira, cuerno.y - 0.16f * h, cuerno.x + 0.12f * h * mira, cuerno.y - 0.2f * h, cuerno.x + 0.14f * h * mira, cuerno.y - 0.26f * h) }, s.medio(C.HUESO, lejos), style = Stroke(g * 0.5f, cap = StrokeCap.Round))
                drawLine(osc, p(0.03f, -0.82f), p(-0.04f, -0.5f), g * 0.4f, StrokeCap.Round)
            }
            Bicho.SAIGA -> {
                val base = q(1.2f, -1.2f)
                for (k in 0..1) drawLine(s.medio(lerp(C.HUESO, C.PIEL, 0.3f), lejos), base + Offset(k * 0.04f * h * mira, 0f), base + Offset((0.02f + k * 0.05f) * h * mira, -0.3f * h), g * 0.4f, StrokeCap.Round)
                drawCircle(s.sombra(pelo, lejos), h * 0.07f, q(1.42f, -0.98f))
            }
            Bicho.MAMUT -> {
                // Los colmillos, largos y vueltos hacia arriba.
                val base = p(1.3f, -0.94f)
                drawPath(Path().apply { moveTo(base.x, base.y); cubicTo(base.x + 0.16f * h * mira, base.y + 0.4f * h, base.x + 0.46f * h * mira, base.y + 0.36f * h, base.x + 0.44f * h * mira, base.y - 0.06f * h) }, s.luz(C.HUESO, lejos), style = Stroke(g * 0.42f, cap = StrokeCap.Round))
                drawCircle(osc, h * 0.03f, p(1.3f, -1.34f))
            }
            Bicho.LEON -> {
                val a = p(0.03f, -0.8f)
                drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x - 0.2f * h * mira, a.y + 0.06f * h, a.x - 0.34f * h * mira, a.y + 0.34f * h, a.x - 0.5f * h * mira, a.y + 0.2f * h) }, medio, style = Stroke(g * 0.5f, cap = StrokeCap.Round))
                drawPath(poligono(q(1.2f, -1.06f), q(1.25f, -1.16f), q(1.29f, -1.04f)), sombra)
            }
        }
        // El ojo, si está cerca.
        if (h > 12f) drawCircle(osc, h * 0.022f, q(if (b == Bicho.MAMUT) 1.3f else 1.26f, if (b == Bicho.MAMUT) -1.34f else if (b == Bicho.BISONTE) -0.86f else -1.2f))
    }

    /** Una manada: [cuantos] animales repartidos alrededor de [centro], en un llano que va de [y0] (lejos) a [y1] (cerca). */
    fun DrawScope.manada(s: SolCueva, b: Bicho, cuantos: Int, x0: Float, x1: Float, y0: Float, y1: Float, alto0: Float, alto1: Float, lejos: Float, az: Azar, mira: Float, hundido: Float = 0f) {
        val bichos = (0 until cuantos).map { val k = az.real(); Triple(az.entre(x0, x1), k, az) }.sortedBy { it.second }
        for ((x, k, _) in bichos) {
            val alto = alto0 + (alto1 - alto0) * k
            animal(s, b, Offset(x, y0 + (y1 - y0) * k), alto * az.entre(0.85f, 1.1f), if (az.entero(6) == 0) -mira else mira, lejos * (1f - 0.4f * k), az.real(), hundido, pasta = hundido == 0f && az.entero(3) == 0)
        }
    }

    // ---------------------------------------------------------------- Los árboles

    private fun hojaDeAbedul(e: Estacion): Color? = when (e) {
        Estacion.PRIMAVERA -> lerp(C.VERDE, C.OCRE_AMARILLO, 0.3f)
        Estacion.VERANO -> C.VERDE
        Estacion.OTONO -> C.OCRE_AMARILLO
        Estacion.INVIERNO -> null
        Estacion.SECAS -> C.VERDE
        Estacion.LLUVIAS -> C.VERDE
        Estacion.INUNDACION -> C.VERDE
        Estacion.SIEMBRA -> C.VERDE
        Estacion.COSECHA -> C.VERDE
        Estacion.NINGUNA -> C.VERDE
    }

    /**
     * Un racimo de hojas menudas: una sombra blanda de borde roto y, encima, las hojas una a una (en tandas de puntos):
     * las de abajo en sombra, las del cuerpo y, arriba y del lado del sol, las que cogen la luz. Deja ver el cielo.
     */
    private fun DrawScope.racimo(s: SolCueva, centro: Offset, rx: Float, ry: Float, hoja: Color, lejos: Float, az: Azar) {
        val hacia = if (s.solALaDerecha) 1f else -1f; val g = (rx * 0.2f).coerceIn(0.9f, 2.6f)
        drawPath(masa(centro + Offset(0f, ry * 0.16f), rx * 0.86f, ry * 0.8f, az, 9, 0.4f), s.sombra(lerp(hoja, C.PINO, 0.3f), lejos).copy(alpha = 0.6f))
        val n = (rx * 2.6f).toInt().coerceIn(8, 34)
        fun hojas(cuantas: Int, dx: Float, dy: Float, kx: Float, ky: Float) = (0 until cuantas).map { val a = az.entre(0f, 6.28f); val d = az.real().let { 1f - it * it }; centro + Offset(dx * rx + cos(a) * rx * kx * d, dy * ry + sin(a) * ry * ky * d) }
        drawPoints(hojas(n, 0f, 0.3f, 1.12f, 1.1f), PointMode.Points, s.sombra(lerp(hoja, C.PINO, 0.25f), lejos), g, StrokeCap.Round)
        drawPoints(hojas(n, hacia * 0.06f, -0.04f, 1.08f, 1.08f), PointMode.Points, s.medio(hoja, lejos), g, StrokeCap.Round)
        drawPoints(hojas(n * 2 / 3, hacia * 0.26f, -0.3f, 0.8f, 0.8f), PointMode.Points, s.luz(hoja, lejos), g * 0.9f, StrokeCap.Round)
        drawPoints(hojas(n / 3, hacia * 0.4f, -0.5f, 0.5f, 0.5f), PointMode.Points, s.luz(lerp(hoja, C.HUESO, 0.3f), lejos), g * 0.7f, StrokeCap.Round)
    }

    /** Una rama que sale de [desde] con el [angulo] dado y se parte en ramillas cada vez más finas, que cuelgan en la punta. */
    private fun DrawScope.rama(desde: Offset, angulo: Float, largo: Float, grueso: Float, color: Color, az: Azar, nivel: Int, puntas: MutableList<Offset>) {
        val fin = desde + Offset(cos(angulo) * largo, sin(angulo) * largo)
        val medio = desde + Offset(cos(angulo - 0.14f) * largo * 0.55f, sin(angulo - 0.14f) * largo * 0.55f)
        drawPath(Path().apply { moveTo(desde.x, desde.y); cubicTo(medio.x, medio.y, medio.x, medio.y, fin.x, fin.y) }, color, style = Stroke(grueso, cap = StrokeCap.Round))
        if (nivel <= 0) { puntas.add(fin); return }
        val hijas = if (nivel >= 2) 3 else 2
        for (j in 0 until hijas) rama(if (j == 0) fin else medio + (fin - medio) * az.entre(0.1f, 0.8f), angulo + az.entre(-0.75f, 0.75f) + (if (nivel == 1) 0.25f * (if (cos(angulo) > 0f) 1f else -1f) else 0f), largo * az.entre(0.5f, 0.72f), (grueso * 0.58f).coerceAtLeast(0.35f), color, az, nivel - 1, puntas)
    }

    /**
     * Un abedul con el [pie] en el suelo: el tronco blanco con sus marcas oscuras, algo vencido, las ramas finas que
     * cuelgan en las puntas y, según la estación, las hojas en racimos o las ramillas peladas (con nieve, si [nevado]).
     */
    fun DrawScope.abedul(s: SolCueva, pie: Offset, alto: Float, estacion: Estacion, az: Azar, nevado: Boolean = false, lejos: Float = 0f, vence: Float = 0.08f) {
        val cima = pie + Offset(alto * vence, -alto); val cruz = pie + Offset(alto * vence * 0.5f, -alto * 0.46f)
        val g = alto * 0.034f
        val blanco = lerp(C.HUESO, Color.White, 0.3f)
        val tronco = Path().apply {
            moveTo(pie.x - g, pie.y); cubicTo(pie.x - g * 0.6f, pie.y - alto * 0.2f, cruz.x - g * 0.8f, cruz.y + alto * 0.1f, cruz.x - g * 0.5f, cruz.y)
            cubicTo(cruz.x - g * 0.2f, cruz.y - alto * 0.2f, cima.x - g * 0.2f, cima.y + alto * 0.2f, cima.x, cima.y)
            cubicTo(cima.x + g * 0.3f, cima.y + alto * 0.2f, cruz.x + g * 0.7f, cruz.y - alto * 0.2f, cruz.x + g * 0.6f, cruz.y)
            cubicTo(cruz.x + g * 0.9f, cruz.y + alto * 0.1f, pie.x + g * 0.8f, pie.y - alto * 0.2f, pie.x + g * 1.3f, pie.y); close()
        }
        val claro = s.luz(blanco, lejos); val oscuro = s.sombra(lerp(blanco, C.ROCA, 0.3f), lejos)
        drawPath(tronco, Brush.horizontalGradient(if (s.solALaDerecha) listOf(oscuro, claro) else listOf(claro, oscuro), pie.x - g * 1.2f, pie.x + g * 1.4f + alto * vence))
        // Las marcas de la corteza: rayas oscuras cortas, más juntas hacia el pie.
        clipPath(tronco) { repeat((alto / 4f).toInt().coerceIn(6, 40)) { val k = az.real(); val y = pie.y - alto * 0.9f * k * k; val x = pie.x + alto * vence * (pie.y - y) / alto; drawLine(s.medio(C.HOLLIN, lejos).copy(alpha = 0.75f), Offset(x - g * az.entre(0.2f, 1.2f), y), Offset(x + g * az.entre(0f, 1f), y + az.entre(-0.4f, 0.4f)), (0.5f + 0.7f * (1f - k)) * alto / 110f + 0.3f) } }
        drawPath(poligono(pie + Offset(-g * 1.4f, 0f), pie + Offset(g * 1.7f, 0f), pie + Offset(g * 0.8f, -alto * 0.05f), pie + Offset(-g * 0.7f, -alto * 0.06f)), s.sombra(lerp(C.HOLLIN, C.ROCA, 0.4f), lejos))
        // El ramaje.
        val ramilla = s.medio(lerp(C.PIEL_OSC, C.SOMBRA, 0.35f), lejos); val puntas = mutableListOf<Offset>()
        val niveles = if (alto > 60f) 3 else 2
        for ((k, ang, lar) in listOf(Triple(0.46f, -2.3f, 0.26f), Triple(0.56f, -0.75f, 0.28f), Triple(0.68f, -2.05f, 0.24f), Triple(0.78f, -0.95f, 0.22f), Triple(0.88f, -1.9f, 0.16f), Triple(0.97f, -1.3f, 0.12f)))
            rama(pie + Offset(alto * vence * k, -alto * k), ang + az.entre(-0.15f, 0.15f), alto * lar, (g * 0.62f * (1.2f - k)).coerceAtLeast(0.5f), ramilla, az, niveles, puntas)
        val hoja = hojaDeAbedul(estacion)
        if (hoja == null) {
            // Peladas: de cada punta cuelgan unas ramillas finísimas.
            for (q in puntas) for (j in 0..1) { val d = Offset(az.entre(-3f, 3f) * alto / 110f, az.entre(3f, 9f) * alto / 110f); drawLine(ramilla.copy(alpha = 0.7f), q, q + d, 0.35f, StrokeCap.Round) }
            if (nevado) drawPoints(puntas.filterIndexed { i, _ -> i % 2 == 0 }.map { it + Offset(0f, -0.6f) }, PointMode.Points, s.luz(C.NIEVE, lejos), (alto / 60f).coerceIn(1f, 2.4f), StrokeCap.Round)
        } else {
            val menos = if (estacion == Estacion.OTONO) 2 else 1
            for ((i, q) in puntas.withIndex()) if (i % menos == 0) racimo(s, q + Offset(0f, alto * 0.025f), alto * az.entre(0.055f, 0.09f), alto * az.entre(0.05f, 0.075f), hoja, lejos, az)
        }
    }

    /**
     * Un pino de monte: el tronco rojizo a la vista, torcido, y la copa en pisos de masas irregulares, oscuras por
     * debajo y con la luz arriba; con nieve posada, si [nevado].
     */
    fun DrawScope.pinoDeMonte(s: SolCueva, pie: Offset, alto: Float, az: Azar, nevado: Boolean = false, lejos: Float = 0f) {
        val g = alto * 0.04f; val vence = az.entre(-0.08f, 0.08f) * alto
        val cima = pie + Offset(vence, -alto * 0.82f)
        val tronco = lerp(C.OCRE_ROJO, C.MADERA, 0.6f)
        drawPath(poligono(pie + Offset(-g, 0f), pie + Offset(g * 1.1f, 0f), cima + Offset(g * 0.35f, 0f), cima + Offset(-g * 0.35f, 0f)), Brush.horizontalGradient(if (s.solALaDerecha) listOf(s.sombra(tronco, lejos), s.luz(tronco, lejos)) else listOf(s.luz(tronco, lejos), s.sombra(tronco, lejos)), pie.x - g, pie.x + g))
        val pisos = if (alto > 30f) 4 else 3
        for (i in 0 until pisos) {
            val k = i / (pisos - 1f); val c = pie + Offset(vence * (0.5f + 0.5f * k) + az.entre(-0.06f, 0.06f) * alto, -alto * (0.5f + 0.44f * k))
            val rx = alto * (0.3f - 0.14f * k) * az.entre(0.85f, 1.15f); val ry = alto * 0.085f
            // Una rama que asoma por debajo de cada piso.
            drawLine(s.sombra(tronco, lejos), Offset(pie.x + vence * (0.5f + 0.5f * k), c.y + ry), c + Offset(rx * 0.7f * (if (i % 2 == 0) -1f else 1f), ry * 0.3f), g * 0.4f, StrokeCap.Round)
            drawPath(masa(c + Offset(0f, ry * 0.3f), rx, ry, az, 9, 0.26f), s.sombra(lerp(C.PINO, C.HOLLIN, 0.25f), lejos))
            drawPath(masa(c + Offset(if (s.solALaDerecha) rx * 0.1f else -rx * 0.1f, -ry * 0.2f), rx * 0.86f, ry * 0.7f, az, 9, 0.28f), s.medio(C.PINO, lejos))
            drawPath(masa(c + Offset(if (s.solALaDerecha) rx * 0.3f else -rx * 0.3f, -ry * 0.5f), rx * 0.5f, ry * 0.36f, az, 7, 0.3f), s.luz(lerp(C.PINO, C.VERDE, 0.5f), lejos).copy(alpha = 0.85f))
            if (nevado) drawPath(masa(c + Offset(0f, -ry * 0.62f), rx * 0.7f, ry * 0.3f, az, 7, 0.3f), s.luz(C.NIEVE, lejos))
            // Las agujas del fleco.
            if (alto > 22f) drawPoints((0 until 10).map { val a = az.entre(0f, 6.28f); c + Offset(cos(a) * rx * 1.04f, sin(a) * ry * 1.1f) }, PointMode.Points, s.medio(C.PINO, lejos), (alto / 50f).coerceIn(0.7f, 1.4f), StrokeCap.Round)
        }
    }

    /** Un enebro: una mata baja y oscura, de borde picudo. */
    fun DrawScope.enebro(s: SolCueva, pie: Offset, ancho: Float, az: Azar, nevado: Boolean = false, lejos: Float = 0f) {
        val c = pie + Offset(0f, -ancho * 0.3f)
        drawPath(masa(c + Offset(0f, ancho * 0.06f), ancho * 0.52f, ancho * 0.32f, az, 11, 0.36f), s.sombra(lerp(C.PINO, C.HOLLIN, 0.3f), lejos))
        drawPath(masa(c + Offset(if (s.solALaDerecha) ancho * 0.08f else -ancho * 0.08f, -ancho * 0.06f), ancho * 0.4f, ancho * 0.22f, az, 10, 0.4f), s.medio(lerp(C.PINO, C.VERDE, 0.25f), lejos))
        if (nevado) drawPath(masa(c + Offset(0f, -ancho * 0.2f), ancho * 0.34f, ancho * 0.1f, az, 7, 0.3f), s.luz(C.NIEVE, lejos))
    }

    /** Una peña con su cara de luz, su cara de sombra y, si hay nieve, su gorro. */
    fun DrawScope.pena(s: SolCueva, pie: Offset, ancho: Float, alto: Float, az: Azar, nieve: Float = 0f, lejos: Float = 0f, propio: Color = C.ROCA) {
        val cima = pie + Offset(ancho * az.entre(-0.14f, 0.14f), -alto)
        val izq = pie + Offset(-ancho * 0.5f, 0f); val der = pie + Offset(ancho * 0.5f, 0f)
        val hombroI = pie + Offset(-ancho * az.entre(0.36f, 0.46f), -alto * az.entre(0.5f, 0.8f)); val hombroD = pie + Offset(ancho * az.entre(0.34f, 0.46f), -alto * az.entre(0.4f, 0.7f))
        drawPath(poligono(izq, hombroI, cima, pie + Offset(ancho * 0.04f, 0f)), s.lado(lerp(propio, C.CALIZA, 0.3f), lejos, alReves = true))
        drawPath(poligono(pie + Offset(ancho * 0.04f, 0f), cima, hombroD, der), s.lado(lerp(propio, C.CALIZA, 0.3f), lejos))
        drawLine(s.sombra(propio, lejos).copy(alpha = 0.5f), cima, pie + Offset(ancho * 0.04f, 0f), 0.5f)
        if (nieve > 0.25f) drawPath(poligono(hombroI + Offset(ancho * 0.04f, 0f), cima + Offset(0f, -alto * 0.08f * nieve), hombroD + Offset(-ancho * 0.04f, 0f), cima + Offset(ancho * 0.1f, alto * 0.16f), cima + Offset(-ancho * 0.12f, alto * 0.14f)), s.luz(C.NIEVE, lejos))
    }
}
