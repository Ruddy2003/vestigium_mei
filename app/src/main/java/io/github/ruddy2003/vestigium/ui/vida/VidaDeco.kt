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
 * Art déco: los muebles de Vida y de Diario de los años veinte, de la misma casa que su casillero de laca de la recepción del
 * hotel (Ideario), su estante de transatlántico y su salón de las ovaciones (Hall): laca negra con filetes de oro, ébano de
 * Macasar veteado, galuchat marfil, jade, coral, cromo; abanicos de rayos, remates escalonados, chevrones y las tres líneas de
 * la velocidad.
 * - Social: el salón de primera de un trasatlántico: la pared de ébano de Macasar, el remate escalonado de laca con el sol
 *   naciente de oro, las pilastras de laca estriadas de oro con capitel y basa escalonados y su aplique de abanico de vidrio
 *   (de noche, encendido), el gran panel de galuchat (de noche, de laca) en su marco de oro con esquinas escalonadas, dos
 *   palmeras en sus jardineras de laca y el zócalo con chevrones sobre el piso de mármol en damero.
 * - Mapas: el escritorio del camarote: el tablero de Macasar con su filete de latón, la carta marfil con cantoneras de cromo,
 *   la lámpara de cromo con su pantalla de jade y la pluma estilográfica a un lado, el globo terráqueo sobre su pie escalonado
 *   y el compás al otro; el canto de laca con las tres líneas de cromo, el faldón con su cajón y su tirador de abanico, y las
 *   patas escalonadas con zapata de latón.
 * - Otras vidas: la vitrina rascacielos: la corona en tres escalones con el abanico de rayos, cinco hornacinas de remate
 *   escalonado forradas de galuchat (de noche, de laca) con un sol tenue detrás de cada medalla, las pilastras de laca, el
 *   zócalo con las líneas de cromo y los chevrones, y las patas escalonadas.
 * - Íconos: el rascacielos con su corona de arcos y su aguja; la placa de laca raspada por una moneda de oro; tres alfileres
 *   escalonados unidos por la ruta del barco.
 * - Notas: el tablero de avisos del vestíbulo: el marco de laca con sus esquinas escalonadas y su filete de oro, el abanico
 *   arriba, la seda tensada y la repisa de cromo con la estilográfica y el timbre; las notas son telegramas con su franja de
 *   jade y su filete de oro, sus esquinas escalonadas, una chincheta de cromo o, si están fijadas, un broche de abanico de oro,
 *   y un detalle según el tipo (el sol, los chevrones, el zigurat o la esquina doblada).
 */
internal object VidaDeco : DisenoVida {
    // ---------------------------------------------------------------- materiales (los de su Ideario y su Hall)
    private val LACA = Color(0xFF161310)
    private val LACA_LUZ = Color(0xFF2E2820)
    private val LACA_BRILLO = Color(0xFF5A5246)
    private val ORO = Color(0xFFC9A24C)
    private val ORO_LUZ = Color(0xFFE8CF8A)
    private val ORO_OSC = Color(0xFF8A6A12)
    private val JADE = Color(0xFF1F6B57)
    private val JADE_LUZ = Color(0xFF4FA88C)
    private val JADE_OSC = Color(0xFF0F4234)
    private val MACASAR = Color(0xFF5C3A20)
    private val MACASAR_VETA = Color(0xFF1E120A)
    private val MARFIL = Color(0xFFF1E8D2)
    private val MARFIL_OSC = Color(0xFFCFC2A2)
    private val CROMO = Color(0xFFC9CDD2)
    private val CROMO_LUZ = Color(0xFFF4F6F8)
    private val CROMO_OSC = Color(0xFF7D838B)
    private val CORAL = Color(0xFFC8553D)
    private val GRANATE = Color(0xFF7A1E2A)
    private val TELEGRAMA = Color(0xFFF4E9C2)
    private val TINTA = Color(0xFF16130E)
    private val PALMA = Color(0xFF2F6B4A)
    private val PALMA_OSC = Color(0xFF1C4A32)
    private val LUZ = Color(0xFFFFE2A0)
    private val NOCHE = Color(0xFF0C0C0E)

    /** El forro del nicho: galuchat marfil de día; laca de noche (de la luminancia de la tarjeta, para que el contenido se lea). */
    private fun forro(oscuro: Boolean) = if (oscuro) Color(0xFF1D1A15) else Color(0xFFF5EDDA)

    /** El color en oscuro: más hundido, como en su biblioteca y su Hall. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.32f) else c

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.12f, 0.2f, 0.88f, 0.82f)
        MuebleVida.MAPAS -> Rect(0.17f, 0.085f, 0.83f, 0.715f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.22f, 0.935f, 0.8f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.15f, 0.93f, 0.87f)
    }

    private fun DrawScope.nicho(m: MuebleVida): Rect {
        val a = area(m)
        return Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    }

    // ---------------------------------------------------------------- ayudas de dibujo

    /** Un número estable de 0 a 1 para lo que varía (vetas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El ébano de Macasar en [r]: el pardo con sus vetas negras largas, recortadas a la pieza. */
    private fun DrawScope.macasar(r: Rect, oscuro: Boolean, semilla: Int, vertical: Boolean = false) {
        drawRect(t(MACASAR, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            val n = (ancho / (2.6f * density)).toInt().coerceIn(3, 70)
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = (0.3f + 1.1f * azar(semilla, k + 100)) * density
                val fase = azar(semilla, k + 200) * 6.28f
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16
                    val v = base + amp * sin(fase + 1.4f * 6.28f * s / 16)
                    val x = if (vertical) r.left + v else r.left + a
                    val y = if (vertical) r.top + a else r.top + v
                    if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                val g = azar(semilla, k + 300)
                drawPath(p, t(MACASAR_VETA, oscuro).copy(alpha = 0.35f + 0.6f * g), style = Stroke((0.3f + 1.5f * g * g) * density))
            }
        }
    }

