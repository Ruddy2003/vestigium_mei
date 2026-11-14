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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
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
//  Edad Contemporánea
// =====================================================================

/** Un engrane: dientes cuadrados alrededor de [c]. */
internal fun engrane(c: Offset, r: Float, dientes: Int): Path = Path().apply {
    val n = dientes * 4
    for (k in 0 until n) {
        val a = 2 * Math.PI * k / n
        val rr = if ((k / 2) % 2 == 0) r else r * 0.8f
        val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Una carátula de manómetro: aro, marcas y aguja ([valor] de 0 a 1). */
internal fun DrawScope.manometro(c: Offset, r: Float, aro: Color, cara: Color, aguja: Color, valor: Float = 0.62f) {
    val u = density
    drawCircle(aro, r, c)
    drawCircle(cara, r * 0.8f, c)
    for (k in 0..10) {
        val a = Math.toRadians(135.0 + k * 27.0)
        val p0 = Offset(c.x + r * 0.62f * cos(a).toFloat(), c.y + r * 0.62f * sin(a).toFloat())
        val p1 = Offset(c.x + r * 0.76f * cos(a).toFloat(), c.y + r * 0.76f * sin(a).toFloat())
        drawLine(Color.Black.copy(alpha = 0.6f), p0, p1, (if (k % 5 == 0) 1.4f else 0.7f) * u)
    }
    val a = Math.toRadians(135.0 + 270.0 * valor)
    drawLine(aguja, c, Offset(c.x + r * 0.66f * cos(a).toFloat(), c.y + r * 0.66f * sin(a).toFloat()), 1.4f * u, StrokeCap.Round)
    drawCircle(aro, r * 0.1f, c)
}

/**
 * Urbe: la edícola, el quiosco de periódicos de hierro verde con su techo de zinc, su letrero, el lambrequín festoneado y los
 * diarios colgados de los costados, sobre un piso de adoquines. Cada tema es una cajita de lata roja para boletos, con su
 * ranura de la que asoman los boletos de colores y el tema en una placa esmaltada. Por dentro, lata litografiada. Las fichas
 * son boletos de tranvía con las casillas para perforar a los lados.
 */
object IdeasUrbe : DisenoIdeas {
    private val VERDE = Color(0xFF2F4A36)
    private val VERDE_LUZ = Color(0xFF4E6E52)
    private val VERDE_OSC = Color(0xFF1C2E22)
    private val CREMA = Color(0xFFF2E8CF)
    private val HIERRO = Color(0xFF1E2420)
    private val ZINC = Color(0xFF8C9490)
    private val ZINC_OSC = Color(0xFF5E6662)
    private val PERIODICO = Color(0xFFE4DCC6)
    private val TINTA = Color(0xFF2B2420)
    private val ROSA = Color(0xFFE8B4A8)
    private val VERDE_BOLETO = Color(0xFFB8D0A8)
    private val OCRE = Color(0xFFE0C47A)
    private val ADOQUIN = Color(0xFF7A746C)
    private val ADOQUIN_OSC = Color(0xFF4E4A44)
    private val LATA = Color(0xFFB0322A)
    private val LATA_OSC = Color(0xFF7A1E18)
    private val ORO = Color(0xFFC9A24C)
    private val BRONCE = Color(0xFFA8783A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 1f
    override val corona = 0.3f
    override val base = 0.11f
    override val lado = 0.09f
    override val hueco = 0.035f

    /** Un diario doblado, colgado: la cabecera negra y las columnas en rayitas. */
    private fun DrawScope.diario(c: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        val u = density
        drawRect(t(PERIODICO, oscuro), Offset(c.x - ancho / 2, c.y), Size(ancho, alto))
        drawRect(t(TINTA, oscuro), Offset(c.x - ancho * 0.4f, c.y + alto * 0.08f), Size(ancho * 0.8f, alto * 0.08f))
        for (j in 0 until 6) drawLine(t(TINTA, oscuro).copy(alpha = 0.3f), Offset(c.x - ancho * 0.4f, c.y + alto * (0.28f + 0.11f * j)), Offset(c.x + ancho * 0.4f, c.y + alto * (0.28f + 0.11f * j)), 0.8f * u)
        drawCircle(t(HIERRO, oscuro), 1.6f * u, Offset(c.x, c.y))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.4f
        // Los adoquines (sampietrini), en hiladas alternadas.
        drawRect(t(ADOQUIN_OSC, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        val la = 7 * u
        var y = piso; var fila = 0
        while (y < d.base.bottom) {
            var x = if (fila % 2 == 0) 0f else -la / 2
            var k = 0
            while (x < w) { drawRoundRect(t(mezcla(ADOQUIN_OSC, ADOQUIN, 0.4f + 0.6f * azar(fila, k)), oscuro), Offset(x + 0.8f * u, y + 0.8f * u), Size(la - 1.6f * u, la - 1.6f * u), CornerRadius(1.5f * u)); x += la; k++ }
            y += la; fila++
        }
        // El cuerpo de hierro verde, con sus columnas en los costados y un diario colgado en cada una.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, piso)
        drawRect(t(VERDE, oscuro), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { r -> drawRect(t(VERDE_OSC, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u)) }
        d.filas.forEach { f ->
            drawLine(t(HIERRO, oscuro), Offset(cuerpo.left, f.bottom + 3 * u), Offset(cuerpo.right, f.bottom + 3 * u), 2 * u)
            drawLine(t(VERDE_LUZ, oscuro), Offset(cuerpo.left, f.bottom + 1 * u), Offset(cuerpo.right, f.bottom + 1 * u), 1 * u)
        }
        listOf(cuerpo.left, cuerpo.right - w * 0.04f).forEach { x ->
            val col = Rect(x, cuerpo.top, x + w * 0.04f, piso)
            drawRect(t(VERDE_LUZ, oscuro), col.topLeft, col.size)
            drawLine(t(VERDE_OSC, oscuro), Offset(col.center.x, col.top), Offset(col.center.x, col.bottom), 1.2f * u)
            drawRect(t(VERDE_OSC, oscuro), Offset(col.left - 2 * u, col.top), Size(col.width + 4 * u, 5 * u))
            drawRect(t(VERDE_OSC, oscuro), Offset(col.left - 2 * u, col.bottom - 6 * u), Size(col.width + 4 * u, 6 * u))
            var dy = cuerpo.top + 20 * u
            while (dy < piso - 70 * u) { diario(Offset(col.center.x, dy), w * 0.06f, w * 0.085f, oscuro); dy += w * 0.2f }
        }
        // El techo de zinc, curvo, con sus juntas y un remate.
        val techo = Path().apply { moveTo(0f, d.corona.top + ch * 0.5f); quadraticTo(w / 2, d.corona.top - ch * 0.12f, w, d.corona.top + ch * 0.5f); close() }
        drawPath(techo, t(ZINC, oscuro))
        clipPath(techo) { var x = w * 0.05f; while (x < w) { drawLine(t(ZINC_OSC, oscuro), Offset(x, d.corona.top), Offset(w / 2 + (x - w / 2) * 1.3f, d.corona.top + ch * 0.5f), 1f * u); x += w * 0.05f } }
        drawCircle(t(VERDE, oscuro), 4.5f * u, Offset(w / 2, d.corona.top + ch * 0.17f))
        // El letrero crema, con su rótulo pintado (letras de lejos), y el lambrequín festoneado.
        val letrero = Rect(w * 0.04f, d.corona.top + ch * 0.48f, w * 0.96f, d.corona.top + ch * 0.74f)
        drawRect(t(VERDE, oscuro), letrero.topLeft, letrero.size)
        val placaL = Rect(letrero.left + 5 * u, letrero.top + 4 * u, letrero.right - 5 * u, letrero.bottom - 4 * u)
        drawRect(t(CREMA, oscuro), placaL.topLeft, placaL.size)
        var lx = placaL.left + placaL.width * 0.18f; var q = 0
        while (lx < placaL.right - placaL.width * 0.18f) {
            if (q % 7 != 6) drawRect(t(VERDE_OSC, oscuro), Offset(lx, placaL.top + placaL.height * 0.25f), Size(placaL.width * 0.035f, placaL.height * 0.5f))
            lx += placaL.width * 0.05f; q++
        }
        val fest = Rect(0f, letrero.bottom, w, d.corona.bottom - 6 * u)
        val nf = 16
        for (j in 0 until nf) {
            val x0 = w * j / nf
            drawArc(t(if (j % 2 == 0) VERDE else CREMA, oscuro), 0f, 180f, true, Offset(x0, fest.top - fest.height), Size(w / nf, fest.height * 2))
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRoundRect(t(CREMA, oscuro), r.topLeft, r.size, CornerRadius(3 * u))
            drawRoundRect(t(VERDE, oscuro), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u), CornerRadius(2 * u), style = Stroke(1.2f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un diario enrollado.
            val r = Rect(w * 0.2f, h * 0.8f, w * 0.8f, h * 0.95f)
            drawRoundRect(t(PERIODICO, oscuro), r.topLeft, r.size, CornerRadius(r.height / 2))
            drawOval(t(mezcla(PERIODICO, TINTA, 0.2f), oscuro), Offset(r.right - r.height * 0.6f, r.top), Size(r.height * 0.6f, r.height), style = Stroke(1f * u))
            drawRect(t(TINTA, oscuro).copy(alpha = 0.6f), Offset(r.left + r.width * 0.2f, r.top + 2 * u), Size(r.width * 0.3f, 2 * u))
            return
        }
        val cuerpo = Rect(w * 0.1f, h * 0.4f, w * 0.9f, h * 0.95f)
        val sube = h * 0.01f * c.n.coerceIn(0, 6) + ab * h * 0.14f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left, cuerpo.bottom - h * 0.03f), Size(cuerpo.width, h * 0.06f))
        // Los boletos que asoman por la ranura, en abanico (más altos al abrirla).
        val tapa = Rect(w * 0.08f, cuerpo.top - sube - h * 0.1f, w * 0.92f, cuerpo.top - sube + 1 * u)
        val k = c.n.coerceIn(1, 7)
        val colores = listOf(ROSA, VERDE_BOLETO, OCRE, CREMA)
        for (i in 0 until k) {
            val x = w * 0.5f + (i - (k - 1) / 2f) * w * 0.07f
            rotate((i - (k - 1) / 2f) * 7f, Offset(x, tapa.center.y)) {
                val alto = h * (0.12f + 0.03f * azar(c.semilla, i)) + ab * h * 0.08f
                drawRect(t(colores[(i + c.semilla.mod(4)) % 4], oscuro), Offset(x - w * 0.05f, tapa.center.y - alto), Size(w * 0.1f, alto))
                drawLine(t(TINTA, oscuro).copy(alpha = 0.3f), Offset(x - w * 0.035f, tapa.center.y - alto + 3 * u), Offset(x + w * 0.035f, tapa.center.y - alto + 3 * u), 0.8f * u)
            }
        }
        // La lata roja, con sus franjas litografiadas y sus filetes dorados.
        drawRect(t(LATA, oscuro), cuerpo.topLeft, cuerpo.size)
        listOf(cuerpo.top + 4 * u, cuerpo.bottom - 9 * u).forEach { yy ->
            drawRect(t(CREMA, oscuro), Offset(cuerpo.left, yy), Size(cuerpo.width, 5 * u))
            var x = cuerpo.left + 2 * u
            while (x < cuerpo.right) { drawRect(t(LATA, oscuro), Offset(x, yy + 1.5f * u), Size(2 * u, 2 * u)); x += 5 * u }
        }
        drawRect(t(ORO, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1.2f * u))
        // La tapa con la ranura.
        drawRect(t(LATA_OSC, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(ORO, oscuro), Offset(tapa.left, tapa.bottom - 2 * u), Size(tapa.width, 2 * u))
        drawRoundRect(Color(0xFF140808), Offset(tapa.center.x - tapa.width * 0.3f, tapa.center.y - 1.5f * u), Size(tapa.width * 0.6f, 3 * u), CornerRadius(1.5f * u))
        // La placa esmaltada con el tema, con sus cuatro remaches.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size, CornerRadius(3 * u))
        drawRoundRect(t(CREMA, oscuro), r.topLeft, r.size, CornerRadius(3 * u))
        drawRoundRect(t(VERDE, oscuro), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u), CornerRadius(2 * u), style = Stroke(1.2f * u))
        listOf(Offset(r.left + 4 * u, r.top + 4 * u), Offset(r.right - 4 * u, r.top + 4 * u), Offset(r.left + 4 * u, r.bottom - 4 * u), Offset(r.right - 4 * u, r.bottom - 4 * u)).forEach { drawCircle(t(ZINC_OSC, oscuro), 1f * u, it) }
    }

    override val rotulo = Rect(0.14f, 0.52f, 0.86f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.placa
    override val salida = 0f
    override val placa = Rect(0.32f, 0.42f, 0.68f, 0.94f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Lata litografiada: rojo con una retícula de puntos crema, y los bordes enrollados.
        drawRect(t(LATA_OSC, oscuro))
        val p = 18 * u
        var y = p / 2; var fila = 0
        while (y < h) {
            var x = if (fila % 2 == 0) p / 2 else p
            while (x < w) { drawCircle(CREMA.copy(alpha = 0.14f), 2 * u, Offset(x, y)); x += p }
            y += p / 2; fila++
        }
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LATA, oscuro), r.topLeft, r.size) }
        drawRect(t(ORO, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.4f * u))
        drawRect(t(CREMA, oscuro).copy(alpha = 0.5f), Offset(pl * 0.4f, pl * 0.4f), Size(w - pl * 0.8f, h - pl * 0.8f), style = Stroke(1.2f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = CREMA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    /** El boleto: un rectángulo con dos muescas a media altura. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val r = minOf(s.width, s.height) * 0.06f
        op(Path().apply { addRect(Rect(0f, 0f, s.width, s.height)) },
            Path().apply { addOval(Rect(-r, s.height / 2 - r, r, s.height / 2 + r)); addOval(Rect(s.width - r, s.height / 2 - r, s.width + r, s.height / 2 + r)) },
            PathOperation.Difference)
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val tono = listOf(ROSA, VERDE_BOLETO, OCRE)[semilla.mod(3)]
        drawRect(tono.copy(alpha = 0.35f))
        // La línea de puntos del corte, arriba.
        var x = 4 * u
        while (x < w) { drawCircle(TINTA.copy(alpha = 0.3f), 0.8f * u * g, Offset(x, 3 * u * g)); x += 4 * u * g }
        // Las casillas de las paradas a los dos lados, y una perforada.
        val cs = 5 * u * g
        val n = ((h - 16 * u * g) / (cs * 1.5f)).toInt().coerceAtLeast(2)
        val perforada = semilla.mod(n)
        for (k in 0 until n) {
            val y = 10 * u * g + k * cs * 1.5f
            listOf(2 * u * g, w - 2 * u * g - cs).forEachIndexed { lado, xx ->
                drawRect(TINTA.copy(alpha = 0.35f), Offset(xx, y), Size(cs, cs), style = Stroke(0.6f * u))
                if (lado == 1 && k == perforada) drawCircle(Color.Black.copy(alpha = 0.55f), cs * 0.32f, Offset(xx + cs / 2, y + cs / 2))
            }
        }
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 12.dp, 24.dp, 26.dp, 24.dp)
    override val proporcionFicha = 1.15f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El timbro de la oficina.
            "hecho" -> selloHecho(VERDE, p.color, cuadrado = true)
            // La perforación del revisor.
            "convence" -> { drawCircle(TINTA, s * 0.3f, c); drawCircle(TINTA, s * 0.42f, c, style = Stroke(s * 0.06f)) }
            // El reloj de la estación.
            "piensa" -> {
                drawCircle(VERDE, s * 0.4f, c, style = Stroke(s * 0.09f))
                drawLine(VERDE, c, Offset(c.x, c.y - s * 0.26f), s * 0.08f, StrokeCap.Round)
                drawLine(VERDE, c, Offset(c.x + s * 0.18f, c.y + s * 0.06f), s * 0.08f, StrokeCap.Round)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LATA)
            else -> {}
        }
    }

    /** Un gettone: la ficha de bronce con su ranura. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(BRONCE, s * 0.44f, c)
        drawCircle(mezcla(BRONCE, Color.Black, 0.3f), s * 0.36f, c, style = Stroke(s * 0.05f))
        drawLine(mezcla(BRONCE, Color.Black, 0.45f), Offset(c.x - s * 0.2f, c.y), Offset(c.x + s * 0.2f, c.y), s * 0.1f, StrokeCap.Round)
    }

    override val desorden = 2f
}

/**
 * Terminal: la ventana de la consola. Arriba, la barra de título, las pestañas y el prompt con su cursor; a la izquierda, el
 * árbol de directorios que baja hasta cada fila; abajo, la barra de estado. Cada tema es un directorio dibujado en cian, con
 * sus archivos asomando, que se enciende al abrirlo. Por dentro, la pantalla negra con sus líneas de barrido y los números de
 * línea. Las fichas son archivos en su ventana, con el nombre en la barra de título.
 */
object IdeasCiber : DisenoIdeas {
    private val NEGRO = Color(0xFF030708)
    private val PANEL = Color(0xFF071216)
    private val CIAN = Color(0xFF00E5FF)
    private val CIAN_OSC = Color(0xFF0A6E7A)
    private val TEXTO = Color(0xFFBDEFF5)
    private val TENUE = Color(0xFF3E7880)
    private val MAGENTA = Color(0xFFFF4F8B)
    private val AMBAR = Color(0xFFFFB84F)

    override val cols = 2
    override val proporcion = 1.6f
    override val corona = 0.2f
    override val base = 0.09f
    override val lado = 0.12f
    override val hueco = 0.05f

    /** Rayitas que se leen como texto de lejos: [n] palabras de largo al azar. */
    private fun DrawScope.texto(x0: Float, y: Float, n: Int, color: Color, semilla: Int) {
        val u = density
        var x = x0
        for (k in 0 until n) {
            val l = (6f + 16f * azar(semilla, k)) * u
            drawLine(color, Offset(x, y), Offset(x + l, y), 2 * u)
            x += l + 4 * u
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ch = d.corona.height
        drawRoundRect(NEGRO, Offset.Zero, size, CornerRadius(8 * u))
        // Las líneas de barrido.
        var y = 0f
        while (y < h) { drawLine(CIAN.copy(alpha = 0.035f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        // La barra de título, con los tres botones y el nombre.
        val barra = Rect(0f, 0f, w, ch * 0.36f)
        drawRect(PANEL, barra.topLeft, barra.size)
        listOf(MAGENTA, AMBAR, CIAN).forEachIndexed { i, col -> drawCircle(col.copy(alpha = 0.8f), barra.height * 0.16f, Offset(12 * u + i * barra.height * 0.45f, barra.center.y)) }
        texto(w * 0.38f, barra.center.y, 3, TENUE, 3)
        // La pestaña activa y el prompt con su cursor.
        val pest = Rect(8 * u, barra.bottom + 4 * u, w * 0.36f, ch * 0.62f)
        drawRect(CIAN_OSC.copy(alpha = 0.35f), pest.topLeft, pest.size)
        drawRect(CIAN, Offset(pest.left, pest.bottom - 1.5f * u), Size(pest.width, 1.5f * u))
        texto(pest.left + 8 * u, pest.center.y, 2, TEXTO.copy(alpha = 0.7f), 5)
        texto(pest.right + 12 * u, pest.center.y, 2, TENUE.copy(alpha = 0.6f), 9)
        val py = ch * 0.82f
        drawPath(Path().apply { moveTo(12 * u, py - 4 * u); lineTo(17 * u, py); lineTo(12 * u, py + 4 * u) }, CIAN, style = Stroke(1.6f * u))
        texto(24 * u, py, 2, TEXTO, 12)
        drawRect(CIAN, Offset(24 * u + 60 * u, py - 5 * u), Size(6 * u, 10 * u))
        // El árbol de directorios que baja por la izquierda y se abre a cada fila.
        val tx = w * 0.05f
        val ultima = d.filas.lastOrNull()?.center?.y ?: d.corona.bottom
        drawLine(TENUE, Offset(tx, d.corona.bottom - 2 * u), Offset(tx, ultima), 1.4f * u)
        d.filas.forEach { f ->
            drawLine(TENUE, Offset(tx, f.center.y), Offset(w * lado - 4 * u, f.center.y), 1.4f * u)
            drawCircle(CIAN, 2 * u, Offset(tx, f.center.y))
        }
        // La barra de estado, con su bloque cian a la izquierda.
        val est = Rect(0f, d.base.top + d.base.height * 0.2f, w, d.base.bottom - 4 * u)
        drawRect(PANEL, est.topLeft, est.size)
        drawRect(CIAN, est.topLeft, Size(w * 0.16f, est.height))
        texto(w * 0.74f, est.center.y, 2, TENUE, 21)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(CIAN.copy(alpha = 0.85f), r.topLeft, r.size)
        }
        drawRoundRect(CIAN_OSC, Offset(1 * u, 1 * u), Size(w - 2 * u, h - 2 * u), CornerRadius(8 * u), style = Stroke(1.4f * u))
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val l = w * 0.04f; val r = w * 0.96f; val top = h * 0.08f; val b = h * 0.94f
        val tabH = h * 0.12f
        val carpeta = Path().apply {
            moveTo(l, top + tabH); lineTo(l, top + 3 * u); lineTo(l + 3 * u, top); lineTo(l + w * 0.3f, top); lineTo(l + w * 0.36f, top + tabH)
            lineTo(r, top + tabH); lineTo(r, b); lineTo(l, b); close()
        }
        if (c.vacio) {
            drawPath(carpeta, TENUE.copy(alpha = 0.5f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 4 * u))))
            return
        }
        // El directorio: el contorno cian con su halo, y más encendido al abrirlo.
        drawPath(carpeta, PANEL)
        drawPath(carpeta, CIAN.copy(alpha = 0.06f + 0.18f * ab))
        drawPath(carpeta, CIAN.copy(alpha = 0.12f + 0.2f * ab), style = Stroke(6 * u))
        drawPath(carpeta, CIAN.copy(alpha = 0.85f), style = Stroke(1.4f * u))
        // Los archivos que tiene, como iconos chicos con la esquina doblada.
        val k = c.n.coerceIn(0, 8)
        for (i in 0 until k) {
            val x = l + w * 0.06f + i * w * 0.1f
            val y = top + tabH + h * 0.06f
            val fw = w * 0.07f; val fh = h * 0.16f
            drawPath(Path().apply { moveTo(x, y); lineTo(x + fw * 0.65f, y); lineTo(x + fw, y + fw * 0.35f); lineTo(x + fw, y + fh); lineTo(x, y + fh); close() }, TENUE.copy(alpha = 0.9f), style = Stroke(1f * u))
            drawLine(TENUE, Offset(x + fw * 0.2f, y + fh * 0.55f), Offset(x + fw * 0.8f, y + fh * 0.55f), 0.8f * u)
        }
        // El cursor, al abrirlo.
        if (ab > 0f) drawRect(CIAN.copy(alpha = ab), Offset(r - w * 0.1f, b - h * 0.16f), Size(w * 0.04f, h * 0.1f))
    }

    override val rotulo = Rect(0.08f, 0.48f, 0.9f, 0.88f)
    override fun tintaRotulo(oscuro: Boolean) = TEXTO
    override val letraRotulo: FontFamily = Letra.terminal
    override val salida = 0f
    override val placa = Rect(0.3f, 0.2f, 0.7f, 0.82f)
    override fun tintaPlaca(oscuro: Boolean) = NEGRO

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La pantalla negra con sus líneas de barrido, los números de línea y el marco.
        drawRect(NEGRO)
        var y = 0f
        while (y < h) { drawLine(CIAN.copy(alpha = 0.03f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        y = 24 * u
        while (y < h - 10 * u) { drawLine(TENUE.copy(alpha = 0.5f), Offset(10 * u, y), Offset(18 * u, y), 2 * u); y += 22 * u }
        drawLine(TENUE.copy(alpha = 0.4f), Offset(24 * u, 0f), Offset(24 * u, h), 1f * u)
        drawRect(CIAN_OSC, Offset(1 * u, 1 * u), Size(w - 2 * u, h - 2 * u), style = Stroke(1.4f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 28.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(3.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val barra = altoBarra(grande).toPx()
        // La barra de título en cian, con los tres botones.
        drawRect(CIAN.copy(alpha = 0.85f), Offset.Zero, Size(w, barra))
        listOf(0, 1, 2).forEach { k -> drawCircle(NEGRO.copy(alpha = 0.7f), barra * 0.17f, Offset(w - barra * (0.7f + k * 0.6f), barra / 2)) }
        // El canal de los números de línea y las líneas de barrido.
        val gx = 7 * u * g
        drawLine(TENUE.copy(alpha = 0.45f), Offset(gx, barra), Offset(gx, h), 0.8f * u)
        var y = barra + 8 * u * g
        while (y < h - 4 * u) { drawLine(TENUE.copy(alpha = 0.5f), Offset(2 * u * g, y), Offset(gx - 2 * u, y), 1.2f * u); y += 14 * u * g }
        y = barra
        while (y < h) { drawLine(CIAN.copy(alpha = 0.035f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        drawRect(CIAN_OSC, Offset.Zero, size, style = Stroke(1.2f * u))
    }

    override val margenes = MargenesFicha(0.dp, 14.dp, 10.dp, 0.dp, 24.dp, 22.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.75f
    override val barra = true
    override fun altoBarra(grande: Boolean): Dp = if (grande) 30.dp else 16.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // [OK] en vídeo inverso.
            "hecho" -> selloHecho(CIAN, p.color, cuadrado = true)
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y); lineTo(c.x - s * 0.1f, c.y + s * 0.28f); lineTo(c.x + s * 0.38f, c.y - s * 0.32f) }, CIAN, style = Stroke(s * 0.12f))
            "piensa" -> for (k in -1..1) drawRect(CIAN, Offset(c.x + k * s * 0.3f - s * 0.08f, c.y - s * 0.08f), Size(s * 0.16f, s * 0.16f))
            "duda" -> drawRect(TENUE, Offset(c.x - s * 0.3f, c.y - s * 0.3f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.1f))
            "desacuerdo" -> aspa(MAGENTA)
            else -> {}
        }
    }

    /** El cursor de bloque, encendido. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(CIAN.copy(alpha = 0.25f), Offset(c.x - s * 0.36f, c.y - s * 0.48f), Size(s * 0.72f, s * 0.96f))
        drawRect(CIAN, Offset(c.x - s * 0.24f, c.y - s * 0.38f), Size(s * 0.48f, s * 0.76f))
    }

    override val desorden = 0f
}

/**
 * Vapor: el gabinete de patentes. Caoba con flejes de latón remachados; arriba, un tubo de vapor con sus bridas, un manómetro
 * al centro, engranes y una válvula que suelta vapor; abajo, un zócalo de hierro con un volante. Cada tema es una gaveta de
 * latón remachada con un portatarjetas y un tirador; las más llenas quedan entreabiertas con sus tarjetas perforadas. Por
 * dentro, caoba con rieles de latón. Las fichas son tarjetas perforadas de esquina cortada.
 */
object IdeasVapor : DisenoIdeas {
    private val LATON = Color(0xFFC9A04A)
    private val LATON_LUZ = Color(0xFFE8CC7E)
    private val LATON_OSC = Color(0xFF8A6A24)
    private val COBRE = Color(0xFFB0663A)
    private val CAOBA = Color(0xFF5A2A1A)
    private val CAOBA_OSC = Color(0xFF2E140C)
    private val HIERRO = Color(0xFF2A2624)
    private val HIERRO_LUZ = Color(0xFF5A5450)
    private val CARTULINA = Color(0xFFE8DCB8)
    private val TINTA = Color(0xFF231B14)
    private val VIDRIO = Color(0xFFF4EFE0)
    private val ROJO = Color(0xFF9C3D24)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 2f
    override val corona = 0.3f
    override val base = 0.13f
    override val lado = 0.08f
    override val hueco = 0.03f

    private const val SALIDA = 0.18f

    /** Remaches a lo largo de una línea. */
    private fun DrawScope.remaches(a: Offset, b: Offset, paso: Float, color: Color) {
        val u = density
        val l = kotlin.math.hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt()
        for (k in 0..n) {
            val f = if (n == 0) 0f else k / n.toFloat()
            val p = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            drawCircle(color, 1.4f * u, p)
            drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(p.x - 0.4f * u, p.y - 0.4f * u))
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de caoba, con su marco de latón remachado.
        val cuerpo = Rect(w * 0.025f, d.corona.bottom - 6 * u, w * 0.975f, d.base.top + d.base.height * 0.3f)
        drawRect(t(CAOBA, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, CAOBA_OSC.copy(alpha = 0.45f), 29, 1f, grosor = 0.7f, ondulacion = 0.5f)
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u)) }
        drawRect(t(LATON, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(5 * u))
        remaches(Offset(cuerpo.left + 2.5f * u, cuerpo.top + 8 * u), Offset(cuerpo.left + 2.5f * u, cuerpo.bottom - 8 * u), 16 * u, t(LATON_OSC, oscuro))
        remaches(Offset(cuerpo.right - 2.5f * u, cuerpo.top + 8 * u), Offset(cuerpo.right - 2.5f * u, cuerpo.bottom - 8 * u), 16 * u, t(LATON_OSC, oscuro))
        // La tapa de latón y, encima, el tubo de vapor con sus bridas.
        val tapa = Rect(0f, d.corona.bottom - 12 * u, w, d.corona.bottom - 3 * u)
        drawRect(Brush.verticalGradient(listOf(t(LATON_LUZ, oscuro), t(LATON, oscuro), t(LATON_OSC, oscuro)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        val ty = d.corona.top + ch * 0.62f
        val tubo = 7 * u
        drawRect(t(COBRE, oscuro), Offset(w * 0.04f, ty - tubo / 2), Size(w * 0.92f, tubo))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(w * 0.04f, ty - tubo * 0.25f), Offset(w * 0.96f, ty - tubo * 0.25f), 1f * u)
        listOf(0.06f, 0.3f, 0.7f, 0.94f).forEach { f -> drawRect(t(LATON_OSC, oscuro), Offset(w * f - 3 * u, ty - tubo * 0.85f), Size(6 * u, tubo * 1.7f)) }
        listOf(0.12f, 0.88f).forEach { f -> drawRect(t(COBRE, oscuro), Offset(w * f - tubo / 2, ty), Size(tubo, tapa.top - ty)) }
        // Los engranes a los lados y el manómetro al centro.
        val rg = ch * 0.26f
        listOf(Offset(w * 0.24f, ty - ch * 0.12f) to rg, Offset(w * 0.33f, ty - ch * 0.3f) to rg * 0.6f, Offset(w * 0.78f, ty - ch * 0.16f) to rg * 0.85f).forEachIndexed { i, (c, r) ->
            drawPath(engrane(c, r, if (i == 1) 8 else 12), t(if (i == 1) COBRE else LATON_OSC, oscuro))
            drawCircle(t(LATON, oscuro), r * 0.5f, c, style = Stroke(r * 0.15f))
            drawCircle(t(HIERRO, oscuro), r * 0.16f, c)
        }
        manometro(Offset(w / 2, ty - ch * 0.18f), ch * 0.36f, t(LATON, oscuro), t(VIDRIO, oscuro), ROJO)
        // La válvula que suelta vapor.
        val v = Offset(w * 0.9f, ty - tubo / 2)
        drawRect(t(LATON_OSC, oscuro), Offset(v.x - 3 * u, v.y - 10 * u), Size(6 * u, 10 * u))
        for (k in 0 until 4) drawCircle(Color.White.copy(alpha = if (oscuro) 0.14f else 0.5f), (4f + 3f * k) * u, Offset(v.x + k * 5 * u, v.y - 16 * u - k * 7 * u))
        // El zócalo de hierro remachado, con un volante.
        val zoc = Rect(w * 0.01f, cuerpo.bottom, w * 0.99f, d.base.bottom)
        drawRect(t(HIERRO, oscuro), zoc.topLeft, zoc.size)
        remaches(Offset(zoc.left + 6 * u, zoc.top + 4 * u), Offset(zoc.right - 6 * u, zoc.top + 4 * u), 14 * u, t(HIERRO_LUZ, oscuro))
        val vol = Offset(w * 0.1f, zoc.center.y + 2 * u)
        val rv = zoc.height * 0.3f
        drawCircle(t(ROJO, oscuro), rv, vol, style = Stroke(2.4f * u))
        for (k in 0 until 4) { val a = k * Math.PI / 2 + 0.4; drawLine(t(ROJO, oscuro), vol, Offset(vol.x + rv * cos(a).toFloat(), vol.y + rv * sin(a).toFloat()), 1.6f * u) }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(LATON, oscuro), r.topLeft, r.size)
            drawRect(t(LATON_OSC, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1f * u))
            listOf(r.left + 4 * u, r.right - 4 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.4f * u, Offset(x, r.center.y)) }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Las gavetas más llenas quedan entreabiertas y asoman las tarjetas.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF0E0806), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val x = w * (0.08f + 0.84f * (i + 0.5f) / k) - w * 0.05f
                val y0 = arriba * (0.1f + 0.5f * azar(c.semilla, i)) + 1 * u
                drawRect(t(CARTULINA, oscuro), Offset(x, y0), Size(w * 0.1f, arriba + 4 * u - y0))
                for (j in 0 until 3) drawRect(t(TINTA, oscuro).copy(alpha = 0.7f), Offset(x + 2 * u + j * 4 * u, y0 + 3 * u), Size(1.6f * u, 3 * u))
            }
        }
        // El frente: una plancha de latón con su doble filete grabado y remaches en el borde.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRect(t(LATON, oscuro), f.topLeft, f.size)
        drawLine(t(LATON_LUZ, oscuro), Offset(f.left, f.top + 1 * u), Offset(f.right, f.top + 1 * u), 1.4f * u)
        drawRect(t(LATON_OSC, oscuro), Offset(f.left, f.bottom - 2 * u), Size(f.width, 2 * u))
        val fh = f.bottom - f.top
        drawRect(t(LATON_OSC, oscuro), Offset(f.left + 7 * u, f.top + 7 * u), Size(f.width - 14 * u, fh - 14 * u), style = Stroke(0.8f * u))
        drawRect(t(LATON_OSC, oscuro), Offset(f.left + 9.5f * u, f.top + 9.5f * u), Size(f.width - 19 * u, fh - 19 * u), style = Stroke(0.5f * u))
        remaches(Offset(f.left + 3.5f * u, f.top + 3.5f * u), Offset(f.right - 3.5f * u, f.top + 3.5f * u), 18 * u, t(LATON_OSC, oscuro))
        remaches(Offset(f.left + 3.5f * u, f.bottom - 3.5f * u), Offset(f.right - 3.5f * u, f.bottom - 3.5f * u), 18 * u, t(LATON_OSC, oscuro))
        // El portatarjetas y su tarjeta.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (!c.vacio) drawRect(t(CARTULINA, oscuro), r.topLeft, r.size)
        drawRect(t(LATON_OSC, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(2.4f * u))
        listOf(r.left - 5 * u, r.right + 5 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.8f * u, Offset(x, r.center.y)) }
        // El tirador de gota.
        val ti = Offset(w / 2, h * 0.8f + baja)
        val rt = minOf(w, h) * 0.08f
        drawCircle(t(LATON_OSC, oscuro), rt * 0.35f, Offset(ti.x - rt, ti.y - rt * 0.2f))
        drawCircle(t(LATON_OSC, oscuro), rt * 0.35f, Offset(ti.x + rt, ti.y - rt * 0.2f))
        drawArc(t(HIERRO, oscuro), 0f, 180f, false, Offset(ti.x - rt, ti.y - rt * 0.8f), Size(rt * 2, rt * 1.4f), style = Stroke(2 * u))
    }

    override val rotulo = Rect(0.26f, 0.16f, 0.74f, 0.62f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.caldera
    override val salida = SALIDA
    override val placa = Rect(0.32f, 0.36f, 0.68f, 0.86f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de caoba, con dos rieles de latón a lo largo.
        drawRect(t(CAOBA, oscuro))
        vetas(Rect(0f, 0f, w, h), CAOBA_OSC.copy(alpha = 0.4f), 71, 1.2f, vertical = true, grosor = 0.7f, ondulacion = 0.5f)
        listOf(w * 0.3f, w * 0.7f).forEach { x ->
            drawRect(t(LATON_OSC, oscuro), Offset(x - 3 * u, 0f), Size(6 * u, h))
            drawLine(t(LATON_LUZ, oscuro), Offset(x - 1.5f * u, 0f), Offset(x - 1.5f * u, h), 1f * u)
        }
        // Las paredes de latón remachadas.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LATON, oscuro), r.topLeft, r.size) }
        remaches(Offset(pl / 2, pl), Offset(pl / 2, h - pl), 20 * u, t(LATON_OSC, oscuro))
        remaches(Offset(w - pl / 2, pl), Offset(w - pl / 2, h - pl), 20 * u, t(LATON_OSC, oscuro))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = VIDRIO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    /** La tarjeta perforada, con la esquina de arriba a la izquierda cortada. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val k = minOf(s.width, s.height) * 0.1f
        moveTo(k, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(0f, k); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(CARTULINA.copy(alpha = 0.45f))
        // Dos bandas de perforaciones, arriba y abajo, con su patrón propio.
        val paso = 5.5f * u * g
        val hw = 1.8f * u * g; val hh = 3.4f * u * g
        listOf(4 * u * g, 9 * u * g, h - 12 * u * g).forEachIndexed { fila, y ->
            var x = minOf(w, h) * 0.12f; var k = 0
            while (x < w - 4 * u) {
                if (azar(semilla + fila * 31, k) < 0.55f) drawRect(TINTA.copy(alpha = 0.75f), Offset(x, y), Size(hw, hh))
                else drawRect(TINTA.copy(alpha = 0.12f), Offset(x, y), Size(hw, hh), style = Stroke(0.5f * u))
                x += paso; k++
            }
        }
        drawLine(ROJO.copy(alpha = 0.5f), Offset(0f, 14.5f * u * g), Offset(w, 14.5f * u * g), 0.7f * u)
    }

    override val margenes = MargenesFicha(18.dp, 12.dp, 16.dp, 32.dp, 24.dp, 28.dp)
    override val proporcionFicha = 1.3f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello de la patente, con sus dos cintas.
            "hecho" -> {
                drawPath(Path().apply { moveTo(c.x - s * 0.18f, c.y); lineTo(c.x - s * 0.3f, c.y + s * 0.46f); lineTo(c.x - s * 0.08f, c.y + s * 0.36f); close() }, LATON_OSC)
                drawPath(Path().apply { moveTo(c.x + s * 0.18f, c.y); lineTo(c.x + s * 0.3f, c.y + s * 0.46f); lineTo(c.x + s * 0.08f, c.y + s * 0.36f); close() }, LATON_OSC)
                drawCircle(ROJO, s * 0.3f, Offset(c.x, c.y - s * 0.08f)); drawCircle(LATON_LUZ, s * 0.2f, Offset(c.x, c.y - s * 0.08f), style = Stroke(s * 0.05f))
            }
            "convence" -> { drawPath(engrane(c, s * 0.46f, 8), LATON_OSC); drawCircle(LATON_LUZ, s * 0.16f, c) }
            "piensa" -> manometro(c, s * 0.46f, LATON_OSC, VIDRIO, ROJO, 0.4f)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un perno de cabeza hexagonal. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(estrella(c, s * 0.46f, 3, 1f, 0f), LATON_OSC)
        drawCircle(LATON_LUZ, s * 0.22f, c)
        drawCircle(LATON_OSC, s * 0.22f, c, style = Stroke(s * 0.05f))
    }

    override val desorden = 0f
}

/**
 * Napoleón: el secrétaire de campaña. Caoba con cantoneras y asas embutidas de latón, una cornisa imperio con su corona de
 * laurel dorada y una abeja; abajo, la tapa abatida forrada de cuero verde con su dorado, un tintero y una pluma. Cada tema es
 * una casilla con su pila de despachos lacrados y atados con cinta azul, y el tema en un portaetiquetas de latón. Por
 * dentro, cuero verde con hierros dorados. Las fichas son despachos con su filete azul y la abeja tenue.
 */
object IdeasNapoleon : DisenoIdeas {
    private val CAOBA = Color(0xFF6A2E1C)
    private val CAOBA_LUZ = Color(0xFF8E4428)
    private val CAOBA_OSC = Color(0xFF34140A)
    private val BRONCE = Color(0xFFC9A24C)
    private val BRONCE_OSC = Color(0xFF8A6A24)
    private val CUERO = Color(0xFF2E5A3A)
    private val CUERO_OSC = Color(0xFF1E3E28)
    private val HUECO = Color(0xFF1A0C06)
    private val PAPEL = Color(0xFFF6F0E0)
    private val PAPEL_OSC = Color(0xFFE2D8BE)
    private val AZUL = Color(0xFF1E3A8A)
    private val ROJO = Color(0xFFA3172E)
    private val TINTA = Color(0xFF14213D)
    private val LACRE = Color(0xFF9A1A1A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 0.9f
    override val corona = 0.24f
    override val base = 0.2f
    override val lado = 0.075f
    override val hueco = 0.025f

    /** Una corona de laurel: dos ramas de hojas que se cierran arriba. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color) {
        val u = density
        listOf(-1f, 1f).forEach { s ->
            drawArc(color, if (s < 0) 100f else -80f, 160f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1f * u))
            for (k in 0 until 7) {
                val a = Math.toRadians((if (s < 0) 110.0 + k * 22.0 else 70.0 - k * 22.0))
                val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                rotate(Math.toDegrees(a).toFloat() + 90f * s, p) { drawOval(color, Offset(p.x - r * 0.08f, p.y - r * 0.2f), Size(r * 0.16f, r * 0.34f)) }
            }
        }
    }

    /** Una abeja imperial, vista desde arriba. */
    private fun DrawScope.abeja(c: Offset, s: Float, color: Color) {
        drawOval(color, Offset(c.x - s * 0.36f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x + s * 0.04f, c.y - s * 0.32f), Size(s * 0.32f, s * 0.42f))
        drawOval(color, Offset(c.x - s * 0.12f, c.y - s * 0.3f), Size(s * 0.24f, s * 0.72f))
        drawCircle(color, s * 0.12f, Offset(c.x, c.y - s * 0.38f))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de caoba, con sus cantoneras de latón y las asas embutidas de los costados.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, d.base.top + 4 * u)
        drawRect(t(CAOBA, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, CAOBA_OSC.copy(alpha = 0.4f), 37, 1f, grosor = 0.7f, ondulacion = 0.7f)
        d.huecos.forEach { r -> drawRect(t(BRONCE_OSC, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(1.2f * u)) }
        listOf(Offset(cuerpo.left, cuerpo.top) to Offset(1f, 1f), Offset(cuerpo.right, cuerpo.top) to Offset(-1f, 1f)).forEach { (o, s) ->
            drawRect(t(BRONCE, oscuro), Offset(if (s.x > 0) o.x else o.x - 14 * u, o.y), Size(14 * u, 4 * u))
            drawRect(t(BRONCE, oscuro), Offset(if (s.x > 0) o.x else o.x - 4 * u, o.y), Size(4 * u, 14 * u))
        }
        listOf(cuerpo.left + 2 * u, cuerpo.right - 8 * u).forEach { x ->
            val y = (cuerpo.top + cuerpo.bottom) / 2
            drawRect(t(BRONCE, oscuro), Offset(x, y - 14 * u), Size(6 * u, 28 * u))
            drawRect(t(CAOBA_OSC, oscuro), Offset(x + 1.5f * u, y - 10 * u), Size(3 * u, 20 * u))
        }
        // La cornisa imperio, con su perlado dorado, y encima la corona de laurel con la abeja.
        val cornisa = Rect(0f, d.corona.top + ch * 0.6f, w, d.corona.bottom)
        drawRect(t(CAOBA_OSC, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(CAOBA_LUZ, oscuro), Offset(0f, cornisa.top), Size(w, cornisa.height * 0.3f))
        var x = 3 * u
        while (x < w) { drawCircle(t(BRONCE, oscuro), 1.3f * u, Offset(x, cornisa.top + cornisa.height * 0.62f)); x += 5 * u }
        val tim = Rect(w * 0.3f, d.corona.top + ch * 0.08f, w * 0.7f, cornisa.top)
        drawRect(t(CAOBA, oscuro), tim.topLeft, tim.size)
        drawRect(t(BRONCE, oscuro), tim.topLeft, tim.size, style = Stroke(1.2f * u))
        val lc = tim.center
        laurel(lc, tim.height * 0.4f, t(BRONCE, oscuro))
        abeja(Offset(lc.x, lc.y + tim.height * 0.05f), tim.height * 0.36f, t(BRONCE, oscuro))
        listOf(w * 0.15f, w * 0.85f).forEach { px -> drawPath(estrella(Offset(px, cornisa.top - ch * 0.12f), ch * 0.12f, 7, 0.4f), t(BRONCE, oscuro)) }
        // La tapa abatida: cuero verde con su doble filete dorado, un tintero y una pluma.
        val tapa = Rect(0f, d.base.top + 4 * u, w, d.base.bottom)
        drawRect(t(CAOBA, oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, tapa.top), Size(w, 3 * u))
        val cuero = Rect(tapa.left + 8 * u, tapa.top + 7 * u, tapa.right - 8 * u, tapa.bottom - 6 * u)
        drawRect(t(CUERO, oscuro), cuero.topLeft, cuero.size)
        drawRect(t(BRONCE, oscuro), Offset(cuero.left + 3 * u, cuero.top + 3 * u), Size(cuero.width - 6 * u, cuero.height - 6 * u), style = Stroke(0.9f * u))
        drawRect(t(BRONCE, oscuro), Offset(cuero.left + 5.5f * u, cuero.top + 5.5f * u), Size(cuero.width - 11 * u, cuero.height - 11 * u), style = Stroke(0.5f * u))
        val ti = Offset(w * 0.86f, cuero.center.y)
        drawRect(Color(0xFF1A2430), Offset(ti.x - 7 * u, ti.y - 5 * u), Size(14 * u, 12 * u))
        drawRect(t(BRONCE, oscuro), Offset(ti.x - 8 * u, ti.y - 7 * u), Size(16 * u, 3 * u))
        drawLine(t(PAPEL, oscuro), Offset(ti.x, ti.y - 6 * u), Offset(ti.x + 16 * u, ti.y - 24 * u), 1.4f * u, StrokeCap.Round)
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(BRONCE, oscuro), r.topLeft, r.size, style = Stroke(1.6f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // La casilla: el hueco oscuro, con su sombra adentro.
        drawRect(t(HUECO, oscuro))
        sombraInterior(Rect(0f, 0f, w, h), w * 0.12f, 0.5f)
        val piso = h * 0.68f
        if (c.vacio) {
            // Un lugar libre: un catalejo de latón.
            val y = piso - h * 0.06f
            drawRoundRect(t(BRONCE, oscuro), Offset(w * 0.14f, y), Size(w * 0.3f, h * 0.07f), CornerRadius(2 * u))
            drawRoundRect(t(BRONCE_OSC, oscuro), Offset(w * 0.42f, y + h * 0.006f), Size(w * 0.24f, h * 0.058f), CornerRadius(2 * u))
            drawRoundRect(t(CAOBA_LUZ, oscuro), Offset(w * 0.64f, y + h * 0.012f), Size(w * 0.2f, h * 0.046f), CornerRadius(2 * u))
        } else {
            // La pila de despachos, atada con su cinta azul, con algún sello de lacre.
            val k = c.n.coerceIn(1, 8)
            val g = h * 0.035f
            for (i in 0 until k) {
                val y = piso - (i + 1) * g
                val dx = (azar(c.semilla, i) - 0.5f) * w * 0.08f
                drawRect(t(if (i % 2 == 0) PAPEL else PAPEL_OSC, oscuro), Offset(w * 0.12f + dx, y), Size(w * 0.76f, g - 0.8f * u))
                if (i % 3 == 1) drawCircle(t(LACRE, oscuro), g * 0.32f, Offset(w * 0.24f + dx, y + g * 0.45f))
            }
            val top = piso - k * g
            drawRect(t(AZUL, oscuro), Offset(w * 0.6f, top - 1 * u), Size(2.4f * u, piso - top + 1 * u))
            drawOval(t(AZUL, oscuro), Offset(w * 0.6f - 6 * u, top - 4 * u), Size(6 * u, 5 * u), style = Stroke(1.4f * u))
            drawOval(t(AZUL, oscuro), Offset(w * 0.6f + 2.4f * u, top - 4 * u), Size(6 * u, 5 * u), style = Stroke(1.4f * u))
            if (ab > 0f) {
                val y = top + ab * h * 0.2f
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.16f, y + 2 * u), Size(w * 0.7f, h * 0.06f))
                drawRect(t(PAPEL, oscuro), Offset(w * 0.14f, y), Size(w * 0.7f, h * 0.06f))
                drawCircle(t(LACRE, oscuro), h * 0.018f, Offset(w * 0.49f, y + h * 0.03f))
            }
        }
        // El canto de latón de la casilla y el portaetiquetas con el tema.
        drawRect(t(BRONCE_OSC, oscuro), Offset(0f, piso), Size(w, 2.5f * u))
        drawRect(t(CAOBA, oscuro), Offset(0f, piso + 2.5f * u), Size(w, h - piso))
        if (c.vacio) return
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(BRONCE, oscuro), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u), style = Stroke(2.2f * u))
    }

    override val rotulo = Rect(0.08f, 0.73f, 0.92f, 0.95f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.bodoniTitulo
    override val salida = 0f
    override val placa = Rect(0.32f, 0.3f, 0.68f, 0.72f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Cuero verde con hierros dorados: doble filete, florones en las esquinas y una corona de laurel al centro.
        drawRect(t(CUERO, oscuro))
        for (k in 0 until 30) mancha(Offset(w * azar(91, k), h * azar(92, k)), (20f + 30f * azar(93, k)) * u, CUERO_OSC.copy(alpha = 0.18f))
        val m = 30 * u
        drawRect(t(BRONCE, oscuro).copy(alpha = 0.7f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.4f * u))
        drawRect(t(BRONCE, oscuro).copy(alpha = 0.5f), Offset(m + 6 * u, m + 6 * u), Size(w - 2 * m - 12 * u, h - 2 * m - 12 * u), style = Stroke(0.8f * u))
        listOf(Offset(m, m), Offset(w - m, m), Offset(m, h - m), Offset(w - m, h - m)).forEach { drawPath(estrella(it, 9 * u, 8, 0.4f), t(BRONCE, oscuro).copy(alpha = 0.7f)) }
        laurel(Offset(w / 2, h * 0.45f), w * 0.22f, BRONCE.copy(alpha = 0.12f))
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CAOBA, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PAPEL
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El doble filete azul de la cancillería, los dobleces en tercios y la abeja tenue abajo.
        val m = 4 * u * g
        drawRect(AZUL.copy(alpha = 0.5f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1f * u * g))
        drawRect(AZUL.copy(alpha = 0.35f), Offset(m + 2.4f * u * g, m + 2.4f * u * g), Size(w - 2 * m - 4.8f * u * g, h - 2 * m - 4.8f * u * g), style = Stroke(0.5f * u))
        listOf(w / 3, w * 2 / 3).forEach { x -> drawLine(Color.Black.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, h), 1f * u) }
        abeja(Offset(w / 2, h - 12 * u * g), 12 * u * g, AZUL.copy(alpha = 0.22f))
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 14.dp, 26.dp, 28.dp, 32.dp)
    override val proporcionFicha = 0.82f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La N imperial en su sello.
            "hecho" -> {
                drawCircle(AZUL, s * 0.42f, c)
                drawPath(Path().apply { moveTo(c.x - s * 0.17f, c.y + s * 0.22f); lineTo(c.x - s * 0.17f, c.y - s * 0.22f); lineTo(c.x + s * 0.17f, c.y + s * 0.22f); lineTo(c.x + s * 0.17f, c.y - s * 0.22f) },
                    p.color, style = Stroke(s * 0.09f, join = androidx.compose.ui.graphics.StrokeJoin.Miter))
            }
            "convence" -> laurel(c, s * 0.36f, BRONCE_OSC)
            "piensa" -> abeja(c, s * 0.9f, AZUL)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** La estrella de la Legión de Honor, de esmalte blanco, colgada de su cinta roja. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(ROJO, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.26f))
        val e = Offset(c.x, c.y + s * 0.12f)
        drawPath(estrella(e, s * 0.38f, 5, 0.45f), BRONCE)
        drawPath(estrella(e, s * 0.3f, 5, 0.45f), Color.White)
        drawCircle(BRONCE, s * 0.08f, e)
    }

    override val desorden = 1f
}

/**
 * Art déco: el casillero de la recepción de un hotel. Laca negra con filetes de oro y pilastras estriadas; arriba, un remate
 * escalonado con un abanico de rayos; abajo, el mostrador de mármol verde con chevrones dorados y su timbre. Cada tema es un
 * casillero con su llave colgada y los telegramas que van llegando, y el tema en una placa de latón grabada. Por dentro, laca
 * negra con un sol de rayos de oro. Las fichas son telegramas con esquinas escalonadas.
 */
object IdeasDeco : DisenoIdeas {
    private val LACA = Color(0xFF161310)
    private val LACA_LUZ = Color(0xFF2E2820)
    private val ORO = Color(0xFFC9A24C)
    private val ORO_LUZ = Color(0xFFE8CF8A)
    private val ORO_OSC = Color(0xFF8A6A12)
    private val VERDE = Color(0xFF1F6B57)
    private val MARMOL = Color(0xFF2E5E50)
    private val MARMOL_VETA = Color(0xFF9CC8B8)
    private val CREMA = Color(0xFFF6F0E1)
    private val TELEGRAMA = Color(0xFFF2E6B8)
    private val TINTA = Color(0xFF16130E)
    private val GRANATE = Color(0xFF7A1E2A)
    private val ROJO = Color(0xFFA8322A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.25f) else c

    override val cols = 3
    override val proporcion = 0.95f
    override val corona = 0.3f
    override val base = 0.18f
    override val lado = 0.075f
    override val hueco = 0.025f

    /** Un abanico de rayos: medio sol con sus rayos alternados. */
    private fun DrawScope.abanico(c: Offset, r: Float, a: Color, b: Color) {
        val n = 14
        for (k in 0 until n) drawArc(if (k % 2 == 0) a else b, 180f + k * 180f / n, 180f / n, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        drawArc(LACA, 180f, 180f, true, Offset(c.x - r * 0.28f, c.y - r * 0.28f), Size(r * 0.56f, r * 0.56f))
        drawArc(ORO, 180f, 180f, false, Offset(c.x - r * 0.28f, c.y - r * 0.28f), Size(r * 0.56f, r * 0.56f), style = Stroke(1.2f * density))
    }

    /** Una llave de hotel, colgando de [c]. */
    private fun DrawScope.llave(c: Offset, l: Float, color: Color) {
        val u = density
        drawCircle(color, l * 0.16f, Offset(c.x, c.y + l * 0.16f), style = Stroke(1.6f * u))
        drawLine(color, Offset(c.x, c.y + l * 0.32f), Offset(c.x, c.y + l), 1.8f * u)
        drawLine(color, Offset(c.x, c.y + l * 0.86f), Offset(c.x + l * 0.16f, c.y + l * 0.86f), 1.8f * u)
        drawLine(color, Offset(c.x, c.y + l * 0.96f), Offset(c.x + l * 0.12f, c.y + l * 0.96f), 1.8f * u)
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de laca negra, con el filete de oro en cada casillero.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, d.base.top + 4 * u)
        drawRect(t(LACA, oscuro), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { r -> drawRect(t(ORO, oscuro), Offset(r.left - 2 * u, r.top - 2 * u), Size(r.width + 4 * u, r.height + 4 * u), style = Stroke(1.4f * u)) }
        // Las pilastras estriadas de los costados.
        listOf(cuerpo.left, cuerpo.right - w * 0.045f).forEach { x ->
            val r = Rect(x, cuerpo.top, x + w * 0.045f, cuerpo.bottom)
            drawRect(t(LACA_LUZ, oscuro), r.topLeft, r.size)
            for (k in 1 until 4) drawLine(t(ORO, oscuro), Offset(r.left + r.width * k / 4, r.top + 6 * u), Offset(r.left + r.width * k / 4, r.bottom - 6 * u), 0.8f * u)
        }
        // El remate escalonado, con su abanico de rayos.
        val pasos = listOf(Rect(0f, d.corona.top + ch * 0.74f, w, d.corona.bottom), Rect(w * 0.12f, d.corona.top + ch * 0.56f, w * 0.88f, d.corona.top + ch * 0.75f), Rect(w * 0.26f, d.corona.top + ch * 0.38f, w * 0.74f, d.corona.top + ch * 0.57f))
        pasos.forEach { r ->
            drawRect(t(LACA, oscuro), r.topLeft, r.size)
            drawLine(t(ORO, oscuro), r.topLeft, Offset(r.right, r.top), 1.6f * u)
        }
        abanico(Offset(w / 2, pasos[2].top), ch * 0.36f, t(ORO, oscuro), t(ORO_OSC, oscuro))
        drawRect(t(ORO, oscuro), Offset(0f, d.corona.bottom - 3 * u), Size(w, 1.4f * u))
        // El mostrador: la cubierta de mármol verde veteado y el frente de laca con chevrones dorados, y el timbre.
        val cubierta = Rect(0f, d.base.top + 4 * u, w, d.base.top + d.base.height * 0.34f)
        drawRect(t(MARMOL, oscuro), cubierta.topLeft, cubierta.size)
        clipRect(cubierta.left, cubierta.top, cubierta.right, cubierta.bottom) {
            for (k in 0 until 7) {
                val p = Path().apply { moveTo(w * azar(61, k), cubierta.top); cubicTo(w * azar(62, k), cubierta.bottom, w * azar(63, k), cubierta.top, w * azar(64, k) + 40 * u, cubierta.bottom) }
                drawPath(p, MARMOL_VETA.copy(alpha = 0.35f), style = Stroke(0.8f * u))
            }
        }
        drawLine(t(ORO, oscuro), Offset(0f, cubierta.bottom), Offset(w, cubierta.bottom), 2 * u)
        val frente = Rect(0f, cubierta.bottom + 2 * u, w, d.base.bottom)
        drawRect(t(LACA, oscuro), frente.topLeft, frente.size)
        val cv = frente.height * 0.4f
        var x = 0f
        while (x < w) {
            drawPath(Path().apply { moveTo(x, frente.bottom - 4 * u); lineTo(x + cv / 2, frente.bottom - 4 * u - cv * 0.5f); lineTo(x + cv, frente.bottom - 4 * u) }, t(ORO, oscuro).copy(alpha = 0.6f), style = Stroke(1f * u))
            x += cv
        }
        val tim = Offset(w * 0.86f, cubierta.top + cubierta.height * 0.7f)
        drawArc(t(ORO_LUZ, oscuro), 180f, 180f, true, Offset(tim.x - 9 * u, tim.y - 9 * u), Size(18 * u, 18 * u))
        drawRect(t(ORO_OSC, oscuro), Offset(tim.x - 11 * u, tim.y), Size(22 * u, 2.5f * u))
        drawRect(t(ORO_OSC, oscuro), Offset(tim.x - 1.2f * u, tim.y - 12 * u), Size(2.4f * u, 3.5f * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(ORO, oscuro), r.topLeft, r.size)
            drawRect(t(ORO_OSC, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // El casillero: el fondo de laca con su sombra.
        drawRect(t(LACA_LUZ, oscuro))
        sombraInterior(Rect(0f, 0f, w, h), w * 0.14f, 0.5f)
        val piso = h * 0.68f
        // La llave colgada de su gancho.
        val gancho = Offset(w * 0.26f, h * 0.08f)
        drawCircle(t(ORO, oscuro), 1.8f * u, gancho)
        llave(Offset(gancho.x, gancho.y + 1 * u), h * 0.36f, t(ORO, oscuro))
        drawRoundRect(t(GRANATE, oscuro), Offset(gancho.x + 3 * u, gancho.y + h * 0.3f), Size(w * 0.12f, h * 0.12f), CornerRadius(w * 0.05f))
        if (!c.vacio) {
            // Los telegramas que van llegando, apoyados en el fondo (más al abrirlo).
            val k = c.n.coerceIn(1, 6)
            for (i in 0 until k) {
                val x = w * (0.44f + 0.07f * i)
                rotate(-8f + 3f * i, Offset(x, piso)) {
                    val alto = h * (0.3f + 0.05f * azar(c.semilla, i)) + ab * h * 0.12f
                    drawRect(t(TELEGRAMA, oscuro), Offset(x - w * 0.11f, piso - alto), Size(w * 0.22f, alto))
                    drawRect(t(VERDE, oscuro), Offset(x - w * 0.11f, piso - alto), Size(w * 0.22f, 2.5f * u))
                }
            }
        }
        // El canto dorado y la placa grabada con el tema.
        drawRect(t(ORO, oscuro), Offset(0f, piso), Size(w, 2 * u))
        drawRect(t(LACA, oscuro), Offset(0f, piso + 2 * u), Size(w, h - piso))
        if (c.vacio) return
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(ORO_LUZ, oscuro), r.topLeft, r.size)
        drawRect(t(ORO_OSC, oscuro), Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u), style = Stroke(0.8f * u))
    }

    override val rotulo = Rect(0.07f, 0.73f, 0.93f, 0.95f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.pasaje
    override val salida = 0f
    override val placa = Rect(0.32f, 0.44f, 0.68f, 0.88f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Laca negra con un sol de rayos de oro que sale de abajo, y un doble filete.
        drawRect(t(LACA, oscuro))
        val o = Offset(w / 2, h * 1.05f)
        for (k in 0 until 36) {
            val a = Math.toRadians(180.0 + k * 5.0)
            drawLine(ORO.copy(alpha = if (k % 2 == 0) 0.1f else 0.05f), o, Offset(o.x + h * 1.4f * cos(a).toFloat(), o.y + h * 1.4f * sin(a).toFloat()), (if (k % 2 == 0) 3f else 1f) * u)
        }
        val pl = 14 * u
        drawRect(t(ORO, oscuro), Offset(pl * 0.5f, pl * 0.5f), Size(w - pl, h - pl), style = Stroke(1.6f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.5f), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(0.8f * u))
        escuadrasPared(w, h, pl * 0.5f, 34 * u, t(ORO, oscuro))
    }

    override fun tintaInterior(oscuro: Boolean) = CREMA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(TELEGRAMA.copy(alpha = 0.5f))
        // La cabecera verde y oro, y las esquinas escalonadas.
        drawRect(VERDE.copy(alpha = 0.8f), Offset.Zero, Size(w, 3 * u * g))
        drawRect(ORO, Offset(0f, 3 * u * g), Size(w, 1.2f * u * g))
        val e = 9 * u * g; val m = 4 * u * g
        listOf(Offset(m, m + 4 * u * g) to Offset(1f, 1f), Offset(w - m, m + 4 * u * g) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, s) ->
            val p = Path().apply {
                moveTo(o.x + s.x * e, o.y); lineTo(o.x, o.y); lineTo(o.x, o.y + s.y * e)
                moveTo(o.x + s.x * e * 0.6f, o.y + s.y * 2.5f * u * g); lineTo(o.x + s.x * 2.5f * u * g, o.y + s.y * 2.5f * u * g); lineTo(o.x + s.x * 2.5f * u * g, o.y + s.y * e * 0.6f)
            }
            drawPath(p, ORO_OSC.copy(alpha = 0.7f), style = Stroke(0.9f * u * g))
        }
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 14.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.92f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un rombo de oro macizo.
            "hecho" -> {
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.46f); lineTo(c.x + s * 0.34f, c.y); lineTo(c.x, c.y + s * 0.46f); lineTo(c.x - s * 0.34f, c.y); close() }, ORO)
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.24f); lineTo(c.x + s * 0.18f, c.y); lineTo(c.x, c.y + s * 0.24f); lineTo(c.x - s * 0.18f, c.y); close() }, ORO_OSC, style = Stroke(s * 0.05f))
            }
            "convence" -> abanico(Offset(c.x, c.y + s * 0.26f), s * 0.48f, ORO, ORO_OSC)
            "piensa" -> listOf(-0.16f, 0.12f).forEach { dy -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + dy * s - s * 0.12f); lineTo(c.x, c.y + dy * s + s * 0.14f); lineTo(c.x + s * 0.36f, c.y + dy * s - s * 0.12f) }, VERDE, style = Stroke(s * 0.1f)) }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** La llave de la habitación, con su placa granate. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        llave(Offset(c.x - s * 0.1f, c.y - s * 0.5f), s * 0.9f, ORO_OSC)
        drawRoundRect(GRANATE, Offset(c.x + s * 0.04f, c.y - s * 0.12f), Size(s * 0.3f, s * 0.4f), CornerRadius(s * 0.12f))
    }

    override val desorden = 1.5f
}

/**
 * Botánico: el gabinete del naturalista. Un mueble de roble con estantes de canto filetado; encima, una campana de vidrio con
 * un helecho, una lupa y unos cuadernos de campo; en el zócalo, cajoncitos con portaetiquetas. Cada tema es una caja de
 * herbario forrada de tela verde, con el lomo de cuero, dos correas con hebilla y el tema en una etiqueta de papel; asoman los
 * pliegos con sus hojas. Por dentro, papel secante con un helecho prensado. Las fichas son pliegos de herbario con su
 * ejemplar sujeto con tiras de papel.
 */
object IdeasBotanico : DisenoIdeas {
    private val ROBLE = Color(0xFF6E5030)
    private val ROBLE_LUZ = Color(0xFF8E6C44)
    private val ROBLE_OSC = Color(0xFF3E2C18)
    private val VERDE = Color(0xFF3F5A2C)
    private val TELA = Color(0xFF4E6A3A)
    private val TELA_OSC = Color(0xFF34482A)
    private val CUERO = Color(0xFF7A4A28)
    private val CUERO_OSC = Color(0xFF4A2A14)
    private val LATON = Color(0xFFC2A055)
    private val PAPEL = Color(0xFFF1E9D2)
    private val SECANTE = Color(0xFFE4D8BC)
    private val VIDRIO = Color(0xFFDDEBEB)
    private val TINTA = Color(0xFF2A2318)
    private val HOJA = Color(0xFF5A7A3A)
    private val TERRACOTA = Color(0xFFB0603A)
    private val FLOR = Color(0xFF9A5A8A)
    private val ROJO = Color(0xFFA8322A)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.34f
    override val base = 0.12f
    override val lado = 0.07f
    override val hueco = 0.06f

    /** Una fronda de helecho: un tallo curvo con pares de foliolos que se achican hacia la punta. */
    internal fun DrawScope.helecho(base: Offset, largo: Float, giro: Float, color: Color, grosor: Float) {
        val pasos = 12
        var p = base
        var a = Math.toRadians(giro.toDouble())
        for (k in 0 until pasos) {
            val f = 1f - k / pasos.toFloat()
            val l = largo / pasos
            val q = Offset(p.x + l * cos(a).toFloat(), p.y + l * sin(a).toFloat())
            drawLine(color, p, q, grosor * (0.4f + 0.6f * f))
            val hoja = largo * 0.16f * f
            listOf(-1.1, 1.1).forEach { da ->
                val b = a + da
                val e = Offset(q.x + hoja * cos(b).toFloat(), q.y + hoja * sin(b).toFloat())
                drawLine(color, q, e, grosor * 1.6f * f, StrokeCap.Round)
            }
            p = q; a += 0.06
        }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cuerpo de roble.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 8 * u, w * 0.98f, d.base.top + d.base.height * 0.7f)
        drawRect(t(ROBLE, oscuro), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, ROBLE_OSC.copy(alpha = 0.35f), 47, 1f, grosor = 0.7f, ondulacion = 0.5f)
        d.filas.forEach { f ->
            val r = Rect(cuerpo.left, f.top - d.separacion * 0.6f, cuerpo.right, f.bottom)
            drawRect(t(mezcla(ROBLE_OSC, Color.Black, 0.2f), oscuro), Offset(w * 0.05f, r.top), Size(w * 0.9f, r.height))
            val canto = Rect(cuerpo.left, f.bottom, cuerpo.right, f.bottom + maxOf(d.separacion * 0.6f, 8 * u))
            drawRect(t(ROBLE_LUZ, oscuro), canto.topLeft, canto.size)
            drawLine(t(ROBLE_OSC, oscuro), Offset(canto.left, canto.center.y), Offset(canto.right, canto.center.y), 0.6f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(canto.left, canto.bottom), Size(canto.width, 2.5f * u))
        }
        // La tabla de arriba y, sobre ella, los cuadernos de campo, la campana con el helecho y la lupa.
        val tabla = Rect(0f, d.corona.bottom - 14 * u, w, d.corona.bottom - 6 * u)
        drawRect(t(ROBLE_LUZ, oscuro), tabla.topLeft, tabla.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tabla.bottom), Size(w, 2 * u))
        listOf(Color(0xFF4E3A28), Color(0xFF2E4A3A), Color(0xFF6A3A28)).forEachIndexed { i, col ->
            drawRect(t(col, oscuro), Offset(w * (0.08f + 0.01f * i), tabla.top - (i + 1) * ch * 0.07f), Size(w * (0.2f - 0.02f * i), ch * 0.065f))
        }
        val cb = Offset(w * 0.5f, tabla.top)
        val cr = ch * 0.3f
        drawRect(t(ROBLE_OSC, oscuro), Offset(cb.x - cr * 1.1f, cb.y - 5 * u), Size(cr * 2.2f, 5 * u))
        helecho(Offset(cb.x - cr * 0.2f, cb.y - 5 * u), cr * 1.2f, -100f, t(HOJA, oscuro), 1.6f * u)
        helecho(Offset(cb.x + cr * 0.1f, cb.y - 5 * u), cr * 0.9f, -60f, t(VERDE, oscuro), 1.4f * u)
        val campana = Path().apply { moveTo(cb.x - cr, cb.y - 5 * u); lineTo(cb.x - cr, cb.y - cr * 1.2f); cubicTo(cb.x - cr, cb.y - cr * 2.1f, cb.x + cr, cb.y - cr * 2.1f, cb.x + cr, cb.y - cr * 1.2f); lineTo(cb.x + cr, cb.y - 5 * u) }
        drawPath(campana, VIDRIO.copy(alpha = 0.22f))
        drawPath(campana, Color.White.copy(alpha = 0.6f), style = Stroke(1.4f * u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(cb.x - cr * 0.7f, cb.y - cr * 1.6f), Offset(cb.x - cr * 0.75f, cb.y - cr * 0.6f), 2.5f * u, StrokeCap.Round)
        drawCircle(t(LATON, oscuro), 3 * u, Offset(cb.x, cb.y - cr * 1.86f))
        val lupa = Offset(w * 0.8f, tabla.top - ch * 0.12f)
        drawLine(t(ROBLE_OSC, oscuro), Offset(lupa.x + ch * 0.08f, lupa.y + ch * 0.06f), Offset(lupa.x + ch * 0.28f, lupa.y + ch * 0.1f), 3.5f * u, StrokeCap.Round)
        drawCircle(VIDRIO.copy(alpha = 0.4f), ch * 0.1f, lupa)
        drawCircle(t(LATON, oscuro), ch * 0.1f, lupa, style = Stroke(2 * u))
        // El zócalo, con su fila de cajoncitos con portaetiquetas.
        val zoc = Rect(cuerpo.left, d.base.top + 4 * u, cuerpo.right, cuerpo.bottom)
        drawRect(t(ROBLE_OSC, oscuro), zoc.topLeft, zoc.size)
        val nc = 5
        for (j in 0 until nc) {
            val r = Rect(zoc.left + zoc.width * j / nc + 3 * u, zoc.top + 3 * u, zoc.left + zoc.width * (j + 1) / nc - 3 * u, zoc.bottom - 3 * u)
            drawRect(t(ROBLE, oscuro), r.topLeft, r.size)
            drawRect(t(LATON, oscuro), Offset(r.center.x - r.width * 0.2f, r.center.y - r.height * 0.22f), Size(r.width * 0.4f, r.height * 0.3f), style = Stroke(1f * u))
            drawCircle(t(LATON, oscuro), 1.6f * u, Offset(r.center.x, r.center.y + r.height * 0.26f))
        }
        drawRect(t(ROBLE_OSC, oscuro).copy(alpha = 0.8f), Offset(0f, cuerpo.bottom), Size(w, d.base.bottom - cuerpo.bottom))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
            drawRect(t(TINTA, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: una maceta de barro con un brote.
            val b = Offset(w / 2, h * 0.95f)
            drawPath(Path().apply { moveTo(b.x - w * 0.1f, b.y - h * 0.2f); lineTo(b.x + w * 0.1f, b.y - h * 0.2f); lineTo(b.x + w * 0.07f, b.y); lineTo(b.x - w * 0.07f, b.y); close() }, t(TERRACOTA, oscuro))
            drawRect(t(mezcla(TERRACOTA, Color.Black, 0.2f), oscuro), Offset(b.x - w * 0.11f, b.y - h * 0.22f), Size(w * 0.22f, h * 0.04f))
            helecho(Offset(b.x, b.y - h * 0.22f), h * 0.3f, -95f, t(HOJA, oscuro), 1.4f * u)
            return
        }
        val caja = Rect(w * 0.1f, h * 0.3f, w * 0.9f, h * 0.95f)
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(caja.left, caja.bottom - h * 0.03f), Size(caja.width, h * 0.06f))
        // Los pliegos que asoman por arriba, con la punta de alguna hoja.
        if (sube > 1f) {
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val y = caja.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                val x = caja.left + caja.width * (0.12f + 0.12f * i)
                drawRect(t(PAPEL, oscuro), Offset(x, y), Size(caja.width * 0.18f, caja.top - y + 2 * u))
                if (i % 2 == 0) drawOval(t(HOJA, oscuro), Offset(x + caja.width * 0.05f, y - 4 * u), Size(5 * u, 9 * u))
            }
        }
        // La caja forrada de tela verde, con el lomo de cuero.
        drawRect(t(TELA, oscuro), caja.topLeft, caja.size)
        var x = caja.left
        while (x < caja.right) { drawLine(t(TELA_OSC, oscuro).copy(alpha = 0.5f), Offset(x, caja.top), Offset(x, caja.bottom), 0.6f * u); x += 2.5f * u }
        drawRect(t(CUERO, oscuro), Offset(caja.left, caja.top), Size(caja.width * 0.1f, caja.height))
        drawLine(t(LATON, oscuro), Offset(caja.left + caja.width * 0.1f, caja.top), Offset(caja.left + caja.width * 0.1f, caja.bottom), 1f * u)
        drawRect(t(TELA_OSC, oscuro), caja.topLeft, caja.size, style = Stroke(1.2f * u))
        // Las dos correas con su hebilla.
        listOf(caja.top + caja.height * 0.1f, caja.bottom - caja.height * 0.16f).forEach { y ->
            drawRect(t(CUERO_OSC, oscuro), Offset(caja.left, y), Size(caja.width, caja.height * 0.07f))
            val hb = Rect(caja.right - caja.width * 0.2f, y - 1.5f * u, caja.right - caja.width * 0.12f, y + caja.height * 0.07f + 1.5f * u)
            drawRect(t(LATON, oscuro), hb.topLeft, hb.size, style = Stroke(1.4f * u))
        }
        // La etiqueta de papel con el tema, con su filete.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t(PAPEL, oscuro), r.topLeft, r.size)
        drawRect(t(TINTA, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.7f * u))
    }

    override val rotulo = Rect(0.26f, 0.44f, 0.84f, 0.75f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.fraunces
    override val salida = 0f
    override val placa = Rect(0.33f, 0.1f, 0.67f, 0.62f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Papel secante, con un helecho grande prensado y su etiqueta en la esquina.
        val s = if (oscuro) mezcla(SECANTE, Color.Black, 0.6f) else SECANTE
        drawRect(s)
        for (k in 0 until 40) drawCircle(Color.Black.copy(alpha = 0.03f), (6f + 20f * azar(95, k)) * u, Offset(w * azar(96, k), h * azar(97, k)))
        helecho(Offset(w * 0.25f, h * 0.95f), h * 0.85f, -75f, HOJA.copy(alpha = 0.16f), 3 * u)
        drawRect(PAPEL.copy(alpha = 0.6f), Offset(w * 0.62f, h * 0.86f), Size(w * 0.3f, h * 0.08f))
        drawRect(TINTA.copy(alpha = 0.3f), Offset(w * 0.62f, h * 0.86f), Size(w * 0.3f, h * 0.08f), style = Stroke(0.8f * u))
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(TELA, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.25f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.4f))
        // El ejemplar prensado abajo a la derecha, sujeto con dos tiras de papel.
        val b = Offset(w * 0.9f, h * 0.98f)
        helecho(b, minOf(w, h) * 0.5f, -130f + 20f * azar(semilla, 1), HOJA.copy(alpha = 0.3f), 1.2f * u * g)
        listOf(0.25f, 0.6f).forEach { f ->
            val c = Offset(b.x - minOf(w, h) * 0.5f * f * 0.64f, b.y - minOf(w, h) * 0.5f * f * 0.77f)
            rotate(-40f, c) { drawRect(Color.White.copy(alpha = 0.55f), Offset(c.x - 7 * u * g, c.y - 2 * u * g), Size(14 * u * g, 4 * u * g)) }
        }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 12.dp, 24.dp, 28.dp, 26.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El ejemplar clavado con su alfiler: ya está identificado.
            "hecho" -> {
                drawLine(LATON, Offset(c.x - s * 0.3f, c.y + s * 0.42f), Offset(c.x + s * 0.18f, c.y - s * 0.26f), s * 0.07f, StrokeCap.Round)
                drawCircle(ROJO, s * 0.16f, Offset(c.x + s * 0.2f, c.y - s * 0.28f))
            }
            "convence" -> {
                drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + s * 0.36f); quadraticTo(c.x - s * 0.36f, c.y - s * 0.3f, c.x + s * 0.4f, c.y - s * 0.4f); quadraticTo(c.x + s * 0.3f, c.y + s * 0.34f, c.x - s * 0.36f, c.y + s * 0.36f); close() }, HOJA)
                drawLine(PAPEL, Offset(c.x - s * 0.3f, c.y + s * 0.3f), Offset(c.x + s * 0.3f, c.y - s * 0.3f), s * 0.05f)
            }
            "piensa" -> {
                drawCircle(LATON, s * 0.26f, Offset(c.x - s * 0.08f, c.y - s * 0.08f), style = Stroke(s * 0.09f))
                drawLine(CUERO_OSC, Offset(c.x + s * 0.12f, c.y + s * 0.12f), Offset(c.x + s * 0.42f, c.y + s * 0.42f), s * 0.12f, StrokeCap.Round)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una flor prensada. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        for (k in 0 until 5) {
            val a = Math.toRadians(-90.0 + k * 72.0)
            drawCircle(FLOR, s * 0.18f, Offset(c.x + s * 0.24f * cos(a).toFloat(), c.y + s * 0.24f * sin(a).toFloat()))
        }
        drawCircle(Color(0xFFE0B84A), s * 0.12f, c)
    }

    override val desorden = 1.5f
}

