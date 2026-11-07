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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Futuro
// =====================================================================

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY], para
 * no cruzar la primera línea). Van de abajo arriba porque el campo crece con su texto: así cada raya cae bajo un renglón,
 * lleve rótulo o no.
 */
private inline fun DrawScope.renglones(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/**
 * Distopía: las piezas comunes del registro. El campo es la ventanilla de consultas del archivo, como su buscador: la placa
 * de acero con sus cuatro tornillos y el formulario metido en su ranura, con la raya punteada donde se escribe a máquina y
 * la esquina roja de «clasificado».
 */
object ComunDistopia : DisenoComun {
    private val ACERO = Color(0xFF6F7479)
    private val ACERO_OSC = Color(0xFF43474B)
    private val ACERO_LUZ = Color(0xFF9BA1A6)
    private val ROJO = Color(0xFFA8201C)
    private val NEGRO = Color(0xFF141516)
    private val PAPEL = Color(0xFFE9E4D6)
    /** El papel del formulario en oscuro: el papel carbón de la copia, hundido. */
    private val CARBON = Color(0xFF2A2C2F)
    private val PELIGRO = Color(0xFFE0A030)

    /** El color en oscuro: hundido hacia el negro del archivo. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0C0D), 0.4f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 15.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9A978E) else Color(0xFF66625A)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07068) else ROJO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0A848) else Color(0xFF8E4C00)

    /**
     * La ventanilla de consultas: la placa de acero con su canto, sus cuatro tornillos y el piloto; el formulario metido en
     * su ranura, con la esquina roja de «clasificado» y la raya punteada de la máquina (una por renglón si son varios). Con el
     * foco, la ranura se marca en rojo, la raya se vuelve continua y el piloto se enciende; con un error, la franja de peligro
     * corre por el canto de abajo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF7A7A7A), 0.4f) }
        // La placa de acero con su canto.
        drawRoundRect(c(ACERO), Offset.Zero, size, CornerRadius(3 * u))
        drawRect(c(ACERO_LUZ), Offset(2 * u, 0.8f * u), Size(w - 4 * u, 1.2f * u))
        drawRect(c(ACERO_OSC), Offset(2 * u, h - 1.6f * u), Size(w - 4 * u, 1.6f * u))
        // El formulario en su ranura.
        val b = 5 * u
        val f = Rect(b, b, w - b, h - b)
        drawRect(c(NEGRO).copy(alpha = 0.6f), f.inflate(1 * u).topLeft, f.inflate(1 * u).size)
        val papel = if (oscuro) CARBON else PAPEL
        drawRect(if (e.habilitado) papel else mezcla(papel, Color(0xFF8A8A8A), 0.25f), f.topLeft, f.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), f.topLeft, Size(f.width, 1.4f * u))
        // La esquina de «clasificado», arriba a la derecha.
        val k = minOf(9 * u, f.height * 0.3f)
        drawPath(Path().apply { moveTo(f.right - k, f.top); lineTo(f.right, f.top); lineTo(f.right, f.top + k); close() }, c(ROJO))
        // La raya de la máquina: punteada en reposo y continua con el foco.
        val raya = tintaCampo(oscuro).copy(alpha = if (e.enfocado) 0.42f else 0.26f)
        val efecto = if (e.enfocado) null else PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u))
        val izq = f.left + 6 * u; val der = f.right - 6 * u
        if (e.multilinea) renglones(margenCampoY) { _, y -> drawLine(raya, Offset(izq, y), Offset(der, y), 0.8f * u, pathEffect = efecto) }
        else drawLine(raya, Offset(izq, f.bottom - 4 * u), Offset(der, f.bottom - 4 * u), 0.8f * u, pathEffect = efecto)
        // La ranura se marca con el foco o con el error.
        if (e.enfocado || e.error) {
            val borde = if (e.error) errorCampo(oscuro) else rotuloCampo(oscuro)
            drawRect(borde, f.topLeft, f.size, style = Stroke(1.4f * u))
        }
        // Los tornillos de las esquinas, con su ranura.
        listOf(Offset(2.6f * u, 2.6f * u), Offset(w - 2.6f * u, 2.6f * u), Offset(2.6f * u, h - 2.6f * u), Offset(w - 2.6f * u, h - 2.6f * u)).forEach { o ->
            drawCircle(c(ACERO_LUZ), 1.7f * u, o)
            drawLine(c(NEGRO), Offset(o.x - 1.1f * u, o.y - 0.6f * u), Offset(o.x + 1.1f * u, o.y + 0.6f * u), 0.6f * u)
        }
        // La franja de peligro del canto de abajo, con el error.
        if (e.error) clipRect(8 * u, h - b + 0.6f * u, w - 8 * u, h - 0.6f * u) {
            val paso = maxOf(6 * u, 1f)
            var x = 8 * u - b
            var n = 0
            while (x < w && n < 400) {
                drawPath(Path().apply { moveTo(x, h); lineTo(x + 3 * u, h); lineTo(x + 3 * u + b, h - b); lineTo(x + b, h - b); close() }, PELIGRO)
                x += paso; n++
            }
        }
        // El piloto del canto derecho: apagado, o encendido con su halo.
        val p = Offset(w - 2.5f * u, if (e.multilinea) minOf(h / 2, 22 * u) else h / 2)
        val luz = if (e.error) errorCampo(true) else Color(0xFFFF4A3A)
        val prendido = (e.enfocado || e.error) && e.habilitado
        if (prendido) drawCircle(luz.copy(alpha = 0.3f), 3.6f * u, p)
        drawCircle(if (prendido) luz else c(Color(0xFF5A1A18)), 1.6f * u, p)
        drawCircle(Color.White.copy(alpha = if (prendido) 0.7f else 0.2f), 0.5f * u, Offset(p.x - 0.5f * u, p.y - 0.5f * u))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 8.dp

    /**
     * La perforación del formulario: una hilera de agujeritos de lado a lado (un solo trazo punteado), cortada al centro por
     * la franja roja del expediente entre sus dos marcas de registro. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val franja = minOf(14 * u, w * 0.1f)
        val hueco = franja / 2 + 5 * u
        val tinta = (if (oscuro) Color(0xFF8E8C86) else Color(0xFF5A5750)).copy(alpha = 0.6f * a)
        val perforacion = Path().apply { moveTo(2 * u, y); lineTo(cx - hueco, y); moveTo(cx + hueco, y); lineTo(w - 2 * u, y) }
        drawPath(perforacion, tinta, style = Stroke(1.3f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.01f, maxOf(3.5f * u, 1f)))))
        // Las marcas de registro y la franja roja.
        listOf(cx - hueco + 2.5f * u, cx + hueco - 2.5f * u).forEach { x -> drawLine(tinta, Offset(x, y - 2 * u), Offset(x, y + 2 * u), 0.8f * u) }
        drawRect((if (oscuro) Color(0xFFC0392F) else ROJO).copy(alpha = 0.85f * a), Offset(cx - franja / 2, y - 1.1f * u), Size(franja, 2.2f * u))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07068) else ROJO

    /**
     * La casilla del formulario: la raya donde se llena, con sus dos topes, y la marca roja del expediente en la esquina de
     * arriba a la derecha. Al presionarlo la casilla se sella: se rellena de rojo tenue y la raya va en rojo; apagado, queda
     * solo la raya, punteada y gris, sin marca.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = 4 * u; val der = w - 4 * u
        val arriba = 8 * u; val pie = h - 9 * u
        val rojo = tintaBotonTexto(oscuro)
        val tinta = if (oscuro) Color(0xFFA8A69E) else Color(0xFF3E3C37)
        if (!e.habilitado) {
            drawLine(tinta.copy(alpha = 0.28f), Offset(izq, pie), Offset(der, pie), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
            return
        }
        if (e.presionado) drawRect(rojo.copy(alpha = if (oscuro) 0.16f else 0.12f), Offset(izq, arriba), Size(der - izq, pie - arriba))
        val raya = if (e.presionado) rojo else tinta.copy(alpha = 0.5f)
        drawPath(Path().apply { moveTo(izq, pie - 3.5f * u); lineTo(izq, pie); lineTo(der, pie); lineTo(der, pie - 3.5f * u) }, raya,
            style = Stroke((if (e.presionado) 1.3f else 0.9f) * u, join = StrokeJoin.Miter))
        val k = 4 * u
        drawPath(Path().apply { moveTo(der - k, arriba); lineTo(der, arriba); lineTo(der, arriba + k); close() }, rojo.copy(alpha = if (e.presionado) 1f else 0.75f))
    }
}

/**
 * Ciudad neón (solo existe en oscuro): las piezas comunes del callejón. El campo es el vidrio negro del quiosco de
 * información, como su buscador: la chapa violeta con sus tornillos, las líneas de barrido y el tubo de neón que lo enmarca,
 * con sus dos electrodos abajo a la izquierda.
 */
object ComunNeon : DisenoComun {
    private val CARCASA = Color(0xFF221C33)
    private val CARCASA_LUZ = Color(0xFF3B3354)
    private val ALAMBRE = Color(0xFFB9C1CE)
    private val MAGENTA = Color(0xFFFF2E8C)
    private val CIAN = Color(0xFF2EE8E0)
    private val ETIQUETA = Color(0xFFF1EEDF)
    private val VIDRIO = Color(0xFF05040A)

    /** El color bajo la luz del callejón: siempre hundido (el tema solo es oscuro). Los neones no pasan por aquí. */
    @Suppress("UNUSED_PARAMETER")
    private fun t(c: Color, oscuro: Boolean): Color = mezcla(c, Color(0xFF06050B), 0.3f)

    /** Un tubo de neón: el halo ancho, el brillo y el alma blanca. */
    private fun DrawScope.tubo(p: Path, color: Color, grosor: Float, efecto: PathEffect? = null) {
        drawPath(p, color.copy(alpha = 0.16f), style = Stroke(grosor * 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = efecto))
        drawPath(p, color.copy(alpha = 0.4f), style = Stroke(grosor * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = efecto))
        drawPath(p, mezcla(color, Color.White, 0.5f), style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = efecto))
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = ETIQUETA
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF9C94A8)
    override fun rotuloCampo(oscuro: Boolean): Color = CIAN
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF6A3A)

    /**
     * El vidrio del quiosco: la chapa con sus cuatro tornillos, el vidrio negro con sus líneas de barrido (y una raya tenue por
     * renglón si son varios) y el tubo de neón con sus electrodos. En reposo el tubo es cian y tibio; con el foco se enciende
     * en magenta; con un error parpadea en naranja, a tramos, como un tubo que se funde; apagado, es vidrio gris sin luz.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, true).let { if (e.habilitado) it else mezcla(it, Color(0xFF2A2A30), 0.4f) }
        drawRoundRect(c(CARCASA), Offset.Zero, size, CornerRadius(8 * u))
        drawRoundRect(c(CARCASA_LUZ), Offset(0.5f * u, 0.5f * u), Size(w - 1 * u, h - 1 * u), CornerRadius(8 * u), style = Stroke(1 * u))
        val vidrio = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        drawRoundRect(VIDRIO, vidrio.topLeft, vidrio.size, CornerRadius(5 * u))
        // Las líneas de barrido del vidrio.
        val paso = maxOf(3 * u, 1f)
        var y = vidrio.top + 1 * u
        var n = 0
        while (y < vidrio.bottom && n < 2000) { drawLine(Color.White.copy(alpha = 0.025f), Offset(vidrio.left + 3 * u, y), Offset(vidrio.right - 3 * u, y), 0.6f * u); y += paso; n++ }
        if (e.multilinea) renglones(margenCampoY) { _, yy -> drawLine(CIAN.copy(alpha = 0.09f), Offset(vidrio.left + 10 * u, yy), Offset(vidrio.right - 10 * u, yy), 0.7f * u) }
        // El tubo, con el hueco de los electrodos abajo a la izquierda.
        val marco = Path().apply { addRoundRect(RoundRect(vidrio.deflate(2.5f * u), CornerRadius(4 * u))) }
        val prendido = e.habilitado
        val col = when {
            e.error -> errorCampo(true)
            e.enfocado -> MAGENTA
            else -> CIAN
        }
        when {
            !prendido -> drawPath(marco, Color(0xFF4A4852), style = Stroke(0.9f * u))
            e.error -> tubo(marco, col, 1.1f * u, PathEffect.dashPathEffect(floatArrayOf(22 * u, 3 * u, 5 * u, 4 * u)))
            e.enfocado -> tubo(marco, col, 1.1f * u)
            else -> {
                drawPath(marco, col.copy(alpha = 0.1f), style = Stroke(3 * u))
                drawPath(marco, mezcla(col, Color(0xFF3A4048), 0.45f), style = Stroke(0.9f * u))
            }
        }
        val yb = vidrio.bottom - 2.5f * u
        val x0 = vidrio.left + 14 * u
        drawRect(VIDRIO, Offset(x0, yb - 2.6f * u), Size(7 * u, 5.2f * u))
        listOf(x0 - 2.2f * u, x0 + 7 * u).forEach { x ->
            drawRoundRect(c(Color(0xFF6A6478)), Offset(x, yb - 1.4f * u), Size(2.2f * u, 2.8f * u), CornerRadius(0.6f * u))
            drawLine(c(Color(0xFF6A6478)), Offset(x + 1.1f * u, yb + 1.4f * u), Offset(x + 1.1f * u, vidrio.bottom + 1.5f * u), 0.6f * u)
        }
        // Los tornillos de la chapa.
        listOf(Offset(3 * u, 3 * u), Offset(w - 3 * u, 3 * u), Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3 * u))
            .forEach { drawCircle(c(ALAMBRE).copy(alpha = 0.5f), 0.9f * u, it) }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El tubo de neón que cruza la pared: cian, con su halo, apagándose hacia las puntas (los dos degradados del dibujo), y
     * cortado al centro por sus dos electrodos con la chispa magenta entre ellos. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val hueco = 7 * u
        // El tubo se apaga hacia las puntas en tramos planos (sin degradados: el separador se repite muchas veces en una pantalla).
        val luz = mezcla(CIAN, Color.White, 0.35f)
        val pasos = 6
        val borde = (w * 0.2f).coerceAtLeast(1f)
        fun tramo(x0: Float, x1: Float, k: Float) {
            if (x1 <= x0) return
            drawLine(CIAN.copy(alpha = 0.16f * a * k), Offset(x0, y), Offset(x1, y), 4 * u)
            drawLine(luz.copy(alpha = 0.8f * a * k), Offset(x0, y), Offset(x1, y), 1 * u)
        }
        for (i in 0 until pasos) {
            val k = (i + 1f) / pasos
            val ancho = borde / pasos
            tramo(4 * u + ancho * i, minOf(4 * u + ancho * (i + 1), cx - hueco), k)
            tramo(maxOf(w - 4 * u - ancho * (i + 1), cx + hueco), w - 4 * u - ancho * i, k)
        }
        tramo(4 * u + borde, cx - hueco, 1f)
        tramo(cx + hueco, w - 4 * u - borde, 1f)
        // Los electrodos y la chispa.
        val metal = t(Color(0xFF6A6478), true).copy(alpha = a)
        listOf(cx - hueco - 0.5f * u, cx + hueco - 1.5f * u).forEach { x -> drawRoundRect(metal, Offset(x, y - 1.3f * u), Size(2 * u, 2.6f * u), CornerRadius(0.5f * u)) }
        drawCircle(MAGENTA.copy(alpha = 0.25f * a), 2.6f * u, Offset(cx, y))
        drawCircle(mezcla(MAGENTA, Color.White, 0.4f).copy(alpha = a), 0.9f * u, Offset(cx, y))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = Color(0xFFFF4D9D)

    /**
     * El tubo bajo el letrero: una raya de neón cian, tibia, con un electrodo en cada punta. Al presionarlo el tubo se enciende
     * en magenta y su luz baña la placa; apagado, es vidrio gris sin luz.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val y = h - 10 * u
        val izq = 9 * u; val der = w - 9 * u
        val raya = Path().apply { moveTo(izq, y); lineTo(der, y) }
        when {
            !e.habilitado -> drawPath(raya, Color(0xFF4A4852), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            e.presionado -> {
                val placa = RoundRect(Rect(3 * u, 6 * u, w - 3 * u, h - 6 * u), CornerRadius(6 * u))
                drawPath(Path().apply { addRoundRect(placa) }, MAGENTA.copy(alpha = 0.1f))
                drawPath(Path().apply { addRoundRect(placa) }, MAGENTA.copy(alpha = 0.3f), style = Stroke(0.8f * u))
                tubo(raya, MAGENTA, 0.9f * u)
            }
            else -> {
                drawPath(raya, CIAN.copy(alpha = 0.12f), style = Stroke(3.5f * u, cap = StrokeCap.Round))
                drawPath(raya, mezcla(CIAN, Color(0xFF3A4048), 0.3f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            }
        }
        // Los electrodos de las puntas.
        val metal = t(Color(0xFF6A6478), true)
        listOf(izq - 3 * u, der + 1 * u).forEach { x -> drawRoundRect(metal, Offset(x, y - 1.1f * u), Size(2 * u, 2.2f * u), CornerRadius(0.5f * u)) }
    }
}

/**
 * Refugio: las piezas comunes del refugio. El campo es la terminal del supervisor, como su buscador: el bisel de chapa crema
 * con su piloto ámbar y su rejilla a la derecha, y la pantalla de fósforo verde de esquinas redondas con sus líneas de
 * barrido y el indicador «>» arriba a la izquierda.
 */
object ComunRefugio : DisenoComun {
    private val MURO = Color(0xFFD9CFAF)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val AMARILLO = Color(0xFFE5B436)
    private val FOSFORO = Color(0xFF7DFF8C)
    private val PANTALLA = Color(0xFF041208)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0F0F), 0.36f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = FOSFORO
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF4FA05A)
    override fun rotuloCampo(oscuro: Boolean): Color = AMARILLO
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF6B52)

    /**
     * La terminal: el bisel crema (hundido en oscuro) con dos tornillos, el piloto ámbar y la rejilla a la derecha; la
     * pantalla de fósforo con sus líneas de barrido, su reflejo y el «>» arriba a la izquierda; con varios renglones, una raya
     * de fósforo tenue por renglón. Con el foco la pantalla se aviva, se ilumina su borde y el «>» y el piloto se prenden; con
     * un error el borde y el piloto van en rojo de alarma.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val bisel = (if (oscuro) mezcla(MURO, Color(0xFF0A0F0F), 0.58f) else MURO).let { if (e.habilitado) it else mezcla(it, Color(0xFF808080), 0.35f) }
        drawRoundRect(bisel, Offset.Zero, size, CornerRadius(6 * u))
        drawRoundRect(t(TUBO_OSC, oscuro).copy(alpha = 0.5f), Offset.Zero, size, CornerRadius(6 * u), style = Stroke(1 * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.12f else 0.3f), Offset(2 * u, 1 * u), Size(w - 4 * u, 1.2f * u), CornerRadius(0.6f * u))
        // La pantalla.
        val pant = Rect(5 * u, 5 * u, w - 14 * u, h - 5 * u)
        val radio = CornerRadius(7 * u)
        drawRoundRect(PANTALLA, pant.topLeft, pant.size, radio)
        val vivo = e.enfocado && e.habilitado
        if (vivo) drawRoundRect(FOSFORO.copy(alpha = 0.07f), pant.topLeft, pant.size, radio)
        val paso = maxOf(2.5f * u, 1f)
        var y = pant.top + 1 * u
        var n = 0
        while (y < pant.bottom && n < 3000) { drawLine(Color.Black.copy(alpha = 0.28f), Offset(pant.left + 3 * u, y), Offset(pant.right - 3 * u, y), 0.6f * u); y += paso; n++ }
        if (e.multilinea) renglones(margenCampoY) { _, yy -> drawLine(FOSFORO.copy(alpha = 0.08f), Offset(pant.left + 12 * u, yy), Offset(pant.right - 6 * u, yy), 0.7f * u) }
        drawRoundRect(Color.White.copy(alpha = 0.022f), Offset(pant.left + 4 * u, pant.top + 2 * u), Size(pant.width * 0.6f, minOf(pant.height * 0.3f, 14 * u)), CornerRadius(6 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.6f), pant.topLeft, pant.size, radio, style = Stroke(1.5f * u))
        when {
            e.error -> drawRoundRect(errorCampo(oscuro).copy(alpha = 0.8f), Offset(pant.left + 1 * u, pant.top + 1 * u), Size(pant.width - 2 * u, pant.height - 2 * u), CornerRadius(6 * u), style = Stroke(1.2f * u))
            vivo -> drawRoundRect(FOSFORO.copy(alpha = 0.55f), Offset(pant.left + 1 * u, pant.top + 1 * u), Size(pant.width - 2 * u, pant.height - 2 * u), CornerRadius(6 * u), style = Stroke(1 * u))
        }
        // El «>» del indicador.
        val gx = pant.left + 3.2f * u; val gy = pant.top + minOf(9 * u, pant.height / 2)
        drawPath(Path().apply { moveTo(gx, gy - 3 * u); lineTo(gx + 4 * u, gy); lineTo(gx, gy + 3 * u) },
            FOSFORO.copy(alpha = if (vivo) 1f else 0.45f), style = Stroke(1.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // El bisel de la derecha: dos tornillos, el piloto y la rejilla.
        val xb = w - 7 * u
        listOf(Offset(xb, 4 * u), Offset(xb, h - 4 * u)).forEach { o ->
            drawCircle(t(TUBO_OSC, oscuro).copy(alpha = 0.7f), 1.3f * u, o)
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(o.x - 0.8f * u, o.y), Offset(o.x + 0.8f * u, o.y), 0.5f * u)
        }
        val lp = Offset(xb, if (e.multilinea) minOf(h * 0.32f, 16 * u) else h * 0.36f)
        val luz = if (e.error) Color(0xFFFF3B26) else Color(0xFFFFC14A)
        val prendido = (e.enfocado || e.error) && e.habilitado
        if (prendido) drawCircle((if (e.error) Color(0xFFFF3B26) else AMARILLO).copy(alpha = 0.3f), 4.5f * u, lp)
        drawCircle(if (prendido) luz else t(Color(0xFF5A4A20), oscuro), 2.2f * u, lp)
        for (k in 0 until 3) {
            val yr = lp.y + 6 * u + k * 3 * u
            if (yr < h - 7 * u) drawLine(t(TUBO_OSC, oscuro), Offset(xb - 3.5f * u, yr), Offset(xb + 3.5f * u, yr), 1 * u)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El conducto del refugio: el tubo de chapa (verde de fósforo apagado en oscuro) con sus abrazaderas en las puntas, una más
     * a media distancia si cabe, y la cajita del piloto ámbar al centro. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val tubo = (if (oscuro) Color(0xFF2E6A3E) else TUBO_OSC).copy(alpha = (if (oscuro) 0.8f else 0.45f) * a)
        val cx = w / 2
        val caja = 5 * u
        drawPath(Path().apply { moveTo(3 * u, y); lineTo(cx - caja, y); moveTo(cx + caja, y); lineTo(w - 3 * u, y) }, tubo, style = Stroke(1.3f * u))
        // Las abrazaderas: en las puntas y, si el tramo es largo, a la mitad de cada lado.
        val abrazaderas = mutableListOf(3 * u, w - 5.4f * u)
        if (w > 200 * u) { abrazaderas += (cx - caja) / 2; abrazaderas += cx + caja + (w - cx - caja) / 2 - 2.4f * u }
        abrazaderas.forEach { x -> drawRect(tubo, Offset(x, y - 2.2f * u), Size(2.4f * u, 4.4f * u)) }
        // La caja del piloto, con su luz.
        drawRoundRect(tubo, Offset(cx - caja, y - 3 * u), Size(2 * caja, 6 * u), CornerRadius(1.5f * u), style = Stroke(0.8f * u))
        val luz = if (oscuro) AMARILLO else Color(0xFFB98A15)
        if (oscuro) drawCircle(luz.copy(alpha = 0.25f * a), 3 * u, Offset(cx, y))
        drawCircle(luz.copy(alpha = 0.9f * a), 1.5f * u, Offset(cx, y))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF3DFF7F) else Color(0xFF175F5B)

    /**
     * La orden de la terminal: la raya de guiones bajo el texto con el cursor en bloque al final. Al presionarlo se invierte el
     * video (el texto queda sobre un bloque de su color, tenue) y la raya se vuelve continua; apagado, queda una raya de
     * puntos gris, sin cursor.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = 7 * u; val der = w - 11 * u; val pie = h - 10 * u
        if (!e.habilitado) {
            drawLine(Color(0xFF808880).copy(alpha = 0.35f), Offset(izq, pie), Offset(der, pie), 0.9f * u, StrokeCap.Round,
                PathEffect.dashPathEffect(floatArrayOf(0.01f, 2.5f * u)))
            return
        }
        val tinta = tintaBotonTexto(oscuro)
        if (e.presionado) {
            drawRoundRect(tinta.copy(alpha = if (oscuro) 0.13f else 0.12f), Offset(4 * u, 7 * u), Size(w - 8 * u, h - 14 * u), CornerRadius(2 * u))
            drawLine(tinta, Offset(izq, pie), Offset(der, pie), 1.2f * u)
        } else drawLine(tinta.copy(alpha = 0.5f), Offset(izq, pie), Offset(der, pie), 1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 2.5f * u)))
        // El cursor en bloque, al final de la raya.
        drawRect(tinta.copy(alpha = if (e.presionado) 1f else 0.7f), Offset(der + 1.5f * u, pie - 1.4f * u), Size(3 * u, 2.8f * u))
    }
}

/**
 * Orden: las piezas comunes de la orden. El campo es la ranura del archivo sellado, como su buscador: la plancha de acero
 * con su filete de latón y sus remaches, el hueco ochavado de esmalte negro donde se escribe y, en cada esquina, un engrane
 * de latón con su lente.
 */
