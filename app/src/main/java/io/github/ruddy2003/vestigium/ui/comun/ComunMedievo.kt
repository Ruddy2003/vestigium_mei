package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
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
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La raya: un filete de tinta a punta de pluma, cortado en medio por el calderón del rubricador: un rombo de bermellón
     * entre dos puntos de lapislázuli.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val tinta = tenueMedievo(if (oscuro) CERA.copy(alpha = 0.3f) else TINTA.copy(alpha = 0.4f), e)
        val rojo = tenueMedievo(if (oscuro) Color(0xFFD9785C) else BERMELLON, e)
        val azul = tenueMedievo(if (oscuro) Color(0xFF8FA8E0) else LAPIZ, e)
        val hueco = 9.5f * u
        drawLine(tinta, Offset(4 * u, y), Offset(cx - hueco, y), 0.9f * u, StrokeCap.Round)
        drawLine(tinta, Offset(cx + hueco, y), Offset(w - 4 * u, y), 0.9f * u, StrokeCap.Round)
        val r = 2.6f * u
        drawPath(Path().apply { moveTo(cx, y - r); lineTo(cx + r * 0.8f, y); lineTo(cx, y + r); lineTo(cx - r * 0.8f, y); close() }, rojo)
        drawCircle(azul, 1.1f * u, Offset(cx - 6 * u, y))
        drawCircle(azul, 1.1f * u, Offset(cx + 6 * u, y))
    }

    /** El bermellón de las rúbricas; en modo oscuro, el oro de las capitulares. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD5A84A) else Color(0xFF9E1B1B)

    /**
     * El adorno: el subrayado de la rúbrica, un trazo de bermellón bajo el texto que arranca con un ganchito y remata en una
     * floritura enroscada. Al presionarlo, el pergamino se tiñe y el trazo engruesa.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) ORO else BERMELLON).copy(alpha = if (oscuro) 0.14f else 0.1f), 4 * u)
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 8 * u)
        val trazo = apagadoBotonMedievo((if (oscuro) Color(0xFFD9785C) else BERMELLON).copy(alpha = if (e.presionado) 0.95f else 0.7f), e, oscuro)
        val p = Path().apply {
            moveTo(x0 + 1.5f * u, y - 2.6f * u)
            quadraticTo(x0 - 0.8f * u, y, x0 + 2.5f * u, y)
            lineTo(x1 - 4 * u, y)
            quadraticTo(x1 + 0.5f * u, y, x1, y - 2.8f * u)
            quadraticTo(x1 - 0.6f * u, y - 5 * u, x1 - 2.8f * u, y - 3.6f * u)
        }
        drawPath(p, trazo, style = Stroke((if (e.presionado) 1.4f else 1f) * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El pergamino de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más hondo que el del campo. */
    private fun hojaPieza(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color(0xFF17110B), 0.82f) else PERGAMINO
    /** El bermellón de las rúbricas, aclarado en modo oscuro. */
    private fun rubrica(oscuro: Boolean): Color = if (oscuro) Color(0xFFD9785C) else BERMELLON
    /** La punta de plomo del pautado. */
    private fun plomo(oscuro: Boolean): Color = if (oscuro) CERA.copy(alpha = 0.16f) else PIEDRA_OSC.copy(alpha = 0.45f)

    override val margenFila: Dp = 12.dp

    /**
     * La fila: un renglón pautado. Abajo, la raya a punta de plomo con su piquete al final; a la izquierda, la línea de margen
     * del pautado, apenas rubricada. Al tocarla, el pergamino se tiñe de oro y la línea de margen se rubrica entera.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) drawRect((if (oscuro) ORO else mezcla(ORO, BERMELLON, 0.3f)).copy(alpha = if (oscuro) 0.1f else 0.14f))
        val y = h - 0.6f * u
        drawLine(plomo(oscuro), Offset(4 * u, y), Offset(w - 6 * u, y), 0.6f * u)
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f), 0.7f * u, Offset(w - 3 * u, y))
        val x = 5 * u
        if (e.presionado) drawLine(rubrica(oscuro).copy(alpha = 0.9f), Offset(x, 6 * u), Offset(x, h - 6 * u), 1.3f * u)
        else drawLine(rubrica(oscuro).copy(alpha = if (oscuro) 0.24f else 0.2f), Offset(x, 12 * u), Offset(x, h - 12 * u), 0.7f * u)
    }

    /** La flecha: una plumilla de lado, con la punta hacia la derecha, su hendidura, el ojo y la virola de bermellón. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val tinta = if (oscuro) CERA.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.78f)
        val hueco = if (oscuro) Color(0xFF15100A) else Color(0xFFEFE3C8)
        drawPath(Path().apply {
            moveTo(f(0.2f), f(0.32f)); lineTo(f(0.5f), f(0.3f)); quadraticTo(f(0.78f), f(0.36f), f(0.94f), f(0.5f))
            quadraticTo(f(0.78f), f(0.64f), f(0.5f), f(0.7f)); lineTo(f(0.2f), f(0.68f)); close()
        }, tinta)
        drawLine(hueco, Offset(f(0.52f), f(0.5f)), Offset(f(0.9f), f(0.5f)), 0.7f * u)
        drawCircle(hueco, f(0.065f), Offset(f(0.5f), f(0.5f)))
        drawRect(rubrica(oscuro), Offset(f(0.06f), f(0.3f)), Size(f(0.12f), f(0.4f)))
    }

    /** La marca fija: la cinta de registro del códice, con su cola de golondrina; de bermellón y filete de oro si está fija. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val cinta = Path().apply {
            moveTo(f(0.32f), f(0.1f)); lineTo(f(0.68f), f(0.1f)); lineTo(f(0.68f), f(0.9f)); lineTo(f(0.5f), f(0.74f)); lineTo(f(0.32f), f(0.9f)); close()
        }
        val oro = if (oscuro) ORO else mezcla(ORO, TINTA, 0.32f)
        if (fija) {
            drawPath(cinta, rubrica(oscuro))
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(f(0.44f), f(0.14f)), Offset(f(0.44f), f(0.76f)), 0.6f * u)
            drawPath(cinta, oro, style = Stroke(0.9f * u, join = StrokeJoin.Round))
            drawLine(oro, Offset(f(0.24f), f(0.1f)), Offset(f(0.76f), f(0.1f)), 1.6f * u, StrokeCap.Round)
        } else {
            drawPath(cinta, (if (oscuro) CERA else TINTA).copy(alpha = 0.5f), style = Stroke(1.1f * u, join = StrokeJoin.Round))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) CERA else TINTA

    /**
     * El vacío: un folio pautado y sin escribir. La caja del pautado a punta de plomo (las líneas se cruzan en las esquinas,
     * como en los códices), los piquetes en el margen derecho y, arriba a la izquierda, el hueco punteado de la capitular que
     * nadie pintó.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(2.2f * u, 2.6f * u), Size(w - 3 * u, h - 3 * u))
        drawRect(hojaPieza(oscuro), Offset(1 * u, 1 * u), Size(w - 3 * u, h - 3 * u))
        val pl = plomo(oscuro).let { it.copy(alpha = it.alpha * 0.8f) }
        listOf(9 * u, w - 11 * u).forEach { x -> drawLine(pl, Offset(x, 4 * u), Offset(x, h - 5 * u), 0.6f * u) }
        listOf(9 * u, h - 11 * u).forEach { y -> drawLine(pl, Offset(4 * u, y), Offset(w - 6 * u, y), 0.6f * u) }
        val paso = maxOf(8 * u, 1f)
        var y = 14 * u; var k = 0
        while (y < h - 14 * u && k < 60) {
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), 0.55f * u, Offset(w - 6 * u, y)); y += paso; k++
        }
        drawRect((if (oscuro) Color(0xFF8FA8E0) else LAPIZ).copy(alpha = 0.4f), Offset(13 * u, 13 * u), Size(9 * u, 9 * u),
            style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 1.5f * u))))
    }

    /**
     * Lo vacío: el tintero de cuerno seco en su pupitre y la pluma de ave apoyada, con la punta todavía roja de la última
     * rúbrica; en la tabla, una gota seca.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) CERA.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.74f)
        val oro = if (oscuro) ORO.copy(alpha = 0.45f) else ORO.copy(alpha = 0.6f)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // El pupitre.
        drawLine(l, Offset(f(0.04f), f(0.9f)), Offset(f(0.96f), f(0.9f)), tr.width)
        // El tintero de cuerno, con la boca abierta y el fondo agrietado.
        drawPath(Path().apply { moveTo(f(0.14f), f(0.5f)); lineTo(f(0.46f), f(0.5f)); lineTo(f(0.39f), f(0.9f)); lineTo(f(0.21f), f(0.9f)); close() }, l, style = tr)
        drawOval(l, Offset(f(0.12f), f(0.46f)), Size(f(0.36f), f(0.08f)), style = tr)
        drawLine(l.copy(alpha = l.alpha * 0.5f), Offset(f(0.24f), f(0.66f)), Offset(f(0.3f), f(0.72f)), 0.8f * u)
        drawLine(l.copy(alpha = l.alpha * 0.5f), Offset(f(0.3f), f(0.72f)), Offset(f(0.27f), f(0.8f)), 0.8f * u)
        // La pluma: el cañón, la barba de un lado (de oro tenue) y la punta de bermellón.
        val barba = Path().apply { moveTo(f(0.5f), f(0.46f)); quadraticTo(f(0.56f), f(0.14f), f(0.93f), f(0.05f)); quadraticTo(f(0.86f), f(0.32f), f(0.5f), f(0.46f)); close() }
        drawPath(barba, oro)
        drawPath(barba, l, style = Stroke(1 * u, join = StrokeJoin.Round))
        drawLine(l, Offset(f(0.38f), f(0.57f)), Offset(f(0.92f), f(0.06f)), 1.1f * u, StrokeCap.Round)
        listOf(0.3f, 0.5f, 0.7f).forEach { k ->
            val p = Offset(f(0.5f + 0.42f * k), f(0.46f - 0.4f * k))
            drawLine(l.copy(alpha = l.alpha * 0.55f), p, p + Offset(f(0.06f), f(0.08f)), 0.6f * u)
        }
        drawLine(rubrica(oscuro), Offset(f(0.38f), f(0.57f)), Offset(f(0.33f), f(0.62f)), 1.5f * u, StrokeCap.Round)
        drawOval(rubrica(oscuro).copy(alpha = 0.8f), Offset(f(0.6f), f(0.865f)), Size(f(0.07f), f(0.035f)))
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) CERA else TINTA

    /**
     * El aviso: una tira de pergamino con la línea de margen en bermellón tenue y, en el margen, un calderón (¶) rubricado a la
     * altura del primer renglón; en la mitad de los avisos, con la filigrana azul del rubricador alrededor.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(hojaPieza(oscuro))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Offset.Zero, Size(w, 1.2f * u))
        drawLine(plomo(oscuro), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.6f * u)
        val rojo = rubrica(oscuro)
        drawLine(rojo.copy(alpha = 0.35f), Offset(25 * u, 4 * u), Offset(25 * u, h - 4 * u), 0.7f * u)
        val c = Offset(13 * u, minOf(h / 2, 18 * u))
        val r = 6.5f * u
        // El calderón: la panza, el travesaño y los dos palos.
        drawCircle(rojo, r * 0.48f, Offset(c.x - r * 0.12f, c.y - r * 0.46f))
        drawLine(rojo, Offset(c.x - r * 0.12f, c.y - r * 0.92f), Offset(c.x + r * 0.62f, c.y - r * 0.92f), 1.3f * u)
        drawLine(rojo, Offset(c.x + r * 0.22f, c.y - r * 0.92f), Offset(c.x + r * 0.22f, c.y + r), 1.3f * u)
        drawLine(rojo, Offset(c.x + r * 0.58f, c.y - r * 0.92f), Offset(c.x + r * 0.58f, c.y + r), 1.3f * u)
        if (e.semilla % 2 == 1) {
            val azul = (if (oscuro) Color(0xFF8FA8E0) else LAPIZ).copy(alpha = 0.7f)
            val fil = Path().apply {
                moveTo(c.x - r * 1.2f, c.y - r * 1.2f); quadraticTo(c.x - r * 1.5f, c.y, c.x - r * 1.1f, c.y + r * 1.2f)
                moveTo(c.x + r * 1.1f, c.y - r * 1.2f); quadraticTo(c.x + r * 1.4f, c.y, c.x + r * 1.0f, c.y + r * 1.2f)
            }
            drawPath(fil, azul, style = Stroke(0.6f * u, cap = StrokeCap.Round))
            drawCircle(azul, 0.7f * u, Offset(c.x - r * 1.1f, c.y + r * 1.25f)); drawCircle(azul, 0.7f * u, Offset(c.x + r * 1f, c.y + r * 1.25f))
        }
    }

    override val margenCifra: Dp = 10.dp
    /** El bermellón de las rúbricas; en modo oscuro, el oro de las capitulares. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) ORO else BERMELLON
    /** El lapislázuli. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF9DB4E6) else LAPIZ

    /** La cifra: un recuadro de pergamino con el filete doble de oro de las miniaturas y un punto de lapislázuli en cada esquina. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(1.4f * u, 1.8f * u), Size(w - 1.4f * u, h - 1.8f * u))
        drawRect(hojaPieza(oscuro), Offset.Zero, Size(w - 1.4f * u, h - 1.8f * u))
        val oro = if (oscuro) ORO.copy(alpha = 0.6f) else mezcla(ORO, TINTA, 0.3f)
        val a = 3 * u
        drawRect(oro, Offset(a, a), Size(w - 1.4f * u - 2 * a, h - 1.8f * u - 2 * a), style = Stroke(1 * u))
        drawRect(oro.copy(alpha = oro.alpha * 0.5f), Offset(a + 2 * u, a + 2 * u), Size(w - 1.4f * u - 2 * a - 4 * u, h - 1.8f * u - 2 * a - 4 * u), style = Stroke(0.5f * u))
        val azul = if (oscuro) Color(0xFF8FA8E0) else LAPIZ
        listOf(Offset(a, a), Offset(w - 1.4f * u - a, a), Offset(a, h - 1.8f * u - a), Offset(w - 1.4f * u - a, h - 1.8f * u - a)).forEach { o -> drawCircle(azul, 1.3f * u, o) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp
    /** La tinta de agalla; en modo oscuro, la cera. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) CERA else TINTA
    /** El lapislázuli de las iniciales; en la tarjeta de Vida, la tinta aguada. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.VIDA -> if (oscuro) Color(0xFFC9BC9E) else Color(0xFF5A4630)
        else -> if (oscuro) Color(0xFF9DB4E6) else LAPIZ
    }
    /** El bermellón de las rúbricas (aclarado en modo oscuro); en la tarjeta de Vida, el lapislázuli. */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.VIDA -> if (oscuro) Color(0xFF9DB4E6) else LAPIZ
        else -> if (oscuro) Color(0xFFEE9A80) else Color(0xFF9A2E1C)
    }

    /**
     * Las tarjetas, todas de pergamino liso por dentro:
     * - la cifra de Hoy es la tablilla de la librería encadenada: el canto de roble arriba, con la grapa y la cadena de hierro;
     *   destacada, el filete se rubrica y cuelga un sello de lacre en la esquina;
     * - «Te falta hoy» es el rollo abierto entre sus dos varas, con la cinta y el sello de lacre al pie; completo, el sello
     *   lleva su cruz y su aro de oro;
     * - la noche es el folio con su cielo de lapislázuli arriba, sembrado de estrellas de oro y con la luna creciente; sellada,
     *   la luna está llena y la hoja se filetea de oro;
     * - la tarjeta de Vida es la tapa de un códice: el lomo de cuero rojo a la derecha, con sus nervios y el broche de latón.
     * Al presionarlas, la hoja baja y se tiñe de oro.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val hoja = hojaPieza(oscuro)
        val oro = if (oscuro) ORO.copy(alpha = 0.7f) else mezcla(ORO, TINTA, 0.3f)
        val lacre = if (oscuro) Color(0xFFC0503A) else BERMELLON
        val toque = (if (oscuro) ORO else mezcla(ORO, BERMELLON, 0.3f)).copy(alpha = if (oscuro) 0.08f else 0.14f)
        val filo = (if (oscuro) CERA else TINTA).copy(alpha = if (oscuro) 0.22f else 0.35f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hoja, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El canto de roble arriba, con su veta.
                val canto = Rect(r.left, r.top, r.right, r.top + 4.5f * u)
                drawRect(t(ROBLE, oscuro), canto.topLeft, canto.size)
                vetas(canto, t(VETA, oscuro).copy(alpha = 0.45f), 31 + e.semilla, densidad = 0.8f, grosor = 0.5f)
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(r.left, canto.bottom), Offset(r.right, canto.bottom), 0.6f * u)
                drawPath(forma, if (e.destacado) lacre else filo, style = Stroke(if (e.destacado) 1.4f * u else 0.7f * u))
                // La grapa de hierro y la cadena que se va por el canto.
                val g = Offset(r.right - minOf(34 * u, r.width * 0.3f), canto.center.y)
                drawRect(t(HIERRO, oscuro), Offset(g.x - 3 * u, canto.top + 0.5f * u), Size(6 * u, canto.height - 1 * u))
                drawCircle(t(LATON, oscuro), 0.7f * u, Offset(g.x - 1.6f * u, g.y)); drawCircle(t(LATON, oscuro), 0.7f * u, Offset(g.x + 1.6f * u, g.y))
                cadenaMedievo(Offset(g.x + 3 * u, g.y), Offset(r.right - 1 * u, g.y), 5 * u, if (oscuro) Color(0xFF8A8A90) else t(HIERRO, oscuro), 0.9f * u)
                if (e.destacado) {
                    val c = Offset(r.right - 6.5f * u, r.bottom - 6.5f * u)
                    drawLine(lacre, Offset(c.x - 2 * u, c.y - 7 * u), c, 1.4f * u); drawLine(lacre, Offset(c.x + 2 * u, c.y - 7 * u), c, 1.4f * u)
                    drawCircle(lacre, 4.6f * u, c)
                    drawCircle(Color.Black.copy(alpha = 0.25f), 3 * u, c, style = Stroke(0.6f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val vara = minOf(9 * u, r.width / 10)
                val pliego = Rect(r.left + vara * 0.6f, r.top + 1.5f * u, r.right - vara * 0.6f, r.bottom - 1.5f * u)
                sombraTarjetaMedievo(rectMedievo(pliego), e, oscuro)
                drawRect(mezcla(hoja, ORO, if (oscuro) 0.08f else 0.16f), pliego.topLeft, pliego.size)
                if (e.presionado) drawRect(toque, pliego.topLeft, pliego.size)
                // Las rayas de caja, rubricadas: la gruesa y la fina, arriba y abajo.
                listOf(pliego.top + 3 * u to 1f, pliego.top + 5.2f * u to 0.5f, pliego.bottom - 3 * u to 1f, pliego.bottom - 5.2f * u to 0.5f).forEach { (y, g) ->
                    drawLine(lacre.copy(alpha = if (g > 0.6f) 0.85f else 0.5f), Offset(pliego.left + vara * 0.6f, y), Offset(pliego.right - vara * 0.6f, y), g * 1.1f * u)
                }
                // Las varas del rollo, con el pergamino enrollado (cada una con su luz).
                val claro = if (oscuro) mezcla(PERGAMINO, Color.Black, 0.55f) else PERGAMINO
                val hondo = if (oscuro) mezcla(PERGAMINO, Color.Black, 0.86f) else mezcla(PERGAMINO, ROBLE, 0.45f)
                listOf(r.left, r.right - vara).forEach { x ->
                    drawRoundRect(Brush.horizontalGradient(listOf(hondo, claro, hondo), x, x + vara), Offset(x, r.top), Size(vara, r.height), CornerRadius(vara / 2))
                    drawRoundRect(t(ROBLE, oscuro), Offset(x + vara * 0.2f, r.top - 0.5f * u), Size(vara * 0.6f, 2.5f * u), CornerRadius(1 * u))
                    drawRoundRect(t(ROBLE, oscuro), Offset(x + vara * 0.2f, r.bottom - 2 * u), Size(vara * 0.6f, 2.5f * u), CornerRadius(1 * u))
                }
                // La cinta y el sello de lacre al pie.
                val c = Offset(pliego.right - vara - minOf(26 * u, pliego.width * 0.2f), pliego.bottom - 3 * u)
                val cinta = if (oscuro) Color(0xFF6C8FD8) else LAPIZ
                drawLine(cinta, Offset(c.x - 4 * u, pliego.bottom - 9 * u), Offset(c.x - 2 * u, r.bottom), 1.6f * u)
                drawLine(cinta, Offset(c.x + 4 * u, pliego.bottom - 9 * u), Offset(c.x + 3 * u, r.bottom), 1.6f * u)
                drawCircle(lacre, 6.5f * u, c)
                drawCircle(Color.Black.copy(alpha = 0.25f), 4.5f * u, c, style = Stroke(0.6f * u))
                if (e.destacado) {
                    drawCircle(oro, 6.5f * u, c, style = Stroke(1.1f * u))
                    drawPath(cruzPatada(c, 3.3f * u), Color.Black.copy(alpha = 0.32f))
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(mezcla(hoja, LAPIZ, if (oscuro) 0.06f else 0.05f), r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El cielo de lapislázuli, con el filete de oro y las estrellas.
                val cielo = Rect(r.left, r.top, r.right, r.top + 8 * u)
                drawRect(if (oscuro) mezcla(LAPIZ, Color.Black, 0.45f) else LAPIZ, cielo.topLeft, cielo.size)
                drawLine(oro, Offset(r.left, cielo.bottom), Offset(r.right, cielo.bottom), 0.9f * u)
                val paso = maxOf(15 * u, 1f)
                var x = r.left + 8 * u; var k = 0
                val estrellas = Path()
                while (x < r.right - 30 * u && k < 60) {
                    val y = cielo.top + (2.4f + 3.2f * azar(e.semilla + 11, k)) * u
                    estrellas.addPath(estrella(Offset(x, y), (0.9f + 0.6f * azar(e.semilla, k)) * u, 5, 0.45f))
                    x += paso * (0.7f + 0.6f * azar(e.semilla + 3, k)); k++
                }
                drawPath(estrellas, if (oscuro) ORO.copy(alpha = 0.75f) else ORO)
                val luna = Offset(r.right - 16 * u, cielo.center.y)
                drawCircle(if (oscuro) ORO.copy(alpha = 0.9f) else ORO, 2.8f * u, luna)
                if (!e.destacado) drawCircle(if (oscuro) mezcla(LAPIZ, Color.Black, 0.45f) else LAPIZ, 2.4f * u, Offset(luna.x + 1.3f * u, luna.y - 0.6f * u))
                drawPath(forma, if (e.destacado) oro else filo, style = Stroke(if (e.destacado) 1.2f * u else 0.7f * u))
                if (e.destacado) drawRect(oro.copy(alpha = oro.alpha * 0.6f), Offset(r.left + 3 * u, cielo.bottom + 3 * u), Size(r.width - 6 * u, r.bottom - cielo.bottom - 6 * u), style = Stroke(0.5f * u))
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hoja, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El lomo de cuero con sus nervios.
                val ancho = 7 * u
                val cuero = t(Color(0xFF7A2E1E), oscuro).let { if (oscuro) mezcla(it, Color.Black, 0.2f) else it }
                drawRect(cuero, Offset(r.right - ancho, r.top), Size(ancho, r.height))
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.right - ancho, r.top), Offset(r.right - ancho, r.bottom), 0.8f * u)
                val nervios = 4
                for (k in 1..nervios) {
                    val y = r.top + r.height * k / (nervios + 1)
                    drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.right - ancho, y + 0.8f * u), Offset(r.right, y + 0.8f * u), 0.9f * u)
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.25f), Offset(r.right - ancho, y), Offset(r.right, y), 0.9f * u)
                }
                // El broche de latón que cruza a la hoja.
                val y = r.center.y
                drawRoundRect(t(LATON, oscuro), Offset(r.right - ancho - 4 * u, y - 2.2f * u), Size(ancho + 2 * u, 4.4f * u), CornerRadius(1 * u))
                drawCircle(Color.Black.copy(alpha = 0.4f), 0.7f * u, Offset(r.right - ancho - 2.4f * u, y))
                drawPath(forma, filo, style = Stroke(0.7f * u))
                drawRect(oro.copy(alpha = oro.alpha * 0.5f), Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - ancho - 6 * u, r.height - 6 * u), style = Stroke(0.5f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El verdete de los iluminadores (el verde de cobre): el color de lo que entra; aclarado en modo oscuro. */
    private fun verdete(oscuro: Boolean): Color = if (oscuro) Color(0xFF86C29A) else Color(0xFF3B7448)
    /** El folio de la balanza: el elegido, a la luz; el otro, en la sombra del lomo. En modo oscuro, los dos hondos. */
    private fun folio(elegido: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(PERGAMINO, Color(0xFF17110B), if (elegido) 0.74f else 0.88f)
        elegido -> mezcla(PERGAMINO, Color.White, 0.45f)
        else -> mezcla(PERGAMINO, PIEDRA_OSC, 0.34f)
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: el libro de cuentas abierto en su atril de roble, con el gasto en el folio izquierdo y el ingreso en el
     * derecho. El folio elegido queda a la luz, con el filete doble de oro, su rótulo en negrita y la cinta de registro de
     * bermellón colgando sobre su canto; el otro, en la sombra, solo pautado. En medio, el lomo con su pliegue (un degradado).
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(ROBLE, oscuro))
        vetas(Rect(0f, 0f, w, h), VETA.copy(alpha = 0.35f), 11, densidad = 0.8f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), Offset.Zero, Size(w, h), style = Stroke(1 * u))
        val (izq, der) = mitadesMedievo(4 * u, 0f)
        val oro = if (oscuro) ORO.copy(alpha = 0.7f) else mezcla(ORO, TINTA, 0.3f)
        listOf(false to izq, true to der).forEach { (ingreso, r) ->
            val elegido = ingreso == e.ingreso
            val hoja = folio(elegido, oscuro)
            drawRect(hoja, r.topLeft, r.size)
            val pl = plomo(oscuro)
            drawLine(pl, Offset(r.left + 6 * u, r.top + 5 * u), Offset(r.right - 6 * u, r.top + 5 * u), 0.6f * u)
            drawLine(pl, Offset(r.left + 6 * u, r.bottom - 5 * u), Offset(r.right - 6 * u, r.bottom - 5 * u), 0.6f * u)
            // El canto de fuera: donde cuelga la cinta del elegido.
            val xCinta = if (ingreso) r.right - 15 * u else r.left + 15 * u
            if (elegido) {
                drawRect(oro, Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(1 * u))
                drawRect(oro.copy(alpha = oro.alpha * 0.5f), Offset(r.left + 4.5f * u, r.top + 4.5f * u), Size(r.width - 9 * u, r.height - 9 * u), style = Stroke(0.5f * u))
                val a = 3.2f * u; val fin = r.bottom + 2 * u
                drawPath(Path().apply {
                    moveTo(xCinta - a, 0f); lineTo(xCinta + a, 0f); lineTo(xCinta + a, fin); lineTo(xCinta, fin - 4 * u); lineTo(xCinta - a, fin); close()
                }, rubrica(oscuro))
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(xCinta - a * 0.35f, 0f), Offset(xCinta - a * 0.35f, fin - 4 * u), 0.6f * u)
            } else {
                drawLine(rubrica(oscuro).copy(alpha = 0.22f), Offset(xCinta, r.top + 8 * u), Offset(xCinta, r.bottom - 8 * u), 0.7f * u)
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegido) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(r.left + 24 * u, r.top + 6 * u, r.right - 24 * u, r.bottom - 6 * u), LetraExtra.cardo, color, 17f,
                peso = if (elegido) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
        }
        // El lomo: el pliegue hundido entre los dos folios.
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.26f), Color.Transparent),
            startX = w / 2 - 9 * u, endX = w / 2 + 9 * u), Offset(w / 2 - 9 * u, 4 * u), Size(18 * u, h - 8 * u))
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.35f), Offset(w / 2, 4 * u), Offset(w / 2, h - 4 * u), 0.8f * u)
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: la cabecera rubricada del libro de cuentas, una tira de pergamino con dos rayas de bermellón y el
     * mes en rojo al centro; en cada extremo, tras la línea de margen, una plumilla grande que apunta al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        val hoja = hojaPieza(oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(hoja, Offset.Zero, Size(w, h))
        val rojo = rubrica(oscuro)
        listOf(4 * u, h - 4 * u).forEach { y -> drawLine(rojo.copy(alpha = 0.75f), Offset(52 * u, y), Offset(w - 52 * u, y), 0.9f * u) }
        listOf(48 * u, w - 48 * u).forEach { x -> drawLine(plomo(oscuro), Offset(x, 3 * u), Offset(x, h - 3 * u), 0.6f * u) }
        val azul = if (oscuro) Color(0xFF8FA8E0) else LAPIZ
        listOf(56 * u, w - 56 * u).forEach { x ->
            drawPath(Path().apply { moveTo(x, h / 2 - 3 * u); lineTo(x + 2.2f * u, h / 2); lineTo(x, h / 2 + 3 * u); lineTo(x - 2.2f * u, h / 2); close() }, azul.copy(alpha = 0.75f))
        }
        textoEn(medidor, e.titulo, Rect(62 * u, 0f, w - 62 * u, h), LetraExtra.cardo, legibleMedievo(rojo, hoja), 21f, peso = FontWeight.SemiBold, minimo = 12f)
        plumaMes(Offset(24 * u, h / 2), 32 * u, true, hoja, oscuro)
        plumaMes(Offset(w - 24 * u, h / 2), 32 * u, false, hoja, oscuro)
    }

    /** Una plumilla de lado, de [s] de largo con centro en [c]: la punta hacia afuera ([izquierda] o derecha), como en las filas. */
    private fun DrawScope.plumaMes(c: Offset, s: Float, izquierda: Boolean, hueco: Color, oscuro: Boolean) {
        val u = density
        fun p(x: Float, y: Float) = Offset(c.x + (x - 0.5f) * s, c.y + (y - 0.5f) * s)
        scale(if (izquierda) -1f else 1f, 1f, pivot = c) {
            val tinta = if (oscuro) CERA else TINTA
            drawPath(Path().apply {
                p(0.16f, 0.36f).let { moveTo(it.x, it.y) }; p(0.5f, 0.33f).let { lineTo(it.x, it.y) }
                val a = p(0.8f, 0.38f); val b = p(0.98f, 0.5f); quadraticTo(a.x, a.y, b.x, b.y)
                val d = p(0.8f, 0.62f); val f = p(0.5f, 0.67f); quadraticTo(d.x, d.y, f.x, f.y)
                p(0.16f, 0.64f).let { lineTo(it.x, it.y) }; close()
            }, tinta)
            drawLine(hueco, p(0.52f, 0.5f), p(0.93f, 0.5f), 1.1f * u)
            drawCircle(hueco, 0.05f * s, p(0.5f, 0.5f))
            drawRect(rubrica(oscuro), p(0.04f, 0.34f), Size(0.11f * s, 0.32f * s))
        }
    }

    /**
     * El asiento: un renglón del libro, con la raya a punta de plomo abajo y, a 112 dp del canto, el arranque de la doble
     * línea de la columna del importe; al principio de la raya, el signo del asiento: la virgulilla de bermellón del gasto o los dos puntos de verdete
     * del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val pl = plomo(oscuro)
        val y = h - 0.8f * u
        drawLine(pl, Offset(22 * u, y), Offset(w, y), 0.6f * u)
        // La columna del importe: solo su arranque sobre la raya, para no cruzar cifras largas.
        val xc = w - 112 * u
        drawLine(pl, Offset(xc, y - 7 * u), Offset(xc, y), 0.6f * u)
        drawLine(pl, Offset(xc - 2 * u, y - 7 * u), Offset(xc - 2 * u, y), 0.6f * u)
        if (e.ingreso) {
            val v = verdete(oscuro).copy(alpha = 0.8f)
            drawCircle(v, 1.5f * u, Offset(5 * u, y - 1 * u)); drawCircle(v, 1.5f * u, Offset(11 * u, y - 1 * u))
        } else {
            drawPath(Path().apply { moveTo(2 * u, y); quadraticTo(8 * u, y - 4 * u, 12 * u, y - 1 * u); quadraticTo(15 * u, y + 1 * u, 18 * u, y - 2 * u) },
                rubrica(oscuro).copy(alpha = 0.8f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: un calderón de bermellón, la fecha rubricada en versalitas espaciadas y la raya del pautado con su piquete. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val rojo = rubrica(oscuro)
        // La página y la superficie de las tarjetas del tema: el rótulo se lee sobre las dos.
        val fondos = if (oscuro) listOf(Color(0xFF15100A), Color(0xFF1E1710)) else listOf(Color(0xFFEFE3C8), Color(0xFFF6ECD6))
        val c = Offset(5 * u, h / 2)
        drawCircle(rojo, 2.6f * u, Offset(c.x - 0.6f * u, c.y - 2 * u))
        drawLine(rojo, Offset(c.x + 1.4f * u, c.y - 4.6f * u), Offset(c.x + 1.4f * u, c.y + 5 * u), 1.1f * u)
        drawLine(rojo, Offset(c.x + 3.4f * u, c.y - 4.6f * u), Offset(c.x + 3.4f * u, c.y + 5 * u), 1.1f * u)
        drawLine(rojo, Offset(c.x - 0.6f * u, c.y - 4.6f * u), Offset(c.x + 4 * u, c.y - 4.6f * u), 1.1f * u)
        val fin = textoIzqMedievo(medidor, texto, 14 * u, 0f, h, w - 40 * u, LetraExtra.cardo, legibleMedievo(rojo, fondos), 12.5f, FontWeight.Bold, 1.4f)
        if (fin > 14 * u) {
            val y = h / 2 + 3 * u
            drawLine(plomo(oscuro), Offset(fin + 6 * u, y), Offset(w - 6 * u, y), 0.7f * u)
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f), 0.9f * u, Offset(w - 3 * u, y))
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La raya: la junta incisa entre dos hiladas de sillares (el surco y, al lado, su filo de luz o de sombra) y, en medio,
     * una cruz patada pintada de rojo.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val surco = tenueMedievo(if (oscuro) CREMA.copy(alpha = 0.2f) else TINTA.copy(alpha = 0.34f), e)
        val filo = tenueMedievo(if (oscuro) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.7f), e)
        val dy = (if (oscuro) -0.9f else 0.9f) * u
        val hueco = 7.5f * u
        listOf(4 * u to cx - hueco, cx + hueco to w - 4 * u).forEach { (a, b) ->
            drawLine(filo, Offset(a, y + dy), Offset(b, y + dy), 0.7f * u)
            drawLine(surco, Offset(a, y), Offset(b, y), 0.9f * u)
        }
        drawPath(cruzPatada(Offset(cx, y), 3.4f * u), tenueMedievo(if (oscuro) mezcla(ROJO, Color.White, 0.18f) else ROJO, e))
    }

    /** El rojo de la cruz (más claro en modo oscuro, para leerse sobre la noche de la encomienda). */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFEC6A5E) else Color(0xFFA0161C)

    /**
     * El adorno: una raya incisa bajo el texto con una cruz patada labrada en cada punta. Al presionarlo, la caja se rehúnde
     * en la piedra (con la sombra arriba) y las cruces se pintan de rojo.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val hundido = e.presionado && e.habilitado
        if (hundido) {
            val caja = Rect(2 * u, 4 * u, w - 2 * u, h - 4 * u)
            drawRect((if (oscuro) CREMA else TINTA).copy(alpha = 0.07f), caja.topLeft, caja.size)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), caja.topLeft, Size(caja.width, 1.6f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.1f), caja.topLeft, Size(1.4f * u, caja.height))
        }
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 8 * u)
        val surco = apagadoBotonMedievo(if (oscuro) CREMA.copy(alpha = 0.26f) else TINTA.copy(alpha = 0.36f), e, oscuro)
        val filo = apagadoBotonMedievo(if (oscuro) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.7f), e, oscuro)
        val dy = (if (oscuro) -0.8f else 0.8f) * u
        drawLine(filo, Offset(x0 + 4 * u, y + dy), Offset(x1 - 4 * u, y + dy), 0.6f * u)
        drawLine(surco, Offset(x0 + 4 * u, y), Offset(x1 - 4 * u, y), 0.8f * u)
        val cruz = apagadoBotonMedievo(if (hundido) tintaBotonTexto(oscuro) else surco.copy(alpha = surco.alpha * 1.4f), e, oscuro)
        drawPath(cruzPatada(Offset(x0 + 0.5f * u, y), 2.4f * u), cruz)
        drawPath(cruzPatada(Offset(x1 - 0.5f * u, y), 2.4f * u), cruz)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La piedra de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más honda que la del campo. */
    private fun piedraPieza(oscuro: Boolean): Color = if (oscuro) mezcla(SILLAR, Color(0xFF0F0D0B), 0.8f) else mezcla(SILLAR, CREMA, 0.35f)
    /** El rojo de la cruz, aclarado en modo oscuro. */
    private fun rojoCruz(oscuro: Boolean): Color = if (oscuro) mezcla(ROJO, Color.White, 0.22f) else ROJO

    override val margenFila: Dp = 12.dp

    /**
     * La fila: una hilada de sillares. Abajo, la junta incisa (el surco y su filo de luz); al tocarla, la fila se rehúnde en
     * la piedra (con la sombra arriba) y en el margen se pinta una cruz patada roja.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) {
            drawRect((if (oscuro) CREMA else TINTA).copy(alpha = 0.06f))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.12f), Offset.Zero, Size(w, 1.6f * u))
            drawPath(cruzPatada(Offset(5.5f * u, h / 2), 2.8f * u), rojoCruz(oscuro))
        }
        val surco = if (oscuro) CREMA.copy(alpha = 0.14f) else TINTA.copy(alpha = 0.22f)
        val filo = if (oscuro) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.7f)
        drawLine(surco, Offset(0f, h - 1.4f * u), Offset(w, h - 1.4f * u), 0.8f * u)
        drawLine(filo, Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.7f * u)
    }

    /** La flecha: una punta de lanza hacia la derecha, con su cubo y el anillo rojo donde se ata el pendón. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val hierro = if (oscuro) CREMA.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.78f)
        drawPath(Path().apply { moveTo(f(0.36f), f(0.5f)); quadraticTo(f(0.6f), f(0.24f), f(0.95f), f(0.5f)); quadraticTo(f(0.6f), f(0.76f), f(0.36f), f(0.5f)); close() }, hierro)
        drawLine(hierro, Offset(f(0.04f), f(0.5f)), Offset(f(0.4f), f(0.5f)), 1.8f * u)
        drawLine(rojoCruz(oscuro), Offset(f(0.3f), f(0.36f)), Offset(f(0.3f), f(0.64f)), 1.4f * u)
        drawLine((if (oscuro) Color.Black else Color.White).copy(alpha = 0.35f), Offset(f(0.48f), f(0.5f)), Offset(f(0.86f), f(0.5f)), 0.6f * u)
    }

    /** La marca fija: la cruz patada, pintada de rojo si está fija y solo labrada (el contorno) si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.4f
        if (fija) {
            drawPath(cruzPatada(c + Offset(0.6f * u, 0.8f * u), r), Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f))
            drawPath(cruzPatada(c, r), rojoCruz(oscuro))
        } else {
            drawPath(cruzPatada(c, r), (if (oscuro) CREMA else TINTA).copy(alpha = 0.5f), style = Stroke(1 * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA

    /**
     * El vacío: un sillar sin inscribir. La piedra con su bisel suave, unas picaduras de la labra en el borde y la caja
     * rehundida donde iría la inscripción, vacía, con su sombra arriba y su luz abajo.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val bloque = Rect(0f, 0f, w - 1.5f * u, h - 2 * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Offset(1.5f * u, 2 * u), bloque.size)
        drawRect(piedraPieza(oscuro), bloque.topLeft, bloque.size)
        bisel(bloque, Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), 1.6f * u)
        val n = ((bloque.width + bloque.height) / (14 * u)).toInt().coerceIn(6, 60)
        for (j in 0 until n) {
            val a = azar(e.semilla, j); val hondo = (1.2f + 3f * azar(e.semilla, j + 70)) * u
            val p = if (j % 2 == 0) Offset(bloque.width * a, if (j % 4 == 0) hondo else bloque.bottom - hondo)
            else Offset(if (j % 4 == 1) hondo else bloque.right - hondo, bloque.height * a)
            drawCircle(Color.Black.copy(alpha = 0.1f), (0.5f + 0.6f * azar(e.semilla, j + 140)) * u, p)
        }
        val caja = Rect(9 * u, 9 * u, bloque.right - 9 * u, bloque.bottom - 9 * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), caja.topLeft, Size(caja.width, 1.6f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), caja.topLeft, Size(1.4f * u, caja.height))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.3f), Offset(caja.left, caja.bottom - 1 * u), Size(caja.width, 1 * u))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.2f), Offset(caja.right - 1 * u, caja.top), Size(1 * u, caja.height))
    }

    /**
     * Lo vacío: el yelmo de cubo sin nadie dentro. La ranura de la vista, la cruz de los respiraderos, la faja de refuerzo
     * con sus remaches y, pintada al frente, una cruz roja pequeña.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) CREMA.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.74f)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val yelmo = Path().apply {
            moveTo(f(0.2f), f(0.9f)); lineTo(f(0.22f), f(0.3f)); quadraticTo(f(0.24f), f(0.08f), f(0.5f), f(0.06f))
            quadraticTo(f(0.76f), f(0.08f), f(0.78f), f(0.3f)); lineTo(f(0.8f), f(0.9f)); close()
        }
        drawPath(yelmo, (if (oscuro) CREMA else SILLAR).copy(alpha = if (oscuro) 0.08f else 0.35f))
        drawPath(yelmo, l, style = tr)
        // La faja vertical de refuerzo y la ranura de la vista.
        drawLine(l.copy(alpha = l.alpha * 0.6f), Offset(f(0.5f), f(0.07f)), Offset(f(0.5f), f(0.36f)), 0.9f * u)
        drawLine(l, Offset(f(0.25f), f(0.4f)), Offset(f(0.75f), f(0.4f)), 2.4f * u)
        drawLine(l.copy(alpha = l.alpha * 0.6f), Offset(f(0.22f), f(0.48f)), Offset(f(0.78f), f(0.48f)), 0.8f * u)
        // Los respiraderos en cruz, a un lado.
        listOf(Offset(0f, 0f), Offset(0f, -0.06f), Offset(0f, 0.06f), Offset(-0.06f, 0f), Offset(0.06f, 0f)).forEach { d ->
            drawCircle(l, f(0.018f), Offset(f(0.66f + d.x), f(0.66f + d.y)))
        }
        // La cruz pintada y los remaches del canto.
        drawPath(cruzPatada(Offset(f(0.36f), f(0.68f)), f(0.11f)), rojoCruz(oscuro))
        listOf(0.3f, 0.43f, 0.57f, 0.7f).forEach { x -> drawCircle(l, f(0.018f), Offset(f(x), f(0.84f))) }
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA

    /**
     * El aviso: una losa con un medallón labrado en el margen y, dentro, la cruz patada roja; en la mitad de los avisos, el
     * escudo partido de la orden (el jefe negro sobre campo blanco). La junta incisa corre por abajo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(piedraPieza(oscuro))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset.Zero, Size(w, 1.4f * u))
        drawLine(if (oscuro) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 1 * u)
        val c = Offset(15 * u, minOf(h / 2, 19 * u))
        val r = 9 * u
        // El medallón rehundido: la sombra arriba, la luz abajo.
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), r, c)
        drawCircle(piedraPieza(oscuro), r - 0.9f * u, c + Offset(0.3f * u, 0.7f * u))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), 20f, 140f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(0.8f * u))
        if (e.semilla % 2 == 0) {
            drawPath(cruzPatada(c, 6 * u), rojoCruz(oscuro))
        } else {
            val ancho = 5.6f * u; val alto = 7 * u
            val escudo = Path().apply {
                moveTo(c.x - ancho, c.y - alto * 0.8f); lineTo(c.x + ancho, c.y - alto * 0.8f); lineTo(c.x + ancho, c.y)
                quadraticTo(c.x + ancho, c.y + alto * 0.7f, c.x, c.y + alto); quadraticTo(c.x - ancho, c.y + alto * 0.7f, c.x - ancho, c.y); close()
            }
            drawPath(escudo, CREMA)
            drawRect(Color(0xFF1A1714), Offset(c.x - ancho, c.y - alto * 0.8f), Size(2 * ancho, alto * 0.62f))
            drawPath(escudo, if (oscuro) CREMA.copy(alpha = 0.6f) else TINTA.copy(alpha = 0.7f), style = Stroke(0.8f * u))
        }
    }

    override val margenCifra: Dp = 10.dp
    /** El rojo de la cruz, oscurecido como el lacre (más claro en modo oscuro). */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFEC8E7E) else Color(0xFF7E1612)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(CREMA, SILLAR, 0.25f) else mezcla(TINTA, SILLAR, 0.15f)

    /**
     * La cifra: un sillar pequeño con su caja rehundida y, en la esquina de arriba, la marca de cantero del que lo labró (una
     * de cuatro, según la cifra).
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(piedraPieza(oscuro), Offset.Zero, Size(w, h))
        bisel(Rect(0f, 0f, w, h), Color.White.copy(alpha = if (oscuro) 0.05f else 0.28f), Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), 1.4f * u)
        val caja = Rect(5 * u, 5 * u, w - 5 * u, h - 5 * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), caja.topLeft, Size(caja.width, 1.3f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), caja.topLeft, Size(1.2f * u, caja.height))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.26f), Offset(caja.left, caja.bottom - 0.9f * u), Size(caja.width, 0.9f * u))
        // La marca de cantero, incisa.
        val m = Offset(w - 7.5f * u, 6.6f * u); val r = 2f * u
        val inciso = (if (oscuro) CREMA else TINTA).copy(alpha = 0.4f)
        val marca = Path().apply {
            when (((e.semilla % 4) + 4) % 4) {
                0 -> { moveTo(m.x - r, m.y + r); lineTo(m.x, m.y - r); lineTo(m.x + r, m.y + r); moveTo(m.x, m.y - r); lineTo(m.x, m.y + r) }
                1 -> { moveTo(m.x - r, m.y - r); lineTo(m.x + r, m.y + r); moveTo(m.x + r, m.y - r); lineTo(m.x - r, m.y + r); moveTo(m.x - r, m.y - r); lineTo(m.x + r, m.y - r) }
                2 -> { moveTo(m.x - r, m.y - r); lineTo(m.x + r, m.y - r); lineTo(m.x - r, m.y + r); lineTo(m.x + r, m.y + r) }
                else -> { moveTo(m.x, m.y - r); lineTo(m.x, m.y + r); moveTo(m.x - r, m.y); lineTo(m.x + r, m.y); moveTo(m.x - r, m.y + r); lineTo(m.x + r, m.y + r) }
            }
        }
        drawPath(marca, inciso, style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta; en modo oscuro, la crema del manto. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    /** La piedra vieja, la del rótulo labrado. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFC9BDA4) else Color(0xFF574A3A)
    /** El rojo de la cruz, oscurecido como el lacre (aclarado en modo oscuro). */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFEE9585) else Color(0xFF8E1A14)

    /**
     * Las tarjetas de la encomienda:
     * - la cifra de Hoy es un sillar claro con el Beauseant arriba (la banda partida en negro y blanco del estandarte);
     *   destacada, la cruz patada roja se clava en medio de la banda y la piedra se filetea de rojo;
     * - «Te falta hoy» es la orden clavada en la puerta: el pliego de crema con la franja roja arriba, sobre las tablas de
     *   roble y sujeto con cuatro clavos; completa, la cruz de la esquina se vuelve sello;
     * - la noche es la muralla: al pie, las almenas recortadas contra el cielo con estrellas; sellada, una cruz roja en el cielo
     *   y la hoja fileteada;
     * - la tarjeta de Vida es el manto blanco, con el dobladillo cosido y el brazo de la cruz roja asomando por la derecha.
     * Al presionarlas, la piedra se rehúnde (la sombra pasa arriba).
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val rojo = rojoCruz(oscuro)
        val crema = if (oscuro) mezcla(CREMA, Color(0xFF0F0D0B), 0.8f) else CREMA
        val negro = if (oscuro) Color(0xFF050404) else Color(0xFF1E1B18)
        val filo = (if (oscuro) CREMA else TINTA).copy(alpha = if (oscuro) 0.2f else 0.3f)
        fun rehundido(a: Rect) {
            if (!e.presionado) return
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), a.topLeft, Size(a.width, 2.2f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), a.topLeft, Size(1.8f * u, a.height))
        }
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(piedraPieza(oscuro), r.topLeft, r.size)
                bisel(r, Color.White.copy(alpha = if (oscuro) 0.05f else 0.28f), Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), 1.2f * u)
                // El Beauseant: la mitad negra y la mitad blanca, con su orla.
                val banda = Rect(r.left, r.top, r.right, r.top + 5 * u)
                drawRect(negro, banda.topLeft, Size(banda.width / 2, banda.height))
                drawRect(if (oscuro) mezcla(CREMA, Color.Black, 0.35f) else Color(0xFFF6F0E2), Offset(banda.center.x, banda.top), Size(banda.width / 2, banda.height))
                drawLine(Color.Black.copy(alpha = 0.4f), Offset(r.left, banda.bottom), Offset(r.right, banda.bottom), 0.7f * u)
                rehundido(Rect(r.left, banda.bottom, r.right, r.bottom))
                drawPath(forma, if (e.destacado) rojo else filo, style = Stroke(if (e.destacado) 1.4f * u else 0.6f * u))
                if (e.destacado) {
                    val c = Offset(banda.center.x, banda.bottom - 0.5f * u)
                    drawCircle(crema, 5.4f * u, c)
                    drawCircle(rojo, 5.4f * u, c, style = Stroke(0.8f * u))
                    drawPath(cruzPatada(c, 4.2f * u), rojo)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                // Las tablas de la puerta, con su veta y las juntas.
                val puerta = rectMedievo(r)
                sombraTarjetaMedievo(puerta, e, oscuro)
                val roble = if (oscuro) Color(0xFF2E2117) else Color(0xFF6A4A2E)
                drawRect(roble, r.topLeft, r.size)
                vetas(r, Color.Black.copy(alpha = 0.35f), 17, densidad = 0.5f, grosor = 0.6f)
                val tabla = maxOf(22 * u, 1f)
                var y = r.top + tabla; var k = 0
                while (y < r.bottom && k < 30) { drawLine(Color.Black.copy(alpha = 0.4f), Offset(r.left, y), Offset(r.right, y), 0.8f * u); y += tabla; k++ }
                // El pliego clavado, con la franja roja.
                val pliego = Rect(r.left + 4 * u, r.top + 4 * u, r.right - 4 * u, r.bottom - 4 * u)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(pliego.left + 1 * u, pliego.top + 1.4f * u), pliego.size)
                drawRect(crema, pliego.topLeft, pliego.size)
                rehundido(pliego)
                drawRect(rojo, pliego.topLeft, Size(pliego.width, 3 * u))
                drawLine(rojo.copy(alpha = 0.5f), Offset(pliego.left + 3 * u, pliego.bottom - 2.5f * u), Offset(pliego.right - 3 * u, pliego.bottom - 2.5f * u), 0.6f * u)
                val clavo = if (oscuro) Color(0xFF8A847A) else Color(0xFF2B2A2D)
                listOf(Offset(pliego.left + 4 * u, pliego.top + 5.5f * u), Offset(pliego.right - 4 * u, pliego.top + 5.5f * u),
                    Offset(pliego.left + 4 * u, pliego.bottom - 5 * u), Offset(pliego.right - 4 * u, pliego.bottom - 5 * u)).forEach { o ->
                    drawCircle(clavo, 1.5f * u, o); drawCircle(Color.White.copy(alpha = 0.3f), 0.5f * u, Offset(o.x - 0.5f * u, o.y - 0.5f * u))
                }
                val c = Offset(pliego.right - 9 * u, pliego.top + minOf(11 * u, pliego.height / 2))
                if (e.destacado) {
                    drawCircle(rojo, 5.6f * u, c)
                    drawPath(cruzPatada(c, 3.8f * u), crema)
                } else drawPath(cruzPatada(c, 4.6f * u), rojo)
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(crema, r.topLeft, r.size)
                rehundido(r)
                // El cielo de la noche y las almenas recortadas contra él.
                val alto = 11 * u
                val cielo = Rect(r.left, r.bottom - alto, r.right, r.bottom)
                drawRect(if (oscuro) Color(0xFF0B1022) else Color(0xFF1C2747), cielo.topLeft, cielo.size)
                val piedra = if (oscuro) Color(0xFF3A342B) else Color(0xFF8A7E68)
                val muro = Path()
                val paso = maxOf(16 * u, 1f)
                var x = r.left; var k = 0
                muro.moveTo(r.left, r.bottom)
                while (x < r.right && k < 80) {
                    muro.lineTo(x, cielo.top + 4 * u); muro.lineTo(x + paso * 0.5f, cielo.top + 4 * u)
                    muro.lineTo(x + paso * 0.5f, cielo.top + 7.5f * u); muro.lineTo(x + paso, cielo.top + 7.5f * u)
                    x += paso; k++
                }
                muro.lineTo(r.right, cielo.top + 7.5f * u); muro.lineTo(r.right, r.bottom); muro.close()
                clipRect(r.left, r.top, r.right, r.bottom) { drawPath(muro, piedra) }
                val estrellas = Path()
                var j = 0; var xs = r.left + 9 * u
                while (xs < r.right - 6 * u && j < 40) {
                    estrellas.addOval(Rect(Offset(xs, cielo.top + (1.2f + 1.6f * azar(e.semilla, j)) * u), 0.55f * u))
                    xs += maxOf(23 * u, 1f) * (0.7f + 0.6f * azar(e.semilla + 5, j)); j++
                }
                drawPath(estrellas, Color(0xFFF2E8C8))
                // La luna creciente sobre las almenas, a la izquierda.
                val luna = Offset(r.left + 24 * u, cielo.top + 2.4f * u)
                drawCircle(Color(0xFFF2E8C8), 1.9f * u, luna)
                drawCircle(if (oscuro) Color(0xFF0B1022) else Color(0xFF1C2747), 1.6f * u, Offset(luna.x + 0.9f * u, luna.y - 0.4f * u))
                drawLine(Color.Black.copy(alpha = 0.4f), Offset(r.left, cielo.top), Offset(r.right, cielo.top), 0.7f * u)
                drawPath(forma, if (e.destacado) rojo else filo, style = Stroke(if (e.destacado) 1.3f * u else 0.6f * u))
                if (e.destacado) drawPath(cruzPatada(Offset(r.left + 40 * u, cielo.top + 2.2f * u), 2f * u), Color(0xFFE0453A))
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(crema, r.topLeft, r.size)
                rehundido(r)
                // El brazo izquierdo de la cruz roja del manto, que asoma por la derecha.
                val brazo = minOf(9 * u, r.width * 0.08f)
                clipRect(r.right - brazo, r.top, r.right, r.bottom) { drawPath(cruzPatada(Offset(r.right + r.height * 0.32f, r.center.y), r.height * 0.42f), rojo) }
                // El dobladillo, cosido.
                drawLine(filo, Offset(r.left, r.bottom - 4 * u), Offset(r.right - brazo, r.bottom - 4 * u), 0.5f * u)
                drawLine(filo.copy(alpha = filo.alpha * 1.6f), Offset(r.left + 2 * u, r.bottom - 2.2f * u), Offset(r.right - brazo - 2 * u, r.bottom - 2.2f * u), 0.7f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.6f * u)))
                drawPath(forma, filo, style = Stroke(0.6f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El oro de los besantes (las monedas de oro que guardaba y prestaba la Orden): el color de lo que entra. */
    private fun besante(oscuro: Boolean): Color = if (oscuro) Color(0xFFE2C474) else Color(0xFF8A6A22)
    /** La caja rehundida de la balanza: la elegida, de piedra clara a la luz; la otra, en sombra. */
    private fun cajaBalanza(elegida: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(SILLAR, Color(0xFF0F0D0B), if (elegida) 0.7f else 0.86f)
        elegida -> mezcla(CREMA, Color.White, 0.35f)
        else -> mezcla(SILLAR, TINTA, 0.1f)
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: un dintel de sillería con dos cajas rehundidas, la del gasto a la izquierda y la del ingreso a la derecha,
     * separadas por la junta. La caja elegida está a la luz, con la cruz patada roja labrada en su extremo y su rótulo en
     * negrita; la otra queda en sombra, con la cruz apenas incisa.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(if (oscuro) mezcla(SILLAR, Color(0xFF0F0D0B), 0.78f) else SILLAR)
        // El picado de la piedra.
        for (k in 0 until 40) {
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), (0.5f + azar(17, k) * 0.9f) * u, Offset(azar(17, k + 50) * w, azar(17, k + 100) * h))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f), Offset.Zero, Size(w, h), style = Stroke(1 * u))
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.32f), Offset(w / 2, 0f), Offset(w / 2, h), 1.2f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(w / 2 + 1.2f * u, 0f), Offset(w / 2 + 1.2f * u, h), 0.6f * u)
        val (izq, der) = mitadesMedievo(6 * u, 6 * u)
        listOf(false to izq, true to der).forEach { (ingreso, r) ->
            val elegida = ingreso == e.ingreso
            val caja = cajaBalanza(elegida, oscuro)
            drawRect(caja, r.topLeft, r.size)
            // El rehundido: sombra arriba e izquierda, luz abajo y derecha.
            bisel(r, Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f), Color.White.copy(alpha = if (oscuro) 0.08f else 0.45f), 1.6f * u)
            val c = Offset(if (ingreso) r.right - 15 * u else r.left + 15 * u, r.center.y)
            if (elegida) drawPath(cruzPatada(c, 8.5f * u), rojoCruz(oscuro))
            else drawPath(cruzPatada(c, 7.5f * u), (if (oscuro) CREMA else TINTA).copy(alpha = 0.25f), style = Stroke(0.8f * u))
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegida) legibleMedievo(propio, caja) else tenueLegibleMedievo(propio, caja)
            textoEn(medidor, texto, Rect(r.left + 28 * u, r.top + 3 * u, r.right - 28 * u, r.bottom - 3 * u), LetraExtra.cruzada, color, 17f,
                peso = if (elegida) FontWeight.SemiBold else FontWeight.Normal, espaciado = 0.6f, minimo = 10f)
        }
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: una imposta de piedra con dos filetes incisos arriba y abajo y el mes labrado al centro, entre
     * dos cruces patadas rojas; en cada extremo, la punta de un virote de ballesta, labrada y teñida de rojo, que apunta al
     * mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val piedra = piedraPieza(oscuro)
        drawRect(piedra)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset.Zero, Size(w, h), style = Stroke(1 * u))
        val surco = Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f); val luz = Color.White.copy(alpha = if (oscuro) 0.07f else 0.5f)
        listOf(4.5f * u, h - 5.5f * u).forEach { y ->
            drawLine(surco, Offset(48 * u, y), Offset(w - 48 * u, y), 0.8f * u); drawLine(luz, Offset(48 * u, y + 0.9f * u), Offset(w - 48 * u, y + 0.9f * u), 0.6f * u)
        }
        listOf(48 * u, w - 48 * u).forEach { x -> drawLine(surco, Offset(x, 0f), Offset(x, h), 0.9f * u) }
        listOf(60 * u, w - 60 * u).forEach { x -> drawPath(cruzPatada(Offset(x, h / 2), 5 * u), rojoCruz(oscuro)) }
        textoEn(medidor, e.titulo, Rect(70 * u, 0f, w - 70 * u, h), LetraExtra.cruzada, legibleMedievo(if (oscuro) CREMA else TINTA, piedra), 21f,
            peso = FontWeight.SemiBold, espaciado = 1f, minimo = 12f)
        viroteMes(Offset(24 * u, h / 2), 30 * u, true, oscuro)
        viroteMes(Offset(w - 24 * u, h / 2), 30 * u, false, oscuro)
    }

    /** La punta de un virote, de [s] de largo con centro en [c], apuntando a la izquierda o a la derecha: el asta, la punta y las plumas. */
    private fun DrawScope.viroteMes(c: Offset, s: Float, izquierda: Boolean, oscuro: Boolean) {
        val u = density
        scale(if (izquierda) -1f else 1f, 1f, pivot = c) {
            val x0 = c.x - s / 2; val x1 = c.x + s / 2
            val tinta = if (oscuro) CREMA else TINTA
            drawLine(tinta, Offset(x0 + 2 * u, c.y), Offset(x1 - s * 0.3f, c.y), 2 * u, StrokeCap.Round)
            listOf(-1f, 1f).forEach { k ->
                drawLine(tinta.copy(alpha = 0.7f), Offset(x0 + 1 * u, c.y + k * 5 * u), Offset(x0 + s * 0.28f, c.y), 1.4f * u, StrokeCap.Round)
            }
            val punta = Path().apply { moveTo(x1, c.y); lineTo(x1 - s * 0.38f, c.y - s * 0.24f); lineTo(x1 - s * 0.3f, c.y); lineTo(x1 - s * 0.38f, c.y + s * 0.24f); close() }
            translate(0.8f * u, 1 * u) { drawPath(punta, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f)) }
            drawPath(punta, rojoCruz(oscuro))
        }
    }

    /**
     * El asiento: la llaga de la sillería (un surco inciso con su borde de luz) al pie del renglón, con el arranque de la junta
     * de la columna del importe a 112 dp del canto; al principio, la cruz patada roja del gasto o el besante de oro del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 1.2f * u
        val surco = Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f); val luz = Color.White.copy(alpha = if (oscuro) 0.05f else 0.4f)
        drawLine(surco, Offset(16 * u, y), Offset(w, y), 0.8f * u); drawLine(luz, Offset(16 * u, y + 0.9f * u), Offset(w, y + 0.9f * u), 0.5f * u)
        val xc = w - 112 * u
        drawLine(surco, Offset(xc, y - 7 * u), Offset(xc, y), 0.8f * u)
        val c = Offset(6 * u, y - 2.5f * u)
        if (e.ingreso) {
            drawCircle(besante(oscuro).copy(alpha = 0.85f), 3.4f * u, c)
            drawCircle(Color.Black.copy(alpha = 0.25f), 2.3f * u, c, style = Stroke(0.5f * u))
        } else drawPath(cruzPatada(c, 3.8f * u), rojoCruz(oscuro).copy(alpha = 0.85f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: la cruz patada roja, la fecha labrada con letras espaciadas y el surco que sigue hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF12110E), Color(0xFF1B1915)) else listOf(Color(0xFFECE2CC), Color(0xFFFBF8F0))
        drawPath(cruzPatada(Offset(6 * u, h / 2), 5.2f * u), rojoCruz(oscuro))
        val fin = textoIzqMedievo(medidor, texto, 16 * u, 0f, h, w - 40 * u, LetraExtra.cruzada, legibleMedievo(if (oscuro) CREMA else TINTA, fondos),
            12.5f, FontWeight.SemiBold, 1.6f)
        if (fin > 16 * u) {
            val y = h / 2 + 0.5f * u
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.25f), Offset(fin + 6 * u, y), Offset(w, y), 0.8f * u)
            drawLine((if (oscuro) CREMA else Color.White).copy(alpha = if (oscuro) 0.12f else 0.6f), Offset(fin + 6 * u, y + 0.9f * u), Offset(w, y + 0.9f * u), 0.5f * u)
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /** La raya: un hilo de oro y, en medio, dos grecas que se dan la espalda, como en los bordes de las lacas. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val oro = tenueMedievo(if (oscuro) ORO.copy(alpha = 0.52f) else mezcla(ORO, TINTA, 0.3f).copy(alpha = 0.66f), e)
        val s = 5 * u; val hueco = s + 4 * u
        drawLine(oro, Offset(4 * u, y), Offset(cx - hueco, y), 0.8f * u)
        drawLine(oro, Offset(cx + hueco, y), Offset(w - 4 * u, y), 0.8f * u)
        greca(Offset(cx - 1.2f * u, y - s / 2), -1f, 1f, s, oro, 0.8f * u)
        greca(Offset(cx + 1.2f * u, y - s / 2), 1f, 1f, s, oro, 0.8f * u)
        drawCircle(tenueMedievo(if (oscuro) mezcla(LACA, Color.White, 0.2f) else LACA, e), 0.9f * u, Offset(cx - hueco - 2.5f * u, y))
        drawCircle(tenueMedievo(if (oscuro) mezcla(LACA, Color.White, 0.2f) else LACA, e), 0.9f * u, Offset(cx + hueco + 2.5f * u, y))
    }

    /** La laca roja, oscurecida para leerse sobre el papel; en modo oscuro, el oro. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0B04F) else Color(0xFF9A2A20)

    /**
     * El adorno: un hilo de oro bajo el texto que en cada punta se enrosca en una greca. Al presionarlo, la laca roja asoma
     * detrás del texto y el oro se aviva.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) ORO else LACA).copy(alpha = if (oscuro) 0.13f else 0.1f), 3 * u)
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 10 * u)
        val vivo = e.presionado && e.habilitado
        val oro0 = if (oscuro) (if (vivo) mezcla(ORO, Color.White, 0.25f) else ORO.copy(alpha = 0.7f))
        else mezcla(ORO, TINTA, if (vivo) 0.2f else 0.32f).copy(alpha = if (vivo) 1f else 0.8f)
        val oro = apagadoBotonMedievo(oro0, e, oscuro)
        val s = 4.5f * u
        drawLine(oro, Offset(x0 + s, y), Offset(x1 - s, y), 0.8f * u)
        greca(Offset(x0, y), 1f, -1f, s, oro, 0.8f * u)
        greca(Offset(x1, y), -1f, -1f, s, oro, 0.8f * u)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más hondo que el del campo. */
    private fun papelPieza(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF110D0C), 0.8f) else PAPEL
    /** El oro, oscurecido en el claro para verse sobre el papel. */
    private fun oroPieza(oscuro: Boolean): Color = if (oscuro) ORO else mezcla(ORO, TINTA, 0.28f)
    /** El bermellón del sello, aclarado en modo oscuro. */
    private fun sello(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0604E) else BERMELLON

    override val margenFila: Dp = 12.dp

    /**
     * La fila: un hilo de oro al pie con una cuenta en cada cabo. Al tocarla, asoma detrás la laca roja y en el margen cuelga
     * una cuenta de jade de su cordón.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) drawRect((if (oscuro) ORO else LACA).copy(alpha = if (oscuro) 0.1f else 0.08f))
        val oro = oroPieza(oscuro).copy(alpha = if (oscuro) 0.32f else 0.45f)
        val y = h - 0.8f * u
        drawLine(oro, Offset(6 * u, y), Offset(w - 6 * u, y), 0.6f * u)
        drawCircle(oro, 1 * u, Offset(4 * u, y)); drawCircle(oro, 1 * u, Offset(w - 4 * u, y))
        if (e.presionado) {
            drawLine(oroPieza(oscuro), Offset(5.5f * u, 8 * u), Offset(5.5f * u, h / 2 - 2 * u), 0.7f * u)
            drawCircle(if (oscuro) Color(0xFF9CCDB0) else JADE, 2.2f * u, Offset(5.5f * u, h / 2))
            drawCircle(Color.White.copy(alpha = 0.35f), 0.7f * u, Offset(4.8f * u, h / 2 - 0.8f * u))
        }
    }

    /** La flecha: un cordón de oro con su cuenta de jade, rematado en un cheurón de oro. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val oro = oroPieza(oscuro)
        drawLine(oro, Offset(f(0.06f), f(0.5f)), Offset(f(0.84f), f(0.5f)), 1 * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(f(0.56f), f(0.24f)); lineTo(f(0.88f), f(0.5f)); lineTo(f(0.56f), f(0.76f)) }, oro,
            style = Stroke(1.6f * u, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        drawCircle(if (oscuro) Color(0xFF9CCDB0) else Color(0xFF2F5E48), f(0.11f), Offset(f(0.24f), f(0.5f)))
        drawCircle(Color.White.copy(alpha = 0.35f), f(0.035f), Offset(f(0.21f), f(0.46f)))
    }

    /** La marca fija: una moneda de la ruta, redonda con su agujero cuadrado; de oro si está fija, solo el canto si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2); val r = s * 0.4f; val a = s * 0.13f
        if (fija) {
            val moneda = Path().apply {
                fillType = PathFillType.EvenOdd
                addOval(Rect(c, r)); addRect(Rect(c.x - a, c.y - a, c.x + a, c.y + a))
            }
            drawPath(moneda, oroPieza(oscuro))
            drawCircle(Color.Black.copy(alpha = 0.25f), r * 0.78f, c, style = Stroke(0.6f * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(c.x - a - 1.2f * u, c.y - a - 1.2f * u), Size(2 * a + 2.4f * u, 2 * a + 2.4f * u), style = Stroke(0.6f * u))
        } else {
            val tenue = (if (oscuro) MARFIL else TINTA).copy(alpha = 0.5f)
            drawCircle(tenue, r, c, style = Stroke(1.1f * u))
            drawRect(tenue, Offset(c.x - a, c.y - a), Size(2 * a, 2 * a), style = Stroke(1f * u))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) MARFIL else TINTA

    /**
     * El vacío: una hoja de papel en su marco fino de laca negra, con el hilo de laca roja por dentro y una greca pequeña de
     * oro en cada esquina.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(0f, 0f, w - 1.5f * u, h - 2 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(1.5f * u, 2 * u), r.size, CornerRadius(3 * u))
        drawRoundRect(t(NEGRO_LACA, oscuro), Offset.Zero, r.size, CornerRadius(3 * u))
        val hoja = Rect(2 * u, 2 * u, r.right - 2 * u, r.bottom - 2 * u)
        drawRect(papelPieza(oscuro), hoja.topLeft, hoja.size)
        drawRect(LACA.copy(alpha = if (oscuro) 0.5f else 0.35f), Offset(hoja.left + 2.5f * u, hoja.top + 2.5f * u), Size(hoja.width - 5 * u, hoja.height - 5 * u), style = Stroke(0.6f * u))
        val oro = oroPieza(oscuro).copy(alpha = 0.75f); val g = 5 * u; val d = 4.5f * u
        greca(Offset(hoja.left + d, hoja.top + d), 1f, 1f, g, oro, 0.7f * u)
        greca(Offset(hoja.right - d, hoja.top + d), -1f, 1f, g, oro, 0.7f * u)
        greca(Offset(hoja.left + d, hoja.bottom - d), 1f, -1f, g, oro, 0.7f * u)
        greca(Offset(hoja.right - d, hoja.bottom - d), -1f, -1f, g, oro, 0.7f * u)
    }

    /**
     * Lo vacío: el cuenco de la comida ya terminada, con su pie, el borde y la franja de jade, y los palillos de laca roja
     * cruzados encima, con las puntas de oro.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) MARFIL.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.74f)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cuenco = Path().apply { moveTo(f(0.1f), f(0.46f)); quadraticTo(f(0.14f), f(0.84f), f(0.5f), f(0.84f)); quadraticTo(f(0.86f), f(0.84f), f(0.9f), f(0.46f)); close() }
        drawPath(cuenco, (if (oscuro) MARFIL else PAPEL).copy(alpha = if (oscuro) 0.08f else 0.6f))
        // La franja de jade, en zigzag.
        val franja = Path(); var k = 0
        while (k <= 8) { val x = f(0.2f + 0.075f * k); val y = f(if (k % 2 == 0) 0.6f else 0.66f); if (k == 0) franja.moveTo(x, y) else franja.lineTo(x, y); k++ }
        drawPath(franja, if (oscuro) Color(0xFF9CCDB0) else JADE, style = Stroke(1 * u, join = StrokeJoin.Round))
        drawPath(cuenco, l, style = tr)
        drawOval(l, Offset(f(0.1f), f(0.42f)), Size(f(0.8f), f(0.08f)), style = tr)
        drawLine(l, Offset(f(0.38f), f(0.84f)), Offset(f(0.36f), f(0.92f)), tr.width)
        drawLine(l, Offset(f(0.62f), f(0.84f)), Offset(f(0.64f), f(0.92f)), tr.width)
        drawLine(l, Offset(f(0.3f), f(0.92f)), Offset(f(0.7f), f(0.92f)), tr.width)
        // Los palillos.
        val laca = if (oscuro) Color(0xFFE0604E) else LACA
        listOf(Offset(f(0.3f), f(0.42f)) to Offset(f(0.94f), f(0.1f)), Offset(f(0.36f), f(0.44f)) to Offset(f(0.98f), f(0.2f))).forEach { (a, b) ->
            drawLine(laca, a, b, 1.6f * u, StrokeCap.Round)
            drawLine(oroPieza(oscuro), b + (a - b) * 0.18f, b, 1.6f * u, StrokeCap.Round)
        }
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) MARFIL else TINTA

    /**
     * El aviso: una tira de papel con un hilo de oro arriba y, en el margen, el sello de bermellón con su marco y unos trazos
     * de marfil (no son ningún carácter: la impresión de un sello gastado, distinta según el aviso).
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(papelPieza(oscuro))
        drawLine(oroPieza(oscuro).copy(alpha = 0.6f), Offset(0f, 1 * u), Offset(w, 1 * u), 0.8f * u)
        drawLine(oroPieza(oscuro).copy(alpha = 0.6f), Offset(0f, h - 1 * u), Offset(w, h - 1 * u), 0.8f * u)
        val lado = 15 * u
        val o = Offset(8 * u, minOf(h / 2, 18 * u) - lado / 2)
        drawRoundRect(sello(oscuro), o, Size(lado, lado), CornerRadius(1.2f * u))
        val marfil = if (oscuro) Color(0xFF2A1A16) else MARFIL
        drawRect(marfil, o + Offset(1.4f * u, 1.4f * u), Size(lado - 2.8f * u, lado - 2.8f * u), style = Stroke(0.8f * u))
        val trazos = Path()
        for (j in 0 until 3) {
            val y = o.y + (4.2f + j * 3.3f) * u
            val x0 = o.x + (3.5f + 2f * azar(e.semilla, j)) * u; val x1 = o.x + lado - (3.5f + 2f * azar(e.semilla, j + 9)) * u
            trazos.moveTo(x0, y); trazos.lineTo(x1, y)
        }
        val xv = o.x + (5f + 5f * azar(e.semilla, 20)) * u
        trazos.moveTo(xv, o.y + 3.5f * u); trazos.lineTo(xv, o.y + lado - 3.5f * u)
        drawPath(trazos, marfil, style = Stroke(1 * u, cap = StrokeCap.Square))
    }

    override val margenCifra: Dp = 10.dp
    /** La laca roja, oscurecida para leerse sobre el papel; en modo oscuro, el oro. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0B04F) else Color(0xFF9A2A20)
    /** El verde de jade. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CCDB0) else Color(0xFF2F5E48)

    /** La cifra: una placa de laca negra con el filete de oro y el papel liso dentro, como la tapa de una caja de té. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(1.4f * u, 1.8f * u), Size(w, h), CornerRadius(3 * u))
        drawRoundRect(t(NEGRO_LACA, oscuro), Offset.Zero, Size(w, h), CornerRadius(3 * u))
        drawRoundRect(Color.White.copy(alpha = 0.1f), Offset(3 * u, 0.9f * u), Size(w - 6 * u, 1 * u), CornerRadius(0.5f * u))
        val hoja = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        drawRect(papelPieza(oscuro), hoja.topLeft, hoja.size)
        drawRect(oroPieza(oscuro).copy(alpha = 0.8f), Offset(hoja.left - 1.2f * u, hoja.top - 1.2f * u), Size(hoja.width + 2.4f * u, hoja.height + 2.4f * u), style = Stroke(0.7f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta china; en modo oscuro, el marfil. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) MARFIL else TINTA
    /** El verde de jade. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF9CCDB0) else Color(0xFF2F5E48)
    /** La laca roja, oscurecida para leerse sobre el papel; en modo oscuro, el bermellón claro. */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF59C90) else Color(0xFF9A2A20)

    /** Una moneda de la ruta: el bronce redondo con su agujero cuadrado. */
    private fun DrawScope.monedaSeda(c: Offset, r: Float, bronce: Color) {
        val u = density
        drawCircle(bronce, r, c)
        drawCircle(Color.Black.copy(alpha = 0.3f), r * 0.78f, c, style = Stroke(0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.55f), Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.6f))
    }

    /** El sello cuadrado de bermellón (el chop), con su marco y dos trazos blancos. */
    private fun DrawScope.chopSeda(c: Offset, l: Float, color: Color) {
        val u = density
        drawRect(color, Offset(c.x - l / 2, c.y - l / 2), Size(l, l))
        val b = Color.White.copy(alpha = 0.75f)
        drawRect(b, Offset(c.x - l * 0.36f, c.y - l * 0.36f), Size(l * 0.72f, l * 0.72f), style = Stroke(0.5f * u))
        drawLine(b, Offset(c.x - l * 0.2f, c.y - l * 0.12f), Offset(c.x + l * 0.2f, c.y - l * 0.12f), 0.7f * u)
        drawLine(b, Offset(c.x, c.y - l * 0.25f), Offset(c.x, c.y + l * 0.25f), 0.7f * u)
        drawLine(b, Offset(c.x - l * 0.2f, c.y + l * 0.15f), Offset(c.x + l * 0.2f, c.y + l * 0.15f), 0.7f * u)
    }

    /**
     * Las tarjetas del caravasar, de papel liso por dentro:
     * - la cifra de Hoy es una ficha de esquinas ochavadas con el ribete de seda de jade y sus puntos de oro; destacada, el
     *   sello cuadrado de bermellón en la esquina;
     * - «Te falta hoy» es el salvoconducto desplegado entre sus dos varas de laca, con las cenefas de brocado rojo; completo,
     *   lleva el sello;
     * - la noche es el papel con la cuerda de farolillos de seda arriba a la derecha; sellada, los farolillos se encienden;
     * - la tarjeta de Vida es el papel con la sarta de monedas de bronce colgada del cordón rojo, por el canto derecho.
     * Al presionarlas, el papel se tiñe de jade.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val papel = papelPieza(oscuro)
        val toque = (if (oscuro) JADE else mezcla(JADE, PAPEL, 0.3f)).copy(alpha = if (oscuro) 0.12f else 0.18f)
        val laca = t(LACA, oscuro)
        val filo = oroPieza(oscuro)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val c = 5 * u
                val ficha = Path().apply {
                    moveTo(r.left + c, r.top); lineTo(r.right - c, r.top); lineTo(r.right, r.top + c); lineTo(r.right, r.bottom - c)
                    lineTo(r.right - c, r.bottom); lineTo(r.left + c, r.bottom); lineTo(r.left, r.bottom - c); lineTo(r.left, r.top + c); close()
                }
                sombraTarjetaMedievo(ficha, e, oscuro)
                val jade = if (oscuro) mezcla(JADE, Color.Black, 0.45f) else JADE
                drawPath(ficha, jade)
                val dentro = Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.bottom - 3 * u)
                drawRect(papel, dentro.topLeft, dentro.size)
                if (e.presionado) drawRect(toque, dentro.topLeft, dentro.size)
                drawRect(filo.copy(alpha = 0.8f), dentro.topLeft, dentro.size, style = Stroke(0.6f * u))
                // Los puntos de oro del ribete.
                val puntos = Path()
                val paso = maxOf(7 * u, 1f)
                var x = r.left + c + paso / 2; var k = 0
                while (x < r.right - c && k < 80) {
                    puntos.addOval(Rect(Offset(x, r.top + 1.5f * u), 0.5f * u)); puntos.addOval(Rect(Offset(x, r.bottom - 1.5f * u), 0.5f * u)); x += paso; k++
                }
                drawPath(puntos, if (oscuro) ORO.copy(alpha = 0.8f) else Color(0xFFF0DCA0))
                if (e.destacado) chopSeda(Offset(r.right - 7.5f * u, r.bottom - 7.5f * u), 10 * u, sello(oscuro))
            }
            TipoTarjeta.PENDIENTE -> {
                val sale = 4.5f * u
                val hoja = Rect(r.left + sale, r.top + 2 * u, r.right - sale, r.bottom - 2 * u)
                sombraTarjetaMedievo(rectMedievo(hoja), e, oscuro)
                drawRect(papel, hoja.topLeft, hoja.size)
                // Las cenefas de brocado rojo arriba y abajo, con su hilo de oro.
                val ancho = 6 * u
                listOf(hoja.top, hoja.bottom - ancho).forEach { y ->
                    drawRect(laca, Offset(hoja.left, y), Size(hoja.width, ancho))
                    drawLine(filo, Offset(hoja.left, y + 1.2f * u), Offset(hoja.right, y + 1.2f * u), 0.5f * u)
                    drawLine(filo, Offset(hoja.left, y + ancho - 1.2f * u), Offset(hoja.right, y + ancho - 1.2f * u), 0.5f * u)
                    val rombos = Path(); val paso = maxOf(9 * u, 1f)
                    var x = hoja.left + paso; var k = 0
                    while (x < hoja.right - paso / 2 && k < 80) {
                        val m = y + ancho / 2
                        rombos.moveTo(x, m - 1.4f * u); rombos.lineTo(x + 1.4f * u, m); rombos.lineTo(x, m + 1.4f * u); rombos.lineTo(x - 1.4f * u, m); rombos.close()
                        x += paso; k++
                    }
                    drawPath(rombos, filo)
                }
                if (e.presionado) drawRect(toque, Offset(hoja.left, hoja.top + ancho), Size(hoja.width, hoja.height - 2 * ancho))
                // Las dos varas de laca negra, que salen a los lados, con sus remates de oro.
                listOf(hoja.top + 0.8f * u, hoja.bottom - 0.8f * u).forEach { y ->
                    drawLine(if (oscuro) Color(0xFF070505) else NEGRO_LACA, Offset(r.left + 1.5f * u, y), Offset(r.right - 1.5f * u, y), 3.2f * u, StrokeCap.Round)
                    drawCircle(if (oscuro) ORO.copy(alpha = 0.85f) else ORO, 2.3f * u, Offset(r.left + 2.3f * u, y))
                    drawCircle(if (oscuro) ORO.copy(alpha = 0.85f) else ORO, 2.3f * u, Offset(r.right - 2.3f * u, y))
                }
                if (e.destacado) chopSeda(Offset(hoja.right - 14 * u, hoja.center.y), minOf(11 * u, hoja.height - 2 * ancho - 4 * u), sello(oscuro))
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(mezcla(papel, Color(0xFF1F2A55), if (oscuro) 0.08f else 0.06f), r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, filo.copy(alpha = 0.7f), style = Stroke(0.7f * u))
                // La cuerda de los farolillos, que cuelga de la esquina de arriba a la derecha.
                val xa = r.right - minOf(140 * u, r.width * 0.5f); val xb = r.right
                val cuerda = Path().apply { moveTo(xa, r.top); quadraticTo((xa + xb) / 2, r.top + 10 * u, xb, r.top + 1 * u) }
                drawPath(cuerda, (if (oscuro) MARFIL else TINTA).copy(alpha = 0.5f), style = Stroke(0.6f * u))
                listOf(0.22f, 0.5f, 0.78f).forEach { f ->
                    val x = xa + (xb - xa) * f
                    val yc = r.top + 2 * 10 * u * f * (1 - f) + 1 * u * f + 5.5f * u
                    val ancho = 4.6f * u; val alto = 5.8f * u
                    if (e.destacado) {
                        drawCircle(Color(0xFFFFC870).copy(alpha = if (oscuro) 0.16f else 0.26f), 8.5f * u, Offset(x, yc))
                        drawCircle(Color(0xFFFFC870).copy(alpha = if (oscuro) 0.16f else 0.22f), 6.2f * u, Offset(x, yc))
                    }
                    drawOval(if (e.destacado) Color(0xFFE8503A) else laca, Offset(x - ancho, yc - alto / 2), Size(2 * ancho, alto))
                    if (e.destacado) drawOval(Color(0xFFFFD27A).copy(alpha = 0.75f), Offset(x - ancho * 0.45f, yc - alto * 0.3f), Size(ancho * 0.9f, alto * 0.6f))
                    drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, yc - alto / 2), Offset(x, yc + alto / 2), 0.4f * u)
                    drawRect(filo, Offset(x - 1.8f * u, yc - alto / 2 - 1.1f * u), Size(3.6f * u, 1.1f * u))
                    drawRect(filo, Offset(x - 1.8f * u, yc + alto / 2), Size(3.6f * u, 1.1f * u))
                    drawLine(laca, Offset(x, yc + alto / 2 + 0.9f * u), Offset(x, yc + alto / 2 + 3 * u), 0.6f * u)
                }
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(papel, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, filo.copy(alpha = 0.8f), style = Stroke(0.7f * u))
                drawRect(filo.copy(alpha = 0.4f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(0.4f * u))
                // El cordón rojo con la sarta de monedas, por el canto derecho.
                val x = r.right - 6 * u
                val cordon = if (oscuro) Color(0xFFD0503E) else BERMELLON
                drawLine(cordon, Offset(x, r.top), Offset(x, r.top + 5 * u + 3 * 8 * u), 0.8f * u)
                val bronce = if (oscuro) Color(0xFF9A7A3E) else Color(0xFF8A6A30)
                for (k in 0 until 3) monedaSeda(Offset(x, r.top + 8 * u + k * 8 * u), 3.8f * u, bronce)
                drawPath(Path().apply { moveTo(x, r.top + 32 * u); lineTo(x - 1.6f * u, r.top + 38 * u); lineTo(x + 1.6f * u, r.top + 38 * u); close() }, cordon)
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El jade de lo que entra, aclarado en modo oscuro. */
    private fun jadePieza(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FC7A4) else Color(0xFF3F7558)
    /** El papel de la balanza: el elegido, a la luz; el otro, ahumado. */
    private fun papelBalanza(elegido: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(PAPEL, Color(0xFF110D0C), if (elegido) 0.72f else 0.86f)
        elegido -> mezcla(PAPEL, Color.White, 0.45f)
        else -> mezcla(PAPEL, Color(0xFF9A8A6E), 0.36f)
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: la bandeja de laca negra del cambista con dos hojas de papel, el gasto a la izquierda y el ingreso a la
     * derecha, y en medio la moneda de bronce con su agujero cuadrado, de la que sale el cordón rojo hacia la hoja elegida. La
     * elegida está a la luz, con su filete de oro, el sello de bermellón en el extremo y el rótulo en negrita; la otra queda
     * ahumada y sin sello.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(NEGRO_LACA, oscuro))
        // El brillo de la laca, de arriba abajo.
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), Color.Transparent), 0f, h * 0.6f), Offset.Zero, Size(w, h * 0.6f))
        drawRect(t(LACA, oscuro), Offset(1.5f * u, 1.5f * u), Size(w - 3 * u, h - 3 * u), style = Stroke(0.8f * u))
        val (izq, der) = mitadesMedievo(5 * u, 34 * u)
        val oro = oroPieza(oscuro)
        val cordon = sello(oscuro)
        listOf(false to izq, true to der).forEach { (ingreso, r) ->
            val elegido = ingreso == e.ingreso
            val hoja = papelBalanza(elegido, oscuro)
            drawRect(hoja, r.topLeft, r.size)
            val xSello = if (ingreso) r.right - 13 * u else r.left + 13 * u
            if (elegido) {
                drawRect(oro, Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(1 * u))
                chopSeda(Offset(xSello, r.center.y), 13 * u, sello(oscuro))
            } else {
                drawRect(oro.copy(alpha = 0.25f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(0.6f * u))
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegido) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(r.left + 24 * u, r.top + 4 * u, r.right - 24 * u, r.bottom - 4 * u), LetraExtra.almendra, color, 17f,
                peso = if (elegido) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
        }
        // La moneda del medio y su cordón, que va a la hoja elegida.
        val c = Offset(w / 2, h / 2)
        val destino = if (e.ingreso) Offset(der.left - 1 * u, der.top + 9 * u) else Offset(izq.right + 1 * u, izq.top + 9 * u)
        drawPath(Path().apply { moveTo(c.x, c.y - 3 * u); quadraticTo((c.x + destino.x) / 2, c.y - 13 * u, destino.x, destino.y) }, cordon, style = Stroke(1.4f * u, cap = StrokeCap.Round))
        monedaSeda(c, 12 * u, if (oscuro) Color(0xFFB48A4A) else Color(0xFF9A7236))
        drawCircle(oro.copy(alpha = 0.7f), 12 * u, c, style = Stroke(0.7f * u))
        drawLine(cordon, Offset(c.x, c.y + 3.6f * u), Offset(c.x, h - 3 * u), 1.2f * u)
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: una tabla de laca negra con la greca de oro en las esquinas y, en medio, la cartela de papel con
     * el mes; en cada extremo, una punta de flecha de oro sobre la laca que apunta al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(NEGRO_LACA, oscuro))
        drawRect(t(LACA, oscuro), Offset(1.5f * u, 1.5f * u), Size(w - 3 * u, h - 3 * u), style = Stroke(0.8f * u))
        val oro = if (oscuro) ORO else mezcla(ORO, Color.White, 0.1f)
        val s = 7 * u
        greca(Offset(4 * u, 4 * u), 1f, 1f, s, oro, 0.8f * u); greca(Offset(w - 4 * u, 4 * u), -1f, 1f, s, oro, 0.8f * u)
        greca(Offset(4 * u, h - 4 * u), 1f, -1f, s, oro, 0.8f * u); greca(Offset(w - 4 * u, h - 4 * u), -1f, -1f, s, oro, 0.8f * u)
        val cartela = Rect(50 * u, 6 * u, w - 50 * u, h - 6 * u)
        val hoja = papelPieza(oscuro)
        drawRect(hoja, cartela.topLeft, cartela.size)
        drawRect(oroPieza(oscuro), Offset(cartela.left + 2 * u, cartela.top + 2 * u), Size(cartela.width - 4 * u, cartela.height - 4 * u), style = Stroke(0.8f * u))
        textoEn(medidor, e.titulo, cartela.deflate(4 * u), LetraExtra.almendra, legibleMedievo(if (oscuro) MARFIL else TINTA, hoja), 21f, peso = FontWeight.Bold, minimo = 12f)
        listOf(true, false).forEach { izquierda ->
            val c = Offset(if (izquierda) 25 * u else w - 25 * u, h / 2)
            val k = if (izquierda) -1f else 1f
            drawPath(Path().apply {
                moveTo(c.x + k * 10 * u, c.y); lineTo(c.x - k * 6 * u, c.y - 11 * u); lineTo(c.x - k * 2 * u, c.y); lineTo(c.x - k * 6 * u, c.y + 11 * u); close()
            }, oro)
            drawLine(oro, Offset(c.x - k * 2 * u, c.y), Offset(c.x - k * 11 * u, c.y), 1.6f * u, StrokeCap.Round)
        }
    }

    /**
     * El asiento: una raya fina de oro al pie del renglón, con el arranque de la columna del importe a 112 dp del canto; al
     * principio, el sello cuadrado de bermellón del gasto o la moneda de jade del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val oro = oroPieza(oscuro).copy(alpha = 0.45f)
        drawLine(oro, Offset(16 * u, y), Offset(w, y), 0.7f * u)
        drawLine(oro, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.7f * u)
        val c = Offset(6 * u, y - 3 * u)
        if (e.ingreso) monedaSeda(c, 3.6f * u, jadePieza(oscuro).copy(alpha = 0.85f))
        else drawRect(sello(oscuro).copy(alpha = 0.85f), Offset(c.x - 3 * u, c.y - 3 * u), Size(6 * u, 6 * u))
    }

    override val altoRotuloDia: Dp = 24.dp

    /** El rótulo del día: la fecha en bermellón dentro del marco de un sello, y una raya doble de oro hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF0F1426), Color(0xFF161D35)) else listOf(Color(0xFFF5EEDC), Color(0xFFFBF6E9))
        val rojo = legibleMedievo(sello(oscuro), fondos)
        val fin = textoIzqMedievo(medidor, texto, 6 * u, 0f, h, w - 40 * u, LetraExtra.almendra, rojo, 12.5f, FontWeight.Bold, 1.4f)
        if (fin <= 6 * u) return
        drawRect(rojo, Offset(1 * u, 1.5f * u), Size(fin + 4 * u, h - 3 * u), style = Stroke(1.2f * u))
        drawRect(rojo.copy(alpha = 0.5f), Offset(2.8f * u, 3.3f * u), Size(fin + 0.4f * u, h - 6.6f * u), style = Stroke(0.5f * u))
        val oro = oroPieza(oscuro).copy(alpha = 0.6f)
        drawLine(oro, Offset(fin + 10 * u, h / 2 - 1.2f * u), Offset(w, h / 2 - 1.2f * u), 0.7f * u)
        drawLine(oro, Offset(fin + 10 * u, h / 2 + 1.2f * u), Offset(w, h / 2 + 1.2f * u), 0.7f * u)
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * La raya: el filete doble de latón grabado de las laudas y, en medio, un cuadrifolio de latón esmaltado de azul de
     * vidriera.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val laton = tenueMedievo(if (oscuro) LATON.copy(alpha = 0.5f) else mezcla(LATON, TINTA, 0.35f).copy(alpha = 0.62f), e)
        val hueco = 8 * u
        listOf(4 * u to cx - hueco, cx + hueco to w - 4 * u).forEach { (a, b) ->
            drawLine(laton, Offset(a, y - 1.3f * u), Offset(b, y - 1.3f * u), 0.6f * u)
            drawLine(laton, Offset(a, y + 1.3f * u), Offset(b, y + 1.3f * u), 0.6f * u)
        }
        val o = Offset(cx, y)
        drawPath(cuadrifolioPath(o, 4.6f * u), laton)
        drawPath(cuadrifolioPath(o, 2.8f * u), tenueMedievo(if (oscuro) Color(0xFF6C93F0) else AZUL, e))
        drawCircle(laton, 0.7f * u, o)
    }

    /** El azul de vidriera; en modo oscuro, el oro del latón pulido. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE2B34A) else Color(0xFF1F3A9E)

    /**
     * El adorno: un filete doble de latón bajo el texto, rematado en cada punta por un cuadrifolio. Al presionarlo, la luz
     * de la vidriera tiñe de azul el fondo del botón y los cuadrifolios se esmaltan.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) Color(0xFF5C8DFF) else AZUL).copy(alpha = if (oscuro) 0.18f else 0.1f), 2 * u)
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 8 * u)
        val vivo = e.presionado && e.habilitado
        val laton = apagadoBotonMedievo(if (oscuro) LATON.copy(alpha = if (vivo) 0.95f else 0.62f)
            else mezcla(LATON, TINTA, 0.35f).copy(alpha = if (vivo) 0.95f else 0.7f), e, oscuro)
        drawLine(laton, Offset(x0 + 3 * u, y - 0.9f * u), Offset(x1 - 3 * u, y - 0.9f * u), 0.6f * u)
        drawLine(laton, Offset(x0 + 3 * u, y + 0.9f * u), Offset(x1 - 3 * u, y + 0.9f * u), 0.6f * u)
        listOf(Offset(x0, y), Offset(x1, y)).forEach { o ->
            drawPath(cuadrifolioPath(o, 3 * u), laton)
            if (vivo) drawPath(cuadrifolioPath(o, 1.8f * u), if (oscuro) Color(0xFF6C93F0) else AZUL)
        }
    }

    /** Un cuadrifolio de radio [r] en [o] como una sola figura (los cinco círculos unidos, sin dobles transparencias). */
    private fun cuadrifolioPath(o: Offset, r: Float): Path = Path().apply {
        val l = r * 0.5f
        listOf(Offset(0f, -l), Offset(l, 0f), Offset(0f, l), Offset(-l, 0f), Offset.Zero).forEach { d ->
            addOval(Rect(o + d - Offset(l, l), Size(l * 2, l * 2)))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La vitela de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más honda que la del campo. */
    private fun vitelaPieza(oscuro: Boolean): Color = if (oscuro) mezcla(VITELA, Color(0xFF0E0C10), 0.8f) else VITELA
    /** El latón grabado: oscurecido en el claro para verse sobre la vitela. */
    private fun latonPieza(oscuro: Boolean): Color = if (oscuro) LATON.copy(alpha = 0.7f) else mezcla(LATON, TINTA, 0.3f)
    /** El azul de vidriera, encendido en modo oscuro. */
    private fun vidrioAzul(oscuro: Boolean): Color = if (oscuro) Color(0xFF6C93F0) else AZUL
    /** El rubí de vidriera, encendido en modo oscuro. */
    private fun vidrioRubi(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07A8A) else RUBI

    override val margenFila: Dp = 14.dp

    /**
     * La fila: un hilo de latón al pie y, en el margen, una lanceta (la ventanita apuntada de las vidrieras) con su vidrio
     * azul apagado. Al tocarla, la luz entra: la fila se tiñe (de oro en el claro, de azul en el oscuro) y la lanceta se enciende.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) drawRect(if (oscuro) vidrioAzul(true).copy(alpha = 0.14f) else LATON.copy(alpha = 0.14f))
        drawLine(latonPieza(oscuro).copy(alpha = if (oscuro) 0.3f else 0.38f), Offset(4 * u, h - 0.6f * u), Offset(w - 4 * u, h - 0.6f * u), 0.6f * u)
        val alto = minOf(16 * u, h - 16 * u)
        if (alto < 4 * u) return
        val lanceta = arcoApuntado(Rect(4 * u, h / 2 - alto / 2, 9 * u, h / 2 + alto / 2), 3.4f * u)
        drawPath(lanceta, vidrioAzul(oscuro).copy(alpha = if (e.presionado) 0.95f else if (oscuro) 0.3f else 0.22f))
        drawPath(lanceta, latonPieza(oscuro).copy(alpha = if (e.presionado) 1f else 0.55f), style = Stroke(0.6f * u))
    }

    /** La flecha: un astil de latón rematado en una punta de arco apuntado y, en la cola, una bola. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val laton = if (oscuro) LATON else mezcla(LATON, TINTA, 0.4f)
        drawLine(laton, Offset(f(0.16f), f(0.5f)), Offset(f(0.6f), f(0.5f)), 1.3f * u)
        drawPath(Path().apply {
            moveTo(f(0.52f), f(0.24f)); quadraticTo(f(0.7f), f(0.32f), f(0.94f), f(0.5f))
            quadraticTo(f(0.7f), f(0.68f), f(0.52f), f(0.76f)); quadraticTo(f(0.6f), f(0.5f), f(0.52f), f(0.24f)); close()
        }, laton)
        drawCircle(laton, f(0.09f), Offset(f(0.12f), f(0.5f)))
        drawCircle(vidrioAzul(oscuro), f(0.045f), Offset(f(0.12f), f(0.5f)))
    }

    /** La marca fija: un cuadrifolio de latón con su vidrio de rubí si está fija; solo el contorno grabado si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.44f
        if (fija) {
            drawPath(cuadrifolioPath(c, r), if (oscuro) LATON else mezcla(LATON, TINTA, 0.25f))
            drawPath(cuadrifolioPath(c, r * 0.66f), vidrioRubi(oscuro))
            drawCircle(if (oscuro) LATON else mezcla(LATON, TINTA, 0.25f), 1 * u, c)
        } else {
            // Solo el contorno de afuera: el medio círculo de cada lóbulo.
            val l = r * 0.5f
            listOf(-90f, 0f, 90f, 180f).forEach { g ->
                val a = Math.toRadians(g.toDouble())
                val o = c + Offset(l * kotlin.math.cos(a).toFloat(), l * kotlin.math.sin(a).toFloat())
                drawArc((if (oscuro) VITELA else TINTA).copy(alpha = 0.5f), g - 90f, 180f, false, o - Offset(l, l), Size(2 * l, 2 * l), style = Stroke(1 * u))
            }
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) VITELA else TINTA

    /**
     * El vacío: una hoja de vitela con su orla fina de latón y, arriba, una arquería ciega de arcos apuntados, como el friso
     * de una sillería de coro sin santos.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(1.4f * u, 1.8f * u), Size(w - 1.4f * u, h - 1.8f * u))
        drawRect(vitelaPieza(oscuro), Offset.Zero, Size(w - 1.4f * u, h - 1.8f * u))
        val laton = latonPieza(oscuro)
        drawRect(laton, Offset(2.5f * u, 2.5f * u), Size(w - 6.4f * u, h - 6.8f * u), style = Stroke(1 * u))
        val arcos = Path()
        val ancho = maxOf(12 * u, 1f)
        val n = ((w - 16 * u) / ancho).toInt().coerceIn(1, 60)
        val x0 = (w - 1.4f * u - n * ancho) / 2
        for (k in 0 until n) arcos.addPath(arcoApuntado(Rect(x0 + k * ancho + 1.5f * u, 6 * u, x0 + (k + 1) * ancho - 1.5f * u, 14 * u), 5 * u))
        drawPath(arcos, laton.copy(alpha = laton.alpha * 0.45f), style = Stroke(0.6f * u))
        drawLine(laton.copy(alpha = laton.alpha * 0.6f), Offset(x0, 14 * u), Offset(x0 + n * ancho, 14 * u), 0.6f * u)
    }

    /**
     * Lo vacío: una vela apagada en su palmatoria de latón, con la cera corrida, el pabilo negro y un hilo de humo que sube
     * y se enrosca.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) VITELA.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.74f)
        val laton = if (oscuro) LATON else mezcla(LATON, TINTA, 0.25f)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // La palmatoria: el platillo, el asa y el cubo.
        drawOval(laton.copy(alpha = 0.35f), Offset(f(0.18f), f(0.78f)), Size(f(0.64f), f(0.14f)))
        drawOval(laton, Offset(f(0.18f), f(0.78f)), Size(f(0.64f), f(0.14f)), style = tr)
        drawCircle(laton, f(0.07f), Offset(f(0.86f), f(0.8f)), style = Stroke(1.1f * u))
        drawRect(laton, Offset(f(0.38f), f(0.72f)), Size(f(0.24f), f(0.08f)))
        // La vela, con su cera corrida por un lado.
        drawPath(Path().apply {
            moveTo(f(0.41f), f(0.72f)); lineTo(f(0.41f), f(0.34f)); lineTo(f(0.59f), f(0.3f)); lineTo(f(0.59f), f(0.72f))
        }, l, style = tr)
        drawPath(Path().apply { moveTo(f(0.59f), f(0.32f)); quadraticTo(f(0.64f), f(0.4f), f(0.61f), f(0.5f)) }, l.copy(alpha = l.alpha * 0.6f), style = Stroke(1 * u, cap = StrokeCap.Round))
        drawLine(l, Offset(f(0.5f), f(0.32f)), Offset(f(0.51f), f(0.22f)), 1.5f * u, StrokeCap.Round)
        // El humo.
        drawPath(Path().apply {
            moveTo(f(0.51f), f(0.2f)); quadraticTo(f(0.6f), f(0.14f), f(0.53f), f(0.08f)); quadraticTo(f(0.46f), f(0.03f), f(0.56f), f(0.0f))
        }, l.copy(alpha = l.alpha * 0.45f), style = Stroke(1 * u, cap = StrokeCap.Round))
    }

    override val margenAviso: Dp = 28.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) VITELA else TINTA

    /**
     * El aviso: una tira de vitela y, en el margen, una lanceta emplomada de tres vidrios (azul, rubí, oro o al revés, según
     * el aviso), con sus plomos y su marco de latón.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(vitelaPieza(oscuro))
        drawLine(latonPieza(oscuro).copy(alpha = 0.5f), Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 0.8f * u)
        drawLine(latonPieza(oscuro).copy(alpha = 0.5f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.8f * u)
        val alto = minOf(h - 10 * u, 30 * u)
        val r = Rect(8 * u, minOf(h / 2, 21 * u) - alto / 2, 18 * u, minOf(h / 2, 21 * u) + alto / 2)
        val lanceta = arcoApuntado(r, 6 * u)
        val vidrios = listOf(vidrioAzul(oscuro), vidrioRubi(oscuro), if (oscuro) LATON else Color(0xFFB8892B))
        val giro = ((e.semilla % 3) + 3) % 3
        clipPath(lanceta) {
            for (k in 0 until 3) drawRect(vidrios[(k + giro) % 3], Offset(r.left, r.top + r.height * k / 3), Size(r.width, r.height / 3 + 1))
        }
        val plomo = if (oscuro) Color(0xFF0E0C10) else Color(0xFF2A2420)
        for (k in 1 until 3) drawLine(plomo, Offset(r.left, r.top + r.height * k / 3), Offset(r.right, r.top + r.height * k / 3), 0.9f * u)
        drawPath(lanceta, plomo, style = Stroke(1.6f * u))
        drawPath(lanceta, latonPieza(oscuro), style = Stroke(0.6f * u))
    }

    override val margenCifra: Dp = 10.dp
    /** El azul de vidriera; en modo oscuro, el oro del latón pulido. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE2B34A) else AZUL
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFC9C3B4) else mezcla(TINTA, AZUL, 0.25f)

    /**
     * La cifra: una placa de vitela con el remate apuntado (como el de un retablo pequeño), filete de latón grabado y un
     * cuadrifolio diminuto en la punta.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        val placa = arcoApuntado(Rect(0f, 0f, w, h), 7 * u)
        translate(1.4f * u, 1.8f * u) { drawPath(placa, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f)) }
        drawPath(placa, vitelaPieza(oscuro))
        drawPath(placa, latonPieza(oscuro), style = Stroke(1.4f * u))
        drawPath(arcoApuntado(Rect(2.6f * u, 2.6f * u, w - 2.6f * u, h - 2.6f * u), 5.6f * u), latonPieza(oscuro).copy(alpha = 0.45f), style = Stroke(0.5f * u))
        drawPath(cuadrifolioPath(Offset(w / 2, 5.2f * u), 2.2f * u), vidrioAzul(oscuro))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp
    /** La tinta; en modo oscuro y en la bóveda de la noche, la vitela. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro || tipo == TipoTarjeta.NOCHE) VITELA else TINTA
    /** La tinta aguada de la vitela; en la bóveda de la noche, el gris azulado de la piedra a la luz de la luna. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.NOCHE -> Color(0xFFBCC4E0)
        oscuro -> Color(0xFFC9C3B4)
        else -> mezcla(TINTA, AZUL, 0.3f)
    }
    /** El rubí de vidriera; en la bóveda de la noche, el oro de las estrellas. */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.NOCHE -> Color(0xFFE8C468)
        oscuro -> Color(0xFFF2899A)
        else -> Color(0xFFA0102A)
    }

    /**
     * Las tarjetas de la librería del coro:
     * - la cifra de Hoy es la vitela con un friso de vidriera arriba (vidrios azules, rubíes y de oro entre los plomos);
     *   destacada, los vidrios se encienden y la hoja se filetea de rubí;
     * - «Te falta hoy» es la tabla del coro: la vitela en un marco de losa oscura, con la arquería de arcos apuntados en lo
     *   alto; completa, un cuadrifolio de oro en la esquina;
     * - la noche es la bóveda pintada de azul con estrellas de oro (como la de las santas capillas), con el nervio y su clave
     *   arriba; sellada, la clave es de oro y la bóveda se filetea;
     * - la tarjeta de Vida es la página de un libro de horas: la vitela con su orla de tallos y hojas de hiedra por la derecha
     *   y el pie.
     * Al presionarlas, entra la luz azul de las vidrieras.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val vitela = vitelaPieza(oscuro)
        val toque = vidrioAzul(oscuro).copy(alpha = if (oscuro) 0.1f else 0.08f)
        val laton = latonPieza(oscuro)
        val plomo = if (oscuro) Color(0xFF07060A) else Color(0xFF2A2630)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(vitela, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El friso de vidriera: vidrios de colores entre los plomos.
                val friso = Rect(r.left, r.top, r.right, r.top + 6 * u)
                drawRect(plomo, friso.topLeft, friso.size)
                val colores = listOf(vidrioAzul(oscuro), vidrioRubi(oscuro), Color(0xFFD8A73A), vidrioAzul(oscuro), Color(0xFF3E8A5A))
                val paso = maxOf(7 * u, 1f)
                var x = r.left + 0.8f * u; var k = 0
                while (x < r.right - 1 * u && k < 80) {
                    val c = colores[(k + e.semilla) % colores.size]
                    val v = if (e.destacado) c else mezcla(c, plomo, if (oscuro) 0.45f else 0.3f)
                    drawRect(v, Offset(x, friso.top + 0.8f * u), Size(minOf(paso - 1.2f * u, r.right - 0.8f * u - x), friso.height - 1.6f * u))
                    x += paso; k++
                }
                drawPath(forma, if (e.destacado) vidrioRubi(oscuro) else laton.copy(alpha = 0.7f), style = Stroke(if (e.destacado) 1.4f * u else 0.8f * u))
            }
            TipoTarjeta.PENDIENTE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                val losa = t(LOSA_OSCURA, oscuro)
                drawRect(losa, r.topLeft, r.size)
                bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.35f), 1 * u)
                val tabla = Rect(r.left + 4 * u, r.top + 4 * u, r.right - 4 * u, r.bottom - 4 * u)
                drawRect(vitela, tabla.topLeft, tabla.size)
                if (e.presionado) drawRect(toque, tabla.topLeft, tabla.size)
                // La arquería de arcos apuntados en lo alto de la tabla.
                val alto = 9 * u
                val vano = maxOf(14 * u, 1f)
                val n = (tabla.width / vano).toInt().coerceIn(1, 60)
                val ancho = tabla.width / n
                val arcos = Path()
                for (k in 0 until n) arcos.addPath(arcoApuntado(Rect(tabla.left + k * ancho + 1 * u, tabla.top + 1 * u, tabla.left + (k + 1) * ancho - 1 * u, tabla.top + alto), 5 * u))
                drawRect(laton.copy(alpha = 0.25f), tabla.topLeft, Size(tabla.width, alto))
                drawPath(arcos, vitela)
                drawPath(arcos, laton, style = Stroke(0.7f * u))
                drawLine(laton, Offset(tabla.left, tabla.top + alto), Offset(tabla.right, tabla.top + alto), 0.9f * u)
                drawLine(laton.copy(alpha = 0.6f), Offset(tabla.left, tabla.bottom - 2.5f * u), Offset(tabla.right, tabla.bottom - 2.5f * u), 0.6f * u)
                drawRect(Color.Black.copy(alpha = 0.4f), tabla.topLeft, tabla.size, style = Stroke(0.7f * u))
                if (e.destacado) {
                    val c = Offset(tabla.right - 9 * u, tabla.bottom - minOf(9 * u, tabla.height / 2))
                    drawPath(cuadrifolioPath(c, 6.5f * u), if (oscuro) Color(0xFFE2B34A) else Color(0xFFC89A38))
                    drawPath(cuadrifolioPath(c, 6.5f * u), Color.Black.copy(alpha = 0.4f), style = Stroke(0.6f * u))
                    drawCircle(vidrioRubi(oscuro), 2.1f * u, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(if (oscuro) Color(0xFF101A44) else Color(0xFF1B2A6A), r.topLeft, r.size)
                if (e.presionado) drawRect(Color.White.copy(alpha = 0.06f), r.topLeft, r.size)
                // Las estrellas de oro, solo por la orla de la bóveda.
                val oro = Color(0xFFE8C468).copy(alpha = if (oscuro) 0.75f else 0.9f)
                val estrellas = Path()
                val paso = maxOf(22 * u, 1f)
                var x = r.left + 8 * u; var k = 0
                while (x < r.right - 4 * u && k < 60) {
                    estrellas.addPath(estrella(Offset(x, r.top + 5 * u), 2.4f * u, 6, 0.4f))
                    estrellas.addPath(estrella(Offset(x + paso / 2, r.bottom - 5 * u), 2.4f * u, 6, 0.4f))
                    x += paso; k++
                }
                drawPath(estrellas, oro)
                // El filete de piedra y el nervio que baja a la clave.
                drawPath(forma, Color(0xFF9A93A8).copy(alpha = 0.7f), style = Stroke(1.6f * u))
                val clave = Offset(r.right - minOf(130 * u, r.width * 0.45f), r.top + 1.5f * u)
                drawLine(Color(0xFF9A93A8).copy(alpha = 0.6f), Offset(clave.x - 14 * u, r.top), clave, 0.9f * u)
                drawLine(Color(0xFF9A93A8).copy(alpha = 0.6f), Offset(clave.x + 14 * u, r.top), clave, 0.9f * u)
                if (e.destacado) {
                    drawPath(cuadrifolioPath(Offset(clave.x, clave.y + 2 * u), 3.6f * u), Color(0xFFE8C468))
                    drawRect(oro, Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, r.height - 6 * u), style = Stroke(0.6f * u))
                } else drawCircle(Color(0xFF9A93A8), 2.2f * u, Offset(clave.x, clave.y + 1.5f * u))
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(vitela, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, laton.copy(alpha = 0.6f), style = Stroke(0.7f * u))
                // La orla: el tallo de oro por la derecha y el pie, con hojas de hiedra azules y rubíes.
                val xt = r.right - 6 * u; val yt = r.bottom - 6 * u
                val tallo = Path().apply { moveTo(xt, r.top + 6 * u); lineTo(xt, yt); lineTo(r.left + 6 * u, yt) }
                drawPath(tallo, laton, style = Stroke(0.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                fun hoja(c: Offset, dx: Float, dy: Float, color: Color) {
                    drawPath(Path().apply {
                        moveTo(c.x, c.y); quadraticTo(c.x + dx * 0.2f - dy * 0.5f, c.y + dy * 0.2f + dx * 0.5f, c.x + dx, c.y + dy)
                        quadraticTo(c.x + dx * 0.2f + dy * 0.5f, c.y + dy * 0.2f - dx * 0.5f, c.x, c.y); close()
                    }, color)
                }
                val paso = maxOf(11 * u, 1f)
                var y = r.top + 10 * u; var k = 0
                while (y < yt - 4 * u && k < 40) { hoja(Offset(xt, y), if (k % 2 == 0) -4.4f * u else 4.4f * u, 1.8f * u, if (k % 2 == 0) vidrioAzul(oscuro) else vidrioRubi(oscuro)); y += paso; k++ }
                var x = xt - 9 * u; k = 0
                while (x > r.left + 8 * u && k < 60) { hoja(Offset(x, yt), -1.8f * u, if (k % 2 == 0) -4.4f * u else 4.4f * u, if (k % 2 == 0) vidrioRubi(oscuro) else vidrioAzul(oscuro)); x -= paso; k++ }
                drawCircle(if (oscuro) Color(0xFFE2B34A) else Color(0xFFC89A38), 1.8f * u, Offset(xt, yt))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El vidrio verde de las vidrieras: el color de lo que entra (en las marcas de la lista). */
    private fun vidrioVerde(oscuro: Boolean): Color = if (oscuro) Color(0xFF7CCB94) else Color(0xFF2B6E43)
    /** La vitela de la vidriera: la elegida, encendida; la otra, apagada en la penumbra de la losa. */
    private fun vitelaBalanza(elegida: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(VITELA, Color(0xFF0E0C10), if (elegida) 0.7f else 0.86f)
        elegida -> mezcla(VITELA, Color.White, 0.5f)
        else -> mezcla(VITELA, LOSA_OSCURA, 0.3f)
    }

    override val altoBalanza: Dp = 60.dp

    /**
     * La balanza: dos vanos apuntados en un muro de losa, como las lancetas gemelas de un ventanal, con un cuadrifolio de
     * latón en la tracería del medio; el gasto en la de la izquierda y el ingreso en la de la derecha. La elegida está
     * encendida: la vitela clara, el emplomado orlado con el vidrio de su color (el del gasto o el del ingreso) y su luz que se
     * derrama sobre la losa (un degradado); la otra queda apagada, con el vidrio en penumbra.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val losa = t(LOSA_OSCURA, oscuro)
        drawRect(losa)
        for (k in 0 until 30) drawCircle(Color.Black.copy(alpha = 0.22f), (0.5f + azar(23, k)) * u, Offset(azar(23, k + 40) * w, azar(23, k + 80) * h))
        val (izq, der) = mitadesMedievo(5 * u, 26 * u)
        val elegidaR = if (e.ingreso) der else izq
        // La luz de la vidriera encendida sobre la losa.
        val luz = if (e.ingreso) e.colorIngreso else e.colorGasto
        drawRect(Brush.radialGradient(listOf(luz.copy(alpha = if (oscuro) 0.35f else 0.45f), Color.Transparent), elegidaR.center, elegidaR.width * 0.62f),
            Offset.Zero, Size(w, h))
        val plomo = Color(0xFF1A1714)
        listOf(false to izq, true to der).forEach { (ingreso, r) ->
            val elegida = ingreso == e.ingreso
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val vano = arcoApuntado(r, 16 * u)
            drawPath(vano, plomo)
            // El vidrio de la orla: del color de su lado, encendido o en penumbra.
            drawPath(vano, if (elegida) propio else mezcla(propio, plomo, 0.62f), style = Stroke(5 * u, join = StrokeJoin.Miter))
            val dentro = Rect(r.left + 4.5f * u, r.top + 4.5f * u, r.right - 4.5f * u, r.bottom - 4.5f * u)
            val hoja = vitelaBalanza(elegida, oscuro)
            val vitral = arcoApuntado(dentro, 13 * u)
            drawPath(vitral, hoja)
            drawPath(vitral, plomo, style = Stroke(1.2f * u, join = StrokeJoin.Miter))
            drawPath(vano, plomo, style = Stroke(1.2f * u, join = StrokeJoin.Miter))
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val color = if (elegida) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(dentro.left + 10 * u, dentro.top + 11 * u, dentro.right - 10 * u, dentro.bottom - 2 * u), LetraExtra.goudy, color, 17f,
                peso = if (elegida) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
        }
        // El parteluz y su cuadrifolio de latón.
        val lat = if (oscuro) LATON else mezcla(LATON, TINTA, 0.15f)
        drawLine(lat.copy(alpha = 0.6f), Offset(w / 2, h * 0.45f), Offset(w / 2, h - 5 * u), 2 * u)
        cuadrifolio(Offset(w / 2, h * 0.32f), 10 * u, lat)
        cuadrifolio(Offset(w / 2, h * 0.32f), 5 * u, if (e.ingreso) mezcla(e.colorIngreso, plomo, 0.2f) else mezcla(e.colorGasto, plomo, 0.2f))
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: una lauda de latón con su filete grabado y un cuadrifolio en cada esquina de la vitela, el mes al
     * centro; en cada extremo, sobre la losa, una punta de vidrio azul emplomada, con un cuadrifolio de latón detrás, que
     * apunta al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(LOSA_OSCURA, oscuro))
        val lat = if (oscuro) mezcla(LATON, Color.Black, 0.25f) else LATON
        val placa = Rect(48 * u, 3 * u, w - 48 * u, h - 3 * u)
        drawRect(lat, placa.topLeft, placa.size)
        val hoja = vitelaPieza(oscuro)
        val dentro = placa.deflate(4 * u)
        drawRect(hoja, dentro.topLeft, dentro.size)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(placa.left + 2 * u, placa.top + 2 * u), Size(placa.width - 4 * u, placa.height - 4 * u), style = Stroke(0.5f * u))
        listOf(dentro.topLeft, Offset(dentro.right, dentro.top), Offset(dentro.left, dentro.bottom), dentro.bottomRight).forEach { o -> cuadrifolio(o, 5 * u, lat) }
        textoEn(medidor, e.titulo, Rect(dentro.left + 10 * u, dentro.top, dentro.right - 10 * u, dentro.bottom), LetraExtra.goudy,
            legibleMedievo(if (oscuro) VITELA else TINTA, hoja), 21f, minimo = 12f)
        listOf(true, false).forEach { izquierda ->
            val c = Offset(if (izquierda) 24 * u else w - 24 * u, h / 2)
            val k = if (izquierda) -1f else 1f
            val emplomado = if (oscuro) LATON else Color(0xFF1A1714)
            // La punta: un vidrio azul apuntado, con la muesca de la tracería detrás, emplomado.
            val punta = Path().apply {
                moveTo(c.x + k * 14 * u, c.y)
                cubicTo(c.x + k * 6 * u, c.y - 4 * u, c.x - k * 2 * u, c.y - 10 * u, c.x - k * 6 * u, c.y - 12 * u)
                quadraticTo(c.x - k * 1 * u, c.y - 4 * u, c.x - k * 5 * u, c.y)
                quadraticTo(c.x - k * 1 * u, c.y + 4 * u, c.x - k * 6 * u, c.y + 12 * u)
                cubicTo(c.x - k * 2 * u, c.y + 10 * u, c.x + k * 6 * u, c.y + 4 * u, c.x + k * 14 * u, c.y)
                close()
            }
            drawPath(punta, vidrioAzul(oscuro))
            drawPath(punta, emplomado, style = Stroke(1.6f * u, join = StrokeJoin.Round))
            drawLine(emplomado, Offset(c.x - k * 3 * u, c.y), Offset(c.x + k * 9 * u, c.y), 1 * u)
            cuadrifolio(Offset(c.x - k * 12 * u, c.y), 6 * u, latonPieza(oscuro))
        }
    }

    /**
     * El asiento: una raya fina de latón al pie del renglón, con el arranque de la columna del importe a 112 dp del canto; al
     * principio, el rombo de vidrio rubí del gasto o el cuadrifolio de vidrio verde del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val lat = latonPieza(oscuro).copy(alpha = 0.5f)
        drawLine(lat, Offset(16 * u, y), Offset(w, y), 0.7f * u)
        drawLine(lat, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.7f * u)
        val c = Offset(6 * u, y - 3.2f * u)
        if (e.ingreso) cuadrifolio(c, 6.4f * u, vidrioVerde(oscuro).copy(alpha = 0.85f))
        else drawPath(Path().apply { moveTo(c.x, c.y - 3.6f * u); lineTo(c.x + 2.8f * u, c.y); lineTo(c.x, c.y + 3.6f * u); lineTo(c.x - 2.8f * u, c.y); close() },
            vidrioRubi(oscuro).copy(alpha = 0.85f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: un cuadrifolio de latón con su vidrio azul, la fecha en azul de vidriera y el filete de latón hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF0A0F2A), Color(0xFF111842)) else listOf(Color(0xFFEDE7DA), Color(0xFFF7F3EA))
        val c = Offset(7 * u, h / 2)
        cuadrifolio(c, 7 * u, latonPieza(oscuro))
        cuadrifolio(c, 3.6f * u, vidrioAzul(oscuro))
        val fin = textoIzqMedievo(medidor, texto, 18 * u, 0f, h, w - 40 * u, LetraExtra.goudy, legibleMedievo(vidrioAzul(oscuro), fondos), 13f, FontWeight.Normal, 1.5f)
        if (fin > 18 * u) {
            val lat = latonPieza(oscuro)
            drawLine(lat, Offset(fin + 6 * u, h / 2), Offset(w - 6 * u, h / 2), 0.8f * u)
            cuadrifolio(Offset(w - 4 * u, h / 2), 4 * u, lat)
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * La raya: un hilo fino y, en medio, una estrella de ocho puntas de oro con su punto verde, escoltada a cada lado por
     * tres triangulitos de taracea.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val hilo = tenueMedievo(if (oscuro) ORO.copy(alpha = 0.42f) else VERDE.copy(alpha = 0.45f), e)
        val oro = tenueMedievo(if (oscuro) ORO_CLARO else mezcla(ORO, TINTA, 0.22f), e)
        val verde = tenueMedievo(if (oscuro) Color(0xFF4FC1A6) else VERDE, e)
        val hueco = 18 * u
        drawLine(hilo, Offset(4 * u, y), Offset(cx - hueco, y), 0.8f * u)
        drawLine(hilo, Offset(cx + hueco, y), Offset(w - 4 * u, y), 0.8f * u)
        // Los triangulitos: oro y verde alternados, de la estrella hacia afuera.
        val g = 2.2f * u; val paso = 3.6f * u
        for (k in 0 until 3) {
            val col = if (k % 2 == 0) verde else oro
            val d = 7 * u + k * paso
            listOf(cx - d - paso, cx + d).forEach { x ->
                drawPath(Path().apply { moveTo(x, y + g / 2); lineTo(x + paso / 2, y - g / 2); lineTo(x + paso, y + g / 2); close() }, col)
            }
        }
        drawPath(estrella(Offset(cx, y), 4.4f * u, 8, 0.55f), oro)
        drawCircle(verde, 1.1f * u, Offset(cx, y))
    }

    /** El verde de los zócalos; en modo oscuro, el oro. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0B65A) else Color(0xFF1E6B5C)

    /**
     * El adorno: bajo el texto, un hilo fino partido en medio por una estrellita de ocho puntas, como la que corta el
     * renglón de una inscripción en el yeso, con un punto en cada cabo. Al presionarlo, el fondo se tiñe de verde y la
     * estrella se enciende de oro.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) Color(0xFF4FC1A6) else VERDE).copy(alpha = if (oscuro) 0.14f else 0.1f), 6 * u)
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 12 * u); val cx = (x0 + x1) / 2
        val vivo = e.presionado && e.habilitado
        val hilo = apagadoBotonMedievo(if (oscuro) ORO.copy(alpha = 0.5f) else VERDE.copy(alpha = 0.55f), e, oscuro)
        val astro = apagadoBotonMedievo(when {
            vivo -> if (oscuro) ORO_CLARO else mezcla(ORO, TINTA, 0.15f)
            oscuro -> ORO.copy(alpha = 0.8f)
            else -> mezcla(ORO, TINTA, 0.25f).copy(alpha = 0.85f)
        }, e, oscuro)
        val hueco = 4.2f * u
        drawLine(hilo, Offset(x0, y), Offset(cx - hueco, y), 0.7f * u)
        drawLine(hilo, Offset(cx + hueco, y), Offset(x1, y), 0.7f * u)
        drawCircle(hilo, 1 * u, Offset(x0, y)); drawCircle(hilo, 1 * u, Offset(x1, y))
        drawPath(estrella(Offset(cx, y), (if (vivo) 3.2f else 2.8f) * u, 8, 0.5f), astro)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El hueso de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más hondo que el del campo. */
    private fun huesoPieza(oscuro: Boolean): Color = if (oscuro) mezcla(HUESO, Color(0xFF100C0A), 0.8f) else HUESO
    /** El oro: en el claro, oscurecido para verse sobre el hueso. */
    private fun oroPieza(oscuro: Boolean): Color = if (oscuro) ORO_CLARO else mezcla(ORO, TINTA, 0.22f)
    /** El verde de los zócalos, encendido en modo oscuro. */
    private fun verdePieza(oscuro: Boolean): Color = if (oscuro) Color(0xFF4FC1A6) else VERDE
    /** El azul de cobalto, encendido en modo oscuro. */
    private fun azulPieza(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FB0E6) else AZUL

    override val margenFila: Dp = 14.dp

    /**
     * La fila: el hilo verde de un zócalo al pie y, en el margen, un rombo de oro como los que cortan los renglones del yeso.
     * Al tocarla, la fila se tiñe de verde y el rombo se abre en una estrella de ocho puntas.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) drawRect(verdePieza(oscuro).copy(alpha = if (oscuro) 0.12f else 0.08f))
        val hilo = (if (oscuro) ORO else VERDE).copy(alpha = if (oscuro) 0.28f else 0.32f)
        drawLine(hilo, Offset(4 * u, h - 1.8f * u), Offset(w - 4 * u, h - 1.8f * u), 0.5f * u)
        drawLine(hilo, Offset(4 * u, h - 0.5f * u), Offset(w - 4 * u, h - 0.5f * u), 0.5f * u)
        val c = Offset(6.5f * u, h / 2)
        if (e.presionado) drawPath(estrella(c, 3.6f * u, 8, 0.55f), oroPieza(oscuro))
        else drawPath(Path().apply { moveTo(c.x, c.y - 2.4f * u); lineTo(c.x + 1.6f * u, c.y); lineTo(c.x, c.y + 2.4f * u); lineTo(c.x - 1.6f * u, c.y); close() },
            oroPieza(oscuro).copy(alpha = 0.55f))
    }

    /** La flecha: dos cheurones entrelazados, de oro y de verde, como una cinta de lazo que apunta. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val trazo = Stroke(1.6f * u, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        val fondo = if (oscuro) Color(0xFF0B1716) else Color(0xFFF3EBDA)
        drawPath(Path().apply { moveTo(f(0.2f), f(0.18f)); lineTo(f(0.52f), f(0.5f)); lineTo(f(0.2f), f(0.82f)) }, verdePieza(oscuro), style = trazo)
        // El cruce: la cinta de oro pasa por encima, con un filo del fondo para que se lea el entrelazado.
        val oro = Path().apply { moveTo(f(0.48f), f(0.18f)); lineTo(f(0.8f), f(0.5f)); lineTo(f(0.48f), f(0.82f)) }
        drawPath(oro, fondo, style = Stroke(3 * u, join = StrokeJoin.Miter))
        drawPath(oro, oroPieza(oscuro), style = trazo)
    }

    /** La marca fija: la estrella de ocho puntas, de oro con su centro verde si está fija; solo el contorno si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.46f
        val astro = estrella(c, r, 8, 0.765f, giro = -90f)
        if (fija) {
            drawPath(astro, oroPieza(oscuro))
            drawPath(estrella(c, r * 0.5f, 8, 0.765f, giro = -90f), verdePieza(oscuro))
            drawCircle(oroPieza(oscuro), 1 * u, c)
        } else {
            drawPath(astro, (if (oscuro) HUESO else TINTA).copy(alpha = 0.5f), style = Stroke(1 * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) HUESO else TINTA

    /**
     * El vacío: un panel de hueso dentro de su alfiz (el recuadro que enmarca los arcos), de doble filete de oro y verde, con
     * una estrellita de ocho puntas en cada esquina.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(huesoPieza(oscuro), Offset.Zero, Size(w, h))
        val a = 4 * u
        drawRect(oroPieza(oscuro).copy(alpha = 0.8f), Offset(a, a), Size(w - 2 * a, h - 2 * a), style = Stroke(0.9f * u))
        drawRect(verdePieza(oscuro).copy(alpha = 0.55f), Offset(a + 2.4f * u, a + 2.4f * u), Size(w - 2 * a - 4.8f * u, h - 2 * a - 4.8f * u), style = Stroke(0.6f * u))
        listOf(Offset(a, a), Offset(w - a, a), Offset(a, h - a), Offset(w - a, h - a)).forEach { o ->
            drawPath(estrella(o, 3.4f * u, 8, 0.6f), huesoPieza(oscuro))
            drawPath(estrella(o, 3.4f * u, 8, 0.6f), oroPieza(oscuro))
            drawCircle(verdePieza(oscuro), 0.9f * u, o)
        }
    }

    /**
     * Lo vacío: un arco de herradura sin nadie debajo, con sus dovelas alternadas de almagra y hueso, las dos columnas con
     * sus capiteles y el alfiz alrededor.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) HUESO.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.74f)
        val almagra = if (oscuro) Color(0xFFE07A5F) else Color(0xFFB5432A)
        val claro = if (oscuro) HUESO.copy(alpha = 0.85f) else Color(0xFFF7EFDC)
        // Las dovelas: un anillo de herradura partido en tramos alternados.
        val r = f(0.27f); val c = Offset(f(0.5f), f(0.42f)); val n = 9
        val caja = Size(2 * r, 2 * r); val o = Offset(c.x - r, c.y - r)
        for (k in 0 until n) {
            drawArc(if (k % 2 == 0) almagra else claro, 155f + 230f * k / n, 230f / n, false, o, caja, style = Stroke(f(0.1f)))
        }
        drawArc(l, 155f, 230f, false, Offset(c.x - r - f(0.05f), c.y - r - f(0.05f)), Size(2 * r + f(0.1f), 2 * r + f(0.1f)), style = Stroke(0.8f * u))
        drawArc(l, 155f, 230f, false, Offset(c.x - r + f(0.05f), c.y - r + f(0.05f)), Size(2 * r - f(0.1f), 2 * r - f(0.1f)), style = Stroke(0.8f * u))
        // Las columnas, los capiteles y el suelo.
        val yc = c.y + r * 0.42f
        listOf(f(0.27f), f(0.73f)).forEach { x ->
            drawLine(l, Offset(x, yc + f(0.04f)), Offset(x, f(0.9f)), 1.4f * u)
            drawRect(l, Offset(x - f(0.045f), yc), Size(f(0.09f), f(0.04f)))
        }
        drawLine(l, Offset(f(0.1f), f(0.9f)), Offset(f(0.9f), f(0.9f)), 1.2f * u)
        // El alfiz.
        drawRect(oroPieza(oscuro), Offset(f(0.12f), f(0.06f)), Size(f(0.76f), f(0.84f)), style = Stroke(0.9f * u))
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) HUESO else TINTA

    /**
     * El aviso: una tira de hueso con su filete de oro arriba y, en el margen, un alicatado pequeño: la estrella de ocho
     * puntas de cobalto con otra de verde dentro (o al revés, según el aviso) y el centro de hueso.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(huesoPieza(oscuro))
        drawLine(oroPieza(oscuro).copy(alpha = 0.7f), Offset(0f, 0.8f * u), Offset(w, 0.8f * u), 0.9f * u)
        drawLine(verdePieza(oscuro).copy(alpha = 0.45f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.7f * u)
        val c = Offset(14 * u, minOf(h / 2, 19 * u))
        val (fuera, dentro) = if (e.semilla % 2 == 0) azulPieza(oscuro) to verdePieza(oscuro) else verdePieza(oscuro) to azulPieza(oscuro)
        drawPath(estrella(c, 9 * u, 8, 0.66f), fuera)
        drawPath(estrella(c, 9 * u, 8, 0.66f), oroPieza(oscuro), style = Stroke(0.6f * u, join = StrokeJoin.Miter))
        drawPath(estrella(c, 5.4f * u, 8, 0.6f, giro = -67.5f), dentro)
        drawCircle(huesoPieza(oscuro), 1.8f * u, c)
    }

    override val margenCifra: Dp = 10.dp
    /** El verde de los zócalos; en modo oscuro, el oro. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) ORO_CLARO else Color(0xFF1E6B5C)
    /** El azul de cobalto. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FB0E6) else AZUL

    /** La cifra: un azulejo de cuerda seca: el hueso liso dentro, la cenefa de cobalto alrededor y una estrellita de oro en cada esquina. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(azulPieza(oscuro).let { if (oscuro) mezcla(it, Color.Black, 0.45f) else it }, Offset.Zero, Size(w, h))
        drawRect(huesoPieza(oscuro), Offset(3.5f * u, 3.5f * u), Size(w - 7 * u, h - 7 * u))
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(3.5f * u, 3.5f * u), Size(w - 7 * u, h - 7 * u), style = Stroke(0.6f * u))
        listOf(Offset(1.75f * u, 1.75f * u), Offset(w - 1.75f * u, 1.75f * u), Offset(1.75f * u, h - 1.75f * u), Offset(w - 1.75f * u, h - 1.75f * u)).forEach { o ->
            drawPath(estrella(o, 2.6f * u, 8, 0.6f), oroPieza(oscuro))
        }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta; en modo oscuro, el hueso. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) HUESO else TINTA
    /** El azul de cobalto. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF9DBAEA) else AZUL
    /** El verde de los zócalos; en modo oscuro, el oro claro. */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) ORO_CLARO else Color(0xFF175A3E)

    /**
     * Las tarjetas de la sala del palacio, de hueso liso por dentro:
     * - la cifra de Hoy lleva arriba una franja de alicatado (losetas en rombo, verdes, azules y de oro, entre el barro
     *   oscuro); destacada, una estrella de ocho puntas se posa en la franja y la loseta se filetea de verde;
     * - «Te falta hoy» es el paño de yesería: la cenefa de cobalto con su cordón de oro arriba y abajo, y los lados del verde
     *   de los zócalos; completo, la estrella de ocho puntas de oro;
     * - la noche es el zócalo de la noche al pie: la franja honda con su hilera de estrellas de ocho puntas y el creciente;
     *   sellada, junto al creciente se enciende el lucero y la hoja se filetea de oro;
     * - la tarjeta de Vida es la tablilla con el lazo de taracea (dos cintas que se entrelazan) arriba y abajo.
     * Al presionarlas, el hueso se tiñe de verde.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val hueso = huesoPieza(oscuro)
        val toque = verdePieza(oscuro).copy(alpha = if (oscuro) 0.1f else 0.1f)
        val oro = oroPieza(oscuro)
        val barro = if (oscuro) Color(0xFF070606) else NEGRO
        val filo = (if (oscuro) HUESO else TINTA).copy(alpha = if (oscuro) 0.2f else 0.3f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hueso, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // La franja de alicatado: rombos de colores sobre el barro.
                val franja = Rect(r.left, r.top, r.right, r.top + 7 * u)
                drawRect(barro, franja.topLeft, franja.size)
                val colores = listOf(verdePieza(oscuro), azulPieza(oscuro), oro)
                val paso = maxOf(6 * u, 1f)
                var x = r.left + paso / 2; var k = 0
                val m = franja.center.y; val a = 2.6f * u
                while (x < r.right && k < 100) {
                    val c = colores[k % 3]
                    drawPath(Path().apply { moveTo(x, m - a); lineTo(x + a, m); lineTo(x, m + a); lineTo(x - a, m); close() }, c)
                    x += paso; k++
                }
                drawLine(oro, Offset(r.left, franja.bottom), Offset(r.right, franja.bottom), 0.8f * u)
                drawPath(forma, if (e.destacado) verdePieza(oscuro) else filo, style = Stroke(if (e.destacado) 1.5f * u else 0.6f * u))
                if (e.destacado) {
                    val c = Offset(r.center.x, franja.center.y + 1 * u)
                    drawPath(estrella(c, 7.5f * u, 8, 0.6f, giro = -90f + 22.5f), barro)
                    drawPath(estrella(c, 6.3f * u, 8, 0.6f, giro = -90f + 22.5f), oro)
                    drawCircle(verdePieza(oscuro), 1.6f * u, c)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                // Los lados, del verde de los zócalos; arriba y abajo, la cenefa de cobalto.
                drawRect(if (oscuro) mezcla(VERDE, Color.Black, 0.35f) else VERDE, r.topLeft, r.size)
                val lado = 4 * u; val cenefa = 6 * u
                val pano = Rect(r.left + lado, r.top + cenefa, r.right - lado, r.bottom - cenefa)
                drawRect(hueso, pano.topLeft, pano.size)
                if (e.presionado) drawRect(toque, pano.topLeft, pano.size)
                val cobalto = if (oscuro) mezcla(AZUL, Color.Black, 0.3f) else AZUL
                val cuentas = Path()
                listOf(r.top, r.bottom - cenefa).forEach { y ->
                    drawRect(cobalto, Offset(r.left, y), Size(r.width, cenefa))
                    drawLine(oro, Offset(r.left, y + 0.8f * u), Offset(r.right, y + 0.8f * u), 0.5f * u)
                    drawLine(oro, Offset(r.left, y + cenefa - 0.8f * u), Offset(r.right, y + cenefa - 0.8f * u), 0.5f * u)
                    val paso = maxOf(4.5f * u, 1f)
                    var x = r.left + 3 * u; var k = 0
                    while (x < r.right - 2 * u && k < 120) { cuentas.addOval(Rect(Offset(x, y + cenefa / 2), if (k % 3 == 0) 1.1f * u else 0.6f * u)); x += paso; k++ }
                }
                drawPath(cuentas, oro)
                drawRect(Color.Black.copy(alpha = 0.35f), pano.topLeft, pano.size, style = Stroke(0.6f * u))
                if (e.destacado) {
                    val c = Offset(pano.right - 12 * u, pano.center.y)
                    val rr = minOf(8 * u, pano.height / 2 - 1 * u)
                    drawPath(estrella(c, rr, 8, 0.62f, giro = -90f + 22.5f), oro)
                    drawPath(estrella(c, rr, 8, 0.62f, giro = -90f + 22.5f), Color.Black.copy(alpha = 0.35f), style = Stroke(0.5f * u))
                    drawCircle(verdePieza(oscuro), rr * 0.32f, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hueso, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El zócalo de la noche, con su hilera de estrellas y el creciente.
                val alto = 10 * u
                val zocalo = Rect(r.left, r.bottom - alto, r.right, r.bottom)
                val noche = if (oscuro) Color(0xFF06100F) else Color(0xFF123634)
                drawRect(noche, zocalo.topLeft, zocalo.size)
                drawLine(oro, Offset(r.left, zocalo.top), Offset(r.right, zocalo.top), 1 * u)
                drawLine(oro.copy(alpha = 0.5f), Offset(r.left, zocalo.top + 1.8f * u), Offset(r.right, zocalo.top + 1.8f * u), 0.4f * u)
                val paso = maxOf(18 * u, 1f)
                var x = r.left + 9 * u; var k = 0
                val estrellas = Path()
                while (x < r.right - 30 * u && k < 60) { estrellas.addPath(estrella(Offset(x, zocalo.center.y + 0.8f * u), 2.9f * u, 8, 0.55f)); x += paso; k++ }
                drawPath(estrellas, oro.copy(alpha = 0.85f))
                val luna = Offset(r.right - 16 * u, zocalo.center.y + 0.8f * u)
                drawCircle(ORO_CLARO, 3.2f * u, luna)
                drawCircle(noche, 2.8f * u, Offset(luna.x + 1.5f * u, luna.y - 0.5f * u))
                if (e.destacado) {
                    drawPath(estrella(Offset(luna.x + 2.6f * u, luna.y - 0.3f * u), 1.5f * u, 5, 0.45f), ORO_CLARO)
                    drawRect(oro, Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, zocalo.top - r.top - 5 * u), style = Stroke(0.8f * u))
                }
                drawPath(forma, filo, style = Stroke(0.6f * u))
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hueso, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El lazo: dos cintas que se cruzan, de oro y de verde, arriba y abajo.
                val onda = maxOf(14 * u, 1f); val amp = 1.8f * u
                listOf(r.top + 4.5f * u, r.bottom - 4.5f * u).forEach { y ->
                    listOf(0f to oro, 3.1416f to verdePieza(oscuro)).forEach { (fase, c) ->
                        val p = Path()
                        val n = (r.width / maxOf(1.5f * u, 1f)).toInt().coerceIn(4, 400)
                        for (s in 0..n) {
                            val x = r.left + 3 * u + (r.width - 6 * u) * s / n
                            val yy = y + amp * kotlin.math.sin(fase + (x - r.left) / onda * 6.2832f)
                            if (s == 0) p.moveTo(x, yy) else p.lineTo(x, yy)
                        }
                        drawPath(p, c, style = Stroke(0.9f * u))
                    }
                }
                listOf(Offset(r.left, r.top), Offset(r.right - 3 * u, r.top), Offset(r.left, r.bottom - 3 * u), Offset(r.right - 3 * u, r.bottom - 3 * u)).forEach { o ->
                    drawRect(azulPieza(oscuro), o, Size(3 * u, 3 * u))
                }
                drawPath(forma, filo, style = Stroke(0.6f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El almagra (el rojo de tierra de los zócalos): el color de lo que sale, aclarado en modo oscuro. */
    private fun almagra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE48A6E) else Color(0xFF9A3A22)
    /** La tablilla de hueso de la balanza: la elegida, a la luz; la otra, en la sombra. */
    private fun huesoBalanza(elegida: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(HUESO, Color(0xFF100C0A), if (elegida) 0.7f else 0.86f)
        elegida -> mezcla(HUESO, Color.White, 0.45f)
        else -> mezcla(HUESO, CEDRO, 0.22f)
    }

    override val altoBalanza: Dp = 62.dp

    /**
     * La balanza: el astil de cedro del almotacén (el fiel de pesos y medidas del zoco) con su estrella de ocho puntas en el
     * fiel, y de cada brazo cuelga una tablilla de hueso: el gasto a la izquierda, el ingreso a la derecha. La elegida pesa:
     * el astil se inclina hacia ella, cuelga más baja, a la luz, con su filete de oro y el rótulo en negrita; la otra sube,
     * en sombra.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val baja = 4 * u
        val yAstil = 6 * u
        // El astil inclinado: el brazo elegido baja [baja].
        val dy = if (e.ingreso) baja / 2 else -baja / 2
        val a = Offset(8 * u, yAstil - dy); val b = Offset(w - 8 * u, yAstil + dy)
        val cedro = t(CEDRO, oscuro)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), a + Offset(0f, 1.6f * u), b + Offset(0f, 1.6f * u), 3.6f * u, StrokeCap.Round)
        drawLine(cedro, a, b, 3.6f * u, StrokeCap.Round)
        drawLine(VETA.copy(alpha = 0.4f), a, b, 0.6f * u)
        val oro = oroPieza(oscuro)
        listOf(false, true).forEach { ingreso ->
            val elegida = ingreso == e.ingreso
            val x0 = if (ingreso) w / 2 + 10 * u else 6 * u
            val x1 = if (ingreso) w - 6 * u else w / 2 - 10 * u
            val arriba = if (elegida) 15 * u else 15 * u - baja
            val r = Rect(x0, arriba, x1, arriba + h - 19 * u)
            // Los cordones de oro, del astil a las esquinas de la tablilla.
            listOf(r.left + 14 * u, r.right - 14 * u).forEach { x ->
                val f = x / w
                val yA = a.y + (b.y - a.y) * f
                drawLine(oro.copy(alpha = 0.85f), Offset(x, yA), Offset(x, r.top), 1 * u)
                drawCircle(oro, 1.6f * u, Offset(x, r.top))
            }
            val hoja = huesoBalanza(elegida, oscuro)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else if (elegida) 0.22f else 0.12f), Offset(r.left + 1.2f * u, r.top + 1.6f * u), r.size)
            drawRect(hoja, r.topLeft, r.size)
            drawRect(t(VETA, oscuro).copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(0.8f * u))
            if (elegida) {
                drawRect(oro, Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(1 * u))
                listOf(Offset(r.left + 2.5f * u, r.top + 2.5f * u), Offset(r.right - 2.5f * u, r.top + 2.5f * u), Offset(r.left + 2.5f * u, r.bottom - 2.5f * u),
                    Offset(r.right - 2.5f * u, r.bottom - 2.5f * u)).forEach { o -> drawPath(estrella(o, 3.4f * u, 8, 0.55f, -90f), oro) }
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegida) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(r.left + 10 * u, r.top + 3 * u, r.right - 10 * u, r.bottom - 3 * u), LetraExtra.amiri, color, 17f,
                peso = if (elegida) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
        }
        // El fiel: la columnilla y la estrella de ocho puntas.
        val c = Offset(w / 2, yAstil)
        drawLine(cedro, c, Offset(w / 2, h - 6 * u), 2.4f * u)
        drawPath(Path().apply { moveTo(w / 2 - 8 * u, h - 2 * u); lineTo(w / 2 + 8 * u, h - 2 * u); lineTo(w / 2 + 3 * u, h - 7 * u); lineTo(w / 2 - 3 * u, h - 7 * u); close() }, cedro)
        drawPath(estrella(c, 7.5f * u, 8, 0.55f, -90f), oro)
        drawPath(estrella(c, 7.5f * u, 8, 0.55f, -90f), t(VETA, oscuro), style = Stroke(0.6f * u))
        drawCircle(if (e.ingreso) verdePieza(oscuro) else almagra(oscuro), 2.2f * u, c)
    }

    override val altoMes: Dp = 52.dp

    /**
     * El encabezado del mes: una faja de hueso entre dos filetes de taracea (triangulitos de ébano y hueso), con el mes en
     * medio; en cada extremo, sobre el cedro, una punta de lazo de oro (dos cheurones encajados) que apunta al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(CEDRO, oscuro))
        vetas(Rect(0f, 0f, w, h), VETA.copy(alpha = 0.35f), 5, densidad = 0.7f)
        val hoja = huesoPieza(oscuro)
        val faja = Rect(46 * u, 8 * u, w - 46 * u, h - 8 * u)
        drawRect(hoja, faja.topLeft, faja.size)
        // La taracea: una fila de triangulitos arriba y otra abajo.
        val negro = t(NEGRO, oscuro); val claro = if (oscuro) mezcla(HUESO, Color(0xFF100C0A), 0.45f) else HUESO
        listOf(1.5f * u to 5 * u, h - 6.5f * u to h - 1.5f * u).forEach { (y0, y1) ->
            drawRect(claro, Offset(faja.left, y0), Size(faja.width, y1 - y0))
            val paso = maxOf(5 * u, 1f)
            var x = faja.left; var k = 0
            val tri = Path()
            while (x + paso <= faja.right && k < 200) {
                tri.moveTo(x, y1); tri.lineTo(x + paso / 2, y0); tri.lineTo(x + paso, y1); tri.close(); x += paso; k++
            }
            drawPath(tri, negro)
        }
        val oro = oroPieza(oscuro)
        drawRect(oro, faja.topLeft, faja.size, style = Stroke(0.8f * u))
        textoEn(medidor, e.titulo, faja.deflate(3 * u), LetraExtra.amiri, legibleMedievo(if (oscuro) HUESO else TINTA, hoja), 21f, minimo = 12f)
        listOf(true, false).forEach { izquierda ->
            val c = Offset(if (izquierda) 23 * u else w - 23 * u, h / 2)
            val k = if (izquierda) -1f else 1f
            listOf(0f, -7f).forEach { d ->
                drawPath(Path().apply {
                    moveTo(c.x + k * (d - 3) * u, c.y - 11 * u); lineTo(c.x + k * (d + 8) * u, c.y); lineTo(c.x + k * (d - 3) * u, c.y + 11 * u)
                }, if (d == 0f) oro else oro.copy(alpha = 0.6f), style = Stroke(3 * u, cap = StrokeCap.Square, join = StrokeJoin.Miter))
            }
        }
    }

    /**
     * El asiento: un filete fino de oro al pie del renglón, con el arranque de la columna del importe a 112 dp del canto; al
     * principio, el rombo de almagra del gasto o la estrella de ocho puntas verde del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val oro = oroPieza(oscuro).copy(alpha = 0.45f)
        drawLine(oro, Offset(16 * u, y), Offset(w, y), 0.7f * u)
        drawLine(oro, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.7f * u)
        val c = Offset(6 * u, y - 3.4f * u)
        if (e.ingreso) drawPath(estrella(c, 4 * u, 8, 0.6f, -90f), verdePieza(oscuro).copy(alpha = 0.85f))
        else drawPath(Path().apply { moveTo(c.x, c.y - 3.6f * u); lineTo(c.x + 3 * u, c.y); lineTo(c.x, c.y + 3.6f * u); lineTo(c.x - 3 * u, c.y); close() },
            almagra(oscuro).copy(alpha = 0.85f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: la estrella de ocho puntas de oro, la fecha en azul y un filete de taracea menuda hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF0B1716), Color(0xFF112220)) else listOf(Color(0xFFF3EBDA), Color(0xFFFBF6EA))
        val oro = oroPieza(oscuro)
        drawPath(estrella(Offset(6 * u, h / 2), 6 * u, 8, 0.55f, -90f), oro)
        val fin = textoIzqMedievo(medidor, texto, 16 * u, 0f, h, w - 40 * u, LetraExtra.amiri, legibleMedievo(azulPieza(oscuro), fondos), 13f, FontWeight.Normal, 1.5f)
        if (fin <= 16 * u) return
        val paso = maxOf(5 * u, 1f)
        var x = fin + 6 * u; var k = 0
        val tri = Path(); val y0 = h / 2 - 2 * u; val y1 = h / 2 + 2 * u
        while (x + paso <= w && k < 200) { tri.moveTo(x, y1); tri.lineTo(x + paso / 2, y0); tri.lineTo(x + paso, y1); tri.close(); x += paso; k++ }
        drawPath(tri, oro.copy(alpha = 0.5f))
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La raya: la puntada del cordón cosido en el fieltro y, en medio, el cuerno de carnero: dos volutas que se abren desde
     * el centro, en rojo.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val cordon = tenueMedievo(if (oscuro) CIELO.copy(alpha = 0.42f) else AZUL.copy(alpha = 0.55f), e)
        val rojo = tenueMedievo(if (oscuro) Color(0xFFF08A6A) else ROJO, e)
        val r = 2.3f * u; val yb = y + 0.8f * u
        val puntada = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.4f * u))
        drawLine(cordon, Offset(4 * u, y), Offset(cx - 2 * r - 4 * u, y), 1.1f * u, pathEffect = puntada)
        drawLine(cordon, Offset(cx + 2 * r + 4 * u, y), Offset(w - 4 * u, y), 1.1f * u, pathEffect = puntada)
        val trazo = Stroke(1.1f * u, cap = StrokeCap.Round)
        drawArc(rojo, 0f, -270f, false, Offset(cx - 2 * r, yb - r), Size(2 * r, 2 * r), style = trazo)
        drawArc(rojo, 180f, 270f, false, Offset(cx, yb - r), Size(2 * r, 2 * r), style = trazo)
        drawLine(rojo, Offset(cx, yb), Offset(cx, yb + 2.4f * u), 1.1f * u, StrokeCap.Round)
    }

    /** El azul del cielo eterno. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF6FB1FF) else Color(0xFF1F5FA8)

    /**
     * El adorno: un cordón torcido bajo el texto que en cada punta se enrosca en una voluta, como el cuerno de carnero. Al
     * presionarlo, se forma detrás del texto la almohadilla de fieltro y el cordón se vuelve rojo.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) CIELO else AZUL).copy(alpha = if (oscuro) 0.14f else 0.1f), 20 * u)
        val y = renglonBotonMedievo()
        val x0 = 13 * u; val x1 = maxOf(w - 13 * u, x0 + 8 * u)
        val vivo = e.presionado && e.habilitado
        val cordon = apagadoBotonMedievo(when {
            vivo -> if (oscuro) Color(0xFFF08A6A) else ROJO
            oscuro -> CIELO.copy(alpha = 0.6f)
            else -> AZUL.copy(alpha = 0.7f)
        }, e, oscuro)
        drawLine(cordon, Offset(x0, y), Offset(x1, y), 1.2f * u)
        // La torsión del cordón: unas vueltas más oscuras.
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(x0, y), Offset(x1, y), 1.2f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1.8f * u)))
        val r = 1.8f * u; val trazo = Stroke(1f * u, cap = StrokeCap.Round)
        drawArc(cordon, 90f, 270f, false, Offset(x0 - r, y - 2 * r), Size(2 * r, 2 * r), style = trazo)
        drawArc(cordon, 90f, -270f, false, Offset(x1 - r, y - 2 * r), Size(2 * r, 2 * r), style = trazo)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El fieltro de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más hondo que el del campo. */
    private fun fieltroPieza(oscuro: Boolean): Color = if (oscuro) mezcla(FIELTRO, Color(0xFF0F0B09), 0.8f) else FIELTRO
    /** El cordón azul (el cielo en modo oscuro). */
    private fun cordonPieza(oscuro: Boolean): Color = if (oscuro) CIELO else AZUL
    /** El rojo de la puerta, encendido en modo oscuro. */
    private fun rojoPieza(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO
    /** El oro: en el claro, tostado para verse sobre el fieltro. */
    private fun oroPieza(oscuro: Boolean): Color = if (oscuro) ORO else mezcla(ORO, ROJO_OSC, 0.3f)

    override val margenFila: Dp = 12.dp

    /**
     * La fila: la puntada del cordón cosido en el fieltro, al pie. Al tocarla, el fieltro se tiñe, la puntada se vuelve roja
     * y en el margen se enrosca un cuerno de carnero.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) drawRect((if (oscuro) CIELO else ORO).copy(alpha = if (oscuro) 0.1f else 0.14f))
        val puntada = (if (e.presionado) rojoPieza(oscuro) else cordonPieza(oscuro)).copy(alpha = if (e.presionado) 0.8f else if (oscuro) 0.3f else 0.38f)
        drawLine(puntada, Offset(4 * u, h - 1 * u), Offset(w - 4 * u, h - 1 * u), 1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.4f * u)))
        if (e.presionado) {
            val r = 1.6f * u; val c = Offset(6 * u, h / 2)
            val trazo = Stroke(1 * u, cap = StrokeCap.Round)
            drawArc(rojoPieza(oscuro), 0f, -270f, false, Offset(c.x - 2 * r, c.y - r), Size(2 * r, 2 * r), style = trazo)
            drawArc(rojoPieza(oscuro), 180f, 270f, false, Offset(c.x, c.y - r), Size(2 * r, 2 * r), style = trazo)
            drawLine(rojoPieza(oscuro), Offset(c.x, c.y), Offset(c.x, c.y + 3 * u), 1 * u, StrokeCap.Round)
        }
    }

    /** La flecha: una flecha de arquero de la estepa, con su punta de hierro y las plumas rojas en la cola. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val astil = if (oscuro) CREMA.copy(alpha = 0.75f) else TINTA.copy(alpha = 0.72f)
        drawLine(astil, Offset(f(0.06f), f(0.5f)), Offset(f(0.72f), f(0.5f)), 1.1f * u)
        drawPath(Path().apply { moveTo(f(0.96f), f(0.5f)); lineTo(f(0.68f), f(0.32f)); lineTo(f(0.74f), f(0.5f)); lineTo(f(0.68f), f(0.68f)); close() }, astil)
        val pluma = rojoPieza(oscuro)
        listOf(0.08f, 0.2f).forEach { x ->
            drawLine(pluma, Offset(f(x), f(0.28f)), Offset(f(x + 0.12f), f(0.5f)), 1.2f * u, StrokeCap.Round)
            drawLine(pluma, Offset(f(x), f(0.72f)), Offset(f(x + 0.12f), f(0.5f)), 1.2f * u, StrokeCap.Round)
        }
    }

    /**
     * La marca fija: el toono, la corona de la ger vista desde abajo (el aro y sus radios); de oro con los radios rojos si
     * está fija, solo el trazo si no.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.42f
        val radios = Path()
        for (k in 0 until 8) {
            val a = Math.toRadians(k * 45.0 + 22.5)
            val cs = kotlin.math.cos(a).toFloat(); val sn = kotlin.math.sin(a).toFloat()
            radios.moveTo(c.x + cs * r * 0.32f, c.y + sn * r * 0.32f); radios.lineTo(c.x + cs * r * 0.92f, c.y + sn * r * 0.92f)
        }
        if (fija) {
            drawCircle(oroPieza(oscuro), r, c)
            drawCircle(if (oscuro) Color(0xFF0A1220) else FIELTRO, r * 0.3f, c)
            drawPath(radios, rojoPieza(oscuro), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            drawCircle(rojoPieza(oscuro), r * 0.3f, c, style = Stroke(1 * u))
        } else {
            val tenue = (if (oscuro) CREMA else TINTA).copy(alpha = 0.5f)
            drawCircle(tenue, r, c, style = Stroke(1.1f * u))
            drawCircle(tenue, r * 0.3f, c, style = Stroke(0.9f * u))
            drawPath(radios, tenue, style = Stroke(0.8f * u))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA

    /**
     * El vacío: una pieza de fieltro redondeada con su pelusa en el borde y el cordón cosido alrededor, a puntadas, como una
     * alfombra de la ger sin nadie sentado.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(1.4f * u, 1.8f * u), Size(w, h), CornerRadius(12 * u))
        drawRoundRect(fieltroPieza(oscuro), Offset.Zero, Size(w, h), CornerRadius(12 * u))
        val n = ((w + h) / (9 * u)).toInt().coerceIn(8, 90)
        for (j in 0 until n) {
            val a = 0.08f + 0.84f * azar(e.semilla, j); val b = (0.8f + 2.4f * azar(e.semilla, j + 300)) * u
            val p = when (j % 4) { 0 -> Offset(w * a, b); 1 -> Offset(w - b, h * a); 2 -> Offset(w * a, h - b); else -> Offset(b, h * a) }
            drawCircle((if (oscuro) Color.White else Color.Black).copy(alpha = 0.06f), (0.5f + 0.7f * azar(e.semilla, j + 600)) * u, p)
        }
        drawRoundRect(cordonPieza(oscuro).copy(alpha = 0.5f), Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), CornerRadius(8 * u),
            style = Stroke(1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 2.4f * u))))
    }

    /**
     * Lo vacío: el esqueleto de una ger sin fieltro: el muro de celosía (khana), las varas del techo que suben al toono y la
     * puerta roja, abierta a nadie.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) CREMA.copy(alpha = 0.78f) else TINTA.copy(alpha = 0.74f)
        val tr = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val y0 = f(0.56f); val y1 = f(0.88f)
        // La celosía: diagonales en los dos sentidos.
        val celosia = Path()
        for (k in 0..4) {
            val x = f(0.1f + 0.2f * k)
            if (k < 4) { celosia.moveTo(x, y1); celosia.lineTo(x + f(0.2f), y0) }
            if (k > 0) { celosia.moveTo(x, y1); celosia.lineTo(x - f(0.2f), y0) }
        }
        drawPath(celosia, l.copy(alpha = l.alpha * 0.55f), style = Stroke(0.7f * u))
        drawLine(l, Offset(f(0.1f), y0), Offset(f(0.9f), y0), tr.width)
        drawLine(l, Offset(f(0.1f), y0), Offset(f(0.1f), y1), tr.width)
        drawLine(l, Offset(f(0.9f), y0), Offset(f(0.9f), y1), tr.width)
        // Las varas del techo, al toono.
        val toono = Offset(f(0.5f), f(0.2f))
        val varas = Path()
        for (k in 0..6) { val x = f(0.1f + 0.8f * k / 6f); varas.moveTo(x, y0); varas.lineTo(toono.x + (x - toono.x) * 0.16f, toono.y + f(0.03f)) }
        drawPath(varas, l, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        drawCircle(oroPieza(oscuro), f(0.08f), toono, style = Stroke(1.3f * u))
        // La puerta y el suelo.
        drawRect(rojoPieza(oscuro), Offset(f(0.42f), f(0.64f)), Size(f(0.16f), y1 - f(0.64f)))
        drawRect(oroPieza(oscuro), Offset(f(0.42f), f(0.64f)), Size(f(0.16f), y1 - f(0.64f)), style = Stroke(0.7f * u))
        drawLine(l, Offset(f(0.02f), y1), Offset(f(0.98f), y1), tr.width)
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA

    /**
     * El aviso: una tira de fieltro con su puntada arriba y abajo y, en el margen, el cuerno de carnero bordado en rojo (dos
     * volutas que se abren); en la mitad de los avisos, el toono de oro.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(fieltroPieza(oscuro))
        val puntada = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.4f * u))
        drawLine(cordonPieza(oscuro).copy(alpha = 0.5f), Offset(3 * u, 2.5f * u), Offset(w - 3 * u, 2.5f * u), 0.9f * u, pathEffect = puntada)
        drawLine(cordonPieza(oscuro).copy(alpha = 0.5f), Offset(3 * u, h - 2.5f * u), Offset(w - 3 * u, h - 2.5f * u), 0.9f * u, pathEffect = puntada)
        val c = Offset(15 * u, minOf(h / 2, 19 * u))
        if (e.semilla % 2 == 0) {
            val r = 3.6f * u; val trazo = Stroke(1.6f * u, cap = StrokeCap.Round)
            val rojo = rojoPieza(oscuro)
            drawArc(rojo, 0f, -270f, false, Offset(c.x - 2 * r, c.y - 1.4f * r), Size(2 * r, 2 * r), style = trazo)
            drawArc(rojo, 180f, 270f, false, Offset(c.x, c.y - 1.4f * r), Size(2 * r, 2 * r), style = trazo)
            drawLine(rojo, Offset(c.x, c.y - 0.4f * r), Offset(c.x, c.y + 1.6f * r), 1.6f * u, StrokeCap.Round)
            drawCircle(rojo, 0.9f * u, Offset(c.x - r, c.y - 0.4f * r)); drawCircle(rojo, 0.9f * u, Offset(c.x + r, c.y - 0.4f * r))
        } else {
            val r = 8 * u
            drawCircle(oroPieza(oscuro), r, c, style = Stroke(1.8f * u))
            drawCircle(oroPieza(oscuro), r * 0.32f, c, style = Stroke(1.2f * u))
            val radios = Path()
            for (k in 0 until 8) {
                val a = Math.toRadians(k * 45.0)
                val cs = kotlin.math.cos(a).toFloat(); val sn = kotlin.math.sin(a).toFloat()
                radios.moveTo(c.x + cs * r * 0.32f, c.y + sn * r * 0.32f); radios.lineTo(c.x + cs * r, c.y + sn * r)
            }
            drawPath(radios, rojoPieza(oscuro), style = Stroke(1.1f * u))
        }
    }

    override val margenCifra: Dp = 10.dp
    /** El azul del cielo eterno. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) CIELO else Color(0xFF1F5FA8)
    /** El rojo de la puerta. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO

    /** La cifra: un remiendo de fieltro acolchado, redondeado, con el cordón azul cosido alrededor a puntadas y una borla roja. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(1.4f * u, 1.8f * u), Size(w, h), CornerRadius(9 * u))
        drawRoundRect(fieltroPieza(oscuro), Offset.Zero, Size(w, h), CornerRadius(9 * u))
        drawRoundRect(cordonPieza(oscuro).copy(alpha = 0.75f), Offset(3.5f * u, 3.5f * u), Size(w - 7 * u, h - 7 * u), CornerRadius(6 * u),
            style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.2f * u))))
        drawCircle(rojoPieza(oscuro), 1.5f * u, Offset(w - 6 * u, 6 * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta; en modo oscuro, la crema del fieltro. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    /** El azul del cielo eterno. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) CIELO else Color(0xFF1F5592)
    /** El rojo de la puerta (encendido en modo oscuro). */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF59478) else Color(0xFF9A2616)

    /**
     * Las tarjetas de la ger, de fieltro liso por dentro:
     * - la cifra de Hoy es una paiza (la tablilla de paso del kan): las esquinas de arriba muy redondas, el canto de oro y el
     *   agujero con su cordón; destacada, el canto y el cordón son rojos;
     * - «Te falta hoy» es la puerta pintada de la ger: el marco naranja con la greca azul arriba y abajo; completa, el sol y
     *   la luna de oro a la derecha;
     * - la noche es el toono (la corona del techo por donde sale el humo y se ve el cielo) asomando arriba, con sus radios y
     *   las estrellas; sellada, la luna llena se ve por él;
     * - la tarjeta de Vida es un shyrdak: el fieltro con los dientes de sierra cosidos, rojos arriba y azules abajo.
     * Al presionarlas, el fieltro se tiñe de cielo.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val fieltro = fieltroPieza(oscuro)
        val toque = cordonPieza(oscuro).copy(alpha = if (oscuro) 0.1f else 0.1f)
        val filo = (if (oscuro) CREMA else TINTA).copy(alpha = if (oscuro) 0.2f else 0.3f)
        val naranja = if (oscuro) mezcla(NARANJA, Color.Black, 0.35f) else NARANJA
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val rr = minOf(16 * u, r.width / 4, r.height / 3)
                val paiza = Path().apply {
                    addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(rr), CornerRadius(rr), CornerRadius(3 * u), CornerRadius(3 * u)))
                }
                sombraTarjetaMedievo(paiza, e, oscuro)
                val canto = if (e.destacado) rojoPieza(oscuro) else oroPieza(oscuro)
                drawPath(paiza, canto)
                val dentro = Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u)
                val hueco = Path().apply {
                    addRoundRect(androidx.compose.ui.geometry.RoundRect(dentro, CornerRadius(rr - 2.6f * u), CornerRadius(rr - 2.6f * u), CornerRadius(1.5f * u), CornerRadius(1.5f * u)))
                }
                drawPath(hueco, fieltro)
                if (e.presionado) drawPath(hueco, toque)
                drawPath(hueco, Color.Black.copy(alpha = 0.3f), style = Stroke(0.5f * u))
                // El agujero de arriba y el cordón que se va por el borde.
                val o = Offset(r.center.x, r.top + 6 * u)
                val cordon = if (e.destacado) rojoPieza(oscuro) else cordonPieza(oscuro)
                drawLine(cordon, o, Offset(o.x - 3 * u, r.top + 0.3f * u), 1.1f * u, StrokeCap.Round)
                drawLine(cordon, o, Offset(o.x + 3 * u, r.top + 0.3f * u), 1.1f * u, StrokeCap.Round)
                drawCircle(canto, 3.4f * u, o)
                drawCircle(if (oscuro) Color(0xFF0A0806) else Color(0xFF2A1E16), 1.8f * u, o)
            }
            TipoTarjeta.PENDIENTE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(naranja, r.topLeft, r.size)
                val lado = 4 * u; val banda = 7 * u
                val tabla = Rect(r.left + lado, r.top + banda, r.right - lado, r.bottom - banda)
                drawRect(fieltro, tabla.topLeft, tabla.size)
                if (e.presionado) drawRect(toque, tabla.topLeft, tabla.size)
                // La greca azul (el alhamhar) arriba y abajo, de trazo cuadrado.
                val azul = if (oscuro) Color(0xFF5A86C0) else AZUL
                listOf(r.top + banda / 2, r.bottom - banda / 2).forEach { y ->
                    val p = Path(); val paso = maxOf(6 * u, 1f); val a = 1.6f * u
                    var x = r.left + 2 * u; var k = 0
                    p.moveTo(x, y + a)
                    while (x < r.right - 2 * u - paso && k < 120) {
                        p.lineTo(x, y - a); p.lineTo(x + paso / 2, y - a); p.lineTo(x + paso / 2, y + a); p.lineTo(x + paso, y + a)
                        x += paso; k++
                    }
                    drawPath(p, azul, style = Stroke(1.1f * u, join = StrokeJoin.Miter))
                }
                drawRect(Color.Black.copy(alpha = 0.35f), tabla.topLeft, tabla.size, style = Stroke(0.7f * u))
                if (e.destacado) {
                    val c = Offset(tabla.right - 12 * u, tabla.center.y + 1.5f * u)
                    val rr = minOf(5 * u, tabla.height / 4)
                    val oro = if (oscuro) ORO else mezcla(ORO, ROJO_OSC, 0.15f)
                    drawCircle(oro, rr, c)
                    drawCircle(oro, rr * 0.9f, Offset(c.x, c.y - rr * 1.9f))
                    drawCircle(fieltro, rr * 0.8f, Offset(c.x, c.y - rr * 2.3f))
                    drawCircle(rojoPieza(oscuro), rr * 0.4f, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(fieltro, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, filo, style = Stroke(0.6f * u))
                // El toono: el aro rojo, el cielo de la noche dentro, los radios y las varas del techo.
                val rr = 13 * u
                val c = Offset(r.right - minOf(128 * u, r.width * 0.42f), r.top)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    for (k in 0..8) {
                        val a = Math.toRadians(180.0 * k / 8).toFloat()
                        val d = Offset(kotlin.math.cos(a), kotlin.math.sin(a))
                        drawLine(rojoPieza(oscuro).copy(alpha = 0.55f), c + d * (rr + 1 * u), c + d * (rr + 7 * u), 1 * u)
                    }
                    drawCircle(if (oscuro) Color(0xFF050A16) else Color(0xFF16233F), rr, c)
                    if (e.destacado) drawCircle(Color(0xFFF2E6B8), 4.5f * u, Offset(c.x + 4 * u, c.y + 6.5f * u))
                    listOf(Offset(-7f, 4f), Offset(-3f, 9f), Offset(6f, 3f), Offset(1f, 5f)).forEach { o -> drawCircle(Color(0xFFF0E6CC), 0.5f * u, c + o * u) }
                    for (k in 0..4) {
                        val a = Math.toRadians(180.0 * k / 4).toFloat()
                        val d = Offset(kotlin.math.cos(a), kotlin.math.sin(a))
                        drawLine(rojoPieza(oscuro), c, c + d * rr, 1 * u)
                    }
                    drawCircle(rojoPieza(oscuro), rr, c, style = Stroke(2 * u))
                    drawCircle(oroPieza(oscuro), rr + 1.4f * u, c, style = Stroke(0.6f * u))
                }
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(fieltro, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // Los dientes de sierra cosidos: rojos arriba, azules abajo.
                val paso = maxOf(8 * u, 1f); val alto = 4 * u
                listOf(true, false).forEach { arriba ->
                    val y0 = if (arriba) r.top + 1.5f * u else r.bottom - 1.5f * u
                    val s = if (arriba) 1f else -1f
                    val p = Path(); p.moveTo(r.left + 1.5f * u, y0)
                    var x = r.left + 1.5f * u; var k = 0
                    while (x < r.right - 1.5f * u - paso / 2 && k < 120) { p.lineTo(x + paso / 2, y0 + s * alto); p.lineTo(x + paso, y0); x += paso; k++ }
                    p.lineTo(x, y0); p.close()
                    drawPath(p, if (arriba) rojoPieza(oscuro) else cordonPieza(oscuro).let { if (oscuro) mezcla(it, Color.Black, 0.25f) else it })
                }
                drawRect(filo, Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u),
                    style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u))))
                drawPath(forma, filo, style = Stroke(0.6f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El fieltro de la balanza: el paño elegido, blanco y a la luz; el otro, ahumado por el fuego del hogar. */
    private fun fieltroBalanza(elegido: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(FIELTRO, Color(0xFF0F0B09), if (elegido) 0.7f else 0.86f)
        elegido -> mezcla(FIELTRO, Color.White, 0.5f)
        else -> mezcla(FIELTRO, Color(0xFF8E8064), 0.32f)
    }

    /** Un cuerno de carnero: la espiral que se enrosca desde [c], de radio [r], hacia la derecha ([sx] = 1) o la izquierda (-1). */
    private fun cuernoMongol(c: Offset, r: Float, sx: Float): Path = Path().apply {
        val n = 28
        for (k in 0..n) {
            val f = k / n.toFloat()
            val a = (-0.5f + f * 1.6f) * Math.PI.toFloat() * 2
            val rr = r * (1 - f * 0.78f)
            val p = Offset(c.x + sx * rr * kotlin.math.cos(a), c.y + rr * kotlin.math.sin(a))
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: una tira de fieltro acolchado con el cordón azul cosido en doble filete, partida en dos paños: el gasto a
     * la izquierda y el ingreso a la derecha; en la costura del medio, los dos cuernos de carnero enfrentados. El paño elegido
     * está blanco y a la luz, con el cuerno rojo aplicado en su extremo y el jadag (la banda de seda azul de las ofrendas)
     * cruzándole la esquina; el otro queda ahumado y liso.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(ROJO_OSC, oscuro))
        val (izq, der) = mitadesMedievo(3 * u, 22 * u)
        val cordon = cordonPieza(oscuro)
        listOf(false to izq, true to der).forEach { (ingreso, r) ->
            val elegido = ingreso == e.ingreso
            val hoja = fieltroBalanza(elegido, oscuro)
            drawRect(hoja, r.topLeft, r.size)
            // El acolchado: puntadas cortas en rombos, muy tenues.
            val punt = Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f)
            val paso = maxOf(14 * u, 1f)
            var x = r.left + paso / 2; var k = 0
            while (x < r.right && k < 60) { drawLine(punt, Offset(x - 3 * u, r.top + 4 * u), Offset(x + 3 * u, r.top + 7 * u), 0.6f * u); x += paso; k++ }
            // El cordón cosido en doble filete.
            drawRect(cordon.copy(alpha = if (elegido) 0.9f else 0.35f), Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, r.height - 6 * u), style = Stroke(1.4f * u))
            drawRect(cordon.copy(alpha = if (elegido) 0.5f else 0.2f), Offset(r.left + 5.5f * u, r.top + 5.5f * u), Size(r.width - 11 * u, r.height - 11 * u),
                style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.6f * u))))
            val xc = if (ingreso) r.right - 16 * u else r.left + 16 * u
            if (elegido) {
                // El jadag cruzando la esquina de fuera.
                val k2 = if (ingreso) 1f else -1f
                val ex = if (ingreso) r.right else r.left
                drawPath(Path().apply {
                    moveTo(ex - k2 * 22 * u, r.top); lineTo(ex - k2 * 12 * u, r.top); lineTo(ex, r.top + 12 * u); lineTo(ex, r.top + 22 * u); close()
                }, if (oscuro) mezcla(CIELO, Color.Black, 0.25f) else CIELO)
                drawPath(cuernoMongol(Offset(xc, r.center.y + 4 * u), 7 * u, -k2), rojoPieza(oscuro), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegido) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(r.left + 28 * u, r.top + 7 * u, r.right - 28 * u, r.bottom - 7 * u), LetraExtra.filosofo, color, 17f,
                peso = if (elegido) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
        }
        // Los dos cuernos enfrentados en la costura.
        val oro = oroPieza(oscuro)
        drawPath(cuernoMongol(Offset(w / 2 - 4 * u, h / 2), 6 * u, -1f), oro, style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawPath(cuernoMongol(Offset(w / 2 + 4 * u, h / 2), 6 * u, 1f), oro, style = Stroke(1.8f * u, cap = StrokeCap.Round))
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: el dintel rojo de la puerta de la ger, con la greca de martillo (el alkhan khee) de oro arriba y
     * abajo y, en medio, la cartela de fieltro con el mes; en cada extremo, una punta de flecha de cordón azul y oro que apunta
     * al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(t(if (oscuro) ROJO_OSC else ROJO, oscuro))
        val oro = if (oscuro) ORO else mezcla(ORO, Color.White, 0.15f)
        // La greca de martillo: escalones de oro arriba y abajo.
        listOf(2.5f * u to 1f, h - 2.5f * u to -1f).forEach { (y, s) ->
            val paso = maxOf(8 * u, 1f)
            var x = 2 * u; var k = 0
            val p = Path().apply { moveTo(x, y) }
            while (x + paso <= w - 2 * u && k < 100) {
                p.lineTo(x + paso * 0.25f, y); p.lineTo(x + paso * 0.25f, y + s * 3 * u); p.lineTo(x + paso * 0.75f, y + s * 3 * u); p.lineTo(x + paso * 0.75f, y); p.lineTo(x + paso, y)
                x += paso; k++
            }
            drawPath(p, oro, style = Stroke(1 * u))
        }
        val cartela = Rect(48 * u, 8 * u, w - 48 * u, h - 8 * u)
        val hoja = fieltroPieza(oscuro)
        drawRect(hoja, cartela.topLeft, cartela.size)
        drawRect(cordonPieza(oscuro), Offset(cartela.left + 2 * u, cartela.top + 2 * u), Size(cartela.width - 4 * u, cartela.height - 4 * u), style = Stroke(1 * u))
        textoEn(medidor, e.titulo, cartela.deflate(3 * u), LetraExtra.filosofo, legibleMedievo(if (oscuro) CREMA else TINTA, hoja), 21f, peso = FontWeight.Bold, minimo = 12f)
        listOf(true, false).forEach { izquierda ->
            val c = Offset(if (izquierda) 23 * u else w - 23 * u, h / 2)
            val k = if (izquierda) -1f else 1f
            val punta = Path().apply { moveTo(c.x + k * 10 * u, c.y); lineTo(c.x - k * 5 * u, c.y - 11 * u); lineTo(c.x - k * 5 * u, c.y + 11 * u); close() }
            drawPath(punta, oro)
            drawPath(punta, if (oscuro) CIELO else AZUL, style = Stroke(1.4f * u, join = StrokeJoin.Round))
            drawLine(oro, Offset(c.x - k * 5 * u, c.y), Offset(c.x - k * 12 * u, c.y), 2.4f * u, StrokeCap.Round)
        }
    }

    /**
     * El asiento: el pespunte del cordón (una línea de puntadas) al pie del renglón, con el arranque de la columna del
     * importe a 112 dp del canto; al principio, el rombo rojo del gasto o el lingote de plata del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 1 * u
        val cordon = cordonPieza(oscuro).copy(alpha = 0.4f)
        drawLine(cordon, Offset(16 * u, y), Offset(w, y), 0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u)))
        drawLine(cordon, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.9f * u)
        val c = Offset(6 * u, y - 3.4f * u)
        if (e.ingreso) {
            val plata = if (oscuro) Color(0xFFD8DCE2) else Color(0xFF6E7682)
            drawPath(Path().apply {
                moveTo(c.x - 4.4f * u, c.y - 2.4f * u); quadraticTo(c.x, c.y + 0.4f * u, c.x + 4.4f * u, c.y - 2.4f * u)
                lineTo(c.x + 3 * u, c.y + 2.6f * u); lineTo(c.x - 3 * u, c.y + 2.6f * u); close()
            }, plata.copy(alpha = 0.85f))
        } else drawPath(Path().apply { moveTo(c.x, c.y - 3.6f * u); lineTo(c.x + 3 * u, c.y); lineTo(c.x, c.y + 3.6f * u); lineTo(c.x - 3 * u, c.y); close() },
            rojoPieza(oscuro).copy(alpha = 0.85f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: el cuerno de carnero rojo, la fecha y el pespunte azul del cordón hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF0A1220), Color(0xFF101B2E)) else listOf(Color(0xFFF1EDE3), Color(0xFFFAF7F0))
        drawPath(cuernoMongol(Offset(6 * u, h / 2), 5.5f * u, 1f), rojoPieza(oscuro), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        val fin = textoIzqMedievo(medidor, texto, 16 * u, 0f, h, w - 40 * u, LetraExtra.filosofo, legibleMedievo(if (oscuro) CREMA else TINTA, fondos), 12.5f, FontWeight.Bold, 1.6f)
        if (fin > 16 * u) drawLine(cordonPieza(oscuro).copy(alpha = 0.7f), Offset(fin + 6 * u, h / 2), Offset(w, h / 2), 1.2f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u)))
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /** La raya: un trazo fino de tinta y, en medio, una ola seigaiha (tres arcos uno dentro de otro) en añil. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val sumi = tenueMedievo(if (oscuro) ZOGE.copy(alpha = 0.28f) else SUMI.copy(alpha = 0.38f), e)
        val nami = tenueMedievo(if (oscuro) Color(0xFFA6C0E0) else NAMI, e)
        val hueco = 7.5f * u
        drawLine(sumi, Offset(4 * u, y), Offset(cx - hueco, y), 0.9f * u, StrokeCap.Round)
        drawLine(sumi, Offset(cx + hueco, y), Offset(w - 4 * u, y), 0.9f * u, StrokeCap.Round)
        val yb = y + 2.2f * u
        listOf(4.6f, 3.1f, 1.6f).forEach { k ->
            val r = k * u
            drawArc(nami, 180f, 180f, false, Offset(cx - r, yb - r), Size(2 * r, 2 * r), style = Stroke(0.8f * u))
        }
    }

    /** El añil de las olas; en modo oscuro, el bermellón claro de los sellos. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFEF7A55) else Color(0xFF1E3A5F)

    /**
     * El adorno: las comillas japonesas 「 」 en las esquinas del botón, trazadas a pincel. Al presionarlo, el fondo toma el
     * color de la paulonia y las comillas se vuelven de bermellón.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        toqueMedievo(e, (if (oscuro) KIRI else KIRI_VETA).copy(alpha = if (oscuro) 0.12f else 0.16f), 1 * u)
        val vivo = e.presionado && e.habilitado
        val tinta = apagadoBotonMedievo(when {
            vivo -> if (oscuro) Color(0xFFF0806C) else BERMELLON
            oscuro -> ZOGE.copy(alpha = 0.5f)
            else -> SUMI.copy(alpha = 0.55f)
        }, e, oscuro)
        val top = 10 * u; val bot = h - 10 * u
        val brazo = minOf(5 * u, w * 0.12f); val alto = minOf(11 * u, (bot - top) * 0.6f)
        val trazo = Stroke(1.2f * u, cap = StrokeCap.Square)
        drawPath(Path().apply { moveTo(5 * u + brazo, top); lineTo(5 * u, top); lineTo(5 * u, top + alto) }, tinta, style = trazo)
        drawPath(Path().apply { moveTo(w - 5 * u - brazo, bot); lineTo(w - 5 * u, bot); lineTo(w - 5 * u, bot - alto) }, tinta, style = trazo)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La madera clara de las piezas grandes (vacío, aviso, cifra): en modo oscuro, aún más honda que la del campo. */
    private fun kiriPieza(oscuro: Boolean): Color = if (oscuro) mezcla(KIRI_LUZ, Color(0xFF120E0B), 0.8f) else KIRI_LUZ
    /** El bermellón de los sellos, aclarado en modo oscuro. */
    private fun selloEdo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0806C) else BERMELLON
    /** El añil de las olas, aclarado en modo oscuro. */
    private fun namiPieza(oscuro: Boolean): Color = if (oscuro) Color(0xFFA6C0E0) else NAMI

    /** Un trazo de pincel de [a] a [b]: empieza fino, engorda hasta [grueso] y se afila al levantar el pincel. */
    private fun pincel(a: Offset, b: Offset, grueso: Float): Path = Path().apply {
        val dx = b.x - a.x; val dy = b.y - a.y
        val largo = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val nx = -dy / largo * grueso / 2; val ny = dx / largo * grueso / 2
        moveTo(a.x, a.y)
        quadraticTo(a.x + dx * 0.3f + nx * 1.6f, a.y + dy * 0.3f + ny * 1.6f, b.x, b.y)
        quadraticTo(a.x + dx * 0.4f - nx, a.y + dy * 0.4f - ny, a.x, a.y)
        close()
    }

    override val margenFila: Dp = 12.dp

    /**
     * La fila: al pie, un trazo de pincel de tinta sumi que se afila al final. Al tocarla, la fila toma el color de la
     * paulonia y en el margen se pega una tira de bermellón, como un tanzaku.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) {
            drawRect((if (oscuro) KIRI else KIRI_VETA).copy(alpha = if (oscuro) 0.1f else 0.16f))
            drawRect(selloEdo(oscuro), Offset(3 * u, 8 * u), Size(2.6f * u, h - 16 * u))
        }
        drawPath(pincel(Offset(6 * u, h - 1.2f * u), Offset(w - 6 * u, h - 1.4f * u), 1.4f * u), (if (oscuro) ZOGE else SUMI).copy(alpha = if (oscuro) 0.2f else 0.26f))
    }

    /** La flecha: un cheurón a pincel, cada brazo un trazo que engorda y se afila. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val tinta = (if (oscuro) ZOGE else SUMI).copy(alpha = 0.78f)
        drawPath(pincel(Offset(f(0.3f), f(0.16f)), Offset(f(0.74f), f(0.52f)), 3.2f * u), tinta)
        drawPath(pincel(Offset(f(0.72f), f(0.48f)), Offset(f(0.32f), f(0.86f)), 3 * u), tinta)
    }

    /**
     * La marca fija: un mon de cinco pétalos (el blasón de flor de cerezo, como los de las cortinas de las tiendas); lleno
     * de bermellón con el centro claro si está fija, solo el contorno si no.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val s = size.minDimension
        val petalos = Path()
        for (k in 0 until 5) {
            val a = Math.toRadians(-90.0 + k * 72.0)
            val pc = Offset(c.x + s * 0.25f * kotlin.math.cos(a).toFloat(), c.y + s * 0.25f * kotlin.math.sin(a).toFloat())
            petalos.addOval(Rect(pc, s * 0.15f))
        }
        if (fija) {
            drawPath(petalos, selloEdo(oscuro))
            drawCircle(if (oscuro) Color(0xFF111215) else ZOGE, s * 0.1f, c)
            drawCircle(selloEdo(oscuro), s * 0.045f, c)
        } else {
            drawPath(petalos, (if (oscuro) ZOGE else SUMI).copy(alpha = 0.5f), style = Stroke(1 * u))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) ZOGE else SUMI

    /** El vacío: una tabla de paulonia lisa, con su veta suave y el ribete de laca negra, como el fondo de un tansu vaciado. */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        val tabla = Rect(0f, 0f, w, h)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(1.4f * u, 1.8f * u), tabla.size)
        drawRect(kiriPieza(oscuro), tabla.topLeft, tabla.size)
        vetas(tabla, t(KIRI_VETA, oscuro).copy(alpha = if (oscuro) 0.18f else 0.22f), 19 + (e.semilla and 7), densidad = 0.35f, grosor = 0.5f)
        drawRect(t(URUSHI, oscuro).copy(alpha = if (oscuro) 0.9f else 0.55f), tabla.topLeft, tabla.size, style = Stroke(1 * u))
        drawRect(Color.Black.copy(alpha = 0.08f), Offset(1.4f * u, 1.4f * u), Size(w - 2.8f * u, 1.2f * u))
    }

    /**
     * Lo vacío: un farolillo de papel apagado: el cuerpo con sus costillas de bambú, las tapas de laca, el gancho y la franja
     * de bermellón donde iría el nombre de la tienda.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = if (oscuro) ZOGE.copy(alpha = 0.78f) else SUMI.copy(alpha = 0.74f)
        val tr = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cuerpo = Rect(f(0.24f), f(0.2f), f(0.76f), f(0.84f))
        drawOval((if (oscuro) ZOGE else KIRI_LUZ).copy(alpha = if (oscuro) 0.08f else 0.7f), cuerpo.topLeft, cuerpo.size)
        drawRect(selloEdo(oscuro).copy(alpha = 0.75f), Offset(f(0.25f), f(0.48f)), Size(f(0.5f), f(0.08f)))
        val costillas = Path()
        for (k in 1..5) {
            val y = cuerpo.top + cuerpo.height * k / 6f
            val dy = (y - cuerpo.center.y) / (cuerpo.height / 2)
            val media = cuerpo.width / 2 * kotlin.math.sqrt(maxOf(0f, 1f - dy * dy))
            costillas.moveTo(cuerpo.center.x - media, y); costillas.lineTo(cuerpo.center.x + media, y)
        }
        drawPath(costillas, l.copy(alpha = l.alpha * 0.55f), style = Stroke(0.6f * u))
        drawOval(l, cuerpo.topLeft, cuerpo.size, style = tr)
        val laca = if (oscuro) ZOGE.copy(alpha = 0.85f) else URUSHI
        drawRect(laca, Offset(f(0.36f), f(0.13f)), Size(f(0.28f), f(0.08f)))
        drawRect(laca, Offset(f(0.36f), f(0.83f)), Size(f(0.28f), f(0.08f)))
        drawArc(l, 180f, 180f, false, Offset(f(0.45f), f(0.03f)), Size(f(0.1f), f(0.14f)), style = Stroke(1 * u))
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) ZOGE else SUMI

    /**
     * El aviso: una tira de washi con su ribete de laca arriba y, en el margen, un retazo de olas seigaiha en añil (de dos a
     * cuatro olas, según el aviso).
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(kiriPieza(oscuro))
        drawRect(t(URUSHI, oscuro).copy(alpha = if (oscuro) 0.9f else 0.85f), Offset.Zero, Size(w, 1.6f * u))
        val nami = namiPieza(oscuro)
        val cy = minOf(h / 2, 18 * u)
        val n = 2 + ((e.semilla % 3) + 3) % 3
        val r = 4.4f * u
        val olas = Path()
        for (k in 0 until n) {
            val fila = k / 2; val col = k % 2
            val c = Offset(9 * u + col * 2 * r + (if (fila % 2 == 1) r else 0f), cy + 4 * u - fila * r * 0.9f)
            listOf(1f, 0.66f, 0.33f).forEach { f -> olas.addArcRad(Rect(c, r * f), Math.PI.toFloat(), Math.PI.toFloat()) }
        }
        drawPath(olas, nami, style = Stroke(0.9f * u))
        drawLine(nami, Offset(4.6f * u, cy + 4 * u), Offset(4.6f * u + 4 * r, cy + 4 * u), 0.9f * u)
    }

    override val margenCifra: Dp = 10.dp
    /** El añil de las olas. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFA6C0E0) else NAMI
    /** El bermellón de los sellos, oscurecido para leerse sobre la madera. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0806C) else mezcla(BERMELLON, SUMI, 0.22f)

    /**
     * La cifra: una tablilla ema (la de los votos de los santuarios): madera clara con el tejadillo de las esquinas cortadas,
     * dos vetas al pie, el agujero arriba y su cordón rojo.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        val corte = minOf(8 * u, w / 4)
        val ema = Path().apply { moveTo(corte, 0f); lineTo(w - corte, 0f); lineTo(w, corte * 0.8f); lineTo(w, h); lineTo(0f, h); lineTo(0f, corte * 0.8f); close() }
        translate(1.4f * u, 1.8f * u) { drawPath(ema, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f)) }
        drawPath(ema, kiriPieza(oscuro))
        val veta = t(KIRI_VETA, oscuro).copy(alpha = if (oscuro) 0.22f else 0.3f)
        drawLine(veta, Offset(3 * u, h - 6 * u), Offset(w - 3 * u, h - 4.5f * u), 0.5f * u)
        drawLine(veta, Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3.5f * u), 0.5f * u)
        drawPath(ema, t(URUSHI, oscuro).copy(alpha = 0.8f), style = Stroke(1 * u, join = StrokeJoin.Miter))
        val o = Offset(w / 2, 4.5f * u)
        drawCircle(Color.Black.copy(alpha = 0.55f), 1.2f * u, o)
        drawLine(selloEdo(oscuro), o, Offset(o.x - 3.5f * u, 0.5f * u), 0.9f * u, StrokeCap.Round)
        drawLine(selloEdo(oscuro), o, Offset(o.x + 3.5f * u, 0.5f * u), 0.9f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp
    /** La tinta sumi; en modo oscuro y en la caja de laca de la noche, el marfil. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro || tipo == TipoTarjeta.NOCHE) ZOGE else SUMI
    /** El añil de las olas; en la caja de laca, la paulonia clara. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.NOCHE -> Color(0xFFCDBF9F)
        oscuro -> Color(0xFFA6C0E0)
        else -> mezcla(NAMI, SUMI, 0.2f)
    }
    /** El bermellón de los sellos; en la caja de laca, el oro de la hoja. */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.NOCHE -> Color(0xFFE6C670)
        oscuro -> Color(0xFFF59484)
        else -> Color(0xFFA0281F)
    }

    /**
     * Las tarjetas de la tienda:
     * - la cifra de Hoy es la entrada con su noren: la cortina de añil partida en tres paños, colgada de la vara, y el papel
     *   liso debajo; destacada, el blasón de bermellón en el paño de en medio y el filete rojo;
     * - «Te falta hoy» es el kōsatsu, el tablón de los avisos del pueblo: la tabla de paulonia entre sus dos postes, bajo su
     *   tejadillo de tejas; completo, el sello redondo de bermellón;
     * - la noche es una caja de laca negra con makie de oro: las espigas de susuki al pie y la luna creciente; sellada, la luna
     *   llena y el filete de oro;
     * - la tarjeta de Vida es un senjafuda: el papel con su marco de tinta y la tira de bermellón por la derecha.
     * Al presionarlas, el papel toma el color de la paulonia.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val papel = if (oscuro) mezcla(ZOGE, Color(0xFF120E0B), 0.8f) else ZOGE
        val toque = t(KIRI, oscuro).copy(alpha = if (oscuro) 0.12f else 0.3f)
        val sumi = if (oscuro) ZOGE.copy(alpha = 0.3f) else SUMI.copy(alpha = 0.55f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(papel, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, if (e.destacado) selloEdo(oscuro) else sumi.copy(alpha = sumi.alpha * 0.6f), style = Stroke(if (e.destacado) 1.4f * u else 0.6f * u))
                // El noren: tres paños de añil colgados de la vara.
                val alto = 9.5f * u
                val azulNoren = if (oscuro) mezcla(NAMI, Color.Black, 0.3f) else NAMI
                val ancho = r.width / 3
                for (k in 0 until 3) drawRect(azulNoren, Offset(r.left + k * ancho + (if (k > 0) 0.6f * u else 0f), r.top + 1.6f * u), Size(ancho - (if (k in 1..1) 1.2f * u else 0.6f * u), alto))
                drawLine(t(KIRI_VETA, oscuro), Offset(r.left, r.top + 1 * u), Offset(r.right, r.top + 1 * u), 2 * u)
                // El hilo blanco al pie de los paños.
                drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.45f), Offset(r.left, r.top + alto), Offset(r.right, r.top + alto), 0.5f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1.2f * u)))
                if (e.destacado) {
                    val c = Offset(r.center.x, r.top + 1.6f * u + alto / 2)
                    drawCircle(Color.White.copy(alpha = 0.9f), 2.9f * u, c)
                    drawCircle(selloEdo(oscuro), 2.2f * u, c)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val techo = 9.5f * u
                val poste = 3.5f * u
                val tabla = Rect(r.left + 3 * u, r.top + techo, r.right - 3 * u, r.bottom)
                sombraTarjetaMedievo(rectMedievo(tabla), e, oscuro)
                // Los postes y la tabla de paulonia.
                val kiri = kiriPieza(oscuro)
                drawRect(kiri, tabla.topLeft, tabla.size)
                if (e.presionado) drawRect(toque, tabla.topLeft, tabla.size)
                val madera = t(KIRI_VETA, oscuro).let { if (oscuro) mezcla(it, Color.Black, 0.3f) else it }
                drawRect(madera, Offset(tabla.left, tabla.top), Size(poste, tabla.height))
                drawRect(madera, Offset(tabla.right - poste, tabla.top), Size(poste, tabla.height))
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(tabla.left + poste, tabla.top), Offset(tabla.left + poste, tabla.bottom), 0.6f * u)
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(tabla.right - poste, tabla.top), Offset(tabla.right - poste, tabla.bottom), 0.6f * u)
                drawLine(madera, Offset(tabla.left, tabla.bottom - 1.2f * u), Offset(tabla.right, tabla.bottom - 1.2f * u), 2.4f * u)
                // El tejadillo de tejas, con el alero que sale a los lados.
                val tejado = Path().apply {
                    moveTo(r.left + 7 * u, r.top); lineTo(r.right - 7 * u, r.top)
                    lineTo(r.right, r.top + techo - 1 * u); lineTo(r.left, r.top + techo - 1 * u); close()
                }
                translate(0.8f * u, 1.2f * u) { drawPath(tejado, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f)) }
                drawPath(tejado, if (oscuro) Color(0xFF4A3F38) else t(URUSHI, oscuro))
                val tejas = Path(); val paso = maxOf(4 * u, 1f)
                var x = r.left + 6 * u; var k = 0
                while (x < r.right - 4 * u && k < 160) { tejas.moveTo(x, r.top + 1.5f * u); tejas.lineTo(x + (x - r.center.x) * 0.02f, r.top + techo - 1.6f * u); x += paso; k++ }
                drawPath(tejas, Color.White.copy(alpha = if (oscuro) 0.08f else 0.16f), style = Stroke(0.6f * u))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.2f), Offset(r.left + 7 * u, r.top + 0.5f * u), Offset(r.right - 7 * u, r.top + 0.5f * u), 0.9f * u)
                if (e.destacado) {
                    val c = Offset(tabla.right - poste - 12 * u, tabla.center.y)
                    val rr = minOf(7 * u, tabla.height / 2 - 3 * u)
                    drawCircle(selloEdo(oscuro), rr, c)
                    drawCircle(kiri, rr * 0.78f, c, style = Stroke(0.6f * u))
                    drawLine(kiri, Offset(c.x - rr * 0.4f, c.y - rr * 0.2f), Offset(c.x + rr * 0.4f, c.y - rr * 0.2f), 0.9f * u)
                    drawLine(kiri, Offset(c.x, c.y - rr * 0.5f), Offset(c.x, c.y + rr * 0.5f), 0.9f * u)
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(if (oscuro) Color(0xFF0E0B0A) else Color(0xFF1E1714), r.topLeft, r.size)
                if (e.presionado) drawRect(Color.White.copy(alpha = 0.05f), r.topLeft, r.size)
                val oro = Color(0xFFD9B65E).copy(alpha = if (oscuro) 0.75f else 0.9f)
                drawRect(oro, Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(if (e.destacado) 1 * u else 0.5f * u))
                // Las espigas de susuki al pie.
                val espigas = Path()
                val paso = maxOf(6 * u, 1f)
                var x = r.left + 5 * u; var k = 0
                while (x < r.right - 5 * u && k < 120) {
                    val alto = (4.5f + 4f * azar(e.semilla + 7, k)) * u
                    val inclina = (azar(e.semilla, k) * 2 - 0.4f) * 3 * u
                    espigas.moveTo(x, r.bottom - 3 * u)
                    espigas.quadraticTo(x + inclina * 0.2f, r.bottom - 3 * u - alto * 0.6f, x + inclina, r.bottom - 3 * u - alto)
                    x += paso; k++
                }
                drawPath(espigas, oro, style = Stroke(0.6f * u, cap = StrokeCap.Round))
                // La luna.
                val luna = Offset(r.right - minOf(128 * u, r.width * 0.42f), r.top + 10 * u)
                drawCircle(Color(0xFFE6C670), 5 * u, luna)
                if (!e.destacado) drawCircle(if (oscuro) Color(0xFF0E0B0A) else Color(0xFF1E1714), 4.4f * u, Offset(luna.x + 2.2f * u, luna.y - 1 * u))
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(papel, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El marco de tinta y la tira de bermellón.
                drawRect(sumi, Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(1.3f * u))
                drawRect(sumi.copy(alpha = sumi.alpha * 0.6f), Offset(r.left + 4.6f * u, r.top + 4.6f * u), Size(r.width - 9.2f * u, r.height - 9.2f * u), style = Stroke(0.5f * u))
                drawRect(selloEdo(oscuro), Offset(r.right - 10 * u, r.top + 4.6f * u), Size(4.6f * u, r.height - 9.2f * u))
                drawPath(forma, sumi.copy(alpha = sumi.alpha * 0.4f), style = Stroke(0.5f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El frente de un cajón de la balanza: el que está fuera, de paulonia clara a la luz; el que está dentro, en sombra. */
    private fun frenteCajon(fuera: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(KIRI_LUZ, Color(0xFF120E0B), if (fuera) 0.7f else 0.86f)
        fuera -> mezcla(KIRI_LUZ, Color.White, 0.45f)
        else -> mezcla(KIRI, KIRI_VETA, 0.3f)
    }

    /** El tirador de hierro de un cajón (el warabite): la placa y el asa, levantada si [levantado] o caída sobre la placa. */
    private fun DrawScope.tiradorEdo(c: Offset, levantado: Boolean, oscuro: Boolean) {
        val u = density
        val hierro = if (oscuro) Color(0xFF8C847A) else HIERRO
        drawOval(hierro, Offset(c.x - 3.5f * u, c.y - 2.6f * u), Size(7 * u, 5.2f * u))
        drawCircle(Color.White.copy(alpha = 0.25f), 0.8f * u, Offset(c.x - 1.6f * u, c.y))
        drawCircle(Color.White.copy(alpha = 0.25f), 0.8f * u, Offset(c.x + 1.6f * u, c.y))
        val asa = if (levantado) Path().apply { moveTo(c.x - 2 * u, c.y - 1 * u); cubicTo(c.x - 3 * u, c.y - 12 * u, c.x + 3 * u, c.y - 12 * u, c.x + 2 * u, c.y - 1 * u) }
        else Path().apply { moveTo(c.x - 2 * u, c.y + 1 * u); cubicTo(c.x - 3 * u, c.y + 9 * u, c.x + 3 * u, c.y + 9 * u, c.x + 2 * u, c.y + 1 * u) }
        drawPath(asa, hierro, style = Stroke(1.6f * u, cap = StrokeCap.Round))
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: un tansu, el arcón de cajones de la tienda, con dos cajones: el gasto a la izquierda y el ingreso a la
     * derecha. El elegido está tirado hacia fuera: más grande, a la luz, con su sombra sobre el cuerpo del arcón, el asa de
     * hierro levantada y el sello de bermellón al lado del rótulo; el otro queda cerrado, en sombra, con el asa caída.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        // El cuerpo del arcón, con su veta y el canto de laca.
        val cuerpo = kiriHondo(KIRI, oscuro).let { if (oscuro) it else mezcla(it, KIRI_VETA, 0.35f) }
        drawRect(cuerpo)
        vetas(Rect(0f, 0f, w, h), KIRI_VETA.copy(alpha = if (oscuro) 0.25f else 0.4f), 31, densidad = 0.6f)
        drawRect(t(URUSHI, oscuro), Offset.Zero, Size(w, h), style = Stroke(2.4f * u))
        drawLine(t(URUSHI, oscuro), Offset(w / 2, 0f), Offset(w / 2, h), 2 * u)
        listOf(false, true).forEach { ingreso ->
            val fuera = ingreso == e.ingreso
            val m = if (fuera) 3 * u else 6 * u
            val r = Rect(if (ingreso) w / 2 + m else m, m, if (ingreso) w - m else w / 2 - m, h - m)
            if (fuera) drawRect(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size)
            else drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), Offset(r.left - 1 * u, r.top - 1 * u), Size(r.width + 2 * u, r.height + 2 * u), style = Stroke(1 * u))
            val frente = frenteCajon(fuera, oscuro)
            drawRect(frente, r.topLeft, r.size)
            // La veta del frente, solo en las cabezas: el rótulo va sobre la madera lisa.
            listOf(r.left + 2 * u, r.right - 20 * u).forEach { x -> vetas(Rect(x, r.top, x + 18 * u, r.bottom), KIRI_VETA.copy(alpha = if (oscuro) 0.14f else 0.2f), if (ingreso) 7 else 3, densidad = 0.5f, vertical = true, grosor = 0.6f) }
            // Los herrajes en ele de las esquinas.
            val hierro = (if (oscuro) Color(0xFF8C847A) else HIERRO).copy(alpha = if (fuera) 0.9f else 0.55f)
            val l = 6 * u; val g = 1.6f * u
            listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), r.bottomRight to Offset(-1f, -1f)).forEach { (o, d) ->
                drawLine(hierro, o, Offset(o.x + d.x * l, o.y), g); drawLine(hierro, o, Offset(o.x, o.y + d.y * l), g)
            }
            val xa = if (ingreso) r.right - 13 * u else r.left + 13 * u
            tiradorEdo(Offset(xa, r.center.y + 3 * u), fuera, oscuro)
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (fuera) legibleMedievo(propio, frente) else tenueLegibleMedievo(propio, frente)
            val caja = Rect(r.left + 24 * u, r.top + 3 * u, r.right - 24 * u, r.bottom - 3 * u)
            textoEn(medidor, texto, caja, LetraExtra.shippori, color, 17f, peso = if (fuera) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
            if (fuera) {
                val xs = if (ingreso) r.left + 12 * u else r.right - 12 * u
                drawRect(selloEdo(oscuro), Offset(xs - 5 * u, r.center.y - 5 * u), Size(10 * u, 10 * u))
                drawRect(frente.copy(alpha = 0.8f), Offset(xs - 3.6f * u, r.center.y - 3.6f * u), Size(7.2f * u, 7.2f * u), style = Stroke(0.7f * u))
                drawLine(frente.copy(alpha = 0.8f), Offset(xs, r.center.y - 2.6f * u), Offset(xs, r.center.y + 2.6f * u), 0.8f * u)
                drawLine(frente.copy(alpha = 0.8f), Offset(xs - 2 * u, r.center.y), Offset(xs + 2 * u, r.center.y), 0.8f * u)
            }
        }
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: el noren de la tienda, la cortina de añil colgada de su vara de laca, partida en tres paños: el
     * del medio con el mes en blanco de marfil y, en los de los lados, una punta de flecha de dos pinceladas que apunta al mes
     * de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val tela = if (oscuro) mezcla(NAMI, Color(0xFF0A0C12), 0.5f) else NAMI
        drawRect(tela, Offset(0f, 3 * u), Size(w, h - 3 * u))
        // Las aberturas del noren entre los paños, desde un tercio hasta abajo.
        val hueco = if (oscuro) Color(0xFF111215) else Color(0xFFF2ECDF)
        listOf(48 * u, w - 48 * u).forEach { x -> drawRect(hueco, Offset(x - 1.2f * u, h * 0.32f), Size(2.4f * u, h)) }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, 4.5f * u), Size(w, 2 * u))
        drawRect(t(URUSHI, oscuro), Offset.Zero, Size(w, 4.5f * u))
        drawRect(KINPAKU.copy(alpha = 0.7f), Offset(0f, 0f), Size(3 * u, 4.5f * u)); drawRect(KINPAKU.copy(alpha = 0.7f), Offset(w - 3 * u, 0f), Size(3 * u, 4.5f * u))
        val blanco = legibleMedievo(ZOGE, tela)
        textoEn(medidor, e.titulo, Rect(54 * u, 6 * u, w - 54 * u, h), LetraExtra.shippori, blanco, 21f, peso = FontWeight.Bold, minimo = 12f)
        listOf(true, false).forEach { izquierda ->
            val c = Offset(if (izquierda) 24 * u else w - 24 * u, h / 2 + 2 * u)
            val k = if (izquierda) -1f else 1f
            drawPath(pincel(Offset(c.x - k * 6 * u, c.y - 11 * u), Offset(c.x + k * 7 * u, c.y + 0.5f * u), 5.5f * u), blanco)
            drawPath(pincel(Offset(c.x + k * 7 * u, c.y - 0.5f * u), Offset(c.x - k * 6 * u, c.y + 11 * u), 5.5f * u), blanco)
        }
    }

    /**
     * El asiento: un renglón del daifukuchō (el libro mayor de la tienda), con la raya de sumi al pie y el arranque de la
     * columna del importe a 112 dp del canto; al principio, el sello redondo de bermellón del gasto o el koban (la moneda
     * ovalada de oro) del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val sumi = (if (oscuro) ZOGE else SUMI).copy(alpha = if (oscuro) 0.2f else 0.22f)
        drawLine(sumi, Offset(16 * u, y), Offset(w, y), 0.7f * u)
        drawLine(sumi, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.7f * u)
        val c = Offset(6 * u, y - 3.4f * u)
        if (e.ingreso) {
            val oro = if (oscuro) KINPAKU else mezcla(KINPAKU, SUMI, 0.3f)
            drawOval(oro.copy(alpha = 0.9f), Offset(c.x - 2.8f * u, c.y - 4 * u), Size(5.6f * u, 8 * u))
            listOf(-1.6f, 0f, 1.6f).forEach { d -> drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x - 1.6f * u, c.y + d * u), Offset(c.x + 1.6f * u, c.y + d * u), 0.4f * u) }
        } else {
            drawCircle(selloEdo(oscuro).copy(alpha = 0.85f), 3.4f * u, c)
            drawCircle((if (oscuro) Color(0xFF111215) else Color.White).copy(alpha = 0.6f), 2.2f * u, c, style = Stroke(0.5f * u))
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: un sellito cuadrado de bermellón, la fecha en sumi y una pincelada que se adelgaza hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF111215), Color(0xFF191A1F)) else listOf(Color(0xFFF2ECDF), Color(0xFFFAF7EF))
        drawRect(selloEdo(oscuro), Offset(1 * u, h / 2 - 5 * u), Size(10 * u, 10 * u))
        drawRect(fondos[0].copy(alpha = 0.75f), Offset(2.6f * u, h / 2 - 3.4f * u), Size(6.8f * u, 6.8f * u), style = Stroke(0.6f * u))
        val fin = textoIzqMedievo(medidor, texto, 16 * u, 0f, h, w - 40 * u, LetraExtra.shippori, legibleMedievo(if (oscuro) ZOGE else SUMI, fondos), 12.5f, FontWeight.Bold, 1.6f)
        if (fin > 16 * u) drawPath(pincel(Offset(fin + 6 * u, h / 2 + 1 * u), Offset(w - 4 * u, h / 2), 2.6f * u), (if (oscuro) ZOGE else SUMI).copy(alpha = 0.5f))
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La raya: una línea de nogal con las marcas del compás a intervalos iguales, abriéndose desde el centro, donde queda el
     * círculo con la punta del compás en sanguina.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val nogal = tenueMedievo(if (oscuro) PERGAMINO.copy(alpha = 0.28f) else NOGAL.copy(alpha = 0.45f), e)
        val sanguina = tenueMedievo(if (oscuro) Color(0xFFE8987A) else SANGUINA, e)
        val r = 2.4f * u; val hueco = r + 2 * u
        drawLine(nogal, Offset(4 * u, y), Offset(cx - hueco, y), 0.8f * u)
        drawLine(nogal, Offset(cx + hueco, y), Offset(w - 4 * u, y), 0.8f * u)
        // Las marcas, todas en una sola figura.
        val paso = maxOf(14 * u, 1f)
        val marcas = Path(); var k = 1
        while (cx - k * paso > 6 * u && k < 40) {
            val largo = (if (k % 4 == 0) 3.2f else 2f) * u
            listOf(cx - k * paso, cx + k * paso).forEach { x -> marcas.moveTo(x, y); marcas.lineTo(x, y + largo) }
            k++
        }
        drawPath(marcas, nogal, style = Stroke(0.7f * u))
        drawCircle(sanguina, r, Offset(cx, y), style = Stroke(0.8f * u))
        drawCircle(sanguina, 0.8f * u, Offset(cx, y))
    }

    /** La sanguina, oscurecida para leerse sobre el boj. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE08A5E) else Color(0xFF8C3B1E)

    /**
     * El adorno: una regla graduada bajo el texto (la línea y las rayitas, más larga cada cinco y en las puntas). Al
     * presionarlo, el fondo toma la sanguina y la graduación se marca.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) Color(0xFFE8987A) else SANGUINA).copy(alpha = if (oscuro) 0.14f else 0.1f), 2 * u)
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 8 * u)
        val vivo = e.presionado && e.habilitado
        val raya = apagadoBotonMedievo(when {
            vivo -> if (oscuro) Color(0xFFE8987A) else SANGUINA
            oscuro -> PERGAMINO.copy(alpha = 0.45f)
            else -> NOGAL.copy(alpha = 0.6f)
        }, e, oscuro)
        drawLine(raya, Offset(x0, y), Offset(x1, y), 0.8f * u)
        val paso = maxOf(5 * u, 1f)
        val n = ((x1 - x0) / paso).toInt().coerceIn(1, 80)
        val marcas = Path()
        for (j in 0..n) {
            val x = x0 + (x1 - x0) * j / n
            val largo = (if (j == 0 || j == n) 3.4f else if (j % 5 == 0) 2.4f else 1.3f) * u
            marcas.moveTo(x, y); marcas.lineTo(x, y - largo)
        }
        drawPath(marcas, raya, style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La hoja del cuaderno de las piezas grandes (vacío, aviso, cifra): en modo oscuro, honda de verdad. */
    private fun hojaPieza(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color(0xFF100C08), 0.8f) else PERGAMINO
    /** La sanguina, aclarada en modo oscuro. */
    private fun sanguinaPieza(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8987A) else SANGUINA
    /** La tinta sepia de los trazos de construcción. */
    private fun sepia(oscuro: Boolean, alfa: Float): Color = (if (oscuro) PERGAMINO else NOGAL).copy(alpha = alfa)

    override val margenFila: Dp = 12.dp

    /**
     * La fila: una línea de construcción a tinta sepia al pie y, en el margen, el pinchazo del compás (un circulito). Al
     * tocarla, la hoja se tiñe de sanguina, el pinchazo se rellena y le sale un arco trazado con el compás.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) drawRect(sanguinaPieza(oscuro).copy(alpha = if (oscuro) 0.1f else 0.08f))
        drawLine(sepia(oscuro, if (oscuro) 0.18f else 0.3f), Offset(2 * u, h - 0.6f * u), Offset(w - 2 * u, h - 0.6f * u), 0.5f * u)
        val c = Offset(6 * u, h / 2)
        if (e.presionado) {
            drawCircle(sanguinaPieza(oscuro), 1.6f * u, c)
            drawArc(sanguinaPieza(oscuro).copy(alpha = 0.7f), -110f, 220f, false, Offset(c.x - 4.6f * u, c.y - 4.6f * u), Size(9.2f * u, 9.2f * u), style = Stroke(0.6f * u))
        } else {
            drawCircle(sepia(oscuro, 0.35f), 1.6f * u, c, style = Stroke(0.6f * u))
        }
    }

    /** La flecha: una flecha a pluma, con la punta abierta y la cola marcada con una rayita de sanguina. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val tinta = sepia(oscuro, 0.8f)
        val trazo = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawLine(tinta, Offset(f(0.08f), f(0.5f)), Offset(f(0.88f), f(0.5f)), trazo.width, StrokeCap.Round)
        drawPath(Path().apply { moveTo(f(0.62f), f(0.26f)); lineTo(f(0.9f), f(0.5f)); lineTo(f(0.62f), f(0.74f)) }, tinta, style = trazo)
        drawLine(sanguinaPieza(oscuro), Offset(f(0.08f), f(0.36f)), Offset(f(0.08f), f(0.64f)), 1 * u, StrokeCap.Round)
    }

    /**
     * La marca fija: la estrella de cinco puntas construida dentro de su círculo, como en los tratados de proporción; de
     * sanguina llena si está fija, solo los trazos si no.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.44f
        val astro = estrella(c, r * 0.92f, 5, 0.4f)
        if (fija) {
            drawCircle(sanguinaPieza(oscuro).copy(alpha = 0.7f), r, c, style = Stroke(0.8f * u))
            drawPath(astro, sanguinaPieza(oscuro))
        } else {
            val tenue = sepia(oscuro, 0.5f)
            drawCircle(tenue, r, c, style = Stroke(0.7f * u))
            drawPath(astro, tenue, style = Stroke(1 * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA

    /**
     * El vacío: una hoja del cuaderno sin dibujo: solo los trazos de construcción, muy tenues (la línea que corta la hoja en
     * la proporción áurea, el renglón del medio y un arco de compás en cada esquina de arriba).
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.12f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(hojaPieza(oscuro), Offset.Zero, Size(w, h))
        drawRect(sepia(oscuro, if (oscuro) 0.2f else 0.3f), Offset.Zero, Size(w, h), style = Stroke(0.7f * u))
        val trazo = sepia(oscuro, if (oscuro) 0.08f else 0.12f)
        drawLine(trazo, Offset(w * 0.618f, 4 * u), Offset(w * 0.618f, h - 4 * u), 0.5f * u)
        drawLine(trazo, Offset(4 * u, h / 2), Offset(w - 4 * u, h / 2), 0.5f * u)
        val r = minOf(h * 0.45f, 40 * u)
        drawArc(sanguinaPieza(oscuro).copy(alpha = 0.22f), 0f, 90f, false, Offset(-r, -r), Size(2 * r, 2 * r), style = Stroke(0.6f * u))
        drawArc(sanguinaPieza(oscuro).copy(alpha = 0.22f), 90f, 90f, false, Offset(w - r, -r), Size(2 * r, 2 * r), style = Stroke(0.6f * u))
        drawCircle(sanguinaPieza(oscuro).copy(alpha = 0.4f), 1 * u, Offset.Zero + Offset(1 * u, 1 * u))
        drawCircle(sanguinaPieza(oscuro).copy(alpha = 0.4f), 1 * u, Offset(w - 1 * u, 1 * u))
    }

    /**
     * Lo vacío: una hoja en blanco, un poco torcida, y el compás abierto apoyado encima: la bisagra, las dos patas con sus
     * puntas y el arco que dejó a medias.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = sepia(oscuro, 0.78f)
        val tr = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        rotate(-7f, Offset(f(0.42f), f(0.5f))) {
            drawRect((if (oscuro) PERGAMINO else Color.White).copy(alpha = if (oscuro) 0.08f else 0.5f), Offset(f(0.12f), f(0.12f)), Size(f(0.56f), f(0.76f)))
            drawRect(l, Offset(f(0.12f), f(0.12f)), Size(f(0.56f), f(0.76f)), style = tr)
        }
        drawArc(sanguinaPieza(oscuro).copy(alpha = 0.75f), -80f, 78f, false, Offset(f(0.22f), f(0.52f)), Size(f(0.68f), f(0.68f)), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        val bisagra = Offset(f(0.72f), f(0.12f))
        val laton = if (oscuro) LATON else LATON_OSC
        drawLine(laton, bisagra, Offset(f(0.56f), f(0.86f)), 1.6f * u, StrokeCap.Round)
        drawLine(laton, bisagra, Offset(f(0.9f), f(0.84f)), 1.6f * u, StrokeCap.Round)
        drawLine(l, Offset(f(0.56f), f(0.86f)), Offset(f(0.55f), f(0.92f)), 1 * u, StrokeCap.Round)
        drawLine(l, Offset(f(0.9f), f(0.84f)), Offset(f(0.91f), f(0.9f)), 1 * u, StrokeCap.Round)
        drawCircle(laton, f(0.06f), bisagra)
        drawCircle(l, f(0.025f), bisagra)
        drawLine(laton, Offset(f(0.72f), f(0.06f)), Offset(f(0.72f), f(0.02f)), 1.2f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA

    /**
     * El aviso: una tira del cuaderno y, en el margen, una figura de construcción a sanguina: el cuadrado dentro del círculo
     * (la del hombre de las proporciones) o, en la mitad de los avisos, el triángulo dentro del círculo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(hojaPieza(oscuro))
        drawLine(sepia(oscuro, 0.3f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 0.6f * u)
        drawLine(sepia(oscuro, 0.3f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.6f * u)
        val c = Offset(14 * u, minOf(h / 2, 19 * u)); val r = 8.5f * u
        val san = sanguinaPieza(oscuro)
        drawCircle(san, r, c, style = Stroke(0.9f * u))
        if (e.semilla % 2 == 0) {
            val a = r * 0.707f
            drawRect(san, Offset(c.x - a, c.y - a), Size(2 * a, 2 * a), style = Stroke(0.9f * u))
            drawLine(san.copy(alpha = 0.5f), Offset(c.x - a, c.y - a), Offset(c.x + a, c.y + a), 0.5f * u)
            drawLine(san.copy(alpha = 0.5f), Offset(c.x + a, c.y - a), Offset(c.x - a, c.y + a), 0.5f * u)
        } else {
            drawPath(estrella(c, r, 3, 0.5f), san, style = Stroke(0.9f * u, join = StrokeJoin.Miter))
            drawLine(san.copy(alpha = 0.5f), Offset(c.x, c.y - r), Offset(c.x, c.y + r * 0.5f), 0.5f * u)
        }
        drawCircle(san, 1 * u, c)
    }

    override val margenCifra: Dp = 10.dp
    /** La sanguina, oscurecida para leerse sobre la hoja. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8987A) else Color(0xFF8E3E28)
    /** La tinta sepia. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, BOJ, 0.2f) else NOGAL

    /**
     * La cifra: una tablilla de boj con la hoja pegada encima y, al pie del canto, la graduación de la regla (más larga cada
     * cinco), por dentro del margen.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(t(BOJ, oscuro).let { if (oscuro) mezcla(it, Color.Black, 0.35f) else it }, Offset.Zero, Size(w, h))
        drawRect(hojaPieza(oscuro), Offset(2.5f * u, 2.5f * u), Size(w - 5 * u, h - 8 * u))
        drawRect(t(NOGAL, oscuro).copy(alpha = 0.6f), Offset.Zero, Size(w, h), style = Stroke(0.8f * u))
        val marcas = Path()
        val paso = maxOf(3 * u, 1f)
        var x = 4 * u; var j = 0
        while (x < w - 3 * u && j < 120) {
            val largo = (if (j % 5 == 0) 2.6f else 1.4f) * u
            marcas.moveTo(x, h - 0.6f * u); marcas.lineTo(x, h - 0.6f * u - largo)
            x += paso; j++
        }
        drawPath(marcas, (if (oscuro) PERGAMINO else NOGAL).copy(alpha = 0.6f), style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta; en modo oscuro, el pergamino. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA
    /** La tinta sepia de los trazos de construcción (aclarada en modo oscuro). */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFD2BE98) else NOGAL
    /** La sanguina, oscurecida para leerse sobre la hoja (aclarada en modo oscuro). */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFEEA088) else Color(0xFF8E3E28)

    /** Una voluta: la espiral de una cartela, de radio [r] en [c], que gira hacia [sx], [sy] (±1). */
    private fun voluta(c: Offset, r: Float, sx: Float, sy: Float): Path = Path().apply {
        val n = 40
        for (k in 0..n) {
            val f = k / n.toFloat()
            val a = f * 4 * Math.PI.toFloat()
            val rr = r * (1 - f * 0.85f)
            val p = Offset(c.x + sx * rr * kotlin.math.cos(a), c.y + sy * rr * kotlin.math.sin(a))
            if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
    }

    /**
     * Las tarjetas del studiolo, de hoja lisa por dentro:
     * - la cifra de Hoy es un rectángulo de construcción: las cuatro rayas a tinta sepia que se pasan de las esquinas, con
     *   el pinchazo del compás en cada cruce; destacada, el compás traza dos arcos de sanguina en las esquinas;
     * - «Te falta hoy» es el cartellino de los cuadros: el papelito con la esquina doblada, pegado con una gota de lacre y la
     *   doble raya de sanguina; completo, el sello redondo al pie;
     * - la noche es la hoja del astrónomo: arriba, una constelación unida a tinta y la luna trazada con el compás; sellada, las
     *   estrellas y la luna se rellenan de sanguina;
     * - la tarjeta de Vida es una cartela: la hoja entre sus dos filetes de nogal, con una voluta en cada esquina.
     * Al presionarlas, la hoja se tiñe de sanguina.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val hoja = hojaPieza(oscuro)
        val toque = sanguinaPieza(oscuro).copy(alpha = if (oscuro) 0.1f else 0.1f)
        val sang = sanguinaPieza(oscuro)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hoja, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // Las rayas de construcción, que se pasan de las esquinas, y los pinchazos del compás.
                val a = 3.5f * u
                val raya = sepia(oscuro, if (oscuro) 0.45f else 0.6f)
                drawLine(raya, Offset(r.left + 0.5f * u, r.top + a), Offset(r.right - 0.5f * u, r.top + a), 0.5f * u)
                drawLine(raya, Offset(r.left + 0.5f * u, r.bottom - a), Offset(r.right - 0.5f * u, r.bottom - a), 0.5f * u)
                drawLine(raya, Offset(r.left + a, r.top + 0.5f * u), Offset(r.left + a, r.bottom - 0.5f * u), 0.5f * u)
                drawLine(raya, Offset(r.right - a, r.top + 0.5f * u), Offset(r.right - a, r.bottom - 0.5f * u), 0.5f * u)
                val cruces = listOf(Offset(r.left + a, r.top + a), Offset(r.right - a, r.top + a), Offset(r.left + a, r.bottom - a), Offset(r.right - a, r.bottom - a))
                cruces.forEach { o -> drawCircle(raya, 1.2f * u, o, style = Stroke(0.5f * u)); drawCircle(raya, 0.4f * u, o) }
                drawPath(forma, sepia(oscuro, 0.3f), style = Stroke(0.6f * u))
                if (e.destacado) {
                    val rr = 8.5f * u
                    drawArc(sang, 0f, 90f, false, Offset(cruces[0].x - rr, cruces[0].y - rr), Size(2 * rr, 2 * rr), style = Stroke(1.4f * u))
                    drawArc(sang, 180f, 90f, false, Offset(cruces[3].x - rr, cruces[3].y - rr), Size(2 * rr, 2 * rr), style = Stroke(1.4f * u))
                    drawPath(forma, sang, style = Stroke(1.2f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val pliegue = minOf(12 * u, r.height * 0.3f)
                val papel = Path().apply {
                    moveTo(r.left, r.top); lineTo(r.right - pliegue, r.top); lineTo(r.right, r.top + pliegue); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
                }
                sombraTarjetaMedievo(papel, e, oscuro)
                drawPath(papel, mezcla(hoja, BOJ, if (oscuro) 0.06f else 0.12f))
                if (e.presionado) drawPath(papel, toque)
                drawPath(papel, sepia(oscuro, 0.35f), style = Stroke(0.6f * u))
                // La esquina doblada, con su sombrita.
                val doblez = Path().apply { moveTo(r.right - pliegue, r.top); lineTo(r.right - pliegue, r.top + pliegue); lineTo(r.right, r.top + pliegue); close() }
                translate(-0.8f * u, 1 * u) { drawPath(doblez, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f)) }
                drawPath(doblez, if (oscuro) mezcla(PERGAMINO, Color.Black, 0.7f) else mezcla(PERGAMINO, BOJ, 0.5f))
                drawPath(doblez, sepia(oscuro, 0.4f), style = Stroke(0.5f * u))
                // La doble raya de sanguina arriba y abajo.
                listOf(r.top + 3 * u, r.bottom - 3 * u).forEach { y ->
                    drawLine(sang, Offset(r.left + 3 * u, y), Offset(r.right - pliegue - 2 * u, y), 1.1f * u)
                }
                listOf(r.top + 4.8f * u, r.bottom - 4.8f * u).forEach { y ->
                    drawLine(sang.copy(alpha = 0.6f), Offset(r.left + 3 * u, y), Offset(r.right - (if (y < r.center.y) pliegue + 2 * u else 3 * u), y), 0.4f * u)
                }
                drawLine(sang, Offset(r.right - 3 * u, r.top + pliegue + 2 * u), Offset(r.right - 3 * u, r.bottom - 3 * u), 0.5f * u)
                // La gota de lacre que lo sujeta.
                val lacre = if (oscuro) Color(0xFFC0503A) else Color(0xFF9A2A1C)
                drawPath(contornoIrregular(Rect(Offset(r.left + 1.5f * u, r.top + 1 * u), Size(9 * u, 8 * u)), 5, 14, 0.12f), lacre)
                if (e.destacado) {
                    val c = Offset(r.right - 16 * u, r.bottom - minOf(10 * u, r.height / 2 - 1 * u))
                    drawCircle(lacre, 6 * u, c)
                    drawCircle(Color.Black.copy(alpha = 0.3f), 4 * u, c, style = Stroke(0.7f * u))
                    drawCircle(Color.White.copy(alpha = 0.2f), 1.2f * u, Offset(c.x - 2 * u, c.y - 2 * u))
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(mezcla(hoja, Color(0xFF2A3050), if (oscuro) 0.06f else 0.05f), r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, sepia(oscuro, 0.35f), style = Stroke(0.6f * u))
                // La constelación, arriba, entre el título y el botón.
                val x0 = r.right - minOf(170 * u, r.width * 0.55f); val ancho = minOf(110 * u, r.width * 0.35f)
                val ys = listOf(8f, 4.5f, 10f, 5.5f, 11f)
                val puntos = ys.mapIndexed { k, y -> Offset(x0 + ancho * k / 4f + (azar(e.semilla, k) - 0.5f) * 6 * u, r.top + y * u) }
                val raya = sepia(oscuro, if (oscuro) 0.55f else 0.7f)
                for (k in 0 until puntos.size - 1) drawLine(raya, puntos[k], puntos[k + 1], 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u)))
                puntos.forEachIndexed { k, p ->
                    val rr = (if (k == 2) 3.4f else 2.4f) * u
                    if (e.destacado) drawPath(estrella(p, rr, 4, 0.3f), sang) else drawPath(estrella(p, rr, 4, 0.3f), raya.copy(alpha = 0.9f))
                }
                // La luna, trazada con el compás.
                val luna = Offset(r.right - 13 * u, r.top + 10 * u); val rl = 6 * u
                if (e.destacado) drawCircle(sang, rl, luna)
                drawCircle(raya, rl, luna, style = Stroke(0.8f * u))
                drawArc(raya, -70f, 140f, false, Offset(luna.x - rl * 0.4f, luna.y - rl), Size(rl * 1.6f, rl * 2), style = Stroke(0.8f * u))
                drawCircle(raya, 0.5f * u, luna)
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(hoja, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, sepia(oscuro, 0.3f), style = Stroke(0.6f * u))
                // Los filetes de nogal arriba y abajo, y una voluta en cada esquina.
                val nogal = if (oscuro) mezcla(BOJ, Color.Black, 0.3f) else NOGAL
                val a = 6 * u
                drawLine(nogal, Offset(r.left + 2 * a, r.top + 2.5f * u), Offset(r.right - 2 * a, r.top + 2.5f * u), 1.2f * u)
                drawLine(nogal, Offset(r.left + 2 * a, r.bottom - 2.5f * u), Offset(r.right - 2 * a, r.bottom - 2.5f * u), 1.2f * u)
                drawLine(nogal.copy(alpha = 0.5f), Offset(r.left + 2 * a, r.top + 4.3f * u), Offset(r.right - 2 * a, r.top + 4.3f * u), 0.4f * u)
                drawLine(nogal.copy(alpha = 0.5f), Offset(r.left + 2 * a, r.bottom - 4.3f * u), Offset(r.right - 2 * a, r.bottom - 4.3f * u), 0.4f * u)
                val tr = Stroke(0.9f * u, cap = StrokeCap.Round)
                drawPath(voluta(Offset(r.left + a, r.top + a), 4.4f * u, 1f, -1f), nogal, style = tr)
                drawPath(voluta(Offset(r.right - a, r.top + a), 4.4f * u, -1f, -1f), nogal, style = tr)
                drawPath(voluta(Offset(r.left + a, r.bottom - a), 4.4f * u, 1f, 1f), nogal, style = tr)
                drawPath(voluta(Offset(r.right - a, r.bottom - a), 4.4f * u, -1f, 1f), nogal, style = tr)
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El verdigrís de los cuadernos (el verde de cobre): el color de lo que entra, aclarado en modo oscuro. */
    private fun verderame(oscuro: Boolean): Color = if (oscuro) Color(0xFF8CC49E) else Color(0xFF3F6E4C)
    /** La hoja de boj de la regla: la elegida, abierta a la luz; la otra, plegada y en sombra. */
    private fun bojBalanza(elegida: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(BOJ, Color(0xFF100C08), if (elegida) 0.7f else 0.86f)
        elegida -> mezcla(BOJ, Color.White, 0.55f)
        else -> mezcla(BOJ, NOGAL, 0.24f)
    }

    /** Las rayas de la graduación a lo largo de [y], de [xa] a [xb]: cada 4 dp, más largas cada cinco; hacia abajo si [abajo] es 1, arriba si -1. */
    private fun DrawScope.graduacionRen(xa: Float, xb: Float, y: Float, abajo: Float, color: Color) {
        val u = density
        val paso = maxOf(4 * u, 1f)
        val p = Path(); var x = xa; var k = 0
        while (x <= xb && k < 200) {
            val l = if (k % 5 == 0) 4.5f * u else 2.2f * u
            p.moveTo(x, y); p.lineTo(x, y + abajo * l); x += paso; k++
        }
        drawPath(p, color, style = Stroke(0.6f * u))
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: una regla de boj plegable del taller, graduada por los dos cantos, con su charnela de latón en medio: el
     * gasto en la hoja izquierda y el ingreso en la derecha. La hoja elegida está abierta a la luz, con su cabeza de latón,
     * el arco a sanguina del compás que la mide y el rótulo en negrita; la otra está plegada hacia atrás (más estrecha) y en
     * sombra.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val lat = if (oscuro) LATON else LATON_OSC
        listOf(false, true).forEach { ingreso ->
            val elegida = ingreso == e.ingreso
            val m = if (elegida) 3 * u else 8 * u
            val r = Rect(if (ingreso) w / 2 + 2 * u else 4 * u, m, if (ingreso) w - 4 * u else w / 2 - 2 * u, h - m)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else if (elegida) 0.25f else 0.15f), Offset(r.left + 1.2f * u, r.top + 1.8f * u), r.size)
            val hoja = bojBalanza(elegida, oscuro)
            drawRect(hoja, r.topLeft, r.size)
            drawRect(t(NOGAL, oscuro).copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(0.7f * u))
            val raya = sepia(oscuro, if (elegida) 0.55f else 0.3f)
            graduacionRen(r.left + 6 * u, r.right - 6 * u, r.top, 1f, raya)
            graduacionRen(r.left + 6 * u, r.right - 6 * u, r.bottom, -1f, raya)
            // La cabeza de latón del extremo.
            val xa = if (ingreso) r.right - 7 * u else r.left
            drawRect(if (elegida) lat else lat.copy(alpha = 0.45f), Offset(xa, r.top), Size(7 * u, r.height))
            drawCircle(Color.Black.copy(alpha = 0.3f), 1 * u, Offset(xa + 3.5f * u, r.center.y))
            if (elegida) {
                // El arco del compás a sanguina y la marca de la punta, junto a la cabeza.
                val cx = if (ingreso) r.right - 19 * u else r.left + 19 * u
                val sang = sanguinaPieza(oscuro)
                drawArc(sang.copy(alpha = 0.8f), if (ingreso) 110f else -70f, 140f, false, Offset(cx - 7 * u, r.center.y - 7 * u), Size(14 * u, 14 * u), style = Stroke(1.1f * u, cap = StrokeCap.Round))
                drawCircle(sang, 1.4f * u, Offset(cx, r.center.y))
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegida) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(r.left + 32 * u, r.top + 5 * u, r.right - 32 * u, r.bottom - 5 * u), LetraExtra.cormorant, color, 18f,
                peso = if (elegida) FontWeight.SemiBold else FontWeight.Normal, minimo = 10f)
        }
        // La charnela.
        val c = Offset(w / 2, h / 2)
        drawCircle(Color.Black.copy(alpha = 0.3f), 9 * u, c + Offset(0.8f * u, 1.2f * u))
        drawCircle(lat, 9 * u, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.15f else 0.35f), 6 * u, c, style = Stroke(0.8f * u))
        drawCircle(t(NOGAL, oscuro), 2 * u, c)
        drawLine(if (oscuro) PERGAMINO.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.6f), Offset(c.x - 1.2f * u, c.y), Offset(c.x + 1.2f * u, c.y), 0.6f * u)
    }

    override val altoMes: Dp = 52.dp

    /**
     * El encabezado del mes: un cartiglio, la cinta de pergamino con los extremos enrollados en voluta, con el mes en el medio;
     * en cada extremo, una flecha dibujada a sanguina con su círculo de construcción, que apunta al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val hoja = hojaPieza(oscuro)
        val r = Rect(52 * u, 7 * u, w - 52 * u, h - 7 * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.12f), Offset(r.left + 1.2f * u, r.top + 1.8f * u), r.size)
        drawRect(hoja, r.topLeft, r.size)
        val tinta = sepia(oscuro, 0.7f)
        drawLine(tinta, r.topLeft, Offset(r.right, r.top), 0.9f * u); drawLine(tinta, Offset(r.left, r.bottom), r.bottomRight, 0.9f * u)
        // Los rollos de los extremos.
        listOf(r.left to 1f, r.right to -1f).forEach { (x, s) ->
            drawOval(mezcla(hoja, Color.Black, if (oscuro) 0.25f else 0.12f), Offset(x - 6 * u, r.top - 2 * u), Size(12 * u, r.height + 4 * u))
            drawOval(tinta, Offset(x - 6 * u, r.top - 2 * u), Size(12 * u, r.height + 4 * u), style = Stroke(0.9f * u))
            drawPath(voluta(Offset(x, r.center.y), 4.5f * u, s, 1f), tinta, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
        textoEn(medidor, e.titulo, Rect(r.left + 10 * u, r.top, r.right - 10 * u, r.bottom), LetraExtra.cormorant, legibleMedievo(if (oscuro) PERGAMINO else TINTA, hoja), 23f,
            peso = FontWeight.SemiBold, minimo = 12f)
        val sang = sanguinaPieza(oscuro)
        listOf(true, false).forEach { izquierda ->
            val c = Offset(if (izquierda) 24 * u else w - 24 * u, h / 2)
            val k = if (izquierda) -1f else 1f
            drawCircle(sepia(oscuro, 0.22f), 15 * u, c, style = Stroke(0.6f * u))
            drawLine(sepia(oscuro, 0.22f), Offset(c.x, c.y - 17 * u), Offset(c.x, c.y + 17 * u), 0.5f * u)
            drawLine(sang, Offset(c.x - k * 12 * u, c.y), Offset(c.x + k * 11 * u, c.y), 2 * u, StrokeCap.Round)
            drawLine(sang, Offset(c.x + k * 11 * u, c.y), Offset(c.x + k * 2 * u, c.y - 8 * u), 2 * u, StrokeCap.Round)
            drawLine(sang, Offset(c.x + k * 11 * u, c.y), Offset(c.x + k * 2 * u, c.y + 8 * u), 2 * u, StrokeCap.Round)
            // El sombreado a plumilla de la cola.
            listOf(-9f, -6f, -3f).forEach { d -> drawLine(sang.copy(alpha = 0.5f), Offset(c.x + k * d * u, c.y - 3 * u), Offset(c.x + k * (d + 2) * u, c.y + 3 * u), 0.6f * u) }
        }
    }

    /**
     * El asiento: una raya fina a punta de plata al pie del renglón, con el arranque de la columna del importe a 112 dp del
     * canto; al principio, el sombreado a sanguina del gasto o la marca de compás de verdigrís del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val raya = sepia(oscuro, if (oscuro) 0.2f else 0.25f)
        drawLine(raya, Offset(16 * u, y), Offset(w, y), 0.6f * u)
        drawLine(raya, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.6f * u)
        val c = Offset(6 * u, y - 3.4f * u)
        if (e.ingreso) {
            val v = verderame(oscuro)
            drawCircle(v.copy(alpha = 0.85f), 3.2f * u, c, style = Stroke(1.1f * u)); drawCircle(v, 0.9f * u, c)
        } else listOf(-2.6f, 0f, 2.6f).forEach { d ->
            drawLine(sanguinaPieza(oscuro).copy(alpha = 0.85f), Offset(c.x + d * u - 1.4f * u, c.y + 3 * u), Offset(c.x + d * u + 1.4f * u, c.y - 3 * u), 1.2f * u, StrokeCap.Round)
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: una voluta a sanguina, la fecha en sepia y una regla graduada fina hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF1A130C), Color(0xFF241A11)) else listOf(Color(0xFFF0E4C6), Color(0xFFF7EDD4))
        drawPath(voluta(Offset(6 * u, h / 2), 5 * u, 1f, 1f), sanguinaPieza(oscuro), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        val fin = textoIzqMedievo(medidor, texto, 16 * u, 0f, h, w - 40 * u, LetraExtra.cormorant, legibleMedievo(if (oscuro) PERGAMINO else TINTA, fondos), 14f, FontWeight.SemiBold, 1.6f)
        if (fin <= 16 * u) return
        val y = h / 2 + 2 * u
        val raya = sepia(oscuro, 0.45f)
        drawLine(raya, Offset(fin + 6 * u, y), Offset(w - 2 * u, y), 0.7f * u)
        graduacionRen(fin + 6 * u, w - 2 * u, y, -1f, sepia(oscuro, 0.3f))
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /** La raya: un cabo fino y torcido y, en medio, una rosa de los vientos de latón. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val claro = tenueMedievo(if (oscuro) CABO.copy(alpha = 0.5f) else CABO_OSC.copy(alpha = 0.75f), e)
        val sombra = tenueMedievo(if (oscuro) Color.Black.copy(alpha = 0.6f) else TINTA.copy(alpha = 0.35f), e)
        val hueco = 8 * u
        cuerdaMedievo(4 * u, cx - hueco, y, 1.8f * u, claro, sombra)
        cuerdaMedievo(cx + hueco, w - 4 * u, y, 1.8f * u, claro, sombra)
        val laton = tenueMedievo(if (oscuro) LATON else LATON_OSC, e)
        drawPath(estrella(Offset(cx, y), 3.2f * u, 4, 0.32f, giro = -45f), laton.copy(alpha = laton.alpha * 0.6f))
        drawPath(estrella(Offset(cx, y), 5 * u, 4, 0.26f), laton)
        drawCircle(tenueMedievo(if (oscuro) Color(0xFF7DBFB6) else Color(0xFF1E5157), e), 0.8f * u, Offset(cx, y))
    }

    /** El verde de mar hondo; en modo oscuro, el latón. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD6AA4C) else Color(0xFF1E5157)

    /**
     * El adorno: un cabo torcido bajo el texto con un nudo en cada punta. Al presionarlo, la lona se tiñe y el cabo se tensa
     * (más oscuro y más grueso).
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width
        toqueMedievo(e, (if (oscuro) CABO else LONA).copy(alpha = if (oscuro) 0.13f else 0.4f), 3 * u)
        val y = renglonBotonMedievo()
        val x0 = 12 * u; val x1 = maxOf(w - 12 * u, x0 + 8 * u)
        val vivo = e.presionado && e.habilitado
        val claro = apagadoBotonMedievo(if (oscuro) CABO.copy(alpha = if (vivo) 0.9f else 0.6f) else CABO_OSC.copy(alpha = if (vivo) 1f else 0.8f), e, oscuro)
        val sombra = apagadoBotonMedievo(if (oscuro) Color.Black.copy(alpha = 0.6f) else TINTA.copy(alpha = 0.4f), e, oscuro)
        cuerdaMedievo(x0 + 1.5f * u, x1 - 1.5f * u, y, (if (vivo) 2f else 1.6f) * u, claro, sombra)
        listOf(x0, x1).forEach { x ->
            drawCircle(sombra, 1.9f * u, Offset(x, y))
            drawCircle(claro, 1.4f * u, Offset(x, y))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel de carta náutica de las piezas grandes (vacío, aviso, cifra): en modo oscuro, hondo de verdad. */
    private fun cartaPieza(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF0C0A08), 0.8f) else PAPEL
    /** La tinta de la carta (la sepia clara en modo oscuro), con su transparencia. */
    private fun tintaCarta(oscuro: Boolean, alfa: Float): Color = (if (oscuro) PAPEL else TINTA).copy(alpha = alfa)
    /** El rojo del lacre, encendido en modo oscuro. */
    private fun lacre(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A70) else Color(0xFF8E1A12)
    /** El latón: en el claro, el oscuro para verse sobre el papel. */
    private fun latonPieza(oscuro: Boolean): Color = if (oscuro) LATON else LATON_OSC

    override val margenFila: Dp = 12.dp

    /**
     * La fila: al pie, la derrota trazada en la carta (una línea de trazos). Al tocarla, la lona se tiñe y en el margen se
     * marca la situación del barco: el circulito con su punto.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) {
            drawRect((if (oscuro) CABO else LONA).copy(alpha = if (oscuro) 0.1f else 0.24f))
            drawCircle(tintaCarta(oscuro, 0.8f), 2.6f * u, Offset(6 * u, h / 2), style = Stroke(0.8f * u))
            drawCircle(lacre(oscuro), 0.9f * u, Offset(6 * u, h / 2))
        }
        drawLine(tintaCarta(oscuro, if (oscuro) 0.22f else 0.3f), Offset(4 * u, h - 0.8f * u), Offset(w - 4 * u, h - 0.8f * u), 0.7f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * u, 3 * u)))
    }

    /** La flecha: la punta de la aguja de marear de las cartas, con la mitad de arriba llena y la de abajo en blanco. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val tinta = tintaCarta(oscuro, 0.8f)
        val punta = Offset(f(0.94f), f(0.5f)); val muesca = Offset(f(0.38f), f(0.5f))
        drawPath(Path().apply { moveTo(punta.x, punta.y); lineTo(f(0.12f), f(0.18f)); lineTo(muesca.x, muesca.y); close() }, tinta)
        drawPath(Path().apply { moveTo(punta.x, punta.y); lineTo(f(0.12f), f(0.82f)); lineTo(muesca.x, muesca.y); close() }, tinta, style = Stroke(0.9f * u, join = StrokeJoin.Miter))
    }

    /**
     * La marca fija: la estrella náutica de los marinos (cinco puntas, cada una partida en una mitad de lacre y otra de
     * latón) si está fija; solo su contorno si no. (No una equis: en una fila, una equis se leería como «borrar».)
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.46f; val ri = r * 0.4f
        fun punto(k: Int, rr: Float): Offset {
            val a = Math.toRadians(-90.0 + k * 36.0)
            return Offset(c.x + rr * kotlin.math.cos(a).toFloat(), c.y + rr * kotlin.math.sin(a).toFloat())
        }
        if (fija) {
            val oscura = Path(); val clara = Path()
            for (k in 0 until 5) {
                val tip = punto(2 * k, r)
                listOf(oscura to punto(2 * k - 1, ri), clara to punto(2 * k + 1, ri)).forEach { (p, i) ->
                    p.moveTo(c.x, c.y); p.lineTo(tip.x, tip.y); p.lineTo(i.x, i.y); p.close()
                }
            }
            drawPath(oscura, lacre(oscuro))
            drawPath(clara, latonPieza(oscuro))
        } else {
            drawPath(estrella(c, r, 5, 0.4f), tintaCarta(oscuro, 0.5f), style = Stroke(1 * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 44.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA

    /**
     * El vacío: un trozo de carta náutica sin nada anotado: el papel con su doble filete de borde y las líneas de rumbo que
     * salen de una rosa fuera de la vista, muy tenues.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(cartaPieza(oscuro), Offset.Zero, Size(w, h))
        // Las líneas de rumbo, desde un punto abajo a la derecha (16 rumbos), recortadas al papel.
        val o = Offset(w * (0.7f + 0.2f * azar(e.semilla, 1)), h * 1.1f)
        val largo = w + h
        val rumbos = Path()
        for (k in 0 until 16) {
            val a = Math.toRadians(180.0 + k * 11.25)
            rumbos.moveTo(o.x, o.y); rumbos.lineTo(o.x + largo * kotlin.math.cos(a).toFloat(), o.y + largo * kotlin.math.sin(a).toFloat())
        }
        clipRect(3 * u, 3 * u, w - 3 * u, h - 3 * u) { drawPath(rumbos, tintaCarta(oscuro, if (oscuro) 0.07f else 0.09f), style = Stroke(0.5f * u)) }
        drawRect(tintaCarta(oscuro, 0.45f), Offset(2 * u, 2 * u), Size(w - 4 * u, h - 4 * u), style = Stroke(0.9f * u))
        drawRect(tintaCarta(oscuro, 0.3f), Offset(4 * u, 4 * u), Size(w - 8 * u, h - 8 * u), style = Stroke(0.5f * u))
    }

    /**
     * Lo vacío: el mar en calma, una isla pequeña con su palmera y, en la arena, la equis del tesoro que todavía no se ha
     * marcado (solo punteada).
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = tintaCarta(oscuro, 0.76f)
        val tr = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val isla = Path().apply { moveTo(f(0.16f), f(0.74f)); quadraticTo(f(0.5f), f(0.5f), f(0.84f), f(0.74f)) }
        drawPath(Path().apply { addPath(isla); close() }, (if (oscuro) CABO else LONA).copy(alpha = if (oscuro) 0.2f else 0.55f))
        drawPath(isla, l, style = tr)
        // La palmera: el tronco con sus anillos y cuatro hojas.
        drawPath(Path().apply { moveTo(f(0.5f), f(0.64f)); quadraticTo(f(0.48f), f(0.4f), f(0.58f), f(0.22f)) }, l, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        listOf(0.5f, 0.42f, 0.34f).forEach { y -> drawLine(l.copy(alpha = l.alpha * 0.6f), Offset(f(0.47f), f(y)), Offset(f(0.53f), f(y - 0.02f)), 0.6f * u) }
        listOf(-0.24f, -0.1f, 0.08f, 0.24f).forEach { dx ->
            drawPath(Path().apply { moveTo(f(0.58f), f(0.22f)); quadraticTo(f(0.58f + dx * 0.6f), f(0.1f), f(0.58f + dx), f(0.28f)) }, l, style = tr)
        }
        // La equis, punteada.
        val x = Offset(f(0.66f), f(0.66f)); val r = f(0.04f)
        val punteado = Stroke(1.1f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, 1.8f * u)))
        drawLine(lacre(oscuro), x - Offset(r, r), x + Offset(r, r), punteado.width, punteado.cap, punteado.pathEffect)
        drawLine(lacre(oscuro), x + Offset(r, -r), x + Offset(-r, r), punteado.width, punteado.cap, punteado.pathEffect)
        // Las olas.
        listOf(0.84f, 0.95f).forEachIndexed { k, y ->
            val ola = Path().apply {
                moveTo(f(0.08f + k * 0.08f), f(y))
                quadraticTo(f(0.18f + k * 0.08f), f(y - 0.05f), f(0.28f + k * 0.08f), f(y)); quadraticTo(f(0.38f + k * 0.08f), f(y + 0.05f), f(0.48f + k * 0.08f), f(y))
                quadraticTo(f(0.58f + k * 0.08f), f(y - 0.05f), f(0.68f + k * 0.08f), f(y)); quadraticTo(f(0.78f + k * 0.08f), f(y + 0.05f), f(0.88f + k * 0.04f), f(y))
            }
            drawPath(ola, Color(if (oscuro) 0xFF7DBFB6 else 0xFF1E5157).copy(alpha = 0.6f), style = Stroke(1 * u, cap = StrokeCap.Round))
        }
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA

    /**
     * El aviso: una tira de papel de carta con su doble filete arriba y abajo y, en el margen, un ancla de latón: el arganeo, el cepo, la caña y
     * los dos brazos con sus uñas (un poco ladeada o derecha, según el aviso).
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(cartaPieza(oscuro))
        listOf(1.2f * u, 3 * u, h - 3 * u, h - 1.2f * u).forEachIndexed { k, y ->
            drawLine(tintaCarta(oscuro, if (k % 3 == 0) 0.4f else 0.22f), Offset(0f, y), Offset(w, y), (if (k % 3 == 0) 0.9f else 0.5f) * u)
        }
        val c = Offset(15 * u, minOf(h / 2, 19 * u)); val r = 9 * u
        val laton = latonPieza(oscuro)
        val tr = Stroke(1.5f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        rotate(if (e.semilla % 2 == 0) -12f else 0f, c) {
            drawCircle(laton, 1.8f * u, Offset(c.x, c.y - r), style = Stroke(1.1f * u))
            drawLine(laton, Offset(c.x - 3.6f * u, c.y - r * 0.62f), Offset(c.x + 3.6f * u, c.y - r * 0.62f), tr.width, StrokeCap.Round)
            drawLine(laton, Offset(c.x, c.y - r * 0.8f), Offset(c.x, c.y + r * 0.9f), tr.width, StrokeCap.Round)
            drawArc(laton, 20f, 140f, false, Offset(c.x - r * 0.75f, c.y + r * 0.9f - r * 1.2f), Size(r * 1.5f, r * 1.2f), style = tr)
            listOf(-1f, 1f).forEach { sx ->
                val p = Offset(c.x + sx * r * 0.7f, c.y + r * 0.5f)
                drawPath(Path().apply { moveTo(p.x, p.y - 2.2f * u); lineTo(p.x + sx * 1.4f * u, p.y + 0.4f * u); lineTo(p.x - sx * 1.4f * u, p.y + 0.6f * u); close() }, laton)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    /** El verde de mar hondo; en modo oscuro, el latón. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFD6AA4C) else Color(0xFF1E5157)
    /** El rojo del lacre. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A70) else Color(0xFF8E1A12)

    /**
     * La cifra: un marbete de papel (la etiqueta de una caja de la bodega) con las dos esquinas de la izquierda cortadas, su
     * ollao de latón y el cabito atado que se va por el borde.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        val corte = minOf(7 * u, h / 4)
        val marbete = Path().apply { moveTo(corte, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(corte, h); lineTo(0f, h - corte); lineTo(0f, corte); close() }
        translate(1.4f * u, 1.8f * u) { drawPath(marbete, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f)) }
        drawPath(marbete, cartaPieza(oscuro))
        drawPath(marbete, tintaCarta(oscuro, 0.4f), style = Stroke(0.8f * u, join = StrokeJoin.Miter))
        val o = Offset(5.4f * u, h / 2)
        drawCircle(latonPieza(oscuro), 2.4f * u, o)
        drawCircle(if (oscuro) Color(0xFF0C0A08) else Color(0xFF2A2218), 1.1f * u, o)
        drawLine((if (oscuro) CABO else CABO_OSC).copy(alpha = 0.85f), o, Offset(-1 * u, o.y - 3 * u), 1.1f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta de la carta; en modo oscuro, el papel. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    /** El verde de mar de las sondas. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF8FC4C4) else Color(0xFF1E5157)
    /** El rojo del lacre (encendido en modo oscuro). */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF59078) else Color(0xFF8E1A12)

    /**
     * Las tarjetas del camarote, de papel de carta liso por dentro:
     * - la cifra de Hoy es el recuadro de una carta náutica: el filete y la escala de grados, a tramos negros y blancos;
     *   destacada, la rosa de los vientos de lacre en la esquina;
     * - «Te falta hoy» es el bando clavado al palo: el papel de cantos rotos con un clavo a la izquierda, el puñal clavado en
     *   la esquina y la raya de lacre; completo, el sello de lacre;
     * - la noche es el papel con el mar al pie (las olas a la pluma) y la estrella del norte arriba; sellada, la estrella es de
     *   lacre y luce;
     * - la tarjeta de Vida es el marbete de un cajón: el cabo que pasa por dos ollaos de latón al pie.
     * Al presionarlas, el papel se tiñe del verde del mar.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val carta = cartaPieza(oscuro)
        val toque = (if (oscuro) Color(0xFF8FC4C4) else Color(0xFF1E5157)).copy(alpha = if (oscuro) 0.08f else 0.08f)
        val tinta = tintaCarta(oscuro, if (oscuro) 0.5f else 0.7f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(carta, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // El recuadro y la escala de grados.
                val a = 1.6f * u; val b = 4.2f * u
                drawRect(tinta, Offset(r.left + a, r.top + a), Size(r.width - 2 * a, r.height - 2 * a), style = Stroke(0.6f * u))
                drawRect(tinta, Offset(r.left + b, r.top + b), Size(r.width - 2 * b, r.height - 2 * b), style = Stroke(0.5f * u))
                val tramo = maxOf(6 * u, 1f)
                val escala = Path()
                var x = r.left + b; var k = 0
                while (x < r.right - b && k < 120) {
                    val fin = minOf(x + tramo, r.right - b)
                    if (k % 2 == 0) { escala.addRect(Rect(x, r.top + a, fin, r.top + b)); escala.addRect(Rect(x, r.bottom - b, fin, r.bottom - a)) }
                    x += tramo; k++
                }
                var y = r.top + b; k = 0
                while (y < r.bottom - b && k < 120) {
                    val fin = minOf(y + tramo, r.bottom - b)
                    if (k % 2 == 0) { escala.addRect(Rect(r.left + a, y, r.left + b, fin)); escala.addRect(Rect(r.right - b, y, r.right - a, fin)) }
                    y += tramo; k++
                }
                drawPath(escala, tinta)
                if (e.destacado) {
                    val c = Offset(r.right - 7 * u, r.bottom - 7 * u)
                    drawCircle(carta, 5 * u, c)
                    drawPath(estrella(c, 6.5f * u, 4, 0.28f), lacre(oscuro))
                    drawPath(estrella(c, 3.6f * u, 4, 0.3f, giro = -45f), lacre(oscuro).copy(alpha = 0.6f))
                    drawCircle(carta, 0.8f * u, c)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val hondo = minOf(0.03f, 3 * u / minOf(r.width, r.height).coerceAtLeast(1f))
                val bando = contornoIrregular(Rect(r.left, r.top + 1 * u, r.right - 3 * u, r.bottom), 23 + e.semilla, 64, hondo)
                sombraTarjetaMedievo(bando, e, oscuro)
                drawPath(bando, carta)
                if (e.presionado) drawPath(bando, toque)
                drawPath(bando, tinta.copy(alpha = tinta.alpha * 0.5f), style = Stroke(0.6f * u))
                // La raya de lacre bajo el canto de arriba.
                drawLine(lacre(oscuro), Offset(r.left + 6 * u, r.top + 6 * u), Offset(r.right - 22 * u, r.top + 6 * u), 1.6f * u)
                drawLine(lacre(oscuro).copy(alpha = 0.6f), Offset(r.left + 6 * u, r.top + 8.2f * u), Offset(r.right - 22 * u, r.top + 8.2f * u), 0.5f * u)
                // El clavo de la izquierda.
                val clavo = Offset(r.left + 4.5f * u, r.top + 5.5f * u)
                drawCircle(if (oscuro) Color(0xFF8A847A) else Color(0xFF2A2420), 1.7f * u, clavo)
                drawCircle(Color.White.copy(alpha = 0.3f), 0.6f * u, Offset(clavo.x - 0.5f * u, clavo.y - 0.5f * u))
                // El puñal clavado en la esquina de arriba a la derecha.
                val p = Offset(r.right - 16 * u, r.top + 13 * u); val k = 1.4f * u
                val hoja = Path().apply { moveTo(p.x, p.y); lineTo(p.x + 6 * k, p.y - 5.4f * k); lineTo(p.x + 7.2f * k, p.y - 4.2f * k); close() }
                drawPath(hoja, if (oscuro) Color(0xFFB8B8BE) else Color(0xFF8A8C92))
                drawLine(Color.White.copy(alpha = 0.35f), Offset(p.x + 1 * k, p.y - 0.8f * k), Offset(p.x + 6.2f * k, p.y - 5.2f * k), 0.4f * u)
                drawLine(latonPieza(oscuro), Offset(p.x + 4.4f * k, p.y - 6.6f * k), Offset(p.x + 8.4f * k, p.y - 2.6f * k), 1.4f * k, StrokeCap.Round)
                drawLine(t(CABO_OSC, oscuro), Offset(p.x + 7 * k, p.y - 5.4f * k), Offset(p.x + 9.6f * k, p.y - 8 * k), 2 * k, StrokeCap.Round)
                drawCircle(latonPieza(oscuro), 1.1f * k, Offset(p.x + 10 * k, p.y - 8.4f * k))
                drawLine(Color.Black.copy(alpha = 0.5f), Offset(p.x - 1.2f * k, p.y + 0.4f * k), Offset(p.x + 1.2f * k, p.y - 0.6f * k), 0.7f * u)
                if (e.destacado) {
                    val c = Offset(r.right - 18 * u, r.bottom - minOf(10 * u, r.height / 2 - 2 * u))
                    drawCircle(lacre(oscuro), 6 * u, c)
                    drawCircle(Color.Black.copy(alpha = 0.3f), 4.2f * u, c, style = Stroke(0.7f * u))
                    // El ancla del sello.
                    val s = 2.6f * u; val n = Color.Black.copy(alpha = 0.4f)
                    drawLine(n, Offset(c.x, c.y - s), Offset(c.x, c.y + s), 0.7f * u)
                    drawLine(n, Offset(c.x - s * 0.6f, c.y - s * 0.5f), Offset(c.x + s * 0.6f, c.y - s * 0.5f), 0.7f * u)
                    drawArc(n, 20f, 140f, false, Offset(c.x - s, c.y - s * 0.2f), Size(2 * s, 1.2f * s), style = Stroke(0.7f * u))
                }
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(mezcla(carta, Color(0xFF1A2A3A), if (oscuro) 0.08f else 0.07f), r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, tinta.copy(alpha = tinta.alpha * 0.5f), style = Stroke(0.6f * u))
                // El mar al pie: dos hileras de olas a la pluma.
                val paso = maxOf(9 * u, 1f)
                listOf(r.bottom - 6.5f * u to 0f, r.bottom - 3 * u to 0.5f).forEach { (y, desfase) ->
                    val olas = Path()
                    var x = r.left + 2 * u + desfase * paso; var k = 0
                    olas.moveTo(x, y)
                    while (x < r.right - 2 * u - paso && k < 120) { olas.quadraticTo(x + paso / 2, y - 2.4f * u, x + paso, y); x += paso; k++ }
                    drawPath(olas, tinta.copy(alpha = tinta.alpha * (if (desfase > 0f) 0.6f else 0.9f)), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                }
                // La estrella del norte.
                val c = Offset(r.right - minOf(128 * u, r.width * 0.42f), r.top + 9 * u)
                if (e.destacado) mancha(c, 10 * u, (if (oscuro) Color(0xFFF59078) else Color(0xFFE8A060)).copy(alpha = 0.45f))
                drawPath(estrella(c, 5.5f * u, 4, 0.22f), if (e.destacado) lacre(oscuro) else tinta)
                drawPath(estrella(c, 3 * u, 4, 0.3f, giro = -45f), (if (e.destacado) lacre(oscuro) else tinta).copy(alpha = 0.55f))
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(carta, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, tinta.copy(alpha = tinta.alpha * 0.6f), style = Stroke(0.7f * u))
                drawRect(tinta.copy(alpha = tinta.alpha * 0.35f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5 * u, r.height - 5 * u), style = Stroke(0.4f * u))
                // El cabo al pie, que pasa por los dos ollaos.
                val y = r.bottom - 5 * u
                val o1 = Offset(r.left + 6 * u, y); val o2 = Offset(r.right - 6 * u, y)
                listOf(o1, o2).forEach { o ->
                    drawCircle(latonPieza(oscuro), 2.8f * u, o)
                    drawCircle(if (oscuro) Color(0xFF0C0A08) else Color(0xFF2A2218), 1.4f * u, o)
                }
                cuerdaMedievo(o1.x, o2.x, y, 1.6f * u, t(CABO, oscuro), t(CABO_OSC, oscuro))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El paño de lona de la balanza: el elegido, al sol de cubierta; el otro, a la sombra de la vela. */
    private fun lonaBalanza(elegido: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(LONA, Color(0xFF0C0A08), if (elegido) 0.72f else 0.86f)
        elegido -> mezcla(LONA, Color.White, 0.6f)
        else -> mezcla(LONA, CABO_OSC, 0.25f)
    }

    /** Un ollao: el aro de latón cosido a la lona, con su agujero. */
    private fun DrawScope.ollaoPirata(c: Offset, vivo: Boolean, oscuro: Boolean) {
        val u = density
        val lat = latonPieza(oscuro).let { if (vivo) it else it.copy(alpha = 0.45f) }
        drawCircle(lat, 3.4f * u, c)
        drawCircle(if (oscuro) Color(0xFF0C0A08) else Color(0xFF3A2E1E), 1.8f * u, c)
        drawCircle(Color.White.copy(alpha = if (vivo) 0.35f else 0.1f), 2.6f * u, c, style = Stroke(0.5f * u))
    }

    override val altoBalanza: Dp = 60.dp

    /**
     * La balanza: dos paños de lona cosidos con la costura doble del velero y la relinga (el cabo del canto) arriba: el gasto
     * a la izquierda y el ingreso a la derecha. El paño elegido está al sol, con sus ollaos de latón brillando y la equis de
     * lacre (la del mapa del tesoro) junto al rótulo; el otro queda a la sombra de la vela, con los ollaos apagados.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val (izq, der) = mitadesMedievo(0f, 0f)
        listOf(false to izq, true to der).forEach { (ingreso, r0) ->
            val r = Rect(r0.left, 5 * u, r0.right, h)
            val elegido = ingreso == e.ingreso
            val hoja = lonaBalanza(elegido, oscuro)
            drawRect(hoja, r.topLeft, r.size)
            // El tejido: hilos de la trama muy tenues.
            val hilo = Color.Black.copy(alpha = if (oscuro) 0.18f else 0.05f)
            val paso = maxOf(3 * u, 1f)
            var y = r.top + paso; var k = 0
            while (y < r.bottom && k < 40) { drawLine(hilo, Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += paso; k++ }
            val xo = if (ingreso) r.right - 8 * u else r.left + 8 * u
            ollaoPirata(Offset(xo, r.top + 8 * u), elegido, oscuro); ollaoPirata(Offset(xo, r.bottom - 8 * u), elegido, oscuro)
            if (elegido) {
                val cx = if (ingreso) r.right - 24 * u else r.left + 24 * u
                val c = Offset(cx, r.center.y)
                val lac = lacre(oscuro)
                drawLine(lac, Offset(c.x - 5 * u, c.y - 5 * u), Offset(c.x + 5 * u, c.y + 5 * u), 2.6f * u, StrokeCap.Round)
                drawLine(lac, Offset(c.x + 5 * u, c.y - 5 * u), Offset(c.x - 5 * u, c.y + 5 * u), 2.6f * u, StrokeCap.Round)
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            // El color se mide contra el paño con su trama encima (lo más oscuro que queda bajo el rótulo).
            val bajo = mezcla(hoja, Color.Black, if (oscuro) 0f else 0.06f)
            val color = if (elegido) legibleMedievo(propio, bajo) else tenueLegibleMedievo(propio, bajo)
            textoEn(medidor, texto, Rect(r.left + 34 * u, r.top + 4 * u, r.right - 34 * u, r.bottom - 4 * u), LetraExtra.caslon, color, 17f,
                peso = if (elegido) FontWeight.Bold else FontWeight.Normal, minimo = 10f)
        }
        // La costura doble del medio y la relinga de arriba.
        val hilo = Color.Black.copy(alpha = if (oscuro) 0.55f else 0.35f)
        listOf(-2 * u, 2 * u).forEach { d ->
            drawLine(hilo, Offset(w / 2 + d, 5 * u), Offset(w / 2 + d, h), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u)))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(0f, 5 * u), Size(w, h - 5 * u), style = Stroke(0.8f * u))
        cuerdaMedievo(0f, w, 4 * u, 4 * u, t(CABO, oscuro), t(CABO_OSC, oscuro))
    }

    override val altoMes: Dp = 50.dp

    /**
     * El encabezado del mes: una tira de carta de marear con las líneas de rumbo que salen de un punto del medio, y el mes
     * encima; en cada extremo, una punta de la rosa de los vientos (mitad tinta y mitad papel) con su asta, que apunta al mes
     * de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val hoja = cartaPieza(oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(1.2f * u, 1.8f * u), Size(w - 1.2f * u, h - 1.8f * u))
        drawRect(hoja, Offset.Zero, Size(w - 1.2f * u, h - 1.8f * u))
        // Las líneas de rumbo, desde un punto bajo el mes.
        val c = Offset(w / 2, h / 2)
        val rumbo = tintaCarta(oscuro, 0.12f)
        clipRect(0f, 0f, w - 1.2f * u, h - 1.8f * u) {
            for (k in 0 until 16) {
                val a = k * Math.PI / 8
                drawLine(rumbo, c, Offset(c.x + (w * kotlin.math.cos(a)).toFloat(), c.y + (w * kotlin.math.sin(a)).toFloat()), 0.5f * u)
            }
        }
        drawRect(tintaCarta(oscuro, 0.6f), Offset(2.5f * u, 2.5f * u), Size(w - 6.2f * u, h - 6.8f * u), style = Stroke(0.7f * u))
        val texto = Rect(52 * u, 4 * u, w - 52 * u, h - 6 * u)
        // Un velo de papel bajo el mes, para que los rumbos no lo crucen.
        drawRect(hoja, Offset(texto.left + 12 * u, texto.top + 5 * u), Size(texto.width - 24 * u, texto.height - 10 * u))
        textoEn(medidor, e.titulo, texto, LetraExtra.caslon, legibleMedievo(if (oscuro) PAPEL else TINTA, hoja), 21f, peso = FontWeight.SemiBold, minimo = 12f)
        listOf(true, false).forEach { izquierda ->
            val p = Offset(if (izquierda) 24 * u else w - 25 * u, (h - 1.8f * u) / 2)
            val k = if (izquierda) -1f else 1f
            val tinta = tintaCarta(oscuro, 1f)
            // La punta de la rosa: ancha atrás y larga hacia fuera, mitad tinta y mitad papel, con su asta.
            val punta = Offset(p.x + k * 13 * u, p.y); val cola = Offset(p.x - k * 5 * u, p.y)
            drawLine(tinta, cola, Offset(p.x - k * 14 * u, p.y), 1.8f * u, StrokeCap.Round)
            drawPath(Path().apply { moveTo(cola.x, cola.y); lineTo(p.x - k * 3 * u, p.y - 9 * u); lineTo(punta.x, punta.y); close() }, tinta)
            drawPath(Path().apply { moveTo(cola.x, cola.y); lineTo(p.x - k * 3 * u, p.y + 9 * u); lineTo(punta.x, punta.y); close() }, hoja)
            drawPath(Path().apply { moveTo(cola.x, cola.y); lineTo(p.x - k * 3 * u, p.y - 9 * u); lineTo(punta.x, punta.y); lineTo(p.x - k * 3 * u, p.y + 9 * u); close() }, tinta,
                style = Stroke(1.2f * u, join = StrokeJoin.Miter))
            drawCircle(lacre(oscuro), 1.6f * u, Offset(p.x - k * 14 * u, p.y))
        }
    }

    /**
     * El asiento: un renglón del cuaderno de bitácora, con la raya de tinta al pie y el arranque de la columna del importe a
     * 112 dp del canto; al principio, la gota de lacre del gasto o el doblón de latón del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val raya = tintaCarta(oscuro, if (oscuro) 0.2f else 0.22f)
        drawLine(raya, Offset(16 * u, y), Offset(w, y), 0.7f * u)
        drawLine(raya, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.7f * u)
        val c = Offset(6 * u, y - 3.4f * u)
        if (e.ingreso) {
            drawCircle(latonPieza(oscuro), 3.6f * u, c)
            drawCircle(Color.Black.copy(alpha = 0.3f), 2.4f * u, c, style = Stroke(0.5f * u))
        } else drawPath(contornoIrregular(Rect(c.x - 3.6f * u, c.y - 3.2f * u, c.x + 3.6f * u, c.y + 3.2f * u), e.semilla, 9, 0.18f), lacre(oscuro).copy(alpha = 0.9f))
    }

    override val altoRotuloDia: Dp = 22.dp

    /** El rótulo del día: una estrella de rumbo de cuatro puntas, la fecha en tinta y un cabo fino hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF100E0B), Color(0xFF1A1712)) else listOf(Color(0xFFE6DCBF), Color(0xFFF0E8D0))
        val tinta = legibleMedievo(tintaCarta(oscuro, 1f), fondos)
        drawPath(estrella(Offset(6 * u, h / 2), 6.5f * u, 4, 0.28f, -90f), tinta)
        drawCircle(lacre(oscuro), 1.2f * u, Offset(6 * u, h / 2))
        val fin = textoIzqMedievo(medidor, texto, 16 * u, 0f, h, w - 40 * u, LetraExtra.caslon, tinta, 12.5f, FontWeight.Bold, 1.5f)
        if (fin > 16 * u) cuerdaMedievo(fin + 6 * u, w - 2 * u, h / 2 + 1 * u, 2.2f * u, t(CABO, oscuro), t(CABO_OSC, oscuro))
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

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La raya: el filete inglés de la imprenta (grueso en el centro y afilado hacia las puntas), partido en medio por un
     * florón: la hoja de hiedra de los cajistas, en rojo.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2; val cx = w / 2
        val tinta = tenueMedievo(if (oscuro) PAPEL.copy(alpha = 0.34f) else TINTA.copy(alpha = 0.5f), e)
        val hueco = 7 * u; val g = 1.4f * u
        drawPath(Path().apply {
            moveTo(4 * u, y); lineTo(cx - hueco, y - g / 2); lineTo(cx - hueco, y + g / 2); close()
            moveTo(w - 4 * u, y); lineTo(cx + hueco, y - g / 2); lineTo(cx + hueco, y + g / 2); close()
        }, tinta)
        // La hoja de hiedra: dos lóbulos y la punta hacia abajo, con su tallito.
        val rojo = tenueMedievo(if (oscuro) Color(0xFFEE6E78) else ROJO, e)
        val l = 1.5f * u
        drawPath(Path().apply {
            addOval(Rect(Offset(cx - 1.25f * u, y - 1f * u), l))
            addOval(Rect(Offset(cx + 1.25f * u, y - 1f * u), l))
            moveTo(cx + 2.7f * u, y - 0.5f * u); lineTo(cx - 2.7f * u, y - 0.5f * u); lineTo(cx, y + 3f * u); close()
        }, rojo)
        drawLine(rojo, Offset(cx, y - 2.2f * u), Offset(cx + 1.2f * u, y - 3.6f * u), 0.7f * u, StrokeCap.Round)
    }

    /** El rojo de la escarapela (más claro en modo oscuro). */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFEE6E78) else Color(0xFFB22234)

    /**
     * El adorno: las comillas angulares « » de la imprenta, en azul, a cada lado del texto. Al presionarlo, el papel se
     * entinta un poco y las comillas se cierran hacia el texto.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density; val w = size.width; val y = size.height / 2
        toqueMedievo(e, (if (oscuro) PAPEL else TINTA).copy(alpha = if (oscuro) 0.1f else 0.07f), 2 * u)
        val vivo = e.presionado && e.habilitado
        val azul = apagadoBotonMedievo((if (oscuro) Color(0xFF9CAEF0) else AZUL).copy(alpha = if (vivo) 1f else 0.7f), e, oscuro)
        val dentro = if (vivo) 1.2f * u else 0f
        val a = 2.4f * u; val b = 1.8f * u; val sep = 2.2f * u
        val trazo = Stroke(1.1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val p = Path()
        listOf(0f, sep).forEach { d ->
            val xi = 4 * u + dentro + d
            p.moveTo(xi + b, y - a); p.lineTo(xi, y); p.lineTo(xi + b, y + a)
            val xd = w - 4 * u - dentro - d
            p.moveTo(xd - b, y - a); p.lineTo(xd, y); p.lineTo(xd - b, y + a)
        }
        drawPath(p, azul, style = trazo)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel de cartel de las piezas grandes (vacío, aviso, cifra): en modo oscuro, hondo de verdad. */
    private fun cartelPieza(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, Color(0xFF0B0D0C), 0.8f) else PAPEL
    /** La tinta de imprenta (el papel en modo oscuro), con su transparencia. */
    private fun tintaPrensa(oscuro: Boolean, alfa: Float): Color = (if (oscuro) PAPEL else TINTA).copy(alpha = alfa)
    /** El azul de la escarapela, aclarado en modo oscuro. */
    private fun azulPieza(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CAEF0) else AZUL
    /** El rojo de la escarapela, aclarado en modo oscuro. */
    private fun rojoPieza(oscuro: Boolean): Color = if (oscuro) Color(0xFFEE6E78) else ROJO
    /** Los tres colores de la escarapela, de dentro hacia afuera. */
    private fun tricolor(oscuro: Boolean): List<Color> = listOf(azulPieza(oscuro), if (oscuro) Color(0xFFF0EADB) else Color.White, rojoPieza(oscuro))

    override val margenFila: Dp = 12.dp

    /**
     * La fila: al pie, un filete fino de imprenta. Al tocarla, el papel se entinta un poco y en el margen se imprimen las
     * tres franjas de la bandera.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (e.presionado) {
            drawRect(tintaPrensa(oscuro, if (oscuro) 0.08f else 0.06f))
            val cols = listOf(azulPieza(oscuro), if (oscuro) Color(0xFFF0EADB) else Color.White, rojoPieza(oscuro))
            cols.forEachIndexed { k, c -> drawRect(c, Offset((2.5f + k * 2.2f) * u, 9 * u), Size(1.8f * u, h - 18 * u)) }
            drawRect(tintaPrensa(oscuro, 0.35f), Offset(2.5f * u, 9 * u), Size(6.2f * u, h - 18 * u), style = Stroke(0.4f * u))
        }
        drawLine(tintaPrensa(oscuro, if (oscuro) 0.22f else 0.3f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.6f * u)
    }

    /** La flecha: la manecilla de los impresores (☞), con el puño de la camisa, la mano cerrada y el índice que señala. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val tinta = tintaPrensa(oscuro, 0.8f)
        val hueco = if (oscuro) Color(0xFF131417) else Color(0xFFF2ECDD)
        // El puño de la camisa, en blanco con su contorno; la mano cerrada y el índice estirado.
        drawRect(tinta, Offset(f(0.05f), f(0.3f)), Size(f(0.14f), f(0.46f)), style = Stroke(1 * u))
        drawRoundRect(tinta, Offset(f(0.2f), f(0.28f)), Size(f(0.38f), f(0.5f)), CornerRadius(f(0.16f)))
        drawRoundRect(tinta, Offset(f(0.4f), f(0.28f)), Size(f(0.58f), f(0.13f)), CornerRadius(f(0.065f)))
        // Los dedos doblados, marcados con rayas del papel.
        listOf(0.5f, 0.62f).forEach { y -> drawLine(hueco, Offset(f(0.42f), f(y)), Offset(f(0.58f), f(y)), 0.7f * u) }
        drawLine(hueco, Offset(f(0.42f), f(0.41f)), Offset(f(0.58f), f(0.41f)), 0.7f * u)
    }

    /** La marca fija: la escarapela tricolor (azul al centro, blanco y rojo) si está fija; solo sus tres anillos si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2); val r = size.minDimension * 0.44f
        if (fija) {
            val (azul, blanco, rojo) = tricolor(oscuro)
            drawCircle(rojo, r, c)
            // Los pliegues del borde: unas muescas claras alrededor.
            for (k in 0 until 16) {
                val a = Math.toRadians(k * 22.5)
                val d = Offset(kotlin.math.cos(a).toFloat(), kotlin.math.sin(a).toFloat())
                drawLine(Color.Black.copy(alpha = 0.18f), c + d * (r * 0.8f), c + d * r, 0.6f * u)
            }
            drawCircle(blanco, r * 0.66f, c)
            drawCircle(azul, r * 0.36f, c)
        } else {
            val tenue = tintaPrensa(oscuro, 0.5f)
            drawCircle(tenue, r, c, style = Stroke(1 * u))
            drawCircle(tenue, r * 0.62f, c, style = Stroke(0.7f * u))
            drawCircle(tenue, r * 0.3f, c, style = Stroke(0.7f * u))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA

    /**
     * El vacío: un cartel en blanco pegado en la pared: el papel con su orla tipográfica (el filete grueso y el fino) y un
     * cuadradito de adorno en cada esquina, sin nada impreso.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(cartelPieza(oscuro), Offset.Zero, Size(w, h))
        val tinta = tintaPrensa(oscuro, if (oscuro) 0.5f else 0.62f)
        drawRect(tinta, Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), style = Stroke(1.8f * u))
        drawRect(tinta, Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), style = Stroke(0.5f * u))
        listOf(Offset(3 * u, 3 * u), Offset(w - 3 * u, 3 * u), Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3 * u)).forEach { o ->
            drawRect(cartelPieza(oscuro), o - Offset(2.6f * u, 2.6f * u), Size(5.2f * u, 5.2f * u))
            drawRect(rojoPieza(oscuro), o - Offset(1.8f * u, 1.8f * u), Size(3.6f * u, 3.6f * u))
        }
    }

    /**
     * Lo vacío: la urna sin papeletas: la caja con sus patas, la tapa con la ranura y, al frente, la escarapela tricolor.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density; val s = size.minDimension
        fun f(v: Float) = v * s
        val l = tintaPrensa(oscuro, 0.78f)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRect((if (oscuro) PAPEL else Color.White).copy(alpha = if (oscuro) 0.06f else 0.5f), Offset(f(0.18f), f(0.36f)), Size(f(0.64f), f(0.48f)))
        drawRect(l, Offset(f(0.18f), f(0.36f)), Size(f(0.64f), f(0.48f)), style = tr)
        drawPath(Path().apply { moveTo(f(0.14f), f(0.36f)); lineTo(f(0.24f), f(0.26f)); lineTo(f(0.76f), f(0.26f)); lineTo(f(0.86f), f(0.36f)); close() }, l, style = tr)
        drawLine(l, Offset(f(0.4f), f(0.31f)), Offset(f(0.6f), f(0.31f)), 2 * u, StrokeCap.Butt)
        drawLine(l, Offset(f(0.24f), f(0.84f)), Offset(f(0.24f), f(0.92f)), tr.width)
        drawLine(l, Offset(f(0.76f), f(0.84f)), Offset(f(0.76f), f(0.92f)), tr.width)
        val c = Offset(f(0.5f), f(0.6f))
        val (azul, blanco, rojo) = tricolor(oscuro)
        drawCircle(rojo, f(0.12f), c); drawCircle(blanco, f(0.08f), c); drawCircle(azul, f(0.04f), c)
        // Las dos cintas que cuelgan de la escarapela.
        drawLine(rojo, c + Offset(-f(0.03f), f(0.1f)), c + Offset(-f(0.07f), f(0.2f)), 1.4f * u)
        drawLine(azul, c + Offset(f(0.03f), f(0.1f)), c + Offset(f(0.07f), f(0.2f)), 1.4f * u)
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA

    /**
     * El aviso: una tira de papel de cartel con su filete y, en el margen, el gorro frigio rojo con su escarapela; en la
     * mitad de los avisos, la escarapela sola.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        drawRect(cartelPieza(oscuro))
        drawLine(tintaPrensa(oscuro, 0.5f), Offset(0f, 1 * u), Offset(w, 1 * u), 1.4f * u)
        drawLine(tintaPrensa(oscuro, 0.4f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.6f * u)
        val c = Offset(15 * u, minOf(h / 2, 19 * u))
        val (azul, blanco, rojo) = tricolor(oscuro)
        if (e.semilla % 2 == 0) {
            // El gorro: la copa que se dobla hacia delante y el borde de abajo.
            val gorro = Path().apply {
                moveTo(c.x - 7 * u, c.y + 6 * u); lineTo(c.x - 7 * u, c.y)
                quadraticTo(c.x - 6 * u, c.y - 8 * u, c.x + 2 * u, c.y - 8 * u)
                quadraticTo(c.x + 8 * u, c.y - 8 * u, c.x + 7 * u, c.y - 2 * u)
                quadraticTo(c.x + 4 * u, c.y - 5 * u, c.x + 2.5f * u, c.y - 1 * u)
                lineTo(c.x + 7 * u, c.y + 6 * u); close()
            }
            drawPath(gorro, rojo)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(c.x - 7 * u, c.y + 4 * u), Size(14 * u, 2 * u))
            val o = Offset(c.x - 3.6f * u, c.y + 1.4f * u)
            drawCircle(azul, 2.6f * u, o); drawCircle(blanco, 1.7f * u, o); drawCircle(rojo, 0.8f * u, o)
        } else {
            drawCircle(rojo, 8 * u, c); drawCircle(blanco, 5.4f * u, c); drawCircle(azul, 2.8f * u, c)
        }
    }

    override val margenCifra: Dp = 10.dp
    /** La tinta de imprenta. */
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    /** El azul de la escarapela. */
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CAEF0) else AZUL

    /**
     * La cifra: un recorte de cartel con su orla de imprenta (el filete grueso por fuera, el fino por dentro) y, arriba, las
     * tres franjas de la bandera en una tira corta.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density; val w = size.width - 1.4f * u; val h = size.height - 1.8f * u
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(1.4f * u, 1.8f * u), Size(w, h))
        drawRect(cartelPieza(oscuro), Offset.Zero, Size(w, h))
        val tinta = tintaPrensa(oscuro, if (oscuro) 0.55f else 0.7f)
        drawRect(tinta, Offset(1.2f * u, 1.2f * u), Size(w - 2.4f * u, h - 2.4f * u), style = Stroke(1.6f * u))
        drawRect(tinta, Offset(4 * u, 4 * u), Size(w - 8 * u, h - 8 * u), style = Stroke(0.5f * u))
        val ancho = minOf(5 * u, (w - 20 * u) / 3).coerceAtLeast(1f)
        val x0 = w - 7 * u - 3 * ancho
        tricolor(oscuro).forEachIndexed { k, c -> drawRect(c, Offset(x0 + k * ancho, 5.5f * u), Size(ancho, 2.6f * u)) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp
    /** La tinta de imprenta; en modo oscuro, el papel. */
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    /** El azul de la escarapela. */
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFA4B4F0) else AZUL
    /** El rojo de la escarapela, oscurecido para leerse sobre el papel (aclarado en modo oscuro). */
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF2878E) else Color(0xFF9E1F22)

    /** La escarapela: los tres anillos (rojo, blanco y azul de fuera adentro) y el botón del centro. */
    private fun DrawScope.escarapela(c: Offset, r: Float, oscuro: Boolean) {
        val u = density
        val (azul, blanco, rojo) = tricolor(oscuro)
        drawCircle(rojo, r, c); drawCircle(blanco, r * 0.68f, c); drawCircle(azul, r * 0.38f, c)
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c, style = Stroke(0.5f * u))
        for (k in 0 until 12) {
            val a = k * 30.0 * Math.PI / 180
            drawLine(Color.Black.copy(alpha = 0.12f), c + Offset((r * 0.4f * kotlin.math.cos(a)).toFloat(), (r * 0.4f * kotlin.math.sin(a)).toFloat()),
                c + Offset((r * kotlin.math.cos(a)).toFloat(), (r * kotlin.math.sin(a)).toFloat()), 0.4f * u)
        }
    }

    /**
     * Las tarjetas de la imprenta, de papel de cartel liso por dentro:
     * - la cifra de Hoy es un asignado: el papel moneda con su orla de guilloquis (dos ondas que se cruzan) arriba y abajo y
     *   un medallón en cada esquina; destacada, el timbre rojo en la esquina;
     * - «Te falta hoy» es el decreto pegado en la pared: los filetes gruesos y finos de imprenta arriba y abajo, y la
     *   escarapela con sus dos cintas; completo, los filetes van en azul;
     * - la noche es la calle: la cuerda que la cruza arriba y el reverbero colgado de ella; sellada, el farol se enciende;
     * - la tarjeta de Vida es un recorte de la gaceta: la doble raya de la cabecera, la columna de renglones por la derecha
     *   y el filete del pie.
     * Al presionarlas, el papel se entinta un poco.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val r = sitioTarjetaMedievo(e)
        val papel = cartelPieza(oscuro)
        val toque = tintaPrensa(oscuro, if (oscuro) 0.05f else 0.07f)
        val tinta = tintaPrensa(oscuro, if (oscuro) 0.5f else 0.75f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(papel, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawRect(tinta, Offset(r.left + 1.5f * u, r.top + 1.5f * u), Size(r.width - 3 * u, r.height - 3 * u), style = Stroke(0.6f * u))
                // Las guilloquis: dos ondas que se cruzan, arriba y abajo, entre los medallones.
                val onda = maxOf(8 * u, 1f); val amp = 1.5f * u
                listOf(r.top + 4.5f * u, r.bottom - 4.5f * u).forEach { y ->
                    listOf(0f, 3.1416f).forEach { fase ->
                        val p = Path()
                        val xa = r.left + 8 * u; val xb = r.right - 8 * u
                        val n = ((xb - xa) / maxOf(1.2f * u, 1f)).toInt().coerceIn(2, 400)
                        for (s in 0..n) {
                            val x = xa + (xb - xa) * s / n
                            val yy = y + amp * kotlin.math.sin(fase + (x - xa) / onda * 6.2832f)
                            if (s == 0) p.moveTo(x, yy) else p.lineTo(x, yy)
                        }
                        drawPath(p, if (fase == 0f) tinta else azulPieza(oscuro).copy(alpha = 0.8f), style = Stroke(0.5f * u))
                    }
                }
                listOf(Offset(r.left + 4.5f * u, r.top + 4.5f * u), Offset(r.right - 4.5f * u, r.top + 4.5f * u),
                    Offset(r.left + 4.5f * u, r.bottom - 4.5f * u), Offset(r.right - 4.5f * u, r.bottom - 4.5f * u)).forEach { o ->
                    drawCircle(tinta, 2.6f * u, o, style = Stroke(0.6f * u)); drawCircle(tinta, 1 * u, o)
                }
                if (e.destacado) {
                    val c = Offset(r.right - 7.5f * u, r.bottom - 7.5f * u)
                    val rojo = rojoPieza(oscuro).copy(alpha = 0.85f)
                    drawCircle(papel, 6 * u, c)
                    drawCircle(rojo, 6 * u, c, style = Stroke(1 * u))
                    drawCircle(rojo, 4.2f * u, c, style = Stroke(0.5f * u))
                    drawPath(estrella(c, 2.8f * u, 5, 0.42f), rojo)
                    drawPath(forma, rojoPieza(oscuro), style = Stroke(1.3f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(papel, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                // Los filetes: grueso y fino arriba, fino y grueso abajo.
                val filete = if (e.destacado) azulPieza(oscuro) else tintaPrensa(oscuro, if (oscuro) 0.75f else 0.9f)
                val xb = r.right - 22 * u
                drawRect(filete, Offset(r.left + 3 * u, r.top + 3 * u), Size(xb - r.left - 3 * u, 2.4f * u))
                drawRect(filete, Offset(r.left + 3 * u, r.top + 6.6f * u), Size(xb - r.left - 3 * u, 0.6f * u))
                drawRect(filete, Offset(r.left + 3 * u, r.bottom - 7.2f * u), Size(r.width - 6 * u, 0.6f * u))
                drawRect(filete, Offset(r.left + 3 * u, r.bottom - 5.4f * u), Size(r.width - 6 * u, 2.4f * u))
                drawPath(forma, tinta.copy(alpha = tinta.alpha * 0.4f), style = Stroke(0.6f * u))
                // La escarapela con sus dos cintas.
                val c = Offset(r.right - 11 * u, r.top + 9 * u)
                val (azul, _, rojo) = tricolor(oscuro)
                drawPath(Path().apply { moveTo(c.x - 1 * u, c.y); lineTo(c.x - 4.5f * u, c.y + 12 * u); lineTo(c.x - 2.6f * u, c.y + 10.6f * u); lineTo(c.x - 1.6f * u, c.y + 12.6f * u); lineTo(c.x + 1.4f * u, c.y); close() }, azul)
                drawPath(Path().apply { moveTo(c.x + 1 * u, c.y); lineTo(c.x + 4.5f * u, c.y + 12 * u); lineTo(c.x + 2.6f * u, c.y + 10.6f * u); lineTo(c.x + 1.6f * u, c.y + 12.6f * u); lineTo(c.x - 1.4f * u, c.y); close() }, rojo)
                escarapela(c, 7 * u, oscuro)
            }
            TipoTarjeta.NOCHE -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(mezcla(papel, Color(0xFF20263A), if (oscuro) 0.06f else 0.06f), r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, tinta.copy(alpha = tinta.alpha * 0.4f), style = Stroke(0.6f * u))
                // La cuerda que cruza la calle y el reverbero colgado.
                val xc = r.right - minOf(128 * u, r.width * 0.42f)
                val caida = 5 * u
                val cuerda = Path().apply { moveTo(r.left, r.top + 1 * u); lineTo(xc, r.top + caida); lineTo(r.right, r.top + 1 * u) }
                drawPath(cuerda, tinta.copy(alpha = tinta.alpha * 0.7f), style = Stroke(0.6f * u))
                val hierro = if (oscuro) Color(0xFF9AA0A6) else HIERRO
                val y0 = r.top + caida
                scale(1.4f, pivot = Offset(xc, y0)) {
                    drawLine(hierro, Offset(xc, y0), Offset(xc, y0 + 2 * u), 0.7f * u)
                    drawPath(Path().apply { moveTo(xc - 4 * u, y0 + 4.5f * u); lineTo(xc, y0 + 2 * u); lineTo(xc + 4 * u, y0 + 4.5f * u); close() }, hierro)
                    val vidrio = Path().apply { moveTo(xc - 3.6f * u, y0 + 4.5f * u); lineTo(xc + 3.6f * u, y0 + 4.5f * u); lineTo(xc + 2.4f * u, y0 + 11 * u); lineTo(xc - 2.4f * u, y0 + 11 * u); close() }
                    if (e.destacado) {
                        mancha(Offset(xc, y0 + 8 * u), 16 * u, Color(0xFFFFC860).copy(alpha = if (oscuro) 0.4f else 0.5f))
                        drawPath(vidrio, Color(0xFFFFD27A))
                        drawCircle(Color(0xFFFFF2C0), 1.4f * u, Offset(xc, y0 + 8 * u))
                    } else drawPath(vidrio, if (oscuro) Color(0xFF2A2C30) else Color(0xFF5A5E66))
                    drawPath(vidrio, hierro, style = Stroke(0.7f * u))
                    drawLine(hierro, Offset(xc, y0 + 4.5f * u), Offset(xc, y0 + 11 * u), 0.4f * u)
                    drawLine(hierro, Offset(xc - 2.4f * u, y0 + 11.6f * u), Offset(xc + 2.4f * u, y0 + 11.6f * u), 1.2f * u)
                }
            }
            TipoTarjeta.VIDA -> {
                val forma = rectMedievo(r)
                sombraTarjetaMedievo(forma, e, oscuro)
                drawRect(papel, r.topLeft, r.size)
                if (e.presionado) drawRect(toque, r.topLeft, r.size)
                drawPath(forma, tinta.copy(alpha = tinta.alpha * 0.4f), style = Stroke(0.6f * u))
                // La cabecera: la raya gruesa y la fina; el filete del pie.
                drawRect(tinta, Offset(r.left + 3 * u, r.top + 2.5f * u), Size(r.width - 6 * u, 1.8f * u))
                drawRect(tinta, Offset(r.left + 3 * u, r.top + 5.4f * u), Size(r.width - 6 * u, 0.5f * u))
                drawRect(tinta, Offset(r.left + 3 * u, r.bottom - 3 * u), Size(r.width - 6 * u, 0.5f * u))
                // La columna de renglones por la derecha, con su corondel.
                val xr = r.right - 10.5f * u
                drawLine(tinta.copy(alpha = tinta.alpha * 0.6f), Offset(xr, r.top + 8 * u), Offset(xr, r.bottom - 5 * u), 0.4f * u)
                val renglones = Path()
                val paso = maxOf(2.6f * u, 1f)
                var y = r.top + 9 * u; var k = 0
                while (y < r.bottom - 6 * u && k < 80) {
                    val largo = if (k % 7 == 6) 3.5f * u else 7 * u
                    renglones.moveTo(xr + 1.8f * u, y); renglones.lineTo(xr + 1.8f * u + largo, y); y += paso; k++
                }
                drawPath(renglones, tintaPrensa(oscuro, if (oscuro) 0.3f else 0.4f), style = Stroke(0.9f * u))
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** El papel de un pasquín de la balanza: el elegido, recién pegado; el otro, viejo y amarillento. */
    private fun pasquin(elegido: Boolean, oscuro: Boolean): Color = when {
        oscuro -> mezcla(PAPEL, Color(0xFF0B0D0C), if (elegido) 0.72f else 0.86f)
        elegido -> mezcla(PAPEL, Color.White, 0.6f)
        else -> mezcla(PAPEL, Color(0xFF9C8E6C), 0.34f)
    }

    /** Los filetes de imprenta, grueso y fino, de [xa] a [xb] a la altura [y]; el fino hacia dentro ([dentro] = 1 abajo, -1 arriba). */
    private fun DrawScope.filetesRev(xa: Float, xb: Float, y: Float, dentro: Float, color: Color) {
        val u = density
        drawLine(color, Offset(xa, y), Offset(xb, y), 2 * u)
        drawLine(color, Offset(xa, y + dentro * 2.6f * u), Offset(xb, y + dentro * 2.6f * u), 0.6f * u)
    }

    /** La manecilla de imprenta (☞), de [s] de ancho con centro en [c], señalando a la izquierda o a la derecha. */
    private fun DrawScope.manecillaRev(c: Offset, s: Float, izquierda: Boolean, tinta: Color, papel: Color) {
        val u = density
        fun p(x: Float, y: Float) = Offset(c.x + x * s, c.y + y * s)
        scale(if (izquierda) -1f else 1f, 1f, pivot = c) {
            // El puño de la manga.
            drawRect(tinta, p(-0.5f, -0.2f), Size(0.14f * s, 0.42f * s))
            // La mano cerrada y el índice estirado.
            drawRoundRect(papel, p(-0.34f, -0.22f), Size(0.4f * s, 0.46f * s), CornerRadius(0.12f * s))
            drawRoundRect(tinta, p(-0.34f, -0.22f), Size(0.4f * s, 0.46f * s), CornerRadius(0.12f * s), style = Stroke(1.3f * u))
            drawRoundRect(papel, p(0.02f, -0.22f), Size(0.48f * s, 0.15f * s), CornerRadius(0.075f * s))
            drawRoundRect(tinta, p(0.02f, -0.22f), Size(0.48f * s, 0.15f * s), CornerRadius(0.075f * s), style = Stroke(1.3f * u))
            // Los dedos doblados.
            listOf(-0.02f, 0.1f).forEach { y -> drawLine(tinta, p(-0.04f, y), p(0.08f, y), 1.1f * u, StrokeCap.Round) }
            drawArc(tinta, -90f, 180f, false, p(-0.02f, -0.06f), Size(0.14f * s, 0.28f * s), style = Stroke(1.1f * u))
        }
    }

    override val altoBalanza: Dp = 58.dp

    /**
     * La balanza: dos pasquines pegados en el muro, el del gasto a la izquierda y el del ingreso a la derecha, con los
     * filetes grueso y fino de la imprenta arriba y abajo. El elegido está recién pegado: blanco, con la escarapela tricolor
     * prendida en su esquina y el rótulo en negrita; el otro está viejo, amarillento, con los filetes gastados y la esquina
     * de abajo despegada.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val (izq, der) = mitadesMedievo(4 * u, 8 * u)
        listOf(false to izq, true to der).forEach { (ingreso, r) ->
            val elegido = ingreso == e.ingreso
            val hoja = pasquin(elegido, oscuro)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else if (elegido) 0.22f else 0.12f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
            // La esquina de abajo de fuera, despegada en el viejo.
            val doblez = 9 * u
            val ex = if (ingreso) r.right else r.left; val k = if (ingreso) -1f else 1f
            val forma = Path().apply {
                addRect(r)
                if (!elegido) { reset(); moveTo(r.left, r.top); lineTo(r.right, r.top)
                    if (ingreso) { lineTo(r.right, r.bottom - doblez); lineTo(r.right - doblez, r.bottom); lineTo(r.left, r.bottom) }
                    else { lineTo(r.right, r.bottom); lineTo(r.left + doblez, r.bottom); lineTo(r.left, r.bottom - doblez) }
                    close() }
            }
            drawPath(forma, hoja)
            if (!elegido) drawPath(Path().apply { moveTo(ex, r.bottom - doblez); lineTo(ex + k * doblez, r.bottom); lineTo(ex + k * doblez, r.bottom - doblez); close() },
                mezcla(hoja, Color.Black, 0.18f))
            val tinta = tintaPrensa(oscuro, if (elegido) 0.85f else 0.35f)
            filetesRev(r.left + 5 * u, r.right - 5 * u, r.top + 4 * u, 1f, tinta)
            filetesRev(r.left + 5 * u, r.right - 5 * u, r.bottom - 4 * u, -1f, tinta)
            if (elegido) escarapela(Offset(if (ingreso) r.right - 6 * u else r.left + 6 * u, r.top + 6 * u), 7 * u, oscuro)
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            val propio = if (ingreso) e.colorIngreso else e.colorGasto
            val color = if (elegido) legibleMedievo(propio, hoja) else tenueLegibleMedievo(propio, hoja)
            textoEn(medidor, texto, Rect(r.left + 16 * u, r.top + 9 * u, r.right - 16 * u, r.bottom - 9 * u), LetraExtra.convencion, color, 17f,
                peso = if (elegido) FontWeight.Bold else FontWeight.Normal, espaciado = 0.4f, minimo = 10f)
        }
    }

    override val altoMes: Dp = 52.dp

    /**
     * El encabezado del mes: la cabecera de un periódico de la Revolución, con los filetes grueso y fino de imprenta arriba y
     * abajo y el mes al centro; en cada extremo, la manecilla de imprenta (la mano que señala) apuntando al mes de al lado.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val hoja = cartelPieza(oscuro)
        drawRect(hoja)
        val tinta = tintaPrensa(oscuro, 1f)
        filetesRev(4 * u, w - 4 * u, 4 * u, 1f, tinta)
        filetesRev(4 * u, w - 4 * u, h - 4 * u, -1f, tinta)
        listOf(48 * u, w - 48 * u).forEach { x -> drawLine(tinta.copy(alpha = 0.6f), Offset(x, 11 * u), Offset(x, h - 11 * u), 0.6f * u) }
        val tri = tricolor(oscuro)
        listOf(56 * u to tri[0], w - 56 * u to tri[2]).forEach { (x, c) -> drawCircle(c, 2.4f * u, Offset(x, h / 2)) }
        textoEn(medidor, e.titulo, Rect(62 * u, 8 * u, w - 62 * u, h - 8 * u), LetraExtra.convencion, legibleMedievo(tinta, hoja), 21f, peso = FontWeight.Bold, minimo = 12f)
        manecillaRev(Offset(24 * u, h / 2), 34 * u, true, tinta, hoja)
        manecillaRev(Offset(w - 24 * u, h / 2), 34 * u, false, tinta, hoja)
    }

    /**
     * El asiento: un renglón del libro de cuentas de la sección, con la raya de tinta al pie y el arranque de la columna del
     * importe a 112 dp del canto; al principio, el cuadradito rojo del gasto o el punto azul del ingreso.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 120 * u || h < 10 * u) return
        val y = h - 0.8f * u
        val raya = tintaPrensa(oscuro, if (oscuro) 0.2f else 0.22f)
        drawLine(raya, Offset(16 * u, y), Offset(w, y), 0.7f * u)
        drawLine(raya, Offset(w - 112 * u, y - 7 * u), Offset(w - 112 * u, y), 0.7f * u)
        val c = Offset(6 * u, y - 3.4f * u)
        if (e.ingreso) drawCircle(azulPieza(oscuro).copy(alpha = 0.9f), 3.2f * u, c)
        else drawRect(rojoPieza(oscuro).copy(alpha = 0.9f), Offset(c.x - 3 * u, c.y - 3 * u), Size(6 * u, 6 * u))
    }

    override val altoRotuloDia: Dp = 24.dp

    /** El rótulo del día: la escarapela, la fecha en negrita de cartel y los filetes grueso y fino hasta el canto. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fondos = if (oscuro) listOf(Color(0xFF131417), Color(0xFF1B1D22)) else listOf(Color(0xFFF2ECDD), Color(0xFFFAF6EC))
        escarapela(Offset(7 * u, h / 2), 6 * u, oscuro)
        val tinta = legibleMedievo(tintaPrensa(oscuro, 1f), fondos)
        val fin = textoIzqMedievo(medidor, texto, 18 * u, 0f, h, w - 40 * u, LetraExtra.convencion, tinta, 13f, FontWeight.Bold, 1.4f)
        if (fin > 18 * u) filetesRev(fin + 6 * u, w, h / 2 - 1.3f * u, 1f, tinta.copy(alpha = 0.7f))
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

/** Un trazo de separador tenue ([EstadoSeparador.tenue]): a la mitad de fuerte. */
private fun tenueMedievo(c: Color, e: EstadoSeparador): Color = if (e.tenue) c.copy(alpha = c.alpha * 0.5f) else c

