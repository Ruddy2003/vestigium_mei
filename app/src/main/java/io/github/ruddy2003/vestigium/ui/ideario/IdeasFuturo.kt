package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Futuro
// =====================================================================

/** Un rectángulo de [w] por [h] con el borde de abajo en dientes de sierra parejos (el corte de un ticket). */
private fun dentado(w: Float, h: Float, dientes: Int, hondo: Float) = Path().apply {
    moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - hondo)
    for (k in dientes * 2 downTo 0) lineTo(w * k / (dientes * 2), if (k % 2 == 0) h - hondo else h)
    close()
}

/** Un trazo de neón: un halo ancho y tenue y el tubo fino y brillante. */
private fun DrawScope.neon(p: Path, color: Color, grosor: Float) {
    drawPath(p, color.copy(alpha = 0.22f), style = Stroke(grosor * 4, cap = StrokeCap.Round))
    drawPath(p, color.copy(alpha = 0.45f), style = Stroke(grosor * 2, cap = StrokeCap.Round))
    drawPath(p, Color.White.copy(alpha = 0.9f), style = Stroke(grosor * 0.6f, cap = StrokeCap.Round))
    drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
}

/** Un corazón con centro [c] y alto [s]. */
private fun corazon(c: Offset, s: Float) = Path().apply {
    moveTo(c.x, c.y + s * 0.36f)
    cubicTo(c.x - s * 0.55f, c.y, c.x - s * 0.32f, c.y - s * 0.48f, c.x, c.y - s * 0.16f)
    cubicTo(c.x + s * 0.32f, c.y - s * 0.48f, c.x + s * 0.55f, c.y, c.x, c.y + s * 0.36f)
    close()
}

/**
 * Ciudad neón: la barra de un puesto de comida en los niveles bajos. Paneles de acero con calcomanías y cables colgando,
 * arriba un toldo a franjas con un letrero de neón (un tazón con palillos y su vapor); abajo, la barra con taburetes y los
 * charcos que reflejan las luces. Cada tema es un riel de comandas de donde cuelgan los tickets térmicos, y el tema va en un
 * letrerito de neón verde. Por dentro, la plancha de acero con reflejos de neón y vapor. Las fichas son tickets dentados.
 */
object IdeasNeon : DisenoIdeas {
    private val NOCHE = Color(0xFF120C17)
    private val ACERO = Color(0xFF2A2630)
    private val ACERO_LUZ = Color(0xFF4A4452)
    private val ACERO_OSC = Color(0xFF1A1620)
    private val MAGENTA = Color(0xFFFF3D9A)
    private val AMBAR = Color(0xFFFFB238)
    private val SODIO = Color(0xFF7CFF6B)
    private val CIAN = Color(0xFF3DE0FF)
    private val TICKET = Color(0xFFF2EEE6)
    private val TEXTO = Color(0xFFF4E7F0)
    private val LONA = Color(0xFF7A1E3A)
    private val LONA_OSC = Color(0xFF2A1A2E)
    private val FAROL = Color(0xFFE0443A)

