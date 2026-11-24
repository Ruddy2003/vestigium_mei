package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * Dónde va cada cosa del rincón de la libreta de la Ciudad verde, en píxeles, para un lienzo de [w] por [h] y un cuaderno de
 * [hojas] hojas prensadas. Todo se mide en [U] (un dp del alto de diseño), así el rincón no se deforma si cambia el ancho: la
 * mesa y el cuaderno van al centro y lo de los lados se corta por el borde.
 */
internal class SitioLibretaRuinas(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaRuinas.ALTO.value * u)).coerceIn(0.5f, 2f)
    val cx = w * 0.5f
    /** La cara de arriba de la mesa (donde pisan las cosas), su canto, el faldón con el cajón y donde empiezan las patas. */
    val mesa = 200f * U
    val canto = 207f * U
    val faldon = 215f * U
    val patas = 240f * U
    /** El suelo, detrás de las patas. */
    val suelo = 252f * U
    /** El grueso del cuaderno (engorda con las hojas: de 9 a 34 dp) y su ancho y alto. */
    val g = (9f + 25f * sqrt(hojas.coerceIn(0, 240) / 240f)) * U
    val bw = minOf(146f * U, w * 0.42f)
    val bh = 170f * U
    /** El grueso en escorzo: el canto de las hojas se ve a la derecha de la tapa y arriba (el cuaderno está apoyado en la pared). */
    val v = Offset(0.55f * g, -0.34f * g)
    /** El ancho que se ve del lomo, a la izquierda de la tapa. */
    val lomo = 5f * U + 0.2f * g
    /** La tapa del cuaderno, de pie sobre la mesa y apoyada en la pared. */
    val tapa = Rect(cx - bw / 2 + 4f * U, mesa - bh, cx + bw / 2 + 4f * U, mesa)
    /** El candil de botella, a la izquierda en la mesa: su pie y dónde arde la mecha. */
    val candil = Offset(tapa.left - lomo - 50f * U, mesa)
    val llama = Offset(candil.x, mesa - 82f * U)
    /** El cuenco de las pipas, a la derecha. */
    val cuenco = Offset(tapa.right + v.x + 50f * U, mesa)
    /** La tubería de hierro que cruza la pared, y el clavo del sombrero de paja. */
    val tuberia = 34f * U
    val clavo = Offset(tapa.right + v.x + 72f * U, 70f * U)

    private fun dist(a: Offset, b: Offset): Float { val dx = (a.x - b.x) / U; val dy = (a.y - b.y) / U; return sqrt(dx * dx * 0.9f + dy * dy) }

    /** Cuánto alumbra la mecha del candil un punto [p] (de noche): de golpe cerca, poco lejos. */
    fun kCandil(p: Offset): Float = (1.75f * exp(-dist(p, llama) / 70f)).coerceIn(0f, 1f)

    /** Cuánta luna le llega a [p] (de noche, de 0 a 1): un lado frío, arriba a la derecha, que se apaga hacia la mesa y el candil. */
    fun lunaEn(p: Offset): Float {
        val x = ((p.x - cx) / (w * 0.5f)).coerceIn(-1f, 1f)
        val y = (1f - p.y / mesa).coerceIn(0f, 1f)
        return ((0.5f + 0.5f * x) * (0.35f + 0.65f * y)).coerceIn(0f, 1f)
    }

    /**
     * El filo frío de la luna en un canto que mira a la derecha, en [p] (de noche; de día, nada). La luna no pinta paños ni tiñe
     * los colores: solo deja este filo en los cantos (el cuaderno, el sombrero, el cuenco) y lo lejos del candil se hunde.
     */
    fun frio(a: AireRuinas, p: Offset, alfa: Float): Color =
        if (!a.oscuro) Color.Transparent else PaletaRuinas.LUNA.copy(alpha = (alfa * lunaEn(p)).coerceIn(0f, 0.85f))

    /** La luz de un punto: la del candil de noche (lo demás, hundido en el verde botella); de día, la del cuarto (con [sol], la del lucernario). */
    fun luz(a: AireRuinas, p: Offset, sol: Float = 0f): LuzRuinas {
        val amb = (1f - 0.0011f * p.y / U - 0.0005f * (p.x - cx) / U).coerceIn(0.55f, 1f)
        return if (a.oscuro) a.en(kCandil(p), PaletaRuinas.LLAMA, 0f, 0f, amb) else a.en(0f, PaletaRuinas.LLAMA, sol, 0f, amb)
    }

    /** El lado de donde viene la luz a [p]: -1 si de la izquierda (el lucernario de día; el candil de noche, para lo que está a su derecha). */
    fun hacia(a: AireRuinas, p: Offset): Float = if (!a.oscuro) -1f else if (p.x > llama.x) -1f else 1f

    /** La mancha del lucernario (de día) en unidades de [U]: dos paños de luz con su barra, que entran altos por la izquierda. */
    val sol: EnrejadoRuinas = EnrejadoRuinas(-40f, -20f, 118f, 18f, 44f, 128f, 2, 1, 0.14f, floatArrayOf(1f, 0.62f))

    /** Cuánto sol del lucernario le da a [p] (en píxeles), corrida [delante] (en U) hacia la luz para lo que está delante. */
    fun solEn(p: Offset, delante: Float = 0f): Float = sol.en(p.x / U + delante * 0.7f, p.y / U + delante * 0.8f)
}

/**
 * La libreta de momentos de la Ciudad verde: el cuaderno del superviviente, el herbario de la semillera, de pie sobre la mesa de
 * la sala de lectura y apoyado en la pared: tapas forradas de lona verde, cantoneras de badana, el lomo cosido a mano, dos
 * vueltas de bramante con su nudo y el lápiz metido debajo, la etiqueta de cartón cosida con el nombre y la cuenta, y el canto de
 * las hojas hinchado de plantas prensadas (helechos, hojas, una espiga) que asoman, más cuantas más hojas. Contra el papel
 * pintado viejo, desgarrado donde asoma el yeso verde agua, con la tubería de hierro que cruza la pared (la hiedra se le
 * enrosca y gotea en la junta) y el sombrero de paja de recolectora colgado de un clavo. En la mesa, a la izquierda, el candil
 * hecho con una botella; a la derecha, el cuenco de barro con las pipas que se están secando, el lápiz y sus virutas.
 *
 * La luz: de día entra alta por la izquierda, del lucernario de la sala (dos paños con su barra, que pintan el papel y la mesa);
 * de noche arde el candil (su charco enciende el papel, la mesa y el lado izquierdo del cuaderno) y la luna, por la derecha, deja
 * solo un filo frío en los cantos que la miran (el cuaderno, el sombrero, el cuenco); lo demás se hunde en el verde botella. Lo fijo (la pared, la tubería, el sombrero, la mesa, el
 * candil y el cuenco) se pinta una vez por tamaño y modo; el cuaderno, que engorda y se abre, en cada dibujo.
 */
internal object MuebleLibretaRuinas {
    val ALTO: Dp = 266.dp

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaRuinas(w, h, density, e.hojas)
        val a = AireRuinas(oscuro, false)
        clipRect(0f, 0f, w, h) {
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaRuinas) { rincon(s, a) }
                    granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            with(CuadernoLibretaRuinas) { cuaderno(e, s, a, medidor) }
        }
    }
}
