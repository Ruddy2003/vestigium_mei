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

/**
 * La unión de varias formas en una sola. `addPath` no sirve para eso: donde dos formas de distinto sentido se tapan,
 * la suma de vueltas da cero y queda un agujero (en una sombra, en un recorte).
 */
internal fun union(formas: List<Path>): Path {
    var acc = Path()
    for (f in formas) { val r = Path(); r.op(acc, f, androidx.compose.ui.graphics.PathOperation.Union); acc = r }
    return acc
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
                    if (s.escala(0f, 0f) > 20f) {
                        // De cerca el fieltro no es liso: nubes de humo y de lluvia, más claras y más sucias, y el pelo
                        // del batanado, en trazos cortos que se cruzan.
                        val e2 = s.escala(0f, 0f); val tz = Azar(Semilla.de(g.semilla, "fieltro", "pared"))
                        repeat(24) { val th = tz.entre(a, b); val y = tz.entre(0.1f, g.pared - 0.1f); mancha(g.pared(th, y), tz.entre(0.25f, 0.75f) * e2, tz.entre(0.06f, 0.18f) * e2, if (it % 2 == 0) p.luz(lerp(fieltro, Color.White, 0.4f), lejos) else p.sombra(lerp(fieltro, C.TIERRA, 0.45f), lejos), 0.12f + 0.06f * g.desgaste) }
                        repeat((90 * (e2 / 40f)).toInt().coerceIn(40, 220)) { val th = tz.entre(a, b); val y = tz.entre(0.08f, g.pared - 0.08f); val c = g.pared(th, y); val d = Offset(tz.entre(-1f, 1f), tz.entre(-0.5f, 0.5f)) * (0.035f * e2)
                            drawLine((if (it % 2 == 0) p.luz(lerp(fieltro, Color.White, 0.35f), lejos) else p.sombra(fieltro, lejos)).copy(alpha = 0.22f), c - d, c + d, (0.006f * e2).coerceIn(0.3f, 0.8f), StrokeCap.Round) }
                        // Las chorreras de la lluvia que baja del alero, más largas y más sucias junto a la puerta.
                        repeat(8) { val th = tz.entre(a, b); val l = tz.entre(0.25f, 0.6f) * g.pared; val c0 = g.pared(th, g.pared - 0.2f); val c1 = g.pared(th + tz.entre(-0.01f, 0.01f), g.pared - 0.2f - l)
                            drawLine(p.sombra(lerp(fieltro, C.HOLLIN, 0.3f), lejos).copy(alpha = 0.16f), c0, c1, (0.05f * e2).coerceIn(0.5f, 3f), StrokeCap.Round) }
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
                val grueso = (0.05f * s.escala(0f, 0f)).coerceIn(0.4f, 3.2f)
                val camino = Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }
                // La sombra que la cuerda echa en el fieltro, debajo.
                if (grueso > 1f) drawPath(Path().apply { addPath(camino, Offset(0f, grueso * 0.6f)) }, p.sombra(lerp(fieltro, C.HOLLIN, 0.3f), lejos).copy(alpha = 0.35f), style = Stroke(grueso * 0.9f, cap = StrokeCap.Round))
                drawPath(camino, p.sombra(C.CRIN, lejos), style = Stroke(grueso, cap = StrokeCap.Round))
                if (grueso > 0.8f) drawPath(camino, p.luz(lerp(C.CRIN, C.FIELTRO, 0.4f), lejos).copy(alpha = 0.55f), style = Stroke(grueso * 0.35f, cap = StrokeCap.Round))
                // Retorcida: las vueltas de los cabos, en trazos cortos inclinados.
                if (grueso > 1.4f) drawPath(camino, p.luz(lerp(C.CRIN, C.FIELTRO, 0.55f), lejos).copy(alpha = 0.6f), style = Stroke(grueso * 0.8f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(grueso * 0.35f, grueso * 0.9f))))
                // Los nudos donde se empalma o se ata a la celosía.
                if (grueso > 1f && g.detalle) for (k in listOf(0.18f, 0.55f, 0.86f)) { val th = a + (b - a) * k; if (abs(th) < g.mediaPuerta * 2.2f) continue; val c = g.pared(th, y2, g.radio + 0.03f); drawCircle(p.sombra(C.CRIN, lejos), grueso * 0.95f, c); drawCircle(p.luz(lerp(C.CRIN, C.FIELTRO, 0.45f), lejos).copy(alpha = 0.6f), grueso * 0.4f, c + Offset(-grueso * 0.25f, -grueso * 0.3f)); drawLine(p.sombra(C.CRIN, lejos), c, c + Offset(grueso * 0.6f, grueso * 2.8f), grueso * 0.5f, StrokeCap.Round) }
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
        if (g.detalle && s.escala(0f, 0f) > 20f) clipPath(base) {
            val e2 = s.escala(0f, 0f); val tz = Azar(Semilla.de(g.semilla, "fieltro", "techo"))
            val (va, vb) = g.visible()
            // Nubes de humo y de sol en el fieltro.
            repeat(22) { val t = tz.entre(va, vb); val k = tz.entre(0.05f, 0.8f); mancha(arriba(t, k), tz.entre(0.3f, 0.9f) * e2, tz.entre(0.08f, 0.2f) * e2, if (it % 2 == 0) p.luz(lerp(color, Color.White, 0.4f), lejos) else p.sombra(lerp(color, C.TIERRA, 0.4f), lejos), 0.12f + 0.05f * g.desgaste) }
            // Los paños: dos o tres grandes piezas de fieltro, cuyo canto se monta sobre el de al lado, cosido.
            for (j in 0 until 3) {
                val t = va + (vb - va) * (0.22f + 0.28f * j) + tz.entre(-0.1f, 0.1f)
                val canto = (0..10).map { k -> val kk = k / 10f; arriba(t + 0.18f * kk - 0.05f * kk * kk, 0.92f - 0.92f * kk) }
                val linea = Path().apply { moveTo(canto[0]); for (c in canto.drop(1)) lineTo(c) }
                drawPath(Path().apply { addPath(linea, Offset(0.012f * e2, 0.01f * e2)) }, p.sombra(color, lejos).copy(alpha = 0.45f), style = Stroke(0.03f * e2))
                drawPath(linea, p.luz(lerp(color, Color.White, 0.35f), lejos).copy(alpha = 0.55f), style = Stroke(0.012f * e2))
                drawPoints(canto.zipWithNext().flatMap { (c0, c1) -> (0 until 4).map { c0 + (c1 - c0) * (it / 4f) + Offset(-0.02f * e2, 0f) } }, PointMode.Points, p.sombra(C.CRIN, lejos).copy(alpha = 0.5f), (0.008f * e2).coerceIn(0.3f, 1f), StrokeCap.Round)
            }
            // Las chorreras de la lluvia y del hollín, que bajan del aro.
            repeat(16) { val t = tz.entre(va, vb); val k0 = tz.entre(0.6f, 0.92f); val k1 = k0 - tz.entre(0.15f, 0.45f); val a0 = arriba(t, k0); val a1 = arriba(t + tz.entre(-0.02f, 0.02f), k1)
                drawLine(p.sombra(lerp(color, C.HOLLIN, 0.4f), lejos).copy(alpha = 0.1f), a0, a1, (0.04f * e2).coerceIn(0.5f, 2.4f), StrokeCap.Round); drawLine(p.sombra(lerp(color, C.HOLLIN, 0.4f), lejos).copy(alpha = 0.08f), a0, a0 + (a1 - a0) * 0.5f, (0.07f * e2).coerceIn(0.6f, 3.6f), StrokeCap.Round) }
            // Uno o dos remiendos, con su puntada.
            repeat((1 + 2 * g.desgaste).toInt()) {
                val t = tz.entre(va + 0.2f, vb - 0.2f); val k = tz.entre(0.2f, 0.6f); val c = arriba(t, k)
                val pts = (0 until 7).map { j -> val ang = (2 * PI * j / 7).toFloat(); c + Offset(cos(ang) * tz.entre(0.14f, 0.22f) * e2, sin(ang) * tz.entre(0.06f, 0.1f) * e2) }
                drawPath(poligono(pts), p.por(lerp(color, if (it % 2 == 0) Color.White else C.TIERRA, 0.25f), 0.2f, lejos).copy(alpha = 0.9f))
                drawPoints(pts.zipWithNext().flatMap { (c0, c1) -> (0 until 3).map { c0 + (c1 - c0) * (it / 3f) } }, PointMode.Points, p.sombra(C.CRIN, lejos).copy(alpha = 0.55f), (0.008f * e2).coerceIn(0.3f, 1f), StrokeCap.Round)
            }
            // El pelo del fieltro.
            repeat(160) { val t = tz.entre(va, vb); val c = arriba(t, tz.entre(0f, 0.85f)); val d = Offset(tz.entre(-1f, 1f), tz.entre(-0.4f, 0.4f)) * (0.035f * e2); drawLine((if (it % 2 == 0) p.luz(lerp(color, Color.White, 0.35f), lejos) else p.sombra(color, lejos)).copy(alpha = 0.2f), c - d, c + d, (0.006f * e2).coerceIn(0.3f, 0.8f), StrokeCap.Round) }
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
                val cuerda = Path().apply { moveTo(q0); cubicTo(q0 + (medio - q0) * 0.6f, medio + (q1 - medio) * 0.3f, q1) }
                val gc = (0.045f * s.escala(0f, 0f)).coerceIn(0.35f, 2.6f)
                if (gc > 1f) drawPath(Path().apply { addPath(cuerda, Offset(gc * 0.5f, gc * 0.6f)) }, p.sombra(color, lejos).copy(alpha = 0.5f), style = Stroke(gc, cap = StrokeCap.Round))
                drawPath(cuerda, p.sombra(C.CRIN, lejos).copy(alpha = 0.9f), style = Stroke(gc, cap = StrokeCap.Round))
                if (gc > 1.2f) drawPath(cuerda, p.luz(lerp(C.CRIN, C.FIELTRO, 0.5f), lejos).copy(alpha = 0.55f), style = Stroke(gc * 0.7f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(gc * 0.3f, gc * 0.85f))))
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
            if (c.cuna) {
                // La cuna de madera de Saran, atada encima del cofre: la caja pintada de naranja, más baja que el cofre y
                // gastada, con su greca dorada; el arco de madera de la cabecera, del que cuelga la manta; la manta de
                // fieltro blanco, abultada, con el ribete azul; y las dos cintas de crin que la atan **[Diseño propio]**.
                val x0 = -0.52f; val x1 = -0.04f; val y0 = t0 + 0.55f; val y1 = y0 + 0.2f; val z0 = 0.04f; val z1 = 0.86f
                val pintada = lerp(lerp(C.ROJO, C.ORO, 0.3f), C.MADERA, 0.3f)
                caja(s, p, sol, x0, x1, y0, y1, z0, z1, pintada, lejos) { i, forma -> if (i != 4) { drawPath(forma, p.luz(C.ORO, lejos).copy(alpha = 0.55f), style = Stroke((0.03f * s.escala(0f, 0.4f)).coerceIn(0.3f, 1.4f))); drawPath(forma, p.sombra(C.MADERA_OSC, lejos).copy(alpha = 0.25f)) } }
                // La manta, abultada sobre la caja, que asoma por los cantos.
                val manta = lerp(C.FIELTRO, C.LANA, 0.35f)
                val cx = (x0 + x1) / 2f; val cz = (z0 + 0.12f + z1) / 2f
                val borde = (0..15).map { k -> val a = (2 * PI * k / 16).toFloat()
                    V3(cx + cos(a) * (x1 - x0) * 0.56f, y1 + 0.03f + 0.04f * (0.5f + 0.5f * cos(a * 2f)), cz + sin(a) * (z1 - z0 - 0.12f) * 0.56f) }
                val forma = poligono(borde.map { s.p(it) })
                drawPath(forma, p.por(manta, if (p.solALaDerecha) 0.2f else -0.1f, lejos))
                clipPath(forma) {
                    // El bulto de la manta, con su luz arriba, y los pliegues que la cruzan.
                    val lomo = s.p(cx, y1 + 0.14f, cz); drawCircle(p.luz(manta, lejos).copy(alpha = 0.7f), 0.14f * s.escala(cx, cz), lomo)
                    for (k in -1..1) drawLine(p.sombra(manta, lejos).copy(alpha = 0.7f), s.p(x0, y1 + 0.06f, cz + k * 0.2f), s.p(x1, y1 + 0.08f, cz + k * 0.2f + 0.05f), (0.02f * s.escala(cx, cz)).coerceIn(0.3f, 1f))
                }
                drawPath(forma, p.medio(C.AZUL, lejos), style = Stroke((0.03f * s.escala(0f, 0.4f)).coerceIn(0.3f, 1.4f)))
                // El arco de la cabecera, de madera curvada.
                val arco = (0..10).map { k -> val a = (PI * k / 10).toFloat(); V3((x0 + x1) / 2f - cos(a) * (x1 - x0) * 0.5f, y1 + sin(a) * 0.26f, z0 + 0.06f) }
                for (k in 0 until arco.size - 1) palo(s, p, arco[k], arco[k + 1], 0.035f, lerp(C.MADERA, C.ROJO, 0.25f), lejos)
                // Las dos cintas de crin que atan la cuna a la carga.
                for (z in listOf(z0 + 0.3f, z1 - 0.18f)) {
                    val q = listOf(V3(x0 - 0.01f, y0 + 0.05f, z), V3(x0 + 0.04f, y1 + 0.08f, z), V3(x1 - 0.04f, y1 + 0.08f, z), V3(x1 + 0.01f, y0 + 0.05f, z)).map { s.p(it) }
                    drawPath(Path().apply { moveTo(q[0]); cubicTo(q[1], q[2], q[3]) }, p.sombra(C.CRIN, lejos), style = Stroke((0.02f * s.escala(0f, z)).coerceIn(0.3f, 1f), cap = StrokeCap.Round))
                }
            }
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
    //  Las ovejas y las cabras
    // =================================================================

    /** El borde de un vellón: una elipse de [rx] × [ry] con la orilla de arriba en bultos de lana; [az] da los bultos. */
    private fun vellon(c: Offset, rx: Float, ry: Float, az: Azar, bultos: Int = 9, rizo: Float = 0.16f): Path {
        val n = bultos * 4
        val r = FloatArray(bultos) { 1f + az.entre(-rizo, rizo) }
        val puntos = (0 until n).map { i ->
            val a = (2 * PI * i / n).toFloat()
            // Arriba (sen < 0), los bultos se notan; abajo, la barriga es más lisa y plana.
            val b = r[(i / 4) % bultos]; val arriba = (-sin(a)).coerceAtLeast(0f)
            val ondula = 1f + (b - 1f) * (0.35f + 0.65f * arriba) + 0.05f * arriba * sin(i * PI.toFloat() / 2f).let { it * it }
            val plano = if (sin(a) > 0f) 1f - 0.18f * sin(a) * sin(a) else 1f
            Offset(c.x + cos(a) * rx * ondula, c.y + sin(a) * ry * ondula * plano)
        }
        return io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(puntos + puntos.take(1)).apply { close() }
    }

    /**
     * Una oveja de cola gorda, la de la estepa: el cuerpo de lana blanca o crema, de bulto desigual, la cola gorda que
     * le cuelga detrás y la cabeza oscura de orejas caídas. Pisa en [pie] y mide [largo] unidades del hocico a la cola;
     * [mira] dice hacia qué lado (1 y −1). [pasta]: con la cabeza abajo. [patas]: si se le ven (las de la primera fila).
     * Con [cabra], una cabra: más flaca y alta, de pelo largo y oscuro, cuernos vueltos y barba. Con [cordero], un
     * cordero: chico de cuerpo, de patas largas, de lana corta y clara.
     *
     * De cerca, el vellón tiene volumen: la luz arriba y del lado del sol, la sombra violeta abajo, los mechones que
     * hacen rizos y la lana sucia de tierra hacia la barriga. De lejos queda una mancha con su cabeza.
     */
    fun DrawScope.oveja(p: Pincel, pie: Offset, largo: Float, mira: Float, az: Azar, pasta: Boolean, lejos: Float = 0f, patas: Boolean = true, cabra: Boolean = false, lana: Color = C.LANA, cabeza: Color = C.CABEZA, cordero: Boolean = false) {
        val u = largo; val dir = if (mira >= 0f) 1f else -1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        val ladoSol = if (p.solALaDerecha) 1f else -1f
        val detalle = u > 9f; val cerca = u > 18f
        // ---- Las proporciones de cada una.
        val piernas = when { cabra -> 0.3f; cordero -> 0.3f; else -> 0.2f }
        val rx = when { cabra -> 0.32f; cordero -> 0.27f; else -> 0.4f }
        val ry = when { cabra -> 0.15f; cordero -> 0.16f; else -> 0.21f }
        val centro = Offset(0.04f, -(piernas + ry * 0.92f))
        val col = when {
            cabra -> if (az.entero(4) == 0) lerp(C.FIELTRO, C.LANA, 0.5f) else lerp(lerp(C.CUERO, C.HOLLIN, 0.4f), lana, az.entre(0f, 0.35f))
            cordero -> lerp(lana, Color.White, 0.3f)
            else -> lerp(lana, if (az.entero(5) == 0) C.FIELTRO_VIEJO else C.LANA, 0.5f)
        }
        // Las de la estepa: unas de cabeza oscura, otras de cara blanca con manchas pardas.
        val caraBlanca = !cabra && az.entero(3) == 0
        val cabezaCol = when { cabra -> lerp(col, C.HOLLIN, 0.12f); caraBlanca -> lerp(col, C.FIELTRO_VIEJO, 0.4f); else -> cabeza }
        val pataCol = if (cabra) lerp(col, C.HOLLIN, 0.3f) else lerp(C.CABEZA, C.TIERRA, 0.25f)
        // ---- De lejos: una mancha de lana con su cabeza y un asomo de patas.
        if (!detalle) {
            val c = q(centro.x, centro.y)
            if (u > 4f && patas) for (x in listOf(-0.22f, 0.24f)) drawLine(p.medio(pataCol, lejos), q(x, -piernas), q(x, 0f), (0.06f * u).coerceAtLeast(0.35f))
            drawOval(Brush.verticalGradient(listOf(p.luz(col, lejos), p.por(col, -0.1f, lejos), p.sombra(col, lejos)), c.y - ry * u, c.y + ry * u), Offset(c.x - rx * u, c.y - ry * u), Size(2f * rx * u, 2f * ry * u))
            val h = if (pasta) q(-rx - 0.06f, -piernas * 0.5f) else q(-rx - 0.05f, centro.y - ry * 0.6f)
            drawOval(p.por(cabezaCol, 0.1f, lejos), Offset(h.x - 0.09f * u, h.y - 0.06f * u), Size(0.18f * u, 0.13f * u))
            return
        }
        // ---- Las patas, antes que el cuerpo (la lana las tapa arriba): finas, con la rodilla y la pezuña.
        if (patas) for ((x, delante) in listOf(-0.26f to false, 0.22f to false, -0.2f to true, 0.29f to true)) {
            val arriba = q(x, -piernas - 0.08f); val rodilla = q(x + (if (x > 0f) 0.025f else -0.01f), -piernas * 0.45f); val abajo = q(x + (if (delante) 0.012f else -0.012f), 0f)
            val g = ((if (cabra) 0.03f else 0.034f) * u).coerceAtLeast(0.4f)
            val c = if (delante) p.por(pataCol, if ((x > 0f) == (ladoSol * dir > 0f)) 0.3f else -0.2f, lejos) else p.sombra(lerp(pataCol, C.HOLLIN, 0.3f), lejos)
            drawLine(c, arriba, rodilla, g * 1.25f, StrokeCap.Round); drawLine(c, rodilla, abajo, g, StrokeCap.Round)
            drawOval(p.medio(lerp(C.HOLLIN, C.TIERRA, 0.2f), lejos), Offset(abajo.x - g * 0.9f, abajo.y - g * 0.9f), Size(g * 1.9f, g * 1.1f))
        }
        // ---- El cuerpo: el vellón con su cola gorda detrás (la cabra, flaca, con el fleco de pelo de la barriga).
        val cuerpo = if (cabra) Path().apply {
            val c = q(centro.x, centro.y); val w = rx * u; val h = ry * u
            moveTo(c + Offset(-w * dir, -h * 0.2f)); cubicTo(c + Offset(-w * 0.8f * dir, -h * 1.15f), c + Offset(w * 0.7f * dir, -h * 1.2f), c + Offset(w * dir, -h * 0.4f))
            cubicTo(c + Offset(w * 1.08f * dir, h * 0.3f), c + Offset(w * 0.9f * dir, h * 0.9f), c + Offset(w * 0.7f * dir, h * 1.0f))
            for (k in 0..8) { val t = k / 8f; lineTo(c + Offset((0.7f - 1.55f * t) * w * dir, h * (if (k % 2 == 0) 1.55f else 1.1f))) }
            cubicTo(c + Offset(-w * 1.0f * dir, h * 0.9f), c + Offset(-w * 1.1f * dir, h * 0.3f), c + Offset(-w * dir, -h * 0.2f)); close()
        } else vellon(q(centro.x, centro.y), rx * u, ry * u, az, 9, if (cordero) 0.06f else 0.13f).apply {
            addPath(vellon(q(rx * 0.84f, centro.y + ry * 0.5f), (if (cordero) 0.07f else 0.12f) * u, (if (cordero) 0.1f else 0.15f) * u, az, 5, 0.05f))
        }
        val arriba = pie.y + (centro.y - ry * 1.2f) * u; val abajoY = pie.y + (centro.y + ry * 1.5f) * u
        drawPath(cuerpo, p.por(col, 0f, lejos))
        clipPath(cuerpo) {
            // La luz de arriba y la sombra de abajo; el lado que da la espalda al sol, más frío.
            drawRect(Brush.verticalGradient(0f to p.luz(col, lejos), 0.4f to p.por(col, 0.15f, lejos), 0.75f to p.por(col, -0.4f, lejos), 1f to p.sombra(lerp(col, C.TIERRA, 0.3f), lejos), startY = arriba, endY = abajoY), Offset(pie.x - u, arriba), Size(2f * u, abajoY - arriba))
            val x0 = pie.x - 0.5f * u; val x1 = pie.x + 0.55f * u
            drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(col, lejos).copy(alpha = 0.5f), Color.Transparent, Color.Transparent) else listOf(Color.Transparent, Color.Transparent, p.sombra(col, lejos).copy(alpha = 0.5f)), x0, x1), Offset(x0, arriba), Size(x1 - x0, abajoY - arriba))
            if (!cabra) {
                // El vellón en mechones: toques blandos, cada uno con su luz encima y su hueco de sombra debajo, más
                // grandes en el lomo; la lana de la barriga, sucia de tierra.
                val n = if (cerca) (if (cordero) 22 else 46) else (if (cordero) 8 else 14)
                repeat(n) {
                    val t = az.entre(-0.95f, 0.95f); val v = az.entre(-0.95f, 0.85f) * sqrt((1f - t * t).coerceAtLeast(0f))
                    val c = q(centro.x + t * rx, centro.y + v * ry)
                    val w = (if (cordero) 0.04f else 0.055f) * u * az.entre(0.75f, 1.25f)
                    val alto = (1f - (v + 1f) / 2f).coerceIn(0f, 1f); val lado = t * ladoSol * dir
                    drawOval(p.sombra(lerp(col, C.TIERRA, 0.3f), lejos).copy(alpha = 0.22f + 0.18f * (1f - alto)), Offset(c.x - w, c.y - w * 0.25f), Size(w * 2f, w * 1.15f))
                    drawOval(p.luz(lerp(col, Color.White, 0.25f), lejos).copy(alpha = (0.22f + 0.45f * alto + 0.2f * lado).coerceIn(0.05f, 0.85f)), Offset(c.x - w * 0.85f, c.y - w * 0.7f), Size(w * 1.6f, w * 1.0f))
                }
                mancha(q(centro.x + 0.06f, centro.y + ry * 0.95f), rx * 0.95f * u, ry * 0.32f * u, p.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.4f), lejos), 0.4f)
            } else {
                // El pelo de la cabra: liso y brillante en el lomo, más oscuro y largo hacia el fleco.
                drawLine(p.luz(lerp(col, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.45f), q(centro.x - rx * 0.6f, centro.y - ry * 0.8f), q(centro.x + rx * 0.7f, centro.y - ry * 0.75f), (0.03f * u).coerceAtLeast(0.4f), StrokeCap.Round)
                if (cerca) for (k in 0 until 7) { val t = -0.7f + 1.4f * k / 6f; val a = q(centro.x + t * rx, centro.y + ry * 0.2f); drawLine(p.sombra(col, lejos).copy(alpha = 0.3f), a, a + Offset(0.01f * u * dir, ry * 1.1f * u), (0.012f * u).coerceAtLeast(0.35f), StrokeCap.Round) }
            }
        }
        if (cabra) drawLine(p.medio(lerp(col, C.HOLLIN, 0.4f), lejos), q(rx * 0.95f, centro.y - ry * 0.3f), q(rx * 1.08f, centro.y - ry * 1.3f), (0.035f * u).coerceAtLeast(0.4f), StrokeCap.Round)
        // ---- El cuello de lana (o de pelo) y la cabeza: arriba, o abajo pastando; la frente abombada, la oreja caída, el ojo.
        val hombro = q(centro.x - rx * 0.72f, centro.y - ry * 0.25f)
        val hocico = if (pasta) q(-rx - 0.16f, -0.025f) else q(-rx - 0.24f, centro.y - ry * (if (cabra) 1.9f else 1.05f))
        val largoCabeza = (if (cabra) 0.22f else if (cordero) 0.18f else 0.21f) * u
        val dc = (hocico - hombro).let { it / it.getDistance().coerceAtLeast(0.01f) }
        val nuca = hocico - dc * largoCabeza
        val nrm = Offset(-dc.y, dc.x).let { if (it.y < 0f) it * -1f else it }                // hacia la quijada
        // El cuello: un vellón más chico, del hombro a la nuca (en la cabra, un cuello liso).
        val cuello = if (cabra) Path().apply { val a = hombro; val b = nuca; val w0 = 0.1f * u; val w1 = 0.06f * u; moveTo(a - nrm * w0); lineTo(b - nrm * w1); lineTo(b + nrm * w1); lineTo(a + nrm * w0); close() }
            else vellon((hombro + nuca) * 0.5f, ((hombro - nuca).getDistance() * 0.62f).coerceAtLeast(0.08f * u), (if (cordero) 0.08f else 0.11f) * u, az, 5, 0.08f)
        drawPath(cuello, Brush.verticalGradient(listOf(p.luz(col, lejos), p.por(col, -0.35f, lejos)), minOf(hombro.y, nuca.y) - 0.1f * u, maxOf(hombro.y, nuca.y) + 0.12f * u))
        val g = largoCabeza
        val cabezaForma = Path().apply {
            moveTo(nuca - nrm * (g * 0.3f)); cubicTo(nuca + dc * (g * 0.45f) - nrm * (g * 0.42f), hocico - dc * (g * 0.3f) - nrm * (g * 0.26f), hocico - nrm * (g * 0.06f))
            cubicTo(hocico + dc * (g * 0.07f) + nrm * (g * 0.06f), hocico + nrm * (g * 0.22f), hocico - dc * (g * 0.1f) + nrm * (g * 0.25f))
            cubicTo(nuca + dc * (g * 0.45f) + nrm * (g * 0.34f), nuca + nrm * (g * 0.38f), nuca + nrm * (g * 0.28f)); close()
        }
        drawPath(cabezaForma, Brush.linearGradient(listOf(p.por(cabezaCol, 0.55f, lejos), p.por(cabezaCol, -0.5f, lejos)), nuca - nrm * (g * 0.4f), nuca + nrm * (g * 0.4f)))
        if (caraBlanca) clipPath(cabezaForma) { mancha(hocico - dc * (g * 0.2f), g * 0.3f, g * 0.25f, p.por(lerp(C.CUERO, C.HOLLIN, 0.3f), 0f, lejos), 0.7f) }
        // La mota de lana de la frente, que tapa la nuca.
        if (!cabra) drawPath(vellon(nuca - dc * (g * 0.08f) - nrm * (g * 0.06f), g * 0.2f, g * 0.19f, az, 4, 0.1f), p.por(col, 0.35f, lejos))
        // La oreja caída (la de la cabra, de lado), el ojo y el ollar.
        val oreja = nuca + dc * (g * 0.06f) - nrm * (g * 0.04f)
        val caida = (if (cabra) (dc * -0.55f - nrm * 0.45f) else (nrm * 0.6f - dc * 0.8f)).let { it / it.getDistance().coerceAtLeast(0.01f) }
        val punta = oreja + caida * (g * 0.5f)
        drawPath(Path().apply { val n2 = Offset(-caida.y, caida.x) * (g * 0.09f); moveTo(oreja + n2); cubicTo(oreja + n2 * 1.6f + caida * (g * 0.3f), punta + n2 * 0.6f, punta); cubicTo(punta - n2 * 0.8f, oreja - n2 * 1.3f + caida * (g * 0.3f), oreja - n2); close() }, p.por(lerp(cabezaCol, C.HOLLIN, 0.15f), -0.3f, lejos))
        drawCircle(p.medio(C.HOLLIN, lejos), (0.065f * g).coerceAtLeast(0.35f), nuca + dc * (g * 0.36f) - nrm * (g * 0.08f))
        if (cerca) drawCircle(Color.White.copy(alpha = 0.45f), (0.025f * g).coerceAtLeast(0.2f), nuca + dc * (g * 0.34f) - nrm * (g * 0.1f))
        drawLine(p.sombra(lerp(cabezaCol, C.HOLLIN, 0.4f), lejos), hocico - dc * (g * 0.05f), hocico - dc * (g * 0.15f) + nrm * (g * 0.06f), (0.05f * g).coerceAtLeast(0.3f), StrokeCap.Round)
        if (cabra) {
            // Los cuernos, que se vuelven hacia atrás, y la barba.
            val raiz = nuca - nrm * (g * 0.28f) + dc * (g * 0.08f)
            drawPath(Path().apply { moveTo(raiz); cubicTo(raiz - nrm * (g * 0.5f), raiz - nrm * (g * 0.62f) - dc * (g * 0.5f), raiz - nrm * (g * 0.2f) - dc * (g * 0.78f)) }, p.por(lerp(C.PIEDRA, C.CUERO, 0.3f), 0.3f, lejos), style = Stroke((0.09f * g).coerceAtLeast(0.4f), cap = StrokeCap.Round))
            drawLine(p.medio(lerp(col, C.HOLLIN, 0.3f), lejos), hocico - dc * (g * 0.22f) + nrm * (g * 0.2f), hocico - dc * (g * 0.3f) + nrm * (g * 0.55f), (0.08f * g).coerceAtLeast(0.35f), StrokeCap.Round)
        }
        // De noche, la lana clara toma un filo de luna en el lomo.
        if (p.l.noche > 0.3f && !cabra) clipPath(cuerpo) { drawRect(Brush.verticalGradient(listOf(C.LUNA.copy(alpha = 0.3f * p.l.noche), Color.Transparent), arriba, arriba + ry * u * 0.9f), Offset(pie.x - u, arriba), Size(2f * u, ry * u)) }
    }

    /** La silueta de una oveja, para su sombra arrojada. */
    fun siluetaDeOveja(pie: Offset, largo: Float): Path = Path().apply {
        addOval(androidx.compose.ui.geometry.Rect(pie.x - 0.42f * largo, pie.y - 0.66f * largo, pie.x + 0.46f * largo, pie.y - 0.2f * largo))
        addPath(poligono(listOf(pie + Offset(-0.3f * largo, -0.22f * largo), pie + Offset(0.3f * largo, -0.22f * largo), pie + Offset(0.3f * largo, 0f), pie + Offset(-0.3f * largo, 0f))))
    }

    /**
     * Un rebaño apretado: las ovejas de [sitios] (dónde pisa cada una, de atrás adelante se ordenan solas) con su talla
     * según la profundidad ([talla] de la y). Se tapan unas a otras: lomos que asoman detrás de lomos. Una de cada
     * [cabras], cabra. Las de detrás no enseñan las patas. Las chicas (lejos) son manchas con su cabeza y su sombra corta.
     */
    fun DrawScope.rebano(p: Pincel, sitios: List<Offset>, talla: (Float) -> Float, az: Azar, lejos: (Float) -> Float, pastan: Float = 0.5f, cabras: Int = 7) {
        val orden = sitios.sortedBy { it.y }
        // El suelo pisado bajo el rebaño: un poco más oscuro, para que las ovejas no floten.
        for (q in orden) { val u = talla(q.y); drawOval(p.sombra(C.TIERRA, lejos(q.y)).copy(alpha = 0.22f), Offset(q.x - 0.5f * u, q.y - 0.08f * u), Size(u, 0.18f * u)) }
        for ((i, q) in orden.withIndex()) {
            val u = talla(q.y); val l = lejos(q.y)
            if (p.r.fuerza > 0.05f) { val d = (p.r.ax * 0.3f * u).coerceIn(-u, u); drawOval(p.r.color.copy(alpha = (0.55f * p.r.fuerza).coerceIn(0f, 1f)), Offset(q.x - 0.42f * u + minOf(0f, d), q.y - 0.06f * u), Size(0.84f * u + abs(d), 0.13f * u), blendMode = BlendMode.Multiply) }
            val delante = orden.drop(i + 1).none { abs(it.x - q.x) < u * 0.5f && it.y - q.y < u * 0.3f && it.y > q.y }
            oveja(p, q, u, if (az.entero(3) == 0) 1f else -1f, az, az.real() < pastan, l, delante, cabra = az.entero(cabras) == 0)
        }
    }

    /**
     * Un rebaño muy apretado, como en el corral o a la hora de entrar: [cuantas] ovejas dentro de una elipse de centro
     * [centro] y radios [rx] × [ry], en hileras que se tapan (de cada una de atrás asoma el lomo y a veces la cabeza),
     * con su talla según la profundidad. Algunas cabras y, en la parición, [corderos] pegados a las madres de delante.
     * **[Diseño propio]**: el reparto es un tresbolillo con algo de azar, no una simulación.
     */
    fun DrawScope.rebanoApretado(p: Pincel, sol: Sol3, centro: Offset, rx: Float, ry: Float, cuantas: Int, talla: (Float) -> Float, az: Azar, lejos: Float = 0f, pastan: Float = 0.3f, corderos: Int = 0, cabras: Int = 8) {
        val sitios = ArrayList<Offset>()
        // Las hileras, de atrás adelante: cada una más grande; dentro, las ovejas se pisan un poco.
        // El paso entre hileras, para que las que hay llenen la elipse (ni una fila larga ni un hormiguero).
        val uc = talla(centro.y); val paso = (PI.toFloat() * rx * ry / (cuantas * 0.54f * uc)).coerceIn(0.12f * uc, 0.45f * uc)
        var y = centro.y - ry; var fila = 0
        while (sitios.size < cuantas && y <= centro.y + ry) {
            val u = talla(y); val k = (y - centro.y) / ry; val ancho = rx * sqrt((1f - k * k).coerceAtLeast(0f))
            var x = centro.x - ancho + (if (fila % 2 == 0) 0f else u * 0.28f) + az.entre(0f, u * 0.2f)
            while (x <= centro.x + ancho && sitios.size < cuantas) { sitios += Offset(x + az.entre(-u * 0.08f, u * 0.08f), y + az.entre(-u * 0.04f, u * 0.04f)); x += u * az.entre(0.46f, 0.62f) }
            y += paso * u / uc; fila++
        }
        // El suelo pisado, oscuro de estiércol, y la sombra de la masa entera.
        if (sitios.isNotEmpty()) {
            val x0 = sitios.minOf { it.x } - talla(centro.y) * 0.5f; val x1 = sitios.maxOf { it.x } + talla(centro.y) * 0.5f
            val y0 = sitios.minOf { it.y }; val y1 = sitios.maxOf { it.y }
            drawOval(p.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.4f), lejos).copy(alpha = 0.4f), Offset(x0, y0 - talla(y0) * 0.1f), Size(x1 - x0, y1 - y0 + talla(y1) * 0.25f))
        }
        val orden = sitios.sortedBy { it.y }
        for ((i, q) in orden.withIndex()) {
            val u = talla(q.y)
            val delante = orden.drop(i + 1).none { abs(it.x - q.x) < u * 0.45f && it.y - q.y < u * 0.3f }
            oveja(p, q, u, if (az.entero(2) == 0) 1f else -1f, az, az.real() < pastan, lejos, delante, cabra = az.entero(cabras) == 0)
        }
        // Los corderos, pegados a las madres de la primera fila.
        val delanteras = orden.takeLast(maxOf(corderos * 2, 1))
        for (j in 0 until corderos) { val madre = delanteras[(j * 2) % delanteras.size]; val u = talla(madre.y) * 0.62f; oveja(p, madre + Offset(az.entre(-0.3f, 0.3f) * u * 1.6f, u * 0.32f), u, if (j % 2 == 0) 1f else -1f, az, j % 3 == 0, lejos, true, cordero = true) }
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
     * Cómo está un caballo. **[Aproximación]**: las posturas son las de un caballo de la estepa visto de lado, dibujadas
     * a mano (no salen de un esqueleto).
     * - [DE_PIE]: cuadrado, con la cabeza a media altura.
     * - [PASTANDO]: el cuello abajo y el morro en la hierba.
     * - [MIRANDO_ATRAS]: vuelve la cabeza hacia quien mira y hacia la grupa, por encima del lomo.
     * - [DESCANSO]: la cabeza baja, las orejas flojas y una pata de atrás doblada, apoyada en la punta del casco: el Negro
     *   atado junto a la puerta, de noche.
     * - [ECHADO]: tumbado sobre el pecho, las patas recogidas, el cuello en alto (el potro).
     * - [MAMANDO]: de pie, con el cuello estirado hacia abajo y el morro hacia arriba, buscando la ubre (el potro; ver
     *   [parejaMamando]).
     */
    enum class Postura { DE_PIE, PASTANDO, MIRANDO_ATRAS, DESCANSO, ECHADO, MAMANDO }

    /** Lo que tiene cada pelo: la crin, las patas oscuras (los bayos y los isabelinos), la raya del lomo, las motas del tordo, el morro claro, el lucero, el brillo. */
    private class Capa(val pelo: Color, val crin: Color, val patas: Color?, val raya: Boolean, val motas: Boolean, val morro: Color, val lucero: Boolean, val brillo: Color)

    private fun capaDe(pelo: Color, crin: Color?): Capa = when (pelo) {
        BAYO -> Capa(pelo, crin ?: lerp(C.HOLLIN, C.CUERO, 0.18f), lerp(C.HOLLIN, C.CUERO, 0.25f), true, false, lerp(pelo, C.FIELTRO, 0.45f), false, lerp(pelo, C.ORO, 0.4f))
        ISABELINO -> Capa(pelo, crin ?: lerp(C.HOLLIN, C.CUERO, 0.35f), lerp(C.HOLLIN, C.CUERO, 0.4f), true, false, lerp(pelo, C.FIELTRO, 0.4f), false, lerp(pelo, C.FIELTRO, 0.35f))
        TORDO -> Capa(pelo, crin ?: lerp(C.PIEDRA, C.HOLLIN, 0.5f), lerp(pelo, C.HOLLIN, 0.35f), false, true, lerp(C.PIEDRA, C.HOLLIN, 0.4f), false, lerp(pelo, Color.White, 0.4f))
        NEGRO -> Capa(pelo, crin ?: lerp(C.HOLLIN, C.CUERO, 0.12f), null, false, false, lerp(pelo, C.CUERO, 0.35f), true, lerp(C.SOMBRA, C.FIELTRO, 0.35f))
        BLANCO -> Capa(pelo, crin ?: lerp(C.FIELTRO, C.PIEDRA, 0.4f), null, false, false, lerp(C.PIEDRA, C.CUERO, 0.3f), false, lerp(pelo, Color.White, 0.5f))
        ALAZAN -> Capa(pelo, crin ?: lerp(pelo, C.ORO, 0.35f), null, false, false, lerp(pelo, C.HOLLIN, 0.3f), true, lerp(pelo, C.ORO, 0.45f))
        else -> Capa(pelo, crin ?: lerp(pelo, C.HOLLIN, 0.6f), null, false, false, lerp(pelo, C.HOLLIN, 0.3f), false, lerp(pelo, C.FIELTRO, 0.35f))
    }

    /**
     * La forma de un caballo en una postura, en sus propias medidas: [alto] unidades hasta la cruz, de lado, con la
     * cabeza hacia la izquierda si [mira] es −1 (y hacia la derecha si es 1). Las medidas, en centésimas de la cruz; el
     * potro es de patas largas, cuerpo corto y cabeza grande.
     */
    private class Anatomia(val pie: Offset, val alto: Float, mira: Float, val postura: Postura, val potro: Boolean) {
        val u = alto / 100f
        val dir = if (mira >= 0f) -1f else 1f                                        // el dibujo mira a la izquierda; con −1 se voltea
        private val echado = postura == Postura.ECHADO
        /** Cuánto baja el cuerpo al echarse: hasta que la barriga toca el suelo. */
        private val baja = if (echado) -mapa(0f, -42f).y - 3f else 0f

        /** Las proporciones del potro: más corto de cuerpo y de patas más largas (de la cruz al suelo es lo mismo). */
        fun mapa(x: Float, y: Float): Offset = if (!potro) Offset(x, y) else Offset(x * 0.82f, if (y <= -46f) -100f + (y + 100f) * (40f / 54f) else y * (60f / 46f))
        /** Un punto del cuerpo (en las medidas del adulto) en el lienzo. */
        fun c(x: Float, y: Float): Offset { val m = mapa(x, y); return q(m.x, m.y + baja) }
        /** Un punto ya en las medidas del caballo (sin el mapa del potro) en el lienzo. */
        fun q(x: Float, y: Float): Offset = Offset(pie.x + x * u * dir, pie.y + y * u)

        // ---- El cuerpo: la cruz, el dorso, la grupa, la nalga, la babilla, la barriga, la cinchera, el codo y el pecho.
        val cuerpo: Path = Path().apply {
            moveTo(c(-28f, -101f)); cubicTo(c(-18f, -97f), c(-6f, -93f), c(4f, -93.5f)); cubicTo(c(16f, -94f), c(28f, -100f), c(40f, -100.5f))
            cubicTo(c(48f, -101f), c(54f, -98f), c(57f, -93f)); cubicTo(c(62f, -86f), c(64f, -76f), c(61f, -66f)); cubicTo(c(59f, -58f), c(56f, -52f), c(53f, -47f))
            cubicTo(c(46f, -50f), c(40f, -52f), c(34f, -49f)); cubicTo(c(22f, -43f), c(0f, -41f), c(-20f, -44f)); cubicTo(c(-28f, -45f), c(-34f, -47f), c(-38f, -51f))
            cubicTo(c(-44f, -53f), c(-50f, -60f), c(-53f, -70f)); cubicTo(c(-55f, -78f), c(-50f, -88f), c(-44f, -94f)); cubicTo(c(-40f, -98f), c(-34f, -101f), c(-28f, -101f)); close()
        }
        /** La línea de arriba del cuerpo (para el filo de la luna). */
        val lomo: List<Offset> = listOf(c(-30f, -100f), c(-18f, -97f), c(-4f, -93.5f), c(10f, -93.5f), c(24f, -97f), c(38f, -100.5f), c(50f, -99f), c(57f, -93f))

        // ---- El cuello y la cabeza, según la postura.
        private val base = Offset(-38f, -88f)
        /** Donde la cabeza se junta con el cuello, la dirección de la cabeza y cuánto se arquea el cuello. */
        private val junta: Offset; private val haciaCabeza: Offset; private val arco: Float
        init {
            val (j, d, a) = when (postura) {
                Postura.DE_PIE -> Triple(Offset(-64f, -128f), Offset(-0.66f, 0.75f), 5f)
                Postura.PASTANDO -> Triple(Offset(-70f, -52f), Offset(-0.3f, 0.95f), 4f)
                Postura.MIRANDO_ATRAS -> Triple(Offset(-36f, -108f), Offset(0.9f, 0.44f), 14f)
                Postura.DESCANSO -> Triple(Offset(-70f, -110f), Offset(-0.52f, 0.85f), 3f)
                Postura.ECHADO -> Triple(Offset(-62f, -126f), Offset(-0.6f, 0.8f), 5f)
                Postura.MAMANDO -> Triple(Offset(-76f, -40f), Offset(-0.86f, -0.5f), -2f)
            }
            junta = j; haciaCabeza = d; arco = a
        }
        /** La cabeza vuelta hacia atrás (al mirar atrás): se dibuja al revés. */
        val alReves = postura == Postura.MIRANDO_ATRAS
        /** El cuello: una línea del medio, y a cada lado la cresta (de donde cae la crin) y la garganta. */
        val cresta: List<Offset>; val garganta: List<Offset>
        init {
            val b = mapa(base.x, base.y) + Offset(0f, baja); val j = mapa(junta.x, junta.y) + Offset(0f, baja)
            val d = j - b; val l = d.getDistance(); val n0 = Offset(-d.y, d.x) / l              // hacia la cresta
            val p1 = b + d * 0.33f + n0 * arco; val p2 = b + d * 0.72f + n0 * (arco * 0.6f)
            val cr = ArrayList<Offset>(); val ga = ArrayList<Offset>()
            for (i in 0..10) {
                val t = i / 10f; val s = 1f - t
                val m = b * (s * s * s) + p1 * (3f * s * s * t) + p2 * (3f * s * t * t) + j * (t * t * t)
                val tg = (p1 - b) * (3f * s * s) + (p2 - p1) * (6f * s * t) + (j - p2) * (3f * t * t)
                val nn = Offset(-tg.y, tg.x) / tg.getDistance().coerceAtLeast(0.01f)
                val ancho = (if (potro) 13f else 16.5f) * (1f - t) + (if (potro) 8f else 9.5f) * t
                val bulto = 1f + 0.18f * sin(PI.toFloat() * t)
                cr += m + nn * (ancho * bulto); ga += m - nn * ancho
            }
            cresta = cr.map { q(it.x, it.y) }; garganta = ga.map { q(it.x, it.y) }
        }
        val cuello: Path = Path().apply { moveTo(cresta[0]); for (k in cresta.drop(1)) lineTo(k); for (k in garganta.reversed()) lineTo(k); close() }

        /** El tamaño de la cabeza: la del potro, más grande para su cuerpo. */
        private val hs = if (potro) 1.05f else 1f
        private val jm = mapa(junta.x, junta.y) + Offset(0f, baja)
        /** Un punto de la cabeza: [hx] hacia el morro, [hy] hacia la quijada, en centésimas. */
        fun h(hx: Float, hy: Float): Offset {
            val d = haciaCabeza; val n = if (alReves) Offset(-d.y, d.x) else Offset(d.y, -d.x)
            val k = if (alReves) 0.82f else 1f                                     // de tres cuartos, se ve más corta
            return q(jm.x + (d.x * hx * k + n.x * hy) * hs, jm.y + (d.y * hx * k + n.y * hy) * hs)
        }
        val cabeza: Path = Path().apply {
            moveTo(h(-3f, -9f)); cubicTo(h(4f, -11.5f), h(12f, -11.5f), h(20f, -10f)); cubicTo(h(30f, -8.5f), h(40f, -6.5f), h(47f, -4f))
            cubicTo(h(51f, -2.5f), h(54f, 0f), h(53.5f, 3f)); cubicTo(h(53f, 5f), h(50f, 6.5f), h(47.5f, 7f)); cubicTo(h(47f, 9f), h(45f, 10f), h(42f, 9.5f))
            cubicTo(h(36f, 10f), h(30f, 11.5f), h(24f, 14f)); cubicTo(h(18f, 16.5f), h(9f, 16f), h(5f, 13f)); cubicTo(h(2f, 11f), h(-1f, 9f), h(-3f, 7f)); close()
        }
        /** El hocico, donde llega el morro. */
        val hocico: Offset get() = h(52f, 2f)

        // ---- Las patas: de arriba abajo, cada punto con su medio ancho; el corvejón (en las de atrás) y cómo pisa el casco.
        class Pata(val puntos: List<Triple<Float, Float, Float>>, val lejana: Boolean, val trasera: Boolean, val casco: Float = 0f, val directa: Boolean = false)
        val patas: List<Pata> = when (postura) {
            Postura.ECHADO -> listOf(
                Pata(listOf(Triple(-36f, -15f, 6.5f), Triple(-50f, -10f, 5f), Triple(-60f, -7f, 4.4f), Triple(-70f, -4f, 3.2f), Triple(-74f, -2.6f, 3.4f)), true, false, 1.2f, true),
                Pata(listOf(Triple(50f, -10f, 6f), Triple(58f, -6f, 4.6f), Triple(66f, -3.5f, 3.3f), Triple(70f, -2.6f, 3.4f)), true, true, 1.4f, true),
                Pata(listOf(Triple(-38f, -18f, 7.5f), Triple(-48f, -10f, 6f), Triple(-56f, -6f, 4.6f), Triple(-50f, -3.6f, 3.4f), Triple(-42f, -3f, 3.2f), Triple(-37f, -2.8f, 3.4f)), false, false, -1.5f, true),
                Pata(listOf(Triple(30f, -24f, 12f), Triple(44f, -10f, 7f), Triple(52f, -5f, 5f), Triple(42f, -3.4f, 3.4f), Triple(30f, -3f, 3.3f), Triple(24f, -2.8f, 3.5f)), false, true, 1.5f, true),
            )
            Postura.DESCANSO -> listOf(
                Pata(listOf(Triple(-35f, -66f, 9f), Triple(-34f, -52f, 7f), Triple(-33f, -38f, 5f), Triple(-32f, -27f, 4.4f), Triple(-31f, -19f, 3.2f), Triple(-30f, -10f, 3.1f), Triple(-31f, -6f, 3.8f), Triple(-33f, -2.5f, 3.4f)), true, false),
                Pata(listOf(Triple(34f, -66f, 13f), Triple(41f, -50f, 9f), Triple(44f, -40f, 6.4f), Triple(47f, -30f, 5.2f), Triple(45f, -20f, 3.4f), Triple(44f, -10f, 3.3f), Triple(43f, -6f, 4f), Triple(40f, -2.5f, 3.5f)), true, true),
                Pata(listOf(Triple(-41f, -66f, 10f), Triple(-40f, -52f, 7.5f), Triple(-39f, -38f, 5.2f), Triple(-39f, -27f, 4.6f), Triple(-39f, -20f, 3.3f), Triple(-39f, -10f, 3.2f), Triple(-40f, -6f, 4f), Triple(-42f, -2.5f, 3.6f)), false, false),
                // La de atrás de este lado, doblada: la cadera cae, el corvejón se adelanta y el casco apoya de punta.
                Pata(listOf(Triple(40f, -64f, 15f), Triple(44f, -48f, 10f), Triple(46f, -38f, 7f), Triple(48f, -27f, 5.6f), Triple(43f, -17f, 3.6f), Triple(39f, -9f, 3.4f), Triple(37f, -5.6f, 4f), Triple(35f, -2.4f, 3.5f)), false, true, -2.2f),
            )
            Postura.MAMANDO -> listOf(
                Pata(listOf(Triple(-35f, -66f, 9f), Triple(-35f, -52f, 7f), Triple(-36f, -38f, 5f), Triple(-37f, -27f, 4.4f), Triple(-39f, -19f, 3.2f), Triple(-41f, -10f, 3.1f), Triple(-42f, -6f, 3.8f), Triple(-44f, -2.5f, 3.4f)), true, false),
                Pata(listOf(Triple(34f, -66f, 13f), Triple(41f, -50f, 9f), Triple(46f, -40f, 6.4f), Triple(50f, -30f, 5.2f), Triple(49f, -20f, 3.4f), Triple(49f, -10f, 3.3f), Triple(49f, -6f, 4f), Triple(47f, -2.5f, 3.5f)), true, true),
                Pata(listOf(Triple(-41f, -66f, 10f), Triple(-41f, -52f, 7.5f), Triple(-42f, -38f, 5.2f), Triple(-44f, -27f, 4.6f), Triple(-46f, -20f, 3.3f), Triple(-48f, -10f, 3.2f), Triple(-49f, -6f, 4f), Triple(-51f, -2.5f, 3.6f)), false, false),
                Pata(listOf(Triple(40f, -66f, 15f), Triple(47f, -50f, 10f), Triple(51f, -40f, 7f), Triple(55f, -30f, 5.6f), Triple(54f, -20f, 3.6f), Triple(54f, -10f, 3.4f), Triple(53f, -6f, 4.2f), Triple(51f, -2.5f, 3.7f)), false, true),
            )
            else -> listOf(
                Pata(listOf(Triple(-35f, -66f, 9f), Triple(-34f, -52f, 7f), Triple(-33f, -38f, 5f), Triple(-32f, -27f, 4.4f), Triple(-31f, -19f, 3.2f), Triple(-30f, -10f, 3.1f), Triple(-31f, -6f, 3.8f), Triple(-33f, -2.5f, 3.4f)), true, false),
                Pata(listOf(Triple(34f, -66f, 13f), Triple(41f, -50f, 9f), Triple(44f, -40f, 6.4f), Triple(47f, -30f, 5.2f), Triple(45f, -20f, 3.4f), Triple(44f, -10f, 3.3f), Triple(43f, -6f, 4f), Triple(40f, -2.5f, 3.5f)), true, true),
                Pata(listOf(Triple(-41f, -66f, 10f), Triple(-40f, -52f, 7.5f), Triple(-39f, -38f, 5.2f), Triple(-39f, -27f, 4.6f), Triple(-39f, -20f, 3.3f), Triple(-39f, -10f, 3.2f), Triple(-40f, -6f, 4f), Triple(-42f, -2.5f, 3.6f)), false, false),
                Pata(listOf(Triple(40f, -66f, 15f), Triple(47f, -50f, 10f), Triple(50f, -40f, 7f), Triple(53f, -30f, 5.6f), Triple(51f, -20f, 3.6f), Triple(50f, -10f, 3.4f), Triple(49f, -6f, 4.2f), Triple(46f, -2.5f, 3.7f)), false, true),
            )
        }
        /** Un punto de una pata en el lienzo: las del caballo echado ya están en su sitio; las demás pasan por el mapa del potro. */
        fun pq(pata: Pata, x: Float, y: Float): Offset = if (pata.directa) q(x * (if (potro) 0.82f else 1f), y) else c(x, y)

        /** La forma de una pata: el contorno suave, con su medio ancho en cada punto. */
        fun formaDe(pata: Pata): Path {
            val pts = pata.puntos.map { pq(pata, it.first, it.second) }; val an = pata.puntos.map { it.third * u * (if (potro) 0.85f else 1f) }
            val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
            for (i in pts.indices) {
                val a = pts[maxOf(i - 1, 0)]; val b = pts[minOf(i + 1, pts.size - 1)]; val d = b - a; val l = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y, d.x) / l
                izq += pts[i] + n * an[i]; der += pts[i] - n * an[i]
            }
            return io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(izq + der.reversed() + izq.take(1)).apply { close() }
        }

        // ---- La cola: cae de la grupa casi hasta el suelo, espesa, con su peso.
        fun cola(viento: Float): Path {
            val v = viento.coerceIn(-1f, 1f) * 10f * dir
            return if (echado) Path().apply {
                moveTo(c(54f, -96f)); cubicTo(c(66f, -90f), q(72f, -20f), q(76f, -6f)); cubicTo(q(82f, -2f), q(96f, -1f), q(104f, -2f)); lineTo(q(104f, 0f)); cubicTo(q(90f, 1f), q(76f, 1f), q(70f, -1f)); cubicTo(q(66f, -14f), c(60f, -70f), c(50f, -92f)); close()
            } else Path().apply {
                val t0 = c(53f, -98f); val t1 = c(58f, -95f)
                moveTo(t0); cubicTo(t1 + Offset(6f * u * -dir * -1f, 0f), c(70f, -78f), c(68f + v * 0.3f, -50f)); cubicTo(c(67f + v * 0.6f, -30f), c(64f + v, -14f), c(63f + v, -7f))
                cubicTo(c(61f + v, -4f), c(56f + v, -4f), c(55f + v, -8f)); cubicTo(c(57f + v * 0.6f, -26f), c(59f + v * 0.3f, -50f), c(56f, -74f)); cubicTo(c(55f, -84f), c(52f, -92f), t0); close()
            }
        }
    }

    /**
     * Un caballo de la estepa, de lado: pequeño, recio, de patas cortas, cabeza grande y crin espesa. Pisa en [pie] con
     * [alto] unidades hasta la cruz; [mira] −1 hacia la izquierda, 1 hacia la derecha. [pasta]: con la cabeza baja (lo
     * mismo que [postura] `PASTANDO`). [potro]: de patas largas, cuerpo corto y crin de cepillo. [silla]: con la silla de
     * madera alta y su manta. [crin]: el color de la crin y la cola (por omisión, el de su pelo). [cintas]: cintas azules
     * en la crin (las carreras). [viento], de −1 a 1: tiende la cola. [lumbreX]: dónde está, en el lienzo, la lumbre que
     * lo alumbra de noche (con el pincel `con(calida)`); por omisión, del lado de la cabeza.
     *
     * Lleva su sombra de contacto debajo (la arrojada va en una `capaSombra`, con [siluetaDeCaballo]). Cada pelo tiene lo
     * suyo: los bayos y los isabelinos, las patas negras, la raya del lomo y el morro claro; el tordo, las motas; el
     * negro y el alazán, un lucero; el negro, un brillo frío en el pelo para que no quede una mancha. De noche, el lomo
     * con la luz de la luna y un filo cálido del lado de la lumbre.
     */
    fun DrawScope.caballo(
        p: Pincel, pie: Offset, alto: Float, mira: Float, pelo: Color, pasta: Boolean = false, potro: Boolean = false, silla: Boolean = false, crin: Color? = null,
        lejos: Float = 0f, cintas: Boolean = false, postura: Postura = if (pasta) Postura.PASTANDO else Postura.DE_PIE, viento: Float = 0f, lumbreX: Float? = null,
    ) {
        val a = Anatomia(pie, alto, mira, postura, potro); val u = a.u; val dir = a.dir
        val capa = capaDe(pelo, crin)
        val detalle = alto > 20f; val fino = alto > 40f
        val noche = p.l.noche
        val luz = p.luz(lerp(pelo, capa.brillo, 0.35f), lejos); val medio = p.por(pelo, 0.05f, lejos); val sombra = p.sombra(lerp(pelo, capa.crin, 0.25f), lejos)
        val crinCol = p.medio(capa.crin, lejos); val crinLuz = p.luz(lerp(capa.crin, capa.brillo, 0.5f), lejos)
        val solDelante = p.solALaDerecha == (dir < 0f)                            // el sol le da por delante (por la cabeza)
        val ladoSol = if (p.solALaDerecha) 1f else -1f
        /** Las luces y las sombras de un trozo de cuerpo: arriba la luz, abajo la sombra; el lado del sol, más claro. */
        fun vestir(forma: Path, y0: Float, y1: Float, x0: Float = pie.x - 110f * u, x1: Float = pie.x + 110f * u) {
            drawPath(forma, medio)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(0f to luz, 0.36f to luz.copy(alpha = 0f), 0.58f to sombra.copy(alpha = 0f), 1f to sombra, startY = y0, endY = y1), Offset(x0, y0 - 2f), Size(x1 - x0, y1 - y0 + 4f))
                drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(sombra.copy(alpha = 0.55f), Color.Transparent, luz.copy(alpha = 0.22f)) else listOf(luz.copy(alpha = 0.22f), Color.Transparent, sombra.copy(alpha = 0.55f)), x0, x1), Offset(x0, y0 - 2f), Size(x1 - x0, y1 - y0 + 4f))
            }
        }
        // ---- La sombra de contacto: bajo los cascos y la barriga, también sin sol.
        val sx = a.q(-46f, 0f).x; val sx2 = a.q(58f, 0f).x
        drawOval(p.sombra(C.TIERRA, lejos).copy(alpha = if (postura == Postura.ECHADO) 0.5f else 0.38f), Offset(minOf(sx, sx2) - 4f * u, pie.y - 3f * u), Size(abs(sx2 - sx) + 8f * u, 5.5f * u))

        // ---- Una pata: el contorno, la sombra que baja, las patas negras de los bayos, el menudillo peludo y el casco.
        fun pata(pt: Anatomia.Pata) {
            val forma = a.formaDe(pt)
            val pts = pt.puntos.map { a.pq(pt, it.first, it.second) }
            val oscura = capa.patas
            drawPath(forma, if (pt.lejana) p.sombra(lerp(pelo, capa.crin, 0.4f), lejos) else medio)
            clipPath(forma) {
                val y0 = a.c(0f, -66f).y; val y1 = pie.y
                if (!pt.lejana) drawRect(Brush.verticalGradient(0f to luz.copy(alpha = 0.35f), 0.25f to Color.Transparent, 0.6f to sombra.copy(alpha = 0.35f), 1f to sombra.copy(alpha = 0.7f), startY = y0, endY = y1), Offset(pie.x - 120f * u, y0), Size(240f * u, y1 - y0 + 2f))
                // Las patas oscuras, de la rodilla o del corvejón para abajo.
                if (oscura != null) { val desde = pts[minOf(if (pt.trasera) 3 else 3, pts.size - 1)].y - 3f * u; drawRect(Brush.verticalGradient(listOf(p.medio(oscura, lejos).copy(alpha = 0f), p.medio(oscura, lejos), p.sombra(oscura, lejos)), desde - 6f * u, pie.y), Offset(pie.x - 120f * u, desde - 6f * u), Size(240f * u, pie.y - desde + 8f * u)) }
                if (!pt.lejana && detalle) {
                    // El filo de luz del lado del sol, y el tendón de la caña.
                    val k = if ((ladoSol * dir) > 0f) -1f else 1f
                    for (i in 1 until pts.size - 2) drawLine(luz.copy(alpha = 0.3f), pts[i] + Offset(k * pt.puntos[i].third * u * 0.5f * -dir, 0f), pts[i + 1] + Offset(k * pt.puntos[i + 1].third * u * 0.5f * -dir, 0f), (pt.puntos[i].third * 0.45f) * u, StrokeCap.Round)
                }
            }
            // El menudillo, con su pelo largo que cae sobre el casco.
            val menudillo = pts[pts.size - 2]; val casco = pts.last()
            val pelos = p.medio(lerp(oscura ?: capa.crin, C.HOLLIN, 0.2f), lejos)
            if (detalle) for (k in -2..2) drawLine(pelos.copy(alpha = if (pt.lejana) 0.5f else 0.8f), menudillo + Offset((k * 1.1f + 1.6f) * u * dir, -1.6f * u), casco + Offset((k * 1.3f + 2.4f) * u * dir, 0.4f * u), 0.9f * u, StrokeCap.Round)
            // El casco: de canto ancho abajo, inclinado; el echado lo tiene de lado.
            val inclina = pt.casco
            val ancho = 4.2f * u * (if (potro) 0.85f else 1f)
            val cascoForma = poligono(listOf(casco + Offset(-ancho * 0.75f * dir, -2.6f * u + inclina * u), casco + Offset(ancho * 0.75f * dir, -2.6f * u - inclina * u), casco + Offset(ancho * 1.05f * dir, 2.3f * u), casco + Offset(-ancho * 1.3f * dir, 2.3f * u)))
            drawPath(cascoForma, p.por(lerp(C.HOLLIN, C.TIERRA, 0.25f), if (pt.lejana) -0.6f else 0.1f, lejos))
            if (detalle && !pt.lejana) drawLine(p.luz(lerp(C.HOLLIN, C.PIEDRA, 0.4f), lejos).copy(alpha = 0.6f), casco + Offset(-ancho * 1.2f * dir, 2f * u), casco + Offset(-ancho * 0.6f * dir, -2.4f * u), 0.7f * u)
        }

        val lejanas = a.patas.filter { it.lejana }; val cercanas = a.patas.filter { !it.lejana }
        val echado = postura == Postura.ECHADO
        for (pt in lejanas) pata(pt)
        if (!echado) for (pt in cercanas) pata(pt)
        // ---- La cola: una masa espesa con mechones; de noche, el filo de la luna.
        val cola = a.cola(viento)
        drawPath(cola, crinCol)
        if (detalle) clipPath(cola) {
            val y0 = a.c(0f, -98f).y
            for (k in 0..9) { val x = 54f + k * 1.6f; drawLine((if (k % 3 == 0) crinLuz else p.sombra(capa.crin, lejos)).copy(alpha = 0.55f), a.c(x - 2f, -96f), a.c(x + 4f + viento * 6f, -6f), 0.8f * u, StrokeCap.Round) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(capa.crin, lejos).copy(alpha = 0.6f)), y0, pie.y), Offset(pie.x - 120f * u, y0), Size(240f * u, pie.y - y0))
        }
        // ---- El cuerpo.
        val yCruz = a.c(0f, -101f).y; val yBarriga = a.c(0f, -42f).y
        vestir(a.cuerpo, yCruz, yBarriga)
        clipPath(a.cuerpo) {
            // La paletilla y el anca, dos masas con su luz arriba; el costillar y la barriga en sombra; el pliegue de la babilla.
            mancha(a.c(-36f, -82f), 14f * u, 17f * u, luz, 0.45f)
            mancha(a.c(40f, -88f), 18f * u, 14f * u, luz, 0.5f)
            mancha(a.c(4f, -46f), 36f * u, 7f * u, sombra, 0.5f)
            if (detalle) {
                mancha(a.c(-26f, -58f), 6f * u, 12f * u, sombra, 0.32f)
                mancha(a.c(46f, -62f), 8f * u, 12f * u, sombra, 0.25f)
                drawPath(Path().apply { moveTo(a.c(34f, -84f)); cubicTo(a.c(30f, -72f), a.c(32f, -58f), a.c(36f, -50f)) }, sombra.copy(alpha = 0.45f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(a.c(-44f, -90f)); cubicTo(a.c(-36f, -80f), a.c(-34f, -66f), a.c(-37f, -54f)) }, sombra.copy(alpha = 0.3f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            }
            // La raya del lomo de los bayos y los isabelinos.
            if (capa.raya) drawPath(Path().apply { moveTo(a.lomo[0]); for (k in a.lomo.drop(1)) lineTo(k) }, p.medio(capa.crin, lejos).copy(alpha = 0.7f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            // Las motas del tordo: anillos claros con el centro algo más oscuro.
            if (capa.motas && detalle) {
                val az = Azar(Semilla.de("mongol", "tordo", (pie.x * 13f).toInt()))
                drawRect(p.sombra(lerp(pelo, C.HOLLIN, 0.2f), lejos).copy(alpha = 0.25f), Offset(pie.x - 120f * u, a.c(0f, -102f).y), Size(240f * u, 60f * u))
                repeat(if (fino) 40 else 22) { val x = az.entre(-36f, 56f); val c = a.c(x, az.entre(-96f, -60f) + (if (x > 20f) 0f else 6f)); val r = az.entre(3.4f, 5.4f) * u; mancha(c, r, r * 0.85f, p.luz(lerp(pelo, Color.White, 0.45f), lejos), 0.55f) }
            }
            // El pelo de invierno: pinceladas cortas que siguen el cuerpo.
            if (fino) { val az = Azar(Semilla.de("mongol", "pelo", (pie.x * 7f + pie.y).toInt())); repeat(70) { val c = a.c(az.entre(-44f, 58f), az.entre(-98f, -46f)); drawLine((if (it % 3 == 0) luz else sombra).copy(alpha = 0.13f), c, c + Offset(1.2f * u * dir, 2.6f * u), 0.6f * u, StrokeCap.Round) } }
        }
        if (echado) for (pt in cercanas) pata(pt)
        // ---- El cuello y la crin.
        val yCuello = minOf(a.cresta.minOf { it.y }, a.garganta.minOf { it.y }); val yCuello2 = maxOf(a.cresta.maxOf { it.y }, a.garganta.maxOf { it.y })
        vestir(a.cuello, yCuello, yCuello2)
        clipPath(a.cuello) {
            // El hueco de la garganta y el músculo del cuello, con su luz.
            val g = a.garganta; mancha((g[3] + a.cresta[3]) / 2f, 6f * u, 10f * u, luz, 0.3f)
            drawPath(Path().apply { moveTo(g[1]); for (k in g.drop(2)) lineTo(k) }, sombra.copy(alpha = 0.5f), style = Stroke(4f * u))
        }
        // La crin: cae de la cresta por este lado, larga y deshilachada; la del potro, corta y tiesa como un cepillo.
        val cr = a.cresta
        val caida = cr.mapIndexed { k, c0 ->
            val t = k / 10f
            val sigs = cr[minOf(k + 1, 10)] - cr[maxOf(k - 1, 0)]; val n = Offset(sigs.y, -sigs.x) / sigs.getDistance().coerceAtLeast(0.01f)
            val haciaDentro = if ((n.x * (a.garganta[k].x - c0.x) + n.y * (a.garganta[k].y - c0.y)) > 0f) n else n * -1f
            val baja = if (potro) haciaDentro * -0.4f + Offset(0f, -0.2f) else (haciaDentro * 0.55f + Offset(0f, 0.75f))
            val largo = if (potro) 4f else (11f + 6f * sin(PI.toFloat() * t) + 3f * sin(k * 2.3f).let { it * it })
            c0 + baja / baja.getDistance().coerceAtLeast(0.01f) * (largo * u)
        }
        if (cr.size > 2) {
            val masa = Path().apply {
                moveTo(cr[1]); for (k in cr.drop(2)) lineTo(k)
                for (k in caida.indices.reversed()) { if (k < 1) break; lineTo(caida[k]); lineTo((caida[k] + cr[k]) * 0.5f + (caida[k - 1] - cr[k - 1]) * 0.15f) }
                close()
            }
            drawPath(masa, crinCol)
            if (detalle) clipPath(masa) {
                for (k in 1 until cr.size) { val c0 = cr[k]; val c1 = caida[k]; drawLine((if (k % 3 == 0) crinLuz else p.sombra(capa.crin, lejos)).copy(alpha = 0.6f), c0, c1, 0.9f * u, StrokeCap.Round); drawLine(crinLuz.copy(alpha = 0.3f), c0 + (c1 - c0) * 0.1f + Offset(1.2f * u, 0f), c1 + Offset(1.6f * u, 0f), 0.6f * u, StrokeCap.Round) }
            }
            if (cintas) for (k in listOf(3, 6, 9)) drawLine(p.por(C.AZUL, 0.3f, lejos), cr[k], cr[k] + (caida[k] - cr[k]) * 1.3f, 1.8f * u, StrokeCap.Round)
        }
        // ---- De noche, el lomo, la cresta y la grupa con la luz de la luna (antes de la cabeza, que lo tapa si se vuelve).
        val luna = lerp(C.LUNA, Color.White, 0.25f).copy(alpha = (0.55f * noche).coerceIn(0f, 1f))
        if (noche > 0.2f && detalle) clipPath(union(listOf(a.cuerpo, a.cuello))) {
            val lomo = Path().apply { moveTo(a.lomo[0]); for (k in a.lomo.drop(1)) lineTo(k) }; val cresta = Path().apply { moveTo(cr[0]); for (k in cr.drop(1)) lineTo(k) }
            for ((ancho, k) in listOf(7f to 0.35f, 3f to 0.75f)) { drawPath(lomo, luna.copy(alpha = luna.alpha * k), style = Stroke(ancho * u, cap = StrokeCap.Round)); drawPath(cresta, luna.copy(alpha = luna.alpha * k * 0.8f), style = Stroke(ancho * u, cap = StrokeCap.Round)) }
        }
        // ---- La cabeza: la quijada redonda, la cara larga, el morro; el ojo, el ollar, la oreja; el copete.
        val cab = a.cabeza
        val yc0 = listOf(a.h(-3f, -9f), a.h(52f, 2f), a.h(20f, 16f)).minOf { it.y } - 4f * u; val yc1 = listOf(a.h(-3f, -9f), a.h(52f, 2f), a.h(20f, 16f)).maxOf { it.y } + 4f * u
        // La oreja de allá, detrás.
        val orejaFloja = postura == Postura.DESCANSO
        fun oreja(dx: Float, cerca: Boolean) {
            val b0 = a.h(dx, -9.5f); val b1 = a.h(dx + 5f, -10.5f)
            val tip = if (orejaFloja) a.h(dx - 10f, -15f) else a.h(dx - 4f, -23f)
            drawPath(Path().apply { moveTo(b0); cubicTo(a.h(dx - 4f, -14f), (b0 + tip) * 0.5f, tip); cubicTo((b1 + tip) * 0.5f + (b1 - b0) * 0.3f, a.h(dx + 6f, -14f), b1); close() }, if (cerca) medio else sombra)
            if (cerca && detalle) drawLine(p.sombra(capa.crin, lejos).copy(alpha = 0.6f), (b0 + b1) * 0.5f, tip, 0.9f * u, StrokeCap.Round)
        }
        oreja(5f, false)
        vestir(cab, yc0, yc1)
        clipPath(cab) {
            mancha(a.h(14f, 6f), 9f * u, 8f * u, luz, 0.45f)                           // la quijada, redonda
            mancha(a.h(32f, -5f), 12f * u, 3f * u, luz, 0.35f)                          // la caña de la nariz
            mancha(a.h(48f, 3f), 6f * u, 6f * u, p.por(capa.morro, 0f, lejos), 0.75f)   // el morro
            drawPath(Path().apply { moveTo(a.h(6f, 14f)); cubicTo(a.h(14f, 6f), a.h(24f, 6f), a.h(32f, 9f)) }, sombra.copy(alpha = 0.45f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            if (capa.lucero) drawPath(Path().apply { moveTo(a.h(13f, -9f)); cubicTo(a.h(17f, -11.5f), a.h(22f, -10f), a.h(20f, -7.5f)); cubicTo(a.h(26f, -7.5f), a.h(34f, -6f), a.h(40f, -5f)); lineTo(a.h(40f, -3.5f)); cubicTo(a.h(30f, -5f), a.h(20f, -5.5f), a.h(13f, -7f)); close() }, p.por(lerp(C.FIELTRO, pelo, 0.15f), 0.3f, lejos))
        }
        if (alto > 12f) {
            // El ojo, bajo la ceja, con su brillo; el ollar; la comisura.
            val ojo = a.h(16f, -4.5f)
            drawOval(p.medio(lerp(C.HOLLIN, C.CUERO, 0.2f), lejos), Offset(ojo.x - 2.2f * u, ojo.y - 1.5f * u), Size(4.4f * u, 3f * u))
            drawCircle(Color.White.copy(alpha = 0.5f), 0.7f * u, ojo + Offset(-0.8f * u * dir, -0.6f * u))
            if (detalle) drawPath(Path().apply { moveTo(a.h(12f, -7.6f)); cubicTo(a.h(15f, -9.4f), a.h(19f, -9f), a.h(21f, -7f)) }, luz.copy(alpha = 0.6f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(a.h(46f, -2f)); cubicTo(a.h(49f, -2.5f), a.h(50.5f, 0f), a.h(49f, 1.6f)) }, p.medio(C.HOLLIN, lejos), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            if (detalle) drawLine(p.sombra(capa.morro, lejos), a.h(46f, 6.5f), a.h(51f, 5.2f), 0.9f * u, StrokeCap.Round)
        }
        oreja(-1f, true)
        // El copete, que cae sobre la frente.
        val copete = Path().apply { moveTo(a.h(-4f, -10f)); cubicTo(a.h(4f, -14f), a.h(10f, -10f), a.h(16f, -6.5f)); cubicTo(a.h(11f, -6f), a.h(6f, -5f), a.h(2f, -3f)); cubicTo(a.h(1f, -6f), a.h(-2f, -8f), a.h(-4f, -10f)); close() }
        drawPath(copete, crinCol)
        if (detalle) drawLine(crinLuz.copy(alpha = 0.5f), a.h(0f, -10f), a.h(13f, -6.5f), 0.8f * u, StrokeCap.Round)
        if (cintas) drawLine(p.por(C.AZUL, 0.4f, lejos), a.h(1f, -10f), a.h(9f, -2f), 1.6f * u, StrokeCap.Round)
        // El ronzal de crin: el cabezal y la cuerda que cuelga.
        if (detalle && !potro) {
            val ronzal = p.medio(C.CRIN, lejos)
            drawLine(ronzal, a.h(2f, -9f), a.h(8f, 14f), 1.2f * u); drawLine(ronzal, a.h(36f, -6f), a.h(36f, 10f), 1.1f * u); drawLine(ronzal, a.h(8f, 9f), a.h(36f, 6f), 1.1f * u)
            if (postura == Postura.DESCANSO) drawPath(Path().apply { val o = a.h(36f, 10f); moveTo(o); cubicTo(o + Offset(-2f * u * dir, 12f * u), o + Offset(-10f * u * dir, 18f * u), o + Offset(-18f * u * dir, 14f * u)) }, ronzal, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        }
        // ---- La silla de madera, alta delante y detrás, sobre su manta de fieltro, con el estribo corto.
        if (silla && postura != Postura.ECHADO) {
            val manta = Path().apply { moveTo(a.c(-24f, -101f)); cubicTo(a.c(-12f, -97f), a.c(4f, -94f), a.c(14f, -96f)); lineTo(a.c(16f, -78f)); cubicTo(a.c(4f, -76f), a.c(-14f, -78f), a.c(-26f, -82f)); close() }
            drawPath(manta, p.por(C.ROJO, 0.1f, lejos)); drawPath(manta, p.luz(C.ORO, lejos).copy(alpha = 0.55f), style = Stroke(1f * u))
            val asiento = Path().apply { moveTo(a.c(-21f, -101f)); cubicTo(a.c(-24f, -113f), a.c(-20f, -118f), a.c(-16f, -112f)); cubicTo(a.c(-12f, -103f), a.c(0f, -100f), a.c(6f, -104f)); cubicTo(a.c(8f, -114f), a.c(13f, -115f), a.c(13f, -106f)); lineTo(a.c(11f, -96f)); cubicTo(a.c(0f, -94f), a.c(-12f, -96f), a.c(-21f, -101f)); close() }
            drawPath(asiento, Brush.verticalGradient(listOf(p.luz(lerp(C.MADERA, C.ROJO, 0.45f), lejos), p.por(lerp(C.MADERA, C.ROJO, 0.45f), -0.4f, lejos)), a.c(0f, -116f).y, a.c(0f, -96f).y)); drawPath(asiento, p.luz(C.ORO, lejos).copy(alpha = 0.5f), style = Stroke(0.7f * u))
            drawLine(p.medio(C.CRIN, lejos), a.c(-4f, -97f), a.c(-5f, -66f), 0.9f * u); drawPath(poligono(a.c(-9f, -66f), a.c(-1f, -66f), a.c(-2f, -62f), a.c(-8f, -62f)), p.medio(C.HOLLIN, lejos))
            drawLine(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f), lejos), a.c(-8f, -78f), a.c(-6f, -50f), 1.6f * u)
        }
        // ---- De noche: el lomo, la cresta y la grupa con la luz de la luna; un filo cálido del lado de la lumbre.
        if (noche > 0.2f && detalle && !a.alReves) clipPath(cab) { drawPath(Path().apply { moveTo(a.h(-3f, -9f)); cubicTo(a.h(12f, -12f), a.h(30f, -9f), a.h(47f, -4f)) }, luna.copy(alpha = luna.alpha * 0.5f), style = Stroke(3f * u, cap = StrokeCap.Round)) }
        if (p.calida > 0.03f) {
            val desde = lumbreX ?: (pie.x - dir * 200f)
            val haciaLumbre = if (desde < pie.x) -1f else 1f
            val x0 = pie.x - 110f * u; val x1 = pie.x + 110f * u
            val calor = p.lumbre.copy(alpha = (0.55f * p.calida).coerceIn(0f, 1f))
            val formas = union(listOf(a.cuerpo, a.cuello, cab) + cercanas.map { a.formaDe(it) })
            clipPath(formas) { drawRect(Brush.horizontalGradient(if (haciaLumbre < 0f) listOf(calor, calor.copy(alpha = calor.alpha * 0.25f), Color.Transparent) else listOf(Color.Transparent, calor.copy(alpha = calor.alpha * 0.25f), calor), x0, x1), Offset(x0, pie.y - 170f * u), Size(x1 - x0, 175f * u), blendMode = BlendMode.Plus) }
        }
    }

    /** Gira lo que pinta [dibujo] [grados] alrededor de [pivote]. */
    private inline fun DrawScope.withTransformRot(grados: Float, pivote: Offset, dibujo: DrawScope.() -> Unit) {
        if (grados == 0f) dibujo() else rotate(grados, pivote) { dibujo() }
    }

    /** La silueta de un caballo de [caballo], para su sombra arrojada: el cuerpo, el cuello, la cabeza, las patas y la cola. */
    fun siluetaDeCaballo(pie: Offset, alto: Float, mira: Float, pasta: Boolean = false, postura: Postura = if (pasta) Postura.PASTANDO else Postura.DE_PIE, potro: Boolean = false): Path {
        val a = Anatomia(pie, alto, mira, postura, potro)
        return union(listOf(a.cuerpo, a.cuello, a.cabeza, a.cola(0f)) + a.patas.map { a.formaDe(it) })
    }

    /** Dónde queda el hocico de un caballo así (para poner el potro mamando o lo que coma). */
    fun hocicoDe(pie: Offset, alto: Float, mira: Float, postura: Postura = Postura.DE_PIE, potro: Boolean = false): Offset = Anatomia(pie, alto, mira, postura, potro).hocico

    /**
     * Una yegua con su potro mamando: la yegua de pie (o mirando atrás, hacia el potro, con [vuelve]) en [pie], de [alto]
     * hasta la cruz, y el potro del lado de quien mira, vuelto al revés, con el morro en la ubre. Pinta los dos en su orden.
     * Su sombra: [siluetaDeCaballo] de la yegua y la de [potroDe] (que dice dónde pisa el potro).
     */
    fun DrawScope.parejaMamando(p: Pincel, pie: Offset, alto: Float, mira: Float, pelo: Color, peloPotro: Color, lejos: Float = 0f, vuelve: Boolean = true, viento: Float = 0f) {
        caballo(p, pie, alto, mira, pelo, lejos = lejos, postura = if (vuelve) Postura.MIRANDO_ATRAS else Postura.DE_PIE, viento = viento)
        val (pp, ap) = potroDe(pie, alto, mira)
        caballo(p, pp, ap, -mira, peloPotro, potro = true, lejos = lejos, postura = Postura.MAMANDO, viento = viento)
    }

    /** Dónde pisa y cuánto mide el potro que mama de la yegua de [pie], [alto] y [mira]. */
    fun potroDe(pie: Offset, alto: Float, mira: Float): Pair<Offset, Float> {
        val ap = alto * 0.62f
        val yegua = Anatomia(pie, alto, mira, Postura.DE_PIE, false)
        val ubre = yegua.c(30f, -44f)
        val prueba = Anatomia(Offset.Zero, ap, -mira, Postura.MAMANDO, true).hocico
        return Offset(ubre.x - prueba.x, pie.y + alto * 0.05f) to ap
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
        // La crin oscura y, de noche, un filo de luna en el lomo.
        drawLine(p.medio(lerp(pelo, C.HOLLIN, 0.55f), lejos), pie + Offset(3.6f * u * dir, -9.4f * u), cabeza + Offset(-0.6f * u * dir, -0.4f * u), 0.8f * u, StrokeCap.Round)
        if (p.l.noche > 0.3f) drawLine(C.LUNA.copy(alpha = 0.4f * p.l.noche), pie + Offset(-4f * u, -9.6f * u), pie + Offset(4f * u, -9.6f * u), 0.7f * u, StrokeCap.Round)
    }

    // =================================================================
    //  El ovoo, el odre, el hogar y el argal
    // =================================================================

    /**
     * El ovoo: «un montón de piedras con palos y cintas azules» en lo alto del cerro. Pisa en [pie] y mide [alto] hasta
     * la punta de los palos. [cintas]: cuántas hay atadas (crecen cada año); [nueva]: el día del ovoo, las de ese día,
     * vivas y más. [viento] las tiende hacia un lado. [nieve] se queda en las piedras.
     *
     * Las piedras, cada una de su forma, con su cara de luz arriba y del lado del sol y el hueco oscuro entre ellas; el
     * haz de varas de sauce atado en lo alto, con sus ramas secas; las cintas de seda, unas que cuelgan con su peso y
     * otras que el viento levanta y retuerce (se ve su cara y su revés). Al pie, una calavera de caballo **[Aproximación]**.
     */
    fun DrawScope.ovoo(p: Pincel, pie: Offset, alto: Float, cintas: Int, az: Azar, lejos: Float, nieve: Float = 0f, viento: Float = 0.3f, nueva: Boolean = false) {
        val u = alto / 10f
        val monton = alto * 0.48f; val ancho = alto * 0.66f
        val L = if (p.solALaDerecha) 1f else -1f
        val detalle = alto > 16f
        val roca = lerp(C.PIEDRA, C.HOLLIN, 0.12f)
        // ---- El haz de varas: detrás del montón, atado en medio, con las puntas que se abren.
        val cuello = pie + Offset(0f, -monton * 0.92f)
        val atadura = pie + Offset(0f, -alto * 0.63f)
        val varas = listOf(Offset(-0.15f, -10f) to 1.6f, Offset(-1.9f, -8.6f) to 1.1f, Offset(1.7f, -9.2f) to 1.2f, Offset(-0.9f, -9.6f) to 1f, Offset(0.9f, -8.2f) to 1f, Offset(2.6f, -7.6f) to 0.8f, Offset(-2.6f, -7.4f) to 0.8f)
        for ((tip, g) in varas) {
            val b = pie + Offset(tip.x * u, tip.y * u)
            val a = cuello + Offset(tip.x * u * 0.12f, 0f)
            val medio = atadura + Offset(tip.x * u * 0.15f, 0f)
            drawPath(Path().apply { moveTo(a); lineTo(medio); cubicTo(medio + (b - medio) * 0.4f, b + Offset(-tip.x * u * 0.1f, u * 0.6f), b) }, p.medio(C.MADERA_OSC, lejos), style = Stroke((g * 0.26f * u).coerceAtLeast(0.4f), cap = StrokeCap.Round))
            if (detalle) drawLine(p.luz(lerp(C.MADERA, C.FIELTRO, 0.35f), lejos).copy(alpha = 0.6f), medio + Offset(L * g * 0.07f * u, 0f), b + Offset(L * g * 0.05f * u, 0f), (g * 0.08f * u).coerceAtLeast(0.3f), StrokeCap.Round)
            // Las ramitas secas de la punta.
            // (de lejos no: el haz parecería un árbol seco)
            if (alto > 34f) repeat(3) { val t = az.entre(0.55f, 0.92f); val c = medio + (b - medio) * t; drawLine(p.medio(C.MADERA_OSC, lejos), c, c + Offset(az.entre(-1.3f, 1.3f) * u, -az.entre(0.5f, 1.3f) * u), (0.1f * u).coerceAtLeast(0.3f), StrokeCap.Round) } else repeat(3) { az.real() }
        }
        // ---- Las cintas: atadas al haz; las viejas, desteñidas y deshilachadas; las nuevas, de un azul vivo.
        val n = cintas + (if (nueva) 6 else 0)
        class Cinta(val a: Offset, val largo: Float, val ancho: Float, val color: Color, val vuela: Float, val fase: Float)
        val lista = (0 until n).map {
            val (tip, _) = varas[it % varas.size]
            val t = az.entre(0.05f, 0.75f)
            val medio = atadura + Offset(tip.x * u * 0.15f, 0f)
            val a = medio + (pie + Offset(tip.x * u, tip.y * u) - medio) * t
            val nuevaHoy = nueva && it >= n - 6
            val color = when { nuevaHoy -> lerp(C.AZUL, Color.White, 0.08f); it % 6 == 0 -> lerp(C.FIELTRO, C.PIEDRA, 0.15f); it % 4 == 1 -> lerp(C.AZUL, C.FIELTRO, 0.55f); it % 7 == 3 -> lerp(C.ORO, C.FIELTRO, 0.3f); else -> lerp(C.AZUL, C.PIEDRA, az.entre(0.05f, 0.35f)) }
            Cinta(a, az.entre(2f, 4.8f) * u * (if (nuevaHoy) 1.15f else 1f) * (if (alto < 30f) 1.25f else 1f), (az.entre(0.36f, 0.52f) * u).coerceAtLeast(0.8f), if (alto < 30f) lerp(color, C.AZUL, 0.35f) else color, (0.12f + viento * az.entre(0.6f, 1.3f) + (if (nuevaHoy) 0.25f else 0f)).coerceIn(0f, 1.2f), az.entre(0f, 6.28f))
        }
        fun DrawScope.cinta(c: Cinta) {
            // La línea de la cinta: cae con su peso y el viento la tiende y la ondula; se retuerce y enseña el revés.
            val pasos = if (detalle) 14 else 7
            val sign = if (viento >= 0f) 1f else -1f
            val pts = (0..pasos).map { k -> val t = k / pasos.toFloat(); c.a + Offset(sign * c.vuela * c.largo * (t * 0.9f) + sin(t * 5f + c.fase) * c.largo * 0.07f * (0.3f + c.vuela), c.largo * t * (1f - 0.75f * c.vuela) + sin(t * 3.4f + c.fase) * c.largo * 0.05f * c.vuela) }
            val giro = (0..pasos).map { k -> cos(k / pasos.toFloat() * 4.2f + c.fase) }
            val anchos = (0..pasos).map { k -> c.ancho * (0.2f + 0.8f * abs(giro[k])) * (1f - 0.35f * k / pasos.toFloat()) }
            val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
            for (k in 0..pasos) { val a = pts[maxOf(k - 1, 0)]; val b = pts[minOf(k + 1, pasos)]; val d = b - a; val nn = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f); izq += pts[k] + nn * anchos[k]; der += pts[k] - nn * anchos[k] }
            drawPath(poligono(izq + der.reversed()), p.por(c.color, 0.35f * L, lejos))
            // El revés, donde la cinta se da la vuelta: más oscuro y frío.
            var k = 0
            while (k < pasos) {
                if (giro[k] >= 0f && giro[k + 1] >= 0f) { k++; continue }
                val k0 = k; while (k < pasos && (giro[k] < 0f || giro[k + 1] < 0f)) k++
                drawPath(poligono(izq.subList(k0, k + 1) + der.subList(k0, k + 1).reversed()), p.por(lerp(c.color, C.SOMBRA, 0.2f), -0.6f, lejos))
            }
            // Los hilos sueltos de la punta.
            if (detalle) { val f = pts.last(); for (j in -1..1) drawLine(p.por(c.color, -0.2f, lejos).copy(alpha = 0.7f), f, f + Offset(j * c.ancho * 0.5f, c.ancho * 1.6f), (0.06f * u).coerceAtLeast(0.25f)) }
        }
        // Las de detrás del haz, más oscuras, primero.
        for ((i, c) in lista.withIndex()) if (i % 3 == 2) cinta(Cinta(c.a, c.largo, c.ancho, lerp(c.color, C.SOMBRA, 0.25f), c.vuela, c.fase))
        // Las guirnaldas: cintas atadas de una vara a otra, que cuelgan en comba con su peso.
        fun DrawScope.guirnalda(a: Offset, b: Offset, comba: Float, color: Color) {
            val m = (a + b) * 0.5f + Offset(viento * comba * 0.3f, comba)
            val arriba = Path().apply { moveTo(a); cubicTo(a + (m - a) * 0.6f + Offset(0f, comba * 0.5f), b + (m - b) * 0.6f + Offset(0f, comba * 0.5f), b) }
            drawPath(arriba, p.por(color, -0.2f, lejos), style = Stroke((0.42f * u).coerceAtLeast(0.6f), cap = StrokeCap.Round))
            drawPath(arriba, p.por(color, 0.5f * L, lejos), style = Stroke((0.2f * u).coerceAtLeast(0.35f), cap = StrokeCap.Round))
        }
        val gz = Azar(Semilla.de("mongol", "ovoo", "guirnaldas"))
        for (j in 0 until (n / 4).coerceAtMost(5)) {
            val (t0, _) = varas[(j * 2 + 1) % varas.size]; val (t1, _) = varas[(j * 2 + 2) % varas.size]
            val ma = atadura + Offset(t0.x * u * 0.15f, 0f); val mb = atadura + Offset(t1.x * u * 0.15f, 0f)
            val ta = gz.entre(0.3f, 0.7f); val tb = gz.entre(0.3f, 0.7f)
            guirnalda(ma + (pie + Offset(t0.x * u, t0.y * u) - ma) * ta, mb + (pie + Offset(t1.x * u, t1.y * u) - mb) * tb, gz.entre(0.8f, 1.8f) * u, if (j % 2 == 0) lerp(C.AZUL, C.PIEDRA, 0.15f) else lerp(C.AZUL, C.FIELTRO, 0.4f))
        }
        // ---- El montón de piedras: el hueco oscuro de fondo y las piedras, de abajo arriba, cada una de su forma.
        val forma = Path().apply {
            moveTo(pie + Offset(-ancho / 2f, 0.4f * u)); cubicTo(pie + Offset(-ancho * 0.46f, -monton * 0.45f), pie + Offset(-ancho * 0.2f, -monton * 0.98f), pie + Offset(0f, -monton))
            cubicTo(pie + Offset(ancho * 0.22f, -monton * 0.98f), pie + Offset(ancho * 0.46f, -monton * 0.45f), pie + Offset(ancho / 2f, 0.4f * u)); close()
        }
        drawPath(forma, p.sombra(lerp(roca, C.HOLLIN, 0.35f), lejos))
        val filas = if (detalle) 7 else 4
        clipPath(forma) {
            for (f in 0 until filas) {
                val v = (f + 0.5f) / filas
                val y = pie.y - monton * v; val w = ancho * 0.5f * sqrt((1f - v * v * 0.9f).coerceAtLeast(0.05f)) * (1f - 0.25f * v)
                val r = (ancho / (if (detalle) 10f else 6f)) * (1.15f - 0.4f * v)
                var x = pie.x - w - r * 0.3f
                while (x < pie.x + w + r * 0.3f) {
                    val rr = r * az.entre(0.6f, 1.3f) * (if (f == 0 && az.entero(3) == 0) 1.35f else 1f)
                    val col = lerp(roca, when (az.entero(4)) { 0 -> C.TIERRA; 1 -> C.FIELTRO; 2 -> C.HOLLIN; else -> C.PIEDRA }, az.entre(0.05f, 0.3f))
                    val ladoSol = ((x - pie.x) / (ancho / 2f)) * L
                    // Una piedra: un polígono de seis lados irregular, más ancho que alto.
                    val yy = y + az.entre(-0.3f, 0.3f) * rr
                    val pts = (0 until 6).map { k -> val a = (2 * PI * k / 6).toFloat() + az.entre(-0.4f, 0.4f); Offset(x + cos(a) * rr * az.entre(0.7f, 1.15f), yy + sin(a) * rr * 0.6f * az.entre(0.7f, 1.15f)) }
                    drawPath(poligono(pts), p.por(col, -0.35f + 0.4f * ladoSol, lejos))
                    // La cara de arriba, que recibe la luz.
                    val arriba = pts.map { q -> q + (Offset(x, yy - rr * 0.18f) - q) * 0.38f + Offset(L * rr * 0.1f, -rr * 0.1f) }.filterIndexed { k, _ -> k != 1 && k != 2 }
                    drawPath(poligono(arriba), p.por(col, 0.35f + 0.45f * ladoSol, lejos))
                    if (nieve > 0.1f) drawOval(p.por(C.NIEVE, 0.5f + 0.3f * ladoSol, lejos).copy(alpha = nieve), Offset(x - rr * 0.75f, y - rr * 0.7f), Size(rr * 1.5f, rr * 0.45f))
                    x += rr * az.entre(1.3f, 1.9f)
                }
            }
            // La sombra del lado contrario al sol, sobre todo el montón.
            drawRect(Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.35f), Color.Transparent) else listOf(Color.Transparent, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.35f)), pie.x - ancho / 2f, pie.x + ancho / 2f), Offset(pie.x - ancho / 2f, pie.y - monton), Size(ancho, monton + u))
        }
        // Unas piedras sueltas al pie, que rompen el borde.
        repeat(if (detalle) 5 else 2) { val x = pie.x + az.entre(-0.62f, 0.62f) * ancho; val rr = ancho * az.entre(0.035f, 0.06f); drawOval(p.por(roca, -0.4f, lejos), Offset(x - rr, pie.y - rr * 0.5f), Size(rr * 2f, rr * 1.1f)); drawOval(p.por(roca, 0.5f, lejos), Offset(x - rr * 0.7f, pie.y - rr * 0.6f), Size(rr * 1.4f, rr * 0.5f)) }
        // ---- La atadura del haz, con las vueltas de cuerda y de seda, y las cintas de delante, encima.
        drawLine(p.medio(C.CRIN, lejos), atadura + Offset(-0.9f * u, -0.3f * u), atadura + Offset(0.9f * u, 0.3f * u), (0.35f * u).coerceAtLeast(0.5f), StrokeCap.Round)
        drawLine(p.por(C.AZUL, 0.3f, lejos), atadura + Offset(-0.8f * u, 0.4f * u), atadura + Offset(0.8f * u, 0.7f * u), (0.3f * u).coerceAtLeast(0.4f), StrokeCap.Round)
        for ((i, c) in lista.withIndex()) if (i % 3 != 2) cinta(c)
        // Una ofrenda al pie: una calavera de caballo, blanca **[Aproximación]**.
        if (alto > 30f) {
            val k = ancho / 30f; val c = pie + Offset(ancho * 0.3f, -0.6f * k)
            drawPath(Path().apply { moveTo(c + Offset(-1.6f * k, 0f)); cubicTo(c + Offset(-1.5f * k, -1.2f * k), c + Offset(0.4f * k, -1.3f * k), c + Offset(1.8f * k, -0.5f * k)); lineTo(c + Offset(1.9f * k, 0.3f * k)); cubicTo(c + Offset(0.4f * k, 0.5f * k), c + Offset(-1.2f * k, 0.5f * k), c + Offset(-1.6f * k, 0f)); close() }, p.por(lerp(C.FIELTRO, C.ESTEPA, 0.2f), 0.4f * L, lejos))
            drawCircle(p.sombra(C.HOLLIN, lejos), 0.3f * k, c + Offset(-0.6f * k, -0.45f * k))
        }
    }

    /** La silueta del ovoo, para su sombra. */
    fun siluetaDeOvoo(pie: Offset, alto: Float): Path = poligono(listOf(pie + Offset(-alto * 0.33f, 0f), pie + Offset(-alto * 0.16f, -alto * 0.44f), pie + Offset(-alto * 0.2f, -alto * 0.8f), pie + Offset(0f, -alto), pie + Offset(alto * 0.2f, -alto * 0.8f), pie + Offset(alto * 0.16f, -alto * 0.44f), pie + Offset(alto * 0.33f, 0f)))

    /**
     * El odre grande de cuero para el airag, colgado de su armazón: dos horcones y un travesaño. La bolsa, de piel de
     * vaca, cosida con tiento por el lomo y por abajo, hinchada de leche y más ancha abajo; arriba se frunce en la boca,
     * atada con una cuerda; brilla de grasa donde se toca y está oscura de mojada abajo. Por la boca sale el palo de
     * batir; la leche que rebosa ha dejado chorreras y una mancha en el suelo. Al pie, un cubo de cuero y uno de madera.
     * [alto]: hasta el travesaño. [batiendo], de 0 a 1: el palo, más o menos hundido.
     */
    fun DrawScope.odre(p: Pincel, pie: Offset, alto: Float, az: Azar, lejos: Float = 0f, batiendo: Float = 0.5f) {
        val u = alto / 100f; fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        val madera = lerp(C.MADERA, C.TIERRA, 0.25f)
        val L = if (p.solALaDerecha) 1f else -1f
        val detalle = alto > 30f
        // ---- La mancha de airag en el suelo, bajo la bolsa.
        mancha(q(2f, -1f), 30f * u, 4f * u, p.sombra(lerp(C.TIERRA, C.HOLLIN, 0.3f), lejos), 0.5f)
        drawOval(p.por(lerp(C.LANA, C.ESTEPA, 0.2f), 0.2f, lejos).copy(alpha = 0.7f), Offset(q(-6f, 0f).x, q(0f, -1.6f).y), Size(14f * u, 2.4f * u))
        // ---- El armazón: dos horcones clavados, con su corteza, y el travesaño atado.
        fun horcon(x: Float, inclina: Float) {
            val a = q(x, 0f); val b = q(x + inclina, -100f)
            drawLine(p.sombra(madera, lejos), a, b, 4.4f * u, StrokeCap.Round); drawLine(p.luz(madera, lejos), a + Offset(L * 1f * u, 0f), b + Offset(L * 1f * u, 0f), 1.5f * u, StrokeCap.Round)
            if (detalle) for (k in 0 until 5) { val y = -15f - k * 18f + az.entre(-4f, 4f); drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.45f), q(x + inclina * (-y / 100f) - 1.6f, y), q(x + inclina * (-y / 100f) + 1.2f, y + 1f), 0.5f * u) }
            drawLine(p.medio(madera, lejos), b, b + Offset(-5f * u, -9f * u), 2.6f * u, StrokeCap.Round); drawLine(p.medio(madera, lejos), b, b + Offset(5f * u, -8f * u), 2.6f * u, StrokeCap.Round)
        }
        horcon(-38f, 3f); horcon(38f, -3f)
        val viga = listOf(q(-47f, -102f), q(47f, -101f))
        drawLine(p.sombra(madera, lejos), viga[0], viga[1], 4.6f * u, StrokeCap.Round); drawLine(p.luz(madera, lejos), viga[0] + Offset(0f, -1.3f * u), viga[1] + Offset(0f, -1.3f * u), 1.6f * u, StrokeCap.Round)
        for (x in listOf(-35f, 35f)) for (k in 0..2) drawLine(p.medio(C.CRIN, lejos), q(x - 3f, -104f + k * 2f), q(x + 3f, -101f + k * 2f), 1f * u)
        // Las correas de las que cuelga la bolsa.
        for (x in listOf(-14f, 13f)) { drawLine(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f), lejos), q(x * 1.25f, -101f), q(x * 0.9f, -80f), 1.6f * u); drawCircle(p.medio(C.CRIN, lejos), 1.4f * u, q(x * 1.25f, -101f), style = Stroke(0.7f * u)) }
        // ---- La bolsa.
        val cuero = lerp(C.CUERO, C.HOLLIN, 0.12f)
        val bolsa = Path().apply {
            moveTo(q(-9f, -86f)); cubicTo(q(-13f, -82f), q(-20f, -76f), q(-25f, -64f)); cubicTo(q(-33f, -48f), q(-40f, -30f), q(-35f, -15f))
            cubicTo(q(-30f, -5f), q(-10f, -3f), q(4f, -3.5f)); cubicTo(q(20f, -4f), q(36f, -7f), q(37f, -20f)); cubicTo(q(38f, -36f), q(30f, -54f), q(22f, -68f))
            cubicTo(q(17f, -78f), q(12f, -83f), q(8f, -86f)); close()
        }
        drawPath(bolsa, p.por(cuero, -0.15f, lejos))
        clipPath(bolsa) {
            // El bulto: la luz de un lado, la sombra del otro y de abajo, que además está mojado y más oscuro.
            drawRect(Brush.horizontalGradient(if (L > 0f) listOf(p.sombra(cuero, lejos), Color.Transparent, p.luz(lerp(cuero, C.ORO, 0.35f), lejos).copy(alpha = 0.75f)) else listOf(p.luz(lerp(cuero, C.ORO, 0.35f), lejos).copy(alpha = 0.75f), Color.Transparent, p.sombra(cuero, lejos)), q(-36f, 0f).x, q(34f, 0f).x), q(-40f, -90f), Size(80f * u, 86f * u))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.55f)), q(0f, -38f).y, q(0f, -6f).y), q(-40f, -38f), Size(80f * u, 34f * u))
            // Las arrugas del cuero bajo la boca, donde se frunce.
            if (detalle) for (k in -3..3) drawPath(Path().apply { moveTo(q(k * 2.6f, -84f)); cubicTo(q(k * 4.4f, -78f), q(k * 6f, -72f), q(k * 6.6f + 1f, -64f)) }, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.35f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            // El brillo de la grasa: ancho y blando, del lado del sol; las manos lo han dejado más claro a media altura.
            mancha(q(L * 15f, -50f), 9f * u, 16f * u, Color.White, 0.22f * (1f - 0.7f * p.l.noche))
            mancha(q(L * 18f, -52f), 3f * u, 7f * u, Color.White, 0.35f * (1f - 0.7f * p.l.noche))
            mancha(q(-L * 6f, -42f), 12f * u, 9f * u, p.sombra(lerp(cuero, C.HOLLIN, 0.4f), lejos), 0.3f)
            // Un remiendo cosido.
            if (detalle) { val r = poligono(listOf(q(-22f, -40f), q(-12f, -42f), q(-11f, -30f), q(-21f, -28f))); drawPath(r, p.por(lerp(cuero, C.TIERRA, 0.3f), -0.1f * L, lejos)); drawPath(r, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.6f), style = Stroke(0.6f * u)) }
            // Las chorreras de leche que rebosó, secas, que bajan de la boca.
            for ((x, l) in listOf(-4f to 30f, 3f to 18f, 7f to 42f)) drawPath(Path().apply { moveTo(q(x, -84f)); cubicTo(q(x - 1f, -74f), q(x + 1.2f, -66f), q(x + 0.4f, -84f + l)) }, p.luz(C.LANA, lejos).copy(alpha = 0.55f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        // La costura del lomo y la de abajo: tiento crudo en zigzag.
        if (detalle) {
            val lomo = (0..14).map { val t = it / 14f; q(-2f + 6f * t + 3f * sin(t * 3f), -84f + 76f * t) }
            drawPath(Path().apply { moveTo(lomo[0]); for (k in lomo.drop(1)) lineTo(k) }, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.6f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            for (k in lomo.indices) drawLine(p.luz(lerp(C.CUERO, C.FIELTRO, 0.45f), lejos).copy(alpha = 0.75f), lomo[k] + Offset(-1.8f * u, -0.6f * u), lomo[k] + Offset(1.8f * u, 0.6f * u), 0.6f * u)
            drawPath(Path().apply { moveTo(q(-26f, -16f)); cubicTo(q(-12f, -10f), q(12f, -10f), q(30f, -16f)) }, p.sombra(C.HOLLIN, lejos).copy(alpha = 0.5f), style = Stroke(1f * u))
        }
        // ---- La boca, fruncida y atada con varias vueltas de cuerda.
        drawPath(Path().apply { moveTo(q(-9f, -86f)); cubicTo(q(-8f, -92f), q(-6f, -95f), q(-4f, -95f)); lineTo(q(5f, -95f)); cubicTo(q(7f, -95f), q(8f, -92f), q(8f, -86f)); close() }, p.por(lerp(cuero, C.HOLLIN, 0.25f), -0.1f * L, lejos))
        for (k in 0..2) drawLine(p.medio(C.CRIN, lejos), q(-8f, -88f - k * 2f), q(8f, -87.5f - k * 2f), 1.2f * u, StrokeCap.Round)
        // ---- El palo de batir, que sale por la boca, gastado y claro donde se agarra.
        val hundido = 10f * batiendo
        val a = q(1f, -88f + hundido); val b = q(16f, -152f + hundido)
        drawLine(p.sombra(madera, lejos), a, b, 2.8f * u, StrokeCap.Round); drawLine(p.luz(lerp(madera, C.FIELTRO, 0.35f), lejos), a + Offset(L * 0.7f * u, 0f), b + Offset(L * 0.7f * u, 0f), 1f * u, StrokeCap.Round)
        drawLine(p.luz(lerp(madera, C.FIELTRO, 0.55f), lejos).copy(alpha = 0.7f), a + (b - a) * 0.6f, a + (b - a) * 0.85f, 2.4f * u, StrokeCap.Round)
        // ---- Los cubos al pie: uno de cuero cosido, con leche, y uno de madera con sus aros.
        val cubo = Path().apply { moveTo(q(-60f, -17f)); lineTo(q(-41f, -17f)); lineTo(q(-43f, 0f)); lineTo(q(-58f, 0f)); close() }
        drawPath(cubo, Brush.horizontalGradient(listOf(p.por(C.CUERO, -0.6f * L, lejos), p.por(C.CUERO, 0.5f * L, lejos)), q(-60f, 0f).x, q(-41f, 0f).x))
        if (detalle) drawLine(p.sombra(C.HOLLIN, lejos).copy(alpha = 0.6f), q(-51f, -17f), q(-50.5f, 0f), 0.8f * u)
        drawOval(p.medio(C.HOLLIN, lejos), q(-60f, -19f), Size(19f * u, 4f * u)); drawOval(p.luz(C.LANA, lejos).copy(alpha = 0.85f), q(-58f, -18.4f), Size(15f * u, 2.6f * u))
        val cubo2 = Path().apply { moveTo(q(42f, -21f)); lineTo(q(60f, -21f)); lineTo(q(58f, 0f)); lineTo(q(44f, 0f)); close() }
        drawPath(cubo2, Brush.horizontalGradient(listOf(p.por(madera, -0.6f * L, lejos), p.por(madera, 0.6f * L, lejos)), q(42f, 0f).x, q(60f, 0f).x))
        for (y in listOf(-17f, -5f)) drawLine(p.medio(C.HOLLIN, lejos), q(43f, y), q(59f, y), 1.1f * u)
        drawOval(p.sombra(lerp(madera, C.HOLLIN, 0.4f), lejos), q(42f, -23f), Size(18f * u, 4f * u))
        // De noche, con la puerta abierta, la lumbre le saca un filo a la bolsa del lado de la tienda.
        if (p.calida > 0.05f) clipPath(bolsa) { drawRect(Brush.horizontalGradient(listOf(Color.Transparent, p.lumbre.copy(alpha = (0.5f * p.calida).coerceIn(0f, 1f))), q(10f, 0f).x, q(34f, 0f).x), q(-40f, -90f), Size(80f * u, 86f * u), blendMode = BlendMode.Plus) }
    }

    /** La silueta del odre con su armazón, para su sombra. */
    fun siluetaDeOdre(pie: Offset, alto: Float): Path { val u = alto / 100f; return Path().apply { addRect(Rect(pie.x - 40f * u, pie.y - 104f * u, pie.x + 40f * u, pie.y - 98f * u)); addRect(Rect(pie.x - 40f * u, pie.y - 100f * u, pie.x - 36f * u, pie.y)); addRect(Rect(pie.x + 36f * u, pie.y - 100f * u, pie.x + 40f * u, pie.y)); addOval(Rect(pie.x - 34f * u, pie.y - 88f * u, pie.x + 34f * u, pie.y - 6f * u)) } }

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

    // =================================================================
    //  Los yaks y los bueyes, los camellos lejanos y los perros (piezas nuevas de S12)
    // =================================================================

    /** Los pelos de los yaks y los bueyes: el yak negro, el pío (negro con la cara y el vientre blancos), el buey pardo. */
    val YAK = lerp(C.HOLLIN, C.CUERO, 0.28f)
    val YAK_PIO = lerp(C.HOLLIN, C.CUERO, 0.2f)
    val BUEY = lerp(C.CUERO, C.ESTEPA, 0.25f)
    /** El camello de dos jorobas, del color de la estepa seca. */
    val CAMELLO = lerp(C.ESTEPA, C.CUERO, 0.35f)
    /** El perro grande de los campamentos: negro, con las cejas y las patas color fuego. */
    val PERRO = lerp(C.HOLLIN, C.CUERO, 0.15f)

    /**
     * Un yak (o, con [yak] en false, un buey de los que tiran de las carretas) de lado, mirando a la izquierda si [mira]
     * es −1. [alto]: unidades hasta lo alto de la cruz. El yak, con su joroba, la falda de pelo largo que le cuelga
     * casi hasta el suelo, la cola de penacho y los cuernos que se abren hacia arriba; el buey, de pelo corto y más bajo
     * de cruz. [pasta]: con la cabeza en la hierba. [pio]: negro con la cara y la falda blancas. Lleva su sombra de
     * contacto; la arrojada, con [siluetaDeBovino].
     */
    fun DrawScope.bovino(p: Pincel, pie: Offset, alto: Float, mira: Float, pelo: Color, yak: Boolean = true, pasta: Boolean = false, pio: Boolean = false, lejos: Float = 0f) {
        val u = alto / 100f; val dir = if (mira >= 0f) -1f else 1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        val detalle = alto > 16f
        val luz = p.luz(lerp(pelo, C.FIELTRO, 0.25f), lejos); val medio = p.por(pelo, 0f, lejos); val sombra = p.sombra(pelo, lejos)
        val claro = lerp(C.FIELTRO, C.PIEDRA, 0.2f)
        drawOval(p.sombra(C.TIERRA, lejos).copy(alpha = 0.4f), Offset(minOf(q(-50f, 0f).x, q(55f, 0f).x), pie.y - 3f * u), Size(105f * u, 6f * u))
        // Las patas: cortas y gruesas; las de allá, en sombra.
        for ((x, lejana) in listOf(-30f to true, 36f to true, -38f to false, 44f to false)) {
            val c = if (lejana) p.sombra(lerp(pelo, C.HOLLIN, 0.3f), lejos) else medio
            val atras = x > 0f
            val pts = if (atras) listOf(q(x - 2f, -52f), q(x + 4f, -30f), q(x + 2f, -12f), q(x + 1f, -3f)) else listOf(q(x, -52f), q(x - 1f, -26f), q(x - 1f, -12f), q(x - 2f, -3f))
            val an = listOf(11f, 6.5f, 4.4f, 4.6f).map { it * u }
            val izq = pts.indices.map { i -> val d = pts[minOf(i + 1, 3)] - pts[maxOf(i - 1, 0)]; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f); pts[i] + n * an[i] }
            val der = pts.indices.map { i -> val d = pts[minOf(i + 1, 3)] - pts[maxOf(i - 1, 0)]; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f); pts[i] - n * an[i] }
            drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(izq + der.reversed() + izq.take(1)).apply { close() }, c)
            if (!lejana && detalle) drawLine(luz.copy(alpha = 0.45f), pts[1] + Offset((if (p.solALaDerecha) 1f else -1f) * 3f * u, 0f), pts[2] + Offset((if (p.solALaDerecha) 1f else -1f) * 2.4f * u, 0f), 1.2f * u, StrokeCap.Round)
            drawPath(poligono(q(x - 4.4f, -4.5f), q(x + 3.6f, -4.5f), q(x + 4.6f, 0.5f), q(x - 5.4f, 0.5f)), p.medio(lerp(C.HOLLIN, C.TIERRA, 0.2f), lejos))
        }
        // La cola: un penacho de pelo que cuelga (la del buey, una cola fina con su borla).
        if (yak) drawPath(Path().apply { moveTo(q(50f, -80f)); cubicTo(q(60f, -70f), q(64f, -40f), q(60f, -16f)); cubicTo(q(57f, -10f), q(50f, -10f), q(50f, -18f)); cubicTo(q(52f, -40f), q(50f, -64f), q(46f, -78f)); close() }, p.medio(lerp(pelo, C.HOLLIN, 0.3f), lejos))
        else { drawLine(medio, q(50f, -78f), q(54f, -30f), 2f * u, StrokeCap.Round); drawOval(p.medio(lerp(pelo, C.HOLLIN, 0.4f), lejos), q(51f, -32f).let { Offset(it.x - 3f * u, it.y) }, Size(6f * u, 10f * u)) }
        // El cuerpo: la joroba de la cruz, el lomo que baja a la grupa; el yak, con su falda de pelo largo abajo.
        val cuerpo = Path().apply {
            moveTo(q(-46f, -70f)); cubicTo(q(-42f, -92f), q(-26f, if (yak) -104f else -98f), q(-10f, -98f)); cubicTo(q(10f, -92f), q(34f, -88f), q(48f, -84f))
            cubicTo(q(58f, -80f), q(58f, -58f), q(52f, -46f))
            if (yak) { lineTo(q(54f, -22f)); for (k in 0..12) { val x = 54f - k * 8.4f; lineTo(q(x, if (k % 2 == 0) -16f else -21f)) }; lineTo(q(-50f, -26f)) }
            else { cubicTo(q(40f, -42f), q(0f, -38f), q(-30f, -44f)); cubicTo(q(-42f, -48f), q(-48f, -56f), q(-46f, -70f)) }
            close()
        }
        drawPath(cuerpo, medio)
        clipPath(cuerpo) {
            drawRect(Brush.verticalGradient(0f to luz, 0.35f to luz.copy(alpha = 0f), 0.65f to sombra.copy(alpha = 0f), 1f to sombra, startY = q(0f, -104f).y, endY = pie.y), Offset(pie.x - 70f * u, q(0f, -106f).y), Size(140f * u, 106f * u))
            drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(sombra.copy(alpha = 0.5f), Color.Transparent, luz.copy(alpha = 0.2f)) else listOf(luz.copy(alpha = 0.2f), Color.Transparent, sombra.copy(alpha = 0.5f)), pie.x - 60f * u, pie.x + 60f * u), Offset(pie.x - 70f * u, q(0f, -106f).y), Size(140f * u, 106f * u))
            if (pio) drawPath(Path().apply { moveTo(q(-50f, -40f)); cubicTo(q(-20f, -48f), q(20f, -44f), q(56f, -40f)); lineTo(q(56f, -10f)); lineTo(q(-50f, -10f)); close() }, p.por(claro, 0f, lejos))
            if (detalle) {
                // El pelo: largo y en mechones que caen en la falda; corto y ondulado en el lomo.
                val az = Azar(Semilla.de("mongol", "yak", (pie.x * 11f).toInt()))
                repeat(if (yak) 40 else 16) { val x = az.entre(-48f, 54f); val y = az.entre(-96f, -30f); val largo = if (yak && y > -60f) az.entre(8f, 18f) else az.entre(3f, 6f); drawLine((if (it % 3 == 0) luz else sombra).copy(alpha = 0.35f), q(x, y), q(x + 0.6f, y + largo), 1.1f * u, StrokeCap.Round) }
                mancha(q(-28f, -88f), 18f * u, 9f * u, luz, 0.35f)
            }
        }
        // La cabeza: baja y adelantada; al pastar, en la hierba. La frente ancha, el morro, la oreja y los cuernos.
        val nuca = if (pasta) q(-52f, -48f) else q(-52f, if (yak) -70f else -76f)
        val morro = if (pasta) q(-70f, -4f) else q(-78f, if (yak) -44f else -54f)
        val d = (morro - nuca); val l = d.getDistance().coerceAtLeast(0.01f); val dn = d / l; val nn = Offset(dn.y, -dn.x).let { if (it.y > 0f) it else it * -1f }
        val cabeza = Path().apply {
            moveTo(nuca - nn * (10f * u)); cubicTo(nuca + dn * (l * 0.4f) - nn * (11f * u), morro - dn * (l * 0.2f) - nn * (8f * u), morro - nn * (5f * u))
            cubicTo(morro + dn * (3f * u), morro + nn * (4f * u), morro + nn * (6f * u) - dn * (2f * u)); cubicTo(morro - dn * (l * 0.4f) + nn * (9f * u), nuca + nn * (12f * u), nuca + nn * (10f * u)); close()
        }
        if (!pasta) drawPath(poligono(q(-44f, -86f), q(-58f, -72f), q(-56f, -50f), q(-40f, -48f)), medio)
        drawPath(cabeza, Brush.linearGradient(listOf(p.por(if (pio) claro else pelo, 0.5f, lejos), p.por(if (pio) claro else pelo, -0.4f, lejos)), nuca - nn * (10f * u), nuca + nn * (10f * u)))
        if (alto > 10f) {
            val ojo = nuca + dn * (l * 0.32f) - nn * (2f * u)
            drawCircle(p.medio(C.HOLLIN, lejos), 1.6f * u, ojo); drawCircle(Color.White.copy(alpha = 0.4f), 0.5f * u, ojo + Offset(-0.5f * u, -0.5f * u))
            drawLine(p.sombra(C.HOLLIN, lejos), morro - nn * (2f * u), morro - nn * (2f * u) - dn * (3f * u), 1.2f * u, StrokeCap.Round)
            drawLine(p.por(lerp(pelo, C.HOLLIN, 0.2f), -0.3f, lejos), nuca + dn * (4f * u), nuca + dn * (4f * u) + nn * (8f * u) + dn * (-4f * u), 3.6f * u, StrokeCap.Round)
        }
        // Los cuernos: salen de lo alto de la frente, se abren hacia los lados y suben; claros, con la punta oscura.
        val cuerno = lerp(C.FIELTRO, C.PIEDRA, 0.4f)
        for (s in listOf(-1f, 1f)) {
            val r = nuca + dn * (5f * u) - nn * (9f * u) + Offset(s * 2f * u, 0f)
            val punta = r + Offset(s * 14f * u, -6f * u) - nn * (8f * u)
            drawPath(Path().apply { moveTo(r); cubicTo(r + Offset(s * 9f * u, 2f * u), punta + Offset(-s * 2f * u, 6f * u), punta) }, p.por(cuerno, if (s > 0f) 0.4f else -0.3f, lejos), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawCircle(p.medio(C.HOLLIN, lejos), 1.1f * u, punta)
        }
        // De noche, el lomo con un filo de luna.
        if (p.l.noche > 0.3f && detalle) clipPath(cuerpo) { drawPath(Path().apply { moveTo(q(-44f, -86f)); cubicTo(q(-26f, -104f), q(10f, -92f), q(50f, -84f)) }, C.LUNA.copy(alpha = 0.5f * p.l.noche), style = Stroke(4f * u, cap = StrokeCap.Round)) }
    }

    /** La silueta de un yak o de un buey, para su sombra arrojada. */
    fun siluetaDeBovino(pie: Offset, alto: Float, mira: Float): Path {
        val u = alto / 100f; val dir = if (mira >= 0f) -1f else 1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        return poligono(listOf(q(-78f, -44f), q(-52f, -76f), q(-26f, -104f), q(48f, -84f), q(56f, -50f), q(52f, 0f), q(-42f, 0f), q(-50f, -26f)))
    }

    /**
     * Un camello de dos jorobas, lejano: el cuerpo con sus jorobas, el cuello largo en S y las patas largas, de un
     * solo trazo de luz y otro de sombra, y su sombra corta. Pisa en [pie] y mide [alto] hasta lo alto de las jorobas.
     */
    fun DrawScope.camelloLejano(p: Pincel, sol: Sol3, pie: Offset, alto: Float, mira: Float, pelo: Color = CAMELLO, lejos: Float = 0f, echado: Boolean = false) {
        val u = alto / 10f; val dir = if (mira >= 0f) 1f else -1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        if (sol.r.fuerza > 0.05f) { val d = (sol.r.ax * alto * 0.4f).coerceIn(-alto, alto); drawOval(sol.r.color, Offset(pie.x - 6f * u + minOf(0f, d), pie.y - 0.6f * u), Size(12f * u + abs(d), 1.3f * u), alpha = sol.r.fuerza * 0.7f, blendMode = BlendMode.Multiply) }
        val c = p.por(pelo, 0f, lejos); val o = p.sombra(lerp(pelo, C.HOLLIN, 0.3f), lejos); val l = p.luz(pelo, lejos)
        val baja = if (echado) 4.6f else 0f
        if (!echado) for ((x, lejana) in listOf(-3.6f to true, 3f to true, -4.2f to false, 3.6f to false)) drawLine(if (lejana) o else c, q(x, -5.4f), q(x + (if (lejana) 0.3f else 0f), 0f), 0.75f * u, StrokeCap.Round)
        val cuerpo = Path().apply {
            moveTo(q(-5f, -5f + baja)); cubicTo(q(-5.4f, -7f + baja), q(-3.6f, -10f + baja), q(-2f, -10f + baja)); cubicTo(q(-1f, -10f + baja), q(-0.6f, -8.6f + baja), q(0f, -8.4f + baja))
            cubicTo(q(0.8f, -8.6f + baja), q(1.4f, -10f + baja), q(2.6f, -10f + baja)); cubicTo(q(4.2f, -10f + baja), q(5.4f, -7.4f + baja), q(5f, -5f + baja)); cubicTo(q(3f, -4.2f + baja), q(-3f, -4.2f + baja), q(-5f, -5f + baja)); close()
        }
        drawPath(cuerpo, Brush.verticalGradient(listOf(l, c, o), q(0f, -10f + baja).y, q(0f, -4.2f + baja).y))
        drawPath(Path().apply { moveTo(q(-4.4f, -6.4f + baja)); cubicTo(q(-6.4f, -6.6f + baja), q(-7.2f, -8f + baja), q(-7f, -10.2f + baja)); lineTo(q(-8.6f, -10.6f + baja)) }, c, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawLine(o, q(5f, -6.4f + baja), q(5.4f, -4f + baja), 0.4f * u, StrokeCap.Round)
        if (p.l.noche > 0.3f) drawPath(cuerpo, C.LUNA.copy(alpha = 0.35f * p.l.noche), style = Stroke(0.4f * u))
    }

    /**
     * Un perro de los campamentos, grande y peludo: negro, con las cejas, el hocico y las patas color fuego, la cola
     * en penacho enroscada sobre el lomo y las orejas caídas. Pisa en [pie] y mide [alto] hasta la cruz; [echado]: tumbado
     * con la cabeza alta, vigilando. De noche, con un filo de luna.
     */
    fun DrawScope.perro(p: Pincel, pie: Offset, alto: Float, mira: Float, pelo: Color = PERRO, echado: Boolean = false, lejos: Float = 0f) {
        val u = alto / 100f; val dir = if (mira >= 0f) -1f else 1f
        fun q(x: Float, y: Float) = Offset(pie.x + x * u * dir, pie.y + y * u)
        val fuego = lerp(C.CUERO, C.ORO, 0.35f)
        val medio = p.por(pelo, 0f, lejos); val luz = p.luz(lerp(pelo, C.LUNA, 0.25f), lejos); val sombra = p.sombra(pelo, lejos)
        val baja = if (echado) 46f else 0f
        drawOval(p.sombra(C.TIERRA, lejos).copy(alpha = 0.4f), Offset(minOf(q(-50f, 0f).x, q(55f, 0f).x), pie.y - 3f * u), Size(105f * u, 6f * u))
        // Las patas (de pie) o las manos tendidas delante (echado), color fuego abajo.
        if (!echado) for ((x, lejana) in listOf(-26f to true, 30f to true, -32f to false, 36f to false)) {
            val atras = x > 0f
            val col = if (lejana) sombra else medio
            // El muslo ancho (o el brazo), la caña fina, y la pata color fuego con su pelo largo detrás.
            val a = q(x, -60f); val j = if (atras) q(x + 6f, -30f) else q(x - 1f, -28f); val f = q(x - 2f, -3f)
            drawPath(poligono(a + Offset(-7f * u, 0f), a + Offset(7f * u, 0f), j + Offset(3.6f * u, 0f), j + Offset(-3.6f * u, 0f)), col)
            drawLine(col, j, f, 6.4f * u, StrokeCap.Round)
            drawLine(p.por(fuego, if (lejana) -0.6f else 0.1f, lejos), j + (f - j) * 0.45f, f, 6.6f * u, StrokeCap.Round)
            drawOval(p.por(fuego, if (lejana) -0.6f else 0.3f, lejos), Offset(f.x - 5f * u, f.y - 2f * u), Size(9f * u, 4.4f * u))
            for (k in 0..2) drawLine(col, j + Offset(3f * u * dir * (if (atras) -1f else 1f) * -1f, k * 4f * u), j + Offset(7f * u * dir * (if (atras) -1f else 1f) * -1f, k * 4f * u + 6f * u), 1.4f * u, StrokeCap.Round)
        } else for (y in listOf(-6f, -3f)) drawLine(p.por(fuego, if (y < -5f) -0.5f else 0.2f, lejos), q(-30f, y), q(-60f, y + 1f), 8f * u, StrokeCap.Round)
        // La cola: un penacho enroscado sobre el lomo.
        drawPath(Path().apply { moveTo(q(40f, -88f + baja)); cubicTo(q(56f, -104f + baja), q(48f, -126f + baja), q(30f, -114f + baja)); cubicTo(q(40f, -112f + baja), q(44f, -102f + baja), q(34f, -92f + baja)); close() }, medio)
        // El cuerpo, ancho y peludo, con el pecho hondo; el borde, de mechones.
        val cuerpo = Path().apply {
            moveTo(q(-36f, -92f + baja)); cubicTo(q(-20f, -100f + baja), q(20f, -96f + baja), q(44f, -92f + baja)); cubicTo(q(54f, -86f + baja), q(52f, -64f + baja), q(42f, -56f + baja))
            for (k in 0..8) { val x = 40f - k * 9f; lineTo(q(x, (if (k % 2 == 0) -52f else -48f) + baja)) }
            cubicTo(q(-44f, -54f + baja), q(-48f, -74f + baja), q(-36f, -92f + baja)); close()
        }
        drawPath(cuerpo, medio)
        clipPath(cuerpo) {
            drawRect(Brush.verticalGradient(0f to luz, 0.4f to luz.copy(alpha = 0f), 1f to sombra, startY = q(0f, -100f + baja).y, endY = q(0f, -48f + baja).y), Offset(pie.x - 70f * u, q(0f, -102f + baja).y), Size(140f * u, 56f * u))
            mancha(q(-30f, -66f + baja), 10f * u, 8f * u, p.por(fuego, 0f, lejos), 0.35f)
        }
        // La cabeza, ancha, alta y alerta: las cejas color fuego (los «cuatro ojos»), las orejas caídas, el hocico.
        val c = q(-50f, -104f + baja)
        scale(1.3f, 1.3f, c + Offset(6f * u * dir, 4f * u)) {
        val cabeza = Path().apply { moveTo(c + Offset(10f * u * dir, -12f * u)); cubicTo(c + Offset(-4f * u * dir, -16f * u), c + Offset(-16f * u * dir, -12f * u), c + Offset(-26f * u * dir, -2f * u)); cubicTo(c + Offset(-28f * u * dir, 4f * u), c + Offset(-22f * u * dir, 8f * u), c + Offset(-12f * u * dir, 8f * u)); cubicTo(c + Offset(0f, 14f * u), c + Offset(12f * u * dir, 12f * u), c + Offset(14f * u * dir, 2f * u)); close() }
        // La gorguera: el pelo espeso del cuello, que le da su bulto de oso.
        val gorguera = Path().apply { val g0 = q(-40f, -84f + baja); for (k in 0 until 24) { val a = (2 * PI * k / 24).toFloat(); val r = (if (k % 2 == 0) 19f else 17f) * u; val pt = g0 + Offset(cos(a) * r * 0.9f, sin(a) * r); if (k == 0) moveTo(pt) else lineTo(pt) }; close() }
        drawPath(gorguera, Brush.verticalGradient(listOf(luz, medio, sombra), q(0f, -104f + baja).y, q(0f, -66f + baja).y))
        drawPath(cabeza, Brush.verticalGradient(listOf(luz, medio, sombra), c.y - 16f * u, c.y + 12f * u))
        drawPath(Path().apply { moveTo(c + Offset(-14f * u * dir, 0f)); cubicTo(c + Offset(-20f * u * dir, -1f * u), c + Offset(-26f * u * dir, 1f * u), c + Offset(-26f * u * dir, 3f * u)); cubicTo(c + Offset(-22f * u * dir, 8f * u), c + Offset(-14f * u * dir, 8f * u), c + Offset(-12f * u * dir, 5f * u)); close() }, p.por(fuego, -0.1f, lejos))
        drawCircle(p.por(fuego, 0.3f, lejos), 2.2f * u, c + Offset(-8f * u * dir, -8f * u))
        drawCircle(p.medio(C.HOLLIN, lejos), 1.7f * u, c + Offset(-8f * u * dir, -5f * u)); drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, c + Offset(-8.5f * u * dir, -5.6f * u))
        drawCircle(p.medio(C.HOLLIN, lejos), 2f * u, c + Offset(-26f * u * dir, 1.6f * u))
        drawPath(Path().apply { moveTo(c + Offset(4f * u * dir, -12f * u)); cubicTo(c + Offset(10f * u * dir, -11f * u), c + Offset(13f * u * dir, 2f * u), c + Offset(8f * u * dir, 9f * u)); cubicTo(c + Offset(3f * u * dir, 4f * u), c + Offset(0f, -6f * u), c + Offset(4f * u * dir, -12f * u)); close() }, sombra)
        }
        if (p.l.noche > 0.3f) clipPath(cuerpo) { drawPath(Path().apply { moveTo(q(-36f, -92f + baja)); cubicTo(q(-20f, -100f + baja), q(20f, -96f + baja), q(44f, -92f + baja)) }, C.LUNA.copy(alpha = 0.5f * p.l.noche), style = Stroke(4f * u, cap = StrokeCap.Round)) }
    }
}