    override val cols = 2
    override val proporcion = 1.6f
    override val corona = 0.34f
    override val base = 0.17f
    override val lado = 0.07f
    override val hueco = 0.06f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // Los paneles de acero, con sus juntas, calcomanías y cables.
        val cuerpo = Rect(0f, d.corona.top + ch * 0.6f, w, d.base.top + d.base.height * 0.3f)
        drawRect(ACERO, cuerpo.topLeft, cuerpo.size)
        var x = 0f; var k = 0
        while (x < w) {
            drawLine(ACERO_OSC, Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 2 * u)
            drawLine(ACERO_LUZ.copy(alpha = 0.5f), Offset(x + 2 * u, cuerpo.top), Offset(x + 2 * u, cuerpo.bottom), 0.8f * u)
            x += w / 4; k++
        }
        for (j in 0 until 9) {
            val c = Offset(w * azar(51, j), cuerpo.top + cuerpo.height * azar(52, j))
            rotate(-20f + 40f * azar(53, j), c) { drawRoundRect(listOf(MAGENTA, CIAN, AMBAR, SODIO)[j % 4].copy(alpha = 0.35f), Offset(c.x - 9 * u, c.y - 6 * u), Size(18 * u, 12 * u), CornerRadius(2 * u)) }
        }
        listOf(0.15f, 0.55f, 0.85f).forEachIndexed { i, f ->
            val p = Path().apply { moveTo(w * f, cuerpo.top); cubicTo(w * f + 10 * u, cuerpo.top + 40 * u, w * (f + 0.08f), cuerpo.top + 30 * u, w * (f + 0.1f), cuerpo.top + (12 + i * 6) * u) }
            drawPath(p, Color.Black, style = Stroke(1.6f * u))
        }
        // Los rieles de comandas, uno arriba de cada fila.
        d.filas.forEach { f ->
            drawLine(ACERO_LUZ, Offset(w * 0.03f, f.top + 4 * u), Offset(w * 0.97f, f.top + 4 * u), 3 * u)
            drawLine(Color.White.copy(alpha = 0.3f), Offset(w * 0.03f, f.top + 3 * u), Offset(w * 0.97f, f.top + 3 * u), 0.8f * u)
        }
        // Arriba, el letrero de neón: un tazón con palillos y su vapor, en su marco ámbar.
        drawRect(NOCHE, d.corona.topLeft, Size(w, ch * 0.62f))
        val cx = w / 2; val cy = d.corona.top + ch * 0.33f
        val r = ch * 0.2f
        neon(Path().apply { moveTo(cx - r * 1.3f, cy); lineTo(cx + r * 1.3f, cy); arcTo(Rect(cx - r * 1.3f, cy - r * 1.1f, cx + r * 1.3f, cy + r * 1.1f), 0f, 180f, false) }, MAGENTA, 2.4f * u)
        neon(Path().apply { moveTo(cx + r * 0.4f, cy - r * 1.2f); lineTo(cx + r * 1.6f, cy + r * 0.2f) }, AMBAR, 2 * u)
        neon(Path().apply { moveTo(cx + r * 0.7f, cy - r * 1.3f); lineTo(cx + r * 1.8f, cy - r * 0.05f) }, AMBAR, 2 * u)
        for (j in 0 until 3) {
            val vx = cx - r * 0.6f + j * r * 0.5f
            neon(Path().apply { moveTo(vx, cy - r * 0.2f); quadraticTo(vx - r * 0.25f, cy - r * 0.6f, vx, cy - r * 0.9f); quadraticTo(vx + r * 0.25f, cy - r * 1.2f, vx, cy - r * 1.5f) }, CIAN, 1.4f * u)
        }
        neon(Path().apply { addRect(Rect(w * 0.18f, d.corona.top + ch * 0.06f, w * 0.82f, d.corona.top + ch * 0.58f)) }, AMBAR, 1.2f * u)
        // El toldo a franjas, con su festón.
        val toldo = Rect(0f, d.corona.top + ch * 0.62f, w, d.corona.bottom - 4 * u)
        val nf = 12
        for (j in 0 until nf) drawRect(if (j % 2 == 0) LONA else LONA_OSC, Offset(w * j / nf, toldo.top), Size(w / nf + 0.5f, toldo.height * 0.6f))
        for (j in 0 until nf) drawArc(if (j % 2 == 0) LONA else LONA_OSC, 0f, 180f, true, Offset(w * j / nf, toldo.top + toldo.height * 0.6f - toldo.height * 0.4f), Size(w / nf, toldo.height * 0.8f))
        drawRect(MAGENTA.copy(alpha = 0.18f), toldo.topLeft, Size(w, 2 * u))
        // Un farolito de papel rojo colgando a un lado.
        val fc = Offset(w * 0.08f, toldo.bottom + 18 * u)
        mancha(fc, 26 * u, FAROL.copy(alpha = 0.35f))
        drawOval(FAROL, Offset(fc.x - 9 * u, fc.y - 11 * u), Size(18 * u, 22 * u))
        for (j in -1..1) drawLine(Color.Black.copy(alpha = 0.3f), Offset(fc.x + j * 4 * u, fc.y - 10 * u), Offset(fc.x + j * 4 * u, fc.y + 10 * u), 0.8f * u)
        // La barra, sus taburetes y los charcos.
        val barra = Rect(0f, d.base.top + d.base.height * 0.15f, w, d.base.top + d.base.height * 0.38f)
        drawRect(ACERO_LUZ, barra.topLeft, barra.size)
        drawLine(MAGENTA, Offset(0f, barra.bottom), Offset(w, barra.bottom), 1.6f * u)
        drawLine(MAGENTA.copy(alpha = 0.25f), Offset(0f, barra.bottom + 2 * u), Offset(w, barra.bottom + 2 * u), 4 * u)
        drawRect(NOCHE, Offset(0f, barra.bottom + 4 * u), Size(w, d.base.bottom - barra.bottom - 4 * u))
        listOf(0.18f, 0.5f, 0.82f).forEach { f ->
            val tx = w * f
            drawLine(ACERO_LUZ, Offset(tx, barra.bottom + 10 * u), Offset(tx, d.base.bottom - 4 * u), 2 * u)
            drawOval(LONA, Offset(tx - 14 * u, barra.bottom + 6 * u), Size(28 * u, 8 * u))
        }
        drawOval(MAGENTA.copy(alpha = 0.2f), Offset(w * 0.3f, d.base.bottom - 10 * u), Size(w * 0.3f, 6 * u))
        drawOval(CIAN.copy(alpha = 0.16f), Offset(w * 0.62f, d.base.bottom - 8 * u), Size(w * 0.22f, 5 * u))
        if (d.mas > 0) placa.let { pl ->
            val r2 = d.enBase(pl)
            drawRoundRect(Color.Black, r2.topLeft, r2.size, CornerRadius(3 * u))
            neon(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r2, CornerRadius(3 * u))) }, SODIO, 1 * u)
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: una pinza vacía en el riel.
            drawRect(ACERO_LUZ, Offset(w * 0.45f, 2 * u), Size(w * 0.1f, h * 0.08f))
            return
        }
        // Los tickets térmicos colgados de sus pinzas, en abanico (se balancean al abrirlo).
        val k = c.n.coerceIn(1, 6)
        for (i in 0 until k) {
            val x = w * (0.5f + (i - (k - 1) / 2f) * 0.13f)
            val oscila = (i - (k - 1) / 2f) * 4f + ab * 8f * sin(i * 1.7f)
            rotate(oscila, Offset(x, 4 * u)) {
                val alto = h * (0.42f + 0.14f * azar(c.semilla, i))
                drawPath(dentado(w * 0.12f, alto, 4, 3 * u).apply { translate(Offset(x - w * 0.06f, h * 0.06f)) }, TICKET)
                for (j in 0 until 5) drawLine(Color.Black.copy(alpha = 0.35f), Offset(x - w * 0.045f, h * 0.12f + j * h * 0.06f), Offset(x + w * (0.01f + 0.03f * azar(c.semilla, i * 7 + j)), h * 0.12f + j * h * 0.06f), 1f * u)
                drawRect(MAGENTA.copy(alpha = 0.5f), Offset(x - w * 0.06f, h * 0.06f), Size(2 * u, alto - 3 * u))
                drawRect(ACERO_LUZ, Offset(x - w * 0.035f, h * 0.02f), Size(w * 0.07f, h * 0.06f))
            }
        }
        // El letrerito de neón verde sobre su placa negra.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(Color.Black, r.topLeft, r.size, CornerRadius(4 * u))
        neon(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.bottom - 3 * u), CornerRadius(3 * u))) }, SODIO, 1.2f * u)
    }

    override val rotulo = Rect(0.12f, 0.64f, 0.88f, 0.96f)
    override fun tintaRotulo(oscuro: Boolean) = Color(0xFFDFFFD6)
    override val letraRotulo: FontFamily = Letra.letrero
    override val salida = 0f
    override val placa = Rect(0.3f, 0.45f, 0.7f, 0.92f)
    override fun tintaPlaca(oscuro: Boolean) = Color(0xFFDFFFD6)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La plancha de acero cepillado, con reflejos de neón y vapor.
        drawRect(ACERO)
        var y = 0f
        while (y < h) { drawLine(ACERO_LUZ.copy(alpha = 0.12f), Offset(0f, y), Offset(w, y + 1 * u), 0.8f * u); y += 4 * u }
        mancha(Offset(w * 0.2f, h * 0.3f), w * 0.6f, MAGENTA.copy(alpha = 0.16f))
        mancha(Offset(w * 0.85f, h * 0.7f), w * 0.5f, CIAN.copy(alpha = 0.12f))
        for (k in 0 until 10) mancha(Offset(w * azar(61, k), h * azar(62, k)), (30f + 40f * azar(63, k)) * u, Color.White.copy(alpha = 0.05f))
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(ACERO_OSC, r.topLeft, r.size) }
        drawRect(MAGENTA.copy(alpha = 0.5f), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(dentado(s.width, s.height, 14, minOf(s.width, s.height) * 0.03f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El canto magenta, la línea de puntos del corte y un código de barras tenue.
        drawRect(MAGENTA.copy(alpha = 0.7f), Offset.Zero, Size(2.5f * u * g, h))
        var x = 6 * u
        while (x < w) { drawCircle(TEXTO.copy(alpha = 0.25f), 0.8f * u * g, Offset(x, 3 * u * g)); x += 4 * u * g }
        var bx = w - 30 * u * g; var k = 0
        while (bx < w - 6 * u * g) {
            val bw = (0.8f + 1.6f * azar(semilla, k)) * u * g
            drawRect(TEXTO.copy(alpha = 0.16f), Offset(bx, h - 18 * u * g), Size(bw, 9 * u * g))
            bx += bw + 1.2f * u * g; k++
        }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 16.dp, 24.dp, 26.dp, 34.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> { drawPath(corazon(c, s * 0.9f), MAGENTA.copy(alpha = 0.3f), style = Stroke(s * 0.2f)); drawPath(corazon(c, s * 0.9f), MAGENTA, style = Stroke(s * 0.08f)) }
            // Unos palillos.
            "piensa" -> {
                drawLine(AMBAR, Offset(c.x - s * 0.3f, c.y + s * 0.4f), Offset(c.x + s * 0.06f, c.y - s * 0.42f), s * 0.08f, StrokeCap.Round)
                drawLine(AMBAR, Offset(c.x - s * 0.1f, c.y + s * 0.42f), Offset(c.x + s * 0.3f, c.y - s * 0.38f), s * 0.08f, StrokeCap.Round)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(MAGENTA)
            else -> {}
        }
    }

    /** Una pinza de comanda. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.38f, c.y + s * 0.3f); lineTo(c.x - s * 0.3f, c.y - s * 0.1f); lineTo(c.x + s * 0.3f, c.y - s * 0.1f); lineTo(c.x + s * 0.38f, c.y + s * 0.3f); close() }, ACERO_LUZ)
        drawArc(TEXTO, 180f, 180f, false, Offset(c.x - s * 0.26f, c.y - s * 0.46f), Size(s * 0.24f, s * 0.7f), style = Stroke(s * 0.06f))
        drawArc(TEXTO, 180f, 180f, false, Offset(c.x + s * 0.02f, c.y - s * 0.46f), Size(s * 0.24f, s * 0.7f), style = Stroke(s * 0.06f))
    }

    override val desorden = 2.5f
}

/**
 * Distopía: el archivo gris del ministerio. Arriba, el ojo que todo lo ve sobre una banda roja de cartel con sus estrellas, y
 * una cámara de vigilancia con su luz roja; el archivero de acero gris lleva números de inventario estarcidos y un zócalo con
 * rejilla. Cada tema es una gaveta con su tarjeta mecanografiada, la cerradura, el tirador y la franja roja de «clasificado»;
 * los más llenos quedan entreabiertos con sus carpetas de manila. Por dentro, la gaveta de acero con sus rieles para carpetas
 * colgantes. Las fichas son expedientes en su carpeta de manila con pestaña.
 */
object IdeasDistopia : DisenoIdeas {
    private val GRIS = Color(0xFF6E7378)
    private val GRIS_LUZ = Color(0xFF9AA0A6)
    private val GRIS_OSC = Color(0xFF3E4246)
    private val ROJO = Color(0xFFC8102E)
    private val NEGRO = Color(0xFF141414)
    private val MANILA = Color(0xFFD9C48E)
    private val PAPEL = Color(0xFFECE8DC)
    private val HORMIGON = Color(0xFFB8B6AE)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 2.2f
    override val corona = 0.32f
    override val base = 0.1f
    override val lado = 0.06f
    override val hueco = 0.025f

    private const val SALIDA = 0.16f

    /** El ojo: una almendra blanca con su iris y su pupila. */
    private fun DrawScope.ojo(c: Offset, a: Float, iris: Color, contorno: Color) {
        val almendra = Path().apply { moveTo(c.x - a, c.y); quadraticTo(c.x, c.y - a * 0.9f, c.x + a, c.y); quadraticTo(c.x, c.y + a * 0.9f, c.x - a, c.y); close() }
        drawPath(almendra, Color.White)
        clipPath(almendra) { drawCircle(iris, a * 0.38f, c); drawCircle(NEGRO, a * 0.18f, c) }
        drawPath(almendra, contorno, style = Stroke(a * 0.08f))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // La pared de hormigón, el ojo que todo lo ve y sus rayos, y la banda roja de cartel con estrellas.
        drawRect(t(HORMIGON, oscuro), d.corona.topLeft, Size(w, ch))
        val oc = Offset(w / 2, d.corona.top + ch * 0.32f)
        for (k in 0 until 24) {
            val a = k * Math.PI / 12
            drawLine(t(GRIS_OSC, oscuro).copy(alpha = 0.3f), oc, Offset(oc.x + ch * 0.6f * cos(a).toFloat(), oc.y + ch * 0.6f * sin(a).toFloat()), 1.2f * u)
        }
        drawOval(t(GRIS_OSC, oscuro), Offset(oc.x - ch * 0.38f, oc.y - ch * 0.24f), Size(ch * 0.76f, ch * 0.48f))
        ojo(oc, ch * 0.3f, t(GRIS, oscuro), t(NEGRO, oscuro))
        val banda = Rect(0f, d.corona.top + ch * 0.66f, w, d.corona.bottom - 6 * u)
        drawRect(t(ROJO, oscuro), banda.topLeft, banda.size)
        listOf(0.08f, 0.92f).forEach { f -> drawPath(estrella(Offset(w * f, banda.center.y), banda.height * 0.36f, 5, 0.42f), t(NEGRO, oscuro)) }
        var x = w * 0.18f
        while (x < w * 0.82f) { drawRect(t(PAPEL, oscuro), Offset(x, banda.center.y - banda.height * 0.14f), Size(w * 0.035f, banda.height * 0.28f)); x += w * 0.05f }
        // La cámara de vigilancia en la esquina, con su luz roja.
        val cam = Offset(w * 0.86f, d.corona.top + ch * 0.18f)
        drawLine(t(GRIS_OSC, oscuro), Offset(w * 0.96f, cam.y - 6 * u), Offset(cam.x + 8 * u, cam.y), 2.4f * u)
        rotate(18f, cam) {
            drawRect(t(GRIS_LUZ, oscuro), Offset(cam.x - 18 * u, cam.y - 6 * u), Size(26 * u, 12 * u))
            drawRect(t(NEGRO, oscuro), Offset(cam.x - 22 * u, cam.y - 4 * u), Size(5 * u, 8 * u))
            drawCircle(ROJO, 1.8f * u, Offset(cam.x + 4 * u, cam.y - 3 * u))
        }
        // El archivero de acero gris, con los números de inventario estarcidos.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 6 * u, w * 0.985f, d.base.top + d.base.height * 0.5f)
        drawRect(Brush.horizontalGradient(listOf(t(GRIS_OSC, oscuro), t(GRIS, oscuro), t(GRIS_LUZ, oscuro), t(GRIS, oscuro)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.4f * u, r.top - 1.4f * u), Size(r.width + 2.8f * u, r.height + 2.8f * u)) }
        d.filas.forEachIndexed { i, f ->
            for (j in 0 until 3) drawRect(t(NEGRO, oscuro).copy(alpha = 0.55f), Offset(cuerpo.left + 3 * u, f.top + 6 * u + j * 6 * u), Size(w * 0.025f, 3.5f * u))
            drawRect(t(NEGRO, oscuro).copy(alpha = 0.55f), Offset(cuerpo.left + 3 * u, f.top + 26 * u), Size(w * 0.012f * (1 + i % 3), 3.5f * u))
        }
        // El zócalo con su rejilla.
        val zoc = Rect(cuerpo.left, cuerpo.bottom, cuerpo.right, d.base.bottom)
        drawRect(t(GRIS_OSC, oscuro), zoc.topLeft, zoc.size)
        var gx = zoc.left + 10 * u
        while (gx < zoc.right - 10 * u) { drawRoundRect(t(NEGRO, oscuro), Offset(gx, zoc.top + zoc.height * 0.3f), Size(3 * u, zoc.height * 0.4f), CornerRadius(1.5f * u)); gx += 7 * u }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(NEGRO, oscuro), r.topLeft, r.size, style = Stroke(1.2f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Las gavetas más llenas quedan entreabiertas y asoman las carpetas de manila, con sus pestañas.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF0E1012), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val y0 = arriba * (0.15f + 0.08f * i).coerceAtMost(0.8f)
                drawRect(t(MANILA, oscuro), Offset(w * 0.06f, y0), Size(w * 0.88f, arriba + 4 * u - y0))
                drawRect(t(MANILA, oscuro), Offset(w * (0.1f + 0.2f * (i % 4)), y0 - 3 * u), Size(w * 0.14f, 3.5f * u))
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(w * 0.06f, y0), Offset(w * 0.94f, y0), 0.6f * u)
            }
        }
        // El frente de acero gris, con la franja roja de «clasificado» en la esquina.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(GRIS, oscuro), f.topLeft, f.size)
        bisel(f, Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.35f), 1.2f * u)
        if (!c.vacio) clipRect(f.left, f.top, f.right, f.bottom) {
            rotate(-35f, Offset(f.right - w * 0.08f, f.top + (f.bottom - f.top) * 0.25f)) {
                drawRect(t(ROJO, oscuro).copy(alpha = 0.8f), Offset(f.right - w * 0.3f, f.top + (f.bottom - f.top) * 0.18f), Size(w * 0.44f, 8 * u))
            }
        }
        // El portaetiquetas con la tarjeta mecanografiada.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (!c.vacio) drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(GRIS_OSC, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(2 * u))
        // La cerradura y el tirador.
        val cerr = Offset(w * 0.1f, h * 0.36f + baja)
        drawCircle(t(GRIS_LUZ, oscuro), 5 * u, cerr)
        drawRect(t(NEGRO, oscuro), Offset(cerr.x - 0.8f * u, cerr.y - 2.5f * u), Size(1.6f * u, 5 * u))
        val ty = h * 0.8f + baja
        drawRoundRect(t(GRIS_OSC, oscuro), Offset(w * 0.38f, ty - 3 * u), Size(w * 0.24f, 6 * u), CornerRadius(3 * u))
        drawLine(Color.White.copy(alpha = 0.25f), Offset(w * 0.39f, ty - 2 * u), Offset(w * 0.61f, ty - 2 * u), 0.8f * u)
    }

    override val rotulo = Rect(0.24f, 0.16f, 0.76f, 0.6f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.oswald
    override val salida = SALIDA
    override val placa = Rect(0.3f, 0.08f, 0.7f, 0.46f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la gaveta de acero, con los rieles de las carpetas colgantes a los lados.
        drawRect(t(GRIS_OSC, oscuro))
        for (k in 0 until 40) drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, h * azar(71, k)), Offset(w, h * azar(71, k) + 1 * u), 1f * u)
        listOf(20 * u, w - 20 * u).forEach { x ->
            drawLine(t(GRIS_LUZ, oscuro), Offset(x, 0f), Offset(x, h), 3 * u)
            var y = 30 * u
            while (y < h) { drawRect(t(GRIS_LUZ, oscuro), Offset(x - 6 * u, y), Size(12 * u, 2.5f * u)); y += 46 * u }
        }
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(GRIS, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 28.dp
    override val columnas = 2
    override val lineas = 5

    /** La carpeta de manila con su pestaña arriba a la izquierda. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val t = minOf(s.width, s.height) * 0.08f
        moveTo(0f, t); lineTo(s.width * 0.06f, t); lineTo(s.width * 0.09f, 0f); lineTo(s.width * 0.42f, 0f); lineTo(s.width * 0.45f, t)
        lineTo(s.width, t); lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val t = minOf(w, h) * 0.08f
        drawRect(MANILA.copy(alpha = 0.4f))
        // La etiqueta censurada en la pestaña, el sello rojo tenue y un clip.
        drawRect(NEGRO.copy(alpha = 0.8f), Offset(w * 0.13f, t * 0.3f), Size(w * 0.24f, t * 0.45f))
        val sc = Offset(w * 0.78f, h * 0.62f)
        rotate(-15f, sc) {
            drawCircle(ROJO.copy(alpha = 0.16f), minOf(w, h) * 0.16f, sc, style = Stroke(2 * u * g))
            drawCircle(ROJO.copy(alpha = 0.12f), minOf(w, h) * 0.12f, sc, style = Stroke(1 * u * g))
        }
        drawRoundRect(GRIS.copy(alpha = 0.8f), Offset(w - 16 * u * g, t - 4 * u * g), Size(6 * u * g, 16 * u * g), CornerRadius(3 * u * g), style = Stroke(1.2f * u * g))
    }

    override val margenes = MargenesFicha(16.dp, 13.dp, 12.dp, 30.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.74f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello de «aprobado».
            "convence" -> {
                drawCircle(ROJO, s * 0.42f, c, style = Stroke(s * 0.08f))
                drawPath(Path().apply { moveTo(c.x - s * 0.22f, c.y); lineTo(c.x - s * 0.05f, c.y + s * 0.17f); lineTo(c.x + s * 0.24f, c.y - s * 0.18f) }, ROJO, style = Stroke(s * 0.1f))
            }
            "piensa" -> ojo(c, s * 0.46f, GRIS, NEGRO)
            "duda" -> circuloDuda(p.tenue)
            // La barra negra de la censura.
            "desacuerdo" -> drawRect(NEGRO, Offset(c.x - s * 0.44f, c.y - s * 0.14f), Size(s * 0.88f, s * 0.28f))
            else -> {}
        }
    }

    /** Una grapa. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + s * 0.2f); lineTo(c.x - s * 0.36f, c.y - s * 0.06f); lineTo(c.x + s * 0.36f, c.y - s * 0.06f); lineTo(c.x + s * 0.36f, c.y + s * 0.2f) }, GRIS, style = Stroke(s * 0.1f))
    }

    override val desorden = 1f
}

/**
 * Refugio: el armario de cartuchos junto a la terminal. Arriba, la puerta engranada de la bóveda con su aro de franjas de
 * aviso; el armario es de acero verde oliva remachado con sus luces piloto ámbar; abajo, el piso de rejilla. Cada tema es un
 * cartucho de cinta en su ranura, con su ventana de carretes (más cinta cuanto más lleno), la etiqueta y el LED; al abrirlo,
 * el cartucho sale de la ranura. Por dentro, una pantalla de fósforo verde con su bisel. Las fichas son registros en una
 * pantalla CRT de esquinas redondas.
 */
object IdeasRefugio : DisenoIdeas {
    private val OLIVA = Color(0xFF4A5A3A)
    private val OLIVA_LUZ = Color(0xFF6A7A52)
    private val OLIVA_OSC = Color(0xFF2A3420)
    private val ACERO = Color(0xFF7A8088)
    private val ACERO_OSC = Color(0xFF4A5056)
    private val AMBAR = Color(0xFFFFB23F)
    private val FOSFORO = Color(0xFF3DFF7F)
    private val FOSFORO_CLARO = Color(0xFF9CF7B4)
    private val NEGRO = Color(0xFF031A0B)
    private val CINTA = Color(0xFF2A2A2A)
    private val CINTA_LUZ = Color(0xFF4A4A4A)
    private val ETIQUETA = Color(0xFFE8E0C8)
    private val TINTA = Color(0xFF1A1A14)
    private val AMARILLO = Color(0xFFE0B83A)
    private val HORMIGON = Color(0xFF8A8A82)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 0.8f
    override val corona = 0.34f
    override val base = 0.1f
    override val lado = 0.07f
    override val hueco = 0.03f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // La pared de hormigón y la puerta engranada de la bóveda, con su aro de franjas y sus pernos.
        drawRect(t(HORMIGON, oscuro), d.corona.topLeft, Size(w, ch))
        for (k in 0 until 3) drawLine(Color.Black.copy(alpha = 0.15f), Offset(0f, d.corona.top + ch * (0.25f + 0.25f * k)), Offset(w, d.corona.top + ch * (0.25f + 0.25f * k)), 1f * u)
        val pc = Offset(w / 2, d.corona.top + ch * 0.52f)
        val pr = ch * 0.46f
        clipRect(0f, d.corona.top, w, d.corona.bottom - 6 * u) {
            drawCircle(t(AMARILLO, oscuro), pr * 1.12f, pc)
            for (k in 0 until 16) if (k % 2 == 0) drawArc(Color.Black, k * 22.5f, 22.5f, true, Offset(pc.x - pr * 1.12f, pc.y - pr * 1.12f), Size(pr * 2.24f, pr * 2.24f))
            drawCircle(t(ACERO_OSC, oscuro), pr * 0.98f, pc)
            drawPath(engrane(pc, pr * 0.94f, 14), t(ACERO, oscuro))
            drawCircle(t(ACERO_OSC, oscuro), pr * 0.55f, pc)
            drawCircle(t(ACERO, oscuro), pr * 0.48f, pc)
            for (k in 0 until 8) { val a = k * Math.PI / 4; drawCircle(t(ACERO_OSC, oscuro), pr * 0.05f, Offset(pc.x + pr * 0.38f * cos(a).toFloat(), pc.y + pr * 0.38f * sin(a).toFloat())) }
            drawRect(t(AMARILLO, oscuro), Offset(pc.x - pr * 0.24f, pc.y - pr * 0.1f), Size(pr * 0.48f, pr * 0.2f))
            for (k in 0 until 3) drawRect(Color.Black, Offset(pc.x - pr * 0.18f + k * pr * 0.13f, pc.y - pr * 0.07f), Size(pr * 0.08f, pr * 0.14f))
        }
        // El armario de acero oliva, remachado, con la ranura oscura de cada cartucho.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, d.base.top + d.base.height * 0.4f)
        drawRect(t(OLIVA, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(t(OLIVA_OSC, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(3 * u))
        var y = cuerpo.top + 6 * u
        while (y < cuerpo.bottom - 4 * u) { drawCircle(t(OLIVA_LUZ, oscuro), 1.3f * u, Offset(cuerpo.left + 5 * u, y)); drawCircle(t(OLIVA_LUZ, oscuro), 1.3f * u, Offset(cuerpo.right - 5 * u, y)); y += 16 * u }
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.7f), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u)) }
        // Las luces piloto ámbar en el travesaño de arriba.
        listOf(0.2f, 0.26f, 0.32f).forEachIndexed { i, f ->
            val lc = Offset(w * f, cuerpo.top + 4 * u)
            if (i != 2) mancha(lc, 9 * u, AMBAR.copy(alpha = 0.5f))
            drawCircle(if (i != 2) AMBAR else t(OLIVA_OSC, oscuro), 2.4f * u, lc)
        }
        // El piso de rejilla.
        val piso = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(ACERO_OSC, oscuro), piso.topLeft, piso.size)
        var gx = 0f
        while (gx < w) { drawLine(Color.Black.copy(alpha = 0.6f), Offset(gx, piso.top), Offset(gx, piso.bottom), 1.2f * u); gx += 6 * u }
        drawLine(Color.Black.copy(alpha = 0.6f), Offset(0f, piso.center.y), Offset(w, piso.center.y), 1.2f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(ETIQUETA, oscuro), r.topLeft, r.size)
            drawRect(t(AMARILLO, oscuro), r.topLeft, Size(4 * u, r.height))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // La ranura.
        drawRect(Color(0xFF080A06))
        drawRect(t(OLIVA_OSC, oscuro), Offset(0f, h * 0.92f), Size(w, h * 0.08f))
        if (c.vacio) {
            drawCircle(t(OLIVA_LUZ, oscuro), 2 * u, Offset(w / 2, h * 0.96f))
            return
        }
        // El cartucho, que sale hacia arriba al abrirlo.
        val sube = h * 0.12f * ab
        val cart = Rect(w * 0.1f, h * 0.16f - sube, w * 0.9f, h * 0.98f - sube)
        drawRoundRect(t(CINTA, oscuro), cart.topLeft, cart.size, CornerRadius(4 * u))
        for (j in 0 until 4) drawLine(t(CINTA_LUZ, oscuro), Offset(cart.left + 4 * u, cart.top + 4 * u + j * 3 * u), Offset(cart.right - 4 * u, cart.top + 4 * u + j * 3 * u), 1f * u)
        // La ventana con los dos carretes: el de la izquierda, más lleno cuanto más fichas.
        val ven = Rect(cart.left + cart.width * 0.1f, cart.top + cart.height * 0.2f, cart.right - cart.width * 0.1f, cart.top + cart.height * 0.44f)
        drawRoundRect(Color(0xFF0E1410), ven.topLeft, ven.size, CornerRadius(ven.height * 0.4f))
        val rr = ven.height * 0.42f
        val lleno = (c.n.coerceIn(0, 8) / 8f)
        listOf(Offset(ven.left + ven.width * 0.27f, ven.center.y) to (0.4f + 0.6f * lleno), Offset(ven.right - ven.width * 0.27f, ven.center.y) to (0.4f + 0.6f * (1 - lleno))).forEach { (o, f) ->
            drawCircle(Color(0xFF3A2A1A), rr * f, o)
            drawCircle(t(ETIQUETA, oscuro), rr * 0.32f, o)
            for (k in 0 until 3) { val a = k * 2 * Math.PI / 3; drawLine(Color.Black, o, Offset(o.x + rr * 0.3f * cos(a).toFloat(), o.y + rr * 0.3f * sin(a).toFloat()), 1f * u) }
        }
        drawRoundRect(Color.White.copy(alpha = 0.12f), ven.topLeft, ven.size, CornerRadius(ven.height * 0.4f), style = Stroke(1f * u))
        // La etiqueta y el LED.
        val r = Rect(w * rotulo.left, h * rotulo.top - sube, w * rotulo.right, h * rotulo.bottom - sube)
        drawRect(t(ETIQUETA, oscuro), r.topLeft, r.size)
        drawRect(t(AMARILLO, oscuro), r.topLeft, Size(r.width, 3 * u))
        val led = Offset(cart.right - 8 * u, cart.bottom - 8 * u)
        mancha(led, 7 * u, FOSFORO.copy(alpha = 0.6f))
        drawCircle(FOSFORO, 2 * u, led)
    }

    override val rotulo = Rect(0.16f, 0.6f, 0.84f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.vt323
    override val salida = -0.12f
    override val placa = Rect(0.3f, 0.48f, 0.7f, 0.92f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La pantalla de fósforo verde: viñeta, líneas de barrido y el bisel abombado.
        drawRect(Brush.radialGradient(listOf(Color(0xFF0A3A18), NEGRO), Offset(w / 2, h / 2), maxOf(w, h) * 0.7f))
        var y = 0f
        while (y < h) { drawLine(FOSFORO.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        val b = 16 * u
        drawRoundRect(t(OLIVA_OSC, oscuro), Offset(b / 2, b / 2), Size(w - b, h - b), CornerRadius(28 * u), style = Stroke(b))
        drawRect(t(OLIVA, oscuro), Offset.Zero, size, style = Stroke(6 * u))
        drawRoundRect(Color.White.copy(alpha = 0.06f), Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(22 * u), style = Stroke(2 * u))
    }

    override fun tintaInterior(oscuro: Boolean) = FOSFORO_CLARO
    override val pared: Dp = 20.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(14.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El barrido, la viñeta en los bordes, el bisel y el cursor de bloque.
        var y = 0f
        while (y < h) { drawLine(FOSFORO.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        val v = 10 * u * g
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), 0f, v), Offset.Zero, Size(w, v))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)), h - v, h), Offset(0f, h - v), Size(w, v))
        drawRoundRect(OLIVA_OSC, Offset(1.5f * u * g, 1.5f * u * g), Size(w - 3 * u * g, h - 3 * u * g), CornerRadius(12 * u * g), style = Stroke(3 * u * g))
        drawRect(FOSFORO.copy(alpha = 0.7f), Offset(w - 16 * u * g, h - 18 * u * g), Size(6 * u * g, 10 * u * g))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 30.dp)
    override val proporcionFicha = 1.15f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> {
                drawCircle(FOSFORO, s * 0.42f, c, style = Stroke(s * 0.08f))
                drawPath(Path().apply { moveTo(c.x - s * 0.2f, c.y); lineTo(c.x - s * 0.04f, c.y + s * 0.16f); lineTo(c.x + s * 0.22f, c.y - s * 0.16f) }, FOSFORO, style = Stroke(s * 0.1f))
            }
            // El átomo.
            "piensa" -> {
                listOf(0f, 60f, 120f).forEach { a -> rotate(a, c) { drawOval(AMBAR, Offset(c.x - s * 0.44f, c.y - s * 0.16f), Size(s * 0.88f, s * 0.32f), style = Stroke(s * 0.05f)) } }
                drawCircle(AMBAR, s * 0.08f, c)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(AMBAR)
            else -> {}
        }
    }

    /** Una chapa de identificación con su cadenita. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRoundRect(ACERO, Offset(c.x - s * 0.24f, c.y - s * 0.3f), Size(s * 0.48f, s * 0.7f), CornerRadius(s * 0.14f))
        drawCircle(Color.Black.copy(alpha = 0.6f), s * 0.05f, Offset(c.x, c.y - s * 0.2f))
        for (k in 0 until 3) drawCircle(ACERO_OSC, s * 0.04f, Offset(c.x - s * 0.08f * k, c.y - s * 0.34f - s * 0.06f * k))
        for (k in 0 until 3) drawLine(ACERO_OSC, Offset(c.x - s * 0.14f, c.y + s * (0.0f + 0.1f * k)), Offset(c.x + s * 0.14f, c.y + s * (0.0f + 0.1f * k)), s * 0.03f)
    }

    override val desorden = 0f
}

/**
 * Orden: la bóveda de acero de la orden. Placas de blindaje remachadas; arriba, un dintel de acero y un estandarte carmesí con
 * el escudo del engrane y la espada en latón; abajo, franjas de peligro. Cada tema es una gaveta blindada con su placa de
 * latón estampada, dos pestillos y una manija industrial; las más llenas quedan entreabiertas con sus planos. Por dentro, un
 * plano azul con su cuadrícula. Las fichas son fichas técnicas con su cuadrícula, su cajetín y sus marcas de registro.
 */
