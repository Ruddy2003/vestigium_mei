package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  La perspectiva de los cuadros de Mongol
// =====================================================================
//
// Las tiendas, las carretas y los corrales son cuerpos redondos o de caras planas que tienen que verse en tres cuartos,
// enseñando dos caras o su curva (fallo 1 de la tanda S11). Para no inventar cada vista a mano se arman en metros, en
// sus propios ejes, y se proyectan con una cámara de agujero: el punto del lienzo donde pisan y cuántas unidades mide
// ahí un metro dicen a qué distancia están y a qué altura quedan los ojos sobre su suelo. **[Diseño propio]**: cada cosa
// puede tener su propio «suelo» (los ojos más o menos altos sobre él), que es lo que hace un pintor para escalonar un
// campamento en una ladera.

/** Un punto en metros, en los ejes de una cosa: x a su derecha, y hacia arriba, z hacia su espalda (con giro 0, hacia el fondo). */
internal data class V3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: V3) = V3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: V3) = V3(x - o.x, y - o.y, z - o.z)
    operator fun times(k: Float) = V3(x * k, y * k, z * k)
}

/** Una parte de una cosa que se pinta en su turno, de atrás adelante: su profundidad y cómo se pinta. */
internal fun parte(hondo: Float, pintar: DrawScope.() -> Unit): Pair<Float, DrawScope.() -> Unit> = hondo to pintar

/** Desde dónde se mira: la altura del [horizonte] en el lienzo, la [focal] (en unidades) y el [centro] de la vista. */
internal class Mirada(val horizonte: Float, val focal: Float = 640f, val centro: Float = ANCHO / 2f)

/**
 * Una cosa puesta en el suelo y vista con [v]: [pie] es el punto del lienzo donde pisa su centro y [k] cuántas unidades
 * del lienzo mide allí un metro. [giro] (radianes): hacia dónde mira su frente (su −z); con 0, hacia quien mira, y
 * positivo, hacia la derecha.
 */
internal class Sitio(val v: Mirada, val pie: Offset, val k: Float, val giro: Float = 0f) {
    /** A qué distancia está, en metros. */
    val z0 = v.focal / k
    private val x0 = (pie.x - v.centro) / k
    /** A qué altura quedan los ojos sobre su suelo, en metros. */
    val ojo = (pie.y - v.horizonte) / k
    private val cg = cos(giro); private val sg = sin(giro)

    fun mx(x: Float, z: Float) = x0 + x * cg - z * sg
    fun mz(x: Float, z: Float) = z0 + x * sg + z * cg

    /** Un punto del mundo (x y z ya girados, en metros desde los ojos) en el lienzo. */
    fun pm(X: Float, y: Float, Z: Float): Offset { val zz = Z.coerceAtLeast(0.15f); return Offset(v.centro + v.focal * X / zz, v.horizonte + v.focal * (ojo - y) / zz) }

    /** Un punto de la cosa, en sus ejes, en el lienzo. */
    fun p(x: Float, y: Float, z: Float): Offset = pm(mx(x, z), y, mz(x, z))
    fun p(a: V3): Offset = p(a.x, a.y, a.z)

    /** Cuántas unidades del lienzo mide un metro a la profundidad del punto ([x], [z]) de la cosa. */
    fun escala(x: Float, z: Float): Float = v.focal / mz(x, z).coerceAtLeast(0.15f)

    /** Una dirección de la cosa (una normal), girada al mundo. */
    fun normal(n: V3): V3 = V3(n.x * cg - n.z * sg, n.y, n.x * sg + n.z * cg)

    /** Si la cara que pasa por [a] con la normal [n] (en los ejes de la cosa) mira hacia los ojos. */
    fun seVe(a: V3, n: V3): Boolean { val w = normal(n); return w.x * -mx(a.x, a.z) + w.y * (ojo - a.y) + w.z * -mz(a.x, a.z) > 0f }

    /** La profundidad de un punto de la cosa, para pintar de atrás adelante. */
    fun hondo(a: V3): Float = mz(a.x, a.z)
}

/**
 * La luz del sol (o de la luna) en tres dimensiones, con el [r] del momento: viene del lado del sol y un poco desde
 * delante, como en `Rayo` (**[Aproximación]**, la misma de todas las escenas). Da el lado de luz de cada cara ([t]) y
 * hacia dónde cae la sombra arrojada en el suelo ([sx], [sz], metros por metro de alto).
 */
internal class Sol3(val r: Rayo) {
    private val n = sqrt(r.lat * r.lat + 0.25f)
    private val hx = r.lat / n
    private val hz = -0.5f / n
    private val elev = atan(r.tan)
    private val lx = hx * cos(elev); private val ly = sin(elev); private val lz = hz * cos(elev)
    val sx = -hx * r.cot.coerceAtMost(4f)
    val sz = -hz * r.cot.coerceAtMost(4f)

    /** De −1 (de espaldas a la luz) a 1 (de cara a ella), para una cara con la normal [w] (ya en el mundo). */
    fun t(w: V3): Float = (((w.x * lx + w.y * ly + w.z * lz) - 0.12f) / 0.78f).coerceIn(-1f, 1f)
}

/** El color [c] de una cara que mira a la luz [t] (de −1 a 1), entre la sombra, el color propio y la luz del pincel. */
internal fun Pincel.por(c: Color, t: Float, lejos: Float = 0f): Color {
    // El color propio queda un poco hacia la sombra: así un color claro (el fieltro, la nieve) también tiene su lado de luz.
    // El paso de la luz a la sombra es corto, como en un gouache: lo que da la espalda al sol se va pronto a la sombra.
    val propio = lerp(medio(c, lejos), sombra(c, lejos), 0.32f)
    return if (t >= 0f) lerp(propio, luz(c, lejos), t) else lerp(propio, sombra(c, lejos), (-t * 2.2f).coerceAtMost(1f))
}

/** La envolvente convexa de unos puntos (para la sombra de un cuerpo en el suelo). */
internal fun envolvente(puntos: List<Offset>): List<Offset> {
    if (puntos.size < 3) return puntos
    val p = puntos.sortedWith(compareBy({ it.x }, { it.y }))
    fun giro(o: Offset, a: Offset, b: Offset) = (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)
    val abajo = ArrayList<Offset>(); for (q in p) { while (abajo.size >= 2 && giro(abajo[abajo.size - 2], abajo.last(), q) <= 0f) abajo.removeAt(abajo.size - 1); abajo.add(q) }
    val arriba = ArrayList<Offset>(); for (q in p.reversed()) { while (arriba.size >= 2 && giro(arriba[arriba.size - 2], arriba.last(), q) <= 0f) arriba.removeAt(arriba.size - 1); arriba.add(q) }
    return abajo.dropLast(1) + arriba.dropLast(1)
}

/**
 * Las piezas que usan los dos cuadros de Mongol: la tienda de fieltro, la carreta de ruedas altas, las ovejas en masa,
 * los caballos, el ovoo, el odre en su armazón, el corral de piedra y estiércol, el hogar de fuera y el montón de argal.
 * Cada una con su lado de luz y de sombra, su textura y sus señales de uso, a cualquier tamaño.
 */
internal object PiezasMongol {
    private val C = ColorMongol

    // =================================================================
    //  La tienda (ger)
    // =================================================================

    /**
     * Una tienda de fieltro puesta en [s] (su giro dice hacia dónde mira la puerta). Las medidas en metros, las de una
     * tienda de familia **[Aproximación]**: [radio] 3, la celosía de 1.55 de alto y el aro a 2.75.
     * - [desgaste], de 0 (fieltro nuevo, casi blanco) a 1 (viejo, amarillento, remendado); [semilla] cambia dónde caen
     *   las costuras, los remiendos y las manchas.
     * - [puerta] y [dentro] (el fuego del centro, de 0 a 1): por la puerta abierta se ve la penumbra con la celosía y el
     *   fuego; de noche, la luz sale por la puerta y por el aro.
     * - [nieve], de 0 a 1: la nieve arrimada al pie, más alta del lado del viento.
     * - [fieltro], de 0 (solo el armazón: la celosía, las varas y el aro) a 1 (con todo el fieltro): la tienda a medio
     *   armar del día de la mudanza.
     * - [lejos] la desvanece; sin [detalle] se pinta sin cinchas finas ni puntadas (las tiendas lejanas).
     */
    class Ger(
        val s: Sitio, val semilla: Long, val desgaste: Float = 0.5f, val puerta: Puerta = Puerta.CERRADA, val dentro: Float = 0f,
        val nieve: Float = 0f, val fieltro: Float = 1f, val lejos: Float = 0f, val detalle: Boolean = true,
        val radio: Float = 3f, val pared: Float = 1.55f, val cumbre: Float = 2.75f, val aro: Float = 0.62f,
    ) {
        /** El alero: el fieltro del techo sobresale un poco de la pared. */
        val alero: Float get() = radio + 0.1f

        /** Media anchura de la puerta, en radianes de la pared. */
        val mediaPuerta: Float get() = 0.46f / radio
        val altoPuerta: Float get() = 1.28f

        /** El punto de la pared a la altura [y], en el ángulo [th] (0 = la puerta). */
        fun pared(th: Float, y: Float, r: Float = radio): Offset = s.p(r * sin(th), y, -r * cos(th))

        /** Lo que se ve de la pared: de qué ángulo a qué ángulo. */
        fun visible(): Pair<Float, Float> {
            val x0 = s.mx(0f, 0f); val z0 = s.mz(0f, 0f)
            val rho = sqrt(x0 * x0 + z0 * z0)
            val psi = atan2(-x0, z0) - s.giro
            val a = acos((radio / rho).coerceIn(-1f, 1f))
            return (psi - a) to (psi + a)
        }

        /** El centro del aro, por donde sale el humo. */
        val humero: Offset get() = s.p(0f, cumbre + 0.05f, 0f)

        /** La puerta se ve: mira hacia quien mira. */
        val puertaSeVe: Boolean get() = s.seVe(V3(0f, 0.6f, -radio), V3(0f, 0f, -1f))

        /** El centro de la puerta, a media altura. */
        val centroDePuerta: Offset get() = pared(0f, altoPuerta * 0.5f)

        /** Dónde pisa la puerta (el umbral), en el lienzo. */
        val umbral: Offset get() = pared(0f, 0f)
    }

    /** La silueta de la tienda (para lo que la tape o se recorte con ella). */
    fun silueta(g: Ger): Path {
        val (a, b) = g.visible(); val n = 24
        val path = Path()
        val pie = (0..n).map { g.pared(a + (b - a) * it / n, 0f) }
        path.moveTo(pie[0]); for (q in pie.drop(1)) path.lineTo(q)
        val alero = (n downTo 0).map { g.pared(a + (b - a) * it / n, g.pared - 0.06f, g.alero) }
        for (q in alero) path.lineTo(q)
        path.close()
        // El techo: todas las caras del cono que se ven.
        val m = 36
        for (i in 0 until m) {
            val t0 = (2 * PI * i / m).toFloat(); val t1 = (2 * PI * (i + 1) / m).toFloat()
            path.addPath(poligono(g.pared(t0, g.pared - 0.06f, g.alero), g.pared(t1, g.pared - 0.06f, g.alero), g.s.p(g.aro * sin(t1), g.cumbre, -g.aro * cos(t1)), g.s.p(g.aro * sin(t0), g.cumbre, -g.aro * cos(t0))))
        }
        return path
    }

    /**
     * La sombra que la tienda arroja en el suelo con el sol [sol]: la envolvente de su pie, de su alero y de su aro
     * tumbados en el suelo. Va dentro de una `capaSombra`.
     */
    fun sombraDeGer(g: Ger, sol: Sol3): Path {
        val s = g.s; val puntos = ArrayList<Offset>()
        for (i in 0 until 28) {
            val t = (2 * PI * i / 28).toFloat()
            for ((r, y) in listOf(g.radio to 0f, g.alero to g.pared, g.aro to g.cumbre)) {
                val X = s.mx(r * sin(t), -r * cos(t)) + sol.sx * y; val Z = s.mz(r * sin(t), -r * cos(t)) + sol.sz * y
                puntos += s.pm(X, 0f, Z)
            }
        }
        return poligono(envolvente(puntos))
    }

    /**
     * El charco de luz que echa la puerta de noche en el suelo (o en la nieve): un trapecio que sale del umbral y se
     * abre. Con la puerta entreabierta, más estrecho y corrido al lado abierto. Null con la puerta cerrada.
     */
    fun charcoDePuerta(g: Ger, largo: Float = 3.6f): Path? {
        if (g.puerta == Puerta.CERRADA) return null
        val d = g.mediaPuerta
        val (t0, t1) = if (g.puerta == Puerta.ABIERTA) (-d to d) else (-d to -d * 0.1f)
        val r1 = g.radio + largo
        val abre = 1.9f
        val c = (t0 + t1) / 2f
        return poligono(g.pared(t0, 0f), g.pared(t1, 0f), g.pared(c + (t1 - c) * abre * g.radio / r1 * 1.6f, 0f, r1), g.pared(c + (t0 - c) * abre * g.radio / r1 * 1.6f, 0f, r1))
    }

    /** Pinta el charco de luz de la puerta con la fuerza [k] (de 0 a 1). */
    fun DrawScope.luzDePuerta(g: Ger, k: Float, largo: Float = 3.6f) {
        if (charcoDePuerta(g, largo) == null || k <= 0.02f) return
        val a = g.umbral; val b = g.pared(0f, 0f, g.radio + largo)
        val forma = charcoDePuerta(g, largo)!!
        drawPath(forma, Brush.linearGradient(0f to C.LUMBRE.copy(alpha = (0.2f * k).coerceIn(0f, 1f)), 1f to Color.Transparent, start = a, end = b), blendMode = BlendMode.Plus)
        // Encima, manchas blandas a lo largo del charco, cada vez más anchas y más tenues: el borde no queda duro.
        val ancho = (g.pared(g.mediaPuerta, 0f) - g.pared(-g.mediaPuerta, 0f)).getDistance()
        for (j in 0 until 7) { val t = j / 6f; val c = a + (b - a) * (0.08f + 0.8f * t); val w = ancho * (0.7f + 1.6f * t); mancha(c, w, w * 0.32f + 2f, C.LUMBRE, (0.3f * k * (1f - 0.75f * t)).coerceIn(0f, 1f)) }
        mancha(a + (b - a) * 0.12f, (b - a).getDistance() * 0.35f + 6f, 5f, C.LUMBRE, 0.22f * k)
    }

