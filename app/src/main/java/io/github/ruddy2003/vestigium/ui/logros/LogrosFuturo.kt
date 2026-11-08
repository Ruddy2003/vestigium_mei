package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Futuro
// =====================================================================

/** El gris translúcido de lo que aún no se gana: la misma silueta tenue que la figura sin ganar. */
private fun siluetaFuturo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE4E4E4).copy(alpha = 0.13f) else Color(0xFF1E1E1E).copy(alpha = 0.13f)

/** Un rectángulo dado en fracciones del lienzo. */
private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)

/** Un punto dado en fracciones del lienzo. */
private fun DrawScope.pt(x: Float, y: Float) = Offset(size.width * x, size.height * y)

/** Un polígono cerrado con sus vértices en fracciones del lienzo: x0, y0, x1, y1… */
private fun DrawScope.poli(vararg f: Float): Path = Path().apply {
    var k = 0
    while (k + 1 < f.size) { val x = size.width * f[k]; val y = size.height * f[k + 1]; if (k == 0) moveTo(x, y) else lineTo(x, y); k += 2 }
    close()
}

/** La sombra al pie del pedestal, de [x0] a [x1] (fracciones del ancho): dos óvalos bajos, el de adentro más hondo. */
private fun DrawScope.sombraPie(x0: Float, x1: Float, oscuro: Boolean, y: Float = 0.955f) {
    val w = size.width; val h = size.height
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.32f else 0.15f), Offset(w * x0, h * (y - 0.012f)), Size(w * (x1 - x0), h * 0.03f))
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.1f), Offset(w * (x0 + 0.05f), h * (y - 0.006f)), Size(w * (x1 - x0 - 0.1f), h * 0.016f))
}

/**
 * Una chapa en el metal del nivel: la base, el filo de luz arriba, la sombra abajo y su contorno oscuro. La legendaria
 * (cristal, [EstadoPedestal.nivel] 4) lleva además su faceta blanca en diagonal.
 */
private fun DrawScope.chapaNivel(r: Rect, e: EstadoPedestal, radio: Float = 0f) {
    if (r.width < 1f || r.height < 1f) return
    val u = density
    val rr = radio.coerceAtMost(minOf(r.width, r.height) / 2)
    drawRoundRect(e.base, r.topLeft, r.size, CornerRadius(rr))
    val filo = (r.height * 0.24f).coerceIn(minOf(0.5f * u, r.height * 0.4f), r.height * 0.4f)
    drawRect(e.brillo, Offset(r.left + rr * 0.6f, r.top + filo * 0.25f), Size(r.width - rr * 1.2f, filo))
    drawRect(e.metalOscuro.copy(alpha = 0.5f), Offset(r.left + rr * 0.6f, r.bottom - filo), Size(r.width - rr * 1.2f, filo * 0.8f))
    drawRoundRect(e.metalOscuro, r.topLeft, r.size, CornerRadius(rr), style = Stroke(maxOf(minOf(r.width, r.height) * 0.08f, 0.45f * u)))
    if (e.nivel >= 4) drawLine(Color.White.copy(alpha = 0.85f), Offset(r.left + r.width * 0.58f, r.bottom - r.height * 0.18f),
        Offset(r.left + r.width * 0.7f, r.top + r.height * 0.18f), maxOf(r.height * 0.1f, 0.5f * u), StrokeCap.Round)
}

/** Un remache o un tornillo de cabeza redonda: el cuerpo y su punto de luz arriba a la izquierda. */
private fun DrawScope.remache(c: Offset, r: Float, cuerpo: Color, luz: Color) {
    drawCircle(cuerpo, r, c)
    drawCircle(luz, r * 0.42f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
}

/** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
    moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
    lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
}

/** Un tubo de neón por [p]: el halo ancho, el brillo, el color y el alma casi blanca (sin degradados). */
private fun DrawScope.tuboNeon(p: Path, color: Color, grosor: Float) {
    fun trazo(g: Float) = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(p, color.copy(alpha = 0.14f), style = trazo(grosor * 4f))
    drawPath(p, color.copy(alpha = 0.36f), style = trazo(grosor * 2.2f))
    drawPath(p, color, style = trazo(grosor))
    drawPath(p, mezcla(color, Color.White, 0.7f), style = trazo(grosor * 0.35f))
}

/**
 * La geometría del salón del hall, para todas las escenas de la época: el lienzo ([w] × [h], en píxeles, con [u] píxeles por
 * dp) se parte en [n] franjas de ancho [paso], una por columna de trofeos, y el trofeo va centrado en la suya ([cx]). La
 * arquitectura va en los bordes de las franjas ([borde]: x = i · paso, de 0 a n), con medio ancho [pil], que siempre queda
 * libre entre dos pedestales; arriba, el friso de alto [friso]; abajo, el piso desde [suelo]. Las cosas que se repiten por
 * filas usan [modulo] (≈ 230 dp, el alto de una fila del hall) y nunca más de [modulos] veces.
 */
private class GeoSalon(val w: Float, val h: Float, val u: Float, columnas: Int) {
    val n = columnas.coerceIn(1, 6)
    val paso = w / n
    val pil = minOf(paso * 0.12f, 15f * u)
    val friso = minOf(14f * u, h * 0.05f)
    val piso = minOf(16f * u, h * 0.06f)
    val suelo = h - piso
    val modulo = maxOf(230f * u, 1f)
    val modulos = ((h / modulo).toInt() + 1).coerceIn(1, 10)
    fun cx(i: Int) = (i + 0.5f) * paso
    fun borde(i: Int) = i * paso
    /** El vano de la franja [i] entre sus dos pilastras. */
    fun vano(i: Int) = Rect(borde(i) + pil, friso, borde(i + 1) - pil, suelo)
}

/**
 * Repite [f] a lo largo de [y0] a [y1] con [paso] (con su mínimo de un píxel y a lo más [tope] veces): las cosas que cuelgan
 * de las pilastras, los cajones, los tubos.
 */
private inline fun repetirY(y0: Float, y1: Float, paso: Float, tope: Int = 40, f: (k: Int, y: Float) -> Unit) {
    var y = y0; var k = 0
    val p = maxOf(paso, 1f)
    while (y < y1 && k < tope) { f(k, y); y += p; k++ }
}

/**
 * La sombra del marcador de campaña en el suelo, bajo [p]: un óvalo de [ancho] con su centro más hondo, que se achica y se
 * aclara cuando la figura brinca ([salto], de 0 a 1), porque el suelo no se mueve.
 */
private fun DrawScope.sombraMarcadorFuturo(p: Offset, ancho: Float, salto: Float, oscuro: Boolean) {
    val u = density
    val f = 1f - 0.35f * salto.coerceIn(0f, 1f)
    val a = ancho * f; val b = 3.2f * u * f
    drawOval(Color.Black.copy(alpha = (if (oscuro) 0.5f else 0.2f) * f), Offset(p.x - a / 2, p.y - b / 2), Size(a, b))
    drawOval(Color.Black.copy(alpha = (if (oscuro) 0.3f else 0.12f) * f), Offset(p.x - a * 0.3f, p.y - b * 0.3f), Size(a * 0.6f, b * 0.6f))
}

/**
 * El pie de la escena de victoria: la meta [m] subida [sobre] dp y corrida lo necesario para que una escena de [ancho] ×
 * [alto] (centrada en x, hacia arriba desde el pie) quepa en el lienzo con 2 dp de margen. Con la meta junto al borde derecho
 * la escena se corre a la izquierda; con la meta muy arriba, baja.
 */
private fun DrawScope.pieVictoriaFuturo(m: Offset, ancho: Float, alto: Float, sobre: Float = 2f): Offset {
    val u = density; val mg = 2f * u
    val x = if (size.width > ancho + 2 * mg) m.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
    val y = if (size.height > alto + 2 * mg) (m.y - sobre * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
    return Offset(x, y)
}

/**
 * Distopía: los logros del archivo central. El trofeo descansa en el zócalo de hormigón del archivo, y en el hall cada pieza
 * se expone sobre el cajón de un archivero, con su ficha del registro.
 */
object LogrosDistopia : DisenoLogros {
    private val ACERO = Color(0xFF6F7479)
    private val ACERO_OSC = Color(0xFF43474B)
    private val ACERO_LUZ = Color(0xFF9BA1A6)
    private val ROJO = Color(0xFFA8201C)
    private val NEGRO = Color(0xFF141516)
    private val PAPEL = Color(0xFFE9E4D6)
    /** El papel en oscuro: el papel carbón de la copia, hundido. */
    private val CARBON = Color(0xFF2A2C2F)
    private val HORMIGON = Color(0xFF9A9890)
    private val HORMIGON_OSC = Color(0xFF6C6A64)

    /** El color en oscuro: hundido hacia el negro del archivo. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0C0D), 0.4f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El zócalo del archivo: un bloque de hormigón vaciado, con las juntas del encofrado, sus poros y el número de inventario
     * estarcido; la franja roja de «clasificado» por su canto de arriba; encima, la plancha de acero con su cara de luz y sus
     * tornillos del nivel, y al pie la solera de acero. Al frente, el tarjetero en el metal del nivel con la ficha del registro.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fr(0.12f, 0.765f, 0.88f, 0.8f)
        val bloque = fr(0.16f, 0.8f, 0.84f, 0.935f)
        val solera = fr(0.13f, 0.935f, 0.87f, 0.955f)
        if (!e.hecho) {
            drawPath(Path().apply { addRect(tapa); addRect(bloque); addRect(solera) }, siluetaFuturo(oscuro)); return
        }
        sombraPie(0.08f, 0.92f, oscuro)
        // El hormigón vaciado: la cara de la derecha en sombra, el canto de la izquierda con luz y las juntas del encofrado.
        drawRect(t(HORMIGON, oscuro), bloque.topLeft, bloque.size)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(bloque.right - w * 0.05f, bloque.top), Size(w * 0.05f, bloque.height))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.14f), bloque.topLeft, Size(w * 0.025f, bloque.height))
        val junta = t(HORMIGON_OSC, oscuro).copy(alpha = 0.6f)
        drawLine(junta, pt(0.16f, 0.875f), pt(0.84f, 0.875f), lw)
        if (fino) {
            listOf(0.45f, 0.79f).forEach { x -> drawLine(junta, pt(x, 0.818f), pt(x, 0.935f), lw * 0.8f) }
            for (k in 0 until if (muyFino) 28 else 14) {
                val x = bloque.left + bloque.width * azar(e.semilla, k)
                val y = bloque.top + bloque.height * (0.18f + 0.78f * azar(e.semilla, k + 50))
                drawCircle(junta, (0.35f + 0.6f * azar(e.semilla, k + 90)) * u, Offset(x, y))
            }
        }
        // La franja roja de «clasificado».
        drawRect(t(ROJO, oscuro), bloque.topLeft, Size(bloque.width, h * 0.016f))
        // El número de inventario estarcido: cuatro cifras partidas por el puente de la plantilla.
        val num = t(NEGRO, oscuro).copy(alpha = if (oscuro) 0.8f else 0.62f)
        for (k in 0 until 4) {
            val x = w * (0.205f + k * 0.055f); val cw = w * 0.036f
            val hueca = azar(e.semilla, k + 7) > 0.45f
            if (hueca && fino) {
                drawRect(num, Offset(x, h * 0.887f), Size(cw, h * 0.017f), style = Stroke(cw * 0.3f))
                drawRect(num, Offset(x, h * 0.909f), Size(cw, h * 0.016f), style = Stroke(cw * 0.3f))
            } else {
                drawRect(num, Offset(x, h * 0.886f), Size(cw, h * 0.019f))
                drawRect(num, Offset(x, h * 0.909f), Size(cw, h * 0.017f))
            }
        }
        // El tarjetero del nivel con la ficha (a lienzo chico, solo la chapa).
        val tj = fr(0.52f, 0.826f, 0.77f, 0.914f)
        chapaNivel(tj, e, h * 0.004f)
        if (fino) {
            val ficha = Rect(tj.left + w * 0.016f, tj.top + h * 0.01f, tj.right - w * 0.016f, tj.bottom - h * 0.024f)
            drawRect(t(PAPEL, oscuro), ficha.topLeft, ficha.size)
            drawRect(t(ROJO, oscuro), ficha.topLeft, Size(ficha.width, ficha.height * 0.16f))
            for (k in 0 until 2) drawLine(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(ficha.left + ficha.width * 0.12f, ficha.top + ficha.height * (0.45f + k * 0.28f)),
                Offset(ficha.right - ficha.width * (0.12f + k * 0.28f), ficha.top + ficha.height * (0.45f + k * 0.28f)), lw * 0.8f)
            // El labio del tarjetero, que sujeta la ficha por abajo.
            drawRect(e.base, Offset(tj.left, ficha.bottom), Size(tj.width, tj.bottom - ficha.bottom))
            drawRect(e.brillo, Offset(tj.left, ficha.bottom), Size(tj.width, maxOf(h * 0.003f, 0.45f * u)))
            drawRect(e.metalOscuro, tj.topLeft, tj.size, style = Stroke(maxOf(h * 0.0025f, 0.45f * u)))
        }
        // La plancha de acero: su cara de arriba con luz, el frente y el canto en sombra.
        drawRect(t(ACERO, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(ACERO_LUZ, oscuro), tapa.topLeft, Size(tapa.width, tapa.height * 0.3f))
        drawRect(t(ACERO_OSC, oscuro), Offset(tapa.left, tapa.bottom - tapa.height * 0.18f), Size(tapa.width, tapa.height * 0.18f))
        if (fino) (if (muyFino) listOf(0.16f, 0.38f, 0.62f, 0.84f) else listOf(0.17f, 0.83f)).forEach { x ->
            val c = pt(x, 0.787f); val r = h * 0.0075f
            remache(c, r, e.base, e.brillo)
            if (muyFino) drawLine(e.metalOscuro, Offset(c.x - r * 0.7f, c.y), Offset(c.x + r * 0.7f, c.y), lw * 0.6f)
        }
        // La solera de acero, al pie.
        drawRect(t(ACERO_OSC, oscuro), solera.topLeft, solera.size)
        drawRect(t(ACERO_LUZ, oscuro).copy(alpha = 0.6f), solera.topLeft, Size(solera.width, maxOf(h * 0.003f, 0.5f * u)))
    }

    /**
     * El archivero del hall: el cajón de arriba de un archivero de acero, con su tapa de canto, la franja roja, el tarjetero del
     * nivel con su ficha, la jaladera, la cerradura y las juntas de los costados.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val tapa = fr(0.03f, 0f, 0.97f, 0.18f)
        val cuerpo = fr(0.07f, 0.18f, 0.93f, 1f)
        if (!e.hecho) { drawPath(Path().apply { addRect(tapa); addRect(cuerpo) }, siluetaFuturo(oscuro)); return }
        drawRect(t(ACERO, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.right - w * 0.04f, cuerpo.top), Size(w * 0.04f, cuerpo.height))
        // El frente del cajón, con su bisel.
        val frente = fr(0.11f, 0.32f, 0.89f, 0.92f)
        drawRect(t(mezcla(ACERO, ACERO_LUZ, 0.35f), oscuro), frente.topLeft, frente.size)
        drawRect(t(ACERO_LUZ, oscuro), frente.topLeft, Size(frente.width, lw))
        drawRect(t(ACERO_OSC, oscuro), Offset(frente.left, frente.bottom - lw), Size(frente.width, lw))
        // La franja roja bajo la tapa.
        drawRect(t(ROJO, oscuro), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, h * 0.1f))
        // El tarjetero del nivel, con su ficha, y la jaladera debajo.
        val tj = fr(0.4f, 0.38f, 0.6f, 0.62f)
        chapaNivel(tj, e, u * 0.6f)
        drawRect(t(PAPEL, oscuro), Offset(tj.left + tj.width * 0.12f, tj.top + tj.height * 0.2f), Size(tj.width * 0.76f, tj.height * 0.5f))
        val jal = fr(0.43f, 0.7f, 0.57f, 0.84f)
        drawRoundRect(t(ACERO_OSC, oscuro), jal.topLeft, jal.size, CornerRadius(jal.height / 2))
        drawLine(t(ACERO_LUZ, oscuro), Offset(jal.left + jal.width * 0.15f, jal.top + lw), Offset(jal.right - jal.width * 0.15f, jal.top + lw), lw)
        // La cerradura y las juntas.
        drawCircle(t(ACERO_OSC, oscuro), h * 0.07f, pt(0.82f, 0.48f))
        drawLine(t(ACERO_LUZ, oscuro), pt(0.82f, 0.44f), pt(0.82f, 0.52f), lw * 0.8f)
        listOf(0.09f, 0.91f).forEach { x -> drawLine(t(ACERO_OSC, oscuro), pt(x, 0.2f), pt(x, 1f), lw * 0.8f) }
        // La tapa con su canto.
        drawRect(t(ACERO, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(ACERO_LUZ, oscuro), tapa.topLeft, Size(tapa.width, tapa.height * 0.45f))
        drawRect(t(ACERO_OSC, oscuro), Offset(tapa.left, tapa.bottom - lw), Size(tapa.width, lw))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07870) else ROJO

    /**
     * La ficha del registro: la tarjeta de papel en su marco de chapa del nivel, sujeta por las cuatro esquineras y con la raya
     * roja del encabezado.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(2 * u)); return }
        // El marco de chapa.
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(2 * u))
        drawRect(e.brillo, Offset(1.5f * u, 0.6f * u), Size(w - 3 * u, 0.9f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        // La ficha.
        val b = 2.6f * u
        drawRect(if (oscuro) CARBON else PAPEL, Offset(b, b), Size(w - 2 * b, h - 2 * b))
        drawLine(e.metalOscuro.copy(alpha = 0.5f), Offset(b, b + 0.4f * u), Offset(w - b, b + 0.4f * u), 0.8f * u)
        drawLine(if (oscuro) Color(0xFFC0443C) else ROJO, Offset(b + 5 * u, b + 2.2f * u), Offset(w - b - 5 * u, b + 2.2f * u), 0.9f * u)
        // Las cuatro esquineras.
        val es = 5.5f * u
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
            val p = Path().apply { moveTo(o.x + s.x * b, o.y + s.y * b); lineTo(o.x + s.x * (b + es), o.y + s.y * b); lineTo(o.x + s.x * b, o.y + s.y * (b + es)); close() }
            drawPath(p, e.base)
            drawPath(p, e.metalOscuro, style = Stroke(0.6f * u))
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * El pasillo del archivo central. El muro es de bloques de hormigón con sus juntas y sus poros, bajo la franja roja del
     * Ministerio; cada columna de trofeos se expone en un nicho del muro (un vano hundido con su marco de
     * acero) y entre los nichos se levantan las torres de archiveros, con sus cajones numerados, el tarjetero, la jaladera y,
     * en algunos, el sello rojo de «archivado». Arriba corre la viga con el ducto del aire, las cámaras que vigilan en cada
     * archivero con su piloto rojo y, sobre cada nicho, la lámpara de tubo en su jaula. Abajo, el zócalo negro y el linóleo
     * en damero con la raya roja de espera; al pie de los archiveros, cajas de archivo apiladas. De día, la luz blanca de los
     * tubos baja en cono por cada nicho; en oscuro, los tubos se apagan, queda la luz de guardia y el haz del reflector
     * entra en diagonal por la ventanilla enrejada del friso.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaArchivo(g, e, oscuro) }
    }

    private fun DrawScope.escenaArchivo(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        /** El color [c] mezclado con el fondo: [k] = 0 es el fondo, 1 el color tal cual. */
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val junta = m(HORMIGON_OSC, if (oscuro) 0.3f else 0.22f)
        val rojo = m(ROJO, if (oscuro) 0.55f else 0.5f)
        val negro = if (oscuro) Color.Black else NEGRO

        // El muro: las hiladas de bloques trabados, con sus poros.
        val pared = Path()
        val altoB = 20f * u; val largoB = 40f * u
        repetirY(g.friso, g.suelo, altoB, 90) { k, y ->
            pared.moveTo(0f, y); pared.lineTo(w, y)
            var x = if (k % 2 == 0) 0f else -largoB / 2; var j = 0
            while (x < w && j < 40) { pared.moveTo(x, y); pared.lineTo(x, minOf(y + altoB, g.suelo)); x += largoB; j++ }
        }
        drawPath(pared, junta, style = Stroke(0.8f * u))
        for (k in 0 until 60) {
            val x = w * azar(e.semilla + 3, k); val y = g.friso + (g.suelo - g.friso) * azar(e.semilla + 3, k + 70)
            drawCircle(junta, (0.5f + 0.7f * azar(e.semilla + 3, k + 140)) * u, Offset(x, y))
        }
        // Los nichos: el vano hundido de cada columna, su marco de acero y la sombra de su canto de arriba.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            drawRect(negro.copy(alpha = if (oscuro) 0.22f else 0.045f), v.topLeft, v.size)
            drawRect(negro.copy(alpha = if (oscuro) 0.2f else 0.06f), v.topLeft, Size(v.width, 6f * u))
            drawRect(negro.copy(alpha = if (oscuro) 0.14f else 0.04f), v.topLeft, Size(3f * u, v.height))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.025f else 0.18f), Offset(v.right - 2f * u, v.top), Size(2f * u, v.height))
            drawRect(m(ACERO_OSC, 0.35f), v.topLeft, v.size, style = Stroke(1.2f * u))
            // El ojo del Ministerio pintado en el fondo del nicho, apenas: el círculo, el párpado y la pupila.
            val ojo = Offset(g.cx(i), v.top + minOf(v.width * 0.42f, 64f * u))
            val ro = minOf(v.width * 0.3f, 42f * u)
            val tintaOjo = m(NEGRO, if (oscuro) 0.1f else 0.06f)
            drawCircle(tintaOjo, ro, ojo, style = Stroke(2.4f * u))
            drawPath(Path().apply {
                moveTo(ojo.x - ro * 0.8f, ojo.y); quadraticTo(ojo.x, ojo.y - ro * 0.75f, ojo.x + ro * 0.8f, ojo.y)
                quadraticTo(ojo.x, ojo.y + ro * 0.75f, ojo.x - ro * 0.8f, ojo.y); close()
            }, tintaOjo, style = Stroke(2f * u))
            drawCircle(tintaOjo, ro * 0.22f, ojo)
            // El conducto eléctrico por el canto del nicho, con sus cajas de registro.
            val xc = v.left + 4f * u
            drawLine(m(ACERO_OSC, 0.3f), Offset(xc, v.top), Offset(xc, v.bottom), 2.2f * u)
            repetirY(v.top + 70f * u, v.bottom - 20f * u, g.modulo, 10) { _, y ->
                drawRect(m(ACERO, 0.4f), Offset(xc - 3.5f * u, y), Size(7f * u, 9f * u))
                drawRect(m(ACERO_OSC, 0.45f), Offset(xc - 3.5f * u, y), Size(7f * u, 9f * u), style = Stroke(0.7f * u))
                drawCircle(m(ACERO_OSC, 0.5f), 0.8f * u, Offset(xc, y + 4.5f * u))
            }
            // La placa del pasillo, estarcida al pie del nicho: cuatro barras partidas por el puente de la plantilla.
            for (k in 0 until 3) {
                val x = v.left + 6f * u + k * 6f * u
                drawRect(m(NEGRO, 0.16f), Offset(x, v.bottom - 30f * u), Size(4f * u, 5f * u))
                drawRect(m(NEGRO, 0.16f), Offset(x, v.bottom - 24f * u), Size(4f * u, 5f * u))
            }
        }
        // La luz de los tubos: un cono blanco por nicho (de día) o la luz de guardia, más corta (de noche). Un solo degradado.
        val conos = Path()
        for (i in 0 until g.n) {
            val c = g.cx(i); val v = g.vano(i)
            conos.moveTo(c - 22f * u, g.friso); conos.lineTo(c + 22f * u, g.friso)
            conos.lineTo(minOf(c + v.width * 0.5f, v.right), g.friso + (if (oscuro) 90f else 170f) * u)
            conos.lineTo(maxOf(c - v.width * 0.5f, v.left), g.friso + (if (oscuro) 90f else 170f) * u); conos.close()
        }
        val luzTubo = if (oscuro) Color(0xFFBFD8C8) else Color(0xFFFFFFF4)
        drawPath(conos, Brush.verticalGradient(listOf(luzTubo.copy(alpha = if (oscuro) 0.07f else 0.3f), luzTubo.copy(alpha = 0f)),
            g.friso, g.friso + (if (oscuro) 90f else 170f) * u))
        // De noche, el haz del reflector: entra por la ventanilla del friso y cruza el pasillo en diagonal. El segundo degradado.
        if (oscuro) {
            val xv = w * 0.72f
            val haz = Path().apply { moveTo(xv - 8f * u, g.friso); lineTo(xv + 8f * u, g.friso); lineTo(w * 0.18f + 60f * u, h); lineTo(w * 0.18f - 20f * u, h); close() }
            drawPath(haz, Brush.linearGradient(listOf(Color(0xFFE8F0FF).copy(alpha = 0.08f), Color(0xFFE8F0FF).copy(alpha = 0.012f)),
                Offset(xv, g.friso), Offset(w * 0.18f, h)))
        }

        // Las torres de archiveros entre los nichos (y medias en los bordes).
        val acero = m(ACERO, if (oscuro) 0.5f else 0.42f)
        val aceroLuz = m(ACERO_LUZ, if (oscuro) 0.42f else 0.55f)
        val aceroOsc = m(ACERO_OSC, if (oscuro) 0.6f else 0.5f)
        val papel = m(PAPEL, if (oscuro) 0.35f else 0.9f)
        val altoC = 26f * u
        val yCajones = g.friso + 8f * u
        for (i in 0..g.n) {
            val x = g.borde(i); val a = g.pil
            // La caja del archivero, con su sombra en el muro.
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(x - a + 3f * u, yCajones), Size(2 * a, g.suelo - yCajones))
            drawRect(acero, Offset(x - a, yCajones), Size(2 * a, g.suelo - yCajones))
            drawRect(aceroLuz, Offset(x - a, yCajones), Size(1.4f * u, g.suelo - yCajones))
            drawRect(aceroOsc, Offset(x + a - 1.6f * u, yCajones), Size(1.6f * u, g.suelo - yCajones))
            // Los cajones, de arriba abajo.
            repetirY(yCajones + 2f * u, g.suelo - altoC * 0.6f, altoC, 60) { k, y ->
                val cr = Rect(x - a + 2.5f * u, y, x + a - 2.5f * u, minOf(y + altoC - 3f * u, g.suelo - 2f * u))
                if (cr.height < 6f * u) return@repetirY
                drawRect(m(mezcla(ACERO, ACERO_LUZ, 0.3f), if (oscuro) 0.5f else 0.46f), cr.topLeft, cr.size)
                drawLine(aceroLuz, Offset(cr.left, cr.top + 0.5f * u), Offset(cr.right, cr.top + 0.5f * u), 0.8f * u)
                drawLine(aceroOsc, Offset(cr.left, cr.bottom), Offset(cr.right, cr.bottom), 1f * u)
                // El tarjetero con su ficha y la jaladera.
                val tw = minOf(cr.width * 0.42f, 11f * u)
                drawRect(aceroOsc, Offset(x - tw / 2 - 0.6f * u, cr.top + 3.4f * u), Size(tw + 1.2f * u, 6.2f * u))
                drawRect(papel, Offset(x - tw / 2, cr.top + 4f * u), Size(tw, 5f * u))
                drawLine(m(NEGRO, 0.4f), Offset(x - tw * 0.32f, cr.top + 6.5f * u), Offset(x + tw * 0.2f, cr.top + 6.5f * u), 0.7f * u)
                drawRoundRect(aceroOsc, Offset(x - tw * 0.45f, cr.top + 13f * u), Size(tw * 0.9f, 3f * u), CornerRadius(1.5f * u))
                // El sello rojo de «archivado» en uno de cada tres; la cerradura, en el de arriba.
                if (azar(e.semilla + i, k) > 0.66f) drawCircle(rojo, 3.2f * u, Offset(cr.right - 4.5f * u, cr.bottom - 4.5f * u), style = Stroke(0.9f * u))
                if (k == 0) drawCircle(aceroOsc, 1.6f * u, Offset(cr.right - 3.5f * u, cr.top + 4f * u))
            }
            // Las cajas de archivo apiladas al pie, de cartón, con su agujero de mano y la etiqueta.
            val carton = m(Color(0xFFB59D72), if (oscuro) 0.35f else 0.42f)
            for (j in 0 until 2) {
                val bw = minOf(2 * a + 8f * u, 34f * u) - j * 4f * u; val bh = 12f * u
                val bx = x - bw / 2 + (if (j == 1) 2f * u else 0f); val by = g.suelo - bh * (j + 1)
                drawRect(carton, Offset(bx, by), Size(bw, bh))
                drawRect(m(NEGRO, 0.25f), Offset(bx, by), Size(bw, bh), style = Stroke(0.7f * u))
                drawRoundRect(m(NEGRO, 0.3f), Offset(bx + bw * 0.38f, by + 2.5f * u), Size(bw * 0.24f, 2.4f * u), CornerRadius(1.2f * u))
                drawRect(papel, Offset(bx + bw * 0.2f, by + 6.5f * u), Size(bw * 0.6f, 3.6f * u))
                drawRect(rojo, Offset(bx + bw * 0.2f, by + 6.5f * u), Size(bw * 0.6f, 0.9f * u))
            }
        }

        // El friso: la viga de hormigón, la franja roja y el ducto del aire con sus rejillas.
        drawRect(m(HORMIGON, if (oscuro) 0.5f else 0.45f), Offset.Zero, Size(w, g.friso))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, g.friso - 2.4f * u), Size(w, 2.4f * u))
        drawRect(rojo, Offset(0f, g.friso), Size(w, 3f * u))
        drawRect(m(ACERO_OSC, 0.4f), Offset(0f, g.friso * 0.18f), Size(w, g.friso * 0.36f))
        var xr = 10f * u; var kr = 0
        while (xr < w && kr < 30) {
            drawRect(m(NEGRO, 0.45f), Offset(xr, g.friso * 0.24f), Size(12f * u, g.friso * 0.24f))
            for (j in 0 until 3) drawLine(m(ACERO_LUZ, 0.45f), Offset(xr + (2.5f + j * 3.5f) * u, g.friso * 0.24f), Offset(xr + (2.5f + j * 3.5f) * u, g.friso * 0.48f), 0.7f * u)
            xr += 46f * u; kr++
        }
        // La lámpara de tubo en su jaula sobre cada nicho: encendida de día; de noche, apagada y con la luz de guardia verde.
        for (i in 0 until g.n) {
            val c = g.cx(i); val y = g.friso + 3f * u
            drawRect(m(ACERO_OSC, 0.7f), Offset(c - 24f * u, y), Size(48f * u, 2.5f * u))
            drawRect(if (oscuro) m(Color(0xFF8FA89A), 0.32f) else Color(0xFFFFFFF6), Offset(c - 22f * u, y + 2.5f * u), Size(44f * u, 2.6f * u))
            for (j in 0..5) drawLine(m(ACERO_OSC, 0.6f), Offset(c - 22f * u + j * 8.8f * u, y + 2.5f * u), Offset(c - 22f * u + j * 8.8f * u, y + 6f * u), 0.7f * u)
            drawLine(m(ACERO_OSC, 0.6f), Offset(c - 22f * u, y + 6f * u), Offset(c + 22f * u, y + 6f * u), 0.7f * u)
            if (oscuro) {
                drawCircle(Color(0xFF5CFF8A).copy(alpha = 0.18f), 4f * u, Offset(c + 26f * u, y + 3f * u))
                drawCircle(Color(0xFF5CFF8A).copy(alpha = 0.8f), 1.2f * u, Offset(c + 26f * u, y + 3f * u))
            }
        }
        // De noche, la ventanilla enrejada del friso por donde entra el reflector.
        if (oscuro) {
            val xv = w * 0.72f
            drawRect(Color(0xFFDDE6F5).copy(alpha = 0.35f), Offset(xv - 9f * u, g.friso * 0.12f), Size(18f * u, g.friso * 0.7f))
            for (j in 0..3) drawLine(m(NEGRO, 0.9f), Offset(xv - 9f * u + j * 6f * u, g.friso * 0.12f), Offset(xv - 9f * u + j * 6f * u, g.friso * 0.82f), 1.2f * u)
        }
        // Las cámaras de vigilancia sobre cada archivero, con su piloto rojo (y su halo, más fuerte de noche).
        for (i in 0..g.n) {
            val x = g.borde(i); val y = g.friso + 1f * u
            val lado = if (i == g.n) -1f else 1f
            drawRect(m(ACERO_OSC, 0.65f), Offset(x - 1f * u, y), Size(2f * u, 5f * u))
            val cam = Path().apply {
                moveTo(x - lado * 5f * u, y + 4f * u); lineTo(x + lado * 9f * u, y + 6f * u); lineTo(x + lado * 9f * u, y + 11f * u); lineTo(x - lado * 5f * u, y + 10f * u); close()
            }
            drawPath(cam, m(ACERO, if (oscuro) 0.6f else 0.6f))
            drawPath(cam, m(NEGRO, 0.5f), style = Stroke(0.7f * u))
            val led = Offset(x + lado * 8f * u, y + 8.5f * u)
            drawCircle(ROJO.copy(alpha = if (oscuro) 0.3f else 0.14f), 4.5f * u, led)
            drawCircle(if (oscuro) Color(0xFFFF4A40) else ROJO, 1.2f * u, led)
        }

        // El zócalo negro y el linóleo en damero, con la raya roja de espera.
        drawRect(m(NEGRO, if (oscuro) 0.7f else 0.55f), Offset(0f, g.suelo), Size(w, 3f * u))
        val yl = g.suelo + 3f * u
        drawRect(m(HORMIGON_OSC, if (oscuro) 0.4f else 0.32f), Offset(0f, yl), Size(w, h - yl))
        val tl = maxOf((h - yl) / 2f, 1f)
        val damero = Path()
        var xt = 0f; var kt = 0
        while (xt < w && kt < 200) {
            for (f in 0 until 2) if ((kt + f) % 2 == 0) damero.addRect(Rect(xt, yl + f * tl, xt + tl, yl + (f + 1) * tl))
            xt += tl; kt++
        }
        drawPath(damero, m(NEGRO, if (oscuro) 0.25f else 0.14f))
        drawRect(rojo, Offset(0f, yl + tl - 0.8f * u), Size(w, 1.6f * u))
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El banderín del punto de control: el dado de hormigón, el poste a franjas blancas y negras, el banderín rojo ondeando con
     * su franja blanca y el ojo del registro, y en el remate la luz de aviso en el color de acento (de noche, con su halo).
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 14f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val linea = if (oscuro) PAPEL.copy(alpha = 0.6f) else NEGRO
        // El dado de hormigón, con su cara derecha en sombra.
        val dado = Path().apply { moveTo(x - 4.6f * u, pie); lineTo(x + 4.6f * u, pie); lineTo(x + 3.4f * u, pie - 3.6f * u); lineTo(x - 3.4f * u, pie - 3.6f * u); close() }
        drawPath(dado, t(HORMIGON, oscuro))
        drawPath(Path().apply { moveTo(x + 1.2f * u, pie); lineTo(x + 4.6f * u, pie); lineTo(x + 3.4f * u, pie - 3.6f * u); lineTo(x + 1.2f * u, pie - 3.6f * u); close() }, t(HORMIGON_OSC, oscuro))
        drawPath(dado, linea, style = Stroke(0.6f * u, join = StrokeJoin.Round))
        // El poste a franjas, con su contorno.
        val y0 = pie - 3.6f * u; val y1 = pie - 22.5f * u; val pw = 1.9f * u
        drawRect(linea, Offset(x - pw / 2 - 0.45f * u, y1 - 0.45f * u), Size(pw + 0.9f * u, y0 - y1 + 0.45f * u))
        val franja = maxOf(2.5f * u, 1f)
        var yy = y0; var k = 0
        while (yy > y1 && k < 16) {
            val h = minOf(franja, yy - y1)
            drawRect(if (k % 2 == 0) t(PAPEL, oscuro) else t(CARBON, oscuro), Offset(x - pw / 2, yy - h), Size(pw, h))
            yy -= franja; k++
        }
        // El banderín rojo, con la cola partida, la franja blanca y el ojo del registro.
        val bx = x + pw / 2; val by = y1 + 0.8f * u
        val ban = Path().apply {
            moveTo(bx, by); cubicTo(bx + 4 * u, by - 1.4f * u, bx + 8 * u, by + 1.2f * u, bx + 12.5f * u, by - 0.2f * u)
            lineTo(bx + 10 * u, by + 4.2f * u); lineTo(bx + 12.5f * u, by + 8.6f * u)
            cubicTo(bx + 8 * u, by + 9.8f * u, bx + 4 * u, by + 7.2f * u, bx, by + 8.4f * u); close()
        }
        drawPath(ban, t(ROJO, oscuro))
        drawLine(t(PAPEL, oscuro), Offset(bx, by + 4.1f * u), Offset(bx + 9.4f * u, by + 4.3f * u), 1.7f * u)
        val ojo = Offset(bx + 4.6f * u, by + 4.2f * u)
        drawCircle(t(NEGRO, oscuro), 1.9f * u, ojo)
        drawCircle(t(PAPEL, oscuro), 0.95f * u, ojo)
        drawCircle(t(ROJO, oscuro), 0.45f * u, ojo)
        drawPath(ban, linea.copy(alpha = if (oscuro) 0.5f else 0.85f), style = Stroke(0.6f * u, join = StrokeJoin.Round))
        // El remate con la luz de aviso.
        val luz = Offset(x, y1 - 1.7f * u)
        if (oscuro) { drawCircle(e.acento.copy(alpha = 0.12f), 7f * u, luz); drawCircle(e.acento.copy(alpha = 0.26f), 3.8f * u, luz) }
        drawRect(t(CARBON, oscuro), Offset(x - 1.5f * u, y1 - 0.5f * u), Size(3f * u, 1f * u))
        drawCircle(e.acento, 1.4f * u, luz)
        drawCircle(linea, 1.4f * u, luz, style = Stroke(0.5f * u))
        drawCircle(Color.White.copy(alpha = 0.75f), 0.45f * u, luz - Offset(0.45f * u, 0.45f * u))
    }

    /**
     * El expediente aprobado: la hoja con su membrete y sus renglones, el sello rojo estampado y torcido (doble filete, la
     * estrella y la palomita, sin letras, con el entintado desparejo), el sello de goma que se acaba de levantar con las rayas
     * del golpe y, de noche, la lámpara del despacho con su cono de luz sobre la hoja.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 52f * u, 46f * u)
        val x = b.x; val y = b.y
        val rojo = if (oscuro) mezcla(ROJO, Color(0xFFFF4A3A), 0.4f) else ROJO
        val linea = if (oscuro) PAPEL.copy(alpha = 0.5f) else NEGRO
        val papel = if (oscuro) mezcla(PAPEL, Color(0xFF0B0C0D), 0.12f) else PAPEL
        // De noche: la lámpara colgada y su cono de luz (el único degradado).
        if (oscuro) {
            val lx = x - 3 * u
            val cono = Path().apply { moveTo(lx - 3 * u, y - 40.5f * u); lineTo(lx + 3 * u, y - 40.5f * u); lineTo(lx + 23 * u, y - 1 * u); lineTo(lx - 23 * u, y - 1 * u); close() }
            drawPath(cono, Brush.verticalGradient(listOf(Color(0xFFFFF0C4).copy(alpha = 0.3f), Color(0xFFFFF0C4).copy(alpha = 0.04f)), y - 40.5f * u, y))
            drawLine(ACERO_LUZ.copy(alpha = 0.6f), Offset(lx, y - 46 * u), Offset(lx, y - 43.5f * u), 0.7f * u)
            drawPath(Path().apply { moveTo(lx - 1.6f * u, y - 43.5f * u); lineTo(lx + 1.6f * u, y - 43.5f * u); lineTo(lx + 4 * u, y - 40.5f * u); lineTo(lx - 4 * u, y - 40.5f * u); close() }, t(ACERO, oscuro))
            drawCircle(Color(0xFFFFF0C4), 1.2f * u, Offset(lx, y - 40.4f * u))
        }
        // La hoja del expediente, girada, con su sombra, el membrete y los renglones.
        val c = Offset(x - 4 * u, y - 13 * u)
        rotate(-5f, c) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), Offset(c.x - 14.4f * u, c.y - 9.6f * u), Size(31 * u, 22 * u))
            drawRect(papel, Offset(c.x - 15.5f * u, c.y - 11 * u), Size(31 * u, 22 * u))
            drawRect(NEGRO.copy(alpha = 0.8f), Offset(c.x - 13.5f * u, c.y - 9.2f * u), Size(6 * u, 1.8f * u))
            drawRect(rojo.copy(alpha = 0.7f), Offset(c.x + 8.5f * u, c.y - 9.2f * u), Size(4.5f * u, 1.8f * u))
            drawLine(ACERO_OSC.copy(alpha = 0.6f), Offset(c.x - 13.5f * u, c.y - 6.4f * u), Offset(c.x + 13 * u, c.y - 6.4f * u), 0.5f * u)
            for (k in 0 until 5) drawLine(ACERO.copy(alpha = 0.42f), Offset(c.x - 13.5f * u, c.y - 3.6f * u + k * 3.1f * u),
                Offset(c.x + (12.5f - (k % 3) * 4f) * u, c.y - 3.6f * u + k * 3.1f * u), 0.7f * u)
            drawRect(linea.copy(alpha = 0.5f), Offset(c.x - 15.5f * u, c.y - 11 * u), Size(31 * u, 22 * u), style = Stroke(0.5f * u))
        }
        // El sello estampado, más torcido: doble filete, la estrella a la izquierda y la palomita grande.
        val sc = Offset(c.x + 1.5f * u, c.y + 1.5f * u)
        rotate(-13f, sc) {
            val r = Rect(sc.x - 12 * u, sc.y - 6 * u, sc.x + 12 * u, sc.y + 6 * u)
            drawRect(rojo.copy(alpha = 0.88f), r.topLeft, r.size, style = Stroke(1.5f * u))
            drawRect(rojo.copy(alpha = 0.88f), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width - 4 * u, r.height - 4 * u), style = Stroke(0.6f * u))
            drawPath(estrella(Offset(r.left + 5.6f * u, sc.y), 2.6f * u, 5, 0.42f), rojo.copy(alpha = 0.88f))
            drawPath(Path().apply { moveTo(sc.x - 3 * u, sc.y); lineTo(sc.x + 0.2f * u, sc.y + 2.8f * u); lineTo(sc.x + 8 * u, sc.y - 3.2f * u) }, rojo.copy(alpha = 0.9f),
                style = Stroke(1.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // El entintado desparejo: huecos de papel en el filete.
            for (k in 0 until 7) drawCircle(papel, (0.35f + 0.35f * azar(7, k)) * u, Offset(r.left + azar(11, k) * r.width, if (k % 2 == 0) r.top else r.bottom))
        }
        // El sello de goma recién levantado, girado, con las rayas del golpe.
        val g = Offset(x + 15 * u, y - 29 * u)
        rotate(16f, g) {
            drawRect(mezcla(ROJO, NEGRO, 0.35f), Offset(g.x - 6 * u, g.y + 3.1f * u), Size(12 * u, 1.5f * u))
            drawRect(t(ACERO, oscuro), Offset(g.x - 6.5f * u, g.y), Size(13 * u, 3.2f * u))
            drawRect(t(ACERO_LUZ, oscuro), Offset(g.x - 6.5f * u, g.y), Size(13 * u, 0.9f * u))
            val mango = if (oscuro) mezcla(CARBON, ACERO_LUZ, 0.25f) else CARBON
            drawRect(mango, Offset(g.x - 1.3f * u, g.y - 4.6f * u), Size(2.6f * u, 4.7f * u))
            drawOval(mango, Offset(g.x - 3.4f * u, g.y - 10.2f * u), Size(6.8f * u, 6 * u))
            drawOval(Color.White.copy(alpha = if (oscuro) 0.25f else 0.3f), Offset(g.x - 2.2f * u, g.y - 9.4f * u), Size(2.4f * u, 1.6f * u))
            if (oscuro) {
                drawRect(linea, Offset(g.x - 6.5f * u, g.y), Size(13 * u, 3.2f * u), style = Stroke(0.5f * u))
                drawOval(linea, Offset(g.x - 3.4f * u, g.y - 10.2f * u), Size(6.8f * u, 6 * u), style = Stroke(0.5f * u))
            }
        }
        listOf(Triple(-9.5f, 2f, -13f), Triple(-9f, 6.5f, -13.5f), Triple(-5.5f, 10.5f, -8f)).forEach { (dx, dy, hx) ->
            drawLine(linea.copy(alpha = 0.55f), Offset(g.x + dx * u, g.y + dy * u), Offset(g.x + hx * u, g.y + (dy - 1.6f + (if (dx > -6f) 3.2f else 0f)) * u), 0.7f * u, StrokeCap.Round)
        }
        // Unas gotas de tinta que saltaron.
        listOf(Offset(9f, -3f) to 0.7f, Offset(12f, -6f) to 0.45f, Offset(-15f, 4f) to 0.5f).forEach { (o, r) -> drawCircle(rojo.copy(alpha = 0.8f), r * u, Offset(sc.x + o.x * u, sc.y + o.y * u)) }
    }
}

/**
 * Ciudad neón (solo existe en oscuro): los logros del callejón. El trofeo descansa en la caja de cerveza de plástico, de
 * cabeza, que sirve de banco en los puestos; en el hall, sobre la caja de luz del letrero, y su placa es el vidrio negro del
 * quiosco con un hilo de neón.
 */
