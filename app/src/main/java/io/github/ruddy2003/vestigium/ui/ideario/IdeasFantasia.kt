package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
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
//  Fantasía
// =====================================================================

/**
 * Horror cósmico: el archivo de un pueblo de la costa. Una estantería de madera oscura, manchada de humedad, con un quinqué
 * encendido y una pila de libros viejos encima, y jirones de niebla. Cada tema es una caja de archivo de cartón, gastada en
 * las esquinas, atada con bramante y sellada con lacre; asoman papeles bajo la tapa, y el tema va en una tarjeta dentro de un
 * portaetiquetas de latón. Por dentro, el cartón kraft con manchas, un cerco de taza, arena y una conchita. Las fichas son
 * fichas de evidencia: etiquetas de manila con su ojal y su cordel.
 */
object IdeasArcano : DisenoIdeas {
    private val MADERA = Color(0xFF3A2A1E)
    private val MADERA_LUZ = Color(0xFF5E4632)
    private val MADERA_OSC = Color(0xFF1E140C)
    private val MOHO = Color(0xFF4A6B5C)
    private val CARTON = Color(0xFF9C8C74)
    private val CARTON_LUZ = Color(0xFFBFB096)
    private val CARTON_OSC = Color(0xFF6E604C)
    private val BRAMANTE = Color(0xFFC9B58A)
    private val LACRE = Color(0xFF7A2C24)
    private val LATON = Color(0xFFB59A5E)
    private val LATON_OSC = Color(0xFF7A6638)
    private val MANILA = Color(0xFFE2CFA0)
    private val TINTA = Color(0xFF1E3329)
    private val KRAFT = Color(0xFFB8986A)
    private val NIEBLA = Color(0xFFDCE3DE)
    private val LLAMA = Color(0xFFF2C46B)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.45f
    override val corona = 0.24f
    override val base = 0.09f
    override val lado = 0.07f
    override val hueco = 0.06f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 4 * u, w * 0.98f, d.base.top + 2 * u)
        // El fondo de tablas verticales, con su veta, y las manchas de humedad que bajan.
        drawRect(t(MADERA_OSC, oscuro), cuerpo.topLeft, cuerpo.size)
        val tablas = 7
        for (k in 0 until tablas) {
            val r = Rect(cuerpo.left + cuerpo.width * k / tablas, cuerpo.top, cuerpo.left + cuerpo.width * (k + 1) / tablas, cuerpo.bottom)
            drawRect(t(mezcla(MADERA_OSC, MADERA, 0.3f + 0.3f * azar(5, k)), oscuro), r.topLeft, r.size)
            vetas(r, Color.Black.copy(alpha = 0.3f), 40 + k, 0.8f, vertical = true, grosor = 0.6f, ondulacion = 0.3f)
            drawLine(Color.Black.copy(alpha = 0.5f), Offset(r.left, r.top), Offset(r.left, r.bottom), 1f * u)
        }
        for (k in 0 until 6) {
            val x = cuerpo.left + cuerpo.width * azar(11, k)
            val y = cuerpo.top + cuerpo.height * azar(12, k) * 0.7f
            mancha(Offset(x, y), (16f + 22f * azar(13, k)) * u, t(MOHO, oscuro).copy(alpha = 0.3f))
            drawLine(t(MOHO, oscuro).copy(alpha = 0.25f), Offset(x, y), Offset(x + 2 * u, y + (30f + 40f * azar(14, k)) * u), 3 * u, StrokeCap.Round)
        }
        // Las baldas, bajo cada fila.
        d.filas.forEach { f ->
            val r = Rect(cuerpo.left, f.bottom, cuerpo.right, f.bottom + maxOf(d.separacion * 0.5f, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.4f), Offset(r.left, r.bottom), Size(r.width, 4 * u))
            drawRect(Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro), t(MADERA, oscuro)), r.top, r.bottom), r.topLeft, r.size)
            vetas(r, Color.Black.copy(alpha = 0.25f), (f.top / u).toInt(), 0.6f, grosor = 0.6f, ondulacion = 0.3f)
        }
        // Los costados.
        listOf(cuerpo.left, cuerpo.right - w * 0.03f).forEach { x ->
            val r = Rect(x, d.corona.bottom - 8 * u, x + w * 0.03f, d.base.bottom)
            drawRect(Brush.horizontalGradient(listOf(t(MADERA, oscuro), t(MADERA_LUZ, oscuro), t(MADERA_OSC, oscuro)), r.left, r.right), r.topLeft, r.size)
        }
        // La tabla de arriba, y encima los libros viejos, el quinqué y la niebla.
        val tapa = Rect(0f, d.corona.bottom - 10 * u, w, d.corona.bottom - 2 * u)
        drawRect(Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro), t(MADERA_OSC, oscuro)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        val libros = listOf(Color(0xFF2D4A3A), Color(0xFF5A2420), Color(0xFF4A3A28))
        var y = tapa.top
        libros.forEachIndexed { k, c ->
            val alto = d.corona.height * (0.1f + 0.03f * k)
            val ancho = w * (0.24f - 0.03f * k)
            val x = w * (0.08f + 0.012f * k)
            drawRect(t(c, oscuro), Offset(x, y - alto), Size(ancho, alto))
            drawLine(t(LATON, oscuro).copy(alpha = 0.7f), Offset(x, y - alto * 0.3f), Offset(x + ancho, y - alto * 0.3f), 1f * u)
            drawLine(t(MANILA, oscuro).copy(alpha = 0.5f), Offset(x + ancho, y - alto + 1.5f * u), Offset(x + ancho, y - 1.5f * u), 2 * u)
            y -= alto
        }
        val q = Offset(w * 0.8f, tapa.top)
        val ch = d.corona.height
        mancha(Offset(q.x, q.y - ch * 0.5f), ch * (if (oscuro) 1.3f else 0.9f), LLAMA.copy(alpha = if (oscuro) 0.32f else 0.18f))
        drawOval(t(LATON, oscuro), Offset(q.x - ch * 0.16f, q.y - ch * 0.08f), Size(ch * 0.32f, ch * 0.08f))
        drawPath(Path().apply { moveTo(q.x - ch * 0.12f, q.y - ch * 0.05f); quadraticTo(q.x - ch * 0.2f, q.y - ch * 0.25f, q.x - ch * 0.06f, q.y - ch * 0.32f)
            lineTo(q.x + ch * 0.06f, q.y - ch * 0.32f); quadraticTo(q.x + ch * 0.2f, q.y - ch * 0.25f, q.x + ch * 0.12f, q.y - ch * 0.05f); close() }, t(LATON, oscuro))
        drawRect(Color.White.copy(alpha = 0.22f), Offset(q.x - ch * 0.07f, q.y - ch * 0.78f), Size(ch * 0.14f, ch * 0.46f))
        drawRect(Color.White.copy(alpha = 0.35f), Offset(q.x - ch * 0.07f, q.y - ch * 0.78f), Size(ch * 0.14f, ch * 0.46f), style = Stroke(0.8f * u))
        drawOval(LLAMA, Offset(q.x - ch * 0.03f, q.y - ch * 0.56f), Size(ch * 0.06f, ch * 0.14f))
        for (k in 0 until 3) {
            val yy = d.corona.top + ch * (0.2f + 0.22f * k)
            drawRoundRect(t(NIEBLA, oscuro).copy(alpha = 0.12f), Offset(w * (0.1f + 0.2f * azar(21, k)) - 40 * u, yy), Size(w * 0.6f, 8 * u), CornerRadius(4 * u))
        }
        // El piso de duela, y la placa de latón en el zócalo.
        val piso = Rect(0f, d.base.top + d.base.height * 0.6f, w, d.base.bottom)
        drawRect(t(Color(0xFF4A3828), oscuro), piso.topLeft, piso.size)
        for (k in 0 until 6) drawLine(Color.Black.copy(alpha = 0.4f), Offset(w * k / 6f + 20 * u * azar(31, k), piso.top), Offset(w * k / 6f + 20 * u * azar(31, k), piso.bottom), 0.8f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawRect(Brush.verticalGradient(listOf(t(mezcla(LATON, Color.White, 0.2f), oscuro), t(LATON_OSC, oscuro)), r.top, r.bottom), r.topLeft, r.size)
            listOf(r.left + 3 * u, r.right - 3 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.2f * u, Offset(x, r.center.y)) }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un frasco de muestras con agua turbia, y algo enroscado adentro.
            val r = Rect(w * 0.36f, h * 0.38f, w * 0.64f, h * 0.98f)
            drawRoundRect(Color(0xFF6E8A6A).copy(alpha = 0.45f), Offset(r.left, r.top + r.height * 0.2f), Size(r.width, r.height * 0.8f), CornerRadius(4 * u))
            drawPath(Path().apply { moveTo(r.center.x - r.width * 0.2f, r.bottom - 6 * u); cubicTo(r.left, r.center.y, r.right, r.center.y, r.center.x, r.top + r.height * 0.4f) }, Color(0xFF1E2A22).copy(alpha = 0.8f), style = Stroke(3 * u, cap = StrokeCap.Round))
            drawRoundRect(Color.White.copy(alpha = 0.4f), r.topLeft, r.size, CornerRadius(4 * u), style = Stroke(1.2f * u))
            drawRect(t(LATON_OSC, oscuro), Offset(r.left - 2 * u, r.top - 4 * u), Size(r.width + 4 * u, 6 * u))
            return
        }
        val cuerpo = Rect(w * 0.06f, h * 0.27f, w * 0.94f, h * 0.97f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.14f
        // Los papeles que asoman bajo la tapa.
        if (sube > 1f) {
            drawRect(Color(0xFF14100C), Offset(cuerpo.left + 2 * u, cuerpo.top - sube), Size(cuerpo.width - 4 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.06f + 0.15f * i)
                val y = cuerpo.top - sube * (0.3f + 0.65f * azar(c.semilla, i))
                rotate(-6f + 12f * azar(c.semilla, i + 20), Offset(x, cuerpo.top)) {
                    drawRect(t(if (i % 2 == 0) MANILA else Color(0xFFEDE6D3), oscuro), Offset(x, y), Size(cuerpo.width * 0.2f, cuerpo.top - y + 3 * u))
                }
            }
        }
        // La caja: cartón gris pardo, más oscuro en los bordes, con las esquinas gastadas y una mancha de agua.
        drawRect(Brush.verticalGradient(listOf(t(CARTON_LUZ, oscuro), t(CARTON, oscuro), t(CARTON_OSC, oscuro)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        mancha(Offset(cuerpo.left + cuerpo.width * azar(c.semilla, 60), cuerpo.top + cuerpo.height * 0.75f), cuerpo.width * 0.25f, t(CARTON_OSC, oscuro).copy(alpha = 0.35f))
        listOf(cuerpo.topLeft, Offset(cuerpo.right, cuerpo.top), Offset(cuerpo.left, cuerpo.bottom), Offset(cuerpo.right, cuerpo.bottom)).forEach { o ->
            drawCircle(t(CARTON_LUZ, oscuro).copy(alpha = 0.7f), 4 * u, o)
        }
        drawRect(t(CARTON_OSC, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1.2f * u))
        // La tapa, que sube con los papeles (y más al abrirse).
        val tapa = Rect(w * 0.04f, cuerpo.top - sube - h * 0.13f, w * 0.96f, cuerpo.top - sube + 1 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, tapa.bottom), Size(tapa.width, 2.5f * u))
        drawRect(Brush.verticalGradient(listOf(t(CARTON_LUZ, oscuro), t(CARTON, oscuro)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        drawRect(t(CARTON_OSC, oscuro), tapa.topLeft, tapa.size, style = Stroke(1f * u))
        // El bramante, en cruz, con su nudo y el lacre, y un poco flojo cuando se abre.
        val nudo = Offset(w * 0.86f, h * 0.88f)
        val flojo = ab * 4 * u
        drawLine(t(BRAMANTE, oscuro), Offset(nudo.x, tapa.top), Offset(nudo.x + flojo, cuerpo.bottom), 1.5f * u)
        drawLine(t(BRAMANTE, oscuro), Offset(cuerpo.left, nudo.y), Offset(cuerpo.right, nudo.y + flojo), 1.5f * u)
        drawCircle(LACRE, 5 * u, nudo)
        drawCircle(mezcla(LACRE, Color.Black, 0.3f), 3 * u, nudo, style = Stroke(0.8f * u))
        // El portaetiquetas de latón con su tarjeta.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(MANILA, oscuro), r.topLeft, r.size)
        drawLine(Color(0xFFB04A3A).copy(alpha = 0.5f), Offset(r.left + 3 * u, r.top + r.height * 0.22f), Offset(r.right - 3 * u, r.top + r.height * 0.22f), 0.8f * u)
        drawRect(t(LATON, oscuro), Offset(r.left - 2.5f * u, r.top - 2.5f * u), Size(r.width + 5 * u, r.height + 5 * u), style = Stroke(2.4f * u))
        listOf(r.left - 6 * u, r.right + 6 * u).forEach { x -> drawCircle(t(LATON, oscuro), 2.2f * u, Offset(x, r.center.y)); drawCircle(t(LATON_OSC, oscuro), 0.8f * u, Offset(x, r.center.y)) }
    }

    override val rotulo = Rect(0.2f, 0.44f, 0.76f, 0.74f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.castoro
    override val salida = 0f
    override val placa = Rect(0.3f, 0.08f, 0.7f, 0.58f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la caja: cartón kraft con manchas, un cerco de taza, granos de arena y una conchita.
        drawRect(Brush.verticalGradient(listOf(t(KRAFT, oscuro), t(mezcla(KRAFT, CARTON_OSC, 0.3f), oscuro))))
        for (k in 0 until 40) mancha(Offset(w * azar(41, k), h * azar(42, k)), (10f + 30f * azar(43, k)) * u, t(CARTON_OSC, oscuro).copy(alpha = 0.1f))
        val taza = Offset(w * 0.72f, h * 0.3f)
        drawCircle(Color(0xFF5A3A20).copy(alpha = 0.3f), 30 * u, taza, style = Stroke(3 * u))
        drawCircle(Color(0xFF5A3A20).copy(alpha = 0.15f), 28 * u, taza, style = Stroke(1.5f * u))
        for (k in 0 until 50) drawCircle(Color(0xFFD8C8A0).copy(alpha = 0.8f), (0.6f + 0.8f * azar(44, k)) * u, Offset(w * (0.1f + 0.3f * azar(45, k)), h * (0.75f + 0.15f * azar(46, k))))
        val concha = Offset(w * 0.2f, h * 0.82f)
        for (k in 0 until 7) {
            val a = Math.toRadians(-160.0 + k * 23.0)
            drawLine(Color(0xFFE8DCC6), concha, Offset(concha.x + 12 * u * cos(a).toFloat(), concha.y + 12 * u * sin(a).toFloat()), 2.4f * u, StrokeCap.Round)
        }
        // Las paredes de cartón.
        val pl = 10 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CARTON, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 20 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFEDE6D3) else TINTA
    override val pared: Dp = 12.dp
    override val columnas = 2
    override val lineas = 5

    /** La etiqueta de evidencia: un rectángulo con las esquinas de arriba cortadas. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val k = minOf(s.width, s.height) * 0.12f
        moveTo(k, 0f); lineTo(s.width - k, 0f); lineTo(s.width, k); lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(0f, k); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(MANILA.copy(alpha = 0.4f))
        // El ojal reforzado y su cordel, arriba al centro.
        val hoyo = Offset(w / 2, 9 * u * g)
        drawCircle(Color(0xFFF2E8D0), 5 * u * g, hoyo)
        drawCircle(LATON_OSC.copy(alpha = 0.5f), 5 * u * g, hoyo, style = Stroke(0.8f * u))
        drawCircle(Color.Black.copy(alpha = 0.6f), 2 * u * g, hoyo)
        drawPath(Path().apply { moveTo(hoyo.x, hoyo.y); cubicTo(hoyo.x - 10 * u * g, hoyo.y - 6 * u * g, hoyo.x - 20 * u * g, 2 * u, hoyo.x - 34 * u * g, 0f) }, BRAMANTE, style = Stroke(1.3f * u * g))
        // La oreja doblada abajo a la izquierda y una mancha de agua.
        val o = 12 * u * g
        drawPath(Path().apply { moveTo(0f, h - o); lineTo(o, h); lineTo(0f, h); close() }, Color.Black.copy(alpha = 0.18f))
        drawPath(Path().apply { moveTo(0f, h - o); lineTo(o, h - o); lineTo(o, h); close() }, Color(0xFFE9DCB8))
        mancha(Offset(w * (0.5f + 0.4f * azar(semilla, 1)), h * (0.4f + 0.4f * azar(semilla, 2))), minOf(w, h) * 0.3f, Color(0xFF8A7A58).copy(alpha = 0.14f))
    }

    override val margenes = MargenesFicha(20.dp, 12.dp, 14.dp, 36.dp, 26.dp, 30.dp)
    override val proporcionFicha = 0.92f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre del archivo.
            "hecho" -> selloHecho(LACRE, p.color)
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y); lineTo(c.x - s * 0.08f, c.y + s * 0.3f); lineTo(c.x + s * 0.4f, c.y - s * 0.36f) }, TINTA, style = Stroke(s * 0.13f, cap = StrokeCap.Round))
            // Una espiral: algo que da vueltas y no termina de cerrar.
            "piensa" -> drawPath(Path().apply {
                moveTo(c.x, c.y)
                for (k in 1..24) { val a = k * 0.5f; val r = s * 0.02f * k; lineTo(c.x + r * cos(a), c.y + r * sin(a)) }
            }, MOHO, style = Stroke(s * 0.08f, cap = StrokeCap.Round))
            "duda" -> { drawCircle(p.tenue, s * 0.32f, c, style = Stroke(s * 0.1f)); drawLine(p.tenue, Offset(c.x - s * 0.3f, c.y + s * 0.3f), Offset(c.x + s * 0.3f, c.y - s * 0.3f), s * 0.1f) }
            // Una X de sello rojo, dentro de su recuadro.
            "desacuerdo" -> {
                drawRect(LACRE, Offset(c.x - s * 0.4f, c.y - s * 0.4f), Size(s * 0.8f, s * 0.8f), style = Stroke(s * 0.08f))
                drawLine(LACRE, Offset(c.x - s * 0.25f, c.y - s * 0.25f), Offset(c.x + s * 0.25f, c.y + s * 0.25f), s * 0.1f)
                drawLine(LACRE, Offset(c.x + s * 0.25f, c.y - s * 0.25f), Offset(c.x - s * 0.25f, c.y + s * 0.25f), s * 0.1f)
            }
            else -> {}
        }
    }

    /** Un clip de latón. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        val clip = Path().apply {
            moveTo(c.x + s * 0.12f, c.y - s * 0.1f); lineTo(c.x + s * 0.12f, c.y + s * 0.36f)
            quadraticTo(c.x + s * 0.12f, c.y + s * 0.48f, c.x, c.y + s * 0.48f); quadraticTo(c.x - s * 0.12f, c.y + s * 0.48f, c.x - s * 0.12f, c.y + s * 0.36f)
            lineTo(c.x - s * 0.12f, c.y - s * 0.36f); quadraticTo(c.x - s * 0.12f, c.y - s * 0.5f, c.x + s * 0.04f, c.y - s * 0.5f)
            quadraticTo(c.x + s * 0.22f, c.y - s * 0.5f, c.x + s * 0.22f, c.y - s * 0.36f); lineTo(c.x + s * 0.22f, c.y + s * 0.2f)
        }
        drawPath(clip, LATON_OSC, style = Stroke(s * 0.08f, cap = StrokeCap.Round))
    }

    override val desorden = 1.5f
}

/**
 * Tierras Antiguas: la sala de piedra de una compañía de viaje. Un muro de sillares con estantes tallados y runas inventadas
 * a lo largo de sus cantos, un dintel con una banda de runas y dos faroles colgando. Cada tema es un arcón de la compañía, de
 * tablas con flejes de hierro y clavos dorados, tapa abovedada y una cerradura con una gema verde; asoman mapas y páginas, y
 * el tema va en una placa de latón. Por dentro, forro de terciopelo verde capitoné. Las fichas son páginas del cronista: vitela
 * de borde barbado con nudos en las esquinas y un camino punteado con su rosa de los vientos.
 */
object IdeasAntiguas : DisenoIdeas {
    private val PIEDRA = Color(0xFF6F6A62)
    private val PIEDRA_LUZ = Color(0xFF8E8880)
    private val PIEDRA_OSC = Color(0xFF46423C)
    private val JUNTA = Color(0xFF2E2B27)
    private val MADERA = Color(0xFF5A3A22)
    private val MADERA_LUZ = Color(0xFF7A5232)
    private val MADERA_OSC = Color(0xFF33210F)
    private val HIERRO = Color(0xFF2A2A2E)
    private val ORO = Color(0xFFC9A04A)
    private val ESMERALDA = Color(0xFF2E7A5A)
    private val RUBI = Color(0xFF8E1B24)
    private val TERCIOPELO = Color(0xFF1F4A38)
    private val VITELA = Color(0xFFEDE2C6)
    private val LATON = Color(0xFFD8B866)
    private val FAROL = Color(0xFFF2C46B)
    private val TINTA = Color(0xFF1E1D1A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.4f
    override val corona = 0.24f
    override val base = 0.1f
    override val lado = 0.08f
    override val hueco = 0.07f

    /** Una runa inventada: dos a cuatro trazos rectos, siempre los mismos para cada [k]. */
    private fun DrawScope.runa(k: Int, o: Offset, s: Float, color: Color, grosor: Float) {
        drawLine(color, Offset(o.x, o.y - s / 2), Offset(o.x, o.y + s / 2), grosor, StrokeCap.Round)
        val trazos = 1 + k.mod(3)
        for (j in 0 until trazos) {
            val y0 = o.y - s / 2 + s * (0.1f + 0.35f * j)
            val dx = if ((k + j) % 2 == 0) s * 0.4f else -s * 0.4f
            drawLine(color, Offset(o.x, y0), Offset(o.x + dx, y0 + s * (if (k % 4 < 2) 0.3f else -0.2f + 0.5f * (j % 2))), grosor, StrokeCap.Round)
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val y0 = d.corona.bottom; val y1 = d.base.top
        // El muro de sillares: hiladas con juntas alternadas, cada piedra con su tono y su bisel.
        drawRect(t(JUNTA, oscuro), Offset(0f, y0), Size(w, y1 - y0))
        val alto = 24 * u
        var y = y0; var hilada = 0
        while (y < y1) {
            var x = if (hilada % 2 == 0) 0f else -alto * 1.2f
            var k = 0
            while (x < w) {
                val largo = alto * (2f + 1.2f * azar(hilada, k))
                val r = Rect(x + 1 * u, y + 1 * u, x + largo - 1 * u, minOf(y + alto, y1) - 1 * u)
                drawRect(t(mezcla(PIEDRA_OSC, PIEDRA_LUZ, 0.3f + 0.5f * azar(hilada + 100, k)), oscuro), r.topLeft, r.size)
                bisel(r, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.25f), 1.4f * u)
                x += largo; k++
            }
            y += alto; hilada++
        }
        // Los estantes tallados: un hueco más oscuro para cada fila y el canto con runas de oro.
        d.filas.forEachIndexed { i, f ->
            val hueco = Rect(w * 0.04f, f.top - d.separacion * 0.4f, w * 0.96f, f.bottom)
            drawRect(t(mezcla(PIEDRA_OSC, Color.Black, 0.35f), oscuro), hueco.topLeft, hueco.size)
            sombraInterior(hueco, 12 * u, 0.35f)
            val canto = Rect(w * 0.03f, f.bottom, w * 0.97f, f.bottom + maxOf(d.separacion * 0.5f, 9 * u))
            drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro), t(PIEDRA, oscuro)), canto.top, canto.bottom), canto.topLeft, canto.size)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(canto.left, canto.bottom), Size(canto.width, 3 * u))
            val n = (canto.width / (12 * u)).toInt()
            for (j in 0 until n) runa(j * 7 + i * 3, Offset(canto.left + canto.width * (j + 0.5f) / n, canto.center.y), canto.height * 0.6f, t(ORO, oscuro).copy(alpha = 0.7f), 0.9f * u)
        }
        // El dintel con su banda de runas, y los faroles colgando de sus cadenas.
        val ch = d.corona.height
        val dintel = Rect(0f, d.corona.top + ch * 0.38f, w, d.corona.bottom)
        drawRect(Brush.verticalGradient(listOf(t(PIEDRA_LUZ, oscuro), t(PIEDRA, oscuro), t(PIEDRA_OSC, oscuro)), dintel.top, dintel.bottom), dintel.topLeft, dintel.size)
        drawRect(t(JUNTA, oscuro), Offset(0f, dintel.top + dintel.height * 0.25f), Size(w, 1.2f * u))
        drawRect(t(JUNTA, oscuro), Offset(0f, dintel.bottom - dintel.height * 0.25f), Size(w, 1.2f * u))
        val nr = (w / (14 * u)).toInt()
        for (j in 0 until nr) runa(j * 5 + 1, Offset(w * (j + 0.5f) / nr, dintel.center.y), dintel.height * 0.36f, t(ORO, oscuro), 1.3f * u)
        listOf(w * 0.14f, w * 0.86f).forEach { fx ->
            val arriba = d.corona.top
            val farol = Rect(fx - ch * 0.1f, d.corona.top + ch * 0.32f, fx + ch * 0.1f, d.corona.top + ch * 0.62f)
            var cy = arriba
            while (cy < farol.top - 2 * u) { drawOval(t(HIERRO, oscuro), Offset(fx - 1.5f * u, cy), Size(3 * u, 4 * u), style = Stroke(0.8f * u)); cy += 3.5f * u }
            mancha(farol.center, ch * (if (oscuro) 0.9f else 0.6f), FAROL.copy(alpha = if (oscuro) 0.35f else 0.22f))
            drawRect(FAROL.copy(alpha = 0.85f), farol.topLeft, farol.size)
            drawRect(t(HIERRO, oscuro), farol.topLeft, farol.size, style = Stroke(1.6f * u))
            drawLine(t(HIERRO, oscuro), Offset(fx, farol.top), Offset(fx, farol.bottom), 1f * u)
            drawPath(Path().apply { moveTo(farol.left - 2 * u, farol.top); lineTo(fx, farol.top - ch * 0.06f); lineTo(farol.right + 2 * u, farol.top); close() }, t(HIERRO, oscuro))
            drawRect(t(HIERRO, oscuro), Offset(farol.left - 1 * u, farol.bottom), Size(farol.width + 2 * u, 2.5f * u))
        }
        // El piso de losas.
        val piso = Rect(0f, d.base.top, w, d.base.bottom)
        drawRect(t(PIEDRA_OSC, oscuro), piso.topLeft, piso.size)
        for (j in 0 until 5) drawLine(t(JUNTA, oscuro), Offset(w * (j + 0.5f) / 5, piso.top), Offset(w * (j + 0.5f) / 5 + (j - 2) * 6 * u, piso.bottom), 1f * u)
        drawLine(t(JUNTA, oscuro), Offset(0f, piso.top + piso.height * 0.5f), Offset(w, piso.top + piso.height * 0.5f), 1f * u)
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, piso.top), Offset(w, piso.top), 1.5f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = Rect(d.base.left + d.base.width * pl.left, d.base.top + d.base.height * pl.top, d.base.left + d.base.width * pl.right, d.base.top + d.base.height * pl.bottom)
            drawRect(t(PIEDRA_LUZ, oscuro), r.topLeft, r.size)
            drawRect(t(ORO, oscuro), r.topLeft, r.size, style = Stroke(1.4f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un mapa enrollado y atado con una cinta roja.
            val r = Rect(w * 0.2f, h * 0.82f, w * 0.8f, h * 0.95f)
            drawRoundRect(Brush.verticalGradient(listOf(t(VITELA, oscuro), t(mezcla(VITELA, MADERA, 0.35f), oscuro)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(r.height / 2))
            drawOval(t(mezcla(VITELA, MADERA, 0.3f), oscuro), Offset(r.right - r.height * 0.6f, r.top), Size(r.height * 0.6f, r.height), style = Stroke(1f * u))
            drawRect(t(RUBI, oscuro), Offset(r.center.x - 2 * u, r.top - 1 * u), Size(4 * u, r.height + 2 * u))
            return
        }
        val cuerpo = Rect(w * 0.07f, h * 0.44f, w * 0.93f, h * 0.95f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Size(cuerpo.width, h * 0.06f))
        // Los mapas y las páginas que asoman entre la tapa y el arcón.
        if (sube > 1f) {
            drawRect(Color(0xFF120C08), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.1f + 0.13f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                if (i % 3 == 1) {
                    drawRoundRect(t(VITELA, oscuro), Offset(x, y), Size(cuerpo.width * 0.08f, cuerpo.top - y + 2 * u), CornerRadius(cuerpo.width * 0.04f))
                    drawOval(t(mezcla(VITELA, MADERA, 0.4f), oscuro), Offset(x, y), Size(cuerpo.width * 0.08f, cuerpo.width * 0.04f))
                } else drawRect(t(if (i % 2 == 0) VITELA else Color(0xFFD9C79C), oscuro), Offset(x, y), Size(cuerpo.width * 0.12f, cuerpo.top - y + 2 * u))
            }
        }
        // El arcón: tablas con su veta y sus juntas.
        drawRect(Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro), t(MADERA, oscuro), t(MADERA_OSC, oscuro)), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, MADERA_OSC.copy(alpha = 0.45f), c.semilla, 1f, grosor = 0.7f, ondulacion = 0.5f)
        for (j in 1 until 4) drawLine(Color.Black.copy(alpha = 0.4f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * j / 4), Offset(cuerpo.right, cuerpo.top + cuerpo.height * j / 4), 1f * u)
        // La tapa abovedada, que sube.
        val ty = cuerpo.top - sube
        val tapa = Path().apply { moveTo(cuerpo.left - 2 * u, ty); lineTo(cuerpo.right + 2 * u, ty); quadraticTo(cuerpo.right + 2 * u, ty - h * 0.24f, w / 2, ty - h * 0.24f)
            quadraticTo(cuerpo.left - 2 * u, ty - h * 0.24f, cuerpo.left - 2 * u, ty); close() }
        drawPath(tapa, Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro), t(MADERA, oscuro)), ty - h * 0.24f, ty))
        drawPath(tapa, Color.Black.copy(alpha = 0.4f), style = Stroke(1f * u))
        // Los flejes de hierro, de la tapa al pie, con sus clavos de oro, y el fleje del borde.
        listOf(0.2f, 0.8f).forEach { fx ->
            val x = w * fx
            drawRect(t(HIERRO, oscuro), Offset(x - 3 * u, ty - h * 0.2f), Size(6 * u, cuerpo.bottom - ty + h * 0.2f))
            var yy = ty - h * 0.16f
            while (yy < cuerpo.bottom - 3 * u) { drawCircle(t(ORO, oscuro), 1.3f * u, Offset(x, yy)); yy += 9 * u }
        }
        drawRect(t(HIERRO, oscuro), Offset(cuerpo.left - 2 * u, ty - 2 * u), Size(cuerpo.width + 4 * u, 4.5f * u))
        // La cerradura, con su gema verde tallada.
        val cerr = Offset(w / 2, cuerpo.top + h * 0.05f)
        drawPath(Path().apply { moveTo(cerr.x - w * 0.08f, cerr.y - h * 0.04f); lineTo(cerr.x + w * 0.08f, cerr.y - h * 0.04f); lineTo(cerr.x + w * 0.06f, cerr.y + h * 0.04f)
            lineTo(cerr.x, cerr.y + h * 0.07f); lineTo(cerr.x - w * 0.06f, cerr.y + h * 0.04f); close() }, t(HIERRO, oscuro))
        gema(cerr, h * 0.028f, ESMERALDA)
        // La placa de latón con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(Brush.verticalGradient(listOf(t(mezcla(LATON, Color.White, 0.25f), oscuro), t(LATON, oscuro), t(mezcla(LATON, Color.Black, 0.25f), oscuro)), r.top, r.bottom), r.topLeft, r.size)
        drawRect(t(mezcla(LATON, Color.Black, 0.4f), oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), Offset(r.right, r.bottom)).forEach { o -> drawCircle(t(HIERRO, oscuro), 1.4f * u, Offset(o.x + (if (o.x < r.center.x) 4 else -4) * u, o.y + (if (o.y < r.center.y) 4 else -4) * u)) }
    }

    /** Una gema tallada: un rombo con sus facetas y un brillo. */
    private fun DrawScope.gema(c: Offset, r: Float, color: Color) {
        val p = Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y + r); lineTo(c.x - r, c.y); close() }
        drawPath(p, color)
        drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y); close() }, mezcla(color, Color.White, 0.35f))
        drawPath(Path().apply { moveTo(c.x - r, c.y); lineTo(c.x, c.y + r); lineTo(c.x, c.y); close() }, mezcla(color, Color.Black, 0.3f))
        drawPath(p, ORO, style = Stroke(r * 0.18f))
    }

    override val rotulo = Rect(0.2f, 0.6f, 0.8f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.camino
    override val salida = 0f
    override val placa = Rect(0.2f, 0.14f, 0.8f, 0.86f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Terciopelo verde capitoné: un brillo en diagonal, botones dorados en rombo y los pliegues que salen de cada uno.
        drawRect(Brush.linearGradient(listOf(t(mezcla(TERCIOPELO, Color.Black, 0.25f), oscuro), t(mezcla(TERCIOPELO, Color.White, 0.08f), oscuro), t(mezcla(TERCIOPELO, Color.Black, 0.3f), oscuro)), Offset.Zero, Offset(w, h)))
        val paso = 58 * u
        var fila = 0
        var y = paso / 2
        while (y < h) {
            var x = if (fila % 2 == 0) paso / 2 else 0f
            while (x < w + paso) {
                val o = Offset(x, y)
                listOf(Offset(paso / 2, paso / 2), Offset(-paso / 2, paso / 2), Offset(paso / 2, -paso / 2), Offset(-paso / 2, -paso / 2)).forEach { dlt ->
                    drawLine(Color.Black.copy(alpha = 0.25f), o, Offset(o.x + dlt.x, o.y + dlt.y), 1.6f * u)
                    drawLine(Color.White.copy(alpha = 0.06f), Offset(o.x + 1 * u, o.y), Offset(o.x + dlt.x + 1 * u, o.y + dlt.y), 1f * u)
                }
                mancha(o, 10 * u, Color.Black.copy(alpha = 0.3f))
                drawCircle(t(ORO, oscuro), 2.6f * u, o)
                drawCircle(Color.White.copy(alpha = 0.5f), 0.9f * u, Offset(o.x - 0.8f * u, o.y - 0.8f * u))
                x += paso
            }
            y += paso / 2; fila++
        }
        // Las paredes de tablas con su fila de clavos dorados.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(MADERA, oscuro), r.topLeft, r.size)
            vetas(r, MADERA_OSC.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.5f)
        }
        var x = pl * 2
        while (x < w - pl) { drawCircle(t(ORO, oscuro), 1.4f * u, Offset(x, pl / 2)); drawCircle(t(ORO, oscuro), 1.4f * u, Offset(x, h - pl / 2)); x += 14 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFEFE6CC)
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 34, 0.007f)) }

    /** Un nudo en una esquina: dos lazos cuadrados entrelazados, en verde y oro. */
    private fun DrawScope.nudo(c: Offset, s: Float, grosor: Float) {
        rotate(45f, c) {
            drawRoundRect(ESMERALDA.copy(alpha = 0.8f), Offset(c.x - s * 0.5f, c.y - s * 0.22f), Size(s, s * 0.44f), CornerRadius(s * 0.2f), style = Stroke(grosor))
            drawRoundRect(ORO, Offset(c.x - s * 0.22f, c.y - s * 0.5f), Size(s * 0.44f, s), CornerRadius(s * 0.2f), style = Stroke(grosor))
        }
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.8f else 1f
        val s = 10 * u * g
        listOf(Offset(s * 0.8f, s * 0.8f), Offset(w - s * 0.8f, s * 0.8f), Offset(s * 0.8f, h - s * 0.8f), Offset(w - s * 0.8f, h - s * 0.8f)).forEach { nudo(it, s, 1.1f * u * g) }
        // Un camino punteado tenue que cruza la página, y una rosa de los vientos chica.
        val camino = Path().apply {
            moveTo(w * 0.1f, h * (0.6f + 0.2f * azar(semilla, 1)))
            cubicTo(w * 0.35f, h * (0.3f + 0.4f * azar(semilla, 2)), w * 0.6f, h * (0.5f + 0.4f * azar(semilla, 3)), w * 0.9f, h * (0.35f + 0.3f * azar(semilla, 4)))
        }
        drawPath(camino, RUBI.copy(alpha = 0.18f), style = Stroke(1.2f * u * g, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u * g, 3 * u * g))))
        val rosa = Offset(w - s * 2.6f, h - s * 2.8f)
        for (k in 0 until 4) {
            val a = k * 1.5708f
            drawPath(Path().apply { moveTo(rosa.x, rosa.y); lineTo(rosa.x + s * 0.25f * cos(a + 0.785f), rosa.y + s * 0.25f * sin(a + 0.785f)); lineTo(rosa.x + s * cos(a), rosa.y + s * sin(a)); close() }, TINTA.copy(alpha = 0.18f))
        }
        drawCircle(TINTA.copy(alpha = 0.15f), s * 0.55f, rosa, style = Stroke(0.6f * u))
    }

    override val margenes = MargenesFicha(15.dp, 16.dp, 16.dp, 28.dp, 32.dp, 34.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Una runa grabada en piedra.
            "hecho" -> { drawRoundRect(MADERA, Offset(c.x - s * 0.36f, c.y - s * 0.42f), Size(s * 0.72f, s * 0.84f), androidx.compose.ui.geometry.CornerRadius(s * 0.16f)); runa(3, c, s * 0.56f, p.color, s * 0.09f) }
            "convence" -> gema(c, s * 0.42f, ESMERALDA)
            "piensa" -> runa(5, c, s * 0.8f, MADERA, s * 0.1f)
            // Una media luna: la compañía cuenta el tiempo por lunas.
            "duda" -> { drawCircle(p.tenue, s * 0.36f, c); drawCircle(p.color, s * 0.3f, Offset(c.x + s * 0.16f, c.y - s * 0.06f)) }
            "desacuerdo" -> { runa(2, c, s * 0.8f, RUBI, s * 0.1f); drawLine(RUBI, Offset(c.x - s * 0.4f, c.y + s * 0.3f), Offset(c.x + s * 0.4f, c.y - s * 0.3f), s * 0.1f) }
            else -> {}
        }
    }

    /** Un broche: un aro de oro con un rubí. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(ORO, s * 0.42f, c, style = Stroke(s * 0.14f))
        drawCircle(RUBI, s * 0.22f, c)
        drawCircle(Color.White.copy(alpha = 0.5f), s * 0.07f, Offset(c.x - s * 0.07f, c.y - s * 0.07f))
    }

    override val desorden = 0f
}