object IdeasOrden : DisenoIdeas {
    private val ACERO = Color(0xFF3A4048)
    private val ACERO_LUZ = Color(0xFF5E6670)
    private val ACERO_OSC = Color(0xFF1C2127)
    private val CARMESI = Color(0xFFB0202A)
    private val CARMESI_OSC = Color(0xFF6E1018)
    private val LATON = Color(0xFFD6B25A)
    private val LATON_OSC = Color(0xFF8A7030)
    private val TEXTO = Color(0xFFDDE2E7)
    private val PLANO = Color(0xFF1E3A5C)
    private val AMARILLO = Color(0xFFE0B83A)
    private val NEGRO = Color(0xFF101214)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.25f) else c

    override val cols = 2
    override val proporcion = 2f
    override val corona = 0.34f
    override val base = 0.11f
    override val lado = 0.07f
    override val hueco = 0.03f

    private const val SALIDA = 0.16f

    private fun DrawScope.remache(p: Offset, r: Float) {
        drawCircle(ACERO_LUZ, r, p)
        drawCircle(Color.White.copy(alpha = 0.35f), r * 0.4f, Offset(p.x - r * 0.3f, p.y - r * 0.3f))
        drawCircle(Color.Black.copy(alpha = 0.4f), r, p, style = Stroke(r * 0.25f))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // Las placas de blindaje, con su bisel y un remache en cada esquina.
        val muro = Rect(0f, d.corona.top + ch * 0.2f, w, d.base.top + d.base.height * 0.4f)
        drawRect(t(ACERO_OSC, oscuro), muro.topLeft, muro.size)
        val pw = w / 3; val ph = 110 * u
        var y = muro.top
        while (y < muro.bottom) {
            var x = 0f
            while (x < w) {
                val r = Rect(x + 1.5f * u, y + 1.5f * u, x + pw - 1.5f * u, minOf(y + ph, muro.bottom) - 1.5f * u)
                drawRect(t(ACERO, oscuro), r.topLeft, r.size)
                bisel(r, Color.White.copy(alpha = 0.08f), Color.Black.copy(alpha = 0.3f), 1.5f * u)
                listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), Offset(r.right, r.bottom)).forEach { o -> remache(Offset(o.x + (if (o.x < r.center.x) 6 else -6) * u, o.y + (if (o.y < r.center.y) 6 else -6) * u), 2 * u) }
                x += pw
            }
            y += ph
        }
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.6f), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u)) }
        // El estandarte carmesí colgando del dintel, con el escudo del engrane y la espada.
        val est = Rect(w * 0.36f, d.corona.top + ch * 0.12f, w * 0.64f, d.corona.bottom + 2 * u)
        drawPath(Path().apply { moveTo(est.left, est.top); lineTo(est.right, est.top); lineTo(est.right, est.bottom); lineTo(est.center.x, est.bottom - est.height * 0.14f); lineTo(est.left, est.bottom); close() }, t(CARMESI, oscuro))
        drawLine(t(CARMESI_OSC, oscuro), Offset(est.left + 4 * u, est.top), Offset(est.left + 4 * u, est.bottom - 4 * u), 1.4f * u)
        drawLine(t(CARMESI_OSC, oscuro), Offset(est.right - 4 * u, est.top), Offset(est.right - 4 * u, est.bottom - 4 * u), 1.4f * u)
        val ec = Offset(est.center.x, est.top + est.height * 0.48f)
        val ea = est.width * 0.62f; val eh = est.height * 0.62f
        val escudo = Path().apply { moveTo(ec.x - ea / 2, ec.y - eh / 2); lineTo(ec.x + ea / 2, ec.y - eh / 2); lineTo(ec.x + ea / 2, ec.y); quadraticTo(ec.x + ea / 2, ec.y + eh * 0.35f, ec.x, ec.y + eh / 2); quadraticTo(ec.x - ea / 2, ec.y + eh * 0.35f, ec.x - ea / 2, ec.y); close() }
        drawPath(escudo, t(ACERO_LUZ, oscuro))
        drawPath(escudo, t(LATON, oscuro), style = Stroke(1.8f * u))
        drawPath(engrane(Offset(ec.x, ec.y - eh * 0.04f), ea * 0.3f, 10), t(LATON, oscuro))
        drawCircle(t(ACERO_LUZ, oscuro), ea * 0.12f, Offset(ec.x, ec.y - eh * 0.04f))
        drawLine(t(LATON_OSC, oscuro), Offset(ec.x, ec.y - eh * 0.42f), Offset(ec.x, ec.y + eh * 0.38f), 2.4f * u)
        drawLine(t(LATON_OSC, oscuro), Offset(ec.x - ea * 0.18f, ec.y - eh * 0.22f), Offset(ec.x + ea * 0.18f, ec.y - eh * 0.22f), 2.4f * u)
        // El dintel de acero remachado.
        val dintel = Rect(0f, d.corona.top + ch * 0.04f, w, d.corona.top + ch * 0.16f)
        drawRect(t(ACERO_LUZ, oscuro), dintel.topLeft, dintel.size)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, dintel.bottom), Size(w, 3 * u))
        var rx = 8 * u
        while (rx < w) { remache(Offset(rx, dintel.center.y), 2 * u); rx += 18 * u }
        // Las franjas de peligro del zócalo, y el piso.
        val zoc = Rect(0f, muro.bottom, w, d.base.bottom)
        drawRect(t(NEGRO, oscuro), zoc.topLeft, zoc.size)
        clipRect(zoc.left, zoc.top, zoc.right, zoc.top + zoc.height * 0.55f) {
            var x = -zoc.height
            while (x < w) { drawPath(Path().apply { moveTo(x, zoc.top + zoc.height * 0.55f); lineTo(x + 10 * u, zoc.top); lineTo(x + 20 * u, zoc.top); lineTo(x + 10 * u, zoc.top + zoc.height * 0.55f); close() }, t(AMARILLO, oscuro)); x += 20 * u }
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(LATON, oscuro), r.topLeft, r.size)
            drawRect(t(LATON_OSC, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Las gavetas más llenas quedan entreabiertas y asoman los planos azules.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF080A0C), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.05f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                drawRect(t(PLANO, oscuro), Offset(x, y0), Size(w * 0.1f, arriba + 4 * u - y0))
                drawLine(TEXTO.copy(alpha = 0.4f), Offset(x + 2 * u, y0 + 3 * u), Offset(x + w * 0.08f, y0 + 3 * u), 0.6f * u)
            }
        }
        // El frente blindado, con su panel hundido.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(ACERO_LUZ, oscuro), f.topLeft, f.size)
        bisel(f, Color.White.copy(alpha = 0.18f), Color.Black.copy(alpha = 0.4f), 1.6f * u)
        val fh = f.bottom - f.top
        val panel = Rect(f.left + 8 * u, f.top + 6 * u, f.right - 8 * u, f.bottom - 6 * u)
        drawRect(t(ACERO, oscuro), panel.topLeft, panel.size)
        bisel(panel, Color.Black.copy(alpha = 0.35f), Color.White.copy(alpha = 0.1f), 1.4f * u)
        // Los dos pestillos.
        listOf(f.left + w * 0.08f, f.right - w * 0.08f).forEach { x ->
            drawRoundRect(t(ACERO_OSC, oscuro), Offset(x - 5 * u, f.top + fh * 0.3f), Size(10 * u, fh * 0.32f), CornerRadius(2 * u))
            remache(Offset(x, f.top + fh * 0.36f), 2 * u)
            drawRect(t(LATON_OSC, oscuro), Offset(x - 1.5f * u, f.top + fh * 0.42f), Size(3 * u, fh * 0.16f))
        }
        // La placa de latón estampada y la manija industrial.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (!c.vacio) {
            drawRect(t(LATON, oscuro), r.topLeft, r.size)
            drawRect(t(LATON_OSC, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
            listOf(Offset(r.left + 4 * u, r.top + 4 * u), Offset(r.right - 4 * u, r.bottom - 4 * u)).forEach { remache(it, 1.4f * u) }
        }
        val my = h * 0.8f + baja
        drawRect(t(ACERO_OSC, oscuro), Offset(w * 0.36f, my - 6 * u), Size(5 * u, 9 * u))
        drawRect(t(ACERO_OSC, oscuro), Offset(w * 0.64f - 5 * u, my - 6 * u), Size(5 * u, 9 * u))
        drawRoundRect(t(ACERO_OSC, oscuro), Offset(w * 0.34f, my), Size(w * 0.32f, 6 * u), CornerRadius(3 * u))
        drawLine(Color.White.copy(alpha = 0.2f), Offset(w * 0.35f, my + 1.5f * u), Offset(w * 0.65f, my + 1.5f * u), 1f * u)
    }

    override val rotulo = Rect(0.24f, 0.18f, 0.76f, 0.6f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.estarcido
    override val salida = SALIDA
    override val placa = Rect(0.2f, 0.5f, 0.8f, 0.98f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un plano azul: cuadrícula fina y gruesa, y unas piezas tenues.
        drawRect(t(PLANO, oscuro))
        val p = 16 * u
        var x = 0f; var k = 0
        while (x < w) { drawLine(TEXTO.copy(alpha = if (k % 5 == 0) 0.14f else 0.06f), Offset(x, 0f), Offset(x, h), 1f * u); x += p; k++ }
        var y = 0f; k = 0
        while (y < h) { drawLine(TEXTO.copy(alpha = if (k % 5 == 0) 0.14f else 0.06f), Offset(0f, y), Offset(w, y), 1f * u); y += p; k++ }
        drawPath(engrane(Offset(w * 0.72f, h * 0.3f), w * 0.18f, 16), TEXTO.copy(alpha = 0.1f), style = Stroke(1.4f * u))
        drawCircle(TEXTO.copy(alpha = 0.1f), w * 0.07f, Offset(w * 0.72f, h * 0.3f), style = Stroke(1.4f * u))
        drawLine(TEXTO.copy(alpha = 0.12f), Offset(w * 0.5f, h * 0.3f), Offset(w * 0.94f, h * 0.3f), 1f * u)
        // Las paredes de acero remachadas.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(ACERO, oscuro), r.topLeft, r.size) }
        var rx = pl
        while (rx < w - pl) { remache(Offset(rx, pl / 2), 1.8f * u); remache(Offset(rx, h - pl / 2), 1.8f * u); rx += 24 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // La cuadrícula fina, el filete carmesí, el cajetín del plano y las marcas de registro en las esquinas.
        val q = 8 * u * g
        var x = 0f
        while (x < w) { drawLine(TEXTO.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, h), 0.6f * u); x += q }
        var y = 0f
        while (y < h) { drawLine(TEXTO.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 0.6f * u); y += q }
        drawRect(CARMESI, Offset.Zero, Size(w, 2.5f * u * g))
        val caj = Rect(w * 0.58f, h - 16 * u * g, w - 4 * u * g, h - 4 * u * g)
        drawRect(TEXTO.copy(alpha = 0.18f), caj.topLeft, caj.size, style = Stroke(0.7f * u))
        drawLine(TEXTO.copy(alpha = 0.18f), Offset(caj.left, caj.center.y), Offset(caj.right, caj.center.y), 0.6f * u)
        drawLine(TEXTO.copy(alpha = 0.18f), Offset(caj.left + caj.width * 0.35f, caj.top), Offset(caj.left + caj.width * 0.35f, caj.bottom), 0.6f * u)
        val m = 5 * u * g; val rm = 2.5f * u * g
        listOf(Offset(m, m + 3 * u), Offset(w - m, m + 3 * u), Offset(m, h - m)).forEach { o ->
            drawCircle(TEXTO.copy(alpha = 0.35f), rm, o, style = Stroke(0.6f * u))
            drawLine(TEXTO.copy(alpha = 0.35f), Offset(o.x - rm * 1.6f, o.y), Offset(o.x + rm * 1.6f, o.y), 0.6f * u)
            drawLine(TEXTO.copy(alpha = 0.35f), Offset(o.x, o.y - rm * 1.6f), Offset(o.x, o.y + rm * 1.6f), 0.6f * u)
        }
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 30.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> { drawPath(engrane(c, s * 0.46f, 8), LATON); drawCircle(ACERO, s * 0.16f, c) }
            "piensa" -> {
                drawLine(TEXTO, Offset(c.x, c.y - s * 0.36f), Offset(c.x - s * 0.26f, c.y + s * 0.42f), s * 0.08f, StrokeCap.Round)
                drawLine(TEXTO, Offset(c.x, c.y - s * 0.36f), Offset(c.x + s * 0.26f, c.y + s * 0.42f), s * 0.08f, StrokeCap.Round)
                drawCircle(TEXTO, s * 0.1f, Offset(c.x, c.y - s * 0.36f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(CARMESI)
            else -> {}
        }
    }

    /** Un remache grande. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension
        remache(center, s * 0.4f)
    }

    override val desorden = 0f
}

/**
 * Laboratorio: el armario de muestras. Un armario blanco clínico con su marco gris y el triángulo amarillo de riesgo; encima,
 * un microscopio y matraces con líquido cian y violeta; abajo, un zócalo con ruedas. Cada tema es una caja de criogenia azul
 * hielo, con los tapones de colores de sus viales asomando (más cuanto más llena), la etiqueta impresa con su código de
 * barras y un poco de vapor de nitrógeno al abrirla. Por dentro, una bandeja blanca con su cuadrícula, gotas y una pipeta.
 * Las fichas son fichas de protocolo con su encabezado cian, casillas de verificación y un código tenue.
 */
object IdeasLaboratorio : DisenoIdeas {
    private val BLANCO = Color(0xFFF2F6F8)
    private val BLANCO_OSC = Color(0xFFD6DEE4)
    private val CIAN = Color(0xFF0B8FA3)
    private val VIOLETA = Color(0xFF7A5AC8)
    private val ROSA = Color(0xFFE0557A)
    private val GRIS = Color(0xFF8A98A6)
    private val GRIS_OSC = Color(0xFF5A6876)
    private val VIDRIO = Color(0xFFCFE8EE)
    private val TINTA = Color(0xFF1B2733)
    private val HIELO = Color(0xFFA8D8F0)
    private val HIELO_OSC = Color(0xFF6EA8C8)
    private val AMARILLO = Color(0xFFF2C84A)
    private val TAPONES = listOf(Color(0xFF0B8FA3), Color(0xFF7A5AC8), Color(0xFFE0557A), Color(0xFFF2C84A), Color(0xFF4AB86A), Color(0xFFFFFFFF))

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.35f) else c

    override val cols = 2
    override val proporcion = 1.4f
    override val corona = 0.32f
    override val base = 0.11f
    override val lado = 0.07f
    override val hueco = 0.06f

    /** Un matraz de Erlenmeyer con su líquido. */
    private fun DrawScope.matraz(b: Offset, alto: Float, liquido: Color) {
        val u = density
        val ancho = alto * 0.7f
        val p = Path().apply { moveTo(b.x - ancho / 2, b.y); lineTo(b.x - ancho * 0.12f, b.y - alto * 0.62f); lineTo(b.x - ancho * 0.12f, b.y - alto); lineTo(b.x + ancho * 0.12f, b.y - alto); lineTo(b.x + ancho * 0.12f, b.y - alto * 0.62f); lineTo(b.x + ancho / 2, b.y); close() }
        clipPath(p) { drawRect(liquido.copy(alpha = 0.75f), Offset(b.x - ancho, b.y - alto * 0.38f), Size(ancho * 2, alto * 0.38f)) }
        drawPath(p, Color.White.copy(alpha = 0.25f))
        drawPath(p, GRIS_OSC, style = Stroke(1.2f * u))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El armario blanco, con su marco gris y los estantes con canto cian.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 8 * u, w * 0.98f, d.base.top + d.base.height * 0.55f)
        drawRect(t(BLANCO, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(t(GRIS, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(3 * u))
        d.filas.forEach { f ->
            drawRect(t(BLANCO_OSC, oscuro), Offset(cuerpo.left + 6 * u, f.top - d.separacion * 0.5f), Size(cuerpo.width - 12 * u, f.height + d.separacion * 0.5f))
            val r = Rect(cuerpo.left + 3 * u, f.bottom, cuerpo.right - 3 * u, f.bottom + maxOf(d.separacion * 0.4f, 6 * u))
            drawRect(t(BLANCO, oscuro), r.topLeft, r.size)
            drawLine(t(CIAN, oscuro), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 1.6f * u)
        }
        // El triángulo de riesgo en el costado.
        val tr = Offset(cuerpo.left + w * 0.035f, cuerpo.top + 26 * u)
        drawPath(Path().apply { moveTo(tr.x, tr.y - 9 * u); lineTo(tr.x + 9 * u, tr.y + 7 * u); lineTo(tr.x - 9 * u, tr.y + 7 * u); close() }, AMARILLO)
        drawCircle(TINTA, 2.6f * u, Offset(tr.x, tr.y + 2 * u), style = Stroke(1.2f * u))
        // Encima: el microscopio, los matraces cian y violeta y un tubo de ensayo.
        val tabla = d.corona.bottom - 8 * u
        drawRect(t(GRIS, oscuro), Offset(0f, tabla - 3 * u), Size(w, 5 * u))
        val mc = Offset(w * 0.24f, tabla - 3 * u)
        drawRect(t(GRIS_OSC, oscuro), Offset(mc.x - ch * 0.16f, mc.y - ch * 0.06f), Size(ch * 0.32f, ch * 0.06f))
        drawPath(Path().apply { moveTo(mc.x + ch * 0.08f, mc.y - ch * 0.06f); quadraticTo(mc.x + ch * 0.2f, mc.y - ch * 0.4f, mc.x - ch * 0.02f, mc.y - ch * 0.56f) }, t(GRIS_OSC, oscuro), style = Stroke(5 * u, cap = StrokeCap.Round))
        rotate(-25f, Offset(mc.x - ch * 0.02f, mc.y - ch * 0.56f)) { drawRoundRect(t(BLANCO_OSC, oscuro), Offset(mc.x - ch * 0.07f, mc.y - ch * 0.72f), Size(ch * 0.1f, ch * 0.32f), CornerRadius(3 * u)) }
        drawRect(t(GRIS_OSC, oscuro), Offset(mc.x - ch * 0.12f, mc.y - ch * 0.26f), Size(ch * 0.22f, 4 * u))
        matraz(Offset(w * 0.56f, tabla - 3 * u), ch * 0.46f, CIAN)
        matraz(Offset(w * 0.72f, tabla - 3 * u), ch * 0.36f, VIOLETA)
        drawRoundRect(t(VIDRIO, oscuro), Offset(w * 0.84f, tabla - 3 * u - ch * 0.4f), Size(8 * u, ch * 0.4f), CornerRadius(4 * u))
        drawRoundRect(ROSA.copy(alpha = 0.7f), Offset(w * 0.84f, tabla - 3 * u - ch * 0.18f), Size(8 * u, ch * 0.18f), CornerRadius(4 * u))
        // El zócalo con ruedas.
        val zoc = Rect(cuerpo.left, cuerpo.bottom, cuerpo.right, d.base.bottom - 8 * u)
        drawRect(t(GRIS, oscuro), zoc.topLeft, zoc.size)
        listOf(0.08f, 0.92f).forEach { f -> drawCircle(t(GRIS_OSC, oscuro), 6 * u, Offset(w * f, d.base.bottom - 7 * u)) }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(BLANCO, oscuro), r.topLeft, r.size)
            drawRect(t(CIAN, oscuro), r.topLeft, Size(r.width, 3 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: una gradilla con tres tubos.
            val b = h * 0.94f
            drawRect(t(GRIS, oscuro), Offset(w * 0.24f, b - h * 0.12f), Size(w * 0.52f, h * 0.04f))
            drawRect(t(GRIS, oscuro), Offset(w * 0.24f, b - 3 * u), Size(w * 0.52f, 3 * u))
            for (k in 0 until 3) {
                val x = w * (0.33f + 0.17f * k)
                drawRoundRect(t(VIDRIO, oscuro), Offset(x - 4 * u, b - h * 0.3f), Size(8 * u, h * 0.3f), CornerRadius(4 * u))
                drawRoundRect(TAPONES[k].copy(alpha = 0.7f), Offset(x - 4 * u, b - h * 0.12f), Size(8 * u, h * 0.12f), CornerRadius(4 * u))
            }
            return
        }
        val caja = Rect(w * 0.1f, h * 0.38f, w * 0.9f, h * 0.95f)
        val sube = h * 0.04f + ab * h * 0.16f
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(caja.left, caja.bottom - h * 0.03f), Size(caja.width, h * 0.06f))
        // Los tapones de los viales, en su cuadrícula, asomando bajo la tapa transparente.
        drawRect(t(HIELO_OSC, oscuro), Offset(caja.left + 3 * u, caja.top - sube), Size(caja.width - 6 * u, sube + 2 * u))
        val k = c.n.coerceIn(1, 10)
        for (i in 0 until 10) {
            val x = caja.left + caja.width * (0.1f + 0.08f * i + 0.04f)
            val y = caja.top - sube * 0.55f
            if (i < k) drawCircle(TAPONES[(i + c.semilla.mod(TAPONES.size)) % TAPONES.size], caja.width * 0.033f, Offset(x, y))
            else drawCircle(Color.Black.copy(alpha = 0.2f), caja.width * 0.02f, Offset(x, y))
        }
        val tapa = Rect(caja.left - 2 * u, caja.top - sube - h * 0.08f, caja.right + 2 * u, caja.top - sube + 2 * u)
        drawRoundRect(t(VIDRIO, oscuro).copy(alpha = 0.55f), tapa.topLeft, tapa.size, CornerRadius(3 * u))
        drawRoundRect(t(GRIS, oscuro), tapa.topLeft, tapa.size, CornerRadius(3 * u), style = Stroke(1 * u))
        if (ab > 0f) for (j in 0 until 5) drawCircle(Color.White.copy(alpha = 0.45f * ab), (6f + 4f * j) * u, Offset(caja.left + caja.width * (0.15f + 0.18f * j), tapa.top - 6 * u - j * 2 * u))
        // La caja azul hielo, con sus aristas.
        drawRoundRect(t(HIELO, oscuro), caja.topLeft, caja.size, CornerRadius(3 * u))
        drawRoundRect(t(HIELO_OSC, oscuro), caja.topLeft, caja.size, CornerRadius(3 * u), style = Stroke(1.2f * u))
        // La etiqueta impresa con su código de barras.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(BLANCO, oscuro), r.topLeft, r.size)
        drawRect(t(CIAN, oscuro), r.topLeft, Size(r.width, 3 * u))
        // El código de barras va en la caja, debajo de la etiqueta, para no pisar el texto.
        val by = (r.bottom + caja.bottom) / 2
        var bx = caja.right - 6 * u; var q = 0
        while (bx > caja.right - 26 * u) { val bw = (0.6f + 1.2f * azar(c.semilla, q)) * u; drawRect(TINTA.copy(alpha = 0.55f), Offset(bx - bw, by - 2.5f * u), Size(bw, 5 * u)); bx -= bw + 1 * u; q++ }
    }

    override val rotulo = Rect(0.16f, 0.5f, 0.84f, 0.84f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.jost
    override val salida = 0f
    override val placa = Rect(0.2f, 0.5f, 0.8f, 0.98f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La bandeja blanca con su cuadrícula, unas gotas y una pipeta.
        val b = if (oscuro) mezcla(BLANCO, Color.Black, 0.6f) else BLANCO
        drawRect(b)
        val p = 20 * u
        var x = 0f
        while (x < w) { drawLine(GRIS.copy(alpha = 0.18f), Offset(x, 0f), Offset(x, h), 0.8f * u); x += p }
        var y = 0f
        while (y < h) { drawLine(GRIS.copy(alpha = 0.18f), Offset(0f, y), Offset(w, y), 0.8f * u); y += p }
        for (k in 0 until 8) drawCircle(CIAN.copy(alpha = 0.18f), (3f + 4f * azar(81, k)) * u, Offset(w * azar(82, k), h * azar(83, k)))
        rotate(-30f, Offset(w * 0.7f, h * 0.85f)) {
            drawRoundRect(GRIS.copy(alpha = 0.6f), Offset(w * 0.5f, h * 0.85f - 3 * u), Size(w * 0.36f, 6 * u), CornerRadius(3 * u))
            drawRoundRect(VIOLETA.copy(alpha = 0.6f), Offset(w * 0.86f, h * 0.85f - 6 * u), Size(18 * u, 12 * u), CornerRadius(6 * u))
        }
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(BLANCO_OSC, oscuro), r.topLeft, r.size) }
        drawRect(t(CIAN, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) BLANCO else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(3.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El encabezado cian, las casillas de verificación en el margen y un código tenue.
        drawRect(CIAN, Offset.Zero, Size(w, 3 * u * g))
        val cs = 4.5f * u * g
        var y = 16 * u * g; var k = 0
        while (y < h - 10 * u * g) {
            drawRect(GRIS.copy(alpha = 0.6f), Offset(3 * u * g, y), Size(cs, cs), style = Stroke(0.7f * u))
            if (azar(semilla, k) < 0.5f) drawPath(Path().apply { moveTo(3 * u * g + cs * 0.2f, y + cs * 0.5f); lineTo(3 * u * g + cs * 0.45f, y + cs * 0.8f); lineTo(3 * u * g + cs * 0.9f, y + cs * 0.15f) }, CIAN, style = Stroke(0.9f * u * g))
            y += 14 * u * g; k++
        }
        val q = 1.6f * u * g
        val qx = w - 12 * u * g; val qy = h * 0.45f
        for (i in 0 until 6) for (j in 0 until 6) if (azar(semilla + 7, i * 6 + j) < 0.5f || (i < 2 && j < 2)) drawRect(TINTA.copy(alpha = 0.18f), Offset(qx + i * q, qy + j * q), Size(q, q))
    }

    override val margenes = MargenesFicha(13.dp, 16.dp, 12.dp, 26.dp, 30.dp, 26.dp)
    override val proporcionFicha = 0.88f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> matraz(Offset(c.x, c.y + s * 0.44f), s * 0.88f, CIAN)
            // Una molécula: tres átomos enlazados.
            "piensa" -> {
                val a = Offset(c.x - s * 0.26f, c.y + s * 0.18f); val b = Offset(c.x + s * 0.26f, c.y + s * 0.18f); val d = Offset(c.x, c.y - s * 0.28f)
                listOf(a to b, b to d, d to a).forEach { (p1, p2) -> drawLine(VIOLETA, p1, p2, s * 0.06f) }
                listOf(a, b, d).forEach { drawCircle(VIOLETA, s * 0.13f, it) }
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(VIOLETA)
            else -> {}
        }
    }

    /** Un vial con su tapón rosa. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRoundRect(VIDRIO, Offset(c.x - s * 0.16f, c.y - s * 0.2f), Size(s * 0.32f, s * 0.66f), CornerRadius(s * 0.12f))
        drawRoundRect(GRIS_OSC, Offset(c.x - s * 0.16f, c.y - s * 0.2f), Size(s * 0.32f, s * 0.66f), CornerRadius(s * 0.12f), style = Stroke(s * 0.04f))
        drawRoundRect(ROSA, Offset(c.x - s * 0.2f, c.y - s * 0.48f), Size(s * 0.4f, s * 0.3f), CornerRadius(s * 0.06f))
    }

    override val desorden = 0f
}

/**
 * Colonos: la cajonera del almacén general. Tablas de pino rescatadas de distintos tonos (alguna pintada de azul o de
 * ladrillo) con parches de lata clavados; arriba, el letrero pintado del almacén, un quinqué colgado y una balanza de
 * platillos; abajo, el mostrador con una gaceta clavada. Cada tema es un cajetín con su tirador de cordel y una etiqueta de
 * papel de gaceta pegada con cinta; los más llenos quedan entreabiertos con sus recortes. Por dentro, el cajón forrado de
 * gaceta, con tachuelas y un cerco de café. Las fichas son recortes cortados a tijera con su cinta.
 */
object IdeasColonos : DisenoIdeas {
    private val PINO = Color(0xFFB08A5A)
    private val PINO_LUZ = Color(0xFFCBA676)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val LADRILLO = Color(0xFF9E2B25)
    private val MARINO = Color(0xFF3E4B60)
    private val GACETA = Color(0xFFEDE4CC)
    private val LATA = Color(0xFFA0A4A0)
    private val TINTA = Color(0xFF1E1A16)
    private val CINTA = Color(0xFFE8D48A)
    private val LUZ = Color(0xFFF2C46B)
    private val LATON = Color(0xFFB89A50)
    private val CUERDA = Color(0xFFC9A86A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 1.2f
    override val corona = 0.32f
    override val base = 0.15f
    override val lado = 0.06f
    override val hueco = 0.025f

    private const val SALIDA = 0.16f

    /** Un parche de lata clavado, con sus clavos. */
    private fun DrawScope.parche(r: Rect, oscuro: Boolean) {
        val u = density
        drawRect(t(LATA, oscuro), r.topLeft, r.size)
        drawLine(Color.White.copy(alpha = 0.3f), r.topLeft, Offset(r.right, r.top), 1f * u)
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), Offset(r.right, r.bottom)).forEach { o -> drawCircle(Color.Black.copy(alpha = 0.6f), 1 * u, Offset(o.x + (if (o.x < r.center.x) 2.5f else -2.5f) * u, o.y + (if (o.y < r.center.y) 2.5f else -2.5f) * u)) }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo: tablas de pino rescatadas, cada una de su tono (alguna pintada), con parches de lata.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 6 * u, w * 0.985f, d.base.top + d.base.height * 0.3f)
        val nt = 9
        for (k in 0 until nt) {
            val r = Rect(cuerpo.left + cuerpo.width * k / nt, cuerpo.top, cuerpo.left + cuerpo.width * (k + 1) / nt, cuerpo.bottom)
            val tono = when (k) { 2 -> mezcla(MARINO, PINO, 0.35f); 6 -> mezcla(LADRILLO, PINO, 0.4f); else -> mezcla(PINO_OSC, PINO_LUZ, 0.3f + 0.6f * azar(11, k)) }
            drawRect(t(tono, oscuro), r.topLeft, r.size)
            vetas(r, PINO_OSC.copy(alpha = 0.35f), 20 + k, 0.6f, vertical = true, grosor = 0.5f, ondulacion = 0.5f)
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.top), Offset(r.left, r.bottom), 1f * u)
        }
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.5f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        parche(Rect(cuerpo.left + 4 * u, cuerpo.top + cuerpo.height * 0.4f, cuerpo.left + w * 0.05f, cuerpo.top + cuerpo.height * 0.4f + 22 * u), oscuro)
        parche(Rect(cuerpo.right - w * 0.05f, cuerpo.top + cuerpo.height * 0.15f, cuerpo.right - 4 * u, cuerpo.top + cuerpo.height * 0.15f + 18 * u), oscuro)
        // El letrero pintado del almacén, con su filete y sus letras de lejos.
        val letrero = Rect(w * 0.22f, d.corona.top + ch * 0.36f, w * 0.78f, d.corona.bottom - 10 * u)
        drawRect(t(LADRILLO, oscuro), letrero.topLeft, letrero.size)
        drawRect(t(MARINO, oscuro), letrero.topLeft, letrero.size, style = Stroke(4 * u))
        drawRect(t(GACETA, oscuro), Offset(letrero.left + 7 * u, letrero.top + 7 * u), Size(letrero.width - 14 * u, letrero.height - 14 * u), style = Stroke(0.8f * u))
        var lx = letrero.left + letrero.width * 0.14f; var q = 0
        while (lx < letrero.right - letrero.width * 0.14f) {
            if (q % 6 != 5) drawRect(t(GACETA, oscuro), Offset(lx, letrero.center.y - letrero.height * 0.16f), Size(letrero.width * 0.04f, letrero.height * 0.32f))
            lx += letrero.width * 0.055f; q++
        }
        listOf(letrero.left + 10 * u, letrero.right - 10 * u).forEach { x -> drawLine(t(PINO_OSC, oscuro), Offset(x, letrero.bottom), Offset(x, d.corona.bottom - 6 * u), 3 * u) }
        // El quinqué colgado, con su luz.
        val q0 = Offset(w * 0.1f, d.corona.top + ch * 0.12f)
        drawLine(t(TINTA, oscuro), Offset(q0.x, d.corona.top), q0, 1 * u)
        mancha(Offset(q0.x, q0.y + ch * 0.22f), ch * (if (oscuro) 0.7f else 0.5f), LUZ.copy(alpha = if (oscuro) 0.35f else 0.25f))
        drawOval(Color.White.copy(alpha = 0.45f), Offset(q0.x - ch * 0.07f, q0.y + ch * 0.06f), Size(ch * 0.14f, ch * 0.22f))
        drawOval(LUZ, Offset(q0.x - ch * 0.02f, q0.y + ch * 0.13f), Size(ch * 0.04f, ch * 0.08f))
        drawRect(t(LATON, oscuro), Offset(q0.x - ch * 0.08f, q0.y + ch * 0.27f), Size(ch * 0.16f, ch * 0.08f))
        // La balanza de platillos sobre el mueble.
        val bc = Offset(w * 0.9f, d.corona.bottom - 6 * u)
        drawLine(t(LATON, oscuro), bc, Offset(bc.x, bc.y - ch * 0.38f), 2.4f * u)
        drawLine(t(LATON, oscuro), Offset(bc.x - ch * 0.22f, bc.y - ch * 0.34f), Offset(bc.x + ch * 0.22f, bc.y - ch * 0.38f), 2 * u)
        listOf(-1f, 1f).forEach { s ->
            val px = bc.x + s * ch * 0.22f; val py = bc.y - ch * (if (s < 0) 0.34f else 0.38f)
            drawLine(t(LATON, oscuro), Offset(px, py), Offset(px - ch * 0.06f, py + ch * 0.16f), 0.8f * u)
            drawLine(t(LATON, oscuro), Offset(px, py), Offset(px + ch * 0.06f, py + ch * 0.16f), 0.8f * u)
            drawArc(t(LATON, oscuro), 0f, 180f, true, Offset(px - ch * 0.08f, py + ch * 0.12f), Size(ch * 0.16f, ch * 0.08f))
        }
        // El mostrador, con una gaceta clavada al frente.
        val most = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(PINO_LUZ, oscuro), Offset(0f, most.top), Size(w, 7 * u))
        drawRect(t(PINO, oscuro), Offset(0f, most.top + 7 * u), Size(w, most.height - 7 * u))
        for (k in 1 until 8) drawLine(Color.Black.copy(alpha = 0.3f), Offset(w * k / 8f, most.top + 7 * u), Offset(w * k / 8f, most.bottom), 1f * u)
        val gz = Rect(w * 0.06f, most.top + 10 * u, w * 0.2f, most.bottom - 4 * u)
        drawRect(t(GACETA, oscuro), gz.topLeft, gz.size)
        for (j in 0 until 5) drawLine(t(TINTA, oscuro).copy(alpha = 0.3f), Offset(gz.left + 3 * u, gz.top + 6 * u + j * 4 * u), Offset(gz.right - 3 * u, gz.top + 6 * u + j * 4 * u), 0.8f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(GACETA, oscuro), r.topLeft, r.size)
            drawRect(t(CINTA, oscuro).copy(alpha = 0.8f), Offset(r.left - 3 * u, r.top - 2 * u), Size(12 * u, 5 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajetines más llenos quedan entreabiertos y asoman los recortes.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF120C06), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 6)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.08f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                rotate(-6f + 12f * azar(c.semilla, i + 9), Offset(x, arriba)) { drawRect(t(GACETA, oscuro), Offset(x, y0), Size(w * 0.16f, arriba + 4 * u - y0)) }
            }
        }
        // El frente de pino, de su propio tono, con su veta.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(mezcla(PINO_OSC, PINO_LUZ, 0.3f + 0.6f * azar(c.semilla, 77)), oscuro), f.topLeft, f.size)
        vetas(f, PINO_OSC.copy(alpha = 0.4f), c.semilla, 1f, grosor = 0.6f, ondulacion = 0.6f)
        bisel(f, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.35f), 1.2f * u)
        // La etiqueta de gaceta pegada con cinta.
        if (!c.vacio) {
            val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
            drawRect(t(GACETA, oscuro), r.topLeft, r.size)
            rotate(-20f, Offset(r.left, r.top)) { drawRect(t(CINTA, oscuro).copy(alpha = 0.75f), Offset(r.left - 6 * u, r.top - 3 * u), Size(16 * u, 6 * u)) }
            rotate(20f, Offset(r.right, r.top)) { drawRect(t(CINTA, oscuro).copy(alpha = 0.75f), Offset(r.right - 10 * u, r.top - 3 * u), Size(16 * u, 6 * u)) }
        }
        // El tirador de cordel.
        val tc = Offset(w / 2, h * 0.82f + baja)
        drawCircle(t(PINO_OSC, oscuro), 2 * u, Offset(tc.x - w * 0.08f, tc.y - 2 * u))
        drawCircle(t(PINO_OSC, oscuro), 2 * u, Offset(tc.x + w * 0.08f, tc.y - 2 * u))
        drawArc(t(CUERDA, oscuro), 0f, 180f, false, Offset(tc.x - w * 0.08f, tc.y - 6 * u), Size(w * 0.16f, 12 * u), style = Stroke(2 * u))
    }

    override val rotulo = Rect(0.14f, 0.18f, 0.86f, 0.64f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.fell
    override val salida = SALIDA
    override val placa = Rect(0.32f, 0.36f, 0.68f, 0.86f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo forrado de gaceta: columnas de renglones, tachuelas en las esquinas y un cerco de café.
        val g = if (oscuro) mezcla(GACETA, Color.Black, 0.58f) else GACETA
        drawRect(g)
        val col = 3
        val cw = w / col
        for (j in 0 until col) {
            drawRect(TINTA.copy(alpha = 0.35f), Offset(cw * j + 14 * u, 24 * u), Size(cw - 28 * u, 7 * u))
            var y = 40 * u
            while (y < h - 16 * u) { drawLine(TINTA.copy(alpha = 0.12f), Offset(cw * j + 14 * u, y), Offset(cw * (j + 1) - 14 * u, y), 1.2f * u); y += 6 * u }
            if (j > 0) drawLine(TINTA.copy(alpha = 0.2f), Offset(cw * j, 20 * u), Offset(cw * j, h - 14 * u), 0.8f * u)
        }
        drawCircle(Color(0xFF6A4020).copy(alpha = 0.25f), 32 * u, Offset(w * 0.7f, h * 0.65f), style = Stroke(4 * u))
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(PINO, oscuro), r.topLeft, r.size) }
        listOf(Offset(pl + 8 * u, pl + 8 * u), Offset(w - pl - 8 * u, pl + 8 * u), Offset(pl + 8 * u, h - pl - 8 * u), Offset(w - pl - 8 * u, h - pl - 8 * u)).forEach { drawCircle(t(LATON, oscuro), 3 * u, it) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) GACETA else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 12, 0.018f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(GACETA.copy(alpha = 0.45f))
        // Los filetes de columna arriba y abajo, y una tira de cinta amarillenta.
        drawLine(TINTA.copy(alpha = 0.4f), Offset(8 * u, 6 * u * g), Offset(w - 8 * u, 6 * u * g), 0.8f * u)
        drawLine(TINTA.copy(alpha = 0.3f), Offset(8 * u, h - 6 * u * g), Offset(w - 8 * u, h - 6 * u * g), 0.6f * u)
        val c = Offset(w * (0.4f + 0.2f * azar(semilla, 1)), 2 * u * g)
        rotate(-4f + 8f * azar(semilla, 2), c) { drawRect(CINTA.copy(alpha = 0.6f), Offset(c.x - 14 * u * g, c.y - 2 * u * g), Size(28 * u * g, 7 * u * g)) }
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 13.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un martillo.
            "convence" -> {
                drawLine(PINO_OSC, Offset(c.x - s * 0.3f, c.y + s * 0.4f), Offset(c.x + s * 0.12f, c.y - s * 0.12f), s * 0.12f, StrokeCap.Round)
                rotate(45f, Offset(c.x + s * 0.16f, c.y - s * 0.16f)) { drawRect(MARINO, Offset(c.x - s * 0.04f, c.y - s * 0.26f), Size(s * 0.4f, s * 0.2f)) }
            }
            // Un quinqué.
            "piensa" -> {
                drawOval(LUZ, Offset(c.x - s * 0.18f, c.y - s * 0.4f), Size(s * 0.36f, s * 0.5f))
                drawRect(LATON, Offset(c.x - s * 0.24f, c.y + s * 0.1f), Size(s * 0.48f, s * 0.24f))
                drawCircle(Color(0xFFE07A2A), s * 0.07f, Offset(c.x, c.y - s * 0.1f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LADRILLO)
            else -> {}
        }
    }

    /** Una tachuela. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(Color.Black.copy(alpha = 0.25f), s * 0.36f, Offset(c.x + s * 0.06f, c.y + s * 0.06f))
        drawCircle(LATON, s * 0.36f, c)
        drawCircle(Color.White.copy(alpha = 0.45f), s * 0.12f, Offset(c.x - s * 0.1f, c.y - s * 0.1f))
    }

    override val desorden = 2.5f
}

/**
 * Ciudad verde: la estantería metálica rescatada. Una pared de hormigón agrietada con manchas; arriba, una viga rota con su
 * varilla oxidada al aire, hiedra que cuelga y musgo; la estantería es de ángulo ranurado con óxido y, al pie, helechos y
 * raíces que entran. Cada tema es una lata de conserva reutilizada, con el abrefácil levantado, óxido, notas enrolladas
 * asomando y el tema en una tira de cinta de enmascarar; en algunas trepa una enredadera. Por dentro, el fondo de la lata con
 * musgo y hojas caídas. Las fichas son notas en papel rescatado, con manchas de agua y una hoja prensada.
 */
