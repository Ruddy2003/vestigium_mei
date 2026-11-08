package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Las piezas comunes de la Antigüedad (y de los dos temas de fantasía que comparten sus materiales: Arcano y Antiguas)
// =====================================================================

/**
 * Los renglones tenues de un campo de varios renglones: líneas al paso de 24 sp, la primera un paso bajo [arriba] (el margen
 * de arriba del texto), hasta antes de [abajo]. Con [luz], cada renglón lleva debajo una raya clara, como si estuviera grabado.
 */
private fun DrawScope.renglonesAntiguedad(izq: Float, der: Float, arriba: Float, abajo: Float, color: Color, grosor: Float, luz: Color? = null) {
    val paso = maxOf(24.sp.toPx(), 1f)
    var y = arriba + paso
    var k = 0
    while (y < abajo && k < 200) {
        drawLine(color, Offset(izq, y), Offset(der, y), grosor)
        if (luz != null) drawLine(luz, Offset(izq, y + grosor), Offset(der, y + grosor), grosor * 0.8f)
        y += paso; k++
    }
}


/** El contraste entre dos colores, como lo mide la WCAG: de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrasteAnt(a: Color, b: Color): Float {
    val la = a.luminance() + 0.05f; val lb = b.luminance() + 0.05f
    return maxOf(la, lb) / minOf(la, lb)
}

/**
 * [c] corrida hacia la tinta (casi negro sobre fondos claros, casi blanco sobre los oscuros) lo justo para leerse con un
 * contraste de al menos [minimo] sobre [fondo] y sobre [otro] (la parte más clara u oscura de ese fondo, si tiene luz): así el
 * gasto y el ingreso guardan su color, el que da la app, pero nunca quedan ilegibles sobre la piedra, la cera o el papel del
 * tema. A lo más 24 pasos.
 */
private fun legibleAnt(c: Color, minimo: Float, fondo: Color, otro: Color = fondo): Color {
    val claro = (fondo.luminance() + otro.luminance()) / 2 > 0.18f
    val meta = if (claro) Color(0xFF0E0A07) else Color(0xFFFFFBF2)
    var x = c
    for (k in 1..24) {
        if (contrasteAnt(x, fondo) >= minimo && contrasteAnt(x, otro) >= minimo) return x
        x = mezcla(c, meta, k / 24f)
    }
    return meta
}

/** Lo que mide de ancho [texto] en una sola línea, con esa letra (sin achicar). */
private fun anchoTextoAnt(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, peso: FontWeight, espaciado: Float): Float =
    if (texto.isBlank()) 0f
    else medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

/**
 * Las dos flechas del encabezado del mes: [dibujo] (la flecha de las filas del tema, que apunta a la derecha) en un cuadro de
 * [lado] × 0,6 centrado en los [lado] del extremo derecho, y la misma en espejo en el extremo izquierdo.
 */
private fun DrawScope.flechasMesAnt(lado: Float, dibujo: DrawScope.() -> Unit) {
    val w = size.width; val h = size.height
    val q = minOf(lado * 0.6f, h * 0.72f)
    if (q < 4f || w < 2 * lado) return
    val x = w - lado + (lado - q) / 2; val y = (h - q) / 2
    inset(x, y, w - x - q, h - y - q) { dibujo() }
    scale(-1f, 1f, Offset(w / 2, h / 2)) { inset(x, y, w - x - q, h - y - q) { dibujo() } }
}

/** [c] con su transparencia multiplicada por [a]: los adornos de la hoja, las relaciones y la cinta, cuyo color ya puede traer la suya. */
private fun alfaAnt(c: Color, a: Float): Color = c.copy(alpha = (c.alpha * a).coerceIn(0f, 1f))

/**
 * Prehistoria: el campo es una laja de caliza de borde desportillado, con el grano de la roca en la orilla y la cara del medio
 * alisada a mano, lisa, donde va el texto. A la izquierda, tres puntos de ocre puestos con el dedo; abajo a la derecha, unas
 * rayas de cuenta con carbón. Con el foco se enciende una brasa junto a los puntos y el contorno de la cara se pinta de ocre
 * amarillo; con un error, de hematites. Un degradado: la brasa.
 */
object ComunCueva : DisenoComun {
    private val ROCA = Color(0xFFB09A7C)
    private val ROCA_LUZ = Color(0xFFD6C6A8)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF4A3B2D)
    private val CALIZA = Color(0xFFE6DAC2)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    private val BRASA = Color(0xFFE8804F)
    private val FUEGO = Color(0xFFF4C76E)
    /** El ocre amarillo tostado al fuego: el del rótulo. */
    private val TOSTADO = Color(0xFF7E4C14)
    /** El rojo de hematites molida: el del error, más vivo que el ocre de los puntos. */
    private val HEMATITES = Color(0xFFB0201A)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B08), 0.76f) else c
    /** La cara alisada de la laja, donde va el texto. */
    private fun cara(oscuro: Boolean): Color = hondo(CALIZA, oscuro)

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) CALIZA else CARBON
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), cara(oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) FUEGO else TOSTADO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8C78) else HEMATITES

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val cara = cara(oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, cara, 0.45f)
        // La laja entera, desportillada, con su sombra.
        val fuera = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        val puntos = ((fuera.width + fuera.height) * 2 / (7 * u)).toInt().coerceIn(24, 220)
        val laja = contornoIrregular(fuera, s, puntos, 2.2f * u / minOf(fuera.width, fuera.height).coerceAtLeast(1f))
        translate(0f, 1.5f * u) { drawPath(laja, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f)) }
        drawPath(laja, a(t(ROCA, oscuro)))
        // El grano de la roca (la cara alisada lo tapa en el medio).
        val granos = (w * h / (40 * u * u)).toInt().coerceIn(20, 400)
        clipPath(laja) {
            for (j in 0 until granos) drawCircle(a(t(if (j % 3 == 0) ROCA_LUZ else ROCA_OSC, oscuro)).copy(alpha = 0.5f), (0.5f + 0.7f * azar(s, j)) * u,
                Offset(w * azar(s, j + 500), h * azar(s, j + 1000)))
        }
        // La cara alisada: lisa y más clara, con su canto.
        val dentro = Rect(fuera.left + 4 * u, fuera.top + 4 * u, fuera.right - 4 * u, fuera.bottom - 3.5f * u)
        val alisada = contornoIrregular(dentro, s + 7, (puntos * 0.8f).toInt().coerceAtLeast(16), 0.9f * u / minOf(dentro.width, dentro.height).coerceAtLeast(1f))
        drawPath(alisada, cara)
        drawPath(alisada, a(t(ROCA_HONDA, oscuro)).copy(alpha = 0.45f), style = Stroke(1 * u))
        if (e.multilinea) renglonesAntiguedad(dentro.left + 12 * u, dentro.right - 6 * u, margenCampoY.toPx(), dentro.bottom - 4 * u,
            tintaCampo(oscuro).copy(alpha = if (oscuro) 0.1f else 0.12f), 0.8f * u)
        // Los tres puntos de ocre, a la izquierda, a la altura del primer renglón.
        val cy = minOf(h / 2, dentro.top + 18 * u)
        val cx = dentro.left + 4.5f * u
        val encendido = e.enfocado && e.habilitado
        if (encendido && !e.error) clipPath(laja) { mancha(Offset(cx, cy), 24 * u, BRASA.copy(alpha = if (oscuro) 0.3f else 0.2f)) }
        val punto = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) FUEGO else BRASA
            else -> a(if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE).copy(alpha = 0.8f)
        }
        for (j in -1..1) drawCircle(punto, (1.6f + 0.3f * azar(s, 40 + j)) * u, Offset(cx + (azar(s, 50 + j) - 0.5f) * 1.2f * u, cy + j * 6 * u))
        // Las rayas de cuenta, abajo a la derecha: cuatro de pie y una cruzada.
        val carbon = a(if (oscuro) CALIZA.copy(alpha = 0.3f) else CARBON.copy(alpha = 0.45f))
        val y0 = dentro.bottom - 8 * u; val y1 = dentro.bottom - 2.5f * u
        val x0 = dentro.right - 17 * u
        for (j in 0 until 4) drawLine(carbon, Offset(x0 + j * 2.6f * u, y0 + azar(s, 60 + j) * u), Offset(x0 + j * 2.6f * u + 0.4f * u, y1), 1 * u, StrokeCap.Round)
        drawLine(carbon, Offset(x0 - 1.5f * u, y1 - 1 * u), Offset(x0 + 9.5f * u, y0 + 1 * u), 1 * u, StrokeCap.Round)
        // El contorno pintado: ocre amarillo con el foco, hematites con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) FUEGO else OCRE_AMARILLO
            else -> null
        }
        if (linea != null) drawPath(alisada, linea.copy(alpha = 0.95f), style = Stroke(1.9f * u))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Separador: un trazo de carbón hecho a mano sobre la roca, que tiembla un poco (pocos tramos curvos, a lo más 16 por
     * mitad), cortado al centro por los tres puntos de ocre del campo, puestos en fila. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val s = e.semilla
        val f = if (e.tenue) 0.5f else 1f
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = (if (oscuro) 0.26f else 0.4f) * f)
        val ocre = (if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE).copy(alpha = (if (oscuro) 0.8f else 0.72f) * f)
        val hueco = 13 * u
        val tramo = maxOf(34 * u, 1f)
        for (lado in 0..1) {
            val x0 = if (lado == 0) 2 * u else w / 2 + hueco
            val x1 = if (lado == 0) w / 2 - hueco else w - 2 * u
            if (x1 - x0 < 4 * u) continue
            val n = ((x1 - x0) / tramo).toInt().coerceIn(1, 16)
            val p = Path().apply {
                moveTo(x0, cy + (azar(s, 70 + lado) - 0.5f) * 0.6f * u)
                for (j in 1..n) {
                    val xa = x0 + (x1 - x0) * (j - 0.5f) / n
                    val xb = x0 + (x1 - x0) * j / n
                    quadraticTo(xa, cy + (azar(s, 100 * lado + j) - 0.5f) * 1.8f * u, xb, cy + (azar(s, 100 * lado + j + 50) - 0.5f) * 0.6f * u)
                }
            }
            drawPath(p, carbon, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        }
        for (j in -1..1) drawCircle(ocre, (1.3f + 0.3f * azar(s, 40 + j)) * u, Offset(w / 2 + j * 5.5f * u, cy + (azar(s, 50 + j) - 0.5f) * 1.2f * u))
    }

    /** El texto de los botones: el ocre de los puntos, tostado un poco para que se lea sobre la roca clara; de noche, la brasa. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) BRASA else mezcla(OCRE, CARBON, 0.16f)

    /**
     * Botón de texto: una pasada de ocre con el dedo bajo la palabra, curva y con un hilo más fino que se le escapa. Al
     * tocarlo, la mano unta de ocre la roca detrás del texto (una mancha de borde irregular) y la pasada se aviva; apagado,
     * el ocre queda en polvo. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val cy = h / 2
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
        val apagado = if (e.habilitado) 1f else 0.3f
        val tocado = e.presionado && e.habilitado
        if (tocado && w > 12 * u) {
            val r = Rect(4 * u, cy - 15 * u, w - 4 * u, cy + 15 * u)
            val puntos = ((r.width + r.height) * 2 / (8 * u)).toInt().coerceIn(12, 120)
            drawPath(contornoIrregular(r, s + 3, puntos, 2.4f * u / minOf(r.width, r.height).coerceAtLeast(1f)), ocre.copy(alpha = if (oscuro) 0.2f else 0.15f))
        }
        val x0 = minOf(12 * u, w * 0.25f); val x1 = w - x0
        val y = cy + 11 * u
        val alfa = (if (tocado) 0.9f else 0.5f) * apagado
        val pasada = Path().apply { moveTo(x0, y + 0.6f * u); quadraticTo(w / 2, y - 1.4f * u, x1, y - 0.2f * u) }
        drawPath(pasada, ocre.copy(alpha = alfa), style = Stroke(2.1f * u, cap = StrokeCap.Round))
        val hilo = Path().apply { moveTo(x0 + 3 * u, y + 1.9f * u); quadraticTo(w / 2, y + 0.3f * u, x1 + 2 * u, y + 1.1f * u) }
        drawPath(hilo, ocre.copy(alpha = alfa * 0.5f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La piel curtida de la bolsa del vacío. */
    private val PIEL = Color(0xFF9A6A42)

    /**
     * Un trazo de carbón hecho a mano de [x0] a [x1] a la altura [y], que tiembla hasta [temblor] (pocos tramos curvos, a lo
     * más 12).
     */
    private fun DrawScope.trazoCarbon(x0: Float, x1: Float, y: Float, s: Int, color: Color, grosor: Float, temblor: Float) {
        if (x1 - x0 < 2f) return
        val n = ((x1 - x0) / maxOf(40 * density, 1f)).toInt().coerceIn(1, 12)
        val p = Path().apply {
            moveTo(x0, y + (azar(s, 1) - 0.5f) * temblor * 0.4f)
            for (j in 1..n) {
                val xa = x0 + (x1 - x0) * (j - 0.5f) / n; val xb = x0 + (x1 - x0) * j / n
                quadraticTo(xa, y + (azar(s, 10 + j) - 0.5f) * temblor, xb, y + (azar(s, 40 + j) - 0.5f) * temblor * 0.4f)
            }
        }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: casi nada sobre la roca: un trazo de carbón fino abajo, que tiembla, y a la izquierda un punto de ocre puesto con el
     * dedo (dos, uno sobre otro, si la fila lleva detalle). Al tocarla la mano unta la roca de ocre (una mancha de borde
     * irregular, muy tenue) y el punto se aviva. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
        if (e.presionado && w > 8 * u && h > 8 * u) {
            val r = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
            val puntos = ((r.width + r.height) * 2 / (14 * u)).toInt().coerceIn(12, 90)
            drawPath(contornoIrregular(r, s + 5, puntos, 2f * u / minOf(r.width, r.height).coerceAtLeast(1f)), ocre.copy(alpha = if (oscuro) 0.16f else 0.08f))
        }
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.17f else 0.24f)
        trazoCarbon(margenFila.toPx(), w - 6 * u, h - 1.4f * u, s, carbon, 0.9f * u, 1.4f * u)
        val punto = ocre.copy(alpha = if (e.presionado) 0.95f else 0.62f)
        val x = 5.5f * u
        if (e.conDetalle) {
            drawCircle(punto, (1.4f + 0.3f * azar(s, 3)) * u, Offset(x, h / 2 - 3.4f * u))
            drawCircle(punto, (1.2f + 0.3f * azar(s, 4)) * u, Offset(x + 0.6f * u, h / 2 + 3.4f * u))
        } else drawCircle(punto, (1.5f + 0.3f * azar(s, 3)) * u, Offset(x, h / 2))
    }

    /** Flecha: una punta de sílex tallada en forma de hoja, con la punta a la derecha, la escotadura del astil y sus facetas. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val x0 = w * 0.14f; val x1 = w * 0.92f; val xm = w * 0.5f
        val a = h * 0.27f
        val punta = Path().apply {
            moveTo(x1, cy)
            quadraticTo(xm, cy - a * 1.25f, x0, cy - a * 0.5f)
            lineTo(x0 + w * 0.1f, cy)
            lineTo(x0, cy + a * 0.5f)
            quadraticTo(xm, cy + a * 1.25f, x1, cy)
            close()
        }
        val piedra = if (oscuro) mezcla(ROCA_LUZ, ROCA, 0.35f) else mezcla(ROCA_OSC, CARBON, 0.35f)
        drawPath(punta, piedra)
        drawPath(punta, (if (oscuro) CALIZA else CARBON).copy(alpha = 0.55f), style = Stroke(0.7f * u))
        val faceta = (if (oscuro) CARBON else CALIZA).copy(alpha = 0.45f)
        for (j in 0 until 3) {
            val x = x0 + w * (0.22f + 0.17f * j)
            drawLine(faceta, Offset(x, cy - 0.3f * u), Offset(x + w * 0.08f, cy - a * (0.62f - 0.12f * j)), 0.6f * u)
            drawLine(faceta, Offset(x + w * 0.04f, cy + 0.4f * u), Offset(x + w * 0.11f, cy + a * (0.55f - 0.12f * j)), 0.6f * u)
        }
    }

    /** Los dedos de la mano (de la base a la punta, en un lienzo de 24): índice, medio, anular, meñique y pulgar. */
    private val DEDOS = floatArrayOf(9.7f, 11f, 8.9f, 4.7f, 12.1f, 10.6f, 12.2f, 3.6f, 14.5f, 11f, 15.3f, 4.9f, 16.3f, 12.7f, 18.1f, 7.7f, 8.4f, 15.2f, 4.6f, 11.6f)

    /** Si el punto ([x], [y], en el lienzo de 24) cae en la mano, con [holgura] de más. */
    private fun enMano(x: Float, y: Float, holgura: Float): Boolean {
        val dx = (x - 12.4f) / (4.6f + holgura); val dy = (y - 15f) / (4.8f + holgura)
        if (dx * dx + dy * dy < 1f) return true
        if (x > 9.6f - holgura && x < 14.4f + holgura && y > 17.5f) return true
        for (j in 0 until 5) {
            val ax = DEDOS[4 * j]; val ay = DEDOS[4 * j + 1]; val bx = DEDOS[4 * j + 2]; val by = DEDOS[4 * j + 3]
            val vx = bx - ax; val vy = by - ay
            val t = (((x - ax) * vx + (y - ay) * vy) / (vx * vx + vy * vy)).coerceIn(0f, 1f)
            val ex = ax + vx * t - x; val ey = ay + vy * t - y
            if (ex * ex + ey * ey < (1.2f + holgura) * (1.2f + holgura)) return true
        }
        return false
    }

    /**
     * Marca de fijar: una mano, como las de las paredes de las cuevas. Fija, es la mano en positivo, untada de ocre y apoyada
     * en la roca; sin fijar, la mano en negativo: solo el soplo de ocre alrededor, tenue, y la mano vacía en medio. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val k = size.minDimension / 24f
        val ox = (size.width - 24 * k) / 2; val oy = (size.height - 24 * k) / 2
        fun p(x: Float, y: Float) = Offset(ox + x * k, oy + y * k)
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.55f) else OCRE
        fun mano(c: Color) {
            drawOval(c, p(7.8f, 10.2f), Size(9.2f * k, 9.6f * k))
            for (j in 0 until 5) drawLine(c, p(DEDOS[4 * j], DEDOS[4 * j + 1]), p(DEDOS[4 * j + 2], DEDOS[4 * j + 3]), 2.4f * k, StrokeCap.Round)
            drawRect(c, p(9.7f, 17.6f), Size(4.6f * k, 5.4f * k))
        }
        if (fija) mano(ocre)
        else {
            // La mano en negativo: la silueta apenas (en una capa, para que los dedos no se oscurezcan donde se cruzan con la
            // palma) y el soplo de ocre en una orla alrededor.
            drawContext.canvas.saveLayer(Rect(0f, 0f, size.width, size.height), Paint().apply { alpha = if (oscuro) 0.3f else 0.22f })
            mano(ocre)
            drawContext.canvas.restore()
            val soplo = ocre.copy(alpha = if (oscuro) 0.62f else 0.55f)
            for (j in 0 until 360) {
                val x = 1f + 22f * azar(7, j); val y = 1f + 22f * azar(7, j + 400)
                if (!enMano(x, y, 1.5f) || enMano(x, y, 0.45f)) continue
                drawCircle(soplo, (0.35f + 0.3f * azar(7, j + 800)) * k, p(x, y))
            }
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) ROCA_LUZ else ROCA_HONDA

    /** La laja del vacío: clara de día; de noche, la roca honda de la cueva. */
    private fun fondoVacio(oscuro: Boolean): Color = if (oscuro) hondo(ROCA, true) else mezcla(CALIZA, Color.White, 0.3f)

    /**
     * Vacío: un rincón liso de la pared, todavía sin pintar: una laja clara de borde desportillado, con el grano de la roca
     * apenas, el contorno repasado con carbón a tramos sueltos (no cerrado, para que no parezca un botón) y, abajo a la derecha,
     * una sola raya de cuenta, la primera. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 8 * u || h < 8 * u) return
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        val puntos = ((r.width + r.height) * 2 / (9 * u)).toInt().coerceIn(20, 200)
        val laja = contornoIrregular(r, s, puntos, 2.6f * u / minOf(r.width, r.height).coerceAtLeast(1f))
        drawPath(laja, fondoVacio(oscuro))
        val granos = (w * h / (170 * u * u)).toInt().coerceIn(10, 260)
        for (j in 0 until granos) {
            val c = if (j % 3 == 0) ROCA_LUZ else ROCA_OSC
            drawCircle(t(c, oscuro).copy(alpha = if (oscuro) 0.22f else 0.2f), (0.4f + 0.6f * azar(s, j)) * u,
                Offset(r.left + 6 * u + (r.width - 12 * u) * azar(s, j + 500), r.top + 6 * u + (r.height - 12 * u) * azar(s, j + 1000)))
        }
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.2f else 0.28f)
        drawPath(laja, carbon, style = Stroke(1 * u, cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(30 * u, 10 * u), 20 * u * azar(s, 9))))
        val x = r.right - 12 * u
        drawLine(carbon.copy(alpha = carbon.alpha * 1.5f), Offset(x, r.bottom - 13 * u), Offset(x + 0.6f * u, r.bottom - 6 * u), 1.1f * u, StrokeCap.Round)
    }

    /**
     * Lo vacío: la bolsa de piel, abierta y sin nada: la boca abierta muestra el fondo oscuro, la costura baja por el medio, el
     * cordón cuelga suelto y en la piel van los tres puntos de ocre del tema. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val piel = t(PIEL, oscuro)
        val borde = if (oscuro) mezcla(PIEL, CALIZA, 0.35f) else mezcla(PIEL, CARBON, 0.55f)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(f(0.22f), f(0.88f)), Size(f(0.56f), f(0.08f)))
        val cuerpo = Path().apply {
            moveTo(f(0.33f), f(0.32f))
            cubicTo(f(0.34f), f(0.42f), f(0.08f), f(0.5f), f(0.12f), f(0.74f))
            cubicTo(f(0.16f), f(0.94f), f(0.84f), f(0.94f), f(0.88f), f(0.74f))
            cubicTo(f(0.92f), f(0.5f), f(0.66f), f(0.42f), f(0.67f), f(0.32f))
            close()
        }
        drawPath(cuerpo, piel)
        drawPath(cuerpo, borde, style = Stroke(1.1f * u))
        // Los pliegues del fruncido, bajo la boca.
        listOf(-1f, 1f).forEach { l ->
            drawPath(Path().apply { moveTo(f(0.5f + l * 0.1f), f(0.38f)); quadraticTo(f(0.5f + l * 0.16f), f(0.48f), f(0.5f + l * 0.24f), f(0.52f)) },
                borde.copy(alpha = 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        // La boca abierta, con lo hondo de la bolsa vacía.
        drawOval(piel, Offset(f(0.26f), f(0.24f)), Size(f(0.48f), f(0.15f)))
        drawOval(if (oscuro) Color(0xFF0B0806) else mezcla(CARBON, PIEL, 0.25f), Offset(f(0.31f), f(0.27f)), Size(f(0.38f), f(0.09f)))
        drawOval(borde, Offset(f(0.26f), f(0.24f)), Size(f(0.48f), f(0.15f)), style = Stroke(1 * u))
        // La costura: puntadas cortas por el medio.
        for (j in 0 until 4) {
            val y = f(0.55f + 0.085f * j)
            drawLine(borde.copy(alpha = 0.8f), Offset(f(0.47f), y), Offset(f(0.53f), y + f(0.03f)), 0.8f * u, StrokeCap.Round)
        }
        // El cordón suelto.
        val cordon = Path().apply { moveTo(f(0.7f), f(0.33f)); quadraticTo(f(0.94f), f(0.4f), f(0.86f), f(0.62f)); quadraticTo(f(0.82f), f(0.7f), f(0.9f), f(0.76f)) }
        drawPath(cordon, borde, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawCircle(borde, f(0.025f), Offset(f(0.9f), f(0.76f)))
        // Los tres puntos de ocre.
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.55f) else OCRE
        for (j in 0 until 3) drawCircle(ocre, f(0.028f), Offset(f(0.26f + j * 0.07f), f(0.66f + (j % 2) * 0.03f)))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) CALIZA else CARBON

    /**
     * Aviso: una laja lisa, un tono más honda que la roca, y a la izquierda la marca pintada con el dedo: una franja de ocre que
     * baja temblando de arriba abajo y, junto a ella, uno de los signos que se repiten en las paredes, según [EstadoAviso.semilla]:
     * los tres puntos, el zigzag o el claviforme (una raya con su abultamiento). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 8 * u || h < 8 * u) return
        val r = Rect(0f, 0f, w, h)
        val puntos = ((r.width + r.height) * 2 / (12 * u)).toInt().coerceIn(16, 160)
        val laja = contornoIrregular(r, s + 11, puntos, 1.4f * u / minOf(r.width, r.height).coerceAtLeast(1f))
        drawPath(laja, if (oscuro) hondo(ROCA_OSC, true) else mezcla(CALIZA, ROCA, 0.22f))
        // La franja de ocre, untada con el dedo.
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.5f) else OCRE
        val n = (h / maxOf(14 * u, 1f)).toInt().coerceIn(1, 20)
        val franja = Path().apply {
            moveTo(7 * u, 5 * u)
            for (j in 1..n) {
                val ya = 5 * u + (h - 10 * u) * (j - 0.5f) / n; val yb = 5 * u + (h - 10 * u) * j / n
                quadraticTo(7 * u + (azar(s, 20 + j) - 0.5f) * 2.2f * u, ya, 7 * u + (azar(s, 50 + j) - 0.5f) * 0.8f * u, yb)
            }
        }
        drawPath(franja, ocre.copy(alpha = 0.85f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        // El signo, a la altura del primer renglón.
        val signo = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.6f else 0.7f)
        val cx = 17 * u; val y0 = 11 * u
        when (((s % 3) + 3) % 3) {
            0 -> for (j in 0 until 3) drawCircle(ocre, (1.3f + 0.3f * azar(s, j)) * u, Offset(cx + (azar(s, j + 5) - 0.5f) * u, y0 + 1.5f * u + j * 4.5f * u))
            1 -> drawPath(Path().apply {
                moveTo(cx - 2.5f * u, y0); lineTo(cx + 2.5f * u, y0 + 3 * u); lineTo(cx - 2.5f * u, y0 + 6 * u)
                lineTo(cx + 2.5f * u, y0 + 9 * u); lineTo(cx - 2.5f * u, y0 + 12 * u)
            }, signo, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            else -> {
                drawLine(signo, Offset(cx - 1 * u, y0), Offset(cx - 1 * u, y0 + 13 * u), 1.3f * u, StrokeCap.Round)
                drawPath(Path().apply { moveTo(cx - 1 * u, y0 + 2 * u); quadraticTo(cx + 4.5f * u, y0 + 3.5f * u, cx - 1 * u, y0 + 7 * u) }, signo, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) FUEGO else mezcla(OCRE, CARBON, 0.3f)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) ROCA_LUZ else ROCA_HONDA

    /**
     * Cifra: un canto rodado del río, liso y de esquinas muy redondas, con su sombra, el grano apenas, una luz de agua arriba a
     * la izquierda y, en el margen de arriba a la derecha, las rayas de cuenta con carbón (cuatro de pie y una cruzada). Sin
     * degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 8 * u || h < 8 * u) return
        val radio = CornerRadius(minOf(18 * u, h * 0.32f), minOf(16 * u, h * 0.3f))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), Offset(0.5f * u, 1.8f * u), Size(w - 1 * u, h - 2 * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 2 * u)
        drawRoundRect(if (oscuro) hondo(ROCA_LUZ, true) else ROCA_LUZ, r.topLeft, r.size, radio)
        val granos = (w * h / (90 * u * u)).toInt().coerceIn(8, 80)
        for (j in 0 until granos) drawCircle(t(if (j % 2 == 0) ROCA_OSC else CALIZA, oscuro).copy(alpha = 0.28f), (0.35f + 0.5f * azar(s, j)) * u,
            Offset(r.left + 8 * u + (r.width - 16 * u) * azar(s, j + 300), r.top + 6 * u + (r.height - 12 * u) * azar(s, j + 600)))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), 190f, 60f, false, Offset(r.left + 3 * u, r.top + 2.5f * u),
            Size(minOf(36 * u, r.width * 0.6f), r.height - 5 * u), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), r.topLeft, r.size, radio, style = Stroke(0.8f * u))
        // Las rayas de cuenta, chicas, en el margen de arriba.
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.3f else 0.38f)
        val x0 = r.right - 19 * u; val ya = r.top + 3 * u; val yb = r.top + 8.2f * u
        if (x0 > r.left + 30 * u) {
            for (j in 0 until 4) drawLine(carbon, Offset(x0 + j * 2.2f * u, ya + azar(s, 60 + j) * 0.6f * u), Offset(x0 + j * 2.2f * u + 0.3f * u, yb), 0.8f * u, StrokeCap.Round)
            drawLine(carbon, Offset(x0 - 1.2f * u, yb - 0.6f * u), Offset(x0 + 7.8f * u, ya + 0.6f * u), 0.8f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El hueso pulido de las cifras de Hoy. */
    private val HUESO = Color(0xFFEFE5CF)

    /** El fondo liso de cada tarjeta, donde va el texto: hueso, la pared pintada, la roca del hogar y la piel curtida. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) mezcla(HUESO, Color(0xFF0F0B08), 0.8f) else HUESO
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(OCRE, Color(0xFF0F0B08), 0.84f) else mezcla(CALIZA, Color.White, 0.3f)
        TipoTarjeta.NOCHE -> if (oscuro) mezcla(ROCA_HONDA, Color(0xFF0F0B08), 0.55f) else mezcla(CALIZA, ROCA, 0.22f)
        TipoTarjeta.VIDA -> if (oscuro) mezcla(PIEL, Color(0xFF0F0B08), 0.8f) else mezcla(PIEL, CALIZA, 0.74f)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) CALIZA else CARBON
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        oscuro -> ROCA_LUZ
        tipo == TipoTarjeta.VIDA -> mezcla(PIEL, CARBON, 0.72f)
        else -> ROCA_HONDA
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        oscuro -> if (tipo == TipoTarjeta.PENDIENTE) FUEGO else mezcla(BRASA, FUEGO, 0.45f)
        tipo == TipoTarjeta.PENDIENTE -> mezcla(HEMATITES, CARBON, 0.3f)
        tipo == TipoTarjeta.VIDA -> mezcla(OCRE, CARBON, 0.45f)
        else -> mezcla(OCRE, CARBON, 0.18f)
    }

    /** Una mano en positivo, untada de [color], en un cuadro de 24 × [k] con la esquina en [o] (los dedos de [DEDOS]). */
    private fun DrawScope.mano(o: Offset, k: Float, color: Color) {
        fun p(x: Float, y: Float) = Offset(o.x + x * k, o.y + y * k)
        drawOval(color, p(7.8f, 10.2f), Size(9.2f * k, 9.6f * k))
        for (j in 0 until 5) drawLine(color, p(DEDOS[4 * j], DEDOS[4 * j + 1]), p(DEDOS[4 * j + 2], DEDOS[4 * j + 3]), 2.4f * k, StrokeCap.Round)
        drawRect(color, p(9.7f, 17.6f), Size(4.6f * k, 5.4f * k))
    }

    /** Un lado de la tarjeta pintado con el dedo: de [a] a [b], temblando, en pocos tramos curvos (a lo más 14). */
    private fun pasadaDedo(a: Offset, b: Offset, s: Int, temblor: Float): Path {
        val largo = (b - a).getDistance()
        val n = (largo / maxOf(30 * temblor, 1f)).toInt().coerceIn(1, 14)
        val nx = -(b.y - a.y) / maxOf(largo, 1f); val ny = (b.x - a.x) / maxOf(largo, 1f)
        return Path().apply {
            moveTo(a.x, a.y)
            for (j in 1..n) {
                val fa = (j - 0.5f) / n; val fb = j.toFloat() / n
                val d = (azar(s, j) - 0.5f) * 2 * temblor
                quadraticTo(a.x + (b.x - a.x) * fa + nx * d, a.y + (b.y - a.y) * fa + ny * d, a.x + (b.x - a.x) * fb, a.y + (b.y - a.y) * fb)
            }
        }
    }

    /**
     * Tarjetas: cuatro cosas de la cueva, distintas entre sí. La cifra de Hoy es un hueso de cuenta, pulido, con las muescas de
     * contar talladas a lo largo del canto de arriba, en grupos de cinco. «Te falta hoy» es la pared pintada: la caliza clara
     * enmarcada por una pasada gruesa de ocre rojo hecha con el dedo, y una mano en positivo arriba a la derecha. La noche es la
     * roca del hogar: la bóveda más honda arriba y, en la esquina, la lumbre con sus chispas y su resplandor. La tarjeta de Vida
     * es una piel curtida estirada entre cuatro estacas, con los agujeros del cordón por el borde. Al tocarlas se hunden y se
     * oscurecen apenas. Destacadas: el hueso se pinta de ocre, la pared se vuelve de ocre amarillo y la lumbre queda en brasas,
     * con los tres puntos de ocre del sello. Un degradado: la lumbre.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> huesoCuenta(e, oscuro)
            TipoTarjeta.PENDIENTE -> paredPintada(e, oscuro)
            TipoTarjeta.NOCHE -> hogar(e, oscuro)
            TipoTarjeta.VIDA -> pielEstirada(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, ROCA_OSC, 0.12f)
    }

    private fun DrawScope.huesoCuenta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 31
        val baja = if (e.presionado) 1 * u else 0f
        val radio = CornerRadius(minOf(13 * u, w * 0.1f), minOf(10 * u, h * 0.12f))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), Offset(0.5f * u, (if (e.presionado) 1.4f else 2.2f) * u), Size(w - 1 * u, h - 2.2f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u + baja, w - 0.5f * u, h - 2.2f * u + baja)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        // Las fibras del hueso, a lo largo, solo cerca de los cantos.
        val fibra = (if (oscuro) ROCA_LUZ else ROCA_OSC).copy(alpha = if (oscuro) 0.14f else 0.2f)
        for (j in 0 until 3) {
            val ya = r.top + (5.5f + j * 1.6f) * u; val yb = r.bottom - (2.5f + j * 1.6f) * u
            val xa = r.left + (8 + 10 * azar(s, j)) * u; val xb = r.right - (8 + 10 * azar(s, j + 9)) * u
            drawLine(fibra, Offset(xa, ya), Offset(xb, ya + 0.4f * u), 0.6f * u)
            drawLine(fibra, Offset(xa + 6 * u, yb), Offset(xb - 4 * u, yb - 0.3f * u), 0.6f * u)
        }
        // Las muescas, en grupos de cinco, a lo largo del canto de arriba.
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.5f) else OCRE
        val corte = if (e.destacado) ocre else (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.32f else 0.42f)
        val luz = Color.White.copy(alpha = if (oscuro) 0.06f else 0.5f)
        val paso = maxOf(3.4f * u, 1f)
        val x0 = r.left + 14 * u; val x1 = r.right - 14 * u
        var x = x0; var k = 0
        while (x < x1 && k < 120) {
            val largo = (if (k % 5 == 4) 4.6f else 3.4f + azar(s, k + 40) * 0.8f) * u
            drawLine(luz, Offset(x + 0.7f * u, r.top + 1 * u), Offset(x + 0.9f * u, r.top + 1 * u + largo), 0.6f * u)
            drawLine(corte, Offset(x, r.top + 0.8f * u), Offset(x + 0.2f * u, r.top + 0.8f * u + largo), (if (e.destacado) 1.2f else 0.9f) * u, StrokeCap.Round)
            x += paso + if (k % 5 == 4) paso * 1.4f else 0f
            k++
        }
        drawRoundRect((if (oscuro) Color.Black else ROCA_HONDA).copy(alpha = if (oscuro) 0.4f else 0.22f), r.topLeft, r.size, radio, style = Stroke(0.8f * u))
        if (e.destacado) {
            drawRoundRect(ocre.copy(alpha = 0.85f), r.topLeft, r.size, radio, style = Stroke(1.5f * u))
            // Los tres puntos de ocre, abajo a la izquierda, junto al canto.
            for (j in 0 until 3) drawCircle(ocre, (1.2f + 0.3f * azar(s, 60 + j)) * u, Offset(r.left + (4.5f + (azar(s, 70 + j) - 0.5f)) * u, r.bottom - (9 + j * 4.5f) * u))
        }
    }

    private fun DrawScope.paredPintada(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 47
        val completo = e.destacado
        val r = Rect(0f, 0f, w, h)
        val puntos = ((w + h) * 2 / (16 * u)).toInt().coerceIn(16, 160)
        val pared = contornoIrregular(r, s, puntos, 0.8f * u / minOf(w, h))
        drawPath(pared, fondoTocado(e.tipo, e.presionado, oscuro))
        // El grano de la caliza, solo junto a los bordes.
        val grano = (if (oscuro) ROCA_LUZ else ROCA_OSC).copy(alpha = if (oscuro) 0.14f else 0.22f)
        val granos = ((w + h) / (3 * u)).toInt().coerceIn(20, 320)
        for (j in 0 until granos) {
            val f = azar(s, j); val dentro = (1 + 5 * azar(s, j + 400)) * u
            val p = when (j % 4) {
                0 -> Offset(w * f, dentro); 1 -> Offset(w * f, h - dentro)
                2 -> Offset(dentro, h * f); else -> Offset(w - dentro, h * f)
            }
            drawCircle(grano, (0.4f + 0.5f * azar(s, j + 800)) * u, p)
        }
        // La pasada de ocre con el dedo, por los cuatro lados: gruesa y roja si falta algo; fina y amarilla si está completo.
        val ocre = when {
            completo -> if (oscuro) mezcla(OCRE_AMARILLO, FUEGO, 0.3f) else OCRE_AMARILLO
            oscuro -> mezcla(OCRE, BRASA, 0.35f)
            else -> mezcla(OCRE, HEMATITES, 0.4f)
        }
        val m = 4.5f * u
        val esq = listOf(Offset(m, m), Offset(w - m, m), Offset(w - m, h - m), Offset(m, h - m))
        val grueso = (if (completo) 2.6f else 4.6f) * u
        for (j in 0 until 4) {
            val a = esq[j]; val b = esq[(j + 1) % 4]
            drawPath(pasadaDedo(a, b, s + j * 13, 1.5f * u), ocre.copy(alpha = 0.88f), style = Stroke(grueso, cap = StrokeCap.Round))
            drawPath(pasadaDedo(a + Offset(1.4f * u, 1.2f * u), b - Offset(1f * u, 0.8f * u), s + j * 13 + 7, 2.2f * u), ocre.copy(alpha = 0.4f), style = Stroke(grueso * 0.45f, cap = StrokeCap.Round))
        }
        // La mano, arriba a la derecha, apoyada en la pared.
        val lado = minOf(26 * u, h - 14 * u, w * 0.2f)
        if (lado > 10 * u) {
            val k = lado / 24f
            val o = Offset(w - lado - 9 * u, 6 * u + (if (completo) (h - 12 * u - lado) / 2 else 0f))
            if (!completo) {
                // El soplo de ocre alrededor de la mano.
                for (j in 0 until 140) {
                    val x = 24f * azar(s, j + 900); val y = 24f * azar(s, j + 1300)
                    if (!enMano(x, y, 1.6f) || enMano(x, y, 0.3f)) continue
                    drawCircle(ocre.copy(alpha = 0.5f), (0.3f + 0.25f * azar(s, j + 1700)) * k, Offset(o.x + x * k, o.y + y * k))
                }
            }
            mano(o, k, ocre.copy(alpha = if (completo) 0.9f else 0.8f))
        }
        if (completo) for (j in 0 until 3) drawCircle(ocre, (1.5f + 0.3f * azar(s, 30 + j)) * u, Offset(w - 46 * u + j * 6 * u, h / 2 + (azar(s, 40 + j) - 0.5f) * 1.4f * u))
    }

    private fun DrawScope.hogar(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 59
        val sellado = e.destacado
        val radio = CornerRadius(10 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u), radio)
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        // La bóveda: una franja más honda arriba, de borde irregular.
        val boveda = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f)
            val n = (w / maxOf(26 * u, 1f)).toInt().coerceIn(2, 30)
            lineTo(w, 4.5f * u)
            for (j in n - 1 downTo 0) {
                val x = w * j / n
                quadraticTo(w * (j + 0.5f) / n, (5.5f + 2.2f * azar(s, j)) * u, x, (3.5f + 1.5f * azar(s, j + 40)) * u)
            }
            close()
        }
        clipPath(Path().apply { addRoundRect(RoundRect(r, radio)) }) {
            drawPath(boveda, (if (oscuro) Color.Black else ROCA_OSC).copy(alpha = if (oscuro) 0.35f else 0.3f))
        }
        // La lumbre, en la esquina de arriba a la derecha, con su resplandor.
        val z = 1.45f
        val fx = w - 26 * u; val fy = minOf(19 * u, h * 0.24f)
        clipPath(Path().apply { addRoundRect(RoundRect(r, radio)) }) {
            mancha(Offset(fx, fy), (if (sellado) 22 else 34) * u, BRASA.copy(alpha = if (oscuro) (if (sellado) 0.28f else 0.42f) else (if (sellado) 0.18f else 0.3f)))
        }
        val lena = if (oscuro) mezcla(CARBON, ROCA_OSC, 0.4f) else CARBON
        drawLine(lena, Offset(fx - 7 * z * u, fy + 3.5f * z * u), Offset(fx + 6 * z * u, fy + 1 * z * u), 1.8f * z * u, StrokeCap.Round)
        drawLine(lena, Offset(fx - 6 * z * u, fy + 0.8f * z * u), Offset(fx + 7 * z * u, fy + 3.6f * z * u), 1.8f * z * u, StrokeCap.Round)
        if (sellado) {
            for (j in 0 until 5) drawCircle((if (j % 2 == 0) BRASA else FUEGO).copy(alpha = 0.9f), (0.7f + 0.4f * azar(s, j)) * u, Offset(fx - 4 * z * u + j * 2 * z * u, fy + (1.6f + azar(s, j + 5)) * u))
        } else {
            fun llama(cx: Float, alto: Float, ancho: Float, c: Color) = drawPath(Path().apply {
                moveTo(cx, fy + 1.5f * z * u - alto)
                quadraticTo(cx + ancho * 1.1f, fy - alto * 0.3f, cx + ancho * 0.5f, fy + 1.5f * z * u)
                lineTo(cx - ancho * 0.5f, fy + 1.5f * z * u)
                quadraticTo(cx - ancho * 1.1f, fy - alto * 0.3f, cx, fy + 1.5f * z * u - alto)
                close()
            }, c)
            llama(fx - 2.5f * z * u, 8 * z * u, 3.5f * z * u, OCRE.copy(alpha = 0.9f))
            llama(fx + 2.5f * z * u, 9 * z * u, 3.5f * z * u, BRASA)
            llama(fx, 11 * z * u, 4 * z * u, BRASA)
            llama(fx, 7 * z * u, 2.2f * z * u, FUEGO)
            // Las chispas que suben.
            for (j in 0 until 4) drawCircle(FUEGO.copy(alpha = 0.55f + 0.3f * azar(s, j + 20)), (0.5f + 0.3f * azar(s, j + 30)) * u,
                Offset(fx + (azar(s, j + 40) - 0.5f) * 14 * z * u, fy - (15 + 4 * azar(s, j + 50)) * u))
        }
        if (sellado) {
            val ocre = if (oscuro) mezcla(OCRE_AMARILLO, FUEGO, 0.3f) else OCRE_AMARILLO
            drawRoundRect(ocre.copy(alpha = 0.85f), Offset(1 * u, 1 * u), Size(w - 2 * u, r.height - 2 * u), CornerRadius(9 * u), style = Stroke(1.6f * u))
            val rojo = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.5f) else OCRE
            for (j in 0 until 3) drawCircle(rojo, (1.5f + 0.3f * azar(s, 60 + j)) * u, Offset(w - 22 * u + j * 5.5f * u, r.bottom - 6 * u + (azar(s, 70 + j) - 0.5f) * 1.2f * u))
        } else drawRoundRect((if (oscuro) Color.Black else ROCA_HONDA).copy(alpha = if (oscuro) 0.4f else 0.2f), r.topLeft, r.size, radio, style = Stroke(0.8f * u))
    }

    private fun DrawScope.pielEstirada(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 71
        // Los lados se meten hacia adentro entre estaca y estaca; tocada, la piel cede un poco más.
        val hundir = (if (e.presionado) 3.2f else 2f) * u
        val m = 3 * u
        val a = Offset(m, m); val b = Offset(w - m, m); val c = Offset(w - m, h - m); val d = Offset(m, h - m)
        val piel = Path().apply {
            moveTo(a.x, a.y)
            quadraticTo(w / 2, m + hundir, b.x, b.y)
            quadraticTo(w - m - hundir, h / 2, c.x, c.y)
            quadraticTo(w / 2, h - m - hundir, d.x, d.y)
            quadraticTo(m + hundir, h / 2, a.x, a.y)
            close()
        }
        translate(0f, 1.2f * u) { drawPath(piel, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f)) }
        drawPath(piel, fondoTocado(e.tipo, e.presionado, oscuro))
        val borde = if (oscuro) mezcla(PIEL, CALIZA, 0.2f) else mezcla(PIEL, CARBON, 0.45f)
        drawPath(piel, borde.copy(alpha = 0.7f), style = Stroke(1 * u))
        // Las estacas en las cuatro esquinas, con el cordón que tira de cada una.
        val estaca = if (oscuro) mezcla(ROCA_OSC, CALIZA, 0.2f) else ROCA_HONDA
        listOf(a, b, c, d).forEach { p ->
            val q = Offset(if (p.x < w / 2) 1.4f * u else w - 1.4f * u, if (p.y < h / 2) 1.4f * u else h - 1.4f * u)
            drawLine(borde, p, q, 0.8f * u)
            drawCircle(estaca, 1.3f * u, q)
        }
        // Los agujeros del cordón por el borde, a trechos parejos, siguiendo la curva de cada lado.
        val hueco = (if (oscuro) Color.Black else CARBON).copy(alpha = if (oscuro) 0.6f else 0.5f)
        val paso = maxOf(11 * u, 1f)
        fun agujeros(desde: Offset, hasta: Offset, adentro: Offset) {
            val largo = (hasta - desde).getDistance()
            val n = (largo / paso).toInt().coerceIn(1, 40)
            for (j in 1 until n) {
                val f = j.toFloat() / n
                val curva = 4 * f * (1 - f) * hundir
                val p = desde + (hasta - desde) * f + adentro * (curva + 3.2f * u)
                drawCircle(hueco, 0.75f * u, p)
                drawLine(borde.copy(alpha = 0.5f), p, p - adentro * 3f * u, 0.6f * u)
            }
        }
        agujeros(a, b, Offset(0f, 1f)); agujeros(b, c, Offset(-1f, 0f)); agujeros(c, d, Offset(0f, -1f)); agujeros(d, a, Offset(1f, 0f))
        if (e.destacado) for (j in 0 until 3) drawCircle(if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.5f) else OCRE, 1.3f * u, Offset(w - 16 * u + j * 4.5f * u, h - 9 * u))
    }

    // ---------------------------------------------------------------- Gastos

    override val altoBalanza: Dp = 58.dp

    /** La cara de una mitad de la laja de la balanza: la elegida, alisada y clara; la otra, roca sin pulir. */
    private fun caraBalanza(elegida: Boolean, oscuro: Boolean): Color = when {
        elegida && oscuro -> mezcla(CALIZA, Color(0xFF0F0B08), 0.7f)
        elegida -> mezcla(CALIZA, Color.White, 0.18f)
        oscuro -> mezcla(ROCA_HONDA, Color(0xFF0F0B08), 0.62f)
        else -> mezcla(CALIZA, ROCA, 0.42f)
    }

    /**
     * Balanza: una laja de caliza a todo el ancho, partida al medio por una grieta pintada de carbón. La mitad elegida es la
     * cara alisada a mano, clara, rodeada por una pasada de ocre hecha con el dedo, con una mano en positivo en su orilla y,
     * de noche, la luz de la lumbre encima; su texto va grueso. La otra mitad se queda en roca sin pulir, más apagada, con su
     * grano de carbón, y el texto delgado (pero con contraste de sobra). Un degradado: la lumbre.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val laja = Rect(1 * u, 1.5f * u, w - 1 * u, h - 1 * u)
        val contorno = contornoIrregular(laja, 31, ((laja.width + laja.height) * 2 / (9 * u)).toInt().coerceIn(16, 140), 2.2f * u / laja.height)
        drawPath(contorno, if (oscuro) mezcla(ROCA_HONDA, Color(0xFF0F0B08), 0.4f) else ROCA)
        drawPath(contorno, (if (oscuro) Color.Black else ROCA_HONDA).copy(alpha = 0.5f), style = Stroke(1 * u))
        val xm = w / 2
        for (lado in 0..1) {
            val elegida = (lado == 1) == e.ingreso
            val x0 = if (lado == 0) laja.left + 6 * u else xm + 5 * u
            val x1 = if (lado == 0) xm - 5 * u else laja.right - 6 * u
            val r = Rect(x0, laja.top + 5 * u, x1, laja.bottom - 5 * u)
            val fondo = caraBalanza(elegida, oscuro)
            val cara = contornoIrregular(r, 40 + lado, ((r.width + r.height) * 2 / (10 * u)).toInt().coerceIn(12, 90), 1.6f * u / r.height)
            drawPath(cara, fondo)
            var luz = fondo
            if (elegida) {
                if (oscuro) {
                    luz = mezcla(fondo, BRASA, 0.16f)
                    drawPath(cara, Brush.radialGradient(listOf(BRASA.copy(alpha = 0.16f), Color.Transparent), Offset(r.center.x, r.bottom), r.width * 0.55f))
                }
                val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
                drawPath(contornoIrregular(r.inflate(1.2f * u), 50 + lado, ((r.width + r.height) * 2 / (12 * u)).toInt().coerceIn(12, 80), 1.4f * u / r.height),
                    ocre.copy(alpha = 0.9f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                val k = (r.height * 0.72f) / 24f
                val mx = if (lado == 0) r.left + 4 * u else r.right - 4 * u - 24 * k
                mano(Offset(mx, r.center.y - 12 * k), k, ocre.copy(alpha = if (oscuro) 0.55f else 0.42f))
            } else {
                val grano = (if (oscuro) ROCA_LUZ else ROCA_HONDA).copy(alpha = if (oscuro) 0.16f else 0.22f)
                for (j in 0 until 22) drawCircle(grano, (0.5f + 0.7f * azar(60 + lado, j)) * u,
                    Offset(r.left + r.width * azar(70 + lado, j), r.top + r.height * azar(80 + lado, j)))
            }
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val margen = r.height * 0.95f
            val caja = Rect(r.left + margen, r.top + 2 * u, r.right - margen, r.bottom - 2 * u)
            if (elegida) textoEn(medidor, texto, caja, LetraExtra.trazo, legibleAnt(base, 5f, fondo, luz), 23f, peso = FontWeight.Bold, espaciado = 0.5f)
            else textoEn(medidor, texto, caja, LetraExtra.trazo, legibleAnt(mezcla(base, fondo, 0.22f), 4.7f, fondo), 20f, espaciado = 0.5f)
        }
        // La grieta del medio, pintada de carbón.
        val carbon = (if (oscuro) Color.Black else CARBON).copy(alpha = 0.7f)
        val grieta = Path().apply {
            moveTo(xm + 0.6f * u, laja.top + 1 * u)
            for (j in 1..5) lineTo(xm + (azar(33, j) - 0.5f) * 3.2f * u, laja.top + (laja.height - 2 * u) * j / 5f + 1 * u)
        }
        drawPath(grieta, carbon, style = Stroke(1.6f * u, cap = StrokeCap.Round))
    }

    override val altoMes: Dp = 52.dp

    /**
     * Encabezado del mes: un saliente de roca a todo el ancho, con la cara del medio alisada para el nombre del mes, escrito
     * grande con carbón, y tres puntos de ocre a cada lado. En los extremos, sobre la roca, las puntas de sílex de las filas,
     * grandes, una de ida y otra de vuelta. Sin degradados.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 20 * u) return
        val roca = Rect(1 * u, 2 * u, w - 1 * u, h - 1.5f * u)
        val contorno = contornoIrregular(roca, 91, ((roca.width + roca.height) * 2 / (9 * u)).toInt().coerceIn(16, 140), 2.4f * u / roca.height)
        drawPath(contorno, if (oscuro) mezcla(ROCA_HONDA, Color(0xFF0F0B08), 0.35f) else mezcla(ROCA, ROCA_LUZ, 0.35f))
        drawPath(contorno, (if (oscuro) Color.Black else ROCA_HONDA).copy(alpha = 0.5f), style = Stroke(1 * u))
        val r = Rect(52 * u, roca.top + 4 * u, w - 52 * u, roca.bottom - 4 * u)
        val fondo = caraBalanza(true, oscuro)
        drawPath(contornoIrregular(r, 92, ((r.width + r.height) * 2 / (10 * u)).toInt().coerceIn(12, 100), 1.5f * u / r.height), fondo)
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
        for (lado in listOf(-1f, 1f)) for (j in 0..2) {
            val x = if (lado < 0) r.left + (7 + j * 5.5f) * u else r.right - (7 + j * 5.5f) * u
            drawCircle(ocre.copy(alpha = 0.85f), (1.5f + 0.3f * azar(93, j)) * u, Offset(x, r.center.y + (azar(94, j + (lado * 3).toInt()) - 0.5f) * 1.6f * u))
        }
        textoEn(medidor, e.titulo, Rect(r.left + 26 * u, r.top, r.right - 26 * u, r.bottom), LetraExtra.trazo,
            legibleAnt(if (oscuro) CALIZA else CARBON, 7f, fondo), 30f, peso = FontWeight.Bold, espaciado = 1f)
        flechasMesAnt(48 * u) { flechaFila(oscuro) }
    }

    /**
     * Asiento: casi nada sobre la roca: un trazo de carbón muy fino abajo, que tiembla, y al principio de ese trazo la marca
     * del tipo: una pasada de ocre con el dedo si es un gasto, o dos rayas de cuenta de carbón (de caliza, de noche) si es un
     * ingreso. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 8 * u) return
        val s = e.semilla
        val y = h - 1.2f * u
        trazoCarbon(26 * u, w - 4 * u, y, s, (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.16f else 0.2f), 0.9f * u, 1.2f * u)
        if (e.ingreso) {
            val c = (if (oscuro) CALIZA else CARBON).copy(alpha = 0.62f)
            for (j in 0..1) {
                val x = (4 + j * 5) * u + azar(s, 90 + j) * u
                drawLine(c, Offset(x, y + 1 * u), Offset(x + 1.6f * u, y - 6 * u), 1.4f * u, StrokeCap.Round)
            }
        } else {
            val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
            val p = Path().apply { moveTo(2 * u, y + 0.4f * u); quadraticTo(11 * u, y - 1.6f * u, 21 * u, y - 0.2f * u) }
            drawPath(p, ocre.copy(alpha = 0.72f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        }
    }

    override val altoRotuloDia: Dp = 24.dp

    /**
     * Rótulo del día: la fecha pintada con ocre tostado (de noche, con el color de la lumbre) sobre una lajita de caliza alisada,
     * y de ahí hacia la derecha un trazo de carbón que acaba en los tres puntos de ocre. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 10 * u) return
        val tam = 17f
        val ancho = minOf(anchoTextoAnt(medidor, texto, LetraExtra.trazo, tam, FontWeight.Bold, 1.2f) + 2 * u, w * 0.7f)
        val r = Rect(1 * u, 1 * u, ancho + 15 * u, h - 1 * u)
        val fondo = if (oscuro) mezcla(CALIZA, Color(0xFF0F0B08), 0.74f) else mezcla(CALIZA, Color.White, 0.25f)
        val laja = contornoIrregular(r, texto.hashCode(), ((r.width + r.height) * 2 / (8 * u)).toInt().coerceIn(10, 80), 1.3f * u / r.height)
        drawPath(laja, fondo)
        drawPath(laja, (if (oscuro) ROCA_LUZ else ROCA_OSC).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        textoEn(medidor, texto, Rect(r.left + 7 * u, r.top, r.left + 7 * u + ancho, r.bottom), LetraExtra.trazo,
            legibleAnt(if (oscuro) FUEGO else TOSTADO, 5f, fondo), tam, peso = FontWeight.Bold, espaciado = 1.2f)
        val x1 = w - 22 * u
        trazoCarbon(r.right + 6 * u, x1, h / 2, texto.hashCode(), (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.3f else 0.4f), 1.1f * u, 1.6f * u)
        val ocre = (if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE).copy(alpha = 0.85f)
        for (j in 0..2) drawCircle(ocre, 1.5f * u, Offset(x1 + (5 + j * 5) * u, h / 2))
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * La altura, en [x], del borde de arriba de la hoja rápida (el de su forma: la laja partida, tres curvas que ondulan y quedan
     * más altas a la derecha), en píxeles; [u] es la densidad. Cada curva se resuelve por bisección, en 16 pasos.
     */
    private fun bordeLaja(x: Float, w: Float, u: Float): Float {
        val q = when {
            x <= 0.45f * w -> floatArrayOf(0f, 14f, 0.2f * w, 2f, 0.45f * w, 8f)
            x <= 0.86f * w -> floatArrayOf(0.45f * w, 8f, 0.7f * w, 14f, 0.86f * w, 4f)
            else -> floatArrayOf(0.86f * w, 4f, 0.95f * w, 0f, w, 10f)
        }
        var a = 0f; var b = 1f
        repeat(16) {
            val m = (a + b) / 2
            if ((1 - m) * (1 - m) * q[0] + 2 * (1 - m) * m * q[2] + m * m * q[4] < x) a = m else b = m
        }
        val t = (a + b) / 2
        return ((1 - t) * (1 - t) * q[1] + 2 * (1 - t) * t * q[3] + t * t * q[5]) * u
    }

    /**
     * Hoja rápida: bajo el borde de la laja, un trazo de carbón que sigue su perfil y una hilera de puntos de ocre que se abre al
     * centro para una mano chica en positivo; arriba a la izquierda, una mano en negativo (la roca desnuda con el ocre soplado
     * alrededor) y debajo, por ese canto, puntos de ocre cada vez más tenues; por el canto derecho, rayas de cuenta de carbón en
     * grupos de cinco; abajo, tres puntos de ocre a la izquierda y una cuenta más a la derecha. Un degradado: el soplo de la mano.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        val carbon = alfaAnt(e.secundario, if (oscuro) 0.4f else 0.45f)
        val ocre = alfaAnt(e.primario, 0.6f)
        val hueco = 18 * u
        // El trazo de carbón bajo el borde, cortado al centro, donde va la mano.
        listOf(22 * u to w / 2 - hueco, w / 2 + hueco to w - 22 * u).forEach { (x0, x1) ->
            if (x1 - x0 < 8 * u) return@forEach
            val n = ((x1 - x0) / maxOf(6 * u, 1f)).toInt().coerceIn(2, 120)
            val p = Path()
            for (j in 0..n) {
                val x = x0 + (x1 - x0) * j / n
                val y = bordeLaja(x, w, u) + 7 * u + (azar(31, j) - 0.5f) * 0.9f * u
                if (j == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            drawPath(p, carbon, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        }
        // La hilera de puntos de ocre, más abajo, que sigue también el borde.
        var x = 26 * u; var k = 0
        while (x < w - 26 * u && k < 80) {
            if (kotlin.math.abs(x - w / 2) > hueco) drawCircle(ocre, (1.2f + (k % 3) * 0.4f) * u, Offset(x, minOf(bordeLaja(x, w, u) + 13 * u, 25 * u)))
            x += maxOf(11 * u, 1f); k++
        }
        // La mano chica en positivo, al centro.
        mano(Offset(w / 2 - 12.4f * 0.6f * u, bordeLaja(w / 2, w, u) + 1.5f * u), 0.6f * u, alfaAnt(e.primario, 0.85f))
        // La mano en negativo arriba a la izquierda: el ocre soplado alrededor y la roca desnuda donde estuvo la mano.
        val cn = Offset(9 * u, 31 * u)
        drawCircle(Brush.radialGradient(listOf(alfaAnt(e.primario, 0.65f), alfaAnt(e.primario, 0.3f), Color.Transparent), cn, 13 * u), 13 * u, cn)
        mano(Offset(cn.x - 12.4f * 0.6f * u, cn.y - 14f * 0.6f * u), 0.6f * u, e.fondo.copy(alpha = 0.92f))
        // Por el canto izquierdo, puntos de ocre que se desvanecen.
        val nPuntos = ((h * 0.4f) / maxOf(16 * u, 1f)).toInt().coerceIn(0, 14)
        for (j in 0 until nPuntos) drawCircle(alfaAnt(e.primario, 0.5f * (1f - j / (nPuntos + 1f))), (1.5f - (j % 2) * 0.4f) * u, Offset((8 + (j % 2) * 1.5f) * u, 50 * u + j * 16 * u))
        // Por el canto derecho, rayas de cuenta: dos grupos de cuatro con la quinta cruzada.
        fun cuenta(y0: Float, rayas: Int, cruzada: Boolean) {
            for (j in 0 until rayas) {
                val y = y0 + j * 3.6f * u
                drawLine(carbon, Offset(w - 13 * u, y + (azar(7, j) - 0.5f) * u), Offset(w - 5 * u, y + 0.6f * u), 1.1f * u, StrokeCap.Round)
            }
            if (cruzada) drawLine(carbon, Offset(w - 14 * u, y0 - 1.5f * u), Offset(w - 4.5f * u, y0 + (rayas - 1) * 3.6f * u + 1.5f * u), 1f * u, StrokeCap.Round)
        }
        cuenta(42 * u, 4, true); cuenta(64 * u, 4, true)
        if (h > 260 * u) cuenta(h - 40 * u, 3, false)
        // Abajo a la izquierda, tres puntos de ocre puestos con el dedo.
        listOf(Offset(7 * u, h - 16 * u), Offset(12.5f * u, h - 12 * u), Offset(7.5f * u, h - 8.5f * u))
            .forEachIndexed { j, o -> drawCircle(alfaAnt(e.primario, 0.7f), (2f - j * 0.2f) * u, o) }
    }

    /**
     * Relación: un canto rodado pintado. Tres puntos de ocre arriba y abajo, al centro; una mano chica en la esquina de arriba a la
     * derecha; por el canto izquierdo, un trazo de carbón que tiembla, y abajo a la izquierda, cuatro rayas de cuenta con la quinta
     * cruzada. Al centro, apenas un soplo de ocre. Un degradado: ese soplo.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        mancha(center, minOf(w, h) * 0.55f, alfaAnt(e.primario, 0.08f))
        val ocre = alfaAnt(e.primario, 0.7f)
        listOf(4.5f * u, h - 4.5f * u).forEach { y -> for (k in -1..1) drawCircle(ocre, (if (k == 0) 1.8f else 1.4f) * u, Offset(w / 2 + k * 5 * u, y)) }
        mano(Offset(w - 13 * u, 2 * u), 0.38f * u, alfaAnt(e.primario, 0.55f))
        val carbon = alfaAnt(e.secundario, if (oscuro) 0.4f else 0.45f)
        val p = Path().apply {
            moveTo(4 * u, 16 * u)
            val n = ((h - 30 * u) / maxOf(10 * u, 1f)).toInt().coerceIn(1, 12)
            for (j in 1..n) {
                val y0 = 16 * u + (h - 30 * u) * (j - 0.5f) / n; val y1 = 16 * u + (h - 30 * u) * j / n
                quadraticTo(4 * u + (azar(13, j) - 0.5f) * 2.4f * u, y0, 4 * u + (azar(29, j) - 0.5f) * 0.8f * u, y1)
            }
        }
        drawPath(p, carbon, style = Stroke(1f * u, cap = StrokeCap.Round))
        for (j in 0 until 4) drawLine(carbon, Offset(10 * u + j * 3 * u, h - 11 * u), Offset(10.6f * u + j * 3 * u, h - 5.5f * u), 0.9f * u, StrokeCap.Round)
        drawLine(carbon, Offset(8.5f * u, h - 6.5f * u), Offset(21 * u, h - 10 * u), 0.8f * u, StrokeCap.Round)
    }

    /**
     * Pista: un canto rodado, un óvalo apenas torcido con su brillo arriba a la izquierda, y abajo los tres puntos de ocre en el
     * color pleno. Sin degradados.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f
        val x0 = (size.width - l) / 2; val y0 = (size.height - l) / 2
        fun p(x: Float, y: Float) = Offset(x0 + x * l, y0 + y * l)
        val canto = Path().apply {
            p(0.5f, 0.05f).let { moveTo(it.x, it.y) }
            fun c(a: Offset, b: Offset, d: Offset) = cubicTo(a.x, a.y, b.x, b.y, d.x, d.y)
            c(p(0.8f, 0.04f), p(0.96f, 0.26f), p(0.95f, 0.52f))
            c(p(0.94f, 0.8f), p(0.74f, 0.95f), p(0.48f, 0.95f))
            c(p(0.2f, 0.95f), p(0.05f, 0.76f), p(0.06f, 0.5f))
            c(p(0.07f, 0.24f), p(0.22f, 0.06f), p(0.5f, 0.05f))
            close()
        }
        drawPath(canto, e.primario, style = Stroke(g))
        val brillo = Path().apply { p(0.12f, 0.34f).let { moveTo(it.x, it.y) }; p(0.17f, 0.14f).let { a -> p(0.34f, 0.1f).let { b -> quadraticTo(a.x, a.y, b.x, b.y) } } }
        drawPath(brillo, alfaAnt(e.primario, 0.7f), style = Stroke(g * 0.6f, cap = StrokeCap.Round))
        for (k in -1..1) drawCircle(e.secundario, l * 0.042f, p(0.5f + k * 0.13f, 0.865f))
    }
}

/**
 * Roma: el campo es una tablilla de cera (tabula cerata): el marco de haya con sus vetas y, en el rebaje, la cera lisa, color
 * miel de día y ennegrecida de noche. A la izquierda, en el marco, los dos agujeros con el cordón de lino que la une a las
 * otras hojas; a la derecha, dos clavillos de bronce. Con el foco el borde de la cera se marca de púrpura; con un error, de
 * minio. Con varios renglones, las rayas trazadas con el estilo. Sin degradados.
 */
object ComunRomano : DisenoComun {
    private val HAYA = Color(0xFFCDA670)
    private val HAYA_LUZ = Color(0xFFE2C48E)
    private val HAYA_VETA = Color(0xFF7A5630)
    private val CERA_NEGRA = Color(0xFF2E241C)
    private val CERA_RAYA = Color(0xFFB59A70)
    /** La cera de abeja sin ennegrecer, la de la tablilla de día. */
    private val CERA_MIEL = Color(0xFFDDCA9C)
    private val LINO = Color(0xFFEDE4CC)
    private val NEGRO = Color(0xFF1E1714)
    private val BRONCE = Color(0xFFB98B4A)
    private val BRONCE_OSC = Color(0xFF6E4E22)
    private val BRONCE_LUZ = Color(0xFFE0B878)
    private val MINIO = Color(0xFFB0302A)
    private val PURPURA = Color(0xFF5B2A4E)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100D0B), 0.36f) else c
    /** La cera del rebaje, donde va el texto. */
    private fun cera(oscuro: Boolean): Color = if (oscuro) mezcla(CERA_NEGRA, Color(0xFF100D0B), 0.3f) else CERA_MIEL

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), cera(oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(PURPURA, LINO, 0.62f) else PURPURA
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07A64) else mezcla(MINIO, NEGRO, 0.15f)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val cera = cera(oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, cera, 0.45f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, CornerRadius(3 * u))
        // El marco de haya.
        drawRoundRect(a(t(HAYA, oscuro)), r.topLeft, r.size, CornerRadius(3 * u))
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, a(t(HAYA_VETA, oscuro)).copy(alpha = 0.4f), s, 0.8f) }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.06f else 0.28f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        // El rebaje con la cera, lisa, y su hondura: sombra arriba, luz abajo.
        val c = Rect(r.left + 7 * u, r.top + 4.5f * u, r.right - 5 * u, r.bottom - 4.5f * u)
        drawRect(cera, c.topLeft, c.size)
        bisel(c, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), 1.3f * u)
        // Las rayas trazadas con el estilo: la cera abierta deja ver el fondo.
        if (e.multilinea) renglonesAntiguedad(c.left + 6 * u, c.right - 6 * u, margenCampoY.toPx(), c.bottom - 3 * u,
            if (oscuro) CERA_RAYA.copy(alpha = 0.22f) else CERA_NEGRA.copy(alpha = 0.16f), 0.8f * u,
            luz = Color.White.copy(alpha = if (oscuro) 0.03f else 0.3f))
        // Los dos agujeros del cordón, en el marco de la izquierda, y el cordón de lino.
        val hx = r.left + 3.4f * u
        val ya = c.top + 6 * u; val yb = c.bottom - 6 * u
        if (yb > ya) {
            drawPath(Path().apply { moveTo(hx, ya); quadraticTo(r.left - 0.6f * u, (ya + yb) / 2, hx, yb) },
                a(t(LINO, oscuro)), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        }
        listOf(ya, yb).forEach { y ->
            drawCircle(a(t(Color(0xFF3A2814), oscuro)), 1.9f * u, Offset(hx, y))
            drawCircle(a(t(LINO, oscuro)), 0.9f * u, Offset(hx, y))
        }
        // Los clavillos de bronce de la derecha.
        val clavo = if (e.enfocado && e.habilitado) BRONCE_LUZ else BRONCE
        listOf(r.top + 2.4f * u, r.bottom - 2.4f * u).forEach { y ->
            val o = Offset(r.right - 2.5f * u, y)
            drawCircle(a(t(BRONCE_OSC, oscuro)), 1.8f * u, o)
            drawCircle(a(t(clavo, oscuro)), 1.3f * u, o)
            drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(o.x - 0.4f * u, o.y - 0.4f * u))
        }
        // El borde de la cera: púrpura con el foco, minio con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            e.enfocado && e.habilitado -> rotuloCampo(oscuro)
            else -> null
        }
        if (linea != null) {
            drawRect(linea, Offset(c.left + 0.9f * u, c.top + 0.9f * u), Size(c.width - 1.8f * u, c.height - 1.8f * u), style = Stroke(1.8f * u))
            drawLine(if (oscuro) BRONCE_LUZ.copy(alpha = 0.5f) else HAYA_LUZ, Offset(r.left + 7 * u, r.top + 0.8f * u), Offset(r.right - 6 * u, r.top + 0.8f * u), 0.8f * u)
        }
        // El ensayo del dedo: con el campo vacío, la cera recién alisada brilla un poco más.
        if (e.vacio && !oscuro) drawLine(Color.White.copy(alpha = 0.18f), Offset(c.left + 4 * u, c.top + 3 * u), Offset(c.right - 4 * u, c.top + 3 * u), 1.2f * u)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * La hoja de hiedra (hedera distinguenda) de las inscripciones, acostada con la punta hacia la derecha, de unos 8 dp de
     * largo con su tallo enroscado, centrada en [c].
     */
    private fun DrawScope.hedera(c: Offset, color: Color, u: Float) {
        val x = c.x; val y = c.y
        val hoja = Path().apply {
            moveTo(x + 4 * u, y)
            cubicTo(x + 1 * u, y - 1 * u, x - 1 * u, y - 4 * u, x - 3 * u, y - 2.6f * u)
            cubicTo(x - 4.4f * u, y - 1.6f * u, x - 3.6f * u, y - 0.3f * u, x - 2.3f * u, y)
            cubicTo(x - 3.6f * u, y + 0.3f * u, x - 4.4f * u, y + 1.6f * u, x - 3 * u, y + 2.6f * u)
            cubicTo(x - 1 * u, y + 4 * u, x + 1 * u, y + 1 * u, x + 4 * u, y)
            close()
        }
        drawPath(hoja, color)
        val tallo = Path().apply { moveTo(x - 2.3f * u, y); quadraticTo(x - 5.5f * u, y - 0.6f * u, x - 6.4f * u, y + 1.6f * u) }
        drawPath(tallo, color, style = Stroke(0.9f * u, cap = StrokeCap.Round))
    }

    /**
     * Separador: la raya grabada de una inscripción (una línea fina con su luz debajo, como cortada en la piedra) y, al centro,
     * la hoja de hiedra entre dos interpuntos triangulares. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) LINO.copy(alpha = 0.2f) else HAYA_VETA.copy(alpha = 0.45f)).let { it.copy(alpha = it.alpha * f) }
        val luz = Color.White.copy(alpha = (if (oscuro) 0.03f else 0.5f) * f)
        val tinta = (if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA).copy(alpha = (if (oscuro) 0.7f else 0.65f) * f)
        val hueco = 16 * u
        listOf(2 * u to w / 2 - hueco, w / 2 + hueco to w - 2 * u).forEach { (a, b) ->
            if (b - a > 2 * u) {
                drawLine(raya, Offset(a, cy), Offset(b, cy), 1 * u)
                drawLine(luz, Offset(a, cy + 1 * u), Offset(b, cy + 1 * u), 0.6f * u)
            }
        }
        hedera(Offset(w / 2 + 0.8f * u, cy), tinta, u)
        listOf(-1f, 1f).forEach { lado ->
            val x = w / 2 + lado * 10.5f * u
            drawPath(Path().apply { moveTo(x - 1.3f * u, cy - 1.1f * u); lineTo(x + 1.3f * u, cy - 1.1f * u); lineTo(x, cy + 1.3f * u); close() }, tinta)
        }
    }

    /** El texto de los botones: el pórfido de día, el oro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD2AE5A) else Color(0xFF7C2433)

    /**
     * Botón de texto: una tabula ansata, la cartela de las inscripciones: su contorno es un hilo púrpura casi invisible y lo que
     * se ve son sus dos asas en cola de milano, rellenas, a los lados de la palabra. Al tocarlo la cartela se llena de un
     * púrpura tenue y su contorno se marca; apagado, casi no se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val color = if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val ansa = minOf(5 * u, w * 0.1f)
        val izq = 3 * u; val der = w - 3 * u
        val cuerpoIzq = izq + ansa; val cuerpoDer = der - ansa
        val arriba = cy - 13 * u; val abajo = cy + 13 * u
        if (cuerpoDer - cuerpoIzq < 4 * u) return
        val cuerpo = Rect(cuerpoIzq, arriba, cuerpoDer, abajo)
        if (tocado) drawRect(color.copy(alpha = if (oscuro) 0.16f else 0.1f), cuerpo.topLeft, cuerpo.size)
        drawRect(color.copy(alpha = (if (tocado) 0.6f else 0.2f) * apagado), cuerpo.topLeft, cuerpo.size, style = Stroke((if (tocado) 1.1f else 0.8f) * u))
        // Las asas: un trapecio que nace angosto en el lado de la cartela y se abre hacia afuera.
        val asas = Path().apply {
            moveTo(cuerpoDer, cy - 2.5f * u); lineTo(der, cy - 6 * u); lineTo(der, cy + 6 * u); lineTo(cuerpoDer, cy + 2.5f * u); close()
            moveTo(cuerpoIzq, cy - 2.5f * u); lineTo(izq, cy - 6 * u); lineTo(izq, cy + 6 * u); lineTo(cuerpoIzq, cy + 2.5f * u); close()
        }
        drawPath(asas, color.copy(alpha = (if (tocado) 0.85f else 0.5f) * apagado))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El barro cocido del ánfora del vacío. */
    private val TERRACOTA = Color(0xFFB8643A)
    /** La piedra de la lápida y de la cara de las teselas: un mármol blanco que tira al lino. */
    private val LAPIDA = Color(0xFFF4F1E8)
    /** La piedra honda de noche. */
    private val LAPIDA_NOCHE = Color(0xFF1B1714)
    /** El oro del número de noche. */
    private val ORO = Color(0xFFD2AE5A)

    /** Un interpunto triangular de las inscripciones, con la punta abajo, de lado [l], centrado en [c]. */
    private fun DrawScope.interpunto(c: Offset, l: Float, color: Color) {
        drawPath(Path().apply { moveTo(c.x - l / 2, c.y - l * 0.42f); lineTo(c.x + l / 2, c.y - l * 0.42f); lineTo(c.x, c.y + l * 0.48f); close() }, color)
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: la raya grabada de las inscripciones abajo (una línea fina con su luz debajo, como cortada en la piedra) y, a la
     * izquierda, un interpunto triangular (dos, si la fila lleva detalle). Al tocarla, la piedra se tiñe de púrpura apenas, el
     * interpunto se vuelve de minio y arriba brilla un hilo de bronce. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val purpura = if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA
        if (e.presionado) {
            drawRect(purpura.copy(alpha = if (oscuro) 0.12f else 0.07f), Offset.Zero, size)
            drawLine((if (oscuro) BRONCE_LUZ else BRONCE).copy(alpha = 0.5f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 0.8f * u)
        }
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) {
            drawLine((if (oscuro) LINO.copy(alpha = 0.14f) else HAYA_VETA.copy(alpha = 0.32f)), Offset(x0, h - 1.5f * u), Offset(w, h - 1.5f * u), 0.8f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.45f), Offset(x0, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.6f * u)
        }
        val tinta = if (e.presionado) (if (oscuro) Color(0xFFF07A64) else MINIO) else purpura.copy(alpha = 0.55f)
        if (e.conDetalle) {
            interpunto(Offset(5.5f * u, h / 2 - 3.2f * u), 3 * u, tinta)
            interpunto(Offset(5.5f * u, h / 2 + 3.2f * u), 3 * u, tinta)
        } else interpunto(Offset(5.5f * u, h / 2), 3.2f * u, tinta)
    }

    /** Flecha: la hoja de hiedra de las inscripciones, más grande, con la punta hacia la derecha y su tallo enroscado. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val k = size.minDimension / 20f * 1.45f
        hedera(Offset(size.width * 0.6f, size.height / 2), if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA, k)
    }

    /** Una hoja de laurel con la base en [b], la punta hacia el ángulo [ang] (en radianes), de [largo] y [ancho]. */
    private fun hojaLaurel(b: Offset, ang: Float, largo: Float, ancho: Float): Path {
        val dx = kotlin.math.cos(ang); val dy = kotlin.math.sin(ang)
        val nx = -dy; val ny = dx
        val m = Offset(b.x + dx * largo * 0.5f, b.y + dy * largo * 0.5f)
        return Path().apply {
            moveTo(b.x, b.y)
            quadraticTo(m.x + nx * ancho, m.y + ny * ancho, b.x + dx * largo, b.y + dy * largo)
            quadraticTo(m.x - nx * ancho, m.y - ny * ancho, b.x, b.y)
            close()
        }
    }

    /**
     * Marca de fijar: la corona de laurel, dos ramas que suben en arco y casi se cierran arriba, atadas abajo con la cinta.
     * Fija, las hojas son de oro y la cinta de púrpura; sin fijar, solo el contorno de las hojas, tenue. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val k = size.minDimension / 24f
        val ox = (size.width - 24 * k) / 2; val oy = (size.height - 24 * k) / 2
        val c = Offset(ox + 12 * k, oy + 12.5f * k)
        val r = 8 * k
        val oro = if (oscuro) ORO else mezcla(BRONCE, BRONCE_OSC, 0.45f)
        val contorno = (if (oscuro) LINO else HAYA_VETA).copy(alpha = if (oscuro) 0.5f else 0.6f)
        for (lado in listOf(-1f, 1f)) {
            // La rama: un arco desde abajo hasta casi arriba.
            val rama = Path()
            for (j in 0..10) {
                val a = Math.toRadians(90.0 + lado * (25.0 + j * 13.5)).toFloat()
                val p = Offset(c.x + r * kotlin.math.cos(a), c.y + r * kotlin.math.sin(a))
                if (j == 0) rama.moveTo(p.x, p.y) else rama.lineTo(p.x, p.y)
            }
            drawPath(rama, if (fija) oro else contorno, style = Stroke(0.8f * k))
            for (j in 0 until 5) {
                val a = Math.toRadians(90.0 + lado * (32.0 + j * 27.0)).toFloat()
                val b = Offset(c.x + r * kotlin.math.cos(a), c.y + r * kotlin.math.sin(a))
                // La hoja sigue la rama hacia arriba y se abre un poco hacia afuera.
                val tangente = a + lado * (Math.PI.toFloat() / 2) + lado * 0.5f
                val hoja = hojaLaurel(b, tangente, 4.6f * k, 1.5f * k)
                val hoja2 = hojaLaurel(b, tangente - lado * 1.15f, 3.6f * k, 1.2f * k)
                if (fija) { drawPath(hoja, oro); drawPath(hoja2, oro) }
                else { drawPath(hoja, contorno, style = Stroke(0.7f * k)); drawPath(hoja2, contorno, style = Stroke(0.7f * k)) }
            }
        }
        val cinta = if (fija) (if (oscuro) mezcla(PURPURA, LINO, 0.55f) else PURPURA) else contorno
        val nudo = Offset(c.x, c.y + r + 0.4f * k)
        drawCircle(cinta, 1.2f * k, nudo)
        drawLine(cinta, nudo, Offset(nudo.x - 2.6f * k, nudo.y + 2.6f * k), 0.9f * k, StrokeCap.Round)
        drawLine(cinta, nudo, Offset(nudo.x + 2.6f * k, nudo.y + 2.6f * k), 0.9f * k, StrokeCap.Round)
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, CERA_RAYA, 0.3f) else mezcla(NEGRO, HAYA_VETA, 0.4f)

    /** Una raya grabada: la línea honda y su luz al lado, como cortada en la piedra. */
    private fun DrawScope.grabado(a: Offset, b: Offset, oscuro: Boolean, fuerza: Float) {
        val u = density
        drawLine((if (oscuro) LINO else HAYA_VETA).copy(alpha = (if (oscuro) 0.2f else 0.45f) * fuerza), a, b, 0.9f * u)
        drawLine(Color.White.copy(alpha = (if (oscuro) 0.04f else 0.7f) * fuerza), Offset(a.x + 0.7f * u, a.y + 0.7f * u), Offset(b.x + 0.7f * u, b.y + 0.7f * u), 0.6f * u)
    }

    /**
     * Vacío: una lápida lisa, todavía sin inscribir: la losa clara con el marco doble grabado (una raya y otra más fina por
     * dentro) y un interpunto en cada esquina del marco de dentro. Sin contorno pintado ni sombra que la levante, para que no
     * parezca un botón. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 24 * u) return
        drawRect(if (oscuro) LAPIDA_NOCHE else LAPIDA, Offset.Zero, size)
        val m1 = 5 * u; val m2 = 9 * u
        listOf(m1 to 1f, m2 to 0.6f).forEach { (m, f) ->
            grabado(Offset(m, m), Offset(w - m, m), oscuro, f); grabado(Offset(m, h - m), Offset(w - m, h - m), oscuro, f)
            grabado(Offset(m, m), Offset(m, h - m), oscuro, f); grabado(Offset(w - m, m), Offset(w - m, h - m), oscuro, f)
        }
        val tinta = (if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA).copy(alpha = 0.5f)
        listOf(Offset(m2 + 4 * u, m2 + 4 * u), Offset(w - m2 - 4 * u, m2 + 4 * u), Offset(m2 + 4 * u, h - m2 - 4 * u), Offset(w - m2 - 4 * u, h - m2 - 4 * u))
            .forEach { interpunto(it, 2.6f * u, tinta) }
    }

    /**
     * Lo vacío: un ánfora de barro, de pie en su aro, con la boca abierta y oscura: no queda nada dentro. Lleva sus dos asas,
     * el labio, una luz en el vientre y el rótulo pintado en el hombro (dos rayitas de tinta). Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val barro = t(TERRACOTA, oscuro)
        val borde = if (oscuro) mezcla(TERRACOTA, LINO, 0.35f) else mezcla(TERRACOTA, NEGRO, 0.5f)
        // El aro donde se apoya la punta.
        drawOval(borde, Offset(f(0.38f), f(0.86f)), Size(f(0.24f), f(0.08f)), style = Stroke(1 * u))
        val cuerpo = Path().apply {
            moveTo(f(0.44f), f(0.13f)); lineTo(f(0.44f), f(0.25f))
            cubicTo(f(0.24f), f(0.29f), f(0.24f), f(0.52f), f(0.46f), f(0.84f))
            lineTo(f(0.5f), f(0.93f)); lineTo(f(0.54f), f(0.84f))
            cubicTo(f(0.76f), f(0.52f), f(0.76f), f(0.29f), f(0.56f), f(0.25f))
            lineTo(f(0.56f), f(0.13f)); close()
        }
        drawPath(cuerpo, barro)
        drawPath(cuerpo, borde, style = Stroke(1 * u))
        // La luz en el vientre.
        drawPath(Path().apply { moveTo(f(0.36f), f(0.36f)); quadraticTo(f(0.33f), f(0.5f), f(0.42f), f(0.66f)) },
            Color.White.copy(alpha = if (oscuro) 0.18f else 0.4f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        // Las asas, del cuello al hombro.
        drawPath(Path().apply { moveTo(f(0.44f), f(0.15f)); cubicTo(f(0.3f), f(0.13f), f(0.28f), f(0.24f), f(0.33f), f(0.31f)) }, borde, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(f(0.56f), f(0.15f)); cubicTo(f(0.7f), f(0.13f), f(0.72f), f(0.24f), f(0.67f), f(0.31f)) }, borde, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // El labio y la boca vacía.
        drawRect(borde, Offset(f(0.41f), f(0.08f)), Size(f(0.18f), f(0.05f)))
        drawOval(if (oscuro) Color(0xFF0A0706) else NEGRO.copy(alpha = 0.85f), Offset(f(0.43f), f(0.065f)), Size(f(0.14f), f(0.035f)))
        // El rótulo pintado en el hombro.
        val tinta = (if (oscuro) LINO else NEGRO).copy(alpha = 0.55f)
        drawLine(tinta, Offset(f(0.42f), f(0.35f)), Offset(f(0.58f), f(0.35f)), 0.8f * u)
        drawLine(tinta, Offset(f(0.44f), f(0.39f)), Offset(f(0.55f), f(0.39f)), 0.8f * u)
    }

    override val margenAviso: Dp = 24.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO

    /**
     * Aviso: la nota rubricada de los códices: un fondo de cera clara (honda de noche) con la raya grabada arriba y abajo, y a
     * la izquierda la rúbrica de minio, una barra de arriba abajo; junto a ella, a la altura del primer renglón, la marca que
     * abre la nota, según [EstadoAviso.semilla]: la hoja de hiedra, un interpunto o los tres puntos (∴). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) mezcla(CERA_NEGRA, Color(0xFF100D0B), 0.25f) else mezcla(CERA_MIEL, Color.White, 0.55f), Offset.Zero, size)
        grabado(Offset(0f, 0.6f * u), Offset(w, 0.6f * u), oscuro, 0.8f)
        grabado(Offset(0f, h - 1.6f * u), Offset(w, h - 1.6f * u), oscuro, 0.8f)
        val minio = if (oscuro) Color(0xFFE0705A) else MINIO
        drawRect(minio, Offset(4 * u, 4 * u), Size(3 * u, maxOf(h - 8 * u, 0f)))
        val tinta = if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA
        val cy = 17 * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> hedera(Offset(16.5f * u, cy), tinta, u * 0.85f)
            1 -> interpunto(Offset(15.5f * u, cy), 4 * u, minio)
            else -> {
                drawCircle(tinta, 1.2f * u, Offset(15.5f * u, cy - 2.6f * u))
                drawCircle(tinta, 1.2f * u, Offset(13 * u, cy + 1.8f * u))
                drawCircle(tinta, 1.2f * u, Offset(18 * u, cy + 1.8f * u))
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) ORO else Color(0xFF7C2433)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, CERA_RAYA, 0.3f) else mezcla(NEGRO, HAYA_VETA, 0.4f)

    /**
     * Cifra: un emblema de mosaico: la cara lisa de piedra clara donde va el número, rodeada de una orla de teselas negras
     * puestas a mano (cada una un poco corrida), con una de pórfido cada cuatro. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 12 * u || h < 12 * u) return
        drawRect(if (oscuro) LAPIDA_NOCHE else LAPIDA, Offset.Zero, size)
        val lado = 3.2f * u
        val paso = maxOf(4.2f * u, 1f)
        val negra = if (oscuro) mezcla(LINO, LAPIDA_NOCHE, 0.62f) else mezcla(NEGRO, HAYA_VETA, 0.2f)
        val porfido = if (oscuro) mezcla(PURPURA, LINO, 0.35f) else Color(0xFF7C2433)
        var k = 0
        fun tesela(x: Float, y: Float) {
            val c = if (k % 4 == 0) porfido else negra
            drawRect(c, Offset(x + (azar(s, k) - 0.5f) * 0.5f * u, y + (azar(s, k + 400) - 0.5f) * 0.5f * u), Size(lado, lado))
            k++
        }
        val nx = ((w - 2 * u) / paso).toInt().coerceIn(1, 120)
        val ny = ((h - 2 * u) / paso).toInt().coerceIn(1, 80)
        val dx = (w - 2 * u - lado) / maxOf(nx - 1, 1); val dy = (h - 2 * u - lado) / maxOf(ny - 1, 1)
        for (j in 0 until nx) { tesela(1 * u + j * dx, 1 * u); tesela(1 * u + j * dx, h - 1 * u - lado) }
        for (j in 1 until ny - 1) { tesela(1 * u, 1 * u + j * dy); tesela(w - 1 * u - lado, 1 * u + j * dy) }
        // La juntura de cal entre la orla y la cara.
        drawRect(negra.copy(alpha = 0.25f), Offset(5.5f * u, 5.5f * u), Size(w - 11 * u, h - 11 * u), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El rojo de las paredes pompeyanas (el cinabrio y el ocre rojo de los frescos). */
    private val ROJO_POMPEYA = Color(0xFF8E2A1E)
    /** El amarillo de los filetes finos de los frescos. */
    private val OCRE_FRESCO = Color(0xFFD9A441)
    /** La llama de la lucerna. */
    private val LLAMA = Color(0xFFF6C96A)

    /** El fondo liso de cada tarjeta, donde va el texto: el bronce pulido, el album blanqueado, el revoque del fresco y su panel. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) mezcla(BRONCE_OSC, Color(0xFF100D0B), 0.74f) else mezcla(BRONCE_LUZ, LINO, 0.6f)
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(MINIO, Color(0xFF100D0B), 0.84f) else LAPIDA
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF16120F) else Color(0xFFE6E1D7)
        TipoTarjeta.VIDA -> if (oscuro) mezcla(ROJO_POMPEYA, Color(0xFF100D0B), 0.86f) else mezcla(LINO, Color.White, 0.3f)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) mezcla(LINO, BRONCE_LUZ, 0.45f) else mezcla(NEGRO, BRONCE_OSC, 0.55f)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(ORO, LINO, 0.3f) else mezcla(MINIO, NEGRO, 0.18f)
        TipoTarjeta.NOCHE -> if (oscuro) ORO else PURPURA
        TipoTarjeta.METRICA -> if (oscuro) ORO else mezcla(MINIO, NEGRO, 0.3f)
        TipoTarjeta.VIDA -> if (oscuro) ORO else mezcla(ROJO_POMPEYA, NEGRO, 0.1f)
    }

    /**
     * Tarjetas: cuatro piezas de Roma. La cifra de Hoy es una tabella de bronce clavada: el marco de bronce con su canto, un
     * clavo en cada esquina y, por dentro, la raya grabada. «Te falta hoy» es el album, el tablero blanqueado de los avisos
     * públicos, en su marco de haya, con dos bandas de minio (arriba y abajo) y dos clavos de bronce. La noche es una pared
     * pompeyana (de día, revoque claro; de noche, el negro de los frescos) con su filete fino de ocre y, arriba a la derecha,
     * una lucerna de barro encendida. La tarjeta de Vida es un panel de fresco: la banda roja de Pompeya con su filete amarillo
     * y el panel claro en medio. Al tocarlas, el canto se invierte (parece hundida) o el panel se oscurece. Destacadas: la raya
     * de la tabella se llena de minio y los clavos son de oro; el album completo lleva bandas de púrpura y una rama de laurel;
     * la noche sellada, el filete de oro doble y un sello de minio en la esquina. Un degradado: la luz de la lucerna.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> tabella(e, oscuro)
            TipoTarjeta.PENDIENTE -> album(e, oscuro)
            TipoTarjeta.NOCHE -> paredNoche(e, oscuro)
            TipoTarjeta.VIDA -> panelFresco(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, BRONCE_OSC, 0.12f)
    }

    /** Un clavo de cabeza redonda, de radio [r], con su brillo. */
    private fun DrawScope.clavo(c: Offset, r: Float, color: Color, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), r, c + Offset(0.3f * r, 0.4f * r))
        drawCircle(color, r, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.25f else 0.55f), r * 0.38f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una rama de laurel que nace en [b] y sube hacia [ang] (en radianes), de [largo], con sus hojas por pares. */
    private fun DrawScope.ramaLaurel(b: Offset, ang: Float, largo: Float, hoja: Color, tallo: Color) {
        val dx = kotlin.math.cos(ang); val dy = kotlin.math.sin(ang)
        val fin = Offset(b.x + dx * largo, b.y + dy * largo)
        drawLine(tallo, b, fin, largo * 0.04f, StrokeCap.Round)
        for (j in 0 until 4) {
            val f = 0.2f + 0.2f * j
            val p = Offset(b.x + dx * largo * f, b.y + dy * largo * f)
            val l = largo * (0.36f - 0.04f * j)
            drawPath(hojaLaurel(p, ang - 0.75f, l, l * 0.22f), hoja)
            drawPath(hojaLaurel(p, ang + 0.75f, l, l * 0.22f), hoja)
        }
        drawPath(hojaLaurel(fin, ang, largo * 0.26f, largo * 0.06f), hoja)
    }

    private fun DrawScope.tabella(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(0.5f * u, (if (e.presionado) 1f else 1.8f) * u), Size(w - 1 * u, h - 1.8f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.8f * u)
        drawRoundRect(t(BRONCE, oscuro), r.topLeft, r.size, radio)
        val luz = Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f); val sombra = Color.Black.copy(alpha = if (oscuro) 0.4f else 0.28f)
        if (e.presionado) bisel(r, sombra, luz, 1 * u) else bisel(r, luz, sombra, 1 * u)
        // La cara pulida, rebajada en el marco.
        val c = Rect(r.left + 4.5f * u, r.top + 4.5f * u, r.right - 4.5f * u, r.bottom - 4.5f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), c.topLeft, c.size)
        bisel(c, sombra.copy(alpha = sombra.alpha * 0.8f), luz.copy(alpha = luz.alpha * 0.6f), 0.8f * u)
        // La raya grabada, por dentro de la cara, con las esquinas cortadas.
        val g = c.deflate(2.6f * u)
        val k = 2.4f * u
        val raya = Path().apply {
            moveTo(g.left + k, g.top); lineTo(g.right - k, g.top); lineTo(g.right, g.top + k); lineTo(g.right, g.bottom - k)
            lineTo(g.right - k, g.bottom); lineTo(g.left + k, g.bottom); lineTo(g.left, g.bottom - k); lineTo(g.left, g.top + k); close()
        }
        val minio = if (oscuro) mezcla(MINIO, ORO, 0.35f) else MINIO
        if (e.destacado) drawPath(raya, minio.copy(alpha = 0.9f), style = Stroke(1.3f * u))
        else {
            drawPath(raya, (if (oscuro) Color.Black else BRONCE_OSC).copy(alpha = if (oscuro) 0.55f else 0.45f), style = Stroke(0.8f * u))
            translate(0.6f * u, 0.6f * u) { drawPath(raya, Color.White.copy(alpha = if (oscuro) 0.06f else 0.45f), style = Stroke(0.5f * u)) }
        }
        // Los clavos, en el marco.
        val clavo = if (e.destacado) (if (oscuro) ORO else mezcla(ORO, BRONCE_OSC, 0.15f)) else t(BRONCE_OSC, oscuro)
        listOf(Offset(r.left + 2.3f * u, r.top + 2.3f * u), Offset(r.right - 2.3f * u, r.top + 2.3f * u),
            Offset(r.left + 2.3f * u, r.bottom - 2.3f * u), Offset(r.right - 2.3f * u, r.bottom - 2.3f * u)).forEach { clavo(it, 1.5f * u, clavo, oscuro) }
    }

    private fun DrawScope.album(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val completo = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(0.5f * u, 1.8f * u), Size(w - 1 * u, h - 1.8f * u))
        val r = Rect(0f, 0f, w, h - 1.8f * u)
        // El marco de haya, con sus vetas.
        val madera = t(HAYA, oscuro)
        drawRect(madera, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, t(HAYA_VETA, oscuro).copy(alpha = 0.4f), e.semilla + 5, densidad = 0.8f, grosor = 0.6f) }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.1f else 0.35f), Color.Black.copy(alpha = 0.3f), 1 * u)
        // El tablero blanqueado.
        val b = Rect(r.left + 4 * u, r.top + 4 * u, r.right - 4 * u, r.bottom - 4 * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), b.topLeft, b.size)
        bisel(b, Color.Black.copy(alpha = 0.25f), Color.White.copy(alpha = if (oscuro) 0.04f else 0.3f), 0.8f * u)
        // Las bandas: minio si falta algo, púrpura si está completo.
        val banda = when {
            completo -> if (oscuro) mezcla(PURPURA, LINO, 0.35f) else PURPURA
            oscuro -> mezcla(MINIO, LINO, 0.12f)
            else -> MINIO
        }
        val alto = minOf(4.4f * u, b.height * 0.09f)
        drawRect(banda, Offset(b.left + 1.5f * u, b.top + 1.5f * u), Size(b.width - 3 * u, alto))
        drawRect(banda.copy(alpha = 0.6f), Offset(b.left + 1.5f * u, b.top + 2.6f * u + alto), Size(b.width - 3 * u, 0.6f * u))
        drawRect(banda, Offset(b.left + 1.5f * u, b.bottom - 1.5f * u - alto * 0.7f), Size(b.width - 3 * u, alto * 0.7f))
        // Los dos clavos de arriba, en el marco.
        listOf(r.left + 2 * u, r.right - 2 * u).forEach { x -> clavo(Offset(x, r.top + 2 * u), 1.5f * u, t(BRONCE, oscuro), oscuro) }
        if (completo) {
            val hoja = if (oscuro) ORO else mezcla(ORO, BRONCE_OSC, 0.3f)
            val largo = minOf(26 * u, b.height * 0.75f)
            val base = Offset(b.right - 20 * u, b.center.y + largo * 0.42f)
            ramaLaurel(base, -2.2f, largo, hoja, hoja)
            ramaLaurel(base, -0.94f, largo, hoja, hoja)
            drawCircle(banda, 1.4f * u, base)
        }
    }

    private fun DrawScope.paredNoche(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(0f, 1.5f * u), Size(w, h - 1.5f * u))
        val r = Rect(0f, 0f, w, h - 1.5f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        // El zócalo, una franja más honda abajo.
        drawRect((if (oscuro) ROJO_POMPEYA else NEGRO).copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(0f, r.bottom - 3 * u), Size(w, 3 * u))
        // El filete fino de ocre, con un cuadrito en cada esquina (doble y de oro si el día está sellado).
        val filete = when {
            sellado -> if (oscuro) ORO else mezcla(ORO, BRONCE_OSC, 0.35f)
            oscuro -> OCRE_FRESCO.copy(alpha = 0.55f)
            else -> mezcla(OCRE_FRESCO, TERRACOTA, 0.4f).copy(alpha = 0.75f)
        }
        val f = Rect(4.5f * u, 4.5f * u, w - 4.5f * u, r.bottom - 6 * u)
        drawRect(filete, f.topLeft, f.size, style = Stroke((if (sellado) 1.1f else 0.8f) * u))
        if (sellado) drawRect(filete.copy(alpha = 0.6f), f.deflate(1.8f * u).topLeft, f.deflate(1.8f * u).size, style = Stroke(0.5f * u))
        listOf(f.topLeft, Offset(f.right, f.top), Offset(f.left, f.bottom), f.bottomRight).forEach { p ->
            drawRect(filete, Offset(p.x - 1.2f * u, p.y - 1.2f * u), Size(2.4f * u, 2.4f * u))
        }
        // La lucerna, arriba a la derecha, con la llama en el pico y su luz.
        val lx = w - 26 * u; val ly = minOf(15 * u, h * 0.18f)
        val pico = Offset(lx - 10 * u, ly - 1.2f * u)
        mancha(pico + Offset(0f, -3 * u), 26 * u, LLAMA.copy(alpha = if (oscuro) (if (sellado) 0.3f else 0.38f) else 0.3f))
        val barro = t(TERRACOTA, oscuro)
        val borde = if (oscuro) mezcla(TERRACOTA, Color.Black, 0.55f) else mezcla(TERRACOTA, NEGRO, 0.5f)
        val cuerpo = Path().apply {
            moveTo(pico.x - 1.5f * u, pico.y - 1.2f * u)
            quadraticTo(lx - 5 * u, ly - 3.2f * u, lx, ly - 3.6f * u)
            quadraticTo(lx + 8 * u, ly - 3.4f * u, lx + 8 * u, ly)
            quadraticTo(lx + 8 * u, ly + 3.6f * u, lx, ly + 3.6f * u)
            quadraticTo(lx - 5 * u, ly + 3.4f * u, pico.x - 1.5f * u, pico.y + 1.4f * u)
            close()
        }
        drawOval(borde, Offset(lx + 6.5f * u, ly - 3.2f * u), Size(5 * u, 4 * u), style = Stroke(1 * u))
        drawPath(cuerpo, barro)
        drawPath(cuerpo, borde, style = Stroke(0.7f * u))
        drawOval(borde, Offset(lx - 3 * u, ly - 2.4f * u), Size(6 * u, 2.4f * u), style = Stroke(0.6f * u))
        drawCircle(borde, 0.6f * u, Offset(lx - 6.5f * u, ly - 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(lx - 4 * u, ly + 3.6f * u), Size(8 * u, 0.6f * u))
        // La llama.
        val llama = Path().apply {
            moveTo(pico.x - 0.6f * u, pico.y - 7.5f * u)
            quadraticTo(pico.x + 2.6f * u, pico.y - 2.5f * u, pico.x, pico.y)
            quadraticTo(pico.x - 2.8f * u, pico.y - 2.5f * u, pico.x - 0.6f * u, pico.y - 7.5f * u)
            close()
        }
        drawPath(llama, LLAMA)
        drawPath(llama, mezcla(LLAMA, MINIO, 0.5f), style = Stroke(0.5f * u))
        if (sellado) {
            // El sello de minio, abajo a la derecha.
            val c = Offset(w - 13 * u, r.bottom - 12 * u)
            val minio = if (oscuro) mezcla(MINIO, LINO, 0.15f) else MINIO
            drawCircle(minio, 5 * u, c)
            drawCircle(Color.Black.copy(alpha = 0.25f), 3.4f * u, c, style = Stroke(0.6f * u))
            interpunto(c, 2.8f * u, Color.Black.copy(alpha = 0.3f))
        }
    }

    private fun DrawScope.panelFresco(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0.5f * u, (if (e.presionado) 0.8f else 1.6f) * u), Size(w - 1 * u, h - 1.6f * u))
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.6f * u)
        val rojo = if (oscuro) mezcla(ROJO_POMPEYA, Color(0xFF100D0B), 0.35f) else ROJO_POMPEYA
        drawRect(if (e.presionado) mezcla(rojo, Color.Black, 0.2f) else rojo, r.topLeft, r.size)
        // El panel claro, con su filete amarillo por fuera y una orejeta en cada esquina.
        val p = Rect(r.left + 5.5f * u, r.top + 5.5f * u, r.right - 5.5f * u, r.bottom - 5.5f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), p.topLeft, p.size)
        val amarillo = if (oscuro) OCRE_FRESCO.copy(alpha = 0.75f) else OCRE_FRESCO
        val fi = p.inflate(2.2f * u)
        drawRect(amarillo, fi.topLeft, fi.size, style = Stroke(0.7f * u))
        listOf(fi.topLeft, Offset(fi.right, fi.top), Offset(fi.left, fi.bottom), fi.bottomRight).forEach { q ->
            drawCircle(amarillo, 1.1f * u, q)
        }
        // Un filete negro fino por dentro del panel, como en los frescos.
        drawRect((if (oscuro) Color.Black else NEGRO).copy(alpha = if (oscuro) 0.5f else 0.35f), p.topLeft, p.size, style = Stroke(0.6f * u))
        if (e.destacado) drawRect(amarillo, p.deflate(1.4f * u).topLeft, p.deflate(1.4f * u).size, style = Stroke(1 * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** El verde del bronce viejo (el cardenillo): la marca de los ingresos. */
    private val CARDENILLO = Color(0xFF2F6E62)

    override val altoBalanza: Dp = 58.dp

    /**
     * Balanza: un díptico de tablillas de cera abierto, a todo el ancho: el marco de haya con sus vetas y, en medio, la bisagra
     * de dos agujeros con el cordón de lino. La tablilla de la izquierda es el gasto y la de la derecha el ingreso. La elegida
     * tiene la cera recién alisada, clara de día (de noche, la cera negra a la luz), con el borde del rebaje marcado de púrpura,
     * dos clavillos de bronce en su lado del marco y el texto grueso; la otra queda con la cera vieja, opaca y hundida, y el
     * texto delgado. Sin degradados.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 24 * u) return
        val marco = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(if (oscuro) mezcla(HAYA_VETA, Color(0xFF100D0B), 0.55f) else HAYA, marco.topLeft, marco.size, CornerRadius(3 * u))
        vetas(marco.deflate(2 * u), (if (oscuro) Color.Black else HAYA_VETA).copy(alpha = if (oscuro) 0.3f else 0.22f), 12, grosor = 0.8f)
        drawRoundRect((if (oscuro) Color.Black else HAYA_VETA).copy(alpha = 0.6f), marco.topLeft, marco.size, CornerRadius(3 * u), style = Stroke(1 * u))
        val xm = w / 2
        for (lado in 0..1) {
            val elegida = (lado == 1) == e.ingreso
            val r = if (lado == 0) Rect(marco.left + 7 * u, marco.top + 6 * u, xm - 7 * u, marco.bottom - 6 * u)
            else Rect(xm + 7 * u, marco.top + 6 * u, marco.right - 7 * u, marco.bottom - 6 * u)
            val fondo = when {
                elegida && oscuro -> mezcla(CERA_NEGRA, HAYA, 0.16f)
                elegida -> mezcla(CERA_MIEL, LINO, 0.55f)
                oscuro -> mezcla(CERA_NEGRA, Color.Black, 0.45f)
                else -> mezcla(CERA_MIEL, HAYA_VETA, 0.24f)
            }
            drawRect(fondo, r.topLeft, r.size)
            // El rebaje: la sombra arriba y a la izquierda, la luz abajo.
            val sombra = Color.Black.copy(alpha = if (elegida) 0.22f else 0.38f)
            drawLine(sombra, Offset(r.left, r.top + 0.8f * u), Offset(r.right, r.top + 0.8f * u), 1.6f * u)
            drawLine(sombra, Offset(r.left + 0.8f * u, r.top), Offset(r.left + 0.8f * u, r.bottom), 1.6f * u)
            drawLine((if (oscuro) HAYA else HAYA_LUZ).copy(alpha = 0.6f), Offset(r.left, r.bottom + 0.6f * u), Offset(r.right, r.bottom + 0.6f * u), 1 * u)
            if (elegida) {
                drawRect(if (oscuro) mezcla(PURPURA, LINO, 0.45f) else PURPURA, r.topLeft + Offset(2.5f * u, 2.5f * u),
                    Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(1.4f * u))
                val xc = if (lado == 0) marco.left + 3.6f * u else marco.right - 3.6f * u
                for (yc in listOf(marco.top + 9 * u, marco.bottom - 9 * u)) {
                    drawCircle(BRONCE_OSC, 2.3f * u, Offset(xc, yc))
                    drawCircle(if (oscuro) BRONCE else BRONCE_LUZ, 1.5f * u, Offset(xc - 0.4f * u, yc - 0.4f * u))
                }
            } else {
                // Las rayas viejas del estilo, en las orillas de la cera.
                val raya = (if (oscuro) LINO else HAYA_VETA).copy(alpha = if (oscuro) 0.12f else 0.22f)
                for (j in 0 until 4) {
                    val xa = if (j % 2 == 0) r.left + (5 + 3 * azar(21 + lado, j)) * u else r.right - (24 + 3 * azar(21 + lado, j)) * u
                    val ya = r.top + r.height * (0.22f + 0.18f * j)
                    drawLine(raya, Offset(xa, ya), Offset(xa + (14 + 6 * azar(23, j)) * u, ya + 0.6f * u), 0.7f * u)
                }
            }
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val caja = Rect(r.left + 10 * u, r.top + 4 * u, r.right - 10 * u, r.bottom - 4 * u)
            if (elegida) textoEn(medidor, texto, caja, Letra.inscripcion, legibleAnt(base, 5f, fondo), 17f, peso = FontWeight.Bold, espaciado = 0.8f)
            else textoEn(medidor, texto, caja, Letra.inscripcion, legibleAnt(mezcla(base, fondo, 0.2f), 4.7f, fondo), 15f, espaciado = 0.8f)
        }
        // La bisagra: dos agujeros en el marco del medio y el cordón de lino que los une.
        val lino = if (oscuro) mezcla(LINO, Color.Black, 0.35f) else LINO
        val ya = h * 0.34f; val yb = h * 0.66f
        for (y in listOf(ya, yb)) drawCircle(NEGRO.copy(alpha = 0.75f), 1.8f * u, Offset(xm, y))
        val cordon = Path().apply { moveTo(xm, ya); cubicTo(xm - 4.5f * u, ya + 3 * u, xm + 4.5f * u, yb - 3 * u, xm, yb) }
        drawPath(cordon, lino, style = Stroke(1.3f * u, cap = StrokeCap.Round))
    }

    override val altoMes: Dp = 50.dp

    /**
     * Encabezado del mes: una tabula ansata de bronce, la cartela de las inscripciones, con sus dos asas en cola de milano y un
     * clavo en cada una; la cara pulida (de noche, el bronce oscuro) lleva la raya grabada por dentro y el mes en capitales. A
     * los lados, fuera de la cartela, las hojas de hiedra de las filas, grandes, para ir y volver. Un degradado: el brillo del
     * bronce.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 24 * u) return
        val r = Rect(54 * u, 3 * u, w - 54 * u, h - 3 * u)
        val cy = h / 2
        val claro = if (oscuro) mezcla(BRONCE_OSC, Color(0xFF100D0B), 0.4f) else mezcla(BRONCE_LUZ, LINO, 0.55f)
        val hondo = if (oscuro) mezcla(BRONCE_OSC, Color(0xFF100D0B), 0.62f) else mezcla(BRONCE_LUZ, BRONCE, 0.35f)
        val canto = if (oscuro) BRONCE else BRONCE_OSC
        val asas = Path().apply {
            for (lado in listOf(-1f, 1f)) {
                val x = if (lado < 0) r.left + 1 * u else r.right - 1 * u
                moveTo(x, cy - 5 * u); lineTo(x + lado * 13 * u, cy - 11 * u); lineTo(x + lado * 13 * u, cy + 11 * u); lineTo(x, cy + 5 * u); close()
            }
        }
        drawPath(asas, hondo)
        drawPath(asas, canto, style = Stroke(1 * u))
        drawRect(Brush.verticalGradient(listOf(claro, hondo), r.top, r.bottom), r.topLeft, r.size)
        drawRect(canto, r.topLeft, r.size, style = Stroke(1.2f * u))
        drawRect((if (oscuro) BRONCE_LUZ else BRONCE_OSC).copy(alpha = 0.5f), r.topLeft + Offset(3.5f * u, 3.5f * u), Size(r.width - 7 * u, r.height - 7 * u), style = Stroke(0.8f * u))
        for (lado in listOf(-1f, 1f)) {
            val c = Offset(if (lado < 0) r.left - 7.5f * u else r.right + 7.5f * u, cy)
            drawCircle(BRONCE_OSC, 2.2f * u, c); drawCircle(if (oscuro) BRONCE else BRONCE_LUZ, 1.3f * u, c - Offset(0.4f * u, 0.4f * u))
        }
        textoEn(medidor, e.titulo.uppercase(), Rect(r.left + 10 * u, r.top + 3 * u, r.right - 10 * u, r.bottom - 3 * u), Letra.inscripcion,
            legibleAnt(if (oscuro) BRONCE_LUZ else NEGRO, 7f, claro, hondo), 19f, peso = FontWeight.Bold, espaciado = 1.5f)
        flechasMesAnt(48 * u) { flechaFila(oscuro) }
    }

    /**
     * Asiento: la raya grabada de las inscripciones abajo, tenue, y al principio un interpunto triangular del tipo: de minio si
     * es un gasto, de cardenillo (el verde del bronce viejo) si es un ingreso. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 8 * u) return
        val y = h - 1.6f * u
        grabado(Offset(14 * u, y), Offset(w - 2 * u, y), oscuro, 0.75f)
        val c = if (e.ingreso) CARDENILLO else MINIO
        interpunto(Offset(6 * u, y - 0.6f * u), 5.5f * u, (if (oscuro) mezcla(c, LINO, 0.4f) else c).copy(alpha = 0.85f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * Rótulo del día: una tabula ansata pequeña de piedra clara (de noche, la piedra honda), con su canto de bronce y la fecha en
     * capitales; de ahí sale hacia la derecha la raya grabada, que acaba en la hoja de hiedra. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 10 * u) return
        val tam = 12.5f
        val ancho = minOf(anchoTextoAnt(medidor, texto, Letra.inscripcion, tam, FontWeight.Bold, 1.6f) + 2 * u, w * 0.6f)
        val r = Rect(9 * u, 1 * u, 9 * u + ancho + 14 * u, h - 1 * u)
        val cy = h / 2
        val fondo = if (oscuro) LAPIDA_NOCHE else LAPIDA
        val canto = if (oscuro) BRONCE else BRONCE_OSC
        val asas = Path().apply {
            moveTo(r.left, cy - 3 * u); lineTo(r.left - 8 * u, cy - 7 * u); lineTo(r.left - 8 * u, cy + 7 * u); lineTo(r.left, cy + 3 * u); close()
            moveTo(r.right, cy - 3 * u); lineTo(r.right + 8 * u, cy - 7 * u); lineTo(r.right + 8 * u, cy + 7 * u); lineTo(r.right, cy + 3 * u); close()
        }
        drawPath(asas, if (oscuro) mezcla(BRONCE_OSC, Color.Black, 0.3f) else BRONCE)
        drawRect(fondo, r.topLeft, r.size)
        drawRect(canto, r.topLeft, r.size, style = Stroke(1 * u))
        textoEn(medidor, texto, Rect(r.left + 7 * u, r.top, r.left + 7 * u + ancho, r.bottom), Letra.inscripcion,
            legibleAnt(if (oscuro) ORO else PURPURA, 5.5f, fondo), tam, peso = FontWeight.Bold, espaciado = 1.6f)
        val x0 = r.right + 14 * u; val x1 = w - 18 * u
        if (x1 > x0) grabado(Offset(x0, cy), Offset(x1, cy), oscuro, 1.2f)
        hedera(Offset(w - 9 * u, cy), if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA, u)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * El marco de la tabla a [m] del borde, con las esquinas de arriba cóncavas como las de la hoja (de radio [m] + 14 dp) y las de
     * abajo en escuadra.
     */
    private fun marcoTabula(w: Float, h: Float, m: Float, u: Float): Path {
        val r = m + 14 * u
        return Path().apply {
            moveTo(r, m); lineTo(w - r, m); quadraticTo(w - r, r, w - m, r); lineTo(w - m, h - m); lineTo(m, h - m); lineTo(m, r)
            quadraticTo(r, r, r, m); close()
        }
    }

    /**
     * Hoja rápida: la tabla de bronce clavada a la pared. Un doble filete grabado (la raya honda con su luz al lado) corre por
     * arriba, baja por los dos cantos y se cierra abajo, con las esquinas de arriba cóncavas como las de la hoja; en cada canto, a
     * media altura, la cola de milano de una tabula ansata; arriba al centro, una corona de laurel chica con su interpunto; un clavo
     * en cada esquina y, abajo al centro, una hoja de hiedra que corta el filete. Sin degradados.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        val linea = alfaAnt(e.primario, 0.5f)
        val fina = alfaAnt(e.primario, 0.28f)
        val luz = if (oscuro) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.65f)
        val grueso = marcoTabula(w, h, 6 * u, u); val delgado = marcoTabula(w, h, 9.5f * u, u)
        // La hiedra de abajo corta el filete: se deja libre su hueco.
        clipRect(w / 2 - 9 * u, h - 13 * u, w / 2 + 9 * u, h, clipOp = androidx.compose.ui.graphics.ClipOp.Difference) {
            translate(0.7f * u, 0.7f * u) { drawPath(grueso, luz, style = Stroke(1.2f * u)) }
            drawPath(grueso, linea, style = Stroke(1.2f * u))
            drawPath(delgado, fina, style = Stroke(0.7f * u))
        }
        hedera(Offset(w / 2 + 1.2f * u, h - 6 * u), alfaAnt(e.primario, 0.6f), u)
        // Las colas de milano, a media altura de cada canto, por fuera del filete.
        listOf(6 * u to -1f, w - 6 * u to 1f).forEach { (x, d) ->
            val y = h / 2
            drawPath(Path().apply { moveTo(x, y - 8 * u); lineTo(x + d * 4.6f * u, y - 3 * u); lineTo(x + d * 4.6f * u, y + 3 * u); lineTo(x, y + 8 * u); close() }, linea)
            drawLine(fina, Offset(x - d * 3.5f * u, y - 5 * u), Offset(x - d * 3.5f * u, y + 5 * u), 0.7f * u)
        }
        // La corona de laurel, arriba al centro, entre el filete y el texto.
        val hoja = alfaAnt(e.secundario, if (oscuro) 0.8f else 0.65f); val tallo = alfaAnt(e.secundario, 0.5f)
        ramaLaurel(Offset(w / 2 - 2.5f * u, 21.5f * u), -2.7f, 17 * u, hoja, tallo)
        ramaLaurel(Offset(w / 2 + 2.5f * u, 21.5f * u), -0.44f, 17 * u, hoja, tallo)
        interpunto(Offset(w / 2, 15.5f * u), 3.8f * u, alfaAnt(e.primario, 0.8f))
        // Los clavos: dos en el hueco de las esquinas cóncavas y dos abajo.
        val bronce = mezcla(e.secundario, if (oscuro) BRONCE_LUZ else BRONCE, 0.4f)
        listOf(Offset(12 * u, 12 * u), Offset(w - 12 * u, 12 * u), Offset(13 * u, h - 13 * u), Offset(w - 13 * u, h - 13 * u))
            .forEach { clavo(it, 1.9f * u, bronce, oscuro) }
    }

    /**
     * Relación: una tabula ansata. El doble filete (grueso afuera, fino adentro), la cola de milano a cada lado, un clavito en cada
     * esquina y, arriba al centro, tres interpuntos sobre el filete; el centro, liso. Sin degradados.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val c = e.primario
        drawRect(alfaAnt(c, 0.45f), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), style = Stroke(0.8f * u))
        drawRect(alfaAnt(c, 0.25f), Offset(5.5f * u, 5.5f * u), Size(w - 11 * u, h - 11 * u), style = Stroke(0.6f * u))
        listOf(0f to 1f, w to -1f).forEach { (x0, d) ->
            drawPath(Path().apply {
                moveTo(x0 + d * 5.5f * u, h / 2); lineTo(x0 + d * 1.5f * u, h / 2 - 6 * u); lineTo(x0 + d * 1.5f * u, h / 2 + 6 * u); close()
            }, alfaAnt(c, 0.4f))
        }
        val bronce = mezcla(e.secundario, if (oscuro) BRONCE_LUZ else BRONCE, 0.4f)
        listOf(Offset(3 * u, 3 * u), Offset(w - 3 * u, 3 * u), Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3 * u))
            .forEach { clavo(it, 1.3f * u, bronce, oscuro) }
        for (k in -1..1) interpunto(Offset(w / 2 + k * 6 * u, 3 * u), 2.4f * u, alfaAnt(c, if (k == 0) 0.6f else 0.4f))
    }

    /** Pista: el clípeo, el escudo redondo con su doble canto, unido por cuatro tachones de bronce. Sin degradados. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f; val o = center
        drawCircle(e.primario, l * 0.47f, o, style = Stroke(g))
        drawCircle(e.primario, l * 0.4f, o, style = Stroke(g * 0.5f))
        for (k in 0 until 4) {
            val a = Math.PI / 4 + k * Math.PI / 2
            drawCircle(alfaAnt(e.secundario, 0.85f), l * 0.032f, Offset(o.x + (l * 0.435f * kotlin.math.cos(a)).toFloat(), o.y + (l * 0.435f * kotlin.math.sin(a)).toFloat()))
        }
    }
}

/**
 * Grecia: el campo es una losa de mármol con su filete pintado de azul del Egeo, que en cada esquina se vuelve una llave de la
 * greca (el meandro). La cara del medio queda lisa, con apenas unas vetas. Con el foco el filete se vuelve de oro, más grueso,
 * y aparece un segundo filete fino por dentro; con un error, se pinta de rojo de barniz. Con varios renglones, rayas grabadas.
 * Sin degradados.
 */
object ComunHeleno : DisenoComun {
    private val MARMOL = Color(0xFFEEE8DA)
    private val MARMOL_LUZ = Color(0xFFFAF6EC)
    private val MARMOL_OSC = Color(0xFFCBC1AB)
    private val MARMOL_SOMBRA = Color(0xFF9E947E)
    private val EGEO = Color(0xFF2E5E8E)
    private val BARNIZ = Color(0xFF1A1410)
    private val LLAMA = Color(0xFFF2C46A)
    /** El oro del filete con el foco, oscuro para que se vea sobre el mármol. */
    private val ORO = Color(0xFF8E6A18)
    /** El rojo de los vasos de figuras: el del error. */
    private val ROJO_VASO = Color(0xFFA0301C)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0C0A), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0C0A), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) MARMOL_LUZ else BARNIZ
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(MARMOL, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.6f) else EGEO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO_VASO

    /** Una llave de la greca en la esquina [o], de lado [k], que se mete hacia [sx], [sy] (±1): una espiral cuadrada. */
    private fun DrawScope.llave(o: Offset, k: Float, sx: Float, sy: Float, color: Color, grosor: Float) {
        val pts = listOf(0f to 1f, 0f to 0f, 1f to 0f, 1f to 1f, 0.34f to 1f, 0.34f to 0.34f, 0.67f to 0.34f, 0.67f to 0.67f)
        val p = Path()
        pts.forEachIndexed { i, (x, y) ->
            val px = o.x + sx * x * k; val py = o.y + sy * y * k
            if (i == 0) p.moveTo(px, py) else p.lineTo(px, py)
        }
        drawPath(p, color, style = Stroke(grosor))
    }

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val marmol = hondo(MARMOL, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, marmol, 0.5f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size)
        drawRect(marmol, r.topLeft, r.size)
        // Unas vetas apenas, que cruzan la losa.
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (j in 0 until 3) {
                val x = r.left + r.width * azar(s, 41 + j)
                drawLine(hondo(MARMOL_OSC, oscuro).copy(alpha = 0.45f), Offset(x, r.top), Offset(x + (30 + 40 * azar(s, 44 + j)) * u, r.bottom), 0.6f * u)
            }
        }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.06f else 0.55f), Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), 1.5f * u)
        if (e.multilinea) renglonesAntiguedad(r.left + 12 * u, r.right - 12 * u, margenCampoY.toPx(), r.bottom - 9 * u,
            (if (oscuro) Color.Black.copy(alpha = 0.35f) else MARMOL_SOMBRA.copy(alpha = 0.3f)), 0.7f * u,
            luz = Color.White.copy(alpha = if (oscuro) 0.04f else 0.6f))
        // El filete con sus llaves en las esquinas.
        val encendido = e.enfocado && e.habilitado
        val color = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) LLAMA else ORO
            else -> a(if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.25f).copy(alpha = 0.75f) else EGEO)
        }
        val g = (if (encendido || e.error) 1.8f else 1.2f) * u
        val m = 4 * u
        val k = 8 * u
        val f = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        if (f.width > 2 * k + 2 * u) {
            drawLine(color, Offset(f.left + k, f.top), Offset(f.right - k, f.top), g)
            drawLine(color, Offset(f.left + k, f.bottom), Offset(f.right - k, f.bottom), g)
        }
        if (f.height > 2 * k + 2 * u) {
            drawLine(color, Offset(f.left, f.top + k), Offset(f.left, f.bottom - k), g)
            drawLine(color, Offset(f.right, f.top + k), Offset(f.right, f.bottom - k), g)
        }
        val gl = (if (encendido || e.error) 1.4f else 1.1f) * u
        llave(Offset(f.left, f.top), k, 1f, 1f, color, gl)
        llave(Offset(f.right, f.top), k, -1f, 1f, color, gl)
        llave(Offset(f.left, f.bottom), k, 1f, -1f, color, gl)
        llave(Offset(f.right, f.bottom), k, -1f, -1f, color, gl)
        // Con el foco, un filete fino por dentro.
        if (encendido || e.error) {
            val i = Rect(f.left + k + 2 * u, f.top + 2.6f * u, f.right - k - 2 * u, f.bottom - 2.6f * u)
            if (i.width > 0f && i.height > 0f) {
                drawLine(color.copy(alpha = 0.6f), Offset(i.left, i.top), Offset(i.right, i.top), 0.6f * u)
                drawLine(color.copy(alpha = 0.6f), Offset(i.left, i.bottom), Offset(i.right, i.bottom), 0.6f * u)
            }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Separador: el filete azul del Egeo a los dos lados y, al centro, un tramo corto de greca (cuatro vueltas del meandro, de
     * 5 dp de alto, en un solo trazo). Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val azul = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.35f) else EGEO
        val filete = azul.copy(alpha = (if (oscuro) 0.42f else 0.5f) * f)
        val greca = azul.copy(alpha = (if (oscuro) 0.6f else 0.7f) * f)
        val k = 5 * u
        val vueltas = 4
        val x0 = w / 2 - vueltas * k / 2
        val arriba = cy - k / 2
        // Cada vuelta: sube, cruza arriba, baja a media altura, vuelve un poco y baja a la base, que sigue a la vuelta siguiente.
        val p = Path().apply {
            moveTo(x0 - 3 * u, arriba + k)
            lineTo(x0, arriba + k)
            for (j in 0 until vueltas) {
                val x = x0 + j * k
                lineTo(x, arriba); lineTo(x + 0.66f * k, arriba); lineTo(x + 0.66f * k, arriba + 0.62f * k)
                lineTo(x + 0.33f * k, arriba + 0.62f * k); lineTo(x + 0.33f * k, arriba + k); lineTo(x + k, arriba + k)
            }
            lineTo(x0 + vueltas * k + 3 * u, arriba + k)
        }
        drawPath(p, greca, style = Stroke(1 * u))
        val hueco = vueltas * k / 2 + 6 * u
        if (w / 2 - hueco > 4 * u) {
            drawLine(filete, Offset(2 * u, cy), Offset(w / 2 - hueco, cy), 1 * u)
            drawLine(filete, Offset(w / 2 + hueco, cy), Offset(w - 2 * u, cy), 1 * u)
            drawCircle(greca, 1 * u, Offset(w / 2 - hueco + 3 * u, cy))
            drawCircle(greca, 1 * u, Offset(w / 2 + hueco - 3 * u, cy))
        }
    }

    /** El texto de los botones: el violeta del tema de día, el oro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD8B45C) else Color(0xFF5A2A82)

    /**
     * Botón de texto: un filete azul bajo la palabra que en cada punta se enrosca hacia arriba en una llave de la greca, como
     * las esquinas del campo. Al tocarlo el filete se vuelve de oro y el mármol de detrás se lava de azul muy tenue; apagado,
     * apenas se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val azul = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.35f) else EGEO
        if (tocado) drawRoundRect(azul.copy(alpha = if (oscuro) 0.14f else 0.08f), Offset(4 * u, cy - 15 * u), Size(maxOf(w - 8 * u, 0f), 30 * u), CornerRadius(2 * u))
        val color = if (tocado) (if (oscuro) LLAMA else ORO) else azul.copy(alpha = 0.6f * apagado)
        val k = 4.5f * u
        val x0 = minOf(10 * u, w * 0.2f); val x1 = w - x0
        val y = cy + 11.5f * u
        if (x1 - x0 < 2 * k + 2 * u) return
        val g = (if (tocado) 1.3f else 1f) * u
        drawLine(color, Offset(x0 + k, y), Offset(x1 - k, y), g)
        llave(Offset(x0, y), k, 1f, -1f, color, g)
        llave(Offset(x1, y), k, -1f, -1f, color, g)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El barro de los vasos áticos, anaranjado, el de la copa y el del óstracon. */
    private val ARCILLA = Color(0xFFD27A4A)

    /** El Egeo con la luz del momento: de noche, aclarado para que se vea. */
    private fun egeo(oscuro: Boolean): Color = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.35f) else EGEO

    override val margenFila: Dp = 12.dp

    /**
     * Fila: el filete azul del Egeo, fino, abajo, y a la izquierda una llave pequeña de la greca (con detalle, dos, una sobre
     * otra en espejo). Al tocarla el mármol se lava de azul muy tenue y la llave se vuelve de oro. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val azul = egeo(oscuro)
        if (e.presionado) drawRect(azul.copy(alpha = if (oscuro) 0.12f else 0.07f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine(azul.copy(alpha = if (oscuro) 0.26f else 0.28f), Offset(x0, h - 1 * u), Offset(w - 4 * u, h - 1 * u), 0.8f * u)
        val color = if (e.presionado) (if (oscuro) LLAMA else ORO) else azul.copy(alpha = 0.55f)
        val k = 4.4f * u
        if (e.conDetalle) {
            llave(Offset(3.4f * u, h / 2 - 1 * u), k, 1f, -1f, color, 0.9f * u)
            llave(Offset(3.4f * u, h / 2 + 1 * u), k, 1f, 1f, color, 0.9f * u)
        } else llave(Offset(3.4f * u, h / 2 - k / 2), k, 1f, 1f, color, 0.9f * u)
    }

    /**
     * Flecha: una flecha de las de los vasos de figuras negras: el astil fino con sus dos plumas atrás y la punta de aletas,
     * negra de barniz (de noche, clara). Sin degradados.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val color = if (oscuro) MARMOL_LUZ.copy(alpha = 0.8f) else BARNIZ.copy(alpha = 0.82f)
        drawLine(color, Offset(w * 0.1f, cy), Offset(w * 0.6f, cy), 1 * u, StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(w * 0.94f, cy); lineTo(w * 0.56f, cy - h * 0.24f); lineTo(w * 0.64f, cy); lineTo(w * 0.56f, cy + h * 0.24f); close()
        }, color)
        val pluma = egeo(oscuro)
        for (j in 0..1) {
            val x = w * (0.1f + j * 0.1f)
            drawLine(pluma, Offset(x, cy), Offset(x - w * 0.08f, cy - h * 0.16f), 0.9f * u, StrokeCap.Round)
            drawLine(pluma, Offset(x, cy), Offset(x - w * 0.08f, cy + h * 0.16f), 0.9f * u, StrokeCap.Round)
        }
    }

    /**
     * Marca de fijar: el sol de Vergina, la estrella de dieciséis rayos de las tumbas macedonias, con su disco en medio. Fija,
     * de oro; sin fijar, solo su contorno en el azul del Egeo, tenue. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        val sol = estrella(c, 10 * k, 16, 0.42f)
        if (fija) {
            val oro = if (oscuro) LLAMA else ORO
            drawPath(sol, oro)
            drawCircle(if (oscuro) mezcla(LLAMA, BARNIZ, 0.35f) else mezcla(ORO, BARNIZ, 0.35f), 2.6f * k, c)
        } else {
            val azul = egeo(oscuro).copy(alpha = if (oscuro) 0.8f else 0.5f)
            drawPath(sol, azul, style = Stroke(0.8f * u))
            drawCircle(azul, 2.6f * k, c, style = Stroke(0.8f * u))
        }
    }

    override val tamDibujoVacio: Dp = 44.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(MARMOL_LUZ, EGEO, 0.22f) else Color(0xFF4E4760)

    /** El mármol de los recuadros (vacío y aviso): claro de día; de noche, en penumbra. */
    private fun losa(oscuro: Boolean): Color = if (oscuro) mezcla(MARMOL, Color(0xFF0F0C0A), 0.84f) else MARMOL

    /**
     * Vacío: una metopa sin relieve: la losa lisa de mármol, coronada por la fila de dentículos de la cornisa (los dientes
     * cuadrados de piedra) sobre su filete, y abajo un filete doble; los lados quedan abiertos, sin marco, para que no parezca
     * un botón. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 16 * u || h < 16 * u) return
        drawRect(losa(oscuro), Offset.Zero, size)
        val diente = if (oscuro) Color.Black.copy(alpha = 0.45f) else MARMOL_SOMBRA.copy(alpha = 0.5f)
        val paso = maxOf(6 * u, 1f)
        var x = 4 * u; var k = 0
        while (x + 3.4f * u < w - 3 * u && k < 200) {
            drawRect(diente, Offset(x, 2 * u), Size(3.4f * u, 3.6f * u))
            x += paso; k++
        }
        val azul = egeo(oscuro).copy(alpha = if (oscuro) 0.4f else 0.45f)
        drawLine(azul, Offset(3 * u, 7.2f * u), Offset(w - 3 * u, 7.2f * u), 0.9f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.6f), Offset(3 * u, 8.2f * u), Offset(w - 3 * u, 8.2f * u), 0.6f * u)
        drawLine(azul, Offset(3 * u, h - 6 * u), Offset(w - 3 * u, h - 6 * u), 0.9f * u)
        drawLine(azul.copy(alpha = azul.alpha * 0.6f), Offset(3 * u, h - 3.6f * u), Offset(w - 3 * u, h - 3.6f * u), 0.6f * u)
    }

    /**
     * Lo vacío: una cílica, la copa baja de beber, vista un poco desde arriba: el tondo del fondo (el medallón que se pintaba)
     * quedó sin figura. De día, de figuras negras (la copa negra de barniz y el tondo de barro); de noche, de figuras rojas (la
     * copa de barro y el fondo negro). Lleva sus dos asas, el pie y el filete de barro reservado. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val cuerpo = if (oscuro) t(ARCILLA, true) else BARNIZ
        val fondo = if (oscuro) Color(0xFF0B0908) else ARCILLA
        val linea = if (oscuro) mezcla(ARCILLA, MARMOL_LUZ, 0.3f) else ARCILLA
        // Las asas, que suben a los lados.
        drawArc(cuerpo, 180f, 180f, false, Offset(f(0.02f), f(0.26f)), Size(f(0.2f), f(0.18f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawArc(cuerpo, 180f, 180f, false, Offset(f(0.78f), f(0.26f)), Size(f(0.2f), f(0.18f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // La copa: media elipse abajo, la boca arriba.
        drawArc(cuerpo, 0f, 180f, true, Offset(f(0.1f), f(0.14f)), Size(f(0.8f), f(0.48f)))
        drawOval(cuerpo, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)))
        drawOval(fondo, Offset(f(0.14f), f(0.32f)), Size(f(0.72f), f(0.12f)))
        // El tondo vacío, con su círculo de meandro sugerido por un anillo.
        drawOval(if (oscuro) linea.copy(alpha = 0.7f) else BARNIZ.copy(alpha = 0.8f), Offset(f(0.34f), f(0.345f)), Size(f(0.32f), f(0.07f)), style = Stroke(0.8f * u))
        // El filete reservado en el vientre.
        drawArc(linea, 20f, 140f, false, Offset(f(0.16f), f(0.2f)), Size(f(0.68f), f(0.36f)), style = Stroke(0.8f * u))
        // El tallo y el pie.
        drawRect(cuerpo, Offset(f(0.46f), f(0.6f)), Size(f(0.08f), f(0.2f)))
        drawOval(cuerpo, Offset(f(0.3f), f(0.78f)), Size(f(0.4f), f(0.1f)))
        drawArc(linea, 180f, 180f, false, Offset(f(0.31f), f(0.785f)), Size(f(0.38f), f(0.08f)), style = Stroke(0.7f * u))
        if (oscuro) drawOval(linea.copy(alpha = 0.6f), Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)), style = Stroke(0.7f * u))
    }

    override val margenAviso: Dp = 24.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) MARMOL_LUZ else BARNIZ

    /**
     * Aviso: una losa de mármol con una columna a la izquierda, de lo alto del aviso: la basa, el fuste con sus estrías y, según
     * [EstadoAviso.semilla], el capitel jónico (dos volutas) o el dórico (el equino y el ábaco), dibujada a línea en el azul
     * del Egeo. Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(losa(oscuro), Offset.Zero, size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.6f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 1 * u)
        val azul = egeo(oscuro)
        val fuste = if (oscuro) mezcla(MARMOL_OSC, Color(0xFF0F0C0A), 0.65f) else MARMOL_LUZ
        val cx = 10 * u
        val arriba = 4 * u; val abajo = h - 4 * u
        if (abajo - arriba < 16 * u) return
        val capitel = arriba + 5 * u
        val basa = abajo - 3 * u
        // El fuste, con su éntasis apenas, y las estrías.
        drawRect(fuste, Offset(cx - 3.2f * u, capitel), Size(6.4f * u, basa - capitel))
        listOf(-3.2f, 3.2f).forEach { d -> drawLine(azul, Offset(cx + d * u, capitel), Offset(cx + d * u, basa), 0.9f * u) }
        listOf(-1.1f, 1.1f).forEach { d -> drawLine(azul.copy(alpha = 0.45f), Offset(cx + d * u, capitel + 1.5f * u), Offset(cx + d * u, basa - 1.5f * u), 0.6f * u) }
        // La basa: dos toros.
        drawRect(fuste, Offset(cx - 5 * u, basa), Size(10 * u, 3 * u))
        drawRect(azul, Offset(cx - 5 * u, basa), Size(10 * u, 3 * u), style = Stroke(0.8f * u))
        drawLine(azul, Offset(cx - 5 * u, basa + 1.5f * u), Offset(cx + 5 * u, basa + 1.5f * u), 0.6f * u)
        if (e.semilla % 2 == 0) {
            // Jónico: el ábaco fino y las dos volutas.
            drawRect(fuste, Offset(cx - 6 * u, arriba), Size(12 * u, 2.4f * u))
            drawRect(azul, Offset(cx - 6 * u, arriba), Size(12 * u, 2.4f * u), style = Stroke(0.8f * u))
            listOf(-1f, 1f).forEach { l ->
                val c = Offset(cx + l * 4.4f * u, arriba + 4.4f * u)
                drawCircle(fuste, 2.1f * u, c)
                drawCircle(azul, 2.1f * u, c, style = Stroke(0.8f * u))
                drawCircle(azul, 0.8f * u, c)
            }
        } else {
            // Dórico: el equino que se abre y el ábaco cuadrado.
            val equino = Path().apply {
                moveTo(cx - 3.2f * u, capitel); lineTo(cx - 5.4f * u, arriba + 2.6f * u); lineTo(cx + 5.4f * u, arriba + 2.6f * u); lineTo(cx + 3.2f * u, capitel); close()
            }
            drawPath(equino, fuste); drawPath(equino, azul, style = Stroke(0.8f * u))
            drawRect(fuste, Offset(cx - 6 * u, arriba), Size(12 * u, 2.6f * u))
            drawRect(azul, Offset(cx - 6 * u, arriba), Size(12 * u, 2.6f * u), style = Stroke(0.8f * u))
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) LLAMA else BARNIZ
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(MARMOL_LUZ, ARCILLA, 0.3f) else mezcla(BARNIZ, ARCILLA, 0.4f)

    /**
     * Cifra: un óstracon, el tiesto de vasija roto donde se escribía el voto: la cara de barro por dentro, de bordes de
     * fractura en ángulo (cada esquina rota a su manera) y, arriba, la franja de barniz negro que quedó del vaso. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 16 * u || h < 16 * u) return
        fun j(k: Int) = (1 + 3 * azar(s, k)) * u
        val tiesto = Path().apply {
            moveTo(j(0), j(1) + 2 * u)
            lineTo(w * 0.42f, j(2)); lineTo(w - j(3) - 3 * u, j(4)); lineTo(w - j(5), h * 0.38f)
            lineTo(w - j(6) - 1 * u, h - j(7)); lineTo(w * 0.55f, h - j(8) * 0.6f); lineTo(j(9) + 2 * u, h - j(10))
            lineTo(j(11) * 0.6f, h * 0.55f); close()
        }
        translate(0.6f * u, 1.4f * u) { drawPath(tiesto, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f)) }
        val barro = if (oscuro) mezcla(ARCILLA, Color(0xFF0F0C0A), 0.8f) else mezcla(ARCILLA, Color.White, 0.5f)
        drawPath(tiesto, barro)
        clipPath(tiesto) {
            drawRect(if (oscuro) Color(0xFF060504) else BARNIZ, Offset.Zero, Size(w, 5.5f * u))
            drawLine(if (oscuro) mezcla(ARCILLA, Color.Black, 0.5f) else ARCILLA, Offset(0f, 6.4f * u), Offset(w, 6.4f * u), 0.7f * u)
        }
        drawPath(tiesto, (if (oscuro) ARCILLA else mezcla(ARCILLA, BARNIZ, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El blanco de los lécitos de fondo blanco, el engobe cremoso sobre el barro. */
    private val ENGOBE = Color(0xFFF7F1E3)

    /** El fondo liso de cada tarjeta, donde va el texto: el mármol de la estela, el barro del friso, la losa de la noche y el engobe. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) hondo(MARMOL, true) else MARMOL_LUZ
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF18120E) else mezcla(ARCILLA, MARMOL_LUZ, 0.5f)
        TipoTarjeta.NOCHE -> if (oscuro) mezcla(EGEO, Color(0xFF0F0C0A), 0.86f) else MARMOL
        TipoTarjeta.VIDA -> if (oscuro) hondo(MARMOL_OSC, true) else ENGOBE
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) MARMOL_LUZ else BARNIZ
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        oscuro -> MARMOL_OSC
        tipo == TipoTarjeta.PENDIENTE -> mezcla(BARNIZ, ROJO_VASO, 0.35f)
        else -> mezcla(BARNIZ, MARMOL_SOMBRA, 0.42f)
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.55f) else EGEO
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(ARCILLA, MARMOL_LUZ, 0.3f) else mezcla(ROJO_VASO, BARNIZ, 0.45f)
        TipoTarjeta.NOCHE -> if (oscuro) LLAMA else EGEO
        TipoTarjeta.VIDA -> if (oscuro) mezcla(ARCILLA, MARMOL_LUZ, 0.3f) else mezcla(ROJO_VASO, BARNIZ, 0.15f)
    }

    /**
     * Tarjetas: cuatro piezas de Grecia. La cifra de Hoy es una estela votiva pequeña: el frontón bajo arriba, con sus tres
     * acroteras, las pilastras finas a los lados y el plinto abajo; el mármol del medio, liso. «Te falta hoy» es un friso de
     * vaso de figuras negras: el barro anaranjado entre dos bandas de barniz, la de arriba con su fila de lengüetas y la de abajo
     * con sus rayos (de noche, al revés, como las figuras rojas: el fondo de barniz y las bandas de barro). La noche es una losa
     * con su filete azul del Egeo y, arriba a la derecha, la luna creciente con las siete Pléyades. La tarjeta de Vida es la
     * cara de un lécito de fondo blanco: el engobe cremoso con la greca corrida arriba, en negro, y dos filetes abajo. Al
     * tocarlas se oscurecen apenas y la sombra se acorta. Destacadas: el frontón se pinta de azul y las acroteras de oro; el
     * friso completo cambia las lengüetas por una fila de puntos de oro; la noche sellada tiene la luna llena, de oro. Sin
     * degradados.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> estela(e, oscuro)
            TipoTarjeta.PENDIENTE -> frisoVaso(e, oscuro)
            TipoTarjeta.NOCHE -> losaNoche(e, oscuro)
            TipoTarjeta.VIDA -> lecito(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, MARMOL_SOMBRA, 0.14f)
    }

    private fun DrawScope.estela(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sombra = Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f)
        val dy = (if (e.presionado) 0.8f else 1.8f) * u
        val fronton = minOf(9 * u, h * 0.09f)
        val plinto = minOf(5 * u, h * 0.06f)
        val cuerpo = Rect(2.5f * u, fronton, w - 2.5f * u, h - plinto - 1.8f * u)
        val base = Rect(0.5f * u, cuerpo.bottom, w - 0.5f * u, h - 1.8f * u)
        val tri = Path().apply { moveTo(1 * u, fronton + 0.5f * u); lineTo(w / 2, 1 * u); lineTo(w - 1 * u, fronton + 0.5f * u); close() }
        translate(0f, dy) { drawRect(sombra, cuerpo.topLeft, cuerpo.size); drawRect(sombra, base.topLeft, base.size) }
        val marmol = fondoTocado(e.tipo, e.presionado, oscuro)
        val canto = if (oscuro) mezcla(MARMOL_OSC, Color(0xFF0F0C0A), 0.62f) else MARMOL_OSC
        drawRect(marmol, cuerpo.topLeft, cuerpo.size)
        // El frontón: el tímpano (azul si se destaca) y su cornisa.
        drawPath(tri, if (e.destacado) mezcla(egeo(oscuro), marmol, 0.15f) else mezcla(marmol, canto, 0.35f))
        drawPath(tri, canto, style = Stroke(1 * u))
        drawRect(canto, Offset(0.5f * u, fronton), Size(w - 1 * u, 1.2f * u))
        // Las acroteras: una arriba y dos en las puntas.
        val acrotera = if (e.destacado) (if (oscuro) LLAMA else ORO) else canto
        drawCircle(acrotera, 1.5f * u, Offset(w / 2, 1.6f * u))
        drawCircle(acrotera, 1.3f * u, Offset(2 * u, fronton - 0.6f * u))
        drawCircle(acrotera, 1.3f * u, Offset(w - 2 * u, fronton - 0.6f * u))
        // Las pilastras, finas, a los lados.
        val pilastra = canto.copy(alpha = 0.8f)
        listOf(cuerpo.left + 2.5f * u, cuerpo.right - 2.5f * u).forEach { x ->
            drawLine(pilastra, Offset(x, cuerpo.top + 1.2f * u), Offset(x, cuerpo.bottom), 0.7f * u)
            drawRect(pilastra, Offset(x - 1.6f * u, cuerpo.top + 1.2f * u), Size(3.2f * u, 1 * u))
        }
        // El plinto, más ancho.
        drawRect(mezcla(marmol, canto, 0.3f), base.topLeft, base.size)
        drawLine(canto, Offset(base.left, base.top), Offset(base.right, base.top), 0.8f * u)
        drawRect(canto.copy(alpha = 0.6f), cuerpo.topLeft, cuerpo.size, style = Stroke(0.6f * u))
    }

    private fun DrawScope.frisoVaso(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val completo = e.destacado
        val radio = CornerRadius(3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u), radio)
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        // De día las bandas son de barniz negro; de noche, de barro (como las figuras rojas).
        val banda = if (oscuro) mezcla(ARCILLA, Color(0xFF0F0C0A), 0.3f) else BARNIZ
        val arriba = minOf(3.5f * u, h * 0.06f)
        clipPath(Path().apply { addRoundRect(RoundRect(r, radio)) }) {
            drawRect(banda, Offset.Zero, Size(w, arriba))
            drawRect(banda, Offset(0f, r.bottom - arriba * 0.8f), Size(w, arriba * 0.8f))
        }
        // Una raya fina bajo la banda de arriba.
        drawLine(banda, Offset(4 * u, arriba + 1.2f * u), Offset(w - 4 * u, arriba + 1.2f * u), 0.6f * u)
        val paso = maxOf(6 * u, 1f)
        val n = ((w - 8 * u) / paso).toInt().coerceIn(1, 120)
        val x0 = (w - n * paso) / 2
        if (completo) {
            // Una fila de puntos de oro en lugar de las lengüetas.
            val oro = if (oscuro) LLAMA else ORO
            for (j in 0 until n) drawCircle(oro, 0.9f * u, Offset(x0 + (j + 0.5f) * paso, arriba + 3.4f * u))
        } else {
            // Las lengüetas que cuelgan de la banda de arriba, una sí y una no más largas.
            for (j in 0 until n) {
                val largo = (if (j % 2 == 0) 5.5f else 4f) * u
                drawRoundRect(banda, Offset(x0 + j * paso + 1 * u, arriba + 1.8f * u), Size(paso - 2 * u, largo), CornerRadius((paso - 2 * u) / 2))
            }
            // Los rayos que suben desde la banda de abajo.
            val yb = r.bottom - arriba * 0.8f
            for (j in 0 until n) {
                val cx = x0 + (j + 0.5f) * paso
                drawPath(Path().apply { moveTo(cx - paso * 0.32f, yb); lineTo(cx, yb - 4.5f * u); lineTo(cx + paso * 0.32f, yb); close() }, banda)
            }
        }
        drawRoundRect(banda.copy(alpha = 0.6f), r.topLeft, r.size, radio, style = Stroke(0.8f * u))
    }

    private fun DrawScope.losaNoche(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u))
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        if (!oscuro) drawRect(MARMOL_SOMBRA.copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.7f * u))
        // El filete azul del Egeo, con una llave de la greca en cada esquina de abajo.
        val azul = egeo(oscuro).copy(alpha = if (oscuro) 0.7f else 0.85f)
        val f = r.deflate(4 * u)
        drawRect(azul, f.topLeft, f.size, style = Stroke(0.9f * u))
        llave(Offset(f.left + 1.6f * u, f.bottom - 1.6f * u), 4.5f * u, 1f, -1f, azul, 0.8f * u)
        llave(Offset(f.right - 1.6f * u, f.bottom - 1.6f * u), 4.5f * u, -1f, -1f, azul, 0.8f * u)
        // La luna, arriba a la derecha: creciente, o llena si el día está sellado; y las Pléyades a su lado.
        val c = Offset(w - 22 * u, minOf(15 * u, h * 0.18f))
        val rl = 6.5f * u
        val luna = if (oscuro) LLAMA else mezcla(LLAMA, ORO, 0.55f)
        if (sellado) {
            drawCircle(luna.copy(alpha = 0.25f), rl * 1.5f, c)
            drawCircle(luna, rl, c)
            drawCircle(Color.White.copy(alpha = 0.3f), rl * 0.3f, c + Offset(-rl * 0.3f, -rl * 0.25f))
        } else {
            val disco = Path().apply { addOval(Rect(c.x - rl, c.y - rl, c.x + rl, c.y + rl)) }
            val sombra = Path().apply { addOval(Rect(c.x - rl * 0.55f, c.y - rl * 1.1f, c.x + rl * 1.5f, c.y + rl * 0.9f)) }
            drawPath(Path().apply { op(disco, sombra, PathOperation.Difference) }, luna)
        }
        val estrella = (if (oscuro) MARMOL_LUZ else EGEO).copy(alpha = 0.85f)
        val pleyades = floatArrayOf(-17f, -4f, -14.5f, -6.5f, -12f, -3.5f, -15.5f, -1f, -11f, -7.5f, -19f, -1.5f, -13f, 0.5f)
        for (j in 0 until 7) drawCircle(estrella, (if (j < 3) 0.85f else 0.6f) * u, Offset(c.x + pleyades[2 * j] * u, c.y + pleyades[2 * j + 1] * u))
    }

    private fun DrawScope.lecito(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(5 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(0f, (if (e.presionado) 0.8f else 1.6f) * u), Size(w, h - 1.6f * u), radio)
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        val negro = if (oscuro) mezcla(MARMOL_OSC, Color(0xFF0F0C0A), 0.25f) else BARNIZ
        val barro = if (oscuro) mezcla(ARCILLA, Color(0xFF0F0C0A), 0.25f) else ARCILLA
        // La greca corrida, arriba: una fila de llaves sobre su raya.
        val alto = minOf(4.5f * u, h * 0.07f)
        val y0 = 2.6f * u
        val paso = maxOf(alto * 1.25f, 1f)
        val n = ((w - 10 * u) / paso).toInt().coerceIn(1, 120)
        val x0 = (w - n * paso) / 2
        drawLine(negro, Offset(x0, y0 - 0.9f * u), Offset(x0 + n * paso, y0 - 0.9f * u), 0.6f * u)
        drawLine(negro, Offset(x0, y0 + alto + 0.8f * u), Offset(x0 + n * paso, y0 + alto + 0.8f * u), 0.6f * u)
        for (j in 0 until n) llave(Offset(x0 + j * paso, y0), alto, 1f, 1f, negro, 0.7f * u)
        // Los dos filetes de abajo: uno de barro y uno de barniz.
        drawLine(barro, Offset(5 * u, r.bottom - 4.2f * u), Offset(w - 5 * u, r.bottom - 4.2f * u), 0.8f * u)
        drawLine(negro, Offset(5 * u, r.bottom - 2.6f * u), Offset(w - 5 * u, r.bottom - 2.6f * u), 1.1f * u)
        drawRoundRect(negro.copy(alpha = if (oscuro) 0.4f else 0.25f), r.topLeft, r.size, radio, style = Stroke(0.7f * u))
        if (e.destacado) drawRoundRect(egeo(oscuro), r.deflate(1 * u).topLeft, r.deflate(1 * u).size, CornerRadius(4 * u), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- Gastos

    /**
     * Una greca corrida (el meandro) de [x0] a [x1], de [alto] con la base en [base]: una llave tras otra, cada una de [alto] de
     * ancho más su paso, unidas por la raya de abajo. A lo más 80 llaves.
     */
    private fun DrawScope.grecaCorrida(x0: Float, x1: Float, base: Float, alto: Float, color: Color, grosor: Float) {
        val paso = maxOf(alto * 1.45f, 1f)
        val n = ((x1 - x0) / paso).toInt().coerceIn(0, 80)
        if (n == 0) return
        val sobra = (x1 - x0 - n * paso) / 2
        for (j in 0 until n) llave(Offset(x0 + sobra + j * paso + alto * 0.2f, base - alto), alto, 1f, 1f, color, grosor)
        drawLine(color, Offset(x0, base), Offset(x1, base), grosor)
    }

    override val altoBalanza: Dp = 60.dp

    /**
     * Balanza: un friso de mármol a todo el ancho, con dos metopas separadas por un triglifo (tres canales) y, abajo, la greca
     * corrida. La metopa elegida tiene la cara pulida y clara, el filete del Egeo grueso con otro de oro por dentro y la greca de
     * debajo pintada de azul; la otra queda en mármol sin pulir, más gris, con la greca apenas grabada y el texto delgado.
     * Sin degradados.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 30 * u) return
        val losa = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRect(if (oscuro) mezcla(MARMOL_SOMBRA, Color(0xFF0F0C0A), 0.72f) else MARMOL_OSC, losa.topLeft, losa.size)
        drawRect((if (oscuro) Color.Black else MARMOL_SOMBRA).copy(alpha = 0.7f), losa.topLeft, losa.size, style = Stroke(1 * u))
        val xm = w / 2
        val baseGreca = losa.bottom - 3 * u
        val altoGreca = 6.5f * u
        val abajo = baseGreca - altoGreca - 3.5f * u
        // El triglifo del medio, con sus tres canales.
        val tri = Rect(xm - 7 * u, losa.top + 3 * u, xm + 7 * u, abajo)
        drawRect(if (oscuro) mezcla(MARMOL_SOMBRA, Color(0xFF0F0C0A), 0.55f) else MARMOL_SOMBRA, tri.topLeft, tri.size)
        for (j in -1..1) drawLine((if (oscuro) Color.Black else BARNIZ).copy(alpha = 0.45f), Offset(xm + j * 4 * u, tri.top + 2 * u), Offset(xm + j * 4 * u, tri.bottom - 1 * u), 1.4f * u)
        for (lado in 0..1) {
            val elegida = (lado == 1) == e.ingreso
            val r = if (lado == 0) Rect(losa.left + 4 * u, losa.top + 3 * u, tri.left - 3 * u, abajo)
            else Rect(tri.right + 3 * u, losa.top + 3 * u, losa.right - 4 * u, abajo)
            val fondo = when {
                elegida && oscuro -> mezcla(MARMOL, Color(0xFF0F0C0A), 0.7f)
                elegida -> MARMOL_LUZ
                oscuro -> mezcla(MARMOL, Color(0xFF0F0C0A), 0.86f)
                else -> mezcla(MARMOL, MARMOL_OSC, 0.6f)
            }
            drawRect(fondo, r.topLeft, r.size)
            val gx0 = if (lado == 0) losa.left + 4 * u else xm + 2 * u
            val gx1 = if (lado == 0) xm - 2 * u else losa.right - 4 * u
            if (elegida) {
                drawRect(egeo(oscuro), r.topLeft, r.size, style = Stroke(2.2f * u))
                drawRect(if (oscuro) LLAMA.copy(alpha = 0.75f) else ORO, r.topLeft + Offset(4 * u, 4 * u), Size(r.width - 8 * u, r.height - 8 * u), style = Stroke(0.9f * u))
                grecaCorrida(gx0, gx1, baseGreca, altoGreca, egeo(oscuro), 1.3f * u)
            } else {
                drawRect(MARMOL_SOMBRA.copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.8f * u))
                grecaCorrida(gx0, gx1, baseGreca, altoGreca, (if (oscuro) MARMOL_OSC else MARMOL_SOMBRA).copy(alpha = 0.45f), 1 * u)
            }
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val caja = Rect(r.left + 10 * u, r.top + 5 * u, r.right - 10 * u, r.bottom - 5 * u)
            if (elegida) textoEn(medidor, texto, caja, Letra.garamond, legibleAnt(base, 5f, fondo), 21f, peso = FontWeight.Bold, espaciado = 0.4f)
            else textoEn(medidor, texto, caja, Letra.garamond, legibleAnt(mezcla(base, fondo, 0.2f), 4.7f, fondo), 18f, espaciado = 0.4f)
        }
    }

    override val altoMes: Dp = 52.dp

    /**
     * Encabezado del mes: un dintel de mármol a todo el ancho, con un filete del Egeo arriba y abajo; al centro, la cartela lisa
     * con el mes, enmarcada por un filete fino con una llave de la greca en cada esquina. En los extremos del dintel, las
     * flechas de las filas (con sus plumas azules), grandes. Sin degradados.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 24 * u) return
        val dintel = Rect(1 * u, 2 * u, w - 1 * u, h - 2 * u)
        val fondo = losa(oscuro)
        drawRect(fondo, dintel.topLeft, dintel.size)
        drawRect((if (oscuro) Color.Black else MARMOL_SOMBRA).copy(alpha = 0.7f), dintel.topLeft, dintel.size, style = Stroke(1 * u))
        val azul = egeo(oscuro)
        drawLine(azul, Offset(dintel.left, dintel.top + 2.5f * u), Offset(dintel.right, dintel.top + 2.5f * u), 1.6f * u)
        drawLine(azul, Offset(dintel.left, dintel.bottom - 2.5f * u), Offset(dintel.right, dintel.bottom - 2.5f * u), 1.6f * u)
        val r = Rect(54 * u, dintel.top + 6 * u, w - 54 * u, dintel.bottom - 6 * u)
        val filete = (if (oscuro) MARMOL_OSC else MARMOL_SOMBRA).copy(alpha = 0.8f)
        drawRect(filete, r.topLeft, r.size, style = Stroke(0.9f * u))
        val k = 7 * u
        llave(Offset(r.left + 3 * u, r.top + 3 * u), k, 1f, 1f, azul, 1.1f * u)
        llave(Offset(r.right - 3 * u, r.top + 3 * u), k, -1f, 1f, azul, 1.1f * u)
        llave(Offset(r.left + 3 * u, r.bottom - 3 * u), k, 1f, -1f, azul, 1.1f * u)
        llave(Offset(r.right - 3 * u, r.bottom - 3 * u), k, -1f, -1f, azul, 1.1f * u)
        textoEn(medidor, e.titulo, Rect(r.left + 16 * u, r.top, r.right - 16 * u, r.bottom), Letra.garamond,
            legibleAnt(if (oscuro) MARMOL_LUZ else BARNIZ, 7f, fondo), 24f, peso = FontWeight.SemiBold, espaciado = 0.6f)
        flechasMesAnt(48 * u) { flechaFila(oscuro) }
    }

    /**
     * Asiento: una raya fina grabada en el mármol, abajo, y al principio una llave pequeña de la greca del color del tipo: rojo
     * de vaso si es un gasto, azul del Egeo si es un ingreso. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.4f * u
        drawLine((if (oscuro) MARMOL_OSC else MARMOL_SOMBRA).copy(alpha = if (oscuro) 0.22f else 0.45f), Offset(13 * u, y), Offset(w - 2 * u, y), 0.8f * u)
        val c = if (e.ingreso) egeo(oscuro) else if (oscuro) mezcla(ROJO_VASO, MARMOL_LUZ, 0.4f) else ROJO_VASO
        llave(Offset(2 * u, y), 6 * u, 1f, -1f, c.copy(alpha = 0.85f), 1.1f * u)
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * Rótulo del día: una cartela de figuras rojas, como la banda de un vaso: el barniz negro con la fecha en el rojo del barro, y
     * de ahí hacia la derecha la greca corrida, fina y tenue. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 10 * u) return
        val tam = 13f
        val ancho = minOf(anchoTextoAnt(medidor, texto, Letra.garamond, tam, FontWeight.Bold, 1.4f) + 2 * u, w * 0.6f)
        val r = Rect(1 * u, 1 * u, ancho + 17 * u, h - 1 * u)
        val fondo = BARNIZ
        drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(1.5f * u))
        if (oscuro) drawRoundRect(MARMOL_SOMBRA.copy(alpha = 0.6f), r.topLeft, r.size, CornerRadius(1.5f * u), style = Stroke(0.8f * u))
        // Las dos rayas de barro de la banda, arriba y abajo.
        drawLine(ARCILLA.copy(alpha = 0.7f), Offset(r.left + 2 * u, r.top + 2.2f * u), Offset(r.right - 2 * u, r.top + 2.2f * u), 0.7f * u)
        drawLine(ARCILLA.copy(alpha = 0.7f), Offset(r.left + 2 * u, r.bottom - 2.2f * u), Offset(r.right - 2 * u, r.bottom - 2.2f * u), 0.7f * u)
        textoEn(medidor, texto, Rect(r.left + 8 * u, r.top + 2.5f * u, r.left + 8 * u + ancho, r.bottom - 2.5f * u), Letra.garamond,
            legibleAnt(ARCILLA, 5f, fondo), tam, peso = FontWeight.Bold, espaciado = 1.4f)
        val alto = minOf(7 * u, h * 0.4f)
        grecaCorrida(r.right + 8 * u, w - 2 * u, h / 2 + alto / 2, alto, egeo(oscuro).copy(alpha = if (oscuro) 0.5f else 0.42f), 0.9f * u)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /** Una tira de llaves de la greca, de [k] de alto, entre [x0] y [x1] con la base en [y]: las llaves y la raya de abajo. A lo más 80. */
    private fun DrawScope.tiraGreca(x0: Float, x1: Float, y: Float, k: Float, color: Color, grosor: Float) {
        val paso = maxOf(k * 1.45f, 1f)
        val n = ((x1 - x0) / paso).toInt().coerceIn(0, 80)
        if (n == 0) return
        val x = x0 + ((x1 - x0) - n * paso + (paso - k)) / 2
        for (j in 0 until n) llave(Offset(x + j * paso, y), k, 1f, -1f, color, grosor)
        drawLine(color, Offset(x0, y + grosor * 1.5f), Offset(x1, y + grosor * 1.5f), grosor)
    }

    /**
     * Hoja rápida: la fachada de un templo. Bajo el frontón de la hoja, la moldura que sigue sus dos aguas y la cornisa horizontal
     * con su hilera de dentículos; en el tímpano, el sol de Vergina de dieciséis rayos; por los dos cantos, una columna jónica con
     * sus estrías, el capitel de volutas arriba y la basa abajo; y abajo, una tira de greca y el escalón del estilóbato. Sin
     * degradados.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        val linea = alfaAnt(e.primario, 0.5f)
        val tenue = alfaAnt(e.primario, 0.22f)
        // Las dos aguas y la cornisa.
        drawPath(Path().apply { moveTo(0f, 18.5f * u); lineTo(w / 2, 4.5f * u); lineTo(w, 18.5f * u) }, linea, style = Stroke(1f * u))
        drawLine(linea, Offset(0f, 19.5f * u), Offset(w, 19.5f * u), 1f * u)
        // Los dentículos bajo la cornisa, entre los capiteles.
        var x = 18 * u; var k = 0
        while (x < w - 20 * u && k < 120) { drawRect(alfaAnt(e.primario, 0.3f), Offset(x, 20.8f * u), Size(2.4f * u, 2.6f * u)); x += maxOf(5 * u, 1f); k++ }
        // El sol en el tímpano.
        val sol = Offset(w / 2, 12.2f * u)
        drawPath(estrella(sol, 4.6f * u, 16, 0.5f), alfaAnt(e.secundario, 0.7f))
        drawCircle(alfaAnt(e.primario, 0.8f), 1.5f * u, sol)
        // Las columnas de los cantos: estrías, capitel de volutas y basa.
        val alto = h - 16 * u
        listOf(8.5f * u, w - 8.5f * u).forEach { cx ->
            for (j in -1..1) drawLine(tenue, Offset(cx + j * 2.8f * u, 28 * u), Offset(cx + j * 2.8f * u, alto - 3 * u), 0.7f * u)
            drawRect(linea, Offset(cx - 5.5f * u, 21.5f * u), Size(11 * u, 1.6f * u))
            listOf(-1f, 1f).forEach { s ->
                drawCircle(linea, 1.6f * u, Offset(cx + s * 3.6f * u, 25.2f * u), style = Stroke(0.8f * u))
                drawCircle(linea, 0.5f * u, Offset(cx + s * 3.6f * u, 25.2f * u))
            }
            drawLine(linea, Offset(cx - 3.6f * u, 23.6f * u), Offset(cx + 3.6f * u, 23.6f * u), 0.8f * u)
            drawLine(linea, Offset(cx - 4.5f * u, alto - 2.5f * u), Offset(cx + 4.5f * u, alto - 2.5f * u), 1f * u)
            drawLine(linea, Offset(cx - 5.5f * u, alto), Offset(cx + 5.5f * u, alto), 1.2f * u)
        }
        // Abajo, la greca entre las basas y el estilóbato.
        tiraGreca(18 * u, w - 18 * u, h - 11 * u, 4.4f * u, alfaAnt(e.primario, 0.32f), 0.8f * u)
        drawLine(linea, Offset(0f, h - 6 * u), Offset(w, h - 6 * u), 1f * u)
        drawLine(tenue, Offset(0f, h - 3 * u), Offset(w, h - 3 * u), 0.8f * u)
    }

    /**
     * Relación: un friso de meandros arriba y otro abajo, las llaves de la greca sobre su raya, y un punto de oro en cada esquina;
     * el centro, liso. Sin degradados.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val c = alfaAnt(e.primario, 0.45f)
        tiraGreca(8 * u, w - 8 * u, 8 * u, 4.4f * u, c, 0.8f * u)
        tiraGreca(8 * u, w - 8 * u, h - 5 * u, 4.4f * u, c, 0.8f * u)
        listOf(Offset(4.5f * u, 4.5f * u), Offset(w - 4.5f * u, 4.5f * u), Offset(4.5f * u, h - 4.5f * u), Offset(w - 4.5f * u, h - 4.5f * u))
            .forEach { drawCircle(alfaAnt(e.secundario, 0.6f), 1.2f * u, it) }
        listOf(4.5f * u, w - 4.5f * u).forEach { x -> drawLine(alfaAnt(e.primario, 0.25f), Offset(x, 10 * u), Offset(x, h - 10 * u), 0.7f * u) }
    }

    /**
     * Pista: la fachada del templo: el frontón con el sol en su tímpano, dos columnas a los lados y las dos gradas abajo. Sin
     * degradados.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f
        val x0 = (size.width - l) / 2; val y0 = (size.height - l) / 2
        fun p(x: Float, y: Float) = Offset(x0 + x * l, y0 + y * l)
        val c = e.primario
        drawPath(Path().apply { p(0.04f, 0.19f).let { moveTo(it.x, it.y) }; p(0.5f, 0.03f).let { lineTo(it.x, it.y) }; p(0.96f, 0.19f).let { lineTo(it.x, it.y) }; close() },
            c, style = Stroke(g * 0.8f))
        drawCircle(e.secundario, l * 0.032f, p(0.5f, 0.125f))
        listOf(0.1f, 0.9f).forEach { x -> drawLine(c, p(x, 0.24f), p(x, 0.84f), g * 0.75f) }
        listOf(0.165f, 0.835f).forEach { x -> drawLine(alfaAnt(c, 0.6f), p(x, 0.26f), p(x, 0.82f), g * 0.4f) }
        drawLine(c, p(0.12f, 0.875f), p(0.88f, 0.875f), g * 0.6f)
        drawLine(c, p(0.04f, 0.95f), p(0.96f, 0.95f), g)
    }
}

/**
 * Egipto: el campo es la tablilla del escriba, de madera cubierta de yeso blanco, liso, con la banda de rectángulos de colores
 * (azul de lapislázuli, rojo y verde) pintada arriba y abajo entre dos rayas negras, como el borde de las pinturas de las
 * tumbas. Con el foco la banda toma todo su color y el borde del yeso se marca de lapislázuli; con un error, de rojo. Con
 * varios renglones, las líneas rojas de guía, apenas visibles. Sin degradados.
 */
object ComunEgipto : DisenoComun {
    private val MADERA = Color(0xFF8E6A3E)
    private val MADERA_VETA = Color(0xFF5E4424)
    private val YESO = Color(0xFFF2EBDA)
    private val LAPIS = Color(0xFF1D3A8A)
    private val ROJO = Color(0xFFB0432A)
    private val VERDE = Color(0xFF3E7A4A)
    private val AMARILLO = Color(0xFFF2CF4A)
    private val NEGRO = Color(0xFF1E160C)
    private val LINO = Color(0xFFE6D8B4)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110D08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110D08), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 12.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(YESO, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9DB0EE) else LAPIS
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A70) else ROJO

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val yeso = hondo(YESO, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, yeso, 0.5f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, CornerRadius(5 * u))
        // La tabla de madera y el yeso encima.
        drawRoundRect(a(t(MADERA, oscuro)), r.topLeft, r.size, CornerRadius(5 * u))
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, a(t(MADERA_VETA, oscuro)).copy(alpha = 0.35f), s, 0.6f) }
        val y = Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.bottom - 3 * u)
        drawRoundRect(yeso, y.topLeft, y.size, CornerRadius(3 * u))
        // Las líneas rojas de guía, con varios renglones.
        val guia = (if (oscuro) mezcla(ROJO, AMARILLO, 0.3f) else ROJO).copy(alpha = if (oscuro) 0.2f else 0.15f)
        if (e.multilinea) renglonesAntiguedad(y.left + 8 * u, y.right - 8 * u, margenCampoY.toPx(), y.bottom - 8 * u, guia, 0.7f * u)
        // La banda de rectángulos de colores, arriba y abajo, entre rayas negras.
        val encendido = e.enfocado && e.habilitado
        val viveza = if (encendido) 1f else 0.6f
        val colores = listOf(LAPIS, ROJO, VERDE)
        val raya = a(t(NEGRO, oscuro)).copy(alpha = if (oscuro) 0.7f else 0.55f)
        val paso = maxOf(7 * u, 1f)
        listOf(y.top + 2 * u, y.bottom - 4.6f * u).forEach { yb ->
            drawLine(raya, Offset(y.left + 3 * u, yb - 0.7f * u), Offset(y.right - 3 * u, yb - 0.7f * u), 0.6f * u)
            drawLine(raya, Offset(y.left + 3 * u, yb + 3.3f * u), Offset(y.right - 3 * u, yb + 3.3f * u), 0.6f * u)
            var x = y.left + 4 * u; var k = 0
            while (x + 5 * u < y.right - 3 * u && k < 300) {
                val col = colores[k % 3].let { if (oscuro) mezcla(it, Color.White, 0.18f) else it }
                drawRect(a(col).copy(alpha = viveza), Offset(x, yb), Size(5 * u, 2.6f * u))
                x += paso; k++
            }
        }
        // El borde del yeso: lapislázuli con el foco, rojo con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> rotuloCampo(oscuro)
            else -> a(t(MADERA_VETA, oscuro)).copy(alpha = 0.45f)
        }
        val g = (if (e.error || encendido) 1.8f else 0.8f) * u
        drawRoundRect(linea, Offset(y.left + g / 2, y.top + g / 2), Size(y.width - g, y.height - g), CornerRadius(3 * u), style = Stroke(g))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Separador: una raya negra fina, como las que encierran las bandas de las tumbas, y al centro un tramo corto de la banda de
     * rectángulos de colores (lapislázuli, rojo, verde, rojo, lapislázuli) entre dos rayas más finas. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) LINO.copy(alpha = 0.22f) else NEGRO.copy(alpha = 0.38f)).let { it.copy(alpha = it.alpha * f) }
        val colores = listOf(LAPIS, ROJO, VERDE, ROJO, LAPIS)
        val paso = 6 * u
        val ancho = colores.size * paso - 1.6f * u
        val x0 = w / 2 - ancho / 2
        colores.forEachIndexed { j, c ->
            val col = if (oscuro) mezcla(c, Color.White, 0.22f) else c
            drawRect(col.copy(alpha = (if (oscuro) 0.75f else 0.8f) * f), Offset(x0 + j * paso, cy - 1.3f * u), Size(4.4f * u, 2.6f * u))
        }
        drawLine(raya, Offset(x0 - 2 * u, cy - 2.6f * u), Offset(x0 + ancho + 2 * u, cy - 2.6f * u), 0.6f * u)
        drawLine(raya, Offset(x0 - 2 * u, cy + 2.6f * u), Offset(x0 + ancho + 2 * u, cy + 2.6f * u), 0.6f * u)
        val hueco = ancho / 2 + 6 * u
        if (w / 2 - hueco > 4 * u) {
            drawLine(raya, Offset(2 * u, cy), Offset(w / 2 - hueco, cy), 1 * u)
            drawLine(raya, Offset(w / 2 + hueco, cy), Offset(w - 2 * u, cy), 1 * u)
        }
    }

    /** El texto de los botones: el lapislázuli del tema de día, el oro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3B341) else Color(0xFF1D2F7A)

    /**
     * Botón de texto: un cartucho, el óvalo alargado que encierra los nombres reales, sugerido solo por sus dos extremos
     * curvos, como un paréntesis a cada lado de la palabra, y la barra de la cuerda atada a la derecha. Al tocarlo el cartucho
     * se cierra, se llena de oro tenue y se marca; apagado, casi no se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val lapis = if (oscuro) Color(0xFF9DB0EE) else LAPIS
        val alto = 26 * u
        val izq = 3 * u; val der = w - 6 * u
        if (der - izq < alto * 0.6f) return
        val radio = CornerRadius(minOf(alto, der - izq) / 2)
        val o = Offset(izq, cy - alto / 2); val tam = Size(der - izq, alto)
        val color = lapis.copy(alpha = (if (tocado) 0.8f else 0.5f) * apagado)
        val g = (if (tocado) 1.2f else 1f) * u
        if (tocado) {
            drawRoundRect(AMARILLO.copy(alpha = if (oscuro) 0.16f else 0.24f), o, tam, radio)
            drawRoundRect(color, o, tam, radio, style = Stroke(g))
        } else {
            val d = minOf(alto, der - izq)
            drawArc(color, 115f, 130f, false, Offset(izq, cy - alto / 2), Size(d, alto), style = Stroke(g, cap = StrokeCap.Round))
            drawArc(color, -65f, 130f, false, Offset(der - d, cy - alto / 2), Size(d, alto), style = Stroke(g, cap = StrokeCap.Round))
        }
        drawLine(color, Offset(der + 2.2f * u, cy - 9 * u), Offset(der + 2.2f * u, cy + 9 * u), (if (tocado) 1.6f else 1.3f) * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papiro de la hoja del vacío. */
    private val PAPIRO = Color(0xFFF0E4C0)
    /** La fayenza, la loza vidriada de color turquesa. */
    private val FAYENZA = Color(0xFF2E8C8A)
    /** El lapislázuli y el verde aclarados para la noche. */
    private val LAPIS_NOCHE = Color(0xFF9DB0EE)
    private val VERDE_NOCHE = Color(0xFF8CC79A)

    override val margenFila: Dp = 12.dp

    /**
     * Fila: abajo, la raya negra fina que encierra las bandas de las tumbas, y a la izquierda una columnita de tres rectángulos
     * de color (lapislázuli, rojo y verde) de la banda. Al tocarla el yeso se calienta de amarillo y los rectángulos toman todo
     * su color. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(AMARILLO.copy(alpha = if (oscuro) 0.08f else 0.16f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine((if (oscuro) LINO.copy(alpha = 0.16f) else NEGRO.copy(alpha = 0.26f)), Offset(x0, h - 1 * u), Offset(w, h - 1 * u), 0.7f * u)
        val viveza = if (e.presionado) 1f else 0.6f
        val colores = listOf(LAPIS, ROJO, VERDE)
        val alto = if (e.conDetalle) 3.6f * u else 2.6f * u
        val y0 = h / 2 - (3 * alto + 2 * u) / 2
        colores.forEachIndexed { j, c ->
            val col = if (oscuro) mezcla(c, Color.White, 0.25f) else c
            drawRect(col.copy(alpha = viveza), Offset(3.6f * u, y0 + j * (alto + 1 * u)), Size(3.4f * u, alto))
        }
    }

    /**
     * Flecha: un capullo de loto acostado, con la punta a la derecha: el tallo verde, los dos sépalos que lo abrazan y el
     * capullo de lapislázuli. Sin degradados.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val verde = if (oscuro) VERDE_NOCHE else VERDE
        val lapis = if (oscuro) LAPIS_NOCHE else LAPIS
        drawLine(verde, Offset(w * 0.06f, cy + h * 0.04f), Offset(w * 0.46f, cy), 1.2f * u, StrokeCap.Round)
        val capullo = Path().apply {
            moveTo(w * 0.42f, cy)
            cubicTo(w * 0.5f, cy - h * 0.28f, w * 0.78f, cy - h * 0.16f, w * 0.94f, cy)
            cubicTo(w * 0.78f, cy + h * 0.16f, w * 0.5f, cy + h * 0.28f, w * 0.42f, cy)
            close()
        }
        drawPath(capullo, lapis)
        listOf(-1f, 1f).forEach { l ->
            drawPath(Path().apply {
                moveTo(w * 0.4f, cy); quadraticTo(w * 0.5f, cy + l * h * 0.3f, w * 0.72f, cy + l * h * 0.14f); quadraticTo(w * 0.56f, cy + l * h * 0.1f, w * 0.4f, cy); close()
            }, verde)
        }
    }

    /**
     * Marca de fijar: un escarabajo sagrado visto desde arriba: la cabeza con su escudo, el coselete, los élitros partidos por la
     * raya del medio y las seis patas. Fijo, de lapislázuli con el contorno de oro; sin fijar, solo su contorno, tenue. Sin
     * degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val ox = (size.width - 24 * k) / 2; val oy = (size.height - 24 * k) / 2
        fun p(x: Float, y: Float) = Offset(ox + x * k, oy + y * k)
        val lapis = if (oscuro) LAPIS_NOCHE else LAPIS
        val oro = if (oscuro) AMARILLO else mezcla(AMARILLO, MADERA_VETA, 0.45f)
        val linea = if (fija) oro else (if (oscuro) LINO else NEGRO).copy(alpha = 0.45f)
        val g = (if (fija) 0.8f else 0.75f) * u
        // Las patas, de a tres por lado.
        listOf(floatArrayOf(8f, 10.5f, 4f, 8.5f), floatArrayOf(7.4f, 14f, 3.5f, 15f), floatArrayOf(8f, 17.5f, 5f, 21f)).forEach { a ->
            drawLine(linea, p(a[0], a[1]), p(a[2], a[3]), g * 1.2f, StrokeCap.Round)
            drawLine(linea, p(24 - a[0], a[1]), p(24 - a[2], a[3]), g * 1.2f, StrokeCap.Round)
        }
        val cabeza = Rect(p(9.4f, 3.2f), p(14.6f, 7.6f))
        val coselete = Rect(p(7.4f, 6.6f), p(16.6f, 11.4f))
        val elitros = Rect(p(7f, 10.6f), p(17f, 21.4f))
        if (fija) {
            drawOval(lapis, cabeza.topLeft, cabeza.size); drawOval(lapis, coselete.topLeft, coselete.size); drawOval(lapis, elitros.topLeft, elitros.size)
        }
        drawOval(linea, cabeza.topLeft, cabeza.size, style = Stroke(g))
        drawOval(linea, coselete.topLeft, coselete.size, style = Stroke(g))
        drawOval(linea, elitros.topLeft, elitros.size, style = Stroke(g))
        drawLine(linea, p(12f, 11.4f), p(12f, 21f), g)
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, Color(0xFFB9A887), 0.4f) else Color(0xFF55452A)

    /** El papiro con la luz del momento: de noche, en penumbra. */
    private fun papiro(oscuro: Boolean): Color = if (oscuro) mezcla(PAPIRO, Color(0xFF110D08), 0.84f) else PAPIRO

    /**
     * Vacío: una hoja de papiro sin escribir, con sus fibras cruzadas (las de la cara, a lo largo, y las de atrás, de pie, más
     * tenues) y en cada punta el borde enrollado del rollo, con su luz. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 12 * u) return
        val rollo = 6 * u
        drawRect(papiro(oscuro), Offset(rollo, 0f), Size(w - 2 * rollo, h))
        val fibra = if (oscuro) LINO.copy(alpha = 0.06f) else MADERA.copy(alpha = 0.1f)
        val pasoY = maxOf(3.6f * u, 1f)
        var y = 2 * u; var k = 0
        while (y < h - 1 * u && k < 120) {
            drawLine(fibra, Offset(rollo, y), Offset(w - rollo, y + (azar(s, k) - 0.5f) * 0.8f * u), (0.5f + 0.5f * azar(s, k + 50)) * u)
            y += pasoY; k++
        }
        val pasoX = maxOf(8 * u, 1f)
        var x = rollo + 4 * u; k = 0
        while (x < w - rollo && k < 120) {
            drawLine(fibra.copy(alpha = fibra.alpha * 0.55f), Offset(x, 0f), Offset(x, h), 0.6f * u)
            x += pasoX; k++
        }
        // Los bordes enrollados.
        val enrollado = if (oscuro) mezcla(PAPIRO, Color(0xFF110D08), 0.7f) else mezcla(PAPIRO, MADERA, 0.3f)
        listOf(0f, w - rollo).forEach { x0 ->
            drawRoundRect(enrollado, Offset(x0, 0f), Size(rollo, h), CornerRadius(rollo / 2))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), Offset(x0 + rollo * 0.35f, 2 * u), Offset(x0 + rollo * 0.35f, h - 2 * u), 1 * u)
            drawLine(MADERA_VETA.copy(alpha = if (oscuro) 0.5f else 0.35f), Offset(x0 + rollo * 0.8f, 1 * u), Offset(x0 + rollo * 0.8f, h - 1 * u), 0.7f * u)
        }
    }

    /**
     * Lo vacío: un naos, la capilla de madera donde se guardaba la imagen del dios, con las dos puertas abiertas y nada
     * dentro: el hueco oscuro. Lleva la cornisa de gola con su banda de colores, el disco solar al centro y el zócalo. Sin
     * degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val madera = t(if (oscuro) mezcla(MADERA, LINO, 0.25f) else MADERA, oscuro)
        val borde = if (oscuro) mezcla(MADERA, LINO, 0.5f) else MADERA_VETA
        val negro = if (oscuro) Color(0xFF080604) else NEGRO
        // El zócalo y la caja.
        drawRect(borde, Offset(f(0.18f), f(0.86f)), Size(f(0.64f), f(0.07f)))
        drawRect(madera, Offset(f(0.26f), f(0.32f)), Size(f(0.48f), f(0.54f)))
        drawRect(negro, Offset(f(0.33f), f(0.4f)), Size(f(0.34f), f(0.46f)))
        drawRect(borde, Offset(f(0.26f), f(0.32f)), Size(f(0.48f), f(0.54f)), style = Stroke(0.9f * u))
        // Las puertas abiertas, giradas hacia fuera.
        listOf(-1f, 1f).forEach { l ->
            val xb = f(0.5f) + l * f(0.17f); val xa = f(0.5f) + l * f(0.38f)
            val puerta = Path().apply { moveTo(xb, f(0.4f)); lineTo(xa, f(0.45f)); lineTo(xa, f(0.81f)); lineTo(xb, f(0.86f)); close() }
            drawPath(puerta, madera)
            drawPath(puerta, borde, style = Stroke(0.9f * u))
            drawLine(borde.copy(alpha = 0.6f), Offset((xa + xb) / 2, f(0.5f)), Offset((xa + xb) / 2, f(0.78f)), 0.6f * u)
        }
        // La cornisa de gola, con la banda de colores.
        drawPath(Path().apply { moveTo(f(0.24f), f(0.32f)); lineTo(f(0.2f), f(0.16f)); lineTo(f(0.8f), f(0.16f)); lineTo(f(0.76f), f(0.32f)); close() }, madera)
        val colores = listOf(LAPIS, ROJO, VERDE)
        for (j in 0 until 6) {
            val c = colores[j % 3].let { if (oscuro) mezcla(it, Color.White, 0.25f) else it }
            drawRect(c, Offset(f(0.25f + j * 0.088f), f(0.2f)), Size(f(0.06f), f(0.07f)))
        }
        drawLine(borde, Offset(f(0.24f), f(0.32f)), Offset(f(0.76f), f(0.32f)), 1.2f * u)
        drawLine(borde, Offset(f(0.19f), f(0.16f)), Offset(f(0.81f), f(0.16f)), 1.2f * u)
        drawCircle(AMARILLO, f(0.045f), Offset(f(0.5f), f(0.1f)))
        drawLine(AMARILLO, Offset(f(0.38f), f(0.1f)), Offset(f(0.44f), f(0.1f)), 1 * u)
        drawLine(AMARILLO, Offset(f(0.56f), f(0.1f)), Offset(f(0.62f), f(0.1f)), 1 * u)
    }

    override val margenAviso: Dp = 24.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO

    /**
     * Aviso: una franja de yeso con, a la izquierda, la banda de rectángulos de colores puesta de pie entre sus dos rayas negras
     * y, junto a ella, a la altura del primer renglón, un signo según [EstadoAviso.semilla]: el anj (la vida), la pluma de Maat
     * (lo justo) o el pilar dyed (lo estable). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) mezcla(MADERA, Color(0xFF110D08), 0.8f) else YESO, Offset.Zero, size)
        val raya = (if (oscuro) LINO else NEGRO).copy(alpha = if (oscuro) 0.45f else 0.6f)
        drawLine(raya, Offset(3 * u, 3 * u), Offset(3 * u, h - 3 * u), 0.7f * u)
        drawLine(raya, Offset(8.6f * u, 3 * u), Offset(8.6f * u, h - 3 * u), 0.7f * u)
        val colores = listOf(LAPIS, ROJO, VERDE)
        val paso = maxOf(6 * u, 1f)
        var y = 4 * u; var k = 0
        while (y + 4 * u < h - 3 * u && k < 200) {
            val c = colores[k % 3].let { if (oscuro) mezcla(it, Color.White, 0.25f) else it }
            drawRect(c, Offset(4 * u, y), Size(3.6f * u, 4.4f * u))
            y += paso; k++
        }
        val signo = if (oscuro) AMARILLO else mezcla(LAPIS, NEGRO, 0.2f)
        val cx = 16 * u; val y0 = 9 * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> {
                drawOval(signo, Offset(cx - 2.2f * u, y0), Size(4.4f * u, 5 * u), style = Stroke(1.2f * u))
                drawLine(signo, Offset(cx - 4 * u, y0 + 5.6f * u), Offset(cx + 4 * u, y0 + 5.6f * u), 1.4f * u)
                drawLine(signo, Offset(cx, y0 + 5 * u), Offset(cx, y0 + 14 * u), 1.6f * u)
            }
            1 -> {
                val pluma = Path().apply { moveTo(cx - 0.4f * u, y0 + 14 * u); cubicTo(cx - 3.4f * u, y0 + 8 * u, cx - 2.4f * u, y0 + 1 * u, cx + 1.8f * u, y0); cubicTo(cx + 2.6f * u, y0 + 5 * u, cx + 1.8f * u, y0 + 10 * u, cx - 0.4f * u, y0 + 14 * u); close() }
                drawPath(pluma, signo)
                drawLine(if (oscuro) mezcla(MADERA, Color(0xFF110D08), 0.8f) else YESO, Offset(cx - 0.3f * u, y0 + 13 * u), Offset(cx + 1.2f * u, y0 + 2 * u), 0.6f * u)
            }
            else -> {
                drawLine(signo, Offset(cx, y0 + 3 * u), Offset(cx, y0 + 14 * u), 2.4f * u)
                for (j in 0 until 4) drawLine(signo, Offset(cx - 3.4f * u, y0 + j * 1.8f * u), Offset(cx + 3.4f * u, y0 + j * 1.8f * u), 1 * u)
                drawLine(signo, Offset(cx - 3 * u, y0 + 14 * u), Offset(cx + 3 * u, y0 + 14 * u), 1.2f * u)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3B341) else Color(0xFF1D2F7A)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, FAYENZA, 0.25f) else mezcla(FAYENZA, NEGRO, 0.55f)

    /**
     * Cifra: un azulejo de fayenza, la loza vidriada turquesa de los templos: la cara clara y lisa donde va el número, el borde de
     * vidriado más hondo con su raya negra, el brillo del vidrio arriba a la izquierda y un punto de oro en cada esquina. Sin
     * degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 12 * u || h < 12 * u) return
        val radio = CornerRadius(3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f), Offset(0.5f * u, 1.5f * u), Size(w - 1 * u, h - 1.5f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(t(FAYENZA, oscuro), r.topLeft, r.size, radio)
        val c = Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u)
        drawRoundRect(if (oscuro) mezcla(FAYENZA, Color(0xFF110D08), 0.8f) else mezcla(FAYENZA, Color.White, 0.8f), c.topLeft, c.size, CornerRadius(1.5f * u))
        drawRoundRect((if (oscuro) Color.Black else NEGRO).copy(alpha = 0.5f), c.topLeft, c.size, CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), Offset(c.left + 3 * u, c.top + 1.6f * u), Offset(c.left + minOf(24 * u, c.width * 0.4f), c.top + 1.6f * u), 1.2f * u, StrokeCap.Round)
        listOf(Offset(r.left + 1.3f * u, r.top + 1.3f * u), Offset(r.right - 1.3f * u, r.top + 1.3f * u), Offset(r.left + 1.3f * u, r.bottom - 1.3f * u), Offset(r.right - 1.3f * u, r.bottom - 1.3f * u))
            .forEach { drawCircle(AMARILLO, 0.9f * u, it) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El oro de los pectorales, con sus celdas de piedras incrustadas. */
    private val ORO = Color(0xFFC99A2E)

    /** El fondo liso de cada tarjeta, donde va el texto: la placa de oro pálido, el yeso del pilono, el de la noche y el lino. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) mezcla(ORO, Color(0xFF110D08), 0.82f) else Color(0xFFF6ECCD)
        TipoTarjeta.PENDIENTE -> if (oscuro) hondo(mezcla(YESO, ROJO, 0.12f), true) else YESO
        TipoTarjeta.NOCHE -> if (oscuro) mezcla(LAPIS, Color(0xFF110D08), 0.84f) else mezcla(YESO, LINO, 0.35f)
        TipoTarjeta.VIDA -> if (oscuro) hondo(LINO, true) else mezcla(LINO, Color.White, 0.4f)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E4C0) else NEGRO
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) mezcla(LINO, MADERA, 0.22f) else mezcla(NEGRO, MADERA, 0.45f)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA, TipoTarjeta.NOCHE -> if (oscuro) LAPIS_NOCHE else LAPIS
        TipoTarjeta.PENDIENTE -> if (oscuro) AMARILLO else mezcla(ROJO, NEGRO, 0.25f)
        TipoTarjeta.VIDA -> if (oscuro) mezcla(ROJO, YESO, 0.5f) else mezcla(ROJO, NEGRO, 0.25f)
    }

    /**
     * Tarjetas: cuatro piezas de Egipto. La cifra de Hoy es una placa de pectoral: el marco de oro y, en la banda de arriba, la
     * fila de celdas con sus piedras incrustadas (lapislázuli, cornalina, turquesa y feldespato verde); la cara del medio, de oro
     * pálido, lisa. «Te falta hoy» es el frente de un pilono: la cornisa de gola arriba, con sus hojas de colores, el baquetón
     * atado debajo y a los lados, y el disco solar alado al centro de la cornisa. La noche es el cielo de Nut de los techos de
     * las tumbas: una franja de lapislázuli arriba con sus estrellas amarillas de cinco puntas. La tarjeta de Vida es un paño de
     * lino, con las rayas azules del orillo arriba y el fleco abajo. Al tocarlas se oscurecen apenas y la sombra se acorta.
     * Destacadas: las piedras del pectoral toman todo su color y aparece el disco del sol; el pilono completo lleva las alas de
     * oro; la noche sellada, la luna (el disco en el creciente) al final de la franja. Sin degradados.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> pectoral(e, oscuro)
            TipoTarjeta.PENDIENTE -> pilono(e, oscuro)
            TipoTarjeta.NOCHE -> cieloNut(e, oscuro)
            TipoTarjeta.VIDA -> panoLino(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, MADERA, 0.12f)
    }

    /** El oro con la luz del momento. */
    private fun oro(oscuro: Boolean): Color = if (oscuro) mezcla(ORO, Color(0xFF110D08), 0.3f) else ORO

    private fun DrawScope.pectoral(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(0.5f * u, (if (e.presionado) 0.8f else 1.8f) * u), Size(w - 1 * u, h - 1.8f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.8f * u)
        drawRoundRect(oro(oscuro), r.topLeft, r.size, radio)
        val luz = Color.White.copy(alpha = if (oscuro) 0.12f else 0.45f); val sombra = Color.Black.copy(alpha = 0.3f)
        if (e.presionado) bisel(r, sombra, luz, 0.9f * u) else bisel(r, luz, sombra, 0.9f * u)
        // La banda de celdas, arriba, dentro del marco.
        val bandaAlto = minOf(5 * u, h * 0.07f)
        val banda = Rect(r.left + 2.5f * u, r.top + 2.2f * u, r.right - 2.5f * u, r.top + 2.2f * u + bandaAlto)
        val paso = maxOf(4.2f * u, 1f)
        val n = (banda.width / paso).toInt().coerceIn(1, 120)
        val ancho = banda.width / n
        val piedras = listOf(LAPIS, ROJO, FAYENZA, VERDE)
        drawRect(Color.Black.copy(alpha = 0.35f), banda.topLeft, banda.size)
        for (j in 0 until n) {
            val c = piedras[j % 4]
            val p = if (e.destacado) (if (oscuro) mezcla(c, Color.White, 0.15f) else c) else mezcla(c, oro(oscuro), 0.4f)
            drawRect(p, Offset(banda.left + j * ancho + 0.5f * u, banda.top + 0.5f * u), Size(ancho - 1 * u, banda.height - 1 * u))
        }
        // La cara de oro pálido, lisa.
        val c = Rect(r.left + 2.5f * u, banda.bottom + 1.6f * u, r.right - 2.5f * u, r.bottom - 2.5f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), c.topLeft, c.size)
        bisel(c, sombra, luz.copy(alpha = luz.alpha * 0.6f), 0.7f * u)
        if (e.destacado) {
            drawRect(if (oscuro) LAPIS_NOCHE else LAPIS, c.deflate(1.2f * u).topLeft, c.deflate(1.2f * u).size, style = Stroke(1.1f * u))
            // El disco del sol sobre la banda, al centro.
            val sol = Offset(w / 2, banda.center.y)
            drawCircle(oro(oscuro), bandaAlto * 0.85f, sol)
            drawCircle(if (oscuro) mezcla(ROJO, AMARILLO, 0.2f) else ROJO, bandaAlto * 0.62f, sol)
        }
    }

    private fun DrawScope.pilono(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val completo = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u))
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        // La cornisa de gola: las hojas de colores, de pie, con la punta redonda.
        val g = minOf(10 * u, h * 0.16f)
        val paso = maxOf(4.4f * u, 1f)
        val n = (w / paso).toInt().coerceIn(1, 160)
        val ancho = w / n
        drawRect(t(NEGRO, oscuro), Offset.Zero, Size(w, g))
        val hojas = listOf(LAPIS, ROJO, VERDE, ROJO)
        for (j in 0 until n) {
            val c = t(hojas[j % 4], oscuro)
            drawRoundRect(c, Offset(j * ancho + 0.6f * u, 0.8f * u), Size(ancho - 1.2f * u, g - 1.2f * u), CornerRadius((ancho - 1.2f * u) / 2))
        }
        // El baquetón atado: debajo de la cornisa y a los lados, con su cinta en espiral.
        val b = 2.2f * u
        val baqueton = t(AMARILLO, oscuro)
        val cinta = t(NEGRO, oscuro).copy(alpha = 0.7f)
        drawRect(baqueton, Offset(0f, g), Size(w, b))
        drawRect(baqueton, Offset(0f, g), Size(b, r.bottom - g))
        drawRect(baqueton, Offset(w - b, g), Size(b, r.bottom - g))
        val pasoCinta = maxOf(3 * u, 1f)
        val nc = (w / pasoCinta).toInt().coerceIn(1, 240)
        for (j in 0 until nc) drawLine(cinta, Offset(j * pasoCinta, g + b), Offset(j * pasoCinta + b, g), 0.6f * u)
        val nv = ((r.bottom - g - b) / pasoCinta).toInt().coerceIn(0, 120)
        for (j in 0 until nv) {
            val y = g + b + j * pasoCinta
            drawLine(cinta, Offset(0f, y + b), Offset(b, y), 0.6f * u)
            drawLine(cinta, Offset(w - b, y + b), Offset(w, y), 0.6f * u)
        }
        // El disco solar alado, al centro de la cornisa.
        val sol = Offset(w / 2, g * 0.55f)
        val rs = g * 0.46f
        val largo = minOf(w * 0.14f, 44 * u)
        val ala = if (completo) oro(oscuro) else t(LAPIS, oscuro)
        val pluma = if (completo) mezcla(ORO, NEGRO, 0.45f) else t(YESO, oscuro)
        for (lado in listOf(-1f, 1f)) {
            val p = Path().apply {
                moveTo(sol.x + lado * rs * 0.8f, sol.y - rs * 0.55f)
                lineTo(sol.x + lado * (rs + largo), sol.y - rs * 0.95f)
                quadraticTo(sol.x + lado * (rs + largo * 0.9f), sol.y + rs * 0.2f, sol.x + lado * (rs + largo * 0.55f), sol.y + rs * 0.55f)
                lineTo(sol.x + lado * rs * 0.8f, sol.y + rs * 0.5f)
                close()
            }
            drawPath(p, ala)
            for (k in 1..3) {
                val x = sol.x + lado * (rs + largo * k / 4f)
                drawLine(pluma.copy(alpha = 0.6f), Offset(x, sol.y - rs * 0.7f), Offset(x - lado * 1.2f * u, sol.y + rs * 0.45f), 0.5f * u)
            }
            drawPath(p, t(NEGRO, oscuro), style = Stroke(0.6f * u))
        }
        drawCircle(oro(oscuro), rs * 1.05f, sol)
        drawCircle(if (completo) mezcla(ROJO, AMARILLO, 0.25f) else ROJO, rs * 0.8f, sol)
    }

    private fun DrawScope.cieloNut(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        val radio = CornerRadius(3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u), radio)
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        // La franja del cielo: lapislázuli con sus estrellas amarillas, entre dos rayas.
        val alto = minOf(8 * u, h * 0.1f)
        val cielo = if (oscuro) mezcla(LAPIS, Color(0xFF110D08), 0.35f) else LAPIS
        clipPath(Path().apply { addRoundRect(RoundRect(r, radio)) }) { drawRect(cielo, Offset.Zero, Size(w, alto)) }
        drawLine(t(if (sellado) AMARILLO else ROJO, oscuro), Offset(0f, alto + 0.6f * u), Offset(w, alto + 0.6f * u), 1.2f * u)
        drawLine(t(NEGRO, oscuro), Offset(0f, alto + 1.8f * u), Offset(w, alto + 1.8f * u), 0.6f * u)
        val amarilla = if (oscuro) mezcla(AMARILLO, Color(0xFF110D08), 0.15f) else AMARILLO
        val paso = maxOf(14 * u, 1f)
        val fin = if (sellado) w - 22 * u else w
        val n = (fin / paso).toInt().coerceIn(1, 60)
        for (j in 0 until n) {
            val c = Offset(paso * (j + 0.5f), alto / 2 + (if (j % 2 == 0) -0.6f else 0.6f) * u)
            drawPath(estrella(c, alto * 0.36f, 5, 0.42f), amarilla)
        }
        // Abajo, la raya negra que cierra la pintura.
        drawLine(t(NEGRO, oscuro).copy(alpha = 0.6f), Offset(4 * u, r.bottom - 3 * u), Offset(w - 4 * u, r.bottom - 3 * u), 0.8f * u)
        if (sellado) {
            // La luna: el disco dentro del creciente, de oro, al final de la franja.
            val c = Offset(w - 13 * u, alto * 0.62f + 2 * u)
            val rl = 6.5f * u
            val disco = Path().apply { addOval(Rect(c.x - rl, c.y - rl, c.x + rl, c.y + rl)) }
            val hueco = Path().apply { addOval(Rect(c.x - rl * 0.95f, c.y - rl * 1.35f, c.x + rl * 0.95f, c.y + rl * 0.55f)) }
            drawPath(Path().apply { op(disco, hueco, PathOperation.Difference) }, oro(oscuro))
            drawCircle(oro(oscuro), rl * 0.55f, Offset(c.x, c.y - rl * 0.35f))
            drawCircle(Color.White.copy(alpha = 0.3f), rl * 0.18f, Offset(c.x - rl * 0.18f, c.y - rl * 0.5f))
        }
    }

    private fun DrawScope.panoLino(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 13
        val fleco = 5 * u
        val cuerpo = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - fleco)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.14f), cuerpo.topLeft + Offset(0f, (if (e.presionado) 0.6f else 1.4f) * u), cuerpo.size)
        val lino = fondoTocado(e.tipo, e.presionado, oscuro)
        drawRect(lino, cuerpo.topLeft, cuerpo.size)
        // La trama, apenas, solo junto a los bordes.
        val hilo = (if (oscuro) LINO else MADERA).copy(alpha = if (oscuro) 0.1f else 0.16f)
        for (j in 0 until 4) {
            drawLine(hilo, Offset(cuerpo.left + (1.2f + j * 1.1f) * u, cuerpo.top), Offset(cuerpo.left + (1.2f + j * 1.1f) * u, cuerpo.bottom), 0.4f * u)
            drawLine(hilo, Offset(cuerpo.right - (1.2f + j * 1.1f) * u, cuerpo.top), Offset(cuerpo.right - (1.2f + j * 1.1f) * u, cuerpo.bottom), 0.4f * u)
        }
        // Las rayas azules del orillo, arriba.
        val azul = if (oscuro) mezcla(LAPIS_NOCHE, Color(0xFF110D08), 0.35f) else LAPIS
        drawRect(azul, Offset(cuerpo.left, cuerpo.top + 2.2f * u), Size(cuerpo.width, 1.6f * u))
        drawRect(azul.copy(alpha = 0.7f), Offset(cuerpo.left, cuerpo.top + 5 * u), Size(cuerpo.width, 0.7f * u))
        // El dobladillo y el fleco, abajo.
        val hebra = if (oscuro) mezcla(LINO, Color(0xFF110D08), 0.55f) else mezcla(LINO, MADERA, 0.35f)
        drawLine(hebra, Offset(cuerpo.left, cuerpo.bottom - 1.6f * u), Offset(cuerpo.right, cuerpo.bottom - 1.6f * u), 0.6f * u)
        val paso = maxOf(2.2f * u, 1f)
        val n = (cuerpo.width / paso).toInt().coerceIn(1, 400)
        for (j in 0 until n) {
            val x = cuerpo.left + (j + 0.5f) * paso
            val largo = fleco * (0.6f + 0.35f * azar(s, j))
            drawLine(hebra, Offset(x, cuerpo.bottom), Offset(x + (azar(s, j + 500) - 0.5f) * 1.2f * u, cuerpo.bottom + largo), 0.7f * u, StrokeCap.Round)
        }
        if (e.destacado) drawRect(azul, cuerpo.deflate(1 * u).topLeft, cuerpo.deflate(1 * u).size, style = Stroke(1 * u))
    }

    // ---------------------------------------------------------------- Gastos

    /**
     * La banda de rectángulos de colores de las tumbas, de [x0] a [x1], de [alto] con el borde de arriba en [y]: lapislázuli,
     * rojo y verde, con su hueco de yeso, entre dos rayas negras. Con [fuerza] menor que 1, desvaída. A lo más 120 rectángulos.
     */
    private fun DrawScope.bandaColores(x0: Float, x1: Float, y: Float, alto: Float, oscuro: Boolean, fuerza: Float) {
        val u = density
        if (x1 - x0 < 4 * u) return
        val paso = maxOf(alto * 1.5f, 1f)
        val n = ((x1 - x0) / paso).toInt().coerceIn(1, 120)
        val ancho = (x1 - x0) / n
        val colores = if (oscuro) listOf(LAPIS_NOCHE, mezcla(ROJO, LINO, 0.25f), VERDE_NOCHE) else listOf(LAPIS, ROJO, VERDE)
        for (j in 0 until n) drawRect(colores[j % 3].copy(alpha = fuerza), Offset(x0 + j * ancho + 0.6f * u, y + 1.2f * u), Size(ancho - 1.2f * u, alto - 2.4f * u))
        val negro = (if (oscuro) LINO else NEGRO).copy(alpha = (if (oscuro) 0.5f else 0.85f) * fuerza)
        drawLine(negro, Offset(x0, y), Offset(x1, y), 0.8f * u)
        drawLine(negro, Offset(x0, y + alto), Offset(x1, y + alto), 0.8f * u)
    }

    /** Un cartucho: el óvalo alargado donde se escriben los nombres (la raya del nudo se dibuja aparte). */
    private fun cartucho(r: Rect): Path = Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.height / 2))) }

    override val altoBalanza: Dp = 60.dp

    /**
     * Balanza: la tablilla del escriba a todo el ancho, de yeso blanco en su marco de madera, con la banda de colores arriba y
     * dos cartuchos: el del gasto a la izquierda y el del ingreso a la derecha, cada uno con la raya de su nudo hacia afuera. El
     * elegido se rellena de papiro, se marca con un contorno grueso de lapislázuli y la banda de encima toma todo su color; el
     * otro queda en yeso, con el contorno fino y la banda desvaída. Sin degradados.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 30 * u) return
        val marco = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(t(MADERA, oscuro), marco.topLeft, marco.size, CornerRadius(2 * u))
        vetas(marco, (if (oscuro) Color.Black else MADERA_VETA).copy(alpha = 0.35f), 44, grosor = 0.8f)
        val yeso = marco.deflate(3.5f * u)
        val fondoYeso = hondo(YESO, oscuro)
        drawRect(fondoYeso, yeso.topLeft, yeso.size)
        val altoBanda = 7 * u
        val xm = w / 2
        val lapis = if (oscuro) LAPIS_NOCHE else LAPIS
        for (lado in 0..1) {
            val elegido = (lado == 1) == e.ingreso
            val bx0 = if (lado == 0) yeso.left else xm
            val bx1 = if (lado == 0) xm else yeso.right
            bandaColores(bx0, bx1, yeso.top, altoBanda, oscuro, if (elegido) 1f else 0.3f)
            val r = Rect(bx0 + 8 * u, yeso.top + altoBanda + 4 * u, bx1 - 8 * u, yeso.bottom - 4 * u)
            val fondo = when {
                elegido && oscuro -> mezcla(PAPIRO, Color(0xFF110D08), 0.7f)
                elegido -> PAPIRO
                else -> fondoYeso
            }
            val c = cartucho(r)
            drawPath(c, fondo)
            drawPath(c, if (elegido) lapis else (if (oscuro) LINO else NEGRO).copy(alpha = 0.35f), style = Stroke(if (elegido) 2.2f * u else 1 * u))
            // La raya del nudo, hacia afuera.
            val xn = if (lado == 0) r.left - 3.5f * u else r.right + 3.5f * u
            drawLine(if (elegido) lapis else (if (oscuro) LINO else NEGRO).copy(alpha = 0.35f), Offset(xn, r.top + 2 * u), Offset(xn, r.bottom - 2 * u), if (elegido) 2.2f * u else 1 * u)
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val caja = Rect(r.left + r.height * 0.5f, r.top + 2 * u, r.right - r.height * 0.5f, r.bottom - 2 * u)
            if (elegido) textoEn(medidor, texto, caja, Letra.messiri, legibleAnt(base, 5f, fondo), 18f, peso = FontWeight.Bold)
            else textoEn(medidor, texto, caja, Letra.messiri, legibleAnt(mezcla(base, fondo, 0.2f), 4.7f, fondo), 16f)
        }
        drawLine((if (oscuro) LINO else NEGRO).copy(alpha = 0.5f), Offset(xm, yeso.top), Offset(xm, yeso.bottom), 0.8f * u)
    }

    override val altoMes: Dp = 54.dp

    /**
     * Encabezado del mes: un dintel de templo a todo el ancho: arriba la gola, la cornisa curva con sus hojas de palma pintadas
     * (lapislázuli, verde y rojo), debajo el baquetón atado y, en la cara de yeso, el mes. En los extremos de la cara, los
     * capullos de loto de las filas, grandes, para ir y volver. Sin degradados.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 30 * u) return
        val gola = Rect(1 * u, 1 * u, w - 1 * u, 1 * u + h * 0.26f)
        val cara = Rect(4 * u, gola.bottom + 4 * u, w - 4 * u, h - 1 * u)
        // La gola: hojas de palma verticales, un poco más anchas arriba.
        drawRect(if (oscuro) mezcla(YESO, Color(0xFF110D08), 0.72f) else YESO, gola.topLeft, gola.size)
        val paso = maxOf(9 * u, 1f)
        val n = (gola.width / paso).toInt().coerceIn(1, 120)
        val hoja = gola.width / n
        val colores = if (oscuro) listOf(LAPIS_NOCHE, VERDE_NOCHE, mezcla(ROJO, LINO, 0.25f)) else listOf(LAPIS, VERDE, ROJO)
        for (j in 0 until n) {
            val x = gola.left + j * hoja
            val p = Path().apply {
                moveTo(x + 1 * u, gola.top + 1 * u); lineTo(x + hoja - 1 * u, gola.top + 1 * u)
                lineTo(x + hoja - 2 * u, gola.bottom - 1 * u); lineTo(x + 2 * u, gola.bottom - 1 * u); close()
            }
            drawPath(p, colores[j % 3].copy(alpha = if (oscuro) 0.8f else 0.9f))
        }
        // El baquetón, atado en diagonal.
        val toro = Rect(2 * u, gola.bottom, w - 2 * u, gola.bottom + 4 * u)
        drawRect(if (oscuro) mezcla(AMARILLO, Color(0xFF110D08), 0.5f) else AMARILLO, toro.topLeft, toro.size)
        val ataduras = (toro.width / maxOf(6 * u, 1f)).toInt().coerceIn(1, 200)
        for (j in 0 until ataduras) {
            val x = toro.left + j * toro.width / ataduras
            drawLine(NEGRO.copy(alpha = 0.6f), Offset(x, toro.bottom), Offset(x + 3 * u, toro.top), 0.8f * u)
        }
        drawRect(NEGRO.copy(alpha = 0.7f), toro.topLeft, toro.size, style = Stroke(0.7f * u))
        val fondo = hondo(YESO, oscuro)
        drawRect(fondo, cara.topLeft, cara.size)
        drawRect((if (oscuro) LINO else NEGRO).copy(alpha = 0.45f), cara.topLeft, cara.size, style = Stroke(0.8f * u))
        textoEn(medidor, e.titulo, Rect(56 * u, cara.top + 1 * u, w - 56 * u, cara.bottom - 1 * u), Letra.messiri,
            legibleAnt(if (oscuro) LINO else NEGRO, 7f, fondo), 22f, peso = FontWeight.Bold, espaciado = 0.5f)
        inset(0f, cara.top - 1 * u, 0f, h - cara.bottom - 1 * u) { flechasMesAnt(48 * u) { flechaFila(oscuro) } }
    }

    /**
     * Asiento: abajo, la raya negra fina de las pinturas (de noche, de lino), tenue, y al principio un rectángulo de la banda del
     * color del tipo: rojo si es un gasto, verde si es un ingreso, con su hueco y otro de lapislázuli. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.4f * u
        drawLine((if (oscuro) LINO else NEGRO).copy(alpha = if (oscuro) 0.2f else 0.26f), Offset(18 * u, y), Offset(w - 2 * u, y), 0.8f * u)
        val c = if (e.ingreso) (if (oscuro) VERDE_NOCHE else VERDE) else (if (oscuro) mezcla(ROJO, LINO, 0.25f) else ROJO)
        drawRect(c.copy(alpha = 0.85f), Offset(2 * u, y - 3.5f * u), Size(7 * u, 3.5f * u))
        drawRect((if (oscuro) LAPIS_NOCHE else LAPIS).copy(alpha = 0.6f), Offset(10.5f * u, y - 3.5f * u), Size(4 * u, 3.5f * u))
    }

    override val altoRotuloDia: Dp = 24.dp

    /**
     * Rótulo del día: la fecha dentro de un cartucho de papiro con contorno de lapislázuli y la raya de su nudo, y de ahí hacia la
     * derecha la banda de colores, fina y desvaída. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 10 * u) return
        val tam = 13f
        val ancho = minOf(anchoTextoAnt(medidor, texto, Letra.messiri, tam, FontWeight.Bold, 1.2f) + 2 * u, w * 0.6f)
        val r = Rect(1 * u, 1.5f * u, ancho + 1 * u + (h - 3 * u), h - 1.5f * u)
        val fondo = papiro(oscuro)
        val lapis = if (oscuro) LAPIS_NOCHE else LAPIS
        drawPath(cartucho(r), fondo)
        drawPath(cartucho(r), lapis, style = Stroke(1.3f * u))
        drawLine(lapis, Offset(r.right + 2.5f * u, r.top + 1.5f * u), Offset(r.right + 2.5f * u, r.bottom - 1.5f * u), 1.3f * u)
        textoEn(medidor, texto, Rect(r.left + r.height / 2, r.top, r.left + r.height / 2 + ancho, r.bottom), Letra.messiri,
            legibleAnt(if (oscuro) LINO else NEGRO, 7f, fondo), tam, peso = FontWeight.Bold, espaciado = 1.2f)
        val alto = minOf(6 * u, h * 0.3f)
        bandaColores(r.right + 9 * u, w - 2 * u, h / 2 - alto / 2, alto, oscuro, 0.5f)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * El disco solar alado con centro en [c]: el disco de [r], las dos alas de [ala] de largo con sus plumas marcadas y las dos
     * cobras que cuelgan a los lados del disco.
     */
    private fun DrawScope.discoAlado(c: Offset, r: Float, ala: Float, alas: Color, disco: Color, plumas: Color) {
        val u = ala / 32f
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(c.x + s * r * 1.1f, c.y - 1.5f * u); quadraticTo(c.x + s * 16 * u, c.y - 6 * u, c.x + s * ala, c.y - 3 * u)
                lineTo(c.x + s * (ala - 3 * u), c.y + 1.2f * u); quadraticTo(c.x + s * 16 * u, c.y + 1 * u, c.x + s * r * 1.1f, c.y + 2.5f * u); close()
            }, alas)
            for (j in 0 until 4) {
                val x = c.x + s * (r * 1.1f + (5 + j * 5.5f) * u)
                drawLine(plumas, Offset(x, c.y - (3.4f - 0.2f * j) * u), Offset(x + s * 1.4f * u, c.y + 1.6f * u), 0.6f * u)
            }
            drawPath(Path().apply {
                moveTo(c.x + s * r * 0.8f, c.y + r * 0.5f); quadraticTo(c.x + s * r * 1.35f, c.y + r * 0.9f, c.x + s * r * 0.95f, c.y + r * 1.45f)
            }, alas, style = Stroke(0.8f * u, cap = StrokeCap.Round))
            drawCircle(alas, 0.9f * u, Offset(c.x + s * r * 0.8f, c.y + r * 0.5f))
        }
        drawCircle(disco, r, c)
    }

    /** Una flor de loto que nace en [b] y se abre hacia arriba, de [alto]: tres pétalos y los dos sépalos abiertos. */
    private fun loto(b: Offset, alto: Float): Path = Path().apply {
        val a = alto
        moveTo(b.x, b.y); quadraticTo(b.x - a * 0.28f, b.y - a * 0.55f, b.x, b.y - a); quadraticTo(b.x + a * 0.28f, b.y - a * 0.55f, b.x, b.y); close()
        listOf(-1f, 1f).forEach { s ->
            moveTo(b.x, b.y); quadraticTo(b.x + s * a * 0.2f, b.y - a * 0.7f, b.x + s * a * 0.55f, b.y - a * 0.78f)
            quadraticTo(b.x + s * a * 0.42f, b.y - a * 0.3f, b.x, b.y); close()
        }
    }

    /**
     * Hoja rápida: la estela pintada. Bajo el arco de la hoja, una moldura que lo sigue y, en lo más alto, el disco solar alado con
     * sus dos cobras; por los dos cantos, la banda de rectángulos de colores (lapislázuli, rojo y verde) entre sus rayas negras, que
     * nace abajo de una flor de loto; abajo, la misma banda de lado a lado. Sin degradados.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        // La moldura bajo el arco.
        drawPath(Path().apply { moveTo(0f, 20.5f * u); quadraticTo(w / 2, -10.6f * u, w, 20.5f * u) }, alfaAnt(e.primario, 0.45f), style = Stroke(1f * u))
        drawPath(Path().apply { moveTo(0f, 23.5f * u); quadraticTo(w / 2, -7.6f * u, w, 23.5f * u) }, alfaAnt(e.primario, 0.22f), style = Stroke(0.7f * u))
        discoAlado(Offset(w / 2, 13 * u), 3.6f * u, 32 * u, alfaAnt(e.primario, 0.72f), e.secundario, e.fondo.copy(alpha = 0.55f))
        // Las bandas de los cantos.
        val colores = if (oscuro) listOf(LAPIS_NOCHE, mezcla(ROJO, LINO, 0.25f), VERDE_NOCHE) else listOf(LAPIS, ROJO, VERDE)
        val negro = (if (oscuro) LINO else NEGRO).copy(alpha = if (oscuro) 0.35f else 0.5f)
        val y0 = 30 * u; val y1 = h - 26 * u
        if (y1 > y0) {
            val paso = maxOf(6 * u, 1f)
            val n = ((y1 - y0) / paso).toInt().coerceIn(1, 160)
            listOf(4.5f * u, w - 10.5f * u).forEach { x ->
                for (j in 0 until n) drawRect(alfaAnt(colores[j % 3], 0.45f), Offset(x + 0.7f * u, y0 + j * paso + 0.7f * u), Size(4.6f * u, paso - 1.4f * u))
                drawLine(negro, Offset(x, y0), Offset(x, y0 + n * paso), 0.8f * u)
                drawLine(negro, Offset(x + 6 * u, y0), Offset(x + 6 * u, y0 + n * paso), 0.8f * u)
                drawPath(loto(Offset(x + 3 * u, y0 + n * paso + 10 * u), 9 * u), alfaAnt(e.primario, 0.55f))
            }
        }
        bandaColores(18 * u, w - 18 * u, h - 11 * u, 5 * u, oscuro, 0.5f)
    }

    /**
     * Relación: arriba, bajo el remate redondo, un disco alado chico; abajo, la cenefa de rectángulos de colores entre sus rayas
     * negras, como el borde de una pintura de tumba; a los lados, una raya fina. El centro, liso. Sin degradados.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        discoAlado(Offset(w / 2, 6.5f * u), 2.4f * u, 16 * u, alfaAnt(e.primario, 0.55f), alfaAnt(e.secundario, 0.8f), e.fondo.copy(alpha = 0.5f))
        bandaColores(5 * u, w - 5 * u, h - 8 * u, 4.5f * u, oscuro, 0.45f)
        listOf(3.5f * u, w - 3.5f * u).forEach { x -> drawLine(alfaAnt(e.primario, 0.3f), Offset(x, 14 * u), Offset(x, h - 10 * u), 0.7f * u) }
    }

    /**
     * Pista: un pilono, los dos muros en talud y la gola que se abre arriba, con el disco solar alado sobre la puerta. Sin
     * degradados.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f
        val x0 = (size.width - l) / 2; val y0 = (size.height - l) / 2
        fun px(x: Float) = x0 + x * l
        fun py(y: Float) = y0 + y * l
        drawPath(Path().apply {
            moveTo(px(0.1f), py(0.96f)); lineTo(px(0.18f), py(0.2f)); quadraticTo(px(0.15f), py(0.07f), px(0.04f), py(0.04f))
            lineTo(px(0.96f), py(0.04f)); quadraticTo(px(0.85f), py(0.07f), px(0.82f), py(0.2f)); lineTo(px(0.9f), py(0.96f)); close()
        }, e.primario, style = Stroke(g * 0.8f))
        drawLine(e.primario, Offset(px(0.17f), py(0.2f)), Offset(px(0.83f), py(0.2f)), g * 0.5f)
        drawLine(e.primario, Offset(px(0.3f), py(0.12f)), Offset(px(0.7f), py(0.12f)), g * 0.7f, StrokeCap.Round)
        drawCircle(e.secundario, l * 0.045f, Offset(px(0.5f), py(0.12f)))
    }
}

/**
 * Mesoamérica: el campo es un tablero de estuco bruñido, liso, con su marco pintado de cinabrio; en cada esquina, el escalón
 * de la greca, y a media altura de cada lado una cuenta de jade incrustada. Con el foco el borde del estuco se pinta de jade y
 * las cuentas brillan; con un error, de grana (la cochinilla), que tira a morado y no se confunde con el cinabrio. Con varios
 * renglones, líneas de guía tenues. Sin degradados.
 */
object ComunMeso : DisenoComun {
    private val ESTUCO = Color(0xFFF2E8D2)
    private val CALIZA_OSC = Color(0xFFA8997C)
    private val CINABRIO = Color(0xFFA3301E)
    private val JADE = Color(0xFF2F7D57)
    /** El jade hondo, el del rótulo, para que se lea sobre el estuco. */
    private val JADE_HONDO = Color(0xFF1C5A3C)
    private val NEGRO = Color(0xFF2A1810)
    private val AZUL_MAYA = Color(0xFF5E9EAE)
    /** El rojo de la grana cochinilla: el del error. */
    private val GRANA = Color(0xFFA8144C)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) ESTUCO else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(ESTUCO, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(JADE, Color.White, 0.42f) else JADE_HONDO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF58AB4) else GRANA

    /** El escalón de la greca en la esquina [o], de lado [k], hacia [sx], [sy] (±1). */
    private fun escalon(o: Offset, k: Float, sx: Float, sy: Float): Path {
        val pts = listOf(0f to 0f, 1f to 0f, 1f to 0.34f, 0.67f to 0.34f, 0.67f to 0.67f, 0.34f to 0.67f, 0.34f to 1f, 0f to 1f)
        return Path().apply {
            pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(o.x + sx * x * k, o.y + sy * y * k) else lineTo(o.x + sx * x * k, o.y + sy * y * k) }
            close()
        }
    }

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val estuco = hondo(ESTUCO, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, estuco, 0.5f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, CornerRadius(4 * u))
        // El marco de cinabrio y el estuco dentro.
        val rojo = a(t(CINABRIO, oscuro))
        drawRoundRect(rojo, r.topLeft, r.size, CornerRadius(4 * u))
        val b = 3.4f * u
        val c = Rect(r.left + b, r.top + b, r.right - b, r.bottom - b)
        drawRoundRect(estuco, c.topLeft, c.size, CornerRadius(1.5f * u))
        // El grano del estuco, muy fino, y el brillo del bruñido arriba.
        val granos = (c.width * c.height / (90 * u * u)).toInt().coerceIn(10, 200)
        for (j in 0 until granos) drawCircle(hondo(CALIZA_OSC, oscuro).copy(alpha = 0.16f), (0.3f + 0.5f * azar(s, j)) * u,
            Offset(c.left + c.width * azar(s, j + 300), c.top + c.height * azar(s, j + 600)))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.4f), Offset(c.left + 6 * u, c.top + 1.6f * u), Offset(c.right - 6 * u, c.top + 1.6f * u), 1.2f * u)
        if (e.multilinea) renglonesAntiguedad(c.left + 10 * u, c.right - 10 * u, margenCampoY.toPx(), c.bottom - 6 * u,
            hondo(CALIZA_OSC, oscuro).copy(alpha = if (oscuro) 0.5f else 0.32f), 0.7f * u)
        // La raya negra que separa el rojo del estuco.
        drawRoundRect(a(t(NEGRO, oscuro)).copy(alpha = 0.55f), c.topLeft, c.size, CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        // El borde del estuco: jade con el foco, grana con el error.
        val encendido = e.enfocado && e.habilitado
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) mezcla(JADE, Color.White, 0.35f) else JADE
            else -> null
        }
        if (linea != null) drawRoundRect(linea, Offset(c.left + 0.9f * u, c.top + 0.9f * u), Size(c.width - 1.8f * u, c.height - 1.8f * u),
            CornerRadius(1.5f * u), style = Stroke(1.8f * u))
        // Los escalones de las esquinas, encima del borde.
        val k = minOf(7.5f * u, c.height / 3)
        escalon(Offset(c.left, c.top), k, 1f, 1f).let { drawPath(it, rojo) }
        escalon(Offset(c.right, c.top), k, -1f, 1f).let { drawPath(it, rojo) }
        escalon(Offset(c.left, c.bottom), k, 1f, -1f).let { drawPath(it, rojo) }
        escalon(Offset(c.right, c.bottom), k, -1f, -1f).let { drawPath(it, rojo) }
        // Las cuentas de jade incrustadas a media altura de cada lado.
        val jade = a(if (oscuro) mezcla(JADE, Color.Black, 0.25f) else JADE)
        val cy = minOf(h / 2, c.top + 18 * u)
        listOf(r.left + b / 2 + 0.3f * u, r.right - b / 2 - 0.3f * u).forEach { x ->
            val o = Offset(x, cy)
            drawCircle(a(t(NEGRO, oscuro)).copy(alpha = 0.6f), 2.5f * u, o)
            drawCircle(if (encendido) mezcla(jade, AZUL_MAYA, 0.35f) else jade, 2.1f * u, o)
            drawCircle(Color.White.copy(alpha = if (encendido) 0.85f else 0.35f), (if (encendido) 1f else 0.7f) * u, Offset(o.x - 0.7f * u, o.y - 0.7f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * Separador: una raya de cinabrio y, al centro, una pirámide escalonada doble (tres escalones arriba y tres abajo, como
     * las de las grecas) con una cuenta de jade incrustada en medio. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val rojo = if (oscuro) mezcla(CINABRIO, ESTUCO, 0.3f) else CINABRIO
        val raya = rojo.copy(alpha = (if (oscuro) 0.45f else 0.5f) * f)
        val a = 8 * u; val b = 4.5f * u
        val cx = w / 2
        val medios = listOf(-1f to -1f / 3, -2f / 3 to -1f / 3, -2f / 3 to -2f / 3, -1f / 3 to -2f / 3, -1f / 3 to -1f,
            1f / 3 to -1f, 1f / 3 to -2f / 3, 2f / 3 to -2f / 3, 2f / 3 to -1f / 3, 1f to -1f / 3)
        val piramide = Path().apply {
            medios.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(cx + x * a, cy + y * b) else lineTo(cx + x * a, cy + y * b) }
            medios.reversed().forEach { (x, y) -> lineTo(cx + x * a, cy - y * b) }
            close()
        }
        drawPath(piramide, rojo.copy(alpha = (if (oscuro) 0.7f else 0.75f) * f))
        val jade = if (oscuro) mezcla(JADE, Color.White, 0.2f) else JADE
        drawCircle(jade.copy(alpha = f), 1.5f * u, Offset(cx, cy))
        drawCircle(Color.White.copy(alpha = 0.4f * f), 0.5f * u, Offset(cx - 0.5f * u, cy - 0.5f * u))
        val hueco = a + 4 * u
        if (w / 2 - hueco > 4 * u) {
            drawLine(raya, Offset(2 * u, cy), Offset(cx - hueco, cy), 1 * u)
            drawLine(raya, Offset(cx + hueco, cy), Offset(w - 2 * u, cy), 1 * u)
        }
    }

    /** El texto de los botones: el cinabrio del tema, un poco más hondo para que se lea sobre el estuco; de noche, el azul maya. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF63C5DD) else mezcla(CINABRIO, NEGRO, 0.14f)

    /**
     * Botón de texto: una raya de cinabrio bajo la palabra que en cada punta sube en dos escalones hacia afuera, como el
     * arranque de una greca escalonada. Al tocarlo la raya se vuelve de jade y el estuco de detrás toma un verde muy tenue;
     * apagado, apenas se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val jade = if (oscuro) mezcla(JADE, Color.White, 0.35f) else JADE
        if (tocado) drawRoundRect(jade.copy(alpha = if (oscuro) 0.14f else 0.1f), Offset(4 * u, cy - 15 * u), Size(maxOf(w - 8 * u, 0f), 30 * u), CornerRadius(1.5f * u))
        val color = if (tocado) jade else (if (oscuro) mezcla(CINABRIO, ESTUCO, 0.3f) else CINABRIO).copy(alpha = 0.6f * apagado)
        val q = 3 * u
        val x0 = minOf(14 * u, w * 0.3f); val x1 = w - x0
        val y = cy + 11.5f * u
        if (x1 - x0 < 4 * u) return
        val p = Path().apply {
            moveTo(x0 - q, y - 2 * q); lineTo(x0 - q, y - q); lineTo(x0, y - q); lineTo(x0, y)
            lineTo(x1, y); lineTo(x1, y - q); lineTo(x1 + q, y - q); lineTo(x1 + q, y - 2 * q)
        }
        drawPath(p, color, style = Stroke((if (tocado) 1.4f else 1.1f) * u))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El barro bruñido del cajete del vacío. */
    private val BARRO = Color(0xFFC08A5A)

    /** El cinabrio con la luz del momento: de noche, aclarado con estuco para que se vea. */
    private fun cinabrio(oscuro: Boolean): Color = if (oscuro) mezcla(CINABRIO, ESTUCO, 0.3f) else CINABRIO
    /** El jade con la luz del momento. */
    private fun jade(oscuro: Boolean): Color = if (oscuro) mezcla(JADE, Color.White, 0.3f) else JADE
    /** El estuco de los recuadros: claro de día; de noche, en penumbra. */
    private fun estucoRecuadro(oscuro: Boolean): Color = if (oscuro) mezcla(ESTUCO, Color(0xFF100C08), 0.86f) else mezcla(ESTUCO, Color.White, 0.3f)

    /** Una cuenta de jade incrustada, de radio [r], con su engaste negro y su brillo. */
    private fun DrawScope.cuenta(c: Offset, r: Float, oscuro: Boolean, viva: Boolean) {
        drawCircle(t(NEGRO, oscuro).copy(alpha = 0.55f), r * 1.2f, c)
        drawCircle(if (viva) mezcla(jade(oscuro), AZUL_MAYA, 0.35f) else jade(oscuro), r, c)
        drawCircle(Color.White.copy(alpha = if (viva) 0.8f else 0.35f), r * 0.4f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: una raya de cinabrio fina abajo y, a la izquierda, una cuenta de jade incrustada (con detalle, una más chica
     * debajo). Al tocarla el estuco toma un verde muy tenue y la cuenta brilla. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(jade(oscuro).copy(alpha = if (oscuro) 0.12f else 0.08f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine(cinabrio(oscuro).copy(alpha = if (oscuro) 0.3f else 0.3f), Offset(x0, h - 1 * u), Offset(w - 4 * u, h - 1 * u), 0.9f * u)
        if (e.conDetalle) {
            cuenta(Offset(5.5f * u, h / 2 - 2.6f * u), 1.8f * u, oscuro, e.presionado)
            cuenta(Offset(5.5f * u, h / 2 + 3.2f * u), 1.2f * u, oscuro, e.presionado)
        } else cuenta(Offset(5.5f * u, h / 2), 1.9f * u, oscuro, e.presionado)
    }

    /** Flecha: una punta escalonada, como la mitad de una pirámide acostada, de cinabrio, con una cuenta de jade en medio. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val w = size.width; val h = size.height
        val cy = h / 2
        val x = floatArrayOf(0.18f, 0.4f, 0.62f, 0.86f)
        val y = floatArrayOf(0.38f, 0.27f, 0.15f)
        val p = Path().apply {
            moveTo(w * x[0], cy - h * y[0])
            for (j in 0 until 3) { lineTo(w * x[j + 1], cy - h * y[j]); if (j < 2) lineTo(w * x[j + 1], cy - h * y[j + 1]) }
            lineTo(w * x[3], cy + h * y[2])
            for (j in 2 downTo 0) { lineTo(w * x[j + 1], cy + h * y[j]); lineTo(w * x[j], cy + h * y[j]) }
            close()
        }
        drawPath(p, cinabrio(oscuro))
        cuenta(Offset(w * 0.46f, cy), h * 0.08f, oscuro, false)
    }

    /**
     * Marca de fijar: la flor de cuatro pétalos del glifo kin (el sol, el día): cuatro pétalos en cruz alrededor de un centro.
     * Fija, los pétalos de cinabrio y el centro de jade; sin fijar, solo el contorno, tenue. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        val largo = 7.2f * k; val ancho = 5.6f * k; val d = 5.6f * k
        val petalos = listOf(
            Rect(c.x - ancho / 2, c.y - d - largo / 2, c.x + ancho / 2, c.y - d + largo / 2),
            Rect(c.x - ancho / 2, c.y + d - largo / 2, c.x + ancho / 2, c.y + d + largo / 2),
            Rect(c.x - d - largo / 2, c.y - ancho / 2, c.x - d + largo / 2, c.y + ancho / 2),
            Rect(c.x + d - largo / 2, c.y - ancho / 2, c.x + d + largo / 2, c.y + ancho / 2),
        )
        if (fija) {
            petalos.forEach { drawOval(cinabrio(oscuro), it.topLeft, it.size) }
            drawCircle(t(NEGRO, oscuro), 3.4f * k, c)
            drawCircle(jade(oscuro), 2.6f * k, c)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.9f * k, Offset(c.x - 0.9f * k, c.y - 0.9f * k))
        } else {
            val linea = cinabrio(oscuro).copy(alpha = if (oscuro) 0.8f else 0.5f)
            petalos.forEach { drawOval(linea, it.topLeft, it.size, style = Stroke(0.8f * u)) }
            drawCircle(linea, 2.6f * k, c, style = Stroke(0.8f * u))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(ESTUCO, CALIZA_OSC, 0.35f) else Color(0xFF5E4630)

    /**
     * Vacío: una página de códice plegado, de papel de amate cubierto de estuco, todavía sin pintar: las rayas rojas de los
     * registros arriba y abajo (dobles, como se trazaban para guiar al tlacuilo) y, en cada pliegue, una marca corta arriba y
     * abajo, donde la tira se dobla en biombo. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 20 * u) return
        drawRect(estucoRecuadro(oscuro), Offset.Zero, size)
        val granos = (w * h / (220 * u * u)).toInt().coerceIn(10, 180)
        for (j in 0 until granos) drawCircle(hondo(CALIZA_OSC, oscuro).copy(alpha = 0.2f), (0.3f + 0.5f * azar(s, j)) * u, Offset(w * azar(s, j + 300), h * azar(s, j + 600)))
        val rojo = cinabrio(oscuro).copy(alpha = if (oscuro) 0.55f else 0.6f)
        listOf(4 * u, h - 4 * u).forEach { y ->
            drawLine(rojo, Offset(2 * u, y), Offset(w - 2 * u, y), 1 * u)
            val y2 = if (y < h / 2) y + 2.4f * u else y - 2.4f * u
            drawLine(rojo.copy(alpha = rojo.alpha * 0.6f), Offset(2 * u, y2), Offset(w - 2 * u, y2), 0.6f * u)
        }
        val pliegue = (if (oscuro) Color.Black else CALIZA_OSC).copy(alpha = if (oscuro) 0.5f else 0.5f)
        listOf(w / 3, 2 * w / 3).forEach { x ->
            drawLine(pliegue, Offset(x, 7.5f * u), Offset(x, 13 * u), 0.8f * u)
            drawLine(pliegue, Offset(x, h - 13 * u), Offset(x, h - 7.5f * u), 0.8f * u)
        }
    }

    /**
     * Lo vacío: un cajete trípode, el cuenco de barro sobre tres soportes, visto un poco desde arriba: la boca abierta muestra el
     * fondo, vacío; en la pared, la banda de cinabrio con sus puntos negros. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val barro = t(BARRO, oscuro)
        val borde = if (oscuro) mezcla(BARRO, ESTUCO, 0.35f) else mezcla(BARRO, NEGRO, 0.5f)
        // Los tres soportes.
        listOf(0.27f, 0.5f, 0.73f).forEachIndexed { j, x ->
            val y0 = if (j == 1) 0.7f else 0.66f
            val pata = Path().apply { moveTo(f(x - 0.06f), f(y0)); lineTo(f(x + 0.06f), f(y0)); lineTo(f(x + 0.035f), f(y0 + 0.22f)); lineTo(f(x - 0.035f), f(y0 + 0.22f)); close() }
            drawPath(pata, barro); drawPath(pata, borde, style = Stroke(0.8f * u))
            drawCircle(borde, f(0.012f), Offset(f(x), f(y0 + 0.1f)))
        }
        // El cuerpo del cuenco.
        val cuerpo = Path().apply {
            moveTo(f(0.1f), f(0.38f)); lineTo(f(0.9f), f(0.38f))
            cubicTo(f(0.88f), f(0.62f), f(0.72f), f(0.74f), f(0.5f), f(0.74f))
            cubicTo(f(0.28f), f(0.74f), f(0.12f), f(0.62f), f(0.1f), f(0.38f)); close()
        }
        drawPath(cuerpo, barro)
        clipPath(cuerpo) {
            drawRect(cinabrio(oscuro), Offset(0f, f(0.44f)), Size(m, f(0.08f)))
            for (j in 0 until 6) drawCircle(t(NEGRO, oscuro), f(0.014f), Offset(f(0.2f + j * 0.12f), f(0.48f)))
        }
        drawPath(cuerpo, borde, style = Stroke(1 * u))
        // La boca abierta, con el fondo vacío.
        drawOval(barro, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)))
        drawOval(if (oscuro) Color(0xFF0A0806) else mezcla(BARRO, NEGRO, 0.6f), Offset(f(0.15f), f(0.325f)), Size(f(0.7f), f(0.11f)))
        drawOval(borde, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)), style = Stroke(1 * u))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) ESTUCO else NEGRO

    /**
     * Aviso: una franja de estuco con, a la izquierda, la greca escalonada de pie (los escalones que suben y bajan de arriba
     * abajo, de cinabrio) y, junto a ella, a la altura del primer renglón, un signo según [EstadoAviso.semilla]: la vírgula de la
     * palabra (la voluta que en los códices sale de la boca del que habla), una cuenta de jade o la flor kin. Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(estucoRecuadro(oscuro), Offset.Zero, size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.5f), Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 1 * u)
        val rojo = cinabrio(oscuro)
        val paso = maxOf(4 * u, 1f)
        val greca = Path().apply {
            var y = 3 * u; var k = 0
            moveTo(4 * u, y)
            while (y + paso < h - 3 * u && k < 200) {
                val x = if (k % 2 == 0) 9 * u else 4 * u
                lineTo(x, y); y += paso; lineTo(x, y); k++
            }
        }
        drawPath(greca, rojo, style = Stroke(1.4f * u))
        val cx = 18 * u; val cy = 16 * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> {
                val voz = Path().apply {
                    moveTo(cx - 4 * u, cy + 4 * u)
                    cubicTo(cx - 4 * u, cy - 4 * u, cx + 4.5f * u, cy - 5 * u, cx + 4 * u, cy)
                    cubicTo(cx + 3.6f * u, cy + 3 * u, cx - 0.5f * u, cy + 3 * u, cx, cy - 0.2f * u)
                }
                drawPath(voz, (if (oscuro) AZUL_MAYA else mezcla(AZUL_MAYA, NEGRO, 0.35f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
            }
            1 -> cuenta(Offset(cx, cy), 3 * u, oscuro, true)
            else -> {
                listOf(Offset(0f, -3f), Offset(0f, 3f), Offset(-3f, 0f), Offset(3f, 0f)).forEach { d -> drawCircle(rojo, 1.9f * u, Offset(cx + d.x * u, cy + d.y * u)) }
                drawCircle(jade(oscuro), 1.5f * u, Offset(cx, cy))
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF63C5DD) else mezcla(CINABRIO, NEGRO, 0.2f)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(JADE, Color.White, 0.5f) else JADE_HONDO

    /**
     * Cifra: un bloque de glifo, el cuadrete de esquinas redondas en que se pintaba cada palabra: el estuco liso, el borde de
     * cinabrio con su raya negra por dentro y, en el margen de arriba a la derecha, el siete en la cuenta de barras y puntos
     * (dos puntos sobre una barra), tenue. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 12 * u || h < 12 * u) return
        val radio = CornerRadius(minOf(9 * u, h * 0.2f))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(0.5f * u, 1.5f * u), Size(w - 1 * u, h - 1.5f * u), radio)
        val r = Rect(0.8f * u, 0.8f * u, w - 0.8f * u, h - 1.6f * u)
        drawRoundRect(estucoRecuadro(oscuro), r.topLeft, r.size, radio)
        drawRoundRect(cinabrio(oscuro), r.topLeft, r.size, radio, style = Stroke(1.8f * u))
        val dentro = Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u)
        drawRoundRect(t(NEGRO, oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), dentro.topLeft, dentro.size, CornerRadius(maxOf(radio.x - 2.6f * u, 1f)), style = Stroke(0.6f * u))
        val tinta = (if (oscuro) ESTUCO else NEGRO).copy(alpha = 0.38f)
        val x1 = r.right - 8 * u
        if (x1 - 9 * u > r.left + 30 * u) {
            drawRoundRect(tinta, Offset(x1 - 8 * u, r.top + 7 * u), Size(8 * u, 1.6f * u), CornerRadius(0.8f * u))
            drawCircle(tinta, 0.95f * u, Offset(x1 - 5.6f * u, r.top + 5 * u))
            drawCircle(tinta, 0.95f * u, Offset(x1 - 2.4f * u, r.top + 5 * u))
        }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** Los tres tonos de la turquesa de los mosaicos. */
    private val TURQUESAS = listOf(Color(0xFF3F9C98), Color(0xFF5DB3A8), Color(0xFF2F8288))

    /** El fondo liso de cada tarjeta, donde va el texto: el estuco del mosaico, el del mural, el de la noche y el barro bruñido. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> estucoRecuadro(oscuro)
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(AZUL_MAYA, Color(0xFF100C08), 0.85f) else ESTUCO
        TipoTarjeta.NOCHE -> if (oscuro) mezcla(NEGRO, Color(0xFF100C08), 0.5f) else mezcla(ESTUCO, CALIZA_OSC, 0.16f)
        TipoTarjeta.VIDA -> if (oscuro) mezcla(BARRO, Color(0xFF100C08), 0.82f) else mezcla(BARRO, ESTUCO, 0.62f)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) ESTUCO else NEGRO
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        oscuro -> mezcla(ESTUCO, CALIZA_OSC, 0.4f)
        tipo == TipoTarjeta.VIDA -> mezcla(NEGRO, BARRO, 0.25f)
        else -> mezcla(NEGRO, BARRO, 0.4f)
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA, TipoTarjeta.NOCHE -> if (oscuro) mezcla(JADE, Color.White, 0.45f) else JADE_HONDO
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(CINABRIO, ESTUCO, 0.45f) else mezcla(CINABRIO, NEGRO, 0.2f)
        TipoTarjeta.VIDA -> if (oscuro) mezcla(CINABRIO, ESTUCO, 0.45f) else mezcla(CINABRIO, NEGRO, 0.4f)
    }

    /**
     * Tarjetas: cuatro piezas de Mesoamérica. La cifra de Hoy es un mosaico de turquesa: una orla de teselas puestas a mano,
     * cada una un poco corrida y de su tono, con alguna de concha y de azabache, alrededor del estuco liso. «Te falta hoy» es
     * un mural: la banda de azul maya arriba con su fila de pirámides escalonadas en estuco, una raya de cinabrio debajo y
     * otra abajo. La noche es la banda del cielo de los códices: una franja oscura con su fila de ojos estelares (el ojo blanco
     * en su cerco de cinabrio). La tarjeta de Vida es una baldosa de barro bruñido, con el doble filete inciso y el brillo del
     * bruñido arriba a la izquierda. Al tocarlas se oscurecen apenas y la sombra se acorta. Destacadas: el mosaico lleva una
     * cuenta de jade en cada esquina y el estuco ribeteado de cinabrio; el mural completo cambia el azul por jade y la noche
     * sellada cierra los ojos y pone una cuenta de jade al final de la franja. Sin degradados.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> mosaicoTurquesa(e, oscuro)
            TipoTarjeta.PENDIENTE -> mural(e, oscuro)
            TipoTarjeta.NOCHE -> cieloCodice(e, oscuro)
            TipoTarjeta.VIDA -> baldosa(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, CALIZA_OSC, 0.16f)
    }

    private fun DrawScope.mosaicoTurquesa(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 17
        val radio = CornerRadius(4 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0.5f * u, (if (e.presionado) 0.8f else 1.8f) * u), Size(w - 1 * u, h - 1.8f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.8f * u)
        // La cama de resina negra donde se asientan las teselas.
        drawRoundRect(t(NEGRO, oscuro), r.topLeft, r.size, radio)
        // Las teselas de la orla, a lo largo del contorno, en dos hileras.
        val paso = maxOf(4.2f * u, 1f)
        val banda = 5.6f * u
        var k = 0
        fun tesela(x: Float, y: Float, lado: Float) {
            val c = when {
                azar(s, k + 900) < 0.06f -> mezcla(ESTUCO, Color.White, 0.3f)
                azar(s, k + 900) < 0.1f -> JADE
                else -> TURQUESAS[(azar(s, k + 1200) * 3).toInt().coerceIn(0, 2)]
            }
            val dx = (azar(s, k) - 0.5f) * 0.6f * u; val dy = (azar(s, k + 300) - 0.5f) * 0.6f * u
            val l = lado * (0.78f + 0.14f * azar(s, k + 600))
            drawRect(t(c, oscuro), Offset(x + dx + (lado - l) / 2, y + dy + (lado - l) / 2), Size(l, l))
            k++
        }
        val lado = banda / 2
        val nx = ((r.width - 2 * u) / paso).toInt().coerceIn(1, 200)
        val px = (r.width - 2 * u) / nx
        for (fila in 0..1) for (j in 0 until nx) {
            val x = r.left + 1 * u + j * px + (if (fila == 1) px / 2 else 0f)
            if (fila == 1 && j == nx - 1) continue
            tesela(x, r.top + 0.6f * u + fila * lado, minOf(lado, px))
            tesela(x, r.bottom - 0.6f * u - (fila + 1) * lado, minOf(lado, px))
        }
        val ny = ((r.height - 2 * banda) / paso).toInt().coerceIn(1, 120)
        val py = (r.height - 2 * banda) / ny
        for (fila in 0..1) for (j in 0 until ny) {
            val y = r.top + banda + j * py
            tesela(r.left + 0.6f * u + fila * lado, y, minOf(lado, py))
            tesela(r.right - 0.6f * u - (fila + 1) * lado, y, minOf(lado, py))
        }
        // El estuco liso del medio.
        val c = r.deflate(banda + 0.6f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), c.topLeft, c.size, CornerRadius(1.5f * u))
        if (e.destacado) {
            drawRoundRect(cinabrio(oscuro), c.topLeft, c.size, CornerRadius(1.5f * u), style = Stroke(1.2f * u))
            listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { p ->
                cuenta(Offset(p.x + (if (p.x < w / 2) 3 else -3) * u, p.y + (if (p.y < h / 2) 3 else -3) * u), 3 * u, oscuro, true)
            }
        }
    }

    private fun DrawScope.mural(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val completo = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u))
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        // La banda de arriba: azul maya (jade si está completo) con su fila de pirámides escalonadas en estuco.
        val alto = minOf(9.5f * u, h * 0.15f)
        val banda = if (completo) t(JADE, oscuro) else t(AZUL_MAYA, oscuro)
        drawRect(banda, Offset.Zero, Size(w, alto))
        val estuco = if (oscuro) mezcla(ESTUCO, Color(0xFF100C08), 0.2f) else ESTUCO
        val paso = maxOf(alto * 2.2f, 1f)
        val n = (w / paso).toInt().coerceIn(1, 80)
        val ancho = w / n
        val base = alto - 1.2f * u; val cima = 1.4f * u
        val pts = listOf(0f to 1f, 0.12f to 1f, 0.12f to 0.66f, 0.26f to 0.66f, 0.26f to 0.33f, 0.4f to 0.33f, 0.4f to 0f,
            0.6f to 0f, 0.6f to 0.33f, 0.74f to 0.33f, 0.74f to 0.66f, 0.88f to 0.66f, 0.88f to 1f, 1f to 1f)
        for (j in 0 until n) {
            val p = Path()
            pts.forEachIndexed { i, (fx, fy) ->
                val x = j * ancho + fx * ancho; val y = cima + fy * (base - cima)
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            drawPath(p, estuco)
            drawCircle(banda, 0.9f * u, Offset(j * ancho + ancho / 2, cima + (base - cima) * 0.55f))
        }
        drawLine(estuco, Offset(0f, base), Offset(w, base), 0.6f * u)
        // Las rayas de cinabrio: bajo la banda, abajo y a los lados.
        val rojo = cinabrio(oscuro)
        drawRect(rojo, Offset(0f, alto), Size(w, 1.6f * u))
        drawRect(t(NEGRO, oscuro), Offset(0f, alto + 1.6f * u), Size(w, 0.6f * u))
        drawRect(rojo, Offset(0f, r.bottom - 2.4f * u), Size(w, 2.4f * u))
        drawRect(rojo.copy(alpha = 0.8f), Offset(0f, alto), Size(1.6f * u, r.bottom - alto))
        drawRect(rojo.copy(alpha = 0.8f), Offset(w - 1.6f * u, alto), Size(1.6f * u, r.bottom - alto))
        if (completo) cuenta(Offset(w - 18 * u, (alto + r.bottom) / 2 + 0.8f * u), 4 * u, oscuro, true)
    }

    private fun DrawScope.cieloCodice(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        val radio = CornerRadius(3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u), radio)
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        // La franja del cielo nocturno, oscura, entre dos rayas de cinabrio.
        val alto = minOf(9 * u, h * 0.11f)
        val cielo = if (oscuro) mezcla(AZUL_MAYA, Color(0xFF100C08), 0.62f) else mezcla(NEGRO, AZUL_MAYA, 0.15f)
        clipPath(Path().apply { addRoundRect(RoundRect(r, radio)) }) { drawRect(cielo, Offset.Zero, Size(w, alto)) }
        val rojo = cinabrio(oscuro)
        drawLine(rojo, Offset(0f, alto + 0.7f * u), Offset(w, alto + 0.7f * u), 1.4f * u)
        drawLine(rojo.copy(alpha = 0.7f), Offset(3 * u, r.bottom - 3 * u), Offset(w - 3 * u, r.bottom - 3 * u), 0.9f * u)
        // Los ojos estelares: el cerco de cinabrio, el blanco del ojo y la pupila abajo; cerrados si el día está sellado.
        val ojo = alto * 0.34f
        val paso = maxOf(24 * u, 1f)
        val fin = if (sellado) w - 24 * u else w
        val n = (fin / paso).toInt().coerceIn(1, 40)
        val blanco = if (oscuro) mezcla(ESTUCO, Color(0xFF100C08), 0.15f) else mezcla(ESTUCO, Color.White, 0.4f)
        for (j in 0 until n) {
            val c = Offset(paso * (j + 0.5f), alto / 2)
            drawCircle(rojo, ojo, c)
            if (sellado) drawLine(t(NEGRO, oscuro), Offset(c.x - ojo * 0.7f, c.y), Offset(c.x + ojo * 0.7f, c.y), 0.8f * u, StrokeCap.Round)
            else {
                drawCircle(blanco, ojo * 0.68f, c)
                drawArc(t(NEGRO, oscuro), 0f, 180f, true, Offset(c.x - ojo * 0.4f, c.y - ojo * 0.2f), Size(ojo * 0.8f, ojo * 0.8f))
            }
            // Un punto blanco entre ojo y ojo, como las estrellas chicas.
            drawCircle(blanco.copy(alpha = 0.8f), 0.5f * u, Offset(c.x + paso / 2, alto * (if (j % 2 == 0) 0.3f else 0.7f)))
        }
        if (sellado) cuenta(Offset(w - 12 * u, alto / 2 + 1 * u), minOf(3.2f * u, alto * 0.4f), oscuro, true)
    }

    private fun DrawScope.baldosa(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla + 29
        val radio = CornerRadius(5 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0.5f * u, (if (e.presionado) 0.8f else 1.8f) * u), Size(w - 1 * u, h - 1.8f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.8f * u)
        drawRoundRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size, radio)
        // Las motas del barro, solo junto al borde.
        val mota = (if (oscuro) BARRO else NEGRO).copy(alpha = if (oscuro) 0.2f else 0.14f)
        val motas = ((r.width + r.height) / (4 * u)).toInt().coerceIn(12, 200)
        for (j in 0 until motas) {
            val f = azar(s, j); val d = (1 + 2.6f * azar(s, j + 300)) * u
            val p = when (j % 4) { 0 -> Offset(r.left + r.width * f, r.top + d); 1 -> Offset(r.left + r.width * f, r.bottom - d)
                2 -> Offset(r.left + d, r.top + r.height * f); else -> Offset(r.right - d, r.top + r.height * f) }
            drawCircle(mota, (0.3f + 0.4f * azar(s, j + 600)) * u, p)
        }
        // El brillo del bruñido, arriba a la izquierda.
        drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), 188f, 64f, false, Offset(r.left + 2.5f * u, r.top + 2.2f * u),
            Size(minOf(46 * u, r.width * 0.6f), r.height * 0.9f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // El doble filete inciso.
        val inciso = (if (oscuro) Color.Black else NEGRO).copy(alpha = if (oscuro) 0.55f else 0.4f)
        val luz = Color.White.copy(alpha = if (oscuro) 0.05f else 0.3f)
        for (d in listOf(3.2f, 5f)) {
            val q = r.deflate(d * u)
            drawRoundRect(luz, q.topLeft + Offset(0.5f * u, 0.5f * u), q.size, CornerRadius(3 * u), style = Stroke(0.5f * u))
            drawRoundRect(inciso, q.topLeft, q.size, CornerRadius(3 * u), style = Stroke(0.6f * u))
        }
        // Tres puntos de cinabrio en la esquina de abajo a la derecha (el tres en barras y puntos).
        val rojo = cinabrio(oscuro)
        for (j in 0 until 3) drawCircle(rojo, 0.9f * u, Offset(r.right - (8.5f + j * 3) * u, r.bottom - 7.6f * u))
        if (e.destacado) drawRoundRect(rojo, r.deflate(1 * u).topLeft, r.deflate(1 * u).size, CornerRadius(4 * u), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- Gastos

    override val altoBalanza: Dp = 58.dp

    /**
     * Balanza: un friso de estuco a todo el ancho, partido al medio por la greca escalonada (el xicalcoliuhqui) pintada de
     * cinabrio. El tablero elegido lleva el marco de cinabrio grueso con el escalón en cada esquina, el estuco recién bruñido y,
     * en su orilla, una cuenta de jade que brilla; el otro queda en estuco opaco, con un filete fino y la cuenta apagada, y el
     * texto delgado. Sin degradados.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 30 * u) return
        val friso = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRect(if (oscuro) mezcla(CALIZA_OSC, Color(0xFF100C08), 0.78f) else mezcla(ESTUCO, CALIZA_OSC, 0.5f), friso.topLeft, friso.size)
        drawRect(t(NEGRO, oscuro).copy(alpha = 0.6f), friso.topLeft, friso.size, style = Stroke(1 * u))
        val xm = w / 2
        val rojo = cinabrio(oscuro)
        for (lado in 0..1) {
            val elegido = (lado == 1) == e.ingreso
            val r = if (lado == 0) Rect(friso.left + 4 * u, friso.top + 4 * u, xm - 10 * u, friso.bottom - 4 * u)
            else Rect(xm + 10 * u, friso.top + 4 * u, friso.right - 4 * u, friso.bottom - 4 * u)
            val fondo = when {
                elegido -> estucoRecuadro(oscuro)
                oscuro -> mezcla(ESTUCO, Color(0xFF100C08), 0.9f)
                else -> mezcla(ESTUCO, CALIZA_OSC, 0.3f)
            }
            drawRect(fondo, r.topLeft, r.size)
            if (elegido) {
                drawRect(rojo, r.topLeft + Offset(1.4f * u, 1.4f * u), Size(r.width - 2.8f * u, r.height - 2.8f * u), style = Stroke(2.8f * u))
                val k = 7 * u
                drawPath(escalon(Offset(r.left, r.top), k, 1f, 1f), rojo); drawPath(escalon(Offset(r.right, r.top), k, -1f, 1f), rojo)
                drawPath(escalon(Offset(r.left, r.bottom), k, 1f, -1f), rojo); drawPath(escalon(Offset(r.right, r.bottom), k, -1f, -1f), rojo)
            } else drawRect(t(NEGRO, oscuro).copy(alpha = if (oscuro) 0.5f else 0.3f), r.topLeft, r.size, style = Stroke(0.9f * u))
            val xc = if (lado == 0) r.left + 15 * u else r.right - 15 * u
            cuenta(Offset(xc, r.center.y), if (elegido) 4.2f * u else 3.2f * u, oscuro, elegido)
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val caja = Rect(r.left + 26 * u, r.top + 5 * u, r.right - 26 * u, r.bottom - 5 * u)
            if (elegido) textoEn(medidor, texto, caja, LetraExtra.codice, legibleAnt(base, 5f, fondo), 19f, peso = FontWeight.Bold, espaciado = 0.6f)
            else textoEn(medidor, texto, caja, LetraExtra.codice, legibleAnt(mezcla(base, fondo, 0.2f), 4.7f, fondo), 17f, espaciado = 0.6f)
        }
        // La greca escalonada del medio: una escalera que sube y baja, de arriba abajo.
        val alto = friso.height - 6 * u
        val paso = maxOf(alto / 6f, 1f)
        val greca = Path().apply {
            moveTo(xm - 6 * u, friso.top + 3 * u)
            var y = friso.top + 3 * u
            var j = 0
            while (y < friso.bottom - 3 * u - 0.5f && j < 12) {
                val x = if (j % 2 == 0) xm + 6 * u else xm - 6 * u
                lineTo(x, y); y = minOf(y + paso, friso.bottom - 3 * u); lineTo(x, y); j++
            }
        }
        drawPath(greca, rojo, style = Stroke(2.4f * u))
    }

    override val altoMes: Dp = 52.dp

    /**
     * Encabezado del mes: un tablero de estuco bruñido a todo el ancho, con su marco de cinabrio y el escalón de la greca en cada
     * esquina; el mes al centro, entre dos cuentas de jade, y en los extremos las flechas escalonadas de las filas, grandes.
     * Sin degradados.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 24 * u) return
        val r = Rect(1 * u, 2 * u, w - 1 * u, h - 2 * u)
        val fondo = estucoRecuadro(oscuro)
        drawRect(fondo, r.topLeft, r.size)
        val rojo = cinabrio(oscuro)
        drawRect(rojo, r.topLeft + Offset(1.2f * u, 1.2f * u), Size(r.width - 2.4f * u, r.height - 2.4f * u), style = Stroke(2.4f * u))
        val k = 8 * u
        drawPath(escalon(Offset(r.left, r.top), k, 1f, 1f), rojo); drawPath(escalon(Offset(r.right, r.top), k, -1f, 1f), rojo)
        drawPath(escalon(Offset(r.left, r.bottom), k, 1f, -1f), rojo); drawPath(escalon(Offset(r.right, r.bottom), k, -1f, -1f), rojo)
        val texto = Rect(58 * u, r.top + 3 * u, w - 58 * u, r.bottom - 3 * u)
        cuenta(Offset(texto.left - 2 * u, r.center.y), 3.4f * u, oscuro, true)
        cuenta(Offset(texto.right + 2 * u, r.center.y), 3.4f * u, oscuro, true)
        textoEn(medidor, e.titulo, Rect(texto.left + 8 * u, texto.top, texto.right - 8 * u, texto.bottom), LetraExtra.codice,
            legibleAnt(if (oscuro) ESTUCO else NEGRO, 7f, fondo), 24f, peso = FontWeight.Bold, espaciado = 0.8f)
        flechasMesAnt(48 * u) { flechaFila(oscuro) }
    }

    /**
     * Asiento: una raya de cinabrio muy fina abajo y, al principio, la marca del tipo: un punto de cinabrio (el punto de los
     * numerales) si es un gasto, una cuenta de jade incrustada si es un ingreso. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.4f * u
        drawLine(cinabrio(oscuro).copy(alpha = if (oscuro) 0.28f else 0.3f), Offset(12 * u, y), Offset(w - 2 * u, y), 0.8f * u)
        if (e.ingreso) cuenta(Offset(5 * u, y - 1 * u), 2.4f * u, oscuro, false)
        else drawCircle(cinabrio(oscuro).copy(alpha = 0.9f), 2.4f * u, Offset(5 * u, y - 1 * u))
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * Rótulo del día: la fecha en un bloque de glifo (el cartucho de esquinas redondas de las inscripciones), de estuco con su
     * borde de cinabrio, y de ahí hacia la derecha una hilera de puntos de cinabrio, como los de los numerales, con una cuenta
     * de jade al final. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 10 * u) return
        val tam = 14f
        val ancho = minOf(anchoTextoAnt(medidor, texto, LetraExtra.codice, tam, FontWeight.Bold, 1.2f) + 2 * u, w * 0.6f)
        val r = Rect(1 * u, 1 * u, ancho + 15 * u, h - 1 * u)
        val fondo = estucoRecuadro(oscuro)
        val rojo = cinabrio(oscuro)
        drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(5 * u))
        drawRoundRect(rojo, r.topLeft, r.size, CornerRadius(5 * u), style = Stroke(1.6f * u))
        textoEn(medidor, texto, Rect(r.left + 7 * u, r.top, r.left + 7 * u + ancho, r.bottom), LetraExtra.codice,
            legibleAnt(if (oscuro) ESTUCO else JADE_HONDO, 6f, fondo), tam, peso = FontWeight.Bold, espaciado = 1.2f)
        val x0 = r.right + 10 * u; val x1 = w - 14 * u
        val paso = maxOf(8 * u, 1f)
        val n = ((x1 - x0) / paso).toInt().coerceIn(0, 80)
        for (j in 0..n) drawCircle(rojo.copy(alpha = 0.55f), 1.2f * u, Offset(x0 + j * paso, h / 2))
        cuenta(Offset(w - 6 * u, h / 2), 3 * u, oscuro, true)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * Una greca escalonada (la xicalcoliuhqui) entre [x0] y [x1], de [k] de alto con la base en [y]: escalones que suben y bajan,
     * encajados. A lo más 120.
     */
    private fun DrawScope.grecaEscalonada(x0: Float, x1: Float, y: Float, k: Float, color: Color) {
        val paso = maxOf(k * 1.2f, 1f)
        val n = ((x1 - x0) / paso).toInt().coerceIn(0, 120)
        val x = x0 + ((x1 - x0) - n * paso) / 2
        for (j in 0 until n) {
            if (j % 2 == 0) drawPath(escalon(Offset(x + j * paso, y), k, 1f, -1f), color)
            else drawPath(escalon(Offset(x + j * paso + k, y - k), k, -1f, 1f), color)
        }
    }

    /** Un cartucho de glifo de día en [r]: el marco redondeado, dos puntos y una barra (el siete de los mayas) y un ojo al medio. */
    private fun DrawScope.glifoDia(r: Rect, marco: Color, relleno: Color, u: Float) {
        drawRoundRect(relleno, r.topLeft, r.size, CornerRadius(r.height * 0.3f))
        drawRoundRect(marco, r.topLeft, r.size, CornerRadius(r.height * 0.3f), style = Stroke(1f * u))
        drawCircle(marco, 1.1f * u, Offset(r.center.x - r.width * 0.18f, r.top + r.height * 0.3f))
        drawCircle(marco, 1.1f * u, Offset(r.center.x + r.width * 0.18f, r.top + r.height * 0.3f))
        drawLine(marco, Offset(r.left + r.width * 0.22f, r.top + r.height * 0.72f), Offset(r.right - r.width * 0.22f, r.top + r.height * 0.72f), 1.3f * u, StrokeCap.Round)
        drawOval(marco, Offset(r.center.x - 1.4f * u, r.top + r.height * 0.32f), Size(2.8f * u, 2 * u), style = Stroke(0.7f * u))
    }

    /**
     * Hoja rápida: el templo sobre la pirámide. Bajo las gradas de la hoja, una moldura de cinabrio que sigue su perfil y, en el
     * cuerpo de la pirámide, el cartucho de un glifo de día; de lado a lado, la greca escalonada; por los dos cantos, un cordel con
     * cuentas de jade ensartadas, y en las esquinas de abajo, el escalón de la greca, grande. Sin degradados.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        val linea = alfaAnt(e.primario, 0.5f)
        val x0 = w / 2 - 40 * u
        val perfil = Path().apply {
            moveTo(10 * u, 19 * u); lineTo(x0, 19 * u)
            for (k in 0 until 3) { lineTo(x0 + k * 10 * u, (15 - 4 * k) * u); lineTo(x0 + (k + 1) * 10 * u, (15 - 4 * k) * u) }
            for (k in 3 downTo 1) { lineTo(x0 + (9 - k) * 10 * u, (19 - 4 * k) * u); lineTo(x0 + (9 - k) * 10 * u, (23 - 4 * k) * u) }
            lineTo(w - 10 * u, 19 * u)
        }
        drawPath(perfil, linea, style = Stroke(1f * u))
        glifoDia(Rect(w / 2 - 8 * u, 7.5f * u, w / 2 + 8 * u, 18.5f * u), alfaAnt(e.primario, 0.75f), alfaAnt(e.secundario, 0.14f), u)
        grecaEscalonada(14 * u, w - 14 * u, 26.5f * u, 4.5f * u, alfaAnt(e.primario, 0.3f))
        // El cordel de los cantos con sus cuentas de jade.
        val y0 = 34 * u; val y1 = h - 26 * u
        listOf(8 * u, w - 8 * u).forEach { x ->
            if (y1 > y0) drawLine(alfaAnt(e.secundario, 0.3f), Offset(x, y0), Offset(x, y1), 0.8f * u)
            cuenta(Offset(x, 24 * u), 2.4f * u, oscuro, false)
            val paso = maxOf(44 * u, 1f)
            var y = y0 + 10 * u; var k = 0
            while (y < y1 - 6 * u && k < 20) { cuenta(Offset(x, y), if (k % 2 == 0) 2 * u else 1.5f * u, oscuro, false); y += paso / 2; k++ }
        }
        // Los escalones de las esquinas de abajo, con una raya de cinabrio entre ellos.
        drawPath(escalon(Offset(3 * u, h - 3 * u), 13 * u, 1f, -1f), alfaAnt(e.primario, 0.42f))
        drawPath(escalon(Offset(w - 3 * u, h - 3 * u), 13 * u, -1f, -1f), alfaAnt(e.primario, 0.42f))
        drawLine(linea, Offset(20 * u, h - 5 * u), Offset(w - 20 * u, h - 5 * u), 0.9f * u)
    }

    /**
     * Relación: bajo el dintel, una greca escalonada; una moldura paralela a cada esquina cortada; a media altura de cada lado, una
     * cuenta de jade; abajo, una raya de cinabrio con un escalón en cada esquina. El centro, liso. Sin degradados.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val c = alfaAnt(e.primario, 0.5f)
        grecaEscalonada(w * 0.28f, w * 0.72f, 8.5f * u, 4 * u, c)
        drawLine(c, Offset(2 * u, 21.5f * u), Offset(21.5f * u, 2 * u), 0.9f * u)
        drawLine(c, Offset(w - 2 * u, 21.5f * u), Offset(w - 21.5f * u, 2 * u), 0.9f * u)
        cuenta(Offset(4.5f * u, h / 2), 1.9f * u, oscuro, false)
        cuenta(Offset(w - 4.5f * u, h / 2), 1.9f * u, oscuro, false)
        drawLine(c, Offset(10 * u, h - 4 * u), Offset(w - 10 * u, h - 4 * u), 0.9f * u)
        drawPath(escalon(Offset(2.5f * u, h - 2.5f * u), 6 * u, 1f, -1f), alfaAnt(e.primario, 0.45f))
        drawPath(escalon(Offset(w - 2.5f * u, h - 2.5f * u), 6 * u, -1f, -1f), alfaAnt(e.primario, 0.45f))
    }

    /** Pista: el cartucho de un glifo de día, con su pie de tres lóbulos (el del medio, una cuenta). Sin degradados. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f
        val x0 = (size.width - l) / 2; val y0 = (size.height - l) / 2
        drawRoundRect(e.primario, Offset(x0 + l * 0.06f, y0 + l * 0.04f), Size(l * 0.88f, l * 0.78f), CornerRadius(l * 0.2f), style = Stroke(g * 0.8f))
        for (k in -1..1) {
            val c = Offset(x0 + l * (0.5f + k * 0.2f), y0 + l * 0.9f)
            if (k == 0) drawCircle(e.secundario, l * 0.065f, c) else drawCircle(e.primario, l * 0.065f, c, style = Stroke(g * 0.6f))
        }
        listOf(0.13f, 0.87f).forEach { x -> drawCircle(alfaAnt(e.secundario, 0.8f), l * 0.028f, Offset(x0 + x * l, y0 + l * 0.12f)) }
    }
}

/**
 * Horror cósmico: el campo es una ficha del catálogo de la biblioteca, de papel amarillento, clavada con dos tachuelas de
 * latón; a la izquierda, el doble margen rojo; abajo, la raya azul de escribir (con varios renglones, el rayado entero) y, en
 * la esquina, el cerco desvaído de una taza de té. Con el foco el margen se marca y la ficha se rodea de un resplandor verdoso,
 * algo que no debería estar ahí; con un error, el borde se pinta de rojo. Sin degradados.
 */
object ComunArcano : DisenoComun {
    private val PAPEL = Color(0xFFE8DFC8)
    private val PAPEL_VIEJO = Color(0xFFD6C7A0)
    private val FICHA = Color(0xFFF1EADA)
    private val TE = Color(0xFF9C7A48)
    private val RAYADO = Color(0xFF8AA0B8)
    private val MARGEN_ROJO = Color(0xFFB0473A)
    private val TINTA = Color(0xFF231A12)
    private val LATON = Color(0xFFB8963E)
    private val LATON_OSC = Color(0xFF6E5622)
    private val VERDE_RARO = Color(0xFFB8E07A)
    private val TAPETE = Color(0xFF2E4A3A)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.32f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(FICHA, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(VERDE_RARO, PAPEL, 0.35f) else TAPETE
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0806C) else Color(0xFFA0281E)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val ficha = hondo(FICHA, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, ficha, 0.5f)
        val encendido = e.enfocado && e.habilitado
        val r = Rect(2.5f * u, 2 * u, w - 2.5f * u, h - 3 * u)
        // El resplandor verdoso con el foco, por fuera de la ficha.
        if (encendido && !e.error) {
            drawRect(VERDE_RARO.copy(alpha = if (oscuro) 0.18f else 0.3f), Offset(r.left - 1 * u, r.top - 1 * u), Size(r.width + 2 * u, r.height + 2.5f * u), style = Stroke(2.2f * u))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size)
        drawRect(ficha, r.topLeft, r.size)
        // El amarillo del canto de arriba y unas pecas del tiempo en las esquinas.
        drawRect(hondo(PAPEL_VIEJO, oscuro).copy(alpha = 0.5f), r.topLeft, Size(r.width, 2.5f * u))
        for (j in 0 until 6) {
            val izq = j % 2 == 0
            val x = if (izq) r.left + 3 * u + 10 * u * azar(s, j) else r.right - 3 * u - 10 * u * azar(s, j)
            val y = if (j < 4) r.bottom - 2 * u - 6 * u * azar(s, j + 10) else r.top + 4 * u + 4 * u * azar(s, j + 10)
            drawCircle(hondo(TE, oscuro).copy(alpha = 0.18f), (0.6f + 0.8f * azar(s, j + 20)) * u, Offset(x, y))
        }
        // El cerco de la taza, en la esquina de abajo a la derecha.
        clipRect(r.left, r.top, r.right, r.bottom) {
            val cc = Offset(r.right - 13 * u, r.bottom + 2 * u)
            drawCircle(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.35f else 0.22f), 11 * u, cc, style = Stroke(1.3f * u))
            drawArc(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.25f else 0.15f), 190f, 120f, false, Offset(cc.x - 9.5f * u, cc.y - 9.5f * u), Size(19 * u, 19 * u), style = Stroke(0.8f * u))
        }
        // El rayado azul: la raya de escribir, o todos los renglones.
        val raya = a(if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = if (oscuro) 0.55f else 0.6f)
        if (e.multilinea) renglonesAntiguedad(r.left + 6 * u, r.right, margenCampoY.toPx(), r.bottom - 4 * u, raya, 0.7f * u)
        else drawLine(raya, Offset(r.left + 6 * u, r.bottom - 7 * u), Offset(r.right, r.bottom - 7 * u), 0.7f * u)
        // El doble margen rojo.
        val rojo = a(if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO).copy(alpha = if (encendido || e.error) 0.95f else 0.55f)
        val xm = r.left + 12 * u
        val gm = (if (encendido) 1.1f else 0.8f) * u
        drawLine(rojo, Offset(xm, r.top), Offset(xm, r.bottom), gm)
        drawLine(rojo, Offset(xm - 3 * u, r.top), Offset(xm - 3 * u, r.bottom), gm)
        // Las dos tachuelas de latón, arriba.
        listOf(r.left + 5 * u, r.right - 5 * u).forEach { x ->
            val o = Offset(x, r.top + 5 * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 2.2f * u, Offset(o.x + 0.5f * u, o.y + 0.7f * u))
            drawCircle(a(t(LATON, oscuro)), 2 * u, o)
            drawCircle(a(t(LATON_OSC, oscuro)), 2 * u, o, style = Stroke(0.5f * u))
            drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, Offset(o.x - 0.6f * u, o.y - 0.6f * u))
        }
        // El borde: verde de la lámpara con el foco, rojo con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) VERDE_RARO.copy(alpha = 0.75f) else TAPETE
            else -> a(hondo(PAPEL_VIEJO, oscuro)).copy(alpha = if (oscuro) 0.6f else 0.8f)
        }
        val g = (if (e.error || encendido) 1.6f else 0.7f) * u
        drawRect(linea, Offset(r.left + g / 2, r.top + g / 2), Size(r.width - g, r.height - g), style = Stroke(g))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /** Un asterisco de seis rayos, de radio [r], centrado en [c]. */
    private fun DrawScope.asterisco(c: Offset, r: Float, color: Color, grosor: Float) {
        for (j in 0 until 3) {
            val ang = Math.toRadians(90.0 + j * 60.0)
            val dx = (r * kotlin.math.cos(ang)).toFloat(); val dy = (r * kotlin.math.sin(ang)).toFloat()
            drawLine(color, Offset(c.x - dx, c.y - dy), Offset(c.x + dx, c.y + dy), grosor, StrokeCap.Round)
        }
    }

    /**
     * Separador: la raya azul de una ficha del catálogo, cruzada a la izquierda por el doble margen rojo (dos rayitas), y al
     * centro un asterismo (⁂), los tres asteriscos con que los libros viejos cortan una sección. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = (if (oscuro) 0.55f else 0.65f) * f)
        val rojo = (if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO).copy(alpha = 0.5f * f)
        val tinta = (if (oscuro) PAPEL else TINTA).copy(alpha = (if (oscuro) 0.45f else 0.5f) * f)
        val cx = w / 2
        val hueco = 10 * u
        drawLine(raya, Offset(2 * u, cy), Offset(cx - hueco, cy), 0.8f * u)
        drawLine(raya, Offset(cx + hueco, cy), Offset(w - 2 * u, cy), 0.8f * u)
        val xm = 14 * u
        drawLine(rojo, Offset(xm, cy - 3.5f * u), Offset(xm, cy + 3.5f * u), 0.7f * u)
        drawLine(rojo, Offset(xm - 2.5f * u, cy - 3.5f * u), Offset(xm - 2.5f * u, cy + 3.5f * u), 0.7f * u)
        asterisco(Offset(cx, cy - 2.1f * u), 1.6f * u, tinta, 0.6f * u)
        asterisco(Offset(cx - 2.6f * u, cy + 2.1f * u), 1.6f * u, tinta, 0.6f * u)
        asterisco(Offset(cx + 2.6f * u, cy + 2.1f * u), 1.6f * u, tinta, 0.6f * u)
    }

    /** El texto de los botones: el verde del tapete de día, el amarillo de la lámpara de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6D58C) else Color(0xFF2D5446)

    /**
     * Botón de texto: la raya azul de escribir bajo la palabra y, a la izquierda, el doble margen rojo de la ficha. Al tocarlo
     * aparece detrás el resplandor verdoso de la ficha enfocada, algo que no debería estar ahí; apagado, la tinta se desvaía.
     * Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        if (tocado && w > 8 * u) drawRect(VERDE_RARO.copy(alpha = if (oscuro) 0.16f else 0.26f), Offset(4 * u, cy - 15 * u), Size(w - 8 * u, 30 * u))
        val raya = (if (oscuro) mezcla(RAYADO, PAPEL, 0.1f) else RAYADO).copy(alpha = (if (tocado) 0.95f else 0.6f) * apagado)
        val rojo = (if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO).copy(alpha = (if (tocado) 0.9f else 0.5f) * apagado)
        val y = cy + 11 * u
        drawLine(raya, Offset(4 * u, y), Offset(maxOf(w - 4 * u, 4 * u), y), (if (tocado) 1.1f else 0.8f) * u)
        val xm = 7.5f * u
        drawLine(rojo, Offset(xm, cy - 11 * u), Offset(xm, y + 2.5f * u), 0.7f * u)
        drawLine(rojo, Offset(xm - 2.5f * u, cy - 11 * u), Offset(xm - 2.5f * u, y + 2.5f * u), 0.7f * u)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel con la luz del momento, para los recuadros: claro de día; de noche, en penumbra. */
    private fun hoja(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.85f) else c
    /** El margen rojo de la ficha, de noche aclarado con papel. */
    private fun margenRojo(oscuro: Boolean): Color = if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO

    /** La mano que señala (☞, la manícula de los márgenes de los libros viejos), con el índice hacia la derecha, en [r]. */
    private fun DrawScope.manicula(r: Rect, color: Color, grosor: Float) {
        val w = r.width; val h = r.height
        fun p(x: Float, y: Float) = Offset(r.left + x * w, r.top + y * h)
        // El puño de la camisa, lleno.
        drawRect(color, p(0f, 0.3f), Size(w * 0.16f, h * 0.44f))
        // La mano cerrada y el índice extendido, a línea.
        drawRoundRect(color, p(0.2f, 0.3f), Size(w * 0.36f, h * 0.44f), CornerRadius(h * 0.14f), style = Stroke(grosor))
        drawRoundRect(color, p(0.5f, 0.3f), Size(w * 0.48f, h * 0.14f), CornerRadius(h * 0.07f), style = Stroke(grosor))
        // Los dedos doblados, debajo.
        drawLine(color, p(0.3f, 0.5f), p(0.53f, 0.5f), grosor * 0.8f, StrokeCap.Round)
        drawLine(color, p(0.3f, 0.62f), p(0.5f, 0.62f), grosor * 0.8f, StrokeCap.Round)
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: el renglón de un fichero: la raya azul de escribir abajo y, a la izquierda, el doble margen rojo de la ficha en dos
     * rayas cortas (más largas si la fila lleva detalle). Al tocarla, detrás aparece el resplandor verdoso, algo que no debería
     * estar ahí, y el margen se marca. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(VERDE_RARO.copy(alpha = if (oscuro) 0.12f else 0.24f), Offset.Zero, size)
        val raya = (if (oscuro) mezcla(RAYADO, Color.Black, 0.3f) else RAYADO).copy(alpha = if (oscuro) 0.4f else 0.5f)
        drawLine(raya, Offset(0f, h - 1 * u), Offset(w, h - 1 * u), 0.7f * u)
        val rojo = margenRojo(oscuro).copy(alpha = if (e.presionado) 0.9f else 0.5f)
        val medio = (if (e.conDetalle) 11f else 7f) * u
        listOf(4.5f, 7f).forEach { x -> drawLine(rojo, Offset(x * u, h / 2 - medio), Offset(x * u, h / 2 + medio), 0.7f * u) }
    }

    /** Flecha: la manícula, la mano de tinta que señala con el índice, como en los márgenes de los libros viejos. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val w = size.width; val h = size.height
        manicula(Rect(w * 0.04f, h * 0.18f, w * 0.98f, h * 0.86f), (if (oscuro) PAPEL else TINTA).copy(alpha = 0.82f), 0.9f * density)
    }

    /**
     * Marca de fijar: una tachuela de latón vista desde arriba, la misma que clava la ficha del campo. Clavada, la cabeza de
     * latón con su canto, su brillo y su sombra; sin clavar, solo el agujerito que dejaría y el cerco tenue de la cabeza. Sin
     * degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        if (fija) {
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 7.4f * k, Offset(c.x + 1 * k, c.y + 1.4f * k))
            drawCircle(if (oscuro) LATON else LATON, 7 * k, c)
            drawCircle(LATON_OSC, 7 * k, c, style = Stroke(1 * u))
            drawCircle(LATON_OSC.copy(alpha = 0.5f), 4.2f * k, c, style = Stroke(0.6f * u))
            drawCircle(Color.White.copy(alpha = 0.55f), 1.8f * k, Offset(c.x - 2.4f * k, c.y - 2.4f * k))
        } else {
            val linea = (if (oscuro) PAPEL else TINTA).copy(alpha = if (oscuro) 0.6f else 0.42f)
            drawCircle(linea, 7 * k, c, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.8f * u))))
            drawCircle(linea, 1 * k, c)
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, RAYADO, 0.25f) else Color(0xFF4A4436)

    /**
     * Vacío: una página en blanco de un libro viejo: el papel con las pecas del tiempo, el filete doble de imprenta que encuadra
     * una página que nunca se imprimió y, en una esquina, el cerco desvaído de una taza. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 24 * u) return
        drawRect(hoja(FICHA, oscuro), Offset.Zero, size)
        drawRect(hoja(PAPEL_VIEJO, oscuro).copy(alpha = 0.5f), Offset.Zero, Size(w, 2 * u))
        for (j in 0 until 10) {
            drawCircle(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.3f else 0.16f), (0.6f + 1f * azar(s, j + 20)) * u,
                Offset(w * azar(s, j), h * azar(s, j + 10)))
        }
        val tinta = (if (oscuro) PAPEL else TINTA).copy(alpha = if (oscuro) 0.22f else 0.3f)
        drawRect(tinta, Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), style = Stroke(1 * u))
        drawRect(tinta.copy(alpha = tinta.alpha * 0.7f), Offset(7.5f * u, 7.5f * u), Size(w - 15 * u, h - 15 * u), style = Stroke(0.5f * u))
        clipRect(0f, 0f, w, h) {
            val cc = Offset(w - 20 * u, h - 2 * u)
            drawCircle(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.35f else 0.2f), 14 * u, cc, style = Stroke(1.3f * u))
        }
    }

    /**
     * Lo vacío: un marco ovalado de retrato colgado de su clavo, con el cordón y el aro de latón con cuentas, y dentro nada:
     * la oscuridad, con un reflejo verdoso apenas y, si se mira bien, dos puntitos que no deberían estar ahí. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val laton = t(LATON, oscuro)
        val linea = if (oscuro) mezcla(LATON, PAPEL, 0.3f) else LATON_OSC
        // El clavo y el cordón.
        drawCircle(linea, f(0.03f), Offset(f(0.5f), f(0.05f)))
        drawLine(linea, Offset(f(0.5f), f(0.05f)), Offset(f(0.32f), f(0.22f)), 0.8f * u)
        drawLine(linea, Offset(f(0.5f), f(0.05f)), Offset(f(0.68f), f(0.22f)), 0.8f * u)
        // El aro y su hondo oscuro.
        val fuera = Rect(f(0.18f), f(0.16f), f(0.82f), f(0.96f))
        drawOval(laton, fuera.topLeft, fuera.size)
        drawOval(linea, fuera.topLeft, fuera.size, style = Stroke(0.8f * u))
        val dentro = Rect(f(0.26f), f(0.25f), f(0.74f), f(0.87f))
        drawOval(if (oscuro) Color(0xFF040605) else mezcla(TAPETE, TINTA, 0.55f), dentro.topLeft, dentro.size)
        drawOval(linea, dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
        // Las cuentas del aro.
        for (j in 0 until 12) {
            val a = j * Math.PI.toFloat() / 6
            drawCircle(linea, f(0.014f), Offset(f(0.5f) + f(0.28f) * kotlin.math.cos(a), f(0.56f) + f(0.36f) * kotlin.math.sin(a)))
        }
        // El reflejo verdoso y lo que mira desde dentro.
        drawOval(VERDE_RARO.copy(alpha = if (oscuro) 0.12f else 0.16f), Offset(f(0.31f), f(0.31f)), Size(f(0.22f), f(0.3f)))
        val ojo = VERDE_RARO.copy(alpha = if (oscuro) 0.55f else 0.45f)
        drawCircle(ojo, f(0.016f), Offset(f(0.44f), f(0.55f)))
        drawCircle(ojo, f(0.016f), Offset(f(0.56f), f(0.55f)))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA

    /**
     * Aviso: un recorte de periódico pegado, de papel amarillento con la esquina de arriba a la derecha cortada en diagonal; a la
     * izquierda, el doble margen rojo de arriba abajo y, a la altura del primer renglón, la marca que abre la nota según
     * [EstadoAviso.semilla]: el calderón (¶), la manícula (☞) o el asterismo (⁂). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val corte = minOf(8 * u, h / 3)
        val recorte = Path().apply { moveTo(0f, 0f); lineTo(w - corte, 0f); lineTo(w, corte); lineTo(w, h); lineTo(0f, h); close() }
        drawPath(recorte, hoja(PAPEL_VIEJO, oscuro))
        drawPath(recorte, (if (oscuro) PAPEL else TE).copy(alpha = if (oscuro) 0.14f else 0.35f), style = Stroke(0.7f * u))
        val rojo = margenRojo(oscuro).copy(alpha = 0.6f)
        drawLine(rojo, Offset(5 * u, 0f), Offset(5 * u, h), 0.7f * u)
        drawLine(rojo, Offset(7.6f * u, 0f), Offset(7.6f * u, h), 0.7f * u)
        val tinta = (if (oscuro) mezcla(VERDE_RARO, PAPEL, 0.35f) else TAPETE)
        val cx = 16.5f * u; val cy = 16.5f * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> {
                drawArc(tinta, 90f, 180f, true, Offset(cx - 3.6f * u, cy - 5 * u), Size(5.2f * u, 5.2f * u))
                drawLine(tinta, Offset(cx - 0.6f * u, cy - 5 * u), Offset(cx - 0.6f * u, cy + 5 * u), 1 * u)
                drawLine(tinta, Offset(cx + 1.6f * u, cy - 5 * u), Offset(cx + 1.6f * u, cy + 5 * u), 1 * u)
                drawLine(tinta, Offset(cx - 1 * u, cy - 4.6f * u), Offset(cx + 2.4f * u, cy - 4.6f * u), 0.9f * u)
            }
            1 -> manicula(Rect(cx - 6 * u, cy - 4.5f * u, cx + 6 * u, cy + 3.5f * u), tinta, 0.8f * u)
            else -> {
                asterisco(Offset(cx, cy - 2.4f * u), 1.9f * u, tinta, 0.7f * u)
                asterisco(Offset(cx - 3 * u, cy + 2.4f * u), 1.9f * u, tinta, 0.7f * u)
                asterisco(Offset(cx + 3 * u, cy + 2.4f * u), 1.9f * u, tinta, 0.7f * u)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6D58C) else TAPETE
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, RAYADO, 0.3f) else mezcla(TINTA, TE, 0.35f)

    /**
     * Cifra: el portaetiquetas de latón de un cajón del fichero de la biblioteca: el marco de latón con su canto, los dos
     * tornillos a los lados (de ranura) y, dentro, la tarjeta de papel con la raya azul abajo, donde va el número. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 16 * u || h < 16 * u) return
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(0.5f * u, 1.5f * u), Size(w - 1 * u, h - 1.5f * u), CornerRadius(2 * u))
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        val laton = t(LATON, oscuro)
        drawRoundRect(laton, r.topLeft, r.size, CornerRadius(2 * u))
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.1f else 0.4f), Color.Black.copy(alpha = 0.3f), 0.8f * u)
        val c = Rect(r.left + 3.4f * u, r.top + 3.2f * u, r.right - 3.4f * u, r.bottom - 3.2f * u)
        drawRect(hoja(FICHA, oscuro), c.topLeft, c.size)
        bisel(c, Color.Black.copy(alpha = 0.3f), Color.White.copy(alpha = if (oscuro) 0.04f else 0.3f), 0.8f * u)
        drawLine((if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = 0.55f), Offset(c.left + 2 * u, c.bottom - 3 * u), Offset(c.right - 2 * u, c.bottom - 3 * u), 0.6f * u)
        listOf(r.left + 1.7f * u, r.right - 1.7f * u).forEach { x ->
            val o = Offset(x, r.center.y)
            drawCircle(t(LATON_OSC, oscuro), 1.3f * u, o)
            drawLine(t(LATON, oscuro), Offset(o.x, o.y - 1 * u), Offset(o.x, o.y + 1 * u), 0.5f * u)
        }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El papel amarillo de los telegramas. */
    private val TELEGRAMA = Color(0xFFF1E6BE)

    /** El fondo liso de cada tarjeta, donde va el texto: la etiqueta del espécimen, el telegrama, la noche y el bolsillo de manila. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> hoja(FICHA, oscuro)
        TipoTarjeta.PENDIENTE -> hoja(if (oscuro) PAPEL_VIEJO else TELEGRAMA, oscuro)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0E1513) else PAPEL
        TipoTarjeta.VIDA -> if (oscuro) mezcla(TE, Color(0xFF0A0806), 0.8f) else mezcla(TE, PAPEL, 0.7f)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color.White, 0.2f) else TINTA
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        oscuro -> mezcla(PAPEL, RAYADO, 0.3f)
        tipo == TipoTarjeta.VIDA -> mezcla(TINTA, TE, 0.2f)
        else -> mezcla(TINTA, TE, 0.35f)
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA, TipoTarjeta.NOCHE -> if (oscuro) VERDE_RARO else TAPETE
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.45f) else mezcla(MARGEN_ROJO, TINTA, 0.35f)
        TipoTarjeta.VIDA -> if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.45f) else mezcla(MARGEN_ROJO, TINTA, 0.45f)
    }

    /**
     * Tarjetas: cuatro papeles de la biblioteca. La cifra de Hoy es la etiqueta de un espécimen del museo: la cartulina con su
     * filete negro grueso y otro fino por dentro, atravesada arriba a la izquierda por el alfiler de entomólogo. «Te falta hoy»
     * es un telegrama: el papel amarillo con las rayas de imprenta arriba y abajo y, arriba a la derecha, el sello de goma rojo,
     * torcido (dos cercos y su banda). La noche es la hoja vista junto a la ventana: arriba a la derecha, la luna entre dos
     * jirones de nube y una estrella verdosa que no debería estar ahí, con un resplandor apenas. La tarjeta de Vida es el
     * bolsillo de manila de un libro de préstamo, con la ficha asomando por arriba (su raya azul y su margen rojo) y la muesca
     * para el pulgar. Al tocarlas se oscurecen apenas y la ficha del bolsillo se hunde. Destacadas: la etiqueta se rodea del
     * resplandor verdoso y el alfiler se vuelve verde; el telegrama completo lleva el sello verde con su visto bueno; la noche
     * sellada, la luna eclipsada, negra, con su corona verde. Un degradado: el resplandor de la estrella.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> etiquetaEspecimen(e, oscuro)
            TipoTarjeta.PENDIENTE -> telegrama(e, oscuro)
            TipoTarjeta.NOCHE -> ventanaNoche(e, oscuro)
            TipoTarjeta.VIDA -> bolsilloPrestamo(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, TE, 0.14f)
    }

    /** La tinta de los filetes y las rayas, con la luz del momento. */
    private fun tinta(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF0A0806), 0.45f) else TINTA

    private fun DrawScope.etiquetaEspecimen(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 2.5f * u)
        if (e.destacado) {
            // El resplandor verdoso, en capas que se apagan hacia afuera.
            for (k in 3 downTo 1) drawRect(VERDE_RARO.copy(alpha = (if (oscuro) 0.14f else 0.2f) / k), r.inflate(k * 0.5f * u).topLeft, r.inflate(k * 0.5f * u).size, style = Stroke(k * 1f * u))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), r.topLeft + Offset(0.6f * u, (if (e.presionado) 0.6f else 1.4f) * u), r.size)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        // Los dos filetes de imprenta.
        val negro = tinta(oscuro)
        val g = r.deflate(2.6f * u)
        drawRect(negro, g.topLeft, g.size, style = Stroke(1.5f * u))
        val f = g.deflate(2 * u)
        drawRect(negro.copy(alpha = 0.8f), f.topLeft, f.size, style = Stroke(0.5f * u))
        // El alfiler, arriba a la izquierda: su sombra, el vástago que se asoma y la cabeza.
        val c = Offset(r.left + 5.5f * u, r.top + 5.5f * u)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), c + Offset(1f * u, 1.2f * u), c + Offset(6f * u, 5.6f * u), 1.1f * u, StrokeCap.Round)
        drawLine(t(LATON_OSC, oscuro), c, c + Offset(-1.2f * u, -1.2f * u), 0.6f * u)
        val cabeza = if (e.destacado) VERDE_RARO else (if (oscuro) mezcla(TINTA, PAPEL, 0.25f) else TINTA)
        drawCircle(cabeza, 2.6f * u, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.3f else 0.6f), 0.8f * u, c + Offset(-0.8f * u, -0.8f * u))
    }

    private fun DrawScope.telegrama(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val completo = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0.6f * u, 1.6f * u), Size(w - 0.6f * u, h - 1.6f * u))
        val r = Rect(0f, 0f, w - 0.6f * u, h - 1.6f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        // Las rayas de imprenta: gruesa y fina arriba; fina y gruesa abajo.
        val negro = tinta(oscuro)
        drawRect(negro, Offset(4 * u, 3.2f * u), Size(r.width - 8 * u, 1.8f * u))
        drawRect(negro, Offset(4 * u, 6.2f * u), Size(r.width - 8 * u, 0.5f * u))
        drawRect(negro, Offset(4 * u, r.bottom - 6 * u), Size(r.width - 8 * u, 0.5f * u))
        drawRect(negro, Offset(4 * u, r.bottom - 4.6f * u), Size(r.width - 8 * u, 1.8f * u))
        // La tira pegada del texto, a la izquierda de la raya de arriba, como en los telegramas.
        drawRect(negro.copy(alpha = 0.12f), Offset(4 * u, 8 * u), Size(minOf(56 * u, r.width * 0.25f), 1 * u))
        // El sello de goma, torcido, arriba a la derecha.
        val rs = minOf(13 * u, (r.height - 8 * u) / 2)
        val c = Offset(r.right - rs - 10 * u, if (completo) r.height / 2 else rs + 5 * u)
        val tinte = if (completo) (if (oscuro) mezcla(TAPETE, VERDE_RARO, 0.5f) else TAPETE) else margenRojo(oscuro)
        val sello = tinte.copy(alpha = 0.8f)
        rotate(-14f, c) {
            drawCircle(sello, rs, c, style = Stroke(1.3f * u))
            drawCircle(sello, rs * 0.78f, c, style = Stroke(0.6f * u))
            drawRect(fondoTocado(e.tipo, e.presionado, oscuro), Offset(c.x - rs * 1.15f, c.y - rs * 0.28f), Size(rs * 2.3f, rs * 0.56f))
            drawRect(sello, Offset(c.x - rs * 1.15f, c.y - rs * 0.28f), Size(rs * 2.3f, rs * 0.56f), style = Stroke(1 * u))
            if (completo) drawPath(Path().apply {
                moveTo(c.x - rs * 0.45f, c.y); lineTo(c.x - rs * 0.1f, c.y + rs * 0.2f); lineTo(c.x + rs * 0.5f, c.y - rs * 0.2f)
            }, sello, style = Stroke(1.4f * u, cap = StrokeCap.Round))
            else for (j in 0 until 3) drawLine(sello, Offset(c.x - rs * (0.8f - j * 0.6f), c.y), Offset(c.x - rs * (0.45f - j * 0.6f), c.y), 1.2f * u)
            // Las marcas del cerco, como letras borrosas.
            for (j in 0 until 10) {
                val a = Math.toRadians(200.0 + j * 14.0)
                val p = Offset(c.x + rs * 0.89f * kotlin.math.cos(a).toFloat(), c.y + rs * 0.89f * kotlin.math.sin(a).toFloat())
                drawCircle(sello, 0.45f * u, p)
            }
        }
    }

    private fun DrawScope.ventanaNoche(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(0.6f * u, 1.5f * u), Size(w - 0.6f * u, h - 1.5f * u))
        val r = Rect(0f, 0f, w - 0.6f * u, h - 1.5f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        val negro = tinta(oscuro)
        // La raya azul de escribir, abajo, y el doble margen rojo a la izquierda, cortos.
        drawLine((if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = 0.6f), Offset(4 * u, r.bottom - 3.5f * u), Offset(r.right - 4 * u, r.bottom - 3.5f * u), 0.7f * u)
        // Arriba a la derecha: la estrella verde con su resplandor, la luna y los jirones de nube.
        val c = Offset(r.right - 22 * u, minOf(13 * u, h * 0.16f))
        val rl = 7 * u
        val estrella = Offset(c.x - 15 * u, c.y - 4 * u)
        mancha(estrella, 10 * u, VERDE_RARO.copy(alpha = if (oscuro) 0.45f else 0.35f))
        asterisco(estrella, 1.8f * u, if (oscuro) VERDE_RARO else mezcla(VERDE_RARO, TAPETE, 0.5f), 0.6f * u)
        asterisco(Offset(c.x - 26 * u, c.y + 2 * u), 1.2f * u, negro.copy(alpha = 0.5f), 0.5f * u)
        asterisco(Offset(c.x + 12 * u, c.y + 5 * u), 1 * u, negro.copy(alpha = 0.45f), 0.5f * u)
        if (sellado) {
            // El eclipse: el disco negro con su corona verdosa.
            drawCircle(VERDE_RARO.copy(alpha = if (oscuro) 0.35f else 0.5f), rl * 1.3f, c, style = Stroke(1.6f * u))
            drawCircle(VERDE_RARO.copy(alpha = 0.8f), rl * 1.05f, c, style = Stroke(0.8f * u))
            drawCircle(if (oscuro) Color.Black else TINTA, rl, c)
        } else {
            val luna = if (oscuro) mezcla(PAPEL, Color.White, 0.3f) else mezcla(PAPEL_VIEJO, TE, 0.35f)
            drawCircle(luna, rl, c)
            drawCircle(negro.copy(alpha = 0.15f), rl * 0.3f, c + Offset(rl * 0.3f, -rl * 0.2f))
            drawCircle(negro.copy(alpha = 0.12f), rl * 0.2f, c + Offset(-rl * 0.35f, rl * 0.35f))
            drawCircle(negro.copy(alpha = 0.5f), rl, c, style = Stroke(0.6f * u))
        }
        // Dos jirones de nube que cruzan el disco, en el color del papel.
        val papel = fondoTocado(e.tipo, e.presionado, oscuro)
        for ((dy, l) in listOf(2.2f to 1f, 4.6f to 0.7f)) {
            drawLine(papel, Offset(c.x - rl * 1.4f * l, c.y + dy * u), Offset(c.x + rl * 1.6f * l, c.y + dy * u), 1.4f * u, StrokeCap.Round)
            drawLine(negro.copy(alpha = 0.35f), Offset(c.x - rl * 1.2f * l, c.y + (dy + 0.8f) * u), Offset(c.x + rl * 1.4f * l, c.y + (dy + 0.8f) * u), 0.5f * u, StrokeCap.Round)
        }
    }

    private fun DrawScope.bolsilloPrestamo(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val boca = 9 * u
        // La ficha que asoma: se hunde un poco al tocarla.
        val fichaTop = if (e.presionado) 2.2f * u else 0.5f * u
        val ficha = Rect(6 * u, fichaTop, w - 6 * u, boca + 10 * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.12f), ficha.topLeft + Offset(0.5f * u, 0.6f * u), ficha.size)
        drawRect(hoja(FICHA, oscuro), ficha.topLeft, ficha.size)
        drawLine((if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = 0.8f), Offset(ficha.left, fichaTop + 3.2f * u), Offset(ficha.right, fichaTop + 3.2f * u), 0.6f * u)
        val rojo = margenRojo(oscuro).copy(alpha = 0.8f)
        for (dx in listOf(9f, 10.4f)) drawLine(rojo, Offset(ficha.left + dx * u, fichaTop), Offset(ficha.left + dx * u, boca), 0.5f * u)
        // El bolsillo, con su muesca de media luna para el pulgar.
        val muesca = minOf(6 * u, w * 0.06f)
        val bolsillo = Path().apply {
            moveTo(0.5f * u, boca)
            lineTo(w / 2 - muesca, boca)
            arcTo(Rect(w / 2 - muesca, boca - muesca, w / 2 + muesca, boca + muesca), 180f, -180f, false)
            lineTo(w - 0.5f * u, boca)
            lineTo(w - 0.5f * u, h - 1.6f * u)
            lineTo(0.5f * u, h - 1.6f * u)
            close()
        }
        translate(0f, 1.2f * u) { drawPath(bolsillo, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f)) }
        drawPath(bolsillo, fondoTocado(e.tipo, e.presionado, oscuro))
        val borde = if (oscuro) mezcla(TE, PAPEL, 0.2f).copy(alpha = 0.5f) else mezcla(TE, TINTA, 0.4f).copy(alpha = 0.55f)
        drawPath(bolsillo, borde, style = Stroke(0.7f * u))
        // El filete impreso del bolsillo y las solapas pegadas, a los lados.
        val tinta = tinta(oscuro).copy(alpha = if (oscuro) 0.4f else 0.45f)
        drawRect(tinta, Offset(3.5f * u, h - 5.5f * u), Size(w - 7 * u, 0.6f * u))
        drawLine(borde, Offset(2.5f * u, boca + 1 * u), Offset(2.5f * u, h - 2.4f * u), 0.5f * u)
        drawLine(borde, Offset(w - 2.5f * u, boca + 1 * u), Offset(w - 2.5f * u, h - 2.4f * u), 0.5f * u)
        if (e.destacado) drawPath(bolsillo, VERDE_RARO.copy(alpha = 0.7f), style = Stroke(1.4f * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** El verde de la cuenta saldada: el de los ingresos. */
    private val VERDE_LIBRO = Color(0xFF2D5446)

    override val altoBalanza: Dp = 60.dp

    /**
     * Balanza: dos fichas del catálogo, una junto a otra, con su doble margen rojo y su raya azul. La elegida queda delante,
     * entera y clara, un poco más grande, clavada con una tachuela de latón en su esquina de afuera y con su sombra; alrededor,
     * apenas, el resplandor verdoso que no debería estar ahí (unos cercos cada vez más tenues). La otra queda atrás, más chica y
     * amarillenta, con el texto a máquina sin remarcar. Sin degradados.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 30 * u) return
        val xm = w / 2
        val atras = if (e.ingreso) 0 else 1
        val tinta = tinta(oscuro)
        // Primero la de atrás, luego la de delante, que la tapa un poco en el medio.
        for (lado in listOf(atras, 1 - atras)) {
            val elegida = lado != atras
            val r = when {
                lado == 0 && elegida -> Rect(1.5f * u, 1.5f * u, xm + 6 * u, h - 3 * u)
                lado == 0 -> Rect(4 * u, 6 * u, xm + 2 * u, h - 5 * u)
                elegida -> Rect(xm - 6 * u, 1.5f * u, w - 1.5f * u, h - 3 * u)
                else -> Rect(xm - 2 * u, 6 * u, w - 4 * u, h - 5 * u)
            }
            val fondo = if (elegida) hoja(FICHA, oscuro) else if (oscuro) mezcla(PAPEL_VIEJO, Color(0xFF0A0806), 0.88f) else PAPEL_VIEJO
            if (elegida) {
                clipRect {
                    for (j in 1..4) {
                        val halo = r.inflate(j * 1.6f * u)
                        drawRoundRect(VERDE_RARO.copy(alpha = (if (oscuro) 0.11f else 0.16f) * (5 - j) / 4f), halo.topLeft, halo.size,
                            CornerRadius(j * 1.6f * u), style = Stroke(1.7f * u))
                    }
                }
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), r.topLeft + Offset(1.5f * u, 2 * u), r.size)
            }
            drawRect(fondo, r.topLeft, r.size)
            drawRect((if (oscuro) PAPEL else TE).copy(alpha = if (oscuro) 0.25f else 0.4f), r.topLeft, r.size, style = Stroke(0.8f * u))
            // El doble margen rojo, del lado de afuera, y la raya azul de escribir abajo.
            val rojo = margenRojo(oscuro).copy(alpha = if (elegida) 0.85f else 0.4f)
            val xr = if (lado == 0) r.left + 11 * u else r.right - 11 * u
            drawLine(rojo, Offset(xr, r.top), Offset(xr, r.bottom), 0.8f * u)
            drawLine(rojo, Offset(xr + (if (lado == 0) 2.2f else -2.2f) * u, r.top), Offset(xr + (if (lado == 0) 2.2f else -2.2f) * u, r.bottom), 0.8f * u)
            drawLine(RAYADO.copy(alpha = if (elegida) 0.8f else 0.4f), Offset(r.left, r.bottom - 7 * u), Offset(r.right, r.bottom - 7 * u), 0.8f * u)
            if (elegida) {
                val c = Offset(if (lado == 0) r.left + 5.5f * u else r.right - 5.5f * u, r.top + 6 * u)
                drawCircle(Color.Black.copy(alpha = 0.3f), 3 * u, c + Offset(0.6f * u, 0.8f * u))
                drawCircle(LATON, 2.8f * u, c)
                drawCircle(LATON_OSC, 2.8f * u, c, style = Stroke(0.6f * u))
                drawCircle(Color.White.copy(alpha = 0.55f), 0.9f * u, c - Offset(0.9f * u, 0.9f * u))
            }
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val caja = if (lado == 0) Rect(r.left + 18 * u, r.top + 3 * u, r.right - 10 * u, r.bottom - 9 * u)
            else Rect(r.left + 10 * u, r.top + 3 * u, r.right - 18 * u, r.bottom - 9 * u)
            if (elegida) textoEn(medidor, texto, caja, LetraExtra.maquina, legibleAnt(base, 5f, fondo), 18f, peso = FontWeight.Bold)
            else textoEn(medidor, texto, caja, LetraExtra.maquina, legibleAnt(mezcla(base, fondo, 0.2f), 4.7f, fondo), 15f)
            if (!elegida) drawLine(tinta.copy(alpha = 0.08f), Offset(r.left, r.top), Offset(r.right, r.top), 1 * u)
        }
    }

    override val altoMes: Dp = 52.dp

    /**
     * Encabezado del mes: la cabecera de una página del libro mayor: una tira de papel con el filete doble de imprenta arriba,
     * el sencillo abajo, y el mes al centro entre dos asterismos; en los extremos, las manículas de las filas, grandes, que
     * señalan hacia atrás y hacia adelante. Sin degradados.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 24 * u) return
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        val fondo = hoja(PAPEL, oscuro)
        drawRect(fondo, r.topLeft, r.size)
        val tinta = tinta(oscuro)
        drawLine(tinta.copy(alpha = 0.85f), Offset(r.left + 4 * u, r.top + 3 * u), Offset(r.right - 4 * u, r.top + 3 * u), 1.6f * u)
        drawLine(tinta.copy(alpha = 0.85f), Offset(r.left + 4 * u, r.top + 6 * u), Offset(r.right - 4 * u, r.top + 6 * u), 0.6f * u)
        drawLine(tinta.copy(alpha = 0.6f), Offset(r.left + 4 * u, r.bottom - 3.5f * u), Offset(r.right - 4 * u, r.bottom - 3.5f * u), 0.7f * u)
        // Las pecas del tiempo.
        for (j in 0 until 14) drawCircle(TE.copy(alpha = if (oscuro) 0.14f else 0.22f), (0.5f + 0.8f * azar(71, j)) * u,
            Offset(r.left + r.width * azar(72, j), r.top + 8 * u + (r.height - 14 * u) * azar(73, j)))
        val texto = Rect(62 * u, r.top + 7 * u, w - 62 * u, r.bottom - 4 * u)
        val ast = (if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.3f) else MARGEN_ROJO)
        asterisco(Offset(texto.left, texto.center.y), 3 * u, ast, 0.9f * u)
        asterisco(Offset(texto.right, texto.center.y), 3 * u, ast, 0.9f * u)
        textoEn(medidor, e.titulo, Rect(texto.left + 8 * u, texto.top, texto.right - 8 * u, texto.bottom), Letra.castoro,
            legibleAnt(tinta, 7f, fondo), 23f, espaciado = 0.4f)
        inset(0f, 3 * u, 0f, 0f) { flechasMesAnt(48 * u) { flechaFila(oscuro) } }
    }

    /**
     * Asiento: el renglón de un fichero: la raya azul de escribir abajo, tenue, y al principio dos rayitas del color del tipo,
     * como el doble margen: rojas si es un gasto, del verde de la cuenta saldada si es un ingreso. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.4f * u
        drawLine(RAYADO.copy(alpha = if (oscuro) 0.3f else 0.5f), Offset(10 * u, y), Offset(w - 2 * u, y), 0.8f * u)
        val c = if (e.ingreso) (if (oscuro) mezcla(VERDE_LIBRO, VERDE_RARO, 0.55f) else VERDE_LIBRO) else margenRojo(oscuro)
        for (j in 0..1) drawLine(c.copy(alpha = 0.8f), Offset((3 + j * 2.6f) * u, y + 0.5f * u), Offset((3 + j * 2.6f) * u, y - 7 * u), 0.9f * u)
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * Rótulo del día: la pestaña de una ficha separadora del fichero, con la fecha escrita a máquina, y de ahí hacia la derecha el
     * canto de la ficha con su raya azul, que acaba en un asterisco rojo. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 10 * u) return
        val tam = 13f
        val ancho = minOf(anchoTextoAnt(medidor, texto, LetraExtra.maquina, tam, FontWeight.Bold, 1f) + 2 * u, w * 0.6f)
        val fondo = hoja(FICHA, oscuro)
        val borde = (if (oscuro) PAPEL else TE).copy(alpha = if (oscuro) 0.35f else 0.6f)
        val pestana = Path().apply {
            moveTo(1 * u, h - 1 * u); lineTo(1 * u, 4 * u); quadraticTo(1 * u, 1 * u, 4 * u, 1 * u)
            lineTo(ancho + 14 * u, 1 * u); quadraticTo(ancho + 18 * u, 1 * u, ancho + 19 * u, 4 * u); lineTo(ancho + 21 * u, h - 1 * u); close()
        }
        drawPath(pestana, fondo)
        drawPath(pestana, borde, style = Stroke(0.8f * u))
        textoEn(medidor, texto, Rect(8 * u, 1 * u, 8 * u + ancho, h - 1 * u), LetraExtra.maquina,
            legibleAnt(tinta(oscuro), 7f, fondo), tam, peso = FontWeight.Bold, espaciado = 1f)
        val x0 = ancho + 21 * u; val x1 = w - 12 * u
        if (x1 > x0) {
            drawLine(borde, Offset(x0, h - 1 * u), Offset(x1, h - 1 * u), 0.8f * u)
            drawLine(RAYADO.copy(alpha = if (oscuro) 0.4f else 0.6f), Offset(x0 + 4 * u, h / 2 + 1 * u), Offset(x1, h / 2 + 1 * u), 0.8f * u)
        }
        asterisco(Offset(w - 6 * u, h / 2 + 1 * u), 3 * u, if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.3f) else MARGEN_ROJO, 0.9f * u)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /** Un trozo de cinta engomada con centro en [c], de [largo] × [ancho], girado [giro] grados, con las dos puntas rasgadas en dientes. */
    private fun DrawScope.cintaEngomada(c: Offset, largo: Float, ancho: Float, giro: Float, color: Color) {
        val x0 = c.x - largo / 2; val x1 = c.x + largo / 2; val y0 = c.y - ancho / 2
        val dientes = 4; val d = ancho / dientes
        val p = Path().apply {
            moveTo(x0, y0); lineTo(x1, y0)
            for (j in 1..dientes) lineTo(x1 + if (j % 2 == 1) -d * 0.5f else 0f, y0 + j * d)
            lineTo(x0, y0 + ancho)
            for (j in dientes - 1 downTo 0) lineTo(x0 + if (j % 2 == 1) d * 0.5f else 0f, y0 + j * d)
            close()
        }
        rotate(giro, c) {
            drawPath(p, color)
            drawPath(p, color.copy(alpha = color.alpha * 0.8f), style = Stroke(0.6f * density))
        }
    }

    /** El ojo que no debería estar ahí: la almendra, el iris y la pupila en ranura, con centro en [c] y [ancho] de punta a punta. */
    private fun DrawScope.ojoArcano(c: Offset, ancho: Float, linea: Color, pupila: Color, u: Float) {
        val a = ancho / 2
        drawPath(Path().apply {
            moveTo(c.x - a, c.y); quadraticTo(c.x, c.y - a * 0.8f, c.x + a, c.y); quadraticTo(c.x, c.y + a * 0.8f, c.x - a, c.y); close()
        }, linea, style = Stroke(1f * u))
        drawCircle(linea, a * 0.36f, c, style = Stroke(0.8f * u))
        drawOval(pupila, Offset(c.x - a * 0.12f, c.y - a * 0.34f), Size(a * 0.24f, a * 0.68f))
    }

    /**
     * Hoja rápida: la hoja arrancada de un legajo. Por el canto izquierdo, el doble margen rojo de la ficha, de arriba abajo; en
     * las dos esquinas de arriba, un trozo de cinta engomada que la sujeta; al centro, bajo la orilla rasgada, un ojo con su
     * pupila en ranura y el resplandor verdoso, algo que no debería estar ahí, y a sus lados dos filetes de rúbrica; abajo a la
     * izquierda, la manícula que señala, y en la esquina de abajo a la derecha, el cerco desvaído de una taza de té. Un degradado:
     * el resplandor.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        // El doble margen rojo.
        val rojo = margenRojo(oscuro)
        drawLine(alfaAnt(rojo, if (oscuro) 0.6f else 0.45f), Offset(12 * u, 6 * u), Offset(12 * u, h), 0.9f * u)
        drawLine(alfaAnt(rojo, if (oscuro) 0.42f else 0.3f), Offset(14.5f * u, 6 * u), Offset(14.5f * u, h), 0.7f * u)
        // El ojo y su resplandor.
        val c = Offset(w / 2, 13.5f * u)
        drawCircle(Brush.radialGradient(listOf(VERDE_RARO.copy(alpha = if (oscuro) 0.3f else 0.4f), Color.Transparent), c, 17 * u), 17 * u, c)
        ojoArcano(c, 15 * u, alfaAnt(e.primario, 0.7f), alfaAnt(e.secundario, 0.9f), u)
        val rubrica = alfaAnt(e.primario, 0.3f)
        drawLine(rubrica, Offset(42 * u, 15 * u), Offset(c.x - 13 * u, 13.5f * u), 0.8f * u)
        drawLine(rubrica, Offset(46 * u, 17.5f * u), Offset(c.x - 16 * u, 16.5f * u), 0.6f * u)
        drawLine(rubrica, Offset(c.x + 13 * u, 13.5f * u), Offset(w - 42 * u, 14.5f * u), 0.8f * u)
        drawLine(rubrica, Offset(c.x + 16 * u, 16.5f * u), Offset(w - 46 * u, 17 * u), 0.6f * u)
        asterisco(Offset(c.x - 21 * u, 18 * u), 1.6f * u, alfaAnt(e.primario, 0.45f), 0.6f * u)
        asterisco(Offset(c.x + 21 * u, 18 * u), 1.6f * u, alfaAnt(e.primario, 0.45f), 0.6f * u)
        // Las cintas de las esquinas (encima del margen).
        val cinta = alfaAnt(e.secundario, if (oscuro) 0.22f else 0.2f)
        cintaEngomada(Offset(20 * u, 13 * u), 32 * u, 9 * u, -28f, cinta)
        cintaEngomada(Offset(w - 20 * u, 13 * u), 32 * u, 9 * u, 28f, cinta)
        // La manícula abajo, en el aire que deja el contenido.
        manicula(Rect(22 * u, h - 16 * u, 46 * u, h - 4 * u), alfaAnt(e.primario, 0.55f), 0.9f * u)
        // El cerco de té en la esquina de abajo a la derecha.
        val te = t(TE, oscuro)
        val ct = Offset(w - 9 * u, h - 11 * u)
        drawCircle(alfaAnt(te, if (oscuro) 0.3f else 0.22f), 12 * u, ct, style = Stroke(1.6f * u))
        drawArc(alfaAnt(te, 0.14f), 200f, 210f, false, Offset(ct.x - 10.5f * u, ct.y - 10.5f * u), Size(21 * u, 21 * u), style = Stroke(2.4f * u))
    }

    /**
     * Relación: una ficha del catálogo. A la izquierda, el doble margen rojo; abajo, la raya azul de escribir; arriba, una tachuela
     * de latón con su sombra; en la esquina cortada, el doblez; abajo a la derecha, el cerco desvaído de una taza. El centro, liso.
     * Sin degradados.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val rojo = margenRojo(oscuro)
        drawLine(alfaAnt(rojo, 0.45f), Offset(6.5f * u, 2 * u), Offset(6.5f * u, h - 2 * u), 0.8f * u)
        drawLine(alfaAnt(rojo, 0.3f), Offset(8.5f * u, 2 * u), Offset(8.5f * u, h - 2 * u), 0.6f * u)
        drawLine(alfaAnt(t(RAYADO, oscuro), 0.45f), Offset(3 * u, h - 6.5f * u), Offset(w - 3 * u, h - 6.5f * u), 0.7f * u)
        val o = Offset(w / 2, 5 * u)
        drawCircle(Color.Black.copy(alpha = 0.18f), 2.5f * u, o + Offset(0.8f * u, 1f * u))
        drawCircle(t(LATON, oscuro), 2.3f * u, o)
        drawCircle(t(LATON_OSC, oscuro), 2.3f * u, o, style = Stroke(0.5f * u))
        drawLine(alfaAnt(e.primario, 0.4f), Offset(w - 21 * u, 1.5f * u), Offset(w - 1.5f * u, 21 * u), 0.8f * u)
        drawCircle(alfaAnt(t(TE, oscuro), 0.2f), 8 * u, Offset(w - 12 * u, h - 12 * u), style = Stroke(1.2f * u))
    }

    /** Pista: el signo de siete lados que no cierra, con un punto en cada vértice. Sin degradados. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f
        val cx = size.width / 2; val cy = (size.height - l) / 2 + l * 0.52f
        val vertices = (0..6).map { k ->
            val a = Math.toRadians(-90.0 + k * 360.0 / 7)
            Offset(cx + l * 0.45f * kotlin.math.cos(a).toFloat(), cy + l * 0.45f * kotlin.math.sin(a).toFloat())
        }
        drawPath(Path().apply { vertices.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }, e.primario, style = Stroke(g, cap = StrokeCap.Round))
        vertices.forEach { drawCircle(e.secundario, l * 0.036f, it) }
    }
}

/**
 * Tierras Antiguas: el campo es un marco de madera tallada, con sus vetas y una hilera de muescas arriba y abajo, que sujeta una
 * hoja de pergamino de borde irregular, lisa; en las cuatro esquinas, cantoneras de latón remachadas, como las de los tomos.
 * Con el foco las muescas toman la luz de la vela y el pergamino se ribetea de oro; con un error, de rojo de mapa. Con varios
 * renglones, rayas tenues de pauta. Sin degradados.
 */
object ComunAntiguas : DisenoComun {
    private val MADERA = Color(0xFF7A5232)
    private val NUDO = Color(0xFF3A240F)
    private val TALLA = Color(0xFF5E3E22)
    private val PERGAMINO = Color(0xFFE6D3A6)
    private val PERGAMINO_OSC = Color(0xFFB89C68)
    private val TINTA = Color(0xFF3A2614)
    private val LATON = Color(0xFFC9A04A)
    private val LATON_OSC = Color(0xFF7E6024)
    private val LATON_LUZ = Color(0xFFE8CC7E)
    private val LLAMA = Color(0xFFF4C66A)
    /** El latón viejo del rótulo, oscuro para que se lea sobre el pergamino. */
    private val LATON_VIEJO = Color(0xFF654A16)
    private val ROJO_MAPA = Color(0xFF9A2A1E)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0906), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0906), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 58.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 12.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(PERGAMINO, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) LATON_LUZ else LATON_VIEJO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO_MAPA

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val perg = hondo(PERGAMINO, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, perg, 0.45f)
        val encendido = e.enfocado && e.habilitado
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        val radio = CornerRadius(4 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, radio)
        // La madera tallada.
        drawRoundRect(a(t(MADERA, oscuro)), r.topLeft, r.size, radio)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, a(t(NUDO, oscuro)).copy(alpha = 0.45f), s, 0.7f) }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), Color.Black.copy(alpha = 0.3f), 1.5f * u)
        // La hoja de pergamino, de borde irregular, con su sombra de asiento.
        val p = Rect(r.left + 6 * u, r.top + 6 * u, r.right - 6 * u, r.bottom - 6 * u)
        val puntos = ((p.width + p.height) * 2 / (6 * u)).toInt().coerceIn(20, 240)
        val hoja = contornoIrregular(p, s + 17, puntos, 0.7f * u / minOf(p.width, p.height).coerceAtLeast(1f))
        drawPath(hoja, perg)
        drawPath(hoja, a(t(NUDO, oscuro)).copy(alpha = 0.4f), style = Stroke(1 * u))
        if (e.multilinea) renglonesAntiguedad(p.left + 8 * u, p.right - 8 * u, margenCampoY.toPx(), p.bottom - 4 * u,
            hondo(PERGAMINO_OSC, oscuro).copy(alpha = if (oscuro) 0.7f else 0.45f), 0.7f * u)
        // Las muescas de la talla, arriba y abajo de la hoja.
        val muesca = if (encendido) (if (oscuro) LLAMA else LATON_LUZ) else a(t(TALLA, oscuro))
        val pm = maxOf(8 * u, 1f)
        var x = p.left + 8 * u; var k = 0
        while (x < p.right - 10 * u && k < 300) {
            drawLine(muesca, Offset(x, r.top + 2 * u), Offset(x + 2 * u, r.top + 4.4f * u), 1.1f * u, StrokeCap.Round)
            drawLine(muesca, Offset(x, r.bottom - 2 * u), Offset(x + 2 * u, r.bottom - 4.4f * u), 1.1f * u, StrokeCap.Round)
            x += pm; k++
        }
        // El ribete del pergamino: oro con el foco, rojo de mapa con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) LLAMA else LATON_OSC
            else -> null
        }
        if (linea != null) drawPath(hoja, linea, style = Stroke(1.8f * u))
        // Las cantoneras de latón remachadas en las cuatro esquinas.
        val marco = Path().apply { addRoundRect(RoundRect(r, radio)) }
        val lado = minOf(13 * u, r.height / 3)
        val laton = a(t(if (encendido) LATON_LUZ else LATON, oscuro))
        clipPath(marco) {
            listOf(Triple(r.left, r.top, 1f to 1f), Triple(r.right, r.top, -1f to 1f), Triple(r.left, r.bottom, 1f to -1f), Triple(r.right, r.bottom, -1f to -1f))
                .forEach { (cx, cy, d) ->
                    val (sx, sy) = d
                    val pieza = Path().apply {
                        moveTo(cx, cy); lineTo(cx + sx * lado, cy)
                        quadraticTo(cx + sx * lado * 0.55f, cy + sy * lado * 0.55f, cx, cy + sy * lado); close()
                    }
                    drawPath(pieza, laton)
                    drawPath(pieza, a(t(LATON_OSC, oscuro)), style = Stroke(0.7f * u))
                    val o = Offset(cx + sx * 3.6f * u, cy + sy * 3.6f * u)
                    drawCircle(a(t(LATON_OSC, oscuro)), 1.1f * u, o)
                    drawCircle(Color.White.copy(alpha = 0.45f), 0.45f * u, Offset(o.x - 0.3f * u, o.y - 0.3f * u))
                }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * Separador: una raya de tinta de mapa, rematada en un punto en cada punta, y al centro una rosa de los vientos pequeña de
     * latón (una estrella de cuatro puntas sobre otra girada), entre dos puntos. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) PERGAMINO.copy(alpha = 0.22f) else TINTA.copy(alpha = 0.38f)).let { it.copy(alpha = it.alpha * f) }
        val laton = (if (oscuro) LATON else LATON_OSC).copy(alpha = (if (oscuro) 0.75f else 0.85f) * f)
        val laton2 = (if (oscuro) mezcla(LATON, Color.Black, 0.4f) else LATON).copy(alpha = (if (oscuro) 0.6f else 0.7f) * f)
        val c = Offset(w / 2, cy)
        drawPath(estrella(c, 3.2f * u, 4, 0.32f, -45f), laton2)
        drawPath(estrella(c, 4.8f * u, 4, 0.26f), laton)
        val hueco = 12 * u
        listOf(-1f, 1f).forEach { lado -> drawCircle(laton, 0.9f * u, Offset(c.x + lado * 8 * u, cy)) }
        if (w / 2 - hueco > 4 * u) {
            drawLine(raya, Offset(4 * u, cy), Offset(c.x - hueco, cy), 1 * u)
            drawLine(raya, Offset(c.x + hueco, cy), Offset(w - 4 * u, cy), 1 * u)
            drawCircle(raya, 1.3f * u, Offset(3 * u, cy))
            drawCircle(raya, 1.3f * u, Offset(w - 3 * u, cy))
        }
    }

    /** El texto de los botones: el verde del tema de día, el verde claro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF8CC79A) else Color(0xFF2E5A3E)

    /**
     * Botón de texto: cuatro cantoneras pequeñas de latón en las esquinas, como las del campo (un triángulo de lado curvo con
     * su remache); nada más, para que la palabra quede sola en medio. Al tocarlo el hueco se llena de pergamino tenue y el
     * latón toma la luz de la vela; apagado, el latón se oscurece. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val r = Rect(3 * u, cy - 14 * u, w - 3 * u, cy + 14 * u)
        if (r.width < 8 * u) return
        if (tocado) drawRoundRect((if (oscuro) LATON else PERGAMINO).copy(alpha = if (oscuro) 0.12f else 0.45f), r.topLeft, r.size, CornerRadius(2 * u))
        val laton = if (tocado) (if (oscuro) LLAMA else LATON) else (if (oscuro) mezcla(LATON, Color.Black, 0.2f) else LATON).copy(alpha = (if (oscuro) 0.7f else 0.85f) * apagado)
        val borde = (if (oscuro) mezcla(LATON_OSC, Color.Black, 0.3f) else LATON_OSC).copy(alpha = (if (tocado) 1f else 0.8f) * apagado)
        val lado = minOf(7 * u, r.width / 3)
        listOf(Triple(r.left, r.top, 1f to 1f), Triple(r.right, r.top, -1f to 1f), Triple(r.left, r.bottom, 1f to -1f), Triple(r.right, r.bottom, -1f to -1f))
            .forEach { (x, y, d) ->
                val (sx, sy) = d
                val pieza = Path().apply {
                    moveTo(x, y); lineTo(x + sx * lado, y)
                    quadraticTo(x + sx * lado * 0.45f, y + sy * lado * 0.45f, x, y + sy * lado); close()
                }
                drawPath(pieza, laton)
                drawPath(pieza, borde, style = Stroke(0.6f * u))
                drawCircle(borde, 0.8f * u, Offset(x + sx * 2 * u, y + sy * 2 * u))
            }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El rojo de mapa, de noche aclarado para que se vea. */
    private fun rojoMapa(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0705E) else ROJO_MAPA
    /** El pergamino de los recuadros: claro de día; de noche, en penumbra. */
    private fun pergamino(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color(0xFF0D0906), 0.85f) else mezcla(PERGAMINO, Color.White, 0.35f)

    /** Un rombo de latón pequeño (una gema engastada), de medio ancho [a] y medio alto [b], centrado en [c]. */
    private fun rombo(c: Offset, a: Float, b: Float): Path = Path().apply {
        moveTo(c.x, c.y - b); lineTo(c.x + a, c.y); lineTo(c.x, c.y + b); lineTo(c.x - a, c.y); close()
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: abajo, el camino punteado de un mapa (rayitas de tinta, una sola línea con guiones) y, a la izquierda, una gema de
     * latón en rombo (con detalle, dos, una sobre otra). Al tocarla, la luz de la vela lava la fila y la gema se enciende. Sin
     * degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(LLAMA.copy(alpha = if (oscuro) 0.09f else 0.18f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine((if (oscuro) PERGAMINO.copy(alpha = 0.2f) else TINTA.copy(alpha = 0.3f)), Offset(x0, h - 1.2f * u), Offset(w - 4 * u, h - 1.2f * u), 0.9f * u,
            StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(3 * u, 3.5f * u)))
        val laton = if (e.presionado) (if (oscuro) LLAMA else LATON) else (if (oscuro) LATON else LATON_OSC).copy(alpha = 0.7f)
        if (e.conDetalle) {
            drawPath(rombo(Offset(5.5f * u, h / 2 - 3.4f * u), 1.8f * u, 2.6f * u), laton)
            drawPath(rombo(Offset(5.5f * u, h / 2 + 3.4f * u), 1.5f * u, 2.2f * u), laton)
        } else drawPath(rombo(Offset(5.5f * u, h / 2), 2 * u, 3 * u), laton)
    }

    /**
     * Flecha: la aguja de una brújula, acostada: la mitad que señala, de rojo de mapa, llena, y la otra de latón, a línea, con el
     * pivote en medio. Sin degradados.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2; val cx = w * 0.5f
        val a = h * 0.17f
        val punta = Path().apply { moveTo(w * 0.95f, cy); lineTo(cx, cy - a); lineTo(cx, cy + a); close() }
        val cola = Path().apply { moveTo(w * 0.06f, cy); lineTo(cx, cy - a); lineTo(cx, cy + a); close() }
        drawPath(punta, rojoMapa(oscuro))
        drawPath(cola, (if (oscuro) LATON else LATON_OSC), style = Stroke(0.9f * u))
        drawCircle(if (oscuro) LATON_LUZ else LATON_OSC, 1.3f * u, Offset(cx, cy))
    }

    /** El contorno de una gota de lacre de [r] de radio, que se corrió al aplastarla: un círculo de borde irregular (22 lados). */
    private fun gotaLacre(c: Offset, r: Float): Path = Path().apply {
        val n = 22
        for (j in 0..n) {
            val a = j * 2 * Math.PI.toFloat() / n
            val rr = r * (0.9f + 0.1f * azar(23, j % n))
            val p = Offset(c.x + rr * kotlin.math.cos(a), c.y + rr * kotlin.math.sin(a))
            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /**
     * Marca de fijar: un sello de lacre. Sellado, la gota de lacre rojo aplastada, con el cerco del sello y la rosa de los vientos
     * hundida en medio; sin sellar, solo el contorno de la gota con guiones y la rosa a línea, tenues. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        val gota = gotaLacre(c, 10 * k)
        val rosa = estrella(c, 5 * k, 4, 0.34f)
        if (fija) {
            val rojo = rojoMapa(oscuro)
            translate(0.4f * k, 0.9f * k) { drawPath(gota, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f)) }
            drawPath(gota, rojo)
            drawCircle(mezcla(rojo, Color.Black, 0.3f), 6.8f * k, c, style = Stroke(0.9f * u))
            drawPath(rosa, mezcla(rojo, Color.Black, 0.35f))
            drawCircle(Color.White.copy(alpha = 0.35f), 1.4f * k, Offset(c.x - 5 * k, c.y - 5 * k))
        } else {
            val linea = (if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.6f else 0.42f)
            drawPath(gota, linea, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
            drawPath(rosa, linea, style = Stroke(0.7f * u))
        }
    }

    override val tamDibujoVacio: Dp = 44.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, PERGAMINO_OSC, 0.4f) else Color(0xFF574430)

    /**
     * Vacío: un mapa en blanco, la tierra que nadie ha recorrido todavía: la hoja de pergamino de borde irregular con su filete
     * de encuadre (la línea que cierra el mapa) y, abajo a la derecha, la rosa de los vientos pequeña y tenue, sin camino que
     * señalar. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 24 * u) return
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        val puntos = ((r.width + r.height) * 2 / (7 * u)).toInt().coerceIn(20, 240)
        val perg = contornoIrregular(r, s + 3, puntos, 1.4f * u / minOf(r.width, r.height).coerceAtLeast(1f))
        drawPath(perg, pergamino(oscuro))
        drawPath(perg, (if (oscuro) PERGAMINO else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.14f else 0.6f), style = Stroke(0.8f * u))
        val tinta = (if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.2f else 0.28f)
        drawRect(tinta, Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), style = Stroke(0.8f * u))
        val c = Offset(w - 18 * u, h - 18 * u)
        val laton = (if (oscuro) LATON else LATON_OSC).copy(alpha = if (oscuro) 0.4f else 0.45f)
        drawPath(estrella(c, 4.6f * u, 4, 0.32f, -45f), laton.copy(alpha = laton.alpha * 0.7f))
        drawPath(estrella(c, 7 * u, 4, 0.24f), laton)
        drawCircle(laton, 9 * u, c, style = Stroke(0.5f * u))
    }

    /**
     * Lo vacío: un cofre abierto y sin nada: la caja de madera con sus flejes de latón, la tapa curva echada hacia atrás, el
     * hondo oscuro a la vista y la cerradura con su gema al frente. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val madera = t(if (oscuro) mezcla(MADERA, PERGAMINO, 0.2f) else MADERA, oscuro)
        val linea = if (oscuro) mezcla(MADERA, PERGAMINO, 0.55f) else NUDO
        val laton = t(LATON, oscuro)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(f(0.1f), f(0.86f)), Size(f(0.8f), f(0.08f)))
        // La tapa, echada hacia atrás: media caña vista por dentro.
        drawArc(madera, 180f, 180f, true, Offset(f(0.14f), f(0.08f)), Size(f(0.72f), f(0.4f)))
        drawArc(linea, 180f, 180f, true, Offset(f(0.14f), f(0.08f)), Size(f(0.72f), f(0.4f)), style = Stroke(0.9f * u))
        drawArc(laton, 180f, 180f, false, Offset(f(0.24f), f(0.14f)), Size(f(0.52f), f(0.3f)), style = Stroke(1.2f * u))
        // El hondo del cofre, vacío.
        drawRect(if (oscuro) Color(0xFF070504) else mezcla(NUDO, Color.Black, 0.3f), Offset(f(0.15f), f(0.28f)), Size(f(0.7f), f(0.22f)))
        // La caja.
        drawRect(madera, Offset(f(0.12f), f(0.48f)), Size(f(0.76f), f(0.38f)))
        drawRect(linea, Offset(f(0.12f), f(0.48f)), Size(f(0.76f), f(0.38f)), style = Stroke(0.9f * u))
        listOf(0.22f, 0.74f).forEach { x -> drawRect(laton, Offset(f(x), f(0.48f)), Size(f(0.05f), f(0.38f))) }
        drawLine(laton, Offset(f(0.12f), f(0.5f)), Offset(f(0.88f), f(0.5f)), 1.2f * u)
        // La cerradura con su gema.
        drawRect(laton, Offset(f(0.43f), f(0.54f)), Size(f(0.14f), f(0.15f)))
        drawPath(rombo(Offset(f(0.5f), f(0.615f)), f(0.035f), f(0.05f)), rojoMapa(oscuro))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA

    /**
     * Aviso: una hoja de pergamino clavada a un listón de madera tallada, a la izquierda, con sus remaches de latón de trecho en
     * trecho; junto al listón, a la altura del primer renglón, la marca de la nota según [EstadoAviso.semilla]: la rosa de los
     * vientos, la llama de la vela o la runa algiz (la de la protección). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(pergamino(oscuro), Offset.Zero, size)
        drawLine((if (oscuro) PERGAMINO else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.14f else 0.6f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u)
        // El listón con sus remaches.
        val liston = Rect(1.5f * u, 0f, 9 * u, h)
        drawRect(t(TALLA, oscuro), liston.topLeft, liston.size)
        clipRect(liston.left, liston.top, liston.right, liston.bottom) { vetas(liston, t(NUDO, oscuro).copy(alpha = 0.5f), e.semilla, 0.7f, vertical = true) }
        val paso = maxOf(13 * u, 1f)
        var y = 6 * u; var k = 0
        while (y < h - 3 * u && k < 100) {
            drawCircle(t(LATON_OSC, oscuro), 1.3f * u, Offset(liston.center.x, y))
            drawCircle(t(LATON_LUZ, oscuro), 0.6f * u, Offset(liston.center.x - 0.3f * u, y - 0.3f * u))
            y += paso; k++
        }
        val cx = 17.5f * u; val cy = 16 * u
        val laton = if (oscuro) LATON else LATON_OSC
        when (((e.semilla % 3) + 3) % 3) {
            0 -> { drawPath(estrella(Offset(cx, cy), 3.6f * u, 4, 0.32f, -45f), laton.copy(alpha = 0.6f)); drawPath(estrella(Offset(cx, cy), 5.4f * u, 4, 0.26f), laton) }
            1 -> {
                val llama = Path().apply { moveTo(cx, cy - 6 * u); cubicTo(cx + 4 * u, cy - 1 * u, cx + 3.4f * u, cy + 4 * u, cx, cy + 4.4f * u); cubicTo(cx - 3.4f * u, cy + 4 * u, cx - 4 * u, cy - 1 * u, cx, cy - 6 * u); close() }
                drawPath(llama, if (oscuro) LLAMA else mezcla(LLAMA, ROJO_MAPA, 0.45f))
                drawOval(if (oscuro) mezcla(LLAMA, Color.White, 0.5f) else LLAMA, Offset(cx - 1.2f * u, cy - 0.4f * u), Size(2.4f * u, 3.8f * u))
            }
            else -> {
                val tinta = if (oscuro) PERGAMINO else TINTA
                drawLine(tinta, Offset(cx, cy - 5 * u), Offset(cx, cy + 5 * u), 1.2f * u, StrokeCap.Round)
                drawLine(tinta, Offset(cx, cy), Offset(cx - 3.6f * u, cy - 4.6f * u), 1.2f * u, StrokeCap.Round)
                drawLine(tinta, Offset(cx, cy), Offset(cx + 3.6f * u, cy - 4.6f * u), 1.2f * u, StrokeCap.Round)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) LLAMA else ROJO_MAPA
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, LATON, 0.35f) else LATON_VIEJO

    /**
     * Cifra: un marbete de pergamino, la etiqueta que se ata a un tomo o a un cofre: el pergamino con las dos esquinas de la
     * derecha cortadas en punta, su borde de tinta y, cerca de la punta, el ojal con su arandela de latón y el cordón que se va.
     * Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 16 * u) return
        val corte = minOf(10 * u, h * 0.3f)
        val marbete = Path().apply {
            moveTo(0.5f * u, 0.5f * u); lineTo(w - corte, 0.5f * u); lineTo(w - 0.5f * u, corte); lineTo(w - 0.5f * u, h - 1.5f * u - corte)
            lineTo(w - corte, h - 1.5f * u); lineTo(0.5f * u, h - 1.5f * u); close()
        }
        translate(0.6f * u, 1.2f * u) { drawPath(marbete, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f)) }
        drawPath(marbete, pergamino(oscuro))
        drawPath(marbete, (if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.22f else 0.4f), style = Stroke(0.8f * u))
        val o = Offset(w - 6.5f * u, (h - 1.5f * u) / 2)
        drawCircle(t(LATON, oscuro), 2.8f * u, o)
        drawCircle(t(LATON_OSC, oscuro), 2.8f * u, o, style = Stroke(0.6f * u))
        drawCircle(if (oscuro) Color(0xFF070504) else TINTA.copy(alpha = 0.75f), 1.2f * u, o)
        drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + 4 * u, o.y + 3 * u, w, o.y + 1.6f * u) }, (if (oscuro) PERGAMINO else NUDO).copy(alpha = 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** La cera de la vela. */
    private val CERA = Color(0xFFF2E6C8)

    /** El fondo liso de cada tarjeta, donde va el texto: el pergamino del rollo, el del pliego sellado, la noche y la ficha. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA, TipoTarjeta.VIDA -> pergamino(oscuro)
        TipoTarjeta.PENDIENTE -> if (oscuro) mezcla(ROJO_MAPA, Color(0xFF0D0906), 0.86f) else mezcla(PERGAMINO, Color.White, 0.2f)
        TipoTarjeta.NOCHE -> if (oscuro) mezcla(MADERA, Color(0xFF0D0906), 0.84f) else mezcla(PERGAMINO, MADERA, 0.1f)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color.White, 0.35f) else TINTA
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, PERGAMINO_OSC, 0.45f) else mezcla(TINTA, PERGAMINO_OSC, 0.3f)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) LATON_LUZ else LATON_VIEJO
        TipoTarjeta.NOCHE -> if (oscuro) LLAMA else LATON_VIEJO
        TipoTarjeta.PENDIENTE -> if (oscuro) rojoMapa(true) else ROJO_MAPA
        TipoTarjeta.VIDA -> if (oscuro) rojoMapa(true) else mezcla(ROJO_MAPA, TINTA, 0.15f)
    }

    /**
     * Tarjetas: cuatro cosas de la posada y del camino. La cifra de Hoy es un rollo de pergamino abierto, con sus dos varas
     * enrolladas a los lados (el cilindro con su luz y su sombra y las cabezas de las varas). «Te falta hoy» es un pliego con
     * su filete doble de oro, un rombo en cada esquina y, arriba a la derecha, el sello de lacre rojo con sus dos cintas. La
     * noche es la tabla de la posada a la luz de una vela: arriba a la derecha, la vela en su palmatoria de latón, con la llama
     * y su resplandor, y dos listones de madera arriba y abajo. La tarjeta de Vida es una casilla del inventario del
     * aventurero: el marco de madera tallada con sus vetas, el pergamino en medio, una gema de latón arriba y un clavo en cada
     * esquina. Al tocarlas se oscurecen apenas y el marco se hunde. Destacadas: el rollo lleva una cinta roja atada a la vara;
     * el pliego completo, el sello con la rosa de los vientos hundida y el oro más vivo; la noche sellada, la vela apagada, con
     * su hilo de humo, y una gota de lacre en la esquina. Un degradado: la luz de la vela.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 16 * u || size.height < 16 * u) return
        when (e.tipo) {
            TipoTarjeta.METRICA -> rollo(e, oscuro)
            TipoTarjeta.PENDIENTE -> pliegoSellado(e, oscuro)
            TipoTarjeta.NOCHE -> velaPosada(e, oscuro)
            TipoTarjeta.VIDA -> casillaInventario(e, oscuro)
        }
    }

    /** El fondo de [tipo], hundido un poco si se está tocando. */
    private fun fondoTocado(tipo: TipoTarjeta, presionado: Boolean, oscuro: Boolean): Color {
        val f = fondoTarjeta(tipo, oscuro)
        return if (!presionado) f else if (oscuro) mezcla(f, Color.Black, 0.25f) else mezcla(f, PERGAMINO_OSC, 0.2f)
    }

    private fun DrawScope.rollo(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val vara = 8 * u
        val baja = if (e.presionado) 0.8f * u else 0f
        // La hoja, entre las dos varas.
        val hoja = Rect(vara - 1 * u, 2 * u + baja, w - vara + 1 * u, h - 3.5f * u + baja)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), hoja.topLeft + Offset(0f, 1.4f * u - baja), hoja.size)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), hoja.topLeft, hoja.size)
        val sombra = (if (oscuro) Color.Black else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.4f else 0.45f)
        drawLine(sombra, Offset(hoja.left, hoja.top + 0.4f * u), Offset(hoja.right, hoja.top + 0.4f * u), 0.8f * u)
        drawLine(sombra, Offset(hoja.left, hoja.bottom - 0.4f * u), Offset(hoja.right, hoja.bottom - 0.4f * u), 0.8f * u)
        // Las dos varas, con la hoja enrollada: el cilindro con su luz y su sombra, y las cabezas de madera.
        val rollo = t(PERGAMINO_OSC, oscuro)
        for (x in listOf(0.5f * u, w - vara - 0.5f * u)) {
            val r = Rect(x, 1.5f * u + baja, x + vara, h - 3 * u + baja)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), r.topLeft + Offset(0.6f * u, 1.2f * u - baja), r.size)
            drawRect(rollo, r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.35f), Offset(r.left + vara * 0.2f, r.top), Size(vara * 0.22f, r.height))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.2f), Offset(r.right - vara * 0.3f, r.top), Size(vara * 0.3f, r.height))
            val cabeza = t(TALLA, oscuro)
            drawRoundRect(cabeza, Offset(r.left - 0.6f * u, r.top - 1.5f * u), Size(vara + 1.2f * u, 2.4f * u), CornerRadius(1.2f * u))
            drawRoundRect(cabeza, Offset(r.left - 0.6f * u, r.bottom - 0.9f * u), Size(vara + 1.2f * u, 2.4f * u), CornerRadius(1.2f * u))
        }
        if (e.destacado) {
            // La cinta roja atada a la vara de la derecha, con sus dos puntas.
            val rojo = rojoMapa(oscuro)
            val x = w - vara - 0.5f * u; val y = h * 0.62f + baja
            drawRect(rojo, Offset(x - 0.4f * u, y - 1.4f * u), Size(vara + 0.8f * u, 2.8f * u))
            drawPath(Path().apply { moveTo(x + 1 * u, y); lineTo(x - 2 * u, y + 9 * u); lineTo(x + 0.2f * u, y + 8 * u); lineTo(x + 1.4f * u, y + 9.6f * u); close() }, rojo)
            drawPath(Path().apply { moveTo(x + 2 * u, y); lineTo(x + 3.5f * u, y + 10 * u); lineTo(x + 4.6f * u, y + 8.4f * u); lineTo(x + 6 * u, y + 9.4f * u); close() }, mezcla(rojo, Color.Black, 0.2f))
            drawRect(rojoMapa(oscuro).copy(alpha = 0.8f), hoja.deflate(2 * u).topLeft, hoja.deflate(2 * u).size, style = Stroke(0.8f * u))
        }
    }

    private fun DrawScope.pliegoSellado(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val completo = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0.6f * u, 1.6f * u), Size(w - 0.6f * u, h - 1.6f * u))
        val r = Rect(0f, 0f, w - 0.6f * u, h - 1.6f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        drawRect(t(PERGAMINO_OSC, oscuro).copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(0.8f * u))
        // El filete doble de oro, con un rombo en cada esquina.
        val oro = if (completo) (if (oscuro) LATON_LUZ else LATON) else t(if (oscuro) LATON else LATON_OSC, oscuro)
        val f = r.deflate(3.5f * u)
        drawRect(oro, f.topLeft, f.size, style = Stroke(1.2f * u))
        drawRect(oro.copy(alpha = 0.7f), f.deflate(2 * u).topLeft, f.deflate(2 * u).size, style = Stroke(0.5f * u))
        listOf(f.topLeft, Offset(f.right, f.top), Offset(f.left, f.bottom), f.bottomRight).forEach { p -> drawPath(rombo(p, 2.4f * u, 2.4f * u), oro) }
        // El sello de lacre, arriba a la derecha (al medio si el pliego es bajo), con sus dos cintas.
        val rs = minOf(10 * u, (r.height - 10 * u) / 2)
        val c = Offset(r.right - rs - 14 * u, if (completo) r.height / 2 else rs + 6 * u)
        val rojo = rojoMapa(oscuro)
        val cinta = mezcla(rojo, Color.Black, 0.25f)
        drawPath(Path().apply { moveTo(c.x - rs * 0.4f, c.y); lineTo(c.x - rs * 0.9f, c.y + rs * 1.9f); lineTo(c.x - rs * 0.55f, c.y + rs * 1.65f); lineTo(c.x - rs * 0.25f, c.y + rs * 2f); close() }, cinta)
        drawPath(Path().apply { moveTo(c.x + rs * 0.2f, c.y); lineTo(c.x + rs * 0.5f, c.y + rs * 2.1f); lineTo(c.x + rs * 0.8f, c.y + rs * 1.75f); lineTo(c.x + rs * 1.15f, c.y + rs * 2f); close() }, cinta)
        drawPath(gotaLacre(c + Offset(0.4f * u, 0.8f * u), rs), Color.Black.copy(alpha = 0.3f))
        drawPath(gotaLacre(c, rs), rojo)
        drawCircle(mezcla(rojo, Color.Black, 0.25f), rs * 0.68f, c, style = Stroke(0.8f * u))
        if (completo) {
            // La rosa de los vientos hundida en el lacre.
            drawPath(estrella(c, rs * 0.55f, 4, 0.3f), mezcla(rojo, Color.Black, 0.35f))
            drawPath(estrella(c, rs * 0.38f, 4, 0.3f, giro = -45f), mezcla(rojo, Color.Black, 0.2f))
        }
        drawCircle(Color.White.copy(alpha = 0.3f), rs * 0.22f, c + Offset(-rs * 0.45f, -rs * 0.45f))
    }

    private fun DrawScope.velaPosada(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Offset(0f, 1.6f * u), Size(w, h - 1.6f * u))
        val r = Rect(0f, 0f, w, h - 1.6f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), r.topLeft, r.size)
        // Los listones de madera, arriba y abajo, con sus muescas.
        val madera = t(MADERA, oscuro)
        val talla = t(NUDO, oscuro).copy(alpha = 0.7f)
        val alto = 3.4f * u
        for (y in listOf(0f, r.bottom - alto)) {
            drawRect(madera, Offset(0f, y), Size(w, alto))
            vetas(Rect(0f, y, w, y + alto), t(NUDO, oscuro).copy(alpha = 0.35f), e.semilla + y.toInt(), densidad = 0.6f, grosor = 0.5f)
            val paso = maxOf(9 * u, 1f)
            val n = (w / paso).toInt().coerceIn(1, 80)
            for (j in 0 until n) drawPath(rombo(Offset((j + 0.5f) * paso, y + alto / 2), 1 * u, alto * 0.3f), talla)
        }
        // La vela, arriba a la derecha, en su palmatoria de latón.
        val cx = w - 22 * u
        val pie = minOf(24 * u, r.bottom * 0.3f)
        val llama = Offset(cx, pie - 15 * u)
        if (!sellado) mancha(llama, 30 * u, LLAMA.copy(alpha = if (oscuro) 0.4f else 0.32f))
        val laton = t(LATON, oscuro)
        drawOval(laton, Offset(cx - 7 * u, pie - 1.6f * u), Size(14 * u, 3.4f * u))
        drawOval(t(LATON_OSC, oscuro), Offset(cx - 7 * u, pie - 1.6f * u), Size(14 * u, 3.4f * u), style = Stroke(0.6f * u))
        drawArc(laton, -60f, 120f, false, Offset(cx + 4.5f * u, pie - 3.8f * u), Size(5 * u, 4.4f * u), style = Stroke(1 * u))
        val cera = if (oscuro) mezcla(CERA, Color(0xFF0D0906), 0.25f) else CERA
        val alturaVela = 9 * u
        drawRect(cera, Offset(cx - 2 * u, pie - alturaVela), Size(4 * u, alturaVela))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(cx + 1 * u, pie - alturaVela), Size(1 * u, alturaVela))
        drawRoundRect(cera, Offset(cx - 2.6f * u, pie - alturaVela + 1 * u), Size(1.4f * u, 3.4f * u), CornerRadius(0.7f * u))
        drawLine(t(NUDO, oscuro), Offset(cx, pie - alturaVela), Offset(cx + 0.2f * u, pie - alturaVela - 1.6f * u), 0.5f * u)
        if (sellado) {
            // Apagada: el hilo de humo que sube.
            drawPath(Path().apply {
                moveTo(cx + 0.2f * u, pie - alturaVela - 1.6f * u)
                cubicTo(cx - 2.5f * u, pie - alturaVela - 4 * u, cx + 3 * u, pie - alturaVela - 6.5f * u, cx, pie - alturaVela - 9.5f * u)
            }, (if (oscuro) PERGAMINO else TINTA).copy(alpha = 0.35f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            // La gota de lacre, abajo a la derecha, sobre el listón.
            val c = Offset(w - 12 * u, r.bottom - alto - 6 * u)
            drawPath(gotaLacre(c, 4.5f * u), rojoMapa(oscuro))
            drawPath(estrella(c, 2.6f * u, 4, 0.3f), mezcla(rojoMapa(oscuro), Color.Black, 0.3f))
        } else {
            val p = Path().apply {
                moveTo(llama.x, llama.y - 4 * u)
                quadraticTo(llama.x + 2.6f * u, llama.y + 1 * u, llama.x, llama.y + 2.4f * u)
                quadraticTo(llama.x - 2.6f * u, llama.y + 1 * u, llama.x, llama.y - 4 * u)
                close()
            }
            drawPath(p, LLAMA)
            drawCircle(Color.White.copy(alpha = 0.7f), 0.9f * u, Offset(llama.x, llama.y + 1 * u))
        }
    }

    private fun DrawScope.casillaInventario(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(0.5f * u, (if (e.presionado) 0.8f else 1.8f) * u), Size(w - 1 * u, h - 1.8f * u))
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.8f * u)
        // El marco de madera tallada, con sus vetas.
        drawRect(t(MADERA, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, t(NUDO, oscuro).copy(alpha = 0.4f), e.semilla + 3, densidad = 0.7f, grosor = 0.6f) }
        val luz = Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f); val sombra = Color.Black.copy(alpha = 0.35f)
        if (e.presionado) bisel(r, sombra, luz, 1 * u) else bisel(r, luz, sombra, 1 * u)
        // El pergamino en medio, rebajado en el marco.
        val c = r.deflate(4.5f * u)
        drawRect(fondoTocado(e.tipo, e.presionado, oscuro), c.topLeft, c.size)
        bisel(c, sombra, luz.copy(alpha = luz.alpha * 0.5f), 0.9f * u)
        // Los clavos de latón en las esquinas y la gema arriba, al centro.
        val laton = t(LATON, oscuro)
        listOf(Offset(r.left + 2.3f * u, r.top + 2.3f * u), Offset(r.right - 2.3f * u, r.top + 2.3f * u),
            Offset(r.left + 2.3f * u, r.bottom - 2.3f * u), Offset(r.right - 2.3f * u, r.bottom - 2.3f * u)).forEach { p ->
            drawCircle(laton, 1.3f * u, p)
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.2f else 0.5f), 0.45f * u, p - Offset(0.4f * u, 0.4f * u))
        }
        val gema = Offset(w / 2, r.top + 2.3f * u)
        drawPath(rombo(gema, 4.2f * u, 2.6f * u), laton)
        drawPath(rombo(gema, 2.4f * u, 1.5f * u), if (e.destacado) rojoMapa(oscuro) else t(LATON_OSC, oscuro))
        if (e.destacado) drawRect(laton, c.deflate(1.2f * u).topLeft, c.deflate(1.2f * u).size, style = Stroke(0.9f * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** El verde de los bosques del mapa: la marca de los ingresos. */
    private val VERDE_BOSQUE = Color(0xFF2E5A3E)

    /** Una cantonera de latón en la esquina [c] de un recuadro, de [l] de lado, con su remache; [sx] y [sy] dicen hacia dónde abre. */
    private fun DrawScope.cantoneraGastos(c: Offset, l: Float, sx: Float, sy: Float, oscuro: Boolean) {
        val p = Path().apply { moveTo(c.x, c.y); lineTo(c.x + sx * l, c.y); lineTo(c.x, c.y + sy * l); close() }
        drawPath(p, if (oscuro) mezcla(LATON, Color(0xFF0D0906), 0.25f) else LATON)
        drawPath(p, LATON_OSC, style = Stroke(0.6f * density))
        drawCircle(LATON_OSC, l * 0.13f, Offset(c.x + sx * l * 0.28f, c.y + sy * l * 0.28f))
    }

    override val altoBalanza: Dp = 60.dp

    /**
     * Balanza: una tabla de madera tallada a todo el ancho que sujeta una tira de pergamino, partida al medio por una muesca; sobre
     * la mitad elegida corre un marco de latón, como la corredera de un ábaco o de un astrolabio, con su canto claro y un remache
     * en cada esquina, y el pergamino de debajo se aclara a la luz de la vela; la otra mitad queda en pergamino viejo, tostado, sin
     * marco, con el texto delgado. Un degradado: la luz de la vela.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 30 * u) return
        val tabla = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRoundRect(t(MADERA, oscuro), tabla.topLeft, tabla.size, CornerRadius(3 * u))
        vetas(tabla, (if (oscuro) Color.Black else NUDO).copy(alpha = 0.32f), 61, grosor = 0.9f)
        drawRoundRect(NUDO.copy(alpha = 0.7f), tabla.topLeft, tabla.size, CornerRadius(3 * u), style = Stroke(1 * u))
        val xm = w / 2
        for (lado in 0..1) {
            val elegida = (lado == 1) == e.ingreso
            val r = if (lado == 0) Rect(tabla.left + 6 * u, tabla.top + 6 * u, xm - 3 * u, tabla.bottom - 6 * u)
            else Rect(xm + 3 * u, tabla.top + 6 * u, tabla.right - 6 * u, tabla.bottom - 6 * u)
            val fondo = when {
                elegida -> pergamino(oscuro)
                oscuro -> mezcla(PERGAMINO_OSC, Color(0xFF0D0906), 0.86f)
                else -> mezcla(PERGAMINO, PERGAMINO_OSC, 0.55f)
            }
            val hoja = contornoIrregular(r, 62 + lado, ((r.width + r.height) * 2 / (10 * u)).toInt().coerceIn(12, 90), 0.9f * u / r.height)
            drawPath(hoja, fondo)
            var luz = fondo
            if (elegida) {
                luz = mezcla(fondo, LLAMA, if (oscuro) 0.14f else 0.2f)
                drawPath(hoja, Brush.radialGradient(listOf(LLAMA.copy(alpha = if (oscuro) 0.14f else 0.2f), Color.Transparent), r.center, r.width * 0.5f))
                val marco = r.inflate(2.5f * u)
                drawRect(if (oscuro) mezcla(LATON, Color(0xFF0D0906), 0.3f) else LATON, marco.topLeft, marco.size, style = Stroke(3 * u))
                drawRect(if (oscuro) LATON else LATON_LUZ, marco.topLeft + Offset(-1 * u, -1 * u), Size(marco.width + 2 * u, marco.height + 2 * u), style = Stroke(0.7f * u))
                drawRect(LATON_OSC, marco.topLeft + Offset(1.6f * u, 1.6f * u), Size(marco.width - 3.2f * u, marco.height - 3.2f * u), style = Stroke(0.6f * u))
                for (c in listOf(marco.topLeft, marco.topRight, marco.bottomLeft, marco.bottomRight)) {
                    drawCircle(LATON_OSC, 1.7f * u, c); drawCircle(LATON_LUZ, 0.8f * u, c - Offset(0.4f * u, 0.4f * u))
                }
            } else drawPath(hoja, TINTA.copy(alpha = if (oscuro) 0.3f else 0.22f), style = Stroke(0.8f * u))
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val base = if (lado == 0) e.colorGasto else e.colorIngreso
            val caja = Rect(r.left + 10 * u, r.top + 3 * u, r.right - 10 * u, r.bottom - 3 * u)
            if (elegida) textoEn(medidor, texto, caja, LetraExtra.camino, legibleAnt(base, 5f, fondo, luz), 20f, espaciado = 0.6f)
            else textoEn(medidor, texto, caja, LetraExtra.camino, legibleAnt(mezcla(base, fondo, 0.22f), 4.7f, fondo), 17f, espaciado = 0.6f)
        }
        // La muesca tallada del medio.
        drawLine(NUDO.copy(alpha = 0.8f), Offset(xm, tabla.top + 3 * u), Offset(xm, tabla.bottom - 3 * u), 2 * u)
        drawLine((if (oscuro) MADERA else LATON_LUZ).copy(alpha = 0.35f), Offset(xm + 1.4f * u, tabla.top + 3 * u), Offset(xm + 1.4f * u, tabla.bottom - 3 * u), 0.7f * u)
    }

    override val altoMes: Dp = 52.dp

    /**
     * Encabezado del mes: una banderola de pergamino, como las de los mapas antiguos: la cinta del medio con el mes y, detrás, sus
     * dos colas plegadas, más oscuras, cortadas en cola de golondrina; en los extremos, las agujas de brújula de las filas,
     * grandes, una hacia cada lado. Sin degradados.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 24 * u) return
        val r = Rect(58 * u, 2 * u, w - 58 * u, h - 8 * u)
        val cola = if (oscuro) mezcla(PERGAMINO_OSC, Color(0xFF0D0906), 0.7f) else PERGAMINO_OSC
        val tinta = TINTA.copy(alpha = if (oscuro) 0.8f else 0.6f)
        for (lado in listOf(-1f, 1f)) {
            val xi = if (lado < 0) r.left + 8 * u else r.right - 8 * u
            val xf = if (lado < 0) 46 * u else w - 46 * u
            val p = Path().apply {
                moveTo(xi, r.top + 6 * u); lineTo(xf, r.top + 6 * u); lineTo(xf - lado * 7 * u, (r.top + 6 * u + h - 1 * u) / 2)
                lineTo(xf, h - 1 * u); lineTo(xi, h - 1 * u); close()
            }
            drawPath(p, cola); drawPath(p, tinta, style = Stroke(0.8f * u))
            // El pliegue: el triangulito de sombra donde la cola pasa por detrás.
            val xp = if (lado < 0) r.left else r.right
            val pliegue = Path().apply { moveTo(xp, r.bottom); lineTo(xp - lado * 8 * u, h - 1 * u); lineTo(xp - lado * 8 * u, r.bottom); close() }
            drawPath(pliegue, NUDO.copy(alpha = if (oscuro) 0.9f else 0.55f))
        }
        val fondo = pergamino(oscuro)
        drawRect(fondo, r.topLeft, r.size)
        drawRect(tinta, r.topLeft, r.size, style = Stroke(0.9f * u))
        drawLine(rojoMapa(oscuro).copy(alpha = 0.6f), Offset(r.left + 4 * u, r.top + 3 * u), Offset(r.right - 4 * u, r.top + 3 * u), 0.7f * u)
        drawLine(rojoMapa(oscuro).copy(alpha = 0.6f), Offset(r.left + 4 * u, r.bottom - 3 * u), Offset(r.right - 4 * u, r.bottom - 3 * u), 0.7f * u)
        textoEn(medidor, e.titulo, Rect(r.left + 10 * u, r.top + 3 * u, r.right - 10 * u, r.bottom - 3 * u), LetraExtra.camino,
            legibleAnt(if (oscuro) PERGAMINO else TINTA, 7f, fondo), 22f, espaciado = 0.8f)
        inset(0f, 0f, 0f, 6 * u) { flechasMesAnt(46 * u) { flechaFila(oscuro) } }
    }

    /**
     * Asiento: una raya tenue de pauta abajo y, al principio, un rombo pequeño del color del tipo, como las marcas de un mapa:
     * rojo de mapa si es un gasto, verde de bosque si es un ingreso. Sin degradados.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.6f * u
        drawLine((if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.16f else 0.2f), Offset(11 * u, y), Offset(w - 2 * u, y), 0.8f * u)
        val c = if (e.ingreso) (if (oscuro) mezcla(VERDE_BOSQUE, Color.White, 0.42f) else VERDE_BOSQUE) else rojoMapa(oscuro)
        drawPath(rombo(Offset(4.5f * u, y), 3 * u, 3.6f * u), c.copy(alpha = 0.85f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * Rótulo del día: una etiqueta de pergamino con una cantonera de latón en cada esquina, como las de los tomos, y la fecha a
     * pluma; de ahí hacia la derecha, la raya de pauta, que acaba en un rombo rojo de mapa. Sin degradados.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 10 * u) return
        val tam = 14f
        val ancho = minOf(anchoTextoAnt(medidor, texto, LetraExtra.camino, tam, FontWeight.Normal, 1.2f) + 2 * u, w * 0.6f)
        val r = Rect(1 * u, 1 * u, ancho + 17 * u, h - 1 * u)
        val fondo = pergamino(oscuro)
        drawRect(fondo, r.topLeft, r.size)
        drawRect(TINTA.copy(alpha = if (oscuro) 0.8f else 0.45f), r.topLeft, r.size, style = Stroke(0.8f * u))
        val l = minOf(6 * u, r.height * 0.32f)
        cantoneraGastos(r.topLeft, l, 1f, 1f, oscuro); cantoneraGastos(r.topRight, l, -1f, 1f, oscuro)
        cantoneraGastos(r.bottomLeft, l, 1f, -1f, oscuro); cantoneraGastos(r.bottomRight, l, -1f, -1f, oscuro)
        textoEn(medidor, texto, Rect(r.left + 8 * u, r.top, r.left + 8 * u + ancho, r.bottom), LetraExtra.camino,
            legibleAnt(if (oscuro) LATON_LUZ else LATON_VIEJO, 6f, fondo), tam, espaciado = 1.2f)
        val x0 = r.right + 8 * u; val x1 = w - 14 * u
        if (x1 > x0) drawLine((if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.25f else 0.3f), Offset(x0, h / 2), Offset(x1, h / 2), 0.9f * u)
        drawPath(rombo(Offset(w - 7 * u, h / 2), 3.5f * u, 4.5f * u), rojoMapa(oscuro))
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * Una cantonera de latón para la esquina de arriba a la izquierda de la losa (la del chaflán a 45°): una escuadra que sigue el
     * chaflán, de [largo] por lado, con tres remaches. La de la derecha es la misma en espejo.
     */
    private fun DrawScope.cantoneraLosa(largo: Float, metal: Color, borde: Color, remache: Color, u: Float) {
        val p = Path().apply {
            moveTo(2 * u, largo); lineTo(2 * u, 19 * u); lineTo(19 * u, 2.5f * u); lineTo(largo, 2.5f * u)
            lineTo(largo, 7 * u); lineTo(21 * u, 7 * u); lineTo(7 * u, 21 * u); lineTo(7 * u, largo); close()
        }
        drawPath(p, metal)
        drawPath(p, borde, style = Stroke(0.7f * u))
        listOf(Offset(4.5f * u, largo - 4 * u), Offset(largo - 4 * u, 4.8f * u), Offset(12.2f * u, 12.2f * u)).forEach { o ->
            drawCircle(borde, 1.3f * u, o); drawCircle(remache, 0.6f * u, o - Offset(0.3f * u, 0.3f * u))
        }
    }

    /**
     * Hoja rápida: la tapa de un tomo de viaje. En las dos esquinas de arriba, cantoneras de latón remachadas que siguen el chaflán
     * de la losa; de una a otra, una costura de cuero en puntadas que se abre al centro para una gema engastada; por el canto
     * izquierdo, el camino punteado de un mapa que baja serpenteando hasta la cruz roja del tesoro, abajo; por el canto derecho,
     * muescas talladas en grupos de tres, y en las esquinas de abajo, dos cantoneras más. Sin degradados.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 120 * u) return
        val laton = mezcla(t(LATON, oscuro), e.secundario, 0.4f).copy(alpha = 0.8f)
        val borde = t(LATON_OSC, oscuro).copy(alpha = 0.85f)
        val luz = LATON_LUZ.copy(alpha = if (oscuro) 0.5f else 0.8f)
        cantoneraLosa(40 * u, laton, borde, luz, u)
        translate(0f, 3.5f * u) { scale(-1f, 1f, Offset(w / 2, 0f)) { cantoneraLosa(40 * u, laton, borde, luz, u) } }
        // La costura y la gema al centro.
        val y = 15 * u
        val puntadas = Stroke(1f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.5f * u, 3 * u)))
        listOf(46 * u to w / 2 - 12 * u, w / 2 + 12 * u to w - 46 * u).forEach { (a, b) ->
            if (b > a) drawPath(Path().apply { moveTo(a, y); lineTo(b, y) }, alfaAnt(e.secundario, 0.6f), style = puntadas)
        }
        drawLine(alfaAnt(e.primario, 0.2f), Offset(46 * u, y + 3 * u), Offset(w - 46 * u, y + 3 * u), 0.7f * u)
        drawPath(rombo(Offset(w / 2, y), 6.5f * u, 8.5f * u), laton)
        drawPath(rombo(Offset(w / 2, y), 4.5f * u, 6.2f * u), alfaAnt(e.primario, 0.9f))
        drawPath(rombo(Offset(w / 2 - 1.2f * u, y - 2 * u), 1.2f * u, 1.8f * u), Color.White.copy(alpha = if (oscuro) 0.35f else 0.6f))
        // El camino del mapa por el canto izquierdo, hasta la cruz.
        val rojo = rojoMapa(oscuro)
        val y0 = 48 * u; val y1 = h - 26 * u
        if (y1 > y0 + 20 * u) {
            val n = ((y1 - y0) / maxOf(4 * u, 1f)).toInt().coerceIn(2, 200)
            val camino = Path().apply {
                for (j in 0..n) {
                    val yy = y0 + (y1 - y0) * j / n
                    val xx = 8 * u + 3 * u * kotlin.math.sin((yy - y0) / (26 * u))
                    if (j == 0) moveTo(xx, yy) else lineTo(xx, yy)
                }
            }
            drawPath(camino, alfaAnt(e.primario, 0.4f), style = Stroke(0.9f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2.5f * u))))
            val cx = Offset(8 * u + 3 * u * kotlin.math.sin((y1 - y0) / (26 * u)), y1 + 6 * u)
            drawLine(alfaAnt(rojo, 0.75f), cx - Offset(3 * u, 3 * u), cx + Offset(3 * u, 3 * u), 1.4f * u, StrokeCap.Round)
            drawLine(alfaAnt(rojo, 0.75f), cx + Offset(3 * u, -3 * u), cx + Offset(-3 * u, 3 * u), 1.4f * u, StrokeCap.Round)
        }
        // Las muescas talladas del canto derecho.
        val talla = alfaAnt(if (oscuro) LATON_LUZ else TALLA, 0.4f)
        var ym = 50 * u; var k = 0
        while (ym < h - 30 * u && k < 60) {
            for (j in 0..2) drawLine(talla, Offset(w - 11 * u, ym + j * 3 * u), Offset(w - 5 * u, ym + j * 3 * u + (if (j == 1) 1.2f else 0f) * u), 1f * u, StrokeCap.Round)
            ym += maxOf(34 * u, 1f); k++
        }
        cantoneraGastos(Offset(2 * u, h - 2 * u), 13 * u, 1f, -1f, oscuro)
        cantoneraGastos(Offset(w - 2 * u, h - 2 * u), 13 * u, -1f, -1f, oscuro)
    }

    /**
     * Relación: una casilla del tomo. Cantoneras de latón en las cuatro esquinas; una gema arriba y otra abajo, al centro; muescas
     * talladas por los dos cantos, a media altura. El centro, liso. Sin degradados.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        listOf(Offset(2.5f * u, 2.5f * u) to (1f to 1f), Offset(w - 2.5f * u, 2.5f * u) to (-1f to 1f), Offset(2.5f * u, h - 2.5f * u) to (1f to -1f),
            Offset(w - 2.5f * u, h - 2.5f * u) to (-1f to -1f)).forEach { (o, d) -> cantoneraGastos(o, 8 * u, d.first, d.second, oscuro) }
        listOf(Offset(w / 2, 4.5f * u), Offset(w / 2, h - 4.5f * u)).forEach { o ->
            drawPath(rombo(o, 2.2f * u, 2.9f * u), alfaAnt(e.secundario, 0.85f))
            drawLine(alfaAnt(e.primario, 0.3f), Offset(o.x - 12 * u, o.y), Offset(o.x - 4 * u, o.y), 0.7f * u)
            drawLine(alfaAnt(e.primario, 0.3f), Offset(o.x + 4 * u, o.y), Offset(o.x + 12 * u, o.y), 0.7f * u)
        }
        val talla = alfaAnt(e.primario, 0.45f)
        listOf(0.36f, 0.5f, 0.64f).forEachIndexed { i, f ->
            val y = h * f; val d = (if (i % 2 == 0) -1.6f else 1.6f) * u
            drawLine(talla, Offset(3 * u, y), Offset(6.5f * u, y + d), 0.8f * u)
            drawLine(talla, Offset(w - 3 * u, y), Offset(w - 6.5f * u, y + d), 0.8f * u)
        }
    }

    /** Pista: una piedra rúnica de cabeza redonda, con su gema en lo alto y la raya del pie. Sin degradados. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val l = size.minDimension; val g = l * 0.06f
        val x0 = (size.width - l) / 2; val y0 = (size.height - l) / 2
        drawPath(Path().apply {
            moveTo(x0 + l * 0.12f, y0 + l * 0.96f); lineTo(x0 + l * 0.12f, y0 + l * 0.42f)
            arcTo(Rect(x0 + l * 0.12f, y0 + l * 0.04f, x0 + l * 0.88f, y0 + l * 0.8f), 180f, 180f, false)
            lineTo(x0 + l * 0.88f, y0 + l * 0.96f); close()
        }, e.primario, style = Stroke(g))
        drawLine(e.primario, Offset(x0 + l * 0.3f, y0 + l * 0.9f), Offset(x0 + l * 0.7f, y0 + l * 0.9f), g * 0.5f)
        drawPath(rombo(Offset(x0 + l * 0.5f, y0 + l * 0.135f), l * 0.04f, l * 0.052f), e.secundario)
    }
}
