package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Las piezas comunes de la Edad Media y la Edad Moderna
// =====================================================================

/**
 * Medieval: las piezas comunes de la librería encadenada. El campo es una hoja de pergamino clavada en un marco de roble,
 * como la tira del buscador: pautada a punta de plomo, con su línea de margen y los piquetes del pautado.
 */
object ComunMedieval : DisenoComun {
    private val ROBLE = Color(0xFF6E4A2A)
    private val VETA = Color(0xFF2B1B0E)
    private val HIERRO = Color(0xFF2B2A2D)
    private val PIEDRA_OSC = Color(0xFF8A806E)
    private val PERGAMINO = Color(0xFFEADFBF)
    private val TINTA = Color(0xFF2A1B10)
    private val BERMELLON = Color(0xFFA5321F)
    private val LAPIZ = Color(0xFF2E4C88)
    private val ORO = Color(0xFFDDBE76)
    private val LATON = Color(0xFFB8924C)
    private val CERA = Color(0xFFEDE3C4)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c
    /** El pergamino del campo: en modo oscuro se hunde de verdad, para leerse con la cera clara. */
    private fun pergaminoHondo(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color(0xFF17110B), 0.74f) else PERGAMINO

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) CERA else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), pergaminoHondo(oscuro), 0.45f)
    /** El azul de lapislázuli de las iniciales. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9DB4E6) else LAPIZ
    /** El bermellón de las rúbricas. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE88A6E) else BERMELLON

    /**
     * El marco: un bastidor de roble con su veta y, clavada con cuatro clavos de hierro, la hoja de pergamino pautada a punta
     * de plomo (las dos rayas de caja, la línea del margen y los piquetes a la derecha; con varios renglones, la pauta de
     * cada uno). Con el foco la pauta se vuelve de oro, la hoja se filetea y los clavos son de latón; con un error, la pauta y
     * el filete van en bermellón.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val tabla = Rect(0f, 0f, size.width - 1.2f * u, size.height - 1.6f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(1.2f * u, 1.6f * u), tabla.size, CornerRadius(3 * u))
        drawRoundRect(c(ROBLE), Offset.Zero, tabla.size, CornerRadius(3 * u))
        vetas(tabla, c(VETA).copy(alpha = 0.4f), 73 + (e.semilla and 7), densidad = 0.7f, grosor = 0.7f)
        bisel(tabla, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.3f), 1.2f * u)
        // La hoja, con el rebaje del bastidor alrededor.
        val hoja = Rect(5 * u, 5 * u, tabla.right - 5 * u, tabla.bottom - 5 * u)
        drawRect(Color.Black.copy(alpha = 0.32f), Offset(hoja.left - 0.7f * u, hoja.top - 0.7f * u), Size(hoja.width + 1.4f * u, hoja.height + 1.4f * u))
        drawRect(apagadoMedievo(pergaminoHondo(oscuro), e, oscuro, 0.18f), hoja.topLeft, hoja.size)
        drawRect(Color.Black.copy(alpha = 0.1f), hoja.topLeft, Size(hoja.width, 1.4f * u))
        drawRect(Color.Black.copy(alpha = 0.07f), hoja.topLeft, Size(1.4f * u, hoja.height))
        // La pauta: de plomo, de oro con el foco, de bermellón con un error.
        val marcado = e.error || e.enfocado
        val pauta = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> if (oscuro) ORO else mezcla(ORO, TINTA, 0.35f)
            else -> if (oscuro) c(CERA).copy(alpha = 0.24f) else c(PIEDRA_OSC).copy(alpha = 0.6f)
        }
        val grueso = (if (marcado) 1f else 0.6f) * u
        val ys = mutableListOf(hoja.top + 3 * u, hoja.bottom - 3 * u)
        ys.forEach { y -> drawLine(pauta, Offset(hoja.left + 3 * u, y), Offset(hoja.right - 3 * u, y), grueso) }
        drawLine(pauta, Offset(hoja.left + 6 * u, hoja.top + 1.5f * u), Offset(hoja.left + 6 * u, hoja.bottom - 1.5f * u), 0.6f * u)
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < hoja.bottom - 5 * u && k < 200) {
                drawLine(pauta.copy(alpha = pauta.alpha * 0.55f), Offset(hoja.left + 6 * u, y), Offset(hoja.right - 3 * u, y), 0.5f * u)
                ys.add(y); y += paso; k++
            }
        }
        // Los piquetes del pautado, en el margen derecho.
        ys.forEach { y -> drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.35f), 0.6f * u, Offset(hoja.right - 1.6f * u, y)) }
        if (marcado) drawRect(pauta, hoja.topLeft, hoja.size, style = Stroke((if (e.error) 1.4f else 1.2f) * u))
        // Los cuatro clavos.
        val clavo = if (e.enfocado && !e.error) c(LATON) else c(HIERRO)
        listOf(Offset(2.5f * u, 2.5f * u), Offset(tabla.right - 2.5f * u, 2.5f * u), Offset(2.5f * u, tabla.bottom - 2.5f * u),
            Offset(tabla.right - 2.5f * u, tabla.bottom - 2.5f * u)).forEach { o ->
            drawCircle(Color.Black.copy(alpha = 0.35f), 1.6f * u, o + Offset(0.4f * u, 0.5f * u))
            drawCircle(clavo, 1.4f * u, o)
            drawCircle(Color.White.copy(alpha = 0.3f), 0.5f * u, o - Offset(0.45f * u, 0.45f * u))
        }
    }
}

/**
 * Templario: las piezas comunes de la encomienda. El campo es un sillar labrado con la caja rehundida donde va la
 * inscripción, como la lápida del buscador, y una cruz patada labrada en cada esquina.
 */
