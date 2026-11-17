package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.BlendMode
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Orden
// =====================================================================

/**
 * Orden: el archivo de planos de la sala capitular de la orden, mitad taller y mitad capilla. Contra un muro de placas de
 * acero con sus costuras, remaches de cabeza grande y chorreras de óxido, con una arquería ciega de tres arcos apuntados en
 * relieve bajo (el gótico industrial del tema). La luz de día baja de una luminaria enrejada que queda fuera del cuadro,
 * arriba a la izquierda: fría y dura, deja en las placas una mancha con las rayas de su reja. En cada arco cuelga algo: a la
 * izquierda, un panel de herramientas con la silueta pintada de cada una (una llave de dos bocas, un martillo de bola) y la
 * silueta vacía de unos alicates que faltan; en medio, un piloto rojo con su rejilla y su tubo; a la derecha, el estandarte
 * carmesí de la orden colgado de una barra de acero por sus presillas, con pliegues, cola de golondrina, fleco y borla, y el
 * escudo del engrane y la espada bordado en latón. El mueble es una cajonera blindada de acero pavonado: tablero grueso de
 * canto pulido por el uso, un friso con rejilla de ventilación y una placa de latón con los tres galones, cantoneras de
 * latón, remaches grandes en los montantes y un zócalo con franjas de peligro gastadas sobre patas de perfil. Encima, de
 * izquierda a derecha: el libro de la regla con cantoneras y broches de acero, y sobre él el estuche abierto del micrómetro;
 * el guantelete de acero articulado sobre su soporte (el punto focal); el plano azul enrollado, sujeto con dos pesas de
 * latón, que cuelga por el canto, con un compás de puntas de pie encima; una aceitera de latón de pico largo con su cerco de
 * aceite; y el velero de acero con cuatro velas, una ya consumida. Al pie, sobre la chapa estriada, una caja de acero con sus
 * asas y una cadena enrollada, cortadas por el borde. Cada tema es una gaveta pintada con su placa de latón estampada, de
 * una de tres clases según el tema: blindada con dos pestillos y manija; con volante de cierre carmesí, pasador y mirilla
 * (tras el vidrio se ven los planos); o un casillero de tubos portaplanos de latón con sus tapas roscadas, más tubos cuanto
 * más lleno. Las gavetas más llenas quedan entreabiertas y asoman los planos azules; alguna tiene la pintura quemada por una
 * soldadura o rayones. La de las ideas sin tema lleva la placa sin grabar y una etiqueta de cartón atada con alambre a la
 * manija; un hueco todavía sin tema es una gaveta soldada con su cordón y su tinte de calor, o una tapa atornillada. De
 * noche arden tres velas del velero: su halo sube por las placas, encienden el estandarte por abajo, el latón y los cantos
 * que las miran, y las gavetas se apagan según lo lejos que estén; el piloto rojo brilla con su color. El acero lejano se
 * hunde en un azul hondo con algún destello frío en los remaches. Por dentro, un plano azul con su cuadrícula. Las fichas
 * son fichas técnicas con su cuadrícula, su cajetín y sus marcas de registro.
 */
