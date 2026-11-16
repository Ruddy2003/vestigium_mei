package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Mongol
// =====================================================================

/**
 * Mongol: el fondo de la ger de un pastor de la estepa hacia 1220, el sitio de honor frente a la puerta, donde se apilan los
 * arcones de la familia. La pared es la de la ger: el fieltro crema y, delante, el enrejado del khana (listones rojizos en
 * rombos, cada cruce atado con cuero crudo); arriba bajan las varas del techo, pintadas, cortadas por el borde, sobre la cuerda
 * de crin que ciñe el enrejado. Los arcones van en filas, cada fila sobre una tabla gruesa de madera con su veta (la tabla de
 * una fila es el apoyo de la de encima) y todo sobre una tarima baja pintada. Encima, lo de cada día: la silla de montar (lo
 * primero que se ve, a la izquierda), con su borrén alto chapado de plata, el faldón de cuero, la manta roja que cuelga por el
 * canto de la tabla y el estribo de hierro; sobre el borrén, el gorro forrado de piel. Detrás, colgados de una estaca del
 * khana, la funda del arco recurvo (asoma el arco) y el carcaj con sus flechas. A la derecha, la brida con su bocado y las
 * riendas colgada del enrejado, dos cuencos de madera encimados (el de arriba forrado de plata), la lámpara de sebo de
 * hierro en su trípode y el odre de cuero del airag apoyado contra el khana. Al pie, la alfombra de fieltro rojo acolchada
 * con pespuntes, un par de botas y una mesa baja pintada con un cuenco de leche, cortada por el borde. De día la luz cae del
 * techo, fuera del cuadro arriba a la izquierda: una mancha en el fieltro y un haz oblicuo con polvo sobre la silla y los
 * arcones de arriba. Cada tema es lo que se guarda en un hueco, según la semilla: un arcón pintado (naranja o rojo, con
 * frisos azules y volutas doradas, esquineras de hierro y la pintura gastada), un baúl forrado de cuero con correas, hebillas
 * y clavos, o una caja de corteza de abedul cosida con raíz; las tablillas de abedul asoman bajo la tapa (más cuantas más
 * fichas) y el tema va en un panel claro con filete azul. Lo que no tiene tema va en un saco de cuero crudo abierto, con las
 * tablillas sueltas, y su panel lleva un pespunte en lugar del filete. Un hueco sin tema es la tabla vacía con un fieltro
 * doblado, o con un cuenco volcado y un rollo de cuerda. De noche, pardo rojizo hondo: alumbran la lámpara de sebo (halo en
 * el fieltro y el khana, brillos en la plata y el hierro) y el fuego del centro de la ger, fuera del cuadro abajo (un
 * resplandor rojizo rasante en el suelo, la tarima y la fila de abajo); cada compartimento suma la luz de las dos según su
 * fila y su columna, así que las filas de en medio se pierden, y lo que se apaga baja hacia su propio color oscuro.
 * Por dentro, fieltro blanco con pespuntes rojos. Las fichas son tablillas de corteza de abedul.
 */
