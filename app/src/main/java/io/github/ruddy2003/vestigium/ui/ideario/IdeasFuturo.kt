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
            "hecho" -> selloHecho(AMBAR, p.color)
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
            "hecho" -> selloHecho(LATON, p.color, cuadrado = true)
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
            "hecho" -> selloHecho(MARINO, p.color)
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
            "hecho" -> selloHecho(AZUL, p.color)
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