object IdeasOrden : DisenoIdeas {
    // ---------------------------------------------------------------- los colores de siempre (interior, fichas y marcas)
    private val ACERO = Color(0xFF3A4048)
    private val ACERO_LUZ = Color(0xFF5E6670)
    private val ACERO_OSC = Color(0xFF1C2127)
    private val CARMESI = Color(0xFFB0202A)
    private val LATON = Color(0xFFD6B25A)
    private val TEXTO = Color(0xFFDDE2E7)
    private val PLANO = Color(0xFF1E3A5C)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.25f) else c

    // ---------------------------------------------------------------- la paleta de la sala capitular
    /** El acero en tres valores: el lado de la luz, el cuerpo y lo hondo. */
    private val ACERO_CLARO = Color(0xFFA7AFB7)
    private val ACERO_MEDIO = Color(0xFF6D7680)
    private val ACERO_HONDO = Color(0xFF373E48)
    /** La pintura gris azulada de las gavetas y de la caja. */
    private val PINTURA = Color(0xFF55616C)
    /** El latón de placas, pesas, aceitera y bordados. */
    private val LATON_VIVO = Color(0xFFCBA24C)
    private val LATON_BRILLO = Color(0xFFF2DA92)
    private val LATON_HONDO = Color(0xFF73541F)
    /** La placa de latón de noche: de lejos (todavía da 5:1 con la tinta) a junto a las velas. */
    private val PLACA_NOCHE = Color(0xFFA4884A)
    private val PLACA_VELA = Color(0xFFE8C677)
    /** El carmesí del estandarte, el fieltro y el volante. */
    private val GRANA = Color(0xFFA31A2A)
    private val GRANA_HONDA = Color(0xFF5A0D18)
    private val GRANA_LUZ = Color(0xFFD24A5A)
    /** El azul de plano y sus trazos. */
    private val AZUL_PLANO = Color(0xFF2F5D8E)
    private val TRAZO = Color(0xFFDCE7F1)
    /** El amarillo y el negro de peligro, gastados. */
    private val PELIGRO = Color(0xFFD5AB3E)
    private val BETUN = Color(0xFF1A1B20)
    /** La cera de las velas, el óxido de las juntas, el cuero, el cartón de la etiqueta y el mango de nogal. */
    private val CERA = Color(0xFFEDE2C4)
    private val OXIDO = Color(0xFF8E4A26)
    private val CUERO = Color(0xFF4A3426)
    private val CARTON = Color(0xFFC9A774)
    private val CARTON_NOCHE = Color(0xFFB09466)
    /** La luz fría de la luminaria y la sombra violeta azulada. */
    private val LUZ_FRIA = Color(0xFFF1F0E8)
    private val SOMBRA_FRIA = Color(0xFF242638)
    /** La noche: acero azulado hondo; la llama de las velas; el piloto. */
    private val NOCHE = Color(0xFF0F1622)
    private val LLAMA = Color(0xFFFFC56E)
    private val PILOTO = Color(0xFFE8362C)
    private val DESTELLO = Color(0xFF9DB2CE)
    private val TINTA = Color(0xFF1B1710)

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 2.0f
    override val corona = 0.62f
    override val base = 0.24f
    override val lado = 0.055f
    override val hueco = 0.028f

    /** El tablero: su cara de arriba, su canto y el fin del friso, en fracciones del ancho desde arriba. */
    private const val TAB = 0.497f
    private const val TAB_F = 0.509f
    private const val TAB_B = 0.531f
    /** Los costados del cuerpo y los del tablero, que vuela un poco. */
    private const val BL = 0.03f
    private const val BR = 0.97f
    private const val TL = 0.016f
    private const val TR = 0.984f
    /** En la base, en fracción de su alto: el rincón del muro con el suelo, el fin del zócalo y donde pisan las patas. */
    private const val RINCON = 0.3f
    private const val ZOC = 0.42f
    private const val PIES = 0.52f
    private const val SALIDA = 0.16f

    // ---------------------------------------------------------------- la luz y la noche

    /** Dónde arden las tres velas de noche (el centro de la llama), en fracciones del ancho. */
    private val VELAS = listOf(Offset(0.745f, 0.398f), Offset(0.795f, 0.424f), Offset(0.852f, 0.378f))
    private val PIL = Offset(0.545f, 0.122f)

    /** Cuánto alumbran las velas el punto [p] (0 a 1) en un lienzo de ancho [w]. */
    private fun calor(p: Offset, w: Float): Float {
        var m = 0f
        for (v in VELAS) m = maxOf(m, 1f - (p - v * w).getDistance() / (w * 0.62f))
        return m.coerceIn(0f, 1f)
    }

    /** Lo mismo, más abajo y más despacio (el cuerpo y las gavetas), con [p] en fracciones del ancho. */
    private fun luzBaja(p: Offset): Float {
        var m = 0f
        for (v in VELAS) m = maxOf(m, 1.15f * (1f - (p - v).getDistance() / 1.2f))
        return m.coerceIn(0f, 1f)
    }

    /**
     * Un color a [k] (0 a 1) de las velas. De día, tal cual. De noche baja hacia su propio color hondo (multiplicado por el
     * aire azul de la sala, no hacia un gris), y cerca de las velas recobra el suyo y se calienta.
     */
    private fun n(c: Color, oscuro: Boolean, k: Float): Color {
        if (!oscuro) return c
        val q = k.coerceIn(0f, 1f)
        val hondo = Color((c.red * 0.2f + 0.04f).coerceIn(0f, 1f), (c.green * 0.23f + 0.055f).coerceIn(0f, 1f), (c.blue * 0.3f + 0.085f).coerceIn(0f, 1f), c.alpha)
        return mezcla(mezcla(hondo, c, 0.78f * q.pow(1.1f)), LLAMA, 0.24f * q)
    }

    /** La sombra: de día, violeta azulada; de noche, la noche. */
    private fun sombra(oscuro: Boolean, a: Float) =
        if (oscuro) NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else mezcla(SOMBRA_MUEBLE, SOMBRA_FRIA, 0.5f).copy(alpha = a.coerceIn(0f, 1f))

    /** El brillo de lo que mira a la luz: de día, la luz fría; de noche, el filo cálido de las velas según lo cerca. */
    private fun brillo(oscuro: Boolean, a: Float, k: Float = 0f) =
        if (oscuro) LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else LUZ_FRIA.copy(alpha = a.coerceIn(0f, 1f))

    /** El destello de un remache o un canto pulido: de día, la luz; de noche, frío de lejos y cálido cerca de las velas. */
    private fun destello(oscuro: Boolean, a: Float, k: Float) =
        if (oscuro) mezcla(DESTELLO, LLAMA, k * 1.3f).copy(alpha = (a * (0.35f + 0.65f * k)).coerceIn(0f, 1f)) else LUZ_FRIA.copy(alpha = a.coerceIn(0f, 1f))

    // ---------------------------------------------------------------- ayudantes

    private fun unir(partes: List<Path>): Path {
        var r = Path()
        for (p in partes) r = Path().apply { op(r, p, PathOperation.Union) }
        return r
    }

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    private fun rect(l: Float, t: Float, r: Float, b: Float) = Path().apply { addRect(Rect(l, t, r, b)) }
    private fun ovalo(l: Float, t: Float, r: Float, b: Float) = Path().apply { addOval(Rect(l, t, r, b)) }

    /** El remache viejo (lo usan el interior y la marca de fijada, que no cambian). */
    private fun DrawScope.remache(p: Offset, r: Float) {
        drawCircle(ACERO_LUZ, r, p)
        drawCircle(Color.White.copy(alpha = 0.35f), r * 0.4f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
        drawCircle(Color.Black.copy(alpha = 0.4f), r, p, style = Stroke(r * 0.25f))
    }

    /** Un remache de cabeza grande: su sombra abajo a la derecha, la cabeza, la media luna oscura y el brillo. */
    private fun DrawScope.roblon(p: Offset, r: Float, oscuro: Boolean, k: Float, base: Color = ACERO_MEDIO) {
        drawCircle(sombra(oscuro, 0.4f), r * 1.05f, Offset(p.x + r * 0.45f, p.y + r * 0.5f))
        drawCircle(n(base, oscuro, k), r, p)
        drawArc(sombra(oscuro, 0.45f), 0f, 120f, false, Offset(p.x - r * 0.8f, p.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.35f))
        drawCircle(destello(oscuro, 0.75f, k), r * 0.32f, Offset(p.x - r * 0.35f, p.y - r * 0.38f))
    }

    /**
     * Un cuerpo de metal: [p] relleno de [base], con la franja de luz y el brillo del lado que mira a la luz y la franja de
     * sombra del otro, dentro de [caja]. [izq] dice de qué lado viene la luz.
     */
    private fun DrawScope.metal(p: Path, caja: Rect, base: Color, oscuro: Boolean, k: Float, izq: Boolean = true, fuerza: Float = 1f) {
        drawPath(p, n(base, oscuro, k))
        clipPath(p) {
            val wl = caja.width
            val luzX = if (izq) caja.left else caja.right - wl * 0.3f
            val somX = if (izq) caja.right - wl * 0.32f else caja.left
            val luz = if (oscuro) LLAMA.copy(alpha = 0.22f * k * fuerza) else LUZ_FRIA.copy(alpha = 0.22f * fuerza)
            drawRect(luz, Offset(luzX, caja.top), Size(wl * 0.3f, caja.height))
            drawRect(luz.copy(alpha = luz.alpha * 0.45f), Offset(if (izq) luzX + wl * 0.3f else luzX - wl * 0.12f, caja.top), Size(wl * 0.12f, caja.height))
            drawRect(sombra(oscuro, 0.4f * fuerza), Offset(somX, caja.top), Size(wl * 0.32f, caja.height))
            drawRect(sombra(oscuro, 0.14f * fuerza), Offset(if (izq) somX - wl * 0.14f else somX + wl * 0.32f, caja.top), Size(wl * 0.14f, caja.height))
            val bx = if (izq) caja.left + wl * 0.2f else caja.right - wl * 0.22f
            drawRect(destello(oscuro, 0.55f * fuerza, k), Offset(bx, caja.top), Size(wl * 0.05f + 0.5f * density, caja.height))
        }
    }

    /** La sombra de una cosa en el muro: de día, abajo a la derecha; de noche, la que tiende la vela del medio, hacia fuera. */
    private fun DrawScope.sombraEnMuro(sil: Path, centro: Offset, w: Float, oscuro: Boolean, fuerza: Float = 1f, cerca: Float = 1f) {
        clipRect(0f, 0f, w, TAB * w + 1f) {
            if (!oscuro) translate(w * 0.02f * cerca, w * 0.011f * cerca) { drawPath(sil, sombra(false, 0.22f * fuerza)) }
            else {
                // De noche solo arroja sombra lo que está cerca de las velas; lo lejano apenas recibe su luz.
                val v = VELAS[1] * w
                val q = (calor(centro, w) * 2.2f).coerceIn(0f, 1f)
                if (q < 0.05f) return@clipRect
                val o = (centro - v) * (0.1f * cerca)
                withTransform({ translate(o.x, o.y); scale(1.06f, 1.06f, centro) }) { drawPath(sil, NOCHE.copy(alpha = 0.32f * fuerza * q)) }
            }
        }
    }

    // ---------------------------------------------------------------- el muro

    /** El muro de placas de acero, de arriba hasta [hasta], con sus costuras, sus remaches y el óxido que chorrea. */
    private fun DrawScope.muro(w: Float, hasta: Float, oscuro: Boolean) {
        val u = density
        val pared = n(mezcla(ACERO_MEDIO, ACERO_CLARO, 0.12f), oscuro, 0.12f)
        drawRect(pared, Offset.Zero, Size(w, hasta))
        val filas = listOf(0f, 0.262f, 0.6f, 0.98f, 1.36f, 1.74f, 2.12f, 2.5f, 2.9f, 3.3f).map { it * w }
        for (i in 0 until filas.size - 1) {
            val y0 = filas[i]; if (y0 >= hasta) break
            val y1 = minOf(filas[i + 1], hasta)
            val juntas = if (i % 2 == 0) listOf(0f, 0.31f, 0.69f, 1f) else listOf(0f, 0.15f, 0.5f, 0.86f, 1f)
            for (j in 0 until juntas.size - 1) {
                val r = Rect(w * juntas[j], y0, w * juntas[j + 1], y1)
                // Cada placa con su tono, apenas distinto.
                val tono = azar(500 + i, j)
                drawRect(if (tono > 0.5f) brillo(oscuro, 0.06f * (tono - 0.5f) * 2f, 0.1f) else sombra(oscuro, 0.08f * (0.5f - tono) * 2f), r.topLeft, r.size)
                // La costura: un canto de luz arriba e izquierda y la junta en sombra abajo y a la derecha.
                drawRect(sombra(oscuro, 0.5f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
                drawRect(sombra(oscuro, 0.55f), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.8f * u))
                drawRect(destello(oscuro, 0.2f, 0.05f), Offset(r.left, r.top), Size(r.width, 1f * u))
                drawRect(destello(oscuro, 0.14f, 0.05f), Offset(r.left, r.top), Size(1f * u, r.height))
                // Los remaches de la costura de abajo, con el óxido que chorrea de alguno.
                val m = (r.width / (w * 0.045f)).toInt().coerceAtLeast(2)
                for (q in 0 until m) {
                    val p = Offset(r.left + r.width * (q + 0.5f) / m, r.bottom - w * 0.011f)
                    if (azar(530 + i * 9 + j, q) < 0.32f) {
                        val largo = w * (0.012f + 0.035f * azar(540 + i, q + j * 7))
                        drawLine(n(OXIDO, oscuro, 0.05f).copy(alpha = 0.32f), Offset(p.x + 0.4f * u, p.y + 1.6f * u), Offset(p.x + 0.8f * u, p.y + 1.6f * u + largo), 1.6f * u, StrokeCap.Round)
                        drawCircle(n(OXIDO, oscuro, 0.05f).copy(alpha = 0.4f), 2.6f * u, p + Offset(0.6f * u, 1f * u))
                    }
                    roblon(p, w * 0.0052f, oscuro, 0.05f, mezcla(ACERO_MEDIO, ACERO_CLARO, 0.3f))
                }
            }
        }
        pinceladas(Rect(0f, 0f, w, hasta), if (oscuro) NOCHE else SOMBRA_FRIA, 811, 44, u, if (oscuro) 0.06f else 0.05f)
        pinceladas(Rect(0f, 0f, w, hasta), if (oscuro) DESTELLO else LUZ_FRIA, 812, 26, u, if (oscuro) 0.025f else 0.06f)
        // El óxido que sube del suelo, a los lados del mueble.
        for (q in 0 until 9) {
            val x = if (q % 2 == 0) w * BL * azar(560, q) else w * (BR + (1f - BR) * azar(560, q))
            drawOval(n(OXIDO, oscuro, 0f).copy(alpha = 0.22f), Offset(x - 3f * u, hasta - w * (0.02f + 0.03f * azar(561, q))), Size(6f * u, w * 0.03f))
        }
    }

    /** La arquería ciega: tres arcos apuntados en relieve bajo, de pilastra a pilastra, con sus remaches. */
    private fun DrawScope.arqueria(w: Float, oscuro: Boolean) {
        val u = density
        val banda = w * 0.017f
        for (a in 0 until 3) {
            val x0 = w * (0.012f + a * 0.325f); val x1 = x0 + w * 0.325f
            val arco = arcoApuntado(Rect(x0, w * 0.034f, x1, w * TAB), w * 0.155f)
            // El tímpano, un punto hundido.
            clipPath(arco) { drawRect(sombra(oscuro, 0.07f), Offset(x0, 0f), Size(x1 - x0, w * TAB)) }
            translate(banda * 0.18f, banda * 0.22f) { drawPath(arco, sombra(oscuro, 0.45f), style = Stroke(banda)) }
            drawPath(arco, n(mezcla(ACERO_MEDIO, ACERO_CLARO, 0.45f), oscuro, 0.14f), style = Stroke(banda))
            translate(-banda * 0.28f, -banda * 0.28f) { drawPath(arco, destello(oscuro, 0.3f, 0.1f), style = Stroke(1f * u)) }
            translate(banda * 0.3f, banda * 0.3f) { drawPath(arco, sombra(oscuro, 0.3f), style = Stroke(1f * u)) }
            // Los remaches de la rosca del arco.
            val cx = (x0 + x1) / 2
            listOf(0.12f, 0.3f, 0.5f, 0.7f, 0.88f).forEach { f ->
                val ang = Math.PI * (1.0 - f)
                val px = cx + (x1 - x0) / 2 * cos(ang).toFloat() * 0.97f
                val py = w * 0.19f - w * 0.152f * sin(ang).toFloat().pow(0.8f)
                roblon(Offset(px, py), w * 0.0042f, oscuro, 0.1f, ACERO_CLARO)
            }
        }
    }

    /** La luz de día de la luminaria enrejada de fuera del cuadro: una mancha fría arriba a la izquierda con las rayas de su reja. */
    private fun DrawScope.luzDeLuminaria(w: Float) {
        mancha(Offset(w * 0.12f, w * 0.03f), w * 0.8f, LUZ_FRIA.copy(alpha = 0.42f))
        val zona = ovalo(-w * 0.18f, -w * 0.2f, w * 0.62f, w * 0.44f)
        clipPath(zona) {
            for (q in 0 until 4) {
                val x = w * (-0.06f + 0.14f * q)
                drawPath(poligono(x, 0f, x + w * 0.028f, 0f, x + w * 0.2f, w * 0.5f, x + w * 0.172f, w * 0.5f), sombraMueble(0.065f))
            }
            drawPath(poligono(0f, w * 0.15f, w * 0.6f, w * 0.27f, w * 0.6f, w * 0.29f, 0f, w * 0.17f), sombraMueble(0.06f))
        }
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.22f)), w * 0.35f, w), Offset(w * 0.35f, 0f), Size(w * 0.65f, w * TAB))
    }

    /** El halo de noche: las velas suben por las placas y el piloto tiñe de rojo lo suyo. */
    private fun DrawScope.halos(w: Float) {
        mancha(Offset(w * 0.8f, w * 0.37f), w * 0.66f, LLAMA.copy(alpha = 0.2f))
        mancha(Offset(w * 0.8f, w * 0.34f), w * 0.27f, LLAMA.copy(alpha = 0.24f))
        mancha(PIL * w, w * 0.13f, PILOTO.copy(alpha = 0.24f))
    }

    // ---------------------------------------------------------------- lo colgado del muro

    private fun siluetaLlave(w: Float): Path {
        val cx = w * 0.068f
        return unir(listOf(
            rect(cx - w * 0.0055f, w * 0.145f, cx + w * 0.0055f, w * 0.275f),
            ovalo(cx - w * 0.017f, w * 0.118f, cx + w * 0.017f, w * 0.152f),
            ovalo(cx - w * 0.013f, w * 0.27f, cx + w * 0.013f, w * 0.296f),
        ))
    }

    private fun siluetaMartillo(w: Float): Path {
        val cx = w * 0.123f
        return unir(listOf(
            Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(cx - w * 0.006f, w * 0.14f, cx + w * 0.006f, w * 0.292f, CornerRadius(w * 0.004f))) },
            rect(cx - w * 0.026f, w * 0.124f, cx + w * 0.014f, w * 0.143f),
            ovalo(cx + w * 0.008f, w * 0.12f, cx + w * 0.031f, w * 0.147f),
        ))
    }

    private fun siluetaAlicates(w: Float): Path {
        val cx = w * 0.181f
        return unir(listOf(
            poligono(cx - w * 0.004f, w * 0.12f, cx + w * 0.004f, w * 0.12f, cx + w * 0.006f, w * 0.17f, cx - w * 0.006f, w * 0.17f),
            ovalo(cx - w * 0.009f, w * 0.162f, cx + w * 0.009f, w * 0.18f),
            poligono(cx - w * 0.006f, w * 0.175f, cx - w * 0.001f, w * 0.176f, cx - w * 0.011f, w * 0.288f, cx - w * 0.02f, w * 0.286f),
            poligono(cx + w * 0.001f, w * 0.176f, cx + w * 0.006f, w * 0.175f, cx + w * 0.02f, w * 0.286f, cx + w * 0.011f, w * 0.288f),
        ))
    }

    private val PANEL = Rect(0.026f, 0.098f, 0.214f, 0.31f)

    /** El panel de herramientas: cada una sobre su silueta pintada, y la silueta vacía de los alicates que faltan. */
    private fun DrawScope.panelHerramientas(w: Float, oscuro: Boolean) {
        val u = density
        val r = Rect(w * PANEL.left, w * PANEL.top, w * PANEL.right, w * PANEL.bottom)
        drawRect(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.55f), oscuro, 0.04f), r.topLeft, r.size)
        drawRect(destello(oscuro, 0.3f, 0.05f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(destello(oscuro, 0.2f, 0.05f), r.topLeft, Size(1.2f * u, r.height))
        drawRect(sombra(oscuro, 0.5f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(sombra(oscuro, 0.45f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
        pinceladas(r, if (oscuro) NOCHE else SOMBRA_FRIA, 830, 8, u, 0.08f)
        listOf(r.topLeft + Offset(5f * u, 5f * u), Offset(r.right - 5f * u, r.top + 5f * u), Offset(r.left + 5f * u, r.bottom - 5f * u), Offset(r.right - 5f * u, r.bottom - 5f * u))
            .forEach { roblon(it, 2.4f * u, oscuro, 0.04f) }
        val pintura = n(Color(0xFFE4DDC6), oscuro, 0.05f).copy(alpha = if (oscuro) 0.55f else 0.75f)
        // Las siluetas pintadas: un filo claro alrededor de cada herramienta; la de los alicates, rellena y sin nada.
        drawPath(siluetaLlave(w), pintura, style = Stroke(w * 0.007f))
        drawPath(siluetaMartillo(w), pintura, style = Stroke(w * 0.007f))
        drawPath(siluetaAlicates(w), pintura)
        drawPath(siluetaAlicates(w), pintura, style = Stroke(w * 0.004f))
        // Los ganchos (el de los alicates, vacío).
        listOf(Offset(w * 0.068f, w * 0.128f), Offset(w * 0.112f, w * 0.13f), Offset(w * 0.181f, w * 0.118f)).forEach { g ->
            drawCircle(sombra(oscuro, 0.4f), 1.8f * u, g + Offset(1f * u, 1.2f * u))
            drawCircle(n(ACERO_CLARO, oscuro, 0.06f), 1.6f * u, g)
        }
        // La llave de dos bocas.
        translate(w * 0.006f, w * 0.004f) { drawPath(siluetaLlave(w), sombra(oscuro, 0.35f)) }
        val llave = siluetaLlave(w)
        metal(llave, Rect(w * 0.051f, w * 0.118f, w * 0.085f, w * 0.296f), ACERO_CLARO, oscuro, 0.05f)
        drawOval(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.55f), oscuro, 0.04f), Offset(w * 0.061f, w * 0.121f), Size(w * 0.014f, w * 0.016f))
        drawRect(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.55f), oscuro, 0.04f), Offset(w * 0.0635f, w * 0.116f), Size(w * 0.009f, w * 0.012f))
        drawOval(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.55f), oscuro, 0.04f), Offset(w * 0.0625f, w * 0.283f), Size(w * 0.011f, w * 0.012f))
        drawRect(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.55f), oscuro, 0.04f), Offset(w * 0.0645f, w * 0.288f), Size(w * 0.007f, w * 0.01f))
        drawCircle(n(ACERO_CLARO, oscuro, 0.06f), 1.6f * u, Offset(w * 0.068f, w * 0.128f))
        // El martillo de bola: cabeza de acero, mango de nogal gastado.
        translate(w * 0.006f, w * 0.004f) { drawPath(siluetaMartillo(w), sombra(oscuro, 0.35f)) }
        val cx = w * 0.123f
        val mango = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(cx - w * 0.006f, w * 0.14f, cx + w * 0.006f, w * 0.292f, CornerRadius(w * 0.004f))) }
        metal(mango, Rect(cx - w * 0.006f, w * 0.14f, cx + w * 0.006f, w * 0.292f), mezcla(CARTON, CUERO, 0.35f), oscuro, 0.05f)
        drawLine(sombra(oscuro, 0.25f), Offset(cx + w * 0.001f, w * 0.17f), Offset(cx + w * 0.0015f, w * 0.27f), 0.6f * u)
        val cabeza = unir(listOf(rect(cx - w * 0.026f, w * 0.124f, cx + w * 0.014f, w * 0.143f), ovalo(cx + w * 0.008f, w * 0.12f, cx + w * 0.031f, w * 0.147f)))
        drawPath(cabeza, n(ACERO_MEDIO, oscuro, 0.06f))
        clipPath(cabeza) {
            drawRect(destello(oscuro, 0.4f, 0.05f), Offset(cx - w * 0.026f, w * 0.12f), Size(w * 0.06f, w * 0.006f))
            drawRect(sombra(oscuro, 0.35f), Offset(cx - w * 0.026f, w * 0.139f), Size(w * 0.06f, w * 0.01f))
        }
        drawCircle(destello(oscuro, 0.6f, 0.05f), w * 0.0035f, Offset(cx + w * 0.016f, w * 0.128f))
        drawRect(destello(oscuro, 0.5f, 0.05f), Offset(cx - w * 0.026f, w * 0.124f), Size(1.2f * u, w * 0.019f))
    }

    /** El piloto rojo con su rejilla, su caja y el tubo que sube por el muro. */
    private fun DrawScope.piloto(w: Float, oscuro: Boolean) {
        val u = density
        val c = PIL * w
        val kr = 0f
        drawRect(sombra(oscuro, 0.35f), Offset(c.x - w * 0.006f + 2f * u, 0f), Size(w * 0.012f, c.y))
        drawRect(n(ACERO_MEDIO, oscuro, kr), Offset(c.x - w * 0.006f, 0f), Size(w * 0.012f, c.y - w * 0.02f))
        drawRect(destello(oscuro, 0.35f, kr), Offset(c.x - w * 0.004f, 0f), Size(1.2f * u, c.y - w * 0.02f))
        drawRect(n(ACERO_HONDO, oscuro, kr), Offset(c.x - w * 0.009f, w * 0.05f), Size(w * 0.018f, w * 0.008f))
        roblon(Offset(c.x, w * 0.054f), 1.5f * u, oscuro, kr)
        val caja = Rect(c.x - w * 0.022f, c.y - w * 0.022f, c.x + w * 0.022f, c.y + w * 0.022f)
        drawRect(sombra(oscuro, 0.4f), caja.topLeft + Offset(w * 0.006f, w * 0.005f), caja.size)
        metal(rect(caja.left, caja.top, caja.right, caja.bottom), caja, ACERO_HONDO, oscuro, kr)
        drawRect(destello(oscuro, 0.35f, kr), caja.topLeft, Size(caja.width, 1.2f * u))
        listOf(caja.topLeft + Offset(3f * u, 3f * u), Offset(caja.right - 3f * u, caja.top + 3f * u), Offset(caja.left + 3f * u, caja.bottom - 3f * u), caja.bottomRight - Offset(3f * u, 3f * u))
            .forEach { drawCircle(n(ACERO_CLARO, oscuro, kr), 1.1f * u, it) }
        // El cristal rojo: de día apagado con su brillo; de noche encendido, con su color.
        val rr = w * 0.0135f
        drawCircle(if (oscuro) PILOTO else mezcla(PILOTO, GRANA_HONDA, 0.45f), rr, c)
        if (oscuro) { drawCircle(Color(0xFFFFB0A0), rr * 0.45f, c - Offset(rr * 0.15f, rr * 0.15f)) }
        else drawCircle(LUZ_FRIA.copy(alpha = 0.55f), rr * 0.3f, c - Offset(rr * 0.38f, rr * 0.38f))
        // La rejilla.
        val reja = n(ACERO_CLARO, oscuro, kr)
        drawCircle(reja, rr * 1.12f, c, style = Stroke(1.4f * u))
        listOf(-0.45f, 0f, 0.45f).forEach { f -> drawLine(reja, Offset(c.x + rr * f, c.y - rr * 1.05f), Offset(c.x + rr * f, c.y + rr * 1.05f), 1f * u) }
        drawLine(reja, Offset(c.x - rr * 1.05f, c.y), Offset(c.x + rr * 1.05f, c.y), 1f * u)
    }

    /**
     * La orilla de abajo del estandarte, de la punta izquierda al escote y a la punta derecha: no es recta, se ondula donde
     * la alcanzan los pliegues (cada valle sube un poco, cada cresta baja).
     */
    private fun orilla(w: Float): List<Offset> {
        val puntos = mutableListOf<Offset>()
        val a = Offset(0.68f, 0.35f); val m = Offset(0.79f, 0.298f); val b = Offset(0.902f, 0.35f)
        val pasos = 22
        for (i in 0..pasos) {
            val t = i / pasos.toFloat()
            val base = if (t < 0.5f) a + (m - a) * (t * 2f) else m + (b - m) * ((t - 0.5f) * 2f)
            val onda = if (i == 0 || i == pasos || i == pasos / 2) 0f else 0.0055f * sin(t * 6.283f * 3.2f + 0.6f)
            puntos += Offset(base.x * w, (base.y + onda) * w)
        }
        return puntos
    }

    /** El paño del estandarte: presillas arriba, costados que se recogen y cola de golondrina abajo. */
    private fun panoEstandarte(w: Float): Path = Path().apply {
        val o = orilla(w)
        moveTo(w * 0.676f, w * 0.072f)
        lineTo(w * 0.904f, w * 0.072f)
        cubicTo(w * 0.898f, w * 0.13f, w * 0.904f, w * 0.24f, o.last().x, o.last().y)
        for (p in o.reversed()) lineTo(p.x, p.y)
        cubicTo(w * 0.676f, w * 0.24f, w * 0.682f, w * 0.13f, w * 0.676f, w * 0.072f)
        close()
    }

    /** El estandarte carmesí de la orden: tela con peso y pliegues, el escudo bordado en latón, el fleco y la borla. */
    private fun DrawScope.estandarte(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(w * 0.79f, w * 0.26f), w) else 0f
        val pano = panoEstandarte(w)
        val tela = n(GRANA, oscuro, k * 0.8f)
        val honda = n(GRANA_HONDA, oscuro, k * 0.6f)
        val viva = n(GRANA_LUZ, oscuro, k)
        val oro = n(LATON_VIVO, oscuro, k)
        // La barra de acero con sus remates y las escuadras que la sujetan al muro.
        val yb = w * 0.062f
        listOf(w * 0.69f, w * 0.89f).forEach { x ->
            drawRect(sombra(oscuro, 0.35f), Offset(x - w * 0.003f + 2f * u, yb), Size(w * 0.008f, w * 0.03f))
            drawRect(n(ACERO_HONDO, oscuro, k), Offset(x - w * 0.004f, yb - w * 0.004f), Size(w * 0.008f, w * 0.028f))
            roblon(Offset(x, yb + w * 0.018f), 1.6f * u, oscuro, k)
        }
        drawLine(sombra(oscuro, 0.35f), Offset(w * 0.66f, yb + w * 0.007f), Offset(w * 0.925f, yb + w * 0.007f), w * 0.008f)
        drawLine(n(ACERO_MEDIO, oscuro, k), Offset(w * 0.662f, yb), Offset(w * 0.918f, yb), w * 0.008f, StrokeCap.Round)
        drawLine(destello(oscuro, 0.55f, k), Offset(w * 0.664f, yb - w * 0.0022f), Offset(w * 0.916f, yb - w * 0.0022f), 1f * u)
        listOf(w * 0.657f, w * 0.923f).forEach { x ->
            drawCircle(n(LATON_VIVO, oscuro, k), w * 0.0075f, Offset(x, yb))
            drawCircle(destello(oscuro, 0.7f, k), w * 0.0025f, Offset(x - w * 0.002f, yb - w * 0.002f))
        }
        // La sombra de la tela en el muro: cuelga cerca, apenas corrida.
        if (!oscuro) translate(w * 0.012f, w * 0.008f) { drawPath(pano, sombra(false, 0.26f)) }
        else translate(-w * 0.004f, -w * 0.022f) { drawPath(pano, NOCHE.copy(alpha = 0.35f)) }
        drawPath(pano, tela)
        // El escudo bordado: el campo, el engrane y la espada; los pliegues van encima y lo tuercen.
        val ec = Offset(w * 0.79f, w * 0.185f)
        val ea = w * 0.094f; val eh = w * 0.118f
        val escudo = Path().apply {
            moveTo(ec.x - ea / 2, ec.y - eh / 2); lineTo(ec.x + ea / 2, ec.y - eh / 2); lineTo(ec.x + ea / 2, ec.y - eh * 0.05f)
            cubicTo(ec.x + ea / 2, ec.y + eh * 0.25f, ec.x + ea * 0.25f, ec.y + eh * 0.42f, ec.x, ec.y + eh / 2)
            cubicTo(ec.x - ea * 0.25f, ec.y + eh * 0.42f, ec.x - ea / 2, ec.y + eh * 0.25f, ec.x - ea / 2, ec.y - eh * 0.05f)
            close()
        }
        clipPath(pano) {
            drawPath(escudo, mezcla(tela, honda, 0.35f))
            translate(0.8f * u, 0.9f * u) { drawPath(escudo, honda, style = Stroke(w * 0.0065f)) }
            drawPath(escudo, oro, style = Stroke(w * 0.0055f))
            drawPath(escudo, n(LATON_BRILLO, oscuro, k).copy(alpha = 0.6f), style = Stroke(w * 0.0015f))
            val gc = Offset(ec.x, ec.y - eh * 0.02f)
            translate(0.8f * u, 0.9f * u) { drawPath(engrane(gc, ea * 0.3f, 10), honda) }
            drawPath(engrane(gc, ea * 0.3f, 10), oro)
            drawCircle(mezcla(tela, honda, 0.35f), ea * 0.12f, gc)
            // La espada: hoja de punta abajo, guarda, empuñadura y pomo.
            val hoja = poligono(gc.x - w * 0.004f, ec.y - eh * 0.3f, gc.x + w * 0.004f, ec.y - eh * 0.3f, gc.x + w * 0.004f, ec.y + eh * 0.3f, gc.x, ec.y + eh * 0.4f, gc.x - w * 0.004f, ec.y + eh * 0.3f)
            translate(0.8f * u, 0.9f * u) { drawPath(hoja, honda) }
            drawPath(hoja, n(mezcla(ACERO_CLARO, LATON_BRILLO, 0.3f), oscuro, k))
            drawLine(n(ACERO_MEDIO, oscuro, k), Offset(gc.x + w * 0.001f, ec.y - eh * 0.28f), Offset(gc.x + w * 0.001f, ec.y + eh * 0.32f), 0.7f * u)
            drawRect(oro, Offset(gc.x - w * 0.02f, ec.y - eh * 0.33f), Size(w * 0.04f, w * 0.0055f))
            drawRect(n(LATON_HONDO, oscuro, k), Offset(gc.x - w * 0.0028f, ec.y - eh * 0.44f), Size(w * 0.0056f, eh * 0.11f))
            drawCircle(oro, w * 0.0048f, Offset(gc.x, ec.y - eh * 0.46f))
            // Los pliegues: hondos y juntos bajo las presillas, más abiertos y suaves hacia abajo, donde el peso los estira.
            val pliegues = listOf(0.693f to 0.004f, 0.728f to -0.004f, 0.762f to 0.006f, 0.803f to -0.002f, 0.842f to 0.006f, 0.878f to -0.003f)
            pliegues.forEachIndexed { i, (x0, deriva) ->
                val xa = w * x0; val xb = w * (x0 + deriva)
                val valle = Path().apply { moveTo(xa, w * 0.08f); cubicTo(xa + w * 0.004f, w * 0.15f, xb - w * 0.004f, w * 0.24f, xb, w * 0.35f) }
                val cresta = Path().apply { moveTo(xa - w * 0.011f, w * 0.08f); cubicTo(xa - w * 0.008f, w * 0.15f, xb - w * 0.016f, w * 0.24f, xb - w * 0.014f, w * 0.35f) }
                val hondo = if (i % 2 == 0) 1f else 0.7f
                drawPath(valle, honda.copy(alpha = 0.28f * hondo), style = Stroke(w * 0.016f, cap = StrokeCap.Round))
                drawPath(valle, honda.copy(alpha = 0.45f * hondo), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                drawPath(cresta, viva.copy(alpha = if (oscuro) 0.25f + 0.25f * k else 0.32f), style = Stroke(w * 0.009f, cap = StrokeCap.Round))
                drawPath(cresta, brillo(oscuro, 0.18f, k).copy(alpha = if (oscuro) 0.25f * k else 0.16f), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
            }
            // Arriba, la tela recogida entre presillas: comba y sombra bajo la vaina.
            drawRect(honda.copy(alpha = 0.55f), Offset(w * 0.67f, w * 0.072f), Size(w * 0.24f, w * 0.018f))
            drawLine(oro.copy(alpha = 0.7f), Offset(w * 0.678f, w * 0.091f), Offset(w * 0.902f, w * 0.091f), 0.8f * u)
            // Entre presilla y presilla la orilla de arriba cuelga en comba, y deja un pliegue corto debajo.
            for (q in 0 until 4) {
                val x1 = w * (0.708f + 0.0505f * q); val x2 = x1 + w * 0.0245f; val xm = (x1 + x2) / 2
                drawPath(Path().apply { moveTo(x1, w * 0.08f); cubicTo(x1 + w * 0.006f, w * 0.094f, x2 - w * 0.006f, w * 0.094f, x2, w * 0.08f) }, honda.copy(alpha = 0.7f), style = Stroke(w * 0.004f))
                drawPath(Path().apply { moveTo(xm, w * 0.095f); cubicTo(xm + w * 0.002f, w * 0.11f, xm - w * 0.002f, w * 0.125f, xm + w * 0.001f, w * 0.14f) }, honda.copy(alpha = 0.4f), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
            }
            // La luz: de día, de arriba a la izquierda (el costado derecho cae en sombra); de noche, las velas desde abajo.
            if (!oscuro) {
                drawRect(LUZ_FRIA.copy(alpha = 0.1f), Offset(w * 0.67f, w * 0.07f), Size(w * 0.05f, w * 0.3f))
                drawRect(sombraMueble(0.18f), Offset(w * 0.865f, w * 0.07f), Size(w * 0.05f, w * 0.3f))
            } else drawRect(Brush.verticalGradient(listOf(Color.Transparent, LLAMA.copy(alpha = 0.16f + 0.3f * k)), w * 0.15f, w * 0.35f), Offset(w * 0.67f, w * 0.15f), Size(w * 0.25f, w * 0.2f))
        }
        // Las presillas sobre la barra, con la barra que asoma entre ellas.
        for (q in 0 until 5) {
            val x = w * (0.682f + 0.0505f * q)
            val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(x, yb - w * 0.009f, x + w * 0.026f, w * 0.082f, CornerRadius(w * 0.006f))) }
            translate(1.2f * u, 1.4f * u) { drawPath(p, sombra(oscuro, 0.4f)) }
            drawPath(p, tela)
            drawRect(viva.copy(alpha = 0.45f), Offset(x + w * 0.003f, yb - w * 0.007f), Size(w * 0.007f, w * 0.012f))
            drawRect(honda.copy(alpha = 0.6f), Offset(x + w * 0.019f, yb - w * 0.007f), Size(w * 0.006f, w * 0.02f))
        }
        // El fleco de latón por las dos orillas de la cola.
        fun fleco(a: Offset, b: Offset, semilla: Int) {
            val m = 2
            for (q in 0 until m) {
                val p = a + (b - a) * (q / m.toFloat())
                val l = (3.2f + 1.6f * azar(semilla, q)) * u
                drawLine(n(LATON_HONDO, oscuro, k), p, Offset(p.x + 0.4f * u, p.y + l), 1f * u, StrokeCap.Round)
                drawLine(oro, Offset(p.x - 0.4f * u, p.y), Offset(p.x, p.y + l * 0.85f), 0.7f * u, StrokeCap.Round)
            }
        }
        val o = orilla(w)
        for (i in 0 until o.size - 1) fleco(o[i], o[i + 1], 91 + i)
        drawPath(Path().apply { moveTo(o[0].x, o[0].y); for (p in o) lineTo(p.x, p.y) }, oro, style = Stroke(1.6f * u))
        // El cordón con su borla, del remate derecho: tiene peso, cuelga recto.
        val c0 = Offset(w * 0.923f, yb + w * 0.007f)
        val c1 = Offset(w * 0.927f, w * 0.205f)
        drawPath(Path().apply { moveTo(c0.x, c0.y); cubicTo(c0.x + w * 0.006f, w * 0.12f, c1.x - w * 0.002f, w * 0.17f, c1.x, c1.y) }, oro, style = Stroke(1.2f * u))
        val borla = Path().apply {
            moveTo(c1.x - w * 0.004f, c1.y); lineTo(c1.x + w * 0.004f, c1.y)
            cubicTo(c1.x + w * 0.006f, c1.y + w * 0.015f, c1.x + w * 0.009f, c1.y + w * 0.03f, c1.x + w * 0.009f, c1.y + w * 0.04f)
            lineTo(c1.x - w * 0.009f, c1.y + w * 0.04f)
            cubicTo(c1.x - w * 0.009f, c1.y + w * 0.03f, c1.x - w * 0.006f, c1.y + w * 0.015f, c1.x - w * 0.004f, c1.y)
            close()
        }
        translate(w * 0.006f, w * 0.004f) { drawPath(borla, sombra(oscuro, 0.3f)) }
        drawPath(borla, oro)
        clipPath(borla) {
            for (q in 0 until 5) drawLine(n(LATON_HONDO, oscuro, k), Offset(c1.x - w * 0.006f + q * w * 0.003f, c1.y + w * 0.012f), Offset(c1.x - w * 0.008f + q * w * 0.0042f, c1.y + w * 0.04f), 0.6f * u)
            drawRect(brillo(oscuro, 0.35f, k + 0.2f), Offset(c1.x - w * 0.009f, c1.y), Size(w * 0.004f, w * 0.04f))
        }
        drawCircle(n(LATON_HONDO, oscuro, k), w * 0.005f, Offset(c1.x, c1.y + w * 0.003f))
    }

    // ---------------------------------------------------------------- lo de encima

    /** El libro de la regla tumbado, con cantoneras y broches de acero, y encima el estuche abierto del micrómetro. */
    private fun DrawScope.libroYMicrometro(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(w * 0.11f, w * 0.46f), w) else 0f
        // El libro: tapa de arriba (apenas se ve), el canto de las hojas y la tapa de abajo.
        val l0 = w * 0.034f; val l1 = w * 0.196f
        drawOval(sombra(oscuro, 0.35f), Offset(l0 + w * 0.01f, w * 0.493f), Size(l1 - l0 + w * 0.01f, w * 0.009f))
        drawRect(n(CUERO, oscuro, k), Offset(l0, w * 0.49f), Size(l1 - l0, w * 0.0075f))
        drawRect(n(mezcla(CERA, LUZ_FRIA, 0.2f), oscuro, k), Offset(l0 + w * 0.004f, w * 0.464f), Size(l1 - l0 - w * 0.006f, w * 0.027f))
        for (q in 0 until 7) drawLine(sombra(oscuro, 0.14f), Offset(l0 + w * 0.006f, w * (0.467f + 0.0035f * q)), Offset(l1 - w * 0.004f, w * (0.467f + 0.0035f * q)), 0.5f * u)
        drawRect(sombra(oscuro, 0.3f), Offset(l1 - w * 0.012f, w * 0.464f), Size(w * 0.01f, w * 0.027f))
        drawPath(poligono(l0 + w * 0.006f, w * 0.452f, l1 + w * 0.002f, w * 0.452f, l1, w * 0.465f, l0, w * 0.465f), n(CUERO, oscuro, k))
        drawLine(destello(oscuro, 0.3f, k), Offset(l0 + w * 0.006f, w * 0.4525f), Offset(l1, w * 0.4525f), 0.8f * u)
        drawRect(n(mezcla(CUERO, GRANA_HONDA, 0.3f), oscuro, k), Offset(l0, w * 0.462f), Size(l1 - l0, w * 0.004f))
        // Las cantoneras de acero en las cuatro esquinas.
        listOf(l0 to 1f, l1 to -1f).forEach { (x, s) ->
            drawPath(poligono(x, w * 0.452f, x + s * w * 0.018f, w * 0.452f, x + s * w * 0.016f, w * 0.466f, x, w * 0.466f), n(ACERO_CLARO, oscuro, k))
            drawPath(poligono(x, w * 0.488f, x + s * w * 0.016f, w * 0.488f, x + s * w * 0.018f, w * 0.4975f, x, w * 0.4975f), n(ACERO_MEDIO, oscuro, k))
            drawCircle(n(ACERO_HONDO, oscuro, k), 0.9f * u, Offset(x + s * w * 0.008f, w * 0.459f))
        }
        // Los dos broches: correas de acero que cruzan el canto de las hojas.
        listOf(w * 0.066f, w * 0.162f).forEach { x ->
            drawRect(sombra(oscuro, 0.35f), Offset(x + 1.2f * u, w * 0.464f), Size(w * 0.012f, w * 0.027f))
            metal(rect(x - w * 0.006f, w * 0.461f, x + w * 0.006f, w * 0.492f), Rect(x - w * 0.006f, w * 0.461f, x + w * 0.006f, w * 0.492f), ACERO_MEDIO, oscuro, k)
            drawRect(n(ACERO_HONDO, oscuro, k), Offset(x - w * 0.0075f, w * 0.474f), Size(w * 0.015f, w * 0.007f))
            drawCircle(destello(oscuro, 0.6f, k), 1f * u, Offset(x - w * 0.003f, w * 0.4765f))
        }
        // El estuche del micrómetro: la caja de cuero sobre el libro y la tapa abierta, forrada de fieltro carmesí.
        val c0 = w * 0.06f; val c1 = w * 0.168f
        drawRect(sombra(oscuro, 0.3f), Offset(c0 + w * 0.008f, w * 0.449f), Size(c1 - c0, w * 0.004f))
        val tapa = poligono(c0 + w * 0.002f, w * 0.379f, c1 - w * 0.002f, w * 0.379f, c1, w * 0.431f, c0, w * 0.431f)
        sombraEnMuro(tapa, Offset((c0 + c1) / 2, w * 0.4f), w, oscuro, 0.8f, 0.6f)
        drawPath(tapa, n(CUERO, oscuro, k))
        val fieltro = Rect(c0 + w * 0.006f, w * 0.384f, c1 - w * 0.006f, w * 0.428f)
        drawRect(n(mezcla(GRANA, GRANA_HONDA, 0.4f), oscuro, k), fieltro.topLeft, fieltro.size)
        drawRect(sombra(oscuro, 0.3f), fieltro.topLeft, Size(fieltro.width, 2f * u))
        drawRect(sombra(oscuro, 0.25f), fieltro.topLeft, Size(2f * u, fieltro.height))
        drawLine(destello(oscuro, 0.35f, k), Offset(c0 + w * 0.002f, w * 0.38f), Offset(c1 - w * 0.002f, w * 0.38f), 0.8f * u)
        // El micrómetro en su hueco del fieltro: el arco, el yunque, el husillo, el tambor graduado y el trinquete.
        val my = w * 0.398f
        val arco = Path().apply {
            moveTo(w * 0.074f, my - w * 0.004f); lineTo(w * 0.081f, my - w * 0.004f)
            cubicTo(w * 0.081f, my + w * 0.012f, w * 0.112f, my + w * 0.014f, w * 0.114f, my - w * 0.004f)
            lineTo(w * 0.121f, my - w * 0.004f)
            cubicTo(w * 0.12f, my + w * 0.024f, w * 0.075f, my + w * 0.024f, w * 0.074f, my - w * 0.004f)
            close()
        }
        translate(1f * u, 1.2f * u) { drawPath(arco, sombra(oscuro, 0.45f)) }
        drawPath(arco, n(mezcla(ACERO_HONDO, CUERO, 0.2f), oscuro, k))
        drawPath(Path().apply { moveTo(w * 0.076f, my - w * 0.002f); cubicTo(w * 0.077f, my + w * 0.018f, w * 0.116f, my + w * 0.018f, w * 0.118f, my - w * 0.002f) }, destello(oscuro, 0.35f, k), style = Stroke(0.7f * u))
        drawRect(n(ACERO_CLARO, oscuro, k), Offset(w * 0.074f, my - w * 0.008f), Size(w * 0.007f, w * 0.006f))
        drawRect(n(ACERO_CLARO, oscuro, k), Offset(w * 0.088f, my - w * 0.0065f), Size(w * 0.033f, w * 0.004f))
        drawLine(destello(oscuro, 0.6f, k), Offset(w * 0.088f, my - w * 0.0062f), Offset(w * 0.12f, my - w * 0.0062f), 0.5f * u)
        val tambor = Rect(w * 0.121f, my - w * 0.009f, w * 0.139f, my + w * 0.0005f)
        metal(rect(tambor.left, tambor.top, tambor.right, tambor.bottom), tambor, ACERO_CLARO, oscuro, k)
        drawLine(n(ACERO_HONDO, oscuro, k), Offset(tambor.left, my - w * 0.0045f), Offset(tambor.right, my - w * 0.0045f), 0.5f * u)
        for (q in 0 until 5) drawLine(n(ACERO_HONDO, oscuro, k), Offset(tambor.left + w * 0.003f * (q + 0.5f), my - w * 0.0045f), Offset(tambor.left + w * 0.003f * (q + 0.5f), my - w * 0.0072f), 0.4f * u)
        val dedal = Rect(w * 0.139f, my - w * 0.0115f, w * 0.154f, my + w * 0.003f)
        metal(rect(dedal.left, dedal.top, dedal.right, dedal.bottom), dedal, ACERO_MEDIO, oscuro, k)
        for (q in 0 until 4) drawLine(sombra(oscuro, 0.4f), Offset(dedal.left + w * 0.0035f * (q + 0.6f), dedal.top), Offset(dedal.left + w * 0.0035f * (q + 0.6f), dedal.bottom), 0.5f * u)
        drawRect(n(ACERO_MEDIO, oscuro, k), Offset(w * 0.154f, my - w * 0.0075f), Size(w * 0.007f, w * 0.007f))
        // La caja, con su cierre de latón.
        val caja = Rect(c0, w * 0.431f, c1, w * 0.452f)
        drawRect(n(CUERO, oscuro, k), caja.topLeft, caja.size)
        drawRect(n(mezcla(CUERO, LUZ_FRIA, 0.12f), oscuro, k), caja.topLeft, Size(caja.width, w * 0.0035f))
        drawRect(sombra(oscuro, 0.4f), Offset(caja.right - w * 0.008f, caja.top), Size(w * 0.008f, caja.height))
        drawRect(sombra(oscuro, 0.35f), Offset(caja.left, caja.bottom - 1.4f * u), Size(caja.width, 1.4f * u))
        drawRect(n(LATON_VIVO, oscuro, k), Offset(caja.center.x - w * 0.006f, caja.top + w * 0.004f), Size(w * 0.012f, w * 0.008f))
        drawRect(destello(oscuro, 0.6f, k), Offset(caja.center.x - w * 0.006f, caja.top + w * 0.004f), Size(w * 0.012f, 0.8f * u))
    }

    private fun siluetaGuantelete(w: Float): Path {
        val cx = w * 0.315f
        val partes = mutableListOf(
            poligono(cx - w * 0.046f, w * 0.352f, cx + w * 0.046f, w * 0.352f, cx + w * 0.08f, w * 0.446f, cx - w * 0.08f, w * 0.446f),
            poligono(cx - w * 0.05f, w * 0.355f, cx - w * 0.066f, w * 0.272f, cx + w * 0.066f, w * 0.272f, cx + w * 0.05f, w * 0.355f),
            rect(cx - w * 0.009f, w * 0.44f, cx + w * 0.009f, w * 0.478f),
            rect(cx - w * 0.072f, w * 0.476f, cx + w * 0.072f, w * 0.502f),
        )
        DEDOS.forEach { d ->
            val b = Offset(cx + w * d.dx, w * 0.276f)
            val ang = Math.toRadians(d.giro.toDouble())
            val top = Offset(b.x + w * d.largo * sin(ang).toFloat(), b.y - w * d.largo * cos(ang).toFloat())
            val nx = cos(ang).toFloat() * w * d.ancho / 2; val ny = sin(ang).toFloat() * w * d.ancho / 2
            partes += poligono(b.x - nx, b.y - ny, top.x - nx, top.y - ny, top.x + nx, top.y + ny, b.x + nx, b.y + ny)
        }
        partes += poligono(cx - w * 0.05f, w * 0.335f, cx - w * 0.03f, w * 0.31f, cx - w * 0.098f, w * 0.262f, cx - w * 0.112f, w * 0.282f)
        return unir(partes)
    }

    private class Dedo(val dx: Float, val largo: Float, val ancho: Float, val giro: Float)

    private val DEDOS = listOf(Dedo(-0.047f, 0.104f, 0.026f, -6f), Dedo(-0.016f, 0.126f, 0.027f, -1.5f), Dedo(0.015f, 0.114f, 0.026f, 3f), Dedo(0.044f, 0.088f, 0.022f, 9f))

    /** Una lama de la armadura: la placa con su canto de luz, su sombra abajo y el lado que no mira a la luz. */
    private fun DrawScope.lama(r: Rect, oscuro: Boolean, k: Float, izq: Boolean, radio: Float) {
        val u = density
        // Una lama curva, como una escama: los dos bordes arqueados hacia arriba (envuelve el dedo).
        val c = minOf(r.height * 0.3f, r.width * 0.16f)
        val p = Path().apply {
            moveTo(r.left, r.top + c)
            cubicTo(r.left, r.top - c * 0.25f, r.right, r.top - c * 0.25f, r.right, r.top + c)
            lineTo(r.right, r.bottom)
            cubicTo(r.right - r.width * 0.1f, r.bottom - c * 1.1f, r.left + r.width * 0.1f, r.bottom - c * 1.1f, r.left, r.bottom)
            close()
        }
        translate(0f, 1.6f * u) { drawPath(p, sombra(oscuro, 0.5f)) }
        metal(p, r, ACERO_CLARO, oscuro, k, izq)
        clipPath(p) { drawRect(sombra(oscuro, 0.22f), Offset(r.left, r.bottom - r.height * 0.35f), Size(r.width, r.height * 0.35f)) }
        drawPath(Path().apply { moveTo(r.left + r.width * 0.12f, r.top + c * 0.75f); cubicTo(r.left + r.width * 0.3f, r.top + c * 0.05f, r.right - r.width * 0.3f, r.top + c * 0.05f, r.right - r.width * 0.12f, r.top + c * 0.75f) },
            destello(oscuro, 0.75f, k), style = Stroke(1f * u))
    }

    /** Un dedo del guantelete, en vertical desde su base: el cuero, tres lamas y la punta, girado [giro] grados. */
    private fun DrawScope.dedo(b: Offset, largo: Float, ancho: Float, giro: Float, oscuro: Boolean, k: Float, izq: Boolean) {
        val u = density
        rotate(giro, b) {
            drawRoundRect(n(CUERO, oscuro, k), Offset(b.x - ancho * 0.44f, b.y - largo), Size(ancho * 0.88f, largo), CornerRadius(ancho * 0.4f))
            // La punta: redonda, más angosta, un punto vuelta hacia delante (su cima en sombra).
            val punta = Path().apply {
                moveTo(b.x - ancho * 0.42f, b.y - largo * 0.74f)
                lineTo(b.x - ancho * 0.4f, b.y - largo * 0.9f)
                cubicTo(b.x - ancho * 0.38f, b.y - largo * 1.02f, b.x + ancho * 0.38f, b.y - largo * 1.02f, b.x + ancho * 0.4f, b.y - largo * 0.9f)
                lineTo(b.x + ancho * 0.42f, b.y - largo * 0.74f); close()
            }
            val cajaP = Rect(b.x - ancho * 0.42f, b.y - largo, b.x + ancho * 0.42f, b.y - largo * 0.74f)
            translate(0f, 1.4f * u) { drawPath(punta, sombra(oscuro, 0.5f)) }
            metal(punta, cajaP, ACERO_CLARO, oscuro, k, izq)
            clipPath(punta) { drawRect(sombra(oscuro, 0.3f), Offset(cajaP.left, cajaP.top), Size(cajaP.width, largo * 0.07f)) }
            drawCircle(destello(oscuro, 0.7f, k), ancho * 0.09f, Offset(b.x + (if (izq) -1 else 1) * ancho * 0.14f, b.y - largo * 0.9f))
            val cortes = listOf(0.8f to 0.54f, 0.58f to 0.3f, 0.35f to 0.0f)
            cortes.forEachIndexed { i, (a, z) ->
                val ensancha = 0.5f + 0.025f * i
                lama(Rect(b.x - ancho * ensancha, b.y - largo * a, b.x + ancho * ensancha, b.y - largo * z), oscuro, k, izq, ancho * 0.18f)
                drawCircle(n(LATON_VIVO, oscuro, k), ancho * 0.07f, Offset(b.x + (if (izq) -1 else 1) * ancho * 0.32f, b.y - largo * (a + z) / 2))
            }
        }
    }

    /** El guantelete de acero articulado sobre su soporte: la manopla, los dedos, el pulgar y la bocamanga acampanada. */
    private fun DrawScope.guantelete(w: Float, oscuro: Boolean) {
        val u = density
        val cx = w * 0.315f
        val k = if (oscuro) calor(Offset(cx + w * 0.06f, w * 0.33f), w) else 0f
        val izq = !oscuro
        // El soporte: un dado de acero con su placa y un poste que entra en la bocamanga.
        sombraEnMuro(siluetaGuantelete(w), Offset(cx, w * 0.33f), w, oscuro)
        val dado = Rect(cx - w * 0.072f, w * 0.476f, cx + w * 0.072f, w * 0.502f)
        drawOval(sombra(oscuro, 0.4f), Offset(dado.left + w * 0.012f, dado.bottom - w * 0.006f), Size(dado.width + w * 0.012f, w * 0.01f))
        metal(rect(dado.left, dado.top, dado.right, dado.bottom), dado, ACERO_HONDO, oscuro, k, izq)
        drawRect(n(mezcla(ACERO_MEDIO, ACERO_CLARO, 0.3f), oscuro, k), dado.topLeft, Size(dado.width, w * 0.004f))
        drawRect(n(LATON_VIVO, oscuro, k), Offset(cx - w * 0.026f, dado.top + w * 0.008f), Size(w * 0.052f, w * 0.012f))
        drawRect(n(LATON_HONDO, oscuro, k), Offset(cx - w * 0.026f, dado.top + w * 0.019f), Size(w * 0.052f, 1f * u))
        drawRect(destello(oscuro, 0.5f, k), Offset(cx - w * 0.026f, dado.top + w * 0.008f), Size(w * 0.052f, 0.9f * u))
        metal(rect(cx - w * 0.009f, w * 0.44f, cx + w * 0.009f, w * 0.477f), Rect(cx - w * 0.009f, w * 0.44f, cx + w * 0.009f, w * 0.477f), ACERO_MEDIO, oscuro, k, izq)
        // La bocamanga: acampanada, con su borde enrollado, dos estrías y remaches de latón.
        val boca = Path().apply {
            moveTo(cx - w * 0.046f, w * 0.352f); lineTo(cx + w * 0.046f, w * 0.352f)
            cubicTo(cx + w * 0.052f, w * 0.39f, cx + w * 0.068f, w * 0.42f, cx + w * 0.08f, w * 0.44f)
            cubicTo(cx + w * 0.04f, w * 0.452f, cx - w * 0.04f, w * 0.452f, cx - w * 0.08f, w * 0.44f)
            cubicTo(cx - w * 0.068f, w * 0.42f, cx - w * 0.052f, w * 0.39f, cx - w * 0.046f, w * 0.352f)
            close()
        }
        metal(boca, Rect(cx - w * 0.08f, w * 0.35f, cx + w * 0.08f, w * 0.452f), ACERO_CLARO, oscuro, k, izq)
        clipPath(boca) {
            listOf(-0.5f, 0f, 0.5f).forEach { f ->
                val a = Offset(cx + w * 0.04f * f, w * 0.356f); val b = Offset(cx + w * 0.075f * f, w * 0.447f)
                drawLine(sombra(oscuro, 0.3f), a + Offset(1.2f * u, 0f), b + Offset(1.2f * u, 0f), 1.2f * u)
                drawLine(destello(oscuro, 0.45f, k), a, b, 0.8f * u)
            }
        }
        drawPath(Path().apply { moveTo(cx - w * 0.08f, w * 0.44f); cubicTo(cx - w * 0.04f, w * 0.452f, cx + w * 0.04f, w * 0.452f, cx + w * 0.08f, w * 0.44f) }, n(ACERO_MEDIO, oscuro, k), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(cx - w * 0.078f, w * 0.437f); cubicTo(cx - w * 0.04f, w * 0.448f, cx + w * 0.0f, w * 0.449f, cx + w * 0.03f, w * 0.446f) }, destello(oscuro, 0.6f, k), style = Stroke(1f * u))
        listOf(-0.62f, -0.2f, 0.2f, 0.62f).forEach { f -> roblon(Offset(cx + w * 0.07f * f, w * 0.432f + w * 0.006f * kotlin.math.abs(f)), w * 0.0036f, oscuro, k, LATON_VIVO) }
        // La muñeca: dos lamas que entran en la bocamanga.
        lama(Rect(cx - w * 0.05f, w * 0.335f, cx + w * 0.05f, w * 0.358f), oscuro, k, izq, w * 0.006f)
        // El pulgar, abierto hacia la izquierda, que nace por detrás del dorso.
        dedo(Offset(cx - w * 0.036f, w * 0.318f), w * 0.082f, w * 0.03f, -44f, oscuro, k, izq)
        // La manopla: la placa del dorso, ancha arriba, con dos lamas y la cresta del centro.
        val dorso = Path().apply {
            moveTo(cx - w * 0.05f, w * 0.338f); lineTo(cx + w * 0.05f, w * 0.338f)
            cubicTo(cx + w * 0.058f, w * 0.31f, cx + w * 0.064f, w * 0.29f, cx + w * 0.066f, w * 0.276f)
            lineTo(cx - w * 0.066f, w * 0.276f)
            cubicTo(cx - w * 0.064f, w * 0.29f, cx - w * 0.058f, w * 0.31f, cx - w * 0.05f, w * 0.338f)
            close()
        }
        metal(dorso, Rect(cx - w * 0.066f, w * 0.276f, cx + w * 0.066f, w * 0.338f), ACERO_CLARO, oscuro, k, izq)
        clipPath(dorso) {
            listOf(0.296f, 0.316f).forEach { y ->
                drawLine(sombra(oscuro, 0.45f), Offset(cx - w * 0.07f, w * y + 1f * u), Offset(cx + w * 0.07f, w * y + 1f * u), 1.4f * u)
                drawLine(destello(oscuro, 0.55f, k), Offset(cx - w * 0.07f, w * y + 2.4f * u), Offset(cx + w * 0.07f, w * y + 2.4f * u), 0.8f * u)
            }
            drawLine(destello(oscuro, 0.5f, k), Offset(cx - 1f * u, w * 0.28f), Offset(cx - 1f * u, w * 0.336f), 1f * u)
            drawLine(sombra(oscuro, 0.35f), Offset(cx + 1f * u, w * 0.28f), Offset(cx + 1f * u, w * 0.336f), 1f * u)
        }
        listOf(-1f, 1f).forEach { s -> listOf(0.288f, 0.326f).forEach { y -> roblon(Offset(cx + s * w * 0.052f, w * y), w * 0.0032f, oscuro, k, LATON_VIVO) } }
        // Los nudillos: una cresta con cuatro bultos.
        val nud = Rect(cx - w * 0.068f, w * 0.264f, cx + w * 0.068f, w * 0.284f)
        DEDOS.forEach { d -> dedo(Offset(cx + w * d.dx, w * 0.276f), w * d.largo, w * d.ancho, d.giro, oscuro, k, izq) }
        lama(nud, oscuro, k, izq, w * 0.008f)
        DEDOS.forEach { d ->
            val p = Offset(cx + w * d.dx, nud.center.y)
            drawOval(n(ACERO_CLARO, oscuro, k), Offset(p.x - w * 0.011f, p.y - w * 0.0075f), Size(w * 0.022f, w * 0.015f))
            drawArc(destello(oscuro, 0.75f, k), 190f, 90f, false, Offset(p.x - w * 0.009f, p.y - w * 0.006f), Size(w * 0.018f, w * 0.012f), style = Stroke(1f * u))
            drawArc(sombra(oscuro, 0.4f), 10f, 120f, false, Offset(p.x - w * 0.011f, p.y - w * 0.0075f), Size(w * 0.022f, w * 0.015f), style = Stroke(1f * u))
        }
        // De noche, el filo cálido del lado que mira a las velas.
        if (oscuro) drawLine(LLAMA.copy(alpha = 0.45f * k + 0.1f), Offset(cx + w * 0.079f, w * 0.44f), Offset(cx + w * 0.05f, w * 0.358f), 1.2f * u)
    }

    /** El plano enrollado sobre el tablero, que se desenrolla y cuelga por el canto, con sus pesas de latón y el compás de pie. */
    private fun DrawScope.plano(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(w * 0.55f, w * 0.49f), w) else 0f
        val azul = n(AZUL_PLANO, oscuro, k)
        val trazo = n(TRAZO, oscuro, k).copy(alpha = 0.55f)
        // La hoja tendida y la que cae por el canto, con la curva de la arista.
        val tendida = poligono(w * 0.42f, w * 0.48f, w * 0.612f, w * 0.48f, w * 0.627f, w * TAB_F, w * 0.405f, w * TAB_F)
        val cae = Path().apply {
            moveTo(w * 0.405f, w * TAB_F); lineTo(w * 0.627f, w * TAB_F)
            cubicTo(w * 0.632f, w * 0.53f, w * 0.628f, w * 0.55f, w * 0.63f, w * 0.566f)
            cubicTo(w * 0.56f, w * 0.572f, w * 0.48f, w * 0.57f, w * 0.41f, w * 0.574f)
            cubicTo(w * 0.408f, w * 0.55f, w * 0.403f, w * 0.53f, w * 0.405f, w * TAB_F)
            close()
        }
        translate(w * 0.008f, w * 0.006f) { drawPath(cae, sombra(oscuro, 0.4f)) }
        drawPath(tendida, n(mezcla(AZUL_PLANO, TRAZO, 0.1f), oscuro, k))
        drawPath(cae, azul)
        clipPath(cae) {
            // La cuadrícula y un engrane a medio acotar.
            var x = w * 0.41f
            while (x < w * 0.63f) { drawLine(trazo.copy(alpha = 0.18f), Offset(x, w * TAB_F), Offset(x, w * 0.58f), 0.5f * u); x += w * 0.012f }
            var y = w * TAB_F
            while (y < w * 0.58f) { drawLine(trazo.copy(alpha = 0.18f), Offset(w * 0.4f, y), Offset(w * 0.64f, y), 0.5f * u); y += w * 0.012f }
            drawPath(engrane(Offset(w * 0.47f, w * 0.538f), w * 0.022f, 9), trazo, style = Stroke(0.9f * u))
            drawCircle(trazo, w * 0.008f, Offset(w * 0.47f, w * 0.538f), style = Stroke(0.9f * u))
            drawLine(trazo, Offset(w * 0.5f, w * 0.538f), Offset(w * 0.6f, w * 0.538f), 0.7f * u)
            drawLine(trazo, Offset(w * 0.5f, w * 0.528f), Offset(w * 0.6f, w * 0.528f), 0.7f * u)
            drawLine(trazo, Offset(w * 0.6f, w * 0.522f), Offset(w * 0.6f, w * 0.56f), 0.7f * u)
            drawRect(trazo, Offset(w * 0.56f, w * 0.548f), Size(w * 0.05f, w * 0.014f), style = Stroke(0.6f * u))
            // La arista: la hoja coge la luz al doblar y queda en sombra justo debajo; el pie curvado hacia fuera.
            drawRect(brillo(oscuro, 0.35f, k + 0.2f), Offset(w * 0.4f, w * TAB_F), Size(w * 0.24f, w * 0.004f))
            drawRect(sombra(oscuro, 0.25f), Offset(w * 0.4f, w * (TAB_F + 0.006f)), Size(w * 0.24f, w * 0.01f))
            drawRect(brillo(oscuro, 0.22f, k), Offset(w * 0.4f, w * 0.56f), Size(w * 0.24f, w * 0.006f))
            drawRect(sombra(oscuro, 0.2f), Offset(w * 0.6f, w * TAB_F), Size(w * 0.04f, w * 0.07f))
        }
        clipPath(tendida) { for (q in 0 until 5) drawLine(trazo.copy(alpha = 0.22f), Offset(w * (0.43f + 0.04f * q), w * 0.48f), Offset(w * (0.42f + 0.044f * q), w * TAB_F), 0.5f * u) }
        // El rollo: un cilindro con la espiral de las vueltas en su boca.
        val rollo = Rect(w * 0.418f, w * 0.456f, w * 0.616f, w * 0.482f)
        sombraEnMuro(rect(rollo.left, rollo.top, rollo.right, rollo.bottom), rollo.center, w, oscuro, 0.7f, 0.5f)
        drawRect(n(mezcla(AZUL_PLANO, TRAZO, 0.12f), oscuro, k), rollo.topLeft, rollo.size)
        drawRect(brillo(oscuro, 0.3f, k + 0.1f), Offset(rollo.left, rollo.top + rollo.height * 0.15f), Size(rollo.width, rollo.height * 0.2f))
        drawRect(sombra(oscuro, 0.35f), Offset(rollo.left, rollo.top + rollo.height * 0.65f), Size(rollo.width, rollo.height * 0.35f))
        listOf(rollo.left, rollo.right).forEachIndexed { i, x ->
            drawOval(n(mezcla(TRAZO, AZUL_PLANO, 0.35f), oscuro, k), Offset(x - rollo.height * 0.35f, rollo.top), Size(rollo.height * 0.7f, rollo.height))
            for (q in 1..3) drawOval(n(AZUL_PLANO, oscuro, k).copy(alpha = 0.7f), Offset(x - rollo.height * 0.35f * (1f - q * 0.22f), rollo.top + rollo.height * 0.5f * q * 0.22f), Size(rollo.height * 0.7f * (1f - q * 0.22f), rollo.height * (1f - q * 0.22f)), style = Stroke(0.6f * u))
            if (i == 1) drawOval(sombra(oscuro, 0.3f), Offset(x - rollo.height * 0.35f, rollo.top), Size(rollo.height * 0.7f, rollo.height), style = Stroke(0.8f * u))
        }
        // Las pesas de latón: cilindros con su perilla.
        listOf(w * 0.437f to w * 0.5f, w * 0.596f to w * 0.502f).forEach { (x, y) ->
            drawOval(sombra(oscuro, 0.4f), Offset(x - w * 0.008f, y - w * 0.003f), Size(w * 0.028f, w * 0.007f))
            val cuerpo = Rect(x - w * 0.012f, y - w * 0.018f, x + w * 0.012f, y)
            metal(rect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom), cuerpo, LATON_VIVO, oscuro, k)
            drawOval(n(LATON_BRILLO, oscuro, k), Offset(cuerpo.left, cuerpo.top - w * 0.003f), Size(cuerpo.width, w * 0.006f))
            drawOval(n(LATON_VIVO, oscuro, k), Offset(x - w * 0.0045f, cuerpo.top - w * 0.009f), Size(w * 0.009f, w * 0.008f))
            drawCircle(destello(oscuro, 0.7f, k), w * 0.0018f, Offset(x - w * 0.002f, cuerpo.top - w * 0.006f))
            drawLine(n(LATON_HONDO, oscuro, k), Offset(cuerpo.left, y - w * 0.006f), Offset(cuerpo.right, y - w * 0.006f), 0.7f * u)
        }
        // El compás de puntas, de pie y abierto.
        val bisagra = Offset(w * 0.523f, w * 0.43f)
        val pies = listOf(Offset(w * 0.497f, w * 0.502f), Offset(w * 0.55f, w * 0.499f))
        pies.forEach { p ->
            drawLine(sombra(oscuro, 0.4f), bisagra + Offset(2f * u, 1.5f * u), p + Offset(w * 0.012f, 0f), 1.8f * u)
        }
        pies.forEachIndexed { i, p ->
            drawLine(n(ACERO_MEDIO, oscuro, k), bisagra, p, 2.2f * u, StrokeCap.Round)
            drawLine(destello(oscuro, 0.7f, k), bisagra + Offset(-0.6f * u, 0f), p + Offset(-0.6f * u, -2f * u), 0.7f * u)
            drawLine(n(ACERO_HONDO, oscuro, k), p - (p - bisagra) * 0.12f, p, 1.2f * u)
        }
        drawCircle(n(LATON_VIVO, oscuro, k), w * 0.006f, bisagra)
        drawCircle(destello(oscuro, 0.7f, k), w * 0.002f, bisagra - Offset(w * 0.002f, w * 0.002f))
        drawLine(n(LATON_HONDO, oscuro, k), bisagra, bisagra - Offset(0f, w * 0.014f), 2f * u, StrokeCap.Round)
    }

    private fun siluetaAceitera(w: Float): Path = unir(listOf(
        ovalo(w * 0.626f, w * 0.464f, w * 0.684f, w * 0.5f),
        poligono(w * 0.641f, w * 0.47f, w * 0.669f, w * 0.47f, w * 0.661f, w * 0.44f, w * 0.649f, w * 0.44f),
        poligono(w * 0.652f, w * 0.444f, w * 0.658f, w * 0.446f, w * 0.604f, w * 0.372f, w * 0.601f, w * 0.374f),
    ))

    /** La aceitera de latón: el cuerpo bajo y redondo, el cono, el pico largo y la palanca de la bomba; su cerco de aceite. */
    private fun DrawScope.aceitera(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(w * 0.655f, w * 0.47f), w) else 0f
        sombraEnMuro(siluetaAceitera(w), Offset(w * 0.655f, w * 0.45f), w, oscuro)
        drawOval(n(mezcla(BETUN, OXIDO, 0.25f), oscuro, 0f).copy(alpha = 0.35f), Offset(w * 0.618f, w * 0.497f), Size(w * 0.085f, w * 0.009f))
        drawOval(sombra(oscuro, 0.45f), Offset(w * 0.632f, w * 0.495f), Size(w * 0.064f, w * 0.007f))
        // El pico: largo, fino, en diagonal hacia arriba a la izquierda.
        val pico = poligono(w * 0.652f, w * 0.444f, w * 0.659f, w * 0.447f, w * 0.6035f, w * 0.3725f, w * 0.6005f, w * 0.3745f)
        drawPath(pico, n(LATON_HONDO, oscuro, k))
        drawLine(n(LATON_BRILLO, oscuro, k), Offset(w * 0.652f, w * 0.444f), Offset(w * 0.601f, w * 0.374f), 0.8f * u)
        drawCircle(n(BETUN, oscuro, 0f), 1.1f * u, Offset(w * 0.6015f, w * 0.373f))
        // El cono y su collar.
        val cono = poligono(w * 0.641f, w * 0.47f, w * 0.669f, w * 0.47f, w * 0.661f, w * 0.441f, w * 0.649f, w * 0.441f)
        metal(cono, Rect(w * 0.641f, w * 0.44f, w * 0.669f, w * 0.47f), LATON_VIVO, oscuro, k)
        drawOval(n(LATON_HONDO, oscuro, k), Offset(w * 0.647f, w * 0.437f), Size(w * 0.016f, w * 0.007f))
        // El cuerpo, abombado, con su abolladura.
        val cuerpo = ovalo(w * 0.626f, w * 0.464f, w * 0.684f, w * 0.5f)
        metal(cuerpo, Rect(w * 0.626f, w * 0.464f, w * 0.684f, w * 0.5f), LATON_VIVO, oscuro, k)
        clipPath(cuerpo) {
            drawOval(brillo(oscuro, 0.4f, k + 0.2f), Offset(w * 0.633f, w * 0.468f), Size(w * 0.022f, w * 0.01f))
            drawOval(sombra(oscuro, 0.28f), Offset(w * 0.664f, w * 0.481f), Size(w * 0.012f, w * 0.009f))
            drawLine(n(LATON_HONDO, oscuro, k), Offset(w * 0.626f, w * 0.49f), Offset(w * 0.684f, w * 0.49f), 0.7f * u)
        }
        // La palanca de la bomba, al costado.
        drawPath(Path().apply { moveTo(w * 0.682f, w * 0.478f); cubicTo(w * 0.694f, w * 0.472f, w * 0.698f, w * 0.462f, w * 0.694f, w * 0.452f) }, n(ACERO_MEDIO, oscuro, k), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawCircle(n(ACERO_MEDIO, oscuro, k), 1.6f * u, Offset(w * 0.694f, w * 0.452f))
        if (oscuro) drawLine(LLAMA.copy(alpha = 0.55f * k), Offset(w * 0.683f, w * 0.472f), Offset(w * 0.681f, w * 0.494f), 1.2f * u)
    }

    /** Una vela del velero: dónde está, cuánto le queda y lo gruesa. */
    private class Vela(val x: Float, val alto: Float, val gruesa: Float, val ardida: Boolean = false)

    private val VELERO = listOf(Vela(0.745f, 0.066f, 0.024f), Vela(0.797f, 0.04f, 0.021f), Vela(0.853f, 0.086f, 0.025f), Vela(0.908f, 0.014f, 0.027f, ardida = true))
    private const val VELERO_Y = 0.472f

    private fun siluetaVelero(w: Float): Path = unir(VELERO.map { v -> rect(w * (v.x - v.gruesa / 2), w * (VELERO_Y - v.alto), w * (v.x + v.gruesa / 2), w * 0.49f) } +
        listOf(rect(w * 0.715f, w * 0.482f, w * 0.945f, w * 0.499f)))

    /** El velero de acero de los caídos: la bandeja con sus copas y las cuatro velas, una ya consumida; la cera derramada. */
    private fun DrawScope.velero(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) 1f else 0f
        sombraEnMuro(siluetaVelero(w), Offset(w * 0.83f, w * 0.45f), w, oscuro, if (oscuro) 0f else 1f)
        drawOval(sombra(oscuro, 0.4f), Offset(w * 0.722f, w * 0.495f), Size(w * 0.236f, w * 0.008f))
        // La bandeja: cara de arriba, canto y dos patas.
        val canto = Rect(w * 0.715f, w * 0.484f, w * 0.945f, w * 0.495f)
        drawRect(n(mezcla(ACERO_MEDIO, ACERO_CLARO, 0.4f), oscuro, k), Offset(canto.left + w * 0.004f, w * 0.479f), Size(canto.width - w * 0.008f, w * 0.0055f))
        metal(rect(canto.left, canto.top, canto.right, canto.bottom), canto, ACERO_MEDIO, oscuro, k * 0.8f)
        drawLine(destello(oscuro, 0.6f, k), Offset(canto.left, canto.top + 0.5f * u), Offset(canto.right, canto.top + 0.5f * u), 0.8f * u)
        listOf(0.726f, 0.934f).forEach { x -> drawRect(n(ACERO_HONDO, oscuro, k * 0.6f), Offset(w * x - w * 0.006f, canto.bottom), Size(w * 0.012f, w * 0.004f)) }
        // La cera que se derramó en la bandeja y cuelga del canto.
        listOf(0.76f to 0.016f, 0.84f to 0.02f, 0.9f to 0.026f).forEach { (x, a) -> drawOval(n(CERA, oscuro, k), Offset(w * (x - a / 2), w * 0.4785f), Size(w * a, w * 0.004f)) }
        listOf(0.772f to 0.011f, 0.86f to 0.007f, 0.913f to 0.013f).forEach { (x, l) ->
            drawLine(n(CERA, oscuro, k), Offset(w * x, canto.top), Offset(w * x + 0.3f * u, canto.top + w * l), 1.8f * u, StrokeCap.Round)
            drawCircle(n(CERA, oscuro, k), 1.2f * u, Offset(w * x + 0.3f * u, canto.top + w * l))
        }
        VELERO.forEach { v ->
            val g = w * v.gruesa; val cx = w * v.x
            // La copa de acero.
            val copa = poligono(cx - g * 0.78f, w * (VELERO_Y - 0.002f), cx + g * 0.78f, w * (VELERO_Y - 0.002f), cx + g * 0.5f, w * 0.481f, cx - g * 0.5f, w * 0.481f)
            metal(copa, Rect(cx - g * 0.78f, w * VELERO_Y, cx + g * 0.78f, w * 0.481f), ACERO_MEDIO, oscuro, k)
            drawRect(n(ACERO_HONDO, oscuro, k), Offset(cx - g * 0.3f, w * 0.48f), Size(g * 0.6f, w * 0.002f))
            drawOval(n(ACERO_CLARO, oscuro, k), Offset(cx - g * 0.8f, w * (VELERO_Y - 0.004f)), Size(g * 1.6f, w * 0.004f))
            // La vela, con su borde derretido, sus chorreras y la mecha.
            val vr = Rect(cx - g / 2, w * (VELERO_Y - v.alto), cx + g / 2, w * (VELERO_Y - 0.002f))
            val cera = n(CERA, oscuro, k)
            drawRect(cera, vr.topLeft, vr.size)
            drawRect(if (oscuro) LLAMA.copy(alpha = 0.2f) else LUZ_FRIA.copy(alpha = 0.4f), vr.topLeft, Size(g * 0.3f, vr.height))
            drawRect(sombra(oscuro, if (oscuro) 0.15f else 0.22f), Offset(vr.right - g * 0.3f, vr.top), Size(g * 0.3f, vr.height))
            drawOval(n(mezcla(CERA, SOMBRA_MUEBLE, 0.2f), oscuro, k), Offset(vr.left + g * 0.12f, vr.top - g * 0.08f), Size(g * 0.76f, g * 0.24f))
            listOf(0.18f to 0.5f, 0.74f to 0.8f).forEachIndexed { j, (fx, fl) ->
                if (v.ardida && j == 1) return@forEachIndexed
                val x = vr.left + g * fx
                drawLine(n(mezcla(CERA, LUZ_FRIA, 0.3f), oscuro, k), Offset(x, vr.top + g * 0.05f), Offset(x + 0.3f * u, vr.top + vr.height * fl), g * 0.17f, StrokeCap.Round)
            }
            if (v.ardida) {
                // El cabo consumido: la cera desbordada sobre la copa.
                drawOval(cera, Offset(cx - g * 0.85f, vr.top - g * 0.05f), Size(g * 1.7f, g * 0.45f))
                drawLine(n(BETUN, oscuro, 0f), Offset(cx, vr.top + g * 0.05f), Offset(cx + g * 0.18f, vr.top - g * 0.22f), 1f * u, StrokeCap.Round)
            } else drawLine(n(BETUN, oscuro, 0f), Offset(cx, vr.top + g * 0.02f), Offset(cx + g * 0.08f, vr.top - g * 0.3f), 1f * u, StrokeCap.Round)
        }
    }

    /** La llama de una vela encendida, con su resplandor. */
    private fun DrawScope.llama(p: Offset, s: Float) {
        mancha(p - Offset(0f, s * 0.6f), s * 5f, LLAMA.copy(alpha = 0.3f))
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.7f)
            cubicTo(p.x + s * 0.55f, p.y - s * 0.85f, p.x + s * 0.5f, p.y, p.x, p.y + s * 0.15f)
            cubicTo(p.x - s * 0.5f, p.y, p.x - s * 0.55f, p.y - s * 0.85f, p.x, p.y - s * 1.7f)
            close()
        }, LLAMA)
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.1f)
            cubicTo(p.x + s * 0.3f, p.y - s * 0.6f, p.x + s * 0.26f, p.y - s * 0.05f, p.x, p.y + s * 0.02f)
            cubicTo(p.x - s * 0.26f, p.y - s * 0.05f, p.x - s * 0.3f, p.y - s * 0.6f, p.x, p.y - s * 1.1f)
            close()
        }, Color(0xFFFFF4D6))
        drawOval(AZUL_PLANO.copy(alpha = 0.5f), Offset(p.x - s * 0.22f, p.y - s * 0.12f), Size(s * 0.44f, s * 0.26f))
    }

    // ---------------------------------------------------------------- el mueble

    /** El suelo de chapa estriada, de [y0] abajo: lágrimas en relieve, más juntas al fondo, con su mugre. */
    private fun DrawScope.suelo(w: Float, y0: Float, alto: Float, oscuro: Boolean) {
        val u = density
        drawRect(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.45f), oscuro, 0f), Offset(0f, y0), Size(w, alto - y0))
        drawRect(sombra(oscuro, 0.35f), Offset(0f, y0), Size(w, (alto - y0) * 0.18f))
        var y = y0 + w * 0.01f; var fila = 0
        while (y < alto) {
            val paso = w * (0.02f + 0.012f * (y - y0) / (alto - y0))
            var x = -w * 0.01f + (if (fila % 2 == 0) 0f else paso * 0.75f)
            while (x < w) {
                val s = if ((fila + (x / paso).toInt()) % 2 == 0) 1f else -1f
                val a = Offset(x - paso * 0.22f, y + s * paso * 0.1f); val b = Offset(x + paso * 0.22f, y - s * paso * 0.1f)
                drawLine(sombra(oscuro, 0.4f), a + Offset(0.6f * u, 0.8f * u), b + Offset(0.6f * u, 0.8f * u), 1.4f * u, StrokeCap.Round)
                drawLine(destello(oscuro, 0.22f, 0f), a, b, 1f * u, StrokeCap.Round)
                x += paso * 1.5f
            }
            y += paso * 0.75f; fila++
        }
        pinceladas(Rect(0f, y0, w, alto), if (oscuro) NOCHE else BETUN, 931, 14, u, 0.09f)
        drawOval(n(BETUN, oscuro, 0f).copy(alpha = 0.22f), Offset(w * 0.2f, y0 + (alto - y0) * 0.6f), Size(w * 0.16f, (alto - y0) * 0.14f))
    }

    /** Los montantes y travesaños del cuerpo: acero pavonado con remaches grandes y cantoneras de latón. */
    private fun DrawScope.cuerpo(d: Disposicion, w: Float, zoc: Float, oscuro: Boolean) {
        val u = density
        val c = Rect(w * BL, w * TAB_B, w * BR, zoc)
        val pav = PAVON
        // De arriba abajo, más apagado de noche según lo lejos de las velas.
        val tramos = if (oscuro) 48 else 1
        for (i in 0 until tramos) {
            val y0 = c.top + c.height * i / tramos; val y1 = c.top + c.height * (i + 1) / tramos
            drawRect(n(pav, oscuro, if (oscuro) luzBaja(Offset(0.8f, (y0 + y1) / 2 / w)) else 0f), Offset(c.left, y0), Size(c.width, y1 - y0 + 1f))
        }
        if (oscuro) drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.35f), Color.Transparent), c.left, c.left + c.width * 0.65f), c.topLeft, Size(c.width * 0.65f, c.height))
        else drawRect(Brush.horizontalGradient(listOf(LUZ_FRIA.copy(alpha = 0.06f), Color.Transparent, sombraMueble(0.14f)), c.left, c.right), c.topLeft, c.size)
        pinceladas(c, if (oscuro) NOCHE else SOMBRA_FRIA, 951, 20, u, 0.08f)
        fun k(x: Float, y: Float) = if (oscuro) luzBaja(Offset(x / w, y / w)) else 0f
        // Los cantos del cuerpo: luz a la izquierda, sombra a la derecha, y el costado que asoma.
        drawRect(destello(oscuro, 0.35f, 0.1f), c.topLeft, Size(1.4f * u, c.height))
        drawRect(sombra(oscuro, 0.5f), Offset(c.right - 2f * u, c.top), Size(2f * u, c.height))
        // Los montantes: una ranura de unión a cada lado de cada hueco, y remaches grandes.
        val m0 = (d.huecos[0].right + d.huecos[1].left) / 2
        listOf(c.left + (d.huecos[0].left - c.left) / 2, m0, d.huecos[1].right + (c.right - d.huecos[1].right) / 2).forEach { x ->
            d.filas.forEach { f ->
                listOf(0.22f, 0.78f).forEach { fy -> roblon(Offset(x, f.top + f.height * fy), w * 0.0058f, oscuro, k(x, f.top)) }
            }
        }
        d.filas.forEach { f ->
            val y = f.bottom + d.separacion / 2
            // Una junta en medio del travesaño (lejos del borde de las gavetas, donde va el aro que tapa el aura).
            drawLine(sombra(oscuro, 0.3f), Offset(c.left + 3f * u, y), Offset(c.right - 3f * u, y), 1f * u)
            drawLine(destello(oscuro, 0.2f, k(w * 0.8f, y)), Offset(c.left + 3f * u, y + 1f * u), Offset(c.right - 3f * u, y + 1f * u), 0.7f * u)
        }
        // Los cantos pulidos por el uso a la altura de la mano: el montante izquierdo y el del medio, brillantes.
        listOf(c.left + 1.5f * u, m0 - w * 0.008f).forEachIndexed { i, x ->
            for (q in 0 until 5) {
                val y = c.top + c.height * (0.28f + 0.3f * azar(970 + i, q))
                drawLine(destello(oscuro, 0.35f, k(x, y)), Offset(x, y), Offset(x, y + w * (0.03f + 0.05f * azar(971 + i, q))), 1.4f * u, StrokeCap.Round)
            }
        }
        // El friso: rejilla de ventilación a la izquierda y la placa de los tres galones bajo el velero.
        val friso = Rect(c.left, c.top, c.right, w * corona)
        drawRect(sombra(oscuro, 0.4f), Offset(c.left, friso.bottom - 1.4f * u), Size(c.width, 1.4f * u))
        drawRect(destello(oscuro, 0.2f, k(w * 0.8f, friso.bottom)), Offset(c.left, friso.bottom), Size(c.width, 1f * u))
        val rej = Rect(w * 0.075f, friso.top + friso.height * 0.28f, w * 0.31f, friso.bottom - friso.height * 0.28f)
        drawRect(sombra(oscuro, 0.3f), rej.topLeft, rej.size)
        for (q in 0 until 9) {
            val x = rej.left + rej.width * (q + 0.3f) / 9
            drawPath(poligono(x, rej.top + 2f * u, x + rej.width * 0.05f, rej.top + 2f * u, x + rej.width * 0.07f, rej.bottom - 2f * u, x + rej.width * 0.02f, rej.bottom - 2f * u), n(mezcla(BETUN, ACERO_HONDO, 0.4f), oscuro, 0f))
            drawLine(destello(oscuro, 0.35f, k(x, rej.top)), Offset(x + rej.width * 0.052f, rej.top + 2f * u), Offset(x + rej.width * 0.072f, rej.bottom - 2f * u), 0.8f * u)
        }
        drawRect(destello(oscuro, 0.3f, 0.1f), Offset(rej.left, rej.bottom), Size(rej.width, 1f * u))
        listOf(rej.topLeft, Offset(rej.right, rej.top), Offset(rej.left, rej.bottom), rej.bottomRight).forEach { roblon(it, w * 0.0035f, oscuro, k(it.x, it.y)) }
        val gal = Rect(w * 0.695f, friso.top + friso.height * 0.24f, w * 0.845f, friso.bottom - friso.height * 0.24f)
        val kg = if (oscuro) calor(gal.center, w) else 0f
        drawRect(sombra(oscuro, 0.4f), gal.topLeft + Offset(2f * u, 2f * u), gal.size)
        drawRect(n(LATON_VIVO, oscuro, kg), gal.topLeft, gal.size)
        drawRect(destello(oscuro, 0.5f, kg), gal.topLeft, Size(gal.width, 1f * u))
        drawRect(n(LATON_HONDO, oscuro, kg), Offset(gal.left, gal.bottom - 1.2f * u), Size(gal.width, 1.2f * u))
        for (q in 0 until 3) {
            val y = gal.top + gal.height * (0.28f + 0.2f * q)
            val ch = Path().apply { moveTo(gal.center.x - gal.width * 0.2f, y); lineTo(gal.center.x, y + gal.height * 0.16f); lineTo(gal.center.x + gal.width * 0.2f, y) }
            drawPath(ch, n(LATON_HONDO, oscuro, kg), style = Stroke(2f * u))
            translate(0f, 1f * u) { drawPath(ch, destello(oscuro, 0.45f, kg), style = Stroke(0.7f * u)) }
        }
        listOf(Offset(gal.left + 4f * u, gal.center.y), Offset(gal.right - 4f * u, gal.center.y)).forEach { roblon(it, 1.8f * u, oscuro, kg, LATON_VIVO) }
        listOf(friso.top + friso.height * 0.12f, friso.bottom - friso.height * 0.12f).forEach { y ->
            var x = c.left + w * 0.02f
            while (x < c.right - w * 0.01f) {
                if (!(y < friso.center.y && x > w * 0.4f && x < w * 0.63f)) roblon(Offset(x, y), w * 0.0042f, oscuro, k(x, y))
                x += w * 0.04f
            }
        }
        // Las cantoneras de latón en las cuatro esquinas.
        listOf(Triple(c.left, c.top, 1f to 1f), Triple(c.right, c.top, -1f to 1f), Triple(c.left, c.bottom, 1f to -1f), Triple(c.right, c.bottom, -1f to -1f)).forEach { (x, y, s) ->
            val l = w * 0.05f
            val p = poligono(x, y, x + s.first * l, y, x + s.first * l * 0.4f, y + s.second * l * 0.4f, x, y + s.second * l)
            val kc = if (oscuro) maxOf(calor(Offset(x, y), w), luzBaja(Offset(x / w, y / w))) else 0f
            translate(1.4f * u, 1.6f * u) { drawPath(p, sombra(oscuro, 0.4f)) }
            drawPath(p, n(LATON_VIVO, oscuro, kc))
            drawPath(p, n(LATON_HONDO, oscuro, kc), style = Stroke(0.8f * u))
            roblon(Offset(x + s.first * l * 0.22f, y + s.second * l * 0.22f), w * 0.0042f, oscuro, kc, LATON_VIVO)
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val b = d.base; val hb = b.height
        val rincon = b.top + hb * RINCON
        val zoc = b.top + hb * ZOC
        val pie = b.top + hb * PIES

        // ---- el muro, la arquería, la luz
        muro(w, rincon + 1f * u, oscuro)
        arqueria(w, oscuro)
        if (oscuro) halos(w) else luzDeLuminaria(w)

        // ---- lo colgado
        sombraEnMuro(rect(w * PANEL.left, w * PANEL.top, w * PANEL.right, w * PANEL.bottom), Offset(w * 0.12f, w * 0.2f), w, oscuro, 0.9f, 0.6f)
        panelHerramientas(w, oscuro)
        sombraEnMuro(rect(w * 0.523f, w * 0.1f, w * 0.567f, w * 0.144f), PIL * w, w, oscuro, 0.8f, 0.6f)
        piloto(w, oscuro)
        estandarte(w, oscuro)

        // ---- la sombra del mueble en el muro, a su derecha
        drawPath(poligono(w * TR, w * (TAB + 0.01f), w * (TR + 0.016f), w * (TAB + 0.02f), w * (TR + 0.016f), w * TAB_B, w * (BR + 0.03f), w * (TAB_B + 0.02f),
            w * (BR + 0.03f), rincon, w * BR, rincon, w * BR, w * TAB_B, w * TR, w * TAB_B), sombra(oscuro, if (oscuro) 0.4f else 0.3f))

        // ---- el suelo
        suelo(w, rincon, alto, oscuro)
        drawPath(poligono(w * (BL + 0.02f), pie - 1f * u, w * (BR + 0.01f), pie - 1f * u, w * (BR + 0.06f), pie + hb * 0.13f, w * (BL + 0.05f), pie + hb * 0.13f), sombra(oscuro, if (oscuro) 0.45f else 0.3f))

        // ---- el cuerpo
        cuerpo(d, w, zoc, oscuro)

        // ---- el tablero: la cara de arriba, el canto grueso pulido por el uso
        val kt = { x: Float -> if (oscuro) calor(Offset(x, w * TAB_F), w) else 0f }
        val cara = Rect(w * TL, w * TAB, w * TR, w * TAB_F)
        for (i in 0 until 6) {
            val x0 = cara.left + cara.width * i / 6; val x1 = cara.left + cara.width * (i + 1) / 6
            drawRect(n(mezcla(ACERO_MEDIO, ACERO_CLARO, 0.35f), oscuro, kt((x0 + x1) / 2)), Offset(x0, cara.top), Size(x1 - x0 + 1f, cara.height))
        }
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(LUZ_FRIA.copy(alpha = 0.22f), Color.Transparent, sombraMueble(0.1f)), cara.left, cara.right), cara.topLeft, cara.size)
        for (q in 0 until 14) {
            val x = cara.left + cara.width * azar(980, q); val y = cara.top + cara.height * azar(981, q)
            drawLine(destello(oscuro, 0.3f, kt(x)), Offset(x, y), Offset(x + w * (0.01f + 0.03f * azar(982, q)), y + (azar(983, q) - 0.5f) * 2f * u), 0.6f * u)
        }
        val canto = Rect(w * TL, w * TAB_F, w * TR, w * TAB_B)
        for (i in 0 until 6) {
            val x0 = canto.left + canto.width * i / 6; val x1 = canto.left + canto.width * (i + 1) / 6
            drawRect(n(ACERO_MEDIO, oscuro, kt((x0 + x1) / 2)), Offset(x0, canto.top), Size(x1 - x0 + 1f, canto.height))
        }
        drawRect(destello(oscuro, 0.75f, 0.6f), canto.topLeft, Size(canto.width, 1.4f * u))
        drawRect(sombra(oscuro, 0.3f), Offset(canto.left, canto.top + canto.height * 0.55f), Size(canto.width, canto.height * 0.45f))
        drawRect(sombra(oscuro, 0.55f), Offset(canto.left, canto.bottom - 1.6f * u), Size(canto.width, 1.6f * u))
        drawRect(destello(oscuro, 0.4f, 0f), Offset(canto.left, cara.top), Size(1.4f * u, canto.bottom - cara.top))
        drawRect(sombra(oscuro, 0.45f), Offset(canto.right - 1.8f * u, cara.top), Size(1.8f * u, canto.bottom - cara.top))
        // Lo pulido por las manos: una franja clara a media altura del canto.
        drawOval(destello(oscuro, 0.3f, 0.4f), Offset(w * 0.12f, canto.top + canto.height * 0.2f), Size(w * 0.22f, canto.height * 0.3f))
        drawOval(destello(oscuro, 0.22f, 0.6f), Offset(w * 0.66f, canto.top + canto.height * 0.2f), Size(w * 0.16f, canto.height * 0.3f))
        if (oscuro) clipRect(cara.left, cara.top, cara.right, canto.bottom) {
            withTransform({ scale(1f, 0.2f, Offset(w * 0.81f, w * TAB_F)) }) {
                drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.5f), LLAMA.copy(alpha = 0.18f), Color.Transparent), Offset(w * 0.81f, w * TAB_F), w * 0.3f), w * 0.3f, Offset(w * 0.81f, w * TAB_F))
            }
        }

        // ---- lo de encima, de atrás adelante
        velero(w, oscuro)
        aceitera(w, oscuro)
        plano(w, oscuro)
        libroYMicrometro(w, oscuro)
        guantelete(w, oscuro)

        // ---- el zócalo: un riel remachado y las franjas de peligro gastadas
        val z = Rect(w * (BL - 0.008f), b.top, w * (BR + 0.008f), zoc)
        val pav = mezcla(ACERO_HONDO, ACERO_MEDIO, 0.28f)
        drawRect(n(pav, oscuro, 0f), z.topLeft, z.size)
        val riel = z.top + z.height * 0.24f
        drawRect(destello(oscuro, 0.4f, 0.05f), z.topLeft, Size(z.width, 1.2f * u))
        drawRect(sombra(oscuro, 0.45f), Offset(z.left, riel), Size(z.width, 1.4f * u))
        var rx = z.left + w * 0.025f
        while (rx < z.right) { roblon(Offset(rx, z.top + z.height * 0.12f), w * 0.0042f, oscuro, 0f); rx += w * 0.05f }
        val franjas = Rect(z.left, riel + 1.4f * u, z.right, z.bottom - z.height * 0.06f)
        clipRect(franjas.left, franjas.top, franjas.right, franjas.bottom) {
            drawRect(n(BETUN, oscuro, 0f), franjas.topLeft, franjas.size)
            var x = franjas.left - franjas.height
            val paso = w * 0.06f
            while (x < franjas.right) {
                drawPath(poligono(x, franjas.bottom, x + franjas.height, franjas.top, x + franjas.height + paso / 2, franjas.top, x + paso / 2, franjas.bottom), n(PELIGRO, oscuro, 0f))
                x += paso
            }
            // El desgaste: desconchones donde asoma el acero y el roce de las botas abajo.
            for (q in 0 until 26) {
                val p = Offset(franjas.left + franjas.width * azar(990, q), franjas.top + franjas.height * (0.15f + 0.85f * azar(991, q).pow(0.6f)))
                val s = w * (0.003f + 0.009f * azar(992, q))
                drawOval(n(mezcla(ACERO_MEDIO, ACERO_HONDO, 0.3f), oscuro, 0f), Offset(p.x - s, p.y - s * 0.45f), Size(s * 2f, s * 0.9f))
            }
            for (q in 0 until 7) drawOval(n(BETUN, oscuro, 0f).copy(alpha = 0.28f), Offset(franjas.left + franjas.width * azar(993, q), franjas.bottom - franjas.height * 0.45f), Size(w * 0.06f, franjas.height * 0.45f))
            pinceladas(franjas, if (oscuro) NOCHE else SOMBRA_FRIA, 994, 10, u, 0.12f)
            if (!oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.2f)), franjas.left + franjas.width * 0.5f, franjas.right), franjas.topLeft, franjas.size)
            else drawRect(NOCHE.copy(alpha = 0.25f), franjas.topLeft, franjas.size)
        }
        drawRect(sombra(oscuro, 0.5f), Offset(z.left, z.bottom - z.height * 0.06f), Size(z.width, z.height * 0.06f))
        drawRect(sombra(oscuro, 0.45f), Offset(z.right - 2f * u, z.top), Size(2f * u, z.height))
        // Bajo el zócalo, la sombra y las patas de perfil con sus tacos.
        drawRect(sombra(oscuro, 0.6f), Offset(z.left, zoc), Size(z.width, pie - zoc))
        listOf(z.left to z.left + w * 0.07f, w * 0.465f to w * 0.535f, z.right - w * 0.07f to z.right).forEach { (xa, xb) ->
            val p = Rect(xa, zoc, xb, pie)
            drawRect(n(pav, oscuro, 0f), p.topLeft, p.size)
            drawRect(destello(oscuro, 0.3f, 0f), p.topLeft, Size(1.2f * u, p.height))
            drawRect(sombra(oscuro, 0.4f), Offset(p.right - p.width * 0.25f, p.top), Size(p.width * 0.25f, p.height))
            drawRect(n(BETUN, oscuro, 0f), Offset(p.left - w * 0.004f, p.bottom - w * 0.006f), Size(p.width + w * 0.008f, w * 0.006f))
            drawOval(sombra(oscuro, 0.45f), Offset(p.left, p.bottom - 1f * u), Size(p.width + w * 0.02f, 3.4f * u))
        }

        // ---- al pie: la cadena enrollada y la caja de acero, cortadas por el borde
        cadenaYCaja(w, b, oscuro)

        // ---- de noche: lo lejano se hunde y arden las velas
        if (oscuro) {
            val c = Offset(w * 0.79f, w * 0.41f)
            drawRect(Brush.radialGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.28f), NOCHE.copy(alpha = 0.55f)), c, w * 1.6f), Offset.Zero, size)
            VELERO.filter { !it.ardida }.forEach { v -> llama(Offset(w * v.x + w * v.gruesa * 0.06f, w * (VELERO_Y - v.alto) - w * v.gruesa * 0.35f), w * 0.0085f) }
            // El piloto conserva su color, aunque el velo lo cubra.
            drawCircle(PILOTO, w * 0.008f, PIL * w)
            drawCircle(Color(0xFFFFB0A0), w * 0.004f, PIL * w - Offset(w * 0.002f, w * 0.002f))
        }

        if (d.mas > 0) placa.let { pl -> placaLaton(d.enBase(pl), oscuro, 0f, blanca = false) }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    /** La caja de acero con asas, cortada por el borde derecho, y la cadena que sale de ella y se enrolla en la chapa. */
    private fun DrawScope.cadenaYCaja(w: Float, b: Rect, oscuro: Boolean) {
        val u = density
        val hb = b.height
        val top = b.top + hb * 0.5f; val bot = b.top + hb * 0.97f
        // La cadena enrollada en el suelo: tres vueltas de eslabones, de fuera adentro, y el tramo que sube a la caja.
        val cc = Offset(w * 0.69f, b.top + hb * 0.84f)
        val eslabones = mutableListOf<Pair<Offset, Float>>()
        var a = 0.0
        while (a < Math.PI * 5.2) {
            val r = 1f - (a / (Math.PI * 5.2)).toFloat() * 0.55f
            val p = Offset(cc.x + w * 0.075f * r * cos(a).toFloat(), cc.y + hb * 0.1f * r * sin(a).toFloat())
            eslabones += p to Math.toDegrees(a + Math.PI / 2).toFloat()
            a += 0.21 / r
        }
        val subida = listOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f).map { f -> Offset(cc.x + w * 0.075f + (w * 0.81f - cc.x - w * 0.075f) * f, cc.y - (cc.y - top - hb * 0.02f) * f.pow(1.6f)) }
        subida.forEach { p -> eslabones += p to -35f }
        drawOval(sombra(oscuro, 0.35f), Offset(cc.x - w * 0.08f, cc.y - hb * 0.08f), Size(w * 0.18f, hb * 0.24f))
        val acero = n(ACERO_MEDIO, oscuro, 0.12f)
        eslabones.forEachIndexed { i, (p, ang) ->
            rotate(ang, p) {
                if (i % 2 == 0) {
                    drawOval(sombra(oscuro, 0.4f), Offset(p.x - w * 0.007f + 1f * u, p.y - w * 0.004f + 1.2f * u), Size(w * 0.014f, w * 0.008f), style = Stroke(1.8f * u))
                    drawOval(acero, Offset(p.x - w * 0.007f, p.y - w * 0.004f), Size(w * 0.014f, w * 0.008f), style = Stroke(1.8f * u))
                    drawArc(destello(oscuro, 0.6f, 0f), 190f, 90f, false, Offset(p.x - w * 0.007f, p.y - w * 0.004f), Size(w * 0.014f, w * 0.008f), style = Stroke(0.8f * u))
                } else {
                    drawLine(acero, Offset(p.x - w * 0.007f, p.y), Offset(p.x + w * 0.007f, p.y), 2.2f * u, StrokeCap.Round)
                    drawLine(destello(oscuro, 0.5f, 0f), Offset(p.x - w * 0.005f, p.y - 0.7f * u), Offset(p.x + w * 0.004f, p.y - 0.7f * u), 0.6f * u)
                }
            }
        }
        // La caja: el costado que mira a la luz, la cara de arriba en escorzo y el frente, con sus nervios y el asa abatida.
        val frente = Rect(w * 0.81f, top, w * 1.04f, bot)
        drawOval(sombra(oscuro, 0.45f), Offset(frente.left - w * 0.01f, bot - hb * 0.04f), Size(w * 0.26f, hb * 0.08f))
        val costado = poligono(w * 0.79f, top - hb * 0.07f, frente.left, top, frente.left, bot, w * 0.79f, bot - hb * 0.05f)
        drawPath(costado, n(mezcla(PINTURA, ACERO_CLARO, 0.25f), oscuro, 0f))
        val arriba = poligono(w * 0.79f, top - hb * 0.07f, w * 1.04f, top - hb * 0.07f, w * 1.04f, top, frente.left, top)
        drawPath(arriba, n(mezcla(PINTURA, ACERO_CLARO, 0.4f), oscuro, 0f))
        drawRect(n(PINTURA, oscuro, 0f), frente.topLeft, frente.size)
        drawRect(sombra(oscuro, 0.18f), Offset(frente.left + frente.width * 0.4f, frente.top), Size(frente.width * 0.6f, frente.height))
        listOf(0.36f, 0.7f).forEach { f ->
            val y = frente.top + frente.height * f
            drawLine(destello(oscuro, 0.3f, 0f), Offset(frente.left, y), Offset(frente.right, y), 1f * u)
            drawLine(sombra(oscuro, 0.4f), Offset(frente.left, y + 2f * u), Offset(frente.right, y + 2f * u), 1.4f * u)
        }
        drawLine(destello(oscuro, 0.45f, 0f), Offset(frente.left, frente.top), Offset(frente.right, frente.top), 1.2f * u)
        drawLine(destello(oscuro, 0.4f, 0f), Offset(w * 0.79f, top - hb * 0.07f), Offset(frente.left, top), 1f * u)
        // El desgaste de la pintura en las aristas.
        for (q in 0 until 8) drawOval(n(ACERO_CLARO, oscuro, 0f).copy(alpha = 0.55f), Offset(frente.left + frente.width * azar(996, q) * 0.8f, frente.top - 1f * u + (if (q % 2 == 0) 0f else frente.height - 3f * u)), Size(w * 0.008f, 2.4f * u))
        // El asa abatida del frente y el asa del costado.
        val asa = Path().apply { moveTo(w * 0.86f, top + hb * 0.05f); lineTo(w * 0.86f, top + hb * 0.16f); lineTo(w * 0.96f, top + hb * 0.16f); lineTo(w * 0.96f, top + hb * 0.05f) }
        translate(1.2f * u, 1.6f * u) { drawPath(asa, sombra(oscuro, 0.5f), style = Stroke(2.4f * u)) }
        drawPath(asa, n(ACERO_HONDO, oscuro, 0f), style = Stroke(2.4f * u))
        drawLine(destello(oscuro, 0.5f, 0f), Offset(w * 0.862f, top + hb * 0.155f), Offset(w * 0.958f, top + hb * 0.155f), 0.7f * u)
        listOf(w * 0.86f, w * 0.96f).forEach { x -> drawRect(n(ACERO_HONDO, oscuro, 0f), Offset(x - w * 0.007f, top + hb * 0.02f), Size(w * 0.014f, hb * 0.05f)) }
        val asaL = Path().apply { moveTo(w * 0.796f, top + hb * 0.06f); cubicTo(w * 0.78f, top + hb * 0.1f, w * 0.78f, top + hb * 0.18f, w * 0.798f, top + hb * 0.22f) }
        drawPath(asaL, n(ACERO_HONDO, oscuro, 0f), style = Stroke(2.2f * u))
        // El último eslabón entra por la tapa entreabierta.
        drawRect(sombra(oscuro, 0.5f), Offset(frente.left, top - 1f * u), Size(frente.width, 2.2f * u))
    }

    // ---------------------------------------------------------------- los compartimentos

    private val PAVON = mezcla(ACERO_HONDO, ACERO_MEDIO, 0.28f)

    /**
     * El color del cuerpo del mueble en [fx], [fy] (fracciones del ancho), el mismo que pinta [cuerpo]: el pavonado,
     * apagado de noche según lo lejos de las velas, con el velo de la izquierda de noche o la luz y la sombra de día.
     */
    private fun colorCuerpo(fx: Float, fy: Float, oscuro: Boolean): Color {
        val base = n(PAVON, oscuro, if (oscuro) luzBaja(Offset(0.8f, fy)) else 0f)
        val t = ((fx - BL) / (BR - BL)).coerceIn(0f, 1f)
        return if (oscuro) mezcla(base, NOCHE, 0.35f * (1f - t / 0.65f).coerceIn(0f, 1f))
        else if (t < 0.5f) mezcla(base, LUZ_FRIA, 0.06f * (1f - 2f * t))
        else mezcla(base, sombraMueble(1f), 0.14f * (2f * t - 1f))
    }

    /**
     * El aro que tapa el aura en reposo de `Modifier.halo()`: la gaveta llena su hueco, así que el aura sería un marco
     * carmesí alrededor de cada una. Se pinta por fuera del hueco con el color del cuerpo y se compone con `SrcAtop` (como en
     * Helénico): en el dibujo normal cubre esa franja del cuerpo y en la capa vacía del aura no agrega nada. Al tocar la
     * gaveta el aura crece más allá del aro y se sigue viendo.
     */
    private fun DrawScope.cubreAura(c: EstadoCajon, oscuro: Boolean) {
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val x0 = lado + c.col * (anchoH + hueco); val y0 = corona + hueco + c.fila * (altoH + hueco)
        val px = size.width / anchoH
        val a = 3.2f * density
        val fa = a / px
        drawIntoCanvas { it.saveLayer(Rect(-a * 2, -a * 2, size.width + a * 2, size.height + a * 2), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        val tramos = 6
        for (i in 0 until tramos) {
            // Arriba y abajo, por tramos a lo ancho.
            val xa = size.width * i / tramos; val xb = size.width * (i + 1) / tramos
            val fx = x0 + anchoH * (i + 0.5f) / tramos
            drawRect(colorCuerpo(fx, y0 - fa / 2, oscuro), Offset(xa - (if (i == 0) a else 0f), -a), Size(xb - xa + (if (i == 0 || i == tramos - 1) a else 0f) + 1f, a), blendMode = BlendMode.Src)
            drawRect(colorCuerpo(fx, y0 + altoH + fa / 2, oscuro), Offset(xa - (if (i == 0) a else 0f), size.height), Size(xb - xa + (if (i == 0 || i == tramos - 1) a else 0f) + 1f, a), blendMode = BlendMode.Src)
        }
        for (i in 0 until 3) {
            // A los lados, por tramos a lo alto.
            val ya = size.height * i / 3; val yb = size.height * (i + 1) / 3
            val fy = y0 + altoH * (i + 0.5f) / 3
            drawRect(colorCuerpo(x0 - fa / 2, fy, oscuro), Offset(-a, ya), Size(a, yb - ya + 1f), blendMode = BlendMode.Src)
            drawRect(colorCuerpo(x0 + anchoH + fa / 2, fy, oscuro), Offset(size.width, ya), Size(a, yb - ya + 1f), blendMode = BlendMode.Src)
        }
        drawIntoCanvas { it.restore() }
    }

    /** La placa de latón estampada (o sin grabar): marco en relieve, un filete hundido, cuatro tornillos y su sombra. */
    private fun DrawScope.placaLaton(r: Rect, oscuro: Boolean, cerca: Float, blanca: Boolean) {
        val u = density
        val color = if (oscuro) mezcla(PLACA_NOCHE, PLACA_VELA, cerca * 1.3f) else LATON_VIVO
        drawRect(sombra(oscuro, 0.5f), r.topLeft + Offset(1.6f * u, 2f * u), r.size)
        drawRect(color, r.topLeft, r.size)
        drawRect(if (oscuro) LLAMA.copy(alpha = 0.3f * cerca + 0.08f) else LATON_BRILLO.copy(alpha = 0.8f), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(if (oscuro) LLAMA.copy(alpha = 0.2f * cerca + 0.05f) else LATON_BRILLO.copy(alpha = 0.6f), r.topLeft, Size(1.4f * u, r.height))
        drawRect(mezcla(color, LATON_HONDO, 0.6f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(mezcla(color, LATON_HONDO, 0.5f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
        if (!blanca) {
            val i = 3.4f * u
            drawRect(mezcla(color, LATON_HONDO, 0.45f), Offset(r.left + i, r.top + i), Size(r.width - 2 * i, r.height - 2 * i), style = Stroke(0.8f * u))
            drawRect(mezcla(color, LATON_BRILLO, 0.35f), Offset(r.left + i + 0.8f * u, r.top + i + 0.8f * u), Size(r.width - 2 * i, r.height - 2 * i), style = Stroke(0.5f * u))
        }
        // Un velo de pulido, de día, en el tercio izquierdo.
        if (!oscuro) drawPath(poligono(r.left + r.width * 0.06f, r.top, r.left + r.width * 0.16f, r.top, r.left + r.width * 0.1f, r.bottom, r.left, r.bottom), LUZ_FRIA.copy(alpha = 0.12f))
        listOf(Offset(r.left + 2.2f * u, r.top + 2.2f * u), Offset(r.right - 2.2f * u, r.top + 2.2f * u), Offset(r.left + 2.2f * u, r.bottom - 2.2f * u), Offset(r.right - 2.2f * u, r.bottom - 2.2f * u)).forEach {
            drawCircle(mezcla(color, LATON_HONDO, 0.5f), 1.2f * u, it)
            drawLine(mezcla(color, LATON_HONDO, 0.8f), it - Offset(0.9f * u, 0.5f * u), it + Offset(0.9f * u, 0.5f * u), 0.5f * u)
        }
    }

    /** Los planos azules que asoman por arriba de una gaveta entreabierta: más cuantos más guarda. */
    private fun DrawScope.planosAsomados(w: Float, arriba: Float, k: Int, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        drawRect(n(Color(0xFF14181E), oscuro, 0f), Offset.Zero, Size(w, arriba + 3f * u))
        val m = k.coerceIn(1, 9)
        for (i in 0 until m) {
            val ancho = w * (0.07f + 0.05f * azar(semilla, i + 50))
            val x = w * (0.06f + 0.88f * (i + 0.5f) / m) - ancho / 2 + (azar(semilla, i + 60) - 0.5f) * w * 0.03f
            val y0 = arriba * (0.08f + 0.5f * azar(semilla, i)) + 1f * u
            val incl = (azar(semilla, i + 70) - 0.5f) * w * 0.016f
            val hoja = poligono(x, arriba + 4f * u, x + incl, y0, x + ancho + incl, y0 + (azar(semilla, i + 80) - 0.5f) * 3f * u, x + ancho, arriba + 4f * u)
            drawPath(hoja, n(if (i % 3 == 1) mezcla(AZUL_PLANO, TRAZO, 0.2f) else AZUL_PLANO, oscuro, cerca))
            drawLine(n(TRAZO, oscuro, cerca).copy(alpha = 0.7f), Offset(x + incl, y0), Offset(x + ancho + incl, y0), 0.8f * u)
            drawLine(n(TRAZO, oscuro, cerca).copy(alpha = 0.35f), Offset(x + incl + ancho * 0.2f, y0 + 3f * u), Offset(x + incl + ancho * 0.75f, y0 + 3f * u), 0.6f * u)
            drawLine(sombra(oscuro, 0.35f), Offset(x + ancho + incl, y0), Offset(x + ancho, arriba + 4f * u), 0.8f * u)
        }
    }

    /** El frente pintado de una gaveta: acero gris azulado, canto de luz arriba a la izquierda, sombra abajo y a la derecha. */
    private fun DrawScope.frenteGaveta(f: Rect, semilla: Int, oscuro: Boolean, cerca: Float, dia: Float, base: Color = PINTURA) {
        val u = density
        drawRect(n(base, oscuro, cerca), f.topLeft, f.size)
        if (dia > 0f) drawRect(LUZ_FRIA.copy(alpha = dia), f.topLeft, f.size)
        pinceladas(f, if (oscuro) NOCHE else SOMBRA_FRIA, semilla, 5, u, 0.09f)
        // La chapa no es lisa: de día se apaga un poco hacia la derecha; de noche coge el filo de las velas arriba.
        if (oscuro) drawRect(LLAMA.copy(alpha = 0.16f * cerca), f.topLeft, Size(f.width, 2.6f * u))
        else for (q in 0 until 3) drawRect(sombraMueble(0.03f), Offset(f.left + f.width * (0.7f + 0.1f * q), f.top), Size(f.width * (0.3f - 0.1f * q), f.height))
        drawRect(destello(oscuro, 0.45f, cerca + 0.2f), f.topLeft, Size(f.width, 1.4f * u))
        drawRect(destello(oscuro, 0.25f, cerca), f.topLeft, Size(1.3f * u, f.height))
        drawRect(sombra(oscuro, 0.5f), Offset(f.left, f.bottom - 2f * u), Size(f.width, 2f * u))
        drawRect(sombra(oscuro, 0.42f), Offset(f.right - 2f * u, f.top), Size(2f * u, f.height))
        // La pintura gastada en las aristas, donde asoma el acero.
        for (q in 0 until 6) {
            val x = f.left + f.width * azar(semilla, 400 + q)
            val arribaQ = q % 2 == 0
            drawOval(n(ACERO_CLARO, oscuro, cerca).copy(alpha = 0.5f), Offset(x, if (arribaQ) f.top + 0.6f * u else f.bottom - 3f * u), Size(f.width * (0.02f + 0.04f * azar(semilla, 410 + q)), 2f * u))
        }
    }

    /** Un pestillo de palanca: la base, la palanca con su eje y el gancho. */
    private fun DrawScope.pestillo(c: Offset, h: Float, oscuro: Boolean, cerca: Float, espejo: Boolean) {
        val u = density
        val s = if (espejo) -1f else 1f
        val base = Rect(c.x - h * 0.13f, c.y - h * 0.2f, c.x + h * 0.13f, c.y + h * 0.2f)
        drawRect(sombra(oscuro, 0.45f), base.topLeft + Offset(1.2f * u, 1.4f * u), base.size)
        drawRect(n(ACERO_HONDO, oscuro, cerca), base.topLeft, base.size)
        drawRect(destello(oscuro, 0.4f, cerca), base.topLeft, Size(base.width, 0.9f * u))
        val palanca = poligono(c.x - h * 0.06f, c.y - h * 0.16f, c.x + h * 0.06f, c.y - h * 0.16f, c.x + s * h * 0.03f + h * 0.05f, c.y + h * 0.26f, c.x + s * h * 0.03f - h * 0.05f, c.y + h * 0.26f)
        translate(1.2f * u, 1.6f * u) { drawPath(palanca, sombra(oscuro, 0.5f)) }
        metal(palanca, Rect(c.x - h * 0.08f, c.y - h * 0.16f, c.x + h * 0.08f, c.y + h * 0.26f), ACERO_MEDIO, oscuro, cerca)
        drawLine(sombra(oscuro, 0.5f), Offset(c.x - h * 0.05f, c.y + h * 0.2f), Offset(c.x + h * 0.06f, c.y + h * 0.2f), 0.8f * u)
        drawCircle(n(LATON_VIVO, oscuro, cerca), h * 0.045f, Offset(c.x, c.y - h * 0.1f))
        drawRect(n(ACERO_HONDO, oscuro, cerca), Offset(c.x - h * 0.08f, c.y - h * 0.32f), Size(h * 0.16f, h * 0.08f))
    }

    /** La manija industrial: dos tacos atornillados y la barra redonda, con su sombra en la chapa. */
    private fun DrawScope.manija(cx: Float, y: Float, ancho: Float, h: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        listOf(cx - ancho / 2, cx + ancho / 2 - h * 0.1f).forEach { x ->
            drawRect(sombra(oscuro, 0.45f), Offset(x + 1.4f * u, y - h * 0.08f + 1.6f * u), Size(h * 0.1f, h * 0.13f))
            drawRect(n(ACERO_HONDO, oscuro, cerca), Offset(x, y - h * 0.08f), Size(h * 0.1f, h * 0.13f))
            drawCircle(n(ACERO_CLARO, oscuro, cerca), h * 0.022f, Offset(x + h * 0.05f, y - h * 0.04f))
        }
        drawRoundRect(sombra(oscuro, 0.45f), Offset(cx - ancho / 2 + 2f * u, y + h * 0.04f + 2.4f * u), Size(ancho, h * 0.07f), CornerRadius(h * 0.035f))
        drawRoundRect(n(ACERO_MEDIO, oscuro, cerca), Offset(cx - ancho / 2, y + h * 0.04f), Size(ancho, h * 0.07f), CornerRadius(h * 0.035f))
        drawLine(destello(oscuro, 0.7f, cerca), Offset(cx - ancho / 2 + h * 0.04f, y + h * 0.055f), Offset(cx + ancho / 2 - h * 0.04f, y + h * 0.055f), 0.9f * u)
        drawLine(sombra(oscuro, 0.35f), Offset(cx - ancho / 2 + h * 0.04f, y + h * 0.1f), Offset(cx + ancho / 2 - h * 0.04f, y + h * 0.1f), 0.8f * u)
    }

    /** El volante de cierre: aro carmesí moleteado, cuatro radios y el cubo, con su sombra en la chapa. */
    private fun DrawScope.volante(c: Offset, r: Float, giro: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val grana = n(GRANA, oscuro, cerca)
        translate(1.6f * u, 2f * u) {
            drawCircle(sombra(oscuro, 0.45f), r, c, style = Stroke(r * 0.2f))
            for (q in 0 until 4) {
                val a = Math.toRadians((giro + q * 90).toDouble())
                drawLine(sombra(oscuro, 0.45f), c, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), r * 0.13f)
            }
        }
        for (q in 0 until 4) {
            val a = Math.toRadians((giro + q * 90).toDouble())
            drawLine(grana, c, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), r * 0.13f)
        }
        drawCircle(grana, r, c, style = Stroke(r * 0.2f))
        for (q in 0 until 16) {
            val a = Math.toRadians(q * 22.5)
            drawCircle(n(GRANA_HONDA, oscuro, cerca), r * 0.045f, Offset(c.x + r * 1.08f * cos(a).toFloat(), c.y + r * 1.08f * sin(a).toFloat()))
        }
        drawArc(if (oscuro) LLAMA.copy(alpha = 0.5f * cerca + 0.1f) else GRANA_LUZ, 190f, 80f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.07f))
        drawArc(n(GRANA_HONDA, oscuro, cerca), 10f, 100f, false, Offset(c.x - r * 1.05f, c.y - r * 1.05f), Size(2.1f * r, 2.1f * r), style = Stroke(r * 0.06f))
        drawCircle(n(ACERO_MEDIO, oscuro, cerca), r * 0.22f, c)
        drawCircle(destello(oscuro, 0.7f, cerca), r * 0.07f, c - Offset(r * 0.07f, r * 0.07f))
    }

    /** La mirilla: aro de latón con sus tornillos y un vidrio grueso tras el que se ven los planos. */
    private fun DrawScope.mirilla(c: Offset, r: Float, n0: Int, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        drawCircle(sombra(oscuro, 0.45f), r * 1.25f, c + Offset(1.6f * u, 2f * u))
        drawCircle(n(LATON_VIVO, oscuro, cerca), r * 1.25f, c)
        drawArc(destello(oscuro, 0.6f, cerca), 190f, 90f, false, Offset(c.x - r * 1.2f, c.y - r * 1.2f), Size(r * 2.4f, r * 2.4f), style = Stroke(1f * u))
        val vidrio = Path().apply { addOval(Rect(c, r)) }
        drawPath(vidrio, n(Color(0xFF151A22), oscuro, 0f))
        clipPath(vidrio) {
            val m = n0.coerceIn(1, 5)
            for (i in 0 until m) {
                val x = c.x - r + r * 2f * (i + 0.3f) / m
                val y = c.y - r * (0.1f + 0.6f * azar(semilla, 120 + i))
                drawRect(n(if (i % 2 == 0) AZUL_PLANO else mezcla(AZUL_PLANO, TRAZO, 0.2f), oscuro, cerca * 0.7f), Offset(x, y), Size(r * 0.42f, r * 2f))
                drawLine(n(TRAZO, oscuro, cerca).copy(alpha = 0.5f), Offset(x, y + 1.2f * u), Offset(x + r * 0.42f, y + 1.2f * u), 0.6f * u)
            }
            drawRect(sombra(oscuro, 0.45f), Offset(c.x - r, c.y - r), Size(2 * r, r * 0.7f))
            drawArc(brillo(oscuro, 0.5f, cerca + 0.3f).let { if (oscuro) it else LUZ_FRIA.copy(alpha = 0.55f) }, 200f, 60f, false, Offset(c.x - r * 0.75f, c.y - r * 0.75f), Size(r * 1.5f, r * 1.5f), style = Stroke(r * 0.12f, cap = StrokeCap.Round))
        }
        drawCircle(n(LATON_HONDO, oscuro, cerca), r, c, style = Stroke(1f * u))
        for (q in 0 until 6) {
            val a = Math.toRadians(30.0 + q * 60)
            drawCircle(n(LATON_HONDO, oscuro, cerca), r * 0.08f, Offset(c.x + r * 1.12f * cos(a).toFloat(), c.y + r * 1.12f * sin(a).toFloat()))
        }
    }

    /** La tapa de un tubo portaplanos de latón vista de frente: su rosca, su boca y el brillo. Un poco más asomado o menos. */
    private fun DrawScope.tapaTubo(c: Offset, r: Float, asoma: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        // El costado del tubo que asoma (la luz viene de arriba a la izquierda: se ve abajo a la derecha).
        if (asoma > 0f) {
            drawCircle(n(LATON_HONDO, oscuro, cerca), r, c + Offset(asoma * 0.6f, asoma * 0.6f))
            drawLine(n(LATON_HONDO, oscuro, cerca), c, c + Offset(asoma * 0.6f, asoma * 0.6f), r * 2f)
        }
        drawCircle(sombra(oscuro, 0.5f), r * 1.04f, c + Offset(1.2f * u, 1.6f * u))
        drawCircle(n(LATON_VIVO, oscuro, cerca), r, c)
        drawCircle(n(LATON_HONDO, oscuro, cerca), r * 0.82f, c, style = Stroke(0.9f * u))
        drawCircle(n(LATON_HONDO, oscuro, cerca), r * 0.62f, c, style = Stroke(0.6f * u))
        drawCircle(n(mezcla(LATON_VIVO, LATON_BRILLO, 0.3f), oscuro, cerca), r * 0.45f, c)
        drawArc(destello(oscuro, 0.8f, cerca + 0.2f), 195f, 70f, false, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(1.1f * u))
        drawCircle(destello(oscuro, 0.5f, cerca), r * 0.12f, c - Offset(r * 0.18f, r * 0.18f))
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val k = if (c.vacio) 0 else c.n
        // Dónde está esta gaveta en el mueble (en fracciones del ancho), para saber cuánto le llega de las velas.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val cerca = if (oscuro) luzBaja(centro) else 0f
        if (c.filas > 1 || c.cols > 1) cubreAura(c, oscuro)
        // De día, la luz de arriba a la izquierda aclara apenas las filas de arriba y la columna de la izquierda.
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.03f else 0f)
        val clase = if (c.sinTema) 0 else Math.floorMod(c.semilla / 3, 3)
        val tubos = !c.vacio && clase == 2
        // Las más llenas quedan entreabiertas; el casillero de tubos no tiene frente que abrir.
        val entre = if (c.vacio || k == 0 || tubos) 0f else h * (0.02f + 0.013f * k.coerceIn(0, 7)) + (if (c.sinTema) h * 0.015f else 0f)
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f && !tubos) planosAsomados(w, arriba, k.coerceAtLeast(1), c.semilla, oscuro, cerca)
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) drawRect(sombra(oscuro, 0.35f * ab), Offset(f.left + 2f * u, f.bottom - 1f * u), Size(f.width, 1f * u + 5f * u * ab))
        val fh = f.height
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)

        if (c.vacio) {
            if (Math.floorMod(c.semilla / 5, 2) == 0) {
                // Una gaveta soldada: el cordón por las cuatro juntas y el tinte del calor alrededor.
                frenteGaveta(f, c.semilla, oscuro, cerca, dia)
                val i0 = 4f * u
                val cordon = listOf(Offset(i0, i0) to Offset(w - i0, i0), Offset(i0, h - i0) to Offset(w - i0, h - i0), Offset(i0, i0) to Offset(i0, h - i0), Offset(w - i0, i0) to Offset(w - i0, h - i0))
                // El tinte del calor: una franja de paja junto al cordón y otra de azul más allá, de borde irregular.
                cordon.forEachIndexed { j, (a, b) ->
                    val horiz = j < 2
                    val hacia = Offset(if (j == 2) 1f else if (j == 3) -1f else 0f, if (j == 0) 1f else if (j == 1) -1f else 0f)
                    val m = ((b - a).getDistance() / (6f * u)).toInt().coerceAtLeast(2)
                    for (q in 0..m) {
                        val p = a + (b - a) * (q / m.toFloat())
                        val hondo = (2.5f + 3.5f * azar(c.semilla, 600 + j * 50 + q)) * u
                        listOf(Triple(Color(0xFF4A6FA8), 0.1f, hondo * 2.2f), Triple(Color(0xFFC8A060), 0.16f, hondo)).forEach { (col, al, d) ->
                            val largo = 9f * u
                            val centro = p + hacia * (d * 0.5f)
                            val sz = if (horiz) Size(largo, d) else Size(d, largo)
                            drawOval(n(col, oscuro, cerca).copy(alpha = al), Offset(centro.x - sz.width / 2, centro.y - sz.height / 2), sz)
                        }
                    }
                }
                // El cordón: gotas encimadas, con su brillo y su sombra.
                cordon.forEach { (a, b) ->
                    val m = ((b - a).getDistance() / (2.4f * u)).toInt()
                    for (q in 0..m) {
                        val p = a + (b - a) * (q / m.toFloat())
                        drawCircle(sombra(oscuro, 0.4f), 2.6f * u, p + Offset(0.8f * u, 1f * u))
                        drawCircle(n(mezcla(ACERO_MEDIO, BETUN, 0.25f), oscuro, cerca), 2.6f * u, p)
                        drawArc(destello(oscuro, 0.55f, cerca), 200f, 90f, false, Offset(p.x - 2.2f * u, p.y - 2.2f * u), Size(4.4f * u, 4.4f * u), style = Stroke(0.7f * u))
                    }
                }
                // Los agujeros de la placa que nunca tuvo, y una marca de tiza.
                listOf(0.3f, 0.7f).forEach { x ->
                    drawCircle(n(BETUN, oscuro, 0f), 1.6f * u, Offset(w * x, h * 0.3f))
                    drawArc(destello(oscuro, 0.4f, cerca), 0f, 120f, false, Offset(w * x - 1.8f * u, h * 0.3f - 1.8f * u), Size(3.6f * u, 3.6f * u), style = Stroke(0.6f * u))
                }
                drawLine(n(CERA, oscuro, cerca).copy(alpha = 0.45f), Offset(w * 0.44f, h * 0.48f), Offset(w * 0.56f, h * 0.68f), 1.4f * u, StrokeCap.Round)
                drawLine(n(CERA, oscuro, cerca).copy(alpha = 0.45f), Offset(w * 0.56f, h * 0.48f), Offset(w * 0.44f, h * 0.68f), 1.4f * u, StrokeCap.Round)
                drawLine(sombra(oscuro, 0.25f), Offset(w * 0.3f, h * 0.55f), Offset(w * 0.55f, h * 0.6f), 0.7f * u)
            } else {
                // El hueco cerrado con una tapa atornillada.
                frenteGaveta(f, c.semilla, oscuro, cerca * 0.8f, dia, mezcla(ACERO_HONDO, ACERO_MEDIO, 0.35f))
                val t = Rect(w * 0.08f, h * 0.14f, w * 0.92f, h * 0.86f)
                drawRect(sombra(oscuro, 0.5f), t.topLeft + Offset(2f * u, 2.4f * u), t.size)
                frenteGaveta(t, c.semilla + 3, oscuro, cerca, dia, mezcla(PINTURA, ACERO_MEDIO, 0.4f))
                listOf(t.topLeft + Offset(6f * u, 6f * u), Offset(t.right - 6f * u, t.top + 6f * u), Offset(t.left + 6f * u, t.bottom - 6f * u), t.bottomRight - Offset(6f * u, 6f * u)).forEach { p ->
                    val hx = Path().apply { for (q in 0 until 6) { val a = Math.toRadians(30.0 + q * 60); val pp = Offset(p.x + 4.6f * u * cos(a).toFloat(), p.y + 4.6f * u * sin(a).toFloat()); if (q == 0) moveTo(pp.x, pp.y) else lineTo(pp.x, pp.y) }; close() }
                    translate(1f * u, 1.2f * u) { drawPath(hx, sombra(oscuro, 0.5f)) }
                    drawPath(hx, n(ACERO_MEDIO, oscuro, cerca))
                    drawLine(destello(oscuro, 0.6f, cerca), p + Offset(-2.6f * u, -1.4f * u), p + Offset(0f, -3f * u), 0.7f * u)
                }
                drawLine(n(OXIDO, oscuro, cerca).copy(alpha = 0.35f), Offset(t.right - 6f * u, t.bottom - 3f * u), Offset(t.right - 5.6f * u, t.bottom + 6f * u), 1.4f * u, StrokeCap.Round)
            }
            return
        }

        if (tubos) {
            // El casillero: un hueco hondo con los tubos portaplanos y el travesaño que lleva la placa.
            val hondo = Rect(0f, arriba, w, h + baja)
            drawRect(n(mezcla(BETUN, ACERO_HONDO, 0.45f), oscuro, cerca * 0.5f), hondo.topLeft, hondo.size)
            drawRect(sombra(oscuro, 0.5f), hondo.topLeft, Size(w, h * 0.12f))
            drawRect(sombra(oscuro, 0.35f), hondo.topLeft, Size(w * 0.04f, hondo.height))
            // La rejilla de los tubos: una chapa con seis bocas; en las ocupadas, la tapa roscada de un tubo de latón.
            val rejilla = Rect(0f, r.bottom + h * 0.04f, w, h + baja)
            frenteGaveta(rejilla, c.semilla + 5, oscuro, cerca, dia, mezcla(ACERO_HONDO, ACERO_MEDIO, 0.55f))
            val m = k.coerceIn(1, 6)
            val orden = (0 until 6).sortedBy { azar(c.semilla, 250 + it) }.take(m).toSet()
            val rr = h * 0.105f
            val yb = (rejilla.top + rejilla.bottom) / 2 + h * 0.01f
            for (q in 0 until 6) {
                val p = Offset(w * (0.12f + 0.76f * q / 5f), yb)
                if (q in orden) {
                    // El tubo se ve asomar por arriba del travesaño, y su tapa en la boca.
                    drawRect(n(LATON_HONDO, oscuro, cerca * 0.7f), Offset(p.x - rr * 0.9f, arriba + h * 0.02f), Size(rr * 1.8f, h * 0.06f))
                    drawRect(destello(oscuro, 0.4f, cerca), Offset(p.x - rr * 0.6f, arriba + h * 0.02f), Size(rr * 0.25f, h * 0.06f))
                    drawCircle(n(BETUN, oscuro, 0f), rr * 1.12f, p)
                    tapaTubo(p, rr, rr * (0.2f + 0.6f * azar(c.semilla, 200 + q)), oscuro, cerca)
                } else {
                    drawCircle(n(mezcla(BETUN, ACERO_HONDO, 0.6f), oscuro, 0f), rr * 0.85f, p)
                    drawCircle(n(ACERO_HONDO, oscuro, cerca * 0.5f), rr * 0.5f, p + Offset(rr * 0.2f, rr * 0.25f))
                    drawArc(destello(oscuro, 0.45f, cerca), 0f, 120f, false, Offset(p.x - rr * 0.9f, p.y - rr * 0.9f), Size(rr * 1.8f, rr * 1.8f), style = Stroke(1.2f * u))
                    drawArc(sombra(oscuro, 0.5f), 190f, 120f, false, Offset(p.x - rr * 0.8f, p.y - rr * 0.8f), Size(rr * 1.6f, rr * 1.6f), style = Stroke(1.6f * u))
                }
            }
            // El marco del casillero y el travesaño.
            drawRect(n(mezcla(ACERO_HONDO, ACERO_MEDIO, 0.4f), oscuro, cerca), Offset(0f, arriba), Size(w, h * 0.02f))
            drawRect(destello(oscuro, 0.4f, cerca + 0.2f), Offset(0f, arriba), Size(w, 1.2f * u))
            val trav = Rect(0f, r.top - h * 0.06f, w, r.bottom + h * 0.05f)
            drawRect(sombra(oscuro, 0.45f), Offset(trav.left, trav.bottom), Size(trav.width, 2.4f * u))
            frenteGaveta(trav, c.semilla, oscuro, cerca, dia, mezcla(ACERO_HONDO, ACERO_MEDIO, 0.45f))
            listOf(w * 0.03f, w * 0.97f).forEach { x -> roblon(Offset(x, trav.center.y), h * 0.035f, oscuro, cerca) }
            drawRect(destello(oscuro, 0.3f, cerca), Offset(0f, 0f), Size(1.2f * u, h + baja))
            drawRect(sombra(oscuro, 0.45f), Offset(w - 2f * u, 0f), Size(2f * u, h + baja))
            placaLaton(r, oscuro, cerca, blanca = false)
            return
        }

        frenteGaveta(f, c.semilla, oscuro, cerca, dia)
        if (arriba < 0.5f) drawRect(sombra(oscuro, 0.4f), Offset.Zero, Size(w, 2.4f * u))
        // Los remaches de las esquinas.
        listOf(Offset(f.left + w * 0.03f, f.top + fh * 0.1f), Offset(f.right - w * 0.03f, f.top + fh * 0.1f), Offset(f.left + w * 0.03f, f.bottom - fh * 0.1f), Offset(f.right - w * 0.03f, f.bottom - fh * 0.1f))
            .forEach { roblon(it, h * 0.03f, oscuro, cerca) }
        // El uso: pintura quemada por una soldadura, o rayones.
        if (!c.sinTema && Math.floorMod(c.semilla / 7, 4) == 1) {
            val q = Offset(w * 0.72f, f.top + fh * 0.82f)
            drawOval(n(OXIDO, oscuro, cerca).copy(alpha = 0.4f), Offset(q.x - w * 0.09f, q.y - fh * 0.2f), Size(w * 0.18f, fh * 0.4f))
            drawOval(n(BETUN, oscuro, cerca).copy(alpha = 0.55f), Offset(q.x - w * 0.06f, q.y - fh * 0.14f), Size(w * 0.12f, fh * 0.28f))
            drawOval(n(Color(0xFF6F86A8), oscuro, cerca).copy(alpha = 0.7f), Offset(q.x - w * 0.03f, q.y - fh * 0.07f), Size(w * 0.06f, fh * 0.14f))
            drawOval(n(Color(0xFFC8A060), oscuro, cerca).copy(alpha = 0.6f), Offset(q.x - w * 0.016f, q.y - fh * 0.04f), Size(w * 0.032f, fh * 0.08f))
            for (j in 0 until 6) {
                val p = Offset(q.x + (azar(c.semilla, 330 + j) - 0.5f) * w * 0.16f, q.y + (azar(c.semilla, 340 + j) - 0.5f) * fh * 0.34f)
                drawCircle(n(PINTURA, oscuro, cerca), 1.4f * u, p)
                drawCircle(destello(oscuro, 0.3f, cerca), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
            }
        } else if (Math.floorMod(c.semilla / 11, 3) == 0) {
            for (j in 0 until 6) {
                val x = w * (0.15f + 0.6f * azar(c.semilla, 300 + j)); val y = f.top + fh * (0.6f + 0.3f * azar(c.semilla, 310 + j))
                drawLine(destello(oscuro, 0.35f, cerca), Offset(x, y), Offset(x + w * (0.03f + 0.05f * azar(c.semilla, 320 + j)), y - fh * 0.04f), 0.6f * u)
            }
        }

        val my = h * 0.74f + baja
        if (clase == 1) {
            // Mirilla a la izquierda, volante a la derecha y el pasador que va del volante al centro.
            mirilla(Offset(w * 0.2f, my), h * 0.14f, k, c.semilla, oscuro, cerca)
            val vc = Offset(w * 0.79f, my)
            drawRect(sombra(oscuro, 0.4f), Offset(w * 0.42f, my - h * 0.02f + 2f * u), Size(vc.x - w * 0.42f, h * 0.05f))
            drawRect(n(ACERO_MEDIO, oscuro, cerca), Offset(w * 0.42f, my - h * 0.025f), Size(vc.x - w * 0.42f, h * 0.05f))
            drawLine(destello(oscuro, 0.6f, cerca), Offset(w * 0.42f, my - h * 0.02f), Offset(vc.x, my - h * 0.02f), 0.7f * u)
            drawRect(n(ACERO_HONDO, oscuro, cerca), Offset(w * 0.4f, my - h * 0.07f), Size(w * 0.04f, h * 0.14f))
            roblon(Offset(w * 0.42f, my - h * 0.04f), h * 0.022f, oscuro, cerca)
            roblon(Offset(w * 0.42f, my + h * 0.04f), h * 0.022f, oscuro, cerca)
            volante(vc, h * 0.15f, 12f + 30f * azar(c.semilla, 9), oscuro, cerca)
        } else {
            pestillo(Offset(w * 0.1f, my), h, oscuro, cerca, false)
            pestillo(Offset(w * 0.9f, my), h, oscuro, cerca, true)
            manija(w / 2, my - h * 0.03f, w * 0.34f, h, oscuro, cerca)
        }

        if (c.sinTema) {
            // La placa sin grabar asoma detrás de la etiqueta de cartón, atada con alambre a la manija.
            placaLaton(Rect(r.left + w * 0.05f, r.top - h * 0.05f, r.right + w * 0.03f, r.bottom - h * 0.06f), oscuro, cerca, blanca = true)
            val tag = Path().apply {
                moveTo(r.left + 3f * u, r.top); lineTo(r.right - 1f * u, r.top + 1f * u); lineTo(r.right, r.bottom - 2f * u)
                lineTo(r.right - 4f * u, r.bottom); lineTo(r.left + 1f * u, r.bottom - 1f * u); lineTo(r.left, r.top + r.height * 0.62f)
                lineTo(r.left - 2f * u, r.top + r.height * 0.5f); lineTo(r.left, r.top + r.height * 0.36f); close()
            }
            translate(1.6f * u, 2.4f * u) { drawPath(tag, sombra(oscuro, 0.45f)) }
            drawPath(tag, if (oscuro) mezcla(CARTON_NOCHE, CARTON, cerca * 1.3f) else CARTON)
            clipPath(tag) {
                drawRect(brillo(oscuro, 0.3f, cerca), Offset(r.left, r.top), Size(r.width, 1.4f * u))
                drawOval(n(OXIDO, oscuro, cerca).copy(alpha = 0.16f), Offset(r.right - r.width * 0.3f, r.bottom - r.height * 0.4f), Size(r.width * 0.25f, r.height * 0.35f))
                drawLine(sombra(oscuro, 0.12f), Offset(r.left + r.width * 0.55f, r.top), Offset(r.left + r.width * 0.53f, r.bottom), 0.8f * u)
            }
            // El ojal reforzado y el alambre retorcido que baja a la manija.
            val ojal = Offset(r.left + 5f * u, r.top + r.height * 0.5f)
            drawCircle(n(mezcla(CARTON, CUERO, 0.3f), oscuro, cerca), 3.2f * u, ojal)
            drawCircle(n(BETUN, oscuro, 0f), 1.4f * u, ojal)
            val alambre = n(ACERO_CLARO, oscuro, cerca)
            val p1 = Offset(w * 0.34f, my + h * 0.02f)
            drawPath(Path().apply { moveTo(ojal.x, ojal.y); cubicTo(ojal.x - w * 0.03f, ojal.y + h * 0.08f, p1.x - w * 0.1f, p1.y, p1.x, p1.y) }, alambre, style = Stroke(1f * u))
            drawPath(Path().apply { moveTo(ojal.x + 1f * u, ojal.y); cubicTo(ojal.x - w * 0.025f, ojal.y + h * 0.09f, p1.x - w * 0.09f, p1.y + 2f * u, p1.x, p1.y + 1f * u) }, alambre, style = Stroke(0.8f * u))
            for (j in 0 until 4) drawLine(alambre, Offset(p1.x - w * 0.012f * j, p1.y - 1.4f * u), Offset(p1.x - w * 0.012f * j - 2f * u, p1.y + 2f * u), 0.8f * u)
        } else {
            placaLaton(r, oscuro, cerca, blanca = false)
        }
        // Al abrirla, sube un poco de luz de dentro.
        if (ab > 0f) mancha(Offset(w / 2, arriba), w * 0.4f, if (oscuro) LLAMA.copy(alpha = 0.18f * ab) else LUZ_FRIA.copy(alpha = 0.3f * ab))
    }

    override val rotulo = Rect(0.07f, 0.13f, 0.93f, 0.51f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.estarcido
    override val salida = SALIDA
    override val placa = Rect(0.3f, 0.06f, 0.7f, 0.36f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un plano azul: cuadrícula fina y gruesa, y unas piezas tenues.
        drawRect(t(PLANO, oscuro))
        val p = 16 * u
        var x = 0f; var k = 0
        while (x < w) { drawLine(TEXTO.copy(alpha = if (k % 5 == 0) 0.14f else 0.06f), Offset(x, 0f), Offset(x, h), 1f * u); x += p; k++ }
        var y = 0f; k = 0
        while (y < h) { drawLine(TEXTO.copy(alpha = if (k % 5 == 0) 0.14f else 0.06f), Offset(0f, y), Offset(w, y), 1f * u); y += p; k++ }
        drawPath(engrane(Offset(w * 0.72f, h * 0.3f), w * 0.18f, 16), TEXTO.copy(alpha = 0.1f), style = Stroke(1.4f * u))
        drawCircle(TEXTO.copy(alpha = 0.1f), w * 0.07f, Offset(w * 0.72f, h * 0.3f), style = Stroke(1.4f * u))
        drawLine(TEXTO.copy(alpha = 0.12f), Offset(w * 0.5f, h * 0.3f), Offset(w * 0.94f, h * 0.3f), 1f * u)
        // Las paredes de acero remachadas.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(ACERO, oscuro), r.topLeft, r.size) }
        var rx = pl
        while (rx < w - pl) { remache(Offset(rx, pl / 2), 1.8f * u); remache(Offset(rx, h - pl / 2), 1.8f * u); rx += 24 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // La cuadrícula fina, el filete carmesí, el cajetín del plano y las marcas de registro en las esquinas.
        val q = 8 * u * g
        var x = 0f
        while (x < w) { drawLine(TEXTO.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, h), 0.6f * u); x += q }
        var y = 0f
        while (y < h) { drawLine(TEXTO.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 0.6f * u); y += q }
        drawRect(CARMESI, Offset.Zero, Size(w, 2.5f * u * g))
        val caj = Rect(w * 0.58f, h - 16 * u * g, w - 4 * u * g, h - 4 * u * g)
        drawRect(TEXTO.copy(alpha = 0.18f), caj.topLeft, caj.size, style = Stroke(0.7f * u))
        drawLine(TEXTO.copy(alpha = 0.18f), Offset(caj.left, caj.center.y), Offset(caj.right, caj.center.y), 0.6f * u)
        drawLine(TEXTO.copy(alpha = 0.18f), Offset(caj.left + caj.width * 0.35f, caj.top), Offset(caj.left + caj.width * 0.35f, caj.bottom), 0.6f * u)
        val m = 5 * u * g; val rm = 2.5f * u * g
        listOf(Offset(m, m + 3 * u), Offset(w - m, m + 3 * u), Offset(m, h - m)).forEach { o ->
            drawCircle(TEXTO.copy(alpha = 0.35f), rm, o, style = Stroke(0.6f * u))
            drawLine(TEXTO.copy(alpha = 0.35f), Offset(o.x - rm * 1.6f, o.y), Offset(o.x + rm * 1.6f, o.y), 0.6f * u)
            drawLine(TEXTO.copy(alpha = 0.35f), Offset(o.x, o.y - rm * 1.6f), Offset(o.x, o.y + rm * 1.6f), 0.6f * u)
        }
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 30.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(LATON, p.color, cuadrado = true)
            "convence" -> { drawPath(engrane(c, s * 0.46f, 8), LATON); drawCircle(ACERO, s * 0.16f, c) }
            "piensa" -> {
                drawLine(TEXTO, Offset(c.x, c.y - s * 0.36f), Offset(c.x - s * 0.26f, c.y + s * 0.42f), s * 0.08f, StrokeCap.Round)
                drawLine(TEXTO, Offset(c.x, c.y - s * 0.36f), Offset(c.x + s * 0.26f, c.y + s * 0.42f), s * 0.08f, StrokeCap.Round)
                drawCircle(TEXTO, s * 0.1f, Offset(c.x, c.y - s * 0.36f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(CARMESI)
            else -> {}
        }
    }

    /** Un remache grande. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension
        remache(center, s * 0.4f)
    }

    override val desorden = 0f
}
