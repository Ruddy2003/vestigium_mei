package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Contemporánea: las piezas comunes de Urbe, Terminal, Vapor, Napoleón, Art déco, Botánico, Navidad, Abismo y URSS
// =====================================================================

/** El color de una pieza apagada (campo sin habilitar): casi sin color, con su misma luz. */
private fun apagar(c: Color, habilitado: Boolean): Color {
    if (habilitado) return c
    val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
    return Color(c.red + (l - c.red) * 0.65f, c.green + (l - c.green) * 0.65f, c.blue + (l - c.blue) * 0.65f, c.alpha)
}

/**
 * Los renglones tenues de un campo de varios renglones: uno cada 24 sp a partir de [arriba] (el margen de arriba) y hasta
 * [abajo], de [izq] a [der]; de raya continua o, con [punteado] (el paso entre puntos), de puntos.
 */
private fun DrawScope.renglones(izq: Float, der: Float, arriba: Float, abajo: Float, color: Color, grosor: Float, punteado: Float = 0f) {
    val paso = maxOf(24.sp.toPx(), 1f)
    var y = arriba + paso; var n = 0
    while (y < abajo && n < 200) {
        if (punteado > 0f) {
            var x = izq; var k = 0
            while (x < der && k < 2000) { drawCircle(color, grosor, Offset(x, y)); x += maxOf(punteado, 1f); k++ }
        } else drawLine(color, Offset(izq, y), Offset(der, y), grosor)
        y += paso; n++
    }
}

/** Cuánto se nota un separador: la mitad cuando va tenue (la pantalla le pidió un color propio). */
private fun fuerza(e: EstadoSeparador): Float = if (e.tenue) 0.5f else 1f

/** El color del adorno de un botón de texto apagado: casi sin color y a media luz. */
private fun apagarBoton(c: Color, habilitado: Boolean): Color = if (habilitado) c else apagar(c, false).copy(alpha = c.alpha * 0.5f)

/** Un paralelogramo inclinado hacia la derecha, de [izq] a [der] y de [arriba] a [abajo], con [sesgo] de corrimiento. */
private fun paralelogramo(izq: Float, arriba: Float, der: Float, abajo: Float, sesgo: Float): Path = Path().apply {
    moveTo(izq + sesgo, arriba); lineTo(der, arriba); lineTo(der - sesgo, abajo); lineTo(izq, abajo); close()
}

/**
 * [n] puntos al azar en el anillo del margen de una tarjeta de [w]×[h] (el margen mide [m]), a [orilla] de los bordes y
 * fuera de donde va el texto; siempre los mismos para una [semilla]. Cada uno lleva un número de 0 a 1 para variar su
 * tamaño o su brillo.
 */
private fun puntosMargen(w: Float, h: Float, m: Float, n: Int, semilla: Int, orilla: Float): List<Pair<Offset, Float>> {
    val perimetro = 2 * w + 2 * h
    val hondo = maxOf(m - 2 * orilla, 0f)
    return (0 until n).map { k ->
        val s = azar(semilla, k) * perimetro
        val d = orilla + azar(semilla, k + 50) * hondo
        val p = when {
            s < w -> Offset(s, d)
            s < 2 * w -> Offset(s - w, h - d)
            s < 2 * w + h -> Offset(d, s - 2 * w)
            else -> Offset(w - d, s - 2 * w - h)
        }
        Offset(p.x.coerceIn(orilla, maxOf(w - orilla, orilla)), p.y.coerceIn(orilla, maxOf(h - orilla, orilla))) to azar(semilla, k + 99)
    }
}

/** Una luna en creciente: el disco de [luz] mordido por otro del color del [cielo], corrido hacia la derecha y arriba. */
private fun DrawScope.lunaCreciente(c: Offset, r: Float, luz: Color, cielo: Color) {
    drawCircle(luz, r, c)
    drawCircle(cielo, r * 0.84f, Offset(c.x + r * 0.42f, c.y - r * 0.24f))
}

/**
 * Urbe: el campo es la ventanilla de cristal líquido de la obliteradora del autobús, como el buscador de su biblioteca:
 * la chapa naranja de esquinas redondas con sus tornillos de aluminio a los lados, el bisel de hierro y el cristal verdoso
 * liso, con su rejilla de segmentos apenas visible. En la orilla de arriba, el piloto: apagado, verde con foco, rojo con un
 * error. De noche (modo oscuro), el cristal se apaga a un verde hondo y la letra se vuelve clara.
 */
object ComunUrbe : DisenoComun {
    private val NARANJA = Color(0xFFE08A2C)
    private val NARANJA_OSC = Color(0xFFA85E16)
    private val HIERRO = Color(0xFF2C3330)
    private val ALUMINIO = Color(0xFFB4B8BA)
    private val CRISTAL_LIQUIDO = Color(0xFFB9C3A0)
    private val CRISTAL_TINTA = Color(0xFF1F2A1E)
    private val PERSIANA = Color(0xFF2E5234)
    private val PERSIANA_LUZ = Color(0xFF7FB88A)
    private val VERDE_PILOTO = Color(0xFF6BE07A)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100E0C), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFD9E3C4) else CRISTAL_TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8A9A84) else Color(0xFF59654F)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) PERSIANA_LUZ else PERSIANA
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08070) else Color(0xFF8E2620)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        // La chapa naranja, con su sombra, su canto y su brillo de arriba.
        val chapa = Rect(1 * u, 1 * u, w - 1 * u, h - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(chapa.left, chapa.top + 1.5f * u), chapa.size, CornerRadius(8 * u))
        drawRoundRect(c(NARANJA), chapa.topLeft, chapa.size, CornerRadius(8 * u))
        drawRoundRect(c(NARANJA_OSC), chapa.topLeft, chapa.size, CornerRadius(8 * u), style = Stroke(1.2f * u))
        drawLine(Color.White.copy(alpha = 0.25f), Offset(chapa.left + 8 * u, chapa.top + 1.4f * u), Offset(chapa.right - 8 * u, chapa.top + 1.4f * u), 1 * u)
        // Los tornillos de los lados: uno a media altura o, si el campo es alto, dos.
        val alturas = if (h > 90 * u) listOf(0.25f, 0.75f) else listOf(0.5f)
        alturas.forEach { f ->
            listOf(chapa.left + 3 * u, chapa.right - 3 * u).forEach { x ->
                val p = Offset(x, chapa.top + chapa.height * f)
                drawCircle(Color.Black.copy(alpha = 0.3f), 1.9f * u, Offset(p.x, p.y + 0.4f * u))
                drawCircle(c(ALUMINIO), 1.7f * u, p)
                drawLine(c(HIERRO), Offset(p.x, p.y - 1.1f * u), Offset(p.x, p.y + 1.1f * u), 0.6f * u)
            }
        }
        // El cristal líquido en su bisel de hierro.
        val vent = Rect(chapa.left + 6 * u, chapa.top + 5 * u, chapa.right - 6 * u, chapa.bottom - 4.5f * u)
        drawRoundRect(c(HIERRO), vent.inflate(1.2f * u).topLeft, vent.inflate(1.2f * u).size, CornerRadius(4 * u))
        val fondo = when {
            oscuro && e.enfocado -> Color(0xFF263D2C)
            oscuro -> Color(0xFF243128)
            e.enfocado -> mezcla(CRISTAL_LIQUIDO, Color.White, 0.3f)
            else -> CRISTAL_LIQUIDO
        }
        drawRoundRect(apagar(fondo, e.habilitado), vent.topLeft, vent.size, CornerRadius(3 * u))
        // La rejilla tenue de los segmentos, los renglones (si hay varios) y el reflejo del cristal.
        var gx = vent.left + 4 * u; var n = 0
        while (gx < vent.right && n < 400) { drawLine(Color.Black.copy(alpha = 0.03f), Offset(gx, vent.top), Offset(gx, vent.bottom), 0.6f * u); gx += maxOf(4 * u, 1f); n++ }
        if (e.multilinea) renglones(vent.left + 4 * u, vent.right - 4 * u, margenCampoY.toPx(), vent.bottom - 4 * u, tintaCampo(oscuro).copy(alpha = 0.08f), 0.8f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(vent.left + 4 * u, vent.top + 1.6f * u), Offset(vent.right - 4 * u, vent.top + 1.6f * u), 1 * u)
        // El borde que se enciende: verde de piloto con foco, del color del error si el valor no sirve.
        when {
            e.error -> drawRoundRect(errorCampo(oscuro), vent.topLeft, vent.size, CornerRadius(3 * u), style = Stroke(1.6f * u))
            e.enfocado -> drawRoundRect(if (oscuro) VERDE_PILOTO.copy(alpha = 0.8f) else PERSIANA, vent.topLeft, vent.size, CornerRadius(3 * u), style = Stroke(1.6f * u))
        }
        // El piloto de la orilla de arriba.
        val piloto = Offset(vent.right - 10 * u, chapa.top + 2.6f * u)
        drawCircle(Color.Black.copy(alpha = 0.45f), 1.9f * u, piloto)
        val luz = when {
            !e.habilitado -> Color(0xFF5A5A58)
            e.error -> Color(0xFFE0402E)
            e.enfocado -> VERDE_PILOTO
            else -> Color(0xFF6E2A22)
        }
        drawCircle(luz, 1.3f * u, piloto)
        if (e.habilitado && (e.enfocado || e.error)) drawCircle(luz.copy(alpha = 0.3f), 3 * u, piloto)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La línea del autobús en el plano de la parada: la raya naranja de lado a lado, la parada de en medio (un círculo hueco
     * que la corta) y dos paradas menores de través.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val linea = (if (oscuro) mezcla(NARANJA, Color(0xFF100E0C), 0.15f) else NARANJA_OSC).copy(alpha = (if (oscuro) 0.5f else 0.6f) * f)
        val r = 2.8f * u
        drawLine(linea, Offset(4 * u, cy), Offset(cx - r - 0.6f * u, cy), 1.2f * u, StrokeCap.Round)
        drawLine(linea, Offset(cx + r + 0.6f * u, cy), Offset(w - 4 * u, cy), 1.2f * u, StrokeCap.Round)
        listOf(-1f, 1f).forEach { s ->
            val x = cx + s * minOf(22 * u, w * 0.2f)
            drawLine(linea, Offset(x, cy - 2.2f * u), Offset(x, cy + 2.2f * u), 1 * u)
        }
        drawCircle(NARANJA.copy(alpha = (if (oscuro) 0.8f else 0.95f) * f), r, Offset(cx, cy), style = Stroke(1.2f * u))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8955A) else Color(0xFF9C3D22)

    /**
     * La ranura de la obliteradora: una raya de hierro bajo el texto, con un tornillo de aluminio a cada lado. Al tocarlo,
     * la chapa naranja asoma detrás y la ranura se enciende del verde del piloto, como cuando el boleto queda marcado.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        fun c(x: Color, a: Float = 1f) = apagarBoton(t(x, oscuro).copy(alpha = a), e.habilitado)
        val toque = e.presionado && e.habilitado
        if (toque) drawRoundRect(t(NARANJA, oscuro).copy(alpha = if (oscuro) 0.2f else 0.16f), Offset(2 * u, cy - 16 * u), Size(w - 4 * u, 32 * u), CornerRadius(8 * u))
        listOf(4.5f * u, w - 4.5f * u).forEach { x ->
            drawCircle(Color.Black.copy(alpha = if (e.habilitado) 0.25f else 0.1f), 1.7f * u, Offset(x, cy + 0.4f * u))
            drawCircle(c(ALUMINIO), 1.5f * u, Offset(x, cy))
            drawLine(c(HIERRO), Offset(x - 0.9f * u, cy - 0.5f * u), Offset(x + 0.9f * u, cy + 0.5f * u), 0.5f * u)
        }
        val y = minOf(cy + 12 * u, h - 3 * u)
        val x0 = 11 * u; val x1 = w - 11 * u
        if (x1 > x0) {
            val ranura = if (toque) (if (oscuro) VERDE_PILOTO else PERSIANA) else c(if (oscuro) ALUMINIO else HIERRO, 0.45f)
            if (toque && oscuro) drawLine(VERDE_PILOTO.copy(alpha = 0.25f), Offset(x0, y), Offset(x1, y), 3.6f * u, StrokeCap.Round)
            drawLine(ranura, Offset(x0, y), Offset(x1, y), 1.4f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un tramo del plano de la línea: casi sin fondo, con la raya fina de la línea al pie y, a la izquierda, la parada (una
     * pastilla naranja). Al tocarla, la chapa naranja asoma tenue detrás y la parada se enciende del verde del piloto.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(t(NARANJA, oscuro).copy(alpha = if (oscuro) 0.16f else 0.13f), Offset.Zero, size)
        drawLine((if (oscuro) ALUMINIO else HIERRO).copy(alpha = if (oscuro) 0.14f else 0.12f), Offset(margenFila.toPx(), h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u)
        val alto = minOf(h * 0.36f, 18 * u)
        val arriba = h / 2 - alto / 2
        if (e.presionado) {
            val verde = if (oscuro) VERDE_PILOTO else PERSIANA
            if (oscuro) drawRoundRect(verde.copy(alpha = 0.25f), Offset(3 * u, arriba - 1.5f * u), Size(6 * u, alto + 3 * u), CornerRadius(3 * u))
            drawRoundRect(verde, Offset(4.5f * u, arriba), Size(3 * u, alto), CornerRadius(1.5f * u))
        } else {
            drawRoundRect(t(NARANJA, oscuro).copy(alpha = if (oscuro) 0.6f else 0.75f), Offset(4.5f * u, arriba), Size(3 * u, alto), CornerRadius(1.5f * u))
        }
    }

    /** La flecha del plano: la parada en la que se está, la raya y la punta hacia la que sigue. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val color = if (oscuro) Color(0xFFE8955A) else NARANJA_OSC
        drawLine(color.copy(alpha = 0.55f), Offset(6.5f * u, cy), Offset(15 * u, cy), 1.4f * u, StrokeCap.Round)
        drawCircle(color, 2.4f * u, Offset(4.2f * u, cy), style = Stroke(1.3f * u))
        drawPath(Path().apply { moveTo(11.5f * u, cy - 4 * u); lineTo(15.8f * u, cy); lineTo(11.5f * u, cy + 4 * u) }, color,
            style = Stroke(1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /**
     * El letrero de la parada en su poste: la placa naranja con el autobús en blanco si la fila está fija; si no, la placa
     * solo de contorno, tenue.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val cx = size.width / 2
        val placa = Rect(cx - 8 * u, 2 * u, cx + 8 * u, 13 * u)
        val tenue = (if (oscuro) ALUMINIO else HIERRO).copy(alpha = 0.5f)
        drawLine(if (fija) t(ALUMINIO, oscuro) else tenue, Offset(cx, placa.bottom), Offset(cx, 21.5f * u), 1.6f * u, StrokeCap.Round)
        drawLine(if (fija) t(HIERRO, oscuro).copy(alpha = 0.7f) else tenue, Offset(cx - 3 * u, 22 * u), Offset(cx + 3 * u, 22 * u), 1.2f * u, StrokeCap.Round)
        // El autobús: la caja, la fila de ventanillas y dos ruedas.
        fun DrawScope.autobus(color: Color) {
            drawRoundRect(color, Offset(cx - 4.6f * u, 4.6f * u), Size(9.2f * u, 4.6f * u), CornerRadius(1 * u), style = Stroke(1 * u))
            drawLine(color, Offset(cx - 3.4f * u, 6.4f * u), Offset(cx + 3.4f * u, 6.4f * u), 1 * u)
            drawCircle(color, 0.9f * u, Offset(cx - 2.6f * u, 9.8f * u)); drawCircle(color, 0.9f * u, Offset(cx + 2.6f * u, 9.8f * u))
        }
        if (fija) {
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(placa.left, placa.top + 0.8f * u), placa.size, CornerRadius(2 * u))
            drawRoundRect(if (oscuro) Color(0xFFE8955A) else NARANJA, placa.topLeft, placa.size, CornerRadius(2 * u))
            drawRoundRect(NARANJA_OSC, placa.topLeft, placa.size, CornerRadius(2 * u), style = Stroke(1 * u))
            autobus(Color.White.copy(alpha = 0.95f))
        } else {
            drawRoundRect(tenue, placa.topLeft, placa.size, CornerRadius(2 * u), style = Stroke(1.2f * u))
            autobus(tenue)
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFC2B4A0) else Color(0xFF5E5146)

    /**
     * El tablero de horarios de la parada, todavía sin horarios: el recuadro de raya punteada, apenas teñido de la chapa, y
     * arriba la franja naranja con sus dos tornillos.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(if (oscuro) ALUMINIO.copy(alpha = 0.05f) else NARANJA.copy(alpha = 0.07f), r.topLeft, r.size, CornerRadius(8 * u))
        drawRoundRect((if (oscuro) ALUMINIO else HIERRO).copy(alpha = if (oscuro) 0.3f else 0.28f), r.topLeft, r.size, CornerRadius(8 * u),
            style = Stroke(1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * u, 4 * u))))
        // Los tornillos de las esquinas y, arriba al centro, la plaquita naranja del encabezado, vacía.
        listOf(r.topLeft + Offset(6 * u, 6 * u), r.topRight + Offset(-6 * u, 6 * u), r.bottomLeft + Offset(6 * u, -6 * u), r.bottomRight + Offset(-6 * u, -6 * u)).forEach { p ->
            drawCircle(Color.Black.copy(alpha = 0.25f), 1.7f * u, p + Offset(0f, 0.4f * u))
            drawCircle(t(ALUMINIO, oscuro), 1.5f * u, p)
            drawLine(t(HIERRO, oscuro), Offset(p.x - 0.8f * u, p.y - 0.5f * u), Offset(p.x + 0.8f * u, p.y + 0.5f * u), 0.5f * u)
        }
        val ancho = minOf(56 * u, r.width * 0.3f)
        drawRoundRect(t(NARANJA, oscuro).copy(alpha = 0.75f), Offset(r.center.x - ancho / 2, r.top - 1.5f * u), Size(ancho, 4.5f * u), CornerRadius(1.5f * u))
    }

    /** La tazzina del café ya bebido, en su platito: sin vapor, con el cerco seco adentro y la cucharita a un lado. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val linea = if (oscuro) Color(0xFFB5A794) else Color(0xFF6A5D51)
        val acento = if (oscuro) Color(0xFFE8955A) else NARANJA_OSC
        val tr = Stroke(1.5f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // El platito, con su sombra.
        drawOval(linea.copy(alpha = 0.14f), Offset(f(0.06f), f(0.75f)), Size(f(0.88f), f(0.18f)))
        drawOval(if (oscuro) Color(0xFF2B2420) else Color.White.copy(alpha = 0.7f), Offset(f(0.06f), f(0.7f)), Size(f(0.88f), f(0.18f)))
        drawOval(linea, Offset(f(0.06f), f(0.7f)), Size(f(0.88f), f(0.18f)), style = tr)
        // La cucharita sobre el platito.
        drawLine(linea, Offset(f(0.66f), f(0.84f)), Offset(f(0.9f), f(0.76f)), 1.1f * u, StrokeCap.Round)
        drawOval(linea, Offset(f(0.6f), f(0.82f)), Size(f(0.1f), f(0.06f)))
        // La taza: el cuerpo, el asa y la boca, con el cerco del café adentro.
        val cuerpo = Path().apply {
            moveTo(f(0.24f), f(0.38f)); lineTo(f(0.29f), f(0.68f)); quadraticTo(f(0.5f), f(0.8f), f(0.71f), f(0.68f)); lineTo(f(0.76f), f(0.38f)); close()
        }
        drawPath(cuerpo, if (oscuro) Color(0xFF3A322C) else Color(0xFFFBF7EF))
        drawArc(linea, -80f, 190f, false, Offset(f(0.7f), f(0.44f)), Size(f(0.16f), f(0.16f)), style = tr)
        drawPath(cuerpo, linea, style = tr)
        drawLine(acento.copy(alpha = 0.7f), Offset(f(0.27f), f(0.54f)), Offset(f(0.73f), f(0.54f)), 1.2f * u)
        drawOval(if (oscuro) Color(0xFF1E1916) else Color(0xFFEDE3D0), Offset(f(0.24f), f(0.33f)), Size(f(0.52f), f(0.1f)))
        drawOval(Color(0xFF7A5232).copy(alpha = 0.45f), Offset(f(0.3f), f(0.35f)), Size(f(0.4f), f(0.06f)), style = Stroke(0.8f * u))
        drawOval(acento, Offset(f(0.24f), f(0.33f)), Size(f(0.52f), f(0.1f)), style = tr)
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E6D2) else Color(0xFF2B2420)

    /**
     * El boleto del autobús: el papel claro con su banda magnética al pie y, a la izquierda, el talón naranja, separado por
     * la línea de perforación, con la marca de la obliteradora (un «!» o una «i», según el aviso).
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val papel = if (oscuro) Color(0xFF2A2420) else Color(0xFFFBF5E8)
        val r = Rect(0f, 0f, w, h - 1 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(0f, 1 * u), r.size, CornerRadius(3 * u))
        drawRoundRect(papel, r.topLeft, r.size, CornerRadius(3 * u))
        // El talón y su perforación.
        val talon = 20 * u
        drawRoundRect(t(NARANJA, oscuro), r.topLeft, Size(talon + 3 * u, r.height), CornerRadius(3 * u))
        drawRect(papel, Offset(talon, 0f), Size(3 * u, r.height))
        var y = 2.5f * u; var n = 0
        while (y < r.bottom - 1.5f * u && n < 300) { drawCircle(papel, 0.9f * u, Offset(talon, y)); y += maxOf(4 * u, 1f); n++ }
        // La marca de la obliteradora.
        val cx = talon / 2; val cy = minOf(r.height / 2, 18 * u)
        val blanco = Color.White.copy(alpha = if (oscuro) 0.85f else 0.95f)
        if (e.semilla % 2 == 0) {
            drawRoundRect(blanco, Offset(cx - 1.2f * u, cy - 6 * u), Size(2.4f * u, 8 * u), CornerRadius(1.2f * u))
            drawCircle(blanco, 1.4f * u, Offset(cx, cy + 4.6f * u))
        } else {
            drawCircle(blanco, 1.4f * u, Offset(cx, cy - 4.6f * u))
            drawRoundRect(blanco, Offset(cx - 1.2f * u, cy - 2 * u), Size(2.4f * u, 8 * u), CornerRadius(1.2f * u))
        }
        // La banda magnética al pie del boleto.
        if (w > talon + 12 * u) drawRect((if (oscuro) PERSIANA_LUZ else PERSIANA).copy(alpha = 0.35f), Offset(talon + 6 * u, r.bottom - 3 * u), Size(w - talon - 12 * u, 1.4f * u))
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = Color(0xFFFFB347)
    override fun rotuloCifra(oscuro: Boolean): Color = Color(0xFFBFC4C2)

    /**
     * El letrero del número de la línea, al frente del autobús: la chapa naranja con dos tornillos y, dentro, el tablero
     * negro y liso donde el número se enciende en ámbar (en los dos modos).
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val chapa = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(chapa.left, chapa.top + 1.2f * u), chapa.size, CornerRadius(7 * u))
        drawRoundRect(t(NARANJA, oscuro), chapa.topLeft, chapa.size, CornerRadius(7 * u))
        drawRoundRect(NARANJA_OSC, chapa.topLeft, chapa.size, CornerRadius(7 * u), style = Stroke(1 * u))
        val tablero = chapa.deflate(3.5f * u)
        drawRoundRect(Color(0xFF181C1A), tablero.topLeft, tablero.size, CornerRadius(4 * u))
        drawRoundRect(Color.Black, tablero.topLeft, tablero.size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        drawLine(Color.White.copy(alpha = 0.07f), Offset(tablero.left + 4 * u, tablero.top + 1.5f * u), Offset(tablero.right - 4 * u, tablero.top + 1.5f * u), 1 * u)
        // Los tornillos de la chapa, a media altura.
        listOf(chapa.left + 1.75f * u, chapa.right - 1.75f * u).forEach { x ->
            drawCircle(t(ALUMINIO, oscuro), 1.2f * u, Offset(x, chapa.center.y))
        }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta, donde va el texto: el esmalte, el papel de la locandina, la noche, la postal. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF23272A) else Color(0xFFFBF8F1)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF2A2622) else Color(0xFFF6EFDF)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF141A25) else Color(0xFFE2E4EC)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF2B2420) else Color(0xFFFFF8EA)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E6D2) else Color(0xFF1E1A17)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFBCB09F) else Color(0xFF5A4F45)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) PERSIANA_LUZ else PERSIANA
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFFFFB347) else Color(0xFF2E4A7A)
        else -> if (oscuro) Color(0xFFE8955A) else Color(0xFF9C3D22)
    }

    /**
     * Las cuatro son cosas de la parada del autobús. La cifra del tablero (METRICA) es la placa de horarios: el marco de
     * aluminio, el esmalte liso y, arriba, la franja verde de la línea con un tornillo en cada punta y su piloto; si la cifra
     * se aparta de tu normal, la franja se vuelve naranja y el piloto se enciende. Lo que te falta hoy (PENDIENTE) es la
     * locandina del quiosco, colgada del alambre con dos pinzas, con la banda naranja del titular a la izquierda y la doble
     * raya del periódico al pie; completo, lleva el timbre verde de la obliteradora. La noche (NOCHE) es el cielo sobre la
     * parada: la luna en creciente, unas estrellas en la orilla y, al pie, el autobús nocturno con sus ventanillas encendidas;
     * sellado el día, su letrero de destino se pone en verde. Las de Vida (VIDA) son postales de la ciudad, con su sello
     * dentado y el matasellos. Al tocarlas, se hunden un poco y toman el tono de la chapa.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        fun tornillo(p: Offset) {
            drawCircle(Color.Black.copy(alpha = 0.25f), 1.5f * u, p + Offset(0f, 0.4f * u))
            drawCircle(t(ALUMINIO, oscuro), 1.3f * u, p)
            drawLine(t(HIERRO, oscuro), Offset(p.x - 0.8f * u, p.y - 0.5f * u), Offset(p.x + 0.8f * u, p.y + 0.5f * u), 0.5f * u)
        }
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(6 * u))
                drawRoundRect(t(ALUMINIO, oscuro), r.topLeft, r.size, CornerRadius(6 * u))
                drawRoundRect(t(HIERRO, oscuro).copy(alpha = 0.45f), r.topLeft, r.size, CornerRadius(6 * u), style = Stroke(0.8f * u))
                val esmalte = r.deflate(2.5f * u)
                val forma = Path().apply { addRoundRect(RoundRect(esmalte, CornerRadius(4 * u))) }
                drawPath(forma, fondo)
                if (e.presionado) drawPath(forma, t(NARANJA, oscuro).copy(alpha = 0.12f))
                // La franja de la línea, recortada a la forma del esmalte, con sus tornillos y el piloto.
                val franja = minOf(m * 0.45f, 6 * u)
                clipPath(forma) {
                    drawRect(if (e.destacado) t(NARANJA, oscuro) else t(PERSIANA, oscuro), esmalte.topLeft, Size(esmalte.width, franja))
                    drawRect(Color.Black.copy(alpha = 0.18f), Offset(esmalte.left, esmalte.top + franja), Size(esmalte.width, 0.8f * u))
                }
                tornillo(Offset(esmalte.left + 3.5f * u, esmalte.top + franja / 2))
                tornillo(Offset(esmalte.right - 3.5f * u, esmalte.top + franja / 2))
                tornillo(Offset(esmalte.left + 3.5f * u, esmalte.bottom - 3.5f * u))
                tornillo(Offset(esmalte.right - 3.5f * u, esmalte.bottom - 3.5f * u))
                val piloto = Offset(esmalte.right - 10 * u, esmalte.top + franja / 2)
                drawCircle(Color.Black.copy(alpha = 0.45f), 1.6f * u, piloto)
                drawCircle(if (e.destacado) Color(0xFFFFF1B0) else Color(0xFF5E2A20), 1.1f * u, piloto)
                if (e.destacado) drawCircle(Color(0xFFFFE08A).copy(alpha = 0.35f), 2.8f * u, piloto)
            }
            TipoTarjeta.PENDIENTE -> {
                // El alambre del quiosco, de lado a lado, y la locandina colgada de él.
                val papel = Rect(2 * u, 4 * u + baja, w - 2 * u, h - 1.5f * u)
                drawLine((if (oscuro) ALUMINIO else HIERRO).copy(alpha = 0.6f), Offset(0f, 2.2f * u), Offset(w, 2.2f * u), 0.9f * u)
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(papel.left + 1 * u, papel.top + 1.5f * u), papel.size)
                drawRect(fondo, papel.topLeft, papel.size)
                if (e.presionado) drawRect(t(NARANJA, oscuro).copy(alpha = 0.08f), papel.topLeft, papel.size)
                // La banda naranja del titular, a la izquierda, con su canto.
                drawRect(t(NARANJA, oscuro), papel.topLeft, Size(4.5f * u, papel.height))
                drawRect(t(NARANJA_OSC, oscuro), Offset(papel.left + 4.5f * u, papel.top), Size(0.8f * u, papel.height))
                // La doble raya del periódico, al pie.
                val raya = if (oscuro) Color(0xFFBCB09F) else Color(0xFF1E1A17)
                if (papel.right - 4 * u > papel.left + 9 * u) {
                    drawLine(raya.copy(alpha = 0.7f), Offset(papel.left + 9 * u, papel.bottom - 4.4f * u), Offset(papel.right - 4 * u, papel.bottom - 4.4f * u), 1.2f * u)
                    drawLine(raya.copy(alpha = 0.5f), Offset(papel.left + 9 * u, papel.bottom - 2.4f * u), Offset(papel.right - 4 * u, papel.bottom - 2.4f * u), 0.5f * u)
                }
                // Las dos pinzas de aluminio que la sujetan del alambre.
                listOf(0.22f, 0.78f).forEach { f ->
                    val x = papel.left + papel.width * f
                    drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(x - 1.4f * u, 0.8f * u + baja), Size(4.2f * u, 10 * u), CornerRadius(1 * u))
                    drawRoundRect(t(ALUMINIO, oscuro), Offset(x - 2.1f * u, 0.2f * u + baja), Size(4.2f * u, 10 * u), CornerRadius(1 * u))
                    drawLine(t(HIERRO, oscuro), Offset(x, 1.2f * u + baja), Offset(x, 9.2f * u + baja), 0.7f * u)
                    drawCircle(t(HIERRO, oscuro), 0.7f * u, Offset(x, 3 * u + baja))
                }
                // Completo: el timbre verde de la obliteradora, en la esquina de arriba.
                if (e.destacado) {
                    val verde = if (oscuro) VERDE_PILOTO else PERSIANA
                    val timbre = Rect(papel.right - 22 * u, papel.top + 1.4f * u, papel.right - 3 * u, papel.top + 8.6f * u)
                    drawRoundRect(verde.copy(alpha = 0.85f), timbre.topLeft, timbre.size, CornerRadius(1.5f * u), style = Stroke(0.9f * u))
                    drawPath(Path().apply {
                        moveTo(timbre.left + 3 * u, timbre.center.y); lineTo(timbre.left + 5 * u, timbre.bottom - 1.4f * u); lineTo(timbre.left + 8.5f * u, timbre.top + 1.3f * u)
                    }, verde, style = Stroke(1 * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                    drawLine(verde.copy(alpha = 0.7f), Offset(timbre.left + 10 * u, timbre.center.y), Offset(timbre.right - 2 * u, timbre.center.y), 0.9f * u, StrokeCap.Round)
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.top + 1 * u), r.size, CornerRadius(6 * u))
                drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(6 * u))
                drawRoundRect((if (oscuro) ALUMINIO else HIERRO).copy(alpha = 0.3f), r.topLeft, r.size, CornerRadius(6 * u), style = Stroke(1 * u))
                // Las estrellas en la orilla (no en la de abajo, que es del autobús) y la luna en la esquina.
                val estrella = if (oscuro) Color(0xFFF1E6D2) else Color(0xFF7A86A8)
                val n = ((w + h) / (10 * u)).toInt().coerceIn(8, 80)
                puntosMargen(w, h, m, n, e.semilla + 11, 3 * u).forEach { (p, b) ->
                    if (p.y < m && p.x < w - m * 1.6f) drawCircle(estrella.copy(alpha = 0.35f + 0.55f * b), (0.5f + 0.7f * b) * u, p)
                }
                lunaCreciente(Offset(r.right - m * 0.62f, r.top + m * 0.58f), m * 0.36f, if (oscuro) Color(0xFFFFE6A8) else Color(0xFF9AA4C2), fondo)
                // El autobús nocturno al pie: la caja, las ventanillas encendidas, las ruedas y el letrero de destino.
                val largo = minOf(r.width * 0.22f, 48 * u)
                val caja = Rect(r.left + 8 * u, r.bottom - 11 * u, r.left + 8 * u + largo, r.bottom - 3 * u)
                drawRoundRect(t(NARANJA, oscuro).copy(alpha = if (oscuro) 0.8f else 0.9f), caja.topLeft, caja.size, CornerRadius(1.6f * u))
                val ventana = if (oscuro) Color(0xFFFFD27A) else Color(0xFFFFF4D2)
                var x = caja.left + 2 * u; var k = 0
                while (x < caja.right - 9 * u && k < 60) {
                    drawRect(ventana.copy(alpha = if (azar(e.semilla, k) > 0.25f) 0.95f else 0.35f), Offset(x, caja.top + 1.5f * u), Size(3 * u, 3.2f * u))
                    x += maxOf(4.4f * u, 1f); k++
                }
                val letrero = Rect(caja.right - 7.5f * u, caja.top + 1.2f * u, caja.right - 1.2f * u, caja.top + 4.6f * u)
                drawRect(Color(0xFF181C1A), letrero.topLeft, letrero.size)
                drawRect(if (e.destacado) VERDE_PILOTO else Color(0xFFFFB347), letrero.deflate(0.6f * u).topLeft, letrero.deflate(0.6f * u).size)
                if (e.destacado) drawRect(VERDE_PILOTO.copy(alpha = 0.3f), letrero.inflate(1.2f * u).topLeft, letrero.inflate(1.2f * u).size)
                listOf(caja.left + 5 * u, caja.right - 6 * u).forEach { cx ->
                    drawCircle(t(HIERRO, oscuro), 1.7f * u, Offset(cx, caja.bottom))
                    drawCircle(t(ALUMINIO, oscuro), 0.7f * u, Offset(cx, caja.bottom))
                }
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(r.left + 0.8f * u, r.top + 1.4f * u), r.size)
                drawRect(fondo, r.topLeft, r.size)
                if (e.presionado) drawRect(t(NARANJA, oscuro).copy(alpha = 0.12f), r.topLeft, r.size)
                // El filete doble de la postal.
                val filete = if (oscuro) Color(0xFFE8955A) else NARANJA_OSC
                drawRect(filete.copy(alpha = 0.55f), r.deflate(2.4f * u).topLeft, r.deflate(2.4f * u).size, style = Stroke(0.8f * u))
                drawRect(filete.copy(alpha = 0.3f), r.deflate(4 * u).topLeft, r.deflate(4 * u).size, style = Stroke(0.5f * u))
                // El sello, en la esquina de arriba: naranja, dentado, con el disco del autobús en blanco.
                val sello = Rect(r.right - 13 * u, r.top + 2 * u, r.right - 3 * u, r.top + 11.5f * u)
                drawRect(t(NARANJA, oscuro), sello.topLeft, sello.size)
                var y = sello.top + 0.8f * u; var k = 0
                while (y < sello.bottom && k < 40) { drawCircle(fondo, 0.55f * u, Offset(sello.left, y)); drawCircle(fondo, 0.55f * u, Offset(sello.right, y)); y += maxOf(1.7f * u, 1f); k++ }
                var x = sello.left + 0.8f * u; k = 0
                while (x < sello.right && k < 40) { drawCircle(fondo, 0.55f * u, Offset(x, sello.top)); drawCircle(fondo, 0.55f * u, Offset(x, sello.bottom)); x += maxOf(1.7f * u, 1f); k++ }
                drawRect(Color.White.copy(alpha = 0.75f), sello.deflate(1.4f * u).topLeft, sello.deflate(1.4f * u).size, style = Stroke(0.5f * u))
                drawCircle(Color.White.copy(alpha = 0.9f), 1.5f * u, sello.center)
                // El matasellos: el círculo que muerde el sello y las tres ondas hacia la izquierda.
                val tinta = (if (e.destacado) (if (oscuro) VERDE_PILOTO else PERSIANA) else if (oscuro) ALUMINIO else HIERRO).copy(alpha = 0.65f)
                drawCircle(tinta, 4.4f * u, Offset(sello.left, sello.center.y), style = Stroke(0.7f * u))
                val x0 = maxOf(sello.left - 30 * u, r.left + 8 * u)
                listOf(-2.4f, 0f, 2.4f).forEach { dy ->
                    val yy = sello.center.y + dy * u
                    val onda = Path().apply {
                        moveTo(x0, yy); var xx = x0; var j = 0
                        while (xx < sello.left - 4.4f * u && j < 40) { quadraticTo(xx + 1.5f * u, yy - 1 * u * (if (j % 2 == 0) 1 else -1), xx + 3 * u, yy); xx += maxOf(3 * u, 1f); j++ }
                    }
                    drawPath(onda, tinta, style = Stroke(0.7f * u))
                }
            }
        }
    }
}

/**
 * Terminal (solo oscuro): el campo es la pantalla de fósforo del monitor de tubo, como su buscador: la carcasa de plástico
 * beige con el piloto verde abajo, el vidrio negro con sus líneas de barrido muy tenues y, a la izquierda, el indicador «›»
 * del prompt. Con foco, el borde del vidrio resplandece de cian y el indicador se aviva; con un error, todo eso se vuelve
 * del rojo anaranjado del aviso.
 */