object LogrosNeon : DisenoLogros {
    private val CARCASA = Color(0xFF221C33)
    private val CARCASA_LUZ = Color(0xFF3B3354)
    private val ALAMBRE = Color(0xFFB9C1CE)
    private val MAGENTA = Color(0xFFFF2E8C)
    private val CIAN = Color(0xFF2EE8E0)
    private val VIDRIO = Color(0xFF05040A)
    private val PLASTICO = Color(0xFF2F6B3A)
    private val PLASTICO_LUZ = Color(0xFF4A8C52)
    private val PLASTICO_OSC = Color(0xFF173A1E)
    private val CHARCO = Color(0xFF0D0A18)

    /** El color bajo la luz del callejón: siempre hundido (el tema solo es oscuro). Los neones no pasan por aquí. */
    @Suppress("UNUSED_PARAMETER")
    private fun t(c: Color, oscuro: Boolean): Color = mezcla(c, Color(0xFF06050B), 0.3f)

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El banco del puesto: la caja de cerveza de plástico verde puesta de cabeza, con su borde, los nervios de las esquinas y la
     * rejilla calada; por el canto de arriba corre un tubo magenta entre sus dos electrodos del nivel, y al frente va
     * remachada la chapa del permiso del puesto en el metal del nivel. Abajo, el charco del asfalto que devuelve el neón.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val caja = fr(0.15f, 0.778f, 0.85f, 0.945f)
        if (!e.hecho) { drawRoundRect(siluetaFuturo(true), caja.topLeft, caja.size, CornerRadius(w * 0.018f)); return }
        // El charco, con la sombra de la caja y el reflejo del tubo.
        drawOval(CHARCO, Offset(w * 0.05f, h * 0.932f), Size(w * 0.9f, h * 0.042f))
        drawOval(Color.Black.copy(alpha = 0.5f), Offset(w * 0.12f, h * 0.938f), Size(w * 0.76f, h * 0.02f))
        if (fino) for (k in 0 until 3) drawLine(MAGENTA.copy(alpha = 0.3f - k * 0.08f), pt(0.24f + k * 0.07f, 0.958f + k * 0.006f), pt(0.76f - k * 0.07f, 0.958f + k * 0.006f), lw)
        // La caja, con su borde de arriba (el fondo, de cabeza) y el de abajo.
        val verde = t(PLASTICO, true); val osc = t(PLASTICO_OSC, true); val luz = t(PLASTICO_LUZ, true)
        drawRoundRect(verde, caja.topLeft, caja.size, CornerRadius(w * 0.018f))
        drawRect(luz, Offset(caja.left + w * 0.012f, caja.top), Size(caja.width - w * 0.024f, h * 0.01f))
        drawRect(osc, Offset(caja.left + w * 0.01f, caja.bottom - h * 0.016f), Size(caja.width - w * 0.02f, h * 0.016f))
        listOf(caja.left + w * 0.018f, caja.right - w * 0.048f).forEach { x -> drawRect(osc.copy(alpha = 0.55f), Offset(x, caja.top + h * 0.01f), Size(w * 0.03f, caja.height - h * 0.026f)) }
        // El reflejo magenta del letrero en la cara de arriba.
        drawRect(MAGENTA.copy(alpha = 0.13f), caja.topLeft, Size(caja.width, h * 0.05f))
        // La rejilla calada (a lienzo chico, una sola hilera de huecos).
        val hueco = Color(0xFF07120A)
        val filas = if (fino) 2 else 1; val cols = if (muyFino) 7 else if (fino) 6 else 4
        val gx0 = 0.22f; val gx1 = 0.78f; val gy0 = 0.83f; val gy1 = 0.918f
        val cw = (gx1 - gx0) / cols; val chh = (gy1 - gy0) / filas
        for (f in 0 until filas) for (c in 0 until cols) {
            val r = fr(gx0 + c * cw + cw * 0.16f, gy0 + f * chh + chh * 0.16f, gx0 + (c + 1) * cw - cw * 0.16f, gy0 + (f + 1) * chh - chh * 0.16f)
            drawRoundRect(hueco, r.topLeft, r.size, CornerRadius(minOf(r.width, r.height) * 0.3f))
            if (fino) drawLine(MAGENTA.copy(alpha = 0.22f), r.topLeft, Offset(r.right, r.top), lw * 0.7f)
        }
        // La chapa del permiso del puesto, en el metal del nivel, con sus dos remaches.
        val pl = fr(0.57f, 0.852f, 0.76f, 0.908f)
        chapaNivel(pl, e, h * 0.004f)
        if (fino) {
            listOf(pl.left + pl.width * 0.12f, pl.right - pl.width * 0.12f).forEach { x -> drawCircle(e.metalOscuro, pl.height * 0.1f, Offset(x, pl.center.y)) }
            for (k in 0 until 3) drawRect(e.metalOscuro.copy(alpha = 0.7f), Offset(pl.left + pl.width * (0.28f + k * 0.16f), pl.top + pl.height * 0.32f), Size(pl.width * 0.1f, pl.height * 0.36f))
        }
        // El tubo de neón, con sus soportes de alambre y sus dos electrodos del nivel.
        val yT = h * 0.806f
        if (fino) listOf(0.36f, 0.64f).forEach { x -> drawLine(t(ALAMBRE, true), Offset(w * x, yT), Offset(w * x, yT + h * 0.018f), lw) }
        tuboNeon(Path().apply { moveTo(w * 0.24f, yT); lineTo(w * 0.76f, yT) }, MAGENTA, maxOf(h * 0.0065f, 0.7f * u))
        listOf(0.17f, 0.76f).forEach { x -> chapaNivel(fr(x, 0.794f, x + 0.07f, 0.818f), e, h * 0.004f) }
    }

    /**
     * La caja de luz del callejón: el letrero bajo de chapa violeta con su vidrio negro, el tubo cian que lo recorre en zigzag,
     * el piloto magenta y los dos soportes del nivel que lo atornillan a la repisa.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cuerpo = fr(0.04f, 0.08f, 0.96f, 0.82f)
        if (!e.hecho) { drawRoundRect(siluetaFuturo(true), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.14f)); return }
        // Los soportes del nivel, al pie.
        listOf(0.1f, 0.78f).forEach { x -> chapaNivel(fr(x, 0.7f, x + 0.12f, 1f), e, h * 0.05f) }
        drawRoundRect(t(CARCASA, true), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.14f))
        drawRoundRect(t(CARCASA_LUZ, true), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.14f), style = Stroke(maxOf(h * 0.04f, 0.6f * u)))
        val vidrio = fr(0.09f, 0.22f, 0.91f, 0.68f)
        drawRoundRect(VIDRIO, vidrio.topLeft, vidrio.size, CornerRadius(h * 0.08f))
        // El tubo en zigzag.
        val n = 8
        val zig = Path().apply {
            for (k in 0..n) {
                val x = w * (0.14f + 0.62f * k / n); val y = h * (if (k % 2 == 0) 0.34f else 0.56f)
                if (k == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        tuboNeon(zig, CIAN, maxOf(h * 0.045f, 0.7f * u))
        // El piloto magenta y los tornillos.
        drawCircle(MAGENTA.copy(alpha = 0.3f), h * 0.12f, pt(0.85f, 0.45f))
        drawCircle(MAGENTA, h * 0.06f, pt(0.85f, 0.45f))
        listOf(0.08f, 0.92f).forEach { x -> remache(pt(x, 0.45f), h * 0.04f, t(ALAMBRE, true), Color.White) }
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFE6FFFC)
    override fun rotuloPlaca(oscuro: Boolean): Color = Color(0xFFFF8CC2)

    /**
     * La etiqueta del quiosco: el vidrio negro en su chapa del nivel y su carcasa violeta, con un hilo de neón magenta corriendo
     * por dentro del borde.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(true), Offset.Zero, size, CornerRadius(4 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(4 * u))
        drawRect(e.brillo, Offset(3 * u, 0.6f * u), Size(w - 6 * u, 0.9f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        val b1 = 1.8f * u
        drawRoundRect(t(CARCASA, true), Offset(b1, b1), Size(w - 2 * b1, h - 2 * b1), CornerRadius(3 * u))
        val b2 = 3 * u
        drawRoundRect(VIDRIO, Offset(b2, b2), Size(w - 2 * b2, h - 2 * b2), CornerRadius(2.2f * u))
        val b3 = 5 * u
        if (w > 2 * b3 + 4 * u && h > 2 * b3 + 4 * u)
            tuboNeon(Path().apply { addRoundRect(RoundRect(Rect(b3, b3, w - b3, h - b3), CornerRadius(1.6f * u))) }, MAGENTA, 0.75f * u)
    }

    // ---------------------------------------------------------------- el salón del hall

    /** El ladrillo mojado del callejón, el ámbar de los farolillos y el verde de sodio de la calle. */
    private val LADRILLO = Color(0xFF4A2A3A)
    private val AMBAR = Color(0xFFFFB238)
    private val SODIO = Color(0xFF8CFF5A)
    private val FAROLILLO = Color(0xFFFF5A4A)

    /**
     * El callejón de noche (el tema solo es oscuro). El muro es de ladrillo mojado en hiladas trabadas; cada columna de
     * trofeos tiene detrás la cortina metálica de un puesto cerrado, con sus lamas, su cerradura al pie y el toldo de rayas
     * arriba. Entre los puestos, en la esquina de cada muro, cuelga un letrero vertical en su caja oscura con sus tubos de
     * neón (un cuenco, una flecha, una estrella, una gota: formas, no letras), un tubo distinto cada tramo de ≈ 230 dp; a su
     * lado baja el bajante con sus abrazaderas. Arriba cruzan los cables con farolillos de papel junto a los letreros; en los
     * bordes, el aparato del aire que gotea. Llueve en diagonal; abajo, el asfalto mojado con los charcos que devuelven el
     * color de cada letrero, y el vapor de la alcantarilla.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaCallejon(g, e) }
    }

    private fun DrawScope.escenaCallejon(g: GeoSalon, e: EstadoSalon) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        val tonos = listOf(MAGENTA, CIAN, AMBAR, SODIO)

        // El resplandor de la ciudad, que baja del cielo entre los muros (un degradado).
        drawRect(Brush.verticalGradient(listOf(MAGENTA.copy(alpha = 0.07f), Color.Transparent), 0f, h * 0.45f), Offset.Zero, Size(w, h * 0.45f))
        // El ladrillo: las hiladas, más claras en la cara de cada ladrillo, y las juntas.
        val altoL = 11f * u; val largoL = 26f * u
        val caras = Path(); val juntas = Path()
        repetirY(g.friso, g.suelo, altoL, 130) { k, y ->
            caras.addRect(Rect(0f, y + 1.4f * u, w, minOf(y + altoL, g.suelo)))
            juntas.moveTo(0f, y); juntas.lineTo(w, y)
            var x = if (k % 2 == 0) 0f else -largoL / 2; var j = 0
            while (x < w && j < 40) { juntas.moveTo(x, y); juntas.lineTo(x, minOf(y + altoL, g.suelo)); x += largoL; j++ }
        }
        drawPath(caras, m(LADRILLO, 0.32f))
        drawPath(juntas, m(Color.Black, 0.55f), style = Stroke(1.3f * u))
        // Ladrillos sueltos más claros o más oscuros, y las manchas de humedad que bajan.
        for (k in 0 until 50) {
            val fila = (azar(e.semilla + 5, k) * ((g.suelo - g.friso) / altoL)).toInt()
            val y = g.friso + fila * altoL + 1.4f * u
            val x = w * azar(e.semilla + 5, k + 60)
            drawRect(if (k % 3 == 0) Color.Black.copy(alpha = 0.18f) else LADRILLO.copy(alpha = 0.18f), Offset(x, y), Size(largoL - 1.3f * u, altoL - 1.4f * u))
        }

        // Los puestos cerrados: la cortina metálica de cada vano, con sus lamas, la guía, el toldo y la cerradura.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val cort = Rect(v.left + 4f * u, v.top + 10f * u, v.right - 4f * u, v.bottom)
            drawRect(m(CARCASA, 0.55f), cort.topLeft, cort.size)
            val lamas = Path()
            repetirY(cort.top, cort.bottom, 6f * u, 240) { _, y -> lamas.moveTo(cort.left, y); lamas.lineTo(cort.right, y) }
            drawPath(lamas, Color.Black.copy(alpha = 0.32f), style = Stroke(1.1f * u))
            drawPath(lamas, CARCASA_LUZ.copy(alpha = 0.12f), style = Stroke(0.6f * u))
            listOf(cort.left, cort.right).forEach { x -> drawRect(m(ALAMBRE, 0.16f), Offset(x - 1.5f * u, cort.top), Size(3f * u, cort.height)) }
            // Las pintas del muro sobre la cortina: trazos de aerosol, apenas.
            val pinta = tonos[(i + 2) % 4].copy(alpha = 0.07f)
            val yp = cort.top + cort.height * (0.3f + 0.4f * azar(e.semilla, i))
            drawPath(Path().apply {
                moveTo(cort.left + cort.width * 0.12f, yp); cubicTo(cort.left + cort.width * 0.3f, yp - 20f * u, cort.left + cort.width * 0.5f, yp + 24f * u,
                    cort.left + cort.width * 0.88f, yp - 6f * u)
            }, pinta, style = Stroke(5f * u, cap = StrokeCap.Round))
            // El cajón de la cortina enrollada, arriba, y el toldo de rayas con su faldón festoneado.
            drawRect(m(CARCASA_LUZ, 0.5f), Offset(v.left, v.top), Size(v.width, 10f * u))
            drawLine(Color.Black.copy(alpha = 0.5f), Offset(v.left, v.top + 10f * u), Offset(v.right, v.top + 10f * u), 1f * u)
            val ton = tonos[i % 4]
            var xs = v.left; var ks = 0
            while (xs < v.right && ks < 30) {
                val ws = minOf(10f * u, v.right - xs)
                if (ks % 2 == 0) drawRect(ton.copy(alpha = 0.16f), Offset(xs, v.top), Size(ws, 7f * u))
                drawArc(if (ks % 2 == 0) ton.copy(alpha = 0.16f) else m(CARCASA_LUZ, 0.5f), 0f, 180f, true, Offset(xs, v.top + 4.5f * u), Size(ws, 5f * u))
                xs += 10f * u; ks++
            }
            // La cerradura al pie de la cortina, con su candado.
            val cz = Offset(g.cx(i), cort.bottom - 9f * u)
            drawRect(m(ALAMBRE, 0.3f), Offset(cz.x - 7f * u, cz.y), Size(14f * u, 4f * u))
            drawArc(m(ALAMBRE, 0.35f), 180f, 180f, false, Offset(cz.x - 3f * u, cz.y + 2f * u), Size(6f * u, 6f * u), style = Stroke(1.2f * u))
            drawRect(m(AMBAR, 0.3f), Offset(cz.x - 3.5f * u, cz.y + 5f * u), Size(7f * u, 5f * u))
        }

        // La lluvia, en diagonal, por todo el callejón.
        val lluvia = Path()
        for (k in 0 until 90) {
            val x = w * azar(e.semilla + 9, k) * 1.1f; val y = h * azar(e.semilla + 9, k + 100)
            val l = (8f + 10f * azar(e.semilla + 9, k + 200)) * u
            lluvia.moveTo(x, y); lluvia.lineTo(x - l * 0.28f, y + l)
        }
        drawPath(lluvia, Color(0xFFBFD8FF).copy(alpha = 0.13f), style = Stroke(0.7f * u, cap = StrokeCap.Round))

        // Los bajantes de las esquinas, con sus abrazaderas.
        for (i in 0..g.n) {
            val x = g.borde(i) + (if (i == g.n) -g.pil + 3f * u else g.pil - 3f * u)
            drawRect(m(CARCASA_LUZ, 0.55f), Offset(x - 2.5f * u, 0f), Size(5f * u, g.suelo))
            drawRect(Color.White.copy(alpha = 0.06f), Offset(x - 2.5f * u, 0f), Size(1.4f * u, g.suelo))
            repetirY(30f * u, g.suelo, 70f * u, 30) { _, y -> drawRect(m(ALAMBRE, 0.3f), Offset(x - 3.5f * u, y), Size(7f * u, 2.4f * u)) }
        }
        // Los letreros verticales en cada esquina: la caja, el borde de chapa y los tubos (otra forma cada tramo).
        for (i in 0..g.n) {
            val x = g.borde(i); val a = g.pil * 0.82f
            repetirY(g.friso + 34f * u, g.suelo - 60f * u, g.modulo, 10) { k, y0 ->
                val alto = minOf(150f * u, g.suelo - 30f * u - y0)
                if (alto < 40f * u) return@repetirY
                val ton = tonos[(i + k) % 4]
                val caja = Rect(x - a, y0, x + a, y0 + alto)
                // La ménsula de hierro que lo sujeta al muro y la caja con su halo.
                drawLine(m(ALAMBRE, 0.4f), Offset(x, y0 - 12f * u), Offset(x, y0), 1.4f * u)
                drawRect(ton.copy(alpha = 0.05f), Offset(caja.left - 6f * u, caja.top - 6f * u), Size(caja.width + 12f * u, caja.height + 12f * u))
                drawRect(m(VIDRIO, 0.9f), caja.topLeft, caja.size)
                drawRect(m(CARCASA_LUZ, 0.6f), caja.topLeft, caja.size, style = Stroke(1.4f * u))
                val gr = 1.2f * u; val c = caja.center.x; val r = minOf(a * 0.6f, 8f * u)
                // El marco de tubo y, dentro, tres formas una sobre otra.
                tuboNeon(Path().apply { addRect(Rect(caja.left + 3f * u, caja.top + 3f * u, caja.right - 3f * u, caja.bottom - 3f * u)) }, ton.copy(alpha = 0.7f), gr * 0.7f)
                for (j in 0 until 3) {
                    val cy = caja.top + alto * (0.2f + j * 0.3f)
                    val forma = Path().apply {
                        when ((k + j + i) % 4) {
                            0 -> { moveTo(c - r, cy - r * 0.2f); quadraticTo(c, cy + r * 1.4f, c + r, cy - r * 0.2f); close()
                                moveTo(c - r * 0.3f, cy - r * 0.5f); lineTo(c - r * 0.1f, cy - r * 1.2f); moveTo(c + r * 0.3f, cy - r * 0.5f); lineTo(c + r * 0.5f, cy - r * 1.2f) }
                            1 -> { moveTo(c, cy - r); lineTo(c, cy + r); moveTo(c - r * 0.7f, cy + r * 0.2f); lineTo(c, cy + r); lineTo(c + r * 0.7f, cy + r * 0.2f) }
                            2 -> addPath(estrella(Offset(c, cy), r, 5, 0.45f))
                            else -> { moveTo(c, cy - r); quadraticTo(c + r, cy + r * 0.3f, c, cy + r * 0.8f); quadraticTo(c - r, cy + r * 0.3f, c, cy - r) }
                        }
                    }
                    tuboNeon(forma, ton, gr)
                }
            }
        }
        // El aparato del aire en los bordes, con su rejilla, su ventilador y la gota que cae.
        for (i in listOf(0, g.n)) {
            val lado = if (i == 0) 1f else -1f
            val x0 = g.borde(i); val y0 = g.friso + g.modulo * 0.55f
            if (y0 > g.suelo - 60f * u) continue
            val r = Rect(minOf(x0, x0 + lado * (g.pil + 10f * u)), y0, maxOf(x0, x0 + lado * (g.pil + 10f * u)), y0 + 22f * u)
            drawRect(m(ALAMBRE, 0.22f), r.topLeft, r.size)
            drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, r.size, style = Stroke(0.8f * u))
            val cv = Offset(r.center.x, r.center.y)
            drawCircle(m(VIDRIO, 0.8f), 7f * u, cv)
            for (j in 0 until 3) drawLine(m(ALAMBRE, 0.3f), cv, Offset(cv.x + 6f * u * kotlin.math.cos(j * 2.1f), cv.y + 6f * u * kotlin.math.sin(j * 2.1f)), 2f * u)
            drawLine(Color(0xFFBFD8FF).copy(alpha = 0.3f), Offset(r.center.x, r.bottom + 6f * u), Offset(r.center.x, r.bottom + 10f * u), 1f * u, StrokeCap.Round)
        }

        // Los cables que cruzan arriba, con los farolillos junto a las esquinas.
        for (j in 0 until 2) {
            val y0 = g.friso + (6f + j * 14f) * u; val caida = (10f + j * 6f) * u
            val cable = Path().apply { moveTo(0f, y0); var x = 0f; var k = 0
                while (x < w && k < 12) { quadraticTo(x + g.paso / 2, y0 + caida, x + g.paso, y0); x += g.paso; k++ } }
            drawPath(cable, Color.Black.copy(alpha = 0.75f), style = Stroke(1.1f * u))
            for (i in 0..g.n) for (s in listOf(-1f, 1f)) {
                val f = 0.12f + j * 0.06f
                val xl = g.borde(i) + s * g.paso * f
                if (xl < 0f || xl > w) continue
                val tt = (xl - g.borde(if (s < 0) i - 1 else i)) / g.paso
                val yl = y0 + caida * 4f * tt * (1 - tt) * 0.5f + 2f * u
                drawCircle(FAROLILLO.copy(alpha = 0.1f), 11f * u, Offset(xl, yl + 6f * u))
                drawCircle(FAROLILLO.copy(alpha = 0.16f), 6.5f * u, Offset(xl, yl + 6f * u))
                drawOval(FAROLILLO.copy(alpha = 0.75f), Offset(xl - 3.5f * u, yl + 1.5f * u), Size(7f * u, 9.5f * u))
                drawLine(Color.Black.copy(alpha = 0.4f), Offset(xl - 3.5f * u, yl + 6f * u), Offset(xl + 3.5f * u, yl + 6f * u), 0.6f * u)
                drawLine(Color(0xFFFFE0A0).copy(alpha = 0.6f), Offset(xl, yl + 11f * u), Offset(xl, yl + 13.5f * u), 0.8f * u)
            }
        }
        // El friso: el canto del tejadillo, negro, con su gotera.
        drawRect(m(CARCASA, 0.8f), Offset.Zero, Size(w, g.friso))
        drawLine(MAGENTA.copy(alpha = 0.3f), Offset(0f, g.friso - 1f * u), Offset(w, g.friso - 1f * u), 1f * u)
        for (k in 0 until 14) drawCircle(Color(0xFFBFD8FF).copy(alpha = 0.2f), 0.9f * u, Offset(w * azar(e.semilla + 11, k), g.friso + (2f + 10f * azar(e.semilla + 11, k + 20)) * u))

        // El asfalto mojado, los charcos con el reflejo de cada letrero y el vapor de la alcantarilla.
        drawRect(m(CHARCO, 0.9f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        drawLine(Color.Black, Offset(0f, g.suelo), Offset(w, g.suelo), 1.5f * u)
        for (i in 0..g.n) {
            val x = g.borde(i); val ton = tonos[i % 4]
            drawOval(ton.copy(alpha = 0.1f), Offset(x - 26f * u, g.suelo + 2f * u), Size(52f * u, g.piso - 3f * u))
            for (k in 0 until 3) drawLine(ton.copy(alpha = 0.32f - k * 0.08f), Offset(x - (8f - k * 2f) * u, g.suelo + (4f + k * 3.5f) * u),
                Offset(x + (8f - k * 2f) * u, g.suelo + (4f + k * 3.5f) * u), 1.2f * u)
        }
        for (i in 0 until g.n) {
            val c = g.cx(i)
            drawRect(m(ALAMBRE, 0.2f), Offset(c - 12f * u, g.suelo + g.piso * 0.4f), Size(24f * u, 2.5f * u))
            if (i % 2 == 0) for (k in 0 until 4) drawCircle(Color.White.copy(alpha = 0.035f), (7f + k * 5f) * u, Offset(c + (k * 4f - 6f) * u, g.suelo - k * 9f * u))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El patinete de neón: la tabla con su luz cian por debajo y su reflejo en el piso mojado, el viajero de impermeable con
     * capucha y visor, el paraguas transparente con el borde de neón magenta, unas gotas de lluvia y el faro del manubrio en
     * el color de acento, con su haz hacia adelante.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 18f * u, s, true)
        // El reflejo cian en el charco se queda en el piso, como la sombra.
        drawOval(CIAN.copy(alpha = 0.2f * (1 - 0.4f * s)), Offset(x - 9 * u, e.punto.y - 1.2f * u), Size(18 * u, 2.4f * u))
        val pie = e.punto.y - s * 3f * u
        val borde = ALAMBRE.copy(alpha = 0.55f)
        val capa = t(CARCASA_LUZ, oscuro)
        // La lluvia: unas cuantas gotas en diagonal.
        for (k in 0 until 6) {
            val gx = x - 13 * u + azar(5, k) * 25 * u; val gy = pie - 4 * u - azar(9, k) * 22 * u
            drawLine(Color(0xFF9DB4D8).copy(alpha = 0.35f), Offset(gx, gy), Offset(gx - 1 * u, gy + 3.4f * u), 0.5f * u)
        }
        // Las ruedas y la tabla con su luz de abajo.
        val ra = 1.9f * u
        listOf(x - 7 * u, x + 6 * u).forEach { wx ->
            drawCircle(VIDRIO, ra, Offset(wx, pie - ra)); drawCircle(ALAMBRE, ra, Offset(wx, pie - ra), style = Stroke(0.7f * u))
            drawCircle(MAGENTA, 0.5f * u, Offset(wx, pie - ra))
        }
        drawLine(CIAN.copy(alpha = 0.3f), Offset(x - 7 * u, pie - 1.8f * u), Offset(x + 4 * u, pie - 1.8f * u), 3f * u, StrokeCap.Round)
        drawRoundRect(capa, Offset(x - 8 * u, pie - 4.4f * u), Size(12 * u, 1.6f * u), CornerRadius(0.8f * u))
        drawLine(CIAN, Offset(x - 7.5f * u, pie - 2.75f * u), Offset(x + 3.5f * u, pie - 2.75f * u), 0.7f * u, StrokeCap.Round)
        // El manubrio y el faro, con su haz.
        val tope = Offset(x + 4.4f * u, pie - 16.5f * u)
        drawLine(ALAMBRE, Offset(x + 6 * u, pie - ra), tope, 1f * u, StrokeCap.Round)
        drawLine(ALAMBRE, Offset(tope.x - 2.2f * u, tope.y + 0.2f * u), Offset(tope.x + 1.4f * u, tope.y - 0.3f * u), 1f * u, StrokeCap.Round)
        val faro = Offset(x + 5.3f * u, pie - 12.6f * u)
        drawPath(Path().apply { moveTo(faro.x, faro.y - 0.8f * u); lineTo(faro.x + 9 * u, faro.y - 3 * u); lineTo(faro.x + 9 * u, faro.y + 3.6f * u); lineTo(faro.x, faro.y + 0.8f * u); close() }, e.acento.copy(alpha = 0.18f))
        drawCircle(e.acento.copy(alpha = 0.3f), 2.6f * u, faro); drawCircle(e.acento, 1.1f * u, faro)
        // El viajero: las piernas, el impermeable con su ribete magenta, el brazo al manubrio.
        val hx = x - 2.4f * u
        listOf(-0.9f, 0.9f).forEach { dx -> drawLine(capa, Offset(hx + dx * u, pie - 4.4f * u), Offset(hx + (dx + 0.5f) * u, pie - 10 * u), 1.4f * u) }
        val impermeable = Path().apply { moveTo(hx - 2.4f * u, pie - 17 * u); lineTo(hx + 2.8f * u, pie - 17 * u); lineTo(hx + 3.8f * u, pie - 8.6f * u); lineTo(hx - 3.2f * u, pie - 8.6f * u); close() }
        drawPath(impermeable, t(CARCASA, oscuro))
        drawPath(impermeable, borde, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        drawLine(MAGENTA, Offset(hx - 3.1f * u, pie - 9 * u), Offset(hx + 3.7f * u, pie - 9 * u), 0.6f * u)
        drawLine(capa, Offset(hx + 2.4f * u, pie - 16 * u), Offset(tope.x - 1.6f * u, tope.y + 0.4f * u), 1.3f * u, StrokeCap.Round)
        // La cabeza con la capucha y el visor cian.
        val cab = Offset(hx + 0.6f * u, pie - 19.3f * u)
        drawCircle(capa, 2.4f * u, cab)
        drawCircle(borde, 2.4f * u, cab, style = Stroke(0.5f * u))
        drawOval(Color(0xFFC89A7A), Offset(cab.x - 0.2f * u, cab.y - 1.2f * u), Size(2.2f * u, 2.6f * u))
        drawLine(CIAN, Offset(cab.x + 0.1f * u, cab.y - 0.3f * u), Offset(cab.x + 2.3f * u, cab.y - 0.3f * u), 0.8f * u, StrokeCap.Round)
        // El paraguas transparente con su borde de neón: el brazo que lo alza, el mango, la tela y sus varillas.
        val mano = Offset(hx - 3.2f * u, pie - 19.6f * u)
        drawLine(capa, Offset(hx - 1.8f * u, pie - 16 * u), mano, 1.3f * u, StrokeCap.Round)
        val pc = Offset(hx - 1.6f * u, pie - 24.2f * u)
        drawLine(ALAMBRE.copy(alpha = 0.8f), Offset(mano.x, mano.y + 0.8f * u), Offset(pc.x, pc.y - 3.4f * u), 0.6f * u)
        val tela = Rect(pc.x - 7.5f * u, pc.y - 3.4f * u, pc.x + 7.5f * u, pc.y + 3.4f * u)
        drawArc(Color.White.copy(alpha = 0.14f), 180f, 180f, true, tela.topLeft, tela.size)
        listOf(-4.5f, 0f, 4.5f).forEach { dx -> drawLine(Color.White.copy(alpha = 0.3f), Offset(pc.x, pc.y - 3.4f * u), Offset(pc.x + dx * u, pc.y), 0.4f * u) }
        tuboNeon(Path().apply { arcTo(tela, 180f, 180f, true) }, MAGENTA, 0.75f * u)
        drawCircle(ALAMBRE, 0.5f * u, Offset(pc.x, pc.y - 3.6f * u))
    }

    /**
     * Al fondo de la bajada, el puesto se enciende: el letrero de neón (el cuenco con sus palillos y el vapor, formas y no
     * letras), el toldo a rayas, las cortinillas, el mostrador con su luz ámbar y su reflejo en el charco, y dos farolillos de
     * papel colgados de las puntas del toldo con su resplandor.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 58f * u, 48f * u)
        val x = b.x; val y = b.y
        // El resplandor de los dos farolillos (los dos degradados).
        listOf(-24f, 24f).forEach { dx ->
            val c = Offset(x + dx * u, y - 21 * u)
            drawCircle(Brush.radialGradient(listOf(FAROLILLO.copy(alpha = 0.42f), Color.Transparent), c, 10 * u), 10 * u, c)
        }
        // El reflejo en el charco.
        drawOval(MAGENTA.copy(alpha = 0.14f), Offset(x - 20 * u, y - 1.4f * u), Size(40 * u, 3 * u))
        drawOval(AMBAR.copy(alpha = 0.16f), Offset(x - 12 * u, y - 0.8f * u), Size(24 * u, 1.8f * u))
        // Los postes y el mostrador, con su tablero y la luz ámbar de debajo del toldo.
        listOf(-19.5f, 19.5f).forEach { dx -> drawLine(t(ALAMBRE, oscuro), Offset(x + dx * u, y - 2 * u), Offset(x + dx * u, y - 31 * u), 1f * u) }
        drawRect(t(CARCASA, oscuro), Offset(x - 18 * u, y - 11 * u), Size(36 * u, 9 * u))
        for (k in 1 until 6) drawLine(t(CARCASA_LUZ, oscuro), Offset(x - 18 * u + k * 6 * u, y - 10 * u), Offset(x - 18 * u + k * 6 * u, y - 2.5f * u), 0.6f * u)
        drawRect(t(CARCASA_LUZ, oscuro), Offset(x - 19 * u, y - 12.4f * u), Size(38 * u, 1.6f * u))
        drawLine(AMBAR.copy(alpha = 0.35f), Offset(x - 17 * u, y - 13.6f * u), Offset(x + 17 * u, y - 13.6f * u), 2.4f * u)
        drawLine(AMBAR, Offset(x - 17 * u, y - 13.6f * u), Offset(x + 17 * u, y - 13.6f * u), 0.6f * u)
        // Un cuenco humeante en el mostrador.
        drawArc(t(ALAMBRE, oscuro), 0f, 180f, true, Offset(x + 6 * u, y - 15.6f * u), Size(5 * u, 3.4f * u))
        // Las cortinillas rojas, con su emblema redondo.
        for (k in 0 until 5) {
            val cx = x - 16.6f * u + k * 6.8f * u
            drawRect(FAROLILLO.copy(alpha = 0.78f), Offset(cx, y - 26 * u), Size(6 * u, 8 * u))
            drawCircle(VIDRIO.copy(alpha = 0.5f), 1.2f * u, Offset(cx + 3 * u, y - 21.5f * u), style = Stroke(0.5f * u))
        }
        // El toldo a rayas, con el borde festoneado.
        val tl = x - 21 * u; val tw = 42 * u
        drawRect(t(ALAMBRE, oscuro), Offset(tl, y - 31 * u), Size(tw, 5 * u))
        for (k in 0 until 7) if (k % 2 == 0) drawRect(MAGENTA.copy(alpha = 0.8f), Offset(tl + k * 6 * u, y - 31 * u), Size(6 * u, 5 * u))
        for (k in 0 until 7) drawArc(if (k % 2 == 0) MAGENTA.copy(alpha = 0.8f) else t(ALAMBRE, oscuro), 0f, 180f, true, Offset(tl + k * 6 * u, y - 28.4f * u), Size(6 * u, 4 * u))
        // El letrero de neón: el vidrio negro, el marco magenta, el cuenco ámbar con sus palillos y el vapor cian.
        val sg = Rect(x - 15 * u, y - 47 * u, x + 15 * u, y - 33.5f * u)
        drawLine(t(ALAMBRE, oscuro), Offset(x - 10 * u, sg.bottom), Offset(x - 10 * u, y - 31 * u), 0.6f * u)
        drawLine(t(ALAMBRE, oscuro), Offset(x + 10 * u, sg.bottom), Offset(x + 10 * u, y - 31 * u), 0.6f * u)
        drawRoundRect(VIDRIO, sg.topLeft, sg.size, CornerRadius(3 * u))
        tuboNeon(Path().apply { addRoundRect(RoundRect(sg, CornerRadius(3 * u))) }, MAGENTA, 0.9f * u)
        val cu = Offset(x - 3 * u, y - 38.6f * u)
        tuboNeon(Path().apply { moveTo(cu.x - 5 * u, cu.y); lineTo(cu.x + 5 * u, cu.y); quadraticTo(cu.x + 4.6f * u, cu.y + 4 * u, cu.x, cu.y + 4 * u); quadraticTo(cu.x - 4.6f * u, cu.y + 4 * u, cu.x - 5 * u, cu.y) }, AMBAR, 0.7f * u)
        tuboNeon(Path().apply { moveTo(cu.x + 2 * u, cu.y - 0.6f * u); lineTo(cu.x + 8 * u, cu.y - 6 * u); moveTo(cu.x + 3.4f * u, cu.y - 0.4f * u); lineTo(cu.x + 9.4f * u, cu.y - 5 * u) }, SODIO, 0.5f * u)
        listOf(-3f, 0f).forEach { dx ->
            tuboNeon(Path().apply { moveTo(cu.x + dx * u, cu.y - 1.2f * u); quadraticTo(cu.x + (dx - 1.4f) * u, cu.y - 2.8f * u, cu.x + dx * u, cu.y - 4.2f * u); quadraticTo(cu.x + (dx + 1.4f) * u, cu.y - 5.6f * u, cu.x + dx * u, cu.y - 7 * u) }, CIAN, 0.5f * u)
        }
        // Los dos farolillos colgados de las puntas del toldo.
        listOf(-24f, 24f).forEach { dx ->
            val c = Offset(x + dx * u, y - 21 * u)
            drawLine(t(ALAMBRE, oscuro), Offset(c.x - dx / 24f * 3 * u, y - 29 * u), Offset(c.x, c.y - 4 * u), 0.5f * u)
            drawOval(FAROLILLO, Offset(c.x - 3 * u, c.y - 4 * u), Size(6 * u, 8 * u))
            drawOval(mezcla(FAROLILLO, Color.White, 0.5f), Offset(c.x - 1.4f * u, c.y - 2.6f * u), Size(2.2f * u, 4.4f * u))
            for (j in -1..1) drawLine(VIDRIO.copy(alpha = 0.4f), Offset(c.x - 2.8f * u, c.y + j * 2 * u), Offset(c.x + 2.8f * u, c.y + j * 2 * u), 0.4f * u)
            drawRect(VIDRIO, Offset(c.x - 1.6f * u, c.y - 4.6f * u), Size(3.2f * u, 1 * u))
            drawRect(VIDRIO, Offset(c.x - 1.6f * u, c.y + 3.6f * u), Size(3.2f * u, 1 * u))
            drawLine(AMBAR, Offset(c.x, c.y + 4.6f * u), Offset(c.x, c.y + 7 * u), 0.6f * u)
        }
    }
}

/**
 * Refugio: los logros del refugio. El trofeo descansa en una caja de suministros de chapa; en el hall, sobre la consola de la
 * terminal, y su placa es la pantalla de fósforo.
 */