object ComunOrden : DisenoComun {
    private val ACERO = Color(0xFF3C424A)
    private val ACERO_LUZ = Color(0xFF6A727C)
    private val LATON = Color(0xFFB8933E)
    private val LATON_LUZ = Color(0xFFE0C070)
    private val ALERTA = Color(0xFFFF2B1E)
    private val ESMALTE = Color(0xFF0C0D10)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.35f) else c

    /** El latón: se hunde menos que el acero, para que siga brillando en oscuro. */
    private fun placa(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.16f) else c

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = Color(0xFFEFE3C2)
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF8E8672)
    override fun rotuloCampo(oscuro: Boolean): Color = LATON_LUZ
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF6150)

    /**
     * La ranura sellada: la plancha de acero con su filete de latón y los remaches a lo largo de los cantos; el hueco ochavado
     * de esmalte negro con su filo de latón y su sombra de arriba (y una raya tenue por renglón si son varios); un engrane de
     * latón con su lente en cada esquina. Con el foco el filo se pule y engorda, los engranes giran y sus lentes se calientan en
     * ámbar; con un error el filo y las lentes van en rojo de alerta; apagado, el latón se vuelve plomo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF55585C), 0.35f) }
        fun lt(x: Color) = placa(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF6E6E6E), 0.6f) }
        drawRoundRect(c(ACERO), Offset.Zero, size, CornerRadius(3 * u))
        drawRoundRect(lt(LATON), Offset(0.6f * u, 0.6f * u), Size(w - 1.2f * u, h - 1.2f * u), CornerRadius(3 * u), style = Stroke(1.2f * u))
        // Los remaches de los cantos, entre los engranes.
        val paso = maxOf(34 * u, 1f)
        var x = 18 * u
        var n = 0
        while (x < w - 16 * u && n < 300) {
            listOf(3.2f * u, h - 3.2f * u).forEach { y ->
                drawCircle(Color.Black.copy(alpha = 0.4f), 1.2f * u, Offset(x + 0.3f * u, y + 0.4f * u))
                drawCircle(c(ACERO_LUZ), 1.1f * u, Offset(x, y))
            }
            x += paso; n++
        }
        if (h > 80 * u) {
            var y = 18 * u
            var m = 0
            while (y < h - 16 * u && m < 300) {
                listOf(3.2f * u, w - 3.2f * u).forEach { xx -> drawCircle(Color.Black.copy(alpha = 0.4f), 1.2f * u, Offset(xx + 0.3f * u, y + 0.4f * u)); drawCircle(c(ACERO_LUZ), 1.1f * u, Offset(xx, y)) }
                y += paso; m++
            }
        }
        // El hueco ochavado de esmalte.
        val r = Rect(6.5f * u, 6.5f * u, w - 6.5f * u, h - 6.5f * u)
        val hueco = ochavado(r, 6 * u)
        drawPath(hueco, ESMALTE)
        clipPath(hueco) {
            drawRect(Color.Black.copy(alpha = 0.55f), r.topLeft, Size(r.width, 2.2f * u))
            if (e.multilinea) renglones(margenCampoY) { _, y -> drawLine(LATON.copy(alpha = 0.12f), Offset(r.left + 8 * u, y), Offset(r.right - 8 * u, y), 0.7f * u) }
        }
        val filo = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> lt(LATON_LUZ)
            else -> lt(LATON)
        }
        drawPath(hueco, filo, style = Stroke((if (e.enfocado || e.error) 2f else 1.2f) * u))
        // Los engranes de las esquinas, con su lente.
        val giro = if (e.enfocado) 15f else 0f
        val lente = when {
            !e.habilitado -> Color(0xFF1A1C20)
            e.error -> mezcla(Color(0xFF2A0A0A), ALERTA, 0.6f)
            e.enfocado -> Color(0xFFE09A30)
            else -> Color(0xFF1A2228)
        }
        listOf(Offset(5 * u, 5 * u), Offset(w - 5 * u, 5 * u), Offset(5 * u, h - 5 * u), Offset(w - 5 * u, h - 5 * u)).forEach { o ->
            val rr = 4.2f * u
            rotate(giro, o) {
                drawPath(engrane(Offset(o.x + 0.5f * u, o.y + 0.6f * u), rr, 8), Color.Black.copy(alpha = 0.35f))
                drawPath(engrane(o, rr, 8), lt(LATON))
            }
            drawCircle(lt(LATON_LUZ), rr * 0.6f, o)
            drawCircle(lente, rr * 0.42f, o)
            drawCircle(Color.White.copy(alpha = 0.4f), rr * 0.13f, Offset(o.x - rr * 0.14f, o.y - rr * 0.14f))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * El filete doble de latón, con un remache en cada punta y, al centro, el engrane de la orden con su lente. Tenue, a la
     * mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val laton = (if (oscuro) placa(LATON, true) else Color(0xFF8C6E2A)).copy(alpha = 0.7f * a)
        val cx = w / 2
        val r = 4 * u
        val hueco = r + 4 * u
        // El filete doble.
        val filete = Path().apply {
            listOf(-1.2f * u, 1.2f * u).forEach { dy -> moveTo(8 * u, y + dy); lineTo(cx - hueco, y + dy); moveTo(cx + hueco, y + dy); lineTo(w - 8 * u, y + dy) }
        }
        drawPath(filete, laton, style = Stroke(0.7f * u))
        // Los remaches de las puntas.
        listOf(4.5f * u, w - 4.5f * u).forEach { drawCircle(laton, 1.3f * u, Offset(it, y)) }
        // El engrane del centro, con su lente.
        drawPath(engrane(Offset(cx, y), r, 8), laton)
        drawCircle(Color(0xFF1A2228).copy(alpha = 0.85f * a), r * 0.42f, Offset(cx, y))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD6B25A) else Color(0xFF6E5414)

    /**
     * La placa de latón: el contorno ochavado, fino, con un remache a cada lado. Al presionarlo la placa se pule (se rellena de
     * latón tenue y el contorno engorda); apagada, es de plomo.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 7 * u, w - 3 * u, h - 7 * u)
        val contorno = ochavado(r, 4 * u)
        val laton = if (e.habilitado) tintaBotonTexto(oscuro) else Color(0xFF8A8C90)
        val pulida = e.presionado && e.habilitado
        if (pulida) drawPath(contorno, laton.copy(alpha = if (oscuro) 0.16f else 0.14f))
        drawPath(contorno, laton.copy(alpha = when { !e.habilitado -> 0.25f; pulida -> 0.9f; else -> 0.3f }), style = Stroke((if (pulida) 1.3f else 0.9f) * u))
        // Los dos remaches, a los lados.
        listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { drawCircle(laton.copy(alpha = if (e.habilitado) 0.7f else 0.3f), 1 * u, Offset(it, r.center.y)) }
    }
}

/**
 * Laboratorio: las piezas comunes del laboratorio. El campo es el portaobjetos sobre la platina, como su buscador: la
 * platina de metal con sus dos pinzas, el vidrio con su extremo esmerilado a la izquierda y la escala micrométrica grabada a
 * lo largo del canto de abajo.
 */
