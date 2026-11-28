package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/*
 * Lo que comparten las piezas del estante de Soviético además de la luz (`MuebleEstanteUrss.kt`): los paños de la ventana de fuera
 * del cuadro en los Sueños (el último sol y la luna) y cómo pintan lo que tocan, la imagen guardada de lo de arriba y el color del
 * ánimo (el de día de noche, y su material en tela, papel o pintura).
 */

// ---------------------------------------------------------------- la luz de la ventana de los Sueños

/**
 * Los paños de luz de una ventana: el paralelogramo con esquina ([ox], [oy]) y lados ([ux], [uy]) (a lo largo, [cols] paños) y
 * ([vx], [vy]) (a lo alto), con los montantes de [barra] (en fracción de un paño). Solo montantes, sin travesaño: ninguna cruz.
 * De 0 a 1, con el borde neto.
 */
internal class PanosUrss(val ox: Float, val oy: Float, val ux: Float, val uy: Float, val vx: Float, val vy: Float,
                         val cols: Int, val barra: Float) {
    private val det = ux * vy - uy * vx

    fun en(x: Float, y: Float): Float {
        if (abs(det) < 1e-6f) return 0f
        val dx = x - ox; val dy = y - oy
        val s = (dx * vy - dy * vx) / det
        val t = (ux * dy - uy * dx) / det
        if (s <= 0f || s >= 1f || t <= 0f || t >= 1f) return 0f
        val cs = s * cols
        val fs = cs - floor(cs)
        val b = barra * 0.5f
        if (fs < b || fs > 1f - b) return 0f
        return (0.72f + 0.28f * (minOf(t, 1f - t) * 8f).coerceAtMost(1f))
    }

    /** Los paños, como camino en píxeles ([w], el ancho), corridos [dx], [dy] anchos. */
    fun camino(w: Float, dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        val b = barra * 0.5f
        for (c in 0 until cols) {
            val s0 = (c + b) / cols; val s1 = (c + 1 - b) / cols
            fun p(s: Float, t: Float) = Offset((ox + ux * s + vx * t + dx) * w, (oy + uy * s + vy * t + dy) * w)
            val a = p(s0, 0f); val bb = p(s1, 0f); val c2 = p(s1, 1f); val d = p(s0, 1f)
            moveTo(a.x, a.y); lineTo(bb.x, bb.y); lineTo(c2.x, c2.y); lineTo(d.x, d.y); close()
        }
    }
}

/** El último sol de los Sueños en claro: bajo y largo, casi tendido, en la cal de arriba, con sus tres paños. */
internal val OCASO_URSS = PanosUrss(0.2f, 0.03f, 0.3f, 0.05f, 0.05f, 0.19f, 3, 0.14f)

/** La luna de los Sueños en oscuro: más chica y más alta, en la cal entre el altavoz y el pliego. */
internal val LUNA_URSS = PanosUrss(0.17f, 0.015f, 0.27f, 0.045f, 0.05f, 0.2f, 3, 0.16f)

/**
 * Dibuja [pieza] con la mancha apagada y, recortada a los paños de la ventana para lo que está a [delante] anchos de la pared, otra
 * vez con la mancha encendida: así la luz pinta lo que toca, con borde neto. Sin paños (el diario), una sola vez.
 */
internal fun DrawScope.conPanosUrss(g: MarcoUrss, delante: Float, zona: Rect? = null, pieza: DrawScope.(Float?) -> Unit) {
    val e = g.panos
    if (e == null) { pieza(null); return }
    pieza(0f)
    val c = g.corre(delante)
    val camino = e.camino(g.w, c.x, c.y)
    val b = camino.getBounds()
    if (b.isEmpty) return
    if (zona != null && !b.overlaps(zona)) return
    clipRect(b.left, b.top, b.right, b.bottom) { clipPath(camino) { pieza(1f) } }
}

/**
 * Como [conPanosUrss], pero sin pasar la mancha a [pieza]: la fija en el marco ([MarcoUrss.forzar]) mientras pinta, para lo que
 * cuelga de la pared, que así se pinta igual por dentro y por fuera de los paños, con su luz y con la sombra que le echa a la pared.
 */
