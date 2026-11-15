package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  La perspectiva del club
// =====================================================================
//
// El sótano se arma en metros y se mira con una cámara de agujero (`composicion_deco.md`: de pie en la esquina del
// estrado, con el punto de fuga hacia (246, 116)). Así cada cosa, girada como la dejaría alguien, enseña dos caras en
// tres cuartos (fallo 1 de S11), las sombras de cada lámpara y del sol salen de la misma geometría, y la mancha de sol que
// entra por las ventanas altas cae donde debe. Es una caja propia en vez de la `Estancia` de la biblioteca: la mancha de
// sol de `Estancia` entra por un hueco de las paredes de los lados con el ojo a media sala, y aquí el ojo está de lado,
// junto al estrado, y el sol tiene que cortarse con las mesas y las sillas.

/** La cámara del club: ojos a 1.45 m del suelo, mirando al fondo de la sala (eje z), con la focal [F] en unidades. */
internal object CamDeco {
    const val F = 300f
    const val CX = 246f
    const val CY = 116f
    const val OJO = 1.45f

    /** Un punto de la sala (x a la derecha, y hacia arriba desde el suelo, z hacia el fondo, en metros) en el lienzo. */
    fun p(x: Float, y: Float, z: Float): Offset { val zz = max(z, 0.25f); return Offset(CX + F * x / zz, CY - F * (y - OJO) / zz) }

    fun p(v: V3): Offset = p(v.x, v.y, v.z)

    /** Cuántas unidades del lienzo mide un metro a [z] de los ojos. */
    fun k(z: Float): Float = F / max(z, 0.25f)

    /** El camino cerrado que une los [puntos] de la sala, ya en el lienzo. */
    fun forma(puntos: List<V3>): Path = poligono(puntos.map { p(it) })

    /** Si una cara que pasa por [a] con la normal [n] mira hacia los ojos. */
    fun seVe(a: V3, n: V3): Boolean = n.x * -a.x + n.y * (OJO - a.y) + n.z * -a.z > 0f
}

/** Una cosa puesta en la sala: su origen [o] (en el suelo) y su [giro] (radianes: hacia dónde apunta su eje x local, desde el x de la sala hacia el fondo). */
internal class Marco3(val o: V3, val giro: Float) {
    private val cg = cos(giro); private val sg = sin(giro)
    /** Un punto de la cosa (x a lo largo, y arriba, z hacia su espalda) en la sala. */
    fun w(x: Float, y: Float, z: Float): V3 = V3(o.x + x * cg - z * sg, o.y + y, o.z + x * sg + z * cg)
    fun p(x: Float, y: Float, z: Float): Offset = CamDeco.p(w(x, y, z))
    /** Una dirección de la cosa en la sala. */
    fun n(x: Float, y: Float, z: Float): V3 = V3(x * cg - z * sg, y, x * sg + z * cg)
    fun forma(puntos: List<V3>): Path = CamDeco.forma(puntos.map { w(it.x, it.y, it.z) })
}

internal fun v3(x: Float, y: Float, z: Float) = V3(x, y, z)

/** Los puntos de una circunferencia horizontal de radio [r] alrededor de [c], de [a0] a [a1] (radianes; 0 hacia la derecha, π/2 hacia el fondo). */
internal fun aro(c: V3, r: Float, n: Int = 22, a0: Float = 0f, a1: Float = (2 * PI).toFloat(), rz: Float = r): List<V3> =
    (0..n).map { i -> val a = a0 + (a1 - a0) * i / n; V3(c.x + r * cos(a), c.y, c.z + rz * sin(a)) }

/**
 * Una cosa del club que se pinta en su turno: [hondo] es su distancia a los ojos (se pintan de la más lejana a la más
 * cercana), [esqueleto] los puntos de su cuerpo que arrojan sombra (su envolvente), y [pintar] la dibuja.
 */
internal class Cosa3(val hondo: Float, val esqueleto: List<V3>, val pintar: DrawScope.() -> Unit)

/** Las piezas del club de Lou, al nivel del cuadro: cada una con su lado de luz y de sombra, su materia y su uso. */
internal object PiezasDeco {
    private val C = ColorDeco
    private val FRENTE = V3(0f, 0f, -1f)
    private val ARRIBA = V3(0f, 1f, 0f)

    private fun DrawScope.cara(v: VistaDeco, puntos: List<V3>, c: Color, n: V3, centro: V3? = null) {
        val en = centro ?: V3(puntos.sumOf { it.x.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.y.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.z.toDouble() }.toFloat() / puntos.size)
        drawPath(CamDeco.forma(puntos), v.c(en, c, n))
    }

