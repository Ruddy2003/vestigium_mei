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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Mesoamérica: el palacio maya y el taller del tlacuilo, con los materiales de su biblioteca, su Ideario y su plaza de
 * estelas: caliza labrada en sillares, estuco pintado de cinabrio, azul maya y ocre, madera de chicozapote, jade pulido,
 * obsidiana, papel amate con sus fibras, petate de palma y piel de jaguar.
 * - Social: la sala del palacio. Arriba la cornisa de caliza con la greca escalonada (xicalcoliuhqui) en cinabrio y azul
 *   maya, y el dintel de chicozapote con sus cartuchos de glifos; a los lados dos pilastras labradas con numerales de puntos
 *   y barras y orejeras de jade; al centro, el mural de estuco enmarcado donde va tu gente. Abajo, la banca de piedra con
 *   sus petates, cojines de piel de jaguar, un vaso cilíndrico pintado, un plato trípode con cacao y el incensario con el
 *   humo del copal.
 * - Mapas: la mesa del tlacuilo: tablero de chicozapote con la orla de greca pintada, la hoja de amate con cuatro cuentas de
 *   jade en las esquinas, el caracol que sirve de tintero con su pincel, las cazuelitas de pigmento, el códice plegado, el
 *   collar de jade, la navaja de obsidiana y un brasero encendido; al frente, el faldón con glifos y las patas de jaguar.
 * - Otras vidas: una fachada puuc: la cornisa, el friso de celosía con el mascarón de Chaac y sus orejeras, la moldura de
 *   atadura con columnillas, cinco nichos de bóveda en saledizo (el «arco maya») entre pilastras con glifos, y abajo el
 *   tablero y el talud pintados.
 * - Íconos: la pirámide escalonada con su templo y su crestería entre la selva; una hoja de amate con la capa de cinabrio
 *   que rasca un hacha de jade; una tira de amate con tres glifos de cerro unidos por huellas de pies, como en los códices.
 * - Notas: el muro de estuco en su marco pintado, bajo el dintel de chicozapote con glifos, entre jambas de caliza; abajo,
 *   la banca con el incensario y el vaso. Cada nota es una hoja de amate clavada con una espina de maguey, con su glifo
 *   según el tipo; la fijada lleva un disco de jade atado.
 */
internal object VidaMeso : DisenoVida {
    private val ESTUCO = Color(0xFFF2E8D2)
    private val ESTUCO_OSC = Color(0xFFD9C9A6)
    private val CALIZA = Color(0xFFD8CDB4)
    private val CALIZA_LUZ = Color(0xFFEDE5D2)
    private val CALIZA_OSC = Color(0xFFA8997C)
    private val CALIZA_SOMBRA = Color(0xFF7E7158)
    private val CINABRIO = Color(0xFFA3301E)
    private val CINABRIO_LUZ = Color(0xFFC8503A)
    private val CINABRIO_OSC = Color(0xFF6E1E12)
    private val AZUL = Color(0xFF2C7F9C)
    private val AZUL_LUZ = Color(0xFF6FB4C6)
    private val JADE = Color(0xFF2F7D57)
    private val JADE_LUZ = Color(0xFF7CC29A)
    private val JADE_OSC = Color(0xFF1A4F36)
    private val OCRE = Color(0xFFD69A55)
    private val AMATE = Color(0xFFDCC7A0)
    private val AMATE_OSC = Color(0xFFA68A5E)
    private val ZAPOTE = Color(0xFF7A5634)
    private val ZAPOTE_LUZ = Color(0xFFA27A50)
    private val ZAPOTE_OSC = Color(0xFF4C3420)
    private val VETA = Color(0xFF2E1C10)
    private val NEGRO = Color(0xFF2A1810)
    private val OBSIDIANA = Color(0xFF1E1D24)
    private val CUERDA = Color(0xFFC9B07A)
    private val CUERDA_OSC = Color(0xFF8E7A4E)
    private val PALMA = Color(0xFFCDB585)
    private val PALMA_LUZ = Color(0xFFE6D2A0)
    private val PALMA_OSC = Color(0xFF9C8458)
    private val JAGUAR = Color(0xFFC98E3A)
    private val BARRO = Color(0xFF9A6038)
    private val BARRO_LUZ = Color(0xFFC08454)
    private val COPAL = Color(0xFFE8E4DA)
    private val BRASA = Color(0xFFFF8A3C)
    private val LLAMA = Color(0xFFFFC870)
    private val NOCHE = Color(0xFF090D0E)

