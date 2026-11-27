package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
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
 * Dónde va cada cosa del rincón de la libreta de Distopía, en píxeles, para un lienzo de [w] por [h] y un expediente de [hojas]
 * folios. Todo se mide en [U] (un dp del alto de diseño), así el rincón no se deforma si cambia el ancho: la mesa y el expediente
 * van al centro y lo de los lados se corta por el borde.
 */
internal class SitioLibretaDistopia(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaDistopia.ALTO.value * u)).coerceIn(0.5f, 2f)
    val cx = w * 0.5f
    /** La raya oliva entre las dos pinturas de la pared. */
    val raya = 114f * U
    /** El canto de atrás del tablero (donde la pared se mete detrás de la mesa), donde pisan las cosas y el canto de delante. */
    val fondo = 195f * U
    val mesa = 206f * U
    val frente = 214f * U
    /** El pie del canto de chapa del tablero y el de la fila de cajones; debajo, el hueco de las rodillas y los otros cajones. */
    val canto = 220f * U
    val cajones = 242f * U
    /** El grueso del expediente (de 8 a 34 dp, con los folios), su ancho y su alto. */
    val g = (8f + 26f * sqrt(hojas.coerceIn(0, 240) / 240f)) * U
    val bw = minOf(150f * U, w * 0.44f)
    val bh = 168f * U
    /** El grueso en escorzo: el canto de los folios asoma a la derecha y arriba (el expediente se recuesta en el atril). */
    val v = Offset(0.55f * g, -0.34f * g)
    /** La tapa del expediente, de pie en el canal del atril. */
    val tapa = Rect(cx + 16f * U - bw / 2, mesa - 9f * U - bh, cx + 16f * U + bw / 2, mesa - 9f * U)
    /** El ancho que se ve del doblez del lomo, a la izquierda de la tapa. */
    val lomo = 2.5f * U + 0.1f * g
    /** La chapa del atril, detrás del expediente (a la altura de la contratapa), y su canal delante, abajo. */
    val atril = Rect(tapa.left + v.x - 8f * U, tapa.top + v.y - 10f * U, tapa.right + v.x + 8f * U, mesa - 2f * U)
    val canal = Rect(tapa.left - lomo - 7f * U, mesa - 14f * U, tapa.right + 0.5f * v.x + 8f * U, mesa - 5f * U)
    /** La lámpara colgante: dónde cuelga, la boca de la tulipa y la bombilla. */
    val lamparaX = tapa.left - 36f * U
    val boca = 74f * U
    val bombilla = Offset(lamparaX, boca + 2f * U)
    /** El tubo de la luz que cruza la pared, arriba. */
    val conducto = 13f * U
    /** El tragaluz de la acera, arriba a la derecha: el hueco en el muro (con su alféizar) y el cristal. */
    val tragaluz = Rect(tapa.right + 30f * U, 0f, tapa.right + 150f * U, 44f * U)
    /** El sello y el tampón (a la izquierda, bajo la lámpara), el pincho (a la derecha del expediente) y el interruptor. */
    val sello = Offset(lamparaX - 8f * U, mesa + 1f * U)
    val tampon = Offset(lamparaX - 48f * U, mesa + 2f * U)
    val pincho = Offset(tapa.right + 0.55f * 34f * U + 34f * U, mesa + 1f * U)
    val interruptor = Offset(lamparaX - 86f * U, 128f * U)

    private fun dist(a: Offset, b: Offset, fy: Float): Float {
        val dx = (a.x - b.x) / U; val dy = (a.y - b.y) / U * fy
        return sqrt(dx * dx + dy * dy)
    }

    /** Cuánto alumbra la bombilla un punto (de noche): de golpe cerca, poco lejos; la tulipa tapa lo que queda por encima de su boca. */
    fun kLampara(p: Offset): Float {
        val abajo = ((p.y - (boca - 26f * U)) / (26f * U)).coerceIn(0f, 1f)
        val k = 1.9f * exp(-dist(p, bombilla, 0.8f) / 95f)
        return (k * (0.3f + 0.7f * abajo)).coerceIn(0f, 1f)
    }

    /** Cuánto le llega a [p] la luz fría de la calle por el tragaluz (de noche, de 0 a 1): la derecha y lo alto. */
    fun calle(p: Offset): Float {
        val t = Offset(tragaluz.center.x - 20f * U, tragaluz.bottom)
        return (0.9f * exp(-dist(p, t, 0.85f) / 150f)).coerceIn(0f, 0.75f)
    }

    /**
     * La luz de un punto: de día, la del tubo (fuera del cuadro, arriba a la izquierda); de noche, la de la lámpara colgante (su
     * charco) y, del otro lado, la fría de la calle (va en la «mancha» de [LuzDistopia], la misma que la luna de los Sueños de la
     * biblioteca: vira el material a frío y un paso más claro, y deja el filo frío en los cantos). [pared]: en la pared, el rebote
     * de la lámpara en el cuarto (ancho, flojo y sin color).
     */
    fun luz(a: AireDistopia, p: Offset, pared: Boolean = false): LuzDistopia {
        val xr = (p.x - cx) / (380f * U) + 0.5f
        val yr = p.y / (380f * U)
        val amb = (1.02f - 0.52f * xr - 0.14f * yr).coerceIn(0.3f, 1f)
        if (!a.oscuro) return a.en(0f, PaletaDistopia.BOMBILLA, 0f, amb)
        // La pared está detrás de la lámpara, más lejos que lo de la mesa: le llega menos.
        val k = kLampara(p) * (if (pared) 0.72f else 1f)
        val lav =if (pared) (0.6f * exp(-dist(p, bombilla, 0.9f) / 130f)).coerceAtMost(0.6f) else 0f
        return a.en(k, PaletaDistopia.BOMBILLA, calle(p) * (1f - k).coerceAtLeast(0f), amb, lav)
    }

    /** El lado de donde viene la luz a [p]: -1 si de la izquierda (el tubo de día; la lámpara de noche, para lo que está a su derecha). */
    fun hacia(a: AireDistopia, p: Offset): Float = if (!a.oscuro) -1f else if (p.x > lamparaX) -1f else 1f
}

