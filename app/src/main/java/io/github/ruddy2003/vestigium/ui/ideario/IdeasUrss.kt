package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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
//  Soviético
// =====================================================================

/**
 * URSS: la kartoteka de acero en un rincón de la biblioteca del club de la fábrica, hacia 1970. Es un interior sin ventana:
 * la pared pintada a dos tonos, cal amarillenta arriba y óleo azul apagado abajo con su raya de remate, los brochazos, unos
 * chorreones, dos desconchones y un enchufe de baquelita; la luz de día, fría y alta, entra de arriba a la izquierda, fuera
 * del cuadro. El mueble es un fichero de chapa esmaltada gris verdosa, con los cantos gastados hasta el metal, tornillos en
 * los montantes, una abolladura, su chapita de inventario, una tabla extraíble abajo (su portaetiquetas lleva la placa de
 * «y N más») y patas de tubo con su travesaño y sus conteras de goma. Encima, el punto focal, a la izquierda: la lámpara de
 * mesa de pantalla de vidrio verde, con la base y la columna de baquelita negra, la horquilla de latón, la cadenita y el
 * cordón de tela que baja por el costado hasta el enchufe; a su lado, el vaso de té en su podstakánnik calado con la
 * cucharilla, la almohadilla violeta con el sello de goma encima y unas fichas recién selladas, la pila de libros de tela con
 * su tejuelo y, apoyado en la pared, el ábaco de madera (schioty). En la pared, un banderín rojo de emulación con su fleco y
 * su estrella, un cartel constructivista de formas puras (cuña roja, círculo negro, diagonales y bloques que sugieren letras)
 * cortado por el borde de arriba, y un calendario de taco. Al pie, sobre el parquet en espiga, un paquete de fichas nuevas
 * envuelto en papel de estraza, cortado por el borde izquierdo, y la papelera de alambre con sus papeles arrugados, cortada
 * por el derecho. Cada tema es un cajón del fichero, con su portaetiquetas de latón y su tarjeta: según la semilla, el cajón
 * normal con su tirador de concha y el botón de la varilla; el cajón entreabierto, con las fichas, sus pestañas de colores y
 * una ficha a medio sacar; o un cajón de otra partida (esmalte marfil, verde botella o minio, tirador de puente niquelado o
 * pomo de latón o de baquelita, el portaetiquetas torcido y su número a plantilla). El esmalte de los otros dos varía entre
 * gris verdoso, azul grisáceo y arena. Cuantas más fichas guarda, más abierto queda y más fichas asoman. Lo que no tiene
 * tema lleva una carpeta de cartón con cintas atravesada sobre el cajón; un lugar libre es un cajón sacado y sin fichas, con el
 * portaetiquetas vacío y la varilla suelta. Al abrir uno, el frente baja. De noche todo se hunde en azul verdoso: alumbra solo
 * la lámpara (la pantalla encendida por dentro, el arco cálido que deja en la pared por debajo de la pantalla, el resplandor
 * verde por encima, el charco en la tapa y el filo en latones y tiradores); los cajones de arriba a la izquierda, encendidos, y
 * los de abajo a la derecha, casi perdidos. Por dentro, el cajón de acero con la varilla al centro. Las fichas son fichas de
 * catálogo con el agujero para la varilla abajo.
 */