object ComunTemplario : DisenoComun {
    private val SILLAR = Color(0xFFC4B79C)
    private val ROJO = Color(0xFFB0241E)
    private val TINTA = Color(0xFF231A12)
    private val CREMA = Color(0xFFEDE3CB)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0D0B), 0.36f) else c
    /** La piedra del campo: en modo oscuro se hunde de verdad, para leerse con la crema. */
    private fun piedraHonda(oscuro: Boolean): Color = if (oscuro) mezcla(SILLAR, Color(0xFF0F0D0B), 0.74f) else SILLAR

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), piedraHonda(oscuro), 0.45f)
    /** El rojo de la cruz, oscurecido como el lacre para leerse sobre la piedra. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFEC8E7E) else Color(0xFF7E1612)
    /** El óxido del hierro: anaranjado, para no confundirse con el rojo de la cruz. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0A050) else Color(0xFF7A3404)

    /**
     * El marco: un sillar con su bisel y las picaduras del trinchante en el borde, la caja rehundida en el centro (lisa,
     * con la sombra arriba y la luz abajo, sin degradados) y una cruz patada labrada en cada esquina. Con el foco la caja se
     * filetea y las cruces se pintan de rojo; con un error, de óxido. Con varios renglones, unas rayas incisas.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val bloque = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        val piedra = apagadoMedievo(piedraHonda(oscuro), e, oscuro, 0.2f)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(1.5f * u, 2 * u), bloque.size)
        drawRect(piedra, bloque.topLeft, bloque.size)
        bisel(bloque, Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), Color.Black.copy(alpha = 0.3f), 2.4f * u)
        // Las picaduras de la labra, solo en el borde.
        val n = ((bloque.width + bloque.height) / (9 * u)).toInt().coerceIn(8, 90)
        for (j in 0 until n) {
            val lado = (azar(e.semilla, j * 3) * 4).toInt()
            val a = azar(e.semilla, j * 3 + 1)
            val hondo = (1.4f + 4f * azar(e.semilla, j * 3 + 2)) * u
            val p = when (lado) {
                0 -> Offset(bloque.width * a, hondo)
                1 -> Offset(bloque.right - hondo, bloque.height * a)
                2 -> Offset(bloque.width * a, bloque.bottom - hondo)
                else -> Offset(hondo, bloque.height * a)
            }
            drawCircle(Color.Black.copy(alpha = 0.12f), (0.5f + 0.7f * azar(e.semilla, j + 300)) * u, p)
        }
        // La caja rehundida.
        val caja = Rect(8 * u, 8 * u, bloque.right - 8 * u, bloque.bottom - 8 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), caja.topLeft, Size(caja.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.18f), caja.topLeft, Size(1.8f * u, caja.height))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), Offset(caja.left, caja.bottom - 1.2f * u), Size(caja.width, 1.2f * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), Offset(caja.right - 1 * u, caja.top), Size(1 * u, caja.height))
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < caja.bottom - 3 * u && k < 200) {
                drawLine(Color.Black.copy(alpha = 0.12f), Offset(caja.left + 4 * u, y), Offset(caja.right - 4 * u, y), 0.6f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), Offset(caja.left + 4 * u, y + 0.7f * u), Offset(caja.right - 4 * u, y + 0.7f * u), 0.5f * u)
                y += paso; k++
            }
        }
        val rojo = apagadoMedievo(if (oscuro) mezcla(ROJO, Color.White, 0.18f) else ROJO, e, oscuro)
        val marca = if (e.error) errorCampo(oscuro) else rojo
        if (e.enfocado || e.error) drawRect(marca, caja.topLeft, caja.size, style = Stroke((if (e.error) 1.6f else 1.2f) * u))
        // Las cruces de las esquinas: labradas, o pintadas con el foco o el error.
        listOf(Offset(4.2f * u, 4.2f * u), Offset(bloque.right - 4.2f * u, 4.2f * u), Offset(4.2f * u, bloque.bottom - 4.2f * u),
            Offset(bloque.right - 4.2f * u, bloque.bottom - 4.2f * u)).forEach { o ->
            val cruz = cruzPatada(o, 3.6f * u)
            if (e.enfocado || e.error) {
                drawPath(cruz, marca)
            } else {
                drawPath(cruzPatada(o + Offset(0.5f * u, 0.5f * u), 3.6f * u), Color.White.copy(alpha = if (oscuro) 0.07f else 0.32f))
                drawPath(cruz, Color.Black.copy(alpha = 0.3f))
            }
        }
    }
}

/**
 * Seda: las piezas comunes del caravasar. El campo es una hoja de papel en un marco de laca negra con filete de oro, como
 * el buscador, con una greca de oro en cada esquina.
 */
object ComunSeda : DisenoComun {
    private val LACA = Color(0xFFA8261C)
    private val NEGRO_LACA = Color(0xFF1E1412)
    private val ORO = Color(0xFFD2AE5A)
    private val PAPEL = Color(0xFFF1E6CC)
    private val JADE = Color(0xFF6E9C80)
    private val MARFIL = Color(0xFFEFE6CF)
    private val TINTA = Color(0xFF1E1A18)
    private val BERMELLON = Color(0xFFB8322A)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110B09), 0.34f) else c
    /** El papel del campo: en modo oscuro se hunde de verdad, para leerse con tinta de marfil. */
    private fun papelHondo(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF110D0C), 0.74f) else PAPEL

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) MARFIL else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), papelHondo(oscuro), 0.45f)
    /** El verde de jade. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CCDB0) else Color(0xFF2F5E48)
    /** El bermellón del sello. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08070) else BERMELLON

    /** Una greca (la espiral cuadrada) en la esquina [o], abierta hacia [sx], [sy], de lado [s]. */
    private fun DrawScope.greca(o: Offset, sx: Float, sy: Float, s: Float, color: Color, grosor: Float) {
        val puntos = listOf(0f to 1f, 0f to 0f, 1f to 0f, 1f to 0.75f, 0.25f to 0.75f, 0.25f to 0.25f, 0.75f to 0.25f, 0.75f to 0.5f, 0.5f to 0.5f)
        val p = Path()
        puntos.forEachIndexed { k, (x, y) ->
            val px = o.x + sx * x * s; val py = o.y + sy * y * s
            if (k == 0) p.moveTo(px, py) else p.lineTo(px, py)
        }
        drawPath(p, color, style = Stroke(grosor))
    }

    /**
     * El marco: laca negra con su brillo y un hilo de laca roja, la hoja de papel lisa con el filete de oro alrededor y una
     * greca de oro en cada esquina. Con el foco el oro se aviva, el filete engruesa y se dobla; con un error, filete y grecas
     * van en bermellón. Con varios renglones, la pauta roja tenue del papel de cartas.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val marco = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1.5f * u, 2 * u), marco.size, CornerRadius(4 * u))
        drawRoundRect(c(NEGRO_LACA), Offset.Zero, marco.size, CornerRadius(4 * u))
        drawRoundRect(Color.White.copy(alpha = 0.09f), Offset(3 * u, 1.2f * u), Size(marco.width - 6 * u, 1.4f * u), CornerRadius(0.7f * u))
        drawRoundRect(c(LACA).copy(alpha = 0.85f), Offset(2.4f * u, 2.4f * u), Size(marco.width - 4.8f * u, marco.height - 4.8f * u), CornerRadius(3 * u), style = Stroke(0.8f * u))
        val hoja = Rect(6 * u, 6 * u, marco.right - 6 * u, marco.bottom - 6 * u)
        drawRect(apagadoMedievo(papelHondo(oscuro), e, oscuro, 0.18f), hoja.topLeft, hoja.size)
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < hoja.bottom - 4 * u && k < 200) {
                drawLine(c(LACA).copy(alpha = if (oscuro) 0.3f else 0.18f), Offset(hoja.left + 3 * u, y), Offset(hoja.right - 3 * u, y), 0.6f * u)
                y += paso; k++
            }
        }
        val oro = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> apagadoMedievo(mezcla(ORO, Color.White, 0.22f), e, oscuro)
            else -> c(ORO).copy(alpha = 0.72f)
        }
        val marcado = e.error || e.enfocado
        drawRect(oro, Offset(hoja.left - 1.4f * u, hoja.top - 1.4f * u), Size(hoja.width + 2.8f * u, hoja.height + 2.8f * u), style = Stroke((if (marcado) 1.5f else 0.7f) * u))
        if (marcado) drawRect(oro.copy(alpha = 0.6f), Offset(hoja.left + 1.4f * u, hoja.top + 1.4f * u), Size(hoja.width - 2.8f * u, hoja.height - 2.8f * u), style = Stroke(0.6f * u))
        // Las grecas de las esquinas, dentro del papel. En el claro, el oro de la greca se oscurece para verse sobre el papel.
        val tinte = if (e.error || oscuro) oro else mezcla(oro, TINTA, 0.25f).copy(alpha = oro.alpha)
        val s = 7 * u; val d = 2.4f * u
        greca(Offset(hoja.left + d, hoja.top + d), 1f, 1f, s, tinte, 0.8f * u)
        greca(Offset(hoja.right - d, hoja.top + d), -1f, 1f, s, tinte, 0.8f * u)
        greca(Offset(hoja.left + d, hoja.bottom - d), 1f, -1f, s, tinte, 0.8f * u)
        greca(Offset(hoja.right - d, hoja.bottom - d), -1f, -1f, s, tinte, 0.8f * u)
        // Con el foco, la cuenta de jade colgada del medio del marco.
        if (e.enfocado && !e.error) {
            val o = Offset(marco.right - 3 * u, marco.center.y)
            drawCircle(c(JADE), 1.9f * u, o)
            drawCircle(c(NEGRO_LACA), 0.7f * u, o)
        }
    }
}

/**
 * Gótico: las piezas comunes de la librería del coro. El campo es una lauda: la losa oscura, la orla de latón grabado con
 * un cuadrifolio en cada esquina (como las de los evangelistas en las laudas) y la vitela lisa en medio.
 */