/** El color de un botón de texto apagado (sin [EstadoBotonTexto.habilitado]): se acerca al gris y baja a media luz. */
private fun apagadoBotonMedievo(c: Color, e: EstadoBotonTexto, oscuro: Boolean): Color =
    if (e.habilitado) c else mezcla(c, if (oscuro) Color(0xFF4A4741) else Color(0xFFA9A49A), 0.6f).copy(alpha = c.alpha * 0.55f)

/**
 * El lavado tenue de un botón de texto mientras se presiona: un rectángulo redondeado de radio [radio] del tamaño del botón
 * visible (sin la franja de toque de arriba y abajo).
 */
private fun DrawScope.toqueMedievo(e: EstadoBotonTexto, color: Color, radio: Float) {
    if (!e.presionado || !e.habilitado) return
    val u = density
    drawRoundRect(color, Offset(2 * u, 4 * u), Size(size.width - 4 * u, size.height - 8 * u), CornerRadius(radio))
}

/** La altura del subrayado de un botón de texto: justo bajo el renglón, que va centrado en el lienzo. */
private fun DrawScope.renglonBotonMedievo(): Float = maxOf(size.height - 14 * density, size.height / 2 + 8 * density)

/** Un cabo fino y torcido de [xa] a [xb] a la altura [y]: la cuerda y sus vueltas, estas en una sola figura (barato en listas). */
private fun DrawScope.cuerdaMedievo(xa: Float, xb: Float, y: Float, grosor: Float, claro: Color, sombra: Color) {
    val u = density
    if (xb - xa < 1f) return
    drawLine(claro, Offset(xa, y), Offset(xb, y), grosor, StrokeCap.Round)
    val paso = maxOf(2.6f * u, 1f); val m = grosor * 0.5f
    val vueltas = Path(); var x = xa + paso * 0.5f; var k = 0
    while (x < xb - m && k < 200) {
        vueltas.moveTo(x - m * 0.7f, y + m); vueltas.lineTo(x + m * 0.7f, y - m)
        x += paso; k++
    }
    drawPath(vueltas, sombra, style = Stroke(0.6f * u))
}

