package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import io.github.ruddy2003.vestigium.ui.pantallas.simboloCalificacion
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Helénico
// =====================================================================

/**
 * Grecia: la pared del cuarto donde se guardan las vasijas en una casa ática del siglo V a. C. El muro es de revoque pintado
 * por zonas, como se pintaban las casas: el zócalo oscuro abajo, la franja roja, un filete azul y el paño ocre arriba, con sus
 * pinceladas, grietas, desconchones al pie y el hollín que la lucerna deja encima. Contra él, un anaquel de ciprés: dos postes
 * que pisan el suelo de tierra, tablones gruesos que los atraviesan y salen por fuera con su cuña, clavijas en cada unión y
 * ménsulas bajo los tablones; no tiene puertas ni vidrio (en esa época se guardaba en anaqueles, cofres y cestos, o colgado de
 * clavijas). Encima, lo de la casa: la lira de caparazón de tortuga colgada de su clavija con una cinta (lo primero que se
 * ve), la corona de olivo seca en un clavo, el aríbalo y el estrígil colgados juntos de su correa, y sobre el tablón el
 * kálathos con la lana y el huso, un enócoe de arcilla, una kylix apoyada de canto con un delfín en el tondo y el lampadario
 * de bronce con su lucerna de barniz negro. Al pie, el kibotós (el cofre de tapa a dos aguas) con un manto doblado encima, y
 * una crátera de columnas grande, cortada por el borde. Cada tema es una vasija ática con el tema pintado en su zona de
 * arcilla: un ánfora de cuello de figuras negras, una hidria con sus tres asas o un estamnos de barniz negro con tapa, según
 * la semilla, cada una con su desconchón y su rozadura; los óstraka se ven a su pie, sueltos si son pocos, en una lekane si
 * son muchos, amontonados si son más. Lo que no tiene tema va en una vasija de arcilla lisa, sin pintar; un hueco sin tema es
 * el anillo de madera vacío con el cerco que dejó la vasija, o un cesto con un paño. De noche solo arde la lucerna: su halo
 * en el muro ocre, el filo cálido en lo que la mira, las sombras largas hacia el otro lado y lo demás hundido en un azul
 * violeta. Por dentro, un mosaico de guijarros. Las fichas son óstraka: tiestos de terracota.
 */
