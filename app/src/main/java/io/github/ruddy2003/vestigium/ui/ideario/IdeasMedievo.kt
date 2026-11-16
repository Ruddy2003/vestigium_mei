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

/**
 * «Hecho comprobado»: un sello lleno de [color] con la palomita recortada en [hueco] (el papel). Más fuerte que «me
 * convence»: no es una opinión, está comprobado. [cuadrado]: sello cuadrado en lugar de redondo.
 */
internal fun DrawScope.selloHecho(color: Color, hueco: Color, cuadrado: Boolean = false) {
    val s = size.minDimension; val c = center
    if (cuadrado) drawRect(color, Offset(c.x - s * 0.38f, c.y - s * 0.38f), Size(s * 0.76f, s * 0.76f)) else drawCircle(color, s * 0.42f, c)
    drawPath(Path().apply { moveTo(c.x - s * 0.2f, c.y + s * 0.01f); lineTo(c.x - s * 0.05f, c.y + s * 0.16f); lineTo(c.x + s * 0.22f, c.y - s * 0.15f) },
        hueco, style = Stroke(s * 0.1f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
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
            // El sello pendiente del documento.
            "hecho" -> selloHecho(LAPIS, p.color)
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