object ComunCiber : DisenoComun {
    private val CARCASA = Color(0xFFC4BCA6)
    private val CARCASA_SOMBRA = Color(0xFF948C76)
    private val PANTALLA = Color(0xFF06110F)
    private val FOSFORO = Color(0xFF4FE3D6)
    private val FOSFORO_TENUE = Color(0xFF237A72)
    private val FOSFORO_CLARO = Color(0xFFBDEFF5)
    private val PILOTO = Color(0xFF5CF06A)
    private val AVISO = Color(0xFFFF7A59)

    /** El color de siempre: el tema no tiene claro, así que todo va hundido como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = mezcla(c, Color(0xFF050607), 0.3f)

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 20.dp
    override val margenCampoY: Dp = 9.dp
    override fun tintaCampo(oscuro: Boolean): Color = FOSFORO_CLARO
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF3F9C93)
    override fun rotuloCampo(oscuro: Boolean): Color = FOSFORO
    override fun errorCampo(oscuro: Boolean): Color = AVISO

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, true), e.habilitado)
        // La carcasa de plástico, con su canto de sombra abajo.
        val caja = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        drawRoundRect(c(CARCASA_SOMBRA), caja.topLeft, caja.size, CornerRadius(7 * u))
        drawRoundRect(c(CARCASA), caja.topLeft, Size(caja.width, caja.height - 2 * u), CornerRadius(7 * u))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(caja.left + 7 * u, caja.top + 1 * u), Offset(caja.right - 7 * u, caja.top + 1 * u), 0.8f * u)
        // El vidrio negro de la pantalla.
        val vidrio = Rect(caja.left + 4 * u, caja.top + 3.5f * u, caja.right - 4 * u, caja.bottom - 5 * u)
        drawRoundRect(Color.Black, vidrio.inflate(1 * u).topLeft, vidrio.inflate(1 * u).size, CornerRadius(5 * u))
        drawRoundRect(PANTALLA, vidrio.topLeft, vidrio.size, CornerRadius(4 * u))
        val aviso = when {
            e.error -> AVISO
            e.enfocado && e.habilitado -> FOSFORO
            else -> null
        }
        if (aviso != null) drawRoundRect(aviso.copy(alpha = 0.06f), vidrio.topLeft, vidrio.size, CornerRadius(4 * u))
        // Las líneas de barrido, muy tenues para que se lea, y los renglones de fósforo si hay varios.
        var y = vidrio.top + 1 * u; var n = 0
        while (y < vidrio.bottom && n < 600) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(vidrio.left, y), Offset(vidrio.right, y), 0.5f * u); y += maxOf(2.4f * u, 1f); n++ }
        if (e.multilinea) renglones(vidrio.left + 12 * u, vidrio.right - 6 * u, margenCampoY.toPx(), vidrio.bottom - 4 * u, FOSFORO.copy(alpha = 0.07f), 0.7f * u)
        // El indicador del prompt: a media altura o, si hay varios renglones, en el primero.
        val ind = when {
            !e.habilitado -> Color(0xFF4A5654)
            aviso != null -> aviso
            else -> FOSFORO_TENUE
        }
        val cy = if (e.multilinea) vidrio.top + 14 * u else vidrio.center.y
        val cx = vidrio.left + 5 * u
        drawPath(Path().apply { moveTo(cx, cy - 4 * u); lineTo(cx + 4 * u, cy); lineTo(cx, cy + 4 * u) }, ind, style = Stroke(1.6f * u, cap = StrokeCap.Square))
        // El borde que resplandece.
        if (aviso != null) {
            drawRoundRect(aviso.copy(alpha = 0.85f), vidrio.topLeft, vidrio.size, CornerRadius(4 * u), style = Stroke(1 * u))
            drawRoundRect(aviso.copy(alpha = 0.18f), vidrio.inflate(1.5f * u).topLeft, vidrio.inflate(1.5f * u).size, CornerRadius(5 * u), style = Stroke(2 * u))
        }
        // El reflejo del vidrio curvo y el piloto de encendido de la carcasa.
        drawRoundRect(Color.White.copy(alpha = 0.035f), Offset(vidrio.left + 4 * u, vidrio.top + 1.5f * u), Size(vidrio.width - 8 * u, minOf(vidrio.height * 0.3f, 8 * u)), CornerRadius(3 * u))
        val piloto = Rect(caja.right - 14 * u, vidrio.bottom + 1.6f * u, caja.right - 9 * u, vidrio.bottom + 2.8f * u)
        drawRect(if (!e.habilitado) Color(0xFF3A3C3A) else if (e.error) AVISO else PILOTO, piloto.topLeft, piloto.size)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 8.dp

    /** La línea de guiones de la consola, `+------+`: guiones de fósforo tenue de lado a lado y una cruz en cada punta. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = fuerza(e)
        drawLine(FOSFORO_TENUE.copy(alpha = 0.85f * f), Offset(9 * u, cy), Offset(w - 9 * u, cy), 1 * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * u, 3 * u)))
        val cruz = FOSFORO.copy(alpha = 0.55f * f)
        listOf(4 * u, w - 4 * u).forEach { x ->
            drawLine(cruz, Offset(x - 2.4f * u, cy), Offset(x + 2.4f * u, cy), 1 * u)
            drawLine(cruz, Offset(x, cy - 2.4f * u), Offset(x, cy + 2.4f * u), 1 * u)
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = FOSFORO

    /**
     * Los corchetes de la consola, `[ cancelar ]`, en fósforo tenue a los lados del texto. Al tocarlo, el video se invierte
     * a medias (el fósforo rellena tenue el hueco), los corchetes se avivan y asoma el cursor `_` en la esquina.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        val alto = minOf(13 * u, h / 2 - 4 * u)
        val izq = 4 * u; val der = w - 4 * u
        val color = when {
            !e.habilitado -> Color(0xFF34403E)
            toque -> FOSFORO
            else -> FOSFORO_TENUE
        }
        if (toque) {
            drawRect(FOSFORO.copy(alpha = 0.14f), Offset(izq + 2 * u, cy - alto), Size(der - izq - 4 * u, 2 * alto))
            drawRect(FOSFORO, Offset(der - 11 * u, cy + alto - 3.6f * u), Size(4.5f * u, 1.6f * u))
        }
        val brazo = 3.5f * u
        listOf(izq to 1f, der to -1f).forEach { (x, s) ->
            drawPath(Path().apply { moveTo(x + s * brazo, cy - alto); lineTo(x, cy - alto); lineTo(x, cy + alto); lineTo(x + s * brazo, cy + alto) },
                color, style = Stroke(1.4f * u, cap = StrokeCap.Square))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 12.dp

    /**
     * Un renglón del listado de la consola: sin fondo, con la línea de guiones muy tenue al pie y, a la izquierda, un punto
     * de fósforo apagado. Al tocarla, el renglón se invierte a medias (el fósforo lo rellena tenue) y el punto se vuelve el
     * cursor de bloque.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        if (e.presionado) {
            drawRect(FOSFORO.copy(alpha = 0.12f), Offset.Zero, size)
            drawRect(FOSFORO, Offset(3 * u, cy - 7 * u), Size(5 * u, 14 * u))
        } else {
            drawRect(FOSFORO_TENUE.copy(alpha = 0.7f), Offset(4.5f * u, cy - 1 * u), Size(2 * u, 2 * u))
        }
        drawLine(FOSFORO_TENUE.copy(alpha = 0.35f), Offset(margenFila.toPx(), h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u)))
    }

    /** La flecha de la consola, `->`: un guion y su punta, en fósforo tenue. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val color = Color(0xFF3F9C93)
        drawLine(color, Offset(2.5f * u, cy), Offset(10 * u, cy), 1.6f * u, StrokeCap.Square)
        drawPath(Path().apply { moveTo(11 * u, cy - 4.5f * u); lineTo(16.5f * u, cy); lineTo(11 * u, cy + 4.5f * u) }, color, style = Stroke(1.6f * u, cap = StrokeCap.Square))
    }

    /** La casilla de marcar de la consola, `[*]`: con el asterisco encendido si la fila está fija, `[ ]` apagada si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2; val cx = w / 2
        val color = if (fija) FOSFORO else FOSFORO_TENUE
        val alto = 7 * u; val brazo = 2.5f * u
        if (fija) drawRect(FOSFORO.copy(alpha = 0.14f), Offset(3 * u, cy - alto), Size(w - 6 * u, 2 * alto))
        listOf(3 * u to 1f, w - 3 * u to -1f).forEach { (x, s) ->
            drawPath(Path().apply { moveTo(x + s * brazo, cy - alto); lineTo(x, cy - alto); lineTo(x, cy + alto); lineTo(x + s * brazo, cy + alto) },
                color, style = Stroke(1.4f * u, cap = StrokeCap.Square))
        }
        val r = 4.2f * u
        val trazo = if (fija) 1.6f * u else 1.1f * u
        for (k in 0 until 3) {
            val a = PI.toFloat() / 3 * k + PI.toFloat() / 2
            drawLine(color.copy(alpha = if (fija) 1f else 0.5f), Offset(cx - cos(a) * r, cy - sin(a) * r), Offset(cx + cos(a) * r, cy + sin(a) * r), trazo, StrokeCap.Square)
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = Color(0xFF6FA3AB)

    /**
     * Una ventana de texto de la consola, vacía: el recuadro de guiones con una cruz `+` en cada esquina y, arriba a la
     * izquierda, el hueco del título `-[ ]-`, sin nombre.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        drawRect(FOSFORO.copy(alpha = 0.03f), r.topLeft, r.size)
        val guion = FOSFORO_TENUE.copy(alpha = 0.6f)
        val efecto = PathEffect.dashPathEffect(floatArrayOf(5 * u, 3 * u))
        val hueco0 = r.left + 10 * u; val hueco1 = r.left + 30 * u
        drawLine(guion, Offset(r.left + 5 * u, r.top), Offset(hueco0, r.top), 1 * u)
        drawLine(guion, Offset(hueco1, r.top), Offset(r.right - 5 * u, r.top), 1 * u, pathEffect = efecto)
        drawLine(guion, Offset(r.left + 5 * u, r.bottom), Offset(r.right - 5 * u, r.bottom), 1 * u, pathEffect = efecto)
        drawLine(guion, Offset(r.left, r.top + 5 * u), Offset(r.left, r.bottom - 5 * u), 1 * u, pathEffect = efecto)
        drawLine(guion, Offset(r.right, r.top + 5 * u), Offset(r.right, r.bottom - 5 * u), 1 * u, pathEffect = efecto)
        // El hueco del título: dos corchetes vacíos.
        val tc = FOSFORO.copy(alpha = 0.5f)
        listOf(hueco0 + 2 * u to 1f, hueco1 - 2 * u to -1f).forEach { (x, s) ->
            drawPath(Path().apply { moveTo(x + s * 2 * u, r.top - 3 * u); lineTo(x, r.top - 3 * u); lineTo(x, r.top + 3 * u); lineTo(x + s * 2 * u, r.top + 3 * u) }, tc, style = Stroke(1 * u))
        }
        // Las cruces de las esquinas.
        val cruz = FOSFORO.copy(alpha = 0.55f)
        listOf(r.topLeft, r.topRight, r.bottomLeft, r.bottomRight).forEach { p ->
            drawLine(cruz, Offset(p.x - 2.6f * u, p.y), Offset(p.x + 2.6f * u, p.y), 1 * u)
            drawLine(cruz, Offset(p.x, p.y - 2.6f * u), Offset(p.x, p.y + 2.6f * u), 1 * u)
        }
    }

    /**
     * El monitor sin salida: la pantalla con el indicador `›` y el cursor de bloque, y debajo los renglones de lo que no
     * respondió, apenas punteados; abajo, el pie del monitor.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val pantalla = Rect(f(0.06f), f(0.1f), f(0.94f), f(0.74f))
        drawRoundRect(t(CARCASA, true), pantalla.inflate(2 * u).topLeft, pantalla.inflate(2 * u).size, CornerRadius(4 * u))
        drawRoundRect(PANTALLA, pantalla.topLeft, pantalla.size, CornerRadius(3 * u))
        var y = pantalla.top + 1 * u; var n = 0
        while (y < pantalla.bottom && n < 60) { drawLine(FOSFORO.copy(alpha = 0.05f), Offset(pantalla.left, y), Offset(pantalla.right, y), 0.5f * u); y += maxOf(2 * u, 1f); n++ }
        val cy = pantalla.top + f(0.18f)
        drawPath(Path().apply { moveTo(f(0.16f), cy - f(0.07f)); lineTo(f(0.24f), cy); lineTo(f(0.16f), cy + f(0.07f)) }, FOSFORO, style = Stroke(1.5f * u, cap = StrokeCap.Square))
        drawRect(FOSFORO.copy(alpha = 0.85f), Offset(f(0.3f), cy - f(0.07f)), Size(f(0.08f), f(0.14f)))
        listOf(0.44f, 0.56f).forEach { fy ->
            drawLine(FOSFORO_TENUE.copy(alpha = 0.6f), Offset(f(0.16f), f(fy)), Offset(f(0.8f), f(fy)), 1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * u, 2.5f * u)))
        }
        // El pie del monitor y su piloto.
        drawRect(t(CARCASA_SOMBRA, true), Offset(f(0.4f), f(0.78f)), Size(f(0.2f), f(0.08f)))
        drawRoundRect(t(CARCASA, true), Offset(f(0.24f), f(0.86f)), Size(f(0.52f), f(0.07f)), CornerRadius(1.5f * u))
        drawRect(PILOTO, Offset(f(0.82f), f(0.77f)), Size(f(0.06f), 1.2f * u))
    }

    override val margenAviso: Dp = 28.dp
    override fun tintaAviso(oscuro: Boolean): Color = FOSFORO_CLARO

    /**
     * Un mensaje del sistema: el renglón apenas teñido de fósforo y, a la izquierda, el `[!]` en el rojo anaranjado del aviso
     * y debajo su columna de guiones, que baja con el texto.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(FOSFORO.copy(alpha = 0.05f), Offset.Zero, size)
        drawLine(FOSFORO_TENUE.copy(alpha = 0.5f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u)))
        val cx = 13 * u; val cy = minOf(h / 2, 18 * u)
        val alto = 6.5f * u
        listOf(cx - 7 * u to 1f, cx + 7 * u to -1f).forEach { (x, s) ->
            drawPath(Path().apply { moveTo(x + s * 2.2f * u, cy - alto); lineTo(x, cy - alto); lineTo(x, cy + alto); lineTo(x + s * 2.2f * u, cy + alto) },
                AVISO, style = Stroke(1.3f * u, cap = StrokeCap.Square))
        }
        drawRect(AVISO, Offset(cx - 1 * u, cy - 4.5f * u), Size(2 * u, 6 * u))
        drawRect(AVISO, Offset(cx - 1 * u, cy + 2.6f * u), Size(2 * u, 2 * u))
        // La columna de guiones que sigue al texto hacia abajo.
        var y = cy + alto + 4 * u; var n = 0
        while (y < h - 4 * u && n < 100) { drawRect(AVISO.copy(alpha = 0.45f), Offset(cx - 0.8f * u, y), Size(1.6f * u, 3 * u)); y += maxOf(6 * u, 1f); n++ }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = FOSFORO
    override fun rotuloCifra(oscuro: Boolean): Color = Color(0xFF6FA3AB)

    /**
     * Una celda del volcado de memoria: el recuadro liso apenas más claro que la pantalla, de filete tenue, con una cruz `+`
     * de fósforo en cada esquina y, arriba a la derecha, los dos puntitos de su dirección.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2.5f * u, 2.5f * u, w - 2.5f * u, h - 2.5f * u)
        drawRect(Color(0xFF0B191C), r.topLeft, r.size)
        drawRect(FOSFORO_TENUE.copy(alpha = 0.8f), r.topLeft, r.size, style = Stroke(1 * u))
        val cruz = FOSFORO.copy(alpha = 0.6f)
        listOf(r.topLeft, r.topRight, r.bottomLeft, r.bottomRight).forEach { p ->
            drawLine(cruz, Offset(p.x - 2.2f * u, p.y), Offset(p.x + 2.2f * u, p.y), 1 * u)
            drawLine(cruz, Offset(p.x, p.y - 2.2f * u), Offset(p.x, p.y + 2.2f * u), 1 * u)
        }
        val k = (e.semilla and 3) + 1
        for (i in 0 until 2) drawRect(FOSFORO_TENUE.copy(alpha = if (i < k % 2 + 1) 0.9f else 0.4f), Offset(r.right - (9 - 3.5f * i) * u, r.top + 3 * u), Size(2 * u, 1.2f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp

    /** El fondo liso de cada tarjeta: la pantalla, apenas teñida (de ámbar la de lo que falta, por la alerta). */
    private fun fondoTarjeta(tipo: TipoTarjeta): Color = when (tipo) {
        TipoTarjeta.METRICA -> Color(0xFF08161A)
        TipoTarjeta.PENDIENTE -> Color(0xFF170D0A)
        TipoTarjeta.NOCHE -> Color(0xFF03080B)
        TipoTarjeta.VIDA -> Color(0xFF0A1517)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = FOSFORO_CLARO
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = Color(0xFF6FA3AB)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE -> AVISO
        TipoTarjeta.VIDA -> PILOTO
        else -> FOSFORO
    }