object LogrosRefugio : DisenoLogros {
    private val MURO = Color(0xFFD9CFAF)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val AMARILLO = Color(0xFFE5B436)
    private val FOSFORO = Color(0xFF7DFF8C)
    private val PANTALLA = Color(0xFF041208)
    private val CHAPA = Color(0xFF1C6E6A)
    private val CHAPA_LUZ = Color(0xFF3C928B)
    private val CHAPA_OSC = Color(0xFF124A47)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0F0F), 0.36f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La caja de suministros: chapa verde azulado con su tapa, dos nervaduras estampadas, esquineras de acero remachadas, los
     * patines al pie y la placa amarilla con el engrane del refugio; los dos cierres de palanca van en el metal del nivel.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fr(0.12f, 0.765f, 0.88f, 0.8f)
        val cuerpo = fr(0.14f, 0.8f, 0.86f, 0.942f)
        val patines = listOf(fr(0.18f, 0.942f, 0.36f, 0.955f), fr(0.64f, 0.942f, 0.82f, 0.955f))
        if (!e.hecho) {
            drawPath(Path().apply { addRect(tapa); addRect(cuerpo); patines.forEach { addRect(it) } }, siluetaFuturo(oscuro)); return
        }
        sombraPie(0.1f, 0.9f, oscuro)
        val acero = t(TUBO_OSC, oscuro)
        patines.forEach { drawRect(acero, it.topLeft, it.size) }
        // El cuerpo, con la cara de la derecha en sombra y las dos nervaduras.
        drawRect(t(CHAPA, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(cuerpo.right - w * 0.05f, cuerpo.top), Size(w * 0.05f, cuerpo.height))
        listOf(0.848f, 0.902f).forEach { y ->
            drawRect(t(CHAPA_LUZ, oscuro), Offset(cuerpo.left, h * y), Size(cuerpo.width, h * 0.008f))
            drawRect(t(CHAPA_OSC, oscuro), Offset(cuerpo.left, h * (y + 0.008f)), Size(cuerpo.width, h * 0.006f))
        }
        // Las esquineras de acero, con su remache.
        val gx = w * 0.07f; val gy = h * 0.04f; val gg = maxOf(w * 0.022f, 0.8f * u)
        listOf(cuerpo.topLeft to Offset(1f, 1f), Offset(cuerpo.right, cuerpo.top) to Offset(-1f, 1f),
            Offset(cuerpo.left, cuerpo.bottom) to Offset(1f, -1f), cuerpo.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
            drawRect(acero, Offset(if (s.x > 0) o.x else o.x - gx, if (s.y > 0) o.y else o.y - gg), Size(gx, gg))
            drawRect(acero, Offset(if (s.x > 0) o.x else o.x - gg, if (s.y > 0) o.y else o.y - gy), Size(gg, gy))
            if (fino) remache(Offset(o.x + s.x * gg * 0.5f, o.y + s.y * gg * 0.5f), gg * 0.32f, t(MURO, oscuro), Color.White.copy(alpha = 0.7f))
        }
        // La placa amarilla con el engrane del refugio.
        val pl = fr(0.4f, 0.826f, 0.6f, 0.91f)
        drawRoundRect(t(AMARILLO, oscuro), pl.topLeft, pl.size, CornerRadius(w * 0.008f))
        if (fino) {
            drawPath(engrane(pl.center, pl.height * 0.34f, 8), acero)
            drawCircle(t(AMARILLO, oscuro), pl.height * 0.15f, pl.center)
            drawRoundRect(acero, pl.topLeft, pl.size, CornerRadius(w * 0.008f), style = Stroke(lw))
        } else drawRect(acero, Offset(pl.left + pl.width * 0.25f, pl.center.y - h * 0.008f), Size(pl.width * 0.5f, h * 0.016f))
        // La tapa: un poco más ancha, con su cara de luz.
        drawRect(t(CHAPA_OSC, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(CHAPA_LUZ, oscuro), tapa.topLeft, Size(tapa.width, tapa.height * 0.35f))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(tapa.left, tapa.bottom - lw), Size(tapa.width, lw))
        // Los cierres de palanca, en el metal del nivel.
        listOf(0.23f, 0.71f).forEach { x ->
            val c = fr(x, 0.785f, x + 0.06f, 0.84f)
            chapaNivel(c, e, w * 0.006f)
            if (fino) {
                drawLine(e.metalOscuro, Offset(c.center.x, c.top + c.height * 0.3f), Offset(c.center.x, c.bottom - c.height * 0.12f), lw * 1.2f, StrokeCap.Round)
                drawCircle(e.brillo, c.width * 0.16f, Offset(c.center.x, c.top + c.height * 0.24f))
            }
        }
        if (muyFino) listOf(0.22f, 0.78f).forEach { x -> drawLine(t(AMARILLO, oscuro).copy(alpha = 0.8f), pt(x - 0.02f, 0.925f), pt(x + 0.02f, 0.925f), lw * 1.4f) }
    }

    /**
     * La consola de la terminal del supervisor: el mueble de chapa crema con la pantalla de fósforo encendida, el piloto ámbar,
     * la rejilla, sus tornillos y la placa del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val cuerpo = fr(0.03f, 0.04f, 0.97f, 0.96f)
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.16f)); return }
        drawRoundRect(t(MURO, oscuro), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.16f))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(cuerpo.left + h * 0.16f, cuerpo.top + lw * 0.5f), Size(cuerpo.width - h * 0.32f, lw))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(cuerpo.left + h * 0.16f, cuerpo.bottom - lw * 1.5f), Size(cuerpo.width - h * 0.32f, lw))
        // La pantalla, con sus renglones de fósforo y sus líneas de barrido.
        val pan = fr(0.08f, 0.2f, 0.5f, 0.8f)
        drawRoundRect(PANTALLA, pan.topLeft, pan.size, CornerRadius(h * 0.14f))
        drawPath(Path().apply { moveTo(pan.left + pan.width * 0.1f, pan.top + pan.height * 0.22f); lineTo(pan.left + pan.width * 0.17f, pan.top + pan.height * 0.36f)
            lineTo(pan.left + pan.width * 0.1f, pan.top + pan.height * 0.5f) }, FOSFORO, style = Stroke(lw, cap = StrokeCap.Round))
        listOf(0.36f to 0.82f, 0.66f to 0.6f).forEach { (y, x1) -> drawLine(FOSFORO.copy(alpha = 0.8f), Offset(pan.left + pan.width * 0.25f, pan.top + pan.height * y),
            Offset(pan.left + pan.width * x1, pan.top + pan.height * y), lw * 1.2f) }
        var y = pan.top + h * 0.04f; var k = 0
        val paso = maxOf(h * 0.1f, 1f)
        while (y < pan.bottom - h * 0.03f && k < 40) { drawLine(FOSFORO.copy(alpha = 0.07f), Offset(pan.left + h * 0.05f, y), Offset(pan.right - h * 0.05f, y), lw * 0.6f); y += paso; k++ }
        // El piloto ámbar y la placa del nivel.
        drawCircle(AMARILLO.copy(alpha = 0.3f), h * 0.13f, pt(0.59f, 0.34f))
        drawCircle(AMARILLO, h * 0.07f, pt(0.59f, 0.34f))
        chapaNivel(fr(0.54f, 0.56f, 0.64f, 0.8f), e, u * 0.6f)
        // La rejilla.
        for (j in 0 until 6) drawRoundRect(t(TUBO_OSC, oscuro).copy(alpha = 0.75f), pt(0.7f + j * 0.037f, 0.24f), Size(w * 0.018f, h * 0.52f), CornerRadius(w * 0.009f))
        listOf(0.06f, 0.94f).forEach { x -> listOf(0.22f, 0.78f).forEach { yy -> remache(pt(x, yy), h * 0.04f, t(TUBO_OSC, oscuro), Color.White.copy(alpha = 0.5f)) } }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = FOSFORO
    override fun rotuloPlaca(oscuro: Boolean): Color = AMARILLO

    /** La pantalla de fósforo de la terminal: el bisel de chapa crema con el filo del nivel y la pantalla de esquinas redondas. */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(5 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(5 * u))
        drawRect(e.brillo, Offset(4 * u, 0.5f * u), Size(w - 8 * u, 0.8f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(5 * u), style = Stroke(0.8f * u))
        val b1 = 1.6f * u
        drawRoundRect(t(MURO, oscuro), Offset(b1, b1), Size(w - 2 * b1, h - 2 * b1), CornerRadius(4 * u))
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(b1 + 3 * u, h - b1 - 0.9f * u), Size(w - 2 * b1 - 6 * u, 0.8f * u))
        val b2 = 3.6f * u
        drawRoundRect(PANTALLA, Offset(b2, b2), Size(w - 2 * b2, h - 2 * b2), CornerRadius(4 * u))
    }

    // ---------------------------------------------------------------- el salón del hall

    /** La pintura naranja de las llaves de paso y el cobre de los tubos. */
    private val NARANJA = Color(0xFFD9572B)
    private val COBRE = Color(0xFFB8774A)

    /**
     * La sala común del refugio, bajo tierra. El muro es de paneles de chapa crema remachados, con el zócalo verde azulado;
     * cada columna de trofeos queda enmarcada por una puerta de engranaje rodada a un lado (el medio engrane dentado con su
     * aro amarillo de seguridad y sus pernos) y, al fondo del vano, el átomo del refugio pintado. Entre los vanos bajan los
     * tubos de cobre y de chapa con sus abrazaderas, el manómetro y la llave de paso de cada tramo, y la lámpara enjaulada.
     * Arriba corre el tubo de ventilación con sus abrazaderas y la cinta amarilla y negra; abajo, la rejilla del piso con la
     * franja de seguridad. De día, la luz cálida de las lámparas; en oscuro, el refugio en su ciclo de noche: lámparas de
     * guardia verdes de fósforo, los pilotos ámbar y el muro hundido.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaSalaComun(g, e, oscuro) }
    }

    private fun DrawScope.escenaSalaComun(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val luzLampara = if (oscuro) FOSFORO else Color(0xFFFFD98A)
        val chapa = m(CHAPA, if (oscuro) 0.4f else 0.5f)
        val tubo = m(TUBO_OSC, if (oscuro) 0.5f else 0.42f)

        // Los paneles del muro: las juntas y una fila de remaches a cada lado de cada junta.
        val juntas = Path(); val remaches = Path()
        val altoP = 46f * u; val anchoP = 60f * u
        repetirY(g.friso, g.suelo, altoP, 40) { k, y ->
            juntas.moveTo(0f, y); juntas.lineTo(w, y)
            var x = if (k % 2 == 0) 0f else anchoP / 2; var j = 0
            while (x < w && j < 30) {
                juntas.moveTo(x, y); juntas.lineTo(x, minOf(y + altoP, g.suelo))
                remaches.addOval(Rect(x + 3.2f * u, y + 3.2f * u, x + 5f * u, y + 5f * u))
                remaches.addOval(Rect(x + anchoP / 2, y + 3.2f * u, x + anchoP / 2 + 1.8f * u, y + 5f * u))
                x += anchoP; j++
            }
        }
        drawPath(juntas, m(Color.Black, if (oscuro) 0.5f else 0.12f), style = Stroke(1f * u))
        drawPath(juntas, Color.White.copy(alpha = if (oscuro) 0.02f else 0.3f), style = Stroke(0.6f * u))
        drawPath(remaches, m(TUBO_OSC, if (oscuro) 0.3f else 0.22f))
        // El zócalo verde azulado, con su filete amarillo.
        val zocalo = g.suelo - 34f * u
        drawRect(m(CHAPA, if (oscuro) 0.22f else 0.2f), Offset(0f, zocalo), Size(w, g.suelo - zocalo))
        drawRect(m(AMARILLO, if (oscuro) 0.3f else 0.4f), Offset(0f, zocalo - 2f * u), Size(w, 2f * u))

        // Las puertas de engranaje: el medio engrane dentado sobre cada vano, con su aro y sus pernos, y las jambas.
        for (i in 0 until g.n) {
            val v = g.vano(i); val c = g.cx(i)
            val r = v.width / 2 - 6f * u
            val cy = g.friso + 8f * u + r
            val dientes = 22
            val rueda = Path().apply {
                for (k in 0..dientes * 2) {
                    val a = Math.PI + k * Math.PI / (dientes * 2)
                    val rr = if ((k / 2) % 2 == 0) r + 6f * u else r + 1f * u
                    val px = c + (rr * kotlin.math.cos(a)).toFloat(); val py = cy + (rr * kotlin.math.sin(a)).toFloat()
                    if (k == 0) moveTo(px, py) else lineTo(px, py)
                }
            }
            drawPath(rueda, chapa.copy(alpha = 0.55f), style = Stroke(6f * u, join = StrokeJoin.Round))
            drawArc(m(AMARILLO, if (oscuro) 0.4f else 0.55f), 180f, 180f, false, Offset(c - r + 4f * u, cy - r + 4f * u), Size(2 * r - 8f * u, 2 * r - 8f * u), style = Stroke(2.4f * u))
            // Las rayas negras del aro de seguridad.
            for (k in 0 until 9) {
                val a = 180f + 10f + k * 19f
                drawArc(m(Color.Black, if (oscuro) 0.5f else 0.35f), a, 6f, false, Offset(c - r + 4f * u, cy - r + 4f * u), Size(2 * r - 8f * u, 2 * r - 8f * u), style = Stroke(2.4f * u))
            }
            for (k in 0 until 9) {
                val a = Math.PI + (k + 0.5) * Math.PI / 9
                val p = Offset(c + ((r - 3f * u) * kotlin.math.cos(a)).toFloat(), cy + ((r - 3f * u) * kotlin.math.sin(a)).toFloat())
                drawCircle(m(NARANJA, if (oscuro) 0.4f else 0.5f), 1.8f * u, p)
            }
            listOf(c - r - 3.5f * u, c + r + 3.5f * u).forEach { x ->
                drawLine(chapa.copy(alpha = 0.55f), Offset(x, cy), Offset(x, zocalo), 5f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.25f), Offset(x - 2f * u, cy), Offset(x - 2f * u, zocalo), 1f * u)
            }
            // El átomo del refugio pintado al fondo del vano, apenas: el núcleo y sus tres órbitas.
            val at = Offset(c, cy + r * 0.15f); val ra = minOf(r * 0.55f, 40f * u)
            val tinta = m(CHAPA, if (oscuro) 0.14f else 0.1f)
            for (k in 0 until 3) rotate(k * 60f, at) { drawOval(tinta, Offset(at.x - ra, at.y - ra * 0.36f), Size(2 * ra, ra * 0.72f), style = Stroke(2.2f * u)) }
            drawCircle(tinta, ra * 0.14f, at)
        }

        // La luz de las lámparas enjauladas de las esquinas (las capas planas de su halo).
        for (i in 0..g.n) {
            val x = g.borde(i)
            repetirY(g.friso + 60f * u, g.suelo - 40f * u, g.modulo, 10) { _, y ->
                for (k in 0 until 3) drawCircle(luzLampara.copy(alpha = (if (oscuro) 0.05f else 0.08f)), (40f - k * 12f) * u, Offset(x, y))
            }
        }
        // Los tubos de las esquinas: el de cobre y el de chapa, con sus abrazaderas, el manómetro, la llave y la lámpara.
        for (i in 0..g.n) {
            val x = g.borde(i)
            val xs = listOf(x - g.pil * 0.45f, x + g.pil * 0.45f)
            drawLine(m(COBRE, if (oscuro) 0.4f else 0.55f), Offset(xs[0], g.friso), Offset(xs[0], g.suelo), 5f * u)
            drawLine(tubo, Offset(xs[1], g.friso), Offset(xs[1], g.suelo), 5f * u)
            xs.forEach { xx -> drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.3f), Offset(xx - 1.2f * u, g.friso), Offset(xx - 1.2f * u, g.suelo), 1f * u) }
            repetirY(g.friso + 22f * u, g.suelo, 48f * u, 40) { _, y -> drawRect(m(TUBO_OSC, 0.55f), Offset(x - g.pil * 0.75f, y), Size(g.pil * 1.5f, 2.6f * u)) }
            repetirY(g.friso + 60f * u, g.suelo - 40f * u, g.modulo, 10) { _, y ->
                // La lámpara enjaulada: el casquillo, el foco y la jaula.
                val cl = Offset(x, y)
                drawRect(tubo, Offset(x - 3f * u, y - 9f * u), Size(6f * u, 4f * u))
                drawCircle(luzLampara.copy(alpha = if (oscuro) 0.5f else 0.85f), 4.2f * u, cl)
                drawCircle(Color.White.copy(alpha = if (oscuro) 0.5f else 0.9f), 1.6f * u, Offset(cl.x - 1f * u, cl.y - 1f * u))
                drawCircle(tubo, 5.5f * u, cl, style = Stroke(1f * u))
                drawLine(tubo, Offset(cl.x, cl.y - 5.5f * u), Offset(cl.x, cl.y + 5.5f * u), 0.8f * u)
                drawLine(tubo, Offset(cl.x - 5.5f * u, cl.y), Offset(cl.x + 5.5f * u, cl.y), 0.8f * u)
                // El manómetro, con su cara de fósforo de noche.
                val mc = Offset(x, y + 50f * u)
                if (mc.y < g.suelo - 50f * u)
                    manometro(mc, minOf(g.pil * 0.85f, 10f * u), m(TUBO_OSC, 0.75f), if (oscuro) Color(0xFF0B1A0F) else m(MURO, 0.95f), if (oscuro) FOSFORO else NARANJA,
                        0.35f + 0.4f * azar(e.semilla + i, y.toInt()))
                // La llave de paso: la rueda naranja con sus cuatro radios.
                val ll = Offset(x, y + 104f * u)
                if (ll.y < zocalo - 10f * u) {
                    val rl = minOf(g.pil * 0.7f, 8f * u)
                    val nar = m(NARANJA, if (oscuro) 0.55f else 0.75f)
                    drawCircle(nar, rl, ll, style = Stroke(2f * u))
                    drawLine(nar, Offset(ll.x - rl, ll.y), Offset(ll.x + rl, ll.y), 1.4f * u)
                    drawLine(nar, Offset(ll.x, ll.y - rl), Offset(ll.x, ll.y + rl), 1.4f * u)
                    drawCircle(nar, 2f * u, ll)
                }
                // La placa del tramo, con su piloto ámbar.
                val pl = Offset(x, y + 140f * u)
                if (pl.y < zocalo - 14f * u) {
                    drawRect(m(MURO, if (oscuro) 0.3f else 0.9f), Offset(pl.x - g.pil * 0.7f, pl.y), Size(g.pil * 1.4f, 9f * u))
                    drawRect(m(TUBO_OSC, 0.5f), Offset(pl.x - g.pil * 0.7f, pl.y), Size(g.pil * 1.4f, 9f * u), style = Stroke(0.7f * u))
                    drawCircle(AMARILLO.copy(alpha = if (oscuro) 0.25f else 0.18f), 3.4f * u, Offset(pl.x, pl.y + 4.5f * u))
                    drawCircle(if (oscuro) AMARILLO else NARANJA, 1.3f * u, Offset(pl.x, pl.y + 4.5f * u))
                }
            }
        }

        // El tubo de ventilación del techo, con sus abrazaderas y la cinta amarilla y negra debajo.
        drawRect(m(TUBO_OSC, if (oscuro) 0.45f else 0.35f), Offset.Zero, Size(w, g.friso))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.25f), Offset(0f, g.friso * 0.15f), Size(w, 1.4f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, g.friso * 0.75f), Size(w, g.friso * 0.25f))
        var xa = 20f * u; var ka = 0
        while (xa < w && ka < 40) { drawRect(m(NARANJA, if (oscuro) 0.4f else 0.6f), Offset(xa, 0f), Size(4f * u, g.friso)); xa += 64f * u; ka++ }
        val cinta = Path()
        var xc = -6f * u; var kc = 0
        while (xc < w && kc < 200) {
            cinta.moveTo(xc, g.friso + 4f * u); cinta.lineTo(xc + 4f * u, g.friso); cinta.lineTo(xc + 8f * u, g.friso); cinta.lineTo(xc + 4f * u, g.friso + 4f * u); cinta.close()
            xc += 8f * u; kc++
        }
        drawRect(m(AMARILLO, if (oscuro) 0.4f else 0.65f), Offset(0f, g.friso), Size(w, 4f * u))
        drawPath(cinta, m(Color.Black, if (oscuro) 0.6f else 0.55f))

        // La rejilla del piso y la franja de seguridad.
        drawRect(m(TUBO_OSC, if (oscuro) 0.45f else 0.35f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        drawRect(m(AMARILLO, if (oscuro) 0.45f else 0.7f), Offset(0f, g.suelo), Size(w, 2.5f * u))
        val rejilla = Path()
        var xr = 0f; var kr = 0
        while (xr < w && kr < 300) { rejilla.moveTo(xr, g.suelo + 3f * u); rejilla.lineTo(xr, h); xr += 5f * u; kr++ }
        rejilla.moveTo(0f, g.suelo + (h - g.suelo) * 0.6f); rejilla.lineTo(w, g.suelo + (h - g.suelo) * 0.6f)
        drawPath(rejilla, m(Color.Black, if (oscuro) 0.7f else 0.4f), style = Stroke(1f * u))
        if (oscuro) for (i in 0..g.n) drawRect(FOSFORO.copy(alpha = 0.08f), Offset(g.borde(i) - 20f * u, g.suelo + 3f * u), Size(40f * u, h - g.suelo))
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El vecino del refugio que sale a explorar: el overol verde azulado con el cierre y el cinturón amarillos, las botas, la
     * mochila con la cobija enrollada, la llave inglesa en la mano y el casco amarillo con su lámpara en el color de acento,
     * que echa su haz hacia adelante (de noche, con su halo).
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 12f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val borde = if (oscuro) MURO.copy(alpha = 0.55f) else TUBO_OSC
        val piel = t(Color(0xFFE2B48A), oscuro)
        val traje = t(CHAPA, oscuro); val trajeLuz = t(CHAPA_LUZ, oscuro); val trajeOsc = t(CHAPA_OSC, oscuro)
        val amarillo = t(AMARILLO, oscuro)
        // La mochila, detrás, con la cobija enrollada arriba.
        drawRoundRect(t(COBRE, oscuro), Offset(x - 5.4f * u, pie - 14.6f * u), Size(3.2f * u, 6.4f * u), CornerRadius(0.8f * u))
        drawRoundRect(t(NARANJA, oscuro), Offset(x - 5.9f * u, pie - 16.3f * u), Size(3.8f * u, 1.9f * u), CornerRadius(0.95f * u))
        drawRoundRect(borde.copy(alpha = 0.6f), Offset(x - 5.4f * u, pie - 14.6f * u), Size(3.2f * u, 6.4f * u), CornerRadius(0.8f * u), style = Stroke(0.45f * u))
        // Las piernas con sus botas.
        listOf(-1.3f, 1.3f).forEach { dx ->
            drawLine(trajeOsc, Offset(x + dx * u, pie - 7.4f * u), Offset(x + dx * u, pie - 1.4f * u), 2f * u)
            drawRoundRect(t(TUBO_OSC, oscuro), Offset(x + dx * u - 1.2f * u, pie - 1.9f * u), Size(2.7f * u, 1.9f * u), CornerRadius(0.5f * u))
            if (oscuro) drawRoundRect(borde, Offset(x + dx * u - 1.2f * u, pie - 1.9f * u), Size(2.7f * u, 1.9f * u), CornerRadius(0.5f * u), style = Stroke(0.4f * u))
        }
        // Los brazos: el izquierdo cuelga; el derecho lleva la llave inglesa (con su contorno, para que se lean sobre el mapa).
        val manoI = Offset(x - 3.9f * u, pie - 8.8f * u); val manoD = Offset(x + 5.2f * u, pie - 9.8f * u)
        listOf(Offset(x - 2.8f * u, pie - 14.2f * u) to manoI, Offset(x + 2.8f * u, pie - 14.2f * u) to manoD).forEach { (a, m) ->
            drawLine(borde, a, m, 2.4f * u, StrokeCap.Round); drawLine(traje, a, m, 1.6f * u, StrokeCap.Round)
        }
        val llave = if (oscuro) mezcla(MURO, Color(0xFF0A0F0F), 0.3f) else TUBO_OSC
        drawLine(llave, manoD, Offset(manoD.x + 2.4f * u, manoD.y + 4.2f * u), 0.9f * u, StrokeCap.Round)
        drawArc(llave, 200f, 260f, false, Offset(manoD.x + 1.5f * u, manoD.y + 3.8f * u), Size(2.2f * u, 2.2f * u), style = Stroke(0.8f * u))
        drawCircle(piel, 0.8f * u, manoI); drawCircle(piel, 0.8f * u, manoD)
        // El overol: el cuerpo, el filo de luz, el cierre y el cinturón amarillos.
        val cuerpo = Path().apply { moveTo(x - 3 * u, pie - 15 * u); lineTo(x + 3 * u, pie - 15 * u); lineTo(x + 3.2f * u, pie - 7 * u); lineTo(x - 3.2f * u, pie - 7 * u); close() }
        drawPath(cuerpo, traje)
        drawLine(trajeLuz, Offset(x - 2.2f * u, pie - 14.4f * u), Offset(x - 2.3f * u, pie - 7.8f * u), 0.8f * u)
        drawLine(amarillo, Offset(x, pie - 15 * u), Offset(x, pie - 8.6f * u), 0.9f * u)
        drawLine(amarillo, Offset(x - 3.1f * u, pie - 8.2f * u), Offset(x + 3.1f * u, pie - 8.2f * u), 1f * u)
        drawPath(cuerpo, borde, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        drawLine(amarillo, Offset(x - 1.6f * u, pie - 15 * u), Offset(x + 1.6f * u, pie - 15 * u), 0.9f * u, StrokeCap.Round)
        // La cabeza y el casco con su lámpara.
        val cab = Offset(x, pie - 17.3f * u)
        drawCircle(piel, 2.3f * u, cab)
        drawCircle(t(TUBO_OSC, oscuro), 0.35f * u, Offset(cab.x + 0.9f * u, cab.y + 0.2f * u))
        val lampara = Offset(cab.x + 2.3f * u, cab.y - 1.9f * u)
        drawPath(Path().apply { moveTo(lampara.x, lampara.y - 0.6f * u); lineTo(lampara.x + 10 * u, lampara.y - 2 * u); lineTo(lampara.x + 10 * u, lampara.y + 4.4f * u); lineTo(lampara.x, lampara.y + 0.6f * u); close() },
            e.acento.copy(alpha = if (oscuro) 0.26f else 0.2f))
        drawArc(amarillo, 180f, 180f, true, Offset(cab.x - 2.8f * u, cab.y - 3.5f * u), Size(5.6f * u, 5.2f * u))
        drawLine(amarillo, Offset(cab.x - 3.4f * u, cab.y - 0.9f * u), Offset(cab.x + 3.2f * u, cab.y - 0.9f * u), 0.9f * u, StrokeCap.Round)
        drawArc(borde, 180f, 180f, false, Offset(cab.x - 2.8f * u, cab.y - 3.5f * u), Size(5.6f * u, 5.2f * u), style = Stroke(0.45f * u))
        if (oscuro) { drawCircle(e.acento.copy(alpha = 0.14f), 5.5f * u, lampara); drawCircle(e.acento.copy(alpha = 0.3f), 2.4f * u, lampara) }
        drawCircle(t(TUBO_OSC, oscuro), 1.1f * u, lampara)
        drawCircle(e.acento, 0.8f * u, lampara)
    }

    /**
     * La salida a la superficie: la loma con su pasto, la boca redonda del refugio con el anillo de franjas de aviso y el
     * pasillo encendido al fondo, la compuerta en forma de engrane rodada a un lado con su volante amarillo y un brote nuevo.
     * De día el sol asoma detrás de la loma con sus rayos; de noche, la luna y las estrellas, y la luz del pasillo se derrama
     * por la boca.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 54f * u, 46f * u)
        val x = b.x; val y = b.y
        val tierra = t(Color(0xFF8A6A44), oscuro); val pasto = t(Color(0xFF6B8A3A), oscuro)
        val borde = if (oscuro) MURO.copy(alpha = 0.45f) else TUBO_OSC
        val boca = Offset(x + 7 * u, y - 13 * u)
        // El cielo.
        if (!oscuro) {
            val sol = Offset(x + 5 * u, y - 27 * u)
            for (k in 0 until 9) {
                val a = Math.PI * (1.0 + k / 8.0)
                drawLine(AMARILLO, Offset(sol.x + (11 * u * kotlin.math.cos(a)).toFloat(), sol.y + (11 * u * kotlin.math.sin(a)).toFloat()),
                    Offset(sol.x + (16.5f * u * kotlin.math.cos(a)).toFloat(), sol.y + (16.5f * u * kotlin.math.sin(a)).toFloat()), 1.4f * u, StrokeCap.Round)
            }
            drawCircle(AMARILLO, 8.6f * u, sol)
        } else {
            val luna = Offset(x - 14 * u, y - 38 * u)
            val creciente = Path.combine(androidx.compose.ui.graphics.PathOperation.Difference,
                Path().apply { addOval(Rect(luna.x - 4.2f * u, luna.y - 4.2f * u, luna.x + 4.2f * u, luna.y + 4.2f * u)) },
                Path().apply { addOval(Rect(luna.x - 2.4f * u, luna.y - 5.4f * u, luna.x + 5.6f * u, luna.y + 2.6f * u)) })
            drawPath(creciente, MURO.copy(alpha = 0.9f))
            listOf(Offset(-4f, -42f), Offset(6f, -40f), Offset(16f, -35f), Offset(-22f, -30f), Offset(22f, -43f), Offset(-8f, -32f)).forEachIndexed { k, o ->
                drawCircle(MURO.copy(alpha = 0.5f + 0.4f * azar(3, k)), (0.5f + 0.5f * azar(5, k)) * u, Offset(x + o.x * u, y + o.y * u))
            }
        }
        // La loma, con sus vetas de tierra y el pasto en la cresta.
        val cresta = Path().apply { moveTo(x - 26 * u, y); cubicTo(x - 22 * u, y - 14 * u, x - 6 * u, y - 27 * u, x + 8 * u, y - 27 * u); cubicTo(x + 20 * u, y - 27 * u, x + 26 * u, y - 12 * u, x + 27 * u, y) }
        drawPath(Path().apply { addPath(cresta); close() }, tierra)
        clipPath(Path().apply { addPath(cresta); close() }) {
            for (k in 0 until 5) drawLine(Color.Black.copy(alpha = 0.12f), Offset(x - 26 * u, y - (3 + k * 5) * u), Offset(x + 27 * u, y - (5 + k * 5) * u), 0.7f * u)
        }
        drawPath(cresta, pasto, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        for (k in 0 until 9) {
            val gx = x - 18 * u + k * 4.4f * u; val gy = y - 22 * u + kotlin.math.abs(k - 5) * 2.2f * u
            drawLine(pasto, Offset(gx, gy), Offset(gx + 0.8f * u, gy - 2.4f * u), 0.7f * u)
        }
        // La boca: el pasillo encendido al fondo y el anillo con franjas de aviso.
        if (oscuro) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE2A0).copy(alpha = 0.42f), Color.Transparent), boca, 21 * u), 21 * u, boca)
        drawCircle(if (oscuro) Color(0xFFFFE8B0) else Color(0xFF2E3436), 9 * u, boca)
        drawCircle(if (oscuro) Color(0xFFFFF6DA) else mezcla(MURO, Color(0xFF2E3436), 0.35f), 3.6f * u, Offset(boca.x + 0.6f * u, boca.y + 0.4f * u))
        for (k in 0 until 6) {
            val a = k * Math.PI / 3
            drawLine((if (oscuro) Color(0xFFC9A860) else Color(0xFF5E666A)).copy(alpha = 0.7f), Offset(boca.x + (3.6f * u * kotlin.math.cos(a)).toFloat(), boca.y + (3.6f * u * kotlin.math.sin(a)).toFloat()),
                Offset(boca.x + (8.6f * u * kotlin.math.cos(a)).toFloat(), boca.y + (8.6f * u * kotlin.math.sin(a)).toFloat()), 0.6f * u)
        }
        val anillo = Rect(boca.x - 10.2f * u, boca.y - 10.2f * u, boca.x + 10.2f * u, boca.y + 10.2f * u)
        drawArc(t(AMARILLO, oscuro), 0f, 360f, false, anillo.topLeft, anillo.size, style = Stroke(2.6f * u))
        for (k in 0 until 10) drawArc(t(TUBO_OSC, oscuro), k * 36f, 18f, false, anillo.topLeft, anillo.size, style = Stroke(2.6f * u))
        drawCircle(borde, 11.5f * u, boca, style = Stroke(0.5f * u))
        // La compuerta de engrane rodada a un lado, con sus pernos y su volante amarillo.
        val pc = Offset(x - 10 * u, y - 10.6f * u)
        val disco = engrane(pc, 10.2f * u, 12)
        drawPath(disco, t(Color(0xFF8C9496), oscuro))
        drawPath(disco, borde, style = Stroke(0.6f * u, join = StrokeJoin.Round))
        drawCircle(t(CHAPA, oscuro), 7.4f * u, pc)
        drawCircle(t(CHAPA_LUZ, oscuro), 7.4f * u, pc, style = Stroke(0.8f * u))
        for (k in 0 until 8) { val a = k * Math.PI / 4; drawCircle(t(Color(0xFFB8C0C2), oscuro), 0.6f * u, Offset(pc.x + (5.9f * u * kotlin.math.cos(a)).toFloat(), pc.y + (5.9f * u * kotlin.math.sin(a)).toFloat())) }
        // El volante de cierre, amarillo, con sus cuatro rayos.
        drawCircle(t(AMARILLO, oscuro), 3.6f * u, pc, style = Stroke(1.1f * u))
        listOf(0f, 90f).forEach { g -> rotate(g + 45f, pc) { drawLine(t(AMARILLO, oscuro), Offset(pc.x - 3.6f * u, pc.y), Offset(pc.x + 3.6f * u, pc.y), 0.9f * u) } }
        drawCircle(t(TUBO_OSC, oscuro), 1.1f * u, pc)
        // El brote nuevo junto a la boca.
        val br = Offset(x + 20 * u, y - 1.5f * u)
        drawLine(pasto, br, Offset(br.x, br.y - 5 * u), 0.8f * u)
        drawOval(t(Color(0xFF8FCB6A), oscuro), Offset(br.x - 3.2f * u, br.y - 6.4f * u), Size(3.2f * u, 1.8f * u))
        drawOval(t(Color(0xFF8FCB6A), oscuro), Offset(br.x, br.y - 7.6f * u), Size(3.2f * u, 1.8f * u))
    }
}

/**
 * Orden: los logros de la orden. El trofeo descansa en la peana de acero de la sala capitular, con su colgadura carmesí; en el
 * hall, sobre un capitel remachado, y su placa es la plancha ochavada del archivo sellado.
 */
