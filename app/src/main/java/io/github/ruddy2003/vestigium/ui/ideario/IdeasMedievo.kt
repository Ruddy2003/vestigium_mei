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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Media
// =====================================================================

/** La cruz patada: cuatro brazos que se abren hacia la punta. */
internal fun cruzPatada(c: Offset, r: Float): Path = Path().apply {
    val a = r * 0.13f; val b = r * 0.42f
    moveTo(c.x - a, c.y - a)
    lineTo(c.x - b, c.y - r); lineTo(c.x + b, c.y - r); lineTo(c.x + a, c.y - a)
    lineTo(c.x + r, c.y - b); lineTo(c.x + r, c.y + b); lineTo(c.x + a, c.y + a)
    lineTo(c.x + b, c.y + r); lineTo(c.x - b, c.y + r); lineTo(c.x - a, c.y + a)
    lineTo(c.x - r, c.y + b); lineTo(c.x - r, c.y - b); close()
}

/** Un círculo abierto, la marca de la duda en casi todos los temas (cada uno con su color y su trazo). */
internal fun DrawScope.circuloDuda(color: Color) {
    val s = size.minDimension
    drawCircle(color, s * 0.32f, center, style = Stroke(s * 0.11f))
}

/** Escuadras de metal en las cuatro esquinas de un interior, sin salirse de sus paredes de grosor [pl]. */
internal fun DrawScope.escuadrasPared(w: Float, h: Float, pl: Float, largo: Float, color: Color) {
    listOf(0f to 0f, w to 0f, 0f to h, w to h).forEach { (x, y) ->
        val sx = if (x == 0f) 0f else x - largo; val sy = if (y == 0f) 0f else y - pl
        drawRect(color, Offset(sx, sy), Size(largo, pl))
        drawRect(color, Offset(if (x == 0f) 0f else x - pl, if (y == 0f) 0f else y - largo), Size(pl, largo))
    }
}

/** Dos trazos cruzados de [color]: la marca del desacuerdo. */
internal fun DrawScope.aspa(color: Color, grosor: Float = 0.12f) {
    val s = size.minDimension; val c = center
    drawLine(color, Offset(c.x - s * 0.32f, c.y - s * 0.32f), Offset(c.x + s * 0.32f, c.y + s * 0.32f), s * grosor, StrokeCap.Round)
    drawLine(color, Offset(c.x + s * 0.32f, c.y - s * 0.32f), Offset(c.x - s * 0.32f, c.y + s * 0.32f), s * grosor, StrokeCap.Round)
}

/**
 * Medieval: el armario del scriptorium. Un mueble de roble con frontón, una arquería ciega pintada de bermellón y
 * lapislázuli en la cornisa y bandas de hierro en los costados, sobre un piso de baldosas vidriadas. Cada tema es un anaquel
 * de medio punto con su pila de folios (más alta cuanto más lleno), y el tema va en una etiqueta de pergamino clavada al
 * canto de la repisa. Por dentro, tablas de roble a la luz de una vela. Las fichas son folios de pergamino pautados en rojo,
 * con los picados del margen.
 */
object IdeasMedieval : DisenoIdeas {
    private val ROBLE = Color(0xFF6E4C2C)
    private val ROBLE_LUZ = Color(0xFF94704A)
    private val ROBLE_OSC = Color(0xFF3B2715)
    private val HUECO = Color(0xFF1E140B)
    private val HIERRO = Color(0xFF26231F)
    private val HIERRO_LUZ = Color(0xFF6A635A)
    private val PERGAMINO = Color(0xFFF1E6C8)
    private val PERG_OSC = Color(0xFFD6C397)
    private val BERMELLON = Color(0xFF9E1B1B)
    private val LAPIS = Color(0xFF1F3F8F)
    private val ORO = Color(0xFFC09A3A)
    private val TINTA = Color(0xFF2A1B10)
    private val BARRO = Color(0xFF9A4E2E)
    private val OCRE = Color(0xFFC9A464)
    private val CUERNO = Color(0xFF8A6A40)
    private val VELA = Color(0xFFF2C46B)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 0.82f
    override val corona = 0.22f
    override val base = 0.1f
    override val lado = 0.075f
    override val hueco = 0.035f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        // El piso de baldosas vidriadas, rojas y ocres, en damero.
        val piso = d.base.top + d.base.height * 0.45f
        val lt = (d.base.bottom - piso) / 2
        var x = 0f; var k = 0
        while (x < w) {
            for (f in 0 until 2) drawRect(t(if ((k + f) % 2 == 0) BARRO else OCRE, oscuro), Offset(x, piso + f * lt), Size(lt + 0.5f, lt + 0.5f))
            x += lt; k++
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, piso), Size(w, 3 * u))
        // El cuerpo de roble, de tablas verticales.
        val cuerpo = Rect(w * 0.03f, d.corona.bottom - 6 * u, w * 0.97f, piso)
        drawRect(t(ROBLE, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, ROBLE_OSC.copy(alpha = 0.4f), 3, 0.9f, vertical = true, grosor = 0.7f, ondulacion = 0.4f)
        // La moldura de cada anaquel.
        d.huecos.forEach { r ->
            val m = arcoMedioPunto(Rect(r.left - 3.5f * u, r.top - 3.5f * u, r.right + 3.5f * u, r.bottom + 2 * u))
            drawPath(m, t(ROBLE_OSC, oscuro))
            drawPath(m, t(ROBLE_LUZ, oscuro).copy(alpha = 0.6f), style = Stroke(1.2f * u))
        }
        // Las tablas de las repisas, bajo cada fila.
        d.filas.forEach { f ->
            val r = Rect(cuerpo.left, f.bottom + 1 * u, cuerpo.right, f.bottom + maxOf(d.separacion * 0.75f, 6 * u))
            drawRect(t(ROBLE_LUZ, oscuro), r.topLeft, r.size)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.bottom), Size(r.width, 2 * u))
            drawLine(Color.White.copy(alpha = 0.18f), r.topLeft, Offset(r.right, r.top), 1f * u)
        }
        // Las bandas de hierro de los costados, con sus clavos y su remate en punta de lanza.
        listOf(cuerpo.left + 2 * u, cuerpo.right - 8 * u).forEach { bx ->
            drawRect(t(HIERRO, oscuro), Offset(bx, cuerpo.top + 4 * u), Size(6 * u, cuerpo.height - 8 * u))
            drawPath(Path().apply { moveTo(bx - 2 * u, cuerpo.top + 6 * u); lineTo(bx + 3 * u, cuerpo.top - 2 * u); lineTo(bx + 8 * u, cuerpo.top + 6 * u); close() }, t(HIERRO, oscuro))
            var yy = cuerpo.top + 12 * u
            while (yy < cuerpo.bottom - 6 * u) { drawCircle(t(HIERRO_LUZ, oscuro), 1.1f * u, Offset(bx + 3 * u, yy)); yy += 16 * u }
        }
        // El frontón: un gablete de roble con crochets en las vertientes, un remate dorado y un medallón de lapislázuli.
        val ch = d.corona.height
        val cornisa = Rect(0f, d.corona.top + ch * 0.45f, w, d.corona.bottom)
        val cima = Offset(w / 2, d.corona.top + ch * 0.06f)
        val izq = Offset(w * 0.08f, cornisa.top + 1 * u); val der = Offset(w * 0.92f, cornisa.top + 1 * u)
        val fronton = Path().apply { moveTo(izq.x, izq.y); lineTo(cima.x, cima.y); lineTo(der.x, der.y); close() }
        drawPath(fronton, t(ROBLE, oscuro))
        clipPath(fronton) { vetas(Rect(izq.x, cima.y, der.x, izq.y), ROBLE_OSC.copy(alpha = 0.35f), 7, 0.7f, grosor = 0.6f, ondulacion = 0.4f) }
        drawPath(fronton, t(ROBLE_OSC, oscuro), style = Stroke(2.2f * u))
        for (j in 1..5) {
            val f = j / 6f
            listOf(izq, der).forEach { a ->
                val p = Offset(a.x + (cima.x - a.x) * f, a.y + (cima.y - a.y) * f)
                drawCircle(t(ROBLE_LUZ, oscuro), 2.6f * u, Offset(p.x, p.y - 2.5f * u))
                drawCircle(t(ROBLE_OSC, oscuro), 2.6f * u, Offset(p.x, p.y - 2.5f * u), style = Stroke(0.7f * u))
            }
        }
        drawCircle(t(ORO, oscuro), 4 * u, Offset(cima.x, cima.y - 3 * u))
        val med = Offset(w / 2, cornisa.top - (cornisa.top - cima.y) * 0.4f)
        val rm = (cornisa.top - cima.y) * 0.25f
        drawCircle(t(LAPIS, oscuro), rm, med)
        drawPath(estrella(med, rm * 0.72f, 8, 0.45f), t(ORO, oscuro))
        drawCircle(t(ORO, oscuro), rm, med, style = Stroke(1.6f * u))
        // La cornisa, con su arquería ciega pintada de bermellón y lapislázuli.
        drawRect(t(ROBLE_LUZ, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.top), Size(w, 2.5f * u))
        drawRect(t(ROBLE_OSC, oscuro), Offset(0f, cornisa.bottom - 3 * u), Size(w, 3 * u))
        val n = 11
        val aw = w * 0.9f / n
        for (j in 0 until n) {
            val r = Rect(w * 0.05f + aw * j + aw * 0.14f, cornisa.top + cornisa.height * 0.22f, w * 0.05f + aw * (j + 1) - aw * 0.14f, cornisa.bottom - cornisa.height * 0.22f)
            drawPath(arcoMedioPunto(r), t(if (j % 2 == 0) BERMELLON else LAPIS, oscuro))
            drawCircle(t(ORO, oscuro), 1.2f * u, Offset(r.center.x, r.top + r.width * 0.5f + (r.height - r.width * 0.5f) * 0.4f))
            drawPath(arcoMedioPunto(r), t(ROBLE_OSC, oscuro), style = Stroke(1f * u))
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PERGAMINO, oscuro), r.topLeft, r.size)
            drawRect(t(ROBLE_OSC, oscuro), r.topLeft, r.size, style = Stroke(1f * u))
            listOf(r.left + 4 * u, r.right - 4 * u).forEach { xx -> drawCircle(HIERRO, 1.6f * u, Offset(xx, r.center.y)) }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El hueco del anaquel: un medio punto oscuro, con las tablas del fondo y la luz de una vela.
        val nicho = arcoMedioPunto(Rect(0f, 0f, w, h))
        drawPath(nicho, t(HUECO, oscuro))
        clipPath(nicho) {
            for (k in 1 until 4) drawLine(Color.Black.copy(alpha = 0.45f), Offset(w * k / 4, 0f), Offset(w * k / 4, h), 1f * u)
            mancha(Offset(w * 0.3f, h * 0.35f), w * 0.65f, VELA.copy(alpha = if (oscuro) 0.08f else 0.13f))
        }
        val repisa = h * 0.6f
        val tabla = h * 0.045f
        if (c.vacio) {
            // Un lugar libre: un tintero de cuerno y una pluma de ganso.
            val tin = Rect(w * 0.34f, repisa - h * 0.1f, w * 0.52f, repisa)
            drawRoundRect(t(CUERNO, oscuro), tin.topLeft, tin.size, CornerRadius(3 * u))
            drawOval(Color.Black, Offset(tin.left + 2 * u, tin.top - 1.5f * u), Size(tin.width - 4 * u, 3 * u))
            val pluma = Path().apply { moveTo(tin.center.x, tin.top); quadraticTo(w * 0.6f, repisa - h * 0.3f, w * 0.74f, repisa - h * 0.46f) }
            drawPath(pluma, t(PERGAMINO, oscuro), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            for (j in 0 until 6) {
                val f = 0.45f + j * 0.09f
                val p = Offset(tin.center.x + (w * 0.74f - tin.center.x) * f, tin.top + (repisa - h * 0.46f - tin.top) * f)
                drawLine(t(PERG_OSC, oscuro), p, Offset(p.x - 6 * u, p.y + 1.5f * u), 1.6f * u, StrokeCap.Round)
                drawLine(t(PERG_OSC, oscuro), p, Offset(p.x + 3 * u, p.y + 4 * u), 1.6f * u, StrokeCap.Round)
            }
        } else {
            // La pila de folios, más alta cuanto más lleno; con muchos, un códice de cuero rojo encima.
            val hojas = c.n.coerceIn(1, 9)
            val g = h * 0.024f
            for (i in 0 until hojas) {
                val y = repisa - (i + 1) * g
                val dx = (azar(c.semilla, i) - 0.5f) * w * 0.06f
                drawRect(t(if (i % 2 == 0) PERGAMINO else PERG_OSC, oscuro), Offset(w * 0.14f + dx, y), Size(w * 0.72f, g - 0.7f * u))
            }
            if (c.n >= 5) {
                val tope = repisa - hojas * g
                val libro = Rect(w * 0.2f, tope - h * 0.075f, w * 0.78f, tope)
                drawRect(t(mezcla(BERMELLON, Color.Black, 0.25f), oscuro), libro.topLeft, libro.size)
                drawRect(t(PERGAMINO, oscuro), Offset(libro.left + 2 * u, libro.bottom - 2.5f * u), Size(libro.width - 4 * u, 2 * u))
                drawRect(t(ORO, oscuro), Offset(libro.right - 7 * u, libro.top + libro.height * 0.25f), Size(7 * u, libro.height * 0.4f))
            }
        }
        // La repisa.
        drawRect(t(ROBLE_LUZ, oscuro), Offset(0f, repisa), Size(w, tabla))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, repisa + tabla), Size(w, 1.5f * u))
        if (c.vacio) return
        // Al abrirse, el folio de arriba se desliza hacia afuera.
        if (ab > 0f) {
            val y = repisa - h * 0.05f + ab * h * 0.12f
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.18f, y + 2 * u), Size(w * 0.68f, h * 0.07f))
            drawRect(t(PERGAMINO, oscuro), Offset(w * 0.16f, y), Size(w * 0.68f, h * 0.07f))
            drawLine(BERMELLON.copy(alpha = 0.6f), Offset(w * 0.2f, y + h * 0.02f), Offset(w * 0.8f, y + h * 0.02f), 0.8f * u)
        }
        // La etiqueta de pergamino clavada al canto de la repisa, pautada en rojo.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val et = contornoIrregular(r, c.semilla, 14, 0.04f)
        translate(1.5f * u, 2 * u) { drawPath(et, Color.Black.copy(alpha = 0.35f)) }
        drawPath(et, t(PERGAMINO, oscuro))
        drawLine(BERMELLON.copy(alpha = 0.5f), Offset(r.left + 6 * u, r.top + 4.5f * u), Offset(r.right - 6 * u, r.top + 4.5f * u), 0.8f * u)
        drawLine(BERMELLON.copy(alpha = 0.5f), Offset(r.left + 6 * u, r.bottom - 4 * u), Offset(r.right - 6 * u, r.bottom - 4 * u), 0.8f * u)
        listOf(r.left + 5 * u, r.right - 5 * u).forEach { x ->
            drawCircle(t(HIERRO, oscuro), 1.9f * u, Offset(x, r.top + 1.5f * u))
            drawCircle(HIERRO_LUZ, 0.6f * u, Offset(x - 0.5f * u, r.top + 1 * u))
        }
    }

    override val rotulo = Rect(0.07f, 0.68f, 0.93f, 0.96f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.cardo
    override val salida = 0f
    override val placa = Rect(0.3f, 0.04f, 0.7f, 0.42f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Tablas de roble a lo largo, con sus clavos, y la luz de una vela arriba a la izquierda.
        drawRect(t(ROBLE_OSC, oscuro))
        val alto = 44 * u
        var y = 0f; var k = 0
        while (y < h) {
            val r = Rect(0f, y, w, minOf(y + alto, h))
            drawRect(t(mezcla(ROBLE_OSC, ROBLE, 0.35f + 0.3f * azar(51, k)), oscuro), r.topLeft, r.size)
            vetas(r, Color.Black.copy(alpha = 0.3f), 60 + k, 0.7f, grosor = 0.6f, ondulacion = 0.4f)
            drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, r.top), Offset(w, r.top), 1.2f * u)
            listOf(22 * u, w - 22 * u).forEach { xx -> drawCircle(t(HIERRO, oscuro), 1.7f * u, Offset(xx, r.center.y)) }
            y += alto; k++
        }
        mancha(Offset(w * 0.2f, h * 0.12f), w * 0.75f, VELA.copy(alpha = if (oscuro) 0.06f else 0.1f))
        // Las bandas de hierro del borde, remachadas.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(HIERRO, oscuro), r.topLeft, r.size) }
        var p = pl * 2
        while (p < w - pl) { drawCircle(t(HIERRO_LUZ, oscuro), 1.3f * u, Offset(p, pl / 2)); drawCircle(t(HIERRO_LUZ, oscuro), 1.3f * u, Offset(p, h - pl / 2)); p += 20 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 20 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PERGAMINO
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 28, 0.006f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(PERGAMINO.copy(alpha = 0.35f))
        // El lado del pelo, más amarillo hacia los bordes.
        val b = 6 * u * g
        drawRect(PERG_OSC.copy(alpha = 0.22f), Offset.Zero, Size(w, b))
        drawRect(PERG_OSC.copy(alpha = 0.22f), Offset(0f, h - b), Size(w, b))
        drawRect(PERG_OSC.copy(alpha = 0.18f), Offset.Zero, Size(b, h))
        drawRect(PERG_OSC.copy(alpha = 0.18f), Offset(w - b, 0f), Size(b, h))
        // El pautado del margen: a la izquierda en rojo y azul alternados (un filete de pluma), a la derecha en rojo.
        val m = 7 * u * g
        val tramo = 14 * u * g
        var y = 0f; var k = 0
        while (y < h) { drawLine((if (k % 2 == 0) BERMELLON else LAPIS).copy(alpha = 0.4f), Offset(m, y), Offset(m, minOf(y + tramo, h)), 1f * u * g); y += tramo; k++ }
        drawLine(BERMELLON.copy(alpha = 0.3f), Offset(w - m, 0f), Offset(w - m, h), 0.7f * u)
        // Los picados del margen, para el pautado.
        y = 10 * u * g
        while (y < h - 6 * u) { drawCircle(TINTA.copy(alpha = 0.3f), 0.7f * u * g, Offset(w - 3 * u * g, y)); y += 9 * u * g }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 12.dp, 24.dp, 28.dp, 28.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.68f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El calderón rojo (¶) de los rubricadores.
            "convence" -> {
                drawCircle(BERMELLON, s * 0.19f, Offset(c.x - s * 0.1f, c.y - s * 0.16f))
                drawLine(BERMELLON, Offset(c.x + s * 0.04f, c.y - s * 0.36f), Offset(c.x + s * 0.04f, c.y + s * 0.4f), s * 0.09f)
                drawLine(BERMELLON, Offset(c.x + s * 0.22f, c.y - s * 0.36f), Offset(c.x + s * 0.22f, c.y + s * 0.4f), s * 0.09f)
                drawLine(BERMELLON, Offset(c.x - s * 0.1f, c.y - s * 0.36f), Offset(c.x + s * 0.28f, c.y - s * 0.36f), s * 0.09f)
            }
            // Un trébol azul.
            "piensa" -> {
                for (k in 0 until 3) {
                    val a = Math.toRadians(-90.0 + k * 120.0)
                    drawCircle(LAPIS, s * 0.17f, Offset(c.x + s * 0.18f * cos(a).toFloat(), c.y - s * 0.04f + s * 0.18f * sin(a).toFloat()))
                }
                drawLine(LAPIS, Offset(c.x, c.y + s * 0.1f), Offset(c.x, c.y + s * 0.44f), s * 0.08f)
            }
            // El óbelo (÷): así marcaban los copistas un pasaje dudoso.
            "duda" -> {
                drawLine(p.tenue, Offset(c.x - s * 0.36f, c.y), Offset(c.x + s * 0.36f, c.y), s * 0.1f, StrokeCap.Round)
                drawCircle(p.tenue, s * 0.08f, Offset(c.x, c.y - s * 0.25f)); drawCircle(p.tenue, s * 0.08f, Offset(c.x, c.y + s * 0.25f))
            }
            "desacuerdo" -> aspa(BERMELLON)
            else -> {}
        }
    }

    /** Un broche de libro: la correa de cuero y su cierre de latón. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(ROBLE_OSC, Offset(c.x - s * 0.46f, c.y - s * 0.14f), Size(s * 0.56f, s * 0.28f))
        drawRoundRect(ORO, Offset(c.x - s * 0.02f, c.y - s * 0.24f), Size(s * 0.46f, s * 0.48f), CornerRadius(s * 0.1f))
        drawCircle(ROBLE_OSC, s * 0.07f, Offset(c.x + s * 0.21f, c.y))
    }

    override val desorden = 1f
}

/**
 * Templario: la cámara del tesoro de la encomienda. Un muro de sillares de caliza con un gran arco apuntado de dovelas, el
 * escudo blanco de la cruz roja en la clave, dos gonfalones bicolores (el «baucent», negro y blanco) y dos antorchas. Cada
 * tema es un arca de roble con flejes de hierro y dos candados, la cruz patada en la tapa y el tema en una tabla blanca de
 * yeso; asoman cartas lacradas bajo la tapa. Por dentro, un paño de lana blanca con la cruz. Las fichas son cartas selladas
 * con su sello de cera pendiente.
 */