    /**
     * Las cuatro son ventanas de la consola, dibujadas con sus caracteres de caja. La cifra del tablero (METRICA) es un
     * medidor del panel de estado: la doble raya `╔═╗` con un cuadrito en cada cruce y, abajo a la derecha, tres barritas
     * de nivel; si la cifra se aparta de tu normal, la raya se enciende y resplandece y las tres barritas se llenan. Lo que te
     * falta hoy (PENDIENTE) es la alerta del sistema: la franja de rayas oblicuas del rojo anaranjado arriba, el recuadro del
     * mismo color y los corchetes gruesos abajo; completo, la franja se vuelve la barra verde del piloto con su `✓`. La
     * noche (NOCHE) es el protector de pantalla: estrellas de fósforo en la orilla, la luna de fósforo en la esquina y el
     * recuadro de guiones; sellado el día, el recuadro se vuelve continuo y verde y aparece el candado. Las de Vida (VIDA) son
     * carpetas del gestor de archivos, con su pestaña y el piloto de la unidad. Al tocarlas, el video se invierte a medias.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(2 * u, 2 * u, w - 2 * u, h - 2 * u)
                drawRect(fondo, r.topLeft, r.size)
                if (e.presionado) drawRect(FOSFORO.copy(alpha = 0.12f), r.topLeft, r.size)
                val linea = if (e.destacado || e.presionado) FOSFORO else FOSFORO_TENUE
                if (e.destacado) drawRect(FOSFORO.copy(alpha = 0.2f), r.topLeft, r.size, style = Stroke(3.5f * u))
                drawRect(linea, r.topLeft, r.size, style = Stroke(1 * u))
                val dentro = r.deflate(3 * u)
                drawRect(linea.copy(alpha = 0.6f), dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
                // Los cruces de la doble raya: un cuadrito de fósforo en cada esquina.
                listOf(r.topLeft, r.topRight, r.bottomLeft, r.bottomRight).forEach { p ->
                    val q = Offset(p.x + (if (p.x < w / 2) 1.5f else -1.5f) * u, p.y + (if (p.y < h / 2) 1.5f else -1.5f) * u)
                    drawRect(linea, Offset(q.x - 1.8f * u, q.y - 1.8f * u), Size(3.6f * u, 3.6f * u))
                }
                // Las tres barritas de nivel, abajo a la derecha.
                val k = if (e.destacado) 3 else 1 + (e.semilla % 2)
                for (i in 0 until 3) {
                    val alto = (2 + 1.2f * i) * u
                    drawRect(if (i < k) linea else FOSFORO_TENUE.copy(alpha = 0.35f), Offset(dentro.right - (12.5f - 3.6f * i) * u, dentro.bottom - 2 * u - alto), Size(2.4f * u, alto))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 1.5f * u)
                val color = if (e.destacado) PILOTO else AVISO
                drawRect(fondo, r.topLeft, r.size)
                if (e.presionado) drawRect(color.copy(alpha = 0.1f), r.topLeft, r.size)
                drawRect(color.copy(alpha = 0.18f), r.topLeft, r.size, style = Stroke(3 * u))
                drawRect(color.copy(alpha = 0.8f), r.topLeft, r.size, style = Stroke(1 * u))
                // La franja de arriba: rayas oblicuas de peligro o, ya completo, la barra llena con su «✓».
                val banda = Rect(r.left, r.top, r.right, r.top + minOf(m * 0.5f, 6.5f * u))
                if (e.destacado) {
                    drawRect(color.copy(alpha = 0.9f), banda.topLeft, banda.size)
                    val c = Offset(banda.right - 10 * u, banda.center.y)
                    drawPath(Path().apply { moveTo(c.x - 3 * u, c.y); lineTo(c.x - 1 * u, c.y + 2 * u); lineTo(c.x + 3 * u, c.y - 2 * u) }, fondo, style = Stroke(1.4f * u, cap = StrokeCap.Square))
                } else clipRect(banda.left, banda.top, banda.right, banda.bottom) {
                    val paso = maxOf(7 * u, 1f)
                    var x = banda.left - banda.height; var n = 0
                    while (x < banda.right && n < 600) {
                        drawPath(paralelogramo(x, banda.top, x + 3.5f * u + banda.height, banda.bottom, banda.height), color.copy(alpha = 0.85f))
                        x += paso; n++
                    }
                }
                drawLine(color, Offset(banda.left, banda.bottom), Offset(banda.right, banda.bottom), 1 * u)
                // Los corchetes gruesos de las esquinas de abajo.
                val brazo = minOf(10 * u, r.height * 0.3f)
                listOf(r.left + 3.5f * u to 1f, r.right - 3.5f * u to -1f).forEach { (x, s) ->
                    drawPath(Path().apply { moveTo(x, r.bottom - 3.5f * u - brazo); lineTo(x, r.bottom - 3.5f * u); lineTo(x + s * brazo, r.bottom - 3.5f * u) },
                        color, style = Stroke(2 * u, cap = StrokeCap.Square))
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
                drawRect(fondo, r.topLeft, r.size)
                val linea = if (e.destacado) PILOTO else FOSFORO_TENUE
                drawRect(linea.copy(alpha = 0.85f), r.topLeft, r.size,
                    style = Stroke(1 * u, pathEffect = if (e.destacado) null else PathEffect.dashPathEffect(floatArrayOf(5 * u, 3 * u))))
                // Las estrellas del protector de pantalla, en la orilla: puntos y, las más vivas, cruces.
                val n = ((w + h) / (9 * u)).toInt().coerceIn(8, 90)
                puntosMargen(w, h, m, n, e.semilla + 7, 3 * u).forEach { (p, b) ->
                    if (p.x > m * 1.3f || p.y > m * 1.3f) {
                        val c = (if (b > 0.7f) FOSFORO_CLARO else FOSFORO).copy(alpha = 0.3f + 0.6f * b)
                        if (b > 0.85f) {
                            drawLine(c, Offset(p.x - 1.6f * u, p.y), Offset(p.x + 1.6f * u, p.y), 0.7f * u)
                            drawLine(c, Offset(p.x, p.y - 1.6f * u), Offset(p.x, p.y + 1.6f * u), 0.7f * u)
                        } else drawRect(c, Offset(p.x - 0.6f * u, p.y - 0.6f * u), Size(1.2f * u, 1.2f * u))
                    }
                }
                lunaCreciente(Offset(w - m * 0.62f, m * 0.6f), m * 0.34f, FOSFORO, fondo)
                // Sellado: el candado cerrado, verde, en la esquina de arriba a la izquierda.
                if (e.destacado) {
                    val c = Offset(m * 0.56f, m * 0.62f)
                    drawArc(PILOTO, 180f, 180f, false, Offset(c.x - 2 * u, c.y - 2.6f * u), Size(4 * u, 4 * u), style = Stroke(1.2f * u))
                    drawRect(PILOTO, Offset(c.x - 3 * u, c.y - 0.6f * u), Size(6 * u, 4.6f * u))
                    drawRect(fondo, Offset(c.x - 0.5f * u, c.y + 0.6f * u), Size(1 * u, 2 * u))
                }
            }
            TipoTarjeta.VIDA -> {
                val tab = minOf(42 * u, w * 0.36f)
                val l = 1.5f * u; val d = w - 1.5f * u; val b = h - 1.5f * u; val arriba = 5.5f * u
                val forma = Path().apply {
                    moveTo(l, b); lineTo(l, 1.5f * u); lineTo(l + tab - 4 * u, 1.5f * u); lineTo(l + tab, arriba); lineTo(d, arriba); lineTo(d, b); close()
                }
                val linea = if (e.presionado) FOSFORO else FOSFORO_TENUE
                drawPath(forma, fondo)
                if (e.presionado) drawPath(forma, FOSFORO.copy(alpha = 0.12f))
                drawPath(forma, linea, style = Stroke(1 * u, join = StrokeJoin.Miter))
                drawLine(linea.copy(alpha = 0.45f), Offset(l, arriba), Offset(l + tab, arriba), 0.7f * u)
                // El nombre en la pestaña, como renglones de guiones, y el piloto de la unidad abajo a la derecha.
                var x = l + 4 * u; var k = 0
                while (x < l + tab - 9 * u && k < 20) { drawRect(linea.copy(alpha = 0.7f), Offset(x, 2.9f * u), Size(2.6f * u, 1.2f * u)); x += maxOf(4 * u, 1f); k++ }
                val piloto = if (e.presionado || e.destacado) PILOTO else PILOTO.copy(alpha = 0.45f)
                if (e.presionado) drawRect(PILOTO.copy(alpha = 0.25f), Offset(d - 12 * u, b - 6 * u), Size(8 * u, 3.4f * u))
                drawRect(piloto, Offset(d - 11 * u, b - 5.3f * u), Size(6 * u, 2 * u))
            }
        }
    }
}

/**
 * Vapor: el campo es la placa de hierro de la sala de máquinas, como el tubo de nivel de su buscador: la chapa remachada
 * arriba y abajo (y a los lados si es alta), el filete de latón y, dentro, la cara crema de un manómetro con su escala muy
 * fina al pie. Con foco, el latón se pule, la escala se marca y la cara toma la luz tibia de la lámpara de gas; con un
 * error, el filete y la escala se vuelven rojos. En modo oscuro, la cara se hunde en la penumbra y la letra es clara.
 */
object ComunVapor : DisenoComun {
    private val HIERRO = Color(0xFF2E3230)
    private val HIERRO_LUZ = Color(0xFF59605C)
    private val HIERRO_OSC = Color(0xFF181A19)
    private val LATON = Color(0xFFC9A04E)
    private val LATON_LUZ = Color(0xFFE6C77E)
    private val LATON_OSC = Color(0xFF8A6A2C)
    private val CARA = Color(0xFFEDE3C8)
    private val CARA_NOCHE = Color(0xFF2E2820)
    private val GAS = Color(0xFFFFC870)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0B09), 0.36f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 15.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E3BE) else Color(0xFF231B14)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9C8F74) else Color(0xFF7A6A52)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) LATON_LUZ else Color(0xFF6B4F1C)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6E) else Color(0xFF9A2A22)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        // La chapa de hierro.
        val chapa = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(chapa.left, chapa.top + 1.5f * u), chapa.size, CornerRadius(4 * u))
        drawRoundRect(c(HIERRO), chapa.topLeft, chapa.size, CornerRadius(4 * u))
        drawRoundRect(c(HIERRO_LUZ), chapa.topLeft, chapa.size, CornerRadius(4 * u), style = Stroke(1 * u))
        // Los remaches: en fila arriba y abajo y, si la placa es alta, también a los lados.
        fun remache(p: Offset) {
            drawCircle(c(HIERRO_OSC), 1.3f * u, p)
            drawCircle(c(HIERRO_LUZ), 0.55f * u, Offset(p.x - 0.4f * u, p.y - 0.4f * u))
        }
        val paso = maxOf(22 * u, 1f)
        val cuantos = ((chapa.width - 12 * u) / paso).toInt().coerceIn(1, 60)
        val sobra = (chapa.width - 12 * u - paso * (cuantos - 1)) / 2
        for (k in 0 until cuantos) {
            val x = chapa.left + 6 * u + sobra + k * paso
            remache(Offset(x, chapa.top + 2.8f * u)); remache(Offset(x, chapa.bottom - 2.8f * u))
        }
        if (h > 80 * u) {
            var y = chapa.top + 16 * u; var n = 0
            while (y < chapa.bottom - 12 * u && n < 40) { remache(Offset(chapa.left + 2.8f * u, y)); remache(Offset(chapa.right - 2.8f * u, y)); y += paso; n++ }
        }
        // El filete de latón, pulido con foco o rojo con un error.
        val filete = chapa.deflate(5.5f * u)
        val laton = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> c(LATON_LUZ)
            else -> c(LATON)
        }
        drawRoundRect(laton, filete.topLeft, filete.size, CornerRadius(2.5f * u))
        drawLine(c(LATON_OSC), Offset(filete.left + 2 * u, filete.bottom - 0.5f * u), Offset(filete.right - 2 * u, filete.bottom - 0.5f * u), 0.8f * u)
        // La cara del manómetro, lisa.
        val cara = filete.deflate(2 * u)
        drawRoundRect(apagar(if (oscuro) CARA_NOCHE else CARA, e.habilitado), cara.topLeft, cara.size, CornerRadius(1.5f * u))
        if (e.enfocado && e.habilitado) drawRoundRect(GAS.copy(alpha = if (oscuro) 0.1f else 0.12f), cara.topLeft, cara.size, CornerRadius(1.5f * u))
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(cara.left + 1 * u, cara.top + 0.6f * u), Offset(cara.right - 1 * u, cara.top + 0.6f * u), 1.2f * u)
        if (e.multilinea) renglones(cara.left + 6 * u, cara.right - 6 * u, margenCampoY.toPx(), cara.bottom - 6 * u, c(LATON_OSC).copy(alpha = 0.2f), 0.6f * u)
        // La escala fina al pie: marcas cada 8 dp, más largas cada cinco.
        val escala = when {
            e.error -> errorCampo(oscuro).copy(alpha = 0.7f)
            e.enfocado -> c(LATON_OSC).copy(alpha = if (oscuro) 0.9f else 0.7f)
            else -> c(LATON_OSC).copy(alpha = 0.3f)
        }
        var mx = cara.left + 8 * u; var n = 0
        while (mx < cara.right - 6 * u && n < 120) {
            drawLine(escala, Offset(mx, cara.bottom - 1 * u), Offset(mx, cara.bottom - (if (n % 5 == 0) 4.5f else 2.5f) * u), 0.6f * u)
            mx += maxOf(8 * u, 1f); n++
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El tubo de latón de la sala de máquinas: la raya pulida con su sombra debajo, una brida en cada punta y, al centro,
     * un engrane chico de ocho dientes que corta el tubo.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val laton = (if (oscuro) mezcla(LATON, Color(0xFF0D0B09), 0.15f) else LATON_OSC).copy(alpha = (if (oscuro) 0.6f else 0.8f) * f)
        val sombra = (if (oscuro) Color.Black else LATON_OSC).copy(alpha = 0.45f * f)
        val r = 4.2f * u
        listOf(6 * u to cx - r - 2 * u, cx + r + 2 * u to w - 6 * u).forEach { (a, b) ->
            drawLine(laton, Offset(a, cy - 0.3f * u), Offset(b, cy - 0.3f * u), 1.2f * u)
            drawLine(sombra, Offset(a, cy + 0.9f * u), Offset(b, cy + 0.9f * u), 0.6f * u)
        }
        listOf(5 * u, w - 5 * u).forEach { x -> drawLine(laton, Offset(x, cy - 2.4f * u), Offset(x, cy + 2.6f * u), 1.6f * u) }
        // El engrane: cuatro puntos por diente (dos afuera, dos adentro) y su eje.
        val engrane = Path()
        val puntos = 8 * 4
        for (i in 0 until puntos) {
            val ang = (i - 0.5f) / puntos * 2 * PI.toFloat()
            val rr = if (i % 4 < 2) r else r * 0.7f
            val p = Offset(cx + cos(ang) * rr, cy + sin(ang) * rr)
            if (i == 0) engrane.moveTo(p.x, p.y) else engrane.lineTo(p.x, p.y)
        }
        engrane.close()
        drawPath(engrane, laton)
        drawCircle(sombra.copy(alpha = 0.9f * f), 1.2f * u, Offset(cx, cy))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFDBA641) else Color(0xFF7A4E10)

    /**
     * Las escuadras de latón de una placa de la sala de máquinas: cuatro esquinas remachadas que enmarcan el texto sin
     * cerrarlo. Al tocarlo, la placa toma la luz tibia de la lámpara de gas y el latón se pule.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        val alto = minOf(14 * u, h / 2 - 3 * u)
        val r = Rect(3 * u, cy - alto, w - 3 * u, cy + alto)
        if (toque) drawRoundRect(GAS.copy(alpha = if (oscuro) 0.14f else 0.24f), r.topLeft, r.size, CornerRadius(2 * u))
        val laton = apagarBoton(t(if (toque) (if (oscuro) LATON_LUZ else LATON) else (if (oscuro) LATON else LATON_OSC), oscuro), e.habilitado)
        val brazo = minOf(6 * u, r.width / 4)
        listOf(
            Offset(r.left, r.top) to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f),
            Offset(r.left, r.bottom) to Offset(1f, -1f), Offset(r.right, r.bottom) to Offset(-1f, -1f),
        ).forEach { (p, s) ->
            drawPath(Path().apply { moveTo(p.x + s.x * brazo, p.y); lineTo(p.x, p.y); lineTo(p.x, p.y + s.y * brazo * 0.8f) }, laton,
                style = Stroke(if (toque) 1.6f * u else 1.3f * u))
            drawCircle(laton, 0.85f * u, Offset(p.x + s.x * 2.2f * u, p.y + s.y * 2.2f * u))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un tramo de la tubería de la sala de máquinas: el tubo de latón muy fino al pie, con una brida chica donde empieza, y a
     * la izquierda un remache. Al tocarla, la fila toma la luz tibia de la lámpara de gas y el remache se enciende.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        if (e.presionado) drawRect(GAS.copy(alpha = if (oscuro) 0.1f else 0.2f), Offset.Zero, size)
        val tubo = (if (oscuro) LATON else LATON_OSC).copy(alpha = if (oscuro) 0.26f else 0.32f)
        val x0 = margenFila.toPx()
        drawLine(tubo, Offset(x0, h - 1 * u), Offset(w, h - 1 * u), 1 * u)
        drawLine(tubo, Offset(x0, h - 3 * u), Offset(x0, h), 1.4f * u)
        // El remache de la izquierda.
        val p = Offset(6.5f * u, cy)
        if (e.presionado) drawCircle(GAS.copy(alpha = if (oscuro) 0.3f else 0.4f), 4.2f * u, p)
        drawCircle(t(if (e.presionado) LATON_LUZ else LATON_OSC, oscuro), 2 * u, p)
        drawCircle(t(LATON_LUZ, oscuro).copy(alpha = 0.7f), 0.8f * u, Offset(p.x - 0.6f * u, p.y - 0.6f * u))
    }

    /** La aguja del manómetro, apuntando hacia adelante desde su eje. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val color = if (oscuro) Color(0xFFDBA641) else Color(0xFF7A4E10)
        drawPath(Path().apply { moveTo(1.5f * u, cy); lineTo(5 * u, cy - 2.4f * u); lineTo(17.5f * u, cy); lineTo(5 * u, cy + 2.4f * u); close() }, color)
        drawCircle(color, 2.6f * u, Offset(5 * u, cy))
        drawCircle(if (oscuro) Color(0xFF1D1914) else CARA, 0.9f * u, Offset(5 * u, cy))
    }

    /** Un engrane de ocho dientes: de latón lleno, con su eje, si la fila está fija; solo el contorno, tenue, si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val rueda = engrane(c, 9 * u, 8)
        if (fija) {
            drawPath(engrane(c + Offset(0.5f * u, 0.9f * u), 9 * u, 8), Color.Black.copy(alpha = 0.25f))
            drawPath(rueda, LATON)
            drawPath(rueda, t(LATON_OSC, oscuro), style = Stroke(0.8f * u))
            drawCircle(t(LATON_OSC, oscuro), 3.6f * u, c, style = Stroke(1 * u))
            drawCircle(t(HIERRO, oscuro), 1.7f * u, c)
        } else {
            val tenue = (if (oscuro) LATON else LATON_OSC).copy(alpha = 0.55f)
            drawPath(rueda, tenue, style = Stroke(1.1f * u))
            drawCircle(tenue, 2.4f * u, c, style = Stroke(1 * u))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFB8A88A) else Color(0xFF5A4B3B)

    /**
     * La placa esmaltada de una máquina parada: la cara crema (en la penumbra, de noche), el filete de latón con su línea de
     * dentro y un remache en cada esquina.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(if (oscuro) CARA_NOCHE.copy(alpha = 0.6f) else CARA.copy(alpha = 0.55f), r.topLeft, r.size, CornerRadius(4 * u))
        val laton = (if (oscuro) LATON else LATON_OSC).copy(alpha = if (oscuro) 0.45f else 0.6f)
        drawRoundRect(laton, r.topLeft, r.size, CornerRadius(4 * u), style = Stroke(1.2f * u))
        drawRoundRect(laton.copy(alpha = laton.alpha * 0.5f), r.deflate(4 * u).topLeft, r.deflate(4 * u).size, CornerRadius(2 * u), style = Stroke(0.6f * u))
        listOf(r.topLeft, r.topRight, r.bottomLeft, r.bottomRight).forEach { p ->
            val q = Offset(p.x + (if (p.x < w / 2) 2.2f else -2.2f) * u, p.y + (if (p.y < h / 2) 2.2f else -2.2f) * u)
            drawCircle(t(LATON_OSC, oscuro), 1.3f * u, q)
            drawCircle(t(LATON_LUZ, oscuro).copy(alpha = 0.8f), 0.5f * u, Offset(q.x - 0.35f * u, q.y - 0.35f * u))
        }
    }

    /** La caldera fría: el manómetro con la aguja en cero sobre su tubo, y la llave cerrada; ni una voluta de vapor. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val laton = t(LATON, oscuro)
        val hierro = if (oscuro) Color(0xFF6A706C) else HIERRO
        // El tubo de abajo, con su llave.
        drawLine(hierro, Offset(f(0.5f), f(0.74f)), Offset(f(0.5f), f(0.9f)), f(0.08f))
        drawLine(hierro, Offset(f(0.12f), f(0.9f)), Offset(f(0.88f), f(0.9f)), f(0.08f), StrokeCap.Round)
        drawLine(laton, Offset(f(0.76f), f(0.9f)), Offset(f(0.76f), f(0.8f)), 1.4f * u)
        drawLine(laton, Offset(f(0.69f), f(0.8f)), Offset(f(0.83f), f(0.8f)), 1.8f * u, StrokeCap.Round)
        // El manómetro, en cero.
        val c = Offset(f(0.5f), f(0.4f))
        drawCircle(Color.Black.copy(alpha = 0.25f), f(0.36f), c + Offset(0f, 1 * u))
        manometro(c, f(0.36f), laton, t(CARA, oscuro), Color(0xFFB0302A), 0f)
        drawCircle(t(LATON_OSC, oscuro), f(0.36f), c, style = Stroke(0.8f * u))
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFEADCC0) else Color(0xFF231B14)

    /**
     * La placa de advertencia de la caldera: la cara crema con un filete de latón arriba y abajo y, a la izquierda, la rueda
     * roja de la válvula o, en otros avisos, un manómetro chico con la aguja en la zona roja.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(if (oscuro) CARA_NOCHE.copy(alpha = 0.75f) else mezcla(CARA, LATON, 0.2f).copy(alpha = 0.7f), Offset.Zero, size, CornerRadius(3 * u))
        val laton = (if (oscuro) LATON else LATON_OSC).copy(alpha = 0.6f)
        drawLine(laton, Offset(2 * u, 0.6f * u), Offset(w - 2 * u, 0.6f * u), 1.2f * u)
        drawLine(laton, Offset(2 * u, h - 0.6f * u), Offset(w - 2 * u, h - 0.6f * u), 1.2f * u)
        val c = Offset(15 * u, minOf(h / 2, 18 * u))
        val rojo = if (oscuro) Color(0xFFE27A55) else Color(0xFFA8322A)
        if (e.semilla % 2 == 0) {
            // La rueda de la válvula: el aro con sus cinco botones, cinco rayos y el cubo.
            val r = 8 * u
            drawCircle(Color.Black.copy(alpha = 0.2f), r, c + Offset(0f, 0.8f * u), style = Stroke(2.2f * u))
            drawCircle(rojo, r, c, style = Stroke(2.2f * u))
            for (k in 0 until 5) {
                val a = 2 * PI.toFloat() * k / 5 - PI.toFloat() / 2
                drawLine(rojo, c, Offset(c.x + cos(a) * r, c.y + sin(a) * r), 1.4f * u)
                val b = 2 * PI.toFloat() * (k + 0.5f) / 5 - PI.toFloat() / 2
                drawCircle(rojo, 1.6f * u, Offset(c.x + cos(b) * r, c.y + sin(b) * r))
            }
            drawCircle(t(LATON, oscuro), 2.2f * u, c)
        } else {
            manometro(c, 8.5f * u, t(LATON, oscuro), t(CARA, oscuro), rojo, 0.88f)
            drawArc(rojo.copy(alpha = 0.6f), 0f, 45f, false, Offset(c.x - 6 * u, c.y - 6 * u), Size(12 * u, 12 * u), style = Stroke(1.2f * u))
        }
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E3BE) else Color(0xFF231B14)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) LATON_LUZ else Color(0xFF6B4F1C)

    /**
     * El contador de la máquina: la placa de hierro con un remache en cada esquina, el filete de latón y la cara crema lisa
     * donde va el número, con la marca roja del índice arriba al centro.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val placa = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(placa.left, placa.top + 1.2f * u), placa.size, CornerRadius(3 * u))
        drawRoundRect(t(HIERRO, oscuro), placa.topLeft, placa.size, CornerRadius(3 * u))
        drawRoundRect(t(HIERRO_LUZ, oscuro), placa.topLeft, placa.size, CornerRadius(3 * u), style = Stroke(0.8f * u))
        val filete = placa.deflate(3.5f * u)
        drawRoundRect(t(LATON, oscuro), filete.topLeft, filete.size, CornerRadius(2 * u))
        val cara = filete.deflate(1.5f * u)
        drawRoundRect(if (oscuro) CARA_NOCHE else CARA, cara.topLeft, cara.size, CornerRadius(1.5f * u))
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(cara.left + 1 * u, cara.top + 0.6f * u), Offset(cara.right - 1 * u, cara.top + 0.6f * u), 1.2f * u)
        listOf(placa.topLeft + Offset(1.8f * u, 1.8f * u), placa.topRight + Offset(-1.8f * u, 1.8f * u),
            placa.bottomLeft + Offset(1.8f * u, -1.8f * u), placa.bottomRight + Offset(-1.8f * u, -1.8f * u)).forEach { p ->
            drawCircle(t(HIERRO_OSC, oscuro), 1 * u, p)
        }
        val cx = cara.center.x
        drawPath(Path().apply { moveTo(cx - 2 * u, cara.top); lineTo(cx + 2 * u, cara.top); lineTo(cx, cara.top + 2.6f * u); close() }, Color(0xFFB0302A))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: la cara crema, el papel de la orden, la penumbra de la farola, el hierro de la placa. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF2A251E) else Color(0xFFF3EBD6)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF2B261F) else Color(0xFFF6F0E0)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF1A1714) else Color(0xFFE4DFD3)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF202321) else HIERRO
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (oscuro || tipo == TipoTarjeta.VIDA) Color(0xFFF1E3BE) else Color(0xFF231B14)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.VIDA -> Color(0xFFC2B79E)
        oscuro -> Color(0xFFB8A88A)
        else -> Color(0xFF5F5040)
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) LATON_LUZ else Color(0xFF7E3A12)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFFE27A55) else Color(0xFFA8322A)
        TipoTarjeta.NOCHE -> if (oscuro) GAS else Color(0xFF6B4F1C)
        TipoTarjeta.VIDA -> LATON_LUZ
    }

    /** La cabeza hexagonal de un perno, de radio [r], con su brillo. */
    private fun DrawScope.perno(c: Offset, r: Float, cuerpo: Color, luz: Color) {
        val p = Path().apply {
            for (k in 0 until 6) {
                val a = PI.toFloat() / 3 * k + PI.toFloat() / 6
                if (k == 0) moveTo(c.x + cos(a) * r, c.y + sin(a) * r) else lineTo(c.x + cos(a) * r, c.y + sin(a) * r)
            }
            close()
        }
        drawPath(p, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * density))
        drawPath(p, cuerpo)
        drawCircle(luz, r * 0.38f, Offset(c.x - r * 0.2f, c.y - r * 0.2f))
    }

    /**
     * Las cuatro son de la sala de máquinas. La cifra del tablero (METRICA) es la caja de un instrumento: el marco de latón
     * con un perno de cabeza hexagonal en cada esquina, el tubito de hierro que entra por arriba y la cara crema lisa; arriba
     * a la derecha, la mirilla del piloto, apagada, que se enciende de luz de gas cuando la cifra se aparta de tu normal. Lo
     * que te falta hoy (PENDIENTE) es la orden del jefe de máquinas en su tablilla de hierro: la pinza de latón arriba al
     * centro y la raya roja de urgencia a la izquierda; completo, el sello de tinta roja con su engrane. La noche (NOCHE) es la
     * farola de gas: el marco de hierro forjado con su filete de latón y, colgada de la esquina, la farola encendida con su
     * resplandor; sellado el día, un sello de latón con su marca abajo a la izquierda. Las de Vida (VIDA) son placas de
     * fabricante de una locomotora: el campo de hierro, el canto de latón en relieve y dos pernos. Al tocarlas, se hunden.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val rojo = if (oscuro) Color(0xFFE27A55) else Color(0xFFA8322A)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1.5f * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(5 * u))
                drawRoundRect(t(LATON, oscuro), r.topLeft, r.size, CornerRadius(5 * u))
                drawRoundRect(t(LATON_OSC, oscuro), r.topLeft, r.size, CornerRadius(5 * u), style = Stroke(0.8f * u))
                drawLine(t(LATON_LUZ, oscuro).copy(alpha = 0.8f), Offset(r.left + 5 * u, r.top + 1 * u), Offset(r.right - 5 * u, r.top + 1 * u), 0.8f * u)
                // El tubito de hierro que entra por arriba al centro, con su collarín.
                drawRect(t(HIERRO, oscuro), Offset(w / 2 - 2.5f * u, 0f + baja), Size(5 * u, r.top + 2 * u - baja))
                drawRect(t(LATON_OSC, oscuro), Offset(w / 2 - 3.5f * u, r.top - 0.2f * u), Size(7 * u, 1.6f * u))
                val cara = r.deflate(3.2f * u)
                drawRoundRect(if (e.presionado) mezcla(fondo, Color.Black, 0.06f) else fondo, cara.topLeft, cara.size, CornerRadius(3 * u))
                drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.18f), cara.topLeft, cara.size, CornerRadius(3 * u), style = Stroke(0.8f * u))
                val l = 4.4f * u
                listOf(Offset(cara.left + l, cara.top + l), Offset(cara.right - l, cara.top + l), Offset(cara.left + l, cara.bottom - l), Offset(cara.right - l, cara.bottom - l)).forEach { p ->
                    perno(p, 2.2f * u, t(if (oscuro) HIERRO_LUZ else LATON_OSC, oscuro), t(LATON_LUZ, oscuro).copy(alpha = 0.7f))
                }
                // La mirilla del piloto: vidrio oscuro o, si la cifra se aparta, la luz del gas.
                val c = Offset(cara.right - 12 * u, cara.top + l)
                drawCircle(t(LATON_OSC, oscuro), 2.9f * u, c)
                if (e.destacado) {
                    drawCircle(GAS.copy(alpha = 0.35f), 4.4f * u, c)
                    drawCircle(GAS, 2.1f * u, c)
                    drawCircle(Color.White.copy(alpha = 0.8f), 0.8f * u, c)
                } else {
                    drawCircle(Color(0xFF3A2E22), 2.1f * u, c)
                    drawCircle(Color.White.copy(alpha = 0.25f), 0.7f * u, Offset(c.x - 0.7f * u, c.y - 0.7f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val tabla = Rect(0.5f * u, 2.5f * u, w - 0.5f * u, h - 1 * u)
                drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(tabla.left, tabla.top + 1.2f * u), tabla.size, CornerRadius(3 * u))
                drawRoundRect(if (oscuro) t(HIERRO_LUZ, true) else HIERRO, tabla.topLeft, tabla.size, CornerRadius(3 * u))
                drawRoundRect(if (oscuro) LATON_OSC else HIERRO_LUZ, tabla.topLeft, tabla.size, CornerRadius(3 * u), style = Stroke(0.8f * u))
                val papel = Rect(tabla.left + 3 * u, tabla.top + 3 * u, tabla.right - 3 * u, tabla.bottom - 3 * u)
                drawRect(fondo, papel.topLeft, papel.size)
                if (e.presionado) drawRect(rojo.copy(alpha = 0.07f), papel.topLeft, papel.size)
                // La raya roja de urgencia, a la izquierda del papel.
                drawRect(rojo, papel.topLeft, Size(3.2f * u, papel.height))
                drawRect(rojo.copy(alpha = 0.4f), Offset(papel.left + 4.4f * u, papel.top), Size(0.7f * u, papel.height))
                // La pinza de latón arriba al centro, con su ojo.
                val ancho = minOf(64 * u, w * 0.3f); val cx = w / 2
                drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(cx - ancho / 2, 1.4f * u), Size(ancho, 9 * u), CornerRadius(2.2f * u))
                drawRoundRect(t(LATON, oscuro), Offset(cx - ancho / 2, 0.4f * u), Size(ancho, 9 * u), CornerRadius(2.2f * u))
                drawRoundRect(t(LATON_OSC, oscuro), Offset(cx - ancho / 2, 0.4f * u), Size(ancho, 9 * u), CornerRadius(2.2f * u), style = Stroke(0.7f * u))
                drawLine(t(LATON_LUZ, oscuro).copy(alpha = 0.8f), Offset(cx - ancho / 2 + 3 * u, 1.4f * u), Offset(cx + ancho / 2 - 3 * u, 1.4f * u), 0.7f * u)
                drawRoundRect(t(HIERRO_OSC, oscuro), Offset(cx - 5 * u, 3.2f * u), Size(10 * u, 3 * u), CornerRadius(1.5f * u))
                // Completo: el sello de tinta roja con su engrane, arriba a la derecha.
                if (e.destacado) {
                    val c = Offset(papel.right - 7.5f * u, papel.top + 4.6f * u)
                    drawCircle(rojo.copy(alpha = 0.85f), 4 * u, c, style = Stroke(0.9f * u))
                    drawPath(engrane(c, 2.6f * u, 6), rojo.copy(alpha = 0.85f))
                    drawCircle(fondo, 0.8f * u, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1 * u), r.size, CornerRadius(4 * u))
                drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(4 * u))
                // El marco de hierro forjado, con su filete de latón y un rizo en cada esquina de abajo.
                val fierro = if (oscuro) HIERRO_LUZ else HIERRO
                drawRoundRect(fierro.copy(alpha = 0.8f), r.topLeft, r.size, CornerRadius(4 * u), style = Stroke(1.6f * u))
                drawRoundRect((if (oscuro) LATON else LATON_OSC).copy(alpha = 0.55f), r.deflate(3.2f * u).topLeft, r.deflate(3.2f * u).size, CornerRadius(2 * u), style = Stroke(0.6f * u))
                listOf(r.left + 3.2f * u to 1f, r.right - 3.2f * u to -1f).forEach { (x, s) ->
                    val y = r.bottom - 3.2f * u
                    drawPath(Path().apply {
                        moveTo(x + s * 9 * u, y); cubicTo(x + s * 4 * u, y, x + s * 3 * u, y - 5 * u, x + s * 5.5f * u, y - 5.5f * u)
                        cubicTo(x + s * 7.5f * u, y - 5.5f * u, x + s * 7.5f * u, y - 3 * u, x + s * 6 * u, y - 3 * u)
                    }, fierro.copy(alpha = 0.7f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                }
                // La farola, colgada de un brazo que sale del borde derecho, con su resplandor.
                val fx = r.right - m * 0.55f
                drawLine(fierro, Offset(r.right, 2.6f * u), Offset(fx - 2 * u, 2.6f * u), 1 * u)
                drawArc(fierro, 180f, 180f, false, Offset(fx - 4 * u, 2.6f * u), Size(4 * u, 3 * u), style = Stroke(0.7f * u))
                val llama = Offset(fx, 8.6f * u)
                mancha(llama, 7.5f * u, GAS.copy(alpha = if (oscuro) 0.6f else 0.85f))
                drawPath(Path().apply {
                    moveTo(fx - 2 * u, 4 * u); lineTo(fx + 2 * u, 4 * u); lineTo(fx + 3.4f * u, 12 * u); lineTo(fx - 3.4f * u, 12 * u); close()
                }, fierro, style = Stroke(1 * u, join = StrokeJoin.Round))
                drawPath(Path().apply {
                    moveTo(llama.x, llama.y - 2.6f * u); quadraticTo(llama.x + 1.8f * u, llama.y + 0.4f * u, llama.x, llama.y + 1.2f * u)
                    quadraticTo(llama.x - 1.8f * u, llama.y + 0.4f * u, llama.x, llama.y - 2.6f * u)
                }, GAS)
                drawCircle(Color.White.copy(alpha = 0.85f), 0.6f * u, Offset(llama.x, llama.y + 0.2f * u))
                drawLine(fierro, Offset(fx - 4 * u, 12 * u), Offset(fx + 4 * u, 12 * u), 1.2f * u, StrokeCap.Round)
                // Sellado: el sello de latón con su marca, abajo a la izquierda.
                if (e.destacado) {
                    val c = Offset(r.left + m * 0.62f, r.bottom - m * 0.62f - 3 * u)
                    drawCircle(Color.Black.copy(alpha = 0.3f), 4.4f * u, c + Offset(0f, 0.8f * u))
                    drawCircle(t(LATON, oscuro), 4.4f * u, c)
                    drawCircle(t(LATON_OSC, oscuro), 3.4f * u, c, style = Stroke(0.6f * u))
                    drawPath(Path().apply { moveTo(c.x - 1.8f * u, c.y); lineTo(c.x - 0.4f * u, c.y + 1.5f * u); lineTo(c.x + 2 * u, c.y - 1.6f * u) },
                        t(HIERRO_OSC, oscuro), style = Stroke(1 * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                val radio = minOf(14 * u, r.height * 0.22f)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(radio))
                drawRoundRect(t(LATON, oscuro), r.topLeft, r.size, CornerRadius(radio))
                drawRoundRect(t(LATON_OSC, oscuro), r.topLeft, r.size, CornerRadius(radio), style = Stroke(0.8f * u))
                val campo = r.deflate(2.8f * u)
                drawRoundRect(if (e.presionado) mezcla(fondo, Color.Black, 0.25f) else fondo, campo.topLeft, campo.size, CornerRadius(maxOf(radio - 2.8f * u, 1f)))
                // El canto en relieve: una raya de luz por dentro del latón y, por fuera, su sombra.
                drawRoundRect(t(LATON_LUZ, oscuro).copy(alpha = if (e.presionado) 0.85f else 0.55f), campo.deflate(1.8f * u).topLeft, campo.deflate(1.8f * u).size,
                    CornerRadius(maxOf(radio - 4.6f * u, 1f)), style = Stroke(0.9f * u))
                drawRoundRect(Color.Black.copy(alpha = 0.5f), campo.topLeft, campo.size, CornerRadius(maxOf(radio - 2.8f * u, 1f)), style = Stroke(0.6f * u))
                // Los dos pernos a los lados, a media altura.
                listOf(campo.left + 4.2f * u, campo.right - 4.2f * u).forEach { x ->
                    val p = Offset(x, campo.center.y)
                    drawCircle(Color.Black.copy(alpha = 0.4f), 2 * u, p + Offset(0f, 0.5f * u))
                    drawCircle(t(LATON, oscuro), 1.8f * u, p)
                    drawCircle(t(LATON_LUZ, oscuro), 0.7f * u, Offset(p.x - 0.5f * u, p.y - 0.5f * u))
                }
            }
        }
    }
}

/**
 * Napoleón: el campo es la tira de pergamino de las órdenes, como su buscador, ahora montada en un marco de caoba veteada:
 * sus dos extremos enrollados a los lados y el doble filete dorado arriba y abajo. Con foco, el filete se aviva y cae en la
 * esquina una gota de lacre rojo con la N; con un error, el filete se vuelve rojo. En modo oscuro, el pergamino a la luz
 * de las velas, hundido, con la letra clara.
 */