object ComunGotico : DisenoComun {
    private val LATON = Color(0xFFB89450)
    private val VITELA = Color(0xFFEDE3C9)
    private val TINTA = Color(0xFF22160E)
    private val LOSA_OSCURA = Color(0xFF4A4640)
    private val RUBI = Color(0xFFB3122E)
    private val AZUL = Color(0xFF2A4C9A)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0C0A08), 0.34f) else c
    /** La vitela del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun vitelaHonda(oscuro: Boolean): Color = if (oscuro) mezcla(VITELA, Color(0xFF0E0C10), 0.74f) else VITELA

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) VITELA else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), vitelaHonda(oscuro), 0.45f)
    /** El azul de vidriera. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CB2EE) else AZUL
    /** El rojo rubí del vidrio. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07A8A) else RUBI

    /** Un cuadrifolio de radio [r] en [o]: cuatro lóbulos y el centro. */
    private fun DrawScope.cuadrifolio(o: Offset, r: Float, color: Color) {
        val l = r * 0.5f
        listOf(Offset(0f, -l), Offset(l, 0f), Offset(0f, l), Offset(-l, 0f)).forEach { d -> drawCircle(color, l, o + d) }
        drawCircle(color, l, o)
    }

    /**
     * El marco: la losa oscura moteada, la orla de latón con su filete grabado y la vitela lisa dentro; en cada esquina, un
     * cuadrifolio de latón. Con el foco el latón se pule (más claro, con un brillo arriba), los cuadrifolios se esmaltan de
     * azul y la vitela se filetea; con un error, de rubí. Con varios renglones, una pauta grabada muy tenue.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(apagadoMedievo(if (oscuro) mezcla(LOSA_OSCURA, Color.Black, 0.35f) else LOSA_OSCURA, e, oscuro))
        val n = ((w + h) / (12 * u)).toInt().coerceIn(6, 60)
        for (j in 0 until n) {
            val p = if (j % 2 == 0) Offset(w * azar(e.semilla, j), if (azar(e.semilla, j + 90) < 0.5f) 1.4f * u else h - 1.4f * u)
            else Offset(if (azar(e.semilla, j + 90) < 0.5f) 1.4f * u else w - 1.4f * u, h * azar(e.semilla, j))
            drawCircle(Color.White.copy(alpha = 0.07f), (0.4f + 0.6f * azar(e.semilla, j + 40)) * u, p)
        }
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.4f), 1.2f * u)
        val laton0 = if (e.enfocado) mezcla(LATON, Color.White, 0.2f) else LATON
        val laton = apagadoMedievo(if (oscuro) mezcla(laton0, Color.Black, 0.3f) else laton0, e, oscuro)
        val grabado = if (oscuro) mezcla(LATON, Color.Black, 0.78f) else mezcla(LATON, TINTA, 0.6f)
        val banda = Rect(3 * u, 3 * u, w - 3 * u, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(banda.left - 0.8f * u, banda.top - 0.8f * u), Size(banda.width + 1.6f * u, banda.height + 1.6f * u))
        drawRect(laton, banda.topLeft, banda.size)
        drawRect(grabado.copy(alpha = 0.7f), Offset(banda.left + 1.2f * u, banda.top + 1.2f * u), Size(banda.width - 2.4f * u, banda.height - 2.4f * u), style = Stroke(0.6f * u))
        if (e.enfocado) drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.55f), Offset(banda.left + 10 * u, banda.top + 0.9f * u), Offset(banda.right - 10 * u, banda.top + 0.9f * u), 0.8f * u)
        val hoja = Rect(8 * u, 8 * u, w - 8 * u, h - 8 * u)
        drawRect(apagadoMedievo(vitelaHonda(oscuro), e, oscuro, 0.18f), hoja.topLeft, hoja.size)
        drawRect(grabado, hoja.topLeft, hoja.size, style = Stroke(0.8f * u))
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < hoja.bottom - 4 * u && k < 200) {
                drawLine((if (oscuro) VITELA else grabado).copy(alpha = 0.16f), Offset(hoja.left + 8 * u, y), Offset(hoja.right - 4 * u, y), 0.6f * u)
                y += paso; k++
            }
        }
        val esmalte = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> rotuloCampo(oscuro)
            else -> grabado.copy(alpha = 0.45f)
        }
        if (e.error || e.enfocado) drawRect(esmalte, Offset(hoja.left + 0.8f * u, hoja.top + 0.8f * u), Size(hoja.width - 1.6f * u, hoja.height - 1.6f * u), style = Stroke(1.2f * u))
        listOf(Offset(5.8f * u, 5.8f * u), Offset(w - 5.8f * u, 5.8f * u), Offset(5.8f * u, h - 5.8f * u), Offset(w - 5.8f * u, h - 5.8f * u)).forEach { o ->
            cuadrifolio(o, 5.4f * u, Color.Black.copy(alpha = 0.4f))
            cuadrifolio(o, 4.8f * u, laton)
            cuadrifolio(o, 3.2f * u, esmalte)
            drawCircle(laton, 0.9f * u, o)
        }
    }
}

/**
 * Andalus: las piezas comunes de la sala del palacio. El campo es una tablilla de hueso embutida en un marco de cedro con
 * su filete de taracea (triangulitos de hueso y ébano), como la regla del buscador, y una estrella de ocho puntas en cada
 * esquina.
 */
