package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Las piezas de Abismo: las aves y lo de tierra
// =====================================================================
//
// El pelícano y la garza (posados, nadando y en vuelo), la palmera, el barco esponjero amarrado en tres cuartos, las casas
// de madera del pueblo griego, los cafés, la lonja de las esponjas, la iglesia de San Nicolás, el farol del muelle, la
// bita, el pilote, la mesa de café con sus sillas, los sacos de esponjas y la red tendida. Los cuerpos se arman en metros
// y se proyectan con la perspectiva de Mongol (`Sitio`), en tres cuartos y con su lado de luz y de sombra; las aves y las
// palmeras se dibujan en el lienzo, pluma a pluma y hoja a hoja.

internal object PiezasAbismoTierra {
    private val C = ColorAbismo

    private fun lado(p: Pincel): Float = if (p.solALaDerecha) 1f else -1f

    // =================================================================
    //  Las aves
    // =================================================================

    /** Cómo está un ave: posada en un pilote o en la regala, nadando en el agua, o en vuelo. */
    enum class Postura { POSADO, NADANDO, VOLANDO }

    /**
     * Un pelícano pardo de Florida, de [k] unidades por metro (mide algo más de un metro), con el [pie] donde se apoya
     * (en el agua, su línea de flotación) y la cabeza hacia [mira] (±1). El cuerpo pardo grisáceo con las plumas del lomo
     * plateadas en escamas, la cabeza blanca amarillenta, la nuca parda, el pico largo con su bolsa apoyado en el pecho, la
     * punta de las alas oscura. Nadando, bajo en el agua, «a mirar, como viejos en una plaza»; posado, de pie, con las
     * patas oscuras; en vuelo, las alas largas tendidas, planeando.
     */
    fun DrawScope.pelicano(p: Pincel, pie: Offset, k: Float, mira: Float, postura: Postura, semilla: Long, lejos: Float = 0f) {
        val az = Azar(semilla); val m = mira
        val u = k
        val pardo = lerp(C.PLOMO, C.MADERA_OSC, 0.45f); val plata = lerp(C.PLOMO, C.BLANCO, 0.45f)
        val cabeza = lerp(C.BLANCO, C.ESPONJA, 0.25f); val pico = lerp(C.ESPONJA_OSC, C.ROJO, 0.2f)
        val ld = lado(p)
        fun q(x: Float, y: Float) = Offset(pie.x + m * x * u, pie.y + y * u)
        when (postura) {
            Postura.VOLANDO -> {
                val ala = Path().apply {
                    moveTo(q(-0.95f, -0.02f)); cubicTo(q(-0.6f, -0.14f), q(-0.25f, -0.1f), q(0f, 0f))
                    cubicTo(q(0.25f, -0.1f), q(0.6f, -0.16f), q(0.98f, -0.05f)); cubicTo(q(0.6f, -0.02f), q(0.25f, 0.06f), q(0f, 0.08f))
                    cubicTo(q(-0.25f, 0.06f), q(-0.6f, 0.02f), q(-0.95f, -0.02f)); close()
                }
                drawPath(ala, Brush.verticalGradient(listOf(p.luz(pardo, lejos), p.sombra(pardo, lejos)), pie.y - 0.15f * u, pie.y + 0.08f * u))
                for (s in listOf(-1f, 1f)) drawLine(p.sombra(C.BREA, lejos), q(s * 0.75f, -0.05f), q(s * 0.97f, -0.04f), 0.05f * u, StrokeCap.Round)
                drawOval(p.medio(pardo, lejos), Offset(pie.x - 0.2f * u, pie.y - 0.04f * u), Size(0.4f * u, 0.11f * u))
                drawCircle(p.luz(cabeza, lejos), 0.06f * u, q(0.22f, -0.01f))
                drawLine(p.medio(pico, lejos), q(0.26f, 0.0f), q(0.55f, 0.03f), 0.04f * u, StrokeCap.Round)
            }
            Postura.NADANDO, Postura.POSADO -> {
                val alto = if (postura == Postura.POSADO) 0.25f else 0f
                if (postura == Postura.POSADO) {
                    for (s in listOf(-0.06f, 0.06f)) { drawLine(p.sombra(C.BREA, lejos), q(s, -0.02f), q(s + 0.02f, -alto), 0.035f * u, StrokeCap.Round); drawLine(p.sombra(C.BREA, lejos), q(s - 0.06f, 0f), q(s + 0.06f, 0f), 0.03f * u, StrokeCap.Round) }
                }
                val y0 = -alto
                val cuerpo = Path().apply {
                    moveTo(q(-0.48f, y0 - 0.12f)); cubicTo(q(-0.35f, y0 - 0.3f), q(0.1f, y0 - 0.34f), q(0.28f, y0 - 0.22f))
                    cubicTo(q(0.36f, y0 - 0.1f), q(0.26f, y0 + 0.02f), q(0.05f, y0 + 0.03f)); cubicTo(q(-0.2f, y0 + 0.03f), q(-0.4f, y0 - 0.02f), q(-0.48f, y0 - 0.12f)); close()
                }
                drawPath(cuerpo, Brush.verticalGradient(listOf(p.luz(plata, lejos), p.medio(pardo, lejos), p.sombra(pardo, lejos)), pie.y + (y0 - 0.32f) * u, pie.y + (y0 + 0.03f) * u))
                clipPath(cuerpo) {
                    for (i in 0 until 7) for (j in 0 until 2) { val c = q(-0.32f + i * 0.08f + j * 0.04f, y0 - 0.2f + j * 0.06f); drawArc(p.sombra(pardo, lejos).copy(alpha = 0.6f), 0f, 180f, false, Offset(c.x - 0.04f * u, c.y - 0.02f * u), Size(0.08f * u, 0.05f * u), style = Stroke(0.012f * u)) }
                    drawPath(Path().apply { moveTo(q(-0.5f, y0 - 0.12f)); lineTo(q(-0.25f, y0 - 0.2f)); lineTo(q(-0.2f, y0 - 0.08f)); close() }, p.sombra(C.BREA, lejos))
                }
                if (postura == Postura.NADANDO) drawLine(p.luz(C.MAR_CLARO, lejos).copy(alpha = 0.6f), q(-0.42f, 0.02f), q(0.3f, 0.02f), 0.025f * u, StrokeCap.Round)
                val cuello = Path().apply { moveTo(q(0.16f, y0 - 0.24f)); cubicTo(q(0.32f, y0 - 0.34f), q(0.18f, y0 - 0.5f), q(0.26f, y0 - 0.6f)) }
                drawPath(cuello, p.medio(lerp(cabeza, pardo, 0.35f), lejos), style = Stroke(0.1f * u, cap = StrokeCap.Round))
                drawPath(cuello, p.luz(cabeza, lejos).copy(alpha = 0.7f), style = Stroke(0.045f * u, cap = StrokeCap.Round))
                val cab = q(0.27f, y0 - 0.62f)
                drawOval(p.luz(cabeza, lejos), Offset(cab.x - 0.06f * u, cab.y - 0.05f * u), Size(0.12f * u, 0.1f * u))
                drawCircle(p.sombra(C.BREA, lejos), 0.012f * u, cab + Offset(m * 0.02f * u, -0.01f * u))
                val bolsa = Path().apply { moveTo(cab + Offset(m * 0.04f * u, 0.02f * u)); cubicTo(q(0.42f, y0 - 0.48f), q(0.42f, y0 - 0.34f), q(0.38f, y0 - 0.28f)); lineTo(q(0.33f, y0 - 0.3f)); cubicTo(q(0.33f, y0 - 0.42f), q(0.3f, y0 - 0.52f), cab); close() }
                drawPath(bolsa, Brush.horizontalGradient(listOf(p.luz(pico, lejos), p.sombra(pico, lejos)), cab.x, cab.x + m * 0.15f * u))
                if (az.real() > 0.5f) drawLine(p.sombra(C.MADERA_OSC, lejos), cab + Offset(-m * 0.05f * u, -0.03f * u), q(0.2f, y0 - 0.45f), 0.03f * u, StrokeCap.Round)
                if (ld * m < 0) drawPath(cuerpo, p.sombra(pardo, lejos).copy(alpha = 0.15f))
            }
        }
    }

    /**
     * Una garza azulada, de [k] unidades por metro (mide algo más de un metro de alto), posada en un pilote (con el [pie]
     * donde apoya) o en vuelo, con el cuello recogido y las patas largas atrás. Gris azulada, con la cabeza blanca y su
     * lista negra, el pico como un puñal, amarillo.
     */
    fun DrawScope.garza(p: Pincel, pie: Offset, k: Float, mira: Float, postura: Postura, lejos: Float = 0f) {
        val u = k; val m = mira
        fun q(x: Float, y: Float) = Offset(pie.x + m * x * u, pie.y + y * u)
        val gris = lerp(C.PLOMO, C.AZUL, 0.25f)
        when (postura) {
            Postura.VOLANDO -> {
                val ala = Path().apply { moveTo(q(-0.8f, -0.1f)); cubicTo(q(-0.4f, -0.3f), q(0f, -0.15f), q(0.1f, 0f)); cubicTo(q(0.4f, -0.2f), q(0.7f, -0.25f), q(0.9f, -0.1f)); cubicTo(q(0.5f, 0f), q(0.2f, 0.08f), q(0f, 0.08f)); cubicTo(q(-0.3f, 0.06f), q(-0.6f, 0f), q(-0.8f, -0.1f)); close() }
                drawPath(ala, Brush.verticalGradient(listOf(p.luz(gris, lejos), p.sombra(gris, lejos)), pie.y - 0.3f * u, pie.y + 0.1f * u))
                drawLine(p.sombra(gris, lejos), q(-0.1f, 0.02f), q(-0.75f, 0.04f), 0.025f * u, StrokeCap.Round)
                drawCircle(p.luz(C.BLANCO, lejos), 0.05f * u, q(0.25f, 0.02f))
                drawLine(p.medio(C.ESPONJA, lejos), q(0.28f, 0.02f), q(0.45f, 0.04f), 0.02f * u, StrokeCap.Round)
            }
            Postura.POSADO, Postura.NADANDO -> {
                for (s in listOf(-0.04f, 0.04f)) drawLine(p.sombra(lerp(C.ESPONJA_OSC, C.BREA, 0.4f), lejos), q(s, 0f), q(s * 0.5f, -0.45f), 0.02f * u, StrokeCap.Round)
                val cuerpo = Path().apply { moveTo(q(-0.28f, -0.6f)); cubicTo(q(-0.2f, -0.82f), q(0.12f, -0.85f), q(0.16f, -0.68f)); cubicTo(q(0.14f, -0.52f), q(0f, -0.44f), q(-0.12f, -0.46f)); cubicTo(q(-0.22f, -0.5f), q(-0.3f, -0.52f), q(-0.28f, -0.6f)); close() }
                drawPath(cuerpo, Brush.verticalGradient(listOf(p.luz(gris, lejos), p.sombra(gris, lejos)), pie.y - 0.85f * u, pie.y - 0.44f * u))
                val cuello = Path().apply { moveTo(q(0.1f, -0.74f)); cubicTo(q(0.22f, -0.82f), q(0.05f, -0.95f), q(0.16f, -1.08f)) }
                drawPath(cuello, p.luz(lerp(gris, C.BLANCO, 0.5f), lejos), style = Stroke(0.06f * u, cap = StrokeCap.Round))
                val cab = q(0.17f, -1.1f)
                drawOval(p.luz(C.BLANCO, lejos), Offset(cab.x - 0.045f * u, cab.y - 0.035f * u), Size(0.09f * u, 0.07f * u))
                drawLine(p.sombra(C.BREA, lejos), cab + Offset(-m * 0.04f * u, -0.02f * u), cab + Offset(-m * 0.12f * u, 0.0f), 0.015f * u, StrokeCap.Round)
                drawLine(p.medio(C.ESPONJA, lejos), cab + Offset(m * 0.03f * u, 0f), cab + Offset(m * 0.2f * u, 0.02f * u), 0.025f * u, StrokeCap.Round)
            }
        }
    }

    // =================================================================
    //  La palmera
    // =================================================================

