package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.pantallas.simboloCalificacion
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
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
            // La sombra de la piedra, corrida hacia abajo, y la piedra encima.
            drawPath(lajaPath(r.translate(1.5f * u, 2.5f * u), 9), Color.Black.copy(alpha = 0.3f))
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
            // Cuatro muescas en el hueso: lo contado.
            "hecho" -> { for (k in -1..1) drawLine(CARBON, Offset(c.x + k * s * 0.2f, c.y - s * 0.34f), Offset(c.x + k * s * 0.2f, c.y + s * 0.34f), s * 0.1f, StrokeCap.Round); drawLine(OCRE, Offset(c.x - s * 0.4f, c.y + s * 0.22f), Offset(c.x + s * 0.4f, c.y - s * 0.22f), s * 0.11f, StrokeCap.Round) }
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

/**
 * Roma: el armarium, el armario de nogal de las bibliotecas romanas, con su frontón y una corona de laurel, las puertas
 * abiertas a los lados y el fondo pintado de rojo pompeyano. En cada anaquel hay una capsa de cuero con aros de bronce, con
 * los rollos asomando (más cuanto más llena) y su tabula ansata de bronce con el tema. Por dentro, una pared al fresco.
 * Las fichas son rollos: la hoja de papiro entre sus dos varillas con perillas.
 */