object ComunAndalus : DisenoComun {
    private val CEDRO = Color(0xFF7A4A2A)
    private val VETA = Color(0xFF2E1A0C)
    private val HUESO = Color(0xFFE9DFC6)
    private val NEGRO = Color(0xFF1C1A1E)
    private val ORO = Color(0xFFC9A04E)
    private val ORO_CLARO = Color(0xFFE3C47E)
    private val AZUL = Color(0xFF2C4F86)
    private val VERDE = Color(0xFF2F6E52)
    private val TINTA = Color(0xFF221812)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C0A), 0.34f) else c
    /** El hueso del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun huesoHondo(oscuro: Boolean): Color = if (oscuro) mezcla(HUESO, Color(0xFF100C0A), 0.74f) else HUESO

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) HUESO else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), huesoHondo(oscuro), 0.45f)
    /** El azul de cobalto de los alicatados. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FB0E6) else AZUL
    /** Un rojo de almagra encendido. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A66) else Color(0xFFB0301A)

    /**
     * El marco: cedro con su veta y, en los cuatro lados, el filete de taracea de triangulitos de hueso y ébano; una
     * estrella de ocho puntas de hueso en cada esquina y, dentro, la tablilla de hueso lisa con su filo. Con el foco el filo
     * y las estrellas se vuelven de oro; con un error, de almagra encendida. Con varios renglones, una pauta verde tenue.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val marco = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1.5f * u, 2 * u), marco.size, CornerRadius(3 * u))
        drawRoundRect(c(CEDRO), Offset.Zero, marco.size, CornerRadius(3 * u))
        vetas(marco, c(VETA).copy(alpha = 0.4f), 57 + (e.semilla and 7), densidad = 0.7f, grosor = 0.7f)
        bisel(marco, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.2f * u)
        // El filete de taracea, en los cuatro lados.
        val claro = c(HUESO); val oscuroT = c(NEGRO)
        val a = 2.2f * u; val g = 2.6f * u; val paso = maxOf(4 * u, 1f)
        var x = 8 * u; var j = 0
        while (x + paso <= marco.right - 8 * u && j < 300) {
            val col = if (j % 2 == 0) claro else oscuroT
            drawPath(Path().apply { moveTo(x, a + g); lineTo(x + paso / 2, a); lineTo(x + paso, a + g); close() }, col)
            val yb = marco.bottom - a
            drawPath(Path().apply { moveTo(x, yb - g); lineTo(x + paso / 2, yb); lineTo(x + paso, yb - g); close() }, col)
            x += paso; j++
        }
        var y = 8 * u; j = 0
        while (y + paso <= marco.bottom - 8 * u && j < 300) {
            val col = if (j % 2 == 0) claro else oscuroT
            drawPath(Path().apply { moveTo(a + g, y); lineTo(a, y + paso / 2); lineTo(a + g, y + paso); close() }, col)
            val xd = marco.right - a
            drawPath(Path().apply { moveTo(xd - g, y); lineTo(xd, y + paso / 2); lineTo(xd - g, y + paso); close() }, col)
            y += paso; j++
        }
        // La tablilla de hueso.
        val tira = Rect(7 * u, 7 * u, marco.right - 7 * u, marco.bottom - 7 * u)
        drawRect(apagadoMedievo(huesoHondo(oscuro), e, oscuro, 0.18f), tira.topLeft, tira.size)
        drawRect(Color.Black.copy(alpha = 0.1f), tira.topLeft, Size(tira.width, 1.2f * u))
        if (e.multilinea) {
            val p = maxOf(24.sp.toPx(), 1f)
            var yy = margenCampoY.toPx() + p; var k = 0
            while (yy < tira.bottom - 4 * u && k < 200) {
                drawLine((if (oscuro) HUESO else VERDE).copy(alpha = if (oscuro) 0.14f else 0.2f), Offset(tira.left + 4 * u, yy), Offset(tira.right - 4 * u, yy), 0.6f * u)
                yy += p; k++
            }
        }
        val oro = if (oscuro) c(ORO_CLARO) else apagadoMedievo(ORO, e, oscuro)
        val filo = when { e.error -> errorCampo(oscuro); e.enfocado -> oro; else -> c(VETA).copy(alpha = 0.6f) }
        drawRect(filo, tira.topLeft, tira.size, style = Stroke((if (e.error || e.enfocado) 1.5f else 0.8f) * u))
        // Las estrellas de las esquinas.
        val astro = when { e.error -> errorCampo(oscuro); e.enfocado -> c(ORO_CLARO); else -> claro }
        listOf(Offset(3.6f * u, 3.6f * u), Offset(marco.right - 3.6f * u, 3.6f * u), Offset(3.6f * u, marco.bottom - 3.6f * u),
            Offset(marco.right - 3.6f * u, marco.bottom - 3.6f * u)).forEach { o ->
            drawPath(estrella(o, 3.6f * u, 8, 0.55f), astro)
            drawCircle(c(VERDE), 1 * u, o)
        }
    }
}

/**
 * Mongol: las piezas comunes de la ger. El campo es una tira de fieltro blanco acolchado con el cordón cosido en doble
 * filete y un cuerno de carnero en cada esquina, como el buscador.
 */