object LogrosOrden : DisenoLogros {
    private val ACERO = Color(0xFF3C424A)
    private val ACERO_LUZ = Color(0xFF6A727C)
    private val ACERO_OSC = Color(0xFF262A30)
    private val LATON = Color(0xFFB8933E)
    private val LATON_LUZ = Color(0xFFE0C070)
    private val ESMALTE = Color(0xFF0C0D10)
    private val CARMESI = Color(0xFF9E1B22)
    private val CARMESI_OSC = Color(0xFF6A1016)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.35f) else c

    /** El latón: se hunde menos que el acero, para que siga brillando en oscuro. */
    private fun placa(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.16f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La peana de la sala capitular: tres cuerpos de acero (la cornisa con su filete de latón, el dado remachado y la basa en dos
     * gradas) y la colgadura carmesí que cae por el frente desde su barra de latón, con sus pliegues, su fleco y la punta en
     * cola de golondrina; sobre el paño, el engrane de la Orden en el metal del nivel.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cornisa = fr(0.12f, 0.765f, 0.88f, 0.795f)
        val dado = fr(0.18f, 0.795f, 0.82f, 0.925f)
        val grada = fr(0.15f, 0.925f, 0.85f, 0.94f)
        val basa = fr(0.12f, 0.94f, 0.88f, 0.955f)
        if (!e.hecho) {
            drawPath(Path().apply { addRect(cornisa); addRect(dado); addRect(grada); addRect(basa) }, siluetaFuturo(oscuro)); return
        }
        sombraPie(0.08f, 0.92f, oscuro)
        // La basa y el dado, con la cara de la derecha en sombra.
        drawRect(t(ACERO_OSC, oscuro), basa.topLeft, basa.size)
        drawRect(t(ACERO, oscuro), grada.topLeft, grada.size)
        drawRect(t(ACERO_LUZ, oscuro), grada.topLeft, Size(grada.width, maxOf(h * 0.003f, 0.5f * u)))
        drawRect(t(ACERO, oscuro), dado.topLeft, dado.size)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(dado.right - w * 0.05f, dado.top), Size(w * 0.05f, dado.height))
        drawRect(t(ACERO_LUZ, oscuro).copy(alpha = 0.5f), dado.topLeft, Size(w * 0.02f, dado.height))
        // Los remaches del dado, a los dos lados del paño.
        if (fino) (if (muyFino) listOf(0.825f, 0.86f, 0.895f) else listOf(0.84f, 0.895f)).forEach { y ->
            listOf(0.23f, 0.77f).forEach { x -> remache(pt(x, y), h * 0.006f, t(ACERO_LUZ, oscuro), Color.White.copy(alpha = 0.55f)) }
        }
        // La colgadura carmesí, con sus pliegues y su fleco de latón.
        val pano = poli(0.35f, 0.8f, 0.65f, 0.8f, 0.64f, 0.906f, 0.5f, 0.884f, 0.36f, 0.906f)
        drawPath(pano, t(CARMESI, oscuro))
        drawPath(poli(0.58f, 0.8f, 0.65f, 0.8f, 0.64f, 0.906f, 0.6f, 0.9f), Color.Black.copy(alpha = 0.18f))
        if (fino) {
            listOf(0.43f, 0.57f).forEach { x -> drawLine(t(CARMESI_OSC, oscuro), pt(x, 0.81f), pt(x + (x - 0.5f) * 0.1f, 0.89f), lw) }
            val n = if (muyFino) 9 else 6
            for (k in 0..n) {
                val f = k / n.toFloat()
                val x = 0.36f + 0.28f * f; val y = if (f < 0.5f) 0.906f - 0.044f * f else 0.884f + 0.044f * (f - 0.5f)
                drawLine(placa(LATON, oscuro), pt(x, y), pt(x, y + 0.014f), lw)
            }
        }
        // El engrane de la Orden, en el metal del nivel.
        val c = pt(0.5f, 0.84f); val r = h * 0.028f
        drawPath(engrane(c, r, 8), e.base)
        if (fino) drawPath(engrane(c, r, 8), e.metalOscuro, style = Stroke(lw * 0.8f))
        drawCircle(e.brillo, r * 0.62f, c)
        drawCircle(t(CARMESI, oscuro), r * 0.34f, c)
        if (e.nivel >= 4 && fino) drawPath(estrella(Offset(c.x + r * 0.9f, c.y - r * 0.9f), r * 0.5f, 4, 0.25f), Color.White.copy(alpha = 0.9f))
        // La barra de latón de la colgadura, con sus remates.
        drawRect(placa(LATON, oscuro), pt(0.33f, 0.795f), Size(w * 0.34f, h * 0.01f))
        listOf(0.33f, 0.67f).forEach { x -> drawCircle(placa(LATON_LUZ, oscuro), h * 0.007f, pt(x, 0.8f)) }
        // La cornisa, con su cara de luz y su filete de latón.
        drawRect(t(ACERO, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(ACERO_LUZ, oscuro), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.38f))
        drawRect(placa(LATON, oscuro), Offset(cornisa.left, cornisa.bottom - maxOf(h * 0.005f, 0.6f * u)), Size(cornisa.width, maxOf(h * 0.005f, 0.6f * u)))
        if (muyFino) for (k in 0 until 9) drawCircle(t(ACERO_OSC, oscuro), h * 0.003f, pt(0.16f + k * 0.085f, 0.782f))
    }

    /**
     * El capitel de acero de la sala: el bloque remachado con su cornisa y el filete de latón, el paño carmesí al centro y la
     * cruz de la Orden en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val cornisa = fr(0.02f, 0f, 0.98f, 0.2f)
        val bloque = fr(0.06f, 0.2f, 0.94f, 1f)
        if (!e.hecho) { drawPath(Path().apply { addRect(cornisa); addRect(bloque) }, siluetaFuturo(oscuro)); return }
        drawRect(t(ACERO, oscuro), bloque.topLeft, bloque.size)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(bloque.right - w * 0.04f, bloque.top), Size(w * 0.04f, bloque.height))
        drawRect(t(ACERO_OSC, oscuro), Offset(bloque.left, bloque.bottom - h * 0.12f), Size(bloque.width, h * 0.12f))
        listOf(0.12f, 0.24f, 0.76f, 0.88f).forEach { x -> remache(pt(x, 0.55f), h * 0.07f, t(ACERO_LUZ, oscuro), Color.White.copy(alpha = 0.5f)) }
        // El paño carmesí.
        drawPath(poli(0.38f, 0.2f, 0.62f, 0.2f, 0.62f, 0.94f, 0.5f, 0.8f, 0.38f, 0.94f), t(CARMESI, oscuro))
        listOf(0.44f, 0.56f).forEach { x -> drawLine(t(CARMESI_OSC, oscuro), pt(x, 0.24f), pt(x, 0.8f), lw * 0.7f) }
        // La cruz de la Orden, en el metal del nivel.
        val c = pt(0.5f, 0.48f); val r = h * 0.22f
        drawPath(cruzPatada(c, r), e.base)
        drawPath(cruzPatada(c, r), e.metalOscuro, style = Stroke(lw * 0.6f))
        drawCircle(e.brillo, r * 0.22f, c)
        // La cornisa con su filete.
        drawRect(t(ACERO, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(ACERO_LUZ, oscuro), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.45f))
        drawRect(placa(LATON, oscuro), Offset(cornisa.left, cornisa.bottom - lw), Size(cornisa.width, lw))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFEDE6D2)
    override fun rotuloPlaca(oscuro: Boolean): Color = LATON_LUZ

    /**
     * La plancha ochavada del archivo sellado: el filete del nivel con las esquinas cortadas, la banda de acero con un remache en
     * cada esquina y el hueco de esmalte negro donde va el nombre.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val todo = Rect(0f, 0f, w, h)
        if (!e.hecho) { drawPath(ochavado(todo, 5 * u), siluetaFuturo(oscuro)); return }
        drawPath(ochavado(todo, 5 * u), e.base)
        drawLine(e.brillo, Offset(5 * u, 0.8f * u), Offset(w - 5 * u, 0.8f * u), 0.9f * u)
        drawPath(ochavado(todo, 5 * u), e.metalOscuro, style = Stroke(0.8f * u))
        val b1 = 1.4f * u
        drawPath(ochavado(Rect(b1, b1, w - b1, h - b1), 4.4f * u), t(ACERO, oscuro))
        val b2 = 3.4f * u
        drawPath(ochavado(Rect(b2, b2, w - b2, h - b2), 3.6f * u), ESMALTE)
        drawPath(ochavado(Rect(b2, b2, w - b2, h - b2), 3.6f * u), t(ACERO_LUZ, oscuro).copy(alpha = 0.5f), style = Stroke(0.6f * u))
        listOf(Offset(4.6f * u, 2.4f * u), Offset(w - 4.6f * u, 2.4f * u), Offset(4.6f * u, h - 2.4f * u), Offset(w - 4.6f * u, h - 2.4f * u))
            .forEach { remache(it, 0.8f * u, t(ACERO_LUZ, oscuro), Color.White.copy(alpha = 0.55f)) }
    }

    // ---------------------------------------------------------------- el salón del hall

    /** El vidrio ahumado de los ventanales de la sala capitular y la luz que entra por ellos. */
    private val VIDRIERA = Color(0xFF8FB3C8)

    /**
     * La sala capitular de acero. El muro es de grandes planchas con sus juntas y sus remaches avellanados en columna; detrás
     * de cada columna de trofeos se abre un ventanal alto de acero, de arco apuntado, con su retícula de montantes y el vidrio
     * ahumado (de día entra por él la luz fría; de noche se ven las estrellas sobre la ciudad), coronado por el dintel de acero
     * en escalones con su filete de latón. Entre los ventanales se levantan los pilares de viga doble remachada, y de cada
     * uno, en cada tramo de ≈ 230 dp, cuelga el estandarte carmesí con el engrane bordado en latón y, encima, el aplique de
     * latón con su llama. Arriba, la cornisa con su friso de engranes; abajo, el zócalo de acero y el piso de chapa estriada.
     * En oscuro, la sala se hunde y solo arden los apliques.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaCapitular(g, e, oscuro) }
    }

    private fun DrawScope.escenaCapitular(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val llama = Color(0xFFFFC46A)
        val laton = m(LATON, if (oscuro) 0.6f else 0.6f)
        val latonLuz = m(LATON_LUZ, if (oscuro) 0.55f else 0.6f)

        // Las planchas del muro: juntas verticales y horizontales, y los remaches en columna junto a cada junta.
        val juntas = Path(); val remaches = Path()
        val anchoP = 52f * u; val altoP = 92f * u
        repetirY(g.friso, g.suelo, altoP, 20) { _, y -> juntas.moveTo(0f, y); juntas.lineTo(w, y) }
        var xj = anchoP / 2; var kj = 0
        while (xj < w && kj < 30) {
            juntas.moveTo(xj, g.friso); juntas.lineTo(xj, g.suelo)
            repetirY(g.friso + 6f * u, g.suelo, 14f * u, 110) { _, y -> remaches.addOval(Rect(xj + 2.6f * u, y, xj + 4.2f * u, y + 1.6f * u)) }
            xj += anchoP; kj++
        }
        drawPath(juntas, m(ACERO_OSC, if (oscuro) 0.45f else 0.16f), style = Stroke(1f * u))
        drawPath(remaches, m(ACERO_OSC, if (oscuro) 0.4f else 0.16f))

        // Los ventanales: el vano apuntado con el vidrio ahumado, la retícula, el marco y la luz que entra.
        val luces = Path()
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val r = Rect(v.left + 5f * u, g.friso + 18f * u, v.right - 5f * u, g.suelo - 30f * u)
            if (r.height < 60f * u) continue
            val arco = arcoApuntado(r, r.width * 0.75f)
            drawPath(arco, if (oscuro) m(Color(0xFF0B1424), 0.9f) else m(VIDRIERA, 0.16f))
            clipPath(arco) {
                if (oscuro) for (k in 0 until 24) {
                    val p = Offset(r.left + r.width * azar(e.semilla + i, k), r.top + r.height * 0.6f * azar(e.semilla + i, k + 40))
                    drawCircle(Color.White.copy(alpha = 0.25f + 0.35f * azar(e.semilla + i, k + 80)), (0.5f + 0.6f * azar(e.semilla + i, k + 120)) * u, p)
                }
                // La retícula: montantes y travesaños.
                val ret = Path()
                listOf(0.33f, 0.67f).forEach { f -> ret.moveTo(r.left + r.width * f, r.top); ret.lineTo(r.left + r.width * f, r.bottom) }
                repetirY(r.top + r.width * 0.75f, r.bottom, 42f * u, 40) { _, y -> ret.moveTo(r.left, y); ret.lineTo(r.right, y) }
                drawPath(ret, m(ACERO, if (oscuro) 0.4f else 0.2f), style = Stroke(1.6f * u))
                // De noche, la ciudad a lo lejos al pie del ventanal: las siluetas y unas pocas luces.
                if (oscuro) {
                    var xb = r.left; var kb = 0
                    while (xb < r.right && kb < 20) {
                        val bw = (8f + 10f * azar(e.semilla + i, kb + 200)) * u; val bh = (20f + 50f * azar(e.semilla + i, kb + 220)) * u
                        drawRect(m(ACERO_OSC, 0.35f), Offset(xb, r.bottom - bh), Size(bw, bh))
                        if (kb % 2 == 0) drawRect(llama.copy(alpha = 0.35f), Offset(xb + bw * 0.4f, r.bottom - bh * 0.7f), Size(1.4f * u, 1.4f * u))
                        xb += bw + 1f * u; kb++
                    }
                }
            }
            drawPath(arco, m(ACERO, if (oscuro) 0.5f else 0.38f), style = Stroke(3f * u))
            drawPath(arco, m(ACERO_LUZ, if (oscuro) 0.25f else 0.4f), style = Stroke(1f * u))
            // El alféizar.
            drawRect(m(ACERO, if (oscuro) 0.5f else 0.4f), Offset(r.left - 4f * u, r.bottom), Size(r.width + 8f * u, 4f * u))
            if (!oscuro) luces.addPath(Path().apply {
                moveTo(r.left + r.width * 0.15f, r.top + r.width * 0.4f); lineTo(r.right - r.width * 0.2f, r.top + r.width * 0.3f)
                lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
            })
            // El dintel de acero en escalones sobre el ventanal, con el filete de latón y el engrane al centro.
            val c = g.cx(i); val y0 = g.friso
            val esc = Path().apply {
                moveTo(v.left, y0 + 16f * u); lineTo(v.left, y0 + 8f * u); lineTo(v.left + v.width * 0.18f, y0 + 8f * u)
                lineTo(v.left + v.width * 0.18f, y0 + 2f * u); lineTo(v.right - v.width * 0.18f, y0 + 2f * u); lineTo(v.right - v.width * 0.18f, y0 + 8f * u)
                lineTo(v.right, y0 + 8f * u); lineTo(v.right, y0 + 16f * u)
            }
            drawPath(esc, m(ACERO, if (oscuro) 0.55f else 0.42f), style = Stroke(3.5f * u))
            drawLine(laton.copy(alpha = 0.7f), Offset(v.left + v.width * 0.18f, y0 + 2f * u), Offset(v.right - v.width * 0.18f, y0 + 2f * u), 1.2f * u)
            drawPath(engrane(Offset(c, y0 + 8f * u), 5.5f * u, 8), laton)
            drawCircle(m(ESMALTE, 0.8f), 2f * u, Offset(c, y0 + 8f * u))
        }
        // La luz fría de los ventanales, que cae sobre el piso (un degradado, solo de día).
        if (!oscuro) drawPath(luces, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = 0.22f)), g.friso, g.suelo))

        // Los pilares de viga doble remachada, con el estandarte y el aplique en cada tramo.
        for (i in 0..g.n) {
            val x = g.borde(i); val a = g.pil
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(x - a + 3f * u, g.friso), Size(2 * a, g.suelo - g.friso))
            drawRect(m(ACERO, if (oscuro) 0.6f else 0.5f), Offset(x - a, g.friso), Size(2 * a, g.suelo - g.friso))
            drawRect(m(ACERO_OSC, if (oscuro) 0.6f else 0.5f), Offset(x - a, g.friso), Size(a * 0.28f, g.suelo - g.friso))
            drawRect(m(ACERO_OSC, if (oscuro) 0.6f else 0.5f), Offset(x + a * 0.72f, g.friso), Size(a * 0.28f, g.suelo - g.friso))
            drawRect(m(ACERO_LUZ, if (oscuro) 0.4f else 0.5f), Offset(x - a * 0.72f, g.friso), Size(1.2f * u, g.suelo - g.friso))
            val rem = Path()
            repetirY(g.friso + 8f * u, g.suelo, 12f * u, 120) { _, y ->
                rem.addOval(Rect(x - a * 0.86f - 0.9f * u, y, x - a * 0.86f + 0.9f * u, y + 1.8f * u))
                rem.addOval(Rect(x + a * 0.86f - 0.9f * u, y, x + a * 0.86f + 0.9f * u, y + 1.8f * u))
            }
            drawPath(rem, m(ACERO_LUZ, if (oscuro) 0.4f else 0.6f))
            // El capitel y la basa del pilar.
            drawRect(m(ACERO_OSC, if (oscuro) 0.7f else 0.55f), Offset(x - a - 3f * u, g.friso), Size(2 * a + 6f * u, 5f * u))
            drawRect(m(ACERO_OSC, if (oscuro) 0.7f else 0.55f), Offset(x - a - 3f * u, g.suelo - 6f * u), Size(2 * a + 6f * u, 6f * u))
            drawLine(laton, Offset(x - a - 3f * u, g.friso + 5f * u), Offset(x + a + 3f * u, g.friso + 5f * u), 1f * u)
            repetirY(g.friso + 40f * u, g.suelo - 80f * u, g.modulo, 10) { _, y0 ->
                // El aplique: la copa de latón, su llama y el halo (capas planas).
                val ap = Offset(x, y0)
                val halo = if (oscuro) 0.08f else 0.05f
                for (k in 0 until 3) drawCircle(llama.copy(alpha = halo), (30f - k * 9f) * u, ap)
                drawPath(Path().apply { moveTo(x - 5f * u, ap.y + 2f * u); lineTo(x + 5f * u, ap.y + 2f * u); lineTo(x + 2f * u, ap.y + 7f * u); lineTo(x - 2f * u, ap.y + 7f * u); close() }, laton)
                drawLine(laton, Offset(x, ap.y + 7f * u), Offset(x, ap.y + 12f * u), 1.4f * u)
                drawPath(Path().apply { moveTo(x, ap.y - 7f * u); quadraticTo(x + 3.2f * u, ap.y - 1f * u, x, ap.y + 2f * u); quadraticTo(x - 3.2f * u, ap.y - 1f * u, x, ap.y - 7f * u); close() },
                    llama.copy(alpha = if (oscuro) 0.95f else 0.8f))
                drawCircle(Color.White.copy(alpha = 0.7f), 1f * u, Offset(x, ap.y - 1f * u))
                // El estandarte carmesí colgado de su barra, con su cola en punta, la orla de latón y el engrane bordado.
                val yb = y0 + 24f * u; val an = a + 2f * u; val largo = minOf(110f * u, g.suelo - 20f * u - yb)
                if (largo < 40f * u) return@repetirY
                drawLine(laton, Offset(x - an - 3f * u, yb), Offset(x + an + 3f * u, yb), 2f * u, StrokeCap.Round)
                val est = Path().apply { moveTo(x - an, yb); lineTo(x + an, yb); lineTo(x + an, yb + largo); lineTo(x, yb + largo - 14f * u); lineTo(x - an, yb + largo); close() }
                drawPath(est, m(CARMESI, if (oscuro) 0.75f else 0.72f))
                drawRect(m(CARMESI_OSC, if (oscuro) 0.7f else 0.6f), Offset(x + an * 0.45f, yb), Size(an * 0.55f, largo - 8f * u))
                drawPath(est, laton.copy(alpha = 0.8f), style = Stroke(1f * u))
                drawLine(laton.copy(alpha = 0.6f), Offset(x - an + 3f * u, yb + 6f * u), Offset(x + an - 3f * u, yb + 6f * u), 0.8f * u)
                val ce = Offset(x, yb + largo * 0.38f)
                drawPath(engrane(ce, minOf(an * 0.7f, 9f * u), 8), latonLuz)
                drawCircle(m(CARMESI, 0.8f), minOf(an * 0.25f, 3.2f * u), ce)
                drawLine(laton.copy(alpha = 0.5f), Offset(x, ce.y + an), Offset(x, yb + largo - 22f * u), 1f * u)
            }
        }

        // La cornisa: la viga de arriba con su friso de engranes y el canto de latón.
        drawRect(m(ACERO, if (oscuro) 0.55f else 0.45f), Offset.Zero, Size(w, g.friso))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, g.friso - 2f * u), Size(w, 2f * u))
        drawLine(laton, Offset(0f, g.friso - 0.5f * u), Offset(w, g.friso - 0.5f * u), 1f * u)
        var xe = 14f * u; var ke = 0
        while (xe < w && ke < 60) { drawPath(engrane(Offset(xe, g.friso * 0.45f), g.friso * 0.28f, 8), m(ACERO_OSC, if (oscuro) 0.6f else 0.45f)); xe += 28f * u; ke++ }

        // El zócalo de acero y el piso de chapa estriada.
        drawRect(m(ACERO_OSC, if (oscuro) 0.5f else 0.35f), Offset(0f, g.suelo - 3f * u), Size(w, 3f * u))
        drawRect(m(ACERO, if (oscuro) 0.4f else 0.32f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        val estrias = Path()
        var xr = 3f * u; var kr = 0
        val yr = g.suelo + (h - g.suelo) / 2
        while (xr < w && kr < 300) {
            val s = if (kr % 2 == 0) 1f else -1f
            estrias.moveTo(xr - 2f * u, yr + s * 2f * u); estrias.lineTo(xr + 2f * u, yr - s * 2f * u)
            xr += 7f * u; kr++
        }
        drawPath(estrias, m(ACERO_OSC, if (oscuro) 0.6f else 0.45f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawLine(m(ACERO_LUZ, if (oscuro) 0.35f else 0.5f), Offset(0f, g.suelo + 0.6f * u), Offset(w, g.suelo + 0.6f * u), 1f * u)
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El abanderado de la Orden: la armadura de acero con su filo y su cinturón de latón, las hombreras, la capa carmesí, el
     * yelmo cerrado con el penacho y la rendija del visor encendida en el color de acento, y el asta de latón con su punta de
     * lanza y el estandarte carmesí de cola partida con el engrane de la Orden.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 13f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val borde = if (oscuro) LATON_LUZ.copy(alpha = 0.5f) else ACERO_OSC
        val acero = placa(ACERO, oscuro); val luz = placa(ACERO_LUZ, oscuro); val osc = placa(ACERO_OSC, oscuro)
        val carmesi = placa(CARMESI, oscuro); val laton = placa(LATON, oscuro); val latonLuz = placa(LATON_LUZ, oscuro)
        val asta = x + 5.2f * u
        // La capa carmesí, detrás.
        val capa = Path().apply { moveTo(x - 2.8f * u, pie - 15.4f * u); lineTo(x + 1.4f * u, pie - 15.4f * u); lineTo(x + 0.4f * u, pie - 3.6f * u); lineTo(x - 6 * u, pie - 3 * u); close() }
        drawPath(capa, carmesi)
        drawPath(capa, borde.copy(alpha = 0.6f), style = Stroke(0.45f * u, join = StrokeJoin.Round))
        // El asta con su punta de lanza, el travesaño y el estandarte.
        drawLine(laton, Offset(asta, pie + 0.4f * u), Offset(asta, pie - 24 * u), 1f * u)
        drawPath(Path().apply { moveTo(asta, pie - 27.4f * u); lineTo(asta + 1.3f * u, pie - 24 * u); lineTo(asta - 1.3f * u, pie - 24 * u); close() }, latonLuz)
        val ty = pie - 23.2f * u
        drawLine(laton, Offset(asta - 0.6f * u, ty), Offset(asta + 10.4f * u, ty), 0.9f * u, StrokeCap.Round)
        val est = Path().apply { moveTo(asta + 0.6f * u, ty); lineTo(asta + 9.8f * u, ty); lineTo(asta + 9.8f * u, ty + 11 * u); lineTo(asta + 5.2f * u, ty + 8.4f * u); lineTo(asta + 0.6f * u, ty + 11 * u); close() }
        drawPath(est, carmesi)
        drawLine(placa(CARMESI_OSC, oscuro), Offset(asta + 8.2f * u, ty + 0.6f * u), Offset(asta + 8.2f * u, ty + 9.6f * u), 0.8f * u)
        val eg = Offset(asta + 5.2f * u, ty + 4.4f * u)
        drawPath(engrane(eg, 2.7f * u, 8), latonLuz)
        drawCircle(carmesi, 0.9f * u, eg)
        drawPath(est, laton, style = Stroke(0.6f * u, join = StrokeJoin.Round))
        // Las piernas con sus escarpes.
        listOf(-1.3f, 1.3f).forEach { dx ->
            drawLine(borde, Offset(x + dx * u, pie - 7.8f * u), Offset(x + dx * u, pie - 0.8f * u), 2.8f * u)
            drawLine(acero, Offset(x + dx * u, pie - 7.8f * u), Offset(x + dx * u, pie - 0.8f * u), 2f * u)
            drawRect(osc, Offset(x + dx * u - 1.3f * u, pie - 1.6f * u), Size(2.8f * u, 1.6f * u))
        }
        // La coraza, con el filo de luz, el cinturón y el broche de latón.
        val coraza = Path().apply { moveTo(x - 3.2f * u, pie - 16 * u); lineTo(x + 3.2f * u, pie - 16 * u); lineTo(x + 2.7f * u, pie - 7.4f * u); lineTo(x - 2.7f * u, pie - 7.4f * u); close() }
        drawPath(coraza, acero)
        drawLine(luz, Offset(x - 2.2f * u, pie - 15.2f * u), Offset(x - 1.8f * u, pie - 9 * u), 0.9f * u)
        drawLine(laton, Offset(x - 2.8f * u, pie - 8.4f * u), Offset(x + 2.8f * u, pie - 8.4f * u), 0.9f * u)
        drawCircle(latonLuz, 0.8f * u, Offset(x + 0.3f * u, pie - 12.6f * u))
        drawPath(coraza, borde, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        // Las hombreras y el brazo que sostiene el asta.
        listOf(x - 4.6f * u, x + 1.2f * u).forEach { hx ->
            drawOval(luz, Offset(hx, pie - 17 * u), Size(3.4f * u, 2.4f * u))
            drawOval(borde, Offset(hx, pie - 17 * u), Size(3.4f * u, 2.4f * u), style = Stroke(0.45f * u))
        }
        drawLine(acero, Offset(x + 3 * u, pie - 14.6f * u), Offset(asta, pie - 12.4f * u), 1.6f * u, StrokeCap.Round)
        drawCircle(osc, 1 * u, Offset(asta, pie - 12.4f * u))
        // El yelmo cerrado con el visor encendido y el penacho.
        val yx = x; val yy = pie - 19.4f * u
        drawPath(Path().apply { moveTo(yx - 0.6f * u, yy - 2.8f * u); quadraticTo(yx - 1.4f * u, yy - 6 * u, yx - 5.4f * u, yy - 5.2f * u); quadraticTo(yx - 2.4f * u, yy - 4.4f * u, yx + 0.8f * u, yy - 2.6f * u); close() }, carmesi)
        drawRoundRect(acero, Offset(yx - 2.5f * u, yy - 2.9f * u), Size(5 * u, 5.4f * u), CornerRadius(1.7f * u))
        drawRoundRect(borde, Offset(yx - 2.5f * u, yy - 2.9f * u), Size(5 * u, 5.4f * u), CornerRadius(1.7f * u), style = Stroke(0.5f * u))
        if (oscuro) drawLine(e.acento.copy(alpha = 0.32f), Offset(yx - 2.4f * u, yy - 0.2f * u), Offset(yx + 2.6f * u, yy - 0.2f * u), 2.6f * u, StrokeCap.Round)
        drawLine(e.acento, Offset(yx - 1.6f * u, yy - 0.2f * u), Offset(yx + 2.2f * u, yy - 0.2f * u), 0.8f * u, StrokeCap.Round)
    }

    /**
     * La fortaleza tomada: el bastión de sillares de acero con sus almenas, las juntas, la puerta en arco con el engrane de
     * latón por clave y sus troneras, y en lo alto el gran estandarte carmesí de cola partida con el engrane de la Orden. De día
     * lo rodea un abanico de rayos de latón; de noche las troneras se encienden y dos reflectores cruzan sus haces detrás.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 54f * u, 48f * u)
        val x = b.x; val y = b.y
        val borde = if (oscuro) LATON_LUZ.copy(alpha = 0.45f) else ACERO_OSC
        val muro = placa(ACERO_LUZ, oscuro); val muroOsc = placa(ACERO, oscuro)
        val laton = placa(LATON, oscuro); val latonLuz = placa(LATON_LUZ, oscuro); val carmesi = placa(CARMESI, oscuro)
        val centro = Offset(x + 7 * u, y - 40 * u)
        if (oscuro) {
            // Los dos reflectores (los dos degradados), desde el pie de la torre hacia arriba, cruzados.
            listOf(-1f, 1f).forEach { s ->
                val o = Offset(x + s * 11 * u, y - 8 * u); val f = Offset(x - s * 24 * u, y - 47 * u)
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(f.x - 3.5f * u, f.y); lineTo(f.x + 3.5f * u, f.y); close() },
                    Brush.linearGradient(listOf(Color(0xFFEFF3FF).copy(alpha = 0.3f), Color.Transparent), o, f))
            }
        } else {
            for (k in 0 until 11) {
                val a = Math.PI * (1.05 + k * 0.09)
                drawLine(LATON_LUZ.copy(alpha = 0.55f), Offset(centro.x + (10 * u * kotlin.math.cos(a)).toFloat(), centro.y + (10 * u * kotlin.math.sin(a)).toFloat()),
                    Offset(centro.x + (17 * u * kotlin.math.cos(a)).toFloat(), centro.y + (17 * u * kotlin.math.sin(a)).toFloat()), 1f * u, StrokeCap.Round)
            }
        }
        // El bastión: la torre en talud con su cara en sombra, las almenas y las juntas.
        val torre = Path().apply { moveTo(x - 13 * u, y); lineTo(x + 13 * u, y); lineTo(x + 11 * u, y - 20 * u); lineTo(x - 11 * u, y - 20 * u); close() }
        drawPath(torre, muro)
        drawPath(Path().apply { moveTo(x + 5 * u, y); lineTo(x + 13 * u, y); lineTo(x + 11 * u, y - 20 * u); lineTo(x + 4.5f * u, y - 20 * u); close() }, muroOsc.copy(alpha = 0.55f))
        val paso = 22f * u / 3f - 1.2f * u
        for (k in 0 until 4) {
            val mx = x - 11 * u + k * (22f * u - 3.6f * u) / 3f
            drawRect(muro, Offset(mx, y - 23.6f * u), Size(3.6f * u, 3.8f * u))
            drawRect(borde, Offset(mx, y - 23.6f * u), Size(3.6f * u, 3.8f * u), style = Stroke(0.5f * u))
        }
        for (k in 1 until 4) {
            val jy = y - k * 5 * u
            drawLine(muroOsc.copy(alpha = 0.6f), Offset(x - 12.6f * u + k * 0.5f * u, jy), Offset(x + 12.6f * u - k * 0.5f * u, jy), 0.5f * u)
            var jx = x - 10 * u + (k % 2) * paso / 2; var j = 0
            while (jx < x + 11 * u && j < 6) { drawLine(muroOsc.copy(alpha = 0.5f), Offset(jx, jy), Offset(jx, jy + 5 * u), 0.5f * u); jx += maxOf(paso, 1f); j++ }
        }
        drawPath(torre, borde, style = Stroke(0.6f * u, join = StrokeJoin.Round))
        // La puerta en arco con su clave de engrane, y las troneras.
        val puerta = Rect(x - 3.8f * u, y - 10 * u, x + 3.8f * u, y)
        drawPath(arcoMedioPunto(puerta), ESMALTE)
        drawPath(arcoMedioPunto(puerta), laton, style = Stroke(0.8f * u))
        drawPath(engrane(Offset(x, y - 12.4f * u), 2 * u, 8), latonLuz)
        listOf(-6.8f, 6.8f).forEach { dx ->
            val r = Rect(x + dx * u - 0.7f * u, y - 17.4f * u, x + dx * u + 0.7f * u, y - 13.2f * u)
            if (oscuro) drawCircle(LATON_LUZ.copy(alpha = 0.25f), 3 * u, r.center)
            drawRect(if (oscuro) LATON_LUZ else ESMALTE, r.topLeft, r.size)
        }
        // El asta y el gran estandarte, con el engrane de la Orden.
        drawLine(laton, Offset(x, y - 23.6f * u), Offset(x, y - 47 * u), 1.2f * u)
        drawCircle(latonLuz, 1.2f * u, Offset(x, y - 47 * u))
        val ban = Path().apply { moveTo(x + 0.6f * u, y - 46 * u); lineTo(x + 17 * u, y - 46 * u); lineTo(x + 13.6f * u, y - 40.6f * u); lineTo(x + 17 * u, y - 35.2f * u); lineTo(x + 0.6f * u, y - 34.4f * u); close() }
        drawPath(ban, carmesi)
        drawLine(placa(CARMESI_OSC, oscuro), Offset(x + 0.6f * u, y - 36.4f * u), Offset(x + 15 * u, y - 36.8f * u), 0.7f * u)
        drawPath(engrane(Offset(x + 7 * u, y - 40.4f * u), 3.4f * u, 10), latonLuz)
        drawCircle(carmesi, 1.2f * u, Offset(x + 7 * u, y - 40.4f * u))
        drawPath(ban, laton, style = Stroke(0.7f * u, join = StrokeJoin.Round))
    }
}

/**
 * Laboratorio: los logros del laboratorio. El trofeo descansa en el zócalo del escaparate de muestras; en el hall, sobre el gato
 * de laboratorio, y su placa es la etiqueta de la muestra.
 */
