package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Napoleónico
// =====================================================================

/**
 * Napoleón: el secrétaire de campaña de un oficial, montado en la tienda del estado mayor hacia 1812. Es un interior sin
 * abertura: detrás solo se ve la lona cruda de la tienda, con sus paños cosidos, las listas finas de cutí azul, la banda de
 * refuerzo con sus ollaos y un remiendo; la luz de día se filtra por la lona desde arriba a la izquierda y trae la sombra de
 * un viento de fuera. El mueble es de caoba y se desmonta para viajar: cantoneras y filetes de latón, el marco de latón de
 * cada casilla, golpes y rozaduras, un friso con la corona de laurel y la abeja doradas, y abajo la tapa abatida con su
 * cuero verde y su doble filete dorado, sobre cuatro patas torneadas que se desenroscan. Encima, el punto focal, a la
 * izquierda: el bicornio de fieltro negro con su escarapela, su presilla dorada y sus borlas; al centro, el candelero de
 * campaña de latón con su vela a medio gastar y el apagavelas; una cantimplora de calabaza con su cordón y una pipa de
 * arcilla; un reloj de bolsillo con su cadena y, tumbado al frente, el catalejo. En la lona, un mapa de marcha clavado con
 * alfileres (el río, los cerros a plumadas y el camino en rojo) con una punta que se levanta. A la derecha, el poste de la
 * tienda con su ligadura de cáñamo: de su gancho cuelga la casaca azul de oficial (cuello y vueltas rojos, solapas
 * blancas, botones dorados y la charretera) y, de sus tiros de cuero blanco, el sable en su vaina con la dragona. Sobre la tapa
 * abatida, un mapa plegado sujeto con la brújula y el compás de puntas, la carta a medio escribir con la pluma, el tintero,
 * la salvadera, la barra de lacre y el sello. Al pie, sobre la tierra pisada con paja, unas botas de montar y el baúl de
 * campaña claveteado, con las iniciales casi borradas y su chapa de latón (la placa de «y N más»), cortado por el borde.
 * Cada tema es una casilla con su gaveta debajo y el tema en el portaetiquetas de latón de la gaveta; en la casilla, según
 * la semilla, un fajo de despachos lacrados de pie y atados con cinta, un portefeuille de tafilete (rojo, verde o leonado)
 * con su cerradura y los papeles que asoman, o un tubo de mapas de hojalata pintada apoyado en diagonal con sus rollos
 * (más despachos, papeles o rollos cuanto más guarda). Lo que no tiene tema es una sombrerera de cartón gris azulado con
 * la tapa levantada y los papeles sin lacrar; una casilla libre guarda unas barras de lacre, un cabo de vela en su platillo y
 * un carrete de cinta. Al abrir una, lo guardado se levanta y la gaveta sale. De noche todo se hunde en azul hondo: alumbra
 * solo la vela (su halo en la lona, que deja traslucir la luz; el filo cálido en el fieltro, el latón y los dorados), las
 * casillas de arriba encendidas y las de abajo casi perdidas. Por dentro, cuero verde con hierros dorados. Las fichas son
 * despachos con su filete azul y la abeja tenue.
 */
