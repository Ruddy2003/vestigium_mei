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