object LogrosLaboratorio : DisenoLogros {
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val GRIS_LUZ = Color(0xFFCDD5DB)
    private val CIAN = Color(0xFF1FB5C9)
    private val CIAN_OSC = Color(0xFF0B6E7C)
    private val ROJO = Color(0xFFD8312A)
    private val TINTA = Color(0xFF12202B)
    private val BLANCO = Color(0xFFF2F5F7)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0E12), 0.38f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El zócalo del escaparate de muestras: la cubierta blanca de resina con su canto cian, el gabinete de acero con dos cajones y
     * sus jaladeras en el metal del nivel, la etiqueta de inventario con su código de barras y su punto rojo de calibración, el
     * rodapié y las patas niveladoras.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cubierta = fr(0.11f, 0.765f, 0.89f, 0.8f)
        val gabinete = fr(0.16f, 0.8f, 0.84f, 0.935f)
        val rodapie = fr(0.18f, 0.935f, 0.82f, 0.946f)
        val patas = listOf(fr(0.2f, 0.946f, 0.26f, 0.955f), fr(0.74f, 0.946f, 0.8f, 0.955f))
        if (!e.hecho) {
            drawPath(Path().apply { addRoundRect(RoundRect(cubierta, CornerRadius(w * 0.012f))); addRect(gabinete); addRect(rodapie); patas.forEach { addRect(it) } },
                siluetaFuturo(oscuro)); return
        }
        sombraPie(0.1f, 0.9f, oscuro)
        patas.forEach { drawRect(t(GRIS_OSC, oscuro), it.topLeft, it.size) }
        drawRect(t(TINTA, oscuro).copy(alpha = 0.85f), rodapie.topLeft, rodapie.size)
        // El gabinete y sus dos cajones.
        drawRect(t(GRIS, oscuro), gabinete.topLeft, gabinete.size)
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(gabinete.right - w * 0.045f, gabinete.top), Size(w * 0.045f, gabinete.height))
        listOf(0.19f, 0.51f).forEach { x ->
            val cj = fr(x, 0.815f, x + 0.3f, 0.922f)
            drawRect(t(GRIS_LUZ, oscuro), cj.topLeft, cj.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), cj.topLeft, Size(cj.width, lw))
            drawRect(t(GRIS_OSC, oscuro), Offset(cj.left, cj.bottom - lw), Size(cj.width, lw))
            // La jaladera del nivel.
            chapaNivel(fr(x + 0.09f, 0.83f, x + 0.21f, 0.848f), e, h * 0.006f)
        }
        // La etiqueta de inventario, en el cajón de la izquierda.
        if (fino) {
            val et = fr(0.23f, 0.866f, 0.45f, 0.906f)
            drawRect(t(BLANCO, oscuro), et.topLeft, et.size)
            drawRect(t(GRIS_OSC, oscuro), et.topLeft, et.size, style = Stroke(lw * 0.6f))
            val barras = if (muyFino) 11 else 7
            for (k in 0 until barras) {
                val x = et.left + et.width * (0.08f + 0.6f * k / barras)
                drawLine(t(TINTA, oscuro), Offset(x, et.top + et.height * 0.2f), Offset(x, et.bottom - et.height * 0.2f), lw * (if (azar(e.semilla, k) > 0.5f) 1.4f else 0.7f))
            }
            drawCircle(t(ROJO, oscuro), et.height * 0.22f, Offset(et.right - et.width * 0.15f, et.center.y))
        }
        // La cubierta de resina con su canto cian.
        drawRoundRect(t(BLANCO, oscuro), cubierta.topLeft, cubierta.size, CornerRadius(w * 0.012f))
        drawRect(t(GRIS_LUZ, oscuro), Offset(cubierta.left + w * 0.01f, cubierta.top + cubierta.height * 0.45f), Size(cubierta.width - w * 0.02f, cubierta.height * 0.3f))
        drawRect(t(CIAN, oscuro), Offset(cubierta.left, cubierta.bottom - cubierta.height * 0.25f), Size(cubierta.width, cubierta.height * 0.25f))
        if (muyFino) drawLine(Color.White.copy(alpha = 0.7f), pt(0.14f, 0.769f), pt(0.5f, 0.769f), lw)
    }

    /**
     * El gato de laboratorio: la platina de acero con su canto cian, sostenida por la tijera de dos brazos con sus ejes, el
     * tornillo que la sube con la perilla en el metal del nivel, y la base con su escala grabada.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val platina = fr(0.08f, 0f, 0.92f, 0.2f)
        val base = fr(0.04f, 0.8f, 0.96f, 1f)
        if (!e.hecho) {
            drawPath(Path().apply { addRect(platina); addRect(base); addRect(fr(0.24f, 0.2f, 0.76f, 0.8f)) }, siluetaFuturo(oscuro)); return
        }
        val acero = t(GRIS_OSC, oscuro)
        // La tijera: dos brazos cruzados por cada lado, con sus ejes.
        val brazo = maxOf(h * 0.07f, 0.8f * u)
        listOf(0.26f to 0.74f, 0.74f to 0.26f).forEach { (a, b) -> drawLine(acero, pt(a, 0.2f), pt(b, 0.8f), brazo, StrokeCap.Round) }
        listOf(0.26f to 0.74f, 0.74f to 0.26f).forEach { (a, b) -> drawLine(t(GRIS, oscuro), pt(a, 0.2f), pt(b, 0.8f), brazo * 0.4f, StrokeCap.Round) }
        listOf(pt(0.26f, 0.2f), pt(0.74f, 0.2f), pt(0.26f, 0.8f), pt(0.74f, 0.8f)).forEach { drawCircle(acero, brazo * 0.8f, it) }
        // El tornillo y su perilla del nivel.
        drawLine(t(GRIS_LUZ, oscuro), pt(0.2f, 0.5f), pt(0.86f, 0.5f), lw)
        drawCircle(acero, brazo, pt(0.5f, 0.5f))
        val pr = fr(0.86f, 0.28f, 0.95f, 0.72f)
        chapaNivel(pr, e, pr.width * 0.3f)
        for (k in 1..2) drawLine(e.metalOscuro, Offset(pr.left + pr.width * k / 3f, pr.top + pr.height * 0.2f), Offset(pr.left + pr.width * k / 3f, pr.bottom - pr.height * 0.2f), lw * 0.6f)
        // La base con su escala.
        drawRect(t(GRIS, oscuro), base.topLeft, base.size)
        drawRect(t(GRIS_LUZ, oscuro), base.topLeft, Size(base.width, lw))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(base.left, base.bottom - lw), Size(base.width, lw))
        for (k in 0..10) drawLine(t(TINTA, oscuro).copy(alpha = 0.6f), Offset(w * (0.1f + k * 0.08f), h * 0.84f), Offset(w * (0.1f + k * 0.08f), h * (if (k % 5 == 0) 0.95f else 0.9f)), lw * 0.5f)
        // La platina con su canto cian.
        drawRect(t(GRIS_LUZ, oscuro), platina.topLeft, platina.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.5f), platina.topLeft, Size(platina.width, lw))
        drawRect(t(CIAN, oscuro), Offset(platina.left, platina.bottom - h * 0.06f), Size(platina.width, h * 0.06f))
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6EDF2) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFF6FD8E6) else CIAN_OSC

    /**
     * La etiqueta de la muestra: el papel blanco liso en su marco del nivel, con la franja cian del encabezado al canto de arriba
     * y el punto rojo de la esquina.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(3 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(3 * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(3 * u), style = Stroke(0.8f * u))
        val b = 1.8f * u
        drawRoundRect(if (oscuro) Color(0xFF18222B) else Color.White, Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(2 * u))
        drawRect(t(CIAN, oscuro), Offset(b + 1 * u, b), Size(w - 2 * b - 2 * u, 1.6f * u))
        drawCircle(t(ROJO, oscuro), 1.3f * u, Offset(w - b - 3 * u, h - b - 3 * u))
    }

    // ---------------------------------------------------------------- el salón del hall

    /** Los líquidos de los matraces (cian, violeta, verde, ámbar) y el amarillo de los avisos. */
    private val LIQUIDOS = listOf(Color(0xFF0B8FA1), Color(0xFF5E4FD1), Color(0xFF1E8E5A), Color(0xFFD08A1E))
    private val AVISO = Color(0xFFF2C230)

    /**
     * El laboratorio limpio. El muro es de azulejo blanco clínico con sus juntas; cada columna de trofeos se expone tras la
     * mampara de vidrio de una sala blanca, con su marco de aluminio, el travesaño, los reflejos en diagonal y el rótulo cian
     * de la sala al pie. Entre las mamparas se levantan las vitrinas de vidrio con sus repisas de matraces, tubos de ensayo y
     * frascos (un líquido distinto en cada repisa) y, por fuera, las tuberías de gases con sus bandas de color y sus llaves.
     * En cada tramo de ≈ 230 dp, sobre la vitrina, el triángulo amarillo de aviso. Arriba, el plafón de paneles de luz y el
     * ducto de la extracción; abajo, el zócalo sanitario redondeado y el piso de resina con su línea cian. De día, la luz
     * blanca y pareja de los paneles; en oscuro, los paneles se apagan y queda el brillo de los líquidos y de las pantallas
     * de los equipos, cian sobre el azul de la noche.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaLaboratorio(g, e, oscuro) }
    }

    private fun DrawScope.escenaLaboratorio(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val linea = m(GRIS_OSC, if (oscuro) 0.45f else 0.4f)
        val cian = if (oscuro) Color(0xFF3CD2E5) else CIAN_OSC

        // El azulejo: la cuadrícula de juntas, muy tenue.
        val azulejo = Path(); val lado = 22f * u
        repetirY(g.friso, g.suelo, lado, 80) { _, y -> azulejo.moveTo(0f, y); azulejo.lineTo(w, y) }
        var xa = 0f; var ka = 0
        while (xa < w && ka < 80) { azulejo.moveTo(xa, g.friso); azulejo.lineTo(xa, g.suelo); xa += lado; ka++ }
        drawPath(azulejo, m(GRIS_OSC, if (oscuro) 0.2f else 0.13f), style = Stroke(0.8f * u))
        // De noche, el resplandor cian de los equipos que sube desde el pie de las vitrinas (un degradado).
        if (oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF3CD2E5).copy(alpha = 0.06f)), h * 0.5f, h), Offset(0f, h * 0.5f), Size(w, h * 0.5f))

        // Las mamparas de la sala blanca: el vidrio, el marco de aluminio, el travesaño, los reflejos y el rótulo.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val r = Rect(v.left + 3f * u, g.friso + 12f * u, v.right - 3f * u, g.suelo - 22f * u)
            if (r.height < 40f * u) continue
            drawRect(m(CIAN, if (oscuro) 0.06f else 0.05f), r.topLeft, r.size)
            drawRect(m(GRIS, if (oscuro) 0.4f else 0.55f), r.topLeft, r.size, style = Stroke(3f * u))
            drawRect(m(GRIS_LUZ, if (oscuro) 0.25f else 0.8f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3f * u, 1f * u))
            // El travesaño cada tramo, a la altura de la cintura de cada fila.
            repetirY(r.top + g.modulo * 0.85f, r.bottom - 20f * u, g.modulo, 10) { _, y ->
                drawLine(m(GRIS, if (oscuro) 0.3f else 0.4f), Offset(r.left, y), Offset(r.right, y), 2f * u)
            }
            // Los reflejos en diagonal sobre el vidrio, en pares.
            val reflejo = Path()
            repetirY(r.top + 30f * u, r.bottom, 260f * u, 8) { k, y ->
                val x0 = r.left + r.width * (0.15f + 0.5f * azar(e.semilla + i, k))
                reflejo.moveTo(x0, y + 40f * u); reflejo.lineTo(x0 + 30f * u, y)
                reflejo.moveTo(x0 + 9f * u, y + 40f * u); reflejo.lineTo(x0 + 39f * u, y)
            }
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawPath(reflejo, Color.White.copy(alpha = if (oscuro) 0.04f else 0.5f), style = Stroke(3f * u, cap = StrokeCap.Round))
            }
            // El rótulo de la sala, al pie: la cinta cian y su número en barras.
            drawRect(cian.copy(alpha = if (oscuro) 0.35f else 0.4f), Offset(r.left, r.bottom + 6f * u), Size(r.width, 2f * u))
            for (k in 0 until 3) drawRect(m(TINTA, 0.25f), Offset(r.left + 4f * u + k * 4f * u, r.bottom + 10f * u), Size(2.4f * u, 6f * u))
        }

        // Las vitrinas entre las mamparas: el vidrio, las repisas y lo que hay en ellas; por fuera, las tuberías de gases.
        val bandas = listOf(Color(0xFF2F7DD8), AVISO, Color(0xFF3A9A4A))
        for (i in 0..g.n) {
            val x = g.borde(i); val a = g.pil
            val y0 = g.friso + 10f * u; val y1 = g.suelo - 6f * u
            // Las tuberías de gases por los cantos de la vitrina, con su banda de color cada tramo.
            listOf(-1f, 1f).forEachIndexed { j, s ->
                val xt = x + s * (a + 3f * u)
                if (xt < -4f * u || xt > w + 4f * u) return@forEachIndexed
                drawLine(m(GRIS, if (oscuro) 0.45f else 0.6f), Offset(xt, g.friso), Offset(xt, g.suelo), 2.6f * u)
                repetirY(g.friso + 50f * u + j * 30f * u, g.suelo, g.modulo * 0.5f, 20) { k, y ->
                    drawRect(bandas[(i + j + k) % 3].copy(alpha = if (oscuro) 0.4f else 0.6f), Offset(xt - 1.8f * u, y), Size(3.6f * u, 6f * u))
                }
            }
            drawRect(m(GRIS_LUZ, if (oscuro) 0.12f else 0.4f), Offset(x - a, y0), Size(2 * a, y1 - y0))
            drawRect(linea, Offset(x - a, y0), Size(2 * a, y1 - y0), style = Stroke(1.2f * u))
            val repisa = g.modulo / 5f
            repetirY(y0 + repisa, y1 - 4f * u, repisa, 40) { k, yy ->
                drawLine(linea, Offset(x - a, yy), Offset(x + a, yy), 1.4f * u)
                val liq = LIQUIDOS[(i + k) % 4]
                val c = liq.copy(alpha = if (oscuro) 0.75f else 0.5f)
                val vid = m(GRIS_OSC, if (oscuro) 0.5f else 0.45f)
                when ((i + k) % 3) {
                    // El matraz de fondo redondo con su cuello.
                    0 -> {
                        drawCircle(c, 6.5f * u, Offset(x, yy - 7f * u))
                        drawRect(vid, Offset(x - 1.6f * u, yy - 21f * u), Size(3.2f * u, 8f * u), style = Stroke(0.8f * u))
                        drawCircle(vid, 6.5f * u, Offset(x, yy - 7f * u), style = Stroke(0.8f * u))
                        if (oscuro) drawCircle(liq.copy(alpha = 0.12f), 14f * u, Offset(x, yy - 7f * u))
                    }
                    // El Erlenmeyer.
                    1 -> {
                        val p = Path().apply { moveTo(x - 2f * u, yy - 20f * u); lineTo(x + 2f * u, yy - 20f * u); lineTo(x + 2f * u, yy - 12f * u); lineTo(x + 7f * u, yy)
                            lineTo(x - 7f * u, yy); lineTo(x - 2f * u, yy - 12f * u); close() }
                        clipRect(x - 8f * u, yy - 9f * u, x + 8f * u, yy) { drawPath(p, c) }
                        drawPath(p, vid, style = Stroke(0.8f * u))
                        if (oscuro) drawCircle(liq.copy(alpha = 0.12f), 12f * u, Offset(x, yy - 4f * u))
                    }
                    // La gradilla con tres tubos de ensayo.
                    else -> {
                        listOf(-5f, 0f, 5f).forEachIndexed { j, dx ->
                            val tubo = Rect(x + dx * u - 1.4f * u, yy - 17f * u, x + dx * u + 1.4f * u, yy - 2f * u)
                            drawRect(LIQUIDOS[(i + k + j) % 4].copy(alpha = if (oscuro) 0.7f else 0.45f), Offset(tubo.left, tubo.top + tubo.height * 0.45f), Size(tubo.width, tubo.height * 0.55f))
                            drawRect(vid, tubo.topLeft, tubo.size, style = Stroke(0.6f * u))
                        }
                        drawRect(m(GRIS_OSC, 0.6f), Offset(x - 8f * u, yy - 9f * u), Size(16f * u, 1.6f * u))
                        drawRect(m(GRIS_OSC, 0.6f), Offset(x - 8f * u, yy - 3f * u), Size(16f * u, 3f * u))
                    }
                }
            }
            // El reflejo del vidrio de la vitrina.
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.7f), Offset(x - a + 3f * u, y0 + 4f * u), Offset(x - a + 3f * u, y1 - 4f * u), 1.2f * u)
            // El aviso de cada tramo, arriba de su repisa (de noche, uno de cada dos es la pantallita del equipo con su curva).
            repetirY(y0, y1 - 40f * u, g.modulo, 10) { k, yy ->
                if (oscuro && k % 2 == 1) {
                    val pc = Rect(x - a * 0.8f, yy + 5f * u, x + a * 0.8f, yy + 17f * u)
                    drawRect(Color(0xFF3CD2E5).copy(alpha = 0.12f), Offset(pc.left - 3f * u, pc.top - 3f * u), Size(pc.width + 6f * u, pc.height + 6f * u))
                    drawRect(Color(0xFF041016), pc.topLeft, pc.size)
                    drawPath(Path().apply { moveTo(pc.left + 1f * u, pc.center.y); lineTo(pc.left + pc.width * 0.3f, pc.center.y); lineTo(pc.left + pc.width * 0.4f, pc.top + 2f * u)
                        lineTo(pc.left + pc.width * 0.5f, pc.bottom - 2f * u); lineTo(pc.left + pc.width * 0.6f, pc.center.y); lineTo(pc.right - 1f * u, pc.center.y) },
                        Color(0xFF3CD2E5), style = Stroke(0.7f * u))
                    return@repetirY
                }
                val tc = Offset(x, yy + 12f * u); val tr = minOf(a * 0.75f, 7.5f * u)
                val tri = Path().apply { moveTo(tc.x, tc.y - tr); lineTo(tc.x + tr, tc.y + tr * 0.8f); lineTo(tc.x - tr, tc.y + tr * 0.8f); close() }
                drawPath(tri, AVISO.copy(alpha = if (oscuro) 0.6f else 0.85f))
                drawPath(tri, m(TINTA, 0.8f), style = Stroke(0.8f * u, join = StrokeJoin.Round))
                drawLine(m(TINTA, 0.9f), Offset(tc.x, tc.y - tr * 0.35f), Offset(tc.x, tc.y + tr * 0.3f), 1f * u)
                drawCircle(m(TINTA, 0.9f), 0.6f * u, Offset(tc.x, tc.y + tr * 0.55f))
            }
        }

        // El plafón: los paneles de luz sobre cada mampara y el ducto de la extracción, con su rejilla.
        drawRect(m(GRIS, if (oscuro) 0.35f else 0.3f), Offset.Zero, Size(w, g.friso))
        drawRect(m(GRIS_OSC, if (oscuro) 0.4f else 0.3f), Offset(0f, g.friso - 1.5f * u), Size(w, 1.5f * u))
        for (i in 0 until g.n) {
            val c = g.cx(i); val pw = minOf(g.paso * 0.5f, 64f * u)
            val panel = Rect(c - pw / 2, g.friso * 0.2f, c + pw / 2, g.friso * 0.8f)
            drawRect(if (oscuro) m(GRIS_OSC, 0.3f) else Color.White, panel.topLeft, panel.size)
            drawRect(linea, panel.topLeft, panel.size, style = Stroke(0.8f * u))
            drawLine(linea.copy(alpha = 0.5f), Offset(panel.center.x, panel.top), Offset(panel.center.x, panel.bottom), 0.6f * u)
        }
        for (i in 0..g.n) {
            val x = g.borde(i)
            drawRect(m(GRIS_OSC, if (oscuro) 0.5f else 0.45f), Offset(x - 9f * u, g.friso * 0.15f), Size(18f * u, g.friso * 0.7f))
            for (j in 0 until 4) drawLine(m(TINTA, 0.5f), Offset(x - 7f * u + j * 4.6f * u, g.friso * 0.25f), Offset(x - 7f * u + j * 4.6f * u, g.friso * 0.75f), 0.8f * u)
        }

        // El zócalo sanitario y el piso de resina con su línea cian.
        drawRect(m(GRIS, if (oscuro) 0.3f else 0.35f), Offset(0f, g.suelo - 6f * u), Size(w, 6f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.03f else 0.4f), Offset(0f, g.suelo - 6f * u), Size(w, 1.2f * u))
        drawRect(m(GRIS_LUZ, if (oscuro) 0.25f else 0.55f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        drawLine(cian.copy(alpha = if (oscuro) 0.6f else 0.55f), Offset(0f, g.suelo + (h - g.suelo) * 0.45f), Offset(w, g.suelo + (h - g.suelo) * 0.45f), 2f * u)
        for (i in 0..g.n) drawRect(m(GRIS_OSC, 0.3f), Offset(g.borde(i) - g.pil, g.suelo + 1f * u), Size(2 * g.pil, 2f * u))
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La investigadora de bata: la bata blanca con sus solapas, el bolsillo con la pluma roja y el contorno gris para que se
     * lea sobre el mapa claro, los lentes de seguridad cian, la libreta en una mano y en la otra, en alto, el matraz con el
     * líquido en el color de acento y sus burbujas (de noche, el líquido brilla).
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 12f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val borde = if (oscuro) GRIS_LUZ.copy(alpha = 0.6f) else GRIS_OSC
        val bata = if (oscuro) mezcla(BLANCO, Color(0xFF0B0E12), 0.22f) else BLANCO
        val piel = t(Color(0xFFE6BC98), oscuro)
        val pantalon = t(mezcla(TINTA, GRIS_OSC, 0.45f), oscuro)
        // Las piernas y los zapatos.
        listOf(-1.2f, 1.2f).forEach { dx ->
            drawLine(pantalon, Offset(x + dx * u, pie - 6 * u), Offset(x + dx * u, pie - 1 * u), 1.8f * u)
            drawRoundRect(t(TINTA, oscuro), Offset(x + dx * u - 1.2f * u, pie - 1.4f * u), Size(2.6f * u, 1.4f * u), CornerRadius(0.6f * u))
            if (oscuro) drawRoundRect(borde, Offset(x + dx * u - 1.2f * u, pie - 1.4f * u), Size(2.6f * u, 1.4f * u), CornerRadius(0.6f * u), style = Stroke(0.4f * u))
        }
        // Los brazos, con su contorno: el izquierdo con la libreta, el derecho alzando el matraz.
        val manoI = Offset(x - 4 * u, pie - 9.4f * u); val manoD = Offset(x + 5.6f * u, pie - 16.6f * u)
        listOf(Offset(x - 2.6f * u, pie - 14.4f * u) to manoI, Offset(x + 2.6f * u, pie - 14.4f * u) to manoD).forEach { (a, m) ->
            drawLine(borde, a, m, 2.5f * u, StrokeCap.Round); drawLine(bata, a, m, 1.6f * u, StrokeCap.Round)
        }
        drawRect(t(CIAN_OSC, oscuro), Offset(manoI.x - 1.9f * u, manoI.y - 1.4f * u), Size(2.4f * u, 3.2f * u))
        drawLine(t(GRIS_LUZ, oscuro), Offset(manoI.x - 1.5f * u, manoI.y - 0.4f * u), Offset(manoI.x + 0.1f * u, manoI.y - 0.4f * u), 0.4f * u)
        drawCircle(piel, 0.8f * u, manoI)
        // La bata: abierta abajo, las solapas sobre la camisa cian, el bolsillo con la pluma roja.
        val bataP = Path().apply {
            moveTo(x - 2.9f * u, pie - 15 * u); lineTo(x + 2.9f * u, pie - 15 * u); lineTo(x + 4 * u, pie - 4.6f * u); lineTo(x + 0.5f * u, pie - 4.6f * u)
            lineTo(x, pie - 8 * u); lineTo(x - 0.5f * u, pie - 4.6f * u); lineTo(x - 4 * u, pie - 4.6f * u); close()
        }
        drawPath(bataP, bata)
        drawPath(Path().apply { moveTo(x - 1.3f * u, pie - 15 * u); lineTo(x + 1.3f * u, pie - 15 * u); lineTo(x, pie - 12 * u); close() }, t(CIAN, oscuro))
        drawPath(Path().apply { moveTo(x - 1.5f * u, pie - 15 * u); lineTo(x, pie - 11.4f * u); lineTo(x + 1.5f * u, pie - 15 * u) }, borde, style = Stroke(0.45f * u))
        drawLine(borde, Offset(x - 2.8f * u, pie - 11.4f * u), Offset(x - 1.2f * u, pie - 11.4f * u), 0.45f * u)
        drawLine(t(ROJO, oscuro), Offset(x - 2.2f * u, pie - 11.4f * u), Offset(x - 2.2f * u, pie - 12.8f * u), 0.55f * u)
        drawPath(bataP, borde, style = Stroke(0.55f * u, join = StrokeJoin.Round))
        // La cabeza: el pelo recogido, la cara y los lentes de seguridad.
        val cab = Offset(x, pie - 17.4f * u)
        drawCircle(t(Color(0xFF3A2A20), oscuro), 1.3f * u, Offset(cab.x - 2.2f * u, cab.y - 1.4f * u))
        drawCircle(piel, 2.2f * u, cab)
        drawArc(t(Color(0xFF3A2A20), oscuro), 180f, 180f, true, Offset(cab.x - 2.4f * u, cab.y - 2.7f * u), Size(4.8f * u, 3.6f * u))
        drawRoundRect(t(CIAN, oscuro).copy(alpha = 0.9f), Offset(cab.x - 1.4f * u, cab.y - 0.5f * u), Size(3.6f * u, 1.3f * u), CornerRadius(0.6f * u))
        drawLine(Color.White.copy(alpha = 0.7f), Offset(cab.x - 0.6f * u, cab.y - 0.2f * u), Offset(cab.x + 0.4f * u, cab.y - 0.2f * u), 0.35f * u)
        if (oscuro) drawCircle(borde, 2.2f * u, cab, style = Stroke(0.4f * u))
        // El matraz en alto: el brillo de noche, el vidrio, el líquido de acento, el contorno y las burbujas.
        val cuello = Offset(manoD.x + 0.4f * u, manoD.y)
        val matraz = Path().apply {
            moveTo(cuello.x - 0.8f * u, cuello.y - 3 * u); lineTo(cuello.x + 0.8f * u, cuello.y - 3 * u); lineTo(cuello.x + 0.8f * u, cuello.y)
            lineTo(cuello.x + 3.3f * u, cuello.y + 4.6f * u); lineTo(cuello.x - 3.3f * u, cuello.y + 4.6f * u); lineTo(cuello.x - 0.8f * u, cuello.y); close()
        }
        if (oscuro) { drawCircle(e.acento.copy(alpha = 0.13f), 7 * u, Offset(cuello.x, cuello.y + 2.6f * u)); drawCircle(e.acento.copy(alpha = 0.22f), 4.2f * u, Offset(cuello.x, cuello.y + 3 * u)) }
        drawPath(matraz, Color.White.copy(alpha = if (oscuro) 0.2f else 0.5f))
        clipPath(matraz) { drawRect(e.acento, Offset(cuello.x - 4 * u, cuello.y + 1.8f * u), Size(8 * u, 3 * u)) }
        drawPath(matraz, borde, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        drawCircle(piel, 0.8f * u, Offset(manoD.x - 0.5f * u, manoD.y - 0.2f * u))
        listOf(Triple(0.4f, -4.6f, 0.7f), Triple(-0.6f, -6.4f, 0.5f), Triple(0.9f, -7.9f, 0.4f)).forEach { (dx, dy, r) ->
            drawCircle(e.acento, r * u, Offset(cuello.x + dx * u, cuello.y + dy * u), style = Stroke(0.4f * u))
        }
    }

    /**
     * El experimento salió: la placa de Petri de la meta se llena de colonias de cuatro colores (de noche, fluorescentes, con
     * su halo) y a un lado, sobre su trípode y la flama azul del mechero, el matraz hierve y se desborda en espuma, con su vapor
     * y tres destellos amarillos.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val m = e.meta
        val borde = if (oscuro) GRIS_LUZ.copy(alpha = 0.55f) else GRIS_OSC
        // Las colonias sobre la placa de la meta.
        val R = 13f * u
        drawCircle(borde.copy(alpha = 0.45f), R + 3.4f * u, m, style = Stroke(0.8f * u))
        // En espiral de girasol (ángulo áureo), con un poco de desorden: llenan la placa parejo, sin amontonarse.
        val n = 26
        for (k in 0 until n) {
            val a = k * 2.39996f + 0.6f * azar(13, k); val rr = R * kotlin.math.sqrt((k + 0.5f) / n) * (0.85f + 0.15f * azar(29, k))
            val c = Offset(m.x + rr * kotlin.math.cos(a), m.y + rr * kotlin.math.sin(a))
            val col = LIQUIDOS[k % LIQUIDOS.size]
            val r = (1.1f + 1.7f * azar(41, k)) * u
            if (oscuro) drawCircle(mezcla(col, Color.White, 0.3f).copy(alpha = 0.22f), r * 2f, c)
            drawCircle(if (oscuro) mezcla(col, Color.White, 0.25f) else col.copy(alpha = 0.85f), r, c)
            drawCircle(Color.White.copy(alpha = 0.55f), r * 0.32f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
        }
        // El matraz que hierve, arriba a la izquierda de la placa.
        val b = pieVictoriaFuturo(Offset(m.x - 24 * u, m.y - 4 * u), 30f * u, 44f * u, 0f)
        val x = b.x; val y = b.y
        val liquido = LIQUIDOS[1]
        val cuello = Offset(x, y - 20 * u)
        val matraz = Path().apply {
            moveTo(cuello.x - 2.4f * u, cuello.y - 8 * u); lineTo(cuello.x + 2.4f * u, cuello.y - 8 * u); lineTo(cuello.x + 2.4f * u, cuello.y)
            lineTo(cuello.x + 9.5f * u, cuello.y + 13 * u); lineTo(cuello.x - 9.5f * u, cuello.y + 13 * u); lineTo(cuello.x - 2.4f * u, cuello.y); close()
        }
        if (oscuro) drawCircle(Brush.radialGradient(listOf(mezcla(liquido, Color.White, 0.3f).copy(alpha = 0.45f), Color.Transparent), Offset(x, y - 12 * u), 20 * u), 20 * u, Offset(x, y - 12 * u))
        // El trípode y la flama del mechero.
        listOf(-1f, 1f).forEach { sg -> drawLine(t(GRIS_OSC, oscuro), Offset(x + sg * 7 * u, y - 7 * u), Offset(x + sg * 9 * u, y), 0.9f * u) }
        drawLine(t(GRIS_OSC, oscuro), Offset(x - 9 * u, y - 7 * u), Offset(x + 9 * u, y - 7 * u), 1 * u)
        drawPath(Path().apply { moveTo(x, y - 6.8f * u); quadraticTo(x + 2.2f * u, y - 3 * u, x, y - 1.8f * u); quadraticTo(x - 2.2f * u, y - 3 * u, x, y - 6.8f * u); close() }, t(CIAN, oscuro))
        drawPath(Path().apply { moveTo(x, y - 5.4f * u); quadraticTo(x + 1 * u, y - 3 * u, x, y - 2.4f * u); quadraticTo(x - 1 * u, y - 3 * u, x, y - 5.4f * u); close() }, Color.White.copy(alpha = 0.8f))
        drawRect(t(GRIS, oscuro), Offset(x - 1.6f * u, y - 1.8f * u), Size(3.2f * u, 1.8f * u))
        // El vidrio, el líquido con sus burbujas y el contorno.
        drawPath(matraz, Color.White.copy(alpha = if (oscuro) 0.16f else 0.55f))
        clipPath(matraz) {
            drawRect(if (oscuro) mezcla(liquido, Color.White, 0.2f) else liquido, Offset(x - 10 * u, cuello.y + 4 * u), Size(20 * u, 10 * u))
            for (k in 0 until 8) drawCircle(Color.White.copy(alpha = 0.55f), (0.5f + 0.7f * azar(3, k)) * u, Offset(x - 6 * u + azar(7, k) * 12 * u, cuello.y + 6 * u + azar(9, k) * 6 * u))
        }
        drawLine(Color.White.copy(alpha = 0.7f), Offset(cuello.x - 1.4f * u, cuello.y - 6.5f * u), Offset(cuello.x - 1.4f * u, cuello.y - 0.5f * u), 0.6f * u)
        drawPath(matraz, borde, style = Stroke(0.7f * u, join = StrokeJoin.Round))
        // La espuma que se desborda por la boca y escurre por un lado.
        val espuma = mezcla(liquido, Color.White, 0.65f)
        listOf(Offset(-2f, -8.4f) to 2.2f, Offset(1.2f, -9.4f) to 2.4f, Offset(3.4f, -7.8f) to 1.8f, Offset(-0.2f, -11.2f) to 1.8f, Offset(3.6f, -5.6f) to 1.2f, Offset(4.4f, -3.4f) to 1f).forEach { (o, r) ->
            drawCircle(espuma, r * u, Offset(cuello.x + o.x * u, cuello.y + o.y * u))
        }
        // El vapor y los destellos.
        listOf(-2.5f, 2f).forEach { dx ->
            drawPath(Path().apply { moveTo(cuello.x + dx * u, cuello.y - 13 * u); quadraticTo(cuello.x + (dx - 2.4f) * u, cuello.y - 16 * u, cuello.x + dx * u, cuello.y - 18.5f * u); quadraticTo(cuello.x + (dx + 2.4f) * u, cuello.y - 21 * u, cuello.x + dx * u, cuello.y - 23.5f * u) },
                (if (oscuro) GRIS_LUZ else GRIS_OSC).copy(alpha = 0.5f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        listOf(Offset(-11f, -30f) to 2.6f, Offset(10f, -36f) to 2.2f, Offset(12f, -24f) to 1.6f).forEach { (o, r) ->
            val c = Offset(x + o.x * u, y + o.y * u)
            if (oscuro) drawCircle(AVISO.copy(alpha = 0.14f), r * 1.3f * u, c)
            drawPath(estrella(c, r * u, 4, 0.32f, 0f), AVISO)
        }
    }
}

/**
 * Colonos: los logros del almacén. El trofeo descansa en un tocón del claro; en el hall, sobre el banco de pino, y su placa es
 * la pizarra del almacén.
 */
