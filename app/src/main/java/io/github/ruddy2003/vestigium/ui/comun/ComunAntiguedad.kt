package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
}
