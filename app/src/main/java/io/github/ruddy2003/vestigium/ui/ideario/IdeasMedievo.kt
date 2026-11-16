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
            // El escudo rojo con su palomita.
            "hecho" -> {
                drawPath(escudo(c, s * 0.7f, s * 0.86f), ROJO)
                drawPath(Path().apply { moveTo(c.x - s * 0.18f, c.y - s * 0.02f); lineTo(c.x - s * 0.04f, c.y + s * 0.13f); lineTo(c.x + s * 0.2f, c.y - s * 0.16f) }, BLANCO, style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            }
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
