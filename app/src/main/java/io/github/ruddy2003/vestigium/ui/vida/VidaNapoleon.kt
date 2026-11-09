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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Napoleónico: los muebles de Vida y de Diario del Imperio (1804–1815), de la misma casa que su secrétaire de caoba del
 * Ideario, su biblioteca de campaña y su galería imperial de logros: caoba veteada con bronces dorados, seda verde y crema,
 * mármol verde, carmesí con fleco de oro, cuero verde, pergamino, lacre, abejas y coronas de laurel.
 * - Social: el salón de recepciones: la pared de seda verde sembrada de abejas, el gran panel de seda crema (de noche, azul
 *   imperial) en su marco dorado con rosetas, la cornisa con perlado, el lambrequín carmesí en festones con fleco y borlas y la
 *   corona de laurel con la abeja al centro; a los lados, pilastras de caoba con capitel y basa de bronce y una girándula de
 *   tres velas (de noche, encendidas); abajo, el zócalo de tableros y el parquet en espiga.
 * - Mapas: la mesa de campaña del Emperador: el tablero de caoba con su filete de latón, el cuero verde con hierros dorados,
 *   el mapa de pergamino clavado con alfileres; el tintero de bronce con su pluma y el compás a un lado, el bicornio y el
 *   catalejo al otro; el canto con perlado, el faldón con su cajón, las anillas de bronce y las patas con zapatas doradas.
 * - Otras vidas: la vitrina Imperio: la cornisa con dentículos, el friso de caoba con palmetas, rosetas y la corona de laurel,
 *   cinco hornacinas de seda (crema de día, azul de noche) separadas por columnillas con capitel dorado, dos columnas de
 *   mármol verde a los lados y el zócalo con sus apliques y sus patas de peonza.
 * - Íconos: el arco de triunfo de piedra con su ático, sus relieves y la bandera; el mapa de pergamino raspado por un
 *   napoleón de oro; tres banderas con su águila clavadas en el mapa de campaña.
 * - Notas: el tablero de despachos: el marco de caoba con su moldura dorada, rosetas en las esquinas, la seda tensada, el
 *   copete con la corona de laurel y la abeja, y la repisa con la pluma y la barra de lacre; las notas son despachos de
 *   pergamino con su doble filete azul y sus pliegues, prendidos con un alfiler de latón o, si están fijados, con el sello de
 *   lacre y sus cintas, y con un detalle según el tipo (la estrella de la Legión de Honor, el laurel, la escarapela tricolor o
 *   la esquina doblada).
 */