object LogrosColonos : DisenoLogros {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val TIZA = Color(0xFFF2EEE2)
    private val CORTEZA = Color(0xFF5A3A22)
    private val CORTEZA_OSC = Color(0xFF3A2414)
    private val ANILLO = Color(0xFFCDA878)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El tocón del claro: el tronco cortado a hacha, con la corteza agrietada en vetas, la sombra de un lado, las raíces que se
     * abren al pie y las astillas alrededor; arriba, el corte con sus anillos, la médula y una raja. Clavada al frente, la
     * chapa de hojalata del nivel, un poco torcida, con sus clavos.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tronco = Path().apply {
            moveTo(w * 0.2f, h * 0.79f); lineTo(w * 0.8f, h * 0.79f)
            cubicTo(w * 0.8f, h * 0.86f, w * 0.82f, h * 0.91f, w * 0.87f, h * 0.935f)
            quadraticTo(w * 0.91f, h * 0.95f, w * 0.92f, h * 0.956f); lineTo(w * 0.08f, h * 0.956f)
            quadraticTo(w * 0.09f, h * 0.95f, w * 0.13f, h * 0.935f)
            cubicTo(w * 0.18f, h * 0.91f, w * 0.2f, h * 0.86f, w * 0.2f, h * 0.79f); close()
        }
        val corte = fr(0.2f, 0.765f, 0.8f, 0.815f)
        if (!e.hecho) { drawPath(Path().apply { addPath(tronco); addOval(corte) }, siluetaFuturo(oscuro)); return }
        sombraPie(0.06f, 0.94f, oscuro)
        // El tronco con su corteza.
        drawPath(tronco, t(CORTEZA, oscuro))
        clipPath(tronco) {
            if (fino) vetas(fr(0.08f, 0.79f, 0.92f, 0.96f), t(CORTEZA_OSC, oscuro), e.semilla, densidad = if (muyFino) 1.4f else 0.9f, vertical = true, grosor = 1.1f, ondulacion = 0.6f)
            else for (k in 0 until 5) drawLine(t(CORTEZA_OSC, oscuro), pt(0.26f + k * 0.12f, 0.8f), pt(0.25f + k * 0.125f, 0.95f), lw)
            drawRect(Color.Black.copy(alpha = 0.24f), pt(0.66f, 0.78f), Size(w * 0.3f, h * 0.2f))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), pt(0.08f, 0.78f), Size(w * 0.18f, h * 0.2f))
        }
        // Las raíces que se abren al pie.
        listOf(0.3f to -1f, 0.52f to 1f, 0.7f to 1f).forEach { (x, s) ->
            drawPath(poli(x - 0.03f, 0.956f, x + s * 0.05f, 0.935f, x + 0.03f, 0.956f), t(CORTEZA_OSC, oscuro))
        }
        // El corte, con sus anillos, la médula y la raja.
        drawOval(t(CORTEZA_OSC, oscuro), corte.topLeft, corte.size)
        val cara = Rect(corte.left + w * 0.015f, corte.top + h * 0.003f, corte.right - w * 0.015f, corte.bottom - h * 0.006f)
        drawOval(t(ANILLO, oscuro), cara.topLeft, cara.size)
        val cx = cara.center.x + cara.width * 0.06f; val cy = cara.center.y
        val nAnillos = if (muyFino) 5 else if (fino) 3 else 0
        for (k in 1..nAnillos) {
            val f = k / (nAnillos + 1f)
            drawOval(t(PINO_OSC, oscuro).copy(alpha = 0.55f), Offset(cx - cara.width * 0.5f * f, cy - cara.height * 0.5f * f), Size(cara.width * f, cara.height * f), style = Stroke(lw * 0.8f))
        }
        drawCircle(t(VETA, oscuro), maxOf(h * 0.0035f, 0.5f * u), Offset(cx, cy))
        if (fino) drawLine(t(VETA, oscuro), Offset(cx + cara.width * 0.05f, cy), Offset(cara.right - cara.width * 0.06f, cy - cara.height * 0.12f), lw)
        // Las astillas alrededor del pie.
        if (fino) for (k in 0 until 4) {
            val x = if (k < 2) 0.04f + k * 0.05f else 0.84f + (k - 2) * 0.06f
            drawLine(t(PINO_LUZ, oscuro), pt(x, 0.958f), pt(x + 0.035f, 0.95f + 0.01f * azar(e.semilla, k)), lw * 1.3f, StrokeCap.Round)
        }
        // La chapa de hojalata del nivel, clavada y un poco torcida.
        val pl = fr(0.4f, 0.848f, 0.6f, 0.9f)
        rotate(-4f, pl.center) {
            chapaNivel(pl, e, h * 0.003f)
            val r = maxOf(pl.height * 0.09f, 0.5f * u)
            val clavos = if (fino) listOf(pl.left + pl.width * 0.1f to pl.top + pl.height * 0.22f, pl.right - pl.width * 0.1f to pl.top + pl.height * 0.22f,
                pl.left + pl.width * 0.1f to pl.bottom - pl.height * 0.22f, pl.right - pl.width * 0.1f to pl.bottom - pl.height * 0.22f)
            else listOf(pl.left + pl.width * 0.12f to pl.center.y, pl.right - pl.width * 0.12f to pl.center.y)
            clavos.forEach { (x, y) -> drawCircle(t(CLAVO, oscuro), r, Offset(x, y)) }
        }
    }

    /**
     * El banco de pino del almacén: el tablón con sus vetas y los cantos gastados, las dos patas abiertas con su travesaño y los
     * clavos de forja en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val tablon = fr(0.02f, 0f, 0.98f, 0.34f)
        val patas = listOf(poli(0.12f, 0.34f, 0.21f, 0.34f, 0.17f, 1f, 0.07f, 1f), poli(0.79f, 0.34f, 0.88f, 0.34f, 0.93f, 1f, 0.83f, 1f))
        if (!e.hecho) {
            drawPath(Path().apply { addRect(tablon); patas.forEach { addPath(it) }; addRect(fr(0.16f, 0.58f, 0.84f, 0.7f)) }, siluetaFuturo(oscuro)); return
        }
        // Las patas y el travesaño.
        patas.forEach { drawPath(it, t(PINO_OSC, oscuro)) }
        drawRect(t(PINO_OSC, oscuro), pt(0.16f, 0.58f), Size(w * 0.68f, h * 0.12f))
        drawRect(Color.Black.copy(alpha = 0.2f), pt(0.16f, 0.66f), Size(w * 0.68f, h * 0.04f))
        // El tablón con sus vetas y sus cantos.
        drawRect(t(PINO, oscuro), tablon.topLeft, tablon.size)
        vetas(tablon, t(VETA, oscuro).copy(alpha = 0.5f), e.semilla, densidad = 0.9f, grosor = 0.6f, ondulacion = 0.4f)
        drawRect(t(PINO_LUZ, oscuro), tablon.topLeft, Size(tablon.width, lw))
        drawRect(t(PINO_OSC, oscuro), Offset(tablon.left, tablon.bottom - lw), Size(tablon.width, lw))
        listOf(tablon.left, tablon.right - w * 0.025f).forEach { x -> drawRect(t(VETA, oscuro).copy(alpha = 0.6f), Offset(x, tablon.top), Size(w * 0.025f, tablon.height)) }
        // Los clavos de forja del nivel, encima de cada pata.
        listOf(0.14f, 0.19f, 0.81f, 0.86f).forEach { x -> remache(pt(x, 0.17f), h * 0.065f, e.base, e.brillo) }
        listOf(0.14f, 0.19f, 0.81f, 0.86f).forEach { x -> drawCircle(e.metalOscuro, h * 0.065f, pt(x, 0.17f), style = Stroke(maxOf(h * 0.015f, 0.4f * u))) }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = TIZA
    override fun rotuloPlaca(oscuro: Boolean): Color = PINO_LUZ

    /** La pizarra del almacén: la pizarra lisa en su marco de pino, con el fleje del nivel por dentro y un clavo en cada esquina. */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(2 * u)); return }
        // El marco de pino, con su veta.
        drawRoundRect(t(PINO, oscuro), Offset.Zero, size, CornerRadius(2 * u))
        drawRect(t(PINO_LUZ, oscuro), Offset(1 * u, 0.5f * u), Size(w - 2 * u, 0.8f * u))
        listOf(1.6f * u, h - 1.6f * u).forEach { y -> drawLine(t(VETA, oscuro).copy(alpha = 0.5f), Offset(6 * u, y), Offset(w - 9 * u, y), 0.5f * u) }
        drawRoundRect(t(PINO_OSC, oscuro), Offset.Zero, size, CornerRadius(2 * u), style = Stroke(0.7f * u))
        // El fleje del nivel y la pizarra.
        val b = 3.2f * u
        drawRect(e.base, Offset(b, b), Size(w - 2 * b, h - 2 * b))
        drawRect(e.metalOscuro, Offset(b, b), Size(w - 2 * b, h - 2 * b), style = Stroke(0.5f * u))
        val b2 = b + 1.1f * u
        drawRect(if (oscuro) Color(0xFF1E2522) else Color(0xFF2F3A35), Offset(b2, b2), Size(w - 2 * b2, h - 2 * b2))
        // Los clavos de las esquinas.
        listOf(Offset(1.7f * u, 1.7f * u), Offset(w - 1.7f * u, 1.7f * u), Offset(1.7f * u, h - 1.7f * u), Offset(w - 1.7f * u, h - 1.7f * u))
            .forEach { remache(it, 0.9f * u, t(CLAVO, oscuro), Color.White.copy(alpha = 0.35f)) }
    }

    // ---------------------------------------------------------------- el salón del hall

    /** La luz de la linterna, la cuerda de cáñamo, la arpillera de los costales y el hierro de los aros. */
    private val LLAMA = Color(0xFFFFC870)
    private val CANAMO = Color(0xFFC9A57A)
    private val ARPILLERA = Color(0xFFB89A6A)
    private val HIERRO = Color(0xFF3A3A3C)

    /**
     * El almacén de los pioneros. El muro es de tablas verticales de pino, cada una con sus vetas, su nudo y sus clavos, y
     * con las rendijas entre tabla y tabla; cada columna de trofeos queda entre dos postes de tronco descortezado, con su
     * corteza y sus anillos, unidos arriba por la viga y los jabalcones en diagonal. De cada poste cuelgan, en cada tramo de
     * ≈ 230 dp, la linterna de hojalata y, más abajo, el rollo de cuerda y la repisa con frascos de conserva. Abajo, el piso
     * de tablones y, al pie de los postes, el barril con sus aros y el costal de harina. De día, la luz cálida de las
     * linternas; en oscuro, la noche azul del claro entra por las rendijas en hilos de luna y las linternas arden más.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaAlmacen(g, e, oscuro) }
    }

    private fun DrawScope.escenaAlmacen(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)

        // Las tablas del muro: una sí y otra no un poco más oscura, con sus vetas, las rendijas, un nudo y dos clavos arriba.
        val anchoT = 24f * u
        var x = 0f; var k = 0
        while (x < w && k < 40) {
            val r = Rect(x, g.friso, x + anchoT, g.suelo)
            drawRect(m(if (k % 2 == 0) PINO else PINO_OSC, if (oscuro) 0.16f else 0.14f), r.topLeft, r.size)
            vetas(r, m(VETA, if (oscuro) 0.2f else 0.16f), e.semilla + k, densidad = 0.45f, vertical = true, grosor = 0.8f)
            val ny = g.friso + (g.suelo - g.friso) * azar(e.semilla, k + 30)
            drawOval(m(VETA, if (oscuro) 0.25f else 0.2f), Offset(x + anchoT * 0.3f, ny), Size(anchoT * 0.3f, 6f * u))
            drawLine(m(CORTEZA_OSC, if (oscuro) 0.6f else 0.3f), Offset(x, g.friso), Offset(x, g.suelo), 1.4f * u)
            listOf(0.3f, 0.7f).forEach { f -> drawCircle(m(CLAVO, 0.4f), 1f * u, Offset(x + anchoT * f, g.friso + 18f * u)) }
            x += anchoT; k++
        }
        // De noche, la luna entra en hilos por algunas rendijas (un degradado).
        if (oscuro) {
            val hilos = Path()
            var xr = anchoT * 2; var kr = 0
            while (xr < w && kr < 20) { if (azar(e.semilla, kr + 90) > 0.45f) hilos.addRect(Rect(xr - 0.7f * u, g.friso, xr + 0.7f * u, g.suelo)); xr += anchoT; kr++ }
            drawPath(hilos, Brush.verticalGradient(listOf(Color(0xFFBFD4FF).copy(alpha = 0.35f), Color(0xFFBFD4FF).copy(alpha = 0.05f)), g.friso, g.suelo))
        }
        // La luz cálida que las linternas echan sobre las tablas (capas planas).
        for (i in 0..g.n) repetirY(g.friso + 46f * u, g.suelo - 40f * u, g.modulo, 10) { _, y ->
            for (j in 0 until 3) drawCircle(LLAMA.copy(alpha = if (oscuro) 0.06f else 0.07f), (46f - j * 13f) * u, Offset(g.borde(i), y + 8f * u))
        }

        // La viga de arriba y los jabalcones en diagonal sobre cada franja.
        val viga = Rect(0f, 0f, w, g.friso + 4f * u)
        drawRect(m(CORTEZA, if (oscuro) 0.55f else 0.5f), viga.topLeft, viga.size)
        vetas(viga, m(CORTEZA_OSC, 0.5f), e.semilla + 77, densidad = 0.6f, grosor = 0.8f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.15f), Offset(0f, viga.bottom), Size(w, 2.4f * u))
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val l = minOf(v.width * 0.24f, 34f * u)
            listOf(v.left to 1f, v.right to -1f).forEach { (x0, s) ->
                drawLine(m(CORTEZA, if (oscuro) 0.55f else 0.48f), Offset(x0, viga.bottom + l), Offset(x0 + s * l, viga.bottom), 5f * u, StrokeCap.Square)
                drawLine(m(CORTEZA_OSC, 0.4f), Offset(x0, viga.bottom + l + 2f * u), Offset(x0 + s * (l + 2f * u), viga.bottom), 1f * u)
                drawCircle(m(CLAVO, 0.6f), 1.2f * u, Offset(x0 + s * 3f * u, viga.bottom + l - 3f * u))
            }
        }
        var xc = 12f * u; var kc = 0
        while (xc < w && kc < 40) { drawCircle(m(CLAVO, 0.6f), 1.2f * u, Offset(xc, viga.height * 0.5f)); xc += 38f * u; kc++ }

        // Los postes de tronco, con su corteza y, de arriba abajo, la linterna, la cuerda y la repisa de conservas.
        for (i in 0..g.n) {
            val xp = g.borde(i); val a = g.pil * 0.75f
            val poste = Rect(xp - a, viga.bottom, xp + a, g.suelo)
            drawRect(m(CORTEZA, if (oscuro) 0.6f else 0.55f), poste.topLeft, poste.size)
            drawRect(m(CORTEZA_OSC, if (oscuro) 0.6f else 0.5f), Offset(poste.right - a * 0.5f, poste.top), Size(a * 0.5f, poste.height))
            drawRect(m(PINO_LUZ, if (oscuro) 0.25f else 0.35f), Offset(poste.left + a * 0.3f, poste.top), Size(1.2f * u, poste.height))
            vetas(poste, m(CORTEZA_OSC, 0.55f), e.semilla + 200 + i, densidad = 0.8f, vertical = true, grosor = 0.9f, ondulacion = 0.6f)
            // Un muñón de rama cortada, con sus anillos.
            val nm = Offset(xp + a * 0.1f, viga.bottom + (g.suelo - viga.bottom) * (0.3f + 0.4f * azar(e.semilla, i + 300)))
            drawOval(m(ANILLO, if (oscuro) 0.45f else 0.6f), Offset(nm.x - 3.5f * u, nm.y - 2.5f * u), Size(7f * u, 5f * u))
            drawOval(m(VETA, 0.5f), Offset(nm.x - 2f * u, nm.y - 1.4f * u), Size(4f * u, 2.8f * u), style = Stroke(0.6f * u))
            repetirY(g.friso + 30f * u, g.suelo - 60f * u, g.modulo, 10) { _, y0 ->
                // La linterna de hojalata colgada de su clavo: el aro, el sombrerete, el vidrio con la llama y la base.
                val c = Offset(xp, y0 + 16f * u)
                drawCircle(m(CLAVO, 0.7f), 1.4f * u, Offset(xp, y0))
                drawArc(m(HIERRO, 0.7f), 180f, 180f, false, Offset(xp - 4f * u, y0), Size(8f * u, 8f * u), style = Stroke(0.9f * u))
                drawPath(Path().apply { moveTo(xp - 6f * u, y0 + 8f * u); lineTo(xp + 6f * u, y0 + 8f * u); lineTo(xp + 3f * u, y0 + 4f * u); lineTo(xp - 3f * u, y0 + 4f * u); close() }, m(HIERRO, 0.7f))
                drawCircle(LLAMA.copy(alpha = if (oscuro) 0.25f else 0.18f), 9f * u, c)
                drawRect(LLAMA.copy(alpha = if (oscuro) 0.75f else 0.55f), Offset(c.x - 4.5f * u, c.y - 7f * u), Size(9f * u, 14f * u))
                drawPath(Path().apply { moveTo(c.x, c.y - 5f * u); quadraticTo(c.x + 2.4f * u, c.y, c.x, c.y + 3f * u); quadraticTo(c.x - 2.4f * u, c.y, c.x, c.y - 5f * u); close() },
                    Color(0xFFFFF2C8).copy(alpha = 0.95f))
                listOf(-4.5f, 4.5f).forEach { dx -> drawLine(m(HIERRO, 0.75f), Offset(c.x + dx * u, c.y - 7f * u), Offset(c.x + dx * u, c.y + 7f * u), 1f * u) }
                drawRect(m(HIERRO, 0.75f), Offset(c.x - 6f * u, c.y + 7f * u), Size(12f * u, 3f * u))
                // El rollo de cuerda colgado más abajo.
                val rc = Offset(xp, y0 + 62f * u)
                if (rc.y < g.suelo - 70f * u) {
                    val cuerda = m(CANAMO, if (oscuro) 0.5f else 0.75f)
                    drawLine(m(CLAVO, 0.6f), Offset(xp, rc.y - 9f * u), Offset(xp, rc.y - 6f * u), 1.6f * u)
                    for (j in 0 until 3) drawOval(cuerda, Offset(rc.x - (8f - j) * u, rc.y - 6f * u + j * 0.8f * u), Size((16f - 2 * j) * u, (12f - j) * u), style = Stroke(1.6f * u))
                    drawLine(cuerda, Offset(rc.x + 3f * u, rc.y + 5f * u), Offset(rc.x + 5f * u, rc.y + 16f * u), 1.4f * u, StrokeCap.Round)
                }
                // La repisa de tabla con sus frascos de conserva (las tapas, la etiqueta y lo de dentro).
                val yr = y0 + 120f * u
                if (yr < g.suelo - 60f * u) {
                    val rw = g.pil + 10f * u
                    drawRect(m(PINO_OSC, if (oscuro) 0.55f else 0.6f), Offset(xp - rw, yr), Size(2 * rw, 3f * u))
                    drawLine(m(CORTEZA_OSC, 0.5f), Offset(xp - rw * 0.6f, yr + 3f * u), Offset(xp - rw * 0.2f, yr + 9f * u), 1.4f * u)
                    val tonos = listOf(Color(0xFFB2412E), Color(0xFFD9A43A), Color(0xFF6E8A3A))
                    listOf(-1f, 0f, 1f).forEachIndexed { j, s ->
                        val fx = xp + s * rw * 0.62f; val fh = (12f + 3f * azar(e.semilla + i, j + 400)) * u
                        drawRoundRect(m(Color(0xFFDCE8E4), if (oscuro) 0.2f else 0.5f), Offset(fx - 4f * u, yr - fh), Size(8f * u, fh), CornerRadius(1.5f * u))
                        drawRect(m(tonos[(i + j) % 3], if (oscuro) 0.45f else 0.6f), Offset(fx - 3.2f * u, yr - fh * 0.75f), Size(6.4f * u, fh * 0.72f))
                        drawRect(m(CLAVO, 0.6f), Offset(fx - 4f * u, yr - fh - 1.6f * u), Size(8f * u, 2f * u))
                        drawRect(m(TIZA, if (oscuro) 0.35f else 0.8f), Offset(fx - 3f * u, yr - fh * 0.55f), Size(6f * u, 3f * u))
                    }
                }
            }
        }

        // El piso de tablones, con sus juntas y sus clavos.
        drawRect(m(PINO_OSC, if (oscuro) 0.4f else 0.42f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.15f), Offset(0f, g.suelo), Size(w, 2f * u))
        val tablones = Path()
        tablones.moveTo(0f, g.suelo + (h - g.suelo) * 0.5f); tablones.lineTo(w, g.suelo + (h - g.suelo) * 0.5f)
        var xt = 0f; var kt = 0
        while (xt < w && kt < 30) {
            val y0 = g.suelo + (if (kt % 2 == 0) 0f else (h - g.suelo) * 0.5f)
            tablones.moveTo(xt, y0); tablones.lineTo(xt, y0 + (h - g.suelo) * 0.5f)
            xt += 46f * u; kt++
        }
        drawPath(tablones, m(VETA, if (oscuro) 0.6f else 0.5f), style = Stroke(1f * u))

        // Al pie de los postes: el barril con sus duelas y sus aros, y el costal de harina recargado.
        for (i in 0..g.n) {
            val xp = g.borde(i)
            val bw = minOf(2 * g.pil + 6f * u, 30f * u); val bh = 26f * u
            val b = Rect(xp - bw / 2, g.suelo - bh, xp + bw / 2, g.suelo)
            drawRoundRect(m(PINO_OSC, if (oscuro) 0.6f else 0.7f), b.topLeft, b.size, CornerRadius(bw * 0.25f, 4f * u))
            listOf(0.33f, 0.66f).forEach { f -> drawLine(m(VETA, 0.6f), Offset(b.left + bw * f, b.top + 1f * u), Offset(b.left + bw * f, b.bottom - 1f * u), 0.8f * u) }
            listOf(0.2f, 0.8f).forEach { f -> drawRect(m(HIERRO, if (oscuro) 0.7f else 0.7f), Offset(b.left, b.top + bh * f - 1.2f * u), Size(bw, 2.4f * u)) }
            drawOval(m(PINO, if (oscuro) 0.5f else 0.7f), Offset(b.left + 1f * u, b.top - 2f * u), Size(bw - 2f * u, 4f * u))
            // El costal, del lado de afuera del barril (en los bordes, hacia adentro del lienzo).
            val s = if (i == g.n) -1f else 1f
            val cx = xp + s * (bw / 2 + 4f * u)
            val costal = Path().apply {
                moveTo(cx - 6f * u, g.suelo); quadraticTo(cx - 8f * u, g.suelo - 12f * u, cx - 3f * u, g.suelo - 17f * u)
                lineTo(cx + 3f * u, g.suelo - 17f * u); quadraticTo(cx + 8f * u, g.suelo - 12f * u, cx + 6f * u, g.suelo); close()
            }
            drawPath(costal, m(ARPILLERA, if (oscuro) 0.4f else 0.6f))
            drawLine(m(CANAMO, 0.6f), Offset(cx - 3f * u, g.suelo - 15f * u), Offset(cx + 3f * u, g.suelo - 15f * u), 1.2f * u)
            drawRect(m(CORTEZA_OSC, 0.4f), Offset(cx - 2.4f * u, g.suelo - 9f * u), Size(4.8f * u, 3f * u))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La carreta de los colonos: la caja de tablas de pino con sus vetas, la lona de cáñamo sobre sus arcos con la boca
     * trasera, las dos ruedas de rayos con llanta de hierro (la de atrás más grande), la lanza, el farol colgado atrás (de
     * noche, encendido, con su halo) y el banderín del color de acento en la vara delantera.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 22f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val borde = if (oscuro) TIZA.copy(alpha = 0.5f) else CORTEZA_OSC
        val lona = if (oscuro) mezcla(TIZA, Color(0xFF100C08), 0.22f) else TIZA
        // La vara delantera con el banderín de acento.
        val vx = x + 7.6f * u
        drawLine(t(CORTEZA, oscuro), Offset(vx, pie - 8.4f * u), Offset(vx, pie - 22.5f * u), 0.8f * u)
        drawPath(Path().apply { moveTo(vx, pie - 22.5f * u); quadraticTo(vx + 3 * u, pie - 23 * u, vx + 6 * u, pie - 21 * u); lineTo(vx, pie - 18.6f * u); close() }, e.acento)
        drawPath(Path().apply { moveTo(vx, pie - 22.5f * u); quadraticTo(vx + 3 * u, pie - 23 * u, vx + 6 * u, pie - 21 * u); lineTo(vx, pie - 18.6f * u); close() }, borde, style = Stroke(0.4f * u))
        // La lona sobre sus arcos, con la boca trasera oscura.
        val lonaP = Path().apply {
            moveTo(x - 8.6f * u, pie - 8.4f * u); cubicTo(x - 10.6f * u, pie - 13 * u, x - 9.6f * u, pie - 17.6f * u, x - 6.4f * u, pie - 17.4f * u)
            cubicTo(x - 2 * u, pie - 18.6f * u, x + 3 * u, pie - 18.6f * u, x + 6 * u, pie - 16.6f * u)
            cubicTo(x + 8.6f * u, pie - 15 * u, x + 8.4f * u, pie - 11 * u, x + 7.2f * u, pie - 8.4f * u); close()
        }
        drawPath(lonaP, lona)
        drawPath(Path().apply { moveTo(x + 2.4f * u, pie - 18.2f * u); cubicTo(x + 5 * u, pie - 18 * u, x + 8.4f * u, pie - 15 * u, x + 7.2f * u, pie - 8.4f * u); lineTo(x + 4.6f * u, pie - 8.4f * u); cubicTo(x + 5.4f * u, pie - 12 * u, x + 4.6f * u, pie - 16 * u, x + 2.4f * u, pie - 18.2f * u); close() },
            t(ARPILLERA, oscuro).copy(alpha = 0.55f))
        listOf(-4.4f, -0.4f, 3.4f).forEach { dx -> drawLine(t(CANAMO, oscuro), Offset(x + dx * u, pie - 8.6f * u), Offset(x + (dx + 0.2f) * u, pie - 18 * u), 0.7f * u) }
        drawOval(t(CORTEZA_OSC, oscuro), Offset(x - 9.4f * u, pie - 16 * u), Size(3.4f * u, 7 * u))
        drawPath(lonaP, borde, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        // La caja de tablas.
        val caja = Rect(x - 9.4f * u, pie - 9 * u, x + 8.6f * u, pie - 4.8f * u)
        drawRect(t(PINO, oscuro), caja.topLeft, caja.size)
        drawLine(t(PINO_LUZ, oscuro), Offset(caja.left, caja.top + 0.5f * u), Offset(caja.right, caja.top + 0.5f * u), 0.8f * u)
        drawLine(t(VETA, oscuro), Offset(caja.left, caja.top + 2.2f * u), Offset(caja.right, caja.top + 2.2f * u), 0.4f * u)
        listOf(-5f, 1f, 6f).forEach { dx -> drawLine(t(PINO_OSC, oscuro), Offset(x + dx * u, caja.top), Offset(x + dx * u, caja.bottom), 0.6f * u) }
        drawRect(borde, caja.topLeft, caja.size, style = Stroke(0.5f * u))
        // La lanza hacia adelante.
        drawLine(t(CORTEZA, oscuro), Offset(caja.right, pie - 5.4f * u), Offset(x + 14 * u, pie - 4 * u), 0.9f * u, StrokeCap.Round)
        // Las ruedas de rayos, con su llanta de hierro y su cubo.
        listOf(Offset(x - 5.6f * u, pie - 3.8f * u) to 3.8f * u, Offset(x + 5.4f * u, pie - 3.1f * u) to 3.1f * u).forEach { (c, r) ->
            for (k in 0 until 6) { val a = k * Math.PI / 3 + 0.3; drawLine(t(CORTEZA, oscuro), c, Offset(c.x + (r * kotlin.math.cos(a)).toFloat(), c.y + (r * kotlin.math.sin(a)).toFloat()), 0.5f * u) }
            drawCircle(t(HIERRO, oscuro), r - 0.45f * u, c, style = Stroke(0.9f * u))
            if (oscuro) drawCircle(borde, r + 0.1f * u, c, style = Stroke(0.4f * u))
            drawCircle(t(CORTEZA_OSC, oscuro), 0.9f * u, c)
        }
        // El farol colgado atrás.
        val farol = Offset(x - 10.6f * u, pie - 10.6f * u)
        drawLine(t(HIERRO, oscuro), Offset(caja.left, pie - 8.6f * u), Offset(farol.x, farol.y - 2.2f * u), 0.5f * u)
        if (oscuro) { drawCircle(LLAMA.copy(alpha = 0.12f), 7 * u, farol); drawCircle(LLAMA.copy(alpha = 0.26f), 3.6f * u, farol) }
        drawRect(if (oscuro) LLAMA else mezcla(LLAMA, CORTEZA, 0.3f), Offset(farol.x - 1 * u, farol.y - 1.4f * u), Size(2 * u, 2.8f * u))
        drawRect(t(HIERRO, oscuro), Offset(farol.x - 1 * u, farol.y - 1.4f * u), Size(2 * u, 2.8f * u), style = Stroke(0.45f * u))
        drawRect(t(HIERRO, oscuro), Offset(farol.x - 1.3f * u, farol.y - 2.2f * u), Size(2.6f * u, 0.8f * u))
    }

    /**
     * El pueblo nuevo: la casa de tablas con techo de ladrillo, su puerta y dos ventanas, la chimenea con su humo, el campanario
     * con la campana, el remate y la bandera azul con su franja, un pino y la cerca. De noche, las ventanas se encienden con su
     * resplandor y salen las estrellas.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 54f * u, 44f * u)
        val x = b.x + 3 * u; val y = b.y
        val borde = if (oscuro) TIZA.copy(alpha = 0.45f) else CORTEZA_OSC
        val ladrillo = t(Color(0xFF9C4A2E), oscuro); val tabla = if (oscuro) mezcla(TIZA, Color(0xFF100C08), 0.28f) else Color(0xFFE9DCC0)
        val azul = t(Color(0xFF1F3A6E), oscuro)
        if (oscuro) listOf(Offset(-22f, -40f), Offset(-10f, -34f), Offset(12f, -41f), Offset(20f, -30f), Offset(-26f, -26f)).forEachIndexed { k, o ->
            drawCircle(TIZA.copy(alpha = 0.45f + 0.4f * azar(17, k)), (0.5f + 0.5f * azar(19, k)) * u, Offset(x + o.x * u, y + o.y * u))
        }
        // El suelo, el pino y la cerca.
        drawLine(t(Color(0xFF6B8A3A), oscuro), Offset(x - 27 * u, y), Offset(x + 21 * u, y), 1.4f * u)
        val pino = Offset(x - 20 * u, y)
        drawRect(t(CORTEZA, oscuro), Offset(pino.x - 0.7f * u, pino.y - 3 * u), Size(1.4f * u, 3 * u))
        listOf(Triple(3f, 5f, 7f), Triple(7f, 4f, 6f), Triple(10.5f, 3f, 5f)).forEach { (sube, ancho, alto) ->
            drawPath(Path().apply { moveTo(pino.x - ancho * u, pino.y - sube * u); lineTo(pino.x, pino.y - (sube + alto) * u); lineTo(pino.x + ancho * u, pino.y - sube * u); close() }, t(Color(0xFF3F6A3A), oscuro))
        }
        for (k in 0 until 3) drawLine(t(PINO_OSC, oscuro), Offset(x - 15 * u + k * 1.6f * u, y), Offset(x - 15 * u + k * 1.6f * u, y - 3 * u), 0.6f * u)
        drawLine(t(PINO_OSC, oscuro), Offset(x - 15.6f * u, y - 2.2f * u), Offset(x - 11.8f * u, y - 2.2f * u), 0.5f * u)
        // La chimenea y su humo.
        drawRect(ladrillo, Offset(x + 6 * u, y - 19 * u), Size(3 * u, 6 * u))
        for (k in 0 until 3) drawCircle((if (oscuro) Color(0xFFB8B4AA) else Color(0xFF8A8D91)).copy(alpha = 0.45f - k * 0.1f), (1.6f + k * 0.9f) * u, Offset(x + (8 + k * 3) * u, y - (21 + k * 4) * u))
        // La casa de tablas.
        val muro = Rect(x - 11 * u, y - 10 * u, x + 11 * u, y)
        drawRect(tabla, muro.topLeft, muro.size)
        for (k in 1 until 4) drawLine(ladrillo.copy(alpha = 0.28f), Offset(muro.left, y - k * 2.5f * u), Offset(muro.right, y - k * 2.5f * u), 0.5f * u)
        drawRect(borde, muro.topLeft, muro.size, style = Stroke(0.6f * u))
        val techo = Path().apply { moveTo(x - 13 * u, y - 10 * u); lineTo(x, y - 17 * u); lineTo(x + 13 * u, y - 10 * u); close() }
        drawPath(techo, ladrillo)
        drawPath(techo, borde, style = Stroke(0.6f * u, join = StrokeJoin.Round))
        drawRect(t(CORTEZA, oscuro), Offset(x - 2 * u, y - 6 * u), Size(4 * u, 6 * u))
        drawCircle(t(Color(0xFFC9A04A), oscuro), 0.4f * u, Offset(x + 1.2f * u, y - 3 * u))
        // Las ventanas: de noche, encendidas con su resplandor (los dos degradados).
        listOf(-7f, 4.6f).forEach { dx ->
            val c = Offset(x + (dx + 1.2f) * u, y - 6.3f * u)
            if (oscuro) drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.5f), Color.Transparent), c, 6 * u), 6 * u, c)
            drawRect(if (oscuro) LLAMA else azul.copy(alpha = 0.7f), Offset(c.x - 1.2f * u, c.y - 1.2f * u), Size(2.4f * u, 2.4f * u))
            drawLine(t(CORTEZA, oscuro), Offset(c.x, c.y - 1.2f * u), Offset(c.x, c.y + 1.2f * u), 0.4f * u)
            drawRect(t(CORTEZA, oscuro), Offset(c.x - 1.2f * u, c.y - 1.2f * u), Size(2.4f * u, 2.4f * u), style = Stroke(0.4f * u))
        }
        // El campanario con su campana, el remate y la bandera.
        val cr = Rect(x - 2.5f * u, y - 23 * u, x + 2.5f * u, y - 16 * u)
        drawRect(tabla, cr.topLeft, cr.size)
        drawPath(arcoMedioPunto(Rect(cr.left + 1 * u, cr.top + 1.2f * u, cr.right - 1 * u, cr.bottom - 1 * u)), t(CORTEZA_OSC, oscuro))
        drawArc(t(Color(0xFFC9A04A), oscuro), 180f, 180f, true, Offset(x - 1.3f * u, y - 20.6f * u), Size(2.6f * u, 3.2f * u))
        drawRect(borde, cr.topLeft, cr.size, style = Stroke(0.5f * u))
        drawPath(Path().apply { moveTo(x - 3.5f * u, y - 23 * u); lineTo(x, y - 29 * u); lineTo(x + 3.5f * u, y - 23 * u); close() }, ladrillo)
        drawLine(if (oscuro) TIZA.copy(alpha = 0.7f) else CORTEZA_OSC, Offset(x, y - 29 * u), Offset(x, y - 40 * u), 0.9f * u)
        val ban = Path().apply { moveTo(x, y - 40 * u); quadraticTo(x + 4 * u, y - 41.4f * u, x + 9 * u, y - 39.6f * u); lineTo(x + 9 * u, y - 34.6f * u); quadraticTo(x + 4 * u, y - 36.4f * u, x, y - 35 * u); close() }
        drawPath(ban, azul)
        drawLine(lona(oscuro), Offset(x, y - 37.6f * u), Offset(x + 9 * u, y - 37.2f * u), 1 * u)
        drawPath(ban, borde, style = Stroke(0.4f * u))
    }

    private fun lona(oscuro: Boolean): Color = if (oscuro) mezcla(TIZA, Color(0xFF100C08), 0.2f) else Color(0xFFF1E6C8)
}

/**
 * Ciudad verde: los logros de las ruinas. El trofeo descansa en un bloque de hormigón partido, tomado por el musgo y la hiedra;
 * en el hall, sobre un tambor de columna caído, y su placa es un letrero de lámina rescatado.
 */