object IdeasRomano : DisenoIdeas {
    private val NOGAL = Color(0xFF5E3C22)
    private val NOGAL_LUZ = Color(0xFF8C6340)
    private val NOGAL_OSC = Color(0xFF331F10)
    private val POMPEYANO = Color(0xFF8E2B1F)
    private val NEGRO = Color(0xFF1E1714)
    private val FILETE = Color(0xFFC9A54A)
    private val ORO = Color(0xFFD4AF37)
    private val ORO_OSC = Color(0xFF8E6A1F)
    private val BRONCE = Color(0xFFD9B26A)
    private val BRONCE_OSC = Color(0xFF7A5524)
    private val CUERO = Color(0xFF7A4A28)
    private val CUERO_OSC = Color(0xFF45280F)
    private val CUERO_LUZ = Color(0xFFA8703F)
    private val PAPIRO = Color(0xFFE9D9A9)
    private val PAPIRO_OSC = Color(0xFFB59B62)
    private val LAUREL = Color(0xFF5E7A3A)
    private val LACRE = Color(0xFFA3172E)
    private val TINTA = Color(0xFF2A1A0C)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 0.9f
    override val corona = 0.22f
    override val base = 0.11f
    override val lado = 0.1f
    override val hueco = 0.035f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val cuerpo = Rect(w * 0.055f, d.corona.bottom, w * 0.945f, d.base.top + d.base.height * 0.3f)
        // El fondo pintado: rojo pompeyano con un filete amarillo alrededor de cada capsa y el zócalo negro.
        drawRect(Brush.verticalGradient(listOf(t(mezcla(POMPEYANO, Color.Black, 0.15f), oscuro), t(POMPEYANO, oscuro), t(mezcla(POMPEYANO, Color.Black, 0.2f), oscuro)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        for (k in 0 until 40) mancha(Offset(cuerpo.left + cuerpo.width * azar(3, k), cuerpo.top + cuerpo.height * azar(4, k)), (6f + 14f * azar(5, k)) * u, Color.Black.copy(alpha = 0.06f))
        d.huecos.forEach { r -> drawRect(t(FILETE, oscuro).copy(alpha = 0.7f), Offset(r.left - 3 * u, r.top - 3 * u), Size(r.width + 6 * u, r.height + 3 * u), style = Stroke(0.9f * u)) }
        drawRect(t(NEGRO, oscuro), Offset(cuerpo.left, cuerpo.bottom - d.separacion * 0.9f), Size(cuerpo.width, d.separacion * 0.9f))
        // Los anaqueles, bajo cada fila, con su canto claro y la sombra.
        d.filas.forEach { f ->
            val r = Rect(cuerpo.left, f.bottom, cuerpo.right, f.bottom + maxOf(d.separacion * 0.5f, 5 * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), r.top, r.bottom), r.topLeft, r.size)
            drawLine(Color.White.copy(alpha = 0.18f), Offset(r.left, r.top), Offset(r.right, r.top), 1f * u)
        }
        // Los montantes y las puertas abiertas, de lado, con su tablero y su argolla.
        val poste = w * 0.04f
        listOf(cuerpo.left to 1f, cuerpo.right to -1f).forEach { (x, s) ->
            val r = if (s > 0) Rect(x, cuerpo.top, x + poste, cuerpo.bottom) else Rect(x - poste, cuerpo.top, x, cuerpo.bottom)
            drawRect(Brush.horizontalGradient(listOf(t(NOGAL_OSC, oscuro), t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), r.left, r.right), r.topLeft, r.size)
            vetas(r, NOGAL_OSC.copy(alpha = 0.35f), if (s > 0) 7 else 8, 1f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
            val px = if (s > 0) x else x
            val ancho = w * 0.055f
            val puerta = Path().apply {
                moveTo(px, cuerpo.top + 4 * u); lineTo(px - s * ancho, cuerpo.top - 6 * u); lineTo(px - s * ancho, cuerpo.bottom + 6 * u); lineTo(px, cuerpo.bottom - 4 * u); close()
            }
            drawPath(puerta, Brush.horizontalGradient(listOf(t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), px - ancho, px + ancho))
            val tablero = Path().apply {
                moveTo(px - s * ancho * 0.25f, cuerpo.top + 10 * u); lineTo(px - s * ancho * 0.75f, cuerpo.top + 4 * u)
                lineTo(px - s * ancho * 0.75f, cuerpo.bottom - 2 * u); lineTo(px - s * ancho * 0.25f, cuerpo.bottom - 10 * u); close()
            }
            drawPath(tablero, Color.Black.copy(alpha = 0.25f), style = Stroke(1f * u))
            drawCircle(t(BRONCE, oscuro), 2.6f * u, Offset(px - s * ancho * 0.5f, (cuerpo.top + cuerpo.bottom) / 2), style = Stroke(1.2f * u))
        }
        // El entablamento con sus dentículos y el frontón con su corona de laurel; acroteras en las puntas.
        val ch = d.corona.height
        val arq = Rect(w * 0.02f, d.corona.bottom - ch * 0.3f, w * 0.98f, d.corona.bottom)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(arq.left + 3 * u, arq.bottom), Size(arq.width - 6 * u, 3 * u))
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), arq.top, arq.bottom), arq.topLeft, arq.size)
        val dent = (arq.width / (7 * u)).toInt()
        for (k in 0 until dent) drawRect(t(NOGAL_OSC, oscuro), Offset(arq.left + k * arq.width / dent + 1.5f * u, arq.top + arq.height * 0.55f), Size(arq.width / dent - 3 * u, arq.height * 0.3f))
        val frontonBase = arq.top
        val apice = Offset(w / 2, d.corona.top + ch * 0.1f)
        val fronton = Path().apply { moveTo(w * 0.04f, frontonBase); lineTo(apice.x, apice.y); lineTo(w * 0.96f, frontonBase); close() }
        drawPath(fronton, Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro)), apice.y, frontonBase))
        val timpano = Path().apply { moveTo(w * 0.12f, frontonBase - 2 * u); lineTo(apice.x, apice.y + ch * 0.16f); lineTo(w * 0.88f, frontonBase - 2 * u); close() }
        drawPath(timpano, t(NOGAL_OSC, oscuro))
        drawPath(fronton, Color.White.copy(alpha = 0.15f), style = Stroke(1.2f * u))
        laurel(Offset(w / 2, frontonBase - (frontonBase - apice.y) * 0.36f), (frontonBase - apice.y) * 0.24f, t(ORO, oscuro))
        listOf(Offset(w * 0.04f, frontonBase), apice, Offset(w * 0.96f, frontonBase)).forEach { o -> palmeta(o, ch * 0.13f, t(ORO, oscuro)) }
        // El zócalo moldurado, con las patas de garra a los lados.
        val zocalo = Rect(w * 0.035f, cuerpo.bottom, w * 0.965f, d.base.top + d.base.height * 0.78f)
        drawRect(Brush.verticalGradient(listOf(t(NOGAL_LUZ, oscuro), t(NOGAL, oscuro), t(NOGAL_OSC, oscuro)), zocalo.top, zocalo.bottom), zocalo.topLeft, zocalo.size)
        drawLine(Color.White.copy(alpha = 0.18f), zocalo.topLeft, Offset(zocalo.right, zocalo.top), 1f * u)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(zocalo.left, zocalo.top + 3 * u), Offset(zocalo.right, zocalo.top + 3 * u), 0.8f * u)
        listOf(zocalo.left + 10 * u, zocalo.right - 10 * u).forEach { x ->
            val y = zocalo.bottom
            drawOval(t(NOGAL_OSC, oscuro), Offset(x - 9 * u, y - 4 * u), Size(18 * u, 10 * u))
            for (k in 0 until 3) drawCircle(t(NOGAL, oscuro), 2.6f * u, Offset(x - 5 * u + k * 5 * u, y + 4 * u))
        }
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            tabulaAnsata(r, oscuro)
        }
    }

    /** Una corona de laurel: dos ramas de hojas en círculo, abiertas arriba, y la cinta abajo. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color) {
        for (lado in listOf(-1f, 1f)) for (k in 0 until 7) {
            val ang = (if (lado > 0) 100f + 22f * k else 80f - 22f * k)
            val rad = Math.toRadians(ang.toDouble())
            val p = Offset(c.x + r * kotlin.math.cos(rad).toFloat(), c.y + r * kotlin.math.sin(rad).toFloat())
            rotate(ang + 90f, p) { drawOval(color, Offset(p.x - r * 0.28f, p.y - r * 0.11f), Size(r * 0.56f, r * 0.22f)) }
        }
        drawLine(LACRE, Offset(c.x - r * 0.2f, c.y + r * 1.05f), Offset(c.x - r * 0.45f, c.y + r * 1.5f), r * 0.12f, StrokeCap.Round)
        drawLine(LACRE, Offset(c.x + r * 0.2f, c.y + r * 1.05f), Offset(c.x + r * 0.45f, c.y + r * 1.5f), r * 0.12f, StrokeCap.Round)
    }

    /** Una acrotera en forma de palmeta: un abanico de hojas sobre la punta. */
    private fun DrawScope.palmeta(o: Offset, r: Float, color: Color) {
        for (k in -2..2) {
            val ang = Math.toRadians(-90.0 + k * 24.0)
            drawLine(color, o, Offset(o.x + r * kotlin.math.cos(ang).toFloat(), o.y + r * kotlin.math.sin(ang).toFloat()), r * 0.2f, StrokeCap.Round)
        }
        drawCircle(color, r * 0.22f, o)
    }

    /** La tabula ansata: una placa de bronce con dos asas en cola de milano a los lados y sus clavos. */
    private fun DrawScope.tabulaAnsata(r: Rect, oscuro: Boolean) {
        val u = density
        val a = r.height * 0.55f
        val forma = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            lineTo(r.right + a, r.top - r.height * 0.05f); lineTo(r.right + a * 0.55f, r.center.y); lineTo(r.right + a, r.bottom + r.height * 0.05f)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom)
            lineTo(r.left - a, r.bottom + r.height * 0.05f); lineTo(r.left - a * 0.55f, r.center.y); lineTo(r.left - a, r.top - r.height * 0.05f)
            close()
        }
        drawPath(forma, Color.Black.copy(alpha = 0.3f), style = Stroke(2.5f * u))
        drawPath(forma, Brush.verticalGradient(listOf(t(mezcla(BRONCE, Color.White, 0.25f), oscuro), t(BRONCE, oscuro), t(mezcla(BRONCE, BRONCE_OSC, 0.4f), oscuro)), r.top, r.bottom))
        drawRect(t(BRONCE_OSC, oscuro).copy(alpha = 0.7f), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        listOf(r.left - a * 0.72f, r.right + a * 0.72f).forEach { x -> drawCircle(t(BRONCE_OSC, oscuro), 1.3f * u, Offset(x, r.center.y)) }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre del anaquel: una lucerna de bronce encendida.
            val o = Offset(w / 2, h * 0.9f)
            drawOval(t(BRONCE, oscuro), Offset(o.x - w * 0.2f, o.y - h * 0.06f), Size(w * 0.4f, h * 0.1f))
            drawLine(t(BRONCE, oscuro), Offset(o.x + w * 0.18f, o.y - h * 0.02f), Offset(o.x + w * 0.28f, o.y - h * 0.06f), 3 * u, StrokeCap.Round)
            mancha(Offset(o.x - w * 0.22f, o.y - h * 0.12f), w * 0.2f, Color(0xFFF4C76E).copy(alpha = 0.5f))
            drawOval(Color(0xFFF4C76E), Offset(o.x - w * 0.25f, o.y - h * 0.17f), Size(w * 0.05f, h * 0.1f))
            return
        }
        val x0 = w * 0.13f; val x1 = w * 0.87f
        val eh = (x1 - x0) * 0.3f
        val arriba = h * 0.34f; val abajo = h * 0.98f - eh / 2
        // La tapa echada hacia atrás (más al abrirse).
        val subeTapa = ab * h * 0.08f
        drawOval(t(CUERO_OSC, oscuro), Offset(x0 - 2 * u, arriba - eh * 1.75f - subeTapa), Size(x1 - x0 + 4 * u, eh * 1.3f))
        drawOval(t(BRONCE, oscuro), Offset(x0 - 2 * u, arriba - eh * 1.75f - subeTapa), Size(x1 - x0 + 4 * u, eh * 1.3f), style = Stroke(1.4f * u))
        // La boca, oscura, y los rollos de pie, que suben al abrirse.
        drawOval(Color(0xFF1C0F06), Offset(x0, arriba - eh / 2), Size(x1 - x0, eh))
        val k = c.n.coerceIn(0, 6)
        for (i in 0 until k) {
            val fila = i % 2
            val cx = x0 + (x1 - x0) * (0.2f + 0.6f * ((i / 2) + 0.5f * fila + 0.25f) / 3f)
            val rr = (x1 - x0) * 0.09f
            val sube = h * (0.05f + 0.05f * azar(c.semilla, i)) + ab * h * 0.12f
            val y = arriba - eh * 0.1f + fila * eh * 0.25f
            drawRect(t(PAPIRO_OSC, oscuro), Offset(cx - rr, y - sube), Size(rr * 2, sube))
            drawOval(t(PAPIRO, oscuro), Offset(cx - rr, y - sube - rr * 0.45f), Size(rr * 2, rr * 0.9f))
            drawOval(t(PAPIRO_OSC, oscuro), Offset(cx - rr * 0.6f, y - sube - rr * 0.27f), Size(rr * 1.2f, rr * 0.54f), style = Stroke(0.7f * u))
            drawCircle(t(NOGAL_OSC, oscuro), rr * 0.3f, Offset(cx, y - sube))
        }
        // El cilindro de cuero, con su luz, el borde de la boca y los aros de bronce.
        val cuerpo = Rect(x0, arriba, x1, abajo)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x0 - 2 * u, abajo - eh * 0.3f), Size(x1 - x0 + 6 * u, eh))
        drawOval(t(CUERO_OSC, oscuro), Offset(x0, abajo - eh / 2), Size(x1 - x0, eh))
        drawRect(Brush.horizontalGradient(listOf(t(CUERO_OSC, oscuro), t(CUERO, oscuro), t(CUERO_LUZ, oscuro), t(CUERO, oscuro), t(CUERO_OSC, oscuro)), x0, x1), cuerpo.topLeft, cuerpo.size)
        drawArc(t(CUERO_LUZ, oscuro), 0f, 180f, false, Offset(x0, arriba - eh / 2), Size(x1 - x0, eh), style = Stroke(2.2f * u))
        listOf(arriba + (abajo - arriba) * 0.1f, abajo - (abajo - arriba) * 0.08f).forEach { y ->
            drawArc(t(BRONCE_OSC, oscuro), 0f, 180f, false, Offset(x0, y - eh / 2), Size(x1 - x0, eh), style = Stroke(4 * u))
            drawArc(t(BRONCE, oscuro), 0f, 180f, false, Offset(x0, y - eh / 2 - 1 * u), Size(x1 - x0, eh), style = Stroke(1.6f * u))
        }
        // Costuras del cuero, a los lados.
        listOf(x0 + (x1 - x0) * 0.08f, x1 - (x1 - x0) * 0.08f).forEach { x ->
            var y = arriba + eh * 0.6f
            while (y < abajo - eh * 0.3f) { drawLine(t(CUERO_LUZ, oscuro).copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 2 * u), 0.8f * u); y += 4 * u }
        }
        // La placa con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        tabulaAnsata(r, oscuro)
    }

    override val rotulo = Rect(0.24f, 0.54f, 0.76f, 0.78f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.inscripcion
    override val salida = 0f
    override val placa = Rect(0.33f, 0.12f, 0.67f, 0.62f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una pared al fresco: el campo rojo, el friso negro arriba, el zócalo negro abajo, filetes amarillos y candelabros finos.
        drawRect(Brush.verticalGradient(listOf(t(POMPEYANO, oscuro), t(mezcla(POMPEYANO, Color.Black, 0.18f), oscuro))))
        for (k in 0 until 90) mancha(Offset(w * azar(31, k), h * azar(32, k)), (10f + 30f * azar(33, k)) * u, (if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.05f))
        val friso = h * 0.07f; val zocalo = h * 0.09f
        drawRect(t(NEGRO, oscuro), Offset.Zero, Size(w, friso))
        drawRect(t(NEGRO, oscuro), Offset(0f, h - zocalo), Size(w, zocalo))
        // Una greca de ovas en el friso.
        var x = 6 * u
        while (x < w) { drawOval(t(FILETE, oscuro).copy(alpha = 0.6f), Offset(x, friso * 0.35f), Size(5 * u, friso * 0.4f), style = Stroke(0.8f * u)); x += 9 * u }
        drawLine(t(FILETE, oscuro), Offset(0f, friso), Offset(w, friso), 1.2f * u)
        drawLine(t(FILETE, oscuro), Offset(0f, h - zocalo), Offset(w, h - zocalo), 1.2f * u)
        val m = 8 * u
        drawRect(t(FILETE, oscuro).copy(alpha = 0.8f), Offset(m, friso + m), Size(w - 2 * m, h - friso - zocalo - 2 * m), style = Stroke(1f * u))
        listOf(m + 3 * u, w - m - 3 * u).forEach { cx ->
            drawLine(t(FILETE, oscuro).copy(alpha = 0.6f), Offset(cx, friso + m), Offset(cx, h - zocalo - m), 1f * u)
            var y = friso + m + 30 * u
            while (y < h - zocalo - m - 10 * u) { drawOval(t(FILETE, oscuro).copy(alpha = 0.7f), Offset(cx - 3 * u, y), Size(6 * u, 3 * u)); y += 70 * u }
        }
        // En el zócalo, pinceladas de mármol fingido.
        for (k in 0 until 12) {
            val y = h - zocalo + zocalo * azar(41, k)
            drawLine(Color.White.copy(alpha = 0.08f), Offset(w * azar(42, k), y), Offset(w * azar(42, k) + 30 * u, y + 6 * u), 1f * u)
        }
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFF3E6C8)
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    /** El grueso de las varillas del rollo, en px, según el tamaño de la ficha. */
    private fun varilla(s: Size) = (minOf(s.width, s.height) * 0.065f).coerceIn(22f, 56f)

    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val v = varilla(s)
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, 0f, s.width, v), androidx.compose.ui.geometry.CornerRadius(v / 2)))
        addRect(Rect(v * 0.55f, v * 0.5f, s.width - v * 0.55f, s.height - v * 0.5f))
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, s.height - v, s.width, s.height), androidx.compose.ui.geometry.CornerRadius(v / 2)))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val v = varilla(size)
        // Las fibras del papiro: tiras horizontales y, debajo, las verticales; y una junta entre hojas.
        var y = v; while (y < h - v) { drawLine(PAPIRO_OSC.copy(alpha = 0.12f + 0.08f * azar(semilla, y.toInt())), Offset(v * 0.55f, y), Offset(w - v * 0.55f, y), 0.6f * u); y += 2.6f * u }
        var x = v; while (x < w - v) { drawLine(PAPIRO_OSC.copy(alpha = 0.06f), Offset(x, v), Offset(x, h - v), 0.5f * u); x += 4.5f * u }
        val junta = w * (0.3f + 0.4f * azar(semilla, 7))
        drawLine(PAPIRO_OSC.copy(alpha = 0.3f), Offset(junta, v), Offset(junta, h - v), 1.2f * u)
        // La sombra donde la hoja se enrolla en cada varilla.
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), v * 0.5f, v * 0.5f + 10 * u), Offset(0f, v * 0.5f), Size(w, 10 * u))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)), h - v * 0.5f - 10 * u, h - v * 0.5f), Offset(0f, h - v * 0.5f - 10 * u), Size(w, 10 * u))
        // Las varillas (umbilici) de madera, con sus perillas de marfil.
        listOf(0f, h - v).forEach { y0 ->
            drawRoundRect(Brush.verticalGradient(listOf(NOGAL_LUZ, NOGAL, NOGAL_OSC), y0, y0 + v), Offset(v * 0.35f, y0 + v * 0.12f), Size(w - v * 0.7f, v * 0.76f), androidx.compose.ui.geometry.CornerRadius(v * 0.38f))
            listOf(v * 0.5f, w - v * 0.5f).forEach { cx ->
                drawCircle(Color(0xFFEDE3CC), v * 0.5f, Offset(cx, y0 + v / 2))
                drawCircle(Color(0xFFB8A784), v * 0.5f, Offset(cx, y0 + v / 2), style = Stroke(1f * u))
                drawCircle(Color.White.copy(alpha = 0.6f), v * 0.14f, Offset(cx - v * 0.15f, y0 + v * 0.35f))
            }
        }
    }

    override val margenes = MargenesFicha(18.dp, 16.dp, 18.dp, 32.dp, 32.dp, 36.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre de una tablilla sellada.
            "hecho" -> selloHecho(LACRE, p.color)
            // Una corona de laurel chica.
            "convence" -> laurel(Offset(c.x, c.y - s * 0.05f), s * 0.36f, LAUREL)
            // Tres interpuntos triangulares, como en las inscripciones.
            "piensa" -> listOf(-0.32f, 0f, 0.32f).forEach { dx ->
                val o = Offset(c.x + dx * s, c.y)
                drawPath(Path().apply { moveTo(o.x - s * 0.1f, o.y - s * 0.09f); lineTo(o.x + s * 0.1f, o.y - s * 0.09f); lineTo(o.x, o.y + s * 0.12f); close() }, TINTA)
            }
            "duda" -> drawArc(p.tenue, 30f, 300f, false, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Size(s * 0.68f, s * 0.68f), style = Stroke(s * 0.12f))
            // La theta nigrum: la marca de los caídos en las listas.
            "desacuerdo" -> { drawCircle(LACRE, s * 0.36f, c, style = Stroke(s * 0.12f)); drawLine(LACRE, Offset(c.x - s * 0.36f, c.y), Offset(c.x + s * 0.36f, c.y), s * 0.12f) }
            else -> {}
        }
    }

    /** Un sello de lacre rojo, con su borde corrido y una L. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        for (k in 0 until 8) drawCircle(LACRE, s * 0.36f, Offset(c.x + s * 0.06f * kotlin.math.cos(k * 0.785f), c.y + s * 0.06f * kotlin.math.sin(k * 0.785f)))
        drawCircle(mezcla(LACRE, Color.Black, 0.25f), s * 0.24f, c, style = Stroke(s * 0.06f))
    }

    override val desorden = 0f
}

/**
 * Grecia: la stoa. Un pórtico de mármol con columnas dóricas acanaladas, el entablamento con triglifos y metopas y antefijas
 * arriba, el estilóbato de tres escalones abajo y el muro ocre con su franja azul. Entre columna y columna, sobre tablas, un
 * ánfora de terracota por tema: barniz negro en el cuello y el pie, una greca en el hombro y el tema pintado en el vientre;
 * los óstraka se amontonan a su pie. Por dentro, un mosaico de guijarros. Las fichas son óstraka: tiestos de terracota.
 */