object ComunMongol : DisenoComun {
    private val ROJO = Color(0xFFB0301F)
    private val ROJO_OSC = Color(0xFF6E1C12)
    private val NARANJA = Color(0xFFDA7A2C)
    private val AZUL = Color(0xFF2D5A8F)
    private val ORO = Color(0xFFE0B54A)
    private val FIELTRO = Color(0xFFE9E0CC)
    private val CIELO = Color(0xFF9CC3E0)
    private val CREMA = Color(0xFFF0E6CC)
    private val TINTA = Color(0xFF231812)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B09), 0.34f) else c
    /** El fieltro del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun fieltroHondo(oscuro: Boolean): Color = if (oscuro) mezcla(FIELTRO, Color(0xFF0F0B09), 0.74f) else FIELTRO

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), fieltroHondo(oscuro), 0.45f)
    /** El azul del cielo eterno. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) CIELO else AZUL
    /** El rojo de la puerta de la ger. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO

    /**
     * El marco: el fieltro redondeado con su pelusa en el borde, el cordón azul cosido en doble filete y un cuerno de
     * carnero (la voluta del ornamento de la estepa) en cada esquina. Con el foco el cordón se vuelve de oro, engruesa y
     * saltan unas chispas del eslabón en la esquina; con un error, el cordón va en rojo. Con varios renglones, las puntadas
     * del acolchado.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val pieza = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(1.5f * u, 2 * u), pieza.size, CornerRadius(10 * u))
        drawRoundRect(apagadoMedievo(fieltroHondo(oscuro), e, oscuro, 0.18f), Offset.Zero, pieza.size, CornerRadius(10 * u))
        // La pelusa del fieltro, en el borde.
        val n = ((pieza.width + pieza.height) / (5 * u)).toInt().coerceIn(10, 160)
        for (j in 0 until n) {
            val a = azar(e.semilla, j); val b = (0.8f + 3f * azar(e.semilla, j + 500)) * u
            val p = when (j % 4) {
                0 -> Offset(pieza.width * a, b)
                1 -> Offset(pieza.right - b, pieza.height * a)
                2 -> Offset(pieza.width * a, pieza.bottom - b)
                else -> Offset(b, pieza.height * a)
            }
            drawCircle((if (oscuro) Color.White else Color.Black).copy(alpha = 0.06f), (0.5f + 0.8f * azar(e.semilla, j + 900)) * u, p)
        }
        val oro = if (oscuro) c(ORO) else apagadoMedievo(mezcla(ORO, ROJO_OSC, 0.3f), e, oscuro)
        val cordon = when { e.error -> errorCampo(oscuro); e.enfocado -> oro; else -> c(if (oscuro) CIELO else AZUL).copy(alpha = 0.85f) }
        val grueso = (if (e.error || e.enfocado) 2f else 1.4f) * u
        val m1 = Rect(3.5f * u, 3.5f * u, pieza.right - 3.5f * u, pieza.bottom - 3.5f * u)
        val m2 = Rect(6.2f * u, 6.2f * u, pieza.right - 6.2f * u, pieza.bottom - 6.2f * u)
        drawRoundRect(cordon, m1.topLeft, m1.size, CornerRadius(7 * u), style = Stroke(grueso))
        drawRoundRect(cordon.copy(alpha = cordon.alpha * 0.6f), m2.topLeft, m2.size, CornerRadius(4.5f * u), style = Stroke(0.8f * u))
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            val puntada = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.6f * u))
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < m2.bottom - 4 * u && k < 200) {
                drawLine(c(if (oscuro) CIELO else AZUL).copy(alpha = 0.28f), Offset(m2.left + 12 * u, y), Offset(m2.right - 12 * u, y), 0.8f * u, pathEffect = puntada)
                y += paso; k++
            }
        }
        // Los cuernos de carnero: dos volutas que se abren desde la esquina.
        listOf(Triple(m2.left + 2.6f * u, m2.top + 2.6f * u, 0f), Triple(m2.right - 2.6f * u, m2.top + 2.6f * u, 90f),
            Triple(m2.right - 2.6f * u, m2.bottom - 2.6f * u, 180f), Triple(m2.left + 2.6f * u, m2.bottom - 2.6f * u, 270f)).forEach { (x, y, g) ->
            val o = Offset(x, y)
            val r = 1.9f * u
            val giro = Math.toRadians(g.toDouble())
            fun rot(dx: Float, dy: Float) = Offset(o.x + (dx * kotlin.math.cos(giro) - dy * kotlin.math.sin(giro)).toFloat(), o.y + (dx * kotlin.math.sin(giro) + dy * kotlin.math.cos(giro)).toFloat())
            val c1 = rot(r, 0f); val c2 = rot(0f, r)
            drawArc(cordon, g + 180f, 270f, false, Offset(c1.x - r, c1.y - r), Size(r * 2, r * 2), style = Stroke(1.1f * u))
            drawArc(cordon, g + 180f, -270f, false, Offset(c2.x - r, c2.y - r), Size(r * 2, r * 2), style = Stroke(1.1f * u))
            drawCircle(cordon, 0.9f * u, o)
        }
        // Con el foco, las chispas del eslabón saltan junto al cuerno de arriba a la izquierda.
        if (e.enfocado && !e.error) {
            val o = Offset(m2.left + 2.6f * u, m2.top + 2.6f * u)
            for (j in 0 until 4) {
                val ang = Math.toRadians((-10.0 + 90.0 * azar(e.semilla, j + 70)))
                val d0 = 4.5f * u; val d1 = (6.5f + 3f * azar(e.semilla, j + 80)) * u
                val p0 = Offset(o.x + d0 * kotlin.math.cos(ang).toFloat(), o.y + d0 * kotlin.math.sin(ang).toFloat())
                val p1 = Offset(o.x + d1 * kotlin.math.cos(ang).toFloat(), o.y + d1 * kotlin.math.sin(ang).toFloat())
                drawLine(c(NARANJA), p0, p1, 0.7f * u)
                drawCircle(c(ORO), 0.6f * u, p1)
            }
        }
    }
}

/**
 * Edo: las piezas comunes de la tienda. El campo es una tabla de paulonia con su ribete de laca negra y, en cada esquina,
 * el herraje en ele de los arcones tansu, emparentada con el rótulo de la tienda del buscador.
 */