object IdeasNapoleon : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la tienda
    /** La caoba del mueble, su luz y su sombra. */
    private val CAOBA = Color(0xFF6A2E1C)
    private val CAOBA_LUZ = Color(0xFF8E4428)
    private val CAOBA_OSC = Color(0xFF34140A)
    /** El latón (y los dorados) y su sombra. */
    private val BRONCE = Color(0xFFC9A24C)
    private val BRONCE_OSC = Color(0xFF8A6A24)
    /** El cuero verde de la tapa y de un portefeuille. */
    private val CUERO = Color(0xFF2E5A3A)
    private val CUERO_OSC = Color(0xFF1E3E28)
    /** El papel, la tinta, el azul de la cinta y el rojo del lacre, del cuello y de la escarapela. */
    private val PAPEL = Color(0xFFF6F0E0)
    private val AZUL = Color(0xFF1E3A8A)
    private val ROJO = Color(0xFFA3172E)
    private val TINTA = Color(0xFF14213D)
    /** La lona cruda de la tienda y su sombra. */
    private val LONA = Color(0xFFD8CAA8)
    private val LONA_OSC = Color(0xFFAD9C78)
    /** El paño azul de la casaca, el fieltro negro del bicornio (y el cuero negro de botas y vaina). */
    private val PANO = Color(0xFF24376C)
    private val FIELTRO = Color(0xFF25232D)
    /** El cuero leonado (correas, calabaza, baúl), la hojalata y la tierra del suelo. */
    private val CORREA = Color(0xFF9A6A3A)
    private val HOJALATA = Color(0xFF8F979B)
    private val TIERRA = Color(0xFF86704F)
    /** La llama de la vela y la noche de la tienda: azul hondo. */
    private val LLAMA = Color(0xFFFFC56E)
    private val NOCHE = Color(0xFF0A1122)
    /**
     * La tarjeta del portaetiquetas de noche, lejos de la vela: con [TINTA] da unos 7:1 en el título y 4.6:1 en la cuenta
     * (al 78 %); cerca de la vela sube hacia [PAPEL] con un poco de llama.
     */
    private val PAPEL_LEJOS = Color(0xFFAEADA2)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.66f
    override val base = 0.36f
    override val lado = 0.07f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la tapa de arriba, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.875f
    private const val CANTO = 0.93f
    /** Los costados del mueble, en fracción del ancho. */
    private const val IZQ = 0.03f
    private const val DER = 0.97f
    /**
     * En la base: la bisagra de la tapa abatida, su borde de delante, lo bajo de su canto, el rincón de la lona con el
     * suelo, donde pisan las patas y donde pisan las cosas del pie.
     */
    private const val BISAGRA = 0.03f
    private const val BORDE = 0.34f
    private const val GRUESO = 0.41f
    private const val SUELO = 0.7f
    private const val PISAN = 0.92f
    private const val PIE = 0.975f
    /** En cada casilla: el piso (la tabla), donde empieza la gaveta y cuánto se mueve lo guardado al abrirla, en fracción de su alto. */
    private const val PISO = 0.665f
    private const val GAVETA = 0.68f
    private const val SALIDA = 0.05f
    /** El marco de latón de cada casilla, en fracción del ancho del mueble. */
    private const val MARCO = 0.011f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la vela, en fracciones del ancho. */
    private val LUZ = Offset(0.47f, 0.305f)

    /** Cuánto le llega de la vela a un punto [p] (en fracciones del ancho): cae con la distancia. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.5f * kotlin.math.exp(-4f * kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que la vela echa hacia abajo sobre el frente del mueble: cae despacio de fila en fila (la primera, encendida; la
     * segunda, a medias; la tercera, tenue; las demás, casi perdidas). Los puntos son los centros de las filas con 9 temas,
     * en fracciones del ancho.
     */
    private val CAIDA = listOf(0.64f to 0.88f, 0.864f to 0.8f, 1.232f to 0.52f, 1.6f to 0.26f, 1.968f to 0.1f, 2.336f to 0.05f, 2.9f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.2f).coerceAtLeast(0f)).coerceIn(0.75f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire azul de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia
     * el suyo según [k] (lo que le llega de la vela), teñido de llama.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.14f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche de la tienda. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    /** De noche la luz viene de la vela: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.02f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la lona: de día corrida a la derecha y abajo; de noche, hacia fuera de la vela, tanto más marcada
     * cuanto más cerca está de ella. No baja de [hasta] (la tapa o el suelo que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.16f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.02f, w * 0.011f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.1f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.035f * dx, w * 0.006f) { drawPath(p, NOCHE.copy(alpha = (0.7f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa, la tabla o el suelo, corrida a la derecha (de noche, hacia fuera de la vela). */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) {
        val corr = if (luzDerecha(centro.x)) 0.58f else 0.42f
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * corr, centro.y - alto / 2), Size(ancho, alto))
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

    /**
     * El filo cálido de la vela en algo, de noche: en su canto de arriba y en el costado que mira a la luz ([der]: el
     * derecho), tanto más vivo cuanto más le llega. De día no pinta nada.
     */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /**
     * El filo de la vela a lo largo de un contorno, de noche (para lo que no es una caja: la casaca, la calabaza): solo en la
     * mitad que mira a la vela, a un lado de [cx] ([der]: la derecha).
     */
    private fun DrawScope.filoContorno(p: Path, t: Tono, cx: Float, der: Boolean, fuerza: Float = 1f) {
        if (!t.o || t.k < 0.06f) return
        clipRect(if (der) cx else -size.width, -size.height, if (der) size.width * 2f else cx, size.height * 2f) {
            drawPath(p, LLAMA.copy(alpha = (0.75f * t.k * fuerza).coerceAtMost(0.7f)), style = Stroke(1.3f * density))
        }
    }

    /** El destello de la vela en el latón o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.2f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Un clavo o tornillo de latón con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono, metal: Color = BRONCE) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(metal), r, p)
        drawCircle(t.b(0.6f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una pieza de latón plana (cantonera, chapa, bisagra): su sombra, el metal, la luz arriba y la sombra abajo. */
    private fun DrawScope.chapa(r: Rect, t: Tono, metal: Color = BRONCE) {
        val u = density
        drawRect(t.s(0.3f), Offset(r.left + 1.2f * u, r.top + 1.5f * u), r.size)
        drawRect(t.c(metal), r.topLeft, r.size)
        drawRect(t.b(0.55f), r.topLeft, Size(r.width, 0.9f * u))
        drawRect(t.b(0.3f), r.topLeft, Size(0.9f * u, r.height))
        drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
    }

    /** Una corona de laurel: dos ramas de hojas que se cierran arriba. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color) {
        val u = density
        listOf(-1f, 1f).forEach { s ->
            drawArc(color, if (s < 0) 100f else -80f, 160f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1f * u))
            for (k in 0 until 7) {
                val a = Math.toRadians((if (s < 0) 110.0 + k * 22.0 else 70.0 - k * 22.0))
                val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                rotate(Math.toDegrees(a).toFloat() + 90f * s, p) { drawOval(color, Offset(p.x - r * 0.08f, p.y - r * 0.2f), Size(r * 0.16f, r * 0.34f)) }
            }
        }
    }

    /** Una abeja imperial, vista desde arriba. */
    private fun DrawScope.abeja(c: Offset, s: Float, color: Color) {
        drawOval(color, Offset(c.x - s * 0.36f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x + s * 0.04f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x - s * 0.12f, c.y - s * 0.3f), Size(s * 0.24f, s * 0.72f))
        drawCircle(color, s * 0.12f, Offset(c.x, c.y - s * 0.38f))
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE; val canto = ch * CANTO
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.86f
        val suelo = b.top + hb * SUELO

        // ---- la lona de la tienda y el suelo de tierra
        lona(w, ch, suelo)
        tierra(w, suelo, alto)

        // ---- en la lona: el mapa clavado; el poste con su ligadura
        mapa(Rect(w * 0.04f, ch * 0.075f, w * 0.305f, ch * 0.405f))
        poste(w, suelo + hb * 0.05f)

        // ---- lo que cuelga del poste: la casaca y el sable
        casaca(Offset(w * POSTE, w * (GANCHO + 0.022f)), sup)
        sable(Offset(w * (POSTE - 0.02f), w * (GANCHO + 0.012f)), sup)

        // ---- lo que hay al pie, debajo de la tapa abatida: las patas, las botas y el baúl
        bajoLaTapa(w, b)

        // ---- el mueble, su tapa de arriba y la tapa abatida con lo de escribir
        cuerpo(d, ch, canto, b)
        tapaDeArriba(w, sup, frente, canto)
        tapaAbatida(w, b)

        // ---- lo de encima, de atrás adelante
        bicornio(Offset(w * 0.222f, pieA), sup)
        candelero(Offset(LUZ.x * w, pieA), sup)
        calabaza(Offset(w * 0.612f, pieA), sup)
        pipa(Offset(w * 0.6f, pieF))
        reloj(Offset(w * 0.4f, pieF))
        catalejo(Offset(w * 0.815f, pieF))

        // ---- la placa de «y N más»: la tarjeta en la chapa de latón del baúl
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            val dentro = Rect(r.left + 3f * density, r.top + 2.5f * density, r.right - 3f * density, r.bottom - 2.5f * density)
            drawRect(if (o) PAPEL_LEJOS else PAPEL, dentro.topLeft, dentro.size)
            drawRect(t.s(0.15f), dentro.topLeft, Size(dentro.width, 1.5f * density))
        }

        // ---- la luz de la vela, por encima de lo que tiene cerca
        if (o) {
            val llama = LUZ * w
            mancha(llama + Offset(0f, w * 0.03f), w * 0.46f, LLAMA.copy(alpha = 0.12f))
            mancha(llama + Offset(0f, w * 0.01f), w * 0.2f, LLAMA.copy(alpha = 0.24f))
            mancha(llama, w * 0.07f, Color(0xFFFFF0CF).copy(alpha = 0.35f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- la lona y el suelo

    /** Las costuras de los paños de la lona, en fracción del ancho. */
    private val COSTURAS = listOf(0.125f, 0.37f, 0.615f, 0.86f)

    /**
     * La lona cruda de la tienda: la luz que se filtra arriba a la izquierda (de noche, el halo de la vela, que la lona deja
     * traslucir), la caída de cada paño entre costura y costura, las listas finas de cutí azul, las costuras dobles con sus
     * puntadas, la banda de refuerzo con sus ollaos y sus cabos, las arrugas que tiran de cada ollao, la sombra de un viento
     * de fuera, un remiendo y, abajo, la humedad y el barro del faldón.
     */
    private fun DrawScope.lona(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(LONA, LUZ_MUEBLE, 0.45f), LONA, mezcla(LONA, LONA_OSC, 0.8f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.2f, w * 0.06f), w * 0.62f, luzMueble(0.55f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.16f)), w * 0.3f, w * 0.75f), Offset(0f, w * 0.3f), Size(w, suelo - w * 0.3f))
        } else {
            // La lona es clara: para que la noche no quede lechosa, su luz cae más deprisa que la de las cosas.
            fun k(d: Float): Color { val kk = luz(LUZ + Offset(d, 0f)).pow(2f); return mezcla(Tono(true, kk).c(LONA), LLAMA, 0.24f * kk) }
            drawRect(Brush.radialGradient(0f to k(0.03f), 0.07f to k(0.085f), 0.14f to k(0.17f), 0.25f to k(0.3f), 0.42f to k(0.5f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f), Offset.Zero, Size(w, suelo))
        }
        // La caída de cada paño (una panza suave: claro a un lado, sombra al otro) y las listas de cutí.
        val lista = (if (o) mezcla(AZUL, NOCHE, 0.55f) else mezcla(AZUL, LONA, 0.3f)).copy(alpha = if (o) 0.42f else 0.32f)
        // De noche los claros solo llegan hasta donde alumbra la vela (más abajo, la lona queda detrás del mueble o a oscuras).
        val claros = if (o) ch else suelo
        for (i in -1 until COSTURAS.size) {
            val x0 = w * (if (i < 0) COSTURAS[0] - 0.245f else COSTURAS[i])
            val t = tn(Offset(x0 + w * 0.12f, ch * 0.35f))
            drawRect(t.b(0.1f), Offset(x0 + w * 0.02f, 0f), Size(w * 0.055f, claros))
            drawRect(t.s(0.045f), Offset(x0 + w * 0.14f, 0f), Size(w * 0.09f, suelo))
            drawRect(t.s(0.04f), Offset(x0 + w * 0.185f, 0f), Size(w * 0.045f, suelo))
            listOf(0.066f, 0.08f, 0.165f, 0.179f).forEach { dx -> drawRect(lista, Offset(x0 + w * dx, 0f), Size(w * 0.0042f, suelo)) }
        }
        // Las costuras: la tela doble, más opaca, entre dos hileras de puntadas.
        val puntadas = PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.2f * u))
        COSTURAS.forEach { fx ->
            val x = w * fx
            val t = tn(Offset(x, ch * 0.35f))
            drawRect(t.s(0.1f), Offset(x - w * 0.007f, 0f), Size(w * 0.014f, suelo))
            listOf(-0.007f, 0.007f).forEach { dx -> drawLine(t.s(0.32f), Offset(x + w * dx, 0f), Offset(x + w * dx, suelo), 0.7f * u, pathEffect = puntadas) }
            drawRect(t.b(0.3f), Offset(x - w * 0.007f - 1.2f * u, 0f), Size(1.2f * u, claros))
        }
        // La sombra de un viento de fuera, que la lona deja ver de día.
        if (!o) {
            drawLine(sombraMueble(0.06f), Offset(w * 0.31f, 0f), Offset(w * 0.69f, ch * 0.7f), w * 0.013f)
            drawLine(sombraMueble(0.09f), Offset(w * 0.31f, 0f), Offset(w * 0.69f, ch * 0.7f), w * 0.004f)
        }
        // La banda de refuerzo de arriba, con sus ollaos cosidos, el cabo que sale de cada uno y las arrugas que tira.
        val by0 = w * 0.024f; val by1 = w * 0.07f
        val tb = tn(Offset(w * 0.5f, by1))
        drawRect(tb.s(0.08f), Offset(0f, by0), Size(w, by1 - by0))
        drawLine(tb.s(0.3f), Offset(0f, by0 + 2f * u), Offset(w, by0 + 2f * u), 0.7f * u, pathEffect = puntadas)
        drawLine(tb.s(0.3f), Offset(0f, by1 - 2f * u), Offset(w, by1 - 2f * u), 0.7f * u, pathEffect = puntadas)
        drawRect(tb.s(0.14f), Offset(0f, by1), Size(w, 1.6f * u))
        val canamo = mezcla(CORREA, LONA, 0.5f)
        for (i in 0..8) {
            val p = Offset(w * (0.045f + 0.1135f * i), (by0 + by1) / 2)
            val t = tn(p)
            drawLine(t.s(0.09f), p + Offset(0f, w * 0.014f), p + Offset(-w * 0.022f, w * 0.1f), 1.2f * u)
            drawLine(t.b(0.16f), p + Offset(1.4f * u, w * 0.014f), p + Offset(-w * 0.022f + 1.4f * u, w * 0.1f), 0.9f * u)
            drawLine(t.s(0.07f), p + Offset(0f, w * 0.014f), p + Offset(w * 0.018f, w * 0.075f), 1f * u)
            drawCircle(t.c(mezcla(LONA_OSC, CORREA, 0.35f)), w * 0.0105f, p)
            drawCircle(t.b(0.3f), w * 0.0105f, p, style = Stroke(0.7f * u))
            drawCircle(t.c(mezcla(CAOBA_OSC, NOCHE, 0.3f)), w * 0.0058f, p)
            drawLine(t.s(0.25f), p + Offset(1.5f * u, 1.5f * u), Offset(p.x + w * 0.012f + 1.5f * u, 0f), 1.6f * u)
            drawLine(t.c(canamo), p, Offset(p.x + w * 0.012f, 0f), 1.8f * u, StrokeCap.Round)
            drawLine(t.b(0.35f), p - Offset(0.5f * u, 0f), Offset(p.x + w * 0.012f - 0.5f * u, 0f), 0.5f * u)
        }
        // El remiendo: un cuadro de lona más nueva, cosido a punto por encima.
        val rem = Rect(w * 0.52f, ch * 0.2f, w * 0.605f, ch * 0.315f)
        rotate(5f, rem.center) {
            val t = tn(rem.center)
            drawRect(t.s(0.1f), rem.topLeft + Offset(1.4f * u, 1.8f * u), rem.size)
            drawRect((if (o) t.c(LONA) else mezcla(LONA, LUZ_MUEBLE, 0.3f)).copy(alpha = if (o) 0.5f else 0.75f), rem.topLeft, rem.size)
            drawRect(t.s(0.07f), Offset(rem.left + rem.width * 0.6f, rem.top), Size(rem.width * 0.4f, rem.height))
            drawRect(t.s(0.4f), Offset(rem.left + 2f * u, rem.top + 2f * u), Size(rem.width - 4f * u, rem.height - 4f * u), style = Stroke(0.8f * u, pathEffect = puntadas))
            drawRect(t.b(0.35f), rem.topLeft, Size(rem.width, 0.9f * u))
        }
        // Las pinceladas de la tela y, abajo, la humedad y el barro que suben del suelo.
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else LONA_OSC, 4001, 44, u, 0.1f)
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else mezcla(LONA, LUZ_MUEBLE, 0.5f), 4002, 20, u, 0.1f)
        val ts = tn(Offset(w * 0.5f, suelo))
        pinceladas(Rect(0f, suelo - w * 0.14f, w, suelo), if (o) NOCHE else mezcla(TIERRA, LONA_OSC, 0.3f), 4003, 30, u, 0.2f)
        drawRect(ts.c(mezcla(LONA_OSC, TIERRA, 0.55f)).copy(alpha = 0.55f), Offset(0f, suelo - w * 0.026f), Size(w, w * 0.026f))
        drawLine(ts.s(0.3f), Offset(0f, suelo - w * 0.028f), Offset(w, suelo - w * 0.028f), 0.7f * u, pathEffect = puntadas)
    }

    /** El suelo de la tienda: tierra pisada, con su paja, unas piedrecillas y la sombra del rincón. */
    private fun DrawScope.tierra(w: Float, suelo: Float, alto: Float) {
        val u = density
        val hs = alto - suelo
        val t = tn(Offset(w * 0.5f, alto)); val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(TIERRA), Offset(0f, suelo), Size(w, hs))
        drawRect(t.b(0.1f), Offset(0f, suelo + hs * 0.55f), Size(w * 0.6f, hs * 0.45f))
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else mezcla(TIERRA, CAOBA_OSC, 0.5f), 4040, 22, u, 0.18f)
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else mezcla(TIERRA, LONA, 0.55f), 4041, 14, u, 0.16f)
        val paja = t.c(mezcla(LONA, BRONCE, 0.4f))
        for (k in 0 until 30) {
            val p = Offset(w * azar(4042, k), suelo + hs * (0.14f + 0.84f * azar(4043, k)))
            val a = (azar(4044, k) - 0.5f) * 1.2f
            val l = w * (0.02f + 0.035f * azar(4045, k))
            val q = p + Offset(l * cos(a), l * sin(a))
            drawLine(t.s(0.25f), p + Offset(1f * u, 1.3f * u), q + Offset(1f * u, 1.3f * u), 0.9f * u)
            drawLine(paja.copy(alpha = 0.9f), p, q, 1f * u, StrokeCap.Round)
        }
        for (k in 0 until 6) {
            val p = Offset(w * (0.05f + 0.9f * azar(4046, k)), suelo + hs * (0.3f + 0.6f * azar(4047, k)))
            val r = w * (0.004f + 0.004f * azar(4048, k))
            drawOval(t.s(0.3f), Offset(p.x - r * 0.6f, p.y - r * 0.2f), Size(r * 2.6f, r * 1.1f))
            drawOval(t.c(mezcla(TIERRA, HOJALATA, 0.5f)), Offset(p.x - r, p.y - r * 0.6f), Size(r * 2f, r * 1.2f))
            drawOval(t.b(0.35f), Offset(p.x - r * 0.7f, p.y - r * 0.55f), Size(r, r * 0.5f))
        }
        drawRect(tt.s(0.32f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- en la lona y en el poste

    /**
     * El mapa de marcha clavado a la lona con alfileres, un poco torcido: el papel con sus dobleces, el marco a pluma, el río,
     * los cerros a plumadas, el camino en rojo con sus pueblos, la cartela, la escala y la flecha del norte (todo sin letra);
     * el borde de abajo cuelga y la punta de la derecha se levanta.
     */
    private fun DrawScope.mapa(r: Rect) {
        val u = density
        // El papel es claro: de noche su luz cae más deprisa, como la de la lona, para que no quede como un recorte.
        val t = tn(r.center).let { Tono(it.o, it.k.pow(1.7f)) }
        val rw = r.width; val rh = r.height
        rotate(-2.2f, r.center) {
            val esq = rw * 0.13f
            val hoja = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top)
                lineTo(r.right, r.bottom - esq); lineTo(r.right - esq, r.bottom)
                cubicTo(r.left + rw * 0.6f, r.bottom + rh * 0.03f, r.left + rw * 0.3f, r.bottom - rh * 0.02f, r.left, r.bottom + rh * 0.012f)
                close()
            }
            sombraEnPared(hoja, r.center, size.height, 0.2f)
            val papel = mezcla(PAPEL, LONA, 0.35f)
            drawPath(hoja, t.c(papel))
            clipPath(hoja) {
                pinceladas(r, if (esNoche) NOCHE else CORREA, 4070, 8, u, 0.08f)
                val m = rw * 0.045f
                val campo = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
                val cw = campo.width; val chh = campo.height
                fun p(fx: Float, fy: Float) = Offset(campo.left + cw * fx, campo.top + chh * fy)
                val tinta = t.c(mezcla(TINTA, CORREA, 0.45f))
                drawRect(tinta.copy(alpha = 0.75f), campo.topLeft, campo.size, style = Stroke(0.9f * u))
                drawRect(tinta.copy(alpha = 0.45f), campo.topLeft + Offset(2.2f * u, 2.2f * u), Size(cw - 4.4f * u, chh - 4.4f * u), style = Stroke(0.4f * u))
                // El río y su afluente.
                val agua = t.c(mezcla(AZUL, papel, 0.5f))
                val rio = Path().apply {
                    moveTo(p(0f, 0.64f).x, p(0f, 0.64f).y)
                    cubicTo(p(0.18f, 0.42f).x, p(0.18f, 0.42f).y, p(0.3f, 0.8f).x, p(0.3f, 0.8f).y, p(0.48f, 0.56f).x, p(0.48f, 0.56f).y)
                    cubicTo(p(0.62f, 0.38f).x, p(0.62f, 0.38f).y, p(0.8f, 0.5f).x, p(0.8f, 0.5f).y, p(1f, 0.2f).x, p(1f, 0.2f).y)
                }
                drawPath(rio, agua, style = Stroke(2.8f * u, cap = StrokeCap.Round))
                drawPath(rio, tinta.copy(alpha = 0.5f), style = Stroke(0.5f * u))
                val afl = Path().apply {
                    moveTo(p(0.48f, 0.56f).x, p(0.48f, 0.56f).y)
                    cubicTo(p(0.52f, 0.72f).x, p(0.52f, 0.72f).y, p(0.42f, 0.84f).x, p(0.42f, 0.84f).y, p(0.5f, 1f).x, p(0.5f, 1f).y)
                }
                drawPath(afl, agua, style = Stroke(1.4f * u, cap = StrokeCap.Round))
                // Los cerros, a plumadas.
                listOf(Offset(0.47f, 0.2f), Offset(0.76f, 0.76f), Offset(0.9f, 0.62f), Offset(0.14f, 0.36f)).forEachIndexed { i, f ->
                    val c = p(f.x, f.y)
                    val rr = rw * (0.05f + 0.012f * (i % 2))
                    for (j in 0 until 14) {
                        val a = j * 0.4488f + 0.3f * i
                        drawLine(tinta.copy(alpha = 0.62f), Offset(c.x + rr * 0.45f * cos(a), c.y + rr * 0.3f * sin(a)), Offset(c.x + rr * cos(a), c.y + rr * 0.68f * sin(a)), 0.55f * u)
                    }
                }
                // El camino de la marcha, en rojo, y sus pueblos.
                val ruta = listOf(p(0.06f, 0.9f), p(0.26f, 0.82f), p(0.44f, 0.63f), p(0.62f, 0.3f), p(0.92f, 0.36f))
                val camino = Path().apply { moveTo(ruta[0].x, ruta[0].y); ruta.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(camino, t.c(ROJO).copy(alpha = 0.85f), style = Stroke(1.1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.5f * u, 2f * u))))
                ruta.drop(1).forEachIndexed { i, q ->
                    drawCircle(t.c(papel), 2.1f * u, q)
                    drawCircle(tinta, 2.1f * u, q, style = Stroke(0.7f * u))
                    drawCircle(t.c(ROJO), 0.9f * u, q)
                    if (i % 2 == 0) for (j in 0 until 3) drawRect(tinta.copy(alpha = 0.7f), q + Offset((2.8f + 2.2f * j) * u, (-2.5f + 1.5f * (j % 2)) * u), Size(1.5f * u, 1.5f * u))
                }
                // La cartela (renglones sin letra), la escala y la flecha del norte.
                val car = Rect(p(0.04f, 0.06f).x, p(0.04f, 0.06f).y, p(0.34f, 0.24f).x, p(0.34f, 0.24f).y)
                drawRect(t.c(mezcla(papel, PAPEL, 0.6f)), car.topLeft, car.size)
                drawRect(tinta.copy(alpha = 0.7f), car.topLeft, car.size, style = Stroke(0.6f * u))
                listOf(0.36f to 0.7f, 0.66f to 0.45f).forEach { (fy, fw) ->
                    drawLine(tinta.copy(alpha = 0.6f), Offset(car.center.x - car.width * fw / 2, car.top + car.height * fy), Offset(car.center.x + car.width * fw / 2, car.top + car.height * fy), 0.7f * u)
                }
                val e0 = p(0.62f, 0.93f); val e1 = p(0.86f, 0.93f)
                drawLine(tinta.copy(alpha = 0.7f), e0, e1, 0.7f * u)
                for (j in 0..4) drawLine(tinta.copy(alpha = 0.7f), Offset(e0.x + (e1.x - e0.x) * j / 4f, e0.y - 1.8f * u), Offset(e0.x + (e1.x - e0.x) * j / 4f, e0.y), 0.6f * u)
                val nn = p(0.9f, 0.12f)
                drawLine(tinta.copy(alpha = 0.7f), nn + Offset(0f, rh * 0.07f), nn - Offset(0f, rh * 0.05f), 0.7f * u)
                drawPath(poligono(nn.x, nn.y - rh * 0.075f, nn.x - 1.8f * u, nn.y - rh * 0.035f, nn.x + 1.8f * u, nn.y - rh * 0.035f), tinta.copy(alpha = 0.8f))
                drawLine(tinta.copy(alpha = 0.6f), nn - Offset(rw * 0.022f, 0f), nn + Offset(rw * 0.022f, 0f), 0.6f * u)
                // Los dobleces y la comba del papel.
                listOf(1f / 3f, 2f / 3f).forEach { fx ->
                    drawLine(t.s(0.16f), Offset(r.left + rw * fx, r.top), Offset(r.left + rw * fx, r.bottom + rh * 0.03f), 0.9f * u)
                    drawLine(t.b(0.4f), Offset(r.left + rw * fx + 1f * u, r.top), Offset(r.left + rw * fx + 1f * u, r.bottom + rh * 0.03f), 0.7f * u)
                }
                drawLine(t.s(0.14f), Offset(r.left, r.top + rh * 0.5f), Offset(r.right, r.top + rh * 0.5f), 0.9f * u)
                drawLine(t.b(0.35f), Offset(r.left, r.top + rh * 0.5f + 1f * u), Offset(r.right, r.top + rh * 0.5f + 1f * u), 0.7f * u)
                drawRect(t.s(0.07f), Offset(r.left + rw * 0.68f, r.top), Size(rw * 0.32f, rh * 1.1f))
                drawRect(t.s(0.06f), Offset(r.left, r.top + rh * 0.82f), Size(rw, rh * 0.3f))
            }
            // La punta que se levanta: el revés del papel y su sombra.
            val doblez = poligono(r.right, r.bottom - esq, r.right - esq * 0.25f, r.bottom - esq * 0.22f, r.right - esq, r.bottom)
            translate(1.5f * u, 2f * u) { drawPath(doblez, t.s(0.3f)) }
            drawPath(doblez, t.c(mezcla(papel, LONA_OSC, 0.4f)))
            // Los alfileres.
            listOf(Offset(r.left + 3f * u, r.top + 3f * u), Offset(r.right - 3f * u, r.top + 3f * u), Offset(r.left + 3f * u, r.bottom - 2f * u)).forEach { q ->
                drawLine(t.s(0.3f), q, q + Offset(2.6f * u, 2.6f * u), 0.7f * u)
                clavo(q, 1.5f * u, t)
            }
        }
    }

    /** El centro del poste de la tienda y el alto de su gancho, en fracción del ancho. */
    private const val POSTE = 0.895f
    private const val GANCHO = 0.088f

    /**
     * El poste de pino de la tienda, de arriba abajo (el mueble lo tapa en medio): redondo, con su veta, la ligadura de
     * cáñamo de la que sale un cabo hacia el techo y el zuncho de hierro con su gancho.
     */
    private fun DrawScope.poste(w: Float, hasta: Float) {
        val u = density
        val pw = w * 0.036f
        val x0 = w * POSTE - pw / 2
        val pino = mezcla(CORREA, LONA, 0.42f)
        sombraEnPared(Path().apply { addRect(Rect(x0, 0f, x0 + pw, hasta)) }, Offset(w * POSTE, w * 0.3f), hasta, 0.14f)
        val tramos = 16
        for (k in 0 until tramos) {
            val y0 = hasta * k / tramos; val y1 = hasta * (k + 1) / tramos + 0.5f
            val t = tn(Offset(w * POSTE, (y0 + y1) / 2))
            drawRect(t.c(pino), Offset(x0, y0), Size(pw, y1 - y0))
            clipRect(x0, y0, x0 + pw, y1) { cilindro(Rect(x0, y0, x0 + pw, y1), t, 1.3f) }
        }
        vetas(Rect(x0, 0f, x0 + pw, hasta), (if (esNoche) NOCHE else CAOBA_OSC).copy(alpha = 0.22f), 4080, 0.5f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        // La ligadura de cáñamo y el cabo que sube hacia el techo, a la izquierda.
        val tl = tn(Offset(w * POSTE, w * 0.04f))
        val canamo = mezcla(CORREA, LONA, 0.5f)
        val cabo0 = Offset(x0 + pw * 0.2f, w * 0.05f); val cabo1 = Offset(w * 0.69f, -w * 0.01f)
        drawLine(tl.s(0.22f), cabo0 + Offset(w * 0.012f, w * 0.014f), cabo1 + Offset(w * 0.012f, w * 0.014f), 2.2f * u)
        drawLine(tl.c(canamo), cabo0, cabo1, 2.6f * u, StrokeCap.Round)
        drawLine(tl.b(0.4f), cabo0 - Offset(0f, 0.8f * u), cabo1 - Offset(0f, 0.8f * u), 0.6f * u)
        for (j in 0 until 11) {
            val f = (j + 0.5f) / 11f
            val q = Offset(cabo0.x + (cabo1.x - cabo0.x) * f, cabo0.y + (cabo1.y - cabo0.y) * f)
            drawLine(tl.s(0.3f), q + Offset(-1f * u, -1.2f * u), q + Offset(1f * u, 1.2f * u), 0.6f * u)
        }
        for (j in 0 until 5) {
            val y = w * (0.03f + 0.0085f * j)
            drawLine(tl.s(0.3f), Offset(x0 - 1.5f * u, y + 2.2f * u), Offset(x0 + pw + 1.5f * u, y + 0.6f * u), 1.4f * u)
            drawLine(tl.c(canamo), Offset(x0 - 1.5f * u, y + 0.8f * u), Offset(x0 + pw + 1.5f * u, y - 0.8f * u), 2.6f * u, StrokeCap.Round)
            drawLine(tl.b(0.4f), Offset(x0 - 1f * u, y), Offset(x0 + pw * 0.4f, y - 0.5f * u), 0.6f * u)
        }
        // El zuncho de hierro y su gancho.
        val hierro = mezcla(FIELTRO, HOJALATA, 0.35f)
        val tz = tn(Offset(w * POSTE, w * GANCHO))
        val z = Rect(x0 - 1.2f * u, w * (GANCHO - 0.012f), x0 + pw + 1.2f * u, w * (GANCHO + 0.004f))
        drawRect(tz.s(0.3f), Offset(z.left, z.bottom), Size(z.width, 1.6f * u))
        drawRect(tz.c(hierro), z.topLeft, z.size)
        drawRect(tz.b(0.4f), z.topLeft, Size(z.width * 0.5f, 0.9f * u))
        clavo(Offset(z.left + z.width * 0.25f, z.center.y), 1.1f * u, tz, hierro)
        val g = Offset(w * POSTE, z.bottom)
        val gancho = Path().apply {
            moveTo(g.x, g.y - 1f * u)
            cubicTo(g.x, g.y + w * 0.022f, g.x - w * 0.02f, g.y + w * 0.022f, g.x - w * 0.02f, g.y + w * 0.006f)
        }
        drawPath(gancho, tz.c(hierro), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(gancho, tz.b(0.4f), style = Stroke(0.6f * u))
    }

    /** Un punto [p] girado [grados] alrededor de [c] (como `rotate`). */
    private fun girar(p: Offset, c: Offset, grados: Float): Offset {
        val a = Math.toRadians(grados.toDouble()).toFloat()
        val dx = p.x - c.x; val dy = p.y - c.y
        return Offset(c.x + dx * cos(a) - dy * sin(a), c.y + dx * sin(a) + dy * cos(a))
    }

    /**
     * La casaca de oficial colgada del gancho por la presilla del cuello, vista de frente y un poco ladeada: el paño azul que
     * cae con su peso (los hombros vencidos, las mangas lacias, los pliegues que bajan del cuello, el bajo desigual), el
     * cuello y las vueltas rojos, las solapas blancas con sus botones dorados, los faldones con el forro blanco vuelto y la
     * charretera de flecos en un hombro.
     */
    private fun DrawScope.casaca(g: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val cx = g.x; val y0 = g.y
        val t = tn(Offset(cx - w * 0.04f, y0 + w * 0.2f))
        val blanco = mezcla(PAPEL, LONA, 0.3f)
        val oro = mezcla(BRONCE, LUZ_MUEBLE, 0.15f)
        fun x(f: Float) = cx + w * f
        fun y(f: Float) = y0 + w * f
        val cuerpo = Path().apply {
            moveTo(x(-0.024f), y(0.012f))
            cubicTo(x(-0.05f), y(0.03f), x(-0.086f), y(0.052f), x(-0.103f), y(0.088f))
            cubicTo(x(-0.097f), y(0.14f), x(-0.1f), y(0.2f), x(-0.093f), y(0.255f))
            cubicTo(x(-0.094f), y(0.31f), x(-0.084f), y(0.37f), x(-0.086f), y(0.418f))
            cubicTo(x(-0.066f), y(0.428f), x(-0.04f), y(0.41f), x(-0.012f), y(0.424f))
            lineTo(x(0.001f), y(0.385f))
            lineTo(x(0.012f), y(0.404f))
            cubicTo(x(0.034f), y(0.394f), x(0.056f), y(0.41f), x(0.076f), y(0.398f))
            cubicTo(x(0.08f), y(0.37f), x(0.092f), y(0.31f), x(0.093f), y(0.255f))
            cubicTo(x(0.1f), y(0.2f), x(0.097f), y(0.14f), x(0.103f), y(0.088f))
            cubicTo(x(0.086f), y(0.052f), x(0.05f), y(0.03f), x(0.024f), y(0.012f))
            close()
        }
        /** Una manga: [s] el lado, [largo] hasta dónde cae y [codo] cuánto se abre en el codo. */
        fun manga(s: Float, largo: Float, codo: Float) = Path().apply {
            moveTo(x(s * 0.064f), y(0.072f))
            cubicTo(x(s * 0.088f), y(0.06f), x(s * 0.108f), y(0.076f), x(s * 0.113f), y(0.102f))
            cubicTo(x(s * (0.124f + codo)), y(0.16f), x(s * (0.126f + codo)), y(0.21f), x(s * (0.118f + codo * 0.4f)), y(0.25f))
            cubicTo(x(s * 0.116f), y(0.28f), x(s * 0.124f), y(largo - 0.03f), x(s * 0.126f), y(largo))
            lineTo(x(s * 0.076f), y(largo + 0.008f))
            cubicTo(x(s * 0.074f), y(0.27f), x(s * (0.066f + codo * 0.5f)), y(0.2f), x(s * 0.064f), y(0.072f))
            close()
        }
        val largoI = 0.345f; val largoD = 0.328f
        val mIzq = manga(-1f, largoI, 0.01f); val mDer = manga(1f, largoD, 0f)
        val todo = Path().apply { addPath(cuerpo); addPath(mIzq); addPath(mDer) }
        rotate(2.2f, g) {
            sombraEnPared(todo, Offset(cx - w * 0.06f, y(0.2f)), sup, 0.24f)

            // El paño: el cuerpo y los faldones.
            drawPath(cuerpo, t.c(PANO))
            clipPath(cuerpo) {
                drawRect(t.b(0.12f), Offset(x(-0.11f), y0), Size(w * 0.06f, w * 0.44f))
                drawRect(t.s(0.2f), Offset(x(0.03f), y0), Size(w * 0.09f, w * 0.44f))
                // El forro blanco vuelto de los faldones, con su vivo rojo: una punta por fuera y otra por dentro de cada uno.
                listOf(-1f, 1f).forEach { s ->
                    val bajo = if (s < 0) 0.43f else 0.41f
                    listOf(
                        poligono(x(s * 0.094f), y(0.33f), x(s * 0.064f), y(bajo), x(s * 0.092f), y(bajo)),
                        poligono(x(s * 0.004f), y(0.335f), x(s * 0.034f), y(bajo), x(s * 0.008f), y(bajo)),
                    ).forEach { vuelta ->
                        drawPath(vuelta, t.c(blanco))
                        drawPath(vuelta, t.c(ROJO), style = Stroke(1f * u))
                        if (s > 0) drawPath(vuelta, t.s(0.16f))
                    }
                }
                // Los pliegues largos de los faldones: el valle oscuro y su lomo claro.
                listOf(-0.07f, -0.046f, -0.026f, 0.03f, 0.052f).forEachIndexed { i, f ->
                    val p = Path().apply { moveTo(x(f * 1.06f), y(0.268f)); cubicTo(x(f + 0.008f), y(0.31f), x(f - 0.008f), y(0.37f), x(f * 0.92f), y(0.43f)) }
                    drawPath(p, t.s(if (i % 2 == 0) 0.34f else 0.22f), style = Stroke((if (i % 2 == 0) 2.4f else 1.4f) * u))
                    translate(-2.2f * u, 0f) { drawPath(p, t.b(0.16f), style = Stroke(1.1f * u)) }
                }
                // La abertura entre los dos faldones: uno monta sobre el otro.
                drawLine(t.s(0.45f), Offset(x(0.002f), y(0.262f)), Offset(x(0.001f), y(0.39f)), 2.2f * u)
                drawLine(t.b(0.16f), Offset(x(0.002f) - 2.4f * u, y(0.27f)), Offset(x(0.001f) - 2.4f * u, y(0.385f)), 1f * u)
                // El talle, donde el faldón se frunce.
                drawLine(t.s(0.34f), Offset(x(-0.095f), y(0.262f)), Offset(x(0.095f), y(0.262f)), 1.4f * u)
                drawRect(t.s(0.16f), Offset(x(-0.1f), y(0.262f)), Size(w * 0.2f, w * 0.016f))
                // El bajo, más hondo, y el polvo del camino.
                drawRect(t.s(0.14f), Offset(x(-0.1f), y(0.385f)), Size(w * 0.2f, w * 0.05f))
                pinceladas(Rect(x(-0.11f), y0, x(0.11f), y(0.43f)), if (esNoche) NOCHE else mezcla(PANO, HOJALATA, 0.4f), 4090, 10, u, 0.1f)
                pinceladas(Rect(x(-0.11f), y(0.36f), x(0.11f), y(0.43f)), if (esNoche) NOCHE else TIERRA, 4091, 6, u, 0.16f)
            }
            // Las solapas blancas, abotonadas, con el vivo rojo y los pliegues que bajan del cuello.
            listOf(-1f, 1f).forEach { s ->
                val solapa = Path().apply {
                    moveTo(x(s * 0.002f), y(0.05f))
                    cubicTo(x(s * 0.02f), y(0.04f), x(s * 0.05f), y(0.05f), x(s * 0.066f), y(0.078f))
                    cubicTo(x(s * 0.066f), y(0.14f), x(s * 0.05f), y(0.2f), x(s * 0.04f), y(0.256f))
                    lineTo(x(s * 0.002f), y(0.262f))
                    close()
                }
                translate(0f, 2f * u) { drawPath(solapa, t.s(0.25f)) }
                drawPath(solapa, t.c(blanco))
                clipPath(solapa) {
                    if (s > 0) drawRect(t.s(0.16f), Offset(x(0f), y(0.03f)), Size(w * 0.08f, w * 0.25f))
                    listOf(0.012f to 0.03f, 0.03f to 0.042f).forEachIndexed { i, (a, z) ->
                        val pl = Path().apply { moveTo(x(s * a), y(0.058f)); cubicTo(x(s * (a + 0.016f)), y(0.11f), x(s * (z - 0.012f)), y(0.17f), x(s * z), y(0.256f)) }
                        drawPath(pl, t.s(if (i == 0) 0.2f else 0.12f), style = Stroke((3f - i) * u))
                        translate(-s * 2.4f * u, 0f) { drawPath(pl, t.b(0.3f), style = Stroke(1f * u)) }
                    }
                    drawRect(t.s(0.12f), Offset(x(-0.07f), y(0.235f)), Size(w * 0.14f, w * 0.03f))
                }
                drawPath(solapa, t.c(ROJO), style = Stroke(0.9f * u))
                for (j in 0 until 7) {
                    val f = j / 6f
                    val q = Offset(x(s * (0.056f - 0.02f * f - 0.004f * sin(f * 3.14f))), y(0.086f + 0.158f * f))
                    drawCircle(t.s(0.3f), 1.5f * u, q + Offset(0.7f * u, 0.8f * u))
                    drawCircle(t.c(oro), 1.4f * u, q)
                    destello(q - Offset(0.4f * u, 0.4f * u), 0.5f * u, t)
                }
            }
            drawLine(t.s(0.34f), Offset(x(0.002f), y(0.055f)), Offset(x(0.001f), y(0.262f)), 1.1f * u)
            // Las mangas, lacias, con las arrugas del codo, la vuelta roja y la pata blanca de tres botones.
            listOf(Triple(-1f, mIzq, largoI), Triple(1f, mDer, largoD)).forEach { (s, m, largo) ->
                translate(1.8f * u, 1.4f * u) { drawPath(m, t.s(0.26f)) }
                drawPath(m, t.c(if (s < 0) mezcla(PANO, HOJALATA, 0.1f) else PANO))
                clipPath(m) {
                    if (s < 0) { drawRect(t.b(0.16f), Offset(x(-0.14f), y(0.05f)), Size(w * 0.035f, w * 0.32f)); drawRect(t.s(0.16f), Offset(x(-0.09f), y(0.05f)), Size(w * 0.03f, w * 0.32f)) }
                    else drawRect(t.s(0.24f), Offset(x(0.095f), y(0.05f)), Size(w * 0.05f, w * 0.32f))
                    listOf(0.16f, 0.182f, 0.205f, 0.255f).forEachIndexed { i, fy ->
                        val p = Path().apply { moveTo(x(s * 0.068f), y(fy)); cubicTo(x(s * 0.088f), y(fy + 0.014f), x(s * 0.106f), y(fy + 0.002f), x(s * 0.132f), y(fy + 0.018f - 0.01f * i)) }
                        drawPath(p, t.s(0.3f), style = Stroke(1.4f * u))
                        translate(0f, 1.5f * u) { drawPath(p, t.b(0.14f), style = Stroke(0.8f * u)) }
                    }
                    // La vuelta roja y su pata.
                    val vy = largo - 0.042f
                    drawRect(t.c(ROJO), Offset(x(if (s < 0) -0.135f else 0.07f), y(vy)), Size(w * 0.065f, w * 0.06f))
                    drawRect(t.s(0.3f), Offset(x(if (s < 0) -0.135f else 0.07f), y(vy)), Size(w * 0.065f, 1.2f * u))
                    val px = x(s * 0.1f) - w * 0.008f
                    drawRect(t.c(blanco), Offset(px, y(vy - 0.01f)), Size(w * 0.016f, w * 0.07f))
                    for (j in 0 until 3) drawCircle(t.c(oro), 1.1f * u, Offset(px + w * 0.008f, y(vy + 0.002f + 0.014f * j)))
                    if (s > 0) drawRect(t.s(0.2f), Offset(x(0.07f), y(vy - 0.01f)), Size(w * 0.065f, w * 0.07f))
                }
            }
            filoContorno(mIzq, t, x(-0.1f), false, 0.6f)
            // Los pliegues de los hombros, que bajan del cuello.
            listOf(-1f, 1f).forEach { s ->
                val p = Path().apply { moveTo(x(s * 0.03f), y(0.024f)); cubicTo(x(s * 0.055f), y(0.04f), x(s * 0.075f), y(0.06f), x(s * 0.082f), y(0.1f)) }
                drawPath(p, t.s(0.28f), style = Stroke(1.6f * u))
            }
            // El cuello rojo: por detrás (por donde cuelga) y sus dos puntas por delante.
            drawPath(poligono(x(-0.022f), y(0.016f), x(-0.012f), y(-0.012f), x(0.012f), y(-0.012f), x(0.022f), y(0.016f)), t.c(mezcla(ROJO, CAOBA_OSC, 0.4f)))
            listOf(-1f, 1f).forEach { s ->
                val punta = poligono(x(s * 0.026f), y(0.03f), x(s * 0.02f), y(-0.008f), x(s * 0.003f), y(0.014f), x(s * 0.003f), y(0.056f))
                drawPath(punta, t.c(ROJO))
                if (s > 0) drawPath(punta, t.s(0.2f))
                drawLine(t.b(0.35f), Offset(x(s * 0.02f), y(-0.008f)), Offset(x(s * 0.003f), y(0.014f)), 0.8f * u)
            }
            // La charretera de flecos en el hombro de la derecha y la contracharretera, sin flecos, en el otro.
            listOf(-1f, 1f).forEach { s ->
                val a = Offset(x(s * 0.034f), y(0.022f)); val b = Offset(x(s * 0.092f), y(0.068f))
                drawLine(t.s(0.3f), a + Offset(1f * u, 1.6f * u), b + Offset(1f * u, 1.6f * u), w * 0.017f)
                drawLine(t.c(oro), a, b, w * 0.016f)
                drawLine(t.b(0.5f), a - Offset(0f, w * 0.005f), b - Offset(0f, w * 0.005f), 0.8f * u)
                drawLine(t.c(BRONCE_OSC).copy(alpha = 0.7f), a + Offset(0f, w * 0.004f), b + Offset(0f, w * 0.004f), 0.6f * u)
                val media = Rect(b.x - w * 0.02f, b.y - w * 0.012f, b.x + w * 0.02f, b.y + w * 0.02f)
                drawOval(t.s(0.3f), media.topLeft + Offset(1f * u, 1.6f * u), media.size)
                drawOval(t.c(oro), media.topLeft, media.size)
                drawArc(t.c(BRONCE_OSC), 0f, 180f, false, media.topLeft + Offset(w * 0.004f, w * 0.004f), Size(media.width - w * 0.008f, media.height - w * 0.008f), style = Stroke(0.8f * u))
                if (s > 0) for (j in 0 until 8) {
                    val fx = media.left + media.width * (0.08f + 0.84f * j / 7f)
                    val largo = w * (0.03f + 0.005f * sin(j * 0.45f))
                    drawLine(t.c(if (j % 2 == 0) oro else BRONCE_OSC), Offset(fx, media.center.y + w * 0.008f), Offset(fx + w * 0.002f, media.center.y + w * 0.008f + largo), 1.3f * u, StrokeCap.Round)
                }
                destello(Offset(media.left + media.width * 0.3f, media.top + media.height * 0.3f), 0.9f * u, t)
            }
        }
    }

    /**
     * El sable del oficial en su vaina, colgado del gancho por los tiros de cuero blanco, que cruzan el hombro de la casaca: la
     * empuñadura con su guarda de latón y el gavilán, la dragona dorada con su borla, y la vaina de cuero negro con el
     * brocal, la abrazadera y la contera de latón.
     */
    private fun DrawScope.sable(g: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val giro = 7f
        // El sable se dibuja a plomo y se gira alrededor del gancho.
        val sx = g.x - w * 0.115f
        val yT = g.y + w * 0.085f
        val ls = w * 0.33f
        val t = tn(Offset(sx - w * 0.03f, yT + ls * 0.4f))
        val cueroN = mezcla(FIELTRO, CAOBA_OSC, 0.2f)
        val blanco = mezcla(PAPEL, LONA, 0.3f)
        val vaina = Path().apply {
            moveTo(sx - w * 0.011f, yT)
            cubicTo(sx - w * 0.011f, yT + ls * 0.4f, sx - w * 0.022f, yT + ls * 0.75f, sx - w * 0.046f, yT + ls)
            lineTo(sx - w * 0.036f, yT + ls + w * 0.004f)
            cubicTo(sx - w * 0.006f, yT + ls * 0.76f, sx + w * 0.011f, yT + ls * 0.4f, sx + w * 0.011f, yT)
            close()
        }
        val anilla1 = Offset(sx + w * 0.014f, yT + w * 0.022f)
        val anilla2 = Offset(sx + w * 0.012f, yT + w * 0.118f)
        rotate(giro, g) {
            sombraEnPared(vaina, Offset(sx, yT + ls * 0.5f), sup, 0.22f)
            // Los tiros de cuero blanco, del gancho a las anillas.
            listOf(anilla1 to 0.012f, anilla2 to 0.03f).forEach { (a, comba) ->
                val tiro = Path().apply {
                    moveTo(g.x, g.y)
                    cubicTo(g.x - (g.x - a.x) * 0.3f, g.y + (a.y - g.y) * 0.3f + w * comba, g.x - (g.x - a.x) * 0.7f, g.y + (a.y - g.y) * 0.7f + w * comba, a.x, a.y)
                }
                translate(1.4f * u, 1.8f * u) { drawPath(tiro, t.s(0.3f), style = Stroke(2.6f * u)) }
                drawPath(tiro, t.c(blanco), style = Stroke(2.8f * u, cap = StrokeCap.Round))
                translate(0.8f * u, 0.8f * u) { drawPath(tiro, t.s(0.2f), style = Stroke(0.7f * u)) }
            }
            // La vaina de cuero negro con sus tres piezas de latón.
            drawPath(vaina, t.c(cueroN))
            clipPath(vaina) {
                drawRect(t.b(0.22f), Offset(sx - w * 0.05f, yT), Size(w * 0.045f, ls + w * 0.01f))
                listOf(0f to 0.05f, 0.1f to 0.13f).forEach { (a, z) ->
                    drawRect(t.c(BRONCE), Offset(sx - w * 0.03f, yT + w * a), Size(w * 0.06f, w * (z - a)))
                    drawRect(t.b(0.45f), Offset(sx - w * 0.03f, yT + w * a), Size(w * 0.06f, 0.9f * u))
                    drawRect(t.s(0.3f), Offset(sx - w * 0.03f, yT + w * z - 1f * u), Size(w * 0.06f, 1f * u))
                }
                drawRect(t.c(BRONCE), Offset(sx - w * 0.06f, yT + ls - w * 0.07f), Size(w * 0.08f, w * 0.08f))
                drawRect(t.s(0.3f), Offset(sx - w * 0.06f, yT + ls - w * 0.07f), Size(w * 0.08f, 1f * u))
                drawRect(t.s(0.22f), Offset(sx + w * 0.002f, yT), Size(w * 0.012f, ls))
            }
            val luzV = Path().apply { moveTo(sx - w * 0.007f, yT + w * 0.006f); cubicTo(sx - w * 0.007f, yT + ls * 0.4f, sx - w * 0.018f, yT + ls * 0.75f, sx - w * 0.04f, yT + ls * 0.98f) }
            drawPath(luzV, t.b(0.4f), style = Stroke(0.8f * u))
            listOf(anilla1, anilla2).forEach { a -> drawCircle(t.c(BRONCE), 1.9f * u, a, style = Stroke(1.1f * u)) }
            // La empuñadura: el puño con su torzal, el pomo, la cruz y el gavilán.
            val puno = Rect(sx - w * 0.008f, yT - w * 0.068f, sx + w * 0.008f, yT - w * 0.008f)
            drawRect(t.c(mezcla(FIELTRO, CAOBA_OSC, 0.4f)), puno.topLeft, puno.size)
            for (j in 0 until 6) drawLine(t.c(BRONCE).copy(alpha = 0.8f), Offset(puno.left, puno.top + puno.height * (j + 0.8f) / 6.5f), Offset(puno.right, puno.top + puno.height * (j + 0.4f) / 6.5f), 0.6f * u)
            drawRect(t.b(0.25f), puno.topLeft, Size(puno.width * 0.3f, puno.height))
            val pomo = poligono(sx - w * 0.011f, puno.top + w * 0.004f, sx - w * 0.008f, puno.top - w * 0.012f, sx + w * 0.012f, puno.top - w * 0.008f, sx + w * 0.01f, puno.top + w * 0.006f)
            drawPath(pomo, t.c(BRONCE))
            drawRect(t.s(0.3f), Offset(sx - w * 0.03f + 1f * u, yT - w * 0.01f + 1.4f * u), Size(w * 0.052f, w * 0.01f))
            drawRoundRect(t.c(BRONCE), Offset(sx - w * 0.03f, yT - w * 0.012f), Size(w * 0.052f, w * 0.012f), CornerRadius(2f * u))
            drawRect(t.b(0.5f), Offset(sx - w * 0.028f, yT - w * 0.012f), Size(w * 0.048f, 0.9f * u))
            val gavilan = Path().apply {
                moveTo(sx - w * 0.028f, yT - w * 0.007f)
                cubicTo(sx - w * 0.05f, yT - w * 0.02f, sx - w * 0.04f, yT - w * 0.07f, sx - w * 0.008f, puno.top - w * 0.006f)
            }
            translate(1.2f * u, 1.6f * u) { drawPath(gavilan, t.s(0.28f), style = Stroke(2.2f * u)) }
            drawPath(gavilan, t.c(BRONCE), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawPath(gavilan, t.b(0.55f), style = Stroke(0.7f * u))
            destello(Offset(sx - w * 0.041f, yT - w * 0.04f), 0.9f * u, t)
        }
        // La dragona: cuelga a plomo del gavilán, con su pasador y su borla.
        val nudo = girar(Offset(sx - w * 0.042f, yT - w * 0.03f), g, giro)
        val oro = mezcla(BRONCE, LUZ_MUEBLE, 0.15f)
        val cordon = Path().apply {
            moveTo(nudo.x, nudo.y)
            cubicTo(nudo.x - w * 0.012f, nudo.y + w * 0.02f, nudo.x - w * 0.01f, nudo.y + w * 0.045f, nudo.x - w * 0.004f, nudo.y + w * 0.06f)
        }
        translate(w * 0.012f, w * 0.008f) { drawPath(cordon, t.s(0.2f), style = Stroke(2.4f * u)) }
        drawPath(cordon, t.c(oro), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawPath(cordon, t.c(BRONCE_OSC).copy(alpha = 0.6f), style = Stroke(0.6f * u))
        val b0 = Offset(nudo.x - w * 0.004f, nudo.y + w * 0.06f)
        drawOval(t.s(0.25f), Offset(b0.x - w * 0.009f + w * 0.012f, b0.y + w * 0.008f), Size(w * 0.018f, w * 0.04f))
        drawOval(t.c(oro), Offset(b0.x - w * 0.008f, b0.y - w * 0.002f), Size(w * 0.016f, w * 0.016f))
        for (j in 0 until 5) drawLine(t.c(if (j % 2 == 0) oro else BRONCE_OSC), Offset(b0.x - w * 0.006f + w * 0.003f * j, b0.y + w * 0.012f), Offset(b0.x - w * 0.008f + w * 0.004f * j, b0.y + w * 0.04f), 1.4f * u, StrokeCap.Round)
        destello(Offset(b0.x - w * 0.003f, b0.y + w * 0.003f), 0.8f * u, t)
    }

    // ---------------------------------------------------------------- al pie, debajo de la tapa abatida

    /**
     * Lo que queda debajo de la tapa abatida: su sombra en la lona (honda arriba, abierta hacia el suelo), las patas
     * torneadas del mueble, las botas de montar y el baúl de campaña, cortado por el borde derecho.
     */
    private fun DrawScope.bajoLaTapa(w: Float, b: Rect) {
        val hb = b.height
        val yTapa = b.top + hb * GRUESO
        val pisan = b.top + hb * PISAN
        val ts = tn(Offset(w * 0.5f, yTapa))
        drawRect(ts.s(0.4f), Offset(0f, b.top), Size(w, yTapa - b.top + hb * 0.06f))
        drawRect(ts.s(0.26f), Offset(0f, yTapa + hb * 0.06f), Size(w, hb * 0.08f))
        drawRect(ts.s(0.13f), Offset(0f, yTapa + hb * 0.14f), Size(w, hb * 0.1f))
        pata(w * 0.108f, yTapa, b.top + hb * (SUELO + 0.09f), w * 0.022f, true)
        pata(w * 0.062f, yTapa, pisan, w * 0.03f, false)
        pata(w * 0.938f, yTapa, pisan, w * 0.03f, false)
        botas(Offset(w * 0.25f, b.top + hb * PIE), hb)
        baul(w, b)
    }

    /** Una pata torneada de quita y pon: el casquillo de latón de la rosca, el dado, el fuste que adelgaza con su anillo y la contera. */
    private fun DrawScope.pata(cx: Float, y0: Float, y1: Float, ancho: Float, atras: Boolean) {
        val u = density
        val t = tn(Offset(cx, (y0 + y1) / 2))
        val a = ancho / 2
        val hp = y1 - y0
        contacto(Offset(cx + a * 0.3f, y1), ancho * 1.5f, 3.5f * u, if (atras) 0.25f else 0.38f)
        val fuste = poligono(cx - a, y0, cx + a, y0, cx + a, y0 + hp * 0.2f, cx + a * 0.82f, y0 + hp * 0.26f, cx + a * 0.5f, y1, cx - a * 0.5f, y1, cx - a * 0.82f, y0 + hp * 0.26f, cx - a, y0 + hp * 0.2f)
        drawPath(fuste, t.c(if (atras) mezcla(CAOBA, CAOBA_OSC, 0.5f) else CAOBA))
        clipPath(fuste) { cilindro(Rect(cx - a, y0, cx + a, y1), t, 1.3f) }
        // El anillo del torneado y el casquillo y la contera de latón.
        listOf(0.26f to 0.035f, 0.33f to 0.02f).forEach { (f, g) ->
            drawRoundRect(t.c(mezcla(CAOBA, CAOBA_LUZ, 0.5f)), Offset(cx - a * 0.95f, y0 + hp * f), Size(a * 1.9f, hp * g), CornerRadius(1.5f * u))
            drawRect(t.s(0.3f), Offset(cx - a * 0.9f, y0 + hp * (f + g)), Size(a * 1.8f, 1f * u))
        }
        if (!atras) {
            val casq = Rect(cx - a * 1.05f, y0 + hp * 0.02f, cx + a * 1.05f, y0 + hp * 0.07f)
            drawRect(t.c(BRONCE), casq.topLeft, casq.size)
            clipRect(casq.left, casq.top, casq.right, casq.bottom) { cilindro(casq, t, 1.4f) }
            val cont = Rect(cx - a * 0.56f, y1 - hp * 0.08f, cx + a * 0.56f, y1)
            drawRect(t.c(BRONCE), cont.topLeft, cont.size)
            clipRect(cont.left, cont.top, cont.right, cont.bottom) { cilindro(cont, t, 1.4f) }
            drawRect(t.s(0.3f), Offset(cont.left, cont.top - 1f * u), Size(cont.width, 1f * u))
        }
    }

    /** Una bota de montar de cuero negro con su vuelta leonada, vista de lado, con el pie hacia la derecha. */
    private fun DrawScope.bota(pie: Offset, bh: Float, t: Tono, caida: Boolean) {
        val u = density
        val w = anchoW
        val x = pie.x; val y = pie.y
        val negro = mezcla(FIELTRO, CAOBA_OSC, 0.25f)
        val forma = Path().apply {
            moveTo(x - w * 0.031f, y - bh)
            lineTo(x + w * 0.032f, y - bh + w * 0.006f)
            cubicTo(x + w * 0.03f, y - bh * 0.6f, x + w * 0.024f, y - bh * 0.45f, x + w * 0.027f, y - w * 0.056f)
            cubicTo(x + w * 0.048f, y - w * 0.056f, x + w * 0.076f, y - w * 0.05f, x + w * 0.09f, y - w * 0.03f)
            cubicTo(x + w * 0.1f, y - w * 0.02f, x + w * 0.101f, y - w * 0.006f, x + w * 0.094f, y)
            lineTo(x - w * 0.036f, y)
            lineTo(x - w * 0.037f, y - w * 0.024f)
            cubicTo(x - w * 0.03f, y - bh * 0.4f, x - w * 0.036f, y - bh * 0.7f, x - w * 0.031f, y - bh)
            close()
        }
        drawPath(forma, t.c(negro))
        clipPath(forma) {
            // El brillo del cuero lustrado a lo largo de la caña y en el empeine.
            drawRect(t.b(0.14f), Offset(x - w * 0.036f, y - bh), Size(w * 0.018f, bh))
            drawRoundRect(t.b(0.26f), Offset(x - w * 0.022f, y - bh * 0.76f), Size(w * 0.0045f, bh * 0.4f), CornerRadius(w * 0.002f))
            drawRect(t.s(0.3f), Offset(x + w * 0.012f, y - bh), Size(w * 0.03f, bh * 0.75f))
            drawOval(t.b(0.22f), Offset(x + w * 0.04f, y - w * 0.05f), Size(w * 0.04f, w * 0.009f))
            drawOval(t.b(0.18f), Offset(x + w * 0.072f, y - w * 0.034f), Size(w * 0.018f, w * 0.012f))
            // Las arrugas del tobillo.
            for (j in 0 until 2) {
                val yy = y - w * (0.064f + 0.016f * j)
                val p = Path().apply { moveTo(x - w * 0.03f, yy); cubicTo(x - w * 0.01f, yy + w * 0.007f, x + w * 0.012f, yy + w * 0.006f, x + w * 0.027f, yy - w * 0.002f) }
                drawPath(p, t.s(0.45f), style = Stroke(1.2f * u))
                translate(0f, 1.3f * u) { drawPath(p, t.b(0.14f), style = Stroke(0.7f * u)) }
            }
            // La vuelta de cuero leonado.
            drawRect(t.c(CORREA), Offset(x - w * 0.04f, y - bh), Size(w * 0.08f, bh * 0.2f))
            drawRect(t.b(0.25f), Offset(x - w * 0.04f, y - bh), Size(w * 0.03f, bh * 0.2f))
            drawRect(t.s(0.3f), Offset(x - w * 0.04f, y - bh * 0.8f), Size(w * 0.08f, 1.2f * u))
            drawRect(t.s(0.22f), Offset(x + w * 0.012f, y - bh), Size(w * 0.03f, bh * 0.2f))
            // La suela, el tacón y el barro.
            drawRect(t.c(mezcla(CORREA, CAOBA_OSC, 0.6f)), Offset(x - w * 0.04f, y - w * 0.007f), Size(w * 0.14f, w * 0.007f))
            drawRect(t.c(mezcla(CORREA, CAOBA_OSC, 0.6f)), Offset(x - w * 0.04f, y - w * 0.018f), Size(w * 0.028f, w * 0.018f))
            // El barro seco de la suela para arriba.
            drawRect(t.c(TIERRA).copy(alpha = 0.45f), Offset(x - w * 0.04f, y - w * 0.018f), Size(w * 0.14f, w * 0.011f))
            pinceladas(Rect(x - w * 0.04f, y - w * 0.035f, x + w * 0.1f, y - w * 0.006f), if (esNoche) NOCHE else TIERRA, if (caida) 4201 else 4202, 5, u, 0.22f)
        }
        // El tirante de la caña.
        drawLine(t.c(CORREA), Offset(x - w * 0.02f, y - bh + 1f * u), Offset(x - w * 0.024f, y - bh - w * 0.012f), 1.6f * u, StrokeCap.Round)
        drawLine(t.c(CORREA), Offset(x - w * 0.012f, y - bh + 1f * u), Offset(x - w * 0.024f, y - bh - w * 0.012f), 1.6f * u, StrokeCap.Round)
    }

    /** Las dos botas: una de pie y la otra vencida contra ella. */
    private fun DrawScope.botas(pie: Offset, hb: Float) {
        val u = density
        val w = anchoW
        val bh = hb * 0.5f
        val t = tn(Offset(pie.x, pie.y - bh / 2))
        contacto(Offset(pie.x - w * 0.01f, pie.y), w * 0.24f, 5f * u, 0.4f)
        val atras = Offset(pie.x - w * 0.085f, pie.y - hb * 0.02f)
        rotate(-15f, Offset(atras.x - w * 0.036f, atras.y)) { bota(atras, bh * 0.96f, t, true) }
        bota(pie, bh, t, false)
    }

    /**
     * El baúl de campaña, cortado por el borde derecho: forrado de cuero y claveteado, con las cantoneras de hierro, la
     * correa con su hebilla, la aldaba del cierre, las iniciales casi borradas y la chapa de latón del dueño (la de «y N más»).
     */
    private fun DrawScope.baul(w: Float, b: Rect) {
        val u = density
        val hb = b.height
        val r = Rect(w * 0.5f, b.top + hb * 0.53f, w * 1.03f, b.top + hb * PIE)
        val t = tn(r.center)
        val piel = mezcla(CORREA, CAOBA_OSC, 0.45f)
        val hierro = mezcla(FIELTRO, HOJALATA, 0.35f)
        contacto(Offset(r.left + r.width * 0.45f, r.bottom), r.width * 1.05f, 6f * u, 0.42f)
        // La tapa, vista un poco desde arriba.
        val fondo = hb * 0.07f
        val tapa = poligono(r.left + w * 0.018f, r.top - fondo, r.right, r.top - fondo, r.right, r.top, r.left, r.top)
        drawPath(tapa, t.c(mezcla(piel, CORREA, 0.35f)))
        drawPath(tapa, t.s(0.22f))
        drawLine(t.b(0.3f), Offset(r.left + w * 0.018f, r.top - fondo), Offset(r.right, r.top - fondo), 0.9f * u)
        // El frente.
        drawRect(t.c(piel), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, if (esNoche) NOCHE else CAOBA_OSC, 4210, 14, u, 0.16f)
            pinceladas(r, if (esNoche) NOCHE else CORREA, 4211, 9, u, 0.16f)
            drawRect(t.b(0.12f), r.topLeft, Size(r.width * 0.2f, r.height))
            drawRect(t.s(0.2f), r.topLeft, Size(r.width, r.height * 0.16f))
            // El borde de la tapa y sus dos hileras de clavos.
            val yt = r.top + r.height * 0.25f
            drawRect(t.s(0.4f), Offset(r.left, yt), Size(r.width, 1.4f * u))
            drawRect(t.b(0.25f), Offset(r.left, yt + 1.4f * u), Size(r.width, 0.8f * u))
            var x = r.left + w * 0.012f
            var k = 0
            while (x < r.right) {
                listOf(yt - w * 0.012f, yt + w * 0.014f, r.bottom - w * 0.012f).forEach { yy ->
                    if (azar(4212, k++) > 0.1f) clavo(Offset(x, yy), 1.2f * u, t)
                }
                x += w * 0.021f
            }
            // Las cantoneras de hierro de la izquierda.
            listOf(r.top to 1f, r.bottom to -1f).forEach { (yy, s) ->
                drawPath(poligono(r.left, yy, r.left + w * 0.05f, yy, r.left, yy + s * w * 0.05f), t.c(hierro))
                drawLine(t.b(0.3f), Offset(r.left + w * 0.05f, yy), Offset(r.left, yy + s * w * 0.05f), 0.8f * u)
            }
            // Las iniciales pintadas, casi borradas: quedan unos trazos.
            val ini = t.c(mezcla(piel, LONA, 0.55f)).copy(alpha = 0.5f)
            val ix = r.left + w * 0.135f; val iy = yt + w * 0.03f
            drawLine(ini, Offset(ix, iy), Offset(ix, iy + w * 0.03f), 2f * u)
            drawArc(ini, 20f, 150f, false, Offset(ix - w * 0.018f, iy + w * 0.018f), Size(w * 0.018f, w * 0.018f), style = Stroke(2f * u))
            drawLine(ini, Offset(ix + w * 0.024f, iy), Offset(ix + w * 0.024f, iy + w * 0.018f), 2f * u)
            drawArc(ini, -90f, 130f, false, Offset(ix + w * 0.016f, iy), Size(w * 0.022f, w * 0.016f), style = Stroke(2f * u))
            drawArc(ini, -60f, 120f, false, Offset(ix + w * 0.018f, iy + w * 0.018f), Size(w * 0.024f, w * 0.016f), style = Stroke(2f * u))
        }
        // La correa de cuero con su hebilla.
        val cx = r.left + w * 0.062f
        drawRect(t.s(0.3f), Offset(cx + w * 0.012f, r.top - fondo), Size(1.6f * u, r.height + fondo))
        drawRect(t.c(mezcla(CORREA, LONA, 0.12f)), Offset(cx - w * 0.012f, r.top - fondo), Size(w * 0.024f, r.height + fondo))
        drawRect(t.b(0.3f), Offset(cx - w * 0.012f, r.top - fondo), Size(1f * u, r.height + fondo))
        val heb = Rect(cx - w * 0.017f, r.top + r.height * 0.34f, cx + w * 0.017f, r.top + r.height * 0.34f + w * 0.026f)
        drawRect(t.s(0.3f), heb.topLeft + Offset(1f * u, 1.3f * u), heb.size, style = Stroke(1.6f * u))
        drawRect(t.c(BRONCE), heb.topLeft, heb.size, style = Stroke(1.6f * u))
        drawLine(t.c(BRONCE), Offset(cx, heb.top), Offset(cx, heb.bottom), 1.1f * u)
        destello(heb.topLeft, 0.9f * u, t)
        // La aldaba del cierre, sobre el borde de la tapa.
        val ax = w * (placa.left + placa.right) / 2
        val ald = Rect(ax - w * 0.013f, r.top + r.height * 0.1f, ax + w * 0.013f, r.top + r.height * 0.42f)
        chapa(ald, t, hierro)
        drawCircle(t.s(0.5f), 1.3f * u, Offset(ax, ald.bottom - w * 0.01f))
        // La chapa de latón del dueño.
        val pl = Rect(b.left + b.width * placa.left, b.top + hb * placa.top, b.left + b.width * placa.right, b.top + hb * placa.bottom)
        chapa(pl, t)
        listOf(pl.left + 2.2f * u, pl.right - 2.2f * u).forEach { xx -> drawCircle(t.c(BRONCE_OSC), 0.9f * u, Offset(xx, pl.center.y)) }
        val dentro = Rect(pl.left + 3f * u, pl.top + 2.5f * u, pl.right - 3f * u, pl.bottom - 2.5f * u)
        drawRect(t.c(BRONCE_OSC).copy(alpha = 0.5f), dentro.topLeft, dentro.size, style = Stroke(0.6f * u))
        // Lo grabado en la chapa: dos rasgos a buril, sin letra, y las rozaduras del viaje.
        listOf(0.42f to 0.5f, 0.66f to 0.3f).forEach { (fy, fw) ->
            val y = pl.top + pl.height * fy
            val g = Path().apply { moveTo(pl.center.x - pl.width * fw / 2, y); cubicTo(pl.center.x - pl.width * fw / 6, y - 2f * u, pl.center.x + pl.width * fw / 6, y + 2f * u, pl.center.x + pl.width * fw / 2, y) }
            drawPath(g, t.c(BRONCE_OSC).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        }
        for (k in 0 until 3) drawLine(t.b(0.3f), Offset(pl.left + pl.width * (0.15f + 0.3f * k), pl.top + pl.height * 0.2f), Offset(pl.left + pl.width * (0.22f + 0.3f * k), pl.top + pl.height * (0.34f + 0.1f * k)), 0.5f * u)
    }

    // ---------------------------------------------------------------- el mueble

    /**
     * El cuerpo del secrétaire: su sombra en la lona, el frente de caoba con la veta (de noche más encendido arriba, cerca de
     * la vela), el rebaje y el filete de latón de cada casilla, el filete embutido entre fila y fila, las cantoneras con sus
     * tornillos, el friso con la corona de laurel y la abeja doradas, y los golpes de los viajes.
     */
    private fun DrawScope.cuerpo(d: Disposicion, ch: Float, canto: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val xL = w * IZQ; val xR = w * DER
        val abajo = b.top + b.height * BISAGRA
        val cuerpo = Rect(xL, canto, xR, abajo)

        // Su sombra en la lona, a la derecha.
        val t0 = tn(Offset(xR, ch * 1.4f))
        drawPath(poligono(xR, ch * SUP, xR + w * 0.026f, ch * SUP + w * 0.03f, xR + w * 0.026f, abajo, xR, abajo), t0.s(0.3f))

        // El frente de caoba.
        if (!o) drawRect(CAOBA, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(7) { i -> tn(Offset(w * LUZ.x, cuerpo.top + cuerpo.height * i / 6f)).c(CAOBA) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent, sombraMueble(0.24f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.35f), Color.Transparent, Color.Transparent, NOCHE.copy(alpha = 0.45f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(luzMueble(0.07f), cuerpo.topLeft, Size(cuerpo.width, minOf(cuerpo.height, w * 0.5f)))
        vetas(cuerpo, (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.36f), 4101, 0.55f, vertical = true, grosor = 0.6f, ondulacion = 0.6f)
        pinceladas(cuerpo, if (o) NOCHE else CAOBA_LUZ, 4102, 22, u, 0.12f)

        // Los cantos de los costados: el de la izquierda recibe la luz; el de la derecha, en sombra.
        for (k in 0 until 10) {
            val y0 = canto + cuerpo.height * k / 10f
            val tl = tn(Offset(xL, y0 + cuerpo.height / 20f))
            drawRect(tl.b(0.45f), Offset(xL, y0), Size(1.4f * u, cuerpo.height / 10f + 0.5f))
            val tr = tn(Offset(xR, y0 + cuerpo.height / 20f))
            drawRect(tr.c(CAOBA_OSC), Offset(xR - w * 0.008f, y0), Size(w * 0.008f, cuerpo.height / 10f + 0.5f))
        }

        // El rebaje y el filete de latón de cada casilla.
        val m = w * MARCO
        d.huecos.forEach { r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.3f), Offset(marco.left + 2f * u, marco.top + 2.4f * u), marco.size)
            drawRect(t.c(mezcla(CAOBA_OSC, CAOBA, 0.25f)), marco.topLeft, marco.size)
            drawRect(t.s(0.3f), marco.topLeft, Size(marco.width, 1.4f * u))
            drawRect(t.b(0.3f), Offset(marco.left, marco.bottom - 1.2f * u), Size(marco.width, 1.2f * u))
            val f = Rect(r.left - m * 0.5f, r.top - m * 0.5f, r.right + m * 0.5f, r.bottom + m * 0.5f)
            drawRect(t.c(BRONCE), f.topLeft, f.size, style = Stroke(1.5f * u))
            drawRect(t.b(0.55f), Offset(f.left, f.top - 0.75f * u), Size(f.width, 0.7f * u))
            drawRect(t.b(0.4f), Offset(f.left - 0.75f * u, f.top), Size(0.7f * u, f.height))
        }
        // El filete de latón embutido entre fila y fila.
        d.filas.forEachIndexed { i, f ->
            if (i < d.filas.size - 1) {
                val y = f.bottom + d.separacion / 2
                val t = tn(Offset(w * 0.5f, y))
                drawRect(t.c(BRONCE).copy(alpha = 0.85f), Offset(xL + w * 0.03f, y - 0.6f * u), Size(xR - xL - w * 0.06f, 1.2f * u))
                drawRect(t.s(0.3f), Offset(xL + w * 0.03f, y + 0.6f * u), Size(xR - xL - w * 0.06f, 0.8f * u))
            }
        }

        // El friso: la corona de laurel con la abeja, doradas, entre dos filetes embutidos.
        val primero = d.huecos.first()
        val fy = (canto + primero.top - m) / 2 + 1f * u
        val fr = (primero.top - m - canto) * 0.36f
        val tf = tn(Offset(w * 0.5f, fy))
        translate(1f * u, 1.3f * u) { laurel(Offset(w * 0.5f, fy), fr, tf.s(0.4f)); abeja(Offset(w * 0.5f, fy + fr * 0.1f), fr * 0.9f, tf.s(0.4f)) }
        laurel(Offset(w * 0.5f, fy), fr, tf.c(BRONCE))
        abeja(Offset(w * 0.5f, fy + fr * 0.1f), fr * 0.9f, tf.c(mezcla(BRONCE, LUZ_MUEBLE, 0.2f)))
        destello(Offset(w * 0.5f - fr * 0.1f, fy - fr * 0.3f), 0.8f * u, tf)
        listOf(-1f, 1f).forEach { s ->
            val x0 = w * 0.5f + s * fr * 1.7f; val x1 = w * 0.5f + s * w * 0.36f
            val tt = tn(Offset((x0 + x1) / 2, fy))
            drawLine(tt.c(BRONCE).copy(alpha = 0.85f), Offset(x0, fy), Offset(x1, fy), 1.2f * u)
            drawLine(tt.s(0.3f), Offset(x0, fy + 1f * u), Offset(x1, fy + 1f * u), 0.8f * u)
            drawPath(estrella(Offset(x1 + s * w * 0.012f, fy), w * 0.009f, 5, 0.45f), tt.c(BRONCE))
        }

        // Las cantoneras de latón de las cuatro esquinas, con sus tornillos, y las pletinas de en medio.
        listOf(canto to 1f, abajo to -1f).forEach { (yy, sy) ->
            listOf(xL to 1f, xR to -1f).forEach { (xx, sx) ->
                val t = tn(Offset(xx, yy))
                val largo = w * 0.07f; val g = w * 0.014f
                val hor = Rect(minOf(xx, xx + sx * largo), minOf(yy, yy + sy * g), maxOf(xx, xx + sx * largo), maxOf(yy, yy + sy * g))
                val ver = Rect(minOf(xx, xx + sx * g), minOf(yy, yy + sy * largo), maxOf(xx, xx + sx * g), maxOf(yy, yy + sy * largo))
                chapa(hor, t); chapa(ver, t)
                clavo(Offset(xx + sx * largo * 0.8f, yy + sy * g / 2), 1f * u, t, BRONCE_OSC)
                clavo(Offset(xx + sx * g / 2, yy + sy * largo * 0.8f), 1f * u, t, BRONCE_OSC)
                clavo(Offset(xx + sx * g / 2, yy + sy * g / 2), 1f * u, t, BRONCE_OSC)
                if (sx > 0 && sy > 0) destello(Offset(xx + g * 0.4f, yy + g * 0.3f), 0.9f * u, t)
            }
        }
        if (d.filas.size >= 3) {
            val f = d.filas[d.filas.size / 2 - 1]
            val y = f.bottom + d.separacion / 2
            listOf(xL, xR - w * 0.02f).forEach { xx ->
                val pl = Rect(xx, y - w * 0.032f, xx + w * 0.02f, y + w * 0.032f)
                val t = tn(pl.center)
                chapa(pl, t)
                clavo(Offset(pl.center.x, pl.top + w * 0.01f), 1f * u, t, BRONCE_OSC)
                clavo(Offset(pl.center.x, pl.bottom - w * 0.01f), 1f * u, t, BRONCE_OSC)
            }
        }

        // Los golpes de los viajes: rozaduras claras en los montantes y una esquina saltada.
        for (k in 0 until 7) {
            val x = when (k % 3) { 0 -> xL + w * 0.012f; 1 -> w * 0.492f; else -> xR - w * 0.03f } + w * 0.012f * azar(4120, k)
            val y = canto + cuerpo.height * (0.08f + 0.86f * azar(4121, k))
            val t = tn(Offset(x, y))
            drawLine(t.c(mezcla(CAOBA_LUZ, LONA, 0.35f)).copy(alpha = 0.7f), Offset(x, y), Offset(x + w * 0.008f, y + w * (0.012f + 0.02f * azar(4122, k))), 0.8f * u, StrokeCap.Round)
            drawLine(t.s(0.3f), Offset(x + 1f * u, y), Offset(x + w * 0.008f + 1f * u, y + w * (0.012f + 0.02f * azar(4122, k))), 0.5f * u)
        }
    }

    /**
     * La tapa de arriba del mueble: la tabla vista un poco desde arriba (con su veta, un cerco de vaso, unas gotas de cera y
     * rozaduras), su canto de frente con el filete, las cantoneras de latón de las puntas y la sombra que deja en el friso.
     */
    private fun DrawScope.tapaDeArriba(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val o = esNoche
        val xL = w * IZQ - w * 0.012f; val xR = w * DER + w * 0.012f
        val t = tn(Offset(w * LUZ.x, sup))
        val tabla = Rect(xL, sup, xR, frente)
        val madera = mezcla(CAOBA_LUZ, CAOBA, 0.3f)
        if (!o) drawRect(madera, tabla.topLeft, tabla.size)
        else drawRect(Brush.horizontalGradient(List(7) { i -> tn(Offset(xL + (xR - xL) * i / 6f, (sup + frente) / 2)).c(madera) }, xL, xR), tabla.topLeft, tabla.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.34f), Color.Transparent, sombraMueble(0.14f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.34f), 4140, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.s(0.32f), tabla.topLeft, Size(tabla.width, 1.4f * u))
        val hs = frente - sup
        // El cerco de un vaso, unas gotas de cera junto al candelero y rozaduras.
        drawOval(tn(Offset(w * 0.69f, sup)).s(0.22f), Offset(w * 0.675f, sup + hs * 0.3f), Size(w * 0.05f, hs * 0.5f), style = Stroke(0.9f * u))
        listOf(Offset(0.515f, 0.5f), Offset(0.53f, 0.72f), Offset(0.432f, 0.8f)).forEach { f ->
            val p = Offset(w * f.x, sup + hs * f.y)
            val tc = tn(p)
            drawOval(tc.s(0.25f), Offset(p.x - 1.2f * u, p.y - 0.4f * u), Size(4.4f * u, 2.2f * u))
            drawOval(tc.c(mezcla(PAPEL, BRONCE, 0.12f)), Offset(p.x - 1.8f * u, p.y - 1f * u), Size(3.8f * u, 2f * u))
        }
        for (k in 0 until 5) {
            val x = w * (0.06f + 0.85f * azar(4141, k))
            drawLine(tn(Offset(x, sup)).b(0.28f), Offset(x, sup + hs * (0.3f + 0.4f * azar(4142, k))), Offset(x + w * 0.03f, sup + hs * (0.4f + 0.3f * azar(4143, k))), 0.6f * u)
        }
        // El canto: dos planos (el de arriba recibe la luz) y el filete de latón embutido.
        val m1 = frente + (canto - frente) * 0.5f
        for (k in 0 until 8) {
            val x0 = xL + (xR - xL) * k / 8f
            val an = (xR - xL) / 8f + 0.5f
            val tk = tn(Offset(x0 + an / 2, frente))
            drawRect(tk.c(CAOBA_LUZ), Offset(x0, frente), Size(an, m1 - frente))
            drawRect(tk.c(mezcla(CAOBA, CAOBA_OSC, 0.3f)), Offset(x0, m1), Size(an, canto - m1))
            drawRect(tk.b(0.7f), Offset(x0, frente), Size(an, 1.2f * u))
            drawRect(tk.c(BRONCE).copy(alpha = 0.85f), Offset(x0, m1 - 0.6f * u), Size(an, 1.1f * u))
        }
        drawRect(t.s(0.36f), Offset(xL, canto - 1f * u), Size(xR - xL, 1f * u))
        // Las cantoneras de latón que abrazan las dos puntas del canto.
        listOf(xL to tn(Offset(xL, frente)), xR - w * 0.05f to tn(Offset(xR, frente))).forEach { (x0, tc) ->
            chapa(Rect(x0, frente - hs * 0.22f, x0 + w * 0.05f, canto), tc)
            clavo(Offset(x0 + w * 0.012f, (frente + canto) / 2), 1f * u, tc, BRONCE_OSC)
            clavo(Offset(x0 + w * 0.038f, (frente + canto) / 2), 1f * u, tc, BRONCE_OSC)
        }
        // La sombra de la tapa en el friso.
        drawRect(t.s(0.34f), Offset(w * IZQ, canto), Size(w * (DER - IZQ), 2.4f * u))
        drawRect(t.s(0.15f), Offset(w * IZQ, canto + 2.4f * u), Size(w * (DER - IZQ), 3f * u))
    }

    /**
     * La tapa abatida: la tabla que sale hacia quien mira, con su cuero verde gastado, el doble filete dorado y los florones
     * de las esquinas; las bisagras de latón, el canto de delante con sus cantoneras y el escudo de la cerradura con la
     * llave puesta. Encima, lo de escribir: el mapa plegado con la brújula y el compás de puntas, la carta a medio escribir
     * con la pluma, el tintero, la salvadera, la barra de lacre y el sello.
     */
    private fun DrawScope.tapaAbatida(w: Float, b: Rect) {
        val u = density
        val o = esNoche
        val hb = b.height
        val yB = b.top + hb * BISAGRA; val yF = b.top + hb * BORDE; val yG = b.top + hb * GRUESO
        val xb0 = w * IZQ; val xb1 = w * DER; val xf0 = -w * 0.006f; val xf1 = w * 1.006f
        /** Un punto de la tabla: [fx] de izquierda a derecha y [fy] del fondo (0) al borde de delante (1). */
        fun p(fx: Float, fy: Float): Offset {
            val a = xb0 + (xf0 - xb0) * fy; val z = xb1 + (xf1 - xb1) * fy
            return Offset(a + (z - a) * fx, yB + (yF - yB) * fy)
        }
        fun cuadro(x0: Float, y0: Float, x1: Float, y1: Float) = poligono(p(x0, y0).x, p(x0, y0).y, p(x1, y0).x, p(x1, y0).y, p(x1, y1).x, p(x1, y1).y, p(x0, y1).x, p(x0, y1).y)
        val t = tn(Offset(w * 0.5f, (yB + yF) / 2))
        val tabla = cuadro(0f, 0f, 1f, 1f)
        val madera = mezcla(CAOBA_LUZ, CAOBA, 0.35f)
        drawPath(tabla, t.c(madera))
        clipPath(tabla) {
            if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.3f), Color.Transparent, sombraMueble(0.16f)), 0f, w), Offset(0f, yB), Size(w, yF - yB))
            vetas(Rect(0f, yB, w, yF), (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.34f), 4160, 0.7f, grosor = 0.5f, ondulacion = 0.4f)
        }
        // El cuero verde, embutido: gastado donde se apoya la mano, con su doble filete y sus florones.
        val cuero = cuadro(0.04f, 0.14f, 0.96f, 0.88f)
        drawPath(cuero, t.c(CUERO))
        clipPath(cuero) {
            pinceladas(Rect(0f, yB, w, yF), if (o) NOCHE else CUERO_OSC, 4161, 22, u, 0.2f)
            pinceladas(Rect(0f, yB, w, yF), if (o) NOCHE else mezcla(CUERO, LONA, 0.4f), 4162, 12, u, 0.12f)
            if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent, sombraMueble(0.2f)), 0f, w), Offset(0f, yB), Size(w, yF - yB))
            drawOval(t.c(mezcla(CUERO, LONA, 0.45f)).copy(alpha = 0.3f), Offset(w * 0.36f, yB + (yF - yB) * 0.5f), Size(w * 0.3f, (yF - yB) * 0.45f))
            drawRect(t.s(0.3f), Offset(0f, p(0f, 0.14f).y), Size(w, 2f * u))
            drawRect(t.s(0.22f), Offset(0f, yB), Size(w, (yF - yB) * 0.24f))
        }
        val oro = t.c(BRONCE).copy(alpha = 0.9f)
        drawPath(cuadro(0.058f, 0.24f, 0.942f, 0.78f), oro, style = Stroke(1f * u))
        drawPath(cuadro(0.068f, 0.3f, 0.932f, 0.72f), oro.copy(alpha = 0.6f), style = Stroke(0.5f * u))
        listOf(p(0.058f, 0.24f), p(0.942f, 0.24f), p(0.058f, 0.78f), p(0.942f, 0.78f)).forEach { q -> drawPath(estrella(q, 2.6f * u, 4, 0.35f), oro) }

        // ---- lo de escribir, sobre el cuero
        escribania(w, t) { fx, fy -> p(fx, fy) }

        // La sombra del cuerpo del mueble sobre el fondo de la tapa, y las bisagras.
        drawRect(t.s(0.3f), Offset(xb0, yB), Size(xb1 - xb0, 2.4f * u))
        listOf(0.16f, 0.5f, 0.84f).forEach { fx ->
            val c = p(fx, 0f)
            val tb = tn(c)
            chapa(Rect(c.x - w * 0.03f, c.y - w * 0.012f, c.x + w * 0.03f, c.y + w * 0.012f), tb)
            drawRect(tb.c(BRONCE_OSC), Offset(c.x - w * 0.03f, c.y - 1f * u), Size(w * 0.06f, 2f * u))
            drawRect(tb.b(0.5f), Offset(c.x - w * 0.03f, c.y - 1f * u), Size(w * 0.06f, 0.7f * u))
            listOf(-0.02f, 0.02f).forEach { dx -> clavo(Offset(c.x + w * dx, c.y - w * 0.006f), 0.8f * u, tb, BRONCE_OSC); clavo(Offset(c.x + w * dx, c.y + w * 0.007f), 0.8f * u, tb, BRONCE_OSC) }
        }

        // El canto de delante: dos planos, el filete, las cantoneras y el escudo con su llave.
        val m1 = yF + (yG - yF) * 0.5f
        for (k in 0 until 8) {
            val x0 = w * k / 8f; val an = w / 8f + 0.5f
            val tk = tn(Offset(x0 + an / 2, yF))
            drawRect(tk.c(CAOBA_LUZ), Offset(x0, yF), Size(an, m1 - yF))
            drawRect(tk.c(mezcla(CAOBA, CAOBA_OSC, 0.3f)), Offset(x0, m1), Size(an, yG - m1))
            drawRect(tk.b(0.7f), Offset(x0, yF), Size(an, 1.2f * u))
        }
        vetas(Rect(0f, yF, w, yG), (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.3f), 4163, 0.7f, grosor = 0.5f, ondulacion = 0.3f)
        drawRect(t.s(0.4f), Offset(0f, yG - 1.2f * u), Size(w, 1.2f * u))
        listOf(0f, w * 0.945f).forEach { x0 ->
            val tc = tn(Offset(x0, yF))
            chapa(Rect(x0, yF - (yF - yB) * 0.12f, x0 + w * 0.055f, yG), tc)
            clavo(Offset(x0 + w * 0.014f, (yF + yG) / 2), 1f * u, tc, BRONCE_OSC)
            clavo(Offset(x0 + w * 0.04f, (yF + yG) / 2), 1f * u, tc, BRONCE_OSC)
        }
        val esc = Offset(w * 0.5f, (yF + yG) / 2)
        val te = tn(esc)
        drawRoundRect(te.s(0.3f), Offset(esc.x - w * 0.014f + 1f * u, yF + 2.5f * u), Size(w * 0.028f, yG - yF - 3f * u), CornerRadius(w * 0.006f))
        drawRoundRect(te.c(BRONCE), Offset(esc.x - w * 0.014f, yF + 1.5f * u), Size(w * 0.028f, yG - yF - 3f * u), CornerRadius(w * 0.006f))
        // La llave puesta, con su anilla y una cinta roja.
        val llave = Offset(esc.x, esc.y + 0.5f * u)
        drawCircle(te.s(0.5f), 1.5f * u, llave)
        drawLine(te.s(0.3f), llave + Offset(1.2f * u, 1.5f * u), llave + Offset(1.2f * u, w * 0.02f + 1.5f * u), 1.6f * u)
        drawLine(te.c(mezcla(HOJALATA, FIELTRO, 0.3f)), llave, llave + Offset(0f, w * 0.02f), 1.8f * u)
        drawCircle(te.c(mezcla(HOJALATA, FIELTRO, 0.3f)), w * 0.01f, llave + Offset(0f, w * 0.03f), style = Stroke(1.6f * u))
        val cinta = Path().apply { moveTo(llave.x, llave.y + w * 0.04f); cubicTo(llave.x + w * 0.012f, llave.y + w * 0.06f, llave.x - w * 0.008f, llave.y + w * 0.075f, llave.x + w * 0.006f, llave.y + w * 0.095f) }
        drawPath(cinta, te.c(ROJO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    }

    /** Lo de escribir, sobre el cuero de la tapa abatida. [p] da un punto de la tabla (de izquierda a derecha, del fondo al borde). */
    private fun DrawScope.escribania(w: Float, t: Tono, p: (Float, Float) -> Offset) {
        val u = density
        fun cuadro(x0: Float, y0: Float, x1: Float, y1: Float, sesgo: Float = 0f) =
            poligono(p(x0 + sesgo, y0).x, p(x0 + sesgo, y0).y, p(x1 + sesgo, y0).x, p(x1 + sesgo, y0).y, p(x1, y1).x, p(x1, y1).y, p(x0, y1).x, p(x0, y1).y)
        val tinta = t.c(mezcla(TINTA, CORREA, 0.4f))

        // El mapa plegado, con la brújula y el compás de puntas encima.
        val plegado = cuadro(0.085f, 0.3f, 0.31f, 0.86f, 0.012f)
        translate(2f * u, 2.4f * u) { drawPath(plegado, t.s(0.35f)) }
        drawPath(plegado, t.c(mezcla(PAPEL, LONA, 0.4f)))
        clipPath(plegado) {
            val a = p(0.09f, 0.7f); val z = p(0.31f, 0.45f)
            val rio = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + w * 0.06f, a.y - w * 0.03f, z.x - w * 0.08f, z.y + w * 0.03f, z.x, z.y) }
            drawPath(rio, t.c(mezcla(AZUL, PAPEL, 0.5f)), style = Stroke(1.8f * u))
            for (j in 0 until 9) { val c = p(0.14f + 0.018f * j, 0.4f + 0.02f * (j % 3)); drawLine(tinta.copy(alpha = 0.6f), c, c + Offset(1.2f * u, -3f * u), 0.5f * u) }
            val d0 = p(0.2f, 0.3f); val d1 = p(0.195f, 0.86f)
            drawLine(t.s(0.2f), d0, d1, 0.9f * u)
            drawLine(t.b(0.4f), d0 + Offset(1f * u, 0f), d1 + Offset(1f * u, 0f), 0.7f * u)
        }
        drawPath(plegado, t.b(0.3f), style = Stroke(0.6f * u))
        // La brújula: la caja de latón, baja, con su vidrio, la rosa y la aguja.
        val bc = p(0.135f, 0.6f)
        val brx = w * 0.033f; val bry = w * 0.017f; val bg = w * 0.009f
        drawOval(t.s(0.4f), Offset(bc.x - brx + 2.5f * u, bc.y - bry + bg + 1.5f * u), Size(brx * 2, bry * 2))
        drawOval(t.c(BRONCE_OSC), Offset(bc.x - brx, bc.y - bry + bg), Size(brx * 2, bry * 2))
        drawRect(t.c(BRONCE_OSC), Offset(bc.x - brx, bc.y), Size(brx * 2, bg))
        clipRect(bc.x - brx, bc.y, bc.x + brx, bc.y + bg + bry) { cilindro(Rect(bc.x - brx, bc.y, bc.x + brx, bc.y + bg + bry), t, 1.5f) }
        drawOval(t.c(BRONCE), Offset(bc.x - brx, bc.y - bry), Size(brx * 2, bry * 2))
        drawOval(t.c(mezcla(PAPEL, HOJALATA, 0.15f)), Offset(bc.x - brx * 0.82f, bc.y - bry * 0.82f), Size(brx * 1.64f, bry * 1.64f))
        for (j in 0 until 8) { val a = j * 0.7854f; drawLine(tinta.copy(alpha = 0.6f), Offset(bc.x + brx * 0.6f * cos(a), bc.y + bry * 0.6f * sin(a)), Offset(bc.x + brx * 0.76f * cos(a), bc.y + bry * 0.76f * sin(a)), 0.5f * u) }
        drawLine(t.c(ROJO), bc, bc + Offset(-brx * 0.55f, -bry * 0.4f), 1.1f * u, StrokeCap.Round)
        drawLine(t.c(TINTA), bc, bc + Offset(brx * 0.55f, bry * 0.4f), 1.1f * u, StrokeCap.Round)
        drawCircle(t.c(BRONCE), 1f * u, bc)
        drawArc(t.b(0.6f), 190f, 70f, false, Offset(bc.x - brx * 0.7f, bc.y - bry * 0.7f), Size(brx * 1.4f, bry * 1.4f), style = Stroke(1f * u))
        destello(bc + Offset(-brx * 0.75f, -bry * 0.5f), 0.9f * u, t)
        // El compás de puntas, abierto.
        val ch0 = p(0.215f, 0.42f); val c1 = p(0.295f, 0.72f); val c2 = p(0.255f, 0.84f)
        val acero = t.c(mezcla(HOJALATA, PAPEL, 0.15f))
        listOf(c1, c2).forEach { q ->
            drawLine(t.s(0.35f), ch0 + Offset(1.5f * u, 2f * u), q + Offset(1.5f * u, 2f * u), 1.4f * u)
            drawLine(t.c(BRONCE), ch0, Offset(ch0.x + (q.x - ch0.x) * 0.55f, ch0.y + (q.y - ch0.y) * 0.55f), 2.2f * u, StrokeCap.Round)
            drawLine(acero, Offset(ch0.x + (q.x - ch0.x) * 0.5f, ch0.y + (q.y - ch0.y) * 0.5f), q, 1.2f * u, StrokeCap.Round)
        }
        drawCircle(t.c(BRONCE), 2.4f * u, ch0)
        drawCircle(t.c(BRONCE_OSC), 0.9f * u, ch0)
        destello(ch0 - Offset(0.8f * u, 0.8f * u), 0.6f * u, t)

        // La carta a medio escribir, con sus renglones, y la pluma atravesada.
        val carta = cuadro(0.395f, 0.26f, 0.625f, 0.86f, 0.006f)
        translate(2f * u, 2.4f * u) { drawPath(carta, t.s(0.35f)) }
        drawPath(carta, t.c(PAPEL))
        clipPath(carta) {
            for (j in 0 until 6) {
                val fy = 0.36f + 0.075f * j
                val a = p(0.415f, fy); val z = p(if (j == 5) 0.5f else 0.6f - 0.01f * (j % 2), fy)
                val ren = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (z.x - a.x) * 0.3f, a.y - 1.3f * u, a.x + (z.x - a.x) * 0.65f, a.y + 1.3f * u, z.x, z.y) }
                drawPath(ren, tinta.copy(alpha = 0.75f), style = Stroke(0.7f * u))
            }
            drawRect(t.s(0.08f), Offset(p(0.56f, 0.26f).x, p(0f, 0.26f).y), Size(w * 0.08f, w * 0.2f))
        }
        val pl0 = p(0.52f, 0.78f); val pl1 = p(0.69f, 0.16f)
        val dirx = pl1.x - pl0.x; val diry = pl1.y - pl0.y
        val largoP = kotlin.math.sqrt(dirx * dirx + diry * diry)
        val nx = -diry / largoP; val ny = dirx / largoP
        fun q(f: Float, lado: Float) = Offset(pl0.x + dirx * f + nx * lado, pl0.y + diry * f + ny * lado)
        val barbas = Path().apply {
            moveTo(q(0.3f, 0f).x, q(0.3f, 0f).y)
            cubicTo(q(0.42f, w * 0.014f).x, q(0.42f, w * 0.014f).y, q(0.8f, w * 0.018f).x, q(0.8f, w * 0.018f).y, q(1.04f, w * 0.004f).x, q(1.04f, w * 0.004f).y)
            cubicTo(q(0.85f, -w * 0.012f).x, q(0.85f, -w * 0.012f).y, q(0.5f, -w * 0.012f).x, q(0.5f, -w * 0.012f).y, q(0.3f, 0f).x, q(0.3f, 0f).y)
            close()
        }
        translate(2.2f * u, 3f * u) { drawPath(barbas, t.s(0.3f)); drawLine(t.s(0.3f), pl0, q(0.35f, 0f), 1.2f * u) }
        drawPath(barbas, t.c(mezcla(PAPEL, HOJALATA, 0.12f)))
        clipPath(barbas) { for (j in 0 until 7) { val a = q(0.4f + 0.09f * j, 0f); drawLine(t.s(0.2f), a, q(0.46f + 0.09f * j, w * 0.02f), 0.6f * u) } }
        drawLine(t.c(mezcla(PAPEL, BRONCE, 0.25f)), pl0, q(1f, 0f), 1.1f * u, StrokeCap.Round)
        drawLine(tinta, pl0, q(0.05f, 0f), 1.3f * u, StrokeCap.Round)

        // El tintero de vidrio con su casquillo de latón y la tapa abierta, y la salvadera de boj.
        val ti = p(0.73f, 0.52f)
        val tw = w * 0.042f; val th = w * 0.036f
        val cuerpoT = Rect(ti.x - tw / 2, ti.y - th, ti.x + tw / 2, ti.y)
        drawOval(t.s(0.4f), Offset(cuerpoT.left - 1f * u, ti.y - 3f * u), Size(tw * 1.5f, 7f * u))
        drawRoundRect(t.c(mezcla(TINTA, HOJALATA, 0.25f)), cuerpoT.topLeft, cuerpoT.size, CornerRadius(2f * u))
        clipRect(cuerpoT.left, cuerpoT.top, cuerpoT.right, cuerpoT.bottom) { cilindro(cuerpoT, t, 1.5f) }
        drawRect(t.c(TINTA), Offset(cuerpoT.left + 2f * u, cuerpoT.top + th * 0.45f), Size(tw - 4f * u, th * 0.45f))
        val cas = Rect(ti.x - tw * 0.3f, cuerpoT.top - w * 0.008f, ti.x + tw * 0.3f, cuerpoT.top + 1f * u)
        drawRect(t.c(BRONCE), cas.topLeft, cas.size)
        drawRect(t.b(0.5f), cas.topLeft, Size(cas.width * 0.3f, cas.height))
        val tapaT = poligono(cas.right - 1f * u, cas.top, cas.right + w * 0.004f, cas.top - w * 0.022f, cas.right + w * 0.012f, cas.top - w * 0.02f, cas.right + w * 0.004f, cas.top + 1f * u)
        drawPath(tapaT, t.c(BRONCE))
        destello(Offset(cuerpoT.left + tw * 0.22f, cuerpoT.top + th * 0.25f), 0.9f * u, t)
        val sa = p(0.805f, 0.4f)
        val sw = w * 0.034f; val sh = w * 0.034f
        val boj = mezcla(BRONCE, CORREA, 0.45f)
        val salv = poligono(sa.x - sw * 0.5f, sa.y - sh, sa.x + sw * 0.5f, sa.y - sh, sa.x + sw * 0.34f, sa.y - sh * 0.5f, sa.x + sw * 0.46f, sa.y, sa.x - sw * 0.46f, sa.y, sa.x - sw * 0.34f, sa.y - sh * 0.5f)
        drawOval(t.s(0.38f), Offset(sa.x - sw * 0.4f, sa.y - 2.5f * u), Size(sw * 1.4f, 6f * u))
        drawPath(salv, t.c(boj))
        clipPath(salv) { cilindro(Rect(sa.x - sw / 2, sa.y - sh, sa.x + sw / 2, sa.y), t, 1.3f) }
        drawOval(t.c(mezcla(boj, CAOBA_OSC, 0.35f)), Offset(sa.x - sw * 0.5f, sa.y - sh - w * 0.005f), Size(sw, w * 0.01f))
        for (j in 0 until 4) drawCircle(t.s(0.6f), 0.6f * u, Offset(sa.x - sw * 0.24f + sw * 0.16f * j, sa.y - sh))

        // La barra de lacre, con la punta quemada, sus gotas y el sello de mango torneado.
        val l0 = p(0.855f, 0.74f); val l1 = p(0.94f, 0.58f)
        drawLine(t.s(0.35f), l0 + Offset(2f * u, 2.5f * u), l1 + Offset(2f * u, 2.5f * u), 3.2f * u)
        drawLine(t.c(ROJO), l0, l1, 3.6f * u)
        drawLine(t.b(0.35f), l0 - Offset(0f, 1f * u), l1 - Offset(0f, 1f * u), 0.8f * u)
        drawLine(t.c(mezcla(ROJO, FIELTRO, 0.6f)), l0, Offset(l0.x + (l1.x - l0.x) * 0.14f, l0.y + (l1.y - l0.y) * 0.14f), 3.6f * u)
        listOf(p(0.83f, 0.8f), p(0.81f, 0.72f)).forEachIndexed { i, g -> drawOval(t.c(ROJO), Offset(g.x - (2.2f - i) * u, g.y - 1.1f * u), Size((4.4f - 2f * i) * u, 2.2f * u)) }
        val se = p(0.905f, 0.36f)
        drawOval(t.s(0.38f), Offset(se.x - w * 0.008f, se.y - 2.2f * u), Size(w * 0.03f, 5f * u))
        val mango = poligono(se.x - w * 0.006f, se.y - w * 0.04f, se.x + w * 0.006f, se.y - w * 0.04f, se.x + w * 0.009f, se.y - w * 0.026f, se.x + w * 0.004f, se.y - w * 0.012f, se.x - w * 0.004f, se.y - w * 0.012f, se.x - w * 0.009f, se.y - w * 0.026f)
        drawPath(mango, t.c(CAOBA))
        clipPath(mango) { cilindro(Rect(se.x - w * 0.009f, se.y - w * 0.04f, se.x + w * 0.009f, se.y), t, 1.3f) }
        drawCircle(t.c(CAOBA_LUZ), w * 0.007f, Offset(se.x, se.y - w * 0.042f))
        val matriz = Rect(se.x - w * 0.011f, se.y - w * 0.012f, se.x + w * 0.011f, se.y)
        drawRect(t.c(BRONCE), matriz.topLeft, matriz.size)
        clipRect(matriz.left, matriz.top, matriz.right, matriz.bottom) { cilindro(matriz, t, 1.5f) }
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El bicornio de fieltro negro, de pie sobre sus dos puntas y recostado en la lona: la media luna del ala de delante, la de
     * atrás que asoma por encima y por el arco de abajo, el galón del borde, la escarapela con su presilla dorada y su
     * botón, las borlas de las puntas, el fieltro rozado y con polvo.
     */
    private fun DrawScope.bicornio(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val a = w * 0.182f; val hg = w * 0.19f; val arco = w * 0.07f
        val cx = pie.x; val py = pie.y
        // El fieltro negro se bebe la luz: de noche le llega a medias, y la vela se le nota sobre todo en el filo.
        val t = tn(Offset(cx + a * 0.5f, py - hg * 0.55f)).let { if (it.o) Tono(true, it.k * 0.42f) else it }
        val tf = tn(Offset(cx + a * 0.5f, py - hg * 0.55f))
        val der = luzDerecha(cx)
        /** El borde de arriba de un ala, de punta a punta. */
        fun lomo(p: Path, dx: Float, dy: Float, k: Float) {
            p.cubicTo(cx - a * 0.93f + dx, py - hg * 0.62f * k + dy, cx - a * 0.52f + dx, py - hg * k + dy, cx + dx, py - hg * k + dy)
            p.cubicTo(cx + a * 0.52f + dx, py - hg * k + dy, cx + a * 0.93f + dx, py - hg * 0.62f * k + dy, cx + a + dx, py + dy)
        }
        fun ala(dx: Float, dy: Float, k: Float = 1f) = Path().apply {
            moveTo(cx - a + dx, py + dy)
            lomo(this, dx, dy, k)
            cubicTo(cx + a * 0.76f + dx, py - arco * 0.86f + dy, cx + a * 0.4f + dx, py - arco + dy, cx + dx, py - arco + dy)
            cubicTo(cx - a * 0.4f + dx, py - arco + dy, cx - a * 0.76f + dx, py - arco * 0.86f + dy, cx - a + dx, py + dy)
            close()
        }
        val delante = ala(0f, 0f)
        val detras = ala(w * 0.014f, -w * 0.005f, 1.07f)
        val lomoDelante = Path().apply { moveTo(cx - a, py); lomo(this, 0f, 0f, 1f) }
        val todo = Path().apply { addPath(delante); addPath(detras) }
        val giro = -2.5f
        rotate(giro, pie) { sombraEnPared(todo, Offset(cx, py - hg * 0.5f), sup + 2f * u, 0.26f) }
        // Pisa la tapa con las dos puntas.
        listOf(-1f, 1f).forEach { s -> contacto(Offset(cx + s * a * 0.9f + w * 0.01f, py + 1f * u), a * 0.5f, 4.5f * u, 0.42f) }
        contacto(Offset(cx + w * 0.02f, py + 2f * u), a * 1.5f, 3f * u, 0.16f)
        rotate(giro, pie) {
            // El ala de atrás, que asoma por arriba y por debajo del arco.
            drawPath(detras, t.c(mezcla(FIELTRO, NOCHE, 0.4f)))
            clipPath(detras) { drawRect(t.s(0.3f), Offset(cx - a, py - arco * 1.4f), Size(a * 2.2f, arco * 1.5f)) }
            if (tf.o && tf.k > 0.06f) clipRect(if (der) cx - a * 0.1f else cx - a * 1.2f, py - hg * 1.3f, if (der) cx + a * 1.3f else cx + a * 0.1f, py - hg * 0.3f) {
                drawPath(detras, LLAMA.copy(alpha = (0.6f * tf.k).coerceAtMost(0.55f)), style = Stroke(1.2f * u))
            }
            // El ala de delante: la media luna.
            drawPath(delante, t.c(FIELTRO))
            clipPath(delante) {
                // La luz en el fieltro: un claro ancho hacia la luz, el otro lado en sombra y el brillo que sigue el lomo.
                // Las manchas siguen la curva del lomo (medio lomo para cada lado), no van en franjas a plomo.
                val s = if (der) -1f else 1f
                fun medio(lado: Float) = Path().apply {
                    moveTo(cx, py - hg)
                    cubicTo(cx + lado * a * 0.52f, py - hg, cx + lado * a * 0.93f, py - hg * 0.62f, cx + lado * a, py)
                }
                val haciaLuz = medio(-s); val aSombra = medio(s)
                if (!esNoche) {
                    translate(-s * w * 0.012f, w * 0.045f) { drawPath(aSombra, t.s(0.26f), style = Stroke(w * 0.09f, cap = StrokeCap.Round)) }
                    translate(-s * w * 0.02f, w * 0.06f) { drawPath(aSombra, t.s(0.2f), style = Stroke(w * 0.06f, cap = StrokeCap.Round)) }
                    translate(s * w * 0.01f, w * 0.04f) { drawPath(haciaLuz, t.b(0.09f), style = Stroke(w * 0.07f, cap = StrokeCap.Round)) }
                    translate(s * w * 0.004f, w * 0.022f) { drawPath(haciaLuz, t.b(0.1f), style = Stroke(w * 0.018f, cap = StrokeCap.Round)) }
                    translate(s * w * 0.004f, w * 0.016f) { drawPath(lomoDelante, t.b(0.1f), style = Stroke(w * 0.005f)) }
                } else if (tf.k > 0.06f) {
                    // De noche, el resplandor de la vela en el lado que la mira: se apaga hacia dentro del fieltro.
                    translate(s * w * 0.008f, w * 0.034f) { drawPath(haciaLuz, LLAMA.copy(alpha = 0.09f * tf.k), style = Stroke(w * 0.07f, cap = StrokeCap.Round)) }
                    translate(s * w * 0.006f, w * 0.018f) { drawPath(haciaLuz, LLAMA.copy(alpha = 0.15f * tf.k), style = Stroke(w * 0.03f, cap = StrokeCap.Round)) }
                }
                // El polvo del camino y las rozaduras.
                pinceladas(Rect(cx - a, py - hg, cx + a, py), if (esNoche) NOCHE else LONA, 4170, 10, u, 0.07f)
                for (j in 0 until 5) {
                    val q = Offset(cx - a * 0.7f + a * 1.4f * azar(4171, j), py - hg * (0.55f + 0.3f * azar(4172, j)))
                    drawLine(t.c(mezcla(FIELTRO, LONA, 0.3f)).copy(alpha = 0.6f), q, q + Offset(w * 0.016f, w * 0.004f), 0.7f * u, StrokeCap.Round)
                }
                // El pliegue blando del ala, que baja del pico al arco.
                val pl = Path().apply { moveTo(cx + a * 0.1f, py - hg * 0.97f); cubicTo(cx + a * 0.18f, py - hg * 0.75f, cx + a * 0.08f, py - hg * 0.55f, cx + a * 0.14f, py - arco) }
                drawPath(pl, t.s(0.32f), style = Stroke(2.2f * u))
                translate(-2f * u, 0f) { drawPath(pl, t.b(0.1f), style = Stroke(1.2f * u)) }
                // El canto de abajo, más hondo.
                translate(0f, -w * 0.006f) {
                    val bajo = Path().apply {
                        moveTo(cx + a, py)
                        cubicTo(cx + a * 0.76f, py - arco * 0.86f, cx + a * 0.4f, py - arco, cx, py - arco)
                        cubicTo(cx - a * 0.4f, py - arco, cx - a * 0.76f, py - arco * 0.86f, cx - a, py)
                    }
                    drawPath(bajo, t.s(0.3f), style = Stroke(w * 0.014f))
                }
            }
            // El galón de seda del borde, con su brillo en el lomo.
            drawPath(delante, t.c(mezcla(FIELTRO, HOJALATA, 0.3f)), style = Stroke(1.5f * u))
            if (!esNoche) clipRect(cx - a * 1.1f, py - hg * 1.2f, cx + a * 0.1f, py - hg * 0.2f) { drawPath(lomoDelante, luzMueble(0.45f), style = Stroke(1f * u)) }
            else if (tf.k > 0.06f) clipRect(if (der) cx - a * 0.3f else cx - a * 1.2f, py - hg * 1.2f, if (der) cx + a * 1.2f else cx + a * 0.3f, py + 2f * u) {
                drawPath(lomoDelante, LLAMA.copy(alpha = (0.9f * tf.k).coerceAtMost(0.8f)), style = Stroke(1.5f * u))
            }
            // La escarapela (azul al centro, rojo y blanco al borde), con sus pliegues, la presilla dorada y el botón.
            val c = Offset(cx - a * 0.4f, py - hg * 0.64f)
            val rc = w * 0.031f
            drawCircle(t.s(0.35f), rc, c + Offset(1.6f * u, 2f * u))
            drawCircle(tf.c(mezcla(PAPEL, LONA, 0.2f)), rc, c)
            drawCircle(tf.c(ROJO), rc * 0.72f, c)
            drawCircle(tf.c(AZUL), rc * 0.42f, c)
            for (j in 0 until 12) { val an = j * 0.5236f; drawLine(t.s(0.16f), Offset(c.x + rc * 0.2f * cos(an), c.y + rc * 0.2f * sin(an)), Offset(c.x + rc * cos(an), c.y + rc * sin(an)), 0.6f * u) }
            drawCircle(t.s(0.14f), rc, c, style = Stroke(1f * u))
            val oro = mezcla(BRONCE, LUZ_MUEBLE, 0.15f)
            val arriba = Offset(c.x + w * 0.012f, py - hg * 0.955f)
            listOf(-1f, 1f).forEach { s ->
                drawLine(t.s(0.3f), arriba + Offset(s * 1.5f * u + 1f * u, 1.2f * u), c + Offset(s * 1.5f * u + 1f * u, rc * 0.2f + 1.2f * u), 1.4f * u)
                drawLine(t.c(oro), arriba + Offset(s * 1.5f * u, 0f), c + Offset(s * 1.5f * u, rc * 0.2f), 1.5f * u)
            }
            drawCircle(t.s(0.35f), 2.6f * u, c + Offset(0.8f * u, rc * 0.3f + 1f * u))
            drawCircle(t.c(oro), 2.5f * u, c + Offset(0f, rc * 0.3f))
            destello(c + Offset(-0.8f * u, rc * 0.3f - 0.8f * u), 0.8f * u, t)
            // Las borlas doradas de las puntas, caídas sobre la tapa.
            listOf(-1f, 1f).forEach { s ->
                val q = Offset(cx + s * (a - w * 0.004f), py - w * 0.006f)
                for (j in 0 until 4) drawLine(t.c(if (j % 2 == 0) oro else BRONCE_OSC), q, q + Offset(s * w * (0.012f + 0.004f * j), w * (0.012f - 0.003f * j)), 1.2f * u, StrokeCap.Round)
                drawCircle(t.c(oro), 2.2f * u, q)
                destello(q - Offset(0.7f * u, 0.7f * u), 0.6f * u, t)
            }
        }
    }

    /** La llama de la vela (de noche): el halo corto, la lengua y su corazón claro. */
    private fun DrawScope.llama(p: Offset, s: Float) {
        drawCircle(LLAMA.copy(alpha = 0.3f), s * 1.5f, p)
        val lengua = Path().apply {
            moveTo(p.x, p.y - s * 1.15f)
            cubicTo(p.x + s * 0.5f, p.y - s * 0.3f, p.x + s * 0.42f, p.y + s * 0.55f, p.x, p.y + s * 0.6f)
            cubicTo(p.x - s * 0.42f, p.y + s * 0.55f, p.x - s * 0.5f, p.y - s * 0.3f, p.x, p.y - s * 1.15f)
            close()
        }
        drawPath(lengua, LLAMA)
        drawOval(Color(0xFFFFF4D8), Offset(p.x - s * 0.2f, p.y - s * 0.3f), Size(s * 0.4f, s * 0.8f))
        drawOval(mezcla(AZUL, LLAMA, 0.4f).copy(alpha = 0.6f), Offset(p.x - s * 0.16f, p.y + s * 0.3f), Size(s * 0.32f, s * 0.3f))
    }

    /**
     * El candelero de campaña de latón: el platillo ancho con su asa de anilla, el fuste torneado, la arandela y el cubo, la
     * vela de sebo a medio gastar con sus chorreras, y el apagavelas de cucurucho apoyado en el platillo. De noche, encendida.
     */
    private fun DrawScope.candelero(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val o = esNoche
        val x = pie.x; val y = pie.y
        val llamaP = LUZ * w
        val t = tn(Offset(x + w * 0.04f, y - w * 0.07f))
        val tArriba = tn(Offset(x, llamaP.y + w * 0.05f))
        val velaTop = llamaP.y + w * 0.026f
        val cuboTop = y - w * 0.108f
        val vela = Rect(x - w * 0.0155f, velaTop, x + w * 0.0155f, cuboTop + w * 0.004f)
        val fuste = Path().apply {
            moveTo(x - w * 0.011f, y - w * 0.088f)
            cubicTo(x - w * 0.032f, y - w * 0.072f, x - w * 0.03f, y - w * 0.05f, x - w * 0.011f, y - w * 0.042f)
            lineTo(x - w * 0.009f, y - w * 0.03f)
            cubicTo(x - w * 0.02f, y - w * 0.026f, x - w * 0.027f, y - w * 0.014f, x - w * 0.036f, y - w * 0.006f)
            lineTo(x + w * 0.036f, y - w * 0.006f)
            cubicTo(x + w * 0.027f, y - w * 0.014f, x + w * 0.02f, y - w * 0.026f, x + w * 0.009f, y - w * 0.03f)
            lineTo(x + w * 0.011f, y - w * 0.042f)
            cubicTo(x + w * 0.03f, y - w * 0.05f, x + w * 0.032f, y - w * 0.072f, x + w * 0.011f, y - w * 0.088f)
            close()
        }
        val cubo = Rect(x - w * 0.019f, cuboTop, x + w * 0.019f, y - w * 0.086f)
        val aran = Rect(x - w * 0.034f, cuboTop + w * 0.01f, x + w * 0.034f, cuboTop + w * 0.026f)
        val todo = Path().apply { addPath(fuste); addRect(vela); addOval(aran) }
        if (!o) sombraEnPared(todo, Offset(x, y - w * 0.1f), sup, 0.16f)
        // El platillo, con su reborde, y el asa de anilla a la derecha.
        val plato = Rect(x - w * 0.066f, y - w * 0.014f, x + w * 0.066f, y + w * 0.014f)
        drawOval(t.s(0.42f), Offset(plato.left + w * 0.008f, plato.top + w * 0.007f), Size(plato.width * 1.06f, plato.height))
        drawOval(t.c(BRONCE_OSC), plato.topLeft + Offset(0f, w * 0.005f), plato.size)
        drawOval(t.c(BRONCE), plato.topLeft, plato.size)
        drawOval(t.c(mezcla(BRONCE, BRONCE_OSC, 0.45f)), Offset(plato.left + w * 0.01f, plato.top + w * 0.004f), Size(plato.width - w * 0.02f, plato.height - w * 0.01f))
        drawArc(t.b(0.6f), 180f, 90f, false, plato.topLeft, plato.size, style = Stroke(1f * u))
        val asa = Rect(x + w * 0.058f, y - w * 0.022f, x + w * 0.09f, y + w * 0.006f)
        drawOval(t.s(0.3f), asa.topLeft + Offset(1.2f * u, 1.6f * u), asa.size, style = Stroke(2.2f * u))
        drawOval(t.c(BRONCE), asa.topLeft, asa.size, style = Stroke(2.6f * u))
        drawArc(t.b(0.6f), 180f, 100f, false, asa.topLeft, asa.size, style = Stroke(0.7f * u))
        // El fuste torneado, la arandela y el cubo.
        drawPath(fuste, t.c(BRONCE))
        clipPath(fuste) {
            cilindro(Rect(x - w * 0.034f, y - w * 0.09f, x + w * 0.034f, y), t, 1.5f)
            drawRect(t.s(0.3f), Offset(x - w * 0.04f, y - w * 0.044f), Size(w * 0.08f, 1.2f * u))
            drawRect(t.s(0.3f), Offset(x - w * 0.04f, y - w * 0.031f), Size(w * 0.08f, 1.2f * u))
            drawRect(t.s(0.25f), Offset(x - w * 0.04f, y - w * 0.012f), Size(w * 0.08f, w * 0.008f))
        }
        drawRect(t.c(BRONCE), cubo.topLeft, cubo.size)
        clipRect(cubo.left, cubo.top, cubo.right, cubo.bottom) { cilindro(cubo, t, 1.5f) }
        drawOval(t.s(0.3f), aran.topLeft + Offset(0f, 1.8f * u), aran.size)
        drawOval(t.c(BRONCE), aran.topLeft, aran.size)
        drawOval(t.c(mezcla(BRONCE, BRONCE_OSC, 0.5f)), Offset(aran.left + w * 0.006f, aran.top + w * 0.003f), Size(aran.width - w * 0.012f, aran.height - w * 0.007f))
        drawArc(t.b(0.6f), 180f, 100f, false, aran.topLeft, aran.size, style = Stroke(0.9f * u))
        destello(Offset(x - w * 0.016f, y - w * 0.068f), 1.1f * u, t)
        // La vela: el sebo con su lado en sombra, las chorreras, el pozo de arriba y el pabilo.
        val sebo = mezcla(PAPEL, BRONCE, 0.14f)
        drawRect(tArriba.c(sebo), vela.topLeft, vela.size)
        clipRect(vela.left, vela.top, vela.right, vela.bottom) { cilindro(vela, tArriba, 1f) }
        if (o) drawRect(Brush.verticalGradient(listOf(LLAMA.copy(alpha = 0.6f), Color.Transparent), vela.top, vela.top + vela.height * 0.6f), vela.topLeft, Size(vela.width, vela.height * 0.6f))
        listOf(Triple(-0.0155f, 0.034f, 0.008f), Triple(0.008f, 0.056f, 0.0065f), Triple(-0.005f, 0.02f, 0.006f)).forEach { (dx, largo, g) ->
            drawRoundRect(tArriba.c(mezcla(sebo, PAPEL, 0.5f)), Offset(x + w * dx, velaTop + w * 0.002f), Size(w * g, w * largo), CornerRadius(w * g / 2))
            drawCircle(tArriba.c(mezcla(sebo, PAPEL, 0.5f)), w * g * 0.7f, Offset(x + w * (dx + g / 2), velaTop + w * (largo + 0.002f)))
        }
        // La cera que rebosó y se cuajó en la arandela.
        drawOval(t.c(mezcla(sebo, PAPEL, 0.4f)), Offset(x - w * 0.026f, cuboTop + w * 0.006f), Size(w * 0.03f, w * 0.01f))
        drawOval(tArriba.c(mezcla(sebo, BRONCE_OSC, 0.25f)), Offset(vela.left, velaTop - w * 0.004f), Size(vela.width, w * 0.008f))
        drawLine(tArriba.c(FIELTRO), Offset(x, velaTop), Offset(x + w * 0.001f, velaTop - w * 0.014f), 1.2f * u, StrokeCap.Round)
        if (o) llama(llamaP, w * 0.023f)
        // El apagavelas de cucurucho, apoyado en el platillo, con su ganchito.
        val ap = Offset(x - w * 0.052f, y - w * 0.002f)
        val cono = poligono(ap.x - w * 0.02f, ap.y, ap.x + w * 0.018f, ap.y + w * 0.004f, ap.x + w * 0.007f, ap.y - w * 0.052f)
        translate(1.4f * u, 1.6f * u) { drawPath(cono, t.s(0.3f)) }
        drawPath(cono, t.c(mezcla(BRONCE, BRONCE_OSC, 0.25f)))
        clipPath(cono) { cilindro(Rect(ap.x - w * 0.02f, ap.y - w * 0.052f, ap.x + w * 0.018f, ap.y + w * 0.004f), t, 1.4f, luzDerecha(ap.x)) }
        drawCircle(t.c(BRONCE), w * 0.006f, Offset(ap.x + w * 0.008f, ap.y - w * 0.056f), style = Stroke(1.2f * u))
    }

    /**
     * La cantimplora de calabaza del soldado: la calabaza de peregrino con sus dos panzas, las manchas y el brillo de la
     * cáscara, el tapón de madera, el cordón anudado a la cintura y la bandolera de cáñamo que cae sobre la tapa.
     */
    private fun DrawScope.calabaza(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val cx = pie.x; val yB = pie.y; val yT = pie.y - w * 0.15f
        val t = tn(Offset(cx, yB - w * 0.07f))
        val der = luzDerecha(cx)
        val cascara = mezcla(BRONCE, CORREA, 0.5f)
        val canamo = mezcla(CORREA, LONA, 0.5f)
        val forma = Path().apply {
            moveTo(cx - w * 0.012f, yT)
            cubicTo(cx - w * 0.03f, yT + w * 0.012f, cx - w * 0.032f, yT + w * 0.042f, cx - w * 0.017f, yT + w * 0.054f)
            cubicTo(cx - w * 0.058f, yT + w * 0.07f, cx - w * 0.064f, yB - w * 0.012f, cx - w * 0.03f, yB)
            lineTo(cx + w * 0.03f, yB)
            cubicTo(cx + w * 0.064f, yB - w * 0.012f, cx + w * 0.058f, yT + w * 0.07f, cx + w * 0.017f, yT + w * 0.054f)
            cubicTo(cx + w * 0.032f, yT + w * 0.042f, cx + w * 0.03f, yT + w * 0.012f, cx + w * 0.012f, yT)
            close()
        }
        // La bandolera, que cae por detrás y sobre la tapa hacia la derecha.
        val band = Path().apply {
            moveTo(cx + w * 0.016f, yT + w * 0.056f)
            cubicTo(cx + w * 0.09f, yT + w * 0.05f, cx + w * 0.085f, yB - w * 0.02f, cx + w * 0.06f, yB + w * 0.004f)
            cubicTo(cx + w * 0.04f, yB + w * 0.02f, cx + w * 0.085f, yB + w * 0.03f, cx + w * 0.1f, yB + w * 0.012f)
        }
        translate(w * 0.008f, w * 0.006f) { drawPath(band, t.s(0.25f), style = Stroke(2f * u)) }
        drawPath(band, t.c(canamo), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(band, t.s(0.2f), style = Stroke(0.6f * u))
        rotate(4f, pie) {
            sombraEnPared(forma, Offset(cx, yB - w * 0.07f), sup + 2f * u, 0.22f)
        }
        contacto(Offset(cx + w * 0.004f, yB), w * 0.1f, 4f * u, 0.42f)
        rotate(4f, pie) {
            drawPath(forma, t.c(cascara))
            clipPath(forma) {
                cilindro(Rect(cx - w * 0.06f, yT, cx + w * 0.06f, yB), t, 1.3f, der)
                drawRect(t.s(0.16f), Offset(cx - w * 0.07f, yB - w * 0.025f), Size(w * 0.14f, w * 0.03f))
                // Las manchas de la cáscara.
                for (j in 0 until 6) drawOval(t.c(mezcla(cascara, CAOBA_OSC, 0.4f)).copy(alpha = 0.45f), Offset(cx - w * 0.045f + w * 0.08f * azar(4180, j), yT + w * (0.02f + 0.12f * azar(4181, j))), Size(w * (0.008f + 0.014f * azar(4182, j)), w * 0.008f))
            }
            destello(Offset(cx + (if (der) 0.03f else -0.03f) * w, yB - w * 0.06f), 1.3f * u, t)
            filoContorno(forma, t, cx, der, 0.8f)
            // El tapón de madera.
            val tap = poligono(cx - w * 0.009f, yT + 1f * u, cx - w * 0.012f, yT - w * 0.016f, cx + w * 0.012f, yT - w * 0.016f, cx + w * 0.009f, yT + 1f * u)
            drawPath(tap, t.c(mezcla(CORREA, CAOBA_OSC, 0.4f)))
            drawRect(t.b(0.3f), Offset(cx - w * 0.012f, yT - w * 0.016f), Size(w * 0.008f, w * 0.016f))
            // El cordón de la cintura con su nudo y los dos ramales que abrazan la panza.
            val cin = yT + w * 0.055f
            drawLine(t.s(0.3f), Offset(cx - w * 0.02f, cin + 1.8f * u), Offset(cx + w * 0.02f, cin + 1.8f * u), 1.4f * u)
            drawLine(t.c(canamo), Offset(cx - w * 0.021f, cin), Offset(cx + w * 0.021f, cin), 2.6f * u, StrokeCap.Round)
            listOf(-1f, 1f).forEach { s ->
                val ramal = Path().apply { moveTo(cx + s * w * 0.012f, cin); cubicTo(cx + s * w * 0.04f, cin + w * 0.03f, cx + s * w * 0.04f, yB - w * 0.03f, cx + s * w * 0.016f, yB - w * 0.002f) }
                drawPath(ramal, t.s(0.25f), style = Stroke(2.6f * u))
                drawPath(ramal, t.c(canamo), style = Stroke(1.6f * u))
            }
            drawCircle(t.c(canamo), 2.4f * u, Offset(cx - w * 0.004f, cin))
            drawCircle(t.s(0.3f), 2.4f * u, Offset(cx - w * 0.004f, cin), style = Stroke(0.6f * u))
        }
    }

    /** La pipa de arcilla blanca, tumbada: la caña larga y fina y la cazoleta con el borde tiznado. */
    private fun DrawScope.pipa(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val arcilla = mezcla(PAPEL, LONA, 0.25f)
        val a = Offset(c.x - w * 0.085f, c.y + w * 0.004f); val z = Offset(c.x + w * 0.04f, c.y - w * 0.002f)
        drawLine(t.s(0.35f), a + Offset(1.5f * u, 2.2f * u), z + Offset(1.5f * u, 2.2f * u), 1.6f * u)
        drawLine(t.c(arcilla), a, z, 2f * u, StrokeCap.Round)
        drawLine(t.s(0.18f), a + Offset(0f, 0.7f * u), z + Offset(0f, 0.7f * u), 0.6f * u)
        val caz = poligono(z.x - w * 0.004f, z.y + w * 0.004f, z.x + w * 0.002f, z.y - w * 0.022f, z.x + w * 0.018f, z.y - w * 0.02f, z.x + w * 0.012f, z.y + w * 0.006f)
        translate(1.5f * u, 2f * u) { drawPath(caz, t.s(0.3f)) }
        drawPath(caz, t.c(arcilla))
        drawPath(poligono(z.x + w * 0.008f, z.y - w * 0.021f, z.x + w * 0.018f, z.y - w * 0.02f, z.x + w * 0.012f, z.y + w * 0.006f, z.x + w * 0.006f, z.y + w * 0.005f), t.s(0.18f))
        drawLine(t.c(mezcla(FIELTRO, CORREA, 0.3f)), Offset(z.x + w * 0.002f, z.y - w * 0.022f), Offset(z.x + w * 0.018f, z.y - w * 0.02f), 1.6f * u)
    }

    /** El reloj de bolsillo, tumbado y abierto: la caja de latón, la esfera con sus horas y sus agujas, la anilla y la cadena con su llave, que cuelga del canto. */
    private fun DrawScope.reloj(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val rx = w * 0.024f; val ry = w * 0.013f
        // La cadena, que va al borde y cuelga por delante del canto.
        val cad = Path().apply {
            moveTo(c.x + rx * 0.9f, c.y)
            cubicTo(c.x + w * 0.05f, c.y + w * 0.004f, c.x + w * 0.05f, c.y + w * 0.012f, c.x + w * 0.062f, c.y + w * 0.018f)
            cubicTo(c.x + w * 0.066f, c.y + w * 0.03f, c.x + w * 0.062f, c.y + w * 0.045f, c.x + w * 0.064f, c.y + w * 0.06f)
        }
        val eslabones = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.3f * u))
        translate(1.4f * u, 1.8f * u) { drawPath(cad, t.s(0.3f), style = Stroke(1.6f * u, pathEffect = eslabones)) }
        drawPath(cad, t.c(BRONCE), style = Stroke(1.7f * u, pathEffect = eslabones))
        val llave = Offset(c.x + w * 0.064f, c.y + w * 0.06f)
        drawLine(t.c(BRONCE), llave, llave + Offset(0f, w * 0.016f), 1.4f * u)
        drawCircle(t.c(BRONCE), 1.8f * u, llave + Offset(0f, w * 0.002f), style = Stroke(1f * u))
        // La caja y la esfera.
        drawOval(t.s(0.4f), Offset(c.x - rx + 2f * u, c.y - ry + w * 0.006f), Size(rx * 2, ry * 2))
        drawOval(t.c(BRONCE_OSC), Offset(c.x - rx, c.y - ry + w * 0.004f), Size(rx * 2, ry * 2))
        drawOval(t.c(BRONCE), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(t.c(PAPEL), Offset(c.x - rx * 0.8f, c.y - ry * 0.8f), Size(rx * 1.6f, ry * 1.6f))
        for (j in 0 until 12) { val a = j * 0.5236f; drawLine(t.c(TINTA).copy(alpha = 0.7f), Offset(c.x + rx * 0.6f * cos(a), c.y + ry * 0.6f * sin(a)), Offset(c.x + rx * 0.74f * cos(a), c.y + ry * 0.74f * sin(a)), 0.5f * u) }
        drawLine(t.c(TINTA), c, c + Offset(rx * 0.1f, -ry * 0.55f), 0.9f * u, StrokeCap.Round)
        drawLine(t.c(TINTA), c, c + Offset(rx * 0.5f, ry * 0.2f), 0.7f * u, StrokeCap.Round)
        drawArc(t.b(0.7f), 180f, 90f, false, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(0.9f * u))
        drawCircle(t.c(BRONCE), w * 0.006f, Offset(c.x - rx - w * 0.004f, c.y - w * 0.002f), style = Stroke(1.2f * u))
        destello(Offset(c.x - rx * 0.5f, c.y - ry * 0.45f), 0.8f * u, t)
    }

    /**
     * El catalejo, tumbado al frente de la tapa y a medio abrir: el cuerpo forrado de cuero rojizo, los tres tubos de latón
     * que salen cada vez más delgados, sus anillos, el ocular y el aro del objetivo.
     */
    private fun DrawScope.catalejo(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        // De izquierda a derecha: ocular, tres tubos, el cuerpo y el aro del objetivo. (largo, diámetro, color)
        val tramos = listOf(
            Triple(0.012f, 0.02f, BRONCE_OSC), Triple(0.04f, 0.015f, BRONCE), Triple(0.045f, 0.02f, BRONCE), Triple(0.045f, 0.025f, BRONCE),
            Triple(0.08f, 0.032f, mezcla(CAOBA, ROJO, 0.25f)), Triple(0.012f, 0.036f, BRONCE))
        val total = tramos.sumOf { it.first.toDouble() }.toFloat() * w
        val x0 = c.x - total / 2
        contacto(Offset(c.x + w * 0.02f, c.y + 1f * u), total * 0.98f, 4.5f * u, 0.42f)
        rotate(-3f, c) {
            var x = x0
            tramos.forEachIndexed { i, (l, dia, col) ->
                val r = Rect(x, c.y - w * dia, x + w * l, c.y)
                drawRect(t.c(col), r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    tumbado(r, t, 1.5f)
                    if (i == 4) pinceladas(r, if (esNoche) NOCHE else CAOBA_OSC, 4190, 5, u, 0.2f)
                }
                if (i in 1..4) {
                    drawRect(t.c(BRONCE_OSC), Offset(r.right - w * 0.005f, r.top - 0.6f * u), Size(w * 0.005f, r.height + 0.6f * u))
                    drawRect(t.b(0.5f), Offset(r.right - w * 0.005f, r.top - 0.6f * u), Size(w * 0.005f, 1f * u))
                }
                drawRect(t.s(0.3f), Offset(r.left, r.top), Size(0.8f * u, r.height))
                x += w * l
            }
            destello(Offset(x0 + total * 0.3f, c.y - w * 0.014f), 0.9f * u, t)
            destello(Offset(x - w * 0.006f, c.y - w * 0.028f), 0.9f * u, t)
        }
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0xA5268A23L.toInt()
        x = x xor (x ushr 15)
        x *= 0x1B2E32EF
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está esta casilla en el mueble (en fracciones del ancho), para saber cuánto le llega de la vela.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        // De noche la vela queda arriba, entre las dos columnas: a la de la izquierda le da por la derecha.
        val der = oscuro && dentro && centro.x < LUZ.x
        casilla(w, h, t, der)
        val m = mezclar(c.semilla)
        if (c.vacio) libre(w, h, m, t, der)
        else translate(0f, -SALIDA * 0.8f * h * ab) {
            when {
                c.sinTema -> sombrerera(w, h, c, t, der)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> despachos(w, h, c, m, t, der)
                    1 -> portefeuille(w, h, c, m, t, der)
                    else -> tubo(w, h, c, m, t, der)
                }
            }
        }
        gaveta(w, h, c, t, ab, der)
    }

    /** La sombra de contacto de lo guardado en la tabla de la casilla. */
    private fun DrawScope.apoyo(t: Tono, cx: Float, y: Float, ancho: Float, alto: Float, der: Boolean, a: Float = 0.42f) =
        drawOval(t.s(a), Offset(cx - ancho * (if (der) 0.58f else 0.42f), y - alto / 2), Size(ancho, alto))

    /**
     * La casilla: las tablas del fondo, la sombra de la tabla de arriba en escalones, un costado más hondo, el arco rebajado
     * de la boca y la tabla del piso, vista un poco desde arriba. De día, la fila de arriba un poco más clara; de noche, la
     * vela según lo cerca que esté.
     */
    private fun DrawScope.casilla(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        val boca = h * GAVETA
        val fondoPiso = h * 0.6f
        drawRect(t.c(mezcla(CAOBA_OSC, CAOBA, 0.35f)), Offset.Zero, Size(w, boca))
        if (der) drawRect(t.b(0.1f), Offset(w * 0.55f, 0f), Size(w * 0.45f, boca)) else drawRect(t.b(0.1f), Offset.Zero, Size(w * 0.45f, boca))
        // Las juntas de las tablas del fondo.
        listOf(0.31f, 0.66f).forEach { fx ->
            drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, fondoPiso))
            drawRect(t.b(0.16f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, fondoPiso))
        }
        // La sombra de la tabla de arriba, en escalones, y los costados.
        listOf(0.4f to 0.1f, 0.26f to 0.13f, 0.14f to 0.2f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, boca * a)) }
        drawRect(t.s(0.24f), Offset(if (der) 0f else w * 0.935f, 0f), Size(w * 0.065f, boca))
        drawRect(t.s(0.12f), Offset(if (der) w * 0.978f else 0f, 0f), Size(w * 0.022f, boca))
        // La tabla del piso.
        drawRect(t.c(mezcla(CAOBA, CAOBA_LUZ, 0.55f)), Offset(0f, fondoPiso), Size(w, boca - fondoPiso))
        drawRect(t.s(0.3f), Offset(0f, fondoPiso), Size(w, 1.6f * u))
        drawRect(t.b(0.4f), Offset(0f, boca - 1.4f * u), Size(w, 1.4f * u))
        // El arco rebajado de la boca, con su sombra sobre el fondo.
        val arco = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.125f)
            cubicTo(w * 0.93f, h * 0.05f, w * 0.75f, h * 0.036f, w * 0.5f, h * 0.036f)
            cubicTo(w * 0.25f, h * 0.036f, w * 0.07f, h * 0.05f, 0f, h * 0.125f)
            close()
        }
        translate(0f, h * 0.035f) { drawPath(arco, t.s(0.3f)) }
        drawPath(arco, t.c(mezcla(CAOBA, CAOBA_LUZ, 0.2f)))
        val filete = Path().apply {
            moveTo(w, h * 0.125f)
            cubicTo(w * 0.93f, h * 0.05f, w * 0.75f, h * 0.036f, w * 0.5f, h * 0.036f)
            cubicTo(w * 0.25f, h * 0.036f, w * 0.07f, h * 0.05f, 0f, h * 0.125f)
        }
        drawPath(filete, t.s(0.4f), style = Stroke(1.2f * u))
        translate(0f, -1.4f * u) { drawPath(filete, t.c(BRONCE).copy(alpha = 0.7f), style = Stroke(0.8f * u)) }
    }

    /**
     * La gaveta de debajo de la casilla: el frente de caoba con su veta y su bisel, y el portaetiquetas de latón con la
     * tarjeta del tema (vacío en una casilla libre). Al abrir, sale hacia quien mira y se le ve la boca.
     */
    private fun DrawScope.gaveta(w: Float, h: Float, c: EstadoCajon, t: Tono, ab: Float, der: Boolean) {
        val u = density
        val y0 = h * GAVETA
        val sale = SALIDA * h * ab
        if (ab > 0f) {
            drawRect(t.c(mezcla(CAOBA_OSC, NOCHE, 0.3f)), Offset(w * 0.015f, y0), Size(w * 0.97f, sale + 1f))
            drawRect(t.c(PAPEL), Offset(w * 0.12f, y0 + sale * 0.35f), Size(w * 0.66f, sale * 0.65f + 1f))
            drawRect(t.s(0.25f), Offset(w * 0.015f, y0), Size(w * 0.97f, sale * 0.35f))
        }
        val r = Rect(0f, y0 + sale, w, h + sale)
        drawRect(t.c(mezcla(CAOBA, CAOBA_LUZ, 0.4f)), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, (if (t.o) NOCHE else CAOBA_OSC).copy(alpha = 0.32f), c.semilla, 0.6f, grosor = 0.5f, ondulacion = 0.5f)
            if (der) drawRect(t.s(0.12f), r.topLeft, Size(w * 0.3f, r.height)) else drawRect(t.s(0.14f), Offset(w * 0.72f, r.top), Size(w * 0.28f, r.height))
        }
        drawRect(t.b(0.5f), r.topLeft, Size(w, 1.2f * u))
        drawRect(t.b(0.28f), r.topLeft, Size(1.2f * u, r.height))
        drawRect(t.s(0.38f), Offset(0f, r.bottom - 1.4f * u), Size(w, 1.4f * u))
        drawRect(t.s(0.26f), Offset(w - 1.4f * u, r.top), Size(1.4f * u, r.height))
        if (ab == 0f) drawRect(t.s(0.3f), Offset(0f, y0), Size(w, 1.8f * u))
        // El portaetiquetas de latón, con sus orejas atornilladas.
        val pr = Rect(w * rotulo.left, h * rotulo.top + sale, w * rotulo.right, h * rotulo.bottom + sale)
        val marco = Rect(pr.left - 2.6f * u, pr.top - 2.4f * u, pr.right + 2.6f * u, pr.bottom + 2.4f * u)
        listOf(marco.left - 0.5f * u, marco.right + 0.5f * u).forEach { x ->
            drawCircle(t.s(0.3f), 3f * u, Offset(x + 1f * u, marco.center.y + 1.3f * u))
            drawCircle(t.c(BRONCE), 3f * u, Offset(x, marco.center.y))
        }
        chapa(marco, t)
        listOf(marco.left - 1f * u, marco.right + 1f * u).forEach { x -> drawCircle(t.c(BRONCE_OSC), 0.9f * u, Offset(x, marco.center.y)) }
        if (c.vacio) {
            drawRect(t.c(mezcla(CAOBA_OSC, CAOBA, 0.3f)), pr.topLeft, pr.size)
            drawRect(t.s(0.3f), pr.topLeft, Size(pr.width, 2f * u))
        } else {
            val tarjeta = if (t.o) mezcla(PAPEL_LEJOS, mezcla(PAPEL, LLAMA, 0.12f), ((t.k - 0.05f) / 0.75f).coerceIn(0f, 1f).pow(0.7f)) else t.c(PAPEL)
            drawRect(tarjeta, pr.topLeft, pr.size)
            drawRect(t.s(0.14f), pr.topLeft, Size(pr.width, 1.6f * u))
            drawRect(t.s(0.08f), pr.topLeft, Size(1.4f * u, pr.height))
        }
        destello(Offset(if (der) marco.right - 1.5f * u else marco.left + 1.5f * u, marco.top + 1.2f * u), 0.9f * u, t)
        filo(r, t, der)
    }

    /** Un despacho plegado y lacrado, visto por el cierre: el papel, las solapas del pliego y la oblea de lacre. */
    private fun DrawScope.pliego(r: Rect, papel: Color, t: Tono, lacre: Boolean, der: Boolean) {
        val u = density
        drawRect(t.s(0.24f), r.topLeft + Offset(1.6f * u, 1.8f * u), r.size)
        drawRect(t.c(papel), r.topLeft, r.size)
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
        if (der) drawRect(t.s(0.1f), r.topLeft, Size(r.width * 0.16f, r.height)) else drawRect(t.s(0.12f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.16f, r.height))
        drawRect(t.s(0.1f), Offset(r.left, r.bottom - r.height * 0.14f), Size(r.width, r.height * 0.14f))
        if (lacre) {
            val c = Offset(r.center.x - r.width * 0.06f, r.top + r.height * 0.52f)
            // Las solapas del pliego, que se cierran bajo el lacre.
            listOf(r.topLeft, Offset(r.right, r.top)).forEach { e ->
                drawLine(t.s(0.3f), e, c, 0.8f * u)
                drawLine(t.b(0.35f), e + Offset(0f, 1f * u), c + Offset(0f, 1f * u), 0.6f * u)
            }
            listOf(Offset(r.left, r.bottom), r.bottomRight).forEach { e -> drawLine(t.s(0.18f), e, c, 0.6f * u) }
            val rl = r.height * 0.13f
            drawCircle(t.s(0.3f), rl * 1.05f, c + Offset(1f * u, 1.4f * u))
            drawCircle(t.c(ROJO), rl, c)
            drawCircle(t.c(ROJO), rl * 0.5f, c + Offset(rl * 0.8f, rl * 0.45f))
            drawCircle(t.c(ROJO), rl * 0.4f, c + Offset(-rl * 0.75f, -rl * 0.5f))
            drawCircle(t.s(0.3f), rl * 0.62f, c, style = Stroke(0.8f * u))
            drawCircle(t.b(0.45f), rl * 0.22f, c - Offset(rl * 0.35f, rl * 0.4f))
        }
    }

    /**
     * El fajo de despachos lacrados, de pie y un poco vencido, atado en cruz con su cinta y su lazo: uno por ficha (hasta
     * siete), cada uno un poco más atrás y más alto. Según la semilla cambian el papel (blanco, azulado o agarbanzado) y la
     * cinta (azul, roja o verde).
     */
    private fun DrawScope.despachos(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(1, 7)
        val clase = Math.floorMod(m ushr 3, 3)
        val papel = when (clase) { 0 -> PAPEL; 1 -> mezcla(PAPEL, AZUL, 0.2f); else -> mezcla(PAPEL, BRONCE, 0.36f) }
        val cinta = when (clase) { 0 -> AZUL; 1 -> ROJO; else -> CUERO }
        // Con uno o dos despachos, el pliego es más grande: que la casilla no quede vacía.
        val pocos = if (n <= 2) 1.1f else 1f
        val lw = w * 0.54f * pocos; val lh = h * 0.44f * pocos
        val paso = Offset(-w * 0.042f, -h * 0.022f)
        val x0 = w * 0.5f - lw / 2 - paso.x * (n - 1) / 2
        val giro = (azar(m, 1) - 0.5f) * 5f
        apoyo(t, x0 + lw / 2 + paso.x * (n - 1) / 2, piso, lw * 1.1f - paso.x * (n - 1), h * 0.05f, der)
        rotate(giro, Offset(x0 + lw / 2, piso)) {
            for (i in n - 1 downTo 0) {
                val r = Rect(x0 + paso.x * i, piso - lh + paso.y * i, x0 + lw + paso.x * i, piso)
                val tono = when { i == 0 -> papel; i % 2 == 1 -> mezcla(papel, LONA_OSC, 0.3f); else -> mezcla(papel, LONA, 0.3f) }
                rotate((azar(m, 10 + i) - 0.5f) * (if (i == 0) 0f else 3f), Offset(r.center.x, piso)) {
                    pliego(r, tono, t, i == 0, der)
                    if (i > 0) {
                        // La cinta, que asoma en el canto de arriba de cada despacho.
                        drawRect(t.c(cinta), Offset(r.left + lw * 0.66f, r.top), Size(lw * 0.1f, -paso.y + 1f))
                        drawRect(t.s(0.2f), Offset(r.left + lw * 0.66f, r.top), Size(lw * 0.1f, 1f * u))
                    }
                    if (i == 0) filo(r, t, der)
                }
            }
            // La cinta en cruz sobre el despacho de delante, con su lazo.
            val f = Rect(x0, piso - lh, x0 + lw, piso)
            val vx = f.left + lw * 0.66f; val hy = f.top + lh * 0.3f
            val anc = lw * 0.1f
            drawRect(t.s(0.25f), Offset(vx + anc, f.top), Size(1.4f * u, lh))
            drawRect(t.c(cinta), Offset(vx, f.top), Size(anc, lh))
            drawRect(t.s(0.25f), Offset(f.left, hy + anc), Size(lw, 1.4f * u))
            drawRect(t.c(cinta), Offset(f.left, hy), Size(lw, anc))
            drawRect(t.b(0.3f), Offset(vx, f.top), Size(anc * 0.25f, lh))
            drawRect(t.b(0.3f), Offset(f.left, hy), Size(lw, anc * 0.25f))
            val nudo = Offset(vx + anc / 2, hy + anc / 2)
            listOf(-1f, 1f).forEach { s ->
                val lazo = Path().apply {
                    moveTo(nudo.x, nudo.y)
                    cubicTo(nudo.x + s * lw * 0.1f, nudo.y - lh * 0.3f, nudo.x + s * lw * 0.3f, nudo.y - lh * 0.2f, nudo.x + s * lw * 0.24f, nudo.y - lh * 0.02f)
                    cubicTo(nudo.x + s * lw * 0.2f, nudo.y + lh * 0.06f, nudo.x + s * lw * 0.08f, nudo.y + lh * 0.04f, nudo.x, nudo.y)
                    close()
                }
                translate(1.4f * u, 1.8f * u) { drawPath(lazo, t.s(0.28f)) }
                drawPath(lazo, t.c(if (s < 0) cinta else mezcla(cinta, NOCHE, 0.2f)))
                drawPath(lazo, t.b(0.3f), style = Stroke(0.7f * u))
                val cola = Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + s * lw * 0.05f, nudo.y + lh * 0.12f, nudo.x + s * lw * 0.14f, nudo.y + lh * 0.2f, nudo.x + s * lw * 0.12f, nudo.y + lh * 0.34f) }
                drawPath(cola, t.s(0.25f), style = Stroke(anc * 0.75f))
                drawPath(cola, t.c(mezcla(cinta, PAPEL, 0.12f)), style = Stroke(anc * 0.6f))
            }
            drawCircle(t.c(mezcla(cinta, NOCHE, 0.25f)), anc * 0.55f, nudo)
            drawCircle(t.b(0.35f), anc * 0.2f, nudo - Offset(anc * 0.15f, anc * 0.15f))
        }
    }

    /**
     * El portefeuille de tafilete (rojo, verde o leonado según la semilla), de pie: la solapa con su lengüeta y su cerradura
     * de latón, el doble filete dorado, una corona de laurel a hierros, el fuelle del costado (más abierto cuanto más guarda),
     * las puntas gastadas y los papeles que asoman por arriba.
     */
    private fun DrawScope.portefeuille(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(1, 8)
        val piel = when (Math.floorMod(m ushr 3, 3)) { 0 -> mezcla(CORREA, CAOBA, 0.25f); 1 -> CUERO; else -> mezcla(ROJO, CAOBA_OSC, 0.22f) }
        val pielO = mezcla(piel, NOCHE, 0.35f)
        val r = Rect(w * 0.15f, h * 0.16f, w * 0.8f, piso)
        val fuelle = w * (0.02f + 0.007f * n)
        val giro = (azar(m, 2) - 0.5f) * 4f
        apoyo(t, r.center.x + fuelle / 2, piso, (r.width + fuelle) * 1.08f, h * 0.05f, der)
        rotate(giro, Offset(r.center.x, piso)) {
            drawRect(t.s(0.28f), Offset(r.left + w * 0.03f, r.top + h * 0.03f), Size(r.width + fuelle, r.height - h * 0.03f))
            // La tapa de atrás y los papeles que asoman (más y más altos cuanto más guarda).
            drawRect(t.c(pielO), Offset(r.left + fuelle, r.top - h * 0.012f), Size(r.width, r.height + h * 0.012f))
            val cuantos = (1 + n / 2).coerceIn(1, 5)
            for (i in 0 until cuantos) {
                val pw = r.width * (0.3f + 0.2f * azar(m, 20 + i))
                val px = r.left + fuelle * 0.6f + (r.width - pw) * ((i + 0.5f) / cuantos * 0.9f + 0.08f * azar(m, 30 + i))
                val py = r.top - h * (0.04f + 0.01f * n) * (0.5f + 0.5f * azar(m, 40 + i))
                rotate((azar(m, 50 + i) - 0.5f) * 5f, Offset(px + pw / 2, r.top)) {
                    drawRect(t.s(0.2f), Offset(px + 1.4f * u, py + 1.4f * u), Size(pw, r.top - py + 2f * u))
                    drawRect(t.c(if (i % 2 == 0) PAPEL else mezcla(PAPEL, AZUL, 0.16f)), Offset(px, py), Size(pw, r.top - py + 3f * u))
                    drawRect(t.b(0.45f), Offset(px, py), Size(pw, 0.9f * u))
                }
            }
            // El fuelle del costado, con sus pliegues.
            val fu = Rect(r.right, r.top + h * 0.02f, r.right + fuelle, piso)
            drawRect(t.c(pielO), fu.topLeft, fu.size)
            var fx = fu.left + 2f * u
            while (fx < fu.right) { drawRect(t.b(0.16f), Offset(fx, fu.top), Size(1f * u, fu.height)); fx += w * 0.012f }
            // El frente.
            drawRect(t.c(piel), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                pinceladas(r, if (t.o) NOCHE else pielO, m, 8, u, 0.2f)
                if (der) { drawRect(t.b(0.14f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height)); drawRect(t.s(0.18f), r.topLeft, Size(r.width * 0.16f, r.height)) }
                else { drawRect(t.b(0.14f), r.topLeft, Size(r.width * 0.2f, r.height)); drawRect(t.s(0.2f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.16f, r.height)) }
                // Las puntas gastadas.
                listOf(r.left, r.right).forEach { x -> drawOval(t.c(mezcla(piel, LONA, 0.4f)).copy(alpha = 0.55f), Offset(x - w * 0.03f, piso - h * 0.035f), Size(w * 0.06f, h * 0.05f)) }
            }
            val oro = t.c(mezcla(BRONCE, LUZ_MUEBLE, 0.1f)).copy(alpha = 0.85f)
            // El cuerpo, bajo la solapa: el filete y la corona de laurel a hierros dorados.
            val bajo = Rect(r.left + w * 0.03f, r.top + r.height * 0.55f, r.right - w * 0.03f, r.bottom - h * 0.03f)
            drawRect(oro, bajo.topLeft, bajo.size, style = Stroke(0.8f * u))
            drawRect(oro.copy(alpha = 0.5f), bajo.topLeft + Offset(2.2f * u, 2.2f * u), Size(bajo.width - 4.4f * u, bajo.height - 4.4f * u), style = Stroke(0.5f * u))
            listOf(bajo.topLeft, Offset(bajo.right, bajo.top), Offset(bajo.left, bajo.bottom), bajo.bottomRight).forEach { q -> drawPath(estrella(q, 2.2f * u, 4, 0.35f), oro) }
            // La solapa, con su borde, su filete y la lengüeta del cierre.
            val sol = r.top + r.height * 0.46f
            val solapa = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, sol)
                lineTo(r.center.x + r.width * 0.1f, sol)
                cubicTo(r.center.x + r.width * 0.09f, sol + r.height * 0.14f, r.center.x - r.width * 0.09f, sol + r.height * 0.14f, r.center.x - r.width * 0.1f, sol)
                lineTo(r.left, sol); close()
            }
            translate(0f, 2.2f * u) { drawPath(solapa, t.s(0.32f)) }
            drawPath(solapa, t.c(mezcla(piel, LUZ_MUEBLE, 0.07f)))
            clipPath(solapa) {
                if (der) drawRect(t.s(0.16f), r.topLeft, Size(r.width * 0.16f, r.height)) else { drawRect(t.b(0.16f), r.topLeft, Size(r.width * 0.2f, r.height)); drawRect(t.s(0.18f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.16f, r.height)) }
                drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1.1f * u))
            }
            val fs = Rect(r.left + w * 0.03f, r.top + h * 0.03f, r.right - w * 0.03f, sol - h * 0.028f)
            drawRect(oro, fs.topLeft, fs.size, style = Stroke(0.8f * u))
            drawRect(oro.copy(alpha = 0.5f), fs.topLeft + Offset(2.2f * u, 2.2f * u), Size(fs.width - 4.4f * u, fs.height - 4.4f * u), style = Stroke(0.5f * u))
            listOf(fs.topLeft, Offset(fs.right, fs.top), Offset(fs.left, fs.bottom), fs.bottomRight).forEach { q -> drawPath(estrella(q, 2.4f * u, 4, 0.35f), oro) }
            // La corona de laurel a hierros dorados, al centro de la solapa.
            laurel(Offset(r.center.x, fs.top + fs.height * 0.42f), fs.height * 0.26f, oro.copy(alpha = 0.75f))
            // La cerradura de latón en la lengüeta.
            val ce = Rect(r.center.x - r.width * 0.055f, sol - r.height * 0.07f, r.center.x + r.width * 0.055f, sol + r.height * 0.075f)
            drawRoundRect(t.s(0.3f), ce.topLeft + Offset(1.2f * u, 1.6f * u), ce.size, CornerRadius(2f * u))
            drawRoundRect(t.c(BRONCE), ce.topLeft, ce.size, CornerRadius(2f * u))
            drawRect(t.b(0.5f), Offset(ce.left + 1f * u, ce.top), Size(ce.width - 2f * u, 0.9f * u))
            drawCircle(t.s(0.6f), 1.3f * u, Offset(ce.center.x, ce.center.y - 1f * u))
            drawRect(t.s(0.6f), Offset(ce.center.x - 0.6f * u, ce.center.y - 1f * u), Size(1.2f * u, 3.4f * u))
            destello(Offset(if (der) ce.right - 1.5f * u else ce.left + 1.5f * u, ce.top + 1.5f * u), 0.9f * u, t)
            filo(r, t, der)
        }
    }

    /** Un rollo de mapa de pie: el papel enrollado con su boca en espiral arriba y la cinta que lo ata. */
    private fun DrawScope.rollo(r: Rect, t: Tono, cinta: Color, der: Boolean, k: Int) {
        val u = density
        val papel = if (k % 2 == 0) mezcla(PAPEL, LONA, 0.3f) else mezcla(PAPEL, BRONCE, 0.25f)
        drawRect(t.s(0.25f), r.topLeft + Offset(1.6f * u, 1.6f * u), r.size)
        drawRect(t.c(papel), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.1f, der) }
        // El borde de la hoja, a lo largo.
        drawLine(t.s(0.25f), Offset(r.left + r.width * 0.64f, r.top), Offset(r.left + r.width * 0.6f, r.bottom), 0.7f * u)
        // La boca: el óvalo y la espiral del papel.
        val boca = Rect(r.left, r.top - r.width * 0.2f, r.right, r.top + r.width * 0.2f)
        drawOval(t.c(mezcla(papel, PAPEL, 0.5f)), boca.topLeft, boca.size)
        drawOval(t.c(mezcla(CAOBA_OSC, NOCHE, 0.3f)), Offset(boca.left + boca.width * 0.2f, boca.top + boca.height * 0.24f), Size(boca.width * 0.6f, boca.height * 0.52f))
        drawArc(t.c(papel), 20f, 250f, false, Offset(boca.left + boca.width * 0.3f, boca.top + boca.height * 0.32f), Size(boca.width * 0.4f, boca.height * 0.36f), style = Stroke(0.9f * u))
        drawOval(t.s(0.25f), boca.topLeft, boca.size, style = Stroke(0.6f * u))
        // La cinta.
        val cy = r.top + r.height * (0.42f + 0.1f * (k % 2))
        drawRect(t.c(cinta), Offset(r.left - 0.5f * u, cy), Size(r.width + 1f * u, r.width * 0.2f))
        drawRect(t.s(0.25f), Offset(r.left, cy + r.width * 0.2f), Size(r.width, 1f * u))
        if (der) drawRect(t.s(0.2f), Offset(r.left, cy), Size(r.width * 0.3f, r.width * 0.2f)) else drawRect(t.s(0.25f), Offset(r.left + r.width * 0.65f, cy), Size(r.width * 0.35f, r.width * 0.2f))
    }

    /**
     * El tubo de mapas de hojalata pintada (verde botella, azul gris u ocre según la semilla), apoyado en diagonal en la
     * casilla: los aros de hojalata, la tapa con su anilla, la correa, la etiqueta pegada y los golpes; detrás, de pie, los
     * rollos de mapas atados con cinta (más cuanto más guarda).
     */
    private fun DrawScope.tubo(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(1, 8)
        val clase = Math.floorMod(m ushr 3, 3)
        val laca = when (clase) { 0 -> mezcla(PANO, HOJALATA, 0.3f); 1 -> mezcla(BRONCE, CORREA, 0.42f); else -> mezcla(CUERO, FIELTRO, 0.25f) }
        val cinta = when (clase) { 0 -> ROJO; 1 -> AZUL; else -> ROJO }
        // Los rollos, de pie contra el fondo, a la izquierda.
        val cuantos = (1 + n / 2).coerceIn(1, 4)
        for (i in cuantos - 1 downTo 0) {
            val rw = w * 0.1f
            val rx = w * (0.07f + 0.105f * i)
            val alto = h * (0.4f + 0.12f * azar(m, 60 + i))
            val rr = Rect(rx, piso - h * 0.02f - alto, rx + rw, piso - h * 0.02f)
            apoyo(t, rr.center.x, rr.bottom, rw * 1.5f, h * 0.035f, der, 0.35f)
            rotate((azar(m, 70 + i) - 0.4f) * 7f, Offset(rr.center.x, rr.bottom)) { rollo(rr, t, if (i % 2 == 0) cinta else AZUL, der, i) }
        }
        // El tubo: se dibuja tumbado y se levanta por la derecha.
        val largo = w * 0.74f; val dia = h * 0.2f
        val p0 = Offset(w * 0.2f, piso - dia * 0.56f)
        apoyo(t, w * 0.5f, piso, w * 0.74f, h * 0.05f, der, 0.36f)
        rotate(-24f, p0) {
            val r = Rect(p0.x, p0.y - dia / 2, p0.x + largo, p0.y + dia / 2)
            // La correa, que cuelga floja por debajo.
            val correa = Path().apply { moveTo(r.left + largo * 0.16f, r.bottom - 1f * u); cubicTo(r.left + largo * 0.3f, r.bottom + dia * 0.55f, r.left + largo * 0.62f, r.bottom + dia * 0.5f, r.left + largo * 0.78f, r.bottom - 1f * u) }
            drawPath(correa, t.s(0.3f), style = Stroke(3.4f * u))
            drawPath(correa, t.c(CORREA), style = Stroke(2.4f * u))
            drawRoundRect(t.s(0.3f), r.topLeft + Offset(w * 0.012f, h * 0.03f), r.size, CornerRadius(dia * 0.16f))
            drawRoundRect(t.c(laca), r.topLeft, r.size, CornerRadius(dia * 0.16f))
            clipRect(r.left, r.top, r.right, r.bottom) {
                tumbado(r, t, 1.4f)
                // Los golpes, donde asoma la hojalata.
                for (j in 0 until 4) drawOval(t.c(HOJALATA), Offset(r.left + largo * (0.1f + 0.75f * azar(m, 80 + j)), r.top + dia * (0.15f + 0.6f * azar(m, 84 + j))), Size(w * (0.014f + 0.016f * azar(m, 88 + j)), dia * 0.07f))
            }
            // Los aros de hojalata y el filete pintado.
            listOf(0.02f, 0.2f, 0.74f).forEach { f ->
                val a = Rect(r.left + largo * f, r.top - 0.8f * u, r.left + largo * f + w * 0.022f, r.bottom + 0.8f * u)
                drawRect(t.c(HOJALATA), a.topLeft, a.size)
                clipRect(a.left, a.top, a.right, a.bottom) { tumbado(a, t, 1.6f) }
                drawRect(t.s(0.25f), Offset(a.right, r.top), Size(1f * u, dia))
            }
            // La etiqueta pegada, con dos renglones a pluma.
            val et = Rect(r.left + largo * 0.36f, r.top + dia * 0.2f, r.left + largo * 0.6f, r.top + dia * 0.72f)
            drawRect(t.c(mezcla(PAPEL, LONA, 0.3f)), et.topLeft, et.size)
            drawRect(t.s(0.12f), Offset(et.left, et.top + et.height * 0.6f), Size(et.width, et.height * 0.4f))
            listOf(0.36f, 0.66f).forEachIndexed { j, fy -> drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(et.left + et.width * 0.12f, et.top + et.height * fy), Offset(et.left + et.width * (0.86f - 0.3f * j), et.top + et.height * fy), 0.6f * u) }
            // La tapa, un poco más ancha, con su reborde y su anilla.
            val tapa = Rect(r.right - largo * 0.13f, r.top - dia * 0.05f, r.right + w * 0.01f, r.bottom + dia * 0.05f)
            drawRoundRect(t.c(mezcla(laca, NOCHE, 0.18f)), tapa.topLeft, tapa.size, CornerRadius(dia * 0.14f))
            clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) { tumbado(tapa, t, 1.5f) }
            drawRect(t.c(HOJALATA), tapa.topLeft, Size(w * 0.014f, tapa.height))
            drawRect(t.s(0.3f), Offset(tapa.left - 1f * u, tapa.top), Size(1f * u, tapa.height))
            val an = Offset(tapa.right + w * 0.012f, tapa.center.y)
            drawCircle(t.s(0.3f), w * 0.016f, an + Offset(1f * u, 1.4f * u), style = Stroke(1.6f * u))
            drawCircle(t.c(HOJALATA), w * 0.016f, an, style = Stroke(1.8f * u))
            destello(Offset(r.left + largo * 0.3f, r.top + dia * 0.18f), 1f * u, t)
            if (t.o && t.k > 0.06f) drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(r.left + dia * 0.16f, r.top), Size(largo - dia * 0.3f, 1.4f * u))
        }
    }

    /**
     * Lo que no tiene tema: una sombrerera de cartón forrado de papel gris azulado, redonda, con la tapa levantada por un
     * lado, el cordel suelto y los papeles sin lacrar que asoman revueltos por la rendija (más cuanto más guarda).
     */
    private fun DrawScope.sombrerera(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(1, 8)
        val carton = mezcla(HOJALATA, PAPEL, 0.4f)
        val cartonO = mezcla(carton, PANO, 0.35f)
        val r = Rect(w * 0.19f, h * 0.31f, w * 0.79f, piso)
        val comba = h * 0.035f
        apoyo(t, r.center.x, piso, r.width * 1.12f, h * 0.06f, der)
        /** Un rombo menudo del papel de forro. */
        fun rombo(q: Offset) = drawPath(estrella(q, 2f * u, 2, 0.5f, 0f), t.c(cartonO).copy(alpha = 0.55f))
        // El cuerpo redondo.
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - comba)
            cubicTo(r.right - r.width * 0.2f, r.bottom + comba * 0.4f, r.left + r.width * 0.2f, r.bottom + comba * 0.4f, r.left, r.bottom - comba)
            close()
        }
        drawPath(cuerpo, t.c(carton))
        clipPath(cuerpo) {
            for (j in 0 until 4) for (i in 0 until 9) rombo(Offset(r.left + r.width * (i + 0.5f + 0.5f * (j % 2)) / 9.5f, r.top + r.height * (0.24f + 0.2f * j)))
            cilindro(r, t, 1.3f, der)
            drawRect(t.c(cartonO).copy(alpha = 0.6f), Offset(r.left, r.bottom - h * 0.05f), Size(r.width, h * 0.05f))
            // Una esquina del papel, despegada y rota: asoma el cartón.
            drawPath(poligono(r.left + r.width * 0.62f, r.bottom, r.left + r.width * 0.7f, r.bottom - h * 0.07f, r.left + r.width * 0.8f, r.bottom - h * 0.04f, r.left + r.width * 0.84f, r.bottom), t.c(mezcla(CORREA, LONA, 0.55f)))
        }
        // La boca, oscura, y los papeles sin lacrar que asoman por donde la tapa se levanta.
        val boca = Rect(r.left, r.top - comba, r.right, r.top + comba)
        drawOval(t.c(mezcla(cartonO, NOCHE, 0.55f)), boca.topLeft, boca.size)
        val cuantos = (2 + n / 2).coerceIn(2, 5)
        for (i in 0 until cuantos) {
            val pw = r.width * (0.3f + 0.12f * azar(77, i))
            val px = r.left + r.width * 0.03f + (r.width * 0.5f - pw * 0.5f) * (i / maxOf(1f, cuantos - 1f))
            val alto = h * (0.12f + 0.012f * n) * (0.65f + 0.35f * azar(78, i))
            rotate(-14f + 20f * azar(79, i), Offset(px + pw / 2, r.top)) {
                drawRect(t.s(0.22f), Offset(px + 1.5f * u, r.top - alto + 1.5f * u), Size(pw, alto))
                drawRect(t.c(if (i % 2 == 0) PAPEL else mezcla(PAPEL, LONA, 0.35f)), Offset(px, r.top - alto), Size(pw, alto + comba * 0.6f))
                drawRect(t.b(0.45f), Offset(px, r.top - alto), Size(pw, 0.9f * u))
                for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(px + pw * 0.12f, r.top - alto * (0.8f - 0.22f * j)), Offset(px + pw * (0.82f - 0.15f * j), r.top - alto * (0.8f - 0.22f * j)), 0.6f * u)
            }
        }
        // El borde de delante de la boca.
        drawArc(t.c(carton), 0f, 180f, true, boca.topLeft, boca.size)
        drawArc(t.b(0.45f), 20f, 140f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
        // La tapa: apoyada en el borde de la derecha y levantada por la izquierda; se le ve lo de arriba.
        val ta = Rect(r.left - w * 0.022f, r.top - h * 0.1f, r.right + w * 0.022f, r.top - h * 0.012f)
        rotate(-7.5f, Offset(ta.right, ta.bottom)) {
            val aro = Path().apply {
                moveTo(ta.left, ta.top); lineTo(ta.right, ta.top); lineTo(ta.right, ta.bottom - comba)
                cubicTo(ta.right - ta.width * 0.2f, ta.bottom + comba * 0.4f, ta.left + ta.width * 0.2f, ta.bottom + comba * 0.4f, ta.left, ta.bottom - comba)
                close()
            }
            translate(w * 0.012f, h * 0.03f) { drawPath(aro, t.s(0.3f)) }
            drawPath(aro, t.c(mezcla(carton, LUZ_MUEBLE, 0.1f)))
            clipPath(aro) {
                for (i in 0 until 10) rombo(Offset(ta.left + ta.width * (i + 0.5f) / 10f, ta.top + ta.height * 0.6f))
                cilindro(ta, t, 1.3f, der)
                drawRect(t.c(cartonO).copy(alpha = 0.7f), Offset(ta.left, ta.bottom - h * 0.03f), Size(ta.width, h * 0.03f))
            }
            val cima = Rect(ta.left, ta.top - comba, ta.right, ta.top + comba)
            drawOval(t.c(mezcla(carton, LUZ_MUEBLE, 0.25f)), cima.topLeft, cima.size)
            drawOval(t.c(cartonO).copy(alpha = 0.5f), Offset(cima.left + cima.width * 0.06f, cima.top + cima.height * 0.14f), Size(cima.width * 0.88f, cima.height * 0.72f), style = Stroke(0.8f * u))
            drawArc(t.b(0.5f), 180f, 180f, false, cima.topLeft, cima.size, style = Stroke(1f * u))
            drawArc(t.s(0.25f), 0f, 180f, false, cima.topLeft, cima.size, style = Stroke(0.8f * u))
            if (t.o && t.k > 0.06f) drawArc(LLAMA.copy(alpha = (0.7f * t.k).coerceAtMost(0.6f)), 180f, 180f, false, cima.topLeft, cima.size, style = Stroke(1.2f * u))
        }
        // El cordel, suelto, que cae por delante.
        val cordel = Path().apply {
            moveTo(r.left + r.width * 0.34f, r.top + comba * 0.8f)
            cubicTo(r.left + r.width * 0.26f, r.top + r.height * 0.4f, r.left + r.width * 0.44f, r.top + r.height * 0.6f, r.left + r.width * 0.34f, r.bottom - h * 0.01f)
            cubicTo(r.left + r.width * 0.3f, r.bottom + h * 0.02f, r.left + r.width * 0.1f, r.bottom, r.left - w * 0.05f, r.bottom + h * 0.005f)
        }
        translate(1.4f * u, 1.6f * u) { drawPath(cordel, t.s(0.3f), style = Stroke(1.3f * u)) }
        drawPath(cordel, t.c(mezcla(CORREA, LONA, 0.5f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
    }

    /** Una casilla libre: unas barras de lacre, un cabo de vela en su platillo de latón y un carrete de cinta azul. */
    private fun DrawScope.libre(w: Float, h: Float, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val espejo = Math.floorMod(m, 2) == 0
        fun x(f: Float) = w * (if (espejo) 1f - f else f)
        scale(1.22f, Offset(w / 2, piso)) { libreCosas(w, h, piso, espejo, t, der) { f -> x(f) } }
    }

    /** Las cosas de una casilla libre, a su tamaño de dibujo (la casilla las agranda). */
    private fun DrawScope.libreCosas(w: Float, h: Float, piso: Float, espejo: Boolean, t: Tono, der: Boolean, x: (Float) -> Float) {
        val u = density
        // El cabo de vela en su platillo.
        val vx = x(0.28f)
        val plato = Rect(vx - w * 0.12f, piso - h * 0.05f, vx + w * 0.12f, piso + h * 0.012f)
        apoyo(t, vx, piso, w * 0.3f, h * 0.05f, der)
        drawOval(t.c(BRONCE_OSC), plato.topLeft + Offset(0f, h * 0.012f), plato.size)
        drawOval(t.c(BRONCE), plato.topLeft, plato.size)
        drawOval(t.c(mezcla(BRONCE, BRONCE_OSC, 0.5f)), Offset(plato.left + w * 0.02f, plato.top + h * 0.012f), Size(plato.width - w * 0.04f, plato.height - h * 0.03f))
        drawArc(t.b(0.6f), 180f, 100f, false, plato.topLeft, plato.size, style = Stroke(1f * u))
        val sebo = mezcla(PAPEL, BRONCE, 0.14f)
        val cabo = Rect(vx - w * 0.035f, piso - h * 0.2f, vx + w * 0.035f, piso - h * 0.02f)
        drawRect(t.c(sebo), cabo.topLeft, cabo.size)
        clipRect(cabo.left, cabo.top, cabo.right, cabo.bottom) { cilindro(cabo, t, 1f, der) }
        drawOval(t.c(mezcla(sebo, BRONCE_OSC, 0.25f)), Offset(cabo.left, cabo.top - h * 0.012f), Size(cabo.width, h * 0.024f))
        drawRoundRect(t.c(mezcla(sebo, PAPEL, 0.5f)), Offset(cabo.left - 1f * u, cabo.top), Size(w * 0.022f, h * 0.1f), CornerRadius(w * 0.011f))
        drawOval(t.c(mezcla(sebo, PAPEL, 0.4f)), Offset(cabo.left - w * 0.03f, piso - h * 0.04f), Size(w * 0.09f, h * 0.03f))
        drawLine(t.c(FIELTRO), Offset(vx, cabo.top), Offset(vx + w * 0.004f, cabo.top - h * 0.035f), 1.2f * u, StrokeCap.Round)
        destello(Offset(plato.left + w * 0.03f, plato.top + h * 0.012f), 0.9f * u, t)
        // Las barras de lacre: una tumbada y otra, gastada, apoyada encima.
        val lx = x(0.6f)
        val barra = Rect(lx - w * 0.17f, piso - h * 0.055f, lx + w * 0.17f, piso)
        apoyo(t, lx, piso, w * 0.4f, h * 0.04f, der, 0.36f)
        drawRect(t.c(ROJO), barra.topLeft, barra.size)
        clipRect(barra.left, barra.top, barra.right, barra.bottom) { tumbado(barra, t, 1.4f) }
        drawRect(t.s(0.25f), Offset(barra.left + barra.width * 0.48f, barra.top), Size(1f * u, barra.height))
        rotate(if (espejo) 16f else -16f, Offset(lx, piso - h * 0.055f)) {
            val b2 = Rect(lx - w * 0.13f, piso - h * 0.105f, lx + w * 0.11f, piso - h * 0.055f)
            drawRect(t.s(0.3f), b2.topLeft + Offset(1.5f * u, 2f * u), b2.size)
            drawRect(t.c(mezcla(ROJO, CAOBA_OSC, 0.15f)), b2.topLeft, b2.size)
            clipRect(b2.left, b2.top, b2.right, b2.bottom) { tumbado(b2, t, 1.4f) }
            drawRect(t.c(mezcla(ROJO, FIELTRO, 0.65f)), Offset(if (espejo) b2.left else b2.right - w * 0.03f, b2.top), Size(w * 0.03f, b2.height))
        }
        // El carrete de cinta, al fondo.
        val kx = x(0.8f)
        val car = Rect(kx - w * 0.055f, piso - h * 0.26f, kx + w * 0.055f, piso - h * 0.06f)
        drawRect(t.c(mezcla(AZUL, NOCHE, 0.15f)), Offset(car.left + w * 0.01f, car.top + h * 0.02f), Size(car.width - w * 0.02f, car.height - h * 0.04f))
        clipRect(car.left, car.top, car.right, car.bottom) { cilindro(Rect(car.left + w * 0.01f, car.top, car.right - w * 0.01f, car.bottom), t, 1.2f, der) }
        listOf(car.top, car.bottom - h * 0.025f).forEach { y ->
            drawRoundRect(t.c(mezcla(CORREA, LONA, 0.3f)), Offset(car.left, y), Size(car.width, h * 0.025f), CornerRadius(1.5f * u))
            drawRect(t.b(0.4f), Offset(car.left, y), Size(car.width, 0.9f * u))
        }
    }

    override val rotulo = Rect(0.06f, 0.715f, 0.94f, 0.972f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.bodoniTitulo
    override val salida = SALIDA
    override val placa = Rect(0.62f, 0.74f, 0.91f, 0.92f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Cuero verde con hierros dorados: doble filete, florones en las esquinas y una corona de laurel al centro.
        drawRect(t(CUERO, oscuro))
        for (k in 0 until 30) mancha(Offset(w * azar(91, k), h * azar(92, k)), (20f + 30f * azar(93, k)) * u, CUERO_OSC.copy(alpha = 0.18f))
        val m = 30 * u
        drawRect(t(BRONCE, oscuro).copy(alpha = 0.7f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.4f * u))
        drawRect(t(BRONCE, oscuro).copy(alpha = 0.5f), Offset(m + 6 * u, m + 6 * u), Size(w - 2 * m - 12 * u, h - 2 * m - 12 * u), style = Stroke(0.8f * u))
        listOf(Offset(m, m), Offset(w - m, m), Offset(m, h - m), Offset(w - m, h - m)).forEach { drawPath(estrella(it, 9 * u, 8, 0.4f), t(BRONCE, oscuro).copy(alpha = 0.7f)) }
        laurel(Offset(w / 2, h * 0.45f), w * 0.22f, BRONCE.copy(alpha = 0.12f))
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CAOBA, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El doble filete azul de la cancillería, los dobleces en tercios y la abeja tenue abajo.
        val m = 4 * u * g
        drawRect(AZUL.copy(alpha = 0.5f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1f * u * g))
        drawRect(AZUL.copy(alpha = 0.35f), Offset(m + 2.4f * u * g, m + 2.4f * u * g), Size(w - 2 * m - 4.8f * u * g, h - 2 * m - 4.8f * u * g), style = Stroke(0.5f * u))
        listOf(w / 3, w * 2 / 3).forEach { x -> drawLine(Color.Black.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, h), 1f * u) }
        abeja(Offset(w / 2, h - 12 * u * g), 12 * u * g, AZUL.copy(alpha = 0.22f))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 32.dp)
    override val proporcionFicha = 0.82f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La N imperial en su sello.
            "hecho" -> {
                drawCircle(AZUL, s * 0.42f, c)
                drawPath(Path().apply { moveTo(c.x - s * 0.17f, c.y + s * 0.22f); lineTo(c.x - s * 0.17f, c.y - s * 0.22f); lineTo(c.x + s * 0.17f, c.y + s * 0.22f); lineTo(c.x + s * 0.17f, c.y - s * 0.22f) },
                    p.color, style = Stroke(s * 0.09f, join = androidx.compose.ui.graphics.StrokeJoin.Miter))
            }
            "convence" -> laurel(c, s * 0.36f, BRONCE_OSC)
            "piensa" -> abeja(c, s * 0.9f, AZUL)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** La estrella de la Legión de Honor, de esmalte blanco, colgada de su cinta roja. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(ROJO, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.26f))
        val e = Offset(c.x, c.y + s * 0.12f)
        drawPath(estrella(e, s * 0.38f, 5, 0.45f), BRONCE)
        drawPath(estrella(e, s * 0.3f, 5, 0.45f), Color.White)
        drawCircle(BRONCE, s * 0.08f, e)
    }

    override val desorden = 1f
}