object IdeasHeleno : DisenoIdeas {
    private val MARMOL = Color(0xFFEDE6D6)
    private val MARMOL_OSC = Color(0xFFC9BFA8)
    private val MARMOL_SOMBRA = Color(0xFF9C927C)
    private val TERRACOTA = Color(0xFFC2652F)
    private val TERRA_OSC = Color(0xFF8A4220)
    private val TERRA_LUZ = Color(0xFFDE8A52)
    private val BARNIZ = Color(0xFF1A1410)
    private val EGEO = Color(0xFF2E5E8E)
    private val PURPURA = Color(0xFF5B2A6E)
    private val ORO = Color(0xFFC9A54A)
    private val MURO = Color(0xFFD9B97E)
    private val TABLA = Color(0xFF7A5232)
    private val GUIJARRO = Color(0xFFE9E1CF)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.32f) else c
    /** El mueble y sus ánforas (piloto de la luz): de noche, la penumbra de siempre, apenas fría. [t] sigue para lo de adentro. */
    private fun tm(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color(0xFF0D0C12), 0.32f) else c

    override val cols = 3
    override val proporcion = 0.62f
    override val corona = 0.21f
    override val base = 0.11f
    override val lado = 0.075f
    override val hueco = 0.075f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val alto0 = d.corona.bottom; val alto1 = d.base.top
        // El muro: ocre con una franja azul del Egeo abajo y otra púrpura fina.
        drawRect(Brush.verticalGradient(listOf(tm(mezcla(MURO, SOMBRA_MUEBLE, 0.12f), oscuro), tm(MURO, oscuro)), alto0, alto1), Offset(0f, alto0), Size(w, alto1 - alto0))
        // El revoque pintado a mano: pinceladas, no manchas con degradado (eran 50 por dibujo).
        pinceladas(Rect(0f, alto0, w, alto1), tm(mezcla(MURO, SOMBRA_MUEBLE, 0.35f), oscuro), 51, 46, u, 0.08f)
        pinceladas(Rect(0f, alto0, w, alto1), tm(mezcla(MURO, LUZ_MUEBLE, 0.4f), oscuro), 57, 24, u, 0.07f)
        drawRect(tm(EGEO, oscuro), Offset(0f, alto1 - 14 * u), Size(w, 14 * u))
        drawRect(tm(PURPURA, oscuro), Offset(0f, alto1 - 17 * u), Size(w, 3 * u))
        // Las tablas donde descansan las ánforas, bajo cada fila.
        d.filas.dropLast(1).forEach { f ->
            drawRect(sombraMueble(0.25f), Offset(0f, f.bottom + 4 * u), Size(w, 3 * u))
            drawRect(Brush.verticalGradient(listOf(tm(mezcla(TABLA, LUZ_MUEBLE, 0.15f), oscuro), tm(TABLA, oscuro)), f.bottom, f.bottom + 5 * u), Offset(0f, f.bottom), Size(w, 5 * u))
        }
        // Las columnas: a los lados y entre cada par de ánforas.
        val fila0 = d.huecos.take(cols)
        val xs = mutableListOf(d.huecos.first().left / 2)
        fila0.zipWithNext().forEach { (a, b) -> xs += (a.right + b.left) / 2 }
        xs += (fila0.last().right + w) / 2
        val cw = d.separacion * 0.62f
        // La sombra de cada columna en el muro, a su derecha: la luz viene de arriba a la izquierda.
        xs.forEach { x -> drawRect(sombraMueble(0.14f), Offset(x + cw / 2, alto0), Size(cw * 0.55f, alto1 - alto0 - 17 * u)) }
        xs.forEach { x -> columna(x, cw, alto0, alto1, oscuro) }
        // El entablamento: arquitrabe, friso de triglifos y metopas, cornisa y antefijas.
        val ch = d.corona.height
        val arq = Rect(0f, d.corona.bottom - ch * 0.26f, w, d.corona.bottom)
        drawRect(Brush.verticalGradient(listOf(tm(MARMOL, oscuro), tm(MARMOL_OSC, oscuro)), arq.top, arq.bottom), arq.topLeft, arq.size)
        drawLine(tm(MARMOL_SOMBRA, oscuro), Offset(0f, arq.top + arq.height * 0.25f), Offset(w, arq.top + arq.height * 0.25f), 0.8f * u)
        val friso = Rect(0f, arq.top - ch * 0.34f, w, arq.top)
        drawRect(tm(MARMOL, oscuro), friso.topLeft, friso.size)
        val n = 9
        for (k in 0 until n) {
            val x = w * k / n
            val tr = Rect(x + w / n * 0.05f, friso.top + 2 * u, x + w / n * 0.38f, friso.bottom - 1 * u)
            drawRect(tm(Color(0xFF263646), oscuro), tr.topLeft, tr.size)
            for (g in 1..2) drawLine(sombraMueble(0.5f), Offset(tr.left + tr.width * g / 3, tr.top + 2 * u), Offset(tr.left + tr.width * g / 3, tr.bottom), 1.4f * u)
            // En la metopa, un disco de terracota.
            drawCircle(tm(TERRACOTA, oscuro).copy(alpha = 0.8f), friso.height * 0.22f, Offset(x + w / n * 0.7f, friso.center.y))
        }
        val cornisa = Rect(-4 * u, friso.top - ch * 0.14f, w + 4 * u, friso.top)
        drawRect(sombraMueble(0.25f), Offset(0f, cornisa.bottom), Size(w, 2.5f * u))
        drawRect(Brush.verticalGradient(listOf(tm(MARMOL, oscuro), tm(MARMOL_SOMBRA, oscuro)), cornisa.top, cornisa.bottom), cornisa.topLeft, cornisa.size)
        // La greca pintada en la cornisa.
        greca(Rect(0f, cornisa.top + 1.5f * u, w, cornisa.bottom - 1.5f * u), tm(TERRACOTA, oscuro), u)
        // Antefijas: palmetas de terracota sobre el borde.
        val ant = (w / (26 * u)).toInt()
        for (k in 0 until ant) {
            val o = Offset(w * (k + 0.5f) / ant, cornisa.top)
            for (j in -2..2) {
                val a = Math.toRadians(-90.0 + j * 26.0)
                drawLine(tm(TERRACOTA, oscuro), o, Offset(o.x + ch * 0.14f * kotlin.math.cos(a).toFloat(), o.y + ch * 0.14f * kotlin.math.sin(a).toFloat()), 1.6f * u, StrokeCap.Round)
            }
        }
        // El estilóbato: tres escalones de mármol.
        for (k in 0 until 3) {
            val y = d.base.top + d.base.height * k / 3f
            val r = Rect(-k * 6 * u, y, w + k * 6 * u, y + d.base.height / 3f)
            drawRect(Brush.verticalGradient(listOf(tm(MARMOL, oscuro), tm(MARMOL_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
            drawLine(tm(MARMOL_SOMBRA, oscuro), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 1f * u)
            drawLine(luzMueble(0.5f), Offset(r.left, r.top + 0.5f * u), Offset(r.right, r.top + 0.5f * u), 0.8f * u)
        }
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawRect(tm(MARMOL_OSC, oscuro), r.topLeft, r.size)
            drawRect(tm(MARMOL_SOMBRA, oscuro), r.topLeft, r.size, style = Stroke(0.8f * u))
        }
        // El grano de papel, al final (regla 7).
        granoMueble(Rect(0f, 0f, w, size.height), oscuro)
    }

    /** Una columna dórica: el fuste acanalado de mármol, el equino y el ábaco. */
    private fun DrawScope.columna(x: Float, cw: Float, y0: Float, y1: Float, oscuro: Boolean) {
        val u = density
        val capitel = cw * 0.5f
        // El fuste por planos: la cara de la luz, crema; la de la sombra, a la derecha, con su color.
        drawRect(tm(MARMOL, oscuro), Offset(x - cw / 2, y0 + capitel), Size(cw, y1 - y0 - capitel))
        drawRect(luzMueble(if (oscuro) 0.08f else 0.4f), Offset(x - cw / 2, y0 + capitel), Size(cw * 0.32f, y1 - y0 - capitel))
        drawRect(sombraMueble(0.26f), Offset(x + cw * 0.12f, y0 + capitel), Size(cw * 0.38f, y1 - y0 - capitel))
        for (k in 1 until 5) drawLine(tm(MARMOL_SOMBRA, oscuro).copy(alpha = 0.6f), Offset(x - cw / 2 + cw * k / 5, y0 + capitel), Offset(x - cw / 2 + cw * k / 5, y1), 0.7f * u)
        drawPath(Path().apply { moveTo(x - cw / 2, y0 + capitel); lineTo(x - cw * 0.8f, y0 + capitel * 0.35f); lineTo(x + cw * 0.8f, y0 + capitel * 0.35f); lineTo(x + cw / 2, y0 + capitel); close() }, tm(MARMOL_OSC, oscuro))
        drawRect(tm(MARMOL, oscuro), Offset(x - cw * 0.9f, y0), Size(cw * 1.8f, capitel * 0.35f))
        drawLine(tm(MARMOL_SOMBRA, oscuro), Offset(x - cw * 0.9f, y0 + capitel * 0.35f), Offset(x + cw * 0.9f, y0 + capitel * 0.35f), 0.8f * u)
    }

    /** Una greca (meandro) que corre a lo largo de [r]. */
    private fun DrawScope.greca(r: Rect, color: Color, u: Float) {
        val a = r.height; var x = r.left
        val p = Path().apply { moveTo(x, r.bottom) }
        while (x < r.right) {
            p.lineTo(x, r.top); p.lineTo(x + a * 0.8f, r.top); p.lineTo(x + a * 0.8f, r.top + a * 0.6f); p.lineTo(x + a * 0.4f, r.top + a * 0.6f)
            p.lineTo(x + a * 0.4f, r.top + a * 0.3f); p.moveTo(x + a * 0.4f, r.bottom); p.lineTo(x + a * 1.2f, r.bottom); x += a * 1.2f
        }
        drawPath(p, color, style = Stroke(maxOf(0.9f * u, a * 0.1f)))
    }

    /** El contorno del ánfora en el lienzo de [w] × [h]: boca, cuello, hombro ancho y vientre que baja al pie en punta. */
    private fun anfora(w: Float, h: Float): Path = Path().apply {
        val cx = w / 2
        moveTo(cx - w * 0.12f, h * 0.06f)
        lineTo(cx - w * 0.1f, h * 0.2f)
        cubicTo(cx - w * 0.4f, h * 0.26f, cx - w * 0.38f, h * 0.5f, cx - w * 0.3f, h * 0.6f)
        cubicTo(cx - w * 0.2f, h * 0.76f, cx - w * 0.08f, h * 0.84f, cx - w * 0.05f, h * 0.9f)
        lineTo(cx, h * 0.95f)
        lineTo(cx + w * 0.05f, h * 0.9f)
        cubicTo(cx + w * 0.08f, h * 0.84f, cx + w * 0.2f, h * 0.76f, cx + w * 0.3f, h * 0.6f)
        cubicTo(cx + w * 0.38f, h * 0.5f, cx + w * 0.4f, h * 0.26f, cx + w * 0.1f, h * 0.2f)
        lineTo(cx + w * 0.12f, h * 0.06f)
        close()
    }

    /** Un tiesto: un trozo de vasija, con tres a cinco bordes rectos. */
    private fun DrawScope.tiesto(c: Offset, r: Float, semilla: Int, color: Color) {
        val p = Path()
        val lados = 4 + semilla.mod(3)
        for (k in 0 until lados) {
            val a = 6.283f * k / lados + azar(semilla, k) * 0.8f
            val rr = r * (0.7f + 0.4f * azar(semilla, k + 9))
            val o = Offset(c.x + rr * kotlin.math.cos(a), c.y + rr * 0.6f * kotlin.math.sin(a))
            if (k == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
        }
        p.close()
        drawPath(p, color)
        drawPath(p, BARNIZ.copy(alpha = 0.5f), style = Stroke(0.8f * density))
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un kílix (la copa de beber) sobre la tabla.
            val y = h * 0.93f
            drawPath(Path().apply { moveTo(w * 0.25f, y - h * 0.08f); quadraticTo(w * 0.5f, y + h * 0.02f, w * 0.75f, y - h * 0.08f); close() }, tm(TERRACOTA, oscuro))
            drawLine(tm(BARNIZ, oscuro), Offset(w * 0.25f, y - h * 0.08f), Offset(w * 0.75f, y - h * 0.08f), 2 * u)
            drawRect(tm(TERRA_OSC, oscuro), Offset(w * 0.47f, y - h * 0.03f), Size(w * 0.06f, h * 0.04f))
            drawOval(tm(TERRA_OSC, oscuro), Offset(w * 0.4f, y), Size(w * 0.2f, h * 0.02f))
            return
        }
        // Los óstraka amontonados al pie, más cuanto más llena.
        val k = c.n.coerceIn(0, 6)
        for (i in 0 until k) tiesto(Offset(w * (0.18f + 0.64f * azar(c.semilla, i + 30)), h * (0.93f + 0.04f * azar(c.semilla, i + 40))), w * 0.09f, c.semilla + i,
            tm(mezcla(TERRACOTA, TERRA_LUZ, azar(c.semilla, i)), oscuro))
        // El aro donde se para.
        sombraContacto(Offset(w * 0.56f, h * 0.915f), w * 0.5f, h * 0.06f, 0.3f)
        drawOval(tm(Color(0xFF5E4630), oscuro), Offset(w * 0.36f, h * 0.88f), Size(w * 0.28f, h * 0.05f), style = Stroke(2.2f * u))
        val forma = anfora(w, h)
        // El cuerpo por luz: claro del lado de la ventana, la sombra del otro lado tirando a violeta, no a negro.
        drawPath(forma, Brush.horizontalGradient(listOf(tm(TERRACOTA, oscuro), tm(TERRA_LUZ, oscuro), tm(TERRACOTA, oscuro), tm(mezcla(TERRA_OSC, SOMBRA_MUEBLE, 0.25f), oscuro)), w * 0.12f, w * 0.88f))
        clipPath(forma) {
            // Barniz negro: el cuello, una banda bajo el hombro y el pie; la greca en el hombro, sobre terracota.
            drawRect(tm(BARNIZ, oscuro), Offset(0f, 0f), Size(w, h * 0.21f))
            greca(Rect(0f, h * 0.27f, w, h * 0.33f), tm(BARNIZ, oscuro), u)
            drawRect(tm(BARNIZ, oscuro), Offset(0f, h * 0.38f), Size(w, h * 0.025f))
            drawRect(tm(BARNIZ, oscuro), Offset(0f, h * 0.71f), Size(w, h * 0.3f))
            // Lengüetas en la base del barniz.
            for (j in 0 until 7) drawLine(tm(TERRACOTA, oscuro), Offset(w * (0.2f + 0.1f * j), h * 0.71f), Offset(w * (0.2f + 0.1f * j), h * 0.76f), 1.4f * u)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, luzMueble(0.22f), Color.Transparent), w * 0.28f, w * 0.44f), Offset(w * 0.28f, 0f), Size(w * 0.16f, h))
        }
        // La boca, con su labio.
        drawOval(tm(TERRA_OSC, oscuro), Offset(w * 0.36f, h * 0.045f), Size(w * 0.28f, h * 0.03f))
        drawOval(Color(0xFF120C08), Offset(w * 0.4f, h * 0.05f), Size(w * 0.2f, h * 0.018f))
        // Las asas, del cuello al hombro.
        listOf(-1f, 1f).forEach { s ->
            val asa = Path().apply { moveTo(w / 2 + s * w * 0.1f, h * 0.1f); cubicTo(w / 2 + s * w * 0.42f, h * 0.08f, w / 2 + s * w * 0.42f, h * 0.24f, w / 2 + s * w * 0.3f, h * 0.3f) }
            drawPath(asa, tm(TERRA_OSC, oscuro), style = Stroke(3.2f * u, cap = StrokeCap.Round))
            drawPath(asa, tm(TERRA_LUZ, oscuro).copy(alpha = 0.6f), style = Stroke(1f * u, cap = StrokeCap.Round))
        }
        // Al abrirse, los óstraka asoman por la boca.
        if (ab > 0f) for (i in 0 until 3) tiesto(Offset(w * (0.42f + 0.08f * i), h * (0.05f - 0.08f * ab) + i * 2 * u), w * 0.07f, c.semilla + 50 + i, tm(TERRA_LUZ, oscuro))
        // El panel del vientre donde va el nombre, enmarcado con un filete de barniz.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawLine(tm(BARNIZ, oscuro), Offset(r.left, r.top), Offset(r.right, r.top), 1f * u)
        drawLine(tm(BARNIZ, oscuro), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 1f * u)
    }

    override val rotulo = Rect(0.16f, 0.43f, 0.84f, 0.66f)
    override fun tintaRotulo(oscuro: Boolean) = BARNIZ
    override val letraRotulo: FontFamily = Letra.garamond
    override val salida = 0f
    override val placa = Rect(0.28f, 0.36f, 0.72f, 0.64f)
    override fun tintaPlaca(oscuro: Boolean) = BARNIZ

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un mosaico de guijarros, como los de Pela: fondo oscuro, guijarros sueltos y una greca de guijarros claros alrededor.
        drawRect(t(Color(0xFF2E2621), oscuro))
        val paso = 9 * u
        var y = paso / 2
        var fila = 0
        while (y < h) {
            var x = paso / 2 + (if (fila % 2 == 0) 0f else paso / 2)
            while (x < w) {
                val k = (x / paso).toInt() * 31 + fila
                val c = mezcla(Color(0xFF3A302A), Color(0xFF51463C), azar(61, k))
                drawOval(t(c, oscuro), Offset(x - paso * 0.42f, y - paso * 0.34f), Size(paso * 0.84f, paso * 0.68f))
                x += paso
            }
            y += paso * 0.86f; fila++
        }
        val borde = 22 * u
        val claro = t(GUIJARRO, oscuro)
        val efecto = PathEffect.dashPathEffect(floatArrayOf(0.1f, 4.2f * u))
        fun linea(p: Path) = drawPath(p, claro, style = Stroke(3.4f * u, cap = StrokeCap.Round, pathEffect = efecto))
        linea(Path().apply { addRect(Rect(6 * u, 6 * u, w - 6 * u, h - 6 * u)) })
        linea(Path().apply { addRect(Rect(borde, borde, w - borde, h - borde)) })
        // La greca, arriba y abajo, entre los dos marcos.
        listOf(6 * u, h - borde).forEach { y0 ->
            val a = borde - 6 * u; var x = 6 * u
            val p = Path()
            while (x < w - 6 * u - a) {
                p.moveTo(x, y0 + a * 0.85f); p.lineTo(x, y0 + a * 0.2f); p.lineTo(x + a * 0.7f, y0 + a * 0.2f); p.lineTo(x + a * 0.7f, y0 + a * 0.6f); p.lineTo(x + a * 0.35f, y0 + a * 0.6f)
                x += a
            }
            linea(p)
        }
        // Al centro, una roseta de guijarros, muy tenue.
        val c = Offset(w / 2, h / 2)
        for (k in 0 until 16) {
            val a = 6.283f * k / 16
            drawLine(claro.copy(alpha = 0.12f), c, Offset(c.x + w * 0.3f * kotlin.math.cos(a), c.y + w * 0.3f * kotlin.math.sin(a)), 3 * u, StrokeCap.Round)
        }
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFEFE6D2)
    override val pared: Dp = 24.dp
    override val columnas = 2
    override val lineas = 4

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoTiesto(Rect(0f, 0f, s.width, s.height), semilla)) }

    /** El contorno del tiesto. Si la hoja es muy alta (el escritor), lleva más vértices: con 9 las esquinas se cortarían en diagonales larguísimas. */
    private fun contornoTiesto(r: Rect, semilla: Int) = contornoIrregular(r, semilla, if (r.height > r.width * 1.6f) 24 else 9, 0.035f)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.8f else 1f
        // El barro: el papel teñido de terracota, más oscuro en un borde.
        drawRect(TERRACOTA.copy(alpha = 0.28f))
        drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, TERRA_OSC.copy(alpha = 0.2f)), Offset.Zero, Offset(w, h)))
        // El barniz negro de la cara de afuera, en el borde de abajo, y el canto roto más claro.
        drawRect(BARNIZ.copy(alpha = 0.75f), Offset(0f, h - 6 * u * g), Size(w, 6 * u * g))
        drawPath(contornoTiesto(Rect(0f, 0f, w, h), semilla), TERRA_LUZ.copy(alpha = 0.6f), style = Stroke(2.2f * u))
        // Rayones incisos y poros del barro.
        for (k in 0 until 2) {
            val y = h * (0.25f + 0.5f * azar(semilla, k + 70))
            drawLine(Color.White.copy(alpha = 0.25f), Offset(w * 0.1f, y), Offset(w * (0.4f + 0.4f * azar(semilla, k + 80)), y + 3 * u), 0.7f * u)
        }
        for (k in 0 until (if (grande) 70 else 24)) drawCircle(TERRA_OSC.copy(alpha = 0.2f), (0.5f + azar(semilla, k + 200)) * u, Offset(w * azar(semilla, k + 300), h * azar(semilla, k + 400)))
    }

    override val margenes = MargenesFicha(12.dp, 18.dp, 16.dp, 24.dp, 34.dp, 36.dp)
    override val proporcionFicha = 1.15f
    override val anchoGrande = 1f
    override val proporcionGrande = 0.95f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un óstrakon marcado.
            "hecho" -> selloHecho(TERRA_OSC, p.color, cuadrado = true)
            "convence" -> { drawCircle(BARNIZ, s * 0.2f, c); drawCircle(BARNIZ, s * 0.4f, c, style = Stroke(s * 0.1f)) }
            // Un gancho de greca: lo estás pensando, todavía da vueltas.
            "piensa" -> drawPath(Path().apply {
                moveTo(c.x - s * 0.36f, c.y + s * 0.36f); lineTo(c.x - s * 0.36f, c.y - s * 0.36f); lineTo(c.x + s * 0.36f, c.y - s * 0.36f)
                lineTo(c.x + s * 0.36f, c.y + s * 0.12f); lineTo(c.x - s * 0.06f, c.y + s * 0.12f); lineTo(c.x - s * 0.06f, c.y - s * 0.1f)
            }, TERRA_OSC, style = Stroke(s * 0.12f))
            "duda" -> drawArc(p.tenue, 30f, 300f, false, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Size(s * 0.68f, s * 0.68f), style = Stroke(s * 0.12f))
            "desacuerdo" -> { drawLine(BARNIZ, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Offset(c.x + s * 0.36f, c.y + s * 0.36f), s * 0.11f); drawLine(BARNIZ, Offset(c.x + s * 0.36f, c.y - s * 0.36f), Offset(c.x - s * 0.36f, c.y + s * 0.36f), s * 0.11f) }
            else -> {}
        }
    }

    /** El sol de Vergina, en oro. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        drawPath(simboloCalificacion(TemaId.HELENO, center, size.minDimension * 0.46f), ORO)
    }

    override val desorden = 2.5f
}

/**
 * Egipto: la Casa de la Vida, donde los escribas guardaban sus escritos. Arriba, la cornisa de gola con el friso jekeru en
 * azul, rojo y verde, y el disco solar alado; en el muro, columnas de jeroglíficos; estantes de piedra con bandas pintadas.
 * Cada tema es un cofre de cedro con tapa de capilla y marco de fayenza, cerrado con un hilo de lino entre dos perillas y un
 * terrón de barro sellado; el tema va dentro de un cartucho. Por dentro, el cofre forrado de lino. Las fichas son tablillas
 * de escriba: madera con una cara de estuco blanco y pautas en rojo.
 */
