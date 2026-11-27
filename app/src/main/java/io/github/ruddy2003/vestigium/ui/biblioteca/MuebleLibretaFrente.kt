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
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.pow

/**
 * Dónde va cada cosa en la libreta de Frente, para un lienzo de [w] × [h] (en píxeles; [u], la densidad). El objeto se mide por
 * el alto (que es fijo) y se acomoda a lo ancho: la grabadora y su cartucho un poco a la izquierda del centro, el flexo a su
 * izquierda y el osciloscopio a su derecha, cortado por el borde si el teléfono es angosto.
 */
internal class SitioLibretaFrente(val w: Float, val h: Float, val u: Float) {
    /** El ancho de la grabadora: por el alto, sin pasar de dos tercios del ancho. */
    val dw = minOf(h * 0.82f, w * 0.66f)
    val cx: Float = run {
        val lo = dw / 2 + h * 0.12f; val hi = w - dw / 2 - h * 0.1f
        if (lo < hi) (w * 0.47f).coerceIn(lo, hi) else w * 0.5f
    }
    /** El pupitre: donde su cara de arriba toca el muro, su canto de delante y el pie de su labio curvo. */
    val mesa = h * 0.715f
    val canto = h * 0.77f
    val labio = h * 0.83f
    /** La grabadora (la cara de arriba, hasta [tapa]; debajo, su frente). */
    val deck = Rect(cx - dw / 2, h * 0.49f, cx + dw / 2, h * 0.752f)
    val tapa = h * 0.535f
    /** La cara del cartucho, de pie en la ranura (lo de abajo queda dentro). */
    val cw = dw * 0.72f
    val cart = Rect(cx - cw / 2, h * 0.085f, cx + cw / 2, h * 0.53f)
    /** La boca del flexo, el codo de sus brazos y la placa atornillada al muro. */
    val boca = Offset(cart.left - h * 0.1f, h * 0.29f)
    val placa = Offset(maxOf(7f * u, boca.x - h * 0.22f), h * 0.2f)
    val codo = Offset((placa.x + boca.x) / 2 - h * 0.03f, h * 0.06f)
    /** Hacia donde cae la luz del flexo (el centro de su charco). */
    val charco = Offset(cart.left + h * 0.04f, h * 0.6f)
    /** El osciloscopio y su pantalla. */
    val scope = Rect(deck.right + h * 0.035f, h * 0.36f, deck.right + h * 0.035f + h * 0.44f, h * 0.752f)
    val pantalla = Rect(scope.left + h * 0.04f, scope.top + h * 0.05f, scope.left + h * 0.28f, scope.top + h * 0.215f)
}

/**
 * La libreta de momentos de Frente exterior (la usa `EstanteFrente`, en `BibliotecaFrente.kt`): el puesto de grabación de la
 * célula, en la misma sala de radar del Ideario y del estante, contra el mismo muro de sillares con sus raíces y su tubo de
 * cobre. Sobre el pupitre de chapa hueso, la grabadora de cartuchos de consola oscura con el cartucho de datos de pie en su
 * ranura: la caja de aluminio, la etiqueta de cinta de papel con el nombre y la cuenta, la ventana ahumada con sus dos bobinas.
 * Engorda con los bloques (el canto de arriba más grueso, la bobina de recogida más llena, los cartuchos llenos apilados sobre
 * el osciloscopio). A la izquierda, el flexo atornillado al muro; a la derecha, el osciloscopio con su onda verde. De día, el
 * flexo encendido pinta su charco tibio; de noche, arde en la penumbra verde y del otro lado el verde frío del osciloscopio
 * filetea lo que tiene cerca. Al tocarla, la grabadora suelta el cartucho, que sube un poco por la ranura.
 *
 * Lo fijo (el muro, el flexo, el pupitre, el osciloscopio y la grabadora) se pinta una vez por tamaño y modo en una imagen
 * guardada; encima, cada vez, el cartucho y los llenos, que cambian con los bloques y al tocarla.
 */
internal object MuebleLibretaFrente {
    private val P = PaletaFrente

    // ---------------------------------------------------------------- la luz

    /** La luz tibia del flexo en [p] (de 0 a 1): el charco bajo su boca, que se abre hacia abajo, y lo que tiene junto. */
    fun cerca(s: SitioLibretaFrente, p: Offset): Float {
        val d = (p - s.charco).getDistance() / (s.h * 0.95f)
        val junto = 1f - (p - s.boca).getDistance() / (s.h * 0.45f)
        return maxOf((1f - d).coerceIn(0f, 1f).toDouble().pow(1.25).toFloat(), 0.95f * junto.coerceIn(0f, 1f))
    }

    /** El frío de la pantalla del osciloscopio en [p] (de 0 a 1), de noche. */
    fun frio(s: SitioLibretaFrente, p: Offset, oscuro: Boolean): Float {
        if (!oscuro) return 0f
        val d = (p - s.pantalla.center).getDistance() / (s.h * 0.62f)
        return (1f - d).coerceIn(0f, 1f).toDouble().pow(1.4).toFloat()
    }

    /** El color de [c] en [p]: de día, con el charco del flexo encima; de noche, con la luz que le llega. [f], más claro u hondo. */
    fun en(c: Color, f: Float, p: Offset, s: SitioLibretaFrente, a: AireFrente): Color {
        val k = cerca(s, p)
        return if (a.oscuro) a.luz(c, k, frio(s, p, true)) else a.cupula(a.dia(c, f - 0.1f * (1f - k)), 0.65f * k)
    }

    // ---------------------------------------------------------------- lo fijo, guardado

    private var clave: String? = null
    private var imagen: ImageBitmap? = null

    /** Pega lo fijo de arriba (de la fila 0 a la [abajo]); lo pinta antes con [pintar] si cambió el tamaño o el modo. */
    private fun DrawScope.fijo(nueva: String, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt()
        if (ancho < 1 || abajo < 1) return
        var img = imagen
        if (img == null || clave != nueva) {
            val lienzo = ImageBitmap(ancho, abajo)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(lienzo), Size(ancho.toFloat(), abajo.toFloat())) { pintar() }
            imagen = lienzo; clave = nueva; img = lienzo
        }
        drawImage(img, Offset.Zero)
    }

    // ---------------------------------------------------------------- la escena

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val s = SitioLibretaFrente(w, h, u)
        val a = AireFrente(oscuro, false)
        val ab = e.abierto.coerceIn(0f, 1f)
        // Todo lo que no cambia con los bloques ni al tocarla (el muro, el flexo, el pupitre, el osciloscopio, la grabadora y lo
        // de rotular): una vez por tamaño y modo. Encima, lo que sí: los cartuchos llenos, el cartucho y el canto de la ranura.
        fijo("$w:$h:$u:$fontScale:$oscuro", ceil(h).toInt()) {
            with(RinconLibretaFrente) {
                muro(s, a)
                pupitre(s, a)
                osciloscopio(s, a)
                grabadora(s, a)
                utiles(s, a)
            }
            granoMueble(Rect(0f, s.mesa, w, h), oscuro, 0.8f)
        }
        clipRect(0f, 0f, w, h) {
            with(CartuchoLibretaFrente) {
                llenos(e.hojas, s, a)
                cartucho(e, medidor, s, ab, a)
            }
            with(RinconLibretaFrente) { delanteDeck(s, a) }
        }
    }
}