    /** El amate de las notas: claro de día; de noche, amate oscuro (con la tinta clara de [tintaNota]). */
    private val AMATE_CLARO = Color(0xFFEBDDBD)
    private val AMATE_NOCHE = Color(0xFF382B1E)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.14f, 0.215f, 0.86f, 0.715f)
        MuebleVida.MAPAS -> Rect(0.175f, 0.095f, 0.825f, 0.70f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.045f, 0.28f, 0.955f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.14f, 0.925f, 0.855f)
    }

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de noche: el mismo, en penumbra ([f] cuánto se hunde). */
    private fun n(c: Color, oscuro: Boolean, f: Float = 0.6f): Color = if (oscuro) mez(c, NOCHE, f) else c

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mez(e.fondo, mez(material, NOCHE, 0.82f), 0.4f) else mez(e.fondo, material, 0.3f)

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

    /** Una mancha radial suave (luz de brasa, humedad del estuco). */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Vetas de madera en [r]: líneas onduladas a lo largo, de grosor [p] (≈ 1 dp). */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, p: Float, densidad: Float = 1f) {
        if (r.width <= 0f || r.height <= 0f) return
        val cuantas = (r.height / (3.2f * p) * densidad).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until cuantas) {
                val base = r.height * (k + azar(semilla, k)) / cuantas
                val amp = (0.6f + 1.6f * azar(semilla, k + 100)) * p
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val path = Path()
                for (s in 0..16) {
                    val x = r.left + r.width * s / 16
                    val y = r.top + base + amp * sin(fase + ondas * 6.28f * s / 16)
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(p * (0.5f + 0.6f * azar(semilla, k + 500))))
            }
        }
    }

    /** Sillares de caliza en [r]: hiladas trabadas, cada piedra con su tono, su luz arriba y algún poro. */
    private fun DrawScope.sillares(r: Rect, o: Boolean, p: Float, alto: Float, semilla: Int) {
        rect(r, n(CALIZA_OSC, o))
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top; var fila = 0
            while (y < r.bottom) {
                var x = r.left - (if (fila % 2 == 1) alto * 1.1f else 0f)
                var k = 0
                while (x < r.right) {
                    val largo = alto * (1.8f + 0.8f * azar(semilla + fila, k))
                    val q = Rect(x + p * 0.5f, y + p * 0.5f, x + largo - p * 0.5f, y + alto - p * 0.5f)
                    val tono = mez(n(CALIZA, o), n(CALIZA_LUZ, o), azar(semilla * 3 + fila, k) * 0.6f)
                    rect(q, vert(q, mez(tono, Color.White, if (o) 0.04f else 0.18f), tono, mez(tono, n(CALIZA_OSC, o), 0.35f)))
                    drawLine(n(CALIZA_SOMBRA, o).copy(alpha = 0.6f), Offset(q.left, q.bottom), Offset(q.right, q.bottom), p * 0.7f)
                    if (azar(semilla, fila * 31 + k) > 0.5f) drawCircle(n(CALIZA_OSC, o), p * 0.6f, Offset(q.left + q.width * azar(semilla, k + 7), q.top + q.height * azar(semilla, k + 9)))
                    x += largo; k++
                }
                y += alto; fila++
            }
        }
    }

    /** La greca escalonada (xicalcoliuhqui) en [r]: escalera y gancho, alternando [c1] y [c2] sobre [fondo]. */
    private fun DrawScope.greca(r: Rect, c1: Color, c2: Color, fondo: Color, p: Float) {
        rect(r, fondo)
        val hh = r.height * 0.84f
        val y0 = r.top + r.height * 0.08f
        val t = hh * 0.17f
        clipRect(r.left, r.top, r.right, r.bottom) {
            var x = r.left - hh * 0.4f
            var k = 0
            while (x < r.right) {
                val pts = listOf(0.08f to 1f, 0.08f to 0.72f, 0.33f to 0.72f, 0.33f to 0.46f, 0.58f to 0.46f, 0.58f to 0.1f, 1.62f to 0.1f, 1.62f to 0.86f, 1.02f to 0.86f, 1.02f to 0.46f, 1.32f to 0.46f)
                val path = Path()
                pts.forEachIndexed { i, (a, b) -> val px = x + a * hh; val py = y0 + b * hh; if (i == 0) path.moveTo(px, py) else path.lineTo(px, py) }
                drawPath(path, if (k % 2 == 0) c1 else c2, style = Stroke(t, cap = StrokeCap.Butt, join = StrokeJoin.Miter))
                x += hh * 1.75f; k++
            }
        }
        drawLine(n(NEGRO, false).copy(alpha = 0.8f), Offset(r.left, r.top), Offset(r.right, r.top), p * 0.8f)
        drawLine(n(NEGRO, false).copy(alpha = 0.8f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), p * 0.8f)
    }

    /** Un numeral maya de puntos y barras ([v] de 1 a 19), centrado en [c], de ancho [s]. */
    private fun DrawScope.numeral(v: Int, c: Offset, s: Float, color: Color) {
        val barras = v / 5; val puntos = v % 5
        val bh = s * 0.16f; val gap = s * 0.08f
        val totalH = barras * (bh + gap) + (if (puntos > 0) bh * 1.2f + gap else 0f) - gap
        var y = c.y - totalH / 2
        if (puntos > 0) {
            val r = bh * 0.6f
            for (k in 0 until puntos) drawCircle(color, r, Offset(c.x + (k - (puntos - 1) / 2f) * r * 2.8f, y + r))
            y += bh * 1.2f + gap
        }
        repeat(barras) { drawRoundRect(color, Offset(c.x - s * 0.45f, y), Size(s * 0.9f, bh), CornerRadius(bh * 0.4f)); y += bh + gap }
    }

    /** Un cartucho de glifo labrado: el bloque redondeado con su relieve y un numeral o un signo dentro. */
    private fun DrawScope.cartucho(r: Rect, k: Int, piedra: Color, o: Boolean, p: Float) {
        val rad = CornerRadius(r.width * 0.22f)
        drawRoundRect(mez(piedra, Color.Black, 0.35f), Offset(r.left + p * 0.6f, r.top + p * 0.8f), r.size, rad)
        drawRoundRect(vert(r, mez(piedra, Color.White, if (o) 0.06f else 0.22f), piedra, mez(piedra, Color.Black, 0.15f)), r.topLeft, r.size, rad)
        drawRoundRect(mez(piedra, Color.Black, 0.35f), r.topLeft, r.size, rad, style = Stroke(p * 0.5f))
        val tinta = mez(piedra, Color.Black, 0.5f)
        val c = r.center; val s = r.width
        when (((k % 4) + 4) % 4) {
            0 -> numeral(1 + (k * 7) % 19, c, s * 0.7f, tinta)
            1 -> { drawCircle(tinta, s * 0.26f, c, style = Stroke(p * 0.7f)); for (j in 0 until 4) { val a = j * Math.PI / 2 + Math.PI / 4; drawCircle(tinta, s * 0.07f, Offset(c.x + s * 0.17f * cos(a).toFloat(), c.y + s * 0.17f * sin(a).toFloat())) } }
            2 -> { drawOval(tinta, Offset(c.x - s * 0.3f, c.y - s * 0.14f), Size(s * 0.6f, s * 0.28f), style = Stroke(p * 0.7f)); drawCircle(tinta, s * 0.08f, c) }
            else -> { drawLine(tinta, Offset(c.x - s * 0.25f, c.y - s * 0.25f), Offset(c.x + s * 0.25f, c.y + s * 0.25f), p * 0.9f); drawLine(tinta, Offset(c.x + s * 0.25f, c.y - s * 0.25f), Offset(c.x - s * 0.25f, c.y + s * 0.25f), p * 0.9f) }
        }
    }

    /** Una orejera de jade: el disco pulido con su hueco y su brillo. */
    private fun DrawScope.orejera(c: Offset, r: Float, o: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.12f, c.y + r * 0.18f))
        drawCircle(Brush.radialGradient(listOf(n(JADE_LUZ, o, 0.45f), n(JADE, o, 0.45f), n(JADE_OSC, o, 0.45f)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f), r, c)
        drawCircle(n(JADE_OSC, o, 0.4f), r * 0.42f, c)
        drawCircle(n(ESTUCO, o, 0.5f), r * 0.22f, c)
        drawCircle(Color.White.copy(alpha = 0.45f), r * 0.16f, Offset(c.x - r * 0.48f, c.y - r * 0.48f))
    }

    /** El petate en [r]: el tejido de palma en diagonal, tiras claras en un sentido y oscuras cruzadas en el otro. */
    private fun DrawScope.petate(r: Rect, o: Boolean, p: Float, paso: Float) {
        rect(r, n(PALMA, o))
        clipRect(r.left, r.top, r.right, r.bottom) {
            val alto = r.height
            var x = r.left - alto
            while (x < r.right + alto) {
                drawLine(n(PALMA_LUZ, o), Offset(x, r.top), Offset(x + alto, r.bottom), paso * 0.42f)
                drawLine(n(PALMA_OSC, o).copy(alpha = 0.8f), Offset(x + alto, r.top), Offset(x, r.bottom), paso * 0.42f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(paso * 0.7f, paso * 0.72f)))
                drawLine(n(PALMA_OSC, o).copy(alpha = 0.5f), Offset(x + paso * 0.21f, r.top), Offset(x + alto + paso * 0.21f, r.bottom), p * 0.4f)
                x += paso
            }
        }
        drawRect(n(PALMA_OSC, o), r.topLeft, r.size, style = Stroke(p * 0.9f))
    }

    /** Una llama (o una brasa) con su halo; de noche el halo es grande. */
    private fun DrawScope.llama(c: Offset, r: Float, oscuro: Boolean) {
        mancha(c, r * (if (oscuro) 8f else 3f), LLAMA.copy(alpha = if (oscuro) 0.34f else 0.2f))
        drawPath(Path().apply {
            moveTo(c.x, c.y - r * 1.6f); quadraticTo(c.x + r * 0.9f, c.y - r * 0.2f, c.x, c.y + r * 0.5f); quadraticTo(c.x - r * 0.9f, c.y - r * 0.2f, c.x, c.y - r * 1.6f); close()
        }, Color(0xFFFFA733))
        drawOval(Color(0xFFFFF1C2), Offset(c.x - r * 0.3f, c.y - r * 0.6f), Size(r * 0.6f, r * 0.95f))
    }

    /** El incensario de barro con las brasas del copal y la voluta de humo ([humo]: su largo, en fracción del normal). */
    private fun DrawScope.incensario(base: Offset, alto: Float, o: Boolean, p: Float, humo: Float = 1f) {
        val an = alto * 0.62f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(base.x - an * 0.6f, base.y - alto * 0.05f), Size(an * 1.2f, alto * 0.1f))
        // El humo del copal, que sube en volutas.
        val colHumo = if (o) COPAL.copy(alpha = 0.35f) else Color(0xFF9A948A).copy(alpha = 0.4f)
        val y0 = base.y - alto * 0.62f; val k = alto * 0.83f * humo
        drawPath(Path().apply {
            moveTo(base.x, y0)
            cubicTo(base.x - an * 0.5f, y0 - k * 0.22f, base.x + an * 0.5f, y0 - k * 0.4f, base.x - an * 0.1f, y0 - k * 0.64f)
            cubicTo(base.x - an * 0.45f, y0 - k * 0.8f, base.x + an * 0.2f, y0 - k * 0.92f, base.x + an * 0.05f, y0 - k)
        }, colHumo, style = Stroke(p * 2.2f, cap = StrokeCap.Round))
        // El pie y la cazoleta.
        drawPath(Path().apply { moveTo(base.x - an * 0.3f, base.y); lineTo(base.x + an * 0.3f, base.y); lineTo(base.x + an * 0.16f, base.y - alto * 0.18f); lineTo(base.x - an * 0.16f, base.y - alto * 0.18f); close() },
            horiz(Rect(base.x - an * 0.3f, 0f, base.x + an * 0.3f, 0f), n(BARRO, o), n(BARRO_LUZ, o), n(BARRO, o), mez(n(BARRO, o), Color.Black, 0.35f)))
        val caz = Rect(base.x - an * 0.5f, base.y - alto * 0.6f, base.x + an * 0.5f, base.y - alto * 0.16f)
        drawPath(Path().apply { moveTo(caz.left, caz.top); lineTo(caz.right, caz.top); quadraticTo(caz.right, caz.bottom, caz.center.x, caz.bottom); quadraticTo(caz.left, caz.bottom, caz.left, caz.top); close() },
            horiz(caz, mez(n(BARRO, o), Color.Black, 0.3f), n(BARRO_LUZ, o), n(BARRO, o), mez(n(BARRO, o), Color.Black, 0.4f)))
        // Las púas de barro y la banda azul.
        rect(Rect(caz.left + an * 0.08f, caz.top + caz.height * 0.22f, caz.right - an * 0.08f, caz.top + caz.height * 0.36f), n(AZUL, o, 0.45f))
        for (k in 0 until 4) drawCircle(n(BARRO_LUZ, o), an * 0.05f, Offset(caz.left + an * (0.2f + 0.2f * k), caz.top + caz.height * 0.6f))
        // Las brasas.
        drawOval(n(NEGRO, o), Offset(caz.left, caz.top - alto * 0.04f), Size(caz.width, alto * 0.08f))
        drawOval(BRASA, Offset(caz.left + an * 0.15f, caz.top - alto * 0.025f), Size(caz.width - an * 0.3f, alto * 0.05f))
        mancha(Offset(base.x, caz.top), an * (if (o) 2.6f else 1.2f), BRASA.copy(alpha = if (o) 0.4f else 0.2f))
    }

    /** Un vaso cilíndrico de barro pintado: bandas de cinabrio y negro, y una fila de glifos en la franja clara. */
    private fun DrawScope.vaso(base: Offset, alto: Float, o: Boolean, p: Float) {
        val an = alto * 0.62f
        val v = Rect(base.x - an / 2, base.y - alto, base.x + an / 2, base.y)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(v.left - an * 0.1f, base.y - alto * 0.05f), Size(an * 1.2f, alto * 0.1f))
        val cuerpo = horiz(v, mez(n(OCRE, o), Color.Black, 0.3f), n(mez(OCRE, Color.White, 0.25f), o), n(OCRE, o), mez(n(OCRE, o), Color.Black, 0.35f))
        rect(v, cuerpo)
        rect(Rect(v.left, v.top + alto * 0.08f, v.right, v.top + alto * 0.2f), n(CINABRIO, o, 0.5f))
        rect(Rect(v.left, v.bottom - alto * 0.16f, v.right, v.bottom - alto * 0.06f), n(CINABRIO, o, 0.5f))
        drawLine(n(NEGRO, o), Offset(v.left, v.top + alto * 0.22f), Offset(v.right, v.top + alto * 0.22f), p * 0.6f)
        drawLine(n(NEGRO, o), Offset(v.left, v.bottom - alto * 0.18f), Offset(v.right, v.bottom - alto * 0.18f), p * 0.6f)
        // Un personaje sentado de perfil, a la manera de los vasos pintados: cabeza, tocado de plumas y el cuerpo.
        val fx = v.center.x; val fy = v.top + alto * 0.52f
        drawCircle(n(CINABRIO_OSC, o, 0.5f), an * 0.1f, Offset(fx, fy - alto * 0.08f))
        drawPath(Path().apply { moveTo(fx - an * 0.05f, fy - alto * 0.16f); quadraticTo(fx - an * 0.3f, fy - alto * 0.3f, fx - an * 0.32f, fy - alto * 0.12f) }, n(JADE, o, 0.45f), style = Stroke(p * 1.2f, cap = StrokeCap.Round))
        drawRoundRect(n(CINABRIO_OSC, o, 0.5f), Offset(fx - an * 0.14f, fy), Size(an * 0.28f, alto * 0.2f), CornerRadius(an * 0.06f))
        drawOval(mez(n(OCRE, o), Color.Black, 0.45f), Offset(v.left, v.top - alto * 0.04f), Size(an, alto * 0.08f))
        drawOval(n(NEGRO, o), Offset(v.left + an * 0.08f, v.top - alto * 0.025f), Size(an * 0.84f, alto * 0.05f))
    }

    /** Un cojín de piel de jaguar, con sus rosetas. */
    private fun DrawScope.cojinJaguar(r: Rect, o: Boolean, p: Float, semilla: Int) {
        val piel = n(JAGUAR, o)
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + p, r.top + p * 1.5f), r.size, CornerRadius(r.height * 0.35f))
        drawRoundRect(Brush.radialGradient(listOf(mez(piel, Color.White, 0.3f), piel, mez(piel, Color.Black, 0.35f)), Offset(r.center.x - r.width * 0.15f, r.center.y - r.height * 0.2f), r.width * 0.75f), r.topLeft, r.size, CornerRadius(r.height * 0.35f))
        clipPath(Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.height * 0.35f))) }) {
            val rr = r.height * 0.12f
            for (k in 0 until 9) {
                val c = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 20))
                drawCircle(n(NEGRO, o), rr, c, style = Stroke(rr * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(rr * 1.1f, rr * 0.5f))))
                drawCircle(n(NEGRO, o), rr * 0.25f, c)
            }
        }
        listOf(r.left + r.width * 0.08f, r.right - r.width * 0.08f).forEach { x -> drawLine(n(CINABRIO, o, 0.45f), Offset(x, r.bottom - r.height * 0.15f), Offset(x, r.bottom + r.height * 0.15f), p * 1.2f) }
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.SOCIAL)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val suelo = h * 0.955f
        val t = 2.4f * u
        val marco = ni.inflate(t)

        // El muro de estuco pintado de cinabrio, con su zócalo ocre y el piso de estuco pulido.
        val rojo = n(CINABRIO, o, 0.66f)
        rect(Rect(0f, 0f, w, suelo), vert(Rect(0f, 0f, w, suelo), mez(rojo, Color.Black, 0.2f), rojo, mez(rojo, Color.Black, 0.1f)))
        val rnd = Random(e.semilla.toLong() * 31 + 3)
        repeat(18) { mancha(Offset(w * rnd.nextFloat(), suelo * rnd.nextFloat()), (5f + 12f * rnd.nextFloat()) * u, Color.Black.copy(alpha = 0.06f)) }
        val zTop = marco.bottom + 1.4f * u
        rect(Rect(0f, zTop, w, suelo), vert(Rect(0f, zTop, w, suelo), n(OCRE, o, 0.62f), mez(n(OCRE, o, 0.62f), Color.Black, 0.2f)))
        drawLine(n(NEGRO, o), Offset(0f, zTop), Offset(w, zTop), p)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(ESTUCO_OSC, o), mez(n(ESTUCO_OSC, o), Color.Black, 0.2f)))
        drawLine(n(ESTUCO, o), Offset(0f, suelo), Offset(w, suelo), p * 0.7f)

        // La cornisa de caliza con la greca escalonada y el dintel de chicozapote con sus glifos.
        val corB = h * 0.035f; val grecaB = h * 0.1f; val dintelB = marco.top - 0.8f * u
        val cor = Rect(0f, 0f, w, corB)
        rect(cor, vert(cor, n(CALIZA_LUZ, o), n(CALIZA, o), n(CALIZA_OSC, o)))
        drawLine(n(CALIZA_SOMBRA, o), Offset(0f, corB), Offset(w, corB), p)
        greca(Rect(0f, corB, w, grecaB), n(CINABRIO, o, 0.5f), n(AZUL, o, 0.5f), n(ESTUCO, o, 0.62f), p)
        val din = Rect(0f, grecaB + 0.3f * u, w, dintelB)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(din.left, din.bottom), Size(din.width, 1.6f * u))
        rect(din, vert(din, n(ZAPOTE_LUZ, o), n(ZAPOTE, o), n(ZAPOTE_OSC, o)))
        vetas(din, n(VETA, o).copy(alpha = 0.45f), e.semilla, p, 0.7f)
        val gs = din.height * 0.72f
        val ng = (w / (gs * 1.5f)).toInt()
        for (k in 0 until ng) {
            val x = w * (k + 0.5f) / ng
            cartucho(Rect(x - gs / 2, din.center.y - gs / 2, x + gs / 2, din.center.y + gs / 2), k, n(ZAPOTE, o), o, p)
        }

        // Las pilastras de caliza a los lados, con sus glifos y una orejera de jade.
        val pw = minOf(w * 0.075f, 9f * u)
        listOf(maxOf(pw * 0.62f, w * 0.05f), w - maxOf(pw * 0.62f, w * 0.05f)).forEachIndexed { j, x ->
            val pi = Rect(x - pw / 2, dintelB + 1.6f * u, x + pw / 2, suelo)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), pi.right, pi.right + 2f * u), Offset(pi.right, pi.top), Size(2f * u, pi.height))
            sillares(pi, o, p, pw * 0.7f, e.semilla + j)
            drawLine(n(CALIZA_LUZ, o), Offset(pi.left, pi.top), Offset(pi.left, pi.bottom), p * 0.8f)
            val cs = pw * 0.62f
            var y = pi.top + cs * 0.9f; var k = j * 3
            while (y < zTop - cs * 0.5f) { cartucho(Rect(x - cs / 2, y - cs / 2, x + cs / 2, y + cs / 2), k, n(CALIZA, o), o, p); y += cs * 1.35f; k++ }
            orejera(Offset(x, (zTop + suelo) / 2), pw * 0.32f, o)
        }

        // El mural de estuco enmarcado: el nicho donde va tu gente.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 0.7f * u, marco.top + 1.1f * u), marco.size)
        rect(marco, n(AZUL, o, 0.5f))
        drawRect(n(NEGRO, o), marco.topLeft, marco.size, style = Stroke(p * 0.8f))
        val puntos = (marco.width / (2.6f * u)).toInt()
        for (k in 0 until puntos) listOf(marco.top + t / 2, marco.bottom - t / 2).forEach { y -> drawCircle(n(ESTUCO, o, 0.45f), t * 0.14f, Offset(marco.left + marco.width * (k + 0.5f) / puntos, y)) }
        val pv = (marco.height / (2.6f * u)).toInt()
        for (k in 1 until pv) listOf(marco.left + t / 2, marco.right - t / 2).forEach { x -> drawCircle(n(ESTUCO, o, 0.45f), t * 0.14f, Offset(x, marco.top + marco.height * (k + 0.5f) / pv)) }
        rect(ni.inflate(0.6f * u), n(CINABRIO, o, 0.5f))
        rect(ni, nicho(e, o, ESTUCO))
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), ni.top, ni.top + 3f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c -> orejera(c, t * 0.7f, o) }

        // Abajo: la banca de piedra con petates, cojines de jaguar, el vaso, el cacao y el incensario.
        val hz = suelo - zTop
        val banca = Rect(w * 0.115f, suelo - hz * 0.4f, w * 0.885f, suelo)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(banca.left, banca.top + hz * 0.05f), Size(banca.width + u, banca.height))
        sillares(banca, o, p, banca.height * 0.5f, e.semilla + 5)
        val canto = Rect(banca.left - 0.6f * u, banca.top - hz * 0.07f, banca.right + 0.6f * u, banca.top)
        rect(canto, vert(canto, n(CALIZA_LUZ, o), n(CALIZA_OSC, o)))
        // Los petates sobre la banca, a cada lado.
        val pt = Rect(banca.left + banca.width * 0.03f, canto.top - hz * 0.06f, banca.left + banca.width * 0.38f, canto.top + hz * 0.02f)
        petate(pt, o, p, 2.2f * u)
        val pt2 = Rect(banca.right - banca.width * 0.38f, pt.top, banca.right - banca.width * 0.03f, pt.bottom)
        petate(pt2, o, p, 2.2f * u)
        // Los cojines de jaguar contra el muro.
        val cw = banca.width * 0.1f; val ch = hz * 0.34f
        listOf(pt.left + pt.width * 0.04f, pt.left + pt.width * 0.34f, pt2.right - pt2.width * 0.04f - cw, pt2.right - pt2.width * 0.34f - cw).forEachIndexed { k, x ->
            cojinJaguar(Rect(x, pt.top - ch, x + cw, pt.top + ch * 0.1f), o, p, e.semilla + k * 11)
        }
        // El incensario al centro, el vaso y el plato trípode con cacao a los lados.
        clipRect(0f, marco.bottom + p, w, h) { incensario(Offset(w / 2, canto.top), hz * 0.55f, o, p, 0.45f) }
        vaso(Offset(w / 2 - banca.width * 0.08f, canto.top), hz * 0.38f, o, p)
        platoCacao(Offset(w / 2 + banca.width * 0.085f, canto.top), banca.width * 0.065f, o, p)
    }

    /** El plato trípode de barro con granos de cacao. */
    private fun DrawScope.platoCacao(base: Offset, r: Float, o: Boolean, p: Float) {
        listOf(-0.6f, 0f, 0.6f).forEach { f -> drawLine(n(BARRO, o), Offset(base.x + f * r, base.y - r * 0.25f), Offset(base.x + f * r * 1.15f, base.y), r * 0.14f, StrokeCap.Round) }
        for (k in 0 until 5) {
            val c = Offset(base.x + (k - 2) * r * 0.32f, base.y - r * 0.52f - (if (k % 2 == 0) 0f else r * 0.12f))
            drawOval(n(Color(0xFF5A2E1C), o), Offset(c.x - r * 0.17f, c.y - r * 0.11f), Size(r * 0.34f, r * 0.22f))
            drawLine(n(Color(0xFF3A1A10), o), Offset(c.x - r * 0.12f, c.y), Offset(c.x + r * 0.12f, c.y), p * 0.4f)
        }
        val pl = Path().apply { moveTo(base.x - r, base.y - r * 0.5f); lineTo(base.x + r, base.y - r * 0.5f); quadraticTo(base.x + r * 0.8f, base.y - r * 0.18f, base.x, base.y - r * 0.18f); quadraticTo(base.x - r * 0.8f, base.y - r * 0.18f, base.x - r, base.y - r * 0.5f); close() }
        drawPath(pl, horiz(Rect(base.x - r, 0f, base.x + r, 0f), mez(n(BARRO, o), Color.Black, 0.3f), n(BARRO_LUZ, o), n(BARRO, o), mez(n(BARRO, o), Color.Black, 0.35f)))
        drawLine(n(CINABRIO, o, 0.45f), Offset(base.x - r * 0.9f, base.y - r * 0.42f), Offset(base.x + r * 0.9f, base.y - r * 0.42f), p * 0.7f)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.MAPAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val faldonA = h * 0.8f; val faldonB = h * 0.875f; val suelo = h * 0.955f

        // El muro de sillares, una banda de cinabrio detrás de las patas y el piso de estuco.
        sillares(Rect(0f, 0f, w, suelo), o, p, 6f * u, e.semilla)
        rect(Rect(0f, faldonA, w, suelo), vert(Rect(0f, faldonA, w, suelo), n(CINABRIO, o, 0.62f), mez(n(CINABRIO, o, 0.62f), Color.Black, 0.25f)))
        drawLine(n(NEGRO, o), Offset(0f, faldonA), Offset(w, faldonA), p)
        val piso = Rect(0f, suelo, w, h)
        rect(piso, vert(piso, n(ESTUCO_OSC, o), mez(n(ESTUCO_OSC, o), Color.Black, 0.2f)))

        // El tablero de chicozapote: el canto, la cara con su veta y la orla de greca pintada.
        val t = Rect(w * 0.02f, h * 0.022f, w * 0.98f, faldonA)
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(t.left, t.top + 1.2f * u), t.size, CornerRadius(1.2f * u))
        drawRoundRect(n(ZAPOTE_OSC, o), t.topLeft, t.size, CornerRadius(1.2f * u))
        val cara = t.deflate(1.1f * u)
        rect(cara, Brush.linearGradient(listOf(n(ZAPOTE_LUZ, o), n(ZAPOTE, o), mez(n(ZAPOTE, o), n(ZAPOTE_OSC, o), 0.5f)), cara.topLeft, cara.bottomRight))
        vetas(cara, n(VETA, o).copy(alpha = 0.45f), e.semilla, p, 0.9f)
        val tb = 2.4f * u
        val orla = cara.deflate(1.2f * u)
        greca(Rect(orla.left, orla.top, orla.right, orla.top + tb), n(CINABRIO, o, 0.5f), n(AZUL, o, 0.5f), n(ESTUCO, o, 0.62f), p)
        greca(Rect(orla.left, orla.bottom - tb, orla.right, orla.bottom), n(CINABRIO, o, 0.5f), n(AZUL, o, 0.5f), n(ESTUCO, o, 0.62f), p)
        val dentro = Rect(orla.left, orla.top + tb, orla.right, orla.bottom - tb)

        val izq0 = dentro.left + 1.2f * u; val izq1 = ni.left - 1.6f * u
        val der0 = ni.right + 1.6f * u; val der1 = dentro.right - 1.2f * u
        val zona = minOf(izq1 - izq0, der1 - der0)
        val brasero = Offset((der0 + der1) / 2, ni.bottom - ni.height * 0.02f)
        mancha(Offset(brasero.x, brasero.y - zona * 0.4f), (if (o) 34f else 14f) * u, BRASA.copy(alpha = if (o) 0.26f else 0.1f))

        // La hoja de amate (el nicho), con su sombra, sus fibras y cuatro cuentas de jade en las esquinas.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ni.left + 0.8f * u, ni.top + 1.3f * u), ni.size)
        rect(ni, nicho(e, o, AMATE))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val fibra = (if (o) Color.White else AMATE_OSC).copy(alpha = if (o) 0.04f else 0.12f)
            for (k in 0 until 14) {
                val y = ni.top + ni.height * azar(e.semilla, k)
                drawPath(Path().apply { moveTo(ni.left, y); cubicTo(ni.left + ni.width * 0.3f, y + 2f * u, ni.left + ni.width * 0.6f, y - 2f * u, ni.right, y + u) }, fibra, style = Stroke(p * 0.7f))
            }
            rect(Rect(ni.left, ni.top, ni.right, ni.top + 2f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), ni.top, ni.top + 2f * u))
        }
        drawRect(n(CINABRIO, o, 0.5f), ni.topLeft, ni.size, style = Stroke(p * 0.7f))
        listOf(ni.topLeft, Offset(ni.right, ni.top), Offset(ni.left, ni.bottom), ni.bottomRight).forEach { c -> cuenta(c, 1.7f * u, o) }

        // El caracol que sirve de tintero, con su pincel, arriba a la izquierda.
        val rc = minOf(zona * 0.42f, 8.5f * u)
        caracol(Offset((izq0 + izq1) / 2, ni.top + rc + 2f * u), rc, o, p)
        // Las cazuelitas de pigmento: azul maya, cinabrio, ocre y negro de humo.
        val cz = minOf(zona * 0.2f, 3.4f * u)
        val zc = Offset((izq0 + izq1) / 2, ni.bottom - ni.height * 0.24f)
        listOf(AZUL, CINABRIO, OCRE, NEGRO).forEachIndexed { k, col ->
            val c = Offset(zc.x + (if (k % 2 == 0) -1f else 1f) * cz * 1.15f, zc.y + (k / 2) * cz * 2.4f)
            drawCircle(Color.Black.copy(alpha = 0.3f), cz, Offset(c.x + p, c.y + p * 1.6f))
            drawCircle(Brush.radialGradient(listOf(n(BARRO_LUZ, o), n(BARRO, o), mez(n(BARRO, o), Color.Black, 0.35f)), Offset(c.x - cz * 0.3f, c.y - cz * 0.3f), cz * 1.3f), cz, c)
            drawCircle(n(col, o, 0.45f), cz * 0.68f, c)
            drawCircle(Color.White.copy(alpha = 0.25f), cz * 0.2f, Offset(c.x - cz * 0.25f, c.y - cz * 0.25f))
        }
        // El códice plegado, arriba a la derecha.
        val cx = (der0 + der1) / 2
        val cdw = minOf(zona * 0.8f, 13f * u); val cdh = cdw * 1.25f
        for (k in 2 downTo 0) {
            val r = Rect(cx - cdw / 2 + k * 0.7f * u, ni.top + 1.6f * u + k * 0.8f * u, cx + cdw / 2 + k * 0.7f * u, ni.top + 1.6f * u + cdh + k * 0.8f * u)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 0.5f * u, r.top + 0.8f * u), r.size)
            rect(r, vert(r, n(ESTUCO, o), n(ESTUCO_OSC, o)))
            drawRect(n(AMATE_OSC, o), r.topLeft, r.size, style = Stroke(p * 0.5f))
            if (k == 0) {
                val q = r.deflate(cdw * 0.1f)
                drawRect(n(CINABRIO, o, 0.45f), q.topLeft, q.size, style = Stroke(p * 0.7f))
                numeral(13, Offset(q.center.x, q.top + q.height * 0.3f), q.width * 0.62f, n(NEGRO, o))
                drawCircle(n(AZUL, o, 0.45f), q.width * 0.16f, Offset(q.center.x, q.top + q.height * 0.72f))
                drawLine(n(CINABRIO, o, 0.45f), Offset(q.left, q.top + q.height * 0.5f), Offset(q.right, q.top + q.height * 0.5f), p * 0.5f)
            }
        }
        // El brasero de barro encendido, abajo a la derecha.
        clipRect(ni.right + p, dentro.top, dentro.right, dentro.bottom) { incensario(brasero, minOf(zona * 0.9f, 14f * u), o, p, 0.8f) }

        // El collar de jade y la navaja de obsidiana, al pie del amate.
        val yb = (ni.bottom + dentro.bottom) / 2
        val cuentas = 13
        for (k in 0 until cuentas) {
            val f = k / (cuentas - 1f)
            val c = Offset(ni.left + ni.width * (0.08f + 0.4f * f), yb + 1.2f * u * sin(f * 3.14f))
            cuenta(c, 0.95f * u, o)
        }
        val nx = ni.left + ni.width * 0.7f
        val nav = Path().apply { moveTo(nx, yb + 0.8f * u); lineTo(nx + 12f * u, yb - 0.6f * u); lineTo(nx + 13f * u, yb); lineTo(nx + 1f * u, yb + 1.4f * u); close() }
        drawPath(nav, Brush.linearGradient(listOf(n(OBSIDIANA, o), mez(n(OBSIDIANA, o), Color.White, 0.35f), n(OBSIDIANA, o)), Offset(nx, yb - u), Offset(nx + 13f * u, yb + u)))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(nx + 2f * u, yb + 0.7f * u), Offset(nx + 11f * u, yb - 0.3f * u), p * 0.4f)

        // El faldón con sus glifos y discos de jade, y las patas de jaguar.
        val fa = Rect(t.left + 1.2f * u, faldonA, t.right - 1.2f * u, faldonB)
        rect(fa, vert(fa, n(ZAPOTE, o), n(ZAPOTE_OSC, o)))
        drawLine(n(OCRE, o), Offset(fa.left, fa.top + p * 0.6f), Offset(fa.right, fa.top + p * 0.6f), p * 0.8f)
        val gs = fa.height * 0.66f
        val an = minOf(w * 0.06f, 9f * u)
        val g0 = fa.left + an * 2.4f; val g1 = fa.right - an * 2.4f
        val ng = ((g1 - g0) / (gs * 1.6f)).toInt().coerceAtLeast(1)
        for (k in 0 until ng) {
            val x = g0 + (g1 - g0) * (k + 0.5f) / ng
            if (k % 3 == 1) orejera(Offset(x, fa.center.y), gs * 0.4f, o)
            else cartucho(Rect(x - gs / 2, fa.center.y - gs / 2, x + gs / 2, fa.center.y + gs / 2), k, n(ZAPOTE_LUZ, o), o, p)
        }
        listOf(fa.left + an, fa.right - an).forEach { x -> pataJaguar(x, faldonB, suelo, an, o, p) }
    }

    /** Una cuenta de jade pulida. */
    private fun DrawScope.cuenta(c: Offset, r: Float, o: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, Offset(c.x + r * 0.2f, c.y + r * 0.35f))
        drawCircle(Brush.radialGradient(listOf(n(JADE_LUZ, o, 0.45f), n(JADE, o, 0.45f), n(JADE_OSC, o, 0.45f)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.4f), r, c)
        drawCircle(Color.White.copy(alpha = 0.5f), r * 0.25f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
    }

    /** El caracol marino que sirve de tintero: la espiral, la abertura con la tinta y un pincel de pluma. */
    private fun DrawScope.caracol(c: Offset, r: Float, o: Boolean, p: Float) {
        val concha = n(Color(0xFFEAD9C0), o)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - r * 0.9f, c.y - r * 0.5f + r * 0.2f), Size(r * 2f, r * 1.2f))
        val cuerpo = Path().apply {
            moveTo(c.x - r, c.y); cubicTo(c.x - r, c.y - r * 0.7f, c.x + r * 0.4f, c.y - r * 0.8f, c.x + r, c.y - r * 0.2f)
            lineTo(c.x + r * 1.05f, c.y + r * 0.1f); cubicTo(c.x + r * 0.4f, c.y + r * 0.6f, c.x - r * 0.6f, c.y + r * 0.6f, c.x - r, c.y); close()
        }
        drawPath(cuerpo, Brush.radialGradient(listOf(mez(concha, Color.White, 0.4f), concha, mez(concha, n(CINABRIO, o), 0.35f)), Offset(c.x - r * 0.2f, c.y - r * 0.3f), r * 1.4f))
        for (k in 1..3) drawArc(mez(concha, Color.Black, 0.35f), 200f, 120f, false, Offset(c.x - r * 0.95f + k * r * 0.25f, c.y - r * 0.55f + k * r * 0.05f), Size(r * (1.4f - k * 0.3f), r * 0.8f), style = Stroke(p * 0.6f))
        drawOval(n(NEGRO, o), Offset(c.x - r * 0.2f, c.y - r * 0.05f), Size(r * 0.9f, r * 0.42f))
        drawOval(Color.White.copy(alpha = 0.2f), Offset(c.x, c.y), Size(r * 0.3f, r * 0.12f))
        // El pincel: el mango de carrizo y la punta de pelo.
        val p0 = Offset(c.x + r * 0.2f, c.y + r * 0.1f); val p1 = Offset(c.x + r * 0.9f, c.y + r * 1.7f)
        drawLine(n(Color(0xFFCFB27A), o), p0, p1, p * 1.3f, StrokeCap.Round)
        drawLine(n(NEGRO, o), p0, Offset(p0.x + (p1.x - p0.x) * 0.15f, p0.y + (p1.y - p0.y) * 0.15f), p * 1.6f, StrokeCap.Round)
        drawLine(n(CINABRIO, o, 0.45f), Offset(p0.x + (p1.x - p0.x) * 0.55f, p0.y + (p1.y - p0.y) * 0.55f), Offset(p0.x + (p1.x - p0.x) * 0.62f, p0.y + (p1.y - p0.y) * 0.62f), p * 1.6f)
    }

    /** Una pata de jaguar: el muslo, la pata recta con sus rosetas y los dedos con garras. */
    private fun DrawScope.pataJaguar(cx: Float, y0: Float, y1: Float, an: Float, o: Boolean, p: Float) {
        val hh = y1 - y0
        val piel = n(JAGUAR, o); val osc = mez(piel, Color.Black, 0.4f); val luz = mez(piel, Color.White, 0.25f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cx - an * 0.8f, y1 - hh * 0.08f), Size(an * 1.6f, hh * 0.16f))
        val pata = Path().apply {
            moveTo(cx - an * 0.45f, y0); lineTo(cx + an * 0.45f, y0)
            cubicTo(cx + an * 0.5f, y0 + hh * 0.3f, cx + an * 0.25f, y0 + hh * 0.5f, cx + an * 0.3f, y1 - hh * 0.22f)
            lineTo(cx - an * 0.3f, y1 - hh * 0.22f)
            cubicTo(cx - an * 0.25f, y0 + hh * 0.5f, cx - an * 0.5f, y0 + hh * 0.3f, cx - an * 0.45f, y0); close()
        }
        drawPath(pata, Brush.horizontalGradient(listOf(osc, luz, piel, osc), cx - an * 0.5f, cx + an * 0.5f))
        listOf(0.2f to -0.15f, 0.4f to 0.15f, 0.58f to -0.08f).forEach { (f, dx) ->
            drawCircle(n(NEGRO, o), an * 0.09f, Offset(cx + dx * an, y0 + hh * f), style = Stroke(an * 0.05f))
        }
        // La zarpa: tres dedos y las garras.
        for (k in -1..1) {
            val dx = cx + k * an * 0.26f
            drawOval(Brush.verticalGradient(listOf(luz, piel, osc), y1 - hh * 0.28f, y1), Offset(dx - an * 0.16f, y1 - hh * 0.28f), Size(an * 0.32f, hh * 0.28f))
            drawPath(Path().apply { moveTo(dx - an * 0.05f, y1 - hh * 0.04f); lineTo(dx + an * 0.05f, y1 - hh * 0.04f); lineTo(dx + k * an * 0.04f, y1 + hh * 0.02f); close() }, n(ESTUCO, o, 0.5f))
        }
        rect(Rect(cx - an * 0.5f, y0, cx + an * 0.5f, y0 + hh * 0.08f), vert(Rect(0f, y0, 0f, y0 + hh * 0.08f), n(ZAPOTE_LUZ, o), n(ZAPOTE_OSC, o)))
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.OTRAS_VIDAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)

        // El muro de sillares.
        sillares(Rect(0f, 0f, w, h), o, p, 5.5f * u, e.semilla)

        // La cornisa, el friso de celosía con el mascarón y la moldura de atadura.
        val corB = h * 0.04f; val frisoB = h * 0.185f; val atB = ni.top - 0.8f * u
        val cor = Rect(0f, 0f, w, corB)
        rect(cor, vert(cor, n(CALIZA_LUZ, o), n(CALIZA_OSC, o)))
        val fr = Rect(0f, corB, w, frisoB)
        rect(fr, n(CALIZA_SOMBRA, o))
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            // La celosía: crucetas de piedra en diagonal.
            val paso = fr.height * 0.5f
            var x = -fr.height
            while (x < w + fr.height) {
                drawLine(n(CALIZA, o), Offset(x, fr.top), Offset(x + fr.height, fr.bottom), paso * 0.32f)
                drawLine(n(CALIZA_OSC, o), Offset(x + fr.height, fr.top), Offset(x, fr.bottom), paso * 0.32f)
                drawLine(n(CALIZA_LUZ, o), Offset(x - paso * 0.12f, fr.top), Offset(x + fr.height - paso * 0.12f, fr.bottom), p * 0.5f)
                x += paso
            }
        }
        drawLine(n(CALIZA_SOMBRA, o), Offset(0f, corB), Offset(w, corB), p)
        mascaron(Offset(w / 2, (corB + frisoB) / 2), fr.height * 0.98f, o, p)
        // La atadura: dos molduras con una fila de columnillas en medio.
        val at = Rect(0f, frisoB, w, atB)
        val m1 = Rect(0f, at.top, w, at.top + at.height * 0.25f)
        val m2 = Rect(0f, at.bottom - at.height * 0.25f, w, at.bottom)
        listOf(m1, m2).forEach { m -> rect(m, vert(m, n(CALIZA_LUZ, o), n(CALIZA, o), n(CALIZA_OSC, o))) }
        val col = Rect(0f, m1.bottom, w, m2.top)
        rect(col, n(CALIZA_SOMBRA, o))
        val cw = col.height * 0.5f
        var xc = cw * 0.4f
        while (xc < w) {
            val cr = Rect(xc, col.top, xc + cw, col.bottom)
            rect(cr, horiz(cr, n(CALIZA_OSC, o), n(CALIZA_LUZ, o), n(CALIZA, o), n(CALIZA_OSC, o)))
            drawLine(n(CALIZA_SOMBRA, o), Offset(cr.left, cr.center.y), Offset(cr.right, cr.center.y), p * 0.4f)
            xc += cw * 1.5f
        }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, at.bottom), Size(w, 1.2f * u))

        // Los cinco nichos de bóveda en saledizo, entre pilastras con glifos.
        val cwid = ni.width / 5
        val pil = minOf(3.4f * u, cwid * 0.09f)
        val ai = (cwid - pil) / 2 - 0.2f * u
        val pasos = 3
        val subida = ni.height * 0.15f
        val ys = ni.top + subida
        for (i in 0 until 5) {
            val cx = ni.left + cwid * (i + 0.5f)
            val hueco = Path().apply {
                moveTo(cx - ai, ni.bottom); lineTo(cx - ai, ys)
                for (k in 1..pasos) { val xk = cx - ai + ai * 0.62f * k / pasos; val yk = ys - subida * k / pasos; lineTo(cx - ai + ai * 0.62f * (k - 1) / pasos, yk); lineTo(xk, yk) }
                lineTo(cx + ai * 0.38f, ni.top)
                for (k in pasos downTo 1) { val xk = cx + ai - ai * 0.62f * k / pasos; val yk = ys - subida * k / pasos; lineTo(xk, yk); lineTo(cx + ai - ai * 0.62f * (k - 1) / pasos, yk) }
                lineTo(cx + ai, ys); lineTo(cx + ai, ni.bottom); close()
            }
            drawPath(hueco, Color.Black.copy(alpha = 0.35f), style = Stroke(1.6f * u))
            drawPath(hueco, nicho(e, o, ESTUCO))
            clipPath(hueco) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (o) 0.25f else 0.12f), Color.Transparent), ni.top, ys + subida * 0.5f), Offset(cx - ai, ni.top), Size(ai * 2, ys + subida * 0.5f - ni.top))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent), cx - ai, cx - ai + 2.4f * u), Offset(cx - ai, ys), Size(2.4f * u, ni.bottom - ys))
            }
            drawPath(hueco, n(CINABRIO, o, 0.5f), style = Stroke(p * 0.8f))
            // El dintel de la piedra de cierre.
            drawLine(n(CALIZA_SOMBRA, o), Offset(cx - ai * 0.38f, ni.top), Offset(cx + ai * 0.38f, ni.top), p * 1.2f)
        }
        // Las pilastras entre los nichos, con un glifo arriba.
        for (i in 0..5) {
            val x = (ni.left + cwid * i).coerceIn(ni.left + pil / 2, ni.right - pil / 2)
            val pr = Rect(x - pil / 2, ys, x + pil / 2, ni.bottom)
            rect(pr, horiz(pr, n(CALIZA_OSC, o), n(CALIZA_LUZ, o), n(CALIZA, o), n(CALIZA_OSC, o)))
            val gs = pil * 0.9f
            cartucho(Rect(x - gs / 2, ys + 1f * u, x + gs / 2, ys + 1f * u + gs), i * 5 + 2, n(CALIZA, o), o, p)
        }

        // El tablero pintado y el talud.
        val tab = Rect(0f, ni.bottom + 0.6f * u, w, h * 0.9f)
        rect(tab, vert(tab, n(CALIZA_LUZ, o), n(CALIZA, o)))
        val tabIn = Rect(tab.left + 2f * u, tab.top + tab.height * 0.22f, tab.right - 2f * u, tab.bottom - tab.height * 0.22f)
        rect(tabIn, n(CINABRIO, o, 0.55f))
        drawRect(n(NEGRO, o), tabIn.topLeft, tabIn.size, style = Stroke(p * 0.6f))
        var xp = tabIn.left + 3f * u
        while (xp < tabIn.right - 2f * u) { drawCircle(n(ESTUCO, o, 0.45f), tabIn.height * 0.16f, Offset(xp, tabIn.center.y)); xp += 4f * u }
        val talud = Path().apply { moveTo(0f, tab.bottom); lineTo(w, tab.bottom); lineTo(w, h); lineTo(0f, h); close() }
        drawPath(talud, Brush.verticalGradient(listOf(n(CINABRIO_OSC, o, 0.55f), mez(n(CINABRIO_OSC, o, 0.55f), Color.Black, 0.3f)), tab.bottom, h))
        drawLine(n(CALIZA_SOMBRA, o), Offset(0f, tab.bottom), Offset(w, tab.bottom), p)
    }

    /** El mascarón de Chaac: la cara de piedra con los ojos cuadrados, la nariz en gancho, los colmillos y las orejeras. */
    private fun DrawScope.mascaron(c: Offset, alto: Float, o: Boolean, p: Float) {
        val an = alto * 1.5f
        val cara = Rect(c.x - an / 2, c.y - alto / 2, c.x + an / 2, c.y + alto / 2)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(cara.left + p * 1.4f, cara.top + p * 2f), cara.size)
        rect(cara, vert(cara, n(CALIZA_LUZ, o), n(CALIZA, o), n(CALIZA_OSC, o)))
        // La banda de la frente, pintada.
        rect(Rect(cara.left, cara.top, cara.right, cara.top + alto * 0.2f), n(CINABRIO, o, 0.5f))
        for (k in 0 until 3) drawRect(n(ESTUCO, o, 0.45f), Offset(cara.left + an * (0.2f + 0.26f * k) - alto * 0.05f, cara.top + alto * 0.05f), Size(alto * 0.1f, alto * 0.1f), style = Stroke(p * 0.6f))
        // Los ojos cuadrados con sus pupilas.
        listOf(-1f, 1f).forEach { s ->
            val ojo = Rect(c.x + s * an * 0.24f - alto * 0.13f, c.y - alto * 0.18f, c.x + s * an * 0.24f + alto * 0.13f, c.y + alto * 0.04f)
            rect(ojo, n(CALIZA_SOMBRA, o))
            rect(ojo.deflate(alto * 0.03f), n(CALIZA_LUZ, o))
            drawRect(n(NEGRO, o), Offset(ojo.center.x - alto * 0.05f, ojo.center.y - alto * 0.04f), Size(alto * 0.1f, alto * 0.08f))
        }
        // La nariz en gancho que se enrosca hacia arriba.
        drawPath(Path().apply {
            moveTo(c.x - alto * 0.08f, c.y - alto * 0.14f); lineTo(c.x + alto * 0.08f, c.y - alto * 0.14f); lineTo(c.x + alto * 0.1f, c.y + alto * 0.12f)
            quadraticTo(c.x + alto * 0.22f, c.y + alto * 0.02f, c.x + alto * 0.14f, c.y - alto * 0.08f); lineTo(c.x + alto * 0.2f, c.y - alto * 0.1f)
            quadraticTo(c.x + alto * 0.3f, c.y + alto * 0.12f, c.x + alto * 0.08f, c.y + alto * 0.2f); lineTo(c.x - alto * 0.1f, c.y + alto * 0.2f); close()
        }, horiz(Rect(c.x - alto * 0.1f, 0f, c.x + alto * 0.3f, 0f), n(CALIZA_OSC, o), n(CALIZA_LUZ, o), n(CALIZA, o)))
        // La boca con los colmillos.
        val boca = Rect(c.x - an * 0.24f, c.y + alto * 0.26f, c.x + an * 0.24f, c.y + alto * 0.42f)
        rect(boca, n(NEGRO, o))
        for (k in 0 until 4) {
            val x = boca.left + boca.width * (k + 0.5f) / 4
            drawPath(Path().apply { moveTo(x - boca.width * 0.06f, boca.top); lineTo(x + boca.width * 0.06f, boca.top); lineTo(x, boca.bottom - boca.height * 0.1f); close() }, n(ESTUCO, o, 0.5f))
        }
        listOf(-1f, 1f).forEach { s -> orejera(Offset(c.x + s * (an / 2 + alto * 0.24f), c.y), alto * 0.2f, o) }
        drawRect(n(CALIZA_SOMBRA, o), cara.topLeft, cara.size, style = Stroke(p * 0.6f))
    }

    // ---------------------------------------------------------------- Íconos

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val o = oscuro
        val c = Offset(w / 2, h / 2)
        val s = minOf(h / 76f, w / 150f)
        val p = density * 0.8f
        when (id) {
            "unesco" -> {
                // La pirámide escalonada con su escalinata, el templo con la crestería, y la selva a los lados.
                val base = c.y + 30f * s
                val selva = Color(0xFF3F6B45)
                listOf(-56f to 14f, -44f to 11f, 46f to 13f, 58f to 10f).forEach { (x, r) ->
                    drawLine(Color(0xFF6A4A2A), Offset(c.x + x * s, base), Offset(c.x + x * s, base - r * 1.4f * s), 2.2f * s)
                    drawCircle(mez(selva, Color.Black, 0.2f), r * s, Offset(c.x + x * s, base - r * 1.6f * s))
                    drawCircle(selva, r * 0.75f * s, Offset(c.x + x * s - r * 0.25f * s, base - r * 1.85f * s))
                }
                val piedra = if (o) Color(0xFFBCAF92) else CALIZA
                val sombra = mez(piedra, Color.Black, 0.3f)
                val niveles = 6
                val nh = 6f * s
                for (k in 0 until niveles) {
                    val an = (44f - k * 6f) * s
                    val y1 = base - k * nh; val y0 = y1 - nh
                    val q = Path().apply { moveTo(c.x - an, y1); lineTo(c.x + an, y1); lineTo(c.x + an - 1.6f * s, y0); lineTo(c.x - an + 1.6f * s, y0); close() }
                    drawPath(q, Brush.horizontalGradient(listOf(sombra, piedra, mez(piedra, Color.White, 0.25f), piedra), c.x - an, c.x + an))
                    drawLine(sombra, Offset(c.x - an + 1.6f * s, y0 + nh * 0.35f), Offset(c.x + an - 1.6f * s, y0 + nh * 0.35f), p * 0.6f)
                }
                // La escalinata al centro, con su alfarda.
                val top = base - niveles * nh
                drawPath(Path().apply { moveTo(c.x - 9f * s, base); lineTo(c.x + 9f * s, base); lineTo(c.x + 6f * s, top); lineTo(c.x - 6f * s, top); close() }, mez(piedra, Color.Black, 0.12f))
                var ye = base - 2f * s
                while (ye > top) { drawLine(sombra, Offset(c.x - 8f * s, ye), Offset(c.x + 8f * s, ye), p * 0.6f); ye -= 2.4f * s }
                // El templo con su puerta y la crestería pintada.
                val tem = Rect(c.x - 9f * s, top - 10f * s, c.x + 9f * s, top)
                rect(tem, horiz(tem, sombra, piedra, mez(piedra, Color.White, 0.2f), sombra))
                drawPath(Path().apply { moveTo(c.x - 3f * s, top); lineTo(c.x - 3f * s, top - 5f * s); lineTo(c.x - 1.5f * s, top - 6.5f * s); lineTo(c.x + 1.5f * s, top - 6.5f * s); lineTo(c.x + 3f * s, top - 5f * s); lineTo(c.x + 3f * s, top); close() }, mez(piedra, Color.Black, 0.6f))
                rect(Rect(tem.left - 1f * s, tem.top - 1.5f * s, tem.right + 1f * s, tem.top), e.c1)
                val cre = Rect(c.x - 6f * s, tem.top - 9f * s, c.x + 6f * s, tem.top - 1.5f * s)
                rect(cre, e.c1)
                for (k in 0 until 3) rect(Rect(cre.left + (1.2f + k * 3.6f) * s, cre.top + 2f * s, cre.left + (2.6f + k * 3.6f) * s, cre.bottom - 2f * s), mez(e.c1, Color.Black, 0.45f))
                drawLine(e.c2, Offset(c.x - 70f * s, base), Offset(c.x + 70f * s, base), p * 1.4f)
            }
            "rascar" -> {
                // Una hoja de amate pintada, cubierta de cinabrio que un hacha de jade va rascando desde una esquina.
                val v = Rect(c.x - 46f * s, c.y - 30f * s, c.x + 28f * s, c.y + 30f * s)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(v.left + 2f * s, v.top + 3f * s), v.size)
                rect(v, vert(v, mez(AMATE, Color.White, 0.2f), AMATE, mez(AMATE, Color.Black, 0.08f)))
                for (k in 0 until 6) drawLine(AMATE_OSC.copy(alpha = 0.5f), Offset(v.left, v.top + v.height * (k + 0.5f) / 6), Offset(v.right, v.top + v.height * (k + 0.3f) / 6), p * 0.5f)
                // Lo que ya se ve: un río azul y huellas.
                drawPath(Path().apply { moveTo(v.left + v.width * 0.45f, v.top); quadraticTo(v.left + v.width * 0.75f, v.top + v.height * 0.3f, v.right, v.top + v.height * 0.25f) }, e.c2, style = Stroke(p * 2f))
                listOf(0.7f to 0.18f, 0.82f to 0.1f).forEach { (fx, fy) -> drawOval(NEGRO, Offset(v.left + v.width * fx, v.top + v.height * fy), Size(2.4f * s, 3.6f * s)) }
                val capa = Path().apply { moveTo(v.left, v.top); lineTo(v.left + v.width * 0.48f, v.top); quadraticTo(v.left + v.width * 0.62f, v.top + v.height * 0.42f, v.right, v.top + v.height * 0.58f); lineTo(v.right, v.bottom); lineTo(v.left, v.bottom); close() }
                drawPath(capa, Brush.linearGradient(listOf(CINABRIO_LUZ, CINABRIO, CINABRIO_OSC), v.topLeft, v.bottomRight))
                for (k in 0 until 5) drawLine(Color.Black.copy(alpha = 0.15f), Offset(v.left + v.width * (0.08f + 0.12f * k), v.bottom - 2f * s), Offset(v.left + v.width * (0.24f + 0.12f * k), v.top + v.height * 0.45f), p * 0.7f)
                greca(Rect(v.left + 4f * s, v.bottom - 12f * s, v.right - 4f * s, v.bottom - 4f * s), ESTUCO, OCRE, CINABRIO_OSC, p * 0.6f)
                drawPath(Path().apply { moveTo(v.left + v.width * 0.48f, v.top); quadraticTo(v.left + v.width * 0.62f, v.top + v.height * 0.42f, v.right, v.top + v.height * 0.58f) }, CINABRIO_OSC, style = Stroke(p))
                // El hacha de jade: la hoja pulida, en diagonal.
                val hc = Offset(c.x + 34f * s, c.y + 2f * s)
                val hacha = Path().apply { moveTo(hc.x - 8f * s, hc.y - 22f * s); lineTo(hc.x + 8f * s, hc.y - 20f * s); lineTo(hc.x + 14f * s, hc.y + 20f * s); quadraticTo(hc.x, hc.y + 28f * s, hc.x - 12f * s, hc.y + 18f * s); close() }
                translate(2f * s, 3f * s) { drawPath(hacha, Color.Black.copy(alpha = 0.3f)) }
                drawPath(hacha, Brush.linearGradient(listOf(JADE_LUZ, JADE, JADE_OSC), Offset(hc.x - 10f * s, hc.y - 20f * s), Offset(hc.x + 12f * s, hc.y + 22f * s)))
                drawLine(Color.White.copy(alpha = 0.5f), Offset(hc.x - 4f * s, hc.y - 18f * s), Offset(hc.x - 6f * s, hc.y + 12f * s), p * 1.2f)
                drawPath(hacha, JADE_OSC, style = Stroke(p * 0.6f))
                numeral(7, Offset(hc.x + 1f * s, hc.y - 2f * s), 10f * s, JADE_OSC)
            }
            else -> {
                // Una tira de amate con tres glifos de cerro unidos por huellas de pies, como en los códices.
                val v = Rect(c.x - 60f * s, c.y - 26f * s, c.x + 60f * s, c.y + 28f * s)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(v.left + 2f * s, v.top + 3f * s), v.size)
                val am = if (o) Color(0xFFCDB890) else AMATE_CLARO
                rect(v, vert(v, mez(am, Color.White, 0.2f), am, mez(am, Color.Black, 0.08f)))
                for (k in 0 until 7) drawLine(AMATE_OSC.copy(alpha = 0.35f), Offset(v.left, v.top + v.height * (k + 0.5f) / 7), Offset(v.right, v.top + v.height * (k + 0.2f) / 7), p * 0.5f)
                drawRect(CINABRIO, v.topLeft, v.size, style = Stroke(p * 1.2f))
                val pts = listOf(Offset(c.x - 40f * s, c.y + 6f * s), Offset(c.x, c.y - 4f * s), Offset(c.x + 40f * s, c.y + 8f * s))
                // Las huellas entre los cerros.
                for (j in 0 until 2) {
                    val a0 = pts[j]; val a1 = pts[j + 1]
                    for (k in 1..4) {
                        val f = k / 5f
                        val q = Offset(a0.x + (a1.x - a0.x) * f, a0.y + (a1.y - a0.y) * f + (if (k % 2 == 0) 2.4f else -2.4f) * s + 4f * s)
                        drawOval(NEGRO, Offset(q.x - 1.6f * s, q.y - 2.2f * s), Size(3.2f * s, 4.4f * s))
                        drawCircle(NEGRO, 0.8f * s, Offset(q.x + 1.6f * s, q.y - 2.8f * s))
                    }
                }
                val cols = listOf(e.c1, e.c2, JADE)
                pts.forEachIndexed { k, q -> cerro(q, 12f * s, cols[k], p) }
            }
        }
    }

    /** El glifo de cerro (tepetl): la campana verde con su banda, su base y el rizo en la cima, con [color] dentro. */
    private fun DrawScope.cerro(c: Offset, r: Float, color: Color, p: Float) {
        val forma = Path().apply {
            moveTo(c.x - r, c.y + r * 0.6f); cubicTo(c.x - r * 0.9f, c.y - r * 0.2f, c.x - r * 0.6f, c.y - r, c.x, c.y - r)
            cubicTo(c.x + r * 0.6f, c.y - r, c.x + r * 0.9f, c.y - r * 0.2f, c.x + r, c.y + r * 0.6f); close()
        }
        translate(r * 0.1f, r * 0.15f) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, Brush.radialGradient(listOf(mez(color, Color.White, 0.3f), color, mez(color, Color.Black, 0.3f)), Offset(c.x - r * 0.3f, c.y - r * 0.4f), r * 1.6f))
        drawPath(forma, mez(color, Color.Black, 0.5f), style = Stroke(p))
        rect(Rect(c.x - r, c.y + r * 0.45f, c.x + r, c.y + r * 0.75f), OCRE)
        drawRect(NEGRO, Offset(c.x - r, c.y + r * 0.45f), Size(r * 2, r * 0.3f), style = Stroke(p * 0.6f))
        for (k in 0 until 4) drawCircle(CINABRIO, r * 0.07f, Offset(c.x - r * 0.7f + k * r * 0.47f, c.y + r * 0.6f))
        drawArc(mez(color, Color.Black, 0.5f), 180f, 270f, false, Offset(c.x - r * 0.18f, c.y - r * 1.3f), Size(r * 0.36f, r * 0.36f), style = Stroke(p * 1.2f))
        drawCircle(Color.White.copy(alpha = 0.4f), r * 0.14f, Offset(c.x - r * 0.35f, c.y - r * 0.4f))
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f; val p = u * 0.45f
        val o = oscuro
        val a = area(MuebleVida.NOTAS)
        val ni = Rect(w * a.left, h * a.top, w * a.right, h * a.bottom)
        val t = 2.4f * u
        val marco = ni.inflate(t)

        // El muro de sillares.
        sillares(Rect(0f, 0f, w, h), o, p, 6f * u, e.semilla)

        // La cornisa con la greca y el dintel de chicozapote con sus glifos.
        val corB = h * 0.022f; val grecaB = h * 0.06f
        rect(Rect(0f, 0f, w, corB), vert(Rect(0f, 0f, w, corB), n(CALIZA_LUZ, o), n(CALIZA_OSC, o)))
        greca(Rect(0f, corB, w, grecaB), n(CINABRIO, o, 0.5f), n(AZUL, o, 0.5f), n(ESTUCO, o, 0.62f), p)
        val din = Rect(w * 0.01f, grecaB + 0.4f * u, w * 0.99f, marco.top - 0.6f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(din.left, din.bottom), Size(din.width, 1.4f * u))
        rect(din, vert(din, n(ZAPOTE_LUZ, o), n(ZAPOTE, o), n(ZAPOTE_OSC, o)))
        vetas(din, n(VETA, o).copy(alpha = 0.45f), e.semilla, p, 0.7f)
        val gs = din.height * 0.74f
        val ng = (din.width / (gs * 1.45f)).toInt()
        for (k in 0 until ng) {
            val x = din.left + din.width * (k + 0.5f) / ng
            if (k == ng / 2) orejera(Offset(x, din.center.y), gs * 0.5f, o)
            else cartucho(Rect(x - gs / 2, din.center.y - gs / 2, x + gs / 2, din.center.y + gs / 2), k, n(ZAPOTE, o), o, p)
        }

        // Las jambas de caliza a los lados, con su columna de numerales.
        val zl = marco.left
        val bancaTop = marco.bottom + 1.2f * u
        listOf(zl / 2, w - zl / 2).forEachIndexed { j, x ->
            val gj = minOf(zl * 0.62f, 6f * u)
            var y = marco.top + gj
            var k = 3 + j * 5
            while (y < bancaTop - gj * 0.4f) { cartucho(Rect(x - gj / 2, y - gj / 2, x + gj / 2, y + gj / 2), k * 4, n(CALIZA, o), o, p); y += gj * 1.4f; k++ }
        }

        // El marco pintado: banda de cinabrio con puntos y un filete negro, y el estuco liso del nicho.
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(marco.left + 0.8f * u, marco.top + 1.3f * u), marco.size)
        rect(marco, n(CINABRIO, o, 0.5f))
        drawRect(n(NEGRO, o), marco.topLeft, marco.size, style = Stroke(p * 0.9f))
        val np = (marco.width / (3f * u)).toInt()
        for (k in 0 until np) listOf(marco.top + t / 2, marco.bottom - t / 2).forEach { y -> drawCircle(n(ESTUCO, o, 0.45f), t * 0.15f, Offset(marco.left + marco.width * (k + 0.5f) / np, y)) }
        val nv = (marco.height / (3f * u)).toInt()
        for (k in 1 until nv - 1) listOf(marco.left + t / 2, marco.right - t / 2).forEach { x -> drawCircle(n(ESTUCO, o, 0.45f), t * 0.15f, Offset(x, marco.top + marco.height * (k + 0.5f) / nv)) }
        rect(ni.inflate(0.5f * u), n(NEGRO, o))
        rect(ni, nicho(e, o, ESTUCO))
        val rnd = Random(e.semilla.toLong() * 19 + 11)
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            repeat(12) { mancha(Offset(ni.left + ni.width * rnd.nextFloat(), ni.top + ni.height * rnd.nextFloat()), (5f + 9f * rnd.nextFloat()) * u, (if (o) Color.White else ESTUCO_OSC).copy(alpha = if (o) 0.03f else 0.2f)) }
        }
        rect(Rect(ni.left, ni.top, ni.right, ni.top + 3f * u), Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent), ni.top, ni.top + 3f * u))
        listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { c -> orejera(c, t * 0.7f, o) }

        // La banca de piedra, con un incensario y un vaso en los extremos.
        val banca = Rect(0f, bancaTop, w, h)
        sillares(banca, o, p, (h - bancaTop) * 0.5f, e.semilla + 3)
        rect(Rect(0f, bancaTop, w, bancaTop + 1.2f * u), vert(Rect(0f, bancaTop, w, bancaTop + 1.2f * u), n(CALIZA_LUZ, o), n(CALIZA_OSC, o)))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, bancaTop + 1.2f * u), Size(w, 0.8f * u))
        val hb = h - bancaTop
        clipRect(0f, marco.bottom + p, w, h) { incensario(Offset(w * 0.2f, h - hb * 0.08f), hb * 0.62f, o, p, 0.4f) }
        vaso(Offset(w * 0.8f, h - hb * 0.08f), hb * 0.6f, o, p)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        val hoja = amate(r.deflate(2f * un), semilla)
        translate(1.2f * un, 2.2f * un) { drawPath(hoja, Color.Black.copy(alpha = 0.32f)) }
        val am = if (o) AMATE_NOCHE else AMATE_CLARO
        drawPath(hoja, Brush.linearGradient(listOf(mez(am, Color.White, if (o) 0.04f else 0.2f), am, mez(am, Color.Black, 0.07f)), r.topLeft, r.bottomRight))
        clipPath(hoja) {
            // Las fibras del amate, onduladas, y alguna más oscura.
            for (k in 0 until 9) {
                val y = r.top + r.height * (k + azar(semilla, k)) / 9
                val col = mez(am, if (o) Color.White else Color(0xFF7A5A30), if (k % 3 == 0) 0.16f else 0.08f)
                drawPath(Path().apply { moveTo(r.left, y); cubicTo(r.left + r.width * 0.3f, y + 3f * un, r.left + r.width * 0.6f, y - 3f * un, r.right, y + 1.5f * un) }, col, style = Stroke((0.5f + azar(semilla, k + 30)) * un))
            }
        }
        drawPath(hoja, mez(am, Color.Black, 0.3f), style = Stroke(0.6f * un, join = StrokeJoin.Round))
        // El marco rojo de códice, por fuera del texto; la fijada lo lleva doble.
        val rojo = if (o) Color(0xFFE07A5C) else CINABRIO
        val m = Rect(r.left + r.width * 0.07f, r.top + r.height * 0.1f, r.right - r.width * 0.07f, r.bottom - r.height * 0.07f)
        drawRect(rojo, m.topLeft, m.size, style = Stroke(0.7f * un))
        if (e.fijada) drawRect(rojo, m.deflate(1.4f * un).topLeft, m.deflate(1.4f * un).size, style = Stroke(0.4f * un))

        // El glifo del tipo, abajo a la derecha.
        val q = Offset(r.right - r.width * 0.125f, r.bottom - r.height * 0.105f)
        val g = r.height * 0.05f
        when (e.tipo) {
            "idea" -> {
                // K'in, el sol: la flor de cuatro pétalos en su círculo.
                val col = if (o) Color(0xFFE6B35E) else Color(0xFFB0701E)
                drawCircle(col, g * 1.1f, q, style = Stroke(0.6f * un))
                for (k in 0 until 4) { val a = k * Math.PI / 2; drawOval(col, Offset(q.x + g * 0.45f * cos(a).toFloat() - g * 0.22f, q.y + g * 0.45f * sin(a).toFloat() - g * 0.22f), Size(g * 0.44f, g * 0.44f)) }
                drawCircle(col, g * 0.16f, q)
            }
            "pensamiento" -> {
                // La vírgula de la palabra: la voluta que sale de la boca.
                val col = if (o) AZUL_LUZ else AZUL
                drawPath(Path().apply { moveTo(q.x - g * 1.1f, q.y + g * 0.8f); cubicTo(q.x - g * 1.1f, q.y - g * 1.2f, q.x + g * 1.2f, q.y - g * 1.2f, q.x + g * 0.8f, q.y + g * 0.1f); quadraticTo(q.x + g * 0.4f, q.y + g * 0.6f, q.x, q.y + g * 0.05f) }, col, style = Stroke(0.9f * un, cap = StrokeCap.Round))
                drawCircle(col, g * 0.22f, Offset(q.x, q.y + g * 0.05f))
            }
            "proposito" -> {
                // Una huella de pie: el camino.
                val col = if (o) JADE_LUZ else JADE
                drawOval(col, Offset(q.x - g * 0.45f, q.y - g * 0.5f), Size(g * 0.9f, g * 1.4f))
                for (k in 0 until 4) drawCircle(col, g * 0.15f, Offset(q.x - g * 0.4f + k * g * 0.27f, q.y - g * 0.8f - (if (k == 0) g * 0.1f else 0f)))
            }
            else -> numeral(8, q, g * 2.4f, rojo)
        }

        // La espina de maguey que la clava; la fijada lleva además un disco de jade atado con hilo de algodón.
        val cl = Offset(r.center.x, r.top + r.height * 0.05f)
        if (e.fijada) {
            val hilo = if (o) Color(0xFFBFAF8A) else Color(0xFFF2EAD8)
            drawLine(mez(hilo, Color.Black, 0.25f), cl, Offset(cl.x - 5f * un, cl.y + 6f * un), 0.7f * un, StrokeCap.Round)
            drawLine(mez(hilo, Color.Black, 0.25f), cl, Offset(cl.x + 4f * un, cl.y + 6.4f * un), 0.7f * un, StrokeCap.Round)
            val rj = 3.6f * un
            drawCircle(Color.Black.copy(alpha = 0.35f), rj, Offset(cl.x + 0.8f * un, cl.y + 1.4f * un))
            drawCircle(Brush.radialGradient(listOf(JADE_LUZ, JADE, JADE_OSC), Offset(cl.x - rj * 0.35f, cl.y - rj * 0.35f), rj * 1.5f), rj, cl)
            drawCircle(JADE_OSC, rj * 0.35f, cl)
            drawCircle(Color.White.copy(alpha = 0.5f), rj * 0.22f, Offset(cl.x - rj * 0.45f, cl.y - rj * 0.45f))
        }
        val esp = Path().apply { moveTo(cl.x - 4.5f * un, cl.y - 3.5f * un); lineTo(cl.x + 3.5f * un, cl.y + 2.5f * un); lineTo(cl.x - 5.3f * un, cl.y - 2.4f * un); close() }
        drawPath(esp, Brush.linearGradient(listOf(Color(0xFF6A4A2A), Color(0xFF2A1A10)), Offset(cl.x - 5f * un, cl.y - 3f * un), Offset(cl.x + 3.5f * un, cl.y + 2.5f * un)))
        drawCircle(Color.Black.copy(alpha = 0.3f), 0.8f * un, Offset(cl.x + 3.4f * un, cl.y + 2.8f * un))
    }

    /** Una hoja de amate: casi rectangular, con los bordes desflecados de la corteza batida. */
    private fun amate(r: Rect, semilla: Int): Path {
        val rnd = Random(semilla.toLong() * 104729L + 3L)
        val path = Path()
        val pasos = 10
        path.moveTo(r.left, r.top)
        for (k in 1..pasos) path.lineTo(r.left + r.width * k / pasos, r.top + r.height * 0.015f * rnd.nextFloat())
        for (k in 1..pasos) path.lineTo(r.right - r.width * 0.02f * rnd.nextFloat(), r.top + r.height * k / pasos)
        for (k in 1..pasos) path.lineTo(r.right - r.width * k / pasos, r.bottom - r.height * 0.018f * rnd.nextFloat())
        for (k in 1 until pasos) path.lineTo(r.left + r.width * 0.02f * rnd.nextFloat(), r.bottom - r.height * k / pasos)
        path.close()
        return path
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0E3C6) else Color(0xFF2A1810)
}