/**
 * El sitio de la pieza de una tarjeta: deja 1,6 × 2 dp abajo a la derecha para su sombra y, al presionarla
 * ([EstadoTarjeta.presionado]), la pieza baja hacia la sombra.
 */
private fun DrawScope.sitioTarjetaMedievo(e: EstadoTarjeta): Rect {
    val u = density; val d = if (e.presionado) 1f else 0f
    return Rect(d * u, d * 1.2f * u, size.width - (1.6f - d) * u, size.height - (2f - 1.2f * d) * u)
}

/** La sombra de [forma] (la figura de la pieza, en su sitio): se acorta al presionarla, como si la pieza bajara. */
private fun DrawScope.sombraTarjetaMedievo(forma: Path, e: EstadoTarjeta, oscuro: Boolean) {
    val u = density; val d = if (e.presionado) 0.6f else 1.6f
    translate(d * u, d * 1.25f * u) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.42f else 0.18f)) }
}

/** Un rectángulo como figura (para su sombra y su contorno). */
private fun rectMedievo(r: Rect): Path = Path().apply { addRect(r) }

/** Las piezas de una cadena de [a] a [b]: eslabones de [largo], uno de frente y otro de canto. */
private fun DrawScope.cadenaMedievo(a: Offset, b: Offset, largo: Float, color: Color, grosor: Float) {
    val dx = b.x - a.x; val dy = b.y - a.y
    val total = kotlin.math.sqrt(dx * dx + dy * dy)
    if (total < 1f) return
    val n = (total / maxOf(largo * 0.8f, 1f)).toInt().coerceIn(1, 40)
    val ux = dx / total; val uy = dy / total
    for (k in 0 until n) {
        val c = Offset(a.x + dx * (k + 0.5f) / n, a.y + dy * (k + 0.5f) / n)
        val p0 = Offset(c.x - ux * largo / 2, c.y - uy * largo / 2); val p1 = Offset(c.x + ux * largo / 2, c.y + uy * largo / 2)
        if (k % 2 == 0) {
            val m = largo * 0.28f
            drawPath(Path().apply {
                moveTo(p0.x, p0.y); quadraticTo(c.x - uy * m * 2, c.y + ux * m * 2, p1.x, p1.y); quadraticTo(c.x + uy * m * 2, c.y - ux * m * 2, p0.x, p0.y); close()
            }, color, style = Stroke(grosor))
        } else drawLine(color, p0, p1, grosor * 1.2f, StrokeCap.Round)
    }
}

