package io.github.ruddy2003.vestigium.ui.vida

import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Helénico: la casa griega, con los materiales de su biblioteca, su Ideario y su stoa de logros: mármol con vetas,
 * estuco rojo con el zócalo azul del Egeo, terracota con barniz negro, nogal, bronce y el sol de Vergina.
 * - Social: el andrón del simposio. El entablamento jónico arriba (antefijas, cornisa con greca, dentículos, ovas y
 *   arquitrabe de tres fajas), dos columnas jónicas a los lados y, al centro, el panel de mármol enmarcado donde va tu
 *   gente. Abajo, sobre el piso de losas, dos klinai con su colchón, sus cojines y el fulcro de bronce, las mesitas con su
 *   kílix y sus uvas, y la crátera en medio.
 * - Mapas: la mesa del geógrafo: un tablero de nogal con canto moldurado, el rollo de papiro abierto entre sus dos
 *   cilindros, la lucerna encendida, unas dracmas, el compás de bronce, el tintero con el cálamo, la regla y otro rollo
 *   atado; al frente, el faldón con la greca de marfil y tachuelas, y las patas de león.
 * - Otras vidas: un tesoro dórico. Antefijas, cornisa, friso de triglifos y metopas con rosetas, arquitrabe, dos columnas
 *   dóricas a los lados, cinco hornacinas iguales separadas por pilastras y el estilóbato de tres gradas.
 * - Íconos: el Partenón sobre la roca, una tetradracma de la lechuza que raspa una tablilla barnizada y un trozo de
 *   carta con tres clavos de bronce.
 * - Notas: un leukoma (el tablón blanqueado donde se ponían los avisos) en su marco de nogal, con frontón y sol de
 *   Vergina, cantoneras de bronce y una repisa con consolas; cada nota es un óstrakon con su banda de barniz.
 */
internal object VidaHeleno : DisenoVida {
    private val MARMOL = Color(0xFFEDE6D6)
    private val MARMOL_LUZ = Color(0xFFFAF6EC)
    private val MARMOL_OSC = Color(0xFFC9BFA8)
    private val MARMOL_SOMBRA = Color(0xFF9C927C)
    private val TERRACOTA = Color(0xFFC2652F)
    private val TERRA_OSC = Color(0xFF8A4220)
    private val TERRA_LUZ = Color(0xFFDE8A52)
    private val BARNIZ = Color(0xFF1A1410)
    private val EGEO = Color(0xFF2E5E8E)
    private val PURPURA = Color(0xFF5B2A6E)
    private val ORO = Color(0xFFC9A54A)
    private val BRONCE = Color(0xFFB07A3A)
    private val BRONCE_LUZ = Color(0xFFE6BC72)
    private val BRONCE_OSC = Color(0xFF5E3C18)
    private val PLATA = Color(0xFFCDD0D4)
    private val ROJO = Color(0xFF8E3B2C)
    private val NOGAL = Color(0xFF6E4A2E)
    private val NOGAL_LUZ = Color(0xFF9A6C44)
    private val NOGAL_OSC = Color(0xFF3B2414)
    private val OLIVO = Color(0xFF5E7A3A)
    private val UVA = Color(0xFF4B2A5E)
    private val PAPIRO = Color(0xFFEBDDB8)
    private val CALIZA = Color(0xFFD8C49A)
    private val LLAMA = Color(0xFFFFC870)
    private val NOCHE = Color(0xFF0E0B12)