object IdeasEgipto : DisenoIdeas {
    private val CEDRO = Color(0xFF8E4E2C)
    private val CEDRO_LUZ = Color(0xFFB4703F)
    private val CEDRO_OSC = Color(0xFF55301A)
    private val LAPIS = Color(0xFF1D2F7A)
    private val FAYENZA = Color(0xFF2C8C9C)
    private val ORO = Color(0xFFD4AF37)
    private val CORNALINA = Color(0xFFB8471E)
    private val VERDE = Color(0xFF3E7A4A)
    private val MURO = Color(0xFFE8D7AE)
    private val PIEDRA = Color(0xFFCDB98E)
    private val PIEDRA_OSC = Color(0xFF9A835A)
    private val EBANO = Color(0xFF1E1610)
    private val LINO = Color(0xFFE6D8B4)
    private val LINO_OSC = Color(0xFFBFAD84)
    private val BARRO = Color(0xFF6E4A2E)
    private val ESTUCO = Color(0xFFF6F0E0)
    private val TINTA = Color(0xFF1E160C)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.24f
    override val base = 0.1f
    override val lado = 0.08f
    override val hueco = 0.07f

    /** Un jeroglífico sencillo (agua, boca, pan, junco, ojo, pájaro…), del tamaño [s], para las columnas del muro. */
    private fun DrawScope.signo(k: Int, o: Offset, s: Float, color: Color) {
        val u = density
        when (k % 7) {
            0 -> { // agua: zigzag
                val p = Path().apply { moveTo(o.x - s / 2, o.y); for (i in 1..6) lineTo(o.x - s / 2 + s * i / 6, o.y + (if (i % 2 == 0) 0f else -s * 0.18f)) }
                drawPath(p, color, style = Stroke(0.9f * u))
            }
            1 -> drawOval(color, Offset(o.x - s * 0.4f, o.y - s * 0.18f), Size(s * 0.8f, s * 0.36f)) // boca
            2 -> drawArc(color, 180f, 180f, true, Offset(o.x - s * 0.35f, o.y - s * 0.3f), Size(s * 0.7f, s * 0.6f)) // pan
            3 -> { drawLine(color, Offset(o.x, o.y + s * 0.4f), Offset(o.x, o.y - s * 0.4f), 1.1f * u); drawOval(color, Offset(o.x - s * 0.08f, o.y - s * 0.5f), Size(s * 0.24f, s * 0.3f)) } // junco
            4 -> { drawOval(color, Offset(o.x - s * 0.4f, o.y - s * 0.15f), Size(s * 0.8f, s * 0.3f), style = Stroke(0.9f * u)); drawCircle(color, s * 0.1f, o) } // ojo
            5 -> drawCircle(color, s * 0.25f, o, style = Stroke(1f * u)) // sol
            else -> { drawLine(color, Offset(o.x - s * 0.4f, o.y + s * 0.2f), Offset(o.x + s * 0.3f, o.y + s * 0.2f), 1f * u); drawCircle(color, s * 0.12f, Offset(o.x + s * 0.25f, o.y - s * 0.05f)) } // pájaro, muy simple
        }
    }

