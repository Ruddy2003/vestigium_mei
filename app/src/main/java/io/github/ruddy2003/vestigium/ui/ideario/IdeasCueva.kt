package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Prehistoria
// =====================================================================

/**
 * Prehistoria: la pared del fondo de la cueva donde vive el grupo, en el Paleolítico superior. En la cueva no hay muebles: la
 * roca ES el mueble. Es caliza en estratos inclinados, con abultamientos, dos grietas y una colada de calcita húmeda que baja
 * por la derecha desde arriba hasta el suelo. La luz de día entra rasante desde la boca, que queda fuera del cuadro a la
 * izquierda, y saca el relieve de cada saliente. Arriba, en el abultamiento grande, el panel pintado (lo primero que se ve):
 * un bisonte de ocre rojo con la giba de manganeso y un caballo amarillo que se le encima por la grupa, una hilera de puntos
 * rojos y tres manos en negativo sopladas con ocre; donde la roca se desconchó, el pigmento se perdió. En la repisa natural
 * de la derecha: la lámpara de arenisca con su sebo y su mecha (y el hollín que deja en la pared), una concha con ocre,
 * agujas de hueso, la piedra de moler con el polvo rojo, su mano y dos terrones, y un núcleo con sus hojas de sílex. En la
 * repisa baja de la izquierda, un fardo de piel atado con tendón y el propulsor de asta apoyado; del saliente de la calcita
 * cuelga un collar de dientes y conchas. Al pie, el suelo de tierra con una piel de reno extendida, el fogón con su cerco de
 * piedras, brasas y un hueso quemado, y la leña apilada, cortada por el borde. Cada tema es un nicho natural de la roca, no
 * tallado a escuadra: un nicho en arco con las lajas de pie, una grieta ancha con las lajas apiladas de plano o una repisa
 * bajo una visera con las lajas en abanico, según la semilla, cada uno con algo suyo (un terrón de ocre, una concha, un
 * hueso grabado) y más lajas cuantas más fichas; cada nicho tiene su tamaño y está corrido a un lado según la semilla, para
 * que no se lea la rejilla. Las lajas son piedra: caliza crema grisácea con su canto, desportilladas (alguna rota), con rayas,
 * puntos, un zigzag de ocre o líneas grabadas. A veces queda algo fuera, en el labio (un hueso, un raspador, un terrón con
 * la mancha que dejó en la roca). Entre los nichos, la roca: escalones que arrojan sombra, hollín de lámparas viejas, coladas
 * cortas, grietas que bajan de un nicho a otro, desconchones y pintura chica a medio perder (una cabra, un ciervo, una mano,
 * trazos de dedos, una retícula, puntos, un claviforme). El nombre va pintado con ocre debajo, sobre la roca más lisa y
 * clara, pulida por el roce y lavada por la calcita, sin contorno.
 * Lo que no tiene tema tiene las lajas revueltas y un trazo de carbón en vez de los puntos de ocre; un hueco sin tema es el
 * nicho vacío, con una telaraña o una mancha de hollín. De noche la cueva es negro pardo y solo alumbran la llama de la
 * lámpara, arriba (el caballo y el lomo del bisonte vibran, la cabeza se pierde), y las brasas del fogón, abajo, en rojo
 * hondo: los nichos de en medio quedan entre las dos luces, casi perdidos. Por dentro, el fondo de un nicho con ceniza y
 * manos sopladas. Las fichas son lajas de caliza, cada una con su contorno desportillado.
 */