object ComunLaboratorio : DisenoComun {
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val CIAN = Color(0xFF1FB5C9)
    private val ROJO = Color(0xFFD8312A)
    private val TINTA = Color(0xFF12202B)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0E12), 0.38f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 17.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE4F0F4) else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8A9CA6) else Color(0xFF63707A)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF55D2E2) else Color(0xFF0B7180)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF7A6E) else Color(0xFFC0261F)

    /**
     * El portaobjetos: la platina (gris en claro, grafito en oscuro) con sus dos pinzas arriba; el vidrio claro, o ahumado en
     * oscuro, con su brillo, el extremo esmerilado con dos rayas de lápiz y la escala micrométrica abajo; con varios renglones,
     * una línea punteada tenue por renglón. Con el foco el canto del vidrio se tiñe de cian y se enciende la luz de la platina
     * debajo; con un error el canto va en rojo y aparece el rombo de riesgo en el esmerilado.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun ap(x: Color) = if (e.habilitado) x else mezcla(x, Color(0xFF8C9296), 0.35f)
        // La platina.
        drawRoundRect(ap(if (oscuro) Color(0xFF20262C) else GRIS), Offset.Zero, size, CornerRadius(5 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset.Zero, size, CornerRadius(5 * u), style = Stroke(1 * u))
        // El vidrio.
        val vi = Rect(3 * u, 6 * u, w - 3 * u, h - 5 * u)
        val vidrio = ap(if (oscuro) Color(0xFF16222A) else Color(0xFFF4FAFB))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(vi.left + 0.8f * u, vi.top + 1.4f * u), vi.size)
        drawRect(vidrio, vi.topLeft, vi.size)
        val es = Rect(vi.left, vi.top, vi.left + 9 * u, vi.bottom)
        if (e.enfocado && e.habilitado) drawRect(CIAN.copy(alpha = if (oscuro) 0.1f else 0.07f), Offset(es.right, vi.top), Size(vi.right - es.right, vi.height))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.14f else 0.9f), Offset(vi.left + 2 * u, vi.top + 1.4f * u), Offset(vi.right - 2 * u, vi.top + 1.4f * u), 0.8f * u)
        // El extremo esmerilado, con sus granos y dos rayas de lápiz.
        drawRect(ap(if (oscuro) Color(0xFF2C3840) else Color(0xFFE2E8EA)), es.topLeft, es.size)
        val granos = (es.height / u * 0.8f).toInt().coerceIn(10, 120)
        for (i in 0 until granos) drawCircle(Color.Black.copy(alpha = 0.06f), 0.6f * u, Offset(es.left + es.width * azar(61 + e.semilla, i), es.top + es.height * azar(67 + e.semilla, i)))
        val lapiz = if (oscuro) Color(0xFF8C98A0) else GRIS_OSC
        val ym = es.top + minOf(es.height / 2, 14 * u)
        drawLine(lapiz.copy(alpha = 0.7f), Offset(es.left + 2 * u, ym - 2.5f * u), Offset(es.right - 2 * u, ym - 3 * u), 0.9f * u, StrokeCap.Round)
        drawLine(lapiz.copy(alpha = 0.7f), Offset(es.left + 2 * u, ym + 1 * u), Offset(es.right - 3.5f * u, ym + 1 * u), 0.9f * u, StrokeCap.Round)
        if (e.error) {
            val o = Offset(es.center.x, minOf(ym + 9 * u, es.bottom - 4 * u)); val d = 3.2f * u
            drawPath(Path().apply { moveTo(o.x, o.y - d); lineTo(o.x + d, o.y); lineTo(o.x, o.y + d); lineTo(o.x - d, o.y); close() }, errorCampo(oscuro), style = Stroke(1 * u))
        }
        // Las líneas de los renglones.
        if (e.multilinea) renglones(margenCampoY) { _, y ->
            drawLine(CIAN.copy(alpha = if (oscuro) 0.2f else 0.28f), Offset(es.right + 5 * u, y), Offset(vi.right - 5 * u, y), 0.7f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * u, 2 * u)))
        }
        // La escala micrométrica del canto de abajo.
        val paso = maxOf(2.4f * u, 1f)
        var x = es.right + 3 * u
        var k = 0
        val marca = (if (oscuro) Color(0xFF8C98A0) else GRIS_OSC).copy(alpha = 0.45f)
        while (x < vi.right - 3 * u && k < 2000) {
            val largo = if (k % 10 == 0) 3 * u else if (k % 5 == 0) 2.2f * u else 1.3f * u
            drawLine(marca, Offset(x, vi.bottom - 0.6f * u), Offset(x, vi.bottom - 0.6f * u - largo), 0.5f * u)
            x += paso; k++
        }
        // El canto del vidrio.
        val canto = when {
            e.error -> errorCampo(oscuro)
            e.enfocado && e.habilitado -> CIAN
            else -> Color(0xFF9FB4BE).let { if (oscuro) mezcla(it, Color(0xFF16222A), 0.4f) else it }
        }
        drawRect(canto, vi.topLeft, vi.size, style = Stroke((if (e.enfocado || e.error) 1.6f else 1f) * u))
        // Las pinzas de la platina.
        listOf(es.right + 2 * u, vi.right - 9 * u).forEach { xp ->
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(xp - 2.5f * u + 0.8f * u, 0f), Size(5 * u, 9.5f * u), CornerRadius(1.8f * u))
            drawRoundRect(ap(if (oscuro) Color(0xFF4A5560) else Color(0xFF8C98A2)), Offset(xp - 2.5f * u, 0f), Size(5 * u, 9 * u), CornerRadius(1.8f * u))
            drawCircle(Color.White.copy(alpha = 0.35f), 1 * u, Offset(xp, 2.6f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La línea de la escala: un filo gris de lado a lado y, al centro, un tramo de escala micrométrica (una marca cada 3 dp,
     * larga cada cinco, en un solo trazo) con su punto cian de referencia. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2 + 1.5f * u
        val a = if (e.tenue) 0.5f else 1f
        val gris = (if (oscuro) Color(0xFF8C98A0) else GRIS_OSC).copy(alpha = 0.5f * a)
        drawLine(gris, Offset(0f, y), Offset(w, y), 0.8f * u)
        // La escala del centro.
        val cx = w / 2
        val paso = maxOf(3 * u, 1f)
        val n = ((minOf(48 * u, w * 0.3f) / paso).toInt() / 2).coerceIn(1, 10)
        val escala = Path()
        for (k in -n..n) {
            val x = cx + k * paso
            escala.moveTo(x, y); escala.lineTo(x, y - (if (k % 5 == 0) 3.4f else 1.8f) * u)
        }
        drawPath(escala, gris, style = Stroke(0.6f * u))
        drawCircle((if (oscuro) Color(0xFF3CD2E5) else CIAN).copy(alpha = 0.9f * a), 1.2f * u, Offset(cx, y - 4.8f * u))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF3CD2E5) else Color(0xFF0B7180)

    /**
     * La línea de cota del dibujo técnico, bajo el texto: la raya con sus dos topes y sus dos flechas. Al presionarlo la cota
     * se marca y el botón se tiñe de cian, redondeado como los frascos del tema; apagado, la cota es gris y sin flechas.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val y = h - 10 * u
        val izq = 6 * u; val der = w - 6 * u
        val tinta = if (e.habilitado) tintaBotonTexto(oscuro) else Color(0xFF8C9296)
        if (e.presionado && e.habilitado) drawRoundRect(tinta.copy(alpha = if (oscuro) 0.12f else 0.09f), Offset(3 * u, 6 * u), Size(w - 6 * u, h - 12 * u), CornerRadius(10 * u))
        val c = tinta.copy(alpha = when { !e.habilitado -> 0.35f; e.presionado -> 0.95f; else -> 0.5f })
        val g = (if (e.presionado) 1.1f else 0.8f) * u
        // La raya de cota con sus topes.
        drawLine(c, Offset(izq, y), Offset(der, y), g)
        listOf(izq, der).forEach { drawLine(c, Offset(it, y - 2.6f * u), Offset(it, y + 2.6f * u), g) }
        // Las flechas, hacia los topes.
        if (e.habilitado) {
            val f = 2.8f * u
            drawPath(Path().apply {
                moveTo(izq, y); lineTo(izq + f, y - 1.2f * u); lineTo(izq + f, y + 1.2f * u); close()
                moveTo(der, y); lineTo(der - f, y - 1.2f * u); lineTo(der - f, y + 1.2f * u); close()
            }, c)
        }
    }
}

/**
 * Colonos: las piezas comunes del almacén. El campo es la pizarra del almacén, como su buscador: la pizarra en su marco de
 * pino con sus clavos y, abajo, el canto ancho donde descansa la tiza.
 */
