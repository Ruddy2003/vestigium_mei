package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Ruta de la Seda
// =====================================================================

/**
 * Ruta de la Seda: el cuarto de Nanai-vandak, mercader sogdiano, en un caravasar hacia el año 750, la noche antes de salir.
 * Es un interior sin ventana ni puerta: un muro de adobe enlucido con barro (la paja que asoma, grietas, desconchones donde
 * se ve el ladrillo cocido, un friso pintado de almagre con medallones de perlas, gastado) y la luz de día que entra de
 * arriba a la izquierda, fuera del cuadro. El mueble es un nicho de arco apuntado abierto en el muro y enmarcado de ladrillo
 * cocido, con repisas de tablón de álamo seco y agrietado empotradas en él, sus ménsulas y un poste al medio. Arriba, el
 * punto focal a la izquierda: la balanza de platillos de bronce colgada de su gancho, con un poco de azafrán en un platillo,
 * y debajo el estuche de las pesas abierto con tres pesas; al centro, rollos de seda apilados (añil, azafrán y verde) y,
 * delante, uno carmesí a medio desenrollar cuyo paño cae por el canto de la repisa; a la derecha, la jarra de plata de pico
 * de ave, una sarta de monedas de agujero cuadrado que cuelga del canto y la lámpara de aceite vidriada de turquesa sobre su
 * pie de bronce, con el hollín encima. En el muro, la campana de camello con su correa colgada de una estaca, sobre el
 * arco, y el odre de agua colgado al otro lado. Al pie, sobre el suelo de tierra, la tarima de álamo con la alforja de kilim
 * a medio cargar (dos bolsas unidas por una banda: una encima, abierta, y la otra colgando por el frente con su solapa
 * en pico, su cierre de lazo y sus borlas; al lado, la ropa doblada que falta meter) y
 * un fardo de yute con el sello de arcilla del mercader, cortado por el borde derecho. Cada tema es, según la semilla, una
 * caja de laca roja con volutas y filetes de oro, un estuche cilíndrico de cuero o de bambú tumbado con su tapa y su cordón,
 * o un fardo de seda envuelto en tela basta y atado, con su tablilla de madera; en las tres asoman tiras de bambú (más con
 * más fichas) y el tema va en una tira de papel o en la tablilla. Lo que no tiene tema es un cesto con tiras sueltas y la
 * tablilla sin escribir; un hueco libre lleva un cuenco de barro o unas monedas sueltas con su bolsita. De noche todo se
 * hunde en azul hondo con pardo: alumbra solo la lámpara (un halo cálido en el fondo del nicho, el filo en la plata, el
 * bronce y la seda), las filas de arriba encendidas y las de abajo casi perdidas. Por dentro, laca negra con nubes de oro.
 * Las fichas son tiras de bambú atadas con cordel.
 */
