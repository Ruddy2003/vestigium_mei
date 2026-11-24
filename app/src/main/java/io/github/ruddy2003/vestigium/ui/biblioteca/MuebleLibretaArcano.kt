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
 * Dónde va cada cosa del rincón de la libreta de Horror cósmico, en píxeles, para un lienzo de [w] por [h] y un diario de [hojas]
 * recortes. Todo se mide en [U] (un dp del alto de diseño), así el rincón no se deforma si cambia el ancho: el diario va al centro
 * y lo de los lados se corta por el borde.
 */
internal class SitioLibretaArcano(val w: Float, val h: Float, u: Float, hojas: Int) {
    val U = u * (h / (MuebleLibretaArcano.ALTO.value * u)).coerceIn(0.5f, 2f)
    /** La repisa alta de la pared: la tabla, de arriba abajo, y sus ménsulas. */
    val repisaArriba = 26f * U
    val repisaAbajo = 34f * U
    /** La mesa: el fondo del tablero (contra la pared), su canto de delante y el pie del lienzo. */
    val mesaFondo = 213f * U
    val mesaCanto = 228f * U
    val mesaCantoAbajo = 237f * U
    /** Donde pisa lo de encima de la mesa (algo delante del fondo). */
    val pisa = 222f * U
    /** El diario: su tapa, de frente, apoyada en el atril; [g] es el grueso (engorda con los recortes: de 12 a 38 dp). */
    val g = (12f + 26f * sqrt(hojas.coerceIn(0, 240) / 240f)) * U
    val cx = w * 0.43f
    val bw = minOf(176f * U, w * 0.47f)
    val libro = Rect(cx - bw / 2, 46f * U, cx + bw / 2, 216f * U)
    /** El canto de las hojas en escorzo: hacia la derecha y hacia arriba (se ve la cabeza y el corte de delante). */
    val v = Offset(0.55f * g, -0.32f * g)
    /** El atril de mesa donde descansa el diario: su listón de delante. */
    val atril = Rect(libro.left - 10f * U, 215f * U, libro.right + v.x + 8f * U, 224f * U)
    /** La lámpara de estudiante: el pie, la columna y la llama bajo la pantalla verde. */
    val lampara = Offset(minOf(w * 0.86f, libro.right + v.x + 92f * U), pisa)
    val llama = Offset(lampara.x - 30f * U, 124f * U)
    /** El tazón de lata, entre el diario y la lámpara; el bote de engrudo y las tijeras, a la izquierda. */
    val tazon = Offset((libro.right + v.x + lampara.x - 30f * U) / 2f + 6f * U, pisa + 2f * U)
    val bote = Offset(libro.left - 40f * U, pisa - 1f * U)
}

/**
 * La libreta de momentos de Horror cósmico: el diario de recortes del archivista en la oficina del mismo archivo (la casa del
 * Ideario y de la biblioteca), de noche y con prisa. La pared está encalada de azulete, como las casas de los pescadores,
 * comida por la humedad (asoma una mano vieja de ocre y el yeso); la cruza una repisa alta de tabla sobre dos ménsulas con los
 * fardos de periódicos atados, una lata y un tintero, y de un clavo cuelga el morral de lona. En la mesa de teca, el diario
 * de recortes de pie en su atril: tapas de tela sangre de buey con lomo y puntas de piel, hinchado por los recortes pegados
 * (engorda con ellos y le asoman por la cabeza y el corte), atado con una cinta de hilo y con la etiqueta del nombre y la cuenta.
 * Al tocarlo, la cinta se suelta y la tapa gira sobre el lomo y deja ver la primera hoja con sus recortes. A la izquierda, el
 * bote de engrudo con su pincel y las tijeras; a la derecha, el tazón de lata del café y la lámpara de estudiante de petróleo
 * con su pantalla de vidrio verde.
 *
 * La luz: de día, la de una ventana que queda fuera a la izquierda, gris y fría; de noche arde la lámpara (su charco enciende
 * la mesa, el corte del diario y la pared de la derecha; la pantalla verde brilla arriba) y por la izquierda entra un roce frío
 * de la luna; lo demás se hunde en el verde hondo (la pared) y el violeta (la madera). La paleta y el aire son los de la
 * biblioteca ([PaletaArcano], [AireArcano]). Lo fijo (la pared, la repisa, la mesa, la lámpara y lo de encima) se pinta una vez
 * por tamaño y modo ([RinconLibretaArcano]); el diario, en cada dibujo ([LibroLibretaArcano]).
 */
internal object MuebleLibretaArcano {
    val ALTO: Dp = 260.dp

    /** Cuánta luz de la lámpara le llega a [p] (1 bajo la pantalla, poca lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioLibretaArcano, a: AireArcano): Float {
        if (!a.oscuro) return 1f
        val dx = (p.x - s.llama.x) / s.U; val dy = (p.y - s.llama.y) / s.U
        // Bajo la pantalla la luz va hacia abajo: arriba (encima de la pantalla) se apaga antes.
        return (1.6f * exp(-0.0125f * sqrt(dx * dx * 0.85f + dy * dy * (if (dy < 0f) 2.2f else 0.75f)))).coerceIn(0.03f, 1f)
    }

    /** El roce frío de la luna por la izquierda, de noche (de 1 junto al borde a 0 hacia el centro). */
    fun frio(p: Offset, s: SitioLibretaArcano): Float = (1f - p.x / (s.w * 0.55f)).coerceIn(0f, 1f).let { it * it }

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val s = SitioLibretaArcano(w, h, density, e.hojas)
        val a = AireArcano(oscuro, false)
        val am = AireArcano(oscuro, false, AireArcano.MUEBLE)
        clipRect(0f, 0f, w, h) {
            // La pared, la repisa, la mesa, el atril, la lámpara y lo de encima: una imagen por tamaño y modo.
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaArcano) { rincon(s, a, am) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            // El diario, en vivo (engorda, se abre, lleva el nombre y la cuenta); delante, el listón del atril y la luz de la lámpara.
            with(LibroLibretaArcano) { diario(e, s, am, medidor) }
            with(RinconLibretaArcano) { delante(s, am) }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
        }
    }

    // ---------------------------------------------------------------- el índice (lo llama `EstanteArcano`)

    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) = with(IndiceLibretaArcano) { pestana(e, medidor, oscuro) }
}

/** El color de [c] con la luz de la lámpara que le llega en [p]: de día, tal cual (con [f] más claro o más hondo). */
internal fun SitioLibretaArcano.c(a: AireArcano, col: Color, p: Offset, f: Float = 0f): Color = a.dia(col, f, MuebleLibretaArcano.cerca(p, this, a))