object ComunColonos : DisenoComun {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val TIZA = Color(0xFFF2EEE2)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 15.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = TIZA
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFFA3A69C)
    override fun rotuloCampo(oscuro: Boolean): Color = Color(0xFFE8C877)
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFEE8A70)

    /**
     * La pizarra: el marco de pino con sus vetas, sus clavos y el canto de abajo más ancho, con el polvo de tiza; la pizarra
     * con los borrones de lo de antes (y una raya de tiza tenue por renglón si son varios). Con el foco aparece el trozo de tiza
     * en el canto y el borde de la pizarra se aclara; con un error, la tiza es de almagre y el borde va en ese rojo; apagada, la
     * pizarra queda velada de polvo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF8A8478), 0.35f) }
        // El marco de pino.
        drawRect(c(PINO), Offset.Zero, size)
        vetas(Rect(0f, 0f, w, h), c(VETA).copy(alpha = 0.32f), 41 + e.semilla % 7, densidad = 0.6f)
        val canto = 7.5f * u
        val pz = Rect(5 * u, 5 * u, w - 5 * u, h - canto)
        // El canto de abajo: su luz arriba y su sombra al pie.
        drawRect(c(PINO_LUZ), Offset(0f, pz.bottom + 1 * u), Size(w, 1.2f * u))
        drawRect(c(PINO_OSC), Offset(0f, h - 1.2f * u), Size(w, 1.2f * u))
        // La pizarra.
        drawRect(Color.Black.copy(alpha = 0.35f), pz.inflate(1 * u).topLeft, pz.inflate(1 * u).size)
        val pizarra = if (oscuro) Color(0xFF1E2522) else Color(0xFF2F3A35)
        drawRect(pizarra, pz.topLeft, pz.size)
        // Los borrones de lo que se escribió antes.
        val borrones = (pz.width / (70 * u)).toInt().coerceIn(2, 10)
        for (i in 0 until borrones) {
            val bw = pz.width * (0.12f + 0.08f * azar(e.semilla + 83, i))
            drawOval(TIZA.copy(alpha = 0.022f), Offset(pz.left + (pz.width - bw) * (i + azar(e.semilla + 89, i)) / borrones, pz.top + pz.height * 0.15f * azar(e.semilla + 97, i)),
                Size(bw, minOf(pz.height * 0.55f, 22 * u)))
        }
        if (e.multilinea) renglones(margenCampoY) { _, y -> drawLine(TIZA.copy(alpha = 0.1f), Offset(pz.left + 6 * u, y), Offset(pz.right - 6 * u, y), 0.8f * u, StrokeCap.Round) }
        if (!e.habilitado) drawRect(TIZA.copy(alpha = 0.08f), pz.topLeft, pz.size)
        val roja = errorCampo(oscuro)
        when {
            e.error -> drawRect(roja.copy(alpha = 0.85f), pz.topLeft, pz.size, style = Stroke(1.3f * u))
            e.enfocado -> drawRect(TIZA.copy(alpha = 0.6f), pz.topLeft, pz.size, style = Stroke(1.2f * u))
        }
        // El polvo de tiza en el canto.
        for (i in 0 until 9) drawCircle(TIZA.copy(alpha = 0.18f), (0.5f + 0.6f * azar(e.semilla + 101, i)) * u, Offset(pz.left + pz.width * azar(e.semilla + 103, i), pz.bottom + 3 * u + 2 * u * azar(e.semilla + 107, i)))
        // El trozo de tiza (o de almagre), con el foco o con el error.
        if ((e.enfocado || e.error) && e.habilitado) {
            val ti = Rect(w - 38 * u, pz.bottom + 1.6f * u, w - 23 * u, pz.bottom + 5.6f * u)
            drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(ti.left + 0.6f * u, ti.top + 0.8f * u), ti.size, CornerRadius(2 * u))
            drawRoundRect(if (e.error) roja else Color(0xFFF7F4EA), ti.topLeft, ti.size, CornerRadius(2 * u))
            drawRoundRect(Color.Black.copy(alpha = 0.15f), Offset(ti.left, ti.bottom - 1.2f * u), Size(ti.width, 1.2f * u), CornerRadius(1 * u))
        }
        // Los clavos de las esquinas.
        listOf(Offset(2.5f * u, 2.5f * u), Offset(w - 2.5f * u, 2.5f * u), Offset(2.5f * u, h - 3.5f * u), Offset(w - 2.5f * u, h - 3.5f * u)).forEach {
            drawCircle(c(CLAVO), 1.1f * u, it)
            drawCircle(Color.White.copy(alpha = 0.25f), 0.4f * u, Offset(it.x - 0.3f * u, it.y - 0.3f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El cordel del almacén: las hebras torcidas en diagonal (todas en un solo trazo) de punta a punta, con el nudo al centro y
     * sus dos cabos colgando. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cuerda = (if (oscuro) Color(0xFFA8865A) else PINO_OSC).copy(alpha = (if (oscuro) 0.6f else 0.7f) * a)
        val cx = w / 2
        val nudo = 5 * u
        // Las hebras, a los dos lados del nudo.
        val paso = maxOf(2.6f * u, 1f)
        val d = 1.1f * u
        val hebras = Path()
        listOf(4 * u to cx - nudo, cx + nudo to w - 4 * u).forEach { (x0, x1) ->
            var x = x0
            var n = 0
            while (x < x1 - 2 * u && n < 200) { hebras.moveTo(x, y + d); hebras.lineTo(x + 2 * u, y - d); x += paso; n++ }
        }
        drawPath(hebras, cuerda, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // El nudo, con sus dos cabos.
        drawOval(cuerda, Offset(cx - 3.5f * u, y - 2.2f * u), Size(7 * u, 4.4f * u), style = Stroke(1.1f * u))
        drawLine(cuerda, Offset(cx + 1.5f * u, y + 1.8f * u), Offset(cx + 3.5f * u, y + 4.2f * u), 0.9f * u, StrokeCap.Round)
        drawLine(cuerda, Offset(cx - 1.5f * u, y + 1.8f * u), Offset(cx - 3 * u, y + 4.4f * u), 0.9f * u, StrokeCap.Round)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3C27A) else Color(0xFF1F3A5F)

    /**
     * El vale del almacén: el contorno de papel con las cuatro esquinas comidas, en raya de recortar. Al presionarlo el vale se
     * llena de papel de estraza y el contorno se vuelve continuo; apagado, queda un contorno gris muy tenue.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 7 * u, w - 3 * u, h - 7 * u)
        val k = 3.2f * u
        val vale = Path().apply {
            moveTo(r.left + k, r.top); lineTo(r.right - k, r.top); quadraticTo(r.right - k, r.top + k, r.right, r.top + k)
            lineTo(r.right, r.bottom - k); quadraticTo(r.right - k, r.bottom - k, r.right - k, r.bottom)
            lineTo(r.left + k, r.bottom); quadraticTo(r.left + k, r.bottom - k, r.left, r.bottom - k)
            lineTo(r.left, r.top + k); quadraticTo(r.left + k, r.top + k, r.left + k, r.top); close()
        }
        val lleno = e.presionado && e.habilitado
        val tinta = if (!e.habilitado) Color(0xFF8A8478) else if (oscuro) Color(0xFFC9A66A) else PINO_OSC
        if (lleno) drawPath(vale, (if (oscuro) Color(0xFFE3C27A) else PINO_LUZ).copy(alpha = if (oscuro) 0.1f else 0.25f))
        drawPath(vale, tinta.copy(alpha = when { !e.habilitado -> 0.3f; lleno -> 0.95f; else -> 0.55f }),
            style = Stroke((if (lleno) 1.2f else 0.9f) * u, pathEffect = if (lleno) null else PathEffect.dashPathEffect(floatArrayOf(3 * u, 1.6f * u))))
    }
}

/**
 * Ciudad verde: las piezas comunes de las ruinas. El campo es una tira de cinta pegada en el muro de hormigón rajado, como
 * su buscador: plateada y escrita con rotulador negro en claro; en oscuro, cinta de tela negra escrita con marcador de
 * pintura. Tiene las puntas rotas a mano, sus arrugas, una mata de musgo en la esquina y una hoja de hiedra que asoma.
 */
