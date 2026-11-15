package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Las piezas de Botánico
// =====================================================================
//
// Lo que usan los dos cuadros (el bergantín y la selva) o pide cuidado: los cajones de especímenes, la prensa de
// plantas, las cajas de frascos con serrín, la mesa de trabajo, el bergantín lejano fondeado; las piezas grandes del
// barco visto de cerca (la rueda del timón con la bitácora, la claraboya, el palo con su cabillero y sus obenques, un
// bote sobre sus calzos, un cañón, las velas con su botavara y la escota con sus motones); y las de la selva (la hoja de
// plátano, la palmera, el helecho arborescente, la liana y el naranjo con fruta).
//
// Los cuerpos se arman en metros, en sus propios ejes, y se proyectan con la perspectiva cónica de Mongol (`V3`,
// `Mirada`, `Sitio`, `Sol3` en `EscenaMongolPiezas.kt`, usada tal cual): así se ven en tres cuartos, con dos caras o su
// curva, y con su lado de luz y su lado de sombra (fallo 1 de la tanda S11). Las plantas, que no tienen caras, se
// dibujan en el lienzo, hoja por hoja.

/** Una cara plana de un cuerpo: sus cuatro esquinas en los ejes de la cosa (abajo-izquierda, abajo-derecha, arriba-derecha, arriba-izquierda) y su normal. */
internal class Cara3(val q: List<V3>, val n: V3) {
    val centro: V3 get() = (q[0] + q[1] + q[2] + q[3]) * 0.25f

    /** El punto de la cara en ([u], [v]), de 0 a 1 a lo ancho y a lo alto. */
    fun en(u: Float, v: Float): V3 { val a = q[0] + (q[1] - q[0]) * u; val b = q[3] + (q[2] - q[3]) * u; return a + (b - a) * v }

    /** Si es la cara de arriba (la que mira al cielo). */
    val arriba: Boolean get() = n.y > 0.5f
}

/** Las seis caras de una caja de [x0] a [x1], de [y0] a [y1] y de [z0] a [z1] (en los ejes de la cosa). */
internal fun carasDeCaja(x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float): List<Cara3> = listOf(
    Cara3(listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)), V3(0f, 0f, -1f)),
    Cara3(listOf(V3(x1, y0, z1), V3(x0, y0, z1), V3(x0, y1, z1), V3(x1, y1, z1)), V3(0f, 0f, 1f)),
    Cara3(listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)), V3(1f, 0f, 0f)),
    Cara3(listOf(V3(x0, y0, z1), V3(x0, y0, z0), V3(x0, y1, z0), V3(x0, y1, z1)), V3(-1f, 0f, 0f)),
    Cara3(listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)), V3(0f, 1f, 0f)),
    Cara3(listOf(V3(x0, y0, z1), V3(x1, y0, z1), V3(x1, y0, z0), V3(x0, y0, z0)), V3(0f, -1f, 0f)),
)

/** Las caras de [caras] que se ven desde los ojos, de la más honda a la más cercana. */
internal fun visibles(s: Sitio, caras: List<Cara3>): List<Cara3> = caras.filter { s.seVe(it.centro, it.n) }.sortedByDescending { s.hondo(it.centro) }

/** La forma en el lienzo de una cara. */
internal fun forma(s: Sitio, c: Cara3): Path = poligono(c.q.map { s.p(it) })

/** Cuánto le da la luz a una cara, de −1 a 1: la de arriba recibe algo más (mira al cielo). */
internal fun luzDe(s: Sitio, sol: Sol3, c: Cara3): Float = (sol.t(s.normal(c.n)) + if (c.arriba) 0.3f else 0f).coerceIn(-1f, 1f)

/** Una línea dentro de una cara, de ([u0], [v0]) a ([u1], [v1]). */
internal fun DrawScope.linea(s: Sitio, c: Cara3, u0: Float, v0: Float, u1: Float, v1: Float, color: Color, grueso: Float, cap: StrokeCap = StrokeCap.Butt) =
    drawLine(color, s.p(c.en(u0, v0)), s.p(c.en(u1, v1)), grueso, cap)

/**
 * La sombra en el suelo (a la altura [suelo] de la cosa) de un cuerpo con estas [esquinas]: cada una se tumba según el
 * sol y la envolvente da la mancha. Va dentro de una `capaSombra`.
 */
internal fun sombraDe(s: Sitio, sol: Sol3, esquinas: List<V3>, suelo: Float = 0f): Path {
    val pts = esquinas.map { a -> val h = (a.y - suelo).coerceAtLeast(0f); s.pm(s.mx(a.x, a.z) + sol.sx * h, suelo, s.mz(a.x, a.z) + sol.sz * h) }
    return poligono(envolvente(pts))
}

/** Las ocho esquinas de una caja. */
internal fun esquinas(x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float): List<V3> =
    listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y0, z1), V3(x0, y0, z1), V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1))

/** Una cuerda que cuelga de [a] a [b] y baja [caida] en medio (puntos para una curva). */
internal fun combada(a: Offset, b: Offset, caida: Float, pasos: Int = 10): List<Offset> = (0..pasos).map { i -> val k = i / pasos.toFloat(); a + (b - a) * k + Offset(0f, caida * 4f * k * (1f - k)) }

/**
 * Las piezas de los dos cuadros de Botánico. Cada una con su lado de luz y de sombra, su textura y sus señales de uso,
 * a cualquier tamaño. Todo color pasa por el [Pincel] (que de noche suma la lumbre de cerca con `Pincel.con`), salvo
 * lo que da luz.
 */
internal object PiezasBotanico {
    private val C = ColorBotanico

    // =================================================================
    //  Cuerdas, motones y palos
    // =================================================================

    /**
     * Un cabo de [a] a [b] que cuelga [caida] (unidades del lienzo), de [grueso] unidades: la sombra que echa debajo, el
     * cuerpo, el filo de luz y, si es grueso, las vueltas de la colcha.
     */
    fun DrawScope.cabo(p: Pincel, a: Offset, b: Offset, caida: Float, grueso: Float, color: Color = C.CANAMO, lejos: Float = 0f, sombra: Boolean = true) {
        val camino = curva(combada(a, b, caida, if ((b - a).getDistance() > 60f) 14 else 8))
        val g = grueso.coerceAtLeast(0.3f)
        if (sombra && g > 0.7f) drawPath(camino, p.sombra(lerp(color, C.BREA, 0.4f), lejos).copy(alpha = 0.3f), style = Stroke(g * 1.1f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        drawPath(camino, p.sombra(color, lejos), style = Stroke(g, cap = StrokeCap.Round))
        if (g > 0.6f) drawPath(Path().apply { addPath(camino, Offset(-g * 0.18f, -g * 0.18f)) }, p.luz(lerp(color, C.LONA, 0.2f), lejos).copy(alpha = 0.7f), style = Stroke(g * 0.38f, cap = StrokeCap.Round))
        if (g > 1.3f) drawPath(camino, p.medio(lerp(color, C.BREA, 0.25f), lejos).copy(alpha = 0.55f), style = Stroke(g * 0.9f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.22f, g * 0.75f))))
    }

