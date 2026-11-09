package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Mongol: la ger (la yurta) por dentro, con los materiales de su biblioteca, su Ideario y su salón de logros: madera pintada
 * de naranja con paneles azules, laca roja, oro en espirales (los cuernos del carnero), fieltro blanco, la celosía de la
 * pared (khana), las varas del techo (uni) y la corona abierta al Cielo Azul Eterno (toono).
 * - Social: el interior de la ger. Arriba, la corona con el cielo y las varas del techo; la faja del borde con su greca;
 *   la pared de celosía y, colgado de una vara, el shirdeg (el tapiz de fieltro acolchado) donde va tu gente. A los lados,
 *   los dos pilares pintados (bagana) y, abajo, los dos arcones pintados, la mesita baja con el cuenco de plata, el
 *   boortsog y la lámpara de mantequilla, sobre la alfombra roja.
 * - Mapas: la mesa baja pintada con un cuero tensado por tiras a sus estacas, donde va el mapa; a la izquierda, la gerege
 *   (la tablilla de oro del salvoconducto) con su cordón, la piedra de tinta y el pincel; a la derecha, el arco compuesto,
 *   dos flechas y el frasquito de rapé con tapón de coral. Al frente, el faldón con sus cajones y las patas de nube.
 * - Otras vidas: el aparador pintado, con la cresta del nudo infinito (ulzii), la tapa roja con sus cuentas, el friso con
 *   la greca del martillo, dos pilares azules con puntos de oro y cinco hornacinas forradas de fieltro con su arco lobulado.
 * - Íconos: la ger con su estupa blanca en la estepa, la gerege de oro que raspa un cuero y tres ovoos (los montones de
 *   piedras con su khadag) sobre un mapa de cuero.
 * - Notas: el panel pintado con la cresta, los pilares y un khadag azul anudado; cada nota es una lámina de corteza de
 *   abedul atada con una tira a una clavija (o, fijada, con un clavo de plata y coral y un cordón rojo).
 */
internal object VidaMongol : DisenoVida {
    private val NARANJA = Color(0xFFD9702A)
    private val NARANJA_LUZ = Color(0xFFF0984E)
    private val NARANJA_OSC = Color(0xFFA24D1A)
    private val ROJO = Color(0xFFB8322A)
    private val ROJO_LUZ = Color(0xFFD5503F)
    private val ROJO_OSC = Color(0xFF7A1E16)
    private val AZUL = Color(0xFF2B5EA6)
    private val AZUL_LUZ = Color(0xFF5A8BCB)
    private val AZUL_OSC = Color(0xFF1B3B6C)
    private val VERDE = Color(0xFF3B7A4E)
    private val ORO = Color(0xFFE2A93B)
    private val ORO_LUZ = Color(0xFFF7D884)
    private val ORO_OSC = Color(0xFF9C6A1C)
    private val FIELTRO = Color(0xFFF1EADB)
    private val FIELTRO_OSC = Color(0xFFDCD0B6)
    private val FIELTRO_SOMBRA = Color(0xFFB9AA8A)
    private val CELOSIA = Color(0xFFB4561F)
    private val CIELO = Color(0xFFBFD9F1)
    private val CIELO_OSC = Color(0xFF6E9ACB)
    private val PLATA = Color(0xFFD3D7DC)
    private val PLATA_LUZ = Color(0xFFF6F8FA)
    private val PLATA_OSC = Color(0xFF878F99)
    private val CORAL = Color(0xFFD9563F)
    private val CUERO = Color(0xFFC79A64)
    private val CUERO_OSC = Color(0xFF7A5230)
    private val TINTA_CHINA = Color(0xFF1A1A1E)
    private val PIEDRA = Color(0xFF9A9A94)
    private val PIEDRA_OSC = Color(0xFF66665F)
    private val PASTO = Color(0xFF7FA35A)
    private val LLAMA = Color(0xFFFFC870)
    private val NOCHE = Color(0xFF060B16)

