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
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Moderna
// =====================================================================

/** Un rectángulo de [w] por [h] con el borde de abajo rasgado en [dientes] picos de hasta [hondo]. */
private fun rasgado(w: Float, h: Float, dientes: Int, hondo: Float, semilla: Int) = Path().apply {
    moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - hondo)
    for (k in dientes downTo 0) lineTo(w * k / dientes, h - hondo * (if (k % 2 == 0) 1f else 0.2f + 0.4f * azar(semilla, k)))
    close()
}

/**
 * Revolución: el estante de la sección. Una pared de yeso con decretos pegados, estantes de pino con escuadras de hierro y,
 * arriba, colgaduras tricolores con escarapelas; el piso es de baldosas hexagonales de barro (tomettes). Cada tema es una
 * caja de pino de listones, atada con una cinta tricolor, con los cuadernos de quejas asomando por arriba y el tema en un
 * cartel impreso pegado al frente. Por dentro, listones forrados de papel de periódico. Las fichas son pasquines: papel de
 * imprenta con su doble filete y el borde de abajo rasgado.
 */
object IdeasRevolucion : DisenoIdeas {
    private val PINO = Color(0xFFB08A5A)
    private val PINO_LUZ = Color(0xFFCBA676)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val AZUL = Color(0xFF1F3A6E)
    private val ROJO = Color(0xFFB22234)
    private val BLANCO = Color(0xFFF4F0E6)
    private val PAPEL = Color(0xFFEFE8D6)
    private val NEGRO = Color(0xFF1C1A17)
    private val YESO = Color(0xFFE3D9C2)
    private val YESO_OSC = Color(0xFFC9BC9E)
    private val TOMETTE = Color(0xFFA8442A)
    private val TOMETTE_OSC = Color(0xFF6E2A18)
    private val HIERRO = Color(0xFF2A2826)
    private val CUADERNOS = listOf(Color(0xFF8C9AB0), Color(0xFFD8B87A), Color(0xFFD9A6A0), Color(0xFFE8E0CC), Color(0xFF9CB094))

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.4f
    override val corona = 0.25f
    override val base = 0.11f
    override val lado = 0.075f
    override val hueco = 0.06f

