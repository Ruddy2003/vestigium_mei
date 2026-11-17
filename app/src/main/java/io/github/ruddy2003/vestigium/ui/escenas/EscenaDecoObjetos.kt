package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Fase
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los doce objetos del día de Lou. Cada uno tiene su sitio en el club: los chicos van sobre la mesa cercana (cortada por
 * el borde de abajo), en su borde de delante; los grandes van en el suelo, al pie de esa mesa. El piano es el piano de
 * siempre: ese día, algo lo señala.
 *
 * Se pintan **en la sala**, como el resto del cuadro: el punto que recibe cada función (y su escala) se vuelve a llevar a
 * la sala con la cámara del club ([CamDeco]) y cada cara toma el color con la luz de ese sitio (`VistaDeco.c`), la
 * misma cuenta que la mesa sobre la que están: la penumbra de color, cada lámpara por su lado (de noche, el lado de las
 * lámparas, cálido, y el otro en el violeta frío del neón y de la calle), el haz de sol de la tarde y el neón. Las sombras
 * cortas las tumba la luz que más alumbra el sitio ([Pincel] del cuadro).
 *
 * - **Grandes** ([GRANDES]): la radio de capilla (de madera, con su rejilla calada de rayos sobre la tela, el dial y tres
 *   botones de baquelita; de noche, encendida, con el dial ámbar) y los zapatos de baile de charol (de tira en T, con su
 *   botón y su forro de raso; los dos de pie, como se los quitó, uno de perfil y el otro hacia el fondo). Van en el suelo,
 *   junto a la mesa, y pintan su sombra.
 * - **Chicos** (sobre la mesa): la partitura (con su portada de abanico de rayos que cae por el borde de la mesa; ese día
 *   el atril del piano está vacío), el cloché de fieltro con su cinta y su broche, la papeleta del empeño (doblada por el
 *   trepado, de pie como una tienda: renglones y un sello morado, sin letras ni cifras), la taza de porcelana con la
 *   ginebra y el carmín en el borde, el disco de pizarra medio fuera de su funda (la etiqueta de color, sin letras), el
 *   collar de perlas de dos vueltas, enroscado y colgando por el borde, el pañuelo blanco arrugado, con una punta caída,
 *   el cigarrillo encendido en su boquilla larga, apoyado en un cenicero de latón, con su hilo de humo, y el metrónomo de
 *   pirámide, abierto, con la aguja a un lado.
 * - **El piano** ([enElPiano]): un ramo de rosas atado con su cinta, dejado sobre la tapa (también sobre la funda).
 *
 * **[Diseño propio]**: dónde está cada uno y cómo (la papeleta doblada de pie, la partitura y el collar por el borde de la
 * mesa, la radio y los zapatos en el suelo, la radio encendida de noche); los colores del cloché (fieltro color vino) y de
 * la etiqueta del disco; el carmín de la taza; la boquilla; el ramo como señal del piano (el cuento no lo nombra).
 */
internal object ObjetosDeco {
    private val C = ColorDeco

    /** Los doce objetos del calendario de Lou (`AnioG6.kt`). */
    val TODOS = listOf("piano", "partitura", "sombrero", "ficha", "taza", "disco", "radio", "collar", "zapatos", "panuelo", "cigarrillo", "metronomo")

    /** Los que no caben sobre la mesa: van en el suelo, a su pie. */
    val GRANDES = setOf("radio", "zapatos")

    /** Unidades del lienzo por metro a escala 1 (las de la mesa cercana: [CamDeco.k] / 120 es su escala). */
    const val METRO = 120f

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val PAPEL = lerp(C.MARFIL, C.ORO, 0.12f)
    private val CARTON = lerp(C.MARFIL, C.ROBLE, 0.28f)
    private val KRAFT = lerp(C.ROBLE, C.MARFIL, 0.42f)
    private val NOGAL = lerp(C.CAOBA, C.ROBLE, 0.32f)
    private val BAQUELITA = lerp(C.CAOBA, C.LACA, 0.62f)
    private val FIELTRO = lerp(C.PARED, C.PANO, 0.42f)
    private val PERLA = lerp(C.MARFIL, C.CROMO, 0.22f)
    private val ALGODON = lerp(C.MARFIL, Color.White, 0.35f)
    private val SELLO = lerp(C.SOMBRA, C.PANO, 0.35f)
    private val ROSA = lerp(C.PARED, C.CORAL, 0.35f)
    private val GINEBRA = lerp(C.ORO, C.MARFIL, 0.35f)
    private val ASCUA = Color(0xFFFF8A3C)                                        // lo que da luz: la brasa del cigarrillo
    private val DIAL = Color(0xFFFFC46E)                                         // lo que da luz: el dial encendido

    /** Si el objeto de hoy es de los grandes (no cabe sobre la mesa y va en el suelo). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /**
     * El objeto chico de hoy, sobre la mesa cercana. No pinta nada si hoy no hay objeto, es de los grandes o es el piano.
     * [sobre] es el punto de la tapa de la mesa donde se apoya; [escala] 1 es el tamaño del primer plano (120 unidades del
     * lienzo por metro, las de la mesa cercana). [p] trae la luz de ese sitio: la lámpara de la mesa, el neón o el sol.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) = chico(VistaDeco(m), m.ficha.objeto, p, sobre, escala)

    /** El objeto chico [id] sobre la mesa, con la luz de [v] (lo mismo que [sobreElSoporte]: para las pruebas). */
    fun DrawScope.chico(v: VistaDeco, id: String?, p: Pincel, sobre: Offset, escala: Float = 1f) {
        if (id == null || id in GRANDES || id !in TODOS || id == "piano") return
        val o = enLaSala(sobre, escala)
        val borde = Borde(v)
        // Un poco mayores que el natural, para que se lean a tamaño de teléfono (como las cosas del primer plano).
        withTransform({ scale(AMPLIA, AMPLIA, sobre) }) { when (id) {
            "taza" -> taza(v, p, Marco3(o, 0.15f))
            "disco" -> disco(v, p, Marco3(o, 0.22f))
            "ficha" -> ficha(v, p, Marco3(o, -0.28f))
            "collar" -> collar(v, p, Marco3(o, 0f), borde)
            "panuelo" -> panuelo(v, p, Marco3(o, 0f), borde)
            "partitura" -> partitura(v, p, Marco3(o, 0f), borde)
            "cigarrillo" -> cigarrillo(v, p, Marco3(o, -0.35f))
            "metronomo" -> metronomo(v, p, Marco3(o, 0.38f))
            "sombrero" -> cloche(v, p, Marco3(o, 0.3f))
        } }
    }

    /** Cuánto mayores que el natural se pintan los chicos **[Diseño propio]**. */
    const val AMPLIA = 1.3f