/**
 * Navidad: la alacena de la cocina. Un aparador pintado de crema con filetes verdes, con su lambrequín festoneado, una
 * guirnalda de abeto con bolas rojas y una estrella; los estantes llevan su puntilla de papel y el mostrador, un mantel de
 * cuadros. Cada tema es una lata redonda de galletas, roja o azul, con copos de nieve, filetes dorados y el tema en una
 * etiqueta ovalada; asoman tarjetas bajo la tapa. Por dentro, la lata con su blonda de papel. Las fichas son tarjetas de
 * felicitación con filete rojo y verde y una ramita de acebo.
 */
object IdeasNavidad : DisenoIdeas {
    private val CREMA = Color(0xFFF2E8D2)
    private val CREMA_OSC = Color(0xFFD8C8A8)
    private val VERDE = Color(0xFF2E5E3A)
    private val ABETO = Color(0xFF1B5E20)
    private val ABETO_OSC = Color(0xFF0E3A14)
    private val ROJO = Color(0xFFB71C1C)
    private val ROJO_OSC = Color(0xFF7A1010)
    private val ORO = Color(0xFFD4A84A)
    private val BLANCO = Color(0xFFFBF6EC)
    private val LATA_AZUL = Color(0xFF1E3E7A)
    private val PLATA = Color(0xFFC8CCD0)
    private val TINTA = Color(0xFF1E2A22)
    private val GALLETA = Color(0xFFB07A44)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.15f
    override val corona = 0.28f
    override val base = 0.13f
    override val lado = 0.08f
    override val hueco = 0.07f