object IdeasHeleno : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta (sale del cuadro de Helénico)
    /** El paño ocre del muro, la franja roja y el zócalo oscuro; el filete azul del Egeo. */
    private val OCRE = Color(0xFFD9B57D)
    private val ROJO = Color(0xFF9A3B2C)
    private val ZOCALO = Color(0xFF3F3338)
    private val EGEO = Color(0xFF2E5E8E)
    /** El ciprés del anaquel, el cofre y la lira: su color y su sombra honda. */
    private val MADERA = Color(0xFFA9793F)
    private val MADERA_OSC = Color(0xFF5B3C22)
    /** La arcilla ática: la de la vasija, la cocida oscura y la reservada de los paneles, más clara. */
    private val TERRACOTA = Color(0xFFC2652F)
    private val TERRA_OSC = Color(0xFF8A4220)
    private val ARCILLA = Color(0xFFE8A46C)
    /** El barniz negro. */
    private val BARNIZ = Color(0xFF1A1410)
    /** El bronce del lampadario y del estrígil. */
    private val BRONCE = Color(0xFFB07A3A)
    /** El caparazón de la tortuga de la lira. */
    private val CAREY = Color(0xFF6E4A28)
    /** Las hojas secas de la corona de olivo. */
    private val OLIVO = Color(0xFF8E8F63)
    /** La lana sin teñir y la púrpura de las orillas. */
    private val LANA = Color(0xFFEDE3CB)
    private val PURPURA = Color(0xFF6A2F7E)
    /** El suelo de tierra apisonada. */
    private val TIERRA = Color(0xFFBDA27A)
    /** La noche del cuarto: el azul violeta donde se hunde lo que no alumbra la lucerna. */
    private val NOCHE = Color(0xFF17142A)

    // Lo de las fichas y el interior, como antes.
    private val TERRA_LUZ = Color(0xFFDE8A52)
    private val GUIJARRO = Color(0xFFE9E1CF)
    private val ORO = Color(0xFFC9A54A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.32f) else c

    /** Un color a [cerca] (0 a 1) de la lucerna: de día, tal cual; de noche, hundido en el azul violeta y calentado cerca de ella. */
    private fun luz(c: Color, oscuro: Boolean, cerca: Float): Color {
        if (!oscuro) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.86f - 0.6f * Math.pow(k.toDouble(), 1.15).toFloat()), FILO_MUEBLE, 0.2f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, el azul violeta del cuarto. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** La luz crema de lo que mira arriba a la izquierda; de noche casi nada (la lucerna la pone aparte, con su filo). */
    private fun claro(oscuro: Boolean, a: Float) = luzMueble(if (oscuro) a * 0.15f else a)

    /** La zona de arcilla donde va el rótulo: de noche se apaga menos, para que el nombre se siga leyendo. */
    private fun panel(oscuro: Boolean, cerca: Float) =
        if (oscuro) mezcla(mezcla(ARCILLA, NOCHE, 0.08f + 0.15f * (1f - cerca)), FILO_MUEBLE, 0.2f * cerca) else ARCILLA

    /** Cuánto alcanza la lucerna a una vasija dibujada fuera del mueble (sin fila ni columna): una luz media. */
    private const val CERCA_VASIJA = 0.42f
    /** El lampadario: dónde está y cuánto mide, en fracciones del ancho del mueble. */
    private const val LAMPARA_X = 0.8f
    private const val ALTO_LAMP = 0.225f

    /** La llama, en fracciones del ancho del mueble (las mismas cuentas que en [mueble]). */
    private val llamaRel get() = Offset(LAMPARA_X - ALTO_LAMP * 1.3f * 0.25f, corona - 0.012f * 0.4f - ALTO_LAMP - ALTO_LAMP * 1.3f * 0.12f)

    /**
     * Cuánto alumbra la lucerna la vasija del hueco de [c] (1 la de junto a ella, 0 la que se pierde) y hacia dónde se aleja
     * de la llama (para su sombra). Null si el compartimento se dibuja fuera del mueble.
     */
    private fun luzDelHueco(c: EstadoCajon): Pair<Float, Offset>? {
        if (c.cols != cols) return null
        val aw = (1f - 2 * lado - (cols - 1) * hueco) / cols
        val ah = aw / proporcion
        val centro = Offset(lado + c.col * (aw + hueco) + aw * 0.5f, corona + hueco + c.fila * (ah + hueco) + ah * 0.55f)
        val v = centro - llamaRel
        return ((1.35f - v.getDistance()) / 0.95f).coerceIn(0f, 1f) to v
    }
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    private const val SUELO = 0.8f

    override val cols = 3
    override val proporcion = 0.64f
    override val corona = 0.56f
    override val base = 0.3f
    override val lado = 0.075f
    override val hueco = 0.05f

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    /** Une [q] con curvas suaves (Catmull-Rom pasado a Bézier). */
    private fun suave(p: Path, q: List<Offset>) {
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            p.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }

    /**
     * El contorno de algo torneado (una vasija, un cesto): [p] son pares (medio ancho, altura) de arriba abajo, en unidades de
     * [sx] y [sy] desde [cx] y [y0]. Se cierra por el otro lado, en espejo.
     */
    private fun perfil(cx: Float, y0: Float, sx: Float, sy: Float, p: FloatArray): Path {
        val der = List(p.size / 2) { Offset(cx + p[2 * it] * sx, y0 + p[2 * it + 1] * sy) }
        val izq = der.reversed().map { Offset(2 * cx - it.x, it.y) }
        return Path().apply { moveTo(der[0].x, der[0].y); suave(this, der); lineTo(izq[0].x, izq[0].y); suave(this, izq); close() }
    }

    /**
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra a la derecha (en dos tonos), el claro a la
     * izquierda y, si [brillo] es más de 0, el brillo del barniz. De noche, el filo cálido del lado de la lucerna ([haciaX]: 1 si
     * está a la derecha, -1 a la izquierda), más fuerte cuanto más [filo].
     */
    private fun DrawScope.redondo(forma: Path, oscuro: Boolean, filo: Float, haciaX: Float = 1f, brillo: Float = 0f, fuerza: Float = 1f, rotuloEn: Rect? = null) {
        val r = forma.getBounds(); val a = r.width; val hh = r.height
        // El lado de la luz: de día, la izquierda; de noche, el lado donde está la lucerna.
        val sl = if (oscuro) haciaX else -1f
        val s1 = borde(forma, sl * a * 0.26f, -hh * 0.02f)
        val s2 = borde(forma, sl * a * 0.11f, -hh * 0.01f)
        val a1 = fuerza * (if (oscuro) 0.26f else 0.2f)
        val a2 = fuerza * (if (oscuro) 0.24f else 0.2f)
        if (rotuloEn == null || !oscuro) {
            drawPath(s1, sombra(oscuro, a1)); drawPath(s2, sombra(oscuro, a2))
        } else {
            // De noche, sobre la franja del rótulo la sombra propia va mucho más suave: el nombre tiene que leerse.
            clipRect(rotuloEn.left, rotuloEn.top, rotuloEn.right, rotuloEn.bottom, androidx.compose.ui.graphics.ClipOp.Difference) {
                drawPath(s1, sombra(oscuro, a1)); drawPath(s2, sombra(oscuro, a2))
            }
            clipRect(rotuloEn.left, rotuloEn.top, rotuloEn.right, rotuloEn.bottom) {
                drawPath(s1, sombra(oscuro, minOf(a1, 0.26f) * 0.35f)); drawPath(s2, sombra(oscuro, minOf(a2, 0.24f) * 0.35f))
            }
        }
        clipPath(forma) {
            drawOval(if (oscuro) FILO_MUEBLE.copy(alpha = 0.28f * filo) else luzMueble(0.2f), Offset(if (sl < 0) r.left + a * 0.1f else r.right - a * 0.36f, r.top + hh * 0.06f), Size(a * 0.26f, hh * 0.72f))
            if (brillo > 0f) drawRoundRect(if (oscuro) FILO_MUEBLE.copy(alpha = (brillo * 1.6f * filo).coerceAtMost(0.7f)) else luzMueble(brillo),
                Offset(if (sl < 0) r.left + a * 0.21f else r.right - a * 0.255f, r.top + hh * 0.14f), Size(a * 0.045f, hh * 0.44f), CornerRadius(a * 0.03f))
        }
        if (oscuro && filo > 0.02f) drawPath(borde(forma, -sl * a * 0.04f, hh * 0.03f), FILO_MUEBLE.copy(alpha = (0.06f + 0.6f * filo).coerceAtMost(0.65f)))
    }

    /**
     * La sombra de contacto de lo que va en un compartimento. Se pinta con `SrcAtop`: cae sobre la pared y el tablón ya
     * pintados, pero no cuenta en la capa vacía donde `Modifier.halo()` calca la silueta para el aura (si contara, el aura
     * rodearía también la sombra y de noche quedaría un recorte pálido alrededor de la vasija).
     */
    /**
     * Tapa el aura en reposo de `Modifier.halo()` alrededor de una silueta (las formas [rellenos] y las líneas [trazos], con
     * su grueso). Por qué hace falta: `halo()` calca en una capa vacía todo lo que pinta el compartimento, lo tiñe de un color
     * claro y lo corre unos 2.5 dp en ocho direcciones; alrededor de una vasija suelta sobre el muro de noche eso es una franja
     * clara pegada a su borde, que se lee como un recorte. El aro es esa franja, oscura: dos anchos (más hondo junto al borde,
     * donde el aura se suma dos veces), pintados en una capa propia (para que las piezas no se sumen entre sí) que se compone
     * con `SrcAtop`. En la capa del aura no cambia nada (SrcAtop no agrega alfa); en el dibujo normal oscurece la franja. Al
     * tocar la vasija el aura crece más allá del aro y se sigue viendo.
     */
    private fun DrawScope.cubreAura(oscuro: Boolean, rellenos: List<Path>, trazos: List<Pair<Path, Float>> = emptyList()) {
        // De día el aura es del violeta del tema, más oscura que el ocre: se lee como un borde de sombra y no hace falta taparla.
        if (!oscuro) return
        val ancho = 2.9f * density; val junto = 1.7f * density
        val fuera = NOCHE.copy(alpha = 0.3f)
        val dentro = NOCHE.copy(alpha = 0.55f)
        drawIntoCanvas { it.saveLayer(Rect(-ancho * 4, -ancho * 4, size.width + ancho * 4, size.height + ancho * 4), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        for ((color, a) in listOf(fuera to ancho, dentro to junto)) {
            rellenos.forEach { drawPath(it, color, style = Stroke(a * 2, join = androidx.compose.ui.graphics.StrokeJoin.Round), blendMode = BlendMode.Src) }
            trazos.forEach { (t, g) -> drawPath(t, color, style = Stroke(g + a * 2, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round), blendMode = BlendMode.Src) }
        }
        drawIntoCanvas { it.restore() }
    }

    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float, oscuro: Boolean) =
        drawOval(sombra(oscuro, a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto), blendMode = BlendMode.SrcAtop)

    /** Una tabla o un poste de ciprés dentro de [r]: el color, la veta y un nudo. */
    private fun DrawScope.madera(r: Rect, color: Color, semilla: Int, oscuro: Boolean, vertical: Boolean) {
        val u = density
        drawRect(color, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, MADERA_OSC.copy(alpha = if (oscuro) 0.5f else 0.42f), semilla, densidad = 0.55f, vertical = vertical, grosor = 0.8f, ondulacion = 0.8f)
            val n = Offset(r.left + r.width * (0.15f + 0.7f * azar(semilla, 7)), r.top + r.height * (0.25f + 0.5f * azar(semilla, 8)))
            val nr = minOf(r.width, r.height) * 0.2f
            drawOval(MADERA_OSC.copy(alpha = 0.55f), Offset(n.x - nr, n.y - nr * 0.6f), Size(2 * nr, 1.2f * nr))
            drawOval(MADERA_OSC.copy(alpha = 0.3f), Offset(n.x - nr * 1.7f, n.y - nr), Size(3.4f * nr, 2f * nr), style = Stroke(0.6f * u))
        }
    }

    /** Una clavija de madera clavada de frente, con su sombra y la luz en la cabeza. */
    private fun DrawScope.clavija(p: Offset, r: Float, color: Color, oscuro: Boolean, filo: Float = 0f) {
        drawCircle(sombra(oscuro, 0.4f), r * 1.1f, p + Offset(r * 0.35f, r * 0.45f))
        drawCircle(color, r, p)
        drawCircle(claro(oscuro, 0.45f), r * 0.42f, p - Offset(r * 0.32f, r * 0.32f))
        if (oscuro && filo > 0.05f) drawArc(FILO_MUEBLE.copy(alpha = 0.6f * filo), -80f, 110f, false, Offset(p.x - r, p.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.35f))
    }

    /** Una greca (meandro) que corre a lo largo de [r]. */
    private fun DrawScope.greca(r: Rect, color: Color) {
        val a = r.height; var x = r.left
        val p = Path().apply { moveTo(x, r.bottom) }
        while (x < r.right) {
            p.lineTo(x, r.top); p.lineTo(x + a * 0.8f, r.top); p.lineTo(x + a * 0.8f, r.top + a * 0.6f); p.lineTo(x + a * 0.4f, r.top + a * 0.6f)
            p.lineTo(x + a * 0.4f, r.top + a * 0.3f); p.moveTo(x + a * 0.4f, r.bottom); p.lineTo(x + a * 1.2f, r.bottom); x += a * 1.2f
        }
        drawPath(p, color, style = Stroke(maxOf(0.9f * density, a * 0.12f)))
    }

    /** Un tiesto con volumen: arcilla, a veces con su cara de barniz, el canto de arriba a la luz y su sombra. */
    private fun DrawScope.tiesto(c: Offset, r: Float, semilla: Int, oscuro: Boolean, cerca: Float, aura: Boolean = false) {
        val u = density
        val p = Path()
        val lados = 4 + Math.floorMod(semilla, 3)
        for (k in 0 until lados) {
            val a = 6.283f * k / lados + azar(semilla, k) * 0.8f
            val rr = r * (0.7f + 0.4f * azar(semilla, k + 9))
            val o = Offset(c.x + rr * cos(a), c.y + rr * 0.62f * sin(a))
            if (k == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
        }
        p.close()
        if (aura) cubreAura(oscuro, listOf(p))
        drawPath(corrido(p, r * 0.14f, r * 0.2f), sombra(oscuro, 0.32f), blendMode = BlendMode.SrcAtop)
        drawPath(p, luz(mezcla(TERRACOTA, ARCILLA, azar(semilla, 20)), oscuro, cerca))
        if (azar(semilla, 21) < 0.55f) clipPath(p) {
            drawRect(luz(BARNIZ, oscuro, cerca), Offset(c.x - r * 1.3f, c.y - r * 1.3f), Size(r * 2.6f, r * (0.7f + 0.7f * azar(semilla, 22))))
        }
        drawPath(borde(p, r * 0.16f, r * 0.16f), claro(oscuro, 0.4f))
        drawPath(p, sombra(oscuro, 0.35f), style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- el mueble: la pared y el anaquel

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.bottom
        val sep = d.separacion
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val grueso = sep * 0.6f
        val fondo = w * 0.012f
        // Los tablones: el de arriba (donde está lo de la casa) y uno bajo cada fila de vasijas.
        val tablas = listOf(ch) + d.filas.map { it.bottom }
        // El lampadario, en el tablón de arriba, a la derecha; la llama sale del pico de la lucerna.
        val lampX = w * LAMPARA_X
        val apoyo = ch - fondo * 0.4f
        val altoLamp = w * ALTO_LAMP
        val disco = apoyo - altoLamp
        val lu = altoLamp * 1.3f
        val punta = Offset(lampX - lu * 0.25f, disco - lu * 0.05f)
        val llama = punta - Offset(0f, lu * 0.07f)
        fun cerca(p: Offset) = (1f - (p - llama).getDistance() / (w * 1.1f)).coerceIn(0f, 1f)
        fun c(col: Color, p: Offset) = luz(col, oscuro, cerca(p))
        fun hacia(x: Float) = if (llama.x >= x) 1f else -1f
        /** Hacia dónde cae la sombra de algo en [p] sobre la pared: de día, a la derecha y abajo; de noche, lejos de la llama. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro) return Offset(largo * 0.8f, largo * 0.6f)
            val v = p - llama; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 4f)
        }

        // ---- la pared pintada por zonas
        val franja = suelo - w * 0.215f
        val zoc = suelo - w * 0.13f
        val lejos = 0.06f
        drawRect(luz(OCRE, oscuro, lejos + 0.04f), Offset.Zero, Size(w, franja))
        drawRect(luz(ROJO, oscuro, lejos), Offset(0f, franja), Size(w, zoc - franja))
        drawRect(luz(ZOCALO, oscuro, lejos), Offset(0f, zoc), Size(w, suelo - zoc))
        val muro = Rect(0f, 0f, w, franja)
        pinceladas(muro, luz(mezcla(OCRE, SOMBRA_MUEBLE, 0.35f), oscuro, lejos), 801, 110, u, 0.09f)
        pinceladas(muro, luz(mezcla(OCRE, LUZ_MUEBLE, 0.5f), oscuro, lejos), 802, 70, u, 0.11f)
        pinceladas(muro, luz(mezcla(OCRE, ROJO, 0.3f), oscuro, lejos), 807, 30, u, 0.06f)
        pinceladas(Rect(0f, franja, w, zoc), luz(mezcla(ROJO, LUZ_MUEBLE, 0.3f), oscuro, lejos), 803, 22, u, 0.1f)
        pinceladas(Rect(0f, zoc, w, suelo), luz(mezcla(ZOCALO, LUZ_MUEBLE, 0.3f), oscuro, lejos), 804, 18, u, 0.12f)
        // Las orillas de cada zona, pintadas a mano: no son rectas del todo.
        for (k in 0 until 26) {
            val x = w * k / 25f
            drawOval(luz(ROJO, oscuro, lejos), Offset(x - w * 0.03f, franja - (0.6f + 1.4f * azar(805, k)) * u), Size(w * 0.06f, 3f * u))
            drawOval(luz(ZOCALO, oscuro, lejos), Offset(x - w * 0.03f, zoc - (0.4f + 1.2f * azar(806, k)) * u), Size(w * 0.06f, 2.6f * u))
        }
        // El filete azul entre el rojo y el ocre, y otro fino y oscuro sobre el zócalo.
        drawRect(luz(EGEO, oscuro, lejos), Offset(0f, franja - w * 0.009f), Size(w, w * 0.008f))
        drawRect(luz(mezcla(ROJO, ZOCALO, 0.6f), oscuro, lejos), Offset(0f, zoc - w * 0.004f), Size(w, w * 0.003f))
        // La mancha de luz de día, arriba a la izquierda.
        if (!oscuro) mancha(Offset(w * 0.06f, ch * 0.02f), w * 1.05f, luzMueble(0.42f))
        // Los arreglos del revoque: parches un poco más claros, con el borde de la llana.
        listOf(Offset(w * 0.08f, ch * 0.55f), Offset(w * 0.68f, ch * 0.12f), Offset(w * 0.5f, b.top - w * 0.4f)).forEachIndexed { i, o ->
            val pr = Path()
            for (k in 0 until 9) {
                val a = 6.283f * k / 9
                val rr = w * (0.045f + 0.03f * azar(810 + i, k))
                val q = Offset(o.x + rr * cos(a) * 1.4f, o.y + rr * sin(a) * 0.8f)
                if (k == 0) pr.moveTo(q.x, q.y) else pr.lineTo(q.x, q.y)
            }
            pr.close()
            drawPath(pr, luz(mezcla(OCRE, LANA, 0.3f), oscuro, lejos).copy(alpha = 0.35f))
        }
        // Grietas finas en el revoque.
        fun grieta(desde: Offset, largo: Float, semilla: Int) {
            val g = Path().apply { moveTo(desde.x, desde.y) }
            var p = desde
            for (k in 0 until 7) {
                p += Offset((azar(semilla, k) - 0.45f) * largo * 0.35f, largo / 7f)
                g.lineTo(p.x, p.y)
            }
            drawPath(g, sombra(oscuro, 0.32f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            drawPath(corrido(g, -0.8f * u, 0f), claro(oscuro, 0.25f), style = Stroke(0.5f * u))
        }
        grieta(Offset(w * 0.06f, w * 0.02f), w * 0.12f, 821)
        grieta(Offset(w * 0.44f, ch - w * 0.2f), w * 0.09f, 822)
        // El zócalo gastado: desconchones donde se ve el revoque claro, y la humedad que sube del suelo.
        for (k in 0 until 4) {
            val o = Offset(w * (0.04f + 0.92f * azar(830, k)), zoc + (suelo - zoc) * (0.25f + 0.65f * azar(831, k)))
            val rr = w * (0.005f + 0.01f * azar(832, k))
            val pr = Path()
            for (j in 0 until 6) {
                val a = 6.283f * j / 6 + azar(833 + k, j)
                val q = Offset(o.x + rr * cos(a) * 1.6f, o.y + rr * sin(a) * 0.8f)
                if (j == 0) pr.moveTo(q.x, q.y) else pr.lineTo(q.x, q.y)
            }
            pr.close()
            drawPath(pr, luz(mezcla(ZOCALO, OCRE, 0.45f), oscuro, lejos))
            drawPath(borde(pr, rr * 0.3f, rr * 0.3f), sombra(oscuro, 0.5f))
        }
        pinceladas(Rect(0f, franja - w * 0.02f, w, zoc + w * 0.02f), luz(mezcla(ROJO, ZOCALO, 0.6f), oscuro, lejos), 834, 16, u, 0.14f)
        pinceladas(Rect(0f, suelo - w * 0.05f, w, suelo), sombra(oscuro, 1f), 835, 18, u, 0.12f)
        // El hollín que la lucerna deja en la pared, encima de la llama.
        for (k in 0 until 7) {
            val r = w * (0.004f + 0.0035f * k)
            val y = llama.y - w * (0.008f + 0.014f * k)
            drawOval(sombra(oscuro, 0.035f), Offset(llama.x - r + k * 0.5f * u, y - r * 1.6f), Size(r * 2f, r * 3.2f))
        }
        // El halo de la lucerna, de noche: unas pocas manchas, no un velo.
        if (oscuro) {
            mancha(llama, w * 1.3f, FILO_MUEBLE.copy(alpha = 0.16f))
            mancha(llama, w * 0.62f, FILO_MUEBLE.copy(alpha = 0.26f))
            mancha(llama, w * 0.24f, Color(0xFFFFD58A).copy(alpha = 0.36f))
        }

        // ---- el suelo de tierra apisonada
        drawRect(luz(TIERRA, oscuro, 0.02f), Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), luz(mezcla(TIERRA, SOMBRA_MUEBLE, 0.4f), oscuro, 0.02f), 841, 22, u, 0.12f)
        pinceladas(Rect(0f, suelo, w, alto), luz(mezcla(TIERRA, LUZ_MUEBLE, 0.5f), oscuro, 0.02f), 842, 14, u, 0.12f)
        for (k in 0 until 16) {
            val p = Offset(w * azar(843, k), suelo + (alto - suelo) * (0.25f + 0.7f * azar(844, k)))
            val r = (0.8f + 1.6f * azar(845, k)) * u
            drawOval(sombra(oscuro, 0.3f), Offset(p.x - r + 0.6f * u, p.y - r * 0.4f), Size(2 * r, 1.1f * r))
            drawOval(luz(mezcla(TIERRA, LANA, 0.35f), oscuro, 0.02f), Offset(p.x - r, p.y - r * 0.6f), Size(2 * r, 1.1f * r))
        }
        // El paso del muro al suelo: la junta en sombra.
        drawRect(sombra(oscuro, 0.4f), Offset(0f, suelo), Size(w, 1.5f * u))
        drawRect(sombra(oscuro, 0.14f), Offset(0f, suelo + 1.5f * u), Size(w, 4f * u))

        // ---- las sombras del anaquel en la pared
        val postes = listOf(w * 0.016f to w * 0.064f, w * 0.936f to w * 0.984f)
        val topePoste = ch - w * 0.06f
        postes.forEach { (xa, xb) ->
            val s = caida(Offset((xa + xb) / 2, ch), w * 0.014f)
            drawRect(sombra(oscuro, if (oscuro) 0.4f else 0.2f), Offset(xa + s.x, topePoste + s.y), Size(xb - xa, suelo - topePoste - s.y))
        }
        tablas.forEach { y ->
            val dx = if (oscuro) 0f else w * 0.01f
            val largo = if (oscuro) sep * 1.6f else sep * 0.5f
            drawRect(sombra(oscuro, if (oscuro) 0.45f else 0.26f), Offset(w * 0.004f + dx, y + grueso), Size(w * 0.992f, largo))
            drawRect(sombra(oscuro, if (oscuro) 0.25f else 0.1f), Offset(w * 0.004f + dx, y + grueso + largo), Size(w * 0.992f, largo * 1.4f))
        }

        // ---- las ménsulas, bajo cada tablón, en los huecos entre las vasijas
        val huecosX = (0 until cols - 1).map { i -> (d.huecos[i].right + d.huecos[i + 1].left) / 2 }
        tablas.forEachIndexed { j, y ->
            huecosX.forEachIndexed { i, xg -> mensula(Offset(xg, y + grueso), sep * 0.56f, sep * 1.2f, 850 + 7 * j + i, oscuro, cerca(Offset(xg, y)), hacia(xg)) }
        }
        // ---- los tablones
        tablas.forEachIndexed { j, y -> tablon(w * 0.004f, w * 0.996f, y, grueso, fondo, 870 + j, oscuro, cerca(Offset(w * 0.6f, y))) }
        // De noche, el tablón de arriba se enciende junto al lampadario.
        if (oscuro) clipRect(0f, ch - fondo, w, ch + grueso) { mancha(Offset(lampX, ch - fondo), w * 0.42f, FILO_MUEBLE.copy(alpha = 0.5f)) }
        // ---- los postes, que pisan el suelo, con la clavija de cada tablón y la cuña por fuera
        postes.forEachIndexed { i, (xa, xb) ->
            val r = Rect(xa, topePoste, xb, suelo + w * 0.005f)
            sombraContacto(Offset(r.center.x + 2 * u, r.bottom), r.width * 1.6f, 4 * u, if (oscuro) 0.5f else 0.35f)
            poste(r, 890 + i, oscuro, cerca(Offset(r.center.x, ch)), hacia(r.center.x))
            // De noche, el poste se va perdiendo hacia abajo, lejos de la llama.
            if (oscuro) drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0f), NOCHE.copy(alpha = 0.78f)), ch, suelo), r.topLeft, r.size)
            tablas.forEach { y ->
                clavija(Offset(r.center.x, y + grueso * 0.5f), grueso * 0.2f, c(mezcla(MADERA, LUZ_MUEBLE, 0.2f), Offset(r.center.x, y)), oscuro, cerca(Offset(r.center.x, y)))
                // La punta del tablón que sale por fuera del poste, con su cuña.
                val xc = if (i == 0) w * 0.0085f else w * 0.9915f
                drawRect(c(MADERA_OSC, Offset(xc, y)), Offset(xc - w * 0.0025f, y - grueso * 0.25f), Size(w * 0.005f, grueso * 1.5f))
                drawRect(claro(oscuro, 0.3f), Offset(xc - w * 0.0025f, y - grueso * 0.25f), Size(w * 0.0015f, grueso * 1.5f))
            }
        }

        // ---- lo colgado en la pared
        val crownClavo = Offset(w * 0.585f, w * 0.06f)
        coronaOlivo(crownClavo, w * 0.055f, oscuro, cerca(crownClavo), caida(crownClavo, w * 0.012f))
        val correa = Offset(w * 0.915f, w * 0.1f)
        estrigil(correa, w, oscuro, cerca(correa + Offset(0f, w * 0.1f)), hacia(correa.x), caida(correa, w * 0.012f))
        val clavoLira = Offset(w * 0.31f, w * 0.1f)
        lira(clavoLira, w * 0.19f, oscuro, cerca(clavoLira + Offset(0f, w * 0.2f)), caida(clavoLira + Offset(0f, w * 0.2f), w * 0.016f))

        // ---- lo de encima del tablón, de atrás adelante
        kylix(Offset(w * 0.635f, apoyo - w * 0.068f), w * 0.074f, w * 0.066f, oscuro, cerca(Offset(w * 0.635f, apoyo - w * 0.07f)), hacia(w * 0.635f), caida(Offset(w * 0.635f, apoyo), w * 0.01f))
        for (k in 0 until 3) {
            // Unas hojas de olivo que se cayeron de la corona.
            val p = Offset(w * (0.55f + 0.06f * k + 0.02f * azar(861, k)), apoyo - fondo * 0.3f * azar(862, k))
            rotate(-20f + 50f * azar(863, k), p) { drawOval(c(mezcla(OLIVO, MADERA, 0.4f), p), Offset(p.x - w * 0.009f, p.y - w * 0.0025f), Size(w * 0.018f, w * 0.005f)) }
        }
        lampadario(Offset(lampX, apoyo), altoLamp, oscuro, cerca(Offset(lampX, disco + altoLamp * 0.4f)), caida(Offset(lampX, apoyo - altoLamp * 0.5f), w * 0.012f))
        enocoe(Offset(w * 0.475f, apoyo), w * 0.165f, oscuro, cerca(Offset(w * 0.475f, apoyo - w * 0.08f)), caida(Offset(w * 0.475f, apoyo), w * 0.012f))
        kalathos(Offset(w * 0.135f, apoyo), w * 0.14f, w * 0.115f, oscuro, cerca(Offset(w * 0.135f, apoyo - w * 0.06f)), caida(Offset(w * 0.135f, apoyo), w * 0.012f))

        // ---- al pie: el cofre con el manto y la crátera
        val cofreR = Rect(w * 0.085f, suelo - w * 0.1f, w * 0.365f, suelo + w * 0.01f)
        cofre(cofreR, w, oscuro, maxOf(cerca(cofreR.center), 0.16f), hacia(cofreR.center.x), caida(cofreR.center, w * 0.014f))
        val pieCratera = Offset(w * 0.905f, suelo + w * 0.035f)
        cratera(pieCratera, w * 0.235f, oscuro, maxOf(cerca(pieCratera - Offset(0f, w * 0.15f)), 0.18f), hacia(pieCratera.x - w * 0.08f), caida(pieCratera - Offset(0f, w * 0.1f), w * 0.014f))
        if (d.mas > 0) placa.let { pl -> pinax(d.enBase(pl), oscuro, caida(d.enBase(pl).center, w * 0.01f)) }
        else {
            // Sin placa: dos óstraka que se cayeron al suelo.
            tiesto(Offset(w * 0.5f, suelo + w * 0.035f), w * 0.016f, 871, oscuro, 0.05f)
            tiesto(Offset(w * 0.55f, suelo + w * 0.045f), w * 0.012f, 872, oscuro, 0.05f)
        }

        // ---- la llama, encima de todo, con su propio color
        if (oscuro) llamita(punta, lu)
        // El grano de papel, al final.
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    /** Un tablón grueso: la cara de arriba, vista apenas desde arriba, y el canto de delante con su veta y su luz. */
    private fun DrawScope.tablon(x0: Float, x1: Float, y: Float, grueso: Float, fondo: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val arriba = Rect(x0, y - fondo, x1, y)
        drawRect(luz(mezcla(MADERA, LUZ_MUEBLE, 0.25f), oscuro, cerca), arriba.topLeft, arriba.size)
        clipRect(arriba.left, arriba.top, arriba.right, arriba.bottom) {
            vetas(arriba, MADERA_OSC.copy(alpha = 0.22f), semilla + 3, densidad = 0.8f, grosor = 0.6f, ondulacion = 0.4f)
            // Manchas de uso: el cerco de una vasija que goteó y un roce más claro.
            drawOval(sombra(oscuro, 0.1f), Offset(x0 + (x1 - x0) * (0.2f + 0.6f * azar(semilla, 1)), y - fondo * 0.8f), Size(fondo * 4f, fondo * 0.6f), style = Stroke(0.8f * u))
        }
        drawLine(sombra(oscuro, 0.4f), Offset(x0, y - fondo), Offset(x1, y - fondo), 0.8f * u)
        val cara = Rect(x0, y, x1, y + grueso)
        madera(cara, luz(MADERA, oscuro, cerca), semilla, oscuro, false)
        drawRect(if (oscuro) FILO_MUEBLE.copy(alpha = 0.15f + 0.5f * cerca) else luzMueble(0.5f), Offset(x0, y), Size(x1 - x0, 1.1f * u))
        drawRect(sombra(oscuro, 0.35f), Offset(x0, y + grueso - 1.4f * u), Size(x1 - x0, 1.4f * u))
        // Las juntas de testa en los extremos.
        drawRect(sombra(oscuro, 0.3f), Offset(x0, y), Size(1.2f * u, grueso))
        drawRect(sombra(oscuro, 0.3f), Offset(x1 - 1.2f * u, y), Size(1.2f * u, grueso))
    }

    /** Un poste cuadrado de ciprés: la cara con su veta, la arista de la luz, el lado de la sombra y la cabeza achaflanada. */
    private fun DrawScope.poste(r: Rect, semilla: Int, oscuro: Boolean, cerca: Float, haciaX: Float) {
        val u = density
        madera(r, luz(MADERA, oscuro, cerca), semilla, oscuro, true)
        drawRect(claro(oscuro, 0.26f), r.topLeft, Size(r.width * 0.2f, r.height))
        drawRect(sombra(oscuro, 0.3f), Offset(r.right - r.width * 0.28f, r.top), Size(r.width * 0.28f, r.height))
        // La cabeza achaflanada: cuatro caras, la de la izquierda a la luz.
        val ap = r.width * 0.45f
        drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, r.top - ap); lineTo(r.center.x, r.top); close() }, luz(mezcla(MADERA, LUZ_MUEBLE, 0.3f), oscuro, cerca))
        drawPath(Path().apply { moveTo(r.right, r.top); lineTo(r.center.x, r.top - ap); lineTo(r.center.x, r.top); close() }, luz(mezcla(MADERA, SOMBRA_MUEBLE, 0.4f), oscuro, cerca))
        if (oscuro && cerca > 0.05f) {
            val x = if (haciaX > 0) r.right - 1.2f * u else r.left
            drawRect(FILO_MUEBLE.copy(alpha = 0.55f * cerca), Offset(x, r.top), Size(1.2f * u, r.height * 0.45f))
        }
    }

    /** Una ménsula de ciprés bajo un tablón: se angosta hacia abajo y acaba en curva; su sombra en la pared y su clavija. */
    private fun DrawScope.mensula(arriba: Offset, ancho: Float, largo: Float, semilla: Int, oscuro: Boolean, cerca: Float, haciaX: Float) {
        val u = density
        val x = arriba.x; val y = arriba.y
        val forma = Path().apply {
            moveTo(x - ancho / 2, y); lineTo(x + ancho / 2, y)
            lineTo(x + ancho / 2, y + largo * 0.5f)
            cubicTo(x + ancho / 2, y + largo * 0.96f, x - ancho * 0.1f, y + largo * 1.02f, x - ancho / 2, y + largo * 0.72f)
            close()
        }
        drawPath(corrido(forma, if (oscuro) 0f else ancho * 0.35f, if (oscuro) largo * 0.25f else ancho * 0.25f), sombra(oscuro, if (oscuro) 0.4f else 0.2f))
        drawPath(forma, luz(MADERA, oscuro, cerca))
        clipPath(forma) {
            vetas(Rect(x - ancho / 2, y, x + ancho / 2, y + largo), MADERA_OSC.copy(alpha = 0.35f), semilla, densidad = 0.7f, vertical = true, grosor = 0.6f)
        }
        drawPath(borde(forma, -ancho * 0.3f, 0f), sombra(oscuro, 0.3f))
        drawPath(borde(forma, ancho * 0.2f, 0f), claro(oscuro, 0.25f))
        // La voluta tallada al pie y la clavija que la sujeta a la pared.
        drawCircle(sombra(oscuro, 0.35f), ancho * 0.2f, Offset(x + ancho * 0.08f, y + largo * 0.66f), style = Stroke(1f * u))
        drawArc(claro(oscuro, 0.3f), 160f, 120f, false, Offset(x + ancho * 0.08f - ancho * 0.2f, y + largo * 0.66f - ancho * 0.2f), Size(ancho * 0.4f, ancho * 0.4f), style = Stroke(0.7f * u))
        clavija(Offset(x, y + largo * 0.24f), ancho * 0.14f, luz(mezcla(MADERA, LUZ_MUEBLE, 0.2f), oscuro, cerca), oscuro, cerca)
        if (oscuro && cerca > 0.1f) drawPath(borde(forma, -haciaX * ancho * 0.15f, 0f), FILO_MUEBLE.copy(alpha = 0.4f * cerca))
    }

    // ---------------------------------------------------------------- lo de la casa

    /**
     * La lira de caparazón de tortuga (chelys), colgada de [clavo] por la cuerda del yugo: los dos brazos curvos, el yugo con sus
     * clavijas de cuero, la concha con sus escudos, las siete cuerdas sobre el puente y una cinta de lana atada a un brazo.
     */
    private fun DrawScope.lira(clavo: Offset, s: Float, oscuro: Boolean, cerca: Float, caida: Offset) {
        val u = density
        val cx = clavo.x
        val yugo = clavo.y + s * 0.2f
        val concha = Rect(cx - s * 0.5f, yugo + s * 0.98f, cx + s * 0.5f, yugo + s * 1.82f)
        fun brazo(lado: Float) = Path().apply {
            moveTo(cx + lado * s * 0.27f, concha.top + s * 0.12f)
            cubicTo(cx + lado * s * 0.62f, concha.top - s * 0.28f, cx + lado * s * 0.76f, yugo + s * 0.34f, cx + lado * s * 0.52f, yugo)
        }
        fun cinta() = Path().apply {
            val a = Offset(cx - s * 0.66f, yugo + s * 0.36f)
            moveTo(a.x, a.y)
            cubicTo(a.x - s * 0.12f, a.y + s * 0.2f, a.x + s * 0.02f, a.y + s * 0.42f, a.x - s * 0.08f, a.y + s * 0.66f)
        }
        // La sombra en la pared: de día, corta a la derecha; de noche, larga hacia el otro lado de la lucerna.
        translate(caida.x, caida.y) {
            val sc = sombra(oscuro, if (oscuro) 0.42f else 0.2f)
            drawOval(sc, concha.topLeft, concha.size)
            for (l in listOf(-1f, 1f)) drawPath(brazo(l), sc, style = Stroke(s * 0.08f, cap = StrokeCap.Round))
            drawLine(sc, Offset(cx - s * 0.64f, yugo), Offset(cx + s * 0.64f, yugo), s * 0.065f, StrokeCap.Round)
            drawPath(cinta(), sc, style = Stroke(s * 0.06f, cap = StrokeCap.Round))
        }
        // La cuerda de colgar y la clavija.
        val cuerda = luz(mezcla(MADERA_OSC, LANA, 0.3f), oscuro, cerca)
        drawLine(cuerda, clavo, Offset(cx - s * 0.36f, yugo), 1f * u)
        drawLine(cuerda, clavo, Offset(cx + s * 0.36f, yugo), 1f * u)
        clavija(clavo, s * 0.035f, luz(MADERA_OSC, oscuro, cerca), oscuro, cerca)
        // Los brazos, de cuerno oscuro, con su luz a la izquierda.
        for (l in listOf(-1f, 1f)) {
            val p = brazo(l)
            drawPath(p, luz(mezcla(MADERA_OSC, CAREY, 0.4f), oscuro, cerca), style = Stroke(s * 0.075f, cap = StrokeCap.Round))
            drawPath(corrido(p, -s * 0.018f, 0f), claro(oscuro, 0.35f), style = Stroke(s * 0.018f, cap = StrokeCap.Round))
            if (oscuro) drawPath(corrido(p, s * 0.022f, 0f), FILO_MUEBLE.copy(alpha = 0.55f * cerca), style = Stroke(s * 0.014f, cap = StrokeCap.Round))
        }
        // La cinta de lana púrpura atada al brazo de la izquierda.
        drawPath(cinta(), luz(PURPURA, oscuro, cerca), style = Stroke(s * 0.055f, cap = StrokeCap.Round))
        drawPath(corrido(cinta(), -s * 0.012f, 0f), claro(oscuro, 0.25f), style = Stroke(s * 0.012f, cap = StrokeCap.Round))
        drawCircle(luz(PURPURA, oscuro, cerca), s * 0.045f, Offset(cx - s * 0.66f, yugo + s * 0.36f))
        // El yugo, con las clavijas de cuero donde se enrolla cada cuerda.
        drawLine(luz(MADERA, oscuro, cerca), Offset(cx - s * 0.64f, yugo), Offset(cx + s * 0.64f, yugo), s * 0.06f, StrokeCap.Round)
        drawLine(claro(oscuro, 0.4f), Offset(cx - s * 0.62f, yugo - s * 0.016f), Offset(cx + s * 0.62f, yugo - s * 0.016f), s * 0.014f, StrokeCap.Round)
        drawLine(sombra(oscuro, 0.3f), Offset(cx - s * 0.62f, yugo + s * 0.02f), Offset(cx + s * 0.62f, yugo + s * 0.02f), s * 0.01f, StrokeCap.Round)
        for (i in 0 until 7) {
            val x = cx + (i - 3) * s * 0.085f
            drawRoundRect(luz(MADERA_OSC, oscuro, cerca), Offset(x - s * 0.022f, yugo - s * 0.042f), Size(s * 0.044f, s * 0.084f), CornerRadius(s * 0.012f))
            drawLine(claro(oscuro, 0.3f), Offset(x - s * 0.012f, yugo - s * 0.034f), Offset(x - s * 0.012f, yugo + s * 0.03f), 0.6f * u)
        }
        // La concha: el caparazón con sus escudos (los de en medio y los de los lados) y el borde de placas chicas.
        val forma = Path().apply { addOval(concha) }
        drawPath(forma, luz(CAREY, oscuro, cerca))
        clipPath(forma) {
            val ambar = luz(mezcla(CAREY, OCRE, 0.55f), oscuro, cerca)
            val juntas = luz(mezcla(CAREY, BARNIZ, 0.55f), oscuro, cerca)
            val hw = concha.width; val hh = concha.height
            // Las manchas de ámbar en el centro de cada escudo.
            val centros = listOf(0f to 0.2f, 0f to 0.48f, 0f to 0.76f, -0.3f to 0.3f, 0.3f to 0.3f, -0.32f to 0.66f, 0.32f to 0.66f)
            centros.forEachIndexed { k, (fx, fy) ->
                val p = Offset(cx + fx * hw, concha.top + fy * hh)
                drawOval(ambar.copy(alpha = 0.75f), Offset(p.x - hw * 0.09f, p.y - hh * 0.08f), Size(hw * (0.16f + 0.04f * azar(901, k)), hh * 0.14f))
            }
            // Las juntas entre escudos: la fila de en medio y las de los lados.
            val red = Path().apply {
                for (fy in listOf(0.06f, 0.34f, 0.62f, 0.9f)) {
                    moveTo(cx - hw * 0.14f, concha.top + fy * hh); lineTo(cx + hw * 0.14f, concha.top + fy * hh)
                }
                moveTo(cx - hw * 0.14f, concha.top + 0.06f * hh); lineTo(cx - hw * 0.17f, concha.top + 0.2f * hh); lineTo(cx - hw * 0.14f, concha.top + 0.34f * hh)
                lineTo(cx - hw * 0.17f, concha.top + 0.48f * hh); lineTo(cx - hw * 0.14f, concha.top + 0.62f * hh); lineTo(cx - hw * 0.17f, concha.top + 0.76f * hh); lineTo(cx - hw * 0.14f, concha.top + 0.9f * hh)
                moveTo(cx + hw * 0.14f, concha.top + 0.06f * hh); lineTo(cx + hw * 0.17f, concha.top + 0.2f * hh); lineTo(cx + hw * 0.14f, concha.top + 0.34f * hh)
                lineTo(cx + hw * 0.17f, concha.top + 0.48f * hh); lineTo(cx + hw * 0.14f, concha.top + 0.62f * hh); lineTo(cx + hw * 0.17f, concha.top + 0.76f * hh); lineTo(cx + hw * 0.14f, concha.top + 0.9f * hh)
                for (l in listOf(-1f, 1f)) {
                    moveTo(cx + l * hw * 0.17f, concha.top + 0.48f * hh); lineTo(cx + l * hw * 0.5f, concha.top + 0.46f * hh)
                    moveTo(cx + l * hw * 0.16f, concha.top + 0.2f * hh); lineTo(cx + l * hw * 0.42f, concha.top + 0.14f * hh)
                    moveTo(cx + l * hw * 0.16f, concha.top + 0.78f * hh); lineTo(cx + l * hw * 0.42f, concha.top + 0.86f * hh)
                }
            }
            drawPath(red, juntas, style = Stroke(0.9f * u))
            // El borde de placas chicas.
            drawOval(juntas, Offset(concha.left + hw * 0.06f, concha.top + hh * 0.06f), Size(hw * 0.88f, hh * 0.88f), style = Stroke(0.8f * u))
            for (k in 0 until 18) {
                val a = 6.283f * k / 18
                val p0 = Offset(concha.center.x + hw * 0.44f * cos(a), concha.center.y + hh * 0.44f * sin(a))
                val p1 = Offset(concha.center.x + hw * 0.5f * cos(a), concha.center.y + hh * 0.5f * sin(a))
                drawLine(juntas, p0, p1, 0.8f * u)
            }
        }
        redondo(forma, oscuro, cerca, 1f, brillo = 0.3f)
        // El puente, el cordal y las siete cuerdas de tripa, del yugo al cordal.
        val puenteY = concha.top + concha.height * 0.4f
        val cordal = concha.top + concha.height * 0.74f
        for (i in 0 until 7) {
            val x0 = cx + (i - 3) * s * 0.085f
            val x1 = cx + (i - 3) * s * 0.03f
            drawLine(sombra(oscuro, 0.3f), Offset(x0 + 0.8f * u, yugo + 0.8f * u), Offset(x1 + 0.8f * u, cordal + 0.8f * u), 0.6f * u)
            drawLine(luz(LANA, oscuro, cerca + 0.15f).copy(alpha = 0.92f), Offset(x0, yugo), Offset(x1, cordal), 0.6f * u)
        }
        drawRoundRect(luz(mezcla(MADERA, LANA, 0.3f), oscuro, cerca), Offset(cx - s * 0.2f, puenteY - s * 0.02f), Size(s * 0.4f, s * 0.04f), CornerRadius(s * 0.01f))
        drawLine(sombra(oscuro, 0.4f), Offset(cx - s * 0.19f, puenteY + s * 0.022f), Offset(cx + s * 0.2f, puenteY + s * 0.022f), 0.8f * u)
        drawRoundRect(luz(MADERA_OSC, oscuro, cerca), Offset(cx - s * 0.12f, cordal - s * 0.015f), Size(s * 0.24f, s * 0.05f), CornerRadius(s * 0.015f))
    }

    /** La corona de olivo seca, colgada de un clavo: dos ramas que se juntan abajo con una cinta, hojas de tres tonos y huecos. */
    private fun DrawScope.coronaOlivo(clavo: Offset, r: Float, oscuro: Boolean, cerca: Float, caida: Offset) {
        val u = density
        val c = clavo + Offset(0f, r * 1.05f)
        drawCircle(sombra(oscuro, if (oscuro) 0.35f else 0.16f), r, c + caida, style = Stroke(r * 0.32f))
        drawCircle(luz(mezcla(OLIVO, MADERA_OSC, 0.6f), oscuro, cerca), r, c, style = Stroke(0.9f * u))
        val tonos = listOf(OLIVO, mezcla(OLIVO, LANA, 0.38f), mezcla(OLIVO, MADERA, 0.5f))
        for (k in 0 until 34) {
            if (azar(911, k) < 0.1f) continue
            val lado = if (k % 2 == 0) -1f else 1f
            val f = (k / 2) / 16f
            // De abajo (donde se atan) hacia arriba, cada rama por su lado; la punta de la hoja mira hacia arriba.
            val a = Math.toRadians(90.0 + lado * (12.0 + 160.0 * f)).toFloat()
            val p = Offset(c.x + r * cos(a), c.y + r * sin(a))
            val giro = Math.toDegrees(a.toDouble()).toFloat() + lado * 90f + (azar(912, k) - 0.5f) * 40f + (if (k % 3 == 0) 25f else -15f)
            val col = luz(tonos[Math.floorMod(k * 7 + (azar(913, k) * 3).toInt(), 3)], oscuro, cerca)
            rotate(giro, p) {
                drawOval(col, Offset(p.x - r * 0.04f, p.y - r * 0.32f), Size(r * 0.11f, r * 0.34f))
                drawLine(claro(oscuro, 0.3f), Offset(p.x - r * 0.01f, p.y - r * 0.28f), Offset(p.x - r * 0.01f, p.y - r * 0.04f), 0.4f * u)
            }
        }
        // La cinta roja del nudo, con sus dos cabos.
        val nudo = c + Offset(0f, r)
        drawLine(luz(ROJO, oscuro, cerca), nudo, nudo + Offset(-r * 0.25f, r * 0.55f), r * 0.1f, StrokeCap.Round)
        drawLine(luz(ROJO, oscuro, cerca), nudo, nudo + Offset(r * 0.18f, r * 0.6f), r * 0.1f, StrokeCap.Round)
        drawCircle(luz(ROJO, oscuro, cerca), r * 0.09f, nudo)
        // El clavo.
        drawCircle(sombra(oscuro, 0.4f), 1.6f * u, clavo + Offset(0.8f * u, 0.8f * u))
        drawCircle(luz(BRONCE, oscuro, cerca), 1.4f * u, clavo)
    }

    /** El aríbalo y el estrígil del gimnasio, colgados juntos de una correa con su anilla de bronce. */
    private fun DrawScope.estrigil(clavo: Offset, w: Float, oscuro: Boolean, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val anilla = clavo + Offset(0f, w * 0.05f)
        val ari = Offset(anilla.x - w * 0.026f, anilla.y + w * 0.052f)
        val ar = w * 0.024f
        val mango = Path().apply { moveTo(anilla.x + w * 0.004f, anilla.y + w * 0.006f); lineTo(anilla.x + w * 0.016f, anilla.y + w * 0.062f) }
        val hoja = Path().apply {
            moveTo(anilla.x + w * 0.016f, anilla.y + w * 0.062f)
            cubicTo(anilla.x + w * 0.034f, anilla.y + w * 0.12f, anilla.x + w * 0.032f, anilla.y + w * 0.165f, anilla.x + w * 0.006f, anilla.y + w * 0.178f)
            cubicTo(anilla.x - w * 0.01f, anilla.y + w * 0.184f, anilla.x - w * 0.018f, anilla.y + w * 0.172f, anilla.x - w * 0.014f, anilla.y + w * 0.158f)
        }
        // Las sombras en la pared.
        translate(caida.x, caida.y) {
            drawCircle(sombra(oscuro, if (oscuro) 0.38f else 0.18f), ar, ari)
            drawPath(hoja, sombra(oscuro, if (oscuro) 0.38f else 0.18f), style = Stroke(w * 0.018f, cap = StrokeCap.Round))
            drawPath(mango, sombra(oscuro, if (oscuro) 0.38f else 0.18f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        }
        // La correa doblada sobre la clavija.
        val cuero = luz(mezcla(MADERA_OSC, ROJO, 0.25f), oscuro, cerca)
        drawLine(cuero, clavo + Offset(-w * 0.006f, 0f), anilla + Offset(-w * 0.002f, 0f), 1.6f * u)
        drawLine(cuero, clavo + Offset(w * 0.006f, 0f), anilla + Offset(w * 0.002f, 0f), 1.6f * u)
        clavija(clavo, w * 0.008f, luz(MADERA, oscuro, cerca), oscuro, cerca)
        // El aríbalo: el frasco redondo de aceite, de arcilla con bandas de barniz, el cuello y su boca de disco, de un cordel.
        drawLine(luz(LANA, oscuro, cerca), anilla, ari - Offset(0f, ar * 1.25f), 0.7f * u)
        val frasco = Path().apply { addOval(Rect(ari, ar)) }
        drawPath(frasco, luz(ARCILLA, oscuro, cerca))
        clipPath(frasco) {
            drawRect(luz(BARNIZ, oscuro, cerca), Offset(ari.x - ar, ari.y - ar * 0.2f), Size(ar * 2, ar * 0.16f))
            drawRect(luz(BARNIZ, oscuro, cerca), Offset(ari.x - ar, ari.y + ar * 0.45f), Size(ar * 2, ar * 0.6f))
            for (k in 0 until 5) drawCircle(luz(BARNIZ, oscuro, cerca), ar * 0.08f, Offset(ari.x - ar * 0.6f + k * ar * 0.3f, ari.y + ar * 0.16f))
        }
        redondo(frasco, oscuro, cerca, haciaX, brillo = 0.3f)
        drawRect(luz(ARCILLA, oscuro, cerca), Offset(ari.x - ar * 0.18f, ari.y - ar * 1.2f), Size(ar * 0.36f, ar * 0.3f))
        drawOval(luz(BARNIZ, oscuro, cerca), Offset(ari.x - ar * 0.45f, ari.y - ar * 1.32f), Size(ar * 0.9f, ar * 0.22f))
        // El estrígil: el mango y la hoja curva de bronce, con su canal en sombra.
        drawPath(mango, luz(mezcla(BRONCE, MADERA_OSC, 0.3f), oscuro, cerca), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(hoja, luz(BRONCE, oscuro, cerca), style = Stroke(w * 0.017f, cap = StrokeCap.Round))
        // El canal de la hoja, cóncavo: su fondo en sombra y el filo de fuera a la luz.
        drawPath(corrido(hoja, w * 0.003f, 0f), luz(mezcla(BRONCE, BARNIZ, 0.55f), oscuro, cerca), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(corrido(hoja, -w * 0.006f, 0f), claro(oscuro, 0.5f), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
        if (oscuro) drawPath(corrido(hoja, -haciaX * w * 0.006f, 0f), FILO_MUEBLE.copy(alpha = 0.7f * cerca), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
        // La anilla de bronce.
        drawCircle(luz(BRONCE, oscuro, cerca), w * 0.006f, anilla, style = Stroke(1.4f * u))
    }

    /** La kylix, apoyada de canto contra la pared: el barniz de dentro, el labio reservado, el tondo con un delfín y las asas. */
    private fun DrawScope.kylix(c: Offset, rx: Float, ry: Float, oscuro: Boolean, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val r = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
        drawOval(sombra(oscuro, if (oscuro) 0.4f else 0.2f), r.topLeft + caida, r.size)
        sombraContacto(Offset(c.x, c.y + ry), rx * 0.9f, ry * 0.16f, if (oscuro) 0.5f else 0.32f)
        val barniz = luz(BARNIZ, oscuro, cerca)
        // Las asas, a los lados del labio.
        for (l in listOf(-1f, 1f)) {
            val asa = Rect(c.x + l * rx * 1.1f - rx * 0.2f, c.y - ry * 0.18f, c.x + l * rx * 1.1f + rx * 0.2f, c.y + ry * 0.18f)
            drawArc(barniz, if (l < 0) 90f else -90f, 180f, false, asa.topLeft, asa.size, style = Stroke(rx * 0.08f))
        }
        val fuera = Path().apply { addOval(r) }
        drawPath(fuera, barniz)
        // El labio de arcilla, una línea fina.
        drawOval(luz(ARCILLA, oscuro, cerca), r.topLeft + Offset(rx * 0.03f, ry * 0.03f), Size(rx * 1.94f, ry * 1.94f), style = Stroke(rx * 0.035f))
        val dentro = Path().apply { addOval(Rect(c.x - rx * 0.9f, c.y - ry * 0.9f, c.x + rx * 0.9f, c.y + ry * 0.9f)) }
        // Lo cóncavo: la sombra arriba a la izquierda, por dentro, y la luz abajo a la derecha.
        drawPath(borde(dentro, rx * 0.22f, ry * 0.22f), sombra(oscuro, 0.4f))
        drawPath(borde(dentro, -rx * 0.12f, -ry * 0.12f), if (oscuro) FILO_MUEBLE.copy(alpha = 0.4f * cerca) else luzMueble(0.22f))
        // El tondo: arcilla con un círculo de barniz y un delfín.
        val tr = rx * 0.4f
        val t = Offset(c.x + rx * 0.04f, c.y + ry * 0.04f)
        drawOval(panel(oscuro, cerca), Offset(t.x - tr, t.y - tr * ry / rx), Size(tr * 2, tr * 2 * ry / rx))
        drawOval(barniz, Offset(t.x - tr * 0.9f, t.y - tr * 0.9f * ry / rx), Size(tr * 1.8f, tr * 1.8f * ry / rx), style = Stroke(0.7f * u))
        val R = tr * 0.75f
        val delfin = Path().apply {
            moveTo(t.x - 0.85f * R, t.y + 0.2f * R)
            lineTo(t.x - 0.62f * R, t.y + 0.08f * R)
            cubicTo(t.x - 0.4f * R, t.y - 0.55f * R, t.x + 0.35f * R, t.y - 0.5f * R, t.x + 0.62f * R, t.y + 0.02f * R)
            lineTo(t.x + 0.85f * R, t.y - 0.22f * R); lineTo(t.x + 0.74f * R, t.y + 0.12f * R); lineTo(t.x + 0.9f * R, t.y + 0.34f * R); lineTo(t.x + 0.56f * R, t.y + 0.16f * R)
            cubicTo(t.x + 0.3f * R, t.y - 0.12f * R, t.x - 0.3f * R, t.y - 0.06f * R, t.x - 0.6f * R, t.y + 0.26f * R)
            close()
            moveTo(t.x - 0.05f * R, t.y - 0.38f * R); lineTo(t.x + 0.12f * R, t.y - 0.62f * R); lineTo(t.x + 0.16f * R, t.y - 0.36f * R); close()
        }
        drawPath(delfin, barniz)
        // El brillo del barniz en el labio, arriba a la izquierda.
        drawArc(claro(oscuro, 0.45f), 195f, 60f, false, r.topLeft + Offset(rx * 0.08f, ry * 0.08f), Size(rx * 1.84f, ry * 1.84f), style = Stroke(rx * 0.04f, cap = StrokeCap.Round))
        if (oscuro) drawArc(FILO_MUEBLE.copy(alpha = 0.7f * cerca), if (haciaX > 0) -40f else 180f, 70f, false, r.topLeft, r.size, style = Stroke(rx * 0.04f, cap = StrokeCap.Round))
    }

    /** El enócoe de arcilla: la boca trilobulada con el pico, el cuello y el asa de barniz, una banda negra en el vientre. */
    private fun DrawScope.enocoe(pie: Offset, H: Float, oscuro: Boolean, cerca: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        val cuerpo = perfil(cx, pie.y, H, H, floatArrayOf(0.11f, -0.86f, 0.1f, -0.78f, 0.22f, -0.66f, 0.32f, -0.5f, 0.33f, -0.34f, 0.27f, -0.18f, 0.19f, -0.08f, 0.17f, -0.03f, 0.19f, 0f))
        val boca = Path().apply {
            moveTo(cx - H * 0.11f, pie.y - H * 0.86f)
            lineTo(cx - H * 0.3f, pie.y - H * 1.0f)
            cubicTo(cx - H * 0.2f, pie.y - H * 1.02f, cx + H * 0.05f, pie.y - H * 0.99f, cx + H * 0.14f, pie.y - H * 0.97f)
            lineTo(cx + H * 0.11f, pie.y - H * 0.86f)
            close()
        }
        val asa = Path().apply {
            moveTo(cx + H * 0.12f, pie.y - H * 0.95f)
            cubicTo(cx + H * 0.42f, pie.y - H * 1.1f, cx + H * 0.5f, pie.y - H * 0.74f, cx + H * 0.31f, pie.y - H * 0.56f)
        }
        translate(caida.x, caida.y) { drawPath(cuerpo, sombra(oscuro, if (oscuro) 0.4f else 0.18f)); drawPath(asa, sombra(oscuro, if (oscuro) 0.4f else 0.18f), style = Stroke(H * 0.06f)) }
        sombraContacto(Offset(cx + 2 * u, pie.y), H * 0.5f, H * 0.06f, if (oscuro) 0.5f else 0.32f)
        val barniz = luz(BARNIZ, oscuro, cerca)
        drawPath(asa, barniz, style = Stroke(H * 0.06f, cap = StrokeCap.Round))
        drawPath(corrido(asa, -H * 0.015f, 0f), claro(oscuro, 0.3f), style = Stroke(H * 0.012f, cap = StrokeCap.Round))
        drawPath(cuerpo, luz(TERRACOTA, oscuro, cerca))
        clipPath(cuerpo) {
            drawRect(barniz, Offset(cx - H, pie.y - H * 1.1f), Size(H * 2, H * 0.42f))
            drawRect(barniz, Offset(cx - H, pie.y - H * 0.4f), Size(H * 2, H * 0.1f))
            drawRect(luz(PURPURA, oscuro, cerca), Offset(cx - H, pie.y - H * 0.43f), Size(H * 2, H * 0.015f))
            drawRect(barniz, Offset(cx - H, pie.y - H * 0.06f), Size(H * 2, H * 0.06f))
            // Las marcas del torno.
            for (k in 0 until 4) drawLine(sombra(oscuro, 0.12f), Offset(cx - H, pie.y - H * (0.15f + 0.06f * k)), Offset(cx + H, pie.y - H * (0.15f + 0.06f * k)), 0.5f * u)
        }
        // Un desconchón en el barniz del hombro.
        drawOval(luz(TERRACOTA, oscuro, cerca), Offset(cx - H * 0.28f, pie.y - H * 0.7f), Size(H * 0.07f, H * 0.04f))
        redondo(cuerpo, oscuro, cerca, 1f, brillo = 0.3f)
        drawPath(boca, barniz)
        drawPath(borde(boca, 0f, H * 0.03f), claro(oscuro, 0.35f))
        drawOval(luz(mezcla(BARNIZ, NOCHE, 0.4f), oscuro, cerca), Offset(cx - H * 0.12f, pie.y - H * 0.99f), Size(H * 0.22f, H * 0.04f))
    }

    /** El kálathos: el cesto de la lana, que se abre hacia arriba, con la lana cardada encima y un huso clavado con su hilo. */
    private fun DrawScope.kalathos(pie: Offset, ancho: Float, alto: Float, oscuro: Boolean, cerca: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        val boca = pie.y - alto
        val cuerpo = Path().apply {
            moveTo(cx - ancho * 0.26f, pie.y); lineTo(cx + ancho * 0.26f, pie.y)
            cubicTo(cx + ancho * 0.32f, pie.y - alto * 0.45f, cx + ancho * 0.42f, boca + alto * 0.2f, cx + ancho * 0.5f, boca)
            lineTo(cx - ancho * 0.5f, boca)
            cubicTo(cx - ancho * 0.42f, boca + alto * 0.2f, cx - ancho * 0.32f, pie.y - alto * 0.45f, cx - ancho * 0.26f, pie.y)
            close()
        }
        translate(caida.x, caida.y) { drawPath(cuerpo, sombra(oscuro, if (oscuro) 0.4f else 0.18f)) }
        sombraContacto(Offset(cx + 2 * u, pie.y), ancho * 0.7f, alto * 0.08f, if (oscuro) 0.5f else 0.32f)
        // La lana cardada, en copos, asomando por la boca.
        val lana = luz(LANA, oscuro, cerca)
        for (k in 0 until 7) {
            val x = cx + ancho * (-0.4f + 0.13f * k)
            val r = ancho * (0.1f + 0.05f * azar(921, k))
            drawCircle(lana, r, Offset(x, boca - r * 0.3f - ancho * 0.03f * sin(k * 0.9f)))
        }
        for (k in 0 until 7) {
            val x = cx + ancho * (-0.4f + 0.13f * k)
            val r = ancho * (0.1f + 0.05f * azar(921, k))
            drawArc(sombra(oscuro, 0.2f), 20f, 120f, false, Offset(x - r, boca - r * 1.3f), Size(2 * r, 2 * r), style = Stroke(0.7f * u))
        }
        // El huso: la varilla, la tortera de barro y el ovillo de hilo hilado, clavado en la lana.
        val h0 = Offset(cx - ancho * 0.05f, boca + alto * 0.05f)
        val h1 = Offset(cx + ancho * 0.48f, boca - alto * 0.85f)
        drawLine(luz(MADERA, oscuro, cerca), h0, h1, 1.5f * u, StrokeCap.Round)
        val ovillo = h0 + (h1 - h0) * 0.42f
        rotate(-60f, ovillo) { drawOval(luz(mezcla(LANA, OCRE, 0.2f), oscuro, cerca), Offset(ovillo.x - ancho * 0.12f, ovillo.y - ancho * 0.045f), Size(ancho * 0.24f, ancho * 0.09f)) }
        rotate(-60f, ovillo) { for (k in 0 until 5) drawLine(sombra(oscuro, 0.25f), Offset(ovillo.x - ancho * 0.09f + k * ancho * 0.045f, ovillo.y - ancho * 0.04f), Offset(ovillo.x - ancho * 0.07f + k * ancho * 0.045f, ovillo.y + ancho * 0.04f), 0.5f * u) }
        val tortera = h0 + (h1 - h0) * 0.22f
        drawOval(luz(TERRACOTA, oscuro, cerca), Offset(tortera.x - ancho * 0.06f, tortera.y - ancho * 0.03f), Size(ancho * 0.12f, ancho * 0.06f))
        drawLine(luz(LANA, oscuro, cerca), h1, Offset(cx - ancho * 0.2f, boca - ancho * 0.12f), 0.5f * u)
        // El cuerpo de mimbre: hileras que se cruzan y las varas de la urdimbre.
        val mimbre = luz(mezcla(MADERA, LANA, 0.45f), oscuro, cerca)
        drawPath(cuerpo, mimbre)
        clipPath(cuerpo) {
            var y = boca + 1.5f * u; var j = 0
            while (y < pie.y) {
                val paso = 4f * u
                var x = cx - ancho * 0.6f + (if (j % 2 == 0) 0f else paso / 2)
                while (x < cx + ancho * 0.6f) {
                    drawOval(luz(mezcla(MADERA, LANA, 0.25f), oscuro, cerca), Offset(x, y - 1f * u), Size(paso * 0.7f, 2f * u))
                    x += paso
                }
                y += 2.6f * u; j++
            }
            for (k in 0 until 9) {
                val f = -0.45f + 0.1125f * k
                drawLine(sombra(oscuro, 0.15f), Offset(cx + ancho * f, boca), Offset(cx + ancho * f * 0.55f, pie.y), 0.6f * u)
            }
        }
        redondo(cuerpo, oscuro, cerca, 1f)
        // El borde trenzado de la boca.
        drawLine(luz(mezcla(MADERA, MADERA_OSC, 0.3f), oscuro, cerca), Offset(cx - ancho * 0.5f, boca), Offset(cx + ancho * 0.5f, boca), 2.6f * u, StrokeCap.Round)
        drawLine(claro(oscuro, 0.4f), Offset(cx - ancho * 0.48f, boca - 0.7f * u), Offset(cx + ancho * 0.3f, boca - 0.7f * u), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u)))
    }

    /**
     * El lampadario de bronce en el tablón: tres patas de león, el fuste con sus anillos, el plato y, encima, la lucerna ática
     * de barniz negro con su pico (de donde sale la llama) y su asa. De día está apagada, con el hollín en el pico.
     */
    private fun DrawScope.lampadario(pie: Offset, alto: Float, oscuro: Boolean, cerca: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        val bronce = luz(BRONCE, oscuro, cerca)
        val bronceLuz = if (oscuro) mezcla(bronce, FILO_MUEBLE, 0.5f) else mezcla(BRONCE, LUZ_MUEBLE, 0.45f)
        val bronceOsc = luz(mezcla(BRONCE, BARNIZ, 0.45f), oscuro, cerca)
        val disco = pie.y - alto
        val arranque = pie.y - alto * 0.12f
        // La sombra del fuste en la pared (de día; de noche la llama está arriba y la tira hacia abajo, detrás de él).
        if (!oscuro) {
            drawLine(sombra(false, 0.16f), Offset(cx + caida.x, arranque), Offset(cx + caida.x, disco + caida.y), alto * 0.035f)
            drawOval(sombra(false, 0.16f), Offset(cx - alto * 0.12f + caida.x, disco - alto * 0.08f + caida.y), Size(alto * 0.24f, alto * 0.08f))
        }
        sombraContacto(Offset(cx + 2 * u, pie.y), alto * 0.42f, alto * 0.04f, if (oscuro) 0.4f else 0.32f)
        // Las patas: la de atrás más oscura, las dos de delante con su garra.
        for ((k, dx) in listOf(0.0f, -0.17f, 0.17f).withIndex()) {
            val fin = Offset(cx + alto * dx, pie.y - (if (k == 0) alto * 0.02f else 0f))
            val p = Path().apply { moveTo(cx, arranque); cubicTo(cx + alto * dx * 0.2f, arranque + alto * 0.04f, fin.x - alto * dx * 0.2f, fin.y - alto * 0.08f, fin.x, fin.y - alto * 0.015f) }
            drawPath(p, if (k == 0) bronceOsc else bronce, style = Stroke(alto * 0.03f, cap = StrokeCap.Round))
            drawOval(if (k == 0) bronceOsc else bronce, Offset(fin.x - alto * 0.03f, fin.y - alto * 0.025f), Size(alto * 0.06f, alto * 0.03f))
            if (k != 0) drawPath(corrido(p, -alto * 0.008f, 0f), claro(oscuro, 0.4f), style = Stroke(alto * 0.008f, cap = StrokeCap.Round))
        }
        // El fuste, con su arista de luz y su lado de sombra, y tres anillos.
        val fw = alto * 0.032f
        drawRect(bronce, Offset(cx - fw / 2, disco), Size(fw, arranque - disco))
        drawRect(bronceLuz.copy(alpha = if (oscuro) 0.6f else 0.8f), Offset(cx - fw / 2, disco), Size(fw * 0.3f, arranque - disco))
        drawRect(sombra(oscuro, 0.35f), Offset(cx + fw * 0.15f, disco), Size(fw * 0.35f, arranque - disco))
        for (k in 1..3) {
            val y = disco + (arranque - disco) * k / 4f
            drawOval(bronce, Offset(cx - fw * 1.1f, y - fw * 0.35f), Size(fw * 2.2f, fw * 0.7f))
            drawOval(bronceLuz, Offset(cx - fw * 1f, y - fw * 0.35f), Size(fw * 1.2f, fw * 0.3f))
        }
        drawOval(bronce, Offset(cx - fw * 1.6f, arranque - fw * 0.5f), Size(fw * 3.2f, fw))
        // El capitel en copa y el plato.
        drawPath(Path().apply { moveTo(cx - fw * 0.6f, disco + alto * 0.06f); lineTo(cx - alto * 0.07f, disco + alto * 0.008f); lineTo(cx + alto * 0.07f, disco + alto * 0.008f); lineTo(cx + fw * 0.6f, disco + alto * 0.06f); close() }, bronce)
        drawOval(bronceOsc, Offset(cx - alto * 0.13f, disco - alto * 0.008f), Size(alto * 0.26f, alto * 0.03f))
        drawOval(bronceLuz, Offset(cx - alto * 0.12f, disco - alto * 0.01f), Size(alto * 0.14f, alto * 0.016f))
        val L = alto * 1.3f
        // La lucerna: el cuerpo bajo y redondo, el pico largo a la izquierda, el asa a la derecha y el agujero de llenarla.
        val barniz = luz(BARNIZ, oscuro, maxOf(cerca, 0.5f))
        val cuerpo = Path().apply { addOval(Rect(cx - L * 0.11f, disco - L * 0.085f, cx + L * 0.1f, disco - L * 0.004f)) }
        val pico = Path().apply {
            moveTo(cx - L * 0.06f, disco - L * 0.07f)
            lineTo(cx - L * 0.24f, disco - L * 0.065f)
            cubicTo(cx - L * 0.27f, disco - L * 0.06f, cx - L * 0.27f, disco - L * 0.03f, cx - L * 0.24f, disco - L * 0.028f)
            lineTo(cx - L * 0.06f, disco - L * 0.02f)
            close()
        }
        drawArc(barniz, -90f, 180f, false, Offset(cx + L * 0.06f, disco - L * 0.07f), Size(L * 0.09f, L * 0.05f), style = Stroke(L * 0.016f))
        drawPath(pico, barniz)
        drawPath(cuerpo, barniz)
        drawRect(luz(ARCILLA, oscuro, maxOf(cerca, 0.5f)), Offset(cx - L * 0.1f, disco - L * 0.016f), Size(L * 0.19f, L * 0.008f))
        drawOval(luz(mezcla(BARNIZ, NOCHE, 0.5f), oscuro, cerca), Offset(cx - L * 0.025f, disco - L * 0.086f), Size(L * 0.05f, L * 0.014f))
        redondo(cuerpo, oscuro, 0.6f, -1f, brillo = 0.4f)
        drawLine(claro(oscuro, 0.45f), Offset(cx - L * 0.22f, disco - L * 0.062f), Offset(cx - L * 0.08f, disco - L * 0.066f), L * 0.006f, StrokeCap.Round)
        // El pico ennegrecido por la mecha y el agujero de la mecha.
        drawOval(BARNIZ, Offset(cx - L * 0.265f, disco - L * 0.07f), Size(L * 0.04f, L * 0.02f))
        if (oscuro) {
            // De noche, la llama alumbra el barniz desde el pico: el filo de arriba del cuerpo y del plato.
            drawArc(FILO_MUEBLE.copy(alpha = 0.8f), 190f, 120f, false, Offset(cx - L * 0.11f, disco - L * 0.085f), Size(L * 0.21f, L * 0.08f), style = Stroke(L * 0.008f))
            drawLine(FILO_MUEBLE.copy(alpha = 0.85f), Offset(cx - L * 0.24f, disco - L * 0.066f), Offset(cx - L * 0.07f, disco - L * 0.071f), L * 0.006f)
            drawLine(FILO_MUEBLE.copy(alpha = 0.6f), Offset(cx - L * 0.12f, disco - L * 0.006f), Offset(cx + L * 0.02f, disco - L * 0.006f), L * 0.006f)
        } else {
            // De día, apagada: un hilo de humo que ya no sale, solo el hollín del pico.
            drawOval(sombra(false, 0.3f), Offset(cx - L * 0.27f, disco - L * 0.08f), Size(L * 0.05f, L * 0.02f))
        }
    }

    /** La llama de la lucerna, encendida: el halo chico, la lengua cálida y su corazón claro. No pasa por la noche: da luz. */
    private fun DrawScope.llamita(punta: Offset, alto: Float) {
        val base = punta - Offset(0f, alto * 0.012f)
        val h = alto * 0.15f; val a = alto * 0.06f
        mancha(base - Offset(0f, h * 0.45f), alto * 0.14f, FILO_MUEBLE.copy(alpha = 0.55f))
        val lengua = Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x - a * 0.7f, base.y - h * 0.15f, base.x - a * 0.5f, base.y - h * 0.6f, base.x + a * 0.05f, base.y - h)
            cubicTo(base.x + a * 0.25f, base.y - h * 0.6f, base.x + a * 0.7f, base.y - h * 0.2f, base.x, base.y)
            close()
        }
        drawPath(lengua, Color(0xFFF2A24A))
        val corazon = Path().apply {
            moveTo(base.x, base.y - h * 0.04f)
            cubicTo(base.x - a * 0.35f, base.y - h * 0.2f, base.x - a * 0.2f, base.y - h * 0.5f, base.x + a * 0.04f, base.y - h * 0.68f)
            cubicTo(base.x + a * 0.15f, base.y - h * 0.45f, base.x + a * 0.35f, base.y - h * 0.2f, base.x, base.y - h * 0.04f)
            close()
        }
        drawPath(corazon, Color(0xFFFFF0C4))
        drawOval(Color(0xFF6F7FB8).copy(alpha = 0.7f), Offset(base.x - a * 0.22f, base.y - h * 0.08f), Size(a * 0.44f, h * 0.12f))
    }

    /**
     * El kibotós: el cofre de ciprés con sus pies, los montantes y los paneles rehundidos, la tapa a dos aguas con los botones y
     * el cordel que lo cierra, y un manto de lana doblado encima, con su orilla púrpura colgando por delante.
     */
    private fun DrawScope.cofre(r: Rect, w: Float, oscuro: Boolean, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val alto = r.height
        val tapa = alto * 0.38f
        val vuelo = w * 0.008f
        val pies = alto * 0.12f
        val cumbre = Offset(r.center.x, r.top - tapa)
        val silueta = Path().apply {
            moveTo(r.left - vuelo, r.top); lineTo(cumbre.x, cumbre.y); lineTo(r.right + vuelo, r.top)
            lineTo(r.right, r.bottom + pies); lineTo(r.left, r.bottom + pies); close()
        }
        drawPath(corrido(silueta, caida.x, caida.y * 0.5f), sombra(oscuro, if (oscuro) 0.4f else 0.2f))
        sombraContacto(Offset(r.center.x + 4 * u, r.bottom + pies), r.width * 1.08f, alto * 0.12f, if (oscuro) 0.55f else 0.36f)
        val mad = luz(mezcla(MADERA, MADERA_OSC, 0.25f), oscuro, cerca)
        // El cuerpo: el frente con su veta.
        madera(Rect(r.left, r.top, r.right, r.bottom), mad, 931, oscuro, false)
        // Los pies (los montantes que siguen hasta el suelo).
        val mw = r.width * 0.09f
        listOf(r.left, r.right - mw).forEach { x ->
            madera(Rect(x, r.top, x + mw, r.bottom + pies), luz(MADERA, oscuro, cerca), 932, oscuro, true)
            drawRect(claro(oscuro, 0.25f), Offset(x, r.top), Size(mw * 0.25f, alto + pies))
            drawRect(sombra(oscuro, 0.25f), Offset(x + mw * 0.72f, r.top), Size(mw * 0.28f, alto + pies))
        }
        // Los dos paneles rehundidos: sombra arriba y a la izquierda por dentro, luz abajo y a la derecha.
        val ancho = (r.width - mw * 3) / 2
        for (k in 0..1) {
            val p = Rect(r.left + mw + k * (ancho + mw), r.top + alto * 0.16f, r.left + mw + k * (ancho + mw) + ancho, r.bottom - alto * 0.14f)
            drawRect(luz(mezcla(MADERA, MADERA_OSC, 0.35f), oscuro, cerca), p.topLeft, p.size)
            vetas(p, MADERA_OSC.copy(alpha = 0.3f), 933 + k, densidad = 0.5f, grosor = 0.7f)
            drawRect(sombra(oscuro, 0.4f), p.topLeft, Size(p.width, 2f * u))
            drawRect(sombra(oscuro, 0.3f), p.topLeft, Size(2f * u, p.height))
            drawRect(claro(oscuro, 0.3f), Offset(p.left, p.bottom - 1.2f * u), Size(p.width, 1.2f * u))
            drawRect(claro(oscuro, 0.2f), Offset(p.right - 1.2f * u, p.top), Size(1.2f * u, p.height))
        }
        // El travesaño de en medio.
        drawRect(claro(oscuro, 0.2f), Offset(r.left + mw + ancho, r.top), Size(mw * 0.25f, alto))
        drawRect(sombra(oscuro, 0.2f), Offset(r.left + mw * 1.7f + ancho, r.top), Size(mw * 0.3f, alto))
        // La tapa a dos aguas: la falda de la izquierda a la luz, la de la derecha en sombra; el canto de abajo.
        val izq = Path().apply { moveTo(r.left - vuelo, r.top); lineTo(cumbre.x, cumbre.y); lineTo(cumbre.x, r.top); close() }
        val der = Path().apply { moveTo(r.right + vuelo, r.top); lineTo(cumbre.x, cumbre.y); lineTo(cumbre.x, r.top); close() }
        drawPath(izq, luz(mezcla(MADERA, LUZ_MUEBLE, 0.15f), oscuro, cerca))
        drawPath(der, luz(mezcla(MADERA, SOMBRA_MUEBLE, 0.3f), oscuro, cerca))
        clipPath(izq) { vetas(Rect(r.left - vuelo, cumbre.y, cumbre.x, r.top), MADERA_OSC.copy(alpha = 0.3f), 935, densidad = 0.6f, grosor = 0.6f) }
        clipPath(der) { vetas(Rect(cumbre.x, cumbre.y, r.right + vuelo, r.top), MADERA_OSC.copy(alpha = 0.3f), 936, densidad = 0.6f, grosor = 0.6f) }
        drawRect(luz(MADERA_OSC, oscuro, cerca), Offset(r.left - vuelo, r.top - 1.5f * u), Size(r.width + 2 * vuelo, 3f * u))
        drawRect(sombra(oscuro, 0.35f), Offset(r.left, r.top + 1.5f * u), Size(r.width, 2.5f * u))
        if (oscuro && cerca > 0.05f) drawLine(FILO_MUEBLE.copy(alpha = 0.5f * cerca), if (haciaX > 0) cumbre else Offset(r.left - vuelo, r.top), if (haciaX > 0) Offset(r.right + vuelo, r.top) else cumbre, 1.2f * u)
        // Los dos botones de bronce y el cordel que va de uno a otro.
        val b1 = Offset(cumbre.x + r.width * 0.12f, r.top - tapa * 0.35f)
        val b2 = Offset(cumbre.x + r.width * 0.12f, r.top + alto * 0.08f)
        drawLine(luz(LANA, oscuro, cerca), b1, b2, 1f * u)
        drawLine(luz(LANA, oscuro, cerca), b1 + Offset(1.4f * u, 0f), b2 - Offset(1.4f * u, 0f), 1f * u)
        listOf(b1, b2).forEach { p ->
            drawCircle(sombra(oscuro, 0.4f), w * 0.007f, p + Offset(0.8f * u, 0.8f * u))
            drawCircle(luz(BRONCE, oscuro, cerca), w * 0.007f, p)
            drawCircle(claro(oscuro, 0.5f), w * 0.003f, p - Offset(w * 0.002f, w * 0.002f))
        }
        // El manto de lana doblado sobre la falda de la izquierda: por arriba sigue la tapa; en el canto se dobla en un rollo
        // grueso y por delante cae con peso, escurriéndose por la esquina del cofre: el ruedo baja en diagonal (más largo a la
        // izquierda) y los pliegues se abren en abanico desde donde la tela se recoge, cada uno con su cara a la luz y su
        // cara en sombra. La orilla púrpura sigue el ruedo.
        val lana = luz(LANA, oscuro, cerca)
        val x0 = r.left - vuelo - w * 0.012f; val x1 = r.left + w * 0.17f
        val arriba = r.top + w * 0.002f
        // El ruedo: de la derecha (corto) a la izquierda (largo), con una onda en cada pliegue.
        val corto = r.top + alto * 0.3f; val largo = r.top + alto * 0.86f
        fun enRuedo(t: Float) = Offset(x1 + (x0 - x1) * t, corto + (largo - corto) * t * (0.75f + 0.25f * t))
        // Dónde empieza cada pliegue arriba (apretados a la derecha, donde la tela se recoge) y dónde acaba en el ruedo.
        val arranques = listOf(0.06f, 0.2f, 0.4f, 0.66f)
        val finales = listOf(0.1f, 0.34f, 0.62f, 0.9f)
        val caida = Path().apply {
            moveTo(x0, arriba); lineTo(x1, arriba); lineTo(enRuedo(0f).x, enRuedo(0f).y)
            var prev = 0f
            for (f in finales + 1f) {
                val m = enRuedo((prev + f) / 2)
                val e = enRuedo(f)
                // Cada tramo del ruedo se comba hacia abajo: el peso de la tela.
                quadraticTo(m.x, m.y + alto * 0.07f, e.x, e.y)
                prev = f
            }
            lineTo(x0 - w * 0.004f, arriba + alto * 0.2f)
            close()
        }
        val encima = Path().apply {
            moveTo(x0, r.top + w * 0.004f)
            lineTo(cumbre.x + w * 0.012f, cumbre.y - w * 0.01f)
            lineTo(cumbre.x + w * 0.042f, cumbre.y + tapa * 0.5f)
            cubicTo(cumbre.x + w * 0.012f, r.top - w * 0.002f, x1 + w * 0.012f, r.top - w * 0.004f, x1, arriba + w * 0.004f)
            lineTo(x0, arriba + w * 0.004f)
            close()
        }
        // La sombra que deja en el frente del cofre y en la tapa.
        drawPath(corrido(caida, w * 0.009f, w * 0.006f), sombra(oscuro, if (oscuro) 0.4f else 0.34f))
        drawPath(corrido(encima, w * 0.006f, w * 0.004f), sombra(oscuro, 0.25f))
        drawPath(encima, lana)
        clipPath(encima) {
            // Dos dobleces que siguen la falda de la tapa, desiguales, con su luz arriba.
            for (k in 0 until 2) {
                val d = Offset(0f, tapa * (0.3f + 0.3f * k))
                val fin = cumbre + Offset(w * (0.03f - 0.03f * k), tapa * 0.1f * k)
                drawLine(sombra(oscuro, 0.2f), Offset(x0 + w * 0.02f * k, r.top) + d, fin + d, 1.6f * u, StrokeCap.Round)
                drawLine(claro(oscuro, 0.5f), Offset(x0 + w * 0.02f * k, r.top - 1.6f * u) + d, fin + d - Offset(0f, 1.6f * u), 1f * u, StrokeCap.Round)
            }
            drawPath(borde(encima, w * 0.03f, 0f), sombra(oscuro, 0.18f))
        }
        drawPath(caida, lana)
        clipPath(caida) {
            for (i in arranques.indices) {
                val a = Offset(x1 + (x0 - x1) * arranques[i], arriba + alto * 0.06f)
                val b = enRuedo(finales[i]) + Offset(0f, alto * 0.06f)
                val ancho = (x1 - x0) * (0.07f + 0.05f * i)
                // La cara en sombra, a la derecha de la cresta, que se abre hacia abajo; la cara a la luz, a la izquierda.
                drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x + ancho, b.y - alto * 0.02f); lineTo(a.x + ancho * 0.15f, a.y); close() },
                    sombra(oscuro, 0.3f))
                drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x - ancho * 0.7f, b.y); lineTo(a.x - ancho * 0.1f, a.y); close() },
                    claro(oscuro, 0.36f))
                // El valle hondo junto a la cara en sombra.
                drawLine(sombra(oscuro, 0.45f), a + Offset(ancho * 0.15f, 0f), b + Offset(ancho, -alto * 0.02f), 1f * u)
            }
            // La orilla púrpura, paralela al ruedo, y un hilo fino más arriba.
            val orilla = Path().apply {
                val p0 = enRuedo(0f); moveTo(p0.x, p0.y - alto * 0.06f)
                var prev = 0f
                for (f in finales + 1f) {
                    val m = enRuedo((prev + f) / 2); val e = enRuedo(f)
                    quadraticTo(m.x, m.y + alto * 0.01f, e.x, e.y - alto * 0.06f); prev = f
                }
            }
            drawPath(orilla, luz(PURPURA, oscuro, cerca), style = Stroke(alto * 0.06f))
            drawPath(corrido(orilla, 0f, -alto * 0.08f), luz(PURPURA, oscuro, cerca), style = Stroke(alto * 0.018f))
            // La lana pesa: más sombra abajo y hacia la esquina, donde la tela se junta.
            drawPath(borde(caida, w * 0.02f, -alto * 0.1f), sombra(oscuro, 0.16f))
        }
        // El rollo del doblez sobre el canto de la tapa: grueso, con su luz arriba y su sombra debajo.
        drawLine(sombra(oscuro, 0.35f), Offset(x0, arriba + w * 0.006f), Offset(x1, arriba + w * 0.006f), w * 0.008f, StrokeCap.Round)
        drawLine(lana, Offset(x0, arriba + w * 0.001f), Offset(x1, arriba + w * 0.001f), w * 0.009f, StrokeCap.Round)
        drawLine(claro(oscuro, 0.6f), Offset(x0 + w * 0.004f, arriba - w * 0.002f), Offset(x1 - w * 0.01f, arriba - w * 0.002f), w * 0.003f, StrokeCap.Round)
        // Dos flecos en la punta que cuelga.
        val punta = enRuedo(1f)
        drawLine(lana, punta, punta + Offset(-w * 0.004f, alto * 0.1f), 1.2f * u, StrokeCap.Round)
        drawLine(lana, punta + Offset(w * 0.008f, -alto * 0.01f), punta + Offset(w * 0.006f, alto * 0.08f), 1.2f * u, StrokeCap.Round)
    }


    /** La crátera de columnas, grande, en el suelo y cortada por el borde: barniz negro, el cuello con capullos de loto, el panel con una rama de hiedra, el asa de columna y el labio con su desconchón. */
    private fun DrawScope.cratera(pie: Offset, H: Float, oscuro: Boolean, cerca: Float, haciaX: Float, caida: Offset) {
        val u = density
        val cx = pie.x
        val cuerpo = perfil(cx, pie.y, H, H, floatArrayOf(0.47f, -0.9f, 0.46f, -0.8f, 0.55f, -0.7f, 0.62f, -0.56f, 0.6f, -0.42f, 0.5f, -0.28f, 0.33f, -0.15f, 0.2f, -0.08f, 0.17f, -0.045f))
        val piePath = Path().apply { moveTo(cx - H * 0.17f, pie.y - H * 0.05f); lineTo(cx + H * 0.17f, pie.y - H * 0.05f); lineTo(cx + H * 0.28f, pie.y); lineTo(cx - H * 0.28f, pie.y); close() }
        drawPath(corrido(cuerpo, caida.x, caida.y * 0.4f), sombra(oscuro, if (oscuro) 0.4f else 0.2f))
        sombraContacto(Offset(cx + 4 * u, pie.y), H * 0.8f, H * 0.07f, if (oscuro) 0.55f else 0.36f)
        val barniz = luz(BARNIZ, oscuro, cerca)
        val pc = luz(ARCILLA, oscuro, cerca)
        drawPath(piePath, barniz)
        drawRect(pc, Offset(cx - H * 0.28f, pie.y - H * 0.012f), Size(H * 0.56f, H * 0.012f))
        drawPath(cuerpo, barniz)
        clipPath(cuerpo) {
            // El cuello de arcilla con su fila de capullos de loto.
            drawRect(pc, Offset(cx - H, pie.y - H * 0.88f), Size(H * 2, H * 0.11f))
            var x = cx - H * 0.6f
            while (x < cx + H * 0.6f) {
                drawLine(barniz, Offset(x, pie.y - H * 0.88f), Offset(x, pie.y - H * 0.83f), 0.8f * u)
                drawOval(barniz, Offset(x - H * 0.018f, pie.y - H * 0.84f), Size(H * 0.036f, H * 0.05f))
                x += H * 0.08f
            }
            // El panel del vientre con una rama de hiedra.
            val panelR = Rect(cx - H * 0.46f, pie.y - H * 0.64f, cx + H * 0.46f, pie.y - H * 0.34f)
            drawRect(pc, panelR.topLeft, panelR.size)
            val y = panelR.center.y
            val rama = Path().apply {
                moveTo(panelR.left + H * 0.04f, y)
                var xx = panelR.left + H * 0.04f
                var k = 0
                while (xx < panelR.right - H * 0.1f) { cubicTo(xx + H * 0.04f, y - H * 0.05f * (if (k % 2 == 0) 1 else -1), xx + H * 0.08f, y - H * 0.05f * (if (k % 2 == 0) 1 else -1), xx + H * 0.12f, y); xx += H * 0.12f; k++ }
            }
            drawPath(rama, barniz, style = Stroke(1f * u))
            var xh = panelR.left + H * 0.1f; var k = 0
            while (xh < panelR.right - H * 0.06f) {
                val ly = y + (if (k % 2 == 0) -1f else 1f) * H * 0.07f
                rotate(if (k % 2 == 0) 20f else -20f, Offset(xh, ly)) {
                    drawPath(Path().apply { moveTo(xh, ly + H * 0.035f); cubicTo(xh - H * 0.05f, ly, xh - H * 0.02f, ly - H * 0.04f, xh, ly - H * 0.015f); cubicTo(xh + H * 0.02f, ly - H * 0.04f, xh + H * 0.05f, ly, xh, ly + H * 0.035f); close() }, barniz)
                }
                xh += H * 0.12f; k++
            }
            drawRect(barniz, Offset(panelR.left, panelR.bottom - H * 0.01f), Size(panelR.width, H * 0.01f))
            // Los rayos al pie.
            drawRect(pc, Offset(cx - H, pie.y - H * 0.24f), Size(H * 2, H * 0.08f))
            var xr = cx - H * 0.6f
            while (xr < cx + H * 0.6f) {
                drawPath(Path().apply { moveTo(xr - H * 0.025f, pie.y - H * 0.16f); lineTo(xr, pie.y - H * 0.24f); lineTo(xr + H * 0.025f, pie.y - H * 0.16f); close() }, barniz)
                xr += H * 0.07f
            }
        }
        redondo(cuerpo, oscuro, cerca, haciaX, brillo = 0.35f)
        // El asa de columna de la izquierda: las dos columnitas y la placa sobre el labio.
        val asa = Path().apply { moveTo(cx - H * 0.57f, pie.y - H * 0.66f); cubicTo(cx - H * 0.74f, pie.y - H * 0.7f, cx - H * 0.74f, pie.y - H * 0.86f, cx - H * 0.66f, pie.y - H * 0.97f) }
        drawPath(asa, barniz, style = Stroke(H * 0.04f, cap = StrokeCap.Round))
        drawPath(corrido(asa, -H * 0.01f, 0f), claro(oscuro, 0.35f), style = Stroke(H * 0.01f, cap = StrokeCap.Round))
        // El labio: ancho y plano, de barniz con lengüetas en el canto; la placa del asa encima.
        val labio = Rect(cx - H * 0.6f, pie.y - H * 1.0f, cx + H * 0.6f, pie.y - H * 0.92f)
        drawRect(barniz, labio.topLeft, labio.size)
        clipRect(labio.left, labio.top, labio.right, labio.bottom) {
            var xl = labio.left + H * 0.01f
            while (xl < labio.right) { drawRect(pc, Offset(xl, labio.top + H * 0.02f), Size(H * 0.022f, H * 0.045f)); xl += H * 0.05f }
        }
        drawRect(barniz, Offset(cx - H * 0.75f, pie.y - H * 1.015f), Size(H * 0.2f, H * 0.045f))
        drawRect(pc, Offset(cx - H * 0.75f, pie.y - H * 1.015f), Size(H * 0.2f, H * 0.012f))
        drawRect(claro(oscuro, 0.4f), Offset(labio.left, labio.top), Size(labio.width, H * 0.008f))
        if (oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.55f * cerca), Offset(labio.left, labio.top), Size(labio.width * 0.5f, H * 0.008f))
        // El desconchón del labio, donde asoma la arcilla, y una rozadura en el vientre.
        drawPath(Path().apply { moveTo(cx - H * 0.4f, labio.top); lineTo(cx - H * 0.3f, labio.top); lineTo(cx - H * 0.33f, labio.top + H * 0.035f); lineTo(cx - H * 0.38f, labio.top + H * 0.03f); close() }, luz(TERRACOTA, oscuro, cerca))
        drawLine(claro(oscuro, 0.3f), Offset(cx - H * 0.5f, pie.y - H * 0.3f), Offset(cx - H * 0.36f, pie.y - H * 0.26f), 0.8f * u)
    }

    /** El pínax: una tablilla de barro claro apoyada en el zócalo, con su orla de barniz y los agujeros para colgarla (la placa de «y N más»). */
    private fun DrawScope.pinax(r: Rect, oscuro: Boolean, caida: Offset) {
        val u = density
        drawRoundRect(sombra(oscuro, if (oscuro) 0.45f else 0.24f), r.topLeft + caida, r.size, CornerRadius(2 * u))
        sombraContacto(Offset(r.center.x + 3 * u, r.bottom + 2 * u), r.width * 1.05f, 4 * u, if (oscuro) 0.5f else 0.35f)
        val barro = if (oscuro) mezcla(mezcla(mezcla(LANA, ARCILLA, 0.4f), NOCHE, 0.24f), FILO_MUEBLE, 0.06f) else mezcla(LANA, ARCILLA, 0.4f)
        drawRect(mezcla(barro, SOMBRA_MUEBLE, 0.35f), Offset(r.left + 1 * u, r.bottom), Size(r.width, 2.4f * u))
        drawRoundRect(barro, r.topLeft, r.size, CornerRadius(2 * u))
        val orla = Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.bottom - 3 * u)
        drawRect(BARNIZ.copy(alpha = 0.8f), orla.topLeft, orla.size, style = Stroke(0.9f * u))
        listOf(r.left + 6 * u, r.right - 6 * u).forEach { x -> drawCircle(mezcla(barro, BARNIZ, 0.6f), 1.2f * u, Offset(x, r.top + 1.8f * u)) }
        // Una esquina rota.
        drawPath(Path().apply { moveTo(r.right - 7 * u, r.bottom); lineTo(r.right, r.bottom - 5 * u); lineTo(r.right, r.bottom); close() }, sombra(oscuro, 0.5f))
        drawRect(claro(oscuro, 0.35f), r.topLeft, Size(r.width, 1f * u))
    }

    // ---------------------------------------------------------------- los compartimentos: una vasija por tema

    /** El contorno de la vasija de cada [tipo] (sin boca, asas ni pie), en un compartimento de [w] × [h]. */
    private fun cuerpo(tipo: Int, cx: Float, w: Float, h: Float): Path = when (tipo) {
        // El ánfora de cuello: cuello alto, hombro redondo, vientre ancho que baja al pie.
        0 -> perfil(cx, 0f, w, h, floatArrayOf(0.105f, 0.085f, 0.098f, 0.15f, 0.105f, 0.205f, 0.2f, 0.255f, 0.34f, 0.32f, 0.415f, 0.42f, 0.425f, 0.52f, 0.39f, 0.63f, 0.3f, 0.75f, 0.19f, 0.84f, 0.12f, 0.9f, 0.11f, 0.915f))
        // La hidria: cuello corto, hombro marcado y casi plano, vientre que se angosta.
        1 -> perfil(cx, 0f, w, h, floatArrayOf(0.12f, 0.13f, 0.11f, 0.175f, 0.14f, 0.22f, 0.3f, 0.252f, 0.405f, 0.3f, 0.432f, 0.38f, 0.428f, 0.5f, 0.39f, 0.62f, 0.31f, 0.74f, 0.2f, 0.84f, 0.13f, 0.9f, 0.12f, 0.915f))
        // El estamnos: bajo y ancho, de hombro alto, con su tapa.
        else -> perfil(cx, 0f, w, h, floatArrayOf(0.15f, 0.33f, 0.152f, 0.352f, 0.3f, 0.378f, 0.41f, 0.42f, 0.448f, 0.5f, 0.438f, 0.59f, 0.38f, 0.7f, 0.28f, 0.8f, 0.17f, 0.875f, 0.125f, 0.905f, 0.12f, 0.915f))
    }

    /** Dónde está la boca de la vasija (por donde salen los óstraka al abrirla), en fracción del alto. */
    private fun boca(tipo: Int) = when (tipo) { 0 -> 0.06f; 1 -> 0.11f; else -> 0.3f }

    /**
     * La vasija de un tema: el asa de atrás (la hidria), el cuerpo con su decoración de figuras negras o de barniz negro y su
     * zona de arcilla para el rótulo, el volumen, el desconchón y la rozadura, la boca, las asas y el pie. [liso]: la de lo que
     * no tiene tema, de arcilla sin pintar.
     */
    private fun DrawScope.vasija(tipo: Int, liso: Boolean, cx: Float, pie: Float, semilla: Int, ab: Float, oscuro: Boolean, k: Float, haciaX: Float) {
        val u = density
        val w = size.width; val h = size.height
        val barniz = if (liso) luz(mezcla(TERRACOTA, ARCILLA, 0.3f), oscuro, k) else if (oscuro) mezcla(BARNIZ, NOCHE, 0.15f) else BARNIZ
        val arcilla = luz(TERRACOTA, oscuro, k)
        val pc = panel(oscuro, k)
        val purpura = luz(PURPURA, oscuro, k)
        val linea = if (liso) luz(TERRA_OSC, oscuro, k) else barniz
        val forma = cuerpo(tipo, cx, w, h)
        val asas = listOf(-1f, 1f).map { s ->
            when (tipo) {
                0 -> Path().apply { moveTo(cx + s * w * 0.1f, h * 0.11f); cubicTo(cx + s * w * 0.33f, h * 0.08f, cx + s * w * 0.37f, h * 0.2f, cx + s * w * 0.3f, h * 0.3f) }
                1 -> Path().apply { moveTo(cx + s * w * 0.4f, h * 0.33f); cubicTo(cx + s * w * 0.5f, h * 0.335f, cx + s * w * 0.5f, h * 0.41f, cx + s * w * 0.415f, h * 0.42f) }
                else -> Path().apply { moveTo(cx + s * w * 0.4f, h * 0.455f); cubicTo(cx + s * w * 0.5f, h * 0.46f, cx + s * w * 0.51f, h * 0.39f, cx + s * w * 0.45f, h * 0.375f) }
            }
        }
        val anchoAsa = w * (if (tipo == 0) 0.045f else 0.036f)
        val asaAtras = Path().apply { moveTo(cx + w * 0.25f, h * 0.255f); cubicTo(cx + w * 0.42f, h * 0.18f, cx + w * 0.3f, h * 0.08f, cx + w * 0.12f, h * 0.14f) }
        // El aro que tapa el aura en reposo, por fuera de toda la silueta (cuerpo, pie, boca, asas y tapa).
        val pieP = Path().apply {
            moveTo(cx - w * 0.11f, h * 0.905f); lineTo(cx + w * 0.11f, h * 0.905f); lineTo(cx + w * 0.1f, h * 0.935f)
            lineTo(cx + w * 0.165f, pie - w * 0.03f); lineTo(cx + w * 0.17f, pie); lineTo(cx - w * 0.17f, pie); lineTo(cx - w * 0.165f, pie - w * 0.03f); lineTo(cx - w * 0.1f, h * 0.935f); close()
        }
        val labio = Path().apply {
            when (tipo) {
                0 -> addRoundRect(androidx.compose.ui.geometry.RoundRect(cx - w * 0.15f, h * 0.05f, cx + w * 0.15f, h * 0.09f, CornerRadius(w * 0.03f)))
                1 -> { moveTo(cx - w * 0.12f, h * 0.135f); lineTo(cx - w * 0.2f, h * 0.104f); lineTo(cx + w * 0.2f, h * 0.104f); lineTo(cx + w * 0.12f, h * 0.135f); close() }
                else -> if (ab == 0f) {
                    addArc(Rect(cx - w * 0.17f, h * 0.29f, cx + w * 0.17f, h * 0.37f), 180f, 180f); close()
                    addOval(Rect(cx - w * 0.06f, h * 0.252f, cx + w * 0.06f, h * 0.27f))
                    addRect(Rect(cx - w * 0.022f, h * 0.262f, cx + w * 0.022f, h * 0.292f))
                }
            }
        }
        // El aro que tapa el aura en reposo, por fuera de toda la silueta (cuerpo, pie, boca, asas y tapa).
        cubreAura(oscuro, listOf(forma, pieP, labio), asas.map { it to anchoAsa } + (if (tipo == 1) listOf(asaAtras to w * 0.045f) else emptyList()))
        // El asa de atrás de la hidria, que sube del hombro al labio.
        if (tipo == 1) {
            val asa = asaAtras
            drawPath(asa, barniz, style = Stroke(w * 0.045f, cap = StrokeCap.Round))
            drawPath(corrido(asa, -w * 0.01f, 0f), claro(oscuro, 0.3f), style = Stroke(w * 0.01f, cap = StrokeCap.Round))
        }
        // El pie anular: el tallo, el disco y la arcilla reservada del canto.
        drawPath(pieP, barniz)
        drawRect(if (liso) luz(TERRA_OSC, oscuro, k) else arcilla, Offset(cx - w * 0.17f, pie - w * 0.016f), Size(w * 0.34f, w * 0.016f))
        drawPath(borde(pieP, w * 0.04f, 0f), claro(oscuro, 0.2f))
        drawPath(borde(pieP, -w * 0.05f, 0f), sombra(oscuro, 0.3f))
        // El cuerpo y su decoración.
        drawPath(forma, if (liso) pc else barniz)
        clipPath(forma) {
            if (liso) {
                // Arcilla sin pintar: las marcas del torno y una chorrera del engobe.
                for (j in 0 until 7) {
                    val y = h * (0.2f + 0.1f * j + 0.03f * azar(semilla, j + 60))
                    if (y in h * 0.45f..h * 0.69f) continue
                    drawLine(luz(TERRA_OSC, oscuro, k).copy(alpha = 0.22f), Offset(0f, y), Offset(w, y), 0.6f * u)
                }
                drawLine(luz(TERRA_OSC, oscuro, k).copy(alpha = 0.18f), Offset(cx + w * 0.25f, h * 0.3f), Offset(cx + w * 0.26f, h * 0.42f), 1.6f * u, StrokeCap.Round)
                drawRect(luz(TERRA_OSC, oscuro, k).copy(alpha = 0.5f), Offset(0f, h * 0.88f), Size(w, h * 0.006f))
            } else when (tipo) {
                0 -> {
                    // El hombro: lengüetas de barniz y de púrpura sobre arcilla.
                    drawRect(arcilla, Offset(0f, h * 0.25f), Size(w, h * 0.065f))
                    var x = cx - w * 0.46f; var j = 0
                    while (x < cx + w * 0.46f) {
                        drawRoundRect(if (j % 2 == 0) barniz else purpura, Offset(x, h * 0.25f), Size(w * 0.042f, h * 0.055f), CornerRadius(w * 0.02f))
                        x += w * 0.062f; j++
                    }
                    drawRect(barniz, Offset(0f, h * 0.312f), Size(w, h * 0.006f))
                    // El panel reservado: arriba, una cadena de palmetas y capullos de loto.
                    val p = Rect(cx - w * 0.34f, h * 0.395f, cx + w * 0.34f, h * 0.705f)
                    drawRect(pc, p.topLeft, p.size)
                    for (q in 0 until 5) {
                        val x0 = p.left + w * 0.07f + q * w * 0.135f
                        for (a in -2..2) {
                            val ang = Math.toRadians(-90.0 + a * 24.0)
                            drawLine(barniz, Offset(x0, h * 0.448f), Offset(x0 + w * 0.032f * cos(ang).toFloat(), h * 0.448f + h * 0.034f * sin(ang).toFloat()), 1f * u, StrokeCap.Round)
                        }
                        if (q < 4) drawPath(Path().apply { moveTo(x0 + w * 0.05f, h * 0.45f); lineTo(x0 + w * 0.067f, h * 0.41f); lineTo(x0 + w * 0.084f, h * 0.45f); close() }, barniz)
                    }
                    drawRect(barniz, Offset(p.left, h * 0.452f), Size(p.width, h * 0.004f))
                    drawRect(barniz, Offset(p.left, h * 0.69f), Size(p.width, h * 0.004f))
                    drawRect(purpura, Offset(0f, h * 0.735f), Size(w, h * 0.005f))
                    // Los rayos que suben del pie.
                    drawRect(arcilla, Offset(0f, h * 0.76f), Size(w, h * 0.1f))
                    var xr = cx - w * 0.42f
                    while (xr < cx + w * 0.42f) {
                        drawPath(Path().apply { moveTo(xr - w * 0.022f, h * 0.86f); lineTo(xr, h * 0.768f); lineTo(xr + w * 0.022f, h * 0.86f); close() }, barniz)
                        xr += w * 0.065f
                    }
                    drawRect(purpura, Offset(0f, h * 0.872f), Size(w, h * 0.005f))
                }
                1 -> {
                    // El hombro con un meandro; el panel con una rama de hiedra arriba.
                    drawRect(arcilla, Offset(0f, h * 0.232f), Size(w, h * 0.072f))
                    greca(Rect(cx - w * 0.5f, h * 0.247f, cx + w * 0.5f, h * 0.29f), barniz)
                    val p = Rect(cx - w * 0.345f, h * 0.39f, cx + w * 0.345f, h * 0.7f)
                    drawRect(pc, p.topLeft, p.size)
                    val yv = h * 0.42f
                    val tallo = Path().apply {
                        moveTo(p.left + w * 0.03f, yv)
                        var x = p.left + w * 0.03f; var j = 0
                        while (x < p.right - w * 0.05f) { val s = if (j % 2 == 0) 1f else -1f; cubicTo(x + w * 0.03f, yv - s * h * 0.014f, x + w * 0.06f, yv - s * h * 0.014f, x + w * 0.09f, yv); x += w * 0.09f; j++ }
                    }
                    drawPath(tallo, barniz, style = Stroke(0.9f * u))
                    var xh = p.left + w * 0.06f; var j = 0
                    while (xh < p.right - w * 0.04f) {
                        val ly = yv + (if (j % 2 == 0) -1f else 1f) * h * 0.016f
                        drawPath(Path().apply { moveTo(xh, ly + h * 0.012f); cubicTo(xh - w * 0.03f, ly, xh - w * 0.012f, ly - h * 0.014f, xh, ly - h * 0.005f); cubicTo(xh + w * 0.012f, ly - h * 0.014f, xh + w * 0.03f, ly, xh, ly + h * 0.012f); close() }, barniz)
                        if (j % 3 == 1) drawCircle(barniz, w * 0.008f, Offset(xh + w * 0.04f, yv + h * 0.012f))
                        xh += w * 0.09f; j++
                    }
                    drawRect(barniz, Offset(p.left, h * 0.448f), Size(p.width, h * 0.004f))
                    drawRect(barniz, Offset(p.left, h * 0.688f), Size(p.width, h * 0.004f))
                    drawRect(arcilla, Offset(0f, h * 0.765f), Size(w, h * 0.012f))
                    drawRect(purpura, Offset(0f, h * 0.36f), Size(w, h * 0.005f))
                }
                else -> {
                    // Barniz negro: dos filetes en el hombro, las ovas y la franja de arcilla del vientre.
                    drawRect(arcilla, Offset(0f, h * 0.392f), Size(w, h * 0.004f))
                    drawRect(arcilla, Offset(0f, h * 0.405f), Size(w, h * 0.004f))
                    drawRect(arcilla, Offset(0f, h * 0.422f), Size(w, h * 0.034f))
                    var x = cx - w * 0.5f
                    while (x < cx + w * 0.5f) {
                        drawOval(barniz, Offset(x, h * 0.425f), Size(w * 0.04f, h * 0.026f))
                        drawLine(barniz, Offset(x + w * 0.05f, h * 0.425f), Offset(x + w * 0.05f, h * 0.451f), 0.8f * u)
                        x += w * 0.06f
                    }
                    drawRect(pc, Offset(0f, h * 0.456f), Size(w, h * 0.236f))
                    drawRect(barniz, Offset(0f, h * 0.674f), Size(w, h * 0.004f))
                    drawRect(barniz, Offset(0f, h * 0.684f), Size(w, h * 0.004f))
                    drawRect(arcilla, Offset(0f, h * 0.78f), Size(w, h * 0.006f))
                }
            }
            // El desconchón (asoma la arcilla) y la rozadura, cada vasija en su sitio.
            if (!liso) {
                val dx = if (azar(semilla, 70) < 0.5f) -1f else 1f
                val dy = h * (0.3f + 0.5f * azar(semilla, 71))
                val dX = cx + dx * w * (0.3f + 0.08f * azar(semilla, 72))
                if (dy !in h * 0.39f..h * 0.71f) drawPath(Path().apply {
                    moveTo(dX, dy); lineTo(dX + w * 0.04f, dy - h * 0.008f); lineTo(dX + w * 0.05f, dy + h * 0.012f); lineTo(dX + w * 0.01f, dy + h * 0.02f); close()
                }, arcilla)
                val ry = h * (0.22f + 0.12f * azar(semilla, 73))
                drawLine(claro(oscuro, 0.4f).copy(alpha = if (oscuro) 0.15f else 0.4f), Offset(cx + w * 0.06f, ry), Offset(cx + w * 0.22f, ry + h * 0.02f), 0.7f * u, StrokeCap.Round)
            }
        }
        // De noche la sombra propia va más suave: pasa por encima de la zona del rótulo y el nombre tiene que leerse.
        redondo(forma, oscuro, k, haciaX, brillo = if (liso) 0.12f else 0.32f, fuerza = if (oscuro) 0.35f + 0.75f * k else 1f,
            rotuloEn = Rect(w * rotulo.left - 3 * u, h * rotulo.top, w * rotulo.right + 3 * u, h * rotulo.bottom))
        // La boca: el labio y la oscuridad de dentro; la tapa del estamnos, que se levanta al abrirla.
        when (tipo) {
            0 -> {
                drawRoundRect(barniz, Offset(cx - w * 0.15f, h * 0.05f), Size(w * 0.3f, h * 0.04f), CornerRadius(w * 0.03f))
                drawOval(luz(mezcla(BARNIZ, NOCHE, 0.4f), oscuro, k), Offset(cx - w * 0.11f, h * 0.044f), Size(w * 0.22f, h * 0.018f))
                drawLine(claro(oscuro, 0.4f), Offset(cx - w * 0.13f, h * 0.06f), Offset(cx - w * 0.02f, h * 0.06f), 0.8f * u, StrokeCap.Round)
            }
            1 -> {
                drawPath(Path().apply { moveTo(cx - w * 0.12f, h * 0.135f); lineTo(cx - w * 0.2f, h * 0.112f); lineTo(cx + w * 0.2f, h * 0.112f); lineTo(cx + w * 0.12f, h * 0.135f); close() }, barniz)
                drawRoundRect(barniz, Offset(cx - w * 0.2f, h * 0.104f), Size(w * 0.4f, h * 0.016f), CornerRadius(w * 0.02f))
                drawOval(luz(mezcla(BARNIZ, NOCHE, 0.4f), oscuro, k), Offset(cx - w * 0.15f, h * 0.099f), Size(w * 0.3f, h * 0.014f))
                drawLine(claro(oscuro, 0.4f), Offset(cx - w * 0.18f, h * 0.11f), Offset(cx - w * 0.04f, h * 0.11f), 0.8f * u, StrokeCap.Round)
            }
            else -> {
                drawOval(luz(mezcla(BARNIZ, NOCHE, 0.4f), oscuro, k), Offset(cx - w * 0.15f, h * 0.322f), Size(w * 0.3f, h * 0.02f))
                val alza = ab * h * 0.07f
                rotate(-ab * 12f, Offset(cx - w * 0.16f, h * 0.33f)) {
                    translate(0f, -alza) {
                        val tapa = Path().apply { addArc(Rect(cx - w * 0.17f, h * 0.29f, cx + w * 0.17f, h * 0.37f), 180f, 180f); close() }
                        drawPath(tapa, barniz)
                        drawArc(arcilla, 200f, 140f, false, Offset(cx - w * 0.12f, h * 0.302f), Size(w * 0.24f, h * 0.05f), style = Stroke(0.8f * u))
                        redondo(tapa, oscuro, k, haciaX, brillo = 0.3f)
                        drawRect(barniz, Offset(cx - w * 0.022f, h * 0.262f), Size(w * 0.044f, h * 0.03f))
                        drawOval(barniz, Offset(cx - w * 0.06f, h * 0.252f), Size(w * 0.12f, h * 0.018f))
                        drawOval(claro(oscuro, 0.4f), Offset(cx - w * 0.05f, h * 0.253f), Size(w * 0.05f, h * 0.008f))
                    }
                }
            }
        }
        // Las asas: del cuello al hombro en el ánfora; las laterales, horizontales, en la hidria y el estamnos.
        for ((i, s) in listOf(-1f, 1f).withIndex()) {
            val asa = asas[i]
            drawPath(asa, barniz, style = Stroke(anchoAsa, cap = StrokeCap.Round))
            if (!oscuro && s < 0) drawPath(corrido(asa, -w * 0.008f, -w * 0.004f), luzMueble(0.35f), style = Stroke(w * 0.01f, cap = StrokeCap.Round))
            else if (oscuro && s == haciaX) drawPath(corrido(asa, s * w * 0.008f, -w * 0.004f), FILO_MUEBLE.copy(alpha = 0.75f * k), style = Stroke(w * 0.01f, cap = StrokeCap.Round))
        }
    }

    /** La lekane: un cuenco bajo de dos asas, con el labio de barniz, y los óstraka asomando ([k]). */
    private fun DrawScope.lekane(c: Offset, ancho: Float, k: Int, semilla: Int, oscuro: Boolean, cerca: Float, haciaX: Float) {
        val u = density
        val rx = ancho / 2; val ry = ancho * 0.1f
        val boca = Rect(c.x - rx, c.y - ancho * 0.24f - ry, c.x + rx, c.y - ancho * 0.24f + ry)
        contacto(Offset(c.x + 2 * u, c.y), ancho * 0.85f, ry * 1.2f, if (oscuro) 0.5f else 0.32f, oscuro)
        cubreAura(oscuro, listOf(Path().apply { addOval(Rect(boca.left - ancho * 0.06f, boca.top - ancho * 0.1f, boca.right + ancho * 0.06f, c.y)) }))
        drawOval(luz(mezcla(TERRA_OSC, NOCHE, 0.4f), oscuro, cerca), boca.topLeft, boca.size)
        // Los óstraka de dentro, de canto, asomando por encima del labio.
        for (i in 0 until k) {
            val f = if (k == 1) 0.5f else i / (k - 1f)
            val x = boca.left + boca.width * (0.18f + 0.64f * f)
            val y = boca.center.y - ancho * (0.02f + 0.06f * azar(semilla, i + 80))
            tiesto(Offset(x, y), ancho * (0.1f + 0.03f * azar(semilla, i + 81)), semilla + i * 5, oscuro, cerca, aura = true)
        }
        val frente = Path().apply {
            moveTo(boca.left, boca.center.y)
            cubicTo(boca.left + rx * 0.05f, c.y - ancho * 0.05f, c.x - rx * 0.5f, c.y, c.x, c.y)
            cubicTo(c.x + rx * 0.5f, c.y, boca.right - rx * 0.05f, c.y - ancho * 0.05f, boca.right, boca.center.y)
            arcTo(boca, 0f, 180f, false)
            close()
        }
        drawPath(frente, luz(TERRACOTA, oscuro, cerca))
        clipPath(frente) { drawRect(luz(BARNIZ, oscuro, cerca), Offset(boca.left, boca.top), Size(ancho, ry * 2.6f)) }
        redondo(frente, oscuro, cerca, haciaX, brillo = 0.2f)
        for (s in listOf(-1f, 1f)) {
            val asa = Rect(c.x + s * rx - ancho * 0.07f, boca.center.y - ancho * 0.03f, c.x + s * rx + ancho * 0.07f, boca.center.y + ancho * 0.06f)
            drawArc(luz(BARNIZ, oscuro, cerca), if (s < 0) 90f else -90f, 180f, false, asa.topLeft, asa.size, style = Stroke(ancho * 0.035f))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El pie de la vasija, sobre el tablón (su cara de arriba la pinta el mueble al fondo de cada hueco).
        val pie = h - w * 0.022f
        val repisa = h - w * 0.048f
        // La luz de la lucerna en este hueco: cuánta llega y de qué lado (la llama está arriba a la derecha del mueble).
        val luzH = luzDelHueco(c)
        val k = luzH?.first ?: CERCA_VASIJA
        val lejos = luzH?.second ?: Offset(-1f, 1f)
        val haciaX = if (lejos.x > 0f) -1f else 1f
        val fuera = lejos / lejos.getDistance().coerceAtLeast(0.0001f)
        if (c.vacio) { vacio(c.semilla, pie, repisa, oscuro, k, haciaX, fuera); return }
        val mezclada = (c.semilla xor (c.semilla ushr 3)) ushr 1
        val tipo = mezclada % 3
        val lado = if ((mezclada ushr 1) % 2 == 0) -1f else 1f
        val cx = w * (0.5f - lado * 0.025f)
        val forma = cuerpo(tipo, cx, w, h)
        // La sombra de la vasija en la pared (con SrcAtop, ver [contacto]): de día, corta a la derecha; de noche, larga hacia
        // fuera de la llama, de borde firme y honda cerca de ella, y nada donde la luz ya no llega.
        clipRect(-w, -h, w * 2, repisa) {
            if (!oscuro) drawPath(corrido(forma, w * 0.055f, h * 0.01f), sombra(false, 0.14f), blendMode = BlendMode.SrcAtop)
            else if (k > 0.08f) drawPath(corrido(forma, fuera.x * w * 0.2f, fuera.y * w * 0.2f), NOCHE.copy(alpha = (0.75f * k).coerceAtMost(0.62f)), blendMode = BlendMode.SrcAtop)
        }
        contacto(Offset(cx + 2 * u, pie), w * 0.42f, w * 0.06f, if (oscuro) 0.3f + 0.4f * k else 0.34f, oscuro)
        vasija(tipo, c.sinTema, cx, pie, c.semilla, ab, oscuro, k, haciaX)
        // Los óstraka: pocos, sueltos al pie; muchos, en una lekane al lado; más, también amontonados delante.
        val n = c.n
        if (n in 1..3) {
            for (i in 0 until n) {
                val x = cx + (if (i % 2 == 0) lado else -lado) * w * (0.26f + 0.1f * azar(c.semilla, i + 90)) - lado * i * w * 0.03f
                tiesto(Offset(x, pie - w * 0.006f * i), w * (0.055f + 0.02f * azar(c.semilla, i + 91)), c.semilla + i * 3, oscuro, k, aura = true)
            }
        } else if (n >= 4) {
            val bx = (cx + lado * w * 0.3f).coerceIn(w * 0.17f, w * 0.83f)
            if (n >= 8) for (i in 0 until 3) tiesto(Offset(cx - lado * w * (0.22f + 0.08f * i), pie - w * 0.004f * i), w * 0.055f, c.semilla + 40 + i, oscuro, k, aura = true)
            lekane(Offset(bx, pie + w * 0.004f), w * 0.34f, (n - 2).coerceAtMost(6), c.semilla + 7, oscuro, k, haciaX)
            tiesto(Offset(bx - lado * w * 0.2f, pie), w * 0.045f, c.semilla + 31, oscuro, k, aura = true)
        }
        // Al abrirla, los óstraka suben por la boca y sale un poco de luz.
        if (ab > 0f) {
            val by = h * boca(tipo)
            mancha(Offset(cx, by), w * 0.4f, if (oscuro) FILO_MUEBLE.copy(alpha = 0.25f * ab) else luzMueble(0.35f * ab))
            for (i in 0 until 3) tiesto(Offset(cx + w * (-0.06f + 0.06f * i), by - ab * h * (0.06f + 0.03f * i)), w * 0.06f, c.semilla + 50 + i, oscuro, maxOf(k, 0.6f), aura = true)
        }
    }

    /** Un hueco sin tema: el anillo de madera vacío con el cerco que dejó la vasija, o un cesto con un paño doblado. */
    private fun DrawScope.vacio(semilla: Int, pie: Float, repisa: Float, oscuro: Boolean, k: Float, haciaX: Float, fuera: Offset) {
        val u = density
        val w = size.width; val h = size.height
        if (Math.floorMod(semilla / 7919, 2) == 0) {
            // El cerco que dejó la vasija en el tablón, y el anillo de madera donde se paraba, corrido a un lado.
            drawOval(luz(mezcla(MADERA_OSC, TERRA_OSC, 0.5f), oscuro, k).copy(alpha = 0.45f), Offset(w * 0.52f, pie - w * 0.02f), Size(w * 0.32f, w * 0.04f), style = Stroke(1.6f * u))
            val c = Offset(w * 0.38f, pie - w * 0.01f)
            val rx = w * 0.22f; val ry = w * 0.055f; val alto = w * 0.075f
            contacto(Offset(c.x + 2 * u, pie), rx * 2.1f, ry, if (oscuro) 0.5f else 0.32f, oscuro)
            val mad = luz(MADERA, oscuro, k)
            drawRect(mad, Offset(c.x - rx, c.y - alto), Size(rx * 2, alto))
            drawOval(mad, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
            drawRect(sombra(oscuro, 0.3f), Offset(c.x + rx * 0.5f, c.y - alto), Size(rx * 0.5f, alto))
            drawRect(claro(oscuro, 0.25f), Offset(c.x - rx, c.y - alto), Size(rx * 0.3f, alto))
            drawOval(luz(mezcla(MADERA, LUZ_MUEBLE, 0.25f), oscuro, k), Offset(c.x - rx, c.y - alto - ry), Size(rx * 2, ry * 2))
            drawOval(luz(mezcla(MADERA_OSC, NOCHE, 0.3f), oscuro, k), Offset(c.x - rx * 0.62f, c.y - alto - ry * 0.62f), Size(rx * 1.24f, ry * 1.24f))
            drawOval(sombra(oscuro, 0.3f), Offset(c.x - rx * 0.62f, c.y - alto - ry * 0.62f), Size(rx * 1.24f, ry * 0.6f))
            // Un tiesto que se quedó.
            tiesto(Offset(w * 0.78f, pie - w * 0.004f), w * 0.05f, semilla + 3, oscuro, k)
        } else {
            // Un cesto de mimbre con un paño de lana doblado encima.
            val cx = w * 0.48f
            val ancho = w * 0.62f; val alto = h * 0.2f
            val boca = pie - alto
            val cesto = Path().apply {
                moveTo(cx - ancho * 0.42f, pie); lineTo(cx + ancho * 0.42f, pie)
                cubicTo(cx + ancho * 0.5f, pie - alto * 0.3f, cx + ancho * 0.52f, boca + alto * 0.2f, cx + ancho * 0.5f, boca)
                lineTo(cx - ancho * 0.5f, boca)
                cubicTo(cx - ancho * 0.52f, boca + alto * 0.2f, cx - ancho * 0.5f, pie - alto * 0.3f, cx - ancho * 0.42f, pie)
                close()
            }
            clipRect(-w, -h, w * 2, repisa) {
                if (!oscuro) drawPath(corrido(cesto, w * 0.06f, h * 0.01f), sombra(false, 0.15f), blendMode = BlendMode.SrcAtop)
                else if (k > 0.08f) drawPath(corrido(cesto, fuera.x * w * 0.2f, fuera.y * w * 0.2f), NOCHE.copy(alpha = (0.75f * k).coerceAtMost(0.62f)), blendMode = BlendMode.SrcAtop)
            }
            contacto(Offset(cx + 2 * u, pie), ancho, w * 0.05f, if (oscuro) 0.5f else 0.32f, oscuro)
            drawPath(cesto, luz(mezcla(MADERA, LANA, 0.45f), oscuro, k))
            clipPath(cesto) {
                var y = boca + 1.5f * u; var j = 0
                while (y < pie) {
                    val paso = 4.4f * u
                    var x = cx - ancho * 0.6f + (if (j % 2 == 0) 0f else paso / 2)
                    while (x < cx + ancho * 0.6f) { drawOval(luz(mezcla(MADERA, LANA, 0.25f), oscuro, k), Offset(x, y - 1f * u), Size(paso * 0.7f, 2.2f * u)); x += paso }
                    y += 2.8f * u; j++
                }
            }
            redondo(cesto, oscuro, k, haciaX)
            // El paño, doblado sobre la boca y colgando por un lado, con su orilla púrpura.
            val pano = Path().apply {
                moveTo(cx - ancho * 0.5f, boca + 1 * u)
                cubicTo(cx - ancho * 0.3f, boca - alto * 0.3f, cx + ancho * 0.1f, boca - alto * 0.25f, cx + ancho * 0.3f, boca - alto * 0.05f)
                lineTo(cx + ancho * 0.34f, boca + alto * 0.55f); lineTo(cx + ancho * 0.24f, boca + alto * 0.5f); lineTo(cx + ancho * 0.14f, boca + alto * 0.58f)
                lineTo(cx + ancho * 0.1f, boca + alto * 0.08f)
                cubicTo(cx - ancho * 0.1f, boca + alto * 0.02f, cx - ancho * 0.3f, boca + alto * 0.06f, cx - ancho * 0.5f, boca + 1 * u)
                close()
            }
            drawPath(corrido(pano, 1.5f * u, 1.5f * u), sombra(oscuro, 0.3f))
            drawPath(pano, luz(LANA, oscuro, k))
            clipPath(pano) {
                drawLine(luz(PURPURA, oscuro, k), Offset(cx + ancho * 0.1f, boca + alto * 0.46f), Offset(cx + ancho * 0.4f, boca + alto * 0.48f), alto * 0.07f)
                drawLine(sombra(oscuro, 0.25f), Offset(cx + ancho * 0.2f, boca), Offset(cx + ancho * 0.22f, boca + alto * 0.55f), 2f * u)
            }
            redondo(pano, oscuro, k, haciaX)
            drawLine(luz(mezcla(MADERA, MADERA_OSC, 0.3f), oscuro, k), Offset(cx - ancho * 0.5f, boca), Offset(cx - ancho * 0.1f, boca + alto * 0.02f), 2.4f * u, StrokeCap.Round)
        }
    }

    override val rotulo = Rect(0.14f, 0.465f, 0.86f, 0.665f)
    override fun tintaRotulo(oscuro: Boolean) = BARNIZ
    override val letraRotulo: FontFamily = Letra.garamond
    override val salida = 0f
    /** El pínax apoyado en el zócalo, entre el cofre y la crátera. */
    override val placa = Rect(0.4f, 0.43f, 0.66f, 0.79f)
    override fun tintaPlaca(oscuro: Boolean) = BARNIZ

    // ---------------------------------------------------------------- el interior y las fichas (no cambian)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un mosaico de guijarros, como los de Pela: fondo oscuro, guijarros sueltos y una greca de guijarros claros alrededor.
        drawRect(t(Color(0xFF2E2621), oscuro))
        val paso = 9 * u
        var y = paso / 2
        var fila = 0
        while (y < h) {
            var x = paso / 2 + (if (fila % 2 == 0) 0f else paso / 2)
            while (x < w) {
                val k = (x / paso).toInt() * 31 + fila
                val c = mezcla(Color(0xFF3A302A), Color(0xFF51463C), azar(61, k))
                drawOval(t(c, oscuro), Offset(x - paso * 0.42f, y - paso * 0.34f), Size(paso * 0.84f, paso * 0.68f))
                x += paso
            }
            y += paso * 0.86f; fila++
        }
        val borde = 22 * u
        val claro = t(GUIJARRO, oscuro)
        val efecto = PathEffect.dashPathEffect(floatArrayOf(0.1f, 4.2f * u))
        fun linea(p: Path) = drawPath(p, claro, style = Stroke(3.4f * u, cap = StrokeCap.Round, pathEffect = efecto))
        linea(Path().apply { addRect(Rect(6 * u, 6 * u, w - 6 * u, h - 6 * u)) })
        linea(Path().apply { addRect(Rect(borde, borde, w - borde, h - borde)) })
        // La greca, arriba y abajo, entre los dos marcos.
        listOf(6 * u, h - borde).forEach { y0 ->
            val a = borde - 6 * u; var x = 6 * u
            val p = Path()
            while (x < w - 6 * u - a) {
                p.moveTo(x, y0 + a * 0.85f); p.lineTo(x, y0 + a * 0.2f); p.lineTo(x + a * 0.7f, y0 + a * 0.2f); p.lineTo(x + a * 0.7f, y0 + a * 0.6f); p.lineTo(x + a * 0.35f, y0 + a * 0.6f)
                x += a
            }
            linea(p)
        }
        // Al centro, una roseta de guijarros, muy tenue.
        val c = Offset(w / 2, h / 2)
        for (k in 0 until 16) {
            val a = 6.283f * k / 16
            drawLine(claro.copy(alpha = 0.12f), c, Offset(c.x + w * 0.3f * kotlin.math.cos(a), c.y + w * 0.3f * kotlin.math.sin(a)), 3 * u, StrokeCap.Round)
        }
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFEFE6D2)
    override val pared: Dp = 24.dp
    override val columnas = 2
    override val lineas = 4

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoTiesto(Rect(0f, 0f, s.width, s.height), semilla)) }

    /** El contorno del tiesto. Si la hoja es muy alta (el escritor), lleva más vértices: con 9 las esquinas se cortarían en diagonales larguísimas. */
    private fun contornoTiesto(r: Rect, semilla: Int) = contornoIrregular(r, semilla, if (r.height > r.width * 1.6f) 24 else 9, 0.035f)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.8f else 1f
        // El barro: el papel teñido de terracota, más oscuro en un borde.
        drawRect(TERRACOTA.copy(alpha = 0.28f))
        drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, TERRA_OSC.copy(alpha = 0.2f)), Offset.Zero, Offset(w, h)))
        // El barniz negro de la cara de afuera, en el borde de abajo, y el canto roto más claro.
        drawRect(BARNIZ.copy(alpha = 0.75f), Offset(0f, h - 6 * u * g), Size(w, 6 * u * g))
        drawPath(contornoTiesto(Rect(0f, 0f, w, h), semilla), TERRA_LUZ.copy(alpha = 0.6f), style = Stroke(2.2f * u))
        // Rayones incisos y poros del barro.
        for (k in 0 until 2) {
            val y = h * (0.25f + 0.5f * azar(semilla, k + 70))
            drawLine(Color.White.copy(alpha = 0.25f), Offset(w * 0.1f, y), Offset(w * (0.4f + 0.4f * azar(semilla, k + 80)), y + 3 * u), 0.7f * u)
        }
        for (k in 0 until (if (grande) 70 else 24)) drawCircle(TERRA_OSC.copy(alpha = 0.2f), (0.5f + azar(semilla, k + 200)) * u, Offset(w * azar(semilla, k + 300), h * azar(semilla, k + 400)))
    }

    override val margenes = MargenesFicha(12.dp, 18.dp, 16.dp, 24.dp, 34.dp, 36.dp)
    override val proporcionFicha = 1.15f
    override val anchoGrande = 1f
    override val proporcionGrande = 0.95f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un óstrakon marcado.
            "hecho" -> selloHecho(TERRA_OSC, p.color, cuadrado = true)
            "convence" -> { drawCircle(BARNIZ, s * 0.2f, c); drawCircle(BARNIZ, s * 0.4f, c, style = Stroke(s * 0.1f)) }
            // Un gancho de greca: lo estás pensando, todavía da vueltas.
            "piensa" -> drawPath(Path().apply {
                moveTo(c.x - s * 0.36f, c.y + s * 0.36f); lineTo(c.x - s * 0.36f, c.y - s * 0.36f); lineTo(c.x + s * 0.36f, c.y - s * 0.36f)
                lineTo(c.x + s * 0.36f, c.y + s * 0.12f); lineTo(c.x - s * 0.06f, c.y + s * 0.12f); lineTo(c.x - s * 0.06f, c.y - s * 0.1f)
            }, TERRA_OSC, style = Stroke(s * 0.12f))
            "duda" -> drawArc(p.tenue, 30f, 300f, false, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Size(s * 0.68f, s * 0.68f), style = Stroke(s * 0.12f))
            "desacuerdo" -> { drawLine(BARNIZ, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Offset(c.x + s * 0.36f, c.y + s * 0.36f), s * 0.11f); drawLine(BARNIZ, Offset(c.x + s * 0.36f, c.y - s * 0.36f), Offset(c.x - s * 0.36f, c.y + s * 0.36f), s * 0.11f) }
            else -> {}
        }
    }

    /** El sol de Vergina, en oro. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        drawPath(simboloCalificacion(TemaId.HELENO, center, size.minDimension * 0.46f), ORO)
    }

    override val desorden = 2.5f
}