object IdeasMongol : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del mueble
    /** El fieltro de la pared (crema) y el del suelo y el techo (gris pardo). */
    private val FIELTRO_PARED = Color(0xFFE6DCC4)
    private val FIELTRO_GRIS = Color(0xFFB7AA92)
    /** El rojizo de los listones del khana y de las varas. */
    private val KHANA = Color(0xFFB45A2E)
    /** La pintura de los arcones: naranja, rojo, azul y oro. */
    private val NARANJA_A = Color(0xFFD27532)
    private val ROJO_A = Color(0xFFA63A2C)
    private val AZUL_A = Color(0xFF2F5C8A)
    private val ORO_A = Color(0xFFD6A64A)
    /** La madera de las tablas y la que asoma donde se gastó la pintura. */
    private val MADERA = Color(0xFF94673E)
    private val MADERA_OSC = Color(0xFF5C3B23)
    /** El cuero curtido y el cuero crudo. */
    private val CUERO = Color(0xFF7C4A2A)
    private val CUERO_CLARO = Color(0xFFB98A57)
    /** La plata de la silla y del cuenco, y el hierro del estribo, del bocado y de la lámpara. */
    private val PLATA = Color(0xFFD3D2CC)
    private val HIERRO = Color(0xFF4A4347)
    /** La piel de zorro del gorro. */
    private val PIEL = Color(0xFFB07A48)
    /** El panel claro del rótulo. */
    private val PANEL = Color(0xFFF2E7CC)
    /** La tinta de los rótulos: pardo casi negro. */
    private val TINTA_ROTULO = Color(0xFF24160E)
    /** La noche de la ger (pardo rojizo hondo), la llama del sebo y el fuego del hogar. */
    private val NOCHE = Color(0xFF2C1410)
    private val LLAMA = Color(0xFFFFC56C)
    private val FUEGO = Color(0xFFE5683A)
    /** La sombra de noche: pardo violeta casi negro. */
    private val SOMBRA_NOCHE = Color(0xFF140A0B)
    /**
     * El panel del rótulo de noche: de [PANEL_NOCHE] (lejos de las luces) a [PANEL_LUZ] (junto a ellas). Con [TINTA_ROTULO],
     * el de lejos da unos 7.5:1 en el título y más de 5:1 en el subtítulo (78 %).
     */
    private val PANEL_NOCHE = Color(0xFFBFA983)
    private val PANEL_LUZ = Color(0xFFF2DDB0)

    // ---------------------------------------------------------------- los colores del interior y las fichas (no cambian)
    private val FIELTRO = Color(0xFFEDE6D6)
    private val NARANJA = Color(0xFFD9712A)
    private val ROJO = Color(0xFFB0302A)
    private val AZUL = Color(0xFF1F5FA8)
    private val AZUL_OSC = Color(0xFF163F70)
    private val ORO = Color(0xFFD8A23A)
    private val ABEDUL = Color(0xFFF2EEE4)
    private val LENTICELA = Color(0xFF3A3530)
    private val TINTA = Color(0xFF18202E)
    private val CREMA = Color(0xFFF4ECD8)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas
    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.5f
    override val base = 0.2f
    override val lado = 0.06f
    override val hueco = 0.05f

    /** Lo que baja la tarima de abajo por debajo de la base (fracción del ancho): ahí empieza el suelo. */
    private const val TARIMA = 0.04f
    /** El alto de la franja del techo, con sus varas. */
    private const val TECHO = 0.11f

    private fun anchoH() = (1f - 2f * lado - hueco * (cols - 1)) / cols
    private fun altoH() = anchoH() / proporcion
    /** Donde empieza el suelo, en fracciones del ancho, con [filas] filas. */
    private fun pisoY(filas: Int) = corona + hueco + filas * altoH() + (filas - 1) * hueco + hueco + TARIMA

    // ---------------------------------------------------------------- la luz y la noche
    /** La lámpara de sebo: su pie (sobre la tabla de arriba) y su llama, en fracciones del ancho. */
    private const val LAMPARA_X = 0.705f
    private val llama get() = Offset(LAMPARA_X + 0.027f, corona + hueco * 0.22f - 0.1f)
    /** El fuego del centro de la ger: fuera del cuadro, abajo, delante. */
    private fun hogar(filas: Int) = Offset(0.45f, pisoY(filas) + 0.32f)

    private fun deLampara(p: Offset) = (1.15f * (1f - (p - llama).getDistance() / 0.72f)).coerceAtLeast(0f)
    private fun deHogar(p: Offset, filas: Int) = (1.05f * (1f - (p - hogar(filas)).getDistance() / 1.0f)).coerceAtLeast(0f)

    /** Cuánto alumbran el punto [p] (fracciones del ancho) las dos luces juntas: arriba la lámpara, abajo el hogar. */
    private fun calor(p: Offset, filas: Int) = (deLampara(p) + deHogar(p, filas)).coerceIn(0f, 1f)

    /** El color de la luz que llega a [p]: amarilla del sebo cerca de la lámpara, rojiza cerca del hogar. */
    private fun luzDe(p: Offset, filas: Int): Color {
        val a = deLampara(p); val b = deHogar(p, filas)
        return if (a + b <= 0.0001f) LLAMA else mezcla(LLAMA, FUEGO, b / (a + b))
    }

    /** La luz que manda en [p]. */
    private fun luzMayor(p: Offset, filas: Int) = if (deLampara(p) >= deHogar(p, filas)) llama else hogar(filas)

    /**
     * Un color a [k] (0 a 1) de las luces: de día, tal cual (aclarado [dia]). De noche se apaga hacia **su propio** oscuro (se
     * baja el valor conservando el matiz: el naranja se vuelve pardo rojizo, el fieltro pardo cálido), con un poco de la noche
     * rojiza de la ger; cerca de una luz sube el valor y toma su color ([luz]).
     */
    private fun n(c: Color, oscuro: Boolean, k: Float, dia: Float = 0f, luz: Color = LLAMA): Color {
        if (!oscuro) return if (dia > 0f) mezcla(c, LUZ_MUEBLE, dia) else c
        val q = k.coerceIn(0f, 1f)
        val v = 0.2f + 0.64f * q.pow(1.1f)
        // El aire de la ger de noche es cálido: lo neutro (el fieltro, el abedul) se apaga hacia pardo, no hacia gris.
        val bajo = Color(c.red * v, c.green * v * 0.86f, c.blue * v * 0.7f, c.alpha)
        return mezcla(mezcla(bajo, NOCHE, 0.3f * (1f - q)), luz, 0.2f * q)
    }

    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) SOMBRA_NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    private fun brillo(oscuro: Boolean, a: Float, k: Float = 0f, luz: Color = LLAMA) =
        if (oscuro) luz.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else luzMueble(a)

    // ---------------------------------------------------------------- ayudantes
    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    private fun poli(vararg v: Float) = Path().apply {
        moveTo(v[0], v[1]); var i = 2
        while (i < v.size) { lineTo(v[i], v[i + 1]); i += 2 }
        close()
    }

    /** Una forma irregular alrededor de [o] (gastado de la pintura, manchas). */
    private fun mancho(o: Offset, rx: Float, ry: Float, semilla: Int, lados: Int = 8): Path = Path().apply {
        for (k in 0 until lados) {
            val a = 6.283f * k / lados + azar(semilla, k) * 0.5f
            val r = 0.65f + 0.45f * azar(semilla, k + 20)
            val q = Offset(o.x + rx * r * cos(a), o.y + ry * r * sin(a))
            if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
        }
        close()
    }

    /**
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz, el claro del
     * lado de la luz y, si [lustre] es más de 0, un brillo. De día la luz viene de la izquierda; de noche, del lado [haciaX]
     * (1 si la luz está a la derecha), con un filo cálido tanto más fuerte cuanto más [k].
     */
    private fun DrawScope.volumen(forma: Path, oscuro: Boolean, k: Float, haciaX: Float, luz: Color, lustre: Float = 0f, fuerza: Float = 1f) {
        val r = forma.getBounds(); val a = r.width; val hh = r.height
        val sl = if (oscuro) haciaX else -1f
        drawPath(borde(forma, sl * a * 0.26f, -hh * 0.02f), sombra(oscuro, 0.2f * fuerza))
        drawPath(borde(forma, sl * a * 0.1f, -hh * 0.01f), sombra(oscuro, 0.16f * fuerza))
        clipPath(forma) {
            drawOval(if (oscuro) luz.copy(alpha = 0.2f * k) else luzMueble(0.2f), Offset(if (sl < 0) r.left + a * 0.1f else r.right - a * 0.34f, r.top + hh * 0.08f), Size(a * 0.24f, hh * 0.7f))
            if (lustre > 0f) drawRoundRect(if (oscuro) luz.copy(alpha = (lustre * 1.4f * k).coerceAtMost(0.7f)) else luzMueble(lustre),
                Offset(if (sl < 0) r.left + a * 0.2f else r.right - a * 0.25f, r.top + hh * 0.16f), Size(a * 0.05f, hh * 0.36f), CornerRadius(a * 0.03f))
        }
        if (oscuro && k > 0.03f) drawPath(borde(forma, -sl * a * 0.045f, hh * 0.03f), luz.copy(alpha = (0.06f + 0.5f * k).coerceAtMost(0.55f)))
    }

    /** Una voluta de nube: una espiral corta. */
    private fun DrawScope.voluta(c: Offset, r: Float, color: Color, grosor: Float, espejo: Boolean = false) {
        val p = Path()
        for (k in 0..16) {
            val a = k * 0.55f
            val rr = r * (1f - k / 18f)
            val x = c.x + (if (espejo) -1 else 1) * rr * cos(a); val y = c.y + rr * sin(a)
            if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- la pared, para el mueble y para tapar el aura

    /** Cuánto se ensucia de sombra el fieltro hacia el suelo, de día; las manchas de luz del techo (centro, radio, alfa). */
    private const val SUCIO = 0.16f
    private val MANCHAS_DIA = listOf(Triple(Offset(0.04f, 0.12f), 0.95f, 0.42f), Triple(Offset(0.42f, 0.34f), 0.3f, 0.22f))
    /** El halo de la lámpara en el fieltro (radio, color, alfa) y el resplandor rasante del hogar. */
    private val MANCHAS_LAMPARA = listOf(Triple(0.72f, LLAMA, 0.14f), Triple(0.36f, Color(0xFFFF9E52), 0.2f), Triple(0.13f, LLAMA, 0.34f))
    private const val RASANTE = 0.22f

    /**
     * El color del fieltro de la pared en ([x], [y]) (fracciones del ancho), con [filas] filas, sin pinceladas ni grano: el
     * mismo que pinta [mueble] (el fieltro, la sombra de abajo y las manchas de luz, o de noche los halos de las dos luces).
     */
    private fun fondoPared(x: Float, y: Float, filas: Int, oscuro: Boolean): Color {
        val p = Offset(x, y)
        val piso = pisoY(filas)
        fun f(o: Offset, r: Float) = (1f - (p - o).getDistance() / r).coerceAtLeast(0f)
        if (!oscuro) {
            var c = FIELTRO_PARED
            if (y < piso) c = mezcla(c, SOMBRA_MUEBLE, SUCIO * ((y - (piso - 0.9f)) / 0.9f).coerceIn(0f, 1f))
            MANCHAS_DIA.forEach { (o, r, a) -> c = mezcla(c, LUZ_MUEBLE, a * f(o, r)) }
            return c
        }
        var c = n(FIELTRO_PARED, true, 0f)
        MANCHAS_LAMPARA.forEach { (r, col, a) -> c = mezcla(c, col, a * f(llama, r)) }
        val alto = piso - TARIMA + base
        c = mezcla(c, FUEGO, RASANTE * ((y - (piso - 1f)) / (alto - (piso - 1f))).coerceIn(0f, 1f))
        c = mezcla(c, FUEGO, 0.16f * f(hogar(filas), 0.95f))
        return c
    }

    /**
     * El enrejado del khana, con sus sombras en el fieltro, el canto a la luz y las ataduras de cuero crudo, en la zona
     * ([x0], [y0])–([x1], [y1]) (fracciones del ancho). Se pinta en píxeles con el origen en ([ox], [oy]) y [W] píxeles por
     * ancho: [mueble] lo pinta entero; [cubreAura], solo el trozo que hay detrás de un compartimento, con [modo].
     */
    private fun DrawScope.celosia(ox: Float, oy: Float, W: Float, x0: Float, y0: Float, x1: Float, y1: Float, filas: Int, oscuro: Boolean, modo: BlendMode) {
        val u = density
        fun px(f: Offset) = Offset((f.x - ox) * W, (f.y - oy) * W)
        fun k(f: Offset) = if (oscuro) calor(f, filas) else 0f
        fun lz(f: Offset) = if (oscuro) luzDe(f, filas) else LLAMA
        val P = 0.15f
        val mitad = P / 2f
        val lw = 0.0105f * W
        val yo = TECHO + P * 0.2f
        val xk = 0.04f
        val crudo = mezcla(CUERO_CLARO, FIELTRO_PARED, 0.35f)
        val mMin = kotlin.math.floor((y0 - yo) / mitad).toInt() - 1
        val mMax = kotlin.math.ceil((y1 - yo) / mitad).toInt() + 1
        val cruces = mutableListOf<Offset>()
        for (capa in 0..1) for (m in mMin..mMax) {
            val ya = yo + m * mitad; val yb = ya + mitad
            val jMin = kotlin.math.floor((x0 - P - xk - m * mitad) / P).toInt()
            val jMax = kotlin.math.ceil((x1 + P - xk - m * mitad) / P).toInt()
            for (j in jMin..jMax) {
                val x = xk + m * mitad + j * P
                val a = Offset(x, ya)
                val b = if (capa == 0) Offset(x + mitad, yb) else Offset(x - mitad, yb)
                val mid = (a + b) / 2f
                if (mid.y < TECHO - mitad || mid.x < x0 - mitad || mid.x > x1 + mitad || mid.y < y0 - mitad || mid.y > y1 + mitad) continue
                val pa = px(a); val pb = px(b)
                val col = mezcla(mezcla(KHANA, FIELTRO_PARED, 0.18f), MADERA, 0.3f * azar(820 + capa, m * 31 + j))
                drawLine(sombra(oscuro, 0.15f), pa + Offset(2.2f * u, 2.4f * u), pb + Offset(2.2f * u, 2.4f * u), lw, StrokeCap.Round, blendMode = modo)
                drawLine(n(col, oscuro, k(mid), 0f, lz(mid)), pa, pb, lw, StrokeCap.Round, blendMode = modo)
                val off = if (capa == 1) Offset(-lw * 0.28f, -lw * 0.28f) else Offset(lw * 0.3f, -lw * 0.3f)
                drawLine(brillo(oscuro, if (capa == 1) 0.4f else 0.18f, k(mid), lz(mid)), pa + off, pb + off, lw * 0.25f, blendMode = modo)
                if (capa == 1) cruces += a
            }
        }
        // Las ataduras: una vuelta de cuero crudo en cada cruce.
        cruces.forEach { f ->
            if (f.y <= TECHO) return@forEach
            val p = px(f)
            drawOval(sombra(oscuro, 0.25f), Offset(p.x - lw * 0.6f, p.y - lw * 0.45f), Size(lw * 1.6f, lw * 1.25f), blendMode = modo)
            drawOval(n(crudo, oscuro, k(f), 0f, lz(f)), Offset(p.x - lw * 0.8f, p.y - lw * 0.62f), Size(lw * 1.6f, lw * 1.25f), blendMode = modo)
            drawLine(n(mezcla(CUERO, crudo, 0.4f), oscuro, k(f), 0f, lz(f)), Offset(p.x - lw * 0.55f, p.y - lw * 0.35f), Offset(p.x + lw * 0.5f, p.y + lw * 0.35f), 0.8f * u, blendMode = modo)
            drawCircle(brillo(oscuro, 0.4f, k(f), lz(f)), lw * 0.22f, Offset(p.x - lw * 0.35f, p.y - lw * 0.25f), blendMode = modo)
        }
    }

    /**
     * Tapa el aura en reposo de `Modifier.halo()` alrededor de lo guardado: `halo()` calca lo que pinta el compartimento, lo
     * tiñe y lo corre unos 2.5 dp, y quedaba como un contorno pálido. Aquí un aro de unos 3.3 dp alrededor de cada una de
     * [siluetas] toma **lo que hay detrás**: el fieltro ([fondoPared], por tramos), el khana ([celosia]), las sombras que el
     * mueble deja en la pared y, debajo del hueco, la tabla donde se apoya. Va en una capa compuesta con `SrcAtop`, así que
     * en la capa del aura no agrega nada (al tocarlo, el aura crece y se sigue viendo). Se pinta antes que lo guardado.
     */
    private fun DrawScope.cubreAura(c: EstadoCajon, oscuro: Boolean, siluetas: List<Path>) {
        if (c.cols != cols) return
        val w = size.width; val h = size.height
        val aw = anchoH(); val ah = altoH()
        val W = w / aw
        val ox = lado + c.col * (aw + hueco)
        val oy = corona + hueco + c.fila * (ah + hueco)
        val ancho = 3.3f * density
        val ext = ancho * 3
        drawIntoCanvas { it.saveLayer(Rect(-ext, -ext, w + ext, h + ext), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        siluetas.forEach { drawPath(it, Color.Black, style = Stroke(ancho * 2, join = StrokeJoin.Round)) }
        // El fieltro, por tramos.
        val tx = 6; val ty = 6
        for (i in 0 until tx) for (j in 0 until ty) {
            val xa = if (i == 0) -ext else w * i / tx
            val xb = if (i == tx - 1) w + ext else w * (i + 1) / tx
            val ya = if (j == 0) -ext else h * j / ty
            val yb = h * (j + 1) / ty
            val col = fondoPared(ox + (xa + xb) / 2 / W, oy + (ya + yb) / 2 / W, c.filas, oscuro)
            drawRect(col, Offset(xa, ya), Size(xb - xa, yb - ya), blendMode = BlendMode.SrcIn)
        }
        // Debajo del hueco: la cara de arriba de la tabla, con la sombra del fondo y la del arcón.
        val ultima = c.fila == c.filas - 1
        val diaT = if (oscuro) 0f else 0.1f * (1f - (c.fila + 1) / 6f).coerceAtLeast(0f)
        val colT = if (ultima) mezcla(MADERA, FIELTRO_GRIS, 0.2f) else mezcla(MADERA, LUZ_MUEBLE, 0.22f)
        for (i in 0 until tx) {
            val xa = if (i == 0) -ext else w * i / tx
            val xb = if (i == tx - 1) w + ext else w * (i + 1) / tx
            val f = Offset(ox + (xa + xb) / 2 / W, oy + ah + 0.01f)
            val kk = if (oscuro) calor(f, c.filas) else 0f
            var col = n(colT, oscuro, kk, diaT, if (oscuro) luzDe(f, c.filas) else LLAMA)
            col = mezcla(col, if (oscuro) SOMBRA_NOCHE else SOMBRA_MUEBLE, if (oscuro) 0.25f else 0.22f)
            col = mezcla(col, if (oscuro) SOMBRA_NOCHE else SOMBRA_MUEBLE, if (oscuro) ((0.4f + 0.3f * kk) * 1.15f).coerceAtMost(1f) else 0.38f)
            drawRect(col, Offset(xa, h), Size(xb - xa, ext), blendMode = BlendMode.SrcIn)
        }
        // El khana encima del fieltro, y la sombra que la tabla de encima deja en la pared.
        clipRect(-ext, -ext, w + ext, h) {
            celosia(ox, oy, W, ox - 0.02f, oy - 0.02f, ox + aw + 0.02f, oy + ah + 0.02f, c.filas, oscuro, BlendMode.SrcAtop)
            if (!oscuro) {
                drawRect(sombra(false, 0.24f), Offset(-ext, 0f), Size(w + ext * 2, 0.014f * W), blendMode = BlendMode.SrcAtop)
                drawRect(sombra(false, 0.1f), Offset(-ext, 0.014f * W), Size(w + ext * 2, 0.022f * W), blendMode = BlendMode.SrcAtop)
            } else drawRect(sombra(true, 0.35f), Offset(-ext, 0f), Size(w + ext * 2, 0.02f * W), blendMode = BlendMode.SrcAtop)
        }
        drawIntoCanvas { it.restore() }
    }

    /**
     * La sombra que lo guardado deja en la pared: de día, a la derecha y un poco abajo; de noche, lejos de la luz que manda y
     * tanto más marcada cuanto más cerca esté ([k]). Se pinta con `SrcAtop` sobre lo que ya hay debajo (el mueble), así que no
     * entra en el aura; no baja de la tabla ([h]).
     */
    private fun DrawScope.sombraEnPared(siluetas: List<Path>, oscuro: Boolean, k: Float, lejos: Offset) {
        val w = size.width; val h = size.height
        val W = w / anchoH()
        val d = if (!oscuro) Offset(0.022f * W, 0.012f * W) else lejos * (0.03f * W)
        val a = if (!oscuro) 0.17f else (0.6f * k).coerceAtMost(0.55f)
        if (a < 0.02f) return
        val todo = siluetas.drop(1).fold(siluetas.first()) { acc, p -> Path().apply { op(acc, p, PathOperation.Union) } }
        clipRect(-w, -h, w * 2, h - 0.004f * W) { drawPath(corrido(todo, d.x, d.y), sombra(oscuro, a), blendMode = BlendMode.SrcAtop) }
    }

    // ---------------------------------------------------------------- el mueble
    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.bottom
        val sep = d.separacion
        val nf = d.filas.size
        val piso = d.base.top + w * TARIMA
        val techo = w * TECHO
        fun kf(p: Offset) = if (oscuro) calor(Offset(p.x / w, p.y / w), nf) else 0f
        fun lz(p: Offset) = if (oscuro) luzDe(Offset(p.x / w, p.y / w), nf) else LLAMA
        fun c(col: Color, p: Offset, dia: Float = 0f) = n(col, oscuro, kf(p), dia, lz(p))
        val llamaP = Offset(w * llama.x, w * llama.y)
        val hogarP = Offset(w * hogar(nf).x, w * hogar(nf).y)

        // ---- el fieltro de la pared
        drawRect(n(FIELTRO_PARED, oscuro, 0f), Offset(0f, 0f), Size(w, piso))
        val pared = Rect(0f, techo, w, piso)
        pinceladas(pared, n(mezcla(FIELTRO_PARED, FIELTRO_GRIS, 0.6f), oscuro, 0.2f), 801, 70, u, 0.12f)
        pinceladas(pared, n(mezcla(FIELTRO_PARED, LUZ_MUEBLE, 0.6f), oscuro, 0.2f), 802, 50, u, 0.12f)
        // Las juntas de los paños de fieltro.
        listOf(0.62f, 1.45f, 2.3f).forEachIndexed { i, f ->
            val y = w * f
            if (y < piso) {
                val junta = Path().apply { moveTo(0f, y); for (j in 1..8) lineTo(w * j / 8f, y + (azar(810 + i, j) - 0.5f) * 3f * u) }
                drawPath(junta, sombra(oscuro, 0.2f), style = Stroke(1.6f * u))
                drawPath(corrido(junta, 0f, 1.6f * u), brillo(oscuro, 0.3f, 0.3f), style = Stroke(1f * u))
            }
        }
        // La luz: de día, la del techo (arriba a la izquierda) y el fieltro más sucio abajo; de noche, el halo de la lámpara y
        // el resplandor rojizo del hogar. Son las mismas cuentas que [fondoPared].
        if (!oscuro) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(SUCIO)), piso - w * 0.9f, piso), Offset(0f, piso - w * 0.9f), Size(w, w * 0.9f))
            MANCHAS_DIA.forEach { (o, r, a) -> mancha(Offset(w * o.x, w * o.y), w * r, luzMueble(a)) }
        } else {
            MANCHAS_LAMPARA.forEach { (r, col, a) -> mancha(llamaP, w * r, col.copy(alpha = a)) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, FUEGO.copy(alpha = RASANTE)), piso - w * 1.0f, alto), Offset(0f, piso - w * 1.0f), Size(w, alto - piso + w * 1.0f))
            mancha(hogarP, w * 0.95f, FUEGO.copy(alpha = 0.16f))
        }

        // ---- el khana: el enrejado de listones, con las cruces atadas con cuero crudo
        clipRect(0f, techo, w, piso) { celosia(0f, 0f, w, -0.1f, TECHO - 0.1f, 1.1f, pisoY(nf) + 0.1f, nf, oscuro, BlendMode.SrcOver) }

        // ---- el techo: el fieltro de la cubierta y las varas pintadas que bajan hasta el khana
        val fondoTecho = Rect(0f, 0f, w, techo)
        // De noche el techo queda más lejos de la lámpara que la pared: le llega menos.
        fun cT(col: Color, p: Offset) = n(col, oscuro, kf(p) * 0.6f, 0f, lz(p))
        drawRect(cT(mezcla(FIELTRO_GRIS, FIELTRO_PARED, 0.4f), Offset(w * 0.5f, techo * 0.5f)), fondoTecho.topLeft, fondoTecho.size)
        pinceladas(fondoTecho, cT(FIELTRO_GRIS, Offset(w * 0.5f, 0f)), 830, 20, u, 0.14f)
        if (!oscuro) mancha(Offset(0f, 0f), w * 0.5f, luzMueble(0.35f))
        val fuga = Offset(w * 0.4f, -w * 2.6f)
        var xv = -w * 0.02f; var iv = 0
        while (xv < w + w * 0.05f) {
            val pie = Offset(xv, techo + w * 0.004f)
            val dir = (fuga - pie) / (fuga - pie).getDistance()
            val arriba = pie + dir * (techo * 1.4f)
            val gv = w * 0.016f
            val pm = Offset(xv, techo * 0.5f)
            drawLine(sombra(oscuro, 0.3f), pie + Offset(gv * 0.6f, 0f), arriba + Offset(gv * 0.6f, 0f), gv * 0.8f)
            drawLine(cT(KHANA, pm), pie, arriba, gv)
            // La punta de abajo, roja, y la franja azul con su filete de oro, pintadas.
            drawLine(cT(ROJO_A, pm), pie, pie + dir * (techo * 0.2f), gv)
            drawLine(cT(AZUL_A, pm), pie + dir * (techo * 0.32f), pie + dir * (techo * 0.42f), gv)
            drawLine(cT(ORO_A, pm), pie + dir * (techo * 0.44f), pie + dir * (techo * 0.47f), gv)
            drawLine(brillo(oscuro, 0.35f, kf(pm) * 0.6f, lz(pm)), pie + Offset(-gv * 0.3f, 0f), arriba + Offset(-gv * 0.3f, 0f), gv * 0.22f)
            xv += w * (0.074f + 0.012f * azar(831, iv)); iv++
        }
        // La cuerda de crin que ciñe la cabeza del khana, torcida.
        val cuerda = Rect(0f, techo - w * 0.006f, w, techo + w * 0.008f)
        drawRect(sombra(oscuro, 0.3f), Offset(0f, cuerda.bottom), Size(w, w * 0.006f))
        drawRect(c(Color(0xFF4E3A2C), Offset(w * 0.5f, techo)), cuerda.topLeft, cuerda.size)
        var xc = 0f
        while (xc < w) { drawLine(c(Color(0xFF8A7058), Offset(xc, techo)), Offset(xc, cuerda.bottom), Offset(xc + w * 0.012f, cuerda.top), 1.4f * u); xc += w * 0.011f }

        // ---- el bastidor: tres pies derechos de palo (a los lados y entre las dos columnas) que sostienen las tablas
        val fondoPies = d.filas.last().bottom
        val pies = listOf(w * 0.026f, w * (0.5f - 0.014f), w * (0.974f - 0.028f))
        pies.forEachIndexed { i, x ->
            val top = if (i == 1) ch + sep * 0.5f else ch - w * 0.035f
            val r = Rect(x, top, x + w * 0.028f, fondoPies + sep * 0.3f)
            if (!oscuro) drawRect(sombra(false, 0.24f), Offset(r.right, r.top + w * 0.01f), Size(w * 0.016f, r.height))
            if (oscuro) drawRect(Brush.verticalGradient(List(5) { q -> c(MADERA, Offset(r.center.x, r.top + r.height * q / 4f)) }, r.top, r.bottom), r.topLeft, r.size)
            else drawRect(MADERA, r.topLeft, r.size)
            vetas(r, c(MADERA_OSC, r.center).copy(alpha = 0.5f), 850 + i, 0.8f, vertical = true)
            drawRect(sombra(oscuro, 0.3f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            drawRect(brillo(oscuro, 0.4f, kf(r.topLeft), lz(r.topLeft)), r.topLeft, Size(r.width * 0.18f, r.height))
            // La cabeza redondeada de los pies de fuera, por encima de la tabla de arriba.
            if (i != 1) {
                drawOval(c(MADERA, r.topLeft), Offset(r.left, r.top - w * 0.008f), Size(r.width, w * 0.016f))
                drawOval(brillo(oscuro, 0.5f, kf(r.topLeft), lz(r.topLeft)), Offset(r.left + r.width * 0.15f, r.top - w * 0.006f), Size(r.width * 0.4f, w * 0.008f))
            }
        }

        // ---- las tablas: una sobre cada fila de arcones y la tarima pintada abajo
        for (j in 0..nf) {
            val y0 = if (j == 0) ch else d.filas[j - 1].bottom
            val y1 = if (j == nf) piso else y0 + sep
            val xa = w * (0.016f + 0.006f * azar(840, j))
            val xb = w * (0.984f - 0.006f * azar(841, j))
            tabla(Rect(xa, y0, xb, y1), j == nf, j, w, oscuro, ::kf, ::lz)
            // Las ataduras de cuero crudo que sujetan la tabla a cada pie.
            if (j < nf) pies.forEach { x ->
                val p = Offset(x + w * 0.014f, y0 + sep * 0.6f)
                val crudo = mezcla(CUERO_CLARO, FIELTRO_PARED, 0.35f)
                for (q in 0 until 3) {
                    val yq = y0 + sep * (0.3f + 0.22f * q)
                    drawLine(sombra(oscuro, 0.3f), Offset(x - w * 0.003f, yq + 1.4f * u), Offset(x + w * 0.031f, yq + w * 0.008f + 1.4f * u), w * 0.006f)
                    drawLine(c(crudo, p), Offset(x - w * 0.004f, yq), Offset(x + w * 0.032f, yq + w * 0.008f), w * 0.006f, StrokeCap.Round)
                    drawLine(brillo(oscuro, 0.45f, kf(p), lz(p)), Offset(x - w * 0.002f, yq - w * 0.002f), Offset(x + w * 0.03f, yq + w * 0.006f), w * 0.0015f)
                }
            }
            // Lo que pisa esta tabla: la sombra de contacto de cada arcón de la fila de encima.
            if (j > 0) d.huecos.filter { kotlin.math.abs(it.bottom - y0) < 2f }.forEach { r ->
                val kk = kf(r.center)
                drawOval(sombra(oscuro, if (oscuro) 0.4f + 0.3f * kk else 0.38f), Offset(r.left + r.width * 0.02f, y0 - sep * 0.04f), Size(r.width * 1.0f, sep * (if (j == nf) 0.3f else 0.34f)))
            }
        }

        // ---- el suelo de fieltro y la alfombra acolchada
        drawRect(c(mezcla(FIELTRO_GRIS, CUERO, 0.2f), Offset(w * 0.45f, piso + w * 0.06f)), Offset(0f, piso), Size(w, alto - piso))
        pinceladas(Rect(0f, piso, w, alto), c(mezcla(FIELTRO_GRIS, SOMBRA_MUEBLE, 0.3f), Offset(w * 0.5f, piso)), 850, 18, u, 0.14f)
        drawRect(sombra(oscuro, 0.4f), Offset(0f, piso), Size(w, 2.2f * u))
        drawRect(sombra(oscuro, 0.14f), Offset(0f, piso + 2.2f * u), Size(w, w * 0.02f))
        alfombra(piso, alto, w, oscuro, ::kf, ::lz)
        if (oscuro) clipRect(0f, piso, w, alto) { mancha(Offset(hogarP.x, alto + w * 0.1f), w * 0.55f, FUEGO.copy(alpha = 0.22f)) }

        // ---- lo de encima de los arcones
        val apoyo = ch + sep * 0.22f
        fun haciaX(x: Float, y: Float) = if (luzMayor(Offset(x / w, y / w), nf).x * w >= x) 1f else -1f
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 0.9f, largo * 0.5f)
            val l = luzMayor(Offset(p.x / w, p.y / w), nf) * w
            val v = p - l
            return v / v.getDistance().coerceAtLeast(1f) * (largo * 1.5f)
        }
        // Colgados del khana: el arco en su funda y el carcaj; la brida.
        val estaca = Offset(w * 0.1f, techo + w * 0.045f)
        val pArco = Offset(w * 0.15f, w * 0.3f)
        arcoYCarcaj(estaca, w, oscuro, kf(pArco), lz(pArco), haciaX(pArco.x, pArco.y), caida(pArco, w * 0.02f))
        val clavoBrida = Offset(w * 0.6f, techo + w * 0.055f)
        val pBrida = Offset(w * 0.6f, w * 0.28f)
        brida(clavoBrida, w, oscuro, kf(pBrida), lz(pBrida), caida(pBrida, w * 0.016f))
        // Sobre la tabla: el odre, la lámpara, los cuencos y la silla (lo primero que se ve).
        // El gorro de piel, colgado de una estaca encima del odre.
        val estacaGorro = Offset(w * 0.875f, techo + w * 0.035f)
        val pGorro = Offset(w * 0.87f, w * 0.22f)
        drawLine(sombra(oscuro, 0.3f), estacaGorro + Offset(1.6f * u, 1.6f * u), Offset(w * 0.872f, w * 0.19f) + Offset(1.6f * u, 1.6f * u), 1.4f * u)
        drawLine(c(CUERO, pGorro), estacaGorro, Offset(w * 0.872f, w * 0.19f), 1.4f * u)
        gorro(Offset(w * 0.87f, w * 0.245f), w * 0.12f, oscuro, kf(pGorro), lz(pGorro), haciaX(pGorro.x, pGorro.y), caida(pGorro, w * 0.012f))
        drawCircle(c(MADERA, estacaGorro), w * 0.008f, estacaGorro)
        val pOdre = Offset(w * 0.87f, apoyo - w * 0.1f)
        odre(w * 0.785f, apoyo, w * 0.165f, w * 0.215f, w, oscuro, kf(pOdre), lz(pOdre), haciaX(pOdre.x, pOdre.y), caida(pOdre, w * 0.025f))
        val pLamp = Offset(w * LAMPARA_X, apoyo - w * 0.04f)
        lampara(Offset(w * LAMPARA_X, apoyo), w, oscuro, kf(pLamp), lz(pLamp))
        val pCuencos = Offset(w * 0.615f, apoyo - w * 0.03f)
        cuencos(w * 0.615f, apoyo, w, oscuro, kf(pCuencos), lz(pCuencos), haciaX(pCuencos.x, pCuencos.y), caida(pCuencos, w * 0.015f))
        val pSilla = Offset(w * 0.38f, apoyo - w * 0.1f)
        silla(w * 0.19f, apoyo, w * 0.37f, ch + sep * 0.92f, w, oscuro, kf(pSilla), lz(pSilla), haciaX(pSilla.x, pSilla.y), caida(pSilla, w * 0.03f))
        if (oscuro) llamaSebo(llamaP, w)

        // ---- al pie: las botas, la placa (fieltro doblado) y la mesa baja cortada por el borde
        val pBotas = Offset(w * 0.1f, piso + w * 0.08f)
        botas(w * 0.075f, piso + w * 0.135f, w, oscuro, kf(pBotas), lz(pBotas), haciaX(pBotas.x, pBotas.y))
        if (d.mas > 0) placa.let { pl -> placaFieltro(d.enBase(pl), oscuro, kf(d.enBase(pl).center), lz(d.enBase(pl).center)) }
        val pMesa = Offset(w * 0.9f, piso + w * 0.07f)
        mesa(w * 0.79f, piso + w * 0.135f, w, oscuro, kf(pMesa), lz(pMesa), haciaX(pMesa.x, pMesa.y))

        // ---- el haz de luz del techo, con su polvo (de día)
        if (!oscuro) {
            val haz = poli(w * 0.0f, 0f, w * 0.24f, 0f, w * 0.72f, ch + sep, w * 0.36f, ch + sep)
            drawPath(haz, Brush.linearGradient(listOf(luzMueble(0.42f), luzMueble(0.2f), luzMueble(0.04f)), Offset(w * 0.1f, 0f), Offset(w * 0.55f, ch + sep * 2f)))
            drawPath(poli(w * 0.06f, 0f, w * 0.14f, 0f, w * 0.56f, ch + sep, w * 0.44f, ch + sep), luzMueble(0.1f))
            clipPath(haz) {
                for (q in 0 until 46) {
                    val f = azar(860, q)
                    val y = (ch + sep) * f
                    val x = w * (0.0f + 0.36f * f) + w * (0.2f + 0.1f * f) * azar(861, q)
                    drawCircle(luzMueble(0.35f + 0.4f * azar(862, q)), (0.6f + 1.4f * azar(863, q)) * u, Offset(x, y))
                }
            }
        }

        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    /**
     * Una tabla gruesa de madera (o, si [tarima], la tarima pintada de abajo) en [r]: la cara de arriba vista apenas desde
     * arriba, el canto con su luz, la cara de delante con la veta, la junta entre dos tablas y los clavos; su sombra en la
     * pared, a la derecha y debajo.
     */
    private fun DrawScope.tabla(r: Rect, tarima: Boolean, j: Int, w: Float, oscuro: Boolean, kf: (Offset) -> Float, lz: (Offset) -> Color) {
        val u = density
        fun c(col: Color, p: Offset, dia: Float = 0f) = n(col, oscuro, kf(p), dia, lz(p))
        val sep = if (tarima) w * hueco else r.height
        val cara = r.top + sep * 0.36f
        // Las sombras en la pared: a la derecha y debajo (sobre el hueco de abajo).
        if (!oscuro) {
            drawRect(sombra(false, 0.26f), Offset(r.right, r.top + w * 0.008f), Size(w * 0.014f, r.height))
            if (!tarima) {
                drawRect(sombra(false, 0.24f), Offset(r.left + w * 0.01f, r.bottom), Size(r.width, w * 0.014f))
                drawRect(sombra(false, 0.1f), Offset(r.left + w * 0.01f, r.bottom + w * 0.014f), Size(r.width, w * 0.022f))
            }
        } else if (!tarima) drawRect(sombra(true, 0.35f), Offset(r.left, r.bottom), Size(r.width, w * 0.02f))
        val dia = if (oscuro) 0f else 0.1f * (1f - j / 6f).coerceAtLeast(0f)
        // La cara de arriba.
        val arriba = Rect(r.left + w * 0.004f, r.top, r.right - w * 0.004f, cara)
        val colArriba = if (tarima) mezcla(MADERA, FIELTRO_GRIS, 0.2f) else mezcla(MADERA, LUZ_MUEBLE, 0.22f)
        if (oscuro) drawRect(Brush.horizontalGradient(List(5) { i -> c(colArriba, Offset(arriba.left + arriba.width * i / 4f, arriba.center.y)) }, arriba.left, arriba.right), arriba.topLeft, arriba.size)
        else drawRect(n(colArriba, false, 0f, dia), arriba.topLeft, arriba.size)
        vetas(arriba, c(MADERA_OSC, arriba.center).copy(alpha = 0.35f), 870 + j, 0.7f)
        drawRect(sombra(oscuro, 0.22f), arriba.topLeft, Size(arriba.width, arriba.height * 0.35f))
        // La cara de delante.
        val frente = Rect(r.left, cara, r.right, r.bottom)
        val colFrente = if (tarima) ROJO_A else MADERA
        if (oscuro) drawRect(Brush.horizontalGradient(List(5) { i -> c(colFrente, Offset(frente.left + frente.width * i / 4f, frente.center.y)) }, frente.left, frente.right), frente.topLeft, frente.size)
        else drawRect(n(colFrente, false, 0f, dia * 0.6f), frente.topLeft, frente.size)
        if (tarima) {
            // La tarima pintada: rojo con un filete de oro y una franja azul, gastada donde se pisa y donde rozan los arcones.
            pinceladas(frente, c(mezcla(ROJO_A, MADERA, 0.5f), frente.center), 880, 30, u, 0.16f)
            val fh = frente.height
            drawRect(c(AZUL_A, frente.center), Offset(frente.left, frente.top + fh * 0.32f), Size(frente.width, fh * 0.22f))
            drawRect(c(ORO_A, frente.center), Offset(frente.left, frente.top + fh * 0.26f), Size(frente.width, 1.4f * u))
            drawRect(c(ORO_A, frente.center), Offset(frente.left, frente.top + fh * 0.57f), Size(frente.width, 1.4f * u))
            var xg = frente.left + w * 0.03f; var q = 0
            while (xg < frente.right - w * 0.03f) {
                drawCircle(c(ORO_A, Offset(xg, frente.center.y)), w * 0.004f, Offset(xg, frente.top + fh * 0.43f))
                xg += w * 0.045f; q++
            }
            for (q2 in 0 until 6) {
                val o = Offset(frente.left + frente.width * (0.05f + 0.9f * azar(881, q2)), frente.top + fh * (0.15f + 0.7f * azar(882, q2)))
                val p = mancho(o, w * (0.015f + 0.02f * azar(883, q2)), w * 0.006f, 884 + q2, 9)
                drawPath(p, c(MADERA, o).copy(alpha = 0.75f))
            }
            // Los pies, cortos, y su sombra en el suelo.
            listOf(frente.left + w * 0.02f, frente.right - w * 0.06f, frente.center.x - w * 0.02f).forEach { x ->
                drawRect(sombra(oscuro, 0.35f), Offset(x + w * 0.012f, frente.bottom), Size(w * 0.05f, w * 0.008f))
                drawRect(c(MADERA_OSC, Offset(x, frente.bottom)), Offset(x, frente.bottom - 1f * u), Size(w * 0.04f, w * 0.008f))
            }
        } else {
            vetas(frente, c(MADERA_OSC, frente.center).copy(alpha = 0.5f), 890 + j, 1.1f)
            pinceladas(frente, c(mezcla(MADERA, LUZ_MUEBLE, 0.4f), frente.center), 891 + j, 10, u, 0.12f)
            // La junta entre dos tablas, con su clavo de hierro a cada lado.
            val xj = r.left + r.width * (0.3f + 0.4f * azar(892, j))
            drawLine(sombra(oscuro, 0.5f), Offset(xj, r.top), Offset(xj, r.bottom), 1.4f * u)
            drawLine(brillo(oscuro, 0.3f, kf(Offset(xj, r.top)), lz(Offset(xj, r.top))), Offset(xj + 1.4f * u, cara), Offset(xj + 1.4f * u, r.bottom), 0.8f * u)
            listOf(r.left + w * 0.02f, xj - w * 0.015f, xj + w * 0.015f, r.right - w * 0.02f).forEach { x ->
                val p = Offset(x, (cara + r.bottom) / 2)
                drawCircle(c(HIERRO, p), w * 0.0045f, p)
                drawCircle(brillo(oscuro, 0.45f, kf(p), lz(p)), w * 0.0018f, p - Offset(w * 0.0015f, w * 0.0015f))
            }
        }
        // El canto: la luz arriba (de noche, el filo de la luz), la sombra abajo y a la derecha.
        drawRect(brillo(oscuro, 0.55f, kf(Offset(r.center.x, cara)), lz(Offset(r.center.x, cara))), Offset(r.left, cara - 0.6f * u), Size(r.width, 1.6f * u))
        drawRect(sombra(oscuro, 0.35f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(sombra(oscuro, 0.25f), Offset(r.right - w * 0.006f, r.top), Size(w * 0.006f, r.height))
        drawRect(brillo(oscuro, 0.3f, kf(r.topLeft), lz(r.topLeft)), Offset(r.left, r.top), Size(1.4f * u, r.height))
    }

    /** La alfombra de fieltro rojo acolchada, vista desde arriba: su cenefa azul, los pespuntes crema y los cuernos cosidos. */
    private fun DrawScope.alfombra(piso: Float, alto: Float, w: Float, oscuro: Boolean, kf: (Offset) -> Float, lz: (Offset) -> Color) {
        val u = density
        fun c(col: Color, p: Offset) = n(col, oscuro, kf(p), 0f, lz(p))
        val y0 = piso + w * 0.025f
        val forma = poli(w * 0.1f, y0, w * 0.9f, y0, w * 1.02f, alto + w * 0.02f, -w * 0.02f, alto + w * 0.02f)
        val centro = Offset(w * 0.5f, (y0 + alto) / 2)
        drawPath(corrido(forma, w * 0.008f, -w * 0.004f), sombra(oscuro, 0.3f))
        drawPath(forma, c(ROJO_A, centro))
        clipPath(forma) {
            pinceladas(Rect(0f, y0, w, alto), c(mezcla(ROJO_A, NOCHE, 0.3f), centro), 900, 20, u, 0.16f)
            // La cenefa azul del borde de atrás y los pespuntes.
            drawRect(c(AZUL_A, centro), Offset(0f, y0), Size(w, w * 0.016f))
            val punteado = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u))
            val crudo = mezcla(FIELTRO_PARED, ORO_A, 0.2f)
            drawLine(c(crudo, centro).copy(alpha = 0.8f), Offset(0f, y0 + w * 0.024f), Offset(w, y0 + w * 0.024f), 1.2f * u, pathEffect = punteado)
            drawLine(c(crudo, centro).copy(alpha = 0.7f), Offset(0f, alto - w * 0.012f), Offset(w, alto - w * 0.012f), 1.2f * u, pathEffect = punteado)
            // Los cuernos de carnero, cosidos en hilera.
            var x = w * 0.1f; var q = 0
            val ym = (y0 + w * 0.03f + alto) / 2
            while (x < w * 0.95f) {
                val o = Offset(x + (ym - y0) * 0.1f * (x / w - 0.5f), ym)
                val r = w * 0.022f
                val cuerno = Path().apply {
                    moveTo(o.x, o.y + r * 0.8f); lineTo(o.x, o.y)
                    cubicTo(o.x, o.y - r * 1.1f, o.x - r * 1.5f, o.y - r * 1.1f, o.x - r * 1.4f, o.y - r * 0.1f)
                    cubicTo(o.x - r * 1.3f, o.y + r * 0.5f, o.x - r * 0.6f, o.y + r * 0.4f, o.x - r * 0.65f, o.y - r * 0.1f)
                    moveTo(o.x, o.y)
                    cubicTo(o.x, o.y - r * 1.1f, o.x + r * 1.5f, o.y - r * 1.1f, o.x + r * 1.4f, o.y - r * 0.1f)
                    cubicTo(o.x + r * 1.3f, o.y + r * 0.5f, o.x + r * 0.6f, o.y + r * 0.4f, o.x + r * 0.65f, o.y - r * 0.1f)
                }
                drawPath(cuerno, sombra(oscuro, 0.25f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                drawPath(cuerno, c(crudo, o).copy(alpha = 0.85f), style = Stroke(1.2f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
                x += w * 0.12f; q++
            }
            // Lo gastado donde se pisa.
            drawPath(mancho(Offset(w * 0.32f, alto - w * 0.04f), w * 0.12f, w * 0.03f, 905, 10), c(mezcla(ROJO_A, FIELTRO_GRIS, 0.35f), centro).copy(alpha = 0.5f))
            drawRect(brillo(oscuro, 0.3f, kf(Offset(w * 0.5f, y0)), lz(Offset(w * 0.5f, y0))), Offset(0f, y0), Size(w, 1.2f * u))
        }
    }

    // ---------------------------------------------------------------- lo de encima de los arcones

    /**
     * La silla de montar, de lado, con el borrén de delante (alto, a la izquierda) y el de detrás, sobre la tabla de arriba:
     * la manta de fieltro rojo debajo, que cuelga por el canto hasta [caeHasta] con sus pliegues; el faldón de cuero; los
     * borrenes de madera pintada con su placa de plata; el estribo de hierro colgando de su ación; y el gorro de piel en el
     * borrén. [x0] es su extremo izquierdo; [L], su largo.
     */
    private fun DrawScope.silla(x0: Float, pie: Float, L: Float, caeHasta: Float, w: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float, caida: Offset) {
        val u = density
        fun X(f: Float) = x0 + L * f
        fun Y(g: Float) = pie - L * g
        fun c(col: Color, dia: Float = 0f) = n(col, oscuro, k, dia, luz)
        // Los borrenes: dos tablas estrechas de canto, el de delante alto y casi derecho, el de detrás más bajo y echado atrás.
        val delante = Path().apply {
            moveTo(X(0.15f), Y(0.14f))
            cubicTo(X(0.14f), Y(0.28f), X(0.15f), Y(0.42f), X(0.175f), Y(0.5f))
            cubicTo(X(0.19f), Y(0.545f), X(0.25f), Y(0.545f), X(0.255f), Y(0.5f))
            cubicTo(X(0.26f), Y(0.41f), X(0.27f), Y(0.26f), X(0.33f), Y(0.15f))
            close()
        }
        val detras = Path().apply {
            moveTo(X(0.68f), Y(0.15f))
            cubicTo(X(0.74f), Y(0.22f), X(0.77f), Y(0.3f), X(0.785f), Y(0.37f))
            cubicTo(X(0.795f), Y(0.415f), X(0.85f), Y(0.415f), X(0.85f), Y(0.37f))
            cubicTo(X(0.85f), Y(0.28f), X(0.84f), Y(0.2f), X(0.86f), Y(0.14f))
            close()
        }
        val asiento = Path().apply {
            moveTo(X(0.25f), Y(0.17f))
            cubicTo(X(0.3f), Y(0.29f), X(0.37f), Y(0.26f), X(0.5f), Y(0.25f))
            cubicTo(X(0.62f), Y(0.25f), X(0.71f), Y(0.28f), X(0.78f), Y(0.33f))
            lineTo(X(0.8f), Y(0.15f)); close()
        }
        val faldon = Path().apply {
            moveTo(X(0.15f), Y(0.18f)); lineTo(X(0.86f), Y(0.17f))
            cubicTo(X(0.92f), Y(0.12f), X(0.9f), Y(0.03f), X(0.8f), Y(0.01f))
            lineTo(X(0.22f), Y(0.01f))
            cubicTo(X(0.12f), Y(0.03f), X(0.1f), Y(0.12f), X(0.15f), Y(0.18f)); close()
        }
        val manta = Path().apply {
            moveTo(X(0.03f), Y(0.12f))
            cubicTo(X(0.3f), Y(0.15f), X(0.7f), Y(0.15f), X(0.97f), Y(0.11f))
            cubicTo(X(1.0f), pie, X(1.01f), caeHasta - L * 0.05f, X(0.98f), caeHasta)
            cubicTo(X(0.86f), caeHasta + L * 0.03f, X(0.78f), caeHasta - L * 0.01f, X(0.67f), caeHasta + L * 0.025f)
            cubicTo(X(0.55f), caeHasta + L * 0.045f, X(0.45f), caeHasta, X(0.34f), caeHasta + L * 0.035f)
            cubicTo(X(0.22f), caeHasta + L * 0.055f, X(0.1f), caeHasta, X(0.02f), caeHasta + L * 0.015f)
            cubicTo(X(-0.01f), caeHasta - L * 0.05f, X(-0.005f), pie, X(0.03f), Y(0.12f))
            close()
        }
        // La sombra de todo en la pared y en la tabla.
        val silueta = Path().apply { op(delante, detras, PathOperation.Union) }
        val todo = Path().apply { op(silueta, Path().apply { op(asiento, faldon, PathOperation.Union) }, PathOperation.Union) }
        clipRect(-w, -w, w * 2, pie) { drawPath(corrido(todo, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.5f * k else 0.22f)) }
        drawOval(sombra(oscuro, 0.4f), Offset(X(0.02f), pie - L * 0.025f), Size(L * 1.02f, L * 0.05f))

        // La manta: fieltro rojo con la cenefa azul abajo, el pespunte crema, los pliegues y el peso.
        drawPath(manta, c(ROJO_A))
        clipPath(manta) {
            pinceladas(manta.getBounds(), c(mezcla(ROJO_A, NOCHE, 0.3f)), 910, 14, u, 0.16f)
            drawPath(borde(manta, 0f, -L * 0.05f), c(AZUL_A))
            drawPath(corrido(manta, 0f, -L * 0.065f), c(mezcla(FIELTRO_PARED, ORO_A, 0.2f)).copy(alpha = 0.8f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u))))
            // El doblez sobre el canto de la tabla: arriba a la luz, abajo en sombra.
            drawRect(brillo(oscuro, 0.3f, k, luz), Offset(X(0f), pie - L * 0.01f), Size(L, L * 0.012f))
            drawRect(sombra(oscuro, 0.16f), Offset(X(0f), pie + L * 0.005f), Size(L, (caeHasta - pie) * 0.4f))
            // Los pliegues que bajan con el peso del fieltro.
            listOf(0.08f, 0.22f, 0.37f, 0.5f, 0.66f, 0.8f, 0.93f).forEachIndexed { i, fx ->
                val pl = Path().apply {
                    moveTo(X(fx), pie + L * 0.01f)
                    cubicTo(X(fx + 0.01f), pie + (caeHasta - pie) * 0.4f, X(fx - 0.015f), pie + (caeHasta - pie) * 0.7f, X(fx + 0.005f), caeHasta + L * 0.05f)
                }
                drawPath(pl, sombra(oscuro, 0.22f + 0.06f * (i % 2)), style = Stroke(L * 0.018f, cap = StrokeCap.Round))
                drawPath(corrido(pl, -L * 0.016f, 0f), brillo(oscuro, 0.22f, k, luz), style = Stroke(L * 0.008f, cap = StrokeCap.Round))
            }
        }
        // El faldón de cuero, repujado, con lo gastado donde roza la pierna.
        drawPath(faldon, c(CUERO))
        clipPath(faldon) {
            drawPath(corrido(faldon, 0f, 0f), c(mezcla(CUERO, MADERA_OSC, 0.4f)), style = Stroke(L * 0.03f))
            drawPath(mancho(Offset(X(0.5f), Y(0.09f)), L * 0.16f, L * 0.05f, 911, 10), c(mezcla(CUERO, CUERO_CLARO, 0.45f)).copy(alpha = 0.7f))
            drawRect(sombra(oscuro, 0.25f), Offset(X(0f), Y(0.18f)), Size(L, L * 0.035f))
        }
        drawPath(corrido(faldon, 0f, 0f), c(ORO_A).copy(alpha = 0.0f))
        val rep = Path().apply {
            moveTo(X(0.2f), Y(0.14f)); lineTo(X(0.82f), Y(0.135f))
            cubicTo(X(0.86f), Y(0.1f), X(0.85f), Y(0.05f), X(0.79f), Y(0.04f)); lineTo(X(0.23f), Y(0.04f))
            cubicTo(X(0.16f), Y(0.05f), X(0.15f), Y(0.1f), X(0.2f), Y(0.14f))
        }
        drawPath(corrido(rep, 0.8f * u, 0.8f * u), brillo(oscuro, 0.3f, k, luz), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
        drawPath(rep, c(MADERA_OSC).copy(alpha = 0.8f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
        volumen(faldon, oscuro, k, hx, luz, lustre = 0.18f, fuerza = 0.8f)
        // El asiento de cuero acolchado, con su costura.
        drawPath(asiento, c(mezcla(CUERO, MADERA_OSC, 0.25f)))
        clipPath(asiento) {
            drawPath(Path().apply { moveTo(X(0.3f), Y(0.24f)); cubicTo(X(0.4f), Y(0.21f), X(0.6f), Y(0.21f), X(0.76f), Y(0.27f)) },
                c(mezcla(FIELTRO_PARED, ORO_A, 0.3f)).copy(alpha = 0.7f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2f * u))))
            drawRect(brillo(oscuro, 0.3f, k, luz), Offset(X(0.3f), Y(0.27f)), Size(L * 0.4f, L * 0.02f))
        }
        volumen(asiento, oscuro, k, hx, luz, lustre = 0.25f)
        // Los borrenes: madera pintada de rojo, con su grosor (la cara de lado en sombra), filete de oro y clavos de plata.
        listOf(delante, detras).forEachIndexed { i, b ->
            drawPath(corrido(b, L * 0.03f, -L * 0.004f), c(mezcla(ROJO_A, MADERA_OSC, 0.55f)))
            drawPath(b, c(ROJO_A))
            clipPath(b) {
                pinceladas(b.getBounds(), c(mezcla(ROJO_A, MADERA, 0.5f)), 920 + i, 6, u, 0.2f)
                drawPath(b, c(ORO_A), style = Stroke(L * 0.014f))
                // La pintura gastada en el canto de arriba: asoma la madera.
                drawPath(mancho(Offset(b.getBounds().center.x, b.getBounds().top + L * 0.02f), L * 0.04f, L * 0.02f, 925 + i, 8), c(MADERA))
            }
            volumen(b, oscuro, k, hx, luz, lustre = 0.3f)
        }
        // La chapa de plata que guarnece el canto del borrén de delante, con su grabado, y los clavos.
        val chapa = Path().apply {
            moveTo(X(0.16f), Y(0.2f))
            cubicTo(X(0.155f), Y(0.32f), X(0.165f), Y(0.43f), X(0.185f), Y(0.49f))
            cubicTo(X(0.2f), Y(0.52f), X(0.235f), Y(0.52f), X(0.235f), Y(0.49f))
            cubicTo(X(0.23f), Y(0.42f), X(0.215f), Y(0.32f), X(0.215f), Y(0.2f))
            close()
        }
        drawPath(chapa, c(PLATA))
        clipPath(chapa) {
            drawPath(borde(chapa, -L * 0.012f, 0f), sombra(oscuro, 0.35f))
            drawRect(brillo(oscuro, 0.75f, k, luz), Offset(X(0.168f), Y(0.46f)), Size(L * 0.012f, L * 0.2f))
            for (q in 0 until 4) {
                val y = Y(0.24f + 0.065f * q)
                drawArc(c(mezcla(PLATA, HIERRO, 0.55f)), 200f, 140f, false, Offset(X(0.17f), y - L * 0.012f), Size(L * 0.04f, L * 0.03f), style = Stroke(0.9f * u))
            }
        }
        listOf(Offset(0.145f, 0.2f), Offset(0.15f, 0.33f), Offset(0.255f, 0.24f), Offset(0.835f, 0.33f), Offset(0.84f, 0.22f)).forEach { (fx, fy) ->
            val p = Offset(X(fx), Y(fy))
            drawCircle(c(PLATA), L * 0.008f, p)
            drawCircle(brillo(oscuro, 0.8f, k, luz), L * 0.003f, p - Offset(L * 0.003f, L * 0.003f))
        }
        // El estribo de hierro, colgando de su ación por delante del canto de la tabla.
        val sx = X(0.56f)
        val top = pie + (caeHasta - pie) * 0.05f
        val bot = caeHasta + L * 0.03f
        val sw = L * 0.105f
        val acion = Path().apply { moveTo(X(0.5f), Y(0.12f)); cubicTo(X(0.53f), Y(0.05f), sx, pie - L * 0.02f, sx, top) }
        drawPath(corrido(acion, 1.6f * u, 1.2f * u), sombra(oscuro, 0.3f), style = Stroke(L * 0.028f))
        drawPath(acion, c(mezcla(CUERO, MADERA_OSC, 0.3f)), style = Stroke(L * 0.028f))
        drawPath(acion, brillo(oscuro, 0.25f, k, luz), style = Stroke(L * 0.006f))
        val arco = Path().apply {
            moveTo(sx - sw / 2, bot)
            cubicTo(sx - sw * 0.58f, top + (bot - top) * 0.25f, sx - sw * 0.25f, top, sx, top)
            cubicTo(sx + sw * 0.25f, top, sx + sw * 0.58f, top + (bot - top) * 0.25f, sx + sw / 2, bot)
        }
        drawPath(corrido(arco, 2f * u, 1.6f * u), sombra(oscuro, 0.35f), style = Stroke(L * 0.022f))
        drawRoundRect(sombra(oscuro, 0.35f), Offset(sx - sw * 0.62f + 2f * u, bot - L * 0.012f + 1.6f * u), Size(sw * 1.24f, L * 0.026f), CornerRadius(L * 0.01f))
        drawPath(arco, c(HIERRO), style = Stroke(L * 0.022f, cap = StrokeCap.Round))
        drawRoundRect(c(HIERRO), Offset(sx - sw * 0.62f, bot - L * 0.012f), Size(sw * 1.24f, L * 0.026f), CornerRadius(L * 0.01f))
        drawPath(corrido(arco, -L * 0.006f, -L * 0.004f), brillo(oscuro, 0.5f, k, luz), style = Stroke(L * 0.006f))
        drawRect(brillo(oscuro, 0.45f, k, luz), Offset(sx - sw * 0.6f, bot - L * 0.012f), Size(sw * 1.2f, L * 0.005f))
        drawRect(c(HIERRO), Offset(sx - L * 0.014f, top - L * 0.012f), Size(L * 0.028f, L * 0.02f))
    }

    /**
     * El gorro, colgado por su botón: la copa de paño azul con sus costuras de oro y su borla roja, el ala de piel de zorro
     * vuelta hacia arriba (un rollo de pelo, oscuro en la raíz y claro en las puntas) y las dos orejeras que caen.
     */
    private fun DrawScope.gorro(o: Offset, a: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float, caida: Offset) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        rotate(8f, Offset(o.x, o.y - a * 0.5f)) {
            val copa = Path().apply {
                moveTo(o.x - a * 0.4f, o.y)
                cubicTo(o.x - a * 0.42f, o.y - a * 0.3f, o.x - a * 0.16f, o.y - a * 0.5f, o.x, o.y - a * 0.52f)
                cubicTo(o.x + a * 0.16f, o.y - a * 0.5f, o.x + a * 0.42f, o.y - a * 0.3f, o.x + a * 0.4f, o.y)
                close()
            }
            val orejas = listOf(-1f, 1f).map { s ->
                Path().apply {
                    moveTo(o.x + s * a * 0.3f, o.y + a * 0.05f)
                    cubicTo(o.x + s * a * 0.42f, o.y + a * 0.2f, o.x + s * a * 0.44f, o.y + a * 0.38f, o.x + s * a * 0.36f, o.y + a * 0.46f)
                    cubicTo(o.x + s * a * 0.26f, o.y + a * 0.44f, o.x + s * a * 0.2f, o.y + a * 0.25f, o.x + s * a * 0.12f, o.y + a * 0.08f)
                    close()
                }
            }
            val ala = Path().apply { addRoundRect(RoundRect(Rect(o.x - a * 0.5f, o.y - a * 0.1f, o.x + a * 0.5f, o.y + a * 0.14f), CornerRadius(a * 0.12f))) }
            // Su sombra en el fieltro.
            val todo = Path().apply { op(copa, ala, PathOperation.Union) }
            drawPath(corrido(todo, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.45f * k else 0.2f))
            orejas.forEach { drawPath(corrido(it, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.45f * k else 0.2f)) }
            // Las orejeras, de piel, detrás del ala.
            orejas.forEachIndexed { i, p ->
                drawPath(p, c(mezcla(PIEL, MADERA_OSC, if (i == 0) 0.2f else 0.4f)))
                clipPath(p) {
                    for (q in 0 until 4) {
                        val y = o.y + a * (0.12f + 0.08f * q)
                        drawLine(c(mezcla(PIEL, LUZ_MUEBLE, 0.3f)).copy(alpha = 0.5f), Offset(o.x - a, y), Offset(o.x + a, y + a * 0.04f), 1f * u)
                    }
                }
            }
            // La copa.
            drawPath(copa, c(AZUL_A))
            clipPath(copa) {
                listOf(-0.2f, 0.16f).forEach { f ->
                    drawPath(Path().apply { moveTo(o.x + a * f * 2f, o.y); cubicTo(o.x + a * f * 1.6f, o.y - a * 0.25f, o.x + a * f * 0.5f, o.y - a * 0.45f, o.x, o.y - a * 0.52f) },
                        c(ORO_A).copy(alpha = 0.85f), style = Stroke(1.1f * u))
                }
            }
            volumen(copa, oscuro, k, hx, luz, lustre = 0.18f)
            drawCircle(c(ROJO_A), a * 0.055f, Offset(o.x, o.y - a * 0.53f))
            // La borla roja que cae del botón.
            listOf(-0.05f, 0f, 0.05f).forEach { dx -> drawLine(c(ROJO_A), Offset(o.x, o.y - a * 0.53f), Offset(o.x + a * (0.1f + dx), o.y - a * 0.36f), 1.3f * u, StrokeCap.Round) }
            // El ala de piel: el rollo, con su lado de sombra abajo, y los mechones.
            drawPath(ala, c(PIEL))
            clipPath(ala) {
                drawRect(c(mezcla(PIEL, MADERA_OSC, 0.45f)), Offset(o.x - a, o.y + a * 0.04f), Size(a * 2, a * 0.12f))
                drawRect(c(mezcla(PIEL, LUZ_MUEBLE, 0.35f)), Offset(o.x - a, o.y - a * 0.1f), Size(a * 2, a * 0.06f))
                for (q in 0 until 22) {
                    val x = o.x - a * 0.5f + a * azar(945, q)
                    val y = o.y - a * 0.08f + a * 0.2f * azar(946, q)
                    val l = a * (0.05f + 0.05f * azar(947, q))
                    val col = if (azar(948, q) < 0.5f) mezcla(PIEL, LUZ_MUEBLE, 0.4f) else mezcla(PIEL, MADERA_OSC, 0.4f)
                    drawLine(c(col).copy(alpha = 0.75f), Offset(x, y), Offset(x + l * 0.4f, y - l), 1.5f * u, StrokeCap.Round)
                }
            }
            // El borde de arriba del rollo, con los pelos que asoman.
            for (q in 0 until 16) {
                val x = o.x - a * 0.46f + a * 0.92f * q / 15f
                val l = a * (0.03f + 0.04f * azar(949, q))
                drawLine(c(mezcla(PIEL, LUZ_MUEBLE, 0.3f)), Offset(x, o.y - a * 0.08f), Offset(x + l * 0.5f, o.y - a * 0.08f - l), 1.3f * u, StrokeCap.Round)
            }
            volumen(ala, oscuro, k, hx, luz, fuerza = 0.8f)
        }
    }

    /**
     * Colgados de una [estaca] del khana: el carcaj de cuero con sus flechas (las plumas asoman arriba) y, delante, en
     * diagonal, la funda del arco con el arco recurvo asomando: su punta de asta vuelta y la cuerda.
     */
    private fun DrawScope.arcoYCarcaj(estaca: Offset, w: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float, caida: Offset) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        // El carcaj, vertical, a la izquierda.
        val qx = estaca.x - w * 0.055f
        val qt = estaca.y + w * 0.065f; val qb = estaca.y + w * 0.31f
        val carcaj = Path().apply {
            moveTo(qx - w * 0.03f, qt); lineTo(qx + w * 0.03f, qt)
            lineTo(qx + w * 0.024f, qb - w * 0.02f)
            cubicTo(qx + w * 0.022f, qb + w * 0.004f, qx - w * 0.022f, qb + w * 0.004f, qx - w * 0.024f, qb - w * 0.02f)
            close()
        }
        // Las flechas: astiles de abedul, plumas grises con su barra oscura y la muesca.
        for (q in 0 until 5) {
            val bx = qx - w * 0.02f + w * 0.01f * q
            val giro = (azar(950, q) - 0.5f) * 0.5f
            val tip = Offset(bx + w * 0.06f * giro, qt - w * (0.05f + 0.025f * azar(951, q)))
            drawLine(c(mezcla(ABEDUL, MADERA, 0.35f)), Offset(bx, qt + w * 0.01f), tip, w * 0.004f)
            val dir = (tip - Offset(bx, qt)) / (tip - Offset(bx, qt)).getDistance()
            val base = tip - dir * (w * 0.035f)
            listOf(-1f, 1f).forEach { s ->
                val nrm = Offset(-dir.y, dir.x) * s
                val pluma = poli(base.x, base.y, tip.x - dir.x * w * 0.004f, tip.y - dir.y * w * 0.004f,
                    tip.x - dir.x * w * 0.008f + nrm.x * w * 0.008f, tip.y - dir.y * w * 0.008f + nrm.y * w * 0.008f,
                    base.x + nrm.x * w * 0.01f, base.y + nrm.y * w * 0.01f)
                drawPath(pluma, c(if (s < 0) mezcla(FIELTRO_PARED, FIELTRO_GRIS, 0.5f) else mezcla(FIELTRO_GRIS, HIERRO, 0.3f)))
            }
            val barra = base + dir * (w * 0.018f)
            drawLine(c(HIERRO), barra - Offset(-dir.y, dir.x) * (w * 0.009f), barra + Offset(-dir.y, dir.x) * (w * 0.009f), w * 0.004f)
            drawCircle(c(ROJO_A), w * 0.003f, tip)
        }
        drawPath(corrido(carcaj, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.5f * k else 0.22f))
        drawPath(carcaj, c(CUERO))
        clipPath(carcaj) {
            pinceladas(carcaj.getBounds(), c(mezcla(CUERO, CUERO_CLARO, 0.6f)), 952, 8, u, 0.2f)
            // La boca forrada de fieltro rojo con botones, la placa de hueso del medio y la contera.
            drawRect(c(ROJO_A), Offset(qx - w * 0.04f, qt), Size(w * 0.08f, w * 0.026f))
            for (q in 0 until 3) drawCircle(c(ORO_A), w * 0.004f, Offset(qx - w * 0.018f + w * 0.018f * q, qt + w * 0.013f))
            // Una banda de fieltro azul con tachones a media altura, y el repujado en espiga.
            drawRect(c(AZUL_A), Offset(qx - w * 0.04f, qt + w * 0.09f), Size(w * 0.08f, w * 0.018f))
            for (q in 0 until 3) drawCircle(c(PLATA), w * 0.0035f, Offset(qx - w * 0.016f + w * 0.016f * q, qt + w * 0.099f))
            for (q in 0 until 5) {
                val y = qt + w * (0.035f + 0.011f * q)
                drawLine(c(MADERA_OSC).copy(alpha = 0.6f), Offset(qx - w * 0.02f, y), Offset(qx, y + w * 0.007f), 0.9f * u)
                drawLine(c(MADERA_OSC).copy(alpha = 0.6f), Offset(qx, y + w * 0.007f), Offset(qx + w * 0.02f, y), 0.9f * u)
            }
            for (q in 0 until 4) {
                val y = qt + w * (0.13f + 0.011f * q)
                drawLine(c(MADERA_OSC).copy(alpha = 0.6f), Offset(qx - w * 0.02f, y), Offset(qx, y + w * 0.007f), 0.9f * u)
                drawLine(c(MADERA_OSC).copy(alpha = 0.6f), Offset(qx, y + w * 0.007f), Offset(qx + w * 0.02f, y), 0.9f * u)
            }
            drawRect(c(MADERA_OSC), Offset(qx - w * 0.03f, qb - w * 0.03f), Size(w * 0.06f, w * 0.03f))
        }
        volumen(carcaj, oscuro, k, hx, luz, lustre = 0.2f)
        // La funda del arco, en diagonal, colgada por la boca.
        val bt = estaca + Offset(w * 0.065f, w * 0.07f)
        val L = w * 0.3f
        val correa = Path().apply { moveTo(qx - w * 0.02f, qt); lineTo(estaca.x, estaca.y); lineTo(bt.x + w * 0.04f, bt.y - w * 0.005f) }
        drawPath(corrido(correa, 1.5f * u, 1.5f * u), sombra(oscuro, 0.3f), style = Stroke(w * 0.006f))
        drawPath(correa, c(mezcla(CUERO, MADERA_OSC, 0.4f)), style = Stroke(w * 0.006f, join = StrokeJoin.Round))
        rotate(-16f, bt) {
            // El arco asoma por la boca: el brazo que se curva y la punta vuelta de asta, con la cuerda.
            val brazo = Path().apply {
                moveTo(bt.x - w * 0.012f, bt.y + w * 0.04f)
                cubicTo(bt.x - w * 0.03f, bt.y, bt.x - w * 0.045f, bt.y - w * 0.05f, bt.x - w * 0.034f, bt.y - w * 0.085f)
                lineTo(bt.x - w * 0.002f, bt.y - w * 0.135f)
            }
            drawPath(corrido(brazo, 1.6f * u, 1.6f * u), sombra(oscuro, 0.3f), style = Stroke(w * 0.013f))
            drawPath(brazo, c(mezcla(MADERA_OSC, CUERO, 0.3f)), style = Stroke(w * 0.013f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // El forro de corteza de abedul del vientre y la punta (la oreja) de asta clara.
            drawPath(corrido(brazo, w * 0.003f, 0f), c(mezcla(ABEDUL, CUERO_CLARO, 0.45f)).copy(alpha = 0.8f), style = Stroke(w * 0.004f))
            drawLine(c(mezcla(ABEDUL, ORO_A, 0.25f)), Offset(bt.x - w * 0.03f, bt.y - w * 0.09f), Offset(bt.x - w * 0.002f, bt.y - w * 0.135f), w * 0.008f, StrokeCap.Round)
            drawCircle(c(MADERA_OSC), w * 0.006f, Offset(bt.x - w * 0.034f, bt.y - w * 0.085f))
            // La cuerda, tensa desde la punta.
            drawLine(c(mezcla(FIELTRO_PARED, CUERO_CLARO, 0.4f)), Offset(bt.x - w * 0.004f, bt.y - w * 0.13f), Offset(bt.x + w * 0.03f, bt.y + w * 0.04f), 1.1f * u)
            val funda = Path().apply {
                moveTo(bt.x - w * 0.045f, bt.y)
                lineTo(bt.x + w * 0.05f, bt.y)
                cubicTo(bt.x + w * 0.07f, bt.y + L * 0.35f, bt.x + w * 0.04f, bt.y + L * 0.7f, bt.x + w * 0.005f, bt.y + L)
                cubicTo(bt.x - w * 0.012f, bt.y + L * 1.02f, bt.x - w * 0.022f, bt.y + L * 0.98f, bt.x - w * 0.02f, bt.y + L * 0.9f)
                cubicTo(bt.x - w * 0.03f, bt.y + L * 0.6f, bt.x - w * 0.05f, bt.y + L * 0.3f, bt.x - w * 0.045f, bt.y)
                close()
            }
            drawPath(corrido(funda, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.5f * k else 0.22f))
            drawPath(funda, c(mezcla(CUERO, CUERO_CLARO, 0.3f)))
            clipPath(funda) {
                pinceladas(funda.getBounds(), c(CUERO), 953, 10, u, 0.25f)
                drawPath(corrido(funda, 0f, 0f), c(MADERA_OSC).copy(alpha = 0.7f), style = Stroke(w * 0.012f))
                // El repujado del borde y el aplique de fieltro rojo con su voluta de oro.
                drawPath(funda, c(ORO_A).copy(alpha = 0.55f), style = Stroke(w * 0.022f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 3f * u))))
                val ap = Path().apply { addOval(Rect(bt.x - w * 0.028f, bt.y + L * 0.25f, bt.x + w * 0.04f, bt.y + L * 0.5f)) }
                drawPath(ap, c(ROJO_A))
                voluta(Offset(bt.x + w * 0.006f, bt.y + L * 0.37f), w * 0.02f, c(ORO_A), 1.3f * u)
                drawRect(c(MADERA_OSC), Offset(bt.x - w * 0.06f, bt.y), Size(w * 0.12f, w * 0.012f))
            }
            volumen(funda, oscuro, k, hx, luz, lustre = 0.22f)
        }
        // La estaca: un taco de madera metido en el cruce.
        drawCircle(sombra(oscuro, 0.35f), w * 0.011f, estaca + Offset(2f * u, 2f * u))
        drawCircle(c(MADERA), w * 0.01f, estaca)
        drawCircle(brillo(oscuro, 0.5f, k, luz), w * 0.004f, estaca - Offset(w * 0.003f, w * 0.003f))
    }

    /** La brida colgada de un [clavo] del khana: la testera con su frontalera tachonada, el bocado de hierro y las riendas. */
    private fun DrawScope.brida(clavo: Offset, w: Float, oscuro: Boolean, k: Float, luz: Color, caida: Offset) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        val cx = clavo.x
        val anilloI = Offset(cx - w * 0.04f, clavo.y + w * 0.155f)
        val anilloD = Offset(cx + w * 0.038f, clavo.y + w * 0.15f)
        val correas = listOf(
            Path().apply { moveTo(cx, clavo.y); cubicTo(cx - w * 0.035f, clavo.y + w * 0.03f, cx - w * 0.045f, clavo.y + w * 0.09f, anilloI.x, anilloI.y - w * 0.012f) },
            Path().apply { moveTo(cx, clavo.y); cubicTo(cx + w * 0.034f, clavo.y + w * 0.03f, cx + w * 0.042f, clavo.y + w * 0.09f, anilloD.x, anilloD.y - w * 0.012f) },
            Path().apply { moveTo(cx - w * 0.03f, clavo.y + w * 0.04f); cubicTo(cx - w * 0.01f, clavo.y + w * 0.05f, cx + w * 0.01f, clavo.y + w * 0.05f, cx + w * 0.029f, clavo.y + w * 0.038f) },
            Path().apply { moveTo(cx - w * 0.043f, clavo.y + w * 0.12f); cubicTo(cx - w * 0.02f, clavo.y + w * 0.135f, cx + w * 0.02f, clavo.y + w * 0.133f, cx + w * 0.041f, clavo.y + w * 0.115f) },
        )
        val riendas = Path().apply {
            moveTo(anilloI.x, anilloI.y + w * 0.01f)
            cubicTo(anilloI.x - w * 0.01f, anilloI.y + w * 0.14f, anilloD.x + w * 0.02f, anilloD.y + w * 0.16f, anilloD.x, anilloD.y + w * 0.01f)
        }
        val g = w * 0.0075f
        (correas + riendas).forEach { drawPath(corrido(it, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.4f * k else 0.22f), style = Stroke(g)) }
        drawPath(riendas, c(mezcla(CUERO, MADERA_OSC, 0.3f)), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(corrido(riendas, w * 0.004f, w * 0.003f), c(mezcla(CUERO, MADERA_OSC, 0.5f)), style = Stroke(g * 0.8f, cap = StrokeCap.Round))
        correas.forEach {
            drawPath(it, c(CUERO), style = Stroke(g, cap = StrokeCap.Round))
            drawPath(corrido(it, -g * 0.25f, -g * 0.15f), brillo(oscuro, 0.3f, k, luz), style = Stroke(g * 0.25f))
        }
        // Los tachones de plata de la frontalera.
        for (q in 0 until 4) {
            val p = Offset(cx - w * 0.02f + w * 0.0135f * q, clavo.y + w * 0.046f)
            drawCircle(c(PLATA), w * 0.0042f, p)
            drawCircle(brillo(oscuro, 0.85f, k, luz), w * 0.0016f, p - Offset(w * 0.0012f, w * 0.0012f))
        }
        // El bocado: dos anillas y la embocadura partida.
        drawLine(c(HIERRO), anilloI + Offset(w * 0.01f, 0f), Offset(cx, clavo.y + w * 0.165f), w * 0.005f, StrokeCap.Round)
        drawLine(c(HIERRO), Offset(cx, clavo.y + w * 0.165f), anilloD - Offset(w * 0.01f, 0f), w * 0.005f, StrokeCap.Round)
        listOf(anilloI, anilloD).forEach { a ->
            drawCircle(sombra(oscuro, 0.35f), w * 0.014f, a + Offset(1.8f * u, 1.8f * u), style = Stroke(w * 0.005f))
            drawCircle(c(HIERRO), w * 0.014f, a, style = Stroke(w * 0.005f))
            drawArc(brillo(oscuro, 0.7f, k, luz), 190f, 70f, false, a - Offset(w * 0.014f, w * 0.014f), Size(w * 0.028f, w * 0.028f), style = Stroke(w * 0.002f))
        }
        drawCircle(c(MADERA), w * 0.007f, clavo)
    }

    /** Dos cuencos de madera encimados: el de abajo, derecho; el de arriba, ladeado, forrado de plata por dentro. */
    private fun DrawScope.cuencos(cx: Float, pie: Float, w: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float, caida: Offset) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        val a = w * 0.1f; val hh = w * 0.04f
        val boca = pie - hh
        val cuerpo = Path().apply {
            moveTo(cx - a / 2, boca)
            cubicTo(cx - a / 2, boca + hh * 0.7f, cx - a * 0.25f, pie - hh * 0.12f, cx - a * 0.18f, pie - hh * 0.12f)
            lineTo(cx - a * 0.16f, pie); lineTo(cx + a * 0.16f, pie); lineTo(cx + a * 0.18f, pie - hh * 0.12f)
            cubicTo(cx + a * 0.25f, pie - hh * 0.12f, cx + a / 2, boca + hh * 0.7f, cx + a / 2, boca)
            close()
        }
        drawPath(corrido(cuerpo, caida.x, caida.y * 0.5f), sombra(oscuro, if (oscuro) 0.45f * k else 0.22f))
        drawOval(sombra(oscuro, 0.4f), Offset(cx - a * 0.4f, pie - w * 0.005f), Size(a * 0.95f, w * 0.012f))
        drawPath(cuerpo, c(MADERA))
        clipPath(cuerpo) { vetas(cuerpo.getBounds(), c(MADERA_OSC).copy(alpha = 0.35f), 960, 0.5f) }
        volumen(cuerpo, oscuro, k, hx, luz, lustre = 0.3f)
        drawOval(c(mezcla(MADERA_OSC, NOCHE, 0.3f)), Offset(cx - a / 2, boca - hh * 0.16f), Size(a, hh * 0.32f))
        // El de arriba, metido y ladeado, con la plata por dentro.
        val o = Offset(cx + a * 0.08f, boca - hh * 0.25f)
        rotate(-11f, o) {
            val a2 = a * 0.82f; val h2 = hh * 0.85f
            val c2 = Path().apply {
                moveTo(o.x - a2 / 2, o.y)
                cubicTo(o.x - a2 / 2, o.y + h2 * 0.8f, o.x - a2 * 0.2f, o.y + h2, o.x, o.y + h2)
                cubicTo(o.x + a2 * 0.2f, o.y + h2, o.x + a2 / 2, o.y + h2 * 0.8f, o.x + a2 / 2, o.y)
                close()
            }
            drawPath(c2, c(mezcla(MADERA, CUERO, 0.35f)))
            clipPath(c2) {
                vetas(c2.getBounds(), c(MADERA_OSC).copy(alpha = 0.3f), 961, 0.5f)
                // La guarnición de plata del borde, por fuera.
                drawRect(c(PLATA), Offset(o.x - a2, o.y - h2 * 0.05f), Size(a2 * 2, h2 * 0.2f))
                drawRect(brillo(oscuro, 0.7f, k, luz), Offset(o.x - a2 * 0.4f, o.y - h2 * 0.02f), Size(a2 * 0.2f, h2 * 0.06f))
            }
            volumen(c2, oscuro, k, hx, luz, lustre = 0.3f)
            // La boca: la plata por dentro, en sombra del lado de la luz (el borde la tapa), y el brillo en el labio.
            val dentro = Rect(o.x - a2 / 2, o.y - h2 * 0.2f, o.x + a2 / 2, o.y + h2 * 0.2f)
            drawOval(c(mezcla(PLATA, HIERRO, 0.45f)), dentro.topLeft, dentro.size)
            drawOval(c(PLATA), Offset(dentro.left + a2 * 0.3f, dentro.top + h2 * 0.08f), Size(a2 * 0.62f, h2 * 0.3f))
            drawOval(brillo(oscuro, 0.85f, k, luz), Offset(dentro.left + a2 * 0.55f, dentro.top + h2 * 0.12f), Size(a2 * 0.22f, h2 * 0.1f))
            drawOval(c(mezcla(PLATA, ORO_A, 0.2f)), dentro.topLeft, dentro.size, style = Stroke(1.6f * u))
        }
    }

    /** La lámpara de sebo: un platillo de hierro sobre un vástago con trípode, el sebo cuajado y la mecha (tiznada de día). */
    private fun DrawScope.lampara(pie: Offset, w: Float, oscuro: Boolean, k: Float, luz: Color) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        val dishY = pie.y - w * 0.075f
        if (!oscuro) {
            // El hollín que la llama dejó en el fieltro.
            for (j in 0 until 3) {
                val r = w * (0.01f + 0.008f * j)
                drawOval(sombraMueble(0.035f), Offset(pie.x + w * 0.027f - r + (j % 2) * 1.5f * u, dishY - w * (0.04f + 0.035f * j) - r * 1.3f), Size(r * 2f, r * 2.6f))
            }
        }
        drawOval(sombra(oscuro, 0.4f), Offset(pie.x - w * 0.028f, pie.y - w * 0.004f), Size(w * 0.06f, w * 0.01f))
        // El trípode y el vástago.
        listOf(-0.026f, 0.026f, 0.006f).forEach { dx -> drawLine(c(HIERRO), Offset(pie.x, pie.y - w * 0.02f), Offset(pie.x + w * dx, pie.y + (if (dx == 0.006f) w * 0.003f else 0f)), w * 0.004f, StrokeCap.Round) }
        drawRect(c(HIERRO), Offset(pie.x - w * 0.0035f, dishY), Size(w * 0.007f, pie.y - w * 0.02f - dishY))
        drawRect(brillo(oscuro, 0.5f, k, luz), Offset(pie.x - w * 0.0035f, dishY), Size(w * 0.002f, pie.y - w * 0.02f - dishY))
        drawCircle(c(HIERRO), w * 0.006f, Offset(pie.x, pie.y - w * 0.045f))
        // El platillo con su pico, el sebo y la mecha.
        val plato = Path().apply {
            moveTo(pie.x - w * 0.032f, dishY - w * 0.008f)
            cubicTo(pie.x - w * 0.03f, dishY + w * 0.012f, pie.x + w * 0.02f, dishY + w * 0.012f, pie.x + w * 0.03f, dishY - w * 0.004f)
            lineTo(pie.x + w * 0.04f, dishY - w * 0.012f)
            lineTo(pie.x + w * 0.02f, dishY - w * 0.01f)
            close()
        }
        drawPath(plato, c(HIERRO))
        drawOval(c(mezcla(FIELTRO_PARED, ORO_A, 0.35f)), Offset(pie.x - w * 0.03f, dishY - w * 0.013f), Size(w * 0.054f, w * 0.009f))
        drawPath(plato, brillo(oscuro, 0.4f, k, luz), style = Stroke(0.8f * u))
        drawLine(Color(0xFF241812), Offset(pie.x + w * 0.03f, dishY - w * 0.01f), Offset(pie.x + w * 0.036f, dishY - w * 0.02f), 1.6f * u, StrokeCap.Round)
    }

    /** La llama del sebo, con su propio color, y su hilo de humo. */
    private fun DrawScope.llamaSebo(c: Offset, w: Float) {
        val u = density
        var p = c - Offset(0f, w * 0.025f)
        for (j in 0 until 4) {
            val sube = w * (0.025f + 0.006f * j)
            val lado = (if (j % 2 == 0) 1f else -1f) * w * (0.006f + 0.003f * j)
            val q = p + Offset(lado * 0.3f, -sube)
            drawPath(Path().apply { moveTo(p.x, p.y); cubicTo(p.x + lado, p.y - sube * 0.35f, q.x + lado, q.y + sube * 0.35f, q.x, q.y) },
                Color(0xFF2A1A14).copy(alpha = 0.4f - 0.08f * j), style = Stroke((1.2f + 1f * j) * u, cap = StrokeCap.Round))
            p = q
        }
        fun gota(alto: Float, ancho: Float) = Path().apply {
            moveTo(c.x, c.y - alto * 0.62f)
            cubicTo(c.x + ancho * 0.25f, c.y - alto * 0.3f, c.x + ancho * 0.55f, c.y + alto * 0.05f, c.x, c.y + alto * 0.38f)
            cubicTo(c.x - ancho * 0.55f, c.y + alto * 0.05f, c.x - ancho * 0.3f, c.y - alto * 0.3f, c.x, c.y - alto * 0.62f)
            close()
        }
        drawPath(gota(w * 0.042f, w * 0.024f), Color(0xFFE8822E))
        drawPath(gota(w * 0.03f, w * 0.017f), LLAMA)
        drawPath(gota(w * 0.018f, w * 0.01f), Color(0xFFFFF0C4))
    }

    /**
     * El odre del airag: cuero cosido, panzudo, con su cuello y el tapón de madera atado, el repujado de la cara (un arco doble
     * y una roseta), la costura del canto y la mancha que dejó la leche al servir. Apoyado contra el khana, un poco ladeado.
     */
    private fun DrawScope.odre(x0: Float, pie: Float, ancho: Float, alto: Float, w: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float, caida: Offset) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        val cx = x0 + ancho / 2
        val cuelloY = pie - alto * 0.82f
        rotate(5f, Offset(cx, pie)) {
            val cuerpo = Path().apply {
                moveTo(x0 + ancho * 0.12f, pie)
                cubicTo(x0 - ancho * 0.02f, pie - alto * 0.2f, x0 - ancho * 0.04f, pie - alto * 0.6f, x0 + ancho * 0.18f, pie - alto * 0.74f)
                cubicTo(x0 + ancho * 0.3f, pie - alto * 0.8f, cx - ancho * 0.1f, cuelloY + alto * 0.02f, cx - ancho * 0.1f, cuelloY)
                lineTo(cx + ancho * 0.1f, cuelloY)
                cubicTo(cx + ancho * 0.1f, cuelloY + alto * 0.02f, x0 + ancho * 0.72f, pie - alto * 0.8f, x0 + ancho * 0.84f, pie - alto * 0.72f)
                cubicTo(x0 + ancho * 1.04f, pie - alto * 0.58f, x0 + ancho * 1.02f, pie - alto * 0.2f, x0 + ancho * 0.88f, pie)
                close()
            }
            drawPath(corrido(cuerpo, caida.x, caida.y * 0.6f), sombra(oscuro, if (oscuro) 0.5f * k else 0.22f))
            drawOval(sombra(oscuro, 0.4f), Offset(x0 + ancho * 0.08f, pie - w * 0.007f), Size(ancho * 0.92f, w * 0.014f))
            drawPath(cuerpo, c(CUERO))
            clipPath(cuerpo) {
                pinceladas(cuerpo.getBounds(), c(mezcla(CUERO, CUERO_CLARO, 0.5f)), 970, 12, u, 0.22f)
                pinceladas(cuerpo.getBounds(), c(mezcla(CUERO, MADERA_OSC, 0.6f)), 971, 8, u, 0.22f)
                // La mancha oscura de la leche que chorreó del cuello.
                drawPath(mancho(Offset(cx + ancho * 0.05f, cuelloY + alto * 0.14f), ancho * 0.12f, alto * 0.1f, 972, 9), c(mezcla(CUERO, MADERA_OSC, 0.7f)).copy(alpha = 0.7f))
                drawLine(c(mezcla(CUERO, MADERA_OSC, 0.7f)).copy(alpha = 0.6f), Offset(cx + ancho * 0.08f, cuelloY + alto * 0.2f), Offset(cx + ancho * 0.1f, cuelloY + alto * 0.4f), 2.4f * u, StrokeCap.Round)
                // El repujado: un arco doble y una roseta de cuatro pétalos, en relieve (luz arriba, sombra abajo).
                val o = Offset(cx, pie - alto * 0.38f)
                val arco = Path().apply {
                    moveTo(x0 + ancho * 0.18f, pie - alto * 0.08f)
                    cubicTo(x0 + ancho * 0.16f, pie - alto * 0.6f, x0 + ancho * 0.84f, pie - alto * 0.6f, x0 + ancho * 0.82f, pie - alto * 0.08f)
                }
                listOf(0f, ancho * 0.06f).forEach { e ->
                    val a2 = corrido(arco, 0f, e)
                    drawPath(corrido(a2, 0f, 1.2f * u), sombra(oscuro, 0.35f), style = Stroke(1.6f * u))
                    drawPath(a2, brillo(oscuro, 0.3f, k, luz), style = Stroke(1f * u))
                }
                for (q in 0 until 4) {
                    val a = q * 1.5708f
                    val pe = Offset(o.x + ancho * 0.07f * cos(a), o.y + ancho * 0.07f * sin(a))
                    drawCircle(sombra(oscuro, 0.3f), ancho * 0.05f, pe + Offset(1f * u, 1.2f * u))
                    drawCircle(c(mezcla(CUERO, CUERO_CLARO, 0.35f)), ancho * 0.05f, pe)
                }
                drawCircle(c(ORO_A), ancho * 0.03f, o)
                // La costura del canto.
                drawPath(cuerpo, c(mezcla(CUERO_CLARO, FIELTRO_PARED, 0.4f)).copy(alpha = 0.7f), style = Stroke(ancho * 0.05f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 3f * u))))
                drawPath(cuerpo, c(mezcla(CUERO, MADERA_OSC, 0.5f)), style = Stroke(ancho * 0.03f))
            }
            volumen(cuerpo, oscuro, k, hx, luz, lustre = 0.28f, fuerza = 1.2f)
            // El cuello, la tira de cuero y el tapón de madera.
            val cuello = Rect(cx - ancho * 0.1f, cuelloY - alto * 0.07f, cx + ancho * 0.1f, cuelloY + 1f * u)
            drawRect(c(mezcla(CUERO, MADERA_OSC, 0.3f)), cuello.topLeft, cuello.size)
            drawRect(c(CUERO_CLARO), Offset(cuello.left - ancho * 0.01f, cuello.top + cuello.height * 0.35f), Size(cuello.width + ancho * 0.02f, cuello.height * 0.25f))
            val tapon = Path().apply { addRoundRect(RoundRect(Rect(cx - ancho * 0.07f, cuello.top - alto * 0.07f, cx + ancho * 0.07f, cuello.top + 2f * u), CornerRadius(ancho * 0.03f))) }
            drawPath(tapon, c(MADERA))
            volumen(tapon, oscuro, k, hx, luz, lustre = 0.3f)
            drawPath(Path().apply { moveTo(cx + ancho * 0.1f, cuello.center.y); cubicTo(cx + ancho * 0.2f, cuello.center.y + alto * 0.05f, cx + ancho * 0.2f, cuello.center.y + alto * 0.12f, cx + ancho * 0.15f, cuello.center.y + alto * 0.17f) },
                c(CUERO_CLARO), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- al pie

    /** Un par de botas de cuero con la caña ribeteada de fieltro rojo: una de pie y la otra apoyada detrás, ladeada. */
    private fun DrawScope.botas(x0: Float, suelo: Float, w: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        fun bota(o: Offset): Path {
            val s = w
            return Path().apply {
                moveTo(o.x + s * 0.02f, o.y - s * 0.13f)
                lineTo(o.x + s * 0.078f, o.y - s * 0.13f)
                cubicTo(o.x + s * 0.08f, o.y - s * 0.08f, o.x + s * 0.086f, o.y - s * 0.03f, o.x + s * 0.084f, o.y)
                lineTo(o.x - s * 0.026f, o.y)
                cubicTo(o.x - s * 0.04f, o.y - s * 0.004f, o.x - s * 0.042f, o.y - s * 0.016f, o.x - s * 0.036f, o.y - s * 0.022f)
                cubicTo(o.x - s * 0.02f, o.y - s * 0.03f, o.x + s * 0.0f, o.y - s * 0.04f, o.x + s * 0.016f, o.y - s * 0.06f)
                close()
            }
        }
        fun pinta(b: Path, o: Offset, tono: Float) {
            drawPath(b, c(mezcla(CUERO, MADERA_OSC, tono)))
            clipPath(b) {
                pinceladas(b.getBounds(), c(CUERO_CLARO), 980, 6, u, 0.25f)
                drawRect(c(ROJO_A), Offset(o.x, o.y - w * 0.135f), Size(w * 0.1f, w * 0.022f))
                drawLine(c(ORO_A), Offset(o.x, o.y - w * 0.115f), Offset(o.x + w * 0.1f, o.y - w * 0.115f), 1f * u)
                drawRect(c(mezcla(CUERO_CLARO, FIELTRO_GRIS, 0.4f)), Offset(o.x - w * 0.05f, o.y - w * 0.006f), Size(w * 0.15f, w * 0.006f))
                // Las arrugas del empeine y del tobillo.
                listOf(0.04f, 0.055f, 0.07f).forEach { f -> drawArc(sombra(oscuro, 0.3f), 200f, 120f, false, Offset(o.x + w * 0.02f, o.y - w * f - w * 0.01f), Size(w * 0.06f, w * 0.02f), style = Stroke(1.1f * u)) }
            }
            volumen(b, oscuro, k, hx, luz, lustre = 0.25f)
        }
        val o2 = Offset(x0 + w * 0.06f, suelo - w * 0.012f)
        rotate(5f, Offset(o2.x + w * 0.08f, o2.y)) {
            val b2 = bota(o2)
            drawPath(corrido(b2, w * 0.02f, w * 0.004f), sombra(oscuro, 0.3f))
            pinta(b2, o2, 0.4f)
        }
        val o1 = Offset(x0, suelo)
        val b1 = bota(o1)
        drawOval(sombra(oscuro, 0.42f), Offset(o1.x - w * 0.03f, suelo - w * 0.008f), Size(w * 0.14f, w * 0.016f))
        drawPath(corrido(b1, w * 0.012f, 0f), sombra(oscuro, 0.2f))
        pinta(b1, o1, 0.1f)
    }

    /** La mesa baja pintada (cortada por el borde derecho) con un cuenco de leche. */
    private fun DrawScope.mesa(x0: Float, suelo: Float, w: Float, oscuro: Boolean, k: Float, luz: Color, hx: Float) {
        val u = density
        fun c(col: Color) = n(col, oscuro, k, 0f, luz)
        val top = suelo - w * 0.085f
        drawRect(sombra(oscuro, 0.4f), Offset(x0 - w * 0.004f, suelo - w * 0.006f), Size(w, w * 0.014f))
        // Las patas, cortas y torneadas.
        listOf(x0 + w * 0.01f, x0 + w * 0.17f).forEach { x ->
            drawRect(c(mezcla(NARANJA_A, MADERA_OSC, 0.5f)), Offset(x, top + w * 0.05f), Size(w * 0.016f, suelo - top - w * 0.05f))
            drawRect(sombra(oscuro, 0.3f), Offset(x + w * 0.01f, top + w * 0.05f), Size(w * 0.006f, suelo - top - w * 0.05f))
        }
        // El tablero, visto apenas desde arriba, y el faldón pintado.
        val tablero = Rect(x0 - w * 0.01f, top, w * 1.02f, top + w * 0.016f)
        drawRect(c(mezcla(NARANJA_A, LUZ_MUEBLE, 0.15f)), tablero.topLeft, tablero.size)
        vetas(tablero, c(MADERA_OSC).copy(alpha = 0.25f), 990, 0.6f)
        val faldon = Rect(x0, tablero.bottom, w * 1.02f, top + w * 0.05f)
        drawRect(c(NARANJA_A), faldon.topLeft, faldon.size)
        drawRect(c(AZUL_A), Offset(faldon.left + w * 0.012f, faldon.top + w * 0.006f), Size(faldon.width, faldon.height - w * 0.012f))
        voluta(Offset(faldon.left + w * 0.05f, faldon.center.y), w * 0.01f, c(ORO_A), 1.2f * u)
        voluta(Offset(faldon.left + w * 0.09f, faldon.center.y), w * 0.01f, c(ORO_A), 1.2f * u, espejo = true)
        drawRect(brillo(oscuro, 0.5f, k, luz), tablero.topLeft, Size(tablero.width, 1.2f * u))
        drawRect(sombra(oscuro, 0.3f), Offset(faldon.left, faldon.top), Size(faldon.width, 1.6f * u))
        drawPath(mancho(Offset(faldon.left + w * 0.012f, faldon.center.y), w * 0.008f, faldon.height * 0.4f, 991, 7), c(MADERA))
        // El cuenco de leche.
        val bc = Offset(x0 + w * 0.09f, top + w * 0.006f)
        val cuenco = Path().apply {
            moveTo(bc.x - w * 0.04f, bc.y - w * 0.028f)
            cubicTo(bc.x - w * 0.04f, bc.y, bc.x - w * 0.015f, bc.y + w * 0.002f, bc.x, bc.y + w * 0.002f)
            cubicTo(bc.x + w * 0.015f, bc.y + w * 0.002f, bc.x + w * 0.04f, bc.y, bc.x + w * 0.04f, bc.y - w * 0.028f)
            close()
        }
        drawOval(sombra(oscuro, 0.35f), Offset(bc.x - w * 0.03f, bc.y - w * 0.004f), Size(w * 0.07f, w * 0.009f))
        drawPath(cuenco, c(MADERA))
        volumen(cuenco, oscuro, k, hx, luz, lustre = 0.3f)
        drawOval(c(mezcla(FIELTRO_PARED, LUZ_MUEBLE, 0.3f)), Offset(bc.x - w * 0.038f, bc.y - w * 0.034f), Size(w * 0.076f, w * 0.012f))
        drawOval(c(MADERA_OSC), Offset(bc.x - w * 0.04f, bc.y - w * 0.035f), Size(w * 0.08f, w * 0.014f), style = Stroke(1.2f * u))
    }

    /** La placa de «y N más»: un fieltro crema doblado sobre la alfombra, con pespunte azul. */
    private fun DrawScope.placaFieltro(r: Rect, oscuro: Boolean, k: Float, luz: Color) {
        val u = density
        val p = contornoIrregular(r, 77, 24, 0.02f)
        drawPath(corrido(p, 2f * u, 3f * u), sombra(oscuro, 0.4f))
        drawPath(p, if (oscuro) mezcla(PANEL_NOCHE, PANEL_LUZ, k * 1.2f) else PANEL)
        clipPath(p) {
            drawRect(sombra(oscuro, 0.14f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
            drawRect(brillo(oscuro, 0.4f, k, luz), r.topLeft, Size(r.width, 1.4f * u))
        }
        drawRect(n(AZUL_A, oscuro, maxOf(k, 0.6f), 0f, luz), Offset(r.left + 3f * u, r.top + 3f * u), Size(r.width - 6f * u, r.height - 6f * u),
            style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 2.5f * u))))
    }

    // ---------------------------------------------------------------- los compartimentos

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val dentro = c.cols == cols
        val aw = anchoH(); val ah = altoH()
        val centro = Offset(lado + c.col * (aw + hueco) + aw * 0.5f, corona + hueco + c.fila * (ah + hueco) + ah * 0.6f)
        val k = if (!oscuro) 0f else if (dentro) (calor(centro, c.filas) * 1.1f).coerceIn(0.05f, 1f) else 0.5f
        val luz = if (dentro) luzDe(centro, c.filas) else LLAMA
        val fuente = if (dentro) luzMayor(centro, c.filas) else Offset(centro.x + 0.3f, centro.y - 0.3f)
        val hx = if (fuente.x >= centro.x) 1f else -1f
        val dia = if (oscuro) 0f else 0.09f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        if (c.vacio) { vacio(c.semilla, oscuro, k, luz, dia, hx); return }
        val mez = (c.semilla xor (c.semilla ushr 3)) ushr 1
        val tipo = if (c.sinTema) 3 else Math.floorMod(mez, 3)
        val nn = c.n.coerceIn(0, 7)
        val cuantas = if (c.n <= 0) 0 else (1 + nn).coerceAtMost(7)
        val sube = (if (c.n > 0) h * (0.03f + 0.017f * nn) else 0f) + ab * h * 0.16f
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        // Primero el aro que tapa el aura (con lo que hay detrás); luego, encima, lo guardado.
        fun rect(a: Float, b: Float, c2: Float, d2: Float) = Path().apply { addRect(Rect(a, b, c2, d2)) }
        val siluetas = when (tipo) {
            0 -> listOf(rect(w * 0.03f, h * 0.37f, w * 0.97f, h), rect(w * 0.015f, h * 0.2f - sube, w * 0.985f, h * 0.375f - sube),
                rect(w * 0.05f, h * 0.375f - sube - 2f * u, w * 0.95f, h * 0.38f))
            1 -> listOf(rect(w * 0.035f, h * 0.4f, w * 0.965f, h), rect(w * 0.025f, h * 0.2f - sube, w * 0.975f, h * 0.4f - sube),
                rect(w * 0.055f, h * 0.4f - sube - 2f * u, w * 0.945f, h * 0.41f))
            2 -> listOf(rect(w * 0.035f, h * 0.38f, w * 0.965f, h), rect(w * 0.02f, h * 0.22f - sube, w * 0.98f, h * 0.395f - sube),
                rect(w * 0.055f, h * 0.395f - sube - 2f * u, w * 0.945f, h * 0.41f))
            else -> listOf(sacoForma(w, h), Path().apply { addOval(Rect(w * 0.15f, h * 0.27f, w * 0.85f, h * 0.39f)) }) +
                tablillasContorno(w * 0.18f, w * 0.82f, h * 0.06f - ab * h * 0.1f, h * 0.4f, cuantas.coerceAtLeast(2), c.semilla, 16f)
        } + rect(r.left, r.top, r.right, r.bottom)
        cubreAura(c, oscuro, siluetas)
        val lejos = (centro - fuente).let { it / it.getDistance().coerceAtLeast(0.0001f) }
        sombraEnPared(siluetas, oscuro, k, lejos)
        when (tipo) {
            0 -> arcon(w, h, sube, cuantas, c.semilla, mez, oscuro, k, luz, dia, hx)
            1 -> baul(w, h, sube, cuantas, c.semilla, mez, oscuro, k, luz, dia, hx)
            2 -> cajaAbedul(w, h, sube, cuantas, c.semilla, oscuro, k, luz, dia, hx)
            else -> saco(w, h, cuantas, c.semilla, ab, oscuro, k, luz, dia, hx)
        }
        panel(r, tipo, c.semilla, oscuro, k, luz, dia)
        // Al abrirlo, sube un poco de luz de dentro.
        if (ab > 0f) mancha(Offset(w / 2, h * 0.22f - sube), w * 0.4f, if (oscuro) luz.copy(alpha = 0.25f * ab) else luzMueble(0.35f * ab))
    }

    /** El contorno de cada tablilla de [tablillas] (un rectángulo girado como ella), para el aro que tapa el aura. */
    private fun tablillasContorno(x0: Float, x1: Float, arriba: Float, abajo: Float, cuantas: Int, semilla: Int, giroMax: Float): List<Path> {
        if (cuantas <= 0) return emptyList()
        val ancho = (x1 - x0) / (cuantas * 0.72f + 0.4f)
        return List(cuantas) { i ->
            val f = if (cuantas == 1) 0.5f else i / (cuantas - 1f)
            val cx = x0 + ancho * 0.5f + (x1 - x0 - ancho) * f + (azar(semilla, i) - 0.5f) * ancho * 0.25f
            val top = arriba + (abajo - arriba) * 0.3f * azar(semilla, i + 10)
            val giro = Math.toRadians(((azar(semilla, i + 20) - 0.5f) * 2f * giroMax).toDouble())
            val co = cos(giro).toFloat(); val si = sin(giro).toFloat()
            fun g(x: Float, y: Float): Offset { val dx = x - cx; val dy = y - abajo; return Offset(cx + dx * co - dy * si, abajo + dx * si + dy * co) }
            val a = g(cx - ancho / 2, top); val b = g(cx + ancho / 2, top); val c = g(cx + ancho / 2, abajo); val d = g(cx - ancho / 2, abajo)
            poli(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y)
        }
    }

    /** La silueta del saco de cuero crudo de las ideas sin tema. */
    private fun sacoForma(w: Float, h: Float) = Path().apply {
        moveTo(w * 0.14f, h * 0.33f)
        cubicTo(w * 0.04f, h * 0.45f, w * 0.0f, h * 0.65f, w * 0.01f, h * 0.82f)
        cubicTo(w * 0.02f, h * 0.95f, w * 0.06f, h, w * 0.14f, h)
        lineTo(w * 0.88f, h)
        cubicTo(w * 0.96f, h, w * 0.995f, h * 0.92f, w * 0.99f, h * 0.8f)
        cubicTo(w * 0.985f, h * 0.62f, w * 0.95f, h * 0.44f, w * 0.85f, h * 0.31f)
        cubicTo(w * 0.7f, h * 0.36f, w * 0.55f, h * 0.29f, w * 0.42f, h * 0.34f)
        cubicTo(w * 0.32f, h * 0.38f, w * 0.22f, h * 0.3f, w * 0.14f, h * 0.33f)
        close()
    }

    /** Las tablillas de abedul, de pie y algo ladeadas, de [x0] a [x1], subiendo de [abajo] hasta cerca de [arriba]. */
    private fun DrawScope.tablillas(x0: Float, x1: Float, arriba: Float, abajo: Float, cuantas: Int, semilla: Int, oscuro: Boolean, k: Float, luz: Color, dia: Float, giroMax: Float = 7f) {
        val u = density
        if (cuantas <= 0) return
        val ancho = (x1 - x0) / (cuantas * 0.72f + 0.4f)
        for (i in 0 until cuantas) {
            val f = if (cuantas == 1) 0.5f else i / (cuantas - 1f)
            val cx = x0 + ancho * 0.5f + (x1 - x0 - ancho) * f + (azar(semilla, i) - 0.5f) * ancho * 0.25f
            val top = arriba + (abajo - arriba) * 0.3f * azar(semilla, i + 10)
            val giro = (azar(semilla, i + 20) - 0.5f) * 2f * giroMax
            rotate(giro, Offset(cx, abajo)) {
                val t = contornoIrregular(Rect(cx - ancho / 2, top, cx + ancho / 2, abajo), semilla + i, 12, 0.015f)
                drawPath(corrido(t, ancho * 0.1f, ancho * 0.04f), sombra(oscuro, 0.3f))
                drawPath(t, n(if (i % 2 == 0) ABEDUL else mezcla(ABEDUL, CUERO_CLARO, 0.25f), oscuro, k, dia, luz))
                clipPath(t) {
                    for (q in 0 until 3) {
                        val y = top + (abajo - top) * (0.15f + 0.22f * q + 0.08f * azar(semilla + i, q))
                        val x = cx - ancho * 0.3f + ancho * 0.3f * azar(semilla + i, q + 5)
                        drawLine(n(LENTICELA, oscuro, k, 0f, luz).copy(alpha = 0.6f), Offset(x, y), Offset(x + ancho * 0.35f, y), 1f * u)
                    }
                    drawRect(sombra(oscuro, 0.16f), Offset(cx + ancho * 0.15f, top), Size(ancho, abajo - top))
                    drawRect(n(mezcla(CUERO_CLARO, MADERA, 0.4f), oscuro, k, 0f, luz), Offset(cx - ancho, top), Size(ancho * 2, 1.6f * u))
                }
            }
        }
    }

    /**
     * El arcón pintado: el cuerpo naranja o rojo con su friso de arriba (dos recuadros azules con volutas de oro, una greca
     * de martillo o tres anillos, según la semilla) y su zócalo, las esquineras de hierro, la pintura gastada en los cantos,
     * la tapa con su cara de arriba y el cierre. Si guarda tablillas, la tapa se levanta y asoman.
     */
    private fun DrawScope.arcon(w: Float, h: Float, sube: Float, cuantas: Int, semilla: Int, mez: Int, oscuro: Boolean, k: Float, luz: Color, dia: Float, hx: Float) {
        val u = density
        fun c(col: Color, d: Float = dia) = n(col, oscuro, k, d, luz)
        val rojo = (mez ushr 2) % 2 == 1
        val color = if (rojo) ROJO_A else NARANJA_A
        val acento = if (rojo) NARANJA_A else ROJO_A
        val cuerpo = Rect(w * 0.03f, h * 0.37f, w * 0.97f, h)
        val tapa = Rect(w * 0.015f, h * 0.2f - sube, w * 0.985f, h * 0.375f - sube)
        if (sube > 0.5f) {
            drawRect(c(Color(0xFF2A160E), 0f), Offset(cuerpo.left + w * 0.02f, tapa.bottom - 2f * u), Size(cuerpo.width - w * 0.04f, cuerpo.top - tapa.bottom + 6f * u))
            tablillas(cuerpo.left + w * 0.05f, cuerpo.right - w * 0.05f, tapa.bottom - h * 0.06f, cuerpo.top + h * 0.08f, cuantas, semilla, oscuro, k, luz, dia)
        }
        drawRect(c(color), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            pinceladas(cuerpo, c(mezcla(color, MADERA, 0.4f)), semilla, 14, u, 0.18f)
            pinceladas(cuerpo, c(mezcla(color, LUZ_MUEBLE, 0.4f)), semilla + 1, 8, u, 0.14f)
            // El friso de arriba.
            val f0 = h * 0.395f; val f1 = h * 0.535f
            when ((mez ushr 3) % 3) {
                0 -> listOf(0.06f to 0.47f, 0.53f to 0.94f).forEachIndexed { i, (a, b) ->
                    val rr = Rect(w * a, f0, w * b, f1)
                    drawRect(c(AZUL_A), rr.topLeft, rr.size)
                    drawRect(c(ORO_A), Offset(rr.left + 2f * u, rr.top + 2f * u), Size(rr.width - 4f * u, rr.height - 4f * u), style = Stroke(0.9f * u))
                    voluta(Offset(rr.center.x - rr.width * 0.18f, rr.center.y), rr.height * 0.3f, c(ORO_A), 1.4f * u)
                    voluta(Offset(rr.center.x + rr.width * 0.18f, rr.center.y), rr.height * 0.3f, c(ORO_A), 1.4f * u, espejo = true)
                }
                1 -> {
                    drawRect(c(AZUL_A), Offset(w * 0.06f, f0), Size(w * 0.88f, f1 - f0))
                    val g = (f1 - f0) * 0.6f
                    var x = w * 0.08f
                    val y = f0 + (f1 - f0) * 0.2f
                    while (x < w * 0.9f) {
                        drawPath(Path().apply { moveTo(x, y + g); lineTo(x, y); lineTo(x + g * 0.8f, y); lineTo(x + g * 0.8f, y + g * 0.6f); lineTo(x + g * 0.4f, y + g * 0.6f); lineTo(x + g * 0.4f, y + g * 0.3f) },
                            c(ORO_A), style = Stroke(1.4f * u))
                        x += g * 1.1f
                    }
                }
                else -> for (q in 0 until 3) {
                    val o = Offset(w * (0.22f + 0.28f * q), (f0 + f1) / 2)
                    drawCircle(c(AZUL_A), (f1 - f0) * 0.42f, o)
                    drawCircle(c(ORO_A), (f1 - f0) * 0.3f, o, style = Stroke(1.4f * u))
                    drawCircle(c(ORO_A), (f1 - f0) * 0.1f, o)
                }
            }
            // El zócalo, con su filete de oro.
            drawRect(c(mezcla(acento, MADERA_OSC, 0.3f)), Offset(cuerpo.left, h * 0.87f), Size(cuerpo.width, h * 0.13f))
            drawRect(c(ORO_A), Offset(cuerpo.left, h * 0.865f), Size(cuerpo.width, 1.2f * u))
            // La pintura gastada en las esquinas y en el canto de arriba: asoma la madera.
            listOf(Offset(cuerpo.left, cuerpo.top + h * 0.02f), Offset(cuerpo.right, cuerpo.top + h * 0.03f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Offset(cuerpo.right, cuerpo.bottom - h * 0.05f), Offset(w * (0.3f + 0.4f * azar(semilla, 3)), cuerpo.bottom - h * 0.02f)).forEachIndexed { i, o ->
                drawPath(mancho(o, w * (0.03f + 0.02f * azar(semilla, i + 30)), h * 0.025f, semilla + i, 8), c(MADERA).copy(alpha = 0.85f))
            }
            // El volumen de la caja: la izquierda a la luz, la derecha en sombra; la sombra de la tapa.
            val sl = if (oscuro) hx else -1f
            drawRect(if (oscuro) luz.copy(alpha = 0.2f * k) else luzMueble(0.16f), Offset(if (sl < 0) cuerpo.left else cuerpo.right - cuerpo.width * 0.1f, cuerpo.top), Size(cuerpo.width * 0.1f, cuerpo.height))
            drawRect(sombra(oscuro, 0.2f), Offset(if (sl < 0) cuerpo.right - cuerpo.width * 0.16f else cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.16f, cuerpo.height))
            drawRect(sombra(oscuro, 0.3f), cuerpo.topLeft, Size(cuerpo.width, h * 0.03f))
        }
        // Las esquineras de hierro, con sus clavos.
        esquineras(cuerpo, w, oscuro, k, luz)
        // La tapa: la cara de arriba, el frente de otro color con su filete, y el cierre.
        val caraArriba = poli(tapa.left + w * 0.02f, tapa.top, tapa.right - w * 0.02f, tapa.top, tapa.right, tapa.top + h * 0.045f, tapa.left, tapa.top + h * 0.045f)
        drawPath(caraArriba, c(mezcla(color, LUZ_MUEBLE, 0.2f)))
        drawPath(mancho(Offset(tapa.left + w * 0.2f + w * 0.5f * azar(semilla, 7), tapa.top + h * 0.022f), w * 0.06f, h * 0.012f, semilla + 9, 8), c(MADERA).copy(alpha = 0.6f))
        val frente = Rect(tapa.left, tapa.top + h * 0.045f, tapa.right, tapa.bottom)
        drawRect(c(acento), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            pinceladas(frente, c(mezcla(acento, MADERA, 0.4f)), semilla + 2, 6, u, 0.18f)
            drawRect(c(ORO_A), Offset(frente.left + w * 0.02f, frente.top + frente.height * 0.25f), Size(frente.width - w * 0.04f, 1.2f * u))
            drawRect(c(ORO_A), Offset(frente.left + w * 0.02f, frente.bottom - frente.height * 0.25f), Size(frente.width - w * 0.04f, 1.2f * u))
            val sl = if (oscuro) hx else -1f
            drawRect(sombra(oscuro, 0.2f), Offset(if (sl < 0) frente.right - frente.width * 0.14f else frente.left, frente.top), Size(frente.width * 0.14f, frente.height))
            drawRect(sombra(oscuro, 0.22f), Offset(frente.left, frente.bottom - frente.height * 0.2f), Size(frente.width, frente.height * 0.2f))
        }
        drawRect(brillo(oscuro, 0.5f, k, luz), Offset(tapa.left, frente.top), Size(tapa.width, 1.2f * u))
        drawRect(brillo(oscuro, 0.35f, k, luz), Offset(tapa.left + w * 0.02f, tapa.top), Size(tapa.width - w * 0.04f, 1f * u))
        // El cierre de hierro: la aldaba que baja de la tapa al cuerpo.
        val ax = w * 0.5f
        val aldaba = Rect(ax - w * 0.025f, frente.top + frente.height * 0.3f, ax + w * 0.025f, cuerpo.top + h * 0.1f)
        drawRect(sombra(oscuro, 0.35f), Offset(aldaba.left + 1.6f * u, aldaba.top + 1.6f * u), aldaba.size)
        drawRect(n(HIERRO, oscuro, k, 0f, luz), aldaba.topLeft, aldaba.size)
        drawRect(brillo(oscuro, 0.5f, k, luz), aldaba.topLeft, Size(1.2f * u, aldaba.height))
        drawCircle(n(mezcla(HIERRO, PLATA, 0.3f), oscuro, k, 0f, luz), w * 0.012f, Offset(ax, aldaba.bottom - h * 0.025f), style = Stroke(1.6f * u))
    }

    /** Las esquineras de hierro de una caja, en ángulo, con sus clavos y el canto a la luz. */
    private fun DrawScope.esquineras(r: Rect, w: Float, oscuro: Boolean, k: Float, luz: Color) {
        val u = density
        val a = w * 0.065f; val g = w * 0.016f
        listOf(Triple(r.left, r.top, 1f to 1f), Triple(r.right, r.top, -1f to 1f), Triple(r.left, r.bottom, 1f to -1f), Triple(r.right, r.bottom, -1f to -1f)).forEach { (x, y, s) ->
            val (sx, sy) = s
            val p = poli(x, y, x + sx * a, y, x + sx * a, y + sy * g, x + sx * g, y + sy * g, x + sx * g, y + sy * a * 0.9f, x, y + sy * a * 0.9f)
            drawPath(corrido(p, 1.4f * u, 1.4f * u), sombra(oscuro, 0.3f))
            drawPath(p, n(HIERRO, oscuro, k, 0f, luz))
            drawPath(borde(p, 1.2f * u, 1.2f * u), brillo(oscuro, 0.45f, k, luz))
            drawCircle(n(mezcla(HIERRO, PLATA, 0.4f), oscuro, k, 0f, luz), w * 0.0045f, Offset(x + sx * g * 0.5f, y + sy * g * 0.5f))
            drawCircle(n(mezcla(HIERRO, PLATA, 0.4f), oscuro, k, 0f, luz), w * 0.0045f, Offset(x + sx * a * 0.75f, y + sy * g * 0.5f))
        }
    }

    /**
     * El baúl forrado de cuero: la tapa abombada, el repujado del borde, dos correas con hebilla de hierro que pasan por la
     * tapa y el cuerpo, las hileras de clavos, el cuero crudo que refuerza las esquinas de abajo y las rozaduras.
     */
    private fun DrawScope.baul(w: Float, h: Float, sube: Float, cuantas: Int, semilla: Int, mez: Int, oscuro: Boolean, k: Float, luz: Color, dia: Float, hx: Float) {
        val u = density
        fun c(col: Color, d: Float = dia) = n(col, oscuro, k, d, luz)
        val cuero = if ((mez ushr 2) % 2 == 0) CUERO else mezcla(CUERO, ROJO_A, 0.3f)
        val cuerpo = Rect(w * 0.035f, h * 0.4f, w * 0.965f, h)
        val tb = cuerpo.top - sube; val tt = h * 0.17f - sube
        val tapa = Path().apply {
            moveTo(cuerpo.left - w * 0.01f, tb); lineTo(cuerpo.left - w * 0.01f, tb - (tb - tt) * 0.4f)
            cubicTo(cuerpo.left, tt, cuerpo.right, tt, cuerpo.right + w * 0.01f, tb - (tb - tt) * 0.4f)
            lineTo(cuerpo.right + w * 0.01f, tb); close()
        }
        if (sube > 0.5f) {
            drawRect(c(Color(0xFF2A160E), 0f), Offset(cuerpo.left + w * 0.02f, tb - 2f * u), Size(cuerpo.width - w * 0.04f, sube + 6f * u))
            tablillas(cuerpo.left + w * 0.06f, cuerpo.right - w * 0.06f, tb - h * 0.08f, cuerpo.top + h * 0.08f, cuantas, semilla, oscuro, k, luz, dia)
        }
        val fc = Path().apply { addRect(cuerpo) }
        listOf(fc, tapa).forEachIndexed { i, p ->
            drawPath(p, c(cuero))
            clipPath(p) {
                val b = p.getBounds()
                pinceladas(b, c(mezcla(cuero, CUERO_CLARO, 0.6f)), semilla + i, 12, u, 0.2f)
                pinceladas(b, c(mezcla(cuero, MADERA_OSC, 0.6f)), semilla + i + 5, 8, u, 0.2f)
                // El repujado del borde: una línea hundida con su luz.
                val ins = Rect(b.left + w * 0.035f, b.top + h * 0.035f, b.right - w * 0.035f, b.bottom - h * 0.035f)
                drawRect(sombra(oscuro, 0.35f), ins.topLeft, ins.size, style = Stroke(1.4f * u))
                drawRect(brillo(oscuro, 0.25f, k, luz), Offset(ins.left + 1.2f * u, ins.top + 1.2f * u), ins.size, style = Stroke(0.8f * u))
                // Las rozaduras.
                drawPath(mancho(Offset(b.left + b.width * (0.2f + 0.6f * azar(semilla, i + 40)), b.bottom - b.height * 0.25f), w * 0.07f, h * 0.03f, semilla + i + 41, 9), c(mezcla(cuero, CUERO_CLARO, 0.5f)).copy(alpha = 0.6f))
                val sl = if (oscuro) hx else -1f
                drawRect(if (oscuro) luz.copy(alpha = 0.2f * k) else luzMueble(0.15f), Offset(if (sl < 0) b.left else b.right - b.width * 0.1f, b.top), Size(b.width * 0.1f, b.height))
                drawRect(sombra(oscuro, 0.22f), Offset(if (sl < 0) b.right - b.width * 0.16f else b.left, b.top), Size(b.width * 0.16f, b.height))
                if (i == 1) {
                    drawRect(brillo(oscuro, 0.3f, k, luz), Offset(b.left, b.top + b.height * 0.15f), Size(b.width, b.height * 0.12f))
                    drawRect(sombra(oscuro, 0.25f), Offset(b.left, b.bottom - b.height * 0.2f), Size(b.width, b.height * 0.2f))
                } else drawRect(sombra(oscuro, 0.3f), b.topLeft, Size(b.width, h * 0.035f))
            }
        }
        // El cuero crudo de las esquinas de abajo.
        listOf(1f, -1f).forEach { s ->
            val x = if (s > 0) cuerpo.left else cuerpo.right
            val p = poli(x, cuerpo.bottom, x, cuerpo.bottom - h * 0.16f, x + s * w * 0.1f, cuerpo.bottom)
            drawPath(p, c(mezcla(CUERO_CLARO, FIELTRO_PARED, 0.3f)))
            drawPath(p, sombra(oscuro, 0.3f), style = Stroke(1f * u))
        }
        // Las correas con su hebilla.
        listOf(0.25f, 0.75f).forEach { fx ->
            val x = w * fx
            val a = w * 0.07f
            drawRect(sombra(oscuro, 0.3f), Offset(x - a / 2 + 1.6f * u, tt + h * 0.02f), Size(a, h - tt))
            drawRect(c(mezcla(CUERO_CLARO, cuero, 0.45f)), Offset(x - a / 2, tt + h * 0.015f), Size(a, h - tt - h * 0.015f))
            drawRect(brillo(oscuro, 0.3f, k, luz), Offset(x - a / 2, tt + h * 0.015f), Size(1.2f * u, h - tt))
            drawLine(c(MADERA_OSC).copy(alpha = 0.5f), Offset(x - a * 0.35f, tt + h * 0.02f), Offset(x - a * 0.35f, h), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u)))
            drawLine(c(MADERA_OSC).copy(alpha = 0.5f), Offset(x + a * 0.35f, tt + h * 0.02f), Offset(x + a * 0.35f, h), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u)))
            val hb = Rect(x - a * 0.65f, cuerpo.top + h * 0.03f, x + a * 0.65f, cuerpo.top + h * 0.12f)
            drawRect(sombra(oscuro, 0.35f), Offset(hb.left + 1.4f * u, hb.top + 1.4f * u), hb.size, style = Stroke(w * 0.012f))
            drawRect(n(HIERRO, oscuro, k, 0f, luz), hb.topLeft, hb.size, style = Stroke(w * 0.012f))
            drawLine(n(HIERRO, oscuro, k, 0f, luz), Offset(hb.center.x, hb.top), Offset(hb.center.x, hb.bottom), w * 0.008f)
            drawLine(brillo(oscuro, 0.6f, k, luz), Offset(hb.left, hb.top), Offset(hb.right, hb.top), 1f * u)
        }
        // Los clavos del canto de la tapa y del cuerpo.
        var x = cuerpo.left + w * 0.03f
        while (x < cuerpo.right - w * 0.02f) {
            listOf(cuerpo.top + h * 0.018f, tb - h * 0.02f).forEach { y ->
                drawCircle(n(mezcla(HIERRO, ORO_A, 0.35f), oscuro, k, 0f, luz), w * 0.006f, Offset(x, y))
                drawCircle(brillo(oscuro, 0.7f, k, luz), w * 0.0022f, Offset(x - w * 0.002f, y - w * 0.002f))
            }
            x += w * 0.068f
        }
    }

    /**
     * La caja de corteza de abedul: el cuerpo blanco con sus lenticelas y cicatrices, la costura de raíz en zigzag del canto,
     * el borde de corteza rojiza (la cara de dentro, enrollada), un zigzag rayado a punzón y la tapa cosida.
     */
    private fun DrawScope.cajaAbedul(w: Float, h: Float, sube: Float, cuantas: Int, semilla: Int, oscuro: Boolean, k: Float, luz: Color, dia: Float, hx: Float) {
        val u = density
        fun c(col: Color, d: Float = dia) = n(col, oscuro, k, d, luz)
        val corteza = mezcla(ABEDUL, FIELTRO_PARED, 0.4f)
        val rojiza = Color(0xFFA8643C)
        val cuerpo = Rect(w * 0.035f, h * 0.38f, w * 0.965f, h)
        val tapa = Rect(w * 0.02f, h * 0.22f - sube, w * 0.98f, h * 0.395f - sube)
        if (sube > 0.5f) {
            drawRect(c(Color(0xFF2A160E), 0f), Offset(cuerpo.left + w * 0.02f, tapa.bottom - 2f * u), Size(cuerpo.width - w * 0.04f, cuerpo.top - tapa.bottom + 6f * u))
            tablillas(cuerpo.left + w * 0.05f, cuerpo.right - w * 0.05f, tapa.bottom - h * 0.06f, cuerpo.top + h * 0.08f, cuantas, semilla, oscuro, k, luz, dia)
        }
        val fc = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(w * 0.03f))) }
        val ft = Path().apply { addRoundRect(RoundRect(tapa, CornerRadius(w * 0.02f))) }
        listOf(fc, ft).forEachIndexed { i, p ->
            val b = p.getBounds()
            drawPath(p, c(corteza))
            clipPath(p) {
                // Las lenticelas: rayas oscuras horizontales; alguna cicatriz en «ojo»; vetas pardas.
                for (q in 0 until (if (i == 0) 16 else 7)) {
                    val x = b.left + b.width * azar(semilla + i, q)
                    val y = b.top + b.height * azar(semilla + i, q + 50)
                    val l = w * (0.02f + 0.05f * azar(semilla + i, q + 80))
                    drawLine(c(LENTICELA, 0f).copy(alpha = 0.35f + 0.3f * azar(semilla, q)), Offset(x, y), Offset(x + l, y), (0.9f + 0.8f * azar(semilla + i, q + 90)) * u)
                }
                if (i == 0) {
                    val o = Offset(b.left + b.width * (0.15f + 0.1f * azar(semilla, 61)), b.top + b.height * 0.3f)
                    drawPath(Path().apply { moveTo(o.x - w * 0.03f, o.y - h * 0.03f); lineTo(o.x, o.y); lineTo(o.x + w * 0.03f, o.y - h * 0.03f) }, c(LENTICELA, 0f).copy(alpha = 0.6f), style = Stroke(1.6f * u))
                    // El zigzag rayado a punzón, en el friso de arriba.
                    val zz = Path().apply {
                        moveTo(b.left + w * 0.06f, h * 0.47f)
                        var x = b.left + w * 0.06f; var arriba = true
                        while (x < b.right - w * 0.08f) { x += w * 0.035f; lineTo(x, if (arriba) h * 0.42f else h * 0.5f); arriba = !arriba }
                    }
                    drawPath(corrido(zz, 0.8f * u, 0.8f * u), brillo(oscuro, 0.35f, k, luz), style = Stroke(1f * u))
                    drawPath(zz, c(rojiza, 0f).copy(alpha = 0.8f), style = Stroke(1.2f * u))
                    // El borde de abajo, de corteza rojiza.
                    drawRect(c(rojiza), Offset(b.left, b.bottom - h * 0.07f), Size(b.width, h * 0.07f))
                    drawRect(c(mezcla(rojiza, LUZ_MUEBLE, 0.3f)), Offset(b.left, b.bottom - h * 0.07f), Size(b.width, 1.2f * u))
                } else {
                    // El borde enrollado de la tapa.
                    drawRect(c(rojiza), Offset(b.left, b.top), Size(b.width, b.height * 0.24f))
                    drawRect(brillo(oscuro, 0.4f, k, luz), Offset(b.left, b.top), Size(b.width, 1.4f * u))
                }
                val sl = if (oscuro) hx else -1f
                drawRect(if (oscuro) luz.copy(alpha = 0.18f * k) else luzMueble(0.18f), Offset(if (sl < 0) b.left else b.right - b.width * 0.1f, b.top), Size(b.width * 0.1f, b.height))
                drawRect(sombra(oscuro, 0.22f), Offset(if (sl < 0) b.right - b.width * 0.16f else b.left, b.top), Size(b.width * 0.16f, b.height))
                if (i == 0) drawRect(sombra(oscuro, 0.3f), b.topLeft, Size(b.width, h * 0.035f))
                else drawRect(sombra(oscuro, 0.2f), Offset(b.left, b.bottom - b.height * 0.2f), Size(b.width, b.height * 0.2f))
            }
        }
        // La costura de raíz: el zigzag del canto derecho y las puntadas de la tapa.
        val xs = cuerpo.right - w * 0.075f
        var y = cuerpo.top + h * 0.03f; var lado = 1f
        val raiz = n(mezcla(MADERA, CUERO, 0.5f), oscuro, k, 0f, luz)
        while (y < cuerpo.bottom - h * 0.09f) {
            drawLine(raiz, Offset(xs - w * 0.02f * lado, y), Offset(xs + w * 0.02f * lado, y + h * 0.04f), 1.8f * u, StrokeCap.Round)
            y += h * 0.04f; lado = -lado
        }
        var x = tapa.left + w * 0.04f
        while (x < tapa.right - w * 0.03f) { drawLine(raiz, Offset(x, tapa.bottom - h * 0.035f), Offset(x + w * 0.015f, tapa.bottom - h * 0.01f), 1.4f * u, StrokeCap.Round); x += w * 0.04f }
    }

    /**
     * Lo que no tiene tema: un saco de cuero crudo abierto, con la boca vuelta, la correa suelta y las tablillas metidas de
     * cualquier modo; una se salió y quedó apoyada.
     */
    private fun DrawScope.saco(w: Float, h: Float, cuantas: Int, semilla: Int, ab: Float, oscuro: Boolean, k: Float, luz: Color, dia: Float, hx: Float) {
        val u = density
        fun c(col: Color, d: Float = dia) = n(col, oscuro, k, d, luz)
        val crudo = mezcla(CUERO_CLARO, FIELTRO_PARED, 0.2f)
        val forma = sacoForma(w, h)
        drawOval(c(Color(0xFF2A160E), 0f), Offset(w * 0.15f, h * 0.27f), Size(w * 0.7f, h * 0.12f))
        tablillas(w * 0.18f, w * 0.82f, h * 0.06f - ab * h * 0.1f, h * 0.4f, cuantas.coerceAtLeast(2), semilla, oscuro, k, luz, dia, giroMax = 16f)
        drawPath(forma, c(crudo))
        clipPath(forma) {
            pinceladas(forma.getBounds(), c(mezcla(crudo, CUERO, 0.5f)), semilla, 14, u, 0.2f)
            // Los pliegues que bajan de la boca y se abren con el peso.
            for (q in 0 until 6) {
                val fx = 0.2f + 0.12f * q + 0.03f * azar(semilla, q)
                val pl = Path().apply { moveTo(w * fx, h * 0.34f); cubicTo(w * (fx - 0.03f), h * 0.5f, w * (fx + 0.04f), h * 0.62f, w * (fx + (fx - 0.5f) * 0.4f), h * 0.78f) }
                drawPath(pl, sombra(oscuro, 0.22f), style = Stroke(w * 0.02f, cap = StrokeCap.Round))
                drawPath(corrido(pl, -w * 0.012f, 0f), brillo(oscuro, 0.25f, k, luz), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
            }
            drawRect(sombra(oscuro, 0.22f), Offset(0f, h * 0.9f), Size(w, h * 0.1f))
        }
        volumen(forma, oscuro, k, hx, luz, fuerza = 1.1f)
        // La boca vuelta y la correa suelta.
        val labio = Path().apply {
            moveTo(w * 0.14f, h * 0.33f)
            cubicTo(w * 0.22f, h * 0.3f, w * 0.32f, h * 0.38f, w * 0.42f, h * 0.34f)
            cubicTo(w * 0.55f, h * 0.29f, w * 0.7f, h * 0.36f, w * 0.85f, h * 0.31f)
        }
        drawPath(labio, c(mezcla(crudo, LUZ_MUEBLE, 0.3f)), style = Stroke(h * 0.035f, cap = StrokeCap.Round))
        drawPath(corrido(labio, 0f, h * 0.025f), sombra(oscuro, 0.3f), style = Stroke(h * 0.015f))
        val correa = Path().apply { moveTo(w * 0.78f, h * 0.36f); cubicTo(w * 0.86f, h * 0.42f, w * 0.84f, h * 0.48f, w * 0.9f, h * 0.53f) }
        drawPath(correa, c(CUERO), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
    }

    /** El panel claro del rótulo: pintado, con las esquinas recortadas en cuarto de círculo y su filete (o un pespunte). */
    private fun DrawScope.panel(r: Rect, tipo: Int, semilla: Int, oscuro: Boolean, k: Float, luz: Color, dia: Float) {
        val u = density
        val e = r.height * 0.18f
        val p = Path().apply {
            moveTo(r.left + e, r.top); lineTo(r.right - e, r.top)
            arcTo(Rect(r.right - e, r.top - e, r.right + e, r.top + e), 180f, -90f, false)
            lineTo(r.right, r.bottom - e)
            arcTo(Rect(r.right - e, r.bottom - e, r.right + e, r.bottom + e), 270f, -90f, false)
            lineTo(r.left + e, r.bottom)
            arcTo(Rect(r.left - e, r.bottom - e, r.left + e, r.bottom + e), 0f, -90f, false)
            lineTo(r.left, r.top + e)
            arcTo(Rect(r.left - e, r.top - e, r.left + e, r.top + e), 90f, -90f, false)
            close()
        }
        val fondo = if (oscuro) mezcla(PANEL_NOCHE, PANEL_LUZ, k * 1.2f) else mezcla(if (tipo == 3) mezcla(PANEL, CUERO_CLARO, 0.12f) else PANEL, LUZ_MUEBLE, dia)
        drawPath(p, fondo)
        clipPath(p) {
            pinceladas(r, mezcla(fondo, CUERO_CLARO, 0.5f), semilla + 3, 6, u, 0.12f)
            drawRect(brillo(oscuro, 0.35f, k, luz), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(sombra(oscuro, 0.1f), Offset(r.left, r.bottom - 2.4f * u), Size(r.width, 2.4f * u))
        }
        if (tipo == 3) {
            drawPath(p, TINTA_ROTULO.copy(alpha = 0.32f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.5f * u))))
            return
        }
        val q = Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u)
        drawRect(n(AZUL_A, oscuro, maxOf(k, 0.6f), 0f, luz), Offset(q.left + e * 0.5f, q.top), Size(q.width - e, 1.2f * u))
        drawRect(n(AZUL_A, oscuro, maxOf(k, 0.6f), 0f, luz), Offset(q.left + e * 0.5f, q.bottom - 1.2f * u), Size(q.width - e, 1.2f * u))
        // En el baúl, el panel va clavado; en la caja de abedul, cosido por los lados.
        if (tipo == 1) listOf(r.left + e * 0.9f, r.right - e * 0.9f).forEach { x ->
            listOf(r.top + e * 0.9f, r.bottom - e * 0.9f).forEach { y ->
                drawCircle(n(HIERRO, oscuro, maxOf(k, 0.5f), 0f, luz), 1.8f * u, Offset(x, y))
            }
        }
        if (tipo == 2) listOf(r.left + 2f * u, r.right - 2f * u).forEach { x ->
            var y = r.top + e
            while (y < r.bottom - e) { drawLine(n(mezcla(MADERA, CUERO, 0.5f), oscuro, maxOf(k, 0.5f), 0f, luz), Offset(x - 1.5f * u, y), Offset(x + 1.5f * u, y + 3f * u), 1.2f * u); y += 5f * u }
        }
    }

    /** Un hueco sin tema: la tabla vacía con un fieltro doblado, o con un cuenco de madera volcado y un rollo de cuerda. */
    private fun DrawScope.vacio(semilla: Int, oscuro: Boolean, k: Float, luz: Color, dia: Float, hx: Float) {
        val u = density
        val w = size.width; val h = size.height
        fun c(col: Color) = n(col, oscuro, k, dia, luz)
        if (Math.floorMod(semilla / 7919, 2) == 0) {
            // El fieltro doblado: tres capas, con el canto del doblez redondo a la izquierda y el pespunte rojo.
            for (q in 0 until 3) {
                val y1 = h - q * h * 0.11f
                val y0 = y1 - h * 0.12f
                val x0 = w * (0.12f + 0.02f * q); val x1 = w * (0.8f - 0.03f * q)
                val capa = Path().apply { addRoundRect(RoundRect(Rect(x0, y0, x1, y1), CornerRadius(h * 0.06f))) }
                val col = if (q == 1) FIELTRO_GRIS else FIELTRO_PARED
                drawPath(capa, c(col))
                clipPath(capa) {
                    drawRect(sombra(oscuro, 0.22f), Offset(x0, y1 - h * 0.035f), Size(x1 - x0, h * 0.035f))
                    drawRect(brillo(oscuro, 0.4f, k, luz), Offset(x0, y0), Size(x1 - x0, 1.4f * u))
                    drawLine(c(ROJO_A).copy(alpha = 0.8f), Offset(x0 + w * 0.05f, y0 + h * 0.05f), Offset(x1 - w * 0.04f, y0 + h * 0.05f), 1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * u, 3f * u)))
                }
                volumen(capa, oscuro, k, hx, luz, fuerza = 0.7f)
            }
        } else {
            // El cuenco volcado, boca abajo, y el rollo de cuerda de crin.
            val bc = Offset(w * 0.34f, h)
            val cuenco = Path().apply {
                moveTo(bc.x - w * 0.16f, bc.y)
                cubicTo(bc.x - w * 0.16f, bc.y - h * 0.24f, bc.x + w * 0.16f, bc.y - h * 0.24f, bc.x + w * 0.16f, bc.y)
                close()
            }
            drawPath(cuenco, c(MADERA))
            clipPath(cuenco) { vetas(cuenco.getBounds(), c(MADERA_OSC).copy(alpha = 0.5f), semilla, 0.8f, ondulacion = 2f) }
            drawRect(c(MADERA_OSC), Offset(bc.x - w * 0.06f, bc.y - h * 0.215f), Size(w * 0.12f, h * 0.03f))
            volumen(cuenco, oscuro, k, hx, luz, lustre = 0.3f)
            val rc = Offset(w * 0.68f, h - h * 0.06f)
            for (q in 0 until 4) {
                val rx = w * (0.15f - 0.03f * q); val ry = h * (0.07f - 0.012f * q)
                drawOval(sombra(oscuro, 0.3f), Offset(rc.x - rx + 1.5f * u, rc.y - ry - q * h * 0.03f + 1.5f * u), Size(rx * 2, ry * 2), style = Stroke(w * 0.022f))
                drawOval(c(Color(0xFF5A4636)), Offset(rc.x - rx, rc.y - ry - q * h * 0.03f), Size(rx * 2, ry * 2), style = Stroke(w * 0.022f))
                drawArc(brillo(oscuro, 0.3f, k, luz), 190f, 120f, false, Offset(rc.x - rx, rc.y - ry - q * h * 0.03f), Size(rx * 2, ry * 2), style = Stroke(w * 0.006f))
            }
        }
    }

    override val rotulo = Rect(0.045f, 0.555f, 0.955f, 0.795f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA_ROTULO
    override val letraRotulo: FontFamily = LetraExtra.filosofo
    override val salida = 0f
    /** El fieltro doblado sobre la alfombra, al pie. */
    override val placa = Rect(0.33f, 0.42f, 0.67f, 0.84f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA_ROTULO

    // ---------------------------------------------------------------- el interior y las fichas (no cambian)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Fieltro blanco, acolchado con pespuntes rojos en rombos y espirales.
        val f = if (oscuro) mezcla(FIELTRO, Color.Black, 0.6f) else FIELTRO
        drawRect(f)
        val pesp = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u))
        val p = 60 * u
        var d0 = -h
        while (d0 < w + h) {
            drawLine(ROJO.copy(alpha = 0.3f), Offset(d0, 0f), Offset(d0 + h, h), 1.2f * u, pathEffect = pesp)
            drawLine(ROJO.copy(alpha = 0.3f), Offset(d0 + h, 0f), Offset(d0, h), 1.2f * u, pathEffect = pesp)
            d0 += p
        }
        var y = p / 2
        while (y < h) {
            var x = p / 2
            while (x < w) { voluta(Offset(x, y), p * 0.16f, ROJO.copy(alpha = 0.28f), 1.2f * u); x += p }
            y += p
        }
        // El marco pintado de naranja, con su filete azul.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(NARANJA, oscuro), r.topLeft, r.size) }
        drawRect(t(AZUL, oscuro), Offset(pl * 0.35f, pl * 0.35f), Size(w - pl * 0.7f, h - pl * 0.7f), style = Stroke(2 * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.25f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) CREMA else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 22, 0.012f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(ABEDUL.copy(alpha = 0.55f))
        // Las lenticelas: rayitas oscuras horizontales, y alguna veta parda.
        val n = if (grande) 60 else 26
        for (k in 0 until n) {
            val x = w * azar(semilla, k); val y = h * azar(semilla, k + 100)
            val l = (5f + 12f * azar(semilla, k + 200)) * u * g
            drawLine(LENTICELA.copy(alpha = 0.18f + 0.14f * azar(semilla, k + 300)), Offset(x, y), Offset(x + l, y), (0.8f + 0.8f * azar(semilla, k + 400)) * u)
        }
        for (k in 0 until 3) {
            val y = h * (0.2f + 0.3f * k + 0.1f * azar(semilla, k + 500))
            drawLine(Color(0xFF9C7A4E).copy(alpha = 0.12f), Offset(0f, y), Offset(w, y + 3 * u), 2.5f * u * g)
        }
        // La corteza que se levanta en una esquina.
        val e = 12 * u * g
        drawPath(Path().apply { moveTo(w - e, 0f); lineTo(w, 0f); lineTo(w, e); close() }, Color(0xFFB8926A).copy(alpha = 0.6f))
        drawLine(Color.Black.copy(alpha = 0.2f), Offset(w - e, 0f), Offset(w, e), 0.8f * u)
    }

    override val margenes = MargenesFicha(13.dp, 13.dp, 12.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 1.05f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La tamga: el sello.
            "hecho" -> selloHecho(ROJO, p.color, cuadrado = true)
            // El sol sobre la luna creciente.
            "convence" -> {
                drawCircle(ORO, s * 0.16f, Offset(c.x, c.y - s * 0.24f))
                drawArc(ORO, 0f, 180f, false, Offset(c.x - s * 0.3f, c.y - s * 0.2f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.12f, cap = StrokeCap.Round))
            }
            // Un nudo sin fin, simplificado: un rombo con un lazo en cada punta.
            "piensa" -> {
                drawPath(estrella(c, s * 0.3f, 2, 1f, 0f), AZUL, style = Stroke(s * 0.08f))
                drawRect(AZUL, Offset(c.x - s * 0.2f, c.y - s * 0.2f), Size(s * 0.4f, s * 0.4f), style = Stroke(s * 0.08f))
                listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { o -> drawCircle(AZUL, s * 0.1f, Offset(c.x + o.x * s * 0.34f, c.y + o.y * s * 0.34f), style = Stroke(s * 0.07f)) }
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un jadag azul: el pañuelo de seda anudado. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawOval(AZUL, Offset(c.x - s * 0.36f, c.y - s * 0.44f), Size(s * 0.72f, s * 0.5f), style = Stroke(s * 0.12f))
        drawPath(Path().apply { moveTo(c.x - s * 0.12f, c.y); lineTo(c.x - s * 0.3f, c.y + s * 0.48f); lineTo(c.x - s * 0.06f, c.y + s * 0.4f); close() }, AZUL)
        drawPath(Path().apply { moveTo(c.x + s * 0.12f, c.y); lineTo(c.x + s * 0.3f, c.y + s * 0.48f); lineTo(c.x + s * 0.06f, c.y + s * 0.4f); close() }, AZUL)
        drawCircle(AZUL_OSC, s * 0.1f, Offset(c.x, c.y + s * 0.02f))
    }

    override val desorden = 2f
}
