package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Japón Edo
// =====================================================================

/**
 * Japón Edo: el tansu del cuarto de atrás de una casa de té de posta, hacia 1830. Es un interior sin ventana: una pared de
 * barro enlucido (tsuchikabe, color arena verdosa, con sus pajas, una grieta fina y una mancha de barro más nuevo), enmarcada por una viga
 * oscura arriba y un pilar cortado por el borde derecho. La luz de día entra por la izquierda, baja y rasante, de un shōji
 * que no se ve: sombras largas y limpias hacia la derecha y la veta del keyaki encendida. El mueble es un tansu de keyaki
 * (olmo japonés) con laca frotada, más oscura en el marco: cantoneras de hierro negro con sus remaches, placas en cada
 * travesaño, asas de carga a los costados, la junta entre los cuerpos apilados cada dos filas y un zócalo con patas. Encima,
 * con aire entre las cosas: a la izquierda, el punto focal, un andon (la linterna de papel con su armazón laqueado, su asa
 * con anilla, el papel tiznado arriba y el panel de delante subido, por donde se ve el platillo de aceite con su mecha); la piedra de tinta con el pincel apoyado, la barra
 * de sumi y un tanzaku a medio escribir (trazos, no palabras); la tetera de hierro sobre su rodete de paja y una taza; un
 * tubo de bambú con una rama quebrada de ciruelo rojo, y delante un fardo envuelto en furoshiki índigo con su nudo. En la
 * pared, un sombrero de viaje de junco (sugegasa) colgado de un clavo, con su cordón. Al pie, el tatami con su trama y su
 * borde (heri) índigo, y un brasero (hibachi) de loza oscura con su ceniza, su carbón y sus palillos de hierro, cortado por
 * el borde. Cada tema es un cajón, de tres clases repartidas por su sitio (nunca dos iguales en una fila): con el papel de
 * washi pegado, dos tiradores colgantes (warabite) y los tanzaku pillados que cuelgan por arriba; con cerradura redonda,
 * fajas de hierro y el papel sujeto con una cinta, con los tanzaku metidos detrás en un manojo abierto; o entreabierto, con
 * su placa de mariposa y los tanzaku de pie asomando. Cambian según la semilla la madera (keyaki o keyaki viejo; uno de cada
 * cinco, un frente de kiri repuesto), la veta, el tinte del papel, el color de la cinta y los de los tanzaku; se ven más
 * tanzaku cuantas más fichas guarda. Lo que no tiene tema es una caja larga de kiri atada con cordón plano (sanada-himo), puesta en el hueco, con
 * los tanzaku detrás. Un hueco libre es un cajón cerrado, solo con su herraje y la huella de un papel viejo. Al abrir uno, el
 * frente baja y se acerca. De noche alumbra el andon: el papel encendido desde dentro con la sombra de su armazón, un charco
 * de luz ámbar en la pared y en la tapa, el filo cálido en el hierro; la veta se apaga de fila en fila y hacia la derecha, y
 * abajo solo queda el rescoldo de las brasas del hibachi; lo demás se hunde en índigo. Por dentro, el cajón es de kiri
 * (paulonia), claro y de veta recta. Las fichas son tanzaku teñidos en nube (uchigumori: añil arriba, violeta abajo) con
 * hojuelas de oro y su cordón.
 */