object ComunEdo : DisenoComun {
    private val KIRI = Color(0xFFDCC7A1)
    private val KIRI_LUZ = Color(0xFFEBDDBF)
    private val KIRI_VETA = Color(0xFF9E825A)
    private val URUSHI = Color(0xFF231C18)
    private val KINPAKU = Color(0xFFCFAE58)
    private val NAMI = Color(0xFF3D5F86)
    private val SUMI = Color(0xFF1B1A1E)
    private val BERMELLON = Color(0xFFB8322A)
    private val ZOGE = Color(0xFFF1E8D2)
    private val HIERRO = Color(0xFF3A3530)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF120E0B), 0.32f) else c
    /** La madera clara del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun kiriHondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF120E0B), 0.74f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) ZOGE else SUMI
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), kiriHondo(KIRI_LUZ, oscuro), 0.45f)
    /** El añil de las olas. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFA6C0E0) else NAMI
    /** El bermellón del sello. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0806C) else BERMELLON

    /**
     * El marco: un canto de paulonia con su veta y su ribete de laca negra, la tabla clara y lisa encajada dentro, y en cada
     * esquina el herraje de hierro en ele con sus remaches. Con el foco los herrajes se doran y una raya de añil corre al pie
     * de la tabla; con un error, la tabla se filetea de bermellón. Con varios renglones, una pauta de tinta muy tenue.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val tabla = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(1.5f * u, 2 * u), tabla.size)
        drawRect(apagadoMedievo(kiriHondo(KIRI, oscuro), e, oscuro, 0.2f), tabla.topLeft, tabla.size)
        vetas(tabla, c(KIRI_VETA).copy(alpha = if (oscuro) 0.3f else 0.4f), 33 + (e.semilla and 7), densidad = 0.6f, grosor = 0.6f)
        drawRect(c(URUSHI), tabla.topLeft, tabla.size, style = Stroke(1.6f * u))
        val dentro = Rect(5 * u, 5 * u, tabla.right - 5 * u, tabla.bottom - 5 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(dentro.left - 0.8f * u, dentro.top - 0.8f * u), Size(dentro.width + 1.6f * u, dentro.height + 1.6f * u))
        drawRect(apagadoMedievo(kiriHondo(KIRI_LUZ, oscuro), e, oscuro, 0.18f), dentro.topLeft, dentro.size)
        drawRect(Color.Black.copy(alpha = 0.08f), dentro.topLeft, Size(dentro.width, 1.2f * u))
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < dentro.bottom - 4 * u && k < 200) {
                drawLine((if (oscuro) ZOGE else SUMI).copy(alpha = 0.1f), Offset(dentro.left + 4 * u, y), Offset(dentro.right - 4 * u, y), 0.6f * u)
                y += paso; k++
            }
        }
        if (e.error) drawRect(errorCampo(oscuro), dentro.topLeft, dentro.size, style = Stroke(1.4f * u))
        else if (e.enfocado) drawLine(rotuloCampo(oscuro), Offset(dentro.left + 3 * u, dentro.bottom - 1.6f * u), Offset(dentro.right - 3 * u, dentro.bottom - 1.6f * u), 1.4f * u)
        // Los herrajes en ele de las esquinas.
        val metal = when { e.error -> c(HIERRO); e.enfocado -> c(KINPAKU); else -> c(HIERRO) }
        val brillo = if (e.enfocado && !e.error) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.18f)
        val largo = minOf(13 * u, tabla.width * 0.2f, tabla.height * 0.35f); val ancho = 3.4f * u; val m = 0.8f * u
        listOf(Triple(tabla.left, tabla.top, 1f to 1f), Triple(tabla.right, tabla.top, -1f to 1f), Triple(tabla.left, tabla.bottom, 1f to -1f),
            Triple(tabla.right, tabla.bottom, -1f to -1f)).forEach { (x0, y0, s) ->
            val (sx, sy) = s
            val ox = x0 + sx * m; val oy = y0 + sy * m
            val horiz = Rect(minOf(ox, ox + sx * largo), minOf(oy, oy + sy * ancho), maxOf(ox, ox + sx * largo), maxOf(oy, oy + sy * ancho))
            val vert = Rect(minOf(ox, ox + sx * ancho), minOf(oy, oy + sy * largo), maxOf(ox, ox + sx * ancho), maxOf(oy, oy + sy * largo))
            drawRect(Color.Black.copy(alpha = 0.3f), horiz.topLeft + Offset(0.5f * u, 0.6f * u), horiz.size)
            drawRect(Color.Black.copy(alpha = 0.3f), vert.topLeft + Offset(0.5f * u, 0.6f * u), vert.size)
            drawRect(metal, horiz.topLeft, horiz.size)
            drawRect(metal, vert.topLeft, vert.size)
            // Las puntas redondeadas y los remaches.
            drawCircle(metal, ancho * 0.62f, Offset(ox + sx * largo, oy + sy * ancho / 2))
            drawCircle(metal, ancho * 0.62f, Offset(ox + sx * ancho / 2, oy + sy * largo))
            listOf(Offset(ox + sx * largo, oy + sy * ancho / 2), Offset(ox + sx * ancho / 2, oy + sy * largo), Offset(ox + sx * ancho / 2, oy + sy * ancho / 2)).forEach { p ->
                drawCircle(Color.Black.copy(alpha = 0.45f), 0.75f * u, p)
                drawCircle(brillo, 0.35f * u, p - Offset(0.3f * u, 0.3f * u))
            }
        }
    }
}

/**
 * Renacimiento: las piezas comunes del studiolo. El campo es una regla de boj graduada por los dos cantos, con sus
 * cabezas de latón remachadas, como la regla del buscador.
 */