object IdeasSeda : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del cuarto
    /** El enlucido de barro del muro y su sombra; el ladrillo cocido que asoma. */
    private val ADOBE = Color(0xFFC9A478)
    private val ADOBE_OSC = Color(0xFF94714E)
    private val LADRILLO = Color(0xFFAE6E48)
    /** El álamo seco de las repisas y la tarima: su luz y su sombra. */
    private val ALAMO = Color(0xFFB39470)
    private val ALAMO_LUZ = Color(0xFFDCC59C)
    private val ALAMO_OSC = Color(0xFF6E5640)
    /** Las sedas (los colores salen del cuadro del tema). */
    private val CARMESI = Color(0xFFA8283A)
    private val AZAFRAN = Color(0xFFD99A30)
    private val VERDE = Color(0xFF5C8550)
    private val INDIGO = Color(0xFF34477A)
    /** Los metales, el cuero y la tela basta. */
    private val BRONCE = Color(0xFFB4863C)
    private val PLATA = Color(0xFFC6C8CC)
    private val CUERO = Color(0xFF85522E)
    private val YUTE = Color(0xFFB39A6E)
    /** La llama de la lámpara, el hollín y la noche del caravasar: azul hondo con pardo. */
    private val LLAMA = Color(0xFFFFC56E)
    private val HOLLIN = Color(0xFF2E2622)
    private val NOCHE = Color(0xFF17161F)
    /**
     * El papel de noche lejos de la lámpara: con [TINTA] da unos 5.7:1 en el título y 4.6:1 en la cuenta (al 78 %); cerca de
     * la lámpara sube hacia el papel con un poco de llama.
     */
    private val PAPEL_LEJOS = Color(0xFF9E977E)

    // Los de siempre (la laca, el oro, el papel, el bambú y la tinta): también los usan el interior y las fichas.
    private val TURQUESA = Color(0xFF1A8C8C)
    private val LACA = Color(0xFFA8261C)
    private val LACA_NEGRA = Color(0xFF1C1414)
    private val ORO = Color(0xFFD0A84A)
    private val BAMBU = Color(0xFFD9C38E)
    private val PAPEL = Color(0xFFF6EFDC)
    private val TINTA = Color(0xFF1F1B3A)
    private val CUERDA = Color(0xFFC9A86A)
    private val BAMBU_VIEJO = Color(0xFF4E3A22)

    /** El almagre del friso y el fondo del nicho: variaciones de la paleta. */
    private val ALMAGRE = mezcla(LACA, ADOBE_OSC, 0.4f)
    private val NICHO = mezcla(mezcla(ADOBE, ADOBE_OSC, 0.2f), Color(0xFFB4ADA6), 0.45f)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.55f
    override val corona = 0.64f
    override val base = 0.3f
    override val lado = 0.085f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** El nicho: sus costados en fracción del ancho, el ancho del marco de ladrillo, la clave y la flecha del arco (fracción de la corona). */
    private const val NL = 0.045f
    private const val NR = 0.955f
    private const val BANDA = 0.03f
    private const val ARCO = 0.17f
    private const val FLECHA = 0.34f
    /** La repisa de arriba: el fondo de su tabla (contra el muro) y su canto, en fracción de la corona. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.87f
    /** En la base: el fondo del nicho, el rincón con el suelo, la tarima (su tabla, su frente, sus pies) y dónde pisa lo del suelo. */
    private const val ALFEIZAR = 0.06f
    private const val SUELO = 0.4f
    private const val TT = 0.34f
    private const val TF = 0.44f
    private const val TB = 0.71f
    private const val PISAN = 0.76f
    /** El piso de cada hueco (la tabla) y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.88f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la lámpara de aceite, en fracciones del ancho. */
    private val LUZ = Offset(0.808f, 0.425f)

    /** Cuánto le llega de la lámpara a un punto [p] (en fracciones del ancho): cae con la distancia; abajo a la izquierda, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 1.3f else 1f)
        val radial = 1.5f * kotlin.math.exp(-2.2f * kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que la lámpara echa hacia abajo sobre las repisas: cae de fila en fila (la primera, encendida; la segunda, a
     * medias; la tercera, tenue; la cuarta y la quinta, casi perdidas), menos en la columna de la izquierda. Los puntos son
     * los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.5f to 0.9f, 0.81f to 0.76f, 1.1f to 0.5f, 1.4f to 0.25f, 1.69f to 0.08f, 1.99f to 0.04f, 2.4f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.9f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.5f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.1f + NOCHE.red * 0.75f, c.green * 0.1f + NOCHE.green * 0.75f, c.blue * 0.1f + NOCHE.blue * 0.75f, c.alpha)

    /** Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia el suyo según [k]. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.14f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del cuarto. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    /** De noche la luz viene de la lámpara: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.02f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en el muro: de día corrida a la derecha y abajo; de noche, hacia fuera de la lámpara, tanto más
     * marcada cuanto más cerca está de ella. No baja de [hasta] (la repisa o el piso que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.085f * dx, w * 0.022f) { drawPath(p, NOCHE.copy(alpha = (0.55f * k).coerceAtMost(0.42f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la repisa, la tarima o el suelo, corrida a la derecha (de noche, hacia la izquierda). */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) {
        val corre = if (esNoche && centro.x / anchoW < LUZ.x) -0.58f else -0.42f
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x + ancho * corre, centro.y - alto / 2), Size(ancho, alto))
    }

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un cuerpo tumbado dentro de [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.tumbado(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.18f * fuerza), r.topLeft, Size(r.width, r.height * 0.32f))
        drawRect(t.b(0.3f * fuerza), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, r.height * 0.08f))
        drawRect(t.s(0.16f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(t.s(0.2f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** El filo cálido de la lámpara en algo, de noche: el canto de arriba y el costado que la mira ([der]). De día, nada. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello de la lámpara en el metal o la laca, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Una moneda de bronce de agujero cuadrado, vista de frente ([plana]: tumbada, achatada). */
    private fun DrawScope.moneda(c: Offset, r: Float, t: Tono, s: Int, plana: Boolean = false) {
        val u = density
        val col = mezcla(BRONCE, VERDE, 0.15f + 0.3f * azar(s, 1))
        val alto = if (plana) r * 0.55f else r
        drawOval(t.s(0.3f), Offset(c.x - r + r * 0.3f, c.y - alto + r * 0.25f), Size(2 * r, 2 * alto))
        drawOval(t.c(col), Offset(c.x - r, c.y - alto), Size(2 * r, 2 * alto))
        drawOval(t.c(mezcla(col, HOLLIN, 0.25f)), Offset(c.x - r, c.y - alto), Size(2 * r, 2 * alto), style = Stroke(0.6f * u))
        drawArc(t.b(0.55f), 190f, 80f, false, Offset(c.x - r * 0.85f, c.y - alto * 0.85f), Size(r * 1.7f, alto * 1.7f), style = Stroke(0.6f * u))
        val q = r * 0.32f
        drawRect(t.c(mezcla(HOLLIN, col, 0.2f)), Offset(c.x - q, c.y - q * alto / r), Size(2 * q, 2 * q * alto / r))
    }

    /**
     * Un relleno de pared o de ladrillo: de día con la luz de la izquierda; de noche, en círculos alrededor de la lámpara (un
     * solo degradado radial por superficie, no decenas).
     */
    private fun DrawScope.relleno(p: Path, col: Color) {
        val w = anchoW
        if (!esNoche) drawPath(p, Brush.horizontalGradient(listOf(mezcla(col, LUZ_MUEBLE, 0.2f), col, mezcla(col, SOMBRA_MUEBLE, 0.16f)), 0f, w))
        else {
            fun k(d: Float) = Tono(true, luz(LUZ + Offset(d, 0f))).c(col)
            drawPath(p, Brush.radialGradient(0f to k(0.03f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f))
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val suelo = b.top + hb * SUELO
        val fondoNicho = b.top + hb * ALFEIZAR
        val primera = d.huecos.first().top

        // ---- el muro, el friso, el suelo
        pared(w, ch, suelo)
        friso(w, ch)
        piso(w, suelo, alto)

        // ---- el nicho con su marco de ladrillo, sus repisas y el poste
        nicho(d, w, ch, fondoNicho)

        // ---- en el muro: la campana de camello y el odre
        campana(Offset(w * 0.072f, ch * 0.16f))
        odre(Offset(w * 0.935f, ch * 0.15f))

        // ---- sobre la repisa de arriba, de atrás adelante
        val pieA = ch * SUP + w * 0.012f
        val pieF = ch * FRENTE - w * 0.004f
        pesas(Offset(w * 0.06f, ch * 0.845f))
        latigo(Offset(w * 0.585f, ch * 0.29f), pieA)
        balanza(Offset(w * 0.25f, ch * 0.255f), pieA)
        sedas(w, pieA, pieF, primera)
        jarra(Offset(w * 0.735f, ch * 0.848f), pieA)
        lampara(Offset(w * 0.865f, ch * 0.852f))
        monedas(w, ch * FRENTE, primera)

        // ---- al pie: la tarima con la alforja, y el fardo cortado por el borde
        tarima(b, w, suelo)
        alforja(b, w)
        fardo(Offset(w * 0.8f, b.top + hb * 0.95f), w)

        // ---- la placa de «y N más»: una tablilla clavada al frente de la tarima
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRoundRect(if (o) PAPEL_LEJOS else ALAMO_LUZ, r.topLeft, r.size, CornerRadius(2f * u))
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            listOf(r.left + 3f * u, r.right - 3f * u).forEach { x -> drawCircle(t.c(HOLLIN), 1.1f * u, Offset(x, r.center.y)); drawCircle(t.b(0.4f), 0.4f * u, Offset(x - 0.4f * u, r.center.y - 0.4f * u)) }
        }

        // ---- la luz de la lámpara, por encima de lo que tiene cerca
        val llama = LUZ * w
        if (o) {
            mancha(llama + Offset(-w * 0.12f, -w * 0.04f), w * 0.62f, LLAMA.copy(alpha = 0.16f))
            mancha(llama + Offset(0f, w * 0.01f), w * 0.24f, LLAMA.copy(alpha = 0.3f))
            mancha(llama, w * 0.06f, Color(0xFFFFE6B0).copy(alpha = 0.45f))
            llamaLampara(llama)
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El muro de adobe enlucido con barro: la mancha de luz arriba a la izquierda (de noche, el halo de la lámpara),
     * pinceladas, la paja que asoma, grietas, y un desconchón donde se ve el ladrillo cocido.
     */
    private fun DrawScope.pared(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        relleno(Path().apply { addRect(Rect(0f, 0f, w, suelo)) }, ADOBE)
        if (!o) {
            mancha(Offset(w * 0.04f, 0f), w * 0.7f, luzMueble(0.36f))
            drawRect(sombraMueble(0.08f), Offset(0f, ch * 1.6f), Size(w, suelo - ch * 1.6f))
        }
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else ADOBE_OSC, 5001, 46, u, 0.1f)
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else mezcla(ADOBE, LUZ_MUEBLE, 0.45f), 5002, 20, u, 0.1f)
        // La paja del adobe: briznas claras y huecos oscuros.
        for (k in 0 until 64) {
            val p = Offset(w * azar(5003, k), suelo * azar(5004, k))
            val a = azar(5005, k) * 3.14f
            val l = (2.2f + 3.5f * azar(5006, k)) * u
            val t = tn(p)
            val q = Offset(p.x + l * cos(a), p.y + l * sin(a))
            if (k % 3 == 0) drawLine(t.s(0.3f), p, q, 0.7f * u, StrokeCap.Round)
            else drawLine(t.c(mezcla(ADOBE, PAPEL, 0.6f)).copy(alpha = 0.75f), p, q, 0.6f * u, StrokeCap.Round)
        }
        // Grietas del enlucido.
        listOf(Offset(0.3f, 0.11f), Offset(0.62f, 0.13f), Offset(0.985f, 0.55f), Offset(0.01f, 0.7f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, ch * f.y)
            val t = tn(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.006f + 0.012f * azar(5010 + i, j)) * (if (j % 2 == 0) 1f else -0.5f) * (if (f.x > 0.9f) -1f else 1f), w * (0.01f + 0.01f * azar(5020 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.3f), style = Stroke(0.8f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.3f), style = Stroke(0.5f * u)) }
        }
    }

    /** Un desconchón del enlucido: el ladrillo cocido que asoma con sus juntas, y el canto del barro alrededor. */
    private fun DrawScope.desconchon(r: Rect, s: Int) {
        val u = density
        val t = tn(r.center)
        val hueco = contornoIrregular(r, s, 14, 0.22f)
        translate(-1f * u, -1.2f * u) { drawPath(hueco, t.b(0.45f)) }
        drawPath(hueco, t.c(mezcla(ADOBE_OSC, HOLLIN, 0.25f)))
        clipPath(hueco) {
            val la = anchoW * 0.016f; val ll = anchoW * 0.05f
            var y = r.top; var f = 0
            while (y < r.bottom) {
                var x = r.left - (if (f % 2 == 0) 0f else ll / 2)
                var k = 0
                while (x < r.right) {
                    drawRect(t.c(mezcla(LADRILLO, CARMESI, 0.15f * azar(s + f, k))), Offset(x + 1f * u, y + 1f * u), Size(ll - 2f * u, la - 2f * u))
                    drawRect(t.b(0.2f), Offset(x + 0.8f * u, y + 0.8f * u), Size(ll - 1.6f * u, 0.7f * u))
                    x += ll; k++
                }
                y += la; f++
            }
            drawRect(t.s(0.3f), r.topLeft, Size(r.width, 2f * u))
            drawRect(t.s(0.2f), r.topLeft, Size(2f * u, r.height))
        }
        drawPath(hueco, t.s(0.35f), style = Stroke(0.8f * u))
    }

    /**
     * El friso pintado de almagre de arriba: dos filetes, medallones de perlas con una roseta, la pintura saltada en varios
     * sitios y más gastada hacia la derecha.
     */
    private fun DrawScope.friso(w: Float, ch: Float) {
        val u = density
        val y0 = ch * 0.03f; val y1 = ch * 0.105f
        val hf = y1 - y0
        val n = 12
        for (i in 0 until n) {
            val x0 = w * i / n
            val t = tn(Offset(x0 + w / n / 2, (y0 + y1) / 2))
            drawRect(t.c(ALMAGRE), Offset(x0, y0), Size(w / n + 1f, hf))
            drawRect(t.c(mezcla(HOLLIN, ALMAGRE, 0.3f)), Offset(x0, y0), Size(w / n + 1f, 1.4f * u))
            drawRect(t.c(mezcla(HOLLIN, ALMAGRE, 0.3f)), Offset(x0, y1 - 1.4f * u), Size(w / n + 1f, 1.4f * u))
            drawRect(t.c(PAPEL).copy(alpha = 0.6f), Offset(x0, y0 + 2.4f * u), Size(w / n + 1f, 0.7f * u))
            drawRect(t.c(PAPEL).copy(alpha = 0.6f), Offset(x0, y1 - 3.1f * u), Size(w / n + 1f, 0.7f * u))
        }
        // Los medallones de perlas, cada uno con su roseta.
        val paso = w / 6.5f
        var x = paso * 0.45f; var j = 0
        while (x < w) {
            val c = Offset(x, (y0 + y1) / 2)
            val t = tn(c)
            val r = hf * 0.34f
            drawCircle(t.c(mezcla(ALMAGRE, HOLLIN, 0.2f)), r * 1.05f, c)
            for (q in 0 until 12) {
                val a = q * 0.5236f
                drawCircle(t.c(PAPEL), hf * 0.045f, Offset(c.x + r * cos(a), c.y + r * sin(a)))
            }
            for (q in 0 until 4) {
                val a = q * 1.5708f + 0.785f
                drawOval(t.c(if (j % 2 == 0) TURQUESA else AZAFRAN), Offset(c.x + r * 0.38f * cos(a) - r * 0.2f, c.y + r * 0.38f * sin(a) - r * 0.2f), Size(r * 0.4f, r * 0.4f))
            }
            drawCircle(t.c(PAPEL), r * 0.14f, c)
            // Entre medallón y medallón, una palmeta.
            val pc = Offset(x + paso / 2, c.y)
            drawPath(poligono(pc.x, pc.y - hf * 0.26f, pc.x + hf * 0.14f, pc.y, pc.x, pc.y + hf * 0.26f, pc.x - hf * 0.14f, pc.y), t.c(mezcla(PAPEL, AZAFRAN, 0.3f)).copy(alpha = 0.7f))
            x += paso; j++
        }
        // Lo que se gastó: más hacia la derecha, y unos saltos donde asoma el barro.
        for (i in 0 until n) {
            val x0 = w * i / n
            val t = tn(Offset(x0, y0))
            drawRect(t.c(ADOBE).copy(alpha = 0.32f * i / n), Offset(x0, y0), Size(w / n + 1f, hf))
        }
        listOf(Rect(0.2f, 0.1f, 0.245f, 0.7f), Rect(0.55f, 0.35f, 0.585f, 1.05f), Rect(0.86f, 0.0f, 0.93f, 0.8f)).forEachIndexed { i, f ->
            val r = Rect(w * f.left, y0 + hf * f.top, w * f.right, y0 + hf * f.bottom)
            val t = tn(r.center)
            val salto = contornoIrregular(r, 5040 + i, 12, 0.3f)
            drawPath(salto, t.c(mezcla(ADOBE, ADOBE_OSC, 0.25f)))
            clipPath(salto) { pinceladas(r, if (esNoche) NOCHE else ADOBE_OSC, 5045 + i, 4, u, 0.2f) }
            translate(0.8f * u, 1f * u) { drawPath(salto, t.s(0.3f), style = Stroke(0.7f * u)) }
        }
    }

    /** El suelo de tierra apisonada: más oscuro, con pinceladas, briznas de paja y la sombra del rincón. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        drawRect(t.c(mezcla(ADOBE_OSC, ADOBE, 0.35f)), Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else ADOBE_OSC, 5050, 22, u, 0.16f)
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else mezcla(ADOBE, LUZ_MUEBLE, 0.3f), 5051, 10, u, 0.12f)
        for (k in 0 until 14) {
            val p = Offset(w * azar(5052, k), suelo + (alto - suelo) * (0.15f + 0.8f * azar(5053, k)))
            val a = azar(5054, k) * 3.14f
            drawLine(t.c(mezcla(ADOBE, PAPEL, 0.5f)).copy(alpha = 0.7f), p, p + Offset(4f * u * cos(a), 4f * u * sin(a)), 0.7f * u)
        }
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(tt.s(0.35f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- el nicho

    /**
     * El nicho: el marco de ladrillo cocido (dovelas en el arco, hiladas en las jambas) con restos de enlucido; dentro, el
     * fondo de barro con la sombra que le echa el arco, los costados (la jamba de la izquierda en sombra, la de la derecha
     * con luz), el hollín sobre la lámpara, el poste, las repisas de tablón con sus grietas y sus ménsulas.
     */
    private fun DrawScope.nicho(d: Disposicion, w: Float, ch: Float, fondo: Float) {
        val u = density
        val o = esNoche
        val e = w * BANDA
        val top = ch * ARCO; val fl = ch * FLECHA
        val arranque = top + fl
        val fuera = arcoApuntado(Rect(w * NL - e, top - e, w * NR + e, fondo), fl + e * 0.25f)
        val nicho = arcoApuntado(Rect(w * NL, top, w * NR, fondo), fl)
        // La sombra del marco en el muro y el desconchón de abajo a la derecha.
        if (!o) translate(w * 0.012f, w * 0.008f) { drawPath(fuera, sombraMueble(0.14f)) }
        desconchon(Rect(w * 0.58f, fondo + w * 0.01f, w * 0.78f, fondo + w * 0.07f), 5060)
        // El marco de ladrillo cocido.
        relleno(fuera, LADRILLO)
        clipPath(fuera) {
            clipPath(nicho, clipOp = ClipOp.Difference) {
                // Las dovelas del arco: juntas que salen del centro.
                val cen = Offset(w * 0.5f, arranque + w * 0.12f)
                clipRect(0f, 0f, w, arranque) {
                    for (k in 0..40) {
                        val a = Math.toRadians(180.0 + k * 4.5).toFloat()
                        val p1 = Offset(cen.x + w * 0.8f * cos(a), cen.y + w * 0.8f * sin(a))
                        val t = tn(Offset(cen.x + w * 0.45f * cos(a), cen.y + w * 0.3f * sin(a)))
                        drawLine(t.s(0.45f), cen, p1, 1f * u)
                        drawLine(t.b(0.2f), cen + Offset(1f * u, 0f), p1 + Offset(1f * u, 0f), 0.6f * u)
                    }
                }
                // Las hiladas de las jambas.
                var y = arranque; var f = 0
                while (y < fondo) {
                    val t = tn(Offset(w * 0.5f, y))
                    drawRect(t.s(0.45f), Offset(0f, y), Size(w, 1f * u))
                    drawRect(t.b(0.2f), Offset(0f, y + 1f * u), Size(w, 0.6f * u))
                    val dx = if (f % 2 == 0) e * 0.5f else 0f
                    listOf(w * NL - e + dx, w * NR + dx).forEach { x -> drawRect(t.s(0.35f), Offset(x, y), Size(0.9f * u, w * 0.02f)) }
                    y += w * 0.02f; f++
                }
                // La luz en el canto exterior y la sombra en el interior del marco.
                drawPath(fuera, tn(Offset(w * 0.2f, top)).b(0.35f), style = Stroke(2f * u))
            }
        }
        // Restos de enlucido sobre el ladrillo.
        listOf(Rect(0.005f, 0.7f, 0.04f, 0.8f), Rect(0.62f, 0.15f, 0.67f, 0.2f), Rect(0.955f, 1.1f, 0.99f, 1.18f)).forEachIndexed { i, f ->
            val r = Rect(w * f.left, ch * f.top, w * f.right, ch * f.bottom)
            val t = tn(r.center)
            val p = contornoIrregular(r, 5070 + i, 12, 0.25f)
            translate(0.8f * u, 1.2f * u) { drawPath(p, t.s(0.3f)) }
            drawPath(p, t.c(mezcla(ADOBE, ADOBE_OSC, 0.3f)))
            clipPath(p) { pinceladas(r, if (esNoche) NOCHE else ADOBE_OSC, 5075 + i, 5, u, 0.2f); drawRect(t.b(0.2f), r.topLeft, Size(r.width, r.height * 0.3f)) }
            drawPath(p, t.s(0.25f), style = Stroke(0.6f * u))
        }

        // ---- el fondo del nicho
        relleno(nicho, NICHO)
        clipPath(nicho) {
            pinceladas(Rect(w * NL, top, w * NR, fondo), if (o) NOCHE else ADOBE_OSC, 5080, 30, u, 0.1f)
            // La paja del fondo.
            for (k in 0 until 24) {
                val p = Offset(w * (NL + (NR - NL) * azar(5081, k)), top + (ch - top) * azar(5082, k))
                val a = azar(5083, k) * 3.14f
                val t = tn(p)
                drawLine(t.c(mezcla(NICHO, PAPEL, 0.5f)).copy(alpha = 0.7f), p, p + Offset(3.5f * u * cos(a), 3.5f * u * sin(a)), 0.6f * u)
            }
            // La sombra que el arco y la jamba izquierda echan en el fondo (de día); de noche, lo de arriba en penumbra.
            val corrido = Path().apply { addPath(nicho, if (o) Offset(-w * 0.02f, w * 0.05f) else Offset(w * 0.05f, w * 0.06f)) }
            clipPath(corrido, clipOp = ClipOp.Difference) { drawRect(if (o) NOCHE.copy(alpha = 0.4f) else sombraMueble(0.26f), Offset.Zero, size) }
            // Los costados del nicho (un canto de barro): el de la izquierda en sombra, el de la derecha con luz.
            val r = w * 0.018f
            val dentro = arcoApuntado(Rect(w * NL + r, top + r, w * NR - r, fondo), fl - r * 0.3f)
            clipPath(dentro, clipOp = ClipOp.Difference) {
                if (!o) {
                    drawRect(sombraMueble(0.32f), Offset.Zero, Size(w * 0.5f, size.height))
                    drawRect(sombraMueble(0.22f), Offset.Zero, Size(w, arranque))
                    drawRect(luzMueble(0.3f), Offset(w * 0.5f, arranque), Size(w * 0.5f, size.height))
                } else {
                    drawRect(LLAMA.copy(alpha = 0.18f), Offset(w * 0.5f, 0f), Size(w * 0.5f, ch * 1.8f))
                    drawRect(NOCHE.copy(alpha = 0.25f), Offset.Zero, Size(w * 0.5f, size.height))
                }
            }
            // El hollín de la lámpara, que sube por el fondo y mancha el arco.
            val ll = LUZ * w
            mancha(ll - Offset(0f, w * 0.1f), w * 0.1f, HOLLIN.copy(alpha = if (o) 0.25f else 0.34f))
            drawOval(HOLLIN.copy(alpha = 0.12f), Offset(ll.x - w * 0.03f, top + w * 0.02f), Size(w * 0.07f, ll.y - top - w * 0.05f))
        }

        // ---- el poste del medio, detrás de las tablas
        val h0 = d.huecos[0]; val h1 = d.huecos[1]
        val ultima = d.filas.last()
        val poste = Rect(h0.right, h0.top, h1.left, ultima.bottom)
        for (k in 0 until 10) {
            val y0 = poste.top + poste.height * k / 10f
            val t = tn(Offset(poste.center.x, y0 + poste.height / 20f))
            drawRect(t.c(mezcla(ALAMO, ALAMO_OSC, 0.3f)), Offset(poste.left, y0), Size(poste.width, poste.height / 10f + 1f))
            drawRect(if (o) t.b(0.4f) else luzMueble(0.3f), Offset(if (o) poste.right - 1.4f * u else poste.left, y0), Size(1.4f * u, poste.height / 10f + 1f))
            drawRect(t.s(0.3f), Offset(if (o) poste.left else poste.right - poste.width * 0.3f, y0), Size(poste.width * 0.3f, poste.height / 10f + 1f))
        }
        vetas(poste, (if (o) NOCHE else ALAMO_OSC).copy(alpha = 0.35f), 5090, 0.8f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)

        // ---- la repisa de arriba: la tabla vista desde arriba y su canto
        val sup = Rect(w * NL, ch * SUP, w * NR, ch * FRENTE)
        for (k in 0 until 8) {
            val x0 = sup.left + sup.width * k / 8f
            val t = tn(Offset(x0 + sup.width / 16f, sup.center.y))
            drawRect(t.c(mezcla(ALAMO, ALAMO_LUZ, 0.55f)), Offset(x0, sup.top), Size(sup.width / 8f + 1f, sup.height))
        }
        drawRect(tn(sup.center).s(0.3f), sup.topLeft, Size(sup.width, 2.5f * u))
        vetas(sup, (if (o) NOCHE else ALAMO_OSC).copy(alpha = 0.22f), 5091, 0.6f, grosor = 0.5f, ondulacion = 0.3f)
        tabla(Rect(w * NL, ch * FRENTE, w * NR, d.huecos.first().top), 5100)
        mensulas(d.huecos.first().top, w)
        // ---- las repisas entre fila y fila, y el alféizar de abajo
        d.filas.forEachIndexed { i, f ->
            val ultimaFila = i == d.filas.size - 1
            tabla(Rect(w * NL, f.bottom, w * NR, if (ultimaFila) fondo else f.bottom + d.separacion), 5110 + i)
            if (!ultimaFila) mensulas(f.bottom + d.separacion, w)
        }
    }

    /** El canto de un tablón de álamo seco: luz arriba, sombra abajo, la veta, grietas y una raja en la punta. */
    private fun DrawScope.tabla(r: Rect, s: Int) {
        val u = density
        val n = 8
        for (i in 0 until n) {
            val x0 = r.left + r.width * i / n
            val t = tn(Offset(x0 + r.width / n / 2, r.center.y))
            drawRect(t.c(ALAMO), Offset(x0, r.top), Size(r.width / n + 1f, r.height))
            drawRect(t.b(0.5f), Offset(x0, r.top), Size(r.width / n + 1f, 1.2f * u))
            drawRect(t.s(0.12f), Offset(x0, r.top + r.height * 0.55f), Size(r.width / n + 1f, r.height * 0.45f))
            drawRect(t.s(0.35f), Offset(x0, r.bottom - 1.6f * u), Size(r.width / n + 1f, 1.6f * u))
        }
        vetas(r, (if (esNoche) NOCHE else ALAMO_OSC).copy(alpha = 0.4f), s, 0.8f, grosor = 0.5f, ondulacion = 0.35f)
        for (k in 0 until 3) {
            val x = r.left + r.width * (0.08f + 0.8f * azar(s, k + 900))
            val y = r.top + r.height * (0.3f + 0.4f * azar(s, k + 910))
            val l = r.width * (0.04f + 0.08f * azar(s, k + 920))
            val t = tn(Offset(x, y))
            drawLine(t.s(0.6f), Offset(x, y), Offset(x + l, y + 1.2f * u * (azar(s, k + 930) - 0.5f)), 1f * u, StrokeCap.Round)
            drawLine(t.b(0.3f), Offset(x + 1f * u, y + 1f * u), Offset(x + l * 0.8f, y + 1f * u), 0.5f * u)
        }
        // La raja de la punta derecha, donde se secó la madera.
        val t = tn(Offset(r.right, r.center.y))
        drawPath(poligono(r.right - r.width * 0.05f, r.center.y, r.right, r.center.y - 1.4f * u, r.right, r.center.y + 1.2f * u), t.s(0.6f))
    }

    /** Las ménsulas bajo una tabla, en el canto del nicho a cada lado. */
    private fun DrawScope.mensulas(y: Float, w: Float) {
        val u = density
        listOf(w * (NL + 0.006f), w * (NR - 0.034f)).forEach { x ->
            val mw = w * 0.028f; val mh = w * 0.05f
            val t = tn(Offset(x + mw / 2, y + mh / 2))
            val p = Path().apply {
                moveTo(x, y); lineTo(x + mw, y); lineTo(x + mw, y + mh * 0.25f)
                cubicTo(x + mw * 0.7f, y + mh * 0.3f, x + mw * 0.75f, y + mh * 0.75f, x + mw * 0.5f, y + mh)
                cubicTo(x + mw * 0.25f, y + mh * 0.75f, x + mw * 0.3f, y + mh * 0.3f, x, y + mh * 0.25f)
                close()
            }
            translate(if (esNoche) -w * 0.008f else w * 0.008f, w * 0.006f) { drawPath(p, t.s(0.3f)) }
            drawPath(p, t.c(mezcla(ALAMO, ALAMO_OSC, 0.35f)))
            clipPath(p) {
                drawRect(t.b(0.3f), Offset(x, y), Size(mw * 0.3f, mh))
                drawRect(t.s(0.25f), Offset(x + mw * 0.65f, y), Size(mw * 0.35f, mh))
            }
            drawRect(t.s(0.35f), Offset(x, y), Size(mw, 1.2f * u))
        }
    }

    // ---------------------------------------------------------------- en el muro

    /**
     * La campana de camello de bronce colgada de una estaca con su correa de cuero: la boca abierta con el badajo, dos
     * molduras, el brillo a la izquierda y una borla de lana roja en la correa.
     */
    private fun DrawScope.campana(estaca: Offset) {
        val u = density; val w = anchoW
        val bw = w * 0.052f; val bh = w * 0.078f
        val top = Offset(estaca.x + w * 0.004f, estaca.y + w * 0.08f)
        val t = tn(top + Offset(0f, bh / 2))
        val der = luzDerecha(top.x)
        val bell = Path().apply {
            moveTo(top.x - bw * 0.3f, top.y)
            cubicTo(top.x - bw * 0.34f, top.y + bh * 0.5f, top.x - bw * 0.4f, top.y + bh * 0.82f, top.x - bw * 0.52f, top.y + bh)
            lineTo(top.x + bw * 0.52f, top.y + bh)
            cubicTo(top.x + bw * 0.4f, top.y + bh * 0.82f, top.x + bw * 0.34f, top.y + bh * 0.5f, top.x + bw * 0.3f, top.y)
            close()
        }
        val c1 = Path().apply { moveTo(estaca.x - w * 0.003f, estaca.y); cubicTo(estaca.x - w * 0.02f, estaca.y + w * 0.035f, top.x - bw * 0.25f, top.y - w * 0.035f, top.x - bw * 0.08f, top.y - w * 0.004f) }
        val c2 = Path().apply { moveTo(estaca.x + w * 0.003f, estaca.y); cubicTo(estaca.x + w * 0.018f, estaca.y + w * 0.03f, top.x + bw * 0.25f, top.y - w * 0.035f, top.x + bw * 0.08f, top.y - w * 0.004f) }
        sombraEnPared(bell, top, size.height, 0.22f)
        if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(c1, sombraMueble(0.2f), style = Stroke(2.4f * u)); drawPath(c2, sombraMueble(0.2f), style = Stroke(2.4f * u)) }
        // La estaca de madera clavada en el muro.
        val te = tn(estaca)
        drawCircle(te.s(0.35f), w * 0.009f, estaca + Offset(1.5f * u, 1.5f * u))
        drawCircle(te.c(ALAMO_OSC), w * 0.009f, estaca)
        drawCircle(te.c(mezcla(ALAMO, ALAMO_OSC, 0.3f)), w * 0.006f, estaca - Offset(0.6f * u, 0.6f * u))
        // La correa de cuero.
        listOf(c1, c2).forEach { c ->
            drawPath(c, t.c(CUERO), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawPath(c, t.b(0.3f), style = Stroke(0.6f * u))
        }
        // La argolla y la campana.
        drawCircle(t.c(BRONCE), w * 0.006f, top + Offset(0f, -w * 0.003f), style = Stroke(1.6f * u))
        drawPath(bell, t.c(BRONCE))
        clipPath(bell) {
            cilindro(Rect(top.x - bw * 0.52f, top.y, top.x + bw * 0.52f, top.y + bh), t, 1.3f, der)
            listOf(0.18f, 0.7f).forEach { f ->
                val y = top.y + bh * f
                drawRect(t.s(0.3f), Offset(top.x - bw, y + 1.2f * u), Size(bw * 2, 1f * u))
                drawRect(t.b(0.5f), Offset(top.x - bw, y), Size(bw * 2, 1.2f * u))
            }
            // Una mancha de verdín.
            drawOval(t.c(mezcla(BRONCE, VERDE, 0.6f)).copy(alpha = 0.5f), Offset(top.x + bw * 0.05f, top.y + bh * 0.35f), Size(bw * 0.25f, bh * 0.2f))
        }
        // La boca, el badajo y el brillo.
        val boca = Rect(top.x - bw * 0.52f, top.y + bh - w * 0.008f, top.x + bw * 0.52f, top.y + bh + w * 0.008f)
        drawOval(t.c(mezcla(BRONCE, HOLLIN, 0.4f)), boca.topLeft, boca.size)
        drawOval(t.c(HOLLIN), Offset(boca.left + bw * 0.08f, boca.top + w * 0.002f), Size(boca.width - bw * 0.16f, boca.height * 0.65f))
        drawCircle(t.c(mezcla(BRONCE, HOLLIN, 0.5f)), w * 0.005f, Offset(top.x + bw * 0.06f, boca.bottom + w * 0.002f))
        destello(Offset(top.x - bw * (if (der) -0.18f else 0.18f), top.y + bh * 0.4f), 1.2f * u, t)
        // La borla de lana roja.
        val bo = Offset(top.x + bw * 0.38f, top.y - w * 0.022f)
        drawCircle(t.c(CARMESI), w * 0.005f, bo)
        for (k in -2..2) drawLine(t.c(if (k == 0) AZAFRAN else CARMESI), bo, bo + Offset(k * w * 0.0025f + w * 0.003f, w * 0.03f), 1.1f * u, StrokeCap.Round)
        drawLine(t.s(0.3f), bo + Offset(w * 0.006f, w * 0.005f), bo + Offset(w * 0.01f, w * 0.032f), 1f * u)
    }

    /**
     * El odre de piel de cabra colgado por el cuello de una estaca: el cuerpo lleno y pesado, las patas atadas, la costura,
     * el brillo del cuero untado y lo húmedo abajo.
     */
    private fun DrawScope.odre(estaca: Offset) {
        val u = density; val w = anchoW
        val cuello = Offset(estaca.x - w * 0.01f, estaca.y + w * 0.04f)
        val c = Offset(estaca.x - w * 0.005f, estaca.y + w * 0.13f)
        val bw = w * 0.1f; val bh = w * 0.15f
        val t = tn(c)
        val der = luzDerecha(c.x)
        val cuerpo = Path().apply {
            moveTo(cuello.x - w * 0.011f, cuello.y)
            cubicTo(cuello.x - w * 0.02f, cuello.y + w * 0.03f, c.x - bw * 0.56f, c.y - bh * 0.3f, c.x - bw * 0.52f, c.y + bh * 0.08f)
            cubicTo(c.x - bw * 0.5f, c.y + bh * 0.42f, c.x - bw * 0.2f, c.y + bh * 0.54f, c.x + bw * 0.06f, c.y + bh * 0.5f)
            cubicTo(c.x + bw * 0.36f, c.y + bh * 0.47f, c.x + bw * 0.54f, c.y + bh * 0.28f, c.x + bw * 0.48f, c.y)
            cubicTo(c.x + bw * 0.44f, c.y - bh * 0.3f, cuello.x + w * 0.024f, cuello.y + w * 0.028f, cuello.x + w * 0.011f, cuello.y)
            close()
        }
        // Las patas atadas: la de arriba a la izquierda y la de abajo a la derecha.
        val pata1 = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(c.x - bw * 0.72f, c.y - bh * 0.26f, c.x - bw * 0.38f, c.y - bh * 0.16f), CornerRadius(w * 0.01f))) }
        val pata2 = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(c.x + bw * 0.36f, c.y + bh * 0.26f, c.x + bw * 0.66f, c.y + bh * 0.35f), CornerRadius(w * 0.01f))) }
        val todo = Path().apply { addPath(cuerpo); addPath(pata1); addPath(pata2) }
        sombraEnPared(todo, c, size.height, 0.24f)
        // La cuerda del cuello a la estaca.
        val te = tn(estaca)
        drawLine(t.c(CUERDA), estaca, cuello + Offset(-w * 0.008f, 0f), 1.4f * u)
        drawLine(t.c(CUERDA), estaca, cuello + Offset(w * 0.008f, 0f), 1.4f * u)
        drawCircle(te.s(0.35f), w * 0.008f, estaca + Offset(1.5f * u, 1.5f * u))
        drawCircle(te.c(ALAMO_OSC), w * 0.008f, estaca)
        drawCircle(te.c(mezcla(ALAMO, ALAMO_OSC, 0.3f)), w * 0.005f, estaca - Offset(0.6f * u, 0.6f * u))
        listOf(pata1 to (c.x - bw * 0.55f), pata2 to (c.x + bw * 0.52f)).forEach { (p, x) ->
            drawPath(p, t.c(mezcla(CUERO, HOLLIN, 0.15f)))
            clipPath(p) { drawRect(t.s(0.25f), Offset(x - bw, c.y + (if (x < c.x) -bh * 0.2f else bh * 0.31f)), Size(bw * 2, bh)) }
            drawLine(t.c(CUERDA), Offset(x, c.y - bh * (if (x < c.x) 0.27f else -0.25f)), Offset(x, c.y - bh * (if (x < c.x) 0.15f else -0.36f)), 1.6f * u)
        }
        drawPath(cuerpo, t.c(CUERO))
        clipPath(cuerpo) {
            val r = Rect(c.x - bw * 0.56f, cuello.y, c.x + bw * 0.56f, c.y + bh * 0.55f)
            cilindro(r, t, 1.2f, der)
            // Lo húmedo y pesado de abajo.
            drawOval(t.s(0.2f), Offset(c.x - bw * 0.6f, c.y + bh * 0.2f), Size(bw * 1.3f, bh * 0.5f))
            // El brillo del cuero untado.
            val bx = if (der) c.x + bw * 0.22f else c.x - bw * 0.3f
            drawPath(Path().apply { moveTo(bx, c.y - bh * 0.22f); cubicTo(bx - bw * 0.08f, c.y, bx - bw * 0.06f, c.y + bh * 0.2f, bx + bw * 0.04f, c.y + bh * 0.35f) }, t.b(0.55f), style = Stroke(2f * u, cap = StrokeCap.Round))
            // La costura, con sus puntadas, y los rozones.
            val cos1 = Path().apply { moveTo(c.x + bw * 0.12f, cuello.y + w * 0.02f); cubicTo(c.x + bw * 0.22f, c.y - bh * 0.1f, c.x + bw * 0.22f, c.y + bh * 0.25f, c.x + bw * 0.1f, c.y + bh * 0.5f) }
            drawPath(cos1, t.s(0.4f), style = Stroke(1f * u))
            for (k in 0 until 9) {
                val y = cuello.y + w * 0.025f + (c.y + bh * 0.45f - cuello.y - w * 0.025f) * k / 8f
                val x = c.x + bw * (0.16f + 0.07f * sin(k * 0.45f))
                drawLine(t.c(CUERDA).copy(alpha = 0.7f), Offset(x - 1.5f * u, y), Offset(x + 1.5f * u, y + 0.8f * u), 0.8f * u)
            }
            drawOval(t.c(mezcla(CUERO, ADOBE, 0.45f)).copy(alpha = 0.45f), Offset(c.x - bw * 0.2f, c.y + bh * 0.05f), Size(bw * 0.24f, bh * 0.12f))
        }
        // El cuello atado.
        drawRect(t.c(mezcla(CUERO, HOLLIN, 0.2f)), Offset(cuello.x - w * 0.012f, cuello.y - w * 0.01f), Size(w * 0.024f, w * 0.016f))
        for (k in 0 until 3) drawLine(t.c(CUERDA), Offset(cuello.x - w * 0.014f, cuello.y - w * 0.006f + k * 1.8f * u), Offset(cuello.x + w * 0.014f, cuello.y - w * 0.004f + k * 1.8f * u), 1.1f * u)
        filo(Rect(c.x - bw * 0.4f, c.y + bh * 0.4f, c.x + bw * 0.3f, c.y + bh * 0.5f), t, true)
    }

    // ---------------------------------------------------------------- sobre la repisa

    /** El látigo de camellero colgado de una estaca: el mango de madera con su empuñadura de cuero y la tralla enrollada en tres vueltas. */
    private fun DrawScope.latigo(estaca: Offset, hasta: Float) {
        val u = density; val w = anchoW
        val t = tn(estaca + Offset(0f, w * 0.06f))
        val vueltas = listOf(Rect(-0.03f, 0.01f, 0.022f, 0.115f), Rect(-0.024f, 0.012f, 0.03f, 0.122f), Rect(-0.036f, 0.008f, 0.016f, 0.108f))
            .map { Rect(estaca.x + w * it.left, estaca.y + w * it.top, estaca.x + w * it.right, estaca.y + w * it.bottom) }
        val m0 = estaca + Offset(w * 0.004f, w * 0.012f)
        val m1 = estaca + Offset(w * 0.022f, w * 0.118f)
        // Sus sombras en el fondo del nicho.
        clipRect(0f, 0f, size.width, hasta) {
            val dx = if (esNoche) -w * 0.025f else w * 0.02f
            val a = if (esNoche) (0.5f * t.k).coerceAtMost(0.4f) else 0.2f
            translate(dx, w * 0.012f) {
                vueltas.forEach { r -> drawOval(t.s(a), r.topLeft, r.size, style = Stroke(1.8f * u)) }
                drawLine(t.s(a), m0, m1, 3.2f * u, StrokeCap.Round)
            }
        }
        // La tralla de cuero trenzado, en tres vueltas, y su punta que cuelga.
        vueltas.forEachIndexed { i, r ->
            drawOval(t.c(mezcla(CUERO, HOLLIN, 0.2f + 0.1f * i)), r.topLeft, r.size, style = Stroke(1.8f * u))
            drawArc(t.b(0.45f), 150f, 80f, false, r.topLeft, r.size, style = Stroke(0.7f * u))
        }
        val punta = Path().apply { moveTo(estaca.x - w * 0.03f, estaca.y + w * 0.07f); cubicTo(estaca.x - w * 0.04f, estaca.y + w * 0.12f, estaca.x - w * 0.02f, estaca.y + w * 0.14f, estaca.x - w * 0.028f, estaca.y + w * 0.165f) }
        drawPath(punta, t.c(mezcla(CUERO, HOLLIN, 0.25f)), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawLine(t.c(mezcla(CUERDA, PAPEL, 0.3f)), estaca + Offset(-w * 0.028f, w * 0.165f), estaca + Offset(-w * 0.026f, w * 0.178f), 0.8f * u)
        // El mango, colgado de su lazo.
        drawLine(t.c(CUERO), estaca, m0, 1.2f * u)
        drawLine(t.c(mezcla(ALAMO, CUERO, 0.3f)), m0, m1, 3.2f * u, StrokeCap.Round)
        drawLine(t.b(0.45f), m0 + Offset(-0.8f * u, 0f), m1 + Offset(-0.8f * u, 0f), 0.8f * u)
        for (k in 0 until 6) {
            val p = m0 + (m1 - m0) * (0.05f + 0.06f * k)
            drawLine(t.c(mezcla(CUERO, HOLLIN, 0.3f)), p + Offset(-2f * u, 0f), p + Offset(2f * u, 1.2f * u), 1f * u)
        }
        drawCircle(t.c(mezcla(ALAMO, CUERO, 0.4f)), 2.4f * u, m1)
        // La estaca.
        val te = tn(estaca)
        drawCircle(te.s(0.35f), w * 0.007f, estaca + Offset(1.4f * u, 1.4f * u))
        drawCircle(te.c(ALAMO_OSC), w * 0.007f, estaca)
        drawCircle(te.c(mezcla(ALAMO, ALAMO_OSC, 0.3f)), w * 0.0045f, estaca - Offset(0.6f * u, 0.6f * u))
    }

    /**
     * La balanza de platillos colgada de su gancho: la cadena, la horquilla con el fiel, el astil de bronce un poco
     * inclinado (pesa más el platillo de la izquierda, con su azafrán), los cordones y los dos platillos.
     */
    private fun DrawScope.balanza(gancho: Offset, hasta: Float) {
        val u = density; val w = anchoW
        val pivote = Offset(gancho.x, gancho.y + w * 0.085f)
        val t = tn(pivote)
        val der = luzDerecha(pivote.x)
        val largo = w * 0.3f
        val ang = Math.toRadians(-3.5).toFloat()
        val ca = cos(ang); val sa = sin(ang)
        val izq = Offset(pivote.x - largo / 2 * ca, pivote.y - largo / 2 * sa)
        val dch = Offset(pivote.x + largo / 2 * ca, pivote.y + largo / 2 * sa)
        val caida = w * 0.122f
        val pr = w * 0.066f
        fun plato(c: Offset) = Path().apply {
            moveTo(c.x - pr, c.y)
            cubicTo(c.x - pr * 0.8f, c.y + pr * 0.42f, c.x + pr * 0.8f, c.y + pr * 0.42f, c.x + pr, c.y)
            close()
        }
        val platoI = Offset(izq.x, izq.y + caida); val platoD = Offset(dch.x, dch.y + caida)
        // Su sombra en el fondo del nicho.
        val silueta = Path().apply {
            addPath(plato(platoI)); addPath(plato(platoD))
            addRect(Rect(izq.x, pivote.y - w * 0.004f, dch.x, pivote.y + w * 0.004f))
            addRect(Rect(pivote.x - w * 0.006f, gancho.y, pivote.x + w * 0.006f, pivote.y))
        }
        sombraEnPared(silueta, pivote, hasta, 0.2f)
        // El gancho de hierro clavado en el muro.
        val tg = tn(gancho)
        drawCircle(tg.s(0.4f), w * 0.006f, gancho + Offset(1.4f * u, 1.4f * u))
        drawCircle(tg.c(HOLLIN), w * 0.006f, gancho)
        drawArc(tg.c(mezcla(HOLLIN, BRONCE, 0.2f)), 0f, 200f, false, Offset(gancho.x - w * 0.008f, gancho.y - w * 0.002f), Size(w * 0.016f, w * 0.016f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        // La cadena: eslabones alternos.
        val tope = pivote.y - w * 0.05f
        var y = gancho.y + w * 0.012f; var k = 0
        while (y < tope) {
            if (k % 2 == 0) drawOval(t.c(BRONCE), Offset(gancho.x - w * 0.003f, y), Size(w * 0.006f, w * 0.011f), style = Stroke(1.1f * u))
            else drawLine(t.c(mezcla(BRONCE, HOLLIN, 0.3f)), Offset(gancho.x, y), Offset(gancho.x, y + w * 0.011f), 1.4f * u)
            y += w * 0.009f; k++
        }
        // Los cordones de los platillos.
        listOf(izq to platoI, dch to platoD).forEach { (a, p) ->
            listOf(-0.92f, 0f, 0.92f).forEach { f ->
                drawLine(t.c(mezcla(CUERDA, ALAMO_OSC, 0.3f)), a, Offset(p.x + pr * f, p.y - (if (f == 0f) pr * 0.08f else 0f)), 0.8f * u)
            }
        }
        // La horquilla y el fiel.
        val hq = Rect(pivote.x - w * 0.008f, tope, pivote.x + w * 0.008f, pivote.y + w * 0.004f)
        drawRect(t.c(BRONCE), hq.topLeft, Size(hq.width, 1.6f * u))
        drawLine(t.c(BRONCE), Offset(hq.left, hq.top), Offset(hq.left, hq.bottom), 1.4f * u)
        drawLine(t.c(BRONCE), Offset(hq.right, hq.top), Offset(hq.right, hq.bottom), 1.4f * u)
        rotate(-3.5f, pivote) {
            drawPath(poligono(pivote.x - w * 0.003f, pivote.y, pivote.x + w * 0.003f, pivote.y, pivote.x, tope + w * 0.008f), t.c(mezcla(BRONCE, LUZ_MUEBLE, 0.15f)))
            // El astil: más grueso al medio, con sus remates.
            val astil = Path().apply {
                moveTo(izq.x + w * 0.004f, pivote.y - w * 0.0035f)
                lineTo(pivote.x, pivote.y - w * 0.0075f)
                lineTo(pivote.x + largo / 2 - w * 0.004f, pivote.y - w * 0.0035f)
                lineTo(pivote.x + largo / 2 - w * 0.004f, pivote.y + w * 0.0035f)
                lineTo(pivote.x, pivote.y + w * 0.0075f)
                lineTo(izq.x + w * 0.004f, pivote.y + w * 0.0035f)
                close()
            }
            val izqR = pivote.x - largo / 2
            drawPath(astil, t.c(BRONCE))
            drawLine(t.b(0.7f), Offset(izqR + w * 0.01f, pivote.y - w * 0.002f), Offset(pivote.x + largo / 2 - w * 0.01f, pivote.y - w * 0.002f), 0.8f * u)
            drawLine(t.s(0.4f), Offset(izqR + w * 0.01f, pivote.y + w * 0.003f), Offset(pivote.x + largo / 2 - w * 0.01f, pivote.y + w * 0.003f), 0.8f * u)
            listOf(izqR + w * 0.004f, pivote.x + largo / 2 - w * 0.004f).forEach { x ->
                drawCircle(t.c(BRONCE), w * 0.0055f, Offset(x, pivote.y))
                drawCircle(t.b(0.6f), w * 0.002f, Offset(x - w * 0.0015f, pivote.y - w * 0.0015f))
            }
            drawCircle(t.c(mezcla(BRONCE, HOLLIN, 0.3f)), w * 0.004f, pivote)
        }
        // Los platillos (el de la izquierda, con su azafrán).
        listOf(platoI to true, platoD to false).forEach { (p, conAzafran) ->
            if (conAzafran) {
                val az = Path().apply {
                    moveTo(p.x - pr * 0.55f, p.y + 0.5f * u)
                    cubicTo(p.x - pr * 0.35f, p.y - pr * 0.3f, p.x + pr * 0.25f, p.y - pr * 0.34f, p.x + pr * 0.55f, p.y + 0.5f * u)
                    close()
                }
                drawPath(az, t.c(mezcla(AZAFRAN, CARMESI, 0.5f)))
                for (q in 0 until 7) {
                    val x = p.x - pr * 0.45f + pr * 0.15f * q
                    drawLine(t.c(mezcla(AZAFRAN, LACA, 0.3f + 0.3f * azar(5200, q))), Offset(x, p.y - pr * 0.05f), Offset(x + pr * (azar(5201, q) - 0.5f) * 0.3f, p.y - pr * (0.12f + 0.12f * azar(5202, q))), 0.8f * u, StrokeCap.Round)
                }
                drawLine(t.b(0.4f), Offset(p.x - pr * 0.3f, p.y - pr * 0.2f), Offset(p.x, p.y - pr * 0.26f), 0.7f * u)
            }
            val pl = plato(p)
            drawPath(pl, t.c(BRONCE))
            clipPath(pl) {
                drawRect(t.s(0.3f), Offset(if (der) p.x - pr else p.x + pr * 0.2f, p.y), Size(pr * 0.8f, pr))
                drawRect(t.b(0.3f), Offset(if (der) p.x + pr * 0.35f else p.x - pr * 0.7f, p.y), Size(pr * 0.3f, pr))
                drawRect(t.s(0.15f), Offset(p.x - pr, p.y + pr * 0.24f), Size(pr * 2, pr))
            }
            drawOval(t.c(mezcla(BRONCE, LUZ_MUEBLE, 0.2f)), Offset(p.x - pr, p.y - w * 0.003f), Size(pr * 2, w * 0.006f))
            drawArc(t.b(0.7f), 180f, 90f, false, Offset(p.x - pr, p.y - w * 0.003f), Size(pr * 2, w * 0.006f), style = Stroke(0.8f * u))
            destello(Offset(p.x + (if (der) pr * 0.5f else -pr * 0.55f), p.y + pr * 0.1f), 1.3f * u, t)
        }
        destello(Offset(pivote.x - largo * 0.32f, pivote.y - w * 0.002f + largo * 0.32f * sa), 1.2f * u, t)
        // De noche, el bronce devuelve la llama: el filo del astil y el borde de los platillos que miran a la lámpara.
        if (esNoche) {
            val a = (1.4f * t.k).coerceAtMost(0.9f)
            drawLine(LLAMA.copy(alpha = a), Offset(izq.x, izq.y - w * 0.004f), Offset(dch.x, dch.y - w * 0.004f), 1.3f * u)
            listOf(platoI, platoD).forEach { p ->
                drawArc(Color(0xFFFFF0CF).copy(alpha = a), 300f, 50f, false, Offset(p.x - pr, p.y - w * 0.004f), Size(pr * 2, w * 0.008f), style = Stroke(1.2f * u))
            }
            drawCircle(LLAMA.copy(alpha = a * 0.5f), w * 0.008f, Offset(gancho.x, pivote.y))
        }
    }

    /** El estuche de las pesas, con la tapa abierta apoyada en el muro y su forro rojo, y tres pesas de bronce delante. */
    private fun DrawScope.pesas(p: Offset) {
        val u = density; val w = anchoW
        val t = tn(p + Offset(w * 0.06f, 0f))
        val der = luzDerecha(p.x)
        val caja = Rect(p.x, p.y - w * 0.042f, p.x + w * 0.09f, p.y)
        val tapa = Rect(caja.left + w * 0.004f, caja.top - w * 0.05f, caja.right - w * 0.002f, caja.top)
        val nogal = mezcla(CUERO, ALAMO_OSC, 0.5f)
        // La tapa, con su forro, apoyada atrás.
        sombraEnPared(Path().apply { addRect(Rect(tapa.left, tapa.top, tapa.right, caja.bottom)) }, caja.center, caja.bottom, 0.2f)
        drawRect(t.c(nogal), tapa.topLeft, tapa.size)
        drawRect(t.c(mezcla(CARMESI, HOLLIN, 0.3f)), Offset(tapa.left + w * 0.006f, tapa.top + w * 0.006f), Size(tapa.width - w * 0.012f, tapa.height - w * 0.006f))
        drawRect(t.s(0.25f), Offset(tapa.left + w * 0.006f, tapa.bottom - w * 0.012f), Size(tapa.width - w * 0.012f, w * 0.012f))
        drawRect(t.b(0.4f), tapa.topLeft, Size(tapa.width, 1f * u))
        // Los huecos de las pesas, con una que se quedó dentro.
        drawRect(t.c(mezcla(CARMESI, HOLLIN, 0.5f)), Offset(caja.left + w * 0.004f, caja.top - w * 0.004f), Size(caja.width - w * 0.008f, w * 0.008f))
        drawCircle(t.c(BRONCE), w * 0.006f, Offset(caja.left + w * 0.02f, caja.top - w * 0.004f))
        // La caja.
        contacto(Offset(caja.center.x + w * 0.02f, caja.bottom), caja.width * 1.2f, w * 0.012f, 0.4f)
        drawRect(t.c(nogal), caja.topLeft, caja.size)
        vetas(caja, (if (esNoche) NOCHE else HOLLIN).copy(alpha = 0.25f), 5210, 0.8f, grosor = 0.5f)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) { cilindro(caja, t, 0.7f, der) }
        drawRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1f * u))
        drawRect(t.c(BRONCE), Offset(caja.center.x - w * 0.006f, caja.top + w * 0.004f), Size(w * 0.012f, w * 0.012f))
        destello(Offset(caja.center.x - w * 0.003f, caja.top + w * 0.007f), 0.8f * u, t)
        // Las tres pesas, de mayor a menor.
        listOf(Triple(0.105f, 0.026f, 0.03f), Triple(0.135f, 0.02f, 0.022f), Triple(0.158f, 0.014f, 0.016f)).forEach { (fx, rw, rh) ->
            val c = Offset(p.x + w * fx - w * 0.06f + w * 0.05f, p.y + w * 0.004f)
            val r = Rect(c.x - w * rw / 2, c.y - w * rh, c.x + w * rw / 2, c.y)
            contacto(Offset(c.x + w * 0.006f, c.y), r.width * 1.4f, w * 0.008f, 0.45f)
            drawRect(t.c(BRONCE), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.3f, der) }
            drawOval(t.c(mezcla(BRONCE, LUZ_MUEBLE, 0.25f)), Offset(r.left, r.top - w * 0.003f), Size(r.width, w * 0.006f))
            drawCircle(t.c(BRONCE), r.width * 0.18f, Offset(c.x, r.top - w * 0.003f))
            drawCircle(t.b(0.6f), r.width * 0.07f, Offset(c.x - r.width * 0.06f, r.top - w * 0.004f))
            filo(r, t, der)
        }
    }

    /** Un rollo de seda tumbado a lo largo, con su canto en espiral a la izquierda, el brillo, dos ataduras y, si [sello], el sello de tinta. */
    private fun DrawScope.rollo(r: Rect, col: Color, s: Int, sello: Boolean = false) {
        val u = density
        val t = tn(r.center)
        val der = luzDerecha(r.center.x)
        val ec = r.height * 0.32f
        contacto(Offset(r.center.x + r.width * 0.05f, r.bottom), r.width * 1.05f, r.height * 0.25f, 0.4f)
        // El cuerpo: un cilindro tumbado, con el canto de la derecha redondo y en sombra.
        val cuerpo = Rect(r.left + ec, r.top, r.right, r.bottom)
        drawOval(t.c(mezcla(col, HOLLIN, 0.35f)), Offset(r.right - ec * 1.1f, r.top), Size(ec * 1.1f, r.height))
        drawRect(t.c(col), cuerpo.topLeft, Size(cuerpo.width - ec * 0.55f, cuerpo.height))
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right - ec * 0.55f, cuerpo.bottom) {
            tumbado(cuerpo, t, 1.5f)
            // El brillo de la seda, ancho y suave, y la orilla de la última vuelta.
            drawRect(t.b(0.22f), Offset(cuerpo.left, r.top + r.height * 0.16f), Size(cuerpo.width, r.height * 0.14f))
            drawLine(t.b(0.7f), Offset(cuerpo.left + r.width * 0.06f, r.top + r.height * 0.22f), Offset(cuerpo.right - r.width * 0.1f, r.top + r.height * 0.22f), 1.2f * u)
            val ox = cuerpo.left + cuerpo.width * (0.55f + 0.25f * azar(s, 3))
            drawLine(t.s(0.35f), Offset(ox, r.top), Offset(ox + r.height * 0.15f, r.bottom), 0.8f * u)
            drawLine(t.b(0.3f), Offset(ox - 1f * u, r.top), Offset(ox + r.height * 0.15f - 1f * u, r.bottom), 0.6f * u)
        }
        // La atadura.
        val x = r.left + r.width * (0.3f + 0.1f * azar(s, 1))
        drawRect(t.c(mezcla(CUERDA, PAPEL, 0.2f)), Offset(x, r.top - 0.5f * u), Size(1.8f * u, r.height + 1f * u))
        drawRect(t.s(0.3f), Offset(x + 1.8f * u, r.top), Size(0.8f * u, r.height))
        // El canto de la izquierda, hacia la luz: la espiral de las vueltas de tela.
        val canto = Rect(r.left, r.top, r.left + ec * 2f, r.bottom)
        drawOval(t.c(mezcla(col, LUZ_MUEBLE, if (der) 0.04f else 0.2f)), canto.topLeft, canto.size)
        val c0 = canto.center
        val espiral = Path().apply {
            for (k in 0..40) {
                val a = k * 0.5f
                val f = 0.08f + 0.9f * k / 40f
                val p = Offset(c0.x + canto.width / 2 * f * cos(a), c0.y + canto.height / 2 * f * sin(a))
                if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
        }
        drawPath(espiral, t.c(mezcla(col, HOLLIN, 0.45f)), style = Stroke(0.6f * u))
        drawCircle(t.c(mezcla(col, HOLLIN, 0.6f)), canto.width * 0.07f, c0)
        drawArc(t.s(0.3f), -60f, 150f, false, canto.topLeft, canto.size, style = Stroke(1f * u))
        if (sello) drawRect(t.c(LACA).copy(alpha = 0.85f), Offset(c0.x - canto.width * 0.18f, c0.y + canto.height * 0.12f), Size(canto.width * 0.24f, canto.height * 0.24f))
        filo(r, t, der)
    }

    /**
     * Los rollos de seda: tres apilados atrás (añil, azafrán y verde) y uno carmesí delante, a medio desenrollar, cuyo paño
     * cae por el canto de la repisa con sus pliegues, su brillo y dos medallones de perlas tejidos.
     */
    private fun DrawScope.sedas(w: Float, pieA: Float, pieF: Float, primera: Float) {
        val u = density
        val rh = w * 0.05f
        val r1 = Rect(w * 0.43f, pieA - rh, w * 0.64f, pieA)
        val r2 = Rect(w * 0.45f, pieA - rh * 1.95f, w * 0.66f, pieA - rh * 0.95f)
        val r3 = Rect(w * 0.52f, pieA - rh * 2.9f, w * 0.65f, pieA - rh * 1.9f)
        sombraEnPared(Path().apply { listOf(r1, r2, r3).forEach { r -> addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(rh * 0.4f))) } }, r2.center, pieA, 0.2f)
        rollo(r1, INDIGO, 5220, sello = true)
        rollo(r2, AZAFRAN, 5221)
        rollo(r3, VERDE, 5222)
        // El carmesí de delante y su paño, que cae por el canto.
        val r4 = Rect(w * 0.455f, pieF - rh, w * 0.67f, pieF)
        val x0 = w * 0.47f; val x1 = w * 0.652f; val y0 = r4.bottom - rh * 0.35f; val y1 = primera - 0.5f * u
        val alto = y1 - y0
        // El paño se abre al caer: la orilla derecha se va hacia fuera y el ruedo ondula en tres caídas.
        val pano = Path().apply {
            moveTo(x0, y0)
            lineTo(x1, y0)
            cubicTo(x1 + w * 0.004f, y0 + alto * 0.3f, x1 + w * 0.022f, y0 + alto * 0.62f, x1 + w * 0.026f, y1 - w * 0.03f)
            cubicTo(x1 + w * 0.012f, y1 - w * 0.022f, x1 - w * 0.012f, y1 - w * 0.002f, x1 - w * 0.03f, y1 - w * 0.012f)
            cubicTo(x1 - w * 0.05f, y1 - w * 0.024f, x1 - w * 0.07f, y1 + w * 0.002f, x1 - w * 0.095f, y1 - w * 0.008f)
            cubicTo(x1 - w * 0.12f, y1 - w * 0.018f, x1 - w * 0.15f, y1 + w * 0.003f, x0 - w * 0.012f, y1 - w * 0.004f)
            cubicTo(x0 - w * 0.012f, y0 + alto * 0.6f, x0 - w * 0.002f, y0 + alto * 0.3f, x0, y0)
            close()
        }
        val tp = tn(Offset((x0 + x1) / 2, y0 + alto / 2))
        val der = luzDerecha(x0)
        clipRect(0f, y0, w, primera) { translate(if (der) -w * 0.008f else w * 0.01f, w * 0.006f) { drawPath(pano, tp.s(0.28f)) } }
        drawPath(pano, tp.c(CARMESI))
        clipPath(pano) {
            // Donde se dobla sobre el canto: brillo arriba y sombra debajo.
            val canto = y0 + (pieF + w * 0.004f - y0)
            drawRect(tp.b(0.35f), Offset(x0 - w * 0.02f, y0), Size(x1 - x0 + w * 0.04f, canto - y0))
            drawRect(tp.s(0.22f), Offset(x0 - w * 0.02f, canto), Size(x1 - x0 + w * 0.04f, w * 0.01f))
            // Los pliegues que bajan: sombra a un lado, brillo de seda al otro.
            for (j in 0 until 5) {
                val fx = x0 + (x1 - x0) * (0.1f + 0.2f * j)
                val sale = w * (0.004f + 0.006f * j)
                val onda = (if (j % 2 == 0) 1f else -1f) * w * 0.005f
                val pl = Path().apply {
                    moveTo(fx, canto)
                    cubicTo(fx + onda, canto + alto * 0.3f, fx + sale - onda, canto + alto * 0.55f, fx + sale * 1.4f, y1 + w * 0.01f)
                    lineTo(fx + sale * 1.4f + w * 0.012f, y1 + w * 0.01f)
                    cubicTo(fx + sale + w * 0.01f - onda, canto + alto * 0.55f, fx + w * 0.006f + onda, canto + alto * 0.3f, fx + w * 0.003f, canto)
                    close()
                }
                drawPath(pl, tp.s(0.26f))
                val br = Path().apply { moveTo(fx - w * 0.004f, canto + w * 0.004f); cubicTo(fx - w * 0.004f + onda, canto + alto * 0.3f, fx + sale - w * 0.006f - onda, canto + alto * 0.55f, fx + sale * 1.4f - w * 0.006f, y1) }
                drawPath(br, tp.b(0.55f), style = Stroke(1.1f * u))
            }
            // El vuelo del ruedo, que se ve por dentro en la esquina derecha.
            drawPath(poligono(x1 - w * 0.03f, y1 - w * 0.012f, x1 + w * 0.026f, y1 - w * 0.03f, x1 + w * 0.012f, y1 - w * 0.016f), tp.c(mezcla(CARMESI, HOLLIN, 0.3f)))
            // Dos medallones de perlas tejidos en oro.
            listOf(Offset(x0 + (x1 - x0) * 0.3f, canto + alto * 0.42f), Offset(x0 + (x1 - x0) * 0.75f, canto + alto * 0.36f)).forEach { c ->
                val rr = w * 0.017f
                for (q in 0 until 10) { val a = q * 0.628f; drawCircle(tp.c(ORO).copy(alpha = 0.75f), w * 0.0022f, Offset(c.x + rr * cos(a), c.y + rr * 0.85f * sin(a))) }
                drawPath(estrella(c, rr * 0.5f, 4, 0.4f, 0f), tp.c(ORO).copy(alpha = 0.6f))
            }
            // El orillo, al costado.
            drawPath(Path().apply { moveTo(x1, y0); cubicTo(x1 + w * 0.004f, y0 + alto * 0.3f, x1 + w * 0.022f, y0 + alto * 0.62f, x1 + w * 0.026f, y1 - w * 0.03f) }, tp.c(mezcla(CARMESI, AZAFRAN, 0.5f)), style = Stroke(w * 0.012f))
        }
        drawPath(pano, tp.s(0.25f), style = Stroke(0.6f * u))
        rollo(r4, CARMESI, 5223)
        // La vuelta suelta que se desprende del rollo.
        drawPath(Path().apply { moveTo(x0, r4.bottom - rh * 0.2f); cubicTo(x0 + w * 0.05f, r4.bottom + w * 0.004f, x1 - w * 0.05f, r4.bottom + w * 0.004f, x1, r4.bottom - rh * 0.2f) }, tp.b(0.5f), style = Stroke(1f * u))
    }

    /**
     * La jarra de plata sogdiana: el pie, el cuerpo de pera con su banda de perlas repujada, el cuello alto, la tapa en
     * cabeza de ave con el pico hacia delante y el asa con su perla; el reflejo de la seda en un costado.
     */
    private fun DrawScope.jarra(p: Offset, hasta: Float) {
        val u = density; val w = anchoW
        val cx = p.x; val pie = p.y
        val t = tn(Offset(cx, pie - w * 0.1f))
        val der = luzDerecha(cx)
        val cuerpo = Path().apply {
            moveTo(cx - w * 0.016f, pie - w * 0.012f)
            cubicTo(cx - w * 0.052f, pie - w * 0.02f, cx - w * 0.064f, pie - w * 0.062f, cx - w * 0.048f, pie - w * 0.092f)
            cubicTo(cx - w * 0.036f, pie - w * 0.115f, cx - w * 0.012f, pie - w * 0.12f, cx - w * 0.011f, pie - w * 0.14f)
            lineTo(cx - w * 0.011f, pie - w * 0.166f)
            lineTo(cx + w * 0.011f, pie - w * 0.166f)
            lineTo(cx + w * 0.011f, pie - w * 0.14f)
            cubicTo(cx + w * 0.012f, pie - w * 0.12f, cx + w * 0.036f, pie - w * 0.115f, cx + w * 0.048f, pie - w * 0.092f)
            cubicTo(cx + w * 0.064f, pie - w * 0.062f, cx + w * 0.052f, pie - w * 0.02f, cx + w * 0.016f, pie - w * 0.012f)
            close()
        }
        val piePath = poligono(cx - w * 0.026f, pie, cx + w * 0.026f, pie, cx + w * 0.016f, pie - w * 0.013f, cx - w * 0.016f, pie - w * 0.013f)
        val cabeza = Path().apply {
            moveTo(cx - w * 0.013f, pie - w * 0.163f)
            cubicTo(cx - w * 0.016f, pie - w * 0.19f, cx - w * 0.006f, pie - w * 0.206f, cx + w * 0.004f, pie - w * 0.205f)
            cubicTo(cx + w * 0.016f, pie - w * 0.2f, cx + w * 0.018f, pie - w * 0.18f, cx + w * 0.014f, pie - w * 0.163f)
            close()
        }
        val pico = poligono(cx - w * 0.01f, pie - w * 0.194f, cx - w * 0.048f, pie - w * 0.182f, cx - w * 0.011f, pie - w * 0.176f)
        val asa = Path().apply {
            moveTo(cx + w * 0.012f, pie - w * 0.156f)
            cubicTo(cx + w * 0.05f, pie - w * 0.17f, cx + w * 0.072f, pie - w * 0.13f, cx + w * 0.052f, pie - w * 0.078f)
        }
        val todo = Path().apply { addPath(cuerpo); addPath(piePath); addPath(cabeza); addPath(pico) }
        sombraEnPared(todo, Offset(cx, pie - w * 0.1f), hasta, 0.22f)
        if (!esNoche) translate(w * 0.02f, w * 0.012f) { clipRect(0f, 0f, size.width, hasta) { drawPath(asa, sombraMueble(0.2f), style = Stroke(2.6f * u)) } }
        contacto(Offset(cx + w * 0.012f, pie), w * 0.07f, w * 0.012f, 0.45f)
        // El asa, detrás del brillo.
        drawPath(asa, t.c(mezcla(PLATA, HOLLIN, 0.2f)), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.5f), style = Stroke(0.8f * u))
        drawCircle(t.c(PLATA), w * 0.005f, Offset(cx + w * 0.03f, pie - w * 0.167f))
        drawPath(piePath, t.c(mezcla(PLATA, HOLLIN, 0.15f)))
        drawPath(cuerpo, t.c(PLATA))
        clipPath(cuerpo) {
            val r = Rect(cx - w * 0.064f, pie - w * 0.17f, cx + w * 0.064f, pie)
            cilindro(r, t, 1.6f, der)
            // El reflejo carmesí de la seda a su izquierda, y el brillo fuerte de la plata.
            drawRect(t.c(CARMESI).copy(alpha = 0.18f), Offset(r.left, r.top), Size(r.width * 0.12f, r.height))
            val bx = if (der) cx + w * 0.022f else cx - w * 0.028f
            drawPath(Path().apply { moveTo(bx, pie - w * 0.03f); cubicTo(bx - w * 0.008f, pie - w * 0.06f, bx - w * 0.004f, pie - w * 0.09f, bx + w * 0.006f, pie - w * 0.105f) }, if (esNoche) LLAMA.copy(alpha = (0.9f * t.k).coerceAtMost(0.85f)) else luzMueble(0.8f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            // La banda de perlas repujada y el anillo del cuello.
            for (q in 0 until 12) {
                val fx = -0.9f + 1.8f * q / 11f
                val c = Offset(cx + w * 0.05f * fx, pie - w * 0.064f + w * 0.006f * (1f - fx * fx))
                drawCircle(t.s(0.3f), w * 0.0032f, c + Offset(0.5f * u, 0.5f * u))
                drawCircle(t.c(mezcla(PLATA, LUZ_MUEBLE, 0.3f)), w * 0.0028f, c)
            }
            drawRect(t.s(0.3f), Offset(cx - w * 0.02f, pie - w * 0.142f), Size(w * 0.04f, 1.2f * u))
            drawRect(t.b(0.5f), Offset(cx - w * 0.02f, pie - w * 0.145f), Size(w * 0.04f, 1f * u))
            // El ennegrecido de la plata abajo.
            drawRect(t.c(HOLLIN).copy(alpha = 0.12f), Offset(r.left, pie - w * 0.04f), Size(r.width, w * 0.03f))
        }
        // La cabeza de ave con su pico, y el ojo.
        drawPath(cabeza, t.c(PLATA))
        clipPath(cabeza) { drawRect(t.s(0.3f), Offset(cx + w * 0.004f, pie - w * 0.21f), Size(w * 0.02f, w * 0.05f)) }
        drawPath(pico, t.c(mezcla(PLATA, LUZ_MUEBLE, 0.1f)))
        drawLine(t.s(0.35f), Offset(cx - w * 0.011f, pie - w * 0.18f), Offset(cx - w * 0.045f, pie - w * 0.182f), 0.6f * u)
        drawCircle(t.c(HOLLIN), w * 0.0025f, Offset(cx - w * 0.001f, pie - w * 0.19f))
        destello(Offset(cx - w * 0.006f, pie - w * 0.198f), 1f * u, t)
        destello(Offset(if (der) cx + w * 0.03f else cx - w * 0.033f, pie - w * 0.078f), 1.4f * u, t)
    }

    /**
     * La lámpara de aceite: un candil de cerámica vidriada de turquesa, con su pico y su asa, sobre un pie de bronce con su
     * plato; de día el pabilo apagado y negro.
     */
    private fun DrawScope.lampara(p: Offset) {
        val u = density; val w = anchoW * 1.35f
        val cx = p.x; val pie = p.y
        val t = tn(Offset(cx, pie - w * 0.05f))
        val o = esNoche
        // El pie de bronce: la base, el vástago con sus nudos y el plato.
        contacto(Offset(cx + w * 0.008f, pie), w * 0.07f, w * 0.012f, 0.45f)
        val base = Path().apply {
            moveTo(cx - w * 0.028f, pie)
            cubicTo(cx - w * 0.026f, pie - w * 0.01f, cx - w * 0.008f, pie - w * 0.014f, cx - w * 0.004f, pie - w * 0.016f)
            lineTo(cx + w * 0.004f, pie - w * 0.016f)
            cubicTo(cx + w * 0.008f, pie - w * 0.014f, cx + w * 0.026f, pie - w * 0.01f, cx + w * 0.028f, pie)
            close()
        }
        val vastago = Rect(cx - w * 0.003f, pie - w * 0.076f, cx + w * 0.003f, pie - w * 0.014f)
        val silueta = Path().apply { addPath(base); addRect(vastago); addRect(Rect(cx - w * 0.042f, pie - w * 0.1f, cx + w * 0.03f, pie - w * 0.076f)) }
        sombraEnPared(silueta, Offset(cx, pie - w * 0.05f), pie, 0.2f)
        drawPath(base, t.c(BRONCE))
        clipPath(base) { drawRect(t.s(0.3f), Offset(cx + w * 0.004f, pie - w * 0.02f), Size(w * 0.03f, w * 0.02f)); drawRect(t.b(0.3f), Offset(cx - w * 0.02f, pie - w * 0.02f), Size(w * 0.008f, w * 0.02f)) }
        drawRect(t.c(BRONCE), vastago.topLeft, vastago.size)
        drawRect(t.s(0.35f), Offset(vastago.right - 1.2f * u, vastago.top), Size(1.2f * u, vastago.height))
        listOf(0.3f, 0.65f).forEach { f -> drawOval(t.c(mezcla(BRONCE, LUZ_MUEBLE, 0.15f)), Offset(cx - w * 0.007f, vastago.top + vastago.height * f), Size(w * 0.014f, w * 0.008f)) }
        drawOval(t.c(BRONCE), Offset(cx - w * 0.024f, pie - w * 0.08f), Size(w * 0.048f, w * 0.008f))
        drawOval(t.b(0.5f), Offset(cx - w * 0.024f, pie - w * 0.081f), Size(w * 0.048f, w * 0.004f), style = Stroke(0.6f * u))
        // El candil vidriado.
        val y = pie - w * 0.079f
        val candil = Path().apply {
            moveTo(cx + w * 0.024f, y)
            cubicTo(cx + w * 0.026f, y - w * 0.018f, cx - w * 0.006f, y - w * 0.026f, cx - w * 0.018f, y - w * 0.012f)
            lineTo(cx - w * 0.043f, y - w * 0.012f)
            cubicTo(cx - w * 0.047f, y - w * 0.009f, cx - w * 0.044f, y - w * 0.004f, cx - w * 0.038f, y - w * 0.004f)
            lineTo(cx - w * 0.018f, y)
            close()
        }
        drawOval(t.c(mezcla(TURQUESA, VERDE, 0.2f)), Offset(cx + w * 0.019f, y - w * 0.017f), Size(w * 0.012f, w * 0.012f), style = Stroke(1.6f * u))
        drawPath(candil, t.c(mezcla(TURQUESA, PAPEL, 0.12f)))
        clipPath(candil) {
            drawRect(t.s(0.3f), Offset(cx - w * 0.05f, y - w * 0.006f), Size(w * 0.08f, w * 0.01f))
            drawRect(if (o) t.b(0.4f) else luzMueble(0.3f), Offset(cx - w * 0.05f, y - w * 0.03f), Size(w * 0.08f, w * 0.011f))
            // El vidriado que chorrea y deja ver el barro abajo.
            drawRect(t.c(mezcla(ADOBE, LADRILLO, 0.3f)), Offset(cx - w * 0.03f, y - w * 0.003f), Size(w * 0.06f, w * 0.004f))
        }
        drawCircle(t.c(HOLLIN), w * 0.0035f, Offset(cx + w * 0.002f, y - w * 0.019f))
        destello(Offset(cx - w * 0.004f, y - w * 0.02f), 1f * u, t)
        // El pabilo: de día apagado, negro; de noche la llama la pinta mueble() al final.
        if (!o) {
            drawLine(HOLLIN, Offset(cx - w * 0.043f, y - w * 0.01f), Offset(cx - w * 0.047f, y - w * 0.016f), 1.4f * u, StrokeCap.Round)
            drawOval(HOLLIN.copy(alpha = 0.14f), Offset(cx - w * 0.055f, y - w * 0.08f), Size(w * 0.02f, w * 0.06f))
        }
    }

    /** La llama del candil, con su núcleo claro y un halo chico. */
    private fun DrawScope.llamaLampara(c: Offset) {
        val w = anchoW * 1.35f
        drawCircle(LLAMA.copy(alpha = 0.35f), w * 0.016f, c + Offset(0f, w * 0.002f))
        val llama = Path().apply {
            moveTo(c.x - w * 0.006f, c.y + w * 0.008f)
            cubicTo(c.x - w * 0.008f, c.y, c.x - w * 0.002f, c.y - w * 0.012f, c.x + w * 0.001f, c.y - w * 0.022f)
            cubicTo(c.x + w * 0.004f, c.y - w * 0.01f, c.x + w * 0.008f, c.y, c.x + w * 0.006f, c.y + w * 0.008f)
            close()
        }
        drawPath(llama, LLAMA)
        drawOval(Color(0xFFFFF4DA), Offset(c.x - w * 0.003f, c.y - w * 0.006f), Size(w * 0.006f, w * 0.012f))
    }

    /** La sarta de monedas de agujero cuadrado: tendida en el canto de la repisa, con una vuelta que cuelga y el nudo con su borla. */
    private fun DrawScope.monedas(w: Float, canto: Float, primera: Float) {
        val u = density
        val t = tn(Offset(w * 0.77f, canto))
        fun bez(a: Offset, b: Offset, c: Offset, d: Offset, s: Float): Offset {
            val m = 1f - s
            return a * (m * m * m) + b * (3 * m * m * s) + c * (3 * m * s * s) + d * (s * s * s)
        }
        val a = Offset(w * 0.68f, canto - w * 0.004f)
        val d = Offset(w * 0.8f, canto - w * 0.002f)
        val b = Offset(w * 0.71f, canto + w * 0.08f)
        val c = Offset(w * 0.78f, canto + w * 0.07f)
        val cuerda = Path().apply { moveTo(a.x, a.y); cubicTo(b.x, b.y, c.x, c.y, d.x, d.y) }
        translate(if (esNoche) -w * 0.006f else w * 0.008f, w * 0.005f) { drawPath(cuerda, t.s(0.28f), style = Stroke(w * 0.012f)) }
        drawPath(cuerda, t.c(CUERDA), style = Stroke(1f * u))
        for (k in 0 until 15) {
            val s = 0.04f + 0.92f * k / 14f
            val p = bez(a, b, c, d, s)
            moneda(p, w * 0.0075f, t, 5230 + k, plana = s < 0.12f || s > 0.88f)
        }
        // El nudo y la borla.
        drawCircle(t.c(CUERDA), w * 0.004f, d + Offset(w * 0.004f, 0f))
        for (k in 0 until 4) drawLine(t.c(CARMESI), d + Offset(w * 0.006f, 0f), d + Offset(w * (0.008f + 0.004f * k), w * 0.022f), 1f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- al pie

    /** La tarima de álamo: la tabla vista desde arriba, su frente de dos tablones agrietados, los tacos, sus sombras y una esquina gastada. */
    private fun DrawScope.tarima(b: Rect, w: Float, suelo: Float) {
        val u = density
        val hb = b.height
        val x0 = w * 0.02f; val x1 = w * 0.79f
        val arriba = Rect(x0, b.top + hb * TT, x1, b.top + hb * TF)
        val frente = Rect(x0, b.top + hb * TF, x1, b.top + hb * TB)
        val pisan = b.top + hb * PISAN
        // Las sombras: en el muro, a la derecha, y en el suelo, debajo.
        val ts = tn(Offset(x1, suelo))
        drawPath(poligono(x1, arriba.top, x1 + w * 0.022f, arriba.top + w * 0.012f, x1 + w * 0.022f, suelo, x1, suelo), ts.s(0.3f))
        drawRect(ts.s(0.35f), Offset(x0 + w * 0.02f, frente.bottom), Size(x1 - x0 + w * 0.012f, pisan - frente.bottom + hb * 0.04f))
        drawRect(ts.s(0.15f), Offset(x0 + w * 0.03f, pisan + hb * 0.04f), Size(x1 - x0, hb * 0.05f))
        // Los tacos.
        listOf(x0 + w * 0.01f, (x0 + x1) / 2 - w * 0.03f, x1 - w * 0.07f).forEach { x ->
            val t = tn(Offset(x, pisan))
            drawRect(t.c(ALAMO_OSC), Offset(x, frente.bottom), Size(w * 0.06f, pisan - frente.bottom))
            drawRect(t.s(0.3f), Offset(x + w * 0.045f, frente.bottom), Size(w * 0.015f, pisan - frente.bottom))
        }
        // La tabla de arriba.
        for (k in 0 until 8) {
            val xx = x0 + (x1 - x0) * k / 8f
            val t = tn(Offset(xx, arriba.center.y))
            drawRect(t.c(mezcla(ALAMO, ALAMO_LUZ, 0.5f)), Offset(xx, arriba.top), Size((x1 - x0) / 8f + 1f, arriba.height))
        }
        drawRect(tn(arriba.center).s(0.3f), arriba.topLeft, Size(arriba.width, 2f * u))
        drawLine(tn(arriba.center).s(0.3f), Offset(x0, arriba.center.y), Offset(x1, arriba.center.y), 0.8f * u)
        vetas(arriba, (if (esNoche) NOCHE else ALAMO_OSC).copy(alpha = 0.22f), 5300, 0.6f, grosor = 0.5f, ondulacion = 0.3f)
        // El frente: dos tablones.
        tabla(Rect(x0, frente.top, x1, frente.center.y), 5301)
        tabla(Rect(x0, frente.center.y, x1, frente.bottom), 5302)
        // El costado derecho, en sombra, y la esquina gastada de la izquierda.
        val tc = tn(Offset(x1, frente.center.y))
        drawRect(tc.c(ALAMO_OSC), Offset(x1 - w * 0.01f, arriba.top), Size(w * 0.01f, frente.bottom - arriba.top))
        drawRect(tc.s(0.3f), Offset(x1 - w * 0.01f, arriba.top), Size(w * 0.01f, frente.bottom - arriba.top))
        val tg = tn(Offset(x0, frente.top))
        drawPath(poligono(x0, frente.top - w * 0.004f, x0 + w * 0.03f, frente.top, x0, frente.top + w * 0.022f), tg.c(mezcla(ALAMO_LUZ, ALAMO, 0.4f)))
        drawLine(tg.s(0.4f), Offset(x0 + w * 0.03f, frente.top), Offset(x0, frente.top + w * 0.022f), 0.7f * u)
        // Los clavos de forja.
        listOf(0.06f, 0.5f, 0.94f).forEach { f ->
            listOf(frente.top + frente.height * 0.25f, frente.top + frente.height * 0.75f).forEach { yy ->
                val p = Offset(x0 + (x1 - x0) * f, yy)
                val t = tn(p)
                drawCircle(t.c(HOLLIN), 1.3f * u, p)
                drawCircle(t.b(0.35f), 0.5f * u, p - Offset(0.4f * u, 0.4f * u))
            }
        }
    }

    /** Un trozo de kilim dentro de [r]: el campo rojo, las bandas de arriba y abajo y una hilera de rombos escalonados. */
    private fun DrawScope.kilim(r: Rect, t: Tono, s: Int) {
        val u = density
        drawRect(t.c(mezcla(CARMESI, LACA, 0.4f)), r.topLeft, r.size)
        listOf(0.06f to PAPEL, 0.13f to INDIGO, 0.84f to INDIGO, 0.92f to PAPEL).forEach { (f, col) ->
            drawRect(t.c(if (col == PAPEL) mezcla(PAPEL, YUTE, 0.25f) else col), Offset(r.left, r.top + r.height * f), Size(r.width, r.height * 0.05f))
        }
        // Los dientes de sierra de la banda.
        val paso = r.height * 0.08f
        var x = r.left
        while (x < r.right) {
            drawPath(poligono(x, r.top + r.height * 0.24f, x + paso / 2, r.top + r.height * 0.19f, x + paso, r.top + r.height * 0.24f), t.c(AZAFRAN))
            drawPath(poligono(x, r.top + r.height * 0.78f, x + paso / 2, r.top + r.height * 0.83f, x + paso, r.top + r.height * 0.78f), t.c(AZAFRAN))
            x += paso
        }
        // Los rombos escalonados con sus ganchos.
        val n = 3
        for (i in 0 until n) {
            val c = Offset(r.left + r.width * (i + 0.5f) / n, r.top + r.height * 0.51f)
            val rh = r.height * 0.24f; val rw = r.width / n * 0.36f
            drawPath(poligono(c.x, c.y - rh, c.x + rw, c.y, c.x, c.y + rh, c.x - rw, c.y), t.c(if (i % 2 == 0) INDIGO else mezcla(VERDE, INDIGO, 0.3f)))
            drawPath(poligono(c.x, c.y - rh * 0.6f, c.x + rw * 0.6f, c.y, c.x, c.y + rh * 0.6f, c.x - rw * 0.6f, c.y), t.c(AZAFRAN))
            drawPath(poligono(c.x, c.y - rh * 0.25f, c.x + rw * 0.25f, c.y, c.x, c.y + rh * 0.25f, c.x - rw * 0.25f, c.y), t.c(PAPEL))
            listOf(-1f, 1f).forEach { sx ->
                drawLine(t.c(INDIGO), Offset(c.x + sx * rw, c.y), Offset(c.x + sx * rw * 1.3f, c.y - rh * 0.3f), 1.2f * u)
                drawLine(t.c(INDIGO), Offset(c.x + sx * rw, c.y), Offset(c.x + sx * rw * 1.3f, c.y + rh * 0.3f), 1.2f * u)
            }
        }
        // La trama del tejido.
        var y = r.top
        while (y < r.bottom) { drawLine(t.s(0.08f), Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += 2.4f * u }
        pinceladas(r, if (t.o) NOCHE else HOLLIN, s, 6, u, 0.1f)
    }

    /**
     * La alforja de camello, de kilim, a medio cargar: un bolsillo tumbado sobre la tarima, abierto, con un rollo de fieltro
     * que asoma; el otro cae por el frente con sus borlas; al lado, la ropa doblada que falta meter.
     */
    private fun DrawScope.alforja(b: Rect, w: Float) {
        val u = density
        val hb = b.height
        val borde = b.top + hb * TF
        val t = tn(Offset(w * 0.22f, borde))
        val der = luzDerecha(w * 0.22f)
        // La ropa doblada, al lado: tres capas de tela.
        val px = w * 0.41f; val pw = w * 0.18f
        var py = b.top + hb * (TT + TF) / 2 + w * 0.006f
        contacto(Offset(px + pw / 2 + w * 0.01f, py), pw * 1.1f, w * 0.012f, 0.4f)
        listOf(INDIGO, mezcla(PAPEL, YUTE, 0.3f), VERDE).forEachIndexed { i, col ->
            val r = Rect(px + w * 0.004f * i, py - w * 0.018f, px + pw - w * 0.006f * i, py)
            drawRoundRect(t.c(col), r.topLeft, r.size, CornerRadius(w * 0.006f))
            drawRect(t.b(0.4f), Offset(r.left + w * 0.006f, r.top), Size(r.width - w * 0.012f, 1f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            drawRect(t.s(0.2f), Offset(r.right - w * 0.012f, r.top), Size(w * 0.012f, r.height))
            py -= w * 0.018f
        }
        // La bolsa de encima, tumbada sobre la tarima y abierta.
        val ab = Rect(w * 0.045f, b.top + hb * TT - w * 0.05f, w * 0.3f, borde + w * 0.004f)
        val bolsa = Path().apply {
            moveTo(ab.left, ab.bottom)
            cubicTo(ab.left - w * 0.006f, ab.top + w * 0.03f, ab.left + w * 0.02f, ab.top + w * 0.012f, ab.left + w * 0.06f, ab.top + w * 0.014f)
            cubicTo(ab.left + w * 0.12f, ab.top + w * 0.02f, ab.right - w * 0.07f, ab.top - w * 0.004f, ab.right - w * 0.02f, ab.top + w * 0.012f)
            cubicTo(ab.right + w * 0.008f, ab.top + w * 0.03f, ab.right + w * 0.004f, ab.bottom - w * 0.01f, ab.right, ab.bottom)
            close()
        }
        contacto(Offset(ab.center.x + w * 0.02f, ab.bottom - w * 0.004f), ab.width * 1.05f, w * 0.014f, 0.4f)
        // Lo que asoma por la boca: un rollo de fieltro y una punta de seda verde.
        val boca = Offset(ab.left + w * 0.15f, ab.top + w * 0.014f)
        drawRoundRect(t.c(mezcla(YUTE, PAPEL, 0.2f)), Offset(boca.x - w * 0.05f, boca.y - w * 0.026f), Size(w * 0.1f, w * 0.034f), CornerRadius(w * 0.017f))
        drawOval(t.c(mezcla(YUTE, PAPEL, 0.45f)), Offset(boca.x - w * 0.056f, boca.y - w * 0.026f), Size(w * 0.016f, w * 0.034f))
        drawOval(t.s(0.25f), Offset(boca.x - w * 0.052f, boca.y - w * 0.018f), Size(w * 0.008f, w * 0.018f), style = Stroke(0.6f * u))
        drawRect(t.s(0.2f), Offset(boca.x - w * 0.04f, boca.y - w * 0.005f), Size(w * 0.09f, w * 0.012f))
        drawPath(poligono(boca.x + w * 0.04f, boca.y - w * 0.004f, boca.x + w * 0.075f, boca.y - w * 0.03f, boca.x + w * 0.085f, boca.y - w * 0.018f, boca.x + w * 0.06f, boca.y + w * 0.004f), t.c(VERDE))
        drawLine(t.b(0.5f), Offset(boca.x + w * 0.05f, boca.y - w * 0.01f), Offset(boca.x + w * 0.076f, boca.y - w * 0.026f), 0.8f * u)
        drawPath(bolsa, t.c(CARMESI))
        clipPath(bolsa) {
            kilim(Rect(ab.left - w * 0.01f, ab.top, ab.right + w * 0.01f, ab.bottom), t, 5310)
            tumbado(ab, t, 1.2f)
            // La boca abierta: el revés de la solapa, doblado hacia atrás.
            drawRect(t.c(mezcla(YUTE, HOLLIN, 0.2f)), Offset(ab.left, ab.top), Size(ab.width, w * 0.018f))
            drawRect(t.s(0.3f), Offset(ab.left, ab.top + w * 0.018f), Size(ab.width, 1.4f * u))
        }
        // Una borla en la esquina de la bolsa de encima.
        borla(Offset(ab.left + w * 0.006f, ab.bottom - w * 0.006f), t, w)

        // La banda que une las dos bolsas: pasa por el canto de la tarima y baja.
        val fr = Rect(w * 0.13f, borde + w * 0.03f, w * 0.37f, borde + w * 0.108f)
        val banda = Rect(w * 0.19f, ab.bottom - w * 0.012f, w * 0.27f, fr.top + w * 0.006f)
        val tbd = tn(banda.center)
        translate(if (esNoche) -w * 0.006f else w * 0.008f, w * 0.005f) { drawRect(tbd.s(0.3f), banda.topLeft, banda.size) }
        drawRect(tbd.c(mezcla(CARMESI, LACA, 0.3f)), banda.topLeft, banda.size)
        listOf(0.12f to PAPEL, 0.3f to INDIGO, 0.5f to AZAFRAN, 0.7f to INDIGO, 0.88f to PAPEL).forEach { (f, col) ->
            drawRect(tbd.c(if (col == PAPEL) mezcla(PAPEL, YUTE, 0.25f) else col), Offset(banda.left + banda.width * f - w * 0.004f, banda.top), Size(w * 0.008f, banda.height))
        }
        // El doblez sobre el canto: luz arriba, sombra debajo.
        drawRect(tbd.b(0.45f), Offset(banda.left, borde - w * 0.004f), Size(banda.width, w * 0.006f))
        drawRect(tbd.s(0.3f), Offset(banda.left, borde + w * 0.004f), Size(banda.width, w * 0.006f))
        drawRect(tbd.s(0.25f), Offset(banda.right - w * 0.008f, banda.top), Size(w * 0.008f, banda.height))

        // La bolsa de delante, colgando por el frente de la tarima: llena, con su solapa en pico y el cierre de lazo.
        val cae = Path().apply {
            moveTo(fr.left, fr.top)
            lineTo(fr.right, fr.top)
            cubicTo(fr.right + w * 0.008f, fr.top + fr.height * 0.4f, fr.right + w * 0.006f, fr.bottom - w * 0.016f, fr.right - w * 0.016f, fr.bottom)
            cubicTo(fr.right - w * 0.08f, fr.bottom + w * 0.008f, fr.left + w * 0.08f, fr.bottom + w * 0.006f, fr.left + w * 0.014f, fr.bottom - w * 0.002f)
            cubicTo(fr.left - w * 0.008f, fr.bottom - w * 0.02f, fr.left - w * 0.006f, fr.top + fr.height * 0.4f, fr.left, fr.top)
            close()
        }
        val tf = tn(fr.center)
        translate(if (esNoche) -w * 0.01f else w * 0.012f, w * 0.008f) { drawPath(cae, tf.s(0.3f)) }
        drawPath(cae, tf.c(CARMESI))
        clipPath(cae) {
            kilim(Rect(fr.left - w * 0.01f, fr.top + w * 0.02f, fr.right + w * 0.01f, fr.bottom + w * 0.004f), tf, 5311)
            // El bulto de lo que ya va dentro: claro arriba a la izquierda, sombra abajo y al costado.
            drawRect(tf.s(0.22f), Offset(fr.left, fr.bottom - fr.height * 0.28f), Size(fr.width, fr.height * 0.28f))
            drawRect(tf.s(0.2f), Offset(fr.right - fr.width * 0.14f, fr.top), Size(fr.width * 0.14f, fr.height))
            drawRect(tf.b(0.18f), Offset(fr.left, fr.top), Size(fr.width * 0.18f, fr.height))
        }
        // La solapa en pico, de otro tejido (añil con una greca), con su sombra sobre la bolsa.
        val pico = Offset(fr.center.x, fr.top + fr.height * 0.5f)
        val solapa = poligono(fr.left - w * 0.004f, fr.top, fr.right + w * 0.004f, fr.top, fr.right + w * 0.002f, fr.top + fr.height * 0.18f, pico.x, pico.y, fr.left - w * 0.002f, fr.top + fr.height * 0.18f)
        translate(0f, w * 0.006f) { drawPath(solapa, tf.s(0.3f)) }
        drawPath(solapa, tf.c(mezcla(INDIGO, NOCHE, 0.15f)))
        clipPath(solapa) {
            var x = fr.left
            var k = 0
            while (x < fr.right) {
                drawPath(poligono(x, fr.top + fr.height * 0.08f, x + w * 0.012f, fr.top + fr.height * 0.02f, x + w * 0.024f, fr.top + fr.height * 0.08f), tf.c(if (k % 2 == 0) AZAFRAN else PAPEL))
                x += w * 0.024f; k++
            }
            drawRect(tf.b(0.3f), Offset(fr.left, fr.top), Size(fr.width, w * 0.004f))
        }
        drawPath(solapa, tf.c(mezcla(CARMESI, HOLLIN, 0.2f)), style = Stroke(1.2f * u))
        // El cierre: una presilla de cordón en el pico que abraza un botón de madera.
        val boton = Offset(pico.x, pico.y + w * 0.012f)
        drawCircle(tf.s(0.35f), w * 0.008f, boton + Offset(1.2f * u, 1.4f * u))
        drawCircle(tf.c(mezcla(ALAMO, CUERO, 0.4f)), w * 0.008f, boton)
        drawCircle(tf.b(0.4f), w * 0.003f, boton - Offset(w * 0.002f, w * 0.002f))
        drawOval(tf.c(CUERDA), Offset(pico.x - w * 0.007f, pico.y - w * 0.004f), Size(w * 0.014f, w * 0.026f), style = Stroke(1.3f * u))
        // Las borlas de las esquinas de abajo.
        borla(Offset(fr.left + w * 0.014f, fr.bottom - w * 0.002f), tf, w)
        borla(Offset(fr.right - w * 0.016f, fr.bottom), tf, w)
        filo(fr, tf, true)
    }

    /** Una borla de lana: el nudo y los hilos que cuelgan. */
    private fun DrawScope.borla(p: Offset, t: Tono, w: Float) {
        val u = density
        drawCircle(t.c(INDIGO), w * 0.005f, p)
        for (k in -2..2) drawLine(t.c(if (k % 2 == 0) AZAFRAN else INDIGO), p, p + Offset(k * w * 0.003f, w * 0.022f), 1.1f * u, StrokeCap.Round)
    }

    /** El fardo de yute en el suelo, cortado por el borde: atado con dos cuerdas y el sello de arcilla del mercader en el nudo. */
    private fun DrawScope.fardo(p: Offset, w: Float) {
        val u = density
        val r = Rect(p.x, p.y - w * 0.17f, p.x + w * 0.28f, p.y)
        val t = tn(r.center)
        val forma = Path().apply {
            moveTo(r.left + w * 0.02f, r.bottom)
            cubicTo(r.left - w * 0.012f, r.bottom - w * 0.05f, r.left - w * 0.006f, r.top + w * 0.04f, r.left + w * 0.03f, r.top + w * 0.012f)
            cubicTo(r.left + w * 0.08f, r.top - w * 0.012f, r.left + w * 0.16f, r.top + w * 0.01f, r.right, r.top)
            lineTo(r.right, r.bottom)
            close()
        }
        // Su sombra en el muro (a la izquierda no: la luz viene de ahí) y en el suelo.
        contacto(Offset(r.center.x + w * 0.02f, r.bottom), r.width * 1.1f, w * 0.022f, 0.45f)
        drawPath(forma, t.c(YUTE))
        clipPath(forma) {
            // La trama de la tela basta.
            var x = r.left
            while (x < r.right) { drawLine(t.s(0.07f), Offset(x, r.top), Offset(x + w * 0.004f, r.bottom), 0.5f * u); x += 2.6f * u }
            var y = r.top
            while (y < r.bottom) { drawLine(t.b(0.06f), Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += 2.6f * u }
            cilindro(Rect(r.left - w * 0.01f, r.top, r.left + w * 0.2f, r.bottom), t, 1.1f)
            tumbado(r, t, 0.8f)
            pinceladas(r, if (esNoche) NOCHE else HOLLIN, 5320, 8, u, 0.12f)
            // Las arrugas que tiran de las cuerdas.
            listOf(0.3f, 0.55f, 0.75f).forEach { f ->
                val c = Offset(r.left + w * 0.09f, r.top + r.height * 0.45f)
                drawLine(t.s(0.3f), c, Offset(r.left + w * 0.02f + w * 0.2f * f, r.top + r.height * (0.1f + 0.9f * f)), 0.8f * u)
            }
        }
        // Las cuerdas: una de arriba abajo y otra de lado a lado.
        val nudo = Offset(r.left + w * 0.09f, r.top + r.height * 0.45f)
        val c1 = Path().apply { moveTo(nudo.x - w * 0.01f, r.top + w * 0.004f); cubicTo(nudo.x - w * 0.004f, nudo.y - w * 0.02f, nudo.x + w * 0.004f, nudo.y + w * 0.04f, nudo.x - w * 0.002f, r.bottom) }
        val c2 = Path().apply { moveTo(r.left - w * 0.004f, nudo.y + w * 0.012f); cubicTo(nudo.x - w * 0.03f, nudo.y + w * 0.004f, nudo.x + w * 0.06f, nudo.y - w * 0.008f, r.right, nudo.y + w * 0.006f) }
        listOf(c1, c2).forEach { c ->
            translate(1.2f * u, 1.4f * u) { drawPath(c, t.s(0.3f), style = Stroke(2.2f * u)) }
            drawPath(c, t.c(CUERDA), style = Stroke(2.2f * u))
            drawPath(c, t.s(0.3f), style = Stroke(2.2f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.2f * u))))
        }
        // El sello de arcilla con la marca del mercader.
        val rs = w * 0.02f
        drawCircle(t.s(0.35f), rs, nudo + Offset(1.6f * u, 1.8f * u))
        drawCircle(t.c(mezcla(LADRILLO, ADOBE_OSC, 0.25f)), rs, nudo)
        drawCircle(t.s(0.3f), rs * 0.68f, nudo, style = Stroke(0.8f * u))
        drawPath(estrella(nudo, rs * 0.42f, 4, 0.35f, 0f), t.s(0.4f))
        for (q in 0 until 4) { val a = q * 1.5708f + 0.785f; drawCircle(t.s(0.4f), rs * 0.08f, Offset(nudo.x + rs * 0.5f * cos(a), nudo.y + rs * 0.5f * sin(a))) }
        drawArc(t.b(0.5f), 180f, 90f, false, Offset(nudo.x - rs * 0.9f, nudo.y - rs * 0.9f), Size(rs * 1.8f, rs * 1.8f), style = Stroke(0.8f * u))
        filo(r, t, false)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega de la lámpara.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro
        fondoHueco(w, h, t, c)
        if (c.vacio) { libre(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cesto(w, h, c, t, der)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> caja(w, h, c, m, t, der)
                    1 -> estuche(w, h, c, m, t, der)
                    else -> fardoSeda(w, h, c, m, t, der)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: el barro del fondo del nicho con su paja, la sombra de la tabla de arriba en escalones, la del
     * poste o la jamba (de día a la izquierda; de noche, del lado contrario a la lámpara) y la tabla del piso.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono, c: EstadoCajon) {
        val u = density
        drawRect(t.c(NICHO), Offset.Zero, Size(w, h))
        pinceladas(Rect(0f, 0f, w, h), if (t.o) NOCHE else ADOBE_OSC, 5400 + c.fila * 3 + c.col, 8, u, 0.12f)
        for (i in 0 until 9) {
            val p = Offset(w * azar(5410 + c.col, i + c.fila * 9), h * 0.8f * azar(5420 + c.col, i + c.fila * 9))
            val a = azar(5430, i) * 3.14f
            drawLine(t.c(mezcla(NICHO, PAPEL, 0.5f)).copy(alpha = 0.7f), p, p + Offset(3.5f * u * cos(a), 3.5f * u * sin(a)), 0.6f * u)
        }
        // La sombra de la repisa de arriba, en escalones, y el costado de la izquierda en sombra (el poste o la jamba).
        listOf(0.34f to 0.1f, 0.2f to 0.14f, 0.09f to 0.28f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.22f), Offset.Zero, Size(w * 0.16f, h))
        drawRect(t.s(0.18f), Offset.Zero, Size(w * 0.06f, h))
        drawRect(t.b(0.12f), Offset(w * 0.62f, h * 0.34f), Size(w * 0.38f, h * 0.54f))
        // La tabla del piso.
        val piso = h * PISO
        drawRect(t.c(mezcla(ALAMO, ALAMO_LUZ, 0.5f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.s(0.3f), Offset(0f, piso), Size(w, 1.6f * u))
        drawLine(t.s(0.15f), Offset(0f, piso + (h - piso) * 0.55f), Offset(w, piso + (h - piso) * 0.5f), 0.6f * u)
    }

    /** El papel o la tablilla del tema, con su sombra; de noche, más hundida cuanto más lejos de la lámpara. */
    private fun DrawScope.etiqueta(w: Float, h: Float, t: Tono, papel: Color = PAPEL, filete: Color? = null, madera: Boolean = false) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.28f), Offset(r.left + (if (t.o) -1.2f else 1.2f) * u, r.top + 1.6f * u), r.size)
        val fondo = if (t.o) mezcla(mezcla(papel, PAPEL_LEJOS, 0.9f), mezcla(papel, LLAMA, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(papel)
        if (madera) drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(3f * u)) else drawRect(fondo, r.topLeft, r.size)
        if (madera) {
            // La veta tenue de la tablilla, por los bordes (no estorba la letra).
            listOf(0.12f, 0.88f).forEach { f -> drawLine(t.s(0.12f), Offset(r.left + 3f * u, r.top + r.height * f), Offset(r.right - 3f * u, r.top + r.height * f + 0.8f * u), 0.6f * u) }
            drawRect(t.s(0.15f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        }
        if (filete != null) drawRect(filete.copy(alpha = 0.6f), Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), style = Stroke(0.8f * u))
        drawRect(t.s(0.1f), Offset(r.right - r.width * 0.04f, r.top), Size(r.width * 0.04f, r.height))
        drawRect(t.b(0.45f), r.topLeft, Size(r.width, 0.8f * u))
    }

    /** Las tiras de bambú que asoman entre [x0] y [x1] por encima de [arriba]: más y más altas cuanto más guarda, con su nudo y unas letras. */
    private fun DrawScope.tiras(x0: Float, x1: Float, arriba: Float, h: Float, c: EstadoCajon, t: Tono, cuantasMax: Int = 7, extra: Float = 0f) {
        val u = density
        val n = c.n.coerceIn(0, 12)
        val cuantas = (2 + n / 2).coerceIn(2, cuantasMax)
        val sube = h * (0.06f + 0.016f * n + extra)
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val tw = (x1 - x0) * 0.07f + 2f * u
            val x = x0 + (x1 - x0 - tw) * (f * 0.92f + 0.04f * azar(c.semilla, i + 30))
            val y = (arriba - sube * (0.45f + 0.55f * azar(c.semilla, i))).coerceAtLeast(h * 0.03f + h * 0.04f * azar(c.semilla, i + 50))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 9f
            rotate(g, Offset(x + tw / 2, arriba)) {
                drawRect(t.s(0.22f), Offset(x + 1.4f * u, y + 1.4f * u), Size(tw, arriba - y))
                drawRect(t.c(mezcla(BAMBU, AZAFRAN, 0.15f * (i % 2))), Offset(x, y), Size(tw, arriba - y + 2f * u))
                drawRect(t.b(0.45f), Offset(x, y), Size(tw * 0.35f, arriba - y))
                drawRect(t.s(0.18f), Offset(x + tw * 0.7f, y), Size(tw * 0.3f, arriba - y))
                drawLine(t.c(mezcla(BAMBU, ALAMO_OSC, 0.5f)), Offset(x, y + (arriba - y) * 0.45f), Offset(x + tw, y + (arriba - y) * 0.45f), 0.8f * u)
                for (q in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.55f), Offset(x + tw * 0.3f, y + 3f * u + q * 3f * u), Offset(x + tw * 0.7f, y + 3.6f * u + q * 3f * u), 0.7f * u)
            }
        }
    }

    /**
     * La caja de laca roja: el cuerpo con volutas de nubes talladas, el filete de oro y las cantoneras de latón; la tapa,
     * levantada por las tiras que la llenan (con muchas, asoman también por detrás), con su cerradura; la tira de papel.
     */
    private fun DrawScope.caja(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val laca = when (Math.floorMod(m ushr 3, 3)) { 0 -> LACA; 1 -> mezcla(LACA, CARMESI, 0.5f); else -> mezcla(LACA, HOLLIN, 0.22f) }
        val lacaO = mezcla(laca, HOLLIN, 0.4f)
        val sube = h * 0.008f * c.n.coerceIn(0, 8)
        val cuerpo = Rect(w * 0.07f, h * 0.36f, w * 0.93f, piso)
        val tapa = Rect(w * 0.045f, h * 0.22f - sube, w * 0.955f, h * 0.36f - sube + 1f * u)
        drawRect(t.s(0.26f), Offset(cuerpo.left + (if (t.o) -w * 0.035f else w * 0.035f), tapa.top + h * 0.04f), Size(cuerpo.width, piso - tapa.top - h * 0.04f))
        contacto(Offset(cuerpo.center.x + w * 0.04f, piso), cuerpo.width * 1.05f, h * 0.05f, 0.42f)
        // Las tiras asoman por detrás de la tapa, que no cierra: más y más altas cuanto más guarda.
        tiras(cuerpo.left + w * 0.08f, cuerpo.right - w * 0.08f, tapa.top + h * 0.04f, h, c, t, 7, 0.12f)
        // La rendija entre tapa y cuerpo, con las tiras.
        if (sube > 1f) {
            drawRect(t.c(LACA_NEGRA), Offset(cuerpo.left + 3f * u, tapa.bottom - 2f * u), Size(cuerpo.width - 6f * u, cuerpo.top - tapa.bottom + 4f * u))
            val k = c.n.coerceIn(0, 9)
            for (i in 0 until k) {
                val x = cuerpo.left + cuerpo.width * (0.08f + 0.095f * i)
                drawRect(t.c(BAMBU), Offset(x, tapa.bottom - 1f * u), Size(cuerpo.width * 0.06f, cuerpo.top - tapa.bottom + 2f * u))
                drawRect(t.s(0.2f), Offset(x + cuerpo.width * 0.045f, tapa.bottom - 1f * u), Size(cuerpo.width * 0.015f, cuerpo.top - tapa.bottom + 2f * u))
            }
        }
        // El cuerpo.
        drawRect(t.c(laca), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            val p = w * 0.07f
            var yy = cuerpo.top + p * 0.35f; var fila = 0
            while (yy < cuerpo.bottom) {
                var xx = cuerpo.left + (if (fila % 2 == 0) 0f else p / 2)
                while (xx < cuerpo.right) {
                    drawArc(t.c(lacaO), 180f, 270f, false, Offset(xx - p * 0.28f, yy - p * 0.28f), Size(p * 0.56f, p * 0.56f), style = Stroke(1.1f * u))
                    drawArc(t.b(0.25f), 190f, 90f, false, Offset(xx - p * 0.28f + 0.8f * u, yy - p * 0.28f + 0.8f * u), Size(p * 0.56f, p * 0.56f), style = Stroke(0.6f * u))
                    xx += p
                }
                yy += p * 0.62f; fila++
            }
            cilindro(cuerpo, t, 0.9f, der)
            // El desgaste: la madera asoma en los cantos.
            listOf(Offset(cuerpo.left, cuerpo.bottom), Offset(cuerpo.right, cuerpo.top + cuerpo.height * 0.3f)).forEach { q -> drawCircle(t.c(mezcla(CUERO, laca, 0.4f)), w * 0.022f, q) }
        }
        drawRect(t.c(ORO).copy(alpha = 0.85f), Offset(cuerpo.left + w * 0.025f, cuerpo.top + w * 0.02f), Size(cuerpo.width - w * 0.05f, cuerpo.height - w * 0.035f), style = Stroke(1f * u))
        // Las cantoneras de latón.
        listOf(cuerpo.left, cuerpo.right).forEach { x ->
            val s = if (x < w / 2) 1f else -1f
            drawPath(poligono(x, cuerpo.bottom, x + s * w * 0.05f, cuerpo.bottom, x, cuerpo.bottom - w * 0.05f), t.c(ORO))
            drawCircle(t.c(lacaO), 0.9f * u, Offset(x + s * w * 0.014f, cuerpo.bottom - w * 0.014f))
        }
        filo(cuerpo, t, der)
        // La tapa, un poco ladeada si va muy llena.
        rotate(if (c.n > 6) -1.6f else 0f, Offset(tapa.right, tapa.bottom)) {
            drawRect(t.s(0.3f), Offset(tapa.left + 1.5f * u, tapa.bottom), Size(tapa.width, 2f * u))
            drawRoundRect(t.c(laca), tapa.topLeft, tapa.size, CornerRadius(3f * u))
            drawRect(t.c(mezcla(laca, LUZ_MUEBLE, 0.1f)), tapa.topLeft, Size(tapa.width, tapa.height * 0.3f))
            drawRect(t.c(lacaO), Offset(tapa.left, tapa.bottom - 2.5f * u), Size(tapa.width, 2.5f * u))
            drawRect(t.b(0.55f), tapa.topLeft, Size(tapa.width, 1.1f * u))
            drawRect(t.c(ORO).copy(alpha = 0.85f), Offset(tapa.left + w * 0.02f, tapa.top + tapa.height * 0.4f), Size(tapa.width - w * 0.04f, tapa.height * 0.4f), style = Stroke(0.9f * u))
            // Una nube de oro en el centro de la tapa.
            val nc = Offset(w * 0.5f, tapa.top + tapa.height * 0.6f)
            drawArc(t.c(ORO), 180f, 240f, false, Offset(nc.x - w * 0.05f, nc.y - w * 0.012f), Size(w * 0.03f, w * 0.024f), style = Stroke(1f * u))
            drawArc(t.c(ORO), 120f, 240f, false, Offset(nc.x + w * 0.02f, nc.y - w * 0.012f), Size(w * 0.03f, w * 0.024f), style = Stroke(1f * u))
            drawLine(t.c(ORO), Offset(nc.x - w * 0.035f, nc.y + w * 0.012f), Offset(nc.x + w * 0.035f, nc.y + w * 0.012f), 1f * u)
            destello(Offset(if (der) tapa.right - w * 0.06f else tapa.left + w * 0.06f, tapa.top + 2f * u), 1.2f * u, t)
            filo(tapa, t, der)
        }
        // La cerradura: la placa redonda de latón con su pasador.
        val cerr = Offset(w * 0.5f, tapa.bottom + h * 0.035f)
        drawCircle(t.s(0.3f), w * 0.028f, cerr + Offset(1f * u, 1.4f * u))
        drawCircle(t.c(ORO), w * 0.028f, cerr)
        drawCircle(t.b(0.5f), w * 0.028f, cerr, style = Stroke(0.6f * u))
        drawRect(t.c(LACA_NEGRA), Offset(cerr.x - w * 0.004f, cerr.y - w * 0.01f), Size(w * 0.008f, w * 0.02f))
        etiqueta(w, h, t, PAPEL, filete = laca)
    }

    /**
     * El estuche para documentos, tumbado: de cuero (natural, teñido de rojo o casi negro) o de bambú con sus nudos, con la
     * tapa a la izquierda atada con su cordón que cae hasta la tabla, la boca abierta a la derecha y las tiras que salen.
     */
    private fun DrawScope.estuche(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        // El cuero teñido (rojo, añil, verde o negro) o el bambú, y un cordón de seda de otro color.
        val clase = Math.floorMod(m ushr 11, 5)
        val bambu = clase == 0
        val col = when (clase) { 0 -> mezcla(BAMBU, VERDE, 0.2f); 1 -> mezcla(LACA, CUERO, 0.2f); 2 -> mezcla(INDIGO, CUERO, 0.15f); 3 -> mezcla(VERDE, CUERO, 0.25f); else -> mezcla(HOLLIN, CUERO, 0.3f) }
        val sedaCordon = when (clase) { 1, 3 -> AZAFRAN; 4 -> mezcla(AZAFRAN, CARMESI, 0.3f); else -> CARMESI }
        val colTapa = if (bambu) mezcla(BAMBU, ALAMO_OSC, 0.25f) else mezcla(col, HOLLIN, 0.25f)
        val tubo = Rect(w * 0.08f, h * 0.3f, w * 0.91f, piso - h * 0.004f)
        val tapa = Rect(w * 0.025f, h * 0.28f, w * 0.115f, piso)
        // Un mazo de tiras de bambú de pie detrás, apoyado en el fondo: más alto y más lleno con más fichas.
        tiras(w * 0.28f, w * 0.78f, tubo.top + h * 0.05f, h, c, t, 6, 0.14f)
        drawRoundRect(t.s(0.26f), Offset(tapa.left + (if (t.o) -w * 0.03f else w * 0.035f), tapa.top + h * 0.035f), Size(tubo.right - tapa.left, tubo.height), CornerRadius(tubo.height / 2))
        contacto(Offset(tubo.center.x + w * 0.03f, piso), tubo.width * 1.1f, h * 0.05f, 0.45f)
        // La boca abierta y las tiras que salen por ella.
        val boca = Rect(tubo.right - w * 0.028f, tubo.top, tubo.right + w * 0.028f, tubo.bottom)
        val n = c.n.coerceIn(0, 12)
        val cuantas = (2 + n / 2).coerceIn(2, 7)
        drawRoundRect(t.c(col), tubo.topLeft, tubo.size, CornerRadius(tubo.height * 0.1f))
        clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) {
            tumbado(tubo, t, 1.4f)
            if (bambu) {
                listOf(0.33f, 0.68f).forEach { f ->
                    val x = tubo.left + tubo.width * f
                    drawRect(t.s(0.3f), Offset(x + 1.2f * u, tubo.top), Size(1.2f * u, tubo.height))
                    drawRect(t.b(0.5f), Offset(x, tubo.top), Size(1.2f * u, tubo.height))
                }
                var y = tubo.top + 2f * u
                while (y < tubo.bottom) { drawLine(t.s(0.1f), Offset(tubo.left, y), Offset(tubo.right, y), 0.5f * u); y += 3.4f * u }
            } else {
                for (k in 0 until 22) drawLine(t.c(sedaCordon).copy(alpha = 0.7f), Offset(tubo.left + tubo.width * (0.03f + 0.044f * k), tubo.top + h * 0.035f), Offset(tubo.left + tubo.width * (0.05f + 0.044f * k), tubo.top + h * 0.035f), 0.8f * u)
                drawRect(t.b(0.3f), Offset(tubo.left, tubo.top + h * 0.06f), Size(tubo.width, h * 0.02f))
                drawOval(t.c(mezcla(col, ADOBE, 0.4f)).copy(alpha = 0.45f), Offset(tubo.left + tubo.width * 0.7f, tubo.bottom - h * 0.1f), Size(w * 0.1f, h * 0.06f))
            }
        }
        drawOval(t.c(mezcla(col, LUZ_MUEBLE, 0.1f)), boca.topLeft, boca.size)
        drawOval(t.c(LACA_NEGRA), Offset(boca.left + w * 0.006f, boca.top + h * 0.03f), Size(boca.width - w * 0.012f, boca.height - h * 0.06f))
        for (i in 0 until cuantas) {
            val y = boca.top + boca.height * (0.2f + 0.6f * (i + 0.5f) / cuantas)
            val l = w * (0.03f + 0.05f * azar(c.semilla, i) + 0.004f * n)
            rotate((azar(c.semilla, i + 7) - 0.5f) * 14f, Offset(boca.center.x, y)) {
                val r = Rect(boca.center.x - w * 0.01f, y - h * 0.018f, boca.center.x + l, y + h * 0.018f)
                drawRect(t.s(0.25f), Offset(r.left + 1f * u, r.top + 1.4f * u), r.size)
                drawRect(t.c(BAMBU), r.topLeft, r.size)
                drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1f * u))
                drawLine(t.c(TINTA).copy(alpha = 0.5f), Offset(r.left + r.width * 0.4f, r.center.y), Offset(r.right - 2f * u, r.center.y), 0.7f * u)
            }
        }
        // La tapa, con su cordón que da dos vueltas y cae.
        drawRoundRect(t.c(colTapa), tapa.topLeft, tapa.size, CornerRadius(tapa.width * 0.35f))
        clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) { tumbado(tapa, t, 1.4f) }
        drawRect(t.s(0.4f), Offset(tapa.right - 1.4f * u, tapa.top + 1f * u), Size(1.4f * u, tapa.height - 2f * u))
        drawRect(t.b(0.5f), Offset(tapa.left + tapa.width * 0.2f, tapa.top), Size(tapa.width * 0.6f, 1f * u))
        val cord = t.c(sedaCordon)
        listOf(0.4f, 0.6f).forEach { f -> drawLine(cord, Offset(tapa.left + tapa.width * f, tapa.top), Offset(tapa.left + tapa.width * (f + 0.08f), tapa.bottom), 1.6f * u) }
        val caeC = Path().apply { moveTo(tapa.left + tapa.width * 0.6f, tapa.bottom - h * 0.02f); cubicTo(w * 0.12f, h * 0.98f, w * 0.2f, h * 0.9f, w * 0.3f, h * 0.95f) }
        translate(1f * u, 1.4f * u) { drawPath(caeC, t.s(0.3f), style = Stroke(1.6f * u)) }
        drawPath(caeC, cord, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        rotate(-12f, Offset(w * 0.31f, h * 0.95f)) {
            drawRoundRect(t.c(PAPEL), Offset(w * 0.3f, h * 0.935f), Size(w * 0.05f, h * 0.03f), CornerRadius(h * 0.015f))
            drawRect(t.s(0.25f), Offset(w * 0.3f, h * 0.955f), Size(w * 0.05f, h * 0.01f))
        }
        filo(tubo, t, der)
        etiqueta(w, h, t, mezcla(PAPEL, YUTE, 0.12f), filete = TINTA)
        // La curvatura del tubo, apenas, sobre el papel.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.12f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
    }

    /**
     * El fardo de seda envuelto en tela basta: la tela con su trama y sus arrugas, la seda que asoma por la boca del envoltorio,
     * la cuerda con su nudo, las tiras metidas bajo la cuerda y la tablilla de madera colgada.
     */
    private fun DrawScope.fardoSeda(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val tela = when (Math.floorMod(m ushr 3, 3)) { 0 -> YUTE; 1 -> mezcla(YUTE, ADOBE_OSC, 0.3f); else -> mezcla(YUTE, PAPEL, 0.25f) }
        val seda = listOf(CARMESI, AZAFRAN, VERDE, INDIGO)[Math.floorMod(m ushr 5, 4)]
        val r = Rect(w * 0.05f, h * 0.2f, w * 0.95f, piso)
        val forma = Path().apply {
            moveTo(r.left + w * 0.03f, r.bottom)
            cubicTo(r.left - w * 0.01f, r.bottom - h * 0.2f, r.left - w * 0.01f, r.top + h * 0.1f, r.left + w * 0.06f, r.top + h * 0.03f)
            cubicTo(r.left + w * 0.25f, r.top - h * 0.02f, r.left + w * 0.4f, r.top + h * 0.05f, r.center.x, r.top + h * 0.02f)
            cubicTo(r.right - w * 0.3f, r.top - h * 0.01f, r.right - w * 0.1f, r.top - h * 0.01f, r.right - w * 0.04f, r.top + h * 0.05f)
            cubicTo(r.right + w * 0.015f, r.top + h * 0.15f, r.right + w * 0.01f, r.bottom - h * 0.15f, r.right - w * 0.03f, r.bottom)
            close()
        }
        translate(if (t.o) -w * 0.035f else w * 0.035f, h * 0.035f) { drawPath(forma, t.s(0.26f)) }
        contacto(Offset(r.center.x + w * 0.04f, piso), r.width * 1.05f, h * 0.05f, 0.42f)
        // Las tiras metidas bajo la cuerda, detrás.
        tiras(w * 0.66f, w * 0.9f, r.top + h * 0.06f, h, c, t, 5, 0.14f)
        drawPath(forma, t.c(tela))
        clipPath(forma) {
            var x = r.left
            while (x < r.right) { drawLine(t.s(0.06f), Offset(x, r.top), Offset(x, r.bottom), 0.5f * u); x += 3f * u }
            var y = r.top
            while (y < r.bottom) { drawLine(t.b(0.06f), Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += 3f * u }
            if (der) drawRect(t.b(0.15f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
            else { drawRect(t.b(0.16f), r.topLeft, Size(r.width * 0.22f, r.height)); drawRect(t.s(0.2f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height)) }
            drawRect(t.s(0.18f), Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f))
            pinceladas(r, if (t.o) NOCHE else HOLLIN, c.semilla, 6, u, 0.12f)
            // La boca del envoltorio con la seda que asoma.
            // (la seda: una franja bajo el borde deshilachado de la tela, con su brillo)
            val sy = r.top + h * 0.05f
            drawPath(poligono(w * 0.14f, r.top - h * 0.02f, w * 0.66f, r.top - h * 0.02f, w * 0.64f, sy + h * 0.07f, w * 0.4f, sy + h * 0.1f, w * 0.16f, sy + h * 0.06f), t.c(seda))
            // Los pliegues de la seda, con su lustre.
            listOf(0.22f, 0.34f, 0.46f, 0.57f).forEachIndexed { i, f ->
                drawLine(t.s(0.3f), Offset(w * f, r.top), Offset(w * (f + 0.015f), sy + h * 0.08f), 1f * u)
                drawLine(t.b(0.6f), Offset(w * (f - 0.03f), r.top + h * 0.01f), Offset(w * (f - 0.02f), sy + h * (0.04f + 0.01f * i)), 1.3f * u)
            }
            drawLine(t.b(0.7f), Offset(w * 0.18f, sy - h * 0.01f), Offset(w * 0.6f, sy), 1.2f * u)
            // El borde de la tela basta que la envuelve, doblado y con hilos sueltos.
            val borde = Path().apply { moveTo(w * 0.12f, sy + h * 0.05f); cubicTo(w * 0.25f, sy + h * 0.12f, w * 0.5f, sy + h * 0.13f, w * 0.68f, sy + h * 0.07f) }
            drawPath(borde, t.c(mezcla(tela, HOLLIN, 0.25f)), style = Stroke(2.4f * u))
            drawPath(borde, t.b(0.4f), style = Stroke(0.8f * u))
            for (q in 0 until 10) { val x = w * (0.16f + 0.05f * q); val y = sy + h * (0.08f + 0.03f * sin(q * 0.9f + 1f).coerceAtLeast(0f)); drawLine(t.c(mezcla(tela, PAPEL, 0.3f)), Offset(x, y), Offset(x + 1f * u, y - h * 0.02f), 0.6f * u) }
            // Las arrugas que tira la cuerda.
            listOf(0.15f, 0.3f, 0.7f, 0.86f).forEach { f -> drawLine(t.s(0.25f), Offset(r.left + r.width * f, r.top + h * 0.13f), Offset(r.left + r.width * (f + 0.04f), r.top + h * 0.22f), 0.8f * u) }
        }
        // La cuerda de lado a lado, y la que baja.
        val cy = r.top + h * 0.2f
        val cuerda = t.c(CUERDA)
        val c1 = Path().apply { moveTo(r.left - w * 0.004f, cy + h * 0.02f); cubicTo(w * 0.3f, cy - h * 0.01f, w * 0.7f, cy + h * 0.01f, r.right + w * 0.004f, cy + h * 0.02f) }
        translate(1f * u, 1.6f * u) { drawPath(c1, t.s(0.3f), style = Stroke(2f * u)) }
        drawPath(c1, cuerda, style = Stroke(2f * u))
        listOf(w * 0.2f, w * 0.8f).forEach { x ->
            drawLine(t.s(0.3f), Offset(x + 1f * u, cy), Offset(x + 2f * u, piso), 2f * u)
            drawLine(cuerda, Offset(x, cy), Offset(x + 1f * u, piso), 2f * u)
        }
        // El nudo y el cordel de la tablilla.
        val nudo = Offset(w * 0.2f, cy + h * 0.005f)
        drawCircle(cuerda, 2.6f * u, nudo)
        drawCircle(t.b(0.4f), 1f * u, nudo - Offset(0.8f * u, 0.8f * u))
        drawLine(cuerda, nudo, Offset(w * rotulo.left + w * 0.05f, h * rotulo.top + 2f * u), 1f * u)
        // El sello de arcilla roja en el cruce de la derecha.
        val se = Offset(w * 0.8f, cy + h * 0.015f)
        val rs = w * 0.04f
        drawCircle(t.s(0.35f), rs, se + Offset(1.4f * u, 1.8f * u))
        drawCircle(t.c(mezcla(LACA, LADRILLO, 0.35f)), rs, se)
        drawCircle(t.s(0.3f), rs * 0.66f, se, style = Stroke(0.9f * u))
        drawPath(estrella(se, rs * 0.4f, 4, 0.35f, 0f), t.s(0.35f))
        drawArc(t.b(0.55f), 190f, 80f, false, Offset(se.x - rs * 0.85f, se.y - rs * 0.85f), Size(rs * 1.7f, rs * 1.7f), style = Stroke(0.9f * u))
        filo(r, t, der)
        etiqueta(w, h, t, ALAMO_LUZ, madera = true)
        // El agujero de la tablilla.
        drawCircle(t.c(HOLLIN), 1.4f * u, Offset(w * rotulo.left + w * 0.05f, h * rotulo.top + 3f * u))
    }

    /** Lo que no tiene tema: un cesto de mimbre con tiras sueltas a todos lados y la tablilla sin escribir. */
    private fun DrawScope.cesto(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val top = h * 0.26f
        val cesto = poligono(w * 0.07f, top, w * 0.93f, top, w * 0.86f, piso, w * 0.14f, piso)
        translate(if (t.o) -w * 0.035f else w * 0.035f, h * 0.035f) { drawPath(cesto, t.s(0.26f)) }
        contacto(Offset(w * 0.54f, piso), w * 0.8f, h * 0.05f, 0.42f)
        // Las tiras sueltas, en todas direcciones.
        val n = c.n.coerceIn(0, 12)
        for (i in 0 until (3 + n / 2).coerceAtMost(8)) {
            val bx = w * (0.2f + 0.6f * (i + 0.5f) / (3 + n / 2).coerceAtMost(8))
            val g = -35f + 70f * azar(c.semilla, i)
            val l = h * (0.17f + 0.06f * azar(c.semilla, i + 3))
            rotate(g, Offset(bx, top + h * 0.05f)) {
                val r = Rect(bx - w * 0.016f, top + h * 0.05f - l, bx + w * 0.016f, top + h * 0.06f)
                drawRect(t.s(0.22f), Offset(r.left + 1.4f * u, r.top + 1.4f * u), r.size)
                drawRect(t.c(mezcla(BAMBU, AZAFRAN, 0.15f * (i % 2))), r.topLeft, r.size)
                drawRect(t.b(0.4f), r.topLeft, Size(r.width * 0.35f, r.height))
                drawLine(t.c(mezcla(BAMBU, ALAMO_OSC, 0.5f)), Offset(r.left, r.top + r.height * 0.4f), Offset(r.right, r.top + r.height * 0.4f), 0.8f * u)
            }
        }
        val mimbre = mezcla(ALAMO_LUZ, YUTE, 0.4f)
        drawPath(cesto, t.c(mimbre))
        clipPath(cesto) {
            // Los palos y las vueltas del tejido.
            for (k in 0..13) {
                val f = k / 13f
                drawLine(t.s(0.3f), Offset(w * (0.07f + 0.86f * f), top), Offset(w * (0.14f + 0.72f * f), piso), 0.9f * u)
            }
            var y = top + h * 0.05f; var fila = 0
            while (y < piso) {
                drawLine(t.s(0.25f), Offset(0f, y), Offset(w, y), 0.7f * u)
                for (k in 0 until 13) {
                    if ((k + fila) % 2 == 0) drawLine(t.b(0.35f), Offset(w * (0.1f + 0.066f * k), y - h * 0.022f), Offset(w * (0.14f + 0.066f * k), y - h * 0.022f), 1.6f * u, StrokeCap.Round)
                }
                y += h * 0.05f; fila++
            }
            cilindro(Rect(w * 0.07f, top, w * 0.93f, piso), t, 1f, der)
        }
        // El borde trenzado.
        val borde = Rect(w * 0.06f, top - h * 0.03f, w * 0.94f, top + h * 0.035f)
        drawRoundRect(t.c(mezcla(mimbre, ALAMO_OSC, 0.2f)), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
        var x = borde.left + 2f * u
        while (x < borde.right - 2f * u) { drawLine(t.s(0.35f), Offset(x, borde.top + 1f * u), Offset(x + w * 0.02f, borde.bottom - 1f * u), 0.8f * u); x += w * 0.025f }
        drawRect(t.b(0.45f), Offset(borde.left + borde.height / 2, borde.top), Size(borde.width - borde.height, 1f * u))
        filo(borde, t, der)
        etiqueta(w, h, t, mezcla(ALAMO_LUZ, PAPEL, 0.3f), madera = true)
    }

    /** Un hueco libre: un cuenco de barro, o unas monedas sueltas con su bolsita de cuero, según la semilla. */
    private fun DrawScope.libre(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(c.semilla ushr 2, 2) == 0) {
            val cx = w * 0.44f; val bw = w * 0.46f; val bh = h * 0.32f
            val boca = piso - bh
            val cuenco = Path().apply {
                moveTo(cx - bw / 2, boca)
                cubicTo(cx - bw / 2, boca + bh * 0.7f, cx - bw * 0.2f, piso, cx - bw * 0.12f, piso)
                lineTo(cx + bw * 0.12f, piso)
                cubicTo(cx + bw * 0.2f, piso, cx + bw / 2, boca + bh * 0.7f, cx + bw / 2, boca)
                close()
            }
            translate(if (t.o) -w * 0.03f else w * 0.035f, h * 0.025f) { drawPath(cuenco, t.s(0.25f)) }
            contacto(Offset(cx + bw * 0.08f, piso), bw * 0.9f, h * 0.05f, 0.42f)
            drawPath(cuenco, t.c(mezcla(LADRILLO, ADOBE, 0.35f)))
            clipPath(cuenco) {
                cilindro(Rect(cx - bw / 2, boca, cx + bw / 2, piso), t, 1.2f, t.o)
                drawRect(t.c(PAPEL).copy(alpha = 0.6f), Offset(cx - bw / 2, boca + bh * 0.2f), Size(bw, bh * 0.08f))
                drawRect(t.c(HOLLIN).copy(alpha = 0.35f), Offset(cx - bw / 2, boca + bh * 0.32f), Size(bw, bh * 0.04f))
            }
            drawOval(t.c(mezcla(LADRILLO, ADOBE, 0.5f)), Offset(cx - bw / 2, boca - h * 0.03f), Size(bw, h * 0.06f))
            drawOval(t.c(mezcla(LADRILLO, HOLLIN, 0.35f)), Offset(cx - bw / 2 + w * 0.012f, boca - h * 0.018f), Size(bw - w * 0.024f, h * 0.04f))
            drawArc(t.b(0.5f), 180f, 100f, false, Offset(cx - bw / 2, boca - h * 0.03f), Size(bw, h * 0.06f), style = Stroke(0.8f * u))
            // Una mella en el borde.
            drawPath(poligono(cx + bw * 0.3f, boca - h * 0.025f, cx + bw * 0.38f, boca - h * 0.02f, cx + bw * 0.34f, boca), t.c(mezcla(ADOBE, PAPEL, 0.3f)))
        } else {
            // La bolsita de cuero, caída, y las monedas que se salieron.
            val bc = Offset(w * 0.3f, piso - h * 0.12f)
            val bolsa = Path().apply {
                moveTo(bc.x - w * 0.15f, piso)
                cubicTo(bc.x - w * 0.19f, piso - h * 0.18f, bc.x - w * 0.03f, piso - h * 0.3f, bc.x + w * 0.08f, piso - h * 0.21f)
                cubicTo(bc.x + w * 0.14f, piso - h * 0.17f, bc.x + w * 0.15f, piso - h * 0.06f, bc.x + w * 0.11f, piso)
                close()
            }
            translate(if (t.o) -w * 0.03f else w * 0.03f, h * 0.02f) { drawPath(bolsa, t.s(0.25f)) }
            drawPath(bolsa, t.c(CUERO))
            clipPath(bolsa) { tumbado(Rect(bc.x - w * 0.19f, piso - h * 0.3f, bc.x + w * 0.15f, piso), t, 1.3f); drawRect(t.b(0.2f), Offset(bc.x - w * 0.15f, piso - h * 0.3f), Size(w * 0.06f, h * 0.3f)) }
            drawLine(t.c(CUERDA), Offset(bc.x + w * 0.06f, piso - h * 0.22f), Offset(bc.x + w * 0.12f, piso - h * 0.12f), 1.6f * u)
            drawLine(t.c(CUERDA), Offset(bc.x + w * 0.12f, piso - h * 0.12f), Offset(bc.x + w * 0.2f, piso - h * 0.15f), 1.4f * u)
            listOf(Offset(0.5f, 0.02f), Offset(0.58f, 0.04f), Offset(0.66f, 0.015f), Offset(0.75f, 0.035f), Offset(0.62f, 0.07f)).forEachIndexed { i, f ->
                moneda(Offset(w * (f.x + 0.02f), piso - h * f.y), w * 0.03f, t, 5500 + i, plana = true)
            }
            // Una de canto, apoyada en la pared.
            moneda(Offset(w * 0.88f, piso - h * 0.08f), w * 0.034f, t, 5510)
        }
    }

    override val rotulo = Rect(0.08f, 0.46f, 0.92f, 0.82f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.almendra
    override val salida = -SALIDA
    override val placa = Rect(0.42f, 0.47f, 0.74f, 0.68f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Laca negra, con nubes de oro pintadas.
        drawRect(t(LACA_NEGRA, oscuro))
        for (k in 0 until 22) {
            val c = Offset(w * azar(81, k), h * azar(82, k))
            val r = (8f + 10f * azar(83, k)) * u
            val oro = ORO.copy(alpha = 0.16f)
            drawArc(oro, 180f, 270f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.2f * u))
            drawArc(oro, 0f, 270f, false, Offset(c.x + r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(1.2f * u))
            drawLine(oro, Offset(c.x - r * 2.2f, c.y + r), Offset(c.x + r * 2.4f, c.y + r), 1.2f * u)
        }
        // Las paredes de laca roja, con su filete de oro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LACA, oscuro), r.topLeft, r.size) }
        drawRect(t(ORO, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(2.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        // Bambú viejo, tostado: la página de la Seda es de tinta clara sobre fondo oscuro.
        drawRect(BAMBU_VIEJO.copy(alpha = 0.78f))
        // Las tablillas, una junto a otra, cada una con su nudo.
        val tira = 13 * u * g
        var x = 0f; var k = 0
        while (x < w) {
            if (k % 2 == 1) drawRect(Color.Black.copy(alpha = 0.08f), Offset(x, 0f), Size(tira, h))
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, h), 1f * u)
            drawLine(BAMBU.copy(alpha = 0.14f), Offset(x + 1.2f * u, 0f), Offset(x + 1.2f * u, h), 0.8f * u)
            val ny = h * (0.2f + 0.6f * azar(semilla, k))
            drawLine(BAMBU.copy(alpha = 0.22f), Offset(x + 1 * u, ny), Offset(x + tira - 1 * u, ny), 1.4f * u)
            x += tira; k++
        }
        // Los dos cordeles que las atan, arriba y abajo, con su vuelta en cada tablilla.
        listOf(5 * u * g, h - 5 * u * g).forEach { y ->
            drawLine(CUERDA, Offset(0f, y), Offset(w, y), 1.5f * u * g)
            var xx = 0f
            while (xx < w) { drawCircle(CUERDA, 1.3f * u * g, Offset(xx, y)); xx += tira }
        }
    }

    override val margenes = MargenesFicha(14.dp, 12.dp, 14.dp, 26.dp, 24.dp, 26.dp)
    override val proporcionFicha = 0.85f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello de cinabrio, cuadrado.
            "hecho" -> { drawRect(LACA, Offset(c.x - s * 0.38f, c.y - s * 0.38f), Size(s * 0.76f, s * 0.76f)); drawRect(p.color, Offset(c.x - s * 0.24f, c.y - s * 0.24f), Size(s * 0.48f, s * 0.48f), style = Stroke(s * 0.06f)) }
            // Un disco bi de jade.
            "convence" -> { drawCircle(Color(0xFF3E8A62), s * 0.32f, c, style = Stroke(s * 0.22f)); drawCircle(Color.White.copy(alpha = 0.35f), s * 0.36f, c, style = Stroke(s * 0.04f)) }
            // Una nube en voluta (ruyi).
            "piensa" -> {
                drawArc(TURQUESA, 180f, 270f, false, Offset(c.x - s * 0.4f, c.y - s * 0.2f), Size(s * 0.4f, s * 0.4f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
                drawArc(TURQUESA, 90f, 270f, false, Offset(c.x, c.y - s * 0.2f), Size(s * 0.4f, s * 0.4f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LACA, 0.14f)
            else -> {}
        }
    }

    /** Una borla de seda roja con su nudo. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawLine(LACA, Offset(c.x, c.y - s * 0.48f), Offset(c.x, c.y - s * 0.2f), s * 0.06f)
        drawCircle(ORO, s * 0.12f, Offset(c.x, c.y - s * 0.14f))
        for (k in -3..3) drawLine(LACA, Offset(c.x + k * s * 0.03f, c.y - s * 0.04f), Offset(c.x + k * s * 0.06f, c.y + s * 0.46f), s * 0.05f)
    }

    override val desorden = 0f
}
