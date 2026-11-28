package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trazos que comparten las piezas del estante de Refugio: caminos (corrido, borde, polígono, curva suave), el volumen de gouache
 * de una forma y de un cilindro, el cromo de una moldura, la pintura saltada, el tornillo y los renglones que no se leen.
 */

/** [p] corrido. */
internal fun corridoRefugio(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeRefugio(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoRefugio(p, dx, dy), PathOperation.Difference) }

/** Un polígono de lados rectos por los puntos [q]. */
internal fun poligonoRefugio(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 1 until q.size) lineTo(q[i].x, q[i].y)
    close()
}

internal fun poligonoRefugio(a: Offset, b: Offset, c: Offset): Path = poligonoRefugio(listOf(a, b, c))
internal fun poligonoRefugio(a: Offset, b: Offset, c: Offset, d: Offset): Path = poligonoRefugio(listOf(a, b, c, d))

/** Una figura cerrada que pasa suave por los puntos [q] (Catmull-Rom pasado a Bézier). */
internal fun cerradaRefugio(q: List<Offset>): Path = Path().apply {
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
internal fun abiertaRefugio(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 0 until q.size - 1) {
        val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
}

/** Una forma irregular de cantos vivos alrededor de [c] (pintura saltada, desconchón): un polígono desigual. */
internal fun escamaRefugio(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 9): Path = Path().apply {
    for (k in 0 until m) {
        val a = 6.2832f * k / m + azar(semilla, k) * 0.3f
        val r = if (k % 3 == 0) 0.55f + 0.2f * azar(semilla, k + 30) else 0.85f + 0.25f * azar(semilla, k + 50)
        val p = Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/**
 * Esmalte saltado por el roce: la chapa negra que asoma (con un punto de óxido en algunos) y el borde del esmalte que coge la
 * luz. [hierro] y [luz] ya vienen con la luz del sitio.
 */
internal fun DrawScope.saltadoRefugio(c: Offset, r: Float, semilla: Int, hierro: Color, luz: Color, oxido: Color? = null) {
    val u = density
    val p = escamaRefugio(c, r, r * 0.65f, semilla, 7)
    drawPath(p, hierro)
    if (oxido != null && azar(semilla, 90) < 0.5f) drawCircle(oxido, r * 0.35f, Offset(c.x + r * 0.1f, c.y + r * 0.15f))
    translate(0.7f * u, 0.7f * u) { drawPath(p, luz, style = Stroke(0.6f * u)) }
}

/** Una abolladura en la chapa: hundida, con la sombra arriba a la izquierda y la luz en su borde de abajo a la derecha. */
internal fun DrawScope.abolladuraRefugio(c: Offset, rx: Float, ry: Float, l: LuzRefugio) {
    drawOval(l.s(0.12f), Offset(c.x - rx, c.y - ry), Size(2 * rx, 2 * ry))
    drawOval(l.s(0.1f), Offset(c.x - rx * 0.9f, c.y - ry * 0.9f), Size(rx * 1.3f, ry * 1.2f))
    drawOval(l.b(0.22f), Offset(c.x - rx * 0.2f, c.y - ry * 0.05f), Size(rx * 1.1f, ry * 0.95f))
}

/** Renglones que no se leen: tramos de largo desigual en [r], de [alto] cada uno, cada [paso]. Todo en un solo camino. */
internal fun renglonesRefugio(r: Rect, alto: Float, paso: Float, semilla: Int): Path = Path().apply {
    if (paso < 0.5f || alto < 0.2f || r.width < 2f) return@apply
    var y = r.top; var j = 0
    while (y + alto <= r.bottom && j < 60) {
        var x = r.left
        val fin = r.right - r.width * 0.25f * azar(semilla, 300 + j)
        var q = 0
        while (x < fin && q < 40) {
            val ancho = r.width * (0.07f + 0.17f * azar(semilla + j, q))
            addRect(Rect(x, y, minOf(x + ancho, fin), y + alto))
            x += ancho + r.width * 0.04f; q++
        }
        y += paso; j++
    }
}

/**
 * El volumen de una forma en planos de gouache, de un solo relleno: el plano de luz (el mismo color, más claro), el tono entero y
 * el plano de sombra (más hondo, con la sombra de color), con bordes nítidos entre ellos. [hacia]: -1 si la luz viene de la
 * izquierda, 1 si de la derecha.
 */
internal fun DrawScope.planosRefugio(f: Path, base: Color, l: LuzRefugio, hacia: Float, luzAncho: Float, sombraAncho: Float) {
    val r = f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = Color((base.red * 1.17f + 0.06f).coerceAtMost(1f), (base.green * 1.15f + 0.06f).coerceAtMost(1f), (base.blue * 1.12f + 0.05f).coerceAtMost(1f), base.alpha)
    val luz = if (l.a.oscuro) mezcla(base, mezcla(claro, l.tinte, 0.3f), (0.1f + 1.4f * l.k).coerceAtMost(1f)) else claro
    val honda = if (l.a.oscuro) mezcla(base, l.a.hondo, 0.5f)
        else mezcla(Color(base.red * 0.7f, base.green * 0.72f, base.blue * 0.76f, base.alpha), l.a.sombra(1f).copy(alpha = 1f), 0.16f)
    val media = mezcla(base, honda, 0.55f)
    val izq = if (hacia < 0f) luz else honda
    val der = if (hacia < 0f) honda else luz
    val a1 = if (hacia < 0f) luzAncho else sombraAncho
    val a2 = 1f - (if (hacia < 0f) sombraAncho else luzAncho)
    val stops = if (hacia < 0f) arrayOf(0f to izq, a1 to izq, a1 to base, a2 to base, a2 to media, 1f to der)
        else arrayOf(0f to izq, a1 to media, a1 to base, a2 to base, a2 to der, 1f to der)
    drawPath(f, Brush.horizontalGradient(colorStops = stops, startX = r.left, endX = r.right))
}

/**
 * El volumen de una forma redonda (un cilindro, un bote): degradado a lo ancho del claro a la sombra, con el brillo largo del lado
 * de la luz. Para el esmalte y la goma.
 */
internal fun DrawScope.cilindroRefugio(f: Path, base: Color, l: LuzRefugio, hacia: Float, brillo: Float = 0.6f) {
    val r = f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = if (l.a.oscuro) mezcla(base, l.tinte, 0.1f + 0.5f * l.k) else Color((base.red * 1.2f + 0.08f).coerceAtMost(1f), (base.green * 1.18f + 0.08f).coerceAtMost(1f), (base.blue * 1.14f + 0.08f).coerceAtMost(1f), base.alpha)
    val honda = mezcla(base, if (l.a.oscuro) l.a.hondo else l.a.sombra(1f).copy(alpha = 1f), if (l.a.oscuro) 0.55f else 0.38f)
    val izqADer = listOf(mezcla(base, claro, 0.5f), claro, base, base, mezcla(base, honda, 0.6f), honda)
    drawPath(f, Brush.horizontalGradient(if (hacia < 0f) izqADer else izqADer.asReversed(), r.left, r.right))
    val bx = if (hacia < 0f) r.left + r.width * 0.2f else r.right - r.width * 0.3f
    drawRect(l.b(brillo), Offset(bx, r.top + r.height * 0.08f), Size(r.width * 0.09f, r.height * 0.84f))
}

/**
 * El cromo de una pieza: no tiene color propio, refleja. Bandas a lo ancho de [r] (o a lo alto con [vertical]): el claro del
 * techo, el reflejo oscuro del suelo y una raya de brillo. De noche, oscuro y con el color de la luz que le llega.
 */
internal fun DrawScope.cromoRefugio(r: Rect, l: LuzRefugio, vertical: Boolean = false, brillo: Float = 0.9f) {
    if (r.width < 0.5f || r.height < 0.5f) return
    val claro = l.c(P.CROMO_LUZ); val medio = l.c(P.CROMO); val oscuroC = l.c(P.CROMO_OSC)
    val bandas = if (l.a.oscuro) listOf(mezcla(medio, l.tinte, 0.35f * l.k), oscuroC, medio, oscuroC)
        else listOf(claro, medio, oscuroC, medio, claro)
    if (vertical) drawRect(Brush.horizontalGradient(bandas, r.left, r.right), r.topLeft, r.size)
    else drawRect(Brush.verticalGradient(bandas, r.top, r.bottom), r.topLeft, r.size)
    val u = density
    if (vertical) drawRect(l.b(brillo), Offset(r.left + r.width * 0.22f, r.top), Size(maxOf(0.7f * u, r.width * 0.12f), r.height))
    else drawRect(l.b(brillo), Offset(r.left, r.top + r.height * 0.18f), Size(r.width, maxOf(0.6f * u, r.height * 0.14f)))
}

/** Un tornillo de cabeza redonda: la cabeza, su ranura y el brillo. */
internal fun DrawScope.tornilloRefugio(c: Offset, r: Float, l: LuzRefugio, semilla: Int) {
    val u = density
    drawCircle(l.s(0.45f), r, c + Offset(0.6f * u, 0.8f * u))
    drawCircle(l.c(P.ACERO), r, c)
    drawCircle(l.b(0.6f), r * 0.45f, c - Offset(r * 0.3f, r * 0.3f))
    val a = azar(semilla, 7) * 3.1416f
    drawLine(l.c(P.ACERO_OSC), Offset(c.x - cos(a) * r * 0.75f, c.y - sin(a) * r * 0.75f), Offset(c.x + cos(a) * r * 0.75f, c.y + sin(a) * r * 0.75f), maxOf(0.6f * u, r * 0.28f))
}

/** Un botón cromado abombado (los de los costados, los mandos): el aro, el claro arriba a la izquierda y el reflejo oscuro. */
internal fun DrawScope.botonCromoRefugio(c: Offset, r: Float, l: LuzRefugio) {
    val u = density
    drawCircle(l.s(0.45f), r, c + Offset(0.7f * u, 0.9f * u))
    drawCircle(l.c(P.CROMO_OSC), r, c)
    drawCircle(l.c(P.CROMO), r * 0.82f, c)
    drawCircle(l.c(mezcla(P.CROMO_OSC, P.CROMO, 0.4f)), r * 0.5f, c + Offset(r * 0.15f, r * 0.18f))
    drawCircle(l.b(0.9f), r * 0.28f, c - Offset(r * 0.3f, r * 0.32f))
}