    /** Un copo de nieve de seis brazos. */
    private fun DrawScope.copo(c: Offset, r: Float, color: Color, grosor: Float) {
        for (k in 0 until 6) {
            val a = k * Math.PI / 3
            val e = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            drawLine(color, c, e, grosor, StrokeCap.Round)
            val m = Offset(c.x + r * 0.6f * cos(a).toFloat(), c.y + r * 0.6f * sin(a).toFloat())
            listOf(0.6, -0.6).forEach { da -> drawLine(color, m, Offset(m.x + r * 0.3f * cos(a + da).toFloat(), m.y + r * 0.3f * sin(a + da).toFloat()), grosor, StrokeCap.Round) }
        }
    }

    /** Una ramita de acebo: dos hojas dentadas y tres bayas. */
    private fun DrawScope.acebo(c: Offset, s: Float, hoja: Color, baya: Color) {
        listOf(-1f, 1f).forEach { d ->
            val p = Path().apply {
                moveTo(c.x, c.y)
                for (k in 1..4) { val f = k / 4f; lineTo(c.x + d * s * 0.5f * f, c.y - s * (0.16f + (if (k % 2 == 0) 0.04f else 0.14f)) * (1 - f * 0.4f) - s * 0.05f * f) }
                lineTo(c.x + d * s * 0.56f, c.y - s * 0.02f)
                for (k in 4 downTo 1) { val f = k / 4f; lineTo(c.x + d * s * 0.5f * f, c.y + s * (if (k % 2 == 0) 0.04f else 0.12f) * (1 - f * 0.4f)) }
                close()
            }
            drawPath(p, hoja)
        }
        listOf(Offset(-0.06f, 0.02f), Offset(0.07f, 0.04f), Offset(0f, 0.13f)).forEach { o -> drawCircle(baya, s * 0.08f, Offset(c.x + o.x * s, c.y + o.y * s)) }
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El aparador pintado de crema, con sus filetes verdes.
        val cuerpo = Rect(w * 0.02f, d.corona.bottom - 6 * u, w * 0.98f, d.base.top + d.base.height * 0.3f)
        drawRect(t(CREMA, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(t(VERDE, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(4 * u))
        drawRect(t(VERDE, oscuro), Offset(cuerpo.left + 8 * u, cuerpo.top + 8 * u), Size(cuerpo.width - 16 * u, cuerpo.height - 16 * u), style = Stroke(1 * u))
        // Los estantes, con su puntilla de papel calado.
        d.filas.forEach { f ->
            val r = Rect(cuerpo.left + 4 * u, f.bottom - 1 * u, cuerpo.right - 4 * u, f.bottom + maxOf(d.separacion * 0.35f, 6 * u))
            drawRect(t(CREMA_OSC, oscuro), r.topLeft, r.size)
            val pr = 4 * u
            var x = r.left
            while (x < r.right) { drawArc(t(BLANCO, oscuro), 0f, 180f, true, Offset(x, r.bottom - pr), Size(pr * 2, pr * 2)); drawCircle(t(CREMA_OSC, oscuro), 0.8f * u, Offset(x + pr, r.bottom + pr * 0.4f)); x += pr * 2 }
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(r.left, r.bottom + pr), Size(r.width, 1.5f * u))
        }
        // La cornisa verde, el lambrequín festoneado, la guirnalda de abeto con sus bolas y la estrella.
        val cornisa = Rect(0f, d.corona.top + ch * 0.36f, w, d.corona.top + ch * 0.56f)
        drawRect(t(VERDE, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(ORO, oscuro), Offset(0f, cornisa.bottom - 2 * u), Size(w, 1.5f * u))
        val fest = Rect(w * 0.02f, cornisa.bottom, w * 0.98f, d.corona.bottom - 6 * u)
        drawRect(t(CREMA, oscuro), fest.topLeft, Size(fest.width, fest.height * 0.4f))
        val nf = 10
        for (j in 0 until nf) drawArc(t(CREMA, oscuro), 0f, 180f, true, Offset(fest.left + fest.width * j / nf, fest.top - fest.height * 0.2f), Size(fest.width / nf, fest.height * 1.2f))
        val ng = 4
        for (j in 0 until ng) {
            val x0 = w * j / ng; val x1 = w * (j + 1) / ng
            val y0 = cornisa.top + 2 * u
            for (k in 0..14) {
                val f = k / 14f
                val p = Offset(x0 + (x1 - x0) * f, y0 + ch * 0.32f * 4 * f * (1 - f))
                drawCircle(t(if (k % 2 == 0) ABETO else ABETO_OSC, oscuro), 5 * u, p)
            }
            drawCircle(t(ROJO, oscuro), 4.5f * u, Offset((x0 + x1) / 2, y0 + ch * 0.32f + 6 * u))
            drawCircle(Color.White.copy(alpha = 0.6f), 1.4f * u, Offset((x0 + x1) / 2 - 1.5f * u, y0 + ch * 0.32f + 4.5f * u))
            drawCircle(t(ORO, oscuro), 3 * u, Offset(x0 + (x1 - x0) * 0.25f, y0 + ch * 0.26f))
            drawCircle(t(ORO, oscuro), 3 * u, Offset(x0 + (x1 - x0) * 0.75f, y0 + ch * 0.26f))
        }
        drawPath(estrella(Offset(w / 2, d.corona.top + ch * 0.18f), ch * 0.16f, 5, 0.45f), t(ORO, oscuro))
        // El mostrador con su mantel de cuadros rojos.
        val mantel = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(BLANCO, oscuro), mantel.topLeft, mantel.size)
        val q = 6 * u
        var y = mantel.top; var i = 0
        while (y < mantel.bottom) {
            var x = 0f; var j = 0
            while (x < w) {
                val a = (if (i % 2 == 0) 0.25f else 0f) + (if (j % 2 == 0) 0.25f else 0f)
                if (a > 0f) drawRect(ROJO.copy(alpha = a * (if (oscuro) 1.3f else 1f)), Offset(x, y), Size(q, q))
                x += q; j++
            }
            y += q; i++
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRoundRect(t(BLANCO, oscuro), r.topLeft, r.size, CornerRadius(2 * u))
            drawRoundRect(t(ROJO, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), CornerRadius(1.5f * u), style = Stroke(0.8f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: un hombrecito de jengibre con su glaseado.
            val b = Offset(w / 2, h * 0.94f)
            val s = h * 0.36f
            val g = t(GALLETA, oscuro)
            drawCircle(g, s * 0.16f, Offset(b.x, b.y - s * 0.84f))
            drawRoundRect(g, Offset(b.x - s * 0.17f, b.y - s * 0.7f), Size(s * 0.34f, s * 0.42f), CornerRadius(s * 0.1f))
            drawLine(g, Offset(b.x - s * 0.15f, b.y - s * 0.6f), Offset(b.x - s * 0.36f, b.y - s * 0.46f), s * 0.13f, StrokeCap.Round)
            drawLine(g, Offset(b.x + s * 0.15f, b.y - s * 0.6f), Offset(b.x + s * 0.36f, b.y - s * 0.46f), s * 0.13f, StrokeCap.Round)
            drawLine(g, Offset(b.x - s * 0.08f, b.y - s * 0.32f), Offset(b.x - s * 0.18f, b.y - s * 0.04f), s * 0.14f, StrokeCap.Round)
            drawLine(g, Offset(b.x + s * 0.08f, b.y - s * 0.32f), Offset(b.x + s * 0.18f, b.y - s * 0.04f), s * 0.14f, StrokeCap.Round)
            listOf(0.58f, 0.48f).forEach { f -> drawCircle(Color.White, s * 0.025f, Offset(b.x, b.y - s * f)) }
            return
        }
        val lata = if (c.semilla.mod(2) == 0) ROJO else LATA_AZUL
        val cuerpo = Rect(w * 0.12f, h * 0.38f, w * 0.88f, h * 0.9f)
        val elip = h * 0.06f
        val sube = h * 0.012f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left - 2 * u, cuerpo.bottom - elip * 0.2f), Size(cuerpo.width + 4 * u, elip * 1.4f))
        // Las tarjetas que asoman bajo la tapa.
        if (sube > 1f) {
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = cuerpo.left + cuerpo.width * (0.1f + 0.13f * i)
                val y = cuerpo.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRect(t(listOf(BLANCO, ROJO, ABETO)[i % 3], oscuro), Offset(x, y), Size(cuerpo.width * 0.12f, cuerpo.top - y + 2 * u))
                if (i % 3 == 0) drawCircle(t(ROJO, oscuro), 1.5f * u, Offset(x + cuerpo.width * 0.06f, y + 4 * u))
            }
        }
        // La lata redonda: el cilindro con su fondo curvo, copos de nieve y filetes dorados.
        val cil = Path().apply { moveTo(cuerpo.left, cuerpo.top); lineTo(cuerpo.right, cuerpo.top); lineTo(cuerpo.right, cuerpo.bottom); arcTo(Rect(cuerpo.left, cuerpo.bottom - elip, cuerpo.right, cuerpo.bottom + elip), 0f, 180f, false); close() }
        drawPath(cil, t(lata, oscuro))
        clipPath(cil) {
            for (k in 0 until 9) copo(Offset(cuerpo.left + cuerpo.width * azar(c.semilla, k + 40), cuerpo.top + cuerpo.height * azar(c.semilla, k + 50)), (3f + 3f * azar(c.semilla, k + 60)) * u, Color.White.copy(alpha = 0.55f), 0.8f * u)
            drawRect(Color.White.copy(alpha = 0.12f), Offset(cuerpo.left + cuerpo.width * 0.12f, cuerpo.top), Size(cuerpo.width * 0.08f, cuerpo.height + elip))
        }
        drawRect(t(ORO, oscuro), Offset(cuerpo.left, cuerpo.top + 2 * u), Size(cuerpo.width, 3 * u))
        drawArc(t(ORO, oscuro), 0f, 180f, false, Offset(cuerpo.left, cuerpo.bottom - elip - 4 * u), Size(cuerpo.width, elip * 2), style = Stroke(2.5f * u))
        // La tapa: una banda con su filete y el óvalo de arriba.
        val ty = cuerpo.top - sube
        val tapa = Rect(w * 0.1f, ty - h * 0.09f, w * 0.9f, ty + 1 * u)
        drawRect(t(mezcla(lata, Color.Black, 0.15f), oscuro), tapa.topLeft, tapa.size)
        drawArc(t(mezcla(lata, Color.Black, 0.15f), oscuro), 0f, 180f, true, Offset(tapa.left, tapa.bottom - elip * 0.8f), Size(tapa.width, elip * 1.6f))
        drawOval(t(mezcla(lata, Color.White, 0.15f), oscuro), Offset(tapa.left, tapa.top - elip * 0.8f), Size(tapa.width, elip * 1.6f))
        drawOval(t(ORO, oscuro), Offset(tapa.left, tapa.top - elip * 0.8f), Size(tapa.width, elip * 1.6f), style = Stroke(1.4f * u))
        drawLine(t(ORO, oscuro), Offset(tapa.left, tapa.center.y), Offset(tapa.right, tapa.center.y), 1.2f * u)
        // La etiqueta ovalada con el tema.
        // El óvalo es más ancho y más alto que la zona del texto: así el texto no toca su borde curvo.
        val r = Rect(w * rotulo.left - w * 0.07f, h * rotulo.top - h * 0.04f, w * rotulo.right + w * 0.07f, h * rotulo.bottom + h * 0.03f)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
        drawOval(t(BLANCO, oscuro), r.topLeft, r.size)
        drawOval(t(ORO, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(1.2f * u))
    }

    override val rotulo = Rect(0.2f, 0.54f, 0.8f, 0.84f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.calida
    override val salida = 0f
    override val placa = Rect(0.32f, 0.32f, 0.68f, 0.82f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la lata, de hojalata con sus círculos, y una blonda de papel calado al centro.
        val pl = if (oscuro) mezcla(PLATA, Color.Black, 0.55f) else PLATA
        drawRect(pl)
        val c = Offset(w / 2, h * 0.45f)
        var r = 20 * u
        while (r < h) { drawCircle(Color.White.copy(alpha = 0.12f), r, c, style = Stroke(1f * u)); r += 14 * u }
        val rb = w * 0.42f
        val blonda = if (oscuro) mezcla(BLANCO, Color.Black, 0.5f) else BLANCO
        for (k in 0 until 40) {
            val a = k * 2 * Math.PI / 40
            drawCircle(blonda, rb * 0.09f, Offset(c.x + rb * cos(a).toFloat(), c.y + rb * sin(a).toFloat()))
        }
        drawCircle(blonda, rb, c)
        for (anillo in listOf(0.85f, 0.65f, 0.45f)) {
            val n = (40 * anillo).toInt()
            for (k in 0 until n) {
                val a = k * 2 * Math.PI / n
                drawCircle(pl.copy(alpha = 0.6f), rb * 0.025f, Offset(c.x + rb * anillo * cos(a).toFloat(), c.y + rb * anillo * sin(a).toFloat()))
            }
        }
        val pared = 14 * u
        listOf(Rect(0f, 0f, w, pared), Rect(0f, h - pared, w, h), Rect(0f, 0f, pared, h), Rect(w - pared, 0f, w, h)).forEach { rr -> drawRect(t(ROJO, oscuro), rr.topLeft, rr.size) }
        drawRect(t(ORO, oscuro), Offset(pared, pared), Size(w - 2 * pared, h - 2 * pared), style = Stroke(1.6f * u))
        sombraInterior(Rect(pared, pared, w - pared, h - pared), 16 * u, 0.25f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) BLANCO else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    override fun forma(semilla: Int): Shape = RoundedCornerShape(3.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El doble filete rojo y verde, unos copos tenues y una ramita de acebo en la esquina de abajo.
        val m = 4 * u * g
        drawRoundRect(ROJO.copy(alpha = 0.6f), Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(2 * u), style = Stroke(1.2f * u * g))
        drawRoundRect(ABETO.copy(alpha = 0.5f), Offset(m + 2.6f * u * g, m + 2.6f * u * g), Size(w - 2 * m - 5.2f * u * g, h - 2 * m - 5.2f * u * g), CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        for (k in 0 until (if (grande) 14 else 6)) copo(Offset(w * azar(semilla, k), h * azar(semilla, k + 20)), (2.5f + 2.5f * azar(semilla, k + 40)) * u * g, ABETO.copy(alpha = 0.1f), 0.6f * u)
        acebo(Offset(m + 9 * u * g, h - m - 7 * u * g), 16 * u * g, ABETO.copy(alpha = 0.9f), ROJO)
    }

    override val margenes = MargenesFicha(14.dp, 16.dp, 16.dp, 26.dp, 28.dp, 32.dp)
    override val proporcionFicha = 0.85f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre rojo de una carta.
            "hecho" -> selloHecho(ROJO, p.color)
            "convence" -> acebo(Offset(c.x, c.y + s * 0.06f), s * 0.9f, ABETO, ROJO)
            "piensa" -> copo(c, s * 0.44f, LATA_AZUL, s * 0.08f)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un moño rojo. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x - s * 0.46f, c.y - s * 0.3f); lineTo(c.x - s * 0.46f, c.y + s * 0.2f); close() }, ROJO)
        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + s * 0.46f, c.y - s * 0.3f); lineTo(c.x + s * 0.46f, c.y + s * 0.2f); close() }, ROJO)
        drawLine(ROJO_OSC, c, Offset(c.x - s * 0.2f, c.y + s * 0.46f), s * 0.1f)
        drawLine(ROJO_OSC, c, Offset(c.x + s * 0.2f, c.y + s * 0.46f), s * 0.1f)
        drawCircle(ROJO_OSC, s * 0.12f, c)
    }

    override val desorden = 2f
}

