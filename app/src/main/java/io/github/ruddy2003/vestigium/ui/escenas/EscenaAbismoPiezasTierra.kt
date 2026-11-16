package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Las piezas de Abismo: las aves y lo de tierra
// =====================================================================
//
// El pelícano y la garza (posados, nadando y en vuelo), la palmera, la mesa de café con sus sillas, la iglesia de
// madera con su cúpula azul y su campanario, la lonja de las esponjas, el pilote, la bita y la popa de un barco
// esponjero amarrado. Los edificios y la popa se arman en metros y se proyectan con la perspectiva de Mongol (`Sitio`);
// las aves y las palmeras se dibujan en el lienzo, pluma a pluma y hoja a hoja.

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
                // Las alas tendidas, un poco arqueadas, y el cuerpo en medio con el cuello recogido.
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
                    // Las patas cortas y oscuras, con los dedos palmeados.
                    for (s in listOf(-0.06f, 0.06f)) { drawLine(p.sombra(C.BREA, lejos), q(s, -0.02f), q(s + 0.02f, -alto), 0.035f * u, StrokeCap.Round); drawLine(p.sombra(C.BREA, lejos), q(s - 0.06f, 0f), q(s + 0.06f, 0f), 0.03f * u, StrokeCap.Round) }
                }
                val y0 = -alto
                // El cuerpo: un huso grueso, con la cola corta y las alas plegadas, plata en escamas.
                val cuerpo = Path().apply {
                    moveTo(q(-0.48f, y0 - 0.12f)); cubicTo(q(-0.35f, y0 - 0.3f), q(0.1f, y0 - 0.34f), q(0.28f, y0 - 0.22f))
                    cubicTo(q(0.36f, y0 - 0.1f), q(0.26f, y0 + 0.02f), q(0.05f, y0 + 0.03f)); cubicTo(q(-0.2f, y0 + 0.03f), q(-0.4f, y0 - 0.02f), q(-0.48f, y0 - 0.12f)); close()
                }
                drawPath(cuerpo, Brush.verticalGradient(listOf(p.luz(plata, lejos), p.medio(pardo, lejos), p.sombra(pardo, lejos)), pie.y + (y0 - 0.32f) * u, pie.y + (y0 + 0.03f) * u))
                clipPath(cuerpo) {
                    // Las plumas del ala en escamas, y la punta oscura de las primarias.
                    for (i in 0 until 7) for (j in 0 until 2) { val c = q(-0.32f + i * 0.08f + j * 0.04f, y0 - 0.2f + j * 0.06f); drawArc(p.sombra(pardo, lejos).copy(alpha = 0.6f), 0f, 180f, false, Offset(c.x - 0.04f * u, c.y - 0.02f * u), Size(0.08f * u, 0.05f * u), style = Stroke(0.012f * u)) }
                    drawPath(Path().apply { moveTo(q(-0.5f, y0 - 0.12f)); lineTo(q(-0.25f, y0 - 0.2f)); lineTo(q(-0.2f, y0 - 0.08f)); close() }, p.sombra(C.BREA, lejos))
                }
                if (postura == Postura.NADANDO) drawLine(p.luz(C.MAR_CLARO, lejos).copy(alpha = 0.6f), q(-0.42f, 0.02f), q(0.3f, 0.02f), 0.025f * u, StrokeCap.Round)
                // El cuello en S, la cabeza blanca con la nuca parda, y el pico largo con la bolsa, apoyado en el pecho.
                val cuello = Path().apply { moveTo(q(0.16f, y0 - 0.24f)); cubicTo(q(0.32f, y0 - 0.34f), q(0.18f, y0 - 0.5f), q(0.26f, y0 - 0.6f)) }
                drawPath(cuello, p.medio(lerp(cabeza, pardo, 0.35f), lejos), style = Stroke(0.1f * u, cap = StrokeCap.Round))
                drawPath(cuello, p.luz(cabeza, lejos).copy(alpha = 0.7f), style = Stroke(0.045f * u, cap = StrokeCap.Round))
                val cab = q(0.27f, y0 - 0.62f)
                drawOval(p.luz(cabeza, lejos), Offset(cab.x - 0.06f * u, cab.y - 0.05f * u), Size(0.12f * u, 0.1f * u))
                drawCircle(p.sombra(C.BREA, lejos), 0.012f * u, cab + Offset(m * 0.02f * u, -0.01f * u))
                val bolsa = Path().apply { moveTo(cab + Offset(m * 0.04f * u, 0.02f * u)); cubicTo(q(0.42f, y0 - 0.48f), q(0.42f, y0 - 0.34f), q(0.38f, y0 - 0.28f)); lineTo(q(0.33f, y0 - 0.3f)); cubicTo(q(0.33f, y0 - 0.42f), q(0.3f, y0 - 0.52f), cab); close() }
                drawPath(bolsa, Brush.horizontalGradient(listOf(p.luz(pico, lejos), p.sombra(pico, lejos)), cab.x, cab.x + m * 0.15f * u))
                if (az.real() > 0.5f) drawLine(p.sombra(C.MADERA_OSC, lejos), cab + Offset(-m * 0.05f * u, -0.03f * u), q(0.2f, y0 - 0.45f), 0.03f * u, StrokeCap.Round)   // la nuca parda
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
                drawLine(p.sombra(gris, lejos), q(-0.1f, 0.02f), q(-0.75f, 0.04f), 0.025f * u, StrokeCap.Round)        // las patas, atrás
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
     * Un palmito de Florida (sabal), de pie en [pie], de [alto] unidades: el tronco gris con las «botas» de las hojas viejas
     * en cruz por arriba, un poco curvado; la copa de hojas en abanico, cada una con sus pliegues que salen del centro y
     * las puntas partidas, la masa oscura detrás y las hojas secas colgando. [lejos], [semilla].
     */
    fun DrawScope.palmera(p: Pincel, pie: Offset, alto: Float, semilla: Long, lejos: Float = 0f, viento: Float = 0f) {
        val az = Azar(semilla)
        val ld = lado(p)
        val curva = az.entre(-0.12f, 0.12f) * alto
        val copa = Offset(pie.x + curva + viento * alto * 0.05f, pie.y - alto)
        val g = alto * 0.045f
        val tronco = Path().apply { moveTo(pie.x - g, pie.y); cubicTo(pie.x - g * 0.9f, pie.y - alto * 0.4f, copa.x - g * 0.8f + curva * 0.1f, copa.y + alto * 0.35f, copa.x - g * 0.75f, copa.y + alto * 0.05f); lineTo(copa.x + g * 0.75f, copa.y + alto * 0.05f); cubicTo(copa.x + g * 0.8f, copa.y + alto * 0.35f, pie.x + g * 0.9f, pie.y - alto * 0.4f, pie.x + g, pie.y); close() }
        val gris = lerp(C.PLOMO, C.MADERA_GRIS, 0.55f)
        drawPath(tronco, Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(gris, lejos) else p.luz(gris, lejos), p.medio(gris, lejos), if (ld > 0) p.luz(gris, lejos) else p.sombra(gris, lejos)), pie.x - g, pie.x + g))
        clipPath(tronco) {
            // Los anillos, y arriba las botas de las hojas viejas, cruzadas.
            var y = pie.y - g; while (y > copa.y + alto * 0.3f) { drawLine(p.sombra(gris, lejos).copy(alpha = 0.4f), Offset(pie.x - alto, y), Offset(pie.x + alto, y + g * 0.3f), g * 0.12f); y -= g * az.entre(0.6f, 1.1f) }
            for (i in 0 until 10) {
                val yy = copa.y + alto * (0.05f + 0.025f * i); val x = copa.x + curva * 0.02f * i
                drawLine(p.medio(lerp(gris, C.MADERA, 0.3f), lejos), Offset(x - g, yy + g * 0.5f), Offset(x + g * 0.3f, yy - g * 0.4f), g * 0.35f)
                drawLine(p.sombra(lerp(gris, C.MADERA_OSC, 0.3f), lejos), Offset(x + g, yy + g * 0.5f), Offset(x - g * 0.3f, yy - g * 0.4f), g * 0.3f)
            }
        }
        // Las hojas secas, colgando bajo la copa.
        for (i in 0 until 5) {
            val a = PI.toFloat() * (0.15f + 0.7f * az.real()); val l = alto * az.entre(0.14f, 0.24f)
            val b = copa + Offset(cos(a) * l * 0.4f, alto * 0.06f + sin(a) * l)
            drawLine(p.medio(lerp(C.ESPONJA_OSC, C.MADERA, 0.4f), lejos), copa + Offset(0f, alto * 0.04f), b, alto * 0.02f, StrokeCap.Round)
        }
        // La copa: abanicos en tres pisos, los de atrás más oscuros.
        for (piso in 0..2) {
            val n = listOf(7, 6, 5)[piso]
            for (i in 0 until n) {
                val a = -PI.toFloat() * (0.06f + 0.88f * (i + az.entre(0.1f, 0.9f)) / n) + (if (piso == 0) 0f else az.entre(-0.1f, 0.1f))
                val largo = alto * az.entre(0.2f, 0.3f) * (1f - 0.12f * piso)
                val cae = if (sin(a) > -0.3f) 0.4f else 0.1f
                val punta = copa + Offset(cos(a) * largo + viento * largo * 0.4f, sin(a) * largo * 0.75f + cae * largo * 0.5f)
                val tono = when (piso) { 0 -> lerp(C.PALMA, C.BREA, 0.35f); 1 -> C.PALMA; else -> lerp(C.PALMA, C.ESPONJA, 0.25f) }
                abanico(p, copa, punta, largo * 0.55f, tono, lejos, az, lado = if (cos(a) * ld > 0) 1f else -1f)
            }
        }
    }

    /** Una hoja en abanico de [base] a [punta], de [ancho]: los pliegues que salen del pecíolo, cada uno con su lado de luz, y las puntas partidas. */
    private fun DrawScope.abanico(p: Pincel, base: Offset, punta: Offset, ancho: Float, tono: Color, lejos: Float, az: Azar, lado: Float) {
        val d = punta - base; val largo = d.getDistance().coerceAtLeast(0.1f); val u = d / largo; val n = Offset(-u.y, u.x)
        val c0 = base + d * 0.35f
        drawLine(p.medio(lerp(tono, C.MADERA, 0.3f), lejos), base, c0, (ancho * 0.06f).coerceAtLeast(0.4f), StrokeCap.Round)
        val segs = 9
        for (j in 0 until segs) {
            val t = (j + 0.5f) / segs - 0.5f
            val fin = c0 + u * (largo * 0.65f * (1f - 0.5f * t * t)) + n * (ancho * t * 1.6f) + Offset(0f, largo * 0.15f * t * t)
            val luz = if (t * lado > 0) p.luz(tono, lejos) else p.sombra(tono, lejos)
            drawLine(lerp(luz, p.medio(tono, lejos), 0.3f), c0, fin, (ancho * 0.16f).coerceAtLeast(0.5f), StrokeCap.Round)
            if (ancho > 6f) drawLine(luz, fin, fin + (fin - c0) * 0.12f + Offset(0f, ancho * 0.05f), (ancho * 0.04f).coerceAtLeast(0.3f), StrokeCap.Round)
        }
    }

    // =================================================================
    //  El muelle: pilote, bita, mesa de café, silla
    // =================================================================

    /** Un pilote de madera clavado en el río, de [a] (la cabeza) a [b] (donde entra en el agua), de [g] unidades de grueso: gris, rajado, con la franja de verdín y percebes en la marea. */
    fun DrawScope.pilote(p: Pincel, a: Offset, b: Offset, g: Float, lejos: Float = 0f) {
        val ld = lado(p)
        val f = poligono(Offset(a.x - g / 2f, a.y), Offset(a.x + g / 2f, a.y), Offset(b.x + g / 2f, b.y), Offset(b.x - g / 2f, b.y))
        drawPath(f, Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(C.MADERA_GRIS, lejos) else p.luz(C.MADERA_GRIS, lejos), if (ld > 0) p.luz(C.MADERA_GRIS, lejos) else p.sombra(C.MADERA_GRIS, lejos)), a.x - g / 2f, a.x + g / 2f))
        drawRect(p.medio(lerp(C.VERDIN, C.BREA, 0.3f), lejos), Offset(b.x - g / 2f, b.y - g * 1.2f), Size(g, g * 1.2f))
        drawOval(p.luz(C.MADERA_GRIS, lejos), Offset(a.x - g / 2f, a.y - g * 0.18f), Size(g, g * 0.36f))
        if (g > 3f) drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.5f), Offset(a.x + g * 0.1f, a.y + g), Offset(a.x + g * 0.05f, b.y - g * 2f), g * 0.06f)
    }

    /** Una bita de hierro del muelle en [s], con el cabo dado vueltas. */
    fun DrawScope.bita(p: Pincel, s: Sitio, sol: Sol3, cabo: Boolean, lejos: Float = 0f) {
        val e = s.escala(0f, 0f)
        val base = s.p(0f, 0f, 0f); val top = s.p(0f, 0.36f, 0f); val r = 0.12f * e
        val ld = lado(p)
        drawRect(Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(C.PLOMO, lejos) else p.luz(C.PLOMO, lejos), if (ld > 0) p.luz(C.PLOMO, lejos) else p.sombra(C.PLOMO, lejos)), base.x - r, base.x + r), Offset(base.x - r, top.y), Size(r * 2f, base.y - top.y))
        drawOval(p.luz(C.PLOMO, lejos), Offset(top.x - r * 1.3f, top.y - r * 0.5f), Size(r * 2.6f, r))
        if (cabo) for (i in 0 until 3) drawOval(p.sombra(C.CANAMO, lejos), Offset(base.x - r * 1.15f, top.y + r * (0.6f + 0.5f * i)), Size(r * 2.3f, r * 0.7f), style = Stroke(r * 0.3f))
    }

    /**
     * Una mesa de café redonda en [s] (de hierro, con su tablero de mármol gris), con dos sillas de madera y enea, dos
     * tazas pequeñas, un tablero de tavli cerrado y, a veces, un comboloi dejado. Sin personas: lo que dejan.
     */
    fun DrawScope.mesaDeCafe(p: Pincel, s: Sitio, sol: Sol3, semilla: Long, lejos: Float = 0f) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        silla(p, Sitio(s.v, s.p(-0.42f, 0f, 0.18f), s.escala(-0.42f, 0.18f), s.giro + 0.9f), sol, lejos)
        // La pata y el pie de hierro.
        val tope = s.p(0f, 0.74f, 0f)
        drawLine(p.sombra(C.BREA, lejos), s.p(0f, 0f, 0f), tope, 0.04f * e)
        drawLine(p.sombra(C.BREA, lejos), s.p(-0.2f, 0f, 0f), s.p(0.2f, 0f, 0f), 0.03f * e)
        // El tablero: un óvalo en escorzo, con su canto.
        val n = 20
        val borde = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(0.32f * cos(a), 0.74f, 0.32f * sin(a)) }
        val canto = (0 until n).map { val a = it * 2f * PI.toFloat() / n; s.p(0.32f * cos(a), 0.71f, 0.32f * sin(a)) }
        drawPath(poligono(envolvente(borde + canto)), p.sombra(lerp(C.BLANCO, C.PLOMO, 0.4f), lejos))
        drawPath(poligono(borde), p.arriba(lerp(C.BLANCO, C.PLOMO, 0.2f), lejos))
        // Las tazas, el tablero de tavli, el comboloi.
        for (i in 0 until 2) { val c = s.p(az.entre(-0.18f, 0.18f), 0.74f, az.entre(-0.15f, 0.15f)); drawRect(p.luz(C.BLANCO, lejos), Offset(c.x - 0.025f * e, c.y - 0.05f * e), Size(0.05f * e, 0.05f * e)); drawOval(p.sombra(C.BREA, lejos), Offset(c.x - 0.025f * e, c.y - 0.06f * e), Size(0.05f * e, 0.02f * e)) }
        if (az.real() > 0.3f) { val c = s.p(0.08f, 0.75f, 0.05f); drawRect(p.medio(lerp(C.MADERA, C.ROJO, 0.3f), lejos), Offset(c.x - 0.1f * e, c.y - 0.03f * e), Size(0.2f * e, 0.05f * e)); drawLine(p.luz(C.LATON, lejos), Offset(c.x, c.y - 0.03f * e), Offset(c.x, c.y + 0.02f * e), 0.01f * e) }
        if (az.real() > 0.5f) for (i in 0 until 7) drawCircle(p.luz(C.LATON, lejos), 0.01f * e, s.p(-0.12f + 0.02f * i, 0.745f, 0.12f + 0.012f * sin(i.toFloat())))
        silla(p, Sitio(s.v, s.p(0.4f, 0f, -0.22f), s.escala(0.4f, -0.22f), s.giro - 2.2f), sol, lejos)
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
    }

    // =================================================================
    //  La iglesia de San Nicolás y la lonja
    // =================================================================

    /**
     * La iglesia de San Nicolás: «de madera, pequeña, blanca, con una cúpula que alguien pintó de azul para que se pareciera a
     * las de la isla», y su campanario. En [s] (su fachada mira hacia −z), en tres cuartos: las tablas blancas de chilla, el
     * tejado, el tambor octogonal con la cúpula azul y su cruz, el campanario de madera con la campana, la puerta y las
     * ventanas de medio punto ([ventanas], de 0 a 1: encendidas).
     */
    fun DrawScope.iglesia(p: Pincel, s: Sitio, sol: Sol3, ventanas: Float, lejos: Float) {
        val e = s.escala(0f, 0f)
        val w = 3.4f; val fondo = 7f; val alto = 5.2f
        // La nave: una caja de tablas blancas.
        for (cara in visibles(s, carasDeCaja(-w, w, 0f, alto, -fondo, fondo))) {
            val t = luzDe(s, sol, cara); val f = forma(s, cara)
            drawPath(f, p.por(C.BLANCO, t, lejos))
            clipPath(f) {
                for (k in 1 until 14) linea(s, cara, 0f, k / 14f, 1f, k / 14f, p.sombra(C.BLANCO, lejos).copy(alpha = 0.35f), (0.02f * e).coerceAtLeast(0.25f))
                if (!cara.arriba) {
                    val nv = if (cara.n.z < -0.5f) 1 else 4
                    for (i in 0 until nv) {
                        val u = if (nv == 1) 0.5f else (i + 0.5f) / nv
                        val q0 = s.p(cara.en(u - 0.06f, 0.35f)); val q1 = s.p(cara.en(u + 0.06f, 0.75f))
                        val luz = lerp(p.sombra(C.AZUL, lejos), Color(0xFFFFC870), ventanas)
                        drawRect(luz, Offset(minOf(q0.x, q1.x), minOf(q0.y, q1.y)), Size(kotlin.math.abs(q1.x - q0.x).coerceAtLeast(0.6f), kotlin.math.abs(q1.y - q0.y)))
                        drawOval(luz, Offset(minOf(q0.x, q1.x), minOf(q0.y, q1.y) - kotlin.math.abs(q1.x - q0.x) * 0.5f), Size(kotlin.math.abs(q1.x - q0.x).coerceAtLeast(0.6f), kotlin.math.abs(q1.x - q0.x)))
                    }
                    if (cara.n.z < -0.5f) { val q0 = s.p(cara.en(0.4f, 0f)); val q1 = s.p(cara.en(0.6f, 0.4f)); drawRect(p.medio(C.AZUL, lejos), Offset(minOf(q0.x, q1.x), q1.y), Size(kotlin.math.abs(q1.x - q0.x), q0.y - q1.y)) }
                }
            }
        }
        // El tejado a dos aguas (gris), y el frontón.
        val cumbrera0 = s.p(0f, alto + 1.6f, -fondo); val cumbrera1 = s.p(0f, alto + 1.6f, fondo)
        val izq = poligono(listOf(s.p(-w - 0.2f, alto, -fondo - 0.2f), cumbrera0, cumbrera1, s.p(-w - 0.2f, alto, fondo + 0.2f)))
        val der = poligono(listOf(s.p(w + 0.2f, alto, -fondo - 0.2f), cumbrera0, cumbrera1, s.p(w + 0.2f, alto, fondo + 0.2f)))
        drawPath(der, p.por(lerp(C.PLOMO, C.BLANCO, 0.3f), sol.t(s.normal(V3(0.6f, 0.8f, 0f))), lejos))
        drawPath(izq, p.por(lerp(C.PLOMO, C.BLANCO, 0.3f), sol.t(s.normal(V3(-0.6f, 0.8f, 0f))), lejos))
        drawPath(poligono(listOf(s.p(-w, alto, -fondo), cumbrera0, s.p(w, alto, -fondo))), p.por(C.BLANCO, sol.t(s.normal(V3(0f, 0f, -1f))), lejos))
        // El tambor y la cúpula azul, con su cruz.
        val tambor = s.p(0f, alto + 1.4f, 1f); val r = 2.2f * s.escala(0f, 1f)
        drawRect(Brush.horizontalGradient(listOf(p.luz(C.BLANCO, lejos), p.sombra(C.BLANCO, lejos)), tambor.x - r, tambor.x + r), Offset(tambor.x - r, tambor.y - r * 0.8f), Size(r * 2f, r * 0.8f))
        val cupula = Path().apply { moveTo(tambor.x - r * 1.02f, tambor.y - r * 0.78f); cubicTo(tambor.x - r, tambor.y - r * 1.9f, tambor.x + r, tambor.y - r * 1.9f, tambor.x + r * 1.02f, tambor.y - r * 0.78f); close() }
        val ld = lado(p)
        drawPath(cupula, Brush.radialGradient(listOf(p.luz(C.AZUL, lejos), p.medio(C.AZUL, lejos), p.sombra(C.AZUL, lejos)), Offset(tambor.x + ld * r * 0.4f, tambor.y - r * 1.4f), r * 1.8f))
        val tope = Offset(tambor.x, tambor.y - r * 1.62f)
        drawLine(p.medio(C.ORO, lejos), tope, tope + Offset(0f, -r * 0.6f), r * 0.07f)
        drawLine(p.medio(C.ORO, lejos), tope + Offset(-r * 0.18f, -r * 0.42f), tope + Offset(r * 0.18f, -r * 0.42f), r * 0.07f)
        // El campanario: una torre de madera a un lado de la fachada, con su campana.
        val sc = Sitio(s.v, s.p(w + 1.1f, 0f, -fondo + 1f), s.escala(w + 1.1f, -fondo + 1f), s.giro)
        for (cara in visibles(sc, carasDeCaja(-0.9f, 0.9f, 0f, alto + 4.2f, -0.9f, 0.9f))) drawPath(forma(sc, cara), p.por(C.BLANCO, luzDe(sc, sol, cara), lejos))
        val hueco = sc.p(0f, alto + 2.6f, -0.92f); val ek = sc.escala(0f, -0.9f)
        drawRect(p.sombra(C.BREA, lejos), Offset(hueco.x - 0.5f * ek, hueco.y - 0.7f * ek), Size(1.0f * ek, 1.2f * ek))
        drawCircle(p.luz(C.LATON, lejos), 0.3f * ek, hueco + Offset(0f, 0.1f * ek))
        val techoC = poligono(listOf(sc.p(-1.05f, alto + 4.2f, -1.05f), sc.p(0f, alto + 6f, 0f), sc.p(1.05f, alto + 4.2f, -1.05f), sc.p(1.05f, alto + 4.2f, 1.05f)))
        drawPath(techoC, p.por(C.AZUL, 0.2f, lejos))
        drawLine(p.medio(C.ORO, lejos), sc.p(0f, alto + 6f, 0f), sc.p(0f, alto + 6.8f, 0f), 0.12f * ek)
        if (ventanas > 0.02f) resplandor(s.p(0f, 1.4f, -fondo - 0.5f), 4f * e, 0.5f * ventanas, C.LUMBRE)
    }

    /**
     * La lonja de las esponjas: «un patio grande con cuartos alrededor, cada capitán tiene su puerta, donde guarda lo que
     * sacó el año». De madera, con el tejado de chapa a un agua; en [s] su fachada larga mira hacia −x (hacia el río), de
     * [largo] metros: las puertas de cada capitán, unas abiertas con las esponjas apiladas por tamaños dentro y fuera, la
     * puerta grande del patio, el cobertizo. [luces], de 0 a 1: las lámparas de las puertas de noche.
     */
    fun DrawScope.lonja(p: Pincel, s: Sitio, sol: Sol3, largo: Float, semilla: Long, luces: Float, lejos: Float) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val alto = 4.2f; val fondo = 6f
        val madera = lerp(C.MADERA_GRIS, C.BLANCO, 0.35f)
        for (cara in visibles(s, carasDeCaja(0f, fondo, 0f, alto, 0f, largo))) {
            val t = luzDe(s, sol, cara); val f = forma(s, cara)
            drawPath(f, p.por(madera, t, lejos))
            clipPath(f) {
                for (k in 1 until 10) linea(s, cara, 0f, k / 10f, 1f, k / 10f, p.sombra(madera, lejos).copy(alpha = 0.35f), (0.02f * e).coerceAtLeast(0.25f))
                if (cara.n.x < -0.5f) {
                    // Las puertas: cada capitán la suya.
                    val puertas = (largo / 3.2f).toInt()
                    for (i in 0 until puertas) {
                        val u0 = (i + 0.25f) / puertas; val u1 = (i + 0.75f) / puertas
                        val abierta = az.real() > 0.55f
                        val q = poligono(listOf(cara.en(u0, 0f), cara.en(u1, 0f), cara.en(u1, 0.62f), cara.en(u0, 0.62f)).map { s.p(it) })
                        drawPath(q, if (abierta) lerp(p.sombra(C.BREA, lejos), Color(0xFFFFB860), 0.6f * luces) else p.por(lerp(C.AZUL, madera, 0.35f), t, lejos))
                        if (!abierta) linea(s, cara, (u0 + u1) / 2f, 0f, (u0 + u1) / 2f, 0.62f, p.sombra(C.BREA, lejos).copy(alpha = 0.5f), (0.02f * e).coerceAtLeast(0.3f))
                        if (luces > 0.02f && i % 2 == 0) resplandor(s.p(cara.en((u0 + u1) / 2f, 0.7f)), 2.2f * s.escala(0f, largo * (u0 + u1) / 2f), 0.6f * luces, C.LUMBRE)
                    }
                }
            }
        }
        // El tejado de chapa, a un agua, gris, con las ondas.
        val techo = poligono(listOf(s.p(-0.3f, alto + 0.2f, -0.3f), s.p(-0.3f, alto + 0.2f, largo + 0.3f), s.p(fondo + 0.3f, alto + 1.2f, largo + 0.3f), s.p(fondo + 0.3f, alto + 1.2f, -0.3f)))
        drawPath(techo, p.por(lerp(C.PLOMO, C.BLANCO, 0.25f), sol.t(s.normal(V3(-0.3f, 0.95f, 0f))), lejos))
        clipPath(techo) { for (i in 0 until 30) { val z = largo * i / 30f; drawLine(p.sombra(C.PLOMO, lejos).copy(alpha = 0.4f), s.p(-0.3f, alto + 0.2f, z), s.p(fondo + 0.3f, alto + 1.2f, z), (0.03f * e).coerceAtLeast(0.25f)) } }
        drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.6f), s.p(-0.3f, alto + 0.2f, -0.3f), s.p(-0.3f, alto + 0.2f, largo + 0.3f), (0.05f * e).coerceAtLeast(0.4f))
    }

    // =================================================================
    //  La popa de un barco esponjero amarrado
    // =================================================================

    /**
     * Un barco esponjero amarrado, visto desde popa en tres cuartos (para la fila del muelle): en [s] (su z, hacia proa;
     * la popa en z = 0), de [largo] metros. El espejo de popa blanco con la franja azul y el timón por fuera con su caña,
     * la regala, el costado que se aleja, la caseta, los dos palos con las velas aferradas, la jarcia, las ristras y los
     * sacos ([carga], de 0 a 1), el farol o la lamparilla de popa de noche ([lumbre]) y los farolillos de fin de año.
     * Cada uno distinto ([semilla]): el tono del blanco, la altura de los palos, lo que lleva.
     */
    fun DrawScope.popaAmarrada(p: Pincel, s: Sitio, sol: Sol3, largo: Float, semilla: Long, carga: Float, lumbre: Float, lejos: Float, empavesado: Boolean = false, farolillos: Boolean = false) {
        val az = Azar(semilla); val e = s.escala(0f, 0f)
        val manga = 2.1f; val franco = 0.7f; val agua = -0.95f
        val blanco = lerp(C.BLANCO, C.ARENA, az.entre(0f, 0.25f))
        val azul = lerp(C.AZUL, C.MAR_HONDO, az.entre(0f, 0.4f))
        fun media(z: Float): Float = if (z < largo * 0.55f) manga * (0.82f + 0.18f * (z / (largo * 0.55f)).coerceAtMost(1f)) else manga * kotlin.math.sqrt((1f - ((z - largo * 0.55f) / (largo * 0.45f)).let { it * it }).coerceAtLeast(0f))
        // El costado que se ve (el que mira a quien mira).
        val ladoVisto = if (s.seVe(V3(manga, 0f, largo * 0.4f), V3(1f, 0f, 0f))) 1f else -1f
        val zs = (0..14).map { largo * it / 14f }
        val regala = zs.map { V3(ladoVisto * media(it), agua + franco + 0.25f + 0.5f * (it / largo).let { t -> t * t }, it) }
        val flot = zs.map { V3(ladoVisto * media(it) * 0.9f, agua, it) }
        val costado = poligono((regala + flot.reversed()).map { s.p(it) })
        val tc = sol.t(s.normal(V3(ladoVisto, 0f, 0f)))
        drawPath(costado, p.por(blanco, tc, lejos))
        clipPath(costado) {
            drawPath(poligono((regala.map { s.p(it) } + regala.reversed().map { s.p(it.copy(y = it.y - 0.22f)) })), p.por(azul, tc, lejos))
            drawPath(poligono((flot.map { s.p(it.copy(y = agua + 0.2f)) } + flot.reversed().map { s.p(it) })), p.medio(lerp(C.VERDIN, C.BREA, 0.3f), lejos))
        }
        // La cubierta (lo que se ve por encima de la regala de la otra banda) y la caseta.
        val cub = poligono((zs.map { V3(media(it) * 0.98f, agua + franco, it) } + zs.reversed().map { V3(-media(it) * 0.98f, agua + franco, it) }).map { s.p(it) })
        drawPath(cub, p.arriba(C.MADERA_GRIS, lejos))
        val caseta = carasDeCaja(-0.8f, 0.8f, agua + franco, agua + franco + 0.9f, largo * 0.2f, largo * 0.38f)
        for (cara in visibles(s, caseta)) drawPath(forma(s, cara), p.por(lerp(blanco, C.MADERA_GRIS, 0.3f), luzDe(s, sol, cara), lejos))
        // El espejo de popa: blanco, con la franja azul, el nombre sin letras (una raya) y el timón con su caña.
        val espejo = poligono(listOf(V3(-manga * 0.82f, agua + franco + 0.25f, 0f), V3(manga * 0.82f, agua + franco + 0.25f, 0f), V3(manga * 0.6f, agua + 0.1f, -0.05f), V3(0f, agua - 0.1f, -0.08f), V3(-manga * 0.6f, agua + 0.1f, -0.05f)).map { s.p(it) })
        val te = sol.t(s.normal(V3(0f, 0.2f, -1f)))
        drawPath(espejo, p.por(blanco, te, lejos))
        clipPath(espejo) {
            drawRect(p.por(azul, te, lejos), Offset(s.p(-manga, 0f, 0f).x - 100f, s.p(0f, agua + franco + 0.1f, 0f).y), Size(400f, (0.22f * e).coerceAtLeast(0.6f)))
            drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.5f), s.p(-0.5f, agua + franco - 0.2f, -0.03f), s.p(0.5f, agua + franco - 0.2f, -0.03f), (0.05f * e).coerceAtLeast(0.3f))
        }
        val timon0 = s.p(0f, agua + franco + 0.3f, -0.12f); val timon1 = s.p(0f, agua - 0.2f, -0.3f)
        drawLine(p.sombra(C.MADERA_OSC, lejos), timon0, timon1, (0.16f * e).coerceAtLeast(0.6f))
        drawLine(p.medio(C.MADERA, lejos), timon0, s.p(0f, agua + franco + 0.55f, 1.2f), (0.06f * e).coerceAtLeast(0.4f), StrokeCap.Round)
        // Los dos palos, con la vela aferrada en su botavara, y la jarcia.
        val alturas = listOf(largo * 0.3f to az.entre(8.5f, 10f), largo * 0.68f to az.entre(7.5f, 9f))
        for ((zp, h) in alturas) {
            val pie = s.p(0f, agua + franco, zp); val tope = s.p(0f, agua + franco + h, zp); val g = (0.22f * s.escala(0f, zp)).coerceAtLeast(0.5f)
            drawLine(p.medio(C.MADERA, lejos), pie, tope, g)
            drawLine(p.luz(C.MADERA, lejos).copy(alpha = 0.5f), pie + Offset(g * 0.25f * lado(p), 0f), tope + Offset(g * 0.25f * lado(p), 0f), g * 0.3f)
            val b0 = s.p(0f, agua + franco + 1.4f, zp); val b1 = s.p(0f, agua + franco + 1.7f, zp - 3.4f)
            drawLine(p.por(C.LONA, 0.3f, lejos), b0, b1, (0.32f * s.escala(0f, zp)).coerceAtLeast(0.7f), StrokeCap.Round)
            for (sl in listOf(-1f, 1f)) drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.7f), tope, s.p(sl * media(zp), agua + franco + 0.3f, zp - 0.4f), 0.35f)
            // Las ristras y los sacos, según lo que lleve.
            val n = (carga * 6f).toInt()
            for (i in 0 until n) {
                val q = s.p(az.entre(-0.4f, 0.4f), agua + franco + az.entre(2.2f, 3.6f), zp + az.entre(-1.6f, 1.2f)); val kk = s.escala(0f, zp)
                drawLine(lerp(p.medio(C.ESPONJA, lejos), p.sombra(C.ESPONJA, lejos), az.real()), q, q + Offset(0f, az.entre(0.6f, 1.1f) * kk), (0.16f * kk).coerceAtLeast(0.7f), StrokeCap.Round)
            }
            if (empavesado || farolillos) {
                val pts = combada(tope, s.p(0f, agua + franco + 0.4f, if (zp < largo * 0.5f) -0.2f else largo), 0.6f * s.escala(0f, zp), 8)
                pts.forEachIndexed { i, q -> if (farolillos) drawCircle(lerp(Color(0xFFFF9A50), Color(0xFFFFD27A), (i % 3) / 2f).copy(alpha = 0.4f + 0.6f * lumbre), (0.1f * s.escala(0f, zp)).coerceAtLeast(0.6f), q) else drawRect(if (i % 2 == 0) p.luz(C.BLANCO, lejos) else p.medio(C.AZUL, lejos), Offset(q.x - 0.4f, q.y), Size(0.9f, 1.4f)) }
            }
        }
        // La regala de popa y la lamparilla del icono, encendida siempre.
        drawLine(p.por(azul, te + 0.3f, lejos), s.p(-manga * 0.82f, agua + franco + 0.25f, 0f), s.p(manga * 0.82f, agua + franco + 0.25f, 0f), (0.07f * e).coerceAtLeast(0.5f))
        val lamp = s.p(manga * 0.5f, agua + franco + 0.55f, 0.1f)
        resplandor(lamp, (0.8f * e).coerceAtLeast(3f), (0.15f + 0.85f * p.l.noche) * lumbre, C.LUMBRE)
        drawCircle(Color(0xFFFFD890).copy(alpha = (0.3f + 0.7f * p.l.noche) * lumbre), (0.06f * e).coerceAtLeast(0.6f), lamp)
    }

    /** Dónde arde la lamparilla de una popa amarrada (para su reflejo en el río). */
    fun lamparillaDePopa(s: Sitio): Offset = s.p(2.1f * 0.5f, -0.95f + 0.7f + 0.55f, 0.1f)
}
