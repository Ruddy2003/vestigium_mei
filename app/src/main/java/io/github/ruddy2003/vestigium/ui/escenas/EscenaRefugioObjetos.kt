package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de la residente, en la sala de bombas. Los chicos van sobre su soporte, la bancada, en la
 * esquina de delante a la derecha, donde la lámpara los alumbra a mediodía y la luz del portón de noche; los grandes van
 * junto a la punta de la bancada: de pie en el suelo y recostados en su canto, o echados sobre su punta.
 *
 * **Se pintan en la sala, con su luz** (como los objetos de Art déco): el punto que recibe cada función y su escala se
 * vuelven a llevar a la sala con la cámara del cuadro ([enLaSala]) y cada cara toma su color con la luz de ese sitio
 * ([VistaRefugio.c]): la penumbra de color, la lámpara de jaula por su lado (cálida), lo frío que entra por el portón por
 * el otro, y de noche el violeta de los tanques, los pilotos ámbar y la guardia. Su sombra en la tapa (o en el suelo) la
 * tumba cada luz que tumba sombras en el cuadro, desde su sitio ([TrazoObjeto.sombra]). Por eso [sobreElSoporte] y
 * [grande] no usan el `Pincel` que reciben: la luz la sacan de [VistaRefugio].
 *
 * - **Chicos** ([ChicosRefugio], `EscenaRefugioObjetosChicos.kt`): el disco de diez pulgadas medio fuera de su funda de
 *   papel, de pie contra el tocadiscos de la sala; la linterna de dinamo con su manivela; el contador de radiación colgado
 *   de su correa del canto de la tapa, con su lucecita verde encendida; la ración (la tarjeta perforada apoyada en una lata
 *   de antes de la guerra); el cuaderno del turno sobre los tomos viejos; la cajita de las pastillas de yodo, abierta; la
 *   foto de fuera en su carpetilla de cartón, de pie; y dos flores de papel de hojas de libro, pintadas.
 * - **Grandes** ([GRANDES], [GrandesRefugio], `EscenaRefugioObjetosGrandes.kt`): la llave de válvulas, recostada en el
 *   canto; el mono azul y la manta gris, echados sobre la punta de la bancada; y el tocadiscos del comedor, de pie en el
 *   suelo junto a ella, abierto, con su pincel.
 *
 * **Para que nada salga dos veces:** cinco objetos son también piezas de la sala ([YA_EN_LA_SALA], que es
 * `EscenaRefugio.DE_LA_SALA`): el tocadiscos pequeño y el cuaderno del turno (en la bancada), el mono (en su gancho), la
 * llave de válvulas (apoyada en la bomba grande) y las linternas de dinamo (en su percha). Este contrato los dibuja todos
 * ([DIBUJADOS]), así que el día que uno es el objeto la sala no pinta el suyo (`EscenaRefugio.laSalaPinta`) y el objeto
 * sale aquí, bien a la vista. El `tocadiscos` del objeto es el del comedor (la caja de madera grande), no el pequeño de la
 * sala: ese día la sala quita el suyo y el del comedor está de pie junto a la bancada. El día que se rompe el del comedor
 * (`tocadiscos`, 20 de septiembre de 2028) la sala ya lo tiene abierto en la bancada: si además es el objeto, el contrato
 * no pinta otro ([sitio] da [Sitio.EN_LA_SALA]). Otros dos tienen su pareja en el cuadro y **no** se quitan: la `flor`
 * del objeto es de papel (la de verdad, la de Noa, va en su frasco desde `vuelven`, y pueden coincidir), y el `disco` del
 * objeto es el que no tiene nadie (el de la sala va en su funda, plana junto al tocadiscos).
 *
 * **[Diseño propio]**: cómo es cada uno y dónde va; qué los acompaña; los colores. Van algo más grandes que de verdad
 * para que se lean a tamaño de teléfono (cada uno dice cuánto). Sin personas, sin letras, sin cifras.
 */
internal object ObjetosRefugio {
    /** Los doce objetos del calendario de la residente (`AnioG7.kt`). */
    val TODOS = listOf("llave", "disco", "linterna", "mono", "contador", "racion", "cuaderno", "pastilla", "manta", "foto", "tocadiscos", "flor")