internal fun DrawScope.conPanosTodoUrss(g: MarcoUrss, delante: Float, zona: Rect, pieza: DrawScope.() -> Unit) {
    val e = g.panos
    if (e == null) { pieza(); return }
    g.forzar = 0f
    pieza()
    val c = g.corre(delante)
    val camino = e.camino(g.w, c.x, c.y)
    val b = camino.getBounds()
    if (!b.isEmpty && b.overlaps(zona)) {
        g.forzar = 1f
        clipRect(b.left, b.top, b.right, b.bottom) { clipPath(camino) { pieza() } }
    }
    g.forzar = null
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega (en dos ranuras: el diario y
 * los Sueños). La imagen llega hasta el píxel entero de abajo y se pega en un píxel entero: su última fila, que puede ser medio
 * píxel, deja ver lo que se pintó debajo (el pie de la cornisa), no el fondo.
 */
internal object CapaUrss {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    fun pegar(ds: DrawScope, ranura: Int, clave: String, alto: Float, pintar: DrawScope.() -> Unit) {
        val w = ceil(ds.size.width).toInt()
        val h = ceil(alto).toInt().coerceAtMost(ceil(ds.size.height).toInt())
        if (w < 1 || h < 1 || ranura !in 0..1) return
        val img = imagenes[ranura].takeIf { claves[ranura] == clave } ?: ImageBitmap(w, h).also { nueva ->
            CanvasDrawScope().draw(Density(ds.density, ds.fontScale), ds.layoutDirection, Canvas(nueva), Size(w.toFloat(), h.toFloat())) {
                clipRect(0f, 0f, ds.size.width, alto) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave
        }
        ds.drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), dstOffset = IntOffset.Zero, dstSize = IntSize(img.width, img.height))
    }
}

// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de Soviético, la de día y la de noche (bajo, medio, alto). */
private val TONOS_URSS: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.URSS, false) to juegoTonos(TemaId.URSS, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaUrss(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val ex = t.red - a0.red - dr * f; val ey = t.green - a0.green - dg * f; val ez = t.blue - a0.blue - db * f
        val er = ex * ex + ey * ey + ez * ez
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia (el ánimo de en medio pasa de crudo a casi
 * negro y el de arriba, de negro a crudo); aquí la entrada es un objeto del cuarto: conserva su color de día y la luz lo apaga con
 * la distancia. Se ubica el color en la escala de noche y se toma el mismo punto en la de día; si no está, queda igual.
 */
internal fun tonoDeDiaUrss(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_URSS
    val x = enEscalaUrss(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/**
 * El material del ánimo: se ubica el color (de día) en la escala del tema y se toma el mismo punto en [materiales] (tela, papel o
 * pintura, de lo malo a lo bueno: rojo, crudo, negro). Si el color no está en la escala, queda igual.
 */
internal fun materialDelAnimoUrss(t: Color, materiales: List<Color>): Color {
    val x = enEscalaUrss(t, TONOS_URSS.first) ?: return t
    if (x >= 0.5f) {
        // Del crudo al negro: el gris de en medio, algo cálido (el del papel sucio, no un gris de oficina).
        val c = mezcla(materiales[1], materiales[2], (x - 0.5f) * 2)
        return vivoUrss(c, 1.25f)
    }
    // Del rojo al crudo: el rosa de en medio, con más color (un coral de cartel, no un rosa lavado) y un punto más hondo.
    val c = mezcla(materiales[0], materiales[1], x * 2)
    val f = 1f - abs(x - 0.25f) * 4f
    return vivoUrss(Color(c.red, c.green * (1f - 0.12f * f), c.blue * (1f - 0.12f * f), c.alpha), 1f + 0.45f * f.coerceIn(0f, 1f))
}

/** [c] con más color: se aleja de su gris (del mismo valor) en [f]; así los tonos de en medio no se lavan. */
internal fun vivoUrss(c: Color, f: Float): Color {
    val g = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
    return Color((g + (c.red - g) * f).coerceIn(0f, 1f), (g + (c.green - g) * f).coerceIn(0f, 1f), (g + (c.blue - g) * f).coerceIn(0f, 1f), c.alpha)
}