    /** Una caja de x0..x1, y0..y1, z0..z1 (en los ejes de [f]), con las caras que se ven. [encima] pinta sobre cada cara (0 frente, 1 izquierda, 2 derecha, 3 arriba). */
    fun DrawScope.caja(v: VistaDeco, f: Marco3, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, c: Color, encima: (DrawScope.(Int, Path) -> Unit)? = null) {
        val caras = listOf(
            Triple(0, listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)), V3(0f, 0f, -1f)),
            Triple(1, listOf(V3(x0, y0, z1), V3(x0, y0, z0), V3(x0, y1, z0), V3(x0, y1, z1)), V3(-1f, 0f, 0f)),
            Triple(2, listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)), V3(1f, 0f, 0f)),
            Triple(3, listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)), V3(0f, 1f, 0f)),
            Triple(4, listOf(V3(x1, y0, z1), V3(x0, y0, z1), V3(x0, y1, z1), V3(x1, y1, z1)), V3(0f, 0f, 1f)),
        )
        for ((i, ps, nl) in caras) {
            val n = f.n(nl.x, nl.y, nl.z); val mundo = ps.map { f.w(it.x, it.y, it.z) }
            if (!CamDeco.seVe(mundo[0], n)) continue
            val forma = CamDeco.forma(mundo)
            drawPath(forma, v.c(V3(mundo.sumOf { it.x.toDouble() }.toFloat() / 4f, mundo.sumOf { it.y.toDouble() }.toFloat() / 4f, mundo.sumOf { it.z.toDouble() }.toFloat() / 4f), c, n))
            encima?.invoke(this, i, forma)
        }
    }

    /** Un palo redondo de [a] a [b] (en la sala), de [grueso] metros, con su filo de luz del lado de las lámparas. */
    fun DrawScope.palo(v: VistaDeco, a: V3, b: V3, grueso: Float, c: Color, filo: Boolean = true) {
        val pa = CamDeco.p(a); val pb = CamDeco.p(b); val g = grueso * CamDeco.k((a.z + b.z) / 2f)
        val medio = V3((a.x + b.x) / 2f, (a.y + b.y) / 2f, (a.z + b.z) / 2f)
        drawLine(v.c(medio, c, FRENTE), pa, pb, g.coerceAtLeast(0.35f), StrokeCap.Round)
        if (filo && g > 0.9f) { val d = (pb - pa); val l = d.getDistance().coerceAtLeast(0.01f); val o = Offset(-d.y / l, d.x / l) * (g * 0.28f) * (if (d.y > 0f) 1f else -1f)
            drawLine(v.c(medio, lerp(c, C.MARFIL, 0.35f), V3(1f, 0.3f, -0.4f)).copy(alpha = 0.7f), pa + o, pb + o, (g * 0.3f).coerceAtLeast(0.3f), StrokeCap.Round) }
    }

    // =================================================================
    //  El piano vertical de Lou
    // =================================================================

    /** Lo que se ve en el piano según el día. */
    internal class EstadoPiano(val partitura: Boolean, val lampara: Float, val metronomo: Boolean, val funda: Boolean, val luto: Boolean, val flores: Boolean, val quemaduras: Int, val panuelo: Boolean)

    /** Las medidas del piano, en metros: largo, alto y fondo de la caja. */
    const val PIANO_LARGO = 1.48f
    const val PIANO_ALTO = 1.30f
    const val PIANO_FONDO = 0.62f

    /** Dónde está la lámpara de pantalla verde, en los ejes del piano (sobre la tapa, hacia el extremo lejano). */
    val LAMPARA_PIANO = V3(1.0f, PIANO_ALTO + 0.24f, 0.24f)

    /**
     * El piano vertical, casi nuevo («de segunda o tercera mano» en el dato del objeto), de caoba: la caja con sus dos
     * mejillas y sus patas torneadas, el teclado en escorzo (marfil y ébano), el atril con la partitura (pentagramas sin
     * letras: «una imagen dentro de la imagen»), el panel de arriba quitado, que enseña los macillos y las cuerdas, la
     * tapa abierta, y encima la lámpara de pantalla verde. Con [EstadoPiano.funda], tapado con una funda de lona.
     * Lo que se ve: el frente (que mira a la sala) y el costado cercano; la tapa, de arriba.
     */
    fun piano(v: VistaDeco, f: Marco3, e: EstadoPiano): Cosa3 {
        val L = PIANO_LARGO; val H = PIANO_ALTO; val D = PIANO_FONDO
        val esqueleto = listOf(V3(0f, 0f, -0.3f), V3(L, 0f, -0.3f), V3(0f, 0f, D), V3(L, 0f, D), V3(0f, H, 0f), V3(L, H, 0f), V3(0f, H, D), V3(L, H, D)).map { f.w(it.x, it.y, it.z) }
        return Cosa3(f.w(L / 2f, 0f, 0f).z, esqueleto) {
            if (e.funda) { pianoConFunda(v, f); return@Cosa3 }
            val madera = C.CAOBA; val oscura = lerp(C.CAOBA, C.LACA, 0.45f)
            // ---- La caja: el costado cercano, la tapa y el frente de arriba (detrás del atril).
            caja(v, f, 0f, L, 0f, H, 0f, D, madera) { i, forma ->
                when (i) {
                    1 -> clipPath(forma) {                                                  // el costado cercano: vetas y el canto de luz
                        val az = Azar(Semilla.de("deco", "piano", "costado"))
                        repeat(14) { val y = az.entre(0.05f, H - 0.05f); drawPath(f.forma(listOf(V3(0f, y, 0.02f), V3(0f, y + az.entre(-0.03f, 0.03f), D * az.entre(0.4f, 1f)), V3(0f, y + 0.008f, D * az.entre(0.4f, 1f)), V3(0f, y + 0.01f, 0.02f))), v.c(f.w(0f, y, D / 2f), lerp(madera, C.LACA, 0.3f), f.n(-1f, 0f, 0f)).copy(alpha = 0.35f)) }
                        // El canto delantero, redondeado, que coge la luz.
                        drawPath(f.forma(listOf(V3(0f, 0.62f, 0.0f), V3(0f, 0.62f, 0.04f), V3(0f, H, 0.04f), V3(0f, H, 0f))), v.c(f.w(0f, H / 2f, 0f), lerp(madera, C.MARFIL, 0.25f), f.n(-0.3f, 0f, -1f)).copy(alpha = 0.7f))
                        // Un recuadro moldurado.
                        drawPath(f.forma(listOf(V3(0f, 0.16f, 0.1f), V3(0f, 0.16f, D - 0.1f), V3(0f, H - 0.16f, D - 0.1f), V3(0f, H - 0.16f, 0.1f))), v.c(f.w(0f, H / 2f, D / 2f), lerp(madera, C.MARFIL, 0.18f), f.n(-1f, 0f, 0f)).copy(alpha = 0.55f), style = Stroke(0.8f))
                    }
                    3 -> clipPath(forma) {                                                  // la tapa: vista de arriba, con las quemaduras de cigarrillo
                        drawPath(f.forma(listOf(V3(0f, H, 0f), V3(L, H, 0f), V3(L, H, 0.04f), V3(0f, H, 0.04f))), v.c(f.w(L / 2f, H, 0f), lerp(madera, C.MARFIL, 0.3f), ARRIBA).copy(alpha = 0.8f))
                        val az = Azar(Semilla.de("deco", "quemaduras"))
                        repeat(e.quemaduras) { val x = 0.08f + az.entre(0f, 0.5f) + it * 0.07f; val q = f.p(x, H, 0.05f + az.entre(0f, 0.03f)); drawOval(C.LACA.copy(alpha = 0.6f), q - Offset(1.6f, 0.5f), Size(3.2f, 1f)) }
                    }
                    else -> {}
                }
            }
            // ---- El panel de arriba, quitado: se ve la mecánica (los macillos y las cuerdas), en penumbra.
            val y0 = 0.86f; val y1 = H - 0.08f; val x0 = 0.1f; val x1 = L - 0.1f; val zi = 0.12f
            val hueco = listOf(V3(x0, y0, -0.005f), V3(x1, y0, -0.005f), V3(x1, y1, -0.005f), V3(x0, y1, -0.005f))
            drawPath(f.forma(hueco), v.c(f.w(L / 2f, (y0 + y1) / 2f, zi), lerp(C.LACA, C.CAOBA, 0.4f), f.n(0f, 0f, -1f)))
            clipPath(f.forma(hueco)) {
                // Las cuerdas, en diagonal: las graves de cobre a la izquierda, las agudas de acero.
                for (k in 0..34) { val x = x0 + 0.02f + k * (x1 - x0 - 0.04f) / 34f; val cobre = k < 12
                    drawLine(v.c(f.w(x, y0 + 0.2f, zi), if (cobre) lerp(C.ORO, C.CORAL, 0.35f) else C.CROMO, f.n(0f, 0f, -1f)).copy(alpha = 0.55f), f.p(x - (if (cobre) 0.1f else 0f), y1, zi + 0.02f), f.p(x + (if (cobre) 0.06f else 0f), y0, zi), 0.45f) }
                // El riel y la fila de macillos: cabezas de fieltro claro sobre sus mangos.
                val ym = y0 + 0.17f
                drawPath(f.forma(listOf(V3(x0, ym - 0.03f, 0.06f), V3(x1, ym - 0.03f, 0.06f), V3(x1, ym - 0.06f, 0.06f), V3(x0, ym - 0.06f, 0.06f))), v.c(f.w(L / 2f, ym, 0.06f), C.ROBLE, f.n(0f, 0f, -1f)))
                for (k in 0..43) { val x = x0 + 0.03f + k * (x1 - x0 - 0.06f) / 43f
                    drawLine(v.c(f.w(x, ym, 0.07f), C.ROBLE_OSC, f.n(0f, 0f, -1f)), f.p(x, ym - 0.05f, 0.07f), f.p(x, ym + 0.02f, 0.05f), 0.5f)
                    drawPath(f.forma(listOf(V3(x - 0.011f, ym + 0.02f, 0.04f), V3(x + 0.011f, ym + 0.02f, 0.04f), V3(x + 0.011f, ym + 0.055f, 0.04f), V3(x - 0.011f, ym + 0.055f, 0.04f))), v.c(f.w(x, ym + 0.04f, 0.04f), if (k % 9 == 4) lerp(C.MARFIL, C.CORAL, 0.25f) else C.MARFIL, f.n(0f, 0.3f, -1f)))
                }
                // Los clavijeros, arriba: una fila de puntos de acero.
                for (k in 0..30) drawCircle(v.c(f.w(x0 + k * (x1 - x0) / 30f, y1 - 0.04f, 0.1f), C.CROMO, FRENTE).copy(alpha = 0.7f), 0.4f, f.p(x0 + k * (x1 - x0) / 30f, y1 - 0.04f, 0.1f))
            }
            // El marco del hueco: listones de caoba con su filo de luz.
            for ((a, b) in listOf(V3(0f, y1, 0f) to V3(L, y1, 0f), V3(0f, y0, 0f) to V3(L, y0, 0f))) palo(v, f.w(a.x, a.y, a.z - 0.01f), f.w(b.x, b.y, b.z - 0.01f), 0.035f, madera)
            // ---- La tapa de arriba, levantada hacia atrás (la mitad delantera doblada sobre la otra).
            val tapa = listOf(V3(0.02f, H, D * 0.5f), V3(L - 0.02f, H, D * 0.5f), V3(L - 0.02f, H + 0.05f, D * 0.52f), V3(0.02f, H + 0.05f, D * 0.52f))
            drawPath(f.forma(tapa), v.c(f.w(L / 2f, H, D * 0.5f), lerp(madera, C.MARFIL, 0.12f), ARRIBA))
            drawPath(f.forma(listOf(V3(0.0f, H + 0.001f, 0f), V3(L, H + 0.001f, 0f), V3(L, H + 0.001f, D * 0.48f), V3(0f, H + 0.001f, D * 0.48f))), v.c(f.w(L / 2f, H, D * 0.25f), lerp(C.LACA, madera, 0.35f), ARRIBA))
            // ---- La parte de abajo: las mejillas, el teclado y la tabla de las rodillas.
            val zt = -0.3f; val yt = 0.70f
            // La tabla de las rodillas, metida, y los pedales.
            drawPath(f.forma(listOf(V3(0.06f, 0.08f, -0.03f), V3(L - 0.06f, 0.08f, -0.03f), V3(L - 0.06f, yt - 0.06f, -0.03f), V3(0.06f, yt - 0.06f, -0.03f))), v.c(f.w(L / 2f, 0.4f, -0.03f), oscura, f.n(0f, 0f, -1f)))
            drawPath(f.forma(listOf(V3(0.2f, 0.22f, -0.031f), V3(L - 0.2f, 0.22f, -0.031f), V3(L - 0.2f, yt - 0.14f, -0.031f), V3(0.2f, yt - 0.14f, -0.031f))), v.c(f.w(L / 2f, 0.4f, -0.03f), lerp(madera, C.MARFIL, 0.15f), FRENTE).copy(alpha = 0.5f), style = Stroke(0.7f))
            for (k in 0..1) { val x = 0.66f + k * 0.16f; palo(v, f.w(x, 0.04f, -0.04f), f.w(x + 0.02f, 0.05f, -0.15f), 0.022f, C.ORO) }
            // Las mejillas: dos brazos que sostienen el teclado, con sus patas torneadas.
            for (x in listOf(0f, L - 0.05f)) {
                caja(v, f, x, x + 0.05f, yt - 0.1f, yt + 0.1f, zt, 0f, madera)
                palo(v, f.w(x + 0.025f, 0.02f, zt + 0.04f), f.w(x + 0.025f, yt - 0.1f, zt + 0.04f), 0.055f, madera)
                drawCircle(v.c(f.w(x, yt - 0.2f, zt), lerp(madera, C.MARFIL, 0.3f), FRENTE), 0.075f * CamDeco.k(f.w(x, 0f, zt).z) * 0.5f, f.p(x + 0.025f, yt - 0.3f, zt + 0.04f))
            }
            // El teclado: la tabla de las teclas, las blancas de marfil y las negras de ébano, en escorzo.
            val tx0 = 0.05f; val tx1 = L - 0.05f
            drawPath(f.forma(listOf(V3(tx0, yt - 0.05f, zt + 0.02f), V3(tx1, yt - 0.05f, zt + 0.02f), V3(tx1, yt, zt + 0.02f), V3(tx0, yt, zt + 0.02f))), v.c(f.w(L / 2f, yt, zt), madera, FRENTE))
            val blancas = listOf(V3(tx0, yt, zt + 0.02f), V3(tx1, yt, zt + 0.02f), V3(tx1, yt, -0.02f), V3(tx0, yt, -0.02f))
            drawPath(f.forma(blancas), v.c(f.w(L / 2f, yt, zt / 2f), lerp(C.MARFIL, C.ORO, 0.12f), ARRIBA))
            val n = 52
            for (k in 1 until n) { val x = tx0 + (tx1 - tx0) * k / n; drawLine(v.c(f.w(x, yt, zt / 2f), lerp(C.MARFIL, C.LACA, 0.4f), ARRIBA).copy(alpha = 0.6f), f.p(x, yt + 0.001f, zt + 0.02f), f.p(x, yt + 0.001f, -0.02f), 0.35f) }
            for (k in 0 until n - 1) {
                val nota = (k + 5) % 7; if (nota == 2 || nota == 6) continue
                val x = tx0 + (tx1 - tx0) * (k + 1) / n
                drawPath(f.forma(listOf(V3(x - 0.007f, yt + 0.012f, zt + 0.12f), V3(x + 0.007f, yt + 0.012f, zt + 0.12f), V3(x + 0.007f, yt + 0.012f, -0.02f), V3(x - 0.007f, yt + 0.012f, -0.02f))), v.c(f.w(x, yt, zt / 2f), C.LACA, ARRIBA))
            }
            // La tapa del teclado, abierta y metida bajo el atril, con su filo.
            drawPath(f.forma(listOf(V3(tx0, yt + 0.02f, -0.02f), V3(tx1, yt + 0.02f, -0.02f), V3(tx1, yt + 0.12f, -0.01f), V3(tx0, yt + 0.12f, -0.01f))), v.c(f.w(L / 2f, yt + 0.07f, -0.02f), madera, FRENTE))
            // ---- El atril, con la partitura abierta: pentagramas sin letras, con sus notas.
            val ya = 0.83f
            val atril = listOf(V3(0.36f, ya, -0.08f), V3(1.12f, ya, -0.08f), V3(1.12f, ya + 0.24f, -0.02f), V3(0.36f, ya + 0.24f, -0.02f))
            drawPath(f.forma(atril), v.c(f.w(0.74f, ya + 0.1f, -0.06f), lerp(madera, C.LACA, 0.2f), f.n(0f, 0.3f, -1f)))
            if (e.partitura) partitura(v, f, 0.42f, 1.08f, ya + 0.02f, ya + 0.34f)
            palo(v, f.w(0.36f, ya, -0.085f), f.w(1.12f, ya, -0.085f), 0.022f, madera)
            // ---- Lo de encima: la lámpara de pantalla verde, el panuelo, las flores del luto.
            lamparaDePiano(v, f, e.lampara)
            if (e.panuelo) { val o = V3(0.32f, H, 0.2f); drawPath(f.forma(listOf(V3(o.x, o.y + 0.005f, o.z - 0.1f), V3(o.x + 0.24f, o.y + 0.005f, o.z - 0.12f), V3(o.x + 0.2f, o.y + 0.005f, o.z + 0.1f), V3(o.x - 0.02f, o.y + 0.005f, o.z + 0.12f))), v.c(f.w(o.x, o.y, o.z), C.MARFIL, ARRIBA))
                drawLine(v.c(f.w(o.x, o.y, o.z), lerp(C.MARFIL, C.SOMBRA, 0.4f), ARRIBA), f.p(o.x + 0.02f, o.y + 0.006f, o.z - 0.06f), f.p(o.x + 0.18f, o.y + 0.006f, o.z + 0.06f), 0.4f) }
            if (e.metronomo) metronomo(v, f.w(0.18f, H, 0.24f))
            if (e.luto) crespon(v, f)
            if (e.flores) floresEnElPiano(v, f, if (e.luto) C.MARFIL else lerp(C.MARFIL, C.CORAL, 0.15f))
            // El filo de luz del canto de la tapa (lo muy oscuro necesita un filo cálido de noche).
            drawLine(v.c(f.w(L / 2f, H, 0f), lerp(C.CAOBA, C.LAMPARA, 0.6f), V3(0.3f, 1f, -0.5f)).copy(alpha = 0.55f + 0.3f * v.noche), f.p(0.02f, H + 0.003f, -0.003f), f.p(L - 0.02f, H + 0.003f, -0.003f), 0.9f, StrokeCap.Round)
        }
    }

    /** El piano tapado con su funda de lona gris, atada abajo: los días que Lou no está (la gira, sin club). */
    private fun DrawScope.pianoConFunda(v: VistaDeco, f: Marco3) {
        val L = PIANO_LARGO; val H = PIANO_ALTO; val D = PIANO_FONDO; val lona = lerp(lerp(C.ACERA, C.PANO, 0.5f), C.LADRILLO, 0.12f)
        caja(v, f, -0.03f, L + 0.03f, 0.08f, H + 0.04f, -0.33f, D + 0.02f, lona) { i, forma ->
            clipPath(forma) {
                // Los pliegues que caen desde las esquinas de arriba y los dobleces de la tela, en sombra y con su filo.
                val az = Azar(Semilla.de("deco", "funda", i))
                repeat(9) { val t = az.entre(0.05f, 0.95f); val a = if (i == 1) f.p(-0.03f, H, D * t) else if (i == 3) f.p(L * t, H + 0.04f, -0.33f) else f.p(L * t, H + 0.04f, -0.33f)
                    val b = if (i == 1) f.p(-0.03f, 0.08f, D * (t + az.entre(-0.15f, 0.15f))) else if (i == 3) f.p(L * (t + az.entre(-0.1f, 0.1f)), H + 0.04f, D) else f.p(L * (t + az.entre(-0.08f, 0.08f)), 0.08f, -0.33f)
                    drawLine(C.SOMBRA.copy(alpha = 0.35f), a, b, 2.2f, blendMode = BlendMode.Multiply)
                    drawLine(Color.White.copy(alpha = 0.1f), a + Offset(1.4f, 0f), b + Offset(1.4f, 0f), 0.6f) }
                // El bulto del teclado, a media altura.
                if (i == 0) drawLine(C.SOMBRA.copy(alpha = 0.4f), f.p(0f, 0.62f, -0.33f), f.p(L, 0.62f, -0.33f), 2.5f, blendMode = BlendMode.Multiply)
            }
        }
        // El dobladillo de abajo, en ondas, el cordel y una pata que asoma.
        val ondas = Path(); val p0 = f.p(-0.03f, 0.1f, -0.33f); ondas.moveTo(p0.x, p0.y)
        for (k in 1..12) { val q = f.p(-0.03f + (L + 0.06f) * k / 12f, 0.08f + (if (k % 2 == 0) 0.03f else 0f), -0.33f); ondas.lineTo(q.x, q.y) }
        drawPath(ondas, v.c(f.w(L / 2f, 0.1f, -0.33f), lerp(lona, C.LACA, 0.3f), FRENTE), style = Stroke(1f))
        drawLine(v.c(f.w(L / 2f, 0.22f, -0.33f), lerp(C.ROBLE, C.ACERA, 0.4f), FRENTE), f.p(-0.03f, 0.24f, -0.34f), f.p(L + 0.03f, 0.24f, -0.34f), 0.8f)
        palo(v, f.w(0.03f, 0f, -0.27f), f.w(0.03f, 0.08f, -0.27f), 0.05f, C.CAOBA)
    }

    /** La partitura abierta en el atril: dos páginas con sus pentagramas y sus notas, sin letras. */
    private fun DrawScope.partitura(v: VistaDeco, f: Marco3, x0: Float, x1: Float, y0: Float, y1: Float) {
        val papel = lerp(C.MARFIL, C.ORO, 0.1f); val xm = (x0 + x1) / 2f
        fun z(y: Float) = -0.075f + (y - y0) / (y1 - y0) * 0.06f
        for ((a, b) in listOf(x0 to xm - 0.005f, xm + 0.005f to x1)) {
            val hoja = listOf(V3(a, y0, z(y0)), V3(b, y0, z(y0)), V3(b, y1, z(y1)), V3(a, y1, z(y1)))
            drawPath(f.forma(hoja), v.c(f.w((a + b) / 2f, (y0 + y1) / 2f, z(y0)), papel, f.n(0f, 0.35f, -1f)))
            clipPath(f.forma(hoja)) {
                val az = Azar(Semilla.de("deco", "partitura", a))
                for (s in 0..4) { val yb = y1 - 0.05f - s * 0.058f
                    for (l in 0..4) { val y = yb - l * 0.007f; drawLine(v.c(f.w(a, y, z(y)), C.LACA, FRENTE).copy(alpha = 0.55f), f.p(a + 0.02f, y, z(y)), f.p(b - 0.02f, y, z(y)), 0.3f) }
                    var x = a + 0.035f
                    while (x < b - 0.03f) { val y = yb - az.entre(0f, 0.028f); val q = f.p(x, y, z(y)); drawOval(v.c(f.w(x, y, z(y)), C.LACA, FRENTE), q - Offset(0.7f, 0.45f), Size(1.4f, 0.9f))
                        if (az.real() > 0.3f) drawLine(v.c(f.w(x, y, z(y)), C.LACA, FRENTE).copy(alpha = 0.8f), q + Offset(0.6f, 0f), f.p(x + 0.004f, y + 0.022f, z(y)), 0.3f)
                        x += az.entre(0.018f, 0.04f) }
                }
                // Una portada de colores en la página de la izquierda: un abanico de rayos (la del dato de la partitura, sin la cantante ni las letras).
                if (a == x0) { val c = f.p(a + 0.06f, y1 - 0.02f, z(y1)); drawCircle(v.c(f.w(a, y1, z(y1)), C.CORAL, FRENTE).copy(alpha = 0.5f), 1.2f, c) }
            }
            // El borde y la sombra que la hoja deja en el atril.
            drawPath(f.forma(hoja), v.c(f.w(a, y0, z(y0)), lerp(papel, C.SOMBRA, 0.4f), FRENTE).copy(alpha = 0.5f), style = Stroke(0.35f))
        }
    }

    /** La lámpara de piano: pie de latón y pantalla de vidrio verde. Encendida, la pantalla brilla y alumbra hacia abajo. */
    private fun DrawScope.lamparaDePiano(v: VistaDeco, f: Marco3, encendida: Float) {
        val b = LAMPARA_PIANO; val base = V3(b.x, PIANO_ALTO, b.z)
        drawOval(v.c(f.w(base.x, base.y, base.z), C.LATON, ARRIBA), f.p(base.x - 0.07f, base.y + 0.01f, base.z).let { Offset(it.x, it.y - 1.2f) }, Size(abs(f.p(base.x + 0.07f, base.y, base.z).x - f.p(base.x - 0.07f, base.y, base.z).x).coerceAtLeast(3f), 2.4f))
        palo(v, f.w(base.x, base.y, base.z), f.w(base.x, b.y - 0.04f, base.z), 0.018f, C.LATON)
        // La pantalla: medio cilindro tumbado, de vidrio verde; por dentro, blanco.
        val pantalla = listOf(V3(b.x - 0.19f, b.y - 0.03f, b.z - 0.09f), V3(b.x + 0.19f, b.y - 0.03f, b.z - 0.09f), V3(b.x + 0.18f, b.y + 0.07f, b.z - 0.01f), V3(b.x - 0.18f, b.y + 0.07f, b.z - 0.01f))
        val forma = f.forma(pantalla); val p0 = f.p(b.x - 0.16f, b.y, b.z); val p1 = f.p(b.x + 0.16f, b.y, b.z)
        val verde = lerp(C.JADE, Color(0xFF6FE0A0), 0.4f * encendida)
        drawPath(forma, Brush.horizontalGradient(0f to v.c(f.w(b.x - 0.1f, b.y, b.z), lerp(C.JADE, C.MARFIL, 0.25f), V3(-0.5f, 0.5f, -0.5f)), 0.5f to lerp(v.c(f.w(b.x, b.y, b.z), C.JADE, FRENTE), verde, encendida), 1f to v.c(f.w(b.x + 0.1f, b.y, b.z), lerp(C.JADE, C.LACA, 0.3f), V3(0.5f, 0.5f, -0.5f)), startX = p0.x, endX = p1.x))
        drawPath(forma, lerp(C.LATON, C.LAMPARA, encendida * 0.5f).copy(alpha = 0.8f), style = Stroke(0.5f))
        if (encendida > 0.02f) {
            // El borde de abajo, por donde sale la luz: una raya casi blanca.
            drawLine(lerp(C.LAMPARA, Color.White, 0.5f).copy(alpha = encendida), f.p(b.x - 0.15f, b.y - 0.022f, b.z - 0.058f), f.p(b.x + 0.15f, b.y - 0.022f, b.z - 0.058f), 1f, StrokeCap.Round)
            drawPath(forma, Color(0xFF7CF0B0).copy(alpha = 0.25f * encendida), blendMode = BlendMode.Plus)
            // El charco de luz que deja sobre la partitura y el teclado: cálido, con un tinte verde del vidrio.
            val luz = lerp(C.LAMPARA, Color(0xFFD8F8C0), 0.3f)
            for ((x, y, z, r) in listOf(listOf(0.8f, 1.0f, -0.05f, 0.42f), listOf(0.85f, 0.72f, -0.16f, 0.5f))) {
                val c = f.p(x, y, z); val rr = r * CamDeco.k(f.w(x, y, z).z)
                drawOval(Brush.radialGradient(listOf(luz.copy(alpha = 0.42f * encendida), luz.copy(alpha = 0.14f * encendida), Color.Transparent), c, rr), c - Offset(rr, rr * 0.55f), Size(2f * rr, rr * 1.1f), blendMode = BlendMode.Plus)
            }
        }
    }

    /** El metrónomo del ensayo: una pirámide de madera con su aguja. */
    fun DrawScope.metronomo(v: VistaDeco, pie: V3, escala: Float = 1f) {
        val h = 0.22f * escala; val b = 0.06f * escala
        val izq = listOf(V3(pie.x - b, pie.y, pie.z - b), V3(pie.x + b, pie.y, pie.z - b), V3(pie.x + 0.012f, pie.y + h, pie.z - 0.012f), V3(pie.x - 0.012f, pie.y + h, pie.z - 0.012f))
        drawPath(CamDeco.forma(izq), v.c(pie, C.ROBLE_OSC, V3(0.4f, 0.2f, -1f)))
        val lado = listOf(V3(pie.x + b, pie.y, pie.z - b), V3(pie.x + b, pie.y, pie.z + b), V3(pie.x + 0.012f, pie.y + h, pie.z + 0.012f), V3(pie.x + 0.012f, pie.y + h, pie.z - 0.012f))
        drawPath(CamDeco.forma(lado), v.c(pie, C.ROBLE_OSC, V3(1f, 0.2f, 0f)))
        drawLine(v.c(pie, C.LATON, FRENTE), CamDeco.p(pie.x, pie.y + 0.03f, pie.z - b), CamDeco.p(pie.x + 0.03f, pie.y + h * 0.9f, pie.z - b), 0.6f)
    }

    /** El crespón del luto: una banda negra cruzada sobre la tapa, con su lazo. */
    private fun DrawScope.crespon(v: VistaDeco, f: Marco3) {
        val H = PIANO_ALTO; val negro = Color(0xFF221E26)
        drawPath(f.forma(listOf(V3(0.5f, H + 0.002f, -0.01f), V3(0.62f, H + 0.002f, -0.01f), V3(0.66f, H - 0.4f, -0.012f), V3(0.56f, H - 0.42f, -0.012f))), v.c(f.w(0.6f, H, 0f), negro, FRENTE))
        val c = f.p(0.56f, H, -0.01f)
        for (s in listOf(-1f, 1f)) drawPath(poligono(c, c + Offset(5f * s, -3f), c + Offset(5f * s, 3f)), v.c(f.w(0.56f, H, 0f), negro, FRENTE))
        drawCircle(v.c(f.w(0.56f, H, 0f), negro, FRENTE), 1.4f, c)
    }

    /** Flores en un jarrito sobre el piano: blancas de luto, o las de la boda. */
    private fun DrawScope.floresEnElPiano(v: VistaDeco, f: Marco3, color: Color) {
        val o = f.w(0.42f, PIANO_ALTO, 0.3f); val p = CamDeco.p(o); val k = CamDeco.k(o.z)
        val jarro = Path().apply { moveTo(p.x - 0.04f * k, p.y); cubicTo(p.x - 0.06f * k, p.y - 0.06f * k, p.x - 0.03f * k, p.y - 0.1f * k, p.x - 0.02f * k, p.y - 0.12f * k); lineTo(p.x + 0.02f * k, p.y - 0.12f * k); cubicTo(p.x + 0.03f * k, p.y - 0.1f * k, p.x + 0.06f * k, p.y - 0.06f * k, p.x + 0.04f * k, p.y); close() }
        drawPath(jarro, Brush.horizontalGradient(0f to v.c(o, lerp(C.JADE, C.MARFIL, 0.4f), V3(-1f, 0f, -0.5f)), 1f to v.c(o, C.JADE, V3(1f, 0f, -0.5f)), startX = p.x - 0.05f * k, endX = p.x + 0.05f * k))
        val az = Azar(Semilla.de("deco", "flores"))
        repeat(9) { val q = p + Offset(az.entre(-0.12f, 0.12f) * k, -(0.14f + az.entre(0f, 0.12f)) * k)
            drawLine(v.c(o, C.JADE, FRENTE), p + Offset(0f, -0.11f * k), q, 0.4f)
            drawCircle(v.c(o, lerp(color, C.LACA, az.entre(0f, 0.1f)), V3(az.entre(-1f, 1f), 0.5f, -0.5f)), az.entre(0.018f, 0.03f) * k, q) }
    }

    // =================================================================
    //  La mesita redonda, sus sillas y lo que hay encima
    // =================================================================

    /**
     * Cómo está una mesita: [puesta] con su mantel (y lo que dejan los que beben: [tazas], [cenicero]); [lampara]
     * encendida o no; [sillas] patas arriba sobre ella o alrededor; [sombrero] dejado encima.
     */
    internal class Mesa(val c: V3, val puesta: Boolean, val lampara: Float, val sillasArriba: Boolean, val tazas: Int, val cenicero: Int, val sombrero: Boolean, val semilla: Int)

    const val MESA_ALTO = 0.74f
    const val MESA_RADIO = 0.31f

    /** La mesita, con su pie de hierro fundido y su tapa (de madera con canto de latón; puesta, con su mantel de marfil). */
    fun DrawScope.mesita(v: VistaDeco, t: Mesa) {
        val c = t.c; val y = MESA_ALTO; val r = MESA_RADIO
        // El pie: tres patas curvas y la columna.
        // El pie de hierro fundido: tres patas en voluta y la columna con su anillo de latón.
        for (k in 0..2) { val a = (0.5 + k * 2.0 * PI / 3.0).toFloat()
            palo(v, V3(c.x + 0.24f * cos(a), 0.015f, c.z + 0.24f * sin(a)), V3(c.x + 0.16f * cos(a), 0.06f, c.z + 0.16f * sin(a)), 0.05f, C.HIERRO)
            palo(v, V3(c.x + 0.16f * cos(a), 0.06f, c.z + 0.16f * sin(a)), V3(c.x + 0.03f * cos(a), 0.16f, c.z + 0.03f * sin(a)), 0.045f, C.HIERRO) }
        palo(v, V3(c.x, 0.12f, c.z), V3(c.x, y - 0.03f, c.z), 0.07f, C.HIERRO)
        palo(v, V3(c.x, 0.2f, c.z), V3(c.x, 0.24f, c.z), 0.09f, C.LATON)
        if (t.puesta) {
            // El mantel: la tapa y la falda que cuelga, con sus pliegues; de lo cercano se ve la caída delantera.
            val top = aro(V3(c.x, y + 0.004f, c.z), r + 0.02f)
            val falda = ArrayList<Offset>()
            val az = Azar(Semilla.de("deco", "mantel", t.semilla))
            for (i in 0..24) { val a = (PI * i / 24).toFloat(); falda += CamDeco.p(c.x + (r + 0.025f) * cos(a), y - 0.16f - 0.02f * az.real() - (if (i % 4 == 2) 0.025f else 0f), c.z - (r + 0.025f) * sin(a)) }
            val frente = (0..24).map { i -> val a = (PI * i / 24).toFloat(); CamDeco.p(c.x + (r + 0.02f) * cos(a), y, c.z - (r + 0.02f) * sin(a)) }
            val faldaForma = poligono(frente + falda.asReversed())
            val izq = CamDeco.p(c.x - r, y, c.z); val der = CamDeco.p(c.x + r, y, c.z)
            drawPath(faldaForma, Brush.horizontalGradient(0f to v.c(V3(c.x - r, y - 0.08f, c.z), C.MARFIL, V3(-1f, 0f, -0.3f)), 0.45f to v.c(V3(c.x, y - 0.08f, c.z - r), C.MARFIL, FRENTE), 1f to v.c(V3(c.x + r, y - 0.08f, c.z), C.MARFIL, V3(1f, 0f, -0.3f)), startX = izq.x, endX = der.x))
            clipPath(faldaForma) { for (i in 1..5) { val a = (PI * (i + 0.5f) / 7).toFloat(); val q = CamDeco.p(c.x + r * cos(a), y - 0.02f, c.z - r * sin(a)); val q2 = CamDeco.p(c.x + (r + 0.025f) * cos(a), y - 0.19f, c.z - (r + 0.025f) * sin(a))
                drawLine(C.SOMBRA.copy(alpha = 0.16f), q, q2, 1.2f, blendMode = BlendMode.Multiply) } }
            drawPath(CamDeco.forma(top), v.c(V3(c.x, y, c.z), lerp(C.MARFIL, C.ORO, 0.05f), ARRIBA))
            // La arruga del centro, donde se dobló, y una mancha de café.
            drawLine(v.c(c, lerp(C.MARFIL, C.SOMBRA, 0.3f), ARRIBA).copy(alpha = 0.4f), CamDeco.p(c.x - r * 0.9f, y + 0.005f, c.z), CamDeco.p(c.x + r * 0.9f, y + 0.005f, c.z), 0.35f)
            if (t.tazas > 0) { val q = CamDeco.p(c.x + 0.1f, y + 0.005f, c.z - 0.12f); drawOval(v.c(c, lerp(C.MARFIL, C.CAOBA, 0.5f), ARRIBA).copy(alpha = 0.3f), q - Offset(2f, 0.6f), Size(4f, 1.2f)) }
        } else {
            val top = aro(V3(c.x, y, c.z), r)
            val canto = (0..24).map { i -> val a = (PI * i / 24).toFloat(); CamDeco.p(c.x + r * cos(a), y, c.z - r * sin(a)) }
            val canto2 = (0..24).map { i -> val a = (PI * i / 24).toFloat(); CamDeco.p(c.x + r * cos(a), y - 0.035f, c.z - r * sin(a)) }
            drawPath(poligono(canto + canto2.asReversed()), v.c(V3(c.x, y, c.z - r), C.LATON, FRENTE))
            // La tapa de mármol veteado, con los cercos de los vasos de anoche.
            val marmol = lerp(C.MARFIL, C.ACERA, 0.22f)
            drawPath(CamDeco.forma(top), v.c(V3(c.x, y, c.z), marmol, ARRIBA))
            val az = Azar(Semilla.de("deco", "tapa", t.semilla)); val k = CamDeco.k(c.z)
            clipPath(CamDeco.forma(top)) {
                repeat(5) { val a = CamDeco.p(c.x + az.entre(-r, r), y + 0.001f, c.z + az.entre(-r, r)); val b = a + Offset(az.entre(-0.3f, 0.3f) * k, az.entre(-0.05f, 0.05f) * k)
                    drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (b.x - a.x) * 0.3f, a.y - 1.5f, a.x + (b.x - a.x) * 0.7f, b.y + 1.5f, b.x, b.y) }, v.c(c, lerp(marmol, C.SOMBRA, 0.5f), ARRIBA).copy(alpha = 0.45f), style = Stroke(0.4f)) }
                repeat(3) { val q = CamDeco.p(c.x + az.entre(-0.2f, 0.2f), y + 0.002f, c.z + az.entre(-0.2f, 0.2f)); drawOval(v.c(c, lerp(marmol, C.CAOBA, 0.4f), ARRIBA).copy(alpha = 0.3f), q - Offset(0.04f * k, 0.012f * k), Size(0.08f * k, 0.024f * k), style = Stroke(0.45f)) }
                // El brillo del pulido, del lado de la luz.
                drawLine(Color.White.copy(alpha = 0.12f + 0.08f * v.noche), CamDeco.p(c.x - r * 0.6f, y + 0.002f, c.z - r * 0.3f), CamDeco.p(c.x + r * 0.2f, y + 0.002f, c.z - r * 0.5f), 0.04f * k, StrokeCap.Round)
            }
        }
        // Lo de encima: la lamparita, las tazas, el cenicero y algún sombrero.
        val az = Azar(Semilla.de("deco", "encima", t.semilla))
        val sobre = y + (if (t.puesta) 0.006f else 0.002f)
        val cosas = ArrayList<Pair<Float, () -> Unit>>()
        if (t.puesta) cosas += (c.z + 0.05f) to { lamparita(v, V3(c.x - 0.02f, sobre, c.z + 0.08f), t.lampara) }
        repeat(t.tazas) { i -> val a = az.entre(0f, 6.28f); val d = az.entre(0.1f, 0.22f); val q = V3(c.x + d * cos(a), sobre, c.z + d * sin(a) * 0.9f); cosas += q.z to { taza(v, q, i % 3 == 2) } }
        if (t.cenicero > 0) { val q = V3(c.x + az.entre(-0.18f, 0.18f), sobre, c.z - az.entre(0.05f, 0.18f)); cosas += q.z to { cenicero(v, q, t.cenicero) } }
        if (t.sombrero) { val q = V3(c.x + az.entre(-0.12f, 0.12f), sobre, c.z + az.entre(0.06f, 0.16f)); cosas += q.z to { sombrero(v, q, az.entre(-0.4f, 0.4f), if (t.semilla % 2 == 0) C.PANO else lerp(C.PANO, C.CAOBA, 0.5f)) } }
        for ((_, d) in cosas.sortedByDescending { it.first }) d()
    }

    /** La lamparita de mesa: pie de latón y pantalla escalonada de vidrio esmerilado. Encendida, alumbra el mantel. */
    fun DrawScope.lamparita(v: VistaDeco, pie: V3, encendida: Float) {
        val k = CamDeco.k(pie.z); val p = CamDeco.p(pie)
        if (encendida > 0.02f) drawOval(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.55f * encendida), C.LAMPARA.copy(alpha = 0.18f * encendida), Color.Transparent), p, 0.3f * k), p - Offset(0.3f * k, 0.09f * k), Size(0.6f * k, 0.18f * k), blendMode = BlendMode.Plus)
        drawOval(v.c(pie, C.LATON, ARRIBA), p - Offset(0.045f * k, 0.013f * k), Size(0.09f * k, 0.026f * k))
        palo(v, pie, V3(pie.x, pie.y + 0.17f, pie.z), 0.014f, C.LATON)
        // La pantalla: tres escalones de vidrio, del ancho al estrecho.
        for (s in 0..2) {
            val y0 = pie.y + 0.15f + s * 0.035f; val r0 = 0.085f - s * 0.02f; val r1 = r0 - 0.012f
            val a = CamDeco.p(pie.x - r0, y0, pie.z); val b = CamDeco.p(pie.x + r0, y0, pie.z); val c2 = CamDeco.p(pie.x + r1, y0 + 0.033f, pie.z); val d = CamDeco.p(pie.x - r1, y0 + 0.033f, pie.z)
            val forma = poligono(a, b, c2, d)
            val luz = lerp(v.c(V3(pie.x, y0, pie.z), C.MARFIL, FRENTE), lerp(C.LAMPARA, Color.White, 0.35f - 0.1f * s), encendida)
            drawPath(forma, Brush.horizontalGradient(0f to lerp(luz, C.SOMBRA, 0.25f * (1f - encendida)), 0.4f to luz, 1f to lerp(luz, v.c(pie, C.MARFIL, V3(1f, 0f, 0f)), 0.4f), startX = a.x, endX = b.x))
            drawLine(lerp(C.LATON, C.LAMPARA, 0.4f * encendida).copy(alpha = 0.8f), a, b, 0.45f)
        }
        if (encendida > 0.02f) drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.4f * encendida), Color.Transparent), CamDeco.p(pie.x, pie.y + 0.2f, pie.z), 0.16f * k), 0.16f * k, CamDeco.p(pie.x, pie.y + 0.2f, pie.z), blendMode = BlendMode.Plus)
    }

    /** Una taza de café con su plato (la Ley Seca: el licor va en tazas). Con [vaso], un vaso de agua. */
    fun DrawScope.taza(v: VistaDeco, pie: V3, vaso: Boolean = false) {
        val k = CamDeco.k(pie.z); val p = CamDeco.p(pie)
        if (vaso) {
            val forma = Path().apply { moveTo(p.x - 0.03f * k, p.y); lineTo(p.x - 0.035f * k, p.y - 0.09f * k); lineTo(p.x + 0.035f * k, p.y - 0.09f * k); lineTo(p.x + 0.03f * k, p.y); close() }
            drawPath(forma, v.c(pie, lerp(C.CROMO, C.JADE, 0.15f), FRENTE).copy(alpha = 0.55f))
            drawLine(v.c(pie, C.MARFIL, V3(1f, 0f, -1f)).copy(alpha = 0.7f), p + Offset(0.02f * k, -0.01f * k), p + Offset(0.024f * k, -0.08f * k), 0.5f)
            drawOval(v.c(pie, C.CROMO, ARRIBA).copy(alpha = 0.6f), Offset(p.x - 0.035f * k, p.y - 0.095f * k), Size(0.07f * k, 0.016f * k), style = Stroke(0.4f))
            return
        }
        drawOval(v.c(pie, C.MARFIL, ARRIBA), Offset(p.x - 0.06f * k, p.y - 0.012f * k), Size(0.12f * k, 0.026f * k))
        val forma = Path().apply { moveTo(p.x - 0.04f * k, p.y - 0.06f * k); cubicTo(p.x - 0.04f * k, p.y - 0.015f * k, p.x - 0.02f * k, p.y - 0.008f * k, p.x, p.y - 0.008f * k); cubicTo(p.x + 0.02f * k, p.y - 0.008f * k, p.x + 0.04f * k, p.y - 0.015f * k, p.x + 0.04f * k, p.y - 0.06f * k); close() }
        drawPath(forma, Brush.horizontalGradient(0f to v.c(pie, C.MARFIL, V3(-1f, 0f, -0.5f)), 0.6f to v.c(pie, lerp(C.MARFIL, C.CORAL, 0.15f), FRENTE), 1f to v.c(pie, lerp(C.MARFIL, C.SOMBRA, 0.25f), V3(1f, 0f, -0.5f)), startX = p.x - 0.04f * k, endX = p.x + 0.04f * k))
        drawLine(v.c(pie, C.CORAL, FRENTE), Offset(p.x - 0.04f * k, p.y - 0.05f * k), Offset(p.x + 0.04f * k, p.y - 0.05f * k), 0.4f)
        drawOval(v.c(pie, lerp(C.CAOBA, C.LACA, 0.5f), ARRIBA), Offset(p.x - 0.036f * k, p.y - 0.066f * k), Size(0.072f * k, 0.014f * k))
        drawArc(v.c(pie, C.MARFIL, V3(1f, 0f, 0f)), -80f, 160f, false, Offset(p.x + 0.03f * k, p.y - 0.055f * k), Size(0.028f * k, 0.03f * k), style = Stroke(0.5f))
    }

    /** El cenicero de vidrio, con [colillas] y alguna aún humeando (su humo va en la parte viva). */
    fun DrawScope.cenicero(v: VistaDeco, pie: V3, colillas: Int) {
        val k = CamDeco.k(pie.z); val p = CamDeco.p(pie)
        drawOval(v.c(pie, lerp(C.CROMO, C.JADE, 0.2f), ARRIBA).copy(alpha = 0.85f), Offset(p.x - 0.055f * k, p.y - 0.018f * k), Size(0.11f * k, 0.03f * k))
        drawOval(v.c(pie, lerp(C.ACERA, C.LACA, 0.3f), ARRIBA), Offset(p.x - 0.04f * k, p.y - 0.014f * k), Size(0.08f * k, 0.02f * k))
        val az = Azar(Semilla.de("deco", "colillas", pie.x, pie.z))
        repeat(colillas.coerceAtMost(7)) { val q = p + Offset(az.entre(-0.03f, 0.03f) * k, az.entre(-0.008f, 0.004f) * k); drawLine(v.c(pie, C.MARFIL, ARRIBA), q, q + Offset(az.entre(-0.02f, 0.02f) * k, -0.004f * k), 0.5f, StrokeCap.Round) }
    }

    /** Un sombrero de fieltro (de hombre, con su cinta) o un cloché, dejado en una mesa o colgado. */
    fun DrawScope.sombrero(v: VistaDeco, pie: V3, giro: Float, fieltro: Color, cloche: Boolean = false) {
        val k = CamDeco.k(pie.z); val p = CamDeco.p(pie)
        if (cloche) {
            drawPath(Path().apply { moveTo(p.x - 0.1f * k, p.y); cubicTo(p.x - 0.1f * k, p.y - 0.12f * k, p.x + 0.1f * k, p.y - 0.12f * k, p.x + 0.1f * k, p.y); close() }, Brush.horizontalGradient(0f to v.c(pie, fieltro, V3(-1f, 0.3f, -0.5f)), 1f to v.c(pie, fieltro, V3(1f, 0.3f, -0.5f)), startX = p.x - 0.1f * k, endX = p.x + 0.1f * k))
            drawLine(v.c(pie, C.CORAL, FRENTE), p + Offset(-0.09f * k, -0.025f * k), p + Offset(0.09f * k, -0.025f * k), 0.035f * k)
            return
        }
        drawOval(v.c(pie, lerp(fieltro, C.LACA, 0.15f), ARRIBA), Offset(p.x - 0.15f * k, p.y - 0.035f * k), Size(0.3f * k, 0.07f * k))
        val copa = Path().apply { moveTo(p.x - 0.085f * k, p.y - 0.01f * k); lineTo(p.x - 0.08f * k, p.y - 0.1f * k); cubicTo(p.x - 0.04f * k, p.y - 0.12f * k, p.x + 0.04f * k, p.y - 0.12f * k, p.x + 0.08f * k, p.y - 0.1f * k); lineTo(p.x + 0.085f * k, p.y - 0.01f * k); close() }
        drawPath(copa, Brush.horizontalGradient(0f to v.c(pie, fieltro, V3(-1f, 0.2f, -0.5f)), 0.5f to v.c(pie, fieltro, FRENTE), 1f to v.c(pie, lerp(fieltro, C.LACA, 0.3f), V3(1f, 0.2f, -0.5f)), startX = p.x - 0.085f * k, endX = p.x + 0.085f * k))
        drawLine(v.c(pie, lerp(fieltro, C.LACA, 0.5f), FRENTE), p + Offset(-0.084f * k, -0.03f * k), p + Offset(0.084f * k, -0.03f * k), 0.025f * k)
        drawLine(v.c(pie, C.LACA, ARRIBA).copy(alpha = 0.5f), p + Offset(-0.02f * k, -0.112f * k), p + Offset(0.03f * k, -0.105f * k), 0.4f)
    }

    /**
     * Una silla de madera curvada (de las de café): el asiento redondo, cuatro patas abiertas con su aro, el respaldo de
     * dos curvas. [o] es donde pisa (o, patas arriba, dónde apoya el asiento sobre la mesa); [giro], hacia dónde mira su
     * respaldo. Con [abrigo], uno echado en el respaldo.
     */
    fun DrawScope.silla(v: VistaDeco, o: V3, giro: Float, arriba: Boolean, abrigo: Color? = null) {
        val f = Marco3(o, giro); val madera = C.ROBLE_OSC; val g = 0.034f
        // Patas arriba: el asiento descansa en la mesa y las patas suben; el respaldo cuelga por fuera del canto.
        fun y(yl: Float) = if (arriba) 0.47f - yl else yl
        val asiento = 0.45f; val r = 0.2f
        val patas = listOf(V3(-0.17f, 0f, -0.15f), V3(0.17f, 0f, -0.15f), V3(0.15f, 0f, 0.17f), V3(-0.15f, 0f, 0.17f))
        val enAsiento = listOf(V3(-0.12f, asiento, -0.11f), V3(0.12f, asiento, -0.11f), V3(0.11f, asiento, 0.12f), V3(-0.11f, asiento, 0.12f))
        // El respaldo, en la espalda (z positivo): dos montantes que se curvan arriba.
        fun respaldo() {
            val r0 = V3(-0.15f, asiento, 0.17f); val r1 = V3(0.15f, asiento, 0.17f)
            val puntos = (0..16).map { i -> val a = (PI * i / 16).toFloat(); V3(-0.15f * cos(a), asiento + 0.36f + 0.1f * sin(a), 0.2f + 0.02f * sin(a)) }
            val trazo = Path(); val pa = f.p(r0.x, y(r0.y), r0.z); trazo.moveTo(pa.x, pa.y)
            val u = f.p(-0.15f, y(asiento + 0.36f), 0.2f); trazo.lineTo(u.x, u.y)
            for (q in puntos) { val pq = f.p(q.x, y(q.y), q.z); trazo.lineTo(pq.x, pq.y) }
            val pb = f.p(r1.x, y(r1.y), r1.z); trazo.lineTo(pb.x, pb.y)
            val kk = CamDeco.k(o.z)
            drawPath(trazo, v.c(f.w(0f, y(asiento + 0.3f), 0.2f), madera, FRENTE), style = Stroke(g * kk, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // El aro de dentro del respaldo.
            val dentro = Path(); val inner = (0..14).map { i -> val a = (PI * i / 14).toFloat(); f.p(-0.09f * cos(a), y(asiento + 0.1f + 0.2f * sin(a).coerceAtLeast(0f) + 0.06f), 0.2f) }
            dentro.moveTo(inner[0].x, inner[0].y); for (q in inner) dentro.lineTo(q.x, q.y)
            drawPath(dentro, v.c(f.w(0f, y(asiento + 0.2f), 0.2f), madera, FRENTE), style = Stroke(g * 0.7f * kk, cap = StrokeCap.Round))
            drawPath(trazo, v.c(f.w(0f, y(asiento + 0.3f), 0.2f), lerp(madera, C.MARFIL, 0.4f), V3(0.6f, 0.4f, -0.6f)).copy(alpha = 0.5f), style = Stroke(g * 0.3f * kk, cap = StrokeCap.Round))
            if (abrigo != null && !arriba) {
                // El abrigo echado sobre el respaldo: los hombros en el travesaño de arriba, la espalda que cae por detrás
                // del respaldo, una manga que cuelga por delante, y el forro de color asomando en la abertura.
                val kk2 = CamDeco.k(o.z)
                val hombro0 = f.p(-0.17f, asiento + 0.47f, 0.21f); val hombro1 = f.p(0.17f, asiento + 0.47f, 0.21f)
                val cae0 = f.p(-0.2f, asiento - 0.18f, 0.27f); val cae1 = f.p(0.2f, asiento - 0.2f, 0.28f)
                val forma = Path().apply { moveTo(hombro0.x, hombro0.y); cubicTo(hombro0.x, hombro0.y - 0.04f * kk2, hombro1.x, hombro1.y - 0.04f * kk2, hombro1.x, hombro1.y)
                    lineTo(cae1.x, cae1.y); cubicTo(cae1.x - 0.08f * kk2, cae1.y + 0.03f * kk2, cae0.x + 0.08f * kk2, cae0.y + 0.04f * kk2, cae0.x, cae0.y); close() }
                val lx = minOf(hombro0.x, cae0.x); val rx = maxOf(hombro1.x, cae1.x)
                drawPath(forma, Brush.horizontalGradient(0f to v.c(f.w(-0.2f, asiento, 0.2f), abrigo, f.n(-1f, 0f, -0.5f)), 0.45f to v.c(f.w(0f, asiento, 0.2f), lerp(abrigo, C.MARFIL, 0.06f), f.n(0f, 0f, -1f)), 1f to v.c(f.w(0.2f, asiento, 0.2f), lerp(abrigo, C.LACA, 0.3f), f.n(1f, 0f, -0.5f)), startX = lx, endX = rx))
                clipPath(forma) { for (i in 0..3) { val x = -0.13f + i * 0.085f; drawLine(C.SOMBRA.copy(alpha = 0.3f), f.p(x * 0.8f, asiento + 0.42f, 0.22f), f.p(x * 1.1f, asiento - 0.2f, 0.27f), 1f, blendMode = BlendMode.Multiply) }
                    drawPath(f.forma(listOf(V3(0.02f, asiento + 0.44f, 0.215f), V3(0.08f, asiento + 0.44f, 0.215f), V3(0.06f, asiento - 0.15f, 0.27f), V3(0.0f, asiento - 0.15f, 0.27f))), v.c(f.w(0.05f, asiento, 0.24f), lerp(C.CORAL, abrigo, 0.35f), f.n(0f, 0f, -1f))) }
                // La manga que cuelga por delante del respaldo hasta el asiento.
                drawPath(f.forma(listOf(V3(0.1f, asiento + 0.46f, 0.19f), V3(0.17f, asiento + 0.45f, 0.19f), V3(0.15f, asiento + 0.05f, 0.14f), V3(0.07f, asiento + 0.06f, 0.14f))), v.c(f.w(0.12f, asiento + 0.25f, 0.17f), lerp(abrigo, C.LACA, 0.15f), f.n(0.4f, 0f, -1f)))
                // El cuello, de terciopelo, que coge un filo de luz.
                drawPath(Path().apply { moveTo(hombro0.x, hombro0.y); cubicTo(hombro0.x, hombro0.y - 0.04f * kk2, hombro1.x, hombro1.y - 0.04f * kk2, hombro1.x, hombro1.y) }, v.c(f.w(0f, asiento + 0.47f, 0.2f), lerp(abrigo, C.MARFIL, 0.3f), f.n(0f, 1f, -0.5f)), style = Stroke(0.035f * kk2, cap = StrokeCap.Round))
            }
        }
        val k = CamDeco.k(o.z)
        // Lo de atrás primero: si el respaldo queda lejos de los ojos, se pinta antes.
        val respaldoLejos = f.w(0f, 0f, 0.2f).z > f.w(0f, 0f, -0.1f).z
        if (respaldoLejos) respaldo()
        for ((i, pt) in patas.withIndex()) { val a = enAsiento[i]; palo(v, f.w(a.x, y(a.y), a.z), f.w(pt.x, y(pt.y), pt.z), g, madera) }
        // El aro que une las patas.
        val aroP = (0..20).map { i -> val a = (2 * PI * i / 20).toFloat(); f.p(0.15f * cos(a), y(0.17f), 0.15f * sin(a)) }
        drawPath(poligono(aroP), v.c(f.w(0f, y(0.17f), 0f), madera, FRENTE), style = Stroke(g * 0.6f * k))
        // El asiento: la tapa redonda y su canto.
        val tapa = (0..20).map { i -> val a = (2 * PI * i / 20).toFloat(); f.p(r * cos(a), y(asiento), r * sin(a)) }
        val canto = (0..20).map { i -> val a = (2 * PI * i / 20).toFloat(); f.p(r * cos(a), y(asiento) + (if (arriba) 0.03f else -0.03f) * (CamDeco.k(o.z) / k), r * sin(a)) }
        drawPath(poligono(canto), v.c(f.w(0f, y(asiento), -r), lerp(madera, C.LACA, 0.2f), FRENTE))
        drawPath(poligono(tapa), v.c(f.w(0f, y(asiento), 0f), lerp(C.ROBLE, C.MARFIL, if (arriba) 0f else 0.08f), if (arriba) V3(0f, -1f, 0f) else ARRIBA))
        if (!arriba) drawPath(poligono(tapa), v.c(f.w(0f, y(asiento), 0f), C.ROBLE_OSC, ARRIBA).copy(alpha = 0.5f), style = Stroke(0.4f))
        if (!respaldoLejos) respaldo()
    }

    // =================================================================
    //  Las lámparas
    // =================================================================

    /**
     * La lámpara déco colgante: un plafón de latón en el techo, tres varillas, y un cuenco de vidrio esmerilado en tres
     * escalones con su remate de latón. [encendida], de 0 a 1: el vidrio brilla de dentro, más abajo, donde sale la luz.
     */
    fun DrawScope.lamparaColgante(v: VistaDeco, techo: V3, cuelga: Float, radio: Float, encendida: Float) {
        val y0 = techo.y - cuelga; val c = V3(techo.x, y0, techo.z); val k = CamDeco.k(techo.z)
        // Las varillas y el plafón.
        for (a in listOf(0.6f, 2.6f, 4.4f)) palo(v, V3(techo.x + 0.05f * cos(a), techo.y, techo.z + 0.05f * sin(a)), V3(c.x + radio * 0.8f * cos(a), y0 + 0.18f, c.z + radio * 0.8f * sin(a)), 0.008f, C.LATON, filo = false)
        drawPath(CamDeco.forma(aro(V3(techo.x, techo.y - 0.03f, techo.z), 0.09f, 14)), v.c(techo, C.LATON, V3(0f, -1f, -0.5f)))
        // Los tres escalones del vidrio, del más ancho (arriba) al más estrecho, cada uno un tronco de cono.
        val escalones = listOf(Triple(0.18f, radio, radio * 0.92f), Triple(0.1f, radio * 0.78f, radio * 0.68f), Triple(0.03f, radio * 0.52f, radio * 0.38f))
        for ((i, e) in escalones.withIndex()) {
            val (ya, ra, rb) = e; val yb = ya - 0.07f
            val arriba = (0..18).map { j -> val a = (PI * j / 18).toFloat(); CamDeco.p(c.x + ra * cos(a), y0 + ya, c.z - ra * sin(a)) }
            val abajo = (0..18).map { j -> val a = (PI * j / 18).toFloat(); CamDeco.p(c.x + rb * cos(a), y0 + yb, c.z - rb * sin(a)) }
            val forma = poligono(arriba + abajo.asReversed()); val izq = CamDeco.p(c.x - ra, y0 + ya, c.z).x; val der = CamDeco.p(c.x + ra, y0 + ya, c.z).x
            val apagado = v.c(V3(c.x, y0 + ya, c.z), C.MARFIL, FRENTE); val luz = lerp(C.LAMPARA, Color.White, 0.25f + 0.15f * i)
            val medio = lerp(apagado, luz, encendida * (0.75f + 0.08f * i))
            drawPath(forma, Brush.horizontalGradient(0f to lerp(medio, mult(medio, C.SOMBRA, 0.6f), 0.5f), 0.35f to medio, 0.55f to lerp(medio, Color.White, 0.25f * encendida), 1f to lerp(medio, mult(medio, C.SOMBRA, 0.7f), 0.55f), startX = izq, endX = der))
            // El canto de latón de cada escalón, y la raya del esmerilado.
            drawPath(poligono(arriba), v.c(V3(c.x, y0 + ya, c.z - ra), lerp(C.LATON, C.LAMPARA, 0.3f * encendida), FRENTE).copy(alpha = 0.9f), style = Stroke((0.012f * k).coerceAtLeast(0.6f)))
            drawPath(poligono(abajo), Color.White.copy(alpha = 0.12f + 0.2f * encendida), style = Stroke(0.4f))
        }
        // El remate de abajo.
        val pr = CamDeco.p(c.x, y0 - 0.06f, c.z)
        drawCircle(v.c(c, C.LATON, V3(0.3f, -0.5f, -1f)), 0.025f * k, pr)
        drawLine(v.c(c, C.LATON, FRENTE), CamDeco.p(c.x, y0 - 0.04f, c.z), CamDeco.p(c.x, y0 - 0.11f, c.z), 0.008f * k)
    }

    /**
     * Un aplique de pared en forma de abanico (vidrio esmerilado y latón), en la pared de la izquierda: encendido, lava la
     * pared hacia arriba en abanico (eso lo pinta la luz de la sala).
     */
    fun DrawScope.aplique(v: VistaDeco, en: V3, encendido: Float) {
        val k = CamDeco.k(en.z); val p = CamDeco.p(en)
        val forma = Path().apply { moveTo(p.x, p.y + 0.06f * k); lineTo(p.x - 0.07f * k * 0.55f, p.y - 0.06f * k); cubicTo(p.x - 0.03f * k * 0.55f, p.y - 0.09f * k, p.x + 0.03f * k * 0.55f, p.y - 0.09f * k, p.x + 0.07f * k * 0.55f, p.y - 0.06f * k); close() }
        drawPath(forma, lerp(v.c(en, C.MARFIL, V3(1f, 0f, 0f)), lerp(C.LAMPARA, Color.White, 0.3f), encendido))
        for (i in -2..2) drawLine(v.c(en, C.LATON, V3(1f, 0f, 0f)).copy(alpha = 0.8f), Offset(p.x, p.y + 0.05f * k), Offset(p.x + i * 0.013f * k, p.y - 0.075f * k), 0.4f)
        drawPath(forma, v.c(en, C.LATON, V3(1f, 0f, 0f)), style = Stroke(0.5f))
    }

    // =================================================================
    //  Los instrumentos del estrado
    // =================================================================

    /**
     * El contrabajo, recostado en la pared entre dos pases: la caja con sus dos efes, el puente, las cuatro cuerdas, el
     * mástil y la voluta, que sube por encima de todo y cruza el horizonte (regla 6). [f]: x a lo ancho de la tapa, y a lo
     * largo del instrumento (sale ya inclinado), z hacia su espalda.
     */
    fun contrabajo(v: VistaDeco, f: Marco3, inclina: Float): Cosa3 {
        fun q(x: Float, y: Float, z: Float = 0f) = f.w(x, y * cos(inclina) + z * sin(inclina), -y * sin(inclina) + z * cos(inclina))
        val alto = 1.12f
        // El perfil de la caja: hombros caídos, cintura, panza (mitad derecha; la izquierda es su espejo).
        val perfil = listOf(0.06f to 0.0f, 0.24f to 0.05f, 0.34f to 0.2f, 0.33f to 0.38f, 0.24f to 0.52f, 0.25f to 0.62f, 0.27f to 0.76f, 0.2f to 0.95f, 0.1f to 1.06f, 0.06f to alto)
        val esq = (perfil.map { (x, y) -> q(x, y) } + perfil.map { (x, y) -> q(-x, y) } + listOf(q(0f, 1.9f))).toList()
        return Cosa3(f.o.z, esq) {
            val tapa = perfil.map { (x, y) -> CamDeco.p(q(x, y)) } + perfil.asReversed().map { (x, y) -> CamDeco.p(q(-x, y)) }
            val aros = perfil.map { (x, y) -> CamDeco.p(q(x, y, 0.2f)) } + perfil.asReversed().map { (x, y) -> CamDeco.p(q(-x, y, 0.2f)) }
            val izq = CamDeco.p(q(-0.34f, 0.3f)).x; val der = CamDeco.p(q(0.34f, 0.3f)).x
            drawPath(poligono(aros), v.c(q(0f, 0.4f, 0.1f), lerp(C.CAOBA, C.LACA, 0.3f), f.n(1f, 0f, 0f)))
            drawPath(poligono(tapa), Brush.horizontalGradient(0f to v.c(q(-0.3f, 0.4f), lerp(C.CAOBA, C.CORAL, 0.25f), f.n(-1f, 0f, -1f)), 0.4f to v.c(q(0f, 0.4f), lerp(C.CAOBA, C.ORO, 0.25f), f.n(0f, 0f, -1f)), 0.75f to v.c(q(0.2f, 0.4f), C.CAOBA, f.n(0.5f, 0f, -1f)), 1f to v.c(q(0.3f, 0.4f), lerp(C.CAOBA, C.LACA, 0.4f), f.n(1f, 0f, -1f)), startX = minOf(izq, der), endX = maxOf(izq, der)))
            // El filete del borde, las efes y el puente.
            drawPath(poligono(tapa), v.c(q(0f, 0.4f), lerp(C.CAOBA, C.LACA, 0.6f), f.n(0f, 0f, -1f)), style = Stroke(0.5f))
            for (s in listOf(-1f, 1f)) { val ef = Path(); val a = CamDeco.p(q(0.12f * s, 0.55f)); val b = CamDeco.p(q(0.1f * s, 0.4f)); val c = CamDeco.p(q(0.13f * s, 0.27f)); ef.moveTo(a.x, a.y); ef.cubicTo(b.x - 1f, b.y, b.x + 1f, b.y, c.x, c.y)
                drawPath(ef, C.LACA.copy(alpha = 0.85f), style = Stroke(0.7f, cap = StrokeCap.Round)) }
            drawLine(v.c(q(0f, 0.4f), C.MARFIL, f.n(0f, 0f, -1f)), CamDeco.p(q(-0.1f, 0.4f, -0.04f)), CamDeco.p(q(0.1f, 0.4f, -0.04f)), 1.1f)
            // El cordal y las cuerdas, del cordal a la cejuela.
            drawLine(v.c(q(0f, 0.2f), C.LACA, f.n(0f, 0f, -1f)), CamDeco.p(q(0f, 0.08f, -0.02f)), CamDeco.p(q(0f, 0.28f, -0.03f)), 2.2f, StrokeCap.Round)
            // El mástil, el diapasón negro y la voluta.
            drawLine(v.c(q(0f, 1.4f), C.CAOBA, f.n(0.3f, 0f, -1f)), CamDeco.p(q(0f, alto - 0.05f, 0.02f)), CamDeco.p(q(0f, 1.8f, 0.02f)), 0.07f * CamDeco.k(f.o.z), StrokeCap.Round)
            drawLine(v.c(q(0f, 1.2f), C.LACA, f.n(0f, 0f, -1f)), CamDeco.p(q(0f, 0.5f, -0.04f)), CamDeco.p(q(0f, 1.76f, -0.02f)), 0.045f * CamDeco.k(f.o.z), StrokeCap.Round)
            for (i in 0..3) { val x = -0.03f + i * 0.02f; drawLine(v.c(q(0f, 1f), C.CROMO, f.n(0f, 0f, -1f)).copy(alpha = 0.8f), CamDeco.p(q(x * 1.5f, 0.28f, -0.045f)), CamDeco.p(q(x * 0.7f, 1.74f, -0.03f)), 0.3f) }
            val voluta = CamDeco.p(q(0f, 1.86f, 0.02f)); val kk = CamDeco.k(f.o.z)
            drawCircle(v.c(q(0f, 1.86f), C.CAOBA, f.n(0.5f, 0.3f, -1f)), 0.05f * kk, voluta)
            drawCircle(v.c(q(0f, 1.86f), lerp(C.CAOBA, C.LACA, 0.5f), FRENTE), 0.025f * kk, voluta + Offset(0.01f * kk, 0f), style = Stroke(0.5f))
            for (i in 0..1) drawLine(v.c(q(0f, 1.8f), C.LATON, FRENTE), CamDeco.p(q(-0.05f, 1.74f + i * 0.05f, 0.02f)), CamDeco.p(q(0.07f, 1.74f + i * 0.05f, 0.02f)), 0.7f, StrokeCap.Round)
            // El brillo del barniz en la panza: el filo de luz cálida.
            drawPath(Path().apply { val a = CamDeco.p(q(-0.26f, 0.12f)); val b = CamDeco.p(q(-0.33f, 0.3f)); val c = CamDeco.p(q(-0.24f, 0.5f)); moveTo(a.x, a.y); cubicTo(b.x, b.y, b.x, b.y, c.x, c.y) }, lerp(C.ORO, C.LAMPARA, 0.5f).copy(alpha = 0.3f + 0.35f * v.noche), style = Stroke(0.9f, cap = StrokeCap.Round))
            // La pica, apoyada en el suelo del estrado.
            drawLine(v.c(q(0f, -0.05f), C.CROMO, FRENTE), CamDeco.p(q(0f, 0f)), CamDeco.p(q(0f, -0.12f)), 0.8f)
        }
    }

    /**
     * La batería de Boots: el bombo con su parche pintado (un sol de rayos, como los de los años veinte, que de noche se
     * enciende por dentro), la caja con las escobillas encima, un tom, el charles y el platillo de pie.
     */
    fun bateria(v: VistaDeco, c: V3, parcheEncendido: Float): Cosa3 {
        val esq = listOf(V3(c.x - 0.45f, c.y, c.z - 0.3f), V3(c.x + 0.5f, c.y, c.z - 0.3f), V3(c.x - 0.45f, c.y, c.z + 0.4f), V3(c.x + 0.5f, c.y, c.z + 0.4f), V3(c.x - 0.4f, c.y + 1.1f, c.z), V3(c.x + 0.4f, c.y + 1.1f, c.z))
        return Cosa3(c.z - 0.3f, esq) {
            val k = CamDeco.k(c.z)
            // El platillo de pie y el charles, detrás.
            for ((dx, dz, h) in listOf(Triple(-0.38f, 0.25f, 1.05f), Triple(0.42f, 0.15f, 0.92f))) {
                palo(v, V3(c.x + dx, c.y, c.z + dz), V3(c.x + dx, c.y + h, c.z + dz), 0.014f, C.CROMO)
                for (t in 0..2) { val a = t * 2.1f; palo(v, V3(c.x + dx, c.y + 0.25f, c.z + dz), V3(c.x + dx + 0.14f * cos(a), c.y, c.z + dz + 0.14f * sin(a)), 0.01f, C.CROMO, false) }
                val pl = aro(V3(c.x + dx, c.y + h, c.z + dz), 0.2f, 18, rz = 0.2f).map { CamDeco.p(V3(it.x, it.y + 0.03f * (it.x - c.x - dx), it.z)) }
                drawPath(poligono(pl), Brush.horizontalGradient(0f to v.c(V3(c.x + dx - 0.2f, c.y + h, c.z), C.ORO, V3(-0.5f, 1f, -0.5f)), 0.5f to v.c(V3(c.x + dx, c.y + h, c.z), lerp(C.ORO, C.MARFIL, 0.4f), ARRIBA), 1f to v.c(V3(c.x + dx + 0.2f, c.y + h, c.z), lerp(C.ORO, C.CAOBA, 0.3f), V3(0.5f, 1f, -0.5f)), startX = pl.minOf { it.x }, endX = pl.maxOf { it.x }))
                drawPath(poligono(pl), v.c(V3(c.x + dx, c.y + h, c.z), lerp(C.ORO, C.CAOBA, 0.4f), FRENTE).copy(alpha = 0.6f), style = Stroke(0.4f))
            }
            // El bombo: un cilindro tumbado con el parche mirando a la sala.
            val rb = 0.34f; val fondo = 0.36f; val cb = V3(c.x, c.y + rb + 0.02f, c.z)
            val delante = (0..24).map { i -> val a = (2 * PI * i / 24).toFloat(); CamDeco.p(cb.x + rb * cos(a), cb.y + rb * sin(a), cb.z - fondo / 2f) }
            val detras = (0..24).map { i -> val a = (2 * PI * i / 24).toFloat(); CamDeco.p(cb.x + rb * cos(a), cb.y + rb * sin(a), cb.z + fondo / 2f) }
            drawPath(poligono(envolvente(delante + detras)), Brush.horizontalGradient(0f to v.c(V3(cb.x - rb, cb.y, cb.z), C.CORAL, V3(-1f, 0f, 0f)), 0.6f to v.c(cb, lerp(C.CORAL, C.LACA, 0.2f), FRENTE), 1f to v.c(V3(cb.x + rb, cb.y, cb.z), lerp(C.CORAL, C.LACA, 0.45f), V3(1f, 0f, 0f)), startX = delante.minOf { it.x }, endX = delante.maxOf { it.x }))
            // El parche, con el sol de rayos pintado: una imagen dentro de la imagen.
            val centro = CamDeco.p(cb.x, cb.y, cb.z - fondo / 2f); val r = rb * CamDeco.k(cb.z - fondo / 2f)
            val parche = lerp(v.c(cb, C.MARFIL, FRENTE), lerp(C.LAMPARA, C.MARFIL, 0.4f), parcheEncendido)
            drawPath(poligono(delante), parche)
            clipPath(poligono(delante)) {
                for (i in 0..11) { val a = (PI * (i + 0.5f) / 12).toFloat() + PI.toFloat(); val b = a + 0.13f
                    drawPath(poligono(centro + Offset(0f, r * 0.3f), centro + Offset(cos(a) * r, sin(a) * r + r * 0.3f), centro + Offset(cos(b) * r, sin(b) * r + r * 0.3f)), lerp(v.c(cb, if (i % 2 == 0) C.ORO else C.CORAL, FRENTE), if (i % 2 == 0) C.ORO else C.CORAL, parcheEncendido * 0.7f).copy(alpha = 0.85f)) }
                drawArc(lerp(v.c(cb, C.ORO, FRENTE), C.ORO, parcheEncendido), 180f, 180f, true, centro + Offset(-r * 0.32f, -r * 0.02f), Size(r * 0.64f, r * 0.64f))
                drawRect(lerp(v.c(cb, C.JADE, FRENTE), C.JADE, parcheEncendido * 0.6f), centro + Offset(-r, r * 0.3f), Size(2 * r, r))
                for (i in 0..2) drawLine(lerp(v.c(cb, C.ORO, FRENTE), C.ORO, parcheEncendido).copy(alpha = 0.8f), centro + Offset(-r, r * (0.45f + i * 0.14f)), centro + Offset(r, r * (0.45f + i * 0.14f)), 0.5f)
            }
            drawPath(poligono(delante), v.c(cb, C.CROMO, FRENTE), style = Stroke(0.035f * k))
            if (parcheEncendido > 0.02f) drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.3f * parcheEncendido), Color.Transparent), centro, r * 1.4f), r * 1.4f, centro, blendMode = BlendMode.Plus)
            for (i in 0..7) { val a = (2 * PI * i / 8).toFloat(); drawCircle(v.c(cb, C.CROMO, FRENTE), 0.012f * k, CamDeco.p(cb.x + rb * cos(a), cb.y + rb * sin(a), cb.z - fondo / 2f + 0.03f)) }
            // El tom, encima del bombo, y la caja, a la derecha, con las escobillas cruzadas.
            tambor(v, V3(cb.x - 0.12f, cb.y + rb + 0.12f, cb.z + 0.02f), 0.13f, 0.14f, C.CORAL)
            palo(v, V3(c.x + 0.44f, c.y, c.z - 0.2f), V3(c.x + 0.44f, c.y + 0.55f, c.z - 0.2f), 0.012f, C.CROMO)
            for (t in 0..2) { val a = t * 2.1f + 0.4f; palo(v, V3(c.x + 0.44f, c.y + 0.2f, c.z - 0.2f), V3(c.x + 0.44f + 0.15f * cos(a), c.y, c.z - 0.2f + 0.15f * sin(a)), 0.01f, C.CROMO, false) }
            val caja = V3(c.x + 0.44f, c.y + 0.6f, c.z - 0.2f)
            tambor(v, caja, 0.18f, 0.12f, C.MARFIL)
            for (s in listOf(-1f, 1f)) { val a = CamDeco.p(caja.x - 0.12f, caja.y + 0.01f, caja.z - 0.1f * s); val b = CamDeco.p(caja.x + 0.18f, caja.y + 0.03f, caja.z + 0.1f * s)
                drawLine(v.c(caja, C.ROBLE, ARRIBA), a, b, 0.7f, StrokeCap.Round); drawLine(v.c(caja, C.CROMO, ARRIBA).copy(alpha = 0.7f), b, b + (b - a) * 0.22f, 1.3f) }
        }
    }

    /** Un tambor (la caja o un tom): cilindro de pie con su parche arriba y sus aros cromados. */
    private fun DrawScope.tambor(v: VistaDeco, arriba: V3, r: Float, alto: Float, color: Color) {
        val k = CamDeco.k(arriba.z)
        val top = (0..20).map { i -> val a = (2 * PI * i / 20).toFloat(); CamDeco.p(arriba.x + r * cos(a), arriba.y, arriba.z + r * sin(a)) }
        val izq = CamDeco.p(arriba.x - r, arriba.y, arriba.z); val der = CamDeco.p(arriba.x + r, arriba.y, arriba.z)
        val cuerpo = poligono(listOf(izq, der, der + Offset(0f, alto * k), izq + Offset(0f, alto * k)))
        drawPath(cuerpo, Brush.horizontalGradient(0f to v.c(arriba, color, V3(-1f, 0f, -0.3f)), 0.5f to v.c(arriba, color, FRENTE), 1f to v.c(arriba, lerp(color, C.LACA, 0.35f), V3(1f, 0f, -0.3f)), startX = izq.x, endX = der.x))
        drawOval(v.c(arriba, C.MARFIL, ARRIBA), Offset(izq.x, top.minOf { it.y }), Size(der.x - izq.x, (top.maxOf { it.y } - top.minOf { it.y }).coerceAtLeast(1f)))
        drawLine(v.c(arriba, C.CROMO, FRENTE), izq, der, 0.6f); drawLine(v.c(arriba, C.CROMO, FRENTE), izq + Offset(0f, alto * k), der + Offset(0f, alto * k), 0.6f)
        for (i in 0..4) { val x = izq.x + (der.x - izq.x) * (0.1f + i * 0.2f); drawLine(v.c(arriba, C.CROMO, FRENTE).copy(alpha = 0.8f), Offset(x, izq.y), Offset(x, izq.y + alto * k), 0.35f) }
    }

    /** La corneta de latón, dejada de lado sobre el asiento de una silla. */
    fun DrawScope.corneta(v: VistaDeco, sobre: V3, escala: Float = 1f) {
        val k = CamDeco.k(sobre.z) * escala; val p = CamDeco.p(sobre)
        val laton = C.LATON
        // El tubo enrollado, la campana y los tres pistones.
        drawOval(v.c(sobre, laton, FRENTE), Offset(p.x - 0.1f * k, p.y - 0.05f * k), Size(0.16f * k, 0.05f * k), style = Stroke(0.016f * k))
        val campana = Path().apply { moveTo(p.x + 0.04f * k, p.y - 0.035f * k); lineTo(p.x + 0.15f * k, p.y - 0.06f * k); lineTo(p.x + 0.17f * k, p.y + 0.0f * k); lineTo(p.x + 0.04f * k, p.y - 0.02f * k); close() }
        drawPath(campana, Brush.verticalGradient(0f to lerp(v.c(sobre, laton, ARRIBA), C.MARFIL, 0.3f), 1f to v.c(sobre, lerp(laton, C.CAOBA, 0.3f), FRENTE), startY = p.y - 0.06f * k, endY = p.y))
        drawOval(v.c(sobre, lerp(laton, C.LACA, 0.4f), FRENTE), Offset(p.x + 0.145f * k, p.y - 0.062f * k), Size(0.03f * k, 0.065f * k))
        for (i in 0..2) drawLine(v.c(sobre, lerp(laton, C.MARFIL, 0.4f), ARRIBA), Offset(p.x - 0.03f * k + i * 0.022f * k, p.y - 0.05f * k), Offset(p.x - 0.03f * k + i * 0.022f * k, p.y - 0.085f * k), 0.012f * k)
        drawLine(v.c(sobre, laton, FRENTE), Offset(p.x - 0.1f * k, p.y - 0.03f * k), Offset(p.x - 0.17f * k, p.y - 0.02f * k), 0.01f * k)
        drawCircle(lerp(C.LAMPARA, Color.White, 0.4f).copy(alpha = 0.35f + 0.4f * v.noche), 0.012f * k, Offset(p.x + 0.12f * k, p.y - 0.05f * k))
    }

    /** El atril de los músicos: un pie de hierro y la bandeja de laca con un abanico de oro pintado. */
    fun DrawScope.atril(v: VistaDeco, pie: V3, giro: Float) {
        val f = Marco3(pie, giro)
        palo(v, f.w(0f, 0f, 0f), f.w(0f, 0.95f, 0f), 0.016f, C.HIERRO)
        for (t in 0..2) { val a = t * 2.1f; palo(v, f.w(0f, 0.2f, 0f), f.w(0.18f * cos(a), 0f, 0.18f * sin(a)), 0.012f, C.HIERRO, false) }
        val bandeja = listOf(V3(-0.26f, 0.95f, -0.02f), V3(0.26f, 0.95f, -0.02f), V3(0.24f, 1.28f, 0.06f), V3(-0.24f, 1.28f, 0.06f))
        drawPath(f.forma(bandeja), v.c(f.w(0f, 1.1f, 0f), C.LACA, f.n(0f, 0.3f, -1f)))
        val c = f.p(0f, 0.97f, -0.02f); val r = 0.2f * CamDeco.k(pie.z)
        clipPath(f.forma(bandeja)) { for (i in 0..6) { val a = (PI + PI * (i + 0.5f) / 7).toFloat(); drawLine(v.c(f.w(0f, 1.1f, 0f), C.ORO, FRENTE), c, c + Offset(cos(a) * r, sin(a) * r), 0.6f) } }
        drawPath(f.forma(bandeja), v.c(f.w(0f, 1.1f, 0f), C.ORO, FRENTE), style = Stroke(0.6f))
    }

    // =================================================================
    //  El perchero
    // =================================================================

    /** El perchero de madera curvada al pie de la escalera, con [sombreros] y otros tantos abrigos. Con [nieve], copos en los hombros. */
    fun perchero(v: VistaDeco, pie: V3, sombreros: Int, nieve: Boolean): Cosa3 {
        val esq = listOf(V3(pie.x - 0.3f, 0f, pie.z - 0.3f), V3(pie.x + 0.3f, 0f, pie.z - 0.3f), V3(pie.x, 1.9f, pie.z), V3(pie.x - 0.3f, 1.6f, pie.z), V3(pie.x + 0.3f, 1.6f, pie.z))
        return Cosa3(pie.z, esq) {
            val madera = C.ROBLE_OSC
            for (t in 0..3) { val a = (t * PI / 2 + 0.4f).toFloat(); palo(v, V3(pie.x, 0.25f, pie.z), V3(pie.x + 0.25f * cos(a), 0.01f, pie.z + 0.25f * sin(a)), 0.03f, madera) }
            // Los abrigos colgados de sus ganchos: largos y oscuros, con el cuello, la solapa y las mangas que caen.
            val n = sombreros.coerceIn(0, 9); val az = Azar(Semilla.de("deco", "perchero"))
            val colores = listOf(C.PANO, lerp(C.PANO, C.CAOBA, 0.5f), lerp(C.LACA, C.PANO, 0.45f), lerp(C.CORAL, C.LACA, 0.55f), lerp(C.ACERA, C.LACA, 0.5f))
            val ganchos = (0 until 6).map { i -> val a = (PI * 2 * i / 6 + 0.3f).toFloat(); V3(pie.x + 0.16f * cos(a), 1.74f, pie.z + 0.16f * sin(a)) }
            val abrigos = (0 until minOf(n, 4)).map { ganchos[it] }.sortedByDescending { it.z }
            for ((i, g) in abrigos.withIndex()) {
                val col = colores[(i + n) % colores.size]; val largo = az.entre(0.82f, 1.0f); val ancho = az.entre(0.13f, 0.16f); val k = CamDeco.k(g.z)
                val p0 = CamDeco.p(g); val izq = CamDeco.p(g.x - ancho, g.y - 0.18f, g.z).x; val der = CamDeco.p(g.x + ancho, g.y - 0.18f, g.z).x
                val abrigo = Path().apply {
                    moveTo(p0.x - 0.03f * k, p0.y); lineTo(p0.x + 0.03f * k, p0.y)
                    cubicTo(p0.x + 0.08f * k, p0.y + 0.02f * k, der, p0.y + 0.08f * k, der, p0.y + 0.2f * k)
                    lineTo(der + 0.015f * k, p0.y + largo * k); cubicTo(der - 0.05f * k, p0.y + (largo + 0.03f) * k, izq + 0.05f * k, p0.y + (largo + 0.04f) * k, izq - 0.01f * k, p0.y + (largo - 0.02f) * k)
                    lineTo(izq, p0.y + 0.2f * k); cubicTo(izq, p0.y + 0.08f * k, p0.x - 0.08f * k, p0.y + 0.02f * k, p0.x - 0.03f * k, p0.y); close()
                }
                drawPath(abrigo, Brush.horizontalGradient(0f to v.c(V3(g.x - ancho, g.y - 0.5f, g.z), col, V3(-1f, 0f, -0.4f)), 0.4f to v.c(V3(g.x, g.y - 0.5f, g.z), lerp(col, C.MARFIL, 0.08f), FRENTE), 1f to v.c(V3(g.x + ancho, g.y - 0.5f, g.z), lerp(col, C.LACA, 0.35f), V3(1f, 0f, -0.4f)), startX = izq, endX = der))
                clipPath(abrigo) {
                    // Los pliegues que caen del gancho, la solapa abierta (con su forro de color) y una manga.
                    for (j in 0..2) { val x = izq + (der - izq) * (0.25f + j * 0.25f); drawLine(C.SOMBRA.copy(alpha = 0.32f), Offset(p0.x + (x - p0.x) * 0.3f, p0.y + 0.15f * k), Offset(x + (j - 1) * 0.01f * k, p0.y + largo * k), 0.9f, blendMode = BlendMode.Multiply) }
                    drawPath(poligono(Offset(p0.x - 0.01f * k, p0.y + 0.03f * k), Offset(p0.x + 0.04f * k, p0.y + 0.03f * k), Offset(p0.x + 0.015f * k, p0.y + 0.42f * k)), v.c(V3(g.x, g.y - 0.2f, g.z), lerp(C.CORAL, col, 0.45f), FRENTE))
                    drawLine(v.c(V3(g.x + ancho, g.y - 0.4f, g.z), lerp(col, C.LACA, 0.2f), V3(1f, 0f, -0.5f)), Offset(der - 0.03f * k, p0.y + 0.22f * k), Offset(der - 0.02f * k, p0.y + 0.7f * k), 0.05f * k, StrokeCap.Round)
                    if (nieve) repeat(6) { val q = Offset(az.entre(izq, der), p0.y + az.entre(0.05f, 0.25f) * k); drawCircle(C.MARFIL.copy(alpha = 0.8f), 0.5f, q) }
                }
                drawPath(Path().apply { moveTo(izq, p0.y + 0.2f * k); cubicTo(izq, p0.y + 0.08f * k, p0.x - 0.08f * k, p0.y + 0.02f * k, p0.x, p0.y) }, v.c(V3(g.x, g.y, g.z), lerp(col, C.MARFIL, 0.25f), V3(-0.5f, 1f, -0.5f)).copy(alpha = 0.6f), style = Stroke(0.6f))
            }
            palo(v, V3(pie.x, 0f, pie.z), V3(pie.x, 1.88f, pie.z), 0.04f, madera)
            for (g in ganchos) { val pg = CamDeco.p(g); val pp = CamDeco.p(pie.x, 1.8f, pie.z); val ctl = Offset((pg.x + pp.x) / 2f, pg.y - 3f)
                drawPath(Path().apply { moveTo(pp.x, pp.y); cubicTo(ctl.x, ctl.y, pg.x, pg.y - 2f, pg.x, pg.y) }, v.c(g, madera, FRENTE), style = Stroke(0.025f * CamDeco.k(g.z), cap = StrokeCap.Round)) }
            drawCircle(v.c(V3(pie.x, 1.9f, pie.z), madera, ARRIBA), 0.03f * CamDeco.k(pie.z), CamDeco.p(pie.x, 1.9f, pie.z))
            // Los sombreros, en los ganchos de arriba.
            for (i in 0 until n) { val g = ganchos[i % 6]; val q = V3(g.x, g.y + 0.06f + (i / 6) * 0.08f, g.z - 0.02f); sombrero(v, q, 0f, colores[i % colores.size], cloche = i % 3 == 1) }
        }
    }

    // =================================================================
    //  Las cajas de gaseosa y el cubo de fregar
    // =================================================================

    /** Una pila de cajas de botellas de gaseosa (la Ley Seca: lo que se sirve a la vista), con sus botellas asomando. */
    fun cajas(v: VistaDeco, o: V3, giro: Float, pisos: Int): Cosa3 {
        val f = Marco3(o, giro); val l = 0.5f; val a = 0.26f; val d = 0.34f
        val esq = listOf(f.w(0f, 0f, 0f), f.w(l, 0f, 0f), f.w(0f, 0f, d), f.w(l, 0f, d), f.w(0f, a * pisos + 0.1f, 0f), f.w(l, a * pisos + 0.1f, d))
        return Cosa3(f.w(l / 2f, 0f, 0f).z, esq) {
            for (i in 0 until pisos) {
                val y0 = i * a; val dx = if (i % 2 == 1) 0.03f else 0f
                val madera = lerp(lerp(C.ROBLE, C.ACERA, 0.2f), C.CAOBA, (i % 2) * 0.2f)
                caja(v, f, dx, l + dx, y0, y0 + a, 0f, d, madera) { cara, forma ->
                    if (cara == 0 || cara == 1 || cara == 2) clipPath(forma) {
                        // Las tres tablas de cada cara, con sus rendijas, los cantos más oscuros y el asa recortada.
                        val (u0, u1, z0, z1) = if (cara == 0) listOf(dx, l + dx, 0f, 0f) else if (cara == 1) listOf(dx, dx, d, 0f) else listOf(l + dx, l + dx, 0f, d)
                        for (t in listOf(0.34f, 0.67f)) drawLine(C.SOMBRA.copy(alpha = 0.5f), f.p(u0, y0 + a * t, z0), f.p(u1, y0 + a * t, z1), 0.7f, blendMode = BlendMode.Multiply)
                        for (t in listOf(0.04f, 0.96f)) { val uu = u0 + (u1 - u0) * t; val zz = z0 + (z1 - z0) * t; drawLine(C.SOMBRA.copy(alpha = 0.35f), f.p(uu, y0, zz), f.p(uu, y0 + a, zz), 1.6f, blendMode = BlendMode.Multiply) }
                        drawLine(Color.White.copy(alpha = 0.12f), f.p(u0, y0 + a - 0.01f, z0), f.p(u1, y0 + a - 0.01f, z1), 0.5f)
                        if (cara == 0) {
                            drawOval(C.LACA.copy(alpha = 0.75f), Rect3(f.p(l / 2f + dx - 0.07f, y0 + a * 0.86f, 0f), f.p(l / 2f + dx + 0.07f, y0 + a * 0.74f, 0f)))
                            // La marca de la fábrica de gaseosa, estarcida: una estrella de coral, sin letras.
                            val c = f.p(l / 2f + dx, y0 + a * 0.42f, 0f); val r = 0.05f * CamDeco.k(f.w(0f, 0f, 0f).z)
                            drawPath(Path().apply { for (s in 0..9) { val ang = (-PI / 2 + s * PI / 5).toFloat(); val rr = if (s % 2 == 0) r else r * 0.42f; val q = c + Offset(cos(ang) * rr, sin(ang) * rr); if (s == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, v.c(f.w(l / 2f, y0 + a * 0.4f, 0f), C.CORAL, FRENTE).copy(alpha = 0.8f))
                        }
                    }
                }
            }
            // Las botellas de la caja de arriba: cuellos de vidrio verde con sus chapas.
            val y = pisos * a
            for (i in 0..4) for (j in 0..1) { val x = 0.07f + i * 0.09f; val z = 0.09f + j * 0.16f
                palo(v, f.w(x, y - 0.04f, z), f.w(x, y + 0.1f, z), 0.028f, lerp(C.JADE, C.CROMO, 0.3f))
                drawCircle(v.c(f.w(x, y + 0.1f, z), C.CORAL, ARRIBA), 0.018f * CamDeco.k(f.w(x, y, z).z), f.p(x, y + 0.105f, z)) }
        }
    }

    private fun Rect3(a: Offset, b: Offset): androidx.compose.ui.geometry.Rect = androidx.compose.ui.geometry.Rect(minOf(a.x, b.x), minOf(a.y, b.y), maxOf(a.x, b.x), maxOf(a.y, b.y))
    private fun DrawScope.drawOval(c: Color, r: androidx.compose.ui.geometry.Rect) = drawOval(c, r.topLeft, r.size)

    /** El cubo de fregar con su fregona, de la mañana de después. */
    fun cubo(v: VistaDeco, o: V3): Cosa3 {
        val esq = listOf(V3(o.x - 0.15f, 0f, o.z - 0.15f), V3(o.x + 0.15f, 0f, o.z + 0.15f), V3(o.x, 0.3f, o.z), V3(o.x + 0.5f, 1.2f, o.z + 0.2f))
        return Cosa3(o.z, esq) {
            val k = CamDeco.k(o.z); val p = CamDeco.p(o)
            // El palo de la fregona, apoyado en el cubo.
            palo(v, V3(o.x, 0.12f, o.z), V3(o.x + 0.45f, 1.25f, o.z + 0.25f), 0.025f, C.ROBLE)
            val cubo = Path().apply { moveTo(p.x - 0.13f * k, p.y - 0.27f * k); lineTo(p.x + 0.13f * k, p.y - 0.27f * k); lineTo(p.x + 0.11f * k, p.y); lineTo(p.x - 0.11f * k, p.y); close() }
            drawPath(cubo, Brush.horizontalGradient(0f to v.c(o, C.CROMO, V3(-1f, 0f, -0.3f)), 0.4f to v.c(o, lerp(C.CROMO, C.MARFIL, 0.3f), FRENTE), 1f to v.c(o, lerp(C.CROMO, C.HIERRO, 0.4f), V3(1f, 0f, -0.3f)), startX = p.x - 0.13f * k, endX = p.x + 0.13f * k))
            for (y in listOf(0.06f, 0.2f)) drawLine(v.c(o, lerp(C.CROMO, C.HIERRO, 0.5f), FRENTE), Offset(p.x - 0.12f * k, p.y - y * k), Offset(p.x + 0.12f * k, p.y - y * k), 0.5f)
            drawOval(v.c(o, lerp(C.ACERA, C.HUMO, 0.4f), ARRIBA), Offset(p.x - 0.12f * k, p.y - 0.29f * k), Size(0.24f * k, 0.05f * k))
            // Las tiras de la fregona, mojadas, asomando.
            for (i in 0..6) drawLine(v.c(o, lerp(C.MARFIL, C.ACERA, 0.5f), FRENTE), Offset(p.x - 0.06f * k + i * 0.02f * k, p.y - 0.27f * k), Offset(p.x - 0.08f * k + i * 0.025f * k, p.y - 0.15f * k), 0.8f, StrokeCap.Round)
            drawArc(v.c(o, C.HIERRO, FRENTE), 180f, 180f, false, Offset(p.x - 0.12f * k, p.y - 0.4f * k), Size(0.24f * k, 0.25f * k), style = Stroke(0.5f))
        }
    }

    /** El ventilador de techo (el día de calor y en verano): cuatro aspas de madera; si gira, se pintan borrosas en la parte viva. */
    fun DrawScope.ventiladorDeTecho(v: VistaDeco, techo: V3, giro: Float, quieto: Boolean) {
        val y = techo.y - 0.32f; val c = V3(techo.x, y, techo.z)
        palo(v, techo, c, 0.022f, C.LATON)
        drawPath(CamDeco.forma(aro(V3(c.x, c.y + 0.04f, c.z), 0.07f, 12)), v.c(c, C.LATON, FRENTE))
        if (quieto) for (i in 0..3) { val a = giro + i * PI.toFloat() / 2f
            val aspa = listOf(V3(c.x + 0.07f * cos(a - 0.2f), y, c.z + 0.07f * sin(a - 0.2f)), V3(c.x + 0.55f * cos(a - 0.12f), y, c.z + 0.55f * sin(a - 0.12f)), V3(c.x + 0.56f * cos(a + 0.1f), y, c.z + 0.56f * sin(a + 0.1f)), V3(c.x + 0.07f * cos(a + 0.2f), y, c.z + 0.07f * sin(a + 0.2f)))
            drawPath(CamDeco.forma(aspa), v.c(c, C.ROBLE_OSC, V3(0f, -1f, -0.3f))) }
        drawCircle(v.c(c, C.LATON, FRENTE), 0.05f * CamDeco.k(c.z), CamDeco.p(c.x, y - 0.03f, c.z))
    }

    /** Los ejes de [PI] en Float, para no repetir la cuenta. */
    private val PIf = PI.toFloat()

    /** La distancia de un punto a otro en el plano del suelo. */
    fun lejos(a: V3, b: V3): Float = sqrt((a.x - b.x) * (a.x - b.x) + (a.z - b.z) * (a.z - b.z))

    /** Para que una mancha no salga dos veces: un brillo suave y redondo en el lienzo. */
    fun DrawScope.brillo(p: Offset, r: Float, color: Color, alfa: Float) = mancha(p, r, r * 0.7f, color, alfa)
}