object IdeasRuinas : DisenoIdeas {
    private val HORMIGON = Color(0xFFA8A69C)
    private val HORMIGON_OSC = Color(0xFF6E6C64)
    private val HORMIGON_LUZ = Color(0xFFC6C4BA)
    private val OXIDO = Color(0xFFA34A1E)
    private val OXIDO_OSC = Color(0xFF6A2E12)
    private val HIEDRA = Color(0xFF3E6B2A)
    private val HIEDRA_LUZ = Color(0xFF6A9A3E)
    private val MUSGO = Color(0xFF5A7A3A)
    private val HOJALATA = Color(0xFFB8BCB8)
    private val HOJALATA_OSC = Color(0xFF7E8480)
    private val CINTA = Color(0xFFE8DCB0)
    private val TINTA = Color(0xFF1F2420)
    private val PAPEL = Color(0xFFE6E3D6)
    private val ESTANTE = Color(0xFF6E7470)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.32f
    override val base = 0.15f
    override val lado = 0.08f
    override val hueco = 0.06f

    /** Una hoja de hiedra: tres lóbulos. */
    private fun DrawScope.hojaHiedra(c: Offset, s: Float, color: Color) {
        drawCircle(color, s * 0.42f, Offset(c.x, c.y - s * 0.2f))
        drawCircle(color, s * 0.34f, Offset(c.x - s * 0.32f, c.y + s * 0.08f))
        drawCircle(color, s * 0.34f, Offset(c.x + s * 0.32f, c.y + s * 0.08f))
    }