    /** El óstrakon: barro claro de día; de noche, barro cocido oscuro (con la tinta clara de [tintaNota]). */
    private val BARRO_CLARO = Color(0xFFEBD3AF)
    private val BARRO_OSCURO = Color(0xFF4A2C1E)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.13f, 0.18f, 0.87f, 0.775f)
        MuebleVida.MAPAS -> Rect(0.15f, 0.10f, 0.85f, 0.71f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.22f, 0.925f, 0.82f)
        MuebleVida.NOTAS -> Rect(0.065f, 0.12f, 0.935f, 0.885f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de noche: el mismo, en penumbra. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.58f) else c
    /** El tablero de notas (piloto de la luz): de noche, la penumbra de siempre, apenas fría. [n] sigue para los muebles de Vida. */
    private fun nm(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, Color(0xFF12101A), 0.58f) else c

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mez(e.fondo, mez(material, NOCHE, 0.8f), 0.45f) else mez(e.fondo, material, 0.3f)

    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)
    private fun DrawScope.rect(r: Rect, b: Brush) = drawRect(b, r.topLeft, r.size)
    private fun vert(r: Rect, c: List<Color>) = Brush.verticalGradient(c, r.top, r.bottom)
    private fun vert(r: Rect, a: Color, b: Color) = vert(r, listOf(a, b))
    private fun vert(r: Rect, a: Color, b: Color, c: Color) = vert(r, listOf(a, b, c))
    private fun horiz(r: Rect, c: List<Color>) = Brush.horizontalGradient(c, r.left, r.right)
    private fun horiz(r: Rect, a: Color, b: Color) = horiz(r, listOf(a, b))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color) = horiz(r, listOf(a, b, c))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color, d: Color) = horiz(r, listOf(a, b, c, d))
    private fun horiz(r: Rect, a: Color, b: Color, c: Color, d: Color, f: Color) = horiz(r, listOf(a, b, c, d, f))

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Una mancha radial suave (luz de lámpara, humedad del estuco). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Vetas de madera en [r]: líneas onduladas a lo largo, de grosor [p] (≈ 1 dp). */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, p: Float, densidad: Float = 1f, vertical: Boolean = false) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        val n = (ancho / (3.2f * p) * densidad).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.6f + 1.6f * azar(semilla, k + 100)) * p
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val path = Path()
                for (s in 0..16) {
                    val t = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + t
                    val y = if (vertical) r.top + t else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(p * (0.5f + 0.6f * azar(semilla, k + 500))))
            }
        }
    }

    /** Las vetas del mármol: unas pocas líneas quebradas en diagonal. */
    private fun DrawScope.vetasMarmol(r: Rect, color: Color, semilla: Int, p: Float, n: Int = 3) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val path = Path()
                var x = r.left + r.width * azar(semilla, k)
                var y = r.top - r.height * 0.05f
                path.moveTo(x, y)
                for (s in 1..7) {
                    x += (azar(semilla, k * 10 + s) - 0.3f) * r.width * 0.12f
                    y += r.height / 6f
                    path.lineTo(x, y)
                }
                drawPath(path, color, style = Stroke(p * (0.5f + 0.7f * azar(semilla, k + 50)), join = StrokeJoin.Round))
            }
        }
    }

    /** Una greca (meandro de ganchos sobre una línea), recortada a [r]. */
    private fun DrawScope.greca(r: Rect, color: Color, grosor: Float) {
        val a = r.height
        if (a <= 0f || r.width <= 0f) return
        val path = Path()
        var x = r.left
        var k = 0
        path.moveTo(r.left, r.bottom - grosor / 2); path.lineTo(r.right, r.bottom - grosor / 2)
        while (x < r.right && k < 300) {
            val t = r.top + grosor / 2
            path.moveTo(x + a * 0.1f, r.bottom - grosor / 2)
            path.lineTo(x + a * 0.1f, t); path.lineTo(x + a * 0.85f, t); path.lineTo(x + a * 0.85f, t + a * 0.58f)
            path.lineTo(x + a * 0.45f, t + a * 0.58f); path.lineTo(x + a * 0.45f, t + a * 0.3f)
            x += a * 1.1f; k++
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(path, color, style = Stroke(grosor)) }
    }

    /** Una palmeta (antefija) que nace en [base], de radio [r]: siete hojas en abanico y dos volutas. */
    private fun DrawScope.palmeta(base: Offset, r: Float, color: Color) {
        for (j in -3..3) {
            val ang = Math.toRadians(-90.0 + j * 23.0)
            val largo = r * (1f - 0.07f * abs(j))
            val punta = Offset(base.x + largo * cos(ang).toFloat(), base.y + largo * sin(ang).toFloat())
            val nx = -sin(ang).toFloat() * r * 0.1f; val ny = cos(ang).toFloat() * r * 0.1f
            val medio = Offset((base.x + punta.x) / 2, (base.y + punta.y) / 2)
            drawPath(Path().apply {
                moveTo(base.x, base.y); quadraticTo(medio.x + nx, medio.y + ny, punta.x, punta.y); quadraticTo(medio.x - nx, medio.y - ny, base.x, base.y); close()
            }, color)
        }
        listOf(-1f, 1f).forEach { s -> drawCircle(color, r * 0.13f, Offset(base.x + s * r * 0.24f, base.y - r * 0.04f), style = Stroke(r * 0.07f)) }
        drawCircle(color, r * 0.13f, base)
    }

    /** El sol de Vergina: la estrella de dieciséis rayos con su disco. */
    private fun DrawScope.solVergina(c: Offset, r: Float, color: Color) {
        val path = Path()
        for (k in 0 until 32) {
            val a = -Math.PI / 2 + Math.PI * k / 16
            val rr = if (k % 2 == 0) r else r * 0.45f
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
        drawCircle(color, r * 0.3f, c)
    }

    /** Una roseta de [petalos] pétalos, pintada en una metopa o una tachuela. */
    private fun DrawScope.roseta(c: Offset, r: Float, color: Color, centro: Color, petalos: Int = 8) {
        for (k in 0 until petalos) {
            val a = 2 * Math.PI * k / petalos
            val o = Offset(c.x + r * 0.55f * cos(a).toFloat(), c.y + r * 0.55f * sin(a).toFloat())
            drawCircle(color, r * 0.36f, o)
        }
        drawCircle(centro, r * 0.3f, c)
    }

    /** Dentículos: la fila de bloquecitos bajo la cornisa, con su sombra. */
    private fun DrawScope.denticulos(r: Rect, oscuro: Boolean) {
        rect(r, n(MARMOL_SOMBRA, oscuro))
        val d = r.height * 0.75f
        var x = r.left + d * 0.2f
        while (x < r.right) {
            rect(Rect(x, r.top, x + d, r.bottom - r.height * 0.12f), vert(r, n(MARMOL_LUZ, oscuro), n(MARMOL_OSC, oscuro)))
            x += d * 1.45f
        }
    }

    /** Ovas y dardos: el cimacio jónico. */
    private fun DrawScope.ovas(r: Rect, oscuro: Boolean, p: Float) {
        rect(r, vert(r, n(MARMOL_SOMBRA, oscuro), n(MARMOL_OSC, oscuro)))
        val a = r.height * 1.05f
        var x = r.left
        while (x < r.right) {
            drawOval(vert(r, n(MARMOL_LUZ, oscuro), n(MARMOL_OSC, oscuro)), Offset(x + a * 0.15f, r.top + r.height * 0.06f), Size(a * 0.7f, r.height * 0.86f))
            drawLine(n(BARNIZ, oscuro).copy(alpha = 0.45f), Offset(x + a * 0.96f, r.top + r.height * 0.2f), Offset(x + a * 0.96f, r.bottom - r.height * 0.1f), p * 0.7f)
            x += a
        }
        drawLine(n(MARMOL_LUZ, oscuro), Offset(r.left, r.top), Offset(r.right, r.top), p * 0.6f)
    }

    /** Una columna jónica de fuste [cw]: basa ática, fuste estriado, collarino, equino con ovas, volutas y ábaco. */
    private fun DrawScope.columnaJonica(xc: Float, top: Float, bottom: Float, cw: Float, oscuro: Boolean, p: Float) {
        val mar = n(MARMOL, oscuro); val luz = n(MARMOL_LUZ, oscuro); val osc = n(MARMOL_OSC, oscuro); val som = n(MARMOL_SOMBRA, oscuro)
        val capH = cw * 0.66f; val baseH = cw * 0.5f
        val fTop = top + capH; val fBot = bottom - baseH
        // La sombra de la columna sobre el muro.
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), xc + cw / 2, xc + cw), Offset(xc + cw / 2, fTop), Size(cw / 2, fBot - fTop))
        // El fuste con sus estrías.
        val f = Rect(xc - cw / 2, fTop, xc + cw / 2, fBot)
        rect(f, horiz(f, som, luz, mar, osc, som))
        for (k in 1 until 7) {
            val x = f.left + cw * k / 7
            drawLine(som.copy(alpha = 0.7f), Offset(x, fTop + cw * 0.05f), Offset(x, fBot - cw * 0.05f), p * 0.7f)
            drawLine(luz.copy(alpha = 0.55f), Offset(x + p * 0.8f, fTop + cw * 0.05f), Offset(x + p * 0.8f, fBot - cw * 0.05f), p * 0.5f)
        }
        // El collarino y el equino con tres ovas.
        rect(Rect(xc - cw * 0.56f, fTop - cw * 0.08f, xc + cw * 0.56f, fTop), vert(Rect(0f, fTop - cw * 0.08f, 0f, fTop), luz, som))
        val eq = Rect(xc - cw * 0.6f, top + capH * 0.42f, xc + cw * 0.6f, fTop - cw * 0.08f)
        rect(eq, vert(eq, osc, som))
        for (k in 0 until 3) {
            val ox = eq.left + eq.width * (k + 0.5f) / 3
            drawOval(vert(eq, luz, osc), Offset(ox - eq.width * 0.13f, eq.top + eq.height * 0.08f), Size(eq.width * 0.26f, eq.height * 0.84f))
        }
        // El canal de las volutas y las volutas en espiral.
        val canal = Rect(xc - cw * 0.86f, top + capH * 0.16f, xc + cw * 0.86f, top + capH * 0.46f)
        rect(canal, vert(canal, luz, mar, osc))
        listOf(-1f, 1f).forEach { s ->
            val vc = Offset(xc + s * cw * 0.8f, top + capH * 0.52f)
            val rv = cw * 0.27f
            drawCircle(Brush.radialGradient(listOf(luz, mar, osc), Offset(vc.x - rv * 0.3f, vc.y - rv * 0.3f), rv * 1.3f), rv, vc)
            val espiral = Path()
            for (t in 0..36) {
                val ang = t / 36f * 2.4f * 6.283f * s
                val rr = rv * (0.92f - 0.72f * t / 36f)
                val x = vc.x + rr * cos(ang); val y = vc.y + rr * sin(ang)
                if (t == 0) espiral.moveTo(x, y) else espiral.lineTo(x, y)
            }
            drawPath(espiral, som, style = Stroke(p * 0.75f))
            drawCircle(som, rv * 0.16f, vc)
        }
        // El ábaco.
        val ab = Rect(xc - cw * 0.8f, top, xc + cw * 0.8f, top + capH * 0.17f)
        rect(ab, vert(ab, luz, osc))
        drawLine(som, Offset(ab.left, ab.bottom), Offset(ab.right, ab.bottom), p * 0.6f)
        // La basa: toro, escocia, toro y plinto.
        val t1 = Rect(xc - cw * 0.6f, fBot, xc + cw * 0.6f, fBot + baseH * 0.26f)
        drawRoundRect(vert(t1, luz, osc), t1.topLeft, t1.size, CornerRadius(t1.height / 2))
        val es = Rect(xc - cw * 0.52f, t1.bottom, xc + cw * 0.52f, fBot + baseH * 0.42f)
        rect(es, vert(es, som, osc))
        val t2 = Rect(xc - cw * 0.7f, es.bottom, xc + cw * 0.7f, fBot + baseH * 0.72f)
        drawRoundRect(vert(t2, luz, mar, som), t2.topLeft, t2.size, CornerRadius(t2.height / 2))
        val pl = Rect(xc - cw * 0.76f, t2.bottom, xc + cw * 0.76f, bottom)
        rect(pl, vert(pl, mar, osc))
        drawLine(luz, Offset(pl.left, pl.top), Offset(pl.right, pl.top), p * 0.5f)
    }

    /** Una columna dórica de fuste [cw]: el fuste con éntasis y aristas, los anillos, el equino y el ábaco. */
    private fun DrawScope.columnaDorica(xc: Float, top: Float, bottom: Float, cw: Float, oscuro: Boolean, p: Float) {
        val mar = n(MARMOL, oscuro); val luz = n(MARMOL_LUZ, oscuro); val osc = n(MARMOL_OSC, oscuro); val som = n(MARMOL_SOMBRA, oscuro)
        val capH = cw * 0.5f
        val fTop = top + capH
        val arr = cw * 0.4f; val abj = cw * 0.5f
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), xc + abj, xc + abj + cw * 0.4f), Offset(xc + abj, fTop), Size(cw * 0.4f, bottom - fTop))
        val fuste = Path().apply { moveTo(xc - arr, fTop); lineTo(xc + arr, fTop); lineTo(xc + abj, bottom); lineTo(xc - abj, bottom); close() }
        drawPath(fuste, Brush.horizontalGradient(listOf(som, luz, mar, osc, som), xc - abj, xc + abj))
        for (k in 1 until 6) {
            val f = k / 6f
            drawLine(som.copy(alpha = 0.65f), Offset(xc - arr + 2 * arr * f, fTop), Offset(xc - abj + 2 * abj * f, bottom), p * 0.6f)
        }
        // Los anillos bajo el equino.
        for (k in 0 until 3) drawLine(som, Offset(xc - arr, fTop - k * cw * 0.05f), Offset(xc + arr, fTop - k * cw * 0.05f), p * 0.5f)
        val eq = Path().apply {
            moveTo(xc - arr, fTop - cw * 0.12f); quadraticTo(xc - cw * 0.66f, fTop - cw * 0.16f, xc - cw * 0.66f, top + capH * 0.36f)
            lineTo(xc + cw * 0.66f, top + capH * 0.36f); quadraticTo(xc + cw * 0.66f, fTop - cw * 0.16f, xc + arr, fTop - cw * 0.12f); close()
        }
        drawPath(eq, Brush.verticalGradient(listOf(luz, osc), top, fTop))
        val ab = Rect(xc - cw * 0.7f, top, xc + cw * 0.7f, top + capH * 0.38f)
        rect(ab, vert(ab, luz, mar, osc))
        drawLine(som, Offset(ab.left, ab.bottom), Offset(ab.right, ab.bottom), p * 0.6f)
    }

    /** Una pata torneada de kliné: dado, anillo, bulbo, cuello, carrete y pie. */
    private fun DrawScope.pataTorneada(cx: Float, y0: Float, y1: Float, an: Float, mad: Color, luz: Color, osc: Color) {
        val hh = y1 - y0
        fun pieza(r: Rect, oval: Boolean) {
            val b = Brush.horizontalGradient(listOf(osc, luz, mad, osc), r.left, r.right)
            if (oval) drawOval(b, r.topLeft, r.size) else rect(r, b)
        }
        pieza(Rect(cx - an * 0.55f, y0, cx + an * 0.55f, y0 + hh * 0.16f), false)
        pieza(Rect(cx - an * 0.62f, y0 + hh * 0.14f, cx + an * 0.62f, y0 + hh * 0.22f), true)
        pieza(Rect(cx - an * 0.6f, y0 + hh * 0.2f, cx + an * 0.6f, y0 + hh * 0.46f), true)
        pieza(Rect(cx - an * 0.24f, y0 + hh * 0.44f, cx + an * 0.24f, y0 + hh * 0.6f), false)
        pieza(Rect(cx - an * 0.46f, y0 + hh * 0.56f, cx + an * 0.46f, y0 + hh * 0.7f), true)
        val pie = Path().apply { moveTo(cx - an * 0.3f, y0 + hh * 0.68f); lineTo(cx + an * 0.3f, y0 + hh * 0.68f); lineTo(cx + an * 0.16f, y1); lineTo(cx - an * 0.16f, y1); close() }
        drawPath(pie, Brush.horizontalGradient(listOf(osc, luz, mad, osc), cx - an * 0.3f, cx + an * 0.3f))
    }

    /** Una llama de lámpara con su halo; de noche el halo es grande. */
    private fun DrawScope.llama(o: Offset, r: Float, oscuro: Boolean) {
        mancha(o, r * (if (oscuro) 7f else 3f), LLAMA.copy(alpha = if (oscuro) 0.38f else 0.22f))
        drawPath(Path().apply {
            moveTo(o.x, o.y - r * 1.6f); quadraticTo(o.x + r * 0.9f, o.y - r * 0.2f, o.x, o.y + r * 0.5f); quadraticTo(o.x - r * 0.9f, o.y - r * 0.2f, o.x, o.y - r * 1.6f); close()
        }, Color(0xFFFFA733))
        drawOval(Color(0xFFFFF1C2), Offset(o.x - r * 0.3f, o.y - r * 0.6f), Size(r * 0.6f, r * 0.95f))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.SOCIAL)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val cTop = h * 0.04f
        val suelo = h * 0.962f
        val arqBajo = h * 0.155f

        // El muro de estuco rojo, con el filete púrpura y el zócalo azul del Egeo.
        val rojo = n(ROJO, o)
        drawRect(Brush.verticalGradient(listOf(mez(rojo, Color.Black, 0.25f), rojo, mez(rojo, Color.Black, 0.08f)), cTop, suelo), Offset(0f, cTop), Size(w, suelo - cTop))
        val rnd = Random(e.semilla.toLong() * 31 + 7)
        repeat(20) { mancha(Offset(w * rnd.nextFloat(), cTop + (suelo - cTop) * rnd.nextFloat()), (5f + 12f * rnd.nextFloat()) * u, Color.Black.copy(alpha = 0.06f)) }
        val zoc = ni.bottom + 4f * u
        rect(Rect(0f, zoc, w, zoc + 0.9f * u), n(PURPURA, o))
        rect(Rect(0f, zoc + 0.9f * u, w, suelo), vert(Rect(0f, zoc, w, suelo), n(EGEO, o), mez(n(EGEO, o), Color.Black, 0.3f)))

        // El piso: losas de mármol, con las juntas en perspectiva.
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(MARMOL_OSC, o), n(MARMOL_SOMBRA, o)))
        drawLine(n(MARMOL_LUZ, o), Offset(0f, suelo), Offset(w, suelo), p * 0.7f)
        var xj = w * 0.5f % (20f * u)
        while (xj < w) { drawLine(n(MARMOL_SOMBRA, o), Offset(xj, suelo), Offset(xj + (xj - w / 2) * 0.12f, h), p * 0.7f); xj += 20f * u }

        // Las lucernas encendidas de las mesitas: su halo cae sobre el muro (el panel lo tapa después).
        val yMesa = suelo - (suelo - ni.bottom) * 0.4f
        val lamparas = listOf(Offset(w * 0.245f, yMesa - 1.6f * u), Offset(w * 0.755f, yMesa - 1.6f * u))
        if (o) lamparas.forEach { mancha(it, 30f * u, LLAMA.copy(alpha = 0.22f)) }

        // El panel de mármol enmarcado: el nicho donde va tu gente.
        val marco = ni.inflate(2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size)
        rect(marco, Brush.linearGradient(listOf(n(MARMOL_LUZ, o), n(MARMOL, o), n(MARMOL_OSC, o)), marco.topLeft, marco.bottomRight))
        vetasMarmol(marco, n(MARMOL_SOMBRA, o).copy(alpha = 0.5f), e.semilla + 3, p, 6)
        rect(ni.inflate(0.7f * u), n(MARMOL_SOMBRA, o))
        rect(ni, nicho(e, o, MARMOL))
        vetasMarmol(ni, (if (o) Color.White else MARMOL_SOMBRA).copy(alpha = 0.06f), e.semilla, p, 4)
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent), ni.top, ni.top + 3f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c ->
            drawCircle(n(ORO, o), 1.1f * u, c); drawCircle(n(BRONCE_OSC, o), 1.1f * u, c, style = Stroke(p * 0.4f))
        }

        // El entablamento: antefijas, cornisa con greca, dentículos, ovas y el arquitrabe de tres fajas.
        val nAnt = (w / (12f * u)).toInt().coerceAtLeast(8)
        for (i in 0 until nAnt) palmeta(Offset(w * (i + 0.5f) / nAnt, cTop + 0.2f * u), cTop * 0.92f, n(TERRACOTA, o))
        val cor = Rect(0f, cTop, w, h * 0.08f)
        rect(cor, vert(cor, n(MARMOL_LUZ, o), n(MARMOL, o), n(MARMOL_OSC, o)))
        greca(Rect(0f, cor.top + cor.height * 0.2f, w, cor.bottom - cor.height * 0.18f), n(TERRACOTA, o), cor.height * 0.11f)
        drawLine(n(MARMOL_SOMBRA, o), Offset(0f, cor.bottom), Offset(w, cor.bottom), p * 0.7f)
        denticulos(Rect(0f, cor.bottom, w, h * 0.102f), o)
        ovas(Rect(0f, h * 0.102f, w, h * 0.124f), o, p)
        for (j in 0 until 3) {
            val y0 = h * 0.124f + (arqBajo - h * 0.124f) * j / 3f
            val fr = Rect(0f, y0, w, y0 + (arqBajo - h * 0.124f) / 3f)
            rect(fr, vert(fr, n(MARMOL_LUZ, o), n(if (j == 2) MARMOL_OSC else MARMOL, o)))
            drawLine(n(MARMOL_SOMBRA, o), Offset(0f, fr.bottom), Offset(w, fr.bottom), p * 0.5f)
        }
        rect(Rect(0f, arqBajo, w, arqBajo + 2.4f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), arqBajo, arqBajo + 2.4f * u))

        // Las dos columnas jónicas.
        val cw = minOf(w * 0.052f, 8.5f * u)
        listOf(w * 0.062f, w * 0.938f).forEach { columnaJonica(it, arqBajo, suelo, cw, o, p) }

        // Las dos klinai, con la cabecera hacia afuera; las mesitas delante y la crátera en medio.
        val yT = ni.bottom + 0.8f * u
        kline(w * 0.135f, w * 0.425f, yT, suelo, true, e, o, p)
        kline(w * 0.575f, w * 0.865f, yT, suelo, false, e, o, p)
        trapeza(w * 0.285f, yMesa, suelo, w * 0.125f, e, o, p, lamparas[0], uvas = true)
        trapeza(w * 0.715f, yMesa, suelo, w * 0.125f, e, o, p, lamparas[1], uvas = false)
        cratera(w * 0.5f, suelo, (suelo - yT) * 0.96f, minOf(w * 0.085f, (suelo - yT) * 0.95f), o, p)
    }

    /** Una kliné de frente: patas torneadas, larguero con taracea, colchón, manta, cojines y el fulcro de bronce. */
    private fun DrawScope.kline(x0: Float, x1: Float, yT: Float, yF: Float, izq: Boolean, e: EstadoMueble, o: Boolean, p: Float) {
        val l = x1 - x0; val hh = yF - yT
        val mad = n(NOGAL, o); val madL = n(NOGAL_LUZ, o); val madO = n(NOGAL_OSC, o)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x0 + l * 0.02f, yF - hh * 0.07f), Size(l * 0.96f, hh * 0.15f))
        val patW = l * 0.08f
        listOf(x0 + l * 0.07f, x1 - l * 0.07f).forEach { pataTorneada(it, yF - hh * 0.5f, yF, patW, mad, madL, madO) }
        // El larguero, con su banda de marfil y tachuelas de bronce.
        val lar = Rect(x0 + l * 0.015f, yF - hh * 0.6f, x1 - l * 0.015f, yF - hh * 0.47f)
        rect(lar, vert(lar, madL, mad, madO))
        vetas(lar, madO.copy(alpha = 0.5f), (x0 / 3).toInt(), p * 0.6f)
        val banda = Rect(lar.left + l * 0.1f, lar.top + lar.height * 0.32f, lar.right - l * 0.1f, lar.bottom - lar.height * 0.32f)
        rect(banda, n(MARMOL_LUZ, o))
        var xt = banda.left + banda.height
        while (xt < banda.right) { drawCircle(n(BRONCE, o), banda.height * 0.32f, Offset(xt, banda.center.y)); xt += banda.height * 2.4f }
        // El colchón, del color de acento, con sus rayas y el fleco.
        val tela = if (o) mez(e.acento, NOCHE, 0.3f) else e.acento
        val col = Rect(x0 + l * 0.03f, yF - hh * 0.8f, x1 - l * 0.03f, yF - hh * 0.58f)
        drawRoundRect(Brush.verticalGradient(listOf(mez(tela, Color.White, 0.28f), tela, mez(tela, Color.Black, 0.35f)), col.top, col.bottom), col.topLeft, col.size, CornerRadius(col.height * 0.45f))
        for (j in 1..2) drawLine(mez(tela, Color.White, 0.5f).copy(alpha = 0.6f), Offset(col.left + col.height * 0.5f, col.top + col.height * j / 3f), Offset(col.right - col.height * 0.5f, col.top + col.height * j / 3f), p * 0.6f)
        var xf = col.left + col.height * 0.4f
        while (xf < col.right - col.height * 0.4f) { drawLine(mez(tela, Color.Black, 0.4f), Offset(xf, col.bottom - p), Offset(xf, col.bottom + hh * 0.05f), p * 0.5f); xf += p * 2.2f }
        // La manta doblada a los pies, del color secundario con su greca.
        val pies = if (izq) Rect(col.right - l * 0.24f, col.top - hh * 0.06f, col.right - l * 0.04f, col.bottom + hh * 0.02f) else Rect(col.left + l * 0.04f, col.top - hh * 0.06f, col.left + l * 0.24f, col.bottom + hh * 0.02f)
        val manta = if (o) mez(e.secundario, NOCHE, 0.35f) else e.secundario
        drawRoundRect(vert(pies, mez(manta, Color.White, 0.25f), manta, mez(manta, Color.Black, 0.3f)), pies.topLeft, pies.size, CornerRadius(pies.height * 0.3f))
        drawLine(mez(manta, Color.White, 0.6f), Offset(pies.left + p, pies.top + pies.height * 0.3f), Offset(pies.right - p, pies.top + pies.height * 0.3f), p * 0.6f)
        drawLine(mez(manta, Color.White, 0.6f), Offset(pies.left + p, pies.top + pies.height * 0.42f), Offset(pies.right - p, pies.top + pies.height * 0.42f), p * 0.4f)
        // El fulcro de bronce en la cabecera, con su medallón, y los dos cojines apoyados en él.
        val hx = if (izq) x0 else x1; val sg = if (izq) 1f else -1f
        val ful = Path().apply { moveTo(hx + sg * l * 0.05f, col.bottom); cubicTo(hx + sg * l * 0.01f, col.top - hh * 0.12f, hx + sg * l * 0.05f, yT, hx + sg * l * 0.15f, yT + hh * 0.07f) }
        drawPath(ful, n(BRONCE_OSC, o), style = Stroke(hh * 0.12f, cap = StrokeCap.Round))
        drawPath(ful, n(BRONCE, o), style = Stroke(hh * 0.08f, cap = StrokeCap.Round))
        drawPath(ful, n(BRONCE_LUZ, o).copy(alpha = 0.8f), style = Stroke(hh * 0.025f, cap = StrokeCap.Round))
        val med = Offset(hx + sg * l * 0.045f, col.bottom - hh * 0.02f)
        drawCircle(n(BRONCE_OSC, o), hh * 0.075f, med); drawCircle(n(BRONCE_LUZ, o), hh * 0.05f, med); drawCircle(n(BRONCE, o), hh * 0.03f, med)
        listOf(1, 0).forEach { k ->
            val cojin = if (k == 0) n(Color(0xFFD9A441), o) else mez(n(MARMOL_LUZ, o), e.acento, 0.12f)
            val cx = hx + sg * l * (0.14f + 0.11f * k)
            val r = Rect(cx - l * 0.085f, yT + hh * (0.04f + 0.08f * k), cx + l * 0.085f, col.top + col.height * 0.6f)
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + p, r.top + p * 1.5f), r.size, CornerRadius(r.height * 0.45f))
            drawRoundRect(Brush.radialGradient(listOf(mez(cojin, Color.White, 0.35f), cojin, mez(cojin, Color.Black, 0.35f)), Offset(r.center.x - r.width * 0.15f, r.center.y - r.height * 0.25f), r.width * 0.75f), r.topLeft, r.size, CornerRadius(r.height * 0.45f))
            val raya = if (k == 0) mez(cojin, Color.Black, 0.35f) else e.acento.copy(alpha = 0.8f)
            drawLine(raya, Offset(r.left + r.width * 0.15f, r.top + r.height * 0.38f), Offset(r.right - r.width * 0.15f, r.top + r.height * 0.38f), p * 0.6f)
            drawLine(raya, Offset(r.left + r.width * 0.15f, r.top + r.height * 0.62f), Offset(r.right - r.width * 0.15f, r.top + r.height * 0.62f), p * 0.6f)
            listOf(r.left + r.width * 0.06f, r.right - r.width * 0.06f).forEach { x -> drawCircle(n(ORO, o), p * 0.9f, Offset(x, r.bottom - r.height * 0.12f)) }
        }
    }

    /** La trapeza: la mesita baja delante de la kliné, con el kílix, la lucerna encendida y unas uvas. */
    private fun DrawScope.trapeza(cx: Float, yT: Float, yF: Float, ancho: Float, e: EstadoMueble, o: Boolean, p: Float, lampara: Offset, uvas: Boolean) {
        val mad = n(NOGAL, o); val madL = n(NOGAL_LUZ, o); val madO = n(NOGAL_OSC, o)
        val hh = yF - yT
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho * 0.55f, yF - hh * 0.12f), Size(ancho * 1.1f, hh * 0.22f))
        listOf(-0.4f, 0.4f).forEach { f ->
            val x = cx + ancho * f
            drawPath(Path().apply { moveTo(x - ancho * 0.05f, yT); lineTo(x + ancho * 0.05f, yT); lineTo(x + ancho * 0.03f + f * ancho * 0.1f, yF); lineTo(x - ancho * 0.05f + f * ancho * 0.1f, yF); close() },
                Brush.horizontalGradient(listOf(madL, madO), x - ancho * 0.05f, x + ancho * 0.05f))
        }
        val tab = Rect(cx - ancho / 2, yT, cx + ancho / 2, yT + hh * 0.16f)
        rect(tab, vert(tab, madL, mad, madO))
        // El kílix: la copa ancha de barniz negro, con su pie.
        val kx = cx - ancho * 0.18f
        val kw = ancho * 0.42f; val kh = hh * 0.3f
        drawPath(Path().apply { moveTo(kx - kw * 0.08f, yT); lineTo(kx + kw * 0.08f, yT); lineTo(kx + kw * 0.04f, yT - kh * 0.4f); lineTo(kx - kw * 0.04f, yT - kh * 0.4f); close() }, n(BARNIZ, o))
        drawPath(Path().apply { moveTo(kx - kw / 2, yT - kh); quadraticTo(kx, yT - kh * 0.05f, kx + kw / 2, yT - kh); close() }, Brush.horizontalGradient(listOf(n(BARNIZ, o), mez(n(BARNIZ, o), Color.White, 0.25f), n(BARNIZ, o)), kx - kw / 2, kx + kw / 2))
        drawLine(n(TERRACOTA, o), Offset(kx - kw * 0.42f, yT - kh * 0.86f), Offset(kx + kw * 0.42f, yT - kh * 0.86f), p * 0.5f)
        listOf(-1f, 1f).forEach { s -> drawOval(n(BARNIZ, o), Offset(kx + s * kw * 0.5f - kw * 0.08f, yT - kh * 1.05f), Size(kw * 0.16f, kh * 0.2f), style = Stroke(p * 0.6f)) }
        // La lucerna de barro con su llama.
        val lx = lampara.x; val ly = yT
        drawOval(Brush.verticalGradient(listOf(n(TERRA_LUZ, o), n(TERRA_OSC, o)), ly - hh * 0.22f, ly), Offset(lx - ancho * 0.16f, ly - hh * 0.2f), Size(ancho * 0.3f, hh * 0.2f))
        drawPath(Path().apply { moveTo(lx + ancho * 0.08f, ly - hh * 0.16f); lineTo(lx + ancho * 0.2f, ly - hh * 0.13f); lineTo(lx + ancho * 0.2f, ly - hh * 0.07f); lineTo(lx + ancho * 0.08f, ly - hh * 0.05f); close() }, n(TERRA_OSC, o))
        drawOval(n(BARNIZ, o), Offset(lx - ancho * 0.07f, ly - hh * 0.2f), Size(ancho * 0.12f, hh * 0.06f))
        llama(Offset(lx + ancho * 0.2f, ly - hh * 0.18f), hh * 0.06f, o)
        // Uvas o pan.
        if (uvas) {
            for (k in 0 until 6) {
                val gx = cx + ancho * (0.28f + 0.06f * (k % 3)) - (k / 3) * ancho * 0.03f
                val gy = yT - hh * (0.05f + 0.08f * (k / 3))
                drawCircle(n(UVA, o), hh * 0.05f, Offset(gx, gy)); drawCircle(Color.White.copy(alpha = 0.3f), hh * 0.016f, Offset(gx - hh * 0.015f, gy - hh * 0.015f))
            }
            drawLine(n(OLIVO, o), Offset(cx + ancho * 0.34f, yT - hh * 0.21f), Offset(cx + ancho * 0.4f, yT - hh * 0.27f), p * 0.6f)
        } else {
            val pan = Rect(cx + ancho * 0.18f, yT - hh * 0.14f, cx + ancho * 0.46f, yT)
            drawOval(Brush.verticalGradient(listOf(n(Color(0xFFD9A35E), o), n(Color(0xFF9A6328), o)), pan.top, pan.bottom), pan.topLeft, pan.size)
            drawLine(n(Color(0xFF7A4A1C), o), Offset(pan.left + pan.width * 0.3f, pan.top + pan.height * 0.3f), Offset(pan.right - pan.width * 0.3f, pan.top + pan.height * 0.3f), p * 0.5f)
        }
    }

    /** La crátera de cáliz, de figuras rojas: el pie, la panza con lengüetas, la franja de greca y el borde. */
    private fun DrawScope.cratera(cx: Float, yF: Float, alto: Float, ancho: Float, o: Boolean, p: Float) {
        val bar = n(BARNIZ, o); val ter = n(TERRACOTA, o)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho * 0.4f, yF - alto * 0.05f), Size(ancho * 0.8f, alto * 0.1f))
        drawPath(Path().apply { moveTo(cx - ancho * 0.26f, yF); lineTo(cx + ancho * 0.26f, yF); lineTo(cx + ancho * 0.1f, yF - alto * 0.12f); lineTo(cx - ancho * 0.1f, yF - alto * 0.12f); close() }, ter)
        rect(Rect(cx - ancho * 0.08f, yF - alto * 0.22f, cx + ancho * 0.08f, yF - alto * 0.11f), bar)
        val cuerpo = Path().apply {
            moveTo(cx - ancho * 0.14f, yF - alto * 0.2f)
            cubicTo(cx - ancho * 0.38f, yF - alto * 0.26f, cx - ancho * 0.36f, yF - alto * 0.48f, cx - ancho * 0.36f, yF - alto * 0.5f)
            lineTo(cx - ancho * 0.5f, yF - alto * 0.94f); lineTo(cx + ancho * 0.5f, yF - alto * 0.94f)
            lineTo(cx + ancho * 0.36f, yF - alto * 0.5f)
            cubicTo(cx + ancho * 0.36f, yF - alto * 0.48f, cx + ancho * 0.38f, yF - alto * 0.26f, cx + ancho * 0.14f, yF - alto * 0.2f); close()
        }
        drawPath(cuerpo, bar)
        clipPath(cuerpo) {
            // La parte baja de terracota con lengüetas de barniz; arriba, la escena: una banda de figuras rojas.
            rect(Rect(cx - ancho, yF - alto * 0.5f, cx + ancho, yF - alto * 0.18f), ter)
            for (k in -3..3) drawLine(bar, Offset(cx + k * ancho * 0.09f, yF - alto * 0.48f), Offset(cx + k * ancho * 0.07f, yF - alto * 0.3f), ancho * 0.035f)
            val f = Rect(cx - ancho, yF - alto * 0.86f, cx + ancho, yF - alto * 0.58f)
            for (k in -2..2) {
                val fx = cx + k * ancho * 0.2f
                drawCircle(ter, f.height * 0.13f, Offset(fx, f.top + f.height * 0.2f))
                drawPath(Path().apply { moveTo(fx - f.height * 0.15f, f.bottom); lineTo(fx, f.top + f.height * 0.32f); lineTo(fx + f.height * 0.15f, f.bottom); close() }, ter)
            }
            greca(Rect(cx - ancho * 0.6f, yF - alto * 0.56f, cx + ancho * 0.6f, yF - alto * 0.5f), ter, alto * 0.012f)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.22f), Color.Transparent), cx - ancho * 0.3f, cx), Offset(cx - ancho * 0.3f, yF - alto), Size(ancho * 0.3f, alto))
        }
        rect(Rect(cx - ancho * 0.54f, yF - alto, cx + ancho * 0.54f, yF - alto * 0.93f), vert(Rect(0f, yF - alto, 0f, yF - alto * 0.93f), n(TERRA_LUZ, o), ter))
        listOf(-1f, 1f).forEach { s -> drawOval(bar, Offset(cx + s * ancho * 0.4f - ancho * 0.1f, yF - alto * 0.5f), Size(ancho * 0.2f, alto * 0.12f), style = Stroke(p * 1.1f)) }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.MAPAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val faldonA = h * 0.8f; val faldonB = h * 0.875f; val suelo = h * 0.955f

        // El muro rojo con el zócalo azul detrás de las patas, y el piso de baldosas de barro.
        val rojo = n(ROJO, o)
        rect(Rect(0f, 0f, w, suelo), vert(Rect(0f, 0f, w, suelo), mez(rojo, Color.Black, 0.2f), rojo))
        rect(Rect(0f, faldonA, w, suelo), vert(Rect(0f, faldonA, w, suelo), n(EGEO, o), mez(n(EGEO, o), Color.Black, 0.35f)))
        rect(Rect(0f, faldonA - 0.8f * u, w, faldonA), n(PURPURA, o))
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(TERRA_LUZ, o), n(TERRA_OSC, o)))
        var xj = (w / 2) % (16f * u)
        while (xj < w) { drawLine(n(TERRA_OSC, o), Offset(xj, suelo), Offset(xj + (xj - w / 2) * 0.15f, h), p * 0.7f); xj += 16f * u }

        // El tablero de nogal: canto oscuro, moldura clara, la cara con su veta y un filete de bronce.
        val t = Rect(w * 0.02f, h * 0.025f, w * 0.98f, faldonA)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(t.left, t.top + 1.2f * u), t.size, CornerRadius(1.5f * u))
        drawRoundRect(n(NOGAL_OSC, o), t.topLeft, t.size, CornerRadius(1.5f * u))
        val cara = t.deflate(1.3f * u)
        rect(cara, Brush.linearGradient(listOf(n(NOGAL_LUZ, o), n(NOGAL, o), mez(n(NOGAL, o), n(NOGAL_OSC, o), 0.5f)), cara.topLeft, cara.bottomRight))
        vetas(cara, n(NOGAL_OSC, o).copy(alpha = 0.5f), e.semilla, p, 0.9f)
        drawRect(n(NOGAL_LUZ, o).copy(alpha = 0.6f), cara.topLeft, cara.size, style = Stroke(p * 0.8f))
        val filete = cara.deflate(2.2f * u)
        drawRect(n(BRONCE, o), filete.topLeft, filete.size, style = Stroke(p * 0.7f))

        // La lucerna encendida (su halo, antes que el papiro).
        val margen = ni.left - t.left
        val lampC = Offset(t.left + margen * 0.42f, ni.top + (ni.height) * 0.2f)
        mancha(lampC, (if (o) 34f else 14f) * u, LLAMA.copy(alpha = if (o) 0.32f else 0.14f))

        // El rollo de papiro abierto: la hoja es el nicho; a cada lado, el rollo con sus botones.
        val rw = 3.4f * u
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ni.left - rw + 0.8f * u, ni.top + 1.4f * u), Size(ni.width + 2 * rw, ni.height))
        rect(ni, nicho(e, o, PAPIRO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val fibra = (if (o) Color.White else Color(0xFF8A6A3A)).copy(alpha = 0.05f)
            var yf = ni.top + 1.6f * u
            while (yf < ni.bottom) { drawLine(fibra, Offset(ni.left, yf), Offset(ni.right, yf), p * 0.6f); yf += 2.2f * u }
            for (k in 1..3) { val xk = ni.left + ni.width * k / 4f; drawLine(fibra.copy(alpha = 0.08f), Offset(xk, ni.top), Offset(xk, ni.bottom), p) }
        }
        listOf(ni.left - rw, ni.right).forEach { x0 ->
            val r = Rect(x0, ni.top - 1.2f * u, x0 + rw, ni.bottom + 1.2f * u)
            val pap = if (o) mez(PAPIRO, NOCHE, 0.55f) else PAPIRO
            rect(r, horiz(r, mez(pap, Color.Black, 0.35f), pap, mez(pap, Color.White, 0.3f), mez(pap, Color.Black, 0.25f)))
            var yv = r.top + 2f * u
            while (yv < r.bottom - u) { drawLine(mez(pap, Color.Black, 0.3f), Offset(r.left, yv), Offset(r.right, yv + 0.9f * u), p * 0.4f); yv += 3.2f * u }
            listOf(r.top, r.bottom).forEach { y ->
                drawOval(n(NOGAL_OSC, o), Offset(r.center.x - rw * 0.42f, y - 1.2f * u), Size(rw * 0.84f, 2.4f * u))
                drawOval(Brush.radialGradient(listOf(mez(e.acento, Color.White, 0.3f), e.acento), Offset(r.center.x, y), rw * 0.5f), Offset(r.center.x - rw * 0.3f, y - 0.9f * u), Size(rw * 0.6f, 1.8f * u))
            }
        }

        // Los instrumentos alrededor: la lucerna, las dracmas, el compás, el tintero con el cálamo, la regla y otro rollo.
        val izq0 = t.left + 1.8f * u; val izq1 = ni.left - rw - 1f * u
        val der0 = ni.right + rw + 1f * u; val der1 = t.right - 1.8f * u
        val ma = minOf(izq1 - izq0, 12f * u)
        lucerna(lampC, ma * 0.5f, o, p)
        // Tres dracmas.
        listOf(0f to 0f, 0.38f to 0.22f, 0.12f to 0.5f).forEachIndexed { k, (dx, dy) ->
            val c = Offset(izq0 + ma * (0.32f + dx), ni.top + ni.height * 0.66f + ma * dy)
            moneda(c, ma * 0.27f, o, p, k)
        }
        // El compás de bronce, abierto.
        val bisagra = Offset((der0 + der1) / 2 - ma * 0.1f, ni.top + ni.height * 0.08f)
        listOf(-0.32f, 0.22f).forEach { dx ->
            val punta = Offset(bisagra.x + ma * dx, bisagra.y + ni.height * 0.36f)
            drawLine(n(BRONCE_OSC, o), bisagra, punta, p * 1.8f, StrokeCap.Round)
            drawLine(n(BRONCE_LUZ, o), Offset(bisagra.x + p * 0.4f, bisagra.y), Offset(punta.x + p * 0.4f, punta.y - p), p * 0.6f, StrokeCap.Round)
        }
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(bisagra.x + u, bisagra.y + u), Offset(bisagra.x + ma * 0.25f, bisagra.y + ni.height * 0.37f), p * 1.4f)
        drawCircle(n(BRONCE, o), p * 2.6f, bisagra); drawCircle(n(BRONCE_LUZ, o), p * 1.1f, Offset(bisagra.x - p * 0.5f, bisagra.y - p * 0.5f))
        // El tintero de barniz negro y el cálamo.
        val tint = Offset((der0 + der1) / 2, ni.top + ni.height * 0.72f)
        drawCircle(Color.Black.copy(alpha = 0.3f), ma * 0.36f, Offset(tint.x + u * 0.6f, tint.y + u))
        drawCircle(Brush.radialGradient(listOf(mez(n(BARNIZ, o), Color.White, 0.3f), n(BARNIZ, o)), Offset(tint.x - ma * 0.1f, tint.y - ma * 0.1f), ma * 0.4f), ma * 0.34f, tint)
        drawCircle(n(TERRACOTA, o), ma * 0.34f, tint, style = Stroke(p * 0.7f))
        drawCircle(Color(0xFF050302), ma * 0.15f, tint)
        val cal0 = Offset(tint.x - ma * 0.5f, tint.y + ma * 0.75f); val cal1 = Offset(tint.x + ma * 0.05f, tint.y - ma * 0.02f)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(cal0.x + u * 0.5f, cal0.y + u * 0.8f), Offset(cal1.x + u * 0.5f, cal1.y + u * 0.8f), p * 1.6f)
        drawLine(n(Color(0xFFC9B27A), o), cal0, cal1, p * 1.6f, StrokeCap.Round)
        drawLine(n(Color(0xFF8A7444), o), Offset(cal0.x + (cal1.x - cal0.x) * 0.4f, cal0.y + (cal1.y - cal0.y) * 0.4f), Offset(cal0.x + (cal1.x - cal0.x) * 0.44f, cal0.y + (cal1.y - cal0.y) * 0.44f), p * 2f)
        // La regla de bronce y el rollo atado, en el borde de abajo.
        val yb = (ni.bottom + 1.2f * u + filete.bottom) / 2
        val regla = Rect(ni.left + ni.width * 0.06f, yb - 1.1f * u, ni.left + ni.width * 0.42f, yb + 1.1f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(regla.left + 0.5f * u, regla.top + 0.8f * u), regla.size)
        rect(regla, vert(regla, n(BRONCE_LUZ, o), n(BRONCE, o), n(BRONCE_OSC, o)))
        var xr = regla.left + u
        var kr = 0
        while (xr < regla.right) { drawLine(n(BRONCE_OSC, o), Offset(xr, regla.top), Offset(xr, regla.top + regla.height * (if (kr % 5 == 0) 0.7f else 0.4f)), p * 0.4f); xr += 1.3f * u; kr++ }
        val rollo = Rect(ni.left + ni.width * 0.62f, yb - 1.5f * u, ni.left + ni.width * 0.92f, yb + 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(rollo.left + 0.5f * u, rollo.top + 0.8f * u), rollo.size, CornerRadius(rollo.height / 2))
        val pap = if (o) mez(PAPIRO, NOCHE, 0.55f) else PAPIRO
        drawRoundRect(vert(rollo, mez(pap, Color.White, 0.3f), pap, mez(pap, Color.Black, 0.35f)), rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
        drawOval(mez(pap, Color.Black, 0.3f), Offset(rollo.right - rollo.height * 0.6f, rollo.top), Size(rollo.height * 0.6f, rollo.height), style = Stroke(p * 0.5f))
        listOf(0.35f, 0.42f).forEach { f -> drawLine(n(TERRACOTA, o), Offset(rollo.left + rollo.width * f, rollo.top - p * 0.3f), Offset(rollo.left + rollo.width * f, rollo.bottom + p * 0.3f), p * 0.9f) }
        drawLine(n(TERRACOTA, o), Offset(rollo.left + rollo.width * 0.39f, rollo.bottom), Offset(rollo.left + rollo.width * 0.3f, rollo.bottom + 1.6f * u), p * 0.7f)

        // El faldón: moldura, la greca de marfil sobre nogal oscuro y las tachuelas de bronce.
        val fa = Rect(t.left + 1.2f * u, faldonA, t.right - 1.2f * u, faldonB)
        rect(fa, vert(fa, n(NOGAL, o), n(NOGAL_OSC, o)))
        drawLine(n(NOGAL_LUZ, o), Offset(fa.left, fa.top + p), Offset(fa.right, fa.top + p), p * 0.8f)
        val gr = Rect(fa.left + fa.width * 0.18f, fa.top + fa.height * 0.24f, fa.right - fa.width * 0.18f, fa.bottom - fa.height * 0.3f)
        greca(gr, n(MARMOL, o), gr.height * 0.13f)
        var xt = fa.left + 2.4f * u
        while (xt < fa.right - u) {
            if (xt < gr.left - u || xt > gr.right + u) { drawCircle(n(BRONCE, o), 0.9f * u, Offset(xt, fa.center.y)); drawCircle(n(BRONCE_LUZ, o), 0.35f * u, Offset(xt - 0.25f * u, fa.center.y - 0.25f * u)) }
            xt += 3.2f * u
        }
        // Las patas de león.
        val an = minOf(w * 0.05f, 9f * u)
        listOf(fa.left + an * 0.9f, w / 2, fa.right - an * 0.9f).forEachIndexed { k, x -> pataLeon(x, faldonB, suelo, an, if (k == 0) -1f else 1f, o, p) }
    }

    /** Una lucerna griega vista desde arriba: el disco con su orificio, el pico con la llama y el asa. */
    private fun DrawScope.lucerna(c: Offset, r: Float, o: Boolean, p: Float) {
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - r + p, c.y - r * 0.8f + p * 2), Size(r * 2.6f, r * 1.6f))
        drawPath(Path().apply { moveTo(c.x + r * 0.5f, c.y - r * 0.35f); lineTo(c.x + r * 1.5f, c.y - r * 0.18f); quadraticTo(c.x + r * 1.75f, c.y, c.x + r * 1.5f, c.y + r * 0.18f); lineTo(c.x + r * 0.5f, c.y + r * 0.35f); close() }, n(TERRA_OSC, o))
        drawCircle(Brush.radialGradient(listOf(n(TERRA_LUZ, o), n(TERRACOTA, o), n(TERRA_OSC, o)), Offset(c.x - r * 0.3f, c.y - r * 0.3f), r * 1.2f), r * 0.8f, c)
        drawCircle(n(BARNIZ, o), r * 0.5f, c)
        drawCircle(n(TERRACOTA, o), r * 0.5f, c, style = Stroke(p * 0.5f))
        drawCircle(Color(0xFF050302), r * 0.14f, c)
        drawArc(n(TERRA_OSC, o), 120f, 120f, false, Offset(c.x - r * 1.25f, c.y - r * 0.4f), Size(r * 0.8f, r * 0.8f), style = Stroke(p * 1.2f))
        llama(Offset(c.x + r * 1.55f, c.y - r * 0.15f), r * 0.32f, o)
    }

    /** Una dracma de plata con la lechuza. */
    private fun DrawScope.moneda(c: Offset, r: Float, o: Boolean, p: Float, k: Int) {
        val plata = n(PLATA, o)
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + p * 0.6f, c.y + p))
        drawCircle(Brush.radialGradient(listOf(mez(plata, Color.White, 0.5f), plata, mez(plata, Color.Black, 0.35f)), Offset(c.x - r * 0.4f, c.y - r * 0.4f), r * 1.6f), r, c)
        drawCircle(mez(plata, Color.Black, 0.3f), r * 0.82f, c, style = Stroke(p * 0.4f))
        val g = mez(plata, Color.Black, 0.45f)
        drawOval(g, Offset(c.x - r * 0.28f, c.y - r * 0.4f), Size(r * 0.56f, r * 0.78f), style = Stroke(p * 0.45f))
        listOf(-1f, 1f).forEach { s -> drawCircle(g, r * 0.11f, Offset(c.x + s * r * 0.13f, c.y - r * 0.2f), style = Stroke(p * 0.35f)) }
        if (k == 1) drawLine(g, Offset(c.x + r * 0.35f, c.y - r * 0.45f), Offset(c.x + r * 0.45f, c.y + r * 0.3f), p * 0.4f)
    }

    /** Una pata de león de nogal, con el anillo de bronce arriba y la garra sobre el piso. */
    private fun DrawScope.pataLeon(cx: Float, y0: Float, y1: Float, an: Float, sg: Float, o: Boolean, p: Float) {
        val hh = y1 - y0
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - an * 0.8f, y1 - hh * 0.08f), Size(an * 1.6f, hh * 0.16f))
        val pata = Path().apply {
            moveTo(cx - an * 0.5f, y0); lineTo(cx + an * 0.5f, y0)
            cubicTo(cx + an * 0.62f + sg * an * 0.1f, y0 + hh * 0.35f, cx + an * 0.12f, y0 + hh * 0.5f, cx + an * 0.34f, y0 + hh * 0.84f)
            lineTo(cx - an * 0.36f, y0 + hh * 0.84f)
            cubicTo(cx - an * 0.2f, y0 + hh * 0.58f, cx - an * 0.62f + sg * an * 0.1f, y0 + hh * 0.35f, cx - an * 0.5f, y0); close()
        }
        drawPath(pata, Brush.horizontalGradient(listOf(n(NOGAL_OSC, o), n(NOGAL_LUZ, o), n(NOGAL, o), n(NOGAL_OSC, o)), cx - an * 0.6f, cx + an * 0.6f))
        drawLine(n(NOGAL_OSC, o), Offset(cx + an * 0.1f, y0 + hh * 0.2f), Offset(cx - an * 0.05f, y0 + hh * 0.7f), p * 0.6f)
        rect(Rect(cx - an * 0.56f, y0, cx + an * 0.56f, y0 + hh * 0.12f), vert(Rect(0f, y0, 0f, y0 + hh * 0.12f), n(BRONCE_LUZ, o), n(BRONCE_OSC, o)))
        for (j in -1..1) {
            val tx = cx + j * an * 0.25f
            drawOval(Brush.verticalGradient(listOf(n(NOGAL_LUZ, o), n(NOGAL_OSC, o)), y0 + hh * 0.78f, y1), Offset(tx - an * 0.16f, y0 + hh * 0.78f), Size(an * 0.32f, hh * 0.22f))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.OTRAS_VIDAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val cTop = h * 0.05f
        val mar = n(MARMOL, o); val luz = n(MARMOL_LUZ, o); val osc = n(MARMOL_OSC, o); val som = n(MARMOL_SOMBRA, o)

        // El muro de las columnas a los lados del nicho.
        rect(Rect(0f, cTop, w, ni.bottom), vert(Rect(0f, cTop, w, ni.bottom), osc, mar))
        // Las cinco hornacinas: el fondo sólido, la sombra de su hondura y una peana al pie.
        rect(ni, nicho(e, o, MARMOL))
        val cw = ni.width / 5
        val dv = (cw - 47f * density).coerceIn(3f * density, cw * 0.12f)
        for (i in 0 until 5) {
            val c = Rect(ni.left + cw * i + (if (i == 0) 0f else dv / 2), ni.top, ni.left + cw * (i + 1) - (if (i == 4) 0f else dv / 2), ni.bottom)
            rect(Rect(c.left, c.top, c.right, c.top + 6f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.14f), Color.Transparent), c.top, c.top + 6f * u))
            rect(Rect(c.left, c.top, c.left + 2.5f * u, c.bottom), Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), c.left, c.left + 2.5f * u))
            rect(Rect(c.right - 2.5f * u, c.top, c.right, c.bottom), Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.06f)), c.right - 2.5f * u, c.right))
            vetasMarmol(c, (if (o) Color.White else MARMOL_SOMBRA).copy(alpha = 0.06f), e.semilla + i, p, 2)
            val peana = Rect(c.left + c.width * 0.18f, ni.bottom - 4.2f * u, c.right - c.width * 0.18f, ni.bottom)
            rect(peana, vert(peana, luz, osc))
            drawLine(som, Offset(peana.left, peana.top + 1.2f * u), Offset(peana.right, peana.top + 1.2f * u), p * 0.5f)
            rect(Rect(peana.left - u, peana.top - 0.8f * u, peana.right + u, peana.top), vert(Rect(0f, peana.top - 0.8f * u, 0f, peana.top), luz, som))
        }
        // Las pilastras entre las hornacinas, estriadas, con su capitel y su basa.
        for (i in 1 until 5) {
            val x = ni.left + cw * i
            val r = Rect(x - dv / 2, ni.top, x + dv / 2, ni.bottom)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.right, r.top), Size(1.2f * u, r.height))
            rect(r, horiz(r, luz, mar, osc))
            drawLine(som.copy(alpha = 0.7f), Offset(x, r.top + 3f * u), Offset(x, r.bottom - 3f * u), p * 0.5f)
            rect(Rect(r.left - 0.8f * u, r.top, r.right + 0.8f * u, r.top + 2.2f * u), vert(Rect(0f, r.top, 0f, r.top + 2.2f * u), luz, som))
            rect(Rect(r.left - 0.8f * u, r.bottom - 2f * u, r.right + 0.8f * u, r.bottom), vert(Rect(0f, r.bottom - 2f * u, 0f, r.bottom), luz, som))
        }

        // Las columnas dóricas de las esquinas.
        val cwO = minOf(ni.left * 0.6f, 9f * u)
        listOf(ni.left * 0.48f, w - ni.left * 0.48f).forEach { columnaDorica(it, ni.top, ni.bottom, cwO, o, p) }

        // El entablamento: antefijas, cornisa con greca, mútulos, friso de triglifos y metopas, régula con gotas y arquitrabe.
        val nAnt = (w / (12f * u)).toInt().coerceAtLeast(8)
        for (i in 0 until nAnt) palmeta(Offset(w * (i + 0.5f) / nAnt, cTop + 0.2f * u), cTop * 0.95f, n(TERRACOTA, o))
        val cor = Rect(0f, cTop, w, h * 0.095f)
        rect(cor, vert(cor, luz, mar, osc))
        greca(Rect(0f, cor.top + cor.height * 0.2f, w, cor.bottom - cor.height * 0.18f), n(TERRACOTA, o), cor.height * 0.11f)
        denticulos(Rect(0f, cor.bottom, w, h * 0.112f), o)
        val fr = Rect(0f, h * 0.112f, w, h * 0.182f)
        rect(fr, vert(fr, luz, mar))
        val ejes = mutableListOf<Float>()
        for (i in 0..5) ejes += ni.left + cw * i
        for (i in 0 until 5) ejes += ni.left + cw * (i + 0.5f)
        ejes.sort()
        val tw = minOf(cw * 0.22f, fr.height * 0.8f)
        val todos = listOf(tw / 2) + ejes + listOf(w - tw / 2)
        // Las metopas: una roseta entre cada par de triglifos.
        todos.zipWithNext().forEachIndexed { k, (x0, x1) ->
            val ancho = x1 - x0 - tw
            if (ancho > fr.height * 0.4f) {
                val c = Offset((x0 + x1) / 2, fr.center.y)
                val rr = minOf(ancho, fr.height) * 0.3f
                val col = if (k % 2 == 0) e.acento else e.secundario
                roseta(c, rr, n(if (o) mez(col, Color.White, 0.15f) else col, o), n(ORO, o))
            }
        }
        todos.forEach { x ->
            val tr = Rect(x - tw / 2, fr.top + p * 0.5f, x + tw / 2, fr.bottom)
            rect(tr, horiz(tr, n(Color(0xFF263646), o), n(Color(0xFF34495E), o)))
            for (g in 1..2) drawLine(Color.Black.copy(alpha = 0.55f), Offset(tr.left + tr.width * g / 3, tr.top + tr.height * 0.12f), Offset(tr.left + tr.width * g / 3, tr.bottom), tr.width * 0.12f)
        }
        val reg = Rect(0f, fr.bottom, w, fr.bottom + 1.2f * u)
        rect(reg, vert(reg, luz, osc))
        todos.forEach { x -> for (g in -1..1) drawCircle(som, 0.5f * u, Offset(x + g * tw * 0.3f, reg.bottom + 0.4f * u)) }
        val arq = Rect(0f, reg.bottom, w, ni.top)
        rect(arq, vert(arq, mar, osc))
        drawLine(luz, Offset(0f, arq.top + p), Offset(w, arq.top + p), p * 0.5f)
        rect(Rect(0f, ni.top, w, ni.top + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent), ni.top, ni.top + 2f * u))

        // El estilóbato: tres gradas de mármol, cada una más ancha.
        val gh = (h - ni.bottom) / 3f
        for (k in 0 until 3) {
            val y0 = ni.bottom + gh * k
            val m = w * (0.02f - 0.01f * k)
            val g = Rect(m, y0, w - m, y0 + gh)
            rect(g, vert(g, luz, mar, osc))
            drawLine(som, Offset(g.left, g.bottom - p * 0.4f), Offset(g.right, g.bottom - p * 0.4f), p * 0.8f)
            var xj = g.left + w * 0.09f * (1 + k % 2) / 2
            while (xj < g.right) { drawLine(som.copy(alpha = 0.6f), Offset(xj, g.top + p), Offset(xj, g.bottom - p), p * 0.5f); xj += w * 0.09f }
            vetasMarmol(g, som.copy(alpha = 0.35f), e.semilla + 20 + k, p, 4)
        }
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2, h / 2)
        val s = h * 0.43f
        val p = s * 0.035f
        val o = oscuro
        val mar = n(MARMOL, o); val luz = n(MARMOL_LUZ, o); val osc = n(MARMOL_OSC, o); val som = n(MARMOL_SOMBRA, o)
        when (id) {
            "unesco" -> {
                // El Partenón sobre la roca de la Acrópolis, con un olivo y el sol.
                drawCircle(Brush.radialGradient(listOf(n(ORO, o).copy(alpha = 0.5f), Color.Transparent), Offset(c.x - s * 1.55f, c.y - s * 0.55f), s * 0.6f), s * 0.6f, Offset(c.x - s * 1.55f, c.y - s * 0.55f))
                solVergina(Offset(c.x - s * 1.55f, c.y - s * 0.55f), s * 0.25f, n(ORO, o))
                val roca = Path().apply {
                    moveTo(c.x - s * 2.1f, c.y + s); lineTo(c.x - s * 1.7f, c.y + s * 0.55f); lineTo(c.x - s * 1.2f, c.y + s * 0.5f); lineTo(c.x + s * 1.3f, c.y + s * 0.48f)
                    lineTo(c.x + s * 1.75f, c.y + s * 0.62f); lineTo(c.x + s * 2.1f, c.y + s); close()
                }
                drawPath(roca, Brush.verticalGradient(listOf(n(CALIZA, o), mez(n(CALIZA, o), Color.Black, 0.35f)), c.y + s * 0.45f, c.y + s))
                for (k in 0 until 4) drawLine(mez(n(CALIZA, o), Color.Black, 0.4f), Offset(c.x - s * (1.5f - k * 0.9f), c.y + s * 0.6f), Offset(c.x - s * (1.35f - k * 0.9f), c.y + s * 0.95f), p)
                // Gradas.
                for (k in 0 until 3) {
                    val an = s * (2.6f - 0.14f * k)
                    val r = Rect(c.x - an / 2, c.y + s * (0.42f - 0.09f * k), c.x + an / 2, c.y + s * (0.51f - 0.09f * k))
                    rect(r, vert(r, luz, osc)); drawLine(som, Offset(r.left, r.bottom), Offset(r.right, r.bottom), p * 0.6f)
                }
                // La cella en sombra y las ocho columnas.
                rect(Rect(c.x - s * 1.1f, c.y - s * 0.32f, c.x + s * 1.1f, c.y + s * 0.24f), n(Color(0xFF5A5246), o))
                for (k in 0 until 8) {
                    val x = c.x - s * 1.05f + s * 2.1f * k / 7f
                    val col = Rect(x - s * 0.085f, c.y - s * 0.3f, x + s * 0.085f, c.y + s * 0.24f)
                    rect(col, horiz(col, som, luz, osc))
                    rect(Rect(x - s * 0.12f, c.y - s * 0.36f, x + s * 0.12f, c.y - s * 0.3f), luz)
                }
                // Arquitrabe, friso y cornisa.
                val arq = Rect(c.x - s * 1.25f, c.y - s * 0.48f, c.x + s * 1.25f, c.y - s * 0.36f)
                rect(arq, vert(arq, luz, osc))
                val fr = Rect(c.x - s * 1.25f, c.y - s * 0.62f, c.x + s * 1.25f, c.y - s * 0.48f)
                rect(fr, mar)
                for (k in 0 until 11) {
                    val x = fr.left + fr.width * k / 10f
                    rect(Rect(x - s * 0.04f, fr.top, x + s * 0.04f, fr.bottom), n(EGEO, o))
                    if (k < 10) drawCircle(e.c1, s * 0.035f, Offset(x + fr.width / 20f, fr.center.y))
                }
                rect(Rect(c.x - s * 1.36f, c.y - s * 0.68f, c.x + s * 1.36f, c.y - s * 0.62f), som)
                // El frontón con el tímpano azul y las acroteras.
                val fronton = Path().apply { moveTo(c.x - s * 1.4f, c.y - s * 0.68f); lineTo(c.x, c.y - s * 1.0f); lineTo(c.x + s * 1.4f, c.y - s * 0.68f); close() }
                drawPath(fronton, vert(Rect(0f, c.y - s, 0f, c.y - s * 0.68f), luz, osc))
                drawPath(Path().apply { moveTo(c.x - s * 1.12f, c.y - s * 0.71f); lineTo(c.x, c.y - s * 0.93f); lineTo(c.x + s * 1.12f, c.y - s * 0.71f); close() }, n(EGEO, o))
                for (k in -3..3) drawCircle(n(ORO, o), s * 0.03f, Offset(c.x + k * s * 0.26f, c.y - s * 0.77f + abs(k) * s * 0.0f - (3 - abs(k)) * s * 0.025f))
                listOf(Offset(c.x, c.y - s * 1.0f), Offset(c.x - s * 1.4f, c.y - s * 0.68f), Offset(c.x + s * 1.4f, c.y - s * 0.68f)).forEach { palmeta(it, s * 0.18f, n(ORO, o)) }
                // El olivo.
                val tronco = Offset(c.x + s * 1.7f, c.y + s * 0.6f)
                drawLine(n(NOGAL, o), tronco, Offset(tronco.x - s * 0.05f, tronco.y - s * 0.45f), p * 3f, StrokeCap.Round)
                listOf(Offset(0f, -0.62f), Offset(-0.18f, -0.5f), Offset(0.17f, -0.48f), Offset(0.05f, -0.8f)).forEach { (dx, dy) ->
                    drawCircle(n(OLIVO, o), s * 0.2f, Offset(tronco.x + dx * s, tronco.y + dy * s))
                }
                drawCircle(mez(n(OLIVO, o), Color.White, 0.25f), s * 0.08f, Offset(tronco.x - s * 0.05f, tronco.y - s * 0.86f))
            }
            "rascar" -> {
                // Una tablilla de barniz negro: la tetradracma raspa el barniz y aparece el mapa de terracota.
                val tab = Rect(c.x - s * 1.9f, c.y - s * 0.85f, c.x + s * 1.2f, c.y + s * 0.85f)
                drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(tab.left + p * 2, tab.top + p * 3), tab.size, CornerRadius(s * 0.1f))
                drawRoundRect(Brush.linearGradient(listOf(mez(n(BARNIZ, o), Color.White, 0.18f), n(BARNIZ, o)), tab.topLeft, tab.bottomRight), tab.topLeft, tab.size, CornerRadius(s * 0.1f))
                val raspado = Path().apply {
                    moveTo(tab.left + s * 0.25f, tab.top + s * 0.4f); cubicTo(tab.left + s * 0.9f, tab.top + s * 0.1f, tab.left + s * 1.9f, tab.top + s * 0.25f, tab.left + s * 2.5f, tab.top + s * 0.55f)
                    lineTo(tab.left + s * 2.7f, tab.top + s * 1.3f); cubicTo(tab.left + s * 1.8f, tab.bottom - s * 0.1f, tab.left + s * 0.8f, tab.bottom - s * 0.2f, tab.left + s * 0.3f, tab.top + s * 1.25f); close()
                }
                drawPath(raspado, Brush.linearGradient(listOf(n(TERRA_LUZ, o), n(TERRACOTA, o)), tab.topLeft, tab.bottomRight))
                clipPath(raspado) {
                    val costa = Path().apply {
                        moveTo(tab.left + s * 0.5f, tab.top + s * 0.7f); cubicTo(tab.left + s * 1.0f, tab.top + s * 0.4f, tab.left + s * 1.5f, tab.top + s * 1.0f, tab.left + s * 1.9f, tab.top + s * 0.6f)
                        lineTo(tab.left + s * 2.2f, tab.top + s * 1.2f); cubicTo(tab.left + s * 1.6f, tab.top + s * 1.4f, tab.left + s * 1.0f, tab.top + s * 1.2f, tab.left + s * 0.6f, tab.top + s * 1.3f); close()
                    }
                    drawPath(costa, n(TERRA_OSC, o))
                    drawCircle(e.c1, s * 0.09f, Offset(tab.left + s * 1.35f, tab.top + s * 0.95f))
                    for (k in 0 until 5) drawLine(Color.Black.copy(alpha = 0.18f), Offset(tab.left + s * (0.3f + 0.5f * k), tab.top), Offset(tab.left + s * (0.6f + 0.5f * k), tab.bottom), p * 0.7f)
                }
                drawPath(raspado, n(BARNIZ, o).copy(alpha = 0.6f), style = Stroke(p))
                greca(Rect(tab.left + s * 0.2f, tab.bottom - s * 0.3f, tab.right - s * 0.2f, tab.bottom - s * 0.12f), n(TERRACOTA, o), p * 0.8f)
                // La moneda de plata con la lechuza, inclinada al final del raspado.
                val mc = Offset(c.x + s * 1.45f, c.y + s * 0.25f)
                val r = s * 0.7f
                drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(mc.x + p * 2, mc.y + p * 3))
                drawOval(n(Color(0xFF8E9297), o), Offset(mc.x - r, mc.y - r * 0.92f), Size(r * 2, r * 2))
                drawCircle(Brush.radialGradient(listOf(mez(n(PLATA, o), Color.White, 0.6f), n(PLATA, o), mez(n(PLATA, o), Color.Black, 0.3f)), Offset(mc.x - r * 0.4f, mc.y - r * 0.5f), r * 1.6f), r * 0.94f, Offset(mc.x, mc.y - r * 0.06f))
                val g = mez(n(PLATA, o), Color.Black, 0.5f)
                val oc = Offset(mc.x, mc.y - r * 0.06f)
                drawOval(g, Offset(oc.x - r * 0.32f, oc.y - r * 0.45f), Size(r * 0.64f, r * 0.9f), style = Stroke(p * 1.1f))
                listOf(-1f, 1f).forEach { k -> drawCircle(g, r * 0.12f, Offset(oc.x + k * r * 0.15f, oc.y - r * 0.2f), style = Stroke(p)); drawCircle(g, r * 0.04f, Offset(oc.x + k * r * 0.15f, oc.y - r * 0.2f)) }
                drawLine(g, Offset(oc.x, oc.y - r * 0.08f), Offset(oc.x, oc.y + r * 0.02f), p)
                listOf(0f, 1f).forEach { k -> drawLine(n(OLIVO, o), Offset(oc.x + r * 0.45f, oc.y - r * (0.5f - k * 0.25f)), Offset(oc.x + r * 0.62f, oc.y - r * (0.6f - k * 0.25f)), p * 1.6f, StrokeCap.Round) }
                drawCircle(mez(n(PLATA, o), Color.Black, 0.3f), r * 0.82f, oc, style = Stroke(p * 0.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p, p * 1.2f))))
            }
            else -> {
                // Tus lugares: un trozo de carta en papiro, con el mar, la costa y tres clavos de bronce unidos por el camino.
                val hoja = Rect(c.x - s * 2.0f, c.y - s * 0.85f, c.x + s * 2.0f, c.y + s * 0.85f)
                drawRect(Color.Black.copy(alpha = 0.28f), Offset(hoja.left + p * 2, hoja.top + p * 3), hoja.size)
                val pap = if (o) mez(PAPIRO, NOCHE, 0.4f) else PAPIRO
                rect(hoja, vert(hoja, mez(pap, Color.White, 0.2f), pap))
                clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                    val mar0 = Path().apply {
                        moveTo(hoja.left, hoja.top); lineTo(hoja.left + s * 1.6f, hoja.top); cubicTo(hoja.left + s * 1.3f, hoja.top + s * 0.5f, hoja.left + s * 2.2f, hoja.top + s * 0.7f, hoja.left + s * 1.8f, hoja.top + s * 1.2f)
                        cubicTo(hoja.left + s * 1.2f, hoja.bottom, hoja.left + s * 0.6f, hoja.top + s * 1.1f, hoja.left, hoja.top + s * 1.3f); close()
                    }
                    drawPath(mar0, n(EGEO, o).copy(alpha = 0.55f))
                    drawPath(mar0, n(EGEO, o), style = Stroke(p))
                    for (k in 0 until 3) {
                        val y = hoja.top + s * (0.25f + 0.28f * k)
                        drawPath(Path().apply { moveTo(hoja.left + s * 0.2f, y); quadraticTo(hoja.left + s * 0.35f, y - s * 0.08f, hoja.left + s * 0.5f, y); quadraticTo(hoja.left + s * 0.65f, y + s * 0.08f, hoja.left + s * 0.8f, y) }, n(MARMOL_LUZ, o).copy(alpha = 0.7f), style = Stroke(p * 0.8f))
                    }
                    for (k in 0 until 6) drawLine(mez(pap, Color.Black, 0.12f), Offset(hoja.left, hoja.top + s * 0.3f * k), Offset(hoja.right, hoja.top + s * 0.3f * k), p * 0.4f)
                }
                listOf(hoja.left, hoja.right - s * 0.18f).forEach { x ->
                    val r = Rect(x, hoja.top - s * 0.06f, x + s * 0.18f, hoja.bottom + s * 0.06f)
                    rect(r, horiz(r, mez(pap, Color.Black, 0.35f), pap, mez(pap, Color.Black, 0.3f)))
                }
                val pts = listOf(Offset(c.x - s * 0.15f, c.y + s * 0.35f), Offset(c.x + s * 0.65f, c.y - s * 0.35f), Offset(c.x + s * 1.35f, c.y + s * 0.3f))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x + s * 0.1f, c.y - s * 0.4f, pts[1].x, pts[1].y); quadraticTo(c.x + s * 1.2f, c.y - s * 0.3f, pts[2].x, pts[2].y) },
                    n(BARNIZ, o).copy(alpha = 0.7f), style = Stroke(p * 1.1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p * 2.5f, p * 2f))))
                listOf(e.c1, e.c2, n(ORO, o)).forEachIndexed { k, col ->
                    val q = pts[k]
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(q.x - s * 0.1f, q.y + s * 0.05f), Size(s * 0.4f, s * 0.14f))
                    drawLine(n(BRONCE_OSC, o), q, Offset(q.x + s * 0.06f, q.y - s * 0.3f), p * 1.6f)
                    val cab = Offset(q.x + s * 0.06f, q.y - s * 0.36f)
                    drawCircle(Brush.radialGradient(listOf(mez(col, Color.White, 0.55f), col, mez(col, Color.Black, 0.35f)), Offset(cab.x - s * 0.06f, cab.y - s * 0.06f), s * 0.24f), s * 0.18f, cab)
                    solVergina(cab, s * 0.1f, mez(col, Color.White, 0.7f).copy(alpha = 0.85f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.NOTAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val mad = nm(NOGAL, o); val madL = nm(NOGAL_LUZ, o); val madO = nm(NOGAL_OSC, o)
        val marcoTop = h * 0.075f
        val repisa = ni.bottom + 1.6f * u

        // El frontón bajo, con el sol de Vergina, y las acroteras.
        val fr = Path().apply { moveTo(w * 0.2f, marcoTop + 0.2f * u); lineTo(w / 2, h * 0.012f); lineTo(w * 0.8f, marcoTop + 0.2f * u); close() }
        drawPath(fr, vert(Rect(0f, 0f, 0f, marcoTop), madL, madO))
        drawPath(Path().apply { moveTo(w * 0.26f, marcoTop); lineTo(w / 2, h * 0.028f); lineTo(w * 0.74f, marcoTop); close() }, nm(EGEO, o))
        solVergina(Offset(w / 2, marcoTop - (marcoTop - h * 0.028f) * 0.42f), (marcoTop - h * 0.028f) * 0.36f, nm(ORO, o))
        listOf(Offset(w * 0.2f, marcoTop), Offset(w * 0.8f, marcoTop)).forEach { palmeta(it, marcoTop * 0.55f, nm(ORO, o)) }

        // El marco de nogal: travesaño superior con greca, largueros y travesaño inferior.
        val marco = Rect(w * 0.012f, marcoTop, w * 0.988f, repisa)
        drawRect(sombraMueble(0.3f), Offset(marco.left + 0.8f * u, marco.top + 1.4f * u), marco.size)
        rect(marco, Brush.linearGradient(listOf(madL, mad, madO), marco.topLeft, marco.bottomRight))
        vetas(Rect(marco.left, marco.top, marco.right, ni.top), madO.copy(alpha = 0.5f), e.semilla, p)
        vetas(Rect(marco.left, ni.top, ni.left, ni.bottom), madO.copy(alpha = 0.5f), e.semilla + 1, p, vertical = true)
        vetas(Rect(ni.right, ni.top, marco.right, ni.bottom), madO.copy(alpha = 0.5f), e.semilla + 2, p, vertical = true)
        val banda = Rect(w * 0.16f, marco.top + (ni.top - marco.top) * 0.22f, w * 0.84f, ni.top - (ni.top - marco.top) * 0.3f)
        rect(banda, nm(BARNIZ, o))
        greca(banda.deflate(banda.height * 0.12f), nm(TERRACOTA, o), banda.height * 0.1f)
        drawRect(nm(ORO, o), banda.topLeft, banda.size, style = Stroke(p * 0.5f))
        // La moldura interior (un bisel) y el leukoma: el tablón blanqueado.
        val bis = ni.inflate(1.4f * u)
        rect(bis, Brush.linearGradient(listOf(madO, madL), bis.topLeft, bis.bottomRight))
        rect(ni, nicho(e, o, MARMOL_LUZ))
        val rnd = Random(e.semilla.toLong() * 17 + 3)
        repeat(26) { val y0 = ni.top + ni.height * rnd.nextFloat(); val x0 = ni.left + ni.width * rnd.nextFloat() * 0.8f
            drawLine((if (o) Color.White else Color(0xFF8A7A5A)).copy(alpha = if (o) 0.018f else 0.03f), Offset(x0, y0), Offset(x0 + ni.width * (0.15f + 0.3f * rnd.nextFloat()), y0 + (rnd.nextFloat() - 0.5f) * 3f * u), (1.5f + 3f * rnd.nextFloat()) * u, StrokeCap.Round) }
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(sombraMueble(0.12f), Color.Transparent), ni.top, ni.top + 3f * u))
        rect(Rect(ni.left, ni.top, ni.left + 2f * u, ni.bottom), Brush.horizontalGradient(listOf(sombraMueble(0.1f), Color.Transparent), ni.left, ni.left + 2f * u))
        // Las cantoneras de bronce, con remaches, en las cuatro esquinas.
        val ca = 6f * u
        listOf(Triple(marco.left, marco.top, 1f to 1f), Triple(marco.right, marco.top, -1f to 1f), Triple(marco.left, marco.bottom, 1f to -1f), Triple(marco.right, marco.bottom, -1f to -1f)).forEach { (x, y, s) ->
            val (sx, sy) = s
            val esq = Path().apply { moveTo(x, y); lineTo(x + sx * ca, y); lineTo(x + sx * ca, y + sy * ca * 0.35f); lineTo(x + sx * ca * 0.35f, y + sy * ca * 0.35f); lineTo(x + sx * ca * 0.35f, y + sy * ca); lineTo(x, y + sy * ca); close() }
            drawPath(esq, Brush.linearGradient(listOf(nm(BRONCE_LUZ, o), nm(BRONCE, o), nm(BRONCE_OSC, o)), Offset(x, y), Offset(x + sx * ca, y + sy * ca)))
            listOf(Offset(x + sx * ca * 0.72f, y + sy * ca * 0.18f), Offset(x + sx * ca * 0.18f, y + sy * ca * 0.72f), Offset(x + sx * ca * 0.18f, y + sy * ca * 0.18f)).forEach { r ->
                drawCircle(nm(BRONCE_OSC, o), 0.6f * u, r); drawCircle(nm(BRONCE_LUZ, o), 0.25f * u, Offset(r.x - 0.15f * u, r.y - 0.15f * u))
            }
        }

        // La repisa moldurada con sus dos consolas; encima, el estilo, la esponja y la lucerna.
        val rep = Rect(0f, repisa, w, repisa + 3.2f * u)
        drawRect(sombraMueble(0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 1.4f * u))
        rect(rep, vert(rep, nm(MARMOL_LUZ, o), nm(MARMOL, o), nm(MARMOL_SOMBRA, o)))
        drawLine(nm(MARMOL_SOMBRA, o), Offset(0f, rep.top + rep.height * 0.4f), Offset(w, rep.top + rep.height * 0.4f), p * 0.5f)
        listOf(w * 0.14f, w * 0.86f).forEach { x ->
            val con = Path().apply { moveTo(x - 2.6f * u, rep.bottom); lineTo(x + 2.6f * u, rep.bottom); quadraticTo(x + 2.4f * u, h * 0.97f, x, h * 0.99f); quadraticTo(x - 2.4f * u, h * 0.97f, x - 2.6f * u, rep.bottom); close() }
            drawPath(con, Brush.horizontalGradient(listOf(nm(MARMOL_SOMBRA, o), nm(MARMOL_LUZ, o), nm(MARMOL_OSC, o)), x - 2.6f * u, x + 2.6f * u))
            drawCircle(nm(MARMOL_SOMBRA, o), 1.1f * u, Offset(x, rep.bottom + 2.4f * u), style = Stroke(p * 0.6f))
        }
        // El estilo de bronce y la esponja, a la izquierda; la lucerna a la derecha.
        drawLine(nm(BRONCE_OSC, o), Offset(w * 0.3f, repisa - 0.4f * u), Offset(w * 0.44f, repisa - 1.2f * u), p * 1.4f, StrokeCap.Round)
        drawCircle(nm(BRONCE, o), p * 1.1f, Offset(w * 0.3f, repisa - 0.4f * u))
        drawOval(Brush.radialGradient(listOf(nm(Color(0xFFD9B66A), o), nm(Color(0xFF9A7432), o)), Offset(w * 0.22f, repisa - 1.6f * u), 3f * u), Offset(w * 0.2f, repisa - 2.8f * u), Size(4.4f * u, 2.8f * u))
        val lx = w * 0.7f
        drawOval(Brush.verticalGradient(listOf(nm(TERRA_LUZ, o), nm(TERRA_OSC, o)), repisa - 2.8f * u, repisa), Offset(lx - 3f * u, repisa - 2.6f * u), Size(6f * u, 2.6f * u))
        drawPath(Path().apply { moveTo(lx + 2f * u, repisa - 2.2f * u); lineTo(lx + 4.4f * u, repisa - 1.8f * u); lineTo(lx + 4.4f * u, repisa - 0.9f * u); lineTo(lx + 2f * u, repisa - 0.6f * u); close() }, nm(TERRA_OSC, o))
        llama(Offset(lx + 4.3f * u, repisa - 2.4f * u), 1f * u, o)
        // El grano de papel, al final (regla 7).
        granoMueble(Rect(0f, 0f, w, h), o)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        val forma = tiesto(r.deflate(1.5f * un), semilla, 0.05f)
        translate(1.2f * un, 2.2f * un) { drawPath(forma, sombraMueble(0.32f)) }
        val barro = if (o) BARRO_OSCURO else BARRO_CLARO
        drawPath(forma, Brush.linearGradient(listOf(mez(barro, Color.White, 0.14f), barro, mez(barro, Color.Black, 0.1f)), r.topLeft, r.bottomRight))
        clipPath(forma) {
            // La banda de barniz negro de la cara exterior, arriba, con sus lengüetas de terracota.
            val bh = r.height * 0.1f
            val barniz = if (o) Color(0xFF120C08) else BARNIZ
            drawRect(barniz, r.topLeft, Size(r.width, bh))
            var x = r.left + 3f * un
            while (x < r.right) {
                drawPath(Path().apply { moveTo(x - 1.4f * un, r.top + bh); quadraticTo(x, r.top + bh * 0.25f, x + 1.4f * un, r.top + bh); close() }, TERRA_OSC)
                x += 5f * un
            }
            drawLine(TERRACOTA, Offset(r.left, r.top + bh + 0.6f * un), Offset(r.right, r.top + bh + 0.6f * un), 0.7f * un)
            // Los poros del barro.
            for (k in 0 until 22) drawCircle(mez(barro, Color.Black, 0.25f).copy(alpha = 0.35f), (0.3f + 0.6f * azar(semilla, k)) * un, Offset(r.left + r.width * azar(semilla, k + 40), r.top + r.height * (0.15f + 0.85f * azar(semilla, k + 80))))
        }
        // El canto roto, más claro.
        drawPath(forma, (if (o) TERRA_OSC else TERRA_LUZ).copy(alpha = 0.9f), style = Stroke(1.1f * un, join = StrokeJoin.Round))
        // El sello del tipo, en la esquina de abajo a la derecha (fuera del texto).
        val q = Offset(r.right - r.width * 0.1f, r.bottom - r.height * 0.085f)
        val t = r.height * 0.05f
        when (e.tipo) {
            "idea" -> solVergina(q, t * 1.1f, if (o) BRONCE_LUZ else ORO)
            "pensamiento" -> {
                val esp = Path()
                for (k in 0..24) { val ang = k / 24f * 2.2f * 6.283f; val rr = t * (1f - 0.8f * k / 24f); val px = q.x + rr * cos(ang); val py = q.y + rr * sin(ang); if (k == 0) esp.moveTo(px, py) else esp.lineTo(px, py) }
                drawPath(esp, if (o) MARMOL_OSC else BARNIZ, style = Stroke(0.9f * un))
            }
            "proposito" -> {
                drawLine(OLIVO, Offset(q.x - t, q.y + t * 0.6f), Offset(q.x + t, q.y - t * 0.6f), 0.8f * un)
                listOf(-0.5f, 0f, 0.5f).forEach { f ->
                    val b = Offset(q.x + f * t * 1.6f, q.y - f * t * 0.95f)
                    drawOval(mez(OLIVO, Color.White, if (o) 0.25f else 0f), Offset(b.x - t * 0.5f, b.y - t * 0.85f), Size(t * 0.55f, t * 0.8f))
                    drawOval(mez(OLIVO, Color.White, if (o) 0.25f else 0f), Offset(b.x, b.y - t * 0.1f), Size(t * 0.8f, t * 0.5f))
                }
            }
            else -> {
                val cinta = if (o) mez(e.acento, Color.White, 0.2f) else e.acento
                drawCircle(cinta, t * 0.55f, Offset(q.x - t * 0.5f, q.y), style = Stroke(0.9f * un))
                drawCircle(cinta, t * 0.55f, Offset(q.x + t * 0.5f, q.y), style = Stroke(0.9f * un))
                drawLine(cinta, q, Offset(q.x - t * 0.4f, q.y + t * 1.2f), 0.9f * un)
                drawLine(cinta, q, Offset(q.x + t * 0.5f, q.y + t * 1.1f), 0.9f * un)
            }
        }
        // Fijada: un clavo de bronce en la banda de barniz, con su sombra.
        if (e.fijada) {
            val cl = Offset(r.center.x, r.top + r.height * 0.06f)
            drawCircle(sombraMueble(0.35f), 3.2f * un, Offset(cl.x + 0.8f * un, cl.y + 1.2f * un))
            drawCircle(Brush.radialGradient(listOf(BRONCE_LUZ, BRONCE, BRONCE_OSC), Offset(cl.x - un, cl.y - un), 4f * un), 3f * un, cl)
            roseta(cl, 1.8f * un, BRONCE_OSC.copy(alpha = 0.6f), BRONCE_LUZ, 6)
        }
    }

    /** El contorno de un tiesto dentro de [r]: el perímetro con muescas, cada lado empujado hacia adentro hasta [hondo] (fracción del lado menor). */
    private fun tiesto(r: Rect, semilla: Int, hondo: Float): Path {
        val rnd = Random(semilla.toLong() * 7919L + 13L)
        val m = minOf(r.width, r.height) * hondo
        val path = Path()
        val puntos = 16
        for (k in 0 until puntos) {
            val t = k / puntos.toFloat()
            val adentro = m * (0.1f + 0.9f * rnd.nextFloat())
            val (x, y) = when {
                t < 0.25f -> (r.left + r.width * (t / 0.25f)) to (r.top + adentro)
                t < 0.5f -> (r.right - adentro) to (r.top + r.height * ((t - 0.25f) / 0.25f))
                t < 0.75f -> (r.right - r.width * ((t - 0.5f) / 0.25f)) to (r.bottom - adentro)
                else -> (r.left + adentro) to (r.bottom - r.height * ((t - 0.75f) / 0.25f))
            }
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF3E6D0) else Color(0xFF1E1408)
}