/** El contraste entre dos colores opacos, como lo mide la WCAG: de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrasteMedievo(a: Color, b: Color): Float {
    val x = a.luminance() + 0.05f; val y = b.luminance() + 0.05f
    return maxOf(x, y) / minOf(x, y)
}

/**
 * [c] llevado hacia el negro (o hacia el blanco, si los fondos son oscuros) lo justo para leerse a [minimo]:1 sobre todos
 * los [fondos] (opacos): conserva su tono, solo lo hunde o lo aclara. Si ya se lee, vuelve tal cual.
 */
private fun legibleMedievo(c: Color, fondos: List<Color>, minimo: Float = 4.8f): Color {
    val base = c.copy(alpha = 1f)
    if (fondos.isEmpty()) return base
    // Hacia el extremo que más se despega de los fondos: el negro sobre fondos claros, el blanco sobre oscuros.
    val meta = if (fondos.minOf { contrasteMedievo(Color.Black, it) } >= fondos.minOf { contrasteMedievo(Color.White, it) }) Color.Black else Color.White
    var r = base; var k = 0
    while (fondos.any { contrasteMedievo(r, it) < minimo } && k < 20) { k++; r = mezcla(base, meta, k * 0.05f) }
    return r
}

/** [c] llevado lo justo para leerse a [minimo]:1 sobre [fondo] (opaco). */
private fun legibleMedievo(c: Color, fondo: Color, minimo: Float = 4.8f): Color = legibleMedievo(c, listOf(fondo), minimo)