object LogrosRuinas : DisenoLogros {
    private val HORMIGON = Color(0xFF8C8D84)
    private val HORMIGON_OSC = Color(0xFF65665E)
    private val HORMIGON_LUZ = Color(0xFFA9AAA0)
    private val MUSGO = Color(0xFF55722A)
    private val MUSGO_LUZ = Color(0xFF86A33C)
    private val HOJA = Color(0xFF3D6A2A)
    private val LUCIERNAGA = Color(0xFFE6FF7A)
    private val OXIDO = Color(0xFF8A4A22)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0A), 0.36f) else c

    /** Una hoja de hiedra: tres lóbulos alrededor de [c], de radio [r], girada [giro] grados. */
    private fun DrawScope.hoja(c: Offset, r: Float, giro: Float, color: Color) {
        rotate(giro, c) {
            drawPath(Path().apply {
                moveTo(c.x, c.y + r); quadraticTo(c.x - r * 1.1f, c.y + r * 0.2f, c.x - r * 0.9f, c.y - r * 0.2f)
                quadraticTo(c.x - r * 0.4f, c.y - r * 0.3f, c.x, c.y - r); quadraticTo(c.x + r * 0.4f, c.y - r * 0.3f, c.x + r * 0.9f, c.y - r * 0.2f)
                quadraticTo(c.x + r * 1.1f, c.y + r * 0.2f, c.x, c.y + r); close()
            }, color)
        }
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El bloque de hormigón partido: el trozo de losa con su canto roto y la cara del lado en sombra, la grava a la vista, la
     * grieta y las dos varillas oxidadas que asoman; el musgo lo cubre por arriba y la hiedra cuelga por un lado. Atornillada al
     * frente, la placa rescatada en el metal del nivel. De noche, una luciérnaga sobre el musgo.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val bloque = poli(0.15f, 0.79f, 0.38f, 0.786f, 0.44f, 0.776f, 0.6f, 0.781f, 0.66f, 0.8f, 0.85f, 0.795f, 0.87f, 0.83f, 0.85f, 0.87f, 0.86f, 0.952f, 0.14f, 0.954f)
        if (!e.hecho) { drawPath(bloque, siluetaFuturo(oscuro)); return }
        sombraPie(0.08f, 0.92f, oscuro)
        // Las varillas oxidadas, detrás del canto roto.
        val vr = maxOf(w * 0.012f, 0.7f * u)
        drawPath(Path().apply { moveTo(w * 0.69f, h * 0.81f); quadraticTo(w * 0.69f, h * 0.76f, w * 0.72f, h * 0.735f) }, t(OXIDO, oscuro), style = Stroke(vr, cap = StrokeCap.Round))
        if (fino) drawPath(Path().apply { moveTo(w * 0.77f, h * 0.81f); quadraticTo(w * 0.79f, h * 0.775f, w * 0.81f, h * 0.765f) }, t(OXIDO, oscuro), style = Stroke(vr * 0.9f, cap = StrokeCap.Round))
        // El hormigón, con la cara del lado en sombra.
        drawPath(bloque, t(HORMIGON, oscuro))
        drawPath(poli(0.78f, 0.797f, 0.85f, 0.795f, 0.87f, 0.83f, 0.85f, 0.87f, 0.86f, 0.952f, 0.79f, 0.953f), t(HORMIGON_OSC, oscuro))
        drawPath(poli(0.6f, 0.781f, 0.66f, 0.8f, 0.78f, 0.797f, 0.62f, 0.792f), t(HORMIGON_LUZ, oscuro))
        // La grava a la vista y la grieta.
        if (fino) for (k in 0 until if (muyFino) 22 else 10) {
            val x = 0.18f + 0.6f * azar(e.semilla, k); val y = 0.81f + 0.13f * azar(e.semilla, k + 40)
            drawCircle(if (k % 3 == 0) t(HORMIGON_LUZ, oscuro) else t(HORMIGON_OSC, oscuro), (0.4f + 0.7f * azar(e.semilla, k + 80)) * u, pt(x, y))
        }
        drawPath(Path().apply { moveTo(w * 0.6f, h * 0.783f); lineTo(w * 0.57f, h * 0.83f); lineTo(w * 0.6f, h * 0.86f); lineTo(w * 0.55f, h * 0.93f) },
            Color.Black.copy(alpha = 0.45f), style = Stroke(lw, join = StrokeJoin.Round))
        // La placa rescatada, en el metal del nivel, con sus tornillos.
        val pl = fr(0.42f, 0.858f, 0.64f, 0.912f)
        rotate(3f, pl.center) {
            chapaNivel(pl, e, h * 0.004f)
            if (fino) listOf(pl.left + pl.width * 0.1f, pl.right - pl.width * 0.1f).forEach { x -> drawCircle(e.metalOscuro, pl.height * 0.1f, Offset(x, pl.center.y)) }
            if (fino) drawCircle(t(OXIDO, oscuro).copy(alpha = 0.5f), pl.height * 0.16f, Offset(pl.left + pl.width * 0.72f, pl.bottom - pl.height * 0.25f))
        }
        // El musgo por arriba.
        val matas = if (fino) 7 else 4
        for (k in 0 until matas) {
            val x = 0.15f + 0.4f * k / matas; val ww = 0.1f + 0.05f * azar(e.semilla, k + 120)
            drawOval(t(if (k % 2 == 0) MUSGO else MUSGO_LUZ, oscuro), pt(x, 0.772f - 0.006f * azar(e.semilla, k + 130)), Size(w * ww, h * 0.026f))
        }
        // La hiedra que cuelga por la izquierda.
        val tallo = Path().apply { moveTo(w * 0.2f, h * 0.79f); cubicTo(w * 0.16f, h * 0.83f, w * 0.23f, h * 0.87f, w * 0.19f, h * 0.92f) }
        drawPath(tallo, t(HOJA, oscuro), style = Stroke(lw, cap = StrokeCap.Round))
        val hojas = if (fino) listOf(0.18f to 0.82f, 0.22f to 0.86f, 0.18f to 0.9f, 0.205f to 0.935f) else listOf(0.19f to 0.84f, 0.2f to 0.91f)
        hojas.forEachIndexed { i, (x, y) -> hoja(pt(x, y), h * 0.017f, if (i % 2 == 0) -30f else 25f, t(if (i % 2 == 0) HOJA else MUSGO, oscuro)) }
        // La luciérnaga.
        if (oscuro && fino) {
            drawCircle(LUCIERNAGA.copy(alpha = 0.22f), h * 0.014f, pt(0.36f, 0.75f))
            drawCircle(LUCIERNAGA, h * 0.004f, pt(0.36f, 0.75f))
        }
    }

    /**
     * El tambor de columna caído: el trozo de fuste de hormigón tumbado, con sus estrías gastadas y el corte a la vista con las
     * puntas de las varillas; el musgo encima, una mata de hierba al pie y la abrazadera rescatada en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val fuste = fr(0.06f, 0.12f, 0.88f, 0.96f)
        val corte = fr(0.8f, 0.12f, 0.96f, 0.96f)
        if (!e.hecho) {
            drawPath(Path().apply { addRoundRect(RoundRect(fuste, CornerRadius(h * 0.2f))); addOval(corte) }, siluetaFuturo(oscuro)); return
        }
        drawRoundRect(t(HORMIGON, oscuro), fuste.topLeft, fuste.size, CornerRadius(h * 0.2f))
        drawRect(t(HORMIGON_LUZ, oscuro), Offset(fuste.left + h * 0.2f, fuste.top + h * 0.06f), Size(fuste.width - h * 0.2f, h * 0.12f))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(fuste.left + h * 0.15f, fuste.bottom - h * 0.2f), Size(fuste.width - h * 0.15f, h * 0.16f))
        // Las estrías gastadas.
        listOf(0.36f, 0.54f, 0.72f).forEach { y -> drawLine(t(HORMIGON_OSC, oscuro), pt(0.1f, y), pt(0.8f, y + 0.02f), lw * 0.8f) }
        // La abrazadera rescatada, del nivel.
        chapaNivel(fr(0.3f, 0.08f, 0.38f, 1f), e, w * 0.01f)
        // El corte, con las puntas de las varillas.
        drawOval(t(HORMIGON_LUZ, oscuro), corte.topLeft, corte.size)
        drawOval(t(HORMIGON_OSC, oscuro), corte.topLeft, corte.size, style = Stroke(lw))
        listOf(0.4f, 0.62f).forEach { y -> drawCircle(t(OXIDO, oscuro), h * 0.05f, Offset(corte.center.x, h * y)) }
        // El musgo encima y la hierba al pie.
        for (k in 0 until 5) drawOval(t(if (k % 2 == 0) MUSGO else MUSGO_LUZ, oscuro), pt(0.42f + k * 0.07f, 0.02f + 0.04f * azar(e.semilla, k)), Size(w * 0.09f, h * 0.2f))
        for (k in 0 until 5) drawLine(t(HOJA, oscuro), pt(0.06f + k * 0.018f, 1f), pt(0.04f + k * 0.03f, 0.55f + 0.1f * azar(e.semilla, k + 9)), lw, StrokeCap.Round)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3E8D6) else Color(0xFF1E2318)
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFB9D86A) else Color(0xFF3F5A1C)

    /**
     * La chapa rescatada: el letrero de lámina con las esquinas redondeadas, el borde en el metal del nivel con sus puntos de
     * óxido, cuatro agujeros de tornillo y una mata de musgo prendida en una esquina.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(4 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(4 * u))
        drawRect(e.brillo, Offset(3 * u, 0.6f * u), Size(w - 6 * u, 0.8f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        for (k in 0 until 4) drawCircle(t(OXIDO, oscuro).copy(alpha = 0.55f), (0.5f + 0.5f * azar(e.semilla, k)) * u,
            Offset(w * (0.15f + 0.7f * azar(e.semilla, k + 20)), if (k % 2 == 0) 0.9f * u else h - 0.9f * u))
        val b = 1.8f * u
        drawRoundRect(if (oscuro) Color(0xFF1B1E18) else Color(0xFFEAE8DC), Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(3 * u))
        listOf(Offset(4 * u, 4 * u), Offset(w - 4 * u, 4 * u), Offset(4 * u, h - 4 * u), Offset(w - 4 * u, h - 4 * u)).forEach {
            drawCircle(e.metalOscuro, 1 * u, it); drawCircle(Color.Black.copy(alpha = 0.6f), 0.5f * u, it)
        }
        // La mata de musgo prendida en la esquina de arriba a la izquierda.
        listOf(Offset(0.5f * u, 2.5f * u) to 2.6f, Offset(3 * u, 0.4f * u) to 2.2f, Offset(5.5f * u, 0.8f * u) to 1.6f).forEachIndexed { i, (o, r) ->
            drawCircle(t(if (i % 2 == 0) MUSGO else MUSGO_LUZ, oscuro), r * u, o)
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /** El yeso del muro, el ladrillo que asoma, la madera de los estantes (con su veta), el tallo de la hiedra y los lomos de los libros. */
    private val YESO = Color(0xFFD8D2C2)
    private val LADRILLO = Color(0xFF9A5A3E)
    private val ESTANTE = Color(0xFF6E5A40)
    private val TALLO = Color(0xFF4A3A22)
    private val VETA = Color(0xFF3E3020)
    private val LOMOS = listOf(Color(0xFF8A4A22), Color(0xFF3E5A6A), Color(0xFF6A5A2A), Color(0xFF5A3A4A), Color(0xFF4F6B3A))

    /**
     * La biblioteca invadida, en la ciudad que volvió a ser bosque. El muro es de yeso descascarado, con las manchas de
     * humedad y, donde se cayó el yeso, los ladrillos de debajo; cada columna de trofeos queda bajo el arco roto de un
     * ventanal sin vidrios (el marco, el parteluz partido, la hiedra que entra por él). Entre los ventanales se levantan los
     * estantes altos, con sus libros de lomos desteñidos, recargados o tumbados, y los huecos de los que se cayeron; la hiedra
     * baja por ellos desde la viga. Arriba, la viga partida y, por el boquete del techo, la luz que entra en diagonal; abajo,
     * el piso de duela levantado, los escombros, los libros caídos, el pasto y los helechos que salen de las juntas. De
     * noche, el boquete deja ver la luna: el haz es frío y azul, y las luciérnagas encienden el salón.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaBiblioteca(g, e, oscuro) }
    }

    private fun DrawScope.escenaBiblioteca(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val hiedra = m(HOJA, if (oscuro) 0.55f else 0.5f)
        val hiedraLuz = m(MUSGO_LUZ, if (oscuro) 0.4f else 0.45f)

        // Las manchas de humedad que bajan del techo y los parches donde se cayó el yeso y asoma el ladrillo.
        for (k in 0 until 6) {
            val x = w * azar(e.semilla + 1, k); val l = (60f + 140f * azar(e.semilla + 1, k + 10)) * u
            drawOval(m(HORMIGON_OSC, if (oscuro) 0.12f else 0.1f), Offset(x - 16f * u, g.friso - 10f * u), Size(32f * u, l))
        }
        for (k in 0 until (g.modulos * 2).coerceAtMost(16)) {
            val pc = Offset(w * azar(e.semilla + 2, k), g.friso + (g.suelo - g.friso) * azar(e.semilla + 2, k + 30))
            val pw = (20f + 26f * azar(e.semilla + 2, k + 60)) * u; val ph = (14f + 16f * azar(e.semilla + 2, k + 90)) * u
            val parche = Rect(pc.x - pw / 2, pc.y - ph / 2, pc.x + pw / 2, pc.y + ph / 2)
            val borde = contornoIrregular(parche, e.semilla + k, 16, 0.25f)
            drawPath(borde, m(LADRILLO, if (oscuro) 0.14f else 0.12f))
            clipPath(borde) {
                val juntas = Path()
                repetirY(parche.top, parche.bottom, 5f * u, 12) { j, y ->
                    juntas.moveTo(parche.left, y); juntas.lineTo(parche.right, y)
                    var x = parche.left + (if (j % 2 == 0) 0f else 6f * u); var q = 0
                    while (x < parche.right && q < 10) { juntas.moveTo(x, y); juntas.lineTo(x, y + 5f * u); x += 12f * u; q++ }
                }
                drawPath(juntas, m(YESO, if (oscuro) 0.2f else 0.4f), style = Stroke(0.8f * u))
            }
            drawPath(borde, m(HORMIGON_OSC, if (oscuro) 0.25f else 0.2f), style = Stroke(0.8f * u))
        }
        // Las grietas del yeso.
        val grietas = Path()
        for (k in 0 until 8) {
            var p = Offset(w * azar(e.semilla + 4, k), g.friso + (g.suelo - g.friso) * azar(e.semilla + 4, k + 20))
            grietas.moveTo(p.x, p.y)
            for (j in 0 until 5) {
                p = Offset(p.x + (azar(e.semilla + 4, k * 10 + j) - 0.5f) * 14f * u, p.y + (4f + 8f * azar(e.semilla + 4, k * 10 + j + 200)) * u)
                grietas.lineTo(p.x, p.y)
            }
        }
        drawPath(grietas, m(HORMIGON_OSC, if (oscuro) 0.3f else 0.25f), style = Stroke(0.8f * u))

        // Los ventanales rotos: el arco, el marco, el parteluz partido y la hiedra que entra.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val r = Rect(v.left + 6f * u, g.friso + 16f * u, v.right - 6f * u, g.suelo - 34f * u)
            if (r.height < 60f * u) continue
            val arco = arcoMedioPunto(r)
            drawPath(arco, if (oscuro) m(Color(0xFF1A2A3A), 0.4f) else m(Color(0xFFEAF2E6), 0.5f))
            drawPath(arco, m(HORMIGON, if (oscuro) 0.4f else 0.42f), style = Stroke(4f * u))
            drawPath(arco, m(HORMIGON_OSC, if (oscuro) 0.3f else 0.3f), style = Stroke(1f * u))
            // Las dovelas del arco.
            val rad = r.width / 2; val ca = Offset(r.center.x, r.top + rad)
            for (k in 0..8) {
                val a = Math.PI + k * Math.PI / 8
                val p0 = Offset(ca.x + ((rad - 2f * u) * kotlin.math.cos(a)).toFloat(), ca.y + ((rad - 2f * u) * kotlin.math.sin(a)).toFloat())
                val p1 = Offset(ca.x + ((rad + 6f * u) * kotlin.math.cos(a)).toFloat(), ca.y + ((rad + 6f * u) * kotlin.math.sin(a)).toFloat())
                drawLine(m(HORMIGON_OSC, 0.3f), p0, p1, 0.9f * u)
            }
            // El parteluz, partido a media altura, y el travesaño torcido.
            val px = r.center.x
            val corte = minOf(r.top + rad + 70f * u, r.bottom)
            drawLine(m(OXIDO, if (oscuro) 0.3f else 0.28f), Offset(px, r.top), Offset(px, corte), 2f * u)
            drawLine(m(OXIDO, if (oscuro) 0.3f else 0.28f), Offset(px, corte), Offset(px + 6f * u, corte + 12f * u), 2f * u)
            drawLine(m(OXIDO, if (oscuro) 0.25f else 0.22f), Offset(r.left, ca.y + 4f * u), Offset(r.right, ca.y - 2f * u), 1.6f * u)
            // Los travesaños de cada tramo, más abajo: unos enteros y otros caídos a medias.
            repetirY(ca.y + g.modulo * 0.9f, r.bottom - 30f * u, g.modulo, 10) { k, y ->
                if (azar(e.semilla + i, k + 600) > 0.5f) drawLine(m(OXIDO, if (oscuro) 0.22f else 0.2f), Offset(r.left, y), Offset(r.right, y), 1.6f * u)
                else {
                    drawLine(m(OXIDO, if (oscuro) 0.22f else 0.2f), Offset(r.left, y), Offset(r.left + r.width * 0.4f, y + 4f * u), 1.6f * u)
                    drawLine(m(OXIDO, if (oscuro) 0.22f else 0.2f), Offset(r.right, y), Offset(r.right - r.width * 0.2f, y + 10f * u), 1.6f * u)
                }
            }
            // El alféizar, con su mata de pasto.
            drawRect(m(HORMIGON, if (oscuro) 0.45f else 0.5f), Offset(r.left - 4f * u, r.bottom), Size(r.width + 8f * u, 4f * u))
            for (k in 0 until 5) drawLine(m(MUSGO, 0.55f), Offset(r.left + 6f * u + k * 2.5f * u, r.bottom), Offset(r.left + 4f * u + k * 3.5f * u, r.bottom - (5f + 4f * azar(e.semilla, k + i * 7)) * u), 0.9f * u)
            // La hiedra que entra por el arco y cuelga por un lado.
            val lado = if (i % 2 == 0) r.left + 8f * u else r.right - 8f * u
            var p = Offset(lado, r.top + rad * 0.5f)
            val tallo = Path().apply { moveTo(p.x, p.y) }
            for (k in 0 until 9) {
                p = Offset(p.x + (azar(e.semilla + i, k + 500) - 0.5f) * 6f * u, p.y + 9f * u); tallo.lineTo(p.x, p.y)
                hoja(Offset(p.x + (if (k % 2 == 0) 3f else -3f) * u, p.y), 2.6f * u, if (k % 2 == 0) 30f else -30f, if (k % 3 == 0) hiedraLuz else hiedra)
            }
            drawPath(tallo, m(TALLO, 0.5f), style = Stroke(0.8f * u))
        }

        // La luz por el boquete del techo, en diagonal (un degradado): cálida de día, la luna de noche.
        val xb = w * 0.62f
        val haz = Path().apply { moveTo(xb - 18f * u, 0f); lineTo(xb + 18f * u, 0f); lineTo(xb - w * 0.15f + 50f * u, h); lineTo(xb - w * 0.15f - 50f * u, h); close() }
        val luzHaz = if (oscuro) Color(0xFFCFE0FF) else Color(0xFFFFF4C2)
        drawPath(haz, Brush.verticalGradient(listOf(luzHaz.copy(alpha = if (oscuro) 0.12f else 0.3f), luzHaz.copy(alpha = 0.02f)), 0f, h))

        // Los estantes entre los ventanales: el mueble, las tablas y los libros (con huecos, recargados o tumbados).
        for (i in 0..g.n) {
            val x0 = g.borde(i) - g.pil; val x1 = g.borde(i) + g.pil
            val y0 = g.friso + 6f * u
            drawRect(m(ESTANTE, if (oscuro) 0.25f else 0.2f), Offset(x0, y0), Size(x1 - x0, g.suelo - y0))
            drawRect(m(ESTANTE, if (oscuro) 0.6f else 0.55f), Offset(x0, y0), Size(x1 - x0, g.suelo - y0), style = Stroke(2.4f * u))
            repetirY(y0 + 30f * u, g.suelo - 4f * u, 30f * u, 60) { k, y ->
                drawLine(m(ESTANTE, if (oscuro) 0.6f else 0.55f), Offset(x0, y), Offset(x1, y), 2.2f * u)
                var bx = x0 + 2f * u; var q = 0
                while (bx < x1 - 3f * u && q < 12) {
                    val s = e.semilla + i * 1000 + k * 20 + q
                    val bw = (2.6f + 2.4f * azar(s, 1)) * u; val bh = (13f + 9f * azar(s, 2)) * u
                    val col = LOMOS[(azar(s, 3) * LOMOS.size).toInt().coerceIn(0, LOMOS.size - 1)]
                    val r = azar(s, 4)
                    when {
                        r < 0.18f -> bx += bw * 1.6f   // un hueco
                        r < 0.27f && bx + bh < x1 - 2f * u -> {   // un libro tumbado
                            drawRect(m(col, if (oscuro) 0.4f else 0.42f), Offset(bx, y - bw - 1.1f * u), Size(bh, bw)); bx += bh + 0.6f * u
                        }
                        else -> {
                            val inclinado = r > 0.92f
                            if (inclinado) rotate(14f, Offset(bx, y - 1.1f * u)) { drawRect(m(col, if (oscuro) 0.4f else 0.42f), Offset(bx, y - bh - 1.1f * u), Size(bw, bh)) }
                            else {
                                drawRect(m(col, if (oscuro) 0.4f else 0.42f), Offset(bx, y - bh - 1.1f * u), Size(bw, bh))
                                drawLine(m(YESO, 0.25f), Offset(bx, y - bh * 0.75f), Offset(bx + bw, y - bh * 0.75f), 0.6f * u)
                            }
                            bx += bw + 0.6f * u
                        }
                    }
                    q++
                }
            }
            // La hiedra que baja por el estante desde la viga.
            val xh = g.borde(i) + (if (i % 2 == 0) -g.pil * 0.6f else g.pil * 0.6f)
            val largo = (80f + 120f * azar(e.semilla + 8, i)) * u
            var p = Offset(xh, g.friso)
            val tallo = Path().apply { moveTo(p.x, p.y) }
            repetirY(g.friso + 8f * u, minOf(g.friso + largo, g.suelo - 20f * u), 8f * u, 40) { k, y ->
                p = Offset(xh + kotlin.math.sin(k * 0.9f) * 3f * u, y); tallo.lineTo(p.x, p.y)
                hoja(Offset(p.x + (if (k % 2 == 0) 3.4f else -3.4f) * u, p.y), 3f * u, if (k % 2 == 0) 40f else -40f, if (k % 3 == 0) hiedraLuz else hiedra)
            }
            drawPath(tallo, m(TALLO, 0.5f), style = Stroke(0.9f * u))
        }

        // La viga partida: dos trozos con el hueco del boquete en medio, y las astillas.
        val viga = m(ESTANTE, if (oscuro) 0.55f else 0.5f)
        drawRect(viga, Offset(0f, 0f), Size(xb - 26f * u, g.friso))
        drawRect(viga, Offset(xb + 26f * u, 0f), Size(w - xb - 26f * u, g.friso))
        vetas(Rect(0f, 0f, w, g.friso), m(VETA, 0.35f), e.semilla + 50, densidad = 0.5f, grosor = 0.7f)
        drawPath(Path().apply { moveTo(xb - 26f * u, 0f); lineTo(xb - 14f * u, g.friso * 0.4f); lineTo(xb - 22f * u, g.friso * 0.6f); lineTo(xb - 12f * u, g.friso); lineTo(xb - 26f * u, g.friso); close() }, viga)
        drawPath(Path().apply { moveTo(xb + 26f * u, 0f); lineTo(xb + 16f * u, g.friso * 0.5f); lineTo(xb + 26f * u, g.friso); close() }, viga)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, g.friso), Size(w, 2f * u))
        if (oscuro) {
            drawCircle(Color(0xFFE8F0FF).copy(alpha = 0.15f), 9f * u, Offset(xb, g.friso * 0.5f))
            drawCircle(Color(0xFFE8F0FF).copy(alpha = 0.85f), 4f * u, Offset(xb, g.friso * 0.5f))
        }

        // El piso de duela levantado, los escombros, los libros caídos y el pasto.
        drawRect(m(ESTANTE, if (oscuro) 0.3f else 0.28f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        val duela = Path()
        var xd = 0f; var kd = 0
        while (xd < w && kd < 60) { duela.moveTo(xd, g.suelo); duela.lineTo(xd + 2f * u, h); xd += 20f * u; kd++ }
        drawPath(duela, m(VETA, 0.4f), style = Stroke(0.8f * u))
        for (k in 0 until 18) {
            val x = w * azar(e.semilla + 12, k); val s = (2.5f + 5f * azar(e.semilla + 12, k + 30)) * u
            drawPath(Path().apply { moveTo(x, g.suelo + 2f * u); lineTo(x + s * 0.4f, g.suelo + 2f * u - s); lineTo(x + s * 1.3f, g.suelo + 2f * u - s * 0.7f); lineTo(x + s * 1.5f, g.suelo + 2f * u); close() },
                m(HORMIGON, if (oscuro) 0.4f else 0.45f))
        }
        for (i in 0..g.n) {
            val x = g.borde(i)
            // Un libro caído, abierto, al pie del estante.
            val lb = Offset(x + (if (i == g.n) -14f else 6f) * u, g.suelo + 2f * u)
            drawPath(Path().apply { moveTo(lb.x, lb.y); lineTo(lb.x + 5f * u, lb.y - 3f * u); lineTo(lb.x + 10f * u, lb.y); close() }, m(YESO, if (oscuro) 0.35f else 0.85f))
            drawLine(m(LOMOS[i % LOMOS.size], 0.6f), Offset(lb.x - 0.5f * u, lb.y + 0.5f * u), Offset(lb.x + 10.5f * u, lb.y + 0.5f * u), 1.4f * u)
            // El helecho y el pasto que salen de las juntas.
            for (k in 0 until 7) {
                val a = -Math.PI / 2 + (k - 3) * 0.32
                val l = (10f + 8f * azar(e.semilla + i, k + 700)) * u
                drawPath(Path().apply {
                    moveTo(x, g.suelo); quadraticTo(x + (l * 0.5f * kotlin.math.cos(a + 0.25)).toFloat(), g.suelo + (l * 0.5f * kotlin.math.sin(a + 0.25)).toFloat(),
                        x + (l * kotlin.math.cos(a)).toFloat(), g.suelo + (l * kotlin.math.sin(a)).toFloat())
                }, if (k % 2 == 0) hiedra else hiedraLuz, style = Stroke(1.4f * u, cap = StrokeCap.Round))
            }
        }

        // De noche, las luciérnagas: su halo y su punto de luz.
        if (oscuro) for (k in 0 until 26) {
            val p = Offset(w * azar(e.semilla + 13, k), g.friso + (g.suelo - g.friso) * azar(e.semilla + 13, k + 40))
            drawCircle(LUCIERNAGA.copy(alpha = 0.12f), 5f * u, p)
            drawCircle(LUCIERNAGA.copy(alpha = 0.8f), 1.1f * u, p)
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La ciclista de las ruinas: la bicicleta de cuadro oxidado con sus ruedas de rayos, la chamarra de musgo, la mochila de
     * la que brota una mata de hojas, la pañoleta del color de acento que ondea detrás y, de noche, la lámpara del manubrio con
     * su haz y dos luciérnagas que la siguen.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 20f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val borde = if (oscuro) HORMIGON_LUZ.copy(alpha = 0.6f) else Color(0xFF2E332E)
        val oxido = t(OXIDO, oscuro); val piel = t(Color(0xFFC89A72), oscuro)
        val ra = 4f * u
        val tras = Offset(x - 6.4f * u, pie - ra); val del = Offset(x + 6.6f * u, pie - ra)
        // Las ruedas: la llanta, los rayos y el cubo.
        listOf(tras, del).forEach { c ->
            for (k in 0 until 4) { val a = k * Math.PI / 4; val d = Offset((ra * 0.9f * kotlin.math.cos(a)).toFloat(), (ra * 0.9f * kotlin.math.sin(a)).toFloat()); drawLine(borde.copy(alpha = 0.5f), c - d, c + d, 0.35f * u) }
            drawCircle(borde, ra - 0.5f * u, c, style = Stroke(1f * u))
            drawCircle(oxido, 0.6f * u, c)
        }
        // El cuadro, el sillín, el manubrio y la lámpara.
        val sillin = Offset(x - 2.4f * u, pie - 11 * u); val pedal = Offset(x - 0.2f * u, pie - 4 * u); val cabeza = Offset(x + 4.4f * u, pie - 11.6f * u)
        val cuadro = Path().apply { moveTo(tras.x, tras.y); lineTo(sillin.x, sillin.y); lineTo(pedal.x, pedal.y); close(); moveTo(sillin.x, sillin.y); lineTo(cabeza.x, cabeza.y); lineTo(pedal.x, pedal.y); moveTo(cabeza.x, cabeza.y); lineTo(del.x, del.y) }
        drawPath(cuadro, oxido, style = Stroke(1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(borde, Offset(sillin.x - 1.6f * u, sillin.y - 0.6f * u), Offset(sillin.x + 1.2f * u, sillin.y - 0.6f * u), 1f * u, StrokeCap.Round)
        val manubrio = Offset(cabeza.x - 0.4f * u, cabeza.y - 2 * u)
        drawLine(oxido, cabeza, manubrio, 1f * u, StrokeCap.Round)
        drawLine(borde, Offset(manubrio.x - 1.2f * u, manubrio.y), Offset(manubrio.x + 1 * u, manubrio.y + 0.4f * u), 0.9f * u, StrokeCap.Round)
        val lampara = Offset(cabeza.x + 1 * u, cabeza.y + 0.2f * u)
        if (oscuro) {
            drawPath(Path().apply { moveTo(lampara.x, lampara.y - 0.6f * u); lineTo(lampara.x + 10 * u, lampara.y - 1.4f * u); lineTo(lampara.x + 10 * u, lampara.y + 5 * u); lineTo(lampara.x, lampara.y + 0.6f * u); close() }, LUCIERNAGA.copy(alpha = 0.16f))
            drawCircle(LUCIERNAGA.copy(alpha = 0.3f), 2.2f * u, lampara)
        }
        drawCircle(if (oscuro) LUCIERNAGA else t(Color(0xFFE0B040), oscuro), 0.8f * u, lampara)
        // La ciclista: las piernas a los pedales, la mochila con su mata, la chamarra, el brazo, la cabeza y la pañoleta.
        val cadera = Offset(sillin.x + 0.2f * u, sillin.y - 1 * u)
        drawLine(t(Color(0xFF4A4A40), oscuro), cadera, Offset(pedal.x + 1.4f * u, pedal.y + 0.8f * u), 1.4f * u, StrokeCap.Round)
        drawLine(t(Color(0xFF5A5A4E), oscuro), cadera, Offset(x + 2.6f * u, pie - 8 * u), 1.4f * u, StrokeCap.Round)
        drawLine(t(Color(0xFF5A5A4E), oscuro), Offset(x + 2.6f * u, pie - 8 * u), Offset(pedal.x - 1.2f * u, pedal.y - 0.6f * u), 1.3f * u, StrokeCap.Round)
        val hombro = Offset(x + 1 * u, pie - 17.6f * u)
        rotate(-18f, Offset(x - 2.4f * u, pie - 16 * u)) {
            drawRoundRect(t(HORMIGON, oscuro), Offset(x - 4.4f * u, pie - 19 * u), Size(3.8f * u, 5.6f * u), CornerRadius(1 * u))
            drawRoundRect(borde, Offset(x - 4.4f * u, pie - 19 * u), Size(3.8f * u, 5.6f * u), CornerRadius(1 * u), style = Stroke(0.45f * u))
        }
        hoja(Offset(x - 4.6f * u, pie - 21.6f * u), 1.7f * u, -30f, t(HOJA, oscuro))
        hoja(Offset(x - 2.2f * u, pie - 22.6f * u), 1.5f * u, 20f, t(MUSGO_LUZ, oscuro))
        drawLine(t(TALLO, oscuro), Offset(x - 3.4f * u, pie - 19.4f * u), Offset(x - 3.4f * u, pie - 21.4f * u), 0.5f * u)
        drawLine(borde, cadera, hombro, 3.2f * u, StrokeCap.Round)
        drawLine(t(MUSGO, oscuro), cadera, hombro, 2.4f * u, StrokeCap.Round)
        drawLine(t(MUSGO, oscuro), Offset(hombro.x + 0.4f * u, hombro.y + 0.6f * u), Offset(manubrio.x - 0.6f * u, manubrio.y), 1.2f * u, StrokeCap.Round)
        val cab = Offset(hombro.x + 1.6f * u, hombro.y - 2.6f * u)
        drawPath(Path().apply { moveTo(cab.x - 1.4f * u, cab.y - 0.8f * u); quadraticTo(cab.x - 4.6f * u, cab.y - 1.4f * u, cab.x - 6.6f * u, cab.y + 0.6f * u); quadraticTo(cab.x - 4 * u, cab.y + 0.2f * u, cab.x - 1.4f * u, cab.y + 0.4f * u); close() }, e.acento)
        drawCircle(piel, 2 * u, cab)
        drawArc(e.acento, 180f, 180f, true, Offset(cab.x - 2.1f * u, cab.y - 2.3f * u), Size(4.2f * u, 3.4f * u))
        if (oscuro) drawCircle(borde, 2 * u, cab, style = Stroke(0.4f * u))
        // De noche, dos luciérnagas que la siguen.
        if (oscuro) listOf(Offset(-11f, -15f), Offset(-8f, -23f)).forEach { o ->
            val c = Offset(x + o.x * u, pie + o.y * u)
            drawCircle(LUCIERNAGA.copy(alpha = 0.22f), 2 * u, c); drawCircle(LUCIERNAGA, 0.6f * u, c)
        }
    }

    /**
     * La azotea recuperada: la losa de hormigón agrietada con su musgo, el huerto en su cajón, el árbol joven, la bandera de
     * tela verde remendada con su hoja y el semáforo de la esquina con la hiedra trepando y la luz verde encendida. De día,
     * dos pájaros; de noche, las luciérnagas y el resplandor verde del semáforo.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 54f * u, 44f * u)
        val x = b.x - 1 * u; val y = b.y
        val borde = if (oscuro) HORMIGON_LUZ.copy(alpha = 0.45f) else HORMIGON_OSC
        val verde = Color(0xFF8FCB6A)
        // La losa con sus grietas y el musgo en el borde.
        drawRect(t(HORMIGON, oscuro), Offset(x - 25 * u, y - 4 * u), Size(50 * u, 4 * u))
        drawRect(t(HORMIGON_LUZ, oscuro), Offset(x - 25 * u, y - 4 * u), Size(50 * u, 0.9f * u))
        drawPath(Path().apply { moveTo(x - 6 * u, y - 4 * u); lineTo(x - 4.6f * u, y - 2.4f * u); lineTo(x - 5.4f * u, y) ; moveTo(x + 14 * u, y - 4 * u); lineTo(x + 15.4f * u, y - 1.6f * u) }, t(HORMIGON_OSC, oscuro), style = Stroke(0.5f * u))
        listOf(-22f, -12f, 3f, 20f).forEachIndexed { k, dx -> drawOval(t(MUSGO, oscuro), Offset(x + dx * u, y - 4.8f * u), Size((3 + 2 * azar(23, k)) * u, 1.6f * u)) }
        drawRect(borde, Offset(x - 25 * u, y - 4 * u), Size(50 * u, 4 * u), style = Stroke(0.5f * u))
        // El cajón del huerto con sus brotes.
        drawRect(t(ESTANTE, oscuro), Offset(x - 21 * u, y - 8 * u), Size(22 * u, 4 * u))
        drawRect(t(VETA, oscuro), Offset(x - 21 * u, y - 8 * u), Size(22 * u, 0.9f * u))
        for (k in 0 until 5) {
            val bx = x - 18.5f * u + k * 4.4f * u
            drawLine(t(MUSGO_LUZ, oscuro), Offset(bx, y - 8 * u), Offset(bx - 1.2f * u, y - 10.6f * u), 0.8f * u, StrokeCap.Round)
            drawLine(t(MUSGO_LUZ, oscuro), Offset(bx, y - 8 * u), Offset(bx + 1.2f * u, y - 11 * u), 0.8f * u, StrokeCap.Round)
        }
        // El árbol joven.
        val tronco = Offset(x - 9 * u, y - 8 * u)
        drawLine(t(TALLO, oscuro), tronco, Offset(tronco.x, tronco.y - 15 * u), 1.8f * u)
        drawLine(t(TALLO, oscuro), Offset(tronco.x, tronco.y - 10 * u), Offset(tronco.x + 4 * u, tronco.y - 14 * u), 1f * u)
        drawCircle(t(HOJA, oscuro), 8 * u, Offset(tronco.x, tronco.y - 21 * u))
        drawCircle(t(MUSGO, oscuro), 5.5f * u, Offset(tronco.x + 4 * u, tronco.y - 18 * u))
        drawCircle(t(MUSGO_LUZ, oscuro), 4.6f * u, Offset(tronco.x - 3 * u, tronco.y - 24 * u))
        drawCircle(borde.copy(alpha = 0.5f), 8 * u, Offset(tronco.x, tronco.y - 21 * u), style = Stroke(0.5f * u))
        // La bandera verde remendada, con su hoja.
        val asta = x + 11 * u
        drawLine(if (oscuro) HORMIGON_LUZ else Color(0xFF2E332E), Offset(asta, y - 4 * u), Offset(asta, y - 40 * u), 1.1f * u)
        val ban = Path().apply { moveTo(asta, y - 40 * u); quadraticTo(asta + 7 * u, y - 42 * u, asta + 13 * u, y - 37.4f * u); lineTo(asta + 13 * u, y - 30 * u); quadraticTo(asta + 7 * u, y - 34 * u, asta, y - 31.4f * u); close() }
        drawPath(ban, t(Color(0xFF4F7A33), oscuro))
        drawRect(t(Color(0xFF6E8A4A), oscuro), Offset(asta + 8.6f * u, y - 37 * u), Size(3 * u, 3 * u))
        hoja(Offset(asta + 5 * u, y - 35.4f * u), 2.2f * u, 15f, t(YESO, oscuro))
        drawPath(ban, borde, style = Stroke(0.5f * u))
        // El semáforo con la hiedra y el verde encendido (de noche, con su resplandor, el único degradado).
        val sm = Rect(x + 17 * u, y - 18 * u, x + 23 * u, y - 4 * u)
        val luz = Offset(sm.center.x, sm.bottom - 3.4f * u)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(verde.copy(alpha = 0.45f), Color.Transparent), luz, 11 * u), 11 * u, luz)
        drawRect(t(Color(0xFF2E332E), oscuro), sm.topLeft, sm.size)
        drawRect(borde, sm.topLeft, sm.size, style = Stroke(0.5f * u))
        drawCircle(OXIDO.copy(alpha = 0.5f), 1.5f * u, Offset(sm.center.x, sm.top + 2.8f * u))
        drawCircle(Color(0xFFB8862B).copy(alpha = 0.35f), 1.5f * u, Offset(sm.center.x, sm.center.y))
        drawCircle(verde.copy(alpha = 0.35f), 3 * u, luz)
        drawCircle(if (oscuro) mezcla(verde, Color.White, 0.3f) else verde, 1.6f * u, luz)
        listOf(Offset(0.4f, 13f) to -20f, Offset(5.6f, 9f) to 40f, Offset(0.6f, 5f) to -10f, Offset(5.4f, 1.4f) to 30f).forEach { (o, g) -> hoja(Offset(sm.left + o.x * u, sm.top + o.y * u), 1.5f * u, g, t(HOJA, oscuro)) }
        // De día, dos pájaros; de noche, las luciérnagas.
        if (!oscuro) listOf(Offset(-2f, -38f), Offset(4f, -42f)).forEach { o ->
            val c = Offset(x + o.x * u, y + o.y * u)
            drawPath(Path().apply { moveTo(c.x - 2.2f * u, c.y - 0.6f * u); quadraticTo(c.x - 1 * u, c.y - 1.4f * u, c.x, c.y); quadraticTo(c.x + 1 * u, c.y - 1.4f * u, c.x + 2.2f * u, c.y - 0.6f * u) }, Color(0xFF2E332E).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        }
        else for (k in 0 until 8) {
            val c = Offset(x - 24 * u + azar(31, k) * 48 * u, y - 10 * u - azar(37, k) * 32 * u)
            drawCircle(LUCIERNAGA.copy(alpha = 0.2f), 1.8f * u, c); drawCircle(LUCIERNAGA, 0.55f * u, c)
        }
    }
}

/**
 * Espacio: los logros de la nave. El trofeo descansa en la plataforma de acoplamiento; en el hall, sobre el pedestal de
 * levitación del observatorio, y su placa es el visor de datos.
 */
