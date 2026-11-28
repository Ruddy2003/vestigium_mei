package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Los trazos que comparten las piezas del estante de Soviético: caminos (corrido, polígono, curva suave, escama, mancha blanda),
 * el volumen de gouache de una forma (por planos y redondo), el tornillo, los bloques que sugieren letras sin que se lea nada, y
 * las tres baldosas pintadas una sola vez (la veta del roble a lo largo y a lo alto, y el parquet en espiga).
 */

/** [p] corrido. */
internal fun corridoUrss(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: el borde contrario al corrimiento. */
internal fun bordeUrss(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoUrss(p, dx, dy), PathOperation.Difference) }

/** Un polígono de lados rectos por los puntos [q]. */
internal fun poligonoUrss(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 1 until q.size) lineTo(q[i].x, q[i].y)
    close()
}

internal fun poligonoUrss(a: Offset, b: Offset, c: Offset): Path = poligonoUrss(listOf(a, b, c))
internal fun poligonoUrss(a: Offset, b: Offset, c: Offset, d: Offset): Path = poligonoUrss(listOf(a, b, c, d))

/** Una figura cerrada que pasa suave por los puntos [q] (Catmull-Rom pasado a Bézier). */
internal fun cerradaUrss(q: List<Offset>): Path = Path().apply {
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
internal fun abiertaUrss(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 0 until q.size - 1) {
        val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
}

/** Una forma irregular de cantos vivos alrededor de [c] (pintura saltada, desconchón). */
internal fun escamaUrss(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 9): Path = Path().apply {
    for (k in 0 until m) {
        val a = 6.2832f * k / m + azar(semilla, k) * 0.3f
        val r = if (k % 3 == 0) 0.55f + 0.2f * azar(semilla, k + 30) else 0.85f + 0.25f * azar(semilla, k + 50)
        val p = Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Una mancha blanda alrededor de [c] (humedad, cerco): una curva cerrada de radios algo desiguales. */
internal fun blandaUrss(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 9): Path = cerradaUrss(List(m) { k ->
    val a = 6.2832f * k / m + azar(semilla, k) * 0.35f
    val r = 0.74f + 0.36f * azar(semilla, k + 50)
    Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
})

/**
 * El volumen de una forma en planos de gouache, de un solo relleno: el plano de luz, el tono entero y el plano de sombra, con
 * bordes nítidos entre ellos. [hacia]: -1 si la luz viene de la izquierda, 1 si de la derecha.
 */
internal fun DrawScope.planosUrss(f: Path, base: Color, l: LuzUrss, hacia: Float, luzAncho: Float, sombraAncho: Float) {
    val r = f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = Color((base.red * 1.16f + 0.06f).coerceAtMost(1f), (base.green * 1.14f + 0.06f).coerceAtMost(1f), (base.blue * 1.1f + 0.05f).coerceAtMost(1f), base.alpha)
    val luz = if (l.a.oscuro) mezcla(base, mezcla(claro, l.tinte, 0.25f), (0.12f + 1.4f * l.k).coerceAtMost(1f)) else claro
    val honda = if (l.a.oscuro) mezcla(base, l.a.hondo, 0.5f)
        else mezcla(Color(base.red * 0.7f, base.green * 0.71f, base.blue * 0.76f, base.alpha), l.a.sombra(1f).copy(alpha = 1f), 0.14f)
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
 * El volumen de una forma redonda (un rollo, una lata, una bobina): degradado a lo ancho del claro a la sombra, con el brillo largo
 * del lado de la luz.
 */
internal fun DrawScope.cilindroUrss(f: Path, base: Color, l: LuzUrss, hacia: Float, brillo: Float = 0.6f, r0: Rect? = null) {
    val r = r0 ?: f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = if (l.a.oscuro) mezcla(base, l.tinte, 0.1f + 0.5f * l.k) else Color((base.red * 1.18f + 0.08f).coerceAtMost(1f), (base.green * 1.16f + 0.08f).coerceAtMost(1f), (base.blue * 1.12f + 0.08f).coerceAtMost(1f), base.alpha)
    val honda = mezcla(base, if (l.a.oscuro) l.a.hondo else l.a.sombra(1f).copy(alpha = 1f), if (l.a.oscuro) 0.55f else 0.36f)
    val izqADer = listOf(mezcla(base, claro, 0.5f), claro, base, base, mezcla(base, honda, 0.6f), honda)
    drawPath(f, Brush.horizontalGradient(if (hacia < 0f) izqADer else izqADer.asReversed(), r.left, r.right))
    if (brillo > 0f) {
        val bx = if (hacia < 0f) r.left + r.width * 0.2f else r.right - r.width * 0.3f
        drawRect(l.b(brillo), Offset(bx, r.top + r.height * 0.06f), Size(r.width * 0.09f, r.height * 0.88f))
    }
}

/** Un tornillo de cabeza ranurada: la cabeza, su ranura y el brillo. */
internal fun DrawScope.tornilloUrss(c: Offset, r: Float, l: LuzUrss, semilla: Int, metal: Color = P.LATON) {
    val u = density
    drawCircle(l.s(0.45f), r, c + Offset(0.6f * u, 0.8f * u))
    drawCircle(l.c(metal), r, c)
    drawCircle(l.b(0.6f), r * 0.45f, c - Offset(r * 0.3f, r * 0.3f))
    val a = azar(semilla, 7) * 3.1416f
    drawLine(l.c(mezcla(metal, P.NEGRO, 0.55f)), Offset(c.x - cos(a) * r * 0.75f, c.y - sin(a) * r * 0.75f), Offset(c.x + cos(a) * r * 0.75f, c.y + sin(a) * r * 0.75f), maxOf(0.6f * u, r * 0.28f))
}

/**
 * Renglones de bloques que sugieren letras sin que se lea nada (como los del cartel del Ideario), en un solo camino: desde [p], de
 * [alto] cada bloque, hasta [ancho]; con [renglones] renglones, el último más corto.
 */
internal fun bloquesUrss(p: Offset, ancho: Float, alto: Float, semilla: Int, renglones: Int = 1, paso: Float = 1.7f): Path = Path().apply {
    if (alto < 0.2f || ancho < 1f) return@apply
    for (j in 0 until renglones.coerceIn(1, 30)) {
        var x = p.x
        val y = p.y + j * alto * paso
        val fin = p.x + ancho * (if (j == renglones - 1 && renglones > 1) 0.4f + 0.4f * azar(semilla, 90 + j) else 1f)
        var k = 0
        while (x < fin - alto * 0.5f && k < 60) {
            val a = alto * (0.5f + 1.5f * azar(semilla + j * 17, k))
            addRect(Rect(x, y, minOf(x + a, fin), y + alto))
            x += a + alto * 0.45f
            k++
        }
    }
}

/** Un óvalo de [c] con radios [rx], [ry] como camino. */
internal fun ovaloUrss(c: Offset, rx: Float, ry: Float): Path = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }

/**
 * La media luna de los Sueños: un creciente recortado (no un círculo entero, que se leería como un ojo), de papel plateado, con su
 * sombra y su borde frío.
 */
internal fun DrawScope.mediaLunaUrss(c: Offset, r: Float, l: LuzUrss, hacia: Float) {
    val u = density
    val luna = ovaloUrss(c, r, r)
    val mordida = Path().apply { addOval(Rect(c.x - r * 0.3f, c.y - r * 1.22f, c.x + r * 1.7f, c.y + r * 0.78f)) }
    val creciente = Path().apply { op(luna, mordida, PathOperation.Difference) }
    drawPath(corridoUrss(creciente, -hacia * 1.1f * u, 1.1f * u), l.s(0.5f))
    val claro = mezcla(l.c(Color(0xFFF3EEDC)), Color(0xFFF3EEDC), 0.6f)
    val honda = mezcla(l.c(Color(0xFFB8BCB4)), Color(0xFFB8BCB4), 0.45f)
    drawPath(creciente, Brush.linearGradient(listOf(claro, honda), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)))
    drawPath(creciente, mezcla(P.CARTON, P.NEGRO, 0.3f).copy(alpha = 0.6f), style = Stroke(0.6f * u))
    drawPath(creciente, l.frio(0.45f))
}

// ---------------------------------------------------------------- las baldosas, pintadas una sola vez

/**
 * Las texturas que se repiten, pintadas una vez por densidad como pincel en mosaico: solo luz y sombra (blanco y negro con
 * transparencia) sobre el color que pone cada pieza con su luz. Así la veta de una tabla entera, o el parquet, son una operación.
 */
internal object TexturasUrss {
    private var densidad = 0f
    private var vetaH: ShaderBrush? = null
    private var vetaV: ShaderBrush? = null
    private var parquet: ShaderBrush? = null
    private var oleo: ShaderBrush? = null

    private fun revisar(u: Float) {
        if (densidad == u) return
        densidad = u; vetaH = null; vetaV = null; parquet = null; oleo = null
    }

    private fun pintar(an: Int, al: Int, dibujo: DrawScope.() -> Unit): ShaderBrush {
        val img = ImageBitmap(an.coerceAtLeast(2), al.coerceAtLeast(2))
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(an.toFloat(), al.toFloat())) { dibujo() }
        return ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated))
    }

    /**
     * La veta del roble a lo largo: líneas onduladas que se cierran en ojos alargados, los poros en rayitas y los radios del roble
     * (las motas claras), todo periódico a lo ancho y a lo alto para que el mosaico no deje costura.
     */
    fun vetaH(u: Float): ShaderBrush {
        revisar(u)
        vetaH?.let { return it }
        val an = (220f * u).roundToInt().coerceAtLeast(32)
        val al = (36f * u).roundToInt().coerceAtLeast(12)
        return pintar(an, al) { vetaRoble(an.toFloat(), al.toFloat(), u, false) }.also { vetaH = it }
    }

    /** La misma veta a lo alto (para los costados). */
    fun vetaV(u: Float): ShaderBrush {
        revisar(u)
        vetaV?.let { return it }
        val an = (36f * u).roundToInt().coerceAtLeast(12)
        val al = (220f * u).roundToInt().coerceAtLeast(32)
        return pintar(an, al) { vetaRoble(al.toFloat(), an.toFloat(), u, true) }.also { vetaV = it }
    }

    private fun DrawScope.vetaRoble(largo: Float, ancho: Float, u: Float, vertical: Boolean) {
        fun pt(a: Float, b: Float) = if (vertical) Offset(b, a) else Offset(a, b)
        val lineas = 9
        for (k in 0 until lineas) {
            val base = ancho * (k + 0.3f + 0.4f * azar(7700, k)) / lineas
            val amp = ancho * (0.02f + 0.05f * azar(7701, k))
            val ondas = 1 + (azar(7702, k) * 2.99f).toInt()
            val fase = azar(7703, k) * 6.28f
            val p = Path()
            val pasos = 40
            for (s in 0..pasos) {
                val t = s.toFloat() / pasos
                val v = base + amp * sin(fase + ondas * 2f * PI.toFloat() * t)
                val q = pt(largo * t, v)
                if (s == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
            }
            val oscura = k % 3 != 1
            drawPath(p, (if (oscura) Color.Black else Color.White).copy(alpha = if (oscura) 0.16f + 0.12f * azar(7704, k) else 0.1f),
                style = Stroke((0.5f + 0.9f * azar(7705, k)) * u))
            // Lo mismo una vuelta más abajo, si se sale, para que el mosaico case.
            if (base + amp > ancho - 2f * u) translate(if (vertical) -ancho else 0f, if (vertical) 0f else -ancho) {
                drawPath(p, Color.Black.copy(alpha = 0.16f), style = Stroke(0.8f * u))
            }
        }
        // Los poros: rayitas oscuras a lo largo.
        for (j in 0 until 70) {
            val a = largo * azar(7710, j); val b = ancho * azar(7711, j)
            val l = largo * (0.01f + 0.025f * azar(7712, j))
            drawLine(Color.Black.copy(alpha = 0.18f), pt(a, b), pt(minOf(largo, a + l), b), 0.6f * u)
        }
        // Los radios del roble: motas claras alargadas, de través.
        for (j in 0 until 16) {
            val a = largo * azar(7720, j); val b = ancho * (0.1f + 0.8f * azar(7721, j))
            val l = largo * (0.012f + 0.02f * azar(7722, j))
            drawLine(Color.White.copy(alpha = 0.16f), pt(a, b), pt(minOf(largo, a + l), b + ancho * 0.02f), (0.8f + 0.8f * azar(7723, j)) * u, StrokeCap.Round)
        }
    }

    /**
     * El parquet en espiga del Ideario, visto de pie: columnas de tablillas que suben y bajan, cada una de su tono, con sus juntas y
     * el brillo de la cera en el canto. Seis columnas y cuatro filas, para que la repetición no se note.
     */
    fun parquet(u: Float, cw: Float): ShaderBrush {
        revisar(u)
        parquet?.let { return it }
        val colA = cw.coerceAtLeast(8f)
        val ph = colA * 0.3f; val s = colA * 0.4f
        val an = (colA * 6f).roundToInt(); val al = (ph * 5f).roundToInt()
        val cwR = an / 6f; val phR = al / 5f; val sR = cwR * 0.4f
        return pintar(an, al) {
            for (j in 0 until 6) {
                val x0 = j * cwR; val x1 = x0 + cwR
                val sube = j % 2 == 0
                for (i in -2..6) {
                    val y = i * phR
                    val ya = if (sube) y + sR else y; val yb = if (sube) y else y + sR
                    val az = azar(7800 + j, Math.floorMod(i, 5) + 7)
                    // El tono de cada tablilla: más clara o más honda que el color que pone el suelo.
                    val c = if (az > 0.6f) Color.White.copy(alpha = 0.1f * (az - 0.6f) / 0.4f + 0.02f) else Color.Black.copy(alpha = 0.18f * (0.6f - az) / 0.6f + 0.02f)
                    drawPath(poligonoUrss(Offset(x0, ya), Offset(x1, yb), Offset(x1, yb + phR + 0.5f), Offset(x0, ya + phR + 0.5f)), c)
                    drawLine(Color.Black.copy(alpha = 0.4f), Offset(x0, ya + phR), Offset(x1, yb + phR), 0.8f * u)
                    drawLine(Color.White.copy(alpha = 0.12f), Offset(x0, ya + 0.9f * u), Offset(x1, yb + 0.9f * u), 0.6f * u)
                }
                drawLine(Color.Black.copy(alpha = 0.36f), Offset(x0, 0f), Offset(x0, al.toFloat()), 0.8f * u)
            }
        }.also { parquet = it }
    }

    /** Los brochazos del óleo a lo alto (la pintura de abajo de la pared), claros y hondos, con su arrastre. */
    fun oleo(u: Float): ShaderBrush {
        revisar(u)
        oleo?.let { return it }
        val an = (150f * u).roundToInt().coerceAtLeast(32)
        val al = (120f * u).roundToInt().coerceAtLeast(32)
        return pintar(an, al) {
            for (k in 0 until 46) {
                val x = an * azar(7900, k)
                val y0 = al * azar(7901, k)
                val largo = al * (0.18f + 0.4f * azar(7902, k))
                val claro = k % 3 == 0
                val col = (if (claro) Color.White else Color.Black).copy(alpha = if (claro) 0.07f else 0.09f)
                val g = (2f + 5f * azar(7903, k)) * u
                for (dx in listOf(0f, -an.toFloat(), an.toFloat())) for (dy in listOf(0f, -al.toFloat())) {
                    drawLine(col, Offset(x + dx, y0 + dy), Offset(x + dx + u * (azar(7904, k) - 0.5f) * 2f, y0 + dy + largo), g, StrokeCap.Round)
                }
            }
        }.also { oleo = it }
    }
}