    /** Los grandes: no caben sobre la bancada (la llave larga como un brazo, el mono, la manta, el tocadiscos del comedor). */
    val GRANDES = setOf("llave", "mono", "manta", "tocadiscos")

    /** Los objetos que ya están en la sala como piezas suyas. */
    val YA_EN_LA_SALA: Set<String> get() = EscenaRefugio.DE_LA_SALA

    /** Los objetos de la sala que este contrato dibuja él mismo el día que tocan: todos (la sala deja de pintar el suyo). */
    val DIBUJADOS: Set<String> = setOf("tocadiscos", "cuaderno", "mono", "llave", "linterna")

    /** Si el objeto de hoy es de los grandes (no cabe sobre la bancada y va de pie, recostado contra ella, o echado encima). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /** Dónde sale hoy el objeto: en ningún sitio (no hay), sobre el soporte, junto a la bancada, o lo pinta ya la sala. */
    enum class Sitio { NINGUNO, SOPORTE, GRANDE, EN_LA_SALA }

    /**
     * Dónde sale hoy el objeto. El tocadiscos el día que se rompe lo pinta la sala (abierto en la bancada); los demás, este
     * contrato: los grandes con [grande], los chicos con [sobreElSoporte].
     */
    fun sitio(m: Momento): Sitio {
        val id = m.ficha.objeto ?: return Sitio.NINGUNO
        if (id !in TODOS) return Sitio.NINGUNO
        if (id == "tocadiscos" && EscenaRefugio.tocadiscosRoto(m)) return Sitio.EN_LA_SALA
        return if (id in GRANDES) Sitio.GRANDE else Sitio.SOPORTE
    }

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el punto de la tapa de la bancada donde se apoya; [escala] 1 es el tamaño del primer plano (120 unidades
     * del lienzo por metro). La luz la toma de la sala en ese sitio ([VistaRefugio]), no de [p].
     */
    @Suppress("UNUSED_PARAMETER")
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {
        if (sitio(m) != Sitio.SOPORTE) return
        chico(VistaRefugio(m), m.ficha.objeto, sobre, escala)
    }

    /** El objeto chico [id] sobre [sobre], con la luz de [v] (lo mismo que [sobreElSoporte]; para las pruebas y las hojas). */
    fun DrawScope.chico(v: VistaRefugio, id: String?, sobre: Offset, escala: Float = 1f) {
        if (id == null || id in GRANDES || id !in TODOS) return
        val o = enLaSala(sobre, escala)
        val tapa = TrazoObjeto.tapaBajo(o)
        with(ChicosRefugio) {
            when (id) {
                "disco" -> disco(v, o, tapa)
                "linterna" -> linterna(v, o, tapa)
                "contador" -> contador(v, o, tapa)
                "racion" -> racion(v, o, tapa)
                "cuaderno" -> cuaderno(v, o, tapa, EscenaRefugio.cuadernoAbierto(v.m))
                "pastilla" -> pastilla(v, o, tapa)
                "foto" -> foto(v, o, tapa)
                "flor" -> flor(v, o, tapa)
            }
        }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] (el suelo, junto a la punta de la bancada) y se recuesta en [apoyo] (el canto
     * de la bancada, 5 cm por debajo de la tapa). Devuelve null si hoy no hay objeto o es de los chicos. La [Cosa] trae su
     * silueta (para quien quiera tumbarle la sombra) y su pintura, que ya lleva su propia sombra en el suelo y en la tapa.
     */
    @Suppress("UNUSED_PARAMETER")
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (sitio(m) != Sitio.GRANDE) return null
        return grande(VistaRefugio(m), m.ficha.objeto, pie, apoyo, escala)
    }

    /** El objeto grande [id] con la luz de [v] (lo mismo que el de arriba; para las pruebas y las hojas). */
    fun grande(v: VistaRefugio, id: String?, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (id == null || id !in GRANDES) return null
        val suelo = enLaSala(pie, escala).let { V3(it.x, 0f, it.z) }
        val canto = enLaSala(apoyo, CamRefugio.F / (120f * CamRefugio.hondo(suelo).coerceAtLeast(0.6f)))
        return with(GrandesRefugio) {
            when (id) {
                "llave" -> llave(v, suelo, canto, pie)
                "mono" -> mono(v, suelo, canto, pie)
                "manta" -> manta(v, suelo, canto, pie)
                "tocadiscos" -> tocadiscos(v, suelo, canto, pie)
                else -> null
            }
        }
    }

    /**
     * El punto de la sala que la cámara pone en [s] a la [escala] dada: la escala dice a qué hondo está (la sala pasa
     * `CamRefugio.k(punto) / 120`), y con el hondo, el punto del lienzo da el de la sala.
     */
    fun enLaSala(s: Offset, escala: Float): V3 {
        val K = CamRefugio
        val zc = K.F / (120f * escala.coerceAtLeast(0.05f))
        val xc = (s.x - K.CX) * zc / K.F
        val y = K.OJO - (s.y - K.CY) * zc / K.F
        return V3(xc * K.COS - zc * K.SIN, y, xc * K.SIN + zc * K.COS)
    }
}

