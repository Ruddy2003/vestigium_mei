package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Japón Edo: los muebles de Vida y de Diario de la posta, de la misma casa que su tansu de keyaki del Ideario, su hondana
 * de paulonia y su sala de los sellos: keyaki veteado, paulonia (kiri) clara, laca negra (urushi) y bermellón (shu),
 * herrajes de hierro negro y de oro, washi, tatami con su ribete añil, tejas grises y el cobre verde de los santuarios.
 * - Social: la sala de tatami donde se recibe: el techo de tablas de cedro, el ranma calado de olas (seigaiha) con el
 *   blasón del ciruelo, el nageshi de ciprés con sus cubreclavos de oro, los postes, los shoji con su cuadrícula y su
 *   farolillo rojo colgado (de noche, encendido y con su halo); al centro, el gran biombo de washi con sus nubes de oro,
 *   su marco de laca y sus herrajes; en el tatami, dos cojines añiles y el brasero de keyaki con su tetera de hierro.
 * - Mapas: la mesa baja de laca negra del agrimensor: el tablero con su filete de maki-e y ciruelos de oro, el rollo
 *   desplegado (la washi del mapa con su ribete de brocado añil y sus dos rodillos con perillas); a un lado, la piedra de
 *   tinta, la barra de sumi, el pincel y el gotero; al otro, la brújula de agrimensor y el ábaco; el canto con su greca de
 *   oro, el faldón con sus herrajes y las patas curvas sobre el tatami.
 * - Otras vidas: el relicario de keyaki: el tejado de tejas con su caballete, sus tejas de remate y el hastial con el
 *   blasón de oro, la viga con sus cubreclavos, cinco nichos de paulonia (de noche, en penumbra) con su ventana de campana
 *   (katōmado) separados por postes, los postes de laca negra con herrajes y borlas, y la base de cajoncitos con anillas.
 * - Íconos: la pagoda de cinco pisos entre un pino y una linterna de piedra; el mapa de washi raspado por un koban de oro;
 *   tres torii bermellón unidos por el camino de posta.
 * - Notas: el colgadero de ema del santuario: el tejadillo de corteza con su caballete de cobre verde, los postes
 *   bermellón, la cuerda sagrada (shimenawa) con sus papeles en zigzag, el tablado de fondo con sus dos varales, la
 *   grava rastrillada; las notas son ema de paulonia (la tablilla votiva de cinco lados) colgadas de su cordón de
 *   cáñamo o, si están fijadas, del cordón rojo y blanco con su nudo y su remate de oro, y con un detalle según el tipo
 *   (la flor del ciruelo, la luna sobre las olas, el sello bermellón o la campanilla).
 */