    /** Un tallo de enredadera que baja (o sube) desde [a] con sus hojas. */
    private fun DrawScope.enredadera(a: Offset, largo: Float, dir: Float, semilla: Int, oscuro: Boolean) {
        val u = density
        val p = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + largo * 0.2f, a.y + dir * largo * 0.3f, a.x - largo * 0.2f, a.y + dir * largo * 0.6f, a.x + largo * 0.05f, a.y + dir * largo) }
        drawPath(p, t(HIEDRA, oscuro), style = Stroke(1.4f * u))
        for (k in 0 until 5) {
            val f = (k + 1) / 6f
            val x = a.x + largo * (0.08f * sin(f * 6f + semilla)) ; val y = a.y + dir * largo * f
            hojaHiedra(Offset(x + (if (k % 2 == 0) 5 else -5) * u, y), (6f + 3f * azar(semilla, k)) * u, t(if (k % 2 == 0) HIEDRA else HIEDRA_LUZ, oscuro))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.35f
        // La pared de hormigón, con manchas y grietas.
        drawRect(t(HORMIGON, oscuro), Offset(0f, d.corona.top), Size(w, piso - d.corona.top))
        for (k in 0 until 14) mancha(Offset(w * azar(31, k), d.corona.top + (piso - d.corona.top) * azar(32, k)), (20f + 40f * azar(33, k)) * u, t(HORMIGON_OSC, oscuro).copy(alpha = 0.18f))
        for (k in 0 until 3) {
            val p = Path(); var x = w * azar(34, k); var y = d.corona.bottom + 10 * u
            p.moveTo(x, y)
            for (j in 0 until 8) { x += (azar(35 + k, j) - 0.5f) * 30 * u; y += 18 * u; p.lineTo(x, y) }
            drawPath(p, Color.Black.copy(alpha = 0.3f), style = Stroke(1.2f * u))
        }
        // La estantería de ángulo ranurado: montantes con sus ranuras, y los estantes con su óxido.
        listOf(w * 0.03f, w * 0.5f - w * 0.015f, w * 0.97f - w * 0.03f).forEach { x ->
            drawRect(t(ESTANTE, oscuro), Offset(x, d.corona.bottom - 6 * u), Size(w * 0.03f, piso - d.corona.bottom + 6 * u))
            var y = d.corona.bottom
            while (y < piso - 6 * u) { drawRoundRect(Color.Black.copy(alpha = 0.6f), Offset(x + w * 0.01f, y), Size(w * 0.01f, 5 * u), CornerRadius(2 * u)); y += 10 * u }
            mancha(Offset(x + w * 0.015f, piso - 20 * u), 14 * u, t(OXIDO, oscuro).copy(alpha = 0.5f))
        }
        d.filas.forEach { f ->
            val r = Rect(w * 0.02f, f.bottom - 1 * u, w * 0.98f, f.bottom + maxOf(d.separacion * 0.35f, 6 * u))
            drawRect(t(ESTANTE, oscuro), r.topLeft, r.size)
            drawLine(Color.White.copy(alpha = 0.2f), r.topLeft, Offset(r.right, r.top), 1f * u)
            for (k in 0 until 3) mancha(Offset(r.left + r.width * azar(f.top.toInt(), k), r.center.y), 8 * u, t(OXIDO, oscuro).copy(alpha = 0.55f))
        }
        // La viga rota con su varilla al aire, el musgo y la hiedra que cuelga por los costados.
        val viga = Rect(0f, d.corona.top + ch * 0.22f, w, d.corona.top + ch * 0.52f)
        val rota = Path().apply { moveTo(0f, viga.top); lineTo(w * 0.62f, viga.top); lineTo(w * 0.66f, viga.top + viga.height * 0.4f); lineTo(w * 0.7f, viga.top + viga.height * 0.1f); lineTo(w, viga.top); lineTo(w, viga.bottom); lineTo(0f, viga.bottom); close() }
        drawPath(rota, t(HORMIGON_LUZ, oscuro))
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, viga.bottom), Offset(w, viga.bottom), 2 * u)
        drawLine(t(OXIDO_OSC, oscuro), Offset(w * 0.64f, viga.top + viga.height * 0.3f), Offset(w * 0.72f, d.corona.top + ch * 0.04f), 2 * u)
        drawLine(t(OXIDO_OSC, oscuro), Offset(w * 0.67f, viga.top + viga.height * 0.3f), Offset(w * 0.68f, d.corona.top + ch * 0.06f), 2 * u)
        for (k in 0 until 8) mancha(Offset(w * azar(41, k), viga.top + viga.height * azar(42, k)), (8f + 14f * azar(43, k)) * u, t(MUSGO, oscuro).copy(alpha = 0.5f))
        enredadera(Offset(w * 0.06f, viga.bottom), (piso - viga.bottom) * 0.7f, 1f, 3, oscuro)
        enredadera(Offset(w * 0.94f, viga.bottom), (piso - viga.bottom) * 0.45f, 1f, 7, oscuro)
        enredadera(Offset(w * 0.3f, viga.bottom), ch * 0.4f, 1f, 11, oscuro)
        // El piso, con helechos y raíces que entran.
        drawRect(t(HORMIGON_OSC, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        for (k in 0 until 4) {
            val b = Offset(w * (0.1f + 0.27f * k), d.base.bottom)
            for (j in -2..2) {
                val a = Math.toRadians(-90.0 + j * 22.0)
                drawLine(t(HIEDRA_LUZ, oscuro), b, Offset(b.x + d.base.height * 0.6f * cos(a).toFloat(), b.y + d.base.height * 0.6f * sin(a).toFloat()), 2 * u, StrokeCap.Round)
            }
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(CINTA, oscuro), r.topLeft, r.size)
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un brote de helecho que salió entre los estantes.
            val b = Offset(w / 2, h * 0.96f)
            for (j in -2..2) {
                val a = Math.toRadians(-90.0 + j * 20.0)
                drawLine(t(HIEDRA, oscuro), b, Offset(b.x + h * 0.3f * cos(a).toFloat(), b.y + h * 0.3f * sin(a).toFloat()), 2 * u, StrokeCap.Round)
            }
            return
        }
        val lata = Rect(w * 0.16f, h * 0.34f, w * 0.84f, h * 0.94f)
        val elip = h * 0.05f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(lata.left - 2 * u, lata.bottom - elip * 0.2f), Size(lata.width + 4 * u, elip * 1.4f))
        // Las notas enrolladas que asoman (más al abrirla).
        val sube = h * 0.02f * c.n.coerceIn(0, 6) + ab * h * 0.14f
        for (i in 0 until c.n.coerceIn(1, 6)) {
            val x = lata.left + lata.width * (0.15f + 0.13f * i)
            val alto = sube * (0.5f + 0.5f * azar(c.semilla, i)) + h * 0.06f
            drawRoundRect(t(PAPEL, oscuro), Offset(x, lata.top - alto), Size(lata.width * 0.1f, alto + elip), CornerRadius(lata.width * 0.05f))
            drawOval(t(mezcla(PAPEL, TINTA, 0.2f), oscuro), Offset(x, lata.top - alto), Size(lata.width * 0.1f, lata.width * 0.05f))
        }
        // La lata: el cilindro de hojalata con sus nervios y su óxido, y el abrefácil levantado.
        val cil = Path().apply { moveTo(lata.left, lata.top); lineTo(lata.right, lata.top); lineTo(lata.right, lata.bottom); arcTo(Rect(lata.left, lata.bottom - elip, lata.right, lata.bottom + elip), 0f, 180f, false); close() }
        drawPath(cil, t(HOJALATA, oscuro))
        clipPath(cil) {
            var y = lata.top + 6 * u
            while (y < lata.bottom) { drawLine(t(HOJALATA_OSC, oscuro), Offset(lata.left, y), Offset(lata.right, y), 1f * u); y += 7 * u }
            for (k in 0 until 4) mancha(Offset(lata.left + lata.width * azar(c.semilla, k + 30), lata.top + lata.height * azar(c.semilla, k + 40)), lata.width * (0.12f + 0.12f * azar(c.semilla, k + 50)), t(OXIDO, oscuro).copy(alpha = 0.55f))
            drawRect(Color.White.copy(alpha = 0.18f), Offset(lata.left + lata.width * 0.14f, lata.top), Size(lata.width * 0.06f, lata.height + elip))
        }
        drawOval(t(HOJALATA_OSC, oscuro), Offset(lata.left, lata.top - elip), Size(lata.width, elip * 2), style = Stroke(2 * u))
        val tapa = Offset(lata.right - lata.width * 0.1f, lata.top - elip * 0.5f)
        rotate(-60f, tapa) {
            drawOval(t(HOJALATA, oscuro), Offset(tapa.x - lata.width * 0.42f, tapa.y - elip), Size(lata.width * 0.42f, elip * 2))
            drawOval(t(HOJALATA_OSC, oscuro), Offset(tapa.x - lata.width * 0.42f, tapa.y - elip), Size(lata.width * 0.42f, elip * 2), style = Stroke(1 * u))
            drawOval(t(HOJALATA_OSC, oscuro), Offset(tapa.x - lata.width * 0.3f, tapa.y - elip * 0.5f), Size(lata.width * 0.12f, elip), style = Stroke(1.4f * u))
        }
        // La tira de cinta de enmascarar con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1 * u, r.top + 1.5f * u), r.size)
        drawRect(t(CINTA, oscuro), r.topLeft, r.size)
        drawLine(Color.Black.copy(alpha = 0.12f), Offset(r.left, r.top), Offset(r.left + 2 * u, r.bottom), 1f * u)
        // En algunas trepa una enredadera.
        if (c.semilla.mod(3) == 0) enredadera(Offset(lata.left + 3 * u, lata.bottom), lata.height * 0.9f, -1f, c.semilla, oscuro)
    }

    override val rotulo = Rect(0.18f, 0.54f, 0.82f, 0.82f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.overpass
    override val salida = 0f
    override val placa = Rect(0.3f, 0.42f, 0.7f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la lata: hojalata con sus anillos, musgo en el borde y unas hojas caídas.
        val fondo = if (oscuro) mezcla(HOJALATA, Color.Black, 0.6f) else HOJALATA
        drawRect(fondo)
        val c = Offset(w / 2, h / 2)
        var r = 20 * u
        while (r < maxOf(w, h)) { drawCircle(Color.Black.copy(alpha = 0.06f), r, c, style = Stroke(2 * u)); r += 26 * u }
        for (k in 0 until 18) {
            val a = azar(51, k) * 6.28f
            mancha(Offset(c.x + w * 0.5f * cos(a), c.y + h * 0.5f * sin(a)), (30f + 30f * azar(52, k)) * u, MUSGO.copy(alpha = 0.3f))
        }
        for (k in 0 until 4) {
            val p = Offset(w * azar(53, k), h * azar(54, k))
            rotate(azar(55, k) * 360f, p) {
                drawOval(HIEDRA.copy(alpha = 0.3f), Offset(p.x - 14 * u, p.y - 6 * u), Size(28 * u, 12 * u))
                drawLine(HIEDRA.copy(alpha = 0.4f), Offset(p.x - 14 * u, p.y), Offset(p.x + 14 * u, p.y), 0.8f * u)
            }
        }
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { rr -> drawRect(t(HOJALATA_OSC, oscuro), rr.topLeft, rr.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 26, 0.01f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.4f))
        // Manchas de agua y una hoja prensada en la esquina de abajo.
        for (k in 0 until 3) {
            val c = Offset(w * azar(semilla, k), h * azar(semilla, k + 10))
            drawCircle(Color(0xFF8A7A50).copy(alpha = 0.1f), minOf(w, h) * (0.1f + 0.1f * azar(semilla, k + 20)), c)
            drawCircle(Color(0xFF8A7A50).copy(alpha = 0.14f), minOf(w, h) * (0.1f + 0.1f * azar(semilla, k + 20)), c, style = Stroke(1 * u))
        }
        val hc = Offset(w - 14 * u * g, h - 14 * u * g)
        rotate(-35f, hc) {
            drawOval(HIEDRA.copy(alpha = 0.35f), Offset(hc.x - 9 * u * g, hc.y - 4 * u * g), Size(18 * u * g, 8 * u * g))
            drawLine(HIEDRA.copy(alpha = 0.5f), Offset(hc.x - 12 * u * g, hc.y), Offset(hc.x + 9 * u * g, hc.y), 0.8f * u)
        }
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 14.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un brote con sus dos hojas.
            "convence" -> {
                drawLine(HIEDRA, Offset(c.x, c.y + s * 0.44f), Offset(c.x, c.y - s * 0.06f), s * 0.07f, StrokeCap.Round)
                rotate(-35f, Offset(c.x, c.y - s * 0.06f)) { drawOval(HIEDRA_LUZ, Offset(c.x - s * 0.42f, c.y - s * 0.2f), Size(s * 0.42f, s * 0.24f)) }
                rotate(35f, Offset(c.x, c.y - s * 0.06f)) { drawOval(HIEDRA, Offset(c.x, c.y - s * 0.2f), Size(s * 0.42f, s * 0.24f)) }
            }
            "piensa" -> hojaHiedra(c, s * 0.62f, HIEDRA)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(OXIDO)
            else -> {}
        }
    }

    /** Un clavo oxidado. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawLine(OXIDO_OSC, Offset(c.x - s * 0.2f, c.y - s * 0.2f), Offset(c.x + s * 0.32f, c.y + s * 0.4f), s * 0.1f, StrokeCap.Round)
        drawCircle(OXIDO, s * 0.2f, Offset(c.x - s * 0.24f, c.y - s * 0.24f))
    }

    override val desorden = 2f
}

/**
 * Espacio: la sala de control. Arriba, una ventana panorámica a la nebulosa con un planeta anillado, entre dos pantallas de
 * telemetría; los racks oscuros llevan sus rieles atornillados y rejillas; abajo, el piso con luces de guía. Cada tema es un
 * módulo de rack con sus asas, una fila de LED de estado (más encendidos cuanto más lleno), una onda en su pantallita y el
 * tema en un visor; al abrirlo, el módulo sale. Por dentro, el panel con su retícula. Las fichas son registros de misión en
 * vidrio oscuro con corchetes en las esquinas y una barra de progreso.
 */