internal object VidaNapoleon : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su Ideario y su Hall)
    private val CAOBA = Color(0xFF6E2F1E)
    private val CAOBA_LUZ = Color(0xFF8D4430)
    private val CAOBA_VETA = Color(0xFF4A1E12)
    private val CAOBA_OSC = Color(0xFF34140A)
    private val ORO = Color(0xFFD9B54A)
    private val ORO_LUZ = Color(0xFFF0D688)
    private val ORO_OSC = Color(0xFF8A6A24)
    private val MARMOL = Color(0xFF2F5D4A)
    private val MARMOL_VETA = Color(0xFF9CC4AE)
    private val SEDA = Color(0xFF3F6B55)
    private val SEDA_OSC = Color(0xFF2E5240)
    private val CARMESI = Color(0xFFA3172E)
    private val CARMESI_LUZ = Color(0xFFC8344A)
    private val CARMESI_OSC = Color(0xFF6E0E1E)
    private val CUERO = Color(0xFF2E5A3A)
    private val CUERO_OSC = Color(0xFF1E3E28)
    private val AZUL = Color(0xFF1E3A8A)
    private val AZUL_OSC = Color(0xFF14275E)
    private val PERGAMINO = Color(0xFFEFE5C9)
    private val PERGAMINO_OSC = Color(0xFFD9C9A0)
    private val LACRE = Color(0xFF9A1A1A)
    private val TINTA = Color(0xFF14213D)
    private val ROBLE = Color(0xFFB08A5A)
    private val ROBLE_OSC = Color(0xFF7E5E3A)
    private val CERA = Color(0xFFF4ECDA)
    private val LLAMA = Color(0xFFFFC870)
    private val PIEDRA = Color(0xFFE6DCC4)
    private val PIEDRA_OSC = Color(0xFFB3A584)
    private val FIELTRO = Color(0xFF1C1A20)
    private val BLANCO = Color(0xFFF7F3EA)
    private val NOCHE = Color(0xFF0E0B08)

    /** La seda del nicho: crema de día, azul imperial de noche (de la luminancia de la tarjeta, para que el contenido se lea). */
    private fun sedaNicho(oscuro: Boolean) = if (oscuro) Color(0xFF18214A) else Color(0xFFF8F0DC)

    /** El color en oscuro: más hundido, como en su biblioteca y su Hall. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.34f) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.11f, 0.17f, 0.89f, 0.83f)
        MuebleVida.MAPAS -> Rect(0.15f, 0.085f, 0.85f, 0.715f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.2f, 0.935f, 0.81f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.15f, 0.93f, 0.88f)
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

    /** Las vetas de la caoba en [r], recortadas a la pieza: líneas onduladas a lo largo, más o menos marcadas. */
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

    /** Una tabla de caoba en [r]: el color, la veta, la luz arriba y la sombra abajo. */
    private fun DrawScope.caoba(r: Rect, oscuro: Boolean, semilla: Int, vertical: Boolean = false, color: Color = CAOBA) {
        drawRect(t(color, oscuro), r.topLeft, r.size)
        vetas(r, t(CAOBA_VETA, oscuro).copy(alpha = 0.55f), semilla, densidad = 0.8f, vertical = vertical, grosor = 0.7f, ondulacion = 0.8f)
    }

    /** El mármol verde en [r]: el fondo, las nubes y unas vetas claras que cruzan en diagonal. */
    private fun DrawScope.marmol(r: Rect, oscuro: Boolean, semilla: Int) {
        drawRect(t(MARMOL, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until 4) {
                val y0 = r.top + r.height * azar(semilla, k)
                val p = Path().apply {
                    moveTo(r.left, y0)
                    cubicTo(r.left + r.width * 0.4f, y0 + r.height * (azar(semilla, k + 9) - 0.3f) * 0.4f, r.left + r.width * 0.6f, y0 + r.height * 0.15f, r.right, y0 + r.height * (azar(semilla, k + 19) * 0.3f))
                }
                drawPath(p, t(MARMOL_VETA, oscuro).copy(alpha = 0.45f), style = Stroke((0.4f + 0.6f * azar(semilla, k + 29)) * density))
            }
        }
    }

    /** Una pieza de bronce dorado en [r]: el oro, su luz arriba y su sombra abajo. */
    private fun DrawScope.bronce(r: Rect, oscuro: Boolean, radio: Float = 0f) {
        drawRoundRect(t(ORO_OSC, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(t(ORO, oscuro), r.topLeft, Size(r.width, r.height * 0.78f), CornerRadius(radio))
        drawRoundRect(t(ORO_LUZ, oscuro), Offset(r.left + r.width * 0.04f, r.top + r.height * 0.12f), Size(r.width * 0.92f, r.height * 0.18f), CornerRadius(radio))
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

    /** Un halo de luz (las velas encendidas). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Una estrella de [puntas] puntas; [hondo] es el radio de los entrantes, en fracción de [r]. */
    private fun estrella(c: Offset, r: Float, puntas: Int, hondo: Float): Path = Path().apply {
        for (k in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + k * 180.0 / puntas)
            val rr = if (k % 2 == 0) r else r * hondo
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Una hoja de laurel: un óvalo girado. */
    private fun DrawScope.hoja(c: Offset, largo: Float, ancho: Float, grados: Float, color: Color) =
        rotate(grados, c) { drawOval(color, Offset(c.x - largo / 2, c.y - ancho / 2), Size(largo, ancho)) }

    /** La corona de laurel de bronce: dos ramas de hojas en dos tonos que se cierran arriba, y el lazo abajo. */
    private fun DrawScope.laurel(c: Offset, r: Float, oscuro: Boolean, hojas: Int = 7) {
        for (s in listOf(-1f, 1f)) for (k in 0 until hojas) {
            val a = Math.toRadians(90.0 + s * (30.0 + k * 130.0 / hojas))
            val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            val g = Math.toDegrees(a).toFloat() + 90f + s * 20f
            hoja(p, r * 0.5f, r * 0.24f, g, t(ORO_OSC, oscuro))
            hoja(p - Offset(r * 0.03f, r * 0.03f), r * 0.42f, r * 0.17f, g, t(if (k % 2 == 0) ORO else ORO_LUZ, oscuro))
        }
        drawCircle(t(ORO_OSC, oscuro), r * 0.12f, Offset(c.x, c.y + r))
        drawCircle(t(ORO, oscuro), r * 0.07f, Offset(c.x, c.y + r))
    }

    /** La abeja imperial de bronce, vista desde arriba: las alas, el cuerpo con sus anillos y la cabeza. */
    private fun DrawScope.abeja(c: Offset, s: Float, oscuro: Boolean, cuerpo: Color = ORO, alas: Color = ORO_LUZ) {
        listOf(-1f, 1f).forEach { k ->
            rotate(k * 28f, Offset(c.x, c.y - s * 0.1f)) { drawOval(t(alas, oscuro), Offset(c.x + (if (k < 0) -s * 0.36f else s * 0.04f), c.y - s * 0.42f), Size(s * 0.32f, s * 0.44f)) }
        }
        drawOval(t(ORO_OSC, oscuro), Offset(c.x - s * 0.15f, c.y - s * 0.24f), Size(s * 0.3f, s * 0.66f))
        drawOval(t(cuerpo, oscuro), Offset(c.x - s * 0.11f, c.y - s * 0.22f), Size(s * 0.22f, s * 0.6f))
        for (k in 0 until 3) drawLine(t(ORO_OSC, oscuro), Offset(c.x - s * 0.1f, c.y + s * (0.0f + k * 0.12f)), Offset(c.x + s * 0.1f, c.y + s * (0.0f + k * 0.12f)), s * 0.04f)
        drawCircle(t(ORO_OSC, oscuro), s * 0.12f, Offset(c.x, c.y - s * 0.3f))
        drawCircle(t(cuerpo, oscuro), s * 0.08f, Offset(c.x, c.y - s * 0.31f))
    }

    /** Una roseta de bronce: ocho pétalos alrededor del botón. */
    private fun DrawScope.roseta(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(t(ORO_OSC, oscuro), r, c)
        for (k in 0 until 8) {
            val a = k * Math.PI / 4
            drawCircle(t(ORO, oscuro), r * 0.34f, Offset(c.x + r * 0.58f * cos(a).toFloat(), c.y + r * 0.58f * sin(a).toFloat()))
        }
        drawCircle(t(ORO_LUZ, oscuro), r * 0.34f, c)
    }

    /** Una palmeta de bronce que nace de [c] hacia arriba, de alto [s]. */
    private fun DrawScope.palmeta(c: Offset, s: Float, oscuro: Boolean) {
        for (k in -3..3) rotate(k * 22f, c) {
            drawOval(t(ORO_OSC, oscuro), Offset(c.x - s * 0.1f, c.y - s), Size(s * 0.2f, s * 0.82f))
            drawOval(t(if (k == 0) ORO_LUZ else ORO, oscuro), Offset(c.x - s * 0.065f, c.y - s * 0.96f), Size(s * 0.13f, s * 0.72f))
        }
        drawArc(t(ORO, oscuro), 180f, 180f, true, Offset(c.x - s * 0.22f, c.y - s * 0.16f), Size(s * 0.44f, s * 0.32f))
    }

    /** Una fila de perlas doradas a lo largo de [y], entre [x0] y [x1]. */
    private fun DrawScope.perlado(y: Float, x0: Float, x1: Float, r: Float, oscuro: Boolean) {
        var x = x0 + r * 1.6f
        while (x < x1 - r) {
            drawCircle(t(ORO_OSC, oscuro), r, Offset(x, y + r * 0.2f))
            drawCircle(t(ORO, oscuro), r * 0.8f, Offset(x, y))
            drawCircle(t(ORO_LUZ, oscuro), r * 0.3f, Offset(x - r * 0.25f, y - r * 0.25f))
            x += r * 3.2f
        }
    }

    /** Un marco dorado alrededor de [r] (por fuera), de grueso [g]: la moldura con su luz y su sombra. */
    private fun DrawScope.marcoDorado(r: Rect, g: Float, oscuro: Boolean) {
        val m = Rect(r.left - g / 2, r.top - g / 2, r.right + g / 2, r.bottom + g / 2)
        drawRect(t(ORO_OSC, oscuro), m.topLeft, m.size, style = Stroke(g))
        drawRect(t(ORO, oscuro), m.topLeft, m.size, style = Stroke(g * 0.6f))
        drawLine(t(ORO_LUZ, oscuro), Offset(m.left, m.top - g * 0.12f), Offset(m.right, m.top - g * 0.12f), g * 0.22f)
        drawLine(t(ORO_LUZ, oscuro), Offset(m.left - g * 0.12f, m.top), Offset(m.left - g * 0.12f, m.bottom), g * 0.22f)
    }

    /**
     * Una pilastra o columna Imperio en [r]: el fuste (de caoba o de mármol verde) con sus estrías, el capitel de bronce
     * (collarino, equino con hojas y ábaco) y la basa (toro y plinto).
     */
    private fun DrawScope.columna(r: Rect, oscuro: Boolean, semilla: Int, marmol: Boolean) {
        val w = r.width
        val cap = w * 0.75f
        val bas = w * 0.6f
        val fuste = Rect(r.left + w * 0.12f, r.top + cap, r.right - w * 0.12f, r.bottom - bas)
        if (marmol) marmol(fuste, oscuro, semilla) else caoba(fuste, oscuro, semilla, vertical = true)
        // Las estrías: surcos con su luz al lado.
        val n = if (fuste.width > 8 * density) 4 else 2
        for (k in 1..n) {
            val x = fuste.left + fuste.width * k / (n + 1)
            drawLine(Color.Black.copy(alpha = 0.32f), Offset(x, fuste.top + cap * 0.2f), Offset(x, fuste.bottom - bas * 0.2f), max(0.5f * density, fuste.width * 0.05f), StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.14f), Offset(x + fuste.width * 0.06f, fuste.top + cap * 0.2f), Offset(x + fuste.width * 0.06f, fuste.bottom - bas * 0.2f), max(0.4f * density, fuste.width * 0.03f), StrokeCap.Round)
        }
        drawRect(Color.White.copy(alpha = 0.1f), fuste.topLeft, Size(fuste.width * 0.22f, fuste.height))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(fuste.right - fuste.width * 0.2f, fuste.top), Size(fuste.width * 0.2f, fuste.height))
        // El capitel: collarino, equino con dos hojas de agua y el ábaco.
        bronce(Rect(fuste.left - w * 0.02f, fuste.top - cap * 0.18f, fuste.right + w * 0.02f, fuste.top), oscuro)
        val eq = Path().apply { moveTo(fuste.left, fuste.top - cap * 0.18f); lineTo(r.left + w * 0.02f, r.top + cap * 0.32f); lineTo(r.right - w * 0.02f, r.top + cap * 0.32f); lineTo(fuste.right, fuste.top - cap * 0.18f); close() }
        drawPath(eq, t(ORO, oscuro))
        listOf(0.3f, 0.7f).forEach { f ->
            val x = r.left + w * f
            drawPath(Path().apply { moveTo(x, fuste.top - cap * 0.2f); quadraticTo(x - w * 0.14f, r.top + cap * 0.5f, x, r.top + cap * 0.34f); quadraticTo(x + w * 0.14f, r.top + cap * 0.5f, x, fuste.top - cap * 0.2f) }, t(ORO_OSC, oscuro))
        }
        bronce(Rect(r.left, r.top, r.right, r.top + cap * 0.34f), oscuro)
        // La basa: el toro redondo y el plinto.
        bronce(Rect(fuste.left - w * 0.06f, fuste.bottom, fuste.right + w * 0.06f, fuste.bottom + bas * 0.45f), oscuro, radio = bas * 0.2f)
        bronce(Rect(r.left, fuste.bottom + bas * 0.45f, r.right, r.bottom), oscuro)
    }

    /** Una girándula de bronce de tres velas, colgada de [p]: el escudete, los brazos, los platillos y las llamas. */
    private fun DrawScope.girandula(p: Offset, s: Float, oscuro: Boolean) {
        val oro = t(ORO, oscuro)
        val brazo = Stroke(s * 0.06f, cap = StrokeCap.Round)
        drawOval(t(ORO_OSC, oscuro), Offset(p.x - s * 0.13f, p.y - s * 0.2f), Size(s * 0.26f, s * 0.4f))
        drawOval(oro, Offset(p.x - s * 0.09f, p.y - s * 0.16f), Size(s * 0.18f, s * 0.32f))
        val velas = listOf(-0.36f, 0f, 0.36f)
        velas.forEach { f ->
            val cx = p.x + s * f
            val y = p.y - s * (if (f == 0f) 0.45f else 0.3f)
            if (f != 0f) drawPath(Path().apply { moveTo(p.x, p.y + s * 0.1f); quadraticTo(cx, p.y + s * 0.2f, cx, y + s * 0.05f) }, oro, style = brazo)
            else drawLine(oro, Offset(p.x, p.y), Offset(cx, y), s * 0.06f)
            drawRect(t(ORO_OSC, oscuro), Offset(cx - s * 0.1f, y), Size(s * 0.2f, s * 0.06f))
            drawRect(t(CERA, oscuro), Offset(cx - s * 0.045f, y - s * 0.3f), Size(s * 0.09f, s * 0.3f))
            drawLine(Color.Black.copy(alpha = 0.6f), Offset(cx, y - s * 0.3f), Offset(cx, y - s * 0.34f), s * 0.02f)
            if (oscuro) {
                drawOval(LLAMA, Offset(cx - s * 0.045f, y - s * 0.46f), Size(s * 0.09f, s * 0.15f))
                drawOval(Color.White.copy(alpha = 0.9f), Offset(cx - s * 0.02f, y - s * 0.41f), Size(s * 0.04f, s * 0.07f))
            }
        }
        drawCircle(oro, s * 0.07f, Offset(p.x, p.y + s * 0.2f))
    }

    /** El lambrequín carmesí: la banda con sus galones de [y0] a [y1] y los festones con fleco y borlas, de hondo [prof]. */
    private fun DrawScope.lambrequin(y0: Float, y1: Float, prof: Float, oscuro: Boolean) {
        val w = size.width; val u = size.minDimension / 100f
        drawRect(t(CARMESI, oscuro), Offset(0f, y0), Size(w, y1 - y0))
        drawRect(t(CARMESI_OSC, oscuro).copy(alpha = 0.5f), Offset(0f, y1 - (y1 - y0) * 0.25f), Size(w, (y1 - y0) * 0.25f))
        drawRect(t(ORO, oscuro), Offset(0f, y0 + (y1 - y0) * 0.2f), Size(w, 0.6f * u))
        drawRect(t(ORO, oscuro), Offset(0f, y1 - (y1 - y0) * 0.38f), Size(w, 0.4f * u))
        val n = max(5, (w / (26 * u)).roundToInt())
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso; val x1 = x0 + paso
            val p1 = Offset(x1 - paso * 0.15f, y1 + prof * 1.33f); val p2 = Offset(x0 + paso * 0.15f, y1 + prof * 1.33f)
            val f = Path().apply { moveTo(x0, y1 - 0.5f); lineTo(x1, y1 - 0.5f); cubicTo(p1.x, p1.y, p2.x, p2.y, x0, y1) }
            drawPath(f, t(CARMESI, oscuro))
            // Los pliegues del festón: sombras y una luz.
            listOf(0.38f to CARMESI_OSC, 0.62f to CARMESI_OSC, 0.5f to CARMESI_LUZ).forEach { (k, col) ->
                val q = Path().apply { moveTo(x0 + paso * 0.08f, y1); quadraticTo((x0 + x1) / 2, y1 + prof * k * 1.6f, x1 - paso * 0.08f, y1) }
                drawPath(q, t(col, oscuro).copy(alpha = if (col == CARMESI_LUZ) 0.5f else 0.7f), style = Stroke(0.5f * u))
            }
            // El fleco de oro a lo largo de la curva.
            drawPath(Path().apply { moveTo(x1, y1); cubicTo(p1.x, p1.y, p2.x, p2.y, x0, y1) }, t(ORO, oscuro), style = Stroke(0.9f * u))
            for (s in 1 until 16) {
                val tt = s / 16f; val a = 1 - tt
                val bx = a * a * a * x1 + 3 * a * a * tt * p1.x + 3 * a * tt * tt * p2.x + tt * tt * tt * x0
                val by = a * a * a * y1 + 3 * a * a * tt * p1.y + 3 * a * tt * tt * p2.y + tt * tt * tt * y1
                drawLine(t(if (s % 2 == 0) ORO else ORO_OSC, oscuro), Offset(bx, by), Offset(bx, by + 1.6f * u), 0.45f * u)
            }
        }
        // Las borlas entre festón y festón.
        for (i in 0..n) {
            val x = i * paso
            drawLine(t(ORO_OSC, oscuro), Offset(x, y1), Offset(x, y1 + prof * 0.7f), 0.4f * u)
            drawCircle(t(ORO, oscuro), 0.9f * u, Offset(x, y1 + prof * 0.7f))
            val b = Path().apply { moveTo(x - 0.6f * u, y1 + prof * 0.75f); lineTo(x + 0.6f * u, y1 + prof * 0.75f); lineTo(x + 1.4f * u, y1 + prof * 1.25f); lineTo(x - 1.4f * u, y1 + prof * 1.25f); close() }
            drawPath(b, t(ORO, oscuro))
            drawLine(t(ORO_OSC, oscuro), Offset(x, y1 + prof * 0.8f), Offset(x, y1 + prof * 1.24f), 0.3f * u)
        }
    }

    /** El parquet de roble en espiga entre [y0] y [y1]. */
    private fun DrawScope.parquet(y0: Float, y1: Float, oscuro: Boolean) {
        val w = size.width; val u = size.minDimension / 100f
        val h = y1 - y0
        drawRect(t(ROBLE, oscuro), Offset(0f, y0), Size(w, h))
        clipRect(0f, y0, w, y1) {
            val c = h * 0.9f
            var j = 0
            var x = 0f
            while (x < w) {
                val s = if (j % 2 == 0) 1f else -1f
                var y = y0 - c
                var k = 0
                while (y < y1) {
                    val p = Path().apply { moveTo(x, y); lineTo(x + c * 0.6f, y + s * c * 0.3f); lineTo(x + c * 0.6f, y + s * c * 0.3f + c * 0.35f); lineTo(x, y + c * 0.35f); close() }
                    drawPath(p, t(if ((k + j) % 2 == 0) ROBLE else mezcla(ROBLE, ROBLE_OSC, 0.35f), oscuro))
                    drawPath(p, t(ROBLE_OSC, oscuro).copy(alpha = 0.7f), style = Stroke(0.3f * u))
                    y += c * 0.35f; k++
                }
                x += c * 0.6f; j++
            }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), y0, y0 + h * 0.6f), Offset(0f, y0), Size(w, h * 0.6f))
    }

    // ---------------------------------------------------------------- Social: el salón de recepciones

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        // La pared de seda verde, con sus abejas de oro sembradas en tresbolillo.
        drawRect(t(SEDA, oscuro))
        val paso = 11 * u
        var fila = 0
        var y = paso * 0.5f
        while (y < h) {
            var x = if (fila % 2 == 0) paso * 0.5f else paso
            while (x < w) { abeja(Offset(x, y), 2.6f * u, oscuro, cuerpo = mezcla(SEDA, ORO, 0.45f), alas = mezcla(SEDA, ORO, 0.3f)); x += paso }
            y += paso * 0.8f; fila++
        }
        for (k in 0 until 18) drawLine(t(SEDA_OSC, oscuro).copy(alpha = 0.3f), Offset(w * k / 18f, 0f), Offset(w * k / 18f, h), 0.3f * u)
        // De noche, las velas encendidas dan su halo a la pared.
        val pil = w * 0.052f
        val xs = listOf(w * 0.012f, w - w * 0.012f - pil)
        val gy = h * 0.42f
        if (oscuro) xs.forEach { x -> halo(Offset(x + pil / 2, gy - 6 * u), 26 * u, LLAMA.copy(alpha = 0.32f)) }
        // Las pilastras de caoba con capitel y basa de bronce, y su girándula.
        xs.forEachIndexed { i, x ->
            columna(Rect(x, h * 0.12f, x + pil, h * 0.885f), oscuro, e.semilla + i, marmol = false)
            girandula(Offset(x + pil / 2, gy), 12 * u, oscuro)
        }
        // El zócalo de tableros de caoba y el parquet.
        val zoc = Rect(0f, h * 0.885f, w, h * 0.95f)
        caoba(zoc, oscuro, e.semilla + 3)
        drawRect(t(CAOBA_LUZ, oscuro), zoc.topLeft, Size(w, 0.9f * u))
        drawRect(t(ORO, oscuro), Offset(0f, zoc.top + 0.9f * u), Size(w, 0.45f * u))
        val nt = max(4, (w / (30 * u)).roundToInt())
        for (k in 0 until nt) {
            val r = Rect(w * k / nt + 2 * u, zoc.top + 2.2f * u, w * (k + 1) / nt - 2 * u, zoc.bottom - 1.6f * u)
            drawRect(t(CAOBA_OSC, oscuro).copy(alpha = 0.35f), r.topLeft, r.size)
            drawRect(t(ORO, oscuro).copy(alpha = 0.85f), r.topLeft, r.size, style = Stroke(0.35f * u))
            roseta(r.center, 1.1f * u, oscuro)
        }
        parquet(h * 0.95f, h, oscuro)
        // La cornisa de caoba con su perlado, el lambrequín y la corona de laurel con la abeja.
        val corn = Rect(0f, 0f, w, h * 0.055f)
        caoba(corn, oscuro, e.semilla + 5, color = CAOBA_OSC)
        drawRect(t(CAOBA_LUZ, oscuro), Offset(0f, corn.bottom - 1.6f * u), Size(w, 1.6f * u))
        perlado(corn.height * 0.45f, 0f, w, 0.75f * u, oscuro)
        lambrequin(corn.bottom, h * 0.095f, h * 0.05f, oscuro)
        // El gran panel de seda en su marco dorado, con rosetas en las esquinas (el nicho del grafo).
        sombra(n.inflate(2.2f * u), 2.5f * u, if (oscuro) 0.45f else 0.3f)
        marcoDorado(n, 2.6f * u, oscuro)
        listOf(n.topLeft, Offset(n.right, n.top), Offset(n.left, n.bottom), n.bottomRight).forEach { c ->
            val q = Offset(c.x + (if (c.x < w / 2) -1.3f else 1.3f) * u, c.y + (if (c.y < h / 2) -1.3f else 1.3f) * u)
            drawRect(t(ORO_OSC, oscuro), Offset(q.x - 2f * u, q.y - 2f * u), Size(4f * u, 4f * u))
            roseta(q, 1.7f * u, oscuro)
        }
        drawRect(sedaNicho(oscuro), n.topLeft, n.size)
        // La seda: un damasco muy suave de abejas y la sombra de la moldura arriba.
        val tono = if (oscuro) Color.White.copy(alpha = 0.035f) else ORO_OSC.copy(alpha = 0.07f)
        clipRect(n.left, n.top, n.right, n.bottom) {
            var yy = n.top + 8 * u; var f = 0
            while (yy < n.bottom) {
                var xx = n.left + (if (f % 2 == 0) 8f else 16f) * u
                while (xx < n.right) { drawPath(estrella(Offset(xx, yy), 1.3f * u, 4, 0.35f), tono); xx += 16 * u }
                yy += 9 * u; f++
            }
        }
        sombraInterior(n, 3.5f * u, if (oscuro) 0.3f else 0.12f)
        // El copete: la corona de laurel con la abeja, sobre el lambrequín.
        val cc = Offset(w / 2, h * 0.085f)
        drawCircle(t(CARMESI_OSC, oscuro), 6.4f * u, cc)
        drawCircle(t(ORO_OSC, oscuro), 6.4f * u, cc, style = Stroke(0.5f * u))
        laurel(cc, 5.2f * u, oscuro)
        abeja(Offset(cc.x, cc.y + 0.3f * u), 5.4f * u, oscuro)
        listOf(-1f, 1f).forEach { s -> palmeta(Offset(cc.x + s * 11 * u, corn.bottom + 0.5f * u), 4.5f * u, oscuro) }
    }

    // ---------------------------------------------------------------- Mapas: la mesa de campaña

    /** El tintero de bronce con su pluma de ave, de pie en [p] (la base). */
    private fun DrawScope.tintero(p: Offset, s: Float, oscuro: Boolean) {
        sombra(Rect(p.x - s * 0.5f, p.y - s * 0.08f, p.x + s * 0.5f, p.y), s * 0.08f, 0.35f)
        bronce(Rect(p.x - s * 0.5f, p.y - s * 0.12f, p.x + s * 0.5f, p.y), oscuro, s * 0.04f)
        drawRect(t(FIELTRO, oscuro), Offset(p.x - s * 0.28f, p.y - s * 0.46f), Size(s * 0.56f, s * 0.34f))
        drawRect(Color.White.copy(alpha = 0.18f), Offset(p.x - s * 0.24f, p.y - s * 0.44f), Size(s * 0.08f, s * 0.3f))
        bronce(Rect(p.x - s * 0.34f, p.y - s * 0.54f, p.x + s * 0.34f, p.y - s * 0.44f), oscuro, s * 0.03f)
        // La pluma, inclinada, con sus barbas.
        val a = Offset(p.x + s * 0.04f, p.y - s * 0.5f); val b = Offset(p.x + s * 0.55f, p.y - s * 1.55f)
        val pl = Path().apply { moveTo(a.x, a.y); quadraticTo(b.x - s * 0.5f, b.y + s * 0.5f, b.x, b.y); quadraticTo(b.x - s * 0.05f, b.y + s * 0.7f, a.x + s * 0.05f, a.y - s * 0.3f); close() }
        drawPath(pl, t(BLANCO, oscuro))
        drawPath(pl, t(PIEDRA_OSC, oscuro), style = Stroke(s * 0.02f))
        drawLine(t(PIEDRA_OSC, oscuro), a, b, s * 0.03f)
    }

    /** El compás de puntas de latón, abierto, con la cabeza en [c]. */
    private fun DrawScope.compas(c: Offset, l: Float, oscuro: Boolean) {
        listOf(-14f, 14f).forEach { g -> rotate(g, c) {
            drawPath(Path().apply { moveTo(c.x - l * 0.05f, c.y); lineTo(c.x + l * 0.05f, c.y); lineTo(c.x, c.y + l); close() }, t(if (g < 0) ORO else ORO_OSC, oscuro))
            drawLine(t(Color(0xFF5A5A60), oscuro), Offset(c.x, c.y + l * 0.82f), Offset(c.x, c.y + l), l * 0.03f)
        } }
        drawCircle(t(ORO_OSC, oscuro), l * 0.09f, c)
        drawCircle(t(ORO_LUZ, oscuro), l * 0.05f, c)
    }

    /** El bicornio de fieltro negro, visto de frente, con su escarapela tricolor y la presilla de oro; el ala baja en [p]. */
    private fun DrawScope.bicornio(p: Offset, a: Float, oscuro: Boolean) {
        sombra(Rect(p.x - a * 0.5f, p.y - a * 0.05f, p.x + a * 0.5f, p.y + a * 0.03f), a * 0.06f, 0.35f)
        val sil = Path().apply { moveTo(p.x - a * 0.5f, p.y); quadraticTo(p.x, p.y - a * 0.95f, p.x + a * 0.5f, p.y); quadraticTo(p.x, p.y - a * 0.22f, p.x - a * 0.5f, p.y); close() }
        drawPath(sil, t(FIELTRO, oscuro))
        drawPath(Path().apply { moveTo(p.x - a * 0.46f, p.y - a * 0.02f); quadraticTo(p.x, p.y - a * 0.24f, p.x + a * 0.46f, p.y - a * 0.02f) }, t(ORO_OSC, oscuro), style = Stroke(a * 0.025f))
        drawPath(Path().apply { moveTo(p.x - a * 0.3f, p.y - a * 0.2f); quadraticTo(p.x - a * 0.05f, p.y - a * 0.6f, p.x + a * 0.15f, p.y - a * 0.42f) }, Color.White.copy(alpha = 0.12f), style = Stroke(a * 0.04f, cap = StrokeCap.Round))
        val ec = Offset(p.x + a * 0.16f, p.y - a * 0.3f)
        drawCircle(t(AZUL, oscuro), a * 0.1f, ec)
        drawCircle(t(BLANCO, oscuro), a * 0.066f, ec)
        drawCircle(t(CARMESI, oscuro), a * 0.035f, ec)
        drawLine(t(ORO, oscuro), Offset(ec.x, ec.y - a * 0.1f), Offset(ec.x + a * 0.03f, p.y - a * 0.12f), a * 0.035f)
    }

    /** El catalejo de latón y caoba, en vertical, del ocular arriba en [a] al objetivo abajo en [b]. */
    private fun DrawScope.catalejo(a: Offset, b: Offset, g: Float, oscuro: Boolean) {
        val l = b.y - a.y
        sombra(Rect(a.x - g * 0.4f, a.y + l * 0.04f, a.x + g * 0.7f, b.y + l * 0.02f), g * 0.3f, 0.25f)
        val tramos = listOf(0f to 0.22f, 0.22f to 0.45f, 0.45f to 1f)
        tramos.forEachIndexed { i, (f0, f1) ->
            val gr = g * (0.55f + 0.2f * i)
            val r = Rect(a.x - gr / 2, a.y + l * f0, a.x + gr / 2, a.y + l * f1)
            if (i == 2) caoba(r, oscuro, 7, vertical = true) else drawRect(t(ORO, oscuro), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.2f), r.topLeft, Size(r.width * 0.25f, r.height))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
            bronce(Rect(r.left - g * 0.06f, r.top, r.right + g * 0.06f, r.top + g * 0.22f), oscuro)
        }
        bronce(Rect(a.x - g * 0.55f, b.y - g * 0.3f, a.x + g * 0.55f, b.y), oscuro)
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        // Las patas de caoba, que se afinan al bajar, con su anillo y su zapata de bronce, y la sombra en el suelo.
        val canto = h * 0.78f
        sombra(Rect(w * 0.03f, h * 0.965f, w * 0.97f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(w * 0.025f, w * 0.915f).forEachIndexed { i, x ->
            val pw = w * 0.06f
            val p = Path().apply { moveTo(x + pw * 0.1f, h * 0.9f); lineTo(x + pw * 0.9f, h * 0.9f); lineTo(x + pw * 0.7f, h * 0.975f); lineTo(x + pw * 0.3f, h * 0.975f); close() }
            drawPath(p, t(CAOBA, oscuro))
            clipPath(p) { vetas(Rect(x, h * 0.9f, x + pw, h), t(CAOBA_VETA, oscuro).copy(alpha = 0.5f), e.semilla + 30 + i, vertical = true, grosor = 0.6f) }
            drawPath(Path().apply { moveTo(x + pw * 0.55f, h * 0.9f); lineTo(x + pw * 0.9f, h * 0.9f); lineTo(x + pw * 0.7f, h * 0.975f); lineTo(x + pw * 0.5f, h * 0.975f); close() }, Color.Black.copy(alpha = 0.25f))
            bronce(Rect(x + pw * 0.06f, h * 0.9f, x + pw * 0.94f, h * 0.915f), oscuro)
            bronce(Rect(x + pw * 0.24f, h * 0.965f, x + pw * 0.76f, h * 0.995f), oscuro, 0.5f * u)
        }
        // El faldón con su cajón, las anillas con mascarón y los dados de las esquinas con roseta.
        val fal = Rect(w * 0.025f, h * 0.825f, w * 0.975f, h * 0.9f)
        caoba(fal, oscuro, e.semilla + 1)
        drawRect(Color.Black.copy(alpha = 0.3f), fal.topLeft, Size(fal.width, 1.2f * u))
        val caj = Rect(w * 0.36f, fal.top + 1.6f * u, w * 0.64f, fal.bottom - 1.2f * u)
        drawRect(t(CAOBA_OSC, oscuro), caj.topLeft, caj.size, style = Stroke(0.5f * u))
        drawRect(t(ORO, oscuro), Rect(caj.left + 0.9f * u, caj.top + 0.9f * u, caj.right - 0.9f * u, caj.bottom - 0.9f * u).topLeft,
            Size(caj.width - 1.8f * u, caj.height - 1.8f * u), style = Stroke(0.35f * u))
        val ce = caj.center
        drawOval(t(ORO, oscuro), Offset(ce.x - 1.3f * u, ce.y - 2f * u), Size(2.6f * u, 4f * u))
        drawCircle(t(CAOBA_OSC, oscuro), 0.5f * u, Offset(ce.x, ce.y - 0.4f * u))
        drawLine(t(CAOBA_OSC, oscuro), Offset(ce.x, ce.y - 0.2f * u), Offset(ce.x, ce.y + 1.1f * u), 0.4f * u)
        listOf(w * 0.2f, w * 0.8f).forEach { x ->
            val c = Offset(x, fal.center.y - 0.6f * u)
            drawCircle(t(ORO_OSC, oscuro), 1.6f * u, c)
            drawCircle(t(ORO, oscuro), 1.2f * u, c)
            drawCircle(t(ORO, oscuro), 2.2f * u, Offset(c.x, c.y + 1.6f * u), style = Stroke(0.5f * u))
        }
        listOf(fal.left, fal.right - w * 0.06f).forEach { x ->
            val d = Rect(x, fal.top, x + w * 0.06f, fal.bottom)
            drawRect(t(CAOBA_LUZ, oscuro), d.topLeft, d.size)
            drawRect(t(ORO, oscuro), d.deflate(0.8f * u).topLeft, d.deflate(0.8f * u).size, style = Stroke(0.3f * u))
            roseta(d.center, 1.8f * u, oscuro)
        }
        // El canto del tablero: la moldura de luz, el perlado dorado y la sombra de debajo.
        val ct = Rect(w * 0.01f, canto, w * 0.99f, h * 0.825f)
        caoba(ct, oscuro, e.semilla + 2, color = CAOBA_LUZ)
        drawRect(Color.White.copy(alpha = 0.18f), ct.topLeft, Size(ct.width, 0.7f * u))
        perlado(ct.center.y + 0.3f * u, ct.left, ct.right, 0.6f * u, oscuro)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(ct.left, ct.bottom - 0.6f * u), Size(ct.width, 0.6f * u))
        // El tablero visto desde arriba: la caoba en hojas casadas, el filete de latón y el cuero verde con hierros dorados.
        val tab = Rect(w * 0.01f, h * 0.015f, w * 0.99f, canto)
        caoba(tab, oscuro, e.semilla + 4)
        vetas(Rect(tab.left, tab.top, tab.center.x, tab.bottom), t(CAOBA_VETA, oscuro).copy(alpha = 0.3f), e.semilla + 8, densidad = 0.4f, grosor = 0.6f, ondulacion = 3f)
        drawRect(t(ORO, oscuro), tab.deflate(1.4f * u).topLeft, tab.deflate(1.4f * u).size, style = Stroke(0.35f * u))
        val cu = Rect(tab.left + w * 0.035f, tab.top + 3.2f * u, tab.right - w * 0.035f, tab.bottom - 3.2f * u)
        drawRect(t(CUERO, oscuro), cu.topLeft, cu.size)
        for (k in 0 until 10) {
            val c = Offset(cu.left + cu.width * azar(e.semilla, k), cu.top + cu.height * azar(e.semilla, k + 20))
            drawCircle(Brush.radialGradient(listOf(t(CUERO_OSC, oscuro).copy(alpha = 0.5f), Color.Transparent), c, 12 * u), 12 * u, c)
        }
        drawRect(t(ORO, oscuro), cu.deflate(1.4f * u).topLeft, cu.deflate(1.4f * u).size, style = Stroke(0.45f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.7f), cu.deflate(2.4f * u).topLeft, cu.deflate(2.4f * u).size, style = Stroke(0.25f * u))
        listOf(cu.topLeft, Offset(cu.right, cu.top), Offset(cu.left, cu.bottom), cu.bottomRight).forEach { c ->
            drawPath(estrella(Offset(c.x + (if (c.x < w / 2) 2.4f else -2.4f) * u, c.y + (if (c.y < h / 2) 2.4f else -2.4f) * u), 1.6f * u, 4, 0.35f), t(ORO, oscuro))
        }
        // El mapa de pergamino: un poco más grande que el nicho, con su sombra y un alfiler en cada esquina.
        val pg = n.inflate(2.6f * u)
        sombra(pg, 2.2f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(if (oscuro) Color(0xFF4A3D2C) else PERGAMINO_OSC, pg.topLeft, pg.size)
        drawRect(sedaMapa(oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(sedaMapa(oscuro), n.topLeft, n.size)
        drawRect(t(ORO_OSC, oscuro).copy(alpha = 0.5f), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size, style = Stroke(0.3f * u))
        listOf(Offset(pg.left + 1.3f * u, pg.top + 1.3f * u) to AZUL, Offset(pg.right - 1.3f * u, pg.top + 1.3f * u) to CARMESI,
            Offset(pg.left + 1.3f * u, pg.bottom - 1.3f * u) to CARMESI, Offset(pg.right - 1.3f * u, pg.bottom - 1.3f * u) to AZUL).forEach { (c, col) ->
            drawCircle(Color.Black.copy(alpha = 0.3f), 1.2f * u, c + Offset(0.4f * u, 0.5f * u))
            drawCircle(t(col, oscuro), 1.1f * u, c)
            drawCircle(Color.White.copy(alpha = 0.6f), 0.35f * u, c - Offset(0.35f * u, 0.35f * u))
        }
        // A un lado, el tintero con su pluma y el compás; al otro, el bicornio y el catalejo.
        val izq = (cu.left + pg.left) / 2
        val der = (cu.right + pg.right) / 2
        val ancho = (pg.left - cu.left) * 0.8f
        compas(Offset(izq, cu.top + 5 * u), 20 * u, oscuro)
        tintero(Offset(izq, cu.bottom - 3.5f * u), ancho.coerceAtMost(14 * u), oscuro)
        bicornio(Offset(der, cu.top + 15 * u), (ancho * 1.05f).coerceAtMost(17 * u), oscuro)
        catalejo(Offset(der, cu.top + 22 * u), Offset(der, cu.bottom - 3 * u), 4.2f * u, oscuro)
    }

    /** El pergamino del mapa (el nicho): claro de día; de noche, en la penumbra de la tienda. */
    private fun sedaMapa(oscuro: Boolean) = if (oscuro) Color(0xFF241D17) else Color(0xFFF6EEDA)

    // ---------------------------------------------------------------- Otras vidas: la vitrina Imperio

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        // La sombra en el suelo y las patas de peonza.
        sombra(Rect(w * 0.02f, h * 0.95f, w * 0.98f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(0.05f, 0.35f, 0.65f, 0.95f).forEach { f ->
            val x = w * f
            drawOval(t(CAOBA, oscuro), Offset(x - 3.6f * u, h * 0.935f), Size(7.2f * u, h * 0.05f))
            drawOval(Color.White.copy(alpha = 0.12f), Offset(x - 2.6f * u, h * 0.94f), Size(2.2f * u, h * 0.025f))
            bronce(Rect(x - 1.6f * u, h * 0.975f, x + 1.6f * u, h * 0.995f), oscuro, 0.5f * u)
        }
        // El cuerpo de caoba de la vitrina.
        val cuerpo = Rect(w * 0.02f, h * 0.1f, w * 0.98f, h * 0.94f)
        caoba(cuerpo, oscuro, e.semilla)
        // El zócalo: moldura de luz, tablero con sus apliques (rosetas y laureles) y el filete dorado.
        val zoc = Rect(w * 0.01f, h * 0.835f, w * 0.99f, h * 0.94f)
        caoba(zoc, oscuro, e.semilla + 2, color = CAOBA_LUZ)
        drawRect(Color.White.copy(alpha = 0.15f), zoc.topLeft, Size(zoc.width, 0.8f * u))
        drawRect(t(ORO, oscuro), Offset(zoc.left, zoc.top + 0.8f * u), Size(zoc.width, 0.5f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zoc.left, zoc.bottom - 1f * u), Size(zoc.width, 1f * u))
        val tz = Rect(zoc.left + w * 0.06f, zoc.top + 2.4f * u, zoc.right - w * 0.06f, zoc.bottom - 2.2f * u)
        drawRect(t(CAOBA, oscuro), tz.topLeft, tz.size)
        drawRect(t(ORO, oscuro), tz.topLeft, tz.size, style = Stroke(0.35f * u))
        for (k in 0 until 5) {
            val cx = n.left + n.width * (k + 0.5f) / 5
            if (k == 2) {
                listOf(-1f, 1f).forEach { s -> for (j in 0 until 4) hoja(Offset(cx + s * (2.2f + j * 1.9f) * u, tz.center.y + (j * 0.25f) * u), 2.4f * u, 1.1f * u, s * (12f - j * 6f), t(if (j % 2 == 0) ORO else ORO_LUZ, oscuro)) }
                roseta(Offset(cx, tz.center.y), 1.6f * u, oscuro)
            } else roseta(Offset(cx, tz.center.y), 1.4f * u, oscuro)
        }
        // Las columnas de mármol verde a los lados, de la cornisa al zócalo.
        val cw = w * 0.042f
        listOf(w * 0.018f, w * 0.982f - cw).forEachIndexed { i, x -> columna(Rect(x, h * 0.16f, x + cw, h * 0.835f), oscuro, e.semilla + 10 + i, marmol = true) }
        // El nicho: cinco hornacinas de seda, cada una con su arco de sombra, su repisa y su columnilla entre ellas.
        drawRect(t(CAOBA_OSC, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(sedaNicho(oscuro), n.topLeft, n.size)
        val paso = n.width / 5
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k, n.top, n.left + paso * (k + 1), n.bottom)
            val arco = Path().apply {
                val r = c.width * 0.42f
                moveTo(c.center.x - r, c.bottom); lineTo(c.center.x - r, c.top + r + 2 * u)
                arcTo(Rect(c.center.x - r, c.top + 2 * u, c.center.x + r, c.top + 2 * u + 2 * r), 180f, 180f, false)
                lineTo(c.center.x + r, c.bottom); close()
            }
            drawPath(arco, (if (oscuro) Color.Black.copy(alpha = 0.18f) else ORO_OSC.copy(alpha = 0.08f)))
            drawPath(arco, t(ORO, oscuro).copy(alpha = if (oscuro) 0.35f else 0.45f), style = Stroke(0.35f * u))
            // La concha de la hornacina, tenue, en lo alto del arco.
            val cc = Offset(c.center.x, c.top + 2 * u + c.width * 0.42f)
            for (j in 0 until 7) {
                val a = Math.toRadians(180.0 + 15.0 + j * 25.0)
                drawLine(t(ORO, oscuro).copy(alpha = if (oscuro) 0.22f else 0.3f), cc, Offset(cc.x + c.width * 0.3f * cos(a).toFloat(), cc.y + c.width * 0.3f * sin(a).toFloat()), 0.35f * u)
            }
        }
        sombraInterior(n, 4 * u, if (oscuro) 0.35f else 0.14f)
        // La repisa de cada hornacina: un listón de caoba con su canto dorado.
        val rep = Rect(n.left, n.bottom - 2.2f * u, n.right, n.bottom)
        caoba(rep, oscuro, e.semilla + 4, color = CAOBA_LUZ)
        drawRect(t(ORO, oscuro), rep.topLeft, Size(rep.width, 0.4f * u))
        for (k in 1 until 5) {
            val x = n.left + paso * k
            columna(Rect(x - 2f * u, n.top, x + 2f * u, n.bottom), oscuro, e.semilla + 20 + k, marmol = false)
        }
        // La cornisa: el friso de caoba con palmetas y la corona de laurel, los dentículos y la moldura de arriba.
        val fr = Rect(w * 0.01f, h * 0.075f, w * 0.99f, h * 0.17f)
        caoba(fr, oscuro, e.semilla + 6)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(fr.left, fr.bottom - 0.8f * u), Size(fr.width, 0.8f * u))
        drawRect(t(ORO, oscuro), Offset(fr.left, fr.bottom - 1.6f * u), Size(fr.width, 0.5f * u))
        for (k in 0 until 5) {
            val cx = n.left + paso * (k + 0.5f)
            if (k == 2) {
                laurel(Offset(cx, fr.center.y - 0.4f * u), 3.6f * u, oscuro)
                abeja(Offset(cx, fr.center.y - 0.2f * u), 3.6f * u, oscuro)
            } else palmeta(Offset(cx, fr.bottom - 2.2f * u), 6f * u, oscuro)
            if (k < 4) roseta(Offset(n.left + paso * (k + 1), fr.center.y), 1.4f * u, oscuro)
        }
        listOf(fr.left + w * 0.04f, fr.right - w * 0.04f).forEach { roseta(Offset(it, fr.center.y), 1.8f * u, oscuro) }
        val den = Rect(0f, h * 0.045f, w, h * 0.075f)
        drawRect(t(CAOBA_OSC, oscuro), den.topLeft, den.size)
        var x = den.left + 0.6f * u
        while (x < den.right) { drawRect(t(CAOBA_LUZ, oscuro), Offset(x, den.top), Size(1.6f * u, den.height * 0.8f)); x += 2.8f * u }
        val tope = Rect(0f, 0f, w, h * 0.045f)
        caoba(tope, oscuro, e.semilla + 8, color = CAOBA_LUZ)
        drawRect(Color.White.copy(alpha = 0.2f), tope.topLeft, Size(w, 0.7f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tope.bottom - 0.7f * u), Size(w, 0.7f * u))
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f * 60f
        val c = Offset(w / 2, h / 2)
        val u = s / 60f
        when (id) {
            "unesco" -> {
                val cy = c.y + s * 0.05f
                // El arco de triunfo de piedra: el suelo, los machones, el gran arco, la cornisa, el ático y la bandera.
                val base = cy + s * 0.44f
                val aw = s * 1.15f
                val l = c.x - aw / 2; val r = c.x + aw / 2
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(l - 4 * u, base - 2 * u), Size(aw + 8 * u, 4 * u))
                val cuerpo = Rect(l, cy - s * 0.12f, r, base)
                drawRect(t(PIEDRA, oscuro), cuerpo.topLeft, cuerpo.size)
                drawRect(t(PIEDRA_OSC, oscuro), Offset(r - aw * 0.12f, cuerpo.top), Size(aw * 0.12f, cuerpo.height))
                for (k in 1 until 6) drawLine(t(PIEDRA_OSC, oscuro).copy(alpha = 0.45f), Offset(l, cuerpo.top + cuerpo.height * k / 6), Offset(r, cuerpo.top + cuerpo.height * k / 6), 0.4f * u)
                val va = Rect(c.x - aw * 0.17f, cuerpo.top + cuerpo.height * 0.2f, c.x + aw * 0.17f, base)
                val vano = Path().apply { moveTo(va.left, va.bottom); lineTo(va.left, va.top + va.width / 2); arcTo(Rect(va.left, va.top, va.right, va.top + va.width), 180f, 180f, false); lineTo(va.right, va.bottom); close() }
                drawPath(vano, t(Color(0xFF3A3328), oscuro))
                drawPath(vano, Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), va.left, va.right))
                drawPath(Path().apply { arcTo(Rect(va.left - 2 * u, va.top - 2 * u, va.right + 2 * u, va.top + va.width + 2 * u), 180f, 180f, false) }, t(PIEDRA_OSC, oscuro), style = Stroke(1.2f * u))
                drawRect(t(ORO, oscuro), Offset(c.x - 1.2f * u, va.top - 2.6f * u), Size(2.4f * u, 3 * u))
                listOf(-1f, 1f).forEach { sg ->
                    val px = c.x + sg * aw * 0.33f
                    // El relieve: el marco hundido y tres figuras en bulto.
                    val rl = Rect(px - aw * 0.08f, cuerpo.top + cuerpo.height * 0.16f, px + aw * 0.08f, cuerpo.top + cuerpo.height * 0.54f)
                    drawRect(t(PIEDRA_OSC, oscuro), rl.topLeft, rl.size)
                    drawRect(t(mezcla(PIEDRA, PIEDRA_OSC, 0.4f), oscuro), rl.deflate(0.8f * u).topLeft, rl.deflate(0.8f * u).size)
                    for (j in 0 until 3) {
                        val fx = rl.left + rl.width * (0.25f + 0.25f * j)
                        drawOval(t(PIEDRA, oscuro), Offset(fx - 1.1f * u, rl.top + rl.height * 0.3f), Size(2.2f * u, rl.height * 0.58f))
                        drawCircle(t(PIEDRA, oscuro), 1f * u, Offset(fx, rl.top + rl.height * 0.24f))
                    }
                    drawRect(t(PIEDRA_OSC, oscuro), Offset(px - aw * 0.05f, cuerpo.top + cuerpo.height * 0.66f), Size(aw * 0.1f, cuerpo.height * 0.34f))
                }
                val cor = Rect(l - 2 * u, cuerpo.top - 3.2f * u, r + 2 * u, cuerpo.top)
                drawRect(t(PIEDRA, oscuro), cor.topLeft, cor.size)
                drawRect(t(PIEDRA_OSC, oscuro), Offset(cor.left, cor.bottom - 1 * u), Size(cor.width, 1 * u))
                var x = cor.left + 1 * u
                while (x < cor.right - 1 * u) { drawRect(t(PIEDRA_OSC, oscuro), Offset(x, cor.top + 0.6f * u), Size(0.8f * u, 1.2f * u)); x += 2 * u }
                val at = Rect(l + 2 * u, cor.top - s * 0.17f, r - 2 * u, cor.top)
                drawRect(t(PIEDRA, oscuro), at.topLeft, at.size)
                drawRect(t(PIEDRA_OSC, oscuro), Offset(at.right - aw * 0.1f, at.top), Size(aw * 0.1f, at.height))
                drawRect(e.c2.copy(alpha = 0.75f), Offset(at.left + at.width * 0.18f, at.top + at.height * 0.3f), Size(at.width * 0.64f, at.height * 0.4f))
                drawRect(t(PIEDRA, oscuro), Offset(at.left - 1 * u, at.top - 1.4f * u), Size(at.width + 2 * u, 1.4f * u))
                // La bandera tricolor en su asta, encima.
                val ax = c.x
                drawLine(t(ORO_OSC, oscuro), Offset(ax, at.top - 1.4f * u), Offset(ax, at.top - s * 0.19f), 0.8f * u)
                val fy = at.top - s * 0.19f
                listOf(AZUL, BLANCO, CARMESI).forEachIndexed { i, col -> drawRect(t(col, oscuro), Offset(ax + 0.4f * u + i * 3.2f * u, fy + 0.6f * u), Size(3.2f * u, 6f * u)) }
                drawCircle(t(ORO, oscuro), 1 * u, Offset(ax, fy))
            }
            "rascar" -> {
                // El mapa de pergamino, con una franja raspada que deja ver el color, y el napoleón de oro que la raspa.
                val m = Rect(c.x - s * 0.62f, c.y - s * 0.4f, c.x + s * 0.5f, c.y + s * 0.4f)
                drawRect(Color.Black.copy(alpha = 0.25f), m.topLeft + Offset(1.2f * u, 1.6f * u), m.size)
                drawRect(t(PERGAMINO, oscuro), m.topLeft, m.size)
                drawRect(t(ORO_OSC, oscuro), m.deflate(2f * u).topLeft, m.deflate(2f * u).size, style = Stroke(0.6f * u))
                listOf(1f / 3, 2f / 3).forEach { f -> drawLine(t(PERGAMINO_OSC, oscuro), Offset(m.left + m.width * f, m.top), Offset(m.left + m.width * f, m.bottom), 0.8f * u) }
                // La capa por rascar (plateada con su trama) y lo ya raspado, en el color del esquema.
                val capa = m.deflate(4 * u)
                drawRect(t(Color(0xFFB8B2A2), oscuro), capa.topLeft, capa.size)
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    var k = capa.left - capa.height
                    while (k < capa.right) { drawLine(Color.White.copy(alpha = 0.25f), Offset(k, capa.bottom), Offset(k + capa.height, capa.top), 0.5f * u); k += 2.4f * u }
                    val raspa = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.35f); cubicTo(capa.left + capa.width * 0.3f, capa.top - capa.height * 0.1f, capa.left + capa.width * 0.5f, capa.bottom, capa.right * 1f, capa.top + capa.height * 0.55f) }
                    drawPath(raspa, e.fondo, style = Stroke(capa.height * 0.42f, cap = StrokeCap.Round))
                    drawPath(raspa, e.c1.copy(alpha = 0.85f), style = Stroke(capa.height * 0.24f, cap = StrokeCap.Round))
                }
                // El napoleón de oro: el canto, la cara con la corona de laurel y la estrella.
                val mc = Offset(m.right + s * 0.02f, m.bottom - s * 0.12f)
                val mr = s * 0.24f
                drawCircle(Color.Black.copy(alpha = 0.3f), mr, mc + Offset(1.2f * u, 1.6f * u))
                drawCircle(t(ORO_OSC, oscuro), mr, mc)
                drawCircle(t(ORO, oscuro), mr * 0.88f, mc)
                drawArc(t(ORO_LUZ, oscuro), 200f, 100f, false, Offset(mc.x - mr * 0.78f, mc.y - mr * 0.78f), Size(mr * 1.56f, mr * 1.56f), style = Stroke(mr * 0.1f))
                laurel(mc, mr * 0.58f, oscuro, hojas = 5)
                drawPath(estrella(mc, mr * 0.32f, 5, 0.45f), t(ORO_OSC, oscuro))
            }
            else -> {
                // Tus lugares: tres banderas con su águila clavadas en el mapa de campaña, unidas por la ruta de la marcha.
                val m = Rect(c.x - s * 0.95f, c.y + s * 0.08f, c.x + s * 0.95f, c.y + s * 0.46f)
                val mp = Path().apply { moveTo(m.left + 4 * u, m.top); lineTo(m.right, m.top + 1 * u); lineTo(m.right - 4 * u, m.bottom); lineTo(m.left, m.bottom - 1 * u); close() }
                drawPath(mp, Color.Black.copy(alpha = 0.22f), style = Stroke(2 * u))
                drawPath(mp, t(PERGAMINO, oscuro))
                drawPath(mp, t(PERGAMINO_OSC, oscuro), style = Stroke(0.6f * u))
                val pts = listOf(Offset(c.x - s * 0.62f, m.top + m.height * 0.6f), Offset(c.x, m.top + m.height * 0.35f), Offset(c.x + s * 0.62f, m.top + m.height * 0.62f))
                val ruta = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.3f, m.bottom, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.3f, m.top, pts[2].x, pts[2].y) }
                drawPath(ruta, e.c2, style = Stroke(1.2f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
                pts.forEachIndexed { i, p ->
                    val alto = s * (if (i == 1) 0.62f else 0.52f)
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 2 * u, p.y - 0.8f * u), Size(4 * u, 1.6f * u))
                    drawLine(t(CAOBA, oscuro), p, Offset(p.x, p.y - alto), 1.1f * u)
                    val fy = p.y - alto + 4 * u
                    val tela = Path().apply { moveTo(p.x, fy); quadraticTo(p.x + 5 * u, fy - 1.4f * u, p.x + 11 * u, fy + 0.6f * u); lineTo(p.x + 11 * u, fy + 9.6f * u); quadraticTo(p.x + 5 * u, fy + 8f * u, p.x, fy + 9 * u); close() }
                    clipPath(tela) {
                        listOf(AZUL, BLANCO, CARMESI).forEachIndexed { j, col -> drawRect(t(if (i == 1 && j == 0) AZUL_OSC else col, oscuro), Offset(p.x + j * 3.7f * u, fy - 2 * u), Size(3.8f * u, 13 * u)) }
                        drawRect(Color.Black.copy(alpha = 0.12f), Offset(p.x + 7 * u, fy - 2 * u), Size(4 * u, 13 * u))
                    }
                    drawPath(tela, t(ORO_OSC, oscuro), style = Stroke(0.4f * u))
                    // El águila de la asta: alas abiertas sobre el travesaño.
                    val ag = Offset(p.x, p.y - alto)
                    drawRect(t(ORO_OSC, oscuro), Offset(ag.x - 1.4f * u, ag.y), Size(2.8f * u, 0.9f * u))
                    drawPath(Path().apply { moveTo(ag.x, ag.y - 0.4f * u); quadraticTo(ag.x - 3.2f * u, ag.y - 3.2f * u, ag.x - 3.6f * u, ag.y - 0.6f * u); lineTo(ag.x - 1 * u, ag.y - 1.2f * u); lineTo(ag.x, ag.y - 3f * u); lineTo(ag.x + 1 * u, ag.y - 1.2f * u); lineTo(ag.x + 3.6f * u, ag.y - 0.6f * u); quadraticTo(ag.x + 3.2f * u, ag.y - 3.2f * u, ag.x, ag.y - 0.4f * u); close() }, t(ORO, oscuro))
                    drawCircle(t(ORO_LUZ, oscuro), 0.8f * u, Offset(ag.x, ag.y - 3.2f * u))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablero de despachos

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        // El marco de caoba con su moldura, la sombra del tablero y la repisa de abajo.
        val marco = Rect(w * 0.012f, h * 0.075f, w * 0.988f, h * 0.935f)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.25f)
        caoba(marco, oscuro, e.semilla)
        drawRect(Color.White.copy(alpha = 0.14f), marco.topLeft, Size(marco.width, 0.8f * u))
        drawRect(Color.White.copy(alpha = 0.1f), marco.topLeft, Size(0.8f * u, marco.height))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.right - 0.8f * u, marco.top), Size(0.8f * u, marco.height))
        val med = Rect((marco.left + n.left) / 2, (marco.top + n.top) / 2, (marco.right + n.right) / 2, (marco.bottom + n.bottom) / 2)
        drawRect(t(ORO, oscuro).copy(alpha = 0.8f), med.topLeft, med.size, style = Stroke(0.3f * u))
        // El marco dorado del nicho y la seda tensada, con su damasco de abejas muy suave.
        marcoDorado(n, 1.6f * u, oscuro)
        drawRect(sedaNicho(oscuro), n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            val tono = if (oscuro) Color.White.copy(alpha = 0.03f) else ORO_OSC.copy(alpha = 0.07f)
            var y = n.top + 4 * u; var f = 0
            while (y < n.bottom) {
                var x = n.left + (if (f % 2 == 0) 4f else 10f) * u
                while (x < n.right) { drawPath(estrella(Offset(x, y), 0.9f * u, 4, 0.35f), tono); x += 12 * u }
                y += 7 * u; f++
            }
        }
        sombraInterior(n, 3 * u, if (oscuro) 0.3f else 0.12f)
        // Las rosetas de las esquinas del marco.
        listOf(Offset(med.left, med.top), Offset(med.right, med.top), Offset(med.left, med.bottom), Offset(med.right, med.bottom)).forEach { roseta(it, 2f * u, oscuro) }
        // La repisa con la pluma y la barra de lacre.
        val rep = Rect(w * 0.005f, h * 0.935f, w * 0.995f, h * 0.975f)
        caoba(rep, oscuro, e.semilla + 2, color = CAOBA_LUZ)
        drawRect(Color.White.copy(alpha = 0.2f), rep.topLeft, Size(rep.width, 0.6f * u))
        drawRect(t(ORO, oscuro), Offset(rep.left, rep.bottom - 0.9f * u), Size(rep.width, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 0.6f * u))
        val py = rep.top - 0.2f * u
        drawPath(Path().apply { moveTo(w * 0.62f, py); quadraticTo(w * 0.72f, py - 3.6f * u, w * 0.82f, py - 2.6f * u); quadraticTo(w * 0.73f, py - 0.8f * u, w * 0.62f, py); close() }, t(BLANCO, oscuro))
        drawLine(t(PIEDRA_OSC, oscuro), Offset(w * 0.62f, py), Offset(w * 0.82f, py - 2.6f * u), 0.3f * u)
        drawRoundRect(t(LACRE, oscuro), Offset(w * 0.24f, py - 1.8f * u), Size(w * 0.1f, 1.8f * u), CornerRadius(0.5f * u))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(w * 0.245f, py - 1.6f * u), Size(w * 0.09f, 0.4f * u))
        // El copete: la tabla curva con la corona de laurel, la abeja y dos palmetas.
        val cc = Offset(w / 2, h * 0.07f)
        val cop = Path().apply { moveTo(w * 0.3f, marco.top + 0.5f * u); quadraticTo(w * 0.36f, h * 0.01f, w / 2, h * 0.005f); quadraticTo(w * 0.64f, h * 0.01f, w * 0.7f, marco.top + 0.5f * u); close() }
        drawPath(cop, t(CAOBA, oscuro))
        clipPath(cop) { vetas(Rect(w * 0.3f, 0f, w * 0.7f, marco.top), t(CAOBA_VETA, oscuro).copy(alpha = 0.5f), e.semilla + 3, densidad = 0.8f, grosor = 0.7f) }
        drawPath(cop, t(ORO, oscuro), style = Stroke(0.5f * u))
        laurel(cc, 4.6f * u, oscuro)
        abeja(Offset(cc.x, cc.y + 0.3f * u), 4.8f * u, oscuro)
        listOf(-1f, 1f).forEach { s -> palmeta(Offset(cc.x + s * w * 0.12f, marco.top - 0.2f * u), 4.6f * u, oscuro) }
    }

    /** El pergamino del despacho: claro de día; de noche, a la luz de la vela, más pardo (la tinta se lee en los dos). */
    private fun papel(oscuro: Boolean) = if (oscuro) Color(0xFFCDBF9C) else Color(0xFFF5ECD4)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val u = r.width / 100f
        val p = Rect(r.left + 3 * u, r.top + 4 * u, r.right - 3 * u, r.bottom - 2 * u)
        // La sombra, el pergamino (un poco más oscuro en los bordes) y sus pliegues de despacho.
        sombra(p, 3 * u, if (oscuro) 0.5f else 0.28f)
        drawRect(papel(oscuro), p.topLeft, p.size)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, PERGAMINO_OSC.copy(alpha = 0.45f)), p.top + p.height * 0.6f, p.bottom), Offset(p.left, p.top + p.height * 0.6f), Size(p.width, p.height * 0.4f))
        listOf(1f / 3, 2f / 3).forEach { f ->
            val y = p.top + p.height * f
            drawLine(Color.Black.copy(alpha = 0.07f), Offset(p.left, y), Offset(p.right, y), 0.8f * u)
            drawLine(Color.White.copy(alpha = 0.3f), Offset(p.left, y + 0.8f * u), Offset(p.right, y + 0.8f * u), 0.5f * u)
        }
        // El doble filete azul de la cancillería, en el margen.
        val f1 = p.deflate(4 * u)
        drawRect(AZUL.copy(alpha = 0.55f), f1.topLeft, f1.size, style = Stroke(0.9f * u))
        drawRect(AZUL.copy(alpha = 0.35f), f1.deflate(1.8f * u).topLeft, f1.deflate(1.8f * u).size, style = Stroke(0.5f * u))
        // El detalle del tipo, abajo a la derecha (fuera del texto).
        val d = Offset(p.right - 9.5f * u, p.bottom - 9.5f * u)
        when (e.tipo) {
            "idea" -> scale(1.5f, d) {
                // La estrella de la Legión de Honor, de esmalte blanco con su cinta roja.
                drawRect(CARMESI, Offset(d.x - 1.6f * u, d.y - 7 * u), Size(3.2f * u, 4 * u))
                drawPath(estrella(d, 4.4f * u, 5, 0.45f), ORO_OSC)
                drawPath(estrella(d, 3.6f * u, 5, 0.45f), Color.White)
                drawCircle(ORO, 1.2f * u, d)
            }
            "pensamiento" -> scale(1.5f, d) {
                // Una ramita de laurel.
                for (k in 0 until 4) {
                    hoja(Offset(d.x - 3 * u + k * 2 * u, d.y + 1.2f * u - k * 1.4f * u), 3.6f * u, 1.6f * u, -60f, Color(0xFF4E6B3A))
                    hoja(Offset(d.x - 1.4f * u + k * 2 * u, d.y + 2.6f * u - k * 1.4f * u), 3.6f * u, 1.6f * u, 20f, Color(0xFF6B8A4E))
                }
                drawLine(Color(0xFF4E6B3A), Offset(d.x - 4 * u, d.y + 4 * u), Offset(d.x + 4 * u, d.y - 3 * u), 0.5f * u)
            }
            "proposito" -> scale(1.5f, d) {
                // La escarapela tricolor.
                drawCircle(Color.Black.copy(alpha = 0.2f), 4.4f * u, d + Offset(0.4f * u, 0.6f * u))
                drawCircle(AZUL, 4.4f * u, d)
                drawCircle(Color.White, 3f * u, d)
                drawCircle(CARMESI, 1.6f * u, d)
                for (k in 0 until 12) { val a = k * Math.PI / 6; drawLine(Color.Black.copy(alpha = 0.15f), d + Offset(3.2f * u * cos(a).toFloat(), 3.2f * u * sin(a).toFloat()), d + Offset(4.3f * u * cos(a).toFloat(), 4.3f * u * sin(a).toFloat()), 0.3f * u) }
            }
            else -> {
                // El recordatorio: la esquina doblada.
                val k = 9 * u
                drawPath(Path().apply { moveTo(p.right - k, p.bottom); lineTo(p.right, p.bottom - k); lineTo(p.right, p.bottom); close() }, sedaNicho(oscuro))
                drawPath(Path().apply { moveTo(p.right - k, p.bottom); lineTo(p.right, p.bottom - k); lineTo(p.right - k, p.bottom - k); close() }, PERGAMINO_OSC)
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(p.right - k, p.bottom), Offset(p.right, p.bottom - k), 0.5f * u)
            }
        }
        // Arriba al centro: el sello de lacre con sus cintas si está fijada; si no, el alfiler de latón.
        val s = Offset(p.center.x, p.top + 1.5f * u)
        if (e.fijada) {
            listOf(-1f, 1f).forEach { k ->
                val cinta = Path().apply { moveTo(s.x, s.y); lineTo(s.x + k * 13 * u, s.y + 7 * u); lineTo(s.x + k * 12 * u, s.y + 10 * u); lineTo(s.x + k * 10 * u, s.y + 7.6f * u); lineTo(s.x + k * 2.5f * u, s.y + 4 * u); close() }
                drawPath(cinta, if (k < 0) AZUL else CARMESI)
                drawPath(cinta, Color.Black.copy(alpha = 0.2f), style = Stroke(0.3f * u))
            }
            drawCircle(Color.Black.copy(alpha = 0.3f), 7 * u, s + Offset(0.6f * u, 1f * u))
            drawPath(estrella(s, 7.4f * u, 11, 0.86f), LACRE)
            drawCircle(mezcla(LACRE, Color.Black, 0.25f), 5 * u, s)
            drawCircle(mezcla(LACRE, Color.White, 0.12f), 4.4f * u, s, style = Stroke(0.4f * u))
            abeja(Offset(s.x, s.y + 0.6f * u), 6f * u, false, cuerpo = mezcla(LACRE, Color.White, 0.3f), alas = mezcla(LACRE, Color.White, 0.18f))
            drawArc(Color.White.copy(alpha = 0.25f), 200f, 70f, false, Offset(s.x - 6 * u, s.y - 6 * u), Size(12 * u, 12 * u), style = Stroke(0.8f * u))
        } else {
            drawLine(Color.Black.copy(alpha = 0.25f), s + Offset(0.5f * u, 0.5f * u), s + Offset(3f * u, 4.5f * u), 0.8f * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 3 * u, s + Offset(0.8f * u, 1.1f * u))
            drawCircle(ORO_OSC, 3 * u, s)
            drawCircle(ORO, 2.4f * u, s)
            drawCircle(ORO_LUZ, 0.9f * u, s - Offset(0.8f * u, 0.8f * u))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