object ComunNapoleon : DisenoComun {
    private val CAOBA = Color(0xFF6E2F1E)
    private val CAOBA_LUZ = Color(0xFF8D4430)
    private val CAOBA_VETA = Color(0xFF4A1E12)
    private val ORO = Color(0xFFD9B54A)
    private val LATON_LUZ = Color(0xFFE5C988)
    private val PERGAMINO = Color(0xFFEDE2C4)
    private val TINTA = Color(0xFF2A1C14)
    private val TEJUELO = Color(0xFFA4282A)
    private val LACRE = Color(0xFF8E1B1F)
    private val AZUL_IMPERIAL = Color(0xFF1E3A8A)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0E0B08), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2E8CC) else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFA89C80) else Color(0xFF7E6A52)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) ORO else AZUL_IMPERIAL
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFFA494) else TEJUELO

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        // El marco de caoba veteada.
        val marco = Rect(0f, 0f, w, h - 1 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(0f, 1 * u), marco.size, CornerRadius(3 * u))
        drawRoundRect(c(CAOBA), marco.topLeft, marco.size, CornerRadius(3 * u))
        clipRect(marco.left, marco.top, marco.right, marco.bottom) { vetas(marco, c(CAOBA_VETA).copy(alpha = 0.55f), 95 + e.semilla % 7, densidad = 0.7f, grosor = 0.7f) }
        drawLine(c(CAOBA_LUZ), Offset(3 * u, 0.6f * u), Offset(w - 3 * u, 0.6f * u), 1.2f * u)
        // La tira de pergamino, lisa, con su sombra sobre la caoba.
        val papel = apagar(if (oscuro) mezcla(PERGAMINO, Color(0xFF0E0B08), 0.72f) else PERGAMINO, e.habilitado)
        val tira = Rect(5 * u, 4.5f * u, w - 5 * u, h - 5.5f * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tira.left + 0.8f * u, tira.top + 1.4f * u), tira.size)
        drawRect(papel, tira.topLeft, tira.size)
        if (e.multilinea) renglones(tira.left + 8 * u, tira.right - 8 * u, margenCampoY.toPx(), tira.bottom - 6 * u, tintaCampo(oscuro).copy(alpha = 0.08f), 0.7f * u)
        // Los extremos enrollados.
        listOf(tira.left + 2 * u, tira.right - 2 * u).forEach { x ->
            drawRoundRect(mezcla(papel, Color.Black, 0.2f), Offset(x - 3 * u, tira.top - 1.5f * u), Size(6 * u, tira.height + 3 * u), CornerRadius(3 * u))
            drawRoundRect(mezcla(papel, Color.Black, 0.08f), Offset(x - 1.6f * u, tira.top - 1 * u), Size(3.2f * u, tira.height + 2 * u), CornerRadius(1.6f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.45f), Offset(x - 1.6f * u, tira.top), Offset(x - 1.6f * u, tira.bottom), 0.7f * u)
        }
        // El doble filete dorado de arriba y de abajo.
        val oro = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> apagar(if (oscuro) LATON_LUZ else Color(0xFFC79A2A), e.habilitado)
            else -> c(ORO).copy(alpha = 0.6f)
        }
        val grueso = if (e.enfocado || e.error) 1.4f * u else 0.9f * u
        val x0 = tira.left + 7 * u; val x1 = tira.right - 7 * u
        listOf(tira.top + 2.5f * u to tira.top + 4.4f * u, tira.bottom - 2.5f * u to tira.bottom - 4.4f * u).forEach { (a, b) ->
            drawLine(oro, Offset(x0, a), Offset(x1, a), grueso)
            drawLine(oro.copy(alpha = oro.alpha * 0.7f), Offset(x0 + 2 * u, b), Offset(x1 - 2 * u, b), 0.5f * u)
        }
        // La gota de lacre del foco, con la N.
        if (e.enfocado && e.habilitado) {
            val l = Offset(tira.right - 4 * u, tira.bottom)
            drawCircle(Color.Black.copy(alpha = 0.3f), 5.6f * u, l + Offset(0.6f * u, 1 * u))
            drawCircle(c(LACRE), 5.4f * u, l)
            drawCircle(c(TEJUELO), 3.9f * u, l, style = Stroke(0.7f * u))
            drawPath(Path().apply {
                moveTo(l.x - 1.6f * u, l.y + 2 * u); lineTo(l.x - 1.6f * u, l.y - 2 * u); lineTo(l.x + 1.6f * u, l.y + 2 * u); lineTo(l.x + 1.6f * u, l.y - 2 * u)
            }, c(LATON_LUZ), style = Stroke(0.8f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El filete dorado del boletín, grueso y fino, roto al centro por la estrella del Imperio entre dos ramitas de laurel
     * de tres hojas.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val oro = (if (oscuro) ORO.copy(alpha = 0.65f) else Color(0xFFB08A2E).copy(alpha = 0.8f)).let { it.copy(alpha = it.alpha * f) }
        val hueco = 14 * u
        listOf(4 * u to cx - hueco, cx + hueco to w - 4 * u).forEach { (a, b) ->
            drawLine(oro, Offset(a, cy - 0.6f * u), Offset(b, cy - 0.6f * u), 1 * u)
            if (b - a > 8 * u) drawLine(oro.copy(alpha = oro.alpha * 0.6f), Offset(a + 3 * u, cy + 1.4f * u), Offset(b - 3 * u, cy + 1.4f * u), 0.5f * u)
        }
        // Las ramitas de laurel: un tallo y tres hojas alternas, hacia cada lado.
        listOf(-1f, 1f).forEach { s ->
            drawLine(oro, Offset(cx + s * 4.5f * u, cy), Offset(cx + s * 12.5f * u, cy - 0.6f * u), 0.6f * u)
            for (k in 0 until 3) {
                val arriba = k % 2 == 0
                val p = Offset(cx + s * (6.2f + 2.6f * k) * u, cy + (if (arriba) -1.3f else 1.1f) * u)
                rotate(s * (if (arriba) -28f else 28f), p) { drawOval(oro, Offset(p.x - 1.7f * u, p.y - 0.75f * u), Size(3.4f * u, 1.5f * u)) }
            }
        }
        drawPath(estrella(Offset(cx, cy), 3.4f * u, 5, 0.42f), oro.copy(alpha = (if (oscuro) 0.9f else 1f) * f))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD9B25B) else AZUL_IMPERIAL

    /**
     * El doble filete dorado de las órdenes bajo el texto, grueso y fino, con un punto de oro en cada punta. Al tocarlo,
     * asoma detrás un dorado tenue (de noche, el azul imperial del uniforme) y el filete se aviva.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        if (toque) drawRoundRect(if (oscuro) Color(0xFF26337A).copy(alpha = 0.55f) else ORO.copy(alpha = 0.18f), Offset(3 * u, cy - 16 * u), Size(w - 6 * u, 32 * u), CornerRadius(2 * u))
        val oro = apagarBoton(
            if (oscuro) t(if (toque) LATON_LUZ else ORO, true) else (if (toque) Color(0xFF9C741A) else Color(0xFFB8892B)), e.habilitado)
        val y = minOf(cy + 11.5f * u, h - 4 * u)
        val x0 = 12 * u; val x1 = w - 12 * u
        if (x1 > x0) {
            drawLine(oro, Offset(x0, y), Offset(x1, y), if (toque) 1.4f * u else 1 * u)
            if (x1 - x0 > 8 * u) drawLine(oro.copy(alpha = oro.alpha * 0.7f), Offset(x0 + 3 * u, y + 2 * u), Offset(x1 - 3 * u, y + 2 * u), 0.5f * u)
            drawCircle(oro, 0.9f * u, Offset(x0 - 2.6f * u, y + 0.6f * u))
            drawCircle(oro, 0.9f * u, Offset(x1 + 2.6f * u, y + 0.6f * u))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un renglón del registro de las órdenes: el doble filete dorado muy tenue al pie y, a la izquierda, un losange de oro.
     * Al tocarla, asoma detrás el azul imperial (de noche, un dorado tenue) y el losange se aviva y crece.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        if (e.presionado) drawRect(if (oscuro) ORO.copy(alpha = 0.09f) else AZUL_IMPERIAL.copy(alpha = 0.07f), Offset.Zero, size)
        val oro = if (oscuro) ORO else Color(0xFFB08A2E)
        val x0 = margenFila.toPx()
        drawLine(oro.copy(alpha = 0.32f), Offset(x0, h - 2.6f * u), Offset(w, h - 2.6f * u), 0.9f * u)
        drawLine(oro.copy(alpha = 0.2f), Offset(x0 + 3 * u, h - 1 * u), Offset(w, h - 1 * u), 0.5f * u)
        val r = if (e.presionado) 3.4f * u else 2.4f * u
        val p = Offset(6.5f * u, cy)
        drawPath(Path().apply { moveTo(p.x, p.y - r * 1.3f); lineTo(p.x + r, p.y); lineTo(p.x, p.y + r * 1.3f); lineTo(p.x - r, p.y); close() },
            oro.copy(alpha = if (e.presionado) 1f else 0.6f))
    }

    /** La moharra del estandarte: el asta corta, la cruceta y la punta de lanza hacia adelante. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val color = if (oscuro) ORO else Color(0xFF9C741A)
        drawLine(color, Offset(2 * u, cy), Offset(10 * u, cy), 1.4f * u, StrokeCap.Round)
        drawLine(color, Offset(9.5f * u, cy - 2.6f * u), Offset(9.5f * u, cy + 2.6f * u), 1.2f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(11 * u, cy); quadraticTo(13 * u, cy - 3.4f * u, 18 * u, cy); quadraticTo(13 * u, cy + 3.4f * u, 11 * u, cy); close() }, color)
    }

    /** La abeja del Imperio: de oro, con sus franjas, si la fila está fija; solo su contorno, tenue, si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val cx = size.width / 2; val cy = size.height / 2
        val oro = if (oscuro) LATON_LUZ else Color(0xFFC79A2A)
        val tenue = (if (oscuro) ORO else Color(0xFF8A6A2A)).copy(alpha = 0.55f)
        val color = if (fija) oro else tenue
        val estilo = if (fija) null else Stroke(1 * u)
        // Las alas: dos óvalos abiertos hacia arriba, a los lados del cuerpo.
        listOf(-1f, 1f).forEach { s ->
            val c = Offset(cx + s * 5.4f * u, cy - 2.6f * u)
            rotate(s * 50f, c) {
                if (estilo == null) drawOval(oro.copy(alpha = 0.6f), Offset(c.x - 3 * u, c.y - 5 * u), Size(6 * u, 10 * u))
                else drawOval(color, Offset(c.x - 3 * u, c.y - 5 * u), Size(6 * u, 10 * u), style = estilo)
            }
        }
        // La cabeza y el cuerpo.
        val cuerpo = Rect(cx - 3.6f * u, cy - 3.4f * u, cx + 3.6f * u, cy + 10.5f * u)
        if (estilo == null) {
            drawCircle(color, 2.6f * u, Offset(cx, cy - 6.4f * u))
            drawOval(color, cuerpo.topLeft, cuerpo.size)
            val franja = t(CAOBA_VETA, oscuro)
            listOf(0.6f, 3.6f, 6.6f).forEach { dy -> drawLine(franja, Offset(cx - 3.2f * u, cy + dy * u), Offset(cx + 3.2f * u, cy + dy * u), 1 * u) }
        } else {
            drawCircle(color, 2.6f * u, Offset(cx, cy - 6.4f * u), style = estilo)
            drawOval(color, cuerpo.topLeft, cuerpo.size, style = estilo)
            listOf(1.6f, 5f).forEach { dy -> drawLine(color, Offset(cx - 3 * u, cy + dy * u), Offset(cx + 3 * u, cy + dy * u), 0.8f * u) }
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFBDB7A3) else Color(0xFF4F5874)

    /**
     * La orden en blanco: el pergamino apenas teñido (de noche, el azul del uniforme), el doble filete dorado (grueso y fino)
     * y una roseta de oro en cada esquina de dentro.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRect(if (oscuro) Color(0xFF1B2550).copy(alpha = 0.7f) else PERGAMINO.copy(alpha = 0.6f), r.topLeft, r.size)
        val oro = if (oscuro) ORO else Color(0xFFB08A2E)
        drawRect(oro.copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(1.1f * u))
        val dentro = r.deflate(4 * u)
        drawRect(oro.copy(alpha = 0.4f), dentro.topLeft, dentro.size, style = Stroke(0.5f * u))
        listOf(dentro.topLeft, dentro.topRight, dentro.bottomLeft, dentro.bottomRight).forEach { p ->
            drawCircle(oro.copy(alpha = 0.75f), 1.5f * u, p)
            for (k in 0 until 4) {
                val a = PI.toFloat() / 2 * k + PI.toFloat() / 4
                drawCircle(oro.copy(alpha = 0.5f), 0.7f * u, Offset(p.x + cos(a) * 2.6f * u, p.y + sin(a) * 2.6f * u))
            }
        }
    }

    /** El bicornio sin dueño, dejado sobre la mesa: el fieltro negro con su galón de oro y la escarapela tricolor. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(f(0.08f), f(0.7f)), Size(f(0.84f), f(0.12f)))
        val sombrero = Path().apply {
            moveTo(f(0.04f), f(0.7f)); quadraticTo(f(0.5f), f(0.02f), f(0.96f), f(0.7f)); quadraticTo(f(0.5f), f(0.54f), f(0.04f), f(0.7f)); close()
        }
        drawPath(sombrero, if (oscuro) Color(0xFF2C2A3A) else Color(0xFF1C1A22))
        drawPath(sombrero, if (oscuro) Color(0xFF6A6680) else Color.Black, style = Stroke(1 * u))
        // El galón de oro de la orilla.
        drawPath(Path().apply { moveTo(f(0.08f), f(0.68f)); quadraticTo(f(0.5f), f(0.52f), f(0.92f), f(0.68f)) }, if (oscuro) ORO else Color(0xFFC79A2A), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // La escarapela: azul, blanco y rojo, del borde al centro, con su presilla de oro.
        val c = Offset(f(0.66f), f(0.38f))
        drawCircle(t(AZUL_IMPERIAL, oscuro), f(0.11f), c)
        drawCircle(Color(0xFFF2EEE4), f(0.075f), c)
        drawCircle(t(TEJUELO, oscuro), f(0.04f), c)
        drawLine(if (oscuro) ORO else Color(0xFFC79A2A), Offset(c.x, c.y - f(0.11f)), Offset(c.x - f(0.04f), c.y - f(0.26f)), 1.2f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2E8CC) else TINTA

    /**
     * El despacho del cuartel general: la tira de pergamino (de noche, el paño azul) con su filete de oro arriba y abajo y,
     * a la izquierda, el sello de lacre rojo con la N, con sus dos cintas que cuelgan.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) Color(0xFF1B2550) else PERGAMINO.copy(alpha = 0.75f), Offset.Zero, size)
        val oro = (if (oscuro) ORO else Color(0xFFB08A2E)).copy(alpha = 0.7f)
        drawLine(oro, Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 1 * u)
        drawLine(oro, Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 1 * u)
        // Las cintas y el sello.
        val c = Offset(15 * u, minOf(h / 2, 18 * u))
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(c.x + s * 1 * u, c.y); lineTo(c.x + s * 5 * u, c.y + 11 * u); lineTo(c.x + s * 3 * u, c.y + 10 * u); lineTo(c.x + s * 2.4f * u, c.y + 12.5f * u)
                lineTo(c.x - s * 1.4f * u, c.y + 1 * u); close()
            }, t(AZUL_IMPERIAL, oscuro))
        }
        val lacre = if (oscuro) Color(0xFFA82A2E) else LACRE
        drawCircle(Color.Black.copy(alpha = 0.3f), 8 * u, c + Offset(0.6f * u, 1 * u))
        for (k in 0 until 7) {
            val a = 2 * PI.toFloat() * k / 7 + azar(e.semilla, k)
            drawCircle(lacre, (2 + azar(e.semilla, k + 9)) * u, Offset(c.x + cos(a) * 6.6f * u, c.y + sin(a) * 6.6f * u))
        }
        drawCircle(lacre, 7.4f * u, c)
        drawCircle(mezcla(lacre, Color.White, 0.18f), 5.2f * u, c, style = Stroke(0.8f * u))
        drawPath(Path().apply {
            moveTo(c.x - 2 * u, c.y + 2.6f * u); lineTo(c.x - 2 * u, c.y - 2.6f * u); lineTo(c.x + 2 * u, c.y + 2.6f * u); lineTo(c.x + 2 * u, c.y - 2.6f * u)
        }, LATON_LUZ, style = Stroke(1 * u, join = StrokeJoin.Round))
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = LATON_LUZ
    override fun rotuloCifra(oscuro: Boolean): Color = Color(0xFFE6DFCA)

    /**
     * La placa de una condecoración: el paño azul imperial liso, el doble filete de oro y una estrella chica arriba a la
     * derecha, donde no estorba al número.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1.2f * u), r.size)
        drawRect(t(AZUL_IMPERIAL, oscuro), r.topLeft, r.size)
        drawRect(t(ORO, oscuro), r.topLeft, r.size, style = Stroke(1.4f * u))
        val dentro = r.deflate(3.2f * u)
        drawRect(t(ORO, oscuro).copy(alpha = 0.6f), dentro.topLeft, dentro.size, style = Stroke(0.6f * u))
        drawPath(estrella(Offset(dentro.right - 5 * u, dentro.top + 5 * u), 3 * u, 5, 0.42f), t(LATON_LUZ, oscuro).copy(alpha = 0.85f))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: el pergamino del panel, el de la orden, la noche del vivac, el cuero rojo del tejuelo. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF2A1A12) else Color(0xFFF2E9D0)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF221A14) else Color(0xFFF4EBD3)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0F1736) else Color(0xFFDDE1EB)
        TipoTarjeta.VIDA -> if (oscuro) t(TEJUELO, true) else Color(0xFF8A2224)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.VIDA -> Color(0xFFF3E7CC)
        oscuro -> Color(0xFFF1E9D2)
        else -> TINTA
    }
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.VIDA -> Color(0xFFEAD3C0)
        oscuro -> Color(0xFFC2B59A)
        else -> Color(0xFF5B4636)
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFFE58A7E) else LACRE
        TipoTarjeta.VIDA -> LATON_LUZ
        else -> if (oscuro) LATON_LUZ else AZUL_IMPERIAL
    }

    /** Una abeja imperial de oro, de [l] de largo, con la cabeza hacia arriba. */
    private fun DrawScope.abeja(c: Offset, l: Float, oro: Color, raya: Color) {
        listOf(-1f, 1f).forEach { s ->
            drawOval(oro.copy(alpha = 0.75f), Offset(c.x + s * l * 0.1f - (if (s < 0) l * 0.42f else 0f), c.y - l * 0.32f), Size(l * 0.42f, l * 0.28f))
        }
        drawOval(oro, Offset(c.x - l * 0.16f, c.y - l * 0.22f), Size(l * 0.32f, l * 0.62f))
        drawCircle(oro, l * 0.12f, Offset(c.x, c.y - l * 0.32f))
        listOf(0.06f, 0.2f).forEach { f -> drawLine(raya, Offset(c.x - l * 0.13f, c.y + l * f), Offset(c.x + l * 0.13f, c.y + l * f), l * 0.06f) }
    }

    /**
     * Las cuatro son del gabinete de campaña. La cifra del tablero (METRICA) es un panel de marquetería: el marco de caoba
     * veteada con una roseta de oro en cada esquina, el filete de oro y el pergamino liso; si la cifra se aparta de tu normal,
     * el filete engruesa y se posa en el marco una abeja imperial. Lo que te falta hoy (PENDIENTE) es la orden del día: el
     * pergamino en su marco de caoba con filete de oro, la banda tricolor a la izquierda y la escarapela en la esquina; completo,
     * la escarapela lleva al centro una estrella de oro. La noche (NOCHE) es el vivac: el paño de noche con estrellitas de oro
     * en la orilla y una vela en su candelero abajo a la izquierda; sellado el día, una corona de laurel en la esquina. Las de
     * Vida (VIDA) son tejuelos de lomo: cuero rojo con doble filete de oro, rombos en las esquinas y florones arriba y abajo.
     * Al tocarlas, se hunden y se oscurecen un poco.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val oro = t(ORO, oscuro)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.top + 1.5f * u), r.size)
                drawRect(t(CAOBA, oscuro), r.topLeft, r.size)
                vetas(r, t(CAOBA_VETA, oscuro).copy(alpha = 0.85f), e.semilla + 3, densidad = 1.3f, grosor = 0.8f)
                drawRect(t(CAOBA_LUZ, oscuro).copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(0.8f * u))
                val banda = minOf(m * 0.5f, 7 * u)
                val filete = r.deflate(banda)
                drawRect(if (e.destacado) t(LATON_LUZ, oscuro) else oro, filete.topLeft, filete.size)
                val dentro = filete.deflate(if (e.destacado) 1.8f * u else 1 * u)
                drawRect(if (e.presionado) mezcla(fondo, CAOBA, 0.08f) else fondo, dentro.topLeft, dentro.size)
                // Las rosetas de oro en el marco, una por esquina.
                listOf(Offset(r.left + banda / 2, r.top + banda / 2), Offset(r.right - banda / 2, r.top + banda / 2),
                    Offset(r.left + banda / 2, r.bottom - banda / 2), Offset(r.right - banda / 2, r.bottom - banda / 2)).forEach { p ->
                    drawCircle(oro, banda * 0.3f, p)
                    drawCircle(t(CAOBA_VETA, oscuro), banda * 0.1f, p)
                }
                if (e.destacado) abeja(Offset(r.right - banda * 2.6f, r.top + banda * 0.55f), banda * 0.95f, t(LATON_LUZ, oscuro), t(CAOBA_VETA, oscuro))
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2 * u)
                drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.top + 1.5f * u), r.size)
                drawRect(t(CAOBA, oscuro), r.topLeft, r.size)
                val papel = r.deflate(2 * u)
                drawRect(fondo, papel.topLeft, papel.size)
                if (e.presionado) drawRect(LACRE.copy(alpha = 0.06f), papel.topLeft, papel.size)
                drawRect((if (e.destacado) LATON_LUZ else oro).copy(alpha = 0.9f), papel.deflate(1.6f * u).topLeft, papel.deflate(1.6f * u).size, style = Stroke(if (e.destacado) 1.4f * u else 0.9f * u))
                // La banda tricolor a la izquierda: azul, blanco y rojo.
                val x0 = papel.left + 3.4f * u; val y0 = papel.top + 3.4f * u; val y1 = papel.bottom - 3.4f * u
                listOf(t(AZUL_IMPERIAL, oscuro), if (oscuro) Color(0xFFCFC6B2) else Color(0xFFFFFCF4), t(TEJUELO, oscuro)).forEachIndexed { i, c ->
                    drawRect(c, Offset(x0 + i * 1.9f * u, y0), Size(1.9f * u, y1 - y0))
                }
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(x0, y0), Size(5.7f * u, y1 - y0), style = Stroke(0.4f * u))
                // La escarapela en la esquina de arriba (con su estrella de oro, ya completo).
                val c = Offset(papel.right - m * 0.6f, r.top + m * 0.46f)
                val rr = m * 0.4f
                drawCircle(Color.Black.copy(alpha = 0.25f), rr, c + Offset(0f, 0.6f * u))
                drawCircle(t(AZUL_IMPERIAL, oscuro), rr, c)
                drawCircle(if (oscuro) Color(0xFFCFC6B2) else Color.White, rr * 0.66f, c)
                drawCircle(t(TEJUELO, oscuro), rr * 0.36f, c)
                if (e.destacado) {
                    drawCircle(LATON_LUZ, rr, c, style = Stroke(0.8f * u))
                    drawPath(estrella(c, rr * 0.55f, 5, 0.42f), LATON_LUZ)
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1 * u), r.size)
                drawRect(fondo, r.topLeft, r.size)
                drawRect(oro.copy(alpha = 0.7f), r.topLeft, r.size, style = Stroke(1 * u))
                drawRect(oro.copy(alpha = 0.35f), r.deflate(2.6f * u).topLeft, r.deflate(2.6f * u).size, style = Stroke(0.5f * u))
                // Las estrellitas de oro en la orilla (no donde está la vela).
                val astro = if (oscuro) LATON_LUZ else Color(0xFF8E7330)
                val n = ((w + h) / (18 * u)).toInt().coerceIn(5, 40)
                puntosMargen(w, h, m, n, e.semilla + 5, 4 * u).forEach { (p, b) ->
                    if (p.x > m * 1.4f || p.y < h - m * 3.2f) drawPath(estrella(p, (1 + 0.9f * b) * u, 5, 0.45f), astro.copy(alpha = 0.5f + 0.45f * b))
                }
                // La vela en su candelero, abajo a la izquierda, con su resplandor.
                val x = r.left + m * 0.5f
                val pie = r.bottom - 3 * u
                val alto = minOf(m * 1.1f, r.height * 0.3f)
                val llama = Offset(x, pie - alto - 2.6f * u)
                mancha(llama, m * 0.75f, Color(0xFFFFD27A).copy(alpha = if (oscuro) 0.4f else 0.6f))
                drawOval(t(LATON_LUZ, oscuro), Offset(x - 3.6f * u, pie - 1.2f * u), Size(7.2f * u, 2.4f * u))
                drawRect(oro, Offset(x - 0.9f * u, pie - 3 * u), Size(1.8f * u, 3 * u))
                drawRect(if (oscuro) Color(0xFFE8DCC0) else Color(0xFFFFFAEE), Offset(x - 1.5f * u, pie - alto), Size(3 * u, alto - 3 * u))
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(x - 1.5f * u, pie - alto), Size(3 * u, alto - 3 * u), style = Stroke(0.4f * u))
                drawPath(Path().apply {
                    moveTo(llama.x, llama.y - 2.6f * u); quadraticTo(llama.x + 1.7f * u, llama.y + 0.6f * u, llama.x, llama.y + 1.6f * u)
                    quadraticTo(llama.x - 1.7f * u, llama.y + 0.6f * u, llama.x, llama.y - 2.6f * u)
                }, Color(0xFFFFC24A))
                drawCircle(Color.White.copy(alpha = 0.85f), 0.6f * u, Offset(llama.x, llama.y + 0.4f * u))
                // Sellado: la corona de laurel de oro, abajo a la derecha.
                if (e.destacado) {
                    val c = Offset(r.right - m * 0.6f, r.bottom - m * 0.6f)
                    val rr = m * 0.42f
                    listOf(-1f, 1f).forEach { s ->
                        drawArc(LATON_LUZ, if (s < 0) 100f else -20f, 100f, false, Offset(c.x - rr, c.y - rr), Size(2 * rr, 2 * rr), style = Stroke(0.7f * u))
                        for (k in 0 until 4) {
                            val a = (if (s < 0) 110f + k * 24f else 70f - k * 24f) * PI.toFloat() / 180f
                            val p = Offset(c.x + cos(a) * rr, c.y + sin(a) * rr)
                            rotate((a * 180f / PI.toFloat()) + 90f, p) { drawOval(LATON_LUZ, Offset(p.x - 1.4f * u, p.y - 0.6f * u), Size(2.8f * u, 1.2f * u)) }
                        }
                    }
                    drawPath(estrella(c, rr * 0.45f, 5, 0.42f), LATON_LUZ)
                }
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(2 * u))
                drawRoundRect(if (e.presionado) mezcla(fondo, Color.Black, 0.18f) else fondo, r.topLeft, r.size, CornerRadius(2 * u))
                drawRoundRect(Color.White.copy(alpha = 0.07f), Offset(r.left + 2 * u, r.top + 1 * u), Size(r.width - 4 * u, 1.4f * u), CornerRadius(0.7f * u))
                val dorado = if (e.presionado) LATON_LUZ else oro
                val f1 = r.deflate(2.4f * u); val f2 = r.deflate(4.4f * u)
                drawRect(dorado, f1.topLeft, f1.size, style = Stroke(1 * u))
                drawRect(dorado.copy(alpha = 0.75f), f2.topLeft, f2.size, style = Stroke(0.5f * u))
                // Los rombos de las esquinas y los florones de arriba y de abajo.
                fun rombo(c: Offset, rr: Float) = drawPath(Path().apply { moveTo(c.x, c.y - rr); lineTo(c.x + rr, c.y); lineTo(c.x, c.y + rr); lineTo(c.x - rr, c.y); close() }, dorado)
                listOf(f2.topLeft, f2.topRight, f2.bottomLeft, f2.bottomRight).forEach { rombo(it, 1.6f * u) }
                listOf(f1.top + 2 * u, f1.bottom - 2 * u).forEach { y ->
                    rombo(Offset(w / 2, y), 1.3f * u)
                    drawCircle(dorado, 0.6f * u, Offset(w / 2 - 3 * u, y)); drawCircle(dorado, 0.6f * u, Offset(w / 2 + 3 * u, y))
                }
            }
        }
    }
}

/**
 * Art déco: el campo es la placa de laca negra del salón del transatlántico, como su buscador: esquinas en escalón, doble
 * filete de oro y, al centro de arriba y de abajo, un rombo de oro que corta el filete. La laca es negra en los dos modos
 * (y la letra, crema); con foco, el filete se enciende en oro claro y el de dentro en jade; con un error, en coral.
 */
object ComunDeco : DisenoComun {
    private val LACA = Color(0xFF16171A)
    private val CREMA = Color(0xFFF0E6CF)
    private val ORO = Color(0xFFCDAA5C)
    private val ORO_CLARO = Color(0xFFE6C77A)
    private val JADE_LUZ = Color(0xFF4FA88C)
    private val CROMO = Color(0xFFC9CED3)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0C0C0E), 0.34f) else c

    /** Un rectángulo de esquinas en escalón, de [e] de entrante, como las cartelas del estante. */
    private fun escalonado(r: Rect, e: Float): Path = Path().apply {
        moveTo(r.left + e, r.top); lineTo(r.right - e, r.top); lineTo(r.right - e, r.top + e * 0.5f); lineTo(r.right - e * 0.5f, r.top + e * 0.5f)
        lineTo(r.right - e * 0.5f, r.top + e); lineTo(r.right, r.top + e); lineTo(r.right, r.bottom - e); lineTo(r.right - e * 0.5f, r.bottom - e)
        lineTo(r.right - e * 0.5f, r.bottom - e * 0.5f); lineTo(r.right - e, r.bottom - e * 0.5f); lineTo(r.right - e, r.bottom); lineTo(r.left + e, r.bottom)
        lineTo(r.left + e, r.bottom - e * 0.5f); lineTo(r.left + e * 0.5f, r.bottom - e * 0.5f); lineTo(r.left + e * 0.5f, r.bottom - e); lineTo(r.left, r.bottom - e)
        lineTo(r.left, r.top + e); lineTo(r.left + e * 0.5f, r.top + e); lineTo(r.left + e * 0.5f, r.top + e * 0.5f); lineTo(r.left + e, r.top + e * 0.5f); close()
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = CREMA
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF948C7C)
    override fun rotuloCampo(oscuro: Boolean): Color = ORO_CLARO
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFF0907A)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        val placa = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        val esq = minOf(8 * u, placa.height / 4)
        // La placa de laca con su sombra y su brillo.
        drawPath(escalonado(Rect(placa.left + 0.8f * u, placa.top + 1.5f * u, placa.right + 0.8f * u, placa.bottom + 1.5f * u), esq), Color.Black.copy(alpha = 0.3f))
        drawPath(escalonado(placa, esq), c(LACA))
        drawRect(Color.White.copy(alpha = 0.035f), Offset(placa.left + esq + 4 * u, placa.top + 5.5f * u), Size(placa.width - 2 * esq - 8 * u, 2 * u))
        if (e.multilinea) renglones(placa.left + 12 * u, placa.right - 12 * u, margenCampoY.toPx(), placa.bottom - 7 * u, c(ORO).copy(alpha = 0.1f), 0.6f * u)
        // El doble filete: oro claro con foco (y el de dentro en jade), coral con un error.
        val fuera = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> c(ORO_CLARO)
            else -> c(ORO).copy(alpha = 0.8f)
        }
        val dentro = when {
            e.error -> errorCampo(oscuro).copy(alpha = 0.5f)
            e.enfocado -> c(JADE_LUZ)
            else -> c(ORO).copy(alpha = 0.4f)
        }
        val f1 = placa.deflate(2.5f * u); val f2 = placa.deflate(5 * u)
        drawPath(escalonado(f1, esq * 0.75f), fuera, style = Stroke(if (e.enfocado || e.error) 1.5f * u else 1 * u))
        drawPath(escalonado(f2, esq * 0.56f), dentro, style = Stroke(if (e.enfocado) 0.9f * u else 0.5f * u))
        // Los rombos que cortan el filete, arriba y abajo al centro, con sus dos rayitas de cromo.
        listOf(f1.top, f1.bottom).forEach { y ->
            val cx = placa.center.x
            val r = 3.2f * u
            drawLine(c(LACA), Offset(cx - r - 2 * u, y), Offset(cx + r + 2 * u, y), 2.4f * u)
            val rombo = Path().apply { moveTo(cx, y - r); lineTo(cx + r, y); lineTo(cx, y + r); lineTo(cx - r, y); close() }
            drawPath(rombo, c(LACA))
            drawPath(rombo, fuera, style = Stroke(0.9f * u))
            drawCircle(fuera, 0.9f * u, Offset(cx, y))
            listOf(-1f, 1f).forEach { s -> drawLine(c(CROMO).copy(alpha = 0.7f), Offset(cx + s * (r + 3 * u), y), Offset(cx + s * (r + 7 * u), y), 0.6f * u) }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El abanico de rayos del salón: medio sol de oro al centro, de siete rayos, y a los lados el doble filete, el de dentro
     * más corto, que remata en un cuadrito de oro.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val oro = (if (oscuro) ORO.copy(alpha = 0.6f) else mezcla(ORO, Color.Black, 0.22f).copy(alpha = 0.8f)).let { it.copy(alpha = it.alpha * f) }
        // El medio sol: siete rayos que salen de un medio disco.
        val base = Offset(cx, cy + 2.2f * u)
        for (k in 0..6) {
            val a = PI.toFloat() * (1f + k / 6f)
            val largo = (if (k % 2 == 0) 6f else 4.6f) * u
            drawLine(oro, Offset(base.x + cos(a) * 2.2f * u, base.y + sin(a) * 2.2f * u), Offset(base.x + cos(a) * largo, base.y + sin(a) * largo), 0.8f * u)
        }
        drawArc(oro, 180f, 180f, true, Offset(base.x - 1.6f * u, base.y - 1.6f * u), Size(3.2f * u, 3.2f * u))
        // El doble filete con su remate.
        listOf(-1f, 1f).forEach { s ->
            val a = cx + s * 8 * u; val b = if (s < 0) 6 * u else w - 6 * u
            drawLine(oro, Offset(a, cy - 0.4f * u), Offset(b, cy - 0.4f * u), 0.9f * u)
            drawLine(oro.copy(alpha = oro.alpha * 0.6f), Offset(a, cy + 1.8f * u), Offset(b - s * 5 * u, cy + 1.8f * u), 0.5f * u)
            drawRect(oro, Offset(b - 1.1f * u, cy - 1.5f * u), Size(2.2f * u, 2.2f * u))
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD9B44A) else Color(0xFF7A590C)

    /**
     * La marquesina: el texto entre dos filetes finos de oro, arriba y abajo, rematados en un cuadrito en cada punta, como
     * las letras de un cartel de teatro. Al tocarlo se enciende: el oro aclara, corre por dentro un filete de jade y el
     * hueco toma un brillo de oro tenue.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        val alto = minOf(13 * u, h / 2 - 4 * u)
        val x0 = minOf(9 * u, w / 4); val x1 = w - x0
        if (toque) drawRect(ORO.copy(alpha = if (oscuro) 0.16f else 0.15f), Offset(x0, cy - alto), Size(x1 - x0, 2 * alto))
        val oro = apagarBoton(
            if (toque) (if (oscuro) ORO_CLARO else mezcla(ORO, Color.Black, 0.12f)) else (if (oscuro) t(ORO, true) else mezcla(ORO, Color.Black, 0.22f)),
            e.habilitado)
        val jade = if (oscuro) JADE_LUZ else mezcla(JADE_LUZ, Color.Black, 0.25f)
        listOf(cy - alto to 1f, cy + alto to -1f).forEach { (y, s) ->
            drawLine(oro, Offset(x0 + 2 * u, y), Offset(x1 - 2 * u, y), if (toque) 1 * u else 0.7f * u)
            drawRect(oro, Offset(x0 - 0.9f * u, y - 0.9f * u), Size(1.8f * u, 1.8f * u))
            drawRect(oro, Offset(x1 - 0.9f * u, y - 0.9f * u), Size(1.8f * u, 1.8f * u))
            if (toque && x1 - x0 > 8 * u) drawLine(jade, Offset(x0 + 4 * u, y + s * 2 * u), Offset(x1 - 4 * u, y + s * 2 * u), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un renglón de la carta del salón: el filete de oro muy fino al pie, que nace de un cuadrito, y a la izquierda un rombo
     * de oro con sus dos rayitas. Al tocarla, el renglón toma un brillo de oro tenue y corre a la izquierda una barra de jade.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val oro = if (oscuro) ORO else mezcla(ORO, Color.Black, 0.22f)
        if (e.presionado) {
            drawRect(ORO.copy(alpha = if (oscuro) 0.1f else 0.12f), Offset.Zero, size)
            drawRect(if (oscuro) JADE_LUZ else mezcla(JADE_LUZ, Color.Black, 0.25f), Offset(0f, cy - minOf(h * 0.3f, 14 * u)), Size(2 * u, 2 * minOf(h * 0.3f, 14 * u)))
        }
        val x0 = margenFila.toPx()
        drawLine(oro.copy(alpha = if (oscuro) 0.3f else 0.35f), Offset(x0 + 2 * u, h - 1 * u), Offset(w, h - 1 * u), 0.7f * u)
        drawRect(oro.copy(alpha = 0.5f), Offset(x0 - 0.8f * u, h - 1.8f * u), Size(1.6f * u, 1.6f * u))
        val r = 2.6f * u; val c = Offset(7 * u, cy)
        drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y + r); lineTo(c.x - r, c.y); close() }, oro.copy(alpha = if (e.presionado) 1f else 0.6f))
        drawLine(oro.copy(alpha = 0.4f), Offset(c.x, c.y - r - 3.6f * u), Offset(c.x, c.y - r - 1.2f * u), 0.6f * u)
        drawLine(oro.copy(alpha = 0.4f), Offset(c.x, c.y + r + 1.2f * u), Offset(c.x, c.y + r + 3.6f * u), 0.6f * u)
    }

    /** Tres galones de oro, del más vivo al más tenue, como las flechas de un ascensor del transatlántico. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val oro = if (oscuro) Color(0xFFD9B44A) else Color(0xFF7A590C)
        listOf(0.35f, 0.65f, 1f).forEachIndexed { i, a ->
            val x = (5 + 4.2f * i) * u
            drawPath(Path().apply { moveTo(x, cy - 4.5f * u); lineTo(x + 4 * u, cy); lineTo(x, cy + 4.5f * u) }, oro.copy(alpha = a), style = Stroke(1.4f * u, join = StrokeJoin.Miter))
        }
    }

    /** La estrella de cuatro puntas del vestíbulo, con sus rayos: de oro llena y centro de jade si la fila está fija; de contorno si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val oro = if (oscuro) ORO_CLARO else mezcla(ORO, Color.Black, 0.12f)
        val tenue = (if (oscuro) ORO else mezcla(ORO, Color.Black, 0.3f)).copy(alpha = 0.55f)
        val estrella4 = estrella(c, 10 * u, 4, 0.3f)
        for (k in 0 until 4) {
            val a = PI.toFloat() / 2 * k + PI.toFloat() / 4
            drawLine(if (fija) oro else tenue, Offset(c.x + cos(a) * 4.2f * u, c.y + sin(a) * 4.2f * u), Offset(c.x + cos(a) * 7.6f * u, c.y + sin(a) * 7.6f * u), 0.8f * u)
        }
        if (fija) {
            drawPath(estrella4, oro)
            drawCircle(if (oscuro) JADE_LUZ else mezcla(JADE_LUZ, Color.Black, 0.25f), 1.8f * u, c)
        } else {
            drawPath(estrella4, tenue, style = Stroke(1 * u))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFBDB192) else Color(0xFF574F43)

    /**
     * La cartela vacía del salón: el doble filete de oro de esquinas en escalón, apenas teñida (de laca, de noche), con un
     * medio sol de siete rayos asentado en el filete, arriba al centro.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 7 * u, w - 1 * u, h - 1 * u)
        val esq = minOf(9 * u, r.height / 4)
        drawPath(escalonado(r, esq), if (oscuro) LACA else ORO.copy(alpha = 0.07f))
        val oro = if (oscuro) ORO else mezcla(ORO, Color.Black, 0.22f)
        drawPath(escalonado(r, esq), oro.copy(alpha = 0.6f), style = Stroke(1 * u))
        drawPath(escalonado(r.deflate(3 * u), esq * 0.7f), oro.copy(alpha = 0.3f), style = Stroke(0.5f * u))
        // El medio sol de arriba, asentado en el filete.
        val cx = w / 2
        drawArc(oro.copy(alpha = 0.85f), 180f, 180f, true, Offset(cx - 3 * u, r.top - 3 * u), Size(6 * u, 6 * u))
        for (k in 0..6) {
            val a = PI.toFloat() * (1f + (k + 0.5f) / 7f)
            val largo = (if (k % 2 == 0) 7f else 5.6f) * u
            drawLine(oro.copy(alpha = 0.75f), Offset(cx + cos(a) * 4.2f * u, r.top + sin(a) * 4.2f * u), Offset(cx + cos(a) * largo, r.top + sin(a) * largo), 0.8f * u)
        }
        listOf(-1f, 1f).forEach { s -> drawLine(oro.copy(alpha = 0.6f), Offset(cx + s * 9 * u, r.top - 1.6f * u), Offset(cx + s * 16 * u, r.top - 1.6f * u), 0.6f * u) }
    }

    /** La copa de champán vacía (la «coupe»): el cáliz ancho y bajo, el tallo con su anillo, el pie en escalón y una sola burbuja que quedó. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val linea = if (oscuro) Color(0xFFB4A888) else Color(0xFF5A5246)
        val oro = if (oscuro) ORO_CLARO else mezcla(ORO, Color.Black, 0.2f)
        val tr = Stroke(1.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val caliz = Path().apply { moveTo(f(0.14f), f(0.28f)); quadraticTo(f(0.16f), f(0.54f), f(0.5f), f(0.55f)); quadraticTo(f(0.84f), f(0.54f), f(0.86f), f(0.28f)); close() }
        drawPath(caliz, (if (oscuro) CROMO else Color.White).copy(alpha = if (oscuro) 0.08f else 0.55f))
        drawPath(caliz, linea, style = tr)
        drawLine(oro, Offset(f(0.16f), f(0.28f)), Offset(f(0.84f), f(0.28f)), 1.6f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.8f), Offset(f(0.26f), f(0.34f)), Offset(f(0.3f), f(0.46f)), 1 * u, StrokeCap.Round)
        // El tallo con su anillo y el pie en dos escalones.
        drawLine(linea, Offset(f(0.5f), f(0.55f)), Offset(f(0.5f), f(0.84f)), tr.width)
        drawRect(oro, Offset(f(0.46f), f(0.66f)), Size(f(0.08f), f(0.035f)))
        drawRect(linea, Offset(f(0.38f), f(0.84f)), Size(f(0.24f), f(0.04f)))
        drawRect(linea, Offset(f(0.3f), f(0.88f)), Size(f(0.4f), f(0.04f)))
        drawCircle(oro, f(0.035f), Offset(f(0.58f), f(0.15f)), style = Stroke(0.9f * u))
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) CREMA else Color(0xFF16130E)

    /**
     * El telegrama del puente del transatlántico: la franja teñida de oro (de laca, de noche) entre dos filetes finos y, a la
     * izquierda, el abanico de rayos de oro que se abre hacia el texto, con su botón de jade.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) LACA else ORO.copy(alpha = 0.1f), Offset.Zero, size)
        val oro = if (oscuro) ORO else mezcla(ORO, Color.Black, 0.22f)
        drawLine(oro.copy(alpha = 0.7f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 1 * u)
        drawLine(oro.copy(alpha = 0.7f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u)
        drawLine(oro.copy(alpha = 0.35f), Offset(0f, 2.5f * u), Offset(w, 2.5f * u), 0.5f * u)
        drawLine(oro.copy(alpha = 0.35f), Offset(0f, h - 2.5f * u), Offset(w, h - 2.5f * u), 0.5f * u)
        val c = Offset(5 * u, minOf(h / 2, 18 * u))
        for (k in 0..6) {
            val a = PI.toFloat() * (-0.42f + 0.84f * k / 6f)
            val largo = (if (k % 2 == 0) 18f else 14f) * u
            drawLine(oro.copy(alpha = if (k % 2 == 0) 0.95f else 0.6f), Offset(c.x + cos(a) * 4 * u, c.y + sin(a) * 4 * u), Offset(c.x + cos(a) * largo, c.y + sin(a) * largo), 0.9f * u)
        }
        drawArc(oro, -90f, 180f, true, Offset(c.x - 3.4f * u, c.y - 3.4f * u), Size(6.8f * u, 6.8f * u))
        drawCircle(if (oscuro) JADE_LUZ else mezcla(JADE_LUZ, Color.Black, 0.25f), 1.4f * u, c + Offset(1.2f * u, 0f))
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = ORO_CLARO
    override fun rotuloCifra(oscuro: Boolean): Color = Color(0xFFD9CFB8)

    /**
     * La placa de laca negra del salón (en los dos modos): esquinas en escalón, el doble filete de oro (el de dentro más
     * tenue) y la laca lisa donde va el número, con su brillo apenas.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val placa = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        val esq = minOf(6 * u, placa.height / 4)
        drawPath(escalonado(Rect(placa.left + 0.6f * u, placa.top + 1.2f * u, placa.right + 0.6f * u, placa.bottom + 1.2f * u), esq), Color.Black.copy(alpha = 0.28f))
        drawPath(escalonado(placa, esq), t(LACA, oscuro))
        drawRect(Color.White.copy(alpha = 0.035f), Offset(placa.left + esq + 2 * u, placa.top + 4 * u), Size(maxOf(placa.width - 2 * esq - 4 * u, 1f), 1.6f * u))
        drawPath(escalonado(placa.deflate(2.2f * u), esq * 0.7f), t(ORO, oscuro).copy(alpha = 0.85f), style = Stroke(0.9f * u))
        drawPath(escalonado(placa.deflate(4.2f * u), esq * 0.5f), t(ORO, oscuro).copy(alpha = 0.35f), style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: crema de día y laca de noche; la noche de Día, del azul del cielo de la ciudad. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF1C1C20) else Color(0xFFF5EEDD)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF1A1A1E) else Color(0xFFF8F1E0)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0E1220) else Color(0xFFDDE1EA)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF202024) else Color(0xFFF2ECDE)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF0E6CC) else Color(0xFF16130E)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFB4A888) else Color(0xFF5A5246)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE -> if (oscuro) ORO_CLARO else Color(0xFF7A5A1C)
        TipoTarjeta.NOCHE -> if (oscuro) ORO_CLARO else Color(0xFF5E4614)
        else -> if (oscuro) JADE_LUZ else Color(0xFF1F6B55)
    }

    /** Un octágono: el rectángulo [q] con las esquinas cortadas en [c]. */
    private fun octagono(q: Rect, c: Float): Path = Path().apply {
        moveTo(q.left + c, q.top); lineTo(q.right - c, q.top); lineTo(q.right, q.top + c); lineTo(q.right, q.bottom - c)
        lineTo(q.right - c, q.bottom); lineTo(q.left + c, q.bottom); lineTo(q.left, q.bottom - c); lineTo(q.left, q.top + c); close()
    }

    /**
     * Las cuatro son del transatlántico y de la ciudad de los rascacielos. La cifra del tablero (METRICA) es el tablero del
     * ascensor: el marco de cromo, el fondo liso, el remate escalonado de oro que cuelga al centro de arriba y tres rayas finas
     * de oro a cada lado; si la cifra se aparta de tu normal, el remate se aviva, las rayas se vuelven de jade y una gema de
     * jade asoma bajo el remate. Lo que te falta hoy (PENDIENTE) es la invitación de gala: la franja de laca de arriba con su
     * friso de zigzag de oro, el doble filete y el marco fino; completo, la franja se vuelve de jade. La noche (NOCHE) es la
     * ciudad de noche: la luna en creciente y, al pie, la silueta de los rascacielos con ventanas encendidas y la aguja más
     * alta; sellado el día, la luz de la aguja se enciende. Las de Vida (VIDA) son placas de camarote: el marco octagonal de
     * cromo, el filete de oro y tres líneas de velocidad de jade. Al tocarlas, se hunden y se tiñen de oro.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val oro = if (oscuro) ORO else mezcla(ORO, Color.Black, 0.15f)
        val jade = if (oscuro) JADE_LUZ else mezcla(JADE_LUZ, Color.Black, 0.25f)
        val cromo = if (oscuro) mezcla(CROMO, Color.Black, 0.45f) else CROMO
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1.5f * u), r.size)
                drawRect(cromo, r.topLeft, r.size)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.6f), Offset(r.left, r.top + 0.5f * u), Offset(r.right, r.top + 0.5f * u), 1 * u)
                val dentro = r.deflate(2 * u)
                drawRect(fondo, dentro.topLeft, dentro.size)
                if (e.presionado) drawRect(ORO.copy(alpha = 0.1f), dentro.topLeft, dentro.size)
                // El remate escalonado, colgado del borde de arriba.
                val remate = if (e.destacado) ORO_CLARO else oro
                val ancho = minOf(36 * u, r.width * 0.3f)
                for (i in 0 until 3) {
                    val wi = ancho * (1f - 0.3f * i)
                    drawRect(remate, Offset(w / 2 - wi / 2, dentro.top + 2.2f * u * i), Size(wi, 2.2f * u))
                }
                if (e.destacado) {
                    val g = Offset(w / 2, dentro.top + 6.6f * u + 1.8f * u)
                    drawPath(Path().apply { moveTo(g.x, g.y - 2.2f * u); lineTo(g.x + 1.8f * u, g.y); lineTo(g.x, g.y + 2.2f * u); lineTo(g.x - 1.8f * u, g.y); close() }, JADE_LUZ)
                    drawPath(Path().apply { moveTo(g.x, g.y - 2.2f * u); lineTo(g.x + 1.8f * u, g.y); lineTo(g.x, g.y + 2.2f * u); lineTo(g.x - 1.8f * u, g.y); close() },
                        remate, style = Stroke(0.5f * u))
                }
                // Las tres rayas finas a cada lado.
                val rayas = if (e.destacado) jade else oro.copy(alpha = 0.75f)
                for (k in 0 until 3) {
                    val d = (2.4f + 1.8f * k) * u
                    drawLine(rayas, Offset(dentro.left + d, dentro.top + 4 * u), Offset(dentro.left + d, dentro.bottom - 4 * u), 0.7f * u)
                    drawLine(rayas, Offset(dentro.right - d, dentro.top + 4 * u), Offset(dentro.right - d, dentro.bottom - 4 * u), 0.7f * u)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2 * u)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1.5f * u), r.size)
                drawRect(fondo, r.topLeft, r.size)
                if (e.presionado) drawRect(ORO.copy(alpha = 0.08f), r.topLeft, r.size)
                drawRect(oro.copy(alpha = 0.7f), r.topLeft, r.size, style = Stroke(1 * u))
                // La franja de laca (de jade, ya completo) con su friso de zigzag.
                val banda = Rect(r.left, r.top, r.right, r.top + minOf(m * 0.55f, 7.5f * u))
                drawRect(if (e.destacado) mezcla(JADE_LUZ, Color.Black, 0.45f) else if (oscuro) Color(0xFF2C2C33) else LACA, banda.topLeft, banda.size)
                val friso = if (oscuro || e.destacado) ORO_CLARO else ORO
                val paso = maxOf(banda.height * 1.1f, 1f)
                val zig = Path().apply {
                    var x = banda.left + 2 * u; var k = 0
                    moveTo(x, banda.bottom - 1.6f * u)
                    while (x + paso / 2 < banda.right - 2 * u && k < 600) {
                        lineTo(x + paso / 2, banda.top + 1.6f * u); lineTo(x + paso, banda.bottom - 1.6f * u); x += paso; k++
                    }
                }
                drawPath(zig, friso, style = Stroke(0.9f * u, join = StrokeJoin.Miter))
                drawLine(friso, Offset(banda.left, banda.bottom + 0.4f * u), Offset(banda.right, banda.bottom + 0.4f * u), 0.9f * u)
                drawLine(oro.copy(alpha = 0.6f), Offset(banda.left, banda.bottom + 2.2f * u), Offset(banda.right, banda.bottom + 2.2f * u), 0.5f * u)
                // El doble filete al pie y los escalones de las esquinas de abajo.
                drawLine(oro.copy(alpha = 0.6f), Offset(r.left + 6 * u, r.bottom - 3 * u), Offset(r.right - 6 * u, r.bottom - 3 * u), 0.5f * u)
                listOf(r.left to 1f, r.right to -1f).forEach { (x, s) ->
                    drawRect(oro, Offset(if (s > 0) x else x - 4 * u, r.bottom - 2 * u), Size(4 * u, 2 * u))
                    drawRect(oro, Offset(if (s > 0) x else x - 2 * u, r.bottom - 4 * u), Size(2 * u, 2 * u))
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1 * u), r.size)
                drawRect(fondo, r.topLeft, r.size)
                drawRect(oro.copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.8f * u))
                lunaCreciente(Offset(r.right - m * 0.62f, r.top + m * 0.58f), m * 0.34f, if (oscuro) ORO_CLARO else Color(0xFF9AA3B5), fondo)
                // Los rascacielos al pie, con ventanas encendidas; la aguja más alta, hacia un tercio.
                val silueta = if (oscuro) Color(0xFF2A3350) else Color(0xFFB4BBCA)
                val ventana = if (oscuro) ORO_CLARO else Color(0xFFFFF6D8)
                val base = r.bottom - 1 * u
                val tope = m - 2.5f * u
                var x = r.left + 3 * u; var k = 0
                while (x < r.right - 3 * u && k < 200) {
                    val ancho = (5 + 7 * azar(e.semilla, k)) * u
                    val alto = (4.5f * u + (tope - 5.5f * u) * azar(e.semilla, k + 40))
                    val x1 = minOf(x + ancho, r.right - 3 * u)
                    drawRect(silueta, Offset(x, base - alto), Size(x1 - x, alto))
                    var j = 0; var vy = base - alto + 1.6f * u
                    while (vy < base - 1.5f * u && j < 20) {
                        if (azar(e.semilla + j, k) > 0.55f) drawRect(ventana.copy(alpha = 0.8f), Offset(x + 1.2f * u + (x1 - x - 3.2f * u) * azar(e.semilla, k * 7 + j), vy), Size(0.9f * u, 0.9f * u))
                        vy += maxOf(2 * u, 1f); j++
                    }
                    x = x1 + maxOf(0.8f * u, 1f); k++
                }
                // La aguja escalonada y su luz.
                val ax = r.left + r.width * 0.3f
                val aguja = tope - 2 * u
                drawRect(silueta, Offset(ax - 4 * u, base - (aguja - 4 * u)), Size(8 * u, aguja - 4 * u))
                drawRect(silueta, Offset(ax - 2.6f * u, base - (aguja - 2 * u)), Size(5.2f * u, 2 * u))
                drawRect(silueta, Offset(ax - 1.4f * u, base - aguja), Size(2.8f * u, 2 * u))
                val luz = Offset(ax, base - aguja - 0.4f * u)
                drawLine(silueta, Offset(ax, base - aguja), luz, 0.8f * u)
                if (e.destacado) {
                    mancha(luz, 2 * u, ORO_CLARO.copy(alpha = 0.95f))
                    drawCircle(Color(0xFFFFF6D8), 0.9f * u, luz)
                } else drawCircle(ventana.copy(alpha = 0.5f), 0.6f * u, luz)
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                val corte = minOf(m * 0.55f, 7.5f * u)
                if (!e.presionado) {
                    val s = Rect(r.left, r.top + 1.5f * u, r.right, r.bottom + 1.5f * u)
                    drawPath(octagono(s, corte), Color.Black.copy(alpha = 0.25f))
                }
                drawPath(octagono(r, corte), cromo)
                drawPath(octagono(r.deflate(2 * u), corte * 0.75f), if (e.presionado) mezcla(fondo, ORO, if (oscuro) 0.07f else 0.12f) else fondo)
                drawPath(octagono(r.deflate(3.8f * u), corte * 0.55f), oro.copy(alpha = 0.7f), style = Stroke(0.6f * u))
                // Las tres líneas de velocidad de jade, abajo a la derecha.
                listOf(18f, 13f, 8f).forEachIndexed { i, l ->
                    val y = r.bottom - (6 + 2 * i) * u
                    drawLine(jade, Offset(r.right - 7 * u - l * u, y), Offset(r.right - 7 * u, y), 0.9f * u, StrokeCap.Round)
                }
            }
        }
    }
}

/**
 * Botánico: el campo es el frente de un cajón del semillero de roble, como su buscador: la tabla veteada, el portatarjetas
 * de latón con sus dos remaches y la tarjeta crema, lisa, donde se escribe (con renglones de tinta verde si caben varios).
 * Con foco, el cajón se entreabre (la ranura de sombra de arriba se ensancha), el latón brilla y brota una semilla en la
 * esquina; con un error, el portatarjetas se oxida. En modo oscuro, la tarjeta se hunde y la letra es clara.
 */
object ComunBotanico : DisenoComun {
    private val ROBLE = Color(0xFF8A6A42)
    private val ROBLE_LUZ = Color(0xFFA8865A)
    private val ROBLE_VETA = Color(0xFF5E4528)
    private val LATON = Color(0xFFC2A050)
    private val LATON_OSC = Color(0xFF7E6428)
    private val PAPEL = Color(0xFFF1EAD6)
    private val TINTA = Color(0xFF2A2A22)
    private val TINTA_VERDE = Color(0xFF24442E)
    private val HELECHO = Color(0xFF4F7D3E)
    private val HOJA_CLARA = Color(0xFF86A95B)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0A), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFEDE6CF) else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9C9884) else Color(0xFF77735F)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0A070) else Color(0xFF9A3A1A)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        val abierto = e.enfocado && e.habilitado
        // La ranura de sombra de arriba (más ancha con el cajón entreabierto) y el frente de roble.
        val abre = if (abierto) 2.6f * u else 1 * u
        drawRect(Color.Black.copy(alpha = 0.55f), Offset.Zero, Size(w, abre + 1 * u))
        val frente = Rect(0f, abre, w, h - 0.5f * u)
        drawRect(c(ROBLE), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) { vetas(frente, c(ROBLE_VETA).copy(alpha = 0.45f), 131 + e.semilla % 5, densidad = 0.8f, grosor = 0.6f) }
        drawRect(c(ROBLE_LUZ), frente.topLeft, Size(w, 1 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, frente.bottom - 1 * u), Size(w, 1 * u))
        // El portatarjetas de latón: brilla con foco, se oxida con un error.
        val marco = Rect(frente.left + 4 * u, frente.top + 3.5f * u, frente.right - 4 * u, frente.bottom - 3.5f * u)
        val laton = when {
            e.error -> errorCampo(oscuro)
            abierto -> c(mezcla(LATON, Color.White, 0.25f))
            else -> c(LATON)
        }
        drawRect(Color.Black.copy(alpha = 0.3f), marco.topLeft + Offset(0.6f * u, 1 * u), marco.size)
        drawRect(laton, marco.topLeft, marco.size)
        // La tarjeta, lisa, y la sombra del reborde de arriba sobre ella.
        val tarjeta = Rect(marco.left + 2.5f * u, marco.top + 2.2f * u, marco.right - 2.5f * u, marco.bottom - 1.6f * u)
        drawRect(apagar(if (oscuro) Color(0xFF34352C) else PAPEL, e.habilitado), tarjeta.topLeft, tarjeta.size)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(tarjeta.left, tarjeta.top + 0.6f * u), Offset(tarjeta.right, tarjeta.top + 0.6f * u), 1.2f * u)
        drawLine(c(LATON_OSC), Offset(marco.left, marco.top + 1 * u), Offset(marco.right, marco.top + 1 * u), 0.7f * u)
        if (abierto) drawRect(rotuloCampo(oscuro).copy(alpha = 0.8f), tarjeta.deflate(0.7f * u).topLeft, tarjeta.deflate(0.7f * u).size, style = Stroke(1.2f * u))
        if (e.multilinea) renglones(tarjeta.left + 6 * u, tarjeta.right - 6 * u, margenCampoY.toPx(), tarjeta.bottom - 5 * u,
            (if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE).copy(alpha = 0.16f), 0.6f * u)
        // Los remaches de los lados, a media altura.
        listOf(marco.left + 1.25f * u, marco.right - 1.25f * u).forEach { x ->
            drawCircle(c(LATON_OSC), 1 * u, Offset(x, marco.center.y))
            drawCircle(Color.White.copy(alpha = 0.3f), 0.4f * u, Offset(x - 0.3f * u, marco.center.y - 0.3f * u))
        }
        // La semilla que brota en la esquina de abajo a la derecha de la tarjeta.
        if (abierto) {
            val s = Offset(tarjeta.right - 8 * u, tarjeta.bottom - 2.4f * u)
            scale(1.4f, s) {
            drawOval(c(Color(0xFF8A6A3A)), Offset(s.x - 2.2f * u, s.y - 1.4f * u), Size(4.4f * u, 2.8f * u))
            drawLine(c(HELECHO), Offset(s.x, s.y - 1 * u), Offset(s.x + 1 * u, s.y - 3.4f * u), 0.8f * u)
            listOf(-1f, 1f).forEach { lado ->
                val base = Offset(s.x + 1 * u, s.y - 3.2f * u)
                drawPath(Path().apply {
                    moveTo(base.x, base.y)
                    quadraticTo(base.x + lado * 2.4f * u, base.y - 2.6f * u, base.x + lado * 4.4f * u, base.y - 0.8f * u)
                    quadraticTo(base.x + lado * 2.2f * u, base.y + 0.4f * u, base.x, base.y); close()
                }, c(HOJA_CLARA))
            }
            }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 14.dp

    /**
     * La rama del herbario dibujada a tinta: el tallo fino de lado a lado, con una semilla en cada punta y, al centro, una
     * ramita de tres hojas y dos hojas chicas a sus lados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2 + 1.5f * u
        val f = fuerza(e)
        val tinta = (if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE).copy(alpha = (if (oscuro) 0.5f else 0.45f) * f)
        val hoja = (if (oscuro) t(HOJA_CLARA, true) else HELECHO).copy(alpha = 0.8f * f)
        drawLine(tinta, Offset(7 * u, cy), Offset(w - 7 * u, cy), 0.9f * u, StrokeCap.Round)
        drawCircle(tinta, 1.3f * u, Offset(5.5f * u, cy)); drawCircle(tinta, 1.3f * u, Offset(w - 5.5f * u, cy))
        // Una hoja: dos curvas desde la base hasta la punta, girada hacia [ang] (en grados, de la derecha y hacia abajo).
        fun hojita(base: Offset, ang: Float, largo: Float) = rotate(ang, base) {
            drawPath(Path().apply {
                moveTo(base.x, base.y)
                quadraticTo(base.x + largo * 0.5f, base.y - largo * 0.36f, base.x + largo, base.y)
                quadraticTo(base.x + largo * 0.5f, base.y + largo * 0.36f, base.x, base.y); close()
            }, hoja)
        }
        hojita(Offset(cx - 0.6f * u, cy), -148f, 6.6f * u)
        hojita(Offset(cx + 0.6f * u, cy), -32f, 6.6f * u)
        hojita(Offset(cx, cy), -90f, 5.2f * u)
        hojita(Offset(cx - 11 * u, cy), -155f, 4.4f * u)
        hojita(Offset(cx + 11 * u, cy), -25f, 4.4f * u)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFA9C27A) else Color(0xFF3F5A2C)

    /**
     * El subrayado a tinta del naturalista: una raya fina bajo el texto que, a la derecha, se enrosca en un zarcillo. Al
     * tocarlo, el papel se tiñe de un verde de hoja muy tenue y a la raya le brota una hojita.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        if (toque) drawRoundRect(HOJA_CLARA.copy(alpha = if (oscuro) 0.16f else 0.22f), Offset(3 * u, cy - 15 * u), Size(w - 6 * u, 30 * u), CornerRadius(10 * u))
        val tinta = apagarBoton((if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE).copy(alpha = if (toque) 1f else 0.7f), e.habilitado)
        val y = minOf(cy + 11.5f * u, h - 6 * u)
        val x0 = 9 * u; val fin = w - 14 * u
        if (fin > x0) {
            val trazo = Path().apply {
                moveTo(x0, y)
                quadraticTo((x0 + fin) / 2, y + 0.8f * u, fin, y)
                cubicTo(fin + 4 * u, y - 0.2f * u, fin + 5.2f * u, y - 4 * u, fin + 2.6f * u, y - 5 * u)
                cubicTo(fin + 0.6f * u, y - 5.6f * u, fin - 0.2f * u, y - 3 * u, fin + 1.8f * u, y - 2.6f * u)
            }
            drawPath(trazo, tinta, style = Stroke(1 * u, cap = StrokeCap.Round))
            if (toque) {
                val b = Offset(x0 + minOf(10 * u, (fin - x0) * 0.3f), y + 0.3f * u)
                rotate(-130f, b) {
                    drawPath(Path().apply {
                        moveTo(b.x, b.y); quadraticTo(b.x + 3 * u, b.y - 2.4f * u, b.x + 6 * u, b.y)
                        quadraticTo(b.x + 3 * u, b.y + 2.4f * u, b.x, b.y); close()
                    }, if (oscuro) t(HOJA_CLARA, true) else HELECHO)
                }
            }
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un renglón del índice del herbario: la raya de tinta verde muy tenue al pie y, a la izquierda, una hojita. Al tocarla,
     * el papel se tiñe de un verde de hoja tenue y la hojita se aviva.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        if (e.presionado) drawRect(HOJA_CLARA.copy(alpha = if (oscuro) 0.12f else 0.16f), Offset.Zero, size)
        val tinta = if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE
        drawLine(tinta.copy(alpha = if (oscuro) 0.2f else 0.22f), Offset(margenFila.toPx(), h - 0.8f * u), Offset(w, h - 0.8f * u), 0.8f * u, StrokeCap.Round)
        val b = Offset(4 * u, cy + 2.5f * u)
        rotate(-45f, b) {
            drawPath(Path().apply {
                moveTo(b.x, b.y); quadraticTo(b.x + 3.5f * u, b.y - 2.6f * u, b.x + 7 * u, b.y); quadraticTo(b.x + 3.5f * u, b.y + 2.6f * u, b.x, b.y); close()
            }, (if (oscuro) t(HOJA_CLARA, true) else HELECHO).copy(alpha = if (e.presionado) 1f else 0.55f))
        }
    }

    /** Una hoja que apunta hacia adelante, con su nervio y su pecíolo. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val color = if (oscuro) Color(0xFFA9C27A) else Color(0xFF3F5A2C)
        drawLine(color, Offset(1.5f * u, cy + 1 * u), Offset(5 * u, cy), 1.1f * u, StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(4.5f * u, cy); quadraticTo(10 * u, cy - 6.5f * u, 18 * u, cy); quadraticTo(10 * u, cy + 6.5f * u, 4.5f * u, cy); close()
        }, color)
        drawLine(if (oscuro) Color(0xFF1C1912) else Color(0xFFF7F2E4), Offset(6 * u, cy), Offset(15.5f * u, cy), 0.7f * u, StrokeCap.Round)
    }

    /** Una flor prensada de cinco pétalos: llena, terracota con centro de latón, si la fila está fija; de contorno tenue si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val petalo = if (oscuro) Color(0xFFE09A5E) else Color(0xFF8C4A22)
        val tenue = (if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE).copy(alpha = 0.5f)
        for (k in 0 until 5) {
            rotate(72f * k - 90f, c) {
                val o = Offset(c.x + 2.2f * u, c.y - 2.6f * u)
                if (fija) drawOval(petalo, o, Size(7.2f * u, 5.2f * u))
                else drawOval(tenue, o, Size(7.2f * u, 5.2f * u), style = Stroke(1 * u))
            }
        }
        if (fija) {
            drawCircle(t(LATON, oscuro), 2.4f * u, c)
            drawCircle(t(LATON_OSC, oscuro), 2.4f * u, c, style = Stroke(0.6f * u))
        } else drawCircle(tenue, 1.6f * u, c, style = Stroke(1 * u))
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFBDB196) else Color(0xFF574D3B)

    /**
     * La hoja de herbario sin espécimen: el papel claro con su sombra, dos tiras de cinta de goma en las esquinas de arriba y,
     * abajo a la derecha, el recuadro de la etiqueta, en tinta verde tenue.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val hoja = Rect(2 * u, 2 * u, w - 2 * u, h - 3 * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(hoja.left + 1 * u, hoja.top + 1.5f * u), hoja.size)
        drawRect(if (oscuro) Color(0xFF221F17) else Color(0xFFF9F5EA), hoja.topLeft, hoja.size)
        drawRect((if (oscuro) Color.White else ROBLE_VETA).copy(alpha = 0.1f), hoja.topLeft, hoja.size, style = Stroke(0.6f * u))
        // Las tiras de cinta, cruzadas sobre las esquinas.
        val cinta = (if (oscuro) Color(0xFF8A7E5A) else Color(0xFFE8D9A8)).copy(alpha = 0.7f)
        listOf(hoja.topLeft + Offset(8 * u, 4 * u) to -40f, hoja.topRight + Offset(-8 * u, 4 * u) to 40f).forEach { (c, giro) ->
            rotate(giro, c) { drawRect(cinta, Offset(c.x - 8 * u, c.y - 2.5f * u), Size(16 * u, 5 * u)) }
        }
        // El recuadro de la etiqueta, abajo a la derecha.
        val tinta = (if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE).copy(alpha = 0.3f)
        val et = Rect(hoja.right - 44 * u, hoja.bottom - 16 * u, hoja.right - 5 * u, hoja.bottom - 5 * u)
        if (et.left > hoja.left + 20 * u) {
            drawRect(tinta, et.topLeft, et.size, style = Stroke(0.7f * u))
            drawLine(tinta, Offset(et.left + 3 * u, et.center.y + 1 * u), Offset(et.right - 3 * u, et.center.y + 1 * u), 0.5f * u)
        }
    }

    /** El frasco de especímenes vacío: el vidrio con su brillo, el corcho, la etiqueta en blanco y una sola hoja seca en el fondo. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val linea = if (oscuro) Color(0xFFB8AC90) else Color(0xFF5E5340)
        val vidrio = Rect(f(0.22f), f(0.26f), f(0.78f), f(0.94f))
        drawRoundRect((if (oscuro) Color(0xFF6FC2B0) else Color(0xFF2E5E5A)).copy(alpha = 0.1f), vidrio.topLeft, vidrio.size, CornerRadius(f(0.1f)))
        drawRoundRect(linea, vidrio.topLeft, vidrio.size, CornerRadius(f(0.1f)), style = Stroke(1.4f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.85f), Offset(f(0.3f), f(0.36f)), Offset(f(0.3f), f(0.62f)), 1.4f * u, StrokeCap.Round)
        // El corcho.
        val corcho = Rect(f(0.32f), f(0.1f), f(0.68f), f(0.27f))
        drawRoundRect(t(Color(0xFFB08A5A), oscuro), corcho.topLeft, corcho.size, CornerRadius(1.5f * u))
        listOf(0.16f, 0.21f).forEach { y -> drawLine(t(ROBLE_VETA, oscuro).copy(alpha = 0.6f), Offset(corcho.left + 1.5f * u, f(y)), Offset(corcho.right - 1.5f * u, f(y)), 0.6f * u) }
        // La etiqueta en blanco.
        val et = Rect(f(0.33f), f(0.48f), f(0.67f), f(0.66f))
        drawRect(if (oscuro) Color(0xFF3A372C) else PAPEL, et.topLeft, et.size)
        drawRect((if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE).copy(alpha = 0.7f), et.topLeft, et.size, style = Stroke(0.8f * u))
        // La hoja seca en el fondo.
        val b = Offset(f(0.42f), f(0.88f))
        rotate(-12f, b) {
            drawPath(Path().apply { moveTo(b.x, b.y); quadraticTo(b.x + f(0.1f), b.y - f(0.08f), b.x + f(0.22f), b.y); quadraticTo(b.x + f(0.1f), b.y + f(0.05f), b.x, b.y); close() },
                t(Color(0xFFA0763A), oscuro))
        }
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFEDE6CF) else TINTA

    /**
     * La nota del naturalista prendida a la tarjeta: el papel con su filete verde a la izquierda y, en el margen, una fronda
     * de helecho dibujada a tinta, con sus foliolos alternos que se achican hacia la punta.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) Color(0xFF26231B) else Color(0xFFF8F3E6), Offset.Zero, size)
        val tinta = if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE
        drawLine(tinta.copy(alpha = 0.6f), Offset(1 * u, 0f), Offset(1 * u, h), 1.2f * u)
        drawLine((if (oscuro) Color.White else ROBLE_VETA).copy(alpha = 0.12f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u)
        // La fronda: el raquis y los foliolos, de abajo hacia la punta.
        val x = 14 * u
        val arriba = 6 * u; val abajo = minOf(h - 6 * u, 46 * u)
        val hoja = (if (oscuro) t(HOJA_CLARA, true) else HELECHO)
        drawLine(hoja, Offset(x, abajo), Offset(x + 1.5f * u, arriba), 1 * u, StrokeCap.Round)
        val paso = maxOf(4 * u, 1f)
        var y = abajo - 2 * u; var k = 0
        while (y > arriba + 2 * u && k < 20) {
            val f = (y - arriba) / maxOf(abajo - arriba, 1f)
            val largo = (3 + 4 * f) * u
            val lado = if (k % 2 == 0) 1f else -1f
            val bx = x + 1.5f * u * (1 - f)
            rotate(if (lado > 0) -35f else -145f, Offset(bx, y)) {
                drawOval(hoja, Offset(bx, y - largo * 0.22f), Size(largo, largo * 0.44f))
            }
            y -= paso; k++
        }
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFEDE6CF) else TINTA
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE

    /**
     * El portatarjetas de un cajón del semillero: el marco de latón con su remache a cada lado y su muesca abajo, y la
     * tarjeta crema lisa (de noche, hundida) donde va el número.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val marco = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(marco.left + 0.5f * u, marco.top + 1.2f * u), marco.size)
        drawRect(t(LATON, oscuro), marco.topLeft, marco.size)
        drawRect(t(LATON_OSC, oscuro), marco.topLeft, marco.size, style = Stroke(0.7f * u))
        val tarjeta = Rect(marco.left + 3 * u, marco.top + 3 * u, marco.right - 3 * u, marco.bottom - 3 * u)
        drawRect(if (oscuro) Color(0xFF34352C) else PAPEL, tarjeta.topLeft, tarjeta.size)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(tarjeta.left, tarjeta.top + 0.6f * u), Offset(tarjeta.right, tarjeta.top + 0.6f * u), 1.2f * u)
        listOf(marco.left + 1.5f * u, marco.right - 1.5f * u).forEach { x ->
            drawCircle(t(LATON_OSC, oscuro), 0.9f * u, Offset(x, marco.center.y))
        }
        drawArc(t(LATON_OSC, oscuro), 180f, 180f, true, Offset(marco.center.x - 3 * u, marco.bottom - 2.4f * u), Size(6 * u, 4.8f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: la hoja del herbario, el sobre de semillas, el invernadero de noche, la libreta. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF26271F) else Color(0xFFFBF8EE)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF2A2619) else Color(0xFFF4ECD6)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0F170F) else Color(0xFFDCE3D4)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF25241C) else Color(0xFFF3EDDB)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFEDE6CF) else TINTA
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFB8AC90) else Color(0xFF5E5340)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFFE0A070) else Color(0xFF8A3B1E)
        else -> if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE
    }

    /** Una hoja simple de [l] de largo, girada [giro] grados, con su nervio. */
    private fun DrawScope.hojita(c: Offset, l: Float, giro: Float, color: Color, nervio: Color) {
        rotate(giro, c) {
            drawPath(Path().apply {
                moveTo(c.x - l / 2, c.y); quadraticTo(c.x, c.y - l * 0.42f, c.x + l / 2, c.y); quadraticTo(c.x, c.y + l * 0.42f, c.x - l / 2, c.y); close()
            }, color)
            drawLine(nervio, Offset(c.x - l / 2, c.y), Offset(c.x + l * 0.4f, c.y), l * 0.06f)
        }
    }

    /**
     * Las cuatro son del gabinete del naturalista. La cifra del tablero (METRICA) es una hoja del herbario: el papel liso
     * sujeto por cuatro esquineras de papel kraft y, abajo a la derecha, la etiqueta del ejemplar; si la cifra se aparta de tu
     * normal, aparece una hoja prensada en la esquina de arriba. Lo que te falta hoy (PENDIENTE) es un sobre de semillas: la
     * solapa de kraft con su orilla dentada y su ojal de latón, el cordel verde atado a la izquierda y una hilera de semillas
     * dibujadas al pie; completo, la solapa se vuelve verde helecho con su marca. La noche (NOCHE) es el invernadero de noche:
     * la armazón de hierro con sus arcos arriba y luciérnagas en la orilla; sellado el día, se abre una flor de luna en la
     * esquina. Las de Vida (VIDA) son páginas de la libreta de campo: el lomo de tela verde cosido a la izquierda y la liga
     * negra a la derecha. Al tocarlas, se hunden y el papel toma un tinte verde.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val tinte = if (e.presionado) mezcla(fondo, HOJA_CLARA, if (oscuro) 0.1f else 0.14f) else fondo
        val verde = if (oscuro) Color(0xFF9BC06C) else HELECHO
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(r.left + 0.6f * u, r.top + 1.5f * u), r.size)
                drawRect(tinte, r.topLeft, r.size)
                drawRect((if (oscuro) Color.White else ROBLE_VETA).copy(alpha = 0.15f), r.topLeft, r.size, style = Stroke(0.6f * u))
                // Las cuatro esquineras de kraft.
                val lado = minOf(m * 0.85f, 12 * u)
                val kraft = if (oscuro) t(ROBLE, true) else Color(0xFFC9AE7C)
                listOf(r.topLeft to Offset(1f, 1f), r.topRight to Offset(-1f, 1f), r.bottomLeft to Offset(1f, -1f), r.bottomRight to Offset(-1f, -1f)).forEach { (p, s) ->
                    val tri = Path().apply { moveTo(p.x, p.y); lineTo(p.x + s.x * lado, p.y); lineTo(p.x, p.y + s.y * lado); close() }
                    drawPath(tri, kraft.copy(alpha = 0.9f))
                    drawLine(Color.Black.copy(alpha = 0.18f), Offset(p.x + s.x * lado, p.y), Offset(p.x, p.y + s.y * lado), 0.6f * u)
                }
                // La etiqueta del ejemplar, abajo a la derecha.
                val et = Rect(r.right - lado - 18 * u, r.bottom - 8.5f * u, r.right - lado - 2 * u, r.bottom - 3 * u)
                if (et.left > r.left + lado) {
                    val tinta = (if (oscuro) Color(0xFFB8AC90) else TINTA).copy(alpha = 0.4f)
                    drawRect(tinta, et.topLeft, et.size, style = Stroke(0.6f * u))
                    drawLine(tinta, Offset(et.left + 2 * u, et.center.y), Offset(et.right - 4 * u, et.center.y), 0.6f * u)
                }
                if (e.destacado) {
                    hojita(Offset(r.right - lado * 0.62f, r.top + lado * 0.6f), lado * 1.05f, -40f, verde, if (oscuro) Color(0xFF26271F) else TINTA_VERDE)
                    drawLine(verde, Offset(r.right - lado * 0.2f, r.top + lado * 1.05f), Offset(r.right - lado * 0.5f, r.top + lado * 0.75f), 0.8f * u)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2 * u)
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(r.left + 0.6f * u, r.top + 1.5f * u), r.size)
                drawRect(tinte, r.topLeft, r.size)
                // La solapa de kraft (verde helecho, ya completo) con su orilla dentada.
                val solapa = minOf(m * 0.5f, 7 * u)
                val color = if (e.destacado) t(HELECHO, oscuro) else if (oscuro) Color(0xFF7A5E36) else Color(0xFFC49A5E)
                val paso = maxOf(4 * u, 1f)
                val forma = Path().apply {
                    moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + solapa)
                    var x = r.right; var k = 0
                    while (x - paso > r.left && k < 600) { lineTo(x - paso / 2, r.top + solapa + 1.8f * u); lineTo(x - paso, r.top + solapa); x -= paso; k++ }
                    lineTo(r.left, r.top + solapa); close()
                }
                drawPath(forma, Color.Black.copy(alpha = 0.18f), style = Stroke(1.2f * u))
                drawPath(forma, color)
                // El ojal de latón de la solapa y, ya completo, la marca.
                val ojal = Offset(r.right - 7 * u, r.top + solapa / 2)
                drawCircle(t(LATON, oscuro), 1.8f * u, ojal)
                drawCircle(t(LATON_OSC, oscuro), 0.8f * u, ojal)
                if (e.destacado) drawPath(Path().apply {
                    moveTo(ojal.x - 9 * u, ojal.y); lineTo(ojal.x - 7.4f * u, ojal.y + 1.8f * u); lineTo(ojal.x - 4.4f * u, ojal.y - 2 * u)
                }, Color(0xFFF4ECD6), style = Stroke(1.1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                // El cordel verde: baja del doblez por la izquierda y se ata en un moño chico.
                val cordel = if (oscuro) Color(0xFF7FA05A) else TINTA_VERDE
                val cx = r.left + 4.5f * u
                drawLine(cordel, Offset(cx, r.top), Offset(cx, r.bottom), 0.9f * u)
                val nudo = Offset(cx, r.top + solapa + 4 * u)
                listOf(-1f, 1f).forEach { s ->
                    rotate(s * 35f, nudo) { drawOval(cordel, Offset(nudo.x - 1.2f * u + s * 1.6f * u, nudo.y - 1.1f * u), Size(2.6f * u, 2.2f * u), style = Stroke(0.7f * u)) }
                }
                drawCircle(cordel, 0.9f * u, nudo)
                // Las semillas dibujadas al pie.
                val semilla = (if (oscuro) Color(0xFFB8AC90) else ROBLE_VETA).copy(alpha = 0.45f)
                var x = r.left + 12 * u; var k = 0
                while (x < r.right - 6 * u && k < 300) {
                    rotate(-30f + 60f * azar(e.semilla, k), Offset(x, r.bottom - 3.6f * u)) { drawOval(semilla, Offset(x - 1.3f * u, r.bottom - 4.4f * u), Size(2.6f * u, 1.6f * u)) }
                    x += maxOf(9 * u, 1f); k++
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1 * u), r.size)
                drawRect(fondo, r.topLeft, r.size)
                drawRect((if (oscuro) HOJA_CLARA else ROBLE_VETA).copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(1 * u))
                // La armazón del invernadero arriba: el larguero, los montantes y un arco entre cada dos.
                val fierro = if (oscuro) Color(0xFF9DB08A).copy(alpha = 0.4f) else Color(0xFF4E5A4A).copy(alpha = 0.5f)
                val yl = r.top + m * 0.62f
                drawLine(fierro, Offset(r.left + 3 * u, yl), Offset(r.right - 3 * u, yl), 0.9f * u)
                val paso = maxOf(24 * u, 1f)
                var x = r.left + 3 * u; var k = 0
                while (x <= r.right - 3 * u && k < 100) {
                    drawLine(fierro, Offset(x, r.top + 0.5f * u), Offset(x, yl), 0.7f * u)
                    if (x + paso <= r.right - 3 * u) drawArc(fierro, 180f, 180f, false, Offset(x, yl - 3.5f * u), Size(paso, 7 * u), style = Stroke(0.6f * u))
                    x += paso; k++
                }
                // Las luciérnagas en la orilla, con su resplandor chico.
                val luz = if (oscuro) Color(0xFFFFE08A) else Color(0xFFB08A1E)
                val n = ((w + h) / (36 * u)).toInt().coerceIn(4, 16)
                puntosMargen(w, h, m, n, e.semilla + 13, 3.5f * u).forEach { (p, b) ->
                    if (p.y > yl + 2 * u && !(p.x > r.right - m * 1.4f && p.y > r.bottom - m * 1.4f)) {
                        drawCircle(luz.copy(alpha = if (oscuro) 0.22f else 0.18f), (1.8f + b) * u, p)
                        drawCircle(luz, (0.6f + 0.3f * b) * u, p)
                    }
                }
                // Sellado: la flor de luna abierta en la esquina de abajo a la derecha.
                if (e.destacado) {
                    val c = Offset(r.right - m * 0.62f, r.bottom - m * 0.62f)
                    hojita(Offset(c.x - 3.6f * u, c.y + 2.2f * u), 5 * u, 20f, verde, Color.Black.copy(alpha = 0.3f))
                    val petalo = if (oscuro) Color(0xFFF4F1E6) else Color.White
                    for (j in 0 until 5) {
                        rotate(72f * j - 90f, c) { drawOval(petalo, Offset(c.x, c.y - 1.4f * u), Size(4.4f * u, 2.8f * u)) }
                    }
                    for (j in 0 until 5) {
                        rotate(72f * j - 90f, c) { drawOval(Color.Black.copy(alpha = 0.15f), Offset(c.x, c.y - 1.4f * u), Size(4.4f * u, 2.8f * u), style = Stroke(0.4f * u)) }
                    }
                    drawCircle(t(LATON, oscuro), 1.1f * u, c)
                }
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(r.left + 0.6f * u, r.top + 1.5f * u), r.size, CornerRadius(3 * u))
                drawRoundRect(tinte, r.topLeft, r.size, CornerRadius(3 * u))
                // Las hojas de la libreta, al canto de abajo.
                drawLine(Color.Black.copy(alpha = 0.15f), Offset(r.left + 8 * u, r.bottom - 1.4f * u), Offset(r.right - 3 * u, r.bottom - 1.4f * u), 0.5f * u)
                // El lomo de tela verde a la izquierda, con su costura.
                val lomo = minOf(m * 0.5f, 7 * u)
                clipRect(r.left, r.top, r.left + lomo, r.bottom) { drawRoundRect(t(HELECHO, oscuro), r.topLeft, r.size, CornerRadius(3 * u)) }
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(r.left + lomo, r.top), Offset(r.left + lomo, r.bottom), 0.6f * u)
                drawLine(Color(0xFFF1EAD6).copy(alpha = 0.6f), Offset(r.left + lomo - 2 * u, r.top + 2 * u), Offset(r.left + lomo - 2 * u, r.bottom - 2 * u), 0.6f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
                // La liga negra a la derecha.
                drawRect(if (oscuro) Color(0xFF4A4A3E) else Color(0xFF2A2A22).copy(alpha = 0.8f), Offset(r.right - m * 0.55f, r.top), Size(2.4f * u, r.height))
                drawLine(Color.White.copy(alpha = 0.15f), Offset(r.right - m * 0.55f + 0.6f * u, r.top), Offset(r.right - m * 0.55f + 0.6f * u, r.bottom), 0.5f * u)
            }
        }
    }
}

/**
 * Navidad: el campo es un cristal de la ventana en invierno, como su buscador: el marco de madera pintada (crema de día,
 * nogal en modo oscuro), la escarcha en las orillas y una ramita de acebo con sus bayas en la esquina de arriba; el centro
 * del cristal queda limpio para escribir. Con foco, la escarcha se retira y el cristal toma la luz tibia de una vela, con
 * su orilla encendida; con un error, la orilla se vuelve roja. En modo oscuro, afuera es de noche, azul hondo.
 */
object ComunNavidad : DisenoComun {
    private val NOGAL = Color(0xFF6A4528)
    private val NOGAL_LUZ = Color(0xFF8A603A)
    private val CREMA = Color(0xFFF3E8D2)
    private val ABETO = Color(0xFF2E5A3A)
    private val VERDE = Color(0xFF2F6A45)
    private val ROJO = Color(0xFFB3262E)
    private val NIEVE = Color(0xFFF4F6FA)
    private val CIELO_DIA = Color(0xFFBFD0DD)
    private val CIELO_NOCHE = Color(0xFF1A2440)
    private val TINTA = Color(0xFF3A1E18)
    private val LLAMA = Color(0xFFFFD27A)
    private val ORO = Color(0xFFD9B04F)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C0A), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) NIEVE else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8E98B0) else Color(0xFF66625E)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF86C99A) else ABETO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8A80) else Color(0xFF8E1C22)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        val vela = e.enfocado && e.habilitado
        // El marco de madera pintada, con su canto.
        drawRoundRect(c(if (oscuro) NOGAL else CREMA), Offset.Zero, size, CornerRadius(2 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.18f), Offset.Zero, size, CornerRadius(2 * u), style = Stroke(1 * u))
        drawLine(c(if (oscuro) NOGAL_LUZ else Color.White).copy(alpha = 0.6f), Offset(2 * u, 1.2f * u), Offset(w - 2 * u, 1.2f * u), 0.8f * u)
        // El cristal: el cielo pálido de día, azul hondo de noche; tibio con la vela.
        val vidrio = Rect(4.5f * u, 4.5f * u, w - 4.5f * u, h - 4.5f * u)
        drawRect(apagar(if (oscuro) CIELO_NOCHE else mezcla(CIELO_DIA, Color.White, 0.15f), e.habilitado), vidrio.topLeft, vidrio.size)
        if (vela) drawRect(LLAMA.copy(alpha = if (oscuro) 0.08f else 0.07f), vidrio.topLeft, vidrio.size)
        drawLine(Color.Black.copy(alpha = 0.18f), Offset(vidrio.left, vidrio.top + 0.6f * u), Offset(vidrio.right, vidrio.top + 0.6f * u), 1.2f * u)
        // La escarcha de las orillas: manchones y agujas, que no pasan de 4 dp hacia dentro.
        val escarcha = NIEVE.copy(alpha = if (oscuro) 0.4f else 0.85f)
        val hondo = if (vela) 0.55f else 1f
        clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
            val perimetro = 2 * (vidrio.width + vidrio.height)
            val cuantos = (perimetro / (6 * u)).toInt().coerceIn(8, 160)
            for (i in 0 until cuantos) {
                val f = (i + azar(173 + e.semilla % 5, i)) / cuantos * perimetro
                val p = when {
                    f < vidrio.width -> Offset(vidrio.left + f, vidrio.top)
                    f < vidrio.width + vidrio.height -> Offset(vidrio.right, vidrio.top + f - vidrio.width)
                    f < 2 * vidrio.width + vidrio.height -> Offset(vidrio.right - (f - vidrio.width - vidrio.height), vidrio.bottom)
                    else -> Offset(vidrio.left, vidrio.bottom - (f - 2 * vidrio.width - vidrio.height))
                }
                drawCircle(escarcha.copy(alpha = escarcha.alpha * 0.65f), (1.4f + 2.2f * azar(174, i)) * u * hondo, p)
                if (i % 3 == 0) {
                    val adentro = Offset(vidrio.center.x - p.x, vidrio.center.y - p.y)
                    val largo = maxOf(kotlin.math.hypot(adentro.x, adentro.y), 1f)
                    val l = (2.5f + 1.5f * azar(176, i)) * u * hondo
                    drawLine(escarcha, p, Offset(p.x + adentro.x / largo * l + 1.2f * u, p.y + adentro.y / largo * l), 0.6f * u)
                }
            }
        }
        // La orilla del cristal: encendida de vela con foco, roja con un error.
        when {
            e.error -> drawRect(errorCampo(oscuro), vidrio.topLeft, vidrio.size, style = Stroke(1.6f * u))
            vela -> {
                drawRect(c(if (oscuro) LLAMA else ORO), vidrio.topLeft, vidrio.size, style = Stroke(1.4f * u))
                drawRect(LLAMA.copy(alpha = 0.25f), vidrio.inflate(1.6f * u).topLeft, vidrio.inflate(1.6f * u).size, style = Stroke(1.6f * u))
            }
        }
        // La ramita de acebo en la esquina de arriba a la izquierda: dos hojas y tres bayas.
        val a = Offset(8 * u, 7.5f * u)
        listOf(-30f to c(ABETO), 60f to c(VERDE), 195f to c(ABETO)).forEach { (giro, color) ->
            rotate(giro, a) { scale(1.15f, a) {
                val hoja = Path().apply {
                    moveTo(a.x, a.y)
                    quadraticTo(a.x + 3 * u, a.y - 3 * u, a.x + 6.5f * u, a.y - 0.6f * u)
                    lineTo(a.x + 5.2f * u, a.y + 0.4f * u); lineTo(a.x + 4.4f * u, a.y + 1.8f * u); lineTo(a.x + 3 * u, a.y + 1.4f * u)
                    lineTo(a.x + 1.8f * u, a.y + 2.2f * u); close()
                }
                drawPath(hoja, color)
                drawLine(Color.Black.copy(alpha = 0.25f), a, Offset(a.x + 6 * u, a.y - 0.4f * u), 0.4f * u)
            } }
        }
        listOf(Offset(-0.6f, 0.2f), Offset(1.4f, 1.2f), Offset(0.2f, 2.2f)).forEach { d ->
            val b = Offset(a.x + d.x * u, a.y + d.y * u)
            drawCircle(c(ROJO), 1.5f * u, b)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.4f * u, Offset(b.x - 0.4f * u, b.y - 0.4f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /** Un hilo verde de pino con un copo de nieve al centro y una baya de acebo a cada lado. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val hilo = (if (oscuro) Color(0xFF86C99A) else ABETO).copy(alpha = (if (oscuro) 0.4f else 0.45f) * f)
        val copo = (if (oscuro) NIEVE.copy(alpha = 0.75f) else Color(0xFF5E7E9A)).let { it.copy(alpha = it.alpha * f) }
        val hueco = 15 * u
        drawLine(hilo, Offset(6 * u, cy), Offset(cx - hueco, cy), 1 * u, StrokeCap.Round)
        drawLine(hilo, Offset(cx + hueco, cy), Offset(w - 6 * u, cy), 1 * u, StrokeCap.Round)
        listOf(-1f, 1f).forEach { s ->
            val b = Offset(cx + s * 11 * u, cy)
            drawCircle((if (oscuro) Color(0xFFE5484D) else ROJO).copy(alpha = 0.85f * f), 1.6f * u, b)
            drawCircle(Color.White.copy(alpha = 0.45f * f), 0.45f * u, Offset(b.x - 0.45f * u, b.y - 0.45f * u))
        }
        // El copo: tres brazos que se cruzan y una V en cada medio brazo.
        val r = 4.4f * u
        val c = Offset(cx, cy)
        for (k in 0 until 3) {
            val a = PI.toFloat() / 3 * k + PI.toFloat() / 2
            drawLine(copo, Offset(c.x - cos(a) * r, c.y - sin(a) * r), Offset(c.x + cos(a) * r, c.y + sin(a) * r), 0.8f * u)
        }
        for (k in 0 until 6) {
            val a = PI.toFloat() / 3 * k + PI.toFloat() / 2
            val p = Offset(c.x + cos(a) * r * 0.55f, c.y + sin(a) * r * 0.55f)
            listOf(-0.75f, 0.75f).forEach { d -> drawLine(copo, p, Offset(p.x + cos(a + d) * 1.7f * u, p.y + sin(a + d) * 1.7f * u), 0.6f * u) }
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF6F6B) else Color(0xFFB71C1C)

    /**
     * La cinta roja de un regalo bajo el texto, con su moño chico en la punta de la derecha. Al tocarlo, se enciende detrás
     * la luz tibia de una vela y la cinta se aviva.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        if (toque) drawRoundRect(LLAMA.copy(alpha = if (oscuro) 0.12f else 0.28f), Offset(3 * u, cy - 15 * u), Size(w - 6 * u, 30 * u), CornerRadius(8 * u))
        val cinta = apagarBoton((if (oscuro) Color(0xFFE5484D) else ROJO).copy(alpha = if (toque) 1f else 0.75f), e.habilitado)
        val y = minOf(cy + 11.5f * u, h - 6 * u)
        val x0 = 10 * u; val nudo = w - 12 * u
        if (nudo > x0 + 4 * u) {
            drawLine(cinta, Offset(x0, y), Offset(nudo, y), 1.5f * u, StrokeCap.Round)
            // El moño: dos lazos, dos puntas y el nudo.
            listOf(-1f, 1f).forEach { s ->
                val lazo = Path().apply {
                    moveTo(nudo, y)
                    cubicTo(nudo + s * 0.6f * u, y - 5.2f * u, nudo + s * 6.4f * u, y - 5 * u, nudo + s * 5.2f * u, y - 1.2f * u)
                    cubicTo(nudo + s * 4.6f * u, y + 0.4f * u, nudo + s * 2 * u, y + 0.2f * u, nudo, y); close()
                }
                drawPath(lazo, cinta.copy(alpha = cinta.alpha * 0.3f))
                drawPath(lazo, cinta, style = Stroke(1 * u))
                drawLine(cinta, Offset(nudo, y), Offset(nudo + s * 1.8f * u, y + 4.2f * u), 1.1f * u, StrokeCap.Round)
            }
            drawCircle(cinta, 1.2f * u, Offset(nudo, y))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un tramo de la guirnalda: el hilo verde de pino muy tenue al pie y, a la izquierda, una baya de acebo. Al tocarla, se
     * enciende detrás la luz tibia de una vela y a la baya le sale su hojita.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        if (e.presionado) drawRect(LLAMA.copy(alpha = if (oscuro) 0.1f else 0.22f), Offset.Zero, size)
        drawLine((if (oscuro) Color(0xFF86C99A) else ABETO).copy(alpha = if (oscuro) 0.2f else 0.22f), Offset(margenFila.toPx(), h - 0.8f * u), Offset(w, h - 0.8f * u), 1 * u, StrokeCap.Round)
        val b = Offset(6.5f * u, cy + 1 * u)
        if (e.presionado) {
            rotate(-35f, b) {
                drawPath(Path().apply { moveTo(b.x, b.y); quadraticTo(b.x + 2 * u, b.y - 4 * u, b.x + 1 * u, b.y - 7.5f * u); quadraticTo(b.x - 1.6f * u, b.y - 4 * u, b.x, b.y); close() },
                    if (oscuro) Color(0xFF66BB6A) else VERDE)
            }
        }
        val rojo = if (oscuro) Color(0xFFE5484D) else ROJO
        drawCircle(rojo.copy(alpha = if (e.presionado) 1f else 0.75f), 2 * u, b)
        drawCircle(Color.White.copy(alpha = 0.5f), 0.55f * u, Offset(b.x - 0.6f * u, b.y - 0.6f * u))
    }

    /** Un galón de ramita de pino hacia adelante, con una baya en la punta. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val verde = if (oscuro) Color(0xFF86C99A) else ABETO
        drawPath(Path().apply { moveTo(7 * u, cy - 5.5f * u); lineTo(13.5f * u, cy); lineTo(7 * u, cy + 5.5f * u) }, verde, style = Stroke(1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        listOf(-1f, 1f).forEach { s ->
            for (k in 0 until 2) {
                val p = Offset((8.6f + 2.4f * k) * u, cy + s * (4.1f - 2 * k) * u)
                drawLine(verde.copy(alpha = 0.7f), p, Offset(p.x - 2.2f * u, p.y + s * 1.2f * u), 0.8f * u, StrokeCap.Round)
            }
        }
        drawCircle(if (oscuro) Color(0xFFE5484D) else ROJO, 1.7f * u, Offset(16 * u, cy))
    }

    /** La estrella de la punta del árbol: de oro llena y con su resplandor si la fila está fija; de contorno tenue si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2 + 0.5f * u)
        val forma = estrella(c, 10 * u, 5, 0.45f)
        if (fija) {
            drawCircle(LLAMA.copy(alpha = if (oscuro) 0.22f else 0.35f), 11 * u, c)
            drawPath(forma, if (oscuro) Color(0xFFF2C94C) else ORO)
            drawPath(forma, if (oscuro) Color(0xFFB8902A) else Color(0xFF9C7A22), style = Stroke(0.7f * u, join = StrokeJoin.Round))
            drawCircle(Color.White.copy(alpha = 0.6f), 1.2f * u, Offset(c.x - 1.5f * u, c.y - 2.5f * u))
        } else {
            drawPath(forma, (if (oscuro) NIEVE else Color(0xFF56635A)).copy(alpha = 0.5f), style = Stroke(1.1f * u, join = StrokeJoin.Round))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFBCC9BF) else Color(0xFF4E5A52)

    /**
     * La ventana en invierno, sin nada que mirar: el cristal apenas teñido de cielo (azul hondo, de noche) en su marco de
     * madera, unos copos sueltos arriba y la nieve amontonada en el alféizar.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(if (oscuro) CIELO_NOCHE.copy(alpha = 0.55f) else CIELO_DIA.copy(alpha = 0.2f), r.topLeft, r.size, CornerRadius(3 * u))
        drawRoundRect((if (oscuro) NOGAL_LUZ else NOGAL).copy(alpha = if (oscuro) 0.6f else 0.35f), r.topLeft, r.size, CornerRadius(3 * u), style = Stroke(2 * u))
        // Los copos sueltos de arriba.
        val copo = (if (oscuro) NIEVE else Color(0xFF8AA4BC)).copy(alpha = if (oscuro) 0.5f else 0.6f)
        for (i in 0 until 7) {
            val p = Offset(r.left + 10 * u + (r.width - 20 * u) * azar(e.semilla, i), r.top + 5 * u + 10 * u * azar(e.semilla, i + 20))
            drawCircle(copo, (0.8f + 0.8f * azar(e.semilla, i + 40)) * u, p)
        }
        // La nieve del alféizar: un montón ondulado al pie.
        val nieve = Path().apply {
            moveTo(r.left + 1 * u, r.bottom - 1 * u)
            lineTo(r.left + 1 * u, r.bottom - 4 * u)
            val tramos = 6
            for (k in 0 until tramos) {
                val x0 = r.left + 1 * u + (r.width - 2 * u) * k / tramos
                val x1 = r.left + 1 * u + (r.width - 2 * u) * (k + 1) / tramos
                quadraticTo((x0 + x1) / 2, r.bottom - (8 + 4 * azar(e.semilla, k + 60)) * u, x1, r.bottom - 4 * u)
            }
            lineTo(r.right - 1 * u, r.bottom - 1 * u); close()
        }
        drawPath(nieve, NIEVE.copy(alpha = if (oscuro) 0.7f else 0.95f))
        drawPath(nieve, Color(0xFF8AA4BC).copy(alpha = if (oscuro) 0.35f else 0.6f), style = Stroke(0.7f * u))
    }

    /** El calcetín colgado del clavo, sin regalos: rojo, con su puño blanco, el talón y la punta de otro rojo, y una franja verde. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val rojo = if (oscuro) t(Color(0xFFE5484D), true) else ROJO
        val oscurito = mezcla(rojo, Color.Black, 0.3f)
        // El clavo y el cordel.
        drawCircle(if (oscuro) Color(0xFF9AA0A6) else Color(0xFF5A5650), 1.4f * u, Offset(f(0.52f), f(0.04f)))
        drawLine((if (oscuro) NIEVE else TINTA).copy(alpha = 0.6f), Offset(f(0.52f), f(0.04f)), Offset(f(0.42f), f(0.16f)), 0.8f * u)
        drawLine((if (oscuro) NIEVE else TINTA).copy(alpha = 0.6f), Offset(f(0.52f), f(0.04f)), Offset(f(0.6f), f(0.16f)), 0.8f * u)
        val calcetin = Path().apply {
            moveTo(f(0.34f), f(0.24f)); lineTo(f(0.68f), f(0.24f)); lineTo(f(0.68f), f(0.62f)); quadraticTo(f(0.68f), f(0.92f), f(0.38f), f(0.92f))
            lineTo(f(0.22f), f(0.92f)); quadraticTo(f(0.08f), f(0.8f), f(0.24f), f(0.7f)); lineTo(f(0.34f), f(0.62f)); close()
        }
        drawPath(calcetin, rojo)
        // El talón y la punta, de otro rojo, y la franja verde.
        drawPath(Path().apply { moveTo(f(0.68f), f(0.66f)); quadraticTo(f(0.68f), f(0.92f), f(0.46f), f(0.92f)); quadraticTo(f(0.54f), f(0.74f), f(0.68f), f(0.66f)); close() }, oscurito)
        drawPath(Path().apply { moveTo(f(0.22f), f(0.92f)); quadraticTo(f(0.08f), f(0.8f), f(0.24f), f(0.7f)); quadraticTo(f(0.28f), f(0.84f), f(0.22f), f(0.92f)); close() }, oscurito)
        drawRect(if (oscuro) Color(0xFF66BB6A) else VERDE, Offset(f(0.34f), f(0.42f)), Size(f(0.34f), f(0.06f)))
        drawPath(calcetin, mezcla(rojo, Color.Black, 0.45f), style = Stroke(0.9f * u, join = StrokeJoin.Round))
        // El puño blanco.
        drawRoundRect(NIEVE, Offset(f(0.3f), f(0.14f)), Size(f(0.42f), f(0.14f)), CornerRadius(f(0.04f)))
        drawRoundRect(Color(0xFF8AA4BC).copy(alpha = 0.6f), Offset(f(0.3f), f(0.14f)), Size(f(0.42f), f(0.14f)), CornerRadius(f(0.04f)), style = Stroke(0.7f * u))
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFF3EFE6) else Color(0xFF1E2A22)

    /**
     * La tarjeta de un regalo: el cartón claro (verde abeto hondo, de noche) y, a la izquierda, la etiqueta colgante con su
     * ojal y su cordel enroscado; roja o verde, según el aviso.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(if (oscuro) Color(0xFF1C3125) else Color(0xFFFFFCF6), Offset.Zero, size, CornerRadius(4 * u))
        drawRoundRect((if (oscuro) Color(0xFF86C99A) else ABETO).copy(alpha = 0.25f), Offset.Zero, size, CornerRadius(4 * u), style = Stroke(1 * u))
        val cy = minOf(h / 2, 18 * u)
        val color = if (e.semilla % 2 == 0) (if (oscuro) Color(0xFFE5484D) else ROJO) else (if (oscuro) Color(0xFF66BB6A) else VERDE)
        // El cordel: del borde de arriba al ojal, con su rizo.
        drawPath(Path().apply { moveTo(4 * u, 0f); cubicTo(2 * u, cy - 6 * u, 10 * u, cy - 9 * u, 9 * u, cy - 3 * u) }, (if (oscuro) NIEVE else TINTA).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        // La etiqueta: un rectángulo con la punta hacia el cordel.
        val etiqueta = Path().apply {
            moveTo(6 * u, cy); lineTo(10 * u, cy - 5.5f * u); lineTo(25 * u, cy - 5.5f * u); lineTo(25 * u, cy + 5.5f * u); lineTo(10 * u, cy + 5.5f * u); close()
        }
        drawPath(etiqueta, Color.Black.copy(alpha = 0.2f), style = Stroke(1.6f * u))
        drawPath(etiqueta, color)
        drawCircle(if (oscuro) Color(0xFF1C3125) else Color(0xFFFFFCF6), 1.3f * u, Offset(10 * u, cy))
        drawLine(Color.White.copy(alpha = 0.7f), Offset(14 * u, cy), Offset(22 * u, cy), 0.8f * u, StrokeCap.Round)
        drawCircle(if (oscuro) Color(0xFFF2C94C) else ORO, 1 * u, Offset(14 * u, cy - 2.6f * u))
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) NIEVE else Color(0xFF1E2A22)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF86C99A) else Color(0xFF8E1C22)

    /**
     * Un regalo envuelto: el papel liso (crema rosado de día, verde abeto de noche) con la cinta roja que baja por el lado
     * izquierdo y el moño chico en la esquina de arriba.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val caja = Rect(0.5f * u, 4 * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.18f), Offset(caja.left, caja.top + 1.2f * u), caja.size, CornerRadius(3 * u))
        drawRoundRect(if (oscuro) Color(0xFF1C3125) else Color(0xFFF8E7E2), caja.topLeft, caja.size, CornerRadius(3 * u))
        drawRoundRect((if (oscuro) Color(0xFF86C99A) else ROJO).copy(alpha = 0.3f), caja.topLeft, caja.size, CornerRadius(3 * u), style = Stroke(0.8f * u))
        val rojo = if (oscuro) Color(0xFFE5484D) else ROJO
        drawRect(rojo, Offset(caja.left + 3 * u, caja.top), Size(3 * u, caja.height))
        // El moño.
        val n = Offset(caja.left + 4.5f * u, caja.top + 0.5f * u)
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(n.x, n.y); cubicTo(n.x + s * 1 * u, n.y - 4.5f * u, n.x + s * 5.5f * u, n.y - 3.5f * u, n.x + s * 4.2f * u, n.y - 0.4f * u); close()
            }, rojo)
        }
        drawCircle(mezcla(rojo, Color.Black, 0.25f), 1.3f * u, n)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: crema de día; de noche, abeto hondo, la noche de invierno o el nogal. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF1E2C24) else Color(0xFFFBF4E6)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF1C3125) else Color(0xFFFFFBF2)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF141C33) else Color(0xFFDCE4EC)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF2A1F1A) else Color(0xFFFAF1E1)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF3EFE6) else Color(0xFF1E2A22)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFB5C2B8) else Color(0xFF56635A)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFFF07A7E) else Color(0xFF8E1C22)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFFF2C94C) else Color(0xFF8E1C22)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFFF2C94C) else Color(0xFF1F4A6E)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF86C99A) else ABETO
    }

    /**
     * Las cuatro son de la casa en Navidad. La cifra del tablero (METRICA) es una ventanita del calendario de Adviento: el
     * marco rojo, la puertita marcada por su raya de perforación y una estrellita de oro en la esquina; si la cifra se aparta
     * de tu normal, la raya se vuelve dorada y la estrella crece con su brillo. Lo que te falta hoy (PENDIENTE) es la tarjeta
     * con la guirnalda de abeto colgada en festones a lo largo de la orilla de arriba, con sus bolas rojas, y el doble filete
     * rojo y oro; completo, la guirnalda enciende sus lucecitas. La noche (NOCHE) es la noche de invierno: copos en la orilla,
     * la nieve amontonada al pie y la estrella arriba a la derecha, plateada; sellado el día, la estrella es de oro y brilla.
     * Las de Vida (VIDA) son tarjetas de felicitación: el doblez a la izquierda, el filete de oro, la cinta roja con su moño
     * que abraza la esquina y una ramita de acebo abajo. Al tocarlas, se hunden y toman la luz tibia de una vela.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val tinte = if (e.presionado) mezcla(fondo, LLAMA, if (oscuro) 0.05f else 0.16f) else fondo
        val rojo = if (oscuro) Color(0xFFE5484D) else ROJO
        val oro = if (oscuro) Color(0xFFF2C94C) else ORO
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(4 * u))
                drawRoundRect(if (oscuro) mezcla(ROJO, Color.Black, 0.3f) else ROJO, r.topLeft, r.size, CornerRadius(4 * u))
                val hoja = r.deflate(2.4f * u)
                drawRoundRect(tinte, hoja.topLeft, hoja.size, CornerRadius(2.5f * u))
                // La puertita: su raya de perforación.
                val puerta = hoja.deflate(minOf(m * 0.36f, 5 * u))
                drawRoundRect(if (e.destacado) oro else (if (oscuro) NIEVE.copy(alpha = 0.3f) else ROJO.copy(alpha = 0.45f)), puerta.topLeft, puerta.size, CornerRadius(1.5f * u),
                    style = Stroke(if (e.destacado) 1 * u else 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
                // La estrellita de la esquina.
                val c = Offset(hoja.right - 4.6f * u, hoja.top + 4.6f * u)
                if (e.destacado) {
                    drawCircle(LLAMA.copy(alpha = 0.4f), 4.6f * u, c)
                    drawPath(estrella(c, 3.8f * u, 5, 0.45f), oro)
                } else drawPath(estrella(c, 2.6f * u, 5, 0.45f), oro.copy(alpha = 0.9f))
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2 * u)
                drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(4 * u))
                drawRoundRect(tinte, r.topLeft, r.size, CornerRadius(4 * u))
                drawRoundRect(rojo.copy(alpha = 0.8f), r.deflate(1.4f * u).topLeft, r.deflate(1.4f * u).size, CornerRadius(3 * u), style = Stroke(1.2f * u))
                drawRoundRect(oro.copy(alpha = 0.6f), r.deflate(3.4f * u).topLeft, r.deflate(3.4f * u).size, CornerRadius(2 * u), style = Stroke(0.6f * u))
                // La guirnalda de abeto, en festones a lo largo de la orilla de arriba.
                val festones = maxOf(1, (r.width / (64 * u)).toInt())
                val fw = r.width / festones
                val caida = minOf(m * 0.38f, 5.3f * u)
                fun alto(x: Float): Float {
                    val f = ((x - r.left) / fw) % 1f
                    return r.top + 2.4f * u + caida * sin(PI.toFloat() * f)
                }
                val aguja = if (oscuro) Color(0xFF4F8C60) else ABETO
                val aguja2 = if (oscuro) Color(0xFF6FAE7E) else VERDE
                var x = r.left + 1 * u; var k = 0
                while (x < r.right - 1 * u && k < 2000) {
                    val y = alto(x)
                    drawLine(if (k % 2 == 0) aguja else aguja2, Offset(x, y), Offset(x - 1.7f * u, y - 2.2f * u), 0.9f * u, StrokeCap.Round)
                    drawLine(if (k % 2 == 0) aguja2 else aguja, Offset(x, y), Offset(x + 1.5f * u, y + 2.2f * u), 0.9f * u, StrokeCap.Round)
                    x += maxOf(1.5f * u, 1f); k++
                }
                // Las bolas que cuelgan de cada festón y, ya completo, las lucecitas.
                for (j in 0 until festones) {
                    val bx = r.left + fw * (j + 0.5f)
                    val by = alto(bx) + 2.8f * u
                    drawLine(oro, Offset(bx, alto(bx)), Offset(bx, by - 1.5f * u), 0.5f * u)
                    drawCircle(rojo, 2 * u, Offset(bx, by))
                    drawCircle(Color.White.copy(alpha = 0.6f), 0.5f * u, Offset(bx - 0.6f * u, by - 0.6f * u))
                }
                if (e.destacado) {
                    val luces = listOf(LLAMA, Color(0xFFFF6B6B), Color(0xFF7FE0A0), Color(0xFF8EC5FF))
                    x = r.left + 5 * u; k = 0
                    while (x < r.right - 4 * u && k < 400) {
                        val p = Offset(x, alto(x) + 0.4f * u)
                        val c = luces[k % luces.size]
                        drawCircle(c.copy(alpha = 0.35f), 1.8f * u, p)
                        drawCircle(c, 0.8f * u, p)
                        x += maxOf(7 * u, 1f); k++
                    }
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(5 * u))) }
                drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.top + 1 * u), r.size, CornerRadius(5 * u))
                drawPath(forma, fondo)
                // Los copos que caen por la orilla.
                val copo = if (oscuro) NIEVE else Color(0xFF8FA6BC)
                val n = ((w + h) / (10 * u)).toInt().coerceIn(8, 80)
                puntosMargen(w, h, m, n, e.semilla + 3, 3 * u).forEach { (p, b) ->
                    if (p.y < r.bottom - m * 0.8f && !(p.x > r.right - m * 1.3f && p.y < m * 1.3f)) drawCircle(copo.copy(alpha = 0.4f + 0.5f * b), (0.5f + 0.8f * b) * u, p)
                }
                // La nieve amontonada al pie, en lomas suaves.
                clipPath(forma) {
                    val base = r.bottom - 2 * u
                    val nieve = Path().apply {
                        moveTo(r.left, r.bottom); lineTo(r.left, base)
                        var x = r.left; var k = 0
                        while (x < r.right && k < 100) {
                            val ancho = maxOf((26 + 34 * azar(e.semilla, k)) * u, 1f)
                            val alto = (2 + (m / u * 0.42f - 2) * azar(e.semilla, k + 30)) * u
                            quadraticTo(x + ancho / 2, base - 2.2f * alto, x + ancho, base); x += ancho; k++
                        }
                        lineTo(r.right + 60 * u, base); lineTo(r.right + 60 * u, r.bottom); close()
                    }
                    drawPath(nieve, if (oscuro) mezcla(NIEVE, CIELO_NOCHE, 0.3f) else Color.White)
                    drawPath(nieve, (if (oscuro) NIEVE else Color(0xFF8FA6BC)).copy(alpha = if (oscuro) 0.5f else 0.85f), style = Stroke(0.7f * u))
                }
                drawPath(forma, (if (oscuro) NIEVE else CIELO_DIA).copy(alpha = if (oscuro) 0.18f else 0.9f), style = Stroke(1 * u))
                // La estrella de arriba a la derecha.
                val c = Offset(r.right - m * 0.62f, r.top + m * 0.6f)
                if (e.destacado) {
                    mancha(c, m * 0.55f, LLAMA.copy(alpha = 0.7f))
                    drawPath(estrella(c, m * 0.36f, 5, 0.45f), oro)
                } else drawPath(estrella(c, m * 0.3f, 5, 0.45f), if (oscuro) NIEVE.copy(alpha = 0.75f) else Color(0xFF8FA6BC))
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(r.left + 0.6f * u, r.top + 1.5f * u), r.size)
                drawRect(tinte, r.topLeft, r.size)
                // El doblez de la tarjeta, a la izquierda, y el filete de oro.
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.07f), r.topLeft, Size(3 * u, r.height))
                drawLine(Color.Black.copy(alpha = 0.18f), Offset(r.left + 3 * u, r.top), Offset(r.left + 3 * u, r.bottom), 0.6f * u)
                drawRect(oro.copy(alpha = 0.65f), r.deflate(4 * u).topLeft, r.deflate(4 * u).size, style = Stroke(0.6f * u))
                // La cinta roja que abraza la esquina de arriba, con su moño.
                val a = m * 0.42f; val b = m * 0.82f
                drawPath(Path().apply {
                    moveTo(r.left, r.top + a); lineTo(r.left + a, r.top); lineTo(r.left + b, r.top); lineTo(r.left, r.top + b); close()
                }, rojo)
                val nudo = Offset(r.left + (a + b) / 4, r.top + (a + b) / 4)
                listOf(-1f, 1f).forEach { s ->
                    rotate(45f, nudo) { drawOval(mezcla(rojo, Color.Black, 0.15f), Offset(nudo.x + (if (s < 0) -3.4f else 0.4f) * u, nudo.y - 1.4f * u), Size(3 * u, 2.8f * u)) }
                }
                drawCircle(mezcla(rojo, Color.Black, 0.3f), 1 * u, nudo)
                // La ramita de acebo, abajo a la derecha: dos hojas y tres bayas.
                val p = Offset(r.right - m * 0.7f, r.bottom - m * 0.5f)
                val hoja = if (oscuro) Color(0xFF4F8C60) else VERDE
                listOf(-28f, 200f).forEach { g ->
                    rotate(g, p) { drawOval(hoja, Offset(p.x, p.y - 1.3f * u), Size(5.4f * u, 2.6f * u)) }
                }
                listOf(Offset(0f, -1f), Offset(1.4f, 0.4f), Offset(-1.2f, 0.6f)).forEach { o -> drawCircle(rojo, 1 * u, Offset(p.x + o.x * u, p.y + o.y * u)) }
            }
        }
    }
}

/**
 * Abismo: el campo es la mirilla de un instrumento del batiscafo, como su buscador: el anillo de latón con sus pernos a los
 * lados y en hilera arriba y abajo, la junta de goma negra y la esfera de esmalte blanco, lisa, con su escala muy fina al
 * pie. Con foco, la junta se enciende de bioluminiscencia; con un error, se vuelve roja. En modo oscuro, la esfera es del
 * azul del fondo, con la letra clara.
 */
object ComunAbismo : DisenoComun {
    private val LATON = Color(0xFFC09A48)
    private val LATON_LUZ = Color(0xFFE2C27E)
    private val LATON_OSC = Color(0xFF7E6128)
    private val AGUA = Color(0xFF0C2338)
    private val BIOLUZ = Color(0xFF5FF2E0)
    private val GOMA = Color(0xFF26292B)
    private val TINTA = Color(0xFF1B1E21)
    private val ESMALTE_BLANCO = Color(0xFFF2F1EA)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF07090B), 0.36f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFD8F4F0) else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF7A9C9E) else Color(0xFF6E7476)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) BIOLUZ else Color(0xFF17646C)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF7A6E) else Color(0xFFC7302A)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = apagar(t(x, oscuro), e.habilitado)
        // El anillo de latón.
        val marco = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        val r = minOf(marco.height / 2, 16 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 0.4f * u, marco.top + 1.5f * u), marco.size, CornerRadius(r))
        drawRoundRect(c(LATON), marco.topLeft, marco.size, CornerRadius(r))
        drawRoundRect(c(LATON_LUZ), marco.topLeft, marco.size, CornerRadius(r), style = Stroke(1 * u))
        drawRoundRect(c(LATON_OSC), marco.deflate(4.2f * u).topLeft, marco.deflate(4.2f * u).size, CornerRadius(r - 4.2f * u), style = Stroke(0.8f * u))
        // La junta de goma y la esfera, lisa.
        val junta = marco.deflate(5 * u)
        drawRoundRect(GOMA, junta.topLeft, junta.size, CornerRadius(r - 5 * u))
        val esfera = junta.deflate(1.8f * u)
        val re = maxOf(r - 6.8f * u, 1f)
        drawRoundRect(apagar(if (oscuro) mezcla(AGUA, Color.Black, 0.2f) else ESMALTE_BLANCO, e.habilitado), esfera.topLeft, esfera.size, CornerRadius(re))
        val luz = e.enfocado && e.habilitado
        if (luz && oscuro) drawRoundRect(BIOLUZ.copy(alpha = 0.05f), esfera.topLeft, esfera.size, CornerRadius(re))
        when {
            e.error -> drawRoundRect(errorCampo(oscuro), junta.deflate(0.7f * u).topLeft, junta.deflate(0.7f * u).size, CornerRadius(r - 5.7f * u), style = Stroke(1.4f * u))
            luz -> {
                drawRoundRect(BIOLUZ.copy(alpha = 0.9f), junta.deflate(0.7f * u).topLeft, junta.deflate(0.7f * u).size, CornerRadius(r - 5.7f * u), style = Stroke(1.4f * u))
                drawRoundRect(BIOLUZ.copy(alpha = 0.25f), junta.topLeft, junta.size, CornerRadius(r - 5 * u), style = Stroke(2.2f * u))
            }
        }
        if (e.multilinea) renglones(esfera.left + re, esfera.right - re, margenCampoY.toPx(), esfera.bottom - 6 * u, tintaCampo(oscuro).copy(alpha = 0.07f), 0.6f * u)
        // La escala tenue al pie de la esfera y su reflejo de arriba.
        var x = esfera.left + re + 2 * u; var n = 0
        while (x < esfera.right - re - 2 * u && n < 120) {
            drawLine(tintaCampo(oscuro).copy(alpha = 0.14f), Offset(x, esfera.bottom - 1.5f * u), Offset(x, esfera.bottom - (if (n % 5 == 0) 4.5f else 3f) * u), 0.6f * u)
            x += maxOf(6 * u, 1f); n++
        }
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.55f), Offset(esfera.left + re, esfera.top + 1.4f * u), Size(maxOf(esfera.width - 2 * re, 1f), 1.4f * u), CornerRadius(0.7f * u))
        // Los pernos: a media altura en los lados y en hilera arriba y abajo.
        fun perno(p: Offset) {
            drawCircle(c(LATON_OSC), 1.7f * u, p)
            drawCircle(c(LATON_LUZ), 1.1f * u, Offset(p.x - 0.2f * u, p.y - 0.2f * u))
            drawLine(c(LATON_OSC), Offset(p.x - 0.9f * u, p.y + 0.5f * u), Offset(p.x + 0.9f * u, p.y - 0.5f * u), 0.5f * u)
        }
        perno(Offset(marco.left + 2.5f * u, marco.center.y)); perno(Offset(marco.right - 2.5f * u, marco.center.y))
        val desde = marco.left + r + 4 * u; val hasta = marco.right - r - 4 * u
        if (hasta > desde) {
            val cuantos = ((hasta - desde) / maxOf(56 * u, 1f)).toInt().coerceIn(1, 30)
            for (k in 0..cuantos) {
                val px = desde + (hasta - desde) * k / cuantos
                perno(Offset(px, marco.top + 2.5f * u)); perno(Offset(px, marco.bottom - 2.5f * u))
            }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /** El cable del batiscafo con un eco de sonar al centro: el punto y sus dos ondas, que se abren hacia los dos lados. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val color = if (oscuro) BIOLUZ else Color(0xFF17646C)
        val cable = color.copy(alpha = (if (oscuro) 0.35f else 0.4f) * f)
        drawLine(cable, Offset(6 * u, cy), Offset(cx - 11 * u, cy), 0.9f * u, StrokeCap.Round)
        drawLine(cable, Offset(cx + 11 * u, cy), Offset(w - 6 * u, cy), 0.9f * u, StrokeCap.Round)
        drawCircle(color.copy(alpha = 0.8f * f), 1.5f * u, Offset(cx, cy))
        listOf(4 * u to 0.6f, 7.5f * u to 0.35f).forEach { (r, a) ->
            listOf(145f, -35f).forEach { desde ->
                drawArc(color.copy(alpha = a * f), desde, 70f, false, Offset(cx - r, cy - r), Size(2 * r, 2 * r), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            }
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF5CF2C6) else Color(0xFF0A5C8C)

    /**
     * Las ondas del sonar que buscan el texto: dos arcos finos a cada lado, ((  así  )). Al tocarlo llega el eco: los arcos
     * se avivan, aparece un tercero y el agua de alrededor se ilumina tenue (de bioluminiscencia, de noche).
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        val color = apagarBoton(if (oscuro) BIOLUZ else Color(0xFF17646C), e.habilitado)
        if (toque) drawRoundRect(color.copy(alpha = if (oscuro) 0.14f else 0.1f), Offset(3 * u, cy - 15 * u), Size(w - 6 * u, 30 * u), CornerRadius(15 * u))
        val radios = if (toque) listOf(9 * u to 0.95f, 12 * u to 0.65f, 15 * u to 0.35f) else listOf(10 * u to 0.7f, 13 * u to 0.4f)
        val izq = 18 * u; val der = w - 18 * u
        radios.forEach { (r, a) ->
            listOf(izq to 148f, der to -32f).forEach { (x, desde) ->
                drawArc(color.copy(alpha = color.alpha * a), desde, 64f, false, Offset(x - r, cy - r), Size(2 * r, 2 * r), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            }
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un tramo del cable del batiscafo: la raya muy tenue al pie y, a la izquierda, una mota de plancton (que de noche brilla).
     * Al tocarla, el agua de alrededor se aclara (de noche, de bioluminiscencia) y la mota se enciende con su halo.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val luz = if (oscuro) BIOLUZ else Color(0xFF17646C)
        if (e.presionado) drawRect(if (oscuro) BIOLUZ.copy(alpha = 0.08f) else Color(0xFF0A5C8C).copy(alpha = 0.08f), Offset.Zero, size)
        drawLine(luz.copy(alpha = if (oscuro) 0.15f else 0.2f), Offset(margenFila.toPx(), h - 0.8f * u), Offset(w, h - 0.8f * u), 0.9f * u, StrokeCap.Round)
        val p = Offset(6.5f * u, cy)
        if (oscuro || e.presionado) drawCircle(luz.copy(alpha = if (e.presionado) 0.28f else 0.14f), if (e.presionado) 5 * u else 3.6f * u, p)
        drawCircle(luz.copy(alpha = if (e.presionado) 1f else 0.6f), 1.7f * u, p)
    }

    /** La punta del sonar hacia adelante, con dos burbujas que suben detrás. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        val color = if (oscuro) Color(0xFF5CF2C6) else Color(0xFF0A5C8C)
        drawPath(Path().apply { moveTo(9 * u, cy - 5 * u); lineTo(15.5f * u, cy); lineTo(9 * u, cy + 5 * u) }, color, style = Stroke(1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color.copy(alpha = 0.7f), 1.6f * u, Offset(4.5f * u, cy + 2 * u), style = Stroke(0.9f * u))
        drawCircle(color.copy(alpha = 0.5f), 1 * u, Offset(3 * u, cy - 2.6f * u), style = Stroke(0.8f * u))
    }

    /** Una estrella de mar: coral llena y moteada si la fila está fija; de contorno tenue si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2 + 0.6f * u)
        val forma = estrella(c, 10.5f * u, 5, 0.42f)
        if (fija) {
            val coral = if (oscuro) Color(0xFFFF8C6A) else Color(0xFFC8502E)
            drawPath(forma, coral)
            drawPath(forma, mezcla(coral, Color.Black, 0.3f), style = Stroke(0.8f * u, join = StrokeJoin.Round))
            for (k in 0 until 5) {
                val a = 2 * PI.toFloat() * k / 5 - PI.toFloat() / 2
                drawCircle(Color.White.copy(alpha = 0.55f), 0.7f * u, Offset(c.x + cos(a) * 4.5f * u, c.y + sin(a) * 4.5f * u))
            }
            drawCircle(mezcla(coral, Color.White, 0.3f), 1.4f * u, c)
        } else {
            drawPath(forma, (if (oscuro) BIOLUZ else Color(0xFF41657A)).copy(alpha = 0.5f), style = Stroke(1.1f * u, join = StrokeJoin.Round))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFF8DB2BF) else Color(0xFF3B5E72)

    /**
     * Agua quieta, sin nada a la vista: el recuadro de esquinas redondas, apenas teñido de agua (de noche, del azul del
     * fondo), con su filete de latón y una columna de burbujas que sube por la orilla derecha.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(if (oscuro) Color(0xFF0A1A26).copy(alpha = 0.8f) else Color(0xFF0A5C8C).copy(alpha = 0.05f), r.topLeft, r.size, CornerRadius(12 * u))
        drawRoundRect((if (oscuro) LATON else LATON_OSC).copy(alpha = if (oscuro) 0.3f else 0.35f), r.topLeft, r.size, CornerRadius(12 * u), style = Stroke(1 * u))
        // Las burbujas: cada vez más chicas hacia arriba.
        val burbuja = (if (oscuro) BIOLUZ else Color(0xFF17646C)).copy(alpha = if (oscuro) 0.35f else 0.3f)
        var y = r.bottom - 10 * u; var k = 0
        while (y > r.top + 8 * u && k < 5) {
            val rr = (3f - 0.5f * k) * u
            drawCircle(burbuja, rr, Offset(r.right - 14 * u + 4 * u * (azar(e.semilla, k) - 0.5f), y), style = Stroke(0.8f * u))
            y -= maxOf(rr * 2 + 8 * u, 1f); k++
        }
    }

    /** La pantalla del sonar sin un solo eco: el aro de latón, los anillos, la cruz y el haz que barre en vano. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val c = Offset(f(0.5f), f(0.5f))
        val luz = if (oscuro) BIOLUZ else Color(0xFF17646C)
        drawCircle(Color.Black.copy(alpha = 0.2f), f(0.46f), c + Offset(0f, 1 * u))
        drawCircle(t(LATON, oscuro), f(0.46f), c)
        drawCircle(if (oscuro) Color(0xFF041019) else Color(0xFF0C2E40), f(0.39f), c)
        // El haz que barre: un abanico tenue y su raya.
        drawArc(luz.copy(alpha = 0.22f), -90f, 50f, true, Offset(c.x - f(0.39f), c.y - f(0.39f)), Size(f(0.78f), f(0.78f)))
        val anillo = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(f(0.04f), f(0.04f))))
        drawCircle(BIOLUZ.copy(alpha = 0.35f), f(0.26f), c, style = anillo)
        drawCircle(BIOLUZ.copy(alpha = 0.35f), f(0.13f), c, style = anillo)
        drawLine(BIOLUZ.copy(alpha = 0.25f), Offset(c.x - f(0.39f), c.y), Offset(c.x + f(0.39f), c.y), 0.6f * u)
        drawLine(BIOLUZ.copy(alpha = 0.25f), Offset(c.x, c.y - f(0.39f)), Offset(c.x, c.y + f(0.39f)), 0.6f * u)
        val a = (-40f) * PI.toFloat() / 180f
        drawLine(BIOLUZ.copy(alpha = 0.9f), c, Offset(c.x + cos(a) * f(0.38f), c.y + sin(a) * f(0.38f)), 1.2f * u, StrokeCap.Round)
        drawCircle(BIOLUZ, 1 * u, c)
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFD6F0F2) else Color(0xFF05263A)

    /**
     * La luz de aviso del tablero: la franja de agua clara (de noche, del azul del fondo) y, a la izquierda, una mirilla de
     * latón con sus cuatro pernos y, dentro, el piloto de bioluminiscencia encendido.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(if (oscuro) Color(0xFF0A1A26) else Color(0xFFD3E7EF).copy(alpha = 0.75f), Offset.Zero, size, CornerRadius(6 * u))
        drawRoundRect((if (oscuro) LATON else LATON_OSC).copy(alpha = 0.3f), Offset.Zero, size, CornerRadius(6 * u), style = Stroke(1 * u))
        val c = Offset(15 * u, minOf(h / 2, 18 * u))
        drawCircle(Color.Black.copy(alpha = 0.25f), 9 * u, c + Offset(0f, 0.8f * u))
        drawCircle(t(LATON, oscuro), 9 * u, c)
        drawCircle(t(LATON_LUZ, oscuro), 9 * u, c, style = Stroke(0.7f * u))
        drawCircle(GOMA, 6 * u, c)
        drawCircle(if (oscuro) Color(0xFF041019) else Color(0xFF0C2E40), 5 * u, c)
        drawCircle(BIOLUZ.copy(alpha = 0.3f), 4 * u, c)
        drawCircle(BIOLUZ, 2.2f * u, c)
        drawCircle(Color.White.copy(alpha = 0.6f), 0.8f * u, Offset(c.x - 1.6f * u, c.y - 1.8f * u))
        for (k in 0 until 4) {
            val a = PI.toFloat() / 2 * k + PI.toFloat() / 4
            drawCircle(t(LATON_OSC, oscuro), 0.9f * u, Offset(c.x + cos(a) * 7.5f * u, c.y + sin(a) * 7.5f * u))
        }
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFD8F4F0) else TINTA
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) BIOLUZ else Color(0xFF17646C)

    /**
     * La esfera de un instrumento del batiscafo: el bisel de latón de esquinas redondas con un perno en cada esquina, la junta
     * de goma y la esfera lisa de esmalte blanco (de noche, del azul del fondo) donde va el número.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val marco = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(marco.left + 0.4f * u, marco.top + 1.2f * u), marco.size, CornerRadius(10 * u))
        drawRoundRect(t(LATON, oscuro), marco.topLeft, marco.size, CornerRadius(10 * u))
        drawRoundRect(t(LATON_LUZ, oscuro), marco.topLeft, marco.size, CornerRadius(10 * u), style = Stroke(0.8f * u))
        val junta = marco.deflate(3.2f * u)
        drawRoundRect(GOMA, junta.topLeft, junta.size, CornerRadius(7 * u))
        val esfera = junta.deflate(1.3f * u)
        drawRoundRect(if (oscuro) mezcla(AGUA, Color.Black, 0.2f) else ESMALTE_BLANCO, esfera.topLeft, esfera.size, CornerRadius(6 * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.6f), Offset(esfera.left + 6 * u, esfera.top + 1.2f * u), Size(maxOf(esfera.width - 12 * u, 1f), 1.3f * u), CornerRadius(0.6f * u))
        listOf(marco.topLeft + Offset(4.4f * u, 4.4f * u), marco.topRight + Offset(-4.4f * u, 4.4f * u),
            marco.bottomLeft + Offset(4.4f * u, -4.4f * u), marco.bottomRight + Offset(-4.4f * u, -4.4f * u)).forEach { p ->
            drawCircle(t(LATON_OSC, oscuro), 1.2f * u, p)
            drawCircle(t(LATON_LUZ, oscuro), 0.6f * u, Offset(p.x - 0.2f * u, p.y - 0.2f * u))
        }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: el esmalte blanco de día y el azul del fondo de noche; la noche de Día, el agua honda. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF0B1D2C) else ESMALTE_BLANCO
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF0A1924) else Color(0xFFEEF1EF)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF041019) else Color(0xFFD6E4EB)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF0C1E2A) else Color(0xFFEAF2F2)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFD8F4F0) else Color(0xFF05263A)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF7DA2AF) else Color(0xFF41657A)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFFF08A5E) else Color(0xFF9A3A1A)
        else -> if (oscuro) BIOLUZ else Color(0xFF17646C)
    }

    /**
     * Las cuatro son del batiscafo y del agua de afuera. La cifra del tablero (METRICA) es la lectura de un instrumento: el
     * esmalte liso con escuadras de latón atornilladas en las esquinas y, al pie, la escala de profundidad; si la cifra se
     * aparta de tu normal, el sonar marca un eco arriba al centro, con sus anillos. Lo que te falta hoy (PENDIENTE) es la
     * escotilla: el marco de acero con su hilera de remaches arriba y abajo y la luz de alarma roja en la esquina; completo, la
     * luz se vuelve bioluminiscente. La noche (NOCHE) es el agua honda: la nieve marina que baja por la orilla y una medusa
     * arriba a la derecha; sellado el día, la medusa se enciende con su halo. Las de Vida (VIDA) son etiquetas de muestras: la
     * cápsula de esmalte con su filete de latón y una columna de burbujas que sube por la izquierda. Al tocarlas, se hunden y
     * el esmalte toma un reflejo del agua.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val tinte = if (e.presionado) mezcla(fondo, if (oscuro) BIOLUZ else Color(0xFF5FB8C8), if (oscuro) 0.07f else 0.12f) else fondo
        val laton = t(LATON, oscuro)
        val luz = if (oscuro) BIOLUZ else Color(0xFF17646C)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(5 * u))
                drawRoundRect(tinte, r.topLeft, r.size, CornerRadius(5 * u))
                drawRoundRect(laton.copy(alpha = 0.6f), r.topLeft, r.size, CornerRadius(5 * u), style = Stroke(1 * u))
                // Las escuadras de latón de las esquinas, con su perno.
                val largo = minOf(m * 1.4f, r.width * 0.22f); val ancho = 2.8f * u; val d = 1.8f * u
                listOf(r.topLeft to Offset(1f, 1f), r.topRight to Offset(-1f, 1f), r.bottomLeft to Offset(1f, -1f), r.bottomRight to Offset(-1f, -1f)).forEach { (p, s) ->
                    val x0 = p.x + s.x * d; val y0 = p.y + s.y * d
                    val hz = Rect(minOf(x0, x0 + s.x * largo), minOf(y0, y0 + s.y * ancho), maxOf(x0, x0 + s.x * largo), maxOf(y0, y0 + s.y * ancho))
                    val vt = Rect(minOf(x0, x0 + s.x * ancho), minOf(y0, y0 + s.y * largo), maxOf(x0, x0 + s.x * ancho), maxOf(y0, y0 + s.y * largo))
                    listOf(hz, vt).forEach { q ->
                        drawRect(Color.Black.copy(alpha = 0.2f), Offset(q.left, q.top + 0.5f * u), q.size)
                        drawRect(laton, q.topLeft, q.size)
                    }
                    val perno = Offset(x0 + s.x * ancho * 0.5f, y0 + s.y * ancho * 0.5f)
                    drawCircle(t(LATON_OSC, oscuro), 1 * u, perno)
                    drawCircle(t(LATON_LUZ, oscuro), 0.45f * u, Offset(perno.x - 0.2f * u, perno.y - 0.2f * u))
                }
                // La escala de profundidad al pie, entre las escuadras.
                val tic = (if (oscuro) BIOLUZ else TINTA).copy(alpha = 0.35f)
                var x = r.left + d + largo + 3 * u; var k = 0
                while (x < r.right - d - largo - 3 * u && k < 300) {
                    drawLine(tic, Offset(x, r.bottom - 2.6f * u), Offset(x, r.bottom - 2.6f * u - (if (k % 5 == 0) 3.2f else 1.6f) * u), 0.6f * u)
                    x += maxOf(4 * u, 1f); k++
                }
                // El eco del sonar, arriba al centro.
                if (e.destacado) {
                    val c = Offset(w / 2, r.top + m * 0.45f)
                    drawCircle(luz, 1.3f * u, c)
                    drawCircle(luz.copy(alpha = 0.6f), 3 * u, c, style = Stroke(0.7f * u))
                    drawCircle(luz.copy(alpha = 0.3f), 4.8f * u, c, style = Stroke(0.7f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                val radio = 7 * u
                drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.top + 1.2f * u), r.size, CornerRadius(radio))
                val acero = if (oscuro) Color(0xFF34424D) else Color(0xFF5B656C)
                drawRoundRect(acero, r.topLeft, r.size, CornerRadius(radio))
                drawRoundRect(Color.White.copy(alpha = 0.15f), r.topLeft, r.size, CornerRadius(radio), style = Stroke(0.7f * u))
                val dentro = r.deflate(3.8f * u)
                drawRoundRect(tinte, dentro.topLeft, dentro.size, CornerRadius(radio - 3 * u))
                drawRoundRect(Color.Black.copy(alpha = 0.3f), dentro.topLeft, dentro.size, CornerRadius(radio - 3 * u), style = Stroke(0.7f * u))
                // Los remaches, en hilera arriba y abajo.
                var x = r.left + 14 * u; var k = 0
                while (x < r.right - 8 * u && k < 300) {
                    listOf(r.top + 1.9f * u, r.bottom - 1.9f * u).forEach { y ->
                        drawCircle(Color.Black.copy(alpha = 0.3f), 0.9f * u, Offset(x, y + 0.3f * u))
                        drawCircle(mezcla(acero, Color.White, 0.35f), 1 * u, Offset(x, y))
                    }
                    x += maxOf(9 * u, 1f); k++
                }
                // La luz de alarma en la esquina: roja, o bioluminiscente ya completo.
                val c = Offset(r.left + m * 0.55f, r.top + m * 0.55f)
                val color = if (e.destacado) (if (oscuro) BIOLUZ else Color(0xFF2BB5A8)) else Color(0xFFE0402E)
                drawCircle(color.copy(alpha = 0.3f), 5.8f * u, c)
                drawCircle(laton, 4.6f * u, c)
                drawCircle(t(LATON_OSC, oscuro), 4.6f * u, c, style = Stroke(0.6f * u))
                drawCircle(color, 3.3f * u, c)
                drawCircle(Color.White.copy(alpha = 0.6f), 0.9f * u, Offset(c.x - 0.9f * u, c.y - 0.9f * u))
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1 * u), r.size, CornerRadius(8 * u))
                drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(8 * u))
                drawRoundRect(laton.copy(alpha = 0.4f), r.topLeft, r.size, CornerRadius(8 * u), style = Stroke(1 * u))
                // La nieve marina, en la orilla.
                val nieve = if (oscuro) Color(0xFFD6F0F2) else Color(0xFF6E92A2)
                val n = ((w + h) / (9 * u)).toInt().coerceIn(8, 90)
                puntosMargen(w, h, m, n, e.semilla + 17, 3 * u).forEach { (p, b) ->
                    if (!(p.x > r.right - m * 1.4f && p.y < m * 1.8f)) drawCircle(nieve.copy(alpha = 0.25f + 0.45f * b), (0.4f + 0.6f * b) * u, p)
                }
                // La medusa, arriba a la derecha.
                val c = Offset(r.right - m * 0.62f, r.top + m * 0.42f)
                val rad = m * 0.34f
                val color = if (e.destacado) (if (oscuro) BIOLUZ else Color(0xFF1F9A92)) else (if (oscuro) Color(0xFF5FA8B0) else Color(0xFF6E92A2))
                if (e.destacado) mancha(Offset(c.x, c.y + rad * 0.5f), m * 0.6f, BIOLUZ.copy(alpha = if (oscuro) 0.45f else 0.5f))
                for (i in 0 until 3) {
                    val x0 = c.x + (i - 1) * rad * 0.55f
                    drawPath(Path().apply {
                        moveTo(x0, c.y)
                        quadraticTo(x0 + rad * 0.4f, c.y + rad * 0.6f, x0, c.y + rad * 1.2f)
                        quadraticTo(x0 - rad * 0.4f, c.y + rad * 1.8f, x0, c.y + rad * 2.4f)
                    }, color.copy(alpha = 0.75f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
                }
                drawArc(color.copy(alpha = if (e.destacado) 0.95f else 0.8f), 180f, 180f, true, Offset(c.x - rad, c.y - rad), Size(2 * rad, 2 * rad))
                drawArc(Color.White.copy(alpha = 0.35f), 200f, 60f, false, Offset(c.x - rad * 0.7f, c.y - rad * 0.7f), Size(rad * 1.4f, rad * 1.4f), style = Stroke(0.6f * u))
            }
            TipoTarjeta.VIDA -> {
                val r = Rect(1 * u, 1 * u + baja, w - 1 * u, h - 2 * u + baja)
                val radio = minOf(m * 0.9f, r.height / 2)
                if (!e.presionado) drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + 1.5f * u), r.size, CornerRadius(radio))
                drawRoundRect(tinte, r.topLeft, r.size, CornerRadius(radio))
                drawRoundRect(laton, r.topLeft, r.size, CornerRadius(radio), style = Stroke(1.2f * u))
                drawRoundRect(laton.copy(alpha = 0.4f), r.deflate(2.4f * u).topLeft, r.deflate(2.4f * u).size, CornerRadius(maxOf(radio - 2.4f * u, 1f)), style = Stroke(0.5f * u))
                // Las burbujas que suben por la orilla izquierda.
                val burbuja = (if (e.presionado || oscuro) BIOLUZ else Color(0xFF41657A)).copy(alpha = if (e.presionado) 0.8f else 0.55f)
                var y = r.bottom - radio * 0.8f; var k = 0
                while (y > r.top + radio * 0.8f && k < 40) {
                    val rr = (0.7f + 1.1f * azar(e.semilla, k)) * u
                    drawCircle(burbuja, rr, Offset(r.left + m * 0.5f + (azar(e.semilla, k + 20) - 0.5f) * 2 * u, y), style = Stroke(0.6f * u))
                    y -= maxOf(rr * 2 + 2.5f * u + 2 * u * azar(e.semilla, k + 40), 1f); k++
                }
            }
        }
    }
}

/**
 * URSS: el campo es la casilla de un formulario impreso de la biblioteca, como su buscador: papel de periódico con su
 * fibra, la casilla de doble filete fino impresa en azul, el margen rojo a la izquierda y la raya de puntos donde se
 * escribe (una por renglón si caben varios). Con foco, el margen rojo se vuelve doble y aparece el sello violeta de la
 * biblioteca estampado en la esquina; con un error, la casilla y la raya se imprimen en rojo. En modo oscuro, el papel
 * en la sala a oscuras, con la letra clara.
 */
object ComunUrss : DisenoComun {
    private val PAPEL_PERIODICO = Color(0xFFEFEADB)
    private val AZUL_IMPRESO = Color(0xFF2E4A8A)
    private val ROJO = Color(0xFFB0241E)
    private val VIOLETA = Color(0xFF6A4FA0)
    private val TINTA = Color(0xFF1E1C1A)
    private val NOCHE = Color(0xFF161A1C)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0C0B), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFEFE4C9) else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9E9A8E) else Color(0xFF737068)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFA8BEEC) else AZUL_IMPRESO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF9A85) else ROJO

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val papel = apagar(if (oscuro) mezcla(t(PAPEL_PERIODICO, true), NOCHE, 0.72f) else PAPEL_PERIODICO, e.habilitado)
        val azul = apagar(if (oscuro) Color(0xFF8EA4D6) else AZUL_IMPRESO, e.habilitado)
        val rojo = apagar(if (oscuro) Color(0xFFE0705A) else ROJO, e.habilitado)
        // El papel, con su sombra al pie y la fibra del periódico.
        drawRect(papel, Offset.Zero, size)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, h - 1.5f * u), Size(w, 1.5f * u))
        val motas = (w * h / (420 * u * u)).toInt().coerceIn(6, 120)
        for (i in 0 until motas) {
            val p = Offset(maxOf(w - 4 * u, 1f) * azar(211 + e.semilla % 9, i), h * azar(212, i))
            drawLine((if (oscuro) Color.White else Color.Black).copy(alpha = 0.06f), p, Offset(p.x + (1 + 2 * azar(213, i)) * u, p.y + 0.4f * u), 0.5f * u)
        }
        // La casilla de doble filete: azul, o roja con un error.
        val casilla = Rect(3.5f * u, 3.5f * u, w - 3.5f * u, h - 4.5f * u)
        val filete = if (e.error) errorCampo(oscuro) else azul
        drawRect(filete.copy(alpha = 0.9f), casilla.topLeft, casilla.size, style = Stroke(if (e.error) 1.4f * u else 1 * u))
        drawRect(filete.copy(alpha = 0.55f), casilla.deflate(2 * u).topLeft, casilla.deflate(2 * u).size, style = Stroke(0.5f * u))
        // El margen rojo: doble con foco.
        val mx = casilla.left + 7 * u
        val dentro = casilla.deflate(2 * u)
        drawLine(rojo, Offset(mx, dentro.top), Offset(mx, dentro.bottom), 0.9f * u)
        if (e.enfocado && e.habilitado) drawLine(rojo, Offset(mx + 2 * u, dentro.top), Offset(mx + 2 * u, dentro.bottom), 0.9f * u)
        // La raya de puntos donde se escribe (y una por renglón si caben varios).
        val puntos = (if (e.error) errorCampo(oscuro) else azul).copy(alpha = if (e.enfocado) 0.75f else 0.5f)
        val izq = mx + 5 * u; val der = dentro.right - 4 * u
        if (e.multilinea) renglones(izq, der, margenCampoY.toPx(), dentro.bottom - 8 * u, puntos.copy(alpha = puntos.alpha * 0.6f), 0.45f * u, punteado = 3 * u)
        var x = izq; var n = 0
        while (x < der && n < 2000) { drawCircle(puntos, 0.5f * u, Offset(x, dentro.bottom - 3.5f * u)); x += maxOf(3 * u, 1f); n++ }
        // El sello violeta de la biblioteca, a caballo de la esquina de arriba a la derecha.
        if (e.enfocado && e.habilitado) {
            val s = Offset(casilla.right - 10 * u, casilla.top + 6 * u)
            val violeta = if (oscuro) Color(0xFFB09AE0) else VIOLETA
            rotate(-14f, s) {
                drawCircle(violeta.copy(alpha = 0.6f), 7.5f * u, s, style = Stroke(1.1f * u))
                drawCircle(violeta.copy(alpha = 0.5f), 5.4f * u, s, style = Stroke(0.5f * u))
                drawPath(estrella(s, 3.4f * u, 5, 0.42f), violeta.copy(alpha = 0.55f))
            }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La regla de imprenta del cartel: el filete negro de lado a lado, roto al centro por una estrella roja entre dos barras
     * rojas inclinadas, como en una portada constructivista.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cx = w / 2; val cy = size.height / 2
        val f = fuerza(e)
        val tinta = (if (oscuro) Color(0xFFEFE4C9) else TINTA).copy(alpha = (if (oscuro) 0.3f else 0.45f) * f)
        val rojo = (if (oscuro) Color(0xFFE0453F) else ROJO).copy(alpha = 0.9f * f)
        drawLine(tinta, Offset(4 * u, cy), Offset(cx - 17 * u, cy), 1 * u)
        drawLine(tinta, Offset(cx + 17 * u, cy), Offset(w - 4 * u, cy), 1 * u)
        drawPath(paralelogramo(cx - 15 * u, cy - 1 * u, cx - 7 * u, cy + 1 * u, 1.4f * u), rojo)
        drawPath(paralelogramo(cx + 7 * u, cy - 1 * u, cx + 15 * u, cy + 1 * u, 1.4f * u), rojo)
        drawPath(estrella(Offset(cx, cy), 3.8f * u, 5, 0.4f), rojo)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF5C5C) else Color(0xFFB0121C)

    /**
     * El subrayado del cartel constructivista: una barra roja inclinada bajo el texto y la cuña roja que la empuja desde la
     * izquierda. Al tocarlo, una franja roja tenue, también inclinada, se imprime detrás del texto y la barra engruesa.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val toque = e.presionado && e.habilitado
        val rojo = apagarBoton(if (oscuro) Color(0xFFE0453F) else ROJO, e.habilitado)
        if (toque) drawPath(paralelogramo(4 * u, cy - 14 * u, w - 4 * u, cy + 14 * u, 6 * u), rojo.copy(alpha = if (oscuro) 0.16f else 0.12f))
        val y = minOf(cy + 11 * u, h - 5 * u)
        val g = if (toque) 2.6f * u else 1.8f * u
        val x0 = 15 * u; val x1 = w - 9 * u
        if (x1 > x0) {
            drawPath(paralelogramo(x0, y - g / 2, x1, y + g / 2, g * 0.8f), rojo.copy(alpha = rojo.alpha * 0.85f))
            drawPath(Path().apply { moveTo(x0 - 10 * u, y - 2.4f * u); lineTo(x0 - 1 * u, y); lineTo(x0 - 10 * u, y + 2.4f * u); close() }, rojo)
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 14.dp

    /**
     * Un renglón del índice impreso: la regla fina de tinta al pie y, a la izquierda, un cuadrito rojo. Al tocarla, se imprime
     * detrás una franja roja tenue, inclinada como las del cartel, y el cuadrito se vuelve la cuña.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val rojo = if (oscuro) Color(0xFFE0453F) else ROJO
        if (e.presionado) drawPath(paralelogramo(0f, 0f, w, h, minOf(h * 0.4f, 24 * u)), rojo.copy(alpha = if (oscuro) 0.12f else 0.09f))
        drawLine((if (oscuro) Color(0xFFEFE4C9) else TINTA).copy(alpha = if (oscuro) 0.14f else 0.16f), Offset(margenFila.toPx(), h - 0.6f * u), Offset(w, h - 0.6f * u), 1 * u)
        if (e.presionado) drawPath(Path().apply { moveTo(3 * u, cy - 4 * u); lineTo(10 * u, cy); lineTo(3 * u, cy + 4 * u); close() }, rojo)
        else drawRect(rojo.copy(alpha = 0.8f), Offset(4.5f * u, cy - 1.6f * u), Size(3.2f * u, 3.2f * u))
    }

    /** La cuña roja del cartel, empujada por una barra negra. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val cy = size.height / 2
        drawRect(if (oscuro) Color(0xFFEFE4C9) else TINTA, Offset(2 * u, cy - 1 * u), Size(8 * u, 2 * u))
        drawPath(Path().apply { moveTo(9 * u, cy - 5 * u); lineTo(18 * u, cy); lineTo(9 * u, cy + 5 * u); close() }, if (oscuro) Color(0xFFE0453F) else ROJO)
    }

    /** La estrella roja del cartel, sobre el medio aro negro: llena si la fila está fija; de contorno tenue si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2 + 0.6f * u)
        val forma = estrella(c, 10 * u, 5, 0.4f)
        if (fija) {
            drawArc(if (oscuro) Color(0xFFEFE4C9) else TINTA, 20f, 160f, false, Offset(c.x - 10.5f * u, c.y - 10.5f * u), Size(21 * u, 21 * u), style = Stroke(1.6f * u))
            drawPath(forma, if (oscuro) Color(0xFFE0453F) else ROJO)
        } else {
            drawPath(forma, (if (oscuro) Color(0xFFEFE4C9) else TINTA).copy(alpha = 0.45f), style = Stroke(1.1f * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFBDB09B) else Color(0xFF4A4440)

    /**
     * La planilla sin llenar: el papel de periódico apenas teñido, con su fibra, el doble filete fino impreso en azul y, en la
     * esquina de arriba a la izquierda, el triángulo rojo del cartel.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRect(if (oscuro) Color(0xFF262020).copy(alpha = 0.85f) else PAPEL_PERIODICO.copy(alpha = 0.7f), r.topLeft, r.size)
        val motas = (w * h / (900 * u * u)).toInt().coerceIn(6, 80)
        for (i in 0 until motas) {
            val p = Offset(r.left + (r.width - 4 * u) * azar(221 + e.semilla % 9, i), r.top + r.height * azar(222, i))
            drawLine((if (oscuro) Color.White else Color.Black).copy(alpha = 0.05f), p, Offset(p.x + (1 + 2 * azar(223, i)) * u, p.y + 0.4f * u), 0.5f * u)
        }
        val azul = if (oscuro) Color(0xFF8EA4D6) else AZUL_IMPRESO
        drawRect(azul.copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(1 * u))
        drawRect(azul.copy(alpha = 0.35f), r.deflate(2.5f * u).topLeft, r.deflate(2.5f * u).size, style = Stroke(0.5f * u))
        val lado = minOf(22 * u, r.height * 0.3f)
        drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.left + lado, r.top); lineTo(r.left, r.top + lado); close() }, (if (oscuro) Color(0xFFE0453F) else ROJO).copy(alpha = 0.85f))
    }

    /** El cartel todavía sin pintar, un poco ladeado en la pared: la estrella solo de contorno, la barra roja y los renglones vacíos. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val tinta = if (oscuro) Color(0xFFD9CDB4) else TINTA
        val rojo = if (oscuro) Color(0xFFE0453F) else ROJO
        rotate(-6f, Offset(f(0.5f), f(0.5f))) {
            val hoja = Rect(f(0.16f), f(0.06f), f(0.84f), f(0.94f))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(hoja.left + 1 * u, hoja.top + 1.2f * u), hoja.size)
            drawRect(if (oscuro) Color(0xFF3A3434) else Color(0xFFF7F0DE), hoja.topLeft, hoja.size)
            drawRect(tinta.copy(alpha = 0.7f), hoja.topLeft, hoja.size, style = Stroke(1 * u))
            drawPath(estrella(Offset(f(0.5f), f(0.34f)), f(0.2f), 5, 0.4f), tinta.copy(alpha = 0.8f), style = Stroke(1.1f * u, join = StrokeJoin.Miter))
            drawPath(paralelogramo(hoja.left, f(0.58f), hoja.right - f(0.08f), f(0.66f), f(0.06f)), rojo)
            listOf(0.76f, 0.84f).forEach { y -> drawLine(tinta.copy(alpha = 0.35f), Offset(f(0.26f), f(y)), Offset(f(0.74f), f(y)), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * u, 2 * u))) }
            drawCircle(tinta.copy(alpha = 0.6f), 0.9f * u, Offset(f(0.5f), hoja.top + 1.6f * u))
        }
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E7D2) else Color(0xFF161414)

    /**
     * El aviso del mural de la fábrica: el papel claro (de noche, la sala a oscuras) y, a la izquierda, la barra negra gruesa
     * con la cuña roja que apunta al texto, como en una portada constructivista.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) Color(0xFF262020) else Color(0xFFF7F0DE), Offset.Zero, size)
        drawLine((if (oscuro) Color(0xFFEFE4C9) else TINTA).copy(alpha = 0.18f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 1.2f * u)
        drawRect(if (oscuro) Color(0xFFEFE4C9) else TINTA, Offset(3 * u, 0f), Size(4 * u, h))
        val cy = minOf(h / 2, 18 * u)
        drawPath(Path().apply { moveTo(9 * u, cy - 7 * u); lineTo(23 * u, cy); lineTo(9 * u, cy + 7 * u); close() }, if (oscuro) Color(0xFFE0453F) else ROJO)
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFEFE4C9) else TINTA
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFA8BEEC) else AZUL_IMPRESO

    /**
     * Una casilla del plan impreso: el papel liso con una barra gruesa a la izquierda y al pie (negra de día, crema de noche),
     * el filete azul fino y el triángulo rojo en la esquina de arriba a la derecha.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        drawRect(if (oscuro) Color(0xFF262020) else PAPEL_PERIODICO, r.topLeft, r.size)
        drawRect((if (oscuro) Color(0xFF8EA4D6) else AZUL_IMPRESO).copy(alpha = 0.55f), r.topLeft, r.size, style = Stroke(0.7f * u))
        val barra = if (oscuro) Color(0xFFEFE4C9).copy(alpha = 0.85f) else TINTA
        drawRect(barra, Offset(r.left, r.top), Size(3.5f * u, r.height))
        drawRect(barra, Offset(r.left, r.bottom - 2.5f * u), Size(r.width, 2.5f * u))
        val lado = minOf(10 * u, r.height * 0.25f)
        drawPath(Path().apply { moveTo(r.right, r.top); lineTo(r.right, r.top + lado); lineTo(r.right - lado, r.top); close() }, if (oscuro) Color(0xFFE0453F) else ROJO)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** El fondo liso de cada tarjeta: el papel impreso (de noche, la sala a oscuras); la noche de Día, gris azulado. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF262020) else Color(0xFFF3EEDF)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF2A2220) else Color(0xFFF7F0DE)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF141A1E) else Color(0xFFDFE0E2)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF242022) else Color(0xFFF5EFDF)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFEFE4C9) else Color(0xFF161414)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFB7AA95) else Color(0xFF4A4440)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE, TipoTarjeta.NOCHE -> if (oscuro) Color(0xFFF06A60) else ROJO
        else -> if (oscuro) Color(0xFFA8BEEC) else AZUL_IMPRESO
    }

    /**
     * Las cuatro son impresos de una portada constructivista. La cifra del tablero (METRICA) es una ficha del plan: el papel
     * liso con su marco negro fino, el cuarto de círculo rojo en la esquina de arriba y la barra negra al pie; si la cifra se
     * aparta de tu normal, el marco se vuelve rojo y el cuarto de círculo, una estrella. Lo que te falta hoy (PENDIENTE) es el
     * cartel del mural: la barra negra gruesa arriba, la banda roja a la izquierda que la cruza y la cuña roja que corta la
     * barra hacia la derecha; completo, una estrella roja sobre la barra. La noche (NOCHE) es la torre de radio de celosía en
     * la orilla derecha, con estrellas finas en la banda de arriba; sellado el día, su luz roja de arriba se enciende con una
     * estrella. Las de Vida (VIDA) son sellos de correo: el papel dentado, el marco azul fino, la franja roja al pie y una
     * estrellita. Al tocarlas, se hunden y el papel toma un tinte rojo.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m = margenTarjeta.toPx()
        val fondo = fondoTarjeta(e.tipo, oscuro)
        val baja = if (e.presionado) 1 * u else 0f
        val tinte = if (e.presionado) mezcla(fondo, ROJO, if (oscuro) 0.08f else 0.07f) else fondo
        val negro = if (oscuro) Color(0xFFEFE4C9).copy(alpha = 0.85f) else TINTA
        val rojo = if (oscuro) Color(0xFFE0453F) else ROJO
        val azul = if (oscuro) Color(0xFF8EA4D6) else AZUL_IMPRESO
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val r = Rect(0.5f * u, 0.5f * u + baja, w - 0.5f * u, h - 1.5f * u + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 1 * u, r.top + 1.5f * u), r.size)
                drawRect(tinte, r.topLeft, r.size)
                drawRect((if (e.destacado) rojo else negro).copy(alpha = 0.85f), r.deflate(2 * u).topLeft, r.deflate(2 * u).size, style = Stroke(if (e.destacado) 1.4f * u else 0.8f * u))
                if (e.destacado) drawPath(estrella(Offset(r.right - m * 0.55f, r.top + m * 0.55f), m * 0.42f, 5, 0.4f), rojo)
                else {
                    val rad = m * 0.85f
                    drawArc(rojo, 90f, 90f, true, Offset(r.right - rad, r.top - rad), Size(2 * rad, 2 * rad))
                }
                drawRect(negro, Offset(r.left + 2 * u, r.bottom - 5.5f * u), Size(minOf(r.width * 0.42f, 80 * u), 3.5f * u))
                drawLine(negro.copy(alpha = 0.6f), Offset(r.left + 2 * u + minOf(r.width * 0.42f, 80 * u), r.bottom - 4 * u), Offset(r.right - 2 * u, r.bottom - 4 * u), 0.5f * u)
            }
            TipoTarjeta.PENDIENTE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 1 * u, r.top + 1.5f * u), r.size)
                drawRect(tinte, r.topLeft, r.size)
                // La barra negra de arriba, la banda roja de la izquierda y la cuña roja que corta la barra.
                val barra = minOf(m * 0.42f, 6 * u)
                drawRect(negro, r.topLeft, Size(r.width, barra))
                drawRect(rojo, r.topLeft, Size(minOf(m * 0.48f, 6.5f * u), r.height))
                val largo = minOf(36 * u, r.width * 0.2f)
                drawPath(Path().apply {
                    moveTo(r.right - largo, r.top + barra); lineTo(r.right, r.top + barra); lineTo(r.right, r.top + barra + 4.5f * u); close()
                }, rojo)
                drawLine(negro.copy(alpha = 0.45f), Offset(r.left + 9 * u, r.bottom - 2.6f * u), Offset(r.right - 3 * u, r.bottom - 2.6f * u), 0.6f * u)
                // Completo: la estrella roja sobre la barra.
                if (e.destacado) {
                    val c = Offset(r.right - largo - 10 * u, r.top + barra * 0.75f)
                    drawPath(estrella(c, 4.2f * u, 5, 0.4f), Color.White.copy(alpha = if (oscuro) 0.2f else 0.9f), style = Stroke(1.2f * u))
                    drawPath(estrella(c, 4.2f * u, 5, 0.4f), rojo)
                }
            }
            TipoTarjeta.NOCHE -> {
                val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 1 * u, r.top + 1.5f * u), r.size)
                drawRect(fondo, r.topLeft, r.size)
                drawRect(negro.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.8f * u))
                // Las estrellas, puntos finos en la banda de arriba.
                val astro = if (oscuro) Color(0xFFEFE4C9) else Color(0xFF8E8A84)
                val n = ((w + h) / (8 * u)).toInt().coerceIn(8, 90)
                puntosMargen(w, h, m, n, e.semilla + 19, 3 * u).forEach { (p, b) ->
                    if (p.y < m && p.x < r.right - m * 1.3f) drawCircle(astro.copy(alpha = 0.3f + 0.5f * b), (0.5f + 0.5f * b) * u, p)
                }
                // La torre de celosía en la orilla derecha: dos largueros que se juntan y el zigzag de los tirantes.
                val xc = r.right - m * 0.5f
                val base = r.bottom - 1 * u
                val cima = r.top + m * 0.95f
                val abajo = m * 0.38f; val arriba = 0.9f * u
                val acero = (if (oscuro) Color(0xFFB7AA95) else Color(0xFF6A6460)).copy(alpha = if (oscuro) 0.55f else 0.75f)
                fun ancho(y: Float) = arriba + (abajo - arriba) * (y - cima) / maxOf(base - cima, 1f)
                drawLine(acero, Offset(xc - abajo, base), Offset(xc - arriba, cima), 0.8f * u)
                drawLine(acero, Offset(xc + abajo, base), Offset(xc + arriba, cima), 0.8f * u)
                val tramos = ((base - cima) / (5 * u)).toInt().coerceIn(2, 60)
                for (i in 0 until tramos) {
                    val y0 = base - (base - cima) * i / tramos; val y1 = base - (base - cima) * (i + 1) / tramos
                    val s = if (i % 2 == 0) 1f else -1f
                    drawLine(acero, Offset(xc - s * ancho(y0), y0), Offset(xc + s * ancho(y1), y1), 0.5f * u)
                    drawLine(acero, Offset(xc - ancho(y1), y1), Offset(xc + ancho(y1), y1), 0.4f * u)
                }
                drawLine(acero, Offset(xc, cima), Offset(xc, cima - 3 * u), 0.7f * u)
                // La luz de arriba: tenue, o encendida con su estrella, sellado el día.
                val luz = Offset(xc, cima - 4.6f * u)
                if (e.destacado) {
                    mancha(luz, 5.5f * u, rojo.copy(alpha = 0.6f))
                    drawPath(estrella(luz, 2.8f * u, 5, 0.42f), rojo)
                } else drawCircle(rojo.copy(alpha = 0.6f), 1 * u, luz)
            }
            TipoTarjeta.VIDA -> {
                val rd = 1.1f * u; val paso = maxOf(2.9f * u, 1f)
                val r = Rect(1 * u + rd, 1 * u + rd + baja, w - 1 * u - rd, h - 2 * u - rd + baja)
                if (!e.presionado) drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 0.8f * u, r.top + 1.5f * u), r.size)
                drawRect(tinte, r.topLeft, r.size)
                // Los dientes del sello, todo alrededor: el papel festoneado y, entre diente y diente, el agujero del picado.
                val hueco = if (oscuro) Color.Black.copy(alpha = 0.45f) else Color(0xFF8E8676).copy(alpha = 0.45f)
                var x = r.left; var k = 0
                while (x <= r.right && k < 500) { drawCircle(tinte, rd, Offset(x, r.top)); drawCircle(tinte, rd, Offset(x, r.bottom)); x += paso; k++ }
                var y = r.top; k = 0
                while (y <= r.bottom && k < 500) { drawCircle(tinte, rd, Offset(r.left, y)); drawCircle(tinte, rd, Offset(r.right, y)); y += paso; k++ }
                x = r.left + paso / 2; k = 0
                while (x < r.right && k < 500) { drawCircle(hueco, rd * 0.6f, Offset(x, r.top - rd * 0.3f)); drawCircle(hueco, rd * 0.6f, Offset(x, r.bottom + rd * 0.3f)); x += paso; k++ }
                y = r.top + paso / 2; k = 0
                while (y < r.bottom && k < 500) { drawCircle(hueco, rd * 0.6f, Offset(r.left - rd * 0.3f, y)); drawCircle(hueco, rd * 0.6f, Offset(r.right + rd * 0.3f, y)); y += paso; k++ }
                // El marco azul fino, la franja roja al pie y la estrellita.
                drawRect(azul.copy(alpha = 0.75f), r.deflate(2.6f * u).topLeft, r.deflate(2.6f * u).size, style = Stroke(0.8f * u))
                drawRect(rojo, Offset(r.left + 2.6f * u, r.bottom - 6 * u), Size(r.width - 5.2f * u, 3.4f * u))
                drawPath(estrella(Offset(r.right - 6.4f * u, r.top + 6.4f * u), 2.8f * u, 5, 0.4f), rojo)
            }
        }
    }
}