    /** El friso jekeru: haces de juncos atados, en franjas azul, roja y verde con filetes de oro. */
    private fun DrawScope.jekeru(r: Rect, oscuro: Boolean) {
        val u = density
        val n = (r.width / (6 * u)).toInt().coerceAtLeast(6)
        val colores = listOf(LAPIS, CORNALINA, VERDE)
        for (k in 0 until n) {
            val x = r.left + r.width * k / n
            drawRect(t(colores[k % 3], oscuro), Offset(x, r.top), Size(r.width / n, r.height))
            drawLine(t(ORO, oscuro), Offset(x, r.top), Offset(x, r.bottom), 0.6f * u)
            drawCircle(t(ORO, oscuro), r.width / n * 0.3f, Offset(x + r.width / n / 2, r.top + r.height * 0.25f))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val y0 = d.corona.bottom; val y1 = d.base.top
        // El muro de estuco, con columnas de jeroglíficos entre los cofres y a los lados.
        drawRect(Brush.verticalGradient(listOf(t(mezcla(MURO, Color.Black, 0.08f), oscuro), t(MURO, oscuro)), y0, y1), Offset(0f, y0), Size(w, y1 - y0))
        val fila0 = d.huecos.take(cols)
        val columnas = listOf(fila0.first().left / 2, (fila0[0].right + fila0[1].left) / 2, (fila0.last().right + w) / 2)
        columnas.forEach { x ->
            drawLine(t(LAPIS, oscuro).copy(alpha = 0.3f), Offset(x - 9 * u, y0 + 4 * u), Offset(x - 9 * u, y1 - 4 * u), 0.8f * u)
            drawLine(t(LAPIS, oscuro).copy(alpha = 0.3f), Offset(x + 9 * u, y0 + 4 * u), Offset(x + 9 * u, y1 - 4 * u), 0.8f * u)
            var y = y0 + 14 * u; var k = (x / u).toInt()
            while (y < y1 - 8 * u) { signo(k, Offset(x, y), 10 * u, t(if (k % 4 == 0) CORNALINA else LAPIS, oscuro).copy(alpha = 0.45f)); y += 15 * u; k += 3 }
        }
        // Los estantes de piedra, con su banda pintada al frente.
        d.filas.forEach { f ->
            val r = Rect(w * 0.03f, f.bottom, w * 0.97f, f.bottom + maxOf(d.separacion * 0.45f, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            drawRect(t(PIEDRA, oscuro), r.topLeft, r.size)
            val n = (r.width / (10 * u)).toInt()
            for (k in 0 until n) drawRect(t(listOf(LAPIS, CORNALINA, VERDE)[k % 3], oscuro).copy(alpha = 0.75f), Offset(r.left + r.width * k / n + 1 * u, r.top + r.height * 0.35f), Size(r.width / n - 2 * u, r.height * 0.3f))
            drawLine(Color.White.copy(alpha = 0.3f), r.topLeft, Offset(r.right, r.top), 0.8f * u)
        }
        // Los bordes del muro: el bocel atado (una moldura redonda envuelta en cintas).
        listOf(w * 0.015f, w * 0.985f).forEach { x ->
            drawRect(t(PIEDRA, oscuro), Offset(x - 4 * u, y0), Size(8 * u, y1 - y0))
            var y = y0
            while (y < y1) { drawLine(t(LAPIS, oscuro), Offset(x - 4 * u, y), Offset(x + 4 * u, y + 5 * u), 1.6f * u); y += 7 * u }
        }
        // La cornisa: el bocel abajo, la gola con el friso jekeru y, al centro, el disco solar alado.
        val ch = d.corona.height
        val bocel = Rect(0f, d.corona.bottom - ch * 0.12f, w, d.corona.bottom)
        drawRect(t(PIEDRA, oscuro), bocel.topLeft, bocel.size)
        var x = 0f
        while (x < w) { drawLine(t(LAPIS, oscuro), Offset(x, bocel.top), Offset(x + 5 * u, bocel.bottom), 1.6f * u); x += 7 * u }
        val gola = Path().apply {
            moveTo(w * 0.01f, bocel.top); lineTo(w * 0.99f, bocel.top)
            quadraticTo(w * 0.995f, d.corona.top + ch * 0.25f, w, d.corona.top + ch * 0.08f); lineTo(0f, d.corona.top + ch * 0.08f)
            quadraticTo(w * 0.005f, d.corona.top + ch * 0.25f, w * 0.01f, bocel.top); close()
        }
        drawPath(gola, t(PIEDRA, oscuro))
        clipPath(gola) { jekeru(Rect(0f, d.corona.top + ch * 0.08f, w, bocel.top), oscuro) }
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), d.corona.top + ch * 0.08f, d.corona.top + ch * 0.4f), Offset(0f, d.corona.top + ch * 0.08f), Size(w, ch * 0.32f))
        drawRect(t(PIEDRA_OSC, oscuro), Offset(-2 * u, d.corona.top + ch * 0.02f), Size(w + 4 * u, ch * 0.07f))
        val sol = Offset(w / 2, d.corona.top + ch * 0.5f)
        val rs = ch * 0.16f
        listOf(-1f, 1f).forEach { s ->
            for (k in 0 until 3) {
                val ala = Path().apply {
                    moveTo(sol.x + s * rs * 0.8f, sol.y - rs * 0.5f + k * rs * 0.35f)
                    quadraticTo(sol.x + s * rs * 3.5f, sol.y - rs * (1.1f - k * 0.3f), sol.x + s * rs * (5.2f - k * 0.6f), sol.y - rs * (0.4f - k * 0.3f))
                    lineTo(sol.x + s * rs * (5f - k * 0.6f), sol.y + rs * (0.05f + k * 0.25f))
                    quadraticTo(sol.x + s * rs * 3f, sol.y - rs * (0.1f - k * 0.3f), sol.x + s * rs * 0.8f, sol.y + rs * 0.2f + k * rs * 0.3f); close()
                }
                drawPath(ala, t(listOf(LAPIS, FAYENZA, VERDE)[k], oscuro))
                drawPath(ala, t(ORO, oscuro), style = Stroke(0.7f * u))
            }
            // El ureo, la cobra a cada lado del disco.
            drawLine(t(ORO, oscuro), Offset(sol.x + s * rs * 0.9f, sol.y + rs * 0.6f), Offset(sol.x + s * rs * 1.1f, sol.y - rs * 0.4f), 1.8f * u, StrokeCap.Round)
        }
        drawCircle(t(CORNALINA, oscuro), rs, sol)
        drawCircle(t(ORO, oscuro), rs, sol, style = Stroke(1.2f * u))
        // El zócalo: franjas de colores y la línea del piso.
        val zocalo = Rect(0f, d.base.top, w, d.base.top + d.base.height * 0.7f)
        drawRect(t(PIEDRA, oscuro), zocalo.topLeft, zocalo.size)
        val nz = (w / (16 * u)).toInt()
        for (k in 0 until nz) drawRect(t(listOf(LAPIS, CORNALINA, VERDE, ORO)[k % 4], oscuro).copy(alpha = 0.8f), Offset(w * k / nz + 2 * u, zocalo.top + 3 * u), Size(w / nz - 4 * u, zocalo.height - 6 * u))
        drawRect(t(PIEDRA_OSC, oscuro), Offset(0f, zocalo.bottom), Size(w, d.base.bottom - zocalo.bottom))
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawRect(t(ESTUCO, oscuro), r.topLeft, r.size)
            drawRect(t(LAPIS, oscuro), r.topLeft, r.size, style = Stroke(1.6f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre del estante: un vaso de fayenza azul.
            val o = Offset(w / 2, h * 0.96f)
            val vaso = Path().apply { moveTo(o.x - w * 0.08f, o.y); quadraticTo(o.x - w * 0.16f, o.y - h * 0.2f, o.x - w * 0.06f, o.y - h * 0.32f); lineTo(o.x + w * 0.06f, o.y - h * 0.32f)
                quadraticTo(o.x + w * 0.16f, o.y - h * 0.2f, o.x + w * 0.08f, o.y); close() }
            drawPath(vaso, Brush.horizontalGradient(listOf(t(FAYENZA, oscuro), t(mezcla(FAYENZA, Color.White, 0.3f), oscuro), t(FAYENZA, oscuro)), o.x - w * 0.16f, o.x + w * 0.16f))
            drawLine(t(EBANO, oscuro), Offset(o.x - w * 0.1f, o.y - h * 0.16f), Offset(o.x + w * 0.1f, o.y - h * 0.16f), 1.2f * u)
            return
        }
        val cuerpo = Rect(w * 0.08f, h * 0.38f, w * 0.92f, h * 0.88f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        // Las patas cortas.
        listOf(cuerpo.left + 3 * u, cuerpo.right - 3 * u - w * 0.08f).forEach { x ->
            drawPath(Path().apply { moveTo(x, cuerpo.bottom); lineTo(x + w * 0.08f, cuerpo.bottom); lineTo(x + w * 0.06f, h * 0.98f); lineTo(x + w * 0.02f, h * 0.98f); close() }, t(CEDRO_OSC, oscuro))
        }
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(cuerpo.left, h * 0.95f), Size(cuerpo.width, h * 0.05f))
        // Lo que asoma entre la tapa y el cofre: los cantos de las tablillas.
        if (sube > 1f) {
            drawRect(Color(0xFF1C110A), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.12f + 0.13f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRect(t(ESTUCO, oscuro), Offset(x, y), Size(cuerpo.width * 0.1f, cuerpo.top - y + 2 * u))
                drawRect(t(CEDRO_LUZ, oscuro), Offset(x, y), Size(cuerpo.width * 0.1f, 1.6f * u))
            }
        }
        // El cofre: cedro con su veta y el marco de taracea de fayenza y ébano.
        drawRect(Brush.verticalGradient(listOf(t(CEDRO_LUZ, oscuro), t(CEDRO, oscuro), t(CEDRO_OSC, oscuro)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, CEDRO_OSC.copy(alpha = 0.35f), c.semilla, 1f, grosor = 0.7f, ondulacion = 0.4f)
        val m = 3.5f * u; val cuadro = 3.2f * u
        val marco = Rect(cuerpo.left + m, cuerpo.top + m, cuerpo.right - m, cuerpo.bottom - m)
        var x = marco.left; var i = 0
        while (x < marco.right - cuadro) {
            listOf(marco.top, marco.bottom - cuadro).forEach { y -> drawRect(t(if (i % 2 == 0) FAYENZA else EBANO, oscuro), Offset(x, y), Size(cuadro, cuadro)) }
            x += cuadro; i++
        }
        var y = marco.top; i = 0
        while (y < marco.bottom - cuadro) {
            listOf(marco.left, marco.right - cuadro).forEach { xx -> drawRect(t(if (i % 2 == 0) FAYENZA else EBANO, oscuro), Offset(xx, y), Size(cuadro, cuadro)) }
            y += cuadro; i++
        }
        // La tapa de capilla: el techo inclinado con su cornisa, que sube al abrirse.
        val ty = cuerpo.top - sube
        val tapa = Path().apply {
            moveTo(cuerpo.left - 3 * u, ty); lineTo(cuerpo.right + 3 * u, ty)
            lineTo(cuerpo.right - w * 0.02f, ty - h * 0.08f); quadraticTo(w / 2, ty - h * 0.2f, cuerpo.left + w * 0.02f, ty - h * 0.08f); close()
        }
        drawPath(tapa, Brush.verticalGradient(listOf(t(CEDRO_LUZ, oscuro), t(CEDRO, oscuro)), ty - h * 0.2f, ty))
        drawLine(t(ORO, oscuro), Offset(cuerpo.left - 3 * u, ty), Offset(cuerpo.right + 3 * u, ty), 1.6f * u)
        // Las dos perillas, el hilo de lino entre ellas y el terrón de barro sellado.
        val p1 = Offset(w / 2, ty - h * 0.07f); val p2 = Offset(w / 2, cuerpo.top + h * 0.05f)
        listOf(p1, p2).forEach { o -> drawCircle(t(EBANO, oscuro), 3.2f * u, o); drawCircle(Color.White.copy(alpha = 0.3f), 1f * u, Offset(o.x - 1 * u, o.y - 1 * u)) }
        listOf(-2f, 2f).forEach { dx -> drawLine(t(LINO, oscuro), Offset(p1.x + dx * u, p1.y), Offset(p2.x - dx * u, p2.y), 1f * u) }
        drawOval(t(BARRO, oscuro), Offset(p2.x + 3 * u, p2.y - 1 * u), Size(10 * u, 8 * u))
        drawOval(t(mezcla(BARRO, Color.Black, 0.3f), oscuro), Offset(p2.x + 5 * u, p2.y + 0.5f * u), Size(6 * u, 4.5f * u), style = Stroke(0.7f * u))
        // El cartucho donde va el tema: un óvalo largo de estuco con su cuerda de lapislázuli y la barra al final.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(t(ESTUCO, oscuro), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(r.height / 2))
        drawRoundRect(t(LAPIS, oscuro), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(r.height / 2), style = Stroke(1.8f * u))
        drawLine(t(LAPIS, oscuro), Offset(r.right + 2.5f * u, r.top + r.height * 0.15f), Offset(r.right + 2.5f * u, r.bottom - r.height * 0.15f), 2.2f * u)
    }

    override val rotulo = Rect(0.16f, 0.52f, 0.82f, 0.8f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.messiri
    override val salida = 0f
    override val placa = Rect(0.3f, 0.12f, 0.7f, 0.6f)
    override fun tintaPlaca(oscuro: Boolean) = LAPIS

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del cofre, forrado de lino: pliegues suaves en diagonal.
        drawRect(t(LINO, oscuro))
        for (k in 0 until 14) {
            val x = w * (k / 10f) - w * 0.3f
            val pliegue = Path().apply { moveTo(x, 0f); cubicTo(x + w * 0.1f, h * 0.3f, x - w * 0.05f, h * 0.6f, x + w * 0.2f, h) }
            drawPath(pliegue, t(LINO_OSC, oscuro).copy(alpha = 0.35f), style = Stroke((6f + 10f * azar(71, k)) * u, cap = StrokeCap.Round))
            drawPath(pliegue, Color.White.copy(alpha = 0.18f), style = Stroke(1.5f * u))
        }
        // El tejido: hilos finos en las dos direcciones.
        var y = 0f; while (y < h) { drawLine(t(LINO_OSC, oscuro).copy(alpha = 0.12f), Offset(0f, y), Offset(w, y), 0.5f * u); y += 2.5f * u }
        // Las paredes de cedro y la sombra que echan sobre el lino.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl * 1.6f, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(CEDRO, oscuro), r.topLeft, r.size)
            vetas(r, CEDRO_OSC.copy(alpha = 0.4f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        sombraInterior(Rect(pl, pl, w - pl, h - pl * 1.6f), 18 * u, 0.3f)
        // El borde de fayenza en el canto de la pared de enfrente.
        drawRect(t(FAYENZA, oscuro), Offset(0f, h - pl * 1.6f), Size(w, 2.5f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2E6CC) else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 3

    override fun forma(semilla: Int): Shape = RoundedCornerShape(4.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        val m = 5 * u * g
        // La cara de estuco, más blanca que el papel, con su craquelado.
        drawRect(ESTUCO.copy(alpha = 0.55f), Offset(m, m), Size(w - 2 * m, h - 2 * m))
        for (k in 0 until (if (grande) 9 else 4)) {
            val p0 = Offset(m + (w - 2 * m) * azar(semilla, k), m + (h - 2 * m) * azar(semilla, k + 20))
            val q = Path().apply { moveTo(p0.x, p0.y); var o = p0; repeat(4) { j -> o = Offset(o.x + (azar(semilla, k * 7 + j + 40) - 0.5f) * 20 * u, o.y + (azar(semilla, k * 7 + j + 60) - 0.3f) * 14 * u); lineTo(o.x, o.y) } }
            drawPath(q, PIEDRA_OSC.copy(alpha = 0.25f), style = Stroke(0.5f * u))
        }
        // El marco de madera de la tablilla.
        drawRect(Brush.linearGradient(listOf(CEDRO_LUZ, CEDRO, CEDRO_OSC), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), style = Stroke(m * 2))
        drawRect(CEDRO_OSC, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(0.8f * u))
        // El agujero para colgarla, con su cordel de lino, arriba a la derecha.
        val hoyo = Offset(w - m / 2, m * 1.2f)
        drawCircle(Color.Black.copy(alpha = 0.6f), m * 0.28f, hoyo)
        drawLine(LINO, hoyo, Offset(w, m * 0.2f), 1f * u * g)
        // Las dos pastillas de tinta, roja y negra, en la esquina de abajo a la izquierda.
        drawCircle(CORNALINA, 2.6f * u * g, Offset(m * 2.4f, h - m * 2.2f))
        drawCircle(EBANO, 2.6f * u * g, Offset(m * 2.4f + 6.5f * u * g, h - m * 2.2f))
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 16.dp, 22.dp, 28.dp, 32.dp)
    override val proporcionFicha = 1.25f
    override val anchoGrande = 1f
    override val proporcionGrande = 1f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El pilar djed: lo que se mantiene firme.
            "hecho" -> {
                drawRect(LAPIS, Offset(c.x - s * 0.1f, c.y - s * 0.24f), Size(s * 0.2f, s * 0.66f))
                for (k in 0..3) drawRect(LAPIS, Offset(c.x - s * 0.3f, c.y - s * 0.44f + k * s * 0.1f), Size(s * 0.6f, s * 0.06f))
            }
            // El ankh: la vida.
            "convence" -> {
                drawOval(LAPIS, Offset(c.x - s * 0.18f, c.y - s * 0.46f), Size(s * 0.36f, s * 0.4f), style = Stroke(s * 0.1f))
                drawLine(LAPIS, Offset(c.x, c.y - s * 0.06f), Offset(c.x, c.y + s * 0.46f), s * 0.12f)
                drawLine(LAPIS, Offset(c.x - s * 0.34f, c.y + 0.02f * s), Offset(c.x + s * 0.34f, c.y + 0.02f * s), s * 0.11f)
            }
            // El ojo: lo estás mirando.
            "piensa" -> {
                drawOval(EBANO, Offset(c.x - s * 0.42f, c.y - s * 0.18f), Size(s * 0.84f, s * 0.36f), style = Stroke(s * 0.08f))
                drawCircle(EBANO, s * 0.13f, c)
                drawLine(EBANO, Offset(c.x - s * 0.08f, c.y + s * 0.16f), Offset(c.x - s * 0.2f, c.y + s * 0.42f), s * 0.07f)
            }
            // El anillo shen, sin cerrar del todo.
            "duda" -> { drawCircle(p.tenue, s * 0.3f, Offset(c.x, c.y - s * 0.08f), style = Stroke(s * 0.1f)); drawLine(p.tenue, Offset(c.x - s * 0.34f, c.y + s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.1f) }
            "desacuerdo" -> { drawLine(CORNALINA, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.13f); drawLine(CORNALINA, Offset(c.x + s * 0.34f, c.y - s * 0.34f), Offset(c.x - s * 0.34f, c.y + s * 0.34f), s * 0.13f) }
            else -> {}
        }
    }

    /** Un escarabajo de lapislázuli con las alas de oro. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        listOf(-1f, 1f).forEach { k -> for (j in 0 until 3) drawLine(EBANO, Offset(c.x, c.y - s * 0.1f + j * s * 0.18f), Offset(c.x + k * s * 0.42f, c.y - s * 0.2f + j * s * 0.24f), s * 0.05f) }
        drawOval(LAPIS, Offset(c.x - s * 0.26f, c.y - s * 0.24f), Size(s * 0.52f, s * 0.62f))
        drawOval(LAPIS, Offset(c.x - s * 0.15f, c.y - s * 0.44f), Size(s * 0.3f, s * 0.22f))
        drawLine(ORO, Offset(c.x, c.y - s * 0.22f), Offset(c.x, c.y + s * 0.36f), s * 0.05f)
        drawLine(ORO, Offset(c.x - s * 0.24f, c.y - s * 0.12f), Offset(c.x + s * 0.24f, c.y - s * 0.12f), s * 0.05f)
    }

    override val desorden = 0f
}

/**
 * Mesoamérica: el muro pintado, de estuco ocre con paneles de cinabrio, una banda de grecas escalonadas (xicalcoliuhqui)
 * arriba y una plataforma de talud y tablero abajo. Cada tema es una petaca de palma tejida con su tapa, atada con henequén;
 * las hojas de amate dobladas levantan la tapa, y el tema va en una tira de amate con filete rojo. Por dentro, el tejido de
 * la petaca. Las fichas son hojas de amate cubiertas de estuco, con el filete rojo de las páginas de códice.
 */
object IdeasMeso : DisenoIdeas {
    private val ESTUCO = Color(0xFFF2E8D2)
    private val OCRE = Color(0xFFD69A55)
    private val CINABRIO = Color(0xFFA3301E)
    private val AZUL = Color(0xFF2C7F9C)
    private val JADE = Color(0xFF2F7D57)
    private val PALMA = Color(0xFFCDB585)
    private val PALMA_OSC = Color(0xFF9C8458)
    private val PALMA_LUZ = Color(0xFFE6D2A0)
    private val AMATE = Color(0xFFB89A70)
    private val AMATE_CLARO = Color(0xFFDCC7A0)
    private val AMATE_OSC = Color(0xFF8A6E4A)
    private val HENEQUEN = Color(0xFFCDB585)
    private val NEGRO = Color(0xFF2A1810)
    private val QUETZAL = Color(0xFF1E8C5A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.32f) else c

    override val cols = 2
    override val proporcion = 1.35f
    override val corona = 0.15f
    override val base = 0.15f
    override val lado = 0.07f
    override val hueco = 0.06f

    /**
     * El tejido de palma en diagonal, recortado a [r]: tiras claras en un sentido y oscuras en el otro, con el filo de cada
     * tira marcado. Son pocas líneas gruesas (no un cuadrito por cruce), para que se dibuje rápido.
     */
    private fun DrawScope.tejido(r: Rect, celda: Float, oscuro: Boolean, semilla: Int) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(t(PALMA, oscuro), r.topLeft, r.size)
            val largo = r.width + r.height
            var k = 0
            var o = -r.height
            while (o < r.width) {
                val a = Offset(r.left + o, r.top); val b = Offset(r.left + o + r.height, r.bottom)
                val a2 = Offset(r.left + o + r.height, r.top); val b2 = Offset(r.left + o, r.bottom)
                drawLine(t(PALMA_LUZ, oscuro).copy(alpha = 0.45f + 0.2f * azar(semilla, k)), a, b, celda * 0.8f)
                drawLine(t(PALMA_OSC, oscuro).copy(alpha = 0.3f + 0.15f * azar(semilla, k + 50)), a2, b2, celda * 0.45f)
                drawLine(t(PALMA_OSC, oscuro).copy(alpha = 0.5f), Offset(a.x + celda * 0.57f, a.y), Offset(b.x + celda * 0.57f, b.y), 0.6f * density)
                o += celda * 1.4f; k++
                if (k > largo) break
            }
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val y0 = d.corona.bottom; val y1 = d.base.top
        // El muro de estuco ocre, con un panel de cinabrio detrás de cada fila y una franja de azul maya entre filas.
        drawRect(t(OCRE, oscuro), Offset(0f, y0), Size(w, y1 - y0))
        for (k in 0 until 50) mancha(Offset(w * azar(91, k), y0 + (y1 - y0) * azar(92, k)), (6f + 18f * azar(93, k)) * u, Color.Black.copy(alpha = 0.05f))
        d.filas.forEach { f ->
            val r = Rect(w * 0.035f, f.top - d.separacion * 0.35f, w * 0.965f, f.bottom + d.separacion * 0.2f)
            drawRect(t(CINABRIO, oscuro), r.topLeft, r.size)
            drawRect(t(NEGRO, oscuro).copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(1.2f * u))
        }
        d.filas.zipWithNext().forEach { (a, b) ->
            val y = (a.bottom + b.top) / 2
            drawRect(t(AZUL, oscuro), Offset(0f, y - 2.5f * u), Size(w, 5 * u))
            // Puntos y barras, como números mayas, sobre la franja.
            for (k in 0 until 6) drawCircle(t(ESTUCO, oscuro), 1.2f * u, Offset(w * (0.42f + 0.03f * k), y))
        }
        // La banda de grecas escalonadas, arriba.
        val ch = d.corona.height
        val banda = Rect(0f, d.corona.top + ch * 0.2f, w, d.corona.bottom)
        drawRect(t(ESTUCO, oscuro), banda.topLeft, banda.size)
        val a = banda.height * 0.8f
        var x = 0f; var k = 0
        while (x < w) {
            val o = Offset(x, banda.top + banda.height * 0.1f)
            val greca = Path().apply {
                moveTo(o.x, o.y + a); lineTo(o.x, o.y + a * 0.6f); lineTo(o.x + a * 0.25f, o.y + a * 0.6f); lineTo(o.x + a * 0.25f, o.y + a * 0.3f)
                lineTo(o.x + a * 0.5f, o.y + a * 0.3f); lineTo(o.x + a * 0.5f, o.y); lineTo(o.x + a * 1.2f, o.y); lineTo(o.x + a * 1.2f, o.y + a * 0.6f)
                lineTo(o.x + a * 0.85f, o.y + a * 0.6f); lineTo(o.x + a * 0.85f, o.y + a * 0.3f)
            }
            drawPath(greca, t(if (k % 2 == 0) CINABRIO else AZUL, oscuro), style = Stroke(a * 0.16f))
            x += a * 1.3f; k++
        }
        drawRect(t(NEGRO, oscuro), Offset(0f, banda.top - 2 * u), Size(w, 2.5f * u))
        drawRect(t(NEGRO, oscuro), Offset(0f, banda.bottom - 1.5f * u), Size(w, 2 * u))
        // La plataforma: el tablero (un panel con su marco hundido) sobre el talud inclinado.
        val tablero = Rect(w * 0.02f, d.base.top, w * 0.98f, d.base.top + d.base.height * 0.5f)
        drawRect(t(CINABRIO, oscuro), tablero.topLeft, tablero.size)
        drawRect(t(mezcla(CINABRIO, Color.Black, 0.3f), oscuro), Offset(tablero.left + 5 * u, tablero.top + 4 * u), Size(tablero.width - 10 * u, tablero.height - 8 * u), style = Stroke(2 * u))
        val talud = Path().apply { moveTo(tablero.left, tablero.bottom); lineTo(tablero.right, tablero.bottom); lineTo(w, d.base.bottom); lineTo(0f, d.base.bottom); close() }
        drawPath(talud, Brush.verticalGradient(listOf(t(OCRE, oscuro), t(mezcla(OCRE, Color.Black, 0.2f), oscuro)), tablero.bottom, d.base.bottom))
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(tablero.left, tablero.bottom), Offset(tablero.right, tablero.bottom), 1.2f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawRoundRect(t(ESTUCO, oscuro), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(4 * u))
            drawRoundRect(t(NEGRO, oscuro), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(4 * u), style = Stroke(1.2f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un cajete trípode de barro con su borde rojo.
            val y = h * 0.86f
            drawPath(Path().apply { moveTo(w * 0.3f, y - h * 0.16f); quadraticTo(w * 0.32f, y, w * 0.5f, y); quadraticTo(w * 0.68f, y, w * 0.7f, y - h * 0.16f); close() }, t(Color(0xFFB07A4A), oscuro))
            drawLine(t(CINABRIO, oscuro), Offset(w * 0.3f, y - h * 0.16f), Offset(w * 0.7f, y - h * 0.16f), 2.4f * u)
            listOf(0.38f, 0.5f, 0.62f).forEach { fx -> drawLine(t(Color(0xFF8A5A30), oscuro), Offset(w * fx, y - 1 * u), Offset(w * fx, y + h * 0.07f), 2.2f * u, StrokeCap.Round) }
            return
        }
        val cuerpo = Rect(w * 0.08f, h * 0.36f, w * 0.92f, h * 0.95f)
        val sube = h * 0.014f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Size(cuerpo.width, h * 0.06f))
        // Las hojas de amate dobladas que levantan la tapa.
        if (sube > 1f) {
            drawRect(Color(0xFF2A1A0E), Offset(cuerpo.left + 2 * u, cuerpo.top - sube), Size(cuerpo.width - 4 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.08f + 0.14f * i)
                val y = cuerpo.top - sube * (0.35f + 0.55f * azar(c.semilla, i))
                drawRect(t(if (i % 2 == 0) AMATE_CLARO else AMATE, oscuro), Offset(x, y), Size(cuerpo.width * 0.16f, cuerpo.top - y + 2 * u))
                drawLine(t(CINABRIO, oscuro).copy(alpha = 0.7f), Offset(x + 1 * u, y + 1.5f * u), Offset(x + cuerpo.width * 0.15f, y + 1.5f * u), 0.8f * u)
            }
        }
        // La petaca tejida, con una franja teñida de rojo arriba y abajo y su sombra.
        tejido(cuerpo, 5.5f * u, oscuro, c.semilla)
        drawRect(t(CINABRIO, oscuro).copy(alpha = 0.35f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.08f), Size(cuerpo.width, cuerpo.height * 0.06f))
        drawRect(t(CINABRIO, oscuro).copy(alpha = 0.35f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.14f), Size(cuerpo.width, cuerpo.height * 0.06f))
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
        drawRect(t(PALMA_OSC, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1.6f * u))
        // La tapa, un poco más ancha, con su borde trenzado.
        val tapa = Rect(w * 0.05f, cuerpo.top - sube - h * 0.15f, w * 0.95f, cuerpo.top - sube + 1 * u)
        tejido(tapa, 5.5f * u, oscuro, c.semilla + 1)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.2f)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        var x = tapa.left
        while (x < tapa.right) { drawLine(t(PALMA_OSC, oscuro), Offset(x, tapa.bottom - 3 * u), Offset(x + 3 * u, tapa.bottom), 1.4f * u); x += 3 * u }
        drawRect(t(PALMA_OSC, oscuro), tapa.topLeft, tapa.size, style = Stroke(1.4f * u))
        // Los amarres de henequén.
        listOf(0.22f, 0.78f).forEach { fx ->
            val cx = w * fx
            drawLine(t(HENEQUEN, oscuro), Offset(cx, tapa.top), Offset(cx, cuerpo.bottom), 1.8f * u)
            drawLine(t(mezcla(HENEQUEN, Color.Black, 0.3f), oscuro), Offset(cx + 0.9f * u, tapa.top), Offset(cx + 0.9f * u, cuerpo.bottom), 0.6f * u)
            drawCircle(t(HENEQUEN, oscuro), 2.6f * u, Offset(cx, tapa.bottom))
        }
        // La tira de amate con el tema, con su filete rojo.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(t(AMATE_CLARO, oscuro), r.topLeft, r.size)
        for (j in 0 until 10) {
            val y = r.top + r.height * azar(c.semilla + 3, j)
            drawLine(t(AMATE_OSC, oscuro).copy(alpha = 0.3f), Offset(r.left + r.width * azar(c.semilla, j + 10), y), Offset(r.left + r.width * azar(c.semilla, j + 30), y + (azar(c.semilla, j + 50) - 0.5f) * 6 * u), 0.6f * u)
        }
        drawRect(t(CINABRIO, oscuro), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(1.2f * u))
    }

    override val rotulo = Rect(0.18f, 0.5f, 0.82f, 0.8f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.codice
    override val salida = 0f
    override val placa = Rect(0.3f, 0.08f, 0.7f, 0.44f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo tejido de la petaca, en celdas más grandes, con las paredes alrededor y su sombra.
        tejido(Rect(0f, 0f, w, h), 14 * u, oscuro, 7)
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(PALMA_OSC, oscuro).copy(alpha = 0.7f), r.topLeft, r.size)
        }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 22 * u, 0.32f)
        // Una franja teñida de rojo que corre por el fondo.
        drawRect(t(CINABRIO, oscuro).copy(alpha = 0.18f), Offset(pl, h * 0.48f), Size(w - 2 * pl, 18 * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2E6CF) else NEGRO
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 28, 0.012f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El borde donde no llegó el estuco: se ve la corteza de amate, con sus fibras.
        val b = 3.2f * u * g
        drawRect(AMATE, Offset.Zero, Size(w, h), style = Stroke(b * 2))
        for (k in 0 until (if (grande) 60 else 24)) {
            val lado = k % 4
            val f = azar(semilla, k)
            val (a, z) = when (lado) {
                0 -> Offset(w * f, 0f) to Offset(w * f + 12 * u, b * 1.5f)
                1 -> Offset(w, h * f) to Offset(w - b * 1.5f, h * f + 12 * u)
                2 -> Offset(w * f, h) to Offset(w * f - 12 * u, h - b * 1.5f)
                else -> Offset(0f, h * f) to Offset(b * 1.5f, h * f - 12 * u)
            }
            drawLine(AMATE_OSC.copy(alpha = 0.6f), a, z, 0.7f * u)
        }
        // El filete rojo de la página de códice.
        drawRect(CINABRIO.copy(alpha = 0.85f), Offset(b + 3 * u, b + 3 * u), Size(w - 2 * b - 6 * u, h - 2 * b - 6 * u), style = Stroke(1.2f * u * g))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 16.dp, 24.dp, 30.dp, 32.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.95f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La barra y el punto: un número asentado en el códice.
            "hecho" -> { drawRoundRect(CINABRIO, Offset(c.x - s * 0.4f, c.y + s * 0.06f), Size(s * 0.8f, s * 0.2f), androidx.compose.ui.geometry.CornerRadius(s * 0.06f)); drawCircle(CINABRIO, s * 0.14f, Offset(c.x, c.y - s * 0.22f)) }
            // Una cuenta de jade.
            "convence" -> { drawCircle(JADE, s * 0.38f, c); drawCircle(Color.White.copy(alpha = 0.4f), s * 0.12f, Offset(c.x - s * 0.12f, c.y - s * 0.12f)) }
            // La voluta de la palabra: lo que se dice y se piensa.
            "piensa" -> drawPath(Path().apply {
                moveTo(c.x - s * 0.4f, c.y + s * 0.3f); cubicTo(c.x - s * 0.4f, c.y - s * 0.5f, c.x + s * 0.45f, c.y - s * 0.45f, c.x + s * 0.35f, c.y + s * 0.05f)
                cubicTo(c.x + s * 0.28f, c.y + s * 0.3f, c.x - s * 0.05f, c.y + s * 0.2f, c.x + s * 0.05f, c.y - s * 0.05f)
            }, AZUL, style = Stroke(s * 0.11f, cap = StrokeCap.Round))
            "duda" -> drawCircle(p.tenue, s * 0.32f, c, style = Stroke(s * 0.12f))
            "desacuerdo" -> { drawLine(CINABRIO, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.13f); drawLine(CINABRIO, Offset(c.x + s * 0.34f, c.y - s * 0.34f), Offset(c.x - s * 0.34f, c.y + s * 0.34f), s * 0.13f) }
            else -> {}
        }
    }

    /** Una pluma de quetzal: larga, verde, con su raquis. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        rotate(-35f, c) {
            drawOval(QUETZAL, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.9f))
            drawLine(Color(0xFF0E5A38), Offset(c.x, c.y - s * 0.46f), Offset(c.x, c.y + s * 0.5f), s * 0.05f)
        }
    }

    override val desorden = 1f
}
