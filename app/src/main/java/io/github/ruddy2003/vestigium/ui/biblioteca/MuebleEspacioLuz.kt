package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

/**
 * Un mapa de luz de Espacio (el libro del día y la libreta): una imagen chiquita (unas decenas de píxeles por lado) con la luz
 * que le llega a cada punto, pintada una sola vez por tamaño y por modo y estirada al dibujar. Así la caída de la luz con la
 * distancia a la lámpara dibujada (o a la tira de guardia) se pone en un solo dibujo, suave y barato, en lugar de un
 * degradado por pieza (que pesaba al pintar). Las piezas debajo se pintan con la luz entera y el mapa las apaga encima.
 */
internal class MapaLuzEspacio(private val ancho: Int, private val alto: Int) {
    private var clave = Long.MIN_VALUE
    private var imagen: ImageBitmap? = null

    /** El mapa para [clave]; si cambió (otro tamaño, otro modo), se vuelve a pintar con [color] en fracciones (0 a 1) del área. */
    fun de(clave: Long, color: (Float, Float) -> Color): ImageBitmap {
        val hecha = imagen
        if (hecha != null && clave == this.clave) return hecha
        val nueva = ImageBitmap(ancho, alto)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
            for (j in 0 until alto) for (i in 0 until ancho) {
                drawRect(color((i + 0.5f) / ancho, (j + 0.5f) / alto), Offset(i.toFloat(), j.toFloat()), Size(1f, 1f), blendMode = BlendMode.Src)
            }
        }
        imagen = nueva
        this.clave = clave
        return nueva
    }
}

/** La clave de un mapa: el tamaño del lienzo y el modo (en [modo], unos pocos bits). */
internal fun claveMapaEspacio(w: Float, h: Float, modo: Int): Long =
    (w.roundToInt().toLong() shl 32) or (h.roundToInt().toLong() shl 8) or modo.toLong()

/** Estira [mapa] sobre [r], con [modo] de mezcla (encima, o multiplicando). */
internal fun DrawScope.ponerMapaEspacio(mapa: ImageBitmap, r: Rect, modo: BlendMode = BlendMode.SrcOver) =
    drawImage(mapa, IntOffset.Zero, IntSize(mapa.width, mapa.height), IntOffset(r.left.roundToInt(), r.top.roundToInt()),
        IntSize(r.width.roundToInt().coerceAtLeast(1), r.height.roundToInt().coerceAtLeast(1)), blendMode = modo)