    /** Pinta la tienda [g] con el pincel [p] (su luz, y de noche la lumbre de cerca) y la luz [sol]. */
    fun DrawScope.ger(g: Ger, p: Pincel, sol: Sol3) {
        val s = g.s; val az = Azar(g.semilla); val lejos = g.lejos
        val (a, b) = g.visible()
        val n = if (g.detalle) 30 else 16
        val noche = p.l.noche
        val fieltro = lerp(lerp(C.FIELTRO, Color.White, 0.25f * (1f - g.desgaste)), C.FIELTRO_VIEJO, 0.55f * g.desgaste)
        val techoColor = lerp(fieltro, C.FIELTRO_VIEJO, 0.25f + 0.3f * g.desgaste)

        // ---- El armazón, cuando el fieltro no está puesto (o solo el de la pared).
        if (g.fieltro < 0.999f) armazon(g, p, sol, conPared = g.fieltro < 0.5f)

        // ---- La pared: un cilindro, con su lado de luz y su lado de sombra.
        if (g.fieltro >= 0.5f) {
            val angulos = (0..n).map { a + (b - a) * it / n }
            val pie = angulos.map { g.pared(it, 0f) }; val alto = angulos.map { g.pared(it, g.pared) }
            val forma = Path().apply { moveTo(pie[0]); for (q in pie.drop(1)) lineTo(q); for (q in alto.reversed()) lineTo(q); close() }
            val xs = pie.map { it.x }; val x0 = xs.min(); val x1 = xs.max()
            val paradas = angulos.mapIndexed { i, th -> ((xs[i] - x0) / (x1 - x0).coerceAtLeast(0.01f)) to p.por(fieltro, sol.t(s.normal(V3(sin(th), 0f, -cos(th)))), lejos) }.sortedBy { it.first }
            drawPath(forma, Brush.horizontalGradient(*paradas.toTypedArray(), startX = x0, endX = x1))
            clipPath(forma) {
                val yPie = pie.maxOf { it.y }; val yAlto = alto.minOf { it.y }; val h = yPie - yAlto
                // El bajo sucio: la tierra y el estiércol que salpican, y la humedad.
                val sucio = lerp(C.TIERRA, C.ESTIERCOL, 0.4f)
                for (i in 0 until n) {
                    val q0 = pie[i]; val q1 = pie[i + 1]; val u0 = g.pared(angulos[i], 0.32f); val u1 = g.pared(angulos[i + 1], 0.32f)
                    drawPath(poligono(q0, q1, u1, u0), Brush.verticalGradient(0f to p.sombra(sucio, lejos).copy(alpha = 0f), 1f to p.sombra(sucio, lejos).copy(alpha = 0.55f), startY = u0.y, endY = q0.y))
                }
                // Lo que el tiempo deja en el fieltro: manchas, chorreras bajo el alero y un velo más amarillo en lo viejo.
                if (g.detalle) {
                    repeat(9) {
                        val th = az.entre(a, b); val y = az.entre(0.2f, g.pared - 0.2f); val c = g.pared(th, y)
                        mancha(c, az.entre(0.3f, 0.8f) * s.escala(0f, 0f), az.entre(0.08f, 0.18f) * s.escala(0f, 0f), if (it % 3 == 0) p.medio(lerp(fieltro, Color.White, 0.5f), lejos) else p.medio(lerp(fieltro, C.TIERRA, 0.5f), lejos), 0.08f + 0.1f * g.desgaste)
                    }
                    repeat((10 + 16 * g.desgaste).toInt()) {
                        val th = az.entre(a, b); val arriba = g.pared(th, g.pared - 0.05f); val largo = az.entre(0.2f, 0.75f) * g.pared
                        drawLine(p.sombra(lerp(fieltro, C.TIERRA, 0.6f), lejos).copy(alpha = 0.18f + 0.15f * g.desgaste), arriba, g.pared(th, g.pared - 0.05f - largo), (0.02f * s.escala(0f, 0f)).coerceIn(0.3f, 1.4f), StrokeCap.Round)
                    }
                    if (s.escala(0f, 0f) > 30f) {
                        // De cerca: el hollín que sale por la puerta y ennegrece el fieltro de encima, la grasa de las manos
                        // junto al marco, y el fieltro más gris donde rozan las cuerdas.
                        mancha(g.pared(0f, g.pared - 0.12f), g.mediaPuerta * g.radio * 1.6f * s.escala(0f, -g.radio), 0.18f * s.escala(0f, -g.radio), p.sombra(lerp(fieltro, C.HOLLIN, 0.5f), lejos), 0.4f)
                        for (u in listOf(-1.5f, 1.45f)) mancha(g.pared(g.mediaPuerta * u, 0.85f), 0.1f * s.escala(0f, -g.radio), 0.32f * s.escala(0f, -g.radio), p.sombra(lerp(fieltro, C.CUERO, 0.5f), lejos), 0.35f)
                        for (y in listOf(0.24f, 0.72f, 1.2f)) { val linea = (0..n).map { g.pared(a + (b - a) * it / n, y * g.pared / 1.55f + 0.04f) }; drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(fieltro, lejos).copy(alpha = 0.3f), style = Stroke(0.09f * s.escala(0f, 0f))) }
                        // Los paños de la pared se montan: un canto horizontal a media altura, con su sombra.
                        val canto = (0..n).map { g.pared(a + (b - a) * it / n, 0.9f + 0.03f * sin(it * 0.9f)) }
                        drawPath(Path().apply { moveTo(canto[0]); for (q in canto.drop(1)) lineTo(q) }, p.sombra(fieltro, lejos).copy(alpha = 0.45f), style = Stroke(0.025f * s.escala(0f, 0f)))
                        drawPath(Path().apply { moveTo(canto[0] + Offset(0f, -0.6f)); for (q in canto.drop(1)) lineTo(q + Offset(0f, -0.6f)) }, p.luz(lerp(fieltro, Color.White, 0.4f), lejos).copy(alpha = 0.5f), style = Stroke(0.012f * s.escala(0f, 0f)))
                    }
                    // La textura del fieltro: pelusa en el lado de la luz.
                    val esc = s.escala(0f, 0f)
                    if (esc > 12f) drawPoints((0 until (60 * (esc / 30f)).toInt().coerceAtMost(260)).map { val th = az.entre(a, b); g.pared(th, az.entre(0.1f, g.pared - 0.1f)) }, PointMode.Points, p.luz(lerp(fieltro, Color.White, 0.4f), lejos).copy(alpha = 0.35f), (0.03f * esc).coerceIn(0.4f, 1.6f), StrokeCap.Round)
                }
                // La sombra del alero: una franja bajo el borde del techo, más honda del lado del sol alto.
                for (i in 0 until n) {
                    val u0 = g.pared(angulos[i], g.pared); val u1 = g.pared(angulos[i + 1], g.pared)
                    val d0 = g.pared(angulos[i], g.pared - 0.22f); val d1 = g.pared(angulos[i + 1], g.pared - 0.22f)
                    drawPath(poligono(u0, u1, d1, d0), Brush.verticalGradient(0f to p.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f * (0.4f + 0.6f * sol.r.dia)), 1f to Color.Transparent, startY = u0.y, endY = d0.y))
                }
                // Los remiendos: trozos de fieltro de otro color, con su puntada.
                if (g.detalle) repeat((1 + 3 * g.desgaste).toInt()) {
                    val th = az.entre(a + 0.15f, b - 0.15f); if (abs(th) < g.mediaPuerta * 1.6f) return@repeat
                    val y = az.entre(0.25f, g.pared - 0.45f); val w = az.entre(0.1f, 0.2f); val h2 = az.entre(0.14f, 0.26f)
                    // Un trozo de borde irregular, que sigue la curva de la pared.
                    val esquinas = (0 until 8).map { j -> val ang = (2 * PI * j / 8).toFloat(); val rr = az.entre(0.75f, 1.1f); g.pared(th + cos(ang) * w * rr / g.radio, y + h2 / 2f + sin(ang) * h2 / 2f * rr, g.radio + 0.01f) }
                    val t = sol.t(s.normal(V3(sin(th), 0f, -cos(th))))
                    val tela = if (it % 2 == 0) lerp(fieltro, Color.White, 0.3f) else lerp(fieltro, C.TIERRA, 0.22f)
                    drawPath(poligono(esquinas), p.por(tela, t, lejos).copy(alpha = 0.85f))
                    if (s.escala(0f, 0f) > 20f) drawPoints(esquinas.indices.flatMap { j -> val q0 = esquinas[j]; val q1 = esquinas[(j + 1) % esquinas.size]; (0 until 3).map { q0 + (q1 - q0) * (it / 3f) } }, PointMode.Points, p.sombra(C.CRIN, lejos).copy(alpha = 0.5f), (0.012f * s.escala(0f, 0f)).coerceIn(0.4f, 1f), StrokeCap.Round)
                }
                // Las costuras entre los paños de la pared.
                if (g.detalle) for (j in 0 until 3) {
                    val th = az.entre(a + 0.1f, b - 0.1f); if (abs(th) < g.mediaPuerta * 1.4f) continue
                    val q0 = g.pared(th, 0.05f); val q1 = g.pared(th, g.pared - 0.02f)
                    drawLine(p.sombra(fieltro, lejos).copy(alpha = 0.6f), q0, q1, (0.018f * s.escala(0f, 0f)).coerceIn(0.3f, 1f))
                    if (s.escala(0f, 0f) > 20f) drawPoints((0..14).map { q0 + (q1 - q0) * (it / 14f) + Offset(0.8f, 0f) }, PointMode.Points, p.medio(C.CRIN, lejos), 0.7f, StrokeCap.Round)
                }
                // De noche, la lumbre de la puerta abierta alumbra el fieltro de alrededor.
                if (noche > 0.2f && g.dentro > 0.05f && g.puerta != Puerta.CERRADA && g.puertaSeVe) {
                    val c = g.pared(0f, 0.55f)
                    resplandor(c, 1.6f * s.escala(0f, 0f), 0.55f * g.dentro * noche, C.LUMBRE)
                }
            }
            // Las cinchas de crin que ciñen la pared.
            val cinchas = if (g.detalle) listOf(0.24f, 0.72f, 1.2f) else listOf(0.5f, 1.15f)
            for (y in cinchas) {
                val y2 = y * g.pared / 1.55f
                val linea = (0..n).map { g.pared(a + (b - a) * it / n * 0.98f + (b - a) * 0.01f, y2, g.radio + 0.015f) }
                val grueso = (0.035f * s.escala(0f, 0f)).coerceIn(0.35f, 2.2f)
                val camino = Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }
                drawPath(camino, p.sombra(C.CRIN, lejos), style = Stroke(grueso, cap = StrokeCap.Round))
                if (grueso > 0.8f) drawPath(camino, p.luz(lerp(C.CRIN, C.FIELTRO, 0.4f), lejos).copy(alpha = 0.55f), style = Stroke(grueso * 0.35f, cap = StrokeCap.Round))
            }
        }

        // ---- La puerta.
        if (g.puertaSeVe && g.fieltro >= 0.5f) puerta(g, p, sol)

        // ---- El techo: un cono de fieltro, de caras que se pintan de atrás adelante.
        if (g.fieltro >= 0.999f) techo(g, p, sol, techoColor)

        // ---- La nieve arrimada al pie.
        if (g.nieve > 0.02f && g.fieltro >= 0.5f) {
            val m2 = 26
            val angulos = (0..m2).map { a + (b - a) * it / m2 }
            val arriba = angulos.mapIndexed { i, th -> val lado = 0.5f + 0.5f * sin(th + s.giro + 1.2f); g.pared(th, g.nieve * (0.1f + 0.32f * lado + 0.08f * sin(i * 1.7f + g.semilla % 7)), g.radio + 0.04f) }
            val abajo = angulos.map { g.pared(it, -0.02f, g.radio + 0.25f) }
            val banco = Path().apply { moveTo(arriba[0]); for (q in arriba.drop(1)) lineTo(q); for (q in abajo.reversed()) lineTo(q); close() }
            val xs = arriba.map { it.x }; val x0 = xs.min(); val x1 = xs.max()
            val paradas = angulos.mapIndexed { i, th -> ((xs[i] - x0) / (x1 - x0).coerceAtLeast(0.01f)) to p.por(C.NIEVE, 0.4f + 0.6f * sol.t(s.normal(V3(sin(th), 0.5f, -cos(th)))), lejos) }.sortedBy { it.first }
            drawPath(banco, Brush.horizontalGradient(*paradas.toTypedArray(), startX = x0, endX = x1))
            drawPath(Path().apply { moveTo(arriba[0]); for (q in arriba.drop(1)) lineTo(q) }, p.luz(C.NIEVE, lejos).copy(alpha = 0.8f), style = Stroke((0.02f * s.escala(0f, 0f)).coerceIn(0.3f, 1.2f)))
        }

        // ---- El aro, de noche: la luz del fuego sale por arriba y alumbra el humo.
        if (noche > 0.25f && g.dentro > 0.05f && g.fieltro >= 0.999f) {
            val h = g.humero
            resplandor(h + Offset(0f, -0.3f * s.escala(0f, 0f)), 1.3f * s.escala(0f, 0f), 0.45f * g.dentro * noche, C.LUMBRE)
        }
    }

    /** El techo de fieltro, con las varas que se adivinan debajo, el cerco de hollín del aro, las cuerdas y el aro. */
    private fun DrawScope.techo(g: Ger, p: Pincel, sol: Sol3, color: Color) {
        val s = g.s; val lejos = g.lejos; val m = if (g.detalle) 48 else 24
        val pendiente = atan((g.cumbre - g.pared) / (g.alero - g.aro))
        val yAlero = g.pared - 0.06f
        class Cara(val t0: Float, val t1: Float, val hondo: Float, val luz: Float)
        val caras = (0 until m).map { i ->
            val t0 = (2 * PI * i / m).toFloat(); val t1 = (2 * PI * (i + 1) / m).toFloat(); val tm = (t0 + t1) / 2f
            val nl = V3(sin(tm) * sin(pendiente), cos(pendiente), -cos(tm) * sin(pendiente))
            val centro = V3((g.alero + g.aro) / 2f * sin(tm), (yAlero + g.cumbre) / 2f, -(g.alero + g.aro) / 2f * cos(tm))
            if (!s.seVe(centro, nl)) null else Cara(t0, t1, s.hondo(centro), sol.t(s.normal(nl)))
        }.filterNotNull().sortedByDescending { it.hondo }
        fun alero(t: Float) = s.p(g.alero * sin(t), yAlero, -g.alero * cos(t))
        fun arriba(t: Float, k: Float = 1f) = s.p((g.aro + (g.alero - g.aro) * (1f - k)) * sin(t), g.cumbre - (g.cumbre - yAlero) * (1f - k), -(g.aro + (g.alero - g.aro) * (1f - k)) * cos(t))
        val base = Path().apply { for (c in caras) addPath(poligono(alero(c.t0), alero(c.t1), arriba(c.t1), arriba(c.t0))) }
        drawPath(base, p.medio(color, lejos))
        for ((i, c) in caras.withIndex()) {
            val forma = poligono(alero(c.t0), alero(c.t1), arriba(c.t1), arriba(c.t0))
            val col = p.por(color, c.luz.coerceIn(-1f, 1f), lejos)
            drawPath(forma, col)
            drawPath(forma, col, style = Stroke(0.5f))
            // El cerco de hollín alrededor del aro, y la mugre del alero.
            val h0 = arriba(c.t0, 0.72f); val h1 = arriba(c.t1, 0.72f)
            drawPath(poligono(h0, h1, arriba(c.t1), arriba(c.t0)), Brush.linearGradient(0f to Color.Transparent, 1f to p.sombra(C.HOLLIN, lejos).copy(alpha = 0.55f + 0.2f * g.desgaste), start = (h0 + h1) / 2f, end = (arriba(c.t0) + arriba(c.t1)) / 2f))
            // Una vara de cada tres se nota bajo el fieltro: el fieltro se hunde un poco entre ellas.
            if (g.detalle && i % 3 == 0) drawLine(p.sombra(color, lejos).copy(alpha = 0.22f), alero(c.t0), arriba(c.t0), (0.02f * s.escala(0f, 0f)).coerceIn(0.3f, 0.9f))
        }
        // El borde del alero: la cuerda que sujeta el fieltro del techo.
        val (a, b) = g.visible()
        val borde = (0..30).map { val t = a - 0.08f + (b - a + 0.16f) * it / 30f; alero(t) }
        drawPath(Path().apply { moveTo(borde[0]); for (q in borde.drop(1)) lineTo(q) }, p.sombra(C.CRIN, lejos), style = Stroke((0.04f * s.escala(0f, 0f)).coerceIn(0.4f, 2.4f), cap = StrokeCap.Round))
        // Las cuerdas que bajan del aro y se cruzan sobre el techo, para el viento.
        if (g.detalle) {
            val az = Azar(Semilla.de(g.semilla, "cuerdas"))
            for (j in 0 until 4) {
                val t0 = (j * PI / 2).toFloat() + az.entre(-0.2f, 0.2f) + 0.4f; val t1 = t0 + 0.9f
                val q0 = arriba(t0); val q1 = alero(t1)
                val nl = V3(sin((t0 + t1) / 2f), 0.6f, -cos((t0 + t1) / 2f))
                if (!s.seVe(V3(g.alero * sin(t1), yAlero, -g.alero * cos(t1)), nl)) continue
                val medio = arriba((t0 + t1) / 2f, 0.5f)
                drawPath(Path().apply { moveTo(q0); cubicTo(q0 + (medio - q0) * 0.6f, medio + (q1 - medio) * 0.3f, q1) }, p.sombra(C.CRIN, lejos).copy(alpha = 0.85f), style = Stroke((0.03f * s.escala(0f, 0f)).coerceIn(0.3f, 1.6f), cap = StrokeCap.Round))
            }
        }
        // El aro: si se ve desde arriba, el hueco negro de humo con su rueda de madera y el fieltro de taparlo, echado atrás.
        if (s.ojo > g.cumbre + 0.1f) {
            val anillo = (0 until 20).map { val t = (2 * PI * it / 20).toFloat(); s.p(g.aro * sin(t), g.cumbre + 0.02f, -g.aro * cos(t)) }
            val hueco = (0 until 20).map { val t = (2 * PI * it / 20).toFloat(); s.p(g.aro * 0.8f * sin(t), g.cumbre + 0.02f, -g.aro * 0.8f * cos(t)) }
            drawPath(poligono(anillo), p.por(C.MADERA, 0.3f, lejos))
            drawPath(poligono(hueco), Brush.verticalGradient(listOf(p.sombra(C.HOLLIN, lejos), lerp(p.medio(C.HOLLIN, lejos), C.LUMBRE, (0.5f * g.dentro * p.l.noche).coerceIn(0f, 1f))), hueco.minOf { it.y }, hueco.maxOf { it.y }))
            if (g.detalle) for (j in 0 until 8) { val t = (PI * j / 4).toFloat(); drawLine(p.sombra(C.MADERA, lejos), s.p(g.aro * 0.8f * sin(t), g.cumbre + 0.03f, -g.aro * 0.8f * cos(t)), s.p(0f, g.cumbre + 0.03f, 0f), (0.025f * s.escala(0f, 0f)).coerceIn(0.2f, 0.8f)) }
            // El fieltro del aro (örkh), echado hacia la espalda de la tienda, con sus cuerdas.
            fun techoEn(x: Float, z: Float): V3 { val rr = sqrt(x * x + z * z).coerceAtLeast(g.aro); return V3(x, g.cumbre - (rr - g.aro) * (g.cumbre - yAlero) / (g.alero - g.aro) + 0.04f, z) }
            val borde = (0..6).map { techoEn(-0.72f + 0.24f * it, 0.62f + 0.05f * sin(it * 1.3f)) } + (6 downTo 0).map { techoEn(-0.66f + 0.22f * it, 1.45f + 0.06f * sin(it * 2.1f)) }
            val tapa = poligono(borde.map { s.p(it) })
            drawPath(tapa, p.por(lerp(color, C.HOLLIN, 0.1f), 0.35f, lejos))
            for (j in 1..3) drawLine(p.sombra(color, lejos).copy(alpha = 0.4f), s.p(techoEn(-0.6f + 0.3f * j, 0.66f)), s.p(techoEn(-0.56f + 0.28f * j, 1.42f)), (0.02f * s.escala(0f, 0f)).coerceIn(0.2f, 0.8f))
            drawPath(tapa, p.sombra(C.CRIN, lejos).copy(alpha = 0.5f), style = Stroke((0.02f * s.escala(0f, 0f)).coerceIn(0.2f, 0.8f)))
        } else {
            // Desde abajo solo se ve el canto del aro.
            val anillo = (0..16).map { val t = (-PI / 2 + PI * it / 16).toFloat(); s.p(g.aro * 1.05f * sin(t), g.cumbre + 0.06f, -g.aro * 1.05f * cos(t)) }
            drawPath(Path().apply { moveTo(anillo[0]); for (q in anillo.drop(1)) lineTo(q) }, p.sombra(C.MADERA, lejos), style = Stroke((0.08f * s.escala(0f, 0f)).coerceIn(0.4f, 3f)))
        }
    }

    /**
     * La puerta: el marco de madera pintado (**[Diseño propio]**: el cuento solo dice que la puerta es de fieltro) y la
     * hoja de fieltro acolchado, cerrada, recogida a un lado o enrollada arriba. Dentro, la penumbra con la celosía, un
     * cofre y el resplandor del fuego del centro.
     */
    private fun DrawScope.puerta(g: Ger, p: Pincel, sol: Sol3) {
        val s = g.s; val lejos = g.lejos; val d = g.mediaPuerta; val h = g.altoPuerta; val noche = p.l.noche
        val esc = s.escala(0f, -g.radio)
        fun rect(t0: Float, t1: Float, y0: Float, y1: Float, r: Float = g.radio + 0.02f): Path {
            val arriba = (0..4).map { g.pared(t0 + (t1 - t0) * it / 4f, y1, r) }; val abajo = (0..4).map { g.pared(t0 + (t1 - t0) * it / 4f, y0, r) }
            return poligono(abajo + arriba.reversed())
        }
        val tFrente = sol.t(s.normal(V3(0f, 0f, -1f)))
        // El marco: dos jambas, el dintel y el umbral, de madera pintada de rojo, gastada en los cantos.
        val marco = Path().apply { addPath(rect(-d * 1.22f, d * 1.22f, 0f, h + 0.12f, g.radio + 0.03f)) }
        drawPath(marco, p.por(C.ROJO, tFrente, lejos))
        drawPath(rect(-d * 1.22f, d * 1.22f, h + 0.08f, h + 0.12f, g.radio + 0.035f), p.luz(lerp(C.ROJO, C.ORO, 0.35f), lejos).copy(alpha = 0.8f))
        if (g.detalle) drawPath(rect(-d * 1.22f, -d * 1.1f, 0f, h + 0.12f, g.radio + 0.035f), p.luz(lerp(C.ROJO, C.MADERA, 0.6f), lejos).copy(alpha = 0.5f))
        // El hueco.
        val hueco = rect(-d, d, 0.04f, h, g.radio + 0.03f)
        val calor = (g.dentro * (0.25f + 0.75f * noche)).coerceIn(0f, 1f)
        val penumbra = lerp(p.sombra(C.HOLLIN, lejos), C.LUMBRE, 0.1f * calor)
        val abajoCalido = lerp(lerp(p.medio(C.MADERA_OSC, lejos), C.HOLLIN, 0.4f), C.LUMBRE, 0.55f * calor)
        val ys = (0..1).map { g.pared(0f, if (it == 0) h else 0.04f).y }
        drawPath(hueco, Brush.verticalGradient(0f to penumbra, 0.55f to lerp(penumbra, abajoCalido, 0.5f), 1f to abajoCalido, startY = ys[0], endY = ys[1]))
        clipPath(hueco) {
            // La celosía del otro lado de la tienda, alumbrada por el fuego: rombos de varas.
            val lat = lerp(p.sombra(C.MADERA, lejos), C.LUMBRE, 0.35f * calor + 0.08f)
            val c = g.centroDePuerta; val w = 2.4f * d * g.radio * esc; val alto = (ys[1] - ys[0])
            var x = c.x - w; val paso = alto * 0.22f
            while (x < c.x + w) { drawLine(lat.copy(alpha = 0.55f), Offset(x, ys[1]), Offset(x + alto * 0.7f, ys[0]), (0.025f * esc).coerceIn(0.3f, 1.2f)); drawLine(lat.copy(alpha = 0.55f), Offset(x, ys[0]), Offset(x + alto * 0.7f, ys[1]), (0.025f * esc).coerceIn(0.3f, 1.2f)); x += paso }
            // Un cofre pintado al fondo y el resplandor del fuego del centro.
            drawRect(lerp(p.sombra(C.ROJO, lejos), C.LUMBRE, 0.3f * calor), Offset(c.x - w * 0.15f, ys[1] - alto * 0.38f), Size(w * 0.5f, alto * 0.22f))
            drawRect(lerp(p.sombra(C.ORO, lejos), C.LUMBRE, 0.3f * calor).copy(alpha = 0.6f), Offset(c.x - w * 0.15f, ys[1] - alto * 0.33f), Size(w * 0.5f, alto * 0.025f))
            if (g.dentro > 0.05f) resplandor(Offset(c.x, ys[1] - alto * 0.05f), alto * 0.9f, (0.3f + 0.7f * noche) * g.dentro, C.LUMBRE)
        }
        // La hoja de fieltro acolchado: blanca, con el pespunte en rombos de hilo azul y el ribete rojo.
        val hoja = lerp(C.FIELTRO, Color.White, 0.35f)
        fun acolchado(forma: Path, t0: Float, t1: Float, y0: Float, y1: Float) {
            drawPath(forma, p.por(hoja, tFrente, lejos))
            if (!g.detalle || esc < 10f) return
            clipPath(forma) {
                // El pespunte: una cenefa por dentro del borde y un rombo con sus volutas en el centro, de hilo pardo.
                val hilo = p.sombra(lerp(C.CRIN, C.ROJO, 0.4f), lejos).copy(alpha = 0.45f); val g2 = (0.012f * esc).coerceIn(0.25f, 0.7f)
                val m0 = 0.18f; val tA = t0 + (t1 - t0) * m0; val tB = t1 - (t1 - t0) * m0; val yA = y0 + (y1 - y0) * 0.1f; val yB = y1 - (y1 - y0) * 0.1f
                drawPath(poligono(g.pared(tA, yA), g.pared(tB, yA), g.pared(tB, yB), g.pared(tA, yB)), hilo, style = Stroke(g2))
                val tm = (t0 + t1) / 2f; val ym = (y0 + y1) / 2f; val dt = (t1 - t0) * 0.22f; val dy = (y1 - y0) * 0.16f
                drawPath(poligono(g.pared(tm, ym + dy), g.pared(tm + dt, ym), g.pared(tm, ym - dy), g.pared(tm - dt, ym)), hilo, style = Stroke(g2))
                for (k in 1..3) drawLine(hilo.copy(alpha = 0.25f), g.pared(tA, y0 + (y1 - y0) * k / 4f), g.pared(tB, y0 + (y1 - y0) * k / 4f), g2 * 0.7f)
            }
            drawPath(forma, p.por(C.ROJO, tFrente, lejos), style = Stroke((0.04f * esc).coerceIn(0.3f, 1.8f)))
        }
        when (g.puerta) {
            Puerta.CERRADA -> {
                acolchado(hueco, -d, d, 0.04f, h)
                // Por las rendijas se escapa la luz del fuego.
                if (noche > 0.3f && g.dentro > 0.05f) {
                    val rendija = Path().apply { moveTo(g.pared(-d, 0.05f, g.radio + 0.04f)); lineTo(g.pared(-d, h, g.radio + 0.04f)); lineTo(g.pared(d, h, g.radio + 0.04f)); lineTo(g.pared(d, 0.05f, g.radio + 0.04f)) }
                    drawPath(rendija, C.LUMBRE.copy(alpha = (0.7f * g.dentro * noche).coerceIn(0f, 1f)), style = Stroke((0.03f * esc).coerceIn(0.4f, 1.4f)), blendMode = BlendMode.Plus)
                }
            }
            Puerta.ENTREABIERTA -> {
                // Recogida hacia un lado, con sus pliegues, y atada.
                val forma = Path().apply { moveTo(g.pared(-d * 0.05f, h, g.radio + 0.05f)); lineTo(g.pared(d * 1.02f, h, g.radio + 0.05f)); lineTo(g.pared(d * 1.02f, 0.04f, g.radio + 0.05f)); cubicTo(g.pared(d * 0.6f, 0.05f, g.radio + 0.06f), g.pared(d * 0.35f, 0.35f, g.radio + 0.08f), g.pared(d * 0.35f, 0.62f, g.radio + 0.08f)); cubicTo(g.pared(d * 0.2f, 0.85f, g.radio + 0.06f), g.pared(-d * 0.02f, 1.0f, g.radio + 0.05f), g.pared(-d * 0.05f, h, g.radio + 0.05f)); close() }
                acolchado(forma, -d * 0.05f, d, 0.04f, h)
                for (j in 1..3) drawLine(p.sombra(hoja, lejos).copy(alpha = 0.6f), g.pared(d * (0.2f + 0.22f * j), h, g.radio + 0.06f), g.pared(d * (0.36f + 0.2f * j), 0.62f, g.radio + 0.07f), (0.02f * esc).coerceIn(0.3f, 1f))
                drawLine(p.medio(C.CRIN, lejos), g.pared(d * 0.3f, 0.62f, g.radio + 0.09f), g.pared(d * 1.05f, 0.66f, g.radio + 0.06f), (0.025f * esc).coerceIn(0.3f, 1.2f))
            }
            Puerta.ABIERTA -> {
                // Enrollada arriba, sobre el dintel.
                val rollo = rect(-d * 1.05f, d * 1.05f, h - 0.16f, h + 0.06f, g.radio + 0.1f)
                drawPath(rollo, Brush.verticalGradient(listOf(p.luz(hoja, lejos), p.medio(hoja, lejos), p.sombra(hoja, lejos)), g.pared(0f, h + 0.06f).y, g.pared(0f, h - 0.16f).y))
                drawPath(rollo, p.medio(C.ROJO, lejos), style = Stroke((0.025f * esc).coerceIn(0.3f, 1.2f)))
                for (u in listOf(-0.6f, 0.6f)) drawLine(p.medio(C.CRIN, lejos), g.pared(d * u, h + 0.1f, g.radio + 0.11f), g.pared(d * u, h - 0.2f, g.radio + 0.11f), (0.025f * esc).coerceIn(0.3f, 1.2f))
            }
        }
    }

    /** El armazón sin fieltro: la celosía que se abre como un acordeón, las varas del techo y el aro. */
    private fun DrawScope.armazon(g: Ger, p: Pincel, sol: Sol3, conPared: Boolean) {
        val s = g.s; val lejos = g.lejos; val esc = s.escala(0f, 0f)
        val madera = C.MADERA
        val grueso = (0.035f * esc).coerceIn(0.3f, 1.6f)
        // Las varas del techo, del canto de la pared al aro.
        for (i in 0 until 40) {
            val t = (2 * PI * i / 40).toFloat()
            val a = g.pared(t, g.pared); val b = s.p(g.aro * sin(t), g.cumbre, -g.aro * cos(t))
            val delante = s.seVe(V3(g.radio * sin(t), 1f, -g.radio * cos(t)), V3(sin(t), 0f, -cos(t)))
            drawLine((if (delante) p.por(madera, 0.4f, lejos) else p.sombra(madera, lejos)).copy(alpha = if (delante) 1f else 0.7f), a, b, grueso * 0.8f, StrokeCap.Round)
        }
        val anillo = (0 until 20).map { val t = (2 * PI * it / 20).toFloat(); s.p(g.aro * sin(t), g.cumbre, -g.aro * cos(t)) }
        drawPath(poligono(anillo), p.por(madera, 0.5f, lejos), style = Stroke(grueso * 2f))
        if (conPared) {
            // La celosía: dos familias de varas cruzadas, primero las de atrás.
            for (atras in listOf(true, false)) for (i in 0 until 70) {
                val t0 = (2 * PI * i / 70).toFloat()
                for (sentido in listOf(-1f, 1f)) {
                    val t1 = t0 + sentido * 0.22f
                    val delante = s.seVe(V3(g.radio * sin(t0), 0.7f, -g.radio * cos(t0)), V3(sin(t0), 0f, -cos(t0)))
                    if (delante == atras) continue
                    if (!atras && abs(t0) < g.mediaPuerta * 1.2f) continue
                    val linea = (0..4).map { g.pared(t0 + (t1 - t0) * it / 4f, g.pared * it / 4f) }
                    drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, (if (atras) p.sombra(madera, lejos).copy(alpha = 0.6f) else p.por(madera, 0.3f, lejos)), style = Stroke(grueso * 0.7f))
                }
            }
            // El marco de la puerta, ya puesto.
            if (g.puertaSeVe) {
                val d = g.mediaPuerta
                for (u in listOf(-1.15f, 1.15f)) drawLine(p.por(C.ROJO, 0.3f, lejos), g.pared(d * u, 0f), g.pared(d * u, g.altoPuerta + 0.1f), grueso * 2.4f)
                drawLine(p.por(C.ROJO, 0.5f, lejos), g.pared(-d * 1.2f, g.altoPuerta + 0.1f), g.pared(d * 1.2f, g.altoPuerta + 0.1f), grueso * 2.4f)
            }
        } else {
            // Con la pared ya cubierta, se ven las varas encima.
        }
    }

    // =================================================================
    //  Caras, palos y tubos (para la carreta y el corral)
    // =================================================================

    /** Una cara plana de una cosa, si se ve: con el color de su lado de luz. Devuelve su forma en el lienzo, o null. */
    private fun DrawScope.cara(s: Sitio, p: Pincel, sol: Sol3, puntos: List<V3>, n: V3, color: Color, lejos: Float, claro: Float = 0f): Path? {
        val c = V3(puntos.sumOf { it.x.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.y.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.z.toDouble() }.toFloat() / puntos.size)
        if (!s.seVe(c, n)) return null
        val forma = poligono(puntos.map { s.p(it) })
        drawPath(forma, p.por(color, (sol.t(s.normal(n)) + claro).coerceIn(-1f, 1f), lejos))
        return forma
    }

    /** Una caja de [x0] a [x1], de [y0] a [y1] y de [z0] a [z1]; [encima] pinta lo de cada cara que se ve (0 delante, 1 detrás, 2 izquierda, 3 derecha, 4 arriba). */
    private fun DrawScope.caja(s: Sitio, p: Pincel, sol: Sol3, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, color: Color, lejos: Float, encima: (DrawScope.(Int, Path) -> Unit)? = null) {
        val caras = listOf(
            listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)) to V3(0f, 0f, -1f),
            listOf(V3(x0, y0, z1), V3(x1, y0, z1), V3(x1, y1, z1), V3(x0, y1, z1)) to V3(0f, 0f, 1f),
            listOf(V3(x0, y0, z0), V3(x0, y0, z1), V3(x0, y1, z1), V3(x0, y1, z0)) to V3(-1f, 0f, 0f),
            listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)) to V3(1f, 0f, 0f),
            listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)) to V3(0f, 1f, 0f),
        )
        for ((i, c) in caras.withIndex()) { val forma = cara(s, p, sol, c.first, c.second, color, lejos, if (i == 4) 0.25f else 0f) ?: continue; if (encima != null) clipPath(forma) { encima(i, forma) } }
    }

    /** Un palo redondo de [a] a [b], de [grueso] metros, con su filo de luz. */
    private fun DrawScope.palo(s: Sitio, p: Pincel, a: V3, b: V3, grueso: Float, color: Color, lejos: Float) {
        val pa = s.p(a); val pb = s.p(b); val d = pb - pa; val largo = d.getDistance().coerceAtLeast(0.01f)
        val nx = -d.y / largo; val ny = d.x / largo
        val wa = grueso * s.escala(a.x, a.z) / 2f; val wb = grueso * s.escala(b.x, b.z) / 2f
        val forma = poligono(pa + Offset(nx, ny) * wa, pb + Offset(nx, ny) * wb, pb - Offset(nx, ny) * wb, pa - Offset(nx, ny) * wa)
        drawPath(forma, p.medio(lerp(color, C.HOLLIN, 0.12f), lejos))
        val luz = if ((nx > 0f) == p.solALaDerecha) 1f else -1f
        val w = (wa + wb) / 2f
        if (w > 0.35f) drawLine(p.luz(color, lejos), pa + Offset(nx, ny) * (wa * 0.45f * luz), pb + Offset(nx, ny) * (wb * 0.45f * luz), (w * 0.7f).coerceAtLeast(0.3f), StrokeCap.Round)
    }

    /** Un rollo (de fieltro, de piel) tendido a lo largo de x, de [x0] a [x1], con el centro en ([y], [z]) y radio [r]. */
    private fun DrawScope.rollo(s: Sitio, p: Pincel, sol: Sol3, x0: Float, x1: Float, y: Float, z: Float, r: Float, color: Color, lejos: Float, cuerdas: Boolean = true) {
        val n = 14
        val caras = (0 until n).map { i ->
            val a0 = (2 * PI * i / n).toFloat(); val a1 = (2 * PI * (i + 1) / n).toFloat(); val am = (a0 + a1) / 2f
            Triple(listOf(V3(x0, y + r * cos(a0), z + r * sin(a0)), V3(x1, y + r * cos(a0), z + r * sin(a0)), V3(x1, y + r * cos(a1), z + r * sin(a1)), V3(x0, y + r * cos(a1), z + r * sin(a1))), V3(0f, cos(am), sin(am)), s.hondo(V3((x0 + x1) / 2f, y + r * cos(am), z + r * sin(am))))
        }.sortedByDescending { it.third }
        for (c in caras) { val f = cara(s, p, sol, c.first, c.second, color, lejos) ?: continue; drawPath(f, p.por(color, sol.t(s.normal(c.second)), lejos), style = Stroke(0.4f)) }
        // La cabeza del rollo que se ve, con la espiral del fieltro enrollado.
        for ((xx, nx) in listOf(x0 to -1f, x1 to 1f)) {
            if (!s.seVe(V3(xx, y, z), V3(nx, 0f, 0f))) continue
            val borde = (0 until 16).map { val a = (2 * PI * it / 16).toFloat(); s.p(xx, y + r * cos(a), z + r * sin(a)) }
            drawPath(poligono(borde), p.por(lerp(color, C.TIERRA, 0.15f), sol.t(s.normal(V3(nx, 0f, 0f))) - 0.2f, lejos))
            val espiral = (0..40).map { val a = it * 0.5f; val rr = r * (0.12f + 0.85f * it / 40f); s.p(xx, y + rr * cos(a), z + rr * sin(a)) }
            drawPath(Path().apply { moveTo(espiral[0]); for (q in espiral.drop(1)) lineTo(q) }, p.sombra(color, lejos).copy(alpha = 0.6f), style = Stroke((0.012f * s.escala(xx, z)).coerceIn(0.25f, 0.8f)))
        }
        if (cuerdas) for (u in listOf(0.22f, 0.78f)) {
            val xx = x0 + (x1 - x0) * u
            val linea = (0..12).map { val a = (-PI / 2 + PI * 1.4 * it / 12).toFloat(); V3(xx, y + (r + 0.01f) * cos(a), z + (r + 0.01f) * sin(a)) }.filter { s.seVe(it, V3(0f, (it.y - y) / r, (it.z - z) / r)) }.map { s.p(it) }
            if (linea.size > 1) drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(C.CRIN, lejos), style = Stroke((0.025f * s.escala(xx, z)).coerceIn(0.3f, 1.2f), cap = StrokeCap.Round))
        }
    }

    // =================================================================
    //  La carreta
    // =================================================================

    /**
     * Una carreta de dos ruedas de madera «altas como un hombre» (1.6 m), con su plataforma y sus dos varas, puesta en
     * [s]: su frente (por donde salen las varas) mira hacia el giro del sitio. [cargada]: con un cofre pintado, rollos de
     * fieltro, la celosía plegada y el haz de varas del techo, todo atado; si no, con una manta y un cubo. [cuna]: la
     * cuna de madera de Saran, atada encima. Las varas descansan en el suelo, como dice el cuento.
     */
    class Carreta(val s: Sitio, val semilla: Long, val cargada: Boolean = true, val cuna: Boolean = false, val lejos: Float = 0f, val detalle: Boolean = true) {
        val radio = 0.8f; val eje = 0.84f; val zEje = 0.15f
        val tabla = 0.92f
        /** El cubo de cada rueda, en el lienzo. */
        fun cubo(lado: Float): Offset = s.p(eje * lado, radio, zEje)
    }

    private fun DrawScope.rueda(c: Carreta, p: Pincel, sol: Sol3, lado: Float) {
        val s = c.s; val lejos = c.lejos; val xw = c.eje * lado; val R = c.radio; val yc = R; val zc = c.zEje
        val esc = s.escala(xw, zc); val madera = lerp(C.MADERA, C.TIERRA, 0.2f)
        fun pt(x: Float, a: Float, r: Float) = s.p(x, yc + r * cos(a), zc + r * sin(a))
        val fuera = if (s.seVe(V3(xw, yc, zc), V3(lado, 0f, 0f))) lado else -lado      // la cara de la rueda que se ve
        val tCara = sol.t(s.normal(V3(fuera, 0f, 0f)))
        val n = 40
        // El canto de la llanta: la banda que se ve por fuera, en cuatro tramos (las pinas), con sus juntas.
        for (i in 0 until n) {
            val a0 = (2 * PI * i / n).toFloat(); val a1 = (2 * PI * (i + 1) / n).toFloat(); val am = (a0 + a1) / 2f
            val nl = V3(0f, cos(am), sin(am))
            if (!s.seVe(V3(xw, yc + R * cos(am), zc + R * sin(am)), nl)) continue
            drawPath(poligono(pt(xw - 0.05f, a0, R), pt(xw + 0.05f, a0, R), pt(xw + 0.05f, a1, R), pt(xw - 0.05f, a1, R)), p.por(madera, sol.t(s.normal(nl)) * 0.8f, lejos))
        }
        // La cara: el aro de la llanta, los rayos y el cubo.
        val xf = xw + fuera * 0.05f
        val afuera = (0 until n).map { pt(xf, (2 * PI * it / n).toFloat(), R) }; val adentro = (0 until n).map { pt(xf, (2 * PI * it / n).toFloat(), R - 0.1f) }
        val aro = Path().apply { fillType = PathFillType.EvenOdd; addPath(poligono(afuera)); addPath(poligono(adentro)) }
        // Los rayos, de la cara de dentro y de la de fuera.
        val rayos = 12
        for (i in 0 until rayos) {
            val a = (2 * PI * i / rayos).toFloat() + 0.13f
            val q0 = pt(xw + fuera * 0.09f, a, 0.13f); val q1 = pt(xf, a, R - 0.08f)
            val g = (0.05f * esc).coerceIn(0.4f, 4f)
            drawLine(p.por(madera, tCara - 0.35f, lejos), q0, q1, g, StrokeCap.Butt)
            val luzLado = if (sin(a) * (if (p.solALaDerecha) 1f else -1f) > 0f) 0.3f else -0.2f
            if (g > 1.2f) drawLine(p.por(lerp(madera, C.FIELTRO, 0.25f), tCara + luzLado, lejos).copy(alpha = 0.7f), q0, q1, g * 0.35f, StrokeCap.Butt)
        }
        drawPath(aro, p.por(madera, tCara, lejos))
        if (c.detalle) {
            // La veta y el canto gastado, más claro; las juntas de las pinas, con su clavija.
            drawPath(poligono(afuera), p.luz(lerp(madera, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.45f), style = Stroke((0.012f * esc).coerceIn(0.25f, 0.9f)))
            drawPath(poligono(adentro), p.sombra(madera, lejos).copy(alpha = 0.7f), style = Stroke((0.015f * esc).coerceIn(0.25f, 1f)))
            for (j in 0 until 6) { val a = (2 * PI * j / 6).toFloat() + 0.4f; drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.6f), pt(xf, a, R), pt(xf, a + 0.03f, R - 0.1f), (0.012f * esc).coerceIn(0.25f, 0.9f)); drawCircle(p.medio(C.MADERA_OSC, lejos), (0.015f * esc).coerceIn(0.2f, 1f), pt(xf, a + 0.12f, R - 0.05f)) }
            val az = Azar(Semilla.de(c.semilla, "rueda", lado.toInt()))
            repeat(8) { val a = az.entre(0f, 6.28f); val da = az.entre(0.1f, 0.35f); drawLine(p.sombra(madera, lejos).copy(alpha = 0.35f), pt(xf, a, R - 0.03f), pt(xf, a + da, R - 0.04f), (0.008f * esc).coerceIn(0.2f, 0.6f)) }
            // El barro seco de la llanta, abajo.
            drawPath(Path().apply { val q = (0..10).map { pt(xf, (PI * 0.65 + PI * 0.7 * it / 10).toFloat(), R - 0.02f) }; moveTo(q[0]); for (qq in q.drop(1)) lineTo(qq) }, p.medio(C.TIERRA, lejos).copy(alpha = 0.55f), style = Stroke((0.06f * esc).coerceIn(0.4f, 3f)))
        }
        // El cubo: un tarugo que asoma, con su clavija.
        val cara = (0 until 16).map { pt(xw + fuera * 0.17f, (2 * PI * it / 16).toFloat(), 0.13f) }
        val fondo = (0 until 16).map { pt(xw + fuera * 0.02f, (2 * PI * it / 16).toFloat(), 0.15f) }
        drawPath(poligono(envolvente(cara + fondo)), p.por(C.MADERA_OSC, tCara - 0.2f, lejos))
        drawPath(poligono(cara), p.por(madera, tCara, lejos))
        drawCircle(p.medio(C.HOLLIN, lejos), (0.035f * esc).coerceIn(0.3f, 2f), pt(xw + fuera * 0.18f, 0f, 0f))
    }

    /** Pinta la carreta [c]. */
    fun DrawScope.carreta(c: Carreta, p: Pincel, sol: Sol3) {
        val s = c.s; val lejos = c.lejos; val az = Azar(c.semilla); val madera = lerp(C.MADERA, C.TIERRA, 0.2f)
        val t0 = c.tabla
        val partes = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        for (lado in listOf(-1f, 1f)) partes += parte(s.hondo(V3(c.eje * lado, c.radio, c.zEje))) { rueda(c, p, sol, lado) }
        // Las varas, que salen de la plataforma y bajan al suelo.
        for (lado in listOf(-1f, 1f)) {
            val a = V3(0.44f * lado, t0 + 0.02f, -1.1f); val b = V3(0.3f * lado, 0.03f, -3.7f)
            partes += parte(s.hondo(V3(0.4f * lado, 0.5f, -2.3f))) {
                palo(s, p, a, b, 0.09f, madera, lejos)
                // La tabla del yugo, atada entre las dos, cerca de la punta.
                if (lado > 0f) palo(s, p, V3(-0.36f, 0.42f, -3.1f), V3(0.36f, 0.42f, -3.1f), 0.07f, madera, lejos)
            }
        }
        // La plataforma y la carga.
        partes += parte(s.hondo(V3(0f, t0, 0f))) {
            caja(s, p, sol, -0.62f, 0.62f, t0 - 0.12f, t0, -1.15f, 1.05f, madera, lejos) { i, _ -> if (i == 4 && c.detalle) for (j in 1..5) { val z = -1.15f + 2.2f * j / 6f; drawLine(p.sombra(madera, lejos), s.p(-0.62f, t0, z), s.p(0.62f, t0, z), (0.01f * s.escala(0f, z)).coerceIn(0.2f, 0.8f)) } }
            // Las barandillas: dos varas a cada lado, sobre estacas.
            for (lado in listOf(-1f, 1f)) {
                for (z in listOf(-1.05f, -0.1f, 0.95f)) palo(s, p, V3(0.6f * lado, t0, z), V3(0.6f * lado, t0 + 0.3f, z), 0.05f, madera, lejos)
                palo(s, p, V3(0.6f * lado, t0 + 0.28f, -1.12f), V3(0.6f * lado, t0 + 0.28f, 1.02f), 0.05f, madera, lejos)
            }
            if (c.cargada) carga(c, p, sol, az) else {
                // Vacía: una manta de fieltro doblada y un cubo de madera.
                caja(s, p, sol, -0.45f, 0.3f, t0, t0 + 0.12f, -0.6f, 0.4f, lerp(C.FIELTRO, C.TIERRA, 0.15f), lejos)
                caja(s, p, sol, 0.3f, 0.52f, t0, t0 + 0.26f, 0.5f, 0.75f, madera, lejos)
            }
            if (c.cuna) caja(s, p, sol, -0.5f, -0.05f, t0 + 0.55f, t0 + 0.8f, 0.0f, 0.8f, lerp(C.ROJO, C.MADERA, 0.4f), lejos) { i, _ -> if (i != 4) drawRect(p.luz(C.ORO, lejos).copy(alpha = 0.5f), Offset(-400f, 0f), Size(1200f, 1200f), blendMode = BlendMode.Overlay) }
        }
        for ((_, pintar) in partes.sortedByDescending { it.first }) pintar()
    }

    /** La carga: el cofre pintado, los rollos de fieltro, la celosía plegada y el haz de varas, atados con cuerdas de crin. */
    private fun DrawScope.carga(c: Carreta, p: Pincel, sol: Sol3, az: Azar) {
        val s = c.s; val lejos = c.lejos; val t0 = c.tabla
        val piezas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        // El cofre, rojo, con sus flejes de cobre y la cerradura.
        piezas += parte(s.hondo(V3(-0.15f, t0 + 0.3f, 0.6f))) {
            caja(s, p, sol, -0.55f, 0.18f, t0, t0 + 0.55f, 0.25f, 0.98f, C.ROJO, lejos) { i, _ ->
                if (i == 4) return@caja
                for (u in listOf(0.12f, 0.88f)) { val pts = when (i) { 0, 1 -> listOf(V3(-0.55f + 0.73f * u, t0, if (i == 0) 0.25f else 0.98f), V3(-0.55f + 0.73f * u, t0 + 0.55f, if (i == 0) 0.25f else 0.98f)); else -> listOf(V3(if (i == 2) -0.55f else 0.18f, t0, 0.25f + 0.73f * u), V3(if (i == 2) -0.55f else 0.18f, t0 + 0.55f, 0.25f + 0.73f * u)) }; drawLine(p.luz(C.ORO, lejos), s.p(pts[0]), s.p(pts[1]), (0.035f * s.escala(0f, 0.6f)).coerceIn(0.3f, 2f)) }
                // Las espirales doradas del frente, gastadas.
                if (c.detalle && (i == 0 || i == 2 || i == 3)) { val q = when (i) { 0 -> V3(-0.18f, t0 + 0.3f, 0.25f); 2 -> V3(-0.55f, t0 + 0.3f, 0.6f); else -> V3(0.18f, t0 + 0.3f, 0.6f) }; drawCircle(p.medio(C.ORO, lejos).copy(alpha = 0.6f), (0.1f * s.escala(q.x, q.z)), s.p(q), style = Stroke((0.02f * s.escala(q.x, q.z)).coerceIn(0.3f, 1f))); drawCircle(p.medio(C.ORO, lejos).copy(alpha = 0.6f), (0.05f * s.escala(q.x, q.z)), s.p(q), style = Stroke((0.02f * s.escala(q.x, q.z)).coerceIn(0.3f, 1f))) }
            }
        }
        // Dos rollos de fieltro y uno encima.
        piezas += parte(s.hondo(V3(0f, t0 + 0.22f, -0.75f))) { rollo(s, p, sol, -0.62f, 0.62f, t0 + 0.22f, -0.75f, 0.22f, C.FIELTRO, lejos) }
        piezas += parte(s.hondo(V3(0f, t0 + 0.22f, -0.3f))) { rollo(s, p, sol, -0.6f, 0.64f, t0 + 0.22f, -0.3f, 0.21f, lerp(C.FIELTRO, C.FIELTRO_VIEJO, 0.6f), lejos) }
        piezas += parte(s.hondo(V3(0f, t0 + 0.6f, -0.52f)) - 0.01f) { rollo(s, p, sol, -0.55f, 0.58f, t0 + 0.6f, -0.52f, 0.19f, lerp(C.FIELTRO, Color.White, 0.2f), lejos) }
        // La celosía plegada, de pie contra el cofre: un haz de varas cruzadas.
        piezas += parte(s.hondo(V3(0.4f, t0 + 0.6f, 0.4f))) {
            val esquinas = listOf(V3(0.25f, t0, 0.05f), V3(0.58f, t0, 0.05f), V3(0.58f, t0 + 1.15f, 0.25f), V3(0.25f, t0 + 1.15f, 0.25f))
            val canto = listOf(V3(0.25f, t0, 0.05f), V3(0.25f, t0, 0.95f), V3(0.25f, t0 + 1.15f, 1.0f), V3(0.25f, t0 + 1.15f, 0.25f))
            for ((pl, n) in listOf(esquinas to V3(0f, -0.2f, -1f), canto to V3(-1f, 0f, 0f), listOf(V3(0.58f, t0, 0.05f), V3(0.58f, t0, 0.95f), V3(0.58f, t0 + 1.15f, 1.0f), V3(0.58f, t0 + 1.15f, 0.25f)) to V3(1f, 0f, 0f))) {
                if (!s.seVe(pl[0], n)) continue
                val forma = poligono(pl.map { s.p(it) })
                drawPath(forma, p.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.55f))
                clipPath(forma) { val tt = sol.t(s.normal(n)); for (j in 0..12) { val u = j / 12f; drawLine(p.por(C.MADERA, tt, lejos), s.p(pl[0] + (pl[1] - pl[0]) * u), s.p(pl[3] + (pl[2] - pl[3]) * (u + 0.35f)), (0.03f * s.escala(0.4f, 0.4f)).coerceIn(0.3f, 1.5f)); drawLine(p.por(C.MADERA, tt - 0.2f, lejos), s.p(pl[0] + (pl[1] - pl[0]) * u), s.p(pl[3] + (pl[2] - pl[3]) * (u - 0.35f)), (0.03f * s.escala(0.4f, 0.4f)).coerceIn(0.3f, 1.5f)) } }
            }
        }
        // El haz de varas del techo, largo, que sobresale por delante y por detrás.
        piezas += parte(s.hondo(V3(-0.3f, t0 + 0.9f, 0f)) - 0.02f) {
            for (j in 0 until 6) { val dx = -0.35f + 0.05f * j + az.entre(-0.02f, 0.02f); val dy = 0.02f * (j % 3); palo(s, p, V3(dx, t0 + 0.82f + dy, 1.55f), V3(dx + 0.06f, t0 + 0.88f + dy, -2.0f), 0.045f, lerp(C.MADERA, C.ROJO, 0.35f), lejos) }
            // Las cuerdas que lo atan todo, de barandilla a barandilla.
            for (z in listOf(-0.6f, 0.4f)) {
                val q = listOf(V3(-0.62f, t0 + 0.28f, z), V3(-0.4f, t0 + 0.95f, z), V3(0.2f, t0 + 0.98f, z), V3(0.62f, t0 + 0.28f, z)).map { s.p(it) }
                drawPath(Path().apply { moveTo(q[0]); cubicTo(q[1], q[2], q[3]) }, p.sombra(C.CRIN, lejos), style = Stroke((0.025f * s.escala(0f, z)).coerceIn(0.3f, 1.4f), cap = StrokeCap.Round))
            }
        }
        for ((_, pintar) in piezas.sortedByDescending { it.first }) pintar()
    }

    /** La sombra de la carreta en el suelo (el aro y los rayos de cada rueda, la plataforma con su carga, las varas). Va dentro de una `capaSombra`. */
    fun DrawScope.sombraDeCarreta(c: Carreta, sol: Sol3, color: Color) {
        val s = c.s
        fun suelo(a: V3): Offset { val X = s.mx(a.x, a.z) + sol.sx * a.y; val Z = s.mz(a.x, a.z) + sol.sz * a.y; return s.pm(X, 0f, Z) }
        for (lado in listOf(-1f, 1f)) {
            val xw = c.eje * lado
            val aro = (0 until 32).map { val a = (2 * PI * it / 32).toFloat(); suelo(V3(xw, c.radio + c.radio * cos(a), c.zEje + c.radio * sin(a))) }
            val esc = s.escala(xw, c.zEje)
            drawPath(poligono(aro), color, style = Stroke((0.1f * esc).coerceAtLeast(0.6f)))
            for (i in 0 until 12) { val a = (2 * PI * i / 12).toFloat() + 0.13f; drawLine(color, suelo(V3(xw, c.radio, c.zEje)), suelo(V3(xw, c.radio + (c.radio - 0.08f) * cos(a), c.zEje + (c.radio - 0.08f) * sin(a))), (0.05f * esc).coerceAtLeast(0.4f)) }
            drawLine(color, suelo(V3(0.44f * lado, c.tabla, -1.1f)), suelo(V3(0.3f * lado, 0.03f, -3.7f)), (0.08f * s.escala(0f, -2f)).coerceAtLeast(0.4f), StrokeCap.Round)
        }
        val alto = if (c.cargada) c.tabla + 1.15f else c.tabla + 0.3f
        val caja = listOf(V3(-0.62f, c.tabla - 0.1f, -1.15f), V3(0.62f, c.tabla - 0.1f, -1.15f), V3(0.62f, c.tabla - 0.1f, 1.05f), V3(-0.62f, c.tabla - 0.1f, 1.05f),
            V3(-0.62f, alto * 0.8f, -1.0f), V3(0.62f, alto * 0.8f, -1.0f), V3(0.62f, alto, 0.95f), V3(-0.62f, alto, 0.95f))
        drawPath(poligono(envolvente(caja.map { suelo(it) })), color)
    }

    // =================================================================
    //  El corral de piedra y estiércol
    // =================================================================

    /**
     * Un corral «de piedras y estiércol amontonados»: un muro bajo y grueso que sigue los [puntos] (x, z en metros, en los
     * ejes de [s]), de [alto] metros con el canto desigual. [abierto]: el tramo que es una cancilla de varas (su índice),
     * o −1. [nieve], de 0 a 1: la que se queda en el canto y arrimada al pie.
     */
    class Corral(val s: Sitio, val puntos: List<Offset>, val semilla: Long, val alto: Float = 1.15f, val grueso: Float = 0.5f, val abierto: Int = -1, val nieve: Float = 0f, val lejos: Float = 0f, val detalle: Boolean = true)

    /** Pinta el corral [c]. */
    fun DrawScope.corral(c: Corral, p: Pincel, sol: Sol3) {
        val s = c.s; val az = Azar(c.semilla); val lejos = c.lejos
        // Cada muro se parte en tramos cortos de altura desigual: el canto de piedras y estiércol no es una regla.
        class Tramo(val a: Offset, val b: Offset, val ha: Float, val hb: Float, val hondo: Float, val cancilla: Boolean)
        val tramos = ArrayList<Tramo>()
        for (i in 0 until c.puntos.size - 1) {
            val a0 = c.puntos[i]; val b0 = c.puntos[i + 1]
            if (i == c.abierto) { tramos += Tramo(a0, b0, c.alto, c.alto, s.hondo(V3((a0.x + b0.x) / 2f, 0.5f, (a0.y + b0.y) / 2f)), true); continue }
            val n = ((b0 - a0).getDistance() / 0.55f).toInt().coerceAtLeast(1)
            var h0 = c.alto * az.entre(0.86f, 1.06f)
            for (k in 0 until n) {
                val a = a0 + (b0 - a0) * (k / n.toFloat()); val b = a0 + (b0 - a0) * ((k + 1) / n.toFloat()); val h1 = c.alto * az.entre(0.84f, 1.08f)
                tramos += Tramo(a, b, h0, h1, s.hondo(V3((a.x + b.x) / 2f, 0.5f, (a.y + b.y) / 2f)), false); h0 = h1
            }
        }
        for (t in tramos.sortedByDescending { it.hondo }) {
            val a = t.a; val b = t.b; val d = b - a; val largo = d.getDistance().coerceAtLeast(0.01f)
            val n = Offset(d.y, -d.x) / largo * (c.grueso / 2f)
            val ha = t.ha; val hb = t.hb
            if (t.cancilla) { cancilla(c, p, a, b, minOf(ha, hb)); continue }
            // Los dos costados, el canto de arriba y las cabezas, cada uno con su luz.
            val caras = listOf(
                listOf(V3(a.x + n.x, 0f, a.y + n.y), V3(b.x + n.x, 0f, b.y + n.y), V3(b.x + n.x * 0.7f, hb, b.y + n.y * 0.7f), V3(a.x + n.x * 0.7f, ha, a.y + n.y * 0.7f)) to V3(n.x, 0.15f, n.y),
                listOf(V3(a.x - n.x, 0f, a.y - n.y), V3(b.x - n.x, 0f, b.y - n.y), V3(b.x - n.x * 0.7f, hb, b.y - n.y * 0.7f), V3(a.x - n.x * 0.7f, ha, a.y - n.y * 0.7f)) to V3(-n.x, 0.15f, -n.y),
                listOf(V3(a.x - n.x * 0.7f, ha, a.y - n.y * 0.7f), V3(b.x - n.x * 0.7f, hb, b.y - n.y * 0.7f), V3(b.x + n.x * 0.7f, hb, b.y + n.y * 0.7f), V3(a.x + n.x * 0.7f, ha, a.y + n.y * 0.7f)) to V3(0f, 1f, 0f),
                listOf(V3(a.x - n.x, 0f, a.y - n.y), V3(a.x + n.x, 0f, a.y + n.y), V3(a.x + n.x * 0.7f, ha, a.y + n.y * 0.7f), V3(a.x - n.x * 0.7f, ha, a.y - n.y * 0.7f)) to V3(-d.x, 0f, -d.y),
                listOf(V3(b.x - n.x, 0f, b.y - n.y), V3(b.x + n.x, 0f, b.y + n.y), V3(b.x + n.x * 0.7f, hb, b.y + n.y * 0.7f), V3(b.x - n.x * 0.7f, hb, b.y - n.y * 0.7f)) to V3(d.x, 0f, d.y),
            )
            for ((k, cc) in caras.withIndex()) {
                val forma = cara(s, p, sol, cc.first, cc.second, if (k == 2) lerp(C.ESTIERCOL, C.PIEDRA, 0.3f) else lerp(C.PIEDRA, C.ESTIERCOL, 0.35f), lejos, if (k == 2) 0.2f else 0f) ?: continue
                if (!c.detalle) continue
                val tt = sol.t(s.normal(cc.second))
                clipPath(forma) {
                    val q = cc.first
                    fun en(u: Float, v: Float) = s.p(q[0] + (q[1] - q[0]) * u + (q[3] - q[0] + (q[2] - q[1] - (q[3] - q[0])) * u) * v)
                    val esc = s.escala((a.x + b.x) / 2f, (a.y + b.y) / 2f)
                    // Las capas de estiércol pisado entre las hiladas de piedra.
                    if (k < 2) for (j in 1..4) { val v = j / 5f + az.entre(-0.04f, 0.04f); drawPath(Path().apply { moveTo(en(0f, v)); for (u in 1..6) lineTo(en(u / 6f, v + az.entre(-0.03f, 0.03f))) }, p.por(C.ESTIERCOL, tt - 0.4f, lejos).copy(alpha = 0.7f), style = Stroke((0.07f * esc).coerceIn(0.4f, 3.5f))) }
                    // Las piedras: cantos de río, cada uno con su lado de luz.
                    val cuantas = ((largo * (if (k < 2) 9f else 4f)) * (esc / 20f).coerceIn(0.4f, 2f)).toInt().coerceIn(3, 70)
                    repeat(cuantas) {
                        val u = az.real(); val v = az.entre(0.05f, 0.95f); val ctr = en(u, v); val rr = az.entre(0.06f, 0.14f) * esc
                        val col = lerp(C.PIEDRA, if (it % 3 == 0) C.TIERRA else C.FIELTRO_VIEJO, az.entre(0f, 0.4f))
                        drawOval(p.por(col, tt - 0.5f, lejos), Offset(ctr.x - rr, ctr.y - rr * 0.55f), Size(rr * 2f, rr * 1.2f))
                        drawOval(p.por(col, tt + 0.4f, lejos), Offset(ctr.x - rr * 0.8f + (if (p.solALaDerecha) rr * 0.25f else -rr * 0.25f), ctr.y - rr * 0.6f), Size(rr * 1.4f, rr * 0.75f))
                    }
                    // La nieve que se queda en el canto.
                    if (k == 2 && c.nieve > 0.05f) drawPath(forma, p.por(C.NIEVE, 0.6f, lejos).copy(alpha = (0.4f + 0.6f * c.nieve).coerceAtMost(1f)))
                    if (k < 2 && c.nieve > 0.05f) drawPath(Path().apply { moveTo(en(0f, 0.92f)); for (u in 1..8) lineTo(en(u / 8f, 0.86f + az.entre(0f, 0.1f))); lineTo(en(1f, 1.02f)); lineTo(en(0f, 1.02f)); close() }, p.por(C.NIEVE, tt + 0.3f, lejos).copy(alpha = 0.9f * c.nieve))
                }
            }
        }
    }

    /** La cancilla del corral: dos postes y tres varas atadas con crin. */
    private fun DrawScope.cancilla(c: Corral, p: Pincel, a: Offset, b: Offset, alto: Float) {
        val s = c.s; val lejos = c.lejos; val madera = lerp(C.MADERA, C.PIEDRA, 0.3f)
        palo(s, p, V3(a.x, 0f, a.y), V3(a.x, alto * 1.15f, a.y), 0.12f, madera, lejos)
        palo(s, p, V3(b.x, 0f, b.y), V3(b.x, alto * 1.12f, b.y), 0.12f, madera, lejos)
        for (j in 0 until 3) { val y = alto * (0.3f + 0.3f * j); palo(s, p, V3(a.x, y, a.y), V3(b.x, y + 0.05f * (j - 1), b.y), 0.07f, madera, lejos) }
        palo(s, p, V3(a.x, alto * 0.25f, a.y), V3(b.x, alto * 0.9f, b.y), 0.06f, madera, lejos)
    }

    // =================================================================
    //  Las ovejas
    // =================================================================

    /**
     * Una oveja de cola gorda, la de la estepa: el cuerpo de lana blanca o crema, de bulto desigual, y la cabeza oscura.
     * Pisa en [pie] y mide [largo] unidades del hocico a la cola; [mira] 1 hacia la derecha, −1 hacia la izquierda.
     * [pasta]: con la cabeza abajo. [patas]: si se le ven (las de la primera fila). Con [cabra], una cabra: más flaca,
     * oscura, de cuernos curvos.
     */
    fun DrawScope.oveja(p: Pincel, pie: Offset, largo: Float, mira: Float, az: Azar, pasta: Boolean, lejos: Float = 0f, patas: Boolean = true, cabra: Boolean = false, lana: Color = C.LANA, cabeza: Color = C.CABEZA) {
        val u = largo; val dir = if (mira >= 0f) 1f else -1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        val luzDer = p.solALaDerecha == (dir > 0f)
        val alto = if (cabra) 0.62f else 0.56f
        // Las patas: cortas y oscuras.
        if (patas) for ((x, cerca) in listOf(-0.28f to false, 0.24f to false, -0.2f to true, 0.32f to true)) drawLine(p.medio(if (cerca) cabeza else lerp(cabeza, C.HOLLIN, 0.4f), lejos), q(x, -0.22f), q(x + (if (cerca) 0.02f else -0.02f), 0f), (0.07f * u).coerceAtLeast(0.4f), StrokeCap.Round)
        // El cuerpo: bultos de lana en dos pisos, el anca con la cola gorda.
        val cuerpo = Path()
        val bultos = if (cabra) listOf(Triple(-0.18f, -0.4f, 0.2f), Triple(0.12f, -0.42f, 0.2f), Triple(0.3f, -0.4f, 0.17f), Triple(-0.02f, -0.36f, 0.21f))
        else listOf(Triple(-0.22f, -0.38f, 0.22f), Triple(0.02f, -0.43f, 0.24f), Triple(0.26f, -0.4f, 0.22f), Triple(-0.1f, -0.5f, 0.17f), Triple(0.16f, -0.52f, 0.17f), Triple(0.38f, -0.3f, 0.15f), Triple(0.0f, -0.3f, 0.24f))
        for ((x, y, r) in bultos) { val rr = r * az.entre(0.9f, 1.1f); val c = q(x + az.entre(-0.02f, 0.02f), y); cuerpo.addOval(Rect(c, rr * u)) }
        val col = if (cabra) lerp(lana, C.HOLLIN, az.entre(0.2f, 0.75f)) else lerp(lana, if (az.entero(5) == 0) C.FIELTRO_VIEJO else C.LANA, 0.5f)
        drawPath(cuerpo, p.por(col, -0.1f, lejos))
        val arriba = pie.y - (alto + 0.2f) * u
        clipPath(cuerpo) {
            drawRect(Brush.verticalGradient(0f to p.luz(col, lejos), 0.45f to p.por(col, 0.15f, lejos), 1f to p.sombra(col, lejos), startY = arriba, endY = pie.y - 0.12f * u), Offset(pie.x - u, arriba), Size(2f * u, alto * u + 0.2f * u))
            val x0 = pie.x - 0.5f * u; val x1 = pie.x + 0.6f * u
            drawRect(Brush.horizontalGradient(if (luzDer) listOf(p.sombra(col, lejos).copy(alpha = 0.5f), Color.Transparent) else listOf(Color.Transparent, p.sombra(col, lejos).copy(alpha = 0.5f)), x0, x1), Offset(x0, arriba), Size(x1 - x0, alto * u + 0.2f * u))
            // Los rizos de la lana, en el lado de la luz.
            if (u > 8f && !cabra) repeat((u * 0.9f).toInt().coerceAtMost(30)) {
                // Los mechones de lana: toques cortos, claros arriba y del lado del sol, con su hueco oscuro debajo.
                val c = q(az.entre(-0.38f, 0.44f), az.entre(-0.64f, -0.22f)); val w = az.entre(0.05f, 0.09f) * u
                drawOval(p.sombra(col, lejos).copy(alpha = 0.25f), Offset(c.x - w, c.y - w * 0.2f), Size(w * 2f, w * 0.9f))
                drawOval(p.luz(lerp(col, Color.White, 0.25f), lejos).copy(alpha = 0.5f), Offset(c.x - w * 0.9f, c.y - w * 0.55f), Size(w * 1.8f, w * 0.8f))
            }
        }
        // La cabeza: abajo, pastando, o en alto, con las orejas caídas a los lados.
        val cuello = if (pasta) q(-0.42f, -0.18f) else q(-0.45f, -0.5f)
        val forma = Path().apply { addOval(Rect(cuello.x - 0.12f * u, cuello.y - 0.1f * u, cuello.x + 0.12f * u, cuello.y + 0.12f * u)) }
        val hocico = if (pasta) q(-0.54f, -0.02f) else q(-0.63f, -0.42f)
        forma.addPath(Path().apply { moveTo(cuello + Offset(0f, -0.1f * u)); cubicTo(cuello + Offset(-0.1f * u * dir, -0.13f * u), hocico + Offset(0.02f * u * dir, -0.08f * u), hocico + Offset(0f, -0.02f * u)); cubicTo(hocico + Offset(-0.02f * u * dir, 0.04f * u), hocico + Offset(0.04f * u * dir, 0.07f * u), hocico + Offset(0.08f * u * dir, 0.06f * u)); lineTo(cuello + Offset(0f, 0.11f * u)); close() })
        drawPath(forma, p.por(if (cabra) lerp(col, C.HOLLIN, 0.2f) else cabeza, if (luzDer) -0.2f else 0.4f, lejos))
        if (cabra && u > 5f) { drawLine(p.medio(C.PIEDRA, lejos), cuello + Offset(0f, -0.08f * u), cuello + Offset(0.12f * u * dir, -0.24f * u), (0.04f * u).coerceAtLeast(0.4f), StrokeCap.Round) }
        else drawLine(p.medio(cabeza, lejos), cuello + Offset(0f, -0.04f * u), cuello + Offset(0.14f * u * dir, if (pasta) -0.01f * u else 0.02f * u), (0.05f * u).coerceAtLeast(0.4f), StrokeCap.Round)
        // La cola gorda, una papada de grasa detrás.
        if (!cabra) drawOval(p.por(col, -0.3f, lejos), q(0.38f, -0.32f).let { Offset(minOf(it.x, it.x + 0.2f * u * dir), it.y) }, Size(0.2f * u, 0.22f * u))
    }

    /**
     * Un rebaño apretado: las ovejas de [sitios] (dónde pisa cada una, de atrás adelante se ordenan solas) con su talla
     * según la profundidad ([talla] de la y). Se tapan unas a otras: lomos que asoman detrás de lomos. Una de cada
     * [cabras], cabra. Las de detrás no enseñan las patas.
     */
    fun DrawScope.rebano(p: Pincel, sitios: List<Offset>, talla: (Float) -> Float, az: Azar, lejos: (Float) -> Float, pastan: Float = 0.5f, cabras: Int = 7) {
        val orden = sitios.sortedBy { it.y }
        for ((i, q) in orden.withIndex()) {
            val u = talla(q.y); val l = lejos(q.y)
            drawOval(p.sombra(C.TIERRA, l).copy(alpha = 0.35f), Offset(q.x - 0.4f * u, q.y - 0.06f * u), Size(0.9f * u, 0.14f * u))
            val delante = orden.drop(i + 1).none { abs(it.x - q.x) < u * 0.5f && it.y - q.y < u * 0.3f && it.y > q.y }
            oveja(p, q, u, if (az.entero(3) == 0) 1f else -1f, az, az.real() < pastan, l, delante, cabra = az.entero(cabras) == 0)
        }
    }

    // =================================================================
    //  Los caballos
    // =================================================================

    /** Los pelos de los caballos del campamento: el Negro, bayos, tordos, el blanco, alazanes, isabelinos. */
    val NEGRO = lerp(C.HOLLIN, C.CUERO, 0.3f)
    val BAYO = lerp(C.CUERO, C.ORO, 0.3f)
    val TORDO = lerp(C.PIEDRA, C.FIELTRO, 0.25f)
    val BLANCO = lerp(C.FIELTRO, C.PIEDRA, 0.15f)
    val ALAZAN = lerp(C.CUERO, C.ROJO, 0.35f)
    val ISABELINO = lerp(C.ESTEPA, C.CUERO, 0.3f)

    /**
     * Un caballo de la estepa, de lado: pequeño, recio, de patas cortas, cabeza grande y crin espesa. Pisa en [pie] con
     * [alto] unidades hasta la cruz; [mira] 1 hacia la derecha, −1 hacia la izquierda. [pasta]: con la cabeza baja.
     * [potro]: de patas largas, cuerpo corto y crin de cepillo. [silla]: con la silla de madera alta y su manta.
     * [crin]: el color de la crin y la cola (por omisión, el del pelo, más oscuro).
     */
    fun DrawScope.caballo(p: Pincel, pie: Offset, alto: Float, mira: Float, pelo: Color, pasta: Boolean = false, potro: Boolean = false, silla: Boolean = false, crin: Color? = null, lejos: Float = 0f, cintas: Boolean = false) {
        val dir = if (mira >= 0f) -1f else 1f                                     // el dibujo mira a la izquierda; con −1 se voltea
        val u = alto / 100f; val piernas = if (potro) 1.32f else 1f; val sube = (piernas - 1f) * 50f
        // Lo que está por encima del codo sube con las patas largas del potro; las patas se estiran.
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + (if (y < -50f) y - sube else y * piernas) * u)
        val delSol = p.solALaDerecha == (dir < 0f)                               // la luz le da por la grupa
        val oscuro = crin ?: lerp(pelo, C.HOLLIN, 0.6f)
        val luz = p.luz(pelo, lejos); val medio = p.por(pelo, 0.1f, lejos); val sombra = p.sombra(lerp(pelo, oscuro, 0.25f), lejos)
        val detalle = alto > 22f
        fun vestir(forma: Path, y0: Float, y1: Float) {
            drawPath(forma, medio)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(0f to luz, 0.38f to luz.copy(alpha = 0f), 0.6f to sombra.copy(alpha = 0f), 1f to sombra, startY = q(0f, y0).y, endY = q(0f, y1).y), Offset(pie.x - 130f * u, q(0f, y0 - 10f).y), Size(260f * u, (y1 - y0 + 20f) * u * 1.5f))
                val lado = if (delSol) listOf(sombra.copy(alpha = 0.5f), Color.Transparent, luz.copy(alpha = 0.25f)) else listOf(luz.copy(alpha = 0.25f), Color.Transparent, sombra.copy(alpha = 0.5f))
                drawRect(Brush.horizontalGradient(if (dir > 0f) lado else lado.reversed(), pie.x - 70f * u, pie.x + 70f * u), Offset(pie.x - 130f * u, q(0f, y0 - 10f).y), Size(260f * u, (y1 - y0 + 20f) * u * 1.5f))
            }
        }
        // Una pata, de arriba abajo, con su ancho en cada punto: el contorno suave, el menudillo peludo y el casco.
        fun pata(puntos: List<Triple<Float, Float, Float>>, lejana: Boolean) {
            val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
            for ((k, t) in puntos.withIndex()) {
                val a = puntos[maxOf(k - 1, 0)]; val b = puntos[minOf(k + 1, puntos.size - 1)]
                val d = q(b.first, b.second) - q(a.first, a.second); val l = d.getDistance().coerceAtLeast(0.01f); val nrm = Offset(-d.y, d.x) / l
                val c = q(t.first, t.second); izq += c + nrm * (t.third * u); der += c - nrm * (t.third * u)
            }
            val forma = io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(izq + der.reversed()).apply { close() }
            drawPath(forma, if (lejana) p.sombra(lerp(pelo, oscuro, 0.35f), lejos) else medio)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.55f to p.sombra(lerp(pelo, oscuro, 0.5f), lejos).copy(alpha = 0.5f), 1f to p.sombra(oscuro, lejos), startY = q(0f, -40f).y, endY = q(0f, 0f).y), Offset(pie.x - 130f * u, q(0f, -70f).y), Size(260f * u, 90f * u * piernas))
                if (!lejana && detalle) { val x = puntos[2]; drawLine(luz.copy(alpha = 0.5f), q(x.first + (if (delSol == (dir > 0f)) -1.6f else 1.6f), x.second - 10f), q(puntos[3].first + (if (delSol == (dir > 0f)) -1f else 1f), puntos[3].second), 1.1f * u, StrokeCap.Round) }
            }
            val casco = puntos.last()
            if (detalle) for (k in -2..2) drawLine(p.medio(lerp(oscuro, C.HOLLIN, 0.3f), lejos).copy(alpha = 0.7f), q(casco.first + k * 1.2f, -9f), q(casco.first + k * 1.6f - 1.4f, -3.5f), 1f * u, StrokeCap.Round)
            drawPath(poligono(q(casco.first - 3.6f, -4.5f), q(casco.first + 3.6f, -4.5f), q(casco.first + 4.8f, 0f), q(casco.first - 4.8f, 0f)), p.medio(lerp(C.HOLLIN, C.TIERRA, 0.2f), lejos))
        }
        // Las patas del otro lado, más oscuras; las de este lado, debajo del cuerpo.
        pata(listOf(Triple(-30f, -54f, 6.5f), Triple(-31f, -38f, 5f), Triple(-32f, -24f, 4.2f), Triple(-33f, -14f, 3f), Triple(-34f, -6f, 3.3f), Triple(-35f, -2f, 3.6f)), true)
        pata(listOf(Triple(34f, -60f, 10f), Triple(40f, -40f, 6f), Triple(45f, -25f, 4.6f), Triple(42f, -14f, 3f), Triple(40f, -6f, 3.3f), Triple(39f, -2f, 3.6f)), true)
        // La cola, larga y espesa, casi hasta el suelo.
        val cola = Path().apply { moveTo(q(54f, -95f)); cubicTo(q(68f, -90f), q(70f, -58f), q(66f, -16f)); cubicTo(q(64f, -9f), q(58f, -9f), q(56f, -15f)); cubicTo(q(58f, -46f), q(56f, -78f), q(48f, -90f)); close() }
        drawPath(cola, p.medio(oscuro, lejos))
        if (detalle) for (k in 0..5) drawLine(p.luz(lerp(oscuro, pelo, 0.45f), lejos).copy(alpha = 0.35f), q(55f + k * 1.4f, -88f + k * 2f), q(58f + k * 1.3f, -20f + k * 1.6f), 0.6f * u)
        pata(listOf(Triple(-40f, -56f, 8f), Triple(-41f, -38f, 5.6f), Triple(-42f, -24f, 4.6f), Triple(-43f, -14f, 3.2f), Triple(-44f, -6f, 3.5f), Triple(-45f, -2f, 3.8f)), false)
        pata(listOf(Triple(42f, -64f, 13f), Triple(48f, -42f, 7f), Triple(53f, -25f, 4.8f), Triple(50f, -14f, 3.2f), Triple(48f, -6f, 3.5f), Triple(47f, -2f, 3.8f)), false)
        // El cuerpo: la cruz, el dorso que se hunde un poco, la grupa redonda, la nalga, la babilla, el vientre y el pecho.
        val cuerpo = Path().apply {
            moveTo(q(-46f, -86f)); cubicTo(q(-40f, -97f), q(-34f, -103f), q(-26f, -102f)); cubicTo(q(-14f, -97f), q(-2f, -92f), q(10f, -93f)); cubicTo(q(24f, -95f), q(34f, -101f), q(44f, -99f))
            cubicTo(q(56f, -96f), q(63f, -86f), q(62f, -72f)); cubicTo(q(61f, -62f), q(57f, -55f), q(51f, -50f)); cubicTo(q(46f, -47f), q(40f, -50f), q(33f, -50f)); cubicTo(q(18f, -42f), q(-12f, -40f), q(-28f, -45f))
            cubicTo(q(-38f, -47f), q(-46f, -50f), q(-51f, -58f)); cubicTo(q(-56f, -66f), q(-54f, -78f), q(-46f, -86f)); close()
        }
        vestir(cuerpo, -103f, -40f)
        clipPath(cuerpo) {
            // La paletilla y el anca: dos bultos con su luz arriba; la sombra del vientre y del costillar.
            mancha(q(-34f, -80f), 16f * u, 18f * u, luz, 0.5f)
            mancha(q(40f, -86f), 18f * u, 15f * u, luz, 0.5f)
            mancha(q(4f, -50f), 34f * u, 8f * u, sombra, 0.45f)
            if (detalle) { mancha(q(-24f, -60f), 6f * u, 12f * u, sombra, 0.3f); mancha(q(32f, -64f), 7f * u, 13f * u, sombra, 0.3f) }
        }
        // El cuello y la cabeza, que bajan al pastar.
        val giroCabeza = if (pasta) -dir * 62f else 0f
        withTransformRot(giroCabeza, q(-30f, -92f)) {
            val cuello = Path().apply { moveTo(q(-22f, -102f)); cubicTo(q(-36f, -111f), q(-50f, -125f), q(-60f, -139f)); lineTo(q(-72f, -128f)); cubicTo(q(-72f, -112f), q(-62f, -96f), q(-52f, -80f)); cubicTo(q(-44f, -82f), q(-32f, -90f), q(-22f, -102f)); close() }
            vestir(cuello, -140f, -80f)
            val cabeza = Path().apply {
                moveTo(q(-60f, -141f)); cubicTo(q(-68f, -145f), q(-78f, -140f), q(-84f, -131f)); cubicTo(q(-92f, -120f), q(-99f, -110f), q(-103f, -102f)); cubicTo(q(-106f, -96f), q(-102f, -90f), q(-95f, -91f))
                cubicTo(q(-88f, -92f), q(-84f, -98f), q(-78f, -100f)); cubicTo(q(-68f, -102f), q(-62f, -114f), q(-63f, -126f)); cubicTo(q(-63f, -132f), q(-60f, -136f), q(-60f, -141f)); close()
            }
            vestir(cabeza, -145f, -90f)
            clipPath(cabeza) {
                mancha(q(-72f, -112f), 7f * u, 7f * u, luz, 0.5f)                          // la quijada, redonda
                mancha(q(-100f, -96f), 7f * u, 5f * u, p.sombra(oscuro, lejos), 0.6f)        // el hocico, más oscuro
            }
            if (alto > 14f) {
                drawCircle(p.medio(C.HOLLIN, lejos), 2f * u, q(-78f, -127f)); drawCircle(Color.White.copy(alpha = 0.4f), 0.7f * u, q(-78.6f, -127.6f))
                drawLine(p.medio(C.HOLLIN, lejos), q(-99f, -102f), q(-101f, -99f), 1.4f * u, StrokeCap.Round)
                drawLine(p.sombra(pelo, lejos).copy(alpha = 0.6f), q(-96f, -92f), q(-88f, -95f), 0.8f * u, StrokeCap.Round)
            }
            // Las orejas.
            for (dx in listOf(4f, 0f)) drawPath(poligono(q(-64f + dx, -139f), q(-61f + dx, -152f), q(-57f + dx, -140f)), if (dx > 0f) sombra else medio)
            // La crin: una masa espesa que cae por este lado del cuello, de borde deshilachado; y el copete.
            val cresta = (0..9).map { val t = it / 9f; q(-62f + 40f * t, -140f + 38f * t) }
            val caida = if (potro) 6f else 14f
            val bajo = cresta.mapIndexed { k, c -> c + Offset((3f + 5f * (1f - k / 9f)) * u * dir, (caida * (0.6f + 0.4f * sin(k * 1.9f).let { it * it })) * u) }
            val masa = Path().apply { moveTo(cresta[0]); for (c in cresta.drop(1)) lineTo(c); for (k in bajo.indices.reversed()) { lineTo(bajo[k]); if (k > 0) lineTo((bajo[k] + bajo[k - 1]) / 2f + Offset(0f, -3f * u)) }; close() }
            drawPath(masa, p.medio(lerp(oscuro, C.HOLLIN, 0.15f), lejos))
            if (detalle) clipPath(masa) { for (k in 0 until 14) { val t = k / 13f; val a = q(-62f + 40f * t, -140f + 38f * t); drawLine(p.luz(lerp(oscuro, pelo, 0.45f), lejos).copy(alpha = 0.4f), a, a + Offset(5f * u * dir, caida * u), 0.7f * u, StrokeCap.Round) } }
            drawPath(Path().apply { moveTo(q(-62f, -141f)); cubicTo(q(-68f, -138f), q(-72f, -132f), q(-74f, -126f)); lineTo(q(-70f, -128f)); close() }, p.medio(oscuro, lejos))
            // El ronzal de crin.
            if (detalle && !potro) { drawLine(p.medio(C.CRIN, lejos), q(-67f, -134f), q(-84f, -103f), 1.2f * u); drawLine(p.medio(C.CRIN, lejos), q(-92f, -108f), q(-76f, -112f), 1.2f * u) }
            if (cintas) for (k in 0 until 3) drawLine(p.medio(C.AZUL, lejos), q(-56f + 9f * k, -132f + 9f * k), q(-50f + 9f * k, -114f + 9f * k), 1.8f * u, StrokeCap.Round)
        }
        // La silla de madera, alta delante y detrás, sobre su manta de fieltro, con el estribo corto.
        if (silla) {
            val manta = Path().apply { moveTo(q(-24f, -101f)); cubicTo(q(-12f, -97f), q(4f, -94f), q(14f, -96f)); lineTo(q(16f, -80f)); cubicTo(q(4f, -78f), q(-14f, -80f), q(-26f, -84f)); close() }
            drawPath(manta, p.por(C.ROJO, 0.1f, lejos)); drawPath(manta, p.luz(C.ORO, lejos).copy(alpha = 0.55f), style = Stroke(1f * u))
            val asiento = Path().apply { moveTo(q(-21f, -101f)); cubicTo(q(-24f, -112f), q(-20f, -117f), q(-16f, -112f)); cubicTo(q(-12f, -103f), q(0f, -100f), q(6f, -104f)); cubicTo(q(8f, -114f), q(13f, -115f), q(13f, -106f)); lineTo(q(11f, -96f)); cubicTo(q(0f, -94f), q(-12f, -96f), q(-21f, -101f)); close() }
            drawPath(asiento, p.por(lerp(C.MADERA, C.ROJO, 0.45f), 0.25f, lejos)); drawPath(asiento, p.luz(C.ORO, lejos).copy(alpha = 0.5f), style = Stroke(0.7f * u))
            drawLine(p.medio(C.CRIN, lejos), q(-4f, -97f), q(-5f, -70f), 0.9f * u); drawPath(poligono(q(-9f, -70f), q(-1f, -70f), q(-2f, -66f), q(-8f, -66f)), p.medio(C.HOLLIN, lejos))
        }
    }

    /** Gira lo que pinta [dibujo] [grados] alrededor de [pivote] (para bajar la cabeza del caballo). */
    private inline fun DrawScope.withTransformRot(grados: Float, pivote: Offset, dibujo: DrawScope.() -> Unit) {
        if (grados == 0f) dibujo() else rotate(grados, pivote) { dibujo() }
    }

    /** La silueta de un caballo de [caballo], para su sombra arrojada. */
    fun siluetaDeCaballo(pie: Offset, alto: Float, mira: Float, pasta: Boolean = false): Path {
        val dir = if (mira >= 0f) -1f else 1f; val u = alto / 100f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        return Path().apply {
            addPath(poligono(listOf(q(-52f, -80f), q(-26f, -100f), q(20f, -94f), q(52f, -92f), q(60f, -64f), q(48f, -48f), q(46f, 0f), q(40f, 0f), q(36f, -46f), q(-30f, -44f), q(-40f, 0f), q(-46f, 0f), q(-52f, -60f))))
            if (pasta) addPath(poligono(listOf(q(-30f, -96f), q(-50f, -60f), q(-70f, -20f), q(-80f, -6f), q(-64f, -6f), q(-40f, -60f)))) else addPath(poligono(q(-26f, -100f), q(-66f, -140f), q(-104f, -100f), q(-96f, -90f), q(-52f, -80f)))
        }
    }

    /** Un caballo lejano: una mancha con su cabeza, sus patas y su sombra corta. */
    fun DrawScope.caballoLejano(p: Pincel, sol: Sol3, pie: Offset, alto: Float, mira: Float, pelo: Color, lejos: Float, pasta: Boolean = false) {
        val u = alto / 10f; val dir = if (mira >= 0f) 1f else -1f
        if (sol.r.fuerza > 0.05f) drawOval(sol.r.color, Offset(pie.x - 6f * u + (sol.sx * 2f * u).coerceAtMost(0f), pie.y - 0.5f * u), Size(12f * u + abs(sol.sx) * 2f * u, 1.2f * u), alpha = sol.r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
        val c = p.por(pelo, 0f, lejos); val o = p.sombra(lerp(pelo, C.HOLLIN, 0.4f), lejos)
        for (x in listOf(-3.6f, 3.4f)) { drawLine(o, pie + Offset(x * u * dir + 0.6f * u * dir, -5f * u), pie + Offset(x * u * dir + 0.6f * u * dir, 0f), 0.9f * u); drawLine(c, pie + Offset(x * u * dir, -5f * u), pie + Offset(x * u * dir, 0f), 0.9f * u) }
        drawOval(Brush.verticalGradient(listOf(p.luz(pelo, lejos), c, o), pie.y - 10f * u, pie.y - 4.5f * u), Offset(pie.x - 5f * u, pie.y - 9.6f * u), Size(10f * u, 5f * u))
        val cabeza = if (pasta) pie + Offset(7.5f * u * dir, -1.5f * u) else pie + Offset(7f * u * dir, -12f * u)
        drawLine(c, pie + Offset(4f * u * dir, -8f * u), cabeza, 2.2f * u, StrokeCap.Round)
        drawLine(p.medio(lerp(pelo, C.HOLLIN, 0.5f), lejos), pie + Offset(-4.8f * u * dir, -8.6f * u), pie + Offset(-5.6f * u * dir, -3f * u), 1.1f * u, StrokeCap.Round)
    }

    // =================================================================
    //  El ovoo, el odre, el hogar y el argal
    // =================================================================

    /**
     * El ovoo: «un montón de piedras con palos y cintas azules» en lo alto del cerro. Pisa en [pie] y mide [alto] hasta
     * la punta de los palos. [cintas]: cuántas hay atadas (crecen cada año); [nueva]: el día del ovoo, las de ese día,
     * vivas. [viento] las tumba hacia un lado. [nieve] se queda en las piedras.
     */
    fun DrawScope.ovoo(p: Pincel, pie: Offset, alto: Float, cintas: Int, az: Azar, lejos: Float, nieve: Float = 0f, viento: Float = 0.3f, nueva: Boolean = false) {
        val u = alto / 10f
        val monton = alto * 0.48f; val ancho = alto * 0.62f
        // Los palos, con sus ramas secas, que salen del montón.
        val palos = listOf(Offset(0f, -10f) to 1.8f, Offset(-1.6f, -8.4f) to 1.2f, Offset(1.8f, -8.8f) to 1.3f, Offset(-0.6f, -9.2f) to 1f)
        for ((tip, g) in palos) {
            val a = pie + Offset(tip.x * u * 0.3f, -monton * 0.7f); val b = pie + Offset(tip.x * u, tip.y * u)
            drawLine(p.medio(C.MADERA_OSC, lejos), a, b, (g * 0.25f * u).coerceAtLeast(0.4f), StrokeCap.Round)
            drawLine(p.luz(lerp(C.MADERA, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.6f), a + Offset(if (p.solALaDerecha) 0.2f * u else -0.2f * u, 0f), b, (g * 0.08f * u).coerceAtLeast(0.3f), StrokeCap.Round)
            repeat(3) { val t = az.entre(0.4f, 0.9f); val c = a + (b - a) * t; drawLine(p.medio(C.MADERA_OSC, lejos), c, c + Offset(az.entre(-1.4f, 1.4f) * u, -az.entre(0.6f, 1.4f) * u), (0.12f * u).coerceAtLeast(0.3f), StrokeCap.Round) }
        }
        // Las cintas: azules (las del cielo), alguna blanca y alguna desteñida, que el viento tiende.
        repeat(cintas) {
            val (tip, _) = palos[it % palos.size]; val t = az.entre(0.45f, 0.95f)
            val a = pie + Offset(tip.x * u * 0.3f, -monton * 0.7f) + (Offset(tip.x * u, tip.y * u) - Offset(tip.x * u * 0.3f, -monton * 0.7f)) * t
            val largo = az.entre(1.6f, 3.4f) * u; val vence = (0.4f + viento) * largo
            val color = when { nueva && it >= cintas - 3 -> C.AZUL; it % 5 == 0 -> C.FIELTRO; it % 3 == 0 -> lerp(C.AZUL, C.FIELTRO, 0.5f); else -> lerp(C.AZUL, C.PIEDRA, az.entre(0f, 0.3f)) }
            val cinta = Path().apply { moveTo(a); cubicTo(a + Offset(vence * 0.3f, largo * 0.3f), a + Offset(vence * 0.6f, largo * 0.4f), a + Offset(vence, largo * 0.8f)); lineTo(a + Offset(vence + 0.3f * u, largo * 0.78f)); cubicTo(a + Offset(vence * 0.6f + 0.4f * u, largo * 0.3f), a + Offset(vence * 0.3f + 0.4f * u, largo * 0.2f), a + Offset(0.4f * u, 0f)); close() }
            drawPath(cinta, p.por(color, if (it % 2 == 0) 0.4f else -0.2f, lejos))
        }
        // El montón de piedras: hiladas de abajo arriba, cada piedra con su lado de luz.
        val forma = Path().apply { moveTo(pie + Offset(-ancho / 2f, 0f)); cubicTo(pie + Offset(-ancho * 0.42f, -monton * 0.5f), pie + Offset(-ancho * 0.2f, -monton), pie + Offset(0f, -monton)); cubicTo(pie + Offset(ancho * 0.22f, -monton), pie + Offset(ancho * 0.44f, -monton * 0.5f), pie + Offset(ancho / 2f, 0f)); close() }
        drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.PIEDRA, lejos), p.por(C.PIEDRA, 0f, lejos), p.luz(C.PIEDRA, lejos)) else listOf(p.luz(C.PIEDRA, lejos), p.por(C.PIEDRA, 0f, lejos), p.sombra(C.PIEDRA, lejos)), pie.x - ancho / 2f, pie.x + ancho / 2f))
        clipPath(forma) {
            val filas = 6
            for (f in 0 until filas) {
                val y = pie.y - monton * (f + 0.5f) / filas; val w = ancho * (1f - f / filas.toFloat() * 0.8f); val r = (ancho / 9f) * (1f - 0.08f * f)
                var x = pie.x - w / 2f
                while (x < pie.x + w / 2f) {
                    val rr = r * az.entre(0.7f, 1.1f); val col = lerp(C.PIEDRA, if (az.entero(3) == 0) C.TIERRA else C.FIELTRO, az.entre(0f, 0.35f))
                    val ladoSol = (x - pie.x) / (ancho / 2f) * (if (p.solALaDerecha) 1f else -1f)
                    drawOval(p.por(col, -0.4f + 0.4f * ladoSol, lejos), Offset(x - rr, y - rr * 0.6f), Size(rr * 2f, rr * 1.25f))
                    drawOval(p.por(col, 0.3f + 0.5f * ladoSol, lejos), Offset(x - rr * 0.8f + (if (p.solALaDerecha) rr * 0.3f else -rr * 0.3f), y - rr * 0.62f), Size(rr * 1.3f, rr * 0.7f))
                    if (nieve > 0.1f) drawOval(p.por(C.NIEVE, 0.5f + 0.3f * ladoSol, lejos).copy(alpha = nieve), Offset(x - rr * 0.8f, y - rr * 0.66f), Size(rr * 1.5f, rr * 0.42f))
                    x += rr * az.entre(1.4f, 1.9f)
                }
            }
        }
        // Una ofrenda al pie: una calavera de caballo, blanca **[Aproximación]**.
        if (alto > 30f) drawOval(p.por(C.FIELTRO, 0.5f, lejos), Offset(pie.x + ancho * 0.3f, pie.y - 1.2f * u), Size(1.4f * u, 0.9f * u))
    }

    /** La silueta del ovoo, para su sombra. */
    fun siluetaDeOvoo(pie: Offset, alto: Float): Path = poligono(pie + Offset(-alto * 0.31f, 0f), pie + Offset(-alto * 0.1f, -alto * 0.48f), pie + Offset(0f, -alto), pie + Offset(alto * 0.1f, -alto * 0.48f), pie + Offset(alto * 0.31f, 0f))

    /**
     * El odre grande de cuero para el airag, colgado de su armazón: dos horcones y un travesaño. La bolsa, hinchada y
     * oscura de grasa, con su costura; el palo de batir que sale por la boca; los cubos de cuero y de madera al pie.
     * [alto]: hasta el travesaño. [batiendo], de 0 a 1: el palo, más o menos hundido.
     */
    fun DrawScope.odre(p: Pincel, pie: Offset, alto: Float, az: Azar, lejos: Float = 0f, batiendo: Float = 0.5f) {
        val u = alto / 100f; fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        val madera = lerp(C.MADERA, C.TIERRA, 0.25f)
        val ladoLuz = if (p.solALaDerecha) 1f else -1f
        fun horcon(x: Float, inclina: Float) {
            val a = q(x, 0f); val b = q(x + inclina, -100f)
            drawLine(p.sombra(madera, lejos), a, b, 4.2f * u, StrokeCap.Round); drawLine(p.luz(madera, lejos), a + Offset(ladoLuz * 0.9f * u, 0f), b + Offset(ladoLuz * 0.9f * u, 0f), 1.6f * u, StrokeCap.Round)
            drawLine(p.medio(madera, lejos), b, b + Offset(-5f * u, -9f * u), 2.6f * u, StrokeCap.Round); drawLine(p.medio(madera, lejos), b, b + Offset(5f * u, -8f * u), 2.6f * u, StrokeCap.Round)
        }
        horcon(-38f, 3f); horcon(38f, -3f)
        val viga = listOf(q(-46f, -102f), q(46f, -101f))
        drawLine(p.sombra(madera, lejos), viga[0], viga[1], 4.4f * u, StrokeCap.Round); drawLine(p.luz(madera, lejos), viga[0] + Offset(0f, -1.2f * u), viga[1] + Offset(0f, -1.2f * u), 1.6f * u, StrokeCap.Round)
        // Las correas de las que cuelga.
        for (x in listOf(-14f, 12f)) drawLine(p.medio(C.CRIN, lejos), q(x * 1.2f, -101f), q(x, -78f), 1.4f * u)
        // La bolsa: un vientre de cuero que se hunde por abajo.
        val bolsa = Path().apply { moveTo(q(-16f, -80f)); cubicTo(q(-34f, -74f), q(-38f, -40f), q(-30f, -22f)); cubicTo(q(-22f, -8f), q(20f, -8f), q(28f, -22f)); cubicTo(q(36f, -40f), q(32f, -74f), q(14f, -80f)); cubicTo(q(6f, -86f), q(-8f, -86f), q(-16f, -80f)); close() }
        drawPath(bolsa, p.por(C.CUERO, -0.1f, lejos))
        clipPath(bolsa) {
            drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CUERO, lejos), Color.Transparent, p.luz(lerp(C.CUERO, C.ORO, 0.3f), lejos).copy(alpha = 0.7f)) else listOf(p.luz(lerp(C.CUERO, C.ORO, 0.3f), lejos).copy(alpha = 0.7f), Color.Transparent, p.sombra(C.CUERO, lejos)), q(-36f, 0f).x, q(36f, 0f).x), q(-40f, -90f), Size(80f * u, 84f * u))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f)), q(0f, -40f).y, q(0f, -8f).y), q(-40f, -40f), Size(80f * u, 34f * u))
            // El brillo de la grasa y las manchas de leche que rezuma.
            drawOval(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.32f), Color.Transparent), q(ladoLuz * 14f, -56f), 12f * u), q(ladoLuz * 14f - 8f, -66f), Size(16f * u, 20f * u))
            repeat(5) { val c = q(az.entre(-24f, 24f), az.entre(-60f, -20f)); drawOval(p.medio(lerp(C.CUERO, C.FIELTRO, 0.5f), lejos).copy(alpha = 0.3f), Offset(c.x - 2f * u, c.y - 1f * u), Size(4f * u, 6f * u)) }
            drawPath(Path().apply { moveTo(q(-2f, -84f)); cubicTo(q(4f, -60f), q(0f, -30f), q(-4f, -10f)) }, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.6f), style = Stroke(1.2f * u))
            for (j in 0..12) { val c = q(-2f + 4f * sin(j * 0.6f) * 0.3f, -82f + j * 6f); drawLine(p.luz(C.FIELTRO, lejos).copy(alpha = 0.45f), c + Offset(-1.5f * u, 0f), c + Offset(1.5f * u, 0f), 0.6f * u) }
        }
        // La boca atada y el palo de batir.
        drawOval(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f), lejos), q(-8f, -88f), Size(16f * u, 8f * u))
        val hundido = 10f * batiendo
        drawLine(p.sombra(madera, lejos), q(2f, -84f + hundido), q(14f, -150f + hundido), 2.6f * u, StrokeCap.Round); drawLine(p.luz(lerp(madera, C.FIELTRO, 0.3f), lejos), q(2.6f, -84f + hundido), q(14.6f, -150f + hundido), 1f * u, StrokeCap.Round)
        // Los cubos al pie: uno de cuero cosido y uno de madera.
        val cubo = Path().apply { moveTo(q(-58f, -16f)); lineTo(q(-40f, -16f)); lineTo(q(-42f, 0f)); lineTo(q(-56f, 0f)); close() }
        drawPath(cubo, p.por(C.CUERO, ladoLuz * 0.2f, lejos)); drawOval(p.medio(C.HOLLIN, lejos), q(-58f, -18f), Size(18f * u, 4f * u)); drawOval(p.luz(C.FIELTRO, lejos).copy(alpha = 0.8f), q(-56f, -17.5f), Size(14f * u, 2.6f * u))
        val cubo2 = Path().apply { moveTo(q(42f, -20f)); lineTo(q(60f, -20f)); lineTo(q(58f, 0f)); lineTo(q(44f, 0f)); close() }
        drawPath(cubo2, Brush.horizontalGradient(listOf(p.por(madera, -ladoLuz * 0.6f, lejos), p.por(madera, ladoLuz * 0.6f, lejos)), q(42f, 0f).x, q(60f, 0f).x))
        for (y in listOf(-16f, -5f)) drawLine(p.medio(C.HOLLIN, lejos), q(43f, y), q(59f, y), 1.1f * u)
    }

    /** La silueta del odre con su armazón, para su sombra. */
    fun siluetaDeOdre(pie: Offset, alto: Float): Path { val u = alto / 100f; return Path().apply { addRect(Rect(pie.x - 40f * u, pie.y - 104f * u, pie.x + 40f * u, pie.y - 98f * u)); addRect(Rect(pie.x - 40f * u, pie.y - 100f * u, pie.x - 36f * u, pie.y)); addRect(Rect(pie.x + 36f * u, pie.y - 100f * u, pie.x + 40f * u, pie.y)); addOval(Rect(pie.x - 34f * u, pie.y - 84f * u, pie.x + 34f * u, pie.y - 10f * u)) } }

    /**
     * El hogar de fuera **[Diseño propio]**: un corro de piedras ennegrecidas, la ceniza, la trébede de hierro y la olla
     * grande de cobre. [fuego], de 0 (ceniza fría) a 1: las brasas se encienden (las llamas van en la parte viva).
     */
    fun DrawScope.hogar(p: Pincel, pie: Offset, ancho: Float, fuego: Float, az: Azar, lejos: Float = 0f, olla: Boolean = true, nieve: Float = 0f) {
        val u = ancho / 100f; fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        val noche = p.l.noche
        // La ceniza y las brasas.
        drawOval(p.por(lerp(C.PIEDRA, C.HOLLIN, 0.5f), 0.2f, lejos), q(-40f, -10f), Size(80f * u, 18f * u))
        drawOval(lerp(p.medio(C.HOLLIN, lejos), lerp(C.LUMBRE, C.ROJO, 0.5f), (0.25f + 0.6f * noche) * fuego), q(-24f, -6f), Size(48f * u, 10f * u))
        // Las piedras del corro, primero las de atrás.
        for (atras in listOf(true, false)) for (j in 0 until 9) {
            val a = (2 * PI * j / 9).toFloat() + 0.2f; val delante = sin(a) > 0f
            if (delante == atras) continue
            val c = q(cos(a) * 44f, sin(a) * 10f - 2f); val w = az.entre(8f, 12f) * u; val h = az.entre(6f, 9f) * u
            val col = lerp(C.PIEDRA, C.HOLLIN, az.entre(0.1f, 0.4f))
            val lado = cos(a) * (if (p.solALaDerecha) 1f else -1f)
            drawOval(p.por(col, -0.3f + 0.5f * lado, lejos), Offset(c.x - w, c.y - h), Size(w * 2f, h * 1.5f))
            drawOval(p.por(col, 0.4f + 0.4f * lado, lejos), Offset(c.x - w * 0.7f, c.y - h * 1.05f), Size(w * 1.3f, h * 0.7f))
            if (nieve > 0.1f && !atras) drawOval(p.por(C.NIEVE, 0.6f, lejos).copy(alpha = nieve * 0.8f), Offset(c.x - w * 0.6f, c.y - h * 1.1f), Size(w * 1.2f, h * 0.45f))
            if (fuego > 0.1f) drawOval(C.LUMBRE.copy(alpha = (0.35f * fuego * (0.3f + 0.7f * noche)).coerceIn(0f, 1f)), Offset(c.x - w * 0.6f, c.y - h * 0.9f), Size(w * 1.2f, h * 0.9f), blendMode = BlendMode.Plus)
        }
        if (!olla) return
        // La trébede y la olla.
        val hierro = lerp(C.HOLLIN, C.PIEDRA, 0.25f)
        for (x in listOf(-26f, 26f, 4f)) drawLine(p.medio(hierro, lejos), q(x, -2f), q(x * 0.7f, -30f), 2.2f * u, StrokeCap.Round)
        drawOval(p.medio(hierro, lejos), q(-30f, -34f), Size(60f * u, 8f * u), style = Stroke(2.2f * u))
        val cuerpo = Path().apply { moveTo(q(-34f, -66f)); cubicTo(q(-38f, -40f), q(-26f, -26f), q(0f, -25f)); cubicTo(q(26f, -26f), q(38f, -40f), q(34f, -66f)); close() }
        val cobre = lerp(C.CUERO, C.ORO, 0.35f)
        drawPath(cuerpo, p.por(cobre, 0f, lejos))
        clipPath(cuerpo) {
            drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(cobre, lejos), Color.Transparent, p.luz(lerp(cobre, C.ORO, 0.5f), lejos)) else listOf(p.luz(lerp(cobre, C.ORO, 0.5f), lejos), Color.Transparent, p.sombra(cobre, lejos)), q(-36f, 0f).x, q(36f, 0f).x), q(-40f, -70f), Size(80f * u, 46f * u))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.medio(C.HOLLIN, lejos).copy(alpha = 0.85f)), q(0f, -48f).y, q(0f, -25f).y), q(-40f, -48f), Size(80f * u, 24f * u))
            if (fuego > 0.1f) drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.LUMBRE.copy(alpha = (0.5f * fuego * noche).coerceIn(0f, 1f))), q(0f, -40f).y, q(0f, -25f).y), q(-40f, -40f), Size(80f * u, 16f * u), blendMode = BlendMode.Plus)
        }
        drawOval(p.por(lerp(cobre, C.ORO, 0.4f), 0.6f, lejos), q(-36f, -70f), Size(72f * u, 10f * u))
        drawOval(p.medio(lerp(C.HOLLIN, cobre, 0.3f), lejos), q(-31f, -68.5f), Size(62f * u, 7f * u))
        for (x in listOf(-38f, 38f)) drawOval(p.medio(hierro, lejos), q(x - 4f, -64f), Size(8f * u, 6f * u), style = Stroke(1.4f * u))
    }

    /**
     * El montón de argal, la boñiga seca que se quema: tortas apiladas en un cono, con el cesto de llevar a la espalda y
     * el rastrillo de madera apoyados. Pisa en [pie] y mide [ancho] de lado.
     */
    fun DrawScope.argal(p: Pincel, pie: Offset, ancho: Float, az: Azar, lejos: Float = 0f, nieve: Float = 0f, cesto: Boolean = true) {
        val u = ancho / 100f; fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        val tierra = lerp(C.ESTIERCOL, C.ESTEPA, 0.35f)
        val forma = Path().apply { moveTo(q(-50f, 0f)); cubicTo(q(-46f, -20f), q(-26f, -46f), q(-4f, -50f)); cubicTo(q(18f, -48f), q(40f, -24f), q(48f, 0f)); close() }
        drawPath(forma, p.por(tierra, -0.2f, lejos))
        clipPath(forma) {
            for (f in 0 until 9) {
                val y = -4f - f * 5.4f; val w = 48f * (1f - f / 10f)
                var x = -w
                while (x < w) {
                    val r = az.entre(7f, 11f) * (1f - f * 0.05f); val c = q(x, y); val lado = x / 50f * (if (p.solALaDerecha) 1f else -1f)
                    val col = lerp(tierra, if (az.entero(3) == 0) C.ESTEPA else C.ESTIERCOL, az.entre(0f, 0.4f))
                    drawOval(p.por(col, -0.3f + 0.6f * lado, lejos), Offset(c.x - r * u, c.y - r * 0.32f * u), Size(r * 2f * u, r * 0.64f * u))
                    drawArcLine(p.por(col, 0.4f + 0.5f * lado, lejos), c, r * u)
                    x += r * az.entre(1.4f, 1.9f)
                }
            }
            if (nieve > 0.1f) drawRect(Brush.verticalGradient(listOf(p.por(C.NIEVE, 0.5f, lejos).copy(alpha = nieve), Color.Transparent), q(0f, -50f).y, q(0f, -26f).y), q(-50f, -52f), Size(100f * u, 30f * u))
        }
        if (!cesto) return
        // El rastrillo, apoyado.
        val madera = lerp(C.MADERA, C.PIEDRA, 0.2f)
        drawLine(p.medio(madera, lejos), q(42f, 2f), q(-6f, -96f), 2.2f * u, StrokeCap.Round); drawLine(p.luz(madera, lejos), q(42.6f, 1f), q(-5.4f, -96f), 0.8f * u, StrokeCap.Round)
        drawLine(p.medio(madera, lejos), q(-14f, -92f), q(2f, -100f), 3f * u, StrokeCap.Round)
        for (j in 0..4) { val c = q(-14f + 4f * j, -92f - 2f * j); drawLine(p.medio(madera, lejos), c, c + Offset(-2f * u, 7f * u), 1.3f * u, StrokeCap.Round) }
        // El cesto de mimbre, ancho de boca, con sus correas.
        val cesto2 = Path().apply { moveTo(q(48f, -62f)); lineTo(q(90f, -58f)); lineTo(q(80f, 0f)); lineTo(q(58f, -2f)); close() }
        drawPath(cesto2, Brush.horizontalGradient(listOf(p.por(C.ESTEPA, if (p.solALaDerecha) -0.6f else 0.6f, lejos), p.por(C.ESTEPA, if (p.solALaDerecha) 0.6f else -0.6f, lejos)), q(48f, 0f).x, q(90f, 0f).x))
        clipPath(cesto2) {
            for (j in 0..14) { val y = -62f + j * 4.4f; drawLine(p.sombra(C.ESTEPA, lejos).copy(alpha = 0.55f), q(40f, y), q(96f, y + 4f), 0.8f * u) }
            for (j in 0..8) { val x = 50f + j * 5f; drawLine(p.medio(lerp(C.ESTEPA, C.MADERA, 0.4f), lejos), q(x, -64f), q(x * 0.85f + 9f, 2f), 1.1f * u) }
        }
        drawOval(p.sombra(C.ESTIERCOL, lejos), q(48f, -66f), Size(42f * u, 8f * u))
        drawOval(p.por(C.ESTEPA, 0.5f, lejos), q(48f, -66f), Size(42f * u, 8f * u), style = Stroke(1.6f * u))
        drawLine(p.medio(C.CUERO, lejos), q(56f, -62f), q(60f, -20f), 1.8f * u); drawLine(p.medio(C.CUERO, lejos), q(80f, -60f), q(76f, -18f), 1.8f * u)
    }

    private fun DrawScope.drawArcLine(color: Color, c: Offset, r: Float) {
        drawLine(color.copy(alpha = 0.7f), c + Offset(-r * 0.7f, -r * 0.2f), c + Offset(r * 0.6f, -r * 0.24f), (r * 0.12f).coerceAtLeast(0.3f), StrokeCap.Round)
    }
}