    /** La corteza de abedul de las notas: blanca de día; de noche, la misma corteza en la sombra de la ger. */
    private val ABEDUL = Color(0xFFF4EFE3)
    private val ABEDUL_NOCHE = Color(0xFF3E3A34)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.14f, 0.25f, 0.86f, 0.785f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.11f, 0.82f, 0.69f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.08f, 0.27f, 0.92f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.14f, 0.925f, 0.88f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de noche: el mismo, en la penumbra de la ger. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.5f) else c

    /** El oro y la plata de noche: guardan más luz que la madera pintada. */
    private fun nm(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.3f) else c

    /** El fieltro de las paredes de noche: más hundido en la sombra que los muebles. */
    private fun nf(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.7f) else c

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mez(e.fondo, mez(material, NOCHE, 0.8f), 0.4f) else mez(e.fondo, material, 0.35f)

    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)
    private fun DrawScope.rect(r: Rect, b: Brush) = drawRect(b, r.topLeft, r.size)
    private fun vert(r: Rect, c: List<Color>) = Brush.verticalGradient(c, r.top, r.bottom)
    private fun vert(r: Rect, a: Color, b: Color) = vert(r, listOf(a, b))
    private fun vert(r: Rect, a: Color, b: Color, c: Color) = vert(r, listOf(a, b, c))
    private fun horiz(r: Rect, c: List<Color>) = Brush.horizontalGradient(c, r.left, r.right)
    private fun horiz(r: Rect, a: Color, b: Color, c: Color) = horiz(r, listOf(a, b, c))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color, d: Color) = horiz(r, listOf(a, b, c, d))

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Una mancha radial suave (la luz de la lámpara, la sombra del fieltro). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** La sombra que una pieza deja debajo (un óvalo difuso). */
    private fun DrawScope.sombraBajo(x0: Float, x1: Float, y: Float, alto: Float, fuerza: Float = 0.35f) {
        val c = Offset((x0 + x1) / 2, y)
        drawOval(Brush.radialGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), c, (x1 - x0) / 2), Offset(x0, y - alto / 2), Size(x1 - x0, alto))
    }

    /** Las pinceladas de la pintura sobre la madera: unas pocas líneas largas y muy tenues. */
    private fun DrawScope.pinceladas(r: Rect, color: Color, semilla: Int, p: Float, vertical: Boolean = false) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        val n = (ancho / (4f * p)).toInt().coerceIn(2, 30)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val a0 = largo * azar(semilla, k + 50) * 0.4f
                val a1 = largo * (0.6f + 0.4f * azar(semilla, k + 90))
                val s = if (vertical) Offset(r.left + base, r.top + a0) else Offset(r.left + a0, r.top + base)
                val f = if (vertical) Offset(r.left + base + p * 0.6f, r.top + a1) else Offset(r.left + a1, r.top + base + p * 0.6f)
                drawLine(color.copy(alpha = color.alpha * (0.4f + 0.6f * azar(semilla, k + 130))), s, f, p * (0.4f + 0.5f * azar(semilla, k + 170)))
            }
        }
    }

    /** La greca del martillo (alkhan hee): tes que se alternan arriba y abajo, recortada a [r]. */
    private fun DrawScope.martillo(r: Rect, color: Color, g: Float) {
        val a = r.height
        if (a <= 0f || r.width <= 0f) return
        val path = Path()
        var x = r.left - a * 0.2f
        var k = 0
        while (x < r.right && k < 400) {
            val arriba = r.top + g / 2; val abajo = r.bottom - g / 2
            path.moveTo(x, arriba); path.lineTo(x + a * 0.9f, arriba)
            path.moveTo(x + a * 0.45f, arriba); path.lineTo(x + a * 0.45f, abajo - a * 0.26f)
            path.moveTo(x + a * 0.6f, abajo); path.lineTo(x + a * 1.5f, abajo)
            path.moveTo(x + a * 1.05f, abajo); path.lineTo(x + a * 1.05f, arriba + a * 0.26f)
            x += a * 1.2f; k++
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(path, color, style = Stroke(g)) }
    }

    /** Una espiral de [vueltas] que empieza en el ángulo [ang0] a radio [r] y se enrosca hacia [c] en [sentido] (±1). */
    private fun espiral(c: Offset, r: Float, vueltas: Float, sentido: Float, ang0: Float): Path {
        val path = Path()
        val pasos = (vueltas * 26).toInt().coerceAtLeast(10)
        for (k in 0..pasos) {
            val t = k / pasos.toFloat()
            val a = ang0 + sentido * t * vueltas * 6.2832f
            val rr = r * (1f - 0.82f * t)
            val x = c.x + rr * cos(a); val y = c.y + rr * sin(a)
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        return path
    }

    /** Los cuernos del carnero (ugalz): dos espirales enfrentadas que nacen de un arco, de ancho 2·[r]. */
    private fun DrawScope.cuernos(c: Offset, r: Float, color: Color, g: Float, abajo: Boolean = false) {
        val s = if (abajo) -1f else 1f
        val rr = r * 0.5f
        val izq = Offset(c.x - r * 0.5f, c.y)
        val der = Offset(c.x + r * 0.5f, c.y)
        drawPath(espiral(izq, rr, 1.45f, s, Math.PI.toFloat()), color, style = Stroke(g, cap = StrokeCap.Round))
        drawPath(espiral(der, rr, 1.45f, -s, 0f), color, style = Stroke(g, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(c.x - r, c.y); quadraticTo(c.x, c.y + s * r * 0.95f, c.x + r, c.y) }, color, style = Stroke(g, cap = StrokeCap.Round))
        drawCircle(color, g * 0.9f, Offset(c.x, c.y + s * r * 0.48f))
    }

    /** El nudo infinito (ulzii): una reja de cuatro hilos en cada sentido, girada, con sus lazos; con un contorno oscuro. */
    private fun DrawScope.ulzii(c: Offset, r: Float, color: Color, contorno: Color, g: Float) {
        val s = r * 1.05f
        val path = Path()
        val t = listOf(-0.5f, -0.1667f, 0.1667f, 0.5f)
        for (v in t) {
            path.moveTo(c.x - s / 2, c.y + v * s); path.lineTo(c.x + s / 2, c.y + v * s)
            path.moveTo(c.x + v * s, c.y - s / 2); path.lineTo(c.x + v * s, c.y + s / 2)
        }
        // Los lazos de cada lado: unen los hilos 1-2 y 3-4 con medio círculo hacia afuera.
        val lr = s / 6f
        for (par in listOf(-0.3333f, 0.3333f)) {
            val m = c.y + par * s
            path.addOval(Rect(c.x - s / 2 - lr, m - lr, c.x - s / 2 + lr, m + lr))
            path.addOval(Rect(c.x + s / 2 - lr, m - lr, c.x + s / 2 + lr, m + lr))
            val mx = c.x + par * s
            path.addOval(Rect(mx - lr, c.y - s / 2 - lr, mx + lr, c.y - s / 2 + lr))
            path.addOval(Rect(mx - lr, c.y + s / 2 - lr, mx + lr, c.y + s / 2 + lr))
        }
        rotate(45f, c) {
            drawPath(path, contorno, style = Stroke(g * 1.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(path, color, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    /** La celosía de la pared (khana): varillas cruzadas en diagonal con sus nudos de cuero, recortada a [r]. */
    private fun DrawScope.celosia(r: Rect, color: Color, g: Float, paso: Float, oscuro: Boolean) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            val alto = r.height
            var x = r.left - alto
            val sombra = Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f)
            val luz = mez(color, Color.White, 0.25f)
            while (x < r.right + alto) {
                drawLine(sombra, Offset(x + g * 0.7f, r.top + g * 0.7f), Offset(x + alto + g * 0.7f, r.bottom + g * 0.7f), g)
                drawLine(sombra, Offset(x + alto + g * 0.7f, r.top + g * 0.7f), Offset(x + g * 0.7f, r.bottom + g * 0.7f), g)
                x += paso
            }
            x = r.left - alto
            while (x < r.right + alto) {
                drawLine(color, Offset(x, r.top), Offset(x + alto, r.bottom), g)
                drawLine(luz.copy(alpha = 0.5f), Offset(x - g * 0.25f, r.top), Offset(x + alto - g * 0.25f, r.bottom), g * 0.3f)
                drawLine(color, Offset(x + alto, r.top), Offset(x, r.bottom), g)
                x += paso
            }
            // Los nudos de cuero en los cruces.
            val nudo = mez(color, Color.Black, 0.45f)
            var fila = 0
            var y = r.top
            while (y < r.bottom + paso && fila < 200) {
                var cx = r.left + (if (fila % 2 == 0) 0f else paso / 2f)
                while (cx < r.right + paso) {
                    drawCircle(nudo, g * 0.5f, Offset(cx, y))
                    cx += paso
                }
                y += paso / 2f; fila++
            }
        }
    }

    /** La llama de la lámpara de mantequilla, con su halo. */
    private fun DrawScope.llama(o: Offset, r: Float, oscuro: Boolean) {
        mancha(o, r * (if (oscuro) 9f else 3f), LLAMA.copy(alpha = if (oscuro) 0.42f else 0.22f))
        drawPath(Path().apply {
            moveTo(o.x, o.y - r * 1.7f); quadraticTo(o.x + r * 0.9f, o.y - r * 0.2f, o.x, o.y + r * 0.5f); quadraticTo(o.x - r * 0.9f, o.y - r * 0.2f, o.x, o.y - r * 1.7f); close()
        }, Color(0xFFFFA733))
        drawOval(Color(0xFFFFF1C2), Offset(o.x - r * 0.3f, o.y - r * 0.6f), Size(r * 0.6f, r * 0.95f))
    }

    /** Una lámpara de mantequilla de latón, de pie sobre [base], encendida, de radio [r]. */
    private fun DrawScope.lamparaMantequilla(base: Offset, r: Float, o: Boolean) {
        val x = base.x; val y = base.y
        val pie = Path().apply { moveTo(x - r * 1.2f, y); lineTo(x + r * 1.2f, y); lineTo(x + r * 0.35f, y - r * 1.1f); lineTo(x - r * 0.35f, y - r * 1.1f); close() }
        drawPath(pie, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(ORO, o)), x - r * 1.2f, x + r * 1.2f))
        val copa = Path().apply { moveTo(x - r * 1.4f, y - r * 2.6f); quadraticTo(x - r * 1.2f, y - r * 1f, x, y - r * 1f); quadraticTo(x + r * 1.2f, y - r * 1f, x + r * 1.4f, y - r * 2.6f); close() }
        drawPath(copa, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), x - r * 1.4f, x + r * 1.4f))
        drawOval(nm(Color(0xFFF2D58A), o), Offset(x - r * 1.4f, y - r * 2.95f), Size(r * 2.8f, r * 0.7f))
        llama(Offset(x, y - r * 3.6f), r * 0.75f, o)
    }

    /** Un clavo de plata redondo, con su brillo. */
    private fun DrawScope.clavo(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.3f, c.y + r * 0.4f))
        drawCircle(Brush.radialGradient(listOf(nm(PLATA_LUZ, oscuro), nm(PLATA, oscuro), nm(PLATA_OSC, oscuro)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.4f), r, c)
    }

    /** Una tabla pintada de naranja con su luz arriba y sombra abajo. */
    private fun DrawScope.tablaNaranja(r: Rect, o: Boolean, semilla: Int, p: Float) {
        rect(r, vert(r, n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)))
        pinceladas(r, n(NARANJA_OSC, o).copy(alpha = 0.35f), semilla, p)
    }

    /** La tapa roja de los muebles pintados: laca roja con una hilera de cuentas de oro, verdes y azules. */
    private fun DrawScope.tapaRoja(r: Rect, o: Boolean, p: Float) {
        rect(r, vert(r, n(ROJO_LUZ, o), n(ROJO, o), n(ROJO_OSC, o)))
        drawLine(nm(ORO, o), Offset(r.left, r.top + r.height * 0.18f), Offset(r.right, r.top + r.height * 0.18f), p * 0.6f)
        drawLine(nm(ORO, o), Offset(r.left, r.bottom - r.height * 0.18f), Offset(r.right, r.bottom - r.height * 0.18f), p * 0.6f)
        val paso = r.height * 2.4f
        var x = r.left + paso / 2
        var k = 0
        val cuentas = listOf(n(VERDE, o), nm(ORO, o), n(AZUL_OSC, o), nm(ORO, o))
        while (x < r.right) {
            val c = Offset(x, r.center.y)
            drawCircle(cuentas[k % 4], r.height * 0.2f, c)
            drawCircle(Color.White.copy(alpha = 0.35f), r.height * 0.07f, Offset(c.x - r.height * 0.06f, c.y - r.height * 0.06f))
            x += paso; k++
        }
    }

    /** Un panel azul con borde de oro y una columna de puntos de oro (los pilares de los muebles pintados). */
    private fun DrawScope.panelAzul(r: Rect, o: Boolean, p: Float, puntos: Boolean = true) {
        rect(r, horiz(r, n(AZUL_OSC, o), n(AZUL, o), n(AZUL_LUZ, o), n(AZUL_OSC, o)))
        drawRect(nm(ORO, o), r.topLeft, r.size, style = Stroke(p * 0.7f))
        if (puntos && r.width > 0f) {
            val paso = r.width * 1.25f
            var y = r.top + paso * 0.7f
            while (y < r.bottom - paso * 0.4f) {
                drawCircle(nm(ORO_LUZ, o), r.width * 0.14f, Offset(r.center.x, y))
                y += paso
            }
        }
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.SOCIAL)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val techo = h * 0.14f
        val faja = techo + 2.6f * u
        val suelo = h * 0.955f

        // El techo por dentro: el fieltro entre las varas, más oscuro hacia la corona.
        val rt = Rect(0f, 0f, w, techo)
        rect(rt, vert(rt, nf(FIELTRO_SOMBRA, o), nf(FIELTRO, o)))
        // Las varas del techo (uni), pintadas de naranja con la punta azul junto a la corona.
        val tc = Offset(w / 2, h * 0.025f)
        val rc = h * 0.11f
        val nVaras = (w / (8f * u)).toInt().coerceIn(14, 30)
        for (k in 0..nVaras) {
            val t = k / nVaras.toFloat()
            val fin = Offset(-w * 0.1f + t * w * 1.2f, techo + u)
            val dx = fin.x - tc.x; val dy = fin.y - tc.y
            val l = sqrt(dx * dx + dy * dy)
            val dir = Offset(dx / l, dy / l)
            val ini = Offset(tc.x + dir.x * rc, tc.y + dir.y * rc)
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(ini.x + p, ini.y + p * 1.8f), Offset(fin.x + p, fin.y + p * 1.8f), 1.2f * u)
            drawLine(n(NARANJA, o), ini, fin, 1.05f * u, StrokeCap.Round)
            drawLine(n(NARANJA_LUZ, o).copy(alpha = 0.7f), Offset(ini.x - p * 0.4f, ini.y), Offset(fin.x - p * 0.4f, fin.y), 0.35f * u)
            val azulFin = Offset(ini.x + dir.x * rc * 0.32f, ini.y + dir.y * rc * 0.32f)
            drawLine(n(AZUL, o), ini, azulFin, 1.4f * u)
            drawLine(nm(ORO, o), azulFin, Offset(azulFin.x + dir.x * 0.6f * u, azulFin.y + dir.y * 0.6f * u), 1.4f * u)
        }
        toono(tc, rc, o, p, u)

        // La faja donde el techo se apoya en la pared: naranja con la greca del martillo en azul.
        val fa = Rect(0f, techo, w, faja)
        rect(fa, vert(fa, n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)))
        martillo(Rect(0f, fa.top + fa.height * 0.18f, w, fa.bottom - fa.height * 0.18f), n(AZUL, o), fa.height * 0.13f)

        // La pared: fieltro blanco tras la celosía de madera pintada.
        val pared = Rect(0f, faja, w, suelo)
        rect(pared, vert(pared, nf(FIELTRO_OSC, o), nf(FIELTRO, o), nf(FIELTRO_OSC, o)))
        celosia(pared, nf(mez(CELOSIA, NARANJA, 0.3f), o), 0.85f * u, 10f * u, o)
        rect(Rect(0f, faja, w, faja + 2.5f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), faja, faja + 2.5f * u))

        // La alfombra roja del piso, con su pespunte blanco.
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(ROJO_OSC, o), n(ROJO, o)))
        drawLine(n(FIELTRO, o).copy(alpha = 0.8f), Offset(0f, suelo + (h - suelo) * 0.45f), Offset(w, suelo + (h - suelo) * 0.45f), p * 0.6f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 0.9f * u)))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, suelo), Offset(w, suelo), p)

        // La lámpara de mantequilla (su halo cae sobre el tapiz, que se pinta después: el halo queda en la pared).
        val yMesa = h * 0.875f
        val lampara = Offset(w * 0.565f, yMesa - 3.6f * u)
        if (o) mancha(lampara, 40f * u, LLAMA.copy(alpha = 0.16f))

        // El shirdeg colgado de su vara: borde rojo con pespunte, filete azul y el fieltro acolchado (el nicho).
        val marco = ni.inflate(3.4f * u)
        val vara = marco.top - 1.4f * u
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(marco.left + 0.9f * u, marco.top + 1.3f * u), marco.size)
        // Las tiras de cuero de la vara a la celosía.
        listOf(0.18f, 0.5f, 0.82f).forEach { f ->
            val x = marco.left + marco.width * f
            drawLine(n(CUERO_OSC, o), Offset(x, vara), Offset(x - 0.8f * u, faja + 0.4f * u), p * 0.9f)
            drawLine(n(CUERO_OSC, o), Offset(x, vara), Offset(x + 0.8f * u, faja + 0.4f * u), p * 0.9f)
        }
        rect(marco, vert(marco, n(ROJO_LUZ, o), n(ROJO, o), n(ROJO_OSC, o)))
        drawRect(n(FIELTRO, o).copy(alpha = 0.85f), Offset(marco.left + 1.1f * u, marco.top + 1.1f * u), Size(marco.width - 2.2f * u, marco.height - 2.2f * u),
            style = Stroke(p * 0.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 0.8f * u))))
        // Los cuernos de oro en las esquinas y el nudo infinito en medio de arriba y de abajo.
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c ->
            val dx = if (c.x < w / 2) 1f else -1f; val dy = if (c.y < h / 2) 1f else -1f
            drawCircle(nm(ORO, o), 1.0f * u, Offset(c.x + dx * 1.7f * u, c.y + dy * 1.7f * u))
            drawCircle(n(ROJO_OSC, o), 0.45f * u, Offset(c.x + dx * 1.7f * u, c.y + dy * 1.7f * u))
        }
        listOf(marco.top + 1.7f * u, marco.bottom - 1.7f * u).forEach { y ->
            listOf(0.3f, 0.7f).forEach { f -> cuernos(Offset(marco.left + marco.width * f, y + (if (y < h / 2) -0.3f else 0.3f) * u), 2.2f * u, nm(ORO, o), p * 0.7f, abajo = y > h / 2) }
            ulzii(Offset(marco.center.x, y), 1.4f * u, nm(ORO_LUZ, o), n(ROJO_OSC, o), p * 0.55f)
        }
        rect(ni.inflate(1.2f * u), n(AZUL, o))
        rect(ni.inflate(0.45f * u), nm(ORO, o))
        rect(ni, nicho(e, o, FIELTRO))
        // El acolchado: un pespunte que recorre el borde del fieltro, muy tenue.
        drawRect((if (o) Color.White else FIELTRO_SOMBRA).copy(alpha = if (o) 0.1f else 0.45f), Offset(ni.left + 1.8f * u, ni.top + 1.8f * u), Size(ni.width - 3.6f * u, ni.height - 3.6f * u),
            style = Stroke(p * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * u, 0.8f * u))))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 2.6f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.top, ni.top + 2.6f * u))
        // La vara de madera con sus remates de oro.
        val rv = Rect(marco.left - 2.2f * u, vara - 0.8f * u, marco.right + 2.2f * u, vara + 0.8f * u)
        drawRoundRect(vert(rv, n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)), rv.topLeft, rv.size, CornerRadius(0.8f * u))
        listOf(rv.left, rv.right).forEach { x -> drawCircle(Brush.radialGradient(listOf(nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), Offset(x - 0.3f * u, vara - 0.3f * u), 1.6f * u), 1.25f * u, Offset(x, vara)) }

        // Los dos pilares pintados (bagana).
        val cw = minOf(w * 0.045f, 7f * u)
        listOf(w * 0.055f, w * 0.945f).forEach { bagana(it, faja, suelo, cw, o, p, u, e.semilla) }

        // Los dos arcones pintados, a los lados de la mesita.
        val yArcon = h * 0.83f
        arcon(Rect(w * 0.13f, yArcon, w * 0.37f, suelo), o, p, u, e.semilla + 1)
        arcon(Rect(w * 0.63f, yArcon, w * 0.87f, suelo), o, p, u, e.semilla + 2)

        // La mesita baja con el cuenco de plata, el boortsog y la lámpara.
        mesita(w * 0.5f, yMesa, suelo, w * 0.2f, o, p, u, lampara, e)
    }

    /** La corona de la ger (toono): el cielo, los arcos que la cruzan y el aro pintado. */
    private fun DrawScope.toono(c: Offset, r: Float, o: Boolean, p: Float, u: Float) {
        val ri = r * 0.78f
        val cielo = Rect(c.x - ri, c.y - ri, c.x + ri, c.y + ri)
        drawCircle(vert(cielo, if (o) Color(0xFF0B1A33) else n(CIELO_OSC, o), if (o) Color(0xFF1C3560) else CIELO), ri, c)
        if (o) {
            val rnd = Random(77)
            repeat(14) {
                val ang = rnd.nextFloat() * 3.1416f; val d = ri * (0.2f + 0.75f * rnd.nextFloat())
                drawCircle(Color(0xFFFFF4D0).copy(alpha = 0.5f + 0.5f * rnd.nextFloat()), (0.15f + 0.25f * rnd.nextFloat()) * u, Offset(c.x + d * cos(ang), c.y + d * sin(ang)))
            }
        }
        // Los arcos curvos que cruzan la corona.
        clipPath(Path().apply { addOval(cielo) }) {
            listOf(0.42f, 0.62f, 0.8f).forEach { f ->
                val y = c.y + ri * f
                drawPath(Path().apply { moveTo(c.x - ri, y + ri * 0.05f); quadraticTo(c.x, y - ri * 0.1f, c.x + ri, y + ri * 0.05f) }, n(NARANJA_OSC, o), style = Stroke(1.2f * u))
                drawPath(Path().apply { moveTo(c.x - ri, y - p * 0.6f + ri * 0.05f); quadraticTo(c.x, y - p * 0.6f - ri * 0.1f, c.x + ri, y - p * 0.6f + ri * 0.05f) }, n(NARANJA_LUZ, o), style = Stroke(0.4f * u))
            }
        }
        // El aro pintado, con su borde rojo y los agujeros de las varas.
        val g = r - ri
        drawCircle(Brush.radialGradient(listOf(n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)), c, r), ri + g / 2, c, style = Stroke(g))
        drawCircle(n(ROJO_OSC, o), r, c, style = Stroke(p * 1.2f))
        drawCircle(n(ROJO, o), ri, c, style = Stroke(p * 1.4f))
        for (k in 0 until 18) {
            val ang = 3.1416f * (k + 0.5f) / 18f
            drawCircle(n(AZUL_OSC, o), 0.4f * u, Offset(c.x + (ri + g / 2) * cos(ang), c.y + (ri + g / 2) * sin(ang)))
        }
    }

    /** Un pilar pintado (bagana): capitel de ménsula roja con cuernos, fuste naranja con panel azul y basa. */
    private fun DrawScope.bagana(xc: Float, top: Float, bottom: Float, cw: Float, o: Boolean, p: Float, u: Float, semilla: Int) {
        val capH = cw * 1.0f; val baseH = cw * 0.8f
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), xc + cw / 2, xc + cw * 1.1f), Offset(xc + cw / 2, top), Size(cw * 0.6f, bottom - top))
        val f = Rect(xc - cw / 2, top + capH, xc + cw / 2, bottom - baseH)
        rect(f, horiz(f, n(NARANJA_OSC, o), n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)))
        pinceladas(f, n(NARANJA_OSC, o).copy(alpha = 0.3f), semilla, p, vertical = true)
        panelAzul(Rect(xc - cw * 0.26f, f.top + cw * 0.5f, xc + cw * 0.26f, f.bottom - cw * 0.5f), o, p)
        listOf(f.top + cw * 0.22f, f.bottom - cw * 0.22f).forEach { y -> drawLine(nm(ORO, o), Offset(f.left, y), Offset(f.right, y), p * 0.9f) }
        // La ménsula: un bloque rojo ancho que se curva hacia el fuste, con dos cuernos de oro.
        val m = Path().apply {
            moveTo(xc - cw * 1.15f, top); lineTo(xc + cw * 1.15f, top); lineTo(xc + cw * 1.15f, top + capH * 0.38f)
            quadraticTo(xc + cw * 0.6f, top + capH * 0.42f, xc + cw * 0.5f, top + capH); lineTo(xc - cw * 0.5f, top + capH)
            quadraticTo(xc - cw * 0.6f, top + capH * 0.42f, xc - cw * 1.15f, top + capH * 0.38f); close()
        }
        drawPath(m, vert(Rect(0f, top, 0f, top + capH), n(ROJO_LUZ, o), n(ROJO_OSC, o)))
        drawPath(m, nm(ORO_OSC, o), style = Stroke(p * 0.6f))
        cuernos(Offset(xc, top + capH * 0.3f), cw * 0.62f, nm(ORO, o), p * 0.6f)
        // La basa.
        val b = Rect(xc - cw * 0.7f, bottom - baseH, xc + cw * 0.7f, bottom)
        rect(b, vert(b, n(NARANJA, o), n(NARANJA_OSC, o)))
        drawLine(nm(ORO, o), Offset(b.left, b.top + baseH * 0.3f), Offset(b.right, b.top + baseH * 0.3f), p * 0.8f)
        drawLine(n(NARANJA_LUZ, o), Offset(b.left, b.top), Offset(b.right, b.top), p * 0.6f)
    }

    /** Un arcón pintado (avdar): tapa roja con cuentas, frente naranja con panel azul, cuernos y cerradura de plata, patas. */
    private fun DrawScope.arcon(r: Rect, o: Boolean, p: Float, u: Float, semilla: Int) {
        val tapaH = r.height * 0.2f; val pataH = r.height * 0.12f
        sombraBajo(r.left - u, r.right + u, r.bottom, 1.6f * u, 0.45f)
        val tapa = Rect(r.left - 0.6f * u, r.top, r.right + 0.6f * u, r.top + tapaH)
        val cuerpo = Rect(r.left, tapa.bottom, r.right, r.bottom - pataH)
        tablaNaranja(cuerpo, o, semilla, p)
        val panel = cuerpo.deflate(minOf(1.3f * u, cuerpo.height * 0.14f))
        drawRect(n(AZUL, o), panel.topLeft, panel.size, style = Stroke(minOf(1.1f * u, panel.height * 0.16f)))
        drawRect(nm(ORO, o), panel.deflate(0.8f * u).topLeft, panel.deflate(0.8f * u).size, style = Stroke(p * 0.45f))
        val ch = panel.height * 0.24f
        listOf(panel.left + ch * 1.5f, panel.right - ch * 1.5f).forEach { x -> cuernos(Offset(x, panel.center.y - ch * 0.2f), ch, nm(ORO, o), p * 0.6f) }
        // La cerradura de plata en medio.
        val ce = Rect(panel.center.x - ch * 0.8f, panel.center.y - ch * 0.9f, panel.center.x + ch * 0.8f, panel.center.y + ch * 0.9f)
        drawRoundRect(Brush.linearGradient(listOf(nm(PLATA_LUZ, o), nm(PLATA, o), nm(PLATA_OSC, o)), ce.topLeft, ce.bottomRight), ce.topLeft, ce.size, CornerRadius(ch * 0.25f))
        drawCircle(n(TINTA_CHINA, o), ch * 0.18f, Offset(ce.center.x, ce.center.y - ch * 0.12f))
        drawLine(n(TINTA_CHINA, o), ce.center, Offset(ce.center.x, ce.center.y + ch * 0.4f), ch * 0.14f)
        tapaRoja(tapa, o, p)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, 0.6f * u))
        // Las patas.
        listOf(r.left + r.width * 0.06f, r.right - r.width * 0.06f - r.width * 0.08f).forEach { x ->
            val pr = Rect(x, cuerpo.bottom, x + r.width * 0.08f, r.bottom)
            rect(pr, vert(pr, n(NARANJA_OSC, o), mez(n(NARANJA_OSC, o), Color.Black, 0.3f)))
        }
    }

    /** La mesita baja (shiree) con el cuenco de té de plata, el plato de boortsog y la lámpara de mantequilla. */
    private fun DrawScope.mesita(cx: Float, yT: Float, yF: Float, ancho: Float, o: Boolean, p: Float, u: Float, lampara: Offset, e: EstadoMueble) {
        val x0 = cx - ancho / 2; val x1 = cx + ancho / 2
        sombraBajo(x0 - u, x1 + u, yF, 1.6f * u, 0.45f)
        // Patas con su corte de nube.
        val pataW = ancho * 0.09f
        listOf(x0 + ancho * 0.04f, x1 - ancho * 0.04f - pataW).forEach { x ->
            val pr = Rect(x, yT, x + pataW, yF)
            rect(pr, horiz(pr, n(NARANJA_OSC, o), n(NARANJA, o), n(NARANJA_OSC, o)))
        }
        // El faldón con el panel azul y la nube de oro.
        val fal = Rect(x0 + ancho * 0.03f, yT + 1.6f * u, x1 - ancho * 0.03f, yT + (yF - yT) * 0.62f)
        val nube = Path().apply {
            moveTo(fal.left, fal.top); lineTo(fal.right, fal.top); lineTo(fal.right, fal.bottom)
            quadraticTo(fal.right - fal.width * 0.12f, fal.bottom - fal.height * 0.5f, fal.right - fal.width * 0.22f, fal.bottom - fal.height * 0.15f)
            quadraticTo(fal.center.x, fal.bottom - fal.height * 0.55f, fal.left + fal.width * 0.22f, fal.bottom - fal.height * 0.15f)
            quadraticTo(fal.left + fal.width * 0.12f, fal.bottom - fal.height * 0.5f, fal.left, fal.bottom); close()
        }
        drawPath(nube, vert(fal, n(NARANJA, o), n(NARANJA_OSC, o)))
        val pa = Rect(fal.left + fal.width * 0.3f, fal.top + fal.height * 0.12f, fal.right - fal.width * 0.3f, fal.top + fal.height * 0.5f)
        drawRect(n(AZUL, o), pa.topLeft, pa.size)
        cuernos(Offset(pa.center.x, pa.center.y - pa.height * 0.1f), pa.height * 0.55f, nm(ORO, o), p * 0.5f)
        // El tablero.
        val tab = Rect(x0, yT, x1, yT + 1.8f * u)
        rect(tab, vert(tab, n(ROJO_LUZ, o), n(ROJO, o), n(ROJO_OSC, o)))
        drawLine(nm(ORO, o), Offset(x0, yT + 0.5f * u), Offset(x1, yT + 0.5f * u), p * 0.5f)
        drawLine(nm(ORO_LUZ, o), Offset(x0, yT), Offset(x1, yT), p * 0.5f)
        // El cuenco de té con leche (aiyaga) de plata en su platillo.
        val bx = cx - ancho * 0.28f
        val bw = ancho * 0.24f
        drawOval(nm(PLATA_OSC, o), Offset(bx - bw * 0.6f, yT - 0.7f * u), Size(bw * 1.2f, 1.2f * u))
        val cuenco = Path().apply { moveTo(bx - bw / 2, yT - 3.6f * u); quadraticTo(bx - bw * 0.48f, yT - 0.6f * u, bx, yT - 0.6f * u); quadraticTo(bx + bw * 0.48f, yT - 0.6f * u, bx + bw / 2, yT - 3.6f * u); close() }
        drawPath(cuenco, Brush.horizontalGradient(listOf(nm(PLATA_OSC, o), nm(PLATA_LUZ, o), nm(PLATA, o), nm(PLATA_OSC, o)), bx - bw / 2, bx + bw / 2))
        drawLine(nm(ORO, o), Offset(bx - bw * 0.45f, yT - 2.7f * u), Offset(bx + bw * 0.45f, yT - 2.7f * u), p * 0.6f)
        drawOval(n(Color(0xFFE9DCC0), o), Offset(bx - bw / 2, yT - 4.1f * u), Size(bw, 1f * u))
        // El plato de boortsog: rombos de masa frita dorada.
        val px = cx + ancho * 0.02f
        drawOval(n(AZUL_OSC, o), Offset(px - ancho * 0.12f, yT - 0.8f * u), Size(ancho * 0.24f, 1.3f * u))
        drawOval(n(FIELTRO, o), Offset(px - ancho * 0.1f, yT - 0.7f * u), Size(ancho * 0.2f, 0.9f * u))
        val masa = n(Color(0xFFD99A4E), o); val masaOsc = n(Color(0xFF9C6428), o)
        listOf(-0.06f to 0f, 0.0f to -0.6f, 0.06f to 0f, -0.02f to -1.3f, 0.035f to -1.1f).forEach { (dx, dy) ->
            val c = Offset(px + ancho * dx, yT - 1.1f * u + dy * u)
            drawPath(Path().apply { moveTo(c.x - 1.3f * u, c.y); lineTo(c.x, c.y - 0.7f * u); lineTo(c.x + 1.3f * u, c.y); lineTo(c.x, c.y + 0.7f * u); close() }, masa)
            drawLine(masaOsc, Offset(c.x - 0.6f * u, c.y), Offset(c.x + 0.6f * u, c.y), p * 0.5f)
        }
        // La lámpara de mantequilla de latón, encendida.
        val lx = lampara.x
        val base = Path().apply { moveTo(lx - 1.4f * u, yT); lineTo(lx + 1.4f * u, yT); lineTo(lx + 0.4f * u, yT - 1.2f * u); lineTo(lx - 0.4f * u, yT - 1.2f * u); close() }
        drawPath(base, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(ORO, o)), lx - 1.4f * u, lx + 1.4f * u))
        val copa = Path().apply { moveTo(lx - 1.6f * u, yT - 3f * u); quadraticTo(lx - 1.4f * u, yT - 1.1f * u, lx, yT - 1.1f * u); quadraticTo(lx + 1.4f * u, yT - 1.1f * u, lx + 1.6f * u, yT - 3f * u); close() }
        drawPath(copa, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), lx - 1.6f * u, lx + 1.6f * u))
        drawOval(n(Color(0xFFF2D58A), o), Offset(lx - 1.6f * u, yT - 3.4f * u), Size(3.2f * u, 0.8f * u))
        llama(Offset(lampara.x, yT - 4.2f * u), 0.9f * u, o)
        // El khadag doblado sobre el borde, en el color secundario.
        val kx = x1 - ancho * 0.12f
        drawPath(Path().apply { moveTo(kx - 1.2f * u, yT); lineTo(kx + 1.2f * u, yT); lineTo(kx + 1.6f * u, yT + 4f * u); lineTo(kx - 0.4f * u, yT + 4.5f * u); close() },
            vert(Rect(0f, yT, 0f, yT + 4.5f * u), n(mez(e.secundario, Color.White, 0.15f), o), n(mez(e.secundario, Color.Black, 0.2f), o)))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.MAPAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val suelo = h * 0.965f

        // Detrás, la pared de fieltro con su celosía y, abajo, la alfombra.
        val pared = Rect(0f, 0f, w, suelo)
        rect(pared, vert(pared, nf(FIELTRO_OSC, o), nf(FIELTRO, o)))
        celosia(pared, nf(mez(CELOSIA, NARANJA, 0.3f), o), 0.85f * u, 10f * u, o)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(ROJO_OSC, o), n(ROJO, o)))
        if (o) mancha(Offset(w * 0.9f, h * 0.2f), 30f * u, LLAMA.copy(alpha = 0.1f))

        // El tablero visto desde arriba: canto rojo, superficie naranja y la cenefa azul con puntos de oro.
        val tab = Rect(w * 0.015f, h * 0.03f, w * 0.985f, h * 0.775f)
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(tab.left + 0.8f * u, tab.top + 1.4f * u), tab.size)
        rect(tab, vert(tab, n(ROJO_LUZ, o), n(ROJO, o), n(ROJO_OSC, o)))
        val sup = tab.deflate(1.4f * u)
        rect(sup, Brush.linearGradient(listOf(n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)), sup.topLeft, sup.bottomRight))
        pinceladas(sup, n(NARANJA_OSC, o).copy(alpha = 0.28f), e.semilla, p)
        val cen = sup.deflate(1.6f * u)
        drawRect(n(AZUL, o), cen.topLeft, cen.size, style = Stroke(1.5f * u))
        drawRect(nm(ORO, o), cen.deflate(1.2f * u).topLeft, cen.deflate(1.2f * u).size, style = Stroke(p * 0.5f))
        var xp = cen.left + 3f * u
        while (xp < cen.right - 2f * u) {
            drawCircle(nm(ORO_LUZ, o), 0.32f * u, Offset(xp, cen.top)); drawCircle(nm(ORO_LUZ, o), 0.32f * u, Offset(xp, cen.bottom)); xp += 3f * u
        }
        var yp = cen.top + 3f * u
        while (yp < cen.bottom - 2f * u) {
            drawCircle(nm(ORO_LUZ, o), 0.32f * u, Offset(cen.left, yp)); drawCircle(nm(ORO_LUZ, o), 0.32f * u, Offset(cen.right, yp)); yp += 3f * u
        }
        // Los cuernos de oro en las cuatro esquinas de la cenefa.
        listOf(cen.topLeft, Offset(cen.right, cen.top), Offset(cen.left, cen.bottom), cen.bottomRight).forEach { c ->
            val dx = if (c.x < w / 2) 1f else -1f; val dy = if (c.y < h / 2) 1f else -1f
            drawCircle(nm(ORO, o), 1.1f * u, Offset(c.x + dx * 0.0f, c.y + dy * 0.0f))
            drawCircle(n(ROJO_OSC, o), 0.5f * u, c)
        }

        // El cuero tensado: su contorno con lóbulos y las tiras a las estacas; dentro, el nicho del mapa.
        val cu = ni.inflate(2.6f * u)
        val lob = 2.4f * u
        val piel = Path().apply {
            moveTo(cu.left - lob, cu.top - lob * 0.6f)
            quadraticTo(cu.center.x * 0.5f + cu.left * 0.5f, cu.top + lob * 0.4f, cu.center.x, cu.top - lob * 0.5f)
            quadraticTo(cu.center.x * 0.5f + cu.right * 0.5f, cu.top + lob * 0.4f, cu.right + lob, cu.top - lob * 0.6f)
            quadraticTo(cu.right - lob * 0.4f, cu.center.y, cu.right + lob * 0.9f, cu.bottom + lob * 0.6f)
            quadraticTo(cu.center.x * 0.5f + cu.right * 0.5f, cu.bottom - lob * 0.4f, cu.center.x, cu.bottom + lob * 0.5f)
            quadraticTo(cu.center.x * 0.5f + cu.left * 0.5f, cu.bottom - lob * 0.4f, cu.left - lob * 0.9f, cu.bottom + lob * 0.6f)
            quadraticTo(cu.left + lob * 0.4f, cu.center.y, cu.left - lob, cu.top - lob * 0.6f); close()
        }
        translate(0.9f * u, 1.3f * u) { drawPath(piel, Color.Black.copy(alpha = 0.3f)) }
        val pielC = if (o) mez(n(CUERO, o), NOCHE, 0.2f) else CUERO
        drawPath(piel, Brush.linearGradient(listOf(mez(pielC, Color.White, 0.18f), pielC, mez(pielC, Color.Black, 0.12f)), cu.topLeft, cu.bottomRight))
        drawPath(piel, n(CUERO_OSC, o), style = Stroke(p * 0.7f))
        // Las tiras desde las puntas del cuero a las estacas de plata.
        val puntas = listOf(Offset(cu.left - lob, cu.top - lob * 0.6f), Offset(cu.right + lob, cu.top - lob * 0.6f), Offset(cu.left - lob * 0.9f, cu.bottom + lob * 0.6f), Offset(cu.right + lob * 0.9f, cu.bottom + lob * 0.6f))
        puntas.forEach { pt ->
            val sx = if (pt.x < w / 2) -1f else 1f; val sy = if (pt.y < h / 2) -1f else 1f
            val est = Offset(pt.x + sx * 1.8f * u, pt.y + sy * 1.4f * u)
            drawLine(n(CUERO_OSC, o), pt, est, p * 1.1f)
            clavo(est, 0.85f * u, o)
        }
        rect(ni.inflate(0.5f * u), mez(pielC, Color.Black, 0.2f))
        rect(ni, nicho(e, o, CUERO))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.top, ni.top + 2f * u))

        // A la izquierda: la gerege de oro con su cordón, la piedra de tinta y el pincel.
        val gx = (cen.left + cu.left - lob) / 2
        val gw = minOf((cu.left - lob - cen.left) * 0.62f, 8f * u)
        gerege(Rect(gx - gw / 2, h * 0.1f, gx + gw / 2, h * 0.43f), e, o, p, u)
        val pt = Rect(gx - gw * 0.6f, h * 0.5f, gx + gw * 0.6f, h * 0.6f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(pt.left + 0.5f * u, pt.top + 0.7f * u), pt.size, CornerRadius(0.8f * u))
        drawRoundRect(vert(pt, n(Color(0xFF4A4A50), o), n(TINTA_CHINA, o)), pt.topLeft, pt.size, CornerRadius(0.8f * u))
        drawOval(n(Color(0xFF0A0A0C), o), Offset(pt.left + pt.width * 0.2f, pt.top + pt.height * 0.18f), Size(pt.width * 0.6f, pt.height * 0.42f))
        drawOval(Color.White.copy(alpha = 0.12f), Offset(pt.left + pt.width * 0.3f, pt.top + pt.height * 0.22f), Size(pt.width * 0.25f, pt.height * 0.12f))
        // El pincel, en diagonal sobre la mesa.
        val p0 = Offset(gx - gw * 0.55f, h * 0.71f); val p1 = Offset(gx + gw * 0.7f, h * 0.635f)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(p0.x + 0.5f * u, p0.y + 0.8f * u), Offset(p1.x + 0.5f * u, p1.y + 0.8f * u), 1.1f * u, StrokeCap.Round)
        drawLine(n(Color(0xFFCBB27A), o), p0, p1, 1.0f * u, StrokeCap.Round)
        val tdx = (p1.x - p0.x) * 0.22f; val tdy = (p1.y - p0.y) * 0.22f
        drawLine(n(TINTA_CHINA, o), p0, Offset(p0.x + tdx, p0.y + tdy), 1.2f * u, StrokeCap.Round)
        drawLine(n(ROJO, o), Offset(p0.x + tdx, p0.y + tdy), Offset(p0.x + tdx * 1.2f, p0.y + tdy * 1.2f), 1.25f * u)

        // A la derecha: el arco compuesto, dos flechas y el frasquito de rapé.
        val ax = (cen.right + cu.right + lob) / 2
        val aw = minOf((cen.right - cu.right - lob) * 0.5f, 6f * u)
        arcoCompuesto(ax, h * 0.07f, h * 0.6f, aw, o, p, u)
        flecha(Offset(ax - aw * 0.35f, h * 0.66f), Offset(ax + aw * 0.4f, h * 0.12f), o, p, u, e.secundario)
        rape(Offset(ax + aw * 0.1f, h * 0.68f), 1.6f * u, o, p, u)

        // El canto del tablero y el faldón pintado con dos cajones.
        val canto = Rect(tab.left, tab.bottom, tab.right, tab.bottom + 2f * u)
        rect(canto, vert(canto, n(ROJO, o), n(ROJO_OSC, o)))
        drawLine(nm(ORO, o), Offset(canto.left, canto.top + canto.height * 0.5f), Offset(canto.right, canto.top + canto.height * 0.5f), p * 0.6f)
        val fal = Rect(tab.left + w * 0.02f, canto.bottom, tab.right - w * 0.02f, h * 0.885f)
        tablaNaranja(fal, o, e.semilla + 4, p)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), fal.top, fal.top + 1.4f * u), fal.topLeft, Size(fal.width, 1.4f * u))
        val banda = Rect(fal.left + fal.width * 0.33f, fal.top + fal.height * 0.22f, fal.right - fal.width * 0.33f, fal.bottom - fal.height * 0.22f)
        rect(banda, n(AZUL, o))
        martillo(banda.deflate(banda.height * 0.16f), nm(ORO, o), banda.height * 0.09f)
        drawRect(nm(ORO, o), banda.topLeft, banda.size, style = Stroke(p * 0.5f))
        listOf(fal.left + fal.width * 0.17f, fal.right - fal.width * 0.17f).forEach { cx ->
            val ca = Rect(cx - fal.width * 0.12f, fal.top + fal.height * 0.16f, cx + fal.width * 0.12f, fal.bottom - fal.height * 0.16f)
            rect(ca, vert(ca, n(NARANJA, o), n(NARANJA_OSC, o)))
            drawRect(n(ROJO_OSC, o), ca.topLeft, ca.size, style = Stroke(p * 0.8f))
            drawRect(nm(ORO, o), ca.deflate(0.7f * u).topLeft, ca.deflate(0.7f * u).size, style = Stroke(p * 0.4f))
            // La argolla de plata.
            drawCircle(nm(PLATA_OSC, o), ca.height * 0.24f, Offset(cx, ca.center.y + ca.height * 0.08f), style = Stroke(p * 0.9f))
            clavo(Offset(cx, ca.center.y - ca.height * 0.16f), 0.55f * u, o)
        }
        // Las patas de nube.
        listOf(tab.left + w * 0.02f, tab.right - w * 0.02f - w * 0.06f, tab.left + w * 0.24f, tab.right - w * 0.24f - w * 0.045f).forEachIndexed { i, x ->
            val pw = if (i < 2) w * 0.06f else w * 0.045f
            val atras = i >= 2
            val top = fal.bottom; val bot = if (atras) suelo - 0.8f * u else suelo
            val pata = Path().apply {
                moveTo(x, top); lineTo(x + pw, top); lineTo(x + pw, top + (bot - top) * 0.3f)
                quadraticTo(x + pw * 0.62f, top + (bot - top) * 0.45f, x + pw * 0.72f, bot); lineTo(x + pw * 0.28f, bot)
                quadraticTo(x + pw * 0.38f, top + (bot - top) * 0.45f, x, top + (bot - top) * 0.3f); close()
            }
            val c = if (atras) mez(n(NARANJA_OSC, o), Color.Black, 0.3f) else n(NARANJA, o)
            if (!atras) sombraBajo(x - u, x + pw + u, suelo, 1.4f * u, 0.4f)
            drawPath(pata, Brush.horizontalGradient(listOf(mez(c, Color.Black, 0.25f), mez(c, Color.White, 0.12f), c, mez(c, Color.Black, 0.25f)), x, x + pw))
            if (!atras) cuernos(Offset(x + pw / 2, top + (bot - top) * 0.2f), pw * 0.32f, nm(ORO, o), p * 0.5f)
        }
    }

    /** La gerege (paiza): la tablilla de oro del salvoconducto, con su agujero, la argolla y el cordón de seda. */
    private fun DrawScope.gerege(r: Rect, e: EstadoMueble, o: Boolean, p: Float, u: Float) {
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.width / 2, r.width / 2))) }
        translate(0.7f * u, 1.1f * u) { drawPath(forma, Color.Black.copy(alpha = 0.35f)) }
        drawPath(forma, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(ORO, o), nm(ORO_OSC, o)), r.left, r.right))
        drawPath(forma, nm(ORO_OSC, o), style = Stroke(p * 0.6f))
        val in2 = r.deflate(r.width * 0.14f)
        drawRoundRect(nm(ORO_OSC, o).copy(alpha = 0.6f), Offset(in2.left, in2.top + r.width * 0.3f), Size(in2.width, in2.height - r.width * 0.3f), CornerRadius(r.width * 0.2f), style = Stroke(p * 0.4f))
        // El agujero con la argolla y el cordón rojo.
        val ag = Offset(r.center.x, r.top + r.width * 0.42f)
        drawCircle(nm(ORO_OSC, o), r.width * 0.17f, ag)
        drawCircle(mez(n(FIELTRO_OSC, o), Color.Black, 0.2f), r.width * 0.11f, ag)
        drawCircle(nm(PLATA, o), r.width * 0.2f, Offset(ag.x, ag.y - r.width * 0.1f), style = Stroke(p * 0.7f))
        val cord = n(mez(e.secundario, ROJO, 0.4f), o)
        drawPath(Path().apply { moveTo(ag.x, ag.y - r.width * 0.3f); quadraticTo(ag.x - r.width * 0.9f, r.top - r.width * 0.2f, ag.x - r.width * 0.6f, r.top + r.width * 1.6f) }, cord, style = Stroke(p * 1.1f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(ag.x, ag.y - r.width * 0.3f); quadraticTo(ag.x + r.width * 0.2f, r.top - r.width * 0.5f, ag.x - r.width * 0.75f, r.top + r.width * 1.25f) }, cord, style = Stroke(p * 1.1f, cap = StrokeCap.Round))
        // Las tres columnas grabadas (la escritura vertical, sin letras: solo el trazo).
        for (k in 0 until 3) {
            val x = in2.left + in2.width * (k + 0.5f) / 3
            val path = Path(); var y = in2.top + r.width * 0.8f
            path.moveTo(x, y)
            while (y < in2.bottom - r.width * 0.2f) { path.lineTo(x + (if ((y / u).toInt() % 2 == 0) 0.35f else -0.35f) * u, y + 0.9f * u); y += 0.9f * u }
            drawPath(path, nm(ORO_OSC, o), style = Stroke(p * 0.55f))
        }
        drawLine(Color.White.copy(alpha = 0.35f), Offset(r.left + r.width * 0.28f, r.top + r.width * 0.6f), Offset(r.left + r.width * 0.28f, r.bottom - r.width * 0.5f), p * 0.6f)
    }

    /** El arco compuesto mongol, de pie: la curva doble, las orejas vueltas, la empuñadura de cuero y la cuerda. */
    private fun DrawScope.arcoCompuesto(cx: Float, top: Float, bottom: Float, ancho: Float, o: Boolean, p: Float, u: Float) {
        val mid = (top + bottom) / 2; val l = bottom - top
        val arco = Path().apply {
            moveTo(cx + ancho * 0.25f, top)
            quadraticTo(cx - ancho * 0.25f, top + l * 0.06f, cx - ancho * 0.2f, top + l * 0.16f)
            cubicTo(cx - ancho * 0.05f, top + l * 0.3f, cx - ancho * 0.75f, mid - l * 0.12f, cx - ancho * 0.6f, mid)
            cubicTo(cx - ancho * 0.75f, mid + l * 0.12f, cx - ancho * 0.05f, bottom - l * 0.3f, cx - ancho * 0.2f, bottom - l * 0.16f)
            quadraticTo(cx - ancho * 0.25f, bottom - l * 0.06f, cx + ancho * 0.25f, bottom)
        }
        translate(0.6f * u, 1f * u) { drawPath(arco, Color.Black.copy(alpha = 0.3f), style = Stroke(1.5f * u, cap = StrokeCap.Round)) }
        // La cuerda.
        drawLine(n(FIELTRO_OSC, o), Offset(cx + ancho * 0.22f, top + l * 0.02f), Offset(cx + ancho * 0.22f, bottom - l * 0.02f), p * 0.5f)
        drawPath(arco, n(Color(0xFF5A2E16), o), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        drawPath(arco, n(Color(0xFF9A5A2C), o), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        drawPath(arco, nm(ORO, o).copy(alpha = 0.6f), style = Stroke(0.25f * u, cap = StrokeCap.Round))
        // La empuñadura forrada y las orejas de hueso.
        drawRoundRect(n(ROJO_OSC, o), Offset(cx - ancho * 0.6f - 0.9f * u, mid - l * 0.06f), Size(1.8f * u, l * 0.12f), CornerRadius(0.6f * u))
        listOf(top + l * 0.16f, bottom - l * 0.16f).forEach { y -> drawCircle(n(FIELTRO, o), 0.6f * u, Offset(cx - ancho * 0.2f, y)) }
    }

    /** Una flecha con su punta de hierro y sus plumas. */
    private fun DrawScope.flecha(a: Offset, b: Offset, o: Boolean, p: Float, u: Float, pluma: Color) {
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(a.x + 0.5f * u, a.y + 0.8f * u), Offset(b.x + 0.5f * u, b.y + 0.8f * u), 0.7f * u)
        drawLine(n(Color(0xFFD2B47A), o), a, b, 0.55f * u, StrokeCap.Round)
        val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy)
        val ux = dx / l; val uy = dy / l
        val nx = -uy; val ny = ux
        // La punta.
        drawPath(Path().apply { moveTo(b.x + ux * 2.2f * u, b.y + uy * 2.2f * u); lineTo(b.x + nx * 0.8f * u, b.y + ny * 0.8f * u); lineTo(b.x - nx * 0.8f * u, b.y - ny * 0.8f * u); close() }, nm(PLATA_OSC, o))
        // Las plumas.
        val f0 = Offset(a.x + ux * 0.6f * u, a.y + uy * 0.6f * u)
        listOf(1f, -1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(f0.x, f0.y); lineTo(f0.x + ux * 3.6f * u, f0.y + uy * 3.6f * u)
                lineTo(f0.x + ux * 3.2f * u + s * nx * 1.1f * u, f0.y + uy * 3.2f * u + s * ny * 1.1f * u)
                lineTo(f0.x - ux * 0.2f * u + s * nx * 1.1f * u, f0.y - uy * 0.2f * u + s * ny * 1.1f * u); close()
            }, n(if (s > 0) pluma else mez(pluma, Color.Black, 0.3f), o))
        }
    }

    /** El frasquito de rapé (khöörög) de piedra, con tapón de coral y cucharilla. */
    private fun DrawScope.rape(c: Offset, r: Float, o: Boolean, p: Float, u: Float) {
        sombraBajo(c.x - r * 1.2f, c.x + r * 1.2f, c.y + r * 1.1f, r * 0.6f, 0.4f)
        drawOval(Brush.radialGradient(listOf(n(Color(0xFF8FD0B8), o), n(Color(0xFF3E8A70), o), n(Color(0xFF1F4A3A), o)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.5f), Offset(c.x - r, c.y - r * 0.9f), Size(r * 2, r * 2f))
        drawRect(nm(PLATA, o), Offset(c.x - r * 0.4f, c.y - r * 1.2f), Size(r * 0.8f, r * 0.35f))
        drawCircle(Brush.radialGradient(listOf(n(Color(0xFFF08A70), o), n(CORAL, o), n(ROJO_OSC, o)), Offset(c.x - r * 0.15f, c.y - r * 1.55f), r * 0.6f), r * 0.42f, Offset(c.x, c.y - r * 1.4f))
        drawOval(Color.White.copy(alpha = 0.3f), Offset(c.x - r * 0.6f, c.y - r * 0.5f), Size(r * 0.4f, r * 0.7f))
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.OTRAS_VIDAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val x0 = w * 0.012f; val x1 = w * 0.988f

        // La cresta: una nube pintada con el nudo infinito y, a los lados, los cuernos y dos remates.
        val crTop = h * 0.015f; val crBot = h * 0.115f
        val cr = Path().apply {
            val cx = w / 2; val an = w * 0.17f
            moveTo(cx - an, crBot)
            cubicTo(cx - an * 0.95f, crBot - (crBot - crTop) * 0.6f, cx - an * 0.55f, crBot - (crBot - crTop) * 0.3f, cx - an * 0.45f, crBot - (crBot - crTop) * 0.65f)
            cubicTo(cx - an * 0.4f, crTop, cx - an * 0.12f, crTop - (crBot - crTop) * 0.1f, cx, crTop)
            cubicTo(cx + an * 0.12f, crTop - (crBot - crTop) * 0.1f, cx + an * 0.4f, crTop, cx + an * 0.45f, crBot - (crBot - crTop) * 0.65f)
            cubicTo(cx + an * 0.55f, crBot - (crBot - crTop) * 0.3f, cx + an * 0.95f, crBot - (crBot - crTop) * 0.6f, cx + an, crBot); close()
        }
        drawPath(cr, vert(Rect(0f, crTop, 0f, crBot), n(NARANJA_LUZ, o), n(NARANJA, o), n(NARANJA_OSC, o)))
        drawPath(cr, n(ROJO_OSC, o), style = Stroke(p * 0.8f))
        val med = Offset(w / 2, crTop + (crBot - crTop) * 0.55f)
        val mr = (crBot - crTop) * 0.4f
        drawCircle(n(AZUL, o), mr, med)
        drawCircle(nm(ORO, o), mr, med, style = Stroke(p * 0.7f))
        ulzii(med, mr * 0.62f, nm(ORO_LUZ, o), n(AZUL_OSC, o), p * 0.5f)
        listOf(-1f, 1f).forEach { s -> cuernos(Offset(w / 2 + s * w * 0.1f, crBot - (crBot - crTop) * 0.42f), (crBot - crTop) * 0.3f, nm(ORO, o), p * 0.6f) }
        listOf(x0 + w * 0.05f, x1 - w * 0.05f).forEach { x -> lamparaMantequilla(Offset(x, crBot), 1.25f * u, o) }

        // La tapa roja con cuentas, el friso naranja con la greca del martillo y la moldura.
        val tapa = Rect(x0, crBot, x1, crBot + h * 0.04f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, tapa.bottom), Size(tapa.width, 1.2f * u))
        tapaRoja(tapa, o, p)
        val friso = Rect(x0 + w * 0.01f, tapa.bottom, x1 - w * 0.01f, h * 0.225f)
        tablaNaranja(friso, o, e.semilla, p)
        val bandaF = Rect(friso.left + w * 0.08f, friso.top + friso.height * 0.2f, friso.right - w * 0.08f, friso.bottom - friso.height * 0.2f)
        rect(bandaF, n(AZUL, o))
        martillo(bandaF.deflate(bandaF.height * 0.17f), nm(ORO_LUZ, o), bandaF.height * 0.1f)
        drawRect(nm(ORO, o), bandaF.topLeft, bandaF.size, style = Stroke(p * 0.5f))
        listOf(friso.left + w * 0.04f, friso.right - w * 0.04f).forEach { x -> ulzii(Offset(x, friso.center.y), friso.height * 0.28f, nm(ORO_LUZ, o), n(ROJO_OSC, o), p * 0.45f) }
        val mol = Rect(x0 + w * 0.005f, friso.bottom, x1 - w * 0.005f, friso.bottom + h * 0.025f)
        rect(mol, vert(mol, n(ROJO, o), n(ROJO_OSC, o)))
        drawLine(nm(ORO, o), Offset(mol.left, mol.top + mol.height * 0.35f), Offset(mol.right, mol.top + mol.height * 0.35f), p * 0.6f)

        // El cuerpo: los dos pilares azules a los lados y el fondo naranja tras las hornacinas.
        val cuerpo = Rect(x0 + w * 0.01f, mol.bottom, x1 - w * 0.01f, ni.bottom + h * 0.02f)
        tablaNaranja(cuerpo, o, e.semilla + 1, p)
        rect(Rect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.top + 1.8f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), cuerpo.top, cuerpo.top + 1.8f * u))
        val pil = (ni.left - cuerpo.left) * 0.5f
        listOf(cuerpo.left + (ni.left - cuerpo.left) / 2, cuerpo.right - (cuerpo.right - ni.right) / 2).forEach { cx ->
            panelAzul(Rect(cx - pil / 2, cuerpo.top + 2.6f * u, cx + pil / 2, cuerpo.bottom - 1.6f * u), o, p)
        }

        // Las cinco hornacinas iguales, forradas de fieltro, con su arco lobulado y los separadores con puntos de oro.
        val sep = 1.8f * u
        val cw = ni.width / 5f
        val forro = nicho(e, o, FIELTRO)
        rect(ni, n(ROJO_OSC, o))
        for (i in 0 until 5) {
            val c = Rect(ni.left + cw * i + sep / 2, ni.top, ni.left + cw * (i + 1) - sep / 2, ni.bottom)
            rect(c, forro)
            // La sombra del techo de la hornacina y la de un lado.
            rect(Rect(c.left, c.top, c.right, c.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (o) 0.25f else 0.13f), Color.Transparent), c.top, c.top + 3f * u))
            rect(Rect(c.left, c.top, c.left + 1.6f * u, c.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (o) 0.2f else 0.1f), Color.Transparent), c.left, c.left + 1.6f * u))
            // El arco lobulado: las enjutas pintadas de naranja en las esquinas de arriba, con su filete de oro.
            val ah = minOf(c.height * 0.15f, c.width * 0.26f)
            val arco = Path().apply {
                moveTo(c.left, c.top); lineTo(c.right, c.top); lineTo(c.right, c.top + ah)
                quadraticTo(c.right - c.width * 0.02f, c.top + ah * 0.35f, c.right - c.width * 0.2f, c.top + ah * 0.45f)
                quadraticTo(c.right - c.width * 0.3f, c.top, c.center.x, c.top + ah * 0.12f)
                quadraticTo(c.left + c.width * 0.3f, c.top, c.left + c.width * 0.2f, c.top + ah * 0.45f)
                quadraticTo(c.left + c.width * 0.02f, c.top + ah * 0.35f, c.left, c.top + ah); close()
            }
            drawPath(arco, vert(Rect(0f, c.top, 0f, c.top + ah), n(NARANJA_LUZ, o), n(NARANJA, o)))
            val borde = Path().apply {
                moveTo(c.right, c.top + ah)
                quadraticTo(c.right - c.width * 0.02f, c.top + ah * 0.35f, c.right - c.width * 0.2f, c.top + ah * 0.45f)
                quadraticTo(c.right - c.width * 0.3f, c.top, c.center.x, c.top + ah * 0.12f)
                quadraticTo(c.left + c.width * 0.3f, c.top, c.left + c.width * 0.2f, c.top + ah * 0.45f)
                quadraticTo(c.left + c.width * 0.02f, c.top + ah * 0.35f, c.left, c.top + ah)
            }
            drawPath(borde, nm(ORO, o), style = Stroke(p * 0.7f))
            // El estrado de la medalla: una tablita roja al pie.
            val es = Rect(c.left, c.bottom - minOf(2.2f * u, c.height * 0.05f), c.right, c.bottom)
            rect(es, vert(es, n(ROJO_LUZ, o), n(ROJO_OSC, o)))
        }
        for (i in 0..5) {
            val x = ni.left + cw * i
            val s = Rect(x - sep / 2 - (if (i == 0 || i == 5) 0.3f * u else 0f), ni.top, x + sep / 2 + (if (i == 0 || i == 5) 0.3f * u else 0f), ni.bottom)
            rect(s, horiz(s, n(NARANJA_OSC, o), n(NARANJA_LUZ, o), n(NARANJA, o)))
            var y = s.top + 3f * u
            while (y < s.bottom - 1.5f * u) { drawCircle(nm(ORO_LUZ, o), sep * 0.17f, Offset(s.center.x, y)); y += 3f * u }
        }
        drawRect(n(ROJO_OSC, o), ni.topLeft, ni.size, style = Stroke(p * 0.8f))

        // La base: moldura, zócalo con paneles azules y cuernos de oro, y las patas de nube.
        val b0 = cuerpo.bottom
        val zm = Rect(x0, b0, x1, b0 + h * 0.03f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zm.left, zm.top - 0.6f * u), Size(zm.width, 0.6f * u))
        rect(zm, vert(zm, n(ROJO_LUZ, o), n(ROJO_OSC, o)))
        drawLine(nm(ORO, o), Offset(zm.left, zm.center.y), Offset(zm.right, zm.center.y), p * 0.6f)
        val zo = Rect(x0 + w * 0.01f, zm.bottom, x1 - w * 0.01f, h * 0.93f)
        tablaNaranja(zo, o, e.semilla + 2, p)
        val np = 3
        for (k in 0 until np) {
            val pw = zo.width / np
            val pr = Rect(zo.left + pw * k + pw * 0.08f, zo.top + zo.height * 0.2f, zo.left + pw * (k + 1) - pw * 0.08f, zo.bottom - zo.height * 0.2f)
            rect(pr, n(AZUL, o))
            drawRect(nm(ORO, o), pr.topLeft, pr.size, style = Stroke(p * 0.5f))
            cuernos(Offset(pr.center.x, pr.center.y - pr.height * 0.12f), pr.height * 0.42f, nm(ORO_LUZ, o), p * 0.5f)
            listOf(pr.left + pr.height * 0.6f, pr.right - pr.height * 0.6f).forEach { x -> drawCircle(nm(ORO, o), pr.height * 0.12f, Offset(x, pr.center.y)) }
        }
        val suelo = h * 0.995f
        listOf(x0 + w * 0.02f, x1 - w * 0.02f - w * 0.07f, w / 2 - w * 0.035f).forEach { x ->
            val pw = w * 0.07f
            sombraBajo(x - u, x + pw + u, suelo, 1.2f * u, 0.35f)
            val pata = Path().apply {
                moveTo(x, zo.bottom); lineTo(x + pw, zo.bottom)
                quadraticTo(x + pw * 0.8f, zo.bottom + (suelo - zo.bottom) * 0.5f, x + pw * 0.66f, suelo); lineTo(x + pw * 0.34f, suelo)
                quadraticTo(x + pw * 0.2f, zo.bottom + (suelo - zo.bottom) * 0.5f, x, zo.bottom); close()
            }
            drawPath(pata, Brush.horizontalGradient(listOf(n(NARANJA_OSC, o), n(NARANJA, o), n(NARANJA_OSC, o)), x, x + pw))
        }
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u0 = h / 70f
        val o = oscuro
        val cx = w / 2
        when (id) {
            "unesco" -> {
                val u = u0 * 1.25f
                // La ger blanca en la estepa, con su puerta pintada, y una estupa blanca a su lado.
                val suelo = h * 0.86f
                // El cielo: un disco claro detrás.
                drawCircle(Brush.radialGradient(listOf((if (o) Color(0xFF1C3560) else CIELO).copy(alpha = 0.9f), Color.Transparent), Offset(cx, h * 0.48f), 27f * u), 27f * u, Offset(cx, h * 0.48f))
                drawOval(Brush.verticalGradient(listOf(n(PASTO, o), n(Color(0xFF4E7436), o)), suelo - 3f * u, suelo + 5f * u), Offset(cx - 44f * u, suelo - 3f * u), Size(88f * u, 7f * u))
                // La estupa (suburga) a la derecha.
                val sx = cx + 24f * u
                val gradas = listOf(14f, 11f, 8f)
                var y = suelo
                gradas.forEach { an -> val r = Rect(sx - an / 2 * u, y - 3f * u, sx + an / 2 * u, y); rect(r, vert(r, n(Color.White, o), n(FIELTRO_SOMBRA, o))); drawLine(n(FIELTRO_SOMBRA, o), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.4f * u); y -= 3f * u }
                drawPath(Path().apply { moveTo(sx - 5f * u, y); quadraticTo(sx - 5.5f * u, y - 8f * u, sx, y - 8.5f * u); quadraticTo(sx + 5.5f * u, y - 8f * u, sx + 5f * u, y); close() },
                    Brush.horizontalGradient(listOf(n(FIELTRO_OSC, o), n(Color.White, o), n(FIELTRO_SOMBRA, o)), sx - 5.5f * u, sx + 5.5f * u))
                rect(Rect(sx - 1.6f * u, y - 10.5f * u, sx + 1.6f * u, y - 8.3f * u), n(e.c1, o))
                drawPath(Path().apply { moveTo(sx - 1.3f * u, y - 10.5f * u); lineTo(sx, y - 20f * u); lineTo(sx + 1.3f * u, y - 10.5f * u); close() }, Brush.horizontalGradient(listOf(nm(ORO_OSC, o), nm(ORO_LUZ, o), nm(ORO, o)), sx - 1.3f * u, sx + 1.3f * u))
                for (k in 1..4) drawLine(nm(ORO_OSC, o), Offset(sx - 1.3f * u * (1 - k / 5f), y - 10.5f * u - k * 1.9f * u), Offset(sx + 1.3f * u * (1 - k / 5f), y - 10.5f * u - k * 1.9f * u), 0.35f * u)
                drawCircle(nm(ORO_LUZ, o), 0.9f * u, Offset(sx, y - 20.5f * u))
                // La ger: la pared y el techo de fieltro, las cuerdas, la puerta y la corona.
                val gx = cx - 8f * u
                val gw = 40f * u
                val pared = Rect(gx - gw / 2, suelo - 13f * u, gx + gw / 2, suelo)
                sombraBajo(pared.left - 2f * u, pared.right + 2f * u, suelo, 3f * u, 0.4f)
                drawPath(Path().apply { moveTo(pared.left, pared.top); lineTo(pared.right, pared.top); lineTo(pared.right, pared.bottom - 1f * u); quadraticTo(gx, pared.bottom + 1.5f * u, pared.left, pared.bottom - 1f * u); close() },
                    Brush.horizontalGradient(listOf(n(FIELTRO_SOMBRA, o), n(Color.White, o), n(FIELTRO, o), n(FIELTRO_SOMBRA, o)), pared.left, pared.right))
                val techo = Path().apply { moveTo(pared.left - 1.5f * u, pared.top + 0.5f * u); quadraticTo(gx - gw * 0.3f, pared.top - 9f * u, gx - 4f * u, pared.top - 12f * u); lineTo(gx + 4f * u, pared.top - 12f * u); quadraticTo(gx + gw * 0.3f, pared.top - 9f * u, pared.right + 1.5f * u, pared.top + 0.5f * u); close() }
                drawPath(techo, Brush.horizontalGradient(listOf(n(FIELTRO_SOMBRA, o), n(Color.White, o), n(FIELTRO_OSC, o)), pared.left, pared.right))
                drawPath(techo, n(FIELTRO_SOMBRA, o), style = Stroke(0.4f * u))
                // La faja del techo con el martillo y las cuerdas.
                drawLine(n(e.c1, o), Offset(pared.left - 0.5f * u, pared.top - 0.3f * u), Offset(pared.right + 0.5f * u, pared.top - 0.3f * u), 1.4f * u)
                listOf(pared.top + 4f * u, pared.top + 9f * u).forEach { yy -> drawLine(n(CUERO_OSC, o), Offset(pared.left, yy), Offset(pared.right, yy), 0.5f * u) }
                for (k in -2..2) drawLine(n(CUERO_OSC, o).copy(alpha = 0.7f), Offset(gx + k * 2.8f * u, pared.top - 11.5f * u), Offset(gx + k * 8.5f * u, pared.top - 0.8f * u), 0.4f * u)
                drawRect(nm(ORO_OSC, o), Offset(gx - 4f * u, pared.top - 13f * u), Size(8f * u, 1.2f * u))
                // La puerta pintada.
                val pu = Rect(gx - 4f * u, suelo - 10.5f * u, gx + 4f * u, suelo - 0.5f * u)
                rect(pu, vert(pu, n(NARANJA_LUZ, o), n(NARANJA_OSC, o)))
                rect(pu.deflate(1f * u), n(e.c2, o))
                drawRect(nm(ORO, o), pu.deflate(1f * u).topLeft, pu.deflate(1f * u).size, style = Stroke(0.4f * u))
                cuernos(Offset(pu.center.x, pu.center.y - 1f * u), 2.2f * u, nm(ORO_LUZ, o), 0.4f * u)
                // El humo de la estufa, quieto.
                drawPath(Path().apply { moveTo(gx + 2f * u, pared.top - 13f * u); cubicTo(gx + 6f * u, pared.top - 16f * u, gx - 1f * u, pared.top - 19f * u, gx + 4f * u, pared.top - 23f * u) },
                    (if (o) FIELTRO_OSC else FIELTRO_SOMBRA).copy(alpha = 0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            }
            "rascar" -> {
                val u = u0 * 1.1f
                // Un cuero con la capa de plata para rascar, una esquina ya rascada, y la gerege que raspa.
                val r = Rect(cx - 34f * u, h * 0.16f, cx + 22f * u, h * 0.86f)
                val piel = Path().apply {
                    moveTo(r.left, r.top + 2f * u); quadraticTo(r.center.x, r.top - 2f * u, r.right, r.top + 1f * u)
                    quadraticTo(r.right + 2.5f * u, r.center.y, r.right - 1f * u, r.bottom); quadraticTo(r.center.x, r.bottom + 2.5f * u, r.left + 1f * u, r.bottom - 1f * u)
                    quadraticTo(r.left - 2.5f * u, r.center.y, r.left, r.top + 2f * u); close()
                }
                translate(1f * u, 1.6f * u) { drawPath(piel, Color.Black.copy(alpha = 0.3f)) }
                drawPath(piel, Brush.linearGradient(listOf(n(Color(0xFFE2C9A0), o), n(CUERO, o)), r.topLeft, r.bottomRight))
                drawPath(piel, n(CUERO_OSC, o), style = Stroke(0.6f * u))
                val capa = r.deflate(4f * u)
                rect(capa, Brush.linearGradient(listOf(nm(PLATA_LUZ, o), nm(PLATA, o), nm(PLATA_OSC, o)), capa.topLeft, capa.bottomRight))
                martillo(Rect(capa.left, capa.top + 1f * u, capa.right, capa.top + 3.6f * u), nm(PLATA_OSC, o), 0.45f * u)
                // Lo rascado: el mapa debajo, en el acento, con un río.
                val ras = Path().apply { moveTo(capa.left, capa.center.y - 2f * u); quadraticTo(capa.left + capa.width * 0.35f, capa.top + 4f * u, capa.left + capa.width * 0.62f, capa.center.y + 1f * u); quadraticTo(capa.left + capa.width * 0.5f, capa.bottom - 1f * u, capa.left + capa.width * 0.2f, capa.bottom); lineTo(capa.left, capa.bottom); close() }
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    drawPath(ras, n(Color(0xFFE8D7B4), o))
                    drawPath(Path().apply { moveTo(capa.left + capa.width * 0.06f, capa.center.y + 1f * u); quadraticTo(capa.left + capa.width * 0.25f, capa.center.y - 3f * u, capa.left + capa.width * 0.38f, capa.bottom - 3f * u); lineTo(capa.left + capa.width * 0.1f, capa.bottom - 1f * u); close() }, n(e.c1, o))
                    drawPath(Path().apply { moveTo(capa.left, capa.bottom - 5f * u); quadraticTo(capa.left + capa.width * 0.3f, capa.bottom - 8f * u, capa.left + capa.width * 0.45f, capa.center.y) }, n(AZUL_LUZ, o), style = Stroke(0.7f * u))
                    drawPath(ras, nm(PLATA_OSC, o), style = Stroke(0.5f * u))
                }
                // La gerege que raspa, inclinada.
                rotate(28f, Offset(cx + 22f * u, h * 0.5f)) {
                    gerege(Rect(cx + 17f * u, h * 0.1f, cx + 27f * u, h * 0.9f), EstadoMueble(e.fondo, e.c1, e.c2, e.tinta), o, 0.45f * u, u)
                }
                // Las virutas de plata.
                listOf(Offset(cx + 6f * u, h * 0.84f), Offset(cx + 10f * u, h * 0.9f), Offset(cx + 3f * u, h * 0.92f)).forEach { drawCircle(nm(PLATA, o), 0.8f * u, it) }
            }
            else -> {
                val u = u0 * 1.12f
                // Tus lugares: tres ovoos (montones de piedras con su khadag) unidos por el camino sobre un mapa de cuero.
                val r = Rect(cx - 42f * u, h * 0.5f - 18f * u, cx + 42f * u, h * 0.5f + 24f * u)
                val piel = Path().apply {
                    moveTo(r.left, r.top + 2f * u); quadraticTo(r.center.x, r.top - 2.5f * u, r.right, r.top + 2f * u)
                    quadraticTo(r.right + 3f * u, r.center.y, r.right, r.bottom - 1f * u); quadraticTo(r.center.x, r.bottom + 3f * u, r.left, r.bottom - 1f * u)
                    quadraticTo(r.left - 3f * u, r.center.y, r.left, r.top + 2f * u); close()
                }
                translate(1f * u, 1.6f * u) { drawPath(piel, Color.Black.copy(alpha = 0.28f)) }
                drawPath(piel, Brush.linearGradient(listOf(n(Color(0xFFEAD6AE), o), n(CUERO, o)), r.topLeft, r.bottomRight))
                drawPath(piel, n(CUERO_OSC, o), style = Stroke(0.6f * u))
                // Las montañas y el río, a pincel.
                drawPath(Path().apply { moveTo(r.left + 4f * u, r.top + 12f * u); lineTo(r.left + 9f * u, r.top + 6f * u); lineTo(r.left + 13f * u, r.top + 10f * u); lineTo(r.left + 17f * u, r.top + 5f * u); lineTo(r.left + 22f * u, r.top + 12f * u) },
                    n(CUERO_OSC, o), style = Stroke(0.6f * u, join = StrokeJoin.Round))
                drawPath(Path().apply { moveTo(r.right - 30f * u, r.bottom - 2f * u); quadraticTo(r.right - 20f * u, r.bottom - 10f * u, r.right - 4f * u, r.bottom - 8f * u) }, n(AZUL_LUZ, o), style = Stroke(0.9f * u))
                val ovoos = listOf(Offset(r.left + 13f * u, r.bottom - 6f * u), Offset(cx + 2f * u, r.top + 10f * u), Offset(r.right - 12f * u, r.top + 18f * u))
                val camino = Path().apply { moveTo(ovoos[0].x, ovoos[0].y); quadraticTo(cx - 12f * u, r.top + 6f * u, ovoos[1].x, ovoos[1].y); quadraticTo(cx + 18f * u, r.top + 14f * u, ovoos[2].x, ovoos[2].y) }
                drawPath(camino, n(ROJO, o), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u))))
                ovoos.forEachIndexed { i, c -> ovoo(c, if (i == 1) 1.15f * u else u, if (i == 0) e.c2 else e.c1, o) }
            }
        }
    }

    /** Un ovoo: el montón de piedras con su vara y el khadag al viento. */
    private fun DrawScope.ovoo(c: Offset, u: Float, tela: Color, o: Boolean) {
        sombraBajo(c.x - 6f * u, c.x + 6f * u, c.y, 2f * u, 0.35f)
        // La vara con el khadag.
        drawLine(n(CUERO_OSC, o), Offset(c.x, c.y - 5f * u), Offset(c.x, c.y - 15f * u), 0.6f * u)
        drawPath(Path().apply { moveTo(c.x, c.y - 14.5f * u); quadraticTo(c.x + 4f * u, c.y - 15.5f * u, c.x + 7f * u, c.y - 13f * u); lineTo(c.x + 6f * u, c.y - 11.5f * u); quadraticTo(c.x + 3f * u, c.y - 12.5f * u, c.x, c.y - 11.5f * u); close() }, n(tela, o))
        drawPath(Path().apply { moveTo(c.x, c.y - 11f * u); quadraticTo(c.x - 3.5f * u, c.y - 11.5f * u, c.x - 5.5f * u, c.y - 9f * u); lineTo(c.x - 4.5f * u, c.y - 8f * u); quadraticTo(c.x - 2.5f * u, c.y - 9.5f * u, c.x, c.y - 9f * u); close() }, n(mez(tela, Color.White, 0.35f), o))
        // Las piedras, de abajo arriba.
        val piedras = listOf(-3.6f to 0f, 0f to 0.3f, 3.6f to 0f, -1.8f to -2.6f, 1.8f to -2.6f, 0f to -5f)
        piedras.forEachIndexed { k, (dx, dy) ->
            val pc = Offset(c.x + dx * u, c.y - 1.4f * u + dy * u)
            drawOval(Brush.radialGradient(listOf(n(Color(0xFFC9C9C2), o), n(PIEDRA, o), n(PIEDRA_OSC, o)), Offset(pc.x - 0.8f * u, pc.y - 0.8f * u), 3f * u), Offset(pc.x - 2.2f * u, pc.y - 1.6f * u), Size(4.4f * u, 3.2f * u))
            if (k == 5) drawOval(n(PIEDRA_OSC, o), Offset(pc.x - 2.2f * u, pc.y - 1.6f * u), Size(4.4f * u, 3.2f * u), style = Stroke(0.3f * u))
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.NOTAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val x0 = w * 0.01f; val x1 = w * 0.99f

        // La cresta con el nudo infinito y los cuernos.
        val crTop = h * 0.005f; val crBot = h * 0.06f
        val an = w * 0.13f
        val cr = Path().apply {
            moveTo(w / 2 - an, crBot)
            cubicTo(w / 2 - an * 0.9f, crTop + (crBot - crTop) * 0.3f, w / 2 - an * 0.4f, crTop + (crBot - crTop) * 0.5f, w / 2 - an * 0.3f, crTop + (crBot - crTop) * 0.25f)
            quadraticTo(w / 2, crTop - (crBot - crTop) * 0.2f, w / 2 + an * 0.3f, crTop + (crBot - crTop) * 0.25f)
            cubicTo(w / 2 + an * 0.4f, crTop + (crBot - crTop) * 0.5f, w / 2 + an * 0.9f, crTop + (crBot - crTop) * 0.3f, w / 2 + an, crBot); close()
        }
        drawPath(cr, vert(Rect(0f, crTop, 0f, crBot), n(NARANJA_LUZ, o), n(NARANJA_OSC, o)))
        drawPath(cr, n(ROJO_OSC, o), style = Stroke(p * 0.8f))
        val med = Offset(w / 2, crTop + (crBot - crTop) * 0.55f)
        drawCircle(n(AZUL, o), (crBot - crTop) * 0.36f, med)
        drawCircle(nm(ORO, o), (crBot - crTop) * 0.36f, med, style = Stroke(p * 0.6f))
        ulzii(med, (crBot - crTop) * 0.22f, nm(ORO_LUZ, o), n(AZUL_OSC, o), p * 0.45f)
        listOf(-1f, 1f).forEach { s -> cuernos(Offset(w / 2 + s * an * 0.62f, crBot - (crBot - crTop) * 0.4f), (crBot - crTop) * 0.26f, nm(ORO, o), p * 0.55f) }

        // La tapa roja, el friso con la greca y el marco: largueros naranjas con panel azul.
        val tapa = Rect(x0, crBot, x1, crBot + h * 0.03f)
        val marco = Rect(x0 + w * 0.006f, tapa.bottom, x1 - w * 0.006f, h * 0.935f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        tablaNaranja(marco, o, e.semilla, p)
        tapaRoja(tapa, o, p)
        val friso = Rect(marco.left, tapa.bottom, marco.right, ni.top - 1.4f * u)
        val bf = Rect(friso.left + w * 0.1f, friso.top + friso.height * 0.24f, friso.right - w * 0.1f, friso.bottom - friso.height * 0.24f)
        rect(bf, n(AZUL, o))
        martillo(bf.deflate(bf.height * 0.17f), nm(ORO_LUZ, o), bf.height * 0.1f)
        drawRect(nm(ORO, o), bf.topLeft, bf.size, style = Stroke(p * 0.5f))
        listOf(friso.left + w * 0.05f, friso.right - w * 0.05f).forEach { x -> ulzii(Offset(x, friso.center.y), friso.height * 0.24f, nm(ORO_LUZ, o), n(ROJO_OSC, o), p * 0.45f) }
        val pil = (ni.left - marco.left) * 0.48f
        listOf(marco.left + (ni.left - marco.left) / 2, marco.right - (marco.right - ni.right) / 2).forEach { cx ->
            panelAzul(Rect(cx - pil / 2, ni.top + 1f * u, cx + pil / 2, ni.bottom - 1f * u), o, p)
        }

        // El fondo: fieltro blanco acolchado (el nicho), con su filete rojo y el pespunte en rombos muy tenue.
        rect(ni.inflate(1.2f * u), n(ROJO_OSC, o))
        rect(ni.inflate(0.5f * u), nm(ORO, o))
        rect(ni, nicho(e, o, FIELTRO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val paso = 14f * u
            val c = (if (o) Color.White else FIELTRO_SOMBRA).copy(alpha = if (o) 0.05f else 0.22f)
            var x = ni.left - ni.height
            while (x < ni.right) {
                drawLine(c, Offset(x, ni.top), Offset(x + ni.height, ni.bottom), p * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.9f * u, 0.9f * u)))
                drawLine(c, Offset(x + ni.height, ni.top), Offset(x, ni.bottom), p * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.9f * u, 0.9f * u)))
                x += paso
            }
        }
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 2.6f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), ni.top, ni.top + 2.6f * u))
        rect(Rect(ni.left, ni.top, ni.left + 2f * u, ni.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.left, ni.left + 2f * u))

        // El travesaño de abajo: banda roja con cuentas y, debajo, las patas de nube.
        val tb = Rect(marco.left, ni.bottom + 1.2f * u, marco.right, marco.bottom)
        tapaRoja(Rect(tb.left, tb.top + tb.height * 0.2f, tb.right, tb.bottom - tb.height * 0.2f), o, p)
        val suelo = h * 0.995f
        listOf(marco.left + w * 0.03f, marco.right - w * 0.03f - w * 0.07f).forEach { x ->
            val pw = w * 0.07f
            sombraBajo(x - u, x + pw + u, suelo, 1.2f * u, 0.35f)
            drawPath(Path().apply {
                moveTo(x, marco.bottom); lineTo(x + pw, marco.bottom); quadraticTo(x + pw * 0.8f, marco.bottom + (suelo - marco.bottom) * 0.5f, x + pw * 0.66f, suelo)
                lineTo(x + pw * 0.34f, suelo); quadraticTo(x + pw * 0.2f, marco.bottom + (suelo - marco.bottom) * 0.5f, x, marco.bottom); close()
            }, Brush.horizontalGradient(listOf(n(NARANJA_OSC, o), n(NARANJA, o), n(NARANJA_OSC, o)), x, x + pw))
        }

        // El khadag (la bufanda de seda azul de las ofrendas) anudado a la esquina de arriba a la izquierda, que cae por el larguero.
        val kx = marco.left + (ni.left - marco.left) * 0.5f
        val ky = tapa.bottom + 0.6f * u
        val largo = h * 0.4f
        val seda = n(Color(0xFF8DB9EA), o)
        val sedaO = n(Color(0xFF5A88C4), o)
        val sedaL = n(Color(0xFFD6E8FA), o)
        val ak = minOf((ni.left - marco.left) * 0.95f, 5.5f * u)
        val cae = Path().apply {
            moveTo(kx - ak * 0.35f, ky); cubicTo(kx - ak * 0.75f, ky + largo * 0.3f, kx - ak * 0.2f, ky + largo * 0.6f, kx - ak * 0.5f, ky + largo)
            lineTo(kx + ak * 0.5f, ky + largo * 0.97f); cubicTo(kx + ak * 0.25f, ky + largo * 0.6f, kx + ak * 0.6f, ky + largo * 0.3f, kx + ak * 0.35f, ky); close()
        }
        translate(0.8f * u, 0.8f * u) { drawPath(cae, Color.Black.copy(alpha = 0.3f)) }
        drawPath(cae, Brush.horizontalGradient(listOf(sedaO, seda, sedaL, seda, sedaO), kx - ak * 0.75f, kx + ak * 0.6f))
        // Los pliegues, el dibujo tejido y el fleco.
        drawPath(Path().apply { moveTo(kx - ak * 0.05f, ky + 1.5f * u); cubicTo(kx - ak * 0.3f, ky + largo * 0.3f, kx + ak * 0.15f, ky + largo * 0.6f, kx - ak * 0.05f, ky + largo * 0.98f) }, sedaO.copy(alpha = 0.6f), style = Stroke(p * 0.5f))
        listOf(0.45f, 0.62f).forEach { f -> cuernos(Offset(kx - ak * 0.08f, ky + largo * f), ak * 0.22f, sedaL.copy(alpha = 0.8f), p * 0.35f) }
        for (k in 0..6) {
            val t = k / 6f
            val x = kx - ak * 0.5f + ak * t; val y = ky + largo * (1f - 0.03f * t)
            drawLine(seda, Offset(x, y), Offset(x - 0.2f * u, y + 1.6f * u), p * 0.45f)
        }
        // El nudo.
        drawOval(Brush.radialGradient(listOf(sedaL, seda, sedaO), Offset(kx - 0.6f * u, ky - 0.6f * u), ak * 0.7f), Offset(kx - ak * 0.5f, ky - 1.6f * u), Size(ak, 3.2f * u))
        drawPath(Path().apply { moveTo(kx - ak * 0.45f, ky); quadraticTo(kx - ak * 1.1f, ky - 2.4f * u, kx - ak * 0.2f, ky - 1.4f * u) }, seda, style = Stroke(1.1f * u, cap = StrokeCap.Round))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        // La lámina de corteza: un rectángulo con los bordes un poco irregulares y la esquina de abajo enroscada.
        val hoja = r.deflate(2f * un)
        val forma = Path().apply {
            moveTo(hoja.left, hoja.top + 1f * un)
            var k = 0
            for (s in 1..6) { val x = hoja.left + hoja.width * s / 6; lineTo(x, hoja.top + (azar(semilla, k++) - 0.5f) * 1.6f * un) }
            for (s in 1..6) { val y = hoja.top + hoja.height * s / 6 * 0.9f; lineTo(hoja.right + (azar(semilla, k++) - 0.5f) * 1.6f * un, y) }
            quadraticTo(hoja.right - 3f * un, hoja.bottom + 1f * un, hoja.right - 10f * un, hoja.bottom)
            for (s in 5 downTo 1) { val x = hoja.left + hoja.width * s / 6; lineTo(x, hoja.bottom + (azar(semilla, k++) - 0.5f) * 1.6f * un) }
            lineTo(hoja.left, hoja.bottom)
            for (s in 5 downTo 1) { val y = hoja.top + hoja.height * s / 6; lineTo(hoja.left + (azar(semilla, k++) - 0.5f) * 1.6f * un, y) }
            close()
        }
        translate(1.2f * un, 2f * un) { drawPath(forma, Color.Black.copy(alpha = if (o) 0.45f else 0.25f)) }
        val corteza = if (o) ABEDUL_NOCHE else ABEDUL
        drawPath(forma, Brush.linearGradient(listOf(mez(corteza, Color.White, if (o) 0.06f else 0.4f), corteza, mez(corteza, if (o) Color.Black else Color(0xFFD9C7A8), 0.35f)), r.topLeft, r.bottomRight))
        clipPath(forma) {
            // Las lenticelas: rayitas horizontales oscuras, sobre todo en los bordes (no compiten con el texto).
            val len = (if (o) Color(0xFF1C1A16) else Color(0xFF3A3328)).copy(alpha = if (o) 0.5f else 0.42f)
            for (k in 0 until 16) {
                val borde = k % 4
                val fx = azar(semilla, k + 40); val fy = azar(semilla, k + 80)
                val (x, y) = when (borde) {
                    0 -> (hoja.left + hoja.width * fx) to (hoja.top + hoja.height * 0.07f * fy + 0.6f * un)
                    1 -> (hoja.left + hoja.width * fx) to (hoja.bottom - hoja.height * 0.08f * fy - 0.6f * un)
                    2 -> (hoja.left + hoja.width * 0.06f * fx) to (hoja.top + hoja.height * fy)
                    else -> (hoja.right - hoja.width * 0.06f * fx) to (hoja.top + hoja.height * fy)
                }
                val l = (2f + 5f * azar(semilla, k + 120)) * un
                drawLine(len, Offset(x - l / 2, y), Offset(x + l / 2, y), (0.45f + 0.5f * azar(semilla, k + 160)) * un, StrokeCap.Round)
            }
            // Una mancha rosada de corteza nueva.
            mancha(Offset(hoja.left + hoja.width * (0.2f + 0.6f * azar(semilla, 7)), hoja.top + hoja.height * (0.3f + 0.4f * azar(semilla, 8))), hoja.width * 0.35f,
                (if (o) Color(0xFF6A4A38) else Color(0xFFE9C7A8)).copy(alpha = if (o) 0.22f else 0.3f))
        }
        drawPath(forma, (if (o) Color(0xFF221F1A) else Color(0xFFB9A98C)), style = Stroke(0.7f * un, join = StrokeJoin.Round))
        // La esquina enroscada de abajo a la derecha: la cara interna, color salmón.
        val esq = Path().apply {
            moveTo(hoja.right - 10f * un, hoja.bottom); quadraticTo(hoja.right - 3f * un, hoja.bottom + 1f * un, hoja.right + 0.4f * un, hoja.top + hoja.height * 0.9f)
            quadraticTo(hoja.right - 4.5f * un, hoja.bottom - 5f * un, hoja.right - 10f * un, hoja.bottom); close()
        }
        drawPath(esq, Brush.linearGradient(listOf(n(Color(0xFFE6A982), o), n(Color(0xFFB8714A), o)), Offset(hoja.right - 10f * un, hoja.bottom), Offset(hoja.right, hoja.bottom - 8f * un)))

        // El sello del tipo, abajo a la izquierda (fuera del texto).
        val q = Offset(r.left + r.width * 0.12f, r.bottom - r.height * 0.085f)
        val t = r.height * 0.045f
        when (e.tipo) {
            "idea" -> ulzii(q, t, nm(ORO, o).let { if (o) ORO else ORO_OSC }, if (o) Color(0xFF1C1A16) else ABEDUL, 0.5f * un)
            "pensamiento" -> {
                val c = if (o) AZUL_LUZ else AZUL
                drawPath(espiral(Offset(q.x - t * 0.5f, q.y), t * 0.75f, 1.5f, 1f, Math.PI.toFloat()), c, style = Stroke(0.7f * un, cap = StrokeCap.Round))
                drawPath(espiral(Offset(q.x + t * 0.5f, q.y), t * 0.75f, 1.5f, -1f, 0f), c, style = Stroke(0.7f * un, cap = StrokeCap.Round))
                drawLine(c, Offset(q.x - t * 1.25f, q.y), Offset(q.x + t * 1.25f, q.y), 0.6f * un)
            }
            "proposito" -> {
                val c = if (o) Color(0xFFE7C590) else Color(0xFF6A3E1E)
                drawLine(c, Offset(q.x - t * 1.3f, q.y), Offset(q.x + t * 1.1f, q.y), 0.7f * un, StrokeCap.Round)
                drawPath(Path().apply { moveTo(q.x + t * 1.6f, q.y); lineTo(q.x + t * 0.9f, q.y - t * 0.5f); lineTo(q.x + t * 0.9f, q.y + t * 0.5f); close() }, if (o) PLATA else PLATA_OSC)
                listOf(-1f, 1f).forEach { s -> drawLine(if (o) CORAL else ROJO, Offset(q.x - t * 1.3f, q.y), Offset(q.x - t * 0.8f, q.y + s * t * 0.55f), 0.6f * un) }
            }
            else -> {
                val c = if (o) mez(e.acento, Color.White, 0.2f) else e.acento
                drawCircle(c, t * 0.55f, Offset(q.x - t * 0.5f, q.y), style = Stroke(0.8f * un))
                drawCircle(c, t * 0.55f, Offset(q.x + t * 0.5f, q.y), style = Stroke(0.8f * un))
                drawLine(c, q, Offset(q.x - t * 0.4f, q.y + t * 1.2f), 0.8f * un)
                drawLine(c, q, Offset(q.x + t * 0.5f, q.y + t * 1.1f), 0.8f * un)
            }
        }

        // Arriba: la clavija de madera con su tira de cuero; fijada, el clavo de plata con coral y el cordón rojo.
        val cl = Offset(r.center.x, r.top + r.height * 0.06f)
        if (e.fijada) {
            val cordon = if (o) Color(0xFFE0614E) else Color(0xFFB22A22)
            drawPath(Path().apply { moveTo(cl.x, cl.y); quadraticTo(cl.x + 5f * un, cl.y + 1f * un, cl.x + 8f * un, cl.y + 6f * un) }, cordon, style = Stroke(0.9f * un, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(cl.x, cl.y); quadraticTo(cl.x + 6f * un, cl.y - 1f * un, cl.x + 11f * un, cl.y + 3.5f * un) }, cordon, style = Stroke(0.9f * un, cap = StrokeCap.Round))
            drawCircle(Color.Black.copy(alpha = 0.35f), 3.4f * un, Offset(cl.x + 0.8f * un, cl.y + 1.2f * un))
            drawCircle(Brush.radialGradient(listOf(PLATA_LUZ, PLATA, PLATA_OSC), Offset(cl.x - un, cl.y - un), 4.2f * un), 3.2f * un, cl)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFF59A80), CORAL, ROJO_OSC), Offset(cl.x - 0.6f * un, cl.y - 0.6f * un), 2.2f * un), 1.7f * un, cl)
        } else {
            val tira = if (o) Color(0xFF8A5A34) else CUERO_OSC
            drawPath(Path().apply { moveTo(cl.x - 2.6f * un, cl.y + 4f * un); quadraticTo(cl.x - 1f * un, cl.y - 1f * un, cl.x, cl.y); quadraticTo(cl.x + 1f * un, cl.y - 1f * un, cl.x + 2.6f * un, cl.y + 4f * un) }, tira, style = Stroke(0.9f * un, cap = StrokeCap.Round))
            listOf(-2.6f, 2.6f).forEach { dx -> drawCircle(mez(corteza, Color.Black, 0.55f), 0.8f * un, Offset(cl.x + dx * un, cl.y + 4.2f * un)) }
            drawCircle(Color.Black.copy(alpha = 0.3f), 2.2f * un, Offset(cl.x + 0.6f * un, cl.y + 0.9f * un))
            drawCircle(Brush.radialGradient(listOf(NARANJA_LUZ, NARANJA, NARANJA_OSC), Offset(cl.x - 0.6f * un, cl.y - 0.6f * un), 2.8f * un), 2f * un, cl)
            drawCircle(ROJO_OSC, 2f * un, cl, style = Stroke(0.4f * un))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF3EEE2) else Color(0xFF1B2230)
}