    /**
     * El objeto grande de hoy: pisa en [pie] (el suelo, junto a la mesa cercana) y se recuesta en [apoyo] (el canto de la
     * mesa). Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = grande(VistaDeco(m), m.ficha.objeto, p, pie, apoyo, escala)

    /** El objeto grande [id] con la luz de [v] (lo mismo que el de arriba: para las pruebas). */
    fun grande(v: VistaDeco, id: String?, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (id == null || id !in GRANDES) return null
        val o = enLaSala(pie, escala).let { V3(it.x, 0f, it.z) }
        // Se gira un poco hacia el apoyo (hacia la mesa), sin pasarse.
        val d = apoyo - pie
        val hacia = if (abs(d.x) < 0.5f) 0f else (atan2(d.x, -d.y) * 0.4f).coerceIn(-0.3f, 0.3f)
        return when (id) {
            "radio" -> { val f = Marco3(o, -0.32f + hacia); val s = siluetaRadio(f); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); contacto(v, f, 0f, 0f, 0.22f, 0.15f); radio(v, f) } }
            "zapatos" -> { val f = Marco3(V3(o.x - 0.14f, 0f, o.z - 0.12f), 0f); val c = CamDeco.p(f.o); val s = siluetaZapatos(f)
                Cosa(s, pie) { withTransform({ scale(AMPLIA, AMPLIA, c) }) { sombraEnElSuelo(p, s, c); zapatos(v, f) } } }
            else -> null
        }
    }

    /**
     * Lo que señala el piano el día en que el objeto es el piano: un ramo de rosas rojas, envuelto en papel y atado con
     * una cinta de raso, dejado sobre la tapa, entre el sitio del gato y la lámpara (sobre la funda, los días que la lleva).
     */
    fun DrawScope.enElPiano(v: VistaDeco, f: Marco3, funda: Boolean = false) {
        if (v.m.ficha.objeto != "piano") return
        ramo(v, Marco3(f.w(0.68f, PiezasDeco.PIANO_ALTO + if (funda) 0.045f else 0.003f, 0.12f), f.giro + 0.1f))
    }

    // =================================================================
    //  Ayudas
    // =================================================================

    /** El punto de la sala que la cámara pone en [s] a la [escala] dada (la escala dice a qué hondo está). */
    private fun enLaSala(s: Offset, escala: Float): V3 {
        val z = CamDeco.F / (METRO * escala.coerceAtLeast(0.05f))
        return V3((s.x - CamDeco.CX) * z / CamDeco.F, CamDeco.OJO - (s.y - CamDeco.CY) * z / CamDeco.F, z)
    }

    /**
     * El borde de delante de la mesa cercana, visto desde el punto del objeto: la tapa es redonda (centro 0.1 m a la
     * izquierda y 0.13 m más al fondo que el punto) y, puesta, el mantel sobresale y cae en su falda.
     */
    private class Borde(v: VistaDeco) {
        val puesta = !v.prov && (v.fase == Fase.ABIERTO || v.fase == Fase.PREPARA || v.fase == Fase.DESPUES)
        val r = PiezasDeco.MESA_RADIO + if (puesta) 0.025f else 0f
        /** El z (local, en las medidas del objeto ampliado [AMPLIA] veces) del borde a la altura [x] (local). */
        fun z(x: Float): Float { val dx = (x * AMPLIA + 0.1f).coerceIn(-r * 0.95f, r * 0.95f); return (0.13f - sqrt(r * r - dx * dx)) / AMPLIA }
    }

    private fun lum(c: Color) = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

    private fun media(ps: List<V3>): V3 = V3(ps.sumOf { it.x.toDouble() }.toFloat() / ps.size, ps.sumOf { it.y.toDouble() }.toFloat() / ps.size, ps.sumOf { it.z.toDouble() }.toFloat() / ps.size)

    /** Una cara plana de [f] con su color a la luz de su sitio. */
    private fun DrawScope.cara(v: VistaDeco, f: Marco3, ps: List<V3>, c: Color, n: V3, alfa: Float = 1f): Path {
        val forma = f.forma(ps); val m = media(ps)
        drawPath(forma, v.c(f.w(m.x, m.y, m.z), c, f.n(n.x, n.y, n.z)).copy(alpha = alfa))
        return forma
    }

    private fun DrawScope.linea(v: VistaDeco, f: Marco3, a: V3, b: V3, c: Color, n: V3, ancho: Float, alfa: Float = 1f) {
        val k = CamDeco.k(f.w(a.x, a.y, a.z).z)
        drawLine(v.c(f.w((a.x + b.x) / 2f, (a.y + b.y) / 2f, (a.z + b.z) / 2f), c, f.n(n.x, n.y, n.z)).copy(alpha = alfa), f.p(a.x, a.y, a.z), f.p(b.x, b.y, b.z), (ancho * k).coerceAtLeast(0.3f), StrokeCap.Round)
    }

    /** Los puntos de un aro horizontal de [f] (centro [cx], [y], [cz]; radios [rx], [rz]) de [a0] a [a1]. */
    private fun aroL(f: Marco3, cx: Float, y: Float, cz: Float, rx: Float, rz: Float, a0: Float, a1: Float, n: Int = 18): List<Offset> =
        (0..n).map { i -> val a = a0 + (a1 - a0) * i / n; f.p(cx + rx * cos(a), y, cz + rz * sin(a)) }

    private val PI_F = PI.toFloat()

    /**
     * Un cuerpo de revolución (una taza, una copa de sombrero, un cenicero) de eje vertical en ([cx], [cz]) de [f]: su
     * [perfil] son pares (alto, radio) de abajo arriba. Lo pinta con el color de cada lado a la luz de la sala y un brillo
     * del lado que más alumbra. Devuelve la silueta.
     */
    private fun DrawScope.torneado(v: VistaDeco, f: Marco3, cx: Float, cz: Float, perfil: List<Pair<Float, Float>>, color: Color, brillo: Float = 0.45f): Path {
        val arriba = perfil.last(); val abajo = perfil.first()
        val pts = ArrayList<Offset>()
        pts += aroL(f, cx, arriba.first, cz, arriba.second, arriba.second, PI_F, 0f)                    // el borde de atrás, de izquierda a derecha
        for (i in perfil.indices.reversed()) pts += f.p(cx + perfil[i].second, perfil[i].first, cz)      // el lado derecho, bajando
        pts += aroL(f, cx, abajo.first, cz, abajo.second, abajo.second, 0f, -PI_F)                       // el de delante, abajo, de derecha a izquierda
        for (i in perfil.indices) pts += f.p(cx - perfil[i].second, perfil[i].first, cz)                 // el izquierdo, subiendo
        val forma = poligono(pts)
        val rmax = perfil.maxOf { it.second }; val ym = (abajo.first + arriba.first) / 2f; val w = f.w(cx, ym, cz)
        val cI = v.c(w, color, f.n(-1f, 0f, -0.35f)); val cD = v.c(w, color, f.n(1f, 0f, -0.35f))
        val x0 = minOf(f.p(cx - rmax, ym, cz).x, f.p(cx + rmax, ym, cz).x); val x1 = maxOf(f.p(cx - rmax, ym, cz).x, f.p(cx + rmax, ym, cz).x)
        drawPath(forma, Brush.horizontalGradient(0f to lerp(cI, C.SOMBRA, 0.12f), 0.3f to v.c(w, color, f.n(-0.4f, 0.1f, -1f)), 0.7f to v.c(w, color, f.n(0.4f, 0.1f, -1f)), 1f to lerp(cD, C.SOMBRA, 0.12f), startX = x0, endX = x1.coerceAtLeast(x0 + 0.5f)))
        // El brillo: una pincelada a lo alto, del lado de la luz.
        val s = if (lum(cI) > lum(cD)) -1f else 1f
        if (brillo > 0f) {
            val k = CamDeco.k(w.z); val camino = Path()
            for ((i, q) in perfil.withIndex()) { val pp = f.p(cx + s * q.second * 0.55f, q.first, cz - q.second * 0.8f); if (i == 0) camino.moveTo(pp.x, pp.y) else camino.lineTo(pp.x, pp.y) }
            clipPath(forma) { drawPath(camino, v.c(w, lerp(color, Color.White, 0.6f), f.n(s, 0.3f, -0.7f)).copy(alpha = brillo), style = Stroke((0.1f * rmax * k).coerceAtLeast(0.35f), cap = StrokeCap.Round)) }
        }
        return forma
    }

    /** La sombra de contacto en la mesa o en el suelo, bajo lo que se apoya, y la corta que tumba la luz que manda. */
    private fun DrawScope.sombrita(v: VistaDeco, p: Pincel, f: Marco3, cx: Float, cz: Float, rx: Float, rz: Float, alto: Float) {
        val forma = poligono(aroL(f, cx, 0.001f, cz, rx * 1.1f, rz * 1.1f, 0f, 2f * PI_F, 20))
        drawPath(forma, lerp(C.SOMBRA, C.LACA, 0.35f).copy(alpha = 0.42f), blendMode = BlendMode.Multiply)
        val r = p.r; if (r.fuerza < 0.05f) return
        val k = CamDeco.k(f.w(cx, 0f, cz).z)
        val d = (r.ax * alto * k * 0.7f).coerceIn(-rx * k * 1.6f, rx * k * 1.6f)
        withTransform({ translate(d * 0.6f, 0f) }) { drawPath(poligono(aroL(f, cx, 0.001f, cz, rx + abs(d) / k * 0.4f, rz * 1.15f, 0f, 2f * PI_F, 20)), C.SOMBRA.copy(alpha = (r.fuerza * 0.55f).coerceIn(0f, 1f)), blendMode = BlendMode.Multiply) }
    }

    /** La sombra de contacto, oscura y apretada, de un objeto grande en el suelo. */
    private fun DrawScope.contacto(v: VistaDeco, f: Marco3, cx: Float, cz: Float, rx: Float, rz: Float) {
        drawPath(poligono(aroL(f, cx, 0.002f, cz, rx, rz, 0f, 2f * PI_F, 20)), lerp(C.SOMBRA, C.LACA, 0.4f).copy(alpha = 0.5f), blendMode = BlendMode.Multiply)
    }

    /** La sombra arrojada de un objeto grande sobre el suelo, desde su [forma] y su [pie] (la misma cuenta que `sombraArrojada`). */
    private fun DrawScope.sombraEnElSuelo(p: Pincel, forma: Path, pie: Offset) {
        val r = p.r; if (r.fuerza < 0.05f) return
        withTransform({ transform(Matrix().apply { values[4] = -r.ax; values[12] = r.ax * pie.y; values[5] = -r.ay; values[13] = pie.y * (1f + r.ay) }) }) {
            drawPath(forma, r.color, alpha = (r.fuerza * 0.62f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
        }
    }

    /** La envolvente convexa de unos puntos del lienzo. */
    private fun envolvente(ps: List<Offset>): Path {
        val o = ps.sortedWith(compareBy({ it.x }, { it.y }))
        fun cruz(a: Offset, b: Offset, c: Offset) = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
        val abajo = ArrayList<Offset>(); for (q in o) { while (abajo.size >= 2 && cruz(abajo[abajo.size - 2], abajo.last(), q) <= 0f) abajo.removeAt(abajo.size - 1); abajo += q }
        val arriba = ArrayList<Offset>(); for (q in o.asReversed()) { while (arriba.size >= 2 && cruz(arriba[arriba.size - 2], arriba.last(), q) <= 0f) arriba.removeAt(arriba.size - 1); arriba += q }
        return poligono(abajo.dropLast(1) + arriba.dropLast(1))
    }

    private val ARRIBA = V3(0f, 1f, 0f)
    private val FRENTE = V3(0f, 0f, -1f)

    // =================================================================
    //  Los chicos
    // =================================================================

    /** La taza de porcelana con su plato (la Ley Seca: la ginebra va en tazas), su cucharilla y el carmín en el borde. */
    private fun DrawScope.taza(v: VistaDeco, p: Pincel, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        sombrita(v, p, f, 0f, 0f, 0.08f, 0.08f, 0.07f)
        // El plato: el canto, la cara de arriba, el fondo hundido y el filete de jade.
        drawPath(poligono(aroL(f, 0f, 0.003f, 0f, 0.078f, 0.078f, 0f, 2f * PI_F, 24)), v.c(f.w(0f, 0f, -0.07f), lerp(C.MARFIL, C.SOMBRA, 0.25f), FRENTE))
        drawPath(poligono(aroL(f, 0f, 0.009f, 0f, 0.076f, 0.076f, 0f, 2f * PI_F, 24)), v.c(f.o, C.MARFIL, ARRIBA))
        drawPath(poligono(aroL(f, 0f, 0.009f, 0f, 0.066f, 0.066f, 0f, 2f * PI_F, 24)), v.c(f.o, C.JADE, ARRIBA), style = Stroke(0.45f))
        drawPath(poligono(aroL(f, 0f, 0.006f, 0f, 0.044f, 0.044f, 0f, 2f * PI_F, 20)), v.c(f.o, lerp(C.MARFIL, C.SOMBRA, 0.2f), ARRIBA))
        // La cucharilla, de latón, en el plato.
        linea(v, f, V3(-0.025f, 0.012f, -0.055f), V3(-0.08f, 0.014f, -0.012f), C.LATON, V3(-0.3f, 1f, -0.5f), 0.006f)
        drawOval(v.c(f.o, lerp(C.LATON, C.MARFIL, 0.3f), ARRIBA), f.p(-0.026f, 0.012f, -0.057f) - Offset(0.014f * k, 0.006f * k), Size(0.028f * k, 0.012f * k))
        // La taza.
        val perfil = listOf(0.009f to 0.026f, 0.014f to 0.034f, 0.03f to 0.045f, 0.055f to 0.052f, 0.07f to 0.055f)
        // El asa, detrás de la derecha (antes del cuerpo: el cuerpo la tapa donde nace).
        val asa = Path().apply { val a = f.p(0.048f, 0.06f, 0f); val b = f.p(0.05f, 0.026f, 0f); moveTo(a.x, a.y)
            val c1 = f.p(0.095f, 0.066f, 0f); val c2 = f.p(0.09f, 0.02f, 0f); cubicTo(c1.x, c1.y, c2.x, c2.y, b.x, b.y) }
        drawPath(asa, v.c(f.w(0.08f, 0.04f, 0f), C.MARFIL, f.n(1f, 0f, -0.3f)), style = Stroke(0.009f * k, cap = StrokeCap.Round))
        drawPath(asa, v.c(f.w(0.08f, 0.04f, 0f), C.ORO, f.n(1f, 0.3f, -0.6f)).copy(alpha = 0.6f), style = Stroke(0.003f * k, cap = StrokeCap.Round))
        val cuerpo = torneado(v, f, 0f, 0f, perfil, C.MARFIL, 0.6f)
        clipPath(cuerpo) {
            // La greca déco: una banda de jade y un filete de oro.
            drawPath(poligono(aroL(f, 0f, 0.05f, 0f, 0.051f, 0.051f, 0f, -PI_F) + aroL(f, 0f, 0.041f, 0f, 0.049f, 0.049f, -PI_F, 0f)), v.c(f.w(0f, 0.045f, -0.05f), C.JADE, FRENTE))
            drawPath(Path().apply { val ps = aroL(f, 0f, 0.06f, 0f, 0.054f, 0.054f, 0f, -PI_F); moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0f, 0.06f, -0.05f), C.ORO, FRENTE), style = Stroke(0.004f * k))
        }
        // La boca: el blanco de dentro y la ginebra (clara, con un poco de oro), y el borde con su luz.
        val boca = poligono(aroL(f, 0f, 0.07f, 0f, 0.055f, 0.055f, 0f, 2f * PI_F, 24))
        drawPath(boca, v.c(f.w(0f, 0.07f, 0.04f), lerp(C.MARFIL, C.SOMBRA, 0.3f), V3(0f, 0.3f, -1f)))
        clipPath(boca) { drawPath(poligono(aroL(f, 0f, 0.056f, -0.004f, 0.05f, 0.05f, 0f, 2f * PI_F, 24)), v.c(f.w(0f, 0.056f, 0f), GINEBRA, ARRIBA).copy(alpha = 0.85f))
            drawOval(Color.White.copy(alpha = 0.18f + 0.15f * v.noche), f.p(-0.02f, 0.056f, -0.01f) - Offset(0.012f * k, 0.003f * k), Size(0.024f * k, 0.006f * k)) }
        drawPath(boca, v.c(f.w(0f, 0.07f, -0.05f), lerp(C.MARFIL, Color.White, 0.3f), ARRIBA), style = Stroke(0.0035f * k))
        // El carmín de Lou en el borde.
        drawPath(Path().apply { val ps = aroL(f, 0f, 0.0705f, 0f, 0.055f, 0.055f, -PI_F * 0.68f, -PI_F * 0.5f, 6); moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(-0.02f, 0.07f, -0.05f), lerp(C.CORAL, C.PARED, 0.4f), FRENTE).copy(alpha = 0.9f), style = Stroke(0.006f * k, cap = StrokeCap.Round))
    }

    /** El disco de pizarra, medio fuera de su funda de papel: los surcos con su brillo en cruz y la etiqueta de color, sin letras. */
    private fun DrawScope.disco(v: VistaDeco, p: Pincel, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        // La funda, de papel de estraza, con su agujero redondo y una esquina rota.
        val fx = -0.12f; val fz = 0.07f; val h = 0.135f
        sombrita(v, p, f, -0.07f, 0.03f, 0.17f, 0.13f, 0.01f)
        cara(v, f, listOf(V3(fx - h, 0.001f, fz - h), V3(fx + h, 0.001f, fz - h), V3(fx + h, 0.001f, fz + h), V3(fx - h + 0.03f, 0.001f, fz + h), V3(fx - h, 0.001f, fz + h - 0.025f)), KRAFT, ARRIBA)
        drawPath(f.forma(listOf(V3(fx - h, 0.001f, fz - h), V3(fx + h, 0.001f, fz - h))), v.c(f.w(fx, 0f, fz - h), lerp(KRAFT, C.MARFIL, 0.4f), V3(0f, 0.6f, -1f)), style = Stroke(0.5f))
        drawPath(poligono(aroL(f, fx, 0.0015f, fz, 0.048f, 0.048f, 0f, 2f * PI_F)), v.c(f.w(fx, 0f, fz), lerp(KRAFT, C.LACA, 0.5f), ARRIBA))
        // El disco: el canto, la cara negra, los surcos.
        val cx = -0.025f; val cz = -0.005f; val y = 0.006f; val r = 0.127f
        drawPath(poligono(aroL(f, cx, y - 0.004f, cz, r, r, 0f, 2f * PI_F, 32)), v.c(f.w(cx, 0f, cz - r), lerp(C.LACA, C.CAOBA, 0.3f), FRENTE))
        val cara = poligono(aroL(f, cx, y, cz, r, r, 0f, 2f * PI_F, 32))
        drawPath(cara, v.c(f.w(cx, y, cz), C.LACA, ARRIBA))
        clipPath(cara) {
            var rr = 0.052f; var i = 0
            while (rr < r - 0.004f) { drawPath(poligono(aroL(f, cx, y, cz, rr, rr, 0f, 2f * PI_F, 28)), v.c(f.w(cx, y, cz), lerp(C.LACA, C.MARFIL, if (i % 2 == 0) 0.14f else 0.06f), ARRIBA), style = Stroke(0.3f)); rr += 0.0075f; i++ }
            // El brillo en cruz de la luz en los surcos: dos lunas opuestas, del lado de la lámpara y del contrario.
            val cI = v.c(f.w(cx - r, y, cz), C.MARFIL, f.n(-1f, 0.4f, 0f)); val cD = v.c(f.w(cx + r, y, cz), C.MARFIL, f.n(1f, 0.4f, 0f))
            val a0 = if (lum(cI) > lum(cD)) PI_F * 0.86f else PI_F * -0.14f
            for (lado in 0..1) { val a = a0 + lado * PI_F
                for (j in 0..5) { val r1 = 0.058f + j * 0.011f
                    drawPath(Path().apply { val ps = aroL(f, cx, y, cz, r1, r1, a - 0.28f, a + 0.28f, 6); moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) },
                        v.c(f.w(cx, y, cz), lerp(C.LACA, C.MARFIL, 0.75f), ARRIBA).copy(alpha = (if (lado == 0) 0.55f else 0.3f) * (0.6f + 0.4f * v.noche)), style = Stroke(0.6f, cap = StrokeCap.Round)) } }
        }
        // El filo de luz del canto de delante (lo negro de noche necesita su filo cálido).
        drawPath(Path().apply { val ps = aroL(f, cx, y, cz, r, r, -PI_F * 0.85f, -PI_F * 0.15f, 16); moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(cx, y, cz - r), lerp(C.LACA, C.LAMPARA, 0.55f), V3(0f, 0.6f, -1f)).copy(alpha = 0.45f + 0.35f * v.noche), style = Stroke(0.5f))
        // La etiqueta: coral con su aro de oro y una franja de marfil; el agujero.
        val etiqueta = poligono(aroL(f, cx, y + 0.0005f, cz, 0.045f, 0.045f, 0f, 2f * PI_F, 24))
        drawPath(etiqueta, v.c(f.w(cx, y, cz), C.CORAL, ARRIBA))
        clipPath(etiqueta) { cara(v, f, listOf(V3(cx - 0.05f, y + 0.0006f, cz + 0.012f), V3(cx + 0.05f, y + 0.0006f, cz + 0.012f), V3(cx + 0.05f, y + 0.0006f, cz + 0.022f), V3(cx - 0.05f, y + 0.0006f, cz + 0.022f)), C.MARFIL, ARRIBA) }
        drawPath(poligono(aroL(f, cx, y + 0.0006f, cz, 0.04f, 0.04f, 0f, 2f * PI_F, 24)), v.c(f.w(cx, y, cz), C.ORO, ARRIBA), style = Stroke(0.4f))
        drawOval(v.c(f.w(cx, y, cz), C.LACA, ARRIBA), f.p(cx, y, cz) - Offset(0.004f * k, 0.0015f * k), Size(0.008f * k, 0.003f * k))
    }

    /**
     * La papeleta del empeño: un cartón doblado por el trepado que la parte en dos, de pie como una tienda, con su ojal
     * de latón y su cordel; renglones y un sello morado, sin letras ni cifras. Al lado, dos monedas.
     */
    private fun DrawScope.ficha(v: VistaDeco, p: Pincel, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        val an = 0.068f; val alto = 0.06f; val zf = -0.04f; val zt = 0.032f
        sombrita(v, p, f, 0f, -0.004f, 0.08f, 0.045f, alto)
        // La mitad de atrás (el resguardo), que se apoya hacia el fondo.
        cara(v, f, listOf(V3(-an, alto, 0f), V3(an, alto, 0f), V3(an + 0.004f, 0f, zt), V3(-an + 0.004f, 0f, zt)), lerp(CARTON, C.SOMBRA, 0.2f), V3(0f, 0.6f, 0.8f))
        // La cara de delante: el cartón, con su luz.
        fun q(u: Float, s: Float) = V3(u, alto * s, zf * (1f - s))
        val n = V3(0f, 0.55f, -0.85f)
        val frente = cara(v, f, listOf(q(-an, 0f), q(an, 0f), q(an, 1f), q(-an, 1f)), CARTON, n)
        clipPath(frente) {
            // Los renglones impresos y uno escrito a mano, más grueso.
            for ((i, s) in listOf(0.24f, 0.4f, 0.56f).withIndex()) linea(v, f, q(-an + 0.03f, s), q(an - (if (i == 1) 0.04f else 0.012f), s), C.LACA, n, 0.0012f, 0.55f)
            linea(v, f, q(-an + 0.036f, 0.47f), q(an - 0.05f, 0.5f), lerp(C.PANO, C.LACA, 0.3f), n, 0.0026f, 0.7f)
            // El recuadro del número (en blanco, sin cifras) y el sello morado, de medio lado.
            drawPath(f.forma(listOf(q(-an + 0.026f, 0.66f), q(-an + 0.06f, 0.66f), q(-an + 0.06f, 0.86f), q(-an + 0.026f, 0.86f))), v.c(f.w(0f, alto * 0.7f, zf * 0.3f), C.LACA, f.n(n.x, n.y, n.z)).copy(alpha = 0.6f), style = Stroke(0.4f))
            val cs = f.w(an - 0.026f, alto * 0.62f, zf * 0.38f); val ps = CamDeco.p(cs); val rs = 0.016f * k
            drawCircle(v.c(cs, SELLO, f.n(n.x, n.y, n.z)).copy(alpha = 0.75f), rs, ps, style = Stroke(0.0022f * k))
            drawCircle(v.c(cs, SELLO, f.n(n.x, n.y, n.z)).copy(alpha = 0.6f), rs * 0.62f, ps, style = Stroke(0.0012f * k))
            for (j in 0..4) { val a = j * 2f * PI_F / 5f - 0.3f; drawLine(v.c(cs, SELLO, f.n(n.x, n.y, n.z)).copy(alpha = 0.55f), ps, ps + Offset(cos(a), sin(a) * 0.8f) * rs * 0.45f, 0.0016f * k) }
        }
        // El canto doblado de arriba, con su trepado, y los bordes.
        drawLine(v.c(f.w(0f, alto, 0f), lerp(CARTON, C.MARFIL, 0.5f), f.n(0f, 1f, -0.2f)), f.p(-an, alto, 0f), f.p(an, alto, 0f), 0.6f, StrokeCap.Round)
        for (j in 0..9) { val u = -an + 0.006f + j * (2f * an - 0.012f) / 9f; drawCircle(v.c(f.w(u, alto, 0f), lerp(CARTON, C.SOMBRA, 0.5f), ARRIBA), 0.25f, f.p(u, alto * 0.97f, zf * 0.03f)) }
        drawPath(frente, v.c(f.w(0f, 0.02f, zf), lerp(CARTON, C.SOMBRA, 0.45f), f.n(0f, 0.5f, -1f)).copy(alpha = 0.6f), style = Stroke(0.35f))
        // El ojal de latón y el cordel, que cae a la mesa y se enrosca.
        val ojo = q(-an + 0.013f, 0.8f); val po = f.p(ojo.x, ojo.y, ojo.z)
        drawCircle(v.c(f.w(ojo.x, ojo.y, ojo.z), C.LATON, f.n(n.x, n.y, n.z)), 0.0055f * k, po)
        drawCircle(v.c(f.w(ojo.x, ojo.y, ojo.z), lerp(C.LACA, C.CAOBA, 0.4f), f.n(n.x, n.y, n.z)), 0.0028f * k, po)
        val cordel = Path().apply { moveTo(po.x, po.y); val a = f.p(-an - 0.03f, 0.03f, -0.05f); val b = f.p(-an - 0.02f, 0.002f, -0.07f); val c = f.p(-an - 0.06f, 0.002f, -0.06f)
            cubicTo(a.x, a.y, b.x, b.y, c.x, c.y); val d = f.p(-an - 0.1f, 0.002f, -0.05f); val e = f.p(-an - 0.07f, 0.002f, -0.02f); val g = f.p(-an - 0.1f, 0.002f, -0.01f); cubicTo(d.x, d.y, e.x, e.y, g.x, g.y) }
        drawPath(cordel, v.c(f.w(-an - 0.05f, 0.01f, -0.05f), lerp(C.MARFIL, C.ROBLE, 0.35f), ARRIBA), style = Stroke(0.0025f * k, cap = StrokeCap.Round))
        // Dos monedas de diez centavos delante: plata gastada.
        for ((x, z) in listOf(0.05f to -0.085f, 0.075f to -0.07f)) {
            drawPath(poligono(aroL(f, x, 0.0012f, z, 0.0095f, 0.0095f, 0f, 2f * PI_F, 14)), v.c(f.w(x, 0f, z), lerp(C.CROMO, C.ACERA, 0.3f), ARRIBA))
            drawPath(poligono(aroL(f, x, 0.0014f, z, 0.007f, 0.007f, PI_F, 2f * PI_F, 8)), v.c(f.w(x, 0f, z), lerp(C.CROMO, Color.White, 0.5f), ARRIBA).copy(alpha = 0.5f), style = Stroke(0.35f))
        }
    }

    /** El collar de perlas de dos vueltas: enroscado sobre la mesa y con un tramo que cuelga por el borde, con su broche de oro. */
    private fun DrawScope.collar(v: VistaDeco, p: Pincel, f: Marco3, borde: Borde) {
        val k = CamDeco.k(f.o.z)
        val zE = borde.z(0.02f)
        // El camino del hilo: dos vueltas enroscadas, luego hacia el borde, una caída en U y de vuelta.
        val camino = ArrayList<V3>()
        for (i in 0..40) { val a = PI_F * 0.5f + i * 2f * PI_F / 40f; camino += V3(0.03f + 0.07f * cos(a), 0.007f, 0.05f + 0.05f * sin(a)) }
        for (i in 0..40) { val a = PI_F * 0.5f + i * 2f * PI_F / 40f; camino += V3(0.045f + 0.058f * cos(a), 0.016f, 0.045f + 0.042f * sin(a)) }
        val salida = camino.last()
        val cae = 0.12f
        for (i in 1..10) { val s = i / 10f; camino += V3(salida.x + (-0.02f - salida.x) * s, 0.007f, salida.z + (zE + 0.004f - salida.z) * s) }
        for (i in 1..24) { val s = i / 24f; val a = s * PI_F
            camino += V3(-0.02f + 0.075f * (1f - cos(a)) / 2f, -cae * sin(a) * (0.9f + 0.1f * sin(a)), zE - 0.006f - 0.012f * sin(a)) }
        for (i in 1..8) { val s = i / 8f; camino += V3(0.055f + 0.01f * s, 0.007f, zE + 0.004f + (0.03f) * s) }
        // Una perla cada 1.3 cm, de 6.5 mm de radio.
        val radio = 0.0065f; val paso = 0.013f
        var resto = 0f; val perlas = ArrayList<V3>()
        for (i in 1 until camino.size) { val a = camino[i - 1]; val b = camino[i]
            val l = sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y) + (b.z - a.z) * (b.z - a.z)); var s = resto
            while (s < l) { val t = s / l; perlas += V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t); s += paso }
            resto = s - l }
        // La sombra que deja en el mantel o el mármol y, donde cuelga, en la falda.
        for (q in perlas) { val w = f.p(q.x, maxOf(q.y - radio, 0.0005f), q.z + if (q.y < 0f) 0.008f else 0.004f); drawCircle(lerp(C.SOMBRA, C.LACA, 0.4f).copy(alpha = 0.32f), radio * k * 1.1f, w + Offset(0.002f * k, 0.002f * k), blendMode = BlendMode.Multiply) }
        // De la más lejana a la más cercana.
        val luzI = lum(v.c(f.o, C.MARFIL, f.n(-1f, 0.3f, -0.5f))) > lum(v.c(f.o, C.MARFIL, f.n(1f, 0.3f, -0.5f)))
        val sx = if (luzI) -1f else 1f
        for (q in perlas.sortedByDescending { it.z - it.y * 0.2f }) {
            val w = f.w(q.x, q.y, q.z); val pp = CamDeco.p(w); val rr = radio * k
            drawCircle(v.c(w, PERLA, f.n(-sx, 0.1f, -0.5f)), rr, pp)
            drawCircle(v.c(w, PERLA, f.n(sx * 0.5f, 0.6f, -0.6f)), rr * 0.68f, pp + Offset(sx * rr * 0.22f, -rr * 0.22f))
            drawCircle(v.c(w, lerp(PERLA, Color.White, 0.8f), f.n(sx, 0.5f, -0.5f)).copy(alpha = 0.85f), rr * 0.3f, pp + Offset(sx * rr * 0.38f, -rr * 0.4f))
        }
        // El broche de oro, en la vuelta de encima.
        val b = f.w(0.045f + 0.058f * cos(PI_F * 1.4f), 0.018f, 0.045f + 0.042f * sin(PI_F * 1.4f))
        drawOval(v.c(b, C.ORO, ARRIBA), CamDeco.p(b) - Offset(0.008f * k, 0.004f * k), Size(0.016f * k, 0.008f * k))
        drawCircle(v.c(b, lerp(C.ORO, Color.White, 0.6f), ARRIBA).copy(alpha = 0.7f), 0.0025f * k, CamDeco.p(b) - Offset(0.002f * k, 0.0015f * k))
    }

    /** El pañuelo grande de algodón blanco, arrugado, con una punta que cae por el borde y su vainica de color. */
    private fun DrawScope.panuelo(v: VistaDeco, p: Pincel, f: Marco3, borde: Borde) {
        val k = CamDeco.k(f.o.z)
        val az = Azar(Semilla.de("deco", "panuelo"))
        val zE = borde.z(0.0f)
        sombrita(v, p, f, 0.0f, 0.0f, 0.1f, 0.07f, 0.03f)
        // La tela tendida: un contorno irregular que llega al borde.
        val contorno = (0..13).map { i -> val a = i * 2f * PI_F / 14f; val rr = 1f + az.entre(-0.18f, 0.18f)
            val z = 0.02f + 0.075f * sin(a) * rr; V3(0.01f + 0.1f * cos(a) * rr, 0.003f, maxOf(z, zE + 0.006f)) }
        cara(v, f, contorno, ALGODON, ARRIBA)
        // La punta que cae por el borde: un pico de tela, con su pliegue.
        val pi = V3(-0.035f, 0.003f, zE + 0.004f); val pd = V3(0.06f, 0.003f, zE + 0.004f); val punta = V3(0.0f, -0.1f, zE - 0.012f)
        cara(v, f, listOf(pi, pd, V3(0.035f, -0.04f, zE - 0.008f), punta, V3(-0.03f, -0.035f, zE - 0.006f)), ALGODON, V3(0.1f, 0.15f, -1f))
        linea(v, f, V3(0.01f, 0f, zE), V3(0.004f, -0.07f, zE - 0.011f), lerp(ALGODON, C.SOMBRA, 0.45f), FRENTE, 0.003f, 0.6f)
        // La vainica bordada a lo largo de los bordes de la punta.
        linea(v, f, V3(0.05f, -0.004f, zE - 0.002f), V3(0.008f, -0.088f, zE - 0.012f), C.CORAL, V3(0.3f, 0.1f, -1f), 0.0018f, 0.75f)
        linea(v, f, V3(-0.026f, -0.03f, zE - 0.007f), V3(-0.002f, -0.088f, zE - 0.012f), C.CORAL, V3(-0.3f, 0.1f, -1f), 0.0018f, 0.75f)
        // Los pliegues del arrugado: crestas con un lado en luz y otro en sombra.
        for (i in 0..5) {
            val x0 = az.entre(-0.07f, 0.08f); val z0 = az.entre(-0.03f, 0.07f); val l = az.entre(0.04f, 0.08f); val g = az.entre(-0.9f, 0.9f); val alto = az.entre(0.02f, 0.04f)
            val a = V3(x0, 0.004f, z0); val b = V3(x0 + l * cos(g), 0.004f, z0 + l * sin(g)); val c = V3((a.x + b.x) / 2f, alto, (a.z + b.z) / 2f)
            val nx = -sin(g); val nz = cos(g)
            cara(v, f, listOf(a, c, b, V3(c.x + nx * 0.02f, 0.004f, c.z + nz * 0.02f)), ALGODON, V3(nx, 0.8f, nz))
            cara(v, f, listOf(a, c, b, V3(c.x - nx * 0.02f, 0.004f, c.z - nz * 0.02f)), ALGODON, V3(-nx, 0.8f, -nz))
            linea(v, f, a, c, lerp(ALGODON, Color.White, 0.5f), ARRIBA, 0.0015f, 0.55f)
            // El surco de al lado, donde la tela se hunde: una raya de sombra.
            linea(v, f, V3(a.x - nx * 0.012f, 0.004f, a.z - nz * 0.012f), V3(b.x - nx * 0.012f, 0.004f, b.z - nz * 0.012f), lerp(ALGODON, C.SOMBRA, 0.6f), ARRIBA, 0.0025f, 0.45f)
        }
        // Un pliegue grande que se levanta delante, el que más se ve.
        cara(v, f, listOf(V3(-0.05f, 0.004f, -0.04f), V3(0.0f, 0.05f, -0.02f), V3(0.07f, 0.004f, -0.05f)), ALGODON, V3(0.1f, 0.5f, -1f))
        cara(v, f, listOf(V3(0.0f, 0.05f, -0.02f), V3(0.07f, 0.004f, -0.05f), V3(0.06f, 0.004f, 0.02f)), ALGODON, V3(0.8f, 0.5f, -0.2f))
        linea(v, f, V3(-0.05f, 0.004f, -0.04f), V3(0f, 0.05f, -0.02f), lerp(ALGODON, Color.White, 0.6f), ARRIBA, 0.002f, 0.7f)
        drawPath(f.forma(contorno), v.c(f.o, lerp(ALGODON, C.SOMBRA, 0.35f), ARRIBA).copy(alpha = 0.4f), style = Stroke(0.0015f * k))
    }

    /**
     * La partitura de treinta y cinco centavos, cerrada sobre la mesa y caída por el borde: la portada de colores con su
     * abanico de rayos y su cartela en blanco (sin la cantante ni las letras) y, debajo, una hoja suelta con pentagramas.
     */
    private fun DrawScope.partitura(v: VistaDeco, p: Pincel, f: Marco3, borde: Borde) {
        val k = CamDeco.k(f.o.z)
        val x0 = -0.095f; val x1 = 0.135f; val zLejos = 0.14f; val cae = 0.052f
        val xs = (0..10).map { x0 + (x1 - x0) * it / 10f }
        sombrita(v, p, f, 0.02f, 0.02f, 0.14f, 0.1f, 0.005f)
        // La hoja suelta de debajo, asomando por detrás y por la derecha, con sus pentagramas.
        val hoja = listOf(V3(x0 + 0.03f, 0.001f, -0.04f), V3(x1 + 0.03f, 0.001f, -0.03f), V3(x1 + 0.025f, 0.001f, zLejos + 0.04f), V3(x0 + 0.025f, 0.001f, zLejos + 0.03f))
        val fh = cara(v, f, hoja, PAPEL, ARRIBA)
        clipPath(fh) { for (s in 0..4) for (l in 0..4) { val z = zLejos + 0.034f - s * 0.04f - l * 0.0045f; linea(v, f, V3(x0 + 0.04f, 0.0012f, z), V3(x1 + 0.02f, 0.0012f, z), C.LACA, ARRIBA, 0.0009f, 0.5f) } }
        // La portada: la parte tendida en la mesa y la que cae por el borde, siguiendo el canto redondo.
        val tendida = xs.map { V3(it, 0.003f, borde.z(it)) } + xs.asReversed().map { V3(it, 0.003f, zLejos) }
        val caida = xs.map { V3(it, 0.003f, borde.z(it) - 0.002f) } + xs.asReversed().map { V3(it + 0.004f, -cae, borde.z(it) - 0.016f) }
        val ft = cara(v, f, tendida, lerp(C.MARFIL, C.CORAL, 0.15f), ARRIBA)
        val nC = V3(0f, 0.15f, -1f)
        val fc = cara(v, f, caida, lerp(C.MARFIL, C.CORAL, 0.15f), nC)
        // El dibujo de la portada: un abanico de rayos de coral y oro que nace abajo, en la parte que cae, y sube a la tendida.
        fun enPortada(x: Float, s: Float): V3 = if (s <= 1f) V3(x + 0.004f * (1f - s), -cae * (1f - s), borde.z(x) - 0.002f - 0.014f * (1f - s)) else V3(x, 0.003f, borde.z(x) + (s - 1f) * (zLejos - borde.z(x)))
        val xm = 0.02f
        for ((forma, n) in listOf(fc to nC, ft to ARRIBA)) clipPath(forma) {
            for (j in 0..8) { val a0 = -0.95f + j * 0.21f; val a1 = a0 + 0.105f
                val col = if (j % 2 == 0) C.CORAL else C.ORO
                cara(v, f, listOf(enPortada(xm, 0.02f), enPortada(xm + 0.6f * sin(a0), 0.02f + 2.2f * cos(a0)), enPortada(xm + 0.6f * sin(a1), 0.02f + 2.2f * cos(a1))), col, n) }
            // El sol del abanico, la franja de jade del pie y la cartela en blanco.
            cara(v, f, xs.map { enPortada(it, 0.0f) } + xs.asReversed().map { enPortada(it, 0.16f) }, C.JADE, nC)
            val sol = (0..10).map { i -> val a = -PI_F / 2f + PI_F * i / 10f; enPortada(xm + 0.035f * sin(a), 0.16f + 0.42f * cos(a)) }
            cara(v, f, sol, lerp(C.ORO, C.MARFIL, 0.4f), nC)
            cara(v, f, listOf(enPortada(x0 + 0.03f, 1.35f), enPortada(x1 - 0.03f, 1.35f), enPortada(x1 - 0.03f, 1.75f), enPortada(x0 + 0.03f, 1.75f)), C.MARFIL, ARRIBA)
            cara(v, f, listOf(enPortada(x0 + 0.02f, 1.82f), enPortada(x1 - 0.02f, 1.82f), enPortada(x1 - 0.02f, 1.88f), enPortada(x0 + 0.02f, 1.88f)), C.LACA, ARRIBA, 0.7f)
        }
        // El doblez del borde, con su luz, y el canto de la portada.
        drawPath(Path().apply { val ps = xs.map { f.p(it, 0.003f, borde.z(it) - 0.002f) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0f, 0f, borde.z(0f)), lerp(C.MARFIL, Color.White, 0.4f), V3(0f, 1f, -1f)).copy(alpha = 0.7f), style = Stroke(0.0025f * k, cap = StrokeCap.Round))
        drawPath(fc, v.c(f.w(0f, -cae, borde.z(0f)), lerp(C.MARFIL, C.SOMBRA, 0.5f), nC).copy(alpha = 0.6f), style = Stroke(0.35f))
        // Un lápiz encima, de laca roja con su punta.
        linea(v, f, V3(-0.04f, 0.008f, 0.1f), V3(0.09f, 0.008f, 0.06f), C.PARED, V3(0f, 1f, -0.4f), 0.006f)
        linea(v, f, V3(0.09f, 0.008f, 0.06f), V3(0.105f, 0.006f, 0.055f), lerp(C.ROBLE, C.MARFIL, 0.4f), ARRIBA, 0.004f)
    }

    /** El cigarrillo encendido en su boquilla larga de laca y oro, apoyado en un cenicero de latón escalonado, con su hilo de humo. */
    private fun DrawScope.cigarrillo(v: VistaDeco, p: Pincel, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        val cx = 0.045f; val cz = 0.03f
        sombrita(v, p, f, cx - 0.04f, cz - 0.02f, 0.11f, 0.06f, 0.02f)
        // El cenicero: dos escalones de latón y la taza de dentro, con la ceniza.
        torneado(v, f, cx, cz, listOf(0.0f to 0.05f, 0.008f to 0.05f, 0.01f to 0.043f, 0.02f to 0.041f), C.LATON, 0.55f)
        drawPath(poligono(aroL(f, cx, 0.02f, cz, 0.041f, 0.041f, 0f, 2f * PI_F, 22)), v.c(f.w(cx, 0.02f, cz), lerp(C.LATON, C.MARFIL, 0.3f), ARRIBA))
        drawPath(poligono(aroL(f, cx, 0.016f, cz, 0.031f, 0.031f, 0f, 2f * PI_F, 22)), v.c(f.w(cx, 0.016f, cz), lerp(C.ACERA, C.LACA, 0.35f), ARRIBA))
        val az = Azar(Semilla.de("deco", "ceniza"))
        repeat(6) { val q = f.p(cx + az.entre(-0.02f, 0.02f), 0.017f, cz + az.entre(-0.015f, 0.015f)); drawCircle(v.c(f.w(cx, 0.017f, cz), lerp(C.ACERA, C.MARFIL, 0.3f), ARRIBA), az.entre(0.002f, 0.004f) * k, q) }
        // La boquilla: de la mesa, delante a la izquierda, al canto del cenicero; el cigarrillo sigue y cuelga sobre la ceniza.
        val boca = V3(-0.15f, 0.004f, -0.06f); val apoya = V3(cx - 0.041f, 0.021f, cz - 0.006f)
        fun en(t: Float) = V3(boca.x + (apoya.x - boca.x) * t, boca.y + (apoya.y - boca.y) * t, boca.z + (apoya.z - boca.z) * t)
        val fin = en(1.22f); val brasa = en(1.62f)
        // Su sombra en la mesa.
        drawLine(lerp(C.SOMBRA, C.LACA, 0.4f).copy(alpha = 0.3f), f.p(boca.x, 0.001f, boca.z + 0.004f), f.p(fin.x, 0.001f, fin.z + 0.012f), 0.007f * k, StrokeCap.Round)
        linea(v, f, boca, fin, C.LACA, V3(0f, 1f, -0.4f), 0.0075f)
        linea(v, f, boca, fin, lerp(C.LACA, C.LAMPARA, 0.6f), V3(-0.3f, 1f, -0.4f), 0.0022f, 0.5f + 0.3f * v.noche)
        for (t in listOf(0.04f, 1.18f)) linea(v, f, en(t), en(t + 0.04f), C.ORO, V3(0f, 1f, -0.5f), 0.0085f)
        // El cigarrillo: papel blanco, la ceniza gris y la brasa.
        linea(v, f, fin, brasa, lerp(C.MARFIL, Color.White, 0.4f), V3(0f, 1f, -0.5f), 0.0062f)
        val ceniza = en(1.56f)
        linea(v, f, ceniza, brasa, lerp(C.ACERA, C.MARFIL, 0.2f), ARRIBA, 0.0062f)
        val pb = f.p(brasa.x, brasa.y, brasa.z)
        mancha(pb, 0.03f * k, 0.022f * k, ASCUA, 0.4f + 0.4f * v.noche)
        drawCircle(ASCUA, 0.0055f * k, pb)
        drawCircle(lerp(ASCUA, Color.White, 0.45f), 0.003f * k, pb)
        // El hilo de humo, que sube y se abre (se queda quieto en esta capa: el que se mueve es el del cuadro).
        val humo = v.c(f.w(brasa.x, brasa.y + 0.1f, brasa.z), C.HUMO, FRENTE)
        for (j in 0..2) {
            val c = Path(); c.moveTo(pb.x, pb.y)
            val s = j * 0.008f
            val a = f.p(brasa.x - 0.02f + s, brasa.y + 0.06f, brasa.z); val b = f.p(brasa.x + 0.03f - s, brasa.y + 0.1f, brasa.z); val d = f.p(brasa.x + 0.0f + s, brasa.y + 0.15f, brasa.z)
            c.cubicTo(a.x, a.y, b.x, b.y, d.x, d.y)
            val e1 = f.p(brasa.x - 0.04f - s, brasa.y + 0.19f, brasa.z); val e2 = f.p(brasa.x + 0.01f, brasa.y + 0.23f, brasa.z); val e3 = f.p(brasa.x - 0.03f + 2f * s, brasa.y + 0.27f, brasa.z)
            c.cubicTo(e1.x, e1.y, e2.x, e2.y, e3.x, e3.y)
            drawPath(c, humo.copy(alpha = 0.45f - j * 0.1f), style = Stroke((0.003f + j * 0.004f) * k, cap = StrokeCap.Round))
        }
    }

    /**
     * El metrónomo de pirámide, de caoba, sin su tapa: dentro, la regla de marfil con sus rayas (sin cifras) y la aguja de
     * acero con su pesa de latón, parada a un lado; arriba, el remate de latón, y en el costado, la llave de la cuerda.
     */
    private fun DrawScope.metronomo(v: VistaDeco, p: Pincel, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        val b = 0.06f; val d = 0.05f; val h = 0.235f; val tb = 0.013f; val td = 0.011f; val y0 = 0.022f
        sombrita(v, p, f, 0f, 0f, 0.085f, 0.07f, h)
        // El zócalo.
        with(PiezasDeco) { caja(v, f, -b - 0.008f, b + 0.008f, 0f, y0, -d - 0.008f, d + 0.008f, lerp(C.CAOBA, C.LACA, 0.35f)) }
        // Las caras de la pirámide que se ven: el costado izquierdo y el frente.
        val izq = listOf(V3(-b, y0, d), V3(-b, y0, -d), V3(-tb, h, -td), V3(-tb, h, td))
        if (CamDeco.seVe(f.w(-b, y0, 0f), f.n(-1f, 0.25f, 0f))) cara(v, f, izq, C.CAOBA, V3(-1f, 0.25f, 0f))
        val frente = cara(v, f, listOf(V3(-b, y0, -d), V3(b, y0, -d), V3(tb, h, -td), V3(-tb, h, -td)), C.CAOBA, V3(0f, 0.2f, -1f))
        // La veta del chapado y el filete claro de las aristas.
        clipPath(frente) { val az = Azar(Semilla.de("deco", "metronomo")); repeat(8) { val x = az.entre(-b, b); linea(v, f, V3(x, y0, -d), V3(x * 0.25f, h, -td), lerp(C.CAOBA, C.ROBLE, 0.5f), FRENTE, 0.0012f, 0.4f) } }
        linea(v, f, V3(-b, y0, -d), V3(-tb, h, -td), lerp(C.CAOBA, C.LAMPARA, 0.5f), V3(-0.6f, 0.3f, -0.8f), 0.0022f, 0.7f)
        linea(v, f, V3(b, y0, -d), V3(tb, h, -td), lerp(C.CAOBA, C.LAMPARA, 0.5f), V3(0.6f, 0.3f, -0.8f), 0.0022f, 0.7f)
        // El hueco de delante (la tapa quitada): oscuro, con la regla de marfil y sus rayas.
        fun en(s: Float, u: Float): V3 { val y = y0 + 0.03f + s * (h - y0 - 0.06f); val ancho = b + (tb - b) * ((y - y0) / (h - y0)); return V3(u * ancho * 0.62f, y, -d + (d - td) * ((y - y0) / (h - y0)) - 0.001f) }
        cara(v, f, listOf(en(0f, -1f), en(0f, 1f), en(1f, 1f), en(1f, -1f)), lerp(C.LACA, C.CAOBA, 0.35f), V3(0f, 0.2f, -1f))
        cara(v, f, listOf(en(0.12f, -0.18f), en(0.12f, 0.18f), en(0.97f, 0.18f), en(0.97f, -0.18f)), C.MARFIL, V3(0f, 0.2f, -1f))
        for (j in 0..10) { val s = 0.18f + j * 0.075f; linea(v, f, en(s, -0.16f), en(s, if (j % 2 == 0) 0.02f else -0.06f), C.LACA, FRENTE, 0.0009f, 0.7f) }
        // La aguja, parada a la derecha, con su pesa; el eje abajo.
        val eje = en(0.02f, 0f); val ang = 0.24f; val largo = 0.19f
        val punta = V3(eje.x + largo * sin(ang), eje.y + largo * cos(ang), eje.z - 0.006f)
        linea(v, f, eje, punta, C.CROMO, FRENTE, 0.0028f)
        val pesa = V3(eje.x + largo * 0.6f * sin(ang), eje.y + largo * 0.6f * cos(ang), eje.z - 0.008f)
        cara(v, f, listOf(V3(pesa.x - 0.011f, pesa.y - 0.008f, pesa.z), V3(pesa.x + 0.011f, pesa.y - 0.006f, pesa.z), V3(pesa.x + 0.008f, pesa.y + 0.012f, pesa.z), V3(pesa.x - 0.009f, pesa.y + 0.01f, pesa.z)), C.LATON, V3(0.2f, 0.3f, -1f))
        drawCircle(v.c(f.w(pesa.x, pesa.y, pesa.z), lerp(C.LATON, Color.White, 0.6f), FRENTE).copy(alpha = 0.7f), 0.0025f * k, f.p(pesa.x - 0.004f, pesa.y + 0.005f, pesa.z))
        drawCircle(v.c(f.w(eje.x, eje.y, eje.z), C.LATON, FRENTE), 0.004f * k, f.p(eje.x, eje.y, eje.z))
        // El remate y la llave.
        drawCircle(v.c(f.w(0f, h, -td), C.LATON, ARRIBA), 0.007f * k, f.p(0f, h + 0.004f, 0f))
        linea(v, f, V3(-b * 0.75f, 0.075f, 0f), V3(-b * 0.75f - 0.018f, 0.075f, 0f), C.LATON, V3(-1f, 0.3f, -0.3f), 0.004f)
        cara(v, f, listOf(V3(-b * 0.75f - 0.018f, 0.06f, -0.004f), V3(-b * 0.75f - 0.018f, 0.09f, -0.004f), V3(-b * 0.75f - 0.022f, 0.088f, 0.004f), V3(-b * 0.75f - 0.022f, 0.062f, 0.004f)), C.LATON, V3(-1f, 0.2f, -0.4f))
    }

    /** El cloché de fieltro color vino: la campana con su ala corta caída, la cinta de raso y el broche de abanico con sus brillantes. */
    private fun DrawScope.cloche(v: VistaDeco, p: Pincel, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        sombrita(v, p, f, 0f, 0f, 0.115f, 0.1f, 0.13f)
        val perfil = listOf(0.0f to 0.112f, 0.008f to 0.104f, 0.02f to 0.094f, 0.03f to 0.09f, 0.06f to 0.09f, 0.09f to 0.083f, 0.112f to 0.066f, 0.128f to 0.04f, 0.136f to 0.012f)
        val copa = torneado(v, f, 0f, 0f, perfil, FIELTRO, 0.4f)
        clipPath(copa) {
            // La cinta de raso, de oro viejo, con su brillo.
            val cinta = poligono(aroL(f, 0f, 0.05f, 0f, 0.091f, 0.091f, 0f, -PI_F) + aroL(f, 0f, 0.032f, 0f, 0.091f, 0.091f, -PI_F, 0f))
            drawPath(cinta, Brush.horizontalGradient(0f to v.c(f.w(-0.09f, 0.04f, 0f), lerp(C.ORO, C.MARFIL, 0.2f), f.n(-1f, 0f, -0.3f)), 0.5f to v.c(f.w(0f, 0.04f, -0.09f), lerp(C.ORO, C.MARFIL, 0.35f), FRENTE), 1f to v.c(f.w(0.09f, 0.04f, 0f), lerp(C.ORO, C.MARFIL, 0.2f), f.n(1f, 0f, -0.3f)), startX = f.p(-0.09f, 0.04f, 0f).x, endX = f.p(0.09f, 0.04f, 0f).x))
            // El canto del ala, más claro (se dobla hacia abajo y coge luz), y el pespunte.
            drawPath(Path().apply { val ps = aroL(f, 0f, 0.009f, 0f, 0.104f, 0.104f, 0f, -PI_F); moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0f, 0.01f, -0.1f), lerp(FIELTRO, C.MARFIL, 0.25f), V3(0f, 0.6f, -1f)), style = Stroke(0.004f * k))
            drawPath(Path().apply { val ps = aroL(f, 0f, 0.075f, 0f, 0.087f, 0.087f, -0.1f, -PI_F + 0.1f); moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0f, 0.075f, -0.087f), lerp(FIELTRO, C.LACA, 0.4f), FRENTE).copy(alpha = 0.6f), style = Stroke(0.0012f * k))
            // El pelo del fieltro.
            val az = Azar(Semilla.de("deco", "cloche"))
            repeat(30) { val a = az.entre(-PI_F, 0f); val y = az.entre(0.055f, 0.13f); val r = perfil.first { it.first >= y }.second
                val q = f.p(r * cos(a), y, r * sin(a)); drawLine(v.c(f.w(r * cos(a), y, r * sin(a)), lerp(FIELTRO, C.MARFIL, 0.2f), f.n(cos(a), 0.4f, sin(a))).copy(alpha = 0.3f), q, q + Offset(az.entre(-1f, 1f), az.entre(-0.6f, 0.6f)), 0.3f) }
        }
        // El broche: un abanico de oro con tres brillantes, sobre la cinta, a la izquierda.
        val a = -PI_F * 0.68f; val bc = V3(0.092f * cos(a), 0.041f, 0.092f * sin(a)); val pb = f.p(bc.x, bc.y, bc.z)
        val nb = f.n(cos(a), 0.2f, sin(a))
        val abanico = Path().apply { moveTo(pb.x, pb.y + 0.006f * k); for (i in 0..6) { val g = PI_F * (1.15f + 0.7f * i / 6f); lineTo(pb.x + cos(g) * 0.017f * k, pb.y + 0.006f * k + sin(g) * 0.017f * k) }; close() }
        drawPath(abanico, v.c(f.w(bc.x, bc.y, bc.z), C.ORO, nb))
        for (i in 0..4) { val g = PI_F * (1.2f + 0.6f * i / 4f); drawLine(v.c(f.w(bc.x, bc.y, bc.z), lerp(C.ORO, C.LACA, 0.4f), nb), Offset(pb.x, pb.y + 0.006f * k), Offset(pb.x + cos(g) * 0.016f * k, pb.y + 0.006f * k + sin(g) * 0.016f * k), 0.3f) }
        for (i in 0..2) { val g = PI_F * (1.3f + 0.4f * i / 2f); val q = Offset(pb.x + cos(g) * 0.011f * k, pb.y + 0.006f * k + sin(g) * 0.011f * k)
            drawCircle(v.c(f.w(bc.x, bc.y, bc.z), lerp(C.MARFIL, Color.White, 0.6f), nb), 0.0022f * k, q)
            mancha(q, 0.005f * k, 0.005f * k, v.c(f.w(bc.x, bc.y, bc.z), Color.White, nb), 0.2f + 0.3f * v.noche) }
    }

    // =================================================================
    //  Los grandes
    // =================================================================

    private const val RADIO_ANCHO = 0.17f
    private const val RADIO_FONDO = 0.115f
    private const val RADIO_HOMBRO = 0.27f
    private const val RADIO_ALTO = 0.44f

    /** El contorno del frente de la radio de capilla, de abajo a la izquierda a abajo a la derecha, pasando por el arco. */
    private fun contornoRadio(margen: Float = 0f): List<Pair<Float, Float>> {
        val w = RADIO_ANCHO - margen; val y1 = RADIO_HOMBRO; val top = RADIO_ALTO - margen
        val lista = ArrayList<Pair<Float, Float>>()
        lista += -w to margen; lista += -w to y1
        // Un arco apuntado (de capilla): dos curvas que se juntan arriba, algo redondeadas.
        for (i in 1..10) { val s = i / 10f; val a = s * PI_F / 2f; lista += (-w + w * (1f - cos(a)) * 0.98f) to (y1 + (top - y1) * sin(a) * (1f - 0.05f * s)) }
        lista += 0f to top
        for (i in 9 downTo 0) { val s = i / 10f; val a = s * PI_F / 2f; lista += (w - w * (1f - cos(a)) * 0.98f) to (y1 + (top - y1) * sin(a) * (1f - 0.05f * s)) }
        lista += w to margen
        return lista
    }

    private fun siluetaRadio(f: Marco3): Path {
        val c = contornoRadio(); val ps = ArrayList<Offset>()
        for ((x, y) in c) { ps += f.p(x, y, -RADIO_FONDO); ps += f.p(x, y, RADIO_FONDO) }
        return envolvente(ps)
    }

    /**
     * La radio de capilla, de nogal: el arco de madera con su moldura, la rejilla calada en abanico de rayos sobre la tela
     * de oro, el dial en su ventanita de latón y tres botones de baquelita; el cable, por el suelo. De noche está
     * encendida: el dial da luz ámbar.
     */
    private fun DrawScope.radio(v: VistaDeco, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        val c = contornoRadio(); val D = RADIO_FONDO
        val encendida = v.noche > 0.5f && !v.prov
        // El cable, que sale por detrás y se va por el suelo.
        val cable = Path().apply { val a = f.p(0.1f, 0.03f, D); moveTo(a.x, a.y); val b = f.p(0.25f, 0.0f, D + 0.05f); val cc = f.p(0.2f, 0f, -0.15f); val d = f.p(0.42f, 0f, -0.1f); cubicTo(b.x, b.y, cc.x, cc.y, d.x, d.y) }
        drawPath(cable, v.c(f.w(0.3f, 0f, 0f), lerp(C.PANO, C.LACA, 0.4f), ARRIBA), style = Stroke(0.008f * k, cap = StrokeCap.Round))
        // Las caras curvas: cada tramo del contorno, de delante a atrás, con la luz que le toca por su normal.
        for (i in 0 until c.size - 1) {
            val (xa, ya) = c[i]; val (xb, yb) = c[i + 1]
            val nx = yb - ya; val ny = -(xb - xa); val l = sqrt(nx * nx + ny * ny).coerceAtLeast(0.0001f)
            val n = V3(-nx / l, -ny / l, 0f)
            if (!CamDeco.seVe(f.w((xa + xb) / 2f, (ya + yb) / 2f, 0f), f.n(n.x, n.y, n.z))) continue
            cara(v, f, listOf(V3(xa, ya, -D), V3(xb, yb, -D), V3(xb, yb, D), V3(xa, ya, D)), NOGAL, n)
        }
        // El frente.
        val frente = f.forma(c.map { V3(it.first, it.second, -D) })
        val cI = v.c(f.w(-RADIO_ANCHO, 0.2f, -D), NOGAL, f.n(-0.3f, 0f, -1f)); val cD = v.c(f.w(RADIO_ANCHO, 0.2f, -D), NOGAL, f.n(0.3f, 0f, -1f))
        drawPath(frente, Brush.horizontalGradient(0f to cI, 1f to cD, startX = f.p(-RADIO_ANCHO, 0.2f, -D).x, endX = f.p(RADIO_ANCHO, 0.2f, -D).x))
        clipPath(frente) {
            // La veta del chapado, en espiga desde el centro.
            val az = Azar(Semilla.de("deco", "radio"))
            repeat(16) { val x = az.entre(-RADIO_ANCHO, RADIO_ANCHO); linea(v, f, V3(x, 0.02f, -D), V3(x * 0.8f, RADIO_ALTO, -D), lerp(NOGAL, C.ROBLE, 0.5f), FRENTE, 0.0015f, 0.35f) }
            // Las dos columnas de los lados, más oscuras, con su filete claro.
            for (s in listOf(-1f, 1f)) { cara(v, f, listOf(V3(s * RADIO_ANCHO, 0.02f, -D), V3(s * (RADIO_ANCHO - 0.03f), 0.02f, -D), V3(s * (RADIO_ANCHO - 0.03f), RADIO_HOMBRO, -D), V3(s * RADIO_ANCHO, RADIO_HOMBRO, -D)), lerp(NOGAL, C.LACA, 0.3f), V3(s * 0.2f, 0f, -1f))
                linea(v, f, V3(s * (RADIO_ANCHO - 0.03f), 0.03f, -D), V3(s * (RADIO_ANCHO - 0.03f), RADIO_HOMBRO, -D), lerp(NOGAL, C.LAMPARA, 0.45f), V3(-s * 0.4f, 0f, -1f), 0.002f, 0.7f) }
        }
        // La moldura del arco, la rejilla con su tela y su calado en abanico.
        val moldura = c.filter { it.second > 0.13f }.map { (x, y) -> V3(x * 0.82f, 0.13f + (y - 0.13f) * 0.93f, -D - 0.002f) }
        val fm = f.forma(moldura)
        drawPath(fm, v.c(f.w(0f, 0.3f, -D), lerp(NOGAL, C.MARFIL, 0.18f), FRENTE), style = Stroke(0.009f * k))
        val tela = f.forma(moldura.map { V3(it.x * 0.9f, 0.14f + (it.y - 0.14f) * 0.92f, it.z) })
        drawPath(tela, v.c(f.w(0f, 0.28f, -D), lerp(C.ORO, C.ROBLE, 0.45f), FRENTE))
        clipPath(tela) {
            val az = Azar(Semilla.de("deco", "tela")); repeat(40) { val q = f.p(az.entre(-0.13f, 0.13f), az.entre(0.14f, 0.42f), -D - 0.002f); drawCircle(v.c(f.w(0f, 0.28f, -D), lerp(C.ORO, C.MARFIL, 0.3f), FRENTE).copy(alpha = 0.25f), 0.3f, q) }
            if (encendida) drawPath(tela, Brush.verticalGradient(listOf(Color.Transparent, DIAL.copy(alpha = 0.18f)), startY = f.p(0f, 0.4f, -D).y, endY = f.p(0f, 0.14f, -D).y), blendMode = BlendMode.Plus)
            // El calado: rayos de madera que nacen abajo en el centro y arcos que los cruzan.
            val o = V3(0f, 0.14f, -D - 0.003f)
            for (j in 0..8) { val a = -1.1f + j * 2.2f / 8f; linea(v, f, o, V3(o.x + 0.3f * sin(a), o.y + 0.3f * cos(a), o.z), NOGAL, FRENTE, 0.009f) }
            for (rr in listOf(0.09f, 0.19f)) drawPath(Path().apply { val ps = (0..14).map { i -> val a = -1.3f + 2.6f * i / 14f; f.p(o.x + rr * sin(a), o.y + rr * cos(a), o.z) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0f, 0.25f, -D), NOGAL, FRENTE), style = Stroke(0.008f * k))
            for (j in 0..8) { val a = -1.1f + j * 2.2f / 8f; linea(v, f, V3(o.x + 0.004f, o.y + 0.002f, o.z), V3(o.x + 0.3f * sin(a) + 0.004f, o.y + 0.3f * cos(a), o.z), lerp(NOGAL, C.LAMPARA, 0.5f), V3(0.4f, 0.3f, -1f), 0.0018f, 0.6f) }
        }
        // El dial: una ventanita de arco con su bisel de latón; de noche, encendida.
        val dc = V3(0f, 0.085f, -D - 0.002f); val dr = 0.032f
        val dial = f.forma((0..12).map { i -> val a = PI_F * i / 12f; V3(dc.x + dr * cos(a), dc.y + dr * sin(a) * 0.9f, dc.z) } + listOf(V3(dc.x - dr, dc.y - 0.006f, dc.z), V3(dc.x + dr, dc.y - 0.006f, dc.z)).reversed())
        drawPath(dial, if (encendida) lerp(DIAL, Color.White, 0.2f) else v.c(f.w(dc.x, dc.y, dc.z), lerp(C.MARFIL, C.ORO, 0.3f), FRENTE))
        clipPath(dial) { for (j in 0..8) { val a = PI_F * (0.12f + 0.76f * j / 8f); linea(v, f, V3(dc.x + dr * 0.8f * cos(a), dc.y + dr * 0.75f * sin(a), dc.z), V3(dc.x + dr * 0.95f * cos(a), dc.y + dr * 0.88f * sin(a), dc.z), C.LACA, FRENTE, 0.0012f, 0.7f) }
            linea(v, f, V3(dc.x, dc.y - 0.004f, dc.z), V3(dc.x - dr * 0.4f, dc.y + dr * 0.7f, dc.z), C.PARED, FRENTE, 0.0014f) }
        drawPath(dial, v.c(f.w(dc.x, dc.y, dc.z), C.LATON, FRENTE), style = Stroke(0.004f * k))
        if (encendida) { val pd = f.p(dc.x, dc.y + 0.01f, dc.z); mancha(pd, 0.07f * k, 0.05f * k, DIAL, 0.35f); mancha(f.p(dc.x, 0f, -D - 0.06f), 0.14f * k, 0.035f * k, DIAL, 0.22f) }
        // Los tres botones de baquelita.
        for ((x, y, r) in listOf(Triple(-0.085f, 0.075f, 0.016f), Triple(0.085f, 0.075f, 0.016f), Triple(0f, 0.034f, 0.012f))) {
            val w = f.w(x, y, -D - 0.008f); val pp = CamDeco.p(w)
            drawCircle(lerp(C.SOMBRA, C.LACA, 0.3f).copy(alpha = 0.5f), r * k, pp + Offset(0.003f * k, 0.003f * k), blendMode = BlendMode.Multiply)
            drawCircle(v.c(w, BAQUELITA, FRENTE), r * k, pp)
            val s = if (lum(v.c(w, C.MARFIL, f.n(-1f, 0.3f, -0.5f))) > lum(v.c(w, C.MARFIL, f.n(1f, 0.3f, -0.5f)))) -1f else 1f
            drawCircle(v.c(w, lerp(BAQUELITA, C.MARFIL, 0.5f), f.n(s, 0.5f, -0.6f)).copy(alpha = 0.6f), r * k * 0.35f, pp + Offset(s * r * k * 0.35f, -r * k * 0.35f))
            for (j in 0..7) { val a = j * PI_F / 4f; drawLine(v.c(w, lerp(BAQUELITA, C.MARFIL, 0.25f), FRENTE).copy(alpha = 0.5f), pp + Offset(cos(a), sin(a)) * r * k * 0.75f, pp + Offset(cos(a), sin(a)) * r * k * 0.98f, 0.35f) }
        }
        // El zócalo de delante, más oscuro, y el filo cálido del canto del arco (lo oscuro de noche necesita su filo).
        cara(v, f, listOf(V3(-RADIO_ANCHO - 0.004f, 0f, -D - 0.004f), V3(RADIO_ANCHO + 0.004f, 0f, -D - 0.004f), V3(RADIO_ANCHO + 0.004f, 0.018f, -D - 0.004f), V3(-RADIO_ANCHO - 0.004f, 0.018f, -D - 0.004f)), lerp(NOGAL, C.LACA, 0.45f), FRENTE)
        drawPath(Path().apply { val ps = c.map { f.p(it.first, it.second, -D) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0f, RADIO_ALTO, -D), lerp(NOGAL, C.LAMPARA, 0.5f), V3(0f, 0.6f, -1f)).copy(alpha = 0.4f + 0.3f * v.noche), style = Stroke(0.004f * k, cap = StrokeCap.Round))
    }

    // ---- Los zapatos: x a lo largo (del talón, 0, a la punta), y arriba, z a lo ancho.

    private const val TACON = 0.068f
    private fun anchoZapato(x: Float): Float = when {
        x < 0.06f -> 0.026f + 0.06f * x
        x < 0.17f -> 0.03f + 0.08f * (x - 0.06f)
        else -> (0.039f - 0.38f * (x - 0.17f) * (x - 0.17f) / 0.07f).coerceAtLeast(0.008f)
    }
    /** La altura de la planta en [x]: el arco del tacón a la bola del pie. */
    private fun planta(x: Float): Float = when {
        x < 0.055f -> TACON
        x < 0.16f -> { val s = (x - 0.055f) / 0.105f; TACON * (1f - s * s * (3f - 2f * s)) + 0.006f * s }
        else -> 0.006f - 0.012f * (x - 0.16f)
    }.coerceAtLeast(0.004f)
    /** El canto de arriba del zapato (la boca, bajada en el empeine, y la puntera). */
    private fun boca(x: Float): Float = when {
        x < 0.012f -> TACON + 0.05f
        x < 0.13f -> { val s = (x - 0.012f) / 0.118f; TACON + 0.05f - (TACON + 0.05f - 0.058f) * (s * s * (3f - 2f * s)) }
        x < 0.2f -> 0.058f + 0.005f * (x - 0.13f) / 0.07f
        else -> 0.063f - 0.55f * (x - 0.2f)
    }.coerceAtLeast(0.006f)

    private const val LARGO = 0.24f

    private fun siluetaZapatos(f: Marco3): Path {
        val ps = ArrayList<Offset>()
        for (m in listOf(marcoDePie(f), marcoTumbado(f))) for (i in 0..12) { val x = LARGO * i / 12f
            for (z in listOf(-anchoZapato(x), anchoZapato(x))) { ps += m.p(x, 0f, z); ps += m.p(x, boca(x), z) } }
        return envolvente(ps)
    }

    /** El de delante, de perfil: la punta hacia la izquierda. */
    private fun marcoDePie(f: Marco3) = Marco3(f.w(0.1f, 0f, -0.05f), f.giro + PI_F + 0.12f)
    /** El otro, detrás: también de perfil, con la punta hacia la derecha y un poco hacia el fondo. */
    private fun marcoTumbado(f: Marco3) = Marco3(f.w(-0.06f, 0f, 0.1f), f.giro - 0.22f)

    /**
     * Los zapatos de baile de charol negro, de tira en T con su botón de oro y forro de raso claro: los dos de pie, como
     * se los quitó, uno con la punta hacia quien mira y el otro hacia el fondo, enseñando el tacón.
     */
    private fun DrawScope.zapatos(v: VistaDeco, f: Marco3) {
        val a = marcoDePie(f); val b = marcoTumbado(f)
        // Del más lejano al más cercano.
        val lista = listOf(a, b).sortedByDescending { it.w(LARGO / 2f, 0f, 0f).z }
        for (m in lista) { contacto(v, m, LARGO * 0.5f, 0f, LARGO * 0.55f, 0.045f); zapato(v, m) }
    }

    private fun DrawScope.zapato(v: VistaDeco, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        val charol = C.LACA; val forro = lerp(C.MARFIL, C.ORO, 0.3f)
        val xs = (0..20).map { LARGO * it / 20f }
        // Qué lado mira a quien mira: el de z negativa o el de z positiva.
        val cerca = if (CamDeco.seVe(f.w(LARGO / 2f, 0.04f, -anchoZapato(LARGO / 2f)), f.n(0f, 0f, -1f))) -1f else 1f
        fun lado(s: Float) = xs.map { V3(it, planta(it), s * anchoZapato(it)) } + xs.asReversed().map { V3(it, boca(it), s * anchoZapato(it) * 0.92f) }
        // El tacón: un bloque que se estrecha hacia abajo, con su tapa.
        val tz = 0.012f
        for (s in listOf(-cerca, cerca)) cara(v, f, listOf(V3(0.008f, TACON, s * tz), V3(0.05f, TACON, s * tz), V3(0.038f, 0.006f, s * tz * 0.7f), V3(0.024f, 0.006f, s * tz * 0.7f)), charol, V3(0f, 0f, s))
        cara(v, f, listOf(V3(0.024f, 0.006f, -tz * 0.7f), V3(0.038f, 0.006f, -tz * 0.7f), V3(0.038f, 0f, tz * 0.7f), V3(0.024f, 0f, tz * 0.7f)), lerp(C.ROBLE, C.LACA, 0.6f), ARRIBA)
        cara(v, f, listOf(V3(0.008f, TACON, cerca * tz), V3(0.024f, 0.006f, cerca * tz * 0.7f), V3(0.024f, 0.006f, -cerca * tz * 0.7f), V3(0.008f, TACON, -cerca * tz)), charol, V3(-1f, -0.1f, 0f))
        // La suela, un filo de cuero bajo el arco.
        drawPath(f.forma(xs.filter { it > 0.04f }.map { V3(it, planta(it) - 0.003f, cerca * anchoZapato(it)) } + xs.filter { it > 0.04f }.asReversed().map { V3(it, planta(it), cerca * anchoZapato(it)) }), v.c(f.w(0.15f, 0.01f, 0f), lerp(C.ROBLE, C.MARFIL, 0.2f), f.n(0f, -0.3f, cerca)), style = Stroke(0.004f * k))
        // El lado de lejos (por dentro se ve el forro) y la plantilla.
        cara(v, f, lado(-cerca), lerp(lerp(forro, C.CAOBA, 0.5f), C.LACA, 0.35f), V3(0f, 0.3f, cerca))
        cara(v, f, xs.map { V3(it, planta(it) + 0.004f, -anchoZapato(it) * 0.85f) } + xs.asReversed().map { V3(it, planta(it) + 0.004f, anchoZapato(it) * 0.85f) }, lerp(forro, C.LACA, 0.7f), ARRIBA)
        drawPath(Path().apply { val ps = xs.map { f.p(it, boca(it), -cerca * anchoZapato(it) * 0.92f) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0.1f, 0.08f, 0f), forro, f.n(0f, 1f, cerca)), style = Stroke(0.003f * k))
        // El lado de cerca, de charol: oscuro, con los brillos largos que le dan su materia.
        val n = V3(0f, 0.1f, cerca)
        val fc = cara(v, f, lado(cerca), charol, n)
        clipPath(fc) {
            val luz = lerp(charol, Color.White, 0.75f)
            drawPath(Path().apply { val ps = xs.filter { it > 0.13f }.map { f.p(it, boca(it) - 0.012f, cerca * anchoZapato(it)) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0.2f, 0.04f, cerca * 0.03f), luz, f.n(0.3f, 0.6f, cerca)).copy(alpha = 0.55f + 0.25f * v.noche), style = Stroke(0.0025f * k, cap = StrokeCap.Round))
            drawPath(Path().apply { val ps = xs.filter { it < 0.07f }.map { f.p(it + 0.006f, boca(it) - 0.018f, cerca * anchoZapato(it)) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0.03f, 0.09f, cerca * 0.03f), luz, f.n(-0.3f, 0.5f, cerca)).copy(alpha = 0.45f + 0.25f * v.noche), style = Stroke(0.0022f * k, cap = StrokeCap.Round))
            drawLine(v.c(f.w(0.1f, 0.04f, cerca * 0.03f), lerp(charol, C.LAMPARA, 0.6f), f.n(0f, 1f, cerca)).copy(alpha = 0.5f), f.p(0.02f, planta(0.02f) + 0.006f, cerca * anchoZapato(0.02f)), f.p(0.23f, planta(0.23f) + 0.006f, cerca * anchoZapato(0.23f)), 0.002f * k)
        }
        // El ribete de la boca, que coge luz.
        drawPath(Path().apply { val ps = xs.map { f.p(it, boca(it), cerca * anchoZapato(it) * 0.92f) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(0.1f, 0.08f, 0f), lerp(charol, C.MARFIL, 0.45f), f.n(0f, 1f, cerca)), style = Stroke(0.0025f * k))
        // La tira en T: del empeine a la pulsera, y la pulsera, que cruza por encima, con su botón de oro.
        val xp = 0.03f; val yp = TACON + 0.044f
        linea(v, f, V3(0.13f, 0.06f, 0f), V3(xp + 0.004f, yp, 0f), charol, ARRIBA, 0.011f)
        linea(v, f, V3(0.13f, 0.06f, 0f), V3(xp + 0.004f, yp, 0f), lerp(charol, Color.White, 0.6f), V3(0f, 1f, cerca), 0.002f, 0.5f)
        drawPath(Path().apply { val ps = (0..10).map { i -> val a = PI_F * i / 10f; f.p(xp, yp + 0.01f * sin(a), cos(a) * anchoZapato(xp) * 1.02f) }; moveTo(ps[0].x, ps[0].y); for (q in ps) lineTo(q.x, q.y) }, v.c(f.w(xp, yp, 0f), charol, ARRIBA), style = Stroke(0.009f * k, cap = StrokeCap.Round))
        val bt = f.w(xp, yp - 0.004f, cerca * anchoZapato(xp) * 1.05f)
        drawCircle(v.c(bt, C.ORO, f.n(0f, 0.3f, cerca)), 0.0055f * k, CamDeco.p(bt))
        drawCircle(v.c(bt, lerp(C.ORO, Color.White, 0.6f), f.n(0f, 0.5f, cerca)).copy(alpha = 0.7f), 0.0022f * k, CamDeco.p(bt) - Offset(0.0015f * k, 0.0015f * k))
    }

    // =================================================================
    //  El piano: el ramo
    // =================================================================

    /** Un ramo de rosas rojas envuelto en papel y atado con cinta de raso coral, tumbado de través. */
    private fun DrawScope.ramo(v: VistaDeco, f: Marco3) {
        val k = CamDeco.k(f.o.z)
        // Su sombra en la tapa.
        drawPath(poligono(aroL(f, 0.2f, 0.001f, 0.0f, 0.22f, 0.07f, 0f, 2f * PI_F, 20)), lerp(C.SOMBRA, C.LACA, 0.4f).copy(alpha = 0.4f), blendMode = BlendMode.Multiply)
        // Los tallos que asoman por el extremo cercano.
        for (j in 0..4) linea(v, f, V3(0.0f, 0.012f, -0.012f + j * 0.006f), V3(-0.05f, 0.008f + 0.002f * j, -0.02f + j * 0.01f), C.JADE, ARRIBA, 0.004f)
        // El cucurucho de papel, del estrecho al ancho.
        val papel = cara(v, f, listOf(V3(0.0f, 0.004f, -0.016f), V3(0.0f, 0.03f, 0.0f), V3(0.0f, 0.004f, 0.016f), V3(0.22f, 0.004f, 0.075f), V3(0.24f, 0.07f, 0.0f), V3(0.22f, 0.004f, -0.075f)), lerp(C.MARFIL, C.ROBLE, 0.12f), V3(0f, 1f, -0.5f))
        clipPath(papel) { linea(v, f, V3(0.0f, 0.03f, 0f), V3(0.24f, 0.07f, 0f), lerp(C.MARFIL, Color.White, 0.5f), V3(0f, 1f, -0.3f), 0.003f, 0.6f)
            linea(v, f, V3(0.02f, 0.006f, -0.02f), V3(0.2f, 0.01f, -0.07f), lerp(C.MARFIL, C.SOMBRA, 0.5f), FRENTE, 0.002f, 0.5f) }
        // Las hojas y las rosas, del fondo hacia delante.
        val az = Azar(Semilla.de("deco", "ramo"))
        repeat(6) { val c = V3(0.24f + az.entre(0f, 0.05f), 0.03f + az.entre(0f, 0.04f), az.entre(-0.08f, 0.08f)); val q = f.p(c.x, c.y, c.z)
            drawOval(v.c(f.w(c.x, c.y, c.z), lerp(C.JADE, C.ROBLE, 0.15f), V3(0f, 1f, -0.4f)), q - Offset(0.02f * k, 0.008f * k), Size(0.04f * k, 0.016f * k)) }
        val rosas = (0..6).map { V3(0.27f + az.entre(0f, 0.06f), 0.04f + az.entre(0f, 0.05f), az.entre(-0.06f, 0.06f)) }.sortedByDescending { it.z }
        for (c in rosas) {
            val w = f.w(c.x, c.y, c.z); val q = CamDeco.p(w); val r = 0.028f * k
            drawCircle(v.c(w, lerp(ROSA, C.LACA, 0.35f), f.n(0f, 0.3f, -1f)), r, q)
            drawCircle(v.c(w, ROSA, f.n(-0.3f, 0.8f, -0.5f)), r * 0.78f, q - Offset(r * 0.12f, r * 0.15f))
            drawArc(v.c(w, lerp(ROSA, C.LACA, 0.45f), ARRIBA), 200f, 250f, false, q - Offset(r * 0.45f, r * 0.5f), Size(r * 0.8f, r * 0.7f), style = Stroke(0.4f))
            drawCircle(v.c(w, lerp(ROSA, C.MARFIL, 0.35f), f.n(-0.5f, 1f, -0.5f)).copy(alpha = 0.6f), r * 0.25f, q - Offset(r * 0.32f, r * 0.38f))
        }
        // La cinta: un lazo coral con sus dos caídas.
        val lz = V3(0.07f, 0.03f, 0f); val pl = f.p(lz.x, lz.y, lz.z)
        val col = v.c(f.w(lz.x, lz.y, lz.z), C.CORAL, V3(0f, 1f, -0.5f))
        for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(pl.x, pl.y); cubicTo(pl.x + s * 0.02f * k, pl.y - 0.03f * k, pl.x + s * 0.05f * k, pl.y - 0.005f * k, pl.x + s * 0.008f * k, pl.y + 0.002f * k); close() }, col)
        linea(v, f, lz, V3(0.05f, 0.004f, -0.06f), C.CORAL, ARRIBA, 0.006f)
        linea(v, f, lz, V3(0.1f, 0.004f, -0.065f), C.CORAL, ARRIBA, 0.006f)
        drawCircle(lerp(col, C.LACA, 0.2f), 0.007f * k, pl)
    }
}