object ComunRenacimiento : DisenoComun {
    private val BOJ = Color(0xFFE2C98E)
    private val NOGAL = Color(0xFF5A3B24)
    private val LATON = Color(0xFFBF9A52)
    private val LATON_OSC = Color(0xFF8A6A30)
    private val PERGAMINO = Color(0xFFEFE5CB)
    private val TINTA = Color(0xFF2A1E14)
    private val SANGUINA = Color(0xFFA8503A)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c
    /** El boj del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun bojHondo(oscuro: Boolean): Color = if (oscuro) mezcla(BOJ, Color(0xFF100C08), 0.74f) else BOJ

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), bojHondo(oscuro), 0.45f)
    /** La sanguina de los dibujos, oscurecida para leerse sobre el boj. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8987A) else Color(0xFF8E3E28)
    /** El carmín: un rojo frío que no se confunde con la sanguina. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07898) else Color(0xFFA0103A)

    /**
     * El marco: la regla de boj con su bisel y su canto de nogal, la graduación en los dos cantos (más larga cada cinco y
     * cada diez) y una cabeza de latón remachada en cada punta; el centro, liso. Con el foco el latón brilla y el canto y la
     * graduación toman la sanguina; con un error, el carmín. Con varios renglones, una pauta tenue.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val regla = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(1.5f * u, 2 * u), regla.size)
        drawRect(apagadoMedievo(bojHondo(oscuro), e, oscuro, 0.18f), regla.topLeft, regla.size)
        bisel(regla, Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        val marca = when { e.error -> errorCampo(oscuro); e.enfocado -> rotuloCampo(oscuro); else -> null }
        drawRect(marca ?: c(NOGAL).copy(alpha = 0.7f), regla.topLeft, regla.size, style = Stroke((if (marca != null) 1.6f else 1f) * u))
        // Con el foco o un error, el filete de sanguina (o de carmín) por dentro de la graduación.
        if (marca != null) drawRect(marca, Offset(8 * u, 7.2f * u), Size(regla.width - 16 * u, regla.height - 14.4f * u), style = Stroke(1.2f * u))
        // Las cabezas de latón.
        val capa = 4 * u
        val laton = c(if (e.enfocado) mezcla(LATON, Color.White, 0.22f) else LATON)
        listOf(Rect(0f, 0f, capa, regla.bottom), Rect(regla.right - capa, 0f, regla.right, regla.bottom)).forEach { r ->
            drawRect(laton, r.topLeft, r.size)
            bisel(r, Color.White.copy(alpha = if (e.enfocado) 0.45f else 0.25f), Color.Black.copy(alpha = 0.3f), 0.8f * u)
            listOf(r.top + 4 * u, r.bottom - 4 * u).forEach { y ->
                drawCircle(c(LATON_OSC), 0.9f * u, Offset(r.center.x, y))
                drawCircle(Color.White.copy(alpha = 0.4f), 0.35f * u, Offset(r.center.x - 0.3f * u, y - 0.3f * u))
            }
        }
        // La graduación, arriba y abajo.
        val raya = marca ?: tintaCampo(oscuro).copy(alpha = 0.5f)
        val paso = maxOf(4 * u, 1f)
        var x = capa + 4 * u; var j = 0
        while (x < regla.right - capa - 3 * u && j < 400) {
            val largo = (if (j % 10 == 0) 5f else if (j % 5 == 0) 3.6f else 2.2f) * u
            drawLine(raya, Offset(x, 1.2f * u), Offset(x, 1.2f * u + largo), 0.6f * u)
            drawLine(raya, Offset(x, regla.bottom - 1.2f * u), Offset(x, regla.bottom - 1.2f * u - largo), 0.6f * u)
            x += paso; j++
        }
        if (e.multilinea) {
            val p = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + p; var k = 0
            while (y < regla.bottom - 7 * u && k < 200) {
                drawLine(tintaCampo(oscuro).copy(alpha = 0.12f), Offset(capa + 6 * u, y), Offset(regla.right - capa - 6 * u, y), 0.6f * u)
                y += p; k++
            }
        }
    }
}

/**
 * Pirata: las piezas comunes del camarote. El campo es un paño de lona de vela con sus costuras de velero, la relinga (el
 * cabo cosido al canto) alrededor y un ollao de latón en cada esquina, como la lona del buscador.
 */
object ComunPirata : DisenoComun {
    private val LATON = Color(0xFFC09A48)
    private val LATON_OSC = Color(0xFF7E6230)
    private val PAPEL = Color(0xFFE6DCC0)
    private val TINTA = Color(0xFF231A12)
    private val CABO = Color(0xFFB8A070)
    private val CABO_OSC = Color(0xFF7E6A44)
    private val LONA = Color(0xFFBDB294)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0C0A08), 0.34f) else c
    /** La lona del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun lonaHonda(oscuro: Boolean): Color = if (oscuro) mezcla(LONA, Color(0xFF0C0A08), 0.74f) else LONA

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 58.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 12.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), lonaHonda(oscuro), 0.42f)
    /** El azul de mar hondo. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8EC0DA) else Color(0xFF1F4566)
    /** El rojo del lacre. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A70) else Color(0xFF8E1A12)

    /**
     * El marco: el paño de lona con su trama solo en el borde, una costura doble de velero arriba y abajo, la relinga
     * torcida alrededor de los cuatro cantos y un ollao de latón en cada esquina. Con el foco los ollaos se pulen y un
     * filete azul de mar corre por dentro de la relinga; con un error, el filete va en lacre. Con varios renglones, una pauta
     * tenue.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val pano = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(1.5f * u, 2 * u), pano.size)
        drawRect(apagadoMedievo(lonaHonda(oscuro), e, oscuro, 0.18f), pano.topLeft, pano.size)
        // La trama, solo en las bandas del borde.
        val trama = Color.Black.copy(alpha = 0.06f)
        listOf(0f to 9 * u, pano.bottom - 9 * u to pano.bottom).forEach { (y0, y1) ->
            var y = y0 + 1 * u; var j = 0
            while (y < y1 && j < 20) { drawLine(trama, Offset(0f, y), Offset(pano.right, y), 0.5f * u); y += maxOf(2 * u, 1f); j++ }
        }
        // Las costuras del velero, arriba y abajo.
        val hilo = if (oscuro) LONA.copy(alpha = 0.35f) else c(CABO_OSC).copy(alpha = 0.65f)
        listOf(6.5f * u, pano.bottom - 6.5f * u).forEach { y ->
            drawLine(hilo, Offset(11 * u, y), Offset(pano.right - 11 * u, y), 0.6f * u)
            var x = 12 * u; var k = 0
            while (x < pano.right - 12 * u && k < 300) { drawLine(hilo, Offset(x, y - 1.1f * u), Offset(x + 1.5f * u, y + 1.1f * u), 0.6f * u); x += maxOf(4 * u, 1f); k++ }
        }
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var y = margenCampoY.toPx() + paso; var k = 0
            while (y < pano.bottom - 10 * u && k < 200) {
                drawLine(tintaCampo(oscuro).copy(alpha = 0.12f), Offset(12 * u, y), Offset(pano.right - 12 * u, y), 0.6f * u)
                y += paso; k++
            }
        }
        // La relinga, alrededor.
        val g = 3 * u; val r = 2 * u
        val claro = c(CABO); val sombra = c(CABO_OSC)
        caboMedievo(Offset(r, r), Offset(pano.right - r, r), g, claro, sombra)
        caboMedievo(Offset(r, pano.bottom - r), Offset(pano.right - r, pano.bottom - r), g, claro, sombra)
        caboMedievo(Offset(r, r), Offset(r, pano.bottom - r), g, claro, sombra)
        caboMedievo(Offset(pano.right - r, r), Offset(pano.right - r, pano.bottom - r), g, claro, sombra)
        if (e.error || e.enfocado) {
            val f = if (e.error) errorCampo(oscuro) else rotuloCampo(oscuro)
            drawRect(f, Offset(5 * u, 5 * u), Size(pano.width - 10 * u, pano.height - 10 * u), style = Stroke(1.5f * u))
        }
        // Los ollaos de las esquinas.
        val laton = c(if (e.enfocado) mezcla(LATON, Color.White, 0.25f) else LATON)
        listOf(Offset(7.5f * u, 7.5f * u), Offset(pano.right - 7.5f * u, 7.5f * u), Offset(7.5f * u, pano.bottom - 7.5f * u),
            Offset(pano.right - 7.5f * u, pano.bottom - 7.5f * u)).forEach { o ->
            drawCircle(Color.Black.copy(alpha = 0.3f), 3.2f * u, o + Offset(0.4f * u, 0.5f * u))
            drawCircle(laton, 3 * u, o)
            drawCircle(c(LATON_OSC), 1.5f * u, o)
            drawCircle(if (oscuro) Color(0xFF0C0A08) else Color(0xFF2A2218), 1.1f * u, o)
            drawCircle(Color.White.copy(alpha = if (e.enfocado) 0.6f else 0.3f), 0.6f * u, o + Offset(-1.6f * u, -1.6f * u))
        }
    }
}

/**
 * Revolución: las piezas comunes de la imprenta. El campo es el componedor del cajista: la regla de acero cepillado con
 * el canal donde se alinean las letras, el talón con su tornillo a la izquierda y la escuadra a la derecha, como el
 * buscador.
 */