/**
 * [c] apagado hacia [fondo] todo lo que se pueda sin bajar de [minimo]:1 (hasta la mitad del camino): el texto de la mitad
 * no elegida de la balanza, tenue pero legible.
 */
private fun tenueLegibleMedievo(c: Color, fondo: Color, minimo: Float = 4.8f): Color {
    val base = legibleMedievo(c, fondo, minimo)
    var f = 0f; var k = 0
    while (k < 10 && contrasteMedievo(mezcla(base, fondo, f + 0.05f), fondo) >= minimo) { f += 0.05f; k++ }
    return mezcla(base, fondo, f)
}

/**
 * Un texto alineado a la izquierda desde [x], centrado en alto entre [arriba] y [abajo]: se mide con su letra y se le da a
 * [textoEn] un recuadro de ese ancho (sin pasar de [maximo]). Devuelve dónde acaba, para seguir el adorno desde ahí.
 */
private fun DrawScope.textoIzqMedievo(medidor: TextMeasurer, texto: String, x: Float, arriba: Float, abajo: Float, maximo: Float,
                                      letra: FontFamily, color: Color, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f): Float {
    if (texto.isBlank() || maximo <= 1f) return x
    val capa = medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, fontWeight = peso,
        letterSpacing = espaciado.sp), maxLines = 1, softWrap = false)
    val ancho = minOf(capa.size.width + 2f * density, maximo)
    textoEn(medidor, texto, Rect(x, arriba, x + ancho, abajo), letra, color, tam, peso = peso, espaciado = espaciado, minimo = 7f)
    return x + ancho
}

/** Las dos mitades de la balanza (izquierda: el gasto; derecha: el ingreso), con un hueco de [hueco] en medio. */
private fun DrawScope.mitadesMedievo(margen: Float, hueco: Float): Pair<Rect, Rect> {
    val w = size.width; val h = size.height
    return Rect(margen, margen, w / 2 - hueco / 2, h - margen) to Rect(w / 2 + hueco / 2, margen, w - margen, h - margen)
}
