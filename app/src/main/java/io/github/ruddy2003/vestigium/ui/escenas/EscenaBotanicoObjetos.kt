package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día del naturalista, iguales en los dos cuadros (el bergantín y la selva).
 * Cada cuadro dice dónde: los chicos van sobre su soporte (la mesa de trabajo de cubierta o la de la galería);
 * los grandes van de pie o apoyados (en el canto de la mesa, en la pared de la casa).
 *
 * - **Grandes** ([GRANDES], de 0.5 a 1.9 m, a [METRO] unidades por metro a escala 1): la hamaca enrollada y trincada,
 *   con sus arreos y su argolla; la red de mano (el mango largo, el aro y la bolsa de gasa, con una mariposa dentro);
 *   la escopeta de dos cañones, con el frasco de la pólvora colgado; y el cajón de especímenes a medio llenar, forrado
 *   de papel alquitranado, con la tapa apoyada detrás. Pisan en `pie` y se recuestan hacia `apoyo`; el cajón se queda
 *   en el suelo y es su tapa la que se apoya. Cada uno pinta su sombra en el suelo (los cuadros no la piden a la
 *   silueta de la [Cosa]) y devuelve la silueta por si un cuadro la quiere.
 * - **Chicos** (de 20 a 36 unidades a escala 1, sobre el tablero): la libreta abierta (una planta a tinta en una
 *   página y el perfil de una costa en la otra, con sus renglones), la lupa de bolsillo sobre un escarabajo, el frasco
 *   con un pulpo en alcohol, el martillo de geólogo con una piedra partida que enseña una concha fósil, la brújula de
 *   bolsillo con la tapa abierta, la carta abierta de letra cruzada con el atado de las demás, los limones (uno
 *   partido) en su plato de peltre y la galleta de barco junto a su talega. Van con su sombra corta.
 *
 * **[Diseño propio]**: el cuento dice que la hamaca «por la mañana se enrolla y se ata», así que va enrollada (también
 * en la selva, donde duerme en cama: es la del barco, guardada). El cajón lleva «un número pintado en la tapa»: aquí
 * son una flecha y tres rayas pintadas de blanco, sin cifras. La libreta y la carta van algo más levantadas hacia quien
 * mira de lo que daría la perspectiva (como en un cuadro), para que se lean sus dibujos.
 */
internal object ObjetosBotanico {
    private val C = ColorBotanico

    /** Los doce objetos del calendario del naturalista (`AnioG5.kt`). */
    val TODOS = listOf("hamaca", "libreta", "lupa", "red", "frasco", "escopeta", "martillo", "caja", "brujula", "carta", "limon", "galleta")

    /** Los que no caben sobre el soporte: van de pie o apoyados. */
    val GRANDES = setOf("hamaca", "red", "escopeta", "caja")

    /** Unidades del lienzo por metro, a escala 1 (la de Mongol: la galería de la selva a 5 m). */
    const val METRO = 60f

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val LIMON = lerp(C.LATON, Color(0xFFF2DC5A), 0.62f)                 // el amarillo del limón, hermano del latón
    private val SEPIA = lerp(C.CUERO, C.BREA, 0.4f)                             // la tinta
    private val NOGAL = lerp(C.MADERA, C.CAOBA, 0.5f)                           // la culata
    private val PELTRE = lerp(C.ROCA, C.LONA, 0.35f)
    private val ALCOHOL = lerp(C.ARENA, C.LATON, 0.3f)
    private val CORCHO = lerp(C.MADERA, C.ARENA, 0.45f)
    private val GALLETA = lerp(C.ARENA, C.CUERO, 0.32f)
    private val ALQUITRAN = lerp(C.BREA, C.CUERO, 0.18f)

    /** Si el objeto de hoy es de los grandes (no cabe sobre el soporte y va apoyado). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) = chico(m.ficha.objeto, p, sobre, escala)

    /** El objeto chico [id] sobre el soporte (lo mismo que [sobreElSoporte], sin el momento: para el muestrario y las pruebas). */
    fun DrawScope.chico(id: String?, p: Pincel, sobre: Offset, escala: Float = 1f) {
        if (id == null || id in GRANDES || id !in TODOS) return
        val k = escala
        when (id) {
            "libreta" -> libreta(p, sobre, k)
            "lupa" -> lupa(p, sobre, k)
            "frasco" -> frasco(p, sobre, k)
            "martillo" -> martillo(p, sobre, k)
            "brujula" -> brujula(p, sobre, k)
            "carta" -> carta(p, sobre, k)
            "limon" -> limones(p, sobre, k)
            "galleta" -> galleta(p, sobre, k)
        }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], un punto alto de la amurada o de la pared.
     * Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = grande(m.ficha.objeto, p, pie, apoyo, escala)

