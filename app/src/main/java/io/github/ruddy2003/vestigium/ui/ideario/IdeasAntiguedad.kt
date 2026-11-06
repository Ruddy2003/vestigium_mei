package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Antigüedad
// =====================================================================

/**
 * Prehistoria: la pared de la cueva. Arriba, el techo con sus estalactitas; en la roca, manos en negativo sopladas con ocre y
 * un bisonte a carbón; abajo, el suelo con el fogón. Cada tema es un nicho tallado en la roca, con sus lajas apoyadas
 * adentro, y su nombre pintado con ocre debajo, sobre la piedra (no en una etiqueta pegada). Al tocar un nicho, lo ilumina
 * el fuego. Las fichas son lajas de caliza, cada una con su contorno desportillado.
 */
object IdeasCueva : DisenoIdeas {
    private val ROCA = Color(0xFFB8A386)
    private val ROCA_LUZ = Color(0xFFDCCDB2)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF2F241B)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    private val CALIZA = Color(0xFFEDE2CC)
    private val BRASA = Color(0xFFE8804F)
    private val FUEGO = Color(0xFFF4C76E)

    /** De noche la cueva solo la alumbra el fuego: la roca se oscurece. */
    private fun roca(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color(0xFF1E150E), 0.55f) else c

    override val cols = 2
    override val proporcion = 1.05f
    override val corona = 0.24f
    override val base = 0.2f
    override val lado = 0.07f
    override val hueco = 0.06f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La roca: un contorno irregular, más oscura arriba (el techo) y abajo (el suelo).
        val contorno = Path().apply {
            moveTo(0f, d.corona.height * 0.2f)
            val pasos = 14
            for (k in 1..pasos) lineTo(w * k / pasos, d.corona.height * (0.08f + 0.2f * azar(5, k)))
            lineTo(w, h); lineTo(0f, h); close()
        }
        drawPath(contorno, Brush.verticalGradient(listOf(roca(ROCA_OSC, oscuro), roca(ROCA, oscuro), roca(ROCA_LUZ, oscuro), roca(ROCA, oscuro), roca(ROCA_OSC, oscuro)), 0f, h))
        // La textura: manchas de luz y de sombra, estratos y grietas.
        val n = (60 * h / w).toInt().coerceIn(40, 260)
        for (k in 0 until n) {
            val c = Offset(w * azar(11, k), d.corona.height * 0.4f + (h - d.corona.height * 0.4f) * azar(12, k))
            mancha(c, (6f + 22f * azar(13, k)) * u, (if (k % 3 == 0) roca(ROCA_LUZ, oscuro) else roca(ROCA_OSC, oscuro)).copy(alpha = 0.22f))
        }
        vetas(Rect(0f, d.corona.bottom, w, d.base.top), roca(ROCA_OSC, oscuro).copy(alpha = 0.22f), 17, 0.22f, grosor = 1.4f)
        for (g in 0 until 5) {
            val p = Path(); var x = w * azar(21, g); var y = d.corona.bottom + (d.base.top - d.corona.bottom) * azar(22, g)
            p.moveTo(x, y)
            for (s in 0 until 6) { x += (azar(23 + g, s) - 0.5f) * 18 * u; y += (6f + 10f * azar(24 + g, s)) * u; p.lineTo(x, y) }
            drawPath(p, roca(ROCA_HONDA, oscuro).copy(alpha = 0.4f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
        // Las pinturas, solo donde no hay nichos: manos sopladas con ocre, un bisonte a carbón y filas de puntos.
        fun libre(c: Offset, r: Float) = d.huecos.none { it.inflate(r + 4 * u).contains(c) }
        // La franja de pinturas, bajo las estalactitas: manos, el bisonte y una fila de puntos.
        val banda = d.corona.top + d.corona.height * 0.72f
        listOf(0.09f, 0.21f, 0.9f).forEachIndexed { k, fx -> manoNegativa(Offset(w * fx, banda + (k % 2) * 4 * u), (10f + 3f * azar(31, k)) * u, -25f + 50f * azar(32, k), oscuro) }
        for (i in 0 until 7) drawCircle(OCRE.copy(alpha = 0.65f), 1.7f * u, Offset(w * (0.7f + 0.025f * i), banda + 12 * u + (if (i % 2 == 0) 0f else 2 * u)))
        val abajo = Offset(w * 0.5f, d.base.top - 4 * u)
        if (libre(abajo, 14 * u)) manoNegativa(abajo, 11 * u, 15f, oscuro)
        d.filas.zipWithNext().forEachIndexed { k, (a, b) ->
            val y = (a.bottom + b.top) / 2
            for (i in 0 until 9) drawCircle(OCRE.copy(alpha = 0.6f), 1.6f * u, Offset(w * (0.36f + 0.035f * i) + (k % 2) * w * 0.06f, y + (if (i % 2 == 0) 0f else 2 * u)))
        }
        // El techo: estalactitas que cuelgan, con su filo de luz.
        for (k in 0 until 11) {
            val x = w * (0.03f + 0.095f * k + 0.04f * azar(41, k))
            val largo = d.corona.height * (0.18f + 0.3f * azar(42, k))
            val ancho = (6f + 8f * azar(43, k)) * u
            val y0 = d.corona.height * 0.12f
            val e = Path().apply { moveTo(x - ancho / 2, y0); quadraticTo(x - ancho * 0.15f, y0 + largo * 0.6f, x, y0 + largo); quadraticTo(x + ancho * 0.15f, y0 + largo * 0.6f, x + ancho / 2, y0); close() }
            drawPath(e, Brush.horizontalGradient(listOf(roca(ROCA_OSC, oscuro), roca(ROCA_LUZ, oscuro), roca(ROCA_OSC, oscuro)), x - ancho / 2, x + ancho / 2))
            drawCircle(roca(ROCA_LUZ, oscuro).copy(alpha = 0.7f), 1.1f * u, Offset(x, y0 + largo + 2.5f * u))
        }
        // El bisonte, en la roca de arriba a la izquierda si queda lugar.
        bisonte(Offset(w * 0.5f, banda), minOf(26 * u, d.corona.height * 0.3f), oscuro)
        // El suelo: tierra con piedras, y el fogón al centro con sus brasas.
        val piso = d.base.top + d.base.height * 0.25f
        drawRect(Brush.verticalGradient(listOf(roca(Color(0xFF6E5A45), oscuro), roca(Color(0xFF4A3B2D), oscuro)), piso, h), Offset(0f, piso), Size(w, h - piso))
        for (k in 0 until 26) {
            val c = Offset(w * azar(51, k), piso + (h - piso) * (0.15f + 0.8f * azar(52, k)))
            val r = (1.5f + 4f * azar(53, k)) * u
            drawOval(roca(if (k % 2 == 0) ROCA_OSC else ROCA, oscuro), Offset(c.x - r, c.y - r * 0.6f), Size(2 * r, 1.2f * r))
        }
        val fuego = Offset(w * 0.68f, piso + (h - piso) * 0.55f)
        mancha(fuego, w * (if (oscuro) 0.6f else 0.35f), FUEGO.copy(alpha = if (oscuro) 0.28f else 0.16f))
        for (k in 0 until 9) {
            val a = k * 0.698f
            val c = Offset(fuego.x + 20 * u * cos(a), fuego.y + 8 * u * sin(a))
            drawOval(roca(ROCA_OSC, oscuro), Offset(c.x - 5 * u, c.y - 3 * u), Size(10 * u, 6 * u))
            drawOval(roca(ROCA_LUZ, oscuro).copy(alpha = 0.5f), Offset(c.x - 3.5f * u, c.y - 2.8f * u), Size(6 * u, 2 * u))
        }
        drawOval(CARBON, Offset(fuego.x - 14 * u, fuego.y - 4 * u), Size(28 * u, 9 * u))
        for (k in 0 until 7) drawCircle(if (k % 2 == 0) BRASA else FUEGO, (1.4f + 1.4f * azar(61, k)) * u, Offset(fuego.x + (azar(62, k) - 0.5f) * 20 * u, fuego.y + (azar(63, k) - 0.5f) * 5 * u))
        // Las llamas.
        listOf(-5f to 0.9f, 0f to 1.3f, 5f to 0.8f).forEach { (dx, al) ->
            val x = fuego.x + dx * u; val alto = 16 * u * al
            val l = Path().apply { moveTo(x - 4 * u, fuego.y); quadraticTo(x - 5 * u, fuego.y - alto * 0.5f, x, fuego.y - alto); quadraticTo(x + 5 * u, fuego.y - alto * 0.5f, x + 4 * u, fuego.y); close() }
            drawPath(l, Brush.verticalGradient(listOf(FUEGO.copy(alpha = 0.2f), FUEGO, BRASA), fuego.y - alto, fuego.y))
        }
        // La laja con «y N más», tirada en el suelo a la izquierda del fuego.
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawPath(lajaPath(r, 9), Color.Black.copy(alpha = 0.3f), style = Stroke(3 * u))
            drawPath(lajaPath(r, 9), roca(CALIZA, oscuro))
        }
    }

    /** Una mano en negativo: el ocre soplado alrededor y la silueta de la mano en el color de la roca. */
    private fun DrawScope.manoNegativa(c: Offset, r: Float, giro: Float, oscuro: Boolean) {
        mancha(c, r * 1.7f, OCRE.copy(alpha = if (oscuro) 0.4f else 0.5f))
        mancha(c, r * 1.1f, OCRE.copy(alpha = 0.25f))
        rotate(giro, c) {
            val piel = roca(ROCA, oscuro)
            drawOval(piel, Offset(c.x - r * 0.42f, c.y - r * 0.15f), Size(r * 0.84f, r * 0.8f))
            val dedos = listOf(-0.32f to 0.55f, -0.12f to 0.72f, 0.08f to 0.75f, 0.27f to 0.62f)
            dedos.forEach { (dx, l) -> drawLine(piel, Offset(c.x + r * dx, c.y), Offset(c.x + r * dx * 1.15f, c.y - r * l), r * 0.17f, StrokeCap.Round) }
            drawLine(piel, Offset(c.x - r * 0.35f, c.y + r * 0.2f), Offset(c.x - r * 0.72f, c.y - r * 0.1f), r * 0.19f, StrokeCap.Round)
            drawRect(piel, Offset(c.x - r * 0.28f, c.y + r * 0.5f), Size(r * 0.56f, r * 0.6f))
        }
    }

    /** Un bisonte pintado a carbón con un relleno de ocre: lomo con joroba, cabeza baja, cuernos y patas. */
    private fun DrawScope.bisonte(c: Offset, s: Float, oscuro: Boolean) {
        val u = density
        val cuerpo = Path().apply {
            moveTo(c.x + s * 0.9f, c.y + s * 0.1f)
            cubicTo(c.x + s * 0.9f, c.y - s * 0.3f, c.x + s * 0.2f, c.y - s * 0.35f, c.x - s * 0.2f, c.y - s * 0.55f)
            cubicTo(c.x - s * 0.5f, c.y - s * 0.62f, c.x - s * 0.75f, c.y - s * 0.35f, c.x - s * 0.9f, c.y - s * 0.05f)
            cubicTo(c.x - s * 1.0f, c.y + s * 0.15f, c.x - s * 0.85f, c.y + s * 0.3f, c.x - s * 0.7f, c.y + s * 0.28f)
            cubicTo(c.x - s * 0.4f, c.y + s * 0.45f, c.x + s * 0.4f, c.y + s * 0.42f, c.x + s * 0.9f, c.y + s * 0.1f)
            close()
        }
        drawPath(cuerpo, OCRE_AMARILLO.copy(alpha = if (oscuro) 0.35f else 0.45f))
        drawPath(cuerpo, CARBON.copy(alpha = 0.75f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // La joroba y la cerviz, más oscuras.
        drawPath(Path().apply { moveTo(c.x - s * 0.75f, c.y - s * 0.3f); cubicTo(c.x - s * 0.5f, c.y - s * 0.6f, c.x - s * 0.15f, c.y - s * 0.55f, c.x + s * 0.05f, c.y - s * 0.38f) },
            CARBON.copy(alpha = 0.5f), style = Stroke(3 * u, cap = StrokeCap.Round))
        drawLine(CARBON.copy(alpha = 0.75f), Offset(c.x - s * 0.88f, c.y - s * 0.12f), Offset(c.x - s * 0.98f, c.y - s * 0.3f), 1.3f * u, StrokeCap.Round)
        listOf(-0.6f, -0.35f, 0.45f, 0.7f).forEachIndexed { k, dx ->
            drawLine(CARBON.copy(alpha = 0.75f), Offset(c.x + s * dx, c.y + s * 0.3f), Offset(c.x + s * (dx + 0.04f * (k % 2)), c.y + s * 0.62f), 1.4f * u, StrokeCap.Round)
        }
        drawLine(CARBON.copy(alpha = 0.6f), Offset(c.x + s * 0.88f, c.y), Offset(c.x + s * 1.05f, c.y + s * 0.25f), 1f * u, StrokeCap.Round)
    }

    /** El contorno de una laja en el rectángulo [r]: el borde desportillado, siempre el mismo para cada [semilla]. */
    private fun lajaPath(r: Rect, semilla: Int): Path = Path().apply {
        val puntos = 18
        for (k in 0 until puntos) {
            val t = k / puntos.toFloat()
            // Un punto del perímetro del rectángulo, empujado hacia adentro un poco al azar.
            val (x, y) = when {
                t < 0.25f -> r.left + r.width * (t / 0.25f) to r.top
                t < 0.5f -> r.right to r.top + r.height * ((t - 0.25f) / 0.25f)
                t < 0.75f -> r.right - r.width * ((t - 0.5f) / 0.25f) to r.bottom
                else -> r.left to r.bottom - r.height * ((t - 0.75f) / 0.25f)
            }
            val adentro = minOf(r.width, r.height) * (0.015f + 0.05f * azar(semilla, k)) * (if (k % 4 == 0) 1.8f else 1f)
            val px = x + (if (x <= r.left + 0.5f) adentro else if (x >= r.right - 0.5f) -adentro else 0f)
            val py = y + (if (y <= r.top + 0.5f) adentro else if (y >= r.bottom - 0.5f) -adentro else 0f)
            if (k == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val hueco = Rect(w * 0.08f, h * 0.04f, w * 0.92f, h * 0.66f)
        if (c.vacio) {
            // Un lugar todavía sin nicho: apenas un hundimiento en la roca y unas muescas de ocre.
            drawOval(roca(ROCA_OSC, oscuro).copy(alpha = 0.12f), Offset(hueco.left + hueco.width * 0.15f, hueco.top + hueco.height * 0.3f), Size(hueco.width * 0.7f, hueco.height * 0.5f))
            for (k in 0 until 4) drawLine(OCRE.copy(alpha = 0.45f), Offset(w * (0.4f + 0.06f * k), h * 0.78f), Offset(w * (0.41f + 0.06f * k), h * 0.88f), 2 * u, StrokeCap.Round)
            return
        }
        // La boca del nicho: un arco tallado, irregular.
        val boca = Path().apply {
            moveTo(hueco.left + hueco.width * 0.04f, hueco.bottom)
            val pasos = 16
            for (k in 1 until pasos) {
                val a = Math.PI * (1.0 - k / pasos.toDouble())
                val ruido = 1f + (azar(c.semilla, k) - 0.5f) * 0.12f
                lineTo(hueco.center.x + (hueco.width / 2 * ruido * cos(a)).toFloat(), hueco.bottom - (hueco.height * 0.98f * ruido * sin(a)).toFloat().coerceAtLeast(0f))
            }
            lineTo(hueco.right - hueco.width * 0.04f, hueco.bottom)
            quadraticTo(hueco.center.x, hueco.bottom + hueco.height * 0.05f, hueco.left + hueco.width * 0.04f, hueco.bottom)
            close()
        }
        // El borde: luz abajo (donde pega el día) y sombra arriba.
        drawPath(boca, roca(ROCA_LUZ, oscuro), style = Stroke(5 * u))
        drawPath(boca, roca(ROCA_OSC, oscuro), style = Stroke(2 * u))
        drawPath(boca, Brush.radialGradient(listOf(roca(Color(0xFF6A5642), oscuro), roca(ROCA_HONDA, oscuro)), Offset(hueco.center.x, hueco.bottom), hueco.height * 1.1f))
        drawPath(boca, Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), hueco.top, hueco.top + hueco.height * 0.5f))
        // La luz del fuego cuando se abre.
        val ab = c.abierto.coerceIn(0f, 1f)
        if (ab > 0f) mancha(Offset(hueco.center.x, hueco.bottom - hueco.height * 0.1f), hueco.width * 0.7f, FUEGO.copy(alpha = 0.55f * ab))
        // Las lajas apoyadas contra el fondo, de atrás hacia adelante.
        val k = c.n.coerceIn(0, 4)
        for (i in 0 until k) {
            val ancho = hueco.width * (0.2f + 0.05f * azar(c.semilla, i + 50))
            val alto = hueco.height * (0.42f + 0.18f * azar(c.semilla, i + 60)) * (1f + 0.06f * ab)
            val x = hueco.left + hueco.width * (0.16f + 0.62f * (i + 0.5f) / k) - ancho / 2
            val r = Rect(x, hueco.bottom - alto - 2 * u, x + ancho, hueco.bottom - 2 * u)
            rotate(-10f + 20f * azar(c.semilla, i + 70), Offset(r.center.x, r.bottom)) {
                drawPath(lajaPath(r, c.semilla + i), mezcla(mezcla(CALIZA, ROCA, 0.2f * (k - 1 - i)), Color(0xFF1E150E), if (oscuro) 0.28f else 0f))
                drawPath(lajaPath(r, c.semilla + i), roca(ROCA_OSC, oscuro).copy(alpha = 0.6f), style = Stroke(0.8f * u))
                drawLine(OCRE.copy(alpha = 0.7f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.3f), Offset(r.left + r.width * 0.7f, r.top + r.height * 0.32f), 1.2f * u, StrokeCap.Round)
                drawLine(CARBON.copy(alpha = 0.5f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.48f), Offset(r.left + r.width * 0.55f, r.top + r.height * 0.5f), 1f * u, StrokeCap.Round)
            }
        }
        // Ceniza y polvo en el piso del nicho.
        drawOval(Color(0xFF9C8E80).copy(alpha = 0.35f), Offset(hueco.left + hueco.width * 0.12f, hueco.bottom - 4 * u), Size(hueco.width * 0.76f, 6 * u))
        // La piedra lisa donde va el nombre pintado, y dos puntos de ocre a los lados.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        mancha(r.center, r.width * 0.55f, roca(ROCA_LUZ, oscuro).copy(alpha = 0.55f))
        listOf(r.left + 3 * u, r.right - 3 * u).forEach { x -> drawCircle(OCRE.copy(alpha = 0.75f), 2 * u, Offset(x, r.top + r.height * 0.35f)) }
    }

    override val rotulo = Rect(0.06f, 0.69f, 0.94f, 0.99f)
    override fun tintaRotulo(oscuro: Boolean) = if (oscuro) Color(0xFFF0A070) else Color(0xFF8C2F14)
    override val letraRotulo: FontFamily = LetraExtra.abrigo
    override val salida = 0f
    override val placa = Rect(0.06f, 0.42f, 0.42f, 0.9f)
    override fun tintaPlaca(oscuro: Boolean) = Color(0xFF8C2F14)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del nicho: piedra con tierra, ceniza, manchas de ocre y trozos de carbón.
        drawRect(Brush.verticalGradient(listOf(roca(Color(0xFF5A4838), oscuro), roca(Color(0xFF7A6550), oscuro), roca(Color(0xFF5E4B3A), oscuro))))
        for (k in 0 until 140) {
            val c = Offset(w * azar(81, k), h * azar(82, k))
            mancha(c, (8f + 30f * azar(83, k)) * u, (if (k % 4 == 0) roca(ROCA_LUZ, oscuro) else roca(ROCA_HONDA, oscuro)).copy(alpha = 0.12f))
        }
        for (k in 0 until 60) {
            val c = Offset(w * azar(84, k), h * azar(85, k)); val r = (1.2f + 3.2f * azar(86, k)) * u
            drawOval(roca(if (k % 3 == 0) ROCA_LUZ else ROCA_OSC, oscuro).copy(alpha = 0.7f), Offset(c.x - r, c.y - r * 0.7f), Size(2 * r, 1.4f * r))
        }
        for (k in 0 until 6) mancha(Offset(w * azar(87, k), h * azar(88, k)), (16f + 20f * azar(89, k)) * u, OCRE.copy(alpha = 0.18f))
        for (k in 0 until 8) {
            val c = Offset(w * azar(90, k), h * azar(91, k))
            rotate(azar(92, k) * 180f, c) { drawRoundRect(CARBON.copy(alpha = 0.8f), Offset(c.x - 7 * u, c.y - 1.6f * u), Size(14 * u, 3.2f * u), androidx.compose.ui.geometry.CornerRadius(1.6f * u)) }
        }
        // Las paredes de roca alrededor, en sombra, y la luz del fuego que entra desde abajo.
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.6f)), Offset(w / 2, h * 0.55f), maxOf(w, h) * 0.75f))
        mancha(Offset(w / 2, h * 1.05f), w * 0.9f, FUEGO.copy(alpha = if (oscuro) 0.3f else 0.2f))
        manoNegativa(Offset(w * 0.1f, h * 0.06f), 14 * u, -20f, true)
        manoNegativa(Offset(w * 0.9f, h * 0.08f), 12 * u, 25f, true)
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFF2E6CF)
    override val pared: Dp = 10.dp
    override val columnas = 2
    override val lineas = 4

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(lajaPath(Rect(0f, 0f, s.width, s.height), semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        // La luz de lado: más clara arriba a la izquierda, más oscura abajo a la derecha, como una piedra con volumen.
        drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.14f), Color.Transparent, Color.Black.copy(alpha = 0.14f)), Offset.Zero, Offset(w, h)))
        // Poros y granos de la caliza.
        val n = if (grande) 110 else 34
        for (k in 0 until n) drawCircle(ROCA_OSC.copy(alpha = 0.12f + 0.12f * azar(semilla, k)), (0.5f + 1.3f * azar(semilla, k + 1000)) * u, Offset(w * azar(semilla, k + 2000), h * azar(semilla, k + 3000)))
        // Dos vetas.
        for (k in 0 until 2) {
            val y0 = h * (0.3f + 0.4f * azar(semilla, k + 4000))
            val pth = Path().apply { moveTo(0f, y0); quadraticTo(w * 0.5f, y0 + h * (azar(semilla, k + 4100) - 0.5f) * 0.3f, w, y0 + h * 0.1f) }
            drawPath(pth, ROCA_OSC.copy(alpha = 0.16f), style = Stroke(0.8f * u * (if (grande) 1.5f else 1f)))
        }
        // Una mancha de ocre y unas muescas de carbón abajo a la izquierda, como quien llevó la cuenta.
        mancha(Offset(w * 0.08f, h * 0.9f), minOf(w, h) * 0.22f, OCRE.copy(alpha = 0.22f))
        val g = if (grande) 1.6f else 1f
        for (k in 0 until 4) drawLine(CARBON.copy(alpha = 0.45f), Offset(w * 0.06f + k * 4 * u * g, h - 12 * u * g), Offset(w * 0.06f + k * 4 * u * g + 1 * u, h - 6 * u * g), 1.1f * u * g, StrokeCap.Round)
    }

    override val margenes = MargenesFicha(9.dp, 13.dp, 14.dp, 20.dp, 28.dp, 34.dp)
    override val proporcionFicha = 1.12f
    override val anchoGrande = 1f
    override val proporcionGrande = 0.95f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un punto de ocre con su anillo.
            "convence" -> { drawCircle(OCRE, s * 0.24f, c); drawCircle(OCRE, s * 0.42f, c, style = Stroke(s * 0.1f)) }
            // Tres puntos: lo estás pensando.
            "piensa" -> listOf(Offset(0f, -0.28f), Offset(-0.28f, 0.22f), Offset(0.28f, 0.22f)).forEach { o -> drawCircle(OCRE_AMARILLO, s * 0.15f, Offset(c.x + o.x * s, c.y + o.y * s)) }
            // Un círculo de carbón sin rellenar.
            "duda" -> drawCircle(CARBON.copy(alpha = 0.8f), s * 0.34f, c, style = Stroke(s * 0.12f))
            // Dos trazos de carbón cruzados.
            "desacuerdo" -> { drawLine(CARBON, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Offset(c.x + s * 0.36f, c.y + s * 0.36f), s * 0.15f, StrokeCap.Round); drawLine(CARBON, Offset(c.x + s * 0.36f, c.y - s * 0.36f), Offset(c.x - s * 0.36f, c.y + s * 0.36f), s * 0.15f, StrokeCap.Round) }
            else -> {}
        }
    }

    /** Una mano pintada con ocre: la palma y los dedos. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = Offset(center.x, center.y + s * 0.12f)
        drawOval(OCRE, Offset(c.x - s * 0.24f, c.y - s * 0.12f), Size(s * 0.48f, s * 0.42f))
        listOf(-0.18f to 0.3f, -0.06f to 0.4f, 0.06f to 0.42f, 0.17f to 0.34f).forEach { (dx, l) ->
            drawLine(OCRE, Offset(c.x + s * dx, c.y), Offset(c.x + s * dx * 1.2f, c.y - s * l), s * 0.1f, StrokeCap.Round)
        }
        drawLine(OCRE, Offset(c.x - s * 0.2f, c.y + s * 0.08f), Offset(c.x - s * 0.42f, c.y - s * 0.06f), s * 0.11f, StrokeCap.Round)
    }

    override val desorden = 2f
}