/**
 * El sitio de un objeto en la sala: su origen [o] (en metros), su [giro] alrededor de la vertical (0: alineado con la
 * sala, como la bancada; hacia los 0,18 rad mira de frente a los ojos) y cuánto se [amplia] para que se lea a tamaño de
 * teléfono. Sus medidas van en metros del objeto: x a su derecha, y hacia arriba, z hacia su espalda.
 */
internal class MarcoObjeto(val o: V3, val giro: Float, val amplia: Float = 1f) {
    private val cg = cos(giro); private val sg = sin(giro)

    /** Un punto del objeto en la sala. */
    fun w(x: Float, y: Float, z: Float): V3 = V3(o.x + (x * cg - z * sg) * amplia, o.y + y * amplia, o.z + (x * sg + z * cg) * amplia)
    fun w(q: V3): V3 = w(q.x, q.y, q.z)

    /** Un punto del objeto en el lienzo. */
    fun p(x: Float, y: Float, z: Float): Offset = CamRefugio.p(w(x, y, z))
    fun p(q: V3): Offset = CamRefugio.p(w(q))

    /** Una dirección del objeto en la sala (unitaria). */
    fun n(x: Float, y: Float, z: Float): V3 = CamRefugio.unidad(V3(x * cg - z * sg, y, x * sg + z * cg))
    fun n(q: V3): V3 = n(q.x, q.y, q.z)

    /** Cuántas unidades del lienzo mide un metro del objeto en su origen. */
    val k: Float get() = CamRefugio.k(o) * amplia

    /** Un marco hijo: con origen en [x], [y], [z] de este, girado [giro2] más y ampliado [amp] veces más. */
    fun mas(x: Float, y: Float, z: Float, giro2: Float = 0f, amp: Float = 1f) = MarcoObjeto(w(x, y, z), giro + giro2, amplia * amp)

    /** La dirección (en las medidas del objeto, horizontal) de su punto [q] hacia los ojos. */
    fun haciaLosOjos(q: V3): V3 {
        val a = w(q); val dx = -a.x; val dz = -a.z
        // De la sala al objeto: el giro al revés.
        return CamRefugio.unidad(V3(dx * cg + dz * sg, 0f, -dx * sg + dz * cg))
    }
}

/**
 * Las primitivas de los objetos: caras, cajas, cilindros, discos, rayas y aros en las medidas del objeto ([MarcoObjeto]),
 * cada una con la luz de su sitio en la sala ([VistaRefugio.c]), y su sombra en la tapa o en el suelo.
 */
internal object TrazoObjeto {
    private val K = CamRefugio
    private val C = ColorRefugio
    private val S = SalaRefugio

    /** El color de [propio] en el punto [q] del objeto, con su cara mirando a [n] (del objeto; nulo: de todos lados). */
    fun luz(v: VistaRefugio, f: MarcoObjeto, q: V3, n: V3, propio: Color): Color =
        v.c(f.w(q), if (n.x == 0f && n.y == 0f && n.z == 0f) K.NADA else f.n(n), propio)