    /** El objeto grande [id] (lo mismo que el de arriba, sin el momento). */
    fun grande(id: String?, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (id == null || id !in GRANDES) return null
        val u = METRO * escala
        // Cuánto se recuesta: hacia el apoyo, sin pasar de un cuarto de vuelta pequeño (las cosas apoyadas no se tumban).
        val d = apoyo - pie
        val ang = if (d.getDistance() < 1f) 0f else atan2(d.x, -d.y).coerceIn(-0.3f, 0.3f)
        val hacia = if (d.x >= 0f) 1f else -1f
        return when (id) {
            "hamaca" -> { val e = Eje(pie, ang + 0.1f * hacia, u); val s = siluetaHamaca(e); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); hamaca(p, e) } }
            "red" -> { val e = Eje(pie, ang + 0.05f * hacia, u); val s = siluetaRed(e); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); red(p, e) } }
            "escopeta" -> { val e = Eje(pie, ang, u); val s = siluetaEscopeta(e); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); escopeta(p, e, hacia) } }
            "caja" -> { val e = Eje(pie, 0f, u); val s = siluetaCajon(e, apoyo); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); cajon(p, e, apoyo) } }
            else -> null
        }
    }

    // =================================================================
    //  Ayudas
    // =================================================================

    /** Los ejes de una cosa de pie en [pie], recostada [ang] radianes (positivo, hacia la derecha), con [u] unidades por metro. */
    private class Eje(val pie: Offset, val ang: Float, val u: Float) {
        private val c = cos(ang); private val s = sin(ang)
        /** Un punto de la cosa: [x] a su derecha, [y] hacia arriba, en metros. */
        fun q(x: Float, y: Float): Offset = Offset(pie.x + (x * c + y * s) * u, pie.y + (x * s - y * c) * u)
        fun girado(dAng: Float, desde: Offset = pie) = Eje(desde, ang + dAng, u)
    }

    /**
     * La cara de arriba del soporte, vista desde quien mira: [x] a la derecha, [z] hacia el fondo, [h] hacia arriba, en
     * unidades a escala 1. [aplasta] es cuánto se ve de lo hondo (los papeles van algo más levantados que lo demás).
     */
    private class Plano(val o: Offset, val k: Float, giro: Float = 0f, val aplasta: Float = 0.42f) {
        private val c = cos(giro); private val s = sin(giro)
        fun q(x: Float, z: Float, h: Float = 0f) = Offset(o.x + (x * c - z * s) * k, o.y - ((x * s + z * c) * aplasta + h) * k)
    }

    private fun lado(p: Pincel) = if (p.solALaDerecha) 1f else -1f

    /**
     * Un palo o un tubo que sigue los [puntos] con su medio ancho en cada uno (en unidades): oscuro del lado de la sombra,
     * el color propio en medio y un filo de luz del lado del sol.
     */
    private fun DrawScope.tubo(p: Pincel, puntos: List<Offset>, anchos: List<Float>, color: Color, brillo: Float = 0.75f): Path {
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for (i in puntos.indices) {
            val a = puntos[maxOf(i - 1, 0)]; val b = puntos[minOf(i + 1, puntos.size - 1)]
            val d = b - a; val l = d.getDistance().coerceAtLeast(0.001f); val n = Offset(-d.y / l, d.x / l)
            izq += puntos[i] + n * anchos[i]; der += puntos[i] - n * anchos[i]
        }
        val forma = poligono(izq + der.reversed())
        val L = lado(p)
        drawPath(forma, p.por(color, -0.55f))
        clipPath(forma) {
            for (i in 0 until puntos.size - 1) drawLine(p.por(color, 0.2f), puntos[i] + Offset(L * anchos[i] * 0.4f, 0f), puntos[i + 1] + Offset(L * anchos[i + 1] * 0.4f, 0f), anchos[i] + anchos[i + 1], StrokeCap.Round)
            for (i in 0 until puntos.size - 1) {
                val w = (anchos[i] + anchos[i + 1]) * 0.22f
                if (w > 0.18f) drawLine(p.luz(lerp(color, C.LONA, 0.25f)).copy(alpha = brillo), puntos[i] + Offset(L * anchos[i] * 0.62f, 0f), puntos[i + 1] + Offset(L * anchos[i + 1] * 0.62f, 0f), w, StrokeCap.Round)
            }
        }
        return forma
    }

    /** Un filo de luz cálida, de noche, del lado de la lumbre (que el pincel trae con su `calida`), sobre lo que es oscuro. */
    private fun DrawScope.filoCalido(p: Pincel, forma: Path, x0: Float, x1: Float, fuerza: Float = 0.45f) {
        if (p.calida < 0.03f) return
        val a = p.lumbre.copy(alpha = (fuerza * p.calida).coerceIn(0f, 1f))
        clipPath(forma) { drawRect(Brush.horizontalGradient(listOf(a, Color.Transparent, Color.Transparent, a.copy(alpha = a.alpha * 0.4f)), x0, x1), Offset(x0, -400f), Size(x1 - x0, 1200f), blendMode = BlendMode.Plus) }
    }

    /** La sombra corta de un objeto chico sobre el tablero: tendida hacia el lado contrario al sol, baja. */
    private fun DrawScope.sombrita(p: Pincel, pie: Offset, ancho: Float, alto: Float) {
        val r = p.r; if (r.fuerza < 0.05f) return
        val d = (r.ax * alto * 0.7f).coerceIn(-ancho * 1.2f, ancho * 1.2f)
        drawOval(r.color, Offset(minOf(pie.x - ancho / 2f, pie.x - ancho / 2f + d), pie.y - ancho * 0.12f), Size(ancho + abs(d), ancho * 0.22f), alpha = (r.fuerza * 0.6f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }

    /** La sombra de contacto de algo que se apoya: una mancha oscura justo debajo, que hay también sin sol. */
    private fun DrawScope.contacto(p: Pincel, c: Offset, ancho: Float, alto: Float, fuerza: Float = 0.45f) {
        drawOval(p.sombra(C.BREA).copy(alpha = fuerza), Offset(c.x - ancho / 2f, c.y - alto / 2f), Size(ancho, alto))
    }

    /** La sombra arrojada de un objeto grande sobre el suelo, desde su [forma] y su [pie] (la misma cuenta que `sombraArrojada`). */
    private fun DrawScope.sombraEnElSuelo(p: Pincel, forma: Path, pie: Offset) {
        val r = p.r; if (r.fuerza < 0.05f) return
        withTransform({ transform(Matrix().apply { values[4] = -r.ax; values[12] = r.ax * pie.y; values[5] = -r.ay; values[13] = pie.y * (1f + r.ay) }) }) {
            drawPath(forma, r.color, alpha = (r.fuerza * 0.62f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
        }
    }

    /** Unos renglones de escritura: rayitas de largo desigual, como palabras, sin letras. [de] y [a]: el principio y el fin del renglón. */
    private fun DrawScope.renglon(de: Offset, a: Offset, color: Color, grueso: Float, az: Azar, lleno: Float = 0.9f) {
        var t = 0f
        while (t < lleno) {
            val w = az.entre(0.06f, 0.2f); val fin = (t + w).coerceAtMost(lleno)
            drawLine(color, de + (a - de) * t, de + (a - de) * fin, grueso, StrokeCap.Round)
            t = fin + az.entre(0.03f, 0.06f)
        }
    }

    // =================================================================
    //  La hamaca, enrollada y trincada
    // =================================================================

    private const val LARGO_HAMACA = 1.3f
    private const val RADIO_HAMACA = 0.12f

    private fun siluetaHamaca(e: Eje): Path = Path().apply {
        val r = RADIO_HAMACA; val l = LARGO_HAMACA
        addPath(poligono(listOf(e.q(-r, 0.02f), e.q(r, 0.02f), e.q(r * 0.95f, l), e.q(0.02f, l + 0.36f), e.q(-r * 0.95f, l))))
    }

    private fun DrawScope.hamaca(p: Pincel, e: Eje) {
        val u = e.u; val L = lado(p); val r = RADIO_HAMACA; val l = LARGO_HAMACA
        val lona = lerp(C.LONA, C.CANAMO, 0.42f)
        val manta = lerp(C.TEJA, C.BREA, 0.25f)
        contacto(p, e.q(0f, 0.01f), 0.34f * u, 0.07f * u)
        // ---- El rabiza de la argolla, que baja por detrás y se adujó al pie.
        val cabo = lerp(C.CANAMO, C.BREA, 0.15f)
        val aduja = e.q(-0.2f * L, 0.02f)
        for (k in 0..2) drawOval(p.por(cabo, if (k == 1) 0.4f else -0.2f), Offset(aduja.x - (0.12f - k * 0.02f) * u, aduja.y - 0.035f * u - k * 0.012f * u), Size((0.24f - k * 0.04f) * u, 0.06f * u), style = Stroke(0.018f * u))
        drawPath(Path().apply { moveTo(e.q(0.02f, l + 0.34f)); cubicTo(e.q(-0.2f * L, l + 0.2f), e.q(-0.2f * L, 0.6f), aduja + Offset(0f, -0.02f * u)) }, p.sombra(cabo), style = Stroke(0.018f * u, cap = StrokeCap.Round))
        // ---- El rollo: la lona, ceñida en cada vuelta de la trinca y abombada entre ellas.
        val vueltas = 7
        val yv = (0 until vueltas).map { 0.12f + it * (l - 0.22f) / (vueltas - 1) }
        fun radio(y: Float): Float { val d = yv.minOf { abs(it - y) }; return r * (0.84f + 0.16f * (d / 0.1f).coerceAtMost(1f)) }
        val ys = (0..60).map { it * l / 60f }
        val cuerpo = poligono(ys.map { e.q(-radio(it), it) } + ys.reversed().map { e.q(radio(it), it) })
        val sombraL = p.sombra(lona); val luzL = p.luz(lona)
        drawPath(cuerpo, Brush.linearGradient(
            if (L > 0f) listOf(sombraL, p.por(lona, -0.5f), p.por(lona, 0.2f), luzL, p.por(lona, 0.3f)) else listOf(p.por(lona, 0.3f), luzL, p.por(lona, 0.2f), p.por(lona, -0.5f), sombraL),
            e.q(-r, l / 2f), e.q(r, l / 2f)))
        clipPath(cuerpo) {
            // La lona gastada: manchas de brea y de agua, y la costura del borde que corre a lo largo.
            mancha(e.q(0.02f, 0.45f), 0.06f * u, 0.14f * u, p.sombra(C.CANAMO), 0.3f)
            mancha(e.q(-0.04f, 1.1f), 0.05f * u, 0.09f * u, p.sombra(C.BREA), 0.2f)
            drawPath(Path().apply { moveTo(e.q(-0.05f * L, 0.05f)); cubicTo(e.q(-0.02f * L, 0.5f), e.q(-0.07f * L, 0.9f), e.q(-0.04f * L, l)) }, p.sombra(lona).copy(alpha = 0.6f), style = Stroke(0.009f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.025f * u, 0.016f * u))))
            // La manta roja asoma por la juntura de la lona, a lo largo.
            drawPath(Path().apply { moveTo(e.q(r * 0.62f * L, 0.02f)); cubicTo(e.q(r * 0.7f * L, 0.4f), e.q(r * 0.55f * L, 0.9f), e.q(r * 0.66f * L, l)) }, p.por(manta, 0.3f), style = Stroke(0.026f * u))
            // La trinca: siete vueltas de cabo que abrazan el rollo (se ven combadas: se miran desde arriba).
            for (y in yv) {
                val a = e.q(-r * 1.05f, y); val b = e.q(r * 1.05f, y); val m = e.q(0f, y - r * 0.45f)
                val vuelta = Path().apply { moveTo(a); cubicTo(a + (m - a) * 0.6f + (e.q(0f, -0.1f) - e.q(0f, 0f)) * 0.5f, b + (m - b) * 0.6f + (e.q(0f, -0.1f) - e.q(0f, 0f)) * 0.5f, b) }
                drawPath(vuelta, p.sombra(lona).copy(alpha = 0.8f), style = Stroke(0.045f * u))
                drawPath(vuelta, p.por(cabo, 0.1f), style = Stroke(0.026f * u, cap = StrokeCap.Round))
                drawPath(vuelta, p.luz(cabo).copy(alpha = 0.6f), style = Stroke(0.008f * u, cap = StrokeCap.Round))
            }
        }
        drawPath(cuerpo, p.sombra(lona).copy(alpha = 0.5f), style = Stroke(0.01f * u))
        // El pie del rollo: la cabeza de la lona, con las capas en espiral y la manta roja de en medio.
        val cab = e.q(0f, 0.04f)
        drawOval(p.por(lona, -0.3f), Offset(cab.x - r * u, cab.y - 0.045f * u), Size(2f * r * u, 0.09f * u))
        drawOval(p.por(manta, 0.1f), Offset(cab.x - r * 0.7f * u, cab.y - 0.032f * u), Size(1.4f * r * u, 0.064f * u))
        drawOval(p.sombra(manta), Offset(cab.x - r * 0.3f * u, cab.y - 0.014f * u), Size(0.6f * r * u, 0.028f * u))
        // ---- La cabeza de arriba: los arreos (doce cabitos en abanico) que van de la lona a la argolla de hierro.
        val argolla = e.q(0.02f, l + 0.34f)
        for (j in 0 until 11) {
            val x = -r * 0.85f + j * (r * 1.7f / 10f)
            val a = e.q(x, l - 0.02f)
            drawLine(p.sombra(cabo).copy(alpha = 0.6f), a + Offset(0.5f, 0.6f), argolla + Offset(0.5f, 0.6f), (0.014f * u).coerceAtLeast(0.5f))
            drawLine(p.por(cabo, if (j % 3 == 0) 0.5f else 0.1f), a, argolla, (0.011f * u).coerceAtLeast(0.45f))
        }
        // El ojal de la lona, rematado, y la argolla.
        drawLine(p.por(lona, 0.2f), e.q(-r * 0.95f, l - 0.01f), e.q(r * 0.95f, l - 0.01f), 0.03f * u, StrokeCap.Round)
        drawCircle(p.sombra(C.HIERRO), 0.05f * u, argolla, style = Stroke(0.022f * u))
        drawCircle(p.luz(lerp(C.HIERRO, C.LONA, 0.4f)).copy(alpha = 0.7f), 0.05f * u, argolla + Offset(L * 0.005f * u, -0.005f * u), style = Stroke(0.007f * u))
        filoCalido(p, cuerpo, e.q(-r, 0.7f).x, e.q(r, 0.7f).x, 0.3f)
    }

    // =================================================================
    //  La red de mano
    // =================================================================

    private const val MANGO = 1.42f
    private const val ARO = 0.2f

    private fun siluetaRed(e: Eje): Path {
        val c = e.q(0f, MANGO + ARO * 0.75f)
        return union(listOf(
            poligono(e.q(-0.02f, 0f), e.q(0.02f, 0f), e.q(0.02f, MANGO), e.q(-0.02f, MANGO)),
            poligono(listOf(c + Offset(-ARO * e.u, 0f), c + Offset(0f, -ARO * 0.75f * e.u), c + Offset(ARO * e.u, 0f), c + Offset(ARO * 0.5f * e.u, 0.65f * e.u), c + Offset(-ARO * 0.6f * e.u, 0.3f * e.u))),
        ))
    }

    private fun DrawScope.red(p: Pincel, e: Eje) {
        val u = e.u; val L = lado(p)
        val vara = lerp(C.MADERA, C.ARENA, 0.3f)
        contacto(p, e.q(0f, 0f), 0.1f * u, 0.035f * u)
        // ---- El mango: una vara de fresno, más oscura donde se agarra, con la contera de latón abajo y el casquillo arriba.
        val puntos = (0..8).map { e.q(0.004f * sin(it * 0.8f), it * MANGO / 8f) }
        val anchos = (0..8).map { (0.017f - 0.004f * it / 8f) * u }
        tubo(p, puntos, anchos, vara, 0.8f)
        drawLine(p.sombra(C.CUERO).copy(alpha = 0.45f), e.q(0f, 0.55f), e.q(0f, 0.85f), 0.034f * u, StrokeCap.Round)
        drawLine(p.por(C.LATON, 0.3f), e.q(0f, 0.0f), e.q(0f, 0.05f), 0.04f * u)
        drawLine(p.por(C.LATON, 0.4f), e.q(0f, MANGO - 0.07f), e.q(0f, MANGO + 0.01f), 0.036f * u, StrokeCap.Round)
        // ---- El aro, de alambre, visto un poco de lado (se recuesta).
        val c = e.q(0f, MANGO + ARO * 0.75f)
        val rx = ARO * u; val ry = ARO * 0.72f * u
        val gasa = lerp(C.LONA, C.ARENA, 0.15f)
        // La mitad de atrás del aro, primero.
        drawArc(p.sombra(C.HIERRO), 180f, 180f, false, Offset(c.x - rx, c.y - ry), Size(2f * rx, 2f * ry), style = Stroke(0.012f * u))
        // ---- La bolsa: gasa fina que cuelga del aro, con sus pliegues; se ve a través.
        val fondo = c + Offset(L * 0.05f * u, 0.62f * u)
        val bolsa = Path().apply {
            moveTo(c + Offset(-rx, 0f)); cubicTo(c + Offset(-rx * 1.05f, 0.25f * u), fondo + Offset(-0.12f * u, -0.08f * u), fondo)
            cubicTo(fondo + Offset(0.1f * u, -0.05f * u), c + Offset(rx * 1.02f, 0.2f * u), c + Offset(rx, 0f))
            cubicTo(c + Offset(rx, ry * 0.8f), c + Offset(-rx, ry * 0.8f), c + Offset(-rx, 0f)); close()
        }
        drawPath(bolsa, p.por(gasa, 0.2f * L).copy(alpha = 0.62f))
        clipPath(bolsa) {
            // La malla: dos familias de hilos finos, apenas.
            for (j in -12..12) {
                val a = c + Offset(j * 0.022f * u, -0.05f * u); drawLine(p.sombra(gasa).copy(alpha = 0.22f), a, a + Offset(0.12f * u, 0.7f * u), 0.35f)
                drawLine(p.sombra(gasa).copy(alpha = 0.22f), a, a + Offset(-0.12f * u, 0.7f * u), 0.35f)
            }
            // Los pliegues, en luz y en sombra.
            for ((x, s) in listOf(-0.1f to -1f, -0.02f to 1f, 0.07f to -1f)) drawPath(Path().apply { moveTo(c + Offset(x * u, 0.04f * u)); cubicTo(c + Offset(x * u, 0.25f * u), fondo + Offset(x * 0.5f * u, -0.18f * u), fondo + Offset(x * 0.2f * u, -0.02f * u)) }, (if (s * L > 0f) p.luz(gasa) else p.sombra(gasa)).copy(alpha = 0.5f), style = Stroke(0.018f * u, cap = StrokeCap.Round))
            // Una mariposa azul cogida en el fondo de la bolsa, que se ve a través de la gasa.
            val mc = fondo + Offset(-0.03f * u, -0.12f * u); val mk = 0.045f * u
            val azul = lerp(Color(0xFF3E78C8), C.ESCARABAJO, 0.2f)
            for (s in listOf(-1f, 1f)) {
                drawOval(p.por(azul, 0.3f * s * L).copy(alpha = 0.8f), Offset(minOf(mc.x, mc.x + s * mk * 1.2f), mc.y - mk), Size(mk * 1.2f, mk))
                drawOval(p.sombra(lerp(azul, C.BREA, 0.3f)).copy(alpha = 0.8f), Offset(minOf(mc.x, mc.x + s * mk), mc.y - mk * 0.1f), Size(mk, mk * 0.7f))
            }
            // La gasa por delante vela un poco lo de dentro.
            drawPath(bolsa, p.luz(gasa).copy(alpha = 0.18f))
        }
        drawPath(bolsa, p.sombra(gasa).copy(alpha = 0.45f), style = Stroke(0.006f * u))
        // La mitad de delante del aro, con su filo de luz, y la vaina de tela cosida al aro.
        drawArc(p.por(C.HIERRO, 0.2f), 0f, 180f, false, Offset(c.x - rx, c.y - ry), Size(2f * rx, 2f * ry), style = Stroke(0.014f * u))
        drawArc(p.luz(lerp(C.HIERRO, C.LONA, 0.5f)).copy(alpha = 0.6f), 20f, 140f, false, Offset(c.x - rx, c.y - ry - 0.004f * u), Size(2f * rx, 2f * ry), style = Stroke(0.004f * u))
        drawArc(p.por(lerp(gasa, C.CANAMO, 0.4f), 0f), 0f, 180f, false, Offset(c.x - rx, c.y - ry + 0.01f * u), Size(2f * rx, 2f * ry), style = Stroke(0.012f * u))
    }

    // =================================================================
    //  La escopeta de caza, de dos cañones
    // =================================================================

    private const val LARGO_ESCOPETA = 1.24f
    /** Cuánto se ensancha la escopeta (no su largo), para que se lea a tamaño de teléfono. */
    private const val GRUESO_ESCOPETA = 1.7f

    private fun siluetaEscopeta(e: Eje): Path = Path().apply {
        fun q(x: Float, y: Float) = e.q(x * GRUESO_ESCOPETA, y)
        addPath(poligono(listOf(q(-0.065f, 0f), q(0.07f, 0f), q(0.035f, 0.36f), q(0.03f, LARGO_ESCOPETA), q(-0.005f, LARGO_ESCOPETA), q(-0.01f, 0.4f))))
    }

    /** [hacia]: el lado del apoyo (1 a la derecha); el frasco de la pólvora cuelga del otro. */
    private fun DrawScope.escopeta(p: Pincel, e: Eje, hacia: Float) {
        val L = lado(p); val g = GRUESO_ESCOPETA; val u = e.u * g
        fun q(x: Float, y: Float) = e.q(x * g, y)
        contacto(p, q(0f, 0.005f), 0.18f * u, 0.04f * u)
        // ---- La culata de nogal: la cantonera abajo, el lomo, la garganta delgada y la caja donde entran las llaves.
        val culata = Path().apply {
            moveTo(q(-0.062f, 0.01f)); lineTo(q(0.068f, 0.01f))
            cubicTo(q(0.06f, 0.12f), q(0.04f, 0.26f), q(0.03f, 0.34f))           // el lomo, de la cantonera a la garganta
            lineTo(q(0.032f, 0.52f)); lineTo(q(0.014f, 0.86f)); lineTo(q(-0.006f, 0.86f))
            lineTo(q(-0.016f, 0.5f))
            cubicTo(q(-0.02f, 0.42f), q(-0.006f, 0.38f), q(-0.008f, 0.33f))        // el guardamonte va aquí
            cubicTo(q(-0.03f, 0.22f), q(-0.05f, 0.1f), q(-0.062f, 0.01f)); close()
        }
        drawPath(culata, Brush.linearGradient(if (L > 0f) listOf(p.por(NOGAL, -0.7f), p.por(NOGAL, 0.1f), p.por(NOGAL, 0.6f)) else listOf(p.por(NOGAL, 0.6f), p.por(NOGAL, 0.1f), p.por(NOGAL, -0.7f)), q(-0.06f, 0.2f), q(0.07f, 0.2f)))
        clipPath(culata) {
            // La veta del nogal, que sigue la culata; el brillo del aceite; el roce de la mejilla.
            for (j in 0 until 6) drawPath(Path().apply { moveTo(q(-0.05f + j * 0.02f, 0.02f)); cubicTo(q(-0.03f + j * 0.016f, 0.12f), q(-0.04f + j * 0.014f, 0.22f), q(-0.01f + j * 0.008f, 0.34f)) }, p.sombra(lerp(NOGAL, C.BREA, 0.3f)).copy(alpha = 0.45f), style = Stroke(0.004f * u))
            drawLine(p.luz(lerp(NOGAL, C.LONA, 0.3f)).copy(alpha = 0.55f), q(0.05f * L, 0.05f), q(0.025f * L, 0.3f), 0.012f * u, StrokeCap.Round)
            mancha(q(0.02f, 0.2f), 0.02f * u, 0.06f * u, p.luz(NOGAL), 0.2f)
        }
        // La cantonera de hierro, abajo.
        drawLine(p.por(C.HIERRO, 0.1f), q(-0.064f, 0.008f), q(0.07f, 0.008f), 0.016f * u, StrokeCap.Round)
        // ---- Los dos cañones, pavonados de pardo, con la banda de en medio; la baqueta debajo.
        val pavon = lerp(C.HIERRO, C.CUERO, 0.28f)
        drawLine(p.por(lerp(C.MADERA, C.ARENA, 0.2f), 0.2f), q(-0.014f, 0.62f), q(-0.01f, LARGO_ESCOPETA - 0.03f), 0.007f * u, StrokeCap.Round)
        val canones = listOf(-0.002f, 0.016f).map { x ->
            tubo(p, listOf(q(x, 0.5f), q(x + 0.002f, LARGO_ESCOPETA)), listOf(0.0105f * u, 0.0085f * u), pavon, 0.9f)
        }
        drawLine(p.sombra(pavon), q(0.007f, 0.52f), q(0.008f, LARGO_ESCOPETA), 0.004f * u)
        // Las bocas, que se ven de arriba.
        for (x in listOf(-0.002f, 0.018f)) { val b = q(x, LARGO_ESCOPETA); drawOval(p.sombra(C.BREA), Offset(b.x - 0.008f * u, b.y - 0.004f * u), Size(0.016f * u, 0.008f * u)) }
        // El guardamanos de madera, debajo de los cañones; las abrazaderas.
        drawPath(poligono(q(-0.014f, 0.52f), q(0.02f, 0.52f), q(0.012f, 0.85f), q(-0.012f, 0.85f)), p.por(NOGAL, -0.1f * L))
        for (y in listOf(0.84f, 1.06f)) drawLine(p.por(C.LATON, 0.2f), q(-0.016f, y), q(0.03f, y), 0.01f * u)
        // ---- Las llaves: las chapas grabadas, los dos martillos de pistón y el guardamonte.
        val chapa = poligono(listOf(q(-0.012f, 0.4f), q(0.034f, 0.4f), q(0.034f, 0.52f), q(0.016f, 0.56f), q(-0.012f, 0.52f)))
        drawPath(chapa, Brush.linearGradient(listOf(p.por(C.HIERRO, -0.2f), p.luz(lerp(C.HIERRO, C.LONA, 0.3f)), p.por(C.HIERRO, -0.3f)), q(0f, 0.4f), q(0f, 0.56f)))
        clipPath(chapa) { for (j in 0..2) drawCircle(p.sombra(C.HIERRO).copy(alpha = 0.6f), 0.012f * u, q(0.01f, 0.45f + j * 0.015f), style = Stroke(0.002f * u)) }
        for ((x, s) in listOf(-0.016f to -1f, 0.038f to 1f)) {
            val b = q(x, 0.5f)
            val martillo = Path().apply { moveTo(b); cubicTo(b + Offset(s * 0.02f * u, -0.01f * u), q(x + s * 0.03f, 0.56f), q(x + s * 0.012f, 0.6f)); lineTo(q(x, 0.585f)); close() }
            drawPath(martillo, p.por(C.HIERRO, 0.3f * s * L))
            drawCircle(p.luz(lerp(C.HIERRO, C.LONA, 0.5f)).copy(alpha = 0.7f), 0.005f * u, q(x + s * 0.012f, 0.598f))
        }
        val guarda = Path().apply { moveTo(q(-0.012f, 0.42f)); cubicTo(q(-0.06f, 0.42f), q(-0.06f, 0.33f), q(-0.01f, 0.3f)) }
        drawPath(guarda, p.por(C.LATON, 0.2f), style = Stroke(0.008f * u, cap = StrokeCap.Round))
        drawLine(p.sombra(C.HIERRO), q(-0.012f, 0.4f), q(-0.034f, 0.37f), 0.005f * u, StrokeCap.Round)
        val forma = Path().apply { addPath(culata); for (c in canones) addPath(c) }
        filoCalido(p, forma, q(-0.07f, 0.5f).x, q(0.07f, 0.5f).x, 0.55f)
        // ---- El frasco de la pólvora, de cobre repujado, colgado de su cordón verde de un cañón.
        // La correa de cuero: de la culata a la abrazadera, suelta, en un bucle por el lado de fuera.
        val s = -hacia
        val correa = Path().apply { moveTo(q(-0.02f, 0.2f)); cubicTo(q(-0.02f + s * 0.12f, 0.35f), q(s * 0.13f, 0.75f), q(0.0f, 1.06f)) }
        drawPath(correa, p.sombra(C.CUERO), style = Stroke(0.02f * u, cap = StrokeCap.Round))
        drawPath(correa, p.por(C.CUERO, 0.3f * s * L), style = Stroke(0.013f * u, cap = StrokeCap.Round))
        drawCircle(p.por(C.LATON, 0.3f), 0.008f * u, q(-0.02f, 0.2f), style = Stroke(0.004f * u))
        val u2 = u / g * 1.15f                                         // el frasco de la pólvora, a su tamaño (algo crecido)
        val nudo = q(0.004f, 0.98f)
        val frasco = nudo + Offset(s * 0.1f * u2, 0.24f * u2)
        val cordon = lerp(C.ESCARABAJO, C.SELVA, 0.3f)
        drawPath(Path().apply { moveTo(nudo); cubicTo(nudo + Offset(s * 0.04f * u2, 0.06f * u2), frasco + Offset(-s * 0.02f * u2, -0.16f * u2), frasco + Offset(-0.03f * u2, -0.08f * u2)) }, p.medio(cordon), style = Stroke(0.007f * u2, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(nudo); cubicTo(nudo + Offset(s * 0.07f * u2, 0.04f * u2), frasco + Offset(s * 0.06f * u2, -0.14f * u2), frasco + Offset(0.03f * u2, -0.08f * u2)) }, p.sombra(cordon), style = Stroke(0.007f * u2, cap = StrokeCap.Round))
        drawCircle(p.medio(cordon), 0.012f * u2, nudo)
        val cobre = lerp(C.LATON, C.TEJA, 0.38f)
        val pera = Path().apply {
            moveTo(frasco + Offset(-0.022f * u2, -0.085f * u2)); lineTo(frasco + Offset(0.022f * u2, -0.085f * u2))
            cubicTo(frasco + Offset(0.07f * u2, -0.05f * u2), frasco + Offset(0.075f * u2, 0.06f * u2), frasco + Offset(0f, 0.07f * u2))
            cubicTo(frasco + Offset(-0.075f * u2, 0.06f * u2), frasco + Offset(-0.07f * u2, -0.05f * u2), frasco + Offset(-0.022f * u2, -0.085f * u2)); close()
        }
        drawPath(pera, Brush.linearGradient(if (L > 0f) listOf(p.por(cobre, -0.6f), p.por(cobre, 0.2f), p.luz(lerp(cobre, C.LONA, 0.3f))) else listOf(p.luz(lerp(cobre, C.LONA, 0.3f)), p.por(cobre, 0.2f), p.por(cobre, -0.6f)), frasco + Offset(-0.07f * u2, 0f), frasco + Offset(0.07f * u2, 0f)))
        clipPath(pera) {
            // El repujado: una concha en el centro.
            for (j in -2..2) drawLine(p.sombra(cobre).copy(alpha = 0.6f), frasco + Offset(0f, 0.04f * u2), frasco + Offset(j * 0.016f * u2, -0.03f * u2), 0.004f * u2)
            drawCircle(Color.White.copy(alpha = 0.45f * (1f - 0.6f * p.l.noche)), 0.01f * u2, frasco + Offset(L * 0.035f * u2, -0.03f * u2))
        }
        drawLine(p.por(C.LATON, 0.4f), frasco + Offset(0f, -0.085f * u2), frasco + Offset(0f, -0.125f * u2), 0.014f * u2, StrokeCap.Round)
        filoCalido(p, pera, frasco.x - 0.07f * u2, frasco.x + 0.07f * u2, 0.4f)
    }

    // =================================================================
    //  El cajón de especímenes, a medio llenar
    // =================================================================

    private const val CAJ_ANCHO = 0.68f
    private const val CAJ_ALTO = 0.44f

    /** Hacia dónde se ve lo hondo del cajón: hacia el centro del cuadro (si está a la derecha, se le ve el costado izquierdo). */
    private fun hondo(e: Eje): Offset = Offset(if (e.pie.x > 190f) -0.15f else 0.15f, -0.17f) * e.u

    /** La tapa suelta, de pie detrás del cajón y algo a un lado (abajo-izquierda, abajo-derecha, arriba-derecha, arriba-izquierda). */
    private fun tapaDelCajon(e: Eje): List<Offset> {
        val h = hondo(e); val u = e.u; val bc = e.q(if (h.x < 0f) 0.16f else -0.16f, 0f) + h * 1.1f
        val tw = 0.25f * u; val th = 0.74f * u; val inclina = 0.05f * u * (if (h.x < 0f) 1f else -1f)
        return listOf(bc + Offset(-tw, 0f), bc + Offset(tw, 0f), bc + Offset(tw + inclina, -th), bc + Offset(-tw + inclina, -th))
    }

    private fun siluetaCajon(e: Eje, apoyo: Offset): Path {
        val h = hondo(e); val w = CAJ_ANCHO / 2f; val a = CAJ_ALTO
        val cuerpo = if (h.x < 0f) listOf(e.q(-w, 0f), e.q(w, 0f), e.q(w, a), e.q(w, a) + h, e.q(-w, a) + h, e.q(-w, 0f) + h)
        else listOf(e.q(-w, 0f), e.q(w, 0f), e.q(w, 0f) + h, e.q(w, a) + h, e.q(-w, a) + h, e.q(-w, a))
        // La tapa, apoyada detrás: un rectángulo alto hasta el apoyo.
        return union(listOf(poligono(cuerpo), poligono(tapaDelCajon(e))))
    }

    private fun DrawScope.cajon(p: Pincel, e: Eje, apoyo: Offset) {
        val u = e.u; val L = lado(p); val h = hondo(e); val w = CAJ_ANCHO / 2f; val a = CAJ_ALTO
        val az = Azar(Semilla.de("botanico", "objeto", "cajon"))
        val izq = h.x < 0f
        // ---- La tapa, suelta, apoyada detrás contra el canto, con su marca pintada.
        val tq = tapaDelCajon(e)
        val tapa = poligono(tq)
        val tablas = lerp(C.MADERA, C.ARENA, 0.15f)
        drawPath(tapa, p.por(tablas, -0.15f * L))
        clipPath(tapa) {
            // Tres tablas, a lo alto.
            for (j in 1..2) drawLine(p.sombra(tablas).copy(alpha = 0.6f), tq[0] + (tq[1] - tq[0]) * (j / 3f), tq[3] + (tq[2] - tq[3]) * (j / 3f), 0.006f * u)
            // La marca, arriba, donde se ve: una flecha y tres rayas en blanco, pintadas con plantilla (sin cifras).
            val mc = tq[0] + (tq[1] - tq[0]) * 0.5f + (tq[3] - tq[0]) * 0.8f; val blanco = p.por(lerp(C.LONA, Color.White, 0.3f), 0.2f).copy(alpha = 0.88f)
            drawLine(blanco, mc + Offset(-0.12f * u, 0.07f * u), mc + Offset(-0.12f * u, -0.04f * u), 0.024f * u)
            drawPath(poligono(mc + Offset(-0.175f * u, -0.03f * u), mc + Offset(-0.12f * u, -0.1f * u), mc + Offset(-0.065f * u, -0.03f * u)), blanco)
            for (j in 0..2) drawLine(blanco, mc + Offset((-0.01f + j * 0.06f) * u, -0.09f * u), mc + Offset((-0.01f + j * 0.06f) * u, 0.07f * u), 0.026f * u)
            // Los listones de atrás, clavados de través.
            for (v in listOf(0.12f, 0.95f)) drawLine(p.por(tablas, -0.3f), tq[0] + (tq[3] - tq[0]) * v, tq[1] + (tq[2] - tq[1]) * v, 0.05f * u)
        }
        drawPath(tapa, p.sombra(C.BREA).copy(alpha = 0.5f), style = Stroke(0.008f * u))
        contacto(p, e.q(0f, 0f) + h * 0.45f, (CAJ_ANCHO + 0.25f) * u, 0.12f * u, 0.4f)
        // ---- El cuerpo: la cara de delante y el costado que se ve, forrados de papel alquitranado, con listones en las aristas.
        val frente = poligono(e.q(-w, 0f), e.q(w, 0f), e.q(w, a), e.q(-w, a))
        val costado = if (izq) poligono(e.q(-w, 0f), e.q(-w, 0f) + h, e.q(-w, a) + h, e.q(-w, a)) else poligono(e.q(w, 0f), e.q(w, 0f) + h, e.q(w, a) + h, e.q(w, a))
        // El frente mira a quien mira; el costado, al lado contrario de lo hondo.
        val tFrente = 0.1f; val tCostado = if (izq) -0.6f * L else 0.6f * L
        for ((cara, t) in listOf(frente to tFrente, costado to tCostado)) {
            drawPath(cara, p.por(ALQUITRAN, t))
            clipPath(cara) {
                // El papel alquitranado: brillo de brea, pliegues, desgarrones por donde asoma la madera.
                repeat(5) { val c = e.q(az.entre(-w, w), az.entre(0.05f, a)) + h * az.entre(0f, 1f); mancha(c, az.entre(0.04f, 0.1f) * u, az.entre(0.02f, 0.05f) * u, p.luz(lerp(ALQUITRAN, C.LONA, 0.25f)), 0.22f) }
                for (j in 0..2) { val c = e.q(az.entre(-w * 0.8f, w * 0.8f), az.entre(0.1f, a - 0.1f)); drawPath(poligono(c, c + Offset(az.entre(0.03f, 0.07f) * u, az.entre(-0.02f, 0.02f) * u), c + Offset(az.entre(0.02f, 0.05f) * u, az.entre(0.03f, 0.06f) * u)), p.por(tablas, t)) }
                drawLine(p.sombra(C.BREA).copy(alpha = 0.6f), e.q(-w, 0.03f), e.q(w, 0.03f), 0.05f * u)
            }
        }
        // Los listones de las aristas, con sus clavos.
        for (x in listOf(-w, w)) drawLine(p.por(tablas, if (x * L > 0f) 0.5f else -0.1f), e.q(x - 0.02f * x / w, 0f), e.q(x - 0.02f * x / w, a), 0.05f * u)
        drawLine(p.por(tablas, 0.3f), e.q(-w, a - 0.02f), e.q(w, a - 0.02f), 0.045f * u)
        drawLine(p.por(tablas, -0.1f), e.q(-w, 0.03f), e.q(w, 0.03f), 0.04f * u)
        for (x in listOf(-w + 0.02f, w - 0.02f)) for (y in listOf(0.04f, a - 0.03f)) drawCircle(p.luz(lerp(C.HIERRO, C.LONA, 0.3f)), 0.008f * u, e.q(x, y))
        // La gaza de cabo del costado, para cargarlo.
        val gz = if (izq) e.q(-w, a * 0.6f) + h * 0.5f else e.q(w, a * 0.6f) + h * 0.5f
        drawArc(p.por(C.CANAMO, 0.2f), 0f, 180f, false, Offset(gz.x - 0.06f * u, gz.y - 0.05f * u), Size(0.12f * u, 0.1f * u), style = Stroke(0.018f * u, cap = StrokeCap.Round))
        // ---- La boca: el canto de arriba y, dentro, la estopa con los corchos de los frascos y un paquete de papel.
        val boca = poligono(e.q(-w, a), e.q(w, a), e.q(w, a) + h, e.q(-w, a) + h)
        drawPath(boca, p.sombra(lerp(tablas, C.BREA, 0.4f)))
        clipPath(boca) {
            val estopa = lerp(C.CANAMO, C.ARENA, 0.3f)
            val base = e.q(0f, a) + h * 0.5f
            // La estopa: un colchón de fibras que llena la boca, más en sombra hacia el canto de delante.
            drawPath(boca, p.por(estopa, -0.05f))
            drawRect(Brush.verticalGradient(listOf(p.sombra(C.BREA).copy(alpha = 0f), p.sombra(C.BREA).copy(alpha = 0.45f)), base.y - 0.04f * u, e.q(0f, a).y), Offset(base.x - u, base.y - 0.04f * u), Size(2f * u, 0.2f * u))
            repeat(170) { val c = e.q(az.entre(-w, w), a) + h * az.entre(0.05f, 0.95f); val d = Offset(az.entre(-0.06f, 0.06f) * u, az.entre(-0.025f, 0.025f) * u)
                drawPath(Path().apply { moveTo(c); cubicTo(c + d * 0.3f + Offset(0f, -0.01f * u), c + d * 0.7f + Offset(0f, 0.01f * u), c + d) }, p.por(estopa, az.entre(-0.5f, 0.8f)), style = Stroke(0.006f * u, cap = StrokeCap.Round)) }
            // Los corchos de cuatro frascos, metidos en la estopa.
            for ((x, f) in listOf(-0.2f to 0.35f, -0.08f to 0.55f, 0.06f to 0.3f, 0.12f to 0.65f)) {
                val c = e.q(x, a) + h * f
                drawOval(p.por(lerp(C.LONA, C.MAR, 0.15f), 0.1f).copy(alpha = 0.6f), Offset(c.x - 0.04f * u, c.y - 0.015f * u), Size(0.08f * u, 0.04f * u))
                drawOval(p.por(CORCHO, 0.3f), Offset(c.x - 0.022f * u, c.y - 0.02f * u), Size(0.044f * u, 0.024f * u))
            }
            // Un pájaro envuelto en papel, con su etiqueta atada.
            val pq = e.q(-w * 0.55f * (if (izq) -1f else 1f), a) + h * 0.55f
            val paquete = poligono(pq + Offset(-0.1f * u, 0.02f * u), pq + Offset(0.06f * u, 0.035f * u), pq + Offset(0.1f * u, -0.01f * u), pq + Offset(-0.07f * u, -0.035f * u))
            drawPath(paquete, Brush.verticalGradient(listOf(p.luz(C.PAPEL), p.por(C.PAPEL, -0.3f)), pq.y - 0.035f * u, pq.y + 0.035f * u))
            drawLine(p.por(C.CANAMO, -0.2f), pq + Offset(-0.02f * u, -0.03f * u), pq + Offset(0.0f * u, 0.03f * u), 0.006f * u)
            // La etiqueta atada, que asoma.
            drawPath(poligono(pq + Offset(0.1f * u, -0.01f * u), pq + Offset(0.15f * u, 0.0f), pq + Offset(0.14f * u, 0.025f * u), pq + Offset(0.095f * u, 0.015f * u)), p.luz(C.LONA))
        }
        // El canto de la boca: tablas claras, recién aserradas.
        drawPath(boca, p.luz(tablas).copy(alpha = 0.85f), style = Stroke(0.02f * u))
        filoCalido(p, Path().apply { addPath(frente); addPath(costado) }, e.q(-w, 0f).x - 0.2f * u, e.q(w, 0f).x + 0.2f * u, 0.4f)
    }

    // =================================================================
    //  Los chicos, sobre el tablero
    // =================================================================

    /** La libreta de bolsillo, abierta: una planta a tinta en la página de la izquierda, el perfil de una costa y sus renglones en la otra. */
    private fun DrawScope.libreta(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val pl = Plano(o, k, -0.08f, 0.62f)
        val az = Azar(Semilla.de("botanico", "objeto", "libreta"))
        sombrita(p, o, 34f * k, 3f * k)
        // ---- Las tapas de cuero, gastadas en las esquinas, y el grueso de las hojas.
        val cuero = lerp(C.CUERO, C.BREA, 0.3f)
        val tapas = poligono(pl.q(-17f, -10.5f), pl.q(17f, -10.5f), pl.q(17f, 10.5f), pl.q(-17f, 10.5f))
        drawPath(Path().apply { addPath(tapas, Offset(0f, 1.1f * k)) }, p.sombra(cuero))
        drawPath(tapas, p.por(cuero, 0.1f))
        for (c in listOf(pl.q(-17f, -10.5f), pl.q(17f, -10.5f))) drawCircle(p.luz(lerp(cuero, C.ARENA, 0.4f)).copy(alpha = 0.6f), 1.2f * k, c)
        val papel = lerp(C.PAPEL, C.LONA, 0.35f)
        drawPath(poligono(pl.q(-16f, -9.6f, 0f), pl.q(16f, -9.6f, 0f), pl.q(16f, -9.6f, 1.2f), pl.q(-16f, -9.6f, 1.2f)), p.por(lerp(papel, C.ARENA, 0.4f), -0.1f))
        // ---- Las dos páginas, que se abomban y se hunden en el lomo.
        fun h(x: Float) = 1.2f + 1.5f * sin(PI.toFloat() * (abs(x) / 16f).coerceIn(0f, 1f)) - (if (abs(x) < 2f) (2f - abs(x)) * 0.5f else 0f)
        fun pagina(s: Float): Path {
            val xs = (0..8).map { s * (16f - it * 2f) }
            return poligono(xs.map { pl.q(it, -9.6f, h(it)) } + xs.reversed().map { pl.q(it, 9.6f, h(it)) })
        }
        val tinta = p.por(SEPIA, 0.1f)
        for (s in listOf(-1f, 1f)) {
            val pg = pagina(s)
            // La página de la izquierda mira al lomo, hacia la derecha; la de la derecha, hacia la izquierda.
            val t = 0.35f - 0.3f * s * L
            drawPath(pg, Brush.horizontalGradient(if (s < 0f) listOf(p.por(papel, t + 0.1f), p.por(papel, t), p.por(papel, t - 0.5f)) else listOf(p.por(papel, t - 0.5f), p.por(papel, t), p.por(papel, t + 0.1f)), pl.q(s * 16f, 0f).x.coerceAtMost(pl.q(0f, 0f).x), pl.q(s * 16f, 0f).x.coerceAtLeast(pl.q(0f, 0f).x)))
            clipPath(pg) {
                fun en(x: Float, z: Float) = pl.q(x, z, h(x))
                mancha(en(s * 9f, az.entre(-4f, 4f)), 4f * k, 2.4f * k, p.por(lerp(papel, C.ARENA, 0.6f), t), 0.25f)
                if (s < 0f) {
                    // La planta: el tallo, cuatro pares de hojas con su nervio, una flor de cinco pétalos y la raíz.
                    val tallo = (0..8).map { en(-8.5f + 0.9f * sin(it * 0.8f), -7.5f + it * 1.95f) }
                    drawPath(Path().apply { moveTo(tallo[0]); for (q in tallo.drop(1)) lineTo(q) }, tinta, style = Stroke(0.55f * k, cap = StrokeCap.Round))
                    val verde = p.por(lerp(C.HOJA, C.CUERO, 0.25f), t).copy(alpha = 0.55f)
                    for (j in 1..6) {
                        val b = tallo[j]; val ls = if (j % 2 == 0) 1f else -1f; val l = (5.2f - j * 0.45f)
                        val punta = en(-8.5f + ls * l, -7.5f + j * 1.95f + 1.4f)
                        val n = Offset(-(punta - b).y, (punta - b).x) * 0.3f
                        val hoja = Path().apply { moveTo(b); cubicTo(b + (punta - b) * 0.3f + n, punta - (punta - b) * 0.2f + n * 0.5f, punta); cubicTo(punta - (punta - b) * 0.2f - n * 0.5f, b + (punta - b) * 0.3f - n, b); close() }
                        drawPath(hoja, verde)
                        drawPath(hoja, tinta.copy(alpha = 0.8f), style = Stroke(0.3f * k))
                        drawLine(tinta.copy(alpha = 0.6f), b, punta, 0.22f * k)
                    }
                    val fl = tallo.last()
                    for (j in 0 until 5) { val a = (2 * PI * j / 5).toFloat() - 1.57f; val c = fl + Offset(cos(a) * 1.5f * k, sin(a) * 1.1f * k); drawOval(p.por(lerp(C.TEJA, C.LONA, 0.3f), t).copy(alpha = 0.75f), Offset(c.x - 1.1f * k, c.y - 0.8f * k), Size(2.2f * k, 1.6f * k)); drawOval(tinta.copy(alpha = 0.7f), Offset(c.x - 1.1f * k, c.y - 0.8f * k), Size(2.2f * k, 1.6f * k), style = Stroke(0.22f * k)) }
                    drawCircle(p.por(C.LATON, t), 0.6f * k, fl)
                    for (j in -1..1) drawLine(tinta.copy(alpha = 0.6f), tallo[0], tallo[0] + Offset(j * 1.4f * k, 1.2f * k), 0.25f * k)
                    // Dos renglones de notas al pie, a un lado.
                    for (j in 0..1) renglon(en(-14.5f, -6.5f - j * 1.3f), en(-11f, -6.5f - j * 1.3f), tinta.copy(alpha = 0.55f), 0.3f * k, az)
                } else {
                    // El perfil de una costa vista desde el mar: la línea del agua, los morros y el sombreado de sus laderas.
                    val horizonte = 4.4f
                    val perfil = listOf(1.5f to 0f, 3.5f to 0.5f, 5f to 1.2f, 6f to 3.8f, 6.8f to 4.6f, 7.6f to 3.6f, 8.6f to 1.4f, 10.5f to 2.2f, 12f to 3.0f, 13.4f to 1.6f, 14.8f to 0.6f)
                    val pts = perfil.map { en(it.first, horizonte + it.second) }
                    drawPath(Path().apply { moveTo(pts[0]); for (q in pts.drop(1)) lineTo(q) }, tinta, style = Stroke(0.45f * k, cap = StrokeCap.Round))
                    for (j in 0 until 14) { val x = 2f + j * 0.9f; val alto = perfil.zipWithNext().firstOrNull { x >= it.first.first && x <= it.second.first }?.let { (a, b) -> a.second + (b.second - a.second) * (x - a.first) / (b.first - a.first) } ?: 0f
                        if (alto > 0.6f) drawLine(tinta.copy(alpha = 0.45f), en(x, horizonte + alto - 0.4f), en(x - 0.4f, horizonte + 0.2f), 0.22f * k) }
                    drawLine(tinta.copy(alpha = 0.7f), en(1f, horizonte), en(15f, horizonte), 0.3f * k)
                    // Los renglones de la página, con el último a medias.
                    for (j in 0 until 5) renglon(en(1.6f, 2.2f - j * 1.9f), en(if (j == 4) 8f else 14.6f, 2.2f - j * 1.9f), tinta.copy(alpha = 0.6f), 0.32f * k, az)
                }
                // La sombra del lomo.
                drawLine(p.sombra(papel).copy(alpha = 0.5f), pl.q(0f, -9.6f, 0.3f), pl.q(0f, 9.6f, 0.3f), 1.4f * k)
            }
        }
        // ---- La cinta de señal, que sale del lomo, y el lápiz de cedro con la punta de grafito.
        drawPath(Path().apply { moveTo(pl.q(0f, -9.6f, 0.4f)); cubicTo(pl.q(0.5f, -11f, 0f), pl.q(-1f, -12.5f, 0f), pl.q(1.6f, -14f, 0f)) }, p.por(lerp(C.TEJA, C.CUERO, 0.2f), 0.2f), style = Stroke(0.9f * k, cap = StrokeCap.Butt))
        val lap0 = pl.q(5f, -13.5f, 0.6f); val lap1 = pl.q(20f, -7.5f, 0.6f)
        drawLine(p.sombra(C.BREA).copy(alpha = 0.35f), lap0 + Offset(0.6f * k, 0.8f * k), lap1 + Offset(0.6f * k, 0.8f * k), 1.3f * k)
        drawLine(p.por(lerp(C.MADERA, C.ARENA, 0.35f), 0.4f), lap0, lap1, 1.2f * k, StrokeCap.Butt)
        drawLine(p.luz(lerp(C.MADERA, C.ARENA, 0.5f)).copy(alpha = 0.7f), lap0 + Offset(0f, -0.35f * k), lap1 + Offset(0f, -0.35f * k), 0.35f * k)
        val punta = lap0 + (lap0 - lap1) * 0.12f
        drawPath(poligono(lap0 + Offset(0f, -0.6f * k), lap0 + Offset(0f, 0.6f * k), punta), p.por(lerp(C.MADERA, C.ARENA, 0.5f), 0.2f))
        drawLine(p.sombra(C.HIERRO), lap0 + (punta - lap0) * 0.6f, punta, 0.5f * k, StrokeCap.Round)
        filoCalido(p, tapas, pl.q(-17f, 0f).x, pl.q(17f, 0f).x, 0.25f)
    }

    /** La lupa de bolsillo, sacada de su funda de cuerno, sobre un escarabajo que se ve grande a través del cristal. */
    private fun DrawScope.lupa(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val pl = Plano(o, k, 0.2f, 0.5f)
        sombrita(p, o, 26f * k, 4f * k)
        // ---- La tarjeta con el escarabajo, clavado con su alfiler.
        val tarjeta = poligono(pl.q(-6f, -7f), pl.q(9f, -7f), pl.q(9f, 5f), pl.q(-6f, 5f))
        drawPath(Path().apply { addPath(tarjeta, Offset(0.5f * k, 0.6f * k)) }, p.sombra(C.BREA).copy(alpha = 0.25f))
        drawPath(tarjeta, p.por(lerp(C.LONA, C.PAPEL, 0.4f), 0.4f))
        val az = Azar(Semilla.de("botanico", "objeto", "lupa"))
        renglon(pl.q(-4.5f, -5.5f), pl.q(4f, -5.5f), p.por(SEPIA, 0f).copy(alpha = 0.5f), 0.3f * k, az)
        val bicho = pl.q(6.5f, -3f, 0.4f)
        fun escarabajo(c: Offset, e: Float) {
            for (s in listOf(-1f, 1f)) for (j in -1..1) drawLine(p.sombra(C.BREA), c + Offset(s * 0.8f * e, j * 0.8f * e), c + Offset(s * 2.2f * e, j * 1.2f * e + 0.5f * e), 0.22f * e, StrokeCap.Round)
            drawOval(Brush.linearGradient(listOf(p.luz(lerp(C.ESCARABAJO, C.HOJA, 0.45f)), p.medio(C.ESCARABAJO), p.sombra(C.ESCARABAJO)), c + Offset(-L * 1.2f * e, -1.4f * e), c + Offset(L * 1.2f * e, 1.6f * e)), Offset(c.x - 1.25f * e, c.y - 1.2f * e), Size(2.5f * e, 3f * e))
            drawOval(p.por(lerp(C.ESCARABAJO, C.BREA, 0.4f), 0f), Offset(c.x - 0.8f * e, c.y - 2f * e), Size(1.6f * e, 1.1f * e))
            drawLine(p.sombra(C.BREA), c + Offset(0f, -1.1f * e), c + Offset(0f, 1.8f * e), 0.15f * e)
            for (s in listOf(-1f, 1f)) drawLine(p.sombra(C.BREA), c + Offset(s * 0.3f * e, -2f * e), c + Offset(s * 0.9f * e, -2.9f * e), 0.15f * e, StrokeCap.Round)
            drawCircle(Color.White.copy(alpha = 0.55f * (1f - 0.5f * p.l.noche)), 0.35f * e, c + Offset(L * 0.5f * e, -0.6f * e))
        }
        escarabajo(bicho, 0.9f * k)
        drawLine(p.luz(C.LATON), bicho + Offset(0f, -0.2f * k), bicho + Offset(0f, -1.6f * k), 0.25f * k)
        // ---- La funda de cuerno, abierta, que hace de mango.
        val centro = pl.q(1f, 2f, 3.2f); val rx = 8f * k; val ry = 6.2f * k
        val eje = pl.q(-17f, -3f, 1.0f)
        val cuerno = lerp(C.CUERO, C.BREA, 0.45f)
        val funda = Path().apply {
            val a = centro + Offset(-rx * 0.8f, ry * 0.25f); val b = eje
            val d = b - a; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f) * (3.4f * k)
            moveTo(a + n); cubicTo(a + n * 1.4f + d * 0.4f, b + n * 1.3f - d * 0.2f, b + n * 0.4f + d * 0.08f); cubicTo(b + d * 0.12f, b - n * 0.4f + d * 0.08f, b - n * 0.6f)
            cubicTo(b - n * 1.1f - d * 0.3f, a - n * 1.2f + d * 0.4f, a - n); close()
        }
        drawPath(Path().apply { addPath(funda, Offset(0.8f * k, 1.4f * k)) }, p.sombra(C.BREA).copy(alpha = 0.3f))
        drawPath(funda, Brush.linearGradient(listOf(p.por(cuerno, 0.6f), p.por(cuerno, -0.4f)), eje, centro))
        clipPath(funda) {
            // El cuerno tiene vetas de miel, y se trasluce.
            for (j in 0..3) drawLine(p.luz(lerp(cuerno, C.LATON, 0.5f)).copy(alpha = 0.35f), eje + Offset(0f, (j - 1.5f) * 1.2f * k), centro + Offset(-rx * 0.6f, (j - 1.5f) * 1.6f * k), 0.3f * k)
        }
        drawCircle(p.por(C.LATON, 0.4f), 1.1f * k, centro + Offset(-rx * 0.8f, ry * 0.25f))
        // El cordón verde de colgar del cuello, que sale del remache y se enrosca en la mesa.
        drawPath(Path().apply { moveTo(eje); cubicTo(eje + Offset(-5f * k, 2f * k), pl.q(-14f, -9f, 0.3f), pl.q(-8f, -10f, 0.3f)); cubicTo(pl.q(-2f, -11f, 0.3f), pl.q(4f, -11f, 0.3f), pl.q(10f, -9f, 0.3f)) }, p.por(lerp(C.ESCARABAJO, C.SELVA, 0.3f), 0f), style = Stroke(0.55f * k, cap = StrokeCap.Round))
        // ---- El cristal: dentro, la tarjeta y el escarabajo dos veces más grandes; el aro de latón; los brillos.
        val sombraLente = Path().apply { addOval(androidx.compose.ui.geometry.Rect(centro.x - rx + 2.4f * k, centro.y - ry + 4.2f * k, centro.x + rx + 2.4f * k, centro.y + ry + 4.2f * k)) }
        drawPath(sombraLente, p.sombra(C.BREA).copy(alpha = 0.25f))
        val lente = Path().apply { addOval(androidx.compose.ui.geometry.Rect(centro.x - rx, centro.y - ry, centro.x + rx, centro.y + ry)) }
        clipPath(lente) {
            drawRect(p.por(lerp(C.LONA, C.PAPEL, 0.4f), 0.5f), Offset(centro.x - rx, centro.y - ry), Size(2f * rx, 2f * ry))
            // Lo de debajo, ampliado alrededor del centro del cristal.
            escarabajo(centro + Offset(0.8f * k, 0.6f * k), 2.3f * k)
            drawRect(Brush.radialGradient(listOf(Color.Transparent, p.sombra(lerp(C.LONA, C.MAR, 0.25f)).copy(alpha = 0.45f)), centro, rx), Offset(centro.x - rx, centro.y - ry), Size(2f * rx, 2f * ry))
            drawArc(Color.White.copy(alpha = 0.6f * (1f - 0.5f * p.l.noche)), if (L > 0f) 200f else 280f, 60f, false, Offset(centro.x - rx * 0.78f, centro.y - ry * 0.78f), Size(rx * 1.56f, ry * 1.56f), style = Stroke(0.9f * k, cap = StrokeCap.Round))
        }
        drawOval(p.sombra(C.LATON), Offset(centro.x - rx, centro.y - ry + 0.7f * k), Size(2f * rx, 2f * ry), style = Stroke(1.6f * k))
        drawOval(Brush.linearGradient(if (L > 0f) listOf(p.por(C.LATON, -0.4f), p.luz(lerp(C.LATON, C.LONA, 0.4f))) else listOf(p.luz(lerp(C.LATON, C.LONA, 0.4f)), p.por(C.LATON, -0.4f)), Offset(centro.x - rx, centro.y), Offset(centro.x + rx, centro.y)), Offset(centro.x - rx, centro.y - ry), Size(2f * rx, 2f * ry), style = Stroke(1.2f * k))
        // El punto de luz que la lente junta en la mesa, del lado contrario a la sombra.
        if (p.r.fuerza > 0.1f) drawOval(Color.White.copy(alpha = 0.5f * p.r.fuerza), Offset(centro.x - 1.4f * k - p.r.ax * 2f * k, centro.y + ry + 1.2f * k), Size(2.8f * k, 1.0f * k), blendMode = BlendMode.Plus)
        if (p.calida > 0.05f) drawArc(p.lumbre.copy(alpha = 0.7f * p.calida), 150f, 80f, false, Offset(centro.x - rx, centro.y - ry), Size(2f * rx, 2f * ry), style = Stroke(0.8f * k), blendMode = BlendMode.Plus)
    }

    /** El frasco grande de cristal, con un pulpo en alcohol, el corcho atado con vejiga y su etiqueta; un puñado de estopa al lado. */
    private fun DrawScope.frasco(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        fun q(x: Float, y: Float) = Offset(o.x + x * k, o.y + y * k)
        val alto = 30f; val w = 8.2f
        sombrita(p, o, 2f * w * k, alto * k)
        // La sombra del alcohol: una mancha teñida, con la luz que el cristal junta en medio.
        if (p.r.fuerza > 0.1f) {
            val d = (p.r.ax * alto * 0.5f).coerceIn(-14f, 14f)
            drawOval(p.medio(ALCOHOL).copy(alpha = 0.5f * p.r.fuerza), Offset(o.x + (d - w * 0.5f) * k, o.y - 2.4f * k), Size(w * k, 2.4f * k), blendMode = BlendMode.Plus)
        }
        // ---- La estopa, al lado.
        val estopa = lerp(C.CANAMO, C.ARENA, 0.35f)
        val az = Azar(Semilla.de("botanico", "objeto", "frasco"))
        val eo = q(-L * 13f, 0f)
        drawOval(p.sombra(estopa), Offset(eo.x - 6f * k, eo.y - 3.4f * k), Size(12f * k, 4f * k))
        repeat(40) { val c = eo + Offset(az.entre(-5.5f, 5.5f) * k, az.entre(-3.6f, 0f) * k); drawLine(p.por(estopa, az.entre(-0.5f, 0.8f)), c, c + Offset(az.entre(-2.5f, 2.5f) * k, az.entre(-1f, 1f) * k), 0.3f * k, StrokeCap.Round) }
        // ---- El cuerpo del frasco.
        val cuerpo = Path().apply {
            moveTo(q(-w, -1.2f)); lineTo(q(-w, -alto * 0.8f)); cubicTo(q(-w, -alto * 0.86f), q(-w * 0.75f, -alto * 0.88f), q(-w * 0.72f, -alto * 0.9f))
            lineTo(q(w * 0.72f, -alto * 0.9f)); cubicTo(q(w * 0.75f, -alto * 0.88f), q(w, -alto * 0.86f), q(w, -alto * 0.8f)); lineTo(q(w, -1.2f))
            cubicTo(q(w, 0.5f), q(-w, 0.5f), q(-w, -1.2f)); close()
        }
        val nivel = -alto * 0.74f
        // El cristal vacío de arriba, casi sin color; el alcohol, ámbar claro.
        drawPath(cuerpo, p.medio(lerp(C.LONA, C.MAR, 0.12f)).copy(alpha = 0.35f))
        clipPath(cuerpo) {
            drawRect(Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(ALCOHOL).copy(alpha = 0.75f), p.medio(ALCOHOL).copy(alpha = 0.6f), p.luz(ALCOHOL).copy(alpha = 0.55f)) else listOf(p.luz(ALCOHOL).copy(alpha = 0.55f), p.medio(ALCOHOL).copy(alpha = 0.6f), p.sombra(ALCOHOL).copy(alpha = 0.75f)), q(-w, 0f).x, q(w, 0f).x), q(-w, nivel), Size(2f * w * k, -nivel * k + 2f * k))
            // ---- El pulpo: el manto arriba, los ojos, y los brazos que se enroscan hacia el fondo.
            val pulpo = lerp(C.TEJA, C.ARENA, 0.5f)
            val mc = q(0.5f, -alto * 0.5f)
            for (j in 0 until 8) {
                val s = if (j % 2 == 0) 1f else -1f; val f = j / 7f
                val a = mc + Offset((-3f + 6f * f) * k, 2.6f * k)
                val b = q(s * (2f + 4.5f * f), -4f - 3f * (j % 3))
                val c = b + Offset(-s * 2.6f * k, -2f * k)
                val brazo = Path().apply { moveTo(a); cubicTo(a + Offset(s * 1.5f * k, 4f * k), b + Offset(s * 2f * k, -2f * k), b); cubicTo(b + Offset(-s * 1f * k, 1.6f * k), c + Offset(s * 0.4f * k, 1.6f * k), c) }
                drawPath(brazo, p.por(pulpo, if (s * L > 0f) 0.3f else -0.4f).copy(alpha = 0.9f), style = Stroke((1.3f - 0.05f * j) * k, cap = StrokeCap.Round))
                for (v in 0..2) drawCircle(p.luz(lerp(pulpo, C.LONA, 0.5f)).copy(alpha = 0.6f), 0.3f * k, b + Offset(0f, -v * 0.9f * k))
            }
            val manto = Path().apply { moveTo(mc + Offset(-3.6f * k, 2.6f * k)); cubicTo(mc + Offset(-5f * k, -3f * k), mc + Offset(-2.4f * k, -7.4f * k), mc + Offset(0.6f * k, -7.2f * k)); cubicTo(mc + Offset(3.6f * k, -7f * k), mc + Offset(5f * k, -2.4f * k), mc + Offset(3.6f * k, 2.6f * k)); close() }
            drawPath(manto, Brush.linearGradient(if (L > 0f) listOf(p.sombra(pulpo), p.por(pulpo, 0.5f)) else listOf(p.por(pulpo, 0.5f), p.sombra(pulpo)), mc + Offset(-4f * k, 0f), mc + Offset(4f * k, 0f)))
            for (s in listOf(-1f, 1f)) { drawCircle(p.por(lerp(pulpo, C.LONA, 0.4f), 0.3f), 0.9f * k, mc + Offset(s * 2.1f * k, 1.6f * k)); drawCircle(p.sombra(C.BREA), 0.4f * k, mc + Offset(s * 2.1f * k, 1.7f * k)) }
            mancha(mc + Offset(0f, -3.6f * k), 2f * k, 1.6f * k, p.luz(lerp(pulpo, C.LONA, 0.3f)), 0.3f)
            // El menisco, el filo oscuro de los lados del cristal y la raya de luz de la ventana.
            drawOval(p.luz(lerp(ALCOHOL, C.LONA, 0.5f)).copy(alpha = 0.7f), Offset(q(-w, 0f).x, q(0f, nivel - 1f).y), Size(2f * w * k, 2f * k))
            drawRect(p.sombra(lerp(C.MAR, C.BREA, 0.4f)).copy(alpha = 0.5f), q(-L * w - 1f, -alto), Size(2f * k, alto * k))
            drawRect(Color.White.copy(alpha = 0.65f * (1f - 0.45f * p.l.noche)), q(L * w * 0.6f - 0.6f, -alto * 0.82f), Size(1.3f * k, alto * 0.7f * k))
            drawRect(Color.White.copy(alpha = 0.3f * (1f - 0.45f * p.l.noche)), q(L * w * 0.35f - 0.3f, -alto * 0.7f), Size(0.5f * k, alto * 0.45f * k))
        }
        drawPath(cuerpo, p.sombra(lerp(C.MAR, C.BREA, 0.35f)).copy(alpha = 0.55f), style = Stroke(0.45f * k))
        // ---- La etiqueta, que se curva con el cristal.
        val et = Path().apply { moveTo(q(-w * 0.7f, -alto * 0.26f)); cubicTo(q(-w * 0.2f, -alto * 0.24f), q(w * 0.2f, -alto * 0.24f), q(w * 0.7f, -alto * 0.26f)); lineTo(q(w * 0.7f, -alto * 0.12f)); cubicTo(q(w * 0.2f, -alto * 0.1f), q(-w * 0.2f, -alto * 0.1f), q(-w * 0.7f, -alto * 0.12f)); close() }
        drawPath(et, Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(C.PAPEL), p.luz(C.PAPEL)) else listOf(p.luz(C.PAPEL), p.sombra(C.PAPEL)), q(-w, 0f).x, q(w, 0f).x))
        for (j in 0..1) drawLine(p.por(SEPIA, 0f).copy(alpha = 0.6f), q(-w * 0.5f, -alto * (0.21f - j * 0.05f)), q(w * (0.15f + 0.3f * j), -alto * (0.21f - j * 0.05f)), 0.4f * k)
        // ---- El corcho, la vejiga atada por encima y el bramante.
        drawRect(p.por(CORCHO, 0.2f * L), q(-w * 0.62f, -alto * 0.98f), Size(w * 1.24f * k, alto * 0.1f * k))
        val vejiga = Path().apply { moveTo(q(-w * 0.8f, -alto * 0.9f)); cubicTo(q(-w * 0.85f, -alto * 1.0f), q(-w * 0.5f, -alto * 1.04f), q(0f, -alto * 1.04f)); cubicTo(q(w * 0.5f, -alto * 1.04f), q(w * 0.85f, -alto * 1.0f), q(w * 0.8f, -alto * 0.9f)); lineTo(q(w * 0.7f, -alto * 0.86f)); lineTo(q(-w * 0.7f, -alto * 0.86f)); close() }
        drawPath(vejiga, Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(lerp(C.LONA, C.ARENA, 0.4f)), p.luz(lerp(C.LONA, C.ARENA, 0.3f))) else listOf(p.luz(lerp(C.LONA, C.ARENA, 0.3f)), p.sombra(lerp(C.LONA, C.ARENA, 0.4f))), q(-w, 0f).x, q(w, 0f).x))
        drawLine(p.por(C.CANAMO, -0.2f), q(-w * 0.78f, -alto * 0.88f), q(w * 0.78f, -alto * 0.88f), 0.7f * k)
        drawPath(Path().apply { moveTo(q(L * w * 0.7f, -alto * 0.88f)); cubicTo(q(L * w * 1.0f, -alto * 0.8f), q(L * w * 1.1f, -alto * 0.75f), q(L * w * 1.0f, -alto * 0.68f)) }, p.por(C.CANAMO, 0f), style = Stroke(0.4f * k, cap = StrokeCap.Round))
        if (p.calida > 0.05f) drawRect(p.lumbre.copy(alpha = 0.6f * p.calida), q(-L * w * 0.75f - 0.5f, -alto * 0.8f), Size(1f * k, alto * 0.6f * k), blendMode = BlendMode.Plus)
    }

    /** El martillo de geólogo (un lado plano y otro en punta) junto a una piedra partida que enseña una concha fósil. */
    private fun DrawScope.martillo(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val pl = Plano(o + Offset(6f * k, 3f * k), k, -0.42f, 0.5f)
        sombrita(p, o, 34f * k, 4f * k)
        // ---- La piedra partida en dos: la mitad de detrás, de canto; la de delante, tumbada con la concha a la vista.
        val roca = lerp(C.ROCA, C.ARENA, 0.15f)
        val atras = listOf(pl.q(6f, 4f, 0f), pl.q(15f, 4.5f, 0f), pl.q(16f, 5f, 5f), pl.q(12f, 5.5f, 7.5f), pl.q(7f, 5f, 6f))
        drawPath(poligono(atras), p.por(roca, -0.3f * L))
        drawPath(poligono(atras[2], atras[3], atras[4], pl.q(9f, 4.5f, 3f)), p.por(roca, 0.4f * L))
        val corte = listOf(pl.q(4f, -6f, 1.4f), pl.q(14f, -7f, 1.2f), pl.q(17f, -2f, 1.6f), pl.q(15f, 2.5f, 2f), pl.q(7f, 3f, 2f), pl.q(3f, -1f, 1.6f))
        val canto = corte.map { it + Offset(0f, 1.6f * k) }
        drawPath(poligono(canto[0], canto[1], corte[1], corte[0]), p.por(roca, -0.5f))
        drawPath(poligono(canto[1], canto[2], corte[2], corte[1]), p.por(roca, -0.2f * L - 0.2f))
        val tapa = poligono(corte)
        drawPath(tapa, p.por(lerp(roca, C.ARENA, 0.25f), 0.3f))
        clipPath(tapa) {
            // La concha fósil: un abanico de costillas que salen de la charnela, en relieve.
            val ch = pl.q(10f, -4.5f, 1.6f)
            val conchaCol = lerp(C.ARENA, C.LONA, 0.3f)
            val abanico = Path().apply { moveTo(ch); for (j in 0..10) { val a = PI.toFloat() * (0.15f + 0.7f * j / 10f); lineTo(ch + Offset(-cos(a) * 5.4f * k, -sin(a) * 3.6f * k)) }; close() }
            drawPath(abanico, p.por(conchaCol, 0.4f))
            for (j in 0..9) { val a = PI.toFloat() * (0.18f + 0.64f * j / 9f); val f = ch + Offset(-cos(a) * 5.2f * k, -sin(a) * 3.5f * k)
                drawLine(p.sombra(conchaCol).copy(alpha = 0.75f), ch, f, 0.35f * k); drawLine(p.luz(conchaCol).copy(alpha = 0.6f), ch + Offset(L * 0.25f * k, 0f), f + Offset(L * 0.25f * k, 0f), 0.22f * k) }
            for (j in 1..2) drawArc(p.sombra(conchaCol).copy(alpha = 0.5f), 200f, 140f, false, Offset(ch.x - j * 2.2f * k, ch.y - j * 1.5f * k), Size(j * 4.4f * k, j * 3f * k), style = Stroke(0.25f * k))
            drawCircle(p.sombra(roca), 0.8f * k, ch)
        }
        // Las esquirlas que saltaron.
        for ((x, z) in listOf(0f to -8f, 18f to -5f, 2f to 4f)) { val c = pl.q(x, z, 0f); drawPath(poligono(c, c + Offset(1.4f * k, -0.4f * k), c + Offset(0.6f * k, -1.2f * k)), p.por(roca, 0.3f)) }
        // ---- El martillo, tumbado con el mango hacia quien mira: la cabeza de través, la cara plana a un lado y la punta al otro.
        val pm = Plano(o + Offset(-9f * k, 1f * k), k, 0.1f, 0.5f)
        val m0 = pm.q(-3f, -16f, 1.1f); val m1 = pm.q(1f, 5.4f, 1.1f)
        drawLine(p.sombra(C.BREA).copy(alpha = 0.3f), m0 + Offset(0.8f * k, 1.2f * k), m1 + Offset(0.8f * k, 1.2f * k), 2.4f * k, StrokeCap.Round)
        tubo(p, listOf(m0, m1), listOf(1.25f * k, 1.0f * k), lerp(C.MADERA, C.ARENA, 0.25f), 0.8f)
        drawLine(p.sombra(C.CUERO).copy(alpha = 0.45f), m0, m0 + (m1 - m0) * 0.4f, 2.4f * k, StrokeCap.Round)
        val hierro = lerp(C.HIERRO, C.ROCA, 0.2f)
        val z0 = 5.0f; val z1 = 7.8f; val zm = (z0 + z1) / 2f
        val cara = listOf(pm.q(-7f, z0, 0f), pm.q(-7f, z0, 2.8f), pm.q(-7f, z1, 2.8f), pm.q(-7f, z1, 0f))
        val frente = listOf(pm.q(-7f, z0, 0f), pm.q(2.6f, z0 - 0.3f, 0f), pm.q(12f, zm - 0.3f, 1.2f), pm.q(12f, zm - 0.2f, 1.6f), pm.q(2.6f, z0 - 0.3f, 3.0f), pm.q(-7f, z0, 2.8f))
        val arriba = listOf(pm.q(-7f, z0, 2.8f), pm.q(2.6f, z0 - 0.3f, 3.0f), pm.q(12f, zm - 0.2f, 1.6f), pm.q(12f, zm + 0.2f, 1.6f), pm.q(2.6f, z1 + 0.3f, 3.0f), pm.q(-7f, z1, 2.8f))
        drawPath(Path().apply { addPath(poligono(frente), Offset(0.6f * k, 1.4f * k)) }, p.sombra(C.BREA).copy(alpha = 0.3f))
        drawPath(poligono(frente), Brush.horizontalGradient(listOf(p.por(hierro, -0.5f), p.por(hierro, -0.1f), p.por(hierro, -0.4f)), frente[0].x, frente[2].x))
        val cabeza = poligono(arriba)
        drawPath(cabeza, Brush.linearGradient(listOf(p.luz(lerp(hierro, C.LONA, 0.15f)), p.por(hierro, 0.2f)), arriba[0], arriba[2]))
        // La cara de golpear, cuadrada, gastada y brillante, del lado que mira a la luz o en sombra.
        drawPath(poligono(cara), p.por(lerp(hierro, C.LONA, 0.25f), -0.4f * L))
        drawPath(poligono(cara), p.luz(lerp(hierro, C.LONA, 0.4f)).copy(alpha = 0.5f), style = Stroke(0.3f * k))
        // El ojo del mango, la cuña y el óxido del cuello.
        drawOval(p.sombra(C.MADERA), Offset(pm.q(0.6f, zm, 3f).x - 1.2f * k, pm.q(0.6f, zm, 3f).y - 0.5f * k), Size(2.4f * k, 1f * k))
        mancha(pm.q(5f, zm, 2.4f), 1.8f * k, 0.7f * k, p.medio(lerp(C.CUERO, C.TEJA, 0.3f)), 0.4f)
        drawLine(Color.White.copy(alpha = 0.45f * (1f - 0.5f * p.l.noche)), arriba[1], arriba[2], 0.35f * k)
        filoCalido(p, Path().apply { addPath(cabeza); addPath(poligono(frente)) }, cara[0].x - 2f * k, frente[2].x + 2f * k, 0.6f)
    }

    /** La brújula de bolsillo: la caja de latón con la tapa abierta y en pie, la rosa con sus grados y la aguja de punta roja. */
    private fun DrawScope.brujula(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val pl = Plano(o, k, 0f, 0.55f)
        val r = 9.5f
        sombrita(p, o, 24f * k, 10f * k)
        // ---- La tapa, en pie detrás, abierta: por dentro, latón pulido con un círculo grabado.
        val bisagra = pl.q(0f, r, 2.4f)
        val tapa = Path().apply { addOval(androidx.compose.ui.geometry.Rect(bisagra.x - r * k, bisagra.y - 2f * r * k * 0.92f, bisagra.x + r * k, bisagra.y)) }
        drawPath(tapa, Brush.linearGradient(if (L > 0f) listOf(p.por(C.LATON, -0.5f), p.por(C.LATON, 0.2f), p.luz(lerp(C.LATON, C.LONA, 0.35f))) else listOf(p.luz(lerp(C.LATON, C.LONA, 0.35f)), p.por(C.LATON, 0.2f), p.por(C.LATON, -0.5f)), Offset(bisagra.x - r * k, 0f), Offset(bisagra.x + r * k, 0f)))
        val tc = bisagra + Offset(0f, -r * k * 0.92f)
        drawCircle(p.sombra(C.LATON).copy(alpha = 0.6f), r * 0.7f * k, tc, style = Stroke(0.3f * k))
        drawCircle(p.sombra(C.LATON).copy(alpha = 0.45f), r * 0.55f * k, tc, style = Stroke(0.25f * k))
        drawOval(p.sombra(C.LATON), Offset(bisagra.x - r * k, bisagra.y - 2f * r * k * 0.92f), Size(2f * r * k, 2f * r * k * 0.92f), style = Stroke(0.5f * k))
        drawLine(Color.White.copy(alpha = 0.45f * (1f - 0.5f * p.l.noche)), tc + Offset(L * r * 0.45f * k, -r * 0.5f * k), tc + Offset(L * r * 0.2f * k, r * 0.6f * k), 0.6f * k, StrokeCap.Round)
        // ---- La caja: el canto de latón y la tapa del cristal.
        val caja = androidx.compose.ui.geometry.Rect(pl.q(-r, 0f, 2.4f).x, pl.q(0f, r, 2.4f).y, pl.q(r, 0f, 2.4f).x, pl.q(0f, -r, 2.4f).y)
        drawOval(p.sombra(C.LATON), Offset(caja.left, caja.top + 2.4f * k), caja.size)
        drawRect(Brush.horizontalGradient(if (L > 0f) listOf(p.por(C.LATON, -0.6f), p.por(C.LATON, 0.1f), p.luz(C.LATON)) else listOf(p.luz(C.LATON), p.por(C.LATON, 0.1f), p.por(C.LATON, -0.6f)), caja.left, caja.right), Offset(caja.left, caja.center.y), Size(caja.width, 2.4f * k))
        drawOval(p.por(C.LATON, 0.3f), caja.topLeft, caja.size)
        // ---- La esfera: papel, el círculo de los grados, la rosa de ocho puntas y la aguja.
        val esfera = androidx.compose.ui.geometry.Rect(caja.left + 1.3f * k, caja.top + 0.8f * k, caja.right - 1.3f * k, caja.bottom - 0.8f * k)
        drawOval(p.por(lerp(C.LONA, C.PAPEL, 0.3f), 0.4f), esfera.topLeft, esfera.size)
        val c = esfera.center; val ex = esfera.width / 2f; val ey = esfera.height / 2f
        val tinta = p.por(SEPIA, 0f)
        for (j in 0 until 72) {
            val a = (2 * PI * j / 72).toFloat(); val largo = if (j % 18 == 0) 0.22f else if (j % 6 == 0) 0.14f else 0.07f
            drawLine(tinta.copy(alpha = 0.7f), c + Offset(cos(a) * ex * 0.95f, sin(a) * ey * 0.95f), c + Offset(cos(a) * ex * (0.95f - largo), sin(a) * ey * (0.95f - largo)), (if (j % 6 == 0) 0.28f else 0.16f) * k)
        }
        drawOval(tinta.copy(alpha = 0.5f), Offset(c.x - ex * 0.7f, c.y - ey * 0.7f), Size(ex * 1.4f, ey * 1.4f), style = Stroke(0.2f * k))
        for (j in 0 until 8) {
            val a = (PI / 4 * j).toFloat() - (PI / 2).toFloat(); val largo = if (j % 2 == 0) 0.68f else 0.42f
            val pt = c + Offset(cos(a) * ex * largo, sin(a) * ey * largo)
            for (s in listOf(-1f, 1f)) {
                val lat = c + Offset(cos(a + s * 0.5f) * ex * 0.14f, sin(a + s * 0.5f) * ey * 0.14f)
                drawPath(poligono(c, lat, pt), if (s > 0f) p.por(if (j % 2 == 0) C.ESCARABAJO else C.LATON, 0f) else p.por(lerp(C.LONA, C.PAPEL, 0.3f), -0.2f))
            }
        }
        // La aguja: acero pavonado, con la mitad del norte roja, que apunta casi arriba.
        val ang = -1.75f
        val norte = c + Offset(cos(ang) * ex * 0.82f, sin(ang) * ey * 0.82f); val sur = c - (norte - c)
        for ((fin, col) in listOf(norte to lerp(C.TEJA, C.CUERO, 0.15f), sur to lerp(C.HIERRO, C.ESCARABAJO, 0.3f))) {
            val d = fin - c; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f) * (0.9f * k)
            drawPath(poligono(c + n, fin, c - n), p.por(col, 0.2f))
        }
        drawCircle(p.luz(C.LATON), 0.8f * k, c)
        // El cristal: un brillo en arco.
        drawArc(Color.White.copy(alpha = 0.5f * (1f - 0.5f * p.l.noche)), if (L > 0f) 290f else 190f, 60f, false, Offset(esfera.left + ex * 0.2f, esfera.top + ey * 0.2f), Size(ex * 1.6f, ey * 1.6f), style = Stroke(0.7f * k, cap = StrokeCap.Round))
        // ---- La anilla y un trozo de cadena.
        val anilla = pl.q(-r - 1.4f, -1f, 2f)
        drawCircle(p.por(C.LATON, 0.2f), 1.2f * k, anilla, style = Stroke(0.5f * k))
        for (j in 0 until 6) { val cc = anilla + Offset(-(1.6f + j * 1.3f) * k, (0.6f + j * 0.7f + 0.2f * (j % 2)) * k); drawOval(p.por(C.LATON, if (j % 2 == 0) 0.4f else -0.2f), Offset(cc.x - 0.75f * k, cc.y - 0.45f * k), Size(1.5f * k, 0.9f * k), style = Stroke(0.35f * k)) }
        if (p.calida > 0.05f) drawOval(p.lumbre.copy(alpha = 0.5f * p.calida), caja.topLeft, caja.size, style = Stroke(0.8f * k), blendMode = BlendMode.Plus)
    }

    /** La carta abierta, de letra cruzada (renglones a lo largo y otra vez de través), con su lacre; detrás, el atado de las demás. */
    private fun DrawScope.carta(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val az = Azar(Semilla.de("botanico", "objeto", "carta"))
        sombrita(p, o, 34f * k, 2.5f * k)
        val papel = lerp(C.PAPEL, C.LONA, 0.3f)
        val lacre = lerp(C.TEJA, C.BREA, 0.25f)
        // ---- El atado: tres cartas plegadas, con su lacre y sus señas, atadas con una cinta.
        val pa = Plano(o, k, 0.32f, 0.55f)
        for (j in 0 until 3) {
            val x0 = 9f + j * 0.6f; val z0 = 3f + j * 0.5f; val h = j * 1.1f
            val sobre = listOf(pa.q(x0, z0, h), pa.q(x0 + 15f, z0, h), pa.q(x0 + 15f, z0 + 10f, h), pa.q(x0, z0 + 10f, h))
            drawPath(poligono(sobre[0], sobre[1], sobre[1] + Offset(0f, 1.1f * k), sobre[0] + Offset(0f, 1.1f * k)), p.por(lerp(papel, C.ARENA, 0.4f), -0.3f))
            drawPath(poligono(sobre), p.por(lerp(papel, C.ARENA, 0.2f * j), 0.25f - 0.1f * j))
            if (j == 2) {
                for (i in 0..2) renglon(pa.q(x0 + 4f, z0 + 6.5f - i * 1.8f, h), pa.q(x0 + 13f, z0 + 6.5f - i * 1.8f, h), p.por(SEPIA, 0f).copy(alpha = 0.55f), 0.3f * k, az)
                val sello = pa.q(x0 + 3f, z0 + 7.5f, h); drawCircle(p.sombra(SEPIA).copy(alpha = 0.4f), 1.6f * k, sello, style = Stroke(0.25f * k))
            }
        }
        // La cinta, que las cruza.
        drawLine(p.por(lerp(C.TEJA, C.LONA, 0.3f), 0.1f), pa.q(15f, 2.4f, 2.5f), pa.q(17f, 14f, 2.5f), 1.0f * k)
        drawLine(p.sombra(lerp(C.TEJA, C.LONA, 0.3f)), pa.q(15f, 2.4f, 2.3f), pa.q(14.6f, 0.2f, 0f), 1.0f * k)
        // ---- La carta abierta: el pliego con sus dobleces (el papel se levanta en cada uno), escrito dos veces.
        val pl = Plano(o + Offset(-4f * k, 2f * k), k, -0.12f, 0.62f)
        val zs = listOf(-11f, -3.5f, 4f, 11.5f); val hs = listOf(0.3f, 1.6f, 0.4f, 1.4f)
        val x0 = -12f; val x1 = 12f
        for (i in 0 until 3) {
            val panel = poligono(pl.q(x0, zs[i], hs[i]), pl.q(x1, zs[i], hs[i]), pl.q(x1, zs[i + 1], hs[i + 1]), pl.q(x0, zs[i + 1], hs[i + 1]))
            // Cada panel mira un poco arriba o un poco abajo según suba o baje hacia el fondo.
            val t = if (hs[i + 1] > hs[i]) 0.15f else 0.5f
            drawPath(panel, p.por(papel, t))
            clipPath(panel) {
                mancha(pl.q(az.entre(-8f, 8f), (zs[i] + zs[i + 1]) / 2f, 1f), 5f * k, 2f * k, p.por(lerp(papel, C.ARENA, 0.6f), t), 0.25f)
                // Los renglones a lo largo, en tinta parda; y los cruzados, de través, más claros.
                var z = zs[i] + 0.9f
                while (z < zs[i + 1] - 0.5f) { val f = (z - zs[i]) / (zs[i + 1] - zs[i]); val hh = hs[i] + (hs[i + 1] - hs[i]) * f; renglon(pl.q(x0 + 1.5f, z, hh), pl.q(x1 - 1.5f, z, hh), p.por(SEPIA, t).copy(alpha = 0.65f), 0.3f * k, az, 0.97f); z += 1.25f }
                var x = x0 + 1.4f
                while (x < x1 - 1f) { renglon(pl.q(x, zs[i], hs[i]), pl.q(x, zs[i + 1], hs[i + 1]), p.por(lerp(C.CUERO, C.TEJA, 0.3f), t).copy(alpha = 0.4f), 0.26f * k, az, 1f); x += 1.5f }
                if (hs[i + 1] < hs[i]) drawLine(p.sombra(papel).copy(alpha = 0.35f), pl.q(x0, zs[i], hs[i]), pl.q(x1, zs[i], hs[i]), 0.8f * k)
            }
        }
        drawPath(poligono(pl.q(x0, zs[0], hs[0]), pl.q(x1, zs[0], hs[0]), pl.q(x1, zs[0], 0f), pl.q(x0, zs[0], 0f)), p.sombra(papel).copy(alpha = 0.5f))
        // El lacre roto, en el borde de arriba, y una esquina levantada.
        val sello = pl.q(1f, 10.4f, 1.4f)
        for ((dx, dy) in listOf(-1.2f to 0f, 1.1f to 0.3f)) drawPath(poligono(sello + Offset((dx - 1.2f) * k, (dy - 0.6f) * k), sello + Offset((dx + 1.2f) * k, (dy - 0.8f) * k), sello + Offset((dx + 1f) * k, (dy + 0.9f) * k), sello + Offset((dx - 1.1f) * k, (dy + 0.8f) * k)), p.por(lacre, 0.2f * (if (dx > 0f) L else -L)))
        drawCircle(p.luz(lerp(lacre, C.LONA, 0.3f)).copy(alpha = 0.6f), 0.4f * k, sello + Offset(L * 0.6f * k, -0.5f * k))
        val esq = pl.q(x1, zs[0], hs[0])
        drawPath(poligono(esq, esq + Offset(-3.6f * k, -0.2f * k), esq + Offset(-1.4f * k, -2.4f * k)), p.por(lerp(papel, C.ARENA, 0.3f), 0.6f))
        if (p.calida > 0.05f) drawPath(poligono(pl.q(x0, zs[0], hs[0]), pl.q(x1, zs[0], hs[0]), pl.q(x1, zs[3], hs[3]), pl.q(x0, zs[3], hs[3])), p.lumbre.copy(alpha = 0.18f * p.calida), blendMode = BlendMode.Plus)
    }

    /** Los limones: uno partido por la mitad y dos enteros, en un plato de peltre; el jarro del jugo detrás. */
    private fun DrawScope.limones(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val pl = Plano(o, k, 0f, 0.48f)
        sombrita(p, o, 30f * k, 7f * k)
        // ---- El jarro de peltre, detrás, con el jugo.
        val jc = pl.q(-9f, 7f, 0f)
        val jarro = Path().apply { moveTo(jc + Offset(-4.4f * k, 0f)); lineTo(jc + Offset(-3.8f * k, -12f * k)); lineTo(jc + Offset(3.8f * k, -12f * k)); lineTo(jc + Offset(4.4f * k, 0f)); close() }
        drawPath(jarro, Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(PELTRE), p.medio(PELTRE), p.luz(lerp(PELTRE, C.LONA, 0.3f)), p.medio(PELTRE)) else listOf(p.medio(PELTRE), p.luz(lerp(PELTRE, C.LONA, 0.3f)), p.medio(PELTRE), p.sombra(PELTRE)), jc.x - 4.4f * k, jc.x + 4.4f * k))
        for (y in listOf(-1.2f, -10.6f)) drawLine(p.sombra(PELTRE).copy(alpha = 0.6f), jc + Offset(-4.2f * k, y * k), jc + Offset(4.2f * k, y * k), 0.35f * k)
        drawOval(p.sombra(PELTRE), Offset(jc.x - 3.8f * k, jc.y - 13.2f * k), Size(7.6f * k, 2.4f * k))
        drawOval(p.por(LIMON, -0.1f).copy(alpha = 0.85f), Offset(jc.x - 3.2f * k, jc.y - 12.8f * k), Size(6.4f * k, 1.7f * k))
        drawPath(Path().apply { moveTo(jc + Offset(-L * 4f * k, -10.5f * k)); cubicTo(jc + Offset(-L * 7.5f * k, -10.5f * k), jc + Offset(-L * 7.5f * k, -3f * k), jc + Offset(-L * 4.2f * k, -3f * k)) }, p.por(PELTRE, -0.2f), style = Stroke(1f * k))
        // ---- El plato.
        val plato = androidx.compose.ui.geometry.Rect(pl.q(-11f, 0f).x, pl.q(0f, 11f).y, pl.q(11f, 0f).x, pl.q(0f, -11f).y)
        drawOval(p.sombra(PELTRE), Offset(plato.left, plato.top + 1f * k), plato.size)
        drawOval(p.por(PELTRE, 0.3f), plato.topLeft, plato.size)
        drawOval(p.por(PELTRE, -0.1f), Offset(plato.left + 2.4f * k, plato.top + 1.2f * k), Size(plato.width - 4.8f * k, plato.height - 2.4f * k))
        drawArc(Color.White.copy(alpha = 0.35f * (1f - 0.5f * p.l.noche)), if (L > 0f) 300f else 180f, 60f, false, Offset(plato.left + 0.8f * k, plato.top + 0.4f * k), Size(plato.width - 1.6f * k, plato.height - 0.8f * k), style = Stroke(0.5f * k))
        // ---- Un limón entero: un óvalo con sus dos pezones, la piel de poros y su lado de sombra.
        fun limon(c: Offset, rx: Float, ry: Float, giro: Float) {
            val d = Offset(cos(giro), sin(giro))
            val cuerpo = Path().apply {
                val a = c - d * (rx * 1.15f); val b = c + d * (rx * 1.15f); val n = Offset(-d.y, d.x) * ry
                moveTo(a); cubicTo(a + d * (rx * 0.25f) - n * 0.9f, b - d * (rx * 0.6f) - n * 1.35f, b - d * (rx * 0.15f) - n * 0.5f); lineTo(b)
                lineTo(b - d * (rx * 0.15f) + n * 0.5f); cubicTo(b - d * (rx * 0.6f) + n * 1.35f, a + d * (rx * 0.25f) + n * 0.9f, a); close()
            }
            drawPath(cuerpo, Brush.linearGradient(if (L > 0f) listOf(p.sombra(LIMON), p.medio(LIMON), p.luz(LIMON)) else listOf(p.luz(LIMON), p.medio(LIMON), p.sombra(LIMON)), c - Offset(rx, 0f), c + Offset(rx, 0f)))
            clipPath(cuerpo) {
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(LIMON).copy(alpha = 0.6f)), c.y, c.y + ry), Offset(c.x - rx * 2f, c.y), Size(rx * 4f, ry * 1.2f))
                val az = Azar(Semilla.de("botanico", "limon", (c.x * 10).toInt()))
                repeat(18) { drawCircle(p.sombra(LIMON).copy(alpha = 0.35f), 0.18f * k, c + Offset(az.entre(-rx, rx), az.entre(-ry, ry))) }
                drawOval(Color.White.copy(alpha = 0.4f * (1f - 0.6f * p.l.noche)), Offset(c.x + L * rx * 0.2f - rx * 0.25f, c.y - ry * 0.7f), Size(rx * 0.5f, ry * 0.3f))
            }
            drawCircle(p.por(lerp(LIMON, C.HOJA, 0.4f), 0f), 0.5f * k, c + d * (rx * 1.15f))
        }
        limon(pl.q(4f, 4f, 3.4f), 4.6f * k, 3.4f * k, 0.2f)
        // ---- La mitad: la corteza, el blanco y los gajos que salen del centro, brillantes de jugo.
        val mc = pl.q(-3f, -2f, 2.2f)
        val mx = 5f * k; val my = 3.5f * k
        drawOval(p.por(LIMON, -0.3f), Offset(mc.x - mx, mc.y - my + 1.6f * k), Size(2f * mx, 2f * my))
        drawRect(p.por(LIMON, -0.2f * L), Offset(mc.x - mx, mc.y), Size(2f * mx, 1.6f * k))
        drawOval(p.por(lerp(LIMON, C.LONA, 0.55f), 0.4f), Offset(mc.x - mx, mc.y - my), Size(2f * mx, 2f * my))
        drawOval(p.por(lerp(LIMON, C.ARENA, 0.2f), 0.3f), Offset(mc.x - mx * 0.86f, mc.y - my * 0.86f), Size(1.72f * mx, 1.72f * my))
        for (j in 0 until 9) {
            val a = (2 * PI * j / 9).toFloat()
            drawLine(p.por(lerp(LIMON, C.LONA, 0.6f), 0.5f), mc, mc + Offset(cos(a) * mx * 0.86f, sin(a) * my * 0.86f), 0.4f * k)
        }
        drawCircle(p.por(lerp(LIMON, C.LONA, 0.6f), 0.5f), 0.7f * k, mc)
        for (j in 0..2) drawCircle(p.luz(lerp(LIMON, C.LONA, 0.5f)), 0.35f * k, mc + Offset((j - 1) * 1.6f * k, -my * 0.4f + (j % 2) * 0.6f * k))
        // ---- Otro entero, fuera del plato, rodado; y una hoja.
        limon(pl.q(13f, -6f, 3f), 4.2f * k, 3.1f * k, -0.35f)
        val hb = pl.q(9f, 7f, 2.6f)
        val hoja = Path().apply { moveTo(hb); cubicTo(hb + Offset(3f * k, -3f * k), hb + Offset(8f * k, -2.4f * k), hb + Offset(10f * k, 0.6f * k)); cubicTo(hb + Offset(7f * k, 1.6f * k), hb + Offset(3f * k, 1.6f * k), hb); close() }
        drawPath(hoja, p.por(lerp(C.HOJA, C.SELVA, 0.3f), 0.3f * L))
        drawLine(p.sombra(C.SELVA), hb, hb + Offset(10f * k, 0.6f * k), 0.25f * k)
    }

    /** La galleta de barco: tres apiladas con sus agujeros, una rota que enseña la miga, y la talega de lona de donde salen. */
    private fun DrawScope.galleta(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        val pl = Plano(o, k, 0.15f, 0.48f)
        sombrita(p, o, 32f * k, 8f * k)
        val az = Azar(Semilla.de("botanico", "objeto", "galleta"))
        // ---- La talega, detrás: lona sucia, atada a medias, con la boca abierta y galletas asomando.
        val tc = pl.q(-9f, 5f, 0f)
        val lona = lerp(C.LONA, C.CANAMO, 0.4f)
        // Un saco blando: ancho y aplastado abajo, con la boca vuelta hacia fuera y caída a un lado.
        val talega = Path().apply {
            moveTo(tc + Offset(-10.5f * k, 0f)); cubicTo(tc + Offset(-11.5f * k, -3f * k), tc + Offset(-9f * k, -8f * k), tc + Offset(-6.5f * k, -11f * k))
            cubicTo(tc + Offset(-6f * k, -13f * k), tc + Offset(-7.5f * k, -14f * k), tc + Offset(-5f * k, -14.6f * k))
            cubicTo(tc + Offset(-1f * k, -15.4f * k), tc + Offset(4f * k, -14.6f * k), tc + Offset(6.5f * k, -12.6f * k))
            cubicTo(tc + Offset(7.5f * k, -9f * k), tc + Offset(11.5f * k, -4f * k), tc + Offset(10.5f * k, 0f)); close()
        }
        drawPath(talega, Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(lona), p.medio(lona), p.luz(lona)) else listOf(p.luz(lona), p.medio(lona), p.sombra(lona)), tc.x - 9f * k, tc.x + 9f * k))
        clipPath(talega) {
            for ((x, s) in listOf(-4f to -1f, 1f to 1f, 5f to -1f)) drawPath(Path().apply { moveTo(tc + Offset(x * k, -13f * k)); cubicTo(tc + Offset((x - 1f) * k, -8f * k), tc + Offset((x + 1f) * k, -4f * k), tc + Offset(x * k, 0f)) }, (if (s * L > 0f) p.luz(lona) else p.sombra(lona)).copy(alpha = 0.5f), style = Stroke(0.7f * k, cap = StrokeCap.Round))
            mancha(tc + Offset(2f * k, -3f * k), 4f * k, 2f * k, p.sombra(C.CUERO), 0.25f)
        }
        // La boca: la penumbra de dentro y dos galletas que asoman.
        val boca = tc + Offset(0.5f * k, -13.6f * k)
        drawOval(p.sombra(lerp(lona, C.BREA, 0.35f)), Offset(boca.x - 5.4f * k, boca.y - 1.4f * k), Size(10.8f * k, 2.8f * k))
        for ((dx, dy) in listOf(-1.6f to -1f, 1.8f to -0.4f)) drawPath(poligono(boca + Offset((dx - 3f) * k, dy * k), boca + Offset((dx + 3f) * k, (dy - 0.8f) * k), boca + Offset((dx + 3.2f) * k, (dy - 3.4f) * k), boca + Offset((dx - 2.8f) * k, (dy - 2.8f) * k)), p.por(GALLETA, 0.4f * L))
        // El borde vuelto de la boca, en luz, y el cordón suelto que cuelga.
        drawPath(Path().apply { moveTo(boca + Offset(-6f * k, 0.2f * k)); cubicTo(boca + Offset(-2f * k, 2.2f * k), boca + Offset(3f * k, 2.2f * k), boca + Offset(6.4f * k, 0.6f * k)) }, p.luz(lona), style = Stroke(1.4f * k, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(boca + Offset(4f * k, 1.6f * k)); cubicTo(boca + Offset(7f * k, 4f * k), boca + Offset(6f * k, 8f * k), boca + Offset(8.5f * k, 10f * k)) }, p.por(C.CANAMO, 0f), style = Stroke(0.5f * k, cap = StrokeCap.Round))
        // ---- Una galleta: cuadrada, gruesa, de cantos redondeados, tostada, con su rejilla de agujeros.
        fun galleta(cx: Float, cz: Float, h: Float, giro: Float, lado: Float = 6f, rota: Boolean = false) {
            val g = Plano(pl.q(cx, cz, h), k, 0.15f + giro, 0.48f)
            val grueso = 2.2f
            val esq = if (rota) listOf(g.q(-lado, -lado), g.q(lado, -lado), g.q(lado * 0.2f, -lado * 0.1f), g.q(lado * 0.5f, lado * 0.4f), g.q(-lado * 0.1f, lado), g.q(-lado, lado)) else listOf(g.q(-lado, -lado), g.q(lado, -lado), g.q(lado, lado), g.q(-lado, lado))
            val canto = esq.map { it + Offset(0f, grueso * k) }
            // Los cantos que se ven (los de delante); si está rota, el de la rotura enseña la miga.
            for (i in esq.indices) {
                val a = esq[i]; val b = esq[(i + 1) % esq.size]
                if (b.x <= a.x) continue                                       // solo los cantos que miran a quien mira
                val miga = rota && (i == 1 || i == 2 || i == 3)
                drawPath(poligono(a, b, canto[(i + 1) % esq.size], canto[i]), if (miga) p.por(lerp(GALLETA, C.LONA, 0.35f), 0.3f) else p.por(lerp(GALLETA, C.CUERO, 0.25f), -0.3f + 0.3f * L * (b.x - a.x).coerceIn(-1f, 1f)))
            }
            val cara = poligono(esq)
            drawPath(cara, Brush.linearGradient(listOf(p.luz(GALLETA), p.por(lerp(GALLETA, C.CUERO, 0.3f), 0.1f)), esq[0], esq[esq.size / 2]))
            clipPath(cara) {
                mancha(g.q(az.entre(-2f, 2f), az.entre(-2f, 2f)), 3f * k, 1.6f * k, p.por(lerp(GALLETA, C.CUERO, 0.5f), 0f), 0.3f)
                for (i in 0 until 4) for (j in 0 until 4) { val c = g.q(-lado * 0.66f + i * lado * 0.44f, -lado * 0.66f + j * lado * 0.44f); drawOval(p.sombra(lerp(GALLETA, C.CUERO, 0.5f)), Offset(c.x - 0.45f * k, c.y - 0.25f * k), Size(0.9f * k, 0.5f * k)) }
            }
            drawPath(cara, p.luz(lerp(GALLETA, C.LONA, 0.3f)).copy(alpha = 0.5f), style = Stroke(0.4f * k))
        }
        galleta(3f, -3f, 0f, 0.05f)
        galleta(3.4f, -2.6f, 2.2f, -0.12f)
        galleta(2.8f, -3.2f, 4.4f, 0.18f)
        galleta(13f, -6f, 0f, 0.5f, 5.6f, rota = true)
        // Las migas y un gorgojo.
        repeat(9) { val c = pl.q(az.entre(-12f, 14f), az.entre(-12f, -6f), 0f); drawCircle(p.por(GALLETA, 0.2f), az.entre(0.25f, 0.55f) * k, c) }
        val gg = pl.q(12f, -5f, 2.3f); drawOval(p.por(lerp(C.CUERO, C.BREA, 0.5f), 0.1f), Offset(gg.x - 0.6f * k, gg.y - 0.3f * k), Size(1.2f * k, 0.6f * k))
    }
}