object IdeasUrss : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del rincón
    /** La cal amarillenta de arriba y su sombra; el óleo azul apagado de abajo y su raya. */
    private val CAL = Color(0xFFE6DEC6)
    private val CAL_OSC = Color(0xFFC6BB9A)
    private val OLEO = Color(0xFF6C8A92)
    private val OLEO_OSC = Color(0xFF3D5864)
    /** El acero esmaltado gris verdoso, su luz y su sombra; el metal que asoma donde se gastó. */
    private val ACERO = Color(0xFF7A8478)
    private val ACERO_LUZ = Color(0xFF9AA498)
    private val ACERO_OSC = Color(0xFF4E5650)
    private val METAL = Color(0xFFBFC2BA)
    /** El latón de portaetiquetas y tiradores. */
    private val LATON = Color(0xFFB89A50)
    private val LATON_OSC = Color(0xFF7A6430)
    /** El rojo del cartel, del banderín y de las pestañas; el granate de la tela de un libro; el negro de la baquelita. */
    private val ROJO = Color(0xFFC1121F)
    private val GRANATE = Color(0xFF7C1A1E)
    private val NEGRO = Color(0xFF161414)
    /** La cartulina de las fichas, el cartón de las carpetas y el papel de estraza. */
    private val CARTULINA = Color(0xFFEFE6D2)
    private val CARTULINA_OSC = Color(0xFFD8CCB0)
    private val CARTON = Color(0xFFB09670)
    /** El parquet y la madera del ábaco. */
    private val PARQUET = Color(0xFF8A6438)
    private val PARQUET_OSC = Color(0xFF5E4024)
    /** El vidrio verde de la pantalla, y encendido por dentro. */
    private val VERDE = Color(0xFF2E5E3A)
    private val VERDE_LUZ = Color(0xFF86DCA4)
    /** La tinta violeta de la almohadilla y del lápiz de copiar; el té. */
    private val VIOLETA = Color(0xFF5A4690)
    private val TE = Color(0xFFB5652A)
    /** La bombilla y la noche del rincón: azul verdoso hondo. */
    private val LLAMA = Color(0xFFFFD08A)
    private val NOCHE = Color(0xFF0A191D)
    private val TINTA = Color(0xFF161414)
    /** La tarjeta del rótulo de noche, lejos de la lámpara: con [TINTA] da unos 5:1. */
    private val PAPEL_LEJOS = Color(0xFF8A8778)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.7f
    override val corona = 0.64f
    override val base = 0.27f
    override val lado = 0.07f
    override val hueco = 0.03f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la tapa, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.84f
    private const val FRENTE = 0.905f
    private const val CANTO = 0.955f
    /** La raya de remate de la pared, en fracción del ancho. */
    private const val RAYA = 0.4f
    /** Los costados del fichero, en fracción del ancho. */
    private const val IZQ = 0.035f
    private const val DER = 0.965f
    /** En la base: la tabla extraíble, lo bajo del cuerpo, el rincón de la pared con el piso, donde pisan las patas y las cosas. */
    private const val TABLA0 = 0.03f
    private const val TABLA1 = 0.29f
    private const val FONDO = 0.33f
    private const val SUELO = 0.44f
    private const val PISAN = 0.67f
    private const val PIE = 0.95f
    /** Cuánto baja el frente del cajón al abrirlo, en fracción de su alto. */
    private const val SALIDA = 0.14f

    // ---------------------------------------------------------------- la luz y la noche

    /** La bombilla, bajo la pantalla, en fracciones del ancho. */
    private val LUZ = Offset(0.27f, 0.318f)

    /**
     * El borde de la sombra de la pantalla en la pared: una hipérbola con el vértice bajo la pantalla, que baja hacia los
     * lados. Por debajo, la luz cálida de la bombilla; por encima, solo lo que deja pasar el vidrio verde.
     */
    private fun arco(x: Float): Float {
        val dx = (x - LUZ.x) / 0.16f
        return LUZ.y - 0.014f + (sqrt(1f + dx * dx) - 1f) * 0.067f
    }

    /** Cuánto le llega de la lámpara a un punto [p] (en fracciones del ancho). */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x; val dy = p.y - LUZ.y
        val calida = 1.5f * exp(-3.3f * sqrt(dx * dx + dy * dy * 0.7f))
        val verde = 0.5f * exp(-2.7f * sqrt(dx * dx + (dy + 0.04f) * (dy + 0.04f)))
        val s = ((p.y - arco(p.x) + 0.035f) / 0.07f).coerceIn(0f, 1f)
        return maxOf(verde + (calida - verde).coerceAtLeast(0f) * s, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /** Si [p] queda por encima del borde de la sombra de la pantalla: ahí la luz es la verde del vidrio. */
    private fun sobrePantalla(p: Offset) = p.y < arco(p.x) - 0.005f

    /**
     * La luz que cae sobre el frente del fichero: baja despacio de fila en fila (la primera, encendida; la segunda, a medias; la
     * tercera, tenue; las últimas, casi perdidas) y algo menos en la columna de la derecha, más lejos de la lámpara. Los puntos
     * son los centros de las filas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.92f, 0.79f to 0.8f, 1.066f to 0.52f, 1.34f to 0.27f, 1.614f to 0.11f, 1.888f to 0.05f, 2.1f to 0.035f, 2.5f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.75f * (abs(p.x - LUZ.x) - 0.12f).coerceAtLeast(0f)).coerceIn(0.6f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.14f + NOCHE.red * 0.8f, c.green * 0.14f + NOCHE.green * 0.8f, c.blue * 0.14f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia el
     * suyo según [k] (lo que le llega de la lámpara), teñido de la bombilla o, por encima de la pantalla ([verde]), de su vidrio.
     */
    private class Tono(val o: Boolean, val k: Float, val verde: Boolean, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        return if (verde) mezcla(v, VERDE_LUZ, (0.5f * k).coerceAtMost(0.3f)) else mezcla(v, LLAMA, 0.14f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del rincón. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la bombilla (o el verde del vidrio, arriba). */
    private fun Tono.b(a: Float): Color =
        if (!o) luzMueble(a) else (if (verde) mezcla(VERDE_LUZ, LUZ_MUEBLE, 0.3f) else LLAMA).copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))

    /** Un velo (un brillo o una sombra con alfa) ya mezclado sobre un color opaco: para tramos contiguos sin junta a la vista. */
    private fun Tono.sobre(fondo: Color, velo: Color) = mezcla(fondo, velo.copy(alpha = 1f), velo.alpha)

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono {
        val f = p / anchoW
        return Tono(esNoche, if (esNoche) luz(f) else 0f, esNoche && sobrePantalla(f), dia)
    }

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared: de día corta, corrida a la derecha y abajo; de noche, hacia fuera de la lámpara, tanto
     * más marcada cuanto más cerca está de ella. No baja de [hasta] (la tapa o el piso que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f, largo: Float = 1f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.018f * largo, w * 0.012f * largo) { drawPath(p, sombraMueble(a)) }
            else {
                val f = desde / w
                val k = luz(f)
                if (k > 0.1f) {
                    val dx = ((f.x - LUZ.x) / 0.35f).coerceIn(-1f, 1f)
                    val dy = if (f.y < LUZ.y) -0.6f else 1f
                    translate(w * 0.03f * dx * largo, w * 0.012f * dy * largo) { drawPath(p, NOCHE.copy(alpha = (0.75f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa o el piso, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro a la izquierda, la sombra a la derecha. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.18f * fuerza), Offset(r.left, r.top), Size(r.width * 0.32f, r.height))
        drawRect(t.b(0.4f * fuerza), Offset(r.left + r.width * 0.16f, r.top), Size(r.width * 0.09f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(r.left + r.width * 0.58f, r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.22f * fuerza), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un cuerpo tumbado dentro de [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.tumbado(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.16f * fuerza), r.topLeft, Size(r.width, r.height * 0.3f))
        drawRect(t.b(0.3f * fuerza), Offset(r.left, r.top + r.height * 0.1f), Size(r.width, r.height * 0.08f))
        drawRect(t.s(0.16f * fuerza), Offset(r.left, r.top + r.height * 0.62f), Size(r.width, r.height * 0.38f))
        drawRect(t.s(0.2f * fuerza), Offset(r.left, r.top + r.height * 0.84f), Size(r.width, r.height * 0.16f))
    }

    /** Un tornillo de cabeza ranurada, con su sombra y su brillo. */
    private fun DrawScope.tornillo(p: Offset, r: Float, t: Tono, col: Color, giro: Float) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.45f, r * 0.5f))
        drawCircle(t.c(col), r, p)
        drawCircle(t.b(0.5f), r * 0.45f, p - Offset(r * 0.3f, r * 0.3f))
        val a = Math.toRadians(giro.toDouble())
        val v = Offset(r * 0.8f * cos(a).toFloat(), r * 0.8f * sin(a).toFloat())
        drawLine(t.s(0.6f), p - v, p + v, r * 0.32f)
    }

    /** Renglones de bloques que sugieren letras (sin que se lea nada), desde [p]: cada bloque de [alto], hasta [ancho]. */
    private fun DrawScope.bloques(p: Offset, ancho: Float, alto: Float, color: Color, semilla: Int, renglones: Int = 1) {
        for (j in 0 until renglones) {
            var x = p.x
            val y = p.y + j * alto * 1.7f
            val fin = p.x + ancho * (if (j == renglones - 1 && renglones > 1) 0.4f + 0.4f * azar(semilla, 90 + j) else 1f)
            var k = 0
            while (x < fin - alto * 0.5f) {
                val a = alto * (0.5f + 1.5f * azar(semilla + j * 17, k))
                drawRect(color, Offset(x, y), Size(minOf(a, fin - x), alto))
                x += a + alto * 0.45f
                k++
            }
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
        val sup = ch * SUP; val frente = ch * FRENTE
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.88f
        val suelo = b.top + hb * SUELO

        // ---- la pared a dos tonos y el parquet
        pared(w, suelo)
        parquet(d, w, suelo, alto)

        // ---- en la pared: el banderín, el cartel y el calendario de taco
        banderin(Offset(w * 0.083f, w * 0.03f))
        cartel(Rect(w * 0.53f, -w * 0.03f, w * 0.795f, w * 0.335f))
        calendario(Rect(w * 0.862f, w * 0.06f, w * 0.957f, w * 0.215f))

        // ---- el fichero y su tapa
        fichero(d, ch, b)
        tapa(w, ch)

        // ---- lo de encima, de atrás adelante
        abaco(Offset(w * 0.872f, pieA), sup)
        libros(Offset(w * 0.565f, pieA), sup)
        cordon(w, pieA, pieF)
        lampara(Offset(w * LUZ.x, pieA), sup)
        tampon(Offset(w * 0.53f, pieF))
        vaso(Offset(w * 0.435f, pieF))

        // ---- al pie: el paquete de fichas nuevas, cortado por la izquierda, y la papelera, cortada por la derecha
        paquete(Offset(w * 0.02f, b.top + hb * PIE), w)
        papelera(Offset(w * 0.945f, b.top + hb * PIE), hb * 0.74f)

        // ---- la placa de «y N más»: la tarjeta del portaetiquetas de la tabla extraíble
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            val dentro = Rect(r.left + 2.5f * u, r.top + 2f * u, r.right - 2.5f * u, r.bottom - 2f * u)
            drawRect(if (o) PAPEL_LEJOS else CARTULINA, dentro.topLeft, dentro.size)
            drawRect(t.s(0.15f), dentro.topLeft, Size(dentro.width, 1.5f * u))
        }

        // ---- la luz de la lámpara, por encima de lo que tiene cerca
        if (o) {
            val foco = LUZ * w
            mancha(foco + Offset(0f, w * 0.1f), w * 0.34f, LLAMA.copy(alpha = 0.1f))
            mancha(foco + Offset(0f, w * 0.03f), w * 0.13f, LLAMA.copy(alpha = 0.26f))
            mancha(foco - Offset(0f, w * 0.09f), w * 0.2f, VERDE_LUZ.copy(alpha = 0.1f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- la pared y el piso

    /** Un desconchón: la pintura saltada deja ver el yeso, con el escalón de la capa en sombra arriba y con luz abajo. */
    private fun DrawScope.desconchon(c: Offset, r: Float, semilla: Int, estira: Float = 1.4f) {
        val u = density
        val t = tn(c)
        val n = 14
        val p = Path().apply {
            for (k in 0 until n) {
                val a = 2.0 * Math.PI * k / n
                val rr = r * (0.74f + 0.3f * azar(semilla, k))
                val x = c.x + rr * estira * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
                if (k == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(p, t.c(mezcla(CAL_OSC, ACERO, 0.3f)))
        clipPath(p) {
            translate(1.6f * u, 1.8f * u) { drawPath(p, t.s(0.4f), style = Stroke(2.4f * u)) }
            drawCircle(t.s(0.12f), r * 0.3f, c + Offset(r * 0.2f, r * 0.1f))
        }
        clipPath(p, clipOp = ClipOp.Difference) { translate(0.9f * u, 1f * u) { drawPath(p, t.b(0.5f), style = Stroke(1.2f * u)) } }
    }

    /**
     * La pared a dos tonos: arriba la cal amarillenta con sus brochazos anchos, la mancha de luz arriba a la izquierda y unas
     * grietas; abajo el óleo azul apagado, con los brochazos de arriba abajo, el brillo del esmalte, la raya de remate, unos
     * chorreones y el roce de abajo; dos desconchones, el enchufe de baquelita y el rodapié. De noche, el arco cálido que la
     * pantalla deja pasar por debajo y el resplandor verde por encima.
     */
    private fun DrawScope.pared(w: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val raya = w * RAYA
        // La cal.
        drawRect(if (o) mezcla(CAL, LLAMA, 0.2f) else CAL, Offset.Zero, Size(w, raya))
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(luzMueble(0.28f), Color.Transparent, sombraMueble(0.17f)), 0f, w), Offset.Zero, Size(w, raya))
            mancha(Offset(w * 0.02f, 0f), w * 0.62f, luzMueble(0.5f))
        }
        pinceladas(Rect(0f, 0f, w, raya), CAL_OSC, 5101, 44, u, 0.15f)
        pinceladas(Rect(0f, 0f, w, raya), LUZ_MUEBLE, 5102, 26, u, 0.18f)
        // Lo amarillo de la cal vieja, hacia arriba y hacia el rincón de la derecha.
        for (k in 0 until 7) {
            val c = Offset(w * (0.3f + 0.7f * azar(5103, k)), raya * (0.05f + 0.5f * azar(5104, k)))
            drawOval(mezcla(CAL_OSC, LATON, 0.4f).copy(alpha = 0.1f), c - Offset(w * 0.06f, w * 0.025f), Size(w * (0.08f + 0.08f * azar(5105, k)), w * 0.05f))
        }
        // El óleo.
        drawRect(if (o) mezcla(OLEO, LLAMA, 0.1f) else OLEO, Offset(0f, raya), Size(w, suelo - raya))
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.24f), Color.Transparent, sombraMueble(0.2f)), 0f, w), Offset(0f, raya), Size(w, suelo - raya))
        for (k in 0 until 90) {
            val x = w * azar(5110, k)
            val y0 = raya + (suelo - raya) * azar(5111, k) * 0.9f
            val claro = k % 3 == 0
            drawLine((if (claro) LUZ_MUEBLE else OLEO_OSC).copy(alpha = if (claro) 0.09f else 0.12f), Offset(x, y0), Offset(x + w * 0.004f * (azar(5112, k) - 0.5f), y0 + w * (0.05f + 0.12f * azar(5113, k))), (2f + 5f * azar(5114, k)) * u, StrokeCap.Round)
        }
        // El brillo del esmalte: una banda alta y tenue hacia la izquierda.
        if (!o) {
            drawRect(luzMueble(0.1f), Offset(w * 0.02f, raya), Size(w * 0.05f, suelo - raya))
        }
        // La raya de remate, pintada a pulso: sube y baja un poco.
        val rayaP = Path().apply {
            moveTo(0f, raya)
            for (k in 1..12) lineTo(w * k / 12f, raya + (azar(5120, k) - 0.5f) * 2.4f * u)
        }
        drawPath(rayaP, if (o) mezcla(OLEO_OSC, NOCHE, 0.3f) else OLEO_OSC, style = Stroke(3f * u))
        translate(0f, 2.4f * u) { drawPath(rayaP, luzMueble(if (o) 0.06f else 0.2f), style = Stroke(0.8f * u)) }
        // Los chorreones del óleo que se pasó de la raya y las gotas que corrieron.
        listOf(0.452f to 0.034f, 0.467f to 0.016f, 0.2f to 0.022f, 0.985f to 0.022f).forEach { (fx, largo) ->
            drawLine(OLEO_OSC.copy(alpha = 0.3f), Offset(w * fx, raya + 2f * u), Offset(w * fx, raya + w * largo), 1.1f * u, StrokeCap.Round)
            drawCircle(OLEO_OSC.copy(alpha = 0.4f), 1.3f * u, Offset(w * fx, raya + w * largo))
            drawCircle(luzMueble(if (o) 0.1f else 0.35f), 0.5f * u, Offset(w * fx - 0.4f * u, raya + w * largo - 0.4f * u))
        }
        // Grietas finas en la cal.
        listOf(Offset(0.44f, 0f), Offset(0.985f, 0.22f), Offset(0.215f, 0f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, w * f.y)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 6) { q += Offset(w * (0.002f + 0.007f * azar(5130 + i, j)) * (if (j % 2 == 0) 1f else -0.7f), w * (0.01f + 0.016f * azar(5140 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, sombraMueble(0.26f), style = Stroke(0.6f * u))
            translate(0.7f * u, 0.4f * u) { drawPath(g, luzMueble(0.35f), style = Stroke(0.4f * u)) }
        }
        // El roce de abajo: el óleo mate y sucio a la altura de los zapatos y la fregona.
        drawRect(sombraMueble(0.1f), Offset(0f, suelo - w * 0.11f), Size(w, w * 0.11f))
        pinceladas(Rect(0f, suelo - w * 0.1f, w, suelo), OLEO_OSC, 5150, 14, u, 0.2f)

        // De noche: todo cae con la distancia a la bombilla; por encima del borde de la sombra de la pantalla, casi nada.
        if (o) {
            val foco = LUZ * w
            fun velo(d: Float) = NOCHE.copy(alpha = (1f - 1.25f * exp(-3.6f * d)).coerceIn(0.14f, 0.95f))
            drawRect(Brush.radialGradient(0f to velo(0f), 0.12f to velo(0.156f), 0.25f to velo(0.325f), 0.42f to velo(0.55f), 0.65f to velo(0.85f), 1f to velo(1.3f),
                center = foco, radius = w * 1.3f), Offset.Zero, Size(w, suelo))
            val arriba = Path().apply {
                moveTo(0f, -w * 0.05f); lineTo(w, -w * 0.05f)
                for (k in 40 downTo 0) lineTo(w * k / 40f, w * arco(k / 40f))
                close()
            }
            for (k in 0 until 16) translate(0f, w * (0.03f - 0.004f * k)) { drawPath(arriba, NOCHE.copy(alpha = 0.09f)) }
            mancha(foco - Offset(0f, w * 0.1f), w * 0.5f, VERDE_LUZ.copy(alpha = 0.09f))
        }

        // Los desconchones: uno en la cal, arriba a la derecha; otro abajo, donde el óleo saltó junto al rodapié.
        desconchon(Offset(w * 0.447f, w * 0.1f), w * 0.016f, 5160, 1.5f)
        desconchon(Offset(w * 0.988f, w * 0.49f), w * 0.017f, 5161, 0.7f)

        // El enchufe de baquelita, a media altura, a la izquierda.
        val en = Offset(w * 0.017f, w * 0.86f)
        val te = tn(en)
        drawCircle(te.s(0.3f), w * 0.0175f, en + Offset(w * 0.004f, w * 0.004f))
        drawCircle(te.c(mezcla(PARQUET_OSC, NEGRO, 0.55f)), w * 0.017f, en)
        drawCircle(te.b(0.25f), w * 0.017f, en, style = Stroke(1f * u))
        drawCircle(te.s(0.3f), w * 0.011f, en, style = Stroke(0.8f * u))

        // El rodapié de madera pintada, con su canto de luz.
        val rod = suelo - w * 0.034f
        val tz = tn(Offset(w * 0.5f, rod))
        drawRect(tz.c(mezcla(PARQUET_OSC, GRANATE, 0.3f)), Offset(0f, rod), Size(w, suelo - rod))
        drawRect(tz.b(0.4f), Offset(0f, rod), Size(w, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(0f, rod + w * 0.008f), Size(w, 1f * u))
    }

    /**
     * El parquet en espiga, visto de pie: las tablillas en columnas que suben y bajan, cada una de su tono, con las juntas, el
     * paso gastado delante del fichero, el brillo de la cera y la sombra del mueble.
     */
    private fun DrawScope.parquet(d: Disposicion, w: Float, suelo: Float, alto: Float) {
        val u = density
        val cw = w / 12.4f; val ph = cw * 0.3f; val s = cw * 0.4f
        drawRect(tn(Offset(w / 2, alto)).c(PARQUET_OSC), Offset(0f, suelo), Size(w, alto - suelo))
        clipRect(0f, suelo, w, alto) {
            var j = -1
            while (j * cw < w) {
                val x0 = j * cw - cw * 0.3f; val x1 = x0 + cw
                val sube = Math.floorMod(j, 2) == 0
                var i = -3
                while (suelo + i * ph < alto + s) {
                    val y = suelo + i * ph
                    val ya = if (sube) y + s else y; val yb = if (sube) y else y + s
                    val tt = tn(Offset((x0 + x1) / 2, y + ph))
                    val az = azar(5300 + j, i + 7)
                    val tono = if (az > 0.92f) mezcla(PARQUET, CARTON, 0.5f) else mezcla(PARQUET_OSC, PARQUET, 0.3f + 0.75f * az)
                    drawPath(poligono(x0, ya, x1, yb, x1, yb + ph + 0.5f, x0, ya + ph + 0.5f), tt.c(tono))
                    drawLine(tt.s(0.32f), Offset(x0, ya + ph), Offset(x1, yb + ph), 0.8f * u)
                    drawLine(tt.b(0.14f), Offset(x0, ya + 0.9f * u), Offset(x1, yb + 0.9f * u), 0.6f * u)
                    i++
                }
                drawLine(tn(Offset(x0, alto)).s(0.3f), Offset(x0, suelo), Offset(x0, alto), 0.8f * u)
                j++
            }
            // El paso gastado y el brillo de la cera.
            val t = tn(Offset(w * 0.4f, alto))
            pinceladas(Rect(w * 0.15f, suelo + (alto - suelo) * 0.45f, w * 0.8f, alto), if (esNoche) NOCHE else CARTON, 5310, 14, u, 0.16f)
            drawOval(t.b(0.13f), Offset(w * 0.1f, alto - (alto - suelo) * 0.42f), Size(w * 0.42f, (alto - suelo) * 0.3f))
            drawRect(t.b(0.1f), Offset(w * 0.2f, alto - (alto - suelo) * 0.2f), Size(w * 0.22f, 1.4f * u))
        }
        // La sombra del rincón.
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(tt.s(0.35f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- en la pared

    /**
     * El banderín rojo de emulación: cuelga de un clavo por su cordón dorado, con la varilla de madera torneada, la tela de
     * raso con sus aguas, el filete, una estrella pequeña, dos renglones bordados (bloques), el fleco dorado y la borla.
     */
    private fun DrawScope.banderin(clavo: Offset) {
        val u = density
        val w = anchoW
        val t = tn(clavo + Offset(0f, w * 0.11f))
        val x0 = clavo.x - w * 0.054f; val x1 = clavo.x + w * 0.054f
        val yv = clavo.y + w * 0.042f
        val yl = yv + w * 0.13f
        val yp = yv + w * 0.19f
        val cx = clavo.x + w * 0.002f
        val tela = Path().apply {
            moveTo(x0 + w * 0.004f, yv)
            lineTo(x1 - w * 0.004f, yv)
            cubicTo(x1 - w * 0.001f, yv + w * 0.045f, x1 - w * 0.008f, yl - w * 0.04f, x1 - w * 0.004f, yl)
            lineTo(cx, yp)
            lineTo(x0 + w * 0.006f, yl + w * 0.003f)
            cubicTo(x0 + w * 0.01f, yl - w * 0.045f, x0 + w * 0.001f, yv + w * 0.05f, x0 + w * 0.004f, yv)
            close()
        }
        sombraEnPared(tela, clavo + Offset(0f, w * 0.1f), size.height, 0.22f)
        val oro = t.c(mezcla(LATON, LLAMA, 0.3f))
        // El cordón y el clavo.
        drawLine(oro, clavo, Offset(x0 + w * 0.006f, yv), 1.1f * u)
        drawLine(oro, clavo, Offset(x1 - w * 0.006f, yv), 1.1f * u)
        tornillo(clavo, 1.5f * u, t, METAL, 0f)
        // La tela, con sus aguas.
        drawPath(tela, t.c(ROJO))
        clipPath(tela) {
            val aguas = Path().apply {
                moveTo(x0 + w * 0.02f, yv)
                cubicTo(x0 + w * 0.035f, yv + w * 0.06f, x0 + w * 0.012f, yv + w * 0.11f, x0 + w * 0.03f, yp)
                lineTo(x0 + w * 0.046f, yp)
                cubicTo(x0 + w * 0.03f, yv + w * 0.11f, x0 + w * 0.052f, yv + w * 0.06f, x0 + w * 0.036f, yv)
                close()
            }
            translate(-w * 0.012f, 0f) { drawPath(aguas, t.b(0.22f)) }
            translate(w * 0.008f, 0f) { drawPath(aguas, t.s(0.2f)) }
            translate(w * 0.036f, 0f) { drawPath(aguas, t.b(0.12f)) }
            translate(w * 0.058f, 0f) { drawPath(aguas, t.s(0.26f)) }
            drawRect(t.s(0.3f), Offset(x0, yv), Size(x1 - x0, w * 0.009f))
            drawRect(t.s(0.12f), Offset(x0, yv + w * 0.009f), Size(x1 - x0, w * 0.008f))
        }
        // El filete dorado, la estrella y los renglones bordados.
        val fil = Path().apply {
            moveTo(x0 + w * 0.013f, yv + w * 0.014f); lineTo(x1 - w * 0.013f, yv + w * 0.014f); lineTo(x1 - w * 0.014f, yl - w * 0.004f)
            lineTo(cx, yp - w * 0.014f); lineTo(x0 + w * 0.015f, yl - w * 0.002f); close()
        }
        drawPath(fil, oro.copy(alpha = 0.85f), style = Stroke(0.9f * u))
        drawPath(estrella(Offset(cx, yv + w * 0.045f), w * 0.019f, 5, 0.42f), oro)
        bloques(Offset(cx - w * 0.028f, yv + w * 0.078f), w * 0.056f, w * 0.006f, oro, 5201)
        bloques(Offset(cx - w * 0.02f, yv + w * 0.094f), w * 0.04f, w * 0.006f, oro, 5202)
        drawLine(oro, Offset(cx - w * 0.012f, yv + w * 0.118f), Offset(cx + w * 0.012f, yv + w * 0.118f), 1f * u)
        // El fleco, en los dos lados de la punta, y la borla.
        for (k in 0..16) {
            val f = k / 16f
            listOf(Offset(x0 + w * 0.006f, yl + w * 0.003f), Offset(x1 - w * 0.004f, yl)).forEachIndexed { i, a ->
                val p = Offset(a.x + (cx - a.x) * f, a.y + (yp - a.y) * f)
                val largo = w * (0.012f + 0.004f * azar(5210 + i, k))
                drawLine(t.c(if ((k + i) % 3 == 0) LATON_OSC else mezcla(LATON, LLAMA, 0.3f)), p, p + Offset((azar(5212 + i, k) - 0.5f) * 2f * u, largo), 1f * u)
            }
        }
        drawLine(oro, Offset(cx, yp), Offset(cx + 0.8f * u, yp + w * 0.012f), 1f * u)
        drawOval(oro, Offset(cx - w * 0.004f, yp + w * 0.01f), Size(w * 0.009f, w * 0.02f))
        drawOval(t.s(0.25f), Offset(cx + w * 0.001f, yp + w * 0.01f), Size(w * 0.004f, w * 0.02f))
        // La varilla, con sus pomos.
        val madera = t.c(mezcla(PARQUET, CARTON, 0.3f))
        drawRect(t.s(0.3f), Offset(x0 - w * 0.004f, yv + w * 0.003f), Size(x1 - x0 + w * 0.008f, w * 0.004f))
        drawLine(madera, Offset(x0 - w * 0.006f, yv), Offset(x1 + w * 0.006f, yv), w * 0.0075f, StrokeCap.Round)
        drawLine(t.b(0.5f), Offset(x0 - w * 0.004f, yv - w * 0.002f), Offset(x1 + w * 0.004f, yv - w * 0.002f), 0.8f * u)
        listOf(x0 - w * 0.008f, x1 + w * 0.008f).forEach { x ->
            drawCircle(madera, w * 0.0065f, Offset(x, yv))
            drawCircle(t.b(0.5f), w * 0.002f, Offset(x - w * 0.002f, yv - w * 0.002f))
        }
    }

    /**
     * El cartel constructivista, sujeto con chinchetas y cortado por el borde de arriba: papel de cartel amarillento con sus
     * dobleces, un círculo negro, la cuña roja que entra en él, una diagonal negra, un cuadrado rojo y renglones de bloques
     * que sugieren letras; la esquina de abajo a la izquierda se despega.
     */
    private fun DrawScope.cartel(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(Offset(r.center.x, r.top + r.height * 0.6f))
        rotate(-1.3f, r.center) {
            val papel = Path().apply { addRect(r) }
            sombraEnPared(papel, r.center, size.height, 0.24f, 0.3f)
            val fondo = t.c(mezcla(CARTULINA, CARTON, 0.16f))
            drawRect(fondo, r.topLeft, r.size)
            val pw = r.width; val ph = r.height
            fun px(f: Float) = r.left + pw * f
            fun py(f: Float) = r.top + ph * f
            val negro = t.c(mezcla(NEGRO, OLEO_OSC, 0.08f))
            val rojo = t.c(ROJO)
            clipRect(r.left, r.top, r.right, r.bottom) {
                // El amarilleo del papel.
                pinceladas(r, if (esNoche) NOCHE else CARTON, 5220, 9, u, 0.12f)
                // El círculo negro y su arco.
                val cc = Offset(px(0.64f), py(0.4f)); val cr = pw * 0.27f
                drawCircle(negro, cr, cc)
                drawArc(negro, 150f, 140f, false, cc - Offset(cr * 1.22f, cr * 1.22f), Size(cr * 2.44f, cr * 2.44f), style = Stroke(1.5f * u))
                // La cuña roja que entra en el círculo desde abajo a la izquierda.
                drawPath(poligono(px(0f), py(0.56f), px(0f), py(0.86f), px(0.7f), py(0.42f)), rojo)
                // La diagonal negra y su filete rojo.
                drawPath(poligono(px(0.26f), py(0.9f), px(0.33f), py(0.935f), px(1f), py(0.58f), px(1f), py(0.535f)), negro)
                drawLine(rojo, Offset(px(0.4f), py(0.955f)), Offset(px(1f), py(0.645f)), 1.6f * u)
                // El cuadrado rojo de arriba a la izquierda y sus renglones.
                drawRect(rojo, Offset(px(0.08f), py(0.14f)), Size(pw * 0.13f, pw * 0.13f))
                drawRect(negro, Offset(px(0.08f), py(0.14f) + pw * 0.15f), Size(pw * 0.25f, pw * 0.018f))
                bloques(Offset(px(0.08f), py(0.14f) + pw * 0.2f), pw * 0.22f, pw * 0.022f, negro, 5221, 2)
                // Los bloques de abajo a la izquierda (el lema, que no se lee) y el pie de imprenta.
                bloques(Offset(px(0.07f), py(0.88f)), pw * 0.17f, pw * 0.04f, negro, 5222)
                bloques(Offset(px(0.62f), py(0.95f)), pw * 0.3f, pw * 0.014f, negro.copy(alpha = 0.7f), 5223)
                // Los dobleces del papel.
                drawRect(t.s(0.12f), Offset(r.left, py(0.5f)), Size(pw, 1f * u)); drawRect(t.b(0.3f), Offset(r.left, py(0.5f) + 1f * u), Size(pw, 0.8f * u))
                drawRect(t.s(0.12f), Offset(px(0.5f), r.top), Size(1f * u, ph)); drawRect(t.b(0.3f), Offset(px(0.5f) + 1f * u, r.top), Size(0.8f * u, ph))
                // La luz que resbala por el papel.
                drawRect(t.b(0.08f), r.topLeft, Size(pw * 0.5f, ph))
                drawRect(t.s(0.08f), Offset(px(0.8f), r.top), Size(pw * 0.2f, ph))
            }
            drawRect(t.c(CARTON).copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.8f * u))
            // La esquina que se despega: el dorso del papel y su sombra.
            val e = pw * 0.12f
            drawPath(poligono(r.left, r.bottom - e, r.left + e, r.bottom, r.left, r.bottom), tn(Offset(r.left, r.bottom)).c(if (r.bottom > w * RAYA) OLEO else CAL))
            drawPath(poligono(r.left + e * 1.15f, r.bottom, r.left, r.bottom - e * 1.15f, r.left + e * 0.8f, r.bottom - e * 0.62f), t.s(0.22f))
            drawPath(poligono(r.left, r.bottom - e, r.left + e, r.bottom, r.left + e * 0.72f, r.bottom - e * 0.66f), t.c(mezcla(CARTULINA, LUZ_MUEBLE, 0.3f)))
            // Las chinchetas.
            listOf(Offset(px(0.06f), py(0.55f)), Offset(px(0.95f), py(0.14f)), Offset(px(0.95f), py(0.965f))).forEach { p ->
                drawCircle(t.s(0.4f), 2f * u, p + Offset(1.2f * u, 1.4f * u))
                drawCircle(t.c(METAL), 1.9f * u, p)
                drawCircle(t.b(0.8f), 0.7f * u, p - Offset(0.6f * u, 0.6f * u))
            }
        }
    }

    /**
     * El calendario de taco: el cartón de fondo con su estampa (dos franjas y bloques), la grapa de chapa, los restos de las
     * hojas arrancadas, el taco con su grosor y la hoja del día con su cifra en bloques; la esquina de la hoja se levanta.
     */
    private fun DrawScope.calendario(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        val clavo = Offset(r.center.x, r.top + r.height * 0.05f)
        val carton = Path().apply { addRect(r) }
        sombraEnPared(carton, r.center, size.height, 0.22f, 0.5f)
        drawRect(t.c(mezcla(CARTON, CARTULINA, 0.3f)), r.topLeft, r.size)
        // La estampa del cartón: la franja azul de arriba, un sol rojo y renglones.
        val est = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.1f, r.right - r.width * 0.08f, r.top + r.height * 0.36f)
        drawRect(t.c(mezcla(OLEO, OLEO_OSC, 0.4f)), est.topLeft, est.size)
        drawCircle(t.c(ROJO), est.height * 0.3f, Offset(est.left + est.width * 0.28f, est.center.y))
        bloques(Offset(est.left + est.width * 0.52f, est.top + est.height * 0.3f), est.width * 0.4f, est.height * 0.14f, t.c(CARTULINA), 5230, 2)
        drawRect(t.b(0.3f), r.topLeft, Size(r.width, 1f * u)); drawRect(t.b(0.2f), r.topLeft, Size(1f * u, r.height))
        drawRect(t.s(0.25f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        tornillo(clavo, 1.4f * u, t, METAL, 40f)
        // El taco: su grosor abajo y a la derecha, la grapa y los restos arrancados.
        val taco = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.42f, r.right - r.width * 0.14f, r.bottom - r.height * 0.07f)
        drawRect(t.s(0.3f), Offset(taco.left + w * 0.006f, taco.top + w * 0.006f), taco.size)
        for (k in 3 downTo 1) drawRect(t.c(if (k % 2 == 0) CARTULINA_OSC else CARTULINA), Offset(taco.left + k * 0.9f * u, taco.top + k * 0.9f * u), taco.size)
        drawRect(t.c(CARTULINA), taco.topLeft, taco.size)
        // La cifra del día (dos bloques gruesos), el mes y el día de la semana.
        val tinta = t.c(NEGRO)
        val cy = taco.top + taco.height * 0.3f; val ah = taco.height * 0.36f; val gw = taco.width * 0.2f
        val gx = taco.center.x - gw * 1.15f
        drawRect(tinta, Offset(gx + gw * 0.5f, cy), Size(gw * 0.4f, ah)); drawPath(poligono(gx, cy + ah * 0.3f, gx + gw * 0.5f, cy, gx + gw * 0.5f, cy + ah * 0.3f), tinta)
        val g2 = gx + gw * 1.3f
        drawRect(tinta, Offset(g2, cy), Size(gw, ah * 0.24f)); drawPath(poligono(g2 + gw * 0.6f, cy + ah * 0.24f, g2 + gw, cy + ah * 0.24f, g2 + gw * 0.5f, cy + ah, g2 + gw * 0.1f, cy + ah), tinta)
        bloques(Offset(taco.left + taco.width * 0.2f, taco.top + taco.height * 0.12f), taco.width * 0.6f, taco.height * 0.06f, t.c(ROJO), 5231)
        bloques(Offset(taco.left + taco.width * 0.14f, taco.top + taco.height * 0.76f), taco.width * 0.72f, taco.height * 0.045f, tinta.copy(alpha = 0.6f), 5232, 2)
        drawRect(t.s(0.1f), Offset(taco.right - taco.width * 0.12f, taco.top), Size(taco.width * 0.12f, taco.height))
        // La grapa y los restos de las hojas arrancadas.
        val gr = Rect(taco.left - 1f * u, taco.top - r.height * 0.045f, taco.right + 1f * u, taco.top + r.height * 0.02f)
        val restos = Path().apply {
            moveTo(gr.left + 2f * u, gr.bottom)
            for (k in 0..9) lineTo(gr.left + gr.width * k / 9f, gr.bottom + r.height * (0.008f + 0.022f * azar(5233, k)))
            lineTo(gr.right - 2f * u, gr.bottom); close()
        }
        drawPath(restos, t.c(CARTULINA_OSC))
        drawRect(t.s(0.3f), Offset(gr.left + 1f * u, gr.bottom), Size(gr.width, 1.2f * u))
        drawRect(t.c(METAL), gr.topLeft, gr.size)
        drawRect(t.b(0.6f), gr.topLeft, Size(gr.width, 0.9f * u))
        drawRect(t.s(0.25f), Offset(gr.left, gr.bottom - 1f * u), Size(gr.width, 1f * u))
        listOf(0.2f, 0.8f).forEach { f -> drawCircle(t.s(0.5f), 0.9f * u, Offset(gr.left + gr.width * f, gr.center.y)) }
        // La esquina de la hoja que se levanta.
        val e = taco.width * 0.2f
        drawPath(poligono(taco.right - e * 1.2f, taco.bottom, taco.right, taco.bottom - e * 1.2f, taco.right - e * 0.3f, taco.bottom - e * 0.25f), t.s(0.25f))
        drawPath(poligono(taco.right - e, taco.bottom, taco.right, taco.bottom - e, taco.right - e * 0.72f, taco.bottom - e * 0.7f), t.c(mezcla(CARTULINA, LUZ_MUEBLE, 0.5f)))
    }

    // ---------------------------------------------------------------- el fichero

    /**
     * El cuerpo del fichero: su sombra en la pared y en el piso, la chapa esmaltada con el brillo duro del esmalte, el hueco de
     * cada cajón con su fondo en sombra y la sombra que el cajón deja en el larguero de abajo, los tornillos de los montantes,
     * los cantos gastados hasta el metal, una abolladura, el óxido de abajo, la chapita de inventario, la tabla extraíble con su
     * portaetiquetas y sus pomos, y las patas de tubo con el travesaño y las conteras de goma.
     */
    private fun DrawScope.fichero(d: Disposicion, ch: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val canto = ch * CANTO
        val fondo = b.top + hb * FONDO
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(xL, canto, xR, fondo)

        // Su sombra en la pared (a la derecha) y debajo, entre las patas, en la pared y el piso.
        val t0 = tn(Offset(xR, ch * 1.3f))
        drawPath(poligono(xR, ch * SUP, xR + w * 0.03f, ch * SUP + w * 0.022f, xR + w * 0.03f, suelo, xR, suelo), t0.s(0.3f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.42f), Offset(xL, fondo), Size(xR - xL + w * 0.03f, suelo - fondo))
        drawPath(poligono(xL + w * 0.03f, suelo, xR + w * 0.03f, suelo, xR + w * 0.035f, pisan + w * 0.012f, xL + w * 0.07f, pisan + w * 0.012f), tp.s(0.4f))
        drawPath(poligono(xL + w * 0.07f, pisan + w * 0.012f, xR + w * 0.035f, pisan + w * 0.012f, xR + w * 0.035f, pisan + w * 0.03f, xL + w * 0.11f, pisan + w * 0.03f), tp.s(0.18f))

        // Las patas de atrás, en sombra.
        val pw = w * 0.022f
        listOf(xL + w * 0.2f, xR - w * 0.2f - pw).forEach { x ->
            val r = Rect(x, fondo, x + pw, pisan - w * 0.03f)
            drawRect(tp.c(mezcla(ACERO_OSC, NEGRO, 0.6f)), r.topLeft, r.size)
            drawRect(tp.s(0.3f), r.topLeft, r.size)
        }

        // La chapa esmaltada.
        val forma = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(w * 0.01f, w * 0.01f))) }
        if (!o) drawPath(forma, ACERO)
        else drawPath(forma, Brush.verticalGradient(List(7) { i -> tn(Offset(w * 0.3f, cuerpo.top + cuerpo.height * i / 6f)).c(ACERO) }, cuerpo.top, cuerpo.bottom))
        clipPath(forma) {
            if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.26f), Color.Transparent, Color.Transparent, sombraMueble(0.24f)), xL, xR), cuerpo.topLeft, cuerpo.size)
            else drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.42f)), w * 0.35f, xR), cuerpo.topLeft, cuerpo.size)
            pinceladas(cuerpo, if (o) NOCHE else ACERO_OSC, 5401, 30, u, 0.1f)
            pinceladas(cuerpo, if (o) NOCHE else ACERO_LUZ, 5402, 18, u, 0.12f)
            // El brillo duro del esmalte, de arriba abajo, en el montante de la izquierda.
            for (k in 0 until 10) {
                val y0 = cuerpo.top + cuerpo.height * k / 10f
                val t = tn(Offset(xL, y0 + cuerpo.height / 20f))
                drawRect(t.b(0.5f), Offset(xL, y0), Size(1.4f * u, cuerpo.height / 10f + 0.5f))
                drawRect(t.b(0.22f), Offset(xL + w * 0.011f, y0), Size(1.6f * u, cuerpo.height / 10f + 0.5f))
                val tr = tn(Offset(xR, y0 + cuerpo.height / 20f))
                drawRect(tr.s(0.3f), Offset(xR - w * 0.009f, y0), Size(w * 0.009f, cuerpo.height / 10f + 0.5f))
            }
        }

        // El hueco de cada cajón y la sombra que el cajón, que sobresale, deja en el larguero de abajo y a su derecha.
        val m = w * 0.005f
        d.huecos.forEach { r ->
            val t = tn(r.center)
            drawRect(t.c(mezcla(ACERO_OSC, NEGRO, 0.72f)), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, r.height + 2 * m))
            drawRect(t.b(0.4f), Offset(r.left - m, r.bottom + m), Size(r.width + 2 * m, 1f * u))
            drawRect(t.b(0.25f), Offset(r.right + m, r.top - m), Size(1f * u, r.height + 2 * m))
            drawRect(t.s(0.34f), Offset(r.left + w * 0.006f, r.bottom + m + 1f * u), Size(r.width + w * 0.004f, w * 0.008f))
            drawRect(t.s(0.16f), Offset(r.left + w * 0.009f, r.bottom + m + 1f * u + w * 0.008f), Size(r.width + w * 0.003f, w * 0.005f))
            drawRect(t.s(0.28f), Offset(r.right + m + 1f * u, r.top + w * 0.008f), Size(w * 0.007f, r.height))
        }

        // Los tornillos de los montantes, a la altura de cada larguero.
        val ys = d.filas.map { it.top - d.separacion / 2 } + (d.filas.last().bottom + d.separacion / 2)
        ys.forEachIndexed { i, y ->
            listOf(xL + w * 0.017f, w * 0.5f, xR - w * 0.017f).forEachIndexed { j, x ->
                tornillo(Offset(x, y), w * 0.0045f, tn(Offset(x, y)), mezcla(ACERO_LUZ, METAL, 0.5f), 180f * azar(5410 + i, j))
            }
        }

        // Los cantos gastados hasta el metal, a la izquierda y junto a los cajones que más se usan.
        for (k in 0 until 16) {
            val y = cuerpo.top + cuerpo.height * azar(5420, k)
            val izq = k % 4 != 0
            val x = if (izq) xL + w * 0.002f * azar(5421, k) else w * 0.5f + w * 0.012f * (azar(5421, k) - 0.5f)
            val t = tn(Offset(x, y))
            drawLine(t.c(METAL), Offset(x, y), Offset(x + w * 0.003f * azar(5422, k), y + w * (0.008f + 0.025f * azar(5423, k))), (0.8f + 1.4f * azar(5424, k)) * u, StrokeCap.Round)
        }
        // Arañazos finos en diagonal.
        for (k in 0 until 6) {
            val p = Offset(xL + (xR - xL) * (if (k % 2 == 0) 0.008f + 0.02f * azar(5430, k) else 0.955f + 0.03f * azar(5430, k)), cuerpo.top + cuerpo.height * (0.1f + 0.8f * azar(5431, k)))
            drawLine(tn(p).b(0.4f), p, p + Offset(w * 0.012f, w * 0.03f * (azar(5432, k) - 0.3f)), 0.6f * u)
        }
        // La abolladura, en el montante de la derecha.
        val ab = Offset(xR - w * 0.018f, d.filas[minOf(1, d.filas.size - 1)].bottom - w * 0.05f)
        val ta = tn(ab)
        drawOval(ta.s(0.3f), ab - Offset(w * 0.011f, w * 0.018f), Size(w * 0.02f, w * 0.034f))
        drawOval(ta.c(ACERO), ab - Offset(w * 0.007f, w * 0.013f), Size(w * 0.019f, w * 0.032f))
        drawArc(ta.b(0.55f), -20f, 110f, false, ab - Offset(w * 0.011f, w * 0.018f), Size(w * 0.021f, w * 0.035f), style = Stroke(1f * u))
        drawCircle(ta.c(METAL), 1.3f * u, ab + Offset(w * 0.002f, -w * 0.004f))
        // El óxido de abajo.
        for (k in 0 until 9) {
            val p = Offset(xL + (xR - xL) * azar(5440, k), fondo - w * 0.012f * azar(5441, k))
            drawOval(tn(p).c(mezcla(GRANATE, PARQUET, 0.5f)).copy(alpha = 0.5f), p - Offset(w * 0.006f, w * 0.003f), Size(w * (0.008f + 0.012f * azar(5442, k)), w * 0.006f))
        }

        // La chapita de inventario, en el friso, con sus dos remaches.
        val primera = d.filas.first()
        val fy = (canto + primera.top) / 2 + w * 0.004f
        val chapa = Rect(w * 0.79f, fy - w * 0.011f, w * 0.875f, fy + w * 0.011f)
        val tc = tn(chapa.center)
        drawRoundRect(tc.s(0.3f), chapa.topLeft + Offset(1.2f * u, 1.4f * u), chapa.size, CornerRadius(w * 0.004f))
        drawRoundRect(tc.c(METAL), chapa.topLeft, chapa.size, CornerRadius(w * 0.004f))
        drawRect(tc.b(0.6f), chapa.topLeft + Offset(w * 0.004f, 0f), Size(chapa.width - w * 0.008f, 0.9f * u))
        bloques(Offset(chapa.left + w * 0.014f, chapa.center.y - w * 0.004f), chapa.width - w * 0.028f, w * 0.008f, tc.c(NEGRO).copy(alpha = 0.75f), 5450)
        listOf(chapa.left + w * 0.006f, chapa.right - w * 0.006f).forEach { x -> drawCircle(tc.s(0.5f), 1f * u, Offset(x, chapa.center.y)) }
        // El número de la sala, a plantilla, en el friso de la izquierda.
        bloques(Offset(xL + w * 0.045f, fy - w * 0.006f), w * 0.07f, w * 0.012f, tn(Offset(xL + w * 0.06f, fy)).c(CARTULINA).copy(alpha = if (o) 0.5f else 0.55f), 5451)

        // ---- la tabla extraíble: su frente, el portaetiquetas (la placa) y dos pomos
        val tabla = Rect(xL + w * 0.03f, b.top + hb * TABLA0, xR - w * 0.03f, b.top + hb * TABLA1)
        val tt = tn(tabla.center)
        drawRect(tt.c(mezcla(ACERO_OSC, NEGRO, 0.7f)), Offset(tabla.left - m, tabla.top - m), Size(tabla.width + 2 * m, tabla.height + 2 * m))
        drawRect(tt.s(0.32f), Offset(tabla.left + w * 0.006f, tabla.bottom + m), Size(tabla.width, w * 0.008f))
        drawRoundRect(tt.c(mezcla(ACERO, ACERO_LUZ, 0.35f)), tabla.topLeft, tabla.size, CornerRadius(w * 0.006f))
        clipRect(tabla.left, tabla.top, tabla.right, tabla.bottom) {
            for (k in 0 until 6) {
                val x0 = tabla.left + tabla.width * k / 6f
                val tk = tn(Offset(x0 + tabla.width / 12f, tabla.center.y))
                drawRect(tk.b(0.55f), Offset(x0, tabla.top), Size(tabla.width / 6f + 0.5f, 1.3f * u))
                drawRect(tk.b(0.14f), Offset(x0, tabla.top + 1.3f * u), Size(tabla.width / 6f + 0.5f, tabla.height * 0.2f))
                drawRect(tk.s(0.22f), Offset(x0, tabla.bottom - tabla.height * 0.22f), Size(tabla.width / 6f + 0.5f, tabla.height * 0.22f))
            }
            drawRect(tt.b(0.3f), tabla.topLeft, Size(1.2f * u, tabla.height))
            drawRect(tt.s(0.3f), Offset(tabla.right - 1.4f * u, tabla.top), Size(1.4f * u, tabla.height))
            // El roce de las manos, en el canto de arriba.
            for (k in 0 until 5) { val x = tabla.left + tabla.width * (0.2f + 0.6f * azar(5460, k)); drawLine(tn(Offset(x, tabla.top)).c(METAL), Offset(x, tabla.top + 1f * u), Offset(x + w * (0.01f + 0.02f * azar(5461, k)), tabla.top + 1f * u), 1.2f * u, StrokeCap.Round) }
        }
        val pl = d.enBase(placa)
        val tl = tn(pl.center)
        drawRect(tl.s(0.3f), Offset(pl.left + 1.2f * u, pl.top + 1.6f * u), pl.size)
        drawRect(tl.c(LATON), pl.topLeft, pl.size)
        drawRect(tl.b(0.55f), pl.topLeft, Size(pl.width, 1f * u))
        drawRect(tl.s(0.3f), Offset(pl.left, pl.bottom - 1.2f * u), Size(pl.width, 1.2f * u))
        val dentro = Rect(pl.left + 2.5f * u, pl.top + 2f * u, pl.right - 2.5f * u, pl.bottom - 2f * u)
        if (d.mas <= 0) {
            // Sin nada que decir: la tarjeta vieja de la tabla, amarillenta, con sus renglones a lápiz de copiar.
            drawRect(tl.c(mezcla(CARTULINA_OSC, CARTON, 0.3f)), dentro.topLeft, dentro.size)
            bloques(Offset(dentro.left + dentro.width * 0.06f, dentro.center.y - dentro.height * 0.14f), dentro.width * 0.5f, dentro.height * 0.26f, tl.c(VIOLETA).copy(alpha = 0.55f), 5470)
            drawRect(tl.c(ROJO).copy(alpha = 0.5f), Offset(dentro.left + dentro.width * 0.7f, dentro.top), Size(0.8f * u, dentro.height))
            bloques(Offset(dentro.left + dentro.width * 0.74f, dentro.center.y - dentro.height * 0.14f), dentro.width * 0.18f, dentro.height * 0.26f, tl.c(TINTA).copy(alpha = 0.5f), 5471)
            drawRect(tl.s(0.2f), dentro.topLeft, Size(dentro.width, 1.5f * u))
        }
        listOf(w * 0.13f, w * 0.87f).forEach { x ->
            val c = Offset(x, tabla.center.y)
            val tk = tn(c)
            val rp = tabla.height * 0.2f
            drawCircle(tk.s(0.35f), rp, c + Offset(1.4f * u, 2f * u))
            drawCircle(tk.c(NEGRO), rp, c)
            drawCircle(tk.b(0.3f), rp * 0.7f, c - Offset(rp * 0.12f, rp * 0.12f), style = Stroke(0.8f * u))
            drawCircle(tk.b(0.7f), rp * 0.22f, c - Offset(rp * 0.4f, rp * 0.4f))
        }

        // ---- las patas de tubo, el travesaño y las conteras
        val negro = mezcla(NEGRO, ACERO_OSC, 0.6f)
        val trav = Rect(xL + w * 0.15f, fondo + (pisan - fondo) * 0.5f, xR - w * 0.15f, fondo + (pisan - fondo) * 0.5f + w * 0.013f)
        val tv = tn(trav.center)
        drawRect(tv.s(0.3f), Offset(trav.left + w * 0.02f, trav.bottom + w * 0.004f), Size(trav.width, w * 0.005f))
        drawRect(tv.c(negro), trav.topLeft, trav.size)
        clipRect(trav.left, trav.top, trav.right, trav.bottom) { tumbado(trav, tv, 1.6f) }
        listOf(xL + w * 0.14f, xR - w * 0.14f - pw).forEachIndexed { i, x ->
            val r = Rect(x, fondo, x + pw, pisan - w * 0.012f)
            val t = tn(r.center)
            contacto(Offset(r.center.x + w * 0.012f, pisan), pw * 2.4f, w * 0.01f, 0.5f)
            drawRect(t.c(negro), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                cilindro(r, t, 1.5f)
                drawRect(t.s(0.4f), r.topLeft, Size(r.width, w * 0.012f))
                // El esmalte saltado de los puntapiés.
                drawRect(t.c(METAL).copy(alpha = 0.7f), Offset(r.left + r.width * (0.1f + 0.3f * i), r.bottom - w * 0.02f), Size(r.width * 0.3f, w * 0.008f))
            }
            val goma = Rect(x - w * 0.003f, pisan - w * 0.013f, x + pw + w * 0.003f, pisan)
            drawRoundRect(t.c(mezcla(NEGRO, PARQUET_OSC, 0.2f)), goma.topLeft, goma.size, CornerRadius(w * 0.003f))
            drawRect(t.b(0.3f), goma.topLeft + Offset(w * 0.003f, 0f), Size(goma.width * 0.3f, 1f * u))
        }
        // El canto de abajo del cuerpo, con su luz.
        val tf = tn(Offset(w * 0.4f, fondo))
        drawRect(tf.s(0.35f), Offset(xL + w * 0.008f, fondo - 1.6f * u), Size(xR - xL - w * 0.016f, 1.6f * u))
    }

    /**
     * La tapa del fichero: la chapa vista un poco desde arriba, con el brillo del esmalte, el cerco de un vaso y unos arañazos, y
     * su canto de frente redondeado, con el brillo duro arriba, el esmalte saltado en la esquina y la sombra que deja en el friso.
     */
    private fun DrawScope.tapa(w: Float, ch: Float) {
        val u = density
        val o = esNoche
        val sup = ch * SUP; val frente = ch * FRENTE; val canto = ch * CANTO
        val xL = w * IZQ - w * 0.012f; val xR = w * DER + w * 0.012f
        val tabla = Rect(xL, sup, xR, frente)
        val n = 36
        for (k in 0 until n) {
            val x0 = xL + (xR - xL) * k / n; val aw = (xR - xL) / n + 0.5f
            val tk = tn(Offset(x0 + aw / 2, (sup + frente) / 2), if (o) 0f else 0.12f * (1f - k / n.toFloat()))
            drawRect(tk.c(mezcla(ACERO_LUZ, ACERO, 0.2f)), Offset(x0, sup), Size(aw, frente - sup))
            // El canto: el plano de arriba recibe la luz; el de abajo se redondea hacia la sombra.
            val m1 = frente + (canto - frente) * 0.4f
            drawRect(tk.c(mezcla(ACERO, ACERO_LUZ, 0.45f)), Offset(x0, frente), Size(aw, m1 - frente))
            drawRect(tk.c(mezcla(ACERO, ACERO_OSC, 0.4f)), Offset(x0, m1), Size(aw, canto - m1))
            // (Colores ya mezclados y opacos: con alfa, las juntas entre tramos se verían.)
            val arriba = tk.c(mezcla(ACERO, ACERO_LUZ, 0.45f))
            drawRect(tk.sobre(arriba, tk.b(0.8f)), Offset(x0, frente), Size(aw, 1.4f * u))
            drawRect(tk.sobre(arriba, tk.b(0.3f)), Offset(x0, frente + 1.4f * u), Size(aw, 2f * u))
            drawRect(tk.sobre(tk.c(mezcla(ACERO, ACERO_OSC, 0.4f)), tk.s(0.3f)), Offset(x0, canto - 1.2f * u), Size(aw, 1.2f * u))
        }
        val t = tn(Offset(w * 0.5f, sup))
        pinceladas(tabla, if (o) NOCHE else ACERO_OSC, 5501, 10, u, 0.1f)
        drawRect(t.s(0.28f), tabla.topLeft, Size(tabla.width, 1.6f * u))
        // El brillo largo del esmalte en la tapa.
        drawRect(tn(Offset(w * 0.2f, sup)).b(0.22f), Offset(w * 0.06f, sup + (frente - sup) * 0.55f), Size(w * 0.5f, 1.4f * u))
        // El cerco de un vaso y unos arañazos.
        drawOval(tn(Offset(w * 0.25f, sup)).s(0.2f), Offset(w * 0.085f, sup + (frente - sup) * 0.42f), Size(w * 0.05f, (frente - sup) * 0.46f), style = Stroke(1f * u))
        for (k in 0 until 6) {
            val x = w * (0.05f + 0.88f * azar(5510, k))
            drawLine(tn(Offset(x, sup)).b(0.35f), Offset(x, sup + (frente - sup) * (0.25f + 0.5f * azar(5511, k))), Offset(x + w * 0.035f, sup + (frente - sup) * (0.35f + 0.4f * azar(5512, k))), 0.6f * u)
        }
        // El esmalte saltado en la esquina de la izquierda y a media tapa: asoma el metal.
        val tg = tn(Offset(xL, frente))
        drawPath(poligono(xL, frente, xL + w * 0.03f, frente, xL + w * 0.02f, frente + (canto - frente) * 0.45f, xL + w * 0.006f, canto - 1f * u, xL, canto - 1f * u), tg.c(METAL))
        val tm = tn(Offset(w * 0.62f, frente))
        drawPath(poligono(w * 0.6f, frente, w * 0.64f, frente, w * 0.63f, frente + (canto - frente) * 0.35f, w * 0.608f, frente + (canto - frente) * 0.3f), tm.c(METAL))
        // La sombra de la tapa en el friso.
        drawRect(t.s(0.34f), Offset(w * IZQ, canto), Size(w * (DER - IZQ), 2.6f * u))
        drawRect(t.s(0.15f), Offset(w * IZQ, canto + 2.6f * u), Size(w * (DER - IZQ), 3.4f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * La lámpara de mesa: la base y la columna de baquelita negra, la horquilla de latón con sus dos palomillas, la pantalla
     * alargada de vidrio verde (de noche, encendida por dentro), el casquillo y la bombilla que asoman por debajo y la cadenita
     * del interruptor.
     */
    private fun DrawScope.lampara(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val o = esNoche
        val t = tn(Offset(pie.x, pie.y - w * 0.04f))
        val baquelita = mezcla(NEGRO, OLEO_OSC, 0.12f)
        val base = Path().apply {
            moveTo(pie.x - w * 0.078f, pie.y)
            lineTo(pie.x - w * 0.078f, pie.y - w * 0.011f)
            cubicTo(pie.x - w * 0.072f, pie.y - w * 0.024f, pie.x - w * 0.036f, pie.y - w * 0.03f, pie.x - w * 0.017f, pie.y - w * 0.04f)
            lineTo(pie.x + w * 0.017f, pie.y - w * 0.04f)
            cubicTo(pie.x + w * 0.036f, pie.y - w * 0.03f, pie.x + w * 0.072f, pie.y - w * 0.024f, pie.x + w * 0.078f, pie.y - w * 0.011f)
            lineTo(pie.x + w * 0.078f, pie.y)
            cubicTo(pie.x + w * 0.04f, pie.y + w * 0.009f, pie.x - w * 0.04f, pie.y + w * 0.009f, pie.x - w * 0.078f, pie.y)
            close()
        }
        val yh = pie.y - w * 0.2f
        val col = Rect(pie.x - w * 0.0075f, yh, pie.x + w * 0.0075f, pie.y - w * 0.038f)
        val yr = pie.y - w * 0.252f
        val ya = pie.y - w * 0.348f
        val pant = Path().apply {
            moveTo(pie.x - w * 0.13f, yr)
            cubicTo(pie.x - w * 0.128f, yr - w * 0.058f, pie.x - w * 0.1f, ya, pie.x - w * 0.058f, ya)
            lineTo(pie.x + w * 0.058f, ya)
            cubicTo(pie.x + w * 0.1f, ya, pie.x + w * 0.128f, yr - w * 0.058f, pie.x + w * 0.13f, yr)
            cubicTo(pie.x + w * 0.06f, yr + w * 0.014f, pie.x - w * 0.06f, yr + w * 0.014f, pie.x - w * 0.13f, yr)
            close()
        }
        val horquilla = Path().apply {
            moveTo(pie.x - w * 0.122f, yh - w * 0.075f)
            lineTo(pie.x - w * 0.122f, yh - w * 0.03f)
            cubicTo(pie.x - w * 0.122f, yh - w * 0.004f, pie.x - w * 0.1f, yh, pie.x - w * 0.08f, yh)
            lineTo(pie.x + w * 0.08f, yh)
            cubicTo(pie.x + w * 0.1f, yh, pie.x + w * 0.122f, yh - w * 0.004f, pie.x + w * 0.122f, yh - w * 0.03f)
            lineTo(pie.x + w * 0.122f, yh - w * 0.075f)
        }
        // Su sombra en la pared (de día) y la de contacto.
        if (!o) sombraEnPared(Path().apply { addPath(base); addRect(col); addPath(pant) }, Offset(pie.x, pie.y - w * 0.15f), sup, 0.2f)
        contacto(Offset(pie.x + w * 0.012f, pie.y + w * 0.002f), w * 0.19f, w * 0.016f, 0.42f)

        // La base de baquelita, con su brillo duro y el aro de latón.
        drawPath(base, t.c(baquelita))
        clipPath(base) {
            translate(-w * 0.085f, -w * 0.003f) { drawPath(base, t.b(0.16f)) }
            drawPath(Path().apply { moveTo(pie.x - w * 0.066f, pie.y - w * 0.014f); cubicTo(pie.x - w * 0.05f, pie.y - w * 0.024f, pie.x - w * 0.034f, pie.y - w * 0.027f, pie.x - w * 0.02f, pie.y - w * 0.033f) }, t.b(0.7f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            translate(w * 0.085f, w * 0.004f) { drawPath(base, t.s(0.3f)) }
            drawRect(t.b(0.3f), Offset(pie.x - w * 0.078f, pie.y - w * 0.011f), Size(w * 0.156f, 1f * u))
        }
        // La columna, con sus dos anillos de latón.
        drawRect(t.c(baquelita), col.topLeft, col.size)
        clipRect(col.left, col.top, col.right, col.bottom) { cilindro(col, t, 1.7f) }
        val th = tn(Offset(pie.x, yh))
        listOf(pie.y - w * 0.044f, yh + w * 0.012f).forEach { y ->
            val an = Rect(pie.x - w * 0.012f, y - w * 0.006f, pie.x + w * 0.012f, y + w * 0.006f)
            drawRoundRect(t.c(LATON), an.topLeft, an.size, CornerRadius(w * 0.004f))
            clipRect(an.left, an.top, an.right, an.bottom) { cilindro(an, t, 1.6f) }
        }
        // La horquilla de latón.
        translate(1.4f * u, 1.6f * u) { drawPath(horquilla, th.s(0.3f), style = Stroke(w * 0.0085f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
        drawPath(horquilla, th.c(LATON), style = Stroke(w * 0.0085f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        translate(-0.8f * u, -0.9f * u) { drawPath(horquilla, if (o) LLAMA.copy(alpha = 0.7f) else luzMueble(0.55f), style = Stroke(1f * u, cap = StrokeCap.Round)) }

        // El casquillo y la bombilla, que asoman bajo la pantalla.
        val cas = Rect(pie.x - w * 0.012f, yr - w * 0.004f, pie.x + w * 0.012f, yr + w * 0.02f)
        drawRect(th.c(LATON), cas.topLeft, cas.size)
        clipRect(cas.left, cas.top, cas.right, cas.bottom) { cilindro(cas, th, 1.4f) }
        val bc = Offset(pie.x, yr + w * 0.03f)
        if (o) {
            drawCircle(LLAMA.copy(alpha = 0.4f), w * 0.034f, bc)
            drawOval(LLAMA, Offset(bc.x - w * 0.019f, bc.y - w * 0.014f), Size(w * 0.038f, w * 0.03f))
            drawOval(Color(0xFFFFF6DE), Offset(bc.x - w * 0.012f, bc.y - w * 0.009f), Size(w * 0.024f, w * 0.019f))
        } else {
            drawOval(mezcla(CARTULINA, OLEO, 0.25f), Offset(bc.x - w * 0.014f, bc.y - w * 0.014f), Size(w * 0.028f, w * 0.025f))
            drawOval(luzMueble(0.8f), Offset(bc.x - w * 0.01f, bc.y - w * 0.009f), Size(w * 0.008f, w * 0.007f))
            drawArc(sombraMueble(0.25f), -30f, 150f, false, Offset(bc.x - w * 0.014f, bc.y - w * 0.014f), Size(w * 0.028f, w * 0.025f), style = Stroke(1.2f * u))
        }

        // La pantalla de vidrio verde.
        val borde = Path().apply { moveTo(pie.x - w * 0.13f, yr); cubicTo(pie.x - w * 0.06f, yr + w * 0.014f, pie.x + w * 0.06f, yr + w * 0.014f, pie.x + w * 0.13f, yr) }
        if (!o) {
            drawPath(pant, VERDE)
            clipPath(pant) {
                // El lado de luz y el de sombra siguen la curva de la pantalla.
                translate(-w * 0.17f, -w * 0.006f) { drawPath(pant, luzMueble(0.16f)) }
                translate(w * 0.15f, w * 0.004f) { drawPath(pant, sombraMueble(0.26f)) }
                translate(w * 0.2f, w * 0.008f) { drawPath(pant, sombraMueble(0.22f)) }
                translate(0f, w * 0.082f) { drawPath(pant, mezcla(VERDE, VERDE_LUZ, 0.5f).copy(alpha = 0.26f)) }
                // El brillo duro del vidrio: sigue la curva del hombro de la izquierda.
                drawPath(Path().apply { moveTo(pie.x - w * 0.112f, yr - w * 0.03f); cubicTo(pie.x - w * 0.104f, yr - w * 0.066f, pie.x - w * 0.085f, ya + w * 0.014f, pie.x - w * 0.045f, ya + w * 0.012f) }, luzMueble(0.75f), style = Stroke(2f * u, cap = StrokeCap.Round))
                drawLine(luzMueble(0.4f), Offset(pie.x - w * 0.03f, ya + w * 0.012f), Offset(pie.x + w * 0.02f, ya + w * 0.012f), 1.2f * u, StrokeCap.Round)
            }
            drawPath(borde, mezcla(CARTULINA, VERDE, 0.3f), style = Stroke(1.6f * u))
        } else {
            drawPath(pant, mezcla(VERDE, VERDE_LUZ, 0.5f))
            clipPath(pant) {
                drawOval(VERDE_LUZ.copy(alpha = 0.55f), Offset(pie.x - w * 0.1f, yr - w * 0.065f), Size(w * 0.2f, w * 0.1f))
                drawOval(mezcla(VERDE_LUZ, LUZ_MUEBLE, 0.6f).copy(alpha = 0.7f), Offset(pie.x - w * 0.06f, yr - w * 0.035f), Size(w * 0.12f, w * 0.07f))
                drawRect(NOCHE.copy(alpha = 0.3f), Offset(pie.x - w * 0.13f, ya), Size(w * 0.26f, w * 0.022f))
                drawRect(NOCHE.copy(alpha = 0.28f), Offset(pie.x - w * 0.13f, ya), Size(w * 0.03f, w * 0.12f))
                drawRect(NOCHE.copy(alpha = 0.28f), Offset(pie.x + w * 0.1f, ya), Size(w * 0.03f, w * 0.12f))
            }
            drawPath(borde, Color(0xFFFFF6DE), style = Stroke(1.8f * u))
        }
        // El remate de latón de arriba y las palomillas de la horquilla.
        val tr = tn(Offset(pie.x, ya))
        drawRect(tr.c(LATON), Offset(pie.x - w * 0.013f, ya - w * 0.007f), Size(w * 0.026f, w * 0.008f))
        drawCircle(tr.c(LATON), w * 0.007f, Offset(pie.x, ya - w * 0.012f))
        drawCircle(if (o) VERDE_LUZ.copy(alpha = 0.5f) else luzMueble(0.7f), w * 0.0025f, Offset(pie.x - w * 0.002f, ya - w * 0.014f))
        listOf(-1f, 1f).forEach { s ->
            val p = Offset(pie.x + s * w * 0.125f, yh - w * 0.075f)
            drawCircle(th.s(0.3f), w * 0.011f, p + Offset(1.2f * u, 1.4f * u))
            drawCircle(th.c(LATON), w * 0.011f, p)
            drawCircle(th.c(LATON_OSC), w * 0.005f, p)
            drawCircle(if (o) LLAMA.copy(alpha = 0.8f) else luzMueble(0.7f), w * 0.003f, p - Offset(w * 0.004f, w * 0.004f))
        }
        // La cadenita del interruptor, con su bolita.
        for (k in 0..11) drawCircle(th.c(mezcla(LATON, METAL, 0.4f)), 0.9f * u, Offset(pie.x + w * 0.03f + w * 0.0015f * sin(k * 0.5f), yr + w * 0.012f + k * w * 0.0062f))
        drawCircle(th.c(LATON), w * 0.006f, Offset(pie.x + w * 0.031f, yr + w * 0.09f))
        drawCircle(if (o) LLAMA.copy(alpha = 0.8f) else luzMueble(0.7f), w * 0.002f, Offset(pie.x + w * 0.029f, yr + w * 0.088f))
    }

    /** El cordón de tela trenzada: sale de la base de la lámpara, cruza la tapa, cae por el costado y vuelve a subir a la clavija del enchufe. */
    private fun DrawScope.cordon(w: Float, pieA: Float, pieF: Float) {
        val u = density
        val xT = w * IZQ - w * 0.012f
        val enTapa = Path().apply {
            moveTo(w * 0.2f, pieA - w * 0.002f)
            cubicTo(w * 0.15f, pieA + w * 0.016f, w * 0.115f, pieF - w * 0.004f, w * 0.075f, pieA + w * 0.012f)
            cubicTo(w * 0.055f, pieA + w * 0.004f, w * 0.04f, pieA + w * 0.008f, xT, pieA + w * 0.014f)
        }
        val cuelga = Path().apply {
            moveTo(xT, pieA + w * 0.014f)
            cubicTo(w * 0.008f, pieA + w * 0.03f, w * 0.004f, w * 0.8f, w * 0.007f, w * 0.93f)
            cubicTo(w * 0.009f, w * 0.965f, w * 0.022f, w * 0.96f, w * 0.019f, w * 0.878f)
        }
        val color = mezcla(PARQUET_OSC, NEGRO, 0.35f)
        listOf(enTapa to tn(Offset(w * 0.1f, pieA)), cuelga to tn(Offset(w * 0.01f, w * 0.8f))).forEachIndexed { i, (p, t) ->
            translate(if (i == 0) 1f * u else w * 0.006f, if (i == 0) 2f * u else w * 0.004f) { drawPath(p, t.s(0.3f), style = Stroke(2f * u, cap = StrokeCap.Round)) }
            drawPath(p, t.c(color), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawPath(p, t.c(mezcla(CARTON, LATON, 0.3f)).copy(alpha = 0.45f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.2f * u))))
        }
        // La clavija de baquelita en el enchufe.
        val en = Offset(w * 0.019f, w * 0.862f)
        val t = tn(en)
        drawCircle(t.s(0.35f), w * 0.0115f, en + Offset(w * 0.003f, w * 0.004f))
        drawCircle(t.c(mezcla(NEGRO, PARQUET_OSC, 0.15f)), w * 0.011f, en)
        drawCircle(t.b(0.45f), w * 0.004f, en - Offset(w * 0.004f, w * 0.004f))
        drawRect(t.c(mezcla(NEGRO, PARQUET_OSC, 0.15f)), Offset(en.x - w * 0.004f, en.y + w * 0.006f), Size(w * 0.008f, w * 0.014f))
    }

    /**
     * El vaso de té en su podstakánnik: el vaso de vidrio con el té a tres cuartos, el portavasos de metal calado con su asa y
     * su pie, y la cucharilla que asoma. De noche el té se enciende con la bombilla y deja su mancha de luz en la tapa.
     */
    private fun DrawScope.vaso(pie: Offset) {
        val u = density
        val w = anchoW
        val o = esNoche
        val t = tn(Offset(pie.x, pie.y - w * 0.04f))
        val ra = w * 0.027f; val rb = w * 0.0235f
        val alto = w * 0.088f
        val yt = pie.y - alto
        val vidrio = poligono(pie.x - ra, yt, pie.x + ra, yt, pie.x + rb, pie.y - w * 0.006f, pie.x - rb, pie.y - w * 0.006f)
        contacto(Offset(pie.x + w * 0.014f, pie.y), w * 0.085f, w * 0.012f, 0.42f)
        if (o) drawOval(mezcla(TE, LLAMA, 0.5f).copy(alpha = 0.3f * t.k), Offset(pie.x + w * 0.012f, pie.y - w * 0.006f), Size(w * 0.06f, w * 0.012f))
        // La cucharilla, por detrás del vidrio.
        val cuch = t.c(METAL)
        drawLine(t.s(0.3f), Offset(pie.x + w * 0.012f, yt + w * 0.004f), Offset(pie.x - w * 0.016f, yt - w * 0.034f), w * 0.006f, StrokeCap.Round)
        drawLine(cuch, Offset(pie.x + w * 0.01f, yt + w * 0.006f), Offset(pie.x - w * 0.018f, yt - w * 0.036f), w * 0.0055f, StrokeCap.Round)
        drawLine(t.b(0.8f), Offset(pie.x + w * 0.008f, yt + w * 0.002f), Offset(pie.x - w * 0.019f, yt - w * 0.036f), 0.8f * u)
        // El vidrio y el té.
        drawPath(vidrio, t.c(mezcla(CARTULINA, OLEO, 0.35f)).copy(alpha = 0.55f))
        val yte = yt + alto * 0.2f
        val te = if (o) mezcla(hondo(TE), mezcla(TE, LLAMA, 0.25f), (1.4f * t.k).coerceAtMost(1f)) else TE
        clipPath(vidrio) {
            drawRect(te, Offset(pie.x - ra, yte), Size(ra * 2, alto))
            drawRect(mezcla(te, if (o) LLAMA else LUZ_MUEBLE, 0.4f), Offset(pie.x - ra, yte), Size(ra * 0.55f, alto))
            drawRect(t.s(0.25f), Offset(pie.x + ra * 0.45f, yte), Size(ra * 0.6f, alto))
            drawRect(t.b(0.75f), Offset(pie.x - ra * 0.72f, yt), Size(1.3f * u, alto))
        }
        drawOval(mezcla(te, NEGRO, 0.25f), Offset(pie.x - ra * 0.96f, yte - w * 0.004f), Size(ra * 1.92f, w * 0.008f))
        drawOval(t.b(0.7f), Offset(pie.x - ra, yt - w * 0.004f), Size(ra * 2, w * 0.008f), style = Stroke(1f * u))
        // El portavasos calado.
        val pt = pie.y - alto * 0.6f
        val porta = poligono(pie.x - ra * 0.97f, pt, pie.x + ra * 0.97f, pt, pie.x + rb * 1.04f, pie.y - w * 0.004f, pie.x - rb * 1.04f, pie.y - w * 0.004f)
        val metal = t.c(mezcla(METAL, LATON, 0.18f))
        drawPath(porta, metal)
        clipPath(porta) {
            // Los calados: dos filas de rombos por los que se ve el té.
            for (f in 0..1) for (k in 0..4) {
                val c = Offset(pie.x - ra * 0.78f + ra * 0.39f * k, pt + (pie.y - pt) * (0.36f + 0.34f * f))
                val a = ra * 0.15f; val h = (pie.y - pt) * 0.13f
                drawPath(poligono(c.x, c.y - h, c.x + a, c.y, c.x, c.y + h, c.x - a, c.y), mezcla(te, NEGRO, 0.35f))
            }
            cilindro(Rect(pie.x - ra, pt, pie.x + ra, pie.y), t, 1.5f)
            drawRect(t.b(0.5f), Offset(pie.x - ra, pt), Size(ra * 2, 1.1f * u))
            drawRect(t.s(0.3f), Offset(pie.x - ra, pt + (pie.y - pt) * 0.16f), Size(ra * 2, 1f * u))
            drawRect(t.s(0.3f), Offset(pie.x - ra, pie.y - (pie.y - pt) * 0.2f), Size(ra * 2, 1f * u))
        }
        // El pie y el asa.
        val peana = Rect(pie.x - rb * 1.25f, pie.y - w * 0.007f, pie.x + rb * 1.25f, pie.y)
        drawRoundRect(metal, peana.topLeft, peana.size, CornerRadius(w * 0.003f))
        clipRect(peana.left, peana.top, peana.right, peana.bottom) { cilindro(peana, t, 1.4f) }
        val asa = Path().apply {
            moveTo(pie.x + ra * 0.95f, pt + w * 0.006f)
            cubicTo(pie.x + ra * 2.1f, pt - w * 0.004f, pie.x + ra * 2.15f, pie.y - w * 0.014f, pie.x + rb * 1.02f, pie.y - w * 0.014f)
        }
        translate(1.2f * u, 1.4f * u) { drawPath(asa, t.s(0.3f), style = Stroke(w * 0.006f, cap = StrokeCap.Round)) }
        drawPath(asa, metal, style = Stroke(w * 0.006f, cap = StrokeCap.Round))
        translate(-0.7f * u, -0.7f * u) { drawPath(asa, t.b(0.7f), style = Stroke(0.8f * u)) }
    }

    /**
     * La almohadilla de tinta violeta en su caja de hojalata, con el sello de goma de mango torneado apoyado encima, y a su
     * lado unas fichas recién selladas.
     */
    private fun DrawScope.tampon(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x + w * 0.04f, pie.y - w * 0.02f))
        // Las fichas sueltas, vistas desde arriba, con sus renglones y la marca del sello.
        val fx = pie.x + w * 0.105f
        listOf(Triple(0.006f, -0.004f, CARTULINA_OSC), Triple(0f, 0f, CARTULINA)).forEachIndexed { i, (dx, dy, col) ->
            val f = poligono(fx + w * (dx + 0.012f), pie.y + w * (dy - 0.03f), fx + w * (dx + 0.088f), pie.y + w * (dy - 0.03f), fx + w * (dx + 0.078f), pie.y + w * (dy - 0.004f), fx + w * dx, pie.y + w * (dy - 0.004f))
            translate(1.5f * u, 1.5f * u) { drawPath(f, t.s(0.3f)) }
            drawPath(f, t.c(col))
            if (i == 1) {
                bloques(Offset(fx + w * 0.014f, pie.y - w * 0.025f), w * 0.04f, w * 0.003f, t.c(TINTA).copy(alpha = 0.6f), 5601, 3)
                drawOval(t.c(VIOLETA).copy(alpha = 0.75f), Offset(fx + w * 0.052f, pie.y - w * 0.022f), Size(w * 0.022f, w * 0.012f), style = Stroke(1.1f * u))
                drawLine(t.c(VIOLETA).copy(alpha = 0.75f), Offset(fx + w * 0.057f, pie.y - w * 0.016f), Offset(fx + w * 0.069f, pie.y - w * 0.016f), 1f * u)
            }
        }
        // La caja de hojalata: el costado, el reborde y la almohadilla empapada.
        val x0 = pie.x; val x1 = pie.x + w * 0.092f
        val ya = pie.y - w * 0.034f; val yb = pie.y - w * 0.013f
        contacto(Offset((x0 + x1) / 2 + w * 0.012f, pie.y), w * 0.115f, w * 0.01f, 0.4f)
        val lata = t.c(mezcla(OLEO_OSC, NEGRO, 0.25f))
        drawRect(lata, Offset(x0, yb), Size(x1 - x0, pie.y - yb))
        clipRect(x0, yb, x1, pie.y) { cilindro(Rect(x0, yb, x1, pie.y), t, 1.2f); drawRect(t.b(0.5f), Offset(x0, yb), Size(x1 - x0, 1f * u)) }
        val arriba = poligono(x0 + w * 0.01f, ya, x1 - w * 0.004f, ya, x1, yb, x0, yb)
        drawPath(arriba, t.c(METAL))
        val almo = poligono(x0 + w * 0.016f, ya + w * 0.004f, x1 - w * 0.01f, ya + w * 0.004f, x1 - w * 0.007f, yb - w * 0.003f, x0 + w * 0.009f, yb - w * 0.003f)
        drawPath(almo, t.c(VIOLETA))
        clipPath(almo) {
            drawOval(t.c(mezcla(VIOLETA, NEGRO, 0.4f)), Offset(x0 + w * 0.03f, ya + w * 0.005f), Size(w * 0.04f, w * 0.012f))
            drawRect(t.b(0.25f), Offset(x0, ya + w * 0.004f), Size(w * 0.03f, w * 0.02f))
        }
        // El sello: la goma, la tablilla y el mango torneado, ladeado sobre la almohadilla.
        val sp = Offset(x0 + w * 0.046f, yb - w * 0.007f)
        rotate(7f, sp) {
            val goma = Rect(sp.x - w * 0.019f, sp.y - w * 0.006f, sp.x + w * 0.019f, sp.y)
            val tabl = Rect(sp.x - w * 0.021f, sp.y - w * 0.02f, sp.x + w * 0.021f, sp.y - w * 0.006f)
            val madera = t.c(mezcla(PARQUET, TE, 0.3f))
            drawRect(t.c(mezcla(NEGRO, VIOLETA, 0.4f)), goma.topLeft, goma.size)
            drawRect(madera, tabl.topLeft, tabl.size)
            clipRect(tabl.left, tabl.top, tabl.right, tabl.bottom) { cilindro(tabl, t, 1.2f); drawRect(t.b(0.5f), tabl.topLeft, Size(tabl.width, 1f * u)) }
            val mango = Path().apply {
                moveTo(sp.x - w * 0.009f, tabl.top)
                cubicTo(sp.x - w * 0.005f, tabl.top - w * 0.01f, sp.x - w * 0.005f, tabl.top - w * 0.016f, sp.x - w * 0.013f, tabl.top - w * 0.026f)
                cubicTo(sp.x - w * 0.02f, tabl.top - w * 0.045f, sp.x + w * 0.02f, tabl.top - w * 0.045f, sp.x + w * 0.013f, tabl.top - w * 0.026f)
                cubicTo(sp.x + w * 0.005f, tabl.top - w * 0.016f, sp.x + w * 0.005f, tabl.top - w * 0.01f, sp.x + w * 0.009f, tabl.top)
                close()
            }
            drawPath(mango, madera)
            clipPath(mango) {
                cilindro(Rect(sp.x - w * 0.016f, tabl.top - w * 0.045f, sp.x + w * 0.016f, tabl.top), t, 1.4f)
                drawRect(t.s(0.25f), Offset(sp.x - w * 0.02f, tabl.top - w * 0.016f), Size(w * 0.04f, 1f * u))
            }
            drawCircle(t.b(0.8f), w * 0.003f, Offset(sp.x - w * 0.007f, tabl.top - w * 0.034f))
        }
    }

    /**
     * La pila de libros de tela, tumbados con el lomo hacia quien mira (uno, al revés, enseña el corte): cada uno de su color,
     * con la trama de la tela, sus filetes, el título estampado en bloques y el tejuelo de papel de la biblioteca; del de arriba
     * cuelga la cinta de registro.
     */
    private fun DrawScope.libros(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        // Desde abajo: dónde empieza, su ancho, su alto, su tela y si enseña el corte.
        val pila = listOf(
            listOf(0f, 0.25f, 0.038f) to GRANATE,
            listOf(0.014f, 0.214f, 0.03f) to OLEO_OSC,
            listOf(-0.008f, 0.236f, 0.035f) to mezcla(CARTON, PARQUET, 0.5f),
            listOf(0.03f, 0.188f, 0.027f) to mezcla(VERDE, NEGRO, 0.25f),
        )
        val todos = Path()
        var y = pie.y
        val cajas = pila.map { (m, _) -> Rect(pie.x + w * m[0], y - w * m[2], pie.x + w * (m[0] + m[1]), y).also { y -= w * m[2] } }
        cajas.forEach { todos.addRect(it) }
        sombraEnPared(todos, Offset(pie.x + w * 0.12f, pie.y - w * 0.06f), sup, 0.2f)
        contacto(Offset(pie.x + w * 0.14f, pie.y + w * 0.002f), w * 0.3f, w * 0.014f, 0.4f)
        cajas.forEachIndexed { i, r ->
            val t = tn(r.center)
            val tela = pila[i].second
            if (i > 0) drawRect(t.s(0.35f), Offset(r.left + w * 0.006f, r.bottom), Size(r.width, 2.2f * u))
            if (i == 1) {
                // Al revés: el corte de las hojas entre las dos tapas.
                drawRect(t.c(tela), r.topLeft, r.size)
                val hojas = Rect(r.left + w * 0.006f, r.top + w * 0.005f, r.right - w * 0.008f, r.bottom - w * 0.005f)
                drawRect(t.c(CARTULINA_OSC), hojas.topLeft, hojas.size)
                for (k in 1..4) drawRect(t.s(0.14f), Offset(hojas.left, hojas.top + hojas.height * k / 5f), Size(hojas.width, 0.7f * u))
                drawRect(t.b(0.3f), hojas.topLeft, Size(hojas.width * 0.3f, hojas.height))
                drawRect(t.s(0.2f), Offset(hojas.right - hojas.width * 0.12f, hojas.top), Size(hojas.width * 0.12f, hojas.height))
                drawRect(t.s(0.3f), Offset(hojas.left, hojas.top), Size(hojas.width, 1.2f * u))
            } else {
                drawRoundRect(t.c(tela), r.topLeft, r.size, CornerRadius(w * 0.004f))
                clipRect(r.left, r.top, r.right, r.bottom) {
                    // La trama de la tela.
                    var x = r.left
                    while (x < r.right) { drawLine(t.s(0.09f), Offset(x, r.top), Offset(x, r.bottom), 0.6f * u); x += 2.4f * u }
                    tumbado(r, t, 1.1f)
                    drawRect(t.b(0.14f), r.topLeft, Size(r.width * 0.08f, r.height))
                    drawRect(t.s(0.22f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
                    // El cajo de las tapas, arriba y abajo.
                    drawRect(t.s(0.25f), Offset(r.left, r.top + w * 0.0045f), Size(r.width, 0.9f * u))
                    drawRect(t.s(0.25f), Offset(r.left, r.bottom - w * 0.0055f), Size(r.width, 0.9f * u))
                    // Los filetes y el título estampado.
                    val oro = t.c(mezcla(LATON, LLAMA, 0.2f)).copy(alpha = 0.85f)
                    listOf(0.07f, 0.09f, 0.9f, 0.92f).forEach { f -> drawRect(oro, Offset(r.left + r.width * f, r.top + w * 0.006f), Size(1f * u, r.height - w * 0.012f)) }
                    bloques(Offset(r.left + r.width * (0.4f + 0.05f * i), r.center.y - r.height * 0.14f), r.width * (0.3f - 0.03f * i), r.height * 0.26f, oro, 5700 + i)
                    // El desgaste de la tela en el canto.
                    drawRect(t.c(mezcla(tela, CARTULINA, 0.4f)).copy(alpha = 0.5f), Offset(r.left + r.width * 0.55f, r.top), Size(r.width * 0.2f, 1.6f * u))
                }
                // El tejuelo de papel, pegado al pie del lomo.
                val tj = Rect(r.left + r.width * 0.15f, r.top + r.height * 0.16f, r.left + r.width * 0.15f + w * 0.034f, r.bottom - r.height * 0.16f)
                drawRect(t.c(CARTULINA), tj.topLeft, tj.size)
                drawRect(t.c(ROJO).copy(alpha = 0.7f), tj.topLeft, tj.size, style = Stroke(0.7f * u))
                bloques(Offset(tj.left + w * 0.005f, tj.top + tj.height * 0.24f), tj.width - w * 0.01f, tj.height * 0.2f, t.c(VIOLETA), 5710 + i, 2)
            }
        }
        // La cinta de registro del libro de arriba: sale del lomo, se curva y cuelga por delante.
        val r = cajas.last()
        val t = tn(r.center)
        val cinta = Path().apply {
            moveTo(r.left + r.width * 0.72f, r.top + 1f * u)
            cubicTo(r.left + r.width * 0.75f, r.top + w * 0.02f, r.left + r.width * 0.7f, r.top + w * 0.04f, r.left + r.width * 0.74f, r.top + w * 0.058f)
            cubicTo(r.left + r.width * 0.76f, r.top + w * 0.068f, r.left + r.width * 0.735f, r.top + w * 0.076f, r.left + r.width * 0.75f, r.top + w * 0.084f)
        }
        translate(1.4f * u, 1.2f * u) { drawPath(cinta, t.s(0.3f), style = Stroke(w * 0.006f)) }
        drawPath(cinta, t.c(ROJO), style = Stroke(w * 0.006f, cap = StrokeCap.Butt))
        translate(-0.8f * u, 0f) { drawPath(cinta, t.b(0.35f), style = Stroke(0.8f * u)) }
    }

    /**
     * El ábaco (schioty), ladeado contra la pared: el marco de madera con sus cantos, las varillas de alambre algo combadas y
     * las cuentas de madera clara, con las dos del medio oscuras, corridas a un lado y a otro como quedaron de la última cuenta;
     * una varilla lleva solo cuatro (la de los cuartos).
     */
    private fun DrawScope.abaco(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val aw = w * 0.19f; val ah = w * 0.265f
        val r = Rect(pie.x - aw / 2, pie.y - ah, pie.x + aw / 2, pie.y)
        val giro = 5.5f
        val eje = Offset(r.left, r.bottom)
        rotate(giro, eje) {
            val marcoP = Path().apply { addRect(r) }
            sombraEnPared(marcoP, r.center, sup, 0.2f)
            val g = w * 0.016f
            val dentro = Rect(r.left + g, r.top + g, r.right - g, r.bottom - g)
            // Las varillas y sus cuentas.
            val filas = 9
            val corridas = listOf(3, 7, 2, 10, 5, 1, 6, 0, 8)
            for (f in 0 until filas) {
                val y = dentro.top + dentro.height * (f + 0.5f) / filas
                val t = tn(Offset(r.center.x, y))
                val comba = w * 0.003f
                val alambre = Path().apply { moveTo(dentro.left, y); cubicTo(dentro.left + dentro.width * 0.3f, y - comba, dentro.right - dentro.width * 0.3f, y - comba, dentro.right, y) }
                drawPath(alambre, t.c(mezcla(METAL, ACERO_OSC, 0.4f)), style = Stroke(1.1f * u))
                val cuartos = f == 5
                val cuentas = if (cuartos) 4 else 10
                val izq = if (cuartos) 1 else corridas[f]
                val cwd = w * 0.0122f; val chh = dentro.height / filas * 0.84f
                for (k in 0 until cuentas) {
                    val x = if (k < izq) dentro.left + cwd * (k + 0.5f) + 1f * u else dentro.right - cwd * (cuentas - k - 0.5f) - 1f * u
                    val oscura = if (cuartos) false else (k == 4 || k == 5)
                    val col = if (oscura) mezcla(NEGRO, PARQUET_OSC, 0.35f) else mezcla(PARQUET, CARTULINA, if ((k + f) % 3 == 0) 0.3f else 0.42f)
                    val c = Offset(x, y - comba * 0.7f)
                    drawOval(t.s(0.3f), Offset(c.x - cwd * 0.4f, c.y - chh * 0.44f), Size(cwd * 0.96f, chh))
                    drawOval(t.c(col), Offset(c.x - cwd * 0.48f, c.y - chh / 2), Size(cwd * 0.96f, chh))
                    drawOval(t.s(0.2f), Offset(c.x + cwd * 0.05f, c.y - chh * 0.4f), Size(cwd * 0.4f, chh * 0.8f))
                    drawOval(t.b(0.6f), Offset(c.x - cwd * 0.3f, c.y - chh * 0.34f), Size(cwd * 0.26f, chh * 0.34f))
                }
            }
            // El marco: los dos largueros de arriba y abajo por encima de los montantes.
            val t = tn(r.center)
            val madera = mezcla(PARQUET, TE, 0.25f)
            listOf(Rect(r.left, r.top, r.left + g, r.bottom), Rect(r.right - g, r.top, r.right, r.bottom)).forEach { m ->
                drawRect(t.c(madera), m.topLeft, m.size)
                vetas(m, t.c(PARQUET_OSC).copy(alpha = 0.4f), 5720, 0.7f, vertical = true, grosor = 0.5f, ondulacion = 0.5f)
                drawRect(t.b(0.4f), m.topLeft, Size(1.2f * u, m.height))
                drawRect(t.s(0.3f), Offset(m.right - 1.4f * u, m.top), Size(1.4f * u, m.height))
                // Los agujeros de las varillas.
                for (f in 0 until filas) drawCircle(t.s(0.5f), 0.9f * u, Offset(m.center.x, dentro.top + dentro.height * (f + 0.5f) / filas))
            }
            listOf(Rect(r.left - w * 0.003f, r.top, r.right + w * 0.003f, r.top + g), Rect(r.left - w * 0.003f, r.bottom - g, r.right + w * 0.003f, r.bottom)).forEach { m ->
                drawRect(t.s(0.3f), Offset(m.left + 1.4f * u, m.bottom), Size(m.width, 1.6f * u))
                drawRect(t.c(mezcla(madera, PARQUET_OSC, 0.15f)), m.topLeft, m.size)
                vetas(m, t.c(PARQUET_OSC).copy(alpha = 0.4f), 5721, 0.7f, grosor = 0.5f, ondulacion = 0.5f)
                drawRect(t.b(0.5f), m.topLeft, Size(m.width, 1.2f * u))
                drawRect(t.s(0.3f), Offset(m.left, m.bottom - 1.3f * u), Size(m.width, 1.3f * u))
            }
            // El roce de la esquina de arriba.
            drawRect(t.c(CARTULINA).copy(alpha = 0.4f), Offset(r.right - w * 0.02f, r.top), Size(w * 0.023f, 1.6f * u))
        }
        contacto(Offset(pie.x + w * 0.02f, pie.y + w * 0.002f), w * 0.2f, w * 0.01f, 0.35f)
    }

    // ---------------------------------------------------------------- al pie

    /**
     * El paquete de fichas nuevas, envuelto en papel de estraza y atado con bramante, cortado por el borde izquierdo: sus tres
     * caras, los dobleces del envoltorio, el nudo con sus cabos, la etiqueta de la imprenta y su sello violeta.
     */
    private fun DrawScope.paquete(pie: Offset, w: Float) {
        val u = density
        val x0 = pie.x - w * 0.1f; val x1 = pie.x + w * 0.1f
        val ya = pie.y - w * 0.108f
        val fx = w * 0.026f; val fy = w * 0.02f
        val t = tn(Offset(pie.x, pie.y - w * 0.05f))
        // Su sombra en el piso, a la derecha.
        drawPath(poligono(x1, pie.y, x1 + fx, pie.y - fy, x1 + fx + w * 0.05f, pie.y - fy + w * 0.012f, x1 + w * 0.04f, pie.y + w * 0.006f, x0, pie.y + w * 0.006f, x0, pie.y), t.s(0.4f))
        val papel = mezcla(CARTON, PARQUET, 0.12f)
        // La cara de arriba, el costado y el frente.
        drawPath(poligono(x0, ya, x0 + fx, ya - fy, x1 + fx, ya - fy, x1, ya), t.c(mezcla(papel, LUZ_MUEBLE, 0.3f)))
        drawPath(poligono(x1, ya, x1 + fx, ya - fy, x1 + fx, pie.y - fy, x1, pie.y), t.c(mezcla(papel, PARQUET_OSC, 0.45f)))
        drawRect(t.c(papel), Offset(x0, ya), Size(x1 - x0, pie.y - ya))
        clipRect(x0, ya, x1, pie.y) {
            pinceladas(Rect(x0, ya, x1, pie.y), if (esNoche) NOCHE else PARQUET_OSC, 5801, 8, u, 0.14f)
            // Las arrugas del papel.
            for (k in 0 until 6) {
                val p = Offset(x0 + (x1 - x0) * azar(5802, k), ya + (pie.y - ya) * azar(5803, k))
                val q = p + Offset(w * 0.03f * (azar(5804, k) - 0.3f), w * 0.022f * (azar(5805, k) - 0.5f))
                drawLine(t.s(0.18f), p, q, 0.8f * u); drawLine(t.b(0.25f), p + Offset(0f, 1f * u), q + Offset(0f, 1f * u), 0.6f * u)
            }
            drawRect(t.b(0.3f), Offset(x0, ya), Size(x1 - x0, 1.2f * u))
            drawRect(t.s(0.2f), Offset(x0, pie.y - w * 0.012f), Size(x1 - x0, w * 0.012f))
        }
        // El doblez en triángulo del costado.
        drawPath(poligono(x1, ya, x1 + fx, ya - fy + (pie.y - ya) * 0.5f, x1, pie.y), t.s(0.16f))
        drawLine(t.b(0.3f), Offset(x1, ya), Offset(x1 + fx, ya - fy + (pie.y - ya) * 0.5f), 0.7f * u)
        drawLine(t.b(0.4f), Offset(x1, ya), Offset(x1, pie.y), 0.9f * u)
        // La etiqueta de la imprenta, con sus renglones y su sello.
        val et = Rect(x0 + w * 0.112f, ya + w * 0.02f, x0 + w * 0.192f, ya + w * 0.07f)
        drawRect(t.s(0.2f), Offset(et.left + 1f * u, et.top + 1.2f * u), et.size)
        drawRect(t.c(CARTULINA), et.topLeft, et.size)
        drawRect(t.c(ROJO), et.topLeft, Size(et.width, w * 0.006f))
        bloques(Offset(et.left + w * 0.007f, et.top + w * 0.014f), et.width * 0.6f, w * 0.005f, t.c(TINTA).copy(alpha = 0.7f), 5810, 3)
        drawCircle(t.c(VIOLETA).copy(alpha = 0.7f), w * 0.011f, Offset(et.right - w * 0.016f, et.bottom - w * 0.014f), style = Stroke(1.1f * u))
        drawCircle(t.c(VIOLETA).copy(alpha = 0.7f), w * 0.005f, Offset(et.right - w * 0.016f, et.bottom - w * 0.014f), style = Stroke(0.8f * u))
        // El bramante: una vuelta a lo ancho y otra a lo alto, el nudo y sus cabos.
        val cuerda = t.c(mezcla(CARTULINA_OSC, CARTON, 0.4f))
        val cxv = x0 + w * 0.096f; val cyh = ya + (pie.y - ya) * 0.56f
        fun hilo(a: Offset, b: Offset) { drawLine(t.s(0.3f), a + Offset(0f, 1.4f * u), b + Offset(0f, 1.4f * u), 1.4f * u); drawLine(cuerda, a, b, 1.5f * u) }
        hilo(Offset(x0, cyh), Offset(x1, cyh)); hilo(Offset(x1, cyh), Offset(x1 + fx, cyh - fy))
        hilo(Offset(cxv, ya), Offset(cxv, pie.y)); hilo(Offset(cxv, ya), Offset(cxv + fx, ya - fy))
        val nudo = Offset(cxv, cyh)
        listOf(Offset(-0.022f, 0.022f), Offset(0.016f, 0.03f)).forEach { f ->
            val cabo = Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + w * f.x * 0.2f, nudo.y + w * 0.012f, nudo.x + w * f.x * 1.2f, nudo.y + w * 0.008f, nudo.x + w * f.x, nudo.y + w * f.y) }
            drawPath(cabo, cuerda, style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        drawCircle(cuerda, 2.2f * u, nudo); drawCircle(t.s(0.3f), 1f * u, nudo + Offset(0.6f * u, 0.6f * u))
    }

    /** Una bola de papel arrugado: sus caras de luz y de sombra y un par de renglones. */
    private fun DrawScope.bolaPapel(c: Offset, r: Float, semilla: Int, t: Tono) {
        val u = density
        val n = 8
        val ps = List(n) { k ->
            val a = 2.0 * Math.PI * k / n + azar(semilla, k + 20) * 0.5
            val rr = r * (0.72f + 0.38f * azar(semilla, k))
            Offset(c.x + rr * cos(a).toFloat(), c.y + rr * 0.85f * sin(a).toFloat())
        }
        val p = Path().apply { ps.forEachIndexed { k, q -> if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
        drawPath(p, t.c(CARTULINA))
        val m = c + Offset(r * 0.12f, -r * 0.08f)
        for (k in 0 until n) {
            val a = ps[k]; val b = ps[(k + 1) % n]
            val cara = poligono(m.x, m.y, a.x, a.y, b.x, b.y)
            // Las caras que miran abajo y a la derecha, en sombra; las de arriba a la izquierda, con luz.
            val mira = ((a.x + b.x) / 2 - c.x) * 0.6f + ((a.y + b.y) / 2 - c.y)
            drawPath(cara, if (mira > 0f) t.s(0.1f + 0.2f * (mira / r).coerceIn(0f, 1f)) else t.b(0.3f * (-mira / r).coerceIn(0f, 1f)))
            drawLine(t.s(0.18f), m, a, 0.6f * u)
        }
        drawLine(t.c(VIOLETA).copy(alpha = 0.5f), c + Offset(-r * 0.5f, -r * 0.3f), c + Offset(-r * 0.05f, -r * 0.42f), 0.8f * u)
    }

    /**
     * La papelera de alambre, cortada por el borde derecho: el fondo de chapa, los alambres de atrás, los papeles arrugados que
     * se ven a través, los alambres de delante con su brillo y los tres aros; a su pie, una bola de papel que no entró.
     */
    private fun DrawScope.papelera(pie: Offset, alto: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - alto * 0.5f))
        val ra = w * 0.092f; val rb = w * 0.066f
        val ea = ra * 0.2f; val eb = rb * 0.2f
        val ya = pie.y - alto
        val yb = pie.y - eb
        // La sombra en el piso y en lo que tiene detrás.
        drawOval(t.s(0.4f), Offset(pie.x - rb * 0.9f, yb - eb * 0.6f), Size(rb * 2.6f, eb * 2.4f))
        // El fondo de chapa.
        val alambre = mezcla(ACERO_OSC, NEGRO, 0.45f)
        drawOval(t.c(alambre), Offset(pie.x - rb, yb - eb), Size(rb * 2, eb * 2))
        // Los alambres de atrás.
        val nA = 9
        for (k in 1 until nA) {
            val a = Math.PI * k / nA
            val c = cos(a).toFloat(); val s = sin(a).toFloat()
            drawLine(t.c(alambre).copy(alpha = 0.6f), Offset(pie.x + ra * c, ya - ea * s), Offset(pie.x + rb * c, yb - eb * s), 1f * u)
        }
        drawArc(t.c(alambre).copy(alpha = 0.7f), 180f, 180f, false, Offset(pie.x - ra, ya - ea), Size(ra * 2, ea * 2), style = Stroke(1.6f * u))
        // Los papeles arrugados.
        bolaPapel(Offset(pie.x - ra * 0.2f, ya + alto * 0.62f), ra * 0.52f, 5901, t)
        bolaPapel(Offset(pie.x + ra * 0.3f, ya + alto * 0.42f), ra * 0.5f, 5902, t)
        bolaPapel(Offset(pie.x - ra * 0.35f, ya + alto * 0.25f), ra * 0.46f, 5903, t)
        bolaPapel(Offset(pie.x + ra * 0.1f, ya + alto * 0.06f), ra * 0.4f, 5904, t)
        // Una hoja a medio doblar que asoma por el borde.
        val hoja = poligono(pie.x - ra * 0.75f, ya + alto * 0.12f, pie.x - ra * 0.5f, ya - alto * 0.16f, pie.x - ra * 0.12f, ya - alto * 0.1f, pie.x - ra * 0.3f, ya + alto * 0.16f)
        drawPath(hoja, t.c(CARTULINA_OSC))
        drawPath(poligono(pie.x - ra * 0.5f, ya - alto * 0.16f, pie.x - ra * 0.12f, ya - alto * 0.1f, pie.x - ra * 0.36f, ya - alto * 0.02f), t.c(CARTULINA))
        bloques(Offset(pie.x - ra * 0.56f, ya + alto * 0.0f), ra * 0.3f, alto * 0.018f, t.c(TINTA).copy(alpha = 0.45f), 5905, 2)
        // Los alambres de delante.
        val nD = 11
        for (k in 1 until nD) {
            val a = Math.PI * k / nD
            val c = cos(a).toFloat(); val s = sin(a).toFloat()
            val p0 = Offset(pie.x + ra * c, ya + ea * s); val p1 = Offset(pie.x + rb * c, yb + eb * s)
            drawLine(t.c(alambre), p0, p1, 1.5f * u)
            if (c < 0.3f) drawLine(t.b(0.5f), p0 + Offset(-0.5f * u, 0f), p1 + Offset(-0.5f * u, 0f), 0.6f * u)
        }
        // Los aros: el de arriba (grueso), el del medio y el del fondo.
        listOf(0f to 2.4f, 0.5f to 1.4f, 1f to 2f).forEach { (f, grosor) ->
            val rr = ra + (rb - ra) * f; val ee = ea + (eb - ea) * f; val yy = ya + (yb - ya) * f
            drawArc(t.c(alambre), 0f, 180f, false, Offset(pie.x - rr, yy - ee), Size(rr * 2, ee * 2), style = Stroke(grosor * u))
            drawArc(t.b(0.5f), 95f, 80f, false, Offset(pie.x - rr, yy - ee - 0.6f * u), Size(rr * 2, ee * 2), style = Stroke(0.7f * u))
        }
        // La bola que no entró.
        val bp = Offset(pie.x - ra * 1.25f, pie.y - w * 0.006f)
        contacto(bp + Offset(w * 0.01f, w * 0.012f), w * 0.05f, w * 0.008f, 0.4f)
        bolaPapel(bp, w * 0.02f, 5906, tn(bp))
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = (s xor 53481) * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    /** Las clases de cajón: el normal, el entreabierto y el de otra partida; la carpeta de lo que no tiene tema y el lugar libre. */
    private const val NORMAL = 0
    private const val ENTREABIERTO = 1
    private const val OTRA_PARTIDA = 2
    private const val CARPETA = 3
    private const val LIBRE = 4

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este cajón en el fichero (en fracciones del ancho), para saber cuánto le llega de la lámpara.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.03f else 0f)
        val t = Tono(oscuro, k, false, dia)
        val m = mezclar(c.semilla)
        val clase = when { c.vacio -> LIBRE; c.sinTema -> CARPETA; else -> Math.floorMod(m, 3) }
        val n = c.n.coerceIn(0, 8)
        // Cuánto asoma el cajón: más con más fichas; el entreabierto, bastante más.
        val sale = when (clase) { LIBRE -> 0.2f; CARPETA -> 0.25f; ENTREABIERTO -> 0.19f + 0.011f * n; else -> 0.08f + 0.011f * n }
        val baja = h * SALIDA * ab
        val top = h * sale + baja
        val esmalte = when (clase) {
            OTRA_PARTIDA -> when (Math.floorMod(m ushr 3, 3)) { 0 -> mezcla(CARTULINA_OSC, CARTON, 0.22f); 1 -> mezcla(VERDE, NEGRO, 0.3f); else -> mezcla(GRANATE, CARTON, 0.3f) }
            CARPETA -> ACERO
            else -> when (Math.floorMod(m ushr 3, 3)) { 0 -> ACERO; 1 -> mezcla(OLEO, ACERO, 0.22f); else -> mezcla(mezcla(CARTON, LATON, 0.3f), ACERO, 0.2f) }
        }
        dentroDelCajon(w, h, top, c, n, m, clase, t)
        val e = w * 0.012f * ab
        val f = Rect(-e, top, w + e, h + baja + h * 0.03f * ab)
        if (ab > 0f) drawRect(t.s(0.3f * ab), Offset(f.left + w * 0.012f, f.bottom), Size(f.width, h * 0.05f * ab))
        frente(f, w, h, baja, esmalte, c, m, clase, t)
        if (clase == CARPETA) carpeta(w, h, top, t)
        if (clase == LIBRE) varilla(w, h, top, t)
    }

    /**
     * Lo que se ve por encima del frente de un cajón sacado: el hueco del fichero en sombra (más hondo arriba y a la derecha),
     * los costados y el fondo del cajón, y las fichas de pie, una tras otra, con sus pestañas de colores.
     */
    private fun DrawScope.dentroDelCajon(w: Float, h: Float, top: Float, c: EstadoCajon, n: Int, m: Int, clase: Int, t: Tono) {
        val u = density
        // El hueco.
        drawRect(t.c(mezcla(ACERO_OSC, NEGRO, 0.72f)), Offset.Zero, Size(w, top + 2f * u))
        drawRect(t.s(0.45f), Offset.Zero, Size(w, h * 0.035f))
        drawRect(t.s(0.25f), Offset(0f, h * 0.035f), Size(w, h * 0.04f))
        drawRect(t.s(0.3f), Offset(w * 0.9f, 0f), Size(w * 0.1f, top))
        // El cajón: el fondo, los costados (el izquierdo recibe la luz) y la trasera.
        val yb = minOf(h * 0.05f, top * 0.4f)
        val chapa = mezcla(ACERO, ACERO_OSC, 0.55f)
        drawPath(poligono(w * 0.07f, yb, w * 0.93f, yb, w * 0.965f, top, w * 0.035f, top), t.c(mezcla(chapa, NEGRO, 0.35f)))
        drawPath(poligono(w * 0.035f, top, w * 0.07f, yb, w * 0.088f, yb, w * 0.06f, top), t.c(mezcla(ACERO_LUZ, ACERO, 0.4f)))
        drawPath(poligono(w * 0.965f, top, w * 0.93f, yb, w * 0.912f, yb, w * 0.94f, top), t.c(chapa))
        drawRect(t.c(chapa), Offset(w * 0.07f, yb), Size(w * 0.86f, 1.6f * u))
        drawRect(t.b(0.35f), Offset(w * 0.07f, yb), Size(w * 0.86f, 0.8f * u))
        // La sombra del larguero de arriba dentro del cajón.
        drawPath(poligono(w * 0.07f, yb, w * 0.93f, yb, w * 0.945f, yb + (top - yb) * 0.4f, w * 0.055f, yb + (top - yb) * 0.4f), t.s(0.3f))
        if (clase == LIBRE || clase == CARPETA) {
            // Sin fichas: se ve el fondo, con el carril de la varilla.
            drawLine(t.s(0.4f), Offset(w * 0.5f, yb + 2f * u), Offset(w * 0.5f, top), 2.4f * u)
            return
        }
        // Las fichas, de atrás adelante: cada una asoma un poco por encima de la siguiente.
        val asoma = h * 0.05f
        val fondo = top - yb - asoma - h * 0.012f
        val paso = if (n <= 1) 0f else minOf(h * 0.034f, fondo / (n - 1))
        val colores = listOf(ROJO, mezcla(OLEO, OLEO_OSC, 0.4f), mezcla(LATON, LLAMA, 0.35f), mezcla(VERDE, VERDE_LUZ, 0.25f), mezcla(VIOLETA, CARTULINA, 0.25f))
        val sacada = if (clase == ENTREABIERTO && n >= 2) n / 2 else -1
        for (i in n - 1 downTo 0) {
            val y = top - asoma - i * paso
            val x0 = w * (0.095f + 0.012f * azar(c.semilla, i)); val x1 = w * (0.905f - 0.012f * azar(c.semilla, i + 40))
            val papel = when (Math.floorMod(m ushr i, 4)) { 0 -> CARTULINA; 1 -> CARTULINA_OSC; 2 -> mezcla(CARTULINA, CARTON, 0.18f); else -> mezcla(CARTULINA, LUZ_MUEBLE, 0.3f) }
            val conPestana = n == 1 || Math.floorMod(m ushr (i + 2), 5) < 3
            val color = colores[Math.floorMod((m ushr (2 * i + 5)) + i, colores.size)]
            val tw = w * 0.125f
            val xt = x0 + (x1 - x0 - tw) * (Math.floorMod((m ushr (3 * i + 1)) + 2 * i, 5) / 4f)
            if (i == sacada) {
                // La ficha a medio sacar: sube, se ladea y enseña sus renglones.
                val sube = h * 0.11f
                rotate(-4f + 8f * azar(c.semilla, 77), Offset((x0 + x1) / 2, top)) {
                    drawRect(t.s(0.3f), Offset(x0 + w * 0.02f + 1.6f * u, y - sube + 1.6f * u), Size((x1 - x0) * 0.86f, top - y + sube))
                    drawRect(t.c(mezcla(CARTULINA, LUZ_MUEBLE, 0.35f)), Offset(x0 + w * 0.02f, y - sube), Size((x1 - x0) * 0.86f, top - y + sube))
                    drawRect(t.c(ROJO).copy(alpha = 0.6f), Offset(x0 + w * 0.02f, y - sube + h * 0.03f), Size((x1 - x0) * 0.86f, 0.8f * u))
                    bloques(Offset(x0 + w * 0.06f, y - sube + h * 0.05f), (x1 - x0) * 0.6f, h * 0.014f, t.c(VIOLETA).copy(alpha = 0.7f), c.semilla + 5, 3)
                    drawRect(t.b(0.5f), Offset(x0 + w * 0.02f, y - sube), Size((x1 - x0) * 0.86f, 0.9f * u))
                }
                continue
            }
            drawRect(t.s(0.3f), Offset(x0, y - 1.6f * u), Size(x1 - x0, 1.6f * u))
            if (conPestana) {
                drawRoundRect(t.s(0.3f), Offset(xt + 1.4f * u, y - h * 0.04f), Size(tw, h * 0.05f), CornerRadius(1.6f * u))
                drawRoundRect(t.c(color), Offset(xt, y - h * 0.042f), Size(tw, h * 0.06f), CornerRadius(1.6f * u))
                drawRect(t.b(0.45f), Offset(xt + 1.6f * u, y - h * 0.042f), Size(tw - 3.2f * u, 0.9f * u))
                bloques(Offset(xt + tw * 0.16f, y - h * 0.026f), tw * 0.68f, h * 0.012f, t.c(if (color == ROJO || i % 2 == 0) CARTULINA else TINTA).copy(alpha = 0.8f), c.semilla + i)
            }
            drawRect(t.c(papel), Offset(x0, y), Size(x1 - x0, top - y + 2f * u))
            drawRect(t.b(0.5f), Offset(x0, y), Size(x1 - x0, 0.9f * u))
            drawRect(t.s(0.12f), Offset(x0 + (x1 - x0) * 0.82f, y), Size((x1 - x0) * 0.18f, top - y))
        }
    }

    /**
     * El frente de un cajón: la chapa esmaltada con su reborde estampado, el brillo duro del esmalte, los desconchones donde
     * asoma el metal, el portaetiquetas de latón con la tarjeta (el rótulo), el tirador y el botón de la varilla. El de otra
     * partida lleva otro tirador, el portaetiquetas torcido y un número a plantilla.
     */
    private fun DrawScope.frente(f: Rect, w: Float, h: Float, baja: Float, esmalte: Color, c: EstadoCajon, m: Int, clase: Int, t: Tono) {
        val u = density
        val rad = w * 0.02f
        val forma = Path().apply { addRoundRect(RoundRect(f.left, f.top, f.right, f.bottom, CornerRadius(rad, rad), CornerRadius(rad, rad), CornerRadius(rad * 0.4f, rad * 0.4f), CornerRadius(rad * 0.4f, rad * 0.4f))) }
        val oscuroEsmalte = esmalte.red + esmalte.green + esmalte.blue < 1.1f
        // El canto de arriba del frente (su grosor, visto desde arriba), que recibe la luz.
        drawRoundRect(t.c(mezcla(esmalte, ACERO_LUZ, 0.5f)), Offset(f.left + w * 0.006f, f.top - h * 0.022f), Size(f.width - w * 0.012f, h * 0.05f), CornerRadius(rad * 0.6f))
        drawRect(t.b(0.5f), Offset(f.left + w * 0.02f, f.top - h * 0.022f), Size(f.width - w * 0.04f, 0.9f * u))
        drawPath(forma, t.c(esmalte))
        clipPath(forma) {
            // El esmalte: la luz arriba a la izquierda, su brillo duro y la sombra hacia abajo y a la derecha.
            drawRect(t.b(0.07f), Offset(f.left, f.top), Size(f.width * 0.3f, f.height))
            drawRect(t.b(if (oscuroEsmalte) 0.2f else 0.3f), Offset(f.left + f.width * 0.045f, f.top), Size(1.5f * u, f.height))
            drawRect(t.b(0.1f), Offset(f.left + f.width * 0.058f, f.top), Size(w * 0.016f, f.height))
            drawRect(t.s(0.07f), Offset(f.left + f.width * 0.74f, f.top), Size(f.width * 0.26f, f.height))
            drawRect(t.s(0.08f), Offset(f.left + f.width * 0.9f, f.top), Size(f.width * 0.1f, f.height))
            drawRect(t.s(0.09f), Offset(f.left, f.bottom - h * 0.16f), Size(f.width, h * 0.16f))
            // El canto: luz arriba y a la izquierda; sombra abajo y a la derecha.
            drawRect(t.b(0.6f), f.topLeft, Size(f.width, 1.4f * u))
            drawRect(t.b(0.18f), Offset(f.left, f.top + 1.4f * u), Size(f.width, 2.4f * u))
            drawRect(t.b(0.35f), f.topLeft, Size(1.2f * u, f.height))
            drawRect(t.s(0.35f), Offset(f.left, f.bottom - 1.6f * u), Size(f.width, 1.6f * u))
            drawRect(t.s(0.3f), Offset(f.right - 1.6f * u, f.top), Size(1.6f * u, f.height))
        }
        // El reborde estampado de la chapa.
        val g = w * 0.022f
        val reb = Rect(f.left + g, f.top + g * 0.9f, f.right - g, maxOf(f.top + g * 2f, minOf(f.bottom, h + baja) - g * 0.8f))
        drawRoundRect(t.s(0.2f), reb.topLeft, reb.size, CornerRadius(rad * 0.6f), style = Stroke(1f * u))
        drawRoundRect(t.b(0.22f), reb.topLeft + Offset(1f * u, 1f * u), reb.size, CornerRadius(rad * 0.6f), style = Stroke(0.7f * u))
        // Los desconchones del esmalte: en el canto de arriba, por donde se agarra, y junto al tirador.
        for (k in 0 until 3) {
            val x = f.left + f.width * (0.12f + 0.76f * azar(m, k + 60))
            val a = w * (0.012f + 0.02f * azar(m, k + 63))
            drawPath(poligono(x, f.top, x + a, f.top, x + a * 0.7f, f.top + h * 0.02f, x + a * 0.2f, f.top + h * 0.028f), t.c(METAL))
        }
        val py = h * 0.835f + baja
        val rz = Offset(w * (0.36f + 0.3f * azar(m, 70)), py + h * 0.03f)
        drawOval(t.c(METAL).copy(alpha = 0.7f), rz, Size(w * 0.03f, h * 0.022f))

        // El portaetiquetas.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        val torcido = clase == OTRA_PARTIDA
        val giro = if (torcido) (if (Math.floorMod(m ushr 6, 2) == 0) -1.4f else 1.3f) else 0f
        val holgura = if (torcido) h * 0.035f else h * 0.012f
        rotate(giro, Offset(if (giro < 0f) r.right else r.left, r.center.y)) {
            val b = w * 0.014f
            val marco = Rect(r.left - b, r.top - holgura, r.right + b, r.bottom + holgura + b * 0.9f)
            drawRect(t.s(0.3f), Offset(marco.left + 1.6f * u, marco.top + h * 0.02f), Size(marco.width, marco.height - h * 0.02f + 2f * u))
            val laton = t.c(if (clase == OTRA_PARTIDA && Math.floorMod(m ushr 7, 2) == 0) mezcla(METAL, ACERO, 0.25f) else LATON)
            // Los lados y el fondo del marco: la tarjeta entra por arriba.
            drawRect(laton, marco.topLeft, Size(b, marco.height))
            drawRect(laton, Offset(marco.right - b, marco.top), Size(b, marco.height))
            drawRect(laton, Offset(marco.left, marco.bottom - b * 0.9f), Size(marco.width, b * 0.9f))
            // Las orejas con sus tornillos (al torcido se le fue uno).
            listOf(marco.left - b * 1.1f, marco.right + b * 0.1f).forEachIndexed { i, x ->
                drawRoundRect(laton, Offset(x, marco.center.y - b * 0.9f), Size(b, b * 1.8f), CornerRadius(b * 0.3f))
                val p = Offset(x + b / 2, marco.center.y)
                if (torcido && i == (if (giro < 0f) 0 else 1)) drawCircle(t.s(0.55f), b * 0.24f, p)
                else { drawCircle(t.c(LATON_OSC), b * 0.3f, p); drawCircle(t.b(0.5f), b * 0.12f, p - Offset(b * 0.1f, b * 0.1f)) }
            }
            if (c.vacio) {
                // Sin tarjeta: se ve la chapa, más limpia donde estuvo.
                drawRect(t.c(mezcla(esmalte, ACERO_LUZ, 0.3f)), Offset(r.left, r.top - holgura), Size(r.width, r.height + 2 * holgura))
                drawRect(t.s(0.22f), Offset(r.left, r.top - holgura), Size(r.width, h * 0.03f))
            } else {
                // La tarjeta: de noche, más apagada cuanto más lejos de la lámpara.
                val base = when (Math.floorMod(m ushr 9, 4)) { 0 -> mezcla(CARTULINA, OLEO, 0.14f); 1 -> mezcla(CARTULINA, LATON, 0.16f); else -> CARTULINA }
                val papel = if (t.o) mezcla(mezcla(base, PAPEL_LEJOS, 0.94f), mezcla(base, LLAMA, 0.12f), ((t.k - 0.04f) * 1.6f).coerceIn(0f, 1f).pow(0.7f)) else t.c(base)
                val tj = Rect(r.left, r.top - holgura - h * 0.03f, r.right, r.bottom + holgura)
                drawRect(papel, tj.topLeft, tj.size)
                // El canto de la tarjeta que asoma por arriba, con su raya de color.
                val raya = when (Math.floorMod(m ushr 11, 3)) { 0 -> ROJO; 1 -> mezcla(OLEO, OLEO_OSC, 0.5f); else -> mezcla(VIOLETA, CARTULINA, 0.2f) }
                drawRect(t.c(raya).copy(alpha = 0.85f), Offset(tj.left, tj.top + h * 0.012f), Size(tj.width, h * 0.012f))
                drawRect(t.s(0.14f), Offset(tj.left, tj.top + h * 0.03f), Size(tj.width, h * 0.022f))
                drawRect(t.s(0.1f), Offset(tj.right - tj.width * 0.04f, tj.top), Size(tj.width * 0.04f, tj.height))
            }
            // El brillo del marco.
            drawRect(t.b(0.55f), marco.topLeft, Size(1f * u, marco.height))
            drawRect(t.b(0.3f), Offset(marco.right - b, marco.top), Size(1f * u, marco.height))
            drawRect(t.b(0.5f), Offset(marco.left, marco.bottom - b * 0.9f), Size(marco.width, 0.9f * u))
            drawRect(t.s(0.3f), Offset(marco.left, marco.bottom - 1f * u), Size(marco.width, 1f * u))
            // Los labios de arriba, que sujetan la tarjeta por las esquinas.
            listOf(marco.left, marco.right - w * 0.05f).forEach { x -> drawRect(laton, Offset(x, marco.top), Size(w * 0.05f, b * 0.7f)); drawRect(t.b(0.5f), Offset(x, marco.top), Size(w * 0.05f, 0.9f * u)) }
        }

        // El tirador y el botón de la varilla.
        val cx = w * 0.5f
        when {
            clase == OTRA_PARTIDA && Math.floorMod(m ushr 3, 3) == 0 -> {
                // El tirador de puente, niquelado, sobre sus dos pies.
                val a = w * 0.115f
                val barra = Rect(cx - a, py - h * 0.022f, cx + a, py + h * 0.022f)
                drawRoundRect(t.s(0.35f), Offset(barra.left + 1.6f * u, barra.top + h * 0.03f), barra.size, CornerRadius(h * 0.022f))
                listOf(cx - a * 0.86f, cx + a * 0.86f).forEach { x -> drawCircle(t.c(mezcla(METAL, ACERO_OSC, 0.4f)), h * 0.034f, Offset(x, py + h * 0.008f)) }
                drawRoundRect(t.c(METAL), barra.topLeft, barra.size, CornerRadius(h * 0.022f))
                drawRect(t.b(0.8f), Offset(barra.left + h * 0.02f, barra.top + 1f * u), Size(barra.width - h * 0.04f, 1.1f * u))
                drawRect(t.s(0.3f), Offset(barra.left + h * 0.02f, barra.bottom - 1.6f * u), Size(barra.width - h * 0.04f, 1.6f * u))
            }
            clase == OTRA_PARTIDA -> {
                // El pomo: de latón en el verde botella, de baquelita negra en el de minio.
                val deLaton = Math.floorMod(m ushr 3, 3) == 1
                val rp = h * 0.064f
                val p = Offset(cx, py)
                drawCircle(t.s(0.38f), rp, p + Offset(1.8f * u, h * 0.024f))
                drawCircle(t.c(if (deLaton) LATON_OSC else mezcla(NEGRO, PARQUET_OSC, 0.25f)), rp, p)
                if (deLaton) drawCircle(t.c(LATON), rp * 0.8f, p - Offset(rp * 0.1f, rp * 0.1f))
                drawCircle(t.b(0.3f), rp * 0.7f, p - Offset(rp * 0.08f, rp * 0.08f), style = Stroke(0.9f * u))
                drawCircle(t.b(0.85f), rp * 0.2f, p - Offset(rp * 0.4f, rp * 0.42f))
                drawCircle(t.c(if (deLaton) LATON_OSC else LATON_OSC), rp * 0.16f, p)
            }
            else -> {
                // El tirador de concha: la cazoleta, su hueco en sombra y las orejas atornilladas.
                val a = w * 0.095f; val bh = h * 0.062f
                val concha = Path().apply { moveTo(cx - a, py + bh); cubicTo(cx - a, py - bh * 1.45f, cx + a, py - bh * 1.45f, cx + a, py + bh); close() }
                val hueco = Path().apply { moveTo(cx - a * 0.8f, py + bh); cubicTo(cx - a * 0.8f, py - bh * 0.3f, cx + a * 0.8f, py - bh * 0.3f, cx + a * 0.8f, py + bh); close() }
                translate(1.8f * u, h * 0.022f) { drawPath(concha, t.s(0.36f)) }
                listOf(cx - a - w * 0.018f, cx + a - w * 0.004f).forEach { x ->
                    drawRoundRect(t.c(LATON), Offset(x, py + bh - h * 0.045f), Size(w * 0.022f, h * 0.045f), CornerRadius(1.4f * u))
                    drawCircle(t.c(LATON_OSC), 1.1f * u, Offset(x + w * 0.011f, py + bh - h * 0.022f))
                }
                drawPath(concha, t.c(LATON))
                clipPath(concha) {
                    drawRect(t.b(0.3f), Offset(cx - a, py - bh), Size(a * 0.7f, bh * 2))
                    drawRect(t.s(0.25f), Offset(cx + a * 0.4f, py - bh), Size(a * 0.6f, bh * 2))
                    drawPath(hueco, t.c(mezcla(LATON_OSC, NEGRO, 0.5f)))
                    drawPath(Path().apply { moveTo(cx - a * 0.78f, py + bh * 0.2f); cubicTo(cx - a * 0.7f, py - bh * 0.85f, cx - a * 0.1f, py - bh * 0.95f, cx + a * 0.2f, py - bh * 0.9f) }, t.b(0.8f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                }
                drawRect(t.s(0.3f), Offset(cx - a, py + bh - 1f * u), Size(a * 2, 1.2f * u))
            }
        }
        val bx = if (clase == OTRA_PARTIDA) w * 0.13f else w * 0.87f
        val bp = Offset(bx, py + h * 0.015f)
        val rb = h * 0.042f
        if (c.vacio) {
            // La varilla está fuera: queda su agujero.
            drawCircle(t.c(mezcla(NEGRO, ACERO_OSC, 0.3f)), rb * 0.6f, bp)
            drawArc(t.b(0.5f), 20f, 120f, false, bp - Offset(rb * 0.6f, rb * 0.6f), Size(rb * 1.2f, rb * 1.2f), style = Stroke(0.9f * u))
        } else {
            drawCircle(t.s(0.36f), rb, bp + Offset(1.4f * u, h * 0.014f))
            drawCircle(t.c(LATON_OSC), rb, bp)
            drawCircle(t.c(LATON), rb * 0.68f, bp - Offset(rb * 0.1f, rb * 0.1f))
            drawCircle(t.b(0.8f), rb * 0.2f, bp - Offset(rb * 0.34f, rb * 0.36f))
        }
        // En el de otra partida, su número a plantilla.
        if (clase == OTRA_PARTIDA) bloques(Offset(w * 0.8f, py - h * 0.01f), w * 0.09f, h * 0.05f, t.c(if (oscuroEsmalte) CARTULINA else NEGRO).copy(alpha = 0.5f), m)

        // De noche, el filo de la lámpara en el canto de arriba del frente.
        if (t.o && t.k > 0.06f) {
            drawRect(LLAMA.copy(alpha = (0.8f * t.k).coerceAtMost(0.7f)), Offset(f.left + rad, f.top - h * 0.022f), Size(f.width - 2 * rad, 1.4f * u))
            drawRect(LLAMA.copy(alpha = (0.22f * t.k).coerceAtMost(0.25f)), Offset(f.left + rad, f.top), Size(f.width - 2 * rad, h * 0.04f))
        }
    }

    /**
     * La carpeta de lo que no tiene tema: de cartón gris pardo, atravesada sobre el cajón, con su lomo de tela, el rótulo
     * impreso en bloques, sus renglones, un sello violeta, las hojas que asoman y las cintas de atar, que caen por delante.
     */
    private fun DrawScope.carpeta(w: Float, h: Float, top: Float, t: Tono) {
        val u = density
        val r = Rect(w * 0.04f, top - h * 0.24f, w * 0.99f, top + h * 0.075f)
        rotate(-3.2f, r.center) {
            drawRect(t.s(0.32f), Offset(r.left + w * 0.012f, r.top + h * 0.035f), r.size)
            // Las hojas que asoman por arriba y por la derecha.
            drawRect(t.c(CARTULINA), Offset(r.left + w * 0.06f, r.top - h * 0.018f), Size(r.width * 0.8f, h * 0.05f))
            drawRect(t.c(CARTULINA_OSC), Offset(r.left + w * 0.12f, r.top - h * 0.03f), Size(r.width * 0.6f, h * 0.05f))
            val carton = mezcla(CARTON, ACERO, 0.3f)
            drawRect(t.c(carton), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                pinceladas(r, if (t.o) NOCHE else PARQUET_OSC, 6001, 6, u, 0.12f)
                drawRect(t.b(0.16f), r.topLeft, Size(r.width, r.height * 0.3f))
                drawRect(t.s(0.16f), Offset(r.left, r.bottom - r.height * 0.25f), Size(r.width, r.height * 0.25f))
                // El lomo de tela, a la izquierda.
                drawRect(t.c(mezcla(OLEO_OSC, NEGRO, 0.3f)), r.topLeft, Size(r.width * 0.07f, r.height))
                drawRect(t.b(0.2f), Offset(r.left + r.width * 0.07f, r.top), Size(1f * u, r.height))
                // El rótulo impreso (cuatro bloques gruesos), su subrayado y los renglones de puntos.
                val tinta = t.c(NEGRO).copy(alpha = 0.8f)
                val bx = r.left + r.width * 0.36f; val bw = r.height * 0.26f
                for (k in 0 until 4) drawRect(tinta, Offset(bx + k * bw * 1.35f, r.top + r.height * 0.14f), Size(bw * (if (k == 2) 0.8f else 1f), r.height * 0.3f))
                drawRect(tinta, Offset(bx - bw * 0.4f, r.top + r.height * 0.52f), Size(bw * 6.1f, 1.2f * u))
                for (j in 0..1) drawLine(tinta.copy(alpha = 0.5f), Offset(r.left + r.width * 0.16f, r.top + r.height * (0.68f + 0.14f * j)), Offset(r.left + r.width * 0.8f, r.top + r.height * (0.68f + 0.14f * j)), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2f * u)))
                bloques(Offset(r.left + r.width * 0.13f, r.top + r.height * 0.18f), r.width * 0.16f, r.height * 0.07f, tinta.copy(alpha = 0.6f), 6002, 2)
                // El sello violeta, torcido.
                val sc = Offset(r.left + r.width * 0.86f, r.top + r.height * 0.36f)
                drawCircle(t.c(VIOLETA).copy(alpha = 0.7f), r.height * 0.22f, sc, style = Stroke(1.2f * u))
                drawCircle(t.c(VIOLETA).copy(alpha = 0.7f), r.height * 0.13f, sc, style = Stroke(0.8f * u))
                // Las puntas gastadas.
                drawPath(poligono(r.right, r.top, r.right - w * 0.03f, r.top, r.right, r.top + w * 0.03f), t.c(mezcla(carton, CARTULINA, 0.5f)))
            }
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            // Las cintas de atar: una cae por delante, a la derecha, con su vuelta; la otra queda sobre la tapa.
            val cinta = t.c(mezcla(CARTULINA_OSC, CARTON, 0.3f))
            val c1 = Path().apply {
                moveTo(r.right - w * 0.012f, r.center.y)
                cubicTo(r.right - w * 0.0f, r.center.y + h * 0.1f, r.right - w * 0.05f, r.bottom + h * 0.06f, r.right - w * 0.026f, r.bottom + h * 0.16f)
                cubicTo(r.right - w * 0.012f, r.bottom + h * 0.22f, r.right - w * 0.04f, r.bottom + h * 0.25f, r.right - w * 0.03f, r.bottom + h * 0.31f)
            }
            val c2 = Path().apply {
                moveTo(r.right - w * 0.012f, r.center.y)
                cubicTo(r.right - w * 0.07f, r.center.y - h * 0.06f, r.right - w * 0.1f, r.center.y + h * 0.07f, r.right - w * 0.16f, r.center.y + h * 0.03f)
            }
            listOf(c1, c2).forEach { p ->
                translate(1.6f * u, 1.8f * u) { drawPath(p, t.s(0.3f), style = Stroke(w * 0.011f, cap = StrokeCap.Butt)) }
                drawPath(p, cinta, style = Stroke(w * 0.011f, cap = StrokeCap.Butt))
                translate(-w * 0.003f, 0f) { drawPath(p, t.b(0.35f), style = Stroke(0.8f * u)) }
                translate(w * 0.003f, 0f) { drawPath(p, t.s(0.2f), style = Stroke(0.8f * u)) }
            }
            drawCircle(t.c(METAL), w * 0.006f, Offset(r.right - w * 0.012f, r.center.y)); drawCircle(t.s(0.5f), w * 0.0025f, Offset(r.right - w * 0.012f, r.center.y))
        }
    }

    /** La varilla suelta de un cajón libre: la barra de acero atravesada sobre el cajón, con su botón de latón y la punta roscada. */
    private fun DrawScope.varilla(w: Float, h: Float, top: Float, t: Tono) {
        val u = density
        val a = Offset(w * 0.2f, top - h * 0.115f); val b = Offset(w * 0.8f, top + h * 0.035f)
        drawLine(t.s(0.35f), a + Offset(2f * u, h * 0.03f), b + Offset(2f * u, h * 0.03f), 2.4f * u, StrokeCap.Round)
        drawLine(t.c(METAL), a, b, 2.6f * u, StrokeCap.Round)
        drawLine(t.b(0.8f), a + Offset(0f, -0.8f * u), b + Offset(0f, -0.8f * u), 0.8f * u)
        // La punta roscada y el botón.
        for (k in 0..4) drawLine(t.s(0.4f), a + Offset(k * 2.2f * u, -1.6f * u + k * 0.55f * u), a + Offset(k * 2.2f * u + 0.6f * u, 1.8f * u + k * 0.55f * u), 0.7f * u)
        val rb = h * 0.045f
        drawCircle(t.s(0.36f), rb, b + Offset(1.6f * u, h * 0.02f))
        drawCircle(t.c(LATON_OSC), rb, b)
        drawCircle(t.c(LATON), rb * 0.68f, b - Offset(rb * 0.1f, rb * 0.1f))
        drawCircle(t.b(0.8f), rb * 0.2f, b - Offset(rb * 0.34f, rb * 0.36f))
    }

    override val rotulo = Rect(0.07f, 0.37f, 0.93f, 0.66f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.russo
    override val salida = SALIDA
    override val placa = Rect(0.21f, 0.07f, 0.79f, 0.25f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del cajón de acero, con la varilla que corre al centro.
        drawRect(t(ACERO_OSC, oscuro))
        for (k in 0 until 30) drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, h * azar(31, k)), Offset(w, h * azar(31, k) + 2 * u), 1f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(w / 2 - 6 * u, 0f), Size(12 * u, h))
        drawRect(t(ACERO_LUZ, oscuro), Offset(w / 2 - 2.5f * u, 0f), Size(5 * u, h))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(w / 2 - 1 * u, 0f), Offset(w / 2 - 1 * u, h), 1f * u)
        drawCircle(t(LATON, oscuro), 7 * u, Offset(w / 2, h - 22 * u))
        // Las paredes del cajón, de acero más claro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(ACERO, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = CARTULINA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    /** La ficha de catálogo, con el agujero de la varilla abajo al centro. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        fillType = PathFillType.EvenOdd
        addRect(Rect(0f, 0f, s.width, s.height))
        val r = minOf(s.width, s.height).coerceAtMost(s.width * 0.3f) * 0.03f
        addOval(Rect(s.width / 2 - r, s.height - r * 3.6f, s.width / 2 + r, s.height - r * 1.6f))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(CARTULINA.copy(alpha = 0.4f))
        // El filete rojo de arriba, la línea vertical del margen y el refuerzo del agujero.
        drawLine(ROJO.copy(alpha = 0.7f), Offset(0f, 3 * u * g), Offset(w, 3 * u * g), 1f * u * g)
        drawLine(ROJO.copy(alpha = 0.35f), Offset(10 * u * g, 0f), Offset(10 * u * g, h), 0.8f * u)
        val r = minOf(w, h).coerceAtMost(w * 0.3f) * 0.03f
        drawCircle(TINTA.copy(alpha = 0.25f), r * 1.5f, Offset(w / 2, h - r * 2.6f), style = Stroke(0.8f * u))
    }

    override val margenes = MargenesFicha(12.dp, 16.dp, 24.dp, 22.dp, 28.dp, 46.dp)
    override val proporcionFicha = 1.25f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.82f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello del plan cumplido.
            "hecho" -> selloHecho(ROJO, p.color, cuadrado = true)
            "convence" -> drawPath(estrella(c, s * 0.48f, 5, 0.42f), ROJO)
            // Un lápiz.
            "piensa" -> rotate(-45f, c) {
                drawRect(Color(0xFF3A3A3A), Offset(c.x - s * 0.36f, c.y - s * 0.09f), Size(s * 0.56f, s * 0.18f))
                drawPath(Path().apply { moveTo(c.x + s * 0.2f, c.y - s * 0.09f); lineTo(c.x + s * 0.42f, c.y); lineTo(c.x + s * 0.2f, c.y + s * 0.09f); close() }, Color(0xFFD8B880))
                drawRect(ROJO, Offset(c.x - s * 0.44f, c.y - s * 0.09f), Size(s * 0.08f, s * 0.18f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una pestaña roja de separador. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.46f, c.y + s * 0.36f); lineTo(c.x - s * 0.3f, c.y - s * 0.36f); lineTo(c.x + s * 0.3f, c.y - s * 0.36f); lineTo(c.x + s * 0.46f, c.y + s * 0.36f); close() }, ROJO)
        drawLine(CARTULINA, Offset(c.x - s * 0.18f, c.y), Offset(c.x + s * 0.18f, c.y), s * 0.08f)
    }

    override val desorden = 0f
}