    /** Una pieza de laca en [r]: el negro, su reflejo arriba y su filete de oro. */
    private fun DrawScope.laca(r: Rect, oscuro: Boolean, filete: Boolean = true) {
        drawRect(t(LACA, oscuro), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(t(LACA_BRILLO, oscuro).copy(alpha = 0.55f), Color.Transparent), r.top, r.top + r.height * 0.5f), r.topLeft, Size(r.width, r.height * 0.5f))
        if (filete) drawLine(t(ORO, oscuro), r.topLeft, Offset(r.right, r.top), max(0.6f * density, r.height * 0.04f))
    }

    /** Una pieza de metal en [r] (oro o cromo): el tono, su luz y su sombra. */
    private fun DrawScope.metal(r: Rect, oscuro: Boolean, cromo: Boolean = false, radio: Float = 0f) {
        val (b, l, o) = if (cromo) Triple(CROMO, CROMO_LUZ, CROMO_OSC) else Triple(ORO, ORO_LUZ, ORO_OSC)
        drawRoundRect(t(o, oscuro), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(t(b, oscuro), r.topLeft, Size(r.width, r.height * 0.78f), CornerRadius(radio))
        drawRoundRect(t(l, oscuro), Offset(r.left + r.width * 0.04f, r.top + r.height * 0.12f), Size(r.width * 0.92f, r.height * 0.2f), CornerRadius(radio))
    }

    /** Una sombra suave debajo de algo. */
    private fun DrawScope.sombra(r: Rect, alcance: Float, fuerza: Float) {
        for (k in 4 downTo 1) {
            val e = alcance * k / 4f
            drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.5f), Size(r.width + e * 0.6f, r.height + e * 0.4f), CornerRadius(e))
        }
    }

    /** La sombra de dentro de un hueco. */
    private fun DrawScope.sombraInterior(r: Rect, ancho: Float, fuerza: Float) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = fuerza), Color.Transparent), r.top, r.top + ancho), r.topLeft, Size(r.width, ancho))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = fuerza * 0.6f), Color.Transparent), r.left, r.left + ancho * 0.7f), r.topLeft, Size(ancho * 0.7f, r.height))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = fuerza * 0.6f)), r.right - ancho * 0.7f, r.right), Offset(r.right - ancho * 0.7f, r.top), Size(ancho * 0.7f, r.height))
    }

    /** Un halo de luz (las lámparas encendidas). */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** El abanico de rayos: medio sol de rayos alternados que nace de [c], con su ojo de laca y su aro de oro. */
    private fun DrawScope.abanico(c: Offset, r: Float, oscuro: Boolean, rayos: Int = 14, ojo: Color = LACA) {
        for (k in 0 until rayos) drawArc(t(if (k % 2 == 0) ORO else ORO_OSC, oscuro), 180f + k * 180f / rayos, 180f / rayos, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        drawArc(t(ORO_LUZ, oscuro), 180f, 180f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.05f))
        drawArc(t(ojo, oscuro), 180f, 180f, true, Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.6f))
        drawArc(t(ORO_LUZ, oscuro), 180f, 180f, false, Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.6f), style = Stroke(r * 0.04f))
    }

    /** Un rectángulo de esquinas escalonadas (dos escalones de [e]). */
    private fun escalonado(r: Rect, e: Float): Path = Path().apply {
        val a = e; val b = e * 2
        moveTo(r.left + b, r.top); lineTo(r.right - b, r.top); lineTo(r.right - b, r.top + a); lineTo(r.right - a, r.top + a); lineTo(r.right - a, r.top + b); lineTo(r.right, r.top + b)
        lineTo(r.right, r.bottom - b); lineTo(r.right - a, r.bottom - b); lineTo(r.right - a, r.bottom - a); lineTo(r.right - b, r.bottom - a); lineTo(r.right - b, r.bottom)
        lineTo(r.left + b, r.bottom); lineTo(r.left + b, r.bottom - a); lineTo(r.left + a, r.bottom - a); lineTo(r.left + a, r.bottom - b); lineTo(r.left, r.bottom - b)
        lineTo(r.left, r.top + b); lineTo(r.left + a, r.top + b); lineTo(r.left + a, r.top + a); lineTo(r.left + b, r.top + a); close()
    }

    /** Una fila de chevrones dorados entre [y0] y [y1]. */
    private fun DrawScope.chevrones(x0: Float, x1: Float, y0: Float, y1: Float, color: Color, grosor: Float) {
        val alto = y1 - y0
        var x = x0
        while (x < x1) {
            drawPath(Path().apply { moveTo(x, y1); lineTo(x + alto * 0.6f, y0); lineTo(x + alto * 1.2f, y1) }, color, style = Stroke(grosor))
            x += alto * 1.2f
        }
    }

    /** Las tres líneas de la velocidad, de cromo, a lo largo de [y]. */
    private fun DrawScope.velocidad(x0: Float, x1: Float, y: Float, paso: Float, oscuro: Boolean) {
        for (k in 0 until 3) {
            val yy = y + k * paso
            drawLine(t(CROMO_OSC, oscuro), Offset(x0, yy + paso * 0.18f), Offset(x1, yy + paso * 0.18f), paso * 0.4f)
            drawLine(t(CROMO, oscuro), Offset(x0, yy), Offset(x1, yy), paso * 0.32f)
        }
    }

    /** El galuchat del forro: una piel de puntitos muy suaves en [r]. */
    private fun DrawScope.galuchat(r: Rect, oscuro: Boolean, paso: Float) {
        val c = if (oscuro) Color.White.copy(alpha = 0.03f) else MARFIL_OSC.copy(alpha = 0.28f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + paso * 0.5f; var f = 0
            while (y < r.bottom) {
                var x = r.left + (if (f % 2 == 0) paso * 0.5f else paso)
                while (x < r.right) { drawCircle(c, paso * 0.22f, Offset(x, y)); x += paso }
                y += paso * 0.86f; f++
            }
        }
    }

    /** Un sol tenue de rayos finos que nace de [c] (detrás de las medallas y en el forro). */
    private fun DrawScope.solTenue(c: Offset, r: Float, color: Color, rayos: Int = 24) {
        for (k in 0 until rayos) {
            val a = Math.toRadians(k * 360.0 / rayos)
            drawLine(color, Offset(c.x + r * 0.35f * cos(a).toFloat(), c.y + r * 0.35f * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), if (k % 2 == 0) r * 0.025f else r * 0.012f)
        }
    }

    /**
     * Una pilastra de laca en [r]: el fuste con sus estrías de oro, el capitel y la basa escalonados (tres escalones de oro y
     * laca).
     */
    private fun DrawScope.pilastra(r: Rect, oscuro: Boolean) {
        val w = r.width
        val cap = w * 0.9f
        val fuste = Rect(r.left + w * 0.14f, r.top + cap, r.right - w * 0.14f, r.bottom - cap)
        laca(fuste, oscuro, filete = false)
        drawRect(t(LACA_LUZ, oscuro), fuste.topLeft, Size(fuste.width * 0.25f, fuste.height))
        val n = if (fuste.width > 9 * density) 4 else 3
        for (k in 1..n) {
            val x = fuste.left + fuste.width * k / (n + 1)
            drawLine(t(ORO, oscuro), Offset(x, fuste.top + cap * 0.3f), Offset(x, fuste.bottom - cap * 0.3f), max(0.45f * density, fuste.width * 0.05f))
        }
        // El capitel y la basa: tres escalones, el más ancho afuera.
        for (k in 0 until 3) {
            val ar = Rect(r.left + w * 0.14f - w * 0.07f * k, r.top + cap * (0.66f - 0.33f * k), r.right - w * 0.14f + w * 0.07f * k, r.top + cap * (1f - 0.33f * k))
            if (k % 2 == 0) metal(ar, oscuro) else laca(ar, oscuro)
            val ab = Rect(ar.left, r.bottom - (ar.bottom - r.top), ar.right, r.bottom - (ar.top - r.top))
            if (k % 2 == 0) metal(ab, oscuro) else laca(ab, oscuro)
        }
    }

    /** El aplique de pared: el abanico de vidrio esmerilado con su montura de oro, colgado en [c] (de noche, encendido). */
    private fun DrawScope.aplique(c: Offset, r: Float, oscuro: Boolean) {
        val vidrio = if (oscuro) Color(0xFFFFF0C8) else Color(0xFFF2EAD6)
        drawRect(t(ORO_OSC, oscuro), Offset(c.x - r * 0.18f, c.y), Size(r * 0.36f, r * 0.5f))
        drawRect(t(ORO, oscuro), Offset(c.x - r * 0.12f, c.y), Size(r * 0.24f, r * 0.42f))
        drawArc(vidrio, 180f, 180f, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        for (k in 1 until 6) {
            val a = Math.toRadians(180.0 + k * 30.0)
            drawLine(t(ORO_OSC, oscuro).copy(alpha = 0.7f), c, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), r * 0.05f)
        }
        drawArc(t(ORO, oscuro), 180f, 180f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(r * 0.1f))
        drawLine(t(ORO, oscuro), Offset(c.x - r * 1.05f, c.y), Offset(c.x + r * 1.05f, c.y), r * 0.12f)
        if (!oscuro) drawArc(Color.White.copy(alpha = 0.6f), 200f, 50f, false, Offset(c.x - r * 0.75f, c.y - r * 0.75f), Size(r * 1.5f, r * 1.5f), style = Stroke(r * 0.08f))
    }

    /** Una palmera en su jardinera de laca: las hojas en abanico y la maceta escalonada, apoyada en [pie]. */
    private fun DrawScope.palmera(pie: Offset, alto: Float, oscuro: Boolean) {
        val m = alto * 0.3f
        sombra(Rect(pie.x - m * 0.5f, pie.y - m * 0.08f, pie.x + m * 0.5f, pie.y), m * 0.12f, 0.35f)
        val copa = Offset(pie.x, pie.y - alto * 0.55f)
        drawLine(t(Color(0xFF6B5A3A), oscuro), Offset(pie.x, pie.y - m), copa, alto * 0.025f)
        val hojas = listOf(-150f, -125f, -100f, -80f, -55f, -30f, -165f, -15f)
        hojas.forEachIndexed { i, g ->
            val l = alto * (if (i >= 6) 0.36f else 0.46f)
            rotate(g + 90f, copa) {
                val p = Path().apply { moveTo(copa.x, copa.y); quadraticTo(copa.x + l * 0.16f, copa.y - l * 0.5f, copa.x, copa.y - l); quadraticTo(copa.x - l * 0.16f, copa.y - l * 0.5f, copa.x, copa.y); close() }
                drawPath(p, t(if (i % 2 == 0) PALMA else PALMA_OSC, oscuro))
                drawLine(t(PALMA_OSC, oscuro), copa, Offset(copa.x, copa.y - l * 0.96f), alto * 0.008f)
            }
        }
        // La jardinera: el vaso de laca con su banda de oro y su pie escalonado.
        val v = Path().apply { moveTo(pie.x - m * 0.5f, pie.y - m); lineTo(pie.x + m * 0.5f, pie.y - m); lineTo(pie.x + m * 0.36f, pie.y - m * 0.18f); lineTo(pie.x - m * 0.36f, pie.y - m * 0.18f); close() }
        drawPath(v, t(LACA, oscuro))
        drawPath(Path().apply { moveTo(pie.x - m * 0.5f, pie.y - m); lineTo(pie.x - m * 0.2f, pie.y - m); lineTo(pie.x - m * 0.14f, pie.y - m * 0.18f); lineTo(pie.x - m * 0.36f, pie.y - m * 0.18f); close() }, t(LACA_BRILLO, oscuro).copy(alpha = 0.6f))
        metal(Rect(pie.x - m * 0.52f, pie.y - m * 1.06f, pie.x + m * 0.52f, pie.y - m * 0.94f), oscuro)
        metal(Rect(pie.x - m * 0.44f, pie.y - m * 0.68f, pie.x + m * 0.44f, pie.y - m * 0.6f), oscuro)
        metal(Rect(pie.x - m * 0.36f, pie.y - m * 0.18f, pie.x + m * 0.36f, pie.y - m * 0.08f), oscuro)
        laca(Rect(pie.x - m * 0.44f, pie.y - m * 0.08f, pie.x + m * 0.44f, pie.y), oscuro)
    }

    // ---------------------------------------------------------------- Social: el salón del trasatlántico

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.SOCIAL)
        // La pared de ébano de Macasar, en paneles con su junta de latón.
        val np = max(6, (w / (22 * u)).roundToInt())
        for (k in 0 until np) {
            val r = Rect(w * k / np, 0f, w * (k + 1) / np, h)
            macasar(r, oscuro, e.semilla + k, vertical = true)
            drawLine(t(ORO_OSC, oscuro), Offset(r.left, 0f), Offset(r.left, h), 0.4f * u)
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f))
        // De noche, los apliques encendidos dan su halo a la pared.
        val pil = w * 0.055f
        val xs = listOf(w * 0.02f, w * 0.98f - pil)
        val ay = h * 0.42f
        if (oscuro) xs.forEach { x -> halo(Offset(x + pil / 2, ay - 2 * u), 24 * u, LUZ.copy(alpha = 0.35f)) }
        // El zócalo de laca con chevrones y el piso de mármol en damero.
        val zoc = Rect(0f, h * 0.875f, w, h * 0.94f)
        laca(zoc, oscuro)
        chevrones(0f, w, zoc.top + zoc.height * 0.3f, zoc.bottom - zoc.height * 0.2f, t(ORO, oscuro).copy(alpha = 0.8f), 0.4f * u)
        val piso = Rect(0f, h * 0.94f, w, h)
        drawRect(t(MARFIL, oscuro), piso.topLeft, piso.size)
        val c = piso.height / 2
        var x = 0f; var k = 0
        while (x < w) { drawRect(t(LACA, oscuro), Offset(x, piso.top + (k % 2) * c), Size(c, c)); x += c; k++ }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), piso.top, piso.bottom), piso.topLeft, piso.size)
        // Las pilastras de laca y sus apliques.
        xs.forEach { xx ->
            pilastra(Rect(xx, h * 0.15f, xx + pil, h * 0.875f), oscuro)
            aplique(Offset(xx + pil / 2, ay), pil * 0.8f, oscuro)
        }
        // Las palmeras en sus jardineras, delante de las pilastras.
        palmera(Offset(w * 0.06f, h * 0.975f), h * 0.36f, oscuro)
        palmera(Offset(w * 0.94f, h * 0.975f), h * 0.36f, oscuro)
        // El remate escalonado de laca con sus filetes y el sol naciente de oro.
        val r1 = Rect(0f, h * 0.1f, w, h * 0.155f)
        val r2 = Rect(w * 0.2f, h * 0.05f, w * 0.8f, h * 0.1f)
        val r3 = Rect(w * 0.34f, 0f, w * 0.66f, h * 0.05f)
        listOf(r1, r2, r3).forEach { laca(it, oscuro) }
        drawRect(t(ORO, oscuro), Offset(0f, r1.bottom - 0.9f * u), Size(w, 0.6f * u))
        velocidad(r1.left + w * 0.04f, r2.left - w * 0.03f, r1.top + r1.height * 0.3f, 1.6f * u, oscuro)
        velocidad(r2.right + w * 0.03f, r1.right - w * 0.04f, r1.top + r1.height * 0.3f, 1.6f * u, oscuro)
        abanico(Offset(w / 2, r1.top + 0.3f * u), h * 0.095f, oscuro, rayos = 16)
        // El gran panel de galuchat en su marco de oro con esquinas escalonadas (el nicho del grafo).
        sombra(n.inflate(2 * u), 2.5f * u, if (oscuro) 0.5f else 0.35f)
        val marco = n.inflate(2.6f * u)
        drawPath(escalonado(marco.inflate(1.6f * u), 2.2f * u), t(LACA, oscuro))
        drawPath(escalonado(marco.inflate(1.6f * u), 2.2f * u), t(ORO, oscuro), style = Stroke(0.7f * u))
        drawRect(t(ORO_OSC, oscuro), marco.topLeft, marco.size)
        drawRect(t(ORO, oscuro), marco.deflate(0.5f * u).topLeft, marco.deflate(0.5f * u).size)
        drawRect(t(ORO_LUZ, oscuro), marco.deflate(0.8f * u).topLeft, Size(marco.width - 1.6f * u, 0.5f * u))
        drawRect(forro(oscuro), n.topLeft, n.size)
        galuchat(n, oscuro, 2.2f * u)
        clipRect(n.left, n.top, n.right, n.bottom) { solTenue(Offset(n.center.x, n.bottom), n.height * 1.1f, (if (oscuro) ORO.copy(alpha = 0.045f) else ORO_OSC.copy(alpha = 0.05f)), rayos = 36) }
        sombraInterior(n, 3 * u, if (oscuro) 0.35f else 0.12f)
    }

    // ---------------------------------------------------------------- Mapas: el escritorio del camarote

    /** La lámpara de escritorio: el pie escalonado de cromo, el tallo y la pantalla de jade en abanico; la base en [p]. */
    private fun DrawScope.lampara(p: Offset, a: Float, oscuro: Boolean) {
        val pc = Offset(p.x, p.y - a * 0.72f)
        if (oscuro) halo(Offset(pc.x, pc.y + a * 0.1f), a * 0.9f, LUZ.copy(alpha = 0.4f))
        sombra(Rect(p.x - a * 0.22f, p.y - a * 0.03f, p.x + a * 0.22f, p.y), a * 0.05f, 0.35f)
        metal(Rect(p.x - a * 0.22f, p.y - a * 0.05f, p.x + a * 0.22f, p.y), oscuro, cromo = true)
        metal(Rect(p.x - a * 0.15f, p.y - a * 0.1f, p.x + a * 0.15f, p.y - a * 0.05f), oscuro, cromo = true)
        metal(Rect(p.x - a * 0.08f, p.y - a * 0.14f, p.x + a * 0.08f, p.y - a * 0.1f), oscuro, cromo = true)
        drawRect(t(CROMO_OSC, oscuro), Offset(p.x - a * 0.025f, pc.y), Size(a * 0.05f, p.y - a * 0.14f - pc.y))
        drawRect(t(CROMO_LUZ, oscuro), Offset(p.x - a * 0.02f, pc.y), Size(a * 0.015f, p.y - a * 0.14f - pc.y))
        // La pantalla: el abanico de vidrio de jade con sus nervios.
        val r = a * 0.3f
        val pant = Path().apply { moveTo(pc.x - r, pc.y + r * 0.2f); lineTo(pc.x - r * 0.3f, pc.y - r * 0.6f); lineTo(pc.x + r * 0.3f, pc.y - r * 0.6f); lineTo(pc.x + r, pc.y + r * 0.2f); close() }
        drawPath(pant, t(JADE, oscuro))
        drawPath(Path().apply { moveTo(pc.x - r, pc.y + r * 0.2f); lineTo(pc.x - r * 0.3f, pc.y - r * 0.6f); lineTo(pc.x - r * 0.05f, pc.y - r * 0.6f); lineTo(pc.x - r * 0.45f, pc.y + r * 0.2f); close() }, t(JADE_LUZ, oscuro))
        for (k in -1..1) drawLine(t(JADE_OSC, oscuro), Offset(pc.x + k * r * 0.15f, pc.y - r * 0.6f), Offset(pc.x + k * r * 0.55f, pc.y + r * 0.2f), a * 0.012f)
        metal(Rect(pc.x - r * 1.05f, pc.y + r * 0.18f, pc.x + r * 1.05f, pc.y + r * 0.3f), oscuro, cromo = true)
        metal(Rect(pc.x - r * 0.34f, pc.y - r * 0.7f, pc.x + r * 0.34f, pc.y - r * 0.58f), oscuro, cromo = true)
        if (oscuro) drawArc(LUZ.copy(alpha = 0.7f), 0f, 180f, true, Offset(pc.x - r * 0.8f, pc.y + r * 0.05f), Size(r * 1.6f, r * 0.5f))
    }

    /** El globo terráqueo sobre su pie escalonado, con el meridiano de latón; la base en [p]. */
    private fun DrawScope.globo(p: Offset, a: Float, oscuro: Boolean) {
        val r = a * 0.3f
        val c = Offset(p.x, p.y - a * 0.2f - r)
        sombra(Rect(p.x - a * 0.2f, p.y - a * 0.03f, p.x + a * 0.2f, p.y), a * 0.05f, 0.35f)
        laca(Rect(p.x - a * 0.2f, p.y - a * 0.05f, p.x + a * 0.2f, p.y), oscuro)
        metal(Rect(p.x - a * 0.14f, p.y - a * 0.1f, p.x + a * 0.14f, p.y - a * 0.05f), oscuro)
        drawRect(t(ORO_OSC, oscuro), Offset(p.x - a * 0.02f, c.y + r), Size(a * 0.04f, p.y - a * 0.1f - c.y - r))
        drawCircle(t(MARFIL, oscuro), r, c)
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            drawOval(t(JADE, oscuro), Offset(c.x - r * 0.8f, c.y - r * 0.6f), Size(r * 0.7f, r * 0.9f))
            drawOval(t(JADE, oscuro), Offset(c.x + r * 0.1f, c.y - r * 0.3f), Size(r * 0.6f, r * 1.1f))
            for (k in -2..2) drawLine(t(ORO_OSC, oscuro).copy(alpha = 0.4f), Offset(c.x - r, c.y + k * r * 0.35f), Offset(c.x + r, c.y + k * r * 0.35f), a * 0.006f)
            drawCircle(Color.Black.copy(alpha = 0.22f), r, Offset(c.x + r * 0.35f, c.y + r * 0.2f), style = Stroke(r * 0.5f))
        }
        drawArc(t(ORO, oscuro), 120f, 300f, false, Offset(c.x - r * 1.15f, c.y - r * 1.15f), Size(r * 2.3f, r * 2.3f), style = Stroke(a * 0.025f))
        drawCircle(t(ORO_LUZ, oscuro), a * 0.02f, Offset(c.x, c.y - r * 1.15f))
    }

    /** La pluma estilográfica de laca con capuchón de oro, tumbada de [a] (plumín) a [b]. */
    private fun DrawScope.estilografica(a: Offset, b: Offset, g: Float, oscuro: Boolean) {
        drawLine(Color.Black.copy(alpha = 0.25f), a + Offset(g * 0.3f, g * 0.5f), b + Offset(g * 0.3f, g * 0.5f), g, StrokeCap.Round)
        val m = Offset(a.x + (b.x - a.x) * 0.15f, a.y + (b.y - a.y) * 0.15f)
        drawLine(t(ORO, oscuro), a, m, g * 0.55f)
        drawLine(t(JADE, oscuro), m, b, g, StrokeCap.Round)
        drawLine(t(JADE_LUZ, oscuro), m, b, g * 0.3f)
        val q = Offset(a.x + (b.x - a.x) * 0.62f, a.y + (b.y - a.y) * 0.62f)
        drawLine(t(ORO, oscuro), q, Offset(q.x + (b.x - a.x) * 0.04f, q.y + (b.y - a.y) * 0.04f), g * 1.1f)
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.MAPAS)
        val canto = h * 0.78f
        // Las patas escalonadas de laca con su zapata de latón, y la sombra en el suelo.
        sombra(Rect(w * 0.03f, h * 0.965f, w * 0.97f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(w * 0.025f, w * 0.915f).forEach { x ->
            val pw = w * 0.06f
            laca(Rect(x + pw * 0.05f, h * 0.9f, x + pw * 0.95f, h * 0.925f), oscuro)
            laca(Rect(x + pw * 0.18f, h * 0.925f, x + pw * 0.82f, h * 0.95f), oscuro)
            laca(Rect(x + pw * 0.3f, h * 0.95f, x + pw * 0.7f, h * 0.975f), oscuro)
            metal(Rect(x + pw * 0.26f, h * 0.975f, x + pw * 0.74f, h * 0.995f), oscuro)
        }
        // El faldón de laca: el cajón con su tirador de abanico, los paneles de galuchat y las esquinas de oro.
        val fal = Rect(w * 0.025f, h * 0.825f, w * 0.975f, h * 0.9f)
        laca(fal, oscuro, filete = false)
        drawRect(Color.Black.copy(alpha = 0.3f), fal.topLeft, Size(fal.width, 1f * u))
        val caj = Rect(w * 0.36f, fal.top + 1.4f * u, w * 0.64f, fal.bottom - 1.2f * u)
        drawRect(t(LACA_LUZ, oscuro), caj.topLeft, caj.size)
        drawRect(t(ORO, oscuro), caj.topLeft, caj.size, style = Stroke(0.4f * u))
        abanico(Offset(caj.center.x, caj.center.y + 1.6f * u), 3.4f * u, oscuro, rayos = 8)
        listOf(Rect(w * 0.1f, caj.top, w * 0.33f, caj.bottom), Rect(w * 0.67f, caj.top, w * 0.9f, caj.bottom)).forEach { r ->
            drawRect(if (oscuro) Color(0xFF3B352B) else MARFIL, r.topLeft, r.size)
            galuchat(r, oscuro, 1.4f * u)
            drawRect(t(ORO, oscuro), r.topLeft, r.size, style = Stroke(0.4f * u))
        }
        listOf(fal.left, fal.right - w * 0.06f).forEach { x ->
            val d = Rect(x, fal.top, x + w * 0.06f, fal.bottom)
            laca(d, oscuro, filete = false)
            drawPath(escalonado(d.deflate(1.2f * u), 0.8f * u), t(ORO, oscuro), style = Stroke(0.4f * u))
        }
        // El canto de laca con las tres líneas de cromo.
        val ct = Rect(w * 0.01f, canto, w * 0.99f, h * 0.825f)
        laca(ct, oscuro, filete = false)
        drawRect(Color.White.copy(alpha = 0.15f), ct.topLeft, Size(ct.width, 0.6f * u))
        velocidad(ct.left + w * 0.02f, ct.right - w * 0.02f, ct.top + ct.height * 0.28f, ct.height * 0.22f, oscuro)
        // El tablero de Macasar en hojas casadas, con el filete de latón y las esquinas escalonadas de oro.
        val tab = Rect(w * 0.01f, h * 0.015f, w * 0.99f, canto)
        macasar(Rect(tab.left, tab.top, tab.center.x, tab.bottom), oscuro, e.semilla, vertical = true)
        macasar(Rect(tab.center.x, tab.top, tab.right, tab.bottom), oscuro, e.semilla, vertical = true)
        drawLine(t(ORO_OSC, oscuro), Offset(tab.center.x, tab.top), Offset(tab.center.x, tab.bottom), 0.4f * u)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent), tab.top, tab.top + tab.height * 0.4f), tab.topLeft, Size(tab.width, tab.height * 0.4f))
        drawPath(escalonado(tab.deflate(1.6f * u), 2f * u), t(ORO, oscuro), style = Stroke(0.45f * u))
        // La carta marfil, un poco más grande que el nicho, con sus cantoneras de cromo.
        val pg = n.inflate(2.8f * u)
        sombra(pg, 2.2f * u, if (oscuro) 0.5f else 0.35f)
        drawRect(if (oscuro) Color(0xFF3A342A) else MARFIL_OSC, pg.topLeft, pg.size)
        drawRect(forro(oscuro), n.inflate(1.4f * u).topLeft, n.inflate(1.4f * u).size)
        drawRect(t(JADE, oscuro).copy(alpha = 0.6f), n.inflate(1.4f * u).topLeft, n.inflate(1.4f * u).size, style = Stroke(0.35f * u))
        drawRect(forro(oscuro), n.topLeft, n.size)
        val k = 5 * u
        listOf(pg.topLeft to Offset(1f, 1f), Offset(pg.right, pg.top) to Offset(-1f, 1f), Offset(pg.left, pg.bottom) to Offset(1f, -1f), pg.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
            val tri = Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * k, o.y); lineTo(o.x, o.y + s.y * k); close() }
            drawPath(tri, t(CROMO, oscuro))
            drawPath(Path().apply { moveTo(o.x + s.x * k * 0.2f, o.y + s.y * k * 0.2f); lineTo(o.x + s.x * k * 0.75f, o.y + s.y * k * 0.2f); lineTo(o.x + s.x * k * 0.2f, o.y + s.y * k * 0.75f); close() }, t(CROMO_LUZ, oscuro))
            drawPath(tri, t(CROMO_OSC, oscuro), style = Stroke(0.3f * u))
        }
        // A un lado, la lámpara y la estilográfica; al otro, el globo y el compás.
        val izq = (tab.left + pg.left) / 2
        val der = (tab.right + pg.right) / 2
        val anchoLado = pg.left - tab.left
        lampara(Offset(izq, tab.top + tab.height * 0.6f), (anchoLado * 1.25f).coerceAtMost(30 * u), oscuro)
        estilografica(Offset(izq - anchoLado * 0.3f, tab.bottom - 4 * u), Offset(izq + anchoLado * 0.32f, tab.top + tab.height * 0.68f), 1.6f * u, oscuro)
        globo(Offset(der, tab.top + tab.height * 0.5f), (anchoLado * 1.15f).coerceAtMost(28 * u), oscuro)
        // El compás de cromo, abierto.
        val cc = Offset(der, tab.top + tab.height * 0.62f)
        val l = tab.height * 0.26f
        listOf(-16f, 16f).forEach { g -> rotate(g, cc) {
            drawPath(Path().apply { moveTo(cc.x - l * 0.05f, cc.y); lineTo(cc.x + l * 0.05f, cc.y); lineTo(cc.x, cc.y + l); close() }, t(if (g < 0) CROMO else CROMO_OSC, oscuro))
        } }
        drawCircle(t(ORO, oscuro), l * 0.08f, cc)
    }

    // ---------------------------------------------------------------- Otras vidas: la vitrina rascacielos

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.OTRAS_VIDAS)
        // La sombra en el suelo y las patas escalonadas.
        sombra(Rect(w * 0.02f, h * 0.955f, w * 0.98f, h * 0.99f), 3 * u, if (oscuro) 0.5f else 0.25f)
        listOf(0.06f, 0.94f).forEach { f ->
            val x = w * f
            laca(Rect(x - 5 * u, h * 0.93f, x + 5 * u, h * 0.955f), oscuro)
            metal(Rect(x - 3.4f * u, h * 0.955f, x + 3.4f * u, h * 0.99f), oscuro)
        }
        // El cuerpo de laca y el zócalo con las líneas de cromo y los chevrones.
        val cuerpo = Rect(w * 0.015f, h * 0.17f, w * 0.985f, h * 0.93f)
        laca(cuerpo, oscuro, filete = false)
        val zoc = Rect(w * 0.005f, h * 0.83f, w * 0.995f, h * 0.93f)
        laca(zoc, oscuro)
        drawRect(t(ORO, oscuro), Offset(zoc.left, zoc.top + 0.9f * u), Size(zoc.width, 0.4f * u))
        velocidad(zoc.left + w * 0.03f, zoc.left + w * 0.28f, zoc.top + zoc.height * 0.35f, 1.6f * u, oscuro)
        velocidad(zoc.right - w * 0.28f, zoc.right - w * 0.03f, zoc.top + zoc.height * 0.35f, 1.6f * u, oscuro)
        chevrones(w * 0.33f, w * 0.67f, zoc.top + zoc.height * 0.3f, zoc.bottom - zoc.height * 0.25f, t(ORO, oscuro), 0.45f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(zoc.left, zoc.bottom - 0.8f * u), Size(zoc.width, 0.8f * u))
        // Las pilastras de los lados.
        val pw = w * 0.045f
        listOf(w * 0.012f, w * 0.988f - pw).forEach { x -> pilastra(Rect(x, h * 0.17f, x + pw, h * 0.83f), oscuro) }
        // El nicho: cinco hornacinas de remate escalonado, forradas, con un sol tenue detrás de cada medalla.
        drawRect(t(ORO, oscuro), n.inflate(1.4f * u).topLeft, n.inflate(1.4f * u).size)
        drawRect(t(ORO_OSC, oscuro), n.inflate(0.6f * u).topLeft, n.inflate(0.6f * u).size)
        drawRect(forro(oscuro), n.topLeft, n.size)
        galuchat(n, oscuro, 1.8f * u)
        val paso = n.width / 5
        val sol = if (oscuro) ORO.copy(alpha = 0.1f) else ORO_OSC.copy(alpha = 0.13f)
        for (k in 0 until 5) {
            val c = Rect(n.left + paso * k, n.top, n.left + paso * (k + 1), n.bottom)
            clipRect(c.left, c.top, c.right, c.bottom) { solTenue(Offset(c.center.x, c.top + c.height * 0.4f), c.width * 0.7f, sol, rayos = 20) }
            // Los hombros escalonados del remate, en laca con su filete de oro.
            val e1 = c.width * 0.12f; val e2 = c.height * 0.06f
            listOf(-1f, 1f).forEach { s ->
                val x0 = if (s < 0) c.left else c.right
                val p = Path().apply { moveTo(x0, c.top); lineTo(x0 - s * 2 * e1, c.top); lineTo(x0 - s * 2 * e1, c.top + e2); lineTo(x0 - s * e1, c.top + e2); lineTo(x0 - s * e1, c.top + 2 * e2); lineTo(x0, c.top + 2 * e2); close() }
                drawPath(p, t(LACA, oscuro))
                drawPath(Path().apply { moveTo(x0 - s * 2 * e1, c.top); lineTo(x0 - s * 2 * e1, c.top + e2); lineTo(x0 - s * e1, c.top + e2); lineTo(x0 - s * e1, c.top + 2 * e2); lineTo(x0, c.top + 2 * e2) }, t(ORO, oscuro), style = Stroke(0.4f * u))
            }
        }
        sombraInterior(n, 4 * u, if (oscuro) 0.4f else 0.14f)
        // El cristal de cada hornacina, apenas un reflejo en la esquina, y la repisa de cristal con su canto de cromo.
        val rep = Rect(n.left, n.bottom - 1.6f * u, n.right, n.bottom)
        drawRect(t(CROMO, oscuro), rep.topLeft, rep.size)
        drawRect(t(CROMO_LUZ, oscuro), rep.topLeft, Size(rep.width, 0.5f * u))
        // Las columnillas entre hornacinas: laca con tres estrías de oro.
        for (k in 1 until 5) {
            val x = n.left + paso * k
            val r = Rect(x - 1.6f * u, n.top, x + 1.6f * u, n.bottom)
            laca(r, oscuro, filete = false)
            drawLine(t(ORO, oscuro), Offset(x, r.top + 1 * u), Offset(x, r.bottom - 1 * u), 0.35f * u)
            metal(Rect(r.left - 0.5f * u, r.top, r.right + 0.5f * u, r.top + 1.4f * u), oscuro)
            metal(Rect(r.left - 0.5f * u, r.bottom - 1.4f * u, r.right + 0.5f * u, r.bottom), oscuro)
        }
        // La corona en tres escalones, con sus líneas de oro y el abanico de rayos al centro.
        val r1 = Rect(0f, h * 0.11f, w, h * 0.17f)
        val r2 = Rect(w * 0.16f, h * 0.06f, w * 0.84f, h * 0.11f)
        val r3 = Rect(w * 0.34f, h * 0.02f, w * 0.66f, h * 0.06f)
        listOf(r1, r2, r3).forEach { laca(it, oscuro) }
        drawRect(t(ORO, oscuro), Offset(0f, r1.bottom - 0.8f * u), Size(w, 0.5f * u))
        for (k in 0 until 5) {
            val cx = n.left + paso * (k + 0.5f)
            if (k != 2) drawPath(escalonado(Rect(cx - 3.4f * u, r1.top + r1.height * 0.25f, cx + 3.4f * u, r1.bottom - r1.height * 0.3f), 0.7f * u), t(ORO, oscuro), style = Stroke(0.4f * u))
        }
        velocidad(r2.left + w * 0.03f, r3.left - w * 0.03f, r2.top + r2.height * 0.3f, 1.4f * u, oscuro)
        velocidad(r3.right + w * 0.03f, r2.right - w * 0.03f, r2.top + r2.height * 0.3f, 1.4f * u, oscuro)
        abanico(Offset(w / 2, r1.top + 0.2f * u), h * 0.1f, oscuro, rayos = 14)
        drawLine(t(ORO, oscuro), Offset(w / 2, r3.top), Offset(w / 2, 0f), 0.6f * u)
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val s = h / 70f * 60f
        val c = Offset(w / 2, h / 2)
        val u = s / 60f
        when (id) {
            "unesco" -> {
                // El rascacielos: las alas bajas, la torre con sus retranqueos, la corona de arcos escalonados y la aguja.
                val base = c.y + s * 0.47f
                drawOval(Color.Black.copy(alpha = 0.25f), Offset(c.x - s * 0.55f, base - 1.6f * u), Size(s * 1.1f, 3.2f * u))
                fun bloque(r: Rect) {
                    drawRect(t(Color(0xFFE4DCC8), oscuro), r.topLeft, r.size)
                    drawRect(t(Color(0xFFA89E86), oscuro), Offset(r.center.x, r.top), Size(r.width / 2, r.height))
                    // Las ventanas en franjas verticales entre pilares, como en los rascacielos de la época.
                    var x = r.left + 1.4f * u
                    while (x < r.right - 1.6f * u) {
                        drawRect(t(if (x < r.center.x) Color(0xFF3A464E) else Color(0xFF242C32), oscuro), Offset(x, r.top + 1.6f * u), Size(1.1f * u, r.height - 3.2f * u))
                        x += 2.6f * u
                    }
                    drawRect(t(ORO, oscuro), r.topLeft, Size(r.width, 0.7f * u))
                }
                bloque(Rect(c.x - s * 0.55f, base - s * 0.3f, c.x - s * 0.24f, base))
                bloque(Rect(c.x + s * 0.24f, base - s * 0.3f, c.x + s * 0.55f, base))
                bloque(Rect(c.x - s * 0.27f, base - s * 0.5f, c.x + s * 0.27f, base))
                bloque(Rect(c.x - s * 0.19f, base - s * 0.64f, c.x + s * 0.19f, base - s * 0.5f))
                // Los retranqueos de la torre, cada uno más estrecho, con su abanico de ventanas en el último, y la aguja.
                bloque(Rect(c.x - s * 0.13f, base - s * 0.75f, c.x + s * 0.13f, base - s * 0.64f))
                val cima = Rect(c.x - s * 0.08f, base - s * 0.84f, c.x + s * 0.08f, base - s * 0.75f)
                drawRect(t(CROMO, oscuro), cima.topLeft, cima.size)
                drawRect(t(CROMO_OSC, oscuro), Offset(cima.center.x, cima.top), Size(cima.width / 2, cima.height))
                drawArc(e.c1, 180f, 180f, true, Offset(cima.center.x - cima.width * 0.36f, cima.bottom - cima.width * 0.36f - 1 * u), Size(cima.width * 0.72f, cima.width * 0.72f))
                drawPath(Path().apply { moveTo(cima.left, cima.top); lineTo(c.x, cima.top - s * 0.1f); lineTo(cima.right, cima.top); close() }, t(CROMO, oscuro))
                drawPath(Path().apply { moveTo(c.x, cima.top); lineTo(c.x, cima.top - s * 0.1f); lineTo(cima.right, cima.top); close() }, t(CROMO_OSC, oscuro))
                drawLine(t(CROMO_OSC, oscuro), Offset(c.x, cima.top - s * 0.1f), Offset(c.x, base - s * 1.02f), 0.8f * u, StrokeCap.Round)
                drawRect(t(LACA, oscuro), Offset(c.x - s * 0.07f, base - s * 0.13f), Size(s * 0.14f, s * 0.13f))
                abanico(Offset(c.x, base - s * 0.13f), s * 0.07f, oscuro, rayos = 8)
                drawRect(e.c2, Offset(c.x - s * 0.05f, base - s * 0.1f), Size(s * 0.1f, s * 0.1f))
            }
            "rascar" -> {
                // La placa de laca con su marco de oro escalonado, una franja raspada y la moneda de oro con su sol.
                val m = Rect(c.x - s * 0.66f, c.y - s * 0.38f, c.x + s * 0.5f, c.y + s * 0.38f)
                drawRect(Color.Black.copy(alpha = 0.25f), m.topLeft + Offset(1.2f * u, 1.6f * u), m.size)
                drawPath(escalonado(m, 2.4f * u), t(LACA, oscuro))
                drawPath(escalonado(m.deflate(1.6f * u), 1.8f * u), t(ORO, oscuro), style = Stroke(0.8f * u))
                val capa = m.deflate(4.4f * u)
                drawRect(t(LACA_LUZ, oscuro), capa.topLeft, capa.size)
                clipRect(capa.left, capa.top, capa.right, capa.bottom) {
                    solTenue(Offset(capa.center.x, capa.bottom), capa.height * 1.4f, ORO.copy(alpha = 0.3f), rayos = 28)
                    val raspa = Path().apply { moveTo(capa.left, capa.top + capa.height * 0.4f); cubicTo(capa.left + capa.width * 0.3f, capa.top, capa.left + capa.width * 0.55f, capa.bottom, capa.right, capa.top + capa.height * 0.5f) }
                    drawPath(raspa, t(MARFIL, oscuro), style = Stroke(capa.height * 0.44f, cap = StrokeCap.Round))
                    drawPath(raspa, e.c1, style = Stroke(capa.height * 0.24f, cap = StrokeCap.Round))
                }
                val mc = Offset(m.right + s * 0.02f, m.bottom - s * 0.12f)
                val mr = s * 0.23f
                drawCircle(Color.Black.copy(alpha = 0.3f), mr, mc + Offset(1.2f * u, 1.6f * u))
                drawCircle(t(ORO_OSC, oscuro), mr, mc)
                drawCircle(t(ORO, oscuro), mr * 0.88f, mc)
                for (k in 0 until 16) {
                    val a = Math.toRadians(k * 22.5)
                    drawLine(t(ORO_OSC, oscuro), Offset(mc.x + mr * 0.3f * cos(a).toFloat(), mc.y + mr * 0.3f * sin(a).toFloat()), Offset(mc.x + mr * 0.74f * cos(a).toFloat(), mc.y + mr * 0.74f * sin(a).toFloat()), mr * 0.07f)
                }
                drawCircle(t(ORO_LUZ, oscuro), mr * 0.24f, mc)
                drawArc(t(ORO_LUZ, oscuro), 200f, 100f, false, Offset(mc.x - mr * 0.82f, mc.y - mr * 0.82f), Size(mr * 1.64f, mr * 1.64f), style = Stroke(mr * 0.08f))
            }
            else -> {
                // Tus lugares: tres alfileres de cabeza escalonada sobre la carta marfil, unidos por la ruta del barco.
                val m = Rect(c.x - s * 0.95f, c.y + s * 0.1f, c.x + s * 0.95f, c.y + s * 0.46f)
                drawRect(Color.Black.copy(alpha = 0.22f), m.topLeft + Offset(1 * u, 1.4f * u), m.size)
                drawRect(t(MARFIL, oscuro), m.topLeft, m.size)
                drawRect(t(ORO, oscuro), m.deflate(1.2f * u).topLeft, m.deflate(1.2f * u).size, style = Stroke(0.5f * u))
                for (k in 1 until 4) drawLine(t(JADE, oscuro).copy(alpha = 0.35f), Offset(m.left, m.top + m.height * k / 4), Offset(m.right, m.top + m.height * k / 4), 0.3f * u)
                val pts = listOf(Offset(c.x - s * 0.62f, m.top + m.height * 0.62f), Offset(c.x, m.top + m.height * 0.38f), Offset(c.x + s * 0.62f, m.top + m.height * 0.6f))
                val ruta = Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.3f, m.bottom, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.3f, m.top, pts[2].x, pts[2].y) }
                drawPath(ruta, e.c2, style = Stroke(1.1f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
                pts.forEachIndexed { i, p ->
                    val alto = s * (if (i == 1) 0.58f else 0.48f)
                    drawOval(Color.Black.copy(alpha = 0.3f), Offset(p.x - 2 * u, p.y - 0.8f * u), Size(4 * u, 1.6f * u))
                    drawLine(t(CROMO_OSC, oscuro), p, Offset(p.x, p.y - alto), 0.9f * u)
                    val tope = p.y - alto
                    val gema = listOf(JADE, CORAL, e.c1)[i]
                    // La cabeza: un zigurat de oro de tres escalones con su gema.
                    scale(1.4f, Offset(p.x, tope)) {
                        metal(Rect(p.x - 4.5f * u, tope, p.x + 4.5f * u, tope + 2.4f * u), oscuro)
                        metal(Rect(p.x - 3.2f * u, tope - 2.4f * u, p.x + 3.2f * u, tope), oscuro)
                        metal(Rect(p.x - 1.9f * u, tope - 4.8f * u, p.x + 1.9f * u, tope - 2.4f * u), oscuro)
                        drawPath(Path().apply { moveTo(p.x, tope - 1.8f * u); lineTo(p.x + 1.6f * u, tope + 0.6f * u); lineTo(p.x, tope + 2.2f * u); lineTo(p.x - 1.6f * u, tope + 0.6f * u); close() }, t(gema, oscuro))
                        drawCircle(Color.White.copy(alpha = 0.5f), 0.4f * u, Offset(p.x - 0.4f * u, tope))
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas: el tablero de avisos del vestíbulo

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = nicho(MuebleVida.NOTAS)
        // El marco de laca de esquinas escalonadas, con su doble filete de oro.
        val marco = Rect(w * 0.012f, h * 0.075f, w * 0.988f, h * 0.935f)
        sombra(marco, 3 * u, if (oscuro) 0.5f else 0.28f)
        val pm = escalonado(marco, 2.6f * u)
        drawPath(pm, t(LACA, oscuro))
        clipPath(pm) {
            drawRect(Brush.verticalGradient(listOf(t(LACA_BRILLO, oscuro).copy(alpha = 0.6f), Color.Transparent), marco.top, marco.top + marco.height * 0.3f), marco.topLeft, marco.size)
            drawLine(Color.White.copy(alpha = 0.08f), Offset(marco.left + marco.width * 0.1f, marco.bottom), Offset(marco.left + marco.width * 0.35f, marco.top), 6 * u)
        }
        drawPath(escalonado(marco.deflate(1.2f * u), 2.2f * u), t(ORO, oscuro), style = Stroke(0.6f * u))
        val med = Rect((marco.left + n.left) / 2, (marco.top + n.top) / 2, (marco.right + n.right) / 2, (marco.bottom + n.bottom) / 2)
        drawPath(escalonado(med, 1.6f * u), t(ORO, oscuro).copy(alpha = 0.7f), style = Stroke(0.3f * u))
        // Las estrías de oro de los costados.
        listOf(marco.left + (n.left - marco.left) * 0.5f, marco.right - (marco.right - n.right) * 0.5f).forEach { x ->
            for (k in -1..1) drawLine(t(ORO, oscuro), Offset(x + k * 1.2f * u, n.top + 6 * u), Offset(x + k * 1.2f * u, n.bottom - 6 * u), 0.3f * u)
        }
        // El forro del nicho, con su marco de oro y su galuchat.
        drawRect(t(ORO, oscuro), n.inflate(1.2f * u).topLeft, n.inflate(1.2f * u).size)
        drawRect(t(ORO_OSC, oscuro), n.inflate(0.5f * u).topLeft, n.inflate(0.5f * u).size)
        drawRect(forro(oscuro), n.topLeft, n.size)
        galuchat(n, oscuro, 1.8f * u)
        sombraInterior(n, 3 * u, if (oscuro) 0.35f else 0.12f)
        // La repisa de cromo con la estilográfica y el timbre.
        val rep = Rect(w * 0.005f, h * 0.935f, w * 0.995f, h * 0.97f)
        metal(rep, oscuro, cromo = true)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left, rep.bottom), Size(rep.width, 0.6f * u))
        estilografica(Offset(w * 0.6f, rep.top - 0.9f * u), Offset(w * 0.78f, rep.top - 0.9f * u), 1.6f * u, oscuro)
        val tb = Offset(w * 0.26f, rep.top)
        drawRect(t(ORO_OSC, oscuro), Offset(tb.x - 4 * u, tb.y - 1 * u), Size(8 * u, 1 * u))
        drawArc(t(ORO, oscuro), 180f, 180f, true, Offset(tb.x - 3.2f * u, tb.y - 4.2f * u), Size(6.4f * u, 6.4f * u))
        drawArc(t(ORO_LUZ, oscuro), 200f, 50f, false, Offset(tb.x - 2.4f * u, tb.y - 3.4f * u), Size(4.8f * u, 4.8f * u), style = Stroke(0.5f * u))
        drawRect(t(ORO_OSC, oscuro), Offset(tb.x - 0.5f * u, tb.y - 5f * u), Size(1 * u, 1 * u))
        // El copete: el remate escalonado con el abanico de rayos.
        val c1 = Rect(w * 0.3f, h * 0.045f, w * 0.7f, marco.top + 0.5f * u)
        val c2 = Rect(w * 0.4f, h * 0.015f, w * 0.6f, h * 0.045f)
        listOf(c1, c2).forEach { laca(it, oscuro) }
        abanico(Offset(w / 2, marco.top + 0.3f * u), h * 0.075f, oscuro, rayos = 14)
        velocidad(c1.left + w * 0.02f, c2.left - w * 0.03f, c1.top + c1.height * 0.3f, 1.3f * u, oscuro)
        velocidad(c2.right + w * 0.03f, c1.right - w * 0.02f, c1.top + c1.height * 0.3f, 1.3f * u, oscuro)
    }

    /** El papel del telegrama: claro de día; de noche, a la luz baja del vestíbulo (la tinta se lee en los dos). */
    private fun papel(oscuro: Boolean) = if (oscuro) Color(0xFFD2C49A) else TELEGRAMA

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val u = r.width / 100f
        val p = Rect(r.left + 3 * u, r.top + 4 * u, r.right - 3 * u, r.bottom - 2 * u)
        // La sombra, el papel del telegrama, la franja de jade con su filete de oro arriba.
        sombra(p, 3 * u, if (oscuro) 0.5f else 0.28f)
        val forma = escalonado(p, 2.2f * u)
        drawPath(forma, papel(oscuro))
        clipPath(forma) {
            drawRect(JADE, p.topLeft, Size(p.width, 5 * u))
            drawRect(ORO, Offset(p.left, p.top + 5 * u), Size(p.width, 1.2f * u))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, MARFIL_OSC.copy(alpha = 0.5f)), p.top + p.height * 0.6f, p.bottom), Offset(p.left, p.top + p.height * 0.6f), Size(p.width, p.height * 0.4f))
            for (k in 0 until 3) drawLine(ORO.copy(alpha = 0.8f), Offset(p.left + p.width * 0.25f + k * 3 * u, p.top + 2.5f * u), Offset(p.left + p.width * 0.25f + k * 3 * u + 6 * u, p.top + 2.5f * u), 0.4f * u)
        }
        // La esquina escalonada de oro de abajo (la otra la ocupa el detalle del tipo).
        val m = 4 * u; val l = 8 * u
        listOf(Offset(p.left + m, p.bottom - m) to 1f).forEach { (o, s) ->
            drawPath(Path().apply { moveTo(o.x + s * l, o.y); lineTo(o.x, o.y); lineTo(o.x, o.y - l); moveTo(o.x + s * l * 0.6f, o.y - 2 * u); lineTo(o.x + s * 2 * u, o.y - 2 * u); lineTo(o.x + s * 2 * u, o.y - l * 0.6f) }, ORO_OSC, style = Stroke(0.7f * u))
        }
        // El detalle del tipo, abajo a la derecha (fuera del texto).
        val d = Offset(p.right - 11 * u, p.bottom - 10 * u)
        when (e.tipo) {
            "idea" -> scale(1.4f, d) {
                // El sol: un abanico de oro.
                abanico(Offset(d.x, d.y + 2.5f * u), 5 * u, false, rayos = 10)
            }
            "pensamiento" -> scale(1.4f, d) {
                // Los chevrones de jade.
                listOf(-1.8f, 1.2f).forEach { dy -> drawPath(Path().apply { moveTo(d.x - 4 * u, d.y + dy * u - 1.5f * u); lineTo(d.x, d.y + dy * u + 1.5f * u); lineTo(d.x + 4 * u, d.y + dy * u - 1.5f * u) }, JADE, style = Stroke(1.2f * u)) }
            }
            "proposito" -> scale(1.4f, d) {
                // El zigurat de coral.
                listOf(0 to 4.5f, 1 to 3f, 2 to 1.5f).forEach { (k, a) -> drawRect(if (k % 2 == 0) CORAL else mezcla(CORAL, Color.Black, 0.25f), Offset(d.x - a * u, d.y + 3 * u - (k + 1) * 2 * u), Size(2 * a * u, 2 * u)) }
            }
            else -> {
                // El recordatorio: la esquina doblada y el matasellos de coral.
                val k = 9 * u
                drawPath(Path().apply { moveTo(p.right - k, p.bottom); lineTo(p.right, p.bottom - k); lineTo(p.right, p.bottom); close() }, forro(oscuro))
                drawPath(Path().apply { moveTo(p.right - k, p.bottom); lineTo(p.right, p.bottom - k); lineTo(p.right - k, p.bottom - k); close() }, MARFIL_OSC)
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(p.right - k, p.bottom), Offset(p.right, p.bottom - k), 0.5f * u)
                val ms = Offset(d.x - 4 * u, d.y - 1 * u)
                drawCircle(CORAL.copy(alpha = 0.8f), 5 * u, ms, style = Stroke(0.7f * u))
                drawCircle(CORAL.copy(alpha = 0.8f), 3.2f * u, ms, style = Stroke(0.4f * u))
                for (j in 0 until 3) drawLine(CORAL.copy(alpha = 0.7f), Offset(ms.x + 5.5f * u, ms.y - 2 * u + j * 2 * u), Offset(ms.x + 11 * u, ms.y - 2 * u + j * 2 * u), 0.5f * u)
            }
        }
        // Arriba al centro: el broche de abanico de oro si está fijada; si no, la chincheta de cromo.
        val s = Offset(p.center.x, p.top + 2.5f * u)
        if (e.fijada) {
            drawCircle(Color.Black.copy(alpha = 0.3f), 6 * u, s + Offset(0.6f * u, 1.4f * u))
            abanico(Offset(s.x, s.y + 3 * u), 8 * u, false, rayos = 12, ojo = GRANATE)
            drawRect(ORO_OSC, Offset(s.x - 8.5f * u, s.y + 3 * u), Size(17 * u, 1.4f * u))
            drawRect(ORO_LUZ, Offset(s.x - 8.5f * u, s.y + 3 * u), Size(17 * u, 0.5f * u))
        } else {
            drawCircle(Color.Black.copy(alpha = 0.3f), 3 * u, s + Offset(0.8f * u, 1.1f * u))
            drawCircle(CROMO_OSC, 3 * u, s)
            drawCircle(CROMO, 2.4f * u, s)
            drawCircle(CROMO_LUZ, 0.9f * u, s - Offset(0.8f * u, 0.8f * u))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