object ComunRuinas : DisenoComun {
    private val HORMIGON = Color(0xFF8C8D84)
    private val HORMIGON_OSC = Color(0xFF65665E)
    private val MUSGO = Color(0xFF55722A)
    private val MUSGO_LUZ = Color(0xFF86A33C)
    private val HOJA = Color(0xFF3D6A2A)
    private val LUCIERNAGA = Color(0xFFE6FF7A)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0A), 0.36f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E8E0) else Color(0xFF16181A)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9A9D95) else Color(0xFF51544F)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFA4C052) else Color(0xFF3A5618)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFEC8A5A) else Color(0xFF8C3416)

    /**
     * La cinta en el muro: el hormigón con dos grietas, la cinta con sus puntas rotas en zigzag, su brillo de arriba, sus
     * arrugas tenues y, si son varios renglones, el traslape de una tira con la siguiente en cada renglón; el musgo abajo a la
     * derecha y la hoja de hiedra arriba a la izquierda. Con el foco se posa una luciérnaga en la esquina de arriba y el canto
     * de la cinta se marca en verde musgo; con un error, una mancha de óxido escurre desde arriba y el canto va en óxido.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF7E7E7A), 0.35f) }
        // El muro con sus grietas.
        drawRect(c(HORMIGON))
        val s = e.semilla
        val g1 = w * (0.3f + 0.4f * azar(s, 1))
        drawPath(Path().apply { moveTo(g1, 0f); lineTo(g1 - 2 * u, 3 * u); lineTo(g1 + 1.5f * u, 5 * u) }, c(HORMIGON_OSC), style = Stroke(0.9f * u))
        val g2 = w * (0.15f + 0.5f * azar(s, 2))
        drawPath(Path().apply { moveTo(g2, h); lineTo(g2 + 1.5f * u, h - 2.5f * u); lineTo(g2 - 1 * u, h - 4.5f * u) }, c(HORMIGON_OSC), style = Stroke(0.9f * u))
        // La cinta, con sus puntas rotas.
        val ci = Rect(4 * u, 4.5f * u, w - 4 * u, h - 4.5f * u)
        val dientes = (ci.height / (5 * u)).toInt().coerceIn(4, 60)
        val cinta = Path().apply {
            moveTo(ci.left + 2 * u, ci.top)
            lineTo(ci.right - 2 * u, ci.top)
            for (i in 1..dientes) lineTo(ci.right + (if (i % 2 == 0) -2f else 0.5f) * u * (0.6f + 0.8f * azar(s + 5, i)), ci.top + ci.height * i / dientes)
            lineTo(ci.left + 2 * u, ci.bottom)
            for (i in dientes - 1 downTo 0) lineTo(ci.left + (if (i % 2 == 0) 2f else -0.5f) * u * (0.6f + 0.8f * azar(s + 7, i)), ci.top + ci.height * i / dientes)
            close()
        }
        drawPath(cinta, Color.Black.copy(alpha = 0.28f), style = Stroke(1.8f * u))
        val tela = if (oscuro) Color(0xFF2C2E2C) else Color(0xFFC9CDC7)
        drawPath(cinta, if (e.habilitado) tela else mezcla(tela, Color(0xFF8A8C88), 0.3f))
        clipPath(cinta) {
            // Las arrugas, tenues, y el brillo de arriba.
            val arrugas = (ci.width / (46 * u)).toInt().coerceIn(2, 14)
            for (i in 0 until arrugas) {
                val x = ci.left + ci.width * (i + 0.3f + 0.4f * azar(s + 11, i)) / arrugas
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.22f), Offset(x, ci.top), Offset(x + 6 * u, ci.top + minOf(ci.height, 40 * u)), 0.8f * u)
                drawLine(Color.Black.copy(alpha = 0.06f), Offset(x + 2 * u, ci.top), Offset(x + 8 * u, ci.top + minOf(ci.height, 40 * u)), 0.8f * u)
            }
            drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.4f), Offset(ci.left, ci.top + 1 * u), Offset(ci.right, ci.top + 1 * u), 0.8f * u)
            // El traslape de las tiras, uno por renglón.
            if (e.multilinea) renglones(margenCampoY) { k, y ->
                // La primera raya sería el pie de la cinta: solo los traslapes entre renglones.
                if (k > 0) {
                    drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(ci.left, y), Offset(ci.right, y + 0.6f * u), 1 * u)
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.07f else 0.3f), Offset(ci.left, y + 1 * u), Offset(ci.right, y + 1.6f * u), 0.7f * u)
                }
            }
            // La mancha de óxido que escurre, con el error.
            if (e.error) {
                val ox = ci.right - 26 * u
                val roja = errorCampo(oscuro).copy(alpha = 0.32f)
                drawOval(roja, Offset(ox - 6 * u, ci.top - 3 * u), Size(14 * u, 7 * u))
                listOf(0f to 0.55f, 4f to 0.35f, 7f to 0.7f).forEach { (dx, f) ->
                    drawLine(roja, Offset(ox + dx * u - 3 * u, ci.top), Offset(ox + dx * u - 3 * u, ci.top + minOf(ci.height * f, 30 * u)), 1.4f * u, StrokeCap.Round)
                }
            }
        }
        when {
            e.error -> drawPath(cinta, errorCampo(oscuro), style = Stroke(1.3f * u))
            e.enfocado -> drawPath(cinta, rotuloCampo(oscuro), style = Stroke(1.2f * u))
        }
        // La hoja de hiedra que asoma arriba a la izquierda.
        listOf(Triple(Offset(1.5f * u, 2 * u), 30f, 10f), Triple(Offset(9 * u, 0.5f * u), 70f, 6.5f)).forEach { (ho, giro, largo) ->
            rotate(giro, ho) {
                val l = largo * u
                drawPath(Path().apply { moveTo(ho.x, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y - l * 0.42f, ho.x + l, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y + l * 0.42f, ho.x, ho.y); close() }, c(HOJA))
                drawLine(c(MUSGO_LUZ).copy(alpha = 0.6f), ho, Offset(ho.x + l * 0.85f, ho.y), 0.5f * u)
            }
        }
        // La mata de musgo abajo a la derecha.
        val m = Offset(w - 5 * u, h - 1.5f * u)
        for (i in 0 until 13) {
            val a = (190f + 160f * azar(s + 13, i)) * (Math.PI / 180f).toFloat()
            val d = (1 + 6 * azar(s + 17, i)) * u
            drawCircle(c(if (i % 3 == 0) MUSGO_LUZ else MUSGO), (1.3f + 1.5f * azar(s + 19, i)) * u, Offset(m.x + d * kotlin.math.cos(a), m.y + d * kotlin.math.sin(a) * 0.55f))
        }
        // La luciérnaga que se posa, con el foco.
        if (e.enfocado && e.habilitado) {
            val l = Offset(w - 9 * u, 3 * u)
            mancha(l, 9 * u, LUCIERNAGA.copy(alpha = 0.45f))
            drawCircle(LUCIERNAGA.copy(alpha = 0.7f), 2 * u, l)
            drawCircle(Color(0xFFF8FFD0), 1 * u, l)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * El tallo de hiedra que corre por el muro: ondulado, de punta a punta, con tres hojas alternadas al centro. Tenue, a la
     * mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val tallo = (if (oscuro) Color(0xFF6E8A3A) else MUSGO).copy(alpha = 0.6f * a)
        val hoja = (if (oscuro) Color(0xFF55813A) else HOJA).copy(alpha = 0.85f * a)
        // El tallo, ondulado.
        val medio = maxOf(14 * u, 1f)
        val amp = 1.3f * u
        val p = Path().apply {
            moveTo(4 * u, y)
            var x = 4 * u
            var k = 0
            while (x < w - 4 * u && k < 100) {
                val x2 = minOf(x + medio, w - 4 * u)
                quadraticTo((x + x2) / 2, y + 2 * (if (k % 2 == 0) -amp else amp), x2, y)
                x = x2; k++
            }
        }
        drawPath(p, tallo, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // Las tres hojas del centro.
        val cx = w / 2
        listOf(Triple(Offset(cx - 11 * u, y), 145f, 4.8f), Triple(Offset(cx, y - 0.6f * u), -40f, 5.2f), Triple(Offset(cx + 12 * u, y), 35f, 4.5f)).forEach { (ho, giro, largo) ->
            rotate(giro, ho) {
                val l = largo * u
                drawPath(Path().apply { moveTo(ho.x, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y - l * 0.42f, ho.x + l, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y + l * 0.42f, ho.x, ho.y); close() }, hoja)
            }
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FCB6A) else Color(0xFF355E23)

    /**
     * Un trozo de la cinta del campo, pegado detrás del texto: más bajo que el botón, con las puntas rotas a mano, su brillo de
     * arriba y una pizca de musgo en la esquina. Al presionarlo la cinta se aprieta contra el muro (más opaca, con el canto en
     * verde musgo); apagada, apenas se ve y no tiene musgo.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val alto = minOf(h - 16 * u, 24 * u)
        val ci = Rect(3 * u, (h - alto) / 2, w - 3 * u, (h + alto) / 2)
        val dientes = 4
        val cinta = Path().apply {
            moveTo(ci.left + 1.5f * u, ci.top)
            lineTo(ci.right - 1.5f * u, ci.top)
            for (i in 1..dientes) lineTo(ci.right + (if (i % 2 == 0) -1.5f else 0.5f) * u * (0.6f + 0.8f * azar(s + 5, i)), ci.top + ci.height * i / dientes)
            lineTo(ci.left + 1.5f * u, ci.bottom)
            for (i in dientes - 1 downTo 0) lineTo(ci.left + (if (i % 2 == 0) 1.5f else -0.5f) * u * (0.6f + 0.8f * azar(s + 7, i)), ci.top + ci.height * i / dientes)
            close()
        }
        val tela = if (oscuro) Color(0xFF2C2E2C) else Color(0xFFC9CDC7)
        val opaca = when { !e.habilitado -> if (oscuro) 0.28f else 0.16f; e.presionado -> if (oscuro) 1f else 0.8f; else -> if (oscuro) 0.6f else 0.35f }
        drawPath(cinta, tela.copy(alpha = opaca))
        drawLine(Color.White.copy(alpha = (if (oscuro) 0.08f else 0.35f) * opaca), Offset(ci.left + 2 * u, ci.top + 1 * u), Offset(ci.right - 2 * u, ci.top + 1 * u), 0.8f * u)
        if (!e.habilitado) return
        if (e.presionado) drawPath(cinta, rotuloCampo(oscuro).copy(alpha = 0.8f), style = Stroke(1 * u))
        // La pizca de musgo, abajo a la derecha.
        val m = Offset(ci.right - 3 * u, ci.bottom)
        for (i in 0 until 5) {
            val dx = (-4 + 6 * azar(s + 13, i)) * u
            drawCircle(t(if (i % 2 == 0) MUSGO_LUZ else MUSGO, oscuro), (0.9f + 0.7f * azar(s + 19, i)) * u, Offset(m.x + dx, m.y - 0.8f * u * azar(s + 17, i)))
        }
    }
}

/**
 * Espacio: las piezas comunes de la nave. El campo es el visor de exploración del casco, como su buscador: el cristal
 * oscuro ochavado en su marco de panel, con los corchetes de las cuatro esquinas y las tres luces de estado del panel.
 */