object IdeasEdo : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del cuarto
    /** El barro enlucido de la pared (tsuchikabe) y su sombra. */
    private val BARRO = Color(0xFFC7B98C)
    private val BARRO_OSC = Color(0xFF93875F)
    /** El keyaki del tansu, su veta oscura y su luz. */
    private val KEYAKI = Color(0xFF8B5A2E)
    private val KEYAKI_OSC = Color(0xFF55331A)
    private val KEYAKI_LUZ = Color(0xFFB98552)
    /** El cedro ahumado de la viga, el pilar y el armazón del andon. */
    private val SUGI = Color(0xFF46301F)
    /** El hierro negro de los herrajes y de la tetera, y su brillo. */
    private val HIERRO = Color(0xFF1E1B1A)
    private val HIERRO_LUZ = Color(0xFF5C5650)
    /** El kiri (paulonia) y su veta. */
    private val KIRI = Color(0xFFE8D8B9)
    private val KIRI_VETA = Color(0xFFB89D70)
    /** El washi, la tinta sumi, el bermellón del sello y de las flores, el índigo. */
    private val WASHI = Color(0xFFF4EEDF)
    private val SUMI = Color(0xFF1B1A1E)
    private val BERMELLON = Color(0xFFC9442E)
    private val ANIL = Color(0xFF1E3A5F)
    /** La paja del sombrero, del rodete y del tatami; el bambú cortado. */
    private val PAJA = Color(0xFFD2BC80)
    private val BAMBU = Color(0xFF9C9658)
    /** La llama del andon, las brasas del hibachi y la noche del cuarto: índigo hondo. */
    private val LLAMA = Color(0xFFFFC56E)
    private val BRASA = Color(0xFFE9683A)
    private val NOCHE = Color(0xFF0C1226)
    /**
     * El papel de noche lejos del andon: con [SUMI] da más de 6:1 en el título y unos 4.5:1 en la cuenta (al 78 %); cerca de
     * la luz sube hacia [WASHI] con un poco de llama.
     */
    private val WASHI_LEJOS = Color(0xFFA8A28E)
    /** Las del interior y las fichas (sin cambios). */
    private val VIOLETA = Color(0xFF5E4383)
    private val ORO = Color(0xFFC9A24C)
    /** Los colores de los tanzaku que asoman de los cajones. */
    private val TANZAKU = listOf(WASHI, Color(0xFFBCCADC), Color(0xFFEBB9A8), Color(0xFFE9DCA4), Color(0xFFCFBEDB), Color(0xFFBFD6B9))

    /** La madera del interior del cajón, como siempre. */
    private fun madera(oscuro: Boolean) = if (oscuro) mezcla(KEYAKI, Color.Black, 0.22f) else KEYAKI

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.75f
    override val corona = 0.6f
    override val base = 0.27f
    override val lado = 0.06f
    override val hueco = 0.022f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: lo bajo de la viga, el fondo de la tapa del tansu y su canto de delante, en fracción de su alto. */
    private const val VIGA = 0.072f
    private const val SUP = 0.835f
    private const val FRENTE = 0.925f
    /** En la base: lo bajo del cuerpo, el rincón de la pared con el tatami y donde pisan las patas, en fracción de su alto. */
    private const val RIEL = 0.27f
    private const val SUELO = 0.36f
    private const val PISAN = 0.47f
    /** Los costados del tansu y el canto del pilar, en fracción del ancho. */
    private const val IZQ = 0.028f
    private const val DER = 0.972f
    private const val PILAR = 0.928f
    /** Cuánto baja el frente del cajón al abrirse completo, en fracciones de su alto. */
    private const val SALIDA = 0.14f
    /** Las brasas del hibachi: su x (fracción del ancho) y su altura dentro de la base. */
    private const val BRASA_X = 0.862f
    private const val BRASA_Y = 0.6f

    // ---------------------------------------------------------------- la luz y la noche

    /** El corazón del andon (su llama), en fracciones del ancho. */
    private val LUZ = Offset(0.235f, 0.37f)

    /**
     * La luz que el andon echa sobre el frente del tansu: cae despacio de fila en fila (la primera, encendida; la segunda, a
     * medias; la tercera, tenue; las últimas, casi perdidas) y un poco menos en la columna de la derecha, más lejos. Los
     * puntos son los centros de las filas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.555f to 0.95f, 0.745f to 0.82f, 1.012f to 0.56f, 1.279f to 0.31f, 1.546f to 0.14f, 1.813f to 0.06f, 2.1f to 0.035f, 2.8f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.62f * (abs(p.x - LUZ.x) - 0.08f).coerceAtLeast(0f)).coerceIn(0.66f, 1f)
        return k * lado
    }

    /** Cuánto le llega del andon a un punto [p] (en fracciones del ancho): cae con la distancia. */
    private fun luzAndon(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        return maxOf(1.5f * exp(-4f * sqrt(dx * dx + dy * dy)), luzCuerpo(p))
    }

    /** El rescoldo de las brasas, que están en [b]: poco y muy cerca. */
    private fun luzBrasa(p: Offset, b: Offset) = 0.55f * exp(-5.2f * (p - b).getDistance())

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el índigo de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia
     * el suyo según [k] (lo que le llega de las luces), teñido de llama, o de brasa en la parte [br] que viene del hibachi.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f, val br: Float = 0f)

    private fun Tono.calor() = if (br > 0.02f) mezcla(LLAMA, BRASA, br) else LLAMA

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, calor(), 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del cuarto. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else calor().copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche, su ancho (para pasar píxeles a fracciones) y dónde están las brasas. */
    private var esNoche = false
    private var anchoW = 1f
    private var brasas = Offset(BRASA_X, 2.2f)

    private fun tonoEn(f: Offset, b: Offset, dia: Float = 0f): Tono {
        val a = luzAndon(f); val r = luzBrasa(f, b)
        return Tono(true, (a + r).coerceIn(0.03f, 1f), dia, r / (a + r + 0.001f))
    }

    private fun tn(p: Offset): Tono = if (esNoche) tonoEn(p / anchoW, brasas) else Tono(false, 0f)

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared: [silueta] lo pinta del color que se le da. De día la luz es rasante y la sombra se corre
     * larga a la derecha; de noche huye del andon, tanto más marcada cuanto más cerca está de él. No baja de [hasta].
     */
    private inline fun DrawScope.sombraEnPared(desde: Offset, hasta: Float, a: Float, crossinline silueta: DrawScope.(Color) -> Unit) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.05f, w * 0.011f) { silueta(sombraMueble(a)) }
            else {
                val f = desde / w
                val k = luzAndon(f)
                val v = f - LUZ
                val d = v.getDistance()
                if (k > 0.1f && d > 0.06f) translate(w * 0.05f * v.x / d, w * 0.03f * v.y / d) { silueta(NOCHE.copy(alpha = (0.7f * a / 0.2f * k).coerceAtMost(0.5f))) }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa o el tatami, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.4f, centro.y - alto / 2), Size(ancho, alto))

    /** La sombra larga y rasante que algo de [x0] a [x1] deja sobre la tapa, hacia la derecha, desde su pie [y]. */
    private fun DrawScope.rasante(x0: Float, x1: Float, y: Float, largo: Float, t: Tono, a: Float = 0.2f) {
        val g = anchoW * 0.006f
        drawPath(poligono(x0 + (x1 - x0) * 0.3f, y - g, x1, y - g * 1.6f, x1 + largo, y - g * 0.4f, x1 + largo * 0.9f, y + g, x0 + (x1 - x0) * 0.5f, y + g), t.s(a))
    }

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro a la izquierda y la sombra a la derecha. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.2f * fuerza), r.topLeft, Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.32f * fuerza), Offset(r.left + r.width * 0.13f, r.top), Size(r.width * 0.09f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(r.left + r.width * 0.58f, r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un remache de hierro: su sombra, su cabeza y el punto de luz. */
    private fun DrawScope.remache(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.4f), r * 1.1f, p + Offset(r * 0.45f, r * 0.5f))
        drawCircle(t.c(mezcla(HIERRO, HIERRO_LUZ, 0.3f)), r, p)
        drawCircle(t.b(0.55f), r * 0.42f, p - Offset(r * 0.3f, r * 0.32f))
    }

    /** Una placa de hierro con la forma [p]: su sombra corrida, el negro y el filo de luz por arriba y a la izquierda. */
    private fun DrawScope.chapa(p: Path, t: Tono) {
        val u = density
        translate(0.9f * u, 1.2f * u) { drawPath(p, t.s(0.38f)) }
        drawPath(p, t.c(HIERRO))
        clipPath(p) { translate(0.8f * u, 0.9f * u) { drawPath(p, t.c(mezcla(HIERRO, HIERRO_LUZ, 0.22f))) } }
        drawPath(p, t.b(0.28f), style = Stroke(0.7f * u))
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
        brasas = Offset(BRASA_X, (b.top + hb * BRASA_Y) / w)
        val sup = ch * SUP; val frente = ch * FRENTE
        val pieA = sup + (frente - sup) * 0.26f
        val pieM = sup + (frente - sup) * 0.56f
        val pieF = sup + (frente - sup) * 0.88f
        val suelo = b.top + hb * SUELO

        // ---- la pared de barro, el tatami, la viga y el pilar
        pared(w, ch, suelo)
        tatami(w, b, suelo, alto)
        viga(w, ch)
        pilar(w, ch, suelo)

        // ---- en la pared: el sombrero de junco
        sugegasa(Offset(w * 0.512f, w * 0.176f), sup)

        // ---- el tansu y su tapa
        cuerpo(d, frente, b)
        tapa(w, sup, frente)

        // ---- lo de encima, de atrás adelante
        andon(Offset(w * 0.235f, pieA), sup, frente)
        ciruelo(Offset(w * 0.79f, pieA), sup, frente)
        tetsubin(Offset(w * 0.618f, pieM), sup)
        suzuri(Offset(w * 0.452f, pieF), sup)
        furoshiki(Offset(w * 0.885f, pieF), sup)
        taza(Offset(w * 0.712f, pieF))

        // ---- al pie: el hibachi, cortado por el borde
        hibachi(b, w, alto)

        // ---- la placa de «y N más»: un papel pegado en el travesaño de abajo, con su sello
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.2f * u, r.top + 1.6f * u), r.size)
            drawRect(if (o) WASHI_LEJOS else WASHI, r.topLeft, r.size)
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
            // El sello, en la esquina de arriba, sin tocar la letra.
            val s = r.height * 0.26f
            drawRect(if (o) mezcla(BERMELLON, NOCHE, 0.35f) else BERMELLON, Offset(r.right - s * 0.7f, r.top - s * 0.3f), Size(s, s))
        }

        // ---- la luz del andon y el rescoldo de las brasas, por encima de lo que tienen cerca
        if (o) {
            val llama = LUZ * w
            mancha(llama, w * 0.5f, LLAMA.copy(alpha = 0.1f))
            mancha(llama, w * 0.2f, LLAMA.copy(alpha = 0.2f))
            mancha(brasas * w, w * 0.16f, BRASA.copy(alpha = 0.16f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- la pared, la viga, el pilar y el tatami

    /**
     * La pared de barro enlucido: la luz rasante que entra por la izquierda (de noche, el charco del andon), pinceladas, las
     * pajas del amasado con su brillo, una grieta fina que baja de la viga, un remiendo de barro más nuevo y el roce de abajo.
     */
    private fun DrawScope.pared(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val r = Rect(0f, 0f, w, suelo)
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(BARRO, LUZ_MUEBLE, 0.36f), mezcla(BARRO, LUZ_MUEBLE, 0.1f), mezcla(BARRO, BARRO_OSC, 0.5f)), 0f, w), r.topLeft, r.size)
            mancha(Offset(-w * 0.1f, ch * 0.6f), w * 0.72f, luzMueble(0.34f))
        } else {
            fun k(d: Float) = tonoEn(LUZ + Offset(d, 0f), brasas).c(BARRO)
            drawRect(Brush.radialGradient(0f to k(0.04f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f), r.topLeft, r.size)
        }
        pinceladas(r, if (o) NOCHE else BARRO_OSC, 6001, 46, u, 0.1f)
        pinceladas(r, if (o) NOCHE else mezcla(BARRO, LUZ_MUEBLE, 0.5f), 6002, 22, u, 0.11f)
        // El remiendo: un paño de barro más nuevo, apenas más amarillo, que se funde con el viejo.
        val remiendo = Path().apply { addOval(Rect(w * 0.05f, ch * 0.15f, w * 0.15f, ch * 0.27f)) }
        val tr = tn(Offset(w * 0.09f, ch * 0.22f))
        drawPath(remiendo, tr.c(mezcla(BARRO, PAJA, 0.4f)).copy(alpha = 0.22f))
        // Las pajas del amasado: cortas, en todas direcciones; unas en sombra y otras que brillan con la luz rasante.
        for (k in 0 until 120) {
            val p = Offset(w * azar(6003, k), ch * 0.86f * azar(6004, k))
            val t = tn(p)
            val a = (azar(6005, k) - 0.5f) * 2.8f
            val l = (2.5f + 5.5f * azar(6006, k)) * u
            val q = Offset(p.x + l * cos(a), p.y + l * sin(a) * 0.7f)
            if (k % 3 == 0) drawLine(t.s(0.22f), p, q, 0.7f * u)
            else drawLine(t.c(mezcla(PAJA, LUZ_MUEBLE, 0.35f)).copy(alpha = 0.7f), p, q, 0.7f * u)
        }
        // La grieta fina que baja de la viga, con su ramal.
        val g0 = Offset(w * 0.385f, ch * VIGA)
        val tg = tn(Offset(w * 0.39f, ch * 0.2f))
        val grieta = Path().apply {
            moveTo(g0.x, g0.y)
            var q = g0
            for (j in 0 until 7) { q += Offset(w * (0.004f + 0.012f * azar(6010, j)) * (if (j % 2 == 0) 1f else -0.8f), ch * (0.022f + 0.02f * azar(6011, j))); lineTo(q.x, q.y) }
        }
        drawPath(grieta, tg.s(0.3f), style = Stroke(0.8f * u))
        translate(0.9f * u, 0.5f * u) { drawPath(grieta, tg.b(0.3f), style = Stroke(0.5f * u)) }
        drawLine(tg.s(0.24f), Offset(w * 0.392f, ch * 0.155f), Offset(w * 0.418f, ch * 0.19f), 0.6f * u)
        // El roce de abajo, donde se arrima lo que se guarda, y una mancha de humedad junto al pilar.
        drawRect(tn(Offset(w * 0.5f, ch * 0.8f)).s(0.07f), Offset(0f, ch * 0.74f), Size(w, suelo - ch * 0.74f))
        drawOval(tn(Offset(w * 0.86f, ch * 0.15f)).s(0.08f), Offset(w * 0.8f, ch * 0.07f), Size(w * 0.16f, ch * 0.2f))
    }

    /** La viga de cedro ahumado que corre arriba: su veta, un nudo, la arista de abajo y la sombra que deja en el barro. */
    private fun DrawScope.viga(w: Float, ch: Float) {
        val u = density
        val o = esNoche
        val yv = ch * VIGA
        val r = Rect(0f, 0f, w, yv)
        if (!o) { drawRect(SUGI, r.topLeft, r.size); drawRect(luzMueble(0.1f), r.topLeft, Size(w * 0.45f, yv)) }
        else drawRect(Brush.horizontalGradient(List(7) { i -> tn(Offset(w * i / 6f, yv / 2)).c(SUGI) }, 0f, w), r.topLeft, r.size)
        vetas(r, (if (o) NOCHE else SUMI).copy(alpha = 0.4f), 6101, 0.8f, grosor = 0.7f, ondulacion = 0.5f)
        val nudo = Offset(w * 0.66f, yv * 0.45f)
        val tk = tn(nudo)
        drawOval(tk.s(0.4f), Offset(nudo.x - w * 0.012f, nudo.y - yv * 0.18f), Size(w * 0.024f, yv * 0.36f))
        drawOval(tk.b(0.2f), Offset(nudo.x - w * 0.016f, nudo.y - yv * 0.26f), Size(w * 0.032f, yv * 0.52f), style = Stroke(0.6f * u))
        // La arista de abajo y la sombra en la pared.
        for (i in 0 until 8) {
            val x0 = w * i / 8f
            val t = tn(Offset(x0 + w / 16f, yv))
            drawRect(t.s(0.45f), Offset(x0, yv - 1.6f * u), Size(w / 8f + 1f, 1.6f * u))
            if (o) drawRect(t.b(0.5f), Offset(x0, yv - 2.6f * u), Size(w / 8f + 1f, 1f * u))
            drawRect(t.s(0.26f), Offset(x0, yv), Size(w / 8f + 1f, w * 0.011f))
            drawRect(t.s(0.12f), Offset(x0, yv + w * 0.011f), Size(w / 8f + 1f, w * 0.018f))
        }
        if (!o) drawRect(luzMueble(0.22f), Offset(0f, yv * 0.12f), Size(w, 0.8f * u))
    }

    /** El pilar, cortado por el borde derecho: su veta a lo largo, la arista que mira a la luz y una muesca vieja. */
    private fun DrawScope.pilar(w: Float, ch: Float, suelo: Float) {
        val u = density
        val x0 = w * PILAR
        val r = Rect(x0, 0f, w, suelo)
        val t = tn(Offset(x0 + (w - x0) / 2, ch * 0.5f))
        val col = mezcla(SUGI, KEYAKI_OSC, 0.3f)
        drawRect(t.c(col), r.topLeft, r.size)
        drawRect(t.b(0.12f), r.topLeft, Size(r.width * 0.35f, r.height))
        vetas(r, (if (esNoche) NOCHE else SUMI).copy(alpha = 0.34f), 6111, 0.8f, vertical = true, grosor = 0.7f, ondulacion = 0.7f)
        drawRect(t.s(0.3f), Offset(x0 - 1.2f * u, ch * VIGA), Size(1.2f * u, suelo - ch * VIGA))
        drawRect(t.b(0.5f), Offset(x0, 0f), Size(1.3f * u, suelo))
        // La junta con la viga y una muesca donde hubo otra tabla.
        drawRect(t.s(0.4f), Offset(x0, ch * VIGA - 1.2f * u), Size(w - x0, 1.2f * u))
        drawRect(t.s(0.4f), Offset(x0 + r.width * 0.2f, ch * 0.3f), Size(r.width * 0.5f, ch * 0.022f))
        drawRect(t.b(0.25f), Offset(x0 + r.width * 0.2f, ch * 0.322f), Size(r.width * 0.5f, 0.8f * u))
    }

    /**
     * El tatami, visto desde arriba y de frente: la trama de junco, más abierta hacia quien mira; el borde (heri) índigo de dos
     * esteras que se juntan, con su tejido; la junta de las de delante; el listón del rincón y el desgaste del paso.
     */
    private fun DrawScope.tatami(w: Float, b: Rect, suelo: Float, alto: Float) {
        val u = density
        val o = esNoche
        val hb = b.height
        val junco = mezcla(PAJA, BAMBU, 0.32f)
        val r = Rect(0f, suelo, w, alto)
        if (!o) drawRect(Brush.verticalGradient(listOf(mezcla(junco, BARRO_OSC, 0.3f), mezcla(junco, LUZ_MUEBLE, 0.08f)), suelo, alto), r.topLeft, r.size)
        else drawRect(Brush.horizontalGradient(List(7) { i -> tn(Offset(w * i / 6f, suelo + (alto - suelo) * 0.6f)).c(junco) }, 0f, w), r.topLeft, r.size)
        if (!o) drawRect(luzMueble(0.1f), r.topLeft, Size(w * 0.3f, r.height))
        var y = suelo + 2f * u
        var paso = 1.5f * u
        val raya = if (o) NOCHE.copy(alpha = 0.3f) else mezcla(junco, SUGI, 0.55f).copy(alpha = 0.32f)
        while (y < alto) { drawLine(raya, Offset(0f, y), Offset(w, y), 0.6f * u); y += paso; paso *= 1.04f }
        pinceladas(r, if (o) NOCHE else mezcla(junco, LUZ_MUEBLE, 0.5f), 6121, 14, u, 0.13f)
        pinceladas(r, if (o) NOCHE else BARRO_OSC, 6122, 10, u, 0.12f)
        // El heri de dos esteras que se juntan, a lo ancho.
        val h0 = b.top + hb * 0.795f; val h1 = b.top + hb * 0.88f
        val heri = mezcla(ANIL, SUMI, 0.4f)
        for (i in 0 until 8) {
            val x0 = w * i / 8f
            val t = tn(Offset(x0 + w / 16f, (h0 + h1) / 2))
            drawRect(t.c(heri), Offset(x0, h0), Size(w / 8f + 1f, h1 - h0))
            drawRect(t.b(0.3f), Offset(x0, h0), Size(w / 8f + 1f, 0.9f * u))
            drawRect(t.s(0.5f), Offset(x0, (h0 + h1) / 2 - 0.6f * u), Size(w / 8f + 1f, 1.2f * u))
            drawRect(t.s(0.3f), Offset(x0, h1), Size(w / 8f + 1f, 1.4f * u))
        }
        var x = w * 0.004f
        while (x < w) {
            val t = tn(Offset(x, h0))
            drawCircle(t.c(mezcla(ANIL, WASHI, 0.45f)).copy(alpha = 0.55f), 0.7f * u, Offset(x, h0 + (h1 - h0) * 0.25f))
            drawCircle(t.c(mezcla(ANIL, WASHI, 0.45f)).copy(alpha = 0.55f), 0.7f * u, Offset(x + 2.5f * u, h0 + (h1 - h0) * 0.75f))
            x += 5f * u
        }
        // La junta de las dos esteras de delante, que viene hacia quien mira.
        val tj = tn(Offset(w * 0.3f, alto))
        drawPath(poligono(w * 0.328f, h1, w * 0.344f, h1, w * 0.338f, alto, w * 0.316f, alto), tj.c(heri))
        drawLine(tj.s(0.4f), Offset(w * 0.344f, h1), Offset(w * 0.338f, alto), 1.2f * u)
        drawLine(tj.b(0.3f), Offset(w * 0.328f, h1), Offset(w * 0.316f, alto), 0.8f * u)
        // El listón del rincón, bajo la pared.
        val tl = tn(Offset(w * 0.02f, suelo))
        drawRect(tl.c(SUGI), Offset(0f, suelo - w * 0.007f), Size(w, w * 0.007f))
        drawRect(tl.b(0.3f), Offset(0f, suelo - w * 0.007f), Size(w, 0.8f * u))
        drawRect(tl.s(0.3f), Offset(0f, suelo), Size(w, 2.5f * u))
    }

    // ---------------------------------------------------------------- en la pared

    /**
     * El sombrero de viaje de junco (sugegasa), colgado de un clavo por su lazo: el cono bajo visto de frente, con sus varillas,
     * las vueltas del cosido, el aro del borde, un roto en el ala y el cordón de la barbilla que cuelga.
     */
    private fun DrawScope.sugegasa(c: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val rx = w * 0.108f; val ry = w * 0.1f
        val t = tn(c)
        val ala = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
        val clavo = Offset(c.x - w * 0.004f, c.y - ry - w * 0.022f)
        val punta = Offset(c.x - rx * 0.2f, c.y - ry * 0.3f)
        val cordon = Path().apply {
            moveTo(c.x - rx * 0.42f, c.y + ry * 0.86f)
            cubicTo(c.x - rx * 0.5f, c.y + ry * 1.5f, c.x - rx * 0.1f, c.y + ry * 1.95f, c.x + rx * 0.02f, c.y + ry * 1.7f)
            cubicTo(c.x + rx * 0.12f, c.y + ry * 1.45f, c.x + rx * 0.4f, c.y + ry * 1.3f, c.x + rx * 0.36f, c.y + ry * 0.9f)
        }
        sombraEnPared(c, sup, 0.22f) { col -> drawOval(col, ala.topLeft, ala.size); drawPath(cordon, col, style = Stroke(1.6f * u)) }
        // El lazo y el clavo.
        drawLine(t.c(mezcla(WASHI, PAJA, 0.4f)), clavo, Offset(c.x - rx * 0.12f, c.y - ry * 0.97f), 1f * u)
        drawLine(t.c(mezcla(WASHI, PAJA, 0.4f)), clavo, Offset(c.x + rx * 0.1f, c.y - ry * 0.98f), 1f * u)
        remache(clavo, 1.5f * u, t)
        // El cordón de la barbilla, de algodón crudo, con su nudo.
        drawPath(cordon, t.c(mezcla(WASHI, PAJA, 0.35f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        drawPath(cordon, t.s(0.2f), style = Stroke(0.5f * u))
        drawCircle(t.c(mezcla(WASHI, PAJA, 0.5f)), 2f * u, Offset(c.x + rx * 0.02f, c.y + ry * 1.72f))
        // El cono.
        val forma = Path().apply { addOval(ala) }
        drawPath(forma, t.c(PAJA))
        clipPath(forma) {
            // Las caras del cono, de la punta al ala: cada gajo más claro cuanto más mira arriba a la izquierda.
            val gajos = 16
            for (j in 0 until gajos) {
                val a0 = j * 6.2832f / gajos; val a1 = (j + 1) * 6.2832f / gajos
                val am = (a0 + a1) / 2
                val luz = -(cos(am) * 0.7f + sin(am) * 0.7f)
                val gajo = poligono(punta.x, punta.y, c.x + rx * 1.05f * cos(a0), c.y + ry * 1.05f * sin(a0), c.x + rx * 1.05f * cos(a1), c.y + ry * 1.05f * sin(a1))
                if (luz > 0f) drawPath(gajo, t.b(0.3f * luz)) else drawPath(gajo, t.s(-0.26f * luz))
            }
            // Las varillas de bambú, de la punta al borde.
            for (j in 0 until 22) {
                val a = j * 6.2832f / 22f + 0.14f
                val q = Offset(c.x + rx * cos(a), c.y + ry * sin(a))
                drawLine(t.s(0.12f), punta, q, 0.6f * u)
            }
            // Las vueltas del cosido: dos, flojas.
            listOf(0.42f, 0.74f).forEach { f ->
                val cc = Offset(punta.x + (c.x - punta.x) * f, punta.y + (c.y - punta.y) * f)
                drawOval(t.s(0.14f), Offset(cc.x - rx * f, cc.y - ry * f), Size(rx * 2 * f, ry * 2 * f), style = Stroke(0.6f * u))
            }
            // Una mancha de lluvia vieja.
            drawOval(t.c(mezcla(PAJA, SUGI, 0.4f)).copy(alpha = 0.3f), Offset(c.x - rx * 0.7f, c.y + ry * 0.1f), Size(rx * 0.6f, ry * 0.45f))
        }
        // El aro del borde, con el lado de luz y un roto por donde asoman unas pajas.
        drawOval(t.c(mezcla(PAJA, SUGI, 0.5f)), ala.topLeft, ala.size, style = Stroke(2f * u))
        drawArc(t.b(0.5f), 150f, 100f, false, ala.topLeft, ala.size, style = Stroke(1f * u))
        val roto = Offset(c.x + rx * 0.78f, c.y + ry * 0.63f)
        drawCircle(tn(roto + Offset(w * 0.05f, 0f)).c(BARRO), 3f * u, roto + Offset(1.5f * u, 1.5f * u))
        for (j in 0 until 4) drawLine(t.c(mezcla(PAJA, LUZ_MUEBLE, 0.2f)), roto, roto + Offset((3f + 2f * j) * u, (5f - 2.2f * j) * u), 0.7f * u)
        // La punta del cono.
        drawCircle(t.s(0.35f), w * 0.008f, punta + Offset(1.5f * u, 1.5f * u))
        drawCircle(t.c(mezcla(PAJA, SUGI, 0.35f)), w * 0.0075f, punta)
        drawCircle(t.b(0.55f), w * 0.003f, punta - Offset(w * 0.002f, w * 0.002f))
    }

    // ---------------------------------------------------------------- el tansu

    /** Una cantonera de hierro en la esquina [o]: dos brazos con la punta recortada y sus remaches. [sx] y [sy], hacia dónde se abre. */
    private fun DrawScope.cantonera(o: Offset, sx: Float, sy: Float, l: Float, g: Float, t: Tono) {
        val p = Path().apply {
            moveTo(o.x, o.y)
            lineTo(o.x + sx * l, o.y)
            lineTo(o.x + sx * (l + g * 0.45f), o.y + sy * g * 0.5f)
            lineTo(o.x + sx * l, o.y + sy * g)
            lineTo(o.x + sx * g, o.y + sy * g)
            lineTo(o.x + sx * g, o.y + sy * l)
            lineTo(o.x + sx * g * 0.5f, o.y + sy * (l + g * 0.45f))
            lineTo(o.x, o.y + sy * l)
            close()
        }
        chapa(p, t)
        val r = g * 0.13f
        listOf(0.5f to 0.5f, 0.5f to -1f, -1f to 0.5f).forEach { (a, b) ->
            val px = if (a < 0) o.x + sx * l * 0.62f else o.x + sx * g * a
            val py = if (b < 0) o.y + sy * l * 0.62f else o.y + sy * g * b
            remache(Offset(px, py), r, t)
        }
        remache(Offset(o.x + sx * l * 0.95f, o.y + sy * g * 0.5f), r, t)
        remache(Offset(o.x + sx * g * 0.5f, o.y + sy * l * 0.95f), r, t)
    }

    /**
     * El cuerpo del tansu: su sombra en la pared y en el tatami, el marco de keyaki con la laca más oscura (de noche, encendido
     * arriba a la izquierda, bajo el andon), la veta, el rebaje de cada cajón, los travesaños con su arista de luz, la junta
     * de los cuerpos apilados, los herrajes (cantoneras, placas, asas de carga) y el zócalo con sus patas.
     */
    private fun DrawScope.cuerpo(d: Disposicion, frente: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val fondo = b.top + hb * RIEL
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(xL, frente, xR, fondo)
        val marco = mezcla(KEYAKI, KEYAKI_OSC, 0.38f)

        // Su sombra en la pared (a la derecha, sobre el pilar) y en el tatami.
        val t0 = tn(Offset(xR, frente + cuerpo.height * 0.3f))
        drawRect(t0.s(0.3f), Offset(xR, frente + w * 0.012f), Size(w - xR, suelo - frente))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawPath(poligono(xL + w * 0.03f, pisan - 1f * u, xR, pisan - 1f * u, w, pisan + hb * 0.1f, w, pisan + hb * 0.17f, xL + w * 0.1f, pisan + hb * 0.17f), tp.s(0.26f))

        // El zócalo: un bastidor remetido, con el hueco de abajo y sus dos patas.
        val zoc = Rect(xL + w * 0.014f, fondo, xR - w * 0.014f, pisan)
        val tz = tn(zoc.center)
        val maderaZ = mezcla(KEYAKI_OSC, SUGI, 0.45f)
        drawRect(tz.c(maderaZ), zoc.topLeft, zoc.size)
        vetas(zoc, (if (o) NOCHE else SUMI).copy(alpha = 0.3f), 6301, 0.5f, grosor = 0.6f)
        val vano = Path().apply {
            moveTo(zoc.left + w * 0.1f, zoc.bottom + 1f)
            cubicTo(zoc.left + w * 0.11f, zoc.top + zoc.height * 0.5f, zoc.left + w * 0.14f, zoc.top + zoc.height * 0.42f, zoc.left + w * 0.2f, zoc.top + zoc.height * 0.42f)
            lineTo(zoc.right - w * 0.2f, zoc.top + zoc.height * 0.42f)
            cubicTo(zoc.right - w * 0.14f, zoc.top + zoc.height * 0.42f, zoc.right - w * 0.11f, zoc.top + zoc.height * 0.5f, zoc.right - w * 0.1f, zoc.bottom + 1f)
            close()
        }
        drawPath(vano, if (o) NOCHE else mezcla(SUGI, SOMBRA_MUEBLE, 0.75f))
        drawPath(vano, tz.b(0.3f), style = Stroke(0.8f * u))
        drawRect(tz.s(0.45f), zoc.topLeft, Size(zoc.width, 2.5f * u))
        drawRect(tz.s(0.2f), Offset(zoc.left, zoc.top + 2.5f * u), Size(zoc.width, 4f * u))
        drawRect(tz.b(0.3f), zoc.topLeft, Size(1.2f * u, zoc.height))

        // El frente: el marco de keyaki.
        if (!o) {
            drawRect(marco, cuerpo.topLeft, cuerpo.size)
            drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent, sombraMueble(0.22f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        } else {
            drawRect(Brush.verticalGradient(List(8) { i -> tn(Offset(w * 0.3f, cuerpo.top + cuerpo.height * i / 7f)).c(marco) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.4f)), w * 0.35f, xR), cuerpo.topLeft, cuerpo.size)
        }
        val veta = (if (o) NOCHE else KEYAKI_OSC).copy(alpha = 0.42f)
        vetas(cuerpo, veta, 6302, 0.4f, grosor = 0.7f, ondulacion = 0.9f)
        // Los largueros de los costados y el montante del medio, con la veta a lo largo.
        val h0 = d.huecos.first(); val hN = d.huecos.last()
        val izq = Rect(xL, frente, h0.left - 1.6f * u, fondo)
        val der = Rect(hN.right + 1.6f * u, frente, xR, fondo)
        listOf(izq, der).forEach { s ->
            drawRect(tn(s.center).s(0.1f), s.topLeft, s.size)
            vetas(s, veta, 6303 + s.left.toInt(), 0.9f, vertical = true, grosor = 0.7f, ondulacion = 0.7f)
        }
        if (cols > 1) {
            val m = Rect(h0.right + 1.6f * u, h0.top, d.huecos[1].left - 1.6f * u, hN.bottom)
            vetas(m, veta, 6304, 0.9f, vertical = true, grosor = 0.6f, ondulacion = 0.6f)
        }
        // El canto de la tabla de arriba, con sus colas de milano en las esquinas.
        val tabla = frente + w * 0.022f
        for (i in 0 until 8) {
            val x0 = xL + (xR - xL) * i / 8f
            val t = tn(Offset(x0 + (xR - xL) / 16f, tabla))
            drawRect(t.s(0.4f), Offset(x0, tabla), Size((xR - xL) / 8f + 1f, 1.1f * u))
            drawRect(t.b(0.22f), Offset(x0, tabla + 1.1f * u), Size((xR - xL) / 8f + 1f, 0.8f * u))
        }
        // Los rebajes de los cajones: el hueco oscuro alrededor de cada frente y la arista de luz debajo y a la derecha.
        d.huecos.forEach { r ->
            val t = tn(r.center)
            drawRect(if (o) mezcla(NOCHE, t.c(KEYAKI_OSC), 0.45f) else mezcla(KEYAKI_OSC, SOMBRA_MUEBLE, 0.72f), Offset(r.left - 1.8f * u, r.top - 1.8f * u), Size(r.width + 3.6f * u, r.height + 3.6f * u))
            drawRect(t.b(0.42f), Offset(r.left - 1.8f * u, r.bottom + 1.8f * u), Size(r.width + 3.6f * u, 1f * u))
            drawRect(t.b(0.22f), Offset(r.right + 1.8f * u, r.top - 1.8f * u), Size(0.9f * u, r.height + 3.6f * u))
        }
        // La junta entre los cuerpos apilados, cada dos filas, con sus grapas de hierro.
        d.filas.zipWithNext().forEachIndexed { i, (a, c) ->
            val y = (a.bottom + c.top) / 2
            val t = tn(Offset(w * 0.5f, y))
            if (i % 2 == 1) {
                drawRect(t.s(0.6f), Offset(xL, y - 0.8f * u), Size(xR - xL, 1.6f * u))
                drawRect(t.b(0.3f), Offset(xL, y + 0.8f * u), Size(xR - xL, 0.8f * u))
            }
            // Las placas de hierro que abrazan cada travesaño en los largueros.
            listOf(xL to 1f, xR to -1f).forEach { (x, s) ->
                val tt = tn(Offset(x, y))
                val l = w * 0.027f; val g = w * 0.0095f
                val p = poligono(x, y - g, x + s * l, y - g, x + s * (l + g * 0.7f), y, x + s * l, y + g, x, y + g)
                chapa(p, tt)
                remache(Offset(x + s * l * 0.3f, y), g * 0.3f, tt)
                remache(Offset(x + s * l * 0.82f, y), g * 0.3f, tt)
            }
        }
        // Las cantoneras de las cuatro esquinas.
        val l = w * 0.078f; val g = w * 0.024f
        cantonera(Offset(xL, frente), 1f, 1f, l, g, tn(Offset(xL, frente)))
        cantonera(Offset(xR, frente), -1f, 1f, l, g, tn(Offset(xR, frente)))
        cantonera(Offset(xL, fondo), 1f, -1f, l, g, tn(Offset(xL, fondo)))
        cantonera(Offset(xR, fondo), -1f, -1f, l, g, tn(Offset(xR, fondo)))
        // Las asas de carga de cada cuerpo: un hierro que asoma por el costado, con sus dos orejas.
        var f = 0
        while (f < d.filas.size) {
            val yc = d.filas[f].center.y
            listOf(xL to -1f, xR to 1f).forEach { (x, s) ->
                val t = tn(Offset(x, yc))
                val ah = w * 0.042f
                drawRect(t.s(0.3f), Offset(minOf(x, x + s * w * 0.016f) + 1.5f * u, yc - ah + 2f * u), Size(w * 0.016f, ah * 2))
                drawLine(t.c(HIERRO), Offset(x + s * w * 0.013f, yc - ah), Offset(x + s * w * 0.013f, yc + ah), w * 0.0065f, StrokeCap.Round)
                drawLine(t.c(HIERRO), Offset(x, yc - ah), Offset(x + s * w * 0.013f, yc - ah), w * 0.0065f, StrokeCap.Round)
                drawLine(t.c(HIERRO), Offset(x, yc + ah), Offset(x + s * w * 0.013f, yc + ah), w * 0.0065f, StrokeCap.Round)
                drawLine(t.b(0.4f), Offset(x + s * w * 0.013f - 1f * u, yc - ah), Offset(x + s * w * 0.013f - 1f * u, yc + ah), 0.7f * u)
            }
            f += 2
        }
        // Los cantos de los costados: el de la izquierda recibe la luz; el de la derecha, en sombra. Y el de abajo.
        for (k in 0 until 10) {
            val y0 = frente + cuerpo.height * k / 10f
            drawRect(tn(Offset(xL, y0)).b(0.4f), Offset(xL, y0), Size(1.3f * u, cuerpo.height / 10f + 1f))
            drawRect(tn(Offset(xR, y0)).s(0.4f), Offset(xR - w * 0.005f, y0), Size(w * 0.005f, cuerpo.height / 10f + 1f))
        }
        drawRect(tn(Offset(w * 0.5f, fondo)).s(0.35f), Offset(xL, fondo - 1.6f * u), Size(xR - xL, 1.6f * u))
        // El roce de las manos junto a los cajones de arriba.
        pinceladas(cuerpo, if (o) NOCHE else KEYAKI_LUZ, 6305, 18, u, 0.1f)
    }

    /**
     * La tapa del tansu, vista un poco desde arriba: la veta ancha y ondulada del keyaki, encendida por la luz rasante; el
     * cerco de la tetera, una gota de tinta, las chapas de las esquinas y la arista de delante.
     */
    private fun DrawScope.tapa(w: Float, sup: Float, frente: Float) {
        val u = density
        val o = esNoche
        val xL = w * IZQ; val xR = w * DER
        val p = poligono(xL + w * 0.012f, sup, xR - w * 0.012f, sup, xR, frente, xL, frente)
        val col = mezcla(KEYAKI, KEYAKI_LUZ, 0.62f)
        if (!o) drawPath(p, Brush.horizontalGradient(listOf(mezcla(col, LUZ_MUEBLE, 0.32f), mezcla(col, LUZ_MUEBLE, 0.1f), mezcla(col, KEYAKI, 0.45f)), xL, xR))
        else drawPath(p, Brush.horizontalGradient(List(9) { i -> tn(Offset(xL + (xR - xL) * i / 8f, (sup + frente) / 2)).c(col) }, xL, xR))
        clipPath(p) {
            val r = Rect(xL, sup, xR, frente)
            vetas(r, (if (o) NOCHE else KEYAKI_OSC).copy(alpha = 0.5f), 6201, 0.75f, grosor = 0.9f, ondulacion = 2.2f)
            if (!o) vetas(r, luzMueble(0.4f), 6202, 0.45f, grosor = 0.8f, ondulacion = 2.2f)
            // La sombra del rincón con la pared.
            drawRect(tn(Offset(w * 0.5f, sup)).s(0.28f), Offset(xL, sup), Size(xR - xL, 2f * u))
            // El cerco viejo de la tetera y una gota de tinta.
            val tc = tn(Offset(w * 0.55f, frente))
            drawOval(tc.s(0.16f), Offset(w * 0.52f, sup + (frente - sup) * 0.5f), Size(w * 0.07f, (frente - sup) * 0.4f), style = Stroke(1.1f * u))
            drawOval(tc.c(SUMI).copy(alpha = 0.7f), Offset(w * 0.388f, sup + (frente - sup) * 0.83f), Size(w * 0.012f, (frente - sup) * 0.09f))
            // Las chapas de las esquinas, que suben del frente.
            drawPath(poligono(xL, frente, xL + w * 0.078f, frente, xL + w * 0.074f, frente - (frente - sup) * 0.3f, xL + w * 0.005f, frente - (frente - sup) * 0.3f), tn(Offset(xL, frente)).c(HIERRO))
            drawPath(poligono(xR, frente, xR - w * 0.078f, frente, xR - w * 0.074f, frente - (frente - sup) * 0.3f, xR - w * 0.005f, frente - (frente - sup) * 0.3f), tn(Offset(xR, frente)).c(HIERRO))
        }
        for (i in 0 until 10) {
            val x0 = xL + (xR - xL) * i / 10f
            drawRect(tn(Offset(x0 + (xR - xL) / 20f, frente)).b(0.6f), Offset(x0, frente - 1.2f * u), Size((xR - xL) / 10f + 1f, 1.2f * u))
        }
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El andon: la linterna de papel de armazón laqueado. La base con su cajoncito para las mechas, los cuatro postes, la caja
     * de papel con su enrejado y un remiendo en flor, el costado que se aleja y el panel de delante subido: por el hueco se
     * ven el travesaño, el platillo de aceite y la mecha. De noche el papel se enciende desde dentro, con la sombra del
     * armazón, y la llama se ve por el hueco.
     */
    private fun DrawScope.andon(pie: Offset, sup: Float, frente: Float) {
        val u = density
        val w = anchoW
        val o = esNoche
        val aw = w * 0.168f
        val fl = pie.x - aw / 2; val fr = pie.x + aw / 2
        val dx = w * 0.036f; val dy = -w * 0.013f
        val pt = w * 0.0105f
        val yBase = pie.y - w * 0.022f
        val y1 = pie.y - w * 0.074f
        val y0 = pie.y - w * 0.296f
        val sube = w * 0.04f
        val asaY = y0 - w * 0.064f
        val t = tn(Offset(pie.x, y1))
        val laca = mezcla(SUGI, BERMELLON, 0.1f)
        val lacaLuz = mezcla(SUGI, KEYAKI, 0.3f)
        val papel = mezcla(WASHI, PAJA, 0.12f)

        // Su sombra larga en la pared: el papel deja pasar algo de luz; el armazón, no.
        if (!o) {
            sombraEnPared(pie, sup, 0.13f) { col ->
                drawPath(poligono(fl, y0, fr, y0, fr + dx, y0 + dy, fr + dx, yBase + dy, fr, pie.y, fl, pie.y), col)
                drawRect(col, Offset(fl + pt, y0 - sube), Size(aw - 2 * pt, sube))
            }
            sombraEnPared(pie, sup, 0.16f) { col ->
                drawRect(col, Offset(fl, asaY), Size(pt, yBase - asaY))
                drawRect(col, Offset(fr - pt, asaY), Size(pt, yBase - asaY))
                drawRect(col, Offset(fl, asaY + w * 0.006f), Size(aw, w * 0.009f))
                drawRect(col, Offset(fr + dx - pt * 0.8f, y0 + dy - w * 0.01f), Size(pt * 0.8f, yBase - y0))
                drawRect(col, Offset(fl - w * 0.008f, yBase), Size(aw + w * 0.016f + dx, pie.y - yBase))
                drawRect(col, Offset(fl, y1), Size(aw + dx, w * 0.011f))
            }
            rasante(fl, fr + dx, pie.y, w * 0.13f, t, 0.24f)
        }
        contacto(Offset(pie.x + w * 0.012f, pie.y), aw * 1.25f, w * 0.012f, 0.42f)

        // Los postes de atrás y el costado derecho de papel, que se aleja.
        drawRect(t.c(laca), Offset(fl + dx, y0 + dy - w * 0.01f), Size(pt * 0.8f, yBase - y0))
        val costado = poligono(fr, y0, fr + dx, y0 + dy, fr + dx, y1 + dy, fr, y1)
        if (!o) drawPath(costado, mezcla(papel, SOMBRA_MUEBLE, 0.16f))
        else drawPath(costado, Color(0xFFE7A04C))
        if (o) clipPath(costado) { drawRect(Color(0xFFB5682A).copy(alpha = 0.45f), Offset(fr + dx * 0.55f, y0 + dy), Size(dx * 0.45f, y1 - y0 - dy)) }
        val barra = if (o) mezcla(laca, NOCHE, 0.5f) else laca
        listOf(0.52f).forEach { f ->
            val yy = y0 + (y1 - y0) * f
            drawLine(barra, Offset(fr, yy), Offset(fr + dx, yy + dy), 1.2f * u)
        }
        drawPath(poligono(fr, y0, fr + dx, y0 + dy, fr + dx, y0 + dy + w * 0.011f, fr, y0 + w * 0.011f), t.c(laca))
        drawPath(poligono(fr, y1, fr + dx, y1 + dy, fr + dx, y1 + dy + w * 0.011f, fr, y1 + w * 0.011f), t.c(laca))
        drawRect(t.c(laca), Offset(fr + dx - pt * 0.8f, y0 + dy - w * 0.01f), Size(pt * 0.8f, yBase - y0))
        // La boca de arriba, abierta: de día, el hueco en sombra; de noche, la luz que sale.
        drawPath(poligono(fl + pt, y0, fr, y0, fr + dx, y0 + dy, fl + pt + dx, y0 + dy), if (o) Color(0xFFFFE2A0) else mezcla(laca, SOMBRA_MUEBLE, 0.4f))

        // La base: su cara de arriba, su frente con el cajoncito y su costado.
        val bx0 = fl - w * 0.008f; val bx1 = fr + w * 0.008f
        drawPath(poligono(bx0, yBase, bx1, yBase, bx1 + dx, yBase + dy, bx0 + dx, yBase + dy), if (o) mezcla(t.c(lacaLuz), LLAMA, 0.35f) else lacaLuz)
        drawPath(poligono(bx1, yBase, bx1 + dx, yBase + dy, bx1 + dx, pie.y + dy, bx1, pie.y), t.c(mezcla(laca, SUMI, 0.4f)))
        drawRect(t.c(laca), Offset(bx0, yBase), Size(bx1 - bx0, pie.y - yBase))
        drawRect(t.b(0.5f), Offset(bx0, yBase), Size(bx1 - bx0, 1f * u))
        drawRect(t.b(0.16f), Offset(bx0, yBase), Size((bx1 - bx0) * 0.3f, pie.y - yBase))
        val caj = Rect(pie.x - aw * 0.3f, yBase + w * 0.005f, pie.x + aw * 0.3f, pie.y - w * 0.004f)
        drawRect(t.s(0.5f), caj.topLeft, caj.size, style = Stroke(0.8f * u))
        drawRect(t.b(0.25f), Offset(caj.left, caj.bottom), Size(caj.width, 0.7f * u))
        remache(caj.center, 1.5f * u, t)

        // El hueco que deja el panel subido: el fondo, el travesaño, el platillo de aceite y la mecha.
        val hx0 = fl + pt; val hx1 = fr - pt
        val hy0 = y1 - sube
        val plato = Offset(pie.x - w * 0.004f, y1 - w * 0.026f)
        if (!o) {
            drawRect(mezcla(papel, SOMBRA_MUEBLE, 0.5f), Offset(hx0, hy0), Size(hx1 - hx0, sube))
            drawRect(sombraMueble(0.3f), Offset(hx0, hy0), Size(hx1 - hx0, sube * 0.3f))
            drawLine(sombraMueble(0.3f), Offset(pie.x + dx * 0.5f, hy0), Offset(pie.x + dx * 0.5f, y1), 1f * u)
        } else {
            drawRect(Color(0xFFFFE9AE), Offset(hx0, hy0), Size(hx1 - hx0, sube))
            drawRect(Color(0xFFE9A04B).copy(alpha = 0.5f), Offset(hx0, y1 - sube * 0.25f), Size(hx1 - hx0, sube * 0.25f))
        }
        val hierro = if (o) mezcla(HIERRO, NOCHE, 0.3f) else HIERRO
        drawRect(if (o) mezcla(laca, NOCHE, 0.4f) else laca, Offset(hx0, plato.y + w * 0.006f), Size(hx1 - hx0, w * 0.006f))
        drawLine(hierro, Offset(plato.x, plato.y + w * 0.002f), Offset(plato.x, plato.y + w * 0.008f), 1.6f * u)
        drawOval(hierro, Offset(plato.x - w * 0.03f, plato.y - w * 0.006f), Size(w * 0.06f, w * 0.014f))
        drawOval(if (o) Color(0xFFFFD98A) else mezcla(ORO, KEYAKI_OSC, 0.45f), Offset(plato.x - w * 0.024f, plato.y - w * 0.005f), Size(w * 0.048f, w * 0.008f))
        if (!o) drawArc(luzMueble(0.5f), 180f, 80f, false, Offset(plato.x - w * 0.03f, plato.y - w * 0.006f), Size(w * 0.06f, w * 0.014f), style = Stroke(0.8f * u))
        val mecha = Offset(plato.x - w * 0.024f, plato.y - w * 0.004f)
        drawLine(if (o) Color(0xFF7A4A20) else mezcla(WASHI, PAJA, 0.3f), Offset(plato.x + w * 0.006f, plato.y - w * 0.001f), mecha, 1.3f * u, StrokeCap.Round)
        if (!o) drawCircle(SUMI, 1.3f * u, mecha)
        else {
            // La llama.
            drawCircle(Color(0xFFFFF6DC).copy(alpha = 0.55f), w * 0.03f, mecha - Offset(0f, w * 0.014f))
            val llama = Path().apply {
                moveTo(mecha.x, mecha.y + w * 0.002f)
                cubicTo(mecha.x - w * 0.011f, mecha.y - w * 0.008f, mecha.x - w * 0.004f, mecha.y - w * 0.026f, mecha.x + w * 0.002f, mecha.y - w * 0.04f)
                cubicTo(mecha.x + w * 0.006f, mecha.y - w * 0.026f, mecha.x + w * 0.011f, mecha.y - w * 0.008f, mecha.x, mecha.y + w * 0.002f)
                close()
            }
            drawPath(llama, Color(0xFFFFB648))
            clipPath(llama) { drawOval(Color(0xFFFFF8E4), Offset(mecha.x - w * 0.005f, mecha.y - w * 0.024f), Size(w * 0.01f, w * 0.026f)) }
        }

        // El panel de delante, subido: el papel, su enrejado y el remiendo en flor.
        val panel = Rect(hx0, y0 - sube + w * 0.004f, hx1, hy0)
        if (!o) {
            // Papel opaco, más claro que el barro: sin reflejos (no es vidrio), solo un poco de sombra hacia la derecha.
            drawRect(papel, panel.topLeft, panel.size)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.1f)), panel.left + panel.width * 0.4f, panel.right), panel.topLeft, panel.size)
            // El hollín de tantas noches, que tiñe el papel por arriba, y el platillo que se trasluce abajo.
            drawRect(Brush.verticalGradient(listOf(mezcla(SUGI, PAJA, 0.4f).copy(alpha = 0.32f), Color.Transparent), panel.top, panel.top + panel.height * 0.4f), panel.topLeft, Size(panel.width, panel.height * 0.4f))
            drawOval(sombraMueble(0.1f), Offset(panel.center.x - panel.width * 0.3f, panel.bottom - panel.height * 0.12f), Size(panel.width * 0.6f, panel.height * 0.06f))
            // Lo que se trasluce: el poste de atrás y su travesaño, apenas; y la fibra del papel, a lo alto.
            drawRect(sombraMueble(0.07f), Offset(panel.left + dx * 0.6f, panel.top + panel.height * 0.2f), Size(pt, panel.height * 0.8f))
            drawRect(sombraMueble(0.05f), Offset(panel.left, panel.top + panel.height * 0.62f), Size(panel.width, pt * 0.8f))
            for (j in 0 until 9) {
                val x = panel.left + panel.width * (0.06f + 0.1f * j + 0.04f * azar(6402, j))
                drawLine(mezcla(PAJA, SUGI, 0.2f).copy(alpha = 0.12f), Offset(x, panel.top + panel.height * azar(6403, j) * 0.3f), Offset(x + 0.6f * u, panel.bottom - panel.height * azar(6404, j) * 0.3f), 0.6f * u)
            }
        } else {
            val fuego = Offset(mecha.x, mecha.y - w * 0.02f)
            drawRect(Brush.radialGradient(0f to Color(0xFFFFF3CC), 0.3f to Color(0xFFFFDF95), 0.65f to Color(0xFFF7BC62), 1f to Color(0xFFE39A48), center = fuego, radius = w * 0.24f), panel.topLeft, panel.size)
            // La sombra de los postes, a los lados.
            drawRect(Color(0xFF9A5220).copy(alpha = 0.3f), panel.topLeft, Size(w * 0.01f, panel.height))
            drawRect(Color(0xFF9A5220).copy(alpha = 0.3f), Offset(panel.right - w * 0.01f, panel.top), Size(w * 0.01f, panel.height))
        }
        val reja = if (o) Color(0xFF3A2010) else laca
        val penumbra = Color(0xFF9A5220).copy(alpha = 0.28f)
        listOf(0.52f).forEach { f ->
            val yy = panel.top + panel.height * f
            if (o) drawRect(penumbra, Offset(panel.left, yy - 2.2f * u), Size(panel.width, 4.4f * u))
            drawRect(reja, Offset(panel.left, yy - 0.7f * u), Size(panel.width, 1.4f * u))
            if (!o) drawRect(sombraMueble(0.18f), Offset(panel.left, yy + 0.7f * u), Size(panel.width, 1f * u))
        }
        drawRect(reja, panel.topLeft, panel.size, style = Stroke(1.8f * u))
        // El remiendo: un papel recortado en flor de ciruelo, pegado sobre un roto.
        val rem = Offset(panel.left + panel.width * 0.73f, panel.top + panel.height * 0.38f)
        for (j in 0 until 5) {
            val a = -1.5708f + j * 1.2566f
            drawCircle(if (o) Color(0xFFE9A85A).copy(alpha = 0.75f) else mezcla(WASHI, LUZ_MUEBLE, 0.5f), w * 0.0058f, Offset(rem.x + w * 0.006f * cos(a), rem.y + w * 0.006f * sin(a)))
        }
        drawCircle(if (o) Color(0xFFD9944A).copy(alpha = 0.8f) else sombraMueble(0.12f), w * 0.003f, rem)

        // Los postes de delante y el travesaño de abajo.
        listOf(fl, fr - pt).forEachIndexed { i, x ->
            val poste = Rect(x, asaY, x + pt, yBase)
            drawRect(t.c(laca), poste.topLeft, poste.size)
            if (!o) drawRect(luzMueble(0.4f), poste.topLeft, Size(1.1f * u, poste.height))
            else drawRect(LLAMA.copy(alpha = 0.55f), Offset(if (i == 0) poste.right - 1f * u else poste.left, y0 - sube), Size(1f * u, y1 - y0 + sube))
            drawRect(t.s(0.3f), Offset(poste.right - 1.2f * u, poste.top), Size(1.2f * u, poste.height))
            // El remate del poste.
            drawRect(t.c(mezcla(laca, HIERRO_LUZ, 0.2f)), Offset(x - 1f * u, asaY - w * 0.004f), Size(pt + 2f * u, w * 0.005f))
        }
        // El asa de arriba, de poste a poste, para llevarla de un cuarto a otro, con su anilla de hierro.
        val asa = Rect(fl, asaY + w * 0.006f, fr, asaY + w * 0.015f)
        drawRect(t.s(0.3f), Offset(asa.left + 1.2f * u, asa.bottom), Size(asa.width, 1.4f * u))
        drawRect(t.c(laca), asa.topLeft, asa.size)
        drawRect(if (o) LLAMA.copy(alpha = 0.35f) else luzMueble(0.4f), asa.topLeft, Size(asa.width, 0.9f * u))
        val anilla = Offset(pie.x, asa.top - w * 0.006f)
        drawCircle(t.s(0.3f), w * 0.0075f, anilla + Offset(1.2f * u, 1.2f * u), style = Stroke(1.4f * u))
        drawCircle(t.c(HIERRO), w * 0.0075f, anilla, style = Stroke(1.5f * u))
        drawArc(t.b(0.5f), 160f, 90f, false, anilla - Offset(w * 0.0075f, w * 0.0075f), Size(w * 0.015f, w * 0.015f), style = Stroke(0.6f * u))
        drawRect(t.c(laca), Offset(fl, y1), Size(aw, w * 0.011f))
        drawRect(if (o) LLAMA.copy(alpha = 0.6f) else luzMueble(0.4f), Offset(fl + pt, y1), Size(aw - 2 * pt, 1f * u))
        drawRect(t.s(0.35f), Offset(fl, y1 + w * 0.011f), Size(aw, 1.2f * u))
        // El desgaste de la laca en la base, donde se la agarra.
        drawOval(t.c(lacaLuz).copy(alpha = 0.5f), Offset(bx0 + w * 0.004f, yBase + w * 0.004f), Size(w * 0.02f, w * 0.008f))
    }

    /** Una flor de ciruelo: cinco pétalos redondos (más claros los que miran a la luz), el corazón dorado y sus estambres. */
    private fun DrawScope.flor(c: Offset, r: Float, giro: Float, t: Tono) {
        for (p in 0 until 5) {
            val a = giro + p * 1.2566f
            val claro = cos(a) + sin(a) < 0.2f
            drawCircle(t.c(if (claro) mezcla(BERMELLON, WASHI, 0.24f) else BERMELLON), r * 0.54f, Offset(c.x + r * 0.6f * cos(a), c.y + r * 0.6f * sin(a)))
        }
        drawCircle(t.c(mezcla(BERMELLON, KEYAKI_OSC, 0.4f)), r * 0.3f, c)
        for (p in 0 until 5) {
            val a = giro + 0.6f + p * 1.2566f
            drawCircle(t.c(mezcla(ORO, WASHI, 0.4f)), r * 0.11f, Offset(c.x + r * 0.34f * cos(a), c.y + r * 0.34f * sin(a)))
        }
    }

    /**
     * El florero de bambú cortado (su nudo, su fibra, una raja) y la rama de ciruelo rojo: tramos quebrados cada vez más
     * finos, los brotes rectos del año, flores abiertas y capullos; dos pétalos caídos en la tapa.
     */
    private fun DrawScope.ciruelo(pie: Offset, sup: Float, frente: Float) {
        val u = density
        val w = anchoW
        val bw = w * 0.05f; val bh = w * 0.14f
        val tubo = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        val t = tn(Offset(pie.x, pie.y - bh * 0.6f))
        val tr = tn(Offset(pie.x - w * 0.04f, tubo.top - w * 0.16f))
        val boca = Offset(pie.x - bw * 0.08f, tubo.top + w * 0.004f)
        val y = tubo.top
        val tronco = listOf(boca, Offset(pie.x - w * 0.026f, y - w * 0.058f), Offset(pie.x + w * 0.014f, y - w * 0.112f), Offset(pie.x - w * 0.046f, y - w * 0.168f),
            Offset(pie.x - w * 0.074f, y - w * 0.226f), Offset(pie.x - w * 0.142f, y - w * 0.256f))
        val brotes = listOf(tronco[2] to Offset(pie.x + w * 0.07f, y - w * 0.255f), tronco[3] to Offset(pie.x - w * 0.018f, y - w * 0.296f),
            tronco[1] to Offset(pie.x - w * 0.082f, y - w * 0.098f), tronco[4] to Offset(pie.x - w * 0.108f, y - w * 0.312f), tronco[2] to Offset(pie.x + w * 0.062f, y - w * 0.128f))
        fun rama(col: Color, luz: Color?) {
            tronco.zipWithNext().forEachIndexed { k, (a, b) ->
                val g = (4.2f - k * 0.62f) * u
                drawLine(col, a, b, g, StrokeCap.Round)
                if (luz != null) drawLine(luz, a - Offset(g * 0.3f, g * 0.1f), b - Offset(g * 0.3f, g * 0.1f), g * 0.3f, StrokeCap.Round)
            }
            brotes.forEachIndexed { k, (a, b) -> drawLine(col, a, b, (1.5f - 0.12f * k) * u, StrokeCap.Round) }
        }
        // Las sombras en la pared: la del tubo y la de la rama.
        sombraEnPared(Offset(pie.x, y), sup, 0.2f) { col -> drawRect(col, tubo.topLeft, tubo.size) }
        sombraEnPared(Offset(pie.x - w * 0.04f, y - w * 0.16f), sup, 0.17f) { col -> rama(col, null) }
        rasante(tubo.left, tubo.right, pie.y, w * 0.07f, t, 0.2f)
        contacto(Offset(pie.x + w * 0.004f, pie.y), bw * 1.3f, w * 0.008f, 0.4f)
        // La rama, por detrás de la boca.
        rama(tr.c(mezcla(SUGI, SUMI, 0.3f)), tr.b(0.4f))
        // El tubo de bambú.
        val bambu = mezcla(BAMBU, PAJA, 0.25f)
        drawRect(t.c(bambu), tubo.topLeft, tubo.size)
        clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) {
            cilindro(tubo, t, 1.3f)
            for (j in 0 until 6) drawLine(t.s(0.1f), Offset(tubo.left + bw * (0.12f + 0.15f * j), tubo.top), Offset(tubo.left + bw * (0.12f + 0.15f * j), tubo.bottom), 0.5f * u)
            // El nudo: un anillo que sobresale.
            val ny = tubo.top + bh * 0.62f
            drawRect(t.s(0.35f), Offset(tubo.left, ny + 1.6f * u), Size(bw, 1.2f * u))
            drawRect(t.c(mezcla(bambu, SUGI, 0.35f)), Offset(tubo.left, ny), Size(bw, 1.6f * u))
            drawRect(t.b(0.4f), Offset(tubo.left, ny - 1f * u), Size(bw, 1f * u))
            // La mancha del agua, abajo, y la raja que baja de la boca.
            drawRect(t.c(mezcla(bambu, SUGI, 0.5f)).copy(alpha = 0.35f), Offset(tubo.left, tubo.bottom - bh * 0.1f), Size(bw, bh * 0.1f))
            drawLine(t.s(0.45f), Offset(tubo.left + bw * 0.68f, tubo.top), Offset(tubo.left + bw * 0.64f, tubo.top + bh * 0.4f), 0.7f * u)
        }
        // La boca: el grosor de la caña y el hueco.
        drawOval(t.c(mezcla(bambu, LUZ_MUEBLE, 0.3f)), Offset(tubo.left, tubo.top - w * 0.006f), Size(bw, w * 0.012f))
        drawOval(t.c(mezcla(SUGI, SUMI, 0.5f)), Offset(tubo.left + bw * 0.12f, tubo.top - w * 0.004f), Size(bw * 0.76f, w * 0.008f))
        drawLine(tr.c(mezcla(SUGI, SUMI, 0.3f)), boca, tronco[1], 4.2f * u, StrokeCap.Round)
        // Las flores y los capullos.
        fun en(k: Int, f: Float): Offset { val (a, b) = if (k < 10) tronco[k] to tronco[k + 1] else brotes[k - 10]; return Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f) }
        listOf(Triple(1, 0.7f, 0.2f), Triple(2, 0.55f, 1.1f), Triple(3, 0.9f, 0.5f), Triple(4, 0.6f, 2f), Triple(10, 0.5f, 0.9f), Triple(11, 0.62f, 1.6f), Triple(13, 0.5f, 0.3f), Triple(12, 0.75f, 2.4f)).forEachIndexed { i, (k, f, giro) ->
            val p = en(k, f) + Offset(w * 0.006f * (if (i % 2 == 0) -1f else 1f), -w * 0.004f)
            flor(p, w * (0.0115f + 0.002f * (i % 3)), giro, tn(p))
        }
        listOf(4 to 0.98f, 10 to 0.96f, 11 to 0.97f, 13 to 0.95f, 12 to 1f, 10 to 0.78f, 14 to 0.95f, 13 to 0.78f, 0 to 0.75f, 14 to 0.6f).forEach { (k, f) ->
            val p = en(k, f)
            val tt = tn(p)
            drawCircle(tt.c(mezcla(SUGI, BAMBU, 0.4f)), w * 0.0036f, p + Offset(0f, w * 0.003f))
            drawCircle(tt.c(BERMELLON), w * 0.0042f, p)
            drawCircle(tt.b(0.4f), w * 0.0014f, p - Offset(w * 0.0015f, w * 0.0015f))
        }
        // Dos pétalos caídos sobre la tapa.
        listOf(Offset(pie.x - w * 0.06f, pie.y + (frente - sup) * 0.2f), Offset(pie.x - w * 0.105f, pie.y + (frente - sup) * 0.48f)).forEach { p ->
            val tt = tn(p)
            drawOval(tt.s(0.25f), Offset(p.x - w * 0.004f, p.y - w * 0.001f), Size(w * 0.011f, w * 0.005f))
            drawOval(tt.c(mezcla(BERMELLON, WASHI, 0.2f)), Offset(p.x - w * 0.005f, p.y - w * 0.0025f), Size(w * 0.01f, w * 0.005f))
        }
    }

    /**
     * La tetera de hierro (tetsubin) sobre su rodete de paja: el cuerpo achatado con sus granos (arare), la tapa con su pomo,
     * el pico y el asa alta; el lado de la luz gris, el otro perdido.
     */
    private fun DrawScope.tetsubin(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val bw = w * 0.108f; val bh = w * 0.066f
        val t = tn(Offset(pie.x, pie.y - bh * 0.6f))
        val fe = mezcla(HIERRO, KEYAKI_OSC, 0.2f)
        val cy = pie.y - w * 0.006f
        val cuerpo = Path().apply {
            moveTo(pie.x - bw * 0.3f, cy)
            cubicTo(pie.x - bw * 0.6f, cy - bh * 0.08f, pie.x - bw * 0.58f, cy - bh * 0.74f, pie.x - bw * 0.25f, cy - bh * 0.96f)
            lineTo(pie.x + bw * 0.25f, cy - bh * 0.96f)
            cubicTo(pie.x + bw * 0.58f, cy - bh * 0.74f, pie.x + bw * 0.6f, cy - bh * 0.08f, pie.x + bw * 0.3f, cy)
            close()
        }
        val pico = poligono(pie.x + bw * 0.42f, cy - bh * 0.42f, pie.x + bw * 0.7f, cy - bh * 0.98f, pie.x + bw * 0.78f, cy - bh * 0.92f, pie.x + bw * 0.5f, cy - bh * 0.2f)
        val asa = Path().apply {
            moveTo(pie.x - bw * 0.3f, cy - bh * 0.9f)
            cubicTo(pie.x - bw * 0.42f, cy - bh * 2.05f, pie.x + bw * 0.42f, cy - bh * 2.05f, pie.x + bw * 0.3f, cy - bh * 0.9f)
        }
        val tapaR = Rect(pie.x - bw * 0.25f, cy - bh * 1.12f, pie.x + bw * 0.25f, cy - bh * 0.9f)
        sombraEnPared(Offset(pie.x, cy - bh), sup, 0.16f) { col -> drawPath(cuerpo, col); drawPath(asa, col, style = Stroke(w * 0.006f)) }
        // El rodete de paja trenzada.
        val rod = Rect(pie.x - bw * 0.58f, pie.y - w * 0.011f, pie.x + bw * 0.58f, pie.y + w * 0.006f)
        rasante(rod.left, rod.right, pie.y, w * 0.075f, t, 0.22f)
        drawOval(t.s(0.35f), Offset(rod.left + w * 0.006f, rod.top + w * 0.003f), rod.size)
        drawOval(t.c(mezcla(PAJA, KEYAKI, 0.25f)), rod.topLeft, rod.size)
        drawOval(t.s(0.25f), Offset(rod.left + rod.width * 0.08f, rod.top + rod.height * 0.1f), Size(rod.width * 0.84f, rod.height * 0.7f), style = Stroke(0.7f * u))
        drawArc(t.b(0.45f), 150f, 90f, false, rod.topLeft, rod.size, style = Stroke(0.9f * u))
        // El asa, por detrás.
        drawPath(asa, t.c(fe), style = Stroke(w * 0.0065f, cap = StrokeCap.Round))
        translate(-0.8f * u, -0.6f * u) { drawPath(asa, t.b(0.35f), style = Stroke(0.8f * u)) }
        // El pico y el cuerpo.
        drawPath(pico, t.c(fe))
        drawPath(cuerpo, t.c(fe))
        clipPath(cuerpo) {
            val r = Rect(pie.x - bw * 0.6f, cy - bh, pie.x + bw * 0.6f, cy)
            drawRect(t.c(HIERRO_LUZ).copy(alpha = 0.55f), Offset(r.left, r.top), Size(r.width * 0.3f, r.height))
            drawRect(t.c(HIERRO_LUZ).copy(alpha = 0.5f), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.1f, r.height))
            drawRect(t.s(0.3f), Offset(r.left + r.width * 0.66f, r.top), Size(r.width * 0.34f, r.height))
            drawRect(t.s(0.3f), Offset(r.left, cy - bh * 0.22f), Size(r.width, bh * 0.22f))
            // Los granos, en tres hileras.
            for (fila in 0 until 3) {
                val yy = cy - bh * (0.74f - 0.2f * fila)
                val n = 11 + fila
                for (j in 0 until n) {
                    val x = r.left + r.width * (j + 0.5f + 0.5f * (fila % 2)) / (n + 0.5f)
                    val claro = x < pie.x - bw * 0.05f
                    drawCircle(t.s(0.3f), 1.3f * u, Offset(x + 0.6f * u, yy + 0.7f * u))
                    drawCircle(if (claro) t.c(mezcla(HIERRO_LUZ, LUZ_MUEBLE, 0.3f)) else t.c(mezcla(HIERRO, HIERRO_LUZ, 0.35f)), 1.1f * u, Offset(x, yy))
                }
            }
        }
        clipPath(pico) { drawRect(t.c(HIERRO_LUZ).copy(alpha = 0.4f), Offset(pie.x + bw * 0.42f, cy - bh), Size(bw * 0.14f, bh)) }
        // La tapa y su pomo.
        drawOval(t.c(mezcla(fe, HIERRO_LUZ, 0.25f)), tapaR.topLeft, tapaR.size)
        drawArc(t.b(0.5f), 170f, 90f, false, tapaR.topLeft, tapaR.size, style = Stroke(0.9f * u))
        drawRect(t.c(fe), Offset(pie.x - bw * 0.02f, cy - bh * 1.22f), Size(bw * 0.04f, bh * 0.14f))
        drawCircle(t.c(mezcla(fe, HIERRO_LUZ, 0.3f)), bw * 0.05f, Offset(pie.x, cy - bh * 1.26f))
        drawCircle(t.b(0.6f), bw * 0.018f, Offset(pie.x - bw * 0.02f, cy - bh * 1.29f))
        // El filo de la luz en el hombro.
        drawPath(Path().apply { moveTo(pie.x - bw * 0.5f, cy - bh * 0.55f); cubicTo(pie.x - bw * 0.46f, cy - bh * 0.8f, pie.x - bw * 0.36f, cy - bh * 0.92f, pie.x - bw * 0.24f, cy - bh * 0.95f) }, t.b(0.55f), style = Stroke(1f * u, cap = StrokeCap.Round))
    }

    /** La taza de loza blanca con su pincelada de añil y un fondo de té. */
    private fun DrawScope.taza(pie: Offset) {
        val u = density
        val w = anchoW
        val tw = w * 0.036f; val th = w * 0.037f
        val t = tn(Offset(pie.x, pie.y - th / 2))
        val forma = Path().apply {
            moveTo(pie.x - tw * 0.36f, pie.y)
            cubicTo(pie.x - tw * 0.5f, pie.y - th * 0.3f, pie.x - tw * 0.52f, pie.y - th * 0.7f, pie.x - tw * 0.5f, pie.y - th)
            lineTo(pie.x + tw * 0.5f, pie.y - th)
            cubicTo(pie.x + tw * 0.52f, pie.y - th * 0.7f, pie.x + tw * 0.5f, pie.y - th * 0.3f, pie.x + tw * 0.36f, pie.y)
            close()
        }
        rasante(pie.x - tw / 2, pie.x + tw / 2, pie.y, w * 0.04f, t, 0.2f)
        contacto(Offset(pie.x + w * 0.003f, pie.y), tw * 1.2f, w * 0.006f, 0.4f)
        val loza = mezcla(WASHI, Color(0xFFBCCADC), 0.25f)
        drawPath(forma, t.c(loza))
        clipPath(forma) {
            val r = Rect(pie.x - tw / 2, pie.y - th, pie.x + tw / 2, pie.y)
            // La pincelada de añil: una banda y tres hojas de bambú.
            drawRect(t.c(ANIL).copy(alpha = 0.85f), Offset(r.left, r.top + th * 0.2f), Size(tw, 1.1f * u))
            for (j in 0 until 3) drawLine(t.c(ANIL).copy(alpha = 0.8f), Offset(r.left + tw * (0.3f + 0.16f * j), r.top + th * 0.42f), Offset(r.left + tw * (0.22f + 0.2f * j), r.top + th * 0.8f), 1.3f * u, StrokeCap.Round)
            cilindro(r, t, 1.1f)
        }
        drawOval(t.c(mezcla(loza, LUZ_MUEBLE, 0.4f)), Offset(pie.x - tw / 2, pie.y - th - w * 0.004f), Size(tw, w * 0.008f))
        drawOval(t.c(mezcla(BAMBU, KEYAKI, 0.45f)), Offset(pie.x - tw * 0.42f, pie.y - th - w * 0.0028f), Size(tw * 0.84f, w * 0.0058f))
        drawArc(t.b(0.5f), 190f, 60f, false, Offset(pie.x - tw * 0.42f, pie.y - th - w * 0.0028f), Size(tw * 0.84f, w * 0.0058f), style = Stroke(0.6f * u))
    }

    /**
     * La piedra de tinta (suzuri) con su pozo lleno, el pincel apoyado en el borde, la barra de sumi gastada por una punta y,
     * al lado, un tanzaku a medio escribir: tres trazos de pincel, no palabras.
     */
    private fun DrawScope.suzuri(c: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val sw = w * 0.092f; val prof = w * 0.03f; val canto = w * 0.009f
        val x0 = c.x - sw / 2; val x1 = c.x + sw / 2
        val ya = c.y - canto - prof
        val piedra = mezcla(mezcla(SUMI, ANIL, 0.3f), WASHI, 0.16f)
        // El tanzaku, a la izquierda, tumbado y un poco torcido.
        val tz = poligono(x0 - w * 0.075f, c.y - w * 0.002f, x0 - w * 0.027f, c.y + w * 0.002f, x0 - w * 0.014f, ya - w * 0.004f, x0 - w * 0.058f, ya - w * 0.008f)
        translate(1.6f * u, 1.4f * u) { drawPath(tz, t.s(0.3f)) }
        drawPath(tz, t.c(WASHI))
        clipPath(tz) {
            drawRect(t.c(ANIL).copy(alpha = 0.3f), Offset(x0 - w * 0.08f, ya - w * 0.01f), Size(w * 0.08f, w * 0.012f))
            listOf(0.3f to 0.75f, 0.5f to 0.55f, 0.68f to 0.3f).forEach { (fx, largo) ->
                val a = Offset(x0 - w * 0.06f + w * 0.045f * fx, ya + w * 0.006f)
                val q = Path().apply {
                    moveTo(a.x, a.y)
                    cubicTo(a.x - w * 0.004f, a.y + prof * 0.3f * largo, a.x + w * 0.004f, a.y + prof * 0.6f * largo, a.x - w * 0.003f, a.y + prof * largo)
                }
                drawPath(q, t.c(SUMI).copy(alpha = 0.85f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            }
        }
        // La piedra: su sombra rasante, la cara de arriba con el borde, el llano y el pozo; el canto de delante.
        rasante(x0, x1, c.y, w * 0.05f, t, 0.22f)
        val arriba = poligono(x0, c.y - canto, x1, c.y - canto, x1 - sw * 0.04f, ya, x0 + sw * 0.04f, ya)
        drawPath(arriba, t.c(mezcla(piedra, WASHI, 0.12f)))
        drawPath(poligono(x0 + sw * 0.07f, c.y - canto - prof * 0.14f, x1 - sw * 0.07f, c.y - canto - prof * 0.14f, x1 - sw * 0.1f, ya + prof * 0.14f, x0 + sw * 0.1f, ya + prof * 0.14f), t.c(mezcla(piedra, SUMI, 0.45f)))
        val pozo = Rect(x0 + sw * 0.1f, ya + prof * 0.2f, x0 + sw * 0.4f, c.y - canto - prof * 0.2f)
        drawOval(t.c(SUMI), pozo.topLeft, pozo.size)
        drawArc(t.b(0.7f), 190f, 70f, false, Offset(pozo.left + pozo.width * 0.1f, pozo.top + pozo.height * 0.12f), Size(pozo.width * 0.8f, pozo.height * 0.76f), style = Stroke(0.9f * u))
        drawRect(t.c(mezcla(piedra, SUMI, 0.3f)), Offset(x0, c.y - canto), Size(sw, canto))
        drawRect(t.b(0.4f), Offset(x0, c.y - canto), Size(sw, 0.9f * u))
        drawRect(t.s(0.3f), Offset(x0 + sw * 0.7f, c.y - canto), Size(sw * 0.3f, canto))
        // La barra de sumi, de pie contra el borde derecho, con su marca dorada y la punta gastada.
        val bx = x1 + w * 0.014f
        rotate(-9f, Offset(bx, c.y)) {
            drawRect(t.s(0.3f), Offset(bx + 1.5f * u, c.y - w * 0.006f), Size(w * 0.04f, w * 0.01f))
            drawRect(t.c(SUMI), Offset(bx, c.y - w * 0.012f), Size(w * 0.038f, w * 0.011f))
            drawRect(t.b(0.4f), Offset(bx, c.y - w * 0.012f), Size(w * 0.038f, 0.9f * u))
            drawRect(t.c(ORO).copy(alpha = 0.8f), Offset(bx + w * 0.014f, c.y - w * 0.009f), Size(w * 0.012f, w * 0.005f), style = Stroke(0.7f * u))
            drawRect(t.c(HIERRO_LUZ), Offset(bx + w * 0.034f, c.y - w * 0.012f), Size(w * 0.004f, w * 0.011f))
        }
        // El pincel, apoyado en el borde: el mango de bambú hacia la derecha, la punta mojada sobre el llano.
        val punta = Offset(x0 + sw * 0.5f, c.y - canto - prof * 0.42f)
        val virola = Offset(x0 + sw * 0.76f, c.y - canto - prof * 0.66f)
        val cabo = Offset(x1 + w * 0.092f, ya - w * 0.014f)
        drawLine(t.s(0.3f), virola + Offset(2f * u, 3f * u), cabo + Offset(2f * u, w * 0.012f), 2.4f * u, StrokeCap.Round)
        drawLine(t.c(mezcla(BAMBU, PAJA, 0.5f)), virola, cabo, 3.2f * u, StrokeCap.Round)
        drawLine(t.b(0.5f), virola - Offset(0f, 1f * u), cabo - Offset(0f, 1f * u), 0.8f * u)
        drawLine(t.c(SUGI), virola, virola + (cabo - virola) * 0.1f, 3.4f * u)
        drawPath(poligono(virola.x, virola.y - 1.7f * u, virola.x + 0.5f * u, virola.y + 1.9f * u, punta.x, punta.y + 0.6f * u), t.c(SUMI))
        drawCircle(t.c(BERMELLON), 1.4f * u, cabo)
    }

    /**
     * El fardo envuelto en furoshiki índigo: la tela tirante hacia el nudo, con sus pliegues, las esquinas caídas por el peso,
     * el nudo con sus dos orejas y el desteñido de los dobleces.
     */
    private fun DrawScope.furoshiki(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val fw = w * 0.172f; val fh = w * 0.1f
        val x0 = pie.x - fw / 2; val x1 = pie.x + fw / 2
        val top = pie.y - fh
        val nudo = Offset(pie.x - fw * 0.05f, top - w * 0.004f)
        val t = tn(Offset(pie.x, pie.y - fh / 2))
        val bulto = Path().apply {
            moveTo(x0 + fw * 0.02f, pie.y)
            cubicTo(x0 - fw * 0.035f, pie.y - fh * 0.2f, x0 + fw * 0.0f, pie.y - fh * 0.72f, x0 + fw * 0.13f, top + fh * 0.16f)
            cubicTo(x0 + fw * 0.26f, top + fh * 0.02f, nudo.x - fw * 0.1f, top + fh * 0.08f, nudo.x, top + fh * 0.03f)
            cubicTo(nudo.x + fw * 0.1f, top + fh * 0.08f, x1 - fw * 0.26f, top + fh * 0.0f, x1 - fw * 0.12f, top + fh * 0.17f)
            cubicTo(x1 + fw * 0.0f, pie.y - fh * 0.7f, x1 + fw * 0.035f, pie.y - fh * 0.2f, x1 - fw * 0.02f, pie.y)
            close()
        }
        val orejaI = Path().apply {
            moveTo(nudo.x - fw * 0.02f, nudo.y + fh * 0.04f)
            cubicTo(nudo.x - fw * 0.1f, nudo.y - fh * 0.1f, nudo.x - fw * 0.2f, nudo.y - fh * 0.42f, nudo.x - fw * 0.26f, nudo.y - fh * 0.4f)
            cubicTo(nudo.x - fw * 0.26f, nudo.y - fh * 0.2f, nudo.x - fw * 0.16f, nudo.y + fh * 0.08f, nudo.x - fw * 0.02f, nudo.y + fh * 0.12f)
            close()
        }
        val orejaD = Path().apply {
            moveTo(nudo.x + fw * 0.02f, nudo.y + fh * 0.02f)
            cubicTo(nudo.x + fw * 0.12f, nudo.y - fh * 0.3f, nudo.x + fw * 0.24f, nudo.y - fh * 0.26f, nudo.x + fw * 0.3f, nudo.y + fh * 0.02f)
            cubicTo(nudo.x + fw * 0.2f, nudo.y - fh * 0.02f, nudo.x + fw * 0.12f, nudo.y + fh * 0.1f, nudo.x + fw * 0.03f, nudo.y + fh * 0.13f)
            close()
        }
        sombraEnPared(Offset(pie.x, top), sup, 0.16f) { col -> drawPath(bulto, col); drawPath(orejaI, col); drawPath(orejaD, col) }
        rasante(x0, x1, pie.y, w * 0.06f, t, 0.24f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), fw * 1.1f, w * 0.01f, 0.42f)
        val tela = mezcla(ANIL, Color(0xFF3A5A86), 0.35f)
        drawPath(orejaI, t.c(mezcla(tela, SUMI, 0.2f)))
        drawPath(orejaD, t.c(mezcla(tela, SUMI, 0.3f)))
        drawPath(bulto, t.c(tela))
        clipPath(bulto) {
            // El lado de la luz, el lado en sombra y lo de abajo, que cuelga.
            drawPath(poligono(x0 - fw * 0.05f, pie.y, x0 - fw * 0.05f, top, x0 + fw * 0.34f, top, x0 + fw * 0.16f, pie.y), t.b(0.2f))
            drawPath(poligono(x1 + fw * 0.05f, pie.y, x1 + fw * 0.05f, top, x1 - fw * 0.22f, top, x1 - fw * 0.32f, pie.y), t.s(0.3f))
            drawRect(t.s(0.2f), Offset(x0, pie.y - fh * 0.16f), Size(fw, fh * 0.16f))
            // Los pliegues tirantes, del nudo a las esquinas: cada uno con su valle oscuro y su lomo claro.
            listOf(-0.46f to 0.95f, -0.3f to 1f, -0.12f to 0.9f, 0.1f to 0.95f, 0.28f to 1f, 0.44f to 0.9f).forEachIndexed { i, (dx, largo) ->
                val fin = Offset(pie.x + fw * dx * 1.05f, top + fh * largo)
                val q = Path().apply {
                    moveTo(nudo.x + fw * dx * 0.12f, nudo.y + fh * 0.1f)
                    cubicTo(nudo.x + fw * dx * 0.5f, nudo.y + fh * 0.2f, fin.x - fw * dx * 0.06f, fin.y - fh * 0.5f, fin.x, fin.y)
                }
                drawPath(q, t.s(0.34f), style = Stroke((1.6f + 0.5f * (i % 2)) * u, cap = StrokeCap.Round))
                translate(-1.6f * u, 0f) { drawPath(q, t.b(0.3f), style = Stroke(0.9f * u, cap = StrokeCap.Round)) }
            }
            // El desteñido de los dobleces y un remiendo cosido.
            drawOval(t.c(mezcla(tela, WASHI, 0.35f)).copy(alpha = 0.45f), Offset(x0 + fw * 0.06f, top + fh * 0.3f), Size(fw * 0.13f, fh * 0.34f))
            drawOval(t.c(mezcla(tela, WASHI, 0.35f)).copy(alpha = 0.3f), Offset(x0 + fw * 0.52f, top + fh * 0.56f), Size(fw * 0.16f, fh * 0.2f))
            val rem = Rect(x0 + fw * 0.6f, top + fh * 0.28f, x0 + fw * 0.74f, top + fh * 0.5f)
            rotate(-8f, rem.center) {
                drawRect(t.c(mezcla(ANIL, SUMI, 0.25f)), rem.topLeft, rem.size)
                drawRect(t.c(mezcla(WASHI, tela, 0.55f)).copy(alpha = 0.5f), rem.topLeft, rem.size, style = Stroke(0.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.6f * u))))
            }
        }
        // Las orejas del nudo, con su lomo de luz, y el nudo.
        clipPath(orejaI) { drawRect(t.b(0.3f), Offset(nudo.x - fw * 0.3f, nudo.y - fh * 0.45f), Size(fw * 0.14f, fh * 0.6f)) }
        clipPath(orejaD) { drawRect(t.s(0.25f), Offset(nudo.x + fw * 0.18f, nudo.y - fh * 0.3f), Size(fw * 0.14f, fh * 0.5f)) }
        drawPath(orejaI, t.s(0.3f), style = Stroke(0.6f * u))
        drawPath(orejaD, t.s(0.3f), style = Stroke(0.6f * u))
        drawOval(t.s(0.3f), Offset(nudo.x - fw * 0.05f + 1.5f * u, nudo.y - fh * 0.06f + 2f * u), Size(fw * 0.11f, fh * 0.2f))
        drawOval(t.c(mezcla(tela, WASHI, 0.08f)), Offset(nudo.x - fw * 0.055f, nudo.y - fh * 0.07f), Size(fw * 0.11f, fh * 0.2f))
        drawArc(t.b(0.5f), 170f, 90f, false, Offset(nudo.x - fw * 0.055f, nudo.y - fh * 0.07f), Size(fw * 0.11f, fh * 0.2f), style = Stroke(0.9f * u))
        drawLine(t.s(0.35f), Offset(nudo.x - fw * 0.01f, nudo.y - fh * 0.05f), Offset(nudo.x + fw * 0.02f, nudo.y + fh * 0.11f), 0.9f * u)
    }

    // ---------------------------------------------------------------- al pie

    /**
     * El hibachi de loza de vidriado oscuro, cortado por el borde: el labio grueso, los chorreones claros del vidriado, la
     * ceniza, tres carbones (de noche, brasas) y los palillos de hierro clavados.
     */
    private fun DrawScope.hibachi(b: Rect, w: Float, alto: Float) {
        val u = density
        val o = esNoche
        val hb = b.height
        val c = Offset(w * BRASA_X, b.top + hb * BRASA_Y)
        val rx = w * 0.11f; val ry = w * 0.03f
        val t = tn(Offset(c.x, c.y + w * 0.05f))
        val loza = mezcla(ANIL, SUMI, 0.5f)
        val cuerpo = Path().apply {
            moveTo(c.x - rx, c.y)
            cubicTo(c.x - rx * 1.12f, c.y + w * 0.04f, c.x - rx * 1.05f, c.y + w * 0.11f, c.x - rx * 0.78f, alto + w * 0.03f)
            lineTo(c.x + rx * 0.78f, alto + w * 0.03f)
            cubicTo(c.x + rx * 1.05f, c.y + w * 0.11f, c.x + rx * 1.12f, c.y + w * 0.04f, c.x + rx, c.y)
            close()
        }
        // Su sombra sobre el tatami y el zócalo.
        drawOval(t.s(0.3f), Offset(c.x - rx * 0.6f, alto - w * 0.03f), Size(rx * 2.4f, w * 0.06f))
        drawPath(cuerpo, t.c(loza))
        clipPath(cuerpo) {
            val r = Rect(c.x - rx * 1.12f, c.y, c.x + rx * 1.12f, alto)
            drawRect(t.c(mezcla(loza, Color(0xFF6E8FB5), 0.5f)).copy(alpha = 0.5f), r.topLeft, Size(r.width * 0.26f, r.height))
            drawRect(t.b(0.3f), Offset(r.left + r.width * 0.1f, r.top), Size(r.width * 0.07f, r.height))
            drawRect(t.s(0.3f), Offset(r.left + r.width * 0.62f, r.top), Size(r.width * 0.38f, r.height))
            // Los chorreones del vidriado, que bajan del labio.
            for (j in 0 until 9) {
                val x = r.left + r.width * (0.06f + 0.105f * j + 0.03f * azar(6501, j))
                val l = w * (0.02f + 0.045f * azar(6502, j))
                drawLine(t.c(mezcla(loza, Color(0xFF9DB6CF), 0.4f)).copy(alpha = 0.45f), Offset(x, c.y + ry * 0.4f), Offset(x + w * 0.002f, c.y + ry * 0.4f + l), (2.2f + 2f * azar(6503, j)) * u, StrokeCap.Round)
            }
            // De noche, el rescoldo que le sube por dentro del labio no se ve por fuera: solo el filo del andon, lejos.
        }
        // El labio y el hueco con la ceniza.
        drawOval(t.c(mezcla(loza, Color(0xFF6E8FB5), 0.3f)), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawArc(t.b(0.5f), 160f, 80f, false, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(1.2f * u))
        val dentro = Rect(c.x - rx * 0.86f, c.y - ry * 0.74f, c.x + rx * 0.86f, c.y + ry * 0.78f)
        val ceniza = mezcla(WASHI, HIERRO_LUZ, 0.4f)
        drawOval(if (o) mezcla(hondo(ceniza), mezcla(ceniza, BRASA, 0.45f), 0.55f) else ceniza, dentro.topLeft, dentro.size)
        clipPath(Path().apply { addOval(dentro) }) {
            drawRect(if (o) NOCHE.copy(alpha = 0.45f) else sombraMueble(0.3f), dentro.topLeft, Size(dentro.width, dentro.height * 0.34f))
            drawRect(if (o) NOCHE.copy(alpha = 0.3f) else sombraMueble(0.14f), Offset(dentro.left, dentro.top), Size(dentro.width * 0.2f, dentro.height))
            // Las rayas que deja el alisador en la ceniza.
            for (j in 0 until 5) drawArc(if (o) NOCHE.copy(alpha = 0.2f) else sombraMueble(0.1f), 20f, 140f, false, Offset(dentro.left + dentro.width * 0.1f * j, dentro.top - dentro.height * 0.3f), Size(dentro.width * (1f - 0.2f * j), dentro.height * 1.1f), style = Stroke(0.6f * u))
        }
        // Los carbones.
        listOf(Triple(-0.2f, 0.1f, 0.2f), Triple(0.08f, -0.05f, 0.17f), Triple(0.02f, 0.3f, 0.15f)).forEachIndexed { i, (dx, dy, tam) ->
            val p = Offset(c.x + rx * dx, c.y + ry * dy)
            val cw = rx * tam; val chh = ry * 0.42f
            val carbon = poligono(p.x - cw, p.y, p.x - cw * 0.7f, p.y - chh, p.x + cw * 0.8f, p.y - chh * 0.9f, p.x + cw, p.y + chh * 0.2f, p.x + cw * 0.5f, p.y + chh, p.x - cw * 0.6f, p.y + chh * 0.9f)
            drawPath(carbon, if (o) Color(0xFF3A1410) else SUMI)
            if (o) {
                clipPath(carbon) {
                    drawOval(BRASA, Offset(p.x - cw * 0.7f, p.y - chh * 0.4f), Size(cw * 1.4f, chh * 1.5f))
                    drawOval(Color(0xFFFFC56E), Offset(p.x - cw * 0.35f, p.y + chh * 0.1f), Size(cw * 0.7f, chh * 0.7f))
                }
                drawPath(carbon, Color(0xFF3A1410), style = Stroke(1f * u))
            } else {
                drawPath(poligono(p.x - cw * 0.7f, p.y - chh, p.x + cw * 0.8f, p.y - chh * 0.9f, p.x + cw * 0.5f, p.y - chh * 0.4f, p.x - cw * 0.5f, p.y - chh * 0.5f), mezcla(WASHI, HIERRO_LUZ, 0.5f).copy(alpha = 0.7f))
                if (i == 0) drawOval(BRASA.copy(alpha = 0.6f), Offset(p.x - cw * 0.3f, p.y + chh * 0.1f), Size(cw * 0.6f, chh * 0.5f))
            }
        }
        // Los palillos de hierro (hibashi), clavados en la ceniza.
        val pal = Offset(c.x + rx * 0.42f, c.y + ry * 0.2f)
        listOf(Offset(w * 0.03f, -w * 0.082f), Offset(w * 0.046f, -w * 0.076f)).forEachIndexed { i, d ->
            val fin = pal + Offset(w * 0.006f * i, 0f) + d
            val tt = tn(fin)
            drawLine(tt.s(0.3f), pal + Offset(w * 0.006f * i + 2f * u, 1f * u), fin + Offset(3f * u, 2f * u), 1.4f * u)
            drawLine(tt.c(mezcla(HIERRO, HIERRO_LUZ, 0.25f)), pal + Offset(w * 0.006f * i, 0f), fin, 1.8f * u, StrokeCap.Round)
            drawLine(tt.b(0.5f), pal + Offset(w * 0.006f * i - 0.6f * u, 0f), fin - Offset(0.6f * u, 0f), 0.6f * u)
            drawCircle(tt.c(mezcla(HIERRO, HIERRO_LUZ, 0.4f)), 1.7f * u, fin)
        }
    }

    // ---------------------------------------------------------------- los cajones

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    /** La madera de un cajón según su tema: 0, keyaki; 1, keyaki viejo (el kiri, 2, va por el sitio). */
    private fun maderaDe(m: Int) = Math.floorMod(m ushr 4, 2)

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este cajón en el mueble (en fracciones del ancho), para saber cuánto le llega del andon y de las brasas.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val t = when {
            !oscuro -> Tono(false, 0f, 0.075f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.03f else 0f))
            c.cols == cols -> tonoEn(centro, Offset(BRASA_X, corona + hueco + c.filas * (altoH + hueco) + base * BRASA_Y))
            else -> Tono(true, 0.55f)
        }
        val m = mezclar(c.semilla)
        val baja = h * SALIDA * ab
        if (c.sinTema) { caja(w, h, c, m, t, baja, ab); return }
        // La clase va por el sitio (nunca dos iguales en una fila, ninguna más de tres veces en ocho); lo demás, por la semilla.
        val clase = if (c.vacio) Math.floorMod(c.fila + c.col, 2) else Math.floorMod(1 + 2 * c.fila + c.col, 3)
        // Un frente de kiri, repuesto, cada tanto (por el sitio, para que se vea); los demás, keyaki nuevo o viejo según la semilla.
        val madera = if (c.vacio) Math.floorMod(c.semilla, 2) else if (Math.floorMod(c.fila * c.cols + c.col, 5) == 3) 2 else maderaDe(m)
        // El entreabierto asoma más cuanto más guarda; los otros, solo al abrirlos.
        val entre = if (!c.vacio && clase == 2) h * (0.12f + 0.013f * c.n.coerceIn(0, 7)) else 0f
        val arriba = entre + baja
        if (arriba > 0.5f) asoman(w, h, arriba, c.n, m, t)
        // El frente: al abrirse baja y se acerca un poco.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.03f * ab)
        if (ab > 0f) drawRect(t.s(0.4f * ab), Offset(f.left + 2f * u, f.bottom - 1f * u), Size(f.width, 1f * u + 5f * u * ab))
        frenteCajon(f, madera, m, t, arriba < 0.5f)
        val cy = h * 0.815f + baja
        when (clase) {
            0 -> {
                val l = h * 0.15f
                esquinera(Offset(f.left, f.top), 1f, 1f, l, t); esquinera(Offset(f.right, f.top), -1f, 1f, l, t)
                esquinera(Offset(f.left, f.bottom), 1f, -1f, l, t); esquinera(Offset(f.right, f.bottom), -1f, -1f, l, t)
                warabite(Offset(w * 0.23f, cy - h * 0.045f), w * 0.2f, h, t)
                warabite(Offset(w * 0.77f, cy - h * 0.045f), w * 0.2f, h, t)
                bocallave(Offset(w * 0.5f, cy), h * 0.07f, t)
            }
            1 -> {
                faja(Rect(f.left, f.top, f.left + w * 0.03f, f.bottom), t)
                faja(Rect(f.right - w * 0.03f, f.top, f.right, f.bottom), t)
                cerradura(Offset(w * 0.5f, cy), h * 0.125f, t)
            }
            else -> {
                mariposa(Offset(w * 0.5f, cy), h * 0.115f, t)
                argolla(Offset(w * 0.19f, cy), h * 0.06f, t)
                argolla(Offset(w * 0.81f, cy), h * 0.06f, t)
            }
        }
        if (c.vacio) {
            // Sin tema todavía: la huella más clara de un papel que hubo, con restos de engrudo en las esquinas.
            val r = Rect(w * 0.2f, h * 0.27f + baja, w * 0.8f, h * 0.6f + baja)
            drawRect(t.b(0.08f), r.topLeft, r.size)
            drawRect(t.s(0.12f), r.topLeft, r.size, style = Stroke(0.7f * u))
            listOf(r.topLeft, Offset(r.right - w * 0.03f, r.top), Offset(r.left, r.bottom - h * 0.04f)).forEach { p -> drawRect(t.c(WASHI).copy(alpha = 0.35f), p, Size(w * 0.03f, h * 0.04f)) }
            filo(f, t)
            return
        }
        val paleta = Math.floorMod(m ushr 9, TANZAKU.size)
        when (clase) {
            0 -> { colgados(f, w, h, c.n, m, paleta, t); papel(w, h, baja, m, t, pegado = true) }
            1 -> { metidos(w, h, baja, c.n, m, paleta, t); papel(w, h, baja, m, t, pegado = false); cinta(f, w, h, baja, c.n, m, paleta, t) }
            else -> papel(w, h, baja, m, t, pegado = true)
        }
        filo(f, t)
    }

    /** El filo cálido del andon en el canto de arriba de un frente, de noche: tanto más vivo cuanto más le llega. */
    private fun DrawScope.filo(f: Rect, t: Tono) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(t.calor().copy(alpha = (0.8f * t.k).coerceAtMost(0.7f)), f.topLeft, Size(f.width, 1.4f * u))
        drawRect(t.calor().copy(alpha = (0.28f * t.k).coerceAtMost(0.28f)), Offset(f.left, f.top + 1.4f * u), Size(f.width, 3f * u))
        drawRect(t.calor().copy(alpha = (0.5f * t.k).coerceAtMost(0.45f)), f.topLeft, Size(1.2f * u, f.height))
    }

    /**
     * El frente de un cajón: la madera (keyaki, keyaki viejo o kiri) con su veta —en el keyaki, las llamas anchas de la tabla
     * cortada al hilo—, la laca más oscura en los cantos, la sombra del travesaño de arriba y el bisel.
     */
    private fun DrawScope.frenteCajon(f: Rect, madera: Int, m: Int, t: Tono, bajoTravesano: Boolean) {
        val u = density
        val col = when (madera) { 0 -> mezcla(KEYAKI, KEYAKI_LUZ, 0.3f); 1 -> mezcla(KEYAKI, KEYAKI_OSC, 0.42f); else -> mezcla(KIRI, KEYAKI_LUZ, 0.28f) }
        val veta = when (madera) { 0 -> KEYAKI_OSC; 1 -> mezcla(KEYAKI_OSC, SUMI, 0.4f); else -> mezcla(KIRI_VETA, KEYAKI, 0.3f) }
        val vetaC = if (t.o) NOCHE else veta
        drawRect(t.c(col), f.topLeft, f.size)
        clipRect(f.left, f.top, f.right, f.bottom) {
            // La luz que entra por la izquierda y la sombra de la derecha, en escalones suaves (sin cortes duros).
            listOf(0.55f, 0.38f, 0.22f).forEach { a -> drawRect(t.b(0.045f), f.topLeft, Size(f.width * a, f.height)) }
            listOf(0.3f, 0.18f, 0.08f).forEach { a -> drawRect(t.s(0.04f), Offset(f.right - f.width * a, f.top), Size(f.width * a, f.height)) }
            if (madera < 2) {
                // Las llamas de la veta: arcos anidados que se abren hacia un lado, cada uno con su lomo claro.
                val haciaDerecha = Math.floorMod(m ushr 7, 2) == 0
                val yc = f.top + f.height * (0.3f + 0.4f * azar(m, 1))
                val x0 = if (haciaDerecha) f.left + f.width * (0.08f + 0.3f * azar(m, 2)) else f.right - f.width * (0.08f + 0.3f * azar(m, 2))
                val s = if (haciaDerecha) 1f else -1f
                val apaga = if (t.o) 0.5f else 1f
                // Primero la veta larga y ondulada de toda la tabla; encima, las llamas, pocas y desparejas.
                vetas(f, vetaC.copy(alpha = 0.24f * apaga), m, 0.45f, grosor = 0.8f, ondulacion = 1.8f)
                for (j in 0 until 5) {
                    val punta = x0 + s * f.width * (0.07f * j + 0.04f * azar(m, 10 + j))
                    val hh = f.height * (0.14f + 0.2f * j) * (0.85f + 0.3f * azar(m, 40 + j))
                    val fin = punta + s * f.width * (0.55f + 0.35f * azar(m, 50 + j))
                    val ya = yc - hh * (1.2f + 0.5f * azar(m, 60 + j)); val yb = yc + hh * (1.2f + 0.5f * azar(m, 70 + j))
                    val bulto = f.height * 0.06f * (azar(m, 80 + j) - 0.5f)
                    val q = Path().apply {
                        moveTo(fin, ya)
                        cubicTo(punta + (fin - punta) * 0.5f, ya + hh * 0.1f + bulto, punta + s * f.width * 0.01f, yc - hh * 0.75f, punta, yc + bulto * 0.3f)
                        cubicTo(punta - s * f.width * 0.005f, yc + hh * 0.6f, punta + (fin - punta) * 0.4f, yb - hh * 0.2f - bulto, fin, yb)
                    }
                    drawPath(q, vetaC.copy(alpha = (0.2f + 0.16f * azar(m, 20 + j)) * apaga), style = Stroke((0.9f + 1.2f * azar(m, 30 + j)) * u, cap = StrokeCap.Round))
                    if (!t.o || t.k > 0.3f) translate(-s * 1.4f * u, 0f) { drawPath(q, t.b(0.14f), style = Stroke(0.7f * u)) }
                }
            } else {
                vetas(f, vetaC.copy(alpha = if (t.o) 0.25f else 0.5f), m, 0.75f, grosor = 0.6f, ondulacion = 0.2f)
                drawRect(t.s(0.06f), Offset(f.left, f.top + f.height * 0.55f), Size(f.width, f.height * 0.04f))
            }
            // La laca, más cargada en los cantos.
            val g = f.height * 0.03f
            drawRect(t.s(0.14f), f.topLeft, Size(f.width, g)); drawRect(t.s(0.14f), Offset(f.left, f.bottom - g), Size(f.width, g))
            drawRect(t.s(0.12f), f.topLeft, Size(g, f.height)); drawRect(t.s(0.16f), Offset(f.right - g, f.top), Size(g, f.height))
            // La sombra del travesaño de arriba.
            if (bajoTravesano) {
                drawRect(t.s(0.3f), f.topLeft, Size(f.width, f.height * 0.03f))
                drawRect(t.s(0.14f), Offset(f.left, f.top + f.height * 0.03f), Size(f.width, f.height * 0.05f))
            }
            // El uso: un roce claro junto al herraje o un golpe.
            if (Math.floorMod(m ushr 11, 3) == 0) drawOval(t.c(mezcla(col, LUZ_MUEBLE, 0.3f)).copy(alpha = 0.3f), Offset(f.left + f.width * 0.36f, f.bottom - f.height * 0.2f), Size(f.width * 0.28f, f.height * 0.16f))
            if (Math.floorMod(m ushr 13, 3) == 0) {
                val p = Offset(f.left + f.width * (0.1f + 0.1f * azar(m, 50)), f.bottom - f.height * 0.1f)
                drawLine(t.s(0.3f), p, p + Offset(f.width * 0.05f, -f.height * 0.03f), 1f * u)
                drawLine(t.b(0.3f), p + Offset(0f, 1f * u), p + Offset(f.width * 0.05f, -f.height * 0.03f + 1f * u), 0.6f * u)
            }
        }
        if (!bajoTravesano) {
            // El canto de arriba del frente, a la vista: el grosor de la tabla, con luz.
            drawRect(t.c(mezcla(col, LUZ_MUEBLE, 0.22f)), f.topLeft, Size(f.width, f.height * 0.035f))
            drawRect(t.s(0.3f), Offset(f.left, f.top + f.height * 0.035f), Size(f.width, 0.9f * u))
        }
        drawRect(t.b(0.3f), f.topLeft, Size(f.width, 1f * u))
        drawRect(t.b(0.2f), f.topLeft, Size(1f * u, f.height))
        drawRect(t.s(0.35f), Offset(f.left, f.bottom - 1.4f * u), Size(f.width, 1.4f * u))
        drawRect(t.s(0.28f), Offset(f.right - 1.2f * u, f.top), Size(1.2f * u, f.height))
    }

    /** Una esquinera de hierro: un triángulo de borde ondulado con su remache. */
    private fun DrawScope.esquinera(o: Offset, sx: Float, sy: Float, l: Float, t: Tono) {
        val p = Path().apply {
            moveTo(o.x, o.y)
            lineTo(o.x + sx * l, o.y)
            cubicTo(o.x + sx * l * 0.75f, o.y + sy * l * 0.28f, o.x + sx * l * 0.55f, o.y + sy * l * 0.2f, o.x + sx * l * 0.42f, o.y + sy * l * 0.42f)
            cubicTo(o.x + sx * l * 0.2f, o.y + sy * l * 0.55f, o.x + sx * l * 0.28f, o.y + sy * l * 0.75f, o.x, o.y + sy * l)
            close()
        }
        chapa(p, t)
        remache(Offset(o.x + sx * l * 0.2f, o.y + sy * l * 0.2f), l * 0.07f, t)
        remache(Offset(o.x + sx * l * 0.7f, o.y + sy * l * 0.1f), l * 0.05f, t)
        remache(Offset(o.x + sx * l * 0.1f, o.y + sy * l * 0.7f), l * 0.05f, t)
    }

    /** Un tirador colgante (warabite): dos rosetas y el hierro que cuelga entre ellas, curvo, con las puntas vueltas como brotes de helecho. */
    private fun DrawScope.warabite(c: Offset, an: Float, h: Float, t: Tono) {
        val u = density
        val cae = h * 0.115f
        val g = h * 0.034f
        val asa = Path().apply {
            moveTo(c.x - an * 0.42f, c.y - cae * 0.25f)
            cubicTo(c.x - an * 0.56f, c.y + cae * 0.2f, c.x - an * 0.46f, c.y + cae, c.x - an * 0.26f, c.y + cae)
            lineTo(c.x + an * 0.26f, c.y + cae)
            cubicTo(c.x + an * 0.46f, c.y + cae, c.x + an * 0.56f, c.y + cae * 0.2f, c.x + an * 0.42f, c.y - cae * 0.25f)
        }
        listOf(-1f, 1f).forEach { s ->
            val p = Offset(c.x + s * an * 0.4f, c.y)
            val roseta = Path().apply { for (j in 0 until 4) addOval(Rect(p.x - h * 0.03f + h * 0.02f * cos(j * 1.5708f), p.y - h * 0.03f + h * 0.02f * sin(j * 1.5708f), p.x + h * 0.03f + h * 0.02f * cos(j * 1.5708f), p.y + h * 0.03f + h * 0.02f * sin(j * 1.5708f))) }
            translate(0.9f * u, 1.2f * u) { drawPath(roseta, t.s(0.38f)) }
            drawPath(roseta, t.c(HIERRO))
            drawCircle(t.b(0.35f), h * 0.012f, p - Offset(h * 0.012f, h * 0.014f))
        }
        translate(1.6f * u, 2.6f * u) { drawPath(asa, t.s(0.4f), style = Stroke(g, cap = StrokeCap.Round)) }
        drawPath(asa, t.c(HIERRO), style = Stroke(g, cap = StrokeCap.Round))
        translate(-0.6f * u, -0.8f * u) { drawPath(asa, t.b(0.4f), style = Stroke(0.8f * u, cap = StrokeCap.Round)) }
    }

    /** Una bocallave chica: un rombo de hierro con su ojo. */
    private fun DrawScope.bocallave(c: Offset, r: Float, t: Tono) {
        chapa(poligono(c.x, c.y - r * 1.2f, c.x + r * 0.9f, c.y, c.x, c.y + r * 1.2f, c.x - r * 0.9f, c.y), t)
        drawCircle(t.c(SUMI), r * 0.22f, c - Offset(0f, r * 0.15f))
        drawRect(t.c(SUMI), Offset(c.x - r * 0.08f, c.y - r * 0.15f), Size(r * 0.16f, r * 0.55f))
        drawCircle(t.b(0.35f), r * 0.1f, c - Offset(r * 0.4f, r * 0.2f))
    }

    /** Una faja de hierro de arriba abajo del frente, con sus remaches. */
    private fun DrawScope.faja(r: Rect, t: Tono) {
        val u = density
        drawRect(t.s(0.35f), Offset(r.left + 1f * u, r.top), r.size)
        drawRect(t.c(HIERRO), r.topLeft, r.size)
        drawRect(t.c(mezcla(HIERRO, HIERRO_LUZ, 0.25f)), Offset(r.left + r.width * 0.25f, r.top), Size(r.width * 0.5f, r.height))
        drawRect(t.b(0.3f), r.topLeft, Size(0.8f * u, r.height))
        listOf(0.1f, 0.5f, 0.9f).forEach { f -> remache(Offset(r.center.x, r.top + r.height * f), r.width * 0.2f, t) }
    }

    /** La cerradura redonda: la placa con su reborde, el escudo del ojo, cuatro remaches y la sombra que deja en la madera. */
    private fun DrawScope.cerradura(c: Offset, r: Float, t: Tono) {
        val u = density
        drawCircle(t.s(0.4f), r * 1.02f, c + Offset(1.4f * u, 2f * u))
        drawCircle(t.c(HIERRO), r, c)
        drawCircle(t.c(mezcla(HIERRO, HIERRO_LUZ, 0.25f)), r * 0.86f, c + Offset(0.8f * u, 0.9f * u))
        drawCircle(t.c(HIERRO), r * 0.74f, c)
        drawArc(t.b(0.45f), 170f, 110f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(0.9f * u))
        drawArc(t.b(0.25f), 10f, 80f, false, Offset(c.x - r * 0.74f, c.y - r * 0.74f), Size(r * 1.48f, r * 1.48f), style = Stroke(0.7f * u))
        // El escudo del ojo, que sobresale.
        val esc = Path().apply { addOval(Rect(c.x - r * 0.2f, c.y - r * 0.42f, c.x + r * 0.2f, c.y + r * 0.42f)) }
        translate(0.8f * u, 1f * u) { drawPath(esc, t.s(0.4f)) }
        drawPath(esc, t.c(mezcla(HIERRO, HIERRO_LUZ, 0.4f)))
        drawCircle(t.c(SUMI), r * 0.085f, c - Offset(0f, r * 0.14f))
        drawRect(t.c(SUMI), Offset(c.x - r * 0.035f, c.y - r * 0.14f), Size(r * 0.07f, r * 0.36f))
        for (j in 0 until 4) { val a = 0.7854f + j * 1.5708f; remache(Offset(c.x + r * 0.6f * cos(a), c.y + r * 0.6f * sin(a)), r * 0.07f, t) }
    }

    /** La placa de mariposa: las cuatro alas, el cuerpo con el ojo de la llave y las antenas. */
    private fun DrawScope.mariposa(c: Offset, r: Float, t: Tono) {
        val u = density
        val p = Path().apply {
            // Las alas de arriba.
            moveTo(c.x, c.y - r * 0.1f)
            cubicTo(c.x - r * 0.3f, c.y - r * 1.2f, c.x - r * 1.7f, c.y - r * 1.1f, c.x - r * 1.55f, c.y - r * 0.2f)
            cubicTo(c.x - r * 1.5f, c.y + r * 0.1f, c.x - r * 1.1f, c.y + r * 0.2f, c.x - r * 0.9f, c.y + r * 0.15f)
            // Las de abajo.
            cubicTo(c.x - r * 1.25f, c.y + r * 0.5f, c.x - r * 0.9f, c.y + r * 1.05f, c.x - r * 0.35f, c.y + r * 0.85f)
            cubicTo(c.x - r * 0.15f, c.y + r * 0.75f, c.x - r * 0.05f, c.y + r * 0.5f, c.x, c.y + r * 0.4f)
            cubicTo(c.x + r * 0.05f, c.y + r * 0.5f, c.x + r * 0.15f, c.y + r * 0.75f, c.x + r * 0.35f, c.y + r * 0.85f)
            cubicTo(c.x + r * 0.9f, c.y + r * 1.05f, c.x + r * 1.25f, c.y + r * 0.5f, c.x + r * 0.9f, c.y + r * 0.15f)
            cubicTo(c.x + r * 1.1f, c.y + r * 0.2f, c.x + r * 1.5f, c.y + r * 0.1f, c.x + r * 1.55f, c.y - r * 0.2f)
            cubicTo(c.x + r * 1.7f, c.y - r * 1.1f, c.x + r * 0.3f, c.y - r * 1.2f, c.x, c.y - r * 0.1f)
            close()
        }
        // Las antenas.
        listOf(-1f, 1f).forEach { s ->
            val a = Path().apply { moveTo(c.x + s * r * 0.08f, c.y - r * 0.5f); cubicTo(c.x + s * r * 0.15f, c.y - r * 0.95f, c.x + s * r * 0.4f, c.y - r * 1.2f, c.x + s * r * 0.62f, c.y - r * 1.12f) }
            translate(0.8f * u, 1f * u) { drawPath(a, t.s(0.35f), style = Stroke(1.3f * u, cap = StrokeCap.Round)) }
            drawPath(a, t.c(HIERRO), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        }
        chapa(p, t)
        // Las nervaduras grabadas en las alas y el cuerpo.
        listOf(-1f, 1f).forEach { s ->
            drawLine(t.b(0.22f), Offset(c.x + s * r * 0.25f, c.y - r * 0.2f), Offset(c.x + s * r * 1.2f, c.y - r * 0.7f), 0.6f * u)
            drawLine(t.b(0.22f), Offset(c.x + s * r * 0.25f, c.y - r * 0.05f), Offset(c.x + s * r * 1.3f, c.y - r * 0.25f), 0.6f * u)
            drawLine(t.b(0.2f), Offset(c.x + s * r * 0.2f, c.y + r * 0.3f), Offset(c.x + s * r * 0.75f, c.y + r * 0.7f), 0.6f * u)
            remache(Offset(c.x + s * r * 1.1f, c.y - r * 0.5f), r * 0.08f, t)
        }
        val cuerpo = Rect(c.x - r * 0.16f, c.y - r * 0.6f, c.x + r * 0.16f, c.y + r * 0.6f)
        drawOval(t.s(0.4f), Offset(cuerpo.left + 0.8f * u, cuerpo.top + 1f * u), cuerpo.size)
        drawOval(t.c(mezcla(HIERRO, HIERRO_LUZ, 0.4f)), cuerpo.topLeft, cuerpo.size)
        drawCircle(t.c(SUMI), r * 0.075f, c - Offset(0f, r * 0.12f))
        drawRect(t.c(SUMI), Offset(c.x - r * 0.03f, c.y - r * 0.12f), Size(r * 0.06f, r * 0.34f))
    }

    /** Una argolla: su grapa y el aro que cuelga, con su sombra en la madera. */
    private fun DrawScope.argolla(c: Offset, r: Float, t: Tono) {
        val u = density
        drawCircle(t.s(0.38f), r * 0.42f, c + Offset(0.9f * u, 1.2f * u))
        drawCircle(t.c(HIERRO), r * 0.4f, c)
        drawCircle(t.b(0.4f), r * 0.14f, c - Offset(r * 0.12f, r * 0.14f))
        val aro = Rect(c.x - r, c.y + r * 0.05f, c.x + r, c.y + r * 2.05f)
        drawOval(t.s(0.4f), Offset(aro.left + 1.5f * u, aro.top + 2.4f * u), aro.size, style = Stroke(r * 0.28f))
        drawOval(t.c(HIERRO), aro.topLeft, aro.size, style = Stroke(r * 0.3f))
        drawArc(t.b(0.45f), 150f, 100f, false, aro.topLeft, aro.size, style = Stroke(0.8f * u))
    }

    /**
     * El papel de washi con el tema, a tinta: [pegado] al frente (con una esquina que se despega) o suelto y sujeto por la
     * cinta, que lo curva. Lleva sus fibras y un sello bermellón chico; de noche se apaga con la distancia al andon.
     */
    private fun DrawScope.papel(w: Float, h: Float, baja: Float, m: Int, t: Tono, pegado: Boolean, r0: Rect? = null) {
        val u = density
        val r = r0 ?: Rect(w * (rotulo.left - 0.02f), h * (rotulo.top - 0.025f) + baja, w * (rotulo.right + 0.02f), h * (rotulo.bottom + 0.025f) + baja)
        val tinte = when (Math.floorMod(m ushr 15, 3)) { 0 -> WASHI; 1 -> mezcla(WASHI, PAJA, 0.22f); else -> mezcla(WASHI, Color(0xFFBCCADC), 0.2f) }
        val fondo = if (t.o) mezcla(mezcla(tinte, WASHI_LEJOS, 0.92f), mezcla(tinte, LLAMA, 0.12f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(tinte)
        val esq = h * 0.07f
        val despega = pegado && Math.floorMod(m ushr 17, 2) == 0
        val hoja = if (despega) poligono(r.left, r.top, r.right - esq, r.top, r.right, r.top + esq, r.right, r.bottom, r.left, r.bottom) else poligono(r.left, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom)
        translate(if (pegado) 1f * u else 2.2f * u, if (pegado) 1.3f * u else 3f * u) { drawPath(hoja, t.s(if (pegado) 0.28f else 0.36f)) }
        drawPath(hoja, fondo)
        clipPath(hoja) {
            // Las fibras del washi y el borde de la derecha, que se aparta de la luz.
            for (k in 0 until 6) {
                val y = r.top + r.height * (if (k % 2 == 0) 0.05f + 0.07f * azar(m + 9, k) else 0.9f + 0.06f * azar(m + 9, k))
                val x = r.left + r.width * azar(m, k + 20) * 0.75f
                drawLine(BARRO_OSC.copy(alpha = if (t.o) 0.1f else 0.16f), Offset(x, y), Offset(x + r.width * (0.06f + 0.1f * azar(m, k + 40)), y + 0.8f * u), 0.5f * u)
            }
            drawRect(t.s(0.08f), Offset(r.right - r.width * 0.06f, r.top), Size(r.width * 0.06f, r.height))
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
            if (!pegado) {
                // Suelto: se comba hacia la cinta, con una sombra suave abajo.
                drawRect(t.s(0.1f), Offset(r.left, r.bottom - r.height * 0.14f), Size(r.width, r.height * 0.14f))
            }
        }
        if (despega) {
            val doblez = poligono(r.right - esq, r.top, r.right, r.top + esq, r.right - esq * 0.9f, r.top + esq * 0.85f)
            translate(1f * u, 1.4f * u) { drawPath(doblez, t.s(0.3f)) }
            drawPath(doblez, mezcla(fondo, if (t.o) NOCHE else BARRO_OSC, 0.3f))
        }
        // El sello bermellón, abajo a la derecha.
        val s = h * 0.085f
        val so = Offset(r.right - s - w * 0.012f, r.bottom - s - h * 0.018f)
        drawRect(t.c(BERMELLON).copy(alpha = 0.9f), so, Size(s, s))
        drawRect(fondo.copy(alpha = 0.75f), Offset(so.x + s * 0.24f, so.y + s * 0.24f), Size(s * 0.52f, s * 0.52f), style = Stroke(0.7f * u))
        drawLine(fondo.copy(alpha = 0.75f), Offset(so.x + s * 0.5f, so.y + s * 0.24f), Offset(so.x + s * 0.5f, so.y + s * 0.76f), 0.7f * u)
    }

    /** Un tanzaku de [x], de [y0] a [y1]: el papel de color, su nube de añil arriba, el agujero y el cordón bermellón. */
    private fun DrawScope.tanzaku(x: Float, y0: Float, y1: Float, an: Float, col: Color, giro: Float, t: Tono, cordonAbajo: Boolean = false) {
        val u = density
        val pivote = Offset(x + an / 2, if (cordonAbajo) y0 else y1)
        rotate(giro, pivote) {
            drawRect(t.s(0.3f), Offset(x + 1.3f * u, y0 + 1.6f * u), Size(an, y1 - y0))
            drawRect(t.c(col), Offset(x, y0), Size(an, y1 - y0))
            val nube = (y1 - y0) * 0.3f
            val ya = if (cordonAbajo) y1 - nube else y0
            drawRect(t.c(ANIL).copy(alpha = 0.42f), Offset(x, ya), Size(an, nube))
            drawRect(t.s(0.12f), Offset(x + an * 0.72f, y0), Size(an * 0.28f, y1 - y0))
            drawRect(t.b(0.4f), Offset(x, y0), Size(0.8f * u, y1 - y0))
            val hoyo = Offset(x + an / 2, if (cordonAbajo) y1 - nube * 0.45f else y0 + nube * 0.5f)
            drawCircle(t.c(SUMI).copy(alpha = 0.7f), 0.9f * u, hoyo)
            val s = if (cordonAbajo) 1f else -1f
            drawLine(t.c(BERMELLON), hoyo, hoyo + Offset(-an * 0.3f, s * nube * 0.9f), 1f * u, StrokeCap.Round)
            drawLine(t.c(mezcla(BERMELLON, WASHI, 0.2f)), hoyo, hoyo + Offset(an * 0.25f, s * nube * 1.05f), 1f * u, StrokeCap.Round)
        }
    }

    /**
     * Lo que se ve por encima de un frente que ha bajado: el hueco oscuro del cuerpo, con la sombra del travesaño, y los
     * tanzaku de pie dentro del cajón, más cuantos más guarda.
     */
    private fun DrawScope.asoman(w: Float, h: Float, arriba: Float, n: Int, m: Int, t: Tono) {
        val u = density
        val hueco = mezcla(KEYAKI_OSC, SOMBRA_MUEBLE, 0.7f)
        drawRect(if (t.o) mezcla(NOCHE, hondo(hueco), 0.4f) else hueco, Offset.Zero, Size(w, arriba + 2f * u))
        // Los costados de kiri del cajón, que asoman a los lados.
        drawRect(t.c(mezcla(KIRI, KIRI_VETA, 0.5f)), Offset(0f, arriba * 0.72f), Size(w * 0.022f, arriba * 0.28f + 2f * u))
        drawRect(t.c(mezcla(KIRI, KIRI_VETA, 0.7f)), Offset(w * 0.978f, arriba * 0.72f), Size(w * 0.022f, arriba * 0.28f + 2f * u))
        val k = n.coerceIn(0, 7)
        val paleta = Math.floorMod(m ushr 9, TANZAKU.size)
        val an = w * 0.062f
        for (i in 0 until k) {
            val x = w * (0.07f + 0.86f * (i + 0.5f) / k) - an / 2 + w * 0.02f * (azar(m, i + 70) - 0.5f)
            val y0 = arriba * (0.04f + 0.3f * azar(m, i + 60))
            tanzaku(x, y0, arriba + 3f * u, an, TANZAKU[(i * 2 + paleta) % TANZAKU.size], (azar(m, i + 80) - 0.5f) * 7f, t)
        }
        // La sombra del travesaño de arriba y del costado.
        drawRect(t.s(0.42f), Offset.Zero, Size(w, arriba * 0.2f))
        drawRect(t.s(0.2f), Offset(0f, arriba * 0.2f), Size(w, arriba * 0.2f))
        drawRect(t.s(0.2f), Offset(w * 0.93f, 0f), Size(w * 0.07f, arriba))
    }

    /** Los tanzaku pillados por el cajón cerrado, que cuelgan por delante del frente: más cuantos más guarda. */
    private fun DrawScope.colgados(f: Rect, w: Float, h: Float, n: Int, m: Int, paleta: Int, t: Tono) {
        val k = n.coerceIn(0, 7)
        val an = w * 0.056f
        for (i in 0 until k) {
            val x = w * (0.1f + 0.8f * (i + 0.5f) / k) - an / 2 + w * 0.03f * (azar(m, i + 70) - 0.5f)
            val largo = h * (0.1f + 0.1f * azar(m, i + 60))
            tanzaku(x, f.top - 1f, f.top + largo, an, TANZAKU[(i * 2 + paleta + 1) % TANZAKU.size], (azar(m, i + 80) - 0.5f) * 9f, t, cordonAbajo = true)
        }
        // La rendija por donde salen.
        drawRect(t.s(0.5f), f.topLeft, Size(f.width, 1.6f * density))
    }

    /** Los tanzaku metidos bajo la cinta, detrás del papel: asoman de pie por encima de él, en abanico, más cuantos más guarda. */
    private fun DrawScope.metidos(w: Float, h: Float, baja: Float, n: Int, m: Int, paleta: Int, t: Tono) {
        val k = n.coerceIn(0, 7)
        val an = w * 0.052f
        val yPapel = h * (rotulo.top - 0.025f) + baja
        // En un manojo a la izquierda, abiertos en abanico: el primero, el más tumbado.
        for (i in 0 until k) {
            val x = w * (0.1f + 0.042f * i) + w * 0.008f * (azar(m, i + 70) - 0.5f)
            val y0 = yPapel - h * (0.1f + 0.07f * azar(m, i + 60) + 0.012f * i)
            tanzaku(x, y0, yPapel + h * 0.12f, an, TANZAKU[(i * 2 + paleta + 2) % TANZAKU.size], -24f + 6f * i + 4f * (azar(m, i + 80) - 0.5f), t)
        }
    }

    /**
     * La cinta que sujeta el papel: tirante de grapa a grapa, con su nudo y sus dos cabos, y bajo ella los tanzaku metidos,
     * que cuelgan a los lados de la cerradura.
     */
    private fun DrawScope.cinta(f: Rect, w: Float, h: Float, baja: Float, n: Int, m: Int, paleta: Int, t: Tono) {
        val u = density
        val y = h * (rotulo.bottom + 0.045f) + baja
        val g = h * 0.042f
        val color = when (Math.floorMod(m ushr 19, 3)) { 0 -> mezcla(ANIL, Color(0xFF3A5A86), 0.4f); 1 -> mezcla(BERMELLON, KEYAKI_OSC, 0.45f); else -> mezcla(BAMBU, SUGI, 0.35f) }
        val x0 = w * 0.045f; val x1 = w * 0.955f
        drawRect(t.s(0.35f), Offset(x0, y + g * 0.5f), Size(x1 - x0, 1.8f * u))
        drawRect(t.c(color), Offset(x0, y - g / 2), Size(x1 - x0, g))
        drawRect(t.b(0.35f), Offset(x0, y - g / 2), Size(x1 - x0, 0.9f * u))
        drawRect(t.c(mezcla(color, WASHI, 0.5f)), Offset(x0, y - g * 0.08f), Size(x1 - x0, g * 0.16f))
        // Las grapas de hierro de los extremos, el nudo y los cabos.
        listOf(x0, x1).forEach { x ->
            drawRect(t.s(0.35f), Offset(x - w * 0.008f + 1f * u, y - g * 0.9f + 1.4f * u), Size(w * 0.016f, g * 1.8f))
            drawRect(t.c(HIERRO), Offset(x - w * 0.008f, y - g * 0.9f), Size(w * 0.016f, g * 1.8f))
            drawRect(t.b(0.35f), Offset(x - w * 0.008f, y - g * 0.9f), Size(0.8f * u, g * 1.8f))
        }
        val nudo = Offset(x1 - w * 0.045f, y)
        listOf(-0.012f to 0.13f, 0.012f to 0.1f).forEach { (dx, largo) ->
            val fin = nudo + Offset(w * dx, h * largo)
            drawLine(t.s(0.3f), nudo + Offset(1.5f * u, 2f * u), fin + Offset(1.5f * u, 2f * u), g * 0.7f)
            drawLine(t.c(mezcla(color, SUMI, 0.15f)), nudo, fin, g * 0.75f)
        }
        drawOval(t.c(mezcla(color, WASHI, 0.12f)), Offset(nudo.x - g * 0.75f, nudo.y - g * 0.75f), Size(g * 1.5f, g * 1.5f))
        drawArc(t.b(0.45f), 170f, 100f, false, Offset(nudo.x - g * 0.75f, nudo.y - g * 0.75f), Size(g * 1.5f, g * 1.5f), style = Stroke(0.8f * u))
    }

    /**
     * Lo que no tiene tema: el hueco sin cajón (el fondo, la sombra del travesaño, el costado y la tabla de abajo) y, puesta
     * dentro, una caja larga de kiri con su tapa, atada en dos sitios con cordón plano (sanada-himo) y su papel pegado; detrás
     * asoman los tanzaku. Al abrir, la caja se acerca.
     */
    private fun DrawScope.caja(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, baja: Float, ab: Float) {
        val u = density
        // El hueco.
        val fondo = mezcla(KEYAKI_OSC, SOMBRA_MUEBLE, 0.55f)
        drawRect(if (t.o) mezcla(NOCHE, hondo(fondo), 0.5f) else fondo, Offset.Zero, Size(w, h))
        drawRect(t.b(0.08f), Offset.Zero, Size(w * 0.4f, h))
        listOf(0.3f, 0.62f).forEach { fx -> drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, h)); drawRect(t.b(0.14f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, h)) }
        val piso = h * 0.9f
        drawRect(t.c(mezcla(KIRI, KIRI_VETA, 0.55f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.4f), Offset(0f, piso), Size(w, 1f * u))
        // Los tanzaku, de pie contra el fondo, detrás de la caja.
        val e = w * 0.015f * ab
        val cajaR = Rect(w * 0.06f - e, h * 0.2f + baja, w * 0.94f + e, piso + baja + h * 0.03f * ab)
        val k = c.n.coerceIn(0, 7)
        val paleta = Math.floorMod(m ushr 9, TANZAKU.size)
        val an = w * 0.06f
        for (i in 0 until k) {
            val x = w * (0.14f + 0.72f * (i + 0.5f) / k) - an / 2
            tanzaku(x, h * (0.035f + 0.09f * azar(m, i + 60)), h * 0.24f + baja, an, TANZAKU[(i * 2 + paleta) % TANZAKU.size], (azar(m, i + 80) - 0.5f) * 8f, t)
        }
        // La sombra del travesaño de arriba, en escalones, y el costado derecho, más hondo.
        listOf(0.26f to 0.14f, 0.14f to 0.16f, 0.06f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.22f), Offset(w * 0.94f, 0f), Size(w * 0.06f, h))
        // La caja de kiri: su sombra, el cuerpo con su veta recta, la tapa que monta y el lado en sombra.
        drawRect(t.s(0.34f), Offset(cajaR.left + w * 0.02f, cajaR.top + h * 0.03f), cajaR.size)
        drawRect(t.c(KIRI), cajaR.topLeft, cajaR.size)
        clipRect(cajaR.left, cajaR.top, cajaR.right, cajaR.bottom) {
            vetas(cajaR, (if (t.o) NOCHE else KIRI_VETA).copy(alpha = 0.5f), m, 0.7f, grosor = 0.6f, ondulacion = 0.25f)
            drawRect(t.b(0.16f), cajaR.topLeft, Size(cajaR.width * 0.3f, cajaR.height))
            drawRect(t.s(0.14f), Offset(cajaR.right - cajaR.width * 0.12f, cajaR.top), Size(cajaR.width * 0.12f, cajaR.height))
            drawRect(t.s(0.12f), Offset(cajaR.left, cajaR.bottom - cajaR.height * 0.1f), Size(cajaR.width, cajaR.height * 0.1f))
        }
        val tapaH = cajaR.height * 0.14f
        drawRect(t.s(0.3f), Offset(cajaR.left, cajaR.top + tapaH), Size(cajaR.width, 1.3f * u))
        drawRect(t.b(0.3f), Offset(cajaR.left, cajaR.top + tapaH + 1.3f * u), Size(cajaR.width, 0.8f * u))
        drawRect(t.b(0.5f), cajaR.topLeft, Size(cajaR.width, 1.1f * u))
        drawRect(t.s(0.3f), Offset(cajaR.left, cajaR.bottom - 1.2f * u), Size(cajaR.width, 1.2f * u))
        // El papel, pegado en el frente.
        papel(w, h, baja, m, t, pegado = true, r0 = Rect(w * 0.19f, h * (rotulo.top - 0.02f) + baja, w * 0.81f, h * (rotulo.bottom + 0.02f) + baja))
        // El cordón plano, en dos sitios: tejido en espiga, con su lazo sobre la tapa.
        val cordon = mezcla(ANIL, Color(0xFF3A5A86), 0.35f)
        listOf(0.125f, 0.875f).forEach { fx ->
            val x = w * fx - w * 0.021f + (if (fx < 0.5f) -e else e)
            val an2 = w * 0.042f
            drawRect(t.s(0.3f), Offset(x + an2, cajaR.top), Size(1.6f * u, cajaR.height))
            drawRect(t.c(cordon), Offset(x, cajaR.top), Size(an2, cajaR.height))
            drawRect(t.c(mezcla(cordon, WASHI, 0.55f)), Offset(x + an2 * 0.4f, cajaR.top), Size(an2 * 0.2f, cajaR.height))
            drawRect(t.b(0.3f), Offset(x, cajaR.top), Size(0.8f * u, cajaR.height))
            var yy = cajaR.top + 2f * u
            while (yy < cajaR.bottom - 2f * u) { drawLine(t.s(0.2f), Offset(x, yy), Offset(x + an2, yy + 2f * u), 0.5f * u); yy += 3.2f * u }
            // El lazo, que asoma sobre la tapa.
            val nx = x + an2 / 2; val ny = cajaR.top
            listOf(-1f, 1f).forEach { s ->
                val lazo = Path().apply {
                    moveTo(nx, ny)
                    cubicTo(nx + s * w * 0.02f, ny - h * 0.1f, nx + s * w * 0.06f, ny - h * 0.08f, nx + s * w * 0.055f, ny - h * 0.02f)
                    cubicTo(nx + s * w * 0.05f, ny + h * 0.005f, nx + s * w * 0.02f, ny - h * 0.005f, nx, ny)
                    close()
                }
                drawPath(lazo, t.c(mezcla(cordon, SUMI, if (s > 0) 0.25f else 0.05f)))
                drawPath(lazo, t.b(0.3f), style = Stroke(0.7f * u))
            }
            drawOval(t.c(mezcla(cordon, WASHI, 0.15f)), Offset(nx - w * 0.012f, ny - h * 0.035f), Size(w * 0.024f, h * 0.05f))
        }
        filo(cajaR, t)
    }

    override val rotulo = Rect(0.06f, 0.275f, 0.94f, 0.625f)
    override fun tintaRotulo(oscuro: Boolean) = SUMI
    override val letraRotulo: FontFamily = LetraExtra.shippori
    override val salida = SALIDA
    override val placa = Rect(0.33f, 0.05f, 0.67f, 0.235f)
    override fun tintaPlaca(oscuro: Boolean) = SUMI

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ki = if (oscuro) mezcla(KIRI, Color.Black, 0.6f) else KIRI
        // El fondo del cajón: tablas de kiri a lo largo, de veta recta y fina.
        drawRect(ki)
        val tablas = 4
        for (k in 0 until tablas) {
            val r = Rect(w * k / tablas, 0f, w * (k + 1) / tablas, h)
            drawRect(mezcla(ki, if (k % 2 == 0) Color.White else KIRI_VETA, 0.06f), r.topLeft, r.size)
            vetas(r, KIRI_VETA.copy(alpha = if (oscuro) 0.25f else 0.4f), 31 + k, 1.4f, vertical = true, grosor = 0.7f)
            if (k > 0) drawLine(Color.Black.copy(alpha = 0.18f), Offset(r.left, 0f), Offset(r.left, h), 1f * u)
        }
        // Las paredes, vistas desde arriba: más gruesa la de enfrente (la del tirador), con su canto claro y su sombra adentro.
        val pl = 14 * u; val pa = 12 * u; val pf = 26 * u
        fun tabla(r: Rect, horizontal: Boolean) {
            drawRect(mezcla(ki, KIRI_VETA, 0.35f), r.topLeft, r.size)
            vetas(r, KIRI_VETA.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = !horizontal, grosor = 0.6f)
        }
        tabla(Rect(0f, 0f, w, pa), true)
        tabla(Rect(0f, h - pf, w, h), true)
        drawRect(mezcla(madera(oscuro), Color.Black, 0.1f), Offset(0f, h - pf * 0.55f), Size(w, pf * 0.55f))
        vetas(Rect(0f, h - pf * 0.55f, w, h), KEYAKI_OSC.copy(alpha = 0.5f), 61, 1f)
        tabla(Rect(0f, 0f, pl, h), false)
        tabla(Rect(w - pl, 0f, w, h), false)
        // Los cantos (la luz sobre el borde de cada tabla) y la sombra que echan sobre el fondo.
        drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, pa), Offset(w, pa), 1f * u)
        drawLine(Color.White.copy(alpha = 0.35f), Offset(pl, 0f), Offset(pl, h), 1f * u)
        drawLine(Color.White.copy(alpha = 0.2f), Offset(w - pl, 0f), Offset(w - pl, h), 1f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), pa, pa + 22 * u), Offset(pl, pa), Size(w - 2 * pl, 22 * u))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), pl, pl + 16 * u), Offset(pl, pa), Size(16 * u, h - pa - pf))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.16f)), w - pl - 14 * u, w - pl), Offset(w - pl - 14 * u, pa), Size(14 * u, h - pa - pf))
        // Las colas de milano en las cuatro esquinas, donde se ensamblan las tablas.
        listOf(0f to 0f, w - pl to 0f, 0f to h - pf, w - pl to h - pf).forEach { (x, y) ->
            val alto = if (y == 0f) pa else pf
            for (k in 0 until 3) drawRect(Color.Black.copy(alpha = 0.16f), Offset(x + pl * 0.2f, y + alto * (0.15f + 0.3f * k)), Size(pl * 0.6f, alto * 0.14f))
        }
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) WASHI else SUMI
    override val pared: Dp = 16.dp
    override val columnas = 3
    override val lineas = 8

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.5.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 2f else 1f
        // El teñido en nube: añil arriba y violeta abajo, en manchas suaves que se funden con el papel.
        val nubeA = 12 * u * g; val nubeV = 9 * u * g
        for (k in 0 until 11) {
            val x = w * (k + azar(semilla, k)) / 10f
            mancha(Offset(x, nubeA * (0.05f + 0.35f * azar(semilla, k + 10))), nubeA * (0.8f + 0.5f * azar(semilla, k + 20)), ANIL.copy(alpha = 0.26f))
            mancha(Offset(x, h - nubeV * (0.05f + 0.35f * azar(semilla, k + 30))), nubeV * (0.8f + 0.5f * azar(semilla, k + 40)), VIOLETA.copy(alpha = 0.22f))
        }
        // Una franja fina de añil pegada al borde, donde el tinte cargó más.
        drawRect(Brush.verticalGradient(listOf(ANIL.copy(alpha = 0.35f), Color.Transparent), 0f, nubeA * 0.7f), Offset.Zero, Size(w, nubeA * 0.7f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, VIOLETA.copy(alpha = 0.3f)), h - nubeV * 0.7f, h), Offset(0f, h - nubeV * 0.7f), Size(w, nubeV * 0.7f))
        // Hojuelas de oro (kirihaku), cuadraditos esparcidos.
        val n = if (grande) 46 else 16
        for (k in 0 until n) {
            val c = Offset(w * azar(semilla, k + 100), h * azar(semilla, k + 200))
            val s = (0.8f + 1.6f * azar(semilla, k + 300)) * u * (if (grande) 1.3f else 1f)
            rotate(azar(semilla, k + 400) * 90f, c) { drawRect(ORO.copy(alpha = 0.35f + 0.4f * azar(semilla, k + 500)), Offset(c.x - s / 2, c.y - s / 2), Size(s, s)) }
        }
        // El agujero y el cordón de papel torcido (koyori), atado arriba.
        val hoyo = Offset(w / 2, 8 * u * g + 4 * u)
        drawCircle(Color.Black.copy(alpha = 0.55f), 1.6f * u * g, hoyo)
        drawLine(BERMELLON, hoyo, Offset(w / 2 - 5 * u * g, 0f), 1.3f * u * g, StrokeCap.Round)
        drawLine(mezcla(BERMELLON, Color.White, 0.25f), hoyo, Offset(w / 2 + 5 * u * g, 0f), 1.3f * u * g, StrokeCap.Round)
        drawCircle(BERMELLON, 1.8f * u * g, Offset(w / 2, hoyo.y - 3 * u * g))
    }

    override val margenes = MargenesFicha(22.dp, 7.dp, 14.dp, 40.dp, 22.dp, 34.dp)
    override val proporcionFicha = 0.46f
    override val anchoGrande = 0.8f
    override val proporcionGrande = 0.42f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = true

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val u = density
        val s = size.minDimension; val c = center
        when (id) {
            // Un sello redondo de tinta sumi.
            "hecho" -> { drawCircle(SUMI, s * 0.42f, c); drawCircle(WASHI, s * 0.26f, c, style = Stroke(s * 0.07f)) }
            // El sello: un cuadrado bermellón con su marco blanco adentro.
            "convence" -> { drawRect(BERMELLON, Offset(c.x - s * 0.42f, c.y - s * 0.42f), Size(s * 0.84f, s * 0.84f)); drawRect(WASHI, Offset(c.x - s * 0.24f, c.y - s * 0.24f), Size(s * 0.48f, s * 0.48f), style = Stroke(0.8f * u)) }
            // Un círculo añil con un punto: lo estás pensando.
            "piensa" -> { drawCircle(ANIL, s * 0.42f, c); drawCircle(WASHI, s * 0.12f, c) }
            // Un ensō abierto de tinta sumi: todavía no se cierra.
            "duda" -> drawArc(SUMI.copy(alpha = 0.8f), 40f, 290f, false, Offset(c.x - s * 0.38f, c.y - s * 0.38f), Size(s * 0.76f, s * 0.76f), style = Stroke(s * 0.14f, cap = StrokeCap.Round))
            // Dos trazos de pincel cruzados.
            "desacuerdo" -> { drawLine(SUMI, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Offset(c.x + s * 0.36f, c.y + s * 0.36f), s * 0.16f, StrokeCap.Round); drawLine(BERMELLON, Offset(c.x + s * 0.36f, c.y - s * 0.36f), Offset(c.x - s * 0.36f, c.y + s * 0.36f), s * 0.16f, StrokeCap.Round) }
            else -> {}
        }
    }

    /** Un nudo de mizuhiki: dos lazos de cordón, bermellón y oro, con sus puntas. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        val g = s * 0.11f
        drawOval(BERMELLON, Offset(c.x - s * 0.48f, c.y - s * 0.26f), Size(s * 0.5f, s * 0.36f), style = Stroke(g))
        drawOval(ORO, Offset(c.x - s * 0.02f, c.y - s * 0.26f), Size(s * 0.5f, s * 0.36f), style = Stroke(g))
        drawLine(BERMELLON, Offset(c.x, c.y), Offset(c.x - s * 0.26f, c.y + s * 0.44f), g, StrokeCap.Round)
        drawLine(ORO, Offset(c.x, c.y), Offset(c.x + s * 0.26f, c.y + s * 0.44f), g, StrokeCap.Round)
    }

    override val desorden = 0f
}
