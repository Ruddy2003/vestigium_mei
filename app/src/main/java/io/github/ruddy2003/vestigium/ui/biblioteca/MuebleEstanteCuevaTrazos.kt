package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/*
 * Los trazos que comparten las piezas del estante de Prehistoria: la mancha de la boca pintada dos veces (fuera y dentro, con su
 * borde neto), los caminos (corridos, medias lunas, figuras suaves, piedras y polígonos) y el volumen de gouache de las cosas, el
 * completo (para lo que se pinta una sola vez, en la corona guardada) y el barato (para lo que se pinta en cada dibujo).
 */

// ---------------------------------------------------------------- ayudantes de dibujo

/**
 * Dibuja [pieza] dos veces: a la luz de la cueva (con la mancha de la boca apagada) y, recortada a la mancha para lo que está a
 * [delante] anchos de la pared, otra vez con la mancha encendida. Así la luz pinta lo que toca, con borde neto. Sin mancha (la
 * noche del diario), una sola vez.
 */
internal fun DrawScope.conManchaCueva(g: MarcoCueva, delante: Float, zona: Rect? = null, pieza: DrawScope.(Float?) -> Unit) {
    val m = g.mancha(delante)
    if (m == null) { pieza(null); return }
    pieza(0f)
    // Lo que queda lejos de la mancha no se pinta dos veces.
    val b = m.getBounds()
    if (zona != null && !b.overlaps(zona)) return
    clipRect(b.left, b.top, b.right, b.bottom) { clipPath(m) { pieza(1f) } }
}

/** [p] corrido. */
internal fun corridoCueva(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeCueva(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoCueva(p, dx, dy), PathOperation.Difference) }

/** Una figura cerrada que pasa suave por los puntos [q] (Catmull-Rom pasado a Bézier). */
internal fun cerradaCueva(q: List<Offset>): Path = Path().apply {
    val m = q.size
    moveTo(q[0].x, q[0].y)
    for (i in 0 until m) {
        val a = q[(i - 1 + m) % m]; val b = q[i]; val c = q[(i + 1) % m]; val d = q[(i + 2) % m]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
    close()
}

/** Una línea abierta que pasa suave por [q]. */
internal fun abiertaCueva(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 0 until q.size - 1) {
        val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
}

/** Un contorno de piedra alrededor de [c]: [m] puntos con un poco de azar. */
internal fun piedraCueva(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 9, rudo: Float = 0.22f): Path =
    cerradaCueva(List(m) { k ->
        val a = 6.2832f * k / m + azar(semilla, k) * 0.35f
        val r = 1f - rudo + 2 * rudo * azar(semilla, k + 50)
        Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
    })

/** Un polígono de aristas vivas (sílex, terrones): [m] vértices alrededor de [c]. */
internal fun poligonoCueva(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int): Path = Path().apply {
    for (k in 0 until m) {
        val a = 6.2832f * k / m + azar(semilla, k) * 0.5f
        val r = 0.7f + 0.3f * azar(semilla, k + 20)
        val p = Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/**
 * El volumen de gouache sobre algo ya recortado en [f]: el color [base], la media luna de sombra del lado contrario a la luz
 * ([hacia]: -1 si la luz viene de la izquierda, 1 si de la derecha), el claro del lado de la luz y el filo del fuego.
 */
internal fun DrawScope.bultoCueva(f: Path, base: Color, l: LuzCueva, hacia: Float, fuerza: Float = 1f, claroA: Float = 0.34f) {
    val r = f.getBounds(); val an = r.width; val al = r.height
    drawPath(f, base)
    drawPath(bordeCueva(f, -hacia * an * 0.24f, -al * 0.06f), l.s(0.24f * fuerza))
    drawPath(bordeCueva(f, -hacia * an * 0.09f, -al * 0.03f), l.s(0.2f * fuerza))
    drawPath(bordeCueva(f, hacia * an * 0.1f, al * 0.14f), l.b(claroA))
    if (l.k > 0.05f) drawPath(bordeCueva(f, hacia * an * 0.05f, al * 0.03f), l.filo(0.55f))
}

/**
 * La redondez de algo ya pintado en [f], barata (para lo que se pinta en cada dibujo): en la misma forma, un degradado a lo ancho con
 * el claro del lado de la luz ([hacia]: -1 si viene de la izquierda) y la sombra, cada vez más honda, del otro; sin operaciones de
 * caminos ni recortes.
 */
internal fun DrawScope.sombreadoCueva(f: Path, l: LuzCueva, hacia: Float, fuerza: Float = 1f, claroA: Float = 0.34f) {
    val r = f.getBounds()
    if (r.width < 1f) return
    val claro = l.b(claroA); val sombra = l.s(0.3f * fuerza); val honda = l.s(0.5f * fuerza)
    val izqADer = listOf(claro, claro.copy(alpha = claro.alpha * 0.35f), claro.copy(alpha = 0f), sombra.copy(alpha = 0f), sombra, honda)
    drawPath(f, Brush.horizontalGradient(if (hacia < 0f) izqADer else izqADer.asReversed(), r.left, r.right))
}

/**
 * El volumen de [bultoCueva], barato: un solo relleno con un degradado a lo ancho de colores llenos (el color con su claro del lado
 * de la luz y su sombra, cada vez más honda, del otro).
 */
internal fun DrawScope.bultoRapidoCueva(f: Path, base: Color, l: LuzCueva, hacia: Float, fuerza: Float = 1f, claroA: Float = 0.34f) {
    val r = f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = l.b(claroA); val sombra = l.s(0.3f * fuerza); val honda = l.s(0.5f * fuerza)
    fun sobre(c: Color, a: Float) = mezcla(base, c.copy(alpha = 1f), c.alpha * a)
    val izqADer = listOf(sobre(claro, 1f), sobre(claro, 0.35f), base, base, sobre(sombra, 1f), sobre(honda, 1f))
    drawPath(f, Brush.horizontalGradient(if (hacia < 0f) izqADer else izqADer.asReversed(), r.left, r.right))
}
