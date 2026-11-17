package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
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
//  Mesoamérica
// =====================================================================

/**
 * Mesoamérica: el cuarto donde un escriba maya trabaja y guarda sus cosas. El muro es de estuco pintado: el paño ocre claro
 * arriba, una banda de grecas escalonadas (cinabrio y azul maya sobre crema) con su filete azul, y el zócalo rojo óxido, con
 * desconchones que dejan ver la caliza, parches repasados y el hollín que el copal deja encima del incensario. Contra él, la
 * banqueta de mampostería estucada, en gradas: una grada por fila, cada una un poco más ancha que la de arriba, con su cara de
 * almagre rosado y gastado (asoma el estuco junto a los cantos y donde rozan los recipientes), su franja honda de cinabrio al
 * pie, su huella de estuco crema con el canto comido y su sombra en el muro. Encima de la grada alta, lo del escriba: el
 * códice plegado en biombo, medio abierto, con sus tapas de piel de jaguar (lo primero que se ve, a la izquierda); detrás, el
 * abanico de plumas de quetzal apoyado en el muro; una red con un calabazo colgada de una estaca; el vaso cilíndrico
 * policromo con los pinceles; la tea de ocote en su soporte de barro (apagada de día, con la punta tiznada); dos medias
 * conchas con pigmento (rojo, azul maya y negro); el incensario de púas con su copal y su hilo de humo; y, de otra estaca, un
 * collar de cuentas de jade con su pectoral. Al pie, un petate enrollado tendido contra la grada (cortado por el
 * borde) y el brasero trípode de barro con sus brasas. Cada tema es lo que se guarda en una grada, según la semilla: una
 * petaca de palma tejida con su tapa atada con henequén, una olla con tapa (con una banda de glifos inventados o con engobe
 * rojo) o un bulto envuelto en manta de algodón con su nudo; las hojas de amate dobladas asoman (más cuantas más fichas) y el
 * tema va en una tira de amate con filete rojo colgada al frente. Lo que no tiene tema va en una jícara abierta con las hojas
 * sueltas, y su tira no lleva filete. Un hueco sin tema es la grada con el cerco limpio donde hubo una petaca y un caracol o
 * una mazorca seca. De noche arden la tea, arriba (halo rojizo en el paño y las grecas, el códice encendido, filo en las
 * plumas, brillo en el jade, sombras largas hacia fuera de la llama y su hilo de humo negro), y el brasero, abajo a la
 * derecha (el cinabrio de las gradas bajas se enciende); cada compartimento suma la luz de las dos según su fila y su
 * columna, así que las gradas de en medio se pierden, y lo que se apaga baja hacia su propio color oscuro (palma parda,
 * barro rojo hondo, algodón crema sucio), no hacia gris. Por dentro, el tejido de la petaca.
 * Las fichas son hojas de amate cubiertas de estuco, con el filete rojo de las páginas de códice.
 */