    /**
     * Un palmito de Florida (sabal), de pie en [pie], de [alto] unidades: el tronco gris con sus anillos y las «botas» de
     * las hojas viejas en cruz por arriba, curvado; la falda de hojas secas colgando bajo la copa; la copa de hojas en
     * abanico, una por una, cada una con sus pliegues que salen del centro y las puntas partidas, las de atrás más oscuras.
     * [viento] las peina hacia un lado.
     */
    fun DrawScope.palmera(p: Pincel, pie: Offset, alto: Float, semilla: Long, lejos: Float = 0f, viento: Float = 0f) {
        val az = Azar(semilla)
        val ld = lado(p)
        val curva = az.entre(-0.14f, 0.14f) * alto
        val copa = Offset(pie.x + curva + viento * alto * 0.05f, pie.y - alto)
        val g = (alto * 0.042f).coerceAtLeast(0.5f)
        val tronco = Path().apply { moveTo(pie.x - g * 1.15f, pie.y); cubicTo(pie.x - g, pie.y - alto * 0.4f, copa.x - g * 0.8f + curva * 0.1f, copa.y + alto * 0.35f, copa.x - g * 0.8f, copa.y + alto * 0.05f); lineTo(copa.x + g * 0.8f, copa.y + alto * 0.05f); cubicTo(copa.x + g * 0.8f + curva * 0.1f, copa.y + alto * 0.35f, pie.x + g, pie.y - alto * 0.4f, pie.x + g * 1.15f, pie.y); close() }
        val gris = lerp(C.PLOMO, C.MADERA_GRIS, 0.55f)
        drawPath(tronco, Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(gris, lejos) else p.luz(gris, lejos), p.medio(gris, lejos), if (ld > 0) p.luz(gris, lejos) else p.sombra(gris, lejos)), pie.x - g, pie.x + g))
        if (alto > 18f) clipPath(tronco) {
            var y = pie.y - g; while (y > copa.y + alto * 0.3f) { drawLine(p.sombra(gris, lejos).copy(alpha = 0.4f), Offset(pie.x - alto, y), Offset(pie.x + alto, y + g * 0.3f), g * 0.12f); y -= g * az.entre(0.6f, 1.1f) }
            for (i in 0 until 10) {
                val yy = copa.y + alto * (0.05f + 0.025f * i); val x = copa.x + curva * 0.02f * i
                drawLine(p.medio(lerp(gris, C.MADERA, 0.3f), lejos), Offset(x - g, yy + g * 0.5f), Offset(x + g * 0.3f, yy - g * 0.4f), g * 0.35f)
                drawLine(p.sombra(lerp(gris, C.MADERA_OSC, 0.3f), lejos), Offset(x + g, yy + g * 0.5f), Offset(x - g * 0.3f, yy - g * 0.4f), g * 0.3f)
            }
        }
        // La falda de hojas secas, colgando bajo la copa.
        for (i in 0 until 7) {
            val a = PI.toFloat() * (0.12f + 0.76f * az.real()); val l = alto * az.entre(0.12f, 0.22f)
            val b = copa + Offset(cos(a) * l * 0.45f, alto * 0.05f + sin(a) * l)
            drawLine(p.medio(lerp(C.ESPONJA_OSC, C.MADERA, 0.4f), lejos), copa + Offset(0f, alto * 0.03f), b, (alto * 0.022f).coerceAtLeast(0.4f), StrokeCap.Round)
        }
        // La copa: abanicos en tres pisos, los de atrás más oscuros.
        for (piso in 0..2) {
            val n = listOf(8, 7, 5)[piso]
            for (i in 0 until n) {
                val a = -PI.toFloat() * (0.02f + 0.96f * (i + az.entre(0.1f, 0.9f)) / n) + (if (piso == 0) 0f else az.entre(-0.12f, 0.12f))
                val largo = alto * az.entre(0.2f, 0.3f) * (1f - 0.12f * piso)
                val cae = if (sin(a) > -0.3f) 0.45f else 0.1f
                val punta = copa + Offset(cos(a) * largo + viento * largo * 0.4f, sin(a) * largo * 0.72f + cae * largo * 0.5f)
                val tono = when (piso) { 0 -> lerp(C.PALMA, C.BREA, 0.35f); 1 -> C.PALMA; else -> lerp(C.PALMA, C.ESPONJA, 0.22f) }
                abanico(p, copa, punta, largo * 0.55f, tono, lejos, az, lado = if (cos(a) * ld > 0) 1f else -1f)
            }
        }
    }

    /** Una hoja en abanico de [base] a [punta], de [ancho]: los pliegues que salen del pecíolo, cada uno con su lado de luz, y las puntas partidas. */
    private fun DrawScope.abanico(p: Pincel, base: Offset, punta: Offset, ancho: Float, tono: Color, lejos: Float, az: Azar, lado: Float) {
        val d = punta - base; val largo = d.getDistance().coerceAtLeast(0.1f); val u = d / largo; val n = Offset(-u.y, u.x)
        val c0 = base + d * 0.35f
        drawLine(p.medio(lerp(tono, C.MADERA, 0.3f), lejos), base, c0, (ancho * 0.06f).coerceAtLeast(0.35f), StrokeCap.Round)
        val segs = if (ancho > 4f) 9 else 5
        for (j in 0 until segs) {
            val t = (j + 0.5f) / segs - 0.5f
            val fin = c0 + u * (largo * 0.65f * (1f - 0.5f * t * t)) + n * (ancho * t * 1.6f) + Offset(0f, largo * 0.15f * t * t)
            val luz = if (t * lado > 0) p.luz(tono, lejos) else p.sombra(tono, lejos)
            drawLine(lerp(luz, p.medio(tono, lejos), 0.3f), c0, fin, (ancho * 0.16f).coerceAtLeast(0.45f), StrokeCap.Round)
            if (ancho > 6f) drawLine(luz, fin, fin + (fin - c0) * 0.12f + Offset(0f, ancho * 0.05f), (ancho * 0.04f).coerceAtLeast(0.3f), StrokeCap.Round)
        }
        if (az.real() > 0.85f) drawLine(p.medio(lerp(C.ESPONJA, C.PALMA, 0.4f), lejos).copy(alpha = 0.6f), c0, c0 + u * largo * 0.5f, (ancho * 0.12f).coerceAtLeast(0.4f), StrokeCap.Round)
    }

    // =================================================================
    //  Las esponjas secas, en ristra
    // =================================================================

    /**
     * Una ristra de esponjas secas, «doradas y blandas», colgada de [arriba] a lo largo de [largo] unidades: la cuerda y
     * muchas esponjas chicas apretadas (de [r] unidades de radio, cada una distinta en tamaño y en tono: las de lana, las
     * amarillas, alguna más oscura), con su lado de luz y la sombra de una sobre otra. Más ligera que la ristra de a bordo:
     * desde el muelle se ve de lejos y a contraluz. [racimo] las junta abajo; [sesgo] tuerce la cuerda.
     */
    fun DrawScope.ristraSeca(p: Pincel, arriba: Offset, largo: Float, r: Float, semilla: Long, lejos: Float = 0f, racimo: Boolean = false, sesgo: Float = 0f) {
        val az = Azar(semilla)
        val abajo = arriba + Offset(sesgo * largo, largo)
        drawLine(p.sombra(C.CANAMO, lejos), arriba, abajo, (r * 0.18f).coerceAtLeast(0.35f), StrokeCap.Round)
        val ld = lado(p)
        val n = ((largo / (r * 1.25f)).toInt()).coerceIn(4, 26)
        for (i in 0 until n) {
            val t0 = if (racimo) 0.25f + 0.75f * (i + 0.5f) / n else 0.06f + 0.94f * (i + 0.5f) / n
            val q = arriba + (abajo - arriba) * t0 + Offset(az.entre(-0.5f, 0.5f) * r * (if (racimo) 1f + 0.8f * t0 else 0.7f), 0f)
            val rr = r * az.entre(0.7f, 1.25f) * (if (racimo) 0.85f + 0.4f * t0 else 1f)
            val propio = lerp(lerp(C.ESPONJA, listOf(C.ARENA, C.ESPONJA_OSC, C.LONA, C.ESPONJA)[az.entero(4)], az.entre(0f, 0.45f)), C.MADERA, az.entre(0f, 0.15f))
            val forma = if (rr < 2.2f) Path().apply { addOval(Rect(q, rr)) } else lazo((0 until 7).map { val a = it * 0.8976f + az.entre(-0.2f, 0.2f); val k = az.entre(0.8f, 1.1f); Offset(q.x + cos(a) * rr * k * 1.08f, q.y + sin(a) * rr * k * 0.9f) })
            drawPath(forma, Brush.radialGradient(0f to p.luz(propio, lejos), 0.55f to p.medio(propio, lejos), 1f to p.sombra(propio, lejos), center = q + Offset(ld * rr * 0.4f, -rr * 0.4f), radius = rr * 1.5f))
            if (rr > 2.6f) { drawCircle(p.sombra(lerp(propio, C.BREA, 0.4f), lejos).copy(alpha = 0.55f), rr * 0.13f, q + Offset(-ld * rr * 0.25f, rr * 0.15f)); drawCircle(p.luz(lerp(propio, C.BLANCO, 0.4f), lejos).copy(alpha = 0.5f), rr * 0.1f, q + Offset(ld * rr * 0.35f, -rr * 0.3f)) }
        }
    }

    // =================================================================
    //  El barco esponjero amarrado
    // =================================================================

    /** Medio ancho del casco a la distancia [z] de la popa, de un barco de [largo] y [manga]: el espejo, la panza y la proa afilada. */
    private fun media(z: Float, largo: Float, manga: Float): Float {
        val t = (z / largo).coerceIn(0f, 1f)
        return manga / 2f * if (t < 0.4f) 0.8f + 0.2f * sqrt(t / 0.4f) else sqrt((1f - ((t - 0.4f) / 0.6f).let { it * it * it }).coerceAtLeast(0f))
    }

    /** El arrufo: la altura de la regala sobre el agua a la distancia [z] de la popa (la proa más alta). */
    private fun arrufo(z: Float, largo: Float, franco: Float): Float { val t = (z / largo).coerceIn(0f, 1f); return franco * (1.05f - 0.25f * t + 0.55f * t * t * t) }

    /**
     * Cómo es un barco de la fila: cada uno distinto ([semilla]). El largo y la manga, el blanco del casco, el color de la
     * franja y de la regala, la altura de los palos, si lleva toldo en la popa, la caseta y lo que hay en cubierta.
     */
    class Goleta(semilla: Long) {
        private val az = Azar(semilla)
        val largo = az.entre(12f, 14.5f)
        val manga = az.entre(3.9f, 4.5f)
        val franco = az.entre(1.0f, 1.25f)
        val blanco: Color = lerp(C.BLANCO, listOf(C.ARENA, C.LONA, C.BLANCO, C.ARENA)[az.entero(4)], az.entre(0f, 0.3f))
        val franja: Color = listOf(C.AZUL, lerp(C.AZUL, C.MAR_HONDO, 0.4f), C.ROJO, lerp(C.HIERBA, C.MAR_HONDO, 0.4f), C.AZUL)[az.entero(5)]
        val regala: Color = if (az.real() > 0.3f) C.AZUL else lerp(C.AZUL, C.BLANCO, 0.35f)
        val palo1 = az.entre(9.2f, 10.8f)
        val palo2 = az.entre(7.6f, 9.0f)
        val toldo = az.real() > 0.55f
        val sacos = az.entero(4)
        val barriles = az.entero(3)
        val caseta: Color = lerp(listOf(C.BLANCO, C.MADERA_GRIS, lerp(C.AZUL, C.BLANCO, 0.5f))[az.entero(3)], C.MADERA, 0.15f)
        val pintado = az.real() > 0.5f                                                                  // el casco recién pintado o gastado
        val semilla2 = semilla * 13 + 7
    }

    /**
     * Un barco esponjero amarrado, de [g], visto desde popa en tres cuartos: «una goleta pequeña, de dos palos, pintada de
     * blanco y azul», con la proa río arriba. En [s] (su z, hacia proa; la popa en z = 0, a la altura del agua): el
     * costado de estribor que se ve (si [costado]), con su tablazón, la franja, la regala y la flotación verde; el espejo
     * redondeado con el timón y la caña; la cubierta, la caseta, la bomba tapada, los sacos y los barriles; los dos palos
     * con la vela aferrada en su botavara, la jarcia, el bauprés; las ristras de esponjas ([carga], de 0 a 1); el toldo de
     * lona sobre la popa; la lamparilla del icono de popa ([lumbre]); el empavesado y los farolillos; las banderas del 4 de
     * julio; con [zarpa], la vela mayor suelta e izándose.
     */
    fun DrawScope.goleta(p: Pincel, s: Sitio, sol: Sol3, g: Goleta, carga: Float, lumbre: Float, lejos: Float, costado: Boolean = true,
                         empavesado: Boolean = false, farolillos: Boolean = false, banderas: Boolean = false, luto: Boolean = false, zarpa: Boolean = false) {
        val az = Azar(g.semilla2); val e = s.escala(0f, 0f); val L = g.largo; val M = g.manga; val fr = g.franco
        val zs = (0..12).map { L * it / 12f }
        val vida = lejos
        fun gr(w: Float, min: Float = 0.3f) = (w * e).coerceAtLeast(min)
        val tc = sol.t(s.normal(V3(1f, 0f, 0f)))
        // El costado de estribor: de la regala al agua, con la curva del arrufo.
        if (costado) {
            val regala = zs.map { V3(media(it, L, M), arrufo(it, L, fr), it) }
            val flot = zs.map { V3(media(it, L, M) * 0.9f, 0f, it) }
            val forma = poligono((regala + flot.reversed()).map { s.p(it) })
            drawPath(forma, p.por(g.blanco, tc, vida))
            clipPath(forma) {
                // La franja de color bajo la regala y la flotación verde de algas.
                drawPath(poligono(regala.map { s.p(it) } + regala.reversed().map { s.p(it.copy(y = it.y - 0.24f)) }), p.por(g.franja, tc, vida))
                drawPath(poligono(flot.map { s.p(it.copy(x = it.x * 1.02f, y = 0.18f)) } + flot.reversed().map { s.p(it.copy(y = -0.1f)) }), p.medio(lerp(C.VERDIN, C.BREA, 0.35f), vida))
                // La tablazón: las juntas de las tablas, que siguen el arrufo.
                if (e > 6f) for (h in listOf(0.3f, 0.5f, 0.7f)) drawPath(curva(zs.map { s.p(V3(media(it, L, M) * (0.92f + 0.08f * h), arrufo(it, L, fr) * h, it)) }), p.sombra(g.blanco, vida).copy(alpha = 0.5f), style = Stroke(gr(0.012f)))
                // Las chorreras de óxido y de sal, la pintura saltada.
                if (e > 5f) repeat(if (g.pintado) 3 else 7) { val z = az.entre(0.5f, L * 0.8f); val y0 = arrufo(z, L, fr) - 0.28f; drawLine(p.sombra(lerp(g.blanco, C.COBRE_OSC, 0.35f), vida).copy(alpha = 0.4f), s.p(V3(media(z, L, M), y0, z)), s.p(V3(media(z, L, M) * 0.95f, y0 - az.entre(0.3f, 0.6f), z)), gr(0.02f)) }
            }
            // El canto de la regala, con su filo de luz.
            drawPath(curva(regala.map { s.p(it) }), p.por(g.regala, tc + 0.3f, vida), style = Stroke(gr(0.07f, 0.5f), cap = StrokeCap.Round))
            // Las defensas de cuerda colgadas de la borda.
            if (e > 7f) for (z in listOf(L * 0.25f, L * 0.55f)) { val q = s.p(V3(media(z, L, M) + 0.05f, arrufo(z, L, fr) - 0.35f, z)); drawOval(p.por(C.CANAMO, tc, vida), Offset(q.x - 0.12f * e, q.y - 0.2f * e), Size(0.24f * e, 0.4f * e)) }
        }
        // La cubierta, vista un poco desde arriba, y la cara de dentro de la borda de babor.
        val bordeE = zs.map { V3(media(it, L, M) * 0.97f, arrufo(it, L, fr) - 0.05f, it) }
        val bordeB = zs.map { V3(-media(it, L, M) * 0.97f, arrufo(it, L, fr) - 0.05f, it) }
        val cub = poligono((bordeE + bordeB.reversed()).map { s.p(it) })
        drawPath(cub, p.arriba(lerp(C.MADERA, C.MADERA_GRIS, 0.5f), vida))
        drawPath(curva(bordeB.map { s.p(it) }), p.por(g.regala, 0.2f, vida), style = Stroke(gr(0.06f, 0.4f)))
        // Lo de cubierta: la caseta de popa (baja, con su ventanuco y su puerta), la bomba tapada con su lona, los barriles y los sacos.
        val caseta = carasDeCaja(-0.72f, 0.72f, fr - 0.1f, fr + 0.72f, L * 0.2f, L * 0.33f)
        for (cara in visibles(s, caseta)) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(if (cara.arriba) lerp(g.caseta, C.MADERA_GRIS, 0.4f) else g.caseta, t, vida))
            if (e > 7f && !cara.arriba) clipPath(f) {
                if (cara.n.z < -0.5f) { linea(s, cara, 0.18f, 0.0f, 0.18f, 0.8f, p.sombra(C.BREA, vida), gr(0.32f)); linea(s, cara, 0.6f, 0.45f, 0.82f, 0.45f, p.sombra(lerp(C.AZUL, C.BREA, 0.5f), vida), gr(0.18f)) }
                else linea(s, cara, 0.35f, 0.5f, 0.65f, 0.5f, p.sombra(lerp(C.AZUL, C.BREA, 0.5f), vida), gr(0.16f))
            }
        }
        val bq = s.p(V3(0.15f, fr - 0.05f, L * 0.46f)); val rb = 0.42f * e
        drawPath(lazo(listOf(bq + Offset(-rb, 0f), bq + Offset(-rb * 0.9f, -rb * 0.8f), bq + Offset(-rb * 0.2f, -rb * 1.05f), bq + Offset(rb * 0.6f, -rb * 0.9f), bq + Offset(rb, -rb * 0.3f), bq + Offset(rb * 0.8f, rb * 0.05f))), Brush.verticalGradient(listOf(p.luz(C.LONA_MOJADA, vida), p.sombra(C.LONA_MOJADA, vida)), bq.y - rb, bq.y))
        repeat(g.barriles) { val q = s.p(V3(-0.9f + it * 0.45f, fr - 0.1f, L * 0.4f)); val r = 0.22f * e; drawRect(Brush.horizontalGradient(listOf(p.luz(C.MADERA, vida), p.sombra(C.MADERA_OSC, vida)), q.x - r, q.x + r), Offset(q.x - r, q.y - 0.62f * e), Size(2f * r, 0.62f * e)); if (e > 8f) drawLine(p.sombra(C.PLOMO, vida), Offset(q.x - r, q.y - 0.5f * e), Offset(q.x + r, q.y - 0.5f * e), gr(0.03f)) }
        val nSacos = g.sacos + (carga * 6f).toInt()
        repeat(nSacos) { val q = s.p(V3(az.entre(-1.1f, 1.1f), fr - 0.05f, az.entre(L * 0.52f, L * 0.75f))); val r = 0.26f * e * az.entre(0.8f, 1.2f); drawOval(Brush.verticalGradient(listOf(p.luz(C.LONA, vida), p.sombra(C.LONA, vida)), q.y - r, q.y), Offset(q.x - r, q.y - r * 1.1f), Size(r * 2f, r * 1.2f)) }
        // El espejo de popa: redondeado abajo, blanco con la franja y la regala, la raya del nombre (sin letras) y el timón.
        val w0 = media(0f, L, M)
        val espejo = Path().apply {
            moveTo(s.p(V3(-w0, arrufo(0f, L, fr), 0f))); lineTo(s.p(V3(w0, arrufo(0f, L, fr), 0f)))
            cubicTo(s.p(V3(w0 * 0.98f, fr * 0.5f, 0.02f)), s.p(V3(w0 * 0.7f, 0.05f, 0.1f)), s.p(V3(0f, -0.05f, 0.18f)))
            cubicTo(s.p(V3(-w0 * 0.7f, 0.05f, 0.1f)), s.p(V3(-w0 * 0.98f, fr * 0.5f, 0.02f)), s.p(V3(-w0, arrufo(0f, L, fr), 0f))); close()
        }
        val te = sol.t(s.normal(V3(0f, 0.15f, -1f)))
        drawPath(espejo, p.por(g.blanco, te, vida))
        clipPath(espejo) {
            drawPath(poligono(s.p(V3(-w0 * 1.2f, fr + 0.3f, 0f)), s.p(V3(w0 * 1.2f, fr + 0.3f, 0f)), s.p(V3(w0 * 1.2f, fr - 0.24f, 0f)), s.p(V3(-w0 * 1.2f, fr - 0.24f, 0f))), p.por(g.franja, te, vida))
            drawLine(p.sombra(C.BREA, vida).copy(alpha = 0.55f), s.p(V3(-0.55f, fr - 0.45f, 0.02f)), s.p(V3(0.55f, fr - 0.45f, 0.02f)), gr(0.06f))
            drawPath(poligono(s.p(V3(-w0 * 1.2f, 0.16f, 0.1f)), s.p(V3(w0 * 1.2f, 0.16f, 0.1f)), s.p(V3(w0 * 1.2f, -0.1f, 0.1f)), s.p(V3(-w0 * 1.2f, -0.1f, 0.1f))), p.medio(lerp(C.VERDIN, C.BREA, 0.35f), vida))
        }
        drawLine(p.por(g.regala, te + 0.3f, vida), s.p(V3(-w0, arrufo(0f, L, fr), 0f)), s.p(V3(w0, arrufo(0f, L, fr), 0f)), gr(0.07f, 0.5f), StrokeCap.Round)
        val ti0 = s.p(V3(0f, fr + 0.2f, -0.12f)); val ti1 = s.p(V3(0f, -0.25f, -0.32f))
        drawLine(p.sombra(C.MADERA_OSC, vida), ti0, ti1, gr(0.16f, 0.6f))
        drawLine(p.medio(C.MADERA, vida), ti0, s.p(V3(0.1f, fr + 0.5f, 1.3f)), gr(0.06f, 0.4f), StrokeCap.Round)
        // El toldo de lona sobre la popa: a dos aguas sobre su caballete, con la tela vencida y el dobladillo.
        if (g.toldo) {
            val t0 = fr + 1.95f; val z1 = L * 0.19f; val bordeT = fr + 1.4f
            val cumbre0 = s.p(V3(0f, t0, 0.15f)); val cumbre1 = s.p(V3(0f, t0, z1))
            for (x in listOf(-w0 * 0.92f, w0 * 0.92f)) drawLine(p.sombra(C.MADERA_OSC, vida), s.p(V3(x, fr, 0.2f)), s.p(V3(x, bordeT, 0.2f)), gr(0.04f))
            drawLine(p.sombra(C.MADERA_OSC, vida), s.p(V3(0f, fr, 0.15f)), cumbre0, gr(0.05f))
            for (sl in listOf(-1f, 1f)) {
                val faldon = curva(listOf(cumbre0, s.p(V3(sl * w0 * 0.95f, bordeT, 0.15f)), s.p(V3(sl * w0 * 0.97f, bordeT - 0.08f, z1 * 0.5f)), s.p(V3(sl * w0 * 0.95f, bordeT, z1)), cumbre1))
                drawPath(faldon, p.por(lerp(C.LONA, C.BLANCO, 0.3f), if (sl > 0) 0.45f else -0.1f, vida))
                drawLine(p.sombra(C.LONA, vida), s.p(V3(sl * w0 * 0.95f, bordeT, 0.15f)), s.p(V3(sl * w0 * 0.95f, bordeT, z1)), gr(0.035f))
            }
            drawLine(p.medio(C.MADERA, vida), cumbre0, cumbre1, gr(0.05f, 0.4f))
        }
        // Los dos palos (el mayor a popa), con su vela aferrada en la botavara, la jarcia y el bauprés.
        val palos = listOf(Triple(L * 0.36f, g.palo1, 1), Triple(L * 0.7f, g.palo2, 2))
        val topes = ArrayList<Offset>()
        for ((zp, h, n) in palos) {
            val pie = s.p(V3(0f, fr, zp)); val tope = s.p(V3(0f, fr + h, zp + 0.25f)); topes += tope
            val gp = gr(0.2f, 0.5f)
            drawLine(p.medio(C.MADERA, vida), pie, tope, gp)
            drawLine(p.luz(C.MADERA, vida).copy(alpha = 0.6f), pie + Offset(gp * 0.25f * lado(p), 0f), tope + Offset(gp * 0.25f * lado(p), 0f), gp * 0.3f)
            // La jarcia: los obenques a las dos bordas.
            for (sl in listOf(-1f, 1f)) drawLine(p.sombra(C.CANAMO, vida).copy(alpha = 0.75f), s.p(V3(0f, fr + h * 0.86f, zp + 0.2f)), s.p(V3(sl * media(zp, L, M), arrufo(zp, L, fr), zp - 0.3f)), 0.3f)
            // La botavara hacia popa, con la vela aferrada encima (y el pico bajado).
            val largoB = if (n == 1) zp + 0.9f else zp - L * 0.4f
            val b0 = s.p(V3(0f, fr + 1.3f, zp)); val b1 = s.p(V3(0.05f, fr + 1.55f, zp - largoB))
            drawLine(p.sombra(C.MADERA_OSC, vida), b0, b1, gr(0.1f, 0.45f), StrokeCap.Round)
            if (zarpa && n == 1) {
                // La mayor suelta, a medio izar: un paño de lona que cuelga del pico hacia la botavara.
                val pico = s.p(V3(0f, fr + h * 0.7f, zp - largoB * 0.5f))
                drawPath(poligono(b0 + Offset(0f, -gp), pico, b1 + Offset(0f, -gp)), p.por(lerp(C.LONA, C.BLANCO, 0.35f), 0.4f, vida))
                drawLine(p.sombra(C.MADERA_OSC, vida), s.p(V3(0f, fr + h * 0.62f, zp)), pico, gr(0.07f, 0.4f))
            } else {
                val bulto = curva((0..6).map { i -> val t = i / 6f; b0 + (b1 - b0) * t + Offset(0f, -gr(0.16f, 0.6f) * (1f - 0.5f * t)) })
                drawPath(bulto, p.por(lerp(C.LONA, C.BLANCO, 0.2f), 0.35f, vida), style = Stroke(gr(0.3f, 0.9f), cap = StrokeCap.Round))
                if (e > 6f) for (i in 1 until 5) { val q = b0 + (b1 - b0) * (i / 5f); drawLine(p.sombra(C.CANAMO, vida), q + Offset(0f, -gr(0.3f)), q + Offset(0f, gr(0.05f)), gr(0.03f)) }
            }
            if (luto && n == 1) drawRect(p.medio(lerp(C.BREA, C.AZUL, 0.1f), vida), Offset(tope.x, tope.y + (pie.y - tope.y) * 0.45f), Size(gr(0.9f, 2f), gr(0.35f, 1f)))
        }
        // El estay, el bauprés y el amantillo.
        val proa = s.p(V3(0f, arrufo(L, L, fr) + 0.1f, L)); val baupres = s.p(V3(0f, arrufo(L, L, fr) + 0.4f, L + 2.4f))
        drawLine(p.medio(C.MADERA, vida), proa, baupres, gr(0.1f, 0.4f))
        drawLine(p.sombra(C.CANAMO, vida).copy(alpha = 0.7f), topes[1], baupres, 0.3f)
        drawLine(p.sombra(C.CANAMO, vida).copy(alpha = 0.6f), topes[0], topes[1], 0.3f)
        drawLine(p.sombra(C.CANAMO, vida).copy(alpha = 0.6f), topes[0], s.p(V3(0.05f, fr + 1.55f, L * 0.36f - (L * 0.36f + 0.9f))), 0.3f)
        // Las ristras de esponjas tendidas a secar de un palo a otro.
        val nR = (carga * 9f).toInt()
        for (i in 0 until nR) {
            val t = (i + 0.5f) / maxOf(nR, 1)
            val a = topes[0] + (topes[1] - topes[0]) * t + Offset(0f, (s.p(V3(0f, fr, 0f)).y - topes[0].y) * (0.42f + 0.05f * sin(t * PI.toFloat())))
            val lgo = e * az.entre(0.9f, 1.6f)
            drawLine(lerp(p.medio(C.ESPONJA, vida), p.sombra(C.ESPONJA, vida), az.real() * 0.6f), a, a + Offset(az.entre(-0.1f, 0.1f) * e, lgo), gr(0.2f, 0.7f), StrokeCap.Round)
        }
        // El empavesado: banderas de señales azules y blancas del bauprés a los topes y a la popa; los farolillos de fin de año.
        if (empavesado || farolillos) {
            val pts = combada(baupres, topes[1], gr(0.5f, 1f), 7) + combada(topes[1], topes[0], gr(0.3f, 0.6f), 5) + combada(topes[0], s.p(V3(0f, fr + 0.3f, -0.1f)), gr(0.5f, 1f), 7)
            pts.forEachIndexed { i, q ->
                if (farolillos) { val c = lerp(Color(0xFFFF9A50), Color(0xFFFFD27A), (i % 3) / 2f); drawCircle(c.copy(alpha = (0.45f + 0.55f * lumbre).coerceIn(0f, 1f)), gr(0.1f, 0.6f), q) }
                else drawRect(if (i % 2 == 0) p.luz(C.BLANCO, vida) else p.medio(C.AZUL, vida), Offset(q.x - gr(0.15f, 0.4f), q.y), Size(gr(0.3f, 0.8f), gr(0.4f, 1.1f)))
            }
        }
        // El 4 de julio: la bandera de barras con su cantón en el palo mayor y la azul y blanca en el trinquete (sin letras).
        if (banderas) { banderaBarras(p, topes[0], gr(1.1f, 4f), vida); banderaGriega(p, topes[1], gr(0.9f, 3.4f), vida) }
        // La lamparilla del icono de popa, encendida siempre; de noche se ve de lejos.
        val lamp = s.p(V3(w0 * 0.45f, fr + 0.45f, 0.15f))
        if (lumbre > 0.02f) resplandor(lamp, gr(1.1f, 3f), ((0.12f + 0.88f * p.l.noche) * lumbre).coerceIn(0f, 1f), C.LUMBRE)
        drawCircle(Color(0xFFFFD890).copy(alpha = ((0.3f + 0.7f * p.l.noche) * lumbre).coerceIn(0f, 1f)), gr(0.07f, 0.55f), lamp)
    }

    /** Dónde arde la lamparilla de popa de un barco de la fila (para su reflejo en el río). */
    fun lamparillaDePopa(s: Sitio, g: Goleta): Offset = s.p(V3(media(0f, g.largo, g.manga) * 0.45f, g.franco + 0.45f, 0.15f))

    /** Una bandera de barras rojas y blancas con su cantón azul y sus estrellas en puntos (sin letras), de [ancho], colgada de [tope]. */
    fun DrawScope.banderaBarras(p: Pincel, tope: Offset, ancho: Float, lejos: Float = 0f) {
        val alto = ancho * 0.55f; val o = tope + Offset(0f, ancho * 0.05f)
        for (i in 0 until 7) drawRect(if (i % 2 == 0) p.medio(C.ROJO, lejos) else p.luz(C.BLANCO, lejos), Offset(o.x, o.y + i * alto / 7f), Size(ancho, alto / 7f + 0.1f))
        drawRect(p.medio(C.AZUL, lejos), o, Size(ancho * 0.42f, alto * 0.54f))
        if (ancho > 6f) for (i in 0 until 3) for (j in 0 until 2) drawCircle(p.luz(C.BLANCO, lejos), ancho * 0.02f, o + Offset(ancho * (0.08f + 0.13f * i), alto * (0.14f + 0.24f * j)))
        drawRect(p.sombra(C.BREA, lejos).copy(alpha = 0.18f), Offset(o.x + ancho * 0.5f, o.y), Size(ancho * 0.5f, alto))
    }

    /** La bandera azul y blanca de la isla: nueve barras y el cantón con su cruz (sin letras), de [ancho], colgada de [tope]. */
    fun DrawScope.banderaGriega(p: Pincel, tope: Offset, ancho: Float, lejos: Float = 0f) {
        val alto = ancho * 0.6f; val o = tope + Offset(0f, ancho * 0.05f)
        for (i in 0 until 9) drawRect(if (i % 2 == 0) p.medio(C.AZUL, lejos) else p.luz(C.BLANCO, lejos), Offset(o.x, o.y + i * alto / 9f), Size(ancho, alto / 9f + 0.1f))
        drawRect(p.medio(C.AZUL, lejos), o, Size(ancho * 0.38f, alto * 5f / 9f))
        drawRect(p.luz(C.BLANCO, lejos), Offset(o.x + ancho * 0.15f, o.y), Size(ancho * 0.08f, alto * 5f / 9f))
        drawRect(p.luz(C.BLANCO, lejos), Offset(o.x, o.y + alto * 2f / 9f), Size(ancho * 0.38f, alto / 9f))
    }

    /**
     * El reflejo de un barco de la fila en el río quieto: el casco blanco y su franja, roto por las ondas, y los palos como
     * dos trazos largos que tiemblan hacia abajo. [agua] es la altura del agua (la del pie de [s]).
     */
    fun DrawScope.reflejoDeGoleta(p: Pincel, s: Sitio, g: Goleta, lejos: Float, az: Azar) {
        val L = g.largo; val M = g.manga; val e = s.escala(0f, 0f)
        val zs = (0..8).map { L * it / 8f }
        // El casco al revés: la regala del costado que se ve, reflejada bajo la flotación (la misma altura hacia abajo).
        val agua = zs.map { s.p(V3(media(it, L, M) * 0.95f, 0f, it)) }
        val hondo = zs.map { z -> val a = s.p(V3(media(z, L, M), 0f, z)); val b = s.p(V3(media(z, L, M), arrufo(z, L, g.franco), z)); Offset(a.x, a.y + (a.y - b.y) * 0.95f) }
        val espejo = poligono(agua + hondo.reversed() )
        val popa0 = s.p(V3(-media(0f, L, M), 0f, 0f)); val popa1 = s.p(V3(media(0f, L, M), 0f, 0f)); val hp = popa0.y - s.p(V3(0f, g.franco, 0f)).y
        val espejoPopa = poligono(popa0, popa1, popa1 + Offset(0f, hp), popa0 + Offset(0f, hp * 0.7f))
        // Roto por las ondas: trazos tendidos dentro de la silueta, que se corren y se apagan hacia abajo.
        val blanco = p.medio(g.blanco, lejos)
        val y0 = minOf(agua.minOf { it.y }, popa0.y); val y1 = maxOf(hondo.maxOf { it.y }, popa0.y + hp)
        val x0 = minOf(agua.minOf { it.x }, popa0.x, hondo.minOf { it.x }) - 3f; val x1 = maxOf(agua.maxOf { it.x }, popa1.x, hondo.maxOf { it.x }) + 3f
        val paso = (0.06f * e).coerceIn(0.7f, 1.6f)
        for (forma in listOf(espejo, espejoPopa)) clipPath(forma) {
            var y = y0
            while (y < y1) {
                val k = ((y - y0) / (y1 - y0).coerceAtLeast(1f))
                val franja = k > 0.72f
                if (az.real() > 0.18f + 0.3f * k) { val corre = az.entre(-1.5f, 1.5f) * (0.5f + 2f * k)
                    drawLine((if (franja) p.medio(g.franja, lejos) else blanco).copy(alpha = (0.55f - 0.25f * k).coerceIn(0f, 1f)), Offset(x0 + corre, y), Offset(x1 + corre, y), paso * 0.75f) }
                y += paso
            }
        }
        drawPath(curva(agua), p.medio(lerp(C.VERDIN, C.BREA, 0.35f), lejos).copy(alpha = 0.4f), style = Stroke((0.08f * e).coerceAtLeast(0.4f)))
        // Los palos: dos trazos largos que tiemblan hacia abajo, cada vez más rotos.
        for ((zp, h) in listOf(L * 0.36f to g.palo1, L * 0.7f to g.palo2)) {
            val pie = s.p(V3(0f, 0f, zp)); val largo = (pie.y - s.p(V3(0f, h, zp)).y) * 0.9f
            val pts = ArrayList<Offset>(); var y = pie.y + (s.p(V3(0f, 0f, zp)).y - s.p(V3(0f, g.franco, zp)).y)
            var i = 0
            while (y < pie.y + largo) { val k = (y - pie.y) / largo; pts += Offset(pie.x + (if (i % 2 == 0) 1f else -1f) * (0.3f + 2.2f * k) * az.entre(0.4f, 1f), y); y += 1.6f + 3f * k; i++ }
            for (j in 0 until pts.size - 1) { val k = j / pts.size.toFloat(); if (az.real() < 0.3f * k) continue
                drawLine(p.medio(C.MADERA_OSC, lejos).copy(alpha = (0.55f * (1f - k)).coerceIn(0f, 1f)), pts[j], pts[j + 1], (0.16f * e).coerceIn(0.5f, 1.4f) * (1f - 0.4f * k), StrokeCap.Round) }
        }
    }

    /** Un reflejo roto: trazos tendidos que se corren de lado y se abren con la hondura (como `reflejoRoto`, con la mano un poco más suelta). */
    private fun DrawScope.reflejo(x0: Float, x1: Float, y: Float, alto: Float, color: Color, alfa: Float, az: Azar, paso: Float) {
        var yy = y + 0.3f
        while (yy < y + alto) {
            val k = (yy - y) / alto
            val corre = (az.real() - 0.5f) * (0.6f + 4f * k)
            val merma = (x1 - x0) * 0.1f * k
            val a = alfa * (1f - k) * (1f - k * 0.3f)
            if (az.real() > 0.45f * k) drawLine(color.copy(alpha = a.coerceIn(0f, 1f)), Offset(x0 + merma + corre, yy), Offset(x1 - merma + corre, yy), paso * 0.6f, StrokeCap.Round)
            yy += paso * (1f + 0.6f * k)
        }
    }

    // =================================================================
    //  Las casas del pueblo
    // =================================================================

    /** Cómo es una casa de la otra orilla: de madera, de uno o dos pisos, con su galería, su tejado y sus ventanas. */
    class Casa(val largo: Float, val fondo: Float, val pisos: Int, val muro: Color, val tejado: Color, val postigos: Color, val galeria: Boolean, val aguas: Int, val semilla: Long)

    /**
     * Una casa de madera del pueblo griego, en [s] (su frente, la fachada, mira hacia −z; x a lo largo de la fachada, de
     * −largo/2 a largo/2). En tres cuartos: la fachada de tablas de chilla con sus juntas, las ventanas con sus postigos,
     * la puerta, la galería de postes con su baranda (si la lleva), el costado que se ve, el tejado (a dos aguas, con su
     * hastial, o a una) con su alero que da sombra, la chimenea. [ventanas], de 0 a 1: encendidas de noche.
     */
    fun DrawScope.casa(p: Pincel, s: Sitio, sol: Sol3, c: Casa, ventanas: Float, lejos: Float) {
        val az = Azar(c.semilla); val e = s.escala(0f, 0f)
        val w = c.largo / 2f; val alto = 3.1f * c.pisos + 0.4f
        fun gr(v: Float, min: Float = 0.25f) = (v * e).coerceAtLeast(min)
        for (cara in visibles(s, carasDeCaja(-w, w, 0f, alto, 0f, c.fondo))) {
            if (cara.arriba) continue
            val t = luzDe(s, sol, cara); val f = forma(s, cara)
            drawPath(f, p.por(c.muro, t, lejos))
            clipPath(f) {
                // Las tablas de chilla y el zócalo.
                if (e > 2.5f) { val n = (alto / 0.35f).toInt().coerceAtMost(26); for (k in 1 until n) linea(s, cara, 0f, k / n.toFloat(), 1f, k / n.toFloat(), p.sombra(c.muro, lejos).copy(alpha = 0.32f), gr(0.012f)) }
                linea(s, cara, 0f, 0.03f, 1f, 0.03f, p.sombra(lerp(c.muro, C.BREA, 0.3f), lejos), gr(0.2f, 0.6f))
                // Las manchas de la humedad y la pintura gastada.
                if (e > 3f) repeat(3) { val q = s.p(cara.en(az.real(), az.entre(0.05f, 0.5f))); mancha(q, gr(0.8f, 2f), gr(0.3f, 1f), p.sombra(c.muro, lejos), 0.25f) }
                // Las ventanas (y la puerta, abajo en la fachada).
                val frente = cara.n.z < -0.5f
                val nv = if (frente) (c.largo / 2.6f).toInt().coerceAtLeast(1) else (c.fondo / 3.2f).toInt().coerceAtLeast(1)
                for (piso in 0 until c.pisos) for (i in 0 until nv) {
                    val u = (i + 0.5f) / nv; val v0 = (piso * 3.1f + 0.9f) / alto; val v1 = (piso * 3.1f + 2.3f) / alto
                    val puerta = frente && piso == 0 && i == nv / 2
                    val vv0 = if (puerta) 0.02f else v0
                    val hueco = poligono(listOf(cara.en(u - 0.11f / c.largo * 2.6f, vv0), cara.en(u + 0.11f / c.largo * 2.6f, vv0), cara.en(u + 0.11f / c.largo * 2.6f, v1), cara.en(u - 0.11f / c.largo * 2.6f, v1)).map { s.p(it) })
                    val encendida = ventanas > 0.02f && az.real() < 0.75f
                    val cristal = if (encendida) lerp(p.sombra(C.AZUL, lejos), Color(0xFFFFC870), ventanas) else lerp(p.sombra(C.BREA, lejos), p.medio(C.AZUL, lejos), 0.25f + 0.2f * p.r.dia)
                    drawPath(hueco, if (puerta) p.por(lerp(c.postigos, C.MADERA_OSC, 0.3f), t, lejos) else cristal)
                    if (!puerta && e > 3f) {
                        // Los postigos abiertos a los lados, y el marco claro.
                        for (sl in listOf(-1f, 1f)) drawPath(poligono(listOf(cara.en(u + sl * 0.11f / c.largo * 2.6f, v0), cara.en(u + sl * 0.2f / c.largo * 2.6f, v0), cara.en(u + sl * 0.2f / c.largo * 2.6f, v1), cara.en(u + sl * 0.11f / c.largo * 2.6f, v1)).map { s.p(it) }), p.por(c.postigos, t, lejos))
                        drawPath(hueco, p.luz(C.BLANCO, lejos).copy(alpha = 0.6f), style = Stroke(gr(0.03f)))
                    }
                }
            }
        }
        // La galería de la fachada: el piso de arriba volado sobre postes, con su baranda, y su sombra en la fachada.
        if (c.galeria && c.pisos >= 2) {
            val g0 = -0.9f
            val suelo = poligono(listOf(V3(-w, 3.1f, 0f), V3(w, 3.1f, 0f), V3(w, 3.1f, g0), V3(-w, 3.1f, g0)).map { s.p(it) })
            drawPath(suelo, p.sombra(C.MADERA, lejos))
            for (i in 0..4) { val x = -w + 0.2f + (2f * w - 0.4f) * i / 4f; drawLine(p.por(C.BLANCO, 0.2f, lejos), s.p(V3(x, 0f, g0 + 0.1f)), s.p(V3(x, 3.1f, g0 + 0.1f)), gr(0.12f, 0.4f)); drawLine(p.por(C.BLANCO, 0.4f, lejos), s.p(V3(x, 3.1f, g0 + 0.1f)), s.p(V3(x, 6.0f, g0 + 0.1f)), gr(0.08f, 0.35f)) }
            drawLine(p.por(C.BLANCO, 0.5f, lejos), s.p(V3(-w, 4.0f, g0)), s.p(V3(w, 4.0f, g0)), gr(0.08f, 0.4f))
            if (e > 4f) { var x = -w + 0.15f; while (x < w) { drawLine(p.por(C.BLANCO, 0.3f, lejos), s.p(V3(x, 3.15f, g0)), s.p(V3(x, 4.0f, g0)), gr(0.03f)); x += 0.25f } }
            drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.6f), s.p(V3(-w, 3.05f, g0)), s.p(V3(w, 3.05f, g0)), gr(0.12f, 0.4f))
        }
        // El tejado, con su alero.
        val vuelo = 0.45f
        if (c.aguas == 2) {
            val cum0 = V3(-w - vuelo, alto + c.fondo * 0.35f, c.fondo / 2f); val cum1 = V3(w + vuelo, alto + c.fondo * 0.35f, c.fondo / 2f)
            val delante = poligono(listOf(V3(-w - vuelo, alto - 0.1f, -vuelo), V3(w + vuelo, alto - 0.1f, -vuelo), cum1, cum0).map { s.p(it) })
            val hastial = poligono(listOf(V3(-w, alto, 0f), V3(-w, alto, c.fondo), V3(-w, alto + c.fondo * 0.35f, c.fondo / 2f)).map { s.p(it) })
            if (s.seVe(V3(-w, alto, c.fondo / 2f), V3(-1f, 0f, 0f))) drawPath(hastial, p.por(c.muro, sol.t(s.normal(V3(-1f, 0f, 0f))), lejos))
            if (s.seVe(V3(w, alto, c.fondo / 2f), V3(1f, 0f, 0f))) drawPath(poligono(listOf(V3(w, alto, 0f), V3(w, alto, c.fondo), V3(w, alto + c.fondo * 0.35f, c.fondo / 2f)).map { s.p(it) }), p.por(c.muro, sol.t(s.normal(V3(1f, 0f, 0f))), lejos))
            val tt = sol.t(s.normal(V3(0f, 0.8f, -0.6f)))
            drawPath(delante, p.por(c.tejado, tt + 0.2f, lejos))
            if (e > 3f) clipPath(delante) { val n = (c.largo / 0.5f).toInt().coerceAtMost(30); for (i in 1 until n) { val x = -w - vuelo + (2f * (w + vuelo)) * i / n; drawLine(p.sombra(c.tejado, lejos).copy(alpha = 0.35f), s.p(V3(x, alto - 0.1f, -vuelo)), s.p(V3(x, alto + c.fondo * 0.35f, c.fondo / 2f)), gr(0.015f)) } }
            drawLine(p.sombra(lerp(c.tejado, C.BREA, 0.4f), lejos), s.p(V3(-w - vuelo, alto - 0.1f, -vuelo)), s.p(V3(w + vuelo, alto - 0.1f, -vuelo)), gr(0.08f, 0.4f))
        } else {
            val techo = poligono(listOf(V3(-w - vuelo, alto + 0.1f, -vuelo), V3(w + vuelo, alto + 0.1f, -vuelo), V3(w + vuelo, alto + 1.0f, c.fondo + 0.2f), V3(-w - vuelo, alto + 1.0f, c.fondo + 0.2f)).map { s.p(it) })
            drawPath(techo, p.por(c.tejado, sol.t(s.normal(V3(0f, 0.9f, -0.4f))) + 0.2f, lejos))
            // El pretil de madera sobre la fachada (el falso frente de las tiendas).
            drawPath(poligono(listOf(V3(-w, alto, 0f), V3(w, alto, 0f), V3(w, alto + 0.9f, 0f), V3(-w, alto + 0.9f, 0f)).map { s.p(it) }), p.por(c.muro, sol.t(s.normal(V3(0f, 0f, -1f))), lejos))
        }
        // La chimenea de ladrillo.
        if (az.real() > 0.4f) { val ch = carasDeCaja(w * 0.4f, w * 0.4f + 0.5f, alto, alto + c.fondo * 0.35f + 0.9f, c.fondo * 0.55f, c.fondo * 0.55f + 0.5f); for (cara in visibles(s, ch)) drawPath(forma(s, cara), p.por(lerp(C.ROJO, C.MADERA_OSC, 0.4f), luzDe(s, sol, cara), lejos)) }
    }

    /** Las banderolas del 4 de julio colgadas de la fachada de [c] (rojas, blancas y azules, sin letras), en [s]. */
    fun DrawScope.banderolas(p: Pincel, s: Sitio, c: Casa, lejos: Float) {
        val e = s.escala(0f, 0f); val w = c.largo / 2f; val alto = 3.1f * c.pisos
        val n = 7
        for (i in 0 until n) {
            val a = s.p(-w + c.largo * i / n, alto, -0.05f); val b = s.p(-w + c.largo * (i + 1) / n, alto, -0.05f)
            val m = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f + 0.5f * e)
            drawPath(poligono(a, b, m), listOf(p.medio(C.ROJO, lejos), p.luz(C.BLANCO, lejos), p.medio(C.AZUL, lejos))[i % 3])
        }
    }

    /**
     * Un café del muelle de enfrente: una casa de un piso con su toldo a rayas sobre la puerta, las mesas y sillas fuera
     * (en [mesas], sitios del muelle) y el farol de la puerta. De noche, la puerta y las ventanas encendidas ([luz]).
     */
    fun DrawScope.toldoDeCafe(p: Pincel, s: Sitio, largo: Float, alto: Float, vuelo: Float, rayas: Color, lejos: Float) {
        val e = s.escala(0f, 0f); val w = largo / 2f
        val tela = poligono(listOf(V3(-w, alto, 0f), V3(w, alto, 0f), V3(w, alto - 0.6f, -vuelo), V3(-w, alto - 0.6f, -vuelo)).map { s.p(it) })
        drawPath(tela, p.por(C.BLANCO, 0.3f, lejos))
        clipPath(tela) { val n = (largo / 0.5f).toInt(); for (i in 0 until n step 2) { val x = -w + largo * i / n; drawPath(poligono(listOf(V3(x, alto, 0f), V3(x + largo / n, alto, 0f), V3(x + largo / n, alto - 0.6f, -vuelo), V3(x, alto - 0.6f, -vuelo)).map { s.p(it) }), p.por(rayas, 0.3f, lejos)) } }
        // El volante festoneado.
        val n = (largo / 0.45f).toInt().coerceAtLeast(2)
        for (i in 0 until n) { val a = s.p(V3(-w + largo * i / n, alto - 0.6f, -vuelo)); val b = s.p(V3(-w + largo * (i + 1) / n, alto - 0.6f, -vuelo)); val r = (b.x - a.x) / 2f; drawArc(if (i % 2 == 0) p.por(rayas, -0.1f, lejos) else p.por(C.BLANCO, -0.1f, lejos), 0f, 180f, true, Offset(a.x, a.y - abs(r) * 0.6f), Size(b.x - a.x, abs(r) * 1.2f)) }
        if (e > 3f) for (x in listOf(-w + 0.1f, w - 0.1f)) drawLine(p.sombra(C.BREA, lejos), s.p(V3(x, 0f, -vuelo + 0.05f)), s.p(V3(x, alto - 0.6f, -vuelo + 0.05f)), (0.05f * e).coerceAtLeast(0.35f))
    }

    // =================================================================
    //  La lonja y la iglesia
    // =================================================================

    /**
     * La lonja de las esponjas: «un patio grande con cuartos alrededor, cada capitán tiene su puerta, donde guarda lo que
     * sacó el año». La crujía de delante, de madera blanqueada y tejado de chapa a un agua, de [largo] metros, mira al río
     * (−z): las puertas de cada capitán, unas abiertas con las esponjas apiladas por tamaños dentro y fuera, el portón del
     * patio en medio, con el patio y sus cuartos al fondo; las ristras colgadas bajo el alero. [luces], de noche.
     * [montones], de 0 a 1: cuántas esponjas hay fuera (el día de la lonja, todas).
     */
    fun DrawScope.lonja(p: Pincel, s: Sitio, sol: Sol3, largo: Float, semilla: Long, luces: Float, montones: Float, lejos: Float, cerrada: Boolean = false) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val alto = 3.9f; val fondo = 5f; val w = largo / 2f
        val madera = lerp(C.MADERA_GRIS, C.BLANCO, 0.45f)
        fun gr(v: Float, min: Float = 0.25f) = (v * e).coerceAtLeast(min)
        val portonU = 0.5f
        for (cara in visibles(s, carasDeCaja(-w, w, 0f, alto, 0f, fondo))) {
            if (cara.arriba) continue
            val t = luzDe(s, sol, cara); val f = forma(s, cara)
            drawPath(f, p.por(madera, t, lejos))
            clipPath(f) {
                if (e > 2.5f) for (k in 1 until 11) linea(s, cara, 0f, k / 11f, 1f, k / 11f, p.sombra(madera, lejos).copy(alpha = 0.3f), gr(0.012f))
                linea(s, cara, 0f, 0.04f, 1f, 0.04f, p.sombra(lerp(madera, C.BREA, 0.3f), lejos), gr(0.25f, 0.6f))
                if (cara.n.z < -0.5f) {
                    // Las puertas de cada capitán, y el portón del patio en medio.
                    val puertas = (largo / 3.0f).toInt()
                    for (i in 0 until puertas) {
                        val u0 = (i + 0.22f) / puertas; val u1 = (i + 0.78f) / puertas
                        val porton = abs((u0 + u1) / 2f - portonU) < 0.5f / puertas
                        val abierta = !cerrada && (porton || az.real() > 0.45f)
                        val hueco = poligono(listOf(cara.en(u0, 0.03f), cara.en(u1, 0.03f), cara.en(u1, if (porton) 0.78f else 0.62f), cara.en(u0, if (porton) 0.78f else 0.62f)).map { s.p(it) })
                        if (abierta) {
                            drawPath(hueco, lerp(p.sombra(lerp(C.MADERA_OSC, C.BREA, 0.4f), lejos), Color(0xFFFFB860), 0.6f * luces))
                            // Dentro, las esponjas apiladas por tamaños: un montón dorado que la luz enciende en la boca.
                            clipPath(hueco) {
                                val base = s.p(cara.en((u0 + u1) / 2f, 0.03f)); val ancho = abs(s.p(cara.en(u1, 0.03f)).x - s.p(cara.en(u0, 0.03f)).x)
                                for (j in 0 until 3) mancha(base + Offset((j - 1) * ancho * 0.28f, -ancho * 0.15f), ancho * 0.24f, ancho * 0.2f, lerp(p.medio(C.ESPONJA, lejos), p.luz(C.ESPONJA, lejos), 0.4f * p.r.dia), 0.9f)
                                if (porton) { val fondoP = s.p(cara.en((u0 + u1) / 2f, 0.5f)); mancha(fondoP, ancho * 0.6f, ancho * 0.3f, p.luz(lerp(madera, C.ESPONJA, 0.3f), lejos), 0.5f) }
                            }
                        } else {
                            drawPath(hueco, p.por(lerp(C.AZUL, madera, 0.3f), t, lejos))
                            if (e > 3f) linea(s, cara, (u0 + u1) / 2f, 0.03f, (u0 + u1) / 2f, 0.62f, p.sombra(C.BREA, lejos).copy(alpha = 0.5f), gr(0.02f))
                        }
                        if (luces > 0.02f && i % 2 == 0) resplandor(s.p(cara.en((u0 + u1) / 2f, 0.72f)), gr(1.4f, 3f), 0.6f * luces, C.LUMBRE)
                    }
                }
            }
        }
        // El tejado de chapa, a un agua, con sus ondas, y el alero que da sombra a las puertas.
        val techo = poligono(listOf(V3(-w - 0.3f, alto + 0.1f, -0.7f), V3(w + 0.3f, alto + 0.1f, -0.7f), V3(w + 0.3f, alto + 1.1f, fondo + 0.3f), V3(-w - 0.3f, alto + 1.1f, fondo + 0.3f)).map { s.p(it) })
        drawPath(techo, p.por(lerp(C.PLOMO, C.BLANCO, 0.25f), sol.t(s.normal(V3(0f, 0.95f, -0.3f))) + 0.15f, lejos))
        clipPath(techo) { val n = (largo / 0.6f).toInt().coerceAtMost(48); for (i in 0 until n) { val x = -w - 0.3f + (largo + 0.6f) * i / n; drawLine(p.sombra(C.PLOMO, lejos).copy(alpha = 0.35f), s.p(V3(x, alto + 0.1f, -0.7f)), s.p(V3(x, alto + 1.1f, fondo + 0.3f)), gr(0.02f)) }; repeat(4) { mancha(s.p(V3(az.entre(-w, w), alto + 0.5f, az.entre(0f, fondo))), gr(1.2f, 2f), gr(0.4f, 1f), p.sombra(lerp(C.PLOMO, C.COBRE_OSC, 0.5f), lejos), 0.35f) } }
        drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.6f), s.p(V3(-w - 0.3f, alto + 0.1f, -0.7f)), s.p(V3(w + 0.3f, alto + 0.1f, -0.7f)), gr(0.06f, 0.4f))
        // Las ristras colgadas bajo el alero, a secar, y los montones de esponjas fuera, por tamaños.
        if (e > 2f && !cerrada) {
            val nr = (largo / 1.1f).toInt()
            for (i in 0 until nr) { if (az.real() > 0.7f) continue; val x = -w + 0.4f + (largo - 0.8f) * i / nr; val a = s.p(V3(x, alto - 0.05f, -0.6f)); drawLine(lerp(p.medio(C.ESPONJA, lejos), p.sombra(C.ESPONJA, lejos), az.real() * 0.5f), a, a + Offset(0f, gr(1.6f, 3f) * az.entre(0.7f, 1f)), gr(0.22f, 0.8f), StrokeCap.Round) }
            val nm = (2 + 6 * montones).toInt()
            for (i in 0 until nm) { val q = s.p(V3(-w + 0.8f + (largo - 1.6f) * az.real(), 0f, -az.entre(0.5f, 1.8f))); val r = gr(0.45f, 1.2f) * az.entre(0.7f, 1.3f); drawOval(Brush.verticalGradient(listOf(p.luz(C.ESPONJA, lejos), p.sombra(C.ESPONJA_OSC, lejos)), q.y - r, q.y), Offset(q.x - r * 1.2f, q.y - r), Size(r * 2.4f, r)) }
        }
    }

    /**
     * La iglesia de San Nicolás: «de madera, pequeña, blanca, con una cúpula que alguien pintó de azul para que se pareciera
     * a las de la isla», y su campanario. En [s] (su fachada mira hacia −z), en tres cuartos: las tablas blancas de chilla,
     * el tejado a dos aguas con su frontón, el tambor con la cúpula azul y su cruz, el campanario con su cupulín, la
     * campana en su hueco, la puerta, las ventanas de medio punto ([ventanas], de 0 a 1: encendidas). [luna], la plata de la
     * luna en la cúpula.
     */
    fun DrawScope.iglesia(p: Pincel, s: Sitio, sol: Sol3, ventanas: Float, luna: Float, lejos: Float) {
        val e = s.escala(0f, 0f)
        val w = 3.4f; val fondo = 9f; val alto = 5.6f
        fun gr(v: Float, min: Float = 0.25f) = (v * e).coerceAtLeast(min)
        val blanco = lerp(C.BLANCO, Color.White, 0.15f)
        // El campanario, delante a un lado de la fachada (se pinta antes si queda detrás).
        val sc = Sitio(s.v, s.p(-w - 1.0f, 0f, 0.8f), s.escala(-w - 1.0f, 0.8f), s.giro)
        fun campanario() {
            val tc = 10.2f; val a = 0.82f
            for (cara in visibles(sc, carasDeCaja(-a, a, 0f, tc, -a, a))) {
                if (cara.arriba) continue
                val f = forma(sc, cara); val t = luzDe(sc, sol, cara)
                drawPath(f, p.por(blanco, t, lejos))
                clipPath(f) {
                    if (e > 2f) for (k in 1 until 22) linea(sc, cara, 0f, k / 22f, 1f, k / 22f, p.sombra(blanco, lejos).copy(alpha = 0.28f), gr(0.012f))
                    // La cornisa que separa el campanario del cuerpo, y el hueco de la campana: arco de medio punto, en sombra, con la campana dentro.
                    linea(sc, cara, 0f, 0.7f, 1f, 0.7f, p.sombra(blanco, lejos), gr(0.12f, 0.4f))
                    val h0 = sc.p(cara.en(0.28f, 0.74f)); val h1 = sc.p(cara.en(0.72f, 0.92f)); val ancho = kotlin.math.abs(h1.x - h0.x)
                    val arco = Path().apply { moveTo(h0.x, h0.y); lineTo(h0.x, h1.y + ancho * 0.5f); cubicTo(h0.x, h1.y - ancho * 0.1f, h1.x, h1.y - ancho * 0.1f, h1.x, h1.y + ancho * 0.5f); lineTo(h1.x, h0.y); close() }
                    drawPath(arco, p.sombra(lerp(C.AZUL, C.BREA, 0.55f), lejos))
                    if (cara.n.z < -0.5f) { val cb = Offset((h0.x + h1.x) / 2f, h1.y + ancho * 0.55f); drawPath(Path().apply { moveTo(cb.x - ancho * 0.22f, cb.y + ancho * 0.3f); cubicTo(cb.x - ancho * 0.2f, cb.y - ancho * 0.2f, cb.x + ancho * 0.2f, cb.y - ancho * 0.2f, cb.x + ancho * 0.22f, cb.y + ancho * 0.3f); close() }, p.por(C.LATON, t, lejos)) }
                    // Una ventanita alta en el cuerpo.
                    val v0 = sc.p(cara.en(0.42f, 0.38f)); val v1 = sc.p(cara.en(0.58f, 0.5f))
                    drawRect(lerp(p.sombra(C.AZUL, lejos), Color(0xFFFFC870), ventanas), Offset(minOf(v0.x, v1.x), minOf(v0.y, v1.y)), Size(kotlin.math.abs(v1.x - v0.x).coerceAtLeast(0.5f), kotlin.math.abs(v1.y - v0.y)))
                }
            }
            // El cupulín azul del campanario, con su cruz.
            val c0 = sc.p(0f, tc, 0f); val ek = sc.escala(0f, 0f); val r = 0.95f * ek
            drawRect(p.por(blanco, 0.3f, lejos), Offset(c0.x - r * 1.05f, c0.y - r * 0.18f), Size(2.1f * r, r * 0.24f))
            val cup = Path().apply { moveTo(c0.x - r, c0.y - r * 0.15f); cubicTo(c0.x - r, c0.y - r * 1.6f, c0.x + r, c0.y - r * 1.6f, c0.x + r, c0.y - r * 0.15f); close() }
            drawPath(cup, Brush.radialGradient(listOf(p.luz(C.AZUL, lejos), p.medio(C.AZUL, lejos), p.sombra(C.AZUL, lejos)), Offset(c0.x + lado(p) * r * 0.4f, c0.y - r * 1.0f), r * 1.6f))
            if (luna > 0.05f) drawArc(C.LUNA.copy(alpha = (0.6f * luna).coerceIn(0f, 1f)), 200f, 70f, false, Offset(c0.x - r * 0.85f, c0.y - r * 1.4f), Size(r * 1.7f, r * 1.8f), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
            drawLine(p.medio(C.ORO, lejos), c0 + Offset(0f, -r * 1.25f), c0 + Offset(0f, -r * 2.1f), gr(0.08f, 0.5f))
            drawLine(p.medio(C.ORO, lejos), c0 + Offset(-r * 0.25f, -r * 1.8f), c0 + Offset(r * 0.25f, -r * 1.8f), gr(0.08f, 0.5f))
        }
        val campanarioDetras = sc.hondo(V3(0f, 0f, 0f)) > s.hondo(V3(-w, 0f, 0f))
        if (campanarioDetras) campanario()
        // La nave: una caja de tablas blancas, con sus ventanas de medio punto y la puerta.
        for (cara in visibles(s, carasDeCaja(-w, w, 0f, alto, 0f, fondo))) {
            if (cara.arriba) continue
            val t = luzDe(s, sol, cara); val f = forma(s, cara)
            drawPath(f, p.por(blanco, t, lejos))
            clipPath(f) {
                if (e > 2f) for (k in 1 until 16) linea(s, cara, 0f, k / 16f, 1f, k / 16f, p.sombra(blanco, lejos).copy(alpha = 0.3f), gr(0.012f))
                val nv = if (cara.n.z < -0.5f) 2 else 4
                for (i in 0 until nv) {
                    val u = (i + 0.5f) / nv
                    val q0 = s.p(cara.en(u - 0.05f, 0.32f)); val q1 = s.p(cara.en(u + 0.05f, 0.72f))
                    val luz = lerp(p.sombra(C.AZUL, lejos), Color(0xFFFFC870), ventanas)
                    val ww = abs(q1.x - q0.x).coerceAtLeast(0.6f)
                    drawRect(luz, Offset(minOf(q0.x, q1.x), minOf(q0.y, q1.y)), Size(ww, abs(q1.y - q0.y)))
                    drawOval(luz, Offset(minOf(q0.x, q1.x), minOf(q0.y, q1.y) - ww * 0.5f), Size(ww, ww))
                }
                if (cara.n.z < -0.5f) { val q0 = s.p(cara.en(0.42f, 0f)); val q1 = s.p(cara.en(0.58f, 0.42f)); drawRect(lerp(p.medio(C.AZUL, lejos), Color(0xFFFFC870), 0.8f * ventanas), Offset(minOf(q0.x, q1.x), q1.y), Size(abs(q1.x - q0.x), q0.y - q1.y)) }
            }
        }
        // El tejado a dos aguas, gris, y el frontón de la fachada.
        val cum0 = s.p(0f, alto + 1.8f, -0.2f); val cum1 = s.p(0f, alto + 1.8f, fondo + 0.2f)
        for (sl in listOf(-1f, 1f)) {
            if (!s.seVe(V3(sl * w * 0.5f, alto + 0.9f, fondo / 2f), V3(sl * 0.6f, 0.8f, 0f))) continue
            drawPath(poligono(listOf(s.p(sl * (w + 0.25f), alto - 0.05f, -0.2f), cum0, cum1, s.p(sl * (w + 0.25f), alto - 0.05f, fondo + 0.2f))), p.por(lerp(C.PLOMO, C.BLANCO, 0.3f), sol.t(s.normal(V3(sl * 0.6f, 0.8f, 0f))), lejos))
        }
        drawPath(poligono(listOf(s.p(-w, alto, 0f), cum0, s.p(w, alto, 0f))), p.por(blanco, sol.t(s.normal(V3(0f, 0f, -1f))), lejos))
        // El tambor y la cúpula azul, con su cruz; de noche, la plata de la luna en su lomo.
        val tambor = s.p(0f, alto + 1.2f, fondo * 0.55f); val r = 2.3f * s.escala(0f, fondo * 0.55f)
        drawRect(Brush.horizontalGradient(listOf(if (lado(p) < 0) p.luz(blanco, lejos) else p.sombra(blanco, lejos), if (lado(p) < 0) p.sombra(blanco, lejos) else p.luz(blanco, lejos)), tambor.x - r, tambor.x + r), Offset(tambor.x - r, tambor.y - r * 0.9f), Size(r * 2f, r * 0.9f))
        for (i in 0 until 5) { val x = tambor.x - r * 0.7f + r * 0.35f * i; drawRect(lerp(p.sombra(C.AZUL, lejos), Color(0xFFFFC870), ventanas), Offset(x - r * 0.05f, tambor.y - r * 0.7f), Size(r * 0.1f, r * 0.4f)) }
        val cupula = Path().apply { moveTo(tambor.x - r * 1.04f, tambor.y - r * 0.88f); cubicTo(tambor.x - r * 1.02f, tambor.y - r * 2.1f, tambor.x + r * 1.02f, tambor.y - r * 2.1f, tambor.x + r * 1.04f, tambor.y - r * 0.88f); close() }
        drawPath(cupula, Brush.radialGradient(listOf(p.luz(C.AZUL, lejos), p.medio(C.AZUL, lejos), p.sombra(C.AZUL, lejos)), Offset(tambor.x + lado(p) * r * 0.45f, tambor.y - r * 1.6f), r * 1.9f))
        if (luna > 0.05f) drawArc(C.LUNA.copy(alpha = (0.7f * luna).coerceIn(0f, 1f)), 200f, 70f, false, Offset(tambor.x - r * 0.9f, tambor.y - r * 1.95f), Size(r * 1.8f, r * 2f), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
        drawLine(p.sombra(C.AZUL, lejos).copy(alpha = 0.5f), Offset(tambor.x - r * 1.04f, tambor.y - r * 0.88f), Offset(tambor.x + r * 1.04f, tambor.y - r * 0.88f), r * 0.08f)
        val tope = Offset(tambor.x, tambor.y - r * 1.8f)
        drawLine(p.medio(C.ORO, lejos), tope, tope + Offset(0f, -r * 0.7f), r * 0.07f)
        drawLine(p.medio(C.ORO, lejos), tope + Offset(-r * 0.2f, -r * 0.48f), tope + Offset(r * 0.2f, -r * 0.48f), r * 0.07f)
        if (!campanarioDetras) campanario()
        if (ventanas > 0.02f) resplandor(s.p(0f, 1.4f, -1.2f), 3.5f * e, 0.5f * ventanas, C.LUMBRE)
    }

    // =================================================================
    //  Lo del muelle: pilote, bita, farol, mesa, silla, sacos, red
    // =================================================================

    /** Un pilote de madera clavado en el río, de [a] (la cabeza) a [b] (donde entra en el agua), de [g] unidades de grueso: gris, rajado, con la franja de verdín y percebes en la marea. */
    fun DrawScope.pilote(p: Pincel, a: Offset, b: Offset, g: Float, lejos: Float = 0f) {
        val ld = lado(p)
        val f = poligono(Offset(a.x - g / 2f, a.y), Offset(a.x + g / 2f, a.y), Offset(b.x + g / 2f, b.y), Offset(b.x - g / 2f, b.y))
        drawPath(f, Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(C.MADERA_GRIS, lejos) else p.luz(C.MADERA_GRIS, lejos), if (ld > 0) p.luz(C.MADERA_GRIS, lejos) else p.sombra(C.MADERA_GRIS, lejos)), a.x - g / 2f, a.x + g / 2f))
        drawRect(p.medio(lerp(C.VERDIN, C.BREA, 0.3f), lejos), Offset(b.x - g / 2f, b.y - g * 1.4f), Size(g, g * 1.4f))
        drawLine(p.luz(lerp(C.BLANCO, C.ARENA, 0.4f), lejos).copy(alpha = 0.6f), Offset(b.x - g / 2f, b.y - g * 1.4f), Offset(b.x + g / 2f, b.y - g * 1.4f), (g * 0.15f).coerceAtLeast(0.3f))
        drawOval(p.luz(C.MADERA_GRIS, lejos), Offset(a.x - g / 2f, a.y - g * 0.18f), Size(g, g * 0.36f))
        if (g > 3f) drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.5f), Offset(a.x + g * 0.1f, a.y + g), Offset(a.x + g * 0.05f, b.y - g * 2f), g * 0.06f)
    }

    /** Una bita de hierro del muelle en [s], con el cabo dado vueltas: el cuerpo redondo con su lado de luz, la cabeza ancha, el óxido y la pintura gastada. */
    fun DrawScope.bita(p: Pincel, s: Sitio, cabo: Boolean, lejos: Float = 0f) {
        val e = s.escala(0f, 0f)
        val base = s.p(0f, 0f, 0f); val top = s.p(0f, 0.38f, 0f); val r = 0.13f * e
        val ld = lado(p)
        val hierro = lerp(C.PLOMO, C.BREA, 0.35f)
        drawOval(p.medio(hierro, lejos), Offset(base.x - r * 1.5f, base.y - r * 0.45f), Size(r * 3f, r * 0.9f))
        drawRect(Brush.horizontalGradient(0f to (if (ld > 0) p.sombra(hierro, lejos) else p.luz(hierro, lejos)), 0.6f to p.medio(hierro, lejos), 1f to (if (ld > 0) p.luz(hierro, lejos) else p.sombra(hierro, lejos)), startX = base.x - r, endX = base.x + r), Offset(base.x - r, top.y), Size(r * 2f, base.y - top.y))
        drawOval(p.medio(hierro, lejos), Offset(top.x - r * 1.45f, top.y - r * 0.5f), Size(r * 2.9f, r * 1.1f))
        drawOval(p.luz(hierro, lejos), Offset(top.x - r * 1.3f, top.y - r * 0.6f), Size(r * 2.6f, r * 0.8f))
        if (e > 20f) repeat(4) { mancha(base + Offset((it - 1.5f) * r * 0.5f, -r * (0.8f + 0.5f * (it % 2))), r * 0.4f, r * 0.25f, p.medio(C.COBRE_OSC, lejos), 0.45f) }
        if (cabo) for (i in 0 until 3) drawOval(p.por(C.CANAMO, if (i == 0) 0.3f else -0.1f, lejos), Offset(base.x - r * 1.2f, top.y + r * (0.7f + 0.55f * i)), Size(r * 2.4f, r * 0.7f), style = Stroke(r * 0.32f))
    }

    /**
     * El farol del muelle: un poste de hierro con su base, su anilla y el farol de cuatro vidrios arriba. En [pie], de
     * [k] unidades por metro y [alto] metros. [encendido], de 0 a 1: la llama dentro y su halo.
     */
    fun DrawScope.farolDeMuelle(p: Pincel, pie: Offset, k: Float, alto: Float, encendido: Float, lejos: Float = 0f): Offset {
        val ld = lado(p)
        val hierro = lerp(C.PLOMO, C.BREA, 0.5f)
        val top = pie + Offset(0f, -alto * k)
        val g = (0.09f * k).coerceAtLeast(0.6f)
        drawRect(Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(hierro, lejos) else p.luz(hierro, lejos), if (ld > 0) p.luz(hierro, lejos) else p.sombra(hierro, lejos)), pie.x - g, pie.x + g), Offset(pie.x - g * 1.8f, pie.y - 0.5f * k), Size(g * 3.6f, 0.5f * k))
        drawLine(p.medio(hierro, lejos), pie, top, g)
        drawLine(p.luz(hierro, lejos).copy(alpha = 0.5f), pie + Offset(ld * g * 0.25f, 0f), top + Offset(ld * g * 0.25f, 0f), g * 0.3f)
        drawLine(p.medio(hierro, lejos), top + Offset(-g * 2f, 0.05f * k), top + Offset(g * 2f, 0.05f * k), g * 0.8f)
        val c = top + Offset(0f, -0.25f * k)
        val w = 0.17f * k; val h = 0.24f * k
        if (encendido > 0.02f) resplandor(c, (1.4f * k).coerceAtLeast(6f), (0.85f * encendido).coerceIn(0f, 1f), C.LUMBRE)
        val vidrio = poligono(Offset(c.x - w, c.y - h * 0.5f), Offset(c.x + w, c.y - h * 0.5f), Offset(c.x + w * 0.75f, c.y + h * 0.5f), Offset(c.x - w * 0.75f, c.y + h * 0.5f))
        drawPath(vidrio, if (encendido > 0.02f) lerp(p.medio(C.LONA, lejos), Color(0xFFFFE6A8), encendido) else lerp(p.luz(C.BLANCO, lejos), p.sombra(C.MAR, lejos), 0.45f))
        drawLine(p.sombra(hierro, lejos), Offset(c.x, c.y - h * 0.5f), Offset(c.x, c.y + h * 0.5f), g * 0.35f)
        drawPath(poligono(Offset(c.x - w * 1.25f, c.y - h * 0.5f), Offset(c.x, c.y - h * 1.0f), Offset(c.x + w * 1.25f, c.y - h * 0.5f)), p.medio(hierro, lejos))
        drawRect(p.medio(hierro, lejos), Offset(c.x - w * 0.8f, c.y + h * 0.5f), Size(w * 1.6f, h * 0.15f))
        return c
    }

    /**
     * Una mesa de café redonda en [s] (de hierro, con su tablero de mármol gris), con dos sillas de madera y enea, dos
     * tazas pequeñas con su plato, un tablero de tavli abierto y, a veces, un comboloi dejado. Sin personas: lo que dejan.
     */
    fun DrawScope.mesaDeCafe(p: Pincel, s: Sitio, sol: Sol3, semilla: Long, lejos: Float = 0f, sillas: Int = 2) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        silla(p, Sitio(s.v, s.p(0.15f, 0f, 0.5f), s.escala(0.15f, 0.5f), s.giro + 3.0f), sol, lejos)
        val tope = s.p(0f, 0.74f, 0f)
        val hierro = lerp(C.BREA, C.PLOMO, 0.3f)
        drawLine(p.sombra(hierro, lejos), s.p(0f, 0f, 0f), tope, (0.045f * e).coerceAtLeast(0.4f))
        for (a in listOf(0.3f, 2.4f, 4.5f)) drawLine(p.sombra(hierro, lejos), s.p(0f, 0.08f, 0f), s.p(0.24f * cos(a), 0f, 0.24f * sin(a)), (0.03f * e).coerceAtLeast(0.35f), StrokeCap.Round)
        val n = 24
        val borde = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(0.34f * cos(a), 0.74f, 0.34f * sin(a)) }
        val canto = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(0.34f * cos(a), 0.7f, 0.34f * sin(a)) }
        val marmol = lerp(C.BLANCO, C.PLOMO, 0.22f)
        drawPath(poligono(envolvente(borde + canto)), p.sombra(marmol, lejos))
        drawPath(poligono(borde), p.arriba(marmol, lejos))
        if (e > 30f) clipPath(poligono(borde)) { repeat(3) { val q = s.p(az.entre(-0.25f, 0.25f), 0.74f, az.entre(-0.25f, 0.25f)); drawLine(p.sombra(marmol, lejos).copy(alpha = 0.3f), q, q + Offset(az.entre(-0.3f, 0.3f) * e, az.entre(-0.05f, 0.05f) * e), 0.5f) } }
        // El tavli abierto, con sus puntas pintadas y las fichas.
        val t0 = s.p(-0.05f, 0.745f, 0.02f); val tw = 0.3f * e; val th = 0.11f * e
        drawRect(p.arriba(lerp(C.MADERA, C.ROJO, 0.25f), lejos), Offset(t0.x - tw / 2f, t0.y - th / 2f), Size(tw, th))
        if (e > 20f) { for (i in 0 until 6) { val x = t0.x - tw / 2f + tw * (i + 0.5f) / 6f; drawPath(poligono(Offset(x - tw / 26f, t0.y - th / 2f), Offset(x + tw / 26f, t0.y - th / 2f), Offset(x, t0.y - th * 0.05f)), if (i % 2 == 0) p.medio(C.BREA, lejos) else p.luz(C.ESPONJA, lejos)) }
            drawLine(p.sombra(C.MADERA_OSC, lejos), Offset(t0.x, t0.y - th / 2f), Offset(t0.x, t0.y + th / 2f), tw * 0.03f)
            repeat(5) { drawCircle(if (it % 2 == 0) p.luz(C.BLANCO, lejos) else p.medio(C.BREA, lejos), th * 0.11f, Offset(t0.x - tw * 0.4f + az.real() * tw * 0.8f, t0.y + az.entre(-0.3f, 0.3f) * th)) } }
        // Dos tazas con su plato.
        for (i in 0 until 2) { val c = s.p(if (i == 0) 0.2f else -0.22f, 0.745f, if (i == 0) -0.12f else 0.15f); val r = 0.035f * e
            drawOval(p.arriba(C.BLANCO, lejos), Offset(c.x - r * 1.6f, c.y - r * 0.4f), Size(r * 3.2f, r * 0.9f))
            drawRect(Brush.horizontalGradient(listOf(p.luz(C.BLANCO, lejos), p.sombra(C.BLANCO, lejos)), c.x - r, c.x + r), Offset(c.x - r, c.y - r * 1.6f), Size(r * 2f, r * 1.5f))
            drawOval(p.medio(lerp(C.BREA, C.MADERA_OSC, 0.3f), lejos), Offset(c.x - r * 0.9f, c.y - r * 1.75f), Size(r * 1.8f, r * 0.5f)) }
        if (az.real() > 0.35f) { val c = s.p(0.18f, 0.75f, 0.14f); for (i in 0 until 9) drawCircle(p.luz(lerp(C.ESPONJA, C.ROJO, 0.3f), lejos), (0.012f * e).coerceAtLeast(0.4f), c + Offset(cos(i * 0.7f) * 0.05f * e, sin(i * 0.7f) * 0.02f * e)) }
        if (sillas > 1) silla(p, Sitio(s.v, s.p(0.42f, 0f, -0.25f), s.escala(0.42f, -0.25f), s.giro - 2.2f), sol, lejos)
    }

    /** Una silla de café de madera, con el asiento de enea, en [s] (mirando hacia −z). */
    fun DrawScope.silla(p: Pincel, s: Sitio, sol: Sol3, lejos: Float = 0f) {
        val e = s.escala(0f, 0f); val g = (0.03f * e).coerceAtLeast(0.4f)
        val madera = lerp(C.MADERA, C.MADERA_OSC, 0.3f)
        for ((x, z) in listOf(-0.2f to -0.2f, 0.2f to -0.2f, -0.2f to 0.2f, 0.2f to 0.2f)) drawLine(p.sombra(madera, lejos), s.p(x, 0f, z), s.p(x, 0.45f, z), g)
        for (x in listOf(-0.2f, 0.2f)) drawLine(p.medio(madera, lejos), s.p(x, 0.45f, 0.2f), s.p(x, 0.92f, 0.22f), g)
        drawLine(p.luz(madera, lejos), s.p(-0.21f, 0.88f, 0.22f), s.p(0.21f, 0.88f, 0.22f), g * 1.6f)
        drawLine(p.luz(madera, lejos), s.p(-0.21f, 0.7f, 0.22f), s.p(0.21f, 0.7f, 0.22f), g)
        val asiento = poligono(listOf(s.p(-0.22f, 0.45f, -0.22f), s.p(0.22f, 0.45f, -0.22f), s.p(0.22f, 0.45f, 0.22f), s.p(-0.22f, 0.45f, 0.22f)))
        drawPath(asiento, p.arriba(lerp(C.ESPONJA, C.CANAMO, 0.5f), lejos))
        if (e > 30f) clipPath(asiento) { for (i in 0 until 8) drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.5f), s.p(-0.22f + 0.055f * i, 0.45f, -0.22f), s.p(-0.22f + 0.055f * i, 0.45f, 0.22f), 0.4f) }
    }

    /**
     * Un saco de esponjas apretadas, de lona, en [s], de [ancho] metros: la panza llena de bultos, la boca atada, la
     * lona con sus remiendos; [tumbado] si está echado.
     */
    fun DrawScope.saco(p: Pincel, s: Sitio, sol: Sol3, ancho: Float, semilla: Long, tumbado: Boolean, lejos: Float = 0f) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val ld = lado(p)
        val lona = lerp(C.LONA, C.MADERA, az.entre(0.1f, 0.3f))
        val c = s.p(0f, 0f, 0f)
        val w = ancho * 0.5f * e; val h = (if (tumbado) ancho * 0.55f else ancho * 1.1f) * e
        // La panza llena de bultos: ancha abajo, apoyada, y estrecha arriba hacia el cuello atado.
        val pts = if (tumbado) listOf(Offset(-1.15f, 0f), Offset(-1.2f, -0.55f), Offset(-0.7f, -0.98f), Offset(0.1f, -1.0f), Offset(0.8f, -0.9f), Offset(1.25f, -0.5f), Offset(1.1f, 0f), Offset(0f, 0.08f))
            else listOf(Offset(-1f, 0f), Offset(-1.08f, -0.35f), Offset(-0.95f, -0.72f), Offset(-0.5f, -0.92f), Offset(-0.18f, -1f), Offset(0.22f, -1f), Offset(0.55f, -0.9f), Offset(0.98f, -0.7f), Offset(1.06f, -0.3f), Offset(0.95f, 0f), Offset(0f, 0.06f))
        val forma = lazo(pts.map { Offset(c.x + it.x * w * az.entre(0.95f, 1.05f), c.y + it.y * h) })
        drawPath(forma, Brush.radialGradient(0f to p.luz(lona, lejos), 0.6f to p.medio(lona, lejos), 1f to p.sombra(lona, lejos), center = c + Offset(ld * w * 0.4f, -h * 0.65f), radius = maxOf(w, h) * 1.4f))
        clipPath(forma) {
            // Los bultos de las esponjas apretadas bajo la lona, la costura, un remiendo y la sombra de abajo.
            repeat(6) { val q = c + Offset(az.entre(-0.7f, 0.7f) * w, -az.entre(0.15f, 0.8f) * h); drawArc(p.sombra(lona, lejos).copy(alpha = 0.45f), 200f, 130f, false, Offset(q.x - w * 0.28f, q.y - h * 0.08f), Size(w * 0.56f, h * 0.18f), style = Stroke((0.02f * e).coerceAtLeast(0.35f))) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(lerp(lona, C.BREA, 0.3f), lejos).copy(alpha = 0.6f)), c.y - h * 0.3f, c.y), Offset(c.x - w * 1.3f, c.y - h * 0.3f), Size(w * 2.6f, h * 0.3f))
            if (!tumbado) drawLine(p.sombra(lona, lejos).copy(alpha = 0.5f), c + Offset(w * 0.15f, -h * 0.95f), c + Offset(w * 0.25f, -h * 0.05f), (0.012f * e).coerceAtLeast(0.3f))
            if (az.real() > 0.4f) drawRect(p.medio(lerp(lona, C.ESPONJA_OSC, 0.4f), lejos), Offset(c.x + az.entre(-0.6f, 0.1f) * w, c.y - az.entre(0.3f, 0.7f) * h), Size(w * 0.45f, h * 0.22f))
        }
        if (!tumbado) {
            // El cuello atado con su cuerda y las puntas de la lona.
            val boca = c + Offset(0f, -h); val gw = 0.02f * e
            drawPath(poligono(boca + Offset(-w * 0.22f, h * 0.02f), boca + Offset(w * 0.22f, h * 0.02f), boca + Offset(w * 0.3f, -h * 0.16f), boca + Offset(-w * 0.05f, -h * 0.12f), boca + Offset(-w * 0.3f, -h * 0.15f)), p.por(lona, 0.3f, lejos))
            drawLine(p.sombra(C.CANAMO, lejos), boca + Offset(-w * 0.24f, 0f), boca + Offset(w * 0.24f, 0f), (gw * 1.6f).coerceAtLeast(0.5f))
        } else drawLine(p.sombra(C.CANAMO, lejos), c + Offset(w * 1.05f, -h * 0.85f), c + Offset(w * 1.2f, -h * 0.2f), (0.03f * e).coerceAtLeast(0.5f))
    }

    /** Las esquinas de un saco (para su sombra). */
    fun sacoEsquinas(ancho: Float, tumbado: Boolean): List<V3> { val a = if (tumbado) ancho * 0.6f else ancho * 1.2f; return esquinas(-ancho / 2f, ancho / 2f, 0f, a, -ancho * 0.3f, ancho * 0.3f) }

    /**
     * Una red de esponjas tendida de una cuerda: el paño de malla gruesa que cuelga de [a] a [b] y cae [caida] unidades,
     * con su aro de hierro en una punta; se ve la malla y, detrás, lo que hay.
     */
    fun DrawScope.redTendida(p: Pincel, a: Offset, b: Offset, caida: Float, lejos: Float = 0f) {
        val n = 9
        val cuerda = combada(a, b, caida * 0.12f, n)
        val color = p.medio(lerp(C.CANAMO, C.MADERA_OSC, 0.35f), lejos)
        val abajo = cuerda.mapIndexed { i, q -> q + Offset((i - n / 2f) * 0.4f, caida * (0.8f + 0.2f * sin(i * 1.3f))) }
        for (i in 0..n) drawLine(color.copy(alpha = 0.7f), cuerda[i], abajo[i], 0.45f)
        for (j in 1..6) { val t = j / 6f; drawPath(curva((0..n).map { cuerda[it] + (abajo[it] - cuerda[it]) * t }), color.copy(alpha = 0.6f), style = Stroke(0.4f)) }
        drawPath(curva(cuerda), p.sombra(C.CANAMO, lejos), style = Stroke(0.6f))
        val aro = abajo.last(); drawOval(p.sombra(C.PLOMO, lejos), Offset(aro.x - caida * 0.2f, aro.y - caida * 0.08f), Size(caida * 0.4f, caida * 0.16f), style = Stroke(0.8f))
    }
}