object ComunRevolucion : DisenoComun {
    private val HIERRO = Color(0xFF26282A)
    private val PAPEL = Color(0xFFEDE4CC)
    private val TINTA = Color(0xFF1E1C1A)
    private val AZUL = Color(0xFF2B3F8C)
    private val ROJO = Color(0xFFB0282A)
    private val ACERO = Color(0xFF9AA0A6)
    private val PELTRE = Color(0xFF8E9296)
    private val LATON = Color(0xFFD6B55A)

    /** El color en modo oscuro: más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0C), 0.36f) else c
    /** El papel del campo: en modo oscuro se hunde de verdad, para leerse con tinta clara. */
    private fun papelHondo(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF0B0D0C), 0.74f) else PAPEL

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 15.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), papelHondo(oscuro), 0.45f)
    /** El azul de la escarapela. */
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CAEF0) else AZUL
    /** El rojo de la escarapela. */
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08080) else ROJO

    /**
     * El marco: la regla de acero cepillado con su bisel, el canal con el papel de la prueba (rehundido, liso), el talón de
     * hierro a la izquierda con su tornillo y la escuadra a la derecha con dos remaches. Con el foco el tornillo es de latón
     * y aparecen la cuña azul que marca la letra siguiente y el filete azul del canal; con un error, cuña y filete en rojo.
     * Con varios renglones, las regletas que separan las líneas.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = apagadoMedievo(t(x, oscuro), e, oscuro)
        val cuerpo = Rect(0f, 0f, size.width - 1.5f * u, size.height - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(1.5f * u, 2 * u), cuerpo.size)
        drawRect(c(ACERO), cuerpo.topLeft, cuerpo.size)
        var y = 1.5f * u; var j = 0
        while (y < cuerpo.bottom - 1 * u && j < 200) { drawLine(Color.White.copy(alpha = 0.09f), Offset(0f, y), Offset(cuerpo.right, y), 0.5f * u); y += maxOf(2.4f * u, 1f); j++ }
        bisel(cuerpo, Color.White.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.35f), 1.4f * u)
        val canal = Rect(7 * u, 5 * u, cuerpo.right - 9 * u, cuerpo.bottom - 5 * u)
        drawRect(apagadoMedievo(papelHondo(oscuro), e, oscuro, 0.18f), canal.topLeft, canal.size)
        drawRect(Color.Black.copy(alpha = 0.25f), canal.topLeft, Size(canal.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.18f), canal.topLeft, Size(1.8f * u, canal.height))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(canal.left, canal.bottom - 1.2f * u), Size(canal.width, 1.2f * u))
        if (e.multilinea) {
            val paso = maxOf(24.sp.toPx(), 1f)
            var yy = margenCampoY.toPx() + paso; var k = 0
            while (yy < canal.bottom - 4 * u && k < 200) {
                drawLine(c(PELTRE).copy(alpha = if (oscuro) 0.35f else 0.4f), Offset(canal.left + 3 * u, yy), Offset(canal.right - 3 * u, yy), 0.9f * u)
                yy += paso; k++
            }
        }
        // El talón con el tornillo.
        val talon = Rect(1.6f * u, 1.6f * u, 5.4f * u, cuerpo.bottom - 1.6f * u)
        drawRect(c(HIERRO), talon.topLeft, talon.size)
        drawRect(Color.White.copy(alpha = 0.14f), talon.topLeft, Size(0.8f * u, talon.height))
        val o = Offset(talon.center.x, cuerpo.center.y)
        val tornillo = if (e.enfocado) c(LATON) else c(PELTRE)
        drawCircle(Color.Black.copy(alpha = 0.35f), 2.4f * u, o + Offset(0.4f * u, 0.5f * u))
        drawCircle(tornillo, 2.2f * u, o)
        drawLine(Color.Black.copy(alpha = 0.5f), o - Offset(1.5f * u, if (e.enfocado) 1.1f * u else 0f), o + Offset(1.5f * u, if (e.enfocado) 1.1f * u else 0f), 0.6f * u)
        // La escuadra de la derecha.
        val escuadra = Rect(canal.right + 1.4f * u, 1.6f * u, cuerpo.right - 1.6f * u, cuerpo.bottom - 1.6f * u)
        drawRect(c(HIERRO), escuadra.topLeft, escuadra.size)
        drawRect(Color.White.copy(alpha = 0.14f), escuadra.topLeft, Size(0.8f * u, escuadra.height))
        listOf(escuadra.top + 4 * u, escuadra.bottom - 4 * u).forEach { yy -> drawCircle(c(PELTRE), 0.9f * u, Offset(escuadra.center.x, yy)) }
        // La cuña y el filete del canal, con el foco o con un error.
        if (e.enfocado || e.error) {
            val col = if (e.error) errorCampo(oscuro) else rotuloCampo(oscuro)
            drawRect(col, canal.topLeft, canal.size, style = Stroke((if (e.error) 1.5f else 1.2f) * u))
            val cx = canal.left + 1.8f * u
            drawPath(Path().apply { moveTo(cx, canal.top + 1.6f * u); lineTo(cx + 3.4f * u, canal.top + 1.6f * u); lineTo(cx + 1.7f * u, canal.top + 6 * u); close() }, col)
        }
    }
}

// =====================================================================
//  Ayudantes de las piezas comunes (Edad Media y Edad Moderna)
// =====================================================================

/** El color de un campo apagado (sin [EstadoMarco.habilitado]): se acerca a un gris de la luz del momento, en [cuanto]. */
private fun apagadoMedievo(c: Color, e: EstadoMarco, oscuro: Boolean, cuanto: Float = 0.45f): Color =
    if (e.habilitado) c else mezcla(c, if (oscuro) Color(0xFF2C2A27) else Color(0xFFA9A49A), cuanto).copy(alpha = c.alpha)

/** Un cabo torcido de [a] a [b], de grosor [grosor]: la cuerda clara, su sombra y las vueltas de los cordones. */
private fun DrawScope.caboMedievo(a: Offset, b: Offset, grosor: Float, claro: Color, sombra: Color) {
    val u = density
    drawLine(sombra, a, b, grosor + 0.8f * u)
    drawLine(claro, a, b, grosor)
    val dx = b.x - a.x; val dy = b.y - a.y
    val largo = kotlin.math.sqrt(dx * dx + dy * dy)
    if (largo < 1f) return
    val ux = dx / largo; val uy = dy / largo
    val n = (largo / maxOf(2.6f * u, 1f)).toInt().coerceIn(1, 400)
    val m = grosor * 0.5f
    for (k in 0 until n) {
        val f = (k + 0.5f) / n
        val p = Offset(a.x + dx * f, a.y + dy * f)
        val p0 = Offset(p.x - uy * m - ux * m * 0.6f, p.y + ux * m - uy * m * 0.6f)
        val p1 = Offset(p.x + uy * m + ux * m * 0.6f, p.y - ux * m + uy * m * 0.6f)
        drawLine(sombra, p0, p1, 0.7f * u)
    }
}