object IdeasTemplario : DisenoIdeas {
    private val PIEDRA = Color(0xFFC9BC9E)
    private val PIEDRA_LUZ = Color(0xFFE0D5BC)
    private val PIEDRA_OSC = Color(0xFF8E8168)
    private val JUNTA = Color(0xFF6A604E)
    private val ROBLE = Color(0xFF4E3420)
    private val ROBLE_LUZ = Color(0xFF6E4C30)
    private val ROBLE_OSC = Color(0xFF2C1C10)
    private val HIERRO = Color(0xFF1F1E1D)
    private val HIERRO_LUZ = Color(0xFF5E5A54)
    private val ROJO = Color(0xFFB0191F)
    private val BLANCO = Color(0xFFF4EFE4)
    private val NEGRO = Color(0xFF1C1A18)
    private val LINO = Color(0xFFECE4D0)
    private val CERA = Color(0xFF8E1A16)
    private val TINTA = Color(0xFF201E1B)
    private val FUEGO = Color(0xFFF2B65A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.32f) else c

    override val cols = 2
    override val proporcion = 1.5f
    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.09f
    override val hueco = 0.06f

    /** Un escudo de punta: arriba recto, abajo en punta. */
    private fun escudo(c: Offset, a: Float, h: Float) = Path().apply {
        moveTo(c.x - a / 2, c.y - h / 2); lineTo(c.x + a / 2, c.y - h / 2); lineTo(c.x + a / 2, c.y)
        quadraticTo(c.x + a / 2, c.y + h * 0.35f, c.x, c.y + h / 2); quadraticTo(c.x - a / 2, c.y + h * 0.35f, c.x - a / 2, c.y); close()
    }

