package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Futuro
// =====================================================================

/**
 * Distopía: el estante del registro, de ángulo ranurado de acero contra un muro de bloques húmedos. Arriba, una viga de
 * hierro con sus remaches y su cableado, el altavoz en su poste, la lámpara enjaulada que cuelga con su cono de luz y la
 * placa de la sección atornillada. Las tablas son de chapa doblada y cada mes va en un portaetiquetas con su tarjeta
 * sellada. Cada entrada es un archivador de palanca de cartón (gris, oliva, manila, burdeos) con su ventanilla de
 * plástico, la tarjeta con el número del día, el agujero con su ojal de metal y la cinta de clasificación; algunos llevan la
 * franja roja de «clasificado». De noche la lámpara se apaga y entra el haz de un reflector. Distinto del Ideario, que es el
 * archivero de gavetas con el ojo.
 */
object EstanteDistopia : DisenoBiblioteca {
    private val ACERO = Color(0xFF6F7479)
    private val ACERO_OSC = Color(0xFF43474B)
    private val ACERO_LUZ = Color(0xFF9BA1A6)
    private val MURO = Color(0xFF5A5E57)
    private val MURO_JUNTA = Color(0xFF41453F)
    private val ROJO = Color(0xFFA8201C)
    private val NEGRO = Color(0xFF141516)
    private val PAPEL = Color(0xFFE9E4D6)
    private val BOMBILLA = Color(0xFFF0DC8C)
    private val SUELO = Color(0xFF34372F)
    private val CARTON = Color(0xFF9C8256)
    /** El cartón de los archivadores: gris, oliva, manila, burdeos y pizarra. */
    private val CARTONES = listOf(Color(0xFF7C7F82), Color(0xFF5D6443), Color(0xFFB59D63), Color(0xFF6A2A2A), Color(0xFF4A5560))
    private val NOCHE = Color(0xFF0E1420)
    private val ESTARCIDO: FontFamily = LetraExtra.oswald

    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0B0C0D), 0.4f)
        if (noche) r = mezcla(r, NOCHE, 0.55f)
        return r
    }

    override val corona = 0.28f
    override val base = 0.09f
    override val lado = 0.05f
    override val altoRepisa: Dp = 118.dp
    override val tabla: Dp = 17.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.barlow

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int): Float = 0.84f + 0.1f * azar(semilla, 2)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de bloques, con sus juntas y la humedad que baja.
        val muro = Rect(0f, d.corona.top + d.corona.height * 0.18f, w, d.base.top + d.base.height * 0.4f)
        drawRect(c(MURO), muro.topLeft, muro.size)
        val fila = 18 * u; val bloque = 38 * u
        var y = muro.top; var k = 0
        while (y < muro.bottom) {
            drawLine(c(MURO_JUNTA), Offset(0f, y), Offset(w, y), 1f * u)
            var x = if (k % 2 == 0) 0f else bloque / 2
            while (x < w) { drawLine(c(MURO_JUNTA), Offset(x, y), Offset(x, minOf(y + fila, muro.bottom)), 1f * u); x += bloque }
            y += fila; k++
        }
        listOf(0.22f to 0.35f, 0.74f to 0.6f).forEach { (fx, fy) ->
            val o = Offset(w * fx, muro.top + muro.height * fy)
            drawOval(Color.Black.copy(alpha = 0.12f), Offset(o.x - w * 0.08f, o.y - muro.height * 0.04f), Size(w * 0.16f, muro.height * 0.22f))
            drawLine(Color.Black.copy(alpha = 0.1f), o, Offset(o.x + 2 * u, muro.bottom), 3 * u)
        }
        // Cada repisa: la sombra de la chapa de arriba y unas riostras en cruz por detrás, junto a los postes.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.28f), r.topLeft, Size(r.width, 9 * u))
            listOf(r.left + 1 * u to r.left + r.width * 0.12f, r.right - r.width * 0.12f to r.right - 1 * u).forEach { (a, b) ->
                drawLine(c(ACERO_OSC), Offset(a, r.top), Offset(b, r.bottom), 1.6f * u)
                drawLine(c(ACERO_OSC), Offset(b, r.top), Offset(a, r.bottom), 1.6f * u)
            }
        }
        // Los postes de ángulo ranurado.
        listOf(d.izquierda, d.derecha).forEach { r ->
            val p = Rect(r.left + r.width * 0.12f, d.corona.bottom - 6 * u, r.right - r.width * 0.12f, d.base.top + d.base.height * 0.55f)
            drawRect(c(ACERO), p.topLeft, p.size)
            drawRect(c(ACERO_LUZ), p.topLeft, Size(p.width * 0.18f, p.height))
            drawRect(c(ACERO_OSC), Offset(p.right - p.width * 0.22f, p.top), Size(p.width * 0.22f, p.height))
            var yy = p.top + 5 * u; var j = 0
            while (yy < p.bottom - 8 * u) {
                val largo = if (j % 2 == 0) 7 * u else 3.5f * u
                drawRoundRect(c(NEGRO).copy(alpha = 0.8f), Offset(p.center.x - 1.6f * u, yy), Size(3.2f * u, largo), CornerRadius(1.6f * u))
                yy += largo + 4 * u; j++
            }
        }
        // Las tablas: chapa doblada con su labio y dos tornillos en cada punta.
        d.tablas.forEach { r ->
            drawRect(c(ACERO), r.topLeft, r.size)
            drawRect(c(ACERO_LUZ), r.topLeft, Size(r.width, 2 * u))
            drawRect(c(ACERO_OSC), Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
            listOf(r.left - 4 * u, r.right + 4 * u).forEach { x -> drawCircle(c(ACERO_LUZ), 2.2f * u, Offset(x, r.center.y)); drawLine(c(NEGRO), Offset(x - 1.4f * u, r.center.y), Offset(x + 1.4f * u, r.center.y), 0.7f * u) }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom), Size(r.width, 4 * u))
        }
        // El portaetiquetas de cada mes, con su tarjeta mecanografiada y un sello.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(c(ACERO_LUZ), r.inflate(1.6f * u).topLeft, r.inflate(1.6f * u).size)
            drawRect(c(PAPEL), r.topLeft, r.size)
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(r.left + 3 * u, r.top + r.height * 0.24f), Offset(r.right - 3 * u, r.top + r.height * 0.24f), 0.6f * u)
            drawCircle(c(ROJO).copy(alpha = 0.55f), r.height * 0.32f, Offset(r.right - r.height * 0.5f, r.center.y), style = Stroke(1f * u))
            textoEn(medidor, p.texto.uppercase(), Rect(r.left + 4 * u, r.top + r.height * 0.24f, r.right - r.height, r.bottom), ESTARCIDO, c(NEGRO), 10f, espaciado = 0.8f)
        }
        corona(d.corona, oscuro, noche)
        // El suelo de linóleo en baldosas y los pies de los postes, atornillados.
        val piso = d.base.top + d.base.height * 0.4f
        drawRect(c(SUELO), Offset(0f, piso), Size(w, h - piso))
        val baldosa = 22 * u
        var bx = 0f
        while (bx < w) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(bx, piso), Offset(bx - 10 * u, h), 0.8f * u); bx += baldosa }
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, piso + (h - piso) * 0.5f), Offset(w, piso + (h - piso) * 0.5f), 0.8f * u)
        listOf(d.izquierda, d.derecha).forEach { r ->
            val pie = Rect(r.left - 1 * u, d.base.top + d.base.height * 0.48f, r.right + 1 * u, d.base.top + d.base.height * 0.62f)
            drawRect(c(ACERO_OSC), pie.topLeft, pie.size)
            drawCircle(c(ACERO_LUZ), 1.6f * u, Offset(pie.left + pie.width * 0.25f, pie.center.y)); drawCircle(c(ACERO_LUZ), 1.6f * u, Offset(pie.right - pie.width * 0.25f, pie.center.y))
        }
        // Una caja de cartón con cinta, olvidada bajo el estante.
        val caja = Rect(w * 0.12f, d.base.top - d.base.height * 0.1f, w * 0.34f, d.base.top + d.base.height * 0.56f)
        drawRect(c(CARTON), caja.topLeft, caja.size)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
        drawRect(c(PAPEL).copy(alpha = 0.55f), Offset(caja.center.x - caja.width * 0.06f, caja.top), Size(caja.width * 0.12f, caja.height))
        drawRect(c(PAPEL), Offset(caja.left + caja.width * 0.12f, caja.top + caja.height * 0.3f), Size(caja.width * 0.26f, caja.height * 0.28f))
        // De noche, el haz del reflector cruza el muro por la ventana alta.
        if (noche) {
            val haz = Path().apply { moveTo(w * 0.55f, 0f); lineTo(w * 0.8f, 0f); lineTo(w * 0.42f, h); lineTo(w * 0.1f, h); close() }
            drawPath(haz, Color(0xFFDCE3F0).copy(alpha = 0.08f))
        }
    }

    /** La viga de arriba, el altavoz, la lámpara enjaulada y la placa de la sección. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // La viga de hierro con sus remaches y el cable que corre debajo.
        val viga = Rect(r.left, r.top + r.height * 0.08f, r.right, r.top + r.height * 0.26f)
        drawRect(c(ACERO_OSC), viga.topLeft, viga.size)
        drawRect(c(ACERO), Offset(viga.left, viga.top), Size(viga.width, viga.height * 0.22f))
        drawRect(c(ACERO), Offset(viga.left, viga.bottom - viga.height * 0.22f), Size(viga.width, viga.height * 0.22f))
        var x = viga.left + 8 * u
        while (x < viga.right) { drawCircle(c(ACERO_LUZ), 1.5f * u, Offset(x, viga.top + viga.height * 0.11f)); drawCircle(c(ACERO_LUZ), 1.5f * u, Offset(x, viga.bottom - viga.height * 0.11f)); x += 14 * u }
        val cable = Path().apply {
            moveTo(r.left, viga.bottom + 3 * u)
            var xx = r.left
            while (xx < r.right) { quadraticTo(xx + 20 * u, viga.bottom + 9 * u, xx + 40 * u, viga.bottom + 3 * u); xx += 40 * u }
        }
        drawPath(cable, c(NEGRO), style = Stroke(1.4f * u))
        // El altavoz de bocina en su poste, a la izquierda.
        val poste = Offset(r.left + r.width * 0.13f, viga.bottom)
        drawLine(c(ACERO_OSC), poste, Offset(poste.x, viga.bottom + r.height * 0.22f), 3 * u)
        val boca = Path().apply {
            moveTo(poste.x, viga.bottom + r.height * 0.2f)
            lineTo(poste.x + r.width * 0.1f, viga.bottom + r.height * 0.1f)
            lineTo(poste.x + r.width * 0.12f, viga.bottom + r.height * 0.46f)
            close()
        }
        drawPath(boca, c(ACERO))
        drawPath(boca, c(NEGRO), style = Stroke(1.2f * u))
        drawOval(c(NEGRO), Offset(poste.x + r.width * 0.09f, viga.bottom + r.height * 0.1f), Size(r.width * 0.04f, r.height * 0.36f))
        // La lámpara enjaulada que cuelga en el centro, con su cono de luz (de noche, apagada).
        val cx = r.center.x
        val bajo = viga.bottom + r.height * 0.36f
        drawLine(c(NEGRO), Offset(cx, viga.bottom), Offset(cx, bajo - r.height * 0.1f), 1.2f * u)
        val pantalla = Path().apply {
            moveTo(cx - r.width * 0.025f, bajo - r.height * 0.1f); lineTo(cx + r.width * 0.025f, bajo - r.height * 0.1f)
            lineTo(cx + r.width * 0.07f, bajo); lineTo(cx - r.width * 0.07f, bajo); close()
        }
        if (!noche) {
            val cono = Path().apply { moveTo(cx - r.width * 0.07f, bajo); lineTo(cx + r.width * 0.07f, bajo); lineTo(cx + r.width * 0.32f, r.bottom + r.height * 0.6f); lineTo(cx - r.width * 0.32f, r.bottom + r.height * 0.6f); close() }
            drawPath(cono, Brush.verticalGradient(listOf(BOMBILLA.copy(alpha = if (oscuro) 0.16f else 0.22f), Color.Transparent), bajo, r.bottom + r.height * 0.6f))
            mancha(Offset(cx, bajo + r.height * 0.02f), r.height * 0.16f, BOMBILLA.copy(alpha = 0.6f))
        }
        drawCircle(if (noche) c(ACERO_OSC) else BOMBILLA, r.height * 0.05f, Offset(cx, bajo + r.height * 0.02f))
        drawPath(pantalla, c(ACERO))
        drawPath(pantalla, c(NEGRO), style = Stroke(1f * u))
        // La jaula de alambre.
        for (j in -2..2) drawLine(c(NEGRO), Offset(cx + j * r.width * 0.015f, bajo), Offset(cx + j * r.width * 0.01f, bajo + r.height * 0.09f), 0.8f * u)
        drawLine(c(NEGRO), Offset(cx - r.width * 0.035f, bajo + r.height * 0.06f), Offset(cx + r.width * 0.035f, bajo + r.height * 0.06f), 0.8f * u)
        // La placa de la sección atornillada a la derecha, con su número estarcido y una franja roja.
        val placa = Rect(r.right - r.width * 0.3f, viga.bottom + r.height * 0.14f, r.right - r.width * 0.07f, viga.bottom + r.height * 0.5f)
        drawRect(c(ACERO_LUZ), placa.topLeft, placa.size)
        drawRect(c(ROJO), Offset(placa.left, placa.top), Size(placa.width, placa.height * 0.22f))
        listOf(placa.topLeft, Offset(placa.right, placa.top), Offset(placa.left, placa.bottom), placa.bottomRight).forEach { o ->
            drawCircle(c(ACERO_OSC), 1.6f * u, Offset(o.x + (if (o.x < placa.center.x) 4 * u else -4 * u), o.y + (if (o.y < placa.center.y) 4 * u else -4 * u)))
        }
        // Las barras del número de sección, en estarcido (formas, no letras).
        val bx = placa.left + placa.width * 0.18f; val by = placa.top + placa.height * 0.4f
        for (j in 0 until 4) {
            val alto = placa.height * (if (j == 1) 0.42f else 0.3f)
            drawRect(c(NEGRO), Offset(bx + j * placa.width * 0.17f, by + (placa.height * 0.42f - alto)), Size(placa.width * 0.1f, alto))
        }
        // La ventana alta con barrotes entre la lámpara y la placa.
        val ventana = Rect(r.left + r.width * 0.32f, viga.bottom + r.height * 0.1f, r.left + r.width * 0.42f, viga.bottom + r.height * 0.42f)
        drawRect(if (noche) Color(0xFF283550) else c(Color(0xFFB9C2C6)), ventana.topLeft, ventana.size)
        clipRect(ventana.left, ventana.top, ventana.right, ventana.bottom) {
            for (j in 1..3) drawLine(c(NEGRO), Offset(ventana.left + ventana.width * j / 4f, ventana.top), Offset(ventana.left + ventana.width * j / 4f, ventana.bottom), 1.6f * u)
        }
        drawRect(c(ACERO_OSC), ventana.topLeft, ventana.size, style = Stroke(2 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = CARTONES[(v.semilla and 0x7fffffff) % CARTONES.size]
        val carton = c(if (v.tono != null) mezcla(base, v.tono, 0.2f) else base)
        // El lomo de cartón, con el pliegue a la izquierda y el canto en luz a la derecha.
        drawRoundRect(carton, Offset.Zero, size, CornerRadius(1.5f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset.Zero, Size(w * 0.1f, h))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.88f, 0f), Size(w * 0.12f, h))
        // Las hojas que asoman por arriba: más cuanto más escribiste.
        val hojas = 1 + (v.lleno * 4).toInt()
        for (j in 0 until hojas) drawLine(c(PAPEL).copy(alpha = 0.8f), Offset(w * 0.12f, 1.2f * u + j * 1.4f * u), Offset(w * 0.86f, 1.2f * u + j * 1.4f * u), 0.7f * u)
        // La ventanilla de plástico con la tarjeta: el día y el mes.
        val tarjeta = Rect(w * 0.14f, h * 0.08f, w * 0.86f, h * 0.42f)
        drawRect(c(PAPEL), tarjeta.topLeft, tarjeta.size)
        drawRect(Color.White.copy(alpha = 0.25f), tarjeta.topLeft, Size(tarjeta.width, tarjeta.height * 0.18f))
        drawRect(c(Color(0xFF2B2D2E)), tarjeta.topLeft, tarjeta.size, style = Stroke(1f * u))
        drawLine(c(ROJO).copy(alpha = 0.6f), Offset(tarjeta.left + 2 * u, tarjeta.top + tarjeta.height * 0.2f), Offset(tarjeta.right - 2 * u, tarjeta.top + tarjeta.height * 0.2f), 0.6f * u)
        textoEn(medidor, if (v.noche) "☾" else "Nº", Rect(tarjeta.left, tarjeta.top, tarjeta.right, tarjeta.top + tarjeta.height * 0.2f), letra, c(NEGRO).copy(alpha = 0.7f), 6.5f)
        textoEn(medidor, v.dia, Rect(tarjeta.left, tarjeta.top + tarjeta.height * 0.22f, tarjeta.right, tarjeta.top + tarjeta.height * 0.66f), ESTARCIDO, c(NEGRO), 15f, peso = FontWeight.Bold)
        textoEn(medidor, v.mes.uppercase(), Rect(tarjeta.left + 1 * u, tarjeta.top + tarjeta.height * 0.66f, tarjeta.right - 1 * u, tarjeta.bottom - 1 * u), letra, c(NEGRO).copy(alpha = 0.8f), 7f)
        // El agujero para el dedo, con su ojal de metal.
        val o = Offset(w * 0.5f, h * 0.58f)
        val ra = minOf(w * 0.2f, 6.5f * u)
        drawCircle(Color.Black.copy(alpha = 0.7f), ra, o)
        drawCircle(c(ACERO_LUZ), ra, o, style = Stroke(1.8f * u))
        // La franja roja de «clasificado», en diagonal, en algunos.
        if (azar(v.semilla, 7) > 0.62f) {
            val franja = Path().apply { moveTo(0f, h * 0.7f); lineTo(w, h * 0.64f); lineTo(w, h * 0.72f); lineTo(0f, h * 0.78f); close() }
            drawPath(franja, c(ROJO).copy(alpha = 0.9f))
        }
        // La cinta de clasificación abajo y el número de inventario en rayas.
        drawRect(c(if (v.lleno > 0.6f) ROJO else ACERO_LUZ), Offset(0f, h * 0.84f), Size(w, h * 0.05f))
        for (j in 0 until 5) drawLine(c(NEGRO).copy(alpha = 0.55f), Offset(w * (0.22f + j * 0.13f), h * 0.91f), Offset(w * (0.22f + j * 0.13f), h * 0.96f), (if (j % 2 == 0) 1.2f else 0.6f) * u)
    }
}