object ComunEspacio : DisenoComun {
    private val PANEL = Color(0xFFD6DCE5)
    private val PANEL_OSC = Color(0xFF9AA4B4)
    private val CIAN = Color(0xFF5CE1FF)
    private val VERDE = Color(0xFF5CFFB0)
    private val AMBAR = Color(0xFFFFB84A)
    private val CRISTAL = Color(0xFF070C18)

    /** El color en modo oscuro, con las luces de la nave bajas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF05070D), 0.62f) else c

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = Color(0xFFE8F4FF)
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF8396AC)
    override fun rotuloCampo(oscuro: Boolean): Color = CIAN
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF7088)

    /**
     * El visor: el marco de panel ochavado (claro en claro, de casco en oscuro) con las tres luces de estado arriba a la
     * derecha; el cristal oscuro ochavado con su fila de puntos de exploración al pie (y una raya tenue por renglón si son
     * varios) y los corchetes de las esquinas. Con el foco los corchetes se encienden en cian, la luz verde se prende y una
     * línea de exploración cruza el pie del cristal; con un error los corchetes y la primera luz van en rojo de alarma.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF6E737A), 0.4f) }
        val todo = ochavado(Rect(0f, 0f, w, h), 8 * u)
        drawPath(todo, c(PANEL))
        drawPath(todo, c(PANEL_OSC), style = Stroke(1 * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), Offset(9 * u, 1.2f * u), Offset(w - 9 * u, 1.2f * u), 0.8f * u)
        val cr = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        val cristal = ochavado(cr, 6 * u)
        drawPath(cristal, CRISTAL)
        val vivo = e.enfocado && e.habilitado
        clipPath(cristal) {
            drawRect(Color.Black.copy(alpha = 0.5f), cr.topLeft, Size(cr.width, 1.8f * u))
            if (vivo) drawRect(CIAN.copy(alpha = 0.05f), cr.topLeft, cr.size)
            if (e.multilinea) renglones(margenCampoY) { k, y -> if (k > 0) drawLine(CIAN.copy(alpha = 0.08f), Offset(cr.left + 12 * u, y), Offset(cr.right - 12 * u, y), 0.7f * u) }
        }
        // La fila de puntos de exploración, al pie del cristal; con el foco, la línea que la cruza.
        val yp = cr.bottom - 2.6f * u
        val paso = maxOf(8 * u, 1f)
        var x = cr.left + 14 * u
        var k = 0
        while (x < cr.right - 12 * u && k < 500) { drawCircle(CIAN.copy(alpha = if (vivo) 0.3f else 0.14f), 0.6f * u, Offset(x, yp)); x += paso; k++ }
        if (vivo) drawLine(CIAN.copy(alpha = 0.45f), Offset(cr.left + 12 * u, yp), Offset(cr.right - 12 * u, yp), 0.7f * u)
        // Los corchetes de las esquinas.
        val col = when {
            !e.habilitado -> CIAN.copy(alpha = 0.18f)
            e.error -> errorCampo(oscuro)
            vivo -> CIAN
            else -> CIAN.copy(alpha = 0.38f)
        }
        val l = 8 * u
        val gr = (if (vivo || e.error) 1.6f else 1.2f) * u
        listOf(Offset(cr.left + 3 * u, cr.top + 3 * u) to Offset(1f, 1f), Offset(cr.right - 3 * u, cr.top + 3 * u) to Offset(-1f, 1f),
            Offset(cr.left + 3 * u, cr.bottom - 3 * u) to Offset(1f, -1f), Offset(cr.right - 3 * u, cr.bottom - 3 * u) to Offset(-1f, -1f)).forEach { (o, d) ->
            val p = Path().apply { moveTo(o.x + d.x * l, o.y); lineTo(o.x + d.x * 2.5f * u, o.y); lineTo(o.x, o.y + d.y * 2.5f * u); lineTo(o.x, o.y + d.y * l * 0.75f) }
            drawPath(p, col, style = Stroke(gr, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        }
        // Las tres luces de estado del panel, arriba a la derecha.
        val luces = listOf(
            if (e.error) errorCampo(oscuro) else VERDE,
            AMBAR,
            CIAN,
        )
        luces.forEachIndexed { i, cl ->
            val o = Offset(w - 30 * u + i * 5 * u, 2 * u)
            val prendida = e.habilitado && when (i) { 0 -> vivo || e.error; 1 -> false; else -> true }
            drawCircle(if (prendida) cl else mezcla(cl, Color(0xFF1A1E26), 0.7f), 0.95f * u, o)
            if (prendida) drawCircle(cl.copy(alpha = 0.3f), 2 * u, o)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La línea del visor: un trazo fino con un tope en cada punta, cortado al centro por una órbita inclinada con su planeta
     * ámbar y su satélite. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val azul = if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)
        val linea = azul.copy(alpha = 0.42f * a)
        val cx = w / 2
        val hueco = 11 * u
        val p = Path().apply {
            moveTo(4 * u, y); lineTo(cx - hueco, y); moveTo(cx + hueco, y); lineTo(w - 4 * u, y)
            // Los topes de las puntas.
            moveTo(4 * u, y - 2 * u); lineTo(4 * u, y + 2 * u); moveTo(w - 4 * u, y - 2 * u); lineTo(w - 4 * u, y + 2 * u)
        }
        drawPath(p, linea, style = Stroke(0.8f * u))
        // La órbita, el planeta y el satélite.
        val giro = -12f
        rotate(giro, Offset(cx, y)) { drawOval(linea, Offset(cx - 8 * u, y - 2.6f * u), Size(16 * u, 5.2f * u), style = Stroke(0.7f * u)) }
        drawCircle((if (oscuro) AMBAR else Color(0xFFE8601C)).copy(alpha = 0.9f * a), 1.8f * u, Offset(cx, y))
        val th = Math.toRadians(205.0); val g = Math.toRadians(giro.toDouble())
        val ex = 8 * u * kotlin.math.cos(th).toFloat(); val ey = 2.6f * u * kotlin.math.sin(th).toFloat()
        val sat = Offset(cx + ex * kotlin.math.cos(g).toFloat() - ey * kotlin.math.sin(g).toFloat(), y + ex * kotlin.math.sin(g).toFloat() + ey * kotlin.math.cos(g).toFloat())
        drawCircle(azul.copy(alpha = a), 0.9f * u, sat)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)

    /**
     * Los corchetes del visor en las cuatro esquinas, como en el campo. Al presionarlo se cierran sobre el texto (fijan el
     * blanco), se encienden y el recuadro se tiñe; apagado, quedan muy tenues y grises.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fijo = e.presionado && e.habilitado
        val tinta = if (e.habilitado) tintaBotonTexto(oscuro) else Color(0xFF8A93A3)
        val dentro = if (fijo) 2.5f * u else 0f
        val r = Rect(3 * u + dentro, 6 * u + dentro, w - 3 * u - dentro, h - 6 * u - dentro)
        if (fijo) drawRect(tinta.copy(alpha = if (oscuro) 0.12f else 0.08f), r.topLeft, r.size)
        val l = minOf(6 * u, r.width * 0.3f, r.height * 0.35f)
        val esquinas = Path().apply {
            moveTo(r.left, r.top + l); lineTo(r.left, r.top); lineTo(r.left + l, r.top)
            moveTo(r.right - l, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + l)
            moveTo(r.right, r.bottom - l); lineTo(r.right, r.bottom); lineTo(r.right - l, r.bottom)
            moveTo(r.left + l, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.bottom - l)
        }
        drawPath(esquinas, tinta.copy(alpha = when { !e.habilitado -> 0.3f; fijo -> 1f; else -> 0.55f }),
            style = Stroke((if (fijo) 1.4f else 1f) * u, cap = StrokeCap.Square, join = StrokeJoin.Miter))
    }
}