    /** Sillares en [r]: hiladas de [alto] con juntas alternadas, cada piedra con su tono. */
    private fun DrawScope.sillares(r: Rect, alto: Float, claro: Color, osc: Color, semilla: Int, oscuro: Boolean) {
        val u = density
        drawRect(t(JUNTA, oscuro), r.topLeft, r.size)
        var y = r.top; var hilada = 0
        while (y < r.bottom) {
            var x = r.left - (if (hilada % 2 == 0) 0f else alto * 1.1f)
            var k = 0
            while (x < r.right) {
                val largo = alto * (1.8f + 0.9f * azar(semilla + hilada, k))
                drawRect(t(mezcla(osc, claro, 0.45f + 0.4f * azar(semilla + 100 + hilada, k)), oscuro), Offset(x + 1 * u, y + 1 * u), Size(largo - 2 * u, minOf(alto, r.bottom - y) - 2 * u))
                x += largo; k++
            }
            y += alto; hilada++
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.4f
        // El muro de caliza, y dentro del arco el mismo muro en la penumbra.
        sillares(Rect(0f, 0f, w, piso), 30 * u, PIEDRA_LUZ, PIEDRA_OSC, 3, oscuro)
        val ext = arcoApuntado(Rect(w * 0.015f, d.corona.top + ch * 0.02f, w * 0.985f, piso), ch * 0.95f)
        val int = arcoApuntado(Rect(w * 0.055f, d.corona.top + ch * 0.13f, w * 0.945f, piso), ch * 0.84f)
        clipPath(int) { sillares(Rect(0f, 0f, w, piso), 30 * u, PIEDRA, mezcla(PIEDRA_OSC, Color.Black, 0.3f), 9, oscuro) }
        clipPath(int) { drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), d.corona.top, d.corona.bottom + ch * 0.3f), Offset(0f, d.corona.top), Size(w, ch * 1.3f)) }
        // El arco de dovelas: juntas radiales en la curva y horizontales en las jambas.
        val anillo = Path().apply { op(ext, int, PathOperation.Difference) }
        drawPath(anillo, t(PIEDRA_LUZ, oscuro))
        clipPath(anillo) {
            val centro = Offset(w / 2, d.corona.bottom + ch * 0.2f)
            clipRect(0f, 0f, w, d.corona.bottom) {
                for (k in 0..18) {
                    val a = Math.toRadians(180.0 + k * 10.0)
                    drawLine(t(JUNTA, oscuro), centro, Offset(centro.x + w * cos(a).toFloat(), centro.y + w * sin(a).toFloat()), 1.2f * u)
                }
            }
            var y = d.corona.bottom
            while (y < piso) { drawLine(t(JUNTA, oscuro), Offset(0f, y), Offset(w, y), 1.2f * u); y += 30 * u }
        }
        drawPath(int, Color.Black.copy(alpha = 0.35f), style = Stroke(1.5f * u))
        // La clave con el escudo blanco de la cruz roja.
        val esc = Offset(w / 2, d.corona.top + ch * 0.42f)
        val ea = ch * 0.26f; val eh = ch * 0.32f
        drawPath(escudo(Offset(esc.x + 2 * u, esc.y + 2 * u), ea, eh), Color.Black.copy(alpha = 0.3f))
        drawPath(escudo(esc, ea, eh), t(BLANCO, oscuro))
        drawRect(t(ROJO, oscuro), Offset(esc.x - ea * 0.08f, esc.y - eh / 2), Size(ea * 0.16f, eh * 0.9f))
        drawRect(t(ROJO, oscuro), Offset(esc.x - ea / 2, esc.y - eh * 0.2f), Size(ea, eh * 0.14f))
        drawPath(escudo(esc, ea, eh), t(HIERRO, oscuro), style = Stroke(1.2f * u))
        // Los gonfalones bicolores, colgando de su asta, y las antorchas en las jambas.
        listOf(-1f, 1f).forEach { s ->
            val x = w / 2 + s * w * 0.24f
            val y0 = d.corona.top + ch * 0.36f
            val gw = w * 0.07f; val gh = ch * 0.5f
            drawLine(t(HIERRO, oscuro), Offset(x - gw * 0.7f, y0), Offset(x + gw * 0.7f, y0), 1.6f * u)
            drawRect(t(NEGRO, oscuro), Offset(x - gw / 2, y0), Size(gw, gh * 0.48f))
            drawPath(Path().apply { moveTo(x - gw / 2, y0 + gh * 0.48f); lineTo(x + gw / 2, y0 + gh * 0.48f); lineTo(x + gw / 2, y0 + gh); lineTo(x, y0 + gh * 0.8f); lineTo(x - gw / 2, y0 + gh); close() }, t(BLANCO, oscuro))
            drawRect(t(ROJO, oscuro), Offset(x - gw * 0.08f, y0 + gh * 0.52f), Size(gw * 0.16f, gh * 0.22f))
            drawRect(t(ROJO, oscuro), Offset(x - gw * 0.24f, y0 + gh * 0.58f), Size(gw * 0.48f, gh * 0.07f))
            val tx = if (s < 0) w * 0.085f else w * 0.915f
            val ty = d.corona.bottom - ch * 0.05f
            mancha(Offset(tx, ty - ch * 0.12f), ch * (if (oscuro) 0.7f else 0.5f), FUEGO.copy(alpha = if (oscuro) 0.35f else 0.25f))
            drawLine(t(HIERRO, oscuro), Offset(tx, ty), Offset(tx, ty + ch * 0.18f), 2.4f * u)
            drawPath(Path().apply { moveTo(tx - 5 * u, ty); lineTo(tx + 5 * u, ty); lineTo(tx + 3 * u, ty + 6 * u); lineTo(tx - 3 * u, ty + 6 * u); close() }, t(HIERRO, oscuro))
            drawOval(FUEGO, Offset(tx - 4 * u, ty - 12 * u), Size(8 * u, 13 * u))
            drawOval(Color(0xFFFFF0C0), Offset(tx - 2 * u, ty - 7 * u), Size(4 * u, 7 * u))
        }
        // Los poyos de piedra bajo cada fila.
        d.filas.forEach { f ->
            val r = Rect(w * 0.07f, f.bottom - 2 * u, w * 0.93f, f.bottom + maxOf(d.separacion * 0.55f, 8 * u))
            drawRect(t(PIEDRA_LUZ, oscuro), r.topLeft, r.size)
            drawLine(Color.White.copy(alpha = 0.35f), r.topLeft, Offset(r.right, r.top), 1f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El piso de losas.
        drawRect(t(PIEDRA_OSC, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, piso), Offset(w, piso), 2 * u)
        for (j in 0 until 6) drawLine(t(JUNTA, oscuro), Offset(w * (j + 0.5f) / 6, piso), Offset(w * (j + 0.5f) / 6 + (j - 2.5f) * 8 * u, d.base.bottom), 1f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PIEDRA_LUZ, oscuro), r.topLeft, r.size)
            drawRect(t(JUNTA, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un cirio en su candelero de hierro.
            val x = w / 2; val pie = h * 0.95f
            mancha(Offset(x, h * 0.3f), h * 0.4f, FUEGO.copy(alpha = if (oscuro) 0.3f else 0.2f))
            drawLine(t(HIERRO, oscuro), Offset(x - w * 0.08f, pie), Offset(x + w * 0.08f, pie), 2 * u)
            drawLine(t(HIERRO, oscuro), Offset(x, pie), Offset(x, h * 0.7f), 2.4f * u)
            drawRect(t(HIERRO, oscuro), Offset(x - w * 0.05f, h * 0.68f), Size(w * 0.1f, 3 * u))
            drawRect(t(BLANCO, oscuro), Offset(x - w * 0.025f, h * 0.36f), Size(w * 0.05f, h * 0.32f))
            drawOval(FUEGO, Offset(x - 3 * u, h * 0.36f - 11 * u), Size(6 * u, 11 * u))
            return
        }
        val cuerpo = Rect(w * 0.08f, h * 0.38f, w * 0.92f, h * 0.95f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Size(cuerpo.width, h * 0.06f))
        // Las cartas lacradas que asoman bajo la tapa.
        if (sube > 1f) {
            drawRect(Color(0xFF120C08), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.08f + 0.14f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRect(t(LINO, oscuro), Offset(x, y), Size(cuerpo.width * 0.12f, cuerpo.top - y + 2 * u))
                drawCircle(t(CERA, oscuro), 2.2f * u, Offset(x + cuerpo.width * 0.06f, y + 3.5f * u))
            }
        }
        // El arca de roble, con las juntas de sus tablas.
        drawRect(t(ROBLE, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, ROBLE_OSC.copy(alpha = 0.45f), c.semilla, 1f, grosor = 0.7f, ondulacion = 0.4f)
        for (j in 1 until 3) drawLine(Color.Black.copy(alpha = 0.4f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * j / 3), Offset(cuerpo.right, cuerpo.top + cuerpo.height * j / 3), 1f * u)
        // La tapa plana, con la cruz patada sobre un disco blanco.
        val tapa = Rect(w * 0.05f, cuerpo.top - sube - h * 0.13f, w * 0.95f, cuerpo.top - sube)
        drawRect(t(ROBLE_LUZ, oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(tapa.left, tapa.bottom - 2 * u), Size(tapa.width, 2 * u))
        drawCircle(t(BLANCO, oscuro), tapa.height * 0.4f, tapa.center)
        drawPath(cruzPatada(tapa.center, tapa.height * 0.32f), t(ROJO, oscuro))
        // Los flejes de hierro, de la tapa al pie, con sus remaches, y el del pie.
        listOf(0.15f, 0.85f).forEach { fx ->
            val x = w * fx
            drawRect(t(HIERRO, oscuro), Offset(x - 3 * u, tapa.top), Size(6 * u, tapa.height))
            drawRect(t(HIERRO, oscuro), Offset(x - 3 * u, cuerpo.top), Size(6 * u, cuerpo.height))
            var yy = cuerpo.top + 5 * u
            while (yy < cuerpo.bottom - 3 * u) { drawCircle(t(HIERRO_LUZ, oscuro), 1.2f * u, Offset(x, yy)); yy += 10 * u }
        }
        drawRect(t(HIERRO, oscuro), Offset(cuerpo.left, cuerpo.bottom - 5 * u), Size(cuerpo.width, 5 * u))
        // Los dos candados, colgando de sus aldabas.
        listOf(0.33f, 0.67f).forEach { fx ->
            val x = w * fx
            drawRect(t(HIERRO, oscuro), Offset(x - 2.5f * u, tapa.bottom - 3 * u), Size(5 * u, h * 0.08f))
            val cand = Offset(x, tapa.bottom + h * 0.1f)
            drawArc(t(HIERRO, oscuro), 180f, 180f, false, Offset(cand.x - 4 * u, cand.y - 8 * u), Size(8 * u, 8 * u), style = Stroke(1.6f * u))
            drawRoundRect(t(HIERRO, oscuro), Offset(cand.x - 5 * u, cand.y - 4 * u), Size(10 * u, 8 * u), CornerRadius(1.5f * u))
            drawCircle(t(HIERRO_LUZ, oscuro), 1 * u, cand)
        }
        // La tabla de yeso blanco con el tema, filetada de rojo.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(t(BLANCO, oscuro), r.topLeft, r.size)
        drawRect(t(ROJO, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
    }

    override val rotulo = Rect(0.22f, 0.56f, 0.78f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.cruzada
    override val salida = 0f
    override val placa = Rect(0.32f, 0.47f, 0.68f, 0.95f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un paño de lana blanca, con su trama, y la cruz patada grande y tenue.
        val pano = if (oscuro) mezcla(LINO, Color.Black, 0.6f) else LINO
        drawRect(pano)
        var y = 0f
        while (y < h) { drawLine(Color.Black.copy(alpha = 0.04f), Offset(0f, y), Offset(w, y), 0.8f * u); y += 3 * u }
        var x = 0f
        while (x < w) { drawLine(Color.Black.copy(alpha = 0.03f), Offset(x, 0f), Offset(x, h), 0.8f * u); x += 3 * u }
        drawPath(cruzPatada(Offset(w / 2, h * 0.42f), w * 0.36f), ROJO.copy(alpha = if (oscuro) 0.16f else 0.1f))
        // Las paredes del arca: tablas de roble con sus escuadras de hierro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(ROBLE, oscuro), r.topLeft, r.size)
            vetas(r, ROBLE_OSC.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        escuadrasPared(w, h, pl, 46 * u, t(HIERRO, oscuro))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) LINO else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(LINO.copy(alpha = 0.4f))
        // Los dobleces de la carta, en tercios.
        listOf(h / 3, h * 2 / 3).forEach { y ->
            drawLine(Color.Black.copy(alpha = 0.07f), Offset(0f, y), Offset(w, y), 1f * u)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, y + 1 * u), Offset(w, y + 1 * u), 1f * u)
        }
        // El sello de cera pendiente, con su cordón y la cruz.
        val sello = Offset(w / 2, h - 10 * u * g)
        val ranura = h - 26 * u * g
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(w / 2 - 4 * u * g, ranura), Offset(w / 2 + 4 * u * g, ranura), 1.2f * u)
        drawLine(ROJO.copy(alpha = 0.8f), Offset(w / 2 - 2 * u * g, ranura), Offset(sello.x - 2 * u * g, sello.y), 1.2f * u * g)
        drawLine(ROJO.copy(alpha = 0.8f), Offset(w / 2 + 2 * u * g, ranura), Offset(sello.x + 2 * u * g, sello.y), 1.2f * u * g)
        drawCircle(CERA, 7.5f * u * g, sello)
        drawCircle(mezcla(CERA, Color.Black, 0.35f), 6 * u * g, sello, style = Stroke(0.9f * u * g))
        drawPath(cruzPatada(sello, 3.8f * u * g), mezcla(CERA, Color.Black, 0.35f))
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 28.dp, 22.dp, 28.dp, 48.dp)
    override val proporcionFicha = 0.88f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> drawPath(cruzPatada(c, s * 0.44f), ROJO)
            // El baucent: el escudo partido, negro arriba y blanco abajo.
            "piensa" -> {
                val e = escudo(c, s * 0.7f, s * 0.86f)
                clipPath(e) { drawRect(NEGRO, Offset(0f, 0f), Size(size.width, c.y)); drawRect(BLANCO, Offset(0f, c.y), Size(size.width, size.height)) }
                drawPath(e, NEGRO, style = Stroke(s * 0.06f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** El sello de cera roja, con la cruz. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(CERA, s * 0.44f, c)
        drawCircle(mezcla(CERA, Color.Black, 0.35f), s * 0.36f, c, style = Stroke(s * 0.06f))
        drawPath(cruzPatada(c, s * 0.24f), mezcla(CERA, Color.Black, 0.35f))
    }

    override val desorden = 0f
}

/**
 * Ruta de la Seda: el estante de un caravasar. Un muro de ladrillo con una banda de azulejos turquesa y un gran arco
 * apuntado bordeado de azulejo; dentro, en la penumbra, repisas de madera y una lámpara de latón colgada; al pie, un kilim.
 * Cada tema es una caja de laca roja de cinabrio con volutas talladas y filetes de oro; asoman tiras de bambú bajo la tapa,
 * y el tema va en una tira de papel pegada al frente. Por dentro, laca negra con nubes de oro. Las fichas son tiras de bambú
 * atadas con cordel.
 */
object IdeasSeda : DisenoIdeas {
    private val LADRILLO = Color(0xFFC49A6C)
    private val LADRILLO_OSC = Color(0xFF9C7048)
    private val MORTERO = Color(0xFFE6D6B8)
    private val TURQUESA = Color(0xFF1A8C8C)
    private val TURQ_LUZ = Color(0xFF5FC0B8)
    private val LAPIS = Color(0xFF23408E)
    private val MADERA = Color(0xFF6B4528)
    private val MADERA_OSC = Color(0xFF3E2614)
    private val LACA = Color(0xFFA8261C)
    private val LACA_OSC = Color(0xFF6A1410)
    private val LACA_NEGRA = Color(0xFF1C1414)
    private val ORO = Color(0xFFD0A84A)
    private val BAMBU = Color(0xFFD9C38E)
    private val BAMBU_OSC = Color(0xFFA88A50)
    private val PAPEL = Color(0xFFF6EFDC)
    private val TINTA = Color(0xFF1F1B3A)
    private val CELADON = Color(0xFFA8C8B0)
    private val CUERDA = Color(0xFFC9A86A)
    private val BAMBU_VIEJO = Color(0xFF4E3A22)
    private val KILIM_ROJO = Color(0xFF9E2A22)
    private val KILIM_AZUL = Color(0xFF23305E)
    private val KILIM_OCRE = Color(0xFFD39A3A)
    private val KILIM_CREMA = Color(0xFFEFE2C4)
    private val LUZ = Color(0xFFF2C46B)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.32f
    override val base = 0.13f
    override val lado = 0.11f
    override val hueco = 0.06f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.25f
        // El muro de ladrillo, hiladas alternadas.
        drawRect(t(MORTERO, oscuro), Offset.Zero, Size(w, piso))
        val la = 11 * u; val ll = 28 * u
        var y = 0f; var hilada = 0
        while (y < piso) {
            var x = if (hilada % 2 == 0) 0f else -ll / 2
            var k = 0
            while (x < w) {
                drawRect(t(mezcla(LADRILLO_OSC, LADRILLO, 0.4f + 0.6f * azar(hilada, k)), oscuro), Offset(x + 1 * u, y + 1 * u), Size(ll - 2 * u, la - 2 * u))
                x += ll; k++
            }
            y += la; hilada++
        }
        // La banda de azulejos de arriba: turquesa con rombos de lapislázuli y blanco.
        val banda = Rect(0f, d.corona.top, w, d.corona.top + ch * 0.2f)
        drawRect(t(TURQUESA, oscuro), banda.topLeft, banda.size)
        val paso = banda.height
        var bx = paso / 2; var j = 0
        while (bx < w) {
            val c = Offset(bx, banda.center.y)
            drawPath(estrella(c, paso * 0.4f, 4, 0.45f, 0f), t(if (j % 2 == 0) LAPIS else PAPEL, oscuro))
            bx += paso; j++
        }
        drawRect(t(LAPIS, oscuro), Offset(0f, banda.bottom), Size(w, 2.5f * u))
        // El gran arco apuntado: el borde de azulejo y, dentro, el ladrillo en penumbra.
        val vano = Rect(w * 0.06f, d.corona.top + ch * 0.32f, w * 0.94f, piso)
        val borde = arcoApuntado(Rect(vano.left - 7 * u, vano.top - 7 * u, vano.right + 7 * u, piso), ch * 0.68f + 7 * u)
        val arco = arcoApuntado(vano, ch * 0.68f)
        drawPath(borde, t(TURQUESA, oscuro))
        drawPath(borde, t(TURQ_LUZ, oscuro), style = Stroke(1.5f * u))
        drawPath(arco, t(mezcla(LADRILLO_OSC, Color.Black, 0.5f), oscuro))
        clipPath(arco) {
            y = 0f; hilada = 0
            while (y < piso) {
                var x = if (hilada % 2 == 0) 0f else -ll / 2
                while (x < w) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, y), Offset(x, y + la), 1f * u); x += ll }
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), 1f * u)
                y += la; hilada++
            }
            // La lámpara de latón colgada de la clave, y su luz.
            val lx = w / 2; val ly = vano.top + ch * 0.5f
            mancha(Offset(lx, ly), ch * (if (oscuro) 1.1f else 0.85f), LUZ.copy(alpha = if (oscuro) 0.3f else 0.22f))
            drawLine(t(ORO, oscuro), Offset(lx, vano.top), Offset(lx, ly - ch * 0.08f), 1 * u)
            drawPath(Path().apply { moveTo(lx - ch * 0.1f, ly - ch * 0.08f); lineTo(lx + ch * 0.1f, ly - ch * 0.08f); quadraticTo(lx + ch * 0.08f, ly + ch * 0.06f, lx, ly + ch * 0.08f); quadraticTo(lx - ch * 0.08f, ly + ch * 0.06f, lx - ch * 0.1f, ly - ch * 0.08f); close() }, t(ORO, oscuro))
            for (k in 0 until 5) drawCircle(LUZ, 1.2f * u, Offset(lx - ch * 0.06f + k * ch * 0.03f, ly - ch * 0.01f))
            // Las repisas de madera, con sus ménsulas talladas.
            d.filas.forEach { f ->
                val r = Rect(0f, f.bottom - 1 * u, w, f.bottom + maxOf(d.separacion * 0.45f, 7 * u))
                drawRect(t(MADERA, oscuro), r.topLeft, r.size)
                drawLine(Color.White.copy(alpha = 0.15f), r.topLeft, Offset(r.right, r.top), 1f * u)
                drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.bottom), Size(r.width, 2.5f * u))
                listOf(w * 0.1f, w * 0.5f, w * 0.9f).forEach { mx ->
                    drawPath(Path().apply { moveTo(mx - 6 * u, r.bottom); lineTo(mx + 6 * u, r.bottom); quadraticTo(mx + 5 * u, r.bottom + 8 * u, mx, r.bottom + 12 * u); quadraticTo(mx - 5 * u, r.bottom + 8 * u, mx - 6 * u, r.bottom); close() }, t(MADERA_OSC, oscuro))
                }
            }
        }
        // El kilim: rojo, con su cenefa de franjas y rombos azules y ocres con ganchos.
        val kilim = Rect(0f, piso, w, d.base.bottom)
        drawRect(t(KILIM_ROJO, oscuro), kilim.topLeft, kilim.size)
        drawRect(t(KILIM_CREMA, oscuro), Offset(0f, kilim.top + 2 * u), Size(w, 2 * u))
        drawRect(t(KILIM_CREMA, oscuro), Offset(0f, kilim.bottom - 4 * u), Size(w, 2 * u))
        val rh = kilim.height * 0.32f
        var rx = rh * 1.2f; var q = 0
        while (rx < w) {
            val c = Offset(rx, kilim.center.y)
            drawPath(estrella(c, rh, 4, 0.6f, 0f), t(if (q % 2 == 0) KILIM_AZUL else KILIM_OCRE, oscuro))
            drawPath(estrella(c, rh * 0.45f, 4, 0.6f, 0f), t(KILIM_CREMA, oscuro))
            rx += rh * 2.6f; q++
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(LACA, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un jarrón de celadón.
            val x = w / 2; val b = h * 0.95f; val a = h * 0.5f
            val jarron = Path().apply {
                moveTo(x - w * 0.06f, b); cubicTo(x - w * 0.2f, b - a * 0.3f, x - w * 0.16f, b - a * 0.75f, x - w * 0.04f, b - a * 0.85f)
                lineTo(x - w * 0.06f, b - a); lineTo(x + w * 0.06f, b - a); lineTo(x + w * 0.04f, b - a * 0.85f)
                cubicTo(x + w * 0.16f, b - a * 0.75f, x + w * 0.2f, b - a * 0.3f, x + w * 0.06f, b); close()
            }
            drawPath(jarron, t(CELADON, oscuro))
            drawLine(Color.White.copy(alpha = 0.4f), Offset(x - w * 0.1f, b - a * 0.6f), Offset(x - w * 0.08f, b - a * 0.3f), 2 * u, StrokeCap.Round)
            return
        }
        val cuerpo = Rect(w * 0.1f, h * 0.42f, w * 0.9f, h * 0.95f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Size(cuerpo.width, h * 0.06f))
        // Las tiras de bambú que asoman bajo la tapa.
        if (sube > 1f) {
            drawRect(t(LACA_NEGRA, oscuro), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            val k = c.n.coerceIn(0, 8)
            for (i in 0 until k) {
                val x = cuerpo.left + cuerpo.width * (0.1f + 0.1f * i)
                val y = cuerpo.top - sube * (0.35f + 0.6f * azar(c.semilla, i))
                drawRect(t(BAMBU, oscuro), Offset(x, y), Size(cuerpo.width * 0.07f, cuerpo.top - y + 2 * u))
                drawLine(t(BAMBU_OSC, oscuro), Offset(x, y + 4 * u), Offset(x + cuerpo.width * 0.07f, y + 4 * u), 1f * u)
            }
        }
        // La caja: laca de cinabrio con volutas talladas y un filete de oro.
        drawRoundRect(t(LACA, oscuro), cuerpo.topLeft, cuerpo.size, CornerRadius(4 * u))
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            val p = 13 * u
            var yy = cuerpo.top + p / 2; var fila = 0
            while (yy < cuerpo.bottom) {
                var xx = cuerpo.left + (if (fila % 2 == 0) 0f else p / 2)
                while (xx < cuerpo.right) {
                    drawArc(t(LACA_OSC, oscuro), 180f, 270f, false, Offset(xx - p * 0.3f, yy - p * 0.3f), Size(p * 0.6f, p * 0.6f), style = Stroke(1f * u))
                    xx += p
                }
                yy += p * 0.8f; fila++
            }
        }
        drawRoundRect(t(ORO, oscuro), Offset(cuerpo.left + 3 * u, cuerpo.top + 3 * u), Size(cuerpo.width - 6 * u, cuerpo.height - 6 * u), CornerRadius(3 * u), style = Stroke(1f * u))
        // La tapa, con su filete de oro.
        val tapa = Rect(w * 0.06f, cuerpo.top - sube - h * 0.13f, w * 0.94f, cuerpo.top - sube + 1 * u)
        drawRoundRect(t(LACA, oscuro), tapa.topLeft, tapa.size, CornerRadius(4 * u))
        drawRect(t(LACA_OSC, oscuro), Offset(tapa.left, tapa.bottom - 2.5f * u), Size(tapa.width, 2.5f * u))
        drawRoundRect(t(ORO, oscuro), Offset(tapa.left + 3 * u, tapa.top + 3 * u), Size(tapa.width - 6 * u, tapa.height - 7 * u), CornerRadius(2 * u), style = Stroke(1f * u))
        // Las cantoneras de latón y la cerradura redonda.
        listOf(cuerpo.left, cuerpo.right).forEach { x ->
            val s = if (x < w / 2) 1f else -1f
            drawPath(Path().apply { moveTo(x, cuerpo.bottom); lineTo(x + s * 9 * u, cuerpo.bottom); lineTo(x, cuerpo.bottom - 9 * u); close() }, t(ORO, oscuro))
        }
        val cerr = Offset(w / 2, cuerpo.top + 7 * u)
        drawCircle(t(ORO, oscuro), 5.5f * u, cerr)
        drawRect(t(LACA_NEGRA, oscuro), Offset(cerr.x - 0.8f * u, cerr.y - 2 * u), Size(1.6f * u, 4 * u))
        // La tira de papel pegada al frente, con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1 * u, r.top + 1.5f * u), r.size)
        drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(LACA, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
    }

    override val rotulo = Rect(0.2f, 0.56f, 0.8f, 0.88f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.almendra
    override val salida = 0f
    override val placa = Rect(0.3f, 0.26f, 0.7f, 0.76f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Laca negra, con nubes de oro pintadas.
        drawRect(t(LACA_NEGRA, oscuro))
        for (k in 0 until 22) {
            val c = Offset(w * azar(81, k), h * azar(82, k))
            val r = (8f + 10f * azar(83, k)) * u
            val oro = ORO.copy(alpha = 0.16f)
            drawArc(oro, 180f, 270f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.2f * u))
            drawArc(oro, 0f, 270f, false, Offset(c.x + r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(1.2f * u))
            drawLine(oro, Offset(c.x - r * 2.2f, c.y + r), Offset(c.x + r * 2.4f, c.y + r), 1.2f * u)
        }
        // Las paredes de laca roja, con su filete de oro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LACA, oscuro), r.topLeft, r.size) }
        drawRect(t(ORO, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(2.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        // Bambú viejo, tostado: la página de la Seda es de tinta clara sobre fondo oscuro.
        drawRect(BAMBU_VIEJO.copy(alpha = 0.78f))
        // Las tablillas, una junto a otra, cada una con su nudo.
        val tira = 13 * u * g
        var x = 0f; var k = 0
        while (x < w) {
            if (k % 2 == 1) drawRect(Color.Black.copy(alpha = 0.08f), Offset(x, 0f), Size(tira, h))
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, h), 1f * u)
            drawLine(BAMBU.copy(alpha = 0.14f), Offset(x + 1.2f * u, 0f), Offset(x + 1.2f * u, h), 0.8f * u)
            val ny = h * (0.2f + 0.6f * azar(semilla, k))
            drawLine(BAMBU.copy(alpha = 0.22f), Offset(x + 1 * u, ny), Offset(x + tira - 1 * u, ny), 1.4f * u)
            x += tira; k++
        }
        // Los dos cordeles que las atan, arriba y abajo, con su vuelta en cada tablilla.
        listOf(5 * u * g, h - 5 * u * g).forEach { y ->
            drawLine(CUERDA, Offset(0f, y), Offset(w, y), 1.5f * u * g)
            var xx = 0f
            while (xx < w) { drawCircle(CUERDA, 1.3f * u * g, Offset(xx, y)); xx += tira }
        }
    }

    override val margenes = MargenesFicha(14.dp, 12.dp, 14.dp, 26.dp, 24.dp, 26.dp)
    override val proporcionFicha = 0.85f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un disco bi de jade.
            "convence" -> { drawCircle(Color(0xFF3E8A62), s * 0.32f, c, style = Stroke(s * 0.22f)); drawCircle(Color.White.copy(alpha = 0.35f), s * 0.36f, c, style = Stroke(s * 0.04f)) }
            // Una nube en voluta (ruyi).
            "piensa" -> {
                drawArc(TURQUESA, 180f, 270f, false, Offset(c.x - s * 0.4f, c.y - s * 0.2f), Size(s * 0.4f, s * 0.4f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
                drawArc(TURQUESA, 90f, 270f, false, Offset(c.x, c.y - s * 0.2f), Size(s * 0.4f, s * 0.4f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LACA, 0.14f)
            else -> {}
        }
    }

    /** Una borla de seda roja con su nudo. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawLine(LACA, Offset(c.x, c.y - s * 0.48f), Offset(c.x, c.y - s * 0.2f), s * 0.06f)
        drawCircle(ORO, s * 0.12f, Offset(c.x, c.y - s * 0.14f))
        for (k in -3..3) drawLine(LACA, Offset(c.x + k * s * 0.03f, c.y - s * 0.04f), Offset(c.x + k * s * 0.06f, c.y + s * 0.46f), s * 0.05f)
    }

    override val desorden = 0f
}

/**
 * Gótico: la cajonera de la sacristía. Un mueble de roble con una crestería de arcos apuntados y tréboles calados,
 * pináculos con crochets y remates dorados, y un friso de cuadrifolios en el zócalo. Cada tema es un cajón ancho con paneles
 * de pergamino plegado (linenfold) a los lados, el tema en una cartela de vitela con marco de latón y una argolla de hierro;
 * los más llenos quedan entreabiertos y asoman las hojas. Por dentro, seda azul adamascada con flores de lis. Las fichas son
 * hojas de vitela con un filete de oro y hojas de hiedra en el margen.
 */
object IdeasGotico : DisenoIdeas {
    private val ROBLE = Color(0xFF5A3E24)
    private val ROBLE_LUZ = Color(0xFF80603A)
    private val ROBLE_OSC = Color(0xFF2E1F12)
    private val ORO = Color(0xFFC79A3A)
    private val AZUL = Color(0xFF1F3A9E)
    private val CARMESI = Color(0xFFB3122E)
    private val VITELA = Color(0xFFF3ECDC)
    private val VITELA_OSC = Color(0xFFDCCFB0)
    private val HIERRO = Color(0xFF242226)
    private val HIERRO_LUZ = Color(0xFF66626A)
    private val TINTA = Color(0xFF141A33)
    private val DAMASCO = Color(0xFF1C2A5C)
    private val PIEDRA = Color(0xFF8C8A84)
    private val PIEDRA_OSC = Color(0xFF5E5C58)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 2.1f
    override val corona = 0.3f
    override val base = 0.11f
    override val lado = 0.075f
    override val hueco = 0.025f

    private const val SALIDA = 0.18f

    /** Una flor de lis de [s] de alto, centrada en [c]. */
    private fun DrawScope.florDeLis(c: Offset, s: Float, color: Color) {
        drawPath(Path().apply {
            moveTo(c.x, c.y - s * 0.48f); quadraticTo(c.x + s * 0.17f, c.y - s * 0.16f, c.x, c.y + s * 0.06f); quadraticTo(c.x - s * 0.17f, c.y - s * 0.16f, c.x, c.y - s * 0.48f); close()
            moveTo(c.x + s * 0.04f, c.y + s * 0.04f); cubicTo(c.x + s * 0.3f, c.y - s * 0.04f, c.x + s * 0.46f, c.y - s * 0.28f, c.x + s * 0.32f, c.y - s * 0.36f)
            quadraticTo(c.x + s * 0.36f, c.y - s * 0.1f, c.x + s * 0.08f, c.y + s * 0.1f); close()
            moveTo(c.x - s * 0.04f, c.y + s * 0.04f); cubicTo(c.x - s * 0.3f, c.y - s * 0.04f, c.x - s * 0.46f, c.y - s * 0.28f, c.x - s * 0.32f, c.y - s * 0.36f)
            quadraticTo(c.x - s * 0.36f, c.y - s * 0.1f, c.x - s * 0.08f, c.y + s * 0.1f); close()
            moveTo(c.x - s * 0.06f, c.y + s * 0.14f); lineTo(c.x + s * 0.06f, c.y + s * 0.14f); lineTo(c.x + s * 0.02f, c.y + s * 0.46f); lineTo(c.x - s * 0.02f, c.y + s * 0.46f); close()
        }, color)
        drawRect(color, Offset(c.x - s * 0.22f, c.y + s * 0.06f), Size(s * 0.44f, s * 0.09f))
    }

    /** Un cuadrifolio: cuatro lóbulos alrededor de [c]. */
    private fun DrawScope.cuadrifolio(c: Offset, r: Float, color: Color, trazo: Float? = null) {
        listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { o ->
            val p = Offset(c.x + o.x * r * 0.5f, c.y + o.y * r * 0.5f)
            if (trazo == null) drawCircle(color, r * 0.5f, p) else drawCircle(color, r * 0.5f, p, style = Stroke(trazo))
        }
    }

    /** Un pináculo: la aguja con sus crochets y su remate dorado; [b] es el centro de su base. */
    private fun DrawScope.pinaculo(b: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        val u = density
        val aguja = Path().apply { moveTo(b.x - ancho / 2, b.y); lineTo(b.x, b.y - alto); lineTo(b.x + ancho / 2, b.y); close() }
        drawPath(aguja, t(ROBLE_LUZ, oscuro))
        drawPath(aguja, t(ROBLE_OSC, oscuro), style = Stroke(1f * u))
        for (k in 1..3) {
            val f = k / 4f
            val y = b.y - alto * f
            val dx = ancho / 2 * (1 - f)
            drawCircle(t(ROBLE_LUZ, oscuro), 1.8f * u, Offset(b.x - dx - 1.5f * u, y))
            drawCircle(t(ROBLE_LUZ, oscuro), 1.8f * u, Offset(b.x + dx + 1.5f * u, y))
        }
        drawCircle(t(ORO, oscuro), 2.4f * u, Offset(b.x, b.y - alto - 1.5f * u))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.62f
        // El piso de losas grises.
        drawRect(t(PIEDRA, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        for (j in 0 until 7) drawLine(t(PIEDRA_OSC, oscuro), Offset(w * j / 7f, piso), Offset(w * j / 7f, d.base.bottom), 1f * u)
        // El cuerpo de roble.
        val cuerpo = Rect(w * 0.035f, d.corona.bottom - 4 * u, w * 0.965f, piso)
        drawRect(t(ROBLE, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, ROBLE_OSC.copy(alpha = 0.4f), 13, 1f, grosor = 0.7f, ondulacion = 0.4f)
        // El rebaje de cada cajón y los travesaños.
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        // Los contrafuertes de los costados, con su aguja.
        listOf(w * 0.035f, w * 0.965f - w * 0.035f).forEach { x ->
            val r = Rect(x, d.corona.top + ch * 0.5f, x + w * 0.035f, piso)
            drawRect(t(ROBLE_LUZ, oscuro), r.topLeft, r.size)
            drawRect(t(ROBLE_OSC, oscuro), r.topLeft, r.size, style = Stroke(1f * u))
            drawLine(t(ROBLE_OSC, oscuro), Offset(r.center.x, r.top + 4 * u), Offset(r.center.x, r.bottom - 4 * u), 1f * u)
            pinaculo(Offset(r.center.x, r.top), r.width * 1.1f, ch * 0.45f, oscuro)
        }
        // La crestería: una banda de arcos apuntados con tréboles calados, y una fila de pináculos encima.
        val banda = Rect(w * 0.07f, d.corona.top + ch * 0.55f, w * 0.93f, d.corona.bottom)
        drawRect(t(ROBLE_LUZ, oscuro), banda.topLeft, banda.size)
        val n = 8
        val aw = banda.width / n
        for (j in 0 until n) {
            val r = Rect(banda.left + aw * j + aw * 0.12f, banda.top + banda.height * 0.14f, banda.left + aw * (j + 1) - aw * 0.12f, banda.bottom - banda.height * 0.12f)
            drawPath(arcoApuntado(r, r.width * 0.7f), t(ROBLE_OSC, oscuro))
            val tc = Offset(r.center.x, r.top + r.width * 0.5f)
            for (k in 0 until 3) {
                val a = Math.toRadians(-90.0 + k * 120.0)
                drawCircle(t(ROBLE_LUZ, oscuro), r.width * 0.13f, Offset(tc.x + r.width * 0.15f * cos(a).toFloat(), tc.y + r.width * 0.15f * sin(a).toFloat()))
            }
            drawLine(t(ROBLE_LUZ, oscuro), Offset(r.center.x, tc.y + r.width * 0.2f), Offset(r.center.x, r.bottom), 1.4f * u)
            pinaculo(Offset(banda.left + aw * j + aw / 2, banda.top), aw * 0.36f, ch * 0.3f, oscuro)
        }
        drawRect(t(ROBLE_OSC, oscuro), Offset(banda.left, banda.bottom - 3 * u), Size(banda.width, 3 * u))
        drawLine(t(ORO, oscuro), Offset(banda.left, banda.top), Offset(banda.right, banda.top), 1.4f * u)
        // El zócalo con su friso de cuadrifolios.
        val zocalo = Rect(cuerpo.left, d.base.top + 1 * u, cuerpo.right, piso)
        drawRect(t(ROBLE_OSC, oscuro), zocalo.topLeft, zocalo.size)
        val qn = 12
        for (j in 0 until qn) cuadrifolio(Offset(zocalo.left + zocalo.width * (j + 0.5f) / qn, zocalo.center.y), zocalo.height * 0.42f, t(ROBLE_LUZ, oscuro), 1f * u)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(VITELA, oscuro), r.topLeft, r.size)
            drawRect(t(ORO, oscuro), r.topLeft, r.size, style = Stroke(1.6f * u))
        }
    }

    /** Un panel de pergamino plegado tallado: pliegues verticales con sus remates curvos. */
    private fun DrawScope.linenfold(r: Rect, oscuro: Boolean) {
        val u = density
        drawRect(t(mezcla(ROBLE, ROBLE_OSC, 0.3f), oscuro), r.topLeft, r.size)
        val n = 3
        val pw = r.width / n
        for (k in 0 until n) {
            val x = r.left + pw * k
            drawLine(Color.White.copy(alpha = 0.18f), Offset(x + pw * 0.25f, r.top + pw * 0.4f), Offset(x + pw * 0.25f, r.bottom - pw * 0.4f), 1.3f * u)
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(x + pw * 0.75f, r.top + pw * 0.4f), Offset(x + pw * 0.75f, r.bottom - pw * 0.4f), 1.3f * u)
            drawArc(Color.Black.copy(alpha = 0.35f), 180f, 180f, false, Offset(x + pw * 0.15f, r.top + pw * 0.1f), Size(pw * 0.7f, pw * 0.5f), style = Stroke(1f * u))
            drawArc(Color.White.copy(alpha = 0.15f), 0f, 180f, false, Offset(x + pw * 0.15f, r.bottom - pw * 0.6f), Size(pw * 0.7f, pw * 0.5f), style = Stroke(1f * u))
        }
        drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, r.size, style = Stroke(1f * u))
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajones más llenos quedan entreabiertos y asoman las hojas.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF0E0A06), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.05f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                drawRect(t(if (i % 2 == 0) VITELA else VITELA_OSC, oscuro), Offset(x, y0), Size(w * 0.1f, arriba + 4 * u - y0))
                drawRect(t(if (i % 3 == 0) CARMESI else AZUL, oscuro), Offset(x + w * 0.035f, y0 - 3 * u), Size(w * 0.03f, 4 * u))
            }
        }
        // El frente del cajón: al abrirse baja y se acerca un poco.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(ROBLE, oscuro), f.topLeft, f.size)
        vetas(f, ROBLE_OSC.copy(alpha = 0.4f), c.semilla, 1.2f, grosor = 0.7f, ondulacion = 0.4f)
        bisel(f, Color.White.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.35f), 1.3f * u)
        // Los paneles de pergamino plegado a los lados (en el lugar libre, también al centro).
        val fh = f.bottom - f.top
        val izq = Rect(w * 0.04f, f.top + fh * 0.14f, w * 0.2f, f.bottom - fh * 0.14f)
        val der = Rect(w * 0.8f, izq.top, w * 0.96f, izq.bottom)
        linenfold(izq, oscuro); linenfold(der, oscuro)
        if (c.vacio) { linenfold(Rect(w * 0.24f, izq.top, w * 0.76f, izq.bottom), oscuro); return }
        // La cartela de vitela con su marco de latón y un trebolillo encima.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        drawRect(t(VITELA, oscuro), r.topLeft, r.size)
        drawRect(t(ORO, oscuro), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u), style = Stroke(2.2f * u))
        cuadrifolio(Offset(r.center.x, r.top - 2.5f * u), 6 * u, t(ORO, oscuro))
        // La argolla de hierro bajo la cartela.
        val ar = Offset(w / 2, h * 0.84f + baja)
        val rr = minOf(w, h) * 0.07f
        cuadrifolio(Offset(ar.x, ar.y - rr * 0.6f), rr * 0.9f, t(HIERRO, oscuro))
        drawArc(t(HIERRO, oscuro), 0f, 180f, false, Offset(ar.x - rr, ar.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.8f * u))
        drawCircle(t(HIERRO_LUZ, oscuro), 1f * u, Offset(ar.x, ar.y - rr * 0.6f))
    }

    override val rotulo = Rect(0.24f, 0.17f, 0.76f, 0.66f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.goudy
    override val salida = SALIDA
    override val placa = Rect(0.32f, 0.08f, 0.68f, 0.56f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Seda azul adamascada: una retícula de rombos tenue y flores de lis doradas al tresbolillo.
        drawRect(t(DAMASCO, oscuro))
        val p = 44 * u
        var d0 = -h
        while (d0 < w + h) {
            drawLine(Color.White.copy(alpha = 0.05f), Offset(d0, 0f), Offset(d0 + h, h), 1f * u)
            drawLine(Color.White.copy(alpha = 0.05f), Offset(d0 + h, 0f), Offset(d0, h), 1f * u)
            d0 += p
        }
        var y = p / 2; var fila = 0
        while (y < h) {
            var x = if (fila % 2 == 0) p / 2 else p
            while (x < w) { florDeLis(Offset(x, y), 14 * u, ORO.copy(alpha = 0.3f)); x += p }
            y += p / 2; fila++
        }
        // Las paredes del cajón, de roble, con su filete dorado.
        val pl = 16 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(ROBLE, oscuro), r.topLeft, r.size)
            vetas(r, ROBLE_OSC.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        drawRect(t(ORO, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = VITELA
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 30, 0.005f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(VITELA.copy(alpha = 0.4f))
        // El filete de oro a la izquierda, de punta a punta, con hojas de hiedra azules y rojas a los lados y un rizo en cada extremo.
        val x = 6 * u * g
        val y0 = h * 0.08f; val y1 = h * 0.92f
        drawLine(ORO, Offset(x, y0), Offset(x, y1), 2.2f * u * g)
        drawCircle(ORO, 3 * u * g, Offset(x + 2.5f * u * g, y0 - 2 * u * g), style = Stroke(1.2f * u * g))
        drawCircle(ORO, 3 * u * g, Offset(x + 2.5f * u * g, y1 + 2 * u * g), style = Stroke(1.2f * u * g))
        var y = y0 + 8 * u * g; var k = 0
        while (y < y1 - 4 * u * g) {
            val s = if (k % 2 == 0) 1f else -1f
            val hoja = Offset(x + s * 4 * u * g, y)
            drawLine(TINTA.copy(alpha = 0.5f), Offset(x, y - 2 * u * g), hoja, 0.6f * u)
            drawPath(Path().apply { moveTo(hoja.x, hoja.y - 2.4f * u * g); lineTo(hoja.x + s * 2.6f * u * g, hoja.y + 1.4f * u * g); lineTo(hoja.x - s * 0.6f * u * g, hoja.y + 2 * u * g); close() },
                (if (k % 3 == 0) CARMESI else AZUL).copy(alpha = 0.75f))
            y += 13 * u * g; k++
        }
    }

    override val margenes = MargenesFicha(12.dp, 18.dp, 12.dp, 24.dp, 32.dp, 28.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> florDeLis(c, s * 0.92f, ORO)
            "piensa" -> cuadrifolio(c, s * 0.48f, AZUL)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(CARMESI)
            else -> {}
        }
    }

    /** Una cinta de seda carmesí que hace de registro, con su punta dorada. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.16f, c.y - s * 0.5f); lineTo(c.x + s * 0.16f, c.y - s * 0.5f); lineTo(c.x + s * 0.16f, c.y + s * 0.46f); lineTo(c.x, c.y + s * 0.3f); lineTo(c.x - s * 0.16f, c.y + s * 0.46f); close() }, CARMESI)
        drawRect(ORO, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.1f))
    }

    override val desorden = 0f
}

/**
 * Al-Ándalus: la alacena del muro. Un paño de yesería con su red de rombos (sebka) y una banda de inscripción, y un arco de
 * herradura de dovelas rojas y blancas que se abre a la alacena de cedro; abajo, un zócalo de alicatado. Cada tema es una
 * cajita de taracea de nogal con dientes de hueso, ébano y verde, estrellas de ocho puntas en la tapa y el tema en una placa
 * de marfil. Por dentro, un alicatado de estrellas. Las fichas son hojas con su recuadro de oro y azul (jadwal) y dos
 * medallones en el margen.
 */
object IdeasAndalus : DisenoIdeas {
    private val YESO = Color(0xFFF1E8D6)
    private val YESO_OSC = Color(0xFFD2C3A4)
    private val CEDRO = Color(0xFF7A4A2A)
    private val CEDRO_OSC = Color(0xFF3E2414)
    private val VERDE = Color(0xFF1E6B5C)
    private val AZUL = Color(0xFF2A4F9E)
    private val OCRE = Color(0xFFC8963A)
    private val NEGRO = Color(0xFF221C18)
    private val MARFIL = Color(0xFFEFE6CF)
    private val NOGAL = Color(0xFF4A3020)
    private val TERRACOTA = Color(0xFFB5432A)
    private val TINTA = Color(0xFF2A1A12)
    private val LATON = Color(0xFFC9A04A)
    private val LUZ = Color(0xFFF2C46B)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.34f
    override val base = 0.14f
    override val lado = 0.12f
    override val hueco = 0.06f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.1f
        // El paño de yeso, con la red de rombos en relieve y una banda de inscripción arriba.
        drawRect(t(YESO, oscuro), Offset.Zero, Size(w, piso))
        val p = 16 * u
        clipRect(0f, d.corona.top + ch * 0.18f, w, piso) {
            var x0 = -piso
            while (x0 < w + piso) {
                drawLine(t(YESO_OSC, oscuro), Offset(x0, 0f), Offset(x0 + piso, piso), 1.2f * u)
                drawLine(t(YESO_OSC, oscuro), Offset(x0 + piso, 0f), Offset(x0, piso), 1.2f * u)
                x0 += p
            }
        }
        val ins = Rect(0f, d.corona.top, w, d.corona.top + ch * 0.18f)
        drawRect(t(mezcla(YESO, YESO_OSC, 0.4f), oscuro), ins.topLeft, ins.size)
        var x = 6 * u; var k = 0
        while (x < w - 6 * u) {
            val alto = ins.height * (0.3f + 0.45f * azar(5, k))
            drawLine(t(CEDRO_OSC, oscuro).copy(alpha = 0.55f), Offset(x, ins.bottom - 3 * u), Offset(x, ins.bottom - 3 * u - alto), 1.4f * u, StrokeCap.Round)
            if (k % 3 == 1) drawArc(t(CEDRO_OSC, oscuro).copy(alpha = 0.55f), 0f, 180f, false, Offset(x, ins.bottom - 7 * u), Size(6 * u, 4 * u), style = Stroke(1.2f * u))
            x += 5 * u; k++
        }
        drawLine(t(CEDRO_OSC, oscuro).copy(alpha = 0.6f), Offset(0f, ins.bottom - 2.5f * u), Offset(w, ins.bottom - 2.5f * u), 1.2f * u)
        // El arco de herradura, con dovelas alternadas rojas y blancas, que se abre a la alacena de cedro.
        val vano = Rect(w * 0.07f, d.corona.top + ch * 0.26f, w * 0.93f, piso)
        val ext = arcoHerradura(Rect(vano.left - 9 * u, vano.top - 9 * u, vano.right + 9 * u, piso))
        val int = arcoHerradura(vano)
        val anillo = Path().apply { op(ext, int, PathOperation.Difference) }
        drawPath(anillo, t(MARFIL, oscuro))
        clipPath(anillo) {
            val rad = (vano.width + 18 * u) / 2 / 0.906f
            val centro = Offset(w / 2, vano.top - 9 * u + rad)
            for (j in 0 until 24) {
                if (j % 2 == 1) continue
                val a0 = 155f + j * (230f / 24)
                drawArc(t(TERRACOTA, oscuro), a0, 230f / 24, true, Offset(centro.x - rad * 1.2f, centro.y - rad * 1.2f), Size(rad * 2.4f, rad * 2.4f))
            }
        }
        drawPath(ext, t(YESO_OSC, oscuro), style = Stroke(1.6f * u))
        drawPath(int, t(CEDRO_OSC, oscuro))
        clipPath(int) {
            var bx = vano.left
            while (bx < vano.right) { drawLine(Color.Black.copy(alpha = 0.35f), Offset(bx, 0f), Offset(bx, piso), 1f * u); bx += 26 * u }
            mancha(Offset(w / 2, vano.top + ch * 0.4f), w * 0.4f, LUZ.copy(alpha = if (oscuro) 0.12f else 0.1f))
            // Las repisas de cedro, con su canto tallado de arquillos.
            d.filas.forEach { f ->
                val r = Rect(0f, f.bottom - 1 * u, w, f.bottom + maxOf(d.separacion * 0.5f, 8 * u))
                drawRect(t(CEDRO, oscuro), r.topLeft, r.size)
                var ax = 0f
                while (ax < w) { drawArc(t(CEDRO_OSC, oscuro), 0f, 180f, false, Offset(ax, r.top + r.height * 0.15f), Size(6 * u, r.height * 0.6f), style = Stroke(1f * u)); ax += 7 * u }
                drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.bottom), Size(r.width, 2.5f * u))
            }
        }
        drawPath(int, Color.Black.copy(alpha = 0.35f), style = Stroke(1.5f * u))
        // El zócalo de alicatado: lazos de colores y una estrella de ocho puntas blanca en cada cruce.
        val zoc = Rect(0f, piso, w, d.base.bottom)
        drawRect(t(MARFIL, oscuro), zoc.topLeft, zoc.size)
        drawRect(t(NEGRO, oscuro), Offset(0f, zoc.top), Size(w, 3 * u))
        val lt = zoc.height / 3
        var tx = 0f; var q = 0
        while (tx < w) {
            for (f in 0 until 3) {
                val col = listOf(VERDE, AZUL, NEGRO, OCRE)[(q + f * 2) % 4]
                drawRect(t(col, oscuro), Offset(tx + 1 * u, zoc.top + 3 * u + f * lt + 1 * u), Size(lt - 2 * u, lt - 2 * u))
            }
            tx += lt; q++
        }
        tx = lt
        while (tx < w) {
            for (f in 1 until 3) drawPath(estrella(Offset(tx, zoc.top + 3 * u + f * lt), lt * 0.42f, 8, 0.6f), t(MARFIL, oscuro))
            tx += lt * 2
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(MARFIL, oscuro), r.topLeft, r.size)
            drawRect(t(VERDE, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1.2f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un candil de latón con su llama.
            val b = Offset(w / 2, h * 0.92f)
            mancha(Offset(b.x + w * 0.14f, b.y - h * 0.2f), h * 0.35f, LUZ.copy(alpha = if (oscuro) 0.3f else 0.2f))
            drawPath(Path().apply { moveTo(b.x - w * 0.16f, b.y - h * 0.1f); quadraticTo(b.x - w * 0.14f, b.y, b.x, b.y); quadraticTo(b.x + w * 0.1f, b.y, b.x + w * 0.16f, b.y - h * 0.08f)
                lineTo(b.x + w * 0.2f, b.y - h * 0.12f); lineTo(b.x + w * 0.1f, b.y - h * 0.11f); close() }, t(LATON, oscuro))
            drawArc(t(LATON, oscuro), 180f, 180f, false, Offset(b.x - w * 0.24f, b.y - h * 0.2f), Size(w * 0.12f, h * 0.18f), style = Stroke(2 * u))
            drawOval(LUZ, Offset(b.x + w * 0.17f, b.y - h * 0.26f), Size(6 * u, 12 * u))
            return
        }
        val cuerpo = Rect(w * 0.09f, h * 0.42f, w * 0.91f, h * 0.95f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Size(cuerpo.width, h * 0.06f))
        if (sube > 1f) {
            drawRect(Color(0xFF140C06), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.08f + 0.14f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRect(t(MARFIL, oscuro), Offset(x, y), Size(cuerpo.width * 0.12f, cuerpo.top - y + 2 * u))
                drawLine(t(OCRE, oscuro), Offset(x + 2 * u, y + 2.5f * u), Offset(x + cuerpo.width * 0.12f - 2 * u, y + 2.5f * u), 0.8f * u)
            }
        }
        // La cajita de nogal, con sus dientes de taracea arriba y abajo.
        drawRect(t(NOGAL, oscuro), cuerpo.topLeft, cuerpo.size)
        fun dientes(y: Float, alto: Float) {
            val n = (cuerpo.width / alto).toInt().coerceAtLeast(4)
            val aw = cuerpo.width / n
            for (j in 0 until n) {
                val x = cuerpo.left + aw * j
                drawPath(Path().apply { moveTo(x, y + alto); lineTo(x + aw / 2, y); lineTo(x + aw, y + alto); close() }, t(listOf(MARFIL, VERDE, MARFIL, NEGRO)[j % 4], oscuro))
            }
        }
        dientes(cuerpo.top + 3 * u, 6 * u)
        dientes(cuerpo.bottom - 9 * u, 6 * u)
        drawRect(t(MARFIL, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1f * u))
        // La tapa, con estrellas de ocho puntas de hueso y verde.
        val tapa = Rect(w * 0.06f, cuerpo.top - sube - h * 0.14f, w * 0.94f, cuerpo.top - sube + 1 * u)
        drawRect(t(NOGAL, oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(tapa.left, tapa.bottom - 2 * u), Size(tapa.width, 2 * u))
        val ns = 5
        for (j in 0 until ns) {
            val sc = Offset(tapa.left + tapa.width * (j + 0.5f) / ns, tapa.center.y - 1 * u)
            drawPath(estrella(sc, tapa.height * 0.36f, 8, 0.62f), t(if (j % 2 == 0) MARFIL else VERDE, oscuro))
            drawCircle(t(NOGAL, oscuro), tapa.height * 0.1f, sc)
        }
        drawRect(t(MARFIL, oscuro), tapa.topLeft, tapa.size, style = Stroke(1f * u))
        // La placa de marfil con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(t(MARFIL, oscuro), r.topLeft, r.size)
        drawRect(t(VERDE, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
    }

    override val rotulo = Rect(0.18f, 0.56f, 0.82f, 0.82f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.amiri
    override val salida = 0f
    override val placa = Rect(0.32f, 0.3f, 0.68f, 0.9f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un alicatado claro de estrellas de ocho puntas, con lazos de colores tenues.
        val fondo = if (oscuro) mezcla(MARFIL, Color.Black, 0.6f) else MARFIL
        drawRect(fondo)
        val p = 54 * u
        var y = 0f; var fila = 0
        while (y < h + p) {
            var x = if (fila % 2 == 0) 0f else p / 2
            while (x < w + p) {
                val col = if ((fila + (x / p).toInt()) % 2 == 0) VERDE else AZUL
                drawPath(estrella(Offset(x, y), p * 0.36f, 8, 0.7f), col.copy(alpha = 0.16f))
                drawPath(estrella(Offset(x, y), p * 0.36f, 8, 0.7f), col.copy(alpha = 0.32f), style = Stroke(1.2f * u))
                drawCircle(OCRE.copy(alpha = 0.3f), p * 0.07f, Offset(x, y))
                x += p
            }
            y += p / 2; fila++
        }
        // Las paredes de cedro, con su banda de arquillos tallados.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CEDRO, oscuro), r.topLeft, r.size) }
        var ax = pl
        while (ax < w - pl) { drawArc(t(CEDRO_OSC, oscuro), 180f, 180f, false, Offset(ax, pl * 0.3f), Size(7 * u, pl * 0.8f), style = Stroke(1f * u)); ax += 8 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) MARFIL else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(2.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(MARFIL.copy(alpha = 0.35f))
        // El recuadro (jadwal): un filete negro fino, uno de oro y uno azul por dentro.
        val m = 5 * u * g
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(m - 1.6f * u * g, m - 1.6f * u * g), Size(w - 2 * m + 3.2f * u * g, h - 2 * m + 3.2f * u * g), style = Stroke(0.5f * u))
        drawRect(LATON, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.5f * u * g))
        drawRect(AZUL.copy(alpha = 0.7f), Offset(m + 2.4f * u * g, m + 2.4f * u * g), Size(w - 2 * m - 4.8f * u * g, h - 2 * m - 4.8f * u * g), style = Stroke(0.6f * u))
        // Dos medallones dorados en el margen, a media altura.
        listOf(m, w - m).forEach { x ->
            val c = Offset(x, h / 2)
            drawCircle(LATON, 3.6f * u * g, c)
            drawPath(estrella(c, 3.2f * u * g, 8, 0.55f), AZUL.copy(alpha = 0.8f))
            drawCircle(MARFIL, 1 * u * g, c)
        }
    }

    override val margenes = MargenesFicha(14.dp, 16.dp, 14.dp, 26.dp, 30.dp, 28.dp)
    override val proporcionFicha = 0.82f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "convence" -> drawPath(estrella(c, s * 0.46f, 8, 0.7f), VERDE)
            // Dos aros entrelazados.
            "piensa" -> {
                drawCircle(AZUL, s * 0.24f, Offset(c.x - s * 0.13f, c.y), style = Stroke(s * 0.08f))
                drawCircle(AZUL, s * 0.24f, Offset(c.x + s * 0.13f, c.y), style = Stroke(s * 0.08f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(TERRACOTA)
            else -> {}
        }
    }

    /** Una roseta de oro (shamsa) con su centro azul. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(estrella(c, s * 0.48f, 12, 0.72f), LATON)
        drawCircle(AZUL, s * 0.2f, c)
        drawCircle(LATON, s * 0.07f, c)
    }

    override val desorden = 0f
}

/**
 * Mongol: el interior de la ger. Arriba, el toono (la corona de madera por donde entra la luz) con sus varas pintadas que
 * bajan sobre el fieltro; detrás, el enrejado del khana; al pie, una alfombra roja. Cada tema es un arcón pintado de
 * naranja, con un recuadro azul, volutas de nube doradas en las esquinas y el tema en un panel crema; asoman tablillas de
 * abedul bajo la tapa. Por dentro, fieltro blanco con pespuntes rojos. Las fichas son tablillas de corteza de abedul.
 */
object IdeasMongol : DisenoIdeas {
    private val FIELTRO = Color(0xFFEDE6D6)
    private val FIELTRO_OSC = Color(0xFFCFC4AE)
    private val NARANJA = Color(0xFFD9712A)
    private val NARANJA_OSC = Color(0xFFA84E18)
    private val ROJO = Color(0xFFB0302A)
    private val AZUL = Color(0xFF1F5FA8)
    private val AZUL_OSC = Color(0xFF163F70)
    private val VERDE = Color(0xFF2E7A5A)
    private val ORO = Color(0xFFD8A23A)
    private val ABEDUL = Color(0xFFF2EEE4)
    private val LENTICELA = Color(0xFF3A3530)
    private val TINTA = Color(0xFF18202E)
    private val CREMA = Color(0xFFF4ECD8)
    private val MADERA = Color(0xFF8A5A30)
    private val ALFOMBRA = Color(0xFF8E2A24)
    private val CIELO = Color(0xFFBFD8EE)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.35f
    override val corona = 0.32f
    override val base = 0.11f
    override val lado = 0.07f
    override val hueco = 0.06f

    /** Una voluta de nube: una espiral corta. */
    private fun DrawScope.voluta(c: Offset, r: Float, color: Color, grosor: Float, espejo: Boolean = false) {
        val p = Path()
        for (k in 0..16) {
            val a = k * 0.55f
            val rr = r * (1f - k / 18f)
            val x = c.x + (if (espejo) -1 else 1) * rr * cos(a); val y = c.y + rr * sin(a)
            if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.3f
        // El fieltro de la pared y, delante, el enrejado del khana con sus nudos de cuero.
        drawRect(t(FIELTRO_OSC, oscuro), Offset(0f, d.corona.bottom), Size(w, piso - d.corona.bottom))
        clipRect(0f, d.corona.bottom, w, piso) {
            val p = 26 * u
            val alto = piso - d.corona.bottom
            var x0 = -alto
            while (x0 < w + alto) {
                drawLine(t(NARANJA_OSC, oscuro), Offset(x0, d.corona.bottom), Offset(x0 + alto, piso), 2.4f * u)
                drawLine(t(NARANJA_OSC, oscuro), Offset(x0 + alto, d.corona.bottom), Offset(x0, piso), 2.4f * u)
                x0 += p
            }
        }
        // El techo: el fieltro, las varas que bajan del toono y el toono con su cielo.
        drawRect(t(FIELTRO, oscuro), d.corona.topLeft, d.corona.size)
        val toono = Offset(w / 2, d.corona.top)
        val rt = ch * 0.42f
        val nv = 18
        for (k in 0..nv) {
            val xb = w * k / nv
            val dir = Offset(xb - toono.x, d.corona.bottom - toono.y)
            val l = kotlin.math.sqrt(dir.x * dir.x + dir.y * dir.y)
            val a = Offset(toono.x + dir.x / l * rt, toono.y + dir.y / l * rt)
            drawLine(t(NARANJA, oscuro), a, Offset(xb, d.corona.bottom), 3 * u)
            val f = 0.18f
            drawLine(t(AZUL, oscuro), a, Offset(a.x + (xb - a.x) * f, a.y + (d.corona.bottom - a.y) * f), 3 * u)
        }
        drawCircle(t(CIELO, oscuro), rt * 0.82f, toono)
        for (k in 0 until 8) {
            val a = Math.toRadians(k * 22.5).toFloat()
            drawLine(t(NARANJA, oscuro), Offset(toono.x + rt * 0.82f * cos(a), toono.y + rt * 0.82f * sin(a)), Offset(toono.x - rt * 0.82f * cos(a), toono.y + rt * 0.82f * sin(a)), 1.8f * u)
        }
        drawCircle(t(NARANJA, oscuro), rt, toono, style = Stroke(rt * 0.2f))
        drawCircle(t(ROJO, oscuro), rt * 0.9f, toono, style = Stroke(1.4f * u))
        // La viga del muro, con su greca azul.
        val viga = Rect(0f, d.corona.bottom - 7 * u, w, d.corona.bottom + 3 * u)
        drawRect(t(NARANJA, oscuro), viga.topLeft, viga.size)
        var gx = 0f; var q = 0
        while (gx < w) {
            drawRect(t(AZUL, oscuro), Offset(gx + 2 * u, viga.top + 2 * u), Size(5 * u, 2 * u))
            drawRect(t(AZUL, oscuro), Offset(gx + (if (q % 2 == 0) 2 else 5) * u, viga.top + 2 * u), Size(2 * u, 6 * u))
            gx += 9 * u; q++
        }
        // Los bancos pintados donde descansan los arcones.
        d.filas.forEach { f ->
            val r = Rect(w * 0.03f, f.bottom - 1 * u, w * 0.97f, f.bottom + maxOf(d.separacion * 0.45f, 7 * u))
            drawRect(t(NARANJA_OSC, oscuro), r.topLeft, r.size)
            drawRect(t(AZUL, oscuro), Offset(r.left, r.top + r.height * 0.4f), Size(r.width, r.height * 0.25f))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 2.5f * u))
        }
        // El piso de tablas y la alfombra roja con su cenefa.
        drawRect(t(MADERA, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        val alf = Rect(w * 0.04f, piso + 3 * u, w * 0.96f, d.base.bottom - 2 * u)
        drawRect(t(ALFOMBRA, oscuro), alf.topLeft, alf.size)
        drawRect(t(CREMA, oscuro), Offset(alf.left + 3 * u, alf.top + 3 * u), Size(alf.width - 6 * u, alf.height - 6 * u), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * u, 3 * u))))
        var vx = alf.left + 14 * u
        while (vx < alf.right - 10 * u) { voluta(Offset(vx, alf.center.y), alf.height * 0.2f, t(CREMA, oscuro).copy(alpha = 0.7f), 1f * u); vx += 26 * u }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(CREMA, oscuro), r.topLeft, r.size)
            drawRect(t(AZUL, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1.2f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un cuenco de madera sobre su pie.
            val b = Offset(w / 2, h * 0.94f)
            drawRect(t(MADERA, oscuro), Offset(b.x - w * 0.06f, b.y - h * 0.05f), Size(w * 0.12f, h * 0.05f))
            drawArc(t(MADERA, oscuro), 0f, 180f, true, Offset(b.x - w * 0.18f, b.y - h * 0.3f), Size(w * 0.36f, h * 0.36f))
            drawLine(t(ORO, oscuro), Offset(b.x - w * 0.17f, b.y - h * 0.11f), Offset(b.x + w * 0.17f, b.y - h * 0.11f), 1.6f * u)
            return
        }
        val cuerpo = Rect(w * 0.06f, h * 0.38f, w * 0.94f, h * 0.92f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.18f
        // Las patas.
        listOf(cuerpo.left + 4 * u, cuerpo.right - 10 * u).forEach { x -> drawRect(t(NARANJA_OSC, oscuro), Offset(x, cuerpo.bottom), Size(6 * u, h * 0.05f)) }
        // Las tablillas de abedul que asoman bajo la tapa.
        if (sube > 1f) {
            drawRect(Color(0xFF120C08), Offset(cuerpo.left + 3 * u, cuerpo.top - sube), Size(cuerpo.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.08f + 0.14f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRect(t(ABEDUL, oscuro), Offset(x, y), Size(cuerpo.width * 0.12f, cuerpo.top - y + 2 * u))
                drawLine(t(LENTICELA, oscuro), Offset(x + 2 * u, y + 3 * u), Offset(x + 6 * u, y + 3 * u), 1f * u)
            }
        }
        // El arcón naranja, con su recuadro azul y las volutas doradas en las esquinas.
        drawRect(t(NARANJA, oscuro), cuerpo.topLeft, cuerpo.size)
        val panel = Rect(cuerpo.left + 5 * u, cuerpo.top + 5 * u, cuerpo.right - 5 * u, cuerpo.bottom - 5 * u)
        drawRect(t(AZUL, oscuro), panel.topLeft, panel.size, style = Stroke(3.5f * u))
        drawRect(t(ORO, oscuro), Offset(panel.left + 3 * u, panel.top + 3 * u), Size(panel.width - 6 * u, panel.height - 6 * u), style = Stroke(0.8f * u))
        val rv = minOf(panel.width, panel.height) * 0.12f
        listOf(Offset(panel.left + rv * 1.3f, panel.top + rv * 1.3f) to false, Offset(panel.right - rv * 1.3f, panel.top + rv * 1.3f) to true,
            Offset(panel.left + rv * 1.3f, panel.bottom - rv * 1.3f) to false, Offset(panel.right - rv * 1.3f, panel.bottom - rv * 1.3f) to true).forEach { (o, e) ->
            voluta(o, rv, t(ORO, oscuro), 1.4f * u, e)
        }
        drawRect(t(NARANJA_OSC, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1.2f * u))
        // La tapa, con una franja roja y botones verdes y azules.
        val tapa = Rect(w * 0.04f, cuerpo.top - sube - h * 0.12f, w * 0.96f, cuerpo.top - sube + 1 * u)
        drawRect(t(NARANJA_OSC, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(ROJO, oscuro), Offset(tapa.left, tapa.top + tapa.height * 0.35f), Size(tapa.width, tapa.height * 0.3f))
        val nb = 7
        for (j in 0 until nb) drawCircle(t(if (j % 2 == 0) VERDE else AZUL_OSC, oscuro), tapa.height * 0.12f, Offset(tapa.left + tapa.width * (j + 0.5f) / nb, tapa.center.y))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(tapa.left, tapa.bottom - 2 * u), Size(tapa.width, 2 * u))
        // El panel crema con el tema.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(CREMA, oscuro), r.topLeft, r.size)
        drawRect(t(AZUL, oscuro), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u), style = Stroke(1f * u))
    }

    override val rotulo = Rect(0.24f, 0.52f, 0.76f, 0.8f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.filosofo
    override val salida = 0f
    override val placa = Rect(0.3f, 0.38f, 0.7f, 0.92f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Fieltro blanco, acolchado con pespuntes rojos en rombos y espirales.
        val f = if (oscuro) mezcla(FIELTRO, Color.Black, 0.6f) else FIELTRO
        drawRect(f)
        val pesp = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u))
        val p = 60 * u
        var d0 = -h
        while (d0 < w + h) {
            drawLine(ROJO.copy(alpha = 0.3f), Offset(d0, 0f), Offset(d0 + h, h), 1.2f * u, pathEffect = pesp)
            drawLine(ROJO.copy(alpha = 0.3f), Offset(d0 + h, 0f), Offset(d0, h), 1.2f * u, pathEffect = pesp)
            d0 += p
        }
        var y = p / 2
        while (y < h) {
            var x = p / 2
            while (x < w) { voluta(Offset(x, y), p * 0.16f, ROJO.copy(alpha = 0.28f), 1.2f * u); x += p }
            y += p
        }
        // El marco pintado de naranja, con su filete azul.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(NARANJA, oscuro), r.topLeft, r.size) }
        drawRect(t(AZUL, oscuro), Offset(pl * 0.35f, pl * 0.35f), Size(w - pl * 0.7f, h - pl * 0.7f), style = Stroke(2 * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.25f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) CREMA else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 22, 0.012f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(ABEDUL.copy(alpha = 0.55f))
        // Las lenticelas: rayitas oscuras horizontales, y alguna veta parda.
        val n = if (grande) 60 else 26
        for (k in 0 until n) {
            val x = w * azar(semilla, k); val y = h * azar(semilla, k + 100)
            val l = (5f + 12f * azar(semilla, k + 200)) * u * g
            drawLine(LENTICELA.copy(alpha = 0.18f + 0.14f * azar(semilla, k + 300)), Offset(x, y), Offset(x + l, y), (0.8f + 0.8f * azar(semilla, k + 400)) * u)
        }
        for (k in 0 until 3) {
            val y = h * (0.2f + 0.3f * k + 0.1f * azar(semilla, k + 500))
            drawLine(Color(0xFF9C7A4E).copy(alpha = 0.12f), Offset(0f, y), Offset(w, y + 3 * u), 2.5f * u * g)
        }
        // La corteza que se levanta en una esquina.
        val e = 12 * u * g
        drawPath(Path().apply { moveTo(w - e, 0f); lineTo(w, 0f); lineTo(w, e); close() }, Color(0xFFB8926A).copy(alpha = 0.6f))
        drawLine(Color.Black.copy(alpha = 0.2f), Offset(w - e, 0f), Offset(w, e), 0.8f * u)
    }

    override val margenes = MargenesFicha(13.dp, 13.dp, 12.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 1.05f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sol sobre la luna creciente.
            "convence" -> {
                drawCircle(ORO, s * 0.16f, Offset(c.x, c.y - s * 0.24f))
                drawArc(ORO, 0f, 180f, false, Offset(c.x - s * 0.3f, c.y - s * 0.2f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.12f, cap = StrokeCap.Round))
            }
            // Un nudo sin fin, simplificado: un rombo con un lazo en cada punta.
            "piensa" -> {
                drawPath(estrella(c, s * 0.3f, 2, 1f, 0f), AZUL, style = Stroke(s * 0.08f))
                drawRect(AZUL, Offset(c.x - s * 0.2f, c.y - s * 0.2f), Size(s * 0.4f, s * 0.4f), style = Stroke(s * 0.08f))
                listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { o -> drawCircle(AZUL, s * 0.1f, Offset(c.x + o.x * s * 0.34f, c.y + o.y * s * 0.34f), style = Stroke(s * 0.07f)) }
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un jadag azul: el pañuelo de seda anudado. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawOval(AZUL, Offset(c.x - s * 0.36f, c.y - s * 0.44f), Size(s * 0.72f, s * 0.5f), style = Stroke(s * 0.12f))
        drawPath(Path().apply { moveTo(c.x - s * 0.12f, c.y); lineTo(c.x - s * 0.3f, c.y + s * 0.48f); lineTo(c.x - s * 0.06f, c.y + s * 0.4f); close() }, AZUL)
        drawPath(Path().apply { moveTo(c.x + s * 0.12f, c.y); lineTo(c.x + s * 0.3f, c.y + s * 0.48f); lineTo(c.x + s * 0.06f, c.y + s * 0.4f); close() }, AZUL)
        drawCircle(AZUL_OSC, s * 0.1f, Offset(c.x, c.y + s * 0.02f))
    }

    override val desorden = 2f
}