    /** Una escarapela: azul afuera, blanco y rojo al centro, con su plisado. */
    private fun DrawScope.escarapela(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(t(AZUL, oscuro), r, c)
        drawCircle(t(BLANCO, oscuro), r * 0.66f, c)
        drawCircle(t(ROJO, oscuro), r * 0.36f, c)
        for (k in 0 until 16) {
            val a = k * Math.PI / 8
            drawLine(Color.Black.copy(alpha = 0.15f), Offset(c.x + r * 0.4f * cos(a).toFloat(), c.y + r * 0.4f * sin(a).toFloat()), Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), 0.6f * density)
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.35f
        // La pared de yeso, con decretos pegados.
        drawRect(t(YESO, oscuro), Offset.Zero, Size(w, piso))
        for (k in 0 until 7) {
            val c = Offset(w * azar(41, k), d.corona.bottom + (piso - d.corona.bottom) * azar(42, k))
            rotate(-4f + 8f * azar(43, k), c) {
                val r = Rect(c.x - 24 * u, c.y - 32 * u, c.x + 24 * u, c.y + 32 * u)
                drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
                drawRect(t(NEGRO, oscuro), Offset(r.left + 4 * u, r.top + 5 * u), Size(r.width - 8 * u, 3 * u))
                for (j in 0 until 8) drawLine(t(NEGRO, oscuro).copy(alpha = 0.3f), Offset(r.left + 5 * u, r.top + 14 * u + j * 6 * u), Offset(r.right - 5 * u, r.top + 14 * u + j * 6 * u), 1f * u)
            }
        }
        // Los estantes de pino, con sus escuadras de hierro, y los costados.
        listOf(w * 0.015f, w * 0.985f - w * 0.035f).forEach { x ->
            drawRect(t(PINO_OSC, oscuro), Offset(x, d.corona.bottom - 4 * u), Size(w * 0.035f, piso - d.corona.bottom + 4 * u))
            vetas(Rect(x, d.corona.bottom, x + w * 0.035f, piso), Color.Black.copy(alpha = 0.2f), (x / u).toInt(), 0.6f, vertical = true, grosor = 0.5f)
        }
        d.filas.forEach { f ->
            val r = Rect(w * 0.015f, f.bottom - 1 * u, w * 0.985f, f.bottom + maxOf(d.separacion * 0.4f, 7 * u))
            drawRect(t(PINO_LUZ, oscuro), r.topLeft, r.size)
            vetas(r, PINO_OSC.copy(alpha = 0.35f), (f.top / u).toInt(), 0.6f, grosor = 0.5f, ondulacion = 0.4f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 2.5f * u))
            listOf(w * 0.2f, w * 0.8f).forEach { x ->
                drawPath(Path().apply { moveTo(x - 1 * u, r.bottom); lineTo(x + 1 * u, r.bottom); lineTo(x + 1 * u, r.bottom + 14 * u); lineTo(x - 1 * u, r.bottom + 14 * u); close() }, t(HIERRO, oscuro))
                drawLine(t(HIERRO, oscuro), Offset(x, r.bottom + 12 * u), Offset(x + 12 * u, r.bottom), 1.6f * u)
            }
        }
        // El tablón de arriba y las colgaduras tricolores, con escarapelas en cada sujeción.
        val tablon = Rect(0f, d.corona.top + ch * 0.08f, w, d.corona.top + ch * 0.26f)
        drawRect(t(PINO, oscuro), tablon.topLeft, tablon.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tablon.bottom), Size(w, 2 * u))
        val n = 3
        val sw = w / n
        for (j in 0 until n) {
            val x0 = sw * j; val x1 = sw * (j + 1)
            val y0 = tablon.bottom
            listOf(ROJO to 0.95f, BLANCO to 0.7f, AZUL to 0.45f).forEach { (col, f) ->
                val hondo = (d.corona.bottom - y0) * f
                drawPath(Path().apply { moveTo(x0, y0); quadraticTo((x0 + x1) / 2, y0 + hondo * 2, x1, y0); close() }, t(col, oscuro))
            }
            for (k in 1..4) {
                val f = k / 5f
                drawLine(Color.Black.copy(alpha = 0.1f), Offset(x0 + sw * f, y0), Offset(x0 + sw * f, y0 + (d.corona.bottom - y0) * 0.8f * (1 - kotlin.math.abs(f - 0.5f) * 1.2f)), 1f * u)
            }
        }
        for (j in 0..n) escarapela(Offset(sw * j, tablon.bottom + 2 * u), ch * (if (j == 0 || j == n) 0.12f else 0.15f), oscuro)
        // El piso de tomettes, hexágonos de barro.
        val zoc = Rect(0f, piso, w, d.base.bottom)
        drawRect(t(TOMETTE_OSC, oscuro), zoc.topLeft, zoc.size)
        clipRect(zoc.left, zoc.top, zoc.right, zoc.bottom) {
            val r = zoc.height * 0.38f
            var fila = 0
            var cy = zoc.top
            while (cy < zoc.bottom + r) {
                var cx = if (fila % 2 == 0) 0f else r * 0.866f * 1.04f
                while (cx < w + r) {
                    drawPath(estrella(Offset(cx, cy), r * 0.96f, 3, 1f, -90f), t(mezcla(TOMETTE, TOMETTE_OSC, 0.25f * azar(fila, (cx / r).toInt())), oscuro))
                    cx += r * 1.732f * 1.04f
                }
                cy += r * 1.5f; fila++
            }
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, zoc.top), Size(w, 2 * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(NEGRO, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: unos periódicos doblados, apilados.
            for (k in 0 until 3) {
                val r = Rect(w * (0.22f + 0.03f * k), h * (0.86f - 0.07f * k), w * (0.74f + 0.02f * k), h * (0.93f - 0.07f * k))
                drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
                drawRect(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(r.left, r.bottom - 1 * u), Size(r.width, 1 * u))
                for (j in 1 until 4) drawLine(t(NEGRO, oscuro).copy(alpha = 0.25f), Offset(r.left + r.width * j / 4, r.top + 2 * u), Offset(r.left + r.width * j / 4, r.bottom - 2 * u), 0.6f * u)
            }
            return
        }
        val caja = Rect(w * 0.08f, h * 0.38f, w * 0.92f, h * 0.95f)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(caja.left, caja.bottom - h * 0.03f), Size(caja.width, h * 0.06f))
        // Los cuadernos de quejas, de pie, que asoman por arriba (y suben al abrirla).
        drawRect(Color(0xFF1A120A), caja.topLeft, caja.size)
        val k = c.n.coerceIn(1, 8)
        val sube = h * 0.08f + h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        for (i in 0 until k) {
            val x = caja.left + 4 * u + (caja.width - 8 * u) * i / k
            val y = caja.top - sube * (0.5f + 0.5f * azar(c.semilla, i))
            drawRect(t(CUADERNOS[(i + c.semilla.mod(CUADERNOS.size)) % CUADERNOS.size], oscuro), Offset(x, y), Size((caja.width - 8 * u) / k - 1.5f * u, caja.top - y + h * 0.15f))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(x + 2 * u, y + 4 * u), Offset(x + (caja.width - 8 * u) / k - 4 * u, y + 4 * u), 0.8f * u)
        }
        // Los listones de pino del frente, con sus rendijas, y los montantes de las esquinas.
        val lis = 3
        val lh = caja.height / lis
        for (j in 0 until lis) {
            val r = Rect(caja.left, caja.top + lh * j + (if (j == 0) 0f else 2 * u), caja.right, caja.top + lh * (j + 1) - 2 * u)
            drawRect(t(PINO, oscuro), r.topLeft, r.size)
            vetas(r, PINO_OSC.copy(alpha = 0.4f), c.semilla + j, 0.7f, grosor = 0.5f, ondulacion = 0.5f)
        }
        listOf(caja.left, caja.right - 8 * u).forEach { x ->
            drawRect(t(PINO_OSC, oscuro), Offset(x, caja.top), Size(8 * u, caja.height))
            for (j in 0 until lis) drawCircle(t(HIERRO, oscuro), 1.1f * u, Offset(x + 4 * u, caja.top + lh * (j + 0.5f)))
        }
        // La cinta tricolor, atada con su lazo.
        val cx = w * 0.84f
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(t(col, oscuro), Offset(cx - 4.5f * u + i * 3 * u, caja.top), Size(3 * u, caja.height)) }
        val lazo = Offset(cx, caja.top + 4 * u)
        drawOval(t(ROJO, oscuro), Offset(lazo.x - 11 * u, lazo.y - 5 * u), Size(10 * u, 8 * u), style = Stroke(2.4f * u))
        drawOval(t(AZUL, oscuro), Offset(lazo.x + 1 * u, lazo.y - 5 * u), Size(10 * u, 8 * u), style = Stroke(2.4f * u))
        drawCircle(t(BLANCO, oscuro), 2.4f * u, lazo)
        // El cartel impreso pegado al frente, con su doble filete.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(NEGRO, oscuro), Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, 1.8f * u))
        drawRect(t(NEGRO, oscuro), Offset(r.left + 3 * u, r.top + 6 * u), Size(r.width - 6 * u, 0.6f * u))
    }

    override val rotulo = Rect(0.16f, 0.5f, 0.74f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = NEGRO
    override val letraRotulo: FontFamily = LetraExtra.convencion
    override val salida = 0f
    override val placa = Rect(0.32f, 0.4f, 0.68f, 0.92f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Listones de pino forrados de papel de periódico: columnas de renglones grises.
        drawRect(t(PINO, oscuro))
        val hoja = if (oscuro) mezcla(PAPEL, Color.Black, 0.58f) else PAPEL
        val pl = 14 * u
        val hojas = listOf(Rect(pl, pl, w * 0.62f, h * 0.48f), Rect(w * 0.45f, h * 0.3f, w - pl, h * 0.78f), Rect(pl, h * 0.55f, w * 0.6f, h - pl))
        hojas.forEachIndexed { i, r ->
            rotate(if (i == 1) 2f else -1.5f, r.center) {
                drawRect(hoja, r.topLeft, r.size)
                drawRect(NEGRO.copy(alpha = 0.4f), Offset(r.left + 8 * u, r.top + 8 * u), Size(r.width - 16 * u, 6 * u))
                val col = 4
                val cw = (r.width - 16 * u) / col
                for (j in 0 until col) {
                    var y = r.top + 22 * u
                    while (y < r.bottom - 8 * u) { drawLine(NEGRO.copy(alpha = 0.16f), Offset(r.left + 8 * u + cw * j + 2 * u, y), Offset(r.left + 8 * u + cw * (j + 1) - 4 * u, y), 1.2f * u); y += 5 * u }
                }
            }
        }
        // Los montantes de pino y las rendijas de la caja.
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(PINO_OSC, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else NEGRO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(rasgado(s.width, s.height, 22, minOf(s.width, s.height) * 0.035f, semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.45f))
        // El doble filete de imprenta arriba, y uno fino abajo.
        val m = 8 * u * g
        drawRect(NEGRO.copy(alpha = 0.85f), Offset(m, 4 * u * g), Size(w - 2 * m, 2.4f * u * g))
        drawRect(NEGRO.copy(alpha = 0.85f), Offset(m, 8 * u * g), Size(w - 2 * m, 0.7f * u * g))
        drawRect(NEGRO.copy(alpha = 0.5f), Offset(m, h - 12 * u * g), Size(w - 2 * m, 0.7f * u * g))
        // El engrudo en las esquinas de arriba.
        mancha(Offset(4 * u, 4 * u), 14 * u * g, Color.White.copy(alpha = 0.35f))
        mancha(Offset(w - 4 * u, 4 * u), 14 * u * g, Color.White.copy(alpha = 0.35f))
    }

    override val margenes = MargenesFicha(16.dp, 12.dp, 18.dp, 28.dp, 24.dp, 32.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La escarapela.
            "hecho" -> { drawCircle(AZUL, s * 0.44f, c); drawCircle(BLANCO, s * 0.3f, c); drawCircle(ROJO, s * 0.16f, c) }
            // Las tres franjas: azul, blanco y rojo.
            "convence" -> {
                listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, col -> drawRect(col, Offset(c.x - s * 0.39f + i * s * 0.26f, c.y - s * 0.3f), Size(s * 0.26f, s * 0.6f)) }
                drawRect(NEGRO.copy(alpha = 0.4f), Offset(c.x - s * 0.39f, c.y - s * 0.3f), Size(s * 0.78f, s * 0.6f), style = Stroke(s * 0.04f))
            }
            // Una pluma de ave.
            "piensa" -> {
                drawLine(NEGRO, Offset(c.x - s * 0.36f, c.y + s * 0.42f), Offset(c.x + s * 0.3f, c.y - s * 0.38f), s * 0.05f, StrokeCap.Round)
                drawPath(Path().apply { moveTo(c.x - s * 0.1f, c.y + s * 0.08f); quadraticTo(c.x - s * 0.12f, c.y - s * 0.4f, c.x + s * 0.34f, c.y - s * 0.44f); quadraticTo(c.x + s * 0.24f, c.y - s * 0.02f, c.x - s * 0.1f, c.y + s * 0.08f); close() }, NEGRO.copy(alpha = 0.75f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una escarapela tricolor. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension
        escarapela(center, s * 0.46f, false)
    }

    override val desorden = 2.5f
}