object IdeasMeso : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta
    /** El estuco crema (huellas de las gradas, fondo de la greca) y el paño ocre claro del muro. */
    private val ESTUCO = Color(0xFFF2E8D2)
    private val PANO = Color(0xFFE4CB98)
    private val OCRE = Color(0xFFD69A55)
    /** El cinabrio del zócalo y de las grecas; el almagre gastado de las caras de la banqueta. */
    private val CINABRIO = Color(0xFFA3301E)
    private val ALMAGRE = Color(0xFFC88468)
    /** El azul maya y el verde jade. */
    /** El rojo óxido del zócalo del muro, más hondo y más pardo que las caras de la banqueta. */
    private val OXIDO = Color(0xFF8A3324)
    /** La franja honda de cinabrio al pie de cada cara. */
    private val FRANJA = Color(0xFF8E2A1A)
    /** Las astillas de ocote (pino resinoso) de la tea. */
    private val OCOTE = Color(0xFFC08A4E)
    private val AZUL = Color(0xFF2C7F9C)
    private val JADE = Color(0xFF2F7D57)
    private val JADE_LUZ = Color(0xFF86C8A0)
    /** El negro de humo (también la tinta de los rótulos). */
    private val NEGRO = Color(0xFF2A1810)
    /** La caliza que asoma bajo el estuco. */
    private val PIEDRA = Color(0xFF9D978A)
    /** La palma de las petacas y del petate. */
    private val PALMA = Color(0xFFCDB585)
    private val PALMA_OSC = Color(0xFF9C8458)
    private val PALMA_LUZ = Color(0xFFE6D2A0)
    /** El barro cocido y su sombra. */
    private val BARRO = Color(0xFFB36A3C)
    private val BARRO_OSC = Color(0xFF7A4326)
    /** El amate: la corteza, la hoja aclarada y la fibra. */
    private val AMATE = Color(0xFFB89A70)
    private val AMATE_CLARO = Color(0xFFDCC7A0)
    private val AMATE_OSC = Color(0xFF8A6E4A)
    /** La manta de algodón, la piel de jaguar, la pluma de quetzal. */
    private val ALGODON = Color(0xFFEDE4D0)
    private val JAGUAR = Color(0xFFD49A45)
    private val QUETZAL = Color(0xFF1E8C5A)
    /** La noche del cuarto (azul negro) y la luz de las brasas y del copal. */
    private val NOCHE = Color(0xFF0C1019)
    private val BRASA = Color(0xFFFF9A4A)
    private val BRASA_VIVA = Color(0xFFFFD27A)
    /**
     * La tira del rótulo de noche: de [TIRA_NOCHE] (lejos de la lumbre) a [TIRA_LUZ] (junto a ella). Con la tinta [NEGRO], la
     * de lejos da unos 7:1 en el título y unos 4.9:1 en el subtítulo (78 %).
     */
    private val TIRA_NOCHE = Color(0xFFC4AC86)
    private val TIRA_LUZ = Color(0xFFF0DAAC)
    private val HENEQUEN = Color(0xFFCDB585)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.32f) else c

    // ---------------------------------------------------------------- la luz y la noche

    /**
     * Un color a [k] (0 a 1) de la lumbre: de día, tal cual (aclarado [dia]). De noche se apaga hacia **su propio** oscuro: se
     * baja el valor conservando el matiz (la palma se vuelve parda, el barro rojo hondo, el algodón crema sucio), con apenas
     * un poco del azul negro del cuarto para unir la paleta; cerca de la lumbre sube el valor y se calienta.
     */
    private fun n(c: Color, oscuro: Boolean, k: Float, dia: Float = 0f): Color {
        if (!oscuro) return if (dia > 0f) mezcla(c, LUZ_MUEBLE, dia) else c
        val q = k.coerceIn(0f, 1f)
        val v = 0.24f + 0.6f * q.pow(1.1f)
        val bajo = Color(c.red * v, c.green * v, c.blue * v, c.alpha)
        return mezcla(mezcla(bajo, NOCHE, 0.1f * (1f - q)), BRASA, 0.2f * q)
    }

    /** La sombra de noche: violeta muy hondo, no azul marino. */
    private val SOMBRA_NOCHE = Color(0xFF140D12)

    /** La sombra: de día, violeta hondo; de noche, violeta casi negro. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) SOMBRA_NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** Lo que mira a la luz: de día, crema; de noche, el filo de la lumbre según lo cerca que esté. */
    private fun brillo(oscuro: Boolean, a: Float, k: Float = 0f) =
        if (oscuro) BRASA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else luzMueble(a)

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.3f
    override val corona = 0.52f
    override val base = 0.22f
    override val lado = 0.065f
    override val hueco = 0.045f

    /** Del suelo: cuánto baja de la última huella hasta el piso, contado desde el arranque de la base (fracción del ancho). */
    private const val ALTO_PISO = 0.05f

    private fun anchoH() = (1f - 2f * lado - hueco * (cols - 1)) / cols
    private fun altoH() = anchoH() / proporcion
    /** El piso, en fracciones del ancho, con [filas] filas. */
    private fun pisoY(filas: Int) = corona + hueco + filas * altoH() + (filas - 1) * hueco + hueco + ALTO_PISO
    /**
     * Las luces de noche, en fracciones del ancho: las brasas del brasero (abajo), la llama de la tea de ocote (arriba, la que
     * alumbra el códice) y, mucho menor, el copal del incensario.
     */
    private fun brasero(filas: Int) = Offset(0.8f, pisoY(filas) + 0.012f)
    private val copal get() = Offset(COPAL_X, corona - 0.15f)
    private const val COPAL_X = 0.8f
    /** La tea: dónde está su soporte y a qué altura sobre la grada alta arde la llama. */
    private const val TEA_X = 0.685f
    private const val TEA_ALTO = 0.255f
    private val tea get() = Offset(TEA_X - 0.012f, corona - TEA_ALTO - 0.02f)

    private fun deBrasero(p: Offset, filas: Int) = (1.15f * (1f - (p - brasero(filas)).getDistance() / 1.0f)).coerceAtLeast(0f)
    private fun deTea(p: Offset) = (1.1f * (1f - (p - tea).getDistance() / 1.0f)).coerceAtLeast(0f)
    private fun deCopal(p: Offset) = (0.5f * (1f - (p - copal).getDistance() / 0.3f)).coerceAtLeast(0f)

    /**
     * Cuánto alumbran el punto [p] (fracciones del ancho) las luces juntas, con [filas] filas: la suma de las tres. Así las
     * filas de arriba (por la tea) y las de abajo (por el brasero) quedan encendidas y las de en medio se pierden.
     */
    private fun calor(p: Offset, filas: Int): Float = (deBrasero(p, filas) + deTea(p) + deCopal(p)).coerceIn(0f, 1f)

    /** La luz que manda en [p]: la que más alumbra (para el lado del filo y hacia dónde caen las sombras). */
    private fun luzMayor(p: Offset, filas: Int): Offset {
        val a = deBrasero(p, filas); val b = deTea(p); val c = deCopal(p)
        return if (a >= b && a >= c) brasero(filas) else if (b >= c) tea else copal
    }

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    /** Un polígono con los puntos dados como x, y seguidos. */
    private fun poli(vararg v: Float) = Path().apply {
        moveTo(v[0], v[1]); var i = 2
        while (i < v.size) { lineTo(v[i], v[i + 1]); i += 2 }
        close()
    }

    /** Une [q] con curvas suaves (Catmull-Rom pasado a Bézier). */
    private fun suave(p: Path, q: List<Offset>) {
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            p.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }

    /** El contorno de algo torneado: [p] son pares (medio ancho, altura) de arriba abajo, en unidades de [sx] y [sy]. */
    private fun perfil(cx: Float, y0: Float, sx: Float, sy: Float, p: FloatArray): Path {
        val der = List(p.size / 2) { Offset(cx + p[2 * it] * sx, y0 + p[2 * it + 1] * sy) }
        val izq = der.reversed().map { Offset(2 * cx - it.x, it.y) }
        return Path().apply { moveTo(der[0].x, der[0].y); suave(this, der); lineTo(izq[0].x, izq[0].y); suave(this, izq); close() }
    }

    /** Una forma irregular alrededor de [o] (desconchones, piedras, brasas). */
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
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz (en dos tonos),
     * el claro del lado de la luz y, si [lustre] es más de 0, un brillo. De día la luz viene de la izquierda; de noche, del
     * lado [haciaX] (1 si la lumbre está a la derecha), con un filo cálido tanto más fuerte cuanto más [k].
     */
    private fun DrawScope.volumen(forma: Path, oscuro: Boolean, k: Float, haciaX: Float, lustre: Float = 0f, fuerza: Float = 1f) {
        val r = forma.getBounds(); val a = r.width; val hh = r.height
        val sl = if (oscuro) haciaX else -1f
        drawPath(borde(forma, sl * a * 0.26f, -hh * 0.02f), sombra(oscuro, 0.2f * fuerza))
        drawPath(borde(forma, sl * a * 0.1f, -hh * 0.01f), sombra(oscuro, 0.18f * fuerza))
        clipPath(forma) {
            drawOval(if (oscuro) BRASA.copy(alpha = 0.22f * k) else luzMueble(0.2f), Offset(if (sl < 0) r.left + a * 0.1f else r.right - a * 0.34f, r.top + hh * 0.08f), Size(a * 0.24f, hh * 0.7f))
            if (lustre > 0f) drawRoundRect(if (oscuro) BRASA_VIVA.copy(alpha = (lustre * 1.4f * k).coerceAtMost(0.7f)) else luzMueble(lustre),
                Offset(if (sl < 0) r.left + a * 0.2f else r.right - a * 0.25f, r.top + hh * 0.16f), Size(a * 0.05f, hh * 0.36f), CornerRadius(a * 0.03f))
        }
        if (oscuro && k > 0.03f) drawPath(borde(forma, -sl * a * 0.045f, hh * 0.03f), BRASA.copy(alpha = (0.06f + 0.55f * k).coerceAtMost(0.6f)))
    }

    // ---------------------------------------------------------------- lo que hay detrás de cada hueco, de noche

    /** El borde de arriba de la cara [j] de la banqueta (0, la de la grada alta), en fracciones del ancho, con [filas] filas. */
    private fun bordeCara(j: Int) = if (j == 0) corona else corona + hueco + j * altoH() + (j - 1) * hueco
    /** El margen de la cara [j] (las de abajo son más anchas): el mismo que usa [mueble]. */
    private fun margenCara(j: Int, filas: Int) = minOf(0.008f + 0.012f * (filas - j), 0.058f)

    /** Un color de un degradado horizontal de cinco paradas ([paradas]) entre [x0] y [x1], en [x]: como lo pinta `Brush.horizontalGradient`. */
    private fun enDegradado(paradas: List<Color>, x0: Float, x1: Float, x: Float): Color {
        val t = ((x - x0) / (x1 - x0)).coerceIn(0f, 1f) * (paradas.size - 1)
        val i = minOf(t.toInt(), paradas.size - 2)
        return mezcla(paradas[i], paradas[i + 1], t - i)
    }

    /**
     * El color que [mueble] pinta de noche detrás de un compartimento de la fila [fila], en el punto ([x], [y]) en fracciones
     * del ancho, sin las pinceladas ni el grano: la cara de almagre (su degradado hacia la lumbre), la franja honda de abajo
     * o la huella de estuco donde se apoya lo guardado. Son las mismas cuentas que en [mueble].
     */
    private fun fondoNoche(x: Float, y: Float, fila: Int, filas: Int): Color {
        val arriba = bordeCara(fila); val abajo = bordeCara(fila + 1)
        val td = hueco * 0.6f
        fun k(px: Float, py: Float) = calor(Offset(px, py), filas)
        if (y >= abajo - td) {
            val m = margenCara(fila + 1, filas) - 0.004f
            val yc = abajo - td / 2
            return enDegradado(List(5) { i -> n(ESTUCO, true, k(m + (1f - 2 * m) * i / 4f, yc)) }, m, 1f - m, x)
        }
        val banda = minOf((abajo - arriba) * 0.3f, 0.06f)
        if (y >= abajo - banda) return n(FRANJA, true, k(0.5f, abajo - banda / 2))
        val m = margenCara(fila, filas)
        val yc = (arriba + abajo) / 2
        return enDegradado(List(5) { i -> n(ALMAGRE, true, k(m + (1f - 2 * m) * i / 4f, yc)) }, m, 1f - m, x)
    }

    /**
     * Tapa el aura en reposo de `Modifier.halo()` alrededor de lo guardado, de noche. `halo()` calca lo que pinta el
     * compartimento, lo aclara y lo corre unos 2.5 dp: sobre la grada en penumbra se leía como un recorte pálido, y un aro
     * oscuro encima (la solución de Helénico) dejaba otro contorno, oscuro. Aquí el aro toma **el color que tiene detrás**
     * ([fondoNoche]), por tramos de un sexto del ancho y por zonas (cara, franja, huella): en el dibujo el aura en reposo
     * desaparece sin dejar borde. Va en una capa compuesta con `SrcAtop`, así que en la capa del aura no agrega nada (al
     * tocarlo, el aura crece más allá del aro y se sigue viendo). Se pinta antes que lo guardado, que tapa la mitad de dentro.
     */
    private fun DrawScope.cubreAura(c: EstadoCajon, oscuro: Boolean, rellenos: List<Path>) {
        if (!oscuro || c.cols != cols) return
        val aw = anchoH()
        val W = size.width / aw
        val hx = lado + c.col * (aw + hueco)
        val hy = corona + hueco + c.fila * (altoH() + hueco)
        val ancho = 3.3f * density
        val abajo = bordeCara(c.fila + 1)
        val yBanda = (abajo - minOf((abajo - bordeCara(c.fila)) * 0.3f, 0.06f) - hy) * W
        val yHuella = (abajo - hueco * 0.6f - hy) * W
        val zonas = listOf(-size.height to yBanda, yBanda to yHuella, yHuella to size.height * 2)
        val trazo = Stroke(ancho * 2, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        drawIntoCanvas { it.saveLayer(Rect(-ancho * 4, -ancho * 4, size.width + ancho * 4, size.height + ancho * 4), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        val tramos = 6
        for (t in 0 until tramos) {
            val xa = if (t == 0) -ancho * 4 else size.width * t / tramos
            val xb = if (t == tramos - 1) size.width + ancho * 4 else size.width * (t + 1) / tramos
            val xm = hx + (size.width * (t + 0.5f) / tramos) / W
            for ((ya, yb) in zonas) {
                if (yb <= ya) continue
                val ym = hy + ((maxOf(ya, -ancho * 4) + minOf(yb, size.height + ancho * 4)) / 2) / W
                val col = fondoNoche(xm, ym, c.fila, c.filas)
                clipRect(xa, ya, xb, yb) { rellenos.forEach { drawPath(it, col, style = trazo, blendMode = BlendMode.Src) } }
            }
        }
        // La sombra que la cara de arriba deja al fondo de la huella (como en [mueble]).
        clipRect(-ancho * 4, yHuella, size.width + ancho * 4, yHuella + hueco * 0.6f * W * 0.3f) {
            rellenos.forEach { drawPath(it, sombra(true, 0.18f), style = trazo) }
        }
        drawIntoCanvas { it.restore() }
    }

    /** Un desconchón del estuco: la caliza gris que asoma, con el canto del estuco en sombra abajo y a la luz arriba. */
    private fun DrawScope.desconchon(o: Offset, r: Float, semilla: Int, oscuro: Boolean, k: Float) {
        val p = mancho(o, r * 1.5f, r, semilla, 9)
        drawPath(p, n(PIEDRA, oscuro, k))
        clipPath(p) {
            for (j in 0 until 5) drawCircle(n(mezcla(PIEDRA, NEGRO, 0.3f), oscuro, k), r * (0.06f + 0.06f * azar(semilla, j + 40)),
                Offset(o.x + r * 1.6f * (azar(semilla, j + 41) - 0.5f) * 2f, o.y + r * (azar(semilla, j + 42) - 0.5f) * 1.6f))
        }
        drawPath(borde(p, -r * 0.18f, -r * 0.2f), sombra(oscuro, 0.45f))
        drawPath(borde(p, r * 0.14f, r * 0.16f), brillo(oscuro, 0.5f, k))
    }

    /** El contorno de cada hoja de [hojas] (un rectángulo girado como ella), para el aro que tapa el aura. */
    private fun hojasContorno(x0: Float, x1: Float, arriba: Float, abajo: Float, cuantas: Int, semilla: Int): List<Path> {
        if (cuantas <= 0) return emptyList()
        val ancho = (x1 - x0) / (cuantas * 0.62f + 0.5f)
        return List(cuantas) { i ->
            val f = if (cuantas == 1) 0.5f else i / (cuantas - 1f)
            val cx = x0 + ancho * 0.5f + (x1 - x0 - ancho) * f + (azar(semilla, i) - 0.5f) * ancho * 0.25f
            val top = arriba + (abajo - arriba) * 0.35f * azar(semilla, i + 10)
            val giro = Math.toRadians(((azar(semilla, i + 20) - 0.5f) * 14f).toDouble())
            val co = cos(giro).toFloat(); val si = sin(giro).toFloat()
            fun g(x: Float, y: Float): Offset { val dx = x - cx; val dy = y - abajo; return Offset(cx + dx * co - dy * si, abajo + dx * si + dy * co) }
            val a = g(cx - ancho / 2, top); val b = g(cx + ancho / 2, top); val c = g(cx + ancho / 2, abajo); val d = g(cx - ancho / 2, abajo)
            poli(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y)
        }
    }

    /** Las hojas de amate dobladas, de pie y un poco ladeadas, que asoman de un recipiente: [cuantas], de [x0] a [x1], subiendo hasta [arriba]. */
    private fun DrawScope.hojas(x0: Float, x1: Float, arriba: Float, abajo: Float, cuantas: Int, semilla: Int, oscuro: Boolean, k: Float, dia: Float) {
        val u = density
        if (cuantas <= 0) return
        val ancho = (x1 - x0) / (cuantas * 0.62f + 0.5f)
        for (i in 0 until cuantas) {
            val f = if (cuantas == 1) 0.5f else i / (cuantas - 1f)
            val cx = x0 + ancho * 0.5f + (x1 - x0 - ancho) * f + (azar(semilla, i) - 0.5f) * ancho * 0.25f
            val top = arriba + (abajo - arriba) * 0.35f * azar(semilla, i + 10)
            val giro = (azar(semilla, i + 20) - 0.5f) * 14f
            rotate(giro, Offset(cx, abajo)) {
                val r = Rect(cx - ancho / 2, top, cx + ancho / 2, abajo)
                val hoja = contornoIrregular(r, semilla + i, 12, 0.02f)
                drawPath(corrido(hoja, ancho * 0.12f, ancho * 0.05f), sombra(oscuro, 0.3f))
                drawPath(hoja, n(if (i % 2 == 0) AMATE_CLARO else mezcla(AMATE, AMATE_CLARO, 0.45f), oscuro, k, dia))
                clipPath(hoja) {
                    // El doblez: la mitad derecha un poco en sombra; el filete rojo de la página arriba.
                    drawRect(sombra(oscuro, 0.16f), Offset(cx + ancho * 0.08f, top), Size(ancho, abajo - top))
                    drawLine(n(CINABRIO, oscuro, k).copy(alpha = 0.8f), Offset(r.left + ancho * 0.12f, top + 2.2f * u), Offset(r.right - ancho * 0.12f, top + 2.2f * u), 0.9f * u)
                    drawLine(n(AMATE_OSC, oscuro, k).copy(alpha = 0.45f), Offset(r.left + ancho * 0.2f, top + ancho * 0.5f), Offset(r.left + ancho * 0.6f, top + ancho * 0.55f), 0.6f * u)
                    drawRect(brillo(oscuro, 0.35f, k), Offset(r.left, top), Size(ancho, 1f * u))
                }
            }
        }
    }

    /** El tejido de palma, con relieve: tiras claras en un sentido, oscuras en el otro, recortado a [forma]. Pocos trazos. */
    private fun DrawScope.trenzado(forma: Path, r: Rect, celda: Float, oscuro: Boolean, k: Float, dia: Float, semilla: Int) {
        val u = density
        drawPath(forma, n(PALMA, oscuro, k, dia))
        clipPath(forma) {
            var o = -r.height; var j = 0
            while (o < r.width && j < 40) {
                val a = Offset(r.left + o, r.top); val b = Offset(r.left + o + r.height, r.bottom)
                val a2 = Offset(r.left + o + r.height, r.top); val b2 = Offset(r.left + o, r.bottom)
                drawLine(n(PALMA_LUZ, oscuro, k, dia).copy(alpha = 0.5f + 0.25f * azar(semilla, j)), a, b, celda * 0.55f)
                drawLine(n(PALMA_OSC, oscuro, k).copy(alpha = 0.32f + 0.15f * azar(semilla, j + 50)), a2, b2, celda * 0.35f)
                drawLine(n(PALMA_OSC, oscuro, k).copy(alpha = 0.55f), Offset(a.x + celda * 0.32f, a.y), Offset(b.x + celda * 0.32f, b.y), 0.6f * u)
                o += celda; j++
            }
        }
    }

    // ---------------------------------------------------------------- el mueble: el muro y la banqueta

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.bottom
        val sep = d.separacion
        val nf = d.filas.size
        val td = sep * 0.6f
        val td0 = w * 0.03f
        val piso = d.base.top + w * ALTO_PISO
        val brasa = Offset(w * 0.8f, piso + w * 0.012f)
        val copalP = Offset(w * COPAL_X, ch - w * 0.15f)
        fun k(p: Offset) = if (oscuro) calor(Offset(p.x / w, p.y / w), nf) else 0f
        fun c(col: Color, p: Offset, dia: Float = 0f) = n(col, oscuro, k(p), dia)
        val zoc = ch - w * 0.07f

        /** Un paño del muro: de día, de un color; de noche, cada tramo según lo cerca que esté de la lumbre. */
        fun pano(r: Rect, col: Color) {
            if (oscuro) drawRect(Brush.horizontalGradient(List(5) { i -> c(col, Offset(r.left + r.width * i / 4f, r.center.y)) }, r.left, r.right), r.topLeft, r.size)
            else drawRect(col, r.topLeft, r.size)
        }
        // ---- el muro: el paño ocre claro
        pano(Rect(0f, 0f, w, zoc), PANO)
        val muro = Rect(0f, 0f, w, zoc)
        pinceladas(muro, c(mezcla(PANO, OCRE, 0.6f), Offset(w * 0.5f, zoc * 0.5f)), 901, 60, u, 0.12f)
        pinceladas(muro, c(mezcla(PANO, LUZ_MUEBLE, 0.5f), Offset(w * 0.5f, zoc * 0.5f)), 902, 45, u, 0.13f)
        pinceladas(muro, c(mezcla(PANO, SOMBRA_MUEBLE, 0.3f), Offset(w * 0.5f, zoc * 0.5f)), 903, 30, u, 0.07f)
        // La pintura repasada: dos parches de otro ocre, con el borde de la brocha.
        listOf(Offset(w * 0.36f, w * 0.2f), Offset(w * 0.7f, w * 0.27f)).forEachIndexed { i, o ->
            drawPath(mancho(o, w * 0.07f, w * 0.035f, 910 + i, 11), c(mezcla(PANO, OCRE, 0.25f + 0.2f * i), o).copy(alpha = 0.5f))
        }

        // ---- el zócalo de cinabrio, que baja detrás de la banqueta hasta el piso
        val zr = Rect(0f, zoc, w, piso)
        pano(zr, OXIDO)
        pinceladas(zr, c(mezcla(OXIDO, OCRE, 0.35f), Offset(w * 0.5f, zoc)), 904, 70, u, 0.12f)
        pinceladas(zr, c(mezcla(CINABRIO, NEGRO, 0.4f), Offset(w * 0.5f, zoc)), 905, 40, u, 0.1f)
        // La orilla del zócalo, pintada a mano, con su filete de negro de humo.
        for (j in 0 until 30) {
            val x = w * j / 29f
            drawOval(c(OXIDO, Offset(x, zoc)), Offset(x - w * 0.024f, zoc - (0.4f + 1.6f * azar(906, j)) * u), Size(w * 0.048f, 3f * u))
        }
        drawRect(c(NEGRO, Offset(w * 0.5f, zoc)).copy(alpha = 0.85f), Offset(0f, zoc - 3.2f * u), Size(w, 1.4f * u))

        // ---- la banda de grecas escalonadas, arriba
        val g0 = w * 0.03f; val g1 = w * 0.125f
        val pg = Offset(w * 0.5f, (g0 + g1) / 2)
        pano(Rect(0f, g0, w, g1), ESTUCO)
        pinceladas(Rect(0f, g0, w, g1), c(mezcla(ESTUCO, OCRE, 0.5f), pg), 907, 26, u, 0.14f)
        val gh = (g1 - g0) * 0.78f
        var gx = -gh * 0.4f; var gk = 0
        while (gx < w) {
            val o = Offset(gx, g0 + (g1 - g0) * 0.11f)
            // El escalón que sube y el gancho cuadrado que se enrosca (xicalcoliuhqui).
            val greca = Path().apply {
                moveTo(o.x, o.y + gh); lineTo(o.x, o.y + gh * 0.66f); lineTo(o.x + gh * 0.22f, o.y + gh * 0.66f); lineTo(o.x + gh * 0.22f, o.y + gh * 0.33f)
                lineTo(o.x + gh * 0.44f, o.y + gh * 0.33f); lineTo(o.x + gh * 0.44f, o.y); lineTo(o.x + gh * 1.12f, o.y); lineTo(o.x + gh * 1.12f, o.y + gh * 0.62f)
                lineTo(o.x + gh * 0.78f, o.y + gh * 0.62f); lineTo(o.x + gh * 0.78f, o.y + gh * 0.32f)
            }
            val col = if (gk % 2 == 0) CINABRIO else AZUL
            drawPath(greca, c(col, o), style = Stroke(gh * 0.17f))
            // Un tramo repasado: el color de encima no cubre del todo el de abajo.
            if (gk % 5 == 3) drawPath(corrido(greca, 0.8f * u, 0.6f * u), c(mezcla(col, ESTUCO, 0.45f), o).copy(alpha = 0.55f), style = Stroke(gh * 0.08f))
            gx += gh * 1.25f; gk++
        }
        // Los filetes de negro de humo que encierran la banda, y el azul de debajo.
        drawRect(c(NEGRO, pg), Offset(0f, g0 - 1.6f * u), Size(w, 2.2f * u))
        drawRect(c(NEGRO, pg), Offset(0f, g1 - 0.6f * u), Size(w, 1.8f * u))
        drawRect(c(AZUL, pg), Offset(0f, g1 + 2.6f * u), Size(w, w * 0.007f))
        drawRect(c(mezcla(PANO, OCRE, 0.5f), pg), Offset(0f, 0f), Size(w, g0 - 1.6f * u))
        // Un desconchón grande en la banda, que se come una greca.
        desconchon(Offset(w * 0.17f, g1 - w * 0.012f), w * 0.022f, 911, oscuro, k(Offset(w * 0.17f, g1)))

        // ---- la mancha de luz del vano (fuera del cuadro, arriba a la izquierda)
        if (!oscuro) { mancha(Offset(w * 0.02f, w * 0.1f), w * 0.9f, luzMueble(0.45f)); mancha(Offset(w * 0.0f, w * 0.0f), w * 0.4f, luzMueble(0.35f)) }

        // ---- desconchones y grietas en el paño, y el hollín del copal
        desconchon(Offset(w * 0.055f, w * 0.31f), w * 0.02f, 912, oscuro, k(Offset(w * 0.05f, w * 0.3f)))
        desconchon(Offset(w * 0.6f, w * 0.17f), w * 0.012f, 913, oscuro, k(Offset(w * 0.6f, w * 0.17f)))
        fun grieta(desde: Offset, largo: Float, semilla: Int) {
            val g = Path().apply { moveTo(desde.x, desde.y) }
            var p = desde
            for (j in 0 until 6) { p += Offset((azar(semilla, j) - 0.4f) * largo * 0.4f, largo / 6f); g.lineTo(p.x, p.y) }
            drawPath(g, sombra(oscuro, 0.3f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            drawPath(corrido(g, -0.8f * u, 0f), brillo(oscuro, 0.25f, k(desde)), style = Stroke(0.5f * u))
        }
        grieta(Offset(w * 0.47f, g1 + w * 0.012f), w * 0.09f, 921)
        grieta(Offset(w * 0.975f, zoc + w * 0.05f), w * 0.12f, 922)
        for (j in 0 until 8) {
            val r = w * (0.008f + 0.006f * j)
            val y = copalP.y - w * (0.03f + 0.03f * j)
            drawOval(sombra(oscuro, 0.045f), Offset(copalP.x - r + (j % 3 - 1) * 1.2f * u, y - r * 1.4f), Size(r * 2f, r * 2.8f))
        }

        // ---- la banqueta en gradas: el borde de arriba de cada cara, de la más alta (0) a la del pie (nf)
        val ys = listOf(ch) + d.filas.map { it.bottom }
        fun x0(j: Int) = w * minOf(0.008f + 0.012f * (nf - j), 0.058f)
        fun fondo(j: Int) = if (j < nf) ys[j + 1] else piso
        fun dia(j: Int) = if (oscuro) 0f else 0.07f * (1f - j / maxOf(1f, nf.toFloat()))
        // La sombra de cada grada en el muro, a su derecha (de día) o hacia fuera de la lumbre (de noche).
        for (j in 0..nf) {
            val arriba = ys[j] - (if (j == 0) td0 else td)
            if (!oscuro) drawRect(sombra(false, 0.3f), Offset(w - x0(j), arriba + w * 0.012f), Size(w * 0.014f, fondo(j) - arriba - w * 0.006f))
            else {
                val kk = k(Offset(x0(j), (arriba + fondo(j)) / 2))
                if (kk > 0.05f) drawRect(sombra(true, 0.6f * kk), Offset(x0(j) - w * 0.018f, arriba - w * 0.006f), Size(w * 0.018f, fondo(j) - arriba))
            }
        }
        // Las caras de almagre: de noche, más encendidas del lado de la lumbre.
        for (j in 0..nf) {
            val r = Rect(x0(j), ys[j], w - x0(j), fondo(j))
            val yc = r.center.y
            if (oscuro) drawRect(Brush.horizontalGradient(List(5) { i -> c(ALMAGRE, Offset(r.left + r.width * i / 4f, yc)) }, r.left, r.right), r.topLeft, r.size)
            else drawRect(n(ALMAGRE, false, 0f, dia(j)), r.topLeft, r.size)
            pinceladas(r, c(mezcla(ALMAGRE, ESTUCO, 0.45f), Offset(w * 0.5f, yc)), 930 + j, 34, u, 0.14f)
            pinceladas(r, c(mezcla(ALMAGRE, CINABRIO, 0.7f), Offset(w * 0.5f, yc)), 940 + j, 24, u, 0.14f)
            // El estuco gastado: donde se fue el almagre asoma el crema, sobre todo junto al canto de arriba (donde se apoyan
            // las manos) y abajo, donde rozan los recipientes. De noche, más tenue (no compite con lo guardado).
            val gasto = if (oscuro) 0.25f else 0.7f
            for (q in 0 until 7) {
                val arriba = q < 4
                val o = Offset(r.left + r.width * (0.04f + 0.92f * azar(950 + j, q)),
                    if (arriba) r.top + w * (0.033f + 0.008f * azar(951 + j, q)) else r.bottom - w * (0.066f + 0.008f * azar(951 + j, q)))
                val parche = mancho(o, w * (0.02f + 0.025f * azar(952 + j, q)), w * (0.004f + 0.004f * azar(954 + j, q)), 953 + j * 7 + q, 10)
                drawPath(parche, c(mezcla(ESTUCO, ALMAGRE, 0.18f), o).copy(alpha = gasto))
                drawPath(borde(parche, w * 0.003f, w * 0.002f), c(mezcla(ALMAGRE, CINABRIO, 0.4f), o).copy(alpha = gasto * 0.6f))
            }
            if (j % 2 == 1) desconchon(Offset(r.left + r.width * (0.1f + 0.8f * azar(960, j)), r.bottom - w * 0.02f), w * 0.011f, 961 + j, oscuro, k(r.center))
            // Los cantos: el de la izquierda a la luz, el de la derecha en sombra; la sombra del canto de la huella de encima.
            drawRect(brillo(oscuro, 0.35f, k(r.topLeft)), r.topLeft, Size(1.4f * u, r.height))
            drawRect(sombra(oscuro, 0.3f), Offset(r.right - 1.8f * u, r.top), Size(1.8f * u, r.height))
            drawRect(sombra(oscuro, if (oscuro) 0.5f else 0.32f), r.topLeft, Size(r.width, w * 0.016f))
            drawRect(sombra(oscuro, 0.12f), Offset(r.left, r.top + w * 0.016f), Size(r.width, w * 0.014f))
            // Abajo, una franja de cinabrio más hondo con su filete negro, como en los zócalos pintados.
            val fr = Rect(r.left, r.bottom - minOf(r.height * 0.3f, w * 0.06f), r.right, r.bottom)
            drawRect(c(FRANJA, fr.center), fr.topLeft, fr.size)
            pinceladas(fr, c(mezcla(FRANJA, ALMAGRE, 0.4f), fr.center), 945 + j, 10, u, 0.14f)
            drawRect(c(NEGRO, fr.center).copy(alpha = 0.6f), Offset(fr.left, fr.top - 0.6f * u), Size(fr.width, 1.4f * u))
        }
        // Las huellas de estuco crema, vistas un poco desde arriba, con el canto redondeado y comido.
        for (j in 0..nf) {
            val tdj = if (j == 0) td0 else td
            val y = ys[j]
            val r = Rect(x0(j) - w * 0.004f, y - tdj, w - x0(j) + w * 0.004f, y)
            val yc = r.center.y
            if (oscuro) drawRect(Brush.horizontalGradient(List(5) { i -> c(ESTUCO, Offset(r.left + r.width * i / 4f, yc)) }, r.left, r.right), r.topLeft, r.size)
            else drawRect(n(mezcla(ESTUCO, PANO, 0.3f), false, 0f, dia(j)), r.topLeft, r.size)
            pinceladas(r, c(mezcla(ESTUCO, PIEDRA, 0.5f), Offset(w * 0.5f, yc)), 970 + j, 16, u, 0.14f)
            // La huella se oscurece hacia el fondo (la cara de la grada de arriba le hace sombra).
            drawRect(sombra(oscuro, 0.18f), r.topLeft, Size(r.width, tdj * 0.3f))
            // El canto de la huella: una moldura de estuco crema, con la luz arriba y mellas donde se comió.
            val canto = Rect(r.left, y, r.right, y + w * 0.012f)
            if (oscuro) drawRect(Brush.horizontalGradient(List(5) { i -> c(mezcla(ESTUCO, PANO, 0.5f), Offset(canto.left + canto.width * i / 4f, y)) }, canto.left, canto.right), canto.topLeft, canto.size)
            else drawRect(n(mezcla(ESTUCO, PANO, 0.55f), false, 0f, dia(j)), canto.topLeft, canto.size)
            drawRect(brillo(oscuro, 0.6f, k(Offset(w * 0.7f, y))), Offset(r.left, y - 1.2f * u), Size(r.width, 1.4f * u))
            drawRect(sombra(oscuro, 0.3f), Offset(r.left, canto.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            for (q in 0 until 3) {
                val xq = r.left + r.width * (0.05f + 0.9f * azar(980 + j, q))
                val p = mancho(Offset(xq, canto.bottom - w * 0.003f), w * (0.008f + 0.008f * azar(981 + j, q)), w * 0.005f, 982 + j * 5 + q, 7)
                drawPath(p, c(PIEDRA, Offset(xq, y)))
                drawPath(borde(p, 0f, 1.2f * u), sombra(oscuro, 0.35f))
            }
        }

        // ---- el piso de estuco, gastado
        val suelo = mezcla(ESTUCO, PIEDRA, 0.4f)
        drawRect(c(suelo, Offset(w * 0.6f, piso + w * 0.05f)), Offset(0f, piso), Size(w, alto - piso))
        pinceladas(Rect(0f, piso, w, alto), c(mezcla(suelo, SOMBRA_MUEBLE, 0.35f), Offset(w * 0.6f, piso)), 990, 26, u, 0.13f)
        pinceladas(Rect(0f, piso, w, alto), c(mezcla(suelo, LUZ_MUEBLE, 0.5f), Offset(w * 0.6f, piso)), 991, 16, u, 0.12f)
        drawRect(sombra(oscuro, 0.42f), Offset(0f, piso), Size(w, 1.6f * u))
        drawRect(sombra(oscuro, 0.14f), Offset(0f, piso + 1.6f * u), Size(w, 5f * u))

        // ---- la noche: el halo de la tea en el muro de arriba y el de las brasas abajo. Sobre las caras de las gradas no
        // va halo: su luz ya va en su color (el degradado de cada cara), y así el compartimento puede saber qué color tiene
        // detrás (ver [cubreAura]).
        val teaP = Offset(w * tea.x, w * tea.y)
        if (oscuro) {
            clipRect(0f, 0f, w, ch) {
                mancha(teaP, w * 1.0f, BRASA.copy(alpha = 0.16f))
                mancha(teaP, w * 0.5f, Color(0xFFFF7A3A).copy(alpha = 0.2f))
                mancha(teaP, w * 0.18f, BRASA_VIVA.copy(alpha = 0.34f))
                mancha(copalP, w * 0.12f, BRASA_VIVA.copy(alpha = 0.16f))
            }
            clipRect(0f, ys[nf] - td, w, alto) {
                mancha(brasa, w * 0.95f, BRASA.copy(alpha = 0.12f))
                mancha(brasa, w * 0.48f, BRASA.copy(alpha = 0.2f))
                mancha(brasa, w * 0.2f, BRASA_VIVA.copy(alpha = 0.3f))
            }
        }

        // ---- lo del escriba, en la grada alta y colgado
        val apoyo = ch - td0 * 0.45f
        fun haciaX(x: Float, y: Float): Float = if (luzMayor(Offset(x / w, y / w), nf).x * w >= x) 1f else -1f
        /** Hacia dónde cae la sombra de lo que está en [p]: de día, a la derecha y abajo; de noche, lejos de la luz que manda. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 0.8f, largo * 0.6f)
            val l = luzMayor(Offset(p.x / w, p.y / w), nf) * w
            val v = p - l
            return v / v.getDistance().coerceAtLeast(1f) * (largo * 1.6f)
        }
        val pCollar = Offset(w * 0.925f, w * 0.29f)
        collar(Offset(w * 0.925f, w * 0.19f), w, oscuro, k(pCollar), caida(pCollar, w * 0.015f))
        val pRed = Offset(w * 0.4f, w * 0.24f)
        red(Offset(w * 0.4f, w * 0.165f), w, oscuro, k(pRed), caida(pRed, w * 0.015f))
        val pPlumas = Offset(w * 0.1f, apoyo - w * 0.2f)
        plumas(Offset(w * 0.13f, apoyo - td0 * 0.5f), w, oscuro, k(pPlumas), caida(pPlumas, w * 0.016f))
        val pCodice = Offset(w * 0.33f, apoyo - w * 0.1f)
        codice(w * 0.13f, w * 0.52f, apoyo, w * 0.205f, w, oscuro, k(pCodice), caida(pCodice, w * 0.022f))
        val pVaso = Offset(w * 0.585f, apoyo - w * 0.07f)
        vasoPolicromo(Offset(w * 0.585f, apoyo - td0 * 0.3f), w * 0.088f, w * 0.15f, w, oscuro, k(pVaso), haciaX(w * 0.585f, apoyo), caida(pVaso, w * 0.02f))
        teaOcote(Offset(w * TEA_X, apoyo - td0 * 0.55f), w, oscuro, k(teaP + Offset(0f, w * 0.1f)))
        val pInc = Offset(w * COPAL_X, apoyo - w * 0.06f)
        incensario(Offset(w * COPAL_X, apoyo - td0 * 0.3f), w * 0.15f, w, oscuro, k(pInc), haciaX(w * COPAL_X, apoyo), caida(pInc, w * 0.02f))
        conchas(apoyo + td0 * 0.25f, w, oscuro, k(Offset(w * 0.68f, apoyo)))
        // La llama de la tea, encima de lo suyo, con su propio color.
        if (oscuro) llama(teaP, w)

        // ---- al pie: el petate enrollado, la placa (o un cajete caído) y el brasero
        val pieSuelo = piso + w * 0.105f
        petate(pieSuelo, w, oscuro, k(Offset(w * 0.15f, piso + w * 0.05f)))
        if (d.mas > 0) placa.let { pl -> placaAmate(d.enBase(pl), oscuro, k(d.enBase(pl).center)) }
        else cajete(Offset(w * 0.47f, piso + w * 0.1f), w * 0.07f, oscuro, k(Offset(w * 0.47f, piso + w * 0.08f)))
        braseroTripode(Offset(w * 0.8f, pieSuelo + w * 0.01f), w * 0.2f, w * 0.1f, w, oscuro)

        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- lo del escriba

    /**
     * El códice plegado en biombo, de pie y medio abierto, de [x0] a [x1] sobre [pie]: la tapa de piel de jaguar de delante
     * (algo vuelta), el bloque de hojas aún plegadas y seis pliegues abiertos en zigzag, cada uno con su luz o su sombra, sus
     * filetes rojos, glifos inventados y números de puntos y barras; al final, la otra tapa de canto.
     */
    private fun DrawScope.codice(x0: Float, x1: Float, pie: Float, alto: Float, w: Float, oscuro: Boolean, k: Float, caida: Offset) {
        val u = density
        val top = pie - alto
        val tapa = (x1 - x0) * 0.15f
        val xa = x0 + tapa * 1.3f
        val xb = x1 - (x1 - x0) * 0.045f
        val np = 6
        val dy = alto * 0.075f
        // Los pliegues: los que miran a la luz (a la izquierda) son algo más anchos.
        val pesos = FloatArray(np) { if (it % 2 == 0) 1.18f else 0.82f }
        val xs = FloatArray(np + 1); xs[0] = xa
        for (i in 0 until np) xs[i + 1] = xs[i] + (xb - xa) * pesos[i] / pesos.sum()
        fun off(i: Int) = if (i % 2 == 0) 0f else dy
        val silueta = Path().apply {
            moveTo(x0, top - alto * 0.04f); lineTo(xa, top)
            for (i in 0..np) lineTo(xs[i], top + off(i))
            lineTo(x1, top + dy * 0.4f); lineTo(x1, pie - dy * 0.2f)
            for (i in np downTo 0) lineTo(xs[i], pie - dy + off(i))
            lineTo(x0, pie - dy * 0.6f); close()
        }
        // Su sombra en el muro: de día, a la derecha y abajo; de noche, hacia fuera del copal (a la izquierda).
        clipRect(0f, 0f, w, pie - w * 0.012f) {
            drawPath(corrido(silueta, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.18f + 0.32f * k else 0.24f))
        }
        drawOval(sombra(oscuro, 0.35f), Offset(x0 + w * 0.01f, pie - w * 0.01f), Size(x1 - x0 + w * 0.015f, w * 0.016f))
        // Los pliegues abiertos.
        for (i in 0 until np) {
            val p = poli(xs[i], top + off(i), xs[i + 1], top + off(i + 1), xs[i + 1], pie - dy + off(i + 1), xs[i], pie - dy + off(i))
            val luzP = if (oscuro) off(i + 1) < off(i) else off(i + 1) > off(i)
            drawPath(p, n(if (luzP) ESTUCO else mezcla(ESTUCO, PANO, 0.4f), oscuro, k))
            val xl = xs[i]; val xr = xs[i + 1]; val pw = xr - xl
            fun yA(f: Float, v: Float) = top + off(i) + (off(i + 1) - off(i)) * f + (alto - dy) * v
            clipPath(p) {
                // Los filetes rojos de arriba y de abajo, y la línea que parte la página.
                listOf(0.09f, 0.88f).forEach { v -> drawPath(Path().apply { moveTo(xl, yA(0f, v)); lineTo(xr, yA(1f, v)) }, n(CINABRIO, oscuro, k), style = Stroke(1.3f * u)) }
                drawPath(Path().apply { moveTo(xl, yA(0f, 0.5f)); lineTo(xr, yA(1f, 0.5f)) }, n(CINABRIO, oscuro, k).copy(alpha = 0.7f), style = Stroke(0.8f * u))
                val cx = xl + pw * 0.5f
                when (i % 3) {
                    0 -> {
                        // Un cartucho de glifo: el marco, dos ojos y una voluta; abajo, un campo azul.
                        val g = Rect(cx - pw * 0.3f, yA(0.5f, 0.17f), cx + pw * 0.3f, yA(0.5f, 0.43f))
                        drawRoundRect(n(OCRE, oscuro, k), g.topLeft, g.size, CornerRadius(pw * 0.12f))
                        drawRoundRect(n(NEGRO, oscuro, k), g.topLeft, g.size, CornerRadius(pw * 0.12f), style = Stroke(1f * u))
                        // Un signo inventado: una voluta que se enrosca en una esquina y tres rayas en la otra.
                        drawArc(n(NEGRO, oscuro, k), 90f, 270f, false, Offset(g.left + g.width * 0.14f, g.top + g.height * 0.16f), Size(g.width * 0.42f, g.height * 0.42f), style = Stroke(1.1f * u))
                        drawCircle(n(CINABRIO, oscuro, k), pw * 0.04f, Offset(g.left + g.width * 0.35f, g.top + g.height * 0.37f))
                        for (q in 0 until 3) drawLine(n(NEGRO, oscuro, k), Offset(g.left + g.width * 0.62f, g.top + g.height * (0.3f + 0.18f * q)), Offset(g.left + g.width * 0.86f, g.top + g.height * (0.3f + 0.18f * q)), 0.9f * u)
                        drawLine(n(NEGRO, oscuro, k), Offset(g.left + g.width * 0.15f, g.top + g.height * 0.75f), Offset(g.left + g.width * 0.55f, g.top + g.height * 0.75f), 0.9f * u)
                        drawRect(n(AZUL, oscuro, k), Offset(xl, yA(0.5f, 0.56f)), Size(pw, (alto - dy) * 0.24f))
                        for (q in 0 until 3) drawCircle(n(ESTUCO, oscuro, k), pw * 0.045f, Offset(xl + pw * (0.25f + 0.25f * q), yA(0.5f, 0.68f)))
                    }
                    1 -> {
                        // Un número: dos barras y tres puntos; abajo, un glifo pequeño en rojo.
                        listOf(0.27f, 0.36f).forEach { v -> drawRoundRect(n(NEGRO, oscuro, k), Offset(cx - pw * 0.32f, yA(0.5f, v)), Size(pw * 0.64f, (alto - dy) * 0.045f), CornerRadius(pw * 0.05f)) }
                        for (q in 0 until 3) drawCircle(n(NEGRO, oscuro, k), pw * 0.07f, Offset(cx + pw * (q - 1) * 0.24f, yA(0.5f, 0.19f)))
                        val g = Rect(cx - pw * 0.26f, yA(0.5f, 0.58f), cx + pw * 0.26f, yA(0.5f, 0.8f))
                        drawRoundRect(n(CINABRIO, oscuro, k), g.topLeft, g.size, CornerRadius(pw * 0.1f))
                        drawLine(n(ESTUCO, oscuro, k), Offset(g.left + g.width * 0.25f, g.center.y), Offset(g.right - g.width * 0.2f, g.center.y), 1f * u)
                    }
                    else -> {
                        // Un campo de color con una franja de ocre: la escena pintada, sin figura.
                        drawRect(n(CINABRIO, oscuro, k), Offset(xl, yA(0.5f, 0.13f)), Size(pw, (alto - dy) * 0.33f))
                        drawRect(n(OCRE, oscuro, k), Offset(xl + pw * 0.2f, yA(0.5f, 0.18f)), Size(pw * 0.6f, (alto - dy) * 0.12f))
                        drawCircle(n(JADE, oscuro, k), pw * 0.1f, Offset(cx, yA(0.5f, 0.66f)))
                        drawCircle(n(NEGRO, oscuro, k), pw * 0.1f, Offset(cx, yA(0.5f, 0.66f)), style = Stroke(0.8f * u))
                    }
                }
                drawPath(p, if (luzP) brillo(oscuro, 0.18f, k) else sombra(oscuro, 0.22f))
            }
            drawPath(p, sombra(oscuro, 0.4f), style = Stroke(0.6f * u))
            // El pliegue de delante con su luz; el de atrás, hundido.
            if (i % 2 == 0) drawLine(sombra(oscuro, 0.4f), Offset(xl, top + off(i)), Offset(xl, pie - dy + off(i)), 1.2f * u)
            else drawLine(brillo(oscuro, 0.55f, k), Offset(xl, top + off(i)), Offset(xl, pie - dy + off(i)), 1.1f * u)
        }
        // El bloque de hojas aún plegadas, con sus cantos.
        val bloque = poli(x0 + tapa, top - alto * 0.01f, xa, top, xa, pie - dy, x0 + tapa, pie - dy * 0.5f)
        drawPath(bloque, n(mezcla(ESTUCO, PANO, 0.5f), oscuro, k))
        for (q in 1 until 6) {
            val x = x0 + tapa + (xa - x0 - tapa) * q / 6f
            drawLine(sombra(oscuro, 0.25f), Offset(x, top), Offset(x, pie - dy * 0.7f), 0.6f * u)
        }
        // La tapa de delante: una tabla forrada de piel de jaguar, con las rosetas y el canto de madera.
        val cubierta = poli(x0, top - alto * 0.04f, x0 + tapa, top - alto * 0.01f, x0 + tapa, pie - dy * 0.5f, x0, pie - dy * 0.6f)
        drawPath(corrido(cubierta, tapa * 0.18f, 0f), n(mezcla(NEGRO, BARRO_OSC, 0.4f), oscuro, k))
        drawPath(cubierta, n(JAGUAR, oscuro, k))
        clipPath(cubierta) {
            for (q in 0 until 7) {
                val o = Offset(x0 + tapa * (0.2f + 0.65f * azar(1101, q)), top + alto * (0.06f + 0.84f * q / 6f))
                val rr = tapa * 0.15f
                drawCircle(n(mezcla(JAGUAR, OCRE, 0.6f), oscuro, k), rr * 0.7f, o)
                for (s in 0 until 5) {
                    val a = 6.283f * s / 5 + azar(1102, q)
                    drawOval(n(NEGRO, oscuro, k), Offset(o.x + rr * cos(a) - rr * 0.32f, o.y + rr * sin(a) - rr * 0.22f), Size(rr * 0.64f, rr * 0.44f))
                }
            }
            drawRect(brillo(oscuro, 0.3f, k), Offset(x0, top - alto * 0.05f), Size(tapa * 0.18f, alto * 1.1f))
            drawRect(sombra(oscuro, 0.22f), Offset(x0 + tapa * 0.7f, top - alto * 0.05f), Size(tapa * 0.3f, alto * 1.1f))
        }
        drawPath(cubierta, n(BARRO_OSC, oscuro, k).copy(alpha = 0.7f), style = Stroke(0.9f * u))
        // La otra tapa, de canto al final.
        val atras = poli(xb, top + off(np), x1, top + dy * 0.4f, x1, pie - dy * 0.2f, xb, pie - dy + off(np))
        drawPath(atras, n(mezcla(JAGUAR, SOMBRA_MUEBLE, 0.35f), oscuro, k))
        clipPath(atras) { for (q in 0 until 4) drawCircle(n(NEGRO, oscuro, k), (x1 - xb) * 0.18f, Offset(xb + (x1 - xb) * 0.5f, top + alto * (0.2f + 0.2f * q))) }
        // El cordón de la tapa, con su cuenta de jade colgando.
        val nudo = Offset(x0 + tapa * 0.55f, top + alto * 0.5f)
        val cuenta = Offset(x0 + tapa * 0.3f, pie - alto * 0.12f)
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x - tapa * 0.4f, nudo.y + alto * 0.15f, cuenta.x + tapa * 0.2f, cuenta.y - alto * 0.15f, cuenta.x, cuenta.y) }, n(HENEQUEN, oscuro, k), style = Stroke(1.1f * u))
        drawCircle(sombra(oscuro, 0.35f), w * 0.009f, cuenta + Offset(1.5f * u, 1.5f * u))
        drawCircle(n(JADE, oscuro, k), w * 0.009f, cuenta)
        drawCircle(if (oscuro) BRASA_VIVA.copy(alpha = 0.5f * k) else luzMueble(0.6f), w * 0.003f, cuenta - Offset(w * 0.003f, w * 0.003f))
    }

    /** Un punto de la curva de Bézier cúbica a, b, c, d en [t]. */
    private fun bz(a: Offset, b: Offset, c: Offset, d: Offset, t: Float): Offset {
        val m = 1f - t
        return a * (m * m * m) + b * (3f * m * m * t) + c * (3f * m * t * t) + d * (t * t * t)
    }

    /**
     * El abanico de plumas, apoyado contra el muro: el mango de madera con su amarre rojo y, arriba, la rueda de plumas de
     * quetzal (dos coronas, la de atrás más larga y oscura) con las puntas tornasol; su sombra en el estuco.
     */
    private fun DrawScope.plumas(pie: Offset, w: Float, oscuro: Boolean, k: Float, caida: Offset) {
        val u = density
        val centro = Offset(pie.x - w * 0.04f, pie.y - w * 0.215f)
        val eje = (centro - pie).let { it / it.getDistance() }
        val ang0 = Math.toDegrees(kotlin.math.atan2(eje.y.toDouble(), eje.x.toDouble())).toFloat()
        val sd = caida
        fun pluma(ang: Float, largo: Float, ancho: Float): Path {
            val a = Math.toRadians(ang.toDouble())
            val d = Offset(cos(a).toFloat(), sin(a).toFloat()); val nn = Offset(-d.y, d.x)
            val base = centro + d * (w * 0.012f)
            val tip = centro + d * largo
            return Path().apply {
                moveTo(base.x, base.y)
                val c1 = base + d * (largo * 0.45f) + nn * ancho; val c2 = tip - d * (largo * 0.18f) + nn * (ancho * 1.1f)
                cubicTo(c1.x, c1.y, c2.x, c2.y, tip.x, tip.y)
                val c3 = tip - d * (largo * 0.18f) - nn * (ancho * 1.1f); val c4 = base + d * (largo * 0.45f) - nn * ancho
                cubicTo(c3.x, c3.y, c4.x, c4.y, base.x, base.y)
                close()
            }
        }
        val coronas = listOf(Triple(9, w * 0.15f, true), Triple(8, w * 0.115f, false))
        // Las sombras primero (en el muro), luego las plumas.
        coronas.forEach { (cuantas, largo, atras) ->
            for (q in 0 until cuantas) {
                val ang = ang0 - 85f + 170f * (q + if (atras) 0f else 0.5f) / (cuantas - 1f)
                drawPath(corrido(pluma(ang, largo, w * 0.016f), sd.x, sd.y), sombra(oscuro, if (oscuro) 0.2f + 0.3f * k else 0.15f))
            }
        }
        // El mango, de canto: madera con su luz a la izquierda, y su sombra.
        val mango0 = pie; val mango1 = centro + eje * (w * 0.01f)
        drawLine(sombra(oscuro, 0.3f), mango0 + sd * 0.6f, mango1 + sd * 0.6f, w * 0.014f, StrokeCap.Round)
        drawLine(n(BARRO_OSC, oscuro, k), mango0, mango1, w * 0.014f, StrokeCap.Round)
        drawLine(brillo(oscuro, 0.35f, k), mango0 - Offset(w * 0.003f, 0f), mango1 - Offset(w * 0.003f, 0f), w * 0.004f, StrokeCap.Round)
        coronas.forEach { (cuantas, largo, atras) ->
            for (q in 0 until cuantas) {
                val ang = ang0 - 85f + 170f * (q + if (atras) 0f else 0.5f) / (cuantas - 1f)
                val p = pluma(ang, largo, w * (if (atras) 0.017f else 0.015f))
                val col = if (atras) mezcla(QUETZAL, NEGRO, 0.3f) else if (q % 2 == 0) QUETZAL else mezcla(QUETZAL, AZUL, 0.3f)
                drawPath(p, n(col, oscuro, k))
                clipPath(p) {
                    val a = Math.toRadians(ang.toDouble()); val d = Offset(cos(a).toFloat(), sin(a).toFloat())
                    drawCircle(n(mezcla(QUETZAL, AZUL, 0.6f), oscuro, k), w * 0.022f, centro + d * (largo * 0.95f))
                    drawCircle(n(JADE_LUZ, oscuro, k).copy(alpha = 0.5f), w * 0.008f, centro + d * (largo * 0.72f))
                    drawLine(n(mezcla(QUETZAL, NEGRO, 0.55f), oscuro, k), centro, centro + d * largo, 0.8f * u)
                }
                // Lo que mira arriba a la izquierda, a la luz; lo de abajo a la derecha, en sombra.
                if ((ang < ang0 - 10f) != oscuro) drawPath(borde(p, if (oscuro) -w * 0.004f else w * 0.004f, w * 0.004f), brillo(oscuro, if (oscuro) 0.8f else 0.35f, k))
                else drawPath(p, sombra(oscuro, 0.14f))
            }
        }
        // El amarre del mango: hilo rojo enrollado, y una cuenta de jade.
        val amarre = centro + eje * (w * 0.03f)
        drawCircle(n(CINABRIO, oscuro, k), w * 0.016f, amarre)
        for (q in -1..1) drawLine(n(mezcla(CINABRIO, NEGRO, 0.4f), oscuro, k), amarre + Offset(-w * 0.014f, q * w * 0.006f), amarre + Offset(w * 0.014f, q * w * 0.006f), 0.8f * u)
        drawCircle(brillo(oscuro, 0.4f, k), w * 0.005f, amarre - Offset(w * 0.006f, w * 0.006f))
        drawCircle(n(JADE, oscuro, k), w * 0.008f, centro)
        drawOval(sombra(oscuro, 0.35f), Offset(pie.x - w * 0.012f, pie.y - w * 0.005f), Size(w * 0.04f, w * 0.01f))
    }

    /**
     * Una red de henequén colgada de una estaca, con un calabazo para el agua dentro: el cordel, las mallas en rombo que
     * ciñen la panza y el tapón de olote; su sombra en el estuco.
     */
    private fun DrawScope.red(clavo: Offset, w: Float, oscuro: Boolean, k: Float, caida: Offset) {
        val u = density
        val sd = caida
        val c0 = Offset(clavo.x + w * 0.004f, clavo.y + w * 0.085f)
        val calabazo = Path().apply {
            addOval(Rect(c0.x - w * 0.042f, c0.y - w * 0.03f, c0.x + w * 0.042f, c0.y + w * 0.045f))
            addOval(Rect(c0.x - w * 0.022f, c0.y - w * 0.058f, c0.x + w * 0.022f, c0.y - w * 0.012f))
        }
        drawPath(corrido(calabazo, sd.x, sd.y), sombra(oscuro, if (oscuro) 0.1f + 0.2f * k else 0.2f))
        val cuerdas = listOf(-0.03f, 0.03f).map { dx -> Offset(c0.x + w * dx, c0.y - w * 0.01f) }
        cuerdas.forEach { drawLine(sombra(oscuro, 0.25f), clavo + sd, it + sd, 1.1f * u) }
        cuerdas.forEach { drawLine(n(HENEQUEN, oscuro, k), clavo, it, 1.1f * u) }
        drawPath(calabazo, n(mezcla(OCRE, JADE, 0.25f), oscuro, k))
        volumen(calabazo, oscuro, k, 1f, lustre = 0.3f)
        // El tapón de olote.
        drawRoundRect(n(PALMA_LUZ, oscuro, k), Offset(c0.x - w * 0.007f, c0.y - w * 0.07f), Size(w * 0.014f, w * 0.016f), CornerRadius(w * 0.004f))
        // Las mallas: rombos de cordel que ciñen la panza.
        clipPath(Path().apply { addOval(Rect(c0.x - w * 0.046f, c0.y - w * 0.034f, c0.x + w * 0.046f, c0.y + w * 0.05f)) }) {
            for (q in -4..4) {
                val x = c0.x + q * w * 0.016f
                drawLine(n(mezcla(HENEQUEN, BARRO_OSC, 0.3f), oscuro, k), Offset(x - w * 0.03f, c0.y - w * 0.03f), Offset(x + w * 0.03f, c0.y + w * 0.05f), 0.9f * u)
                drawLine(n(mezcla(HENEQUEN, BARRO_OSC, 0.3f), oscuro, k), Offset(x + w * 0.03f, c0.y - w * 0.03f), Offset(x - w * 0.03f, c0.y + w * 0.05f), 0.9f * u)
            }
        }
        // El nudo de abajo con las puntas, y la estaca.
        drawLine(n(HENEQUEN, oscuro, k), Offset(c0.x, c0.y + w * 0.045f), Offset(c0.x + w * 0.004f, c0.y + w * 0.07f), 1.2f * u, StrokeCap.Round)
        drawCircle(n(HENEQUEN, oscuro, k), 2.2f * u, Offset(c0.x, c0.y + w * 0.046f))
        drawCircle(sombra(oscuro, 0.45f), w * 0.009f, clavo + Offset(w * 0.005f, w * 0.007f))
        drawCircle(n(BARRO_OSC, oscuro, k), w * 0.009f, clavo)
        drawCircle(brillo(oscuro, 0.5f, k), w * 0.003f, clavo - Offset(w * 0.003f, w * 0.003f))
    }

    /** El vaso cilíndrico policromo: engobe crema anaranjado, bandas rojas, una columna de glifos inventados y los pinceles dentro. */
    private fun DrawScope.vasoPolicromo(pie: Offset, ancho: Float, alto: Float, w: Float, oscuro: Boolean, k: Float, haciaX: Float, caida: Offset) {
        val u = density
        val l = pie.x - ancho / 2; val r = pie.x + ancho / 2; val top = pie.y - alto
        val ry = ancho * 0.13f
        // Los pinceles: cañas finas con la punta de pelo oscura, inclinadas.
        listOf(-0.25f to -16f, 0.1f to 9f, 0.3f to 22f).forEachIndexed { i, (fx, ang) ->
            val a = Offset(pie.x + ancho * fx, top + ry)
            val largo = alto * (0.55f + 0.12f * i)
            val rad = Math.toRadians((ang - 90.0))
            val b = Offset(a.x + (largo * cos(rad)).toFloat(), a.y + (largo * sin(rad)).toFloat())
            drawLine(n(PALMA, oscuro, k), a, b, 1.5f * u, StrokeCap.Round)
            drawLine(brillo(oscuro, 0.35f, k), a - Offset(0.5f * u, 0f), b - Offset(0.5f * u, 0f), 0.5f * u)
            val tip = Offset(b.x + (largo * 0.12f * cos(rad)).toFloat(), b.y + (largo * 0.12f * sin(rad)).toFloat())
            drawLine(n(NEGRO, oscuro, k), b, tip, 2.4f * u, StrokeCap.Round)
        }
        val cuerpo = Path().apply {
            moveTo(l, top); lineTo(l, pie.y - ry * 0.5f)
            cubicTo(l, pie.y + ry * 0.4f, r, pie.y + ry * 0.4f, r, pie.y - ry * 0.5f)
            lineTo(r, top); close()
        }
        clipRect(0f, 0f, w * 2, pie.y - w * 0.01f) { drawPath(corrido(cuerpo, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.15f + 0.3f * k else 0.22f)) }
        drawOval(sombra(oscuro, 0.4f), Offset(l + w * 0.006f, pie.y - w * 0.008f), Size(ancho * 1.25f, w * 0.014f))
        val fondo = mezcla(OCRE, ESTUCO, 0.45f)
        drawPath(cuerpo, n(fondo, oscuro, k))
        clipPath(cuerpo) {
            drawRect(n(CINABRIO, oscuro, k), Offset(l, top), Size(ancho, alto * 0.14f))
            drawRect(n(NEGRO, oscuro, k), Offset(l, top + alto * 0.14f), Size(ancho, 1.2f * u))
            drawRect(n(CINABRIO, oscuro, k), Offset(l, pie.y - alto * 0.16f), Size(ancho, alto * 0.2f))
            drawRect(n(NEGRO, oscuro, k), Offset(l, pie.y - alto * 0.16f - 1.2f * u), Size(ancho, 1.2f * u))
            // La columna de glifos (formas inventadas) y, al lado, un campo con una voluta.
            for (q in 0 until 3) {
                val g = Rect(l + ancho * 0.56f, top + alto * (0.2f + 0.19f * q), l + ancho * 0.84f, top + alto * (0.36f + 0.19f * q))
                drawRoundRect(n(mezcla(OCRE, CINABRIO, 0.3f), oscuro, k), g.topLeft, g.size, CornerRadius(ancho * 0.05f))
                drawRoundRect(n(NEGRO, oscuro, k), g.topLeft, g.size, CornerRadius(ancho * 0.05f), style = Stroke(0.8f * u))
                drawCircle(n(NEGRO, oscuro, k), ancho * 0.03f, Offset(g.left + g.width * (0.35f + 0.3f * (q % 2)), g.center.y))
            }
            val e0 = Offset(l + ancho * 0.08f, top + alto * 0.62f); val es = ancho * 0.1f
            drawPath(Path().apply {
                moveTo(e0.x, e0.y); lineTo(e0.x, e0.y - es); lineTo(e0.x + es, e0.y - es); lineTo(e0.x + es, e0.y - 2 * es); lineTo(e0.x + 2 * es, e0.y - 2 * es)
                lineTo(e0.x + 2 * es, e0.y - 3 * es); lineTo(e0.x + 4 * es, e0.y - 3 * es); lineTo(e0.x + 4 * es, e0.y - es * 1.4f); lineTo(e0.x + 3 * es, e0.y - es * 1.4f)
            }, n(NEGRO, oscuro, k), style = Stroke(1.4f * u))
            drawCircle(n(AZUL, oscuro, k), ancho * 0.05f, Offset(l + ancho * 0.25f, top + alto * 0.72f))
            drawCircle(n(AZUL, oscuro, k), ancho * 0.05f, Offset(l + ancho * 0.4f, top + alto * 0.72f))
        }
        volumen(cuerpo, oscuro, k, haciaX, lustre = 0.25f)
        // La boca: el labio y lo oscuro de dentro.
        drawOval(n(mezcla(fondo, CINABRIO, 0.4f), oscuro, k), Offset(l, top - ry), Size(ancho, ry * 2))
        drawOval(n(mezcla(BARRO_OSC, NEGRO, 0.6f), oscuro, k), Offset(l + ancho * 0.1f, top - ry * 0.7f), Size(ancho * 0.8f, ry * 1.4f))
        drawArc(brillo(oscuro, 0.5f, k), 160f, 120f, false, Offset(l, top - ry), Size(ancho, ry * 2), style = Stroke(0.8f * u))
    }

    /**
     * La tea de ocote: un manojo de astillas de pino resinoso, atado con henequén, metido en un soporte de barro sobre la
     * grada alta. De día está apagada, con la punta tiznada; de noche arde (la llama se pinta aparte, [llama]).
     */
    private fun DrawScope.teaOcote(pie: Offset, w: Float, oscuro: Boolean, k: Float) {
        val u = density
        val sw = w * 0.042f; val sh = w * 0.05f
        val punta = Offset(w * tea.x, w * (tea.y + 0.02f))
        val astillas = listOf(-0.012f, -0.005f, 0.002f, 0.009f, 0.015f)
        // La sombra del manojo en el muro, de día (de noche la luz es suya).
        if (!oscuro) astillas.forEach { dx ->
            drawLine(sombra(false, 0.16f), Offset(pie.x + w * dx * 0.6f + w * 0.016f, pie.y - sh * 0.7f + w * 0.012f), Offset(punta.x + w * dx + w * 0.016f, punta.y + w * 0.012f), w * 0.008f, StrokeCap.Round)
        }
        astillas.forEachIndexed { i, dx ->
            val a = Offset(pie.x + w * dx * 0.6f, pie.y - sh * 0.7f)
            val b = Offset(punta.x + w * dx, punta.y + w * 0.006f * (i % 2))
            val col = mezcla(OCOTE, if (i % 2 == 0) PALMA_LUZ else BARRO_OSC, 0.25f)
            drawLine(n(col, oscuro, k), a, b, w * 0.0075f, StrokeCap.Butt)
            // La resina que escurre y la cara de la astilla que mira a la luz.
            drawLine(n(mezcla(OCOTE, BARRO_OSC, 0.5f), oscuro, k).copy(alpha = 0.6f), a + Offset(w * 0.002f, 0f), b + Offset(w * 0.002f, 0f), w * 0.002f)
            drawLine(brillo(oscuro, if (oscuro) 0.45f else 0.3f, k), a - Offset(w * 0.0025f, 0f), b - Offset(w * 0.0025f, 0f), w * 0.0015f)
            // La punta tiznada.
            val t0 = b + (a - b) * 0.12f
            drawLine(n(NEGRO, oscuro, maxOf(k, 0.3f)), b, t0, w * 0.0085f, StrokeCap.Butt)
        }
        // Los amarres.
        listOf(0.35f, 0.7f).forEach { f ->
            val y = pie.y - sh * 0.7f + (punta.y - pie.y + sh * 0.7f) * f
            val x = pie.x + (punta.x - pie.x) * f
            drawLine(n(HENEQUEN, oscuro, k), Offset(x - w * 0.017f, y + w * 0.002f), Offset(x + w * 0.019f, y - w * 0.002f), 2.2f * u)
            drawLine(sombra(oscuro, 0.3f), Offset(x - w * 0.017f, y + w * 0.004f), Offset(x + w * 0.019f, y), 0.8f * u)
        }
        // El soporte de barro, con su pie.
        val soporte = perfil(pie.x, pie.y - sh, 1f, 1f, floatArrayOf(sw * 0.5f, 0f, sw * 0.42f, sh * 0.35f, sw * 0.3f, sh * 0.65f, sw * 0.52f, sh * 0.92f, sw * 0.6f, sh))
        drawOval(sombra(oscuro, 0.38f), Offset(pie.x - sw * 0.5f, pie.y - w * 0.006f), Size(sw * 1.4f, w * 0.012f))
        drawPath(soporte, n(BARRO, oscuro, k))
        clipPath(soporte) { drawRect(n(CINABRIO, oscuro, k), Offset(pie.x - sw, pie.y - sh), Size(sw * 2, sh * 0.16f)) }
        volumen(soporte, oscuro, k, -1f, lustre = 0.2f)
        drawOval(n(mezcla(BARRO_OSC, NEGRO, 0.5f), oscuro, k), Offset(pie.x - sw * 0.45f, pie.y - sh - w * 0.004f), Size(sw * 0.9f, w * 0.008f))
    }

    /** La llama de la tea, con su propio color (no se apaga de noche), y su hilo de humo negro que sube y se tuerce. */
    private fun DrawScope.llama(c: Offset, w: Float) {
        val u = density
        // El humo negro del ocote.
        var p = c - Offset(0f, w * 0.03f)
        for (j in 0 until 5) {
            val sube = w * (0.03f + 0.006f * j)
            val lado = (if (j % 2 == 0) 1f else -1f) * w * (0.008f + 0.004f * j)
            val q = p + Offset(lado * 0.3f - w * 0.003f, -sube)
            val seg = Path().apply { moveTo(p.x, p.y); cubicTo(p.x + lado, p.y - sube * 0.35f, q.x + lado, q.y + sube * 0.35f, q.x, q.y) }
            drawPath(seg, Color(0xFF1A1210).copy(alpha = 0.5f - 0.08f * j), style = Stroke((1.4f + 1.2f * j) * u, cap = StrokeCap.Round))
            p = q
        }
        fun gota(alto: Float, ancho: Float) = Path().apply {
            moveTo(c.x, c.y - alto * 0.62f)
            cubicTo(c.x + ancho * 0.25f, c.y - alto * 0.3f, c.x + ancho * 0.55f, c.y + alto * 0.05f, c.x, c.y + alto * 0.38f)
            cubicTo(c.x - ancho * 0.55f, c.y + alto * 0.05f, c.x - ancho * 0.3f, c.y - alto * 0.3f, c.x, c.y - alto * 0.62f)
            close()
        }
        drawPath(gota(w * 0.05f, w * 0.03f), Color(0xFFE8622A))
        drawPath(gota(w * 0.036f, w * 0.021f), BRASA)
        drawPath(gota(w * 0.022f, w * 0.012f), BRASA_VIVA)
        drawCircle(Color(0xFFFFF1C8), w * 0.004f, c + Offset(0f, w * 0.008f))
    }

    /** El incensario de barro con púas: el pie acampanado, el cuerpo con dos hileras de púas, la boca con el copal y su humo. */
    private fun DrawScope.incensario(pie: Offset, alto: Float, w: Float, oscuro: Boolean, k: Float, haciaX: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        val barro = mezcla(BARRO, PIEDRA, 0.5f)
        val pieA = alto * 0.26f
        val cw = w * 0.04f
        val boca = pie.y - alto
        val silueta = Path().apply {
            moveTo(cx - cw * 1.25f, pie.y); lineTo(cx + cw * 1.25f, pie.y)
            cubicTo(cx + cw * 0.9f, pie.y - pieA * 0.4f, cx + cw * 0.6f, pie.y - pieA * 0.8f, cx + cw * 0.7f, pie.y - pieA)
            lineTo(cx + cw, boca + alto * 0.12f); lineTo(cx + cw * 1.3f, boca + alto * 0.05f); lineTo(cx + cw * 1.3f, boca)
            lineTo(cx - cw * 1.3f, boca); lineTo(cx - cw * 1.3f, boca + alto * 0.05f); lineTo(cx - cw, boca + alto * 0.12f)
            lineTo(cx - cw * 0.7f, pie.y - pieA)
            cubicTo(cx - cw * 0.6f, pie.y - pieA * 0.8f, cx - cw * 0.9f, pie.y - pieA * 0.4f, cx - cw * 1.25f, pie.y)
            close()
        }
        clipRect(0f, 0f, w * 2, pie.y - w * 0.01f) { drawPath(corrido(silueta, caida.x, caida.y), sombra(oscuro, if (oscuro) 0.15f + 0.3f * k else 0.22f)) }
        drawOval(sombra(oscuro, 0.4f), Offset(cx - cw * 1.1f, pie.y - w * 0.008f), Size(cw * 2.8f, w * 0.016f))
        drawPath(silueta, n(barro, oscuro, k))
        clipPath(silueta) {
            // Restos del azul con que se pintó, y el hollín que sube de la boca.
            drawPath(mancho(Offset(cx - cw * 0.3f, pie.y - alto * 0.5f), cw * 0.7f, alto * 0.18f, 1201), n(AZUL, oscuro, k).copy(alpha = 0.5f))
            drawRect(n(NEGRO, oscuro, k).copy(alpha = 0.35f), Offset(cx - cw * 1.4f, boca), Size(cw * 2.8f, alto * 0.14f))
            drawLine(n(mezcla(barro, NEGRO, 0.4f), oscuro, k), Offset(cx - cw, pie.y - pieA), Offset(cx + cw, pie.y - pieA), 1.2f * u)
        }
        volumen(silueta, oscuro, k, haciaX)
        // Las púas: de frente, botones con su luz y su sombra; a los lados, conos que salen del perfil.
        listOf(0.42f, 0.66f).forEachIndexed { fila, v ->
            val y = pie.y - alto * v
            listOf(-0.5f, 0f, 0.5f).forEach { fx ->
                val p = Offset(cx + cw * fx + (if (fila == 1) cw * 0.22f else 0f), y)
                drawCircle(sombra(oscuro, 0.35f), w * 0.0065f, p + Offset(1.4f * u, 1.4f * u))
                drawCircle(n(mezcla(barro, ESTUCO, 0.15f), oscuro, k), w * 0.0065f, p)
                drawCircle(brillo(oscuro, 0.45f, k), w * 0.0025f, p - Offset(w * 0.002f, w * 0.002f))
            }
            listOf(-1f, 1f).forEach { s ->
                drawPath(poli(cx + s * cw, y - w * 0.006f, cx + s * (cw + w * 0.014f), y, cx + s * cw, y + w * 0.006f), n(if (s < 0) mezcla(barro, ESTUCO, 0.2f) else mezcla(barro, NEGRO, 0.25f), oscuro, k))
            }
        }
        // La boca, con el copal: una bola blanca de resina sobre las brasas.
        drawOval(n(mezcla(BARRO_OSC, NEGRO, 0.5f), oscuro, k), Offset(cx - cw * 1.2f, boca - w * 0.006f), Size(cw * 2.4f, w * 0.012f))
        drawOval(n(ESTUCO, oscuro, maxOf(k, 0.4f)), Offset(cx - cw * 0.45f, boca - w * 0.011f), Size(cw * 0.9f, w * 0.01f))
        drawCircle(if (oscuro) BRASA_VIVA else BRASA.copy(alpha = 0.7f), 1.4f * u, Offset(cx + cw * 0.5f, boca - w * 0.002f))
        drawCircle(if (oscuro) BRASA else BRASA.copy(alpha = 0.5f), 1.1f * u, Offset(cx - cw * 0.7f, boca - w * 0.001f))
        // El hilo de humo del copal: sube en curvas, se ensancha y se deshace.
        val humo = mezcla(ESTUCO, PIEDRA, 0.25f)
        listOf(0f to 0.0f, 1f to 0.5f).forEach { (s, fase) ->
            var p = Offset(cx + s * cw * 0.3f, boca - w * 0.01f)
            for (j in 0 until 6) {
                val sube = w * (0.045f + 0.008f * j)
                val lado = (if ((j + (fase * 2).toInt()) % 2 == 0) 1f else -1f) * w * (0.012f + 0.006f * j)
                val q = p + Offset(lado * 0.4f + w * 0.004f * j, -sube)
                val seg = Path().apply { moveTo(p.x, p.y); cubicTo(p.x + lado, p.y - sube * 0.35f, q.x + lado, q.y + sube * 0.35f, q.x, q.y) }
                val a = (0.34f - 0.05f * j) * (1f - 0.3f * s)
                val col = if (oscuro) mezcla(mezcla(humo, NOCHE, 0.5f + 0.08f * j), BRASA, maxOf(0f, 0.5f - 0.1f * j)) else humo
                drawPath(seg, col.copy(alpha = a), style = Stroke((1.6f + 1.4f * j) * u, cap = StrokeCap.Round))
                p = q
                if (p.y < w * 0.02f) break
            }
        }
    }

    /** Dos medias conchas con pigmento (rojo y azul maya) y una tercera, chica, con negro de humo; un pincel atravesado. */
    private fun DrawScope.conchas(pie: Float, w: Float, oscuro: Boolean, k: Float) {
        val u = density
        listOf(Triple(w * 0.665f, w * 0.036f, CINABRIO), Triple(w * 0.735f, w * 0.028f, AZUL), Triple(w * 0.64f, w * 0.019f, NEGRO)).forEachIndexed { i, (cx, r, pig) ->
            val y = pie - (if (i == 2) -w * 0.004f else 0f)
            drawOval(sombra(oscuro, 0.4f), Offset(cx - r * 0.8f, y - r * 0.2f), Size(r * 2.2f, r * 0.55f))
            val cascara = Path().apply {
                moveTo(cx - r, y - r * 0.38f)
                cubicTo(cx - r, y + r * 0.1f, cx - r * 0.5f, y + r * 0.22f, cx, y + r * 0.22f)
                cubicTo(cx + r * 0.5f, y + r * 0.22f, cx + r, y + r * 0.1f, cx + r, y - r * 0.38f)
                close()
            }
            drawPath(cascara, n(mezcla(ESTUCO, ALMAGRE, 0.25f), oscuro, k))
            clipPath(cascara) { for (q in 0 until 5) drawLine(sombra(oscuro, 0.25f), Offset(cx + r * (q - 2) * 0.4f, y - r * 0.4f), Offset(cx + r * (q - 2) * 0.22f, y + r * 0.25f), 0.7f * u) }
            volumen(cascara, oscuro, k, 1f)
            drawOval(n(mezcla(ESTUCO, LUZ_MUEBLE, 0.4f), oscuro, k), Offset(cx - r, y - r * 0.62f), Size(r * 2, r * 0.48f))
            drawOval(n(pig, oscuro, k), Offset(cx - r * 0.72f, y - r * 0.56f), Size(r * 1.44f, r * 0.34f))
            drawOval(brillo(oscuro, 0.4f, k), Offset(cx - r * 0.5f, y - r * 0.52f), Size(r * 0.5f, r * 0.08f))
            // El pigmento que se corrió por el borde.
            drawLine(n(pig, oscuro, k).copy(alpha = 0.7f), Offset(cx + r * 0.6f, y - r * 0.4f), Offset(cx + r * 0.66f, y - r * 0.05f), 1.2f * u, StrokeCap.Round)
        }
        // Un pincel apoyado en la concha roja, con la punta manchada.
        val a = Offset(w * 0.64f, pie - w * 0.034f); val b = Offset(w * 0.76f, pie - w * 0.004f)
        drawLine(sombra(oscuro, 0.3f), a + Offset(1.5f * u, 2f * u), b + Offset(1.5f * u, 2f * u), 1.6f * u)
        drawLine(n(PALMA, oscuro, k), a, b, 1.6f * u, StrokeCap.Round)
        drawLine(n(CINABRIO, oscuro, k), a, a + (a - b) * 0.12f, 2.6f * u, StrokeCap.Round)
    }

    /** El collar de cuentas de jade colgado de una estaca del muro, con su pectoral; su sombra en el estuco. */
    private fun DrawScope.collar(clavo: Offset, w: Float, oscuro: Boolean, k: Float, caida: Offset) {
        val u = density
        val rx = w * 0.042f; val ry = w * 0.075f
        val c0 = Offset(clavo.x, clavo.y + ry * 0.98f)
        val cuentas = 19
        val pos = List(cuentas) { j ->
            val a = Math.toRadians(-70.0 + 320.0 * j / (cuentas - 1))
            Offset(c0.x + rx * sin(a).toFloat(), c0.y - ry * cos(a).toFloat())
        }.filter { it.y > clavo.y + w * 0.008f }
        val sd = caida
        val sa = if (oscuro) 0.22f * k + 0.08f else 0.25f
        // La sombra del collar y del pectoral.
        pos.forEach { drawCircle(sombra(oscuro, sa), w * 0.0085f, it + sd) }
        drawRoundRect(sombra(oscuro, sa), Offset(c0.x - w * 0.016f, c0.y + ry - w * 0.006f) + sd, Size(w * 0.032f, w * 0.042f), CornerRadius(w * 0.008f))
        // El cordón.
        val cordon = Path().apply { moveTo(clavo.x - w * 0.006f, clavo.y); pos.forEach { lineTo(it.x, it.y) }; lineTo(clavo.x + w * 0.006f, clavo.y) }
        drawPath(cordon, n(HENEQUEN, oscuro, k), style = Stroke(0.8f * u))
        pos.forEachIndexed { j, p ->
            val col = when (j % 3) { 0 -> JADE; 1 -> mezcla(JADE, JADE_LUZ, 0.35f); else -> mezcla(JADE, NEGRO, 0.2f) }
            val r = w * (0.0075f + 0.0015f * azar(1301, j))
            drawCircle(n(col, oscuro, k), r, p)
            drawCircle(sombra(oscuro, 0.25f), r, p + Offset(r * 0.35f, r * 0.35f), style = Stroke(r * 0.4f))
            drawCircle(if (oscuro) BRASA_VIVA.copy(alpha = 0.6f * k) else luzMueble(0.7f), r * 0.32f, p - Offset(r * 0.35f, r * 0.35f))
        }
        // El pectoral: una placa de jade tallada, con su agujero y una raya.
        val pr = Rect(c0.x - w * 0.016f, c0.y + ry - w * 0.006f, c0.x + w * 0.016f, c0.y + ry + w * 0.036f)
        drawRoundRect(n(JADE, oscuro, k), pr.topLeft, pr.size, CornerRadius(w * 0.008f))
        drawRect(n(mezcla(JADE, NEGRO, 0.35f), oscuro, k), Offset(pr.right - pr.width * 0.3f, pr.top), Size(pr.width * 0.3f, pr.height))
        drawRoundRect(n(mezcla(JADE, NEGRO, 0.45f), oscuro, k), Offset(pr.left + pr.width * 0.25f, pr.top + pr.height * 0.35f), Size(pr.width * 0.5f, pr.height * 0.4f), CornerRadius(w * 0.004f), style = Stroke(1f * u))
        drawRect(if (oscuro) BRASA_VIVA.copy(alpha = 0.55f * k) else luzMueble(0.55f), Offset(pr.left + pr.width * 0.12f, pr.top + pr.height * 0.12f), Size(pr.width * 0.12f, pr.height * 0.55f))
        // La estaca de madera que sale del muro.
        drawCircle(sombra(oscuro, 0.45f), w * 0.011f, clavo + Offset(w * 0.006f, w * 0.008f))
        drawCircle(n(BARRO_OSC, oscuro, k), w * 0.011f, clavo)
        drawCircle(n(mezcla(PALMA, BARRO_OSC, 0.3f), oscuro, k), w * 0.007f, clavo - Offset(w * 0.002f, w * 0.002f))
        drawCircle(brillo(oscuro, 0.5f, k), w * 0.003f, clavo - Offset(w * 0.004f, w * 0.004f))
    }

    /** El petate enrollado, tendido en el piso contra la grada: cortado por el borde, con su tejido, sus amarres y la espiral. */
    private fun DrawScope.petate(pie: Float, w: Float, oscuro: Boolean, k: Float) {
        val u = density
        val r = w * 0.05f
        val cy = pie - r
        val x0 = -w * 0.04f; val x1 = w * 0.3f
        drawOval(sombra(oscuro, 0.4f), Offset(x0, pie - r * 0.3f), Size(x1 - x0 + r * 0.8f, r * 0.55f))
        val cuerpo = Rect(x0, cy - r, x1, cy + r)
        drawRect(n(PALMA, oscuro, k), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            // El tejido en espiga, a lo largo.
            for (fila in 0 until 5) {
                val y = cuerpo.top + cuerpo.height * (fila + 0.5f) / 5f
                var x = x0; var j = 0
                while (x < x1) {
                    val s = if ((j + fila) % 2 == 0) 1f else -1f
                    drawLine(n(PALMA_OSC, oscuro, k).copy(alpha = 0.6f), Offset(x, y - s * r * 0.16f), Offset(x + w * 0.014f, y + s * r * 0.16f), 1.1f * u)
                    x += w * 0.014f; j++
                }
            }
            // El cilindro: luz arriba, sombra abajo en dos tonos.
            drawRect(brillo(oscuro, 0.3f, k), cuerpo.topLeft, Size(cuerpo.width, r * 0.45f))
            drawRect(sombra(oscuro, 0.18f), Offset(x0, cy + r * 0.2f), Size(cuerpo.width, r * 0.8f))
            drawRect(sombra(oscuro, 0.22f), Offset(x0, cy + r * 0.62f), Size(cuerpo.width, r * 0.4f))
        }
        // Los amarres de henequén.
        listOf(w * 0.05f, w * 0.21f).forEach { x ->
            drawLine(n(mezcla(HENEQUEN, BARRO_OSC, 0.3f), oscuro, k), Offset(x, cy - r), Offset(x + w * 0.006f, cy + r), 2.2f * u)
            drawLine(brillo(oscuro, 0.35f, k), Offset(x - 0.6f * u, cy - r), Offset(x + w * 0.004f, cy), 0.8f * u)
        }
        // La boca del rollo: la espiral del petate y una punta que se suelta.
        val boca = Rect(x1 - r * 0.45f, cy - r, x1 + r * 0.45f, cy + r)
        drawOval(n(PALMA_LUZ, oscuro, k), boca.topLeft, boca.size)
        for (q in 1..4) {
            val f = q / 5f
            drawArc(n(PALMA_OSC, oscuro, k), -80f + q * 30f, 260f, false, Offset(boca.center.x - boca.width * f / 2, boca.center.y - boca.height * f / 2), Size(boca.width * f, boca.height * f), style = Stroke(0.9f * u))
        }
        drawOval(sombra(oscuro, 0.3f), Offset(boca.center.x - boca.width * 0.08f, boca.center.y - boca.height * 0.08f), Size(boca.width * 0.16f, boca.height * 0.16f))
        val punta = poli(x1 - w * 0.02f, cy - r, x1 + w * 0.025f, cy - r * 1.25f, x1 + w * 0.03f, cy - r * 0.9f, x1, cy - r * 0.8f)
        drawPath(punta, n(PALMA, oscuro, k))
        drawPath(borde(punta, 0f, 1.5f * u), sombra(oscuro, 0.3f))
    }

    /** Un cajete trípode caído en el piso, con una hoja de amate suelta al lado (sin placa). */
    private fun DrawScope.cajete(c: Offset, ancho: Float, oscuro: Boolean, k: Float) {
        val u = density
        val hoja = poli(c.x + ancho * 0.6f, c.y + ancho * 0.05f, c.x + ancho * 1.4f, c.y - ancho * 0.08f, c.x + ancho * 1.5f, c.y + ancho * 0.12f, c.x + ancho * 0.7f, c.y + ancho * 0.25f)
        drawPath(corrido(hoja, 1.5f * u, 2f * u), sombra(oscuro, 0.3f))
        drawPath(hoja, n(AMATE_CLARO, oscuro, k))
        drawPath(borde(hoja, -1.2f * u, -1.2f * u), n(CINABRIO, oscuro, k).copy(alpha = 0.6f))
        drawOval(sombra(oscuro, 0.35f), Offset(c.x - ancho * 0.45f, c.y - ancho * 0.05f), Size(ancho * 1.05f, ancho * 0.18f))
        listOf(-0.3f, 0f, 0.3f).forEach { fx -> drawLine(n(BARRO_OSC, oscuro, k), Offset(c.x + ancho * fx, c.y - ancho * 0.12f), Offset(c.x + ancho * fx * 1.1f, c.y), 2.2f * u, StrokeCap.Round) }
        val cuenco = Path().apply {
            moveTo(c.x - ancho * 0.5f, c.y - ancho * 0.36f)
            cubicTo(c.x - ancho * 0.48f, c.y - ancho * 0.1f, c.x + ancho * 0.48f, c.y - ancho * 0.1f, c.x + ancho * 0.5f, c.y - ancho * 0.36f)
            close()
        }
        drawPath(cuenco, n(BARRO, oscuro, k))
        drawLine(n(CINABRIO, oscuro, k), Offset(c.x - ancho * 0.5f, c.y - ancho * 0.36f), Offset(c.x + ancho * 0.5f, c.y - ancho * 0.36f), 2.2f * u)
        volumen(cuenco, oscuro, k, 1f, lustre = 0.15f)
    }

    /** La placa de «y N más»: una hoja de amate apoyada contra la grada, con su filete rojo. */
    private fun DrawScope.placaAmate(r: Rect, oscuro: Boolean, k: Float) {
        val u = density
        val p = contornoIrregular(r, 1401, 26, 0.03f)
        drawPath(corrido(p, 2.5f * u, 2.5f * u), sombra(oscuro, 0.4f))
        drawPath(p, if (oscuro) mezcla(TIRA_NOCHE, TIRA_LUZ, k) else AMATE_CLARO)
        drawRect(n(CINABRIO, oscuro, maxOf(k, 0.5f)), Offset(r.left + 3.5f * u, r.top + 3.5f * u), Size(r.width - 7 * u, r.height - 7 * u), style = Stroke(1.2f * u))
        drawRect(brillo(oscuro, 0.4f, k), Offset(r.left, r.top + 1f * u), Size(r.width, 1f * u))
    }

    /** El brasero trípode de barro con sus brasas; de noche, la luz que alumbra el cuarto desde abajo. */
    private fun DrawScope.braseroTripode(pie: Offset, ancho: Float, alto: Float, w: Float, oscuro: Boolean) {
        val u = density
        val cx = pie.x
        val patas = alto * 0.28f
        val boca = pie.y - alto
        val k = if (oscuro) 0.85f else 0f
        drawOval(sombra(oscuro, if (oscuro) 0.25f else 0.38f), Offset(cx - ancho * 0.42f, pie.y - w * 0.008f), Size(ancho * 1.0f, w * 0.018f))
        if (oscuro) mancha(Offset(cx, pie.y), ancho * 0.9f, BRASA.copy(alpha = 0.3f))
        // Las patas (dos a los lados y una delante), huecas, con su sonaja.
        listOf(-0.3f, 0.3f, 0.02f).forEachIndexed { i, fx ->
            val x = cx + ancho * fx
            val yb = pie.y + (if (i == 2) w * 0.004f else 0f)
            val pata = poli(x - ancho * 0.045f, yb - patas, x + ancho * 0.045f, yb - patas, x + ancho * 0.025f, yb, x - ancho * 0.03f, yb)
            drawPath(pata, n(BARRO, oscuro, k * 0.8f))
            drawPath(borde(pata, -ancho * 0.03f, 0f), sombra(oscuro, 0.3f))
        }
        val cuenco = Path().apply {
            moveTo(cx - ancho * 0.5f, boca)
            cubicTo(cx - ancho * 0.48f, boca + alto * 0.55f, cx - ancho * 0.3f, pie.y - patas * 0.9f, cx, pie.y - patas * 0.9f)
            cubicTo(cx + ancho * 0.3f, pie.y - patas * 0.9f, cx + ancho * 0.48f, boca + alto * 0.55f, cx + ancho * 0.5f, boca)
            close()
        }
        drawPath(cuenco, n(BARRO, oscuro, k))
        clipPath(cuenco) {
            drawRect(n(CINABRIO, oscuro, k), Offset(cx - ancho * 0.6f, boca), Size(ancho * 1.2f, alto * 0.16f))
            drawRect(n(NEGRO, oscuro, k).copy(alpha = 0.3f), Offset(cx - ancho * 0.6f, boca + alto * 0.16f), Size(ancho * 1.2f, 1.2f * u))
            // El hollín que sube por el lado.
            drawPath(mancho(Offset(cx + ancho * 0.3f, boca + alto * 0.15f), ancho * 0.12f, alto * 0.2f, 1501), n(NEGRO, oscuro, k).copy(alpha = 0.3f))
        }
        // De noche la luz viene de dentro, de arriba: el cuenco se ve en sombra abajo y con filo en el labio.
        if (oscuro) {
            drawPath(cuenco, NOCHE.copy(alpha = 0.3f))
            drawPath(borde(cuenco, 0f, alto * 0.08f), BRASA.copy(alpha = 0.45f))
        } else volumen(cuenco, false, 0f, 1f, lustre = 0.15f)
        // Las brasas: un montón de carbones, la ceniza y las grietas encendidas.
        val monte = Path().apply {
            moveTo(cx - ancho * 0.46f, boca + alto * 0.02f)
            cubicTo(cx - ancho * 0.3f, boca - alto * 0.22f, cx + ancho * 0.2f, boca - alto * 0.3f, cx + ancho * 0.46f, boca + alto * 0.02f)
            close()
        }
        drawPath(monte, if (oscuro) Color(0xFF3A1A12) else Color(0xFF4A3C36))
        clipPath(monte) {
            for (q in 0 until 7) {
                val o = Offset(cx + ancho * (-0.36f + 0.72f * azar(1502, q)), boca - alto * (0.02f + 0.16f * azar(1503, q)))
                drawPath(mancho(o, ancho * 0.07f, alto * 0.07f, 1504 + q, 6), if (oscuro) Color(0xFF55241A) else Color(0xFF5E514B))
                drawPath(borde(mancho(o, ancho * 0.07f, alto * 0.07f, 1504 + q, 6), 0f, alto * 0.02f), if (oscuro) BRASA_VIVA.copy(alpha = 0.9f) else BRASA.copy(alpha = 0.55f))
            }
            // La ceniza clara encima, de día; de noche, el rojo vivo.
            if (!oscuro) drawRect(Color(0xFFBDB3A6).copy(alpha = 0.45f), Offset(cx - ancho * 0.5f, boca - alto * 0.3f), Size(ancho, alto * 0.12f))
            else drawPath(monte, BRASA.copy(alpha = 0.35f))
        }
        drawOval(n(mezcla(BARRO, CINABRIO, 0.4f), oscuro, k), Offset(cx - ancho * 0.5f, boca - alto * 0.03f), Size(ancho, alto * 0.08f), style = Stroke(1.8f * u))
        if (oscuro) {
            mancha(Offset(cx, boca - alto * 0.12f), ancho * 0.5f, BRASA_VIVA.copy(alpha = 0.5f))
            listOf(Offset(-0.1f, -0.5f), Offset(0.08f, -0.75f), Offset(0.15f, -0.4f)).forEach { o ->
                drawCircle(BRASA_VIVA, 1.1f * u, Offset(cx + ancho * o.x, boca + alto * o.y))
            }
        }
    }

    // ---------------------------------------------------------------- los compartimentos: lo que se guarda en cada grada

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (fracciones del ancho), para saber cuánta lumbre le llega y de qué lado.
        val dentro = c.cols == cols
        val aw = anchoH(); val ah = altoH()
        val centro = Offset(lado + c.col * (aw + hueco) + aw * 0.5f, corona + hueco + c.fila * (ah + hueco) + ah * 0.6f)
        val k = if (!oscuro) 0f else if (dentro) (calor(centro, c.filas) * 1.15f).coerceIn(0.06f, 1f) else 0.5f
        val fuente = if (dentro) luzMayor(centro, c.filas) else Offset(centro.x + 0.3f, centro.y + 0.3f)
        val haciaX = if (fuente.x >= centro.x) 1f else -1f
        val lejos = (centro - fuente).let { it / it.getDistance().coerceAtLeast(0.0001f) }
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        // El pie de lo guardado, sobre la huella de la grada (la pinta el mueble al fondo de cada hueco).
        val pie = h - w * 0.03f
        val repisa = h - w * 0.06f
        if (c.vacio) { vacio(c.semilla, pie, oscuro, k, dia, haciaX); return }
        val mez = (c.semilla xor (c.semilla ushr 3)) ushr 1
        val tipo = if (c.sinTema) 3 else Math.floorMod(mez, 3)
        val nn = c.n.coerceIn(0, 7)
        val cuantas = if (c.n <= 0) 0 else (1 + nn).coerceAtMost(7)
        val sube = h * (0.012f * nn) + ab * h * 0.14f
        val silueta = when (tipo) {
            0 -> Path().apply { addRect(Rect(w * 0.09f, h * 0.37f, w * 0.91f, pie)) }
            1 -> olla(w, h, pie)
            2 -> bulto(w, h, pie)
            else -> jicara(w, h, pie)
        }
        // Primero el aro que tapa el aura (del color de lo que hay detrás); luego, encima, las sombras y lo guardado.
        val extra = when (tipo) {
            1 -> {
                val bocaY = h * 0.3f; val alza = sube * 0.8f
                listOf(Path().apply {
                    moveTo(w * 0.26f, bocaY - alza)
                    cubicTo(w * 0.28f, bocaY - h * 0.12f - alza, w * 0.72f, bocaY - h * 0.12f - alza, w * 0.74f, bocaY - alza)
                    close()
                }, Path().apply { addRect(Rect(w * 0.46f, bocaY - h * 0.13f - alza, w * 0.54f, bocaY - h * 0.09f - alza)) }) +
                    hojasContorno(w * 0.3f, w * 0.7f, bocaY - h * 0.16f - alza * 0.6f, bocaY + h * 0.02f, cuantas.coerceAtMost(5), c.semilla)
            }
            2 -> orejasBulto(w, h) + hojasContorno(w * 0.38f, w * 0.62f, h * 0.06f - sube * 0.6f, h * 0.27f, cuantas.coerceAtMost(4), c.semilla)
            3 -> hojasContorno(w * 0.16f, w * 0.84f, h * 0.18f - ab * h * 0.1f, h * 0.52f, cuantas.coerceAtLeast(2), c.semilla) +
                poli(w * 0.78f, h * 0.48f, w * 0.95f, h * 0.52f, w * 0.93f, h * 0.7f, w * 0.8f, h * 0.62f)
            else -> listOf(Path().apply { addRect(Rect(w * 0.065f, h * 0.24f - sube, w * 0.935f, h * 0.37f - sube + 1.5f * u)) })
        }
        cubreAura(c, oscuro, listOf(silueta, Path().apply { addRect(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)) }) + extra)
        // La sombra de lo guardado en la cara de la grada: de día, a la derecha y abajo; de noche, lejos de la lumbre.
        clipRect(-w, -h, w * 2, repisa) {
            if (!oscuro) drawPath(corrido(silueta, w * 0.045f, h * 0.02f), sombra(false, 0.2f), blendMode = BlendMode.SrcAtop)
            else if (k > 0.08f) drawPath(corrido(silueta, lejos.x * w * 0.12f, lejos.y * w * 0.12f), SOMBRA_NOCHE.copy(alpha = (0.7f * k).coerceAtMost(0.6f)), blendMode = BlendMode.SrcAtop)
        }
        drawOval(sombra(oscuro, if (oscuro) 0.3f + 0.35f * k else 0.34f), Offset(w * 0.08f, pie - w * 0.025f), Size(w * 0.9f, w * 0.05f), blendMode = BlendMode.SrcAtop)
        when (tipo) {
            0 -> petaca(w, h, pie, sube, cuantas, c.semilla, oscuro, k, dia, haciaX)
            1 -> ollaConTapa(w, h, pie, sube, cuantas, c.semilla, mez, oscuro, k, dia, haciaX)
            2 -> bultoAlgodon(w, h, pie, sube, cuantas, c.semilla, mez, oscuro, k, dia, haciaX)
            else -> jicaraSuelta(w, h, pie, cuantas, c.semilla, ab, oscuro, k, dia, haciaX)
        }
        tira(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), c.semilla, !c.sinTema, tipo, oscuro, k, dia)
        // Al abrirlo, sube un poco de luz de dentro.
        if (ab > 0f) mancha(Offset(w / 2, h * 0.2f), w * 0.4f, if (oscuro) BRASA.copy(alpha = 0.22f * ab) else luzMueble(0.35f * ab))
    }

    /** La tira de amate con el tema, colgada al frente con dos cordeles; con filete rojo, menos en la de las ideas sin tema. */
    private fun DrawScope.tira(r: Rect, semilla: Int, filete: Boolean, tipo: Int, oscuro: Boolean, k: Float, dia: Float) {
        val u = density
        val p = contornoIrregular(r, semilla, 30, 0.025f)
        // Los cordeles que la cuelgan.
        val ata = if (tipo == 1) r.top - (size.height * 0.12f) else r.top - size.height * 0.06f
        listOf(0.1f, 0.9f).forEach { f ->
            val x = r.left + r.width * f
            drawLine(n(HENEQUEN, oscuro, k, dia), Offset(x, r.top + 2f * u), Offset(x + (if (f < 0.5f) 1f else -1f) * r.width * 0.03f, ata), 1.2f * u)
            drawCircle(n(mezcla(HENEQUEN, BARRO_OSC, 0.4f), oscuro, k), 1.6f * u, Offset(x, r.top + 3f * u))
        }
        drawPath(corrido(p, 1.6f * u, 2.2f * u), sombra(oscuro, 0.4f))
        drawPath(p, if (oscuro) mezcla(TIRA_NOCHE, TIRA_LUZ, k * 1.2f) else mezcla(AMATE_CLARO, LUZ_MUEBLE, dia))
        clipPath(p) {
            // Las fibras del amate.
            for (j in 0 until 7) {
                val y = r.top + r.height * azar(semilla + 3, j)
                drawLine(AMATE_OSC.copy(alpha = if (oscuro) 0.14f else 0.22f), Offset(r.left + r.width * azar(semilla, j + 10), y),
                    Offset(r.left + r.width * azar(semilla, j + 30), y + (azar(semilla, j + 50) - 0.5f) * 6 * u), 0.6f * u)
            }
            drawRect(brillo(oscuro, 0.4f, k), Offset(r.left, r.top), Size(r.width, 1.2f * u))
            drawRect(sombra(oscuro, 0.12f), Offset(r.left, r.bottom - 2.4f * u), Size(r.width, 2.4f * u))
        }
        if (filete) drawRect(n(CINABRIO, oscuro, maxOf(k, 0.55f)), Offset(r.left + 2.8f * u, r.top + 2.8f * u), Size(r.width - 5.6f * u, r.height - 5.6f * u), style = Stroke(1.2f * u))
    }

    /** La petaca de palma: el cuerpo tejido con una franja teñida, la tapa (que levantan las hojas) y el henequén con su nudo. */
    private fun DrawScope.petaca(w: Float, h: Float, pie: Float, sube: Float, cuantas: Int, semilla: Int, oscuro: Boolean, k: Float, dia: Float, haciaX: Float) {
        val u = density
        val cuerpo = Rect(w * 0.09f, h * 0.37f, w * 0.91f, pie)
        val tapa = Rect(w * 0.065f, cuerpo.top - sube - h * 0.13f, w * 0.935f, cuerpo.top - sube + 1.5f * u)
        if (sube > 1f || cuantas > 0) {
            drawRect(n(Color(0xFF2A1A0E), oscuro, k), Offset(cuerpo.left + 3 * u, tapa.bottom - 2 * u), Size(cuerpo.width - 6 * u, cuerpo.top - tapa.bottom + 4 * u))
            hojas(cuerpo.left + w * 0.04f, cuerpo.right - w * 0.04f, tapa.bottom - h * 0.06f - sube * 0.3f, cuerpo.top + h * 0.06f, cuantas, semilla, oscuro, k, dia)
        }
        val fc = Path().apply { addRect(cuerpo) }
        trenzado(fc, cuerpo, 6.5f * u, oscuro, k, dia, semilla)
        clipPath(fc) {
            // La franja teñida de rojo y el borde de arriba enrollado.
            drawRect(n(CINABRIO, oscuro, k).copy(alpha = 0.4f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.12f), Size(cuerpo.width, cuerpo.height * 0.07f))
            drawRect(n(PALMA_OSC, oscuro, k), cuerpo.topLeft, Size(cuerpo.width, h * 0.035f))
            drawRect(brillo(oscuro, 0.3f, k), cuerpo.topLeft, Size(cuerpo.width, h * 0.01f))
            // El volumen de una caja: la izquierda a la luz, la derecha en sombra (de noche, según la lumbre), la base hundida.
            val sl = if (oscuro) haciaX else -1f
            drawRect(if (oscuro) BRASA.copy(alpha = 0.2f * k) else luzMueble(0.18f), Offset(if (sl < 0) cuerpo.left else cuerpo.right - cuerpo.width * 0.14f, cuerpo.top), Size(cuerpo.width * 0.14f, cuerpo.height))
            drawRect(sombra(oscuro, 0.22f), Offset(if (sl < 0) cuerpo.right - cuerpo.width * 0.22f else cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.22f, cuerpo.height))
            drawRect(sombra(oscuro, 0.2f), Offset(cuerpo.left, cuerpo.bottom - h * 0.05f), Size(cuerpo.width, h * 0.05f))
            // El uso: una tira rota que se levanta en una esquina.
            if (Math.floorMod(semilla, 3) == 0) drawLine(n(PALMA_LUZ, oscuro, k), Offset(cuerpo.right - w * 0.1f, cuerpo.bottom - h * 0.08f), Offset(cuerpo.right - w * 0.04f, cuerpo.bottom - h * 0.15f), 2f * u, StrokeCap.Round)
        }
        drawRect(n(PALMA_OSC, oscuro, k), cuerpo.topLeft, cuerpo.size, style = Stroke(1.2f * u))
        // La tapa: su cara de arriba vista apenas desde arriba, el frente tejido y el borde trenzado.
        val ft = Path().apply { addRect(tapa) }
        trenzado(ft, tapa, 6.5f * u, oscuro, k, dia, semilla + 1)
        clipPath(ft) {
            drawRect(n(PALMA_LUZ, oscuro, k, dia).copy(alpha = 0.55f), tapa.topLeft, Size(tapa.width, tapa.height * 0.24f))
            drawRect(sombra(oscuro, 0.2f), Offset(tapa.left, tapa.bottom - tapa.height * 0.25f), Size(tapa.width, tapa.height * 0.25f))
            val sl = if (oscuro) haciaX else -1f
            drawRect(sombra(oscuro, 0.2f), Offset(if (sl < 0) tapa.right - tapa.width * 0.18f else tapa.left, tapa.top), Size(tapa.width * 0.18f, tapa.height))
        }
        var x = tapa.left
        while (x < tapa.right - 3 * u) { drawLine(n(PALMA_OSC, oscuro, k), Offset(x, tapa.bottom - 3 * u), Offset(x + 3 * u, tapa.bottom), 1.3f * u); x += 3.2f * u }
        drawRect(n(PALMA_OSC, oscuro, k), tapa.topLeft, tapa.size, style = Stroke(1.2f * u))
        // El henequén: dos vueltas que bajan por delante y el nudo en la tapa, con las puntas sueltas.
        listOf(0.24f, 0.76f).forEach { fx ->
            val cx = w * fx
            drawLine(sombra(oscuro, 0.3f), Offset(cx + 1.4f * u, tapa.top), Offset(cx + 1.4f * u, cuerpo.bottom), 1.8f * u)
            drawLine(n(HENEQUEN, oscuro, k, dia), Offset(cx, tapa.top), Offset(cx, cuerpo.bottom), 2f * u)
            drawLine(brillo(oscuro, 0.4f, k), Offset(cx - 0.6f * u, tapa.top), Offset(cx - 0.6f * u, cuerpo.bottom), 0.6f * u)
        }
        val nudo = Offset(w * 0.5f, tapa.top + tapa.height * 0.5f)
        drawLine(n(HENEQUEN, oscuro, k, dia), Offset(w * 0.24f, nudo.y), Offset(w * 0.76f, nudo.y), 2f * u)
        drawCircle(sombra(oscuro, 0.35f), 3.4f * u, nudo + Offset(1.2f * u, 1.4f * u))
        drawCircle(n(HENEQUEN, oscuro, k, dia), 3.2f * u, nudo)
        drawCircle(brillo(oscuro, 0.45f, k), 1.2f * u, nudo - Offset(1f * u, 1f * u))
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + w * 0.03f, nudo.y + h * 0.08f, nudo.x + w * 0.05f, nudo.y + h * 0.1f, nudo.x + w * 0.06f, nudo.y + h * 0.16f) }, n(HENEQUEN, oscuro, k, dia), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x - w * 0.02f, nudo.y + h * 0.06f, nudo.x - w * 0.015f, nudo.y + h * 0.1f, nudo.x - w * 0.03f, nudo.y + h * 0.13f) }, n(HENEQUEN, oscuro, k, dia), style = Stroke(1.4f * u, cap = StrokeCap.Round))
    }

    /** La silueta de la olla: cuello corto, hombro, vientre ancho. */
    private fun olla(w: Float, h: Float, pie: Float): Path {
        val b = pie / h
        return perfil(w * 0.5f, 0f, w, h, floatArrayOf(0.2f, 0.3f, 0.21f, 0.34f, 0.33f, 0.39f, 0.415f, 0.48f, 0.44f, 0.6f, 0.41f, 0.73f, 0.33f, b - 0.08f, 0.22f, b - 0.015f, 0.17f, b))
    }

    /** La olla con tapa: engobe rojo o banda de glifos inventados en el hombro; las hojas asoman bajo la tapa entreabierta. */
    private fun DrawScope.ollaConTapa(w: Float, h: Float, pie: Float, sube: Float, cuantas: Int, semilla: Int, mez: Int, oscuro: Boolean, k: Float, dia: Float, haciaX: Float) {
        val u = density
        val forma = olla(w, h, pie)
        val roja = (mez ushr 2) % 2 == 0
        val bocaY = h * 0.3f
        val alza = sube * 0.8f
        // Las hojas que asoman por la boca, y lo oscuro de dentro.
        if (cuantas > 0 || alza > 1f) {
            drawOval(n(Color(0xFF2A1A0E), oscuro, k), Offset(w * 0.3f, bocaY - h * 0.03f), Size(w * 0.4f, h * 0.06f))
            hojas(w * 0.3f, w * 0.7f, bocaY - h * 0.16f - alza * 0.6f, bocaY + h * 0.02f, cuantas.coerceAtMost(5), semilla, oscuro, k, dia)
        }
        drawPath(forma, n(if (roja) mezcla(CINABRIO, BARRO, 0.35f) else BARRO, oscuro, k, dia))
        clipPath(forma) {
            if (roja) {
                // Engobe rojo bruñido, con la franja del cuello sin engobe y una chorrera.
                drawRect(n(BARRO, oscuro, k, dia), Offset(0f, h * 0.29f), Size(w, h * 0.06f))
                drawLine(n(mezcla(CINABRIO, BARRO, 0.1f), oscuro, k).copy(alpha = 0.6f), Offset(w * 0.68f, h * 0.36f), Offset(w * 0.7f, h * 0.5f), 2.4f * u, StrokeCap.Round)
                drawRect(n(BARRO, oscuro, k, dia).copy(alpha = 0.7f), Offset(0f, pie - h * 0.1f), Size(w, h * 0.1f))
            } else {
                // La banda de engobe crema en el hombro, con glifos inventados entre filetes.
                val b0 = h * 0.38f; val b1 = h * 0.53f
                drawRect(n(mezcla(ESTUCO, OCRE, 0.35f), oscuro, k, dia), Offset(0f, b0), Size(w, b1 - b0))
                drawRect(n(CINABRIO, oscuro, k), Offset(0f, b0 - 1.6f * u), Size(w, 2f * u))
                drawRect(n(CINABRIO, oscuro, k), Offset(0f, b1 - 0.4f * u), Size(w, 2f * u))
                for (q in 0 until 5) {
                    val gx = w * (0.16f + 0.17f * q)
                    val g = Rect(gx - w * 0.055f, b0 + (b1 - b0) * 0.18f, gx + w * 0.055f, b1 - (b1 - b0) * 0.18f)
                    drawRoundRect(n(NEGRO, oscuro, k), g.topLeft, g.size, CornerRadius(w * 0.02f), style = Stroke(1f * u))
                    when (Math.floorMod(semilla + q, 3)) {
                        0 -> { drawCircle(n(NEGRO, oscuro, k), w * 0.012f, Offset(g.center.x - w * 0.02f, g.center.y)); drawCircle(n(CINABRIO, oscuro, k), w * 0.012f, Offset(g.center.x + w * 0.02f, g.center.y)) }
                        1 -> drawArc(n(NEGRO, oscuro, k), 180f, 270f, false, Offset(g.left + g.width * 0.25f, g.top + g.height * 0.2f), Size(g.width * 0.5f, g.height * 0.6f), style = Stroke(1.2f * u))
                        else -> { drawLine(n(NEGRO, oscuro, k), Offset(g.left + g.width * 0.25f, g.center.y), Offset(g.right - g.width * 0.25f, g.center.y), 1.4f * u); drawCircle(n(CINABRIO, oscuro, k), w * 0.01f, Offset(g.center.x, g.top + g.height * 0.28f)) }
                    }
                }
            }
            // El uso: un desportillado y el roce claro del vientre.
            drawPath(poli(w * 0.73f, h * 0.62f, w * 0.78f, h * 0.6f, w * 0.79f, h * 0.65f, w * 0.75f, h * 0.67f), n(mezcla(BARRO, ESTUCO, 0.3f), oscuro, k))
        }
        volumen(forma, oscuro, k, haciaX, lustre = if (roja) 0.3f else 0.18f)
        // La tapa: un cajete puesto boca abajo, con su asidero, ladeada si las hojas la levantan.
        rotate(-(sube / h) * 40f, Offset(w * 0.28f, bocaY)) {
            val tapa = Path().apply {
                moveTo(w * 0.26f, bocaY - alza)
                cubicTo(w * 0.28f, bocaY - h * 0.12f - alza, w * 0.72f, bocaY - h * 0.12f - alza, w * 0.74f, bocaY - alza)
                close()
            }
            drawPath(tapa, n(BARRO, oscuro, k, dia))
            drawRect(n(CINABRIO, oscuro, k), Offset(w * 0.26f, bocaY - alza - 1.6f * u), Size(w * 0.48f, 2.2f * u))
            volumen(tapa, oscuro, k, haciaX, lustre = 0.2f)
            drawRoundRect(n(BARRO_OSC, oscuro, k), Offset(w * 0.46f, bocaY - h * 0.13f - alza), Size(w * 0.08f, h * 0.04f), CornerRadius(w * 0.02f))
            drawRect(brillo(oscuro, 0.4f, k), Offset(w * 0.47f, bocaY - h * 0.128f - alza), Size(w * 0.03f, 1f * u))
        }
    }

    /** La silueta del bulto de algodón: base ancha y plana, lados panzones y el cuello recogido en el nudo. */
    private fun bulto(w: Float, h: Float, pie: Float): Path = Path().apply {
        moveTo(w * 0.07f, pie)
        cubicTo(w * 0.04f, h * 0.62f, w * 0.12f, h * 0.38f, w * 0.38f, h * 0.31f)
        cubicTo(w * 0.42f, h * 0.27f, w * 0.44f, h * 0.24f, w * 0.43f, h * 0.2f)
        lineTo(w * 0.57f, h * 0.2f)
        cubicTo(w * 0.56f, h * 0.25f, w * 0.59f, h * 0.29f, w * 0.63f, h * 0.31f)
        cubicTo(w * 0.88f, h * 0.38f, w * 0.97f, h * 0.62f, w * 0.93f, pie)
        close()
    }

    /** El bulto envuelto en manta de algodón: la cenefa tejida, los pliegues que bajan del nudo, las orejas y las hojas. */
    private fun DrawScope.bultoAlgodon(w: Float, h: Float, pie: Float, sube: Float, cuantas: Int, semilla: Int, mez: Int, oscuro: Boolean, k: Float, dia: Float, haciaX: Float) {
        val u = density
        val forma = bulto(w, h, pie)
        // Las hojas que asoman por la boca del nudo.
        if (cuantas > 0) hojas(w * 0.38f, w * 0.62f, h * 0.06f - sube * 0.6f, h * 0.27f, cuantas.coerceAtMost(4), semilla, oscuro, k, dia)
        drawPath(forma, n(ALGODON, oscuro, k, dia))
        val cenefa = if ((mez ushr 2) % 2 == 0) AZUL else CINABRIO
        clipPath(forma) {
            // La cenefa de la manta, que da la vuelta ondulada.
            val y0 = h * 0.47f
            val cen = Path().apply {
                moveTo(0f, y0); cubicTo(w * 0.3f, y0 + h * 0.06f, w * 0.7f, y0 + h * 0.06f, w, y0)
                lineTo(w, y0 + h * 0.06f); cubicTo(w * 0.7f, y0 + h * 0.12f, w * 0.3f, y0 + h * 0.12f, 0f, y0 + h * 0.06f); close()
            }
            drawPath(cen, n(cenefa, oscuro, k, dia))
            var x = w * 0.04f
            while (x < w) { drawRect(n(ALGODON, oscuro, k, dia), Offset(x, y0 + h * 0.04f + h * 0.04f * (1f - ((x / w - 0.5f) * 2f).pow(2))), Size(w * 0.012f, h * 0.012f)); x += w * 0.04f }
            // Los pliegues: bajan del nudo y se abren hacia la base.
            for (q in 0 until 6) {
                val fx = -0.3f + 0.12f * q + 0.03f * azar(semilla, q)
                val pl = Path().apply {
                    moveTo(w * (0.5f + fx * 0.25f), h * 0.3f)
                    cubicTo(w * (0.5f + fx * 0.7f), h * 0.5f, w * (0.5f + fx * 1.3f), h * 0.7f, w * (0.5f + fx * 1.5f), pie)
                }
                drawPath(pl, sombra(oscuro, 0.18f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
                drawPath(corrido(pl, -2f * u, 0f), brillo(oscuro, 0.3f, k), style = Stroke(1f * u, cap = StrokeCap.Round))
            }
            drawRect(sombra(oscuro, 0.18f), Offset(0f, pie - h * 0.05f), Size(w, h * 0.05f))
        }
        volumen(forma, oscuro, k, haciaX, fuerza = 1.1f)
        // El nudo y las dos orejas de la manta, que caen a los lados.
        val nudo = Offset(w * 0.5f, h * 0.22f)
        val orejas = orejasBulto(w, h)
        listOf(-1f, 1f).forEachIndexed { i, s ->
            val oreja = orejas[i]
            drawPath(corrido(oreja, 1.5f * u, 2f * u), sombra(oscuro, 0.3f))
            drawPath(oreja, n(ALGODON, oscuro, k, dia))
            drawPath(borde(oreja, 0f, 1.1f * u), n(mezcla(cenefa, ALGODON, 0.4f), oscuro, k, dia))
            drawPath(oreja, if ((s > 0) == (if (oscuro) haciaX < 0 else true)) sombra(oscuro, 0.16f) else brillo(oscuro, 0.2f, k))
        }
        drawOval(n(mezcla(ALGODON, PIEDRA, 0.3f), oscuro, k, dia), Offset(nudo.x - w * 0.05f, nudo.y - h * 0.035f), Size(w * 0.1f, h * 0.075f))
        drawOval(sombra(oscuro, 0.25f), Offset(nudo.x - w * 0.01f, nudo.y - h * 0.01f), Size(w * 0.06f, h * 0.045f))
        drawOval(brillo(oscuro, 0.45f, k), Offset(nudo.x - w * 0.04f, nudo.y - h * 0.03f), Size(w * 0.04f, h * 0.025f))
    }

    /** Las dos orejas de la manta del bulto (la izquierda y la derecha) y el nudo. */
    private fun orejasBulto(w: Float, h: Float): List<Path> {
        val nudo = Offset(w * 0.5f, h * 0.22f)
        return listOf(-1f, 1f).map { s ->
            Path().apply {
                moveTo(nudo.x, nudo.y - h * 0.02f)
                cubicTo(nudo.x + s * w * 0.08f, nudo.y - h * 0.12f, nudo.x + s * w * 0.2f, nudo.y - h * 0.06f, nudo.x + s * w * 0.22f, nudo.y + h * 0.02f)
                cubicTo(nudo.x + s * w * 0.15f, nudo.y + h * 0.0f, nudo.x + s * w * 0.08f, nudo.y + h * 0.02f, nudo.x, nudo.y + h * 0.03f)
                close()
            }
        } + Path().apply { addOval(Rect(nudo.x - w * 0.05f, nudo.y - h * 0.035f, nudo.x + w * 0.05f, nudo.y + h * 0.04f)) }
    }

    /** La silueta de la jícara: media calabaza, ancha y baja. */
    private fun jicara(w: Float, h: Float, pie: Float): Path = Path().apply {
        moveTo(w * 0.1f, h * 0.5f)
        cubicTo(w * 0.1f, pie - h * 0.02f, w * 0.3f, pie, w * 0.5f, pie)
        cubicTo(w * 0.7f, pie, w * 0.9f, pie - h * 0.02f, w * 0.9f, h * 0.5f)
        close()
    }

    /** Lo que no tiene tema: una jícara abierta con las hojas de amate sueltas, unas de pie y otras caídas por el borde. */
    private fun DrawScope.jicaraSuelta(w: Float, h: Float, pie: Float, cuantas: Int, semilla: Int, ab: Float, oscuro: Boolean, k: Float, dia: Float, haciaX: Float) {
        val u = density
        val forma = jicara(w, h, pie)
        val boca = Rect(w * 0.1f, h * 0.45f, w * 0.9f, h * 0.55f)
        drawOval(n(mezcla(BARRO_OSC, NEGRO, 0.4f), oscuro, k), boca.topLeft, boca.size)
        hojas(w * 0.16f, w * 0.84f, h * 0.18f - ab * h * 0.1f, h * 0.52f, cuantas.coerceAtLeast(2), semilla, oscuro, k, dia)
        // Una hoja que se salió y cuelga por el borde.
        val caida = poli(w * 0.78f, h * 0.48f, w * 0.95f, h * 0.52f, w * 0.93f, h * 0.7f, w * 0.8f, h * 0.62f)
        drawPath(corrido(caida, 1.5f * u, 2f * u), sombra(oscuro, 0.3f))
        drawPath(caida, n(AMATE_CLARO, oscuro, k, dia))
        drawPath(forma, n(Color(0xFF7A3A22), oscuro, k, dia))
        clipPath(forma) {
            // La jícara laqueada: un filete amarillo y unas volutas rojas, gastadas.
            drawRect(n(OCRE, oscuro, k, dia), Offset(0f, h * 0.53f), Size(w, 2f * u))
            for (q in 0 until 4) drawArc(n(CINABRIO, oscuro, k, dia), 180f, 200f, false, Offset(w * (0.18f + 0.18f * q), h * 0.56f), Size(w * 0.1f, h * 0.06f), style = Stroke(1.2f * u))
        }
        volumen(forma, oscuro, k, haciaX, lustre = 0.3f)
        drawOval(brillo(oscuro, 0.4f, k), boca.topLeft, boca.size, style = Stroke(1f * u))
    }

    /** Un hueco sin tema: la grada con el cerco limpio donde hubo una petaca, y un caracol o una mazorca seca. */
    private fun DrawScope.vacio(semilla: Int, pie: Float, oscuro: Boolean, k: Float, dia: Float, haciaX: Float) {
        val u = density
        val w = size.width; val h = size.height
        // El cerco: el estuco más limpio donde estuvo la petaca, con un borde de polvo.
        val cerco = Rect(w * 0.14f, pie - w * 0.014f, w * 0.78f, pie + w * 0.012f)
        drawRect(n(mezcla(ESTUCO, LUZ_MUEBLE, 0.3f), oscuro, k, dia).copy(alpha = 0.45f), cerco.topLeft, cerco.size)
        drawRect(n(mezcla(ESTUCO, PIEDRA, 0.6f), oscuro, k).copy(alpha = 0.5f), cerco.topLeft, cerco.size, style = Stroke(1f * u))
        if (Math.floorMod(semilla / 7919, 2) == 0) {
            // Un caracol marino (de los que se soplan), tendido: la espira con sus vueltas, la vuelta grande con sus nudos y el
            // labio abierto, rosado por dentro.
            val c = Offset(w * 0.5f, pie - w * 0.085f)
            val L = w * 0.5f
            drawOval(sombra(oscuro, 0.4f), Offset(c.x - L * 0.42f, pie - w * 0.022f), Size(L * 1.0f, w * 0.034f))
            val espira = poli(c.x - L * 0.52f, c.y + w * 0.012f, c.x - L * 0.18f, c.y - w * 0.06f, c.x - L * 0.12f, c.y + w * 0.07f)
            drawPath(espira, n(mezcla(ESTUCO, OCRE, 0.35f), oscuro, k, dia))
            clipPath(espira) {
                for (q in 0 until 4) {
                    val x = c.x - L * (0.44f - q * 0.08f)
                    drawLine(n(mezcla(OCRE, BARRO, 0.5f), oscuro, k), Offset(x, c.y - w * 0.08f), Offset(x + L * 0.03f, c.y + w * 0.08f), 1.4f * u)
                }
                drawRect(sombra(oscuro, 0.2f), Offset(c.x - L * 0.6f, c.y + w * 0.02f), Size(L * 0.6f, w * 0.06f))
            }
            val cuerpo = Path().apply {
                moveTo(c.x - L * 0.2f, c.y - w * 0.05f)
                cubicTo(c.x, c.y - w * 0.1f, c.x + L * 0.25f, c.y - w * 0.08f, c.x + L * 0.42f, c.y - w * 0.05f)
                cubicTo(c.x + L * 0.5f, c.y + w * 0.0f, c.x + L * 0.35f, c.y + w * 0.075f, c.x + L * 0.05f, c.y + w * 0.075f)
                cubicTo(c.x - L * 0.12f, c.y + w * 0.075f, c.x - L * 0.2f, c.y + w * 0.04f, c.x - L * 0.2f, c.y - w * 0.05f)
                close()
            }
            drawPath(cuerpo, n(mezcla(ESTUCO, OCRE, 0.25f), oscuro, k, dia))
            clipPath(cuerpo) {
                for (q in 0 until 4) drawArc(n(mezcla(OCRE, BARRO, 0.4f), oscuro, k).copy(alpha = 0.7f), 190f, 150f, false,
                    Offset(c.x - L * 0.2f + q * L * 0.15f, c.y - w * 0.07f), Size(L * 0.2f, w * 0.12f), style = Stroke(1.3f * u))
                // El labio abierto, con lo rosado de dentro.
                drawOval(n(Color(0xFFE59A7E), oscuro, k, dia), Offset(c.x + L * 0.02f, c.y - w * 0.005f), Size(L * 0.42f, w * 0.07f))
                drawOval(n(Color(0xFFF2C4A6), oscuro, k, dia), Offset(c.x + L * 0.08f, c.y + w * 0.008f), Size(L * 0.28f, w * 0.035f))
            }
            // Los nudos de la vuelta grande.
            for (q in 0 until 5) {
                val p = Offset(c.x - L * 0.12f + q * L * 0.11f, c.y - w * (0.072f + 0.012f * sin(q * 1.2f)))
                drawCircle(sombra(oscuro, 0.3f), w * 0.011f, p + Offset(1.4f * u, 1.4f * u))
                drawCircle(n(mezcla(ESTUCO, OCRE, 0.2f), oscuro, k, dia), w * 0.011f, p)
                drawCircle(brillo(oscuro, 0.5f, k), w * 0.004f, p - Offset(w * 0.004f, w * 0.004f))
            }
            volumen(cuerpo, oscuro, k, haciaX, lustre = 0.35f)
        } else {
            // Una mazorca seca, con las hojas abiertas hacia atrás.
            val c = Offset(w * 0.5f, pie - w * 0.05f)
            val L = w * 0.34f
            drawOval(sombra(oscuro, 0.4f), Offset(c.x - L * 0.5f, pie - w * 0.02f), Size(L * 1.25f, w * 0.03f))
            listOf(-1f, 1f).forEach { s ->
                val hoja = Path().apply {
                    moveTo(c.x + L * 0.3f, c.y)
                    cubicTo(c.x + L * 0.5f, c.y + s * w * 0.06f, c.x + L * 0.7f, c.y + s * w * 0.04f, c.x + L * 0.78f, c.y + s * w * 0.01f + w * 0.02f)
                    cubicTo(c.x + L * 0.6f, c.y + s * w * 0.015f, c.x + L * 0.45f, c.y, c.x + L * 0.3f, c.y)
                    close()
                }
                drawPath(hoja, n(mezcla(PALMA_LUZ, PALMA, 0.3f), oscuro, k, dia))
                drawPath(borde(hoja, 0f, -1.5f * u), sombra(oscuro, 0.25f))
            }
            val mazorca = Path().apply { addOval(Rect(c.x - L * 0.5f, c.y - w * 0.04f, c.x + L * 0.38f, c.y + w * 0.04f)) }
            drawPath(mazorca, n(Color(0xFFC98F3A), oscuro, k, dia))
            clipPath(mazorca) {
                for (fila in 0 until 4) {
                    var x = c.x - L * 0.5f + (fila % 2) * w * 0.008f
                    while (x < c.x + L * 0.38f) {
                        drawOval(n(if (azar(semilla, (x * 10).toInt() + fila) < 0.2f) ALMAGRE else Color(0xFFDDA54E), oscuro, k, dia), Offset(x, c.y - w * 0.04f + fila * w * 0.02f), Size(w * 0.014f, w * 0.016f))
                        x += w * 0.017f
                    }
                }
            }
            volumen(mazorca, oscuro, k, haciaX)
        }
    }

    override val rotulo = Rect(0.045f, 0.585f, 0.955f, 0.83f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.codice
    override val salida = 0f
    /** La hoja de amate apoyada contra la grada, entre el petate y el brasero. */
    override val placa = Rect(0.355f, 0.33f, 0.645f, 0.72f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    // ---------------------------------------------------------------- el interior y las fichas (no cambian)

    /**
     * El tejido de palma en diagonal, recortado a [r]: tiras claras en un sentido y oscuras en el otro, con el filo de cada
     * tira marcado. Son pocas líneas gruesas (no un cuadrito por cruce), para que se dibuje rápido.
     */
    private fun DrawScope.tejido(r: Rect, celda: Float, oscuro: Boolean, semilla: Int) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(t(PALMA, oscuro), r.topLeft, r.size)
            val largo = r.width + r.height
            var k = 0
            var o = -r.height
            while (o < r.width) {
                val a = Offset(r.left + o, r.top); val b = Offset(r.left + o + r.height, r.bottom)
                val a2 = Offset(r.left + o + r.height, r.top); val b2 = Offset(r.left + o, r.bottom)
                drawLine(t(PALMA_LUZ, oscuro).copy(alpha = 0.45f + 0.2f * azar(semilla, k)), a, b, celda * 0.8f)
                drawLine(t(PALMA_OSC, oscuro).copy(alpha = 0.3f + 0.15f * azar(semilla, k + 50)), a2, b2, celda * 0.45f)
                drawLine(t(PALMA_OSC, oscuro).copy(alpha = 0.5f), Offset(a.x + celda * 0.57f, a.y), Offset(b.x + celda * 0.57f, b.y), 0.6f * density)
                o += celda * 1.4f; k++
                if (k > largo) break
            }
        }
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo tejido de la petaca, en celdas más grandes, con las paredes alrededor y su sombra.
        tejido(Rect(0f, 0f, w, h), 14 * u, oscuro, 7)
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(PALMA_OSC, oscuro).copy(alpha = 0.7f), r.topLeft, r.size)
        }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 22 * u, 0.32f)
        // Una franja teñida de rojo que corre por el fondo.
        drawRect(t(CINABRIO, oscuro).copy(alpha = 0.18f), Offset(pl, h * 0.48f), Size(w - 2 * pl, 18 * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2E6CF) else NEGRO
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 28, 0.012f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El borde donde no llegó el estuco: se ve la corteza de amate, con sus fibras.
        val b = 3.2f * u * g
        drawRect(AMATE, Offset.Zero, Size(w, h), style = Stroke(b * 2))
        for (k in 0 until (if (grande) 60 else 24)) {
            val lado = k % 4
            val f = azar(semilla, k)
            val (a, z) = when (lado) {
                0 -> Offset(w * f, 0f) to Offset(w * f + 12 * u, b * 1.5f)
                1 -> Offset(w, h * f) to Offset(w - b * 1.5f, h * f + 12 * u)
                2 -> Offset(w * f, h) to Offset(w * f - 12 * u, h - b * 1.5f)
                else -> Offset(0f, h * f) to Offset(b * 1.5f, h * f - 12 * u)
            }
            drawLine(AMATE_OSC.copy(alpha = 0.6f), a, z, 0.7f * u)
        }
        // El filete rojo de la página de códice.
        drawRect(CINABRIO.copy(alpha = 0.85f), Offset(b + 3 * u, b + 3 * u), Size(w - 2 * b - 6 * u, h - 2 * b - 6 * u), style = Stroke(1.2f * u * g))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 16.dp, 24.dp, 30.dp, 32.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.95f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La barra y el punto: un número asentado en el códice.
            "hecho" -> { drawRoundRect(CINABRIO, Offset(c.x - s * 0.4f, c.y + s * 0.06f), Size(s * 0.8f, s * 0.2f), androidx.compose.ui.geometry.CornerRadius(s * 0.06f)); drawCircle(CINABRIO, s * 0.14f, Offset(c.x, c.y - s * 0.22f)) }
            // Una cuenta de jade.
            "convence" -> { drawCircle(JADE, s * 0.38f, c); drawCircle(Color.White.copy(alpha = 0.4f), s * 0.12f, Offset(c.x - s * 0.12f, c.y - s * 0.12f)) }
            // La voluta de la palabra: lo que se dice y se piensa.
            "piensa" -> drawPath(Path().apply {
                moveTo(c.x - s * 0.4f, c.y + s * 0.3f); cubicTo(c.x - s * 0.4f, c.y - s * 0.5f, c.x + s * 0.45f, c.y - s * 0.45f, c.x + s * 0.35f, c.y + s * 0.05f)
                cubicTo(c.x + s * 0.28f, c.y + s * 0.3f, c.x - s * 0.05f, c.y + s * 0.2f, c.x + s * 0.05f, c.y - s * 0.05f)
            }, AZUL, style = Stroke(s * 0.11f, cap = StrokeCap.Round))
            "duda" -> drawCircle(p.tenue, s * 0.32f, c, style = Stroke(s * 0.12f))
            "desacuerdo" -> { drawLine(CINABRIO, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.13f); drawLine(CINABRIO, Offset(c.x + s * 0.34f, c.y - s * 0.34f), Offset(c.x - s * 0.34f, c.y + s * 0.34f), s * 0.13f) }
            else -> {}
        }
    }

    /** Una pluma de quetzal: larga, verde, con su raquis. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        rotate(-35f, c) {
            drawOval(QUETZAL, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.9f))
            drawLine(Color(0xFF0E5A38), Offset(c.x, c.y - s * 0.46f), Offset(c.x, c.y + s * 0.5f), s * 0.05f)
        }
    }

    override val desorden = 1f
}