/**
 * Abismo: el rack estanco del batiscafo. Planchas de acero remachadas; arriba, el casco con un ojo de buey de latón por el que
 * se ve el agua honda, con burbujas y un pez, entre tuberías con sus bridas y un manómetro; abajo, piso de chapa estriada y
 * una válvula roja. Cada tema es una caja estanca gris verdosa con su junta de goma, dos cierres de palanca y el tema en una
 * placa de aluminio estampada; asoman pizarras bajo la tapa. Por dentro, espuma gris en cuadritos. Las fichas son pizarras de
 * buceo con su agujero y su cordón.
 */
object IdeasAbismo : DisenoIdeas {
    private val ACERO = Color(0xFF5E7078)
    private val ACERO_LUZ = Color(0xFF8496A0)
    private val ACERO_OSC = Color(0xFF34424A)
    private val CAJA = Color(0xFF4E6A62)
    private val CAJA_OSC = Color(0xFF2E443E)
    private val LATON = Color(0xFFB8913E)
    private val LATON_OSC = Color(0xFF7A5E22)
    private val AGUA = Color(0xFF0A3A5C)
    private val AGUA_OSC = Color(0xFF04182A)
    private val ESPUMA = Color(0xFF2A2E30)
    private val BLANCO = Color(0xFFF2F6F6)
    private val TINTA = Color(0xFF0C2430)
    private val AMARILLO = Color(0xFFE0B83A)
    private val ALUMINIO = Color(0xFFD6DCDE)
    private val ROJO = Color(0xFFB0302A)
    private val VERDE = Color(0xFF3E9A62)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.45f
    override val corona = 0.34f
    override val base = 0.1f
    override val lado = 0.08f
    override val hueco = 0.06f