    /** Un motón de [tam] unidades de largo en [c], girado [ang] radianes: la caja de madera con su roldana y la gaza de cabo que lo rodea. */
    fun DrawScope.moton(p: Pincel, c: Offset, tam: Float, ang: Float, lejos: Float = 0f) {
        val d = Offset(sin(ang), -cos(ang)); val n = Offset(d.y * -1f, d.x)
        val forma = Path().apply {
            moveTo(c - d * (tam * 0.5f)); cubicTo(c - d * (tam * 0.5f) + n * (tam * 0.42f), c + d * (tam * 0.5f) + n * (tam * 0.42f), c + d * (tam * 0.5f))
            cubicTo(c + d * (tam * 0.5f) - n * (tam * 0.42f), c - d * (tam * 0.5f) - n * (tam * 0.42f), c - d * (tam * 0.5f)); close()
        }
        drawPath(forma, Brush.linearGradient(listOf(p.luz(C.MADERA, lejos), p.medio(C.MADERA, lejos), p.sombra(C.MADERA_OSC, lejos)), c + n * (tam * 0.35f), c - n * (tam * 0.35f)))
        drawLine(p.sombra(C.BREA, lejos), c - d * (tam * 0.3f), c + d * (tam * 0.3f), tam * 0.12f, StrokeCap.Round)
        drawPath(forma, p.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.7f), style = Stroke((tam * 0.07f).coerceAtLeast(0.3f)))
        drawCircle(p.luz(C.HIERRO, lejos), tam * 0.09f, c - d * (tam * 0.06f))
        // La gaza: arriba sale el cabo que lo sujeta.
        drawLine(p.sombra(C.CANAMO, lejos), c - d * (tam * 0.5f), c - d * (tam * 0.82f), tam * 0.14f, StrokeCap.Round)
    }

    /**
     * Un palo tendido (una verga, la botavara, el pico) de [a] a [b], de [g0] a [g1] unidades de grueso: redondo, con el
     * filo de luz del lado de arriba, la sombra debajo, la veta y las zunchos de hierro en [zunchos] (fracciones del largo).
     */
    fun DrawScope.palito(p: Pincel, a: Offset, b: Offset, g0: Float, g1: Float, color: Color = C.MADERA, lejos: Float = 0f, zunchos: List<Float> = emptyList(), luzArriba: Boolean = true) {
        val d = b - a; val largo = d.getDistance().coerceAtLeast(0.01f); val u = d / largo; var n = Offset(-u.y, u.x); if (n.y > 0f == luzArriba) n = -n
        val forma = poligono(a + n * (g0 / 2f), b + n * (g1 / 2f), b - n * (g1 / 2f), a - n * (g0 / 2f))
        drawPath(forma, Brush.linearGradient(0f to p.luz(color, lejos), 0.3f to p.medio(color, lejos), 1f to p.sombra(lerp(color, C.BREA, 0.25f), lejos), start = a + n * (g0 / 2f), end = a - n * (g0 / 2f)))
        if (g0 > 2.5f) for (k in 1..3) drawLine(p.sombra(color, lejos).copy(alpha = 0.3f), a + n * (g0 * (0.3f - 0.18f * k)), b + n * (g1 * (0.3f - 0.18f * k)), 0.4f)
        for (t in zunchos) { val q = a + d * t; val g = g0 + (g1 - g0) * t; drawLine(p.sombra(C.HIERRO, lejos), q + n * (g * 0.56f), q - n * (g * 0.56f), (g * 0.22f).coerceAtLeast(0.5f)); drawLine(p.luz(C.HIERRO, lejos).copy(alpha = 0.6f), q + n * (g * 0.56f), q + n * (g * 0.1f), (g * 0.1f).coerceAtLeast(0.3f)) }
    }

    // =================================================================
    //  El cajón de especímenes
    // =================================================================

    /**
     * Un cajón de especímenes en [s], de [ancho] × [alto] × [fondo] metros, apoyado a [base] metros del suelo (sobre otro
     * cajón). De tablas de pino, clavado, con sus listones en las esquinas; [desgaste] de 0 (recién clavado) a 1
     * (golpeado, mojado y rozado). [forrado]: envuelto en papel alquitranado, listo para despachar. [trincado]: con su
     * trinca de cabo alrededor. [marca]: la marca pintada del cajón (una flecha y unas rayas: sin cifras ni letras).
     * [escarcha] y [nieve], de 0 a 1, sobre la tapa.
     */
    class Cajon(
        val s: Sitio, val semilla: Long, val ancho: Float = 0.78f, val alto: Float = 0.46f, val fondo: Float = 0.52f, val base: Float = 0f,
        val desgaste: Float = 0.5f, val forrado: Boolean = false, val trincado: Boolean = true, val marca: Boolean = true, val nieve: Float = 0f,
    ) {
        val x0 get() = -ancho / 2f; val x1 get() = ancho / 2f; val y0 get() = base; val y1 get() = base + alto; val z0 get() = -fondo / 2f; val z1 get() = fondo / 2f

        /** El centro de la tapa, en el lienzo: donde se apoya lo que se deja encima. */
        val tapa: Offset get() = s.p(0f, y1, 0f)
    }

    /** La sombra del cajón en el suelo. */
    fun sombraDeCajon(c: Cajon, sol: Sol3): Path = sombraDe(c.s, sol, esquinas(c.x0, c.x1, c.y0, c.y1, c.z0, c.z1))

    fun DrawScope.cajon(c: Cajon, p: Pincel, sol: Sol3) {
        val s = c.s; val az = Azar(c.semilla); val e = s.escala(0f, 0f)
        val pino = lerp(lerp(C.MADERA, C.ARENA, az.entre(0.25f, 0.5f)), C.ROCA, 0.18f * c.desgaste * az.real())
        val papel = lerp(C.BREA, C.MADERA_OSC, 0.25f)
        for (cara in visibles(s, carasDeCaja(c.x0, c.x1, c.y0, c.y1, c.z0, c.z1))) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            if (c.forrado) {
                drawPath(f, p.por(papel, t))
                clipPath(f) {
                    // Los pliegues del papel alquitranado y su brillo de brea.
                    repeat(5) { val u = az.entre(0.1f, 0.9f); linea(s, cara, u, 0f, u + az.entre(-0.08f, 0.08f), 1f, p.luz(papel).copy(alpha = 0.35f), (0.012f * e).coerceIn(0.4f, 1.6f)) }
                    linea(s, cara, 0f, 0.92f, 1f, 0.95f, p.luz(lerp(papel, C.LONA, 0.3f)).copy(alpha = 0.3f), (0.02f * e).coerceIn(0.4f, 2f))
                }
                drawPath(f, p.sombra(C.BREA).copy(alpha = 0.6f), style = Stroke(0.5f))
                continue
            }
            drawPath(f, p.por(pino, t))
            clipPath(f) {
                val tablas = if (cara.arriba) 4 else 3
                // Cada tabla con su tono, las juntas oscuras y el canto de luz bajo cada junta.
                for (k in 0 until tablas) {
                    val v0 = k / tablas.toFloat(); val v1 = (k + 1) / tablas.toFloat()
                    val tono = lerp(pino, if (k % 2 == 0) C.ARENA else C.MADERA_OSC, az.entre(0.04f, 0.16f))
                    drawPath(poligono(listOf(cara.en(0f, v0), cara.en(1f, v0), cara.en(1f, v1), cara.en(0f, v1)).map { s.p(it) }), p.por(tono, t).copy(alpha = 0.7f))
                    // La veta, en trazos largos que siguen la tabla.
                    repeat(3) { val v = v0 + (v1 - v0) * az.entre(0.2f, 0.8f); linea(s, cara, az.entre(0f, 0.3f), v, az.entre(0.6f, 1f), v + az.entre(-0.02f, 0.02f), p.sombra(pino).copy(alpha = 0.25f), (0.006f * e).coerceIn(0.3f, 0.8f)) }
                    if (k > 0) { linea(s, cara, 0f, v0, 1f, v0, p.sombra(C.BREA).copy(alpha = 0.75f), (0.012f * e).coerceIn(0.4f, 1.6f)); linea(s, cara, 0f, v0 - 0.025f, 1f, v0 - 0.025f, p.luz(pino).copy(alpha = 0.45f), (0.006f * e).coerceIn(0.3f, 0.8f)) }
                }
                if (!cara.arriba) {
                    // Los listones de las esquinas, que sobresalen: más claros del lado de la luz, con su sombra al lado.
                    for (u in listOf(0f, 0.92f)) {
                        drawPath(poligono(listOf(cara.en(u, 0f), cara.en(u + 0.08f, 0f), cara.en(u + 0.08f, 1f), cara.en(u, 1f)).map { s.p(it) }), p.por(lerp(pino, C.ARENA, 0.2f), t + 0.12f))
                        linea(s, cara, if (u == 0f) 0.085f else 0.915f, 0f, if (u == 0f) 0.085f else 0.915f, 1f, p.sombra(C.BREA).copy(alpha = 0.4f), (0.008f * e).coerceIn(0.3f, 1f))
                        // La clavazón.
                        if (e > 40f) for (v in listOf(0.18f, 0.5f, 0.82f)) drawCircle(p.sombra(C.HIERRO), (0.008f * e).coerceIn(0.35f, 1.2f), s.p(cara.en(u + 0.04f, v)))
                    }
                    // El agua que subió por abajo: una mancha oscura y su cerco.
                    drawPath(poligono(listOf(cara.en(0f, 0f), cara.en(1f, 0f), cara.en(1f, 0.12f + 0.12f * c.desgaste), cara.en(0f, 0.1f + 0.1f * c.desgaste)).map { s.p(it) }), p.sombra(lerp(pino, C.BREA, 0.4f)).copy(alpha = 0.35f + 0.25f * c.desgaste))
                    // Los golpes y los roces: rayas claras donde la madera se astilla.
                    repeat((3 + 6 * c.desgaste).toInt()) { val u = az.entre(0.1f, 0.85f); val v = az.entre(0.15f, 0.85f); linea(s, cara, u, v, u + az.entre(0.04f, 0.12f), v + az.entre(-0.05f, 0.05f), p.luz(lerp(pino, C.LONA, 0.5f)).copy(alpha = 0.5f), (0.006f * e).coerceIn(0.3f, 0.9f), StrokeCap.Round) }
                    // La marca pintada en el frente (la flecha y unas rayas), gastada.
                    if (c.marca && cara.n.z < -0.5f && e > 30f) {
                        val tinta = p.por(C.BREA, t).copy(alpha = 0.55f + 0.2f * (1f - c.desgaste))
                        val g = (0.018f * e).coerceIn(0.5f, 2.6f)
                        linea(s, cara, 0.32f, 0.28f, 0.32f, 0.7f, tinta, g); linea(s, cara, 0.32f, 0.72f, 0.24f, 0.56f, tinta, g); linea(s, cara, 0.32f, 0.72f, 0.4f, 0.56f, tinta, g)
                        for (k in 0 until 1 + (c.semilla % 4).toInt().let { if (it < 0) -it else it }) linea(s, cara, 0.55f + k * 0.07f, 0.34f, 0.55f + k * 0.07f, 0.64f, tinta, g * 0.8f)
                    }
                } else {
                    // La tapa: el papel alquitranado asoma por la junta, y los dos travesaños con sus clavos.
                    linea(s, cara, 0f, 0.02f, 1f, 0.02f, p.sombra(C.BREA).copy(alpha = 0.7f), (0.016f * e).coerceIn(0.4f, 2f))
                    for (u in listOf(0.14f, 0.86f)) { drawPath(poligono(listOf(cara.en(u - 0.05f, 0f), cara.en(u + 0.05f, 0f), cara.en(u + 0.05f, 1f), cara.en(u - 0.05f, 1f)).map { s.p(it) }), p.por(lerp(pino, C.ARENA, 0.15f), t + 0.1f))
                        if (e > 40f) for (v in listOf(0.2f, 0.5f, 0.8f)) drawCircle(p.sombra(C.HIERRO), (0.008f * e).coerceIn(0.35f, 1.2f), s.p(cara.en(u, v))) }
                    if (c.marca && e > 30f) for (k in 0 until 3) linea(s, cara, 0.4f + k * 0.07f, 0.3f, 0.42f + k * 0.07f, 0.7f, p.por(C.BREA, t).copy(alpha = 0.4f), (0.014f * e).coerceIn(0.4f, 2f))
                    if (c.nieve > 0.02f) drawPath(f, p.luz(C.NIEVE).copy(alpha = 0.85f * c.nieve))
                }
            }
            // Los cantos: el de arriba, rozado y claro; el contorno, apenas.
            drawPath(f, p.sombra(lerp(pino, C.BREA, 0.5f)).copy(alpha = 0.5f), style = Stroke((0.006f * e).coerceIn(0.3f, 1f)))
        }
        // La arista de arriba que da a quien mira, rozada.
        drawLine(p.luz(lerp(pino, C.LONA, 0.5f)).copy(alpha = 0.6f), s.p(c.x0, c.y1, c.z0), s.p(c.x1, c.y1, c.z0), (0.01f * e).coerceIn(0.3f, 1.4f))
        if (c.trincado) trinca(c, p, sol)
    }

    /** La trinca de un cajón: dos vueltas de cabo que lo ciñen de lado a lado, por encima de la tapa, con su nudo. */
    private fun DrawScope.trinca(c: Cajon, p: Pincel, sol: Sol3) {
        val s = c.s; val e = s.escala(0f, 0f); val g = (0.022f * e).coerceIn(0.5f, 3.4f)
        for (x in listOf(c.x0 + c.ancho * 0.24f, c.x1 - c.ancho * 0.24f)) {
            val v = listOf(V3(x, c.y0 + 0.01f, c.z0 - 0.01f), V3(x, c.y1 + 0.01f, c.z0 - 0.01f), V3(x, c.y1 + 0.012f, c.z1 + 0.01f))
            for (i in 0 until v.size - 1) cabo(p, s.p(v[i]), s.p(v[i + 1]), 0f, g)
            drawCircle(p.sombra(C.CANAMO), g * 0.9f, s.p(V3(x, c.y1 + 0.01f, c.z0 - 0.01f)))
            drawCircle(p.luz(C.CANAMO).copy(alpha = 0.7f), g * 0.4f, s.p(V3(x, c.y1 + 0.01f, c.z0 - 0.01f)) + Offset(-g * 0.25f, -g * 0.3f))
        }
    }

    // =================================================================
    //  La prensa de plantas
    // =================================================================

    /**
     * La prensa de herbario, tumbada en [s]: dos rejillas de madera con el fajo de papel secante en medio, apretadas
     * con dos correas de cuero con sus hebillas de latón. Por los bordes asoman las hojas de lo que se está secando.
     * Medidas en metros; [base] es la altura donde se apoya (la mesa, la tapa de un cajón).
     */
    class Prensa(val s: Sitio, val semilla: Long, val ancho: Float = 0.48f, val fondo: Float = 0.32f, val grueso: Float = 0.12f, val base: Float = 0f)

    fun sombraDePrensa(pr: Prensa, sol: Sol3): Path = sombraDe(pr.s, sol, esquinas(-pr.ancho / 2f, pr.ancho / 2f, pr.base, pr.base + pr.grueso, -pr.fondo / 2f, pr.fondo / 2f), pr.base)

    fun DrawScope.prensa(pr: Prensa, p: Pincel, sol: Sol3) {
        val s = pr.s; val az = Azar(pr.semilla); val e = s.escala(0f, 0f)
        val w = pr.ancho / 2f; val d = pr.fondo / 2f; val y0 = pr.base; val tabla = 0.022f; val y1 = y0 + pr.grueso
        // La rejilla de abajo, el fajo (un poco más ancho, con los papeles desiguales) y la rejilla de arriba.
        fun rejilla(ya: Float) {
            for (cara in visibles(s, carasDeCaja(-w, w, ya, ya + tabla, -d, d))) {
                val f = forma(s, cara); val t = luzDe(s, sol, cara)
                if (cara.arriba) {
                    // Los listones de la rejilla, con el papel que se ve entre ellos.
                    drawPath(f, p.por(C.PAPEL, t - 0.15f))
                    for (k in 0..5) { val u = k / 5f * 0.92f + 0.04f; drawPath(poligono(listOf(cara.en(u - 0.035f, 0f), cara.en(u + 0.035f, 0f), cara.en(u + 0.035f, 1f), cara.en(u - 0.035f, 1f)).map { s.p(it) }), p.por(C.MADERA, t)) }
                    for (v in listOf(0.06f, 0.94f)) drawPath(poligono(listOf(cara.en(0f, v - 0.05f), cara.en(1f, v - 0.05f), cara.en(1f, v + 0.05f), cara.en(0f, v + 0.05f)).map { s.p(it) }), p.por(lerp(C.MADERA, C.ARENA, 0.2f), t + 0.1f))
                } else drawPath(f, p.por(C.MADERA_OSC, t))
            }
        }
        rejilla(y0)
        // El fajo de papel: las hojas, una sobre otra, con los cantos que no casan.
        for (cara in visibles(s, carasDeCaja(-w * 1.03f, w * 1.03f, y0 + tabla, y1 - tabla, -d * 1.04f, d * 1.04f))) {
            if (cara.arriba) continue
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(C.PAPEL, t))
            clipPath(f) { for (k in 1..11) { val v = k / 12f; linea(s, cara, 0f, v + az.entre(-0.02f, 0.02f), 1f, v + az.entre(-0.02f, 0.02f), (if (k % 3 == 0) p.sombra(C.CUERO) else p.sombra(C.PAPEL)).copy(alpha = 0.45f), (0.004f * e).coerceIn(0.25f, 0.8f)) } }
        }
        // Lo que asoma por los bordes: una fronda de helecho, una hoja ancha, un tallo con su flor seca.
        if (e > 25f) {
            val brote = listOf(V3(w * 1.02f, y0 + pr.grueso * 0.5f, -d * 0.3f), V3(-w * 0.4f, y0 + pr.grueso * 0.45f, -d * 1.02f), V3(w * 0.5f, y0 + pr.grueso * 0.55f, -d * 1.02f))
            for ((i, b) in brote.withIndex()) {
                val q = s.p(b); val dir = if (i == 0) Offset(1f, 0.3f) else Offset(az.entre(-0.4f, 0.4f), 1f)
                val largo = (0.07f + 0.05f * az.real()) * e
                val verde = if (i == 2) C.CUERO else lerp(C.HOJA, C.SELVA, 0.3f)
                val fin = q + dir * largo
                drawLine(p.medio(lerp(verde, C.BREA, 0.3f)), q, fin, (0.004f * e).coerceIn(0.3f, 1f))
                for (k in 1..6) { val r = q + (fin - q) * (k / 7f); val n = Offset(-dir.y, dir.x) * (largo * 0.18f * (1f - k / 8f)); drawLine(p.por(verde, 0.2f), r, r + n, (0.004f * e).coerceIn(0.3f, 0.9f)); drawLine(p.por(verde, -0.3f), r, r - n, (0.004f * e).coerceIn(0.3f, 0.9f)) }
                if (i == 2) drawCircle(p.luz(lerp(C.NARANJA, C.ARENA, 0.4f)), (0.012f * e).coerceIn(0.6f, 2f), fin)
            }
        }
        rejilla(y1 - tabla)
        // Las correas: ciñen el fajo de un lado a otro, por encima; la hebilla de latón arriba, con su punta suelta.
        for (u in listOf(-0.55f, 0.55f)) {
            val x = w * u; val g = (0.03f * e).coerceIn(0.6f, 4f)
            val camino = listOf(V3(x, y0, -d - 0.005f), V3(x, y1 + 0.004f, -d - 0.005f), V3(x, y1 + 0.006f, d + 0.005f))
            for (i in 0 until camino.size - 1) { drawLine(p.sombra(C.CUERO), s.p(camino[i]), s.p(camino[i + 1]), g * 1.1f); drawLine(p.por(C.CUERO, 0.3f), s.p(camino[i]), s.p(camino[i + 1]), g * 0.8f) }
            val h = s.p(V3(x, y1 + 0.008f, -d * 0.2f))
            drawRect(p.luz(C.LATON), h - Offset(g * 0.7f, g * 0.55f), Size(g * 1.4f, g * 1.1f), style = Stroke((g * 0.3f).coerceAtLeast(0.4f)))
            drawLine(p.luz(lerp(C.LATON, Color.White, 0.3f)).copy(alpha = 0.8f), h - Offset(g * 0.6f, g * 0.5f), h + Offset(-g * 0.1f, -g * 0.5f), (g * 0.2f).coerceAtLeast(0.3f))
            // La punta de la correa, suelta y un poco levantada.
            drawLine(p.por(C.CUERO, 0.1f), h + Offset(0f, g * 0.4f), s.p(V3(x + 0.02f, y1 + 0.02f, -d * 0.6f)), g * 0.75f, StrokeCap.Round)
        }
    }

    // =================================================================
    //  La caja de frascos con serrín
    // =================================================================

    /**
     * Una caja de frascos abierta, en [s]: de tablas, llena de serrín hasta cerca del borde, con los cuellos de los
     * frascos y sus corchos asomando, cada uno a su aire, y alguno torcido. [frascos], cuántos asoman.
     */
    class CajaDeFrascos(val s: Sitio, val semilla: Long, val ancho: Float = 0.46f, val alto: Float = 0.3f, val fondo: Float = 0.32f, val base: Float = 0f, val frascos: Int = 7)

    fun sombraDeCajaDeFrascos(c: CajaDeFrascos, sol: Sol3): Path = sombraDe(c.s, sol, esquinas(-c.ancho / 2f, c.ancho / 2f, c.base, c.base + c.alto, -c.fondo / 2f, c.fondo / 2f), c.base)

    fun DrawScope.cajaDeFrascos(c: CajaDeFrascos, p: Pincel, sol: Sol3) {
        val s = c.s; val az = Azar(c.semilla); val e = s.escala(0f, 0f)
        val w = c.ancho / 2f; val d = c.fondo / 2f; val y0 = c.base; val y1 = c.base + c.alto; val g = 0.018f
        val pino = lerp(C.MADERA, C.ARENA, 0.3f)
        // Por dentro, las dos paredes del fondo (se ven por encima del serrín).
        for (cara in carasDeCaja(-w + g, w - g, y0, y1, -d + g, d - g).filter { !it.arriba && it.n.y == 0f && !s.seVe(it.centro, it.n) }) {
            drawPath(forma(s, cara), p.por(lerp(pino, C.MADERA_OSC, 0.3f), -0.4f))
        }
        // El serrín, con su grano, y lo que asoma de él.
        val serrin = poligono(listOf(V3(-w + g, y1 - 0.04f, -d + g), V3(w - g, y1 - 0.04f, -d + g), V3(w - g, y1 - 0.04f, d - g), V3(-w + g, y1 - 0.04f, d - g)).map { s.p(it) })
        drawPath(serrin, p.arriba(lerp(C.ARENA, C.MADERA, 0.25f)))
        clipPath(serrin) { drawPoints((0 until (60 * e / 60f).toInt().coerceIn(20, 160)).map { s.p(az.entre(-w, w), y1 - 0.04f, az.entre(-d, d)) }, PointMode.Points, p.sombra(C.MADERA).copy(alpha = 0.5f), (0.006f * e).coerceIn(0.4f, 1.2f), StrokeCap.Round)
            drawPoints((0 until (60 * e / 60f).toInt().coerceIn(20, 160)).map { s.p(az.entre(-w, w), y1 - 0.04f, az.entre(-d, d)) }, PointMode.Points, p.luz(C.ARENA).copy(alpha = 0.6f), (0.005f * e).coerceIn(0.4f, 1f), StrokeCap.Round) }
        val sitios = (0 until c.frascos).map { i -> V3(-w * 0.7f + (i % 4) * w * 0.46f + az.entre(-0.02f, 0.02f), y1 - 0.04f, -d * 0.55f + (i / 4) * d * 0.9f + az.entre(-0.02f, 0.02f)) }.sortedByDescending { s.hondo(it) }
        for ((i, b) in sitios.withIndex()) {
            val r = (0.032f + 0.012f * az.real()); val sube = 0.05f + 0.04f * az.real(); val ladeo = az.entre(-0.03f, 0.03f)
            val pie = s.p(b); val boca = s.p(b.x + ladeo, b.y + sube, b.z); val rr = r * s.escala(b.x, b.z)
            // El cuello de vidrio: el líquido que se adivina, el brillo del lado de la luz y el corcho atado.
            val liquido = if (i % 3 == 0) lerp(C.ESCARABAJO, C.ARENA, 0.3f) else if (i % 3 == 1) lerp(C.NARANJA, C.ARENA, 0.45f) else lerp(C.LONA, C.ESCARABAJO, 0.25f)
            drawPath(poligono(pie + Offset(-rr, 0f), pie + Offset(rr, 0f), boca + Offset(rr * 0.8f, 0f), boca + Offset(-rr * 0.8f, 0f)), p.medio(liquido).copy(alpha = 0.75f))
            drawLine(p.luz(Color.White).copy(alpha = 0.55f), pie + Offset(if (p.solALaDerecha) rr * 0.5f else -rr * 0.5f, 0f), boca + Offset(if (p.solALaDerecha) rr * 0.4f else -rr * 0.4f, 0f), (rr * 0.25f).coerceAtLeast(0.3f))
            drawOval(p.arriba(lerp(C.ARENA, C.CUERO, 0.45f)), boca - Offset(rr * 0.85f, rr * 0.4f), Size(rr * 1.7f, rr * 0.8f))
            drawRect(p.medio(lerp(C.ARENA, C.CUERO, 0.5f)), boca - Offset(rr * 0.85f, 0f), Size(rr * 1.7f, rr * 0.6f))
            drawLine(p.sombra(C.CANAMO), boca + Offset(-rr, rr * 0.75f), boca + Offset(rr, rr * 0.75f), (rr * 0.18f).coerceAtLeast(0.3f))
        }
        // Las caras de fuera, de tablas.
        for (cara in visibles(s, carasDeCaja(-w, w, y0, y1, -d, d)).filter { !it.arriba }) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(pino, t))
            clipPath(f) {
                linea(s, cara, 0f, 0.5f, 1f, 0.5f, p.sombra(C.BREA).copy(alpha = 0.6f), (0.01f * e).coerceIn(0.3f, 1.4f))
                repeat(4) { val v = az.entre(0.08f, 0.92f); linea(s, cara, 0f, v, 1f, v + az.entre(-0.03f, 0.03f), p.sombra(pino).copy(alpha = 0.22f), (0.005f * e).coerceIn(0.25f, 0.7f)) }
                linea(s, cara, 0f, 0f, 1f, 0f, p.sombra(lerp(pino, C.BREA, 0.4f)).copy(alpha = 0.5f), (0.04f * e).coerceIn(0.5f, 4f))
                // El asa de cuerda en el costado.
                if (cara.n.x != 0f) drawPath(curva(listOf(s.p(cara.en(0.3f, 0.78f)), s.p(cara.en(0.5f, 0.62f)), s.p(cara.en(0.7f, 0.78f)))), p.sombra(C.CANAMO), style = Stroke((0.012f * e).coerceIn(0.4f, 2f), cap = StrokeCap.Round))
            }
            drawPath(f, p.sombra(lerp(pino, C.BREA, 0.5f)).copy(alpha = 0.5f), style = Stroke(0.4f))
        }
        // El canto de arriba de las paredes de delante, claro.
        for (cara in visibles(s, carasDeCaja(-w, w, y0, y1, -d, d)).filter { !it.arriba }) drawLine(p.luz(pino), s.p(cara.q[3]), s.p(cara.q[2]), (g * e).coerceIn(0.5f, 3f))
    }

    /**
     * Un frasco suelto, de pie, con el [pie] en el lienzo y [alto] unidades: de vidrio, con su corcho atado con bramante
     * y una etiqueta de papel (sin letras); dentro, en alcohol, lo que diga [contenido]: 0, un pez enroscado; 1, una
     * medusa; 2, una culebrilla; 3, unos escarabajos.
     */
    fun DrawScope.frasco(p: Pincel, pie: Offset, alto: Float, contenido: Int, lejos: Float = 0f) {
        val w = alto * 0.36f; val cuello = alto * 0.78f
        val cuerpo = Path().apply {
            moveTo(pie + Offset(-w, -alto * 0.04f)); lineTo(pie + Offset(-w, -alto * 0.66f)); cubicTo(pie + Offset(-w, -alto * 0.74f), pie + Offset(-w * 0.62f, -alto * 0.76f), pie + Offset(-w * 0.62f, -cuello))
            lineTo(pie + Offset(w * 0.62f, -cuello)); cubicTo(pie + Offset(w * 0.62f, -alto * 0.76f), pie + Offset(w, -alto * 0.74f), pie + Offset(w, -alto * 0.66f)); lineTo(pie + Offset(w, -alto * 0.04f))
            cubicTo(pie + Offset(w, alto * 0.01f), pie + Offset(-w, alto * 0.01f), pie + Offset(-w, -alto * 0.04f)); close()
        }
        val alcohol = lerp(C.ARENA, C.NARANJA, 0.25f)
        drawPath(cuerpo, p.medio(lerp(alcohol, C.ESCARABAJO, 0.2f), lejos).copy(alpha = 0.55f))
        clipPath(cuerpo) {
            val c = pie + Offset(0f, -alto * 0.36f); val k = alto / 30f
            val bicho = p.sombra(when (contenido % 4) { 0 -> C.ROCA; 1 -> C.LONA; 2 -> C.CUERO; else -> C.ESCARABAJO }, lejos)
            when (contenido % 4) {
                0 -> drawPath(Path().apply { moveTo(c + Offset(-6f * k, 2f * k)); cubicTo(c + Offset(-4f * k, -6f * k), c + Offset(6f * k, -5f * k), c + Offset(5f * k, 3f * k)); cubicTo(c + Offset(4f * k, 7f * k), c + Offset(-2f * k, 6f * k), c + Offset(-1f * k, 2f * k)) }, bicho, style = Stroke(2.2f * k, cap = StrokeCap.Round))
                1 -> { drawArc(bicho.copy(alpha = 0.7f), 180f, 180f, true, c - Offset(5f * k, 6f * k), Size(10f * k, 9f * k)); for (i in 0..4) drawLine(bicho.copy(alpha = 0.5f), c + Offset(-4f * k + i * 2f * k, -1f * k), c + Offset(-4f * k + i * 2f * k + sin(i * 1.3f) * 1.5f * k, 8f * k), 0.5f * k) }
                2 -> for (i in 0..2) drawOval(bicho, c + Offset(-5f * k, (-4f + i * 3.4f) * k), Size(10f * k, 3f * k), style = Stroke(1.4f * k))
                else -> for (i in 0..3) drawOval(bicho, c + Offset((-4f + (i % 2) * 4f) * k, (-3f + (i / 2) * 5f) * k), Size(3f * k, 4f * k))
            }
            // El menisco del alcohol y el brillo del vidrio.
            drawLine(p.luz(C.LONA, lejos).copy(alpha = 0.5f), pie + Offset(-w, -alto * 0.62f), pie + Offset(w, -alto * 0.62f), alto * 0.02f)
            val lado = if (p.solALaDerecha) 1f else -1f
            drawRect(p.luz(Color.White, lejos).copy(alpha = 0.55f), pie + Offset(lado * w * 0.62f - alto * 0.03f, -alto * 0.64f), Size(alto * 0.06f, alto * 0.5f))
            drawRect(p.sombra(C.ESCARABAJO, lejos).copy(alpha = 0.4f), pie + Offset(-lado * w * 0.92f - alto * 0.04f, -alto * 0.66f), Size(alto * 0.08f, alto * 0.62f))
        }
        drawPath(cuerpo, p.sombra(C.ESCARABAJO, lejos).copy(alpha = 0.5f), style = Stroke((alto * 0.025f).coerceAtLeast(0.3f)))
        // La etiqueta y el corcho atado.
        drawRect(p.medio(C.PAPEL, lejos), pie + Offset(-w * 0.62f, -alto * 0.32f), Size(w * 1.24f, alto * 0.16f))
        for (k in 0..2) drawLine(p.sombra(C.CUERO, lejos).copy(alpha = 0.5f), pie + Offset(-w * 0.45f, -alto * (0.28f - k * 0.04f)), pie + Offset(w * (0.1f + 0.15f * k), -alto * (0.28f - k * 0.04f)), (alto * 0.012f).coerceAtLeast(0.25f))
        drawRect(p.por(lerp(C.ARENA, C.CUERO, 0.45f), 0.2f, lejos), pie + Offset(-w * 0.55f, -alto * 0.96f), Size(w * 1.1f, alto * 0.2f))
        drawOval(p.luz(lerp(C.ARENA, C.CUERO, 0.35f), lejos), pie + Offset(-w * 0.55f, -alto * 1.0f), Size(w * 1.1f, alto * 0.08f))
        drawLine(p.sombra(C.CANAMO, lejos), pie + Offset(-w * 0.66f, -alto * 0.8f), pie + Offset(w * 0.66f, -alto * 0.8f), (alto * 0.03f).coerceAtLeast(0.3f))
        drawLine(p.sombra(C.CANAMO, lejos), pie + Offset(w * 0.6f, -alto * 0.8f), pie + Offset(w * 0.85f, -alto * 0.68f), (alto * 0.02f).coerceAtLeast(0.25f))
    }

    // =================================================================
    //  La mesa de trabajo
    // =================================================================

    /** De cubierta (un tablero sobre caballetes, con violines y trincado a las argollas) o de la galería (de patas torneadas, con su cajón). */
    enum class TipoMesa { CUBIERTA, GALERIA }

    /**
     * La mesa de trabajo en [s], de [ancho] × [fondo] metros y [alto] de alto. Con [tapada], el encerado por encima,
     * atado. Devuelve dónde queda el centro del tablero ([Mesa.tablero]), que es el soporte del objeto del día.
     */
    class Mesa(val s: Sitio, val semilla: Long, val tipo: TipoMesa, val ancho: Float = 1.25f, val fondo: Float = 0.72f, val alto: Float = 0.8f, val tapada: Boolean = false, val nieve: Float = 0f) {
        /** El centro del tablero, en el lienzo. */
        val tablero: Offset get() = s.p(0f, alto, 0f)

        /** Un punto del tablero en ([u], [v]), de −1 a 1 a lo ancho y a lo hondo. */
        fun sobre(u: Float, v: Float): Offset = s.p(u * ancho / 2f, alto, v * fondo / 2f)
    }

    fun sombraDeMesa(me: Mesa, sol: Sol3): Path {
        val w = me.ancho / 2f; val d = me.fondo / 2f
        return sombraDe(me.s, sol, listOf(V3(-w, me.alto, -d), V3(w, me.alto, -d), V3(w, me.alto, d), V3(-w, me.alto, d), V3(-w * 0.8f, 0f, -d * 0.6f), V3(w * 0.8f, 0f, -d * 0.6f), V3(w * 0.8f, 0f, d * 0.6f), V3(-w * 0.8f, 0f, d * 0.6f)))
    }

    fun DrawScope.mesa(me: Mesa, p: Pincel, sol: Sol3) {
        val s = me.s; val az = Azar(me.semilla); val e = s.escala(0f, 0f)
        val w = me.ancho / 2f; val d = me.fondo / 2f; val h = me.alto; val g = 0.045f
        val madera = if (me.tipo == TipoMesa.CUBIERTA) lerp(C.MADERA, C.TECA, 0.35f) else C.MADERA
        // Las patas: de atrás adelante.
        val patas = if (me.tipo == TipoMesa.CUBIERTA) {
            // Dos caballetes: cada uno, dos patas abiertas y un travesaño.
            listOf(-w * 0.78f, w * 0.78f).flatMap { x -> listOf(V3(x, 0f, -d * 0.82f) to V3(x, h - g, -d * 0.3f), V3(x, 0f, d * 0.82f) to V3(x, h - g, d * 0.3f)) }
        } else listOf(V3(-w + 0.05f, 0f, -d + 0.05f) to V3(-w + 0.05f, h - g, -d + 0.05f), V3(w - 0.05f, 0f, -d + 0.05f) to V3(w - 0.05f, h - g, -d + 0.05f), V3(w - 0.05f, 0f, d - 0.05f) to V3(w - 0.05f, h - g, d - 0.05f), V3(-w + 0.05f, 0f, d - 0.05f) to V3(-w + 0.05f, h - g, d - 0.05f))
        for ((a, b) in patas.sortedByDescending { s.hondo(it.first) }) {
            val pa = s.p(a); val pb = s.p(b); val k = s.escala(a.x, a.z) * 0.055f
            drawLine(p.sombra(madera), pa, pb, k * 1.05f, StrokeCap.Butt)
            drawLine(p.por(madera, sol.t(s.normal(V3(if (p.solALaDerecha) 1f else -1f, 0f, -0.5f)))), pa + Offset(if (p.solALaDerecha) k * 0.2f else -k * 0.2f, 0f), pb + Offset(if (p.solALaDerecha) k * 0.2f else -k * 0.2f, 0f), k * 0.55f)
            if (me.tipo == TipoMesa.GALERIA) for (t in listOf(0.18f, 0.24f, 0.8f)) drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.6f), pa + (pb - pa) * t - Offset(k * 0.6f, 0f), pa + (pb - pa) * t + Offset(k * 0.6f, 0f), 0.6f)
        }
        if (me.tipo == TipoMesa.CUBIERTA) {
            // Los travesaños de los caballetes y las trincas a las argollas de la cubierta.
            for (x in listOf(-w * 0.78f, w * 0.78f)) {
                drawLine(p.medio(lerp(madera, C.BREA, 0.2f)), s.p(x, h * 0.35f, -d * 0.62f), s.p(x, h * 0.35f, d * 0.62f), e * 0.035f)
                for (z in listOf(-d * 0.82f, d * 0.82f)) cabo(p, s.p(x, h * 0.35f, z * 0.75f), s.p(x + (if (x < 0f) -0.3f else 0.3f), 0f, z * 1.25f), 1f, (0.014f * e).coerceIn(0.4f, 2f))
            }
        } else {
            // El bastidor de la mesa de la galería, con su cajón y el tirador de latón.
            for (cara in visibles(s, carasDeCaja(-w + 0.03f, w - 0.03f, h - 0.13f, h - g, -d + 0.03f, d - 0.03f)).filter { !it.arriba && it.n.y == 0f }) {
                val f = forma(s, cara); val t = luzDe(s, sol, cara); drawPath(f, p.por(madera, t))
                if (cara.n.z < -0.5f) { drawPath(poligono(listOf(cara.en(0.32f, 0.15f), cara.en(0.68f, 0.15f), cara.en(0.68f, 0.85f), cara.en(0.32f, 0.85f)).map { s.p(it) }), p.sombra(madera).copy(alpha = 0.5f), style = Stroke(0.5f)); drawCircle(p.luz(C.LATON), (0.012f * e).coerceIn(0.5f, 2f), s.p(cara.en(0.5f, 0.5f))) }
            }
        }
        // El tablero: tres tablas con su canto, gastado; en la de cubierta, los violines alrededor para que nada ruede.
        val tablero = carasDeCaja(-w, w, h - g, h, -d, d)
        for (cara in visibles(s, tablero)) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(madera, t))
            clipPath(f) {
                if (cara.arriba) {
                    for (k in 1..2) { val v = k / 3f; linea(s, cara, 0f, v, 1f, v, p.sombra(C.BREA).copy(alpha = 0.55f), (0.008f * e).coerceIn(0.3f, 1.2f)) }
                    repeat(9) { val v = az.entre(0f, 1f); linea(s, cara, az.entre(0f, 0.4f), v, az.entre(0.6f, 1f), v + az.entre(-0.01f, 0.01f), p.sombra(madera).copy(alpha = 0.2f), (0.004f * e).coerceIn(0.25f, 0.6f)) }
                    // El uso: los cercos de los frascos, una mancha de tinta, la cera de la vela, los cortes del escalpelo.
                    repeat(3) { val q = s.p(cara.en(az.entre(0.15f, 0.85f), az.entre(0.2f, 0.8f))); val r = (0.04f * e).coerceIn(1f, 6f); drawOval(p.sombra(madera).copy(alpha = 0.35f), q - Offset(r, r * 0.4f), Size(r * 2f, r * 0.8f), style = Stroke((r * 0.18f).coerceAtLeast(0.3f))) }
                    drawPath(poligono(listOf(cara.en(0.7f, 0.3f), cara.en(0.8f, 0.28f), cara.en(0.82f, 0.4f), cara.en(0.72f, 0.45f)).map { s.p(it) }), p.sombra(C.BREA).copy(alpha = 0.35f))
                    repeat(6) { val u = az.entre(0.2f, 0.7f); val v = az.entre(0.3f, 0.8f); linea(s, cara, u, v, u + az.entre(0.03f, 0.08f), v + az.entre(-0.06f, 0.06f), p.luz(lerp(madera, C.LONA, 0.5f)).copy(alpha = 0.5f), (0.003f * e).coerceIn(0.25f, 0.6f)) }
                } else linea(s, cara, 0f, 1f, 1f, 1f, p.luz(lerp(madera, C.LONA, 0.4f)).copy(alpha = 0.6f), (0.008f * e).coerceIn(0.3f, 1.2f))
            }
        }
        if (me.tipo == TipoMesa.CUBIERTA && !me.tapada) {
            // Los violines: listones bajos alrededor del tablero.
            val alto = 0.035f
            for ((a, b) in listOf(V3(-w, h, -d) to V3(w, h, -d), V3(w, h, -d) to V3(w, h, d), V3(-w, h, d) to V3(w, h, d), V3(-w, h, -d) to V3(-w, h, d)).sortedByDescending { s.hondo(it.first + (it.second - it.first) * 0.5f) }) {
                val f = poligono(s.p(a), s.p(b), s.p(b + V3(0f, alto, 0f)), s.p(a + V3(0f, alto, 0f)))
                drawPath(f, p.por(lerp(madera, C.MADERA_OSC, 0.3f), 0.1f)); drawLine(p.luz(madera).copy(alpha = 0.7f), s.p(a + V3(0f, alto, 0f)), s.p(b + V3(0f, alto, 0f)), (0.006f * e).coerceIn(0.3f, 1f))
            }
        }
        if (me.nieve > 0.02f) drawPath(forma(s, tablero[4]), p.luz(C.NIEVE).copy(alpha = 0.85f * me.nieve))
        if (me.tapada) encerado(me, p, sol, az)
    }

    /** El encerado sobre la mesa: la lona embreada que cae por los lados en pliegues y se ata a las patas. */
    private fun DrawScope.encerado(me: Mesa, p: Pincel, sol: Sol3, az: Azar) {
        val s = me.s; val e = s.escala(0f, 0f); val w = me.ancho / 2f + 0.04f; val d = me.fondo / 2f + 0.04f; val h = me.alto + 0.02f
        val lona = lerp(C.LONA, C.ROCA, 0.35f)
        val caida = 0.32f
        for (cara in visibles(s, carasDeCaja(-w, w, h - caida, h, -d, d)).filter { it.n.y >= 0f }) {
            val pts = if (cara.arriba) cara.q else listOf(cara.q[0], cara.q[1], cara.q[2], cara.q[3]).mapIndexed { i, q -> if (i < 2) q + V3(0f, az.entre(-0.04f, 0.06f), 0f) else q }
            val f = poligono(pts.map { s.p(it) }); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(lona, t))
            clipPath(f) { for (k in 1..5) { val u = k / 6f + az.entre(-0.04f, 0.04f); if (cara.arriba) linea(s, cara, u, 0f, u + az.entre(-0.1f, 0.1f), 1f, p.sombra(lona).copy(alpha = 0.25f), (0.01f * e).coerceIn(0.3f, 1.4f)) else linea(s, cara, u, 0f, u, 1f, (if (k % 2 == 0) p.luz(lona) else p.sombra(lona)).copy(alpha = 0.45f), (0.02f * e).coerceIn(0.4f, 2.4f)) } }
        }
        for (x in listOf(-w * 0.6f, w * 0.6f)) cabo(p, s.p(x, h - caida, -d), s.p(x, h + 0.01f, -d), 0f, (0.012f * e).coerceIn(0.4f, 1.8f))
    }

    // =================================================================
    //  El bergantín lejano, fondeado
    // =================================================================

    /**
     * Un bergantín fondeado visto de lejos, de [largo] unidades de eslora, con la línea de flotación en [pie] (su centro).
     * [proa]: 1 si la proa mira a la derecha. Casco negro con la faja ocre de las portas, el forro de cobre que asoma,
     * dos palos con sus vergas cruzadas y las velas aferradas, el bauprés, la jarcia, el pico con su bandera, el cable
     * del ancla y un bote al costado. [farol], de 0 a 1: el farol de popa encendido. Con su reflejo en el agua.
     */
    fun DrawScope.bergantinLejano(p: Pincel, pie: Offset, largo: Float, proa: Float, lejos: Float, az: Azar, farol: Float = 0f, reflejo: Boolean = true) {
        val u = largo / 100f; val dir = if (proa >= 0f) 1f else -1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        val casco = p.medio(C.BREA, lejos); val faja = p.medio(lerp(C.ARENA, C.NARANJA, 0.2f), lejos); val cobre = p.medio(lerp(C.LATON, C.ESCARABAJO, 0.5f), lejos)
        // El reflejo, antes que el barco.
        if (reflejo) { reflejoRoto(pie.x - 46f * u, pie.x + 46f * u, pie.y + 0.6f, 7f * u + 2f, casco, 0.5f, az, max(0.5f, 0.9f * u)); for (x in listOf(-14f, 20f)) reflejoRoto(q(x, 0f).x - 0.6f, q(x, 0f).x + 0.6f, pie.y + 1f, 30f * u, p.medio(C.MADERA, lejos), 0.35f, az, max(0.5f, u)) }
        // El cable del ancla, de la amura al agua.
        drawLine(p.sombra(C.BREA, lejos), q(40f, -9f), q(56f, 1f), max(0.35f, 0.8f * u))
        // Los palos, con sus masteleros, y la jarcia firme.
        val palos = listOf(-12f, 22f)
        for ((i, x) in palos.withIndex()) {
            val alto = if (i == 0) 108f else 100f
            drawLine(p.sombra(C.MADERA_OSC, lejos), q(x, -10f), q(x + 1f, -alto), max(0.45f, 1.6f * u))
            drawLine(p.luz(C.MADERA, lejos).copy(alpha = 0.6f), q(x + 0.4f, -10f), q(x + 1.2f, -alto * 0.95f), max(0.3f, 0.5f * u))
            // La cofa y la cruceta.
            drawLine(p.sombra(C.BREA, lejos), q(x - 4f, -alto * 0.5f), q(x + 4f, -alto * 0.5f), max(0.4f, 1.2f * u)); drawLine(p.sombra(C.BREA, lejos), q(x - 2.4f, -alto * 0.8f), q(x + 2.6f, -alto * 0.8f), max(0.3f, 0.8f * u))
            // Las vergas cruzadas, con la vela aferrada encima (un rollo blanco).
            for ((y, ancho) in listOf(-alto * 0.47f to 26f, -alto * 0.72f to 19f, -alto * 0.9f to 12f)) {
                drawLine(p.sombra(C.MADERA_OSC, lejos), q(x - ancho / 2f, y), q(x + ancho / 2f, y + 0.6f), max(0.4f, 1.1f * u))
                drawLine(p.luz(C.LONA, lejos), q(x - ancho * 0.44f, y - 1.2f), q(x + ancho * 0.44f, y - 0.6f), max(0.5f, 2f * u), StrokeCap.Round)
            }
            // Los obenques, hacia la borda.
            for (k in -2..2) drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.6f), q(x + k * 2.6f, -9f), q(x, -alto * 0.5f), max(0.25f, 0.35f * u))
        }
        // El estay, del trinquete al bauprés, y del palo mayor al trinquete; el pico de la mayor, con su bandera.
        drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.7f), q(palos[1] + 1f, -100f), q(66f, -18f), max(0.25f, 0.45f * u))
        drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.7f), q(palos[0] + 1f, -104f), q(palos[1], -54f), max(0.25f, 0.4f * u))
        drawLine(p.sombra(C.MADERA_OSC, lejos), q(palos[0], -40f), q(-40f, -52f), max(0.35f, 0.9f * u))
        drawLine(p.sombra(C.MADERA_OSC, lejos), q(palos[0], -16f), q(-48f, -14f), max(0.35f, 0.9f * u))
        drawLine(p.luz(C.LONA, lejos), q(palos[0] - 3f, -18f), q(-44f, -17f), max(0.5f, 1.8f * u), StrokeCap.Round)
        drawPath(poligono(q(-40f, -52f), q(-49f, -50f), q(-49f, -43f), q(-40f, -45f)), p.medio(C.TEJA, lejos))
        drawRect(p.medio(C.LONA, lejos), Offset(min(q(-40f, -52f).x, q(-44f, -52f).x), q(-40f, -52f).y), Size(4f * u, 3.4f * u))
        // El bauprés.
        drawLine(p.sombra(C.MADERA_OSC, lejos), q(42f, -12f), q(70f, -20f), max(0.4f, 1.3f * u))
        // El casco: la obra muerta negra, la faja ocre con las portas, el arrufo y el forro de cobre en la flotación.
        val obra = Path().apply {
            moveTo(q(-46f, -13f)); cubicTo(q(-20f, -10.5f), q(20f, -10.5f), q(44f, -14.5f)); cubicTo(q(48f, -12f), q(50f, -6f), q(44f, 0f))
            lineTo(q(-42f, 0f)); cubicTo(q(-45f, -3f), q(-47f, -8f), q(-46f, -13f)); close()
        }
        drawPath(obra, casco)
        clipPath(obra) {
            drawPath(Path().apply { moveTo(q(-48f, -10.4f)); cubicTo(q(-20f, -8f), q(20f, -8f), q(48f, -12f)); lineTo(q(48f, -9.6f)); cubicTo(q(20f, -5.8f), q(-20f, -5.8f), q(-48f, -8.2f)); close() }, faja)
            for (k in 0..8) { val x = -34f + k * 8.4f; drawRect(casco, q(x, -8.8f + 0.0004f * x * x), Size(2f * u, 1.6f * u)) }
            drawRect(cobre, q(-50f, -1.6f), Size(100f * u, 1.8f * u))
            drawRect(p.luz(C.LONA, lejos).copy(alpha = 0.2f * p.r.dia), q(-50f, -13f), Size(100f * u, 1.2f * u))
        }
        // El bote al costado, con su remo.
        drawPath(poligono(q(-6f, 0.4f), q(8f, 0.4f), q(10f, -2.4f), q(-8f, -2.4f)), p.medio(lerp(C.LONA, C.ROCA, 0.3f), lejos))
        drawLine(p.medio(C.MADERA, lejos), q(-2f, -1f), q(-10f, 3f), max(0.3f, 0.5f * u))
        if (farol > 0.02f) {
            val c = q(-44f, -16f)
            resplandor(c, 9f * u + 4f, 0.7f * farol, C.LUMBRE)
            drawCircle(C.LUMBRE.copy(alpha = farol), max(0.6f, 1.2f * u), c)
            if (reflejo) for (k in 0..5) drawLine(C.LUMBRE.copy(alpha = 0.4f * farol * (1f - k / 6f)), Offset(c.x - 1.5f, pie.y + 2f + k * 2.2f * u), Offset(c.x + 1.5f, pie.y + 2f + k * 2.2f * u), 0.6f)
        }
    }

    // =================================================================
    //  La rueda del timón y la bitácora
    // =================================================================

    /**
     * La rueda doble del timón en [s]: dos ruedas de diez cabillas en el mismo eje de proa a popa, con el tambor entre
     * ellas, donde se enrollan los guardines, y los dos candeleros que lo sostienen. [radio] y la altura del [eje] en
     * metros; la rueda de popa (la cercana) va en z = −[separacion]/2. [luz], de 0 a 1: lo que la alumbra la lámpara de la
     * bitácora de noche. [giro] (radianes): cuánto está metida la rueda (las cabillas no quedan siempre igual).
     */
    class Timon(val s: Sitio, val semilla: Long, val radio: Float = 0.78f, val eje: Float = 1.12f, val separacion: Float = 1.05f, val giro: Float = 0.12f, val nieve: Float = 0f)

    fun sombraDeTimon(t: Timon, sol: Sol3): Path {
        val r = t.radio * 1.2f; val zs = t.separacion / 2f + 0.06f
        return sombraDe(t.s, sol, listOf(V3(-r, t.eje + r, -zs), V3(r, t.eje + r, -zs), V3(r, t.eje - r, -zs), V3(-r, t.eje - r, -zs), V3(-r, t.eje + r, zs), V3(r, t.eje + r, zs), V3(-0.2f, 0f, -zs), V3(0.2f, 0f, zs)))
    }

    fun DrawScope.timon(t: Timon, p: Pincel, sol: Sol3) {
        val s = t.s; val e = s.escala(0f, 0f); val zs = t.separacion / 2f
        val caoba = C.CAOBA
        // La rueda de proa (la lejana), el tambor y los candeleros, y la rueda de popa (la cercana): de atrás adelante.
        rueda(t, p, sol, zs + 0.05f)
        // Los candeleros: dos postes en A, con el cojinete arriba.
        for (z in listOf(zs - 0.12f, -zs + 0.12f)) {
            for (x in listOf(-0.32f, 0.32f)) {
                val a = s.p(x, 0f, z); val b = s.p(x * 0.25f, t.eje - 0.06f, z); val g = 0.07f * s.escala(x, z)
                drawLine(p.sombra(caoba), a, b, g * 1.1f); drawLine(p.por(caoba, if ((x > 0f) == p.solALaDerecha) 0.5f else -0.3f), a, b, g * 0.5f)
            }
            drawLine(p.medio(lerp(caoba, C.BREA, 0.3f)), s.p(-0.36f, 0.08f, z), s.p(0.36f, 0.08f, z), 0.06f * s.escala(0f, z))
        }
        // El tambor: un cilindro de proa a popa, con las vueltas de los guardines.
        val rt = 0.16f
        val arriba = listOf(s.p(0f, t.eje + rt, -zs + 0.08f), s.p(0f, t.eje + rt, zs - 0.08f)); val abajo = listOf(s.p(0f, t.eje - rt, -zs + 0.08f), s.p(0f, t.eje - rt, zs - 0.08f))
        val lado = if (p.solALaDerecha) 1f else -1f
        val tambor = poligono(arriba[0] + Offset(lado * 0f, 0f), arriba[1], abajo[1], abajo[0])
        drawPath(tambor, Brush.verticalGradient(listOf(p.luz(C.MADERA), p.medio(C.MADERA), p.sombra(C.MADERA_OSC)), arriba[0].y, abajo[0].y))
        clipPath(tambor) {
            val g = (0.03f * e).coerceIn(0.5f, 3f)
            for (k in 0..14) { val z = -zs + 0.12f + k * (t.separacion - 0.24f) / 14f; drawLine(p.sombra(C.CANAMO), s.p(-0.05f, t.eje + rt, z), s.p(0.05f, t.eje - rt, z + 0.05f), g); drawLine(p.luz(C.CANAMO).copy(alpha = 0.6f), s.p(-0.05f, t.eje + rt, z - 0.01f), s.p(0f, t.eje, z + 0.015f), g * 0.4f) }
        }
        // Los guardines que bajan del tambor a la cubierta.
        for (x in listOf(-0.08f, 0.08f)) cabo(p, s.p(x, t.eje - rt * 0.8f, 0.1f), s.p(x * 2f, 0f, 0.15f), 0f, (0.022f * e).coerceIn(0.5f, 2.5f))
        rueda(t, p, sol, -zs - 0.05f)
    }

    /** Una de las dos ruedas, en z = [z]: la llanta de cuatro piezas, las cabillas con sus puños torneados, el cubo de latón. */
    private fun DrawScope.rueda(t: Timon, p: Pincel, sol: Sol3, z: Float) {
        val s = t.s; val e = s.escala(0f, z); val r = t.radio; val ri = r * 0.82f; val hondo = 0.07f
        val caoba = C.CAOBA
        val cerca = z < 0f
        fun en(rr: Float, a: Float, dz: Float = 0f) = s.p(rr * cos(a), t.eje + rr * sin(a), z + dz)
        val n = 48
        // La llanta por fuera: el canto, que se ve donde mira hacia los ojos.
        for (i in 0 until n) {
            val a0 = (2 * PI * i / n).toFloat(); val a1 = (2 * PI * (i + 1) / n).toFloat(); val am = (a0 + a1) / 2f
            val nrm = V3(cos(am), sin(am), 0f)
            if (!s.seVe(V3(r * cos(am), t.eje + r * sin(am), z), nrm)) continue
            drawPath(poligono(en(r, a0), en(r, a1), en(r, a1, hondo), en(r, a0, hondo)), p.por(caoba, sol.t(s.normal(nrm)) - 0.15f))
        }
        // Las cabillas, detrás de la cara de la llanta: del cubo hasta más allá de la llanta, con su puño.
        for (k in 0 until 10) {
            val a = t.giro + (2 * PI * k / 10).toFloat()
            val a0 = en(0.1f, a); val a1 = en(ri, a); val b = en(r * 1.32f, a)
            val g = 0.05f * e
            drawLine(p.sombra(caoba), a0, a1, g * 1.1f, StrokeCap.Round)
            drawLine(p.por(caoba, if ((cos(a) > 0f) == p.solALaDerecha) 0.5f else 0f), a0, a1, g * 0.5f, StrokeCap.Round)
            // El puño: torneado, con sus anillos, más grueso en medio.
            val m0 = en(r * 1.02f, a)
            for (j in 0..5) { val q = m0 + (b - m0) * (j / 5f); val gg = g * (0.9f + 0.55f * sin(PI.toFloat() * j / 5f)) * (if (j == 1 || j == 4) 0.75f else 1f); drawCircle(p.sombra(caoba), gg * 0.62f, q); drawCircle(p.por(caoba, 0.6f), gg * 0.34f, q + Offset(-gg * 0.12f, -gg * 0.16f)) }
            drawLine(p.medio(caoba), en(r, a), m0, g * 0.9f)
        }
        // La cara de la llanta, con las juntas de sus cuatro piezas y los flejes de latón.
        val anillo = Path().apply {
            val fuera = (0..n).map { en(r, (2 * PI * it / n).toFloat()) }; val dentro = (0..n).map { en(ri, (2 * PI * it / n).toFloat()) }
            moveTo(fuera[0]); for (q in fuera.drop(1)) lineTo(q); close(); moveTo(dentro[0]); for (q in dentro.reversed().drop(1)) lineTo(q); close()
            fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        }
        val centro = s.p(0f, t.eje, z); val rad = (en(r, 0f) - centro).getDistance()
        drawPath(anillo, Brush.linearGradient(listOf(p.luz(caoba), p.medio(caoba), p.sombra(caoba)), centro + Offset(if (p.solALaDerecha) rad else -rad, -rad), centro + Offset(if (p.solALaDerecha) -rad else rad, rad)))
        clipPath(anillo) {
            for (k in 0 until 4) { val a = t.giro + 0.31f + (PI.toFloat() / 2f) * k; drawLine(p.sombra(C.BREA).copy(alpha = 0.7f), en(ri, a), en(r, a), (0.012f * e).coerceIn(0.4f, 2f)) }
            for (k in 0 until 10) { val a = t.giro + (2 * PI * k / 10).toFloat(); drawLine(p.luz(C.LATON), en(ri, a - 0.05f), en(ri, a + 0.05f), (0.02f * e).coerceIn(0.4f, 2.4f)); drawLine(p.luz(C.LATON), en(r, a - 0.04f), en(r, a + 0.04f), (0.02f * e).coerceIn(0.4f, 2.4f)) }
            // El brillo del barniz por el lado de la luz.
            drawArc(p.luz(lerp(caoba, C.LONA, 0.5f)).copy(alpha = 0.45f), if (p.solALaDerecha) -80f else 200f, 60f, false, centro - Offset(rad * 0.92f, rad * 0.92f), Size(rad * 1.84f, rad * 1.84f), style = Stroke((0.025f * e).coerceIn(0.4f, 3f)))
            if (t.nieve > 0.02f) drawArc(p.luz(C.NIEVE).copy(alpha = t.nieve), 200f, 140f, false, centro - Offset(rad * 0.94f, rad * 0.94f), Size(rad * 1.88f, rad * 1.88f), style = Stroke((0.04f * e).coerceIn(0.6f, 4f)))
        }
        drawPath(anillo, p.sombra(C.BREA).copy(alpha = 0.4f), style = Stroke(0.5f))
        // El cubo, de latón, con su tapa.
        val rc = 0.13f * e
        drawCircle(Brush.radialGradient(listOf(p.luz(lerp(C.LATON, Color.White, 0.3f)), p.medio(C.LATON), p.sombra(C.LATON)), centro + Offset(if (p.solALaDerecha) rc * 0.3f else -rc * 0.3f, -rc * 0.3f), rc * 1.2f), rc, centro)
        drawCircle(p.sombra(lerp(C.LATON, C.BREA, 0.4f)), rc * 0.45f, centro, style = Stroke((rc * 0.12f).coerceAtLeast(0.4f)))
        if (cerca) drawCircle(p.luz(Color.White).copy(alpha = 0.5f), rc * 0.18f, centro + Offset(if (p.solALaDerecha) rc * 0.4f else -rc * 0.4f, -rc * 0.4f))
    }

    /**
     * La bitácora en [s]: un mueble de caoba de cuarterones, con la cúpula de latón encima, su ventanilla de vidrio por
     * donde se ve la rosa, y la chimenea de la lámpara. [lampara], de 0 a 1: la lámpara encendida (de noche alumbra la
     * ventanilla y el latón, y echa luz alrededor).
     */
    class Bitacora(val s: Sitio, val semilla: Long, val ancho: Float = 0.5f, val alto: Float = 1.0f, val lampara: Float = 0f, val nieve: Float = 0f) {
        /** Dónde queda la luz de la lámpara, en el lienzo. */
        val luz: Offset get() = s.p(0f, alto + 0.12f, -ancho * 0.3f)
    }

    fun sombraDeBitacora(b: Bitacora, sol: Sol3): Path { val w = b.ancho / 2f; return sombraDe(b.s, sol, esquinas(-w, w, 0f, b.alto + 0.36f, -w, w)) }

    fun DrawScope.bitacora(b: Bitacora, p: Pincel, sol: Sol3) {
        val s = b.s; val e = s.escala(0f, 0f); val w = b.ancho / 2f; val h = b.alto
        val caoba = C.CAOBA
        // El mueble: un zócalo, el cuerpo de cuarterones y una cornisa.
        for ((y0, y1, k) in listOf(Triple(0f, 0.08f, 1.08f), Triple(0.08f, h - 0.06f, 1f), Triple(h - 0.06f, h, 1.1f))) {
            for (cara in visibles(s, carasDeCaja(-w * k, w * k, y0, y1, -w * k, w * k))) {
                val f = forma(s, cara); val t = luzDe(s, sol, cara)
                drawPath(f, p.por(if (k > 1f) lerp(caoba, C.BREA, 0.25f) else caoba, t))
                if (k == 1f && !cara.arriba) clipPath(f) {
                    // Los cuarterones: dos paneles con su moldura, de luz arriba y sombra abajo.
                    for ((v0, v1) in listOf(0.08f to 0.44f, 0.54f to 0.92f)) {
                        val panel = poligono(listOf(cara.en(0.14f, v0), cara.en(0.86f, v0), cara.en(0.86f, v1), cara.en(0.14f, v1)).map { s.p(it) })
                        drawPath(panel, p.por(caoba, t - 0.1f)); drawPath(panel, p.sombra(C.BREA).copy(alpha = 0.45f), style = Stroke((0.01f * e).coerceIn(0.3f, 1.4f)))
                        linea(s, cara, 0.14f, v1, 0.86f, v1, p.luz(lerp(caoba, C.LONA, 0.4f)).copy(alpha = 0.5f), (0.008f * e).coerceIn(0.3f, 1.2f))
                    }
                    // El barniz, gastado donde se agarra el timonel.
                    mancha(s.p(cara.en(0.5f, 0.86f)), 0.08f * e, 0.03f * e, p.luz(lerp(caoba, C.ARENA, 0.4f)), 0.3f)
                }
                if (k > 1f && cara.arriba && b.nieve > 0.02f) drawPath(f, p.luz(C.NIEVE).copy(alpha = b.nieve))
            }
        }
        // La cúpula de latón: media esfera, con su luz del lado del sol y el reflejo del cielo.
        val c = s.p(0f, h, 0f); val rr = w * 0.86f * e; val top = s.p(0f, h + w * 0.8f, 0f)
        val cupula = Path().apply { moveTo(c + Offset(-rr, 0f)); cubicTo(c + Offset(-rr, -rr * 0.7f - (c.y - top.y) * 0.3f), c + Offset(-rr * 0.55f, -(c.y - top.y)), Offset(c.x, top.y)); cubicTo(c + Offset(rr * 0.55f, -(c.y - top.y)), c + Offset(rr, -rr * 0.7f - (c.y - top.y) * 0.3f), c + Offset(rr, 0f)); close() }
        val lado = if (p.solALaDerecha) 1f else -1f
        drawPath(cupula, Brush.radialGradient(listOf(p.luz(lerp(C.LATON, Color.White, 0.35f)), p.medio(C.LATON), p.sombra(lerp(C.LATON, C.BREA, 0.3f))), c + Offset(lado * rr * 0.35f, -(c.y - top.y) * 0.6f), rr * 1.5f))
        drawLine(p.luz(Color.White).copy(alpha = 0.55f), c + Offset(lado * rr * 0.5f, -(c.y - top.y) * 0.65f), c + Offset(lado * rr * 0.2f, -(c.y - top.y) * 0.9f), (rr * 0.08f).coerceAtLeast(0.4f), StrokeCap.Round)
        // La ventanilla: la rosa de los vientos se adivina; de noche, alumbrada.
        val vent = Rect(c.x - rr * 0.42f, c.y - (c.y - top.y) * 0.62f, c.x + rr * 0.42f, c.y - (c.y - top.y) * 0.14f)
        drawOval(p.sombra(C.BREA), vent.topLeft, vent.size)
        val vidrio = Rect(vent.center.x - vent.width * 0.38f, vent.center.y - vent.height * 0.38f, vent.center.x + vent.width * 0.38f, vent.center.y + vent.height * 0.38f)
        drawOval(lerp(p.medio(lerp(C.LONA, C.ESCARABAJO, 0.4f)), C.LUMBRE, 0.85f * b.lampara), vidrio.topLeft, vidrio.size)
        drawLine(p.sombra(C.BREA).copy(alpha = 0.6f), Offset(vent.left + vent.width * 0.2f, vent.center.y), Offset(vent.right - vent.width * 0.2f, vent.center.y), 0.4f)
        drawLine(p.sombra(C.BREA).copy(alpha = 0.6f), Offset(vent.center.x, vent.top + vent.height * 0.2f), Offset(vent.center.x, vent.bottom - vent.height * 0.2f), 0.4f)
        drawOval(p.luz(lerp(C.LATON, C.BREA, 0.2f)), vent.topLeft, vent.size, style = Stroke((rr * 0.07f).coerceAtLeast(0.4f)))
        // La chimenea de la lámpara, con su sombrerete.
        val ch = Offset(c.x + rr * 0.1f, top.y)
        drawRect(Brush.horizontalGradient(listOf(p.luz(C.LATON), p.sombra(C.LATON)), ch.x - rr * 0.12f, ch.x + rr * 0.12f), Offset(ch.x - rr * 0.12f, ch.y - rr * 0.38f), Size(rr * 0.24f, rr * 0.4f))
        drawOval(p.sombra(lerp(C.LATON, C.BREA, 0.3f)), Offset(ch.x - rr * 0.22f, ch.y - rr * 0.48f), Size(rr * 0.44f, rr * 0.14f))
        if (b.lampara > 0.02f) {
            resplandor(Offset(vent.center.x, vent.center.y), rr * 3f, 0.55f * b.lampara, C.LUMBRE)
            drawOval(C.LUMBRE.copy(alpha = 0.7f * b.lampara), Offset(vent.center.x - vent.width * 0.2f, vent.center.y - vent.height * 0.2f), Size(vent.width * 0.4f, vent.height * 0.4f))
        }
    }

    // =================================================================
    //  La claraboya
    // =================================================================

    /**
     * La claraboya de la cabina de popa en [s]: la brazola de madera, el tejadillo de vidrio a dos aguas (la cumbrera de
     * proa a popa), los barrotes de latón que protegen los vidrios. [luz], de 0 a 1: la cabina encendida de noche, que
     * sale por los vidrios.
     */
    class Claraboya(val s: Sitio, val semilla: Long, val ancho: Float = 1.3f, val fondo: Float = 1.0f, val brazola: Float = 0.42f, val cumbre: Float = 0.78f, val luz: Float = 0f, val nieve: Float = 0f) {
        /** El centro del tejadillo (de donde sale la luz de la cabina). */
        val centro: Offset get() = s.p(0f, (brazola + cumbre) / 2f, 0f)
    }

    fun sombraDeClaraboya(c: Claraboya, sol: Sol3): Path { val w = c.ancho / 2f; val d = c.fondo / 2f; return sombraDe(c.s, sol, esquinas(-w, w, 0f, c.brazola, -d, d) + listOf(V3(0f, c.cumbre, -d), V3(0f, c.cumbre, d))) }

    fun DrawScope.claraboya(c: Claraboya, p: Pincel, sol: Sol3) {
        val s = c.s; val e = s.escala(0f, 0f); val w = c.ancho / 2f; val d = c.fondo / 2f; val h = c.brazola
        val madera = lerp(C.TECA, C.MADERA, 0.5f)
        // La brazola.
        for (cara in visibles(s, carasDeCaja(-w, w, 0f, h, -d, d)).filter { !it.arriba }) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(madera, t))
            clipPath(f) {
                linea(s, cara, 0f, 0.92f, 1f, 0.92f, p.luz(lerp(madera, C.LONA, 0.4f)).copy(alpha = 0.7f), (0.02f * e).coerceIn(0.4f, 2.4f))
                linea(s, cara, 0f, 0.5f, 1f, 0.5f, p.sombra(C.BREA).copy(alpha = 0.4f), (0.008f * e).coerceIn(0.3f, 1f))
                linea(s, cara, 0f, 0.02f, 1f, 0.02f, p.sombra(C.BREA).copy(alpha = 0.6f), (0.03f * e).coerceIn(0.5f, 3f))
            }
        }
        // El tejadillo: dos faldones de vidrio y los dos hastiales de madera.
        val faldones = listOf(
            Cara3(listOf(V3(-w, h, -d), V3(0f, c.cumbre, -d), V3(0f, c.cumbre, d), V3(-w, h, d)), V3(-(c.cumbre - h), w, 0f).let { val l = sqrt(it.x * it.x + it.y * it.y); V3(it.x / l, it.y / l, 0f) }),
            Cara3(listOf(V3(0f, c.cumbre, -d), V3(w, h, -d), V3(w, h, d), V3(0f, c.cumbre, d)), V3(c.cumbre - h, w, 0f).let { val l = sqrt(it.x * it.x + it.y * it.y); V3(it.x / l, it.y / l, 0f) }),
        )
        val hastial = Cara3(listOf(V3(-w, h, -d), V3(w, h, -d), V3(0f, c.cumbre, -d), V3(0f, c.cumbre, -d)), V3(0f, 0f, -1f))
        for (f0 in faldones.sortedByDescending { s.hondo(it.centro) }) {
            if (!s.seVe(f0.centro, f0.n)) continue
            val f = forma(s, f0); val t = luzDe(s, sol, f0)
            // El vidrio: oscuro, con el reflejo del cielo; de noche, la luz de la cabina.
            val vidrio = lerp(lerp(p.l.medio, p.sombra(C.ESCARABAJO), 0.45f), C.LUMBRE, 0.75f * c.luz * p.l.noche)
            drawPath(f, Brush.linearGradient(listOf(lerp(vidrio, p.l.alto, 0.25f * (1f - p.l.noche)), vidrio, lerp(vidrio, p.l.bajo, 0.3f)), s.p(f0.en(0f, 1f)), s.p(f0.en(1f, 0f))))
            clipPath(f) {
                // Los parteluces (tres vidrios por faldón) y los barrotes de latón, cruzados.
                for (k in 1..2) linea(s, f0, 0f, k / 3f, 1f, k / 3f, p.por(madera, t), (0.04f * e).coerceIn(0.6f, 4f))
                for (k in 1..5) linea(s, f0, k / 6f, 0f, k / 6f, 1f, p.luz(C.LATON).copy(alpha = 0.9f), (0.012f * e).coerceIn(0.35f, 1.6f))
                linea(s, f0, 0.1f, 0.2f, 0.5f, 0.9f, p.luz(Color.White).copy(alpha = 0.18f * (1f - p.l.noche)), (0.05f * e).coerceIn(0.5f, 5f))
                if (c.nieve > 0.02f) drawPath(f, p.luz(C.NIEVE).copy(alpha = 0.8f * c.nieve))
            }
            drawPath(f, p.por(madera, t).copy(alpha = 0.9f), style = Stroke((0.03f * e).coerceIn(0.5f, 3f)))
        }
        if (s.seVe(hastial.centro, hastial.n)) { val f = poligono(listOf(s.p(hastial.q[0]), s.p(hastial.q[1]), s.p(hastial.q[2]))); drawPath(f, p.por(madera, luzDe(s, sol, hastial))); drawPath(f, p.sombra(C.BREA).copy(alpha = 0.4f), style = Stroke(0.5f)) }
        // La cumbrera, de latón.
        drawLine(p.luz(C.LATON), s.p(0f, c.cumbre + 0.01f, -d), s.p(0f, c.cumbre + 0.01f, d), (0.03f * e).coerceIn(0.5f, 3f), StrokeCap.Round)
        if (c.luz > 0.02f && p.l.noche > 0.1f) resplandor(c.centro, 0.9f * e, 0.5f * c.luz * p.l.noche, C.LUMBRE)
    }

    // =================================================================
    //  El palo, su cabillero y sus obenques
    // =================================================================

    /**
     * Un palo en [s] (el pie en la cubierta), de [radio] metros, hasta [alto] metros: el tronco de pino con su veta, la
     * grasa de los raca, los zunchos de hierro, la funda embreada al pie y, a su alrededor, el cabillero: un barandal
     * sobre cuatro candeleros con sus cabillas, de donde cuelgan los cabos adujados que suben a la jarcia.
     */
    class Palo(val s: Sitio, val semilla: Long, val radio: Float = 0.28f, val alto: Float = 16f, val cabillero: Boolean = true, val nieve: Float = 0f)

    fun sombraDePalo(pa: Palo, sol: Sol3, hasta: Float = 6f): Path = sombraDe(pa.s, sol, listOf(V3(-pa.radio, 0f, 0f), V3(pa.radio, 0f, 0f), V3(-pa.radio, hasta, 0f), V3(pa.radio, hasta, 0f)))

    fun DrawScope.palo(pa: Palo, p: Pincel, sol: Sol3) {
        val s = pa.s; val az = Azar(pa.semilla); val e = s.escala(0f, 0f); val r = pa.radio
        val lado = if (p.solALaDerecha) 1f else -1f
        // El cabillero de detrás del palo (lo que queda al fondo).
        if (pa.cabillero) cabillero(pa, p, sol, az, detras = true)
        // El tronco: de la cubierta hacia arriba, cada vez más fino.
        val pie = s.p(0f, 0f, 0f); val cima = s.p(0f, pa.alto, 0f)
        val g0 = r * 2f * e; val g1 = g0 * 0.75f
        val tronco = poligono(pie + Offset(-g0 / 2f, 0f), pie + Offset(g0 / 2f, 0f), cima + Offset(g1 / 2f, 0f), cima + Offset(-g1 / 2f, 0f))
        drawPath(tronco, Brush.horizontalGradient(0f to p.sombra(C.MADERA), (if (lado > 0f) 0.35f else 0.15f) to p.medio(C.MADERA), (if (lado > 0f) 0.75f else 0.4f) to p.luz(lerp(C.MADERA, C.ARENA, 0.3f)), 1f to p.medio(C.MADERA_OSC), startX = pie.x + (if (lado > 0f) -g0 / 2f else g0 / 2f), endX = pie.x + (if (lado > 0f) g0 / 2f else -g0 / 2f)))
        clipPath(tronco) {
            repeat(10) { val x = az.entre(-0.4f, 0.4f); drawLine(p.sombra(C.MADERA).copy(alpha = 0.3f), s.p(x * r * 2f, 0f, 0f), s.p(x * r * 1.5f, pa.alto, 0f), 0.4f) }
            // La grasa de los racamentos, más oscura a media altura, y las manchas de brea que chorrean.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.MADERA_OSC).copy(alpha = 0.45f), Color.Transparent), s.p(0f, 3.4f, 0f).y, s.p(0f, 1.6f, 0f).y), Offset(pie.x - g0, s.p(0f, 3.4f, 0f).y), Size(g0 * 2f, s.p(0f, 1.6f, 0f).y - s.p(0f, 3.4f, 0f).y))
            repeat(4) { val x = az.entre(-0.3f, 0.3f); val y = az.entre(2f, 7f); drawLine(p.sombra(C.BREA).copy(alpha = 0.3f), s.p(x * r * 2f, y, 0f), s.p(x * r * 2f, y - az.entre(0.4f, 1.2f), 0f), (0.02f * e).coerceIn(0.3f, 2f)) }
            // Los zunchos.
            for (y in listOf(0.9f, 2.1f, 4.4f, 6.8f, 9.2f)) { val a = s.p(0f, y, 0f); drawRect(p.sombra(C.HIERRO), Offset(a.x - g0, a.y - 0.04f * e), Size(g0 * 2f, (0.08f * e).coerceAtLeast(0.6f))); drawRect(p.luz(C.HIERRO).copy(alpha = 0.5f), Offset(a.x - g0, a.y - 0.04f * e), Size(g0 * 2f, (0.025f * e).coerceAtLeast(0.3f))) }
            if (pa.nieve > 0.02f) drawRect(p.luz(C.NIEVE).copy(alpha = 0.6f * pa.nieve), Offset(pie.x - lado * g0 / 2f - g0 * 0.15f, cima.y), Size(g0 * 0.3f, pie.y - cima.y))
        }
        // La funda embreada del pie, un cono de lona.
        val fa = s.p(0f, 0.32f, 0f)
        drawPath(poligono(pie + Offset(-g0 * 0.85f, 0f), pie + Offset(g0 * 0.85f, 0f), fa + Offset(g0 * 0.55f, 0f), fa + Offset(-g0 * 0.55f, 0f)), Brush.horizontalGradient(listOf(p.sombra(lerp(C.LONA, C.BREA, 0.6f)), p.luz(lerp(C.LONA, C.BREA, 0.5f))), pie.x - lado * g0, pie.x + lado * g0))
        // El cabillero de delante.
        if (pa.cabillero) cabillero(pa, p, sol, az, detras = false)
    }

    /** El cabillero alrededor del palo: el barandal sobre sus candeleros, las cabillas, las adujas que cuelgan y los cabos que suben. */
    private fun DrawScope.cabillero(pa: Palo, p: Pincel, sol: Sol3, az: Azar, detras: Boolean) {
        val s = pa.s; val e = s.escala(0f, 0f); val w = 0.75f; val d0 = -0.55f; val d1 = 0.55f; val h = 0.95f
        val madera = lerp(C.MADERA, C.TECA, 0.3f)
        // Los cuatro tramos del barandal, con lo suyo, cada uno en su turno según su profundidad.
        val tramos = listOf(V3(-w, h, d0) to V3(w, h, d0), V3(w, h, d0) to V3(w, h, d1), V3(w, h, d1) to V3(-w, h, d1), V3(-w, h, d1) to V3(-w, h, d0))
        for ((a, b) in tramos) {
            val mitad = a + (b - a) * 0.5f
            val esDetras = s.hondo(mitad) > s.hondo(V3(0f, 0f, 0f))
            if (esDetras != detras) continue
            // Los candeleros de las esquinas.
            for (q in listOf(a, b)) { val pq = s.p(q.x, 0f, q.z); val pt = s.p(q); val g = 0.07f * s.escala(q.x, q.z); drawLine(p.sombra(madera), pq, pt, g); drawLine(p.por(madera, 0.4f), pq + Offset(g * 0.2f, 0f), pt + Offset(g * 0.2f, 0f), g * 0.4f) }
            // El barandal: un tablón con su canto de luz.
            val g = 0.09f * s.escala(mitad.x, mitad.z)
            drawLine(p.medio(madera), s.p(a), s.p(b), g * 0.9f); drawLine(p.luz(lerp(madera, C.LONA, 0.3f)), s.p(a) + Offset(0f, -g * 0.35f), s.p(b) + Offset(0f, -g * 0.35f), g * 0.3f)
            // Las cabillas, y de algunas una aduja colgada y un cabo que sube a la jarcia.
            for (k in 1..4) {
                val t = k / 5f; val q = a + (b - a) * t; val pq = s.p(q); val ee = s.escala(q.x, q.z)
                drawLine(p.sombra(C.MADERA_OSC), pq + Offset(0f, 0.12f * ee), pq + Offset(0f, -0.1f * ee), (0.03f * ee).coerceAtLeast(0.4f))
                drawCircle(p.luz(madera), (0.022f * ee).coerceAtLeast(0.35f), pq + Offset(0f, -0.1f * ee))
                if ((k + tramos.indexOf(a to b)) % 2 == 0) {
                    // La aduja: unas vueltas de cabo colgadas, que caen más abajo del barandal.
                    for (j in 0..2) { val ancho = (0.09f + 0.02f * j) * ee; val largo = (0.42f + 0.05f * j + az.entre(-0.04f, 0.04f)) * ee; drawOval(p.por(C.CANAMO, if (j == 1) 0.3f else -0.2f), Offset(pq.x - ancho, pq.y - 0.02f * ee), Size(ancho * 2f, largo), style = Stroke((0.018f * ee).coerceIn(0.3f, 2f))) }
                    // Y el cabo que sube, tirante, hacia lo alto.
                    drawLine(p.sombra(C.CANAMO).copy(alpha = 0.85f), pq, s.p(q.x * 0.2f + az.entre(-0.3f, 0.3f), 14f, q.z * 0.4f), (0.016f * ee).coerceIn(0.3f, 1.5f))
                }
            }
        }
    }

    /**
     * Los obenques de una banda del palo [pa]: del costado ([costado] metros a su derecha, en los ejes del palo; negativo,
     * a babor) hasta la cabeza del palo, a [alto] metros. Cada uno con su par de vigotas y su acollador a la altura de la
     * mesa de guarnición ([mesa]), y los flechastes que hacen de escala. [cuantos] obenques, repartidos de [z0] a [z1].
     */
    fun DrawScope.obenques(pa: Palo, p: Pincel, costado: Float, mesa: Float, alto: Float, z0: Float, z1: Float, cuantos: Int = 5, nieve: Float = 0f, lejos: Float = 0f) {
        val s = pa.s; val tope = V3(0f, alto, 0f)
        val pies = (0 until cuantos).map { i -> V3(costado, mesa, z0 + (z1 - z0) * i / (cuantos - 1f)) }
        // Los flechastes, de obenque a obenque, cada 0.4 m (más ralos hacia arriba: no se ven).
        var y = mesa + 0.7f
        while (y < alto * 0.92f) {
            val k = (y - mesa) / (alto - mesa)
            val pts = pies.map { b -> s.p(b + (tope - b) * k) }
            for (i in 0 until pts.size - 1) drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.7f), pts[i], pts[i + 1] + Offset(0f, 0.5f), (0.012f * s.escala(pies[i].x, pies[i].z)).coerceIn(0.25f, 1.2f))
            if (nieve > 0.02f) for (i in 0 until pts.size - 1) drawLine(p.luz(C.NIEVE, lejos).copy(alpha = 0.7f * nieve), pts[i] + Offset(0f, -0.5f), pts[i + 1] + Offset(0f, -0.1f), (0.01f * s.escala(pies[i].x, pies[i].z)).coerceIn(0.25f, 1f))
            y += 0.4f
        }
        for (b in pies) {
            val e = s.escala(b.x, b.z); val g = (0.03f * e).coerceIn(0.35f, 3f)
            // Las vigotas (la de abajo en la mesa, la de arriba en el obenque) y el acollador entre ellas.
            val v0 = s.p(b); val v1 = s.p(b + (tope - b) * (0.55f / (alto - mesa)))
            for (v in listOf(v0, v1)) { drawCircle(p.sombra(C.BREA, lejos), 0.07f * e, v); drawCircle(p.luz(C.MADERA_OSC, lejos).copy(alpha = 0.6f), 0.035f * e, v + Offset(-0.015f * e, -0.02f * e)) }
            for (k in -1..1) drawLine(p.medio(C.CANAMO, lejos), v0 + Offset(k * 0.03f * e, 0f), v1 + Offset(k * 0.03f * e, 0f), (0.008f * e).coerceIn(0.25f, 0.9f))
            // El obenque, embreado, con el filo de luz.
            drawLine(p.sombra(C.BREA, lejos), v1, s.p(tope), g)
            if (g > 0.8f) drawLine(p.luz(lerp(C.BREA, C.LONA, 0.3f), lejos).copy(alpha = 0.5f), v1 + Offset(-g * 0.2f, 0f), s.p(tope) + Offset(-g * 0.1f, 0f), g * 0.3f)
            if (nieve > 0.02f) drawLine(p.luz(C.NIEVE, lejos).copy(alpha = 0.5f * nieve), v1 + Offset(-g * 0.3f, 0f), s.p(tope), g * 0.5f)
            // La cadena de la mesa, que baja por fuera del costado.
            drawLine(p.sombra(C.HIERRO, lejos), v0, s.p(b.x + 0.05f, b.y - 0.6f, b.z), (0.025f * e).coerceIn(0.3f, 2.4f))
        }
    }

    // =================================================================
    //  El bote sobre sus calzos
    // =================================================================

    /**
     * Un bote derecho sobre sus calzos, en [s] (el centro del bote en la cubierta), de [largo] metros, con la popa hacia
     * quien mira (z negativo). De tingladillo, pintado de [pintura] con la regala de madera, sus bancadas y los remos
     * dentro; con [funda], una lona atada por encima de media eslora. Trincado a la cubierta.
     */
    class Bote(val s: Sitio, val semilla: Long, val largo: Float = 5.2f, val manga: Float = 1.5f, val puntal: Float = 0.62f, val calzos: Float = 0.45f, val pintura: Color = ColorBotanico.LONA, val funda: Boolean = true, val nieve: Float = 0f)

    /** La media manga del bote en [t] (−1 popa, 1 proa) y la altura de su regala. */
    private fun mangaEn(b: Bote, t: Float): Float = b.manga / 2f * (1f - (if (t > 0f) t.pow(2f) else (-t).pow(3.2f) * 0.55f)).coerceAtLeast(0.02f).let { sqrt(it) }
    private fun regalaEn(b: Bote, t: Float): Float = b.calzos + b.puntal + 0.14f * t * t + (if (t > 0f) 0.08f * t else 0f)
    private fun quillaEn(b: Bote, t: Float): Float = b.calzos + (if (t > 0.6f) (t - 0.6f) * 0.9f else 0f)

    fun sombraDeBote(b: Bote, sol: Sol3): Path {
        val pts = ArrayList<V3>(); for (i in 0..8) { val t = -1f + 2f * i / 8f; val z = t * b.largo / 2f; val m = mangaEn(b, t); pts += V3(-m, regalaEn(b, t), z); pts += V3(m, regalaEn(b, t), z) }
        return sombraDe(b.s, sol, pts)
    }

    fun DrawScope.bote(b: Bote, p: Pincel, sol: Sol3) {
        val s = b.s; val az = Azar(b.semilla); val e = s.escala(0f, 0f); val n = 14
        val ts = (0..n).map { -1f + 2f * it / n }
        fun borde(t: Float, lado: Float, y: Float? = null) = s.p(lado * mangaEn(b, t), y ?: regalaEn(b, t), t * b.largo / 2f)
        fun pantoque(t: Float, lado: Float) = s.p(lado * mangaEn(b, t) * 0.55f, quillaEn(b, t) + 0.05f, t * b.largo / 2f)
        // Los calzos: dos tacos con su hueco, a un cuarto de cada punta.
        for (t in listOf(0.45f, -0.45f)) {
            val z = t * b.largo / 2f; val m = mangaEn(b, t) * 0.75f
            for (cara in visibles(s, carasDeCaja(-m, m, 0f, b.calzos + 0.06f, z - 0.08f, z + 0.08f))) drawPath(forma(s, cara), p.por(C.MADERA_OSC, luzDe(s, sol, cara)))
        }
        // El interior (se ve por encima de la regala de la banda cercana): la tablazón por dentro, las bancadas y los remos.
        val dentro = poligono(ts.map { borde(it, -1f) } + ts.reversed().map { borde(it, 1f) })
        drawPath(dentro, p.sombra(lerp(b.pintura, C.MADERA, 0.5f)))
        clipPath(dentro) {
            for (k in 1..4) { val t = -0.7f + k * 0.32f; drawLine(p.por(C.MADERA, 0.3f), borde(t, -1f, regalaEn(b, t) - 0.12f), borde(t, 1f, regalaEn(b, t) - 0.12f), (0.08f * s.escala(0f, t * b.largo / 2f)).coerceAtLeast(0.8f)) }
            for (k in 0..2) { val x = -0.35f + 0.3f * k; drawLine(p.luz(lerp(C.MADERA, C.ARENA, 0.3f)), s.p(x, b.calzos + b.puntal - 0.08f, -b.largo * 0.38f), s.p(x + 0.05f, b.calzos + b.puntal - 0.04f, b.largo * 0.4f), (0.045f * e).coerceAtLeast(0.6f), StrokeCap.Round) }
        }
        // La funda de lona, atada, sobre la mitad de proa.
        if (b.funda) {
            val t0 = -0.15f
            val lona = poligono((0..8).map { val t = t0 + (1f - t0) * it / 8f; borde(t, -1.04f, regalaEn(b, t) + 0.04f) } + (0..8).reversed().map { val t = t0 + (1f - t0) * it / 8f; borde(t, 1.04f, regalaEn(b, t) + 0.04f) } )
            drawPath(lona, p.por(lerp(C.LONA, C.ROCA, 0.25f), 0.35f))
            clipPath(lona) { for (k in 0..4) { val t = t0 + (1f - t0) * (k + 0.5f) / 5f; drawLine(p.sombra(C.LONA).copy(alpha = 0.5f), borde(t, -1f), borde(t, 1f), (0.02f * e).coerceIn(0.4f, 2f)) } }
            for (k in 0..3) { val t = t0 + (1f - t0) * (k + 0.3f) / 4f; drawLine(p.sombra(C.CANAMO), borde(t, -1.05f, regalaEn(b, t) + 0.03f), borde(t + 0.06f, 1.05f, regalaEn(b, t) + 0.03f), (0.016f * e).coerceIn(0.35f, 1.6f)) }
            if (b.nieve > 0.02f) drawPath(lona, p.luz(C.NIEVE).copy(alpha = 0.85f * b.nieve))
        }
        // Los costados: el de la banda que se ve (la que da a quien mira), de tingladillo, con su luz.
        for (lado in listOf(-1f, 1f)) {
            val fuera = Cara3(listOf(V3(lado, 0f, 0f), V3(lado, 0f, 0f), V3(lado, 0f, 0f), V3(lado, 0f, 0f)), V3(lado, -0.3f, 0f))
            val muestra = V3(lado * mangaEn(b, 0f), regalaEn(b, 0f) - 0.2f, 0f)
            if (!s.seVe(muestra, V3(lado, -0.2f, 0f))) continue
            val t = sol.t(s.normal(V3(lado, -0.2f, 0f)))
            val costado = poligono(ts.map { borde(it, lado) } + ts.reversed().map { pantoque(it, lado) })
            drawPath(costado, p.por(b.pintura, t))
            clipPath(costado) {
                // Las tracas del tingladillo: cada una con su canto de luz y su sombra.
                for (k in 1..5) { val v = k / 6f
                    val linea = ts.map { tt -> val a = borde(tt, lado); val c = pantoque(tt, lado); a + (c - a) * v }
                    drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(b.pintura).copy(alpha = 0.6f), style = Stroke((0.012f * e).coerceIn(0.3f, 1.4f)))
                    drawPath(Path().apply { moveTo(linea[0] + Offset(0f, -0.6f)); for (q in linea.drop(1)) lineTo(q + Offset(0f, -0.6f)) }, p.luz(b.pintura).copy(alpha = 0.4f), style = Stroke((0.006f * e).coerceIn(0.25f, 0.8f)))
                }
                // La faja de color bajo la regala y el desconchado del roce.
                drawPath(Path().apply { val a = ts.map { borde(it, lado) }; val c = ts.map { borde(it, lado, regalaEn(b, it) - 0.1f) }; moveTo(a[0]); for (q in a.drop(1)) lineTo(q); for (q in c.reversed()) lineTo(q); close() }, p.por(C.ESCARABAJO, t))
                repeat(6) { val tt = az.entre(-0.8f, 0.8f); val c = borde(tt, lado, regalaEn(b, tt) - az.entre(0.15f, 0.45f)); mancha(c, (0.05f * e).coerceAtLeast(1f), (0.02f * e).coerceAtLeast(0.6f), p.sombra(C.MADERA), 0.4f) }
            }
            drawPath(costado, p.sombra(C.BREA).copy(alpha = 0.4f), style = Stroke(0.5f))
        }
        // La regala, de madera, con su luz; el espejo de popa (lo que más cerca queda).
        for (lado in listOf(-1f, 1f)) drawPath(Path().apply { val a = ts.map { borde(it, lado) }; moveTo(a[0]); for (q in a.drop(1)) lineTo(q) }, p.luz(C.MADERA), style = Stroke((0.04f * e).coerceIn(0.5f, 3f), cap = StrokeCap.Round))
        val m0 = mangaEn(b, -1f)
        val espejo = poligono(s.p(-m0, regalaEn(b, -1f), -b.largo / 2f), s.p(m0, regalaEn(b, -1f), -b.largo / 2f), s.p(m0 * 0.4f, quillaEn(b, -1f) + 0.1f, -b.largo / 2f), s.p(-m0 * 0.4f, quillaEn(b, -1f) + 0.1f, -b.largo / 2f))
        drawPath(espejo, p.por(lerp(b.pintura, C.MADERA, 0.25f), sol.t(s.normal(V3(0f, 0f, -1f)))))
        drawPath(espejo, p.sombra(C.BREA).copy(alpha = 0.5f), style = Stroke(0.6f))
        // La trinca: dos cabos de la regala a las argollas de la cubierta.
        for (t in listOf(-0.6f, 0.55f)) for (lado in listOf(-1f, 1f)) cabo(p, borde(t, lado), s.p(lado * (mangaEn(b, t) + 0.35f), 0f, t * b.largo / 2f), 0f, (0.018f * e).coerceIn(0.35f, 1.6f))
    }

    // =================================================================
    //  El cañón
    // =================================================================

    /**
     * Un cañón en su cureña, en [s], con la boca hacia +x (hacia la porta de la amurada) y trincado a son de mar: de
     * hierro negro con sus refuerzos, el tapabocas puesto, la cureña pintada de rojo con sus cuatro ruedas, el braguero
     * que lo ata a la amurada y los palanquines adujados.
     */
    class Canon(val s: Sitio, val semilla: Long, val largo: Float = 1.75f, val nieve: Float = 0f)

    fun sombraDeCanon(c: Canon, sol: Sol3): Path = sombraDe(c.s, sol, esquinas(-0.7f, c.largo - 0.6f, 0f, 0.72f, -0.3f, 0.3f))

    fun DrawScope.canon(c: Canon, p: Pincel, sol: Sol3) {
        val s = c.s; val e = s.escala(0f, 0f); val y = 0.56f
        val curena = C.TEJA
        // Las ruedas de la banda de allá, las gualderas, el eje y las ruedas de la de acá.
        fun ruedas(z: Float) { for (x in listOf(-0.42f, 0.32f)) { val q = s.p(x, 0.13f, z); val r = 0.13f * s.escala(x, z); drawCircle(p.sombra(lerp(curena, C.BREA, 0.4f)), r, q); drawCircle(p.por(curena, 0.2f), r * 0.75f, q + Offset(-r * 0.08f, -r * 0.08f)); drawCircle(p.sombra(C.HIERRO), r * 0.22f, q) } }
        ruedas(0.26f)
        for (z in listOf(0.2f, -0.2f)) {
            // La gualdera: el perfil escalonado.
            val perfil = listOf(V3(-0.55f, 0.1f, z), V3(0.45f, 0.1f, z), V3(0.45f, 0.5f, z), V3(0.15f, 0.5f, z), V3(0.1f, 0.42f, z), V3(-0.15f, 0.42f, z), V3(-0.2f, 0.34f, z), V3(-0.55f, 0.3f, z))
            val f = poligono(perfil.map { s.p(it) })
            drawPath(f, p.por(curena, sol.t(s.normal(V3(0f, 0f, if (z < 0f) -1f else 1f))) + (if (z < 0f) 0f else -0.3f)))
            drawPath(f, p.sombra(C.BREA).copy(alpha = 0.45f), style = Stroke(0.5f))
            if (z < 0f) clipPath(f) { for (k in 1..2) drawLine(p.sombra(curena).copy(alpha = 0.4f), s.p(-0.55f, 0.1f + k * 0.12f, z), s.p(0.45f, 0.1f + k * 0.12f, z), 0.4f); for (x in listOf(-0.4f, 0.3f)) drawCircle(p.sombra(C.HIERRO), (0.018f * e).coerceAtLeast(0.4f), s.p(x, 0.3f, z)) }
        }
        // El tubo: de la culata (con su cascabel) a la boca, con sus refuerzos y el tapabocas.
        val x0 = -0.6f; val x1 = c.largo - 0.6f
        fun radio(x: Float) = 0.17f - 0.055f * ((x - x0) / (x1 - x0))
        val arriba = (0..12).map { val x = x0 + (x1 - x0) * it / 12f; s.p(x, y + radio(x), 0f) }; val abajo = (0..12).map { val x = x0 + (x1 - x0) * it / 12f; s.p(x, y - radio(x), 0f) }
        val tubo = poligono(arriba + abajo.reversed())
        drawPath(tubo, Brush.verticalGradient(listOf(p.luz(lerp(C.HIERRO, C.LONA, 0.15f)), p.medio(C.HIERRO), p.sombra(C.BREA)), arriba[0].y, abajo[0].y))
        clipPath(tubo) {
            for (x in listOf(-0.35f, 0.05f, 0.3f, x1 - 0.1f)) { drawLine(p.sombra(C.BREA), s.p(x, y + 0.2f, 0f), s.p(x, y - 0.2f, 0f), (0.035f * e).coerceAtLeast(0.6f)); drawLine(p.luz(C.HIERRO).copy(alpha = 0.6f), s.p(x - 0.02f, y + 0.2f, 0f), s.p(x - 0.02f, y, 0f), (0.012f * e).coerceAtLeast(0.3f)) }
            // El óxido del salitre, en manchas.
            val az = Azar(c.semilla); repeat(5) { mancha(s.p(az.entre(x0, x1), y + az.entre(-0.1f, 0.1f), 0f), (0.06f * e).coerceAtLeast(1f), (0.025f * e).coerceAtLeast(0.6f), p.medio(C.CUERO), 0.25f) }
            if (c.nieve > 0.02f) drawRect(p.luz(C.NIEVE).copy(alpha = c.nieve), Offset(arriba[0].x, arriba.minOf { it.y } - 2f), Size(arriba.last().x - arriba[0].x, (0.05f * e).coerceAtLeast(1f) + 2f))
        }
        // El cascabel y el tapabocas.
        val cas = s.p(x0 - 0.1f, y, 0f); drawCircle(p.medio(C.HIERRO), 0.06f * e, cas); drawCircle(p.luz(C.HIERRO).copy(alpha = 0.6f), 0.025f * e, cas + Offset(-0.01f * e, -0.02f * e))
        val boca = s.p(x1, y, 0f); val rb = radio(x1) * s.escala(x1, 0f)
        drawOval(p.por(lerp(C.MADERA, C.TEJA, 0.3f), 0.3f), Offset(boca.x - rb * 0.35f, boca.y - rb), Size(rb * 0.7f, rb * 2f))
        // El braguero: un cabo grueso del cascabel a las argollas de la amurada, por los dos lados.
        for (z in listOf(-0.35f, 0.35f)) cabo(p, cas, s.p(x1 + 0.1f, 0.45f, z * 1.4f), 2f, (0.035f * e).coerceIn(0.6f, 4f))
        // Los palanquines, adujados en la cubierta junto a la cureña.
        val q = s.p(-0.3f, 0.01f, -0.5f); val ee = s.escala(-0.3f, -0.5f)
        for (k in 0..3) drawOval(p.por(C.CANAMO, if (k % 2 == 0) 0.3f else -0.1f), Offset(q.x - (0.2f - 0.035f * k) * ee, q.y - (0.06f - 0.01f * k) * ee), Size((0.4f - 0.07f * k) * ee, (0.12f - 0.02f * k) * ee), style = Stroke((0.025f * ee).coerceIn(0.4f, 2.5f)))
        ruedas(-0.26f)
    }

    // =================================================================
    //  La cuba, el farol y la red a secar
    // =================================================================

    /**
     * Una cuba de duelas en [s], de [radio] y [alto] metros: las duelas, que se ven curvarse con su lado de luz, los
     * aros de hierro con su óxido, la tapa con el cazo encima y el verdín de abajo. Va trincada a la amurada.
     */
    class Cuba(val s: Sitio, val semilla: Long, val radio: Float = 0.3f, val alto: Float = 0.82f, val nieve: Float = 0f)

    fun sombraDeCuba(c: Cuba, sol: Sol3): Path = sombraDe(c.s, sol, (0 until 12).flatMap { i -> val a = (2 * PI * i / 12).toFloat(); listOf(V3(c.radio * cos(a), 0f, c.radio * sin(a)), V3(c.radio * cos(a), c.alto, c.radio * sin(a))) })

    fun DrawScope.cuba(c: Cuba, p: Pincel, sol: Sol3) {
        val s = c.s; val e = s.escala(0f, 0f); val r = c.radio; val h = c.alto; val az = Azar(c.semilla)
        fun panza(y: Float) = r * (1f + 0.1f * sin(PI.toFloat() * y / h))
        // El costado: las duelas que miran hacia los ojos (con la perspectiva, no siempre la mitad de delante), cada
        // una con su luz, curvadas con la panza.
        val n = 24
        val ys = (0..6).map { it / 6f }
        val vistas = (0 until n).filter { i -> val a = (2 * PI * (i + 0.5f) / n).toFloat(); s.seVe(V3(r * cos(a), h * 0.5f, r * sin(a)), V3(cos(a), 0f, sin(a))) }
        for (i in vistas.sortedByDescending { val a = (2 * PI * (it + 0.5f) / n).toFloat(); s.hondo(V3(r * cos(a), 0f, r * sin(a))) }) {
            val a0 = (2 * PI * i / n).toFloat(); val a1 = (2 * PI * (i + 1) / n).toFloat()
            val nrm = V3(cos((a0 + a1) / 2f), 0f, sin((a0 + a1) / 2f))
            val pts = ys.map { y -> s.p(panza(y * h) * cos(a0), y * h, panza(y * h) * sin(a0)) } + ys.reversed().map { y -> s.p(panza(y * h) * cos(a1), y * h, panza(y * h) * sin(a1)) }
            val duela = lerp(C.MADERA, if (i % 3 == 0) C.ARENA else C.MADERA_OSC, az.entre(0.05f, 0.2f))
            drawPath(poligono(pts), p.por(duela, sol.t(s.normal(nrm))))
            drawPath(Path().apply { moveTo(pts[0]); for (q in pts.subList(1, ys.size)) lineTo(q) }, p.sombra(C.BREA).copy(alpha = 0.35f), style = Stroke((0.006f * e).coerceIn(0.3f, 0.8f)))
        }
        // Los aros de hierro (solo por donde se ven), con su óxido, y el verdín de abajo.
        // Las duelas que se ven, seguidas (la tira puede pasar por el ángulo 0).
        val orden = vistas.sorted().let { v -> if (v.isEmpty()) v else { var inicio = 0; for (k in v.indices) { val antes = v[(k - 1 + v.size) % v.size]; if ((v[k] - antes + n) % n != 1) { inicio = k; break } }; v.drop(inicio) + v.take(inicio) } }
        for (y in listOf(0.1f, 0.3f, 0.7f, 0.9f)) {
            if (orden.isEmpty()) continue
            val pts = (orden + listOf(orden.last() + 1)).map { j -> val a = (2 * PI * j / n).toFloat(); s.p(panza(y * h) * 1.015f * cos(a), y * h, panza(y * h) * 1.015f * sin(a)) }
            drawPath(Path().apply { moveTo(pts[0]); for (q in pts.drop(1)) lineTo(q) }, p.sombra(C.HIERRO), style = Stroke((0.03f * e).coerceIn(0.5f, 3.6f)))
            val mitad = pts.size / 2
            drawPath(Path().apply { moveTo(pts[maxOf(0, mitad - 3)]); for (q in pts.subList(maxOf(0, mitad - 2), minOf(pts.size, mitad + 2))) lineTo(q + Offset(0f, -0.4f)) }, p.luz(C.HIERRO).copy(alpha = 0.55f), style = Stroke((0.01f * e).coerceIn(0.3f, 1.2f)))
            repeat(3) { val q = pts[az.entero(pts.size)]; drawLine(p.medio(C.CUERO).copy(alpha = 0.4f), q, q + Offset(0f, (0.06f * e).coerceAtLeast(1f)), (0.01f * e).coerceIn(0.3f, 1.2f)) }
        }
        mancha(s.p(0f, 0.06f, -r), 0.3f * e, 0.05f * e, p.sombra(lerp(C.MADERA, C.SELVA, 0.5f)), 0.4f)
        // La tapa, de tablas, y el cazo de latón encima.
        val tapa = (0..20).map { j -> val a = (2 * PI * j / 20).toFloat(); s.p(r * 0.98f * cos(a), h, r * 0.98f * sin(a)) }
        drawPath(poligono(tapa), p.arriba(lerp(C.MADERA, C.TECA, 0.4f)))
        for (k in -1..1) drawLine(p.sombra(C.MADERA).copy(alpha = 0.6f), s.p(k * r * 0.33f, h, -r), s.p(k * r * 0.33f, h, r), 0.5f)
        if (c.nieve > 0.02f) drawPath(poligono(tapa), p.luz(C.NIEVE).copy(alpha = c.nieve))
        val cz = s.p(r * 0.15f, h + 0.02f, 0.05f); val rc = 0.06f * e
        drawOval(p.sombra(C.MADERA), Offset(cz.x - rc, cz.y - rc * 0.2f), Size(rc * 2.2f, rc * 0.6f))
        drawOval(Brush.horizontalGradient(listOf(p.sombra(C.LATON), p.luz(C.LATON)), cz.x - rc, cz.x + rc), Offset(cz.x - rc, cz.y - rc * 0.5f), Size(rc * 2f, rc * 0.9f))
        drawOval(p.sombra(lerp(C.LATON, C.BREA, 0.4f)), Offset(cz.x - rc * 0.75f, cz.y - rc * 0.42f), Size(rc * 1.5f, rc * 0.5f))
        drawLine(p.por(C.MADERA, 0.3f), cz + Offset(rc * 0.8f, -rc * 0.1f), s.p(r * 0.95f, h + 0.06f, -r * 0.55f), (0.018f * e).coerceAtLeast(0.5f), StrokeCap.Round)
    }

    /**
     * Un farol de barco en [c] (su centro), de [alto] unidades: el armazón de latón, tres vidrios que se ven (el del
     * frente entero, los de los lados de canto), el sombrerete con su argolla y la base. [encendido], de 0 a 1: de día los
     * vidrios son oscuros con el reflejo del cielo; de noche, la llama dentro y el halo.
     */
    fun DrawScope.farol(p: Pincel, c: Offset, alto: Float, encendido: Float, lejos: Float = 0f) {
        val w = alto * 0.36f; val h = alto * 0.62f; val lado = if (p.solALaDerecha) 1f else -1f
        val arriba = c.y - h * 0.5f; val abajo = c.y + h * 0.5f
        val vidrioDia = lerp(p.l.medio, p.sombra(C.ESCARABAJO, lejos), 0.55f)
        val vidrio = if (encendido > 0.02f) lerp(vidrioDia, C.LUMBRE, encendido) else vidrioDia
        // Los vidrios: el del frente y los dos de canto, con sus montantes.
        drawRect(lerp(vidrio, Color.Black, 0.12f), Offset(c.x - w, arriba), Size(w * 0.45f, h))
        drawRect(lerp(vidrio, Color.Black, 0.12f), Offset(c.x + w * 0.55f, arriba), Size(w * 0.45f, h))
        drawRect(vidrio, Offset(c.x - w * 0.55f, arriba), Size(w * 1.1f, h))
        if (encendido > 0.02f) { drawOval(Color(0xFFFFE6A0).copy(alpha = encendido), Offset(c.x - w * 0.15f, c.y - h * 0.05f), Size(w * 0.3f, h * 0.36f)); resplandor(c, alto * 2.4f, 0.55f * encendido, C.LUMBRE) }
        else drawLine(p.luz(Color.White, lejos).copy(alpha = 0.45f), Offset(c.x + lado * w * 0.3f, arriba + h * 0.1f), Offset(c.x + lado * w * 0.1f, abajo - h * 0.15f), max(0.4f, w * 0.12f))
        for (x in listOf(-w, -w * 0.55f, w * 0.55f, w)) drawLine(p.por(C.LATON, if (x * lado > 0f) 0.6f else -0.3f, lejos), Offset(c.x + x, arriba), Offset(c.x + x, abajo), max(0.4f, w * 0.12f))
        drawLine(p.por(C.LATON, 0f, lejos), Offset(c.x - w, c.y + h * 0.08f), Offset(c.x + w, c.y + h * 0.08f), max(0.3f, w * 0.06f))
        // La base y el sombrerete cónico con su chimenea y la argolla.
        drawRect(Brush.horizontalGradient(listOf(p.sombra(C.LATON, lejos), p.luz(C.LATON, lejos)), c.x - lado * w * 1.1f, c.x + lado * w * 1.1f), Offset(c.x - w * 1.1f, abajo), Size(w * 2.2f, alto * 0.08f))
        drawPath(poligono(Offset(c.x - w * 1.15f, arriba), Offset(c.x + w * 1.15f, arriba), Offset(c.x + w * 0.3f, arriba - alto * 0.2f), Offset(c.x - w * 0.3f, arriba - alto * 0.2f)), Brush.horizontalGradient(listOf(p.sombra(C.LATON, lejos), p.luz(C.LATON, lejos)), c.x - lado * w, c.x + lado * w))
        drawRect(p.por(C.LATON, 0.2f, lejos), Offset(c.x - w * 0.22f, arriba - alto * 0.3f), Size(w * 0.44f, alto * 0.1f))
        drawCircle(p.sombra(C.LATON, lejos), alto * 0.07f, Offset(c.x, arriba - alto * 0.36f), style = Stroke(max(0.4f, alto * 0.025f)))
    }

    /**
     * La red de arrastre tendida a secar sobre la regala: de [a] a [b] (los dos puntos de la regala, ya en el lienzo),
     * cuelga [caida] unidades por dentro de la amurada, en pliegues, con su malla fina y el aro.
     */
    fun DrawScope.redASecar(p: Pincel, a: Offset, b: Offset, caida: Float, az: Azar, lejos: Float = 0f) {
        val tela = lerp(C.LONA, C.ARENA, 0.3f)
        val n = 9
        val borde = (0..n).map { a + (b - a) * (it / n.toFloat()) }
        val bajo = borde.mapIndexed { i, q -> q + Offset(az.entre(-1f, 1f), caida * (0.7f + 0.3f * sin(i * 1.7f)) * (if (i == 0 || i == n) 0.4f else 1f)) }
        val forma = poligono(borde + bajo.reversed())
        drawPath(forma, p.medio(tela, lejos).copy(alpha = 0.5f))
        clipPath(forma) {
            // La malla: dos familias de hilos que se cruzan, y los pliegues más oscuros.
            val l = (b - a).getDistance(); val paso = max(1.2f, caida * 0.12f)
            var t = -caida; while (t < l + caida) { drawLine(p.sombra(tela, lejos).copy(alpha = 0.45f), a + (b - a) * (t / l), a + (b - a) * ((t + caida) / l) + Offset(0f, caida * 1.2f), 0.35f); drawLine(p.sombra(tela, lejos).copy(alpha = 0.45f), a + (b - a) * (t / l), a + (b - a) * ((t - caida) / l) + Offset(0f, caida * 1.2f), 0.35f); t += paso }
            for (i in 1 until n) drawLine(p.sombra(tela, lejos).copy(alpha = 0.3f), borde[i], bajo[i], max(0.6f, caida * 0.08f))
        }
        drawPath(Path().apply { moveTo(bajo[0]); for (q in bajo.drop(1)) lineTo(q) }, p.medio(tela, lejos).copy(alpha = 0.8f), style = Stroke(0.6f))
        // El aro de hierro, colgado de un extremo.
        val c = bajo[1] + Offset(0f, caida * 0.1f); drawOval(p.sombra(C.HIERRO, lejos), Offset(c.x - caida * 0.25f, c.y - caida * 0.4f), Size(caida * 0.5f, caida * 0.8f), style = Stroke(max(0.5f, caida * 0.06f)))
    }

    // =================================================================
    //  Las velas
    // =================================================================

    /**
     * Una vela larga, dada por sus cuatro puños ya en el lienzo: [amura] (abajo, en el palo), [escota] (abajo, en la
     * botavara o la verga), [pico] (arriba, lejos) y [boca] (arriba, en el palo). [panza]: cuánto se hincha y hacia dónde
     * (unidades). [t] es la luz que le da (de −1 a 1). Los paños cosidos, los rizos con sus tomadores, la relinga, y la
     * luz que pasa a través de la lona cuando el sol está detrás.
     */
    fun DrawScope.vela(p: Pincel, amura: Offset, escota: Offset, pico: Offset, boca: Offset, panza: Offset, t: Float, az: Azar, panos: Int = 12, rizos: Int = 2, lejos: Float = 0f, luzDeDentro: Float = 0f) {
        fun en(u: Float, v: Float): Offset {
            val a = amura + (escota - amura) * u; val b = boca + (pico - boca) * u; val q = a + (b - a) * v
            return q + panza * (4f * u * (1f - u) * (0.4f + 0.6f * sin(PI.toFloat() * v)))
        }
        val n = 12
        val contorno = (0..n).map { en(it / n.toFloat(), 0f) } + (0..n).map { en(1f, it / n.toFloat()) } + (0..n).map { en(1f - it / n.toFloat(), 1f) } + (0..n).map { en(0f, 1f - it / n.toFloat()) }
        val f = poligono(contorno)
        val lona = C.LONA
        val luz = p.por(lona, t, lejos); val sombra = p.por(lona, t - 0.7f, lejos)
        drawPath(f, Brush.linearGradient(listOf(lerp(luz, sombra, 0.25f), luz, lerp(luz, sombra, 0.35f), lerp(luz, sombra, 0.75f)), en(0f, 0.5f), en(1f, 0.5f)))
        clipPath(f) {
            // Cada paño con su tono (la lona cosida de piezas de distinta tanda) y la costura con su doblez de luz.
            for (k in 0 until panos) {
                val u0 = k / panos.toFloat(); val u1 = (k + 1) / panos.toFloat()
                val pano = poligono((0..8).map { en(u0, it / 8f) } + (8 downTo 0).map { en(u1, it / 8f) })
                drawPath(pano, (if (az.real() > 0.5f) p.luz(lerp(lona, C.ARENA, 0.25f), lejos) else p.sombra(lerp(lona, C.ROCA, 0.2f), lejos)).copy(alpha = az.entre(0.05f, 0.16f)))
                if (k > 0) {
                    val linea = (0..10).map { en(u0, it / 10f) }
                    drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(lona, lejos).copy(alpha = 0.45f), style = Stroke(0.55f))
                    drawPath(Path().apply { moveTo(linea[0] + Offset(0.7f, 0f)); for (q in linea.drop(1)) lineTo(q + Offset(0.7f, 0f)) }, p.luz(lona, lejos).copy(alpha = 0.35f), style = Stroke(0.4f))
                }
            }
            // La panza: la sombra que se hunde hacia el centro y el brillo del lomo que la luz encuentra.
            mancha(en(0.5f, 0.42f), (escota - amura).getDistance() * 0.42f, (boca - amura).getDistance() * 0.38f, sombra, 0.4f)
            mancha(en(0.28f, 0.55f), (escota - amura).getDistance() * 0.18f, (boca - amura).getDistance() * 0.3f, luz, 0.35f)
            // La faja de refuerzo de abajo, más gruesa, con su sombra.
            val pie = (0..20).map { en(it / 20f, 0.035f) }
            drawPath(Path().apply { moveTo(pie[0]); for (q in pie.drop(1)) lineTo(q) }, p.sombra(lona, lejos).copy(alpha = 0.35f), style = Stroke(2.2f))
            // Los rizos: una faja de refuerzo y la fila de tomadores colgando.
            for (r in 1..rizos) {
                val v = 0.13f * r
                val fila = (0..20).map { en(it / 20f, v) }
                drawPath(Path().apply { moveTo(fila[0]); for (q in fila.drop(1)) lineTo(q) }, p.sombra(lona, lejos).copy(alpha = 0.4f), style = Stroke(1.1f))
                for (k in 1 until 20 step 1) { val q = fila[k]; drawLine(p.sombra(C.CANAMO, lejos).copy(alpha = 0.7f), q, q + Offset(az.entre(-0.4f, 0.4f), 2.2f + az.entre(0f, 1.6f)), 0.45f, StrokeCap.Round) }
            }
            // Las manchas del uso: el roce de la jarcia y el moho de la humedad.
            repeat(7) { mancha(en(az.entre(0.1f, 0.9f), az.entre(0.1f, 0.9f)), az.entre(6f, 18f), az.entre(3f, 9f), p.sombra(lerp(lona, C.ROCA, 0.4f), lejos), 0.12f) }
            // De noche, la luz que sube de abajo (la claraboya): fuerte junto al pie de la vela y apagándose hacia arriba.
            if (luzDeDentro > 0.01f) drawPath(f, Brush.linearGradient(listOf(C.LUMBRE.copy(alpha = (0.55f * luzDeDentro).coerceIn(0f, 1f)), C.LUMBRE.copy(alpha = (0.15f * luzDeDentro).coerceIn(0f, 1f)), Color.Transparent), en(0.3f, 0f), en(0.3f, 0.7f)), blendMode = BlendMode.Plus)
        }
        // La relinga: el cabo cosido a la orilla de la vela.
        drawPath(f, p.sombra(lerp(lona, C.CANAMO, 0.5f), lejos).copy(alpha = 0.7f), style = Stroke(0.8f, join = StrokeJoin.Round))
    }

    /**
     * Una vela aferrada a lo largo de un palo tendido, de [a] a [b]: un rollo de lona apretado, de [grueso] unidades, con
     * las tomaduras que lo ciñen cada tanto y los pliegues que asoman entre ellas.
     */
    fun DrawScope.velaAferrada(p: Pincel, a: Offset, b: Offset, grueso: Float, az: Azar, lejos: Float = 0f, nieve: Float = 0f) {
        val d = b - a; val largo = d.getDistance().coerceAtLeast(0.01f); val u = d / largo; var n = Offset(-u.y, u.x); if (n.y > 0f) n = -n
        val pasos = (largo / (grueso * 0.9f)).toInt().coerceIn(4, 40)
        for (i in 0 until pasos) {
            val q = a + d * ((i + 0.5f) / pasos); val g = grueso * (0.8f + 0.4f * az.real())
            drawOval(p.por(C.LONA, -0.3f, lejos), Offset(q.x - largo / pasos * 0.62f, q.y - g * 0.5f), Size(largo / pasos * 1.24f, g))
            drawOval(p.por(C.LONA, 0.6f, lejos), Offset(q.x - largo / pasos * 0.5f + n.x * g * 0.18f, q.y - g * 0.5f + n.y * g * 0.18f), Size(largo / pasos, g * 0.5f))
            if (nieve > 0.02f) drawOval(p.luz(C.NIEVE, lejos).copy(alpha = nieve), Offset(q.x - largo / pasos * 0.5f, q.y - g * 0.55f), Size(largo / pasos, g * 0.3f))
        }
        for (i in 1 until pasos step 2) { val q = a + d * (i.toFloat() / pasos); drawLine(p.sombra(C.CANAMO, lejos), q + n * (grueso * 0.55f), q - n * (grueso * 0.55f), (grueso * 0.12f).coerceAtLeast(0.4f)) }
    }

    /**
     * La escota de una vela: del [puno] baja hasta la [cubierta] por un aparejo de dos motones, con los guarnes que van
     * de uno a otro y el chicote adujado en la cabilla.
     */
    fun DrawScope.escota(p: Pincel, puno: Offset, cubierta: Offset, tam: Float, lejos: Float = 0f) {
        val d = cubierta - puno; val ang = atan2(d.x, -d.y) + PI.toFloat()
        val m1 = puno + d * 0.12f; val m2 = puno + d * 0.82f
        cabo(p, puno, m1, 0f, tam * 0.12f, lejos = lejos)
        val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f)
        for (k in -1..1) cabo(p, m1 + n * (k * tam * 0.12f), m2 + n * (k * tam * 0.12f), tam * 0.4f, tam * 0.08f, lejos = lejos, sombra = false)
        moton(p, m1, tam, ang, lejos); moton(p, m2, tam * 1.05f, ang, lejos)
        cabo(p, m2, cubierta, 0f, tam * 0.12f, lejos = lejos)
    }

    // =================================================================
    //  Las imágenes dentro de la imagen: herbario, insectos, la carta y las láminas
    // =================================================================
    //
    // Son los «frescos» de este tema (como los de la capilla de Roma o los dibujos clavados del taller de Renacimiento):
    // superficies que llevan su propio dibujo y lo enseñan cuando les da la luz. Cada una se pinta dentro de un
    // cuadrilátero ya puesto en el lienzo (sus cuatro esquinas: abajo-izquierda, abajo-derecha, arriba-derecha,
    // arriba-izquierda), así sirven tumbadas en una mesa o clavadas en una pared, en perspectiva. Sin letras: las
    // etiquetas y los rótulos son rayas.

    /** Un punto de un cuadrilátero [q] (abajo-izquierda, abajo-derecha, arriba-derecha, arriba-izquierda) en ([u], [v]). */
    fun en(q: List<Offset>, u: Float, v: Float): Offset { val a = q[0] + (q[1] - q[0]) * u; val b = q[3] + (q[2] - q[3]) * u; return a + (b - a) * v }

    /** La escala de un cuadrilátero: lo que mide su lado más largo, en unidades del lienzo. */
    private fun tamano(q: List<Offset>): Float = max((q[1] - q[0]).getDistance(), (q[3] - q[0]).getDistance())

    /**
     * Una hoja de herbario en [q]: el pliego de papel con la planta prensada (tallo, hojas y, según [planta], una flor,
     * una fronda de helecho o una rama con bayas), las tiras de papel engomado que la sujetan y la etiqueta de la
     * esquina con sus rayas. [t]: la luz que le da; [sol], de 0 a 1, si le da el sol de lleno (la enciende).
     */
    fun DrawScope.hojaDeHerbario(p: Pincel, q: List<Offset>, planta: Int, t: Float, az: Azar, lejos: Float = 0f, sol: Float = 0f) {
        val tam = tamano(q); val g = max(0.3f, tam * 0.012f)
        val papel = lerp(C.PAPEL, C.LONA, 0.3f)
        val forma = poligono(q)
        drawPath(forma, p.por(papel, t + 0.2f * sol, lejos))
        clipPath(forma) {
            // El papel no es parejo: manchas de humedad y el amarillo de los bordes.
            mancha(en(q, az.entre(0.2f, 0.8f), az.entre(0.2f, 0.8f)), tam * 0.3f, tam * 0.2f, p.por(lerp(papel, C.ARENA, 0.6f), t, lejos), 0.25f)
            drawPath(forma, p.por(lerp(papel, C.CUERO, 0.3f), t, lejos).copy(alpha = 0.35f), style = Stroke(tam * 0.06f))
            val verde = when (planta % 3) { 0 -> lerp(C.SELVA, C.HOJA, 0.3f); 1 -> lerp(C.HOJA, C.SELVA, 0.5f); else -> lerp(C.SELVA, C.CUERO, 0.35f) }
            val oscuro = p.por(lerp(verde, C.BREA, 0.25f), t - 0.3f, lejos); val claro = p.por(verde, t + 0.2f, lejos)
            // El tallo, de abajo arriba, con una curva.
            val tallo = (0..8).map { en(q, 0.5f + 0.08f * sin(it * 0.7f + planta), 0.12f + 0.75f * it / 8f) }
            drawPath(curva(tallo), oscuro, style = Stroke(g * 1.4f, cap = StrokeCap.Round))
            when (planta % 3) {
                0 -> {
                    // Hojas alternas, cada una con su nervio, y una flor arriba.
                    for (k in 1..6) {
                        val b = tallo[k]; val lado = if (k % 2 == 0) 1f else -1f; val l = tam * (0.16f - 0.012f * k)
                        val punta = b + (en(q, 0.5f + lado * 0.4f, 0.12f + 0.75f * k / 8f + 0.08f) - b) * (l / (tam * 0.4f))
                        val n = Offset(-(punta - b).y, (punta - b).x) * 0.28f
                        drawPath(Path().apply { moveTo(b); cubicTo(b + (punta - b) * 0.3f + n, punta - (punta - b) * 0.2f + n * 0.6f, punta); cubicTo(punta - (punta - b) * 0.2f - n * 0.6f, b + (punta - b) * 0.3f - n, b); close() }, if (k % 2 == 0) claro else lerp(claro, oscuro, 0.4f))
                        drawLine(oscuro.copy(alpha = 0.6f), b, punta, g * 0.5f)
                    }
                    val c = tallo.last(); val r = tam * 0.06f
                    for (k in 0 until 5) { val a = (2 * PI * k / 5).toFloat(); drawOval(p.por(lerp(C.TEJA, C.LONA, 0.25f), t, lejos), Offset(c.x + cos(a) * r - r * 0.5f, c.y + sin(a) * r - r * 0.5f), Size(r, r)) }
                    drawCircle(p.por(lerp(C.LATON, C.ARENA, 0.3f), t, lejos), r * 0.4f, c)
                }
                1 -> {
                    // Una fronda de helecho: el raquis y las pinnas, cada vez más cortas.
                    for (k in 1..14) { val v = k / 15f; val b = en(q, 0.5f + 0.04f * sin(v * 3f), 0.12f + 0.78f * v); val l = tam * 0.17f * (1f - v * 0.75f)
                        for (s in listOf(-1f, 1f)) drawLine(if (s > 0f) claro else lerp(claro, oscuro, 0.35f), b, b + Offset(s * l, -l * 0.25f), g * 1.3f, StrokeCap.Round) }
                }
                else -> {
                    // Una rama con hojas pequeñas y bayas.
                    for (k in 1..7) { val b = tallo[k]; for (s in listOf(-1f, 1f)) { val pt = b + Offset(s * tam * 0.07f, -tam * 0.03f); drawOval(if (s > 0f) claro else lerp(claro, oscuro, 0.4f), Offset(min(b.x, pt.x), pt.y - tam * 0.015f), Size(abs(pt.x - b.x), tam * 0.035f)) } }
                    for (k in 0..4) drawCircle(p.por(lerp(C.TEJA, C.ESCARABAJO, 0.3f), t, lejos), tam * 0.018f, tallo[6] + Offset((k - 2) * tam * 0.025f, tam * 0.03f * (k % 2)))
                }
            }
            // Las tiras de papel engomado que la sujetan, y la etiqueta con sus rayas.
            for ((u, v) in listOf(0.48f to 0.3f, 0.5f to 0.62f)) drawPath(poligono(en(q, u - 0.09f, v - 0.025f), en(q, u + 0.09f, v - 0.025f), en(q, u + 0.09f, v + 0.025f), en(q, u - 0.09f, v + 0.025f)), p.por(lerp(papel, C.ARENA, 0.4f), t, lejos).copy(alpha = 0.85f))
            val et = listOf(en(q, 0.62f, 0.04f), en(q, 0.95f, 0.04f), en(q, 0.95f, 0.2f), en(q, 0.62f, 0.2f))
            drawPath(poligono(et), p.por(C.LONA, t + 0.1f, lejos)); drawPath(poligono(et), oscuro.copy(alpha = 0.4f), style = Stroke(g * 0.5f))
            for (k in 0..2) drawLine(p.por(C.BREA, t, lejos).copy(alpha = 0.55f), en(q, 0.66f, 0.07f + k * 0.045f), en(q, 0.66f + az.entre(0.12f, 0.26f), 0.07f + k * 0.045f), g * 0.5f)
            if (sol > 0.02f) drawPath(forma, lerp(C.LONA, C.ARENA, 0.3f).copy(alpha = 0.18f * sol), blendMode = BlendMode.Plus)
        }
        drawPath(forma, p.sombra(papel, lejos).copy(alpha = 0.5f), style = Stroke(g * 0.6f))
    }

    /**
     * Una caja de insectos en [q]: el marco de madera con su vidrio, el fondo de papel y, en filas, los bichos clavados
     * con alfiler: las mariposas azules de brillo de metal, otras pardas, los escarabajos verdes y bronce, cada uno con
     * su etiqueta. [canto], hacia dónde se ve el grueso del marco (en unidades). [t]: la luz; [sol], el brillo del vidrio.
     */
    fun DrawScope.cajaDeInsectos(p: Pincel, q: List<Offset>, canto: Offset, t: Float, az: Azar, lejos: Float = 0f, sol: Float = 0f) {
        val tam = tamano(q); val g = max(0.3f, tam * 0.01f)
        // El canto del marco, el lado que se ve.
        drawPath(poligono(q[0], q[1], q[1] + canto, q[0] + canto), p.por(C.MADERA, t - 0.4f, lejos))
        drawPath(poligono(q[1], q[2], q[2] + canto, q[1] + canto), p.por(C.MADERA, t - 0.6f, lejos))
        val marco = poligono(q)
        drawPath(marco, p.por(C.CAOBA, t, lejos))
        val dentro = listOf(en(q, 0.06f, 0.08f), en(q, 0.94f, 0.08f), en(q, 0.94f, 0.92f), en(q, 0.06f, 0.92f))
        val fondo = poligono(dentro)
        drawPath(fondo, p.por(lerp(C.LONA, C.ARENA, 0.2f), t, lejos))
        clipPath(fondo) {
            // La sombra del marco sobre el fondo, del lado de la luz.
            drawPath(fondo, p.sombra(C.CAOBA, lejos).copy(alpha = 0.35f), style = Stroke(tam * 0.05f))
            for (fila in 0..2) for (col in 0..3) {
                val c = en(q, 0.17f + col * 0.22f, 0.22f + fila * 0.27f); val k = tam * 0.06f
                when ((fila + col + (az.real() * 2).toInt()) % 3) {
                    0 -> {
                        // Una mariposa azul de brillo de metal: cuatro alas con el borde negro.
                        val azul = lerp(Color(0xFF3E78C8), C.ESCARABAJO, 0.15f)
                        for (s in listOf(-1f, 1f)) {
                            drawOval(Brush.linearGradient(listOf(p.luz(lerp(azul, Color.White, 0.25f), lejos), p.medio(azul, lejos), p.sombra(azul, lejos)), c, c + Offset(s * k * 1.3f, -k)), Offset(min(c.x, c.x + s * k * 1.3f), c.y - k), Size(k * 1.3f, k * 1.05f))
                            drawOval(p.medio(lerp(azul, C.BREA, 0.3f), lejos), Offset(min(c.x, c.x + s * k), c.y - k * 0.05f), Size(k, k * 0.8f))
                            drawOval(p.sombra(C.BREA, lejos).copy(alpha = 0.7f), Offset(min(c.x, c.x + s * k * 1.3f), c.y - k), Size(k * 1.3f, k * 1.05f), style = Stroke(k * 0.12f))
                        }
                        drawLine(p.sombra(C.BREA, lejos), c + Offset(0f, -k * 0.7f), c + Offset(0f, k * 0.7f), k * 0.16f)
                    }
                    1 -> {
                        // Un escarabajo verde metálico, con su brillo.
                        drawOval(Brush.linearGradient(listOf(p.luz(lerp(C.ESCARABAJO, C.HOJA, 0.4f), lejos), p.sombra(C.ESCARABAJO, lejos)), c + Offset(-k * 0.4f, -k * 0.6f), c + Offset(k * 0.4f, k * 0.6f)), Offset(c.x - k * 0.42f, c.y - k * 0.55f), Size(k * 0.84f, k * 1.2f))
                        drawLine(p.sombra(C.BREA, lejos), c + Offset(0f, -k * 0.2f), c + Offset(0f, k * 0.6f), k * 0.06f)
                        for (s in listOf(-1f, 1f)) for (j in 0..2) drawLine(p.sombra(C.BREA, lejos), c + Offset(s * k * 0.35f, (j - 1) * k * 0.3f), c + Offset(s * k * 0.65f, (j - 1) * k * 0.4f + k * 0.1f), k * 0.06f)
                        drawCircle(p.luz(Color.White, lejos).copy(alpha = 0.5f), k * 0.1f, c + Offset(-k * 0.15f, -k * 0.25f))
                    }
                    else -> {
                        // Una mariposa parda con ocelos naranjas.
                        for (s in listOf(-1f, 1f)) { drawOval(p.por(lerp(C.CUERO, C.ARENA, 0.3f), t, lejos), Offset(min(c.x, c.x + s * k * 1.1f), c.y - k * 0.8f), Size(k * 1.1f, k * 0.9f)); drawCircle(p.por(C.NARANJA, t, lejos), k * 0.18f, c + Offset(s * k * 0.6f, -k * 0.4f)) }
                        drawLine(p.sombra(C.BREA, lejos), c + Offset(0f, -k * 0.6f), c + Offset(0f, k * 0.5f), k * 0.14f)
                    }
                }
                // La etiqueta, debajo.
                drawLine(p.sombra(C.BREA, lejos).copy(alpha = 0.4f), c + Offset(-k * 0.5f, k * 1.25f), c + Offset(k * 0.5f, k * 1.25f), g * 0.6f)
            }
            // El vidrio: un reflejo en diagonal (más vivo si le da el sol).
            drawPath(poligono(en(q, 0.1f, 0.95f), en(q, 0.3f, 0.95f), en(q, 0.75f, 0.05f), en(q, 0.55f, 0.05f)), Color.White.copy(alpha = (0.08f + 0.2f * sol).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
        }
        drawPath(marco, p.sombra(C.BREA, lejos).copy(alpha = 0.5f), style = Stroke(g * 0.6f))
    }

    /**
     * La carta marina de una costa que se está levantando, en [q]: el papel, la costa dibujada con su sombreado de
     * montes, las sondas en puntos, las líneas de rumbo que salen de la rosa de los vientos y la escala (sin letras).
     */
    fun DrawScope.cartaMarina(p: Pincel, q: List<Offset>, t: Float, az: Azar, lejos: Float = 0f, sol: Float = 0f) {
        val tam = tamano(q); val g = max(0.25f, tam * 0.006f)
        val papel = lerp(C.PAPEL, C.ARENA, 0.15f)
        val forma = poligono(q)
        drawPath(forma, p.por(papel, t + 0.2f * sol, lejos))
        clipPath(forma) {
            val tinta = p.por(lerp(C.BREA, C.CUERO, 0.3f), t, lejos)
            // Las líneas de rumbo, desde la rosa.
            val rosa = en(q, 0.68f, 0.3f)
            for (k in 0 until 16) { val a = (2 * PI * k / 16).toFloat(); drawLine(tinta.copy(alpha = 0.18f), rosa, rosa + Offset(cos(a), sin(a)) * (tam * 1.2f), g * 0.6f) }
            drawCircle(tinta.copy(alpha = 0.55f), tam * 0.07f, rosa, style = Stroke(g)); drawCircle(tinta.copy(alpha = 0.4f), tam * 0.045f, rosa, style = Stroke(g * 0.7f))
            for (k in 0 until 4) { val a = (PI / 2 * k).toFloat() - PI.toFloat() / 2f; drawPath(poligono(rosa + Offset(cos(a), sin(a)) * (tam * 0.09f), rosa + Offset(cos(a + 0.5f), sin(a + 0.5f)) * (tam * 0.022f), rosa + Offset(cos(a - 0.5f), sin(a - 0.5f)) * (tam * 0.022f)), if (k == 0) p.por(C.TEJA, t, lejos) else tinta.copy(alpha = 0.6f)) }
            // La costa: la tierra a la izquierda, con su orilla rota y el sombreado de los montes.
            val orilla = (0..14).map { val v = it / 14f; en(q, 0.32f + 0.1f * sin(v * 9f + 1f) + 0.06f * sin(v * 23f) + az.entre(-0.02f, 0.02f), v) }
            val tierra = poligono(orilla + listOf(en(q, 0f, 1f), en(q, 0f, 0f)))
            drawPath(tierra, p.por(lerp(papel, C.ARENA, 0.4f), t, lejos))
            drawPath(curva(orilla), tinta.copy(alpha = 0.8f), style = Stroke(g * 1.4f))
            clipPath(tierra) { repeat(30) { val c = en(q, az.entre(0.03f, 0.3f), az.entre(0.05f, 0.95f)); drawLine(tinta.copy(alpha = 0.35f), c, c + Offset(tam * 0.03f, tam * 0.015f), g * 0.6f) } }
            // Las sondas: puntos y rayitas en el agua, más juntas cerca de la costa; y un bajo marcado con cruces.
            repeat(40) { val v = az.real(); val u = 0.38f + 0.55f * az.real() * az.real(); drawCircle(tinta.copy(alpha = 0.55f), g * 0.7f, en(q, u, v)) }
            val bajo = en(q, 0.5f, 0.75f); for (k in 0..2) { val c = bajo + Offset(k * tam * 0.03f, 0f); drawLine(tinta.copy(alpha = 0.6f), c + Offset(-g * 2f, -g * 2f), c + Offset(g * 2f, g * 2f), g * 0.7f); drawLine(tinta.copy(alpha = 0.6f), c + Offset(g * 2f, -g * 2f), c + Offset(-g * 2f, g * 2f), g * 0.7f) }
            // La escala, abajo.
            for (k in 0..4) drawLine(tinta.copy(alpha = 0.6f), en(q, 0.55f + k * 0.07f, 0.06f), en(q, 0.62f + k * 0.07f, 0.06f), if (k % 2 == 0) g * 2f else g * 0.6f)
            mancha(en(q, 0.2f, 0.85f), tam * 0.15f, tam * 0.08f, p.por(lerp(papel, C.CUERO, 0.4f), t, lejos), 0.18f)
        }
        drawPath(forma, p.sombra(papel, lejos).copy(alpha = 0.5f), style = Stroke(g))
    }

    /** Una lámina de dibujo en [q]: un pájaro (un pinzón) a lápiz y aguada sepia, sobre su rama, con sus notas en rayas. */
    fun DrawScope.lamina(p: Pincel, q: List<Offset>, t: Float, az: Azar, lejos: Float = 0f) {
        val tam = tamano(q); val g = max(0.25f, tam * 0.008f)
        val forma = poligono(q)
        drawPath(forma, p.por(lerp(C.PAPEL, C.LONA, 0.5f), t, lejos))
        clipPath(forma) {
            val sepia = p.por(lerp(C.CUERO, C.BREA, 0.3f), t, lejos)
            val c = en(q, 0.5f, 0.55f); val k = tam * 0.16f
            // La rama.
            drawLine(sepia.copy(alpha = 0.7f), en(q, 0.15f, 0.35f), en(q, 0.85f, 0.42f), g * 2f, StrokeCap.Round)
            // El cuerpo, la cabeza con su pico grueso, el ala y la cola, en aguada.
            drawOval(p.por(lerp(C.CUERO, C.ARENA, 0.4f), t, lejos).copy(alpha = 0.75f), Offset(c.x - k * 0.8f, c.y - k * 0.5f), Size(k * 1.6f, k))
            drawCircle(p.por(lerp(C.CUERO, C.BREA, 0.2f), t, lejos).copy(alpha = 0.8f), k * 0.38f, c + Offset(k * 0.7f, -k * 0.35f))
            drawPath(poligono(c + Offset(k * 1.0f, -k * 0.45f), c + Offset(k * 1.35f, -k * 0.3f), c + Offset(k * 1.0f, -k * 0.2f)), sepia)
            drawPath(poligono(c + Offset(-k * 0.6f, -k * 0.1f), c + Offset(-k * 1.6f, -k * 0.35f), c + Offset(-k * 1.5f, k * 0.05f)), sepia.copy(alpha = 0.7f))
            drawOval(sepia.copy(alpha = 0.5f), Offset(c.x - k * 0.5f, c.y - k * 0.35f), Size(k * 0.9f, k * 0.45f), style = Stroke(g))
            drawCircle(p.por(C.BREA, t, lejos), k * 0.06f, c + Offset(k * 0.82f, -k * 0.42f))
            // Las notas, en rayas, y la mancha de un dedo.
            for (j in 0..3) drawLine(sepia.copy(alpha = 0.4f), en(q, 0.1f, 0.15f - j * 0.025f + 0.08f), en(q, 0.1f + az.entre(0.3f, 0.6f), 0.15f - j * 0.025f + 0.08f), g * 0.6f)
            mancha(en(q, 0.8f, 0.8f), tam * 0.06f, tam * 0.04f, sepia, 0.15f)
        }
        drawPath(forma, p.sombra(C.PAPEL, lejos).copy(alpha = 0.5f), style = Stroke(g))
    }

    // =================================================================
    //  La selva: la hoja de plátano
    // =================================================================

    /**
     * Una hoja de plátano, de [base] a [punta] (en el lienzo), de [ancho] unidades en lo más ancho; el nervio se curva
     * [curva] unidades a su izquierda (negativo, a su derecha). [vuelta], de −1 a 1: cuánto se ve de canto cada mitad
     * (la hoja se dobla por el nervio). [rasgado], de 0 a 1: cuántos rasgones la parten, del borde hasta el nervio. [seca],
     * de 0 a 1: el borde seco y pardo de las hojas viejas. [t]: la luz que le da (de −1 a 1). [contraluz], de 0 a 1:
     * cuánto sol le pasa por detrás; a contraluz la lámina se vuelve translúcida, verde amarillo encendido, con las venas
     * paralelas y el nervio oscuros, y lo que la tapa por detrás (otra hoja) se adivina más oscuro.
     */
    fun DrawScope.hojaDePlatano(p: Pincel, base: Offset, punta: Offset, ancho: Float, curva: Float, vuelta: Float, rasgado: Float, seca: Float, t: Float, az: Azar, lejos: Float = 0f, contraluz: Float = 0f) {
        val d = punta - base; val largo = d.getDistance().coerceAtLeast(0.01f); val u = d / largo; val n = Offset(-u.y, u.x)
        fun nervio(s: Float): Offset { val q = base + d * s; return q + n * (curva * 4f * s * (1f - s)) }
        fun tangente(s: Float): Offset { val a = nervio((s - 0.02f).coerceAtLeast(0f)); val b = nervio((s + 0.02f).coerceAtMost(1f)); val dd = b - a; return dd / dd.getDistance().coerceAtLeast(0.001f) }
        fun anchoEn(s: Float): Float = ancho * 0.5f * (sin(PI.toFloat() * s.coerceIn(0f, 1f)).pow(0.5f)) * (if (s < 0.1f) s / 0.1f else 1f) * (1f - 0.18f * s * s)
        // Cada hoja con su verde: las nuevas más claras y amarillas, las viejas más hondas y azuladas.
        val edad = az.real()
        val hoja = when { edad < 0.22f -> lerp(C.HOJA, C.HOJA_LUZ, 0.28f); edad > 0.8f -> lerp(C.SELVA, C.ESCARABAJO, 0.3f); else -> lerp(C.HOJA, C.SELVA, 0.12f + 0.3f * az.real()) }
        val verdeSeco = lerp(C.ARENA, C.CUERO, 0.45f)
        val cl = contraluz.coerceIn(0f, 1f)
        // La luz que pasa a través de la lámina: verde amarillo, y más pálida en el centro de cada tira.
        val translucida = p.l.tono(lerp(C.HOJA_LUZ, C.LATON, 0.18f), lejos)
        val vena = p.l.tono(lerp(hoja, C.SELVA_OSC, 0.35f), lejos)
        for (lado in listOf(-1f, 1f)) {
            // Cada mitad se ve más o menos de canto según la vuelta; la que da la espalda, más oscura y azulada.
            val canto = (0.5f + 0.5f * lado * vuelta).coerceIn(0.18f, 1f)
            val tl = (t + lado * vuelta * 0.5f).coerceIn(-1f, 1f)
            val propio = p.por(if (lado * vuelta < -0.2f) lerp(hoja, C.ESCARABAJO, 0.25f) else hoja, tl, lejos)
            val k = cl * (0.6f + 0.4f * canto)
            val color = lerp(propio, translucida, k)
            // Los rasgones: de dónde a dónde va cada tira entre dos cortes.
            val cortes = ArrayList<Float>(); var s = 0.12f + az.entre(0f, 0.1f)
            while (s < 0.9f) { if (az.real() < rasgado) cortes += s; s += az.entre(0.07f, 0.17f) }
            val bordes = listOf(0.02f) + cortes + listOf(0.995f)
            for (i in 0 until bordes.size - 1) {
                val s0 = bordes[i]; val s1 = bordes[i + 1]; val hueco = if (i > 0) 0.013f else 0f; val hueco1 = if (i < bordes.size - 2) 0.013f else 0f
                // La tira cuelga un poco, tanto más cuanto más larga y suelta.
                val cae = Offset(0f, (s1 - s0) * largo * 0.05f * (if (i > 0 && i < bordes.size - 2) 1f else 0.3f))
                val pasos = 7
                val dentro = (0..pasos).map { j -> nervio(s0 + (s1 - s0) * j / pasos) }
                val fuera = (0..pasos).map { j -> val ss = s0 + hueco + (s1 - hueco1 - s0 - hueco) * j / pasos; val tg = tangente(ss); val nn = Offset(-tg.y, tg.x)
                    nervio(ss) + nn * (lado * anchoEn(ss) * canto * (1f - 0.06f * sin(j * 1.9f + i))) + cae * (j / pasos.toFloat()) * lado.coerceAtLeast(0.3f) }
                val tira = poligono(dentro + fuera.reversed())
                drawPath(tira, color)
                // (todo lo de encima se queda dentro de la tira por cómo se traza, sin recortar: así cuesta menos)
                // A contraluz, la lámina se enciende más hacia el borde de cada tira y el nervio queda oscuro.
                if (k > 0.05f) drawPath(Path().apply { val pts = (1 until pasos).map { j -> val ss = s0 + (s1 - s0) * j / pasos; val tg = tangente(ss); nervio(ss) + Offset(-tg.y, tg.x) * (lado * anchoEn(ss) * canto * 0.55f) }; moveTo(pts[0]); for (q in pts.drop(1)) lineTo(q) },
                    lerp(translucida, Color(0xFFFFF4C0), 0.4f).copy(alpha = (0.4f * k).coerceIn(0f, 1f)), style = Stroke(ancho * 0.16f * canto), blendMode = BlendMode.Plus)
                // Las venas, paralelas, del nervio al borde, inclinadas hacia la punta.
                if (ancho > 5f) {
                    val venas = ArrayList<Offset>(); var v = s0 + 0.01f; val paso = if (ancho > 20f) 0.02f else 0.035f
                    while (v < s1 - 0.01f) { val tg = tangente(v); val nn = Offset(-tg.y, tg.x); val a = nervio(v); val w = anchoEn(v) * canto * 0.9f; venas += a; venas += a + nn * (lado * w) + tg * (w * 0.3f); v += paso }
                    drawPoints(venas, PointMode.Lines, (if (k > 0.2f) vena else p.sombra(hoja, lejos)).copy(alpha = (0.12f + 0.4f * k).coerceIn(0f, 1f)), if (k > 0.2f) 0.45f else 0.3f)
                }
                // El brillo del haz, a lo largo, del lado del sol (de frente; a contraluz no se ve).
                if (lado * vuelta >= -0.2f && k < 0.5f) drawPath(Path().apply { val pts = (1..5).map { j -> val ss = s0 + (s1 - s0) * j / 6f; val tg = tangente(ss); nervio(ss) + Offset(-tg.y, tg.x) * (lado * anchoEn(ss) * canto * 0.45f) }; moveTo(pts[0]); for (q in pts.drop(1)) lineTo(q) },
                    p.luz(lerp(hoja, C.ARENA, 0.3f), lejos).copy(alpha = 0.3f * p.r.dia * (1f - 2f * k)), style = Stroke(ancho * 0.07f))
                // El borde seco, pardo, en las hojas viejas: por dentro del borde.
                if (seca > 0.02f) { val sw = ancho * 0.12f * seca * az.entre(0.5f, 1.3f)
                    drawPath(Path().apply { val pts = fuera.mapIndexed { j, q -> val d0 = dentro[j] - q; val dl = d0.getDistance().coerceAtLeast(0.01f); q + d0 * (sw * 0.5f / dl).coerceAtMost(0.5f) }; moveTo(pts[0]); for (q in pts.drop(1)) lineTo(q) },
                        lerp(p.por(verdeSeco, tl - 0.2f, lejos), p.l.tono(lerp(C.LATON, C.ARENA, 0.3f), lejos), 0.6f * k), style = Stroke(sw)) }
            }
        }
        // El nervio, grueso, con su sombra; a contraluz, una raya oscura con el filo claro.
        val nerv = (0..12).map { nervio(it / 12f) }
        drawPath(Path().apply { moveTo(nerv[0]); for (q in nerv.drop(1)) lineTo(q) }, lerp(p.sombra(lerp(C.HOJA, C.SELVA, 0.4f), lejos), vena, cl), style = Stroke(ancho * 0.07f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(nerv[0]); for (q in nerv.drop(1).dropLast(3)) lineTo(q) }, lerp(p.luz(C.HOJA_LUZ, lejos), translucida, cl), style = Stroke(ancho * 0.03f, cap = StrokeCap.Round))
    }

    /**
     * Una mata de plátano, con el [pie] en el suelo y [alto] unidades hasta la cima de las hojas: el falso tronco de
     * vainas, con las viejas secas colgando, y las hojas grandes, cada una con su ángulo, alguna partida y caída. Con
     * [racimo], el racimo verde colgando y su bellota morada. [contraluz]: el sol que pasa por las hojas altas.
     */
    fun DrawScope.platano(p: Pincel, pie: Offset, alto: Float, az: Azar, lejos: Float = 0f, racimo: Boolean = false, hojas: Int = 7, contraluz: Float = 0f) {
        val cima = pie + Offset(az.entre(-0.06f, 0.06f) * alto, -alto * 0.5f)
        val g0 = alto * 0.065f; val g1 = alto * 0.04f
        val lado = if (p.solALaDerecha) 1f else -1f
        val tronco = poligono(listOf(pie + Offset(-g0, 0f), pie + Offset(g0, 0f), pie + Offset(g0 * 0.92f, -alto * 0.08f), cima + Offset(g1, 0f), cima + Offset(-g1, 0f), pie + Offset(-g0 * 0.95f, -alto * 0.06f)))
        drawPath(tronco, Brush.horizontalGradient(listOf(p.sombra(lerp(C.HOJA, C.CUERO, 0.25f), lejos), p.medio(lerp(C.HOJA, C.ARENA, 0.25f), lejos), p.luz(lerp(C.HOJA, C.ARENA, 0.35f), lejos)), pie.x - lado * g0, pie.x + lado * g0))
        clipPath(tronco) {
            // Las vainas: bandas que suben en espiral, unas más verdes y otras pardas y rotas; las manchas oscuras.
            for (k in 0..7) { val y = pie.y - (pie.y - cima.y) * k / 8f
                drawPath(Path().apply { moveTo(Offset(pie.x - g0 * 1.2f, y)); cubicTo(Offset(pie.x - g0 * 0.3f, y - g0 * 0.6f), Offset(pie.x + g0 * 0.4f, y - g0 * 1.1f), Offset(pie.x + g0 * 1.2f, y - g0 * 2f)) },
                    p.sombra(lerp(C.HOJA, C.BREA, 0.4f), lejos).copy(alpha = 0.45f), style = Stroke(max(0.4f, alto * 0.007f))) }
            repeat(6) { mancha(pie + Offset(az.entre(-g0, g0), -az.entre(0f, alto * 0.45f)), g0 * 0.4f, g0 * 0.9f, p.medio(lerp(C.CUERO, C.BREA, 0.3f), lejos), 0.35f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.6f)), pie.y - alto * 0.12f, pie.y), Offset(pie.x - g0 * 1.5f, pie.y - alto * 0.12f), Size(g0 * 3f, alto * 0.12f))
        }
        // Las hojas viejas, secas, colgando pegadas al tronco.
        repeat(3) { val a = cima + Offset(az.entre(-g1, g1), alto * az.entre(0.02f, 0.14f)); hojaDePlatano(p, a, a + Offset(az.entre(-0.12f, 0.12f) * alto, alto * az.entre(0.22f, 0.32f)), alto * 0.06f, alto * 0.02f, 0.7f, 0.9f, 1f, -0.3f, az, lejos) }
        // Las hojas vivas: abiertas en abanico, las de atrás primero; las más altas son las más nuevas, tiesas y claras.
        val angulos = (0 until hojas).map { -PI.toFloat() / 2f + az.entre(-1.45f, 1.45f) }.sortedBy { abs(it + PI.toFloat() / 2f) }.reversed()
        for ((i, a) in angulos.withIndex()) {
            val largo = alto * az.entre(0.42f, 0.6f)
            val abre = abs(a + PI.toFloat() / 2f)
            val cae = abre * 0.55f
            val punta = cima + Offset(cos(a) * largo, sin(a) * largo * 0.7f + largo * cae)
            val alta = abre < 0.6f
            val rota = az.real() < 0.18f
            if (rota) {
                // Una hoja partida: el pecíolo doblado y la hoja colgando.
                val codo = cima + Offset(cos(a) * largo * 0.25f, sin(a) * largo * 0.2f)
                drawLine(p.medio(lerp(C.HOJA, C.ARENA, 0.2f), lejos), cima, codo, alto * 0.016f)
                hojaDePlatano(p, codo, codo + Offset(cos(a) * largo * 0.15f, largo * 0.6f), alto * 0.16f, largo * 0.05f, az.entre(-0.6f, 0.6f), 0.8f, 0.6f, -0.2f, az, lejos, contraluz * 0.4f)
            } else {
                drawLine(p.medio(lerp(C.HOJA, C.ARENA, 0.25f), lejos), cima, cima + (punta - cima) * 0.12f, alto * 0.016f)
                hojaDePlatano(p, cima + (punta - cima) * 0.1f, punta, alto * az.entre(0.17f, 0.23f), largo * az.entre(-0.12f, 0.12f), az.entre(-0.7f, 0.7f), az.entre(0.3f, 0.85f), if (i < 2) 0.6f else 0.1f,
                    (0.2f + 0.4f * cos(a) * lado).coerceIn(-1f, 1f), az, lejos, contraluz * (if (alta) 1f else 0.55f))
            }
        }
        if (racimo) {
            // El racimo cuelga de la cima, por delante, en su tallo grueso y curvo: las manos de plátanos verdes, de arriba
            // abajo cada vez más chicas, cada plátano con su lado de luz, y abajo la bellota morada en su tallo.
            val a = cima + Offset(lado * alto * 0.05f, alto * 0.03f); val b = a + Offset(lado * alto * 0.1f, alto * 0.3f)
            drawPath(Path().apply { moveTo(a); cubicTo(a + Offset(lado * alto * 0.09f, -alto * 0.03f), b + Offset(0f, -alto * 0.16f), b) }, p.medio(lerp(C.HOJA, C.CUERO, 0.3f), lejos), style = Stroke(alto * 0.016f, cap = StrokeCap.Round))
            val verde = lerp(C.HOJA, C.HOJA_LUZ, 0.35f)
            for (k in 0..5) { val q = a + (b - a) * (0.12f + k * 0.13f) + Offset(lado * alto * 0.012f * (1f - k / 5f), 0f); val w = 1f - k * 0.09f
                for (j in -3..3) { val c = Offset(q.x + j * alto * 0.012f * w, q.y - alto * 0.02f * (1f - abs(j) * 0.15f))
                    val dedo = Path().apply { moveTo(c + Offset(0f, alto * 0.035f)); cubicTo(c + Offset(-alto * 0.014f, alto * 0.012f), c + Offset(-alto * 0.01f, -alto * 0.022f), c + Offset(j * alto * 0.005f, -alto * 0.038f)); cubicTo(c + Offset(alto * 0.008f, -alto * 0.022f), c + Offset(alto * 0.012f, alto * 0.012f), c + Offset(0f, alto * 0.035f)); close() }
                    drawPath(dedo, p.por(verde, if (j * lado > 0) 0.5f else if (j == 0) 0.1f else -0.35f, lejos)) }
                drawLine(p.sombra(lerp(C.HOJA, C.SELVA_OSC, 0.4f), lejos).copy(alpha = 0.6f), q + Offset(-alto * 0.04f * w, alto * 0.016f), q + Offset(alto * 0.04f * w, alto * 0.016f), max(0.4f, alto * 0.006f)) }
            // La bellota morada, colgando de su tallo, con sus brácteas en escamas.
            val bel = b + Offset(lado * alto * 0.01f, alto * 0.07f)
            drawLine(p.medio(lerp(C.HOJA, C.CUERO, 0.3f), lejos), b, bel, alto * 0.01f)
            val bellota = Path().apply { moveTo(bel + Offset(0f, -alto * 0.018f)); cubicTo(bel + Offset(alto * 0.032f, 0f), bel + Offset(alto * 0.022f, alto * 0.075f), bel + Offset(0f, alto * 0.1f)); cubicTo(bel + Offset(-alto * 0.022f, alto * 0.075f), bel + Offset(-alto * 0.032f, 0f), bel + Offset(0f, -alto * 0.018f)); close() }
            drawPath(bellota, Brush.horizontalGradient(listOf(p.sombra(lerp(C.TEJA, C.SOMBRA, 0.6f), lejos), p.luz(lerp(C.TEJA, C.SOMBRA, 0.42f), lejos)), bel.x - lado * alto * 0.025f, bel.x + lado * alto * 0.025f))
            for (k in 1..3) drawLine(p.sombra(lerp(C.SOMBRA, C.BREA, 0.3f), lejos).copy(alpha = 0.6f), bel + Offset(-alto * 0.02f, alto * 0.022f * k), bel + Offset(alto * 0.02f, alto * 0.022f * k + alto * 0.008f), max(0.3f, alto * 0.004f))
        }
    }

    // =================================================================
    //  La selva: la palmera
    // =================================================================

    /**
     * Una palmera con el [pie] en el suelo y la copa a [alto] unidades, el tronco vencido [inclina] unidades a un lado:
     * el tronco anillado y curvo, con su lado de luz; la copa de pencas, cada una con sus folíolos colgando, las de
     * atrás más oscuras; las viejas, secas y caídas bajo la copa; con [cocos], el racimo. [contraluz]: las pencas
     * con el sol detrás se encienden.
     */
    fun DrawScope.palmera(p: Pincel, pie: Offset, alto: Float, inclina: Float, az: Azar, lejos: Float = 0f, cocos: Boolean = false, pencas: Int = 11, contraluz: Float = 0f, secas: Int = 3) {
        val cima = pie + Offset(inclina, -alto)
        val c1 = pie + Offset(inclina * 0.1f, -alto * 0.4f); val c2 = cima + Offset(-inclina * 0.25f, alto * 0.25f)
        fun eje(t: Float): Offset { val m = 1f - t; return pie * (m * m * m) + c1 * (3f * m * m * t) + c2 * (3f * m * t * t) + cima * (t * t * t) }
        val g0 = alto * 0.032f; val g1 = alto * 0.022f
        val lado = if (p.solALaDerecha) 1f else -1f
        val tronco = poligono((0..10).map { eje(it / 10f) + Offset(-(g0 + (g1 - g0) * it / 10f), 0f) } + (10 downTo 0).map { eje(it / 10f) + Offset(g0 + (g1 - g0) * it / 10f, 0f) })
        drawPath(tronco, p.medio(lerp(C.ROCA, C.ARENA, 0.2f), lejos))
        clipPath(tronco) {
            // El lado de luz y el de sombra, y los anillos que dejan las pencas caídas.
            for (k in 0..10) { val q = eje(k / 10f); drawLine(p.luz(lerp(C.ROCA, C.ARENA, 0.45f), lejos).copy(alpha = 0.7f), q + Offset(lado * g0 * 0.1f, 0f), q + Offset(lado * g0 * 1.1f, 0f), max(0.5f, g0 * 0.7f)) }
            for (k in 0..10) { val q = eje(k / 10f); drawLine(p.sombra(C.ROCA, lejos).copy(alpha = 0.55f), q + Offset(-lado * g0 * 1.1f, 0f), q + Offset(-lado * g0 * 0.35f, 0f), max(0.4f, g0 * 0.7f)) }
            for (k in 0..30) { val q = eje(k / 30f); drawLine(p.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.5f), q + Offset(-g0 * 1.2f, 0f), q + Offset(g0 * 1.2f, -g0 * 0.25f), max(0.3f, g0 * 0.14f)) }
            // El verde del musgo que sube del pie por el lado húmedo.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(lerp(C.SELVA, C.HOJA, 0.3f), lejos).copy(alpha = 0.5f)), pie.y - alto * 0.2f, pie.y), Offset(pie.x - g0 * 2f, pie.y - alto * 0.2f), Size(g0 * 2f, alto * 0.2f))
        }
        // El capitel verde bajo la copa (el palmito), y las pencas secas colgando.
        drawPath(poligono(cima + Offset(-g1 * 1.2f, 0f), cima + Offset(g1 * 1.2f, 0f), cima + Offset(g1, alto * 0.08f), cima + Offset(-g1, alto * 0.08f)), p.por(lerp(C.HOJA, C.SELVA, 0.3f), lado * 0.3f, lejos))
        repeat(secas) { val a = PI.toFloat() / 2f + az.entre(-0.5f, 0.5f); penca(p, cima, a, alto * 0.22f, 0.9f, p.medio(lerp(C.ARENA, C.CUERO, 0.4f), lejos), p.sombra(C.CUERO, lejos), az, lejos) }
        if (cocos) for (k in 0..4) { val q = cima + Offset(az.entre(-1f, 1f) * alto * 0.03f, alto * az.entre(0.01f, 0.05f)); drawCircle(p.por(lerp(C.HOJA, C.CUERO, 0.5f), if (k % 2 == 0) 0.3f else -0.4f, lejos), alto * 0.022f, q) }
        // Las pencas vivas: primero las de atrás (hacia arriba y oscuras), luego las de los lados, luego las que caen hacia delante.
        val angulos = (0 until pencas).map { -PI.toFloat() / 2f + (it - pencas / 2f) * (3f / pencas) + az.entre(-0.12f, 0.12f) }
        val brillo = p.l.tono(lerp(C.HOJA_LUZ, C.LATON, 0.15f), lejos)
        for ((i, a) in angulos.sortedBy { abs(it + PI.toFloat() / 2f) }.withIndex()) {
            val atras = i < pencas / 3
            val verde = if (atras) lerp(C.SELVA, C.BREA, 0.15f) else lerp(C.SELVA, C.HOJA, 0.35f + 0.25f * az.real())
            val tl = (cos(a) * lado * 0.6f + 0.2f).coerceIn(-1f, 1f)
            val cl = contraluz * (if (atras) 0.5f else 1f)
            penca(p, cima, a, alto * az.entre(0.3f, 0.42f), az.entre(0.3f, 0.75f), lerp(p.por(verde, if (atras) -0.4f else tl, lejos), brillo, 0.7f * cl), lerp(p.sombra(verde, lejos), p.l.tono(lerp(C.HOJA, C.SELVA, 0.3f), lejos), 0.6f * cl), az, lejos)
        }
        drawCircle(p.sombra(C.SELVA, lejos), max(0.8f, alto * 0.022f), cima)
    }

    /** Una penca de palmera que sale de [c] hacia [ang], de [largo] unidades, y se vence [cae] (de 0 a 1): el raquis y los folíolos que cuelgan. */
    private fun DrawScope.penca(p: Pincel, c: Offset, ang: Float, largo: Float, cae: Float, color: Color, oscuro: Color, az: Azar, lejos: Float) {
        val fin = c + Offset(cos(ang) * largo, sin(ang) * largo * 0.6f + largo * cae * 0.7f)
        val ctrl = c + Offset(cos(ang) * largo * 0.6f, sin(ang) * largo * 0.7f - largo * 0.12f)
        fun en(t: Float): Offset { val m = 1f - t; return c * (m * m) + ctrl * (2f * m * t) + fin * (t * t) }
        val g = max(0.35f, largo * 0.016f)
        // Los folíolos: dos filas que cuelgan del raquis, los de un lado al sol y los del otro en su sombra.
        val luz = ArrayList<Offset>(80); val som = ArrayList<Offset>(80)
        for (k in 2..24) {
            val t = k / 24f; val q = en(t); val dd = en((t + 0.04f).coerceAtMost(1f)) - en(t - 0.04f); val tg = dd / dd.getDistance().coerceAtLeast(0.01f); val n = Offset(-tg.y, tg.x)
            val l = largo * 0.21f * (1f - t * 0.6f) * az.entre(0.8f, 1.15f)
            for (s in listOf(-1f, 1f)) {
                val punta = q + n * (s * l * 0.5f) + tg * (l * 0.42f) + Offset(0f, l * (0.45f + 0.45f * cae))
                if (s > 0f) { luz += q; luz += punta } else { som += q; som += punta }
            }
        }
        drawPoints(som, PointMode.Lines, oscuro, g * 1.3f, StrokeCap.Round)
        drawPoints(luz, PointMode.Lines, color, g * 1.3f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(c); cubicTo(ctrl, ctrl, fin) }, lerp(color, oscuro, 0.4f), style = Stroke(g, cap = StrokeCap.Round))
    }

    // =================================================================
    //  La selva: el helecho arborescente
    // =================================================================

    /**
     * Un helecho arborescente con el [pie] en el suelo y [alto] unidades hasta la corona: el tronco oscuro y fibroso, más
     * grueso abajo por el manto de raíces, con los muñones de las frondas viejas arriba; la corona de frondas arqueadas
     * en estrella (cada una con sus pinnas, que la hacen de encaje, más claras a contraluz) y los cayados que se
     * desenrollan en el centro.
     */
    fun DrawScope.helechoArborescente(p: Pincel, pie: Offset, alto: Float, az: Azar, lejos: Float = 0f, frondas: Int = 11, contraluz: Float = 0f) {
        val corona = pie + Offset(az.entre(-0.05f, 0.05f) * alto, -alto)
        val g0 = alto * 0.075f; val g1 = alto * 0.04f
        val lado = if (p.solALaDerecha) 1f else -1f
        fun borde(t: Float, s: Float): Offset { val q = pie + (corona - pie) * t; val g = g0 + (g1 - g0) * t.pow(0.5f); return q + Offset(s * g * (1f + 0.06f * sin(t * 23f + s)), 0f) }
        val tronco = poligono((0..12).map { borde(it / 12f, -1f) } + (12 downTo 0).map { borde(it / 12f, 1f) })
        val fibra = lerp(C.MADERA_OSC, C.BREA, 0.35f)
        drawPath(tronco, Brush.horizontalGradient(listOf(p.sombra(fibra, lejos), p.medio(lerp(fibra, C.SELVA, 0.15f), lejos), p.luz(lerp(C.MADERA, C.SELVA, 0.25f), lejos)), pie.x - lado * g0, pie.x + lado * g0))
        clipPath(tronco) {
            // Las fibras de las raíces, en trazos largos que bajan torcidos, más juntas y oscuras abajo.
            val trazos = ArrayList<Offset>(120)
            repeat(26) { val x = az.entre(-1f, 1f); val t0 = az.entre(0f, 0.7f); val t1 = (t0 + az.entre(0.15f, 0.4f)).coerceAtMost(1f)
                trazos += borde(t0, x) ; trazos += borde(t1, x + az.entre(-0.2f, 0.2f)) }
            drawPoints(trazos, PointMode.Lines, p.sombra(C.BREA, lejos).copy(alpha = 0.55f), max(0.35f, g0 * 0.09f), StrokeCap.Round)
            // Las cicatrices de las frondas caídas: escamas sueltas, sin orden de rejilla, cada vez más arriba más marcadas.
            repeat(18) { val t = az.entre(0.35f, 0.98f); val q = borde(t, az.entre(-0.8f, 0.8f)); val w = (g0 + (g1 - g0) * t) * az.entre(0.35f, 0.6f)
                drawPath(Path().apply { moveTo(q + Offset(-w, 0f)); cubicTo(q + Offset(-w * 0.5f, w * 0.7f), q + Offset(w * 0.5f, w * 0.7f), q + Offset(w, 0f)); cubicTo(q + Offset(w * 0.4f, w * 0.3f), q + Offset(-w * 0.4f, w * 0.3f), q + Offset(-w, 0f)); close() },
                    p.sombra(C.BREA, lejos).copy(alpha = 0.5f)) }
            // El musgo que lo cubre del lado húmedo, y los pelos pardos junto a la corona.
            repeat(7) { mancha(borde(az.entre(0.05f, 0.7f), -lado * az.entre(0.3f, 0.9f)), g0 * 0.5f, g0 * 1.2f, p.medio(lerp(C.SELVA, C.HOJA, 0.4f), lejos), 0.35f) }
            drawRect(Brush.verticalGradient(listOf(p.medio(lerp(C.CUERO, C.ARENA, 0.2f), lejos).copy(alpha = 0.6f), Color.Transparent), corona.y, corona.y + alto * 0.12f), Offset(corona.x - g0 * 2f, corona.y), Size(g0 * 4f, alto * 0.12f))
        }
        // Los muñones de las frondas viejas, apuntando abajo bajo la corona.
        repeat(6) { val q = corona + Offset(az.entre(-g1, g1), alto * az.entre(0.02f, 0.1f)); drawLine(p.sombra(lerp(C.CUERO, C.BREA, 0.3f), lejos), q, q + Offset(az.entre(-1f, 1f) * g1 * 2f, alto * az.entre(0.04f, 0.08f)), max(0.4f, g1 * 0.4f), StrokeCap.Round) }
        // Las frondas, las de atrás primero: arqueadas hacia fuera y abajo, en estrella.
        val angulos = (0 until frondas).map { (2 * PI * it / frondas).toFloat() + az.entre(-0.2f, 0.2f) }.sortedBy { sin(it) }
        val brillo = p.l.tono(lerp(C.HOJA_LUZ, C.LATON, 0.12f), lejos)
        for (a in angulos) {
            val hacia = sin(a)                                                  // −1: hacia el fondo; 1: hacia quien mira
            val largo = alto * az.entre(0.5f, 0.68f) * (0.75f + 0.25f * abs(cos(a)))
            val fin = corona + Offset(cos(a) * largo, largo * (0.15f + 0.35f * hacia.coerceAtLeast(0f)) - largo * 0.12f * (1f - abs(cos(a))))
            val ctrl = corona + Offset(cos(a) * largo * 0.45f, -largo * (0.35f - 0.2f * hacia))
            val verde = lerp(C.HOJA, C.SELVA, if (hacia < 0f) 0.62f else 0.22f)
            val tl = if (hacia < 0f) -0.3f else (0.2f + 0.5f * cos(a) * lado).coerceIn(-1f, 1f)
            val cl = contraluz * (if (hacia < 0f) 1f else 0.6f)
            fronda(p, corona, ctrl, fin, largo, lerp(p.por(verde, tl, lejos), brillo, 0.72f * cl), lerp(p.por(verde, tl - 0.6f, lejos), p.l.tono(lerp(C.HOJA, C.SELVA, 0.25f), lejos), 0.6f * cl), az, lejos)
        }
        // Los cayados: espirales tiernas en el centro.
        for (k in 0..2) { val q = corona + Offset((k - 1) * alto * 0.03f, -alto * 0.04f); val r = alto * 0.02f; drawArc(p.por(lerp(C.HOJA, C.ARENA, 0.3f), 0.5f, lejos), 0f, 300f, false, q - Offset(r, r), Size(r * 2f, r * 2f), style = Stroke(max(0.4f, alto * 0.008f))); drawLine(p.medio(C.HOJA, lejos), q + Offset(r, 0f), q + Offset(r * 0.6f, alto * 0.05f), max(0.4f, alto * 0.008f)) }
    }

    /** Una fronda de helecho, de [a] por [ctrl] hasta [b]: el raquis y las pinnas, que se acortan hacia la punta, cada una dentada. */
    private fun DrawScope.fronda(p: Pincel, a: Offset, ctrl: Offset, b: Offset, largo: Float, color: Color, oscuro: Color, az: Azar, lejos: Float) {
        fun en(t: Float): Offset { val m = 1f - t; return a * (m * m) + ctrl * (2f * m * t) + b * (t * t) }
        val g = max(0.3f, largo * 0.012f)
        // Las pinnas, en tres tamaños (cada una un trazo que se afina a la punta), las del lado de la luz y las del otro.
        val claras = Array(3) { ArrayList<Offset>(40) }; val sombras = Array(3) { ArrayList<Offset>(40) }
        for (k in 3..28) {
            val t = k / 28f; val q = en(t); val dd = en((t + 0.03f).coerceAtMost(1f)) - en(t - 0.03f); val tg = dd / dd.getDistance().coerceAtLeast(0.01f); val n = Offset(-tg.y, tg.x)
            val l = largo * 0.16f * sin(PI.toFloat() * (0.15f + 0.85f * t)).coerceAtLeast(0.15f) * az.entre(0.85f, 1.1f)
            val tam = if (l > largo * 0.11f) 0 else if (l > largo * 0.06f) 1 else 2
            for (s in listOf(-1f, 1f)) {
                val punta = q + n * (s * l) + tg * (l * 0.25f) + Offset(0f, l * 0.25f)
                (if (s > 0f) claras[tam] else sombras[tam]).apply { add(q); add(punta) }
            }
        }
        for (tam in 0..2) { val w = max(0.4f, largo * (0.036f - 0.01f * tam)); drawPoints(sombras[tam], PointMode.Lines, oscuro, w, StrokeCap.Round); drawPoints(claras[tam], PointMode.Lines, color, w, StrokeCap.Round) }
        drawPath(Path().apply { moveTo(a); cubicTo(ctrl, ctrl, b) }, oscuro, style = Stroke(g, cap = StrokeCap.Round))
    }

    // =================================================================
    //  La selva: la liana
    // =================================================================

    /**
     * Una liana que pasa por [puntos] (en el lienzo), de [grueso] unidades: leñosa y retorcida, colgando con su peso,
     * con el filo de luz, las vueltas de la torsión, algún zarcillo y raíces aéreas que caen rectas; con [hojas],
     * racimos de hojas acorazonadas.
     */
    fun DrawScope.liana(p: Pincel, puntos: List<Offset>, grueso: Float, az: Azar, lejos: Float = 0f, hojas: Boolean = true) {
        val camino = curva(puntos)
        val corteza = lerp(C.MADERA_OSC, C.SELVA, 0.3f)
        drawPath(camino, p.sombra(C.BREA, lejos).copy(alpha = 0.35f), style = Stroke(grueso * 1.3f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        drawPath(camino, p.sombra(corteza, lejos), style = Stroke(grueso, cap = StrokeCap.Round))
        drawPath(Path().apply { addPath(camino, Offset(if (p.solALaDerecha) grueso * 0.2f else -grueso * 0.2f, -grueso * 0.15f)) }, p.luz(lerp(corteza, C.ARENA, 0.3f), lejos).copy(alpha = 0.65f), style = Stroke(grueso * 0.32f, cap = StrokeCap.Round))
        drawPath(camino, p.medio(lerp(corteza, C.BREA, 0.3f), lejos).copy(alpha = 0.7f), style = Stroke(grueso * 0.85f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(grueso * 0.3f, grueso * 1.2f))))
        // Los zarcillos y las raíces aéreas, de algunos puntos de la liana.
        for ((i, q) in puntos.withIndex()) {
            if (i == 0 || i == puntos.size - 1) continue
            if (az.real() < 0.5f) { val l = grueso * az.entre(8f, 22f); drawLine(p.sombra(corteza, lejos).copy(alpha = 0.8f), q, q + Offset(az.entre(-1.5f, 1.5f), l), max(0.3f, grueso * 0.15f)) }
            if (hojas && az.real() < 0.7f) for (k in 0..3) {
                val c = q + Offset(az.entre(-1f, 1f) * grueso * 3f, az.entre(-1f, 2f) * grueso * 2f); val r = grueso * az.entre(1.2f, 2f); val a = az.entre(-0.8f, 0.8f) + PI.toFloat() / 2f
                val tipo = az.real() > 0.5f
                drawPath(Path().apply { moveTo(c); cubicTo(c + Offset(-r, r * 0.2f), c + Offset(cos(a) * r * 0.8f - r * 0.5f, sin(a) * r * 1.6f), c + Offset(cos(a) * r * 1.6f, sin(a) * r * 1.6f)); cubicTo(c + Offset(cos(a) * r * 0.8f + r * 0.5f, sin(a) * r * 1.6f), c + Offset(r, r * 0.2f), c); close() }, p.por(lerp(C.HOJA, C.SELVA, if (tipo) 0.3f else 0.6f), if (tipo) 0.3f else -0.3f, lejos))
            }
        }
    }

    // =================================================================
    //  La selva: el naranjo
    // =================================================================

    /**
     * Un naranjo con el [pie] en el suelo, de [alto] unidades y [ancho] de copa: el tronco corto y retorcido con sus
     * ramas, la copa en masas (honda dentro, nunca una bola ni un agujero negro), las hojas lustrosas en racimos que
     * siguen las ramas, con las claras solo en el fleco del lado del sol, y las naranjas, unas al sol y otras en la
     * sombra; con [caidas], alguna en el suelo.
     */
    fun DrawScope.naranjo(p: Pincel, pie: Offset, alto: Float, ancho: Float, az: Azar, lejos: Float = 0f, frutos: Int = 14, caidas: Boolean = true) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val cruz = pie + Offset(az.entre(-0.05f, 0.05f) * ancho, -alto * 0.32f)
        val corteza = lerp(C.ROCA, C.MADERA_OSC, 0.45f)
        drawPath(poligono(pie + Offset(-alto * 0.045f, 0f), pie + Offset(alto * 0.045f, 0f), cruz + Offset(alto * 0.028f, 0f), cruz + Offset(-alto * 0.028f, 0f)), Brush.horizontalGradient(listOf(p.sombra(corteza, lejos), p.luz(corteza, lejos)), pie.x - lado * alto * 0.05f, pie.x + lado * alto * 0.05f))
        // Las masas de la copa: siete u ocho, de borde irregular, en una cúpula algo achatada; las de abajo, más hondas.
        val centro = pie + Offset(0f, -alto * 0.62f)
        class Masa(val c: Offset, val rx: Float, val ry: Float, val alta: Float)
        val masas = (0 until 8).map { i -> val a = (2 * PI * i / 8).toFloat() + az.entre(-0.3f, 0.3f); val r = az.entre(0.45f, 0.75f)
            val c = centro + Offset(cos(a) * ancho * 0.36f * r, sin(a) * alto * 0.26f * r); Masa(c, ancho * az.entre(0.2f, 0.3f), alto * az.entre(0.16f, 0.24f), (-sin(a) * 0.5f + 0.5f)) } + listOf(Masa(centro + Offset(0f, -alto * 0.08f), ancho * 0.3f, alto * 0.24f, 0.8f))
        // Las ramas, que asoman entre las masas.
        for (ms in masas.take(8)) drawLine(p.sombra(corteza, lejos), cruz, ms.c + Offset(0f, ms.ry * 0.3f), max(0.4f, alto * 0.02f), StrokeCap.Round)
        val hondo = p.sombra(lerp(C.SELVA, C.SELVA_OSC, 0.5f), lejos); val oscuro = p.sombra(lerp(C.SELVA, C.ESCARABAJO, 0.2f), lejos); val cuerpo = p.medio(lerp(C.SELVA, C.HOJA, 0.15f), lejos)
        val claro = p.luz(lerp(C.SELVA, C.HOJA, 0.55f), lejos); val brillo = p.luz(lerp(C.HOJA, C.ARENA, 0.4f), lejos)
        val hoja = max(0.6f, alto * 0.034f)
        for (ms in masas.sortedBy { it.alta }) {
            val cuantas = (ms.rx * ms.ry / (hoja * hoja) * 3.1f).toInt().coerceIn(24, 240)
            // El fondo de la masa: hondo, de borde quebrado, con el lado de luz ya algo más claro.
            val borde = (0 until 12).map { j -> val a = (2 * PI * j / 12).toFloat(); val k = az.entre(0.82f, 1.1f); ms.c + Offset(cos(a) * ms.rx * k, sin(a) * ms.ry * k) }
            drawPath(curva(borde + borde.first()).apply { close() }, Brush.linearGradient(listOf(hondo, oscuro), ms.c + Offset(-lado * ms.rx, ms.ry), ms.c + Offset(lado * ms.rx, -ms.ry)))
            // Las hojas: pequeñas, en racimos que salen del centro de la masa hacia fuera; las del lado del sol y de
            // arriba, claras; las de abajo, oscuras; el brillo del charol solo en el fleco del sol.
            val porColor = arrayOf(ArrayList<Offset>(), ArrayList<Offset>(), ArrayList<Offset>(), ArrayList<Offset>())
            for (k in 0 until cuantas) {
                val a = az.entre(0f, 6.28f); val r = sqrt(az.real()) * 1.02f
                val q = ms.c + Offset(cos(a) * ms.rx * r, sin(a) * ms.ry * r)
                val sol = (cos(a) * lado * 0.6f - sin(a) * 0.6f) * r + (ms.alta - 0.5f) * 0.6f
                val i = when { sol > 0.55f && r > 0.7f -> 3; sol > 0.15f -> 2; sol > -0.35f -> 1; else -> 0 }
                val g = a + az.entre(-0.7f, 0.7f); val d = Offset(cos(g), sin(g)) * (hoja * az.entre(0.8f, 1.2f))
                porColor[i] += q - d; porColor[i] += q + d
            }
            for ((i, col) in listOf(oscuro, cuerpo, claro, brillo).withIndex()) drawPoints(porColor[i], PointMode.Lines, col, hoja * 0.75f, StrokeCap.Round)
        }
        // Las naranjas: con su luz, su sombra y un brillo; las de dentro, más apagadas, y alguna hoja delante.
        repeat(frutos) {
            val ms = masas[az.entero(masas.size)]; val a = az.entre(0f, 6.28f); val r = az.entre(0.35f, 0.95f)
            val q = ms.c + Offset(cos(a) * ms.rx * r, sin(a) * ms.ry * r * 0.85f); val rr = max(0.8f, alto * 0.03f)
            val alSol = cos(a) * lado > -0.1f && r > 0.55f
            drawCircle(Brush.radialGradient(listOf(p.luz(lerp(C.NARANJA, C.ARENA, 0.25f), lejos), p.medio(C.NARANJA, lejos), p.sombra(C.NARANJA, lejos)), q + Offset(lado * rr * 0.35f, -rr * 0.35f), rr * 1.4f), rr, q)
            if (!alSol) drawCircle(hondo.copy(alpha = 0.3f), rr, q)
            if (alSol && rr > 1.4f) drawCircle(p.luz(Color.White, lejos).copy(alpha = 0.5f), rr * 0.25f, q + Offset(lado * rr * 0.35f, -rr * 0.35f))
            if (rr > 1.6f && az.real() > 0.6f) { val c0 = q + Offset(-lado * rr * 0.8f, -rr * 0.9f); drawLine(cuerpo, c0, c0 + Offset(lado * rr * 0.9f, -rr * 0.4f), hoja * 0.75f, StrokeCap.Round) }
        }
        if (caidas) repeat(3) { val q = pie + Offset(az.entre(-ancho * 0.4f, ancho * 0.4f), az.entre(-1f, 2f)); val rr = max(0.7f, alto * 0.03f); drawCircle(p.medio(lerp(C.NARANJA, C.CUERO, 0.3f), lejos), rr, q + Offset(0f, -rr)) }
    }

    // =================================================================
    //  La selva: la heliconia
    // =================================================================

    /**
     * Una mata de heliconias con el [pie] en el suelo y [alto] unidades: tallos con sus hojas de remo (como las del
     * plátano, más chicas y tiesas) y las flores de brácteas en zigzag, rojas con la punta verde amarilla: erguidas o,
     * con [cuelga], colgando. [contraluz]: las hojas con el sol detrás se encienden.
     */
    fun DrawScope.heliconia(p: Pincel, pie: Offset, alto: Float, az: Azar, lejos: Float = 0f, contraluz: Float = 0f, flores: Int = 3, cuelga: Boolean = false) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val tallos = 4 + az.entero(3)
        val angulos = (0 until tallos).map { i -> -PI.toFloat() / 2f + (i - (tallos - 1) / 2f) * 0.3f + az.entre(-0.15f, 0.15f) }.sortedBy { abs(it + PI.toFloat() / 2f) }.reversed()
        val puntas = ArrayList<Offset>()
        for ((i, a) in angulos.withIndex()) {
            val largo = alto * az.entre(0.7f, 1f)
            val base = pie + Offset((az.real() - 0.5f) * alto * 0.12f, 0f)
            val codo = base + Offset(cos(a) * largo * 0.42f, sin(a) * largo * 0.48f)
            drawLine(p.medio(lerp(C.HOJA, C.SELVA, 0.35f), lejos), base, codo, max(0.4f, alto * 0.016f))
            val b = a + az.entre(-0.35f, 0.35f)
            val punta = codo + Offset(cos(b) * largo * 0.6f, sin(b) * largo * 0.42f + largo * 0.12f)
            hojaDePlatano(p, codo, punta, largo * 0.22f, largo * az.entre(-0.07f, 0.07f), az.entre(-0.5f, 0.5f), az.entre(0.02f, 0.25f), if (i == 0) 0.4f else 0.05f, (0.2f + 0.4f * cos(a) * lado).coerceIn(-1f, 1f), az, lejos, contraluz)
            puntas += codo
        }
        // Las flores, que salen entre las hojas, a distintas alturas.
        repeat(flores) { i ->
            val a = pie + Offset((i - (flores - 1) / 2f) * alto * 0.18f + az.entre(-0.05f, 0.05f) * alto, -alto * az.entre(0.35f, 0.6f))
            val tallo = pie + Offset((a.x - pie.x) * 0.3f, 0f)
            drawLine(p.medio(lerp(C.HOJA, C.SELVA, 0.3f), lejos), tallo, a, max(0.4f, alto * 0.012f))
            val b = if (cuelga) a + Offset(az.entre(-0.06f, 0.06f) * alto, alto * az.entre(0.28f, 0.38f)) else a + Offset(az.entre(-0.08f, 0.08f) * alto, -alto * az.entre(0.22f, 0.32f))
            bracteas(p, a, b, alto * 0.075f, az, lejos)
        }
    }

    /** Una espiga de heliconia de [a] a [b]: el raquis en zigzag y las brácteas en barquito, alternas, rojas con la punta verde amarilla. */
    private fun DrawScope.bracteas(p: Pincel, a: Offset, b: Offset, tam: Float, az: Azar, lejos: Float) {
        val n = 6 + az.entero(4)
        val d = b - a; val l = d.getDistance().coerceAtLeast(0.01f); val u = d / l; val nn = Offset(-u.y, u.x)
        val lado = if (p.solALaDerecha) 1f else -1f
        val rojo = lerp(C.TEJA, C.NARANJA, 0.18f)
        drawLine(p.medio(lerp(C.TEJA, C.HOJA, 0.4f), lejos), a, b, max(0.4f, tam * 0.12f))
        for (i in 0 until n) {
            val t = (i + 0.5f) / n; val q = a + d * t; val s = if (i % 2 == 0) 1f else -1f
            val w = tam * (1f - 0.5f * t)
            val atras = q - u * (l / n * 0.5f)
            val punta = q + nn * (s * w * 1.1f) + u * (w * 0.55f)
            val cuerpo = Path().apply { moveTo(atras); cubicTo(atras + nn * (s * w * 0.9f), punta - u * (w * 0.6f), punta); cubicTo(punta - nn * (s * w * 0.3f), q + u * (l / n * 0.2f), q + u * (l / n * 0.45f)); close() }
            drawPath(cuerpo, p.por(rojo, if (s * lado > 0f) 0.45f else -0.25f, lejos))
            drawLine(p.luz(lerp(C.LATON, C.HOJA, 0.35f), lejos), punta - (punta - q) * 0.22f, punta, max(0.4f, w * 0.22f), StrokeCap.Round)
            drawLine(p.luz(lerp(rojo, C.ARENA, 0.3f), lejos).copy(alpha = 0.6f), atras + nn * (s * w * 0.15f), punta - (punta - q) * 0.3f, max(0.3f, w * 0.08f))
        }
    }

    // =================================================================
    //  La selva: la hoja de filodendro
    // =================================================================

    /**
     * Una hoja de filodendro (el guaimbê de la selva de Río) del pecíolo, en [base], a la [punta]: ancha, en corazón,
     * partida en lóbulos hondos de punta roma y borde ondulado, con los calados junto al nervio por donde se ve lo de
     * detrás; lustrosa: el haz verde hondo con el brillo del charol a lo largo de cada lóbulo y el nervio claro, grueso
     * abajo, con sus venas a cada lóbulo. [t]: la luz que le da; [contraluz]: el sol por detrás. [escorzo], de 0 a 1:
     * cuánto se ve de frente; [canto]: la hoja vuelta, que enseña el envés, más pálido y mate, con las venas en relieve.
     */
    fun DrawScope.filodendro(p: Pincel, base: Offset, punta: Offset, ancho: Float, t: Float, az: Azar, lejos: Float = 0f, contraluz: Float = 0f, lobulos: Int = 7, escorzo: Float = 1f, canto: Boolean = false) {
        val d = punta - base; val largo = d.getDistance().coerceAtLeast(0.01f); val u = d / largo; val n0 = Offset(-u.y, u.x)
        val n = n0 * escorzo.coerceIn(0.25f, 1f)
        val lado = if (p.solALaDerecha) 1f else -1f
        val verde = if (canto) lerp(lerp(C.HOJA, C.HOJA_LUZ, 0.25f), C.ROCA, 0.15f) else lerp(C.SELVA, C.HOJA, 0.32f + 0.15f * az.real())
        val translucida = p.l.tono(lerp(C.HOJA_LUZ, C.LATON, 0.15f), lejos)
        fun color(tl: Float): Color = lerp(p.por(verde, tl.coerceIn(-1f, 1f), lejos), translucida, 0.7f * contraluz)
        // El nervio, algo arqueado; el corazón (la lámina sin partir) a su alrededor.
        fun nervio(s: Float): Offset = base + d * s + n0 * (largo * 0.05f * sin(PI.toFloat() * s))
        fun nucleo(s: Float): Float = ancho * 0.2f * sin(PI.toFloat() * (0.08f + 0.88f * s.coerceIn(0f, 1f))).pow(0.6f)
        val lobs = ArrayList<Pair<Path, Float>>(); val venas = ArrayList<Offset>(); val brillos = ArrayList<Offset>()
        for (l in listOf(-1f, 1f)) {
            val tl = t + l * 0.25f * escorzo * lado
            for (i in 0 until lobulos) {
                val s = (i + 0.5f) / lobulos
                val q = nervio(0.92f * s) + n * (l * nucleo(s) * 0.8f)
                // Los de abajo apuntan atrás (las orejas del corazón), los de en medio de lado, los de arriba hacia la punta.
                val ang = -0.95f + 1.75f * s + az.entre(-0.08f, 0.08f)
                val dir0 = n * (l * cos(ang)) + u * sin(ang); val dl = dir0.getDistance().coerceAtLeast(0.001f); val dir = dir0 / dl
                val len = ancho * 0.36f * sin(PI.toFloat() * (0.16f + 0.74f * s)).pow(0.55f) * az.entre(0.88f, 1.06f) * (0.5f + 0.5f * dl)
                val w = len * 0.33f + ancho * 0.03f
                val tip = q + dir * len + Offset(0f, len * 0.06f)
                val perp = Offset(-dir.y, dir.x)
                // Un lóbulo de punta roma y borde ondulado (dos ondas por lado), ancho en su arranque.
                val lob = Path().apply {
                    moveTo(q - u * (w * 0.9f))
                    cubicTo(q + dir * (len * 0.3f) - perp * (w * 1.1f), q + dir * (len * 0.62f) - perp * (w * 0.75f), q + dir * (len * 0.8f) - perp * (w * 0.62f))
                    cubicTo(q + dir * (len * 0.95f) - perp * (w * 0.55f), tip - perp * (w * 0.2f), tip)
                    cubicTo(tip + perp * (w * 0.22f), q + dir * (len * 0.95f) + perp * (w * 0.5f), q + dir * (len * 0.8f) + perp * (w * 0.55f))
                    cubicTo(q + dir * (len * 0.6f) + perp * (w * 0.72f), q + dir * (len * 0.28f) + perp * (w * 1.0f), q + u * (w * 0.9f)); close()
                }
                lobs += lob to (tl - 0.14f * (i % 2))
                venas += q; venas += tip - dir * (len * 0.1f)
                if (!canto) { brillos += q + dir * (len * 0.22f) + perp * (w * 0.32f * lado); brillos += q + dir * (len * 0.7f) + perp * (w * 0.2f * lado) }
            }
        }
        for ((lob, tl) in lobs) {
            drawPath(lob, color(tl))
            // El borde del lóbulo, un poco más oscuro (la lámina se curva hacia abajo).
            drawPath(lob, p.sombra(verde, lejos).copy(alpha = 0.22f), style = Stroke(max(0.4f, ancho * 0.012f)))
        }
        // El corazón, encima del arranque de los lóbulos.
        val izq = (0..10).map { i -> val s = i / 10f; nervio(0.94f * s) - n * nucleo(s) }; val der = (0..10).map { i -> val s = i / 10f; nervio(0.94f * s) + n * nucleo(s) }
        drawPath(poligono(izq + listOf(punta) + der.reversed()), color(t))
        // Los calados: rajas junto al nervio, entre lóbulo y lóbulo, por donde se ve lo oscuro de detrás.
        if (!canto) for (i in 2 until lobulos - 1 step 2) for (l in listOf(-1f, 1f)) {
            val s = i / lobulos.toFloat(); val c = nervio(0.92f * s) + n * (l * nucleo(s) * 0.62f)
            val ang = -0.95f + 1.75f * s; val dd = (n * (l * cos(ang)) + u * sin(ang)).let { it / it.getDistance().coerceAtLeast(0.001f) }; val pp = Offset(-dd.y, dd.x)
            val r = ancho * 0.05f; val w = ancho * 0.012f
            drawPath(Path().apply { moveTo(c - dd * r); cubicTo(c - dd * (r * 0.4f) + pp * w, c + dd * (r * 0.4f) + pp * w, c + dd * r); cubicTo(c + dd * (r * 0.4f) - pp * w, c - dd * (r * 0.4f) - pp * w, c - dd * r); close() },
                p.sombra(lerp(C.SELVA_OSC, C.BREA, 0.3f), lejos).copy(alpha = 0.85f)) }
        // Las venas, del nervio a cada lóbulo: claras en el haz, en relieve (luz y sombra) en el envés.
        val wv = max(0.35f, ancho * 0.014f)
        if (canto) drawPoints(venas.map { it + Offset(0.6f, 0.6f) }, PointMode.Lines, p.sombra(verde, lejos).copy(alpha = 0.5f), wv * 1.4f, StrokeCap.Round)
        drawPoints(venas, PointMode.Lines, p.luz(lerp(verde, C.HOJA_LUZ, 0.55f), lejos).copy(alpha = if (canto) 0.85f else 0.55f), wv, StrokeCap.Round)
        // El brillo del charol a lo largo de los lóbulos del lado de la luz.
        if (contraluz < 0.5f && brillos.isNotEmpty()) drawPoints(brillos, PointMode.Lines, p.luz(Color.White, lejos).copy(alpha = (0.18f + 0.3f * p.r.dia) * (1f - contraluz)), max(0.5f, ancho * 0.03f), StrokeCap.Round)
        // El nervio central, claro y grueso, más fino hacia la punta, con su filo de sombra.
        val nerv = (0..8).map { nervio(0.9f * it / 8f) }
        drawPath(Path().apply { moveTo(nerv[0]); for (q in nerv.drop(1)) lineTo(q) }, p.sombra(verde, lejos).copy(alpha = 0.5f), style = Stroke(max(0.6f, ancho * 0.042f), cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(nerv[0]); for (q in nerv.drop(1).dropLast(2)) lineTo(q) }, p.luz(lerp(verde, C.HOJA_LUZ, 0.5f), lejos), style = Stroke(max(0.5f, ancho * 0.028f), cap = StrokeCap.Round))
    }

    // =================================================================
    //  La selva: la bromelia
    // =================================================================

    /**
     * Una bromelia con el [pie] (el centro de la roseta) en [pie] y [ancho] unidades: la roseta de hojas tiesas y
     * acanaladas, algunas rayadas o teñidas de rojo, que se abren en copa; con [flor], la vara de brácteas rojas que sale
     * del centro (o el corazón encendido). Va en el suelo, en una maceta o atada a una rama.
     */
    fun DrawScope.bromelia(p: Pincel, pie: Offset, ancho: Float, az: Azar, lejos: Float = 0f, flor: Boolean = true, contraluz: Float = 0f) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val n = 11 + az.entero(5)
        val tinte = az.real()
        val angulos = (0 until n).map { -PI.toFloat() / 2f + (it - (n - 1) / 2f) * (3.1f / n) + az.entre(-0.08f, 0.08f) }.sortedBy { abs(it + PI.toFloat() / 2f) }
        val traseras = angulos.take(n / 2); val delanteras = angulos.drop(n / 2).reversed()
        fun hoja(a: Float, delante: Boolean) {
            val largo = ancho * az.entre(0.45f, 0.6f) * (if (delante) 0.8f else 1f)
            val cae = abs(a + PI.toFloat() / 2f) * 0.35f
            val fin = pie + Offset(cos(a) * largo, sin(a) * largo * 0.8f + largo * cae + (if (delante) largo * 0.15f else 0f))
            val ctrl = pie + Offset(cos(a) * largo * 0.4f, sin(a) * largo * 0.6f)
            val w = ancho * 0.07f
            val dd = fin - pie; val nn = Offset(-dd.y, dd.x) / dd.getDistance().coerceAtLeast(0.01f)
            val forma = Path().apply { moveTo(pie - nn * w); cubicTo(ctrl - nn * (w * 0.9f), fin - dd * 0.15f - nn * (w * 0.3f), fin); cubicTo(fin - dd * 0.15f + nn * (w * 0.3f), ctrl + nn * (w * 0.9f), pie + nn * w); close() }
            val verde = if (az.real() < 0.3f + 0.4f * tinte) lerp(C.HOJA, C.TEJA, 0.35f) else lerp(C.SELVA, C.HOJA, 0.45f)
            val tl = (cos(a) * lado * 0.6f + if (delante) 0.1f else -0.3f).coerceIn(-1f, 1f)
            drawPath(forma, lerp(p.por(verde, tl, lejos), p.l.tono(lerp(C.HOJA_LUZ, C.TEJA, 0.15f), lejos), 0.6f * contraluz))
            drawPath(Path().apply { moveTo(pie); cubicTo(ctrl, fin - dd * 0.15f, fin) }, p.luz(lerp(verde, C.ARENA, 0.4f), lejos).copy(alpha = 0.5f), style = Stroke(max(0.3f, w * 0.25f)))
            // Las rayas de través de las hojas pintadas.
            if (tinte > 0.6f) for (j in 1..4) { val q = pie + (fin - pie) * (j / 5f); drawLine(p.medio(lerp(C.LONA, C.HOJA, 0.4f), lejos).copy(alpha = 0.4f), q - nn * (w * 0.6f), q + nn * (w * 0.6f), max(0.3f, w * 0.18f)) }
        }
        for (a in traseras) hoja(a, false)
        if (flor) {
            if (tinte < 0.45f) {
                // La vara con sus brácteas rosadas y las florecillas azules (como la Aechmea).
                val alto = ancho * 0.75f; val tope = pie + Offset(az.entre(-0.05f, 0.05f) * ancho, -alto)
                drawLine(p.medio(lerp(C.TEJA, C.LONA, 0.3f), lejos), pie, tope, max(0.4f, ancho * 0.035f))
                for (j in 0..9) { val q = pie + (tope - pie) * (0.45f + j * 0.06f); val s = if (j % 2 == 0) 1f else -1f
                    drawPath(poligono(q, q + Offset(s * ancho * 0.08f * (1f - j * 0.06f), -ancho * 0.05f), q + Offset(0f, -ancho * 0.06f)), p.por(lerp(C.TEJA, C.LONA, 0.35f), s * lado * 0.4f, lejos))
                    if (j % 3 == 1) drawCircle(p.medio(lerp(C.SOMBRA, C.MAR, 0.3f), lejos), max(0.4f, ancho * 0.015f), q + Offset(s * ancho * 0.07f, -ancho * 0.05f)) }
            } else {
                // El corazón de la roseta, rojo encendido.
                drawOval(p.por(lerp(C.TEJA, C.NARANJA, 0.1f), 0.3f, lejos), Offset(pie.x - ancho * 0.16f, pie.y - ancho * 0.1f), Size(ancho * 0.32f, ancho * 0.16f))
            }
        }
        for (a in delanteras) hoja(a, true)
    }

    // =================================================================
    //  La selva: la orquídea en su cesto colgado
    // =================================================================

    /**
     * Una orquídea en un cesto de palos colgado del [gancho] por tres cordeles; el cesto queda [cuelga] unidades más
     * abajo y mide [tam]: los palos cruzados, el musgo y la fibra que asoman, las raíces grises que caen por fuera, las
     * hojas gruesas y, con [flores], las flores lilas de labelo encendido (como las catleyas de la selva de Río).
     */
    fun DrawScope.orquideaEnCesto(p: Pincel, gancho: Offset, cuelga: Float, tam: Float, az: Azar, lejos: Float = 0f, flores: Int = 3, contraluz: Float = 0f) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val c = gancho + Offset(0f, cuelga)
        val w = tam * 0.5f; val h = tam * 0.42f
        // Los cordeles, del gancho a las esquinas del cesto.
        for (dx in listOf(-w, -w * 0.2f, w)) drawLine(p.sombra(C.CANAMO, lejos), gancho, c + Offset(dx, -h * 0.5f + (if (dx > -w * 0.5f && dx < w * 0.5f) -h * 0.15f else 0f)), max(0.3f, tam * 0.012f))
        // Las raíces que salen por debajo, grises con la punta verde.
        repeat(7) { val a = c + Offset(az.entre(-w, w) * 0.8f, h * 0.45f); val b = a + Offset(az.entre(-0.3f, 0.3f) * tam, tam * az.entre(0.3f, 0.8f))
            drawPath(Path().apply { moveTo(a); cubicTo(a + Offset(0f, (b.y - a.y) * 0.4f), b + Offset((a.x - b.x) * 0.5f, -(b.y - a.y) * 0.2f), b) }, p.medio(lerp(C.LONA, C.ROCA, 0.4f), lejos), style = Stroke(max(0.3f, tam * 0.025f), cap = StrokeCap.Round))
            drawCircle(p.medio(C.HOJA, lejos), max(0.3f, tam * 0.015f), b) }
        // Las hojas: gruesas, arqueadas, oscuras y con su brillo.
        repeat(5) { i -> val a = -PI.toFloat() / 2f + (i - 2) * 0.45f + az.entre(-0.15f, 0.15f); val largo = tam * az.entre(0.7f, 1.1f)
            val base = c + Offset((i - 2) * w * 0.25f, -h * 0.3f); val fin = base + Offset(cos(a) * largo, sin(a) * largo * 0.7f + largo * 0.3f * abs(cos(a)))
            val ctrl = base + Offset(cos(a) * largo * 0.4f, sin(a) * largo * 0.8f); val dd = fin - base; val nn = Offset(-dd.y, dd.x) / dd.getDistance().coerceAtLeast(0.01f); val g = tam * 0.06f
            drawPath(Path().apply { moveTo(base - nn * g); cubicTo(ctrl - nn * g, fin - dd * 0.1f - nn * (g * 0.4f), fin); cubicTo(fin - dd * 0.1f + nn * (g * 0.4f), ctrl + nn * g, base + nn * g); close() },
                lerp(p.por(lerp(C.SELVA, C.HOJA, 0.3f), cos(a) * lado * 0.5f, lejos), p.l.tono(C.HOJA_LUZ, lejos), 0.55f * contraluz)) }
        // El cesto: palos cruzados en pisos, con la fibra y el musgo asomando entre ellos.
        val musgo = p.medio(lerp(C.SELVA, C.HOJA, 0.4f), lejos)
        drawRect(musgo, Offset(c.x - w * 0.9f, c.y - h * 0.5f), Size(w * 1.8f, h))
        for (k in 0..3) { val y = c.y - h * 0.5f + h * k / 3.2f; val ww = w * (1f - 0.04f * k)
            drawLine(p.por(lerp(C.MADERA, C.CANAMO, 0.3f), if (k % 2 == 0) 0.3f else -0.2f, lejos), Offset(c.x - ww, y), Offset(c.x + ww, y + 0.3f), max(0.5f, tam * 0.07f), StrokeCap.Round)
            drawCircle(p.sombra(C.MADERA_OSC, lejos), max(0.3f, tam * 0.035f), Offset(c.x - ww, y)); drawCircle(p.sombra(C.MADERA_OSC, lejos), max(0.3f, tam * 0.035f), Offset(c.x + ww, y + 0.3f)) }
        repeat(6) { mancha(c + Offset(az.entre(-w, w), az.entre(-h, h) * 0.5f), tam * 0.08f, tam * 0.05f, musgo, 0.6f) }
        // Las flores: tres pétalos y dos sépalos lilas, y el labelo rizado, más oscuro, con la garganta amarilla.
        val lila = lerp(C.SOMBRA, C.LONA, 0.38f); val labelo = lerp(C.TEJA, C.SOMBRA, 0.5f)
        repeat(flores) { i ->
            val f = c + Offset((i - (flores - 1) / 2f) * w * 0.9f + az.entre(-0.1f, 0.1f) * w, -h * 0.6f - tam * az.entre(0.25f, 0.55f)); val r = tam * 0.2f
            drawLine(p.medio(C.HOJA, lejos), c + Offset(0f, -h * 0.4f), f, max(0.3f, tam * 0.02f))
            for (j in 0 until 5) { val a = -PI.toFloat() / 2f + j * 2f * PI.toFloat() / 5f; val q = f + Offset(cos(a), sin(a)) * (r * 0.6f)
                drawOval(p.por(lila, (cos(a) * lado * 0.5f + 0.2f), lejos), Offset(q.x - r * 0.4f, q.y - r * 0.28f), Size(r * 0.8f, r * 0.56f)) }
            drawOval(p.por(labelo, 0.1f, lejos), Offset(f.x - r * 0.42f, f.y - r * 0.05f), Size(r * 0.84f, r * 0.62f))
            drawCircle(p.luz(C.LATON, lejos), r * 0.14f, f + Offset(0f, r * 0.08f))
        }
    }

    // =================================================================
    //  La selva: el árbol de raíces tabulares
    // =================================================================

    /**
     * Un árbol enorme de la selva, con el [pie] en el suelo, [ancho] unidades de tronco y [alto] hasta donde se sale del
     * cuadro: el tronco claro y recto, con su lado de luz, manchas de liquen, regueros y musgo del lado de la sombra; al
     * pie, las raíces tabulares que se abren como paredes, cada una con su cara de luz y su cara de sombra; arriba, las
     * primeras ramas. Devuelve las horquillas (donde van las bromelias y las orquídeas, y de donde cuelgan las lianas).
     */
    fun DrawScope.arbolDeRaices(p: Pincel, pie: Offset, ancho: Float, alto: Float, az: Azar, lejos: Float = 0f): List<Offset> {
        val lado = if (p.solALaDerecha) 1f else -1f
        val inclina = az.entre(-0.04f, 0.04f) * alto
        fun eje(t: Float): Offset = pie + Offset(inclina * t * t, -alto * t)
        fun g(t: Float): Float = ancho * 0.5f * (1f - 0.2f * t) * (1f + 0.45f * (1f - t * 7f).coerceAtLeast(0f).pow(2f))
        val corteza = lerp(lerp(C.ROCA, C.MADERA_OSC, 0.42f), C.CUERO, 0.12f)
        // Las raíces de atrás: solo asoma su lomo por encima del suelo.
        val raices = listOf(-1.25f to -0.18f, 1.2f to -0.15f, -0.85f to 0.32f, 0.7f to 0.42f, -0.15f to 0.55f).map { (dx, dz) -> Triple(dx + az.entre(-0.1f, 0.1f), dz, az.entre(0.8f, 1.2f)) }
        fun raiz(dx: Float, dz: Float, k: Float) {
            val alto0 = ancho * 0.95f * k
            val punta = pie + Offset(dx * ancho * 0.95f * k, dz * ancho * 0.35f)
            val raizEnTronco = pie + Offset(dx.coerceIn(-0.5f, 0.5f) * ancho * 0.6f, -alto0)
            val forma = Path().apply { moveTo(raizEnTronco); cubicTo(raizEnTronco + Offset(dx * ancho * 0.2f, alto0 * 0.6f), punta + Offset(-dx * ancho * 0.6f, -ancho * 0.25f), punta); lineTo(pie + Offset(dx.coerceIn(-0.4f, 0.4f) * ancho * 0.6f, dz * ancho * 0.2f)); close() }
            val cara = (dx * lado).coerceIn(-1f, 1f)
            drawPath(forma, p.por(corteza, cara * 0.6f - 0.1f, lejos))
            clipPath(forma) {
                drawPath(Path().apply { moveTo(raizEnTronco); cubicTo(raizEnTronco + Offset(dx * ancho * 0.2f, alto0 * 0.6f), punta + Offset(-dx * ancho * 0.6f, -ancho * 0.25f), punta) }, p.luz(corteza, lejos).copy(alpha = 0.6f * p.r.dia), style = Stroke(max(0.5f, ancho * 0.08f)))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(lerp(C.SELVA, C.HOJA, 0.3f), lejos).copy(alpha = 0.55f)), pie.y - alto0 * 0.5f, pie.y + ancho), Offset(min(pie.x, punta.x) - ancho, pie.y - alto0), Size(abs(punta.x - pie.x) + ancho * 2f, alto0 + ancho))
            }
        }
        for ((dx, dz, k) in raices.filter { it.second < 0f }) raiz(dx, dz, k)
        // El tronco.
        val tronco = poligono((0..14).map { eje(it / 14f) + Offset(-g(it / 14f), 0f) } + (14 downTo 0).map { eje(it / 14f) + Offset(g(it / 14f), 0f) })
        drawPath(tronco, Brush.horizontalGradient(listOf(p.sombra(corteza, lejos), p.medio(corteza, lejos), p.luz(corteza, lejos), p.medio(corteza, lejos)), pie.x - lado * ancho * 0.6f, pie.x + lado * ancho * 0.6f))
        clipPath(tronco) {
            // Los regueros y las grietas a lo largo.
            val vetas = ArrayList<Offset>(80)
            repeat(22) { val x = az.entre(-1f, 1f); val t0 = az.entre(0f, 0.9f); val t1 = (t0 + az.entre(0.05f, 0.25f)).coerceAtMost(1f); vetas += eje(t0) + Offset(x * g(t0), 0f); vetas += eje(t1) + Offset(x * g(t1) + az.entre(-1f, 1f), 0f) }
            drawPoints(vetas, PointMode.Lines, p.sombra(lerp(corteza, C.BREA, 0.3f), lejos).copy(alpha = 0.45f), max(0.35f, ancho * 0.03f), StrokeCap.Round)
            // El liquen: manchas pálidas y verdosas, y el musgo del lado de la sombra.
            repeat(9) { val t = az.entre(0.05f, 0.95f); val q = eje(t) + Offset(az.entre(-0.8f, 0.8f) * g(t), 0f); mancha(q, ancho * az.entre(0.08f, 0.2f), ancho * az.entre(0.06f, 0.14f), if (it % 3 == 0) p.medio(lerp(C.HOJA_LUZ, C.LONA, 0.5f), lejos) else p.luz(lerp(C.LONA, C.ARENA, 0.3f), lejos), 0.45f) }
            repeat(6) { val t = az.entre(0f, 0.8f); mancha(eje(t) + Offset(-lado * g(t) * az.entre(0.4f, 0.9f), 0f), ancho * 0.22f, ancho * 0.45f, p.medio(lerp(C.SELVA, C.HOJA, 0.35f), lejos), 0.5f) }
        }
        // Las raíces de delante, que se abren hacia quien mira.
        for ((dx, dz, k) in raices.filter { it.second >= 0f }) raiz(dx, dz, k)
        // Las primeras ramas, gruesas, que salen hacia los lados y se pierden arriba; sus horquillas.
        val horquillas = ArrayList<Offset>()
        for ((i, s) in listOf(-1f, 1f, -1f).withIndex()) {
            val t = 0.62f + i * 0.13f + az.entre(-0.03f, 0.03f); val a = eje(t) + Offset(s * g(t) * 0.6f, 0f)
            val b = a + Offset(s * ancho * az.entre(1.8f, 2.8f), -ancho * az.entre(1.2f, 2f)); val c = b + Offset(s * ancho * az.entre(0.8f, 1.6f), -ancho * az.entre(1.4f, 2.4f))
            val grueso = ancho * (0.35f - 0.06f * i)
            drawPath(Path().apply { moveTo(a); cubicTo(a + (b - a) * 0.5f + Offset(0f, ancho * 0.2f), b, c) }, p.medio(corteza, lejos), style = Stroke(grueso, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(a); cubicTo(a + (b - a) * 0.5f + Offset(0f, ancho * 0.2f), b, c) }, p.luz(corteza, lejos).copy(alpha = 0.5f * p.r.dia), style = Stroke(grueso * 0.3f, cap = StrokeCap.Round))
            horquillas += a + (b - a) * 0.15f; horquillas += b
        }
        return horquillas
    }

    // =================================================================
    //  La colección viva: la maceta, la tina y la caja de vidrio
    // =================================================================

    /**
     * Una maceta de barro con el [pie] (el centro del fondo) en el lienzo, de [ancho] × [alto] unidades, vista desde
     * arriba un poco: el cuerpo que se estrecha abajo, con su lado de luz, el borde grueso, la tierra; el salitre
     * blanco, el musgo del pie y algún desconchón. Devuelve el centro de la tierra (donde va la planta).
     */
    fun DrawScope.maceta(p: Pincel, pie: Offset, ancho: Float, alto: Float, az: Azar, lejos: Float = 0f, vista: Float = 0.22f): Offset {
        val lado = if (p.solALaDerecha) 1f else -1f
        val w = ancho / 2f; val wb = w * 0.72f; val borde = alto * 0.16f
        val barro = lerp(C.TEJA, C.ARENA, 0.15f + 0.15f * az.real())
        val cuerpo = Path().apply { moveTo(pie + Offset(-wb, 0f)); cubicTo(pie + Offset(-wb, wb * vista), pie + Offset(wb, wb * vista), pie + Offset(wb, 0f)); lineTo(pie + Offset(w * 0.94f, -alto + borde)); lineTo(pie + Offset(-w * 0.94f, -alto + borde)); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(p.sombra(barro, lejos), p.medio(barro, lejos), p.luz(barro, lejos), p.medio(barro, lejos)), pie.x - lado * w, pie.x + lado * w))
        clipPath(cuerpo) {
            // El salitre que sube del agua de riego, el musgo del pie y la sombra del borde.
            mancha(pie + Offset(az.entre(-0.4f, 0.4f) * w, -alto * 0.35f), w * 0.6f, alto * 0.18f, p.medio(lerp(C.LONA, C.ARENA, 0.3f), lejos), 0.4f)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(lerp(C.SELVA, C.HOJA, 0.3f), lejos).copy(alpha = 0.5f)), pie.y - alto * 0.25f, pie.y + 2f), Offset(pie.x - w, pie.y - alto * 0.25f), Size(ancho, alto * 0.3f))
            drawRect(p.sombra(barro, lejos).copy(alpha = 0.45f), Offset(pie.x - w, pie.y - alto + borde), Size(ancho, alto * 0.08f))
        }
        // El borde, con su canto de luz, y la tierra húmeda dentro.
        val arriba = pie + Offset(0f, -alto + borde * 0.5f)
        drawRect(Brush.horizontalGradient(listOf(p.sombra(barro, lejos), p.luz(lerp(barro, C.ARENA, 0.2f), lejos)), arriba.x - lado * w, arriba.x + lado * w), Offset(arriba.x - w, arriba.y - borde * 0.5f), Size(ancho, borde))
        drawOval(p.luz(lerp(barro, C.ARENA, 0.25f), lejos), Offset(arriba.x - w, arriba.y - borde * 0.5f - w * vista), Size(ancho, w * vista * 2f))
        drawOval(p.sombra(lerp(C.MADERA_OSC, C.BREA, 0.4f), lejos), Offset(arriba.x - w * 0.86f, arriba.y - borde * 0.5f - w * vista * 0.8f), Size(w * 1.72f, w * vista * 1.6f))
        if (az.real() > 0.5f) drawPath(poligono(arriba + Offset(lado * w * 0.3f, -borde * 0.5f), arriba + Offset(lado * w * 0.55f, -borde * 0.5f), arriba + Offset(lado * w * 0.45f, borde * 0.4f)), p.medio(lerp(barro, C.ARENA, 0.4f), lejos))
        return arriba + Offset(0f, -borde * 0.5f)
    }

    /** Una tina de media barrica, como la [maceta]: duelas de madera gastada, dos aros de hierro con su óxido y la tierra. Devuelve el centro de la tierra. */
    fun DrawScope.tina(p: Pincel, pie: Offset, ancho: Float, alto: Float, az: Azar, lejos: Float = 0f, vista: Float = 0.22f): Offset {
        val lado = if (p.solALaDerecha) 1f else -1f
        val w = ancho / 2f; val wb = w * 0.88f
        val madera = lerp(C.MADERA, C.ROCA, 0.25f)
        val cuerpo = Path().apply { moveTo(pie + Offset(-wb, 0f)); cubicTo(pie + Offset(-wb, wb * vista), pie + Offset(wb, wb * vista), pie + Offset(wb, 0f)); cubicTo(pie + Offset(w * 1.04f, -alto * 0.5f), pie + Offset(w * 1.02f, -alto * 0.8f), pie + Offset(w, -alto)); lineTo(pie + Offset(-w, -alto)); cubicTo(pie + Offset(-w * 1.02f, -alto * 0.8f), pie + Offset(-w * 1.04f, -alto * 0.5f), pie + Offset(-wb, 0f)); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(p.sombra(madera, lejos), p.medio(madera, lejos), p.luz(madera, lejos), p.medio(madera, lejos)), pie.x - lado * w, pie.x + lado * w))
        clipPath(cuerpo) {
            // Las duelas, curvas, cada una con su tono; los aros de hierro con su filo de luz y su chorrera de óxido.
            for (k in -4..4) { val x = pie.x + k * w * 0.23f; drawLine(p.sombra(lerp(madera, C.BREA, 0.3f), lejos).copy(alpha = 0.6f), Offset(x * 0.98f + pie.x * 0.02f, pie.y + 2f), Offset(x, pie.y - alto), max(0.3f, ancho * 0.012f)) }
            for (y in listOf(0.22f, 0.78f)) { val yy = pie.y - alto * y; drawRect(p.sombra(C.HIERRO, lejos), Offset(pie.x - w * 1.1f, yy - alto * 0.05f), Size(ancho * 1.1f, alto * 0.1f)); drawLine(p.luz(C.HIERRO, lejos).copy(alpha = 0.6f), Offset(pie.x - w, yy - alto * 0.05f), Offset(pie.x + w, yy - alto * 0.05f), max(0.3f, alto * 0.02f))
                repeat(3) { val x = pie.x + az.entre(-0.8f, 0.8f) * w; drawLine(p.medio(lerp(C.CUERO, C.NARANJA, 0.2f), lejos).copy(alpha = 0.5f), Offset(x, yy + alto * 0.05f), Offset(x, yy + alto * az.entre(0.1f, 0.25f)), max(0.3f, ancho * 0.02f)) } }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(lerp(C.SELVA, C.HOJA, 0.3f), lejos).copy(alpha = 0.45f)), pie.y - alto * 0.2f, pie.y + 2f), Offset(pie.x - w * 1.1f, pie.y - alto * 0.2f), Size(ancho * 1.1f, alto * 0.25f))
        }
        val arriba = pie + Offset(0f, -alto)
        drawOval(p.luz(lerp(madera, C.ARENA, 0.25f), lejos), Offset(arriba.x - w, arriba.y - w * vista), Size(ancho, w * vista * 2f))
        drawOval(p.sombra(lerp(C.MADERA_OSC, C.BREA, 0.4f), lejos), Offset(arriba.x - w * 0.9f, arriba.y - w * vista * 0.85f), Size(w * 1.8f, w * vista * 1.7f))
        return arriba
    }

    /**
     * Una caja de vidrio para plantas (un invernadero portátil, de los que viajaban en cubierta con plantas vivas), en
     * [s]: el cajón de madera de abajo con la tierra, y encima la casita de vidrio de dos aguas, con su armazón de
     * madera y latón. Dentro, brotes de helecho y plantitas; el vidrio, empañado abajo, con sus gotas y el reflejo del
     * cielo. Medidas en metros; [base] es la altura donde se apoya (la tapa de un cajón).
     */
    class CajaDeVidrio(val s: Sitio, val semilla: Long, val ancho: Float = 0.64f, val fondo: Float = 0.4f, val zocalo: Float = 0.17f, val alto: Float = 0.3f, val cumbre: Float = 0.17f, val base: Float = 0f) {
        val tope: Float get() = base + zocalo + alto + cumbre
    }

    fun sombraDeCajaDeVidrio(c: CajaDeVidrio, sol: Sol3): Path { val w = c.ancho / 2f; val d = c.fondo / 2f
        return sombraDe(c.s, sol, esquinas(-w, w, c.base, c.base + c.zocalo + c.alto, -d, d) + listOf(V3(-w, c.tope, 0f), V3(w, c.tope, 0f)), c.base) }

    fun DrawScope.cajaDeVidrio(c: CajaDeVidrio, p: Pincel, sol: Sol3, brillo: Float = 0f) {
        val s = c.s; val az = Azar(c.semilla); val e = s.escala(0f, 0f)
        val w = c.ancho / 2f; val d = c.fondo / 2f; val y0 = c.base + c.zocalo; val y1 = y0 + c.alto; val yc = c.tope
        val madera = lerp(C.MADERA, C.CAOBA, 0.4f)
        val vidrio = p.l.tono(lerp(C.LONA, C.MAR, 0.25f))
        // Las caras de vidrio: las cuatro paredes y las dos aguas del tejado (con los dos piñones).
        val paredes = carasDeCaja(-w, w, y0, y1, -d, d).filter { it.n.y == 0f }
        val aguaDelante = Cara3(listOf(V3(-w, y1, -d), V3(w, y1, -d), V3(w, yc, 0f), V3(-w, yc, 0f)), V3(0f, d, -c.cumbre))
        val aguaDetras = Cara3(listOf(V3(w, y1, d), V3(-w, y1, d), V3(-w, yc, 0f), V3(w, yc, 0f)), V3(0f, d, c.cumbre))
        val pinones = listOf(listOf(V3(-w, y1, -d), V3(-w, y1, d), V3(-w, yc, 0f)) to V3(-1f, 0f, 0f), listOf(V3(w, y1, d), V3(w, y1, -d), V3(w, yc, 0f)) to V3(1f, 0f, 0f))
        // 1. Lo de detrás, visto a través del vidrio: las caras que no miran a los ojos, más apagadas.
        for (cara in (paredes + listOf(aguaDelante, aguaDetras)).filter { !s.seVe(it.centro, it.n) }) {
            drawPath(forma(s, cara), vidrio.copy(alpha = 0.18f))
            for (i in 0..3) { val q = cara.q[i]; val r = cara.q[(i + 1) % 4]; drawLine(p.sombra(madera).copy(alpha = 0.6f), s.p(q), s.p(r), (0.012f * e).coerceIn(0.3f, 1.4f)) }
        }
        // 2. La tierra y las plantitas de dentro: frondas de helecho que se desenrollan, hojitas, un brote alto.
        val tierra = poligono(listOf(V3(-w, y0, -d), V3(w, y0, -d), V3(w, y0, d), V3(-w, y0, d)).map { s.p(it) })
        drawPath(tierra, p.arriba(lerp(C.MADERA_OSC, C.BREA, 0.3f)))
        val brotes = (0 until 9).map { V3(az.entre(-w, w) * 0.85f, y0, az.entre(-d, d) * 0.8f) }.sortedByDescending { s.hondo(it) }
        for ((i, b) in brotes.withIndex()) {
            val pie = s.p(b); val alto = (c.alto * az.entre(0.35f, 0.85f)) * s.escala(b.x, b.z)
            when (i % 3) {
                0 -> for (j in 0..4) { val a = -PI.toFloat() / 2f + (j - 2) * 0.5f; val fin = pie + Offset(cos(a) * alto * 0.6f, sin(a) * alto); drawLine(p.medio(lerp(C.HOJA, C.SELVA, 0.3f)), pie, fin, max(0.3f, alto * 0.03f))
                    for (q in 1..5) { val r = pie + (fin - pie) * (q / 6f); drawLine(p.luz(lerp(C.HOJA, C.HOJA_LUZ, 0.3f)), r, r + Offset(alto * 0.06f, -alto * 0.02f), max(0.3f, alto * 0.025f)); drawLine(p.medio(C.HOJA), r, r + Offset(-alto * 0.06f, -alto * 0.02f), max(0.3f, alto * 0.025f)) } }
                1 -> { drawLine(p.medio(C.HOJA), pie, pie + Offset(0f, -alto), max(0.3f, alto * 0.03f)); for (j in 0..2) { val q = pie + Offset(0f, -alto * (0.4f + j * 0.25f)); val sgn = if (j % 2 == 0) 1f else -1f; drawOval(p.por(lerp(C.HOJA, C.SELVA, 0.2f), sgn * 0.4f), Offset(q.x + (if (sgn > 0f) 0f else -alto * 0.22f), q.y - alto * 0.06f), Size(alto * 0.22f, alto * 0.12f)) } }
                else -> { val r = alto * 0.12f; drawArc(p.luz(lerp(C.HOJA, C.ARENA, 0.3f)), 0f, 290f, false, pie + Offset(-r, -alto * 0.7f - r), Size(r * 2f, r * 2f), style = Stroke(max(0.3f, alto * 0.04f))); drawLine(p.medio(C.HOJA), pie, pie + Offset(r, -alto * 0.7f), max(0.3f, alto * 0.04f)) }
            }
        }
        // 3. El cajón de abajo: tablas con su lado de luz, las esquinas de latón.
        for (cara in visibles(s, carasDeCaja(-w * 1.04f, w * 1.04f, c.base, y0, -d * 1.06f, d * 1.06f)).filter { !it.arriba }) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(madera, t))
            clipPath(f) { linea(s, cara, 0f, 0.5f, 1f, 0.5f, p.sombra(C.BREA).copy(alpha = 0.5f), (0.008f * e).coerceIn(0.3f, 1f)); repeat(3) { val v = az.entre(0.1f, 0.9f); linea(s, cara, 0f, v, 1f, v, p.sombra(madera).copy(alpha = 0.25f), (0.004f * e).coerceIn(0.25f, 0.6f)) } }
            for (u in listOf(0.02f, 0.98f)) drawLine(p.luz(C.LATON), s.p(cara.en(u, 0f)), s.p(cara.en(u, 1f)), (0.02f * e).coerceIn(0.4f, 2f))
        }
        // 4. El vidrio de delante: el tinte del cielo, el vaho y las gotas abajo, el reflejo en diagonal, y el armazón.
        val delante = (paredes + listOf(aguaDelante, aguaDetras)).filter { s.seVe(it.centro, it.n) }
        for (cara in delante) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, vidrio.copy(alpha = 0.16f + 0.1f * (t.coerceAtLeast(0f))))
            clipPath(f) {
                if (!cara.arriba && cara.n.y == 0f) {
                    drawPath(poligono(listOf(cara.en(0f, 0f), cara.en(1f, 0f), cara.en(1f, 0.35f), cara.en(0f, 0.25f)).map { s.p(it) }), p.l.tono(C.LONA).copy(alpha = 0.22f))
                    drawPoints((0 until 30).map { s.p(cara.en(az.real(), az.real() * az.real() * 0.6f)) }, PointMode.Points, p.luz(Color.White).copy(alpha = 0.6f), (0.006f * e).coerceIn(0.4f, 1.2f), StrokeCap.Round)
                }
                drawPath(poligono(listOf(cara.en(0.15f, 0f), cara.en(0.32f, 0f), cara.en(0.62f, 1f), cara.en(0.45f, 1f)).map { s.p(it) }), Color.White.copy(alpha = (0.1f + 0.25f * brillo + 0.12f * t.coerceAtLeast(0f)).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
            }
        }
        for ((pts, n) in pinones) if (s.seVe((pts[0] + pts[1] + pts[2]) * (1f / 3f), n)) {
            val f = poligono(pts.map { s.p(it) }); drawPath(f, vidrio.copy(alpha = 0.18f))
            for (i in 0..2) drawLine(p.por(madera, luzDe(s, sol, Cara3(listOf(pts[0], pts[1], pts[2], pts[2]), n))), s.p(pts[i]), s.p(pts[(i + 1) % 3]), (0.016f * e).coerceIn(0.4f, 1.8f))
        }
        // El armazón: los montantes de las esquinas, el travesaño de en medio, la cumbrera y su remate.
        val g = (0.016f * e).coerceIn(0.4f, 1.8f)
        for (x in listOf(-w, w)) for (z in listOf(-d, d)) if (s.seVe(V3(x, y0, z), V3(x, 0f, z))) drawLine(p.por(madera, (x * if (p.solALaDerecha) 1f else -1f) * 2f), s.p(x, y0, z), s.p(x, y1, z), g)
        for (z in listOf(-d, d)) { drawLine(p.medio(madera), s.p(-w, y1, z), s.p(w, y1, z), g); drawLine(p.medio(madera), s.p(-w, y0, z), s.p(w, y0, z), g) }
        drawLine(p.medio(madera), s.p(0f, y0, -d), s.p(0f, y1, -d), g * 0.7f)
        drawLine(p.medio(madera), s.p(-w, y1, -d), s.p(-w, yc, 0f), g); drawLine(p.medio(madera), s.p(w, y1, -d), s.p(w, yc, 0f), g)
        drawLine(p.luz(C.LATON), s.p(-w, yc, 0f), s.p(w, yc, 0f), g * 1.3f)
        drawCircle(p.luz(C.LATON), g * 1.4f, s.p(0f, yc + 0.02f, 0f))
    }
}