    /**
     * Una cara plana del objeto: sus puntos y su normal, en las medidas del objeto. Si da la espalda a los ojos se alumbra
     * por el otro lado (el papel, la tela y la chapa fina se ven por las dos caras). Devuelve su forma en el lienzo.
     */
    fun DrawScope.cara(v: VistaRefugio, f: MarcoObjeto, pts: List<V3>, n: V3, propio: Color, alfa: Float = 1f): Path {
        val w = pts.map { f.w(it) }
        val forma = K.forma(w); if (forma.isEmpty) return forma
        val c = K.centro(w); var nn = f.n(n); if (!K.seVe(c, nn)) nn = nn * -1f
        drawPath(forma, v.c(c, nn, propio).copy(alpha = (propio.alpha * alfa).coerceIn(0f, 1f)))
        return forma
    }

    /** Una cara con un degradado entre la luz de su punto [a] y la de [b] (del objeto): así se ve cómo cae la luz. */
    fun DrawScope.caraDegradada(v: VistaRefugio, f: MarcoObjeto, pts: List<V3>, n: V3, propio: Color, a: V3, b: V3, alfa: Float = 1f): Path {
        val w = pts.map { f.w(it) }
        val forma = K.forma(w); if (forma.isEmpty) return forma
        var nn = f.n(n); if (!K.seVe(K.centro(w), nn)) nn = nn * -1f
        val pa = f.p(a); val pb = f.p(b)
        val ca = v.c(f.w(a), nn, propio).copy(alpha = alfa); val cb = v.c(f.w(b), nn, propio).copy(alpha = alfa)
        if ((pa - pb).getDistance() < 0.5f) drawPath(forma, ca) else drawPath(forma, Brush.linearGradient(listOf(ca, cb), pa, pb))
        return forma
    }