internal object VidaEdo : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su Ideario y su Hall)
    private val KEYAKI = Color(0xFF8B5A2E)
    private val KEYAKI_LUZ = Color(0xFFB98552)
    private val KEYAKI_OSC = Color(0xFF55331A)
    private val KEYAKI_VETA = Color(0xFF4A2C14)
    private val KIRI = Color(0xFFE2CFA8)
    private val KIRI_LUZ = Color(0xFFF0E4C8)
    private val KIRI_VETA = Color(0xFFB89D70)
    private val HINOKI = Color(0xFFD9BD8B)
    private val HINOKI_OSC = Color(0xFFAE8E5C)
    private val SUGI = Color(0xFF7A4E30)
    private val URUSHI = Color(0xFF231C18)
    private val URUSHI_LUZ = Color(0xFF4F4137)
    private val SHU = Color(0xFFB8322A)
    private val SHU_LUZ = Color(0xFFD9573F)
    private val SHU_OSC = Color(0xFF7E1F18)
    private val HIERRO = Color(0xFF1E1B1A)
    private val HIERRO_LUZ = Color(0xFF5C5650)
    private val ORO = Color(0xFFC9A24C)
    private val ORO_LUZ = Color(0xFFEBD08A)
    private val ORO_OSC = Color(0xFF86662A)
    private val WASHI = Color(0xFFF4EEDF)
    private val WASHI_OSC = Color(0xFFDCD0B4)
    private val SUMI = Color(0xFF1B1A1E)
    private val ANIL = Color(0xFF1E3A5F)
    private val ANIL_LUZ = Color(0xFF3B5D8A)
    private val ANIL_OSC = Color(0xFF13263F)
    private val TATAMI = Color(0xFFC8B77A)
    private val TATAMI_LUZ = Color(0xFFD9CB93)
    private val TATAMI_OSC = Color(0xFF9A8A50)
    private val HERI = Color(0xFF1F2B3D)
    private val PIEDRA = Color(0xFF9A958A)
    private val PIEDRA_LUZ = Color(0xFFBDB8AC)
    private val PIEDRA_OSC = Color(0xFF66625A)
    private val TEJA = Color(0xFF4A4D52)
    private val TEJA_LUZ = Color(0xFF767A80)
    private val TEJA_OSC = Color(0xFF2C2E32)
    private val COBRE = Color(0xFF5E8C7A)
    private val COBRE_LUZ = Color(0xFF8DB5A3)
    private val CORTEZA = Color(0xFF6A4630)
    private val CORTEZA_OSC = Color(0xFF452C1C)
    private val PAJA = Color(0xFFCDB577)
    private val PAJA_OSC = Color(0xFF94793F)
    private val CIRUELO = Color(0xFFE38E9C)
    private val PINO = Color(0xFF3F5E3A)
    private val PINO_LUZ = Color(0xFF5E7E4E)
    private val COBRE_ROJO = Color(0xFFA8643A)
    private val LLAMA = Color(0xFFFFC870)
    private val BRASA = Color(0xFFFF7A3A)
    private val NOCHE = Color(0xFF0E0C0A)

    /** La washi del biombo (el nicho de Social): crema de día; de noche, la washi en la penumbra de la sala. */
    private fun washiNicho(oscuro: Boolean) = if (oscuro) Color(0xFF1D1F27) else Color(0xFFF8F3E6)

    /** La washi del mapa: clara de día; de noche, a la luz del farol. */
    private fun mapaNicho(oscuro: Boolean) = if (oscuro) Color(0xFF221F1C) else Color(0xFFF6F0E1)

    /** El fondo de paulonia de los nichos del relicario. */
    private fun relicarioNicho(oscuro: Boolean) = if (oscuro) Color(0xFF1F1C1A) else Color(0xFFF4ECD9)

    /** El tablado claro de ciprés detrás de los ema. */
    private fun tabladoNicho(oscuro: Boolean) = if (oscuro) Color(0xFF1F1D20) else Color(0xFFF3ECDC)

    /** El color en oscuro: más hundido, como en su biblioteca y su Hall. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.34f) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.135f, 0.215f, 0.865f, 0.8f)
        MuebleVida.MAPAS -> Rect(0.18f, 0.1f, 0.82f, 0.7f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.075f, 0.225f, 0.925f, 0.8f)
        MuebleVida.NOTAS -> Rect(0.08f, 0.2f, 0.92f, 0.895f)
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un número estable de 0 a 1 para lo que varía (vetas, nudos). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Las vetas de la madera en [r], recortadas a la pieza: líneas onduladas a lo largo, más o menos marcadas. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, densidad: Float = 1f, vertical: Boolean = false, grosor: Float = 1f, ondulacion: Float = 1f) =
        clipRect(r.left, r.top, r.right, r.bottom) {
            val d = density
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            val n = (ancho / (3.2f * d) * densidad).toInt().coerceIn(2, 60)
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.6f + 1.8f * azar(semilla, k + 100)) * d * ondulacion
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..18) {
                    val a = largo * s / 18
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 18)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.25f + 0.75f * azar(semilla, k + 400))), style = Stroke(grosor * d * (0.5f + azar(semilla, k + 500))))
            }
        }

    /** Una tabla de madera en [r]: el color y su veta. */
    private fun DrawScope.madera(r: Rect, color: Color, oscuro: Boolean, semilla: Int, vertical: Boolean = false, veta: Color = KEYAKI_VETA, fuerza: Float = 0.5f) {
        drawRect(t(color, oscuro), r.topLeft, r.size)
        vetas(r, t(veta, oscuro).copy(alpha = fuerza), semilla, densidad = 0.8f, vertical = vertical, grosor = 0.7f, ondulacion = 0.7f)
    }

    /** Una pieza de laca en [r]: el color hondo, el brillo de arriba y un filo de luz. */
    private fun DrawScope.laca(r: Rect, oscuro: Boolean, color: Color = URUSHI, luz: Color = URUSHI_LUZ) {
        drawRect(t(color, oscuro), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(t(luz, oscuro).copy(alpha = 0.55f), Color.Transparent), r.top, r.top + r.height * 0.45f), r.topLeft, Size(r.width, r.height * 0.45f))
        drawLine(t(luz, oscuro), Offset(r.left, r.top + 0.3f * density), Offset(r.right, r.top + 0.3f * density), 0.6f * density)
    }

    /** Una pieza de oro (herraje, filete) en [r]: el oro, su luz arriba y su sombra abajo. */
    private fun DrawScope.oro(r: Rect, oscuro: Boolean, radio: Float = 0f) {
        drawRoundRect(t(ORO_OSC, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(t(ORO, oscuro), r.topLeft, Size(r.width, r.height * 0.78f), CornerRadius(radio))
        drawRoundRect(t(ORO_LUZ, oscuro), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.12f), Size(r.width * 0.88f, r.height * 0.18f), CornerRadius(radio))
    }

    /** Una sombra suave debajo de algo: varias capas cada vez más transparentes. */
    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(e))
        }
    }

    /** La sombra de dentro de un hueco: más honda en los bordes. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.6f), Color.Transparent), r.left, r.left + ancho * 0.7f), r.topLeft, Size(ancho * 0.7f, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.6f)), r.right - ancho * 0.7f, r.right), Offset(r.right - ancho * 0.7f, r.top), Size(ancho * 0.7f, r.height))
    }

    /** Un halo de luz (los farolillos encendidos). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Una flor de ciruelo: cinco pétalos redondos alrededor del centro. */
    private fun DrawScope.ume(c: Offset, r: Float, petalo: Color, centro: Color) {
        for (k in 0 until 5) {
            val a = Math.toRadians(-90.0 + k * 72.0)
            drawCircle(petalo, r * 0.42f, Offset(c.x + r * 0.55f * cos(a).toFloat(), c.y + r * 0.55f * sin(a).toFloat()))
        }
        drawCircle(centro, r * 0.26f, c)
    }

    /** El blasón de oro con la flor del ciruelo: el aro y la flor en relieve. */
    private fun DrawScope.blason(c: Offset, r: Float, oscuro: Boolean, fondo: Color = URUSHI) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 1.05f, c + Offset(0f, r * 0.12f))
        drawCircle(t(ORO_OSC, oscuro), r, c)
        drawCircle(t(fondo, oscuro), r * 0.84f, c)
        drawCircle(t(ORO, oscuro), r * 0.84f, c, style = Stroke(r * 0.08f))
        ume(c + Offset(r * 0.03f, r * 0.05f), r * 0.66f, t(ORO_OSC, oscuro), t(ORO_OSC, oscuro))
        ume(c, r * 0.64f, t(ORO, oscuro), t(ORO_LUZ, oscuro))
        drawArc(t(ORO_LUZ, oscuro), 200f, 80f, false, Offset(c.x - r * 0.92f, c.y - r * 0.92f), Size(r * 1.84f, r * 1.84f), style = Stroke(r * 0.1f))
    }

    /** El oleaje seigaiha calado en [r]: anillos de madera [linea] sobre el hueco oscuro [fondo]. */
    private fun DrawScope.seigaiha(r: Rect, radio: Float, fondo: Color, linea: Color, grosor: Float) = clipRect(r.left, r.top, r.right, r.bottom) {
        var fila = 0
        var y = r.top
        while (y < r.bottom + radio) {
            var x = r.left - (if (fila % 2 == 0) 0f else radio)
            while (x < r.right + radio) {
                drawCircle(fondo, radio, Offset(x, y))
                for (k in 0 until 3) drawCircle(linea, radio * (0.92f - k * 0.27f), Offset(x, y), style = Stroke(grosor))
                x += radio * 2
            }
            y += radio * 0.5f; fila++
        }
    }

    /** Una escuadra (herraje en ele) con su punta de helecho y sus remaches, en la esquina [o]; [sx], [sy] dicen hacia dónde abre. */
    private fun DrawScope.escuadra(o: Offset, sx: Float, sy: Float, l: Float, g: Float, color: Color, luz: Color) {
        drawRect(color, Offset(if (sx > 0) o.x else o.x - l, if (sy > 0) o.y else o.y - g), Size(l, g))
        drawRect(color, Offset(if (sx > 0) o.x else o.x - g, if (sy > 0) o.y else o.y - l), Size(g, l))
        drawCircle(color, g * 0.62f, Offset(o.x + sx * l, o.y + sy * g / 2))
        drawCircle(color, g * 0.62f, Offset(o.x + sx * g / 2, o.y + sy * l))
        listOf(Offset(o.x + sx * l * 0.6f, o.y + sy * g / 2), Offset(o.x + sx * g / 2, o.y + sy * l * 0.6f), Offset(o.x + sx * g / 2, o.y + sy * g / 2)).forEach {
            drawCircle(luz, g * 0.24f, it)
        }
    }

    /** Un cubreclavos de oro (kugikakushi) en forma de flor, en [c]. */
    private fun DrawScope.cubreclavos(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(0f, r * 0.25f))
        for (k in 0 until 6) {
            val a = k * Math.PI / 3
            drawCircle(t(ORO_OSC, oscuro), r * 0.45f, Offset(c.x + r * 0.55f * cos(a).toFloat(), c.y + r * 0.55f * sin(a).toFloat()))
            drawCircle(t(ORO, oscuro), r * 0.36f, Offset(c.x + r * 0.55f * cos(a).toFloat(), c.y + r * 0.55f * sin(a).toFloat()))
        }
        drawCircle(t(ORO_LUZ, oscuro), r * 0.4f, c)
        drawCircle(t(ORO_OSC, oscuro), r * 0.15f, c)
    }

    /** Un farolillo de papel (chōchin) colgado de [arriba], de alto [alto]: las tapas de laca, el papel con sus aros y el blasón. */
    private fun DrawScope.chochin(arriba: Offset, alto: Float, oscuro: Boolean) {
        val an = alto * 0.62f
        val cu = Rect(arriba.x - an / 2, arriba.y + alto * 0.16f, arriba.x + an / 2, arriba.y + alto * 0.9f)
        if (oscuro) {
            halo(cu.center, alto * 1.5f, LLAMA.copy(alpha = 0.22f))
            halo(cu.center, alto * 0.75f, LLAMA.copy(alpha = 0.3f))
        }
        drawLine(t(HIERRO, oscuro), arriba, Offset(arriba.x, cu.top), alto * 0.03f)
        val papel = if (oscuro) mezcla(SHU, LLAMA, 0.32f) else SHU
        drawOval(papel, cu.topLeft, cu.size)
        // Los aros de bambú: líneas curvas a lo ancho, más cortas hacia las tapas.
        for (k in 1 until 9) {
            val y = cu.top + cu.height * k / 9
            val dy = (y - cu.center.y) / (cu.height / 2)
            val hw = an / 2 * sqrt((1 - dy * dy).coerceAtLeast(0f))
            drawPath(Path().apply { moveTo(cu.center.x - hw, y); quadraticTo(cu.center.x, y + alto * 0.025f, cu.center.x + hw, y) },
                (if (oscuro) SHU_OSC else SHU_OSC).copy(alpha = 0.55f), style = Stroke(alto * 0.012f))
        }
        drawOval(Brush.horizontalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.25f else 0.18f), Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.1f else 0.3f)), cu.left, cu.right), cu.topLeft, cu.size)
        // El blasón del ciruelo en un círculo blanco.
        val bc = Offset(cu.center.x, cu.center.y + alto * 0.02f)
        drawCircle(if (oscuro) mezcla(WASHI, LLAMA, 0.2f) else WASHI, an * 0.24f, bc)
        ume(bc, an * 0.2f, SUMI, if (oscuro) mezcla(WASHI, LLAMA, 0.2f) else WASHI)
        drawCircle(SUMI, an * 0.05f, bc)
        // Las tapas de laca negra, arriba y abajo, con su filo de oro.
        listOf(cu.top - alto * 0.05f, cu.bottom - alto * 0.03f).forEach { y ->
            val tp = Rect(cu.center.x - an * 0.3f, y, cu.center.x + an * 0.3f, y + alto * 0.08f)
            drawRect(t(URUSHI, oscuro), tp.topLeft, tp.size)
            drawRect(t(ORO, oscuro), Offset(tp.left, if (y < cu.center.y) tp.bottom - alto * 0.012f else tp.top), Size(tp.width, alto * 0.012f))
        }
        drawLine(t(SHU_OSC, oscuro), Offset(cu.center.x, cu.bottom + alto * 0.05f), Offset(cu.center.x, cu.bottom + alto * 0.12f), alto * 0.025f)
    }

    /** El tatami en [r]: la estera con su trama y el ribete añil (heri) en [ribetes] (alturas absolutas). */
    private fun DrawScope.tatami(r: Rect, oscuro: Boolean, ribetes: List<Float>, perspectiva: Boolean) {
        val u = size.minDimension / 100f
        drawRect(Brush.verticalGradient(listOf(t(TATAMI_LUZ, oscuro), t(TATAMI, oscuro), t(TATAMI_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + 0.6f * u
            var k = 0
            while (y < r.bottom) {
                drawLine(t(TATAMI_OSC, oscuro).copy(alpha = 0.5f), Offset(r.left, y), Offset(r.right, y), 0.35f * u)
                y += (if (perspectiva) 0.9f + 0.9f * (y - r.top) / r.height else 1.1f) * u; k++
            }
            // Los ribetes verticales que se abren hacia el frente.
            if (perspectiva) listOf(0.18f, 0.5f, 0.82f).forEach { f ->
                val x0 = r.left + r.width * f
                val x1 = r.center.x + (x0 - r.center.x) * 1.18f
                drawPath(Path().apply { moveTo(x0 - 0.7f * u, r.top); lineTo(x0 + 0.7f * u, r.top); lineTo(x1 + 1.1f * u, r.bottom); lineTo(x1 - 1.1f * u, r.bottom); close() }, t(HERI, oscuro))
                drawLine(Color.White.copy(alpha = 0.16f), Offset(x0, r.top), Offset(x1, r.bottom), 0.3f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1.6f * u)))
            }
        }
        ribetes.forEach { y ->
            drawRect(t(HERI, oscuro), Offset(r.left, y), Size(r.width, 2.2f * u))
            var x = r.left + 1.5f * u
            while (x < r.right) { drawCircle(Color.White.copy(alpha = 0.18f), 0.35f * u, Offset(x, y + 1.1f * u)); x += 2.6f * u }
        }
    }

    // ---------------------------------------------------------------- Social: la sala de tatami

    /** Los shoji en [r]: la washi (de noche, alumbrada por detrás) con su cuadrícula de listones y el panel bajo de keyaki. */
    private fun DrawScope.shoji(r: Rect, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        val bajo = r.bottom - r.height * 0.17f
        drawRect(if (oscuro) mezcla(WASHI, NOCHE, 0.6f) else WASHI, r.topLeft, Size(r.width, bajo - r.top))
        if (oscuro) drawRect(Brush.verticalGradient(listOf(LLAMA.copy(alpha = 0.08f), LLAMA.copy(alpha = 0.18f)), r.top, bajo), r.topLeft, Size(r.width, bajo - r.top))
        val liston = t(HINOKI, oscuro)
        val cols = max(2, (r.width / (8 * u)).roundToInt())
        for (k in 0..cols) drawLine(liston, Offset(r.left + r.width * k / cols, r.top), Offset(r.left + r.width * k / cols, bajo), 0.7f * u)
        var y = r.top; while (y < bajo) { drawLine(liston, Offset(r.left, y), Offset(r.right, y), 0.7f * u); y += 6.5f * u }
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f)), r.left, r.right), r.topLeft, Size(r.width, bajo - r.top))
        madera(Rect(r.left, bajo, r.right, r.bottom), KEYAKI, oscuro, semilla, fuerza = 0.45f)
        drawRect(t(HINOKI_OSC, oscuro), Offset(r.left, bajo), Size(r.width, 0.8f * u))
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        val piso = h * 0.845f
        // El techo de tablas de cedro con sus listones.
        val techo = Rect(0f, 0f, w, h * 0.045f)
        madera(techo, SUGI, oscuro, e.semilla + 1)
        var x = 4 * u
        while (x < w) { drawLine(t(KEYAKI_OSC, oscuro), Offset(x, 0f), Offset(x, techo.bottom), 0.9f * u); x += 9 * u }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, techo.bottom - 0.8f * u), Size(w, 0.8f * u))
        // El ranma: el calado de olas entre los postes, con su marco y el blasón del ciruelo al centro.
        val ranma = Rect(0f, techo.bottom, w, h * 0.155f)
        madera(ranma, KEYAKI, oscuro, e.semilla + 2)
        val calado = Rect(ranma.left, ranma.top + 1.6f * u, ranma.right, ranma.bottom - 0.6f * u)
        seigaiha(calado, 3.4f * u, if (oscuro) Color(0xFF0A0807) else Color(0xFF2A1A10), t(KEYAKI_LUZ, oscuro), 0.6f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), calado.top, calado.top + 2 * u), calado.topLeft, Size(calado.width, 2 * u))
        drawRect(t(KEYAKI_LUZ, oscuro), ranma.topLeft, Size(w, 0.6f * u))
        drawRect(t(KEYAKI_OSC, oscuro), Offset(0f, calado.top - 0.5f * u), Size(w, 0.5f * u))
        listOf(w * 0.34f, w * 0.66f).forEachIndexed { i, cx ->
            madera(Rect(cx - 1.4f * u, calado.top, cx + 1.4f * u, ranma.bottom), KEYAKI, oscuro, e.semilla + 40 + i, vertical = true)
            drawRect(Color.White.copy(alpha = 0.12f), Offset(cx - 1.4f * u, calado.top), Size(0.5f * u, calado.height))
        }
        // Los postes de ciprés, con su luz y su sombra.
        val pw = w * 0.026f
        val postes = listOf(w * 0.084f, w * 0.916f - pw)
        // Los shoji de los lados y el farolillo colgado delante.
        shoji(Rect(0f, h * 0.195f, postes[0], piso), oscuro, e.semilla + 3)
        shoji(Rect(postes[1] + pw, h * 0.195f, w, piso), oscuro, e.semilla + 4)
        postes.forEachIndexed { i, px ->
            val p = Rect(px, techo.bottom, px + pw, piso)
            madera(p, HINOKI, oscuro, e.semilla + 10 + i, vertical = true, veta = HINOKI_OSC, fuerza = 0.6f)
            drawRect(Color.White.copy(alpha = 0.18f), p.topLeft, Size(pw * 0.25f, p.height))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(p.right - pw * 0.28f, p.top), Size(pw * 0.28f, p.height))
        }
        // El nageshi (la viga de ciprés que abraza los postes) con sus cubreclavos de oro.
        val nag = Rect(0f, h * 0.155f, w, h * 0.198f)
        madera(nag, HINOKI, oscuro, e.semilla + 5, veta = HINOKI_OSC, fuerza = 0.55f)
        drawRect(Color.White.copy(alpha = 0.2f), nag.topLeft, Size(w, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(0f, nag.bottom - 0.7f * u), Size(w, 0.7f * u))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), nag.bottom, nag.bottom + 2.5f * u), Offset(0f, nag.bottom), Size(w, 2.5f * u))
        postes.forEach { px -> cubreclavos(Offset(px + pw / 2, nag.center.y), 1.8f * u, oscuro) }
        postes.forEachIndexed { i, px ->
            val cx = if (i == 0) px * 0.5f else (px + pw + w) / 2
            chochin(Offset(cx, nag.bottom), 15 * u, oscuro)
        }
        // El tatami con sus ribetes y el umbral de ciprés.
        tatami(Rect(0f, piso, w, h), oscuro, emptyList(), perspectiva = true)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), piso, piso + 4 * u), Offset(0f, piso), Size(w, 4 * u))
        val umbral = Rect(0f, piso - 0.8f * u, w, piso + 1f * u)
        madera(umbral, HINOKI, oscuro, e.semilla + 6, veta = HINOKI_OSC)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, umbral.bottom - 0.4f * u), Size(w, 0.4f * u))
        // El biombo: la sombra, el marco de laca, los herrajes de oro de las esquinas y las bisagras de cada hoja.
        val g = 2.2f * u
        val marco = Rect(n.left - g, n.top - g, n.right + g, piso + 0.4f * u)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.28f)
        laca(marco, oscuro)
        drawRect(t(ORO, oscuro), Rect(n.left - g * 0.45f, n.top - g * 0.45f, n.right + g * 0.45f, n.bottom + g * 0.45f).topLeft,
            Size(n.width + g * 0.9f, n.height + g * 0.9f), style = Stroke(0.3f * u))
        val pie = Rect(n.left - g, n.bottom + g * 0.5f, n.right + g, piso + 0.4f * u)
        laca(pie, oscuro)
        drawRect(washiNicho(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            // Nubes de oro (kasumi) muy tenues en dos esquinas y los pliegues de las seis hojas.
            val nube = mezcla(washiNicho(oscuro), ORO, if (oscuro) 0.07f else 0.2f)
            val borde = mezcla(washiNicho(oscuro), ORO, if (oscuro) 0.32f else 0.4f)
            listOf(Triple(-0.04f, 0.05f, 0.3f), Triple(0.06f, 0.12f, 0.2f), Triple(0.74f, 0.85f, 0.32f), Triple(0.66f, 0.92f, 0.22f)).forEach { (fx, fy, fw) ->
                val tl = Offset(n.left + n.width * fx, n.top + n.height * fy)
                drawRoundRect(nube, tl, Size(n.width * fw, 3f * u), CornerRadius(1.5f * u))
                drawRoundRect(borde, tl, Size(n.width * fw, 3f * u), CornerRadius(1.5f * u), style = Stroke(0.3f * u))
            }
            for (k in 1 until 6) {
                val fx = n.left + n.width * k / 6
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.06f), Offset(fx, n.top), Offset(fx, n.bottom), 0.6f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.4f), Offset(fx + 0.6f * u, n.top), Offset(fx + 0.6f * u, n.bottom), 0.5f * u)
            }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.1f)
        for (k in 1 until 6) {
            val fx = n.left + n.width * k / 6
            listOf(n.top - g * 0.8f, n.bottom + g * 0.1f).forEach { y -> oro(Rect(fx - 1f * u, y, fx + 1f * u, y + g * 0.7f), oscuro) }
        }
        val l = 6 * u
        escuadra(Offset(marco.left, marco.top), 1f, 1f, l, 1.4f * u, t(ORO, oscuro), t(ORO_LUZ, oscuro))
        escuadra(Offset(marco.right, marco.top), -1f, 1f, l, 1.4f * u, t(ORO, oscuro), t(ORO_LUZ, oscuro))
        escuadra(Offset(marco.left, pie.bottom), 1f, -1f, l, 1.4f * u, t(ORO, oscuro), t(ORO_LUZ, oscuro))
        escuadra(Offset(marco.right, pie.bottom), -1f, -1f, l, 1.4f * u, t(ORO, oscuro), t(ORO_LUZ, oscuro))
        // El blasón del ciruelo en el ranma, sobre la hoja central.
        blason(Offset(w / 2, (calado.top + ranma.bottom) / 2), 4.6f * u, oscuro, fondo = KEYAKI_OSC)
        // En el tatami: dos cojines añiles y el brasero de keyaki con su tetera.
        listOf(w * 0.27f, w * 0.73f).forEach { cx -> zabuton(Offset(cx, h * 0.93f), w * 0.15f, h * 0.075f, oscuro, e) }
        brasero(Offset(w / 2, h * 0.99f), 14 * u, oscuro, e.semilla)
    }

    /** Un cojín (zabuton) añil en perspectiva, centrado en [c], con su trama de cruces y sus borlas en las esquinas. */
    private fun DrawScope.zabuton(c: Offset, an: Float, al: Float, oscuro: Boolean, e: EstadoMueble) {
        val u = size.minDimension / 100f
        val p = Path().apply {
            moveTo(c.x - an * 0.42f, c.y - al / 2); lineTo(c.x + an * 0.42f, c.y - al / 2)
            quadraticTo(c.x + an * 0.53f, c.y, c.x + an * 0.5f, c.y + al / 2); lineTo(c.x - an * 0.5f, c.y + al / 2)
            quadraticTo(c.x - an * 0.53f, c.y, c.x - an * 0.42f, c.y - al / 2); close()
        }
        translate(0.8f * u, 1.4f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
        drawPath(p, t(ANIL, oscuro))
        clipPath(p) {
            drawRect(Brush.verticalGradient(listOf(t(ANIL_LUZ, oscuro), Color.Transparent), c.y - al / 2, c.y), Offset(c.x - an, c.y - al / 2), Size(an * 2, al / 2))
            var yy = c.y - al / 2 + 1.6f * u
            var f = 0
            while (yy < c.y + al / 2) {
                var xx = c.x - an * 0.5f + (if (f % 2 == 0) 1.5f else 3.5f) * u
                while (xx < c.x + an * 0.5f) {
                    drawLine(Color.White.copy(alpha = 0.3f), Offset(xx - 0.5f * u, yy), Offset(xx + 0.5f * u, yy), 0.3f * u)
                    drawLine(Color.White.copy(alpha = 0.3f), Offset(xx, yy - 0.5f * u), Offset(xx, yy + 0.5f * u), 0.3f * u)
                    xx += 4 * u
                }
                yy += 2.2f * u; f++
            }
        }
        // La costura del centro y las cuatro borlas del color del esquema.
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x - an * 0.46f, c.y + al * 0.05f), Offset(c.x + an * 0.46f, c.y + al * 0.05f), 0.3f * u)
        listOf(Offset(c.x - an * 0.42f, c.y - al / 2), Offset(c.x + an * 0.42f, c.y - al / 2), Offset(c.x - an * 0.5f, c.y + al / 2), Offset(c.x + an * 0.5f, c.y + al / 2)).forEach { q ->
            drawCircle(e.secundario, 0.8f * u, q)
            drawCircle(Color.Black.copy(alpha = 0.25f), 0.8f * u, q, style = Stroke(0.2f * u))
        }
        drawCircle(e.secundario, 0.6f * u, Offset(c.x, c.y + al * 0.05f))
    }

    /** El brasero (hibachi) de keyaki con su forro de cobre, las brasas y la tetera de hierro; la base en [p]. */
    private fun DrawScope.brasero(p: Offset, s: Float, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        val caja = Rect(p.x - s * 0.5f, p.y - s * 0.58f, p.x + s * 0.5f, p.y - s * 0.02f)
        sombra(Rect(caja.left, caja.bottom - 1 * u, caja.right, caja.bottom), 1.6f * u, 0.35f)
        if (oscuro) halo(Offset(p.x, caja.top), s * 1.1f, BRASA.copy(alpha = 0.22f))
        madera(caja, KEYAKI, oscuro, semilla + 70, vertical = true)
        drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), caja.left, caja.right), caja.topLeft, caja.size)
        drawRect(t(COBRE_ROJO, oscuro), Offset(caja.left, caja.top), Size(caja.width, s * 0.07f))
        drawRect(t(ORO_LUZ, oscuro).copy(alpha = 0.5f), Offset(caja.left, caja.top), Size(caja.width, s * 0.02f))
        drawRect(t(KEYAKI_OSC, oscuro), caja.deflate(s * 0.08f).let { Rect(it.left, caja.top + s * 0.14f, it.right, caja.bottom - s * 0.06f) }.topLeft,
            Size(caja.width - s * 0.16f, caja.height - s * 0.2f), style = Stroke(0.35f * u))
        // Las brasas y, de noche, su resplandor.
        for (k in 0 until 4) drawCircle(if (oscuro) BRASA else Color(0xFF2A2420), s * 0.06f, Offset(caja.left + s * (0.25f + 0.17f * k), caja.top - s * 0.01f))
        // La tetera de hierro (tetsubin) sobre sus trébedes.
        val tc = Offset(p.x, caja.top - s * 0.26f)
        drawLine(t(HIERRO, oscuro), Offset(p.x - s * 0.26f, caja.top), Offset(p.x - s * 0.2f, tc.y + s * 0.1f), s * 0.03f)
        drawLine(t(HIERRO, oscuro), Offset(p.x + s * 0.26f, caja.top), Offset(p.x + s * 0.2f, tc.y + s * 0.1f), s * 0.03f)
        drawPath(Path().apply { moveTo(tc.x + s * 0.24f, tc.y); quadraticTo(tc.x + s * 0.42f, tc.y - s * 0.02f, tc.x + s * 0.44f, tc.y - s * 0.16f) }, t(HIERRO, oscuro), style = Stroke(s * 0.05f, cap = StrokeCap.Round))
        drawOval(t(HIERRO, oscuro), Offset(tc.x - s * 0.3f, tc.y - s * 0.17f), Size(s * 0.6f, s * 0.34f))
        drawOval(Color.White.copy(alpha = 0.16f), Offset(tc.x - s * 0.22f, tc.y - s * 0.13f), Size(s * 0.16f, s * 0.1f))
        for (k in 0 until 3) for (j in 0 until 6) drawCircle(t(HIERRO_LUZ, oscuro).copy(alpha = 0.5f), s * 0.014f, Offset(tc.x - s * 0.2f + j * s * 0.08f, tc.y - s * 0.06f + k * s * 0.06f))
        drawRect(t(HIERRO, oscuro), Offset(tc.x - s * 0.08f, tc.y - s * 0.22f), Size(s * 0.16f, s * 0.06f))
        drawArc(t(HIERRO_LUZ, oscuro), 180f, 180f, false, Offset(tc.x - s * 0.26f, tc.y - s * 0.46f), Size(s * 0.52f, s * 0.5f), style = Stroke(s * 0.035f))
    }

    // ---------------------------------------------------------------- Mapas: la mesa baja del agrimensor

    /** La piedra de tinta (suzuri) vista desde arriba en [r], con su mar y su llano, y la barra de sumi al lado. */
    private fun DrawScope.suzuri(r: Rect, oscuro: Boolean) {
        val u = size.minDimension / 100f
        val piedra = Rect(r.left, r.top, r.right - r.width * 0.3f, r.bottom)
        sombra(piedra, 1.6f * u, 0.35f)
        drawRect(t(Color(0xFF3E3C40), oscuro), piedra.topLeft, piedra.size)
        drawRect(Color.White.copy(alpha = 0.1f), piedra.topLeft, Size(piedra.width, 0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(piedra.right - 0.6f * u, piedra.top), Size(0.6f * u, piedra.height))
        for (k in 0 until 14) drawCircle(Color.White.copy(alpha = 0.08f), 0.25f * u, Offset(piedra.left + piedra.width * azar(5, k), piedra.top + piedra.height * azar(5, k + 20)))
        val dentro = piedra.deflate(piedra.width * 0.14f)
        drawRect(t(Color(0xFF2B2A2F), oscuro), dentro.topLeft, dentro.size)
        drawRect(Color.Black.copy(alpha = 0.35f), dentro.topLeft, dentro.size, style = Stroke(0.4f * u))
        // El mar (umi), donde se junta la tinta, arriba, con su brillo húmedo; y el llano (oka) donde se muele.
        val mar = Rect(dentro.left, dentro.top, dentro.right, dentro.top + dentro.height * 0.3f)
        drawPath(Path().apply { moveTo(mar.left, mar.bottom); lineTo(mar.left, mar.top + mar.width * 0.3f); quadraticTo(mar.left, mar.top, mar.center.x, mar.top); quadraticTo(mar.right, mar.top, mar.right, mar.top + mar.width * 0.3f); lineTo(mar.right, mar.bottom); quadraticTo(mar.center.x, mar.bottom - mar.height * 0.3f, mar.left, mar.bottom); close() }, t(Color(0xFF111014), oscuro))
        drawArc(Color.White.copy(alpha = 0.3f), 205f, 60f, false, Offset(mar.left + mar.width * 0.15f, mar.top + mar.height * 0.15f), Size(mar.width * 0.7f, mar.height * 1.2f), style = Stroke(0.4f * u))
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent), mar.bottom, dentro.bottom), Offset(dentro.left, mar.bottom), Size(dentro.width, dentro.bottom - mar.bottom))
        drawLine(Color.White.copy(alpha = 0.1f), Offset(dentro.left + dentro.width * 0.25f, dentro.top + dentro.height * 0.45f), Offset(dentro.left + dentro.width * 0.25f, dentro.bottom - dentro.height * 0.08f), dentro.width * 0.12f)
        // La barra de sumi, de pie a un lado, con su relieve de oro.
        val barra = Rect(r.right - r.width * 0.22f, r.top + r.height * 0.18f, r.right, r.bottom - r.height * 0.06f)
        drawRect(Color.Black.copy(alpha = 0.3f), barra.topLeft + Offset(0.5f * u, 0.7f * u), barra.size)
        drawRect(t(SUMI, oscuro), barra.topLeft, barra.size)
        drawRect(Color.White.copy(alpha = 0.14f), barra.topLeft, Size(barra.width * 0.3f, barra.height))
        drawRect(t(ORO, oscuro), Rect(barra.left + barra.width * 0.22f, barra.top + barra.height * 0.3f, barra.right - barra.width * 0.22f, barra.top + barra.height * 0.62f).topLeft,
            Size(barra.width * 0.56f, barra.height * 0.32f), style = Stroke(0.3f * u))
        ume(Offset(barra.center.x, barra.top + barra.height * 0.46f), barra.width * 0.3f, t(ORO, oscuro), t(SUMI, oscuro))
    }

    /** El pincel (fude) de bambú, de [a] (el pelo) a [b] (el remate). */
    private fun DrawScope.pincel(a: Offset, b: Offset, g: Float, oscuro: Boolean) {
        val u = size.minDimension / 100f
        drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0.6f * u, 0.9f * u), b + Offset(0.6f * u, 0.9f * u), g)
        val m = Offset(a.x + (b.x - a.x) * 0.22f, a.y + (b.y - a.y) * 0.22f)
        drawLine(t(Color(0xFFC7A76A), oscuro), m, b, g, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.25f), m, b, g * 0.3f)
        for (k in 1..3) {
            val f = 0.22f + k * 0.19f
            val q = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            drawCircle(t(Color(0xFF9A7E48), oscuro), g * 0.55f, q)
        }
        drawLine(t(SHU, oscuro), Offset(a.x + (b.x - a.x) * 0.95f, a.y + (b.y - a.y) * 0.95f), b, g * 1.1f, StrokeCap.Round)
        val p = Path().apply { moveTo(m.x - g * 0.6f, m.y); lineTo(a.x, a.y); lineTo(m.x + g * 0.6f, m.y); close() }
        drawPath(p, t(SUMI, oscuro))
        drawLine(t(Color(0xFF6A5A3A), oscuro), m, Offset(m.x + (a.x - m.x) * 0.3f, m.y + (a.y - m.y) * 0.3f), g * 0.9f)
    }

    /** La brújula de agrimensor (hōi): la caja redonda de keyaki, el aro de las doce horas, la aguja y su pivote. */
    private fun DrawScope.brujula(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        sombra(Rect(c.x - r, c.y - r, c.x + r, c.y + r), r * 0.15f, 0.35f)
        drawCircle(t(KEYAKI, oscuro), r, c)
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) { vetas(Rect(c.x - r, c.y - r, c.x + r, c.y + r), t(KEYAKI_VETA, oscuro).copy(alpha = 0.6f), semilla, grosor = 0.6f) }
        drawCircle(t(KEYAKI_LUZ, oscuro), r, c, style = Stroke(r * 0.06f))
        drawCircle(t(KIRI_LUZ, oscuro), r * 0.78f, c)
        drawCircle(t(SUMI, oscuro), r * 0.78f, c, style = Stroke(r * 0.03f))
        drawCircle(t(SUMI, oscuro), r * 0.56f, c, style = Stroke(r * 0.02f))
        for (k in 0 until 24) {
            val a = k * Math.PI / 12
            val r0 = if (k % 2 == 0) r * 0.56f else r * 0.66f
            drawLine(t(SUMI, oscuro), Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + r * 0.78f * cos(a).toFloat(), c.y + r * 0.78f * sin(a).toFloat()), r * 0.025f)
        }
        // La marca del norte (la rata), en bermellón.
        drawCircle(t(SHU, oscuro), r * 0.07f, Offset(c.x, c.y - r * 0.67f))
        rotate(18f, c) {
            drawPath(Path().apply { moveTo(c.x, c.y - r * 0.5f); lineTo(c.x + r * 0.08f, c.y); lineTo(c.x - r * 0.08f, c.y); close() }, t(SHU, oscuro))
            drawPath(Path().apply { moveTo(c.x, c.y + r * 0.5f); lineTo(c.x + r * 0.08f, c.y); lineTo(c.x - r * 0.08f, c.y); close() }, t(ANIL, oscuro))
        }
        drawCircle(t(ORO, oscuro), r * 0.08f, c)
        drawArc(Color.White.copy(alpha = 0.3f), 200f, 70f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(r * 0.06f))
    }

    /** El ábaco (soroban) visto desde arriba, de pie en [r]: el marco de keyaki, la barra y las cuentas a cada lado. */
    private fun DrawScope.abaco(r: Rect, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        sombra(r, 1.4f * u, 0.35f)
        madera(r, KEYAKI_OSC, oscuro, semilla, vertical = true)
        val dentro = r.deflate(r.width * 0.13f)
        drawRect(t(Color(0xFF3A2416), oscuro), dentro.topLeft, dentro.size)
        val barra = dentro.left + dentro.width * 0.32f
        val varillas = 9
        for (k in 0 until varillas) {
            val y = dentro.top + dentro.height * (k + 0.5f) / varillas
            drawLine(t(Color(0xFFB8A27A), oscuro), Offset(dentro.left, y), Offset(dentro.right, y), 0.3f * u)
            val bh = dentro.height / varillas * 0.82f
            val bw = dentro.width * 0.11f
            val cuenta = { x: Float ->
                drawPath(Path().apply { moveTo(x - bw / 2, y); lineTo(x, y - bh / 2); lineTo(x + bw / 2, y); lineTo(x, y + bh / 2); close() }, t(KEYAKI_LUZ, oscuro))
                drawLine(Color.White.copy(alpha = 0.25f), Offset(x - bw * 0.3f, y - bh * 0.15f), Offset(x, y - bh * 0.4f), 0.25f * u)
            }
            val arriba = (azar(semilla, k) > 0.6f)
            cuenta(if (arriba) barra - bw * 0.6f else dentro.left + bw * 0.6f)
            val sube = (azar(semilla, k + 30) * 5).toInt()
            for (j in 0 until 4) {
                val x = if (j < sube) barra + bw * (0.6f + j) else dentro.right - bw * (0.6f + (3 - j))
                cuenta(x)
            }
        }
        drawRect(t(KEYAKI_OSC, oscuro), Offset(barra - 0.6f * u, dentro.top), Size(1.2f * u, dentro.height))
        for (k in 0 until 3) drawCircle(t(ORO, oscuro), 0.35f * u, Offset(barra, dentro.top + dentro.height * (k * 3 + 1.5f) / varillas))
        drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, 0.6f * u))
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        // El tatami de fondo, con su ribete, y la sombra de la mesa.
        tatami(Rect(0f, 0f, w, h), oscuro, listOf(h * 0.935f), perspectiva = false)
        sombra(Rect(w * 0.05f, h * 0.95f, w * 0.95f, h * 0.985f), 3 * u, if (oscuro) 0.5f else 0.3f)
        // Las patas curvas (neko-ashi) de laca con su zapata de oro.
        val canto = h * 0.78f
        listOf(w * 0.035f to 1f, w * 0.965f to -1f).forEach { (x, s) ->
            val p = Path().apply {
                moveTo(x, h * 0.895f); lineTo(x + s * w * 0.06f, h * 0.895f)
                quadraticTo(x + s * w * 0.04f, h * 0.93f, x + s * w * 0.055f, h * 0.975f)
                lineTo(x + s * w * 0.015f, h * 0.975f)
                quadraticTo(x - s * w * 0.005f, h * 0.93f, x, h * 0.895f); close()
            }
            drawPath(p, t(URUSHI, oscuro))
            drawPath(p, t(URUSHI_LUZ, oscuro), style = Stroke(0.4f * u))
            oro(Rect(minOf(x + s * w * 0.01f, x + s * w * 0.06f), h * 0.968f, maxOf(x + s * w * 0.01f, x + s * w * 0.06f), h * 0.99f), oscuro, 0.4f * u)
        }
        // El faldón de laca con su fila de escamas de oro y los herrajes con anilla.
        val fal = Rect(w * 0.025f, h * 0.825f, w * 0.975f, h * 0.9f)
        laca(fal, oscuro)
        drawRect(Color.Black.copy(alpha = 0.35f), fal.topLeft, Size(fal.width, 1.2f * u))
        val esc = 3.2f * u
        var xe = fal.left + w * 0.09f
        while (xe < fal.right - w * 0.09f - esc) {
            drawPath(Path().apply { moveTo(xe, fal.bottom - 1.6f * u); lineTo(xe + esc / 2, fal.bottom - 1.6f * u - esc * 0.7f); lineTo(xe + esc, fal.bottom - 1.6f * u); close() }, t(ORO, oscuro).copy(alpha = 0.85f))
            drawCircle(t(ORO, oscuro), 0.35f * u, Offset(xe + esc / 2, fal.top + 2.4f * u))
            xe += esc * 1.4f
        }
        listOf(fal.left + w * 0.045f, fal.right - w * 0.045f).forEach { x ->
            val c = Offset(x, fal.center.y)
            drawRect(t(ORO_OSC, oscuro), Offset(c.x - 2.4f * u, c.y - 2.4f * u), Size(4.8f * u, 4.8f * u))
            oro(Rect(c.x - 2f * u, c.y - 2f * u, c.x + 2f * u, c.y + 2f * u), oscuro)
            drawCircle(t(ORO_OSC, oscuro), 2f * u, Offset(c.x, c.y + 1.2f * u), style = Stroke(0.5f * u))
        }
        blason(fal.center, 2.4f * u, oscuro)
        // El canto del tablero: la laca con su filete de oro y la greca de puntas.
        val ct = Rect(w * 0.01f, canto, w * 0.99f, h * 0.825f)
        laca(ct, oscuro)
        drawRect(t(ORO, oscuro), Offset(ct.left, ct.top + 0.6f * u), Size(ct.width, 0.35f * u))
        drawRect(t(ORO, oscuro), Offset(ct.left, ct.bottom - 1f * u), Size(ct.width, 0.35f * u))
        var xg = ct.left + 2 * u
        val gy = ct.center.y
        while (xg < ct.right - 2 * u) {
            drawPath(Path().apply { moveTo(xg, gy + 1.1f * u); lineTo(xg + 1.2f * u, gy - 0.9f * u); lineTo(xg + 2.4f * u, gy + 1.1f * u) }, t(ORO, oscuro), style = Stroke(0.35f * u))
            drawCircle(t(ORO, oscuro), 0.3f * u, Offset(xg + 3.4f * u, gy))
            xg += 4.4f * u
        }
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(ct.left, ct.bottom - 0.5f * u), Size(ct.width, 0.5f * u))
        // El tablero de laca negra visto desde arriba, con su brillo, el filete de maki-e y los ciruelos de las esquinas.
        val tab = Rect(w * 0.01f, h * 0.015f, w * 0.99f, canto)
        drawRect(t(URUSHI, oscuro), tab.topLeft, tab.size)
        drawRect(Brush.linearGradient(listOf(Color.Transparent, t(URUSHI_LUZ, oscuro).copy(alpha = 0.55f), Color.Transparent), Offset(tab.left, tab.bottom), Offset(tab.right, tab.top)), tab.topLeft, tab.size)
        val fil = tab.deflate(1.8f * u)
        drawRect(t(ORO, oscuro), fil.topLeft, fil.size, style = Stroke(0.45f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.6f), fil.deflate(1.2f * u).topLeft, fil.deflate(1.2f * u).size, style = Stroke(0.25f * u))
        listOf(fil.topLeft, Offset(fil.right, fil.top), Offset(fil.left, fil.bottom), fil.bottomRight).forEach { c ->
            val q = Offset(c.x + (if (c.x < w / 2) 3.6f else -3.6f) * u, c.y + (if (c.y < h / 2) 3.6f else -3.6f) * u)
            ume(q, 2.4f * u, t(ORO, oscuro), t(ORO_LUZ, oscuro))
            ume(Offset(q.x + (if (c.x < w / 2) 3.4f else -3.4f) * u, q.y + (if (c.y < h / 2) 1.2f else -1.2f) * u), 1.4f * u, t(ORO_OSC, oscuro), t(ORO, oscuro))
        }
        // El rollo desplegado: el brocado añil arriba y abajo, la washi del mapa y los dos rodillos con sus perillas.
        val brocado = Rect(n.left, n.top - 2.6f * u, n.right, n.bottom + 2.6f * u)
        sombra(brocado, 2.2f * u, if (oscuro) 0.5f else 0.35f)
        drawRect(t(ANIL, oscuro), brocado.topLeft, brocado.size)
        clipRect(brocado.left, brocado.top, brocado.right, brocado.bottom) {
            var xb = brocado.left; var k = 0
            while (xb < brocado.right) {
                listOf(brocado.top + 1.3f * u, brocado.bottom - 1.3f * u).forEach { y -> ume(Offset(xb, y), 1.1f * u, t(ORO, oscuro).copy(alpha = 0.7f), t(ANIL, oscuro)) }
                xb += 5 * u; k++
            }
        }
        drawRect(mapaNicho(oscuro), n.topLeft, n.size)
        drawRect(t(ORO_OSC, oscuro).copy(alpha = 0.6f), n.topLeft, n.size, style = Stroke(0.3f * u))
        sombraInterior(n, 1.6f * u, if (oscuro) 0.25f else 0.08f)
        listOf(n.left, n.right).forEach { x ->
            val rod = Rect(x - 1.8f * u, brocado.top - 1.8f * u, x + 1.8f * u, brocado.bottom + 1.8f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), rod.topLeft + Offset(0.6f * u, 0.8f * u), rod.size)
            drawRect(Brush.horizontalGradient(listOf(t(WASHI_OSC, oscuro), t(WASHI, oscuro), t(WASHI_OSC, oscuro), t(KIRI_VETA, oscuro)), rod.left, rod.right), rod.topLeft, rod.size)
            for (k in 1 until 4) drawLine(t(KIRI_VETA, oscuro).copy(alpha = 0.5f), Offset(rod.left + rod.width * k / 4, rod.top + 2 * u), Offset(rod.left + rod.width * k / 4, rod.bottom - 2 * u), 0.2f * u)
            listOf(rod.top - 2.2f * u, rod.bottom).forEach { y ->
                drawRoundRect(t(SHU_OSC, oscuro), Offset(rod.left - 0.4f * u, y), Size(rod.width + 0.8f * u, 2.2f * u), CornerRadius(0.8f * u))
                drawRoundRect(t(SHU_LUZ, oscuro), Offset(rod.left, y + 0.3f * u), Size(rod.width * 0.4f, 1.2f * u), CornerRadius(0.5f * u))
            }
        }
        // A la izquierda, el pincel y la piedra de tinta con su gotero; a la derecha, la brújula y el ábaco.
        val izq = (fil.left + brocado.left - 1.8f * u) / 2
        val der = (fil.right + brocado.right + 1.8f * u) / 2
        val ancho = (brocado.left - 1.8f * u - fil.left)
        pincel(Offset(izq - ancho * 0.22f, h * 0.36f), Offset(izq + ancho * 0.18f, h * 0.06f), 1.6f * u, oscuro)
        val sz = Rect(izq - ancho * 0.36f, h * 0.41f, izq + ancho * 0.36f, h * 0.7f)
        suzuri(sz, oscuro)
        val gt = Offset(izq + ancho * 0.28f, h * 0.36f)
        drawCircle(Color.Black.copy(alpha = 0.3f), 2.4f * u, gt + Offset(0.5f * u, 0.7f * u))
        drawCircle(t(Color(0xFF6B8CA8), oscuro), 2.4f * u, gt)
        drawCircle(t(Color(0xFFA9C2D6), oscuro), 2.4f * u, gt, style = Stroke(0.4f * u))
        drawCircle(t(SUMI, oscuro), 0.5f * u, gt)
        drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, gt - Offset(1f * u, 1f * u))
        brujula(Offset(der, h * 0.22f), minOf(ancho * 0.38f, 9 * u), oscuro, e.semilla + 50)
        abaco(Rect(der - ancho * 0.28f, h * 0.38f, der + ancho * 0.28f, h * 0.7f), oscuro, e.semilla + 60)
    }

    // ---------------------------------------------------------------- Otras vidas: el relicario de keyaki

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        // La sombra en el suelo y las patas de laca en escuadra.
        sombra(Rect(w * 0.03f, h * 0.955f, w * 0.97f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(w * 0.035f, w * 0.965f - w * 0.06f).forEach { x ->
            laca(Rect(x, h * 0.94f, x + w * 0.06f, h * 0.985f), oscuro)
            oro(Rect(x, h * 0.975f, x + w * 0.06f, h * 0.99f), oscuro)
        }
        // La base de keyaki con cinco cajoncitos y sus anillas de hierro.
        val base = Rect(w * 0.015f, h * 0.81f, w * 0.985f, h * 0.945f)
        madera(base, KEYAKI, oscuro, e.semilla + 1)
        drawRect(Color.White.copy(alpha = 0.16f), base.topLeft, Size(base.width, 0.8f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(base.left, base.bottom - 1f * u), Size(base.width, 1f * u))
        val paso = n.width / 5
        for (k in 0 until 5) {
            val cj = Rect(n.left + paso * k + 1.4f * u, base.top + 2.4f * u, n.left + paso * (k + 1) - 1.4f * u, base.bottom - 2.4f * u)
            drawRect(Color.Black.copy(alpha = 0.45f), cj.inflate(0.5f * u).topLeft, cj.inflate(0.5f * u).size)
            madera(cj, KEYAKI_LUZ, oscuro, e.semilla + 10 + k, fuerza = 0.4f)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(cj.left, cj.bottom - 0.6f * u), Size(cj.width, 0.6f * u))
            val ac = cj.center
            drawRect(t(HIERRO, oscuro), Offset(ac.x - 2.4f * u, ac.y - 1.6f * u), Size(4.8f * u, 2.4f * u))
            drawCircle(t(HIERRO_LUZ, oscuro), 0.4f * u, Offset(ac.x - 1.6f * u, ac.y - 0.4f * u))
            drawCircle(t(HIERRO_LUZ, oscuro), 0.4f * u, Offset(ac.x + 1.6f * u, ac.y - 0.4f * u))
            drawArc(t(HIERRO, oscuro), 0f, 180f, false, Offset(ac.x - 1.8f * u, ac.y - 1.8f * u), Size(3.6f * u, 3.6f * u), style = Stroke(0.6f * u))
        }
        // Las esquinas de la base, con su escuadra de hierro.
        escuadra(Offset(base.left, base.top), 1f, 1f, 5 * u, 1.3f * u, t(HIERRO, oscuro), t(HIERRO_LUZ, oscuro))
        escuadra(Offset(base.right, base.top), -1f, 1f, 5 * u, 1.3f * u, t(HIERRO, oscuro), t(HIERRO_LUZ, oscuro))
        escuadra(Offset(base.left, base.bottom), 1f, -1f, 5 * u, 1.3f * u, t(HIERRO, oscuro), t(HIERRO_LUZ, oscuro))
        escuadra(Offset(base.right, base.bottom), -1f, -1f, 5 * u, 1.3f * u, t(HIERRO, oscuro), t(HIERRO_LUZ, oscuro))
        // El cuerpo detrás del nicho, los postes de laca de los lados con sus herrajes.
        val cuerpo = Rect(w * 0.02f, h * 0.17f, w * 0.98f, base.top)
        madera(cuerpo, KEYAKI_OSC, oscuro, e.semilla + 2)
        val pw = w * 0.04f
        listOf(w * 0.02f, w * 0.98f - pw).forEach { x ->
            val p = Rect(x, h * 0.17f, x + pw, base.top)
            laca(p, oscuro)
            drawRect(t(URUSHI_LUZ, oscuro).copy(alpha = 0.7f), Offset(p.left + pw * 0.2f, p.top), Size(pw * 0.12f, p.height))
            listOf(p.top + 1.5f * u, p.center.y - 1.5f * u, p.bottom - 4.5f * u).forEach { y -> oro(Rect(p.left - 0.3f * u, y, p.right + 0.3f * u, y + 3f * u), oscuro) }
        }
        // El nicho: la paulonia, cinco compartimentos con su ventana de campana y su repisa, separados por postes.
        drawRect(t(KEYAKI_OSC, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(relicarioNicho(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            vetas(n, (if (oscuro) Color.White.copy(alpha = 0.03f) else KIRI_VETA.copy(alpha = 0.16f)), e.semilla + 3, densidad = 0.4f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        }
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k, n.top, n.left + paso * (k + 1), n.bottom)
            val r = c.width * 0.4f
            val tope = c.top + 2.6f * u
            // La ventana de campana (katōmado): el arco apuntado con sus volutas abajo.
            val ventana = Path().apply {
                moveTo(c.center.x - r, c.bottom - 3 * u)
                lineTo(c.center.x - r, tope + r * 1.1f)
                cubicTo(c.center.x - r, tope + r * 0.3f, c.center.x - r * 0.3f, tope + r * 0.35f, c.center.x, tope)
                cubicTo(c.center.x + r * 0.3f, tope + r * 0.35f, c.center.x + r, tope + r * 0.3f, c.center.x + r, tope + r * 1.1f)
                lineTo(c.center.x + r, c.bottom - 3 * u)
            }
            drawPath(ventana, if (oscuro) Color.Black.copy(alpha = 0.2f) else KIRI.copy(alpha = 0.35f))
            drawPath(ventana, t(KEYAKI, oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), style = Stroke(0.45f * u))
            listOf(-1f, 1f).forEach { s ->
                drawArc(t(KEYAKI, oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), if (s < 0) 270f else 90f, 180f, false,
                    Offset(c.center.x + s * r - 1.2f * u, tope + r * 1.1f - 2.4f * u), Size(2.4f * u, 2.4f * u), style = Stroke(0.4f * u))
            }
        }
        sombraInterior(n, 4 * u, if (oscuro) 0.35f else 0.12f)
        val rep = Rect(n.left, n.bottom - 2.4f * u, n.right, n.bottom)
        madera(rep, KIRI, oscuro, e.semilla + 4, veta = KIRI_VETA)
        drawRect(t(ORO, oscuro), rep.topLeft, Size(rep.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(rep.left, rep.bottom - 0.5f * u), Size(rep.width, 0.5f * u))
        for (k in 1 until 5) {
            val x = n.left + paso * k
            val p = Rect(x - 1.5f * u, n.top, x + 1.5f * u, n.bottom)
            madera(p, KEYAKI, oscuro, e.semilla + 20 + k, vertical = true)
            drawRect(Color.White.copy(alpha = 0.14f), p.topLeft, Size(p.width * 0.3f, p.height))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(p.right - p.width * 0.3f, p.top), Size(p.width * 0.3f, p.height))
            oro(Rect(p.left - 0.3f * u, p.top, p.right + 0.3f * u, p.top + 2.4f * u), oscuro)
            oro(Rect(p.left - 0.3f * u, p.bottom - 2.4f * u, p.right + 0.3f * u, p.bottom), oscuro)
        }
        // La viga de keyaki sobre los nichos, con sus cubreclavos de oro.
        val viga = Rect(w * 0.01f, h * 0.15f, w * 0.99f, h * 0.205f)
        madera(viga, KEYAKI, oscuro, e.semilla + 6)
        drawRect(Color.White.copy(alpha = 0.18f), viga.topLeft, Size(viga.width, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(viga.left, viga.bottom - 0.8f * u), Size(viga.width, 0.8f * u))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), viga.bottom, viga.bottom + 2.5f * u), Offset(viga.left, viga.bottom), Size(viga.width, 2.5f * u))
        for (k in 0..5) cubreclavos(Offset(n.left + paso * k, viga.center.y), 1.5f * u, oscuro)
        // Las borlas de seda que cuelgan de la viga sobre los postes.
        listOf(w * 0.02f + pw / 2, w * 0.98f - pw / 2).forEach { x ->
            val y0 = viga.bottom
            drawLine(t(SHU_OSC, oscuro), Offset(x, y0), Offset(x, y0 + 7 * u), 0.5f * u)
            drawCircle(e.acento, 1.1f * u, Offset(x, y0 + 7 * u))
            val b = Path().apply { moveTo(x - 0.8f * u, y0 + 8 * u); lineTo(x + 0.8f * u, y0 + 8 * u); lineTo(x + 1.8f * u, y0 + 16 * u); lineTo(x - 1.8f * u, y0 + 16 * u); close() }
            drawPath(b, t(SHU, oscuro))
            for (j in -2..2) drawLine(t(SHU_OSC, oscuro), Offset(x + j * 0.35f * u, y0 + 9 * u), Offset(x + j * 0.7f * u, y0 + 16 * u), 0.25f * u)
            drawLine(t(ORO, oscuro), Offset(x - 0.9f * u, y0 + 8.6f * u), Offset(x + 0.9f * u, y0 + 8.6f * u), 0.5f * u)
        }
        // El tejado de tejas grises: los faldones con sus canales, las tejas de borde y el caballete con sus remates.
        val alero = h * 0.15f
        val tejado = Path().apply {
            moveTo(0f, alero - 3.2f * u)
            quadraticTo(w * 0.1f, alero, w * 0.25f, alero); lineTo(w * 0.75f, alero)
            quadraticTo(w * 0.9f, alero, w, alero - 3.2f * u)
            lineTo(w * 0.93f, h * 0.045f); lineTo(w * 0.07f, h * 0.045f); close()
        }
        drawPath(tejado, t(TEJA, oscuro))
        clipPath(tejado) {
            var xt = 1.5f * u
            while (xt < w) {
                drawLine(t(TEJA_OSC, oscuro), Offset(w / 2 + (xt - w / 2) * 0.93f, h * 0.045f), Offset(xt, alero), 1.1f * u)
                drawLine(t(TEJA_LUZ, oscuro), Offset(w / 2 + (xt - w / 2) * 0.93f - 0.7f * u, h * 0.045f), Offset(xt - 0.7f * u, alero), 0.4f * u)
                xt += 2.8f * u
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), h * 0.045f, h * 0.09f), Offset(0f, h * 0.045f), Size(w, h * 0.045f))
        }
        // Las tejas redondas del borde, una tras otra a lo largo del alero.
        var xa = w * 0.03f
        while (xa < w * 0.97f) {
            val fy = if (xa < w * 0.25f) alero - 3.2f * u * ((w * 0.25f - xa) / (w * 0.25f)).let { it * it } else if (xa > w * 0.75f) alero - 3.2f * u * ((xa - w * 0.75f) / (w * 0.25f)).let { it * it } else alero
            drawCircle(t(TEJA_OSC, oscuro), 1.1f * u, Offset(xa, fy))
            drawCircle(t(TEJA_LUZ, oscuro), 0.7f * u, Offset(xa - 0.15f * u, fy - 0.15f * u))
            xa += 2.8f * u
        }
        // El caballete con sus tejas de remate (onigawara).
        val cab = Rect(w * 0.06f, h * 0.02f, w * 0.94f, h * 0.05f)
        drawRect(t(TEJA_OSC, oscuro), cab.topLeft, cab.size)
        drawRect(t(TEJA_LUZ, oscuro), cab.topLeft, Size(cab.width, 0.6f * u))
        listOf(cab.left - 1 * u, cab.right - 4 * u).forEach { x ->
            val oni = Path().apply { moveTo(x, cab.bottom); lineTo(x, cab.top - 0.6f * u); quadraticTo(x + 2.5f * u, cab.top - 3.4f * u, x + 5 * u, cab.top - 0.6f * u); lineTo(x + 5 * u, cab.bottom); close() }
            drawPath(oni, t(TEJA, oscuro))
            drawPath(oni, t(TEJA_LUZ, oscuro), style = Stroke(0.4f * u))
            drawCircle(t(TEJA_OSC, oscuro), 0.8f * u, Offset(x + 2.5f * u, cab.center.y - 0.3f * u))
        }
        // El hastial del centro (chidori-hafu) con su tablero de keyaki, su colgante de oro y el blasón.
        val hc = w / 2
        val hast = Path().apply { moveTo(hc - 15 * u, alero - 0.2f * u); lineTo(hc, h * 0.0f + 0.4f * u); lineTo(hc + 15 * u, alero - 0.2f * u); close() }
        drawPath(hast, t(KEYAKI, oscuro))
        clipPath(hast) { vetas(Rect(hc - 15 * u, 0f, hc + 15 * u, alero), t(KEYAKI_VETA, oscuro).copy(alpha = 0.5f), e.semilla + 7, grosor = 0.6f, vertical = true) }
        drawPath(Path().apply { moveTo(hc - 16.5f * u, alero + 0.4f * u); lineTo(hc, h * 0.0f - 0.6f * u); lineTo(hc + 16.5f * u, alero + 0.4f * u) }, t(URUSHI, oscuro), style = Stroke(1.6f * u))
        drawPath(Path().apply { moveTo(hc - 16.5f * u, alero + 0.4f * u); lineTo(hc, h * 0.0f - 0.6f * u); lineTo(hc + 16.5f * u, alero + 0.4f * u) }, t(ORO, oscuro), style = Stroke(0.4f * u))
        blason(Offset(hc, alero - 5.6f * u), 4.2f * u, oscuro)
        // El colgante de oro (gegyo) en la punta del hastial.
        drawPath(Path().apply { moveTo(hc - 2.4f * u, 1.4f * u); lineTo(hc + 2.4f * u, 1.4f * u); lineTo(hc, 4.4f * u); close() }, t(ORO, oscuro))
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f * 60f
        val c = Offset(w / 2, h / 2)
        val u = s / 60f
        when (id) {
            "unesco" -> {
                // La pagoda de cinco pisos sobre su basamento de piedra, entre un pino y una linterna de piedra.
                val base = c.y + s * 0.49f
                drawOval(Color.Black.copy(alpha = 0.22f), Offset(c.x - 24 * u, base - 1.6f * u), Size(48 * u, 3.2f * u))
                val zocalo = Rect(c.x - 12 * u, base - 3 * u, c.x + 12 * u, base)
                drawRect(t(PIEDRA, oscuro), zocalo.topLeft, zocalo.size)
                drawRect(t(PIEDRA_LUZ, oscuro), zocalo.topLeft, Size(zocalo.width, 0.8f * u))
                drawRect(t(PIEDRA_OSC, oscuro), Offset(zocalo.left, zocalo.bottom - 0.6f * u), Size(zocalo.width, 0.6f * u))
                var y = zocalo.top
                for (k in 0 until 5) {
                    val bw = (11.5f - k * 1.3f) * u
                    val cuerpo = Rect(c.x - bw / 2, y - 4f * u, c.x + bw / 2, y)
                    drawRect(t(SHU, oscuro), cuerpo.topLeft, cuerpo.size)
                    drawRect(t(WASHI, oscuro), Offset(cuerpo.left + bw * 0.12f, cuerpo.top + 1.2f * u), Size(bw * 0.18f, 2.2f * u))
                    drawRect(t(WASHI, oscuro), Offset(cuerpo.right - bw * 0.3f, cuerpo.top + 1.2f * u), Size(bw * 0.18f, 2.2f * u))
                    drawRect(t(SUMI, oscuro), Offset(c.x - bw * 0.1f, cuerpo.top + 1f * u), Size(bw * 0.2f, 3f * u))
                    drawRect(Color.Black.copy(alpha = 0.25f), Offset(cuerpo.right - bw * 0.18f, cuerpo.top), Size(bw * 0.18f, cuerpo.height))
                    // El tejado con sus puntas levantadas.
                    val rw = (27f - k * 3.2f) * u
                    val ey = cuerpo.top
                    val techo = Path().apply {
                        moveTo(c.x - rw / 2, ey - 2.4f * u)
                        quadraticTo(c.x - rw * 0.3f, ey + 0.4f * u, c.x, ey + 0.4f * u)
                        quadraticTo(c.x + rw * 0.3f, ey + 0.4f * u, c.x + rw / 2, ey - 2.4f * u)
                        quadraticTo(c.x + rw * 0.3f, ey - 2.2f * u, c.x + bw * 0.42f, ey - 4.2f * u)
                        lineTo(c.x - bw * 0.42f, ey - 4.2f * u)
                        quadraticTo(c.x - rw * 0.3f, ey - 2.2f * u, c.x - rw / 2, ey - 2.4f * u); close()
                    }
                    drawPath(techo, t(TEJA, oscuro))
                    drawPath(Path().apply { moveTo(c.x - rw / 2, ey - 2.4f * u); quadraticTo(c.x - rw * 0.3f, ey + 0.4f * u, c.x, ey + 0.4f * u); quadraticTo(c.x + rw * 0.3f, ey + 0.4f * u, c.x + rw / 2, ey - 2.4f * u) },
                        t(TEJA_LUZ, oscuro), style = Stroke(0.6f * u))
                    drawPath(Path().apply { moveTo(c.x, ey - 4.2f * u); lineTo(c.x + bw * 0.42f, ey - 4.2f * u); quadraticTo(c.x + rw * 0.3f, ey - 2.2f * u, c.x + rw / 2, ey - 2.4f * u); lineTo(c.x, ey) ; close() }, Color.Black.copy(alpha = 0.2f))
                    y = ey - 4.2f * u
                }
                // El remate (sōrin): la base, los nueve anillos de bronce y la joya.
                drawRect(t(TEJA_OSC, oscuro), Offset(c.x - 1.6f * u, y - 1.4f * u), Size(3.2f * u, 1.4f * u))
                drawLine(t(ORO_OSC, oscuro), Offset(c.x, y - 1.4f * u), Offset(c.x, y - 12 * u), 0.7f * u)
                for (k in 0 until 7) drawLine(t(ORO, oscuro), Offset(c.x - 1.3f * u, y - 2.6f * u - k * 1.2f * u), Offset(c.x + 1.3f * u, y - 2.6f * u - k * 1.2f * u), 0.5f * u)
                drawCircle(t(ORO_LUZ, oscuro), 1f * u, Offset(c.x, y - 12.6f * u))
                // El pino, a la izquierda.
                val px = c.x - 21 * u
                drawLine(t(KEYAKI_OSC, oscuro), Offset(px, base - 0.5f * u), Offset(px + 1.5f * u, base - 16 * u), 1.4f * u)
                listOf(Triple(-1.5f, -10f, 7f), Triple(2.5f, -15f, 6f), Triple(-0.5f, -19.5f, 4.5f)).forEach { (dx, dy, r) ->
                    drawOval(t(PINO, oscuro), Offset(px + dx * u - r * u, base + dy * u - r * 0.42f * u), Size(r * 2 * u, r * 0.84f * u))
                    drawOval(t(PINO_LUZ, oscuro), Offset(px + dx * u - r * 0.8f * u, base + dy * u - r * 0.42f * u), Size(r * 1.4f * u, r * 0.4f * u))
                }
                // La linterna de piedra (tōrō), a la derecha.
                val lx = c.x + 20 * u
                drawRect(t(PIEDRA_OSC, oscuro), Offset(lx - 3 * u, base - 1.6f * u), Size(6 * u, 1.6f * u))
                drawRect(t(PIEDRA, oscuro), Offset(lx - 1 * u, base - 8 * u), Size(2 * u, 6.4f * u))
                drawRect(t(PIEDRA, oscuro), Offset(lx - 2.6f * u, base - 9.4f * u), Size(5.2f * u, 1.4f * u))
                drawRect(t(PIEDRA_LUZ, oscuro), Offset(lx - 2 * u, base - 13 * u), Size(4 * u, 3.6f * u))
                drawRect(if (oscuro) LLAMA else t(SUMI, oscuro), Offset(lx - 0.9f * u, base - 12.2f * u), Size(1.8f * u, 2f * u))
                if (oscuro) halo(Offset(lx, base - 11.2f * u), 5 * u, LLAMA.copy(alpha = 0.35f))
                drawPath(Path().apply { moveTo(lx - 4.4f * u, base - 13 * u); quadraticTo(lx, base - 14 * u, lx, base - 16.4f * u); quadraticTo(lx, base - 14 * u, lx + 4.4f * u, base - 13 * u); close() }, t(PIEDRA, oscuro))
                drawCircle(t(PIEDRA_LUZ, oscuro), 0.8f * u, Offset(lx, base - 16.6f * u))
            }
            "rascar" -> {
                // El mapa de washi con su ribete añil, la capa por rascar y lo ya raspado; el koban de oro que la raspa.
                val m = Rect(c.x - s * 0.62f, c.y - s * 0.4f, c.x + s * 0.5f, c.y + s * 0.4f)
                drawRect(Color.Black.copy(alpha = 0.25f), m.topLeft + Offset(1.2f * u, 1.6f * u), m.size)
                drawRect(t(ANIL, oscuro), m.topLeft, m.size)
                val papel = m.deflate(2f * u)
                drawRect(t(WASHI, oscuro), papel.topLeft, papel.size)
                clipRect(m.left, m.top, m.right, m.bottom) {
                    var xb = m.left + 2 * u
                    while (xb < m.right) { ume(Offset(xb, m.top + 1f * u), 0.8f * u, t(ORO, oscuro), t(ANIL, oscuro)); ume(Offset(xb, m.bottom - 1f * u), 0.8f * u, t(ORO, oscuro), t(ANIL, oscuro)); xb += 4.5f * u }
                }
                val capa = papel.deflate(2.2f * u)
                drawRect(t(Color(0xFFB8B2A2), oscuro), capa.topLeft, capa.size)
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    seigaiha(capa, 3 * u, t(Color(0xFFB8B2A2), oscuro), Color.White.copy(alpha = 0.35f), 0.35f * u)
                    val raspa = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.35f); cubicTo(capa.left + capa.width * 0.3f, capa.top - capa.height * 0.1f, capa.left + capa.width * 0.5f, capa.bottom, capa.right, capa.top + capa.height * 0.55f) }
                    drawPath(raspa, e.fondo, style = Stroke(capa.height * 0.42f, cap = StrokeCap.Round))
                    drawPath(raspa, e.c1.copy(alpha = 0.85f), style = Stroke(capa.height * 0.24f, cap = StrokeCap.Round))
                }
                // El koban: el óvalo de oro con su trama de esteras y sus dos sellos.
                val kc = Offset(m.right + s * 0.02f, m.bottom - s * 0.14f)
                val ka = s * 0.2f; val kb = s * 0.3f
                rotate(-18f, kc) {
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(kc.x - ka + 1.2f * u, kc.y - kb + 1.6f * u), Size(ka * 2, kb * 2))
                    drawOval(t(ORO_OSC, oscuro), Offset(kc.x - ka, kc.y - kb), Size(ka * 2, kb * 2))
                    drawOval(t(ORO, oscuro), Offset(kc.x - ka * 0.88f, kc.y - kb * 0.9f), Size(ka * 1.76f, kb * 1.8f))
                    clipPath(Path().apply { addOval(Rect(kc.x - ka * 0.88f, kc.y - kb * 0.9f, kc.x + ka * 0.88f, kc.y + kb * 0.9f)) }) {
                        var yk = kc.y - kb; while (yk < kc.y + kb) { drawLine(t(ORO_OSC, oscuro).copy(alpha = 0.5f), Offset(kc.x - ka, yk), Offset(kc.x + ka, yk), 0.3f * u); yk += 1.3f * u }
                    }
                    listOf(-0.5f, 0.5f).forEach { f -> drawRect(t(ORO_OSC, oscuro), Offset(kc.x - 1.6f * u, kc.y + kb * f - 1.4f * u), Size(3.2f * u, 2.8f * u)); ume(Offset(kc.x, kc.y + kb * f), 1.2f * u, t(ORO_LUZ, oscuro), t(ORO_OSC, oscuro)) }
                    drawArc(t(ORO_LUZ, oscuro), 200f, 90f, false, Offset(kc.x - ka * 0.75f, kc.y - kb * 0.78f), Size(ka * 1.5f, kb * 1.56f), style = Stroke(0.7f * u))
                }
            }
            else -> {
                // Tus lugares: tres torii bermellón en el mapa, unidos por el camino de posta.
                val m = Rect(c.x - s * 0.95f, c.y + s * 0.08f, c.x + s * 0.95f, c.y + s * 0.46f)
                val mp = Path().apply { moveTo(m.left + 4 * u, m.top); lineTo(m.right, m.top + 1 * u); lineTo(m.right - 4 * u, m.bottom); lineTo(m.left, m.bottom - 1 * u); close() }
                drawPath(mp, Color.Black.copy(alpha = 0.22f), style = Stroke(2 * u))
                drawPath(mp, t(WASHI, oscuro))
                clipPath(mp) {
                    drawPath(Path().apply { moveTo(m.left, m.bottom - 2 * u); quadraticTo(m.center.x, m.bottom - 6 * u, m.right, m.bottom - 3 * u); lineTo(m.right, m.bottom); lineTo(m.left, m.bottom); close() }, t(ANIL, oscuro).copy(alpha = 0.25f))
                    for (k in 0 until 3) drawPath(Path().apply { moveTo(m.left, m.bottom - (1 + k) * 1.2f * u); quadraticTo(m.center.x, m.bottom - (5 + k) * 1.2f * u, m.right, m.bottom - (2 + k) * 1.2f * u) }, t(ANIL, oscuro).copy(alpha = 0.3f), style = Stroke(0.3f * u))
                }
                drawPath(mp, t(WASHI_OSC, oscuro), style = Stroke(0.6f * u))
                val pts = listOf(Offset(c.x - s * 0.62f, m.top + m.height * 0.55f), Offset(c.x, m.top + m.height * 0.32f), Offset(c.x + s * 0.62f, m.top + m.height * 0.58f))
                val ruta = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.3f, m.bottom, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.3f, m.top, pts[2].x, pts[2].y) }
                drawPath(ruta, e.c2, style = Stroke(1.2f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
                pts.forEachIndexed { i, p ->
                    val al = s * (if (i == 1) 0.6f else 0.5f)
                    val an = al * 0.8f
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - an * 0.6f, p.y - 0.8f * u), Size(an * 1.2f, 1.6f * u))
                    // Los dos pilares, el travesaño (nuki), el dintel (shimaki) y el kasagi negro de puntas curvas.
                    listOf(-1f, 1f).forEach { sg ->
                        val x = p.x + sg * an * 0.32f
                        drawRect(t(SHU, oscuro), Offset(x - 0.9f * u, p.y - al * 0.8f), Size(1.8f * u, al * 0.8f))
                        drawRect(Color.Black.copy(alpha = 0.2f), Offset(x + 0.2f * u, p.y - al * 0.8f), Size(0.7f * u, al * 0.8f))
                        drawRect(t(URUSHI, oscuro), Offset(x - 1.2f * u, p.y - 1.4f * u), Size(2.4f * u, 1.4f * u))
                    }
                    drawRect(t(SHU, oscuro), Offset(p.x - an * 0.45f, p.y - al * 0.62f), Size(an * 0.9f, 1.4f * u))
                    drawRect(t(SHU, oscuro), Offset(p.x - an * 0.46f, p.y - al * 0.84f), Size(an * 0.92f, 1.6f * u))
                    val ky = p.y - al * 0.88f
                    drawPath(Path().apply { moveTo(p.x - an * 0.62f, ky - 1.6f * u); quadraticTo(p.x, ky + 0.4f * u, p.x + an * 0.62f, ky - 1.6f * u); lineTo(p.x + an * 0.58f, ky - 0.2f * u); quadraticTo(p.x, ky + 1.8f * u, p.x - an * 0.58f, ky - 0.2f * u); close() }, t(URUSHI, oscuro))
                    drawRect(t(SHU, oscuro), Offset(p.x - 0.8f * u, p.y - al * 0.84f + 1.6f * u), Size(1.6f * u, al * 0.22f - 1.6f * u))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el colgadero de ema del santuario

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        // El muro de fondo y la grava rastrillada del recinto.
        drawRect(t(mezcla(HINOKI, WASHI, 0.35f), oscuro))
        val suelo = h * 0.935f
        drawRect(t(Color(0xFFD8D3C6), oscuro), Offset(0f, suelo), Size(w, h - suelo))
        clipRect(0f, suelo, w, h) {
            var yg = suelo + 1.2f * u
            while (yg < h) { drawLine(t(PIEDRA, oscuro).copy(alpha = 0.6f), Offset(0f, yg), Offset(w, yg), 0.35f * u); yg += 1.6f * u }
            for (k in 0 until 40) drawCircle(t(PIEDRA_OSC, oscuro).copy(alpha = 0.4f), 0.3f * u, Offset(w * azar(e.semilla, k), suelo + (h - suelo) * azar(e.semilla, k + 50)))
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), suelo, suelo + 2 * u), Offset(0f, suelo), Size(w, 2 * u))
        // El tablero: el marco de keyaki, el tablado de ciprés y los dos varales de donde cuelgan los ema.
        val g = 2.4f * u
        val marco = Rect(n.left - g, n.top - g, n.right + g, n.bottom + g)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.25f)
        madera(marco, KEYAKI, oscuro, e.semilla + 1)
        drawRect(Color.White.copy(alpha = 0.16f), marco.topLeft, Size(marco.width, 0.7f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left, marco.bottom - 0.7f * u), Size(marco.width, 0.7f * u))
        drawRect(tabladoNicho(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            vetas(n, if (oscuro) Color.White.copy(alpha = 0.03f) else HINOKI_OSC.copy(alpha = 0.16f), e.semilla + 2, densidad = 0.35f, vertical = true, grosor = 0.5f, ondulacion = 0.5f)
            val tablas = max(5, (n.width / (13 * u)).roundToInt())
            for (k in 1 until tablas) {
                val x = n.left + n.width * k / tablas
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.07f), Offset(x, n.top), Offset(x, n.bottom), 0.5f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.025f else 0.35f), Offset(x + 0.5f * u, n.top), Offset(x + 0.5f * u, n.bottom), 0.4f * u)
            }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.1f)
        val hueco = n.width * 0.03f
        val ch = (n.height - hueco) / 2f
        listOf(n.top + 0.4f * u, n.top + ch + hueco + 0.4f * u).forEach { y ->
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(n.left, y + 0.9f * u), Size(n.width, 1.4f * u))
            drawRect(t(SHU, oscuro), Offset(n.left - g, y - 0.9f * u), Size(n.width + 2 * g, 1.8f * u))
            drawRect(t(SHU_LUZ, oscuro), Offset(n.left - g, y - 0.9f * u), Size(n.width + 2 * g, 0.5f * u))
        }
        // Los postes bermellón, con su vaina de laca negra abajo y sus anillos de oro.
        val pw = w * 0.04f
        listOf(w * 0.012f, w * 0.988f - pw).forEach { x ->
            val p = Rect(x, h * 0.1f, x + pw, suelo + 0.8f * u)
            drawRect(t(SHU, oscuro), p.topLeft, p.size)
            drawRect(t(SHU_LUZ, oscuro).copy(alpha = 0.7f), Offset(p.left + pw * 0.15f, p.top), Size(pw * 0.2f, p.height))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(p.right - pw * 0.3f, p.top), Size(pw * 0.3f, p.height))
            laca(Rect(p.left - 0.4f * u, p.bottom - 6 * u, p.right + 0.4f * u, p.bottom), oscuro)
            oro(Rect(p.left - 0.4f * u, p.bottom - 6.8f * u, p.right + 0.4f * u, p.bottom - 6 * u), oscuro)
            listOf(p.top + 3 * u, h * 0.5f).forEach { y -> oro(Rect(p.left - 0.3f * u, y, p.right + 0.3f * u, y + 1.4f * u), oscuro) }
        }
        // El travesaño bajo (nuki) de bermellón, bajo el tablero.
        val nuki = Rect(0f, marco.bottom + 0.6f * u, w, marco.bottom + 3 * u)
        drawRect(t(SHU, oscuro), nuki.topLeft, nuki.size)
        drawRect(t(SHU_LUZ, oscuro), nuki.topLeft, Size(w, 0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, nuki.bottom - 0.5f * u), Size(w, 0.5f * u))
        // La viga de arriba (bermellón) y el tejadillo de corteza con su caballete de cobre verde.
        val viga = Rect(0f, h * 0.095f, w, h * 0.13f)
        drawRect(t(SHU, oscuro), viga.topLeft, viga.size)
        drawRect(t(SHU_LUZ, oscuro), viga.topLeft, Size(w, 0.6f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, viga.bottom - 0.6f * u), Size(w, 0.6f * u))
        listOf(w * 0.012f + pw / 2, w * 0.988f - pw / 2).forEach { x -> oro(Rect(x - 2.2f * u, viga.top + 0.6f * u, x + 2.2f * u, viga.bottom - 0.6f * u), oscuro) }
        val techo = Path().apply {
            moveTo(-1 * u, h * 0.1f); quadraticTo(w * 0.2f, h * 0.085f, w * 0.3f, h * 0.02f); lineTo(w * 0.7f, h * 0.02f)
            quadraticTo(w * 0.8f, h * 0.085f, w + 1 * u, h * 0.1f); close()
        }
        drawPath(techo, t(CORTEZA, oscuro))
        clipPath(techo) {
            var yc = h * 0.025f
            while (yc < h * 0.1f) { drawLine(t(CORTEZA_OSC, oscuro), Offset(0f, yc), Offset(w, yc), 0.5f * u); drawLine(Color.White.copy(alpha = 0.08f), Offset(0f, yc + 0.5f * u), Offset(w, yc + 0.5f * u), 0.3f * u); yc += 1.6f * u }
        }
        drawPath(Path().apply { moveTo(-1 * u, h * 0.1f); quadraticTo(w * 0.2f, h * 0.085f, w * 0.3f, h * 0.02f) ; lineTo(w * 0.7f, h * 0.02f); quadraticTo(w * 0.8f, h * 0.085f, w + 1 * u, h * 0.1f) },
            t(CORTEZA_OSC, oscuro), style = Stroke(1.2f * u))
        val cab = Rect(w * 0.28f, h * 0.0f, w * 0.72f, h * 0.028f)
        drawRect(t(COBRE, oscuro), cab.topLeft, cab.size)
        drawRect(t(COBRE_LUZ, oscuro), cab.topLeft, Size(cab.width, 0.7f * u))
        listOf(cab.left - 1.6f * u, cab.right - 1.6f * u).forEach { x -> oro(Rect(x, cab.top, x + 3.2f * u, cab.bottom + 0.5f * u), oscuro) }
        for (k in 1..5) drawCircle(t(ORO, oscuro), 0.5f * u, Offset(cab.left + cab.width * k / 6, cab.center.y + 0.3f * u))
        // La cuerda sagrada (shimenawa) trenzada, con sus papeles en zigzag (shide).
        val y0 = h * 0.145f
        val sag = 2.6f * u
        val cuerda = Path().apply { moveTo(w * 0.05f, y0); quadraticTo(w / 2, y0 + sag * 2, w * 0.95f, y0) }
        drawPath(cuerda, Color.Black.copy(alpha = 0.25f), style = Stroke(3.6f * u, cap = StrokeCap.Round))
        drawPath(cuerda, t(PAJA, oscuro), style = Stroke(3f * u, cap = StrokeCap.Round))
        val nt = (w * 0.9f / (2.2f * u)).toInt()
        for (k in 0..nt) {
            val tt = k / nt.toFloat()
            val x = w * 0.05f + w * 0.9f * tt
            val y = y0 + sag * 4 * tt * (1 - tt)
            drawLine(t(PAJA_OSC, oscuro), Offset(x - 0.9f * u, y - 1.3f * u), Offset(x + 0.9f * u, y + 1.3f * u), 0.5f * u)
        }
        listOf(0.12f, 0.36f, 0.64f, 0.88f).forEach { f ->
            val x = w * f
            val y = y0 + sag * 4 * f * (1 - f) + 1.2f * u
            val z = 1.6f * u
            val p = Path().apply {
                moveTo(x - z * 0.4f, y); lineTo(x + z * 0.6f, y); lineTo(x + z * 0.6f, y + z * 1.3f); lineTo(x + z * 1.4f, y + z * 1.3f)
                lineTo(x + z * 1.4f, y + z * 2.6f); lineTo(x + z * 0.6f, y + z * 2.6f); lineTo(x + z * 0.6f, y + z * 3.4f); lineTo(x - z * 0.4f, y + z * 3.4f)
                lineTo(x - z * 0.4f, y + z * 2.1f); lineTo(x + z * 0.4f, y + z * 2.1f); lineTo(x + z * 0.4f, y + z * 1.8f); lineTo(x - z * 0.4f, y + z * 1.8f); close()
            }
            translate(0.4f * u, 0.6f * u) { drawPath(p, Color.Black.copy(alpha = 0.2f)) }
            drawPath(p, if (oscuro) mezcla(WASHI, NOCHE, 0.25f) else Color.White)
            drawPath(p, WASHI_OSC, style = Stroke(0.2f * u))
        }
    }

    /** La paulonia del ema: clara de día; de noche, un poco más parda (la tinta se lee en los dos). */
    private fun tablaEma(oscuro: Boolean) = if (oscuro) Color(0xFFD3BF98) else Color(0xFFEBDDBC)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val u = r.width / 100f; val v = r.height / 100f
        val l = r.left + 4 * u; val d = r.right - 4 * u
        val pico = r.top + 5 * v; val alero = r.top + 15 * v; val bajo = r.bottom - 3 * v
        val forma = Path().apply { moveTo(l, alero); lineTo(r.center.x, pico); lineTo(d, alero); lineTo(d, bajo); lineTo(l, bajo); close() }
        // La sombra, la tabla con su veta, el canto y el tejadillo pintado.
        translate(1.4f * u, 2.2f * v) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f)) }
        drawPath(forma, tablaEma(oscuro))
        clipPath(forma) {
            vetas(Rect(l, pico, d, bajo), KIRI_VETA.copy(alpha = 0.4f), e.indice * 7 + 3, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.5f)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, KIRI_VETA.copy(alpha = 0.3f)), r.top + r.height * 0.6f, bajo), Offset(l, r.top + r.height * 0.6f), Size(d - l, bajo - r.top - r.height * 0.6f))
        }
        val techo = Path().apply { moveTo(l, alero); lineTo(r.center.x, pico); lineTo(d, alero); lineTo(d, alero + 2.4f * v); lineTo(r.center.x, pico + 2.4f * v); lineTo(l, alero + 2.4f * v); close() }
        drawPath(techo, if (e.fijada) SHU else KEYAKI)
        drawPath(forma, KIRI_VETA, style = Stroke(1f * u))
        drawLine(Color.White.copy(alpha = 0.45f), Offset(l + 1 * u, alero + 3 * v), Offset(l + 1 * u, bajo - 1 * v), 0.6f * u)
        // El agujero y el cordón que sube al varal.
        val ag = Offset(r.center.x, pico + 6 * v)
        drawCircle(Color(0xFF3A2A1A), 1.4f * u, ag)
        if (e.fijada) {
            // El cordón rojo y blanco con su nudo de mizuhiki y el remate de oro en el pico.
            listOf(-1f to SHU, 1f to Color(0xFFF7F3EA)).forEach { (s, col) ->
                drawPath(Path().apply { moveTo(ag.x + s * 0.6f * u, ag.y); lineTo(r.center.x + s * 5 * u, r.top) }, col, style = Stroke(1.1f * u, cap = StrokeCap.Round))
            }
            listOf(-1f, 1f).forEach { s ->
                drawOval(SHU, Offset(ag.x + (if (s < 0) -6.4f else 0.8f) * u, ag.y - 1.6f * v), Size(5.6f * u, 3.2f * v), style = Stroke(1f * u))
                drawOval(Color(0xFFF7F3EA), Offset(ag.x + (if (s < 0) -5.6f else 1.4f) * u, ag.y - 1f * v), Size(4.2f * u, 2f * v), style = Stroke(0.6f * u))
            }
            drawCircle(ORO, 1.6f * u, ag)
            drawCircle(ORO_LUZ, 0.6f * u, ag - Offset(0.4f * u, 0.4f * u))
            val cap = Path().apply { moveTo(r.center.x - 5 * u, pico + 2.9f * v); lineTo(r.center.x, pico - 0.6f * v); lineTo(r.center.x + 5 * u, pico + 2.9f * v); lineTo(r.center.x + 5 * u, pico + 4.4f * v); lineTo(r.center.x, pico + 1f * v); lineTo(r.center.x - 5 * u, pico + 4.4f * v); close() }
            drawPath(cap, ORO)
            drawPath(cap, ORO_OSC, style = Stroke(0.3f * u))
        } else {
            drawPath(Path().apply { moveTo(ag.x - 0.5f * u, ag.y); lineTo(r.center.x - 4 * u, r.top); moveTo(ag.x + 0.5f * u, ag.y); lineTo(r.center.x + 4 * u, r.top) }, PAJA_OSC, style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(ag.x - 0.5f * u, ag.y); lineTo(r.center.x - 4 * u, r.top) }, PAJA, style = Stroke(0.5f * u, cap = StrokeCap.Round))
        }
        // El detalle del tipo, abajo a la derecha.
        val q = Offset(d - 9 * u, bajo - 9 * v)
        when (e.tipo) {
            "idea" -> {
                // La flor del ciruelo con su ramita.
                drawLine(Color(0xFF4A3424), Offset(q.x - 7 * u, q.y + 6 * u), Offset(q.x + 4 * u, q.y - 4 * u), 0.8f * u, StrokeCap.Round)
                ume(q, 5.4f * u, CIRUELO, ORO)
                ume(Offset(q.x - 5 * u, q.y + 4 * u), 3 * u, mezcla(CIRUELO, Color.White, 0.3f), ORO)
                for (k in 0 until 5) { val a = Math.toRadians(-90.0 + k * 72.0); drawLine(ORO_OSC, q, Offset(q.x + 2 * u * cos(a).toFloat(), q.y + 2 * u * sin(a).toFloat()), 0.25f * u) }
            }
            "pensamiento" -> {
                // La luna creciente sobre las olas.
                val disco = Rect(q.x - 6 * u, q.y - 6 * u, q.x + 6 * u, q.y + 6 * u)
                clipPath(Path().apply { addOval(disco) }) {
                    drawRect(ANIL, disco.topLeft, disco.size)
                    seigaiha(Rect(disco.left, q.y + 0.5f * u, disco.right, disco.bottom), 2.2f * u, ANIL, Color.White.copy(alpha = 0.7f), 0.3f * u)
                }
                drawCircle(ORO_LUZ, 2.2f * u, Offset(q.x - 1.4f * u, q.y - 2.2f * u))
                drawCircle(ANIL, 2f * u, Offset(q.x - 0.4f * u, q.y - 2.8f * u))
                drawCircle(ORO_OSC, 6 * u, q, style = Stroke(0.5f * u))
            }
            "proposito" -> {
                // El sello bermellón (hanko) con su marco y sus trazos blancos.
                val s = Rect(q.x - 5.6f * u, q.y - 5.6f * u, q.x + 5.6f * u, q.y + 5.6f * u)
                rotate(-6f, q) {
                    drawRect(SHU, s.topLeft, s.size)
                    drawRect(Color.White.copy(alpha = 0.85f), s.deflate(0.9f * u).topLeft, s.deflate(0.9f * u).size, style = Stroke(0.5f * u))
                    drawLine(Color.White.copy(alpha = 0.85f), Offset(q.x, s.top + 1.8f * u), Offset(q.x, s.bottom - 1.8f * u), 0.6f * u)
                    drawLine(Color.White.copy(alpha = 0.85f), Offset(s.left + 1.8f * u, q.y - 1.2f * u), Offset(q.x - 0.8f * u, q.y - 1.2f * u), 0.6f * u)
                    drawLine(Color.White.copy(alpha = 0.85f), Offset(q.x + 0.8f * u, q.y + 1.4f * u), Offset(s.right - 1.8f * u, q.y + 1.4f * u), 0.6f * u)
                    drawLine(Color.White.copy(alpha = 0.85f), Offset(s.left + 1.8f * u, q.y + 2.6f * u), Offset(q.x - 1.4f * u, q.y + 2.6f * u), 0.6f * u)
                }
            }
            else -> {
                // La campanilla (suzu) de oro con su cordón.
                drawLine(SHU, Offset(q.x, q.y - 7 * u), Offset(q.x, q.y - 3 * u), 0.7f * u)
                drawCircle(Color.Black.copy(alpha = 0.25f), 4.2f * u, q + Offset(0.5f * u, 0.8f * u))
                drawCircle(ORO_OSC, 4.2f * u, q)
                drawCircle(ORO, 3.6f * u, q - Offset(0.2f * u, 0.3f * u))
                drawLine(ORO_OSC, Offset(q.x - 3.8f * u, q.y + 0.4f * u), Offset(q.x + 3.8f * u, q.y + 0.4f * u), 0.4f * u)
                drawLine(ORO_OSC, Offset(q.x, q.y + 0.4f * u), Offset(q.x, q.y + 4 * u), 0.6f * u)
                drawCircle(ORO_LUZ, 1f * u, q - Offset(1.4f * u, 1.6f * u))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = SUMI
}