    private fun DrawScope.remachesLinea(a: Offset, b: Offset, paso: Float, color: Color) {
        val u = density
        val l = kotlin.math.hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt().coerceAtLeast(1)
        for (k in 0..n) drawCircle(color, 1.3f * u, Offset(a.x + (b.x - a.x) * k / n, a.y + (b.y - a.y) * k / n))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val piso = d.base.top + d.base.height * 0.3f
        // Las planchas de acero remachadas, más oscuras hacia los costados por la curva del casco.
        drawRect(Brush.horizontalGradient(listOf(t(ACERO_OSC, oscuro), t(ACERO, oscuro), t(ACERO_LUZ, oscuro), t(ACERO, oscuro), t(ACERO_OSC, oscuro))), Offset.Zero, Size(w, piso))
        val pw = w / 3; val ph = 90 * u
        var y = 0f
        while (y < piso) {
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, y), Offset(w, y), 1.4f * u)
            remachesLinea(Offset(6 * u, y + 5 * u), Offset(w - 6 * u, y + 5 * u), 12 * u, t(ACERO_LUZ, oscuro))
            y += ph
        }
        for (k in 1 until 3) drawLine(Color.Black.copy(alpha = 0.4f), Offset(pw * k, 0f), Offset(pw * k, piso), 1.4f * u)
        // Los rieles de ángulo bajo cada fila, con su franja de aviso.
        d.filas.forEach { f ->
            val r = Rect(w * 0.03f, f.bottom - 1 * u, w * 0.97f, f.bottom + maxOf(d.separacion * 0.45f, 7 * u))
            drawRect(t(ACERO_OSC, oscuro), r.topLeft, r.size)
            drawLine(t(ACERO_LUZ, oscuro), r.topLeft, Offset(r.right, r.top), 1.2f * u)
            clipRect(r.left, r.bottom - 3 * u, r.right, r.bottom) {
                var x = r.left
                while (x < r.right) { drawLine(AMARILLO.copy(alpha = 0.85f), Offset(x, r.bottom), Offset(x + 3 * u, r.bottom - 3 * u), 2 * u); x += 6 * u }
            }
        }
        // Las tuberías del techo, con sus bridas, y el manómetro.
        val ty = d.corona.top + ch * 0.18f
        listOf(ty, ty + 12 * u).forEach { yy ->
            drawRect(t(ACERO_LUZ, oscuro), Offset(0f, yy - 3.5f * u), Size(w, 7 * u))
            drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, yy - 1.5f * u), Offset(w, yy - 1.5f * u), 1f * u)
            listOf(0.08f, 0.92f).forEach { f -> drawRect(t(ACERO_OSC, oscuro), Offset(w * f - 3 * u, yy - 5.5f * u), Size(6 * u, 11 * u)) }
        }
        manometro(Offset(w * 0.15f, d.corona.top + ch * 0.6f), ch * 0.17f, t(LATON, oscuro), t(BLANCO, oscuro), ROJO, 0.78f)
        // El ojo de buey: el aro de latón con sus pernos y el agua honda, con burbujas y un pez.
        val oc = Offset(w / 2, d.corona.top + ch * 0.58f)
        val or = ch * 0.34f
        drawCircle(t(LATON, oscuro), or * 1.22f, oc)
        drawCircle(t(LATON_OSC, oscuro), or * 1.22f, oc, style = Stroke(1.4f * u))
        for (k in 0 until 10) { val a = k * 2 * Math.PI / 10; drawCircle(t(LATON_OSC, oscuro), or * 0.06f, Offset(oc.x + or * 1.11f * cos(a).toFloat(), oc.y + or * 1.11f * sin(a).toFloat())) }
        drawCircle(Brush.radialGradient(listOf(AGUA, AGUA_OSC), Offset(oc.x - or * 0.3f, oc.y - or * 0.3f), or * 1.3f), or, oc)
        val pez = Offset(oc.x + or * 0.2f, oc.y + or * 0.15f)
        drawOval(Color(0xFF6EA8C8).copy(alpha = 0.4f), Offset(pez.x - or * 0.22f, pez.y - or * 0.08f), Size(or * 0.4f, or * 0.16f))
        drawPath(Path().apply { moveTo(pez.x + or * 0.16f, pez.y); lineTo(pez.x + or * 0.32f, pez.y - or * 0.1f); lineTo(pez.x + or * 0.32f, pez.y + or * 0.1f); close() }, Color(0xFF6EA8C8).copy(alpha = 0.4f))
        for (k in 0 until 6) drawCircle(Color.White.copy(alpha = 0.45f), (1.5f + 2f * azar(77, k)) * u, Offset(oc.x - or * 0.5f + or * 0.2f * azar(78, k), oc.y + or * 0.6f - k * or * 0.2f), style = Stroke(0.8f * u))
        drawArc(Color.White.copy(alpha = 0.25f), 200f, 60f, false, Offset(oc.x - or * 0.85f, oc.y - or * 0.85f), Size(or * 1.7f, or * 1.7f), style = Stroke(3 * u))
        // El piso de chapa estriada y la válvula roja.
        drawRect(t(ACERO_OSC, oscuro), Offset(0f, piso), Size(w, d.base.bottom - piso))
        var px = 0f; var fila = 0
        var py = piso + 4 * u
        while (py < d.base.bottom) {
            px = if (fila % 2 == 0) 3 * u else 9 * u
            while (px < w) { drawLine(t(ACERO_LUZ, oscuro).copy(alpha = 0.6f), Offset(px, py), Offset(px + 5 * u, py - 2 * u), 1.4f * u, StrokeCap.Round); px += 12 * u }
            py += 6 * u; fila++
        }
        val vv = Offset(w * 0.9f, d.base.top + d.base.height * 0.55f)
        drawCircle(t(ROJO, oscuro), d.base.height * 0.3f, vv, style = Stroke(2.4f * u))
        for (k in 0 until 3) { val a = k * 2 * Math.PI / 3; drawLine(t(ROJO, oscuro), vv, Offset(vv.x + d.base.height * 0.3f * cos(a).toFloat(), vv.y + d.base.height * 0.3f * sin(a).toFloat()), 1.6f * u) }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(ALUMINIO, oscuro), r.topLeft, r.size)
            drawRect(t(ACERO, oscuro), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.8f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        if (c.vacio) {
            // Un lugar libre: una linterna de buceo amarilla.
            val r = Rect(w * 0.24f, h * 0.82f, w * 0.7f, h * 0.94f)
            drawRoundRect(t(AMARILLO, oscuro), r.topLeft, r.size, CornerRadius(r.height * 0.3f))
            drawRect(t(ACERO_OSC, oscuro), Offset(r.right - 4 * u, r.top - 2 * u), Size(10 * u, r.height + 4 * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + r.width * 0.3f, r.top), Size(4 * u, r.height))
            return
        }
        val caja = Rect(w * 0.08f, h * 0.38f, w * 0.92f, h * 0.95f)
        val sube = h * 0.01f * c.n.coerceIn(0, 6) + ab * h * 0.16f
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(caja.left, caja.bottom - h * 0.03f), Size(caja.width, h * 0.06f))
        // Las pizarras blancas que asoman bajo la tapa.
        if (sube > 1f) {
            drawRect(Color(0xFF0A1012), Offset(caja.left + 3 * u, caja.top - sube), Size(caja.width - 6 * u, sube + 2 * u))
            for (i in 0 until c.n.coerceIn(0, 6)) {
                val x = caja.left + caja.width * (0.08f + 0.14f * i)
                val y = caja.top - sube * (0.3f + 0.6f * azar(c.semilla, i))
                drawRoundRect(t(BLANCO, oscuro), Offset(x, y), Size(caja.width * 0.12f, caja.top - y + 4 * u), CornerRadius(3 * u))
            }
        }
        // La caja estanca, con su junta de goma.
        drawRoundRect(t(CAJA, oscuro), caja.topLeft, caja.size, CornerRadius(5 * u))
        drawRoundRect(t(CAJA_OSC, oscuro), Offset(caja.left + 4 * u, caja.top + 8 * u), Size(caja.width - 8 * u, caja.height - 12 * u), CornerRadius(3 * u), style = Stroke(1f * u))
        val tapa = Rect(w * 0.06f, caja.top - sube - h * 0.13f, w * 0.94f, caja.top - sube + 2 * u)
        drawRoundRect(t(CAJA, oscuro), tapa.topLeft, tapa.size, CornerRadius(5 * u))
        drawRect(Color.Black, Offset(tapa.left + 2 * u, tapa.bottom - 2 * u), Size(tapa.width - 4 * u, 2.5f * u))
        drawCircle(t(LATON, oscuro), 4 * u, tapa.center)
        drawCircle(t(LATON_OSC, oscuro), 4 * u, tapa.center, style = Stroke(1f * u))
        // Los dos cierres de palanca.
        listOf(0.24f, 0.76f).forEach { fx ->
            val x = w * fx
            drawRoundRect(t(ACERO_LUZ, oscuro), Offset(x - 6 * u, tapa.bottom - 5 * u), Size(12 * u, 14 * u), CornerRadius(2 * u))
            drawLine(t(ACERO_OSC, oscuro), Offset(x - 3 * u, tapa.bottom - 2 * u), Offset(x + 3 * u, tapa.bottom - 2 * u), 1.4f * u)
            drawCircle(t(ACERO_OSC, oscuro), 1.4f * u, Offset(x, tapa.bottom + 6 * u))
        }
        // La placa de aluminio estampada con el tema, con sus cuatro tornillos.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(t(ALUMINIO, oscuro), r.topLeft, r.size, CornerRadius(2 * u))
        drawRoundRect(t(ACERO, oscuro), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        listOf(Offset(r.left + 4 * u, r.top + 4 * u), Offset(r.right - 4 * u, r.top + 4 * u), Offset(r.left + 4 * u, r.bottom - 4 * u), Offset(r.right - 4 * u, r.bottom - 4 * u)).forEach { p ->
            drawCircle(t(ACERO_OSC, oscuro), 1.3f * u, p)
            drawLine(t(ALUMINIO, oscuro), Offset(p.x - 0.8f * u, p.y), Offset(p.x + 0.8f * u, p.y), 0.5f * u)
        }
    }

    override val rotulo = Rect(0.18f, 0.54f, 0.82f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.batiscafo
    override val salida = 0f
    override val placa = Rect(0.32f, 0.4f, 0.68f, 0.9f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Espuma gris en cuadritos (la que se arranca para que quepa cada cosa).
        drawRect(t(ESPUMA, oscuro))
        val p = 12 * u
        var x = 0f
        while (x < w) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(x, 0f), Offset(x, h), 1f * u); drawLine(Color.White.copy(alpha = 0.05f), Offset(x + 1.5f * u, 0f), Offset(x + 1.5f * u, h), 0.8f * u); x += p }
        var y = 0f
        while (y < h) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, y), Offset(w, y), 1f * u); y += p }
        // Las paredes de la caja, con la junta negra.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CAJA, oscuro), r.topLeft, r.size) }
        drawRect(Color.Black, Offset(pl * 0.4f, pl * 0.4f), Size(w - pl * 0.8f, h - pl * 0.8f), style = Stroke(2.5f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = BLANCO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(10.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(BLANCO.copy(alpha = 0.5f))
        // La superficie mate, apenas moteada.
        for (k in 0 until (if (grande) 80 else 30)) drawCircle(TINTA.copy(alpha = 0.05f), 0.8f * u, Offset(w * azar(semilla, k), h * azar(semilla, k + 200)))
        // El agujero de arriba a la izquierda y su cordón elástico amarillo.
        val hoyo = Offset(7 * u * g, 7 * u * g)
        drawCircle(Color.Black.copy(alpha = 0.55f), 2.6f * u * g, hoyo)
        drawCircle(TINTA.copy(alpha = 0.3f), 3.6f * u * g, hoyo, style = Stroke(0.8f * u))
        drawPath(Path().apply { moveTo(hoyo.x, hoyo.y); quadraticTo(hoyo.x - 6 * u * g, hoyo.y + 4 * u * g, 0f, hoyo.y + 10 * u * g) }, AMARILLO, style = Stroke(1.6f * u * g, cap = StrokeCap.Round))
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 14.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La cota alcanzada: un sello verde.
            "hecho" -> selloHecho(VERDE, p.color)
            // La aguja del profundímetro en la zona verde.
            "convence" -> {
                drawCircle(TINTA, s * 0.42f, c, style = Stroke(s * 0.07f))
                drawArc(VERDE, 200f, 90f, false, Offset(c.x - s * 0.3f, c.y - s * 0.3f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.12f))
                drawLine(TINTA, c, Offset(c.x - s * 0.2f, c.y - s * 0.22f), s * 0.07f, StrokeCap.Round)
            }
            // Burbujas que suben.
            "piensa" -> listOf(Offset(-0.12f, 0.26f) to 0.14f, Offset(0.1f, 0.0f) to 0.11f, Offset(-0.04f, -0.28f) to 0.08f).forEach { (o, r) -> drawCircle(Color(0xFF0A5C8C), s * r, Offset(c.x + o.x * s, c.y + o.y * s), style = Stroke(s * 0.06f)) }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un mosquetón. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRoundRect(ACERO_OSC, Offset(c.x - s * 0.24f, c.y - s * 0.44f), Size(s * 0.48f, s * 0.88f), CornerRadius(s * 0.24f), style = Stroke(s * 0.1f))
        drawLine(LATON, Offset(c.x + s * 0.24f, c.y - s * 0.2f), Offset(c.x + s * 0.06f, c.y + s * 0.18f), s * 0.08f, StrokeCap.Round)
    }

    override val desorden = 1f
}

/**
 * URSS: la kartoteka metálica de la biblioteca. Un mueble de acero esmaltado gris verdoso con muchos cajoncitos; arriba, un
 * cartel constructivista en la pared, una pila de libros y una lámpara de pantalla verde encendida; abajo, el zócalo y un
 * parquet en espiga. Cada tema es un cajón de la kartoteka con su portaetiquetas de latón, su tirador y el botón de la varilla
 * que traba las fichas; los más llenos quedan entreabiertos con las fichas y sus pestañas. Por dentro, el cajón de acero con
 * la varilla al centro. Las fichas son fichas de catálogo con el agujero para la varilla abajo.
 */
object IdeasUrss : DisenoIdeas {
    private val ACERO = Color(0xFF7A8478)
    private val ACERO_LUZ = Color(0xFF9AA498)
    private val ACERO_OSC = Color(0xFF4E5650)
    private val LATON = Color(0xFFB89A50)
    private val LATON_OSC = Color(0xFF7A6430)
    private val ROJO = Color(0xFFC1121F)
    private val NEGRO = Color(0xFF161414)
    private val CARTULINA = Color(0xFFEFE6D2)
    private val CARTULINA_OSC = Color(0xFFD8CCB0)
    private val PARQUET = Color(0xFF8A6438)
    private val PARQUET_OSC = Color(0xFF5E4024)
    private val VERDE_LAMPARA = Color(0xFF2E5E3A)
    private val LUZ = Color(0xFFF2DC8A)
    private val TINTA = Color(0xFF161414)
    private val CARTEL = Color(0xFFE8DCC0)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 3
    override val proporcion = 1.25f
    override val corona = 0.32f
    override val base = 0.12f
    override val lado = 0.065f
    override val hueco = 0.022f

    private const val SALIDA = 0.16f

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        // El cartel constructivista en la pared, a la izquierda: una franja roja en diagonal y un círculo negro.
        val cartel = Rect(w * 0.06f, d.corona.top + ch * 0.06f, w * 0.3f, d.corona.bottom - 14 * u)
        drawRect(t(CARTEL, oscuro), cartel.topLeft, cartel.size)
        clipRect(cartel.left, cartel.top, cartel.right, cartel.bottom) {
            rotate(-28f, cartel.center) { drawRect(t(ROJO, oscuro), Offset(cartel.left - cartel.width * 0.3f, cartel.center.y - cartel.height * 0.12f), Size(cartel.width * 1.6f, cartel.height * 0.22f)) }
            drawCircle(t(NEGRO, oscuro), cartel.width * 0.2f, Offset(cartel.left + cartel.width * 0.68f, cartel.top + cartel.height * 0.3f))
            drawRect(t(NEGRO, oscuro), Offset(cartel.left + cartel.width * 0.1f, cartel.bottom - cartel.height * 0.2f), Size(cartel.width * 0.6f, cartel.height * 0.06f))
        }
        drawRect(Color.Black.copy(alpha = 0.25f), cartel.topLeft, cartel.size, style = Stroke(1f * u))
        // La tapa del mueble; encima, una pila de libros y la lámpara de pantalla verde encendida.
        val tapa = Rect(0f, d.corona.bottom - 12 * u, w, d.corona.bottom - 4 * u)
        drawRect(t(ACERO_LUZ, oscuro), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, tapa.bottom), Size(w, 2 * u))
        listOf(Color(0xFF8A2A24), Color(0xFF2E3E5A), Color(0xFF5A5A3A), Color(0xFFB08A4A)).forEachIndexed { i, col ->
            drawRect(t(col, oscuro), Offset(w * (0.44f + 0.008f * i), tapa.top - (i + 1) * ch * 0.07f), Size(w * (0.18f - 0.015f * i), ch * 0.065f))
        }
        val lb = Offset(w * 0.8f, tapa.top)
        mancha(Offset(lb.x, lb.y - ch * 0.15f), ch * (if (oscuro) 0.9f else 0.6f), LUZ.copy(alpha = if (oscuro) 0.3f else 0.22f))
        drawRect(t(LATON, oscuro), Offset(lb.x - ch * 0.14f, lb.y - 4 * u), Size(ch * 0.28f, 4 * u))
        drawLine(t(LATON, oscuro), Offset(lb.x, lb.y - 4 * u), Offset(lb.x, lb.y - ch * 0.42f), 2 * u)
        val pantalla = Rect(lb.x - ch * 0.26f, lb.y - ch * 0.56f, lb.x + ch * 0.26f, lb.y - ch * 0.38f)
        drawArc(t(VERDE_LAMPARA, oscuro), 180f, 180f, true, Offset(pantalla.left, pantalla.top), Size(pantalla.width, pantalla.height * 2))
        drawLine(LUZ, Offset(pantalla.left + 3 * u, pantalla.bottom), Offset(pantalla.right - 3 * u, pantalla.bottom), 2 * u)
        // El cuerpo de acero esmaltado, con el rebaje de cada cajón.
        val cuerpo = Rect(w * 0.015f, d.corona.bottom - 4 * u, w * 0.985f, d.base.top + d.base.height * 0.4f)
        drawRect(Brush.horizontalGradient(listOf(t(ACERO_OSC, oscuro), t(ACERO, oscuro), t(ACERO_LUZ, oscuro), t(ACERO, oscuro)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { r -> drawRect(Color.Black.copy(alpha = 0.55f), Offset(r.left - 1.2f * u, r.top - 1.2f * u), Size(r.width + 2.4f * u, r.height + 2.4f * u)) }
        drawRect(t(ACERO_OSC, oscuro), cuerpo.topLeft, cuerpo.size, style = Stroke(1.6f * u))
        // El parquet en espiga.
        val piso = Rect(0f, cuerpo.bottom, w, d.base.bottom)
        drawRect(t(PARQUET_OSC, oscuro), piso.topLeft, piso.size)
        clipRect(piso.left, piso.top, piso.right, piso.bottom) {
            val l = 16 * u; val a = 5 * u
            var x = -l; var k = 0
            while (x < w + l) {
                rotate(if (k % 2 == 0) 45f else -45f, Offset(x, piso.center.y)) { drawRect(t(mezcla(PARQUET_OSC, PARQUET, 0.5f + 0.5f * azar(3, k)), oscuro), Offset(x - l / 2, piso.center.y - a / 2), Size(l, a - 0.6f * u)) }
                x += a * 1.42f; k++
            }
        }
        drawRect(t(ACERO_OSC, oscuro), Offset(cuerpo.left + 6 * u, cuerpo.bottom), Size(cuerpo.width - 12 * u, 4 * u))
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(t(CARTULINA, oscuro), r.topLeft, r.size)
            drawRect(t(LATON, oscuro), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3 * u, r.height + 3 * u), style = Stroke(2 * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Los cajones más llenos quedan entreabiertos y asoman las fichas, con alguna pestaña roja.
        val entre = if (c.vacio) 0f else h * (0.03f + 0.018f * c.n.coerceIn(0, 6))
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) {
            drawRect(Color(0xFF101210), Offset.Zero, Size(w, arriba + 2 * u))
            val k = c.n.coerceIn(0, 7)
            for (i in 0 until k) {
                val y0 = arriba * (0.15f + 0.08f * i).coerceAtMost(0.8f)
                drawRect(t(if (i % 2 == 0) CARTULINA else CARTULINA_OSC, oscuro), Offset(w * 0.08f, y0), Size(w * 0.84f, arriba + 4 * u - y0))
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(w * 0.08f, y0), Offset(w * 0.92f, y0), 0.6f * u)
                if (i % 3 == 0) drawRect(t(ROJO, oscuro), Offset(w * (0.2f + 0.18f * (i % 4)), y0 - 3 * u), Size(w * 0.12f, 3.5f * u))
            }
        }
        // El frente de acero esmaltado.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4 * u, f.right, f.bottom + 2 * u), 2 * u, 8 * u * ab, 0.3f * ab)
        drawRoundRect(t(ACERO, oscuro), f.topLeft, f.size, CornerRadius(2 * u))
        bisel(f, Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.35f), 1.2f * u)
        // El portaetiquetas de latón (vacío en un lugar libre) con su tarjeta.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (!c.vacio) drawRect(t(CARTULINA, oscuro), r.topLeft, r.size)
        drawPath(Path().apply { moveTo(r.left - 2 * u, r.top - 2 * u); lineTo(r.left - 2 * u, r.bottom + 2 * u); lineTo(r.right + 2 * u, r.bottom + 2 * u); lineTo(r.right + 2 * u, r.top - 2 * u) }, t(LATON, oscuro), style = Stroke(2.2f * u))
        listOf(r.left - 5 * u, r.right + 5 * u).forEach { x -> drawCircle(t(LATON_OSC, oscuro), 1.2f * u, Offset(x, r.center.y)) }
        // El tirador y el botón de la varilla.
        val ty = h * 0.72f + baja
        drawRoundRect(t(LATON, oscuro), Offset(w * 0.32f, ty - 2.5f * u), Size(w * 0.36f, 5 * u), CornerRadius(2.5f * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.32f, ty + 1.5f * u), Size(w * 0.36f, 1.5f * u), CornerRadius(1 * u))
        val boton = Offset(w / 2, h * 0.88f + baja)
        drawCircle(t(LATON_OSC, oscuro), 3.4f * u, boton)
        drawCircle(t(LATON, oscuro), 2.2f * u, Offset(boton.x - 0.5f * u, boton.y - 0.5f * u))
    }

    override val rotulo = Rect(0.16f, 0.14f, 0.84f, 0.56f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.russo
    override val salida = SALIDA
    override val placa = Rect(0.24f, 0.04f, 0.76f, 0.38f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del cajón de acero, con la varilla que corre al centro.
        drawRect(t(ACERO_OSC, oscuro))
        for (k in 0 until 30) drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, h * azar(31, k)), Offset(w, h * azar(31, k) + 2 * u), 1f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(w / 2 - 6 * u, 0f), Size(12 * u, h))
        drawRect(t(ACERO_LUZ, oscuro), Offset(w / 2 - 2.5f * u, 0f), Size(5 * u, h))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(w / 2 - 1 * u, 0f), Offset(w / 2 - 1 * u, h), 1f * u)
        drawCircle(t(LATON, oscuro), 7 * u, Offset(w / 2, h - 22 * u))
        // Las paredes del cajón, de acero más claro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(ACERO, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    override fun tintaInterior(oscuro: Boolean) = CARTULINA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    /** La ficha de catálogo, con el agujero de la varilla abajo al centro. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        fillType = PathFillType.EvenOdd
        addRect(Rect(0f, 0f, s.width, s.height))
        val r = minOf(s.width, s.height).coerceAtMost(s.width * 0.3f) * 0.03f
        addOval(Rect(s.width / 2 - r, s.height - r * 3.6f, s.width / 2 + r, s.height - r * 1.6f))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(CARTULINA.copy(alpha = 0.4f))
        // El filete rojo de arriba, la línea vertical del margen y el refuerzo del agujero.
        drawLine(ROJO.copy(alpha = 0.7f), Offset(0f, 3 * u * g), Offset(w, 3 * u * g), 1f * u * g)
        drawLine(ROJO.copy(alpha = 0.35f), Offset(10 * u * g, 0f), Offset(10 * u * g, h), 0.8f * u)
        val r = minOf(w, h).coerceAtMost(w * 0.3f) * 0.03f
        drawCircle(TINTA.copy(alpha = 0.25f), r * 1.5f, Offset(w / 2, h - r * 2.6f), style = Stroke(0.8f * u))
    }

    override val margenes = MargenesFicha(12.dp, 16.dp, 24.dp, 22.dp, 28.dp, 46.dp)
    override val proporcionFicha = 1.25f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.82f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello del plan cumplido.
            "hecho" -> selloHecho(ROJO, p.color, cuadrado = true)
            "convence" -> drawPath(estrella(c, s * 0.48f, 5, 0.42f), ROJO)
            // Un lápiz.
            "piensa" -> rotate(-45f, c) {
                drawRect(Color(0xFF3A3A3A), Offset(c.x - s * 0.36f, c.y - s * 0.09f), Size(s * 0.56f, s * 0.18f))
                drawPath(Path().apply { moveTo(c.x + s * 0.2f, c.y - s * 0.09f); lineTo(c.x + s * 0.42f, c.y); lineTo(c.x + s * 0.2f, c.y + s * 0.09f); close() }, Color(0xFFD8B880))
                drawRect(ROJO, Offset(c.x - s * 0.44f, c.y - s * 0.09f), Size(s * 0.08f, s * 0.18f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una pestaña roja de separador. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.46f, c.y + s * 0.36f); lineTo(c.x - s * 0.3f, c.y - s * 0.36f); lineTo(c.x + s * 0.3f, c.y - s * 0.36f); lineTo(c.x + s * 0.46f, c.y + s * 0.36f); close() }, ROJO)
        drawLine(CARTULINA, Offset(c.x - s * 0.18f, c.y), Offset(c.x + s * 0.18f, c.y), s * 0.08f)
    }

    override val desorden = 0f
}