    /**
     * Una caja del objeto, de [x0] a [x1], de [y0] a [y1] y de [z0] a [z1]: las caras que se ven, de la más lejana a la más
     * cercana, cada una con su luz (las de los lados, degradadas de arriba abajo). [arriba] es el color de la tapa.
     */
    fun DrawScope.caja(v: VistaRefugio, f: MarcoObjeto, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, propio: Color, arriba: Color = propio) {
        val caras = listOf(
            Triple(listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)), V3(0f, 1f, 0f), arriba),
            Triple(listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)), V3(0f, 0f, -1f), propio),
            Triple(listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)), V3(1f, 0f, 0f), propio),
            Triple(listOf(V3(x0, y0, z1), V3(x0, y0, z0), V3(x0, y1, z0), V3(x0, y1, z1)), V3(-1f, 0f, 0f), propio),
            Triple(listOf(V3(x0, y0, z1), V3(x1, y0, z1), V3(x1, y1, z1), V3(x0, y1, z1)), V3(0f, 0f, 1f), propio),
        )
        val vistas = caras.filter { K.seVe(f.w(K.centro(it.first)), f.n(it.second)) }.sortedByDescending { K.hondo(f.w(K.centro(it.first))) }
        for ((pts, n, col) in vistas) {
            if (n.y == 0f) { val c = K.centro(pts); caraDegradada(v, f, pts, n, col, V3(c.x, y1, c.z), V3(c.x, y0, c.z)) }
            else cara(v, f, pts, n, col)
        }
    }

    /** Un cilindro del objeto, de [a] a [b], de radio [r]: sus tiras con su luz (lado de luz y lado de sombra) y la tapa que se ve. */
    fun DrawScope.cil(v: VistaRefugio, f: MarcoObjeto, a: V3, b: V3, r: Float, propio: Color, tapa: Color = propio, lados: Int = 14, tapas: Boolean = true) {
        Trazo3.run { cilindro(v, f.w(a), f.w(b), r * f.amplia, propio, tapa, lados = lados, tapas = tapas) }
    }

    /** Un disco plano del objeto en [c], de radio [r], mirando a [n]; con [luz] falso, de su propio color (lo que da luz). */
    fun DrawScope.disco(v: VistaRefugio, f: MarcoObjeto, c: V3, n: V3, r: Float, propio: Color, lados: Int = 16, luz: Boolean = true) {
        val w = f.w(c); var nn = f.n(n); if (!K.seVe(w, nn)) nn = nn * -1f
        val forma = K.forma(K.circulo(w, nn, r * f.amplia, lados)); if (forma.isEmpty) return
        drawPath(forma, if (luz) v.c(w, nn, propio) else propio)
    }

    /** Una raya (un borde, un hilo, una costura) de [a] a [b], de [grueso] metros del objeto, de un color ya hecho. */
    fun DrawScope.raya(f: MarcoObjeto, a: V3, b: V3, grueso: Float, color: Color, punta: StrokeCap = StrokeCap.Round) {
        val wa = f.w(a); val wb = f.w(b)
        if (K.hondo(wa) < K.CERCA || K.hondo(wb) < K.CERCA) return
        drawLine(color, K.p(wa), K.p(wb), (grueso * f.amplia * K.k(K.medio(wa, wb))).coerceAtLeast(0.3f), punta)
    }

    /** Una raya con la luz de su sitio (mirando a [n]) y su [alfa]. */
    fun DrawScope.rayaLuz(v: VistaRefugio, f: MarcoObjeto, a: V3, b: V3, grueso: Float, propio: Color, n: V3 = V3(0f, 0f, 0f), alfa: Float = 1f, punta: StrokeCap = StrokeCap.Round) =
        raya(f, a, b, grueso, luz(v, f, K.medio(a, b), n, propio).copy(alpha = alfa), punta)

    /** Una línea quebrada del objeto por los puntos [pts], con la luz de su medio. */
    fun DrawScope.trazo(v: VistaRefugio, f: MarcoObjeto, pts: List<V3>, grueso: Float, propio: Color, n: V3 = V3(0f, 0f, 0f), alfa: Float = 1f) {
        if (pts.size < 2) return
        val q = pts.map { f.p(it) }
        val camino = Path().apply { moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y) }
        drawPath(camino, luz(v, f, pts[pts.size / 2], n, propio).copy(alpha = alfa), style = Stroke((grueso * f.k).coerceAtLeast(0.3f), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
    }

    /** Los puntos de un círculo del objeto alrededor de [c], en el plano de normal [n], de radio [r], de [a0] a [a1] radianes. */
    fun circulo(c: V3, n: V3, r: Float, lados: Int = 18, a0: Float = 0f, a1: Float = 6.2831855f): List<V3> = K.circulo(c, n, r, lados, a0, a1)

    /** El halo de algo que da luz, en [c] del objeto, de [radio] metros del objeto (en el aire: se suma a lo que hay). */
    fun DrawScope.halo(f: MarcoObjeto, c: V3, radio: Float, color: Color, fuerza: Float) {
        val p = f.p(c); val r = radio * f.k
        if (r < 0.4f || fuerza <= 0.01f) return
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = fuerza.coerceIn(0f, 1f)), color.copy(alpha = 0.3f * fuerza.coerceIn(0f, 1f)), Color.Transparent), p, r), r, p, blendMode = BlendMode.Plus)
    }

    /**
     * La tapa sobre la que está [o]: la de la bancada si [o] está en ella; si no (un objeto suelto, en una prueba), un
     * cuadrado de 70 cm a su alrededor. Sirve para recortar la sombra.
     */
    fun tapaBajo(o: V3): Path {
        val enLaBancada = o.x in S.BANCADA_X0 - 0.05f..S.BANCADA_X1 + 0.05f && o.z in S.BANCADA_Z0 - 0.05f..S.BANCADA_Z1 + 0.05f && kotlin.math.abs(o.y - S.BANCADA_Y) < 0.08f
        return if (enLaBancada) K.forma(listOf(V3(S.BANCADA_X0 - 0.02f, o.y, S.BANCADA_Z0 - 0.02f), V3(S.BANCADA_X1 + 0.02f, o.y, S.BANCADA_Z0 - 0.02f), V3(S.BANCADA_X1 + 0.02f, o.y, S.BANCADA_Z1 + 0.01f), V3(S.BANCADA_X0 - 0.02f, o.y, S.BANCADA_Z1 + 0.01f)))
        else K.forma(listOf(V3(o.x - 0.35f, o.y, o.z - 0.35f), V3(o.x + 0.35f, o.y, o.z - 0.35f), V3(o.x + 0.35f, o.y, o.z + 0.35f), V3(o.x - 0.35f, o.y, o.z + 0.35f)))
    }

    /** El canto de delante de la tapa bajo [o] (su z en la sala): el de la bancada, o 20 cm por delante si [o] no está en ella. */
    fun cantoDelante(o: V3): Float =
        if (o.x in S.BANCADA_X0..S.BANCADA_X1 && o.z in S.BANCADA_Z0..S.BANCADA_Z1) S.BANCADA_Z0 - 0.02f else o.z - 0.2f

    /**
     * La sombra de un objeto en el plano horizontal de altura [y0] (de la sala): la de contacto, apretada, bajo su [base]
     * (puntos del objeto que tocan el plano), y la que le tumba cada luz del cuadro que tumba sombras y llega a él, desde los
     * puntos de su [cuerpo]: cada una con la fuerza con que esa luz lo alumbra, recortada a [dentro] (la tapa).
     */
    fun DrawScope.sombra(v: VistaRefugio, f: MarcoObjeto, base: List<V3>, cuerpo: List<V3>, y0: Float, dentro: Path?, contacto: Float = 0.42f) {
        val bw = base.map { f.w(it) }; val cw = cuerpo.map { f.w(it) }
        if (cw.isEmpty()) return
        val pies = bw.map { K.p(it) }
        val alto = cw.maxOf { it.y }; val centro = K.centro(cw)
        val pinta: DrawScope.() -> Unit = {
            for (luz in v.tumban) {
                if (luz.p.y <= alto + 0.08f) continue
                val d = K.largo(luz.p - centro)
                var a = luz.fuerza / (1f + d * d / (luz.alcance * luz.alcance))
                luz.cono?.let { e -> val cs = K.punto(K.unidad(centro - luz.p), e); a *= ((cs - luz.coseFuera) / (luz.coseDentro - luz.coseFuera)).coerceIn(0f, 1f) }
                a = (a * 0.85f).coerceIn(0f, 0.55f); if (a < 0.04f) continue
                val tumbados = cw.map { q -> val t = ((luz.p.y - y0) / (luz.p.y - q.y).coerceAtLeast(0.02f)).coerceIn(1f, 7f); K.p(V3(luz.p.x + (q.x - luz.p.x) * t, y0, luz.p.z + (q.z - luz.p.z) * t)) }
                drawPath(poligono(K.casco(tumbados + pies)), C.SOMBRA.copy(alpha = a), blendMode = BlendMode.Multiply)
            }
            if (pies.size >= 3) {
                val c = poligono(K.casco(pies))
                drawPath(c, lerp(C.SOMBRA, C.TINTA, 0.4f).copy(alpha = contacto), blendMode = BlendMode.Multiply)
                drawPath(c, lerp(C.SOMBRA, C.TINTA, 0.4f).copy(alpha = contacto * 0.5f), style = Stroke(2f), blendMode = BlendMode.Multiply)
            }
        }
        if (dentro != null) clipPath(dentro) { pinta() } else pinta()
    }

    /** La envolvente, en el lienzo, de unos puntos del objeto (para la silueta de un grande). */
    fun silueta(f: MarcoObjeto, pts: List<V3>): Path = poligono(K.casco(pts.map { f.p(it) }))

    /** Un brillo: una pincelada clara de [a] a [b] (del objeto), más fuerte donde más luz le llega. */
    fun DrawScope.brillo(v: VistaRefugio, f: MarcoObjeto, a: V3, b: V3, grueso: Float, propio: Color, n: V3, fuerza: Float = 0.7f) {
        val c = luz(v, f, K.medio(a, b), n, lerp(propio, Color.White, 0.65f))
        val l = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
        raya(f, a, b, grueso, c.copy(alpha = (fuerza * (0.25f + 0.9f * l)).coerceIn(0f, 1f)))
    }

    /** La luminancia de un color (para saber qué lado alumbra más). */
    fun lum(c: Color): Float = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
}