object LogrosEspacio : DisenoLogros {
    private val PANEL = Color(0xFFD6DCE5)
    private val PANEL_OSC = Color(0xFF9AA4B4)
    private val PANEL_HONDO = Color(0xFF6C778A)
    private val CIAN = Color(0xFF5CE1FF)
    private val VERDE = Color(0xFF5CFFB0)
    private val AMBAR = Color(0xFFFFB84A)
    private val CRISTAL = Color(0xFF070C18)
    private val MARINO = Color(0xFF1B2F5C)

    /** El color en modo oscuro, con las luces de la nave bajas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF05070D), 0.62f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La plataforma de acoplamiento: la cubierta ochavada de paneles con la rendija de luz cian bajo su canto, el zócalo de paneles
     * con sus juntas y tornillos, las luces de estado, la franja de peligro ámbar y marina y las dos patas de aterrizaje; al
     * centro, el aro de anclaje en el metal del nivel.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cubierta = ochavado(fr(0.1f, 0.765f, 0.9f, 0.8f), h * 0.012f)
        val zocalo = poli(0.14f, 0.8f, 0.86f, 0.8f, 0.86f, 0.91f, 0.83f, 0.935f, 0.17f, 0.935f, 0.14f, 0.91f)
        val patas = listOf(poli(0.2f, 0.935f, 0.3f, 0.935f, 0.32f, 0.955f, 0.18f, 0.955f), poli(0.7f, 0.935f, 0.8f, 0.935f, 0.82f, 0.955f, 0.68f, 0.955f))
        if (!e.hecho) { drawPath(Path().apply { addPath(cubierta); addPath(zocalo); patas.forEach { addPath(it) } }, siluetaFuturo(oscuro)); return }
        sombraPie(0.1f, 0.9f, oscuro)
        patas.forEach { drawPath(it, t(PANEL_HONDO, oscuro)) }
        // El zócalo de paneles, con la cara de la derecha en sombra, sus juntas y sus tornillos.
        drawPath(zocalo, t(PANEL_OSC, oscuro))
        drawPath(poli(0.8f, 0.8f, 0.86f, 0.8f, 0.86f, 0.91f, 0.83f, 0.935f, 0.8f, 0.935f), Color.Black.copy(alpha = 0.16f))
        val junta = t(PANEL_HONDO, oscuro)
        listOf(0.33f, 0.67f).forEach { x -> drawLine(junta, pt(x, 0.81f), pt(x, 0.905f), lw * 0.8f) }
        if (fino) listOf(0.17f, 0.3f, 0.36f, 0.64f, 0.7f, 0.83f).forEach { x -> listOf(0.822f, 0.892f).forEach { y -> drawCircle(junta, h * 0.0035f, pt(x, y)) } }
        // Las luces de estado.
        if (fino) {
            listOf(0.2f to VERDE, 0.25f to VERDE, 0.75f to AMBAR, 0.8f to CIAN).forEach { (x, c) ->
                drawCircle(c.copy(alpha = 0.3f), h * 0.009f, pt(x, 0.857f)); drawCircle(c, h * 0.0045f, pt(x, 0.857f))
            }
        }
        // La franja de peligro.
        val franja = fr(0.15f, 0.905f, 0.85f, 0.925f)
        drawRect(t(MARINO, oscuro), franja.topLeft, franja.size)
        if (fino) {
            val paso = maxOf(w * 0.05f, 1f)
            val n = ((franja.width / paso).toInt() + 2).coerceAtMost(60)
            clipPath(Path().apply { addRect(franja) }) {
                for (k in 0 until n) {
                    val x = franja.left + k * paso
                    drawPath(Path().apply { moveTo(x, franja.bottom); lineTo(x + paso * 0.5f, franja.bottom); lineTo(x + paso * 0.5f + franja.height, franja.top); lineTo(x + franja.height, franja.top); close() }, AMBAR.copy(alpha = if (oscuro) 0.75f else 1f))
                }
            }
        } else drawRect(AMBAR, franja.topLeft, Size(franja.width, franja.height * 0.4f))
        // El aro de anclaje, en el metal del nivel.
        val c = pt(0.5f, 0.858f); val r = h * 0.032f
        drawCircle(e.base, r, c)
        drawCircle(e.brillo, r * 0.78f, c, style = Stroke(maxOf(r * 0.16f, 0.5f * u)))
        drawCircle(e.metalOscuro, r, c, style = Stroke(maxOf(r * 0.12f, 0.45f * u)))
        drawCircle(CRISTAL, r * 0.48f, c)
        drawCircle(CIAN, r * 0.2f, c)
        if (e.nivel >= 4 && fino) drawPath(estrella(Offset(c.x + r * 0.95f, c.y - r * 0.95f), r * 0.55f, 4, 0.25f), Color.White.copy(alpha = 0.9f))
        // La cubierta, su cara de luz y la rendija cian bajo el canto.
        drawPath(cubierta, t(PANEL, oscuro))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.45f), pt(0.12f, 0.767f), Size(w * 0.76f, h * 0.008f))
        drawRect(t(PANEL_OSC, oscuro), pt(0.11f, 0.792f), Size(w * 0.78f, h * 0.008f))
        drawRect(CIAN.copy(alpha = 0.35f), pt(0.16f, 0.799f), Size(w * 0.68f, h * 0.012f))
        drawRect(CIAN, pt(0.18f, 0.801f), Size(w * 0.64f, maxOf(h * 0.004f, 0.5f * u)))
        if (muyFino) listOf(0.3f, 0.5f, 0.7f).forEach { x -> drawLine(t(PANEL_OSC, oscuro), pt(x, 0.768f), pt(x, 0.792f), lw * 0.7f) }
    }

    /**
     * El pedestal de levitación del observatorio: la losa ochavada de paneles que flota sobre su base, con el haz cian entre las
     * dos, las luces de posición en los extremos y la franja del nivel al frente.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val losa = ochavado(fr(0.03f, 0f, 0.97f, 0.44f), h * 0.12f)
        val base = ochavado(fr(0.2f, 0.74f, 0.8f, 1f), h * 0.08f)
        if (!e.hecho) { drawPath(Path().apply { addPath(losa); addPath(base) }, siluetaFuturo(oscuro)); return }
        // La base y el haz.
        drawPath(base, t(PANEL_OSC, oscuro))
        drawRect(t(PANEL_HONDO, oscuro), pt(0.22f, 0.92f), Size(w * 0.56f, h * 0.06f))
        for (k in 0 until 4) drawRect(CIAN.copy(alpha = 0.32f - k * 0.07f), pt(0.24f - k * 0.02f, 0.5f + k * 0.06f), Size(w * (0.52f + k * 0.04f), h * 0.04f))
        drawRect(CIAN.copy(alpha = 0.6f), pt(0.24f, 0.7f), Size(w * 0.52f, h * 0.04f))
        // La losa, con su cara de luz, sus juntas y la franja del nivel.
        drawPath(losa, t(PANEL, oscuro))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.45f), pt(0.07f, 0.03f), Size(w * 0.86f, lw))
        drawRect(t(PANEL_OSC, oscuro), pt(0.04f, 0.36f), Size(w * 0.92f, h * 0.08f))
        listOf(0.25f, 0.75f).forEach { x -> drawLine(t(PANEL_OSC, oscuro), pt(x, 0.06f), pt(x, 0.36f), lw * 0.7f) }
        chapaNivel(fr(0.34f, 0.1f, 0.66f, 0.3f), e, h * 0.04f)
        listOf(0.09f to VERDE, 0.91f to AMBAR).forEach { (x, c) -> drawCircle(c.copy(alpha = 0.3f), h * 0.1f, pt(x, 0.2f)); drawCircle(c, h * 0.05f, pt(x, 0.2f)) }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFE8F3FF)
    override fun rotuloPlaca(oscuro: Boolean): Color = CIAN

    /**
     * El visor de datos: el cristal oscuro ochavado en su marco de panel, con el filo del nivel por fuera y los corchetes cian de
     * las cuatro esquinas.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val todo = Rect(0f, 0f, w, h)
        if (!e.hecho) { drawPath(ochavado(todo, 5 * u), siluetaFuturo(oscuro)); return }
        drawPath(ochavado(todo, 5 * u), e.base)
        drawLine(e.brillo, Offset(5 * u, 0.7f * u), Offset(w - 5 * u, 0.7f * u), 0.8f * u)
        drawPath(ochavado(todo, 5 * u), e.metalOscuro, style = Stroke(0.7f * u))
        val b1 = 1.4f * u
        drawPath(ochavado(Rect(b1, b1, w - b1, h - b1), 4.4f * u), if (oscuro) mezcla(PANEL, Color(0xFF05070D), 0.5f) else PANEL)
        val b2 = 3 * u
        drawPath(ochavado(Rect(b2, b2, w - b2, h - b2), 3.6f * u), CRISTAL)
        // Los corchetes de las esquinas.
        val a = 5 * u; val l = 4 * u
        listOf(Offset(a, a) to Offset(1f, 1f), Offset(w - a, a) to Offset(-1f, 1f), Offset(a, h - a) to Offset(1f, -1f), Offset(w - a, h - a) to Offset(-1f, -1f)).forEach { (o, s) ->
            drawPath(Path().apply { moveTo(o.x, o.y + s.y * l); lineTo(o.x, o.y); lineTo(o.x + s.x * l, o.y) }, CIAN.copy(alpha = 0.8f), style = Stroke(0.8f * u))
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /** El violeta de la nebulosa y el ocre del planeta que se ve por los ventanales. */
    private val NEBULOSA = Color(0xFF8A6BE0)
    private val PLANETA = Color(0xFFD08A4E)

    /**
     * La cubierta de observación de la nave. El casco es de paneles con sus juntas, sus rejillas y sus tornillos; detrás de
     * cada columna de trofeos se abre un ventanal ochavado al espacio, con su marco de panel atornillado, los corchetes cian
     * de las esquinas y la cuaderna que lo cruza en cada tramo de ≈ 230 dp; por ellos se ven las estrellas, la nebulosa y,
     * al pie de uno, el planeta con su anillo. Entre los ventanales van las cuadernas de la nave, con el haz de conductos, la
     * hilera de pilotos de estado y, en cada tramo, la consola con su pantalla de radar. Arriba, el techo con la tira de luz;
     * abajo, la cubierta de placas con las luces de guía. De día (las luces de la nave encendidas) el espacio apenas se ve
     * por el reflejo del cristal; en oscuro, con la nave en su turno de noche, las estrellas llenan los ventanales y solo
     * quedan los pilotos y las pantallas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaCubierta(g, e, oscuro) }
    }

    private fun DrawScope.escenaCubierta(g: GeoSalon, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        /** En oscuro, los paneles no pasan por [t] (se hundirían hasta el negro): se mezclan directo con el fondo. */
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        val panel = m(PANEL_HONDO, if (oscuro) 0.3f else 0.22f)
        val cian = if (oscuro) CIAN else Color(0xFF1B8FB8)

        // Los paneles del casco: las juntas, una rejilla de ventilación por aquí y por allá, y los tornillos de las esquinas.
        val juntas = Path(); val tornillos = Path()
        val anchoP = 44f * u; val altoP = 70f * u
        repetirY(g.friso, g.suelo, altoP, 30) { k, y ->
            juntas.moveTo(0f, y); juntas.lineTo(w, y)
            var x = if (k % 2 == 0) 0f else anchoP / 2; var j = 0
            while (x < w && j < 30) {
                juntas.moveTo(x, y); juntas.lineTo(x, minOf(y + altoP, g.suelo))
                tornillos.addOval(Rect(x + 2.5f * u, y + 2.5f * u, x + 4f * u, y + 4f * u))
                x += anchoP; j++
            }
        }
        drawPath(juntas, panel, style = Stroke(0.9f * u))
        drawPath(tornillos, panel)

        // Los ventanales al espacio.
        for (i in 0 until g.n) {
            val v = g.vano(i)
            val r = Rect(v.left + 4f * u, g.friso + 14f * u, v.right - 4f * u, g.suelo - 26f * u)
            if (r.height < 50f * u) continue
            val ch = minOf(r.width * 0.18f, 22f * u)
            val vent = ochavado(r, ch)
            drawPath(vent, if (oscuro) m(CRISTAL, 0.9f) else m(MARINO, 0.08f))
            clipPath(vent) {
                // La nebulosa (un degradado) en el primero, de noche.
                if (oscuro && i == 0) {
                    val cn = Offset(r.left + r.width * 0.65f, r.top + minOf(r.height * 0.3f, 160f * u))
                    drawCircle(Brush.radialGradient(listOf(NEBULOSA.copy(alpha = 0.22f), NEBULOSA.copy(alpha = 0f)), cn, r.width * 0.8f), r.width * 0.8f, cn)
                }
                // Las estrellas: muchas de noche, unas pocas de día (apenas, tras el reflejo).
                val estrellas = Path()
                val cuantas = ((r.height / (if (oscuro) 9f else 30f) / u).toInt()).coerceIn(4, 140)
                for (k in 0 until cuantas) {
                    val p = Offset(r.left + r.width * azar(e.semilla + i * 31, k), r.top + r.height * azar(e.semilla + i * 31, k + 500))
                    val s = (0.4f + 0.8f * azar(e.semilla + i * 31, k + 900)) * u
                    estrellas.addOval(Rect(p.x - s, p.y - s, p.x + s, p.y + s))
                }
                drawPath(estrellas, if (oscuro) Color.White.copy(alpha = 0.7f) else m(MARINO, 0.25f))
                if (oscuro) for (k in 0 until 4) {
                    val p = Offset(r.left + r.width * azar(e.semilla + i, k + 70), r.top + r.height * azar(e.semilla + i, k + 80))
                    drawLine(Color.White.copy(alpha = 0.5f), Offset(p.x - 3f * u, p.y), Offset(p.x + 3f * u, p.y), 0.6f * u)
                    drawLine(Color.White.copy(alpha = 0.5f), Offset(p.x, p.y - 3f * u), Offset(p.x, p.y + 3f * u), 0.6f * u)
                    drawCircle(Color.White, 1f * u, p)
                }
                // El planeta con su anillo, asomando al pie del último ventanal.
                if (i == g.n - 1) {
                    val pr = r.width * 0.55f; val pc = Offset(r.left + r.width * 0.3f, r.bottom + pr * 0.45f)
                    drawCircle(m(PLANETA, if (oscuro) 0.36f else 0.14f), pr, pc)
                    drawCircle(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.04f), pr, Offset(pc.x + pr * 0.25f, pc.y + pr * 0.1f))
                    for (k in 0 until 3) drawLine(m(PLANETA, if (oscuro) 0.25f else 0.08f), Offset(pc.x - pr, pc.y - pr * (0.35f - k * 0.15f)),
                        Offset(pc.x + pr, pc.y - pr * (0.4f - k * 0.15f)), 2f * u)
                    drawOval(m(PANEL, if (oscuro) 0.4f else 0.2f), Offset(pc.x - pr * 1.5f, pc.y - pr * 0.35f), Size(pr * 3f, pr * 0.42f), style = Stroke(2.4f * u))
                }
                // El reflejo de la sala en el cristal: dos franjas en diagonal.
                repetirY(r.top + 20f * u, r.bottom, g.modulo, 10) { _, y ->
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.45f), Offset(r.left + r.width * 0.1f, y + 50f * u), Offset(r.left + r.width * 0.35f, y), 6f * u)
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.3f), Offset(r.left + r.width * 0.2f, y + 54f * u), Offset(r.left + r.width * 0.42f, y + 10f * u), 2f * u)
                }
            }
            // El marco: el panel atornillado, el filo de luz, los corchetes cian y la cuaderna de cada tramo.
            drawPath(vent, m(PANEL_OSC, if (oscuro) 0.4f else 0.55f), style = Stroke(4f * u))
            drawPath(vent, m(PANEL, if (oscuro) 0.25f else 0.9f), style = Stroke(1f * u))
            val l = 8f * u; val b = 3f * u
            listOf(Offset(r.left + b, r.top + ch) to Offset(1f, 1f), Offset(r.right - b, r.top + ch) to Offset(-1f, 1f),
                Offset(r.left + b, r.bottom - ch) to Offset(1f, -1f), Offset(r.right - b, r.bottom - ch) to Offset(-1f, -1f)).forEach { (o, s) ->
                drawPath(Path().apply { moveTo(o.x, o.y + s.y * l); lineTo(o.x, o.y); lineTo(o.x + s.x * l * 0.6f, o.y - s.y * l * 0.6f) },
                    cian.copy(alpha = if (oscuro) 0.6f else 0.45f), style = Stroke(1.2f * u))
            }
            repetirY(r.top + g.modulo * 0.95f, r.bottom - 30f * u, g.modulo, 10) { _, y ->
                drawRect(m(PANEL_OSC, if (oscuro) 0.3f else 0.35f), Offset(r.left, y), Size(r.width, 3f * u))
                drawCircle(m(PANEL_HONDO, 0.5f), 1f * u, Offset(r.left + 6f * u, y + 1.5f * u))
                drawCircle(m(PANEL_HONDO, 0.5f), 1f * u, Offset(r.right - 6f * u, y + 1.5f * u))
            }
            // La placa con el número del ventanal, al pie.
            drawRect(m(PANEL_OSC, if (oscuro) 0.3f else 0.4f), Offset(g.cx(i) - 14f * u, r.bottom + 8f * u), Size(28f * u, 7f * u))
            for (k in 0 until 3) drawRect(cian.copy(alpha = if (oscuro) 0.5f else 0.45f), Offset(g.cx(i) - 9f * u + k * 6.5f * u, r.bottom + 10f * u), Size(4f * u, 3f * u))
        }

        // Las cuadernas entre los ventanales: la viga, el haz de conductos, los pilotos y la consola de cada tramo.
        val pilotos = listOf(VERDE, CIAN, AMBAR)
        for (i in 0..g.n) {
            val x = g.borde(i); val a = g.pil
            drawRect(m(PANEL_OSC, if (oscuro) 0.3f else 0.45f), Offset(x - a, g.friso), Size(2 * a, g.suelo - g.friso))
            drawRect(m(PANEL, if (oscuro) 0.2f else 0.85f), Offset(x - a, g.friso), Size(1.4f * u, g.suelo - g.friso))
            drawRect(m(PANEL_HONDO, if (oscuro) 0.4f else 0.45f), Offset(x + a - 1.6f * u, g.friso), Size(1.6f * u, g.suelo - g.friso))
            // El haz de conductos por el centro.
            listOf(-3f, 0f, 3f).forEachIndexed { j, dx ->
                drawLine((if (j == 1) m(MARINO, if (oscuro) 0.5f else 0.4f) else m(PANEL_HONDO, if (oscuro) 0.45f else 0.5f)),
                    Offset(x + dx * u, g.friso), Offset(x + dx * u, g.suelo), 2f * u)
            }
            repetirY(g.friso + 20f * u, g.suelo, 40f * u, 60) { _, y -> drawRect(m(PANEL_HONDO, if (oscuro) 0.5f else 0.6f), Offset(x - 6f * u, y), Size(12f * u, 2.4f * u)) }
            repetirY(g.friso + 30f * u, g.suelo - 70f * u, g.modulo, 10) { k, y0 ->
                // La consola: la caja, la pantalla de radar con su barrido y sus ecos, y la fila de botones.
                val cw = minOf(2 * a + 4f * u, 30f * u)
                val caja = Rect(x - cw / 2, y0, x + cw / 2, y0 + cw * 1.4f)
                drawRoundRect(m(PANEL, if (oscuro) 0.25f else 0.95f), caja.topLeft, caja.size, CornerRadius(3f * u))
                drawRoundRect(m(PANEL_HONDO, if (oscuro) 0.5f else 0.6f), caja.topLeft, caja.size, CornerRadius(3f * u), style = Stroke(1f * u))
                val rc = Offset(x, caja.top + cw * 0.5f); val rr = cw * 0.36f
                drawCircle(CRISTAL, rr, rc)
                drawCircle(cian.copy(alpha = 0.35f), rr * 0.6f, rc, style = Stroke(0.6f * u))
                drawCircle(cian.copy(alpha = 0.35f), rr, rc, style = Stroke(0.6f * u))
                val ang = 360f * azar(e.semilla + i, k + 40)
                drawArc(cian.copy(alpha = 0.3f), ang, 50f, true, Offset(rc.x - rr, rc.y - rr), Size(2 * rr, 2 * rr))
                drawCircle(VERDE, 0.9f * u, Offset(rc.x + rr * 0.4f, rc.y - rr * 0.3f))
                for (j in 0 until 3) {
                    val bc = Offset(caja.left + cw * (0.25f + j * 0.25f), caja.bottom - cw * 0.2f)
                    drawCircle(pilotos[(i + j + k) % 3].copy(alpha = if (oscuro) 0.85f else 0.75f), 1.4f * u, bc)
                }
                // La hilera de pilotos de estado debajo de la consola, con su halo de noche.
                repetirY(caja.bottom + 14f * u, minOf(caja.bottom + 90f * u, g.suelo - 30f * u), 9f * u, 10) { j, yp ->
                    val col = pilotos[(i + j) % 3]
                    if (oscuro) drawCircle(col.copy(alpha = 0.18f), 3.4f * u, Offset(x + a * 0.55f, yp))
                    drawCircle(if (azar(e.semilla + i * 7 + k, j) > 0.3f) col.copy(alpha = if (oscuro) 0.9f else 0.7f) else m(PANEL_HONDO, 0.5f), 1.2f * u, Offset(x + a * 0.55f, yp))
                }
            }
        }

        // El techo: la tira de luz sobre cada ventanal (un degradado de su resplandor de día) y el canto del casco.
        drawRect(m(PANEL_OSC, if (oscuro) 0.35f else 0.5f), Offset.Zero, Size(w, g.friso))
        drawRect(m(PANEL_HONDO, if (oscuro) 0.5f else 0.5f), Offset(0f, g.friso - 2f * u), Size(w, 2f * u))
        val tiras = Path()
        for (i in 0 until g.n) { val c = g.cx(i); val tw = minOf(g.paso * 0.55f, 70f * u); tiras.addRoundRect(RoundRect(Rect(c - tw / 2, g.friso * 0.3f, c + tw / 2, g.friso * 0.62f), CornerRadius(2f * u))) }
        drawPath(tiras, if (oscuro) cian.copy(alpha = 0.35f) else Color.White)
        if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0f)), g.friso, g.friso + 40f * u),
            Offset(0f, g.friso), Size(w, 40f * u))
        var xr = 8f * u; var kr = 0
        while (xr < w && kr < 60) { drawCircle(m(PANEL_HONDO, 0.6f), 1f * u, Offset(xr, g.friso * 0.15f)); xr += 22f * u; kr++ }

        // La cubierta: las placas, sus juntas y las luces de guía.
        drawRect(m(PANEL_OSC, if (oscuro) 0.3f else 0.45f), Offset(0f, g.suelo), Size(w, h - g.suelo))
        drawRect(m(PANEL, if (oscuro) 0.2f else 0.9f), Offset(0f, g.suelo), Size(w, 1.2f * u))
        val placas = Path()
        var xp = 0f; var kp = 0
        while (xp < w && kp < 40) { placas.moveTo(xp, g.suelo); placas.lineTo(xp, h); xp += 36f * u; kp++ }
        drawPath(placas, m(PANEL_HONDO, 0.5f), style = Stroke(0.8f * u))
        var xg = 18f * u; var kg = 0
        val yg = g.suelo + (h - g.suelo) * 0.55f
        while (xg < w && kg < 60) {
            val col = if (kg % 4 == 0) AMBAR else cian
            if (oscuro) drawCircle(col.copy(alpha = 0.2f), 3.5f * u, Offset(xg, yg))
            drawCircle(col.copy(alpha = if (oscuro) 0.9f else 0.6f), 1.3f * u, Offset(xg, yg))
            xg += 18f * u; kg++
        }
    }

    // ---------------------------------------------------------------- las campañas

    /** El tono de la nave y la escena de la meta: de noche, apagado un poco (menos que [t], para que se lean sobre el mapa). */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, CRISTAL, 0.2f) else c

    /**
     * La nave, de pie sobre sus aletas: el fuselaje de paneles con su filo de luz, la nariz del color de acento, la franja
     * azul marino, el ojo de buey cian (de noche, encendido), las tres aletas y la tobera. Cuando brinca, enciende el motor:
     * la llama ámbar crece con el salto.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val x = e.punto.x
        sombraMarcadorFuturo(e.punto, 13f * u, s, oscuro)
        val pie = e.punto.y - s * 3f * u
        val borde = if (oscuro) PANEL.copy(alpha = 0.6f) else PANEL_HONDO
        // La llama, si brinca.
        if (s > 0.02f) {
            val l = (2.5f + 5 * s) * u
            drawPath(Path().apply { moveTo(x - 2.2f * u, pie - 3 * u); quadraticTo(x - 1.6f * u, pie - 3 * u + l * 0.7f, x, pie - 3 * u + l); quadraticTo(x + 1.6f * u, pie - 3 * u + l * 0.7f, x + 2.2f * u, pie - 3 * u); close() }, AMBAR.copy(alpha = 0.85f))
            drawPath(Path().apply { moveTo(x - 1 * u, pie - 3 * u); quadraticTo(x - 0.6f * u, pie - 3 * u + l * 0.45f, x, pie - 3 * u + l * 0.6f); quadraticTo(x + 0.6f * u, pie - 3 * u + l * 0.45f, x + 1 * u, pie - 3 * u); close() }, Color.White.copy(alpha = 0.85f))
        }
        // Las aletas laterales y la tobera.
        listOf(-1f, 1f).forEach { k ->
            val aleta = Path().apply { moveTo(x + k * 3.4f * u, pie - 11 * u); quadraticTo(x + k * 7.4f * u, pie - 7 * u, x + k * 7 * u, pie); lineTo(x + k * 3.4f * u, pie - 4.6f * u); close() }
            drawPath(aleta, n(MARINO, oscuro)); drawPath(aleta, borde, style = Stroke(0.45f * u, join = StrokeJoin.Round))
        }
        drawPath(Path().apply { moveTo(x - 2 * u, pie - 5 * u); lineTo(x + 2 * u, pie - 5 * u); lineTo(x + 2.6f * u, pie - 2.6f * u); lineTo(x - 2.6f * u, pie - 2.6f * u); close() }, n(PANEL_HONDO, oscuro))
        // El fuselaje: la ojiva, el filo de luz y la franja.
        val cuerpo = Path().apply {
            moveTo(x, pie - 25 * u); cubicTo(x + 3 * u, pie - 22 * u, x + 4.2f * u, pie - 16 * u, x + 3.8f * u, pie - 5 * u)
            lineTo(x - 3.8f * u, pie - 5 * u); cubicTo(x - 4.2f * u, pie - 16 * u, x - 3 * u, pie - 22 * u, x, pie - 25 * u); close()
        }
        drawPath(cuerpo, n(PANEL, oscuro))
        clipPath(cuerpo) {
            drawRect(n(PANEL_OSC, oscuro), Offset(x + 1.6f * u, pie - 26 * u), Size(3 * u, 22 * u))
            drawRect(e.acento, Offset(x - 5 * u, pie - 26 * u), Size(10 * u, 5.4f * u))
            drawRect(n(MARINO, oscuro), Offset(x - 5 * u, pie - 9.4f * u), Size(10 * u, 1.8f * u))
            drawLine(n(PANEL_HONDO, oscuro).copy(alpha = 0.6f), Offset(x - 5 * u, pie - 20.6f * u), Offset(x + 5 * u, pie - 20.6f * u), 0.4f * u)
            drawLine(Color.White.copy(alpha = 0.7f), Offset(x - 2.2f * u, pie - 19 * u), Offset(x - 2.6f * u, pie - 11 * u), 0.7f * u)
        }
        drawPath(cuerpo, borde, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        // La aleta del frente y el ojo de buey.
        drawLine(n(MARINO, oscuro), Offset(x, pie - 9 * u), Offset(x, pie - 0.6f * u), 1.3f * u)
        val ojo = Offset(x, pie - 15 * u)
        if (oscuro) { drawCircle(CIAN.copy(alpha = 0.14f), 5.4f * u, ojo); drawCircle(CIAN.copy(alpha = 0.26f), 3 * u, ojo) }
        drawCircle(CRISTAL, 1.8f * u, ojo)
        drawCircle(if (oscuro) CIAN else mezcla(CIAN, CRISTAL, 0.3f), 1.1f * u, ojo)
        drawCircle(Color.White.copy(alpha = 0.8f), 0.4f * u, ojo - Offset(0.4f * u, 0.4f * u))
        drawCircle(n(PANEL_HONDO, oscuro), 1.8f * u, ojo, style = Stroke(0.5f * u))
    }

    /**
     * La llegada a Marte: la orilla curva del planeta con sus cráteres y rocas, el invernadero de cúpula de cristal con sus
     * matas verdes, el módulo de aterrizaje de lámina dorada con su cabina, su antena y sus patas, las huellas hasta la bandera
     * azul marino de la misión (con su estrella y su franja ámbar, tiesa, porque allá no hay viento) y la Tierra a lo lejos. De
     * noche, la cúpula brilla, la cabina se enciende y salen las estrellas.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaFuturo(e.meta, 58f * u, 46f * u)
        val x = b.x; val y = b.y
        val borde = if (oscuro) PANEL.copy(alpha = 0.5f) else PANEL_HONDO
        val marte = n(Color(0xFFC1440E), oscuro); val marteOsc = n(Color(0xFF8A2E0E), oscuro)
        if (oscuro) for (k in 0 until 9) drawCircle(Color.White.copy(alpha = 0.4f + 0.5f * azar(43, k)), (0.4f + 0.6f * azar(47, k)) * u, Offset(x - 27 * u + azar(53, k) * 54 * u, y - 46 * u + azar(59, k) * 26 * u))
        // La Tierra a lo lejos, con su Luna.
        val tierra = Offset(x - 21 * u, y - 38 * u)
        drawCircle(n(Color(0xFF4F86B8), oscuro), 2.6f * u, tierra)
        drawCircle(n(Color(0xFF6B8A3A), oscuro), 1 * u, tierra + Offset(-0.7f * u, -0.6f * u))
        drawCircle(n(Color(0xFFB0B6C2), oscuro), 0.8f * u, tierra + Offset(4.2f * u, -1.6f * u))
        // El suelo de Marte, curvo, con cráteres y rocas.
        val suelo = Path().apply { moveTo(x - 28 * u, y); lineTo(x - 28 * u, y - 4 * u); quadraticTo(x, y - 10 * u, x + 28 * u, y - 4 * u); lineTo(x + 28 * u, y); close() }
        drawPath(suelo, marte)
        drawPath(Path().apply { moveTo(x - 28 * u, y - 4 * u); quadraticTo(x, y - 10 * u, x + 28 * u, y - 4 * u) }, mezcla(marte, Color.White, 0.25f), style = Stroke(0.7f * u))
        listOf(Offset(-20f, -3.4f) to 2.6f, Offset(2f, -2.2f) to 1.8f, Offset(22f, -1.6f) to 1.4f).forEach { (o, r) ->
            drawOval(marteOsc, Offset(x + o.x * u - r * u, y + o.y * u - r * 0.35f * u), Size(2 * r * u, r * 0.7f * u))
        }
        listOf(Offset(-9f, -6.4f), Offset(14f, -5.2f)).forEach { o -> drawCircle(marteOsc, 0.9f * u, Offset(x + o.x * u, y + o.y * u)) }
        // El invernadero: la cúpula de cristal con sus matas (de noche, su resplandor: el único degradado).
        val cu = Offset(x - 16 * u, y - 6.4f * u); val R = 7.6f * u
        if (oscuro) drawCircle(Brush.radialGradient(listOf(VERDE.copy(alpha = 0.32f), Color.Transparent), cu, 15 * u), 15 * u, cu)
        drawRect(n(PANEL_OSC, oscuro), Offset(cu.x - R - 0.6f * u, cu.y - 0.4f * u), Size(2 * R + 1.2f * u, 1.6f * u))
        drawArc(if (oscuro) CRISTAL.copy(alpha = 0.85f) else Color(0xFFB8D8F0).copy(alpha = 0.7f), 180f, 180f, true, Offset(cu.x - R, cu.y - R), Size(2 * R, 2 * R))
        listOf(-4f, -0.6f, 3.4f).forEachIndexed { k, dx -> drawCircle(if (oscuro) VERDE.copy(alpha = 0.85f) else n(Color(0xFF3F8A4A), oscuro), (1.6f + 0.5f * k % 2) * u, Offset(cu.x + dx * u, cu.y - 1.2f * u)) }
        for (k in 1 until 4) { val a = Math.PI * (1.0 + k / 4.0); drawLine(n(PANEL, oscuro).copy(alpha = 0.6f), cu, Offset(cu.x + (R * kotlin.math.cos(a)).toFloat(), cu.y + (R * kotlin.math.sin(a)).toFloat()), 0.4f * u) }
        drawArc(n(PANEL, oscuro), 180f, 180f, false, Offset(cu.x - R, cu.y - R), Size(2 * R, 2 * R), style = Stroke(0.9f * u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(cu.x - R * 0.6f, cu.y - R * 0.5f), Offset(cu.x - R * 0.3f, cu.y - R * 0.8f), 0.6f * u, StrokeCap.Round)
        // El módulo de aterrizaje: las patas, el cuerpo dorado, la cabina con su ventana y la antena.
        val lx = x + 3 * u; val ly = y - 8.4f * u
        listOf(-1f, 1f).forEach { k ->
            drawLine(n(PANEL_OSC, oscuro), Offset(lx + k * 4 * u, ly - 3 * u), Offset(lx + k * 8 * u, ly + 2.6f * u), 0.8f * u)
            drawLine(n(PANEL_OSC, oscuro), Offset(lx + k * 8.8f * u, ly + 2.8f * u), Offset(lx + k * 7.2f * u, ly + 2.8f * u), 0.9f * u, StrokeCap.Round)
        }
        drawPath(Path().apply { moveTo(lx - 1.6f * u, ly - 2 * u); lineTo(lx + 1.6f * u, ly - 2 * u); lineTo(lx + 2.2f * u, ly + 0.4f * u); lineTo(lx - 2.2f * u, ly + 0.4f * u); close() }, n(PANEL_HONDO, oscuro))
        val oro = n(Color(0xFFC9A04A), oscuro)
        drawRect(oro, Offset(lx - 5.6f * u, ly - 8 * u), Size(11.2f * u, 6 * u))
        for (k in 0 until 3) drawLine(mezcla(oro, Color.White, 0.35f).copy(alpha = 0.7f), Offset(lx - 5 * u + k * 3.6f * u, ly - 7.6f * u), Offset(lx - 4 * u + k * 3.6f * u, ly - 2.4f * u), 0.4f * u)
        drawRect(borde, Offset(lx - 5.6f * u, ly - 8 * u), Size(11.2f * u, 6 * u), style = Stroke(0.45f * u))
        val cabina = Path().apply { moveTo(lx - 4.6f * u, ly - 8 * u); lineTo(lx + 4.6f * u, ly - 8 * u); lineTo(lx + 3 * u, ly - 14 * u); lineTo(lx - 3 * u, ly - 14 * u); close() }
        drawPath(cabina, n(PANEL, oscuro))
        drawPath(cabina, borde, style = Stroke(0.45f * u, join = StrokeJoin.Round))
        val ventana = Offset(lx + 0.6f * u, ly - 11 * u)
        if (oscuro) drawCircle(CIAN.copy(alpha = 0.25f), 3.4f * u, ventana)
        drawRoundRect(if (oscuro) CIAN else mezcla(CIAN, CRISTAL, 0.35f), Offset(ventana.x - 1.8f * u, ventana.y - 1.2f * u), Size(3.6f * u, 2.4f * u), CornerRadius(0.8f * u))
        drawLine(n(PANEL_OSC, oscuro), Offset(lx - 2 * u, ly - 14 * u), Offset(lx - 3 * u, ly - 18 * u), 0.5f * u)
        drawArc(n(PANEL, oscuro), 200f, 140f, false, Offset(lx - 5.4f * u, ly - 20 * u), Size(4.8f * u, 3.4f * u), style = Stroke(0.8f * u))
        drawCircle(AMBAR, 0.6f * u, Offset(lx + 3.4f * u, ly - 14 * u))
        if (oscuro) drawCircle(AMBAR.copy(alpha = 0.3f), 1.8f * u, Offset(lx + 3.4f * u, ly - 14 * u))
        // Las huellas hasta la bandera tiesa de la misión.
        for (k in 0 until 4) drawOval(marteOsc, Offset(lx + (8.4f + k * 2.6f) * u, y - (6.6f + (k % 2) * 0.8f) * u), Size(1.4f * u, 0.7f * u))
        val asta = x + 21 * u
        drawLine(if (oscuro) PANEL.copy(alpha = 0.8f) else PANEL_HONDO, Offset(asta, y - 6 * u), Offset(asta, y - 32 * u), 0.8f * u)
        drawLine(if (oscuro) PANEL.copy(alpha = 0.8f) else PANEL_HONDO, Offset(asta, y - 32 * u), Offset(asta + 8.6f * u, y - 32 * u), 0.6f * u)
        drawRect(n(MARINO, oscuro), Offset(asta, y - 32 * u), Size(8.6f * u, 5.6f * u))
        drawRect(AMBAR, Offset(asta, y - 27.6f * u), Size(8.6f * u, 1.2f * u))
        drawPath(estrella(Offset(asta + 4.3f * u, y - 29.8f * u), 1.6f * u, 5, 0.42f), Color.White)
        drawRect(borde, Offset(asta, y - 32 * u), Size(8.6f * u, 5.6f * u), style = Stroke(0.4f * u))
    }
}