object IdeasCueva : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la cueva
    /** La caliza en tres valores: la que coge la luz, la de en medio y la de sombra. */
    private val CALIZA_LUZ = Color(0xFFE4D5B8)
    private val CALIZA_MED = Color(0xFFBFA888)
    private val CALIZA_OSC = Color(0xFF8A735D)
    /** Lo hondo de un nicho: pardo violeta, nunca negro. */
    private val HONDO = Color(0xFF433430)
    /** La calcita de la colada, húmeda. */
    private val CALCITA = Color(0xFFEFE6D2)
    /** El hollín de la lámpara y el carbón. */
    private val HOLLIN = Color(0xFF2E2420)
    /** El ocre rojo, el amarillo y el negro de manganeso de las pinturas. */
    private val OCRE_ROJO = Color(0xFFA63E22)
    private val OCRE_AMAR = Color(0xFFC88A36)
    private val MANGANESO = Color(0xFF231B18)
    /** Las lajas: caliza crema grisácea. */
    private val LAJA = Color(0xFFD6CCB8)
    /** El hueso, el asta y los dientes. */
    private val HUESO = Color(0xFFE6D9BC)
    /** La piel curtida y su pelo. */
    private val PIEL = Color(0xFF9C6C44)
    private val PIEL_OSC = Color(0xFF5E3D26)
    /** El sílex y la arenisca de la lámpara. */
    private val SILEX = Color(0xFF857565)
    private val ARENISCA = Color(0xFFC08B64)
    /** La tierra del suelo y la ceniza. */
    private val TIERRA = Color(0xFF7D6450)
    private val CENIZA = Color(0xFFA9A096)
    /** La llama de la lámpara y las brasas. */
    private val LLAMA = Color(0xFFFFC56E)
    private val BRASA = Color(0xFFE0562A)
    /** La noche de la cueva: negro pardo cálido. */
    private val NOCHE = Color(0xFF150E0B)
    /** La luz rasante del día, que entra por la boca: crema apenas fría. */
    private val LUZ_DIA = Color(0xFFF4EFE0)
    /** La roca lisa donde va el nombre: de día, clara; de noche, oscura (lejos) o tocada por el fuego (cerca). */
    private val PARCHE = Color(0xFFDFD0B4)
    private val PARCHE_NOCHE = Color(0xFF231812)
    private val PARCHE_FUEGO = Color(0xFF4A3325)
    /** La tinta del nombre: ocre rojo hondo de día (≥ 5:1 sobre [PARCHE]); ocre claro de noche (≥ 6:1 sobre el parche). */
    private val TINTA_DIA = Color(0xFF581707)
    private val TINTA_NOCHE = Color(0xFFF2B47C)

    // Lo de las fichas y el interior, como antes.
    private val ROCA = Color(0xFFB8A386)
    private val ROCA_LUZ = Color(0xFFDCCDB2)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF2F241B)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    private val FUEGO = Color(0xFFF4C76E)

    /** De noche la cueva solo la alumbra el fuego: la roca se oscurece (lo usan el interior y las fichas). */
    private fun roca(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color(0xFF1E150E), 0.55f) else c

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.64f
    override val base = 0.3f
    override val lado = 0.085f
    override val hueco = 0.045f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la lámpara, en fracciones del ancho del mueble. */
    private val LAMPARA = Offset(0.645f, 0.392f)
    /** El fogón: su x en fracciones del ancho y su altura dentro de la base (fracción de su alto). */
    private const val FOGON_X = 0.66f
    private const val FOGON_B = 0.64f
    /** Dónde empieza el suelo dentro de la base (fracción de su alto). */
    private const val SUELO = 0.22f

    private val anchoRel get() = (1f - 2 * lado - (cols - 1) * hueco) / cols
    /** El alto del mueble entero con [filas] filas, en fracciones del ancho (las mismas cuentas que `MuebleIdeas`). */
    private fun altoTotal(filas: Int): Float {
        val ah = anchoRel / proporcion
        return corona + hueco + filas * ah + (filas - 1) * hueco + hueco + base
    }
    private fun fogonRel(alto: Float) = Offset(FOGON_X, alto - base + base * FOGON_B)

    /** Cuánta luz llega a [p] (fracciones del ancho) y de qué color: la de la lámpara, amarilla; la de las brasas, roja. */
    private class Luz(val k: Float, val tinte: Color, val deAbajo: Boolean)

    private fun kLampara(p: Offset) = (1f - (p - LAMPARA).getDistance() / 1.0f).coerceIn(0f, 1f)
    private fun kFuego(p: Offset, alto: Float) = (1f - (p - fogonRel(alto)).getDistance() / 0.82f).coerceIn(0f, 1f)
    private fun luzEn(p: Offset, alto: Float): Luz {
        val a = kLampara(p); val b = kFuego(p, alto)
        val k = maxOf(a, b)
        return Luz(k, mezcla(LLAMA, BRASA, if (a + b > 0f) b / (a + b) else 0f), b > a)
    }
    private val SIN_LUZ = Luz(0f, LLAMA, false)

    /** Un color a la luz [l]: de día, tal cual; de noche, hundido en el negro pardo y calentado cerca del fuego. */
    private fun n(c: Color, oscuro: Boolean, l: Luz): Color {
        if (!oscuro) return c
        val q = l.k
        return mezcla(mezcla(c, NOCHE, 0.93f - 0.68f * q.pow(1.05f)), l.tinte, 0.36f * q)
    }

    /** La sombra: de día, pardo violeta; de noche, el negro pardo. */
    private fun sombra(oscuro: Boolean, a: Float) =
        if (oscuro) NOCHE.copy(alpha = (a * 1.2f).coerceIn(0f, 1f)) else mezcla(SOMBRA_MUEBLE, Color(0xFF4A3026), 0.35f).copy(alpha = a.coerceIn(0f, 1f))

    /** El claro de lo que mira a la luz: de día, la luz de la boca; de noche, el filo del fuego, según lo cerca que esté. */
    private fun claro(oscuro: Boolean, a: Float, l: Luz) =
        if (oscuro) l.tinte.copy(alpha = (a * 1.3f * l.k).coerceIn(0f, 1f)) else LUZ_DIA.copy(alpha = a.coerceIn(0f, 1f))

    /** Cuánta luz llega al nicho de [c]. Fuera del mueble, una luz media. */
    private fun luzDelHueco(c: EstadoCajon): Luz {
        if (c.cols != cols) return Luz(0.4f, LLAMA, false)
        val aw = anchoRel; val ah = aw / proporcion
        val centro = Offset(lado + c.col * (aw + hueco) + aw * 0.5f, corona + hueco + c.fila * (ah + hueco) + ah * 0.4f)
        val l = luzEn(centro, altoTotal(c.filas))
        // Un mínimo: aun el nicho más perdido deja contar sus lajas.
        return Luz(maxOf(l.k, 0.16f), l.tinte, l.deAbajo)
    }

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    /** Una figura cerrada que pasa suave por los puntos [q] (Catmull-Rom pasado a Bézier). */
    private fun cerrada(q: List<Offset>): Path = Path().apply {
        val m = q.size
        moveTo(q[0].x, q[0].y)
        for (i in 0 until m) {
            val a = q[(i - 1 + m) % m]; val b = q[i]; val c = q[(i + 1) % m]; val d = q[(i + 2) % m]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
        close()
    }

    /** Una línea abierta que pasa suave por [q]. */
    private fun abierta(q: List<Offset>): Path = Path().apply {
        moveTo(q[0].x, q[0].y)
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }

    /** Un contorno de piedra alrededor de [c]: [m] puntos con un poco de azar. */
    private fun piedra(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 9, rudo: Float = 0.22f): Path =
        cerrada(List(m) { k ->
            val a = 6.2832f * k / m + azar(semilla, k) * 0.35f
            val r = 1f - rudo + 2 * rudo * azar(semilla, k + 50)
            Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
        })

    /** Un polígono de aristas vivas (sílex, terrones): [m] vértices alrededor de [c]. */
    private fun poligono(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int): Path = Path().apply {
        for (k in 0 until m) {
            val a = 6.2832f * k / m + azar(semilla, k) * 0.5f
            val r = 0.7f + 0.3f * azar(semilla, k + 20)
            val p = Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /**
     * El volumen de gouache sobre algo ya recortado en [f]: el color, la media luna de sombra del lado contrario a la luz
     * ([lado]: -1 si la luz viene de la izquierda, 1 si de la derecha) y el claro del lado de la luz.
     */
    private fun DrawScope.volumen(f: Path, base: Color, oscuro: Boolean, l: Luz, lado: Float, fuerza: Float = 1f, claroA: Float = 0.32f) {
        val r = f.getBounds(); val a = r.width; val hh = r.height
        drawPath(f, base)
        drawPath(borde(f, lado * a * 0.24f, -hh * 0.06f), sombra(oscuro, 0.24f * fuerza))
        drawPath(borde(f, lado * a * 0.09f, -hh * 0.03f), sombra(oscuro, 0.2f * fuerza))
        drawPath(borde(f, -lado * a * 0.1f, hh * 0.16f), claro(oscuro, claroA, l))
    }

    /** Un saliente de la roca: su sombra arrojada abajo a la derecha, el color, el filo de luz arriba a la izquierda. */
    private fun DrawScope.saliente(f: Path, base: Color, oscuro: Boolean, l: Luz, d: Float, sombraFuera: Boolean = true) {
        if (sombraFuera) drawPath(corrido(f, d * 0.9f, d * 0.9f), sombra(oscuro, 0.22f))
        drawPath(f, base)
        drawPath(borde(f, -d, -d), sombra(oscuro, 0.2f))
        drawPath(borde(f, d * 0.8f, d * 0.8f), claro(oscuro, 0.45f, l))
    }

    // ---------------------------------------------------------------- el mueble: la pared de la cueva

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val altoR = alto / w
        fun p(x: Float, y: Float) = Offset(w * x, w * y)
        val lamp = LAMPARA * w
        val fog = fogonRel(altoR) * w
        fun luz(q: Offset) = luzEn(q / w, altoR)
        fun col(c: Color, q: Offset) = n(c, oscuro, luz(q))
        fun lado(q: Offset): Float {
            if (!oscuro) return -1f
            val s = if (kLampara(q / w) >= kFuego(q / w, altoR)) lamp else fog
            return if (s.x < q.x) -1f else 1f
        }
        /** Hacia dónde cae la sombra de algo en [q]: de día, a la derecha y abajo; de noche, lejos del fuego que más alumbra. */
        fun caida(q: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo, largo * 0.6f)
            val s = if (kLampara(q / w) >= kFuego(q / w, altoR)) lamp else fog
            val v = q - s; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * largo * 1.8f
        }
        val suelo = d.base.top + d.base.height * SUELO

        // ---- la roca: estratos de caliza, inclinados, de grueso desigual
        val tonos = listOf(CALIZA_MED, mezcla(CALIZA_MED, CALIZA_LUZ, 0.22f), mezcla(CALIZA_MED, CALIZA_OSC, 0.16f), mezcla(CALIZA_MED, CALIZA_LUZ, 0.1f), mezcla(CALIZA_MED, OCRE_AMAR, 0.1f))
        drawRect(n(CALIZA_MED, oscuro, SIN_LUZ), Offset.Zero, Size(w, suelo))
        val lineasEstrato = mutableListOf<Path>()
        run {
            var y = w * 0.05f; var i = 0
            while (y < suelo) {
                // Cada estrato se inclina y ondula a su modo, y alguno se adelgaza hasta casi perderse.
                val incl = w * (0.02f + 0.05f * azar(303, i))
                val pts = List(7) { k -> Offset(w * (-0.05f + 1.1f * k / 6f), y + incl * k / 6f + (azar(302 + i, k) - 0.5f) * w * 0.05f) }
                val linea = abierta(pts)
                val banda = Path().apply { addPath(linea); lineTo(w * 1.1f, suelo); lineTo(-w * 0.1f, suelo); close() }
                drawPath(banda, n(tonos[i % tonos.size], oscuro, SIN_LUZ))
                lineasEstrato += linea
                y += w * (0.07f + 0.2f * azar(301, i)); i++
            }
        }
        // Manchas de la roca: zonas más claras (calcita fina) y más pardas (arcilla), de borde irregular.
        for (k in 0 until (18 * suelo / w).toInt()) {
            val q = Offset(w * azar(605, k), suelo * azar(606, k))
            val f = piedra(q, w * (0.04f + 0.09f * azar(607, k)), w * (0.025f + 0.05f * azar(608, k)), 609 + k, 9, 0.3f)
            drawPath(f, n(if (k % 3 == 0) mezcla(CALIZA_MED, TIERRA, 0.35f) else mezcla(CALIZA_MED, CALIZA_LUZ, 0.4f), oscuro, SIN_LUZ).copy(alpha = 0.35f))
        }
        pinceladas(Rect(0f, 0f, w, suelo), n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.6f), oscuro, SIN_LUZ), 601, (40 * suelo / w).toInt(), u, 0.12f)
        pinceladas(Rect(0f, 0f, w, suelo), n(mezcla(CALIZA_OSC, HONDO, 0.3f), oscuro, SIN_LUZ), 602, (34 * suelo / w).toInt(), u, 0.1f)
        // El canto de cada estrato, solo a tramos (la roca no es una tabla): sombra bajo el escalón y luz encima.
        lineasEstrato.forEachIndexed { i, l ->
            val a = w * (0.05f + 0.5f * azar(631, i)); val b = a + w * (0.2f + 0.5f * azar(632, i))
            clipRect(a, -w, b, suelo) {
                drawPath(corrido(l, 0f, 1.6f * u), sombra(oscuro, 0.2f), style = Stroke((1f + 1.4f * azar(633, i)) * u, cap = StrokeCap.Round))
                drawPath(corrido(l, 0f, -0.2f * u), claro(oscuro, 0.18f, SIN_LUZ), style = Stroke(0.8f * u))
            }
            // Un tramo más corto y más flojo en otro sitio.
            val c0 = w * (0.6f * azar(634, i));
            clipRect(c0, -w, c0 + w * 0.15f, suelo) { drawPath(corrido(l, 0f, 1.2f * u), sombra(oscuro, 0.14f), style = Stroke(1f * u)) }
        }
        // El techo que se cierra arriba: la roca más honda, con su borde irregular.
        val techo = cerrada(List(10) { k -> Offset(w * (-0.05f + 1.1f * k / 9f), w * (0.012f + 0.03f * azar(611, k))) } + listOf(Offset(w * 1.1f, -w * 0.1f), Offset(-w * 0.1f, -w * 0.1f)))
        drawPath(techo, n(mezcla(CALIZA_OSC, HONDO, 0.35f), oscuro, SIN_LUZ))
        drawPath(borde(techo, 0f, -2.5f * u), claro(oscuro, 0.3f, SIN_LUZ))
        // Abultamientos de la roca, con su lado de luz y de sombra.
        // Abultamientos de la roca, anchos y bajos, con su lado de luz y de sombra (apenas: la roca no es de piedras sueltas).
        listOf(Triple(0.02f, 1.1f, 0.06f), Triple(0.03f, 1.95f, 0.07f), Triple(0.02f, 2.6f, 0.06f), Triple(0.8f, 0.3f, 0.06f))
            .forEachIndexed { i, (x, y, r) ->
                if (w * y < suelo - w * 0.05f) {
                    val f = piedra(p(x, y), w * r * 0.9f, w * r * 2.4f, 620 + i, 9, 0.18f)
                    drawPath(f, n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.18f), oscuro, SIN_LUZ))
                    drawPath(borde(f, -w * 0.012f, -w * 0.01f), sombra(oscuro, 0.12f))
                    drawPath(borde(f, w * 0.006f, w * 0.006f), claro(oscuro, 0.3f, SIN_LUZ))
                }
            }
        // La luz rasante que entra por la boca, arriba a la izquierda.
        if (!oscuro) {
            mancha(p(0.0f, 0.1f), w * 1.15f, LUZ_DIA.copy(alpha = 0.34f))
            mancha(p(0.0f, 0.05f), w * 0.5f, LUZ_DIA.copy(alpha = 0.3f))
        }

        // ---- el panel pintado, en el abultamiento grande
        val bulto = piedra(p(0.32f, 0.235f), w * 0.29f, w * 0.17f, 640, 11, 0.12f)
        drawPath(corrido(bulto, w * 0.012f, w * 0.012f), sombra(oscuro, 0.2f))
        drawPath(bulto, n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.62f), oscuro, SIN_LUZ))
        drawPath(borde(bulto, -w * 0.03f, -w * 0.022f), sombra(oscuro, 0.16f))
        drawPath(borde(bulto, -w * 0.012f, -w * 0.01f), sombra(oscuro, 0.14f))
        drawPath(borde(bulto, w * 0.012f, w * 0.014f), claro(oscuro, 0.5f, SIN_LUZ))
        pinceladas(bulto.getBounds(), n(CALIZA_LUZ, oscuro, SIN_LUZ), 641, 16, u, 0.14f)
        // Las manos sopladas, antes que los animales (son más antiguas).
        manoSoplada(p(0.07f, 0.135f), w * 0.034f, -18f, oscuro, luz(p(0.07f, 0.135f)), n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.45f), oscuro, luz(p(0.07f, 0.135f))))
        manoSoplada(p(0.15f, 0.08f), w * 0.029f, 12f, oscuro, luz(p(0.15f, 0.08f)), n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.62f), oscuro, luz(p(0.15f, 0.08f))))
        manoSoplada(p(0.735f, 0.215f), w * 0.03f, 22f, oscuro, luz(p(0.735f, 0.215f)), n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.3f), oscuro, luz(p(0.735f, 0.215f))))
        bisonte(p(0.3f, 0.245f), w * 0.158f, oscuro, ::luz)
        caballo(p(0.52f, 0.3f), w * 0.088f, oscuro, luz(p(0.52f, 0.3f)))
        // La hilera de puntos rojos, en arco, bajo el panel.
        for (i in 0 until 10) {
            val t = i / 9f
            val q = p(0.07f + 0.3f * t, 0.418f + 0.028f * t - 0.022f * sin(t * 3.1416f) + 0.004f * azar(651, i))
            drawCircle(col(OCRE_ROJO, q).copy(alpha = 0.88f), w * (0.0058f + 0.0018f * azar(652, i)), q)
        }
        // Donde la roca se desconchó, el pigmento se perdió.
        listOf(Triple(0.33f, 0.3f, 0.022f), Triple(0.43f, 0.215f, 0.016f), Triple(0.115f, 0.315f, 0.014f), Triple(0.6f, 0.33f, 0.012f)).forEachIndexed { i, (x, y, r) ->
            val f = piedra(p(x, y), w * r * 1.5f, w * r, 660 + i, 7, 0.3f)
            drawPath(f, n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.35f), oscuro, luz(p(x, y)).let { Luz(it.k * 0.75f, it.tinte, it.deAbajo) }))
            // El escalón del desconchado: sombra arriba a la izquierda (la luz no entra) y el canto claro abajo.
            drawPath(borde(f, w * r * 0.15f, w * r * 0.15f), sombra(oscuro, 0.2f))
            drawPath(borde(f, -w * r * 0.08f, -w * r * 0.1f), claro(oscuro, 0.25f, luz(p(x, y))))
        }
        // Las grietas.
        grieta(listOf(p(0.47f, 0.02f), p(0.455f, 0.06f), p(0.47f, 0.1f), p(0.452f, 0.13f)), oscuro, luz(p(0.46f, 0.07f)))
        // El hollín que la lámpara deja en la pared, encima de la llama.
        for (k in 0 until 6) {
            val r = w * (0.012f + 0.011f * k)
            val q = Offset(lamp.x + w * (0.006f * k + 0.01f * (azar(680, k) - 0.5f)), lamp.y - w * (0.03f + 0.035f * k))
            drawOval(HOLLIN.copy(alpha = if (oscuro) 0.07f else 0.05f - 0.004f * k), Offset(q.x - r * 0.8f, q.y - r * 1.4f), Size(r * 1.6f, r * 2.8f))
        }

        // ---- las masas del cuerpo, leídas por la luz rasante: zonas claras y entrantes en sombra, la caliza gris azulada, el óxido
        if (d.filas.isNotEmpty()) {
            val sepM = d.separacion
            val top = d.filas.first().top - sepM
            val bot = d.filas.last().bottom + sepM
            val alto0 = bot - top
            fun y(t: Float) = top + alto0 * t
            // Zonas claras, donde la roca sale hacia la luz de la boca.
            listOf(Triple(0.18f, 0.12f, 0.28f), Triple(0.62f, 0.44f, 0.24f), Triple(0.25f, 0.72f, 0.22f)).forEachIndexed { k, (x, t, r) ->
                drawPath(piedra(Offset(w * x, y(t)), w * r * 1.3f, alto0 * 0.1f, 1700 + k, 10, 0.3f), n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.8f), oscuro, SIN_LUZ).copy(alpha = 0.7f))
            }
            // Entrantes grandes en sombra.
            listOf(Triple(0.08f, 0.38f, 0.2f), Triple(0.78f, 0.7f, 0.22f), Triple(0.5f, 0.95f, 0.26f)).forEachIndexed { k, (x, t, r) ->
                drawPath(piedra(Offset(w * x, y(t)), w * r, alto0 * 0.11f, 1710 + k, 10, 0.3f), sombra(oscuro, 0.38f))
            }
            // Una zona de caliza más dura, gris azulada.
            drawPath(piedra(Offset(w * 0.7f, y(0.2f)), w * 0.24f, alto0 * 0.08f, 1720, 11, 0.3f), n(Color(0xFF9EA2A6), oscuro, SIN_LUZ).copy(alpha = 0.4f))
            // Verdín tenue junto a la colada, donde siempre está húmedo.
            listOf(0.33f, 0.61f, 0.86f).forEachIndexed { k, t ->
                drawPath(piedra(Offset(w * 0.9f, y(t)), w * 0.045f, alto0 * 0.06f, 1730 + k, 8, 0.35f), n(Color(0xFF7E8C58), oscuro, SIN_LUZ).copy(alpha = 0.28f))
            }
            // Las cornisas: tres repisas naturales que cruzan el cuerpo en diagonal. La cara de arriba, casi crema; el canto con
            // luz; la panza en sombra honda y la sombra que arroja sobre lo de abajo. De noche, la de abajo se enciende en rojo.
            listOf(floatArrayOf(-0.04f, 0.72f, 0.08f, 0.26f, 0.013f), floatArrayOf(0.28f, 0.96f, 0.56f, 0.4f, 0.017f), floatArrayOf(-0.04f, 0.6f, 0.74f, 0.9f, 0.014f)).forEachIndexed { k, cfg ->
                val (xa, xb, ta) = Triple(cfg[0], cfg[1], cfg[2]); val tb = cfg[3]; val g = w * cfg[4]
                val canto = List(9) { j ->
                    val t = j / 8f
                    // Las puntas se afinan y se meten en la pared.
                    Offset(w * (xa + (xb - xa) * t), y(ta + (tb - ta) * t) + w * 0.008f * (azar(1740 + k, j) - 0.5f))
                }
                val lCanto = luz(canto[4])
                // La sombra que arroja, en dos capas: honda junto a la panza y abierta hacia abajo.
                val panza = Path().apply {
                    moveTo(canto[0].x, canto[0].y); canto.drop(1).forEach { lineTo(it.x, it.y) }
                    for (j in 8 downTo 0) { val q = canto[j]; lineTo(q.x, q.y + g * (0.4f + 2.4f * sin(j / 8f * 3.1416f) + 0.6f * azar(1750 + k, j))) }
                    close()
                }
                val arrojada = Path().apply {
                    moveTo(canto[0].x, canto[0].y); canto.drop(1).forEach { lineTo(it.x, it.y) }
                    for (j in 8 downTo 0) { val q = canto[j]; lineTo(q.x + w * 0.02f, q.y + g * (1f + 5f * sin(j / 8f * 3.1416f) + 1.5f * azar(1760 + k, j))) }
                    close()
                }
                drawPath(arrojada, sombra(oscuro, 0.24f))
                drawPath(panza, sombra(oscuro, if (oscuro) 0.65f else 0.62f))
                // De noche, si el fuego de abajo la alcanza, la panza se enciende.
                if (oscuro && lCanto.deAbajo && lCanto.k > 0.05f) drawPath(panza, BRASA.copy(alpha = (0.55f * lCanto.k).coerceAtMost(0.45f)))
                // La cara de arriba, clara, que se pierde hacia arriba en la pared.
                val cara = Path().apply {
                    moveTo(canto[0].x, canto[0].y); canto.drop(1).forEach { lineTo(it.x, it.y) }
                    for (j in 8 downTo 0) { val q = canto[j]; lineTo(q.x - w * 0.01f, q.y - g * (0.3f + 1.8f * sin(j / 8f * 3.1416f) + 0.6f * azar(1770 + k, j))) }
                    close()
                }
                drawPath(cara, n(mezcla(CALIZA_LUZ, CALIZA_MED, 0.15f), oscuro, SIN_LUZ).copy(alpha = 0.8f))
                drawPath(abierta(canto), if (oscuro) claro(true, 0.4f, lCanto) else LUZ_DIA.copy(alpha = 0.85f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                // Una chorrera de óxido que baja desde la cornisa.
                val jo = 3 + Math.floorMod(k, 3)
                val xo = canto[jo].x; val yo = canto[jo].y + g * 2.4f
                drawPath(abierta(List(5) { j -> Offset(xo + w * 0.008f * sin(j * 1.3f + k), yo + alto0 * 0.06f * j) }),
                    n(mezcla(OCRE_AMAR, OCRE_ROJO, 0.3f), oscuro, SIN_LUZ).copy(alpha = 0.32f), style = Stroke(w * (0.012f + 0.006f * azar(1781, k)), cap = StrokeCap.Round))
            }
            // Una lengua de hollín, negra, que sube sobre un nicho donde ardió muchas veces una lámpara.
            d.huecos.getOrNull(2)?.let { hq ->
                val b0 = Offset(hq.center.x + hq.width * 0.1f, hq.top + hq.height * 0.3f)
                val lengua = cerrada(listOf(b0 + Offset(-w * 0.06f, 0f), b0 + Offset(-w * 0.04f, -w * 0.08f), b0 + Offset(-w * 0.01f, -w * 0.15f), b0 + Offset(w * 0.02f, -w * 0.2f),
                    b0 + Offset(w * 0.03f, -w * 0.12f), b0 + Offset(w * 0.06f, -w * 0.05f), b0 + Offset(w * 0.05f, 0f)))
                drawPath(lengua, HOLLIN.copy(alpha = 0.22f))
                drawPath(piedra(b0 + Offset(0f, -w * 0.05f), w * 0.04f, w * 0.06f, 1790, 8, 0.3f), HOLLIN.copy(alpha = 0.22f))
            }
        }

        // ---- la colada de calcita, por la derecha, de arriba al suelo
        colada(w, suelo, oscuro, ::luz)

        val margenL = this@IdeasCueva.lado
        // ---- la roca alrededor de los nichos: cejas que arrojan sombra, hollín, coladas cortas, grietas y desconchones
        val sep = d.separacion
        d.huecos.forEachIndexed { i, hq ->
            val colu = i % cols
            val s = 900 + 17 * i
            val siguiente = d.huecos.getOrNull(i + cols)
            // Hollín sobre algunos nichos, donde alguna vez ardió una lámpara.
            if (i % 4 == 1) for (k in 0 until 4) {
                val r = w * (0.025f + 0.016f * k)
                val q = Offset(hq.center.x + w * 0.02f * (azar(s, k) - 0.5f) + w * 0.006f * k, hq.top + hq.height * 0.25f - w * 0.028f * k)
                drawOval(HOLLIN.copy(alpha = if (oscuro) 0.1f else 0.07f), Offset(q.x - r * 1.1f, q.y - r), Size(r * 2.2f, r * 2f))
            }
            // Una grieta que baja por la junta y une este nicho con el de abajo.
            if (i % 3 == 0 && siguiente != null) {
                val x = if (colu == 0) hq.left - w * margenL * 0.45f else hq.left - sep * 0.5f
                val y0 = hq.top + hq.height * 0.15f; val y1 = siguiente.top + siguiente.height * 0.3f
                grieta(List(9) { k -> Offset(x + w * 0.02f * (azar(s, k + 10) - 0.5f), y0 + (y1 - y0) * (k + 0.4f * (azar(s, k + 20) - 0.5f)) / 8f) }, oscuro, luz(Offset(x, y0)))
            }
            // Una colada corta de calcita que escurre por la junta del medio, con su brillo.
            if (i % 4 == 2 || i == 3) {
                val x = if (colu == 0) hq.right + sep * 0.5f else hq.left - sep * 0.5f
                val y0 = hq.top + hq.height * (0.3f + 0.15f * azar(s, 20)); val y1 = hq.top + hq.height * 0.62f
                val a = sep * 0.48f
                val cc = cerrada(listOf(Offset(x - a * 0.15f, y0), Offset(x + a * 0.25f, y0 + (y1 - y0) * 0.1f), Offset(x + a * 0.7f, y0 + (y1 - y0) * 0.7f), Offset(x + a * 0.85f, y1 - a * 0.2f),
                    Offset(x + a * 0.45f, y1 + a * 0.15f), Offset(x + a * 0.05f, y1 - a * 0.05f), Offset(x - a * 0.4f, y1 + a * 0.2f), Offset(x - a * 0.8f, y1 - a * 0.25f), Offset(x - a * 0.5f, y0 + (y1 - y0) * 0.45f)))
                val l = luz(Offset(x, (y0 + y1) / 2))
                drawPath(corrido(cc, w * 0.004f, w * 0.006f), sombra(oscuro, 0.22f))
                volumen(cc, n(mezcla(CALCITA, CALIZA_MED, 0.5f), oscuro, l), oscuro, l, -1f, 0.6f, 0.45f)
                drawCircle(if (oscuro) l.tinte.copy(alpha = 0.8f * l.k) else Color.White.copy(alpha = 0.8f), w * 0.002f, Offset(x - a * 0.3f, y0 + (y1 - y0) * 0.4f))
                drawCircle(if (oscuro) l.tinte.copy(alpha = 0.8f * l.k) else Color.White.copy(alpha = 0.8f), w * 0.0018f, Offset(x, y1 - a * 0.1f))
            }
            // Un desconchón en el margen.
            if (i % 5 == 2) {
                val q = if (colu == 0) Offset(hq.left - w * margenL * 0.5f, hq.center.y) else Offset(hq.left - sep * 0.5f, hq.bottom - hq.height * 0.1f)
                val f = piedra(q, w * 0.022f, w * 0.03f, s + 30, 7, 0.3f)
                drawPath(f, n(mezcla(CALIZA_MED, CALIZA_LUZ, 0.55f), oscuro, SIN_LUZ))
                drawPath(borde(f, w * 0.004f, w * 0.005f), sombra(oscuro, 0.3f))
                drawPath(borde(f, -w * 0.003f, -w * 0.003f), claro(oscuro, 0.3f, luz(q)))
            }
            // Un escalón de la roca encima del nicho (no en todos, ni del mismo largo): la pared de arriba sale un poco; se lee por
            // la luz de su canto y por la sombra que arroja hacia abajo, sin contorno propio.
            if (i % 3 != 2) {
                val x0 = hq.left + hq.width * (-0.08f + 0.2f * azar(s, 1))
                val x1 = hq.right - hq.width * (0.0f + 0.4f * azar(s, 2))
                val y = hq.top - sep * 0.3f
                val canto = List(7) { k -> Offset(x0 + (x1 - x0) * k / 6f, y + w * 0.006f * (azar(s, k + 5) - 0.5f) + w * 0.005f * sin(k * 0.9f)) }
                val g = w * (0.012f + 0.01f * azar(s, 3))
                // La sombra: en capas, honda junto al canto y cada vez más rala hacia abajo; las puntas se afilan en la pared.
                for (k in 0 until 3) {
                    val baja = g * (0.9f + 1.1f * k)
                    val som = Path().apply {
                        moveTo(canto.first().x, canto.first().y)
                        canto.drop(1).forEach { lineTo(it.x, it.y) }
                        for (j in 5 downTo 1) { val t = j / 6f; val q = canto[j]; lineTo(q.x + w * 0.006f * k, q.y + baja * sin(t * 3.1416f)) }
                        close()
                    }
                    drawPath(som, sombra(oscuro, 0.11f))
                }
                drawPath(abierta(canto), if (oscuro) claro(true, 0.25f, luz(Offset((x0 + x1) / 2, y))) else LUZ_DIA.copy(alpha = 0.65f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            }
        }
        // ---- pintura chica repartida entre los nichos, a medio perder
        val fl = d.filas
        val margen = w * margenL * 0.5f
        val junta = w * 0.5f
        fl.getOrNull(1)?.let { fr -> animalito(Offset(margen, fr.center.y - w * 0.01f), w * 0.03f, false, oscuro, luz(Offset(margen, fr.center.y)), 1601) }
        fl.getOrNull(2)?.let { fr ->
            // Trazos digitales: los dedos pasados por la arcilla blanda de la pared.
            for (k in 0 until 4) {
                val x = margen - w * 0.016f + k * w * 0.01f
                val tr = abierta(List(5) { j -> Offset(x + w * 0.01f * sin(j * 1.6f + k * 0.4f), fr.top + fr.height * (0.25f + 0.1f * j)) })
                drawPath(tr, sombra(oscuro, 0.3f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                drawPath(corrido(tr, -1f * u, 0f), claro(oscuro, 0.35f, luz(Offset(x, fr.center.y))), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            }
            // Un signo en retícula, rojo, en la junta del medio.
            val q = Offset(junta, fr.center.y); val a = w * 0.016f
            val ocre = col(OCRE_ROJO, q).copy(alpha = 0.7f)
            for (k in 0..3) {
                drawLine(ocre, Offset(q.x - a + k * a * 0.66f, q.y - a * 1.3f), Offset(q.x - a + k * a * 0.66f + a * 0.1f, q.y + a * 1.3f), 1.4f * u, StrokeCap.Round)
                drawLine(ocre, Offset(q.x - a, q.y - a * 1.3f + k * a * 0.86f), Offset(q.x + a, q.y - a * 1.2f + k * a * 0.86f), 1.4f * u, StrokeCap.Round)
            }
        }
        fl.getOrNull(3)?.let { fr ->
            manoSoplada(Offset(margen, fr.center.y), w * 0.02f, 8f, oscuro, luz(Offset(margen, fr.center.y)), n(CALIZA_MED, oscuro, luz(Offset(margen, fr.center.y))))
            for (i in 0 until 4) {
                val q = Offset(junta + w * 0.003f * (azar(671, i) - 0.5f), fr.top + fr.height * (0.15f + 0.2f * i))
                drawCircle(col(OCRE_ROJO, q).copy(alpha = 0.6f + 0.25f * azar(672, i)), w * (0.004f + 0.003f * azar(673, i)), q)
            }
        }
        fl.getOrNull(4)?.let { fr ->
            val q = Offset(margen, fr.top + fr.height * 0.2f)
            val l = luz(q)
            drawPath(abierta(listOf(q, q + Offset(w * 0.003f, w * 0.05f), q + Offset(-w * 0.002f, w * 0.1f))), n(OCRE_ROJO, oscuro, l).copy(alpha = 0.75f), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
            drawOval(n(OCRE_ROJO, oscuro, l).copy(alpha = 0.75f), Offset(q.x - w * 0.002f, q.y + w * 0.025f), Size(w * 0.014f, w * 0.02f))
        }
        // Un ciervo chico bajo la repisa grande, en la sombra, medio borrado.
        animalito(p(0.6f, 0.585f), w * 0.034f, true, oscuro, luz(p(0.6f, 0.585f)), 1602)

        // ---- el halo de las dos luces, de noche (sobre la roca, debajo de las cosas)
        if (oscuro) {
            mancha(lamp, w * 0.95f, LLAMA.copy(alpha = 0.14f))
            mancha(lamp, w * 0.48f, LLAMA.copy(alpha = 0.22f))
            mancha(lamp + Offset(0f, -w * 0.02f), w * 0.17f, Color(0xFFFFDB98).copy(alpha = 0.34f))
        }

        // ---- el suelo de la cueva
        drawRect(n(TIERRA, oscuro, SIN_LUZ), Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), n(mezcla(TIERRA, CALIZA_LUZ, 0.4f), oscuro, SIN_LUZ), 701, 26, u, 0.14f)
        pinceladas(Rect(0f, suelo, w, alto), n(mezcla(TIERRA, HONDO, 0.5f), oscuro, SIN_LUZ), 702, 20, u, 0.14f)
        // El rincón: la pared se mete bajo el suelo, con su franja de cascajo y arcilla.
        drawRect(sombra(oscuro, 0.4f), Offset(0f, suelo), Size(w, 2f * u))
        drawRect(sombra(oscuro, 0.16f), Offset(0f, suelo + 2f * u), Size(w, w * 0.02f))
        if (oscuro) {
            mancha(fog, w * 1.0f, BRASA.copy(alpha = 0.16f))
            mancha(fog, w * 0.5f, BRASA.copy(alpha = 0.2f))
            mancha(fog + Offset(0f, -w * 0.02f), w * 0.2f, LLAMA.copy(alpha = 0.3f))
        }
        for (k in 0 until 14) {
            val q = Offset(w * azar(711, k), suelo + w * (0.004f + 0.022f * azar(712, k)))
            val r = w * (0.006f + 0.01f * azar(713, k))
            val f = piedra(q, r * 1.3f, r * 0.8f, 714 + k, 6, 0.25f)
            drawPath(corrido(f, r * 0.3f, r * 0.3f), sombra(oscuro, 0.3f))
            drawPath(f, col(mezcla(CALIZA_MED, TIERRA, azar(715, k)), q))
            drawPath(borde(f, r * 0.25f, r * 0.3f), claro(oscuro, 0.4f, luz(q)))
        }
        for (k in 0 until 18) {
            val q = Offset(w * azar(721, k), suelo + (alto - suelo) * (0.25f + 0.72f * azar(722, k)))
            val r = (1f + 2.2f * azar(723, k)) * u
            drawOval(sombra(oscuro, 0.3f), Offset(q.x - r + 0.8f * u, q.y - r * 0.3f), Size(2 * r, r))
            drawOval(col(mezcla(TIERRA, CALIZA_LUZ, 0.45f), q), Offset(q.x - r, q.y - r * 0.55f), Size(2 * r, r))
        }

        // ---- la repisa grande de la derecha, con su sombra en la pared
        val repisaY = w * 0.462f
        val cara = cerrada(listOf(p(0.415f, 0.452f), p(0.5f, 0.44f), p(0.62f, 0.444f), p(0.75f, 0.438f), p(0.88f, 0.443f), p(1.02f, 0.44f),
            p(1.02f, 0.505f), p(0.9f, 0.512f), p(0.78f, 0.5f), p(0.66f, 0.508f), p(0.55f, 0.497f), p(0.47f, 0.488f)))
        drawPath(corrido(cara, w * 0.012f, w * 0.03f), sombra(oscuro, 0.3f))
        drawPath(corrido(cara, w * 0.02f, w * 0.055f), sombra(oscuro, 0.14f))
        val luzRepisa = luz(p(0.65f, 0.47f))
        drawPath(cara, col(CALIZA_MED, p(0.65f, 0.47f)))
        drawPath(borde(cara, -w * 0.01f, -w * 0.02f), sombra(oscuro, 0.26f))
        val arriba = cerrada(listOf(p(0.415f, 0.452f), p(0.5f, 0.44f), p(0.62f, 0.444f), p(0.75f, 0.438f), p(0.88f, 0.443f), p(1.02f, 0.44f),
            p(1.02f, 0.466f), p(0.86f, 0.47f), p(0.72f, 0.466f), p(0.58f, 0.47f), p(0.46f, 0.466f)))
        drawPath(arriba, col(mezcla(CALIZA_LUZ, CALIZA_MED, 0.3f), p(0.65f, 0.45f)))
        drawPath(borde(arriba, 0f, w * 0.006f), claro(oscuro, 0.4f, luzRepisa))
        pinceladas(cara.getBounds(), col(CALIZA_OSC, p(0.7f, 0.48f)), 731, 10, u, 0.16f)

        // ---- la repisa baja de la izquierda
        val baja = cerrada(listOf(p(-0.02f, 0.608f), p(0.1f, 0.6f), p(0.24f, 0.604f), p(0.36f, 0.6f), p(0.45f, 0.612f),
            p(0.44f, 0.632f), p(0.34f, 0.646f), p(0.2f, 0.642f), p(0.08f, 0.648f), p(-0.02f, 0.645f)))
        drawPath(corrido(baja, w * 0.01f, w * 0.022f), sombra(oscuro, 0.3f))
        drawPath(baja, col(CALIZA_MED, p(0.2f, 0.62f)))
        drawPath(borde(baja, -w * 0.006f, -w * 0.014f), sombra(oscuro, 0.26f))
        drawPath(borde(baja, 0f, w * 0.008f), claro(oscuro, 0.45f, luz(p(0.2f, 0.61f))))

        // ---- el collar, colgado de un saliente sobre la calcita
        collar(p(0.845f, 0.075f), w, oscuro, luz(p(0.845f, 0.14f)), lado(p(0.845f, 0.14f)), caida(p(0.845f, 0.14f), w * 0.01f))

        // ---- lo de la repisa grande, de atrás adelante
        lampara(Offset(w * 0.585f, repisaY), w, oscuro, luz(p(0.6f, 0.43f)))
        concha(Offset(w * 0.69f, repisaY - w * 0.001f), w * 0.04f, oscuro, luz(p(0.69f, 0.45f)), lado(p(0.69f, 0.45f)))
        molienda(Offset(w * 0.805f, repisaY), w, oscuro, luz(p(0.81f, 0.45f)), lado(p(0.81f, 0.45f)), caida(p(0.81f, 0.44f), w * 0.01f))
        silex(Offset(w * 0.955f, repisaY), w, oscuro, luz(p(0.92f, 0.45f)), lado(p(0.92f, 0.45f)))
        agujas(Offset(w * 0.66f, repisaY + w * 0.008f), w, oscuro, luz(p(0.745f, 0.46f)))

        // ---- el fardo y el propulsor, en la repisa baja
        val pieFardo = Offset(w * 0.175f, w * 0.607f)
        fardo(pieFardo, w, oscuro, luz(pieFardo - Offset(0f, w * 0.06f)), lado(pieFardo - Offset(0f, w * 0.06f)), caida(pieFardo - Offset(0f, w * 0.06f), w * 0.016f))
        propulsor(p(0.372f, 0.606f), p(0.452f, 0.29f), w, oscuro, luz(p(0.42f, 0.42f)), caida(p(0.42f, 0.42f), w * 0.012f))

        // ---- bajo cada nicho, la roca lisa y clara donde va el nombre pintado: pulida por el roce y lavada por la calcita.
        // Sin contorno: se funde en la roca por capas cada vez más ralas.
        d.huecos.forEachIndexed { i, hq ->
            val r = Rect(hq.left, hq.top + hq.height * rotulo.top, hq.right, hq.top + hq.height * rotulo.bottom)
            val l = luz(r.center)
            val nucleo = if (oscuro) mezcla(PARCHE_NOCHE, mezcla(PARCHE_FUEGO, l.tinte, 0.12f), l.k) else mezcla(CALIZA_MED, PARCHE, 0.58f)
            // Una lasca pulida: más ancha por un lado, con un pico arriba o abajo, de caras rectas; distinta en cada nicho.
            val ancho = if (azar(800 + i, 0) < 0.5f) -1f else 1f
            val picoX = 0.2f + 0.6f * azar(800 + i, 1); val picoArriba = azar(800 + i, 2) < 0.5f
            val base = listOf(Triple(0f, 0f, 0), Triple(0.3f, 0f, 1), Triple(0.62f, 0f, 1), Triple(1f, 0f, 0), Triple(1f, 0.5f, 2), Triple(1f, 1f, 0),
                Triple(0.66f, 1f, 3), Triple(0.33f, 1f, 3), Triple(0f, 1f, 0), Triple(0f, 0.5f, 4))
            fun zona(e: Float, semilla: Int): Path = Path().apply {
                base.forEachIndexed { k, (fu, fv, lado4) ->
                    var x = r.left + r.width * fu; var y = r.top + r.height * fv
                    val dx = if (fu < 0.01f) -1f else if (fu > 0.99f) 1f else 0f
                    val dy = if (fv < 0.01f) -1f else if (fv > 0.99f) 1f else 0f
                    var extra = e + w * 0.008f * azar(semilla, k)
                    if (dx == ancho) extra += w * 0.035f
                    x += dx * extra; y += dy * extra * 0.7f
                    // El pico: la arista que sobresale arriba o abajo.
                    if ((lado4 == 1 && picoArriba) || (lado4 == 3 && !picoArriba)) {
                        val cerca = 1f - (kotlin.math.abs(fu - picoX) / 0.35f).coerceIn(0f, 1f)
                        y += dy * w * 0.03f * cerca
                    }
                    if (k == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            listOf(w * 0.036f to 0.1f, w * 0.024f to 0.18f, w * 0.014f to 0.3f, w * 0.006f to 0.5f, -w * 0.002f to 0.8f, -w * 0.01f to 1f).forEachIndexed { k, (e, a) ->
                drawPath(zona(e, 760 + 7 * i), nucleo.copy(alpha = a))
            }
            // El lavado: dos chorreras más claras que bajan desde el labio del nicho, y el grano de la piedra.
            if (!oscuro) for (k in 0 until 2) {
                val x = r.left + r.width * (0.2f + 0.6f * azar(790 + i, k))
                drawLine(LUZ_DIA.copy(alpha = 0.12f), Offset(x, r.top - hq.height * 0.05f), Offset(x + w * 0.004f, r.bottom), w * (0.01f + 0.008f * azar(791 + i, k)), StrokeCap.Round)
            }
            pinceladas(r, if (oscuro) NOCHE else CALIZA_LUZ, 780 + i, 7, u, 0.1f)
        }

        // ---- al pie: la piel, el fogón y la leña
        val fondoSuelo = alto - suelo
        pielExtendida(Offset(w * 0.24f, suelo + fondoSuelo * 0.55f), w * 0.2f, fondoSuelo * 0.36f, w, oscuro, luz(Offset(w * 0.24f, suelo + fondoSuelo * 0.5f)))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val f = lajaPath(r, 9)
            drawPath(corrido(f, 2f * u, 3f * u), sombra(oscuro, 0.4f))
            drawPath(f, if (oscuro) Color(0xFFBCA788) else CALIZA_LUZ)
            drawPath(borde(f, 2f * u, 2f * u), LUZ_DIA.copy(alpha = if (oscuro) 0.2f else 0.5f))
        }
        lena(Offset(w * 0.93f, suelo + fondoSuelo * 0.8f), w, oscuro, luz(Offset(w * 0.92f, suelo + fondoSuelo * 0.6f)))
        fogon(fog, w, oscuro)

        // ---- la llama de la lámpara, con su propio color
        if (oscuro) llama(Offset(w * 0.645f, w * 0.42f), w * 0.056f, false)
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- las cosas de la pared

    /** Una grieta: el hueco en sombra y, a su izquierda, el labio que coge la luz. */
    private fun DrawScope.grieta(q: List<Offset>, oscuro: Boolean, l: Luz) {
        // A tramos rectos, quebrada; más abierta en medio, con su ramal.
        val g = Path().apply { moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y) }
        drawPath(g, sombra(oscuro, 0.6f), style = Stroke(1.8f * density, cap = StrokeCap.Round, join = StrokeJoin.Miter))
        if (q.size > 4) {
            val m = q[q.size / 2]
            drawLine(sombra(oscuro, 0.5f), m, m + Offset(4f * density, 7f * density), 1.1f * density, StrokeCap.Round)
            drawLine(sombra(oscuro, 0.65f), q[q.size / 2 - 1], m, 2.8f * density, StrokeCap.Round)
        }
        drawPath(corrido(g, -1.4f * density, 0f), claro(oscuro, 0.4f, l), style = Stroke(0.8f * density, cap = StrokeCap.Round))
    }

    /** Una mano en negativo soplada: el ocre salpicado alrededor, espeso junto a la mano y ralo hacia fuera, y la mano en el color de la roca. */
    private fun DrawScope.manoSoplada(c: Offset, r: Float, giro: Float, oscuro: Boolean, l: Luz, roca: Color) {
        val o = n(OCRE_ROJO, oscuro, l)
        val sem = (c.x * 7f + c.y * 13f).toInt()
        // Una nube rala de fondo, corrida hacia donde soplaba la boca, y encima las gotitas del soplo.
        mancha(c + Offset(r * 0.25f, -r * 0.15f), r * 1.6f, o.copy(alpha = 0.3f))
        for (k in 0 until 4) drawPath(piedra(c + Offset(r * (azar(sem, k + 900) - 0.5f) * 1.2f, r * (azar(sem, k + 910) - 0.5f) * 1.2f), r * 0.9f, r * 0.7f, sem + k, 8, 0.4f), o.copy(alpha = 0.16f))
        for (k in 0 until 80) {
            val a = 6.2832f * azar(sem, k)
            val d = r * (0.45f + 1.75f * azar(sem, k + 100).pow(1.3f))
            val q = c + Offset(cos(a) * d * 1.1f, sin(a) * d)
            val tam = (0.35f + 1.3f * azar(sem, k + 300)) * density * (1.5f - d / (r * 2.2f)).coerceIn(0.35f, 1.2f)
            drawCircle(o.copy(alpha = 0.3f + 0.5f * azar(sem, k + 200)), tam, q)
        }
        rotate(giro, c) {
            drawOval(roca, Offset(c.x - r * 0.42f, c.y - r * 0.12f), Size(r * 0.84f, r * 0.8f))
            listOf(-0.32f to 0.6f, -0.11f to 0.78f, 0.09f to 0.8f, 0.28f to 0.64f).forEach { (dx, lg) ->
                drawLine(roca, Offset(c.x + r * dx, c.y + r * 0.05f), Offset(c.x + r * dx * 1.18f, c.y - r * lg), r * 0.18f, StrokeCap.Round)
            }
            drawLine(roca, Offset(c.x - r * 0.36f, c.y + r * 0.3f), Offset(c.x - r * 0.76f, c.y - r * 0.05f), r * 0.2f, StrokeCap.Round)
            drawRect(roca, Offset(c.x - r * 0.3f, c.y + r * 0.5f), Size(r * 0.6f, r * 0.55f))
        }
    }

    /** Un contorno a carbón de trazo vivo: engorda, adelgaza y se interrumpe; [q] son los puntos y [g], el grosor medio. */
    private fun DrawScope.trazo(q: List<Offset>, g: Float, color: Color, semilla: Int) {
        for (j in 0 until q.size - 1) {
            if (azar(semilla, j) < 0.2f) continue
            val a = q[j]; val b = q[j + 1]
            val m = (a + b) / 2f + Offset((azar(semilla, j + 30) - 0.5f) * g * 0.6f, (azar(semilla, j + 60) - 0.5f) * g * 0.6f)
            val gg = g * (0.7f + 0.6f * azar(semilla, j + 90))
            drawPath(abierta(listOf(a, m, b)), color.copy(alpha = color.alpha * (0.8f + 0.2f * azar(semilla, j + 120))), style = Stroke(gg, cap = StrokeCap.Round))
        }
    }

    /** Puntos intermedios de una lista (para que el trazo cambie de grosor más a menudo). */
    private fun densa(q: List<Offset>): List<Offset> = q.zipWithNext { a, b -> listOf(a, (a + b) / 2f) }.flatten() + q.last()

    /** Ocre a manchas dentro de [forma]: una capa rala y tamponazos desiguales que dejan ver la piedra, y su grano encima. */
    private fun DrawScope.ocreAManchas(forma: Path, color: Color, otro: Color, semilla: Int, s: Float, oscuro: Boolean, luz: (Offset) -> Luz, n0: Int) {
        val b = forma.getBounds()
        drawPath(forma, n(color, oscuro, luz(b.center)).copy(alpha = 0.5f))
        clipPath(forma) {
            for (k in 0 until n0) {
                val q0 = Offset(b.left + b.width * azar(semilla, k), b.top + b.height * azar(semilla, k + 40))
                drawPath(piedra(q0, s * (0.16f + 0.24f * azar(semilla, k + 80)), s * (0.1f + 0.16f * azar(semilla, k + 120)), semilla + k, 7, 0.35f),
                    n(if (k % 4 == 0) otro else color, oscuro, luz(q0)).copy(alpha = 0.28f + 0.3f * azar(semilla, k + 160)))
            }
        }
    }

    /** Un animal chico a carbón, de pocos trazos y a medio perder: una cabra montés (cuernos curvos) o un ciervo (astas). */
    private fun DrawScope.animalito(c: Offset, s: Float, ciervo: Boolean, oscuro: Boolean, l: Luz, semilla: Int) {
        fun q(x: Float, y: Float) = Offset(c.x + s * x, c.y + s * y)
        val tinta = n(MANGANESO, oscuro, l).copy(alpha = 0.72f)
        drawPath(cerrada(listOf(q(-0.7f, -0.1f), q(-0.2f, -0.28f), q(0.5f, -0.25f), q(0.72f, 0.05f), q(0.3f, 0.28f), q(-0.5f, 0.26f))),
            n(if (ciervo) OCRE_ROJO else OCRE_AMAR, oscuro, l).copy(alpha = 0.3f))
        trazo(densa(listOf(q(-0.78f, 0.08f), q(-0.7f, -0.15f), q(-0.2f, -0.3f), q(0.4f, -0.26f), q(0.62f, -0.32f), q(0.8f, -0.58f), q(1.02f, -0.5f), q(0.96f, -0.36f),
            q(0.72f, -0.12f), q(0.5f, 0.22f), q(-0.1f, 0.3f), q(-0.62f, 0.26f), q(-0.78f, 0.08f))), s * 0.08f, tinta, semilla)
        listOf(-0.55f, -0.38f, 0.32f, 0.5f).forEachIndexed { k, x -> drawLine(tinta, q(x, 0.24f), q(x + 0.06f * (k % 2), 0.78f), s * 0.06f, StrokeCap.Round) }
        drawLine(tinta, q(-0.78f, -0.05f), q(-0.9f, 0.1f), s * 0.05f, StrokeCap.Round)
        if (ciervo) {
            drawPath(abierta(listOf(q(0.84f, -0.56f), q(0.74f, -1.0f), q(0.55f, -1.35f))), tinta, style = Stroke(s * 0.05f, cap = StrokeCap.Round))
            drawLine(tinta, q(0.73f, -0.95f), q(0.95f, -1.15f), s * 0.04f, StrokeCap.Round)
            drawLine(tinta, q(0.64f, -1.2f), q(0.8f, -1.45f), s * 0.04f, StrokeCap.Round)
            drawLine(tinta, q(0.8f, -0.75f), q(1.02f, -0.86f), s * 0.04f, StrokeCap.Round)
        } else {
            drawPath(abierta(listOf(q(0.84f, -0.56f), q(0.68f, -0.98f), q(0.34f, -1.08f), q(0.18f, -0.82f))), tinta, style = Stroke(s * 0.07f, cap = StrokeCap.Round))
            for (k in 0 until 3) drawLine(tinta, q(0.74f - 0.15f * k, -0.88f - 0.06f * k), q(0.8f - 0.15f * k, -0.96f - 0.06f * k), s * 0.03f)
        }
    }

    /**
     * El bisonte grande, mirando a la izquierda con la cabeza baja, pintado sobre la piedra: el ocre a manchas que dejan ver
     * la roca, la giba y la cabeza de manganeso soplado, la tripa en reserva, el contorno a carbón que engorda, adelgaza y se
     * interrumpe, y el volumen que le da el abultamiento (más claro arriba a la izquierda, donde la roca sobresale). De noche,
     * cada parte toma la luz de donde está: el lomo, cerca de la lámpara, se enciende; la cabeza se pierde.
     */
    private fun DrawScope.bisonte(c: Offset, s: Float, oscuro: Boolean, luz: (Offset) -> Luz) {
        fun q(x: Float, y: Float) = Offset(c.x + s * x, c.y + s * y)
        val lc = luz(c); val lCabeza = luz(q(-0.8f, 0.1f)); val lAnca = luz(q(0.7f, 0f))
        val contorno = listOf(q(-1.0f, 0.24f), q(-0.96f, -0.04f), q(-0.84f, -0.16f), q(-0.66f, -0.44f), q(-0.42f, -0.63f), q(-0.12f, -0.5f),
            q(0.2f, -0.38f), q(0.56f, -0.34f), q(0.82f, -0.22f), q(0.92f, 0.04f), q(0.84f, 0.3f), q(0.5f, 0.38f), q(0.05f, 0.44f), q(-0.42f, 0.46f),
            q(-0.66f, 0.6f), q(-0.78f, 0.52f), q(-0.92f, 0.4f))
        val cuerpo = cerrada(contorno)
        val carbon = { l: Luz -> n(MANGANESO, oscuro, l) }
        // Las patas, a trazo: el ocre debajo y el carbón encima, que no siempre coinciden.
        listOf(Triple(-0.55f, 0.4f, -0.6f), Triple(-0.36f, 0.42f, -0.3f), Triple(0.52f, 0.33f, 0.6f), Triple(0.7f, 0.28f, 0.8f)).forEachIndexed { i, (x0, y0, x1) ->
            val l = luz(q(x0, 0.6f))
            val pata = abierta(listOf(q(x0, y0), q((x0 + x1) / 2 + 0.03f, 0.62f), q(x1, 0.86f)))
            drawPath(pata, n(if (i % 2 == 0) OCRE_ROJO else mezcla(OCRE_ROJO, MANGANESO, 0.5f), oscuro, l).copy(alpha = 0.7f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            trazo(listOf(q(x0 + 0.02f, y0), q((x0 + x1) / 2 + 0.05f, 0.62f), q(x1 + 0.02f, 0.86f)), s * 0.035f, carbon(l), 1530 + i)
            drawOval(carbon(l).copy(alpha = 0.8f), Offset(q(x1, 0.86f).x - s * 0.05f, q(x1, 0.86f).y - s * 0.03f), Size(s * 0.11f, s * 0.06f))
        }
        ocreAManchas(cuerpo, OCRE_ROJO, mezcla(OCRE_ROJO, OCRE_AMAR, 0.45f), 1500, s, oscuro, luz, 18)
        clipPath(cuerpo) {
            val b = cuerpo.getBounds()
            // El anca, con el ocre más ralo y amarillento.
            drawPath(piedra(q(0.5f, -0.08f), s * 0.36f, s * 0.24f, 1520, 9, 0.25f), n(mezcla(OCRE_ROJO, OCRE_AMAR, 0.5f), oscuro, lAnca).copy(alpha = 0.45f))
            // La tripa en reserva: la piedra sin pintar.
            drawPath(piedra(q(-0.12f, 0.36f), s * 0.6f, s * 0.12f, 1521, 9, 0.2f), n(mezcla(CALIZA_LUZ, OCRE_AMAR, 0.2f), oscuro, lc).copy(alpha = 0.8f))
            // La giba, la cerviz y la cabeza: manganeso soplado, más espeso en la cabeza, y el pelo a trazos.
            val negro = cerrada(listOf(q(-1.1f, 0.5f), q(-1.05f, -0.2f), q(-0.7f, -0.75f), q(-0.3f, -0.75f), q(-0.1f, -0.52f), q(-0.28f, -0.3f), q(-0.5f, -0.05f), q(-0.62f, 0.3f), q(-0.7f, 0.7f)))
            drawPath(negro, carbon(lCabeza).copy(alpha = 0.42f))
            for (k in 0 until 7) drawPath(piedra(q(-0.95f + 0.12f * k, -0.3f + 0.1f * (k % 3)), s * 0.16f, s * 0.12f, 1540 + k, 7, 0.35f), carbon(lCabeza).copy(alpha = 0.22f))
            drawPath(piedra(q(-0.86f, 0.05f), s * 0.16f, s * 0.24f, 1550, 8, 0.25f), carbon(lCabeza).copy(alpha = 0.4f))
            for (k in 0 until 9) {
                val a = q(-0.1f - 0.05f * k, -0.48f + 0.06f * k)
                drawLine(carbon(lc).copy(alpha = 0.6f), a, a + Offset(s * 0.14f, s * 0.08f), s * (0.02f + 0.015f * azar(1560, k)), StrokeCap.Round)
            }
            // La piedra se ve a través del pigmento: su grano y sus poros.
            pinceladas(b, n(CALIZA_LUZ, oscuro, lc), 1510, 26, density, 0.09f)
            for (k in 0 until 26) drawCircle(n(CALIZA_LUZ, oscuro, lc).copy(alpha = 0.22f), (0.5f + 0.9f * azar(1511, k)) * density, Offset(b.left + b.width * azar(1512, k), b.top + b.height * azar(1513, k)))
            // El volumen del abultamiento: más claro arriba a la izquierda, en sombra abajo a la derecha.
            drawOval(claro(oscuro, 0.22f, lc), Offset(b.left, b.top - b.height * 0.1f), Size(b.width * 0.6f, b.height * 0.55f))
            drawOval(sombra(oscuro, 0.14f), Offset(b.left + b.width * 0.45f, b.top + b.height * 0.55f), Size(b.width * 0.75f, b.height * 0.6f))
        }
        // El contorno de carbón, de trazo vivo: grueso en el lomo y la giba, flojo y cortado en la tripa.
        trazo(densa(contorno.subList(0, 11)), s * 0.036f, carbon(lc).copy(alpha = 0.85f), 1570)
        trazo(densa(listOf(q(0.5f, 0.38f), q(0.05f, 0.44f), q(-0.3f, 0.46f))), s * 0.022f, carbon(lc).copy(alpha = 0.6f), 1571)
        trazo(densa(listOf(q(-0.42f, 0.46f), q(-0.66f, 0.6f), q(-0.78f, 0.52f), q(-0.92f, 0.4f), q(-1.0f, 0.24f), q(-0.96f, -0.04f))), s * 0.04f, carbon(lCabeza), 1572)
        // El cuerno, el ojo y la cola.
        trazo(listOf(q(-0.84f, -0.18f), q(-0.97f, -0.32f), q(-0.88f, -0.45f)), s * 0.045f, carbon(lCabeza), 1573)
        drawCircle(n(CALIZA_LUZ, oscuro, lCabeza).copy(alpha = 0.8f), s * 0.028f, q(-0.86f, 0.02f))
        trazo(listOf(q(0.84f, -0.18f), q(0.98f, 0f), q(0.96f, 0.28f)), s * 0.025f, carbon(lAnca), 1574)
        drawPath(piedra(q(0.96f, 0.31f), s * 0.04f, s * 0.07f, 1575, 6, 0.3f), carbon(lAnca).copy(alpha = 0.8f))
    }

    /** El caballo, más chico, mirando a la derecha: ocre amarillo a manchas, la crin y el hocico negros, la tripa clara. */
    private fun DrawScope.caballo(c: Offset, s: Float, oscuro: Boolean, l: Luz) {
        fun q(x: Float, y: Float) = Offset(c.x + s * x, c.y + s * y)
        val negro = n(MANGANESO, oscuro, l)
        listOf(-0.5f to -0.58f, -0.32f to -0.28f, 0.36f to 0.42f, 0.5f to 0.66f).forEachIndexed { i, (x0, x1) ->
            trazo(listOf(q(x0, 0.25f), q((x0 + x1) / 2, 0.52f), q(x1, 0.8f)), s * 0.07f, negro.copy(alpha = 0.8f), 1580 + i)
        }
        val contorno = listOf(q(1.0f, -0.02f), q(0.9f, -0.26f), q(0.76f, -0.44f), q(0.56f, -0.32f), q(0.34f, -0.16f), q(0f, -0.2f), q(-0.52f, -0.22f),
            q(-0.78f, -0.08f), q(-0.82f, 0.18f), q(-0.42f, 0.34f), q(0.2f, 0.33f), q(0.55f, 0.2f), q(0.72f, 0.14f), q(0.88f, 0.14f), q(1.02f, 0.08f))
        val cuerpo = cerrada(contorno)
        ocreAManchas(cuerpo, OCRE_AMAR, mezcla(OCRE_AMAR, OCRE_ROJO, 0.35f), 1590, s, oscuro, { l }, 12)
        clipPath(cuerpo) {
            val b = cuerpo.getBounds()
            drawPath(piedra(q(-0.1f, 0.24f), s * 0.6f, s * 0.12f, 1591, 8, 0.2f), n(mezcla(CALIZA_LUZ, OCRE_AMAR, 0.15f), oscuro, l).copy(alpha = 0.8f))
            drawPath(piedra(q(0.95f, 0.04f), s * 0.13f, s * 0.12f, 1592, 7, 0.2f), negro.copy(alpha = 0.7f))
            pinceladas(b, n(CALIZA_LUZ, oscuro, l), 1593, 14, density, 0.09f)
            drawOval(claro(oscuro, 0.2f, l), Offset(b.left, b.top - b.height * 0.1f), Size(b.width * 0.6f, b.height * 0.6f))
        }
        // La crin erizada, a trazos.
        for (k in 0 until 7) {
            val a = q(0.72f - 0.07f * k, -0.42f + 0.04f * k)
            drawLine(negro.copy(alpha = 0.6f + 0.35f * azar(1594, k)), a, a + Offset(s * 0.04f, -s * (0.09f + 0.06f * azar(1595, k))), s * 0.035f, StrokeCap.Round)
        }
        trazo(densa(listOf(q(0.9f, -0.26f), q(0.76f, -0.44f), q(0.56f, -0.32f), q(0.34f, -0.16f), q(0f, -0.2f), q(-0.52f, -0.22f), q(-0.78f, -0.08f), q(-0.82f, 0.18f))),
            s * 0.05f, negro.copy(alpha = 0.85f), 1596)
        trazo(densa(listOf(q(1.0f, -0.02f), q(1.02f, 0.08f), q(0.88f, 0.14f), q(0.72f, 0.14f))), s * 0.035f, negro.copy(alpha = 0.8f), 1597)
        drawLine(negro, q(0.74f, -0.42f), q(0.72f, -0.56f), s * 0.04f, StrokeCap.Round)
        trazo(listOf(q(-0.78f, -0.1f), q(-0.98f, 0.12f), q(-0.92f, 0.4f)), s * 0.08f, negro, 1598)
    }

    /**
     * La colada de calcita: una cortina que baja por la derecha en lóbulos de alto desigual, cada uno con su panza (el borde se
     * hincha hacia la izquierda y vuelve), su pliegue en sombra debajo, su gota y sus brillos de humedad. El grosor cambia
     * de un lóbulo a otro. De noche, cada tramo toma la luz que le llega.
     */
    private fun DrawScope.colada(w: Float, suelo: Float, oscuro: Boolean, luz: (Offset) -> Luz) {
        val u = density
        val pts = mutableListOf<Offset>()
        val lobulos = mutableListOf<Triple<Float, Float, Float>>()
        run {
            var y = -w * 0.03f; var k = 0
            var baseX = 0.95f
            while (y < suelo + w * 0.05f) {
                val alto = w * (0.07f + 0.17f * azar(801, k))
                baseX = (baseX + (azar(802, k) - 0.5f) * 0.03f).coerceIn(0.935f, 0.968f)
                val panza = 0.006f + 0.02f * azar(803, k)
                val xb = w * baseX; val xp = w * (baseX - panza)
                pts += Offset(xb, y)
                pts += Offset(xp, y + alto * (0.5f + 0.25f * azar(804, k)))
                pts += Offset(xp + w * (0.006f + 0.01f * azar(805, k)), y + alto * 0.93f)
                lobulos += Triple(y, y + alto, xp)
                y += alto; k++
            }
        }
        val f = cerrada(listOf(Offset(w * 1.1f, -w * 0.05f)) + pts + listOf(Offset(w * 1.1f, suelo + w * 0.06f)))
        drawPath(corrido(f, -w * 0.009f, w * 0.008f), sombra(oscuro, 0.24f))
        clipPath(f) {
            var yy = -w * 0.05f
            while (yy < suelo + w * 0.06f) {
                val l = luz(Offset(w * 0.95f, yy + w * 0.025f))
                drawRect(n(mezcla(CALCITA, CALIZA_MED, 0.4f), oscuro, l), Offset(w * 0.84f, yy), Size(w * 0.26f, w * 0.05f + 1f))
                yy += w * 0.05f
            }
            // Cada lóbulo: la panza que coge la luz, el pliegue en sombra a su derecha y la sombra que deja sobre el de abajo.
            lobulos.forEachIndexed { j, (y0, y1, xp) ->
                val l = luz(Offset(xp, (y0 + y1) / 2))
                val panza = abierta(listOf(Offset(xp + w * 0.035f, y0 + w * 0.004f), Offset(xp + w * 0.008f, y0 + (y1 - y0) * 0.6f), Offset(xp + w * 0.02f, y1)))
                drawPath(corrido(panza, w * 0.014f, 0f), sombra(oscuro, 0.18f), style = Stroke(w * 0.01f, cap = StrokeCap.Round))
                drawPath(panza, claro(oscuro, 0.4f, l), style = Stroke(w * 0.004f, cap = StrokeCap.Round))
                if (j % 2 == 1) drawPath(corrido(panza, w * 0.03f, 0f), sombra(oscuro, 0.1f), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                drawOval(sombra(oscuro, 0.22f), Offset(xp + w * 0.004f, y1 - w * 0.008f), Size(w * 0.12f, w * 0.014f))
            }
            // Una franja de óxido que baja con el agua, a tramos.
            drawLine(n(mezcla(CALIZA_MED, OCRE_AMAR, 0.45f), oscuro, SIN_LUZ).copy(alpha = 0.3f), Offset(w * 0.972f, 0f), Offset(w * 0.962f, suelo), w * 0.006f)
            drawRect(sombra(oscuro, 0.16f), Offset(w * 0.985f, 0f), Size(w * 0.03f, suelo + w))
        }
        // El filo de la panza de cada lóbulo, con la luz de su tramo; y la gota que cuelga al final.
        val filoColada = borde(f, w * 0.008f, 0f)
        run {
            var y = -w * 0.05f
            while (y < suelo + w * 0.06f) {
                val l = luz(Offset(w * 0.93f, y + w * 0.025f))
                clipRect(w * 0.8f, y, w * 1.1f, y + w * 0.05f + 1f) { drawPath(filoColada, claro(oscuro, 0.55f, l)) }
                y += w * 0.05f
            }
        }
        lobulos.forEachIndexed { j, (_, y1, xp) ->
            if (j % 3 != 2 && y1 < suelo) {
                val l = luz(Offset(xp, y1))
                val g = Offset(xp + w * 0.012f, y1 + w * 0.006f)
                drawOval(n(mezcla(CALCITA, CALIZA_MED, 0.3f), oscuro, l), Offset(g.x - w * 0.006f, g.y - w * 0.004f), Size(w * 0.012f, w * 0.016f))
                drawCircle(if (oscuro) l.tinte.copy(alpha = 0.8f * l.k) else Color.White.copy(alpha = 0.85f), w * 0.0022f, g + Offset(-w * 0.002f, w * 0.002f))
            }
        }
        // Los brillos de lo húmedo: de día, la luz de la boca; de noche, el fuego.
        for (i in 0 until 16) {
            val q = Offset(w * (0.92f + 0.06f * azar(811, i)), suelo * azar(812, i))
            val l = luz(q)
            val c = if (oscuro) l.tinte.copy(alpha = (0.9f * l.k).coerceAtMost(0.85f)) else Color.White.copy(alpha = 0.7f)
            drawLine(c, q, q + Offset(w * 0.002f, w * (0.006f + 0.016f * azar(813, i))), 1.1f * u, StrokeCap.Round)
        }
        // Las cortinas de arriba: pliegues de calcita que cuelgan del techo.
        listOf(Triple(0.74f, 0.07f, 0.02f), Triple(0.79f, 0.12f, 0.024f), Triple(0.86f, 0.05f, 0.02f)).forEachIndexed { i, (x, largo, ancho) ->
            val l = luz(Offset(w * x, w * largo))
            val c = cerrada(listOf(Offset(w * (x - ancho), 0f), Offset(w * (x + ancho), 0f), Offset(w * (x + ancho * 0.6f), w * largo * 0.6f),
                Offset(w * (x + ancho * 0.15f), w * largo), Offset(w * (x - ancho * 0.3f), w * largo * 0.85f), Offset(w * (x - ancho * 0.7f), w * largo * 0.4f)))
            drawPath(corrido(c, w * 0.006f, w * 0.004f), sombra(oscuro, 0.25f))
            volumen(c, n(mezcla(CALCITA, CALIZA_MED, 0.25f), oscuro, l), oscuro, l, -1f, 0.9f, 0.5f)
            drawCircle(if (oscuro) l.tinte.copy(alpha = 0.8f * l.k) else Color.White.copy(alpha = 0.8f), 1.3f * u, Offset(w * (x + ancho * 0.12f), w * largo + 2.2f * u))
        }
    }

    /** El collar: un cordón manchado de ocre, colgado de un saliente, con dientes de ciervo y conchas perforadas. */
    private fun DrawScope.collar(clavo: Offset, w: Float, oscuro: Boolean, l: Luz, lado: Float, caida: Offset) {
        val u = density
        // El saliente de roca del que cuelga.
        val nudo = piedra(clavo, w * 0.016f, w * 0.011f, 820, 7, 0.2f)
        saliente(nudo, n(mezcla(CALCITA, CALIZA_MED, 0.35f), oscuro, l), oscuro, l, w * 0.004f)
        val cuerda = listOf(clavo + Offset(-w * 0.006f, 0f), clavo + Offset(-w * 0.04f, w * 0.06f), clavo + Offset(-w * 0.03f, w * 0.11f),
            clavo + Offset(w * 0.0f, w * 0.128f), clavo + Offset(w * 0.035f, w * 0.112f), clavo + Offset(w * 0.04f, w * 0.06f), clavo + Offset(w * 0.006f, 0f))
        val cp = abierta(cuerda)
        drawPath(corrido(cp, caida.x, caida.y), sombra(oscuro, 0.3f), style = Stroke(1.6f * u))
        drawPath(cp, n(mezcla(PIEL_OSC, OCRE_ROJO, 0.4f), oscuro, l), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // Las cuentas, de la mitad de abajo del cordón, colgando a plomo.
        val sitios = listOf(-0.036f to 0.075f, -0.033f to 0.093f, -0.025f to 0.108f, -0.012f to 0.12f, 0.002f to 0.127f, 0.016f to 0.123f, 0.028f to 0.112f, 0.036f to 0.096f, 0.04f to 0.078f)
        sitios.forEachIndexed { i, (dx, dy) ->
            val a = clavo + Offset(w * dx, w * dy)
            if (i % 2 == 0) {
                // Un diente: la raíz perforada arriba, la corona abajo, curvo.
                val largo = w * (0.018f + 0.004f * azar(821, i))
                val dte = cerrada(listOf(a + Offset(-w * 0.0035f, 0f), a + Offset(w * 0.0035f, 0f), a + Offset(w * 0.005f, largo * 0.6f), a + Offset(w * 0.001f, largo), a + Offset(-w * 0.004f, largo * 0.55f)))
                drawPath(corrido(dte, caida.x, caida.y), sombra(oscuro, 0.3f))
                volumen(dte, n(HUESO, oscuro, l), oscuro, l, lado, 1f, 0.5f)
                drawPath(Path().apply { moveTo(a.x - w * 0.003f, a.y + largo * 0.3f); lineTo(a.x + w * 0.004f, a.y + largo * 0.3f) }, n(mezcla(HUESO, PIEL_OSC, 0.5f), oscuro, l), style = Stroke(0.8f * u))
                drawCircle(n(HONDO, oscuro, l), w * 0.0012f, a + Offset(0f, w * 0.002f))
            } else {
                // Una concha perforada.
                val r = w * 0.0058f
                val cc = a + Offset(0f, r)
                drawCircle(sombra(oscuro, 0.3f), r, cc + caida)
                drawCircle(n(mezcla(HUESO, OCRE_AMAR, 0.25f), oscuro, l), r, cc)
                drawArc(n(mezcla(PIEL_OSC, HUESO, 0.4f), oscuro, l), 200f, 230f, false, Offset(cc.x - r * 0.6f, cc.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(0.8f * u))
                drawCircle(claro(oscuro, 0.6f, l), r * 0.3f, cc + Offset(-r * 0.4f, -r * 0.35f))
            }
        }
    }

    /** La lámpara de arenisca: el cuenco con su mango, el sebo dentro, la mecha y el hollín del borde. */
    private fun DrawScope.lampara(pie: Offset, w: Float, oscuro: Boolean, l: Luz) {
        val u = density
        val cx = pie.x
        val top = pie.y - w * 0.036f
        val rx = w * 0.066f; val ry = w * 0.017f
        // El cuenco: la panza redonda y, encima, la boca vista un poco desde arriba. El mango sale a la izquierda.
        val cuerpo = cerrada(listOf(Offset(cx - rx, top), Offset(cx - rx * 0.9f, top + (pie.y - top) * 0.62f), Offset(cx - rx * 0.5f, pie.y), Offset(cx + rx * 0.5f, pie.y),
            Offset(cx + rx * 0.92f, top + (pie.y - top) * 0.6f), Offset(cx + rx, top), Offset(cx, top + ry * 0.9f)))
        val mango = cerrada(listOf(Offset(cx - rx * 0.7f, top + ry * 0.4f), Offset(cx - rx * 1.3f, top + ry * 0.5f), Offset(cx - rx * 1.72f, top + ry * 1.0f),
            Offset(cx - rx * 1.7f, pie.y - w * 0.006f), Offset(cx - rx * 1.3f, pie.y - w * 0.003f), Offset(cx - rx * 0.7f, pie.y - w * 0.006f)))
        val sx = if (oscuro) -w * 0.01f else w * 0.014f
        drawOval(sombra(oscuro, 0.38f), Offset(cx - rx * 1.7f + sx, pie.y - w * 0.007f), Size(rx * 2.9f, w * 0.014f))
        val ladoL = if (oscuro) 1f else -1f
        volumen(mango, n(mezcla(ARENISCA, CALIZA_MED, 0.2f), oscuro, l), oscuro, l, ladoL, 0.8f)
        // Las marcas del pulido y las rayas grabadas del mango.
        for (k in 0 until 4) drawLine(n(mezcla(ARENISCA, HONDO, 0.45f), oscuro, l), Offset(cx - rx * 1.55f + k * rx * 0.18f, top + ry * 1.0f), Offset(cx - rx * 1.5f + k * rx * 0.18f, pie.y - w * 0.008f), 0.9f * u)
        volumen(cuerpo, n(ARENISCA, oscuro, l), oscuro, l, ladoL)
        // El hollín que tiñe el costado de la mecha.
        clipPath(cuerpo) { drawOval(HOLLIN.copy(alpha = 0.45f), Offset(cx + rx * 0.45f, top - ry), Size(rx * 0.8f, (pie.y - top) * 0.9f)) }
        // La boca: el borde de piedra, lo hondo, y el sebo derretido.
        drawOval(n(mezcla(ARENISCA, CALIZA_LUZ, 0.25f), oscuro, l), Offset(cx - rx, top - ry), Size(rx * 2f, ry * 2f))
        drawOval(n(mezcla(ARENISCA, HONDO, 0.55f), oscuro, l), Offset(cx - rx * 0.84f, top - ry * 0.78f), Size(rx * 1.68f, ry * 1.6f))
        drawOval(n(Color(0xFFE2D09C), oscuro, l), Offset(cx - rx * 0.74f, top - ry * 0.45f), Size(rx * 1.48f, ry * 1.2f))
        drawOval(claro(oscuro, 0.55f, l), Offset(cx - rx * 0.55f, top - ry * 0.3f), Size(rx * 0.6f, ry * 0.35f))
        // La mecha (de liquen o de enebro), tendida en el sebo hasta el borde de la derecha, y el hollín del borde.
        drawOval(HOLLIN.copy(alpha = 0.6f), Offset(cx + rx * 0.55f, top - ry * 1.1f), Size(rx * 0.5f, ry * 1.4f))
        drawLine(n(HOLLIN, oscuro, l), Offset(cx + rx * 0.05f, top + ry * 0.1f), Offset(cx + rx * 0.9f, top - ry * 0.45f), 2.6f * u, StrokeCap.Round)
        drawLine(n(PIEL_OSC, oscuro, l), Offset(cx + rx * 0.05f, top + ry * 0.1f), Offset(cx + rx * 0.55f, top - ry * 0.15f), 1.2f * u, StrokeCap.Round)
        if (oscuro) drawOval(LLAMA.copy(alpha = 0.7f), Offset(cx - rx, top - ry), Size(rx * 2f, ry * 2f), style = Stroke(1.4f * u))
    }

    /** Una llama pequeña, en capas opacas: el rojo de fuera, el naranja, el amarillo y el centro casi blanco. */
    private fun DrawScope.llama(base: Offset, alto: Float, ancha: Boolean) {
        val a = if (ancha) alto * 0.42f else alto * 0.28f
        listOf(Triple(1f, 1f, BRASA), Triple(0.78f, 0.82f, Color(0xFFF08A36)), Triple(0.55f, 0.62f, LLAMA), Triple(0.3f, 0.36f, Color(0xFFFFF0C8))).forEach { (fx, fy, c) ->
            val h = alto * fy; val an = a * fx
            val p = Path().apply {
                moveTo(base.x - an, base.y)
                cubicTo(base.x - an * 1.1f, base.y - h * 0.4f, base.x - an * 0.2f, base.y - h * 0.62f, base.x + an * 0.15f, base.y - h)
                cubicTo(base.x + an * 0.35f, base.y - h * 0.6f, base.x + an * 1.15f, base.y - h * 0.35f, base.x + an, base.y)
                cubicTo(base.x + an * 0.5f, base.y + an * 0.3f, base.x - an * 0.5f, base.y + an * 0.3f, base.x - an, base.y)
                close()
            }
            drawPath(p, c)
        }
    }

    /** Una concha grande usada de paleta: la valva boca arriba, con sus costillas y la pasta de ocre dentro. */
    private fun DrawScope.concha(pie: Offset, r: Float, oscuro: Boolean, l: Luz, lado: Float) {
        val u = density
        val f = cerrada(listOf(Offset(pie.x - r, pie.y - r * 0.25f), Offset(pie.x - r * 0.6f, pie.y - r * 0.62f), Offset(pie.x + r * 0.2f, pie.y - r * 0.7f),
            Offset(pie.x + r * 0.95f, pie.y - r * 0.38f), Offset(pie.x + r * 0.8f, pie.y), Offset(pie.x - r * 0.6f, pie.y + r * 0.02f)))
        drawOval(sombra(oscuro, 0.35f), Offset(pie.x - r * 0.9f + (if (oscuro) 0f else r * 0.3f), pie.y - r * 0.15f), Size(r * 2f, r * 0.35f))
        volumen(f, n(mezcla(HUESO, OCRE_AMAR, 0.2f), oscuro, l), oscuro, l, lado)
        clipPath(f) { for (k in 0 until 5) drawLine(n(mezcla(HUESO, PIEL_OSC, 0.4f), oscuro, l).copy(alpha = 0.5f), Offset(pie.x - r + k * r * 0.45f, pie.y + r * 0.1f), Offset(pie.x - r * 0.3f + k * r * 0.3f, pie.y - r * 0.8f), 0.8f * u) }
        // La pasta de ocre dentro de la valva, y un dedo que la removió.
        val pasta = piedra(Offset(pie.x - r * 0.05f, pie.y - r * 0.42f), r * 0.62f, r * 0.2f, 830, 8, 0.15f)
        drawPath(pasta, n(OCRE_ROJO, oscuro, l))
        drawPath(borde(pasta, r * 0.08f, r * 0.06f), n(mezcla(OCRE_ROJO, HONDO, 0.4f), oscuro, l))
        drawLine(claro(oscuro, 0.4f, l), Offset(pie.x - r * 0.5f, pie.y - r * 0.48f), Offset(pie.x + r * 0.1f, pie.y - r * 0.5f), 1.2f * u, StrokeCap.Round)
    }

    /** La piedra de moler: la losa con el polvo rojo, la mano de moler encima y dos terrones de ocre, rojo y amarillo. */
    private fun DrawScope.molienda(pie: Offset, w: Float, oscuro: Boolean, l: Luz, lado: Float, caida: Offset) {
        val e = w * 1.25f
        val u = density
        val cx = pie.x
        val losa = cerrada(listOf(Offset(cx - e * 0.07f, pie.y - e * 0.012f), Offset(cx - e * 0.05f, pie.y - e * 0.026f), Offset(cx + e * 0.03f, pie.y - e * 0.03f),
            Offset(cx + e * 0.072f, pie.y - e * 0.018f), Offset(cx + e * 0.066f, pie.y), Offset(cx - e * 0.065f, pie.y + e * 0.001f)))
        drawPath(corrido(losa, caida.x, caida.y * 0.4f), sombra(oscuro, 0.3f))
        volumen(losa, n(mezcla(CALIZA_OSC, SILEX, 0.4f), oscuro, l), oscuro, l, lado)
        val cara = cerrada(listOf(Offset(cx - e * 0.064f, pie.y - e * 0.014f), Offset(cx - e * 0.048f, pie.y - e * 0.026f), Offset(cx + e * 0.03f, pie.y - e * 0.029f),
            Offset(cx + e * 0.066f, pie.y - e * 0.018f), Offset(cx + e * 0.02f, pie.y - e * 0.012f)))
        drawPath(cara, n(mezcla(CALIZA_MED, SILEX, 0.3f), oscuro, l))
        // El polvo rojo, molido en el centro y salpicado.
        drawPath(piedra(Offset(cx - e * 0.006f, pie.y - e * 0.02f), e * 0.04f, e * 0.006f, 840, 9, 0.2f), n(OCRE_ROJO, oscuro, l))
        for (k in 0 until 8) drawCircle(n(OCRE_ROJO, oscuro, l), (0.6f + 0.8f * azar(841, k)) * u, Offset(cx + w * (-0.06f + 0.13f * azar(842, k)), pie.y - w * (0.003f + 0.022f * azar(843, k))))
        // La mano de moler: un canto rodado con la cara de abajo roja.
        val mano = piedra(Offset(cx + e * 0.026f, pie.y - e * 0.04f), e * 0.024f, e * 0.016f, 845, 9, 0.08f)
        drawOval(sombra(oscuro, 0.4f), Offset(cx + e * 0.006f, pie.y - e * 0.028f), Size(e * 0.05f, e * 0.008f))
        volumen(mano, n(Color(0xFFB0A090), oscuro, l), oscuro, l, lado, 1f, 0.45f)
        clipPath(mano) { drawRect(n(OCRE_ROJO, oscuro, l).copy(alpha = 0.8f), Offset(cx, pie.y - e * 0.03f), Size(e * 0.06f, e * 0.01f)) }
        // Los terrones: el rojo a la izquierda, el amarillo a la derecha.
        terron(Offset(cx - e * 0.045f, pie.y + e * 0.002f), e * 0.013f, OCRE_ROJO, 846, oscuro, l, lado)
        terron(Offset(cx + e * 0.074f, pie.y - e * 0.004f), e * 0.011f, OCRE_AMAR, 847, oscuro, l, lado)
    }

    /** Un terrón de ocre: aristas vivas, una cara gastada de raspar. */
    private fun DrawScope.terron(c: Offset, r: Float, color: Color, semilla: Int, oscuro: Boolean, l: Luz, lado: Float) {
        val f = poligono(c, r * 1.2f, r * 0.85f, semilla, 6)
        drawOval(sombra(oscuro, 0.35f), Offset(c.x - r, c.y + r * 0.5f), Size(r * 2.4f, r * 0.6f))
        volumen(f, n(color, oscuro, l), oscuro, l, lado, 1.3f, 0.4f)
        drawLine(claro(oscuro, 0.5f, l), Offset(c.x - r * 0.6f, c.y - r * 0.3f), Offset(c.x + r * 0.2f, c.y - r * 0.6f), r * 0.25f, StrokeCap.Round)
    }

    /** El sílex: un núcleo con las cicatrices de las hojas sacadas y dos hojas largas a su pie. */
    private fun DrawScope.silex(pie: Offset, w: Float, oscuro: Boolean, l: Luz, lado: Float) {
        val e = w * 1.2f
        val u = density
        val c = Offset(pie.x - e * 0.012f, pie.y - e * 0.02f)
        val nucleo = poligono(c, e * 0.022f, e * 0.02f, 850, 7)
        drawOval(sombra(oscuro, 0.35f), Offset(c.x - e * 0.02f, pie.y - e * 0.006f), Size(e * 0.05f, e * 0.009f))
        volumen(nucleo, n(SILEX, oscuro, l), oscuro, l, lado, 1.2f, 0.4f)
        clipPath(nucleo) {
            for (k in 0 until 4) drawLine(n(mezcla(SILEX, CALCITA, 0.45f), oscuro, l), Offset(c.x - e * 0.015f + k * e * 0.008f, c.y - e * 0.02f), Offset(c.x - e * 0.012f + k * e * 0.009f, c.y + e * 0.02f), 0.9f * u)
        }
        // El córtex: la corteza blanca del nódulo, arriba.
        clipPath(nucleo) { drawOval(n(CALCITA, oscuro, l), Offset(c.x - e * 0.03f, c.y - e * 0.032f), Size(e * 0.05f, e * 0.022f)) }
        // Dos hojas, largas y finas, con su arista.
        listOf(Triple(-0.035f, 0.004f, -6f), Triple(-0.06f, 0.006f, 4f)).forEach { (dx, dy, g) ->
            val a = Offset(pie.x + w * dx, pie.y + w * dy)
            rotate(g, a) {
                val hoja = Path().apply { moveTo(a.x, a.y); lineTo(a.x + e * 0.045f, a.y - e * 0.004f); lineTo(a.x + e * 0.05f, a.y + e * 0.0005f); lineTo(a.x + e * 0.004f, a.y + e * 0.004f); close() }
                drawPath(corrido(hoja, 0.8f * u, 1.2f * u), sombra(oscuro, 0.35f))
                drawPath(hoja, n(mezcla(SILEX, CALCITA, 0.3f), oscuro, l))
                drawLine(n(mezcla(SILEX, HONDO, 0.3f), oscuro, l), Offset(a.x + e * 0.003f, a.y + e * 0.0015f), Offset(a.x + e * 0.046f, a.y - e * 0.001f), 0.7f * u)
                drawLine(claro(oscuro, 0.6f, l), Offset(a.x + e * 0.004f, a.y - e * 0.0008f), Offset(a.x + e * 0.04f, a.y - e * 0.003f), 0.6f * u)
            }
        }
    }

    /** Tres agujas de hueso, con su ojo, cruzadas sobre el canto de la repisa. */
    private fun DrawScope.agujas(c: Offset, w: Float, oscuro: Boolean, l: Luz) {
        val u = density
        listOf(Triple(0f, 0f, -8f), Triple(w * 0.006f, w * 0.004f, 10f), Triple(-w * 0.004f, w * 0.006f, 2f)).forEach { (dx, dy, g) ->
            val a = Offset(c.x + dx, c.y + dy)
            rotate(g, a) {
                drawLine(sombra(oscuro, 0.35f), a + Offset(-w * 0.018f, 1.4f * u), a + Offset(w * 0.02f, 1.4f * u), 1.4f * u, StrokeCap.Round)
                drawLine(n(HUESO, oscuro, l), a + Offset(-w * 0.018f, 0f), a + Offset(w * 0.02f, 0f), 1.5f * u, StrokeCap.Round)
                drawCircle(n(HONDO, oscuro, l), 0.45f * u, a + Offset(-w * 0.015f, 0f))
            }
        }
    }

    /** El fardo de piel atado con tendón: el bulto con su pelo, la solapa abierta y dos vueltas de cuerda. */
    private fun DrawScope.fardo(pie: Offset, w: Float, oscuro: Boolean, l: Luz, lado: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        fun q(x: Float, y: Float) = Offset(cx + w * x, pie.y - w * y)
        // El bulto: abollado, más alto a la izquierda, con la piel del pelo por fuera.
        val f = cerrada(listOf(q(-0.128f, 0.004f), q(-0.142f, 0.05f), q(-0.13f, 0.1f), q(-0.09f, 0.14f), q(-0.035f, 0.15f), q(0.012f, 0.136f), q(0.06f, 0.128f),
            q(0.108f, 0.108f), q(0.135f, 0.068f), q(0.13f, 0.02f), q(0.08f, 0.0f), q(0f, -0.004f)))
        drawPath(corrido(f, caida.x, caida.y * 0.6f), sombra(oscuro, 0.3f))
        drawOval(sombra(oscuro, 0.45f), Offset(cx - w * 0.13f, pie.y - w * 0.008f), Size(w * 0.28f, w * 0.014f))
        volumen(f, n(PIEL, oscuro, l), oscuro, l, lado, 1.1f, 0.3f)
        clipPath(f) {
            // El pelo: mechones que siguen el bulto, oscuros en la raíz y claros en la punta.
            for (k in 0 until 60) {
                val a = q(-0.14f + 0.28f * azar(860, k), 0.0f + 0.15f * azar(861, k))
                val g = -0.4f + 0.8f * azar(862, k)
                drawLine(n(PIEL_OSC, oscuro, l).copy(alpha = 0.5f), a, a + Offset(w * 0.006f * g, w * 0.013f), 1.3f * u, StrokeCap.Round)
                if (k % 2 == 0) drawLine(n(mezcla(PIEL, CALIZA_LUZ, 0.45f), oscuro, l).copy(alpha = 0.55f), a, a + Offset(w * 0.004f * g, -w * 0.006f), 1f * u, StrokeCap.Round)
            }
            // Los pliegues de la piel doblada.
            drawPath(abierta(listOf(q(-0.12f, 0.06f), q(-0.06f, 0.05f), q(-0.01f, 0.06f))), sombra(oscuro, 0.4f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(abierta(listOf(q(-0.1f, 0.03f), q(-0.04f, 0.022f))), sombra(oscuro, 0.3f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            // La solapa: el revés de la piel (la carne curtida, más clara y lisa), doblado sobre el frente.
            val solapa = cerrada(listOf(q(-0.1f, 0.14f), q(-0.02f, 0.142f), q(0.05f, 0.118f), q(0.03f, 0.07f), q(-0.025f, 0.085f), q(-0.075f, 0.105f)))
            drawPath(corrido(solapa, w * 0.004f, w * 0.006f), sombra(oscuro, 0.4f))
            volumen(solapa, n(mezcla(PIEL, HUESO, 0.5f), oscuro, l), oscuro, l, lado, 0.8f, 0.45f)
            drawLine(n(mezcla(PIEL, PIEL_OSC, 0.4f), oscuro, l), q(0.03f, 0.07f), q(0.05f, 0.118f), 1.6f * u, StrokeCap.Round)
        }
        // El pelo que asoma por el contorno de arriba.
        for (k in 0 until 18) {
            val t = k / 17f
            val a = q(-0.14f + 0.27f * t, 0.05f + 0.095f * sin(t * 3.1416f) + 0.004f * azar(865, k))
            drawLine(n(mezcla(PIEL, CALIZA_LUZ, 0.3f), oscuro, l), a, a + Offset(w * (0.002f - 0.004f * t), -w * 0.007f), 1.2f * u, StrokeCap.Round)
        }
        // Las vueltas de tendón: una de lado a lado y dos de arriba abajo, que se hunden en el bulto; el nudo y sus cabos.
        val cuerdas = listOf(
            abierta(listOf(q(-0.142f, 0.06f), q(-0.06f, 0.075f), q(0.04f, 0.072f), q(0.133f, 0.055f))),
            abierta(listOf(q(-0.075f, 0.142f), q(-0.07f, 0.07f), q(-0.078f, 0.0f))),
            abierta(listOf(q(0.06f, 0.128f), q(0.068f, 0.07f), q(0.072f, 0.0f))))
        cuerdas.forEach { cuerda ->
            drawPath(corrido(cuerda, 1.2f * u, 1f * u), sombra(oscuro, 0.5f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
            drawPath(cuerda, n(Color(0xFFD9C79E), oscuro, l), style = Stroke(2.3f * u, cap = StrokeCap.Round))
            drawPath(corrido(cuerda, -0.5f * u, -0.5f * u), claro(oscuro, 0.55f, l), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
        val nudo = q(0.066f, 0.073f)
        drawCircle(sombra(oscuro, 0.5f), w * 0.008f, nudo + Offset(1.2f * u, 1.2f * u))
        drawCircle(n(Color(0xFFD9C79E), oscuro, l), w * 0.0075f, nudo)
        drawCircle(claro(oscuro, 0.5f, l), w * 0.003f, nudo + Offset(-w * 0.002f, -w * 0.002f))
        drawLine(n(Color(0xFFD9C79E), oscuro, l), nudo, nudo + Offset(w * 0.014f, w * 0.03f), 1.6f * u, StrokeCap.Round)
        drawLine(n(Color(0xFFD9C79E), oscuro, l), nudo, nudo + Offset(w * 0.024f, w * 0.018f), 1.4f * u, StrokeCap.Round)
    }

    /** El propulsor de asta de reno: el fuste algo curvo, el gancho arriba y unas muescas, apoyado contra la repisa. */
    private fun DrawScope.propulsor(pie: Offset, punta: Offset, w: Float, oscuro: Boolean, l: Luz, caida: Offset) {
        val u = density
        val medio = (pie + punta) / 2f + Offset(-w * 0.008f, 0f)
        val fuste = abierta(listOf(pie, medio, punta))
        val ancho = w * 0.011f
        drawPath(corrido(fuste, caida.x, caida.y), sombra(oscuro, 0.3f), style = Stroke(ancho, cap = StrokeCap.Round))
        drawPath(fuste, n(mezcla(HUESO, PIEL, 0.3f), oscuro, l), style = Stroke(ancho, cap = StrokeCap.Round))
        drawPath(corrido(fuste, ancho * 0.3f, 0f), sombra(oscuro, 0.3f), style = Stroke(ancho * 0.35f, cap = StrokeCap.Round))
        drawPath(corrido(fuste, -ancho * 0.25f, 0f), claro(oscuro, 0.5f, l), style = Stroke(ancho * 0.25f, cap = StrokeCap.Round))
        // El gancho: un espolón hacia atrás, en la punta.
        val g = Path().apply { moveTo(punta.x - ancho * 0.4f, punta.y + ancho * 0.6f); lineTo(punta.x - ancho * 0.1f, punta.y - ancho * 1.6f); lineTo(punta.x + ancho * 0.6f, punta.y + ancho * 0.2f); close() }
        drawPath(corrido(g, caida.x, caida.y), sombra(oscuro, 0.3f))
        drawPath(g, n(mezcla(HUESO, PIEL, 0.4f), oscuro, l))
        // Las muescas grabadas y el agarre gastado, más oscuro.
        for (k in 0 until 5) {
            val t = 0.15f + 0.06f * k
            val a = punta + (pie - punta) * t
            drawLine(n(PIEL_OSC, oscuro, l), a + Offset(-ancho * 0.45f, 0f), a + Offset(ancho * 0.45f, -ancho * 0.15f), 0.9f * u)
        }
        drawLine(n(mezcla(PIEL, PIEL_OSC, 0.5f), oscuro, l), pie + (punta - pie) * 0.05f, pie + (punta - pie) * 0.22f, ancho * 0.9f, StrokeCap.Round)
    }

    /** La piel de reno extendida en el suelo: el contorno con las patas, el pelo de la orilla y la raya del lomo. */
    private fun DrawScope.pielExtendida(c: Offset, rx: Float, ry: Float, w: Float, oscuro: Boolean, l: Luz) {
        val u = density
        fun q(x: Float, y: Float) = Offset(c.x + rx * x, c.y + ry * y)
        val f = cerrada(listOf(q(-1.0f, -0.4f), q(-0.82f, -0.62f), q(-0.88f, -1.0f), q(-0.62f, -0.8f), q(-0.2f, -0.86f), q(0.3f, -0.84f), q(0.66f, -0.78f),
            q(0.9f, -1.05f), q(0.86f, -0.6f), q(1.04f, -0.1f), q(0.88f, 0.55f), q(0.98f, 0.95f), q(0.62f, 0.72f), q(0.0f, 0.82f), q(-0.6f, 0.76f), q(-0.92f, 1.0f), q(-0.86f, 0.5f)))
        drawPath(corrido(f, w * 0.008f, w * 0.006f), sombra(oscuro, 0.35f))
        drawPath(f, n(Color(0xFFB48A60), oscuro, l))
        clipPath(f) {
            drawOval(n(mezcla(PIEL, PIEL_OSC, 0.4f), oscuro, l), Offset(c.x - rx * 0.9f, c.y - ry * 0.3f), Size(rx * 1.8f, ry * 0.5f))
            drawOval(n(mezcla(HUESO, PIEL, 0.3f), oscuro, l).copy(alpha = 0.6f), Offset(c.x - rx * 0.6f, c.y - ry * 0.75f), Size(rx * 0.9f, ry * 0.35f))
            for (k in 0 until 34) {
                val a = Offset(c.x + rx * (-1f + 2f * azar(870, k)), c.y + ry * (-0.9f + 1.8f * azar(871, k)))
                drawLine(n(if (k % 2 == 0) PIEL_OSC else HUESO, oscuro, l).copy(alpha = 0.35f), a, a + Offset(w * 0.007f, w * 0.003f), 1f * u, StrokeCap.Round)
            }
        }
        drawPath(f, n(mezcla(HUESO, PIEL, 0.4f), oscuro, l), style = Stroke(1.6f * u, join = StrokeJoin.Round))
        drawPath(borde(f, w * 0.004f, w * 0.004f), claro(oscuro, 0.35f, l))
    }

    /** La leña apilada contra la pared, cortada por el borde: los troncos de punta, con su corteza y sus anillos. */
    private fun DrawScope.lena(pie: Offset, w: Float, oscuro: Boolean, l: Luz) {
        val u = density
        val r = w * 0.03f
        val sitios = listOf(Offset(-1.9f, 0f), Offset(0f, 0f), Offset(1.9f, 0f), Offset(-0.95f, -1.62f), Offset(0.95f, -1.62f), Offset(0f, -3.2f))
        drawOval(sombra(oscuro, 0.4f), Offset(pie.x - r * 3.2f, pie.y + r * 0.6f), Size(r * 7f, r * 0.9f))
        sitios.forEachIndexed { i, o ->
            val c = pie + o * r
            val rr = r * (0.88f + 0.18f * azar(880, i))
            // El tronco que se va hacia atrás, arriba a la izquierda, y la cara cortada delante.
            val lado = Path().apply { moveTo(c.x - rr, c.y); lineTo(c.x - rr - r * 0.7f, c.y - r * 0.5f); lineTo(c.x + rr - r * 0.7f, c.y - rr - r * 0.5f); lineTo(c.x + rr * 0.3f, c.y - rr); close() }
            drawPath(lado, n(mezcla(PIEL_OSC, HONDO, 0.3f), oscuro, l))
            drawCircle(sombra(oscuro, 0.4f), rr * 1.04f, c + Offset(rr * 0.12f, rr * 0.12f))
            drawCircle(n(PIEL_OSC, oscuro, l), rr, c)
            drawCircle(n(Color(0xFFC9A877), oscuro, l), rr * 0.8f, c)
            drawCircle(n(mezcla(Color(0xFFC9A877), PIEL, 0.5f), oscuro, l), rr * 0.55f, c, style = Stroke(0.8f * u))
            drawCircle(n(mezcla(Color(0xFFC9A877), PIEL, 0.5f), oscuro, l), rr * 0.3f, c, style = Stroke(0.8f * u))
            drawLine(n(HONDO, oscuro, l), c, c + Offset(rr * 0.7f, -rr * 0.2f), 0.9f * u)
            drawArc(claro(oscuro, 0.4f, l), 150f, 120f, false, Offset(c.x - rr * 0.8f, c.y - rr * 0.8f), Size(rr * 1.6f, rr * 1.6f), style = Stroke(1.2f * u))
        }
    }

    /** El fogón: el cerco de piedras, la ceniza, los carbones, las brasas, un hueso quemado y la llama. */
    private fun DrawScope.fogon(c: Offset, w: Float, oscuro: Boolean) {
        val u = density
        val rx = w * 0.105f; val ry = w * 0.034f
        val l = Luz(1f, BRASA, true)
        val lPiedra = Luz(0.85f, mezcla(LLAMA, BRASA, 0.5f), true)
        // La ceniza que se extiende alrededor.
        drawOval(n(mezcla(CENIZA, TIERRA, 0.35f), oscuro, lPiedra).copy(alpha = 0.7f), Offset(c.x - rx * 1.45f, c.y - ry * 1.05f), Size(rx * 2.9f, ry * 2.4f))
        drawOval(n(CENIZA, oscuro, lPiedra), Offset(c.x - rx * 0.9f, c.y - ry * 0.75f), Size(rx * 1.8f, ry * 1.5f))
        // Los carbones y las brasas.
        drawOval(n(HOLLIN, oscuro, SIN_LUZ), Offset(c.x - rx * 0.55f, c.y - ry * 0.35f), Size(rx * 1.1f, ry * 0.8f))
        for (k in 0 until 9) {
            val q = Offset(c.x + rx * (-0.5f + azar(890, k)), c.y + ry * (-0.3f + 0.6f * azar(891, k)))
            val rr = (1.4f + 1.8f * azar(892, k)) * u
            drawCircle(if (k % 3 == 0) LLAMA else BRASA, rr, q)
        }
        for (k in 0 until 3) {
            val q = Offset(c.x + rx * (-0.3f + 0.3f * k), c.y + ry * (0.05f - 0.1f * k))
            rotate(-25f + 30f * k, q) { drawRoundRect(n(HOLLIN, oscuro, l), Offset(q.x - rx * 0.28f, q.y - 1.6f * u), Size(rx * 0.56f, 3.2f * u), androidx.compose.ui.geometry.CornerRadius(1.6f * u)) }
        }
        // El hueso quemado, en el borde de la ceniza.
        val hb = Offset(c.x + rx * 0.75f, c.y + ry * 0.75f)
        rotate(-12f, hb) {
            drawLine(n(Color(0xFFD9D0C2), oscuro, lPiedra), hb + Offset(-rx * 0.22f, 0f), hb + Offset(rx * 0.22f, 0f), 2.6f * u, StrokeCap.Round)
            drawCircle(n(Color(0xFFD9D0C2), oscuro, lPiedra), 2.4f * u, hb + Offset(-rx * 0.24f, -0.6f * u))
            drawCircle(n(Color(0xFFD9D0C2), oscuro, lPiedra), 2.4f * u, hb + Offset(-rx * 0.24f, 1.2f * u))
            drawLine(n(HOLLIN, oscuro, l), hb + Offset(rx * 0.08f, 0f), hb + Offset(rx * 0.24f, 0f), 2.6f * u, StrokeCap.Round)
        }
        // El cerco de piedras: las de atrás primero.
        val piedras = (0 until 10).map { k -> val a = 6.2832f * k / 10 + 0.2f; Offset(c.x + rx * cos(a), c.y + ry * sin(a)) }.sortedBy { it.y }
        piedras.forEachIndexed { i, q ->
            val r = w * (0.016f + 0.006f * azar(895, i))
            val f = piedra(q, r * 1.25f, r * 0.85f, 896 + i, 7, 0.18f)
            drawPath(corrido(f, r * 0.25f, r * 0.3f), sombra(oscuro, 0.4f))
            val ll = if (oscuro) lPiedra else SIN_LUZ
            drawPath(f, n(mezcla(CALIZA_MED, CALIZA_OSC, 0.3f + 0.4f * azar(897, i)), oscuro, ll))
            drawPath(borde(f, 0f, -r * 0.3f), sombra(oscuro, 0.3f))
            // El lado que mira al fuego, de noche, encendido; de día, el de la izquierda, a la luz de la boca.
            if (oscuro) drawPath(borde(f, (q.x - c.x) * 0.15f, (q.y - c.y) * 0.4f), BRASA.copy(alpha = 0.65f))
            else drawPath(borde(f, r * 0.25f, r * 0.3f), LUZ_DIA.copy(alpha = 0.4f))
            drawPath(f, HOLLIN.copy(alpha = 0.25f), style = Stroke(0.6f * u))
        }
        // La llama: chica de día, más alta de noche; y el humo, apenas.
        llama(c + Offset(-rx * 0.15f, -ry * 0.1f), w * (if (oscuro) 0.07f else 0.048f), true)
        llama(c + Offset(rx * 0.18f, -ry * 0.05f), w * (if (oscuro) 0.045f else 0.03f), false)
        if (!oscuro) {
            val humo = abierta(listOf(c + Offset(0f, -w * 0.06f), c + Offset(w * 0.012f, -w * 0.11f), c + Offset(-w * 0.006f, -w * 0.16f), c + Offset(w * 0.01f, -w * 0.21f)))
            drawPath(humo, CENIZA.copy(alpha = 0.22f), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
            drawPath(humo, CENIZA.copy(alpha = 0.15f), style = Stroke(w * 0.022f, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- los compartimentos: nichos de la roca

    /**
     * Tapa de noche el aura en reposo de `Modifier.halo()` alrededor del nicho (la misma solución que Helénico): `halo()` calca
     * todo lo que pinta el compartimento, lo tiñe de claro y lo corre unos 2.5 dp; sobre la roca de noche eso es un contorno
     * pálido. El aro oscuro va en una capa propia compuesta con `SrcAtop`: en la capa del aura no agrega nada; en el dibujo
     * normal oscurece la franja. Al tocar el nicho el aura crece más allá del aro y se sigue viendo.
     */
    private fun DrawScope.cubreAura(oscuro: Boolean, rellenos: List<Path>) {
        if (!oscuro) return
        val ancho = 2.9f * density; val junto = 1.7f * density
        drawIntoCanvas { it.saveLayer(Rect(-ancho * 4, -ancho * 4, size.width + ancho * 4, size.height + ancho * 4), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        for ((color, a) in listOf(NOCHE.copy(alpha = 0.3f) to ancho, NOCHE.copy(alpha = 0.55f) to junto)) {
            rellenos.forEach { drawPath(it, color, style = Stroke(a * 2, join = StrokeJoin.Round), blendMode = BlendMode.Src) }
        }
        drawIntoCanvas { it.restore() }
    }

    /** Las tres bocas de nicho, en unidades de su caja (0 a 1): el arco, la grieta y el entrante bajo la visera. */
    private val ARCO = listOf(Offset(0.06f, 1f), Offset(0f, 0.62f), Offset(0.07f, 0.28f), Offset(0.28f, 0.05f), Offset(0.55f, 0f), Offset(0.8f, 0.1f),
        Offset(0.96f, 0.38f), Offset(1f, 0.7f), Offset(0.94f, 1f), Offset(0.5f, 1.03f))
    private val GRIETA = listOf(Offset(0f, 0.58f), Offset(0.12f, 0.3f), Offset(0.34f, 0.1f), Offset(0.6f, 0.05f), Offset(0.84f, 0.18f), Offset(1f, 0.48f),
        Offset(0.9f, 0.86f), Offset(0.62f, 1f), Offset(0.3f, 1f), Offset(0.1f, 0.86f))
    private val VISERA = listOf(Offset(0.03f, 1f), Offset(0f, 0.5f), Offset(0.05f, 0.08f), Offset(0.3f, 0f), Offset(0.7f, 0.02f), Offset(0.96f, 0.07f),
        Offset(1f, 0.52f), Offset(0.97f, 1f), Offset(0.5f, 1.03f))

    /**
     * La boca del nicho de tipo [tipo] en una celda de [w] × [h]: cada semilla la hace más grande o más chica y la corre a un
     * lado, para que la rejilla no se lea. Todas asientan su piso a la misma altura (el nombre va debajo).
     */
    private fun bocaNicho(tipo: Int, w: Float, h: Float, s: Int): Path {
        val piso = h * 0.61f
        val e = 0.95f + 0.05f * azar(s, 500)
        val (anchoF, altoF, puntos) = when (tipo) {
            0 -> Triple(0.76f + 0.1f * azar(s, 502), 0.94f + 0.06f * azar(s, 503), ARCO)
            1 -> Triple(0.9f + 0.1f * azar(s, 502), 0.62f + 0.12f * azar(s, 503), GRIETA)
            else -> Triple(0.82f + 0.14f * azar(s, 502), 0.74f + 0.1f * azar(s, 503), VISERA)
        }
        val aw = w * 0.92f * anchoF * e
        val x0 = w * 0.04f + (w * 0.92f - aw) * azar(s, 501)
        val ah = (piso - h * 0.015f) * altoF * e
        val incl = if (tipo == 1) (azar(s, 504) - 0.5f) * 0.22f else 0f
        return cerrada(puntos.mapIndexed { k, q ->
            Offset(x0 + aw * (q.x + (azar(s, k + 600) - 0.5f) * 0.06f), piso - ah + ah * (q.y + (azar(s, k + 620) - 0.5f) * 0.06f) + ah * incl * (q.x - 0.5f))
        })
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val l = luzDelHueco(c)
        // De día, la fila de arriba y la columna de la izquierda (las que miran a la boca) un poco más claras.
        val apagado = if (c.cols == cols) (0.045f * c.fila + 0.04f * c.col).coerceAtMost(0.2f) else 0.04f
        fun col(x: Color) = if (oscuro) n(x, true, l) else mezcla(x, SOMBRA_MUEBLE, apagado)
        val mezclada = (c.semilla xor (c.semilla ushr 7)) ushr 1
        val tipo = if (c.vacio || c.sinTema) 0 else mezclada % 3
        val boca = bocaNicho(tipo, w, h, c.semilla)
        val r = boca.getBounds()
        val pisoY = r.bottom - r.height * 0.07f
        // La visera, si la hay: un labio grueso que sale por encima y más ancho que el entrante.
        val visera = if (tipo != 2) null else {
            val vx0 = maxOf(r.left - w * 0.05f, 0f); val vx1 = minOf(r.right + w * 0.05f, w)
            val vt = r.top - h * 0.03f; val vb = r.top + r.height * 0.16f
            cerrada(listOf(Offset(vx0, (vt + vb) / 2)) +
                List(5) { k -> val t = (k + 1) / 6f; Offset(vx0 + (vx1 - vx0) * t, vt + (vb - vt) * (0.15f * azar(c.semilla, k + 30) - 0.3f * sin(t * 3.1416f))) } +
                listOf(Offset(vx1, (vt + vb) / 2 + (vb - vt) * 0.1f)) +
                List(5) { k -> val t = 1f - (k + 1) / 6f; Offset(vx0 + (vx1 - vx0) * t, vb + (vb - vt) * (0.35f * sin(t * 3.1416f) + 0.15f * azar(c.semilla, k + 40))) })
        }

        // ---- el hueco en la roca: el labio con luz, lo hondo, la pared de dentro que coge la luz y el piso
        cubreAura(oscuro, listOfNotNull(corrido(boca, 2f * u, 2.6f * u), corrido(boca, -1.8f * u, -1.8f * u), visera))
        drawPath(corrido(boca, 2f * u, 2.6f * u), col(mezcla(CALIZA_MED, CALIZA_OSC, 0.55f)))
        drawPath(corrido(boca, -1.8f * u, -1.8f * u), col(mezcla(CALIZA_LUZ, CALCITA, 0.3f)))
        drawPath(boca, col(mezcla(HONDO, CALIZA_OSC, 0.25f)))
        clipPath(boca) {
            drawPath(corrido(boca, r.width * 0.16f, r.height * 0.02f), col(mezcla(CALIZA_OSC, CALIZA_MED, 0.35f)))
            drawPath(corrido(boca, -r.width * 0.02f, r.height * 0.1f), col(mezcla(HONDO, CALIZA_OSC, 0.25f)))
            drawPath(corrido(boca, -r.width * 0.06f, r.height * 0.18f), col(HONDO))
            // El piso del nicho, de tierra y ceniza, y su borde con luz.
            val piso = Path().apply { moveTo(r.left, pisoY - r.height * 0.03f); cubicTo(r.left + r.width * 0.3f, pisoY - r.height * 0.07f, r.right - r.width * 0.3f, pisoY - r.height * 0.06f, r.right, pisoY - r.height * 0.03f); lineTo(r.right, r.bottom + 2); lineTo(r.left, r.bottom + 2); close() }
            drawPath(piso, col(mezcla(CALIZA_OSC, CENIZA, 0.35f)))
            drawPath(borde(piso, 0f, r.height * 0.02f), col(mezcla(CALIZA_MED, CALCITA, 0.3f)))
            // Ceniza y migas de carbón en el piso.
            for (k in 0 until 6) {
                val q = Offset(r.left + r.width * (0.1f + 0.8f * azar(c.semilla, k + 200)), pisoY - r.height * (0.005f + 0.04f * azar(c.semilla, k + 201)))
                drawOval(col(if (k % 2 == 0) CENIZA else HOLLIN), Offset(q.x - 1.6f * u, q.y - 0.8f * u), Size(3.2f * u, 1.6f * u))
            }
            // El vacío con hollín: la mancha que dejó una lámpara.
            if (c.vacio && Math.floorMod(c.semilla / 7919, 2) == 0) {
                drawOval(HOLLIN.copy(alpha = 0.5f), Offset(r.left + r.width * 0.2f, r.top - r.height * 0.1f), Size(r.width * 0.6f, r.height * 0.55f))
                drawOval(HOLLIN.copy(alpha = 0.4f), Offset(r.left + r.width * 0.32f, r.top - r.height * 0.15f), Size(r.width * 0.36f, r.height * 0.4f))
            }
            // De noche, la pared del fondo recibe la luz: desde arriba, la de la lámpara; desde abajo, el rojo de las brasas.
            if (oscuro && l.k > 0.2f) {
                val cy = if (l.deAbajo) r.bottom - r.height * 0.15f else r.top + r.height * 0.35f
                drawOval(l.tinte.copy(alpha = 0.16f * l.k), Offset(r.center.x - r.width * 0.45f, cy - r.height * 0.4f), Size(r.width * 0.9f, r.height * 0.8f))
                drawOval(l.tinte.copy(alpha = 0.2f * l.k), Offset(r.center.x - r.width * 0.28f, cy - r.height * 0.25f), Size(r.width * 0.56f, r.height * 0.5f))
            }
            // Bajo la visera, la sombra honda que arroja hacia dentro.
            if (visera != null) drawPath(corrido(visera, w * 0.02f, r.height * 0.28f), col(HONDO).copy(alpha = 0.85f))
            if (ab > 0f) mancha(Offset(r.center.x, pisoY - r.height * 0.25f), r.width * 0.7f, (if (oscuro) LLAMA else LUZ_DIA).copy(alpha = 0.5f * ab))
        }
        if (visera != null) {
            val g = r.height * 0.1f
            drawPath(visera, col(CALIZA_MED))
            drawPath(borde(visera, 0f, -g), col(mezcla(CALIZA_MED, CALIZA_OSC, 0.55f)))
            drawPath(borde(visera, w * 0.006f, g * 0.35f), col(mezcla(CALIZA_LUZ, CALCITA, 0.3f)))
            // Dos goteos de calcita bajo el labio, chatos y redondos.
            for (k in 0 until 2) {
                val x = r.left + r.width * (0.3f + 0.4f * k + 0.1f * azar(c.semilla, k + 50))
                val y0 = r.top + r.height * 0.2f
                drawOval(col(mezcla(CALCITA, CALIZA_MED, 0.3f)), Offset(x - w * 0.012f, y0 - g * 0.4f), Size(w * 0.024f, g * 1.3f))
                drawCircle(if (oscuro) l.tinte.copy(alpha = 0.6f * l.k) else Color.White.copy(alpha = 0.75f), 1f * u, Offset(x - w * 0.004f, y0 + g * 0.5f))
            }
        }

        val sube = ab * r.height * 0.08f
        if (c.vacio) {
            if (Math.floorMod(c.semilla / 7919, 2) != 0) telarana(Offset(r.left + r.width * 0.12f, r.top + r.height * 0.22f), r.width * 0.5f, oscuro, l)
            else {
                // Un tizón que quedó.
                val q = Offset(r.center.x + r.width * 0.12f, pisoY - r.height * 0.02f)
                rotate(-8f, q) { drawRoundRect(col(HOLLIN), Offset(q.x - r.width * 0.2f, q.y - 2.2f * u), Size(r.width * 0.4f, 4.4f * u), androidx.compose.ui.geometry.CornerRadius(2.2f * u)) }
            }
        } else {
            val k = c.n
            when {
                c.sinTema -> {
                    // Las lajas revueltas: unas tumbadas, otras de canto, torcidas, alguna rota.
                    val m = k.coerceIn(1, 6)
                    for (i in 0 until m) {
                        val ancho = r.width * (0.26f + 0.14f * azar(c.semilla, i + 10))
                        val alto = r.height * (0.36f + 0.24f * azar(c.semilla, i + 11))
                        val x = r.left + r.width * (0.2f + 0.6f * azar(c.semilla, i + 12))
                        val giro = -60f + 120f * azar(c.semilla, i + 13)
                        laja(Rect(x - ancho / 2, pisoY - alto - sube, x + ancho / 2, pisoY - sube), giro, c.semilla + i, oscuro, l, ::col)
                    }
                }
                tipo == 0 -> {
                    // De pie, apoyadas unas en otras, de tamaños distintos.
                    val m = k.coerceIn(1, 7)
                    for (i in 0 until m) {
                        val ancho = r.width * (0.74f / maxOf(m, 3)) * (1.1f + 0.4f * azar(c.semilla, i + 50))
                        val alto = r.height * (0.52f + 0.26f * azar(c.semilla, i + 60))
                        val x = r.left + r.width * (0.14f + 0.66f * (i + 0.5f) / m) - ancho / 2
                        laja(Rect(x, pisoY - alto - sube, x + ancho, pisoY - sube), -10f + 20f * azar(c.semilla, i + 70), c.semilla + i, oscuro, l, ::col)
                    }
                    terron(Offset(r.right - r.width * 0.13f, pisoY - r.height * 0.05f), r.width * 0.1f, OCRE_ROJO, c.semilla + 5, oscuro, l, if (oscuro) 1f else -1f)
                }
                tipo == 1 -> {
                    // De plano, apiladas: la pila crece con las fichas.
                    val m = k.coerceIn(1, 8)
                    val grueso = r.height * 0.1f
                    for (i in 0 until m) {
                        val ancho = r.width * (0.4f + 0.22f * azar(c.semilla, i + 80))
                        val x = r.left + r.width * (0.42f + 0.1f * (azar(c.semilla, i + 81) - 0.5f)) - ancho / 2
                        val y = pisoY - grueso * (i + 1) - sube * (i + 1) / m
                        lajaPlana(Rect(x, y, x + ancho, y + grueso), c.semilla + i, oscuro, l, ::col)
                    }
                    conchita(Offset(r.right - r.width * 0.17f, pisoY - r.height * 0.02f), r.width * 0.18f, oscuro, l, ::col)
                }
                else -> {
                    // En abanico, abiertas desde un mismo pie.
                    val m = k.coerceIn(1, 7)
                    val pieA = Offset(r.left + r.width * 0.42f, pisoY - sube)
                    for (i in 0 until m) {
                        val giro = if (m == 1) 0f else -40f + 80f * i / (m - 1)
                        val ancho = r.width * (0.2f + 0.07f * azar(c.semilla, i + 91)); val alto = r.height * (0.56f + 0.16f * azar(c.semilla, i + 90))
                        laja(Rect(pieA.x - ancho / 2, pieA.y - alto, pieA.x + ancho / 2, pieA.y), giro, c.semilla + i, oscuro, l, ::col)
                    }
                    huesoGrabado(Offset(r.right - r.width * 0.22f, pisoY - r.height * 0.03f), r.width * 0.4f, oscuro, l, ::col)
                }
            }
            // Algo dejado en el labio o al lado del nicho, fuera de él.
            when (Math.floorMod(c.semilla / 5, 4)) {
                0 -> huesoGrabado(Offset(minOf(r.right - w * 0.04f, w * 0.86f), r.bottom + h * 0.012f), w * 0.22f, oscuro, l, ::col)
                1 -> {
                    val q = Offset(minOf(r.right + w * 0.01f, w * 0.94f), r.bottom + h * 0.0f)
                    // El terrón y la mancha que dejó al frotarlo contra la roca.
                    drawPath(piedra(q + Offset(-w * 0.01f, -h * 0.05f), w * 0.022f, h * 0.05f, c.semilla + 9, 8, 0.35f), col(OCRE_ROJO).copy(alpha = 0.55f))
                    terron(q, w * 0.036f, OCRE_ROJO, c.semilla + 11, oscuro, l, -1f)
                }
                2 -> raspador(Offset(maxOf(r.left + w * 0.02f, w * 0.06f), r.bottom + h * 0.01f), w * 0.05f, oscuro, l, ::col)
                else -> {}
            }
            // Bajo el nicho: tres puntos de ocre si tiene tema; un trazo de carbón si no.
            val y = r.bottom + h * 0.022f
            if (c.sinTema) drawPath(abierta(listOf(Offset(r.center.x - w * 0.14f, y + 1.5f * u), Offset(r.center.x - w * 0.04f, y - 1f * u), Offset(r.center.x + w * 0.06f, y + 1.2f * u), Offset(r.center.x + w * 0.14f, y - 0.5f * u))),
                col(MANGANESO), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            else for (i in -1..1) drawCircle(col(OCRE_ROJO), (1.8f + 0.8f * azar(c.semilla, i + 700)) * u, Offset(r.center.x + i * w * 0.045f, y + (if (i == 0) -1f else 0.6f) * u))
        }
    }

    /** Un raspador de sílex: una lasca gruesa con el frente retocado a golpecitos. */
    private fun DrawScope.raspador(pie: Offset, r: Float, oscuro: Boolean, l: Luz, col: (Color) -> Color) {
        val f = cerrada(listOf(Offset(pie.x - r, pie.y), Offset(pie.x - r * 0.8f, pie.y - r * 0.5f), Offset(pie.x, pie.y - r * 0.7f), Offset(pie.x + r * 0.9f, pie.y - r * 0.4f), Offset(pie.x + r, pie.y)))
        drawOval(sombra(oscuro, 0.4f), Offset(pie.x - r * 0.8f, pie.y - r * 0.1f), Size(r * 2.1f, r * 0.3f))
        volumen(f, col(SILEX), oscuro, l, -1f, 1.2f, 0.45f)
        for (k in 0 until 5) drawCircle(col(mezcla(SILEX, CALCITA, 0.5f)), r * 0.09f, Offset(pie.x - r * 0.6f + k * r * 0.35f, pie.y - r * (0.5f + 0.15f * sin(k * 1.3f))))
    }

    /**
     * Una laja de pie (o girada [giro] grados sobre su pie): caliza crema grisácea con su canto a la vista, el contorno
     * desportillado (alguna, rota), su lado de luz y de sombra, su sombra en el fondo del nicho y sus marcas a mano.
     */
    private fun DrawScope.laja(r: Rect, giro: Float, semilla: Int, oscuro: Boolean, l: Luz, col: (Color) -> Color) {
        val u = density
        val rota = Math.floorMod(semilla, 5) == 0
        val rr = if (rota) Rect(r.left, r.top + r.height * 0.32f, r.right, r.bottom) else r
        rotate(giro, Offset(r.center.x, r.bottom)) {
            val f = contornoIrregular(rr, semilla, if (rota) 11 else 13, if (rota) 0.15f else 0.07f)
            val t = rr.width * 0.13f
            if (oscuro && l.deAbajo) drawPath(corrido(f, rr.width * 0.12f, -rr.height * 0.16f), sombra(true, 0.35f + 0.3f * l.k))
            else drawPath(corrido(f, rr.width * (0.22f + (if (oscuro) 0.08f * l.k else 0f)), rr.height * 0.04f), sombra(oscuro, if (oscuro) 0.3f + 0.35f * l.k else 0.4f))
            drawPath(corrido(f, t, t * 0.35f), col(mezcla(CALIZA_OSC, CALIZA_MED, 0.4f)))
            drawPath(f, col(mezcla(mezcla(LAJA, CALIZA_MED, 0.35f * azar(semilla, 3)), CALCITA, 0.25f * azar(semilla, 4))))
            drawPath(borde(f, -rr.width * 0.16f, 0f), sombra(oscuro, 0.16f))
            drawPath(borde(f, rr.width * 0.08f, rr.height * 0.05f), if (oscuro) l.tinte.copy(alpha = 0.4f * l.k) else LUZ_DIA.copy(alpha = 0.55f))
            for (k in 0 until 6) drawCircle(col(CALIZA_OSC).copy(alpha = 0.5f), (0.4f + 0.6f * azar(semilla, k + 300)) * u,
                Offset(rr.left + rr.width * (0.15f + 0.7f * azar(semilla, k + 301)), rr.top + rr.height * (0.1f + 0.8f * azar(semilla, k + 302))))
            marcas(rr, semilla, oscuro, l, col)
        }
    }

    /** Lo que alguien marcó en la laja, a mano y desigual: rayas de ocre, puntos, un zigzag o líneas grabadas. */
    private fun DrawScope.marcas(r: Rect, semilla: Int, oscuro: Boolean, l: Luz, col: (Color) -> Color) {
        val u = density
        val ocre = col(OCRE_ROJO)
        when (Math.floorMod(semilla / 3, 4)) {
            0 -> for (k in 0 until 2 + Math.floorMod(semilla, 3)) {
                val y = r.top + r.height * (0.22f + 0.17f * k + 0.05f * (azar(semilla, k + 400) - 0.5f))
                val x0 = r.left + r.width * (0.18f + 0.18f * azar(semilla, k + 410))
                drawLine(ocre.copy(alpha = 0.55f + 0.4f * azar(semilla, k + 420)), Offset(x0, y),
                    Offset(x0 + r.width * (0.18f + 0.4f * azar(semilla, k + 430)), y + r.height * 0.05f * (azar(semilla, k + 440) - 0.5f)), (0.8f + 1f * azar(semilla, k + 450)) * u, StrokeCap.Round)
            }
            1 -> for (k in 0 until 3 + Math.floorMod(semilla, 4)) drawCircle(ocre.copy(alpha = 0.75f), (0.8f + 1f * azar(semilla, k + 460)) * u,
                Offset(r.left + r.width * (0.22f + 0.56f * azar(semilla, k + 470)), r.top + r.height * (0.18f + 0.55f * azar(semilla, k + 480))))
            2 -> {
                val z = Path()
                for (k in 0..4) {
                    val q = Offset(r.left + r.width * (0.24f + 0.12f * k), r.top + r.height * (0.36f + (if (k % 2 == 0) -0.035f else 0.035f) + 0.02f * azar(semilla, k + 490)))
                    if (k == 0) z.moveTo(q.x, q.y) else z.lineTo(q.x, q.y)
                }
                drawPath(z, ocre.copy(alpha = 0.75f), style = Stroke(1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            else -> for (k in 0 until 3) {
                // Grabadas con la punta de un sílex: el surco en sombra y su labio con luz.
                val x = r.left + r.width * (0.3f + 0.16f * k + 0.05f * azar(semilla, k + 495))
                val a = Offset(x, r.top + r.height * 0.22f); val b = Offset(x + r.width * 0.08f * (azar(semilla, k + 496) - 0.5f), r.top + r.height * (0.5f + 0.15f * azar(semilla, k + 497)))
                drawLine(sombra(oscuro, 0.55f), a, b, 1f * u, StrokeCap.Round)
                drawLine(claro(oscuro, 0.5f, l), a + Offset(-0.8f * u, 0f), b + Offset(-0.8f * u, 0f), 0.6f * u)
            }
        }
    }

    /** Una laja tumbada, vista de frente: el canto grueso, la cara de arriba apenas, el contorno desportillado. */
    private fun DrawScope.lajaPlana(r: Rect, semilla: Int, oscuro: Boolean, l: Luz, col: (Color) -> Color) {
        val f = contornoIrregular(r, semilla, 10, 0.12f)
        drawPath(corrido(f, r.width * 0.04f, r.height * 0.45f), sombra(oscuro, 0.4f))
        drawPath(f, col(mezcla(CALIZA_OSC, CALIZA_MED, 0.45f + 0.3f * azar(semilla, 5))))
        clipPath(f) { drawRect(col(mezcla(LAJA, CALCITA, 0.3f * azar(semilla, 4))), Offset(r.left - 1f, r.top - 1f), Size(r.width + 2f, r.height * 0.45f)) }
        drawPath(borde(f, r.width * 0.05f, 0f), if (oscuro) l.tinte.copy(alpha = 0.4f * l.k) else LUZ_DIA.copy(alpha = 0.4f))
        drawPath(borde(f, -r.width * 0.05f, 0f), sombra(oscuro, 0.25f))
        if (Math.floorMod(semilla, 3) == 0) drawLine(col(OCRE_ROJO).copy(alpha = 0.7f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.7f), Offset(r.left + r.width * 0.55f, r.top + r.height * 0.68f), 1f * density, StrokeCap.Round)
    }

    /** Una concha chica, de pie en el nicho: un abanico con costillas. */
    private fun DrawScope.conchita(pie: Offset, r: Float, oscuro: Boolean, l: Luz, col: (Color) -> Color) {
        val u = density
        val f = cerrada(listOf(Offset(pie.x - r * 0.15f, pie.y), Offset(pie.x - r * 0.75f, pie.y - r * 0.55f), Offset(pie.x - r * 0.4f, pie.y - r * 0.95f),
            Offset(pie.x + r * 0.4f, pie.y - r * 0.95f), Offset(pie.x + r * 0.75f, pie.y - r * 0.55f), Offset(pie.x + r * 0.15f, pie.y)))
        drawOval(sombra(oscuro, 0.4f), Offset(pie.x - r * 0.5f, pie.y - r * 0.08f), Size(r * 1.3f, r * 0.2f))
        volumen(f, col(mezcla(HUESO, OCRE_AMAR, 0.3f)), oscuro, l, if (oscuro && !l.deAbajo) 1f else -1f, 1f, 0.45f)
        clipPath(f) { for (k in -2..2) drawLine(col(mezcla(PIEL_OSC, OCRE_ROJO, 0.4f)).copy(alpha = 0.6f), pie, Offset(pie.x + k * r * 0.3f, pie.y - r), 1f * u) }
    }

    /** Un hueso largo con muescas grabadas, tumbado. */
    private fun DrawScope.huesoGrabado(pie: Offset, largo: Float, oscuro: Boolean, l: Luz, col: (Color) -> Color) {
        val u = density
        val a = Offset(pie.x - largo / 2, pie.y); val b = Offset(pie.x + largo / 2, pie.y - largo * 0.08f)
        val g = largo * 0.08f
        drawLine(sombra(oscuro, 0.4f), a + Offset(2 * u, 2 * u), b + Offset(2 * u, 2 * u), g, StrokeCap.Round)
        drawLine(col(HUESO), a, b, g, StrokeCap.Round)
        drawCircle(col(HUESO), g * 0.7f, a + Offset(0f, -g * 0.3f)); drawCircle(col(HUESO), g * 0.7f, a + Offset(0f, g * 0.3f))
        drawCircle(col(HUESO), g * 0.7f, b + Offset(0f, -g * 0.3f)); drawCircle(col(HUESO), g * 0.7f, b + Offset(0f, g * 0.3f))
        drawLine(sombra(oscuro, 0.25f), a + Offset(0f, g * 0.3f), b + Offset(0f, g * 0.3f), g * 0.3f, StrokeCap.Round)
        for (k in 0 until 6) {
            val t = 0.25f + 0.09f * k
            val q = a + (b - a) * t
            drawLine(col(mezcla(PIEL_OSC, MANGANESO, 0.4f)), q + Offset(0f, -g * 0.4f), q + Offset(g * 0.15f, g * 0.3f), 0.9f * u)
        }
    }

    /** Una telaraña en la esquina del nicho vacío. */
    private fun DrawScope.telarana(c: Offset, r: Float, oscuro: Boolean, l: Luz) {
        val u = density
        val hilo = if (oscuro) mezcla(CALCITA, l.tinte, 0.3f).copy(alpha = 0.18f + 0.4f * l.k) else CALCITA.copy(alpha = 0.6f)
        val rayos = (0 until 6).map { k -> val a = -0.2f + 1.9f * k / 5f; Offset(c.x + r * cos(a), c.y + r * sin(a) * 0.9f) }
        rayos.forEach { drawLine(hilo, c, it, 0.7f * u) }
        for (j in 1..4) {
            val f = j / 4.5f
            val p = Path()
            rayos.forEachIndexed { i, q -> val e = c + (q - c) * f; if (i == 0) p.moveTo(e.x, e.y) else p.lineTo(e.x, e.y) }
            drawPath(p, hilo, style = Stroke(0.6f * u))
        }
    }

    override val rotulo = Rect(-0.03f, 0.64f, 1.03f, 0.95f)
    override fun tintaRotulo(oscuro: Boolean) = if (oscuro) TINTA_NOCHE else TINTA_DIA
    override val letraRotulo: FontFamily = LetraExtra.abrigo
    override val salida = 0f
    /** La laja con «y N más», tirada sobre la piel. */
    override val placa = Rect(0.08f, 0.48f, 0.4f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = Color(0xFF6E220E)

    // ---------------------------------------------------------------- el interior y las fichas (no cambian)

    /** Una mano en negativo: el ocre soplado alrededor y la silueta de la mano en el color de la roca. */
    private fun DrawScope.manoNegativa(c: Offset, r: Float, giro: Float, oscuro: Boolean) {
        mancha(c, r * 1.7f, OCRE.copy(alpha = if (oscuro) 0.4f else 0.5f))
        mancha(c, r * 1.1f, OCRE.copy(alpha = 0.25f))
        rotate(giro, c) {
            val piel = roca(ROCA, oscuro)
            drawOval(piel, Offset(c.x - r * 0.42f, c.y - r * 0.15f), Size(r * 0.84f, r * 0.8f))
            val dedos = listOf(-0.32f to 0.55f, -0.12f to 0.72f, 0.08f to 0.75f, 0.27f to 0.62f)
            dedos.forEach { (dx, l) -> drawLine(piel, Offset(c.x + r * dx, c.y), Offset(c.x + r * dx * 1.15f, c.y - r * l), r * 0.17f, StrokeCap.Round) }
            drawLine(piel, Offset(c.x - r * 0.35f, c.y + r * 0.2f), Offset(c.x - r * 0.72f, c.y - r * 0.1f), r * 0.19f, StrokeCap.Round)
            drawRect(piel, Offset(c.x - r * 0.28f, c.y + r * 0.5f), Size(r * 0.56f, r * 0.6f))
        }
    }

    /** El contorno de una laja en el rectángulo [r]: el borde desportillado, siempre el mismo para cada [semilla]. */
    private fun lajaPath(r: Rect, semilla: Int): Path = Path().apply {
        val puntos = 18
        for (k in 0 until puntos) {
            val t = k / puntos.toFloat()
            // Un punto del perímetro del rectángulo, empujado hacia adentro un poco al azar.
            val (x, y) = when {
                t < 0.25f -> r.left + r.width * (t / 0.25f) to r.top
                t < 0.5f -> r.right to r.top + r.height * ((t - 0.25f) / 0.25f)
                t < 0.75f -> r.right - r.width * ((t - 0.5f) / 0.25f) to r.bottom
                else -> r.left to r.bottom - r.height * ((t - 0.75f) / 0.25f)
            }
            val adentro = minOf(r.width, r.height) * (0.015f + 0.05f * azar(semilla, k)) * (if (k % 4 == 0) 1.8f else 1f)
            val px = x + (if (x <= r.left + 0.5f) adentro else if (x >= r.right - 0.5f) -adentro else 0f)
            val py = y + (if (y <= r.top + 0.5f) adentro else if (y >= r.bottom - 0.5f) -adentro else 0f)
            if (k == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del nicho: piedra con tierra, ceniza, manchas de ocre y trozos de carbón.
        drawRect(Brush.verticalGradient(listOf(roca(Color(0xFF5A4838), oscuro), roca(Color(0xFF7A6550), oscuro), roca(Color(0xFF5E4B3A), oscuro))))
        for (k in 0 until 140) {
            val c = Offset(w * azar(81, k), h * azar(82, k))
            mancha(c, (8f + 30f * azar(83, k)) * u, (if (k % 4 == 0) roca(ROCA_LUZ, oscuro) else roca(ROCA_HONDA, oscuro)).copy(alpha = 0.12f))
        }
        for (k in 0 until 60) {
            val c = Offset(w * azar(84, k), h * azar(85, k)); val r = (1.2f + 3.2f * azar(86, k)) * u
            drawOval(roca(if (k % 3 == 0) ROCA_LUZ else ROCA_OSC, oscuro).copy(alpha = 0.7f), Offset(c.x - r, c.y - r * 0.7f), Size(2 * r, 1.4f * r))
        }
        for (k in 0 until 6) mancha(Offset(w * azar(87, k), h * azar(88, k)), (16f + 20f * azar(89, k)) * u, OCRE.copy(alpha = 0.18f))
        for (k in 0 until 8) {
            val c = Offset(w * azar(90, k), h * azar(91, k))
            rotate(azar(92, k) * 180f, c) { drawRoundRect(CARBON.copy(alpha = 0.8f), Offset(c.x - 7 * u, c.y - 1.6f * u), Size(14 * u, 3.2f * u), androidx.compose.ui.geometry.CornerRadius(1.6f * u)) }
        }
        // Las paredes de roca alrededor, en sombra, y la luz del fuego que entra desde abajo.
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.6f)), Offset(w / 2, h * 0.55f), maxOf(w, h) * 0.75f))
        mancha(Offset(w / 2, h * 1.05f), w * 0.9f, FUEGO.copy(alpha = if (oscuro) 0.3f else 0.2f))
        manoNegativa(Offset(w * 0.1f, h * 0.06f), 14 * u, -20f, true)
        manoNegativa(Offset(w * 0.9f, h * 0.08f), 12 * u, 25f, true)
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFF2E6CF)
    override val pared: Dp = 10.dp
    override val columnas = 2
    override val lineas = 4

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(lajaPath(Rect(0f, 0f, s.width, s.height), semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        // La luz de lado: más clara arriba a la izquierda, más oscura abajo a la derecha, como una piedra con volumen.
        drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.14f), Color.Transparent, Color.Black.copy(alpha = 0.14f)), Offset.Zero, Offset(w, h)))
        // Poros y granos de la caliza.
        val n = if (grande) 110 else 34
        for (k in 0 until n) drawCircle(ROCA_OSC.copy(alpha = 0.12f + 0.12f * azar(semilla, k)), (0.5f + 1.3f * azar(semilla, k + 1000)) * u, Offset(w * azar(semilla, k + 2000), h * azar(semilla, k + 3000)))
        // Dos vetas.
        for (k in 0 until 2) {
            val y0 = h * (0.3f + 0.4f * azar(semilla, k + 4000))
            val pth = Path().apply { moveTo(0f, y0); quadraticTo(w * 0.5f, y0 + h * (azar(semilla, k + 4100) - 0.5f) * 0.3f, w, y0 + h * 0.1f) }
            drawPath(pth, ROCA_OSC.copy(alpha = 0.16f), style = Stroke(0.8f * u * (if (grande) 1.5f else 1f)))
        }
        // Una mancha de ocre y unas muescas de carbón abajo a la izquierda, como quien llevó la cuenta.
        mancha(Offset(w * 0.08f, h * 0.9f), minOf(w, h) * 0.22f, OCRE.copy(alpha = 0.22f))
        val g = if (grande) 1.6f else 1f
        for (k in 0 until 4) drawLine(CARBON.copy(alpha = 0.45f), Offset(w * 0.06f + k * 4 * u * g, h - 12 * u * g), Offset(w * 0.06f + k * 4 * u * g + 1 * u, h - 6 * u * g), 1.1f * u * g, StrokeCap.Round)
    }

    override val margenes = MargenesFicha(9.dp, 13.dp, 14.dp, 20.dp, 28.dp, 34.dp)
    override val proporcionFicha = 1.12f
    override val anchoGrande = 1f
    override val proporcionGrande = 0.95f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Cuatro muescas en el hueso: lo contado.
            "hecho" -> { for (k in -1..1) drawLine(CARBON, Offset(c.x + k * s * 0.2f, c.y - s * 0.34f), Offset(c.x + k * s * 0.2f, c.y + s * 0.34f), s * 0.1f, StrokeCap.Round); drawLine(OCRE, Offset(c.x - s * 0.4f, c.y + s * 0.22f), Offset(c.x + s * 0.4f, c.y - s * 0.22f), s * 0.11f, StrokeCap.Round) }
            // Un punto de ocre con su anillo.
            "convence" -> { drawCircle(OCRE, s * 0.24f, c); drawCircle(OCRE, s * 0.42f, c, style = Stroke(s * 0.1f)) }
            // Tres puntos: lo estás pensando.
            "piensa" -> listOf(Offset(0f, -0.28f), Offset(-0.28f, 0.22f), Offset(0.28f, 0.22f)).forEach { o -> drawCircle(OCRE_AMARILLO, s * 0.15f, Offset(c.x + o.x * s, c.y + o.y * s)) }
            // Un círculo de carbón sin rellenar.
            "duda" -> drawCircle(CARBON.copy(alpha = 0.8f), s * 0.34f, c, style = Stroke(s * 0.12f))
            // Dos trazos de carbón cruzados.
            "desacuerdo" -> { drawLine(CARBON, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Offset(c.x + s * 0.36f, c.y + s * 0.36f), s * 0.15f, StrokeCap.Round); drawLine(CARBON, Offset(c.x + s * 0.36f, c.y - s * 0.36f), Offset(c.x - s * 0.36f, c.y + s * 0.36f), s * 0.15f, StrokeCap.Round) }
            else -> {}
        }
    }

    /** Una mano pintada con ocre: la palma y los dedos. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = Offset(center.x, center.y + s * 0.12f)
        drawOval(OCRE, Offset(c.x - s * 0.24f, c.y - s * 0.12f), Size(s * 0.48f, s * 0.42f))
        listOf(-0.18f to 0.3f, -0.06f to 0.4f, 0.06f to 0.42f, 0.17f to 0.34f).forEach { (dx, l) ->
            drawLine(OCRE, Offset(c.x + s * dx, c.y), Offset(c.x + s * dx * 1.2f, c.y - s * l), s * 0.1f, StrokeCap.Round)
        }
        drawLine(OCRE, Offset(c.x - s * 0.2f, c.y + s * 0.08f), Offset(c.x - s * 0.42f, c.y - s * 0.06f), s * 0.11f, StrokeCap.Round)
    }

    override val desorden = 2f
}