/**
 * La libreta de momentos de Distopía: el «Expediente de momentos», de pie en el atril de chapa de la mesa del registro, en el
 * mismo sótano del archivo del ministerio que el Ideario y la estantería (la pared crema sucio arriba y verde gris abajo, con la
 * raya oliva). La carpeta de expediente de cartón manila, gruesa de folios (su canto asoma arriba y a la derecha, con alguna
 * copia de color y las papeletas que sobresalen, más cuantos más folios), con su pestaña, las rayas del doblez junto al lomo, la
 * etiqueta del formulario con el nombre y la cuenta, el balduque con su lazo y una papeleta sujeta con un clip. La mesa de chapa
 * con su tablero de linóleo, el canto y los cajones; a la izquierda, el sello de madera y el tampón abierto; a la derecha, el
 * pincho de las papeletas. En la pared, el tubo de la luz que la cruza arriba, la caja de la que cuelga la lámpara, el
 * interruptor y el tragaluz de la acera, con la humedad que baja de él.
 *
 * La luz: de día, el tubo fluorescente de fuera del cuadro (arriba a la izquierda) y la lámpara colgante apagada. De noche arde
 * la lámpara de tulipa esmaltada, a la izquierda del expediente: su charco enciende media tapa, el sello y el linóleo; del otro
 * lado, la luz fría de la calle entra por el tragaluz y deja su filo en los cantos de la derecha; lo demás se hunde en el gris
 * azulado del sótano. Lo fijo (la pared, el tragaluz, la lámpara, el atril, la mesa y lo de encima) se pinta una vez por tamaño
 * y modo; el expediente, que engorda y se abre, en cada dibujo.
 */
internal object MuebleLibretaDistopia {
    val ALTO: Dp = 262.dp

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaDistopia(w, h, density, e.hojas)
        // De noche, la «mancha» de la luz es la de la calle por el tragaluz (la luna de los Sueños de la biblioteca).
        val a = AireDistopia(oscuro, oscuro)
        clipRect(0f, 0f, w, h) {
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density:${s.g.toInt()}"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaDistopia) { rincon(s, a) }
                    granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            with(ExpedienteLibretaDistopia) { expediente(e, s, a, medidor) }
        }
    }
}