object IdeasEspacio : DisenoIdeas {
    private val VACIO = Color(0xFF050A18)
    private val PANEL = Color(0xFF141C30)
    private val PANEL_LUZ = Color(0xFF2A3654)
    private val PANEL_OSC = Color(0xFF0C1222)
    private val AZUL = Color(0xFF8AB4FF)
    private val NARANJA = Color(0xFFFFB86B)
    private val VERDE = Color(0xFF5CFFB0)
    private val TEXTO = Color(0xFFE8ECF8)
    private val NEBULOSA = Color(0xFF7A3AA8)
    private val ROSA = Color(0xFFC85AA0)
    private val METAL = Color(0xFF6E7BA0)
    private val PLANETA = Color(0xFFD8A06A)

    override val cols = 2
    override val proporcion = 1.8f
    override val corona = 0.38f
    override val base = 0.09f
    override val lado = 0.075f
    override val hueco = 0.04f

    private const val SALIDA = 0.1f

    private fun DrawScope.corchetes(r: Rect, l: Float, color: Color, grosor: Float) {
        listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), Offset(r.right, r.bottom) to Offset(-1f, -1f)).forEach { (o, s) ->
            drawLine(color, o, Offset(o.x + s.x * l, o.y), grosor)
            drawLine(color, o, Offset(o.x, o.y + s.y * l), grosor)
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        drawRect(PANEL_OSC, Offset(0f, d.corona.top), Size(w, d.base.bottom - d.corona.top))
        // La ventana panorámica: estrellas, la nebulosa y un planeta con su anillo.
        val ven = Rect(w * 0.2f, d.corona.top + ch * 0.06f, w * 0.8f, d.corona.bottom - ch * 0.14f)
        drawRoundRect(PANEL_LUZ, Offset(ven.left - 5 * u, ven.top - 5 * u), Size(ven.width + 10 * u, ven.height + 10 * u), CornerRadius(14 * u))
        drawRoundRect(VACIO, ven.topLeft, ven.size, CornerRadius(10 * u))
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            mancha(Offset(ven.left + ven.width * 0.35f, ven.center.y), ven.width * 0.4f, NEBULOSA.copy(alpha = 0.45f))
            mancha(Offset(ven.left + ven.width * 0.55f, ven.top + ven.height * 0.3f), ven.width * 0.25f, ROSA.copy(alpha = 0.3f))
            for (k in 0 until 50) drawCircle(Color.White.copy(alpha = 0.4f + 0.6f * azar(91, k)), (0.5f + 1.2f * azar(92, k)) * u, Offset(ven.left + ven.width * azar(93, k), ven.top + ven.height * azar(94, k)))
            val pc = Offset(ven.left + ven.width * 0.74f, ven.top + ven.height * 0.58f)
            val pr = ven.height * 0.26f
            drawArc(AZUL.copy(alpha = 0.5f), 180f, 180f, false, Offset(pc.x - pr * 1.8f, pc.y - pr * 0.4f), Size(pr * 3.6f, pr * 0.8f), style = Stroke(2 * u))
            drawCircle(PLANETA, pr, pc)
            drawCircle(Color.Black.copy(alpha = 0.35f), pr, Offset(pc.x + pr * 0.35f, pc.y + pr * 0.2f))
            drawArc(AZUL, 0f, 180f, false, Offset(pc.x - pr * 1.8f, pc.y - pr * 0.4f), Size(pr * 3.6f, pr * 0.8f), style = Stroke(2 * u))
        }
        // Las pantallas de telemetría a los lados.
        listOf(Rect(w * 0.03f, ven.top + 4 * u, w * 0.17f, ven.bottom - 4 * u), Rect(w * 0.83f, ven.top + 4 * u, w * 0.97f, ven.bottom - 4 * u)).forEachIndexed { i, r ->
            drawRect(PANEL, r.topLeft, r.size)
            drawRect(AZUL.copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(1 * u))
            val onda = Path().apply { for (k in 0..20) { val x = r.left + r.width * k / 20f; val y = r.top + r.height * (0.3f + 0.12f * sin(k * 0.9f + i * 2f)); if (k == 0) moveTo(x, y) else lineTo(x, y) } }
            drawPath(onda, (if (i == 0) VERDE else AZUL).copy(alpha = 0.8f), style = Stroke(1.2f * u))
            for (k in 0 until 4) drawRect(AZUL.copy(alpha = 0.6f), Offset(r.left + 4 * u, r.top + r.height * 0.55f + k * 6 * u), Size(r.width * (0.3f + 0.5f * azar(95 + i, k)), 2.5f * u))
        }
        // Los racks: los rieles atornillados a los lados y las rejillas.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 6 * u, w * 0.985f, d.base.top + 2 * u)
        drawRect(PANEL, cuerpo.topLeft, cuerpo.size)
        listOf(cuerpo.left, cuerpo.right - w * 0.05f).forEach { x ->
            drawRect(METAL, Offset(x, cuerpo.top), Size(w * 0.05f, cuerpo.height))
            var y = cuerpo.top + 6 * u
            while (y < cuerpo.bottom - 4 * u) { drawRect(PANEL_OSC, Offset(x + w * 0.02f, y), Size(w * 0.012f, 3 * u)); y += 9 * u }
        }
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.6f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        // El piso con sus luces de guía.
        val piso = Rect(0f, d.base.top + 2 * u, w, d.base.bottom)
        drawRect(PANEL_LUZ, piso.topLeft, piso.size)
        var lx = 8 * u
        while (lx < w) { drawCircle(AZUL, 1.6f * u, Offset(lx, piso.center.y)); mancha(Offset(lx, piso.center.y), 6 * u, AZUL.copy(alpha = 0.4f)); lx += 22 * u }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(PANEL_OSC, r.topLeft, r.size)
            drawRect(AZUL, r.topLeft, r.size, style = Stroke(1 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El hueco del rack.
        drawRect(Color(0xFF02040A))
        if (c.vacio) {
            // Un lugar libre: una tapa ciega con su rejilla.
            drawRect(PANEL, Offset(w * 0.04f, h * 0.06f), Size(w * 0.92f, h * 0.88f))
            var y = h * 0.2f
            while (y < h * 0.8f) { drawRoundRect(PANEL_OSC, Offset(w * 0.2f, y), Size(w * 0.6f, 3 * u), CornerRadius(1.5f * u)); y += 7 * u }
            return
        }
        // El módulo, que sale al abrirlo.
        val baja = h * SALIDA * ab
        val e = w * 0.02f * ab
        val m = Rect(w * 0.02f - e, h * 0.04f + baja, w * 0.98f + e, h * 0.98f + baja)
        if (ab > 0f) sombraSuave(Rect(m.left, m.bottom - 4 * u, m.right, m.bottom + 2 * u), 2 * u, 8 * u * ab, 0.4f * ab)
        drawRoundRect(PANEL_LUZ, m.topLeft, m.size, CornerRadius(3 * u))
        bisel(m, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.4f), 1.2f * u)
        // Las asas de los lados.
        listOf(m.left + w * 0.04f, m.right - w * 0.04f - 6 * u).forEach { x ->
            drawRoundRect(METAL, Offset(x, m.top + m.height * 0.2f), Size(6 * u, m.height * 0.6f), CornerRadius(3 * u))
            drawRoundRect(PANEL_OSC, Offset(x + 1.5f * u, m.top + m.height * 0.26f), Size(3 * u, m.height * 0.48f), CornerRadius(1.5f * u))
        }
        // La fila de LED de estado: tantos encendidos como fichas, hasta ocho.
        val k = c.n.coerceIn(0, 8)
        for (i in 0 until 8) {
            val p = Offset(m.left + w * 0.16f + i * w * 0.04f, m.top + m.height * 0.16f)
            if (i < k) { mancha(p, 5 * u, VERDE.copy(alpha = 0.5f)); drawCircle(VERDE, 1.6f * u, p) } else drawCircle(PANEL_OSC, 1.6f * u, p)
        }
        // La pantallita con su onda.
        val pa = Rect(m.right - w * 0.36f, m.top + m.height * 0.08f, m.right - w * 0.14f, m.top + m.height * 0.26f)
        drawRect(PANEL_OSC, pa.topLeft, pa.size)
        val onda = Path().apply { for (j in 0..16) { val x = pa.left + pa.width * j / 16f; val y = pa.center.y + pa.height * 0.3f * sin(j * 0.8f + c.semilla); if (j == 0) moveTo(x, y) else lineTo(x, y) } }
        drawPath(onda, AZUL, style = Stroke(1 * u))
        // El visor con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        drawRect(PANEL_OSC, r.topLeft, r.size)
        drawRect(AZUL.copy(alpha = 0.25f), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(3 * u))
        drawRect(AZUL, r.topLeft, r.size, style = Stroke(1 * u))
    }

    override val rotulo = Rect(0.16f, 0.4f, 0.84f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TEXTO
    override val letraRotulo: FontFamily = LetraExtra.orbita
    override val salida = SALIDA
    override val placa = Rect(0.3f, 0.12f, 0.7f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = TEXTO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El panel oscuro con unas estrellas tenues, la retícula y los corchetes de las esquinas.
        drawRect(VACIO)
        for (k in 0 until 60) drawCircle(Color.White.copy(alpha = 0.1f + 0.25f * azar(97, k)), (0.5f + 1f * azar(98, k)) * u, Offset(w * azar(99, k), h * azar(100, k)))
        val c = Offset(w / 2, h * 0.45f)
        drawCircle(AZUL.copy(alpha = 0.1f), w * 0.3f, c, style = Stroke(1 * u))
        drawCircle(AZUL.copy(alpha = 0.08f), w * 0.18f, c, style = Stroke(1 * u))
        drawLine(AZUL.copy(alpha = 0.08f), Offset(c.x - w * 0.4f, c.y), Offset(c.x + w * 0.4f, c.y), 1 * u)
        drawLine(AZUL.copy(alpha = 0.08f), Offset(c.x, c.y - w * 0.4f), Offset(c.x, c.y + w * 0.4f), 1 * u)
        corchetes(Rect(10 * u, 10 * u, w - 10 * u, h - 10 * u), 30 * u, AZUL.copy(alpha = 0.6f), 2 * u)
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(4.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // Los corchetes del visor, una barra de progreso fina abajo y las marcas del día juliano arriba.
        val m = 3 * u * g
        corchetes(Rect(m, m, w - m, h - m), 10 * u * g, AZUL.copy(alpha = 0.7f), 1.2f * u * g)
        val by = h - 6 * u * g
        drawLine(AZUL.copy(alpha = 0.15f), Offset(w * 0.25f, by), Offset(w * 0.75f, by), 2 * u * g)
        drawLine(AZUL, Offset(w * 0.25f, by), Offset(w * (0.25f + 0.5f * (0.2f + 0.8f * azar(semilla, 1))), by), 2 * u * g)
        for (k in 0 until 12) drawLine(AZUL.copy(alpha = 0.25f), Offset(w * 0.3f + k * 4 * u * g, 3 * u * g), Offset(w * 0.3f + k * 4 * u * g, (if (k % 4 == 0) 7 else 5) * u * g), 0.6f * u)
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 14.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> drawPath(estrella(c, s * 0.48f, 5, 0.42f), NARANJA)
            // Una órbita con su satélite.
            "piensa" -> {
                rotate(-25f, c) { drawOval(AZUL, Offset(c.x - s * 0.44f, c.y - s * 0.2f), Size(s * 0.88f, s * 0.4f), style = Stroke(s * 0.06f)) }
                drawCircle(AZUL, s * 0.12f, c)
                drawCircle(NARANJA, s * 0.08f, Offset(c.x + s * 0.36f, c.y - s * 0.16f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(NARANJA)
            else -> {}
        }
    }

    /** Un parche de misión. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(PANEL_LUZ, s * 0.46f, c)
        drawCircle(AZUL, s * 0.46f, c, style = Stroke(s * 0.08f))
        drawPath(estrella(c, s * 0.26f, 5, 0.42f), Color.White)
    }

    override val desorden = 0f
}
