package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
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
 * La tapa del diario es ese archivador de frente, con el formulario del expediente tras la ventanilla; al abrirse, gira
 * sobre el lomo.
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
    override fun alto(semilla: Int, palabras: Int): Float = 0.84f + 0.1f * azar(semilla, 2)

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
        // La ventanilla de plástico con la tarjeta: el día y el mes (abreviado, «M-10», para que quepa en los más delgados).
        val tarjeta = Rect(w * 0.14f, h * 0.08f, w * 0.86f, h * 0.42f)
        drawRect(c(PAPEL), tarjeta.topLeft, tarjeta.size)
        drawRect(Color.White.copy(alpha = 0.25f), tarjeta.topLeft, Size(tarjeta.width, tarjeta.height * 0.18f))
        drawRect(c(Color(0xFF2B2D2E)), tarjeta.topLeft, tarjeta.size, style = Stroke(1f * u))
        drawLine(c(ROJO).copy(alpha = 0.6f), Offset(tarjeta.left + 2 * u, tarjeta.top + tarjeta.height * 0.2f), Offset(tarjeta.right - 2 * u, tarjeta.top + tarjeta.height * 0.2f), 0.6f * u)
        textoEn(medidor, if (v.noche) "☾" else "Nº", Rect(tarjeta.left, tarjeta.top, tarjeta.right, tarjeta.top + tarjeta.height * 0.2f), letra, c(NEGRO).copy(alpha = 0.7f), 6.5f)
        textoEn(medidor, v.dia, Rect(tarjeta.left, tarjeta.top + tarjeta.height * 0.22f, tarjeta.right, tarjeta.top + tarjeta.height * 0.66f), ESTARCIDO, c(NEGRO), 15f, peso = FontWeight.Bold)
        textoEn(medidor, v.mes.uppercase().let { m -> if (m.startsWith("MES ")) "M-" + m.removePrefix("MES ") else m }, Rect(tarjeta.left + 0.5f * u, tarjeta.top + tarjeta.height * 0.66f, tarjeta.right - 0.5f * u, tarjeta.bottom - 1 * u), ESTARCIDO, c(NEGRO).copy(alpha = 0.8f), 7f, minimo = 4.5f)
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

    // ---------------------------------------------------------------- la tapa

    /** El papel del formulario de la tapa: se hunde menos que el cartón, para que el extracto se lea en oscuro y de noche. */
    private fun papelTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL
        if (oscuro) r = mezcla(r, Color(0xFF0B0C0D), 0.2f)
        if (noche) r = mezcla(r, NOCHE, 0.22f)
        return r
    }

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.2f, 0.4f, 0.84f, 0.75f)
    override val letraTapa: FontFamily = Letra.terminal
    override val tamTapa = 13f
    override val lineasTapa = 3
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = Color(0xFF17181A)
    override fun palabrasTapa(n: Int): String = "$n PALABRAS"

    /**
     * El archivador del día, de frente: el lomo con su tarjeta, el agujero con su ojal y la cinta de clasificación; la tapa
     * de cartón con el canto de metal, el ojal con su cordel y, tras la ventanilla de plástico, el formulario del expediente
     * con la fecha, el sello de «ARCHIVO» y las palabras al pie. Al abrirse, la tapa gira sobre el lomo y deja ver el
     * mecanismo de palanca y el legajo mecanografiado. De noche, el haz del reflector lo cruza.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val hoja = papelTapa(oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val carton = c(CARTONES[(e.semilla and 0x7fffffff) % CARTONES.size])
        val cuerpo = Rect(w * 0.015f, h * 0.035f, w * 0.975f, h * 0.955f)
        val lomo = Rect(cuerpo.left, cuerpo.top, cuerpo.left + w * 0.11f, cuerpo.bottom)
        val cara = Rect(lomo.right, cuerpo.top, cuerpo.right, cuerpo.bottom)
        // La sombra sobre la mesa.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Offset(cuerpo.left + 3 * u, cuerpo.top + 4 * u), cuerpo.size, CornerRadius(3 * u))
        // Lo de dentro, que se ve al abrir: la contratapa, el legajo mecanografiado y el mecanismo de palanca.
        if (a > 0.01f) {
            drawRect(mezcla(carton, Color.Black, 0.35f), cara.topLeft, cara.size)
            val legajo = Rect(cara.left + w * 0.06f, cara.top + h * 0.05f, cara.right - w * 0.025f, cara.bottom - h * 0.05f)
            for (j in 3 downTo 1) drawRect(mezcla(hoja, Color.Black, 0.12f * j), Offset(legajo.left + j * 1.2f * u, legajo.top + j * 1.2f * u), legajo.size)
            drawRect(hoja, legajo.topLeft, legajo.size)
            drawRect(c(ROJO).copy(alpha = 0.6f), Offset(legajo.left + w * 0.05f, legajo.top + h * 0.08f), Size(legajo.width * 0.3f, 1.2f * u))
            var ry = legajo.top + h * 0.16f; var k = 0
            while (ry < legajo.bottom - h * 0.06f) {
                val largo = (legajo.width - w * 0.1f) * (0.45f + 0.55f * azar(e.semilla, 90 + k))
                drawLine(tinta.copy(alpha = 0.4f), Offset(legajo.left + w * 0.05f, ry), Offset(legajo.left + w * 0.05f + largo, ry), 1.2f * u)
                ry += 9 * u; k++
            }
            // La placa del mecanismo, los dos arcos que pasan por las perforaciones y la palanca.
            val placa = Rect(cara.left + w * 0.012f, cara.top + h * 0.12f, cara.left + w * 0.04f, cara.bottom - h * 0.12f)
            drawRect(c(ACERO), placa.topLeft, placa.size)
            drawRect(c(ACERO_LUZ), placa.topLeft, Size(placa.width * 0.3f, placa.height))
            listOf(0.3f, 0.7f).forEach { f ->
                val y = cara.top + cara.height * f
                val agujero = Offset(legajo.left + w * 0.022f, y)
                drawCircle(Color.Black.copy(alpha = 0.55f), 2.6f * u, agujero)
                drawOval(c(ACERO_LUZ), Offset(placa.center.x, y - 5 * u), Size(agujero.x - placa.center.x, 10 * u), style = Stroke(2.4f * u))
            }
            val eje = Offset(placa.center.x, cara.center.y)
            drawLine(c(ACERO_OSC), eje, Offset(eje.x + w * 0.05f, eje.y - h * 0.14f), 4 * u, cap = StrokeCap.Round)
            drawCircle(c(ACERO_LUZ), 3.4f * u, Offset(eje.x + w * 0.05f, eje.y - h * 0.14f))
        }
        // La tapa de cartón, que gira sobre el lomo al abrirse.
        val giro = cos(a * 1.6f).coerceAtLeast(0.02f)
        scale(giro, 1f, Offset(cara.left, cara.center.y)) {
            drawRect(Brush.verticalGradient(listOf(mezcla(carton, Color.White, 0.08f), carton, mezcla(carton, Color.Black, 0.18f)), cara.top, cara.bottom), cara.topLeft, cara.size)
            // Las fibras del cartón prensado.
            for (k in 0 until 70) {
                val o = Offset(cara.left + cara.width * azar(e.semilla, 200 + k), cara.top + cara.height * azar(e.semilla, 300 + k))
                drawLine((if (k % 2 == 0) Color.Black else Color.White).copy(alpha = 0.07f), o, Offset(o.x + (3 + 5 * azar(e.semilla, 400 + k)) * u, o.y + 0.6f * u), 0.7f * u)
            }
            drawRect(Color.White.copy(alpha = 0.12f), cara.topLeft, Size(cara.width, 1.4f * u))
            // La franja roja de «clasificado» en la esquina, en algunos (queda bajo la ventanilla).
            if (azar(e.semilla, 7) > 0.62f) clipRect(cara.left, cara.top, cara.right, cara.bottom) {
                drawPath(Path().apply {
                    moveTo(cara.right - w * 0.2f, cara.bottom); lineTo(cara.right, cara.bottom - h * 0.36f)
                    lineTo(cara.right, cara.bottom - h * 0.22f); lineTo(cara.right - w * 0.12f, cara.bottom); close()
                }, c(ROJO).copy(alpha = 0.9f))
            }
            // El canto de metal de abajo, remachado.
            drawRect(c(ACERO_LUZ), Offset(cara.left, cara.bottom - 4.5f * u), Size(cara.width, 4.5f * u))
            drawRect(c(ACERO_OSC), Offset(cara.left, cara.bottom - 1.2f * u), Size(cara.width, 1.2f * u))
            listOf(0.08f, 0.5f, 0.92f).forEach { f -> drawCircle(c(ACERO_OSC), 1.2f * u, Offset(cara.left + cara.width * f, cara.bottom - 2.6f * u)) }
            // El ojal del canto con su cordel, que cuelga.
            val ojal = Offset(cara.right - w * 0.04f, cara.center.y)
            drawCircle(Color.Black.copy(alpha = 0.75f), 3.6f * u, ojal)
            drawCircle(c(ACERO_LUZ), 3.6f * u, ojal, style = Stroke(1.6f * u))
            drawPath(Path().apply {
                moveTo(ojal.x, ojal.y); quadraticTo(ojal.x + w * 0.03f, ojal.y + h * 0.18f, ojal.x - w * 0.005f, cara.bottom - h * 0.06f)
            }, c(Color(0xFFCDBB8E)), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            // La ventanilla de plástico con el formulario del expediente.
            val ventana = Rect(w * 0.165f, h * 0.1f, w * 0.88f, h * 0.865f)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.18f), ventana.inflate(2.5f * u).topLeft, ventana.inflate(2.5f * u).size)
            drawRect(hoja, ventana.topLeft, ventana.size)
            // La cabecera: «EXPEDIENTE» y la fecha, en dos casillas, con el filete rojo debajo.
            val cab = Rect(ventana.left + 5 * u, ventana.top + 3 * u, ventana.right - 5 * u, h * 0.245f)
            val corte = cab.left + cab.width * 0.4f
            textoEn(medidor, "EXPEDIENTE", Rect(cab.left, cab.top, corte - 3 * u, cab.bottom), ESTARCIDO, tinta, 13f, peso = FontWeight.Bold, espaciado = 0.4f)
            textoEn(medidor, e.fecha, Rect(corte + 3 * u, cab.top, cab.right, cab.bottom), Letra.codigo, tinta, 12f, minimo = 7f)
            drawLine(tinta.copy(alpha = 0.6f), Offset(corte, cab.top + 2 * u), Offset(corte, cab.bottom), 0.8f * u)
            drawLine(c(ROJO).copy(alpha = 0.75f), Offset(ventana.left, h * 0.255f), Offset(ventana.right, h * 0.255f), 1.2f * u)
            drawLine(tinta.copy(alpha = 0.5f), Offset(ventana.left, h * 0.27f), Offset(ventana.right, h * 0.27f), 0.6f * u)
            // El nombre del campo y el sello de «ARCHIVO» (o «SUEÑO»), torcido.
            textoEn(medidor, if (e.noche) "INFORME DE ACTIVIDAD ONÍRICA:" else "DECLARACIÓN DE LA JORNADA:",
                Rect(ventana.left + 5 * u, h * 0.28f, ventana.left + ventana.width * 0.6f, h * 0.37f), ESTARCIDO, tinta.copy(alpha = 0.7f), 9f, minimo = 6f)
            val sello = Offset(ventana.left + ventana.width * 0.8f, h * 0.315f)
            rotate(-10f, sello) {
                val r = Rect(sello.x - w * 0.085f, sello.y - h * 0.038f, sello.x + w * 0.085f, sello.y + h * 0.038f)
                drawRect(c(ROJO).copy(alpha = 0.75f), r.topLeft, r.size, style = Stroke(1.5f * u))
                drawRect(c(ROJO).copy(alpha = 0.5f), r.deflate(2 * u).topLeft, r.deflate(2 * u).size, style = Stroke(0.6f * u))
                textoEn(medidor, if (e.noche) "SUEÑO" else "ARCHIVO", r.deflate(2.5f * u), ESTARCIDO, c(ROJO).copy(alpha = 0.8f), 12f, peso = FontWeight.Bold, espaciado = 0.6f)
            }
            // El pie: el folio en rayas y la cuenta de palabras.
            drawLine(tinta.copy(alpha = 0.45f), Offset(ventana.left + 5 * u, h * 0.775f), Offset(ventana.right - 5 * u, h * 0.775f), 0.6f * u)
            var bx = ventana.left + 6 * u; var k = 0
            while (bx < ventana.left + ventana.width * 0.3f) {
                val g = (if (azar(e.semilla, 500 + k) > 0.5f) 1.4f else 0.7f) * u
                drawRect(tinta.copy(alpha = 0.75f), Offset(bx, h * 0.795f), Size(g, h * 0.045f))
                bx += g + (0.8f + azar(e.semilla, 600 + k)) * u; k++
            }
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(ventana.left + ventana.width * 0.45f, h * 0.785f, ventana.right - 5 * u, h * 0.85f), Letra.codigo, tinta.copy(alpha = 0.85f), 10.5f, minimo = 6f)
            // El plástico: el marco, su brillo en la esquina de arriba y el filo de luz de abajo.
            drawRect(c(Color(0xFF2B2D2E)), ventana.topLeft, ventana.size, style = Stroke(1.2f * u))
            clipRect(ventana.left, ventana.top, ventana.right, h * 0.27f) {
                drawPath(Path().apply {
                    moveTo(ventana.left, ventana.top + h * 0.12f); lineTo(ventana.left + w * 0.1f, ventana.top)
                    lineTo(ventana.left + w * 0.16f, ventana.top); lineTo(ventana.left, ventana.top + h * 0.22f); close()
                }, Color.White.copy(alpha = 0.22f))
            }
            drawLine(Color.White.copy(alpha = 0.3f), Offset(ventana.left + 3 * u, ventana.bottom - 1.5f * u), Offset(ventana.right - 3 * u, ventana.bottom - 1.5f * u), 0.8f * u)
            // Al girar, la tapa se ve más de canto y se oscurece.
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.4f * a), cara.topLeft, cara.size)
        }
        // La sombra que la tapa echa sobre el legajo mientras gira.
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * sin(a * PI.toFloat())), Offset(cara.left + cara.width * giro, cara.top), Size(10 * u, cara.height))
        // El lomo, que no se mueve: el pliegue, la tarjeta, el agujero con su ojal, la cinta y el inventario.
        drawRect(mezcla(carton, Color.Black, 0.15f), lomo.topLeft, lomo.size)
        drawRect(Color.Black.copy(alpha = 0.25f), lomo.topLeft, Size(lomo.width * 0.12f, lomo.height))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(lomo.right - lomo.width * 0.14f, lomo.top), Size(lomo.width * 0.14f, lomo.height))
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(lomo.right, lomo.top), Offset(lomo.right, lomo.bottom), 1.4f * u)
        val tarjeta = Rect(lomo.left + lomo.width * 0.18f, h * 0.09f, lomo.right - lomo.width * 0.2f, h * 0.33f)
        drawRect(hoja, tarjeta.topLeft, tarjeta.size)
        drawRect(c(Color(0xFF2B2D2E)), tarjeta.topLeft, tarjeta.size, style = Stroke(1f * u))
        drawLine(c(ROJO).copy(alpha = 0.6f), Offset(tarjeta.left + 2 * u, tarjeta.top + tarjeta.height * 0.3f), Offset(tarjeta.right - 2 * u, tarjeta.top + tarjeta.height * 0.3f), 0.7f * u)
        textoEn(medidor, if (e.noche) "☾" else "Nº", Rect(tarjeta.left, tarjeta.top, tarjeta.right, tarjeta.top + tarjeta.height * 0.3f), letra, tinta.copy(alpha = 0.7f), 8f)
        for (j in 0 until 3) drawLine(tinta.copy(alpha = 0.35f), Offset(tarjeta.left + 3 * u, tarjeta.top + tarjeta.height * (0.48f + 0.16f * j)), Offset(tarjeta.right - 3 * u - j * 2 * u, tarjeta.top + tarjeta.height * (0.48f + 0.16f * j)), 0.8f * u)
        val dedo = Offset(lomo.center.x, h * 0.58f)
        val rd = minOf(lomo.width * 0.27f, 9 * u)
        drawCircle(Color.Black.copy(alpha = 0.75f), rd, dedo)
        drawCircle(c(ACERO_LUZ), rd, dedo, style = Stroke(2 * u))
        drawArc(Color.White.copy(alpha = 0.35f), 200f, 70f, false, Offset(dedo.x - rd, dedo.y - rd), Size(rd * 2, rd * 2), style = Stroke(0.8f * u))
        drawRect(c(if (e.palabras > 480) ROJO else ACERO_LUZ), Offset(lomo.left, h * 0.79f), Size(lomo.width, h * 0.045f))
        for (j in 0 until 5) drawLine(tinta.copy(alpha = 0.6f), Offset(lomo.left + lomo.width * (0.22f + j * 0.13f), h * 0.86f), Offset(lomo.left + lomo.width * (0.22f + j * 0.13f), h * 0.92f), (if (j % 2 == 0) 1.4f else 0.7f) * u)
        // De noche, el haz del reflector entra en diagonal por la ventana alta.
        if (e.noche) drawPath(Path().apply { moveTo(w * 0.5f, 0f); lineTo(w * 0.78f, 0f); lineTo(w * 0.42f, h); lineTo(w * 0.1f, h); close() }, Color(0xFFDCE3F0).copy(alpha = 0.08f))
    }
}

/**
 * Ciudad neón: la máquina expendedora de un callejón de los niveles bajos, pensada para la noche cerrada (el tema solo existe
 * en oscuro). La carcasa es de chapa violeta casi negra, con un tubo de neón magenta que enmarca el vidrio y calcomanías
 * gastadas en un costado. Arriba, el letrero luminoso de acrílico con sus tubos (una lata, un rayo, una estrella y una onda:
 * formas, no letras) y, debajo, el panel con la rejilla del altavoz, la pantallita de anuncios, el teclado con su visor ámbar,
 * la ranura de monedas y el lector de tarjetas. Detrás del vidrio, cada repisa es una bandeja con sus rieles y su luz cian, y
 * cada mes va en una tira de cristal líquido verde de sodio. Abajo, la boca de salida con su tapa y el asfalto mojado que
 * refleja las luces. Cada entrada es un producto (bolsa, lata o barra) metido en su espiral de alambre, con la etiqueta de
 * precio que es el día; detrás asoman más productos cuanto más escribiste. De noche llueve sobre el vidrio. Distinto del
 * Ideario, que es la barra del puesto de comida con su riel de comandas.
 * La tapa del diario es una bolsa grande en su espiral, con el ticket en su banda oscura y la etiqueta de precio con la
 * fecha; al abrirse, se rasga el sello.
 */
object EstanteNeon : DisenoBiblioteca {
    private val CARCASA = Color(0xFF221C33)
    private val CARCASA_LUZ = Color(0xFF3B3354)
    private val CARCASA_OSC = Color(0xFF110E1A)
    private val INTERIOR = Color(0xFF0A0E1C)
    private val ALAMBRE = Color(0xFFB9C1CE)
    private val MAGENTA = Color(0xFFFF2E8C)
    private val CIAN = Color(0xFF2EE8E0)
    private val AMBAR = Color(0xFFFFB238)
    private val SODIO = Color(0xFFB6FF4C)
    private val ETIQUETA = Color(0xFFF1EEDF)
    private val TINTA = Color(0xFF15131C)
    private val ASFALTO = Color(0xFF15141D)
    /** Los envoltorios: rojo, azul eléctrico, mostaza, verde, violeta y naranja. */
    private val ENVOLTORIOS = listOf(Color(0xFFD23A5A), Color(0xFF2F7FD0), Color(0xFFE0A030), Color(0xFF3FAF6A), Color(0xFF8A4FD6), Color(0xFFE2642E))
    private val NOCHE = Color(0xFF0A1030)
    private val LLUVIA = Color(0xFFB4CCFF)

    /** El color bajo la luz del callejón: hundido en oscuro (el tema siempre lo es) y, de noche, más azul. Los neones no pasan por aquí. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF06050B), 0.3f)
        if (noche) r = mezcla(r, NOCHE, 0.35f)
        return r
    }

    override val corona = 0.36f
    override val base = 0.15f
    override val lado = 0.06f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 4.dp
    override val letra: FontFamily = Letra.letrero

    override fun ancho(palabras: Int): Dp = (26 + (palabras / 50).coerceAtMost(16)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.74f + 0.16f * azar(semilla, 3)

    /** Un tubo de neón: el halo ancho y tenue, el brillo y el alma casi blanca (capas planas, sin degradado). */
    private fun DrawScope.tubo(p: Path, color: Color, grosor: Float) {
        drawPath(p, color.copy(alpha = 0.16f), style = Stroke(grosor * 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color.copy(alpha = 0.4f), style = Stroke(grosor * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, mezcla(color, Color.White, 0.5f), style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El callejón detrás y la carcasa entera de la máquina.
        drawRect(c(ASFALTO), Offset.Zero, size)
        val piso = d.base.top + d.base.height * 0.74f
        val tope = d.corona.top + d.corona.height * 0.03f
        drawRoundRect(c(CARCASA), Offset(0f, tope), Size(w, piso - tope), CornerRadius(7 * u))
        // El vidrio: el interior oscuro de la máquina.
        val vidrio = Rect(d.izquierda.right, d.corona.bottom - 4 * u, d.derecha.left, d.base.top + 3 * u)
        drawRect(c(INTERIOR), vidrio.topLeft, vidrio.size)
        // Cada repisa: la luz cian de la bandeja de arriba, su sombra y los rieles que separan las columnas.
        d.repisas.forEach { r ->
            drawRect(CIAN.copy(alpha = 0.05f), r.topLeft, Size(r.width, r.height * 0.35f))
            drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, Size(r.width, 7 * u))
            drawLine(CIAN.copy(alpha = 0.55f), Offset(r.left + 4 * u, r.top + 7.5f * u), Offset(r.right - 4 * u, r.top + 7.5f * u), 1.2f * u)
            val paso = r.width / 6f
            for (j in 1 until 6) drawLine(c(CARCASA_LUZ).copy(alpha = 0.4f), Offset(r.left + paso * j, r.top + 9 * u), Offset(r.left + paso * j, r.bottom), 1f * u)
        }
        // Los reflejos del vidrio: dos franjas en diagonal.
        clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
            listOf(0.18f to 0.1f, 0.62f to 0.05f).forEach { (fx, an) ->
                val x0 = vidrio.left + vidrio.width * fx
                val franja = Path().apply {
                    moveTo(x0, vidrio.top); lineTo(x0 + vidrio.width * an, vidrio.top)
                    lineTo(x0 + vidrio.width * (an - 0.3f), vidrio.bottom); lineTo(x0 - vidrio.width * 0.3f, vidrio.bottom); close()
                }
                drawPath(franja, Color.White.copy(alpha = 0.035f))
            }
        }
        // Las bandejas: chapa con su canal de precios y los botoncitos ámbar de cada columna.
        d.tablas.forEach { r ->
            drawRect(c(CARCASA_LUZ), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.14f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(c(CARCASA_OSC), Offset(r.left, r.top + r.height * 0.2f), Size(r.width, r.height * 0.6f))
            val paso = r.width / 6f
            for (j in 0 until 6) drawCircle(AMBAR.copy(alpha = 0.6f), 1.2f * u, Offset(r.left + paso * (j + 0.5f), r.center.y))
            drawRect(Color.Black.copy(alpha = 0.45f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una tira de cristal líquido verde de sodio, con su halo.
        d.placas.forEach { p ->
            val r = p.r
            drawRoundRect(SODIO.copy(alpha = 0.12f), r.inflate(2.5f * u).topLeft, r.inflate(2.5f * u).size, CornerRadius(3 * u))
            drawRoundRect(c(Color(0xFF0C1A0E)), r.topLeft, r.size, CornerRadius(2 * u))
            drawRoundRect(SODIO.copy(alpha = 0.7f), r.topLeft, r.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
            textoEn(medidor, p.texto.uppercase(), Rect(r.left + 3 * u, r.top + 0.5f * u, r.right - 3 * u, r.bottom - 0.5f * u), letra, SODIO, 10f, peso = FontWeight.SemiBold, espaciado = 1f)
        }
        // Los costados de la carcasa, con sus tornillos y el tubo magenta que enmarca el vidrio.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(CARCASA), r.topLeft, r.size)
            drawRect(c(if (izq) CARCASA_LUZ else CARCASA_OSC), Offset(if (izq) r.left else r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height))
            var yy = r.top + 10 * u
            while (yy < r.bottom - 6 * u) { drawCircle(c(CARCASA_OSC), 1.3f * u, Offset(r.center.x, yy)); yy += 34 * u }
            val x = if (izq) r.right - 1.5f * u else r.left + 1.5f * u
            tubo(Path().apply { moveTo(x, vidrio.top + 3 * u); lineTo(x, vidrio.bottom - 3 * u) }, MAGENTA, 1.3f * u)
        }
        // Las calcomanías del costado izquierdo, despegándose.
        val izqC = d.izquierda
        listOf(Triple(0.2f, -9f, CIAN), Triple(0.45f, 7f, AMBAR), Triple(0.7f, -4f, MAGENTA)).forEach { (fy, giro, col) ->
            val o = Offset(izqC.center.x - izqC.width * 0.1f, izqC.top + izqC.height * fy)
            val s = izqC.width * 0.5f
            rotate(giro, o) {
                drawRect(c(col).copy(alpha = 0.7f), Offset(o.x - s / 2, o.y - s * 0.7f), Size(s, s * 1.4f))
                drawRect(c(TINTA).copy(alpha = 0.5f), Offset(o.x - s * 0.3f, o.y - s * 0.2f), Size(s * 0.6f, s * 0.4f))
            }
        }
        corona(d.corona, oscuro, noche)
        // La base: la boca de salida con su tapa basculante y su flecha, la rejilla de ventilación y las patas.
        val b = d.base
        val boca = Rect(b.left + b.width * 0.16f, b.top + b.height * 0.1f, b.right - b.width * 0.34f, b.top + b.height * 0.52f)
        drawRoundRect(c(CARCASA_OSC), boca.inflate(2.5f * u).topLeft, boca.inflate(2.5f * u).size, CornerRadius(3 * u))
        drawRoundRect(c(Color(0xFF05050A)), boca.topLeft, boca.size, CornerRadius(2 * u))
        drawRect(c(CARCASA_LUZ).copy(alpha = 0.85f), boca.topLeft, Size(boca.width, boca.height * 0.55f))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(boca.left + 2 * u, boca.top + 1.5f * u), Offset(boca.right - 2 * u, boca.top + 1.5f * u), 1f * u)
        val fx = boca.center.x; val fy = boca.top + boca.height * 0.28f
        drawPath(Path().apply { moveTo(fx - 5 * u, fy - 2.5f * u); lineTo(fx + 5 * u, fy - 2.5f * u); lineTo(fx, fy + 3 * u); close() }, AMBAR.copy(alpha = 0.75f))
        val rejilla = Rect(b.right - b.width * 0.28f, b.top + b.height * 0.14f, b.right - b.width * 0.1f, b.top + b.height * 0.5f)
        var ry = rejilla.top
        while (ry < rejilla.bottom) { drawLine(c(CARCASA_OSC), Offset(rejilla.left, ry), Offset(rejilla.right, ry), 1.4f * u); ry += 3.2f * u }
        drawRect(c(CARCASA_OSC), Offset(b.left, piso - 4 * u), Size(b.width, 4 * u))
        listOf(b.left + b.width * 0.06f, b.right - b.width * 0.1f).forEach { x -> drawRect(c(CARCASA_OSC), Offset(x, piso), Size(b.width * 0.04f, 3 * u)) }
        // El asfalto mojado: el charco de luz magenta y los reflejos de los neones.
        mancha(Offset(w * 0.5f, piso + (h - piso) * 0.5f), w * 0.32f, MAGENTA.copy(alpha = 0.14f))
        listOf(0.2f to MAGENTA, 0.5f to CIAN, 0.78f to SODIO).forEach { (fr, col) ->
            for (j in 0 until 4) drawLine(col.copy(alpha = 0.22f - 0.04f * j), Offset(w * fr - 6 * u + j * 4 * u, piso + 2 * u + j * 1.5f * u), Offset(w * fr + 6 * u + j * 4 * u, piso + 2 * u + j * 1.5f * u), 1.2f * u)
        }
        // De noche, la lluvia: el agua que cae delante, las gotas que escurren por el vidrio y las ondas del charco.
        if (noche) {
            val gotas = (h / (6 * u)).toInt().coerceIn(40, 400)
            for (k in 0 until gotas) {
                val x = w * azar(k, 11); val y = h * azar(k, 12)
                val largo = (4 + 10 * azar(k, 13)) * u
                drawLine(LLUVIA.copy(alpha = 0.18f), Offset(x, y), Offset(x - largo * 0.18f, y + largo), 0.8f * u)
            }
            clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
                for (k in 0 until gotas / 2) {
                    val o = Offset(vidrio.left + vidrio.width * azar(k, 21), vidrio.top + vidrio.height * azar(k, 22))
                    val rg = (1 + 1.6f * azar(k, 23)) * u
                    drawCircle(LLUVIA.copy(alpha = 0.22f), rg, o)
                    drawCircle(Color.White.copy(alpha = 0.5f), rg * 0.35f, Offset(o.x - rg * 0.3f, o.y - rg * 0.3f))
                    if (azar(k, 24) > 0.6f) drawLine(LLUVIA.copy(alpha = 0.16f), Offset(o.x, o.y + rg), Offset(o.x + 1 * u, o.y + rg + 18 * u * azar(k, 25)), 0.8f * u)
                }
            }
            listOf(0.3f, 0.66f).forEach { fr -> drawOval(LLUVIA.copy(alpha = 0.25f), Offset(w * fr - 8 * u, piso + (h - piso) * 0.4f), Size(16 * u, 4 * u), style = Stroke(0.7f * u)) }
        }
    }

    /** El letrero luminoso de arriba y el panel del teclado. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // La caja del letrero, con su halo magenta y los focos que corren por los bordes.
        val caja = Rect(r.left + r.width * 0.05f, r.top + r.height * 0.1f, r.right - r.width * 0.05f, r.top + r.height * 0.5f)
        mancha(caja.center, caja.width * 0.6f, MAGENTA.copy(alpha = 0.2f))
        drawRoundRect(c(Color(0xFF140B20)), caja.topLeft, caja.size, CornerRadius(5 * u))
        drawRoundRect(c(CARCASA_LUZ), caja.topLeft, caja.size, CornerRadius(5 * u), style = Stroke(2 * u))
        val focos = 22
        for (k in 0 until focos) {
            val x = caja.left + 4 * u + (caja.width - 8 * u) * k / (focos - 1).toFloat()
            drawCircle(if (k % 2 == 0) AMBAR else MAGENTA, 1.2f * u, Offset(x, caja.top + 3.5f * u))
            drawCircle(if (k % 2 == 0) MAGENTA else AMBAR, 1.2f * u, Offset(x, caja.bottom - 3.5f * u))
        }
        // Los cuatro tubos: una lata, un rayo, una estrella y una onda (formas, no letras).
        val ins = Rect(caja.left + 8 * u, caja.top + 8 * u, caja.right - 8 * u, caja.bottom - 8 * u)
        val hueco = ins.width / 4f
        val g = 1.3f * u
        val s = minOf(ins.height, hueco) * 0.42f
        fun centro(k: Int) = Offset(ins.left + hueco * (k + 0.5f), ins.center.y)
        val c0 = centro(0)
        tubo(Path().apply {
            moveTo(c0.x - s * 0.5f, c0.y - s * 0.8f); lineTo(c0.x + s * 0.5f, c0.y - s * 0.8f)
            lineTo(c0.x + s * 0.55f, c0.y + s * 0.8f); lineTo(c0.x - s * 0.55f, c0.y + s * 0.8f); close()
            moveTo(c0.x - s * 0.5f, c0.y - s * 0.45f); lineTo(c0.x + s * 0.5f, c0.y - s * 0.45f)
        }, CIAN, g)
        val c1 = centro(1)
        tubo(Path().apply {
            moveTo(c1.x + s * 0.3f, c1.y - s); lineTo(c1.x - s * 0.35f, c1.y + s * 0.1f)
            lineTo(c1.x + s * 0.15f, c1.y + s * 0.1f); lineTo(c1.x - s * 0.3f, c1.y + s)
        }, MAGENTA, g)
        tubo(estrella(centro(2), s, 5, 0.45f), AMBAR, g)
        val c3 = centro(3)
        tubo(Path().apply {
            for (fila in 0..1) {
                val y0 = c3.y - s * 0.35f + fila * s * 0.7f
                moveTo(c3.x - s, y0)
                quadraticTo(c3.x - s * 0.5f, y0 - s * 0.4f, c3.x, y0)
                quadraticTo(c3.x + s * 0.5f, y0 + s * 0.4f, c3.x + s, y0)
            }
        }, SODIO, g)
        // El panel de abajo: la rejilla del altavoz, la pantallita de anuncios, el teclado, el monedero y el lector.
        val panel = Rect(r.left + r.width * 0.05f, r.top + r.height * 0.56f, r.right - r.width * 0.05f, r.bottom - r.height * 0.06f)
        drawRoundRect(c(CARCASA_OSC), panel.topLeft, panel.size, CornerRadius(3 * u))
        val altavoz = Rect(panel.left + 6 * u, panel.top + 6 * u, panel.left + panel.width * 0.18f, panel.bottom - 6 * u)
        var ay = altavoz.top
        while (ay < altavoz.bottom) { drawRoundRect(c(CARCASA_LUZ), Offset(altavoz.left, ay), Size(altavoz.width, 1.6f * u), CornerRadius(0.8f * u)); ay += 3.6f * u }
        val pantalla = Rect(panel.left + panel.width * 0.24f, panel.top + 6 * u, panel.left + panel.width * 0.56f, panel.bottom - 6 * u)
        drawRect(c(Color(0xFF05070F)), pantalla.topLeft, pantalla.size)
        listOf(MAGENTA, CIAN, AMBAR).forEachIndexed { j, col ->
            drawRect(col.copy(alpha = 0.55f), Offset(pantalla.left + 3 * u, pantalla.top + 3 * u + j * pantalla.height * 0.28f), Size((pantalla.width - 6 * u) * (0.9f - 0.22f * j), pantalla.height * 0.16f))
        }
        drawRect(c(CARCASA_LUZ), pantalla.topLeft, pantalla.size, style = Stroke(1.2f * u))
        // El teclado: el visor ámbar con sus tres casillas y las teclas, tres por cuatro.
        val teclado = Rect(panel.left + panel.width * 0.62f, panel.top + 5 * u, panel.left + panel.width * 0.84f, panel.bottom - 5 * u)
        val visor = Rect(teclado.left, teclado.top, teclado.right, teclado.top + teclado.height * 0.24f)
        drawRect(c(Color(0xFF1A0F02)), visor.topLeft, visor.size)
        for (j in 0 until 3) drawRect(AMBAR.copy(alpha = 0.85f), Offset(visor.left + visor.width * (0.12f + j * 0.28f), visor.top + visor.height * 0.25f), Size(visor.width * 0.18f, visor.height * 0.5f), style = Stroke(1f * u))
        val filaT = teclado.height * 0.72f / 4f; val colT = teclado.width / 3f
        for (fi in 0 until 4) for (co in 0 until 3) {
            val k0 = Offset(teclado.left + colT * co + colT * 0.12f, teclado.top + teclado.height * 0.28f + filaT * fi + filaT * 0.1f)
            val tecla = Size(colT * 0.76f, filaT * 0.78f)
            drawRoundRect(c(CARCASA_LUZ), k0, tecla, CornerRadius(1.2f * u))
            drawCircle(if (fi == 3 && co == 2) SODIO else c(ALAMBRE).copy(alpha = 0.6f), 0.8f * u, Offset(k0.x + tecla.width / 2, k0.y + tecla.height / 2))
        }
        val mx = panel.left + panel.width * 0.92f
        drawRoundRect(c(ALAMBRE), Offset(mx - 3 * u, panel.top + 6 * u), Size(6 * u, panel.height * 0.36f), CornerRadius(1.5f * u))
        drawRect(Color.Black, Offset(mx - 0.6f * u, panel.top + 8 * u), Size(1.2f * u, panel.height * 0.36f - 4 * u))
        drawRoundRect(c(CARCASA_LUZ), Offset(mx - 4 * u, panel.top + panel.height * 0.56f), Size(8 * u, panel.height * 0.3f), CornerRadius(1.5f * u))
        drawCircle(SODIO, 1.1f * u, Offset(mx, panel.top + panel.height * 0.62f))
        // El borde de arriba del vidrio.
        drawRect(c(CARCASA_LUZ), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = ENVOLTORIOS[(v.semilla and 0x7fffffff) % ENVOLTORIOS.size]
        val color = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val forma = (v.semilla ushr 5) % 3
        val etiquetaAlta = minOf(h * 0.15f, 15 * u)
        val suelo = h - etiquetaAlta - 1 * u
        // La espiral de alambre: la mitad de atrás va detrás del producto.
        val espiral = Rect(w * 0.03f, suelo - h * 0.26f, w * 0.97f, suelo)
        drawArc(c(ALAMBRE).copy(alpha = 0.45f), 180f, 180f, false, espiral.topLeft, espiral.size, style = Stroke(1.3f * u))
        // Los que esperan detrás en la espiral: más cuanto más escribiste.
        val paso = 1.8f * u
        val detras = (v.lleno * 3).toInt()
        val frente = Rect(w * 0.08f, h * 0.03f + detras * paso, w * 0.92f - detras * paso, suelo - h * 0.04f)
        for (j in detras downTo 1) {
            val atras = frente.translate(j * paso, -j * paso)
            drawRoundRect(mezcla(color, Color.Black, 0.35f + 0.12f * j), atras.topLeft, atras.size, CornerRadius(2 * u))
        }
        when (forma) {
            0 -> {
                // Una bolsa de papas, con sus bordes sellados en zigzag y su ventanita.
                drawRoundRect(color, frente.topLeft, frente.size, CornerRadius(frente.width * 0.18f))
                val sello = frente.height * 0.07f
                listOf(true, false).forEach { arriba ->
                    val y0 = if (arriba) frente.top else frente.bottom - sello
                    drawRect(mezcla(color, Color.White, 0.25f), Offset(frente.left, y0), Size(frente.width, sello))
                    val borde = if (arriba) y0 + sello else y0
                    val dientes = Path().apply {
                        moveTo(frente.left, borde)
                        for (k in 0 until 6) {
                            lineTo(frente.left + frente.width * (k + 0.5f) / 6f, if (arriba) borde - sello * 0.5f else borde + sello * 0.5f)
                            lineTo(frente.left + frente.width * (k + 1f) / 6f, borde)
                        }
                    }
                    drawPath(dientes, Color.Black.copy(alpha = 0.3f), style = Stroke(0.6f * u))
                }
                val ventana = Rect(frente.left + frente.width * 0.2f, frente.top + frente.height * 0.64f, frente.right - frente.width * 0.2f, frente.bottom - frente.height * 0.12f)
                drawOval(c(ETIQUETA).copy(alpha = 0.85f), ventana.topLeft, ventana.size)
                for (k in 0 until 4) drawCircle(c(AMBAR), ventana.height * 0.16f, Offset(ventana.left + ventana.width * (0.2f + 0.2f * k), ventana.center.y + ventana.height * (if (k % 2 == 0) -0.1f else 0.12f)))
            }
            1 -> {
                // Una lata, con su tapa y su fondo de aluminio y el brillo a lo largo.
                drawRect(color, frente.topLeft, frente.size)
                val tapa = frente.height * 0.06f
                drawRect(c(ALAMBRE), frente.topLeft, Size(frente.width, tapa))
                drawRect(c(ALAMBRE), Offset(frente.left, frente.bottom - tapa), Size(frente.width, tapa))
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(frente.left, frente.top + tapa), Size(frente.width, 1 * u))
                drawRect(Color.White.copy(alpha = 0.22f), Offset(frente.left + frente.width * 0.18f, frente.top + tapa), Size(frente.width * 0.1f, frente.height - tapa * 2))
                drawRect(c(TINTA).copy(alpha = 0.35f), Offset(frente.left, frente.top + frente.height * 0.66f), Size(frente.width, frente.height * 0.08f))
            }
            else -> {
                // Una barra en su caja, con la banda en diagonal y una estrella de oferta.
                drawRect(color, frente.topLeft, frente.size)
                clipRect(frente.left, frente.top, frente.right, frente.bottom) {
                    val banda = Path().apply {
                        moveTo(frente.left, frente.top + frente.height * 0.55f); lineTo(frente.right, frente.top + frente.height * 0.4f)
                        lineTo(frente.right, frente.top + frente.height * 0.52f); lineTo(frente.left, frente.top + frente.height * 0.67f); close()
                    }
                    drawPath(banda, c(ETIQUETA).copy(alpha = 0.8f))
                }
                drawPath(estrella(Offset(frente.right - frente.width * 0.3f, frente.top + frente.height * 0.8f), frente.width * 0.2f, 8, 0.65f), c(AMBAR))
                drawRect(Color.Black.copy(alpha = 0.22f), Offset(frente.right - frente.width * 0.12f, frente.top), Size(frente.width * 0.12f, frente.height))
            }
        }
        // El nombre del mes a lo largo del envoltorio, en una banda oscura.
        val banda = Rect(frente.center.x - frente.width * 0.2f, frente.top + frente.height * 0.2f, frente.center.x + frente.width * 0.2f, frente.top + frente.height * 0.64f)
        drawRect(c(TINTA).copy(alpha = 0.55f), banda.topLeft, banda.size)
        textoEn(medidor, v.mes.uppercase(), banda.deflate(0.5f * u), letra, ETIQUETA, 8f, vertical = true, peso = FontWeight.SemiBold, minimo = 4f)
        // La marca de arriba (un círculo con su rayo); de noche, una luna y dos estrellitas.
        val m = Offset(frente.center.x, frente.top + frente.height * 0.12f)
        val rm = minOf(frente.width * 0.2f, 5 * u)
        drawCircle(c(ETIQUETA), rm, m)
        if (v.noche) {
            drawCircle(color, rm * 0.8f, Offset(m.x + rm * 0.45f, m.y - rm * 0.25f))
            drawCircle(c(ETIQUETA), 0.7f * u, Offset(m.x - rm * 1.6f, m.y + rm * 0.6f))
            drawCircle(c(ETIQUETA), 0.6f * u, Offset(m.x + rm * 1.5f, m.y + rm * 0.9f))
        } else {
            drawPath(Path().apply {
                moveTo(m.x + rm * 0.2f, m.y - rm * 0.7f); lineTo(m.x - rm * 0.35f, m.y + rm * 0.1f)
                lineTo(m.x + rm * 0.1f, m.y + rm * 0.1f); lineTo(m.x - rm * 0.2f, m.y + rm * 0.7f)
            }, color, style = Stroke(1f * u))
        }
        // La mitad de delante de la espiral, por encima del producto, y la punta del alambre.
        drawArc(c(ALAMBRE), 0f, 180f, false, espiral.topLeft, espiral.size, style = Stroke(1.5f * u))
        drawLine(c(ALAMBRE), Offset(espiral.center.x, espiral.bottom - 0.75f * u), Offset(espiral.center.x + w * 0.2f, espiral.bottom - 0.75f * u), 1.5f * u, cap = StrokeCap.Round)
        // El reflejo del vidrio que hay delante y, de noche, las gotas de lluvia sobre él.
        drawPath(Path().apply { moveTo(w * 0.55f, 0f); lineTo(w * 0.8f, 0f); lineTo(w * 0.3f, h); lineTo(w * 0.05f, h); close() }, Color.White.copy(alpha = 0.05f))
        if (v.noche) for (k in 0 until 3) {
            val o = Offset(w * (0.2f + 0.6f * azar(v.semilla, 30 + k)), h * (0.1f + 0.6f * azar(v.semilla, 40 + k)))
            drawCircle(LLUVIA.copy(alpha = 0.3f), 1.4f * u, o)
            drawCircle(Color.White.copy(alpha = 0.6f), 0.5f * u, Offset(o.x - 0.4f * u, o.y - 0.4f * u))
            drawLine(LLUVIA.copy(alpha = 0.2f), Offset(o.x, o.y + 1.4f * u), Offset(o.x, o.y + 7 * u), 0.7f * u)
        }
        // La etiqueta de precio en el canto de la bandeja: el día.
        val precio = Rect(w * 0.1f, h - etiquetaAlta, w * 0.9f, h)
        drawRect(c(ETIQUETA), precio.topLeft, precio.size)
        drawRect(MAGENTA, precio.topLeft, Size(precio.width * 0.14f, precio.height))
        textoEn(medidor, v.dia, Rect(precio.left + precio.width * 0.14f, precio.top, precio.right, precio.bottom), letra, c(TINTA), 11f, peso = FontWeight.SemiBold)
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 190.dp
    override val cajaTapa = Rect(0.235f, 0.33f, 0.835f, 0.665f)
    override val letraTapa: FontFamily = Letra.letrero
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = ETIQUETA
    override fun palabrasTapa(n: Int): String = (if (n == 1) "1 palabra" else "$n palabras") + " en el ticket"

    /**
     * El producto del día visto por el vidrio de la máquina: la bolsa grande, con sus sellos en zigzag, la marca, la estrella
     * de oferta y la banda oscura donde va el ticket (la cabecera del puesto y lo escrito), metida en su espiral de alambre;
     * detrás esperan más bolsas cuanto más escribiste. Debajo, la etiqueta de precio con la fecha y las palabras, y el tubo
     * magenta que enmarca el vidrio. Al abrirse, el sello de arriba se rasga y se levanta, y el ticket sube por la abertura.
     * De noche llueve sobre el vidrio.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val color = c(ENVOLTORIOS[(e.semilla and 0x7fffffff) % ENVOLTORIOS.size])
        clipRect(0f, 0f, w, h) {
            // La carcasa y, dentro, el vidrio con el fondo oscuro de la máquina.
            drawRoundRect(c(CARCASA), Offset.Zero, size, CornerRadius(8 * u))
            drawRect(c(CARCASA_LUZ), Offset(0f, 0f), Size(w, 1.5f * u))
            val vidrio = Rect(w * 0.035f, h * 0.04f, w * 0.965f, h * 0.83f)
            drawRect(c(INTERIOR), vidrio.topLeft, vidrio.size)
            // La bandeja de arriba, con su luz cian (más fuerte al abrir, como si la máquina despertara).
            drawRect(c(CARCASA_LUZ), vidrio.topLeft, Size(vidrio.width, h * 0.045f))
            drawRect(c(CARCASA_OSC), Offset(vidrio.left, vidrio.top + h * 0.045f), Size(vidrio.width, 2 * u))
            drawRect(CIAN.copy(alpha = 0.05f + 0.08f * a), Offset(vidrio.left, vidrio.top + h * 0.045f), Size(vidrio.width, vidrio.height * 0.3f))
            drawLine(CIAN.copy(alpha = 0.7f), Offset(vidrio.left + 6 * u, vidrio.top + h * 0.045f + 2.5f * u), Offset(vidrio.right - 6 * u, vidrio.top + h * 0.045f + 2.5f * u), 1.3f * u)
            // La espiral de alambre: la mitad de atrás, detrás de la bolsa.
            val espiral = Rect(w * 0.05f, h * 0.6f, w * 0.95f, h * 0.86f)
            drawArc(c(ALAMBRE).copy(alpha = 0.4f), 180f, 180f, false, espiral.topLeft, espiral.size, style = Stroke(1.6f * u))
            // La bolsa: el cuerpo acolchado, el sello de arriba (que se rasga al abrir) y el de abajo.
            val izq = w * 0.09f; val der = w * 0.91f
            val corte = h * 0.2f; val fondo = h * 0.76f
            val sello = h * 0.065f
            // Las que esperan detrás, asomando arriba a la derecha.
            val detras = (e.palabras / 800f * 3).toInt().coerceIn(0, 3)
            for (j in detras downTo 1) {
                val d = j * 4.5f * u
                drawRoundRect(mezcla(color, Color.Black, 0.35f + 0.12f * j), Offset(izq + d, corte - sello - d), Size(der - izq, fondo - corte + sello), CornerRadius(6 * u))
            }
            val cuerpo = Path().apply {
                moveTo(izq, corte); lineTo(der, corte)
                quadraticTo(der + w * 0.014f, (corte + fondo) / 2, der, fondo); lineTo(izq, fondo)
                quadraticTo(izq - w * 0.014f, (corte + fondo) / 2, izq, corte); close()
            }
            drawPath(cuerpo, Brush.horizontalGradient(listOf(mezcla(color, Color.Black, 0.3f), mezcla(color, Color.White, 0.18f), color, mezcla(color, Color.Black, 0.35f)), izq, der))
            // El sello de abajo, con su prensado y su borde en zigzag.
            val sAbajo = Rect(izq, fondo - sello * 0.7f, der, fondo)
            drawRect(mezcla(color, Color.White, 0.22f), sAbajo.topLeft, sAbajo.size)
            var px = izq + 2 * u
            while (px < der) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(px, sAbajo.top + 1 * u), Offset(px, sAbajo.bottom - 1 * u), 0.6f * u); px += 3 * u }
            drawPath(Path().apply {
                moveTo(izq, fondo); val n = 28
                for (k in 0 until n) { lineTo(izq + (der - izq) * (k + 0.5f) / n, fondo + 2.5f * u); lineTo(izq + (der - izq) * (k + 1f) / n, fondo) }
            }, mezcla(color, Color.White, 0.22f))
            // La marca (un círculo con su rayo; de noche, una luna con dos estrellas) y la estrella de oferta.
            val marca = Offset(w * 0.155f, h * 0.36f)
            val rm = minOf(w * 0.045f, 15 * u)
            drawCircle(c(ETIQUETA), rm, marca)
            drawCircle(c(TINTA).copy(alpha = 0.5f), rm, marca, style = Stroke(1 * u))
            if (e.noche) {
                drawCircle(c(ETIQUETA), rm * 0.5f, marca)
                drawCircle(color, rm * 0.42f, Offset(marca.x + rm * 0.22f, marca.y - rm * 0.14f))
                drawPath(estrella(Offset(marca.x - rm * 0.5f, marca.y + rm * 0.45f), rm * 0.16f, 4, 0.4f), color)
                drawPath(estrella(Offset(marca.x + rm * 0.5f, marca.y + rm * 0.5f), rm * 0.12f, 4, 0.4f), color)
            } else {
                drawPath(Path().apply {
                    moveTo(marca.x + rm * 0.25f, marca.y - rm * 0.7f); lineTo(marca.x - rm * 0.35f, marca.y + rm * 0.08f)
                    lineTo(marca.x + rm * 0.1f, marca.y + rm * 0.08f); lineTo(marca.x - rm * 0.25f, marca.y + rm * 0.7f)
                }, color, style = Stroke(2 * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            val oferta = Offset(w * 0.155f, h * 0.575f)
            drawPath(estrella(oferta, rm * 0.95f, 10, 0.72f), c(AMBAR))
            drawCircle(c(TINTA).copy(alpha = 0.6f), rm * 0.32f, oferta, style = Stroke(1 * u))
            drawCircle(c(TINTA).copy(alpha = 0.6f), rm * 0.1f, oferta)
            // La banda oscura del ticket: la cabecera del puesto, el filete magenta y, debajo, el extracto.
            val banda = Rect(w * 0.215f, h * 0.235f, w * 0.855f, h * 0.685f)
            drawRoundRect(mezcla(c(TINTA), color, 0.12f), banda.topLeft, banda.size, CornerRadius(3 * u))
            drawRoundRect(c(ETIQUETA).copy(alpha = 0.18f), banda.topLeft, banda.size, CornerRadius(3 * u), style = Stroke(1 * u))
            textoEn(medidor, if (e.noche) "夢 · TICKET DE LA NOCHE" else "屋台 · TICKET DEL DÍA", Rect(banda.left + 6 * u, banda.top + 2 * u, banda.right - 6 * u, banda.top + h * 0.075f),
                letra, AMBAR, 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
            drawLine(MAGENTA.copy(alpha = 0.85f), Offset(banda.left + 6 * u, banda.top + h * 0.083f), Offset(banda.right - 6 * u, banda.top + h * 0.083f), 1.2f * u)
            drawLine(MAGENTA.copy(alpha = 0.25f), Offset(banda.left + 6 * u, banda.top + h * 0.083f), Offset(banda.right - 6 * u, banda.top + h * 0.083f), 3.5f * u)
            // El ticket que sube por la abertura al rasgarse el sello.
            if (a > 0.01f) clipRect(0f, 0f, w, corte) {
                val t0 = corte - h * 0.24f * a
                val tk = Rect(w * 0.34f, t0, w * 0.6f, corte + 2 * u)
                drawPath(Path().apply {
                    moveTo(tk.left, tk.bottom); lineTo(tk.left, tk.top + 2 * u)
                    val n = 10
                    for (k in 0 until n) { lineTo(tk.left + tk.width * (k + 0.5f) / n, tk.top); lineTo(tk.left + tk.width * (k + 1f) / n, tk.top + 2 * u) }
                    lineTo(tk.right, tk.bottom); close()
                }, c(ETIQUETA))
                var ty = tk.top + 6 * u
                while (ty < tk.bottom - 2 * u) { drawLine(c(TINTA).copy(alpha = 0.45f), Offset(tk.left + 4 * u, ty), Offset(tk.right - 4 * u - 6 * u * azar(e.semilla, (ty / u).toInt()), ty), 0.9f * u); ty += 5 * u }
            }
            // El sello de arriba: al abrir se rasga desde la izquierda y se levanta.
            rotate(a * 14f, Offset(der, corte)) {
                val sArriba = Rect(izq, corte - sello, der, corte)
                drawRect(mezcla(color, Color.White, 0.22f), sArriba.topLeft, sArriba.size)
                var sx = izq + 2 * u
                while (sx < der) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(sx, sArriba.top + 1 * u), Offset(sx, sArriba.bottom - 1 * u), 0.6f * u); sx += 3 * u }
                drawPath(Path().apply {
                    moveTo(izq, sArriba.top); val n = 28
                    for (k in 0 until n) { lineTo(izq + (der - izq) * (k + 0.5f) / n, sArriba.top - 2.5f * u); lineTo(izq + (der - izq) * (k + 1f) / n, sArriba.top) }
                }, mezcla(color, Color.White, 0.22f))
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(izq, corte), Offset(der, corte), 0.8f * u)
                // La muesca para rasgar, a la izquierda.
                drawPath(Path().apply { moveTo(izq, corte - sello * 0.35f); lineTo(izq + 4 * u, corte - sello * 0.5f); lineTo(izq, corte - sello * 0.65f); close() }, c(INTERIOR))
            }
            // La mitad de delante de la espiral, por encima de la bolsa, y la punta del alambre.
            drawArc(c(ALAMBRE), 0f, 180f, false, espiral.topLeft, espiral.size, style = Stroke(2 * u))
            drawArc(Color.White.copy(alpha = 0.4f), 40f, 60f, false, espiral.topLeft, espiral.size, style = Stroke(0.7f * u))
            drawLine(c(ALAMBRE), Offset(espiral.center.x, espiral.bottom - 1 * u), Offset(espiral.center.x + w * 0.2f, espiral.bottom - 1 * u), 2 * u, cap = StrokeCap.Round)
            // El reflejo del vidrio, a un lado de la banda.
            clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
                drawPath(Path().apply { moveTo(w * 0.86f, vidrio.top); lineTo(w * 0.93f, vidrio.top); lineTo(w * 0.83f, vidrio.bottom); lineTo(w * 0.76f, vidrio.bottom); close() }, Color.White.copy(alpha = 0.05f))
            }
            // De noche, las gotas de lluvia en el vidrio (ninguna sobre lo escrito) y sus regueros.
            if (e.noche) {
                val caja = Rect(w * cajaTapa.left, h * cajaTapa.top, w * cajaTapa.right, h * cajaTapa.bottom).inflate(3 * u)
                for (k in 0 until 46) {
                    val o = Offset(vidrio.left + vidrio.width * azar(e.semilla, 700 + k), vidrio.top + vidrio.height * azar(e.semilla, 800 + k))
                    if (caja.contains(o)) continue
                    val rg = (1 + 1.8f * azar(e.semilla, 900 + k)) * u
                    drawCircle(LLUVIA.copy(alpha = 0.25f), rg, o)
                    drawCircle(Color.White.copy(alpha = 0.55f), rg * 0.35f, Offset(o.x - rg * 0.3f, o.y - rg * 0.3f))
                    if (azar(e.semilla, 950 + k) > 0.55f) drawLine(LLUVIA.copy(alpha = 0.18f), Offset(o.x, o.y + rg), Offset(o.x + 0.8f * u, minOf(o.y + rg + 20 * u * azar(e.semilla, 990 + k), vidrio.bottom)), 0.8f * u)
                }
            }
            // El tubo de neón que enmarca el vidrio.
            tubo(Path().apply {
                moveTo(vidrio.left + 2 * u, vidrio.top + 2 * u); lineTo(vidrio.right - 2 * u, vidrio.top + 2 * u)
                lineTo(vidrio.right - 2 * u, vidrio.bottom - 2 * u); lineTo(vidrio.left + 2 * u, vidrio.bottom - 2 * u); close()
            }, MAGENTA, 1.3f * u)
            // La etiqueta de precio en el canto: el puesto, la fecha y las palabras.
            val precio = Rect(w * 0.05f, h * 0.855f, w * 0.95f, h * 0.965f)
            drawRect(c(CARCASA_OSC), precio.inflate(1.5f * u).topLeft, precio.inflate(1.5f * u).size)
            drawRect(mezcla(ETIQUETA, Color(0xFF06050B), (if (oscuro) 0.1f else 0f) + (if (e.noche) 0.08f else 0f)), precio.topLeft, precio.size)
            val bloque = Rect(precio.left, precio.top, precio.left + precio.width * 0.12f, precio.bottom)
            drawRect(MAGENTA, bloque.topLeft, bloque.size)
            textoEn(medidor, if (e.noche) "夢" else "屋台", bloque.deflate(1.5f * u), letra, ETIQUETA, 11f, peso = FontWeight.Bold)
            textoEn(medidor, e.fecha, Rect(bloque.right + 4 * u, precio.top, precio.left + precio.width * 0.52f, precio.bottom), letra, c(TINTA), 12f, peso = FontWeight.SemiBold, minimo = 7f)
            drawLine(c(TINTA).copy(alpha = 0.3f), Offset(precio.left + precio.width * 0.53f, precio.top + 3 * u), Offset(precio.left + precio.width * 0.53f, precio.bottom - 3 * u), 0.8f * u)
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(precio.left + precio.width * 0.54f, precio.top, precio.right - 4 * u, precio.bottom), letra, c(TINTA).copy(alpha = 0.85f), 10f, minimo = 6f)
            // Los tornillos de la carcasa.
            listOf(Offset(w * 0.018f, h * 0.9f), Offset(w * 0.982f, h * 0.9f)).forEach { o -> drawCircle(c(CARCASA_OSC), 1.8f * u, o); drawLine(c(CARCASA_LUZ), Offset(o.x - 1.2f * u, o.y), Offset(o.x + 1.2f * u, o.y), 0.6f * u) }
        }
    }
}

/**
 * Refugio: el armario de taquillas del refugio, de chapa pintada de verde azulado (el optimismo atómico del tema), contra el
 * muro de hormigón crema. Arriba corren las tuberías del techo (una gruesa de acero y una de cobre, con sus bridas, la
 * válvula de volante roja y el manómetro), y sobre el armario está la sirena enjaulada junto a la placa amarilla del refugio
 * (un triángulo y barras: formas, no letras) y sus rejillas. Los costados son los marcos de las puertas: a la izquierda las
 * bisagras, la rejilla y la placa del número; a la derecha el pasador con su candado. El fondo de cada taquilla es de chapa
 * perforada; las tablas llevan remaches y cada mes va en una pantallita de fósforo verde. Abajo, el zócalo con su rejilla y el
 * piso de hormigón con la franja amarilla y la coladera. Cada entrada es un carrete de cinta abierto en su estuche (no un
 * cartucho): la brida con sus seis ventanas deja ver la cinta, más gruesa cuanto más escribiste, y arriba va la etiqueta del
 * día. De noche se enciende la luz roja de emergencia. Distinto del Ideario, que es el armario de cartuchos con la puerta de la
 * bóveda.
 * La tapa del diario es el estuche del carrete cerrado, con la etiqueta de la bitácora y la ventanilla al carrete; al
 * abrirse, la tapa se levanta.
 */
object EstanteRefugio : DisenoBiblioteca {
    private val PINTURA = Color(0xFF3F6B70)
    private val PINTURA_LUZ = Color(0xFF5F8C90)
    private val PINTURA_OSC = Color(0xFF284A4E)
    private val INTERIOR = Color(0xFF1C2829)
    private val MURO = Color(0xFFD9CFAF)
    private val TUBO = Color(0xFF8A9294)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val COBRE = Color(0xFFB0703F)
    private val AMARILLO = Color(0xFFE5B436)
    private val CREMA = Color(0xFFF0E6C8)
    private val ROJO = Color(0xFFC8392B)
    private val SIRENA = Color(0xFFFF3B26)
    private val FOSFORO = Color(0xFF7DFF8C)
    private val PISO = Color(0xFF55574F)
    private val NEGRO = Color(0xFF141716)
    private val CINTA = Color(0xFF4A2E1C)
    /** Los estuches: cartón, oliva, hueso, tabaco y pizarra. */
    private val ESTUCHES = listOf(Color(0xFFC9B48A), Color(0xFF6E7F5E), Color(0xFFDCD3B8), Color(0xFF8A5A3C), Color(0xFF4F6A7A))
    /** Las bridas de los carretes: aluminio, azul, blanco, rojo y negro. */
    private val BRIDAS = listOf(Color(0xFFB8BEC2), Color(0xFF3A6E9E), Color(0xFFD9D3BF), Color(0xFFC9452E), Color(0xFF353535))
    private val NOCHE = Color(0xFF2A0606)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luz roja de emergencia. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0A0F0F), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.42f)
        return r
    }

    /** El papel de las etiquetas: se hunde menos, para que el día se lea en oscuro y de noche. */
    private fun papel(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0A0F0F), 0.16f)
        if (noche) r = mezcla(r, NOCHE, 0.22f)
        return r
    }

    override val corona = 0.27f
    override val base = 0.1f
    override val lado = 0.07f
    override val altoRepisa: Dp = 96.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 5.dp
    override val letra: FontFamily = LetraExtra.vt323

    override fun ancho(palabras: Int): Dp = (46 + (palabras / 100).coerceAtMost(8)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.58f + 0.08f * azar(semilla, 4)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de hormigón pintado del refugio, detrás de todo.
        drawRect(c(MURO), Offset.Zero, size)
        // El fondo de las taquillas: chapa perforada en tresbolillo, con la sombra de la tabla de arriba.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(INTERIOR), cuerpo.topLeft, cuerpo.size)
        d.repisas.forEach { r ->
            var py = r.top + 16 * u; var fila = 0
            while (py < r.bottom - 4 * u) {
                var px = r.left + (if (fila % 2 == 0) 6 * u else 12 * u)
                while (px < r.right - 3 * u) { drawCircle(Color.Black.copy(alpha = 0.4f), 1.1f * u, Offset(px, py)); px += 12 * u }
                py += 9 * u; fila++
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.45f else 0.32f), r.topLeft, Size(r.width, 9 * u))
        }
        // Los costados: los marcos de las puertas, con bisagras, rejilla y número a la izquierda, y el candado a la derecha.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(PINTURA), r.topLeft, r.size)
            drawRect(c(PINTURA_LUZ), Offset(if (izq) r.left else r.right - 2 * u, r.top), Size(2 * u, r.height))
            drawRect(c(PINTURA_OSC), Offset(if (izq) r.right - 2.5f * u else r.left, r.top), Size(2.5f * u, r.height))
            d.repisas.forEachIndexed { k, rep ->
                val ry = if (izq) rep.top + rep.height * 0.62f else rep.top + 10 * u
                for (j in 0 until 3) drawRoundRect(c(PINTURA_OSC), Offset(r.left + r.width * 0.22f, ry + j * 4 * u), Size(r.width * 0.56f, 1.8f * u), CornerRadius(0.9f * u))
                if (izq) {
                    listOf(rep.top + 4 * u, rep.bottom - 12 * u).forEach { y ->
                        drawRect(c(TUBO_OSC), Offset(r.right - 4.5f * u, y), Size(4.5f * u, 8 * u))
                        drawLine(c(TUBO), Offset(r.right - 2.2f * u, y), Offset(r.right - 2.2f * u, y + 8 * u), 0.8f * u)
                    }
                    // El número de la taquilla: una placa amarilla con sus rayas (formas, no cifras).
                    val np = Rect(r.left + r.width * 0.2f, rep.top + rep.height * 0.38f, r.right - r.width * 0.3f, rep.top + rep.height * 0.38f + 9 * u)
                    drawRect(c(AMARILLO), np.topLeft, np.size)
                    for (j in 0..(k % 3)) drawRect(c(NEGRO), Offset(np.left + 2 * u + j * 3 * u, np.top + 2 * u), Size(1.6f * u, np.height - 4 * u))
                } else {
                    val pc = Offset(r.left + r.width * 0.45f, rep.center.y)
                    drawArc(c(TUBO), 180f, 180f, false, Offset(pc.x - 2.5f * u, pc.y + 2 * u), Size(5 * u, 6 * u), style = Stroke(1.2f * u))
                    drawRect(c(TUBO), Offset(pc.x - 3 * u, pc.y - 5 * u), Size(6 * u, 8 * u))
                    drawRoundRect(c(AMARILLO), Offset(pc.x - 3.5f * u, pc.y + 5 * u), Size(7 * u, 7 * u), CornerRadius(1.2f * u))
                    drawCircle(c(NEGRO), 0.8f * u, Offset(pc.x, pc.y + 8.5f * u))
                }
            }
        }
        // Las tablas: chapa doblada con sus remaches.
        d.tablas.forEach { r ->
            drawRect(c(PINTURA), r.topLeft, r.size)
            drawRect(c(PINTURA_LUZ), r.topLeft, Size(r.width, 2 * u))
            drawRect(c(PINTURA_OSC), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
            var rx = r.left + 8 * u
            while (rx < r.right) { drawCircle(c(PINTURA_LUZ), 1.1f * u, Offset(rx, r.center.y)); rx += 26 * u }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una pantallita de fósforo verde en su marco, con las líneas de barrido.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(c(TUBO_OSC), r.inflate(1.5f * u).topLeft, r.inflate(1.5f * u).size)
            drawRect(Color(0xFF061108), r.topLeft, r.size)
            var sy = r.top + 1 * u
            while (sy < r.bottom) { drawLine(FOSFORO.copy(alpha = 0.07f), Offset(r.left, sy), Offset(r.right, sy), 0.5f * u); sy += 2 * u }
            drawRect(Color.White.copy(alpha = 0.06f), r.topLeft, Size(r.width, r.height * 0.3f))
            textoEn(medidor, p.texto.uppercase(), Rect(r.left + 3 * u, r.top, r.right - 3 * u, r.bottom), letra, FOSFORO, 12f, espaciado = 0.6f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo con su rejilla baja y el piso de hormigón con su franja amarilla y la coladera.
        val b = d.base
        val piso = b.top + b.height * 0.55f
        drawRect(c(PINTURA_OSC), b.topLeft, Size(b.width, piso - b.top))
        var zx = b.left + b.width * 0.1f
        while (zx < b.right - b.width * 0.1f) {
            drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(zx, b.top + (piso - b.top) * 0.3f), Size(2 * u, (piso - b.top) * 0.4f), CornerRadius(1 * u))
            zx += 5 * u
        }
        drawRect(c(PISO), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, piso), Size(w, 2.5f * u))
        drawRect(c(AMARILLO).copy(alpha = 0.85f), Offset(0f, piso + (h - piso) * 0.55f), Size(w, (h - piso) * 0.16f))
        val dc = Offset(w * 0.78f, piso + (h - piso) * 0.32f)
        drawOval(c(TUBO_OSC), Offset(dc.x - 9 * u, dc.y - 2.5f * u), Size(18 * u, 5 * u))
        for (j in -2..2) drawLine(Color.Black.copy(alpha = 0.6f), Offset(dc.x + j * 3 * u, dc.y - 2 * u), Offset(dc.x + j * 3 * u, dc.y + 2 * u), 0.8f * u)
        // De noche, la luz roja de emergencia lo baña todo.
        if (noche) drawRect(SIRENA.copy(alpha = 0.07f), Offset.Zero, size)
    }

    /** Las tuberías del techo, la cabecera del armario con su placa y la sirena. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // Las dos tuberías, con sus bridas y las abrazaderas que bajan del techo.
        val y1 = r.top + r.height * 0.16f
        val y2 = r.top + r.height * 0.32f
        val g1 = r.height * 0.1f; val g2 = r.height * 0.055f
        listOf(0.22f, 0.78f).forEach { f -> drawLine(c(TUBO_OSC), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, y2), 1.6f * u) }
        drawRect(c(TUBO), Offset(r.left, y1 - g1 / 2), Size(r.width, g1))
        drawRect(Color.White.copy(alpha = 0.18f), Offset(r.left, y1 - g1 * 0.35f), Size(r.width, g1 * 0.18f))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, y1 + g1 * 0.2f), Size(r.width, g1 * 0.3f))
        drawRect(c(COBRE), Offset(r.left, y2 - g2 / 2), Size(r.width, g2))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(r.left, y2 - g2 * 0.35f), Size(r.width, g2 * 0.2f))
        listOf(0.08f, 0.36f, 0.64f, 0.92f).forEach { f ->
            val x = r.left + r.width * f
            drawRect(c(TUBO_OSC), Offset(x - 3 * u, y1 - g1 * 0.7f), Size(6 * u, g1 * 1.4f))
            drawRect(c(TUBO_OSC), Offset(x + r.width * 0.1f - 2 * u, y2 - g2 * 0.75f), Size(4 * u, g2 * 1.5f))
        }
        // La válvula de volante roja, en el tubo de cobre.
        val rv = r.height * 0.08f
        val vc = Offset(r.left + r.width * 0.16f, y2 + g2 * 0.5f + rv + 2 * u)
        drawLine(c(COBRE), Offset(vc.x, y2), vc, 2.5f * u)
        drawCircle(c(ROJO), rv, vc, style = Stroke(2 * u))
        for (k in 0 until 3) rotate(k * 60f, vc) { drawLine(c(ROJO), Offset(vc.x - rv, vc.y), Offset(vc.x + rv, vc.y), 1.2f * u) }
        drawCircle(c(TUBO_OSC), rv * 0.28f, vc)
        // El manómetro, en el tubo grueso.
        val rm = r.height * 0.09f
        val mc = Offset(r.left + r.width * 0.86f, y1 + g1 * 0.5f + rm + 3 * u)
        drawLine(c(TUBO), Offset(mc.x, y1), mc, 2.5f * u)
        manometro(mc, rm, c(TUBO_OSC), papel(CREMA, oscuro, noche), c(ROJO), if (noche) 0.88f else 0.32f)
        // La cabecera del armario, con sus rejillas y la placa amarilla del refugio (un triángulo y barras: formas, no letras).
        val cab = Rect(r.left, r.top + r.height * 0.56f, r.right, r.bottom)
        drawRect(c(PINTURA), cab.topLeft, cab.size)
        drawRect(c(PINTURA_LUZ), cab.topLeft, Size(cab.width, 2.5f * u))
        drawRect(c(PINTURA_OSC), Offset(cab.left, cab.bottom - 3 * u), Size(cab.width, 3 * u))
        listOf(0.06f, 0.66f).forEach { f ->
            val rej = Rect(cab.left + cab.width * f, cab.top + cab.height * 0.3f, cab.left + cab.width * (f + 0.28f), cab.top + cab.height * 0.72f)
            var x = rej.left
            while (x < rej.right) { drawRoundRect(c(PINTURA_OSC), Offset(x, rej.top), Size(2.2f * u, rej.height), CornerRadius(1.1f * u)); x += 5 * u }
        }
        val placa = Rect(cab.center.x - cab.width * 0.13f, cab.top + cab.height * 0.22f, cab.center.x + cab.width * 0.13f, cab.top + cab.height * 0.8f)
        drawRect(c(AMARILLO), placa.topLeft, placa.size)
        drawRect(c(NEGRO), placa.deflate(1.6f * u).topLeft, placa.deflate(1.6f * u).size, style = Stroke(1 * u))
        val tc = Offset(placa.left + placa.height * 0.55f, placa.center.y)
        val tr = placa.height * 0.3f
        drawPath(Path().apply { moveTo(tc.x, tc.y - tr); lineTo(tc.x + tr, tc.y + tr * 0.8f); lineTo(tc.x - tr, tc.y + tr * 0.8f); close() }, c(NEGRO))
        val barras = placa.right - placa.left - placa.height * 1.25f
        for (j in 0 until 3) drawRect(c(NEGRO), Offset(placa.left + placa.height * 1.05f, placa.top + placa.height * (0.27f + 0.2f * j)), Size(barras * (1f - 0.25f * j), placa.height * 0.1f))
        // La sirena sobre el armario: su base y su cúpula enjaulada; de día apagada, de noche roja, encendida y girando.
        val sc = Offset(r.center.x, cab.top)
        val sr = r.height * 0.13f
        if (noche) {
            mancha(Offset(sc.x, sc.y - sr * 0.6f), sr * 5f, SIRENA.copy(alpha = 0.35f))
            listOf(-1f, 1f).forEach { hacia ->
                val haz = Path().apply {
                    moveTo(sc.x, sc.y - sr * 0.6f)
                    lineTo(sc.x + hacia * r.width * 0.45f, sc.y - sr * 0.6f - r.height * 0.3f)
                    lineTo(sc.x + hacia * r.width * 0.45f, sc.y - sr * 0.6f + r.height * 0.2f); close()
                }
                drawPath(haz, SIRENA.copy(alpha = 0.12f))
            }
        }
        val domo = Rect(sc.x - sr * 0.85f, sc.y - sr * 1.15f, sc.x + sr * 0.85f, sc.y + sr * 0.55f)
        drawArc(if (noche) SIRENA else c(Color(0xFF9A3A28)), 180f, 180f, true, domo.topLeft, domo.size)
        drawArc(Color.White.copy(alpha = 0.3f), 200f, 50f, false, domo.deflate(sr * 0.2f).topLeft, domo.deflate(sr * 0.2f).size, style = Stroke(1.2f * u))
        drawArc(c(TUBO_OSC), 180f, 180f, false, domo.topLeft, domo.size, style = Stroke(1.2f * u))
        for (j in -1..1) drawLine(c(TUBO_OSC), Offset(sc.x + j * sr * 0.5f, sc.y - sr * 0.3f), Offset(sc.x + j * sr * 0.28f, sc.y - sr * 1.05f), 1 * u)
        drawRect(c(TUBO_OSC), Offset(sc.x - sr * 1.1f, sc.y - sr * 0.3f), Size(sr * 2.2f, sr * 0.3f))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = ESTUCHES[(v.semilla and 0x7fffffff) % ESTUCHES.size]
        val estuche = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El estuche abierto: la caja, la tapa abatida a la izquierda (un filo) y el fondo hundido donde va el carrete.
        drawRoundRect(estuche, Offset.Zero, size, CornerRadius(2 * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset.Zero, Size(w * 0.07f, h))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(w * 0.07f, 0f), Size(w * 0.93f, 1.4f * u))
        val fondo = Rect(w * 0.12f, h * 0.24f, w * 0.95f, h * 0.96f)
        val hueco = mezcla(estuche, Color.Black, 0.45f)
        drawRect(mezcla(estuche, Color.Black, 0.3f), fondo.topLeft, fondo.size)
        // La etiqueta de arriba: el día grande y el mes, con su filete rojo.
        val eti = Rect(w * 0.12f, h * 0.04f, w * 0.95f, h * 0.21f)
        drawRect(papel(CREMA, oscuro, v.noche), eti.topLeft, eti.size)
        drawLine(c(ROJO).copy(alpha = 0.7f), Offset(eti.left, eti.bottom - 1 * u), Offset(eti.right, eti.bottom - 1 * u), 0.8f * u)
        textoEn(medidor, v.dia, Rect(eti.left + 1 * u, eti.top, eti.left + eti.width * 0.36f, eti.bottom - 1 * u), letra, c(NEGRO), 14f)
        textoEn(medidor, v.mes.uppercase(), Rect(eti.left + eti.width * 0.38f, eti.top, eti.right - 1 * u, eti.bottom - 1 * u), letra, c(NEGRO).copy(alpha = 0.8f), 9f, minimo = 4.5f)
        // El carrete: la brida, la cinta que se ve por sus seis ventanas (más gruesa cuanto más escribiste) y el cubo.
        val cc = fondo.center
        val rc = minOf(fondo.width, fondo.height) * 0.46f
        val brida = c(BRIDAS[(v.semilla ushr 3) % BRIDAS.size])
        drawCircle(Color.Black.copy(alpha = 0.3f), rc, Offset(cc.x + 1 * u, cc.y + 1.2f * u))
        drawCircle(brida, rc, cc)
        val cubo = rc * 0.24f
        val cinta = cubo + (rc * 0.92f - cubo) * (0.2f + 0.8f * v.lleno)
        val rIn = cubo * 1.35f; val rOut = rc * 0.84f
        val rMed = (rIn + rOut) / 2
        for (k in 0 until 6) {
            val a0 = -90f + k * 60f - 16f
            drawArc(hueco, a0, 32f, false, Offset(cc.x - rMed, cc.y - rMed), Size(rMed * 2, rMed * 2), style = Stroke(rOut - rIn))
            if (cinta > rIn) {
                val tOut = minOf(cinta, rOut)
                val tMed = (rIn + tOut) / 2
                drawArc(c(CINTA), a0, 32f, false, Offset(cc.x - tMed, cc.y - tMed), Size(tMed * 2, tMed * 2), style = Stroke(tOut - rIn))
                var vuelta = rIn + 1.6f * u
                while (vuelta < tOut) { drawArc(Color.Black.copy(alpha = 0.2f), a0, 32f, false, Offset(cc.x - vuelta, cc.y - vuelta), Size(vuelta * 2, vuelta * 2), style = Stroke(0.4f * u)); vuelta += 2 * u }
            }
        }
        drawCircle(Color.Black.copy(alpha = 0.25f), rc, cc, style = Stroke(0.8f * u))
        drawCircle(Color.White.copy(alpha = 0.18f), rc * 0.9f, cc, style = Stroke(0.6f * u))
        drawCircle(c(TUBO), cubo, cc)
        drawCircle(Color.Black.copy(alpha = 0.7f), cubo * 0.42f, cc)
        for (k in 0 until 3) rotate(k * 120f, cc) { drawRect(Color.Black.copy(alpha = 0.7f), Offset(cc.x - cubo * 0.1f, cc.y - cubo * 0.62f), Size(cubo * 0.2f, cubo * 0.24f)) }
        // La punta de la cinta, que sale del carrete hacia el borde del estuche.
        drawLine(c(CINTA), Offset(cc.x + rc * 0.62f, cc.y - rc * 0.78f), Offset(fondo.right - 1 * u, fondo.top + 2 * u), 1.4f * u)
        // El sello de revisado en la esquina (un círculo con su palomita); de noche, una luna y dos estrellas.
        val so = Offset(fondo.left + 4 * u, fondo.bottom - 4 * u)
        val sr = 3 * u
        if (v.noche) {
            drawCircle(papel(CREMA, oscuro, true), sr, so)
            drawCircle(mezcla(estuche, Color.Black, 0.3f), sr * 0.8f, Offset(so.x + sr * 0.45f, so.y - sr * 0.3f))
            drawCircle(papel(CREMA, oscuro, true), 0.6f * u, Offset(so.x + sr * 1.8f, so.y + sr * 0.6f))
            drawCircle(papel(CREMA, oscuro, true), 0.5f * u, Offset(so.x + sr * 2.8f, so.y + sr * 0.2f))
        } else {
            drawCircle(c(ROJO).copy(alpha = 0.7f), sr, so, style = Stroke(0.8f * u))
            drawPath(Path().apply { moveTo(so.x - sr * 0.5f, so.y); lineTo(so.x - sr * 0.1f, so.y + sr * 0.4f); lineTo(so.x + sr * 0.55f, so.y - sr * 0.45f) }, c(ROJO).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 196.dp
    override val cajaTapa = Rect(0.09f, 0.315f, 0.68f, 0.745f)
    override val letraTapa: FontFamily = LetraExtra.vt323
    override val tamTapa = 17f
    override val lineasTapa = 3
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = NEGRO
    override fun palabrasTapa(n: Int): String = "[$n PALABRAS]"

    /**
     * El estuche del carrete del día, cerrado y de frente: la tapa de cartón con sus esquineros de metal remachados, la
     * etiqueta grande de la bitácora (la terminal, la fecha, lo escrito y las palabras entre corchetes, con su sello de
     * revisado), la placa amarilla del refugio, la ventanilla redonda por la que se ve el carrete con su cinta y el piloto
     * verde de grabado. Al abrirse, la tapa se levanta sobre su bisagra de arriba y deja ver el carrete entero y la hoja de
     * registro doblada. De noche, la luz roja de emergencia: el piloto pasa a rojo y el sello lleva una luna.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val estuche = c(ESTUCHES[(e.semilla and 0x7fffffff) % ESTUCHES.size])
        val etiqueta = papel(CREMA, oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val caja = Rect(w * 0.02f, h * 0.045f, w * 0.975f, h * 0.96f)
        // La sombra y la caja de abajo, con el labio del frente y su muesca para el pulgar.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Offset(caja.left + 3 * u, caja.top + 4 * u), caja.size, CornerRadius(4 * u))
        drawRoundRect(mezcla(estuche, Color.Black, 0.22f), caja.topLeft, caja.size, CornerRadius(3 * u))
        // El fondo hundido del estuche: la hoja de registro doblada y el carrete, que se ven al abrir (y el carrete, por la ventanilla).
        val hondo = Rect(caja.left + 4 * u, caja.top + 4 * u, caja.right - 4 * u, caja.bottom - 4 * u)
        drawRect(mezcla(estuche, Color.Black, 0.45f), hondo.topLeft, hondo.size)
        val registro = Rect(w * 0.06f, h * 0.13f, w * 0.44f, h * 0.86f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(registro.left + 2 * u, registro.top + 2 * u), registro.size)
        drawRect(etiqueta, registro.topLeft, registro.size)
        drawLine(Color.Black.copy(alpha = 0.15f), Offset(registro.left, registro.center.y), Offset(registro.right, registro.center.y), 1.2f * u)
        var ry = registro.top + 8 * u; var k = 0
        while (ry < registro.bottom - 6 * u) {
            drawLine(tinta.copy(alpha = 0.35f), Offset(registro.left + 6 * u, ry), Offset(registro.left + 6 * u + (registro.width - 14 * u) * (0.4f + 0.6f * azar(e.semilla, 100 + k)), ry), 1.4f * u)
            ry += 7 * u; k++
        }
        val cc = Offset(w * 0.72f, h * 0.5f)
        val rc = h * 0.4f
        val brida = c(BRIDAS[(e.semilla ushr 3) % BRIDAS.size])
        val hueco = mezcla(estuche, Color.Black, 0.6f)
        drawCircle(Color.Black.copy(alpha = 0.35f), rc, Offset(cc.x + 2 * u, cc.y + 2.5f * u))
        drawCircle(brida, rc, cc)
        val cubo = rc * 0.22f
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val cinta = cubo + (rc * 0.92f - cubo) * (0.2f + 0.8f * lleno)
        val rIn = cubo * 1.35f; val rOut = rc * 0.86f
        val rMed = (rIn + rOut) / 2
        for (j in 0 until 6) {
            val a0 = -90f + j * 60f - 17f
            drawArc(hueco, a0, 34f, false, Offset(cc.x - rMed, cc.y - rMed), Size(rMed * 2, rMed * 2), style = Stroke(rOut - rIn))
            val tOut = minOf(cinta, rOut)
            val tMed = (rIn + tOut) / 2
            drawArc(c(CINTA), a0, 34f, false, Offset(cc.x - tMed, cc.y - tMed), Size(tMed * 2, tMed * 2), style = Stroke(tOut - rIn))
            var vuelta = rIn + 2 * u
            while (vuelta < tOut) { drawArc(Color.Black.copy(alpha = 0.2f), a0, 34f, false, Offset(cc.x - vuelta, cc.y - vuelta), Size(vuelta * 2, vuelta * 2), style = Stroke(0.5f * u)); vuelta += 2.6f * u }
        }
        drawCircle(Color.Black.copy(alpha = 0.25f), rc, cc, style = Stroke(1 * u))
        drawCircle(Color.White.copy(alpha = 0.18f), rc * 0.92f, cc, style = Stroke(0.8f * u))
        drawCircle(c(TUBO), cubo, cc)
        drawCircle(Color.Black.copy(alpha = 0.7f), cubo * 0.42f, cc)
        for (j in 0 until 3) rotate(j * 120f, cc) { drawRect(Color.Black.copy(alpha = 0.7f), Offset(cc.x - cubo * 0.1f, cc.y - cubo * 0.62f), Size(cubo * 0.2f, cubo * 0.24f)) }
        drawLine(c(CINTA), Offset(cc.x - rc * 0.6f, cc.y - rc * 0.8f), Offset(registro.right + 6 * u, hondo.top + 3 * u), 1.6f * u)
        // La tapa, que se levanta sobre la bisagra de arriba; tiene la ventanilla redonda por donde se ve el carrete.
        val tapa = Rect(caja.left, caja.top, caja.right, h * 0.925f)
        val ventana = Offset(w * 0.845f, h * 0.5f)
        val rv = minOf(w * 0.095f, h * 0.3f)
        val levanta = cos(a * 1.6f).coerceAtLeast(0.02f)
        scale(1f, levanta, Offset(tapa.center.x, tapa.top)) {
            val forma = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(tapa)
                addOval(Rect(ventana.x - rv, ventana.y - rv, ventana.x + rv, ventana.y + rv))
            }
            drawPath(forma, Brush.verticalGradient(listOf(mezcla(estuche, Color.White, 0.1f), estuche, mezcla(estuche, Color.Black, 0.12f)), tapa.top, tapa.bottom))
            // Las fibras del cartón y los cantos.
            clipRect(tapa.left, tapa.top, w * 0.74f, tapa.bottom) {
                for (j in 0 until 60) {
                    val o = Offset(tapa.left + tapa.width * azar(e.semilla, 200 + j), tapa.top + tapa.height * azar(e.semilla, 300 + j))
                    drawLine((if (j % 2 == 0) Color.Black else Color.White).copy(alpha = 0.07f), o, Offset(o.x + 5 * u, o.y + 0.5f * u), 0.7f * u)
                }
            }
            drawRect(Color.White.copy(alpha = 0.14f), tapa.topLeft, Size(tapa.width, 1.5f * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, tapa.bottom - 1.5f * u), Size(tapa.width, 1.5f * u))
            // La bisagra de arriba.
            listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawRect(c(TUBO_OSC), Offset(tapa.left + tapa.width * f - 8 * u, tapa.top - 1 * u), Size(16 * u, 3 * u)) }
            // Los esquineros de metal remachados.
            val ke = 13 * u
            listOf(Triple(tapa.left, tapa.top, 1f to 1f), Triple(tapa.right, tapa.top, -1f to 1f), Triple(tapa.left, tapa.bottom, 1f to -1f), Triple(tapa.right, tapa.bottom, -1f to -1f)).forEach { (x, y, s) ->
                drawPath(Path().apply { moveTo(x, y); lineTo(x + s.first * ke, y); lineTo(x, y + s.second * ke); close() }, c(TUBO))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(x + s.first * ke, y), Offset(x, y + s.second * ke), 0.8f * u)
                drawCircle(c(TUBO_OSC), 1.3f * u, Offset(x + s.first * ke * 0.3f, y + s.second * ke * 0.3f))
            }
            // La etiqueta de la bitácora.
            val eti = Rect(w * 0.06f, h * 0.105f, w * 0.71f, h * 0.87f)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(eti.left + 1.5f * u, eti.top + 1.5f * u), eti.size)
            drawRect(etiqueta, eti.topLeft, eti.size)
            textoEn(medidor, if (e.noche) "REGISTRO ONÍRICO · TERMINAL 03" else "BITÁCORA PERSONAL · TERMINAL 03", Rect(eti.left + 5 * u, eti.top + 1 * u, eti.right - 5 * u, h * 0.19f), letra, tinta, 15f, minimo = 8f)
            drawLine(c(ROJO).copy(alpha = 0.8f), Offset(eti.left, h * 0.195f), Offset(eti.right, h * 0.195f), 1.4f * u)
            drawLine(c(ROJO).copy(alpha = 0.5f), Offset(eti.left, h * 0.205f), Offset(eti.right, h * 0.205f), 0.6f * u)
            textoEn(medidor, "FECHA >", Rect(eti.left + 4 * u, h * 0.21f, eti.left + eti.width * 0.22f, h * 0.295f), letra, tinta.copy(alpha = 0.6f), 13f, minimo = 7f)
            textoEn(medidor, e.fecha, Rect(eti.left + eti.width * 0.23f, h * 0.21f, eti.right - 5 * u, h * 0.295f), letra, tinta, 16f, minimo = 8f)
            var dx = eti.left + 4 * u
            while (dx < eti.right - 4 * u) { drawLine(tinta.copy(alpha = 0.35f), Offset(dx, h * 0.302f), Offset(dx + 2.5f * u, h * 0.302f), 0.8f * u); dx += 5 * u }
            // El pie: el sello de revisado (de noche, una luna) y las palabras.
            drawLine(tinta.copy(alpha = 0.4f), Offset(eti.left + 4 * u, h * 0.76f), Offset(eti.right - 4 * u, h * 0.76f), 0.8f * u)
            val so = Offset(eti.left + 12 * u, h * 0.815f)
            val sr = 6.5f * u
            if (e.noche) {
                drawCircle(c(ROJO).copy(alpha = 0.75f), sr, so)
                drawCircle(etiqueta, sr * 0.82f, Offset(so.x + sr * 0.45f, so.y - sr * 0.3f))
                drawCircle(c(ROJO).copy(alpha = 0.75f), 0.9f * u, Offset(so.x + sr * 1.8f, so.y + sr * 0.5f))
                drawCircle(c(ROJO).copy(alpha = 0.75f), 0.7f * u, Offset(so.x + sr * 2.6f, so.y - sr * 0.2f))
            } else {
                drawCircle(c(ROJO).copy(alpha = 0.75f), sr, so, style = Stroke(1.2f * u))
                drawPath(Path().apply { moveTo(so.x - sr * 0.5f, so.y); lineTo(so.x - sr * 0.1f, so.y + sr * 0.4f); lineTo(so.x + sr * 0.55f, so.y - sr * 0.45f) }, c(ROJO).copy(alpha = 0.75f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            }
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(eti.left + eti.width * 0.35f, h * 0.77f, eti.right - 5 * u, h * 0.86f), letra, tinta, 15f, minimo = 8f)
            // La placa amarilla del refugio: un triángulo y tres barras (formas, no letras).
            val pl = Rect(w * 0.765f, h * 0.1f, w * 0.925f, h * 0.27f)
            drawRect(c(AMARILLO), pl.topLeft, pl.size)
            drawRect(c(NEGRO), pl.deflate(1.6f * u).topLeft, pl.deflate(1.6f * u).size, style = Stroke(1 * u))
            val tc = Offset(pl.left + pl.width * 0.25f, pl.center.y)
            val ts = pl.height * 0.3f
            drawPath(Path().apply { moveTo(tc.x, tc.y - ts); lineTo(tc.x + ts, tc.y + ts * 0.8f); lineTo(tc.x - ts, tc.y + ts * 0.8f); close() }, c(NEGRO))
            drawPath(Path().apply { moveTo(tc.x, tc.y - ts * 0.35f); lineTo(tc.x + ts * 0.45f, tc.y + ts * 0.5f); lineTo(tc.x - ts * 0.45f, tc.y + ts * 0.5f); close() }, c(AMARILLO))
            for (j in 0 until 3) drawRect(c(NEGRO), Offset(pl.left + pl.width * 0.5f, pl.top + pl.height * (0.26f + 0.18f * j)), Size(pl.width * (0.38f - 0.08f * j), pl.height * 0.1f))
            // La ventanilla: su aro de plástico y el brillo.
            drawCircle(Color.White.copy(alpha = 0.06f), rv, ventana)
            drawCircle(c(TUBO_OSC), rv, ventana, style = Stroke(2.5f * u))
            drawCircle(c(TUBO), rv + 2 * u, ventana, style = Stroke(1 * u))
            drawArc(Color.White.copy(alpha = 0.45f), 200f, 60f, false, Offset(ventana.x - rv * 0.8f, ventana.y - rv * 0.8f), Size(rv * 1.6f, rv * 1.6f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            // El piloto de grabado (verde; de noche, la luz roja de emergencia) y la rejilla.
            val piloto = Offset(w * 0.79f, h * 0.8f)
            val luz = if (e.noche) SIRENA else FOSFORO
            drawCircle(luz.copy(alpha = 0.18f), 7 * u, piloto)
            drawCircle(luz.copy(alpha = 0.35f), 4.5f * u, piloto)
            drawCircle(luz, 2.6f * u, piloto)
            drawCircle(Color.White.copy(alpha = 0.7f), 0.9f * u, Offset(piloto.x - 0.8f * u, piloto.y - 0.8f * u))
            for (j in 0 until 4) drawRoundRect(Color.Black.copy(alpha = 0.4f), Offset(w * 0.83f, h * (0.76f + 0.03f * j)), Size(w * 0.09f, 1.6f * u), CornerRadius(0.8f * u))
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
        // La sombra que la tapa echa al levantarse.
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * sin(a * PI.toFloat())), Offset(tapa.left, tapa.top + tapa.height * levanta), Size(tapa.width, 8 * u))
    }
}

/**
 * Orden: la estantería de acero gótico-industrial de la orden. El fondo de cada repisa es una arquería ciega de arcos
 * apuntados entre parteluces de acero; los costados son pilares remachados con su ventanita apuntada y un tubo de cobre que
 * baja por fuera con sus abrazaderas de latón. Arriba, detrás, corren dos tuberías con una válvula que suelta vapor; en el
 * centro se alza un frontón apuntado con el escudo carmesí del engrane y la espada en latón, y a los lados dos pináculos con
 * sus crestas y una lámpara de alerta enjaulada; debajo, la cornisa con su arquería menuda. Las tablas llevan filo de latón,
 * remaches y ménsulas, y cada mes va en una placa ochavada de esmalte oscuro con filo de latón. Abajo, el zócalo de arcos y
 * la rejilla del piso. Cada entrada es un tomo blindado: lomo de cuero con cantoneras de acero, dos bandas, la placa de latón
 * con el día, la cadena que lo ata y el sello de lacre con sus cintas de pergamino (más cintas cuanto más escribiste); de
 * noche el sello lleva una luna. De noche se encienden las luces de alerta rojas. Distinto del Ideario, que es la bóveda de
 * gavetas blindadas con su estandarte.
 * La tapa del diario es el tomo blindado de frente, con su placa de latón y pergamino, la cadena y el sello; al abrirse,
 * gira sobre el lomo.
 */
object EstanteOrden : DisenoBiblioteca {
    private val ACERO = Color(0xFF3C424A)
    private val ACERO_LUZ = Color(0xFF6A727C)
    private val ACERO_OSC = Color(0xFF22262B)
    private val HIERRO = Color(0xFF2A2D31)
    private val HOJA = Color(0xFFD6DCE2)
    private val LATON = Color(0xFFB8933E)
    private val LATON_LUZ = Color(0xFFE0C070)
    private val COBRE = Color(0xFF9A5B34)
    private val CARMESI = Color(0xFF8E1622)
    private val LACRE = Color(0xFFA3161F)
    private val PERGAMINO = Color(0xFFE6D8B0)
    private val NEGRO = Color(0xFF101214)
    private val ALERTA = Color(0xFFFF2B1E)
    /** Los cueros de los tomos: burdeos, pizarra, oliva oscuro, cuero curtido y negro. */
    private val CUEROS = listOf(Color(0xFF4A1E1E), Color(0xFF2B3440), Color(0xFF3E3A2C), Color(0xFF5A3420), Color(0xFF262626))
    private val NOCHE = Color(0xFF2A0709)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo las luces de alerta. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF09090B), 0.35f)
        if (noche) r = mezcla(r, NOCHE, 0.42f)
        return r
    }

    /** El latón de las placas con texto: se hunde menos, para que el día se lea en oscuro y de noche. */
    private fun placa(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF09090B), 0.16f)
        if (noche) r = mezcla(r, NOCHE, 0.22f)
        return r
    }

    /** Un escudo: arriba recto y abajo en punta. */
    private fun escudo(r: Rect): Path = Path().apply {
        moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + r.height * 0.45f)
        cubicTo(r.right, r.top + r.height * 0.75f, r.center.x + r.width * 0.2f, r.top + r.height * 0.9f, r.center.x, r.bottom)
        cubicTo(r.center.x - r.width * 0.2f, r.top + r.height * 0.9f, r.left, r.top + r.height * 0.75f, r.left, r.top + r.height * 0.45f)
        close()
    }

    override val corona = 0.32f
    override val base = 0.1f
    override val lado = 0.07f
    override val altoRepisa: Dp = 116.dp
    override val tabla: Dp = 18.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.estarcido

    override fun ancho(palabras: Int): Dp = (26 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.82f + 0.12f * azar(semilla, 5)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de placas de acero y, en cada repisa, la arquería ciega de arcos apuntados entre sus parteluces.
        drawRect(c(ACERO_OSC), Offset.Zero, size)
        d.repisas.forEach { r ->
            val n = maxOf(1, (r.width / (44 * u)).toInt())
            val paso = r.width / n
            for (k in 0 until n) {
                val ar = Rect(r.left + paso * k + 4 * u, r.top + 14 * u, r.left + paso * (k + 1) - 4 * u, r.bottom)
                val ap = arcoApuntado(ar, ar.width * 0.9f)
                drawPath(ap, c(HIERRO))
                drawPath(ap, c(ACERO), style = Stroke(1.6f * u))
                if (k > 0) drawLine(c(ACERO), Offset(r.left + paso * k, r.top), Offset(r.left + paso * k, r.bottom), 2.5f * u)
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.45f else 0.32f), r.topLeft, Size(r.width, 10 * u))
        }
        // Los pilares de los costados: el contrafuerte, la ventanita apuntada, los remaches y el tubo de cobre por fuera.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(ACERO), r.topLeft, r.size)
            val col = Rect(r.left + r.width * 0.2f, r.top, r.right - r.width * 0.2f, r.bottom)
            drawRect(c(ACERO_LUZ).copy(alpha = 0.5f), col.topLeft, Size(col.width * 0.15f, col.height))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(col.right - col.width * 0.15f, col.top), Size(col.width * 0.15f, col.height))
            val tx = if (izq) r.left + r.width * 0.12f else r.right - r.width * 0.12f
            drawLine(c(COBRE), Offset(tx, r.top), Offset(tx, r.bottom), r.width * 0.14f)
            drawLine(Color.White.copy(alpha = 0.15f), Offset(tx - r.width * 0.03f, r.top), Offset(tx - r.width * 0.03f, r.bottom), 0.8f * u)
            d.repisas.forEach { rep ->
                drawRect(c(LATON), Offset(tx - r.width * 0.1f, rep.center.y - 2 * u), Size(r.width * 0.2f, 4 * u))
                listOf(rep.top + 8 * u, rep.bottom - 8 * u).forEach { y -> drawCircle(c(ACERO_LUZ), 1.3f * u, Offset(r.center.x, y)) }
                val va = Rect(r.center.x - r.width * 0.16f, rep.top + rep.height * 0.3f, r.center.x + r.width * 0.16f, rep.top + rep.height * 0.7f)
                drawPath(arcoApuntado(va, va.width * 0.9f), c(HIERRO))
                // De noche, una luz de alerta en cada nivel del pilar.
                if (noche) {
                    val lo = Offset(r.center.x, rep.top + rep.height * 0.2f)
                    drawCircle(ALERTA.copy(alpha = 0.22f), 5 * u, lo)
                    drawCircle(ALERTA, 1.8f * u, lo)
                }
            }
        }
        // Las tablas: acero con filo de latón, una fila de remaches y las ménsulas debajo.
        d.tablas.forEach { r ->
            drawRect(c(ACERO), r.topLeft, r.size)
            drawRect(c(LATON), r.topLeft, Size(r.width, 2 * u))
            drawRect(c(ACERO_OSC), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
            var x = r.left + 6 * u
            while (x < r.right - 3 * u) { drawCircle(c(ACERO_LUZ), 1 * u, Offset(x, r.top + r.height * 0.4f)); x += 12 * u }
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 4 * u))
            listOf(r.left to 1f, r.right to -1f).forEach { (x0, s) ->
                drawPath(Path().apply { moveTo(x0, r.bottom); lineTo(x0 + s * 10 * u, r.bottom); lineTo(x0, r.bottom + 10 * u); close() }, c(ACERO))
            }
        }
        // El rótulo de cada mes: una placa ochavada de esmalte oscuro con filo de latón y dos remaches.
        d.placas.forEach { p ->
            val r = p.r
            val ch = r.height * 0.3f
            val forma = Path().apply {
                moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
                lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
            }
            drawPath(forma, c(HIERRO))
            drawPath(forma, c(LATON), style = Stroke(1.4f * u))
            listOf(r.left + ch * 1.6f, r.right - ch * 1.6f).forEach { x -> drawCircle(c(LATON), 1.2f * u, Offset(x, r.center.y)) }
            textoEn(medidor, p.texto.uppercase(), Rect(r.left + ch * 2.6f, r.top, r.right - ch * 2.6f, r.bottom), letra, placa(LATON_LUZ, oscuro, noche), 11f, espaciado = 1f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo con su arquería y la rejilla del piso.
        val b = d.base
        val piso = b.top + b.height * 0.6f
        drawRect(c(ACERO), b.topLeft, Size(b.width, piso - b.top))
        drawRect(c(LATON), b.topLeft, Size(b.width, 2 * u))
        val bw = 20 * u
        var bx = b.left + 6 * u
        while (bx + bw < b.right) {
            drawPath(arcoApuntado(Rect(bx, b.top + 5 * u, bx + bw - 6 * u, piso - 2 * u), (bw - 6 * u) * 0.8f), c(ACERO_OSC))
            bx += bw
        }
        drawRect(c(HIERRO), Offset(0f, piso), Size(w, h - piso))
        clipRect(0f, piso, w, h) {
            val alto = h - piso
            var gx = -alto
            while (gx < w + alto) {
                drawLine(c(ACERO).copy(alpha = 0.6f), Offset(gx, piso), Offset(gx + alto, h), 1 * u)
                drawLine(c(ACERO).copy(alpha = 0.6f), Offset(gx, h), Offset(gx + alto, piso), 1 * u)
                gx += 7 * u
            }
        }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, piso), Size(w, 3 * u))
        // De noche, el resplandor rojo de las alertas.
        if (noche) drawRect(ALERTA.copy(alpha = 0.05f), Offset.Zero, size)
    }

    /** Las tuberías de atrás, los pináculos con sus lámparas, el frontón con el escudo y la cornisa. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // Las tuberías que corren por detrás, con sus bridas de latón y una válvula que suelta vapor.
        val ty = r.top + r.height * 0.42f
        val gt = r.height * 0.07f
        drawRect(c(COBRE), Offset(r.left, ty - gt / 2), Size(r.width, gt))
        drawRect(Color.White.copy(alpha = 0.16f), Offset(r.left, ty - gt * 0.35f), Size(r.width, gt * 0.2f))
        val ty2 = r.top + r.height * 0.6f
        drawRect(c(ACERO), Offset(r.left, ty2 - gt * 0.4f), Size(r.width, gt * 0.8f))
        listOf(0.06f, 0.3f, 0.7f, 0.94f).forEach { f -> drawRect(c(LATON), Offset(r.left + r.width * f - 2.5f * u, ty - gt * 0.8f), Size(5 * u, gt * 1.6f)) }
        val vx = r.left + r.width * 0.25f
        drawRect(c(ACERO_OSC), Offset(vx - 2 * u, ty - gt / 2 - 6 * u), Size(4 * u, 6 * u))
        for (k in 0 until 4) drawCircle(Color.White.copy(alpha = 0.1f - 0.018f * k), (4 + 3 * k) * u, Offset(vx + k * 2 * u, ty - gt / 2 - 9 * u - k * 6 * u))
        // La cornisa, con su arquería menuda.
        val cornisa = Rect(r.left, r.bottom - r.height * 0.16f, r.right, r.bottom)
        // Los pináculos sobre los costados, con sus crestas y la lámpara de alerta enjaulada (de noche, encendida).
        val lw = r.width * lado
        listOf(r.left + lw / 2, r.right - lw / 2).forEach { px ->
            val punta = r.top + r.height * 0.08f
            val hombro = r.top + r.height * 0.38f
            val pin = Path().apply {
                moveTo(px - lw * 0.4f, cornisa.top); lineTo(px - lw * 0.4f, hombro); lineTo(px, punta)
                lineTo(px + lw * 0.4f, hombro); lineTo(px + lw * 0.4f, cornisa.top); close()
            }
            drawPath(pin, c(ACERO))
            drawLine(c(ACERO_LUZ), Offset(px - lw * 0.4f, cornisa.top), Offset(px - lw * 0.4f, hombro), 1 * u)
            for (k in 0 until 3) {
                val yk = r.top + r.height * (0.14f + 0.08f * k)
                val dx = (yk - punta) / (hombro - punta) * lw * 0.4f
                drawPath(Path().apply { moveTo(px - dx, yk); lineTo(px - dx - 3 * u, yk - 2 * u); lineTo(px - dx, yk + 2 * u); close() }, c(ACERO_LUZ))
                drawPath(Path().apply { moveTo(px + dx, yk); lineTo(px + dx + 3 * u, yk - 2 * u); lineTo(px + dx, yk + 2 * u); close() }, c(ACERO_LUZ))
            }
            val lc = Offset(px, r.top + r.height * 0.52f)
            val lr = lw * 0.3f
            if (noche) mancha(lc, lr * 6f, ALERTA.copy(alpha = 0.45f))
            drawCircle(if (noche) ALERTA else c(Color(0xFF6A1A16)), lr, lc)
            drawCircle(c(HIERRO), lr, lc, style = Stroke(1.2f * u))
            drawLine(c(HIERRO), Offset(lc.x - lr, lc.y), Offset(lc.x + lr, lc.y), 0.9f * u)
            drawLine(c(HIERRO), Offset(lc.x, lc.y - lr), Offset(lc.x, lc.y + lr), 0.9f * u)
        }
        // El frontón apuntado del centro, con el escudo carmesí, el engrane de latón y la espada de punta abajo.
        val fr = Rect(r.center.x - r.width * 0.2f, r.top + r.height * 0.06f, r.center.x + r.width * 0.2f, cornisa.top + 1 * u)
        val arco = arcoApuntado(fr, fr.height * 0.55f)
        drawPath(arco, c(ACERO))
        val dentro = Rect(fr.left + 5 * u, fr.top + 7 * u, fr.right - 5 * u, fr.bottom)
        drawPath(arcoApuntado(dentro, dentro.height * 0.55f), c(HIERRO))
        drawPath(arco, c(LATON), style = Stroke(1.4f * u))
        val ec = Offset(fr.center.x, dentro.top + dentro.height * 0.58f)
        val er = minOf(dentro.width, dentro.height) * 0.3f
        val esc = escudo(Rect(ec.x - er, ec.y - er * 1.05f, ec.x + er, ec.y + er * 1.25f))
        drawPath(esc, c(CARMESI))
        drawPath(esc, c(LATON), style = Stroke(1.6f * u))
        drawPath(engrane(ec, er * 0.62f, 10), c(LATON))
        drawCircle(c(CARMESI), er * 0.32f, ec)
        val hoja = Path().apply {
            moveTo(ec.x - er * 0.08f, ec.y - er * 0.75f); lineTo(ec.x + er * 0.08f, ec.y - er * 0.75f)
            lineTo(ec.x + er * 0.08f, ec.y + er * 0.95f); lineTo(ec.x, ec.y + er * 1.15f); lineTo(ec.x - er * 0.08f, ec.y + er * 0.95f); close()
        }
        drawPath(hoja, c(HOJA))
        drawLine(c(ACERO_OSC), Offset(ec.x, ec.y - er * 0.7f), Offset(ec.x, ec.y + er), 0.6f * u)
        drawRect(c(LATON_LUZ), Offset(ec.x - er * 0.42f, ec.y - er * 0.85f), Size(er * 0.84f, er * 0.12f))
        drawRect(c(HIERRO), Offset(ec.x - er * 0.06f, ec.y - er * 1.15f), Size(er * 0.12f, er * 0.3f))
        drawCircle(c(LATON_LUZ), er * 0.09f, Offset(ec.x, ec.y - er * 1.2f))
        // La cornisa encima de todo, con su filo de latón y sus arquitos.
        drawRect(c(ACERO), cornisa.topLeft, cornisa.size)
        drawRect(c(LATON), cornisa.topLeft, Size(cornisa.width, 2 * u))
        drawRect(c(ACERO_OSC), Offset(cornisa.left, cornisa.bottom - 3 * u), Size(cornisa.width, 3 * u))
        val aw = 14 * u
        var ax = cornisa.left + 3 * u
        while (ax + aw < cornisa.right) {
            drawPath(arcoApuntado(Rect(ax, cornisa.top + 4 * u, ax + aw - 3 * u, cornisa.bottom - 4 * u), (aw - 3 * u) * 0.7f), c(ACERO_OSC))
            ax += aw
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = CUEROS[(v.semilla and 0x7fffffff) % CUEROS.size]
        val cuero = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El lomo de cuero, con la luz a la izquierda y la sombra a la derecha.
        drawRect(cuero, Offset.Zero, size)
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.1f, 0f), Size(w * 0.14f, h))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.84f, 0f), Size(w * 0.16f, h))
        // Las tapas de acero: cantoneras arriba y abajo, remachadas.
        val tapa = h * 0.085f
        listOf(true, false).forEach { arriba ->
            val y0 = if (arriba) 0f else h - tapa
            drawRect(c(ACERO_LUZ), Offset(0f, y0), Size(w, tapa))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, if (arriba) tapa - 1 * u else y0), Size(w, 1 * u))
            listOf(w * 0.22f, w * 0.78f).forEach { x -> drawCircle(c(ACERO_OSC), 1.1f * u, Offset(x, y0 + tapa / 2)) }
        }
        // Dos bandas de acero que abrazan el lomo.
        listOf(0.31f, 0.635f).forEach { f ->
            drawRect(c(ACERO), Offset(0f, h * f), Size(w, h * 0.025f))
            drawRect(Color.White.copy(alpha = 0.18f), Offset(0f, h * f), Size(w, 0.8f * u))
        }
        // La placa de latón con el día, atornillada en sus cuatro esquinas.
        val pl = Rect(w * 0.14f, h * 0.11f, w * 0.86f, h * 0.29f)
        drawRect(placa(LATON_LUZ, oscuro, v.noche), pl.topLeft, pl.size)
        drawRect(c(ACERO_OSC), pl.topLeft, pl.size, style = Stroke(0.8f * u))
        listOf(pl.left + 2 * u, pl.right - 2 * u).forEach { x ->
            drawCircle(c(ACERO_OSC), 0.8f * u, Offset(x, pl.top + 2 * u))
            drawCircle(c(ACERO_OSC), 0.8f * u, Offset(x, pl.bottom - 2 * u))
        }
        textoEn(medidor, v.dia, pl.deflate(2.5f * u), letra, c(NEGRO), 14f)
        // El mes estarcido a lo largo del lomo.
        textoEn(medidor, v.mes.uppercase(), Rect(w * 0.2f, h * 0.34f, w * 0.8f, h * 0.625f), letra, placa(PERGAMINO, oscuro, v.noche), 9f, vertical = true, espaciado = 0.8f, minimo = 4.5f)
        // La cadena que lo ata, eslabón por eslabón.
        val cy = h * 0.69f
        clipRect(0f, 0f, w, h) {
            var x = -1 * u; var k = 0
            while (x < w) {
                if (k % 2 == 0) drawOval(c(ACERO_LUZ), Offset(x, cy - 1.8f * u), Size(5 * u, 3.6f * u), style = Stroke(1 * u))
                else drawLine(c(ACERO_LUZ), Offset(x - 0.5f * u, cy), Offset(x + 4.5f * u, cy), 1.2f * u)
                x += 4 * u; k++
            }
        }
        // El sello de lacre con sus cintas de pergamino (más cintas cuanto más escribiste): de día lleva el engrane; de noche, una luna.
        val sc = Offset(w * 0.5f, h * 0.77f)
        val sr = minOf(w * 0.3f, 7 * u)
        val cintas = 1 + (v.lleno * 2.4f).toInt()
        for (j in 0 until cintas) {
            val x0 = sc.x + (j - (cintas - 1) / 2f) * sr * 0.7f
            val tira = Path().apply {
                moveTo(x0 - sr * 0.25f, sc.y); lineTo(x0 + sr * 0.25f, sc.y)
                lineTo(x0 + sr * 0.25f + j * 0.6f * u, h * 0.905f); lineTo(x0 + j * 0.6f * u, h * 0.885f); lineTo(x0 - sr * 0.25f + j * 0.6f * u, h * 0.905f); close()
            }
            drawPath(tira, placa(PERGAMINO, oscuro, v.noche))
            drawLine(c(NEGRO).copy(alpha = 0.35f), Offset(x0 - sr * 0.12f, sc.y + sr * 1.2f), Offset(x0 + sr * 0.12f, sc.y + sr * 1.2f), 0.5f * u)
        }
        val lacre = Path().apply {
            for (k in 0 until 12) {
                val a = k / 12f * 2 * PI.toFloat()
                val rr = sr * (0.88f + 0.12f * azar(v.semilla, 60 + k))
                val p = Offset(sc.x + rr * cos(a), sc.y + rr * sin(a))
                if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(lacre, c(LACRE))
        drawCircle(Color.Black.copy(alpha = 0.22f), sr * 0.68f, sc, style = Stroke(0.8f * u))
        if (v.noche) {
            drawCircle(placa(PERGAMINO, oscuro, true), sr * 0.42f, sc)
            drawCircle(c(LACRE), sr * 0.36f, Offset(sc.x + sr * 0.2f, sc.y - sr * 0.12f))
        } else {
            drawPath(engrane(sc, sr * 0.45f, 8), Color.Black.copy(alpha = 0.3f))
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.225f, 0.285f, 0.775f, 0.65f)
    override val letraTapa: FontFamily = LetraExtra.estarcido
    override val tamTapa = 12.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = Color(0xFF1A1410)
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /**
     * El tomo blindado del día, cerrado y de frente: la tapa de cuero con sus franjas de acero remachadas arriba y abajo, las
     * dos correas de acero, las cantoneras, el escudo carmesí con el engrane y la espada, la placa de latón con su pergamino
     * (la fecha, lo escrito y las palabras), la cadena que lo cruza y el sello de lacre con sus cintas. Al abrirse, la tapa
     * gira sobre el lomo y deja ver las hojas de pergamino a dos columnas con su capitular. De noche se encienden las luces de
     * alerta rojas en el acero y el sello lleva una luna.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val cuero = c(CUEROS[(e.semilla and 0x7fffffff) % CUEROS.size])
        val pergamino = placa(PERGAMINO, oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val tomo = Rect(w * 0.015f, h * 0.025f, w * 0.955f, h * 0.935f)
        val lomo = Rect(tomo.left, tomo.top, tomo.left + w * 0.07f, tomo.bottom)
        val cara = Rect(lomo.right, tomo.top, tomo.right, tomo.bottom)
        // La sombra y el canto de las hojas, a la derecha y abajo.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Offset(tomo.left + 4 * u, tomo.top + 5 * u), Size(tomo.width + 2 * u, tomo.height + 2 * u))
        val hojas = Rect(tomo.left + w * 0.03f, tomo.top + h * 0.03f, w * 0.975f, h * 0.965f)
        drawRect(mezcla(pergamino, Color.Black, 0.12f), hojas.topLeft, hojas.size)
        for (j in 1..5) {
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(tomo.right + j * 0.7f * u, hojas.top + 2 * u), Offset(tomo.right + j * 0.7f * u, hojas.bottom - 2 * u), 0.5f * u)
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(hojas.left + 2 * u, tomo.bottom + j * 1f * u), Offset(hojas.right - 2 * u, tomo.bottom + j * 1f * u), 0.5f * u)
        }
        // Dentro (se ve al abrir): la hoja de pergamino a dos columnas, con su capitular carmesí.
        if (a > 0.01f) {
            drawRect(pergamino, cara.topLeft, cara.size)
            drawRect(Color.Black.copy(alpha = 0.18f), cara.topLeft, Size(10 * u, cara.height))
            val m = Rect(cara.left + w * 0.06f, cara.top + h * 0.1f, cara.right - w * 0.04f, cara.bottom - h * 0.1f)
            val col = (m.width - w * 0.04f) / 2
            drawRect(c(CARMESI), m.topLeft, Size(14 * u, 14 * u))
            drawRect(c(LATON), m.topLeft, Size(14 * u, 14 * u), style = Stroke(1 * u))
            for (cn in 0..1) {
                val x0 = m.left + cn * (col + w * 0.04f)
                var y = m.top + 4 * u; var k = 0
                while (y < m.bottom) {
                    val x1 = if (cn == 0 && y < m.top + 16 * u) x0 + 18 * u else x0
                    drawLine(tinta.copy(alpha = 0.4f), Offset(x1, y), Offset(x0 + col * (0.75f + 0.25f * azar(e.semilla, 100 + k + cn * 50)), y), 1.2f * u)
                    y += 7 * u; k++
                }
            }
        }
        // La tapa de cuero, que gira sobre el lomo.
        val giro = cos(a * 1.6f).coerceAtLeast(0.02f)
        scale(giro, 1f, Offset(cara.left, cara.center.y)) {
            drawRect(Brush.verticalGradient(listOf(mezcla(cuero, Color.White, 0.08f), cuero, mezcla(cuero, Color.Black, 0.25f)), cara.top, cara.bottom), cara.topLeft, cara.size)
            // El grano del cuero.
            for (k in 0 until 90) {
                val o = Offset(cara.left + cara.width * azar(e.semilla, 200 + k), cara.top + cara.height * azar(e.semilla, 300 + k))
                drawCircle((if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.06f), (0.6f + 1.2f * azar(e.semilla, 400 + k)) * u, o)
            }
            // Las franjas de acero de arriba y de abajo, con su fila de remaches.
            val franja = h * 0.06f
            listOf(cara.top, cara.bottom - franja).forEach { y0 ->
                drawRect(c(ACERO_LUZ), Offset(cara.left, y0), Size(cara.width, franja))
                drawRect(Color.White.copy(alpha = 0.18f), Offset(cara.left, y0), Size(cara.width, 1 * u))
                drawRect(Color.Black.copy(alpha = 0.35f), Offset(cara.left, y0 + franja - 1.2f * u), Size(cara.width, 1.2f * u))
                var x = cara.left + 10 * u
                while (x < cara.right - 6 * u) { drawCircle(c(ACERO_OSC), 1.4f * u, Offset(x, y0 + franja / 2)); drawCircle(Color.White.copy(alpha = 0.3f), 0.5f * u, Offset(x - 0.5f * u, y0 + franja / 2 - 0.5f * u)); x += 14 * u }
                if (e.noche) drawRect(ALERTA.copy(alpha = 0.12f), Offset(cara.left, y0), Size(cara.width, franja))
            }
            // Las dos correas de acero a lo alto, remachadas.
            listOf(cara.left + cara.width * 0.06f, cara.right - cara.width * 0.09f).forEach { x ->
                drawRect(c(ACERO), Offset(x, cara.top), Size(w * 0.03f, cara.height))
                drawRect(Color.White.copy(alpha = 0.15f), Offset(x, cara.top), Size(1 * u, cara.height))
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + w * 0.03f - 1 * u, cara.top), Size(1 * u, cara.height))
                listOf(0.22f, 0.5f, 0.78f).forEach { f -> drawCircle(c(ACERO_OSC), 1.5f * u, Offset(x + w * 0.015f, cara.top + cara.height * f)) }
            }
            // Las cantoneras de las esquinas.
            val k0 = 22 * u
            listOf(Triple(cara.left, cara.top, 1f to 1f), Triple(cara.right, cara.top, -1f to 1f), Triple(cara.left, cara.bottom, 1f to -1f), Triple(cara.right, cara.bottom, -1f to -1f)).forEach { (x, y, s) ->
                drawPath(Path().apply { moveTo(x, y); lineTo(x + s.first * k0, y); lineTo(x, y + s.second * k0); close() }, c(ACERO_LUZ))
                drawLine(c(ACERO_OSC), Offset(x + s.first * k0, y), Offset(x, y + s.second * k0), 1.2f * u)
                drawCircle(c(ACERO_OSC), 1.6f * u, Offset(x + s.first * k0 * 0.3f, y + s.second * k0 * 0.3f))
            }
            // La placa de latón con el pergamino: la fecha, el filete con su engrane, lo escrito y las palabras.
            val marco = Rect(w * 0.19f, h * 0.13f, w * 0.81f, h * 0.8f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(marco.left + 1.5f * u, marco.top + 2 * u), marco.size)
            drawRect(placa(LATON, oscuro, e.noche), marco.topLeft, marco.size)
            drawRect(placa(LATON_LUZ, oscuro, e.noche), marco.topLeft, Size(marco.width, 1.2f * u))
            val ins = marco.deflate(4.5f * u)
            drawRect(pergamino, ins.topLeft, ins.size)
            drawRect(c(COBRE).copy(alpha = 0.6f), ins.topLeft, ins.size, style = Stroke(0.8f * u))
            listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), marco.bottomRight).forEach { o ->
                val p = Offset(o.x + (if (o.x < marco.center.x) 2.3f else -2.3f) * u, o.y + (if (o.y < marco.center.y) 2.3f else -2.3f) * u)
                drawCircle(c(ACERO_OSC), 1.4f * u, p)
                drawLine(placa(LATON_LUZ, oscuro, e.noche), Offset(p.x - 1 * u, p.y - 1 * u), Offset(p.x + 1 * u, p.y + 1 * u), 0.5f * u)
            }
            textoEn(medidor, e.fecha, Rect(ins.left + 6 * u, h * 0.165f, ins.right - 6 * u, h * 0.25f), letra, tinta, 12.5f, minimo = 7f)
            val fy = h * 0.262f
            listOf(-1f, 1f).forEach { s ->
                drawLine(tinta.copy(alpha = 0.55f), Offset(marco.center.x + s * 7 * u, fy), Offset(marco.center.x + s * ins.width * 0.4f, fy), 0.8f * u)
                drawLine(tinta.copy(alpha = 0.3f), Offset(marco.center.x + s * 7 * u, fy + 2.2f * u), Offset(marco.center.x + s * ins.width * 0.32f, fy + 2.2f * u), 0.5f * u)
            }
            drawPath(engrane(Offset(marco.center.x, fy + 1 * u), 4.5f * u, 8), c(LACRE))
            drawLine(tinta.copy(alpha = 0.4f), Offset(ins.left + ins.width * 0.15f, h * 0.665f), Offset(ins.right - ins.width * 0.15f, h * 0.665f), 0.7f * u)
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(ins.left + 6 * u, h * 0.675f, ins.right - 6 * u, h * 0.755f), letra, tinta.copy(alpha = 0.75f), 10f, minimo = 6f)
            // El escudo carmesí sobre la franja de arriba, con su engrane y la espada en latón.
            val esc = Rect(marco.center.x - w * 0.034f, h * 0.018f, marco.center.x + w * 0.034f, h * 0.152f)
            drawPath(escudo(esc.translate(1 * u, 1.5f * u)), Color.Black.copy(alpha = 0.35f))
            drawPath(escudo(esc), c(CARMESI))
            drawPath(escudo(esc), placa(LATON, oscuro, e.noche), style = Stroke(1.4f * u))
            val ec = Offset(esc.center.x, esc.top + esc.height * 0.42f)
            drawPath(engrane(ec, esc.width * 0.3f, 8), placa(LATON, oscuro, e.noche))
            drawCircle(c(CARMESI), esc.width * 0.12f, ec)
            drawLine(placa(LATON_LUZ, oscuro, e.noche), Offset(ec.x, esc.top + esc.height * 0.1f), Offset(ec.x, esc.bottom - esc.height * 0.14f), 1.3f * u)
            drawLine(placa(LATON_LUZ, oscuro, e.noche), Offset(ec.x - esc.width * 0.18f, esc.top + esc.height * 0.68f), Offset(ec.x + esc.width * 0.18f, esc.top + esc.height * 0.68f), 1.3f * u)
            // De noche, dos luces de alerta encendidas en la franja de arriba.
            if (e.noche) listOf(0.28f, 0.72f).forEach { f ->
                val o = Offset(cara.left + cara.width * f, cara.top + franja / 2)
                drawCircle(ALERTA.copy(alpha = 0.18f), 7 * u, o); drawCircle(ALERTA.copy(alpha = 0.4f), 4 * u, o); drawCircle(ALERTA, 2.2f * u, o)
            }
            // La cadena que lo cruza, eslabón por eslabón.
            val cy = h * 0.84f
            var x = cara.left; var k = 0
            while (x < cara.right) {
                if (k % 2 == 0) drawOval(c(ACERO_LUZ), Offset(x, cy - 2.8f * u), Size(8 * u, 5.6f * u), style = Stroke(1.6f * u))
                else { drawLine(c(ACERO_LUZ), Offset(x - 0.5f * u, cy), Offset(x + 7.5f * u, cy), 2 * u); drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, cy + 1.2f * u), Offset(x + 7 * u, cy + 1.2f * u), 0.6f * u) }
                x += 6.5f * u; k++
            }
            // El sello de lacre con sus cintas de pergamino (más cuanto más escribiste); de noche, una luna.
            val sc = Offset(cara.right - cara.width * 0.17f, cy)
            val sr = 11 * u
            val cintas = 1 + (e.palabras / 800f * 2.4f).toInt().coerceIn(0, 2)
            for (j in 0 until cintas) {
                val x0 = sc.x + (j - (cintas - 1) / 2f) * sr * 0.8f
                drawPath(Path().apply {
                    moveTo(x0 - sr * 0.28f, sc.y); lineTo(x0 + sr * 0.28f, sc.y)
                    lineTo(x0 + sr * 0.3f + j * 1.5f * u, h * 0.995f); lineTo(x0 + j * 1.5f * u, h * 0.975f); lineTo(x0 - sr * 0.26f + j * 1.5f * u, h * 0.995f); close()
                }, pergamino)
            }
            val lacre = Path().apply {
                for (q in 0 until 14) {
                    val ang = q / 14f * 2 * PI.toFloat()
                    val rr = sr * (0.88f + 0.12f * azar(e.semilla, 60 + q))
                    if (q == 0) moveTo(sc.x + rr * cos(ang), sc.y + rr * sin(ang)) else lineTo(sc.x + rr * cos(ang), sc.y + rr * sin(ang))
                }
                close()
            }
            drawPath(lacre, c(LACRE))
            drawCircle(Color.Black.copy(alpha = 0.25f), sr * 0.68f, sc, style = Stroke(1 * u))
            drawArc(Color.White.copy(alpha = 0.25f), 200f, 70f, false, Offset(sc.x - sr * 0.8f, sc.y - sr * 0.8f), Size(sr * 1.6f, sr * 1.6f), style = Stroke(1 * u))
            if (e.noche) {
                drawCircle(pergamino, sr * 0.42f, sc)
                drawCircle(c(LACRE), sr * 0.36f, Offset(sc.x + sr * 0.2f, sc.y - sr * 0.12f))
            } else drawPath(engrane(sc, sr * 0.45f, 8), Color.Black.copy(alpha = 0.3f))
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), cara.topLeft, cara.size)
        }
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * sin(a * PI.toFloat())), Offset(cara.left + cara.width * giro, cara.top), Size(10 * u, cara.height))
        // El lomo, con sus nervios y las tapas de acero.
        drawRect(mezcla(cuero, Color.Black, 0.25f), lomo.topLeft, lomo.size)
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.25f, lomo.height))
        listOf(0.24f, 0.42f, 0.6f, 0.78f).forEach { f ->
            val y = lomo.top + lomo.height * f
            drawRect(mezcla(cuero, Color.Black, 0.45f), Offset(lomo.left, y - 3 * u), Size(lomo.width, 6 * u))
            drawRect(Color.White.copy(alpha = 0.14f), Offset(lomo.left, y - 3 * u), Size(lomo.width, 1.2f * u))
        }
        listOf(lomo.top, lomo.bottom - h * 0.06f).forEach { y0 -> drawRect(c(ACERO_LUZ), Offset(lomo.left, y0), Size(lomo.width, h * 0.06f)) }
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(lomo.right, lomo.top), Offset(lomo.right, lomo.bottom), 1.4f * u)
    }
}

/**
 * Laboratorio: el estante abierto sobre la mesa de trabajo, contra la pared de azulejo blanco. Arriba, pegada al azulejo, una
 * tabla periódica de cuadritos de color por bloques (sin letras); sobre la tabla de arriba del estante, los frascos de
 * reactivos: uno ámbar con su etiqueta de riesgo, uno de vidrio con líquido cian, la gradilla con sus tubos de ensayo, un
 * matraz Erlenmeyer con líquido violeta y la piseta de pico doblado; debajo de esa tabla, atornillada, la lámpara de luz
 * negra. Los postes son de acero pintado con su cremallera de ranuras, las tablas de melamina blanca con canto gris y ménsulas,
 * y cada mes va en una etiqueta impresa con su franja cian. Abajo, la mesa: la cubierta de resina negra con la llave del gas
 * y el mueble de cajones y puertas sobre el piso de vinilo. Cada entrada es un cuaderno de laboratorio de tapa dura de color,
 * con su lomo de tela negra, la etiqueta impresa con el día y el mes, el código de barras, la etiqueta de riesgo (un rombo
 * rojo con su señal) y pestañas de índice (más cuanto más escribiste). De noche se enciende la luz ultravioleta: los
 * líquidos y las etiquetas fluorescen y el cuaderno lleva una luna en lugar del rombo. Distinto del Ideario, que es el
 * armario clínico de muestras con el microscopio.
 * La tapa del diario es el cuaderno de frente, con su etiqueta impresa, la de riesgo y la cinta de precaución; al
 * abrirse, deja ver la hoja cuadriculada.
 */
object EstanteLaboratorio : DisenoBiblioteca {
    private val AZULEJO = Color(0xFFE6ECEF)
    private val JUNTA = Color(0xFFC3CDD4)
    private val BLANCO = Color(0xFFF7F9FA)
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val EPOXI = Color(0xFF22282D)
    private val MUEBLE = Color(0xFFD5DCE1)
    private val VINILO = Color(0xFF9AA4AB)
    private val CIAN = Color(0xFF1FB5C9)
    private val VIOLETA = Color(0xFF7A4FD0)
    private val AMBAR = Color(0xFF8A4B14)
    private val AMARILLO = Color(0xFFF2C232)
    private val ROJO = Color(0xFFD8312A)
    private val CORCHO = Color(0xFFB58A5A)
    private val TINTA = Color(0xFF12202B)
    private val UV = Color(0xFF9A6BFF)
    private val FLUOR = Color(0xFF8CFFB8)
    private val FLUOR_ROSA = Color(0xFFFF6BE0)
    private val PAPEL_UV = Color(0xFFE4DEFF)
    /**
     * Los bloques de la tabla periódica: alcalinos, alcalinotérreos, transición, bloque p, gases nobles, lantánidos, actínidos
     * y el hidrógeno.
     */
    private val BLOQUES = listOf(Color(0xFFF2A3A3), Color(0xFFF5C99A), Color(0xFFA9C8EE), Color(0xFFB4E0AE), Color(0xFFD5C0F0), Color(0xFFF2DDA0), Color(0xFFF0B9A6), Color(0xFFF7F0A0))
    /** Las tapas de los cuadernos: azul, rojo, verde, negro, violeta y naranja. */
    private val TAPAS = listOf(Color(0xFF1F5FA8), Color(0xFFC8342C), Color(0xFF2E8B57), Color(0xFF2B2F36), Color(0xFF7A4FD0), Color(0xFFE08A1E))
    private val NOCHE = Color(0xFF1A0E3A)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luz ultravioleta. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0B0E12), 0.38f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    /** El papel de las etiquetas: de noche fluoresce con la luz negra; en oscuro se hunde menos que lo demás. */
    private fun papel(oscuro: Boolean, noche: Boolean): Color = when {
        noche -> PAPEL_UV
        oscuro -> mezcla(BLANCO, Color(0xFF0B0E12), 0.16f)
        else -> BLANCO
    }

    override val corona = 0.34f
    override val base = 0.16f
    override val lado = 0.05f
    override val altoRepisa: Dp = 110.dp
    override val tabla: Dp = 14.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.jost

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 40).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.78f + 0.16f * azar(semilla, 6)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // La pared de azulejo blanco, con sus juntas.
        drawRect(c(AZULEJO), Offset.Zero, size)
        val pieza = 16 * u
        var jy = 0f
        while (jy < h) { drawLine(c(JUNTA), Offset(0f, jy), Offset(w, jy), 0.8f * u); jy += pieza }
        var jx = 0f
        while (jx < w) { drawLine(c(JUNTA), Offset(jx, 0f), Offset(jx, h), 0.8f * u); jx += pieza }
        // Cada repisa, abierta contra el azulejo: la sombra de la tabla de arriba.
        d.repisas.forEach { r -> drawRect(Color.Black.copy(alpha = if (noche) 0.3f else 0.14f), r.topLeft, Size(r.width, 7 * u)) }
        // Los postes de acero pintado, con su cremallera de ranuras.
        listOf(d.izquierda, d.derecha).forEach { r ->
            val p = Rect(r.left + r.width * 0.15f, d.corona.bottom - d.corona.height * 0.2f, r.right - r.width * 0.15f, d.base.top)
            drawRect(c(GRIS), p.topLeft, p.size)
            drawRect(Color.White.copy(alpha = 0.3f), p.topLeft, Size(p.width * 0.2f, p.height))
            drawRect(c(GRIS_OSC).copy(alpha = 0.6f), Offset(p.right - p.width * 0.2f, p.top), Size(p.width * 0.2f, p.height))
            var sy = p.top + 6 * u
            while (sy < p.bottom - 6 * u) { drawRoundRect(c(GRIS_OSC), Offset(p.center.x - 1.2f * u, sy), Size(2.4f * u, 4.5f * u), CornerRadius(1.2f * u)); sy += 9 * u }
        }
        // Las tablas: melamina blanca con su canto gris y las ménsulas debajo.
        d.tablas.forEach { r ->
            drawRect(c(BLANCO), r.topLeft, r.size)
            drawRect(c(GRIS), Offset(r.left, r.bottom - r.height * 0.35f), Size(r.width, r.height * 0.35f))
            drawRect(Color.White.copy(alpha = 0.5f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            listOf(r.left to 1f, r.right to -1f).forEach { (x0, s) ->
                drawPath(Path().apply { moveTo(x0, r.bottom); lineTo(x0 + s * 12 * u, r.bottom); lineTo(x0, r.bottom + 12 * u); close() }, c(GRIS_OSC))
            }
        }
        // El rótulo de cada mes: una etiqueta impresa con su franja (de noche, fluorescente).
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(r.left + 1 * u, r.top + 1 * u), r.size)
            drawRect(papel(oscuro, noche), r.topLeft, r.size)
            drawRect(if (noche) FLUOR else c(CIAN), r.topLeft, Size(r.height * 0.9f, r.height))
            drawRect(c(GRIS), r.topLeft, r.size, style = Stroke(0.6f * u))
            textoEn(medidor, p.texto, Rect(r.left + r.height * 1.2f, r.top, r.right - 3 * u, r.bottom), letra, c(TINTA), 10.5f, peso = FontWeight.Medium, espaciado = 0.4f)
        }
        corona(d.corona, oscuro, noche)
        // La mesa de trabajo: la cubierta de resina negra con la llave del gas y, debajo, el mueble de cajones y puertas.
        val b = d.base
        val cubierta = Rect(0f, b.top, w, b.top + b.height * 0.2f)
        drawRect(c(EPOXI), cubierta.topLeft, cubierta.size)
        drawRect(Color.White.copy(alpha = 0.12f), cubierta.topLeft, Size(w, 1.4f * u))
        val gx = w * 0.12f
        drawRoundRect(c(GRIS), Offset(gx - 3 * u, cubierta.center.y - 2.5f * u), Size(6 * u, 5 * u), CornerRadius(1 * u))
        drawLine(c(AMARILLO), Offset(gx, cubierta.center.y), Offset(gx + 8 * u, cubierta.center.y), 2 * u, cap = StrokeCap.Round)
        val piso = b.top + b.height * 0.86f
        val mueble = Rect(w * 0.03f, cubierta.bottom, w * 0.97f, piso - b.height * 0.06f)
        drawRect(c(MUEBLE), mueble.topLeft, mueble.size)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, cubierta.bottom), Size(w, 3 * u))
        for (k in 0 until 3) {
            val x0 = mueble.left + mueble.width * k / 3f + 2 * u
            val x1 = mueble.left + mueble.width * (k + 1) / 3f - 2 * u
            val cajon = Rect(x0, mueble.top + 4 * u, x1, mueble.top + mueble.height * 0.34f)
            val puerta = Rect(x0, cajon.bottom + 3 * u, x1, mueble.bottom - 2 * u)
            listOf(cajon, puerta).forEach { q ->
                drawRect(c(BLANCO), q.topLeft, q.size)
                drawRect(c(GRIS), q.topLeft, q.size, style = Stroke(0.7f * u))
            }
            drawRoundRect(c(GRIS_OSC), Offset(cajon.center.x - 8 * u, cajon.center.y - 1 * u), Size(16 * u, 2 * u), CornerRadius(1 * u))
            drawRoundRect(c(GRIS_OSC), Offset(if (k % 2 == 0) puerta.right - 5 * u else puerta.left + 3 * u, puerta.top + 3 * u), Size(2 * u, 8 * u), CornerRadius(1 * u))
        }
        drawRect(c(GRIS_OSC), Offset(mueble.left + 4 * u, mueble.bottom), Size(mueble.width - 8 * u, piso - mueble.bottom))
        drawRect(c(VINILO), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, piso), Size(w, 2 * u))
        // De noche, la luz ultravioleta lo tiñe todo de violeta.
        if (noche) drawRect(UV.copy(alpha = 0.08f), Offset.Zero, size)
    }

    /** La tabla periódica, la tabla de arriba con los frascos y la lámpara de luz negra. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        /** Los líquidos: de día su color; de noche, fluorescentes. */
        fun liquido(x: Color, brillo: Color) = if (noche) brillo else c(x)
        // La tabla periódica pegada al azulejo: un cuadrito por elemento, por bloques de color, sin letras.
        val cartel = Rect(r.left + r.width * 0.24f, r.top + r.height * 0.06f, r.right - r.width * 0.24f, r.top + r.height * 0.58f)
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(cartel.left + 1.5f * u, cartel.top + 1.5f * u), cartel.size)
        drawRect(papel(oscuro, noche), cartel.topLeft, cartel.size)
        val ins = cartel.deflate(4 * u)
        val cw = ins.width / 18f; val ch = ins.height / 9.5f
        fun celda(fila: Float, col: Int, k: Int) {
            drawRect(c(BLOQUES[k]), Offset(ins.left + col * cw + 0.35f * u, ins.top + fila * ch + 0.35f * u), Size(cw - 0.7f * u, ch - 0.7f * u))
        }
        for (fila in 0 until 7) for (col in 0 until 18) {
            val hay = when (fila) { 0 -> col == 0 || col == 17; 1, 2 -> col <= 1 || col >= 12; else -> true }
            if (!hay) continue
            val k = when {
                fila == 0 && col == 0 -> 7
                col == 0 -> 0
                col == 1 -> 1
                col == 2 && fila == 5 -> 5
                col == 2 && fila == 6 -> 6
                col <= 11 -> 2
                col <= 16 -> 3
                else -> 4
            }
            celda(fila.toFloat(), col, k)
        }
        for (col in 2 until 17) { celda(7.5f, col, 5); celda(8.5f, col, 6) }
        // El título del cartel: una barra gris (forma, no letras).
        drawRect(c(GRIS_OSC), Offset(ins.left + 3 * cw, ins.top + ch * 0.3f), Size(cw * 7, ch * 0.5f))
        // La tabla de arriba del estante y la lámpara de luz negra atornillada debajo (de noche, encendida).
        val tapa = Rect(r.left + r.width * 0.01f, r.bottom - r.height * 0.2f, r.right - r.width * 0.01f, r.bottom - r.height * 0.11f)
        val lampara = Rect(r.left + r.width * 0.2f, tapa.bottom, r.right - r.width * 0.2f, tapa.bottom + r.height * 0.06f)
        if (noche) mancha(Offset(lampara.center.x, lampara.bottom), r.width * 0.45f, UV.copy(alpha = 0.35f))
        drawRect(c(GRIS_OSC), lampara.topLeft, lampara.size)
        drawRoundRect(if (noche) Color(0xFFC9A8FF) else c(Color(0xFF4A3A70)), Offset(lampara.left + 3 * u, lampara.top + lampara.height * 0.35f), Size(lampara.width - 6 * u, lampara.height * 0.5f), CornerRadius(lampara.height * 0.25f))
        drawRect(c(BLANCO), tapa.topLeft, tapa.size)
        drawRect(c(GRIS), Offset(tapa.left, tapa.bottom - tapa.height * 0.35f), Size(tapa.width, tapa.height * 0.35f))
        drawRect(Color.White.copy(alpha = 0.5f), tapa.topLeft, Size(tapa.width, 1.2f * u))
        val y0 = tapa.top
        val alto = r.height * 0.22f
        // Un frasco ámbar de reactivo, con su tapa negra y su etiqueta con franja roja.
        val f1 = Rect(r.left + r.width * 0.05f, y0 - alto, r.left + r.width * 0.12f, y0)
        drawRoundRect(c(AMBAR), Offset(f1.left, f1.top + f1.height * 0.25f), Size(f1.width, f1.height * 0.75f), CornerRadius(f1.width * 0.18f))
        drawRect(c(AMBAR), Offset(f1.center.x - f1.width * 0.22f, f1.top + f1.height * 0.1f), Size(f1.width * 0.44f, f1.height * 0.2f))
        drawRect(c(TINTA), Offset(f1.center.x - f1.width * 0.28f, f1.top), Size(f1.width * 0.56f, f1.height * 0.12f))
        val e1 = Rect(f1.left + f1.width * 0.12f, f1.top + f1.height * 0.48f, f1.right - f1.width * 0.12f, f1.top + f1.height * 0.82f)
        drawRect(papel(oscuro, noche), e1.topLeft, e1.size)
        drawRect(c(ROJO), e1.topLeft, Size(e1.width, e1.height * 0.25f))
        drawRect(Color.White.copy(alpha = 0.22f), Offset(f1.left + f1.width * 0.15f, f1.top + f1.height * 0.3f), Size(f1.width * 0.1f, f1.height * 0.6f))
        // Un frasco de vidrio con líquido cian y su tapa blanca.
        val f2 = Rect(r.left + r.width * 0.135f, y0 - alto * 0.8f, r.left + r.width * 0.2f, y0)
        drawRoundRect(Color.White.copy(alpha = 0.35f), Offset(f2.left, f2.top + f2.height * 0.2f), Size(f2.width, f2.height * 0.8f), CornerRadius(f2.width * 0.15f))
        drawRoundRect(liquido(CIAN, FLUOR), Offset(f2.left + 1 * u, f2.top + f2.height * 0.5f), Size(f2.width - 2 * u, f2.height * 0.5f - 1 * u), CornerRadius(f2.width * 0.12f))
        drawRoundRect(c(GRIS_OSC), Offset(f2.left, f2.top + f2.height * 0.2f), Size(f2.width, f2.height * 0.8f), CornerRadius(f2.width * 0.15f), style = Stroke(0.8f * u))
        drawRect(c(BLANCO), Offset(f2.center.x - f2.width * 0.25f, f2.top), Size(f2.width * 0.5f, f2.height * 0.2f))
        // La gradilla con sus tubos de ensayo.
        val gr = Rect(r.center.x - r.width * 0.12f, y0 - alto * 0.8f, r.center.x + r.width * 0.12f, y0)
        listOf(CIAN to FLUOR, VIOLETA to FLUOR_ROSA, AMARILLO to FLUOR, ROJO to FLUOR_ROSA, CIAN to FLUOR).forEachIndexed { k, (dia, brillo) ->
            val tx = gr.left + gr.width * (0.12f + 0.19f * k)
            val tubo = Rect(tx - 2.5f * u, gr.top, tx + 2.5f * u, gr.bottom - 1 * u)
            drawRoundRect(Color.White.copy(alpha = 0.4f), tubo.topLeft, tubo.size, CornerRadius(2.5f * u))
            val nivel = 0.35f + 0.3f * ((k * 37) % 10) / 10f
            drawRoundRect(liquido(dia, brillo), Offset(tubo.left + 0.6f * u, tubo.bottom - tubo.height * nivel), Size(tubo.width - 1.2f * u, tubo.height * nivel - 0.6f * u), CornerRadius(2 * u))
            drawRoundRect(c(GRIS_OSC), tubo.topLeft, tubo.size, CornerRadius(2.5f * u), style = Stroke(0.6f * u))
        }
        drawRect(c(GRIS), Offset(gr.left - 2 * u, gr.top + gr.height * 0.35f), Size(gr.width + 4 * u, 3 * u))
        drawRect(c(GRIS), Offset(gr.left - 2 * u, gr.bottom - 3 * u), Size(gr.width + 4 * u, 3 * u))
        listOf(gr.left - 2 * u, gr.right).forEach { x -> drawRect(c(GRIS_OSC), Offset(x, gr.top + gr.height * 0.35f), Size(2 * u, gr.height * 0.65f)) }
        // El matraz Erlenmeyer con su líquido violeta y su tapón de corcho.
        val m = Rect(r.right - r.width * 0.2f, y0 - alto, r.right - r.width * 0.11f, y0)
        val matraz = Path().apply {
            moveTo(m.center.x - m.width * 0.14f, m.top + m.height * 0.12f); lineTo(m.center.x + m.width * 0.14f, m.top + m.height * 0.12f)
            lineTo(m.center.x + m.width * 0.14f, m.top + m.height * 0.38f); lineTo(m.right, m.bottom - 1 * u)
            lineTo(m.left, m.bottom - 1 * u); lineTo(m.center.x - m.width * 0.14f, m.top + m.height * 0.38f); close()
        }
        drawPath(matraz, Color.White.copy(alpha = 0.35f))
        clipPath(matraz) { drawRect(liquido(VIOLETA, FLUOR_ROSA), Offset(m.left, m.top + m.height * 0.64f), Size(m.width, m.height * 0.36f)) }
        drawPath(matraz, c(GRIS_OSC), style = Stroke(0.8f * u))
        drawRect(c(CORCHO), Offset(m.center.x - m.width * 0.12f, m.top), Size(m.width * 0.24f, m.height * 0.14f))
        // La piseta, con su anillo de color y su pico doblado.
        val pi = Rect(r.right - r.width * 0.095f, y0 - alto * 0.75f, r.right - r.width * 0.035f, y0)
        drawRoundRect(c(BLANCO).copy(alpha = 0.9f), Offset(pi.left, pi.top + pi.height * 0.22f), Size(pi.width, pi.height * 0.78f), CornerRadius(pi.width * 0.3f))
        drawRect(if (noche) FLUOR else c(CIAN), Offset(pi.left, pi.top + pi.height * 0.22f), Size(pi.width, pi.height * 0.12f))
        drawLine(c(GRIS_OSC), Offset(pi.center.x, pi.top + pi.height * 0.22f), Offset(pi.center.x, pi.top + pi.height * 0.05f), 1.4f * u)
        drawLine(c(GRIS_OSC), Offset(pi.center.x, pi.top + pi.height * 0.05f), Offset(pi.left - pi.width * 0.3f, pi.top + pi.height * 0.12f), 1.2f * u)
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TAPAS[(v.semilla and 0x7fffffff) % TAPAS.size]
        val tapa = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // La tapa dura de color, con el lomo de tela negra arriba y abajo.
        drawRoundRect(tapa, Offset.Zero, size, CornerRadius(1.2f * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.06f, 0f), Size(w * 0.1f, h))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(w * 0.86f, 0f), Size(w * 0.14f, h))
        drawRect(c(EPOXI), Offset.Zero, Size(w, h * 0.045f))
        drawRect(c(EPOXI), Offset(0f, h * 0.88f), Size(w, h * 0.12f))
        // Las pestañas de índice en el canto: más cuanto más escribiste.
        val pestanas = 1 + (v.lleno * 4).toInt()
        for (j in 0 until pestanas) drawRect(c(BLOQUES[(j + (v.semilla ushr 2)) % 5]), Offset(w * 0.88f, h * (0.46f + 0.08f * j)), Size(w * 0.12f, h * 0.05f))
        // La etiqueta impresa: la franja cian, el día grande y el mes.
        val eti = Rect(w * 0.12f, h * 0.08f, w * 0.84f, h * 0.36f)
        drawRect(papel(oscuro, v.noche), eti.topLeft, eti.size)
        drawRect(if (v.noche) FLUOR else c(CIAN), eti.topLeft, Size(eti.width, eti.height * 0.14f))
        textoEn(medidor, v.dia, Rect(eti.left, eti.top + eti.height * 0.16f, eti.right, eti.top + eti.height * 0.66f), letra, c(TINTA), 14f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(eti.left + 0.5f * u, eti.top + eti.height * 0.66f, eti.right - 0.5f * u, eti.bottom - 0.5f * u), letra, c(TINTA).copy(alpha = 0.8f), 7f, minimo = 4f)
        // El código de barras.
        val cb = Rect(w * 0.12f, h * 0.4f, w * 0.84f, h * 0.53f)
        drawRect(papel(oscuro, v.noche), cb.topLeft, cb.size)
        var bx = cb.left + 1.5f * u; var k = 0
        while (bx < cb.right - 1.5f * u) {
            val g = (if (azar(v.semilla, 50 + k) > 0.5f) 1.2f else 0.6f) * u
            drawRect(c(TINTA), Offset(bx, cb.top + 1 * u), Size(g, cb.height - 2 * u))
            bx += g + (0.6f + azar(v.semilla, 80 + k)) * u; k++
        }
        // La etiqueta de riesgo: un rombo de borde rojo con su señal (barra y punto); de noche, una luna y dos estrellas.
        val rc = Offset(w * 0.47f, h * 0.71f)
        val rs = minOf(w * 0.3f, 8 * u)
        if (v.noche) {
            drawCircle(PAPEL_UV, rs * 0.6f, rc)
            drawCircle(tapa, rs * 0.5f, Offset(rc.x + rs * 0.3f, rc.y - rs * 0.18f))
            drawCircle(FLUOR, 0.8f * u, Offset(rc.x - rs * 0.7f, rc.y + rs * 0.6f))
            drawCircle(FLUOR, 0.7f * u, Offset(rc.x + rs * 0.75f, rc.y + rs * 0.75f))
        } else {
            val rombo = Path().apply { moveTo(rc.x, rc.y - rs); lineTo(rc.x + rs, rc.y); lineTo(rc.x, rc.y + rs); lineTo(rc.x - rs, rc.y); close() }
            drawPath(rombo, papel(oscuro, false))
            drawPath(rombo, c(ROJO), style = Stroke(1.4f * u))
            drawRoundRect(c(TINTA), Offset(rc.x - 0.7f * u, rc.y - rs * 0.5f), Size(1.4f * u, rs * 0.65f), CornerRadius(0.7f * u))
            drawCircle(c(TINTA), 0.8f * u, Offset(rc.x, rc.y + rs * 0.42f))
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 184.dp
    override val cajaTapa = Rect(0.2f, 0.3f, 0.82f, 0.625f)
    override val letraTapa: FontFamily = LetraExtra.jost
    override val tamTapa = 13f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /**
     * El cuaderno de laboratorio del día, de frente: la tapa dura de color con su textura, el lomo y las esquinas de tela
     * negra, las pestañas de índice que asoman por el canto (más cuanto más escribiste) y la etiqueta impresa con su franja
     * cian: el nombre del cuaderno, la fecha, lo escrito, el código de barras y las palabras. Debajo, la etiqueta de riesgo y
     * la cinta de precaución. Al abrirse, la tapa gira sobre el lomo y deja ver la hoja cuadriculada con un anillo dibujado.
     * De noche, bajo la luz ultravioleta, la etiqueta y las salpicaduras fluorescen y el rombo cede su lugar a una luna.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val color = c(TAPAS[(e.semilla and 0x7fffffff) % TAPAS.size])
        val hoja = papel(oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val libro = Rect(w * 0.015f, h * 0.025f, w * 0.95f, h * 0.935f)
        val lomo = Rect(libro.left, libro.top, libro.left + w * 0.09f, libro.bottom)
        val cara = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        // La sombra, el canto de las hojas y las pestañas de índice que asoman.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(libro.left + 4 * u, libro.top + 5 * u), Size(libro.width + 2 * u, libro.height + 2 * u))
        val hojas = Rect(libro.left + w * 0.02f, libro.top + h * 0.03f, libro.right + 2.5f * u, libro.bottom + 3 * u)
        drawRect(mezcla(hoja, Color.Black, 0.1f), hojas.topLeft, hojas.size)
        for (j in 1..3) drawLine(Color.Black.copy(alpha = 0.15f), Offset(hojas.left + 2 * u, libro.bottom + j * 0.8f * u), Offset(hojas.right - 1 * u, libro.bottom + j * 0.8f * u), 0.5f * u)
        val pestanas = 1 + (e.palabras / 800f * 4).toInt().coerceIn(0, 4)
        for (j in 0 until pestanas) {
            val y = h * (0.14f + 0.15f * j)
            drawRoundRect(c(BLOQUES[(j + (e.semilla ushr 2)) % 5]), Offset(libro.right, y), Size(w * 0.04f, h * 0.1f), CornerRadius(2 * u))
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(libro.right + w * 0.035f, y + 2 * u), Offset(libro.right + w * 0.035f, y + h * 0.1f - 2 * u), 0.8f * u)
        }
        // Dentro (se ve al abrir): la hoja cuadriculada, el margen cian y un anillo de seis carbonos dibujado a lápiz.
        if (a > 0.01f) {
            drawRect(hoja, cara.topLeft, cara.size)
            val paso = 7 * u
            var gx = cara.left + paso
            while (gx < cara.right) { drawLine((if (e.noche) FLUOR else CIAN).copy(alpha = 0.25f), Offset(gx, cara.top), Offset(gx, cara.bottom), 0.5f * u); gx += paso }
            var gy = cara.top + paso
            while (gy < cara.bottom) { drawLine((if (e.noche) FLUOR else CIAN).copy(alpha = 0.25f), Offset(cara.left, gy), Offset(cara.right, gy), 0.5f * u); gy += paso }
            drawLine(c(ROJO).copy(alpha = 0.5f), Offset(cara.left + w * 0.06f, cara.top), Offset(cara.left + w * 0.06f, cara.bottom), 1 * u)
            val mc = Offset(cara.left + cara.width * 0.6f, cara.center.y)
            val mr = h * 0.13f
            val anillo = Path().apply {
                for (q in 0..6) { val ang = (q * 60f - 90f) * PI.toFloat() / 180f; val p = Offset(mc.x + mr * cos(ang), mc.y + mr * sin(ang)); if (q == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            }
            drawPath(anillo, tinta.copy(alpha = 0.6f), style = Stroke(1.2f * u))
            drawCircle(tinta.copy(alpha = 0.45f), mr * 0.55f, mc, style = Stroke(1 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), cara.topLeft, Size(8 * u, cara.height))
        }
        // La tapa dura, que gira sobre el lomo.
        val giro = cos(a * 1.6f).coerceAtLeast(0.02f)
        scale(giro, 1f, Offset(cara.left, cara.center.y)) {
            drawRect(Brush.verticalGradient(listOf(mezcla(color, Color.White, 0.12f), color, mezcla(color, Color.Black, 0.2f)), cara.top, cara.bottom), cara.topLeft, cara.size)
            // La textura granulada del cartón forrado.
            for (k in 0 until 140) {
                val o = Offset(cara.left + cara.width * azar(e.semilla, 200 + k), cara.top + cara.height * azar(e.semilla, 300 + k))
                drawCircle((if (k % 2 == 0) Color.White else Color.Black).copy(alpha = 0.06f), 0.9f * u, o)
            }
            drawRect(Color.White.copy(alpha = 0.15f), cara.topLeft, Size(cara.width, 1.2f * u))
            // De noche, las salpicaduras que fluorescen bajo la luz negra.
            if (e.noche) for (k in 0 until 5) {
                val o = Offset(cara.left + cara.width * (0.05f + 0.9f * azar(e.semilla, 30 + k)), cara.top + cara.height * (if (k % 2 == 0) 0.9f else 0.06f))
                drawCircle(FLUOR_ROSA.copy(alpha = 0.55f), (1.4f + 2 * azar(e.semilla, 40 + k)) * u, o)
                drawCircle(FLUOR_ROSA.copy(alpha = 0.4f), 1 * u, Offset(o.x + 4 * u, o.y + 1.5f * u))
            }
            // Las esquinas de tela negra.
            val ke = 20 * u
            listOf(cara.top to 1f, cara.bottom to -1f).forEach { (y, s) ->
                drawPath(Path().apply { moveTo(cara.right, y); lineTo(cara.right - ke, y); lineTo(cara.right, y + s * ke); close() }, c(EPOXI))
                drawLine(Color.White.copy(alpha = 0.12f), Offset(cara.right - ke, y), Offset(cara.right, y + s * ke), 0.8f * u)
            }
            // La etiqueta impresa: la franja cian con el nombre del cuaderno, la fecha, lo escrito, el código y las palabras.
            val eti = Rect(w * 0.17f, h * 0.1f, w * 0.85f, h * 0.795f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(eti.left + 1.5f * u, eti.top + 2 * u), eti.size)
            drawRect(hoja, eti.topLeft, eti.size)
            val franja = Rect(eti.left, eti.top, eti.right, h * 0.185f)
            drawRect(if (e.noche) FLUOR else c(CIAN), franja.topLeft, franja.size)
            textoEn(medidor, "CUADERNO DE LABORATORIO", franja.deflate(2 * u), letra, tinta, 11f, peso = FontWeight.SemiBold, minimo = 6f)
            textoEn(medidor, "FECHA", Rect(eti.left + 6 * u, h * 0.2f, eti.left + eti.width * 0.2f, h * 0.27f), letra, tinta.copy(alpha = 0.55f), 9f, peso = FontWeight.SemiBold, minimo = 6f)
            textoEn(medidor, e.fecha, Rect(eti.left + eti.width * 0.21f, h * 0.2f, eti.right - 6 * u, h * 0.27f), letra, tinta, 12.5f, peso = FontWeight.SemiBold, minimo = 7f)
            drawLine(tinta.copy(alpha = 0.4f), Offset(eti.left + 6 * u, h * 0.28f), Offset(eti.right - 6 * u, h * 0.28f), 0.7f * u)
            drawLine(tinta.copy(alpha = 0.4f), Offset(eti.left + 6 * u, h * 0.64f), Offset(eti.right - 6 * u, h * 0.64f), 0.7f * u)
            val cb = Rect(eti.left + 6 * u, h * 0.665f, eti.left + eti.width * 0.42f, h * 0.77f)
            var bx = cb.left; var k = 0
            while (bx < cb.right) {
                val g = (if (azar(e.semilla, 500 + k) > 0.5f) 1.6f else 0.8f) * u
                drawRect(tinta, Offset(bx, cb.top), Size(g, cb.height))
                bx += g + (0.7f + azar(e.semilla, 600 + k)) * u; k++
            }
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(eti.left + eti.width * 0.46f, h * 0.66f, eti.right - 6 * u, h * 0.775f), letra, tinta.copy(alpha = 0.8f), 10.5f, minimo = 6f)
            // La etiqueta de riesgo (un rombo de borde rojo con su señal); de noche, una luna y dos estrellas fluorescentes.
            val rc = Offset(w * 0.26f, h * 0.87f)
            val rs = 8.5f * u
            if (e.noche) {
                drawCircle(PAPEL_UV, rs * 0.7f, rc)
                drawCircle(color, rs * 0.58f, Offset(rc.x + rs * 0.32f, rc.y - rs * 0.2f))
                drawPath(estrella(Offset(rc.x + rs * 1.4f, rc.y + rs * 0.3f), 2.2f * u, 4, 0.4f), FLUOR)
                drawPath(estrella(Offset(rc.x - rs * 1.3f, rc.y - rs * 0.4f), 1.8f * u, 4, 0.4f), FLUOR)
            } else {
                val rombo = Path().apply { moveTo(rc.x, rc.y - rs); lineTo(rc.x + rs, rc.y); lineTo(rc.x, rc.y + rs); lineTo(rc.x - rs, rc.y); close() }
                drawPath(rombo, hoja)
                drawPath(rombo, c(ROJO), style = Stroke(1.6f * u))
                drawRoundRect(tinta, Offset(rc.x - 0.8f * u, rc.y - rs * 0.5f), Size(1.6f * u, rs * 0.62f), CornerRadius(0.8f * u))
                drawCircle(tinta, 0.9f * u, Offset(rc.x, rc.y + rs * 0.42f))
            }
            // La cinta de precaución, a rayas.
            val cinta = Rect(w * 0.5f, h * 0.845f, w * 0.84f, h * 0.895f)
            drawRect(c(AMARILLO), cinta.topLeft, cinta.size)
            clipRect(cinta.left, cinta.top, cinta.right, cinta.bottom) {
                var sx = cinta.left - cinta.height
                while (sx < cinta.right) {
                    drawPath(Path().apply { moveTo(sx, cinta.bottom); lineTo(sx + cinta.height, cinta.top); lineTo(sx + cinta.height + 4 * u, cinta.top); lineTo(sx + 4 * u, cinta.bottom); close() }, c(EPOXI))
                    sx += 8 * u
                }
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), cara.topLeft, cara.size)
        }
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * sin(a * PI.toFloat())), Offset(cara.left + cara.width * giro, cara.top), Size(10 * u, cara.height))
        // El lomo de tela negra, con su trama y dos filetes impresos.
        drawRect(c(EPOXI), lomo.topLeft, lomo.size)
        var ty = lomo.top
        while (ty < lomo.bottom) { drawLine(Color.White.copy(alpha = 0.05f), Offset(lomo.left, ty), Offset(lomo.right, ty), 0.6f * u); ty += 2 * u }
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.25f, lomo.top), Size(lomo.width * 0.2f, lomo.height))
        listOf(0.1f, 0.9f).forEach { f -> drawLine(hoja.copy(alpha = 0.6f), Offset(lomo.left + 3 * u, lomo.top + lomo.height * f), Offset(lomo.right - 3 * u, lomo.top + lomo.height * f), 1 * u) }
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(lomo.right, lomo.top), Offset(lomo.right, lomo.bottom), 1.2f * u)
        // De noche, la luz ultravioleta lo baña todo.
        if (e.noche) drawRect(UV.copy(alpha = 0.06f), Offset.Zero, size)
    }
}

/**
 * Colonos: el estante hecho de cajas de embalaje apiladas, de pino rescatado. Cada repisa es una caja tumbada de boca, con su
 * fondo de tablillas y sus rendijas (partido donde se juntan dos cajas); los costados son los cantos de las cajas, con sus
 * clavos y las marcas de embarque estarcidas en azul marino y ladrillo (rombos, círculos con barra, flechas de «este lado
 * arriba», estrellas: formas, no letras). Arriba, una caja cerrada con sus marcas grandes y, encima, un rollo de cuerda, el
 * quinqué de latón con su tubo de vidrio y una rueda de carreta recargada. Las tablas son el fondo de una caja y la tapa de la
 * otra, y cada mes va en una tarjeta de envío de cartulina colgada de un clavo con cordel. Abajo, la tarima de largueros, un
 * costal de grano y el piso de tablones. Cada entrada es un almanaque o libro de cuentas forrado de tela gastada (esquinas
 * peladas, bordes deshilachados), con su etiqueta de papel con el día, el mes a lo largo del lomo y el hilo que lo ata con su
 * nudo; asoman más hojas sueltas cuanto más escribiste. De noche se enciende el quinqué y el libro lleva una luna en lugar del
 * sello de la tienda. Distinto del Ideario, que es la cajonera del almacén general con su balanza.
 * La tapa del diario es el libro de cuentas de frente, atado con su hilo y con la etiqueta de doble filete; al abrirse,
 * el hilo se suelta.
 */
object EstanteColonos : DisenoBiblioteca {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val RENDIJA = Color(0xFF2E2418)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val MARINO = Color(0xFF2A3550)
    private val LADRILLO = Color(0xFF9E4A35)
    private val GACETA = Color(0xFFE8DFC6)
    private val MANILA = Color(0xFFD9BE86)
    private val CORDEL = Color(0xFFBFA272)
    private val COSTAL = Color(0xFFB59E74)
    private val HIERRO = Color(0xFF3B3A38)
    private val LATON = Color(0xFFB89140)
    private val VIDRIO = Color(0xFFDCE6E8)
    private val LLAMA = Color(0xFFFFC24A)
    private val TABLON = Color(0xFF7A5B3A)
    private val TINTA = Color(0xFF1F2A3A)
    /** Las telas de los libros: marino, ladrillo, verde, pardo y gris violáceo. */
    private val TELAS = listOf(Color(0xFF2F3E5C), Color(0xFF7A3A2C), Color(0xFF4E5B3A), Color(0xFF6B5A44), Color(0xFF3E3A4A))
    private val NOCHE = Color(0xFF22160A)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, a la luz tibia del quinqué. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.42f)
        return r
    }

    /** El papel de las etiquetas: se hunde menos, para que el texto se lea en oscuro y de noche. */
    private fun papel(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.15f)
        if (noche) r = mezcla(r, NOCHE, 0.2f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 20.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.fell

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 42).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.14f * azar(semilla, 7)

    /** Una marca de embarque estarcida (formas, no letras): rombo, círculo con barra, flechas hacia arriba, estrella o cruz en círculo. */
    private fun DrawScope.marca(k: Int, o: Offset, s: Float, color: Color) {
        val u = density
        when (k % 5) {
            0 -> {
                drawPath(Path().apply { moveTo(o.x, o.y - s); lineTo(o.x + s, o.y); lineTo(o.x, o.y + s); lineTo(o.x - s, o.y); close() }, color, style = Stroke(1.6f * u))
                drawCircle(color, s * 0.18f, o)
            }
            1 -> {
                drawCircle(color, s * 0.8f, o, style = Stroke(1.6f * u))
                drawLine(color, Offset(o.x - s, o.y), Offset(o.x + s, o.y), 1.6f * u)
            }
            2 -> {
                for (j in -1..1 step 2) {
                    val x = o.x + j * s * 0.45f
                    drawLine(color, Offset(x, o.y + s), Offset(x, o.y - s * 0.4f), 1.6f * u)
                    drawPath(Path().apply { moveTo(x - s * 0.35f, o.y - s * 0.3f); lineTo(x, o.y - s); lineTo(x + s * 0.35f, o.y - s * 0.3f); close() }, color)
                }
            }
            3 -> drawPath(estrella(o, s, 5, 0.42f), color)
            else -> {
                drawCircle(color, s * 0.8f, o, style = Stroke(1.6f * u))
                drawLine(color, Offset(o.x, o.y - s * 0.8f), Offset(o.x, o.y + s * 0.8f), 1.4f * u)
                drawLine(color, Offset(o.x - s * 0.8f, o.y), Offset(o.x + s * 0.8f, o.y), 1.4f * u)
            }
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // Detrás de las cajas, la penumbra del almacén.
        drawRect(c(RENDIJA), Offset.Zero, size)
        // Cada repisa es una caja tumbada de boca: el fondo de tablillas con sus rendijas, partido donde se juntan dos cajas.
        d.repisas.forEachIndexed { k, r ->
            val junta = r.left + r.width * (0.38f + 0.24f * azar(k, 3))
            var y = r.top; var j = 0
            while (y < r.bottom) {
                val alto = minOf(15 * u, r.bottom - y)
                if (alto > 2 * u) {
                    listOf(r.left to junta, junta to r.right).forEachIndexed { m, (x0, x1) ->
                        val tono = when ((j + m + k) % 3) { 0 -> PINO; 1 -> PINO_OSC; else -> mezcla(PINO, PINO_OSC, 0.5f) }
                        val tablilla = Rect(x0 + 1 * u, y, x1 - 1 * u, y + alto - 1.6f * u)
                        drawRect(c(tono), tablilla.topLeft, tablilla.size)
                        vetas(tablilla, c(VETA).copy(alpha = 0.35f), k * 31 + j * 7 + m, densidad = 0.5f, grosor = 0.6f)
                    }
                }
                y += 15 * u; j++
            }
            // La sombra de la tabla de arriba y la de los costados de cada caja.
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), r.topLeft, Size(r.width, 10 * u))
            listOf(r.left, junta - 5 * u, junta, r.right - 5 * u).forEach { x -> drawRect(Color.Black.copy(alpha = 0.15f), Offset(x, r.top), Size(5 * u, r.height)) }
        }
        // Los costados: el canto de las cajas, con sus clavos y las marcas estarcidas.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(PINO_LUZ), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.4f), if (izq) 5 else 6, densidad = 0.8f, vertical = true, grosor = 0.6f)
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(if (izq) r.right - 2 * u else r.left, r.top), Size(2 * u, r.height))
            val otro = if (izq) 0 else 1
            d.repisas.forEachIndexed { k, rep ->
                drawLine(c(RENDIJA), Offset(r.left, rep.bottom + 0.5f * u), Offset(r.right, rep.bottom + 0.5f * u), 1.2f * u)
                listOf(rep.top + 6 * u, rep.bottom - 6 * u).forEach { y ->
                    drawCircle(c(CLAVO), 1.1f * u, Offset(r.left + r.width * 0.3f, y))
                    drawCircle(c(CLAVO), 1.1f * u, Offset(r.right - r.width * 0.3f, y))
                }
                marca(k * 2 + otro, Offset(r.center.x, rep.center.y), r.width * 0.32f, c(if ((k + otro) % 2 == 0) MARINO else LADRILLO).copy(alpha = 0.75f))
            }
        }
        // Las tablas: el fondo de la caja de arriba y la tapa de la de abajo, con la rendija entre las dos.
        d.tablas.forEachIndexed { k, r ->
            val mitad = r.top + r.height * 0.5f
            drawRect(c(PINO), r.topLeft, Size(r.width, r.height * 0.5f))
            drawRect(c(PINO_LUZ), Offset(r.left, mitad), Size(r.width, r.bottom - mitad))
            vetas(r, c(VETA).copy(alpha = 0.4f), 70 + k, densidad = 0.5f, grosor = 0.6f)
            drawLine(c(RENDIJA), Offset(r.left, mitad), Offset(r.right, mitad), 1.2f * u)
            drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, 1.2f * u))
            listOf(r.left + 5 * u, r.right - 5 * u).forEach { x ->
                drawCircle(c(CLAVO), 1.1f * u, Offset(x, r.top + r.height * 0.25f))
                drawCircle(c(CLAVO), 1.1f * u, Offset(x, r.top + r.height * 0.75f))
            }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una tarjeta de envío de cartulina con su ojal reforzado, colgada de un clavo con cordel.
        d.placas.forEach { p ->
            val r = p.r
            val ojal = Offset(r.left + r.height * 0.45f, r.center.y)
            val clavo = Offset(r.left - 6 * u, r.center.y - r.height * 0.2f)
            drawLine(c(CORDEL), clavo, ojal, 0.9f * u)
            drawCircle(c(CLAVO), 1.4f * u, clavo)
            val m = r.height * 0.3f
            val tarjeta = Path().apply {
                moveTo(r.left + m, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom)
                lineTo(r.left + m, r.bottom); lineTo(r.left, r.bottom - m); lineTo(r.left, r.top + m); close()
            }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left + m + 1.5f * u, r.top + 1.5f * u), Size(r.width - m, r.height))
            drawPath(tarjeta, papel(MANILA, oscuro, noche))
            drawPath(tarjeta, c(PINO_OSC), style = Stroke(0.6f * u))
            drawCircle(papel(GACETA, oscuro, noche), r.height * 0.2f, ojal)
            drawCircle(c(RENDIJA), r.height * 0.09f, ojal)
            textoEn(medidor, p.texto, Rect(r.left + r.height * 0.9f, r.top, r.right - 3 * u, r.bottom), letra, c(TINTA), 11.5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: la tarima de largueros donde se apoyan las cajas, un costal de grano y el piso de tablones.
        val b = d.base
        val piso = b.top + b.height * 0.66f
        drawRect(c(TABLON), Offset(0f, piso), Size(w, h - piso))
        var py = piso + 5 * u
        while (py < h) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, py), Offset(w, py), 0.8f * u); py += 8 * u }
        vetas(Rect(0f, piso, w, h), Color.Black.copy(alpha = 0.15f), 91, densidad = 0.7f, grosor = 0.6f)
        val tarima = Rect(b.left + b.width * 0.02f, b.top, b.right - b.width * 0.02f, b.top + b.height * 0.3f)
        drawRect(c(PINO_OSC), tarima.topLeft, tarima.size)
        vetas(tarima, c(VETA).copy(alpha = 0.4f), 92, densidad = 0.6f, grosor = 0.6f)
        listOf(0.04f, 0.46f, 0.88f).forEach { f -> drawRect(c(PINO), Offset(b.left + b.width * f, tarima.bottom), Size(b.width * 0.08f, piso - tarima.bottom)) }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(tarima.left, tarima.bottom), Size(tarima.width, 2 * u))
        val s0 = w * 0.62f; val s1 = w * 0.8f; val sm = (s0 + s1) / 2
        val costal = Path().apply {
            moveTo(s0, piso + 2 * u)
            cubicTo(s0 - 4 * u, b.top + b.height * 0.4f, s0 + 6 * u, b.top + b.height * 0.12f, sm, b.top + b.height * 0.1f)
            cubicTo(s1 - 6 * u, b.top + b.height * 0.12f, s1 + 4 * u, b.top + b.height * 0.4f, s1, piso + 2 * u)
            close()
        }
        drawPath(costal, c(COSTAL))
        drawPath(costal, c(PINO_OSC), style = Stroke(0.8f * u))
        marca(1, Offset(sm, b.top + b.height * 0.48f), b.height * 0.15f, c(LADRILLO).copy(alpha = 0.7f))
        drawLine(c(CORDEL), Offset(sm - 4 * u, b.top + b.height * 0.17f), Offset(sm + 4 * u, b.top + b.height * 0.17f), 1.4f * u)
        // De noche, la luz tibia del quinqué lo baña todo.
        if (noche) drawRect(LLAMA.copy(alpha = 0.05f), Offset.Zero, size)
    }

    /** La caja cerrada de arriba, con el rollo de cuerda, el quinqué y la rueda de carreta encima. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // La caja cerrada: el frente de tablillas clavadas en sus listones, con las marcas grandes estarcidas.
        val caja = Rect(r.left, r.top + r.height * 0.5f, r.right, r.bottom)
        drawRect(c(RENDIJA), caja.topLeft, caja.size)
        val nt = 3
        for (k in 0 until nt) {
            val tb = Rect(caja.left, caja.top + caja.height * k / nt, caja.right, caja.top + caja.height * (k + 1) / nt - 1.6f * u)
            drawRect(c(if (k % 2 == 0) PINO else mezcla(PINO, PINO_LUZ, 0.5f)), tb.topLeft, tb.size)
            vetas(tb, c(VETA).copy(alpha = 0.4f), 100 + k, densidad = 0.6f, grosor = 0.6f)
        }
        val lw = r.width * lado
        listOf(caja.left, caja.right - lw).forEach { x ->
            drawRect(c(PINO_LUZ), Offset(x, caja.top), Size(lw, caja.height))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(x, caja.top), Size(lw, caja.height), style = Stroke(1 * u))
            for (k in 0 until nt) drawCircle(c(CLAVO), 1.2f * u, Offset(x + lw / 2, caja.top + caja.height * (k + 0.5f) / nt))
        }
        drawRect(Color.White.copy(alpha = 0.12f), caja.topLeft, Size(caja.width, 1.4f * u))
        marca(0, Offset(caja.left + caja.width * 0.24f, caja.center.y), caja.height * 0.32f, c(MARINO).copy(alpha = 0.7f))
        marca(2, Offset(caja.left + caja.width * 0.46f, caja.center.y), caja.height * 0.32f, c(MARINO).copy(alpha = 0.7f))
        drawRect(c(LADRILLO).copy(alpha = 0.6f), Offset(caja.left + caja.width * 0.62f, caja.center.y - caja.height * 0.12f), Size(caja.width * 0.24f, caja.height * 0.24f))
        marca(3, Offset(caja.left + caja.width * 0.74f, caja.center.y), caja.height * 0.14f, papel(GACETA, oscuro, noche).copy(alpha = 0.85f))
        // El rollo de cuerda, tendido sobre la caja.
        val cuerda = Offset(r.left + r.width * 0.13f, caja.top - r.height * 0.06f)
        for (k in 0 until 4) {
            val rx = r.width * 0.06f - k * 2 * u; val ry = r.height * 0.06f - k * 1.2f * u
            drawOval(c(CORDEL), Offset(cuerda.x - rx, cuerda.y - ry), Size(rx * 2, ry * 2), style = Stroke(1.6f * u))
        }
        // La rueda de carreta recargada: el aro de hierro, las pinas de madera, los doce rayos y el cubo.
        val rr = r.height * 0.23f
        val rc = Offset(r.left + r.width * 0.76f, caja.top - rr + 1 * u)
        drawCircle(c(HIERRO), rr, rc, style = Stroke(2.6f * u))
        drawCircle(c(PINO_OSC), rr - 3 * u, rc, style = Stroke(3 * u))
        for (k in 0 until 12) {
            val a = k * (2 * PI.toFloat() / 12)
            drawLine(c(PINO_OSC), Offset(rc.x + rr * 0.2f * cos(a), rc.y + rr * 0.2f * sin(a)), Offset(rc.x + (rr - 4 * u) * cos(a), rc.y + (rr - 4 * u) * sin(a)), 1.8f * u)
        }
        drawCircle(c(PINO), rr * 0.22f, rc)
        drawCircle(c(HIERRO), rr * 0.22f, rc, style = Stroke(1.4f * u))
        drawCircle(c(HIERRO), rr * 0.08f, rc)
        // El quinqué: el pie, el depósito de latón con su asa, el quemador con su perilla y el tubo de vidrio.
        val qx = r.left + r.width * 0.32f
        val qb = caja.top
        val qs = r.height * 0.4f
        val vb = qb - qs * 0.42f
        val chimenea = Path().apply {
            moveTo(qx - qs * 0.07f, vb)
            cubicTo(qx - qs * 0.2f, vb - qs * 0.08f, qx - qs * 0.2f, vb - qs * 0.26f, qx - qs * 0.06f, vb - qs * 0.32f)
            lineTo(qx - qs * 0.06f, vb - qs * 0.58f); lineTo(qx + qs * 0.06f, vb - qs * 0.58f); lineTo(qx + qs * 0.06f, vb - qs * 0.32f)
            cubicTo(qx + qs * 0.2f, vb - qs * 0.26f, qx + qs * 0.2f, vb - qs * 0.08f, qx + qs * 0.07f, vb)
            close()
        }
        val llama = Offset(qx, vb - qs * 0.14f)
        if (noche) {
            mancha(llama, r.height * 0.95f, LLAMA.copy(alpha = 0.3f))
            mancha(llama, qs * 0.35f, Color(0xFFFFE6A0).copy(alpha = 0.7f))
        }
        drawArc(c(LATON), -60f, 120f, false, Offset(qx + qs * 0.08f, qb - qs * 0.28f), Size(qs * 0.2f, qs * 0.2f), style = Stroke(1.4f * u))
        drawRect(c(LATON), Offset(qx - qs * 0.16f, qb - qs * 0.06f), Size(qs * 0.32f, qs * 0.06f))
        drawOval(c(LATON), Offset(qx - qs * 0.17f, qb - qs * 0.32f), Size(qs * 0.34f, qs * 0.27f))
        drawOval(Color.White.copy(alpha = 0.25f), Offset(qx - qs * 0.11f, qb - qs * 0.28f), Size(qs * 0.08f, qs * 0.12f))
        drawRect(c(LATON), Offset(qx - qs * 0.08f, qb - qs * 0.36f), Size(qs * 0.16f, qs * 0.06f))
        drawRect(c(HIERRO), Offset(qx - qs * 0.1f, vb), Size(qs * 0.2f, qs * 0.06f))
        drawCircle(c(LATON), qs * 0.035f, Offset(qx + qs * 0.13f, vb + qs * 0.03f))
        if (noche) {
            drawPath(chimenea, LLAMA.copy(alpha = 0.3f))
            val lla = Path().apply {
                moveTo(llama.x, llama.y - qs * 0.12f)
                quadraticTo(llama.x + qs * 0.07f, llama.y, llama.x, llama.y + qs * 0.06f)
                quadraticTo(llama.x - qs * 0.07f, llama.y, llama.x, llama.y - qs * 0.12f); close()
            }
            drawPath(lla, LLAMA)
            drawCircle(Color.White.copy(alpha = 0.85f), qs * 0.02f, Offset(llama.x, llama.y + qs * 0.02f))
        } else {
            drawPath(chimenea, c(VIDRIO).copy(alpha = 0.45f))
            drawRect(c(HIERRO), Offset(qx - qs * 0.015f, vb - qs * 0.05f), Size(qs * 0.03f, qs * 0.05f))
        }
        drawPath(chimenea, c(PINO_OSC).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(qx - qs * 0.1f, vb - qs * 0.12f), Offset(qx - qs * 0.1f, vb - qs * 0.22f), 0.8f * u)
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TELAS[(v.semilla and 0x7fffffff) % TELAS.size]
        val tela = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val arriba = h * 0.05f
        // Las hojas sueltas que asoman por arriba: más cuanto más escribiste.
        val sueltas = (v.lleno * 3.5f).toInt()
        for (j in 0 until sueltas) {
            val x0 = w * (0.12f + 0.6f * azar(v.semilla, 20 + j))
            drawRect(papel(if (j % 2 == 0) GACETA else MANILA, oscuro, v.noche), Offset(x0, arriba * (0.1f + 0.5f * azar(v.semilla, 30 + j))), Size(w * 0.22f, arriba * 1.5f))
        }
        // El lomo de tela gastada, con su trama, la luz y la sombra.
        drawRoundRect(tela, Offset(0f, arriba), Size(w, h - arriba), CornerRadius(1.5f * u))
        clipRect(0f, arriba, w, h) {
            var k = -h
            while (k < w) {
                drawLine(Color.White.copy(alpha = 0.05f), Offset(k, h), Offset(k + h, arriba), 0.5f * u)
                drawLine(Color.Black.copy(alpha = 0.06f), Offset(k, arriba), Offset(k + h, h), 0.5f * u)
                k += 2.5f * u
            }
        }
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.08f, arriba), Size(w * 0.1f, h - arriba))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(w * 0.86f, arriba), Size(w * 0.14f, h - arriba))
        // Las esquinas peladas (se ve el cartón), los hilos sueltos del borde y una mancha de uso.
        listOf(Offset(0f, arriba), Offset(w * 0.8f, arriba), Offset(0f, h * 0.95f), Offset(w * 0.8f, h * 0.95f)).forEach { o -> drawRect(c(MANILA).copy(alpha = 0.45f), o, Size(w * 0.2f, h * 0.05f)) }
        for (j in 0 until 6) {
            val x = w * (0.1f + 0.15f * j)
            drawLine(papel(GACETA, oscuro, v.noche).copy(alpha = 0.4f), Offset(x, h), Offset(x + 0.6f * u, h - (1.5f + 2 * azar(v.semilla, 40 + j)) * u), 0.5f * u)
        }
        drawCircle(Color.White.copy(alpha = 0.06f), w * 0.3f, Offset(w * (0.3f + 0.4f * azar(v.semilla, 3)), h * (0.45f + 0.25f * azar(v.semilla, 4))))
        // La etiqueta de papel pegada, con su doble filete y el día; debajo, el mes a lo largo del lomo.
        val eti = Rect(w * 0.14f, h * 0.11f, w * 0.84f, h * 0.36f)
        drawRect(papel(GACETA, oscuro, v.noche), eti.topLeft, eti.size)
        drawRect(c(TINTA).copy(alpha = 0.6f), eti.deflate(1.4f * u).topLeft, eti.deflate(1.4f * u).size, style = Stroke(0.5f * u))
        drawRect(c(TINTA).copy(alpha = 0.4f), eti.deflate(2.4f * u).topLeft, eti.deflate(2.4f * u).size, style = Stroke(0.4f * u))
        textoEn(medidor, v.dia, eti.deflate(3 * u), letra, c(TINTA), 15f)
        textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.39f, w * 0.82f, h * 0.775f), letra, papel(GACETA, oscuro, v.noche).copy(alpha = 0.85f), 9.5f, vertical = true, minimo = 4f)
        // El hilo que lo ata, torcido, con su nudo y las dos puntas colgando.
        val hy = h * 0.8f
        drawLine(c(CORDEL), Offset(0f, hy), Offset(w, hy), 1.3f * u)
        var tx = 0f
        while (tx < w - 1 * u) { drawLine(c(PINO_OSC).copy(alpha = 0.6f), Offset(tx, hy - 0.6f * u), Offset(tx + 0.8f * u, hy + 0.6f * u), 0.4f * u); tx += 2 * u }
        val nudo = Offset(w * 0.55f, hy)
        drawCircle(c(CORDEL), 1.8f * u, nudo)
        drawLine(c(CORDEL), nudo, Offset(nudo.x - 2.5f * u, h * 0.93f), 1f * u)
        drawLine(c(CORDEL), nudo, Offset(nudo.x + 2 * u, h * 0.95f), 1f * u)
        // El sello de la tienda, en tinta (de noche, una luna y dos estrellas).
        val so = Offset(w * 0.26f, h * 0.89f)
        val sr = minOf(w * 0.14f, 4 * u)
        if (v.noche) {
            drawCircle(papel(GACETA, oscuro, true), sr, so)
            drawCircle(tela, sr * 0.8f, Offset(so.x + sr * 0.45f, so.y - sr * 0.3f))
            drawCircle(papel(GACETA, oscuro, true), 0.6f * u, Offset(so.x + sr * 1.7f, so.y + sr * 0.5f))
            drawCircle(papel(GACETA, oscuro, true), 0.5f * u, Offset(so.x - sr * 0.4f, so.y - sr * 1.6f))
        } else {
            drawCircle(c(LADRILLO).copy(alpha = 0.7f), sr, so, style = Stroke(0.8f * u))
            drawCircle(c(LADRILLO).copy(alpha = 0.7f), sr * 0.3f, so)
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.23f, 0.345f, 0.79f, 0.665f)
    override val letraTapa: FontFamily = LetraExtra.oswald
    override val tamTapa = 13f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /**
     * El libro de cuentas del día, de frente: forrado de tela gastada, con las esquinas peladas (se ve el cartón), los hilos
     * sueltos del borde, una mancha de uso y una marca de embarque desteñida; asoman por arriba más hojas sueltas cuanto más
     * escribiste. En el centro, la etiqueta de papel con su doble filete (el nombre del libro, la fecha, lo escrito y las
     * palabras) y el sello de la tienda; el hilo lo ata a lo alto y a lo ancho con su nudo. Al abrirse, el hilo se suelta, la
     * tapa gira sobre el lomo y deja ver la hoja rayada de cuentas. De noche, la luz tibia del quinqué y una luna por sello.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val tela = c(TELAS[(e.semilla and 0x7fffffff) % TELAS.size])
        val gaceta = papel(GACETA, oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val libro = Rect(w * 0.015f, h * 0.06f, w * 0.95f, h * 0.935f)
        val lomo = Rect(libro.left, libro.top, libro.left + w * 0.08f, libro.bottom)
        val cara = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        // La sombra, las hojas sueltas que asoman por arriba (más cuanto más escribiste) y el canto de las hojas.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(libro.left + 4 * u, libro.top + 5 * u), Size(libro.width + 2 * u, libro.height + 2 * u))
        val sueltas = (e.palabras / 800f * 3.5f).toInt().coerceIn(0, 4)
        for (j in 0 until sueltas) {
            val x0 = cara.left + cara.width * (0.08f + 0.7f * azar(e.semilla, 20 + j))
            rotate(-4f + 8f * azar(e.semilla, 25 + j), Offset(x0, libro.top)) {
                drawRect(papel(if (j % 2 == 0) GACETA else MANILA, oscuro, e.noche), Offset(x0, libro.top - h * (0.03f + 0.025f * azar(e.semilla, 30 + j))), Size(w * 0.13f, h * 0.1f))
                drawLine(tinta.copy(alpha = 0.3f), Offset(x0 + 3 * u, libro.top - h * 0.015f), Offset(x0 + w * 0.1f, libro.top - h * 0.015f), 0.6f * u)
            }
        }
        val hojas = Rect(libro.left + w * 0.02f, libro.top + h * 0.025f, libro.right + 3 * u, libro.bottom + 3.5f * u)
        drawRect(mezcla(gaceta, Color.Black, 0.12f), hojas.topLeft, hojas.size)
        for (j in 1..4) drawLine(Color.Black.copy(alpha = 0.16f), Offset(hojas.left + 2 * u, libro.bottom + j * 0.8f * u), Offset(hojas.right - 1 * u, libro.bottom + j * 0.8f * u), 0.5f * u)
        // Dentro (se ve al abrir): la hoja rayada de cuentas, con su cabecera, sus renglones azules y las columnas rojas.
        if (a > 0.01f) {
            drawRect(gaceta, cara.topLeft, cara.size)
            drawRect(c(MARINO).copy(alpha = 0.25f), cara.topLeft, Size(cara.width, h * 0.1f))
            var y = cara.top + h * 0.14f
            while (y < cara.bottom) { drawLine(c(MARINO).copy(alpha = 0.3f), Offset(cara.left, y), Offset(cara.right, y), 0.6f * u); y += 7.5f * u }
            listOf(0.12f, 0.66f, 0.82f).forEach { f ->
                drawLine(c(LADRILLO).copy(alpha = 0.55f), Offset(cara.left + cara.width * f, cara.top), Offset(cara.left + cara.width * f, cara.bottom), 0.7f * u)
                drawLine(c(LADRILLO).copy(alpha = 0.55f), Offset(cara.left + cara.width * f + 2 * u, cara.top), Offset(cara.left + cara.width * f + 2 * u, cara.bottom), 0.7f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.15f), cara.topLeft, Size(8 * u, cara.height))
        }
        // La tapa de tela gastada, que gira sobre el lomo (el hilo se suelta al empezar).
        val giro = cos(a * 1.6f).coerceAtLeast(0.02f)
        scale(giro, 1f, Offset(cara.left, cara.center.y)) {
            drawRect(Brush.verticalGradient(listOf(mezcla(tela, Color.White, 0.08f), tela, mezcla(tela, Color.Black, 0.2f)), cara.top, cara.bottom), cara.topLeft, cara.size)
            // La trama de la tela, en diagonal.
            clipRect(cara.left, cara.top, cara.right, cara.bottom) {
                var k = -cara.height
                while (k < cara.width) {
                    drawLine(Color.White.copy(alpha = 0.045f), Offset(cara.left + k, cara.bottom), Offset(cara.left + k + cara.height, cara.top), 0.6f * u)
                    drawLine(Color.Black.copy(alpha = 0.05f), Offset(cara.left + k, cara.top), Offset(cara.left + k + cara.height, cara.bottom), 0.6f * u)
                    k += 3 * u
                }
            }
            // La mancha de uso, la marca de embarque desteñida y las esquinas peladas.
            drawCircle(Color.White.copy(alpha = 0.05f), cara.height * 0.3f, Offset(cara.left + cara.width * (0.2f + 0.6f * azar(e.semilla, 3)), cara.top + cara.height * 0.75f))
            marca((e.semilla ushr 4) and 0xff, Offset(cara.left + cara.width * 0.065f, h * 0.87f), 7 * u, gaceta.copy(alpha = 0.3f))
            val pe = 14 * u
            listOf(Triple(cara.right, cara.top, -1f to 1f), Triple(cara.right, cara.bottom, -1f to -1f)).forEachIndexed { i, (x, y, s) ->
                drawPath(Path().apply {
                    moveTo(x, y); lineTo(x + s.first * pe * (0.8f + 0.4f * azar(e.semilla, 50 + i)), y)
                    lineTo(x + s.first * pe * 0.45f, y + s.second * pe * 0.35f); lineTo(x, y + s.second * pe * (0.8f + 0.4f * azar(e.semilla, 55 + i))); close()
                }, papel(MANILA, oscuro, e.noche).copy(alpha = 0.8f))
            }
            // Los hilos sueltos del borde, abajo y a la derecha.
            for (j in 0 until 18) {
                val x = cara.left + cara.width * (0.03f + 0.94f * azar(e.semilla, 60 + j))
                drawLine(gaceta.copy(alpha = 0.35f), Offset(x, cara.bottom), Offset(x + (azar(e.semilla, 80 + j) - 0.5f) * 3 * u, cara.bottom + (1.5f + 2.5f * azar(e.semilla, 70 + j)) * u), 0.5f * u)
            }
            for (j in 0 until 8) {
                val y = cara.top + cara.height * (0.1f + 0.8f * azar(e.semilla, 90 + j))
                drawLine(gaceta.copy(alpha = 0.35f), Offset(cara.right, y), Offset(cara.right + (1.5f + 2 * azar(e.semilla, 95 + j)) * u, y + 1 * u), 0.5f * u)
            }
            // La etiqueta de papel pegada, con su doble filete.
            val eti = Rect(w * 0.2f, h * 0.15f, w * 0.82f, h * 0.8f)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(eti.left + 1.5f * u, eti.top + 2 * u), eti.size)
            drawRect(gaceta, eti.topLeft, eti.size)
            drawRect(tinta.copy(alpha = 0.6f), eti.deflate(3 * u).topLeft, eti.deflate(3 * u).size, style = Stroke(1 * u))
            drawRect(tinta.copy(alpha = 0.4f), eti.deflate(5 * u).topLeft, eti.deflate(5 * u).size, style = Stroke(0.5f * u))
            textoEn(medidor, "LIBRO DE CUENTAS", Rect(eti.left + 8 * u, h * 0.175f, eti.right - 8 * u, h * 0.235f), letra, tinta.copy(alpha = 0.7f), 9.5f, minimo = 6f)
            textoEn(medidor, e.fecha, Rect(eti.left + 8 * u, h * 0.235f, eti.right - 8 * u, h * 0.315f), letra, tinta, 13.5f, minimo = 7f)
            listOf(-1f, 1f).forEach { s -> drawLine(tinta.copy(alpha = 0.5f), Offset(eti.center.x + s * 5 * u, h * 0.328f), Offset(eti.center.x + s * eti.width * 0.28f, h * 0.328f), 0.7f * u) }
            drawPath(estrella(Offset(eti.center.x, h * 0.328f), 3 * u, 4, 0.4f), tinta.copy(alpha = 0.6f))
            drawLine(tinta.copy(alpha = 0.4f), Offset(eti.left + eti.width * 0.25f, h * 0.68f), Offset(eti.right - eti.width * 0.25f, h * 0.68f), 0.6f * u)
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(eti.left + eti.width * 0.25f, h * 0.69f, eti.right - eti.width * 0.25f, h * 0.765f), letra, tinta.copy(alpha = 0.75f), 10.5f, minimo = 6f)
            // El sello de la tienda en la esquina de la etiqueta; de noche, una luna y dos estrellas.
            val so = Offset(eti.right - 17 * u, h * 0.725f)
            val sr = 8 * u
            if (e.noche) {
                drawCircle(c(LADRILLO).copy(alpha = 0.7f), sr * 0.7f, so)
                drawCircle(gaceta, sr * 0.58f, Offset(so.x + sr * 0.32f, so.y - sr * 0.22f))
                drawPath(estrella(Offset(so.x - sr * 1.1f, so.y - sr * 0.6f), 1.8f * u, 4, 0.4f), c(LADRILLO).copy(alpha = 0.7f))
                drawPath(estrella(Offset(so.x - sr * 0.6f, so.y + sr * 0.8f), 1.4f * u, 4, 0.4f), c(LADRILLO).copy(alpha = 0.7f))
            } else {
                drawCircle(c(LADRILLO).copy(alpha = 0.7f), sr, so, style = Stroke(1.2f * u))
                drawCircle(c(LADRILLO).copy(alpha = 0.55f), sr * 0.72f, so, style = Stroke(0.6f * u))
                drawCircle(c(LADRILLO).copy(alpha = 0.7f), sr * 0.28f, so)
                for (j in 0 until 8) rotate(j * 45f, so) { drawLine(c(LADRILLO).copy(alpha = 0.55f), Offset(so.x, so.y - sr * 0.45f), Offset(so.x, so.y - sr * 0.6f), 0.8f * u) }
            }
            // El hilo que lo ata a lo alto y a lo ancho, torcido, con su nudo y las puntas colgando; al abrir se suelta.
            val suelta = (1f - a * 2.5f).coerceIn(0f, 1f)
            if (suelta > 0f) {
                val hilo = c(CORDEL).copy(alpha = suelta)
                val hx = w * 0.885f; val hy = h * 0.865f
                drawLine(hilo, Offset(hx, cara.top - 2 * u), Offset(hx, cara.bottom + 2 * u), 1.6f * u)
                drawLine(hilo, Offset(cara.left, hy), Offset(cara.right + 2 * u, hy), 1.6f * u)
                var ty = cara.top
                while (ty < cara.bottom) { drawLine(c(PINO_OSC).copy(alpha = 0.6f * suelta), Offset(hx - 0.7f * u, ty), Offset(hx + 0.7f * u, ty + 1 * u), 0.5f * u); ty += 2.4f * u }
                var tx = cara.left
                while (tx < cara.right) { drawLine(c(PINO_OSC).copy(alpha = 0.6f * suelta), Offset(tx, hy - 0.7f * u), Offset(tx + 1 * u, hy + 0.7f * u), 0.5f * u); tx += 2.4f * u }
                drawCircle(hilo, 3 * u, Offset(hx, hy))
                drawCircle(c(PINO_OSC).copy(alpha = 0.5f * suelta), 3 * u, Offset(hx, hy), style = Stroke(0.6f * u))
                drawPath(Path().apply { moveTo(hx, hy); quadraticTo(hx - 6 * u, hy + 8 * u, hx - 9 * u, h * 0.99f) }, hilo, style = Stroke(1.3f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(hx, hy); quadraticTo(hx + 7 * u, hy + 6 * u, hx + 6 * u, h * 0.985f) }, hilo, style = Stroke(1.3f * u, cap = StrokeCap.Round))
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * a), cara.topLeft, cara.size)
        }
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * sin(a * PI.toFloat())), Offset(cara.left + cara.width * giro, cara.top), Size(10 * u, cara.height))
        // El lomo de tela, más gastado, con su pliegue, dos bandas desteñidas y las puntas peladas.
        drawRect(mezcla(tela, Color.Black, 0.2f), lomo.topLeft, lomo.size)
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.25f, lomo.height))
        listOf(0.16f, 0.84f).forEach { f -> drawRect(gaceta.copy(alpha = 0.25f), Offset(lomo.left, lomo.top + lomo.height * f - 2 * u), Size(lomo.width, 4 * u)) }
        listOf(lomo.top, lomo.bottom - 5 * u).forEach { y -> drawRect(papel(MANILA, oscuro, e.noche).copy(alpha = 0.6f), Offset(lomo.left, y), Size(lomo.width * 0.7f, 5 * u)) }
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(lomo.right, lomo.top), Offset(lomo.right, lomo.bottom), 1.2f * u)
        drawLine(Color.White.copy(alpha = 0.1f), Offset(lomo.right + 1.5f * u, lomo.top), Offset(lomo.right + 1.5f * u, lomo.bottom), 0.8f * u)
        // De noche, la luz tibia del quinqué, que llega desde arriba a la izquierda.
        if (e.noche) clipRect(0f, 0f, w, h) { mancha(Offset(w * 0.12f, 0f), w * 0.6f, LLAMA.copy(alpha = 0.2f)) }
    }
}

/**
 * Ciudad verde: una librería de madera medio caída que la naturaleza tomó, en una sala sin techo. Detrás, el hormigón con
 * manchas de humedad; el fondo de la librería es de tablas a lo alto y faltan algunas. A la izquierda creció un tronco
 * pegado al costado, con su corteza, un nudo y las raíces que se extienden por el suelo; el costado derecho está rajado, con
 * musgo y hongos de repisa. Las tablas están combadas por la punta, con musgo en el canto, y de ellas cuelga la hiedra; cada
 * mes va en un trozo de cartón roto atado con cordel a un clavo. Arriba, la losa rota deja ver el cielo por un hueco (con sus
 * varillas al aire); la cornisa sigue entera a la izquierda y a la derecha se rompió y un trozo cuelga de un clavo; una rama
 * entra con sus hojas. Abajo, tierra con hojarasca y pasto. De día entra un rayo de sol por el hueco. Cada entrada es un libro
 * hinchado de humedad (se abomba más cuanto más escribiste), con cercos de agua, su etiqueta desteñida con el día, el mes a lo
 * largo del lomo, notas atadas con cordel y musgo al pie; de noche lleva una luna y una luciérnaga en lugar del marbete. De
 * noche hay luciérnagas y la luna asoma por el techo roto. Distinto del Ideario, que es la estantería de metal rescatada.
 * La tapa del diario es el libro hinchado de frente, con la hiedra, el musgo, la etiqueta desteñida y las notas; al
 * abrirse, gira sobre el lomo.
 */
object EstanteRuinas : DisenoBiblioteca {
    private val MADERA = Color(0xFF7A5A3C)
    private val MADERA_LUZ = Color(0xFF9A7852)
    private val MADERA_OSC = Color(0xFF4A3524)
    private val VETA = Color(0xFF3A2A1C)
    private val HORMIGON = Color(0xFF8C8D84)
    private val HORMIGON_OSC = Color(0xFF65665E)
    private val VARILLA = Color(0xFF7A4426)
    private val MUSGO = Color(0xFF55722A)
    private val MUSGO_LUZ = Color(0xFF86A33C)
    private val HOJA = Color(0xFF3D6A2A)
    private val HOJA_LUZ = Color(0xFF6E9A3A)
    private val CORTEZA = Color(0xFF5A4636)
    private val CORTEZA_OSC = Color(0xFF382A20)
    private val HONGO = Color(0xFFC9B58A)
    private val CIELO = Color(0xFFC4DCE6)
    private val CIELO_NOCHE = Color(0xFF0C1626)
    private val SOL = Color(0xFFFFE6A0)
    private val PAPEL = Color(0xFFE2D9BF)
    private val CARTON = Color(0xFFC9B48E)
    private val CORDEL = Color(0xFFB39A70)
    private val TINTA = Color(0xFF2A2620)
    private val TIERRA = Color(0xFF4A3B2A)
    private val LUNA = Color(0xFFEDEBD8)
    private val LUCIERNAGA = Color(0xFFE6FF7A)
    /** La hojarasca del suelo: ocre, pardo, naranja seco y verde. */
    private val HOJARASCA = listOf(Color(0xFF8A6A30), Color(0xFF6E5A2A), Color(0xFFA0702E), Color(0xFF3D6A2A))
    /** Los libros, desteñidos: castaño, azul pizarra, oliva, arena y ciruela. */
    private val LIBROS = listOf(Color(0xFF6E4A3A), Color(0xFF4A5A6E), Color(0xFF5E6A3A), Color(0xFF7A6A4A), Color(0xFF5A3A4A))
    private val NOCHE = Color(0xFF0E1A22)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luna. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0B0D0A), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    /** El papel con texto: se hunde menos, para que se lea en oscuro y de noche. */
    private fun papel(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0B0D0A), 0.15f)
        if (noche) r = mezcla(r, NOCHE, 0.22f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.13f
    override val lado = 0.07f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.overpass

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.74f + 0.18f * azar(semilla, 8)

    /** Una mata de musgo: puntitos de dos verdes alrededor de [o], a lo ancho de [ancho]. */
    private fun DrawScope.musgo(o: Offset, ancho: Float, semilla: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        for (k in 0 until 14) {
            val p = Offset(o.x + (azar(semilla, 200 + k) - 0.5f) * ancho * 2, o.y + (azar(semilla, 300 + k) - 0.6f) * 5 * u)
            drawCircle(t(if (k % 3 == 0) MUSGO_LUZ else MUSGO, oscuro, noche), (1.2f + 1.4f * azar(semilla, 400 + k)) * u, p)
        }
    }

    /** Una guía de hiedra que cuelga desde [o], de [largo] px, con sus hojas alternadas a los lados. */
    private fun DrawScope.hiedra(o: Offset, largo: Float, semilla: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        val a = (azar(semilla, 1) - 0.5f) * 14 * u
        val b = (azar(semilla, 2) - 0.5f) * 8 * u
        drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + a, o.y + largo * 0.5f, o.x + b, o.y + largo) }, t(CORTEZA, oscuro, noche), style = Stroke(0.9f * u))
        val n = (largo / (7 * u)).toInt().coerceAtLeast(2)
        for (k in 1..n) {
            val f = k / n.toFloat()
            val p = Offset(o.x + 2 * (1 - f) * f * a + f * f * b, o.y + largo * f)
            val hacia = if (k % 2 == 0) -1f else 1f
            drawPath(estrella(Offset(p.x + hacia * 3 * u, p.y + 1 * u), 3.2f * u, 5, 0.62f, giro = -90f + hacia * 20f), t(if (k % 3 == 0) HOJA_LUZ else HOJA, oscuro, noche))
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // La pared de hormigón de la ruina, con manchas de humedad.
        drawRect(c(HORMIGON), Offset.Zero, size)
        for (k in 0 until 6) {
            val o = Offset(w * azar(k, 41), h * azar(k, 42))
            drawOval(Color.Black.copy(alpha = 0.08f), Offset(o.x - w * 0.12f, o.y - 20 * u), Size(w * 0.24f, 60 * u))
        }
        // El fondo de la librería: tablas a lo alto; faltan algunas y se ve el hormigón rajado.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        val anchoTabla = 30 * u
        var x = cuerpo.left; var k = 0
        while (x < cuerpo.right) {
            val x1 = minOf(x + anchoTabla, cuerpo.right)
            if (azar(k, 51) > 0.18f) {
                val tb = Rect(x + 0.6f * u, cuerpo.top, x1 - 0.6f * u, cuerpo.bottom)
                drawRect(c(if (k % 2 == 0) MADERA_OSC else mezcla(MADERA_OSC, MADERA, 0.35f)), tb.topLeft, tb.size)
                vetas(tb, c(VETA).copy(alpha = 0.4f), 60 + k, densidad = 0.5f, vertical = true, grosor = 0.6f)
            } else {
                drawLine(c(HORMIGON_OSC), Offset(x + anchoTabla * 0.3f, cuerpo.top), Offset(x + anchoTabla * 0.6f, cuerpo.bottom), 1 * u)
            }
            x = x1; k++
        }
        // Cada repisa: la sombra de la tabla de arriba y la hiedra que cuelga de ella.
        d.repisas.forEachIndexed { j, r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.28f), r.topLeft, Size(r.width, 9 * u))
            for (m in 0 until 2) {
                val s = j * 3 + m
                if (azar(s, 61) < 0.35f) continue
                hiedra(Offset(r.left + r.width * (0.1f + 0.8f * azar(s, 62)), r.top), r.height * (0.3f + 0.4f * azar(s, 63)), s, oscuro, noche)
            }
        }
        corona(d.corona, oscuro, noche)
        // El costado derecho: la tabla rajada, con musgo y hongos de repisa.
        val der = d.derecha
        drawRect(c(MADERA), der.topLeft, der.size)
        vetas(der, c(VETA).copy(alpha = 0.5f), 71, densidad = 1f, vertical = true, grosor = 0.7f)
        drawRect(c(MADERA_LUZ), der.topLeft, Size(2 * u, der.height))
        val raja = Path().apply {
            moveTo(der.left + der.width * 0.5f, der.top)
            var yy = der.top; var j = 0
            while (yy < der.top + der.height * 0.6f) { yy += 14 * u; lineTo(der.left + der.width * (if (j % 2 == 0) 0.38f else 0.58f), yy); j++ }
        }
        drawPath(raja, c(VETA), style = Stroke(1.4f * u))
        d.repisas.forEachIndexed { j, rep ->
            if (azar(j, 72) > 0.4f) musgo(Offset(der.center.x, rep.bottom - 6 * u), der.width * 0.45f, j, oscuro, noche)
            if (azar(j, 73) > 0.6f) drawArc(c(HONGO), 90f, 180f, true, Offset(der.left - 5 * u, rep.center.y - 3 * u), Size(10 * u, 6 * u))
        }
        // Las tablas, combadas en la punta algunas, con el musgo que crece en el canto.
        d.tablas.forEachIndexed { j, r ->
            val caida = if (azar(j, 74) > 0.5f) 3 * u else 0f
            val tb = Path().apply {
                moveTo(r.left, r.top); lineTo(r.left + r.width * 0.7f, r.top)
                quadraticTo(r.left + r.width * 0.88f, r.top, r.right, r.top + caida)
                lineTo(r.right, r.bottom + caida)
                quadraticTo(r.left + r.width * 0.88f, r.bottom, r.left + r.width * 0.7f, r.bottom)
                lineTo(r.left, r.bottom); close()
            }
            drawPath(tb, c(MADERA))
            clipPath(tb) { vetas(Rect(r.left, r.top, r.right, r.bottom + caida), c(VETA).copy(alpha = 0.45f), 90 + j, densidad = 0.7f, grosor = 0.7f) }
            drawLine(c(MADERA_LUZ), Offset(r.left, r.top + 0.6f * u), Offset(r.left + r.width * 0.7f, r.top + 0.6f * u), 1.2f * u)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            var mx = r.left + 4 * u; var m = 0
            while (mx < r.right - 4 * u) {
                if (azar(j * 50 + m, 75) > 0.55f) musgo(Offset(mx, r.top + 1 * u), 6 * u, j * 50 + m, oscuro, noche)
                mx += 16 * u; m++
            }
        }
        // El rótulo de cada mes: un trozo de cartón roto, atado con cordel a un clavo.
        d.placas.forEachIndexed { j, p ->
            val r = p.r
            val clavo = Offset(r.center.x, r.top - 1 * u)
            rotate(if (j % 2 == 0) -1.5f else 1.2f, r.center) {
                val carton = contornoIrregular(r, 120 + j, 24, 0.06f)
                drawPath(carton, papel(CARTON, oscuro, noche))
                drawPath(carton, c(MADERA_OSC).copy(alpha = 0.5f), style = Stroke(0.6f * u))
                textoEn(medidor, p.texto.lowercase(), r.deflate(2 * u), letra, c(TINTA), 10.5f)
            }
            drawLine(c(CORDEL), clavo, Offset(r.left + r.width * 0.2f, r.top + 2 * u), 0.8f * u)
            drawLine(c(CORDEL), clavo, Offset(r.right - r.width * 0.2f, r.top + 2 * u), 0.8f * u)
            drawCircle(c(VARILLA), 1.4f * u, clavo)
        }
        // La base: el zócalo podrido y el suelo de tierra con hojarasca y matas de pasto.
        val b = d.base
        val piso = b.top + b.height * 0.35f
        drawRect(c(MADERA_OSC), b.topLeft, Size(b.width, piso - b.top))
        drawRect(c(TIERRA), Offset(0f, piso), Size(w, h - piso))
        for (j in 0 until 40) {
            val o = Offset(w * azar(j, 111), piso + (h - piso) * azar(j, 112))
            rotate(360f * azar(j, 113), o) { drawOval(c(HOJARASCA[j % HOJARASCA.size]), Offset(o.x - 3 * u, o.y - 1.5f * u), Size(6 * u, 3 * u)) }
        }
        for (j in 0 until 14) {
            val x0 = w * azar(j, 121)
            for (m in -1..1) drawLine(c(HOJA_LUZ), Offset(x0, piso + 2 * u), Offset(x0 + m * 3 * u, piso - (4 + 4 * azar(j * 3 + m + 3, 122)) * u), 1 * u)
        }
        // El tronco que creció pegado al costado izquierdo y lo cubre: corteza con sus surcos, un nudo y las raíces en el suelo.
        val izq = d.izquierda
        val tronco = Rect(0f, d.corona.top, izq.right + izq.width * 0.4f, d.base.bottom)
        val tr = Path().apply {
            moveTo(tronco.left, tronco.top)
            lineTo(tronco.right - tronco.width * 0.1f, tronco.top)
            quadraticTo(tronco.right + tronco.width * 0.08f, tronco.top + tronco.height * 0.5f, tronco.right, d.base.top)
            quadraticTo(tronco.right + tronco.width * 0.5f, d.base.top + d.base.height * 0.6f, tronco.right + tronco.width * 1.4f, tronco.bottom)
            lineTo(tronco.left, tronco.bottom); close()
        }
        drawPath(tr, c(CORTEZA))
        clipPath(tr) {
            vetas(Rect(tronco.left, tronco.top, tronco.right + tronco.width * 1.4f, tronco.bottom), c(CORTEZA_OSC).copy(alpha = 0.7f), 81, densidad = 0.6f, vertical = true, grosor = 1.4f, ondulacion = 2f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(tronco.right - tronco.width * 0.25f, tronco.top), Size(tronco.width * 0.4f, tronco.height))
            drawRect(Color.White.copy(alpha = 0.06f), Offset(tronco.left, tronco.top), Size(tronco.width * 0.2f, tronco.height))
        }
        val nudo = Offset(tronco.center.x, d.corona.bottom + (d.base.top - d.corona.bottom) * 0.35f)
        drawOval(c(CORTEZA_OSC), Offset(nudo.x - tronco.width * 0.18f, nudo.y - 6 * u), Size(tronco.width * 0.36f, 12 * u))
        drawOval(c(CORTEZA), Offset(nudo.x - tronco.width * 0.1f, nudo.y - 3 * u), Size(tronco.width * 0.2f, 6 * u), style = Stroke(1 * u))
        listOf(1.6f, 2.4f, 3.4f).forEachIndexed { j, f ->
            val raiz = Path().apply {
                moveTo(izq.width * 0.6f, piso)
                quadraticTo(izq.width * f * 0.7f, piso + (h - piso) * 0.2f, izq.width * f, piso + (h - piso) * (0.4f + 0.2f * j))
            }
            drawPath(raiz, c(CORTEZA), style = Stroke((3.5f - j) * u, cap = StrokeCap.Round))
        }
        musgo(Offset(tronco.center.x, d.base.top - 4 * u), tronco.width * 0.4f, 82, oscuro, noche)
        // De día, el rayo de sol que entra por el hueco del techo; de noche, las luciérnagas.
        if (noche) {
            val n = (h / (40 * u)).toInt().coerceIn(8, 60)
            for (j in 0 until n) {
                val o = Offset(w * (0.1f + 0.85f * azar(j, 131)), h * azar(j, 132))
                drawCircle(LUCIERNAGA.copy(alpha = 0.12f), 5 * u, o)
                drawCircle(LUCIERNAGA.copy(alpha = 0.35f), 2.2f * u, o)
                drawCircle(Color.White.copy(alpha = 0.9f), 0.9f * u, o)
            }
        } else {
            val y0 = d.corona.top + d.corona.height * 0.2f
            val rayo = Path().apply { moveTo(w * 0.5f, y0); lineTo(w * 0.8f, y0); lineTo(w * 0.55f, h); lineTo(w * 0.1f, h); close() }
            drawPath(rayo, SOL.copy(alpha = if (oscuro) 0.07f else 0.12f))
            for (j in 0 until 18) {
                val f = azar(j, 141)
                val o = Offset(w * (0.5f - 0.4f * f + 0.28f * azar(j, 142)), y0 + (h - y0) * f)
                drawCircle(SOL.copy(alpha = 0.5f), 0.7f * u, o)
            }
        }
    }

    /** El techo roto con su hueco, la cornisa medio caída, la rama y la hiedra. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // La losa del techo, rota: por el hueco se ve el cielo (de día, con el sol; de noche, la luna y estrellas).
        drawRect(c(HORMIGON_OSC), r.topLeft, Size(r.width, r.height * 0.55f))
        val hueco = Path().apply {
            moveTo(r.left + r.width * 0.42f, r.top); lineTo(r.left + r.width * 0.86f, r.top)
            lineTo(r.left + r.width * 0.83f, r.top + r.height * 0.14f); lineTo(r.left + r.width * 0.88f, r.top + r.height * 0.22f)
            lineTo(r.left + r.width * 0.76f, r.top + r.height * 0.4f); lineTo(r.left + r.width * 0.64f, r.top + r.height * 0.34f)
            lineTo(r.left + r.width * 0.55f, r.top + r.height * 0.46f); lineTo(r.left + r.width * 0.47f, r.top + r.height * 0.3f)
            lineTo(r.left + r.width * 0.38f, r.top + r.height * 0.18f); close()
        }
        drawPath(hueco, if (noche) CIELO_NOCHE else c(CIELO))
        clipPath(hueco) {
            if (noche) {
                for (k in 0 until 24) drawCircle(LUNA.copy(alpha = 0.4f + 0.5f * azar(k, 91)), (0.5f + 0.8f * azar(k, 92)) * u, Offset(r.left + r.width * (0.38f + 0.5f * azar(k, 93)), r.top + r.height * 0.46f * azar(k, 94)))
                val lc = Offset(r.left + r.width * 0.7f, r.top + r.height * 0.16f)
                mancha(lc, r.height * 0.3f, LUNA.copy(alpha = 0.35f))
                drawCircle(LUNA, r.height * 0.08f, lc)
                drawCircle(Color.Black.copy(alpha = 0.08f), r.height * 0.025f, Offset(lc.x - r.height * 0.02f, lc.y + r.height * 0.02f))
            } else {
                mancha(Offset(r.left + r.width * 0.72f, r.top + r.height * 0.06f), r.height * 0.4f, SOL.copy(alpha = 0.7f))
            }
        }
        drawPath(hueco, c(HORMIGON), style = Stroke(2 * u))
        listOf(0.48f to 0.32f, 0.6f to 0.38f, 0.8f to 0.3f).forEach { (fx, fy) ->
            drawLine(c(VARILLA), Offset(r.left + r.width * fx, r.top + r.height * fy), Offset(r.left + r.width * (fx + 0.03f), r.top + r.height * (fy + 0.12f)), 1.2f * u)
        }
        // La cornisa de la librería: entera a la izquierda, rota a la derecha; lo que queda es la tabla de arriba.
        val cy0 = r.top + r.height * 0.62f
        val resto = Rect(r.left + r.width * 0.6f, r.bottom - r.height * 0.1f, r.right, r.bottom)
        drawRect(c(MADERA_OSC), resto.topLeft, resto.size)
        val cornisa = Path().apply {
            moveTo(r.left, cy0); lineTo(r.left + r.width * 0.58f, cy0)
            lineTo(r.left + r.width * 0.61f, cy0 + r.height * 0.08f); lineTo(r.left + r.width * 0.57f, cy0 + r.height * 0.14f)
            lineTo(r.left + r.width * 0.62f, r.bottom); lineTo(r.left, r.bottom); close()
        }
        drawPath(cornisa, c(MADERA))
        clipPath(cornisa) { vetas(Rect(r.left, cy0, r.right, r.bottom), c(VETA).copy(alpha = 0.45f), 131, densidad = 0.8f, grosor = 0.7f) }
        drawLine(c(MADERA_LUZ), Offset(r.left, cy0), Offset(r.left + r.width * 0.58f, cy0), 2 * u)
        drawLine(c(MADERA_OSC), Offset(r.left, cy0 + r.height * 0.12f), Offset(r.left + r.width * 0.57f, cy0 + r.height * 0.12f), 1.2f * u)
        musgo(Offset(r.left + r.width * 0.3f, cy0), r.width * 0.12f, 150, oscuro, noche)
        // El trozo de cornisa que se rompió, colgando de un clavo por la punta derecha.
        val trozo = Rect(r.left + r.width * 0.7f, cy0, r.left + r.width * 0.96f, cy0 + r.height * 0.14f)
        rotate(-14f, Offset(trozo.right, trozo.bottom)) {
            drawRect(c(MADERA), trozo.topLeft, trozo.size)
            drawLine(c(MADERA_LUZ), trozo.topLeft, Offset(trozo.right, trozo.top), 1.6f * u)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(trozo.left, trozo.bottom - trozo.height * 0.3f), Size(trozo.width, trozo.height * 0.3f))
        }
        drawCircle(c(VARILLA), 1.3f * u, Offset(trozo.right - 2 * u, trozo.bottom - 2 * u))
        // La rama del árbol que entra por arriba, con sus hojas.
        val rama = Path().apply { moveTo(r.left, r.top + r.height * 0.5f); quadraticTo(r.left + r.width * 0.2f, r.top + r.height * 0.2f, r.left + r.width * 0.46f, r.top + r.height * 0.24f) }
        drawPath(rama, c(CORTEZA), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        for (k in 0 until 26) {
            val f = azar(k, 101)
            val px = r.left + r.width * (2 * (1 - f) * f * 0.2f + f * f * 0.46f)
            val py = r.top + r.height * ((1 - f) * (1 - f) * 0.5f + 2 * (1 - f) * f * 0.2f + f * f * 0.24f)
            val o = Offset(px + (azar(k, 102) - 0.5f) * 18 * u, py + (azar(k, 103) - 0.5f) * 14 * u)
            drawPath(estrella(o, (3 + 2 * azar(k, 104)) * u, 5, 0.6f, giro = 360f * azar(k, 105)), c(if (k % 3 == 0) HOJA_LUZ else HOJA))
        }
        // La hiedra que cuelga de la cornisa.
        listOf(0.14f, 0.3f, 0.5f).forEachIndexed { k, f -> hiedra(Offset(r.left + r.width * f, r.bottom - r.height * 0.02f), r.height * (0.25f + 0.2f * k), 140 + k, oscuro, noche) }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = LIBROS[(v.semilla and 0x7fffffff) % LIBROS.size]
        val cuero = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El lomo hinchado de humedad: los cantos se abomban (más cuanto más escribiste) y la cabeza ondula.
        val panza = w * (0.05f + 0.07f * v.lleno)
        val lomo = Path().apply {
            moveTo(panza, h * 0.02f)
            quadraticTo(w * 0.3f, 0f, w * 0.5f, h * 0.02f)
            quadraticTo(w * 0.72f, h * 0.04f, w - panza, h * 0.015f)
            quadraticTo(w + panza, h * 0.5f, w - panza, h)
            lineTo(panza, h)
            quadraticTo(-panza, h * 0.5f, panza, h * 0.02f)
            close()
        }
        drawPath(lomo, cuero)
        clipPath(lomo) {
            // La luz y la sombra, los nervios abombados y los cercos de agua.
            drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.08f, 0f), Size(w * 0.14f, h))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(w * 0.82f, 0f), Size(w * 0.18f, h))
            listOf(0.08f, 0.94f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, h * f), Offset(w, h * f), 1.2f * u) }
            for (k in 0 until 2) {
                val o = Offset(w * (0.25f + 0.5f * azar(v.semilla, 10 + k)), h * (0.3f + 0.4f * azar(v.semilla, 12 + k)))
                val rr = w * (0.25f + 0.2f * azar(v.semilla, 14 + k))
                drawOval(Color.Black.copy(alpha = 0.1f), Offset(o.x - rr, o.y - rr * 1.3f), Size(rr * 2, rr * 2.6f))
                drawOval(c(Color(0xFF3A2E20)).copy(alpha = 0.35f), Offset(o.x - rr, o.y - rr * 1.3f), Size(rr * 2, rr * 2.6f), style = Stroke(0.7f * u))
            }
            // El musgo que le crece al pie y trepa por un canto.
            for (k in 0 until 12) {
                val p = Offset(w * azar(v.semilla, 60 + k), h * (0.92f + 0.07f * azar(v.semilla, 70 + k)))
                drawCircle(c(if (k % 3 == 0) MUSGO_LUZ else MUSGO), (1 + 1.3f * azar(v.semilla, 80 + k)) * u, p)
            }
            for (k in 0 until 5) drawCircle(c(MUSGO), 1 * u, Offset(panza * 0.5f + 1.5f * u, h * (0.6f + 0.06f * k)))
        }
        // La etiqueta desteñida con el día, y el mes a lo largo del lomo.
        val eti = Rect(w * 0.18f, h * 0.12f, w * 0.82f, h * 0.36f)
        drawRect(papel(PAPEL, oscuro, v.noche).copy(alpha = 0.92f), eti.topLeft, eti.size)
        drawRect(c(Color(0xFF6A5A40)).copy(alpha = 0.3f), Offset(eti.left, eti.bottom - eti.height * 0.3f), Size(eti.width, eti.height * 0.3f))
        textoEn(medidor, v.dia, eti.deflate(1.5f * u), letra, c(TINTA), 14f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes.lowercase(), Rect(w * 0.2f, h * 0.385f, w * 0.8f, h * 0.715f), letra, papel(PAPEL, oscuro, v.noche).copy(alpha = 0.85f), 9f, vertical = true, minimo = 4f)
        // Las notas atadas con cordel al lomo: más cuanto más escribiste.
        val ny = h * 0.73f
        drawLine(c(CORDEL), Offset(panza * 0.5f, ny), Offset(w - panza * 0.5f, ny), 1 * u)
        val notas = 1 + (v.lleno * 2).toInt()
        for (j in 0 until notas) {
            val o = Offset(w * (0.3f + 0.2f * j), ny)
            rotate(-12f + 24f * azar(v.semilla, 20 + j), o) {
                val nota = Rect(o.x - w * 0.12f, o.y - 1 * u, o.x + w * 0.12f, o.y + h * 0.09f)
                drawRect(papel(if (j % 2 == 0) PAPEL else CARTON, oscuro, v.noche), nota.topLeft, nota.size)
                for (k in 1..2) drawLine(c(TINTA).copy(alpha = 0.4f), Offset(nota.left + 1 * u, nota.top + nota.height * k / 3f), Offset(nota.right - 1 * u, nota.top + nota.height * k / 3f), 0.5f * u)
            }
        }
        // El marbete redondo de la biblioteca; de noche, una luna y una luciérnaga.
        val mc = Offset(w * 0.5f, h * 0.875f)
        val mr = minOf(w * 0.2f, 4.5f * u)
        if (v.noche) {
            drawCircle(LUNA, mr, mc)
            drawCircle(cuero, mr * 0.8f, Offset(mc.x + mr * 0.45f, mc.y - mr * 0.25f))
            val lu = Offset(mc.x + mr * 1.4f, mc.y - mr * 1.2f)
            drawCircle(LUCIERNAGA.copy(alpha = 0.3f), 2.4f * u, lu)
            drawCircle(LUCIERNAGA, 0.9f * u, lu)
        } else {
            drawCircle(papel(PAPEL, oscuro, false).copy(alpha = 0.8f), mr, mc)
            drawRect(c(Color(0xFF8A3A2A)).copy(alpha = 0.7f), Offset(mc.x - mr, mc.y - mr * 0.25f), Size(mr * 2, mr * 0.5f))
        }
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.215f, 0.275f, 0.755f, 0.61f)
    override val letraTapa: FontFamily = LetraExtra.letrero
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = TINTA
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /**
     * El libro del día rescatado de la ruina, de frente: hinchado de humedad (los cantos se abomban y las hojas ondulan), con
     * cercos de agua y moho, el musgo que le crece al pie y trepa por el lomo, la hiedra que le cuelga por el canto, el
     * marbete redondo de la biblioteca, la etiqueta desteñida (la fecha, lo escrito y las palabras) y las notas atadas con
     * cordel. Al abrirse, la tapa gira sobre el lomo y deja ver la hoja ondulada con una hoja seca prensada. De día lo cruza un
     * rayo de sol; de noche, luciérnagas, y el marbete lleva una luna.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val cuero = c(LIBROS[(e.semilla and 0x7fffffff) % LIBROS.size])
        val hojaP = papel(PAPEL, oscuro, e.noche)
        val tinta = tintaTapa(oscuro, e.noche)
        val libro = Rect(w * 0.015f, h * 0.045f, w * 0.935f, h * 0.925f)
        val lomo = Rect(libro.left, libro.top, libro.left + w * 0.075f, libro.bottom)
        val cara = Rect(lomo.right, libro.top, libro.right, libro.bottom)
        val panza = w * 0.012f
        /** El contorno abombado de [r]: arriba y abajo ondula, a la derecha se hincha. */
        fun hinchado(r: Rect): Path = Path().apply {
            moveTo(r.left, r.top)
            quadraticTo(r.left + r.width * 0.3f, r.top - 3 * u, r.left + r.width * 0.55f, r.top + 1 * u)
            quadraticTo(r.left + r.width * 0.8f, r.top + 4 * u, r.right, r.top)
            quadraticTo(r.right + panza * 2, r.center.y, r.right, r.bottom)
            quadraticTo(r.left + r.width * 0.65f, r.bottom + 3 * u, r.left + r.width * 0.4f, r.bottom - 1 * u)
            quadraticTo(r.left + r.width * 0.2f, r.bottom - 3 * u, r.left, r.bottom)
            close()
        }
        // La sombra y las hojas hinchadas que asoman por el canto, onduladas.
        drawPath(hinchado(libro.translate(4 * u, 5 * u)), Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f))
        val hojas = Rect(libro.left + w * 0.02f, libro.top + h * 0.02f, libro.right + 4 * u, libro.bottom + 4 * u)
        drawPath(hinchado(hojas), mezcla(hojaP, Color(0xFF6A5A40), 0.25f))
        for (j in 1..5) {
            drawPath(Path().apply {
                moveTo(hojas.left + 3 * u, libro.bottom + j * 0.8f * u)
                quadraticTo(hojas.center.x, libro.bottom + j * 0.8f * u + (if (j % 2 == 0) 2f else -1.5f) * u, hojas.right, libro.bottom + j * 0.7f * u)
            }, Color.Black.copy(alpha = 0.15f), style = Stroke(0.5f * u))
        }
        // Dentro (se ve al abrir): la hoja ondulada por el agua, con renglones desvaídos y una hoja seca prensada.
        if (a > 0.01f) {
            drawRect(hojaP, cara.topLeft, cara.size)
            var y = cara.top + h * 0.12f; var k = 0
            while (y < cara.bottom - h * 0.08f) {
                drawPath(Path().apply {
                    moveTo(cara.left + w * 0.06f, y)
                    quadraticTo(cara.left + cara.width * 0.4f, y + (if (k % 2 == 0) 2.5f else -2.5f) * u, cara.left + cara.width * (0.6f + 0.3f * azar(e.semilla, 100 + k)), y)
                }, tinta.copy(alpha = 0.3f), style = Stroke(1.1f * u))
                y += 8 * u; k++
            }
            drawOval(c(Color(0xFF6A5A40)).copy(alpha = 0.2f), Offset(cara.left + cara.width * 0.15f, cara.top + cara.height * 0.5f), Size(cara.width * 0.35f, cara.height * 0.4f))
            val hs = Offset(cara.left + cara.width * 0.72f, cara.center.y)
            drawPath(estrella(hs, h * 0.13f, 5, 0.55f), c(Color(0xFFA0702E)).copy(alpha = 0.85f))
            drawLine(c(Color(0xFF6E5A2A)), hs, Offset(hs.x + h * 0.06f, hs.y + h * 0.18f), 1.2f * u)
            drawRect(Color.Black.copy(alpha = 0.15f), cara.topLeft, Size(8 * u, cara.height))
        }
        // La tapa hinchada, que gira sobre el lomo.
        val giro = cos(a * 1.6f).coerceAtLeast(0.02f)
        scale(giro, 1f, Offset(cara.left, cara.center.y)) {
            val forma = hinchado(cara)
            drawPath(forma, Brush.verticalGradient(listOf(mezcla(cuero, Color.White, 0.06f), cuero, mezcla(cuero, Color.Black, 0.2f)), cara.top, cara.bottom))
            clipPath(forma) {
                // Los cercos de agua y las manchas de moho.
                for (k in 0 until 3) {
                    val o = Offset(cara.left + cara.width * (0.1f + 0.8f * azar(e.semilla, 10 + k)), cara.top + cara.height * (0.15f + 0.7f * azar(e.semilla, 12 + k)))
                    val rr = h * (0.14f + 0.12f * azar(e.semilla, 14 + k))
                    drawOval(Color.Black.copy(alpha = 0.08f), Offset(o.x - rr * 1.3f, o.y - rr), Size(rr * 2.6f, rr * 2))
                    drawOval(c(Color(0xFF3A2E20)).copy(alpha = 0.4f), Offset(o.x - rr * 1.3f, o.y - rr), Size(rr * 2.6f, rr * 2), style = Stroke(0.8f * u))
                }
                for (k in 0 until 26) {
                    val o = Offset(cara.left + cara.width * azar(e.semilla, 120 + k), cara.top + cara.height * (0.75f + 0.25f * azar(e.semilla, 140 + k)))
                    drawCircle(c(Color(0xFF8A9A70)).copy(alpha = 0.18f), (1 + 2.5f * azar(e.semilla, 160 + k)) * u, o)
                }
                drawRect(Color.White.copy(alpha = 0.07f), Offset(cara.left, cara.top), Size(cara.width, 3 * u))
            }
            // El marbete redondo de la biblioteca; de noche, una luna y una luciérnaga.
            val mc = Offset(cara.left + w * 0.06f, h * 0.2f)
            val mr = 11 * u
            if (e.noche) {
                drawCircle(LUNA, mr * 0.75f, mc)
                drawCircle(cuero, mr * 0.62f, Offset(mc.x + mr * 0.35f, mc.y - mr * 0.2f))
                val lu = Offset(mc.x + mr * 0.9f, mc.y + mr * 1.3f)
                drawCircle(LUCIERNAGA.copy(alpha = 0.2f), 5 * u, lu); drawCircle(LUCIERNAGA.copy(alpha = 0.4f), 2.6f * u, lu); drawCircle(LUCIERNAGA, 1.2f * u, lu)
            } else {
                drawCircle(hojaP.copy(alpha = 0.85f), mr, mc)
                drawRect(c(Color(0xFF8A3A2A)).copy(alpha = 0.7f), Offset(mc.x - mr * 0.95f, mc.y - mr * 0.22f), Size(mr * 1.9f, mr * 0.44f))
                drawCircle(tinta.copy(alpha = 0.35f), mr * 0.82f, mc, style = Stroke(0.6f * u))
            }
            // La etiqueta desteñida, con la orilla de arriba amarillenta y una esquina rota.
            val eti = Rect(w * 0.19f, h * 0.14f, w * 0.78f, h * 0.74f)
            val rota = Path().apply {
                moveTo(eti.left, eti.top); lineTo(eti.right, eti.top); lineTo(eti.right, eti.bottom - 12 * u)
                lineTo(eti.right - 5 * u, eti.bottom - 7 * u); lineTo(eti.right - 9 * u, eti.bottom - 3 * u); lineTo(eti.right - 14 * u, eti.bottom)
                lineTo(eti.left, eti.bottom); close()
            }
            drawPath(rota, hojaP)
            drawRect(c(Color(0xFF6A5A40)).copy(alpha = 0.22f), eti.topLeft, Size(eti.width, h * 0.025f))
            drawRect(c(Color(0xFF6A5A40)).copy(alpha = 0.12f), Offset(eti.left, eti.top + h * 0.025f), Size(eti.width, h * 0.015f))
            drawPath(rota, tinta.copy(alpha = 0.25f), style = Stroke(0.6f * u))
            textoEn(medidor, e.fecha, Rect(eti.left + 8 * u, h * 0.17f, eti.right - 8 * u, h * 0.25f), letra, tinta, 12.5f, peso = FontWeight.SemiBold, minimo = 7f)
            drawLine(tinta.copy(alpha = 0.4f), Offset(eti.left + eti.width * 0.2f, h * 0.26f), Offset(eti.right - eti.width * 0.2f, h * 0.26f), 0.7f * u)
            drawLine(tinta.copy(alpha = 0.4f), Offset(eti.left + eti.width * 0.2f, h * 0.628f), Offset(eti.right - eti.width * 0.2f, h * 0.628f), 0.7f * u)
            if (!e.vacio && e.palabras > 0)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(eti.left + eti.width * 0.2f, h * 0.64f, eti.right - eti.width * 0.2f, h * 0.715f), letra, tinta.copy(alpha = 0.7f), 10f, minimo = 6f)
            // El cordel con las notas atadas, que cuelgan torcidas.
            val ny = h * 0.79f
            drawLine(c(CORDEL), Offset(cara.left, ny), Offset(cara.right + panza, ny), 1.2f * u)
            listOf(0.22f, 0.5f).forEachIndexed { j, f ->
                val o = Offset(cara.left + cara.width * f, ny)
                rotate(-10f + 20f * azar(e.semilla, 20 + j), o) {
                    val nota = Rect(o.x - w * 0.07f, o.y - 1 * u, o.x + w * 0.07f, o.y + h * 0.11f)
                    drawRect(Color.Black.copy(alpha = 0.2f), Offset(nota.left + 1 * u, nota.top + 1.5f * u), nota.size)
                    drawRect(papel(if (j % 2 == 0) PAPEL else CARTON, oscuro, e.noche), nota.topLeft, nota.size)
                    for (k in 1..3) drawLine(tinta.copy(alpha = 0.35f), Offset(nota.left + 3 * u, nota.top + nota.height * k / 4f), Offset(nota.right - 3 * u - k * 3 * u, nota.top + nota.height * k / 4f), 0.6f * u)
                    drawCircle(c(CORDEL), 1.6f * u, o)
                }
            }
            // La hiedra que cuelga por el canto y el musgo que crece al pie.
            hiedra(Offset(cara.right - w * 0.05f, cara.top - 2 * u), cara.height * 0.75f, e.semilla, oscuro, e.noche)
            hiedra(Offset(cara.right - w * 0.015f, cara.top - 2 * u), cara.height * 0.45f, e.semilla + 7, oscuro, e.noche)
            for (j in 0 until 5) musgo(Offset(cara.left + cara.width * (0.08f + 0.21f * j), cara.bottom - 1 * u), 9 * u, e.semilla + j, oscuro, e.noche)
            if (a > 0f) drawPath(forma, Color.Black.copy(alpha = 0.45f * a))
        }
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * sin(a * PI.toFloat())), Offset(cara.left + cara.width * giro, cara.top), Size(10 * u, cara.height))
        // El lomo, con sus nervios abombados y el musgo que trepa.
        drawRect(mezcla(cuero, Color.Black, 0.22f), lomo.topLeft, lomo.size)
        drawRect(Color.White.copy(alpha = 0.07f), Offset(lomo.left + lomo.width * 0.2f, lomo.top), Size(lomo.width * 0.25f, lomo.height))
        listOf(0.12f, 0.88f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.3f), Offset(lomo.left, lomo.top + lomo.height * f), Offset(lomo.right, lomo.top + lomo.height * f), 1.6f * u) }
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(lomo.right, lomo.top), Offset(lomo.right, lomo.bottom), 1.2f * u)
        for (k in 0 until 10) drawCircle(c(if (k % 3 == 0) MUSGO_LUZ else MUSGO), (1.2f + 1.4f * azar(e.semilla, 500 + k)) * u, Offset(lomo.left + lomo.width * (0.2f + 0.6f * azar(e.semilla, 520 + k)), lomo.bottom - lomo.height * 0.4f * azar(e.semilla, 540 + k)))
        musgo(Offset(lomo.center.x, lomo.bottom - 1 * u), 8 * u, e.semilla + 31, oscuro, e.noche)
        // De día, un rayo de sol en diagonal; de noche, luciérnagas (ninguna sobre lo escrito).
        if (e.noche) {
            val caja = Rect(w * cajaTapa.left, h * cajaTapa.top, w * cajaTapa.right, h * cajaTapa.bottom).inflate(4 * u)
            for (k in 0 until 7) {
                val o = Offset(w * (0.04f + 0.92f * azar(e.semilla, 600 + k)), h * (0.06f + 0.88f * azar(e.semilla, 610 + k)))
                if (caja.contains(o)) continue
                drawCircle(LUCIERNAGA.copy(alpha = 0.16f), 6 * u, o); drawCircle(LUCIERNAGA.copy(alpha = 0.35f), 3 * u, o); drawCircle(LUCIERNAGA, 1.3f * u, o)
            }
        } else drawPath(Path().apply { moveTo(w * 0.78f, 0f); lineTo(w * 0.9f, 0f); lineTo(w * 0.8f, h); lineTo(w * 0.66f, h); close() }, SOL.copy(alpha = if (oscuro) 0.06f else 0.12f))
    }
}

/**
 * Espacio: la biblioteca de la nave. La pared es curva (más sombra hacia los lados) y está hecha de un panal de celdas
 * hexagonales; los costados son las cuadernas del casco con su filo de luz cian y, en cada nivel, un panelito con tres luces
 * de estado. Arriba, el techo abovedado de paneles y, en el centro, un gran ojo de buey con su aro de pernos por el que se ve
 * la Tierra saliendo (mar, tierras, nubes y el filo de la atmósfera) contra las estrellas; a los lados, dos paneles de
 * servicio. Las tablas son paneles con su línea de luz, y cada mes va en un visor de vidrio oscuro con corchetes cian. Abajo,
 * el zócalo con sus rejillas y el piso de rejilla hexagonal. Cada entrada es un cristal de memoria, un prisma de seis caras
 * que brilla en su celda hexagonal (más alto cuanto más escribiste), con el mes grabado al lado y el día en la placa de la
 * celda; de noche la celda lleva una luna y estrellas en lugar de su emblema. De noche la pared se apaga y deja ver el campo
 * de estrellas, y por el ojo de buey se ven las luces de las ciudades. Distinto del Ideario, que es la sala de control con
 * los racks y la ventana a la nebulosa.
 * La tapa del diario es la celda de memoria de frente, con el visor donde se lee lo escrito y el cristal tumbado en su
 * cuna; al abrirse, el visor se enciende.
 */
object EstanteEspacio : DisenoBiblioteca {
    private val PANEL = Color(0xFFD6DCE5)
    private val PANEL_LUZ = Color(0xFFEDF1F6)
    private val PANEL_OSC = Color(0xFF9AA4B4)
    private val CASCO = Color(0xFF586378)
    private val CASCO_OSC = Color(0xFF2C3446)
    private val CELDA = Color(0xFF141B2C)
    private val VISOR = Color(0xFF1E2638)
    private val CIAN = Color(0xFF5CE1FF)
    private val VERDE = Color(0xFF5CFFB0)
    private val AMBAR = Color(0xFFFFB84A)
    private val VACIO = Color(0xFF03050C)
    private val MAR = Color(0xFF2A66B0)
    private val TIERRA_FIRME = Color(0xFF4A8A4E)
    private val DESIERTO = Color(0xFFB8955E)
    private val NUBE = Color(0xFFF0F5FB)
    private val ATMOSFERA = Color(0xFF8FD4FF)
    private val ESTRELLA = Color(0xFFF2F4FF)
    /** Los cristales de memoria: cian, violeta, ámbar, verde, rosa y azul. */
    private val CRISTALES = listOf(Color(0xFF5CE1FF), Color(0xFFB07CFF), Color(0xFFFFB84A), Color(0xFF5CFFB0), Color(0xFFFF6FA8), Color(0xFF9FB4FF))
    private val NOCHE = Color(0xFF050A1C)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, con las luces de la nave bajas. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF05070D), 0.4f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    /** Un hexágono con las puntas arriba y abajo dentro de [r]; [esquina] es el alto de cada punta, en fracción del alto. */
    private fun hexagono(r: Rect, esquina: Float): Path = Path().apply {
        val e = r.height * esquina
        moveTo(r.center.x, r.top); lineTo(r.right, r.top + e); lineTo(r.right, r.bottom - e)
        lineTo(r.center.x, r.bottom); lineTo(r.left, r.bottom - e); lineTo(r.left, r.top + e); close()
    }

    override val corona = 0.44f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 104.dp
    override val tabla: Dp = 14.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.orbita

    override fun ancho(palabras: Int): Dp = (34 + (palabras / 80).coerceAtMost(10)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.88f + 0.06f * azar(semilla, 9)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // La pared curva de la nave: un panal de celdas hexagonales; de noche se apaga y deja ver el campo de estrellas.
        val cuerpo = Rect(0f, d.corona.bottom, w, d.base.top)
        drawRect(if (noche) VACIO else c(PANEL), cuerpo.topLeft, cuerpo.size)
        if (noche) {
            val n = (cuerpo.width * cuerpo.height / (900 * u * u)).toInt().coerceIn(40, 900)
            for (k in 0 until n) {
                val o = Offset(cuerpo.left + cuerpo.width * azar(k, 5), cuerpo.top + cuerpo.height * azar(k, 6))
                drawCircle(ESTRELLA.copy(alpha = 0.3f + 0.6f * azar(k, 7)), (0.4f + 0.9f * azar(k, 8) * azar(k, 9)) * u, o)
            }
        }
        val rh = 13 * u
        val ah = rh * 1.732f
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            var fila = 0
            var hy = cuerpo.top
            while (hy < cuerpo.bottom + rh) {
                var hx = cuerpo.left + (if (fila % 2 == 0) 0f else ah / 2)
                while (hx < cuerpo.right + ah) {
                    drawPath(hexagono(Rect(hx - ah / 2, hy - rh, hx + ah / 2, hy + rh), 0.25f), if (noche) CIAN.copy(alpha = 0.18f) else c(PANEL_OSC).copy(alpha = 0.7f), style = Stroke(1 * u))
                    hx += ah
                }
                hy += rh * 1.5f; fila++
            }
        }
        // La curva de la pared: más sombra hacia los lados.
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.32f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.32f)), 0f, w), cuerpo.topLeft, cuerpo.size)
        // Cada repisa: su sombra y la línea de luz de la tabla de arriba.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.5f else 0.22f), r.topLeft, Size(r.width, 6 * u))
            drawLine(CIAN.copy(alpha = if (noche) 0.7f else 0.5f), Offset(r.left + 6 * u, r.top + 6.5f * u), Offset(r.right - 6 * u, r.top + 6.5f * u), 1 * u)
        }
        // Los costados: las cuadernas del casco, con su filo de luz, sus pernos y un panelito de estado en cada nivel.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(CASCO), r.topLeft, r.size)
            drawRect(c(CASCO_OSC), Offset(if (izq) r.left else r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            val borde = if (izq) r.right - 1.5f * u else r.left + 1.5f * u
            drawLine(CIAN.copy(alpha = 0.8f), Offset(borde, r.top), Offset(borde, r.bottom), 1.2f * u)
            d.repisas.forEachIndexed { k, rep ->
                val pc = Rect(r.left + r.width * 0.3f, rep.top + rep.height * 0.3f, r.right - r.width * 0.25f, rep.top + rep.height * 0.6f)
                drawRoundRect(c(CASCO_OSC), pc.topLeft, pc.size, CornerRadius(2 * u))
                for (j in 0 until 3) {
                    val luz = when ((j + k) % 4) { 0 -> AMBAR; 1 -> CIAN; else -> VERDE }
                    drawCircle(luz.copy(alpha = 0.9f), 1.2f * u, Offset(pc.center.x, pc.top + pc.height * (0.25f + 0.25f * j)))
                }
                listOf(rep.top + 4 * u, rep.bottom - 4 * u).forEach { y -> drawCircle(c(PANEL_OSC), 1 * u, Offset(r.center.x, y)) }
            }
        }
        // Las tablas: paneles con su línea de luz y sus marcas.
        d.tablas.forEach { r ->
            drawRect(c(PANEL_LUZ), r.topLeft, r.size)
            drawRect(c(PANEL_OSC), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
            drawLine(CIAN.copy(alpha = 0.85f), Offset(r.left, r.bottom - 1 * u), Offset(r.right, r.bottom - 1 * u), 1 * u)
            var mx = r.left + 10 * u
            while (mx < r.right - 4 * u) { drawLine(c(CASCO).copy(alpha = 0.5f), Offset(mx, r.top + r.height * 0.6f), Offset(mx, r.top + r.height * 0.85f), 0.7f * u); mx += 20 * u }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: un visor de vidrio oscuro con sus corchetes cian en las esquinas.
        d.placas.forEach { p ->
            val r = p.r
            drawRoundRect(c(VISOR), r.topLeft, r.size, CornerRadius(2 * u))
            drawRoundRect(CIAN.copy(alpha = 0.5f), r.topLeft, r.size, CornerRadius(2 * u), style = Stroke(0.7f * u))
            val e = r.height * 0.4f
            listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), r.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
                val a = Offset(o.x + s.x * 1.5f * u, o.y + s.y * 1.5f * u)
                drawLine(CIAN, a, Offset(a.x + s.x * e, a.y), 1 * u)
                drawLine(CIAN, a, Offset(a.x, a.y + s.y * e), 1 * u)
            }
            textoEn(medidor, p.texto.uppercase(), Rect(r.left + e + 3 * u, r.top, r.right - e - 3 * u, r.bottom), letra, CIAN, 9.5f, espaciado = 1.2f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo con sus rejillas de ventilación y el piso de rejilla hexagonal.
        val b = d.base
        val piso = b.top + b.height * 0.45f
        drawRect(c(CASCO), b.topLeft, Size(b.width, piso - b.top))
        drawRect(c(PANEL_OSC), b.topLeft, Size(b.width, 2 * u))
        listOf(0.18f, 0.62f).forEach { f ->
            val rej = Rect(b.left + b.width * f, b.top + (piso - b.top) * 0.3f, b.left + b.width * (f + 0.2f), b.top + (piso - b.top) * 0.75f)
            drawRoundRect(c(CASCO_OSC), rej.topLeft, rej.size, CornerRadius(2 * u))
            var gx = rej.left + 3 * u
            while (gx < rej.right - 2 * u) { drawLine(c(CASCO), Offset(gx, rej.top + 1.5f * u), Offset(gx, rej.bottom - 1.5f * u), 1.2f * u); gx += 4 * u }
        }
        drawRect(c(CASCO_OSC), Offset(0f, piso), Size(w, h - piso))
        clipRect(0f, piso, w, h) {
            val rp = 5 * u; val ap = rp * 1.732f
            var fy = piso; var ff = 0
            while (fy < h + rp) {
                var fx = if (ff % 2 == 0) 0f else ap / 2
                while (fx < w + ap) {
                    drawPath(hexagono(Rect(fx - ap / 2, fy - rp, fx + ap / 2, fy + rp), 0.25f), c(CASCO).copy(alpha = 0.8f), style = Stroke(0.8f * u))
                    fx += ap
                }
                fy += rp * 1.5f; ff++
            }
        }
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, piso), Size(w, 2.5f * u))
    }

    /** El techo abovedado, el ojo de buey con la Tierra saliendo y los dos paneles de servicio. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El techo curvo de la nave: paneles con sus juntas, que se cierran hacia los lados.
        drawRect(c(PANEL), r.topLeft, r.size)
        val techo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + r.height * 0.3f)
            quadraticTo(r.center.x, r.top - r.height * 0.05f, r.left, r.top + r.height * 0.3f); close()
        }
        drawPath(techo, c(CASCO))
        for (k in 1 until 6) { val x = r.left + r.width * k / 6f; drawLine(c(PANEL_OSC), Offset(x, r.top + r.height * 0.3f), Offset(x, r.bottom), 0.8f * u) }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.bottom - 4 * u), Size(r.width, 4 * u))
        // A los lados, dos paneles de servicio con sus luces y una manija.
        listOf(r.left + r.width * 0.1f, r.right - r.width * 0.26f).forEachIndexed { k, x0 ->
            val pn = Rect(x0, r.top + r.height * 0.4f, x0 + r.width * 0.16f, r.top + r.height * 0.82f)
            drawRoundRect(c(PANEL_LUZ), pn.topLeft, pn.size, CornerRadius(3 * u))
            drawRoundRect(c(PANEL_OSC), pn.topLeft, pn.size, CornerRadius(3 * u), style = Stroke(1 * u))
            for (j in 0 until 4) drawCircle(listOf(VERDE, CIAN, AMBAR, VERDE)[(j + k) % 4], 1.3f * u, Offset(pn.left + pn.width * (0.2f + 0.2f * j), pn.top + pn.height * 0.18f))
            drawRoundRect(c(CASCO), Offset(pn.left + pn.width * 0.2f, pn.top + pn.height * 0.4f), Size(pn.width * 0.6f, pn.height * 0.12f), CornerRadius(2 * u))
            drawRect(c(CASCO_OSC).copy(alpha = 0.5f), Offset(pn.left + pn.width * 0.15f, pn.top + pn.height * 0.66f), Size(pn.width * 0.7f, pn.height * 0.2f))
        }
        // El ojo de buey: al otro lado del vidrio, el espacio con sus estrellas y la Tierra que sale.
        val oc = Offset(r.center.x, r.top + r.height * 0.56f)
        val orad = minOf(r.height * 0.4f, r.width * 0.26f)
        val ventana = Path().apply { addOval(Rect(oc, orad)) }
        drawCircle(c(CASCO_OSC), orad + 9 * u, oc)
        drawPath(ventana, VACIO)
        clipPath(ventana) {
            val n = if (noche) 90 else 34
            for (k in 0 until n) drawCircle(ESTRELLA.copy(alpha = 0.35f + 0.6f * azar(k, 21)), (0.4f + 0.9f * azar(k, 22) * azar(k, 23)) * u, Offset(oc.x + orad * (azar(k, 24) * 2 - 1), oc.y + orad * (azar(k, 25) * 2 - 1)))
            // La Tierra asomando por abajo: el halo, el mar, las tierras, las nubes y el filo de la atmósfera.
            val tc = Offset(oc.x - orad * 0.2f, oc.y + orad * 2.1f)
            val trad = orad * 1.75f
            mancha(Offset(tc.x, tc.y - trad), orad * 0.9f, ATMOSFERA.copy(alpha = if (noche) 0.25f else 0.45f))
            drawCircle(if (noche) mezcla(MAR, VACIO, 0.6f) else MAR, trad, tc)
            clipPath(Path().apply { addOval(Rect(tc, trad)) }) {
                listOf(Triple(-0.3f, -0.95f, 0.35f), Triple(0.25f, -0.9f, 0.25f), Triple(0.55f, -0.8f, 0.2f)).forEachIndexed { k, (fx, fy, s) ->
                    val col = if (k == 1) DESIERTO else TIERRA_FIRME
                    drawOval(if (noche) mezcla(col, VACIO, 0.65f) else col, Offset(tc.x + trad * fx - trad * s, tc.y + trad * fy - trad * s * 0.35f), Size(trad * s * 2, trad * s * 0.7f))
                }
                for (k in 0 until 5) {
                    val o = Offset(tc.x + trad * (-0.6f + 0.3f * k), tc.y - trad * (0.9f + 0.06f * (k % 2)))
                    drawOval(NUBE.copy(alpha = if (noche) 0.15f else 0.7f), Offset(o.x - trad * 0.12f, o.y - trad * 0.02f), Size(trad * 0.24f, trad * 0.04f))
                }
                // De noche, las luces de las ciudades.
                if (noche) for (k in 0 until 26) drawCircle(AMBAR.copy(alpha = 0.8f), 0.6f * u, Offset(tc.x + trad * (azar(k, 31) * 1.2f - 0.6f), tc.y - trad * (0.82f + 0.15f * azar(k, 32))))
            }
            drawCircle(ATMOSFERA.copy(alpha = 0.8f), trad, tc, style = Stroke(1.6f * u))
            drawCircle(ATMOSFERA.copy(alpha = 0.25f), trad + 2.5f * u, tc, style = Stroke(3 * u))
        }
        // El aro del ojo de buey con sus doce pernos y el reflejo del vidrio.
        drawCircle(c(CASCO), orad + 5 * u, oc, style = Stroke(7 * u))
        drawCircle(c(PANEL_OSC), orad + 1.5f * u, oc, style = Stroke(1.2f * u))
        for (k in 0 until 12) {
            val a = k * (2 * PI.toFloat() / 12)
            drawCircle(c(PANEL_LUZ), 1.4f * u, Offset(oc.x + (orad + 5 * u) * cos(a), oc.y + (orad + 5 * u) * sin(a)))
        }
        drawArc(Color.White.copy(alpha = 0.12f), 200f, 60f, false, Offset(oc.x - orad * 0.8f, oc.y - orad * 0.8f), Size(orad * 1.6f, orad * 1.6f), style = Stroke(3 * u, cap = StrokeCap.Round))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val placaAlta = minOf(h * 0.2f, 18 * u)
        // La celda hexagonal: el marco del casco, el fondo oscuro y su filo de luz.
        val celdaR = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - placaAlta - 1.5f * u)
        drawPath(hexagono(celdaR, 0.16f), c(CASCO))
        val dentro = hexagono(celdaR.deflate(2.2f * u), 0.16f)
        drawPath(dentro, c(CELDA))
        val base = CRISTALES[(v.semilla and 0x7fffffff) % CRISTALES.size]
        val luz = if (v.tono != null) mezcla(base, v.tono, 0.22f) else base
        clipPath(dentro) {
            // De noche, estrellas en el fondo de la celda.
            if (v.noche) for (k in 0 until 7) drawCircle(ESTRELLA.copy(alpha = 0.7f), 0.6f * u, Offset(w * (0.15f + 0.7f * azar(v.semilla, 40 + k)), celdaR.top + celdaR.height * (0.2f + 0.6f * azar(v.semilla, 50 + k))))
            // El cristal de memoria: un prisma de seis caras que crece con lo escrito, con su halo (capas planas, sin degradado).
            val alto = celdaR.height * (0.42f + 0.36f * v.lleno)
            val anchoC = w * 0.32f
            val cc = Offset(w * 0.42f, celdaR.center.y + celdaR.height * 0.04f)
            val cristal = Rect(cc.x - anchoC / 2, cc.y - alto / 2, cc.x + anchoC / 2, cc.y + alto / 2)
            listOf(3.5f to 0.08f, 2f to 0.14f).forEach { (crece, a) -> drawPath(hexagono(cristal.inflate(crece * u), 0.2f), luz.copy(alpha = a)) }
            drawPath(hexagono(cristal, 0.2f), mezcla(luz, Color.Black, if (oscuro) 0.25f else 0.1f))
            val e = cristal.height * 0.2f
            val cara = Path().apply {
                moveTo(cristal.center.x, cristal.top); lineTo(cristal.left, cristal.top + e)
                lineTo(cristal.left, cristal.bottom - e); lineTo(cristal.center.x, cristal.bottom); close()
            }
            drawPath(cara, mezcla(luz, Color.White, 0.35f).copy(alpha = 0.8f))
            drawLine(Color.White.copy(alpha = 0.8f), Offset(cristal.center.x, cristal.top + 1 * u), Offset(cristal.center.x, cristal.bottom - 1 * u), 0.7f * u)
            drawLine(Color.White.copy(alpha = 0.6f), Offset(cristal.left + anchoC * 0.2f, cristal.top + e + 2 * u), Offset(cristal.left + anchoC * 0.2f, cristal.top + cristal.height * 0.5f), 0.9f * u)
            // La luz que el cristal echa sobre el fondo de la celda.
            drawOval(luz.copy(alpha = 0.18f), Offset(cristal.left - 2 * u, cristal.bottom - 1 * u), Size(anchoC + 4 * u, 4 * u))
        }
        drawPath(dentro, CIAN.copy(alpha = 0.35f), style = Stroke(0.7f * u))
        // El mes grabado al lado del cristal.
        textoEn(medidor, v.mes.uppercase(), Rect(w * 0.64f, celdaR.top + celdaR.height * 0.22f, w * 0.86f, celdaR.bottom - celdaR.height * 0.22f), letra, CIAN.copy(alpha = 0.75f), 7f, vertical = true, minimo = 4f)
        // El emblema de la celda (un triángulo ámbar); de noche, una luna.
        val sc = Offset(w * 0.5f, celdaR.top + celdaR.height * 0.11f)
        if (v.noche) {
            drawCircle(ESTRELLA, 2.2f * u, sc)
            drawCircle(c(CELDA), 1.8f * u, Offset(sc.x + 1 * u, sc.y - 0.6f * u))
        } else {
            drawPath(Path().apply { moveTo(sc.x, sc.y - 2 * u); lineTo(sc.x + 2.2f * u, sc.y + 1.6f * u); lineTo(sc.x - 2.2f * u, sc.y + 1.6f * u); close() }, AMBAR.copy(alpha = 0.8f))
        }
        // La placa de la celda con el día y su luz de estado.
        val placa = Rect(w * 0.1f, h - placaAlta, w * 0.9f, h)
        drawRoundRect(c(VISOR), placa.topLeft, placa.size, CornerRadius(2 * u))
        drawRoundRect(CIAN.copy(alpha = 0.45f), placa.topLeft, placa.size, CornerRadius(2 * u), style = Stroke(0.7f * u))
        textoEn(medidor, v.dia, Rect(placa.left + 1 * u, placa.top, placa.right - 5 * u, placa.bottom), letra, CIAN, 11f, peso = FontWeight.Bold)
        drawCircle(if (v.lleno > 0.5f) VERDE else AMBAR, 1.1f * u, Offset(placa.right - 3 * u, placa.center.y))
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.115f, 0.29f, 0.815f, 0.65f)
    override val letraTapa: FontFamily = LetraExtra.grotesk
    override val tamTapa = 13f
    override val lineasTapa = 3
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = Color(0xFFE8EEFF)
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra transmitida" else "$n palabras transmitidas"

    /** Un hexágono tumbado, con las puntas a la izquierda y a la derecha, dentro de [r]; [punta] es el largo de cada punta. */
    private fun hexTumbado(r: Rect, punta: Float): Path = Path().apply {
        moveTo(r.left, r.center.y); lineTo(r.left + punta, r.top); lineTo(r.right - punta, r.top)
        lineTo(r.right, r.center.y); lineTo(r.right - punta, r.bottom); lineTo(r.left + punta, r.bottom); close()
    }

    /**
     * La celda de memoria del día, grande y de frente: el marco hexagonal del casco con sus pernos, la cabecera de la
     * bitácora de vuelo con su emblema y la fecha, el visor de vidrio oscuro con sus corchetes cian (donde se lee lo
     * escrito), las tres luces de estado y, abajo, el cristal de memoria tumbado en su cuna (más largo cuanto más
     * escribiste) junto a la placa con las palabras transmitidas. Al abrirse, el cristal se alza y brilla más, y el visor se
     * enciende desde el centro con su línea de barrido. De noche, la celda se llena de estrellas y el emblema es una luna.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val luz = CRISTALES[(e.semilla and 0x7fffffff) % CRISTALES.size]
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        // El marco del casco, hexagonal, con su sombra y la fila de pernos.
        val marco = Rect(w * 0.005f, h * 0.03f, w * 0.995f, h * 0.97f)
        val punta = marco.height * 0.26f
        drawPath(hexTumbado(marco.translate(2 * u, 4 * u), punta), Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f))
        drawPath(hexTumbado(marco, punta), Brush.verticalGradient(listOf(c(PANEL_OSC), c(CASCO), c(CASCO_OSC)), marco.top, marco.bottom))
        listOf(marco.top + 2.5f * u, marco.bottom - 2.5f * u).forEach { y ->
            var x = marco.left + punta + 8 * u
            while (x < marco.right - punta - 4 * u) { drawCircle(c(CASCO_OSC), 1.3f * u, Offset(x, y)); drawCircle(Color.White.copy(alpha = 0.3f), 0.5f * u, Offset(x - 0.4f * u, y - 0.4f * u)); x += 18 * u }
        }
        // El fondo de la celda; de noche, un campo de estrellas.
        val dentro = marco.deflate(5 * u)
        val celda = hexTumbado(dentro, punta - 3 * u)
        drawPath(celda, c(CELDA))
        clipPath(celda) {
            if (e.noche) for (k in 0 until 60) {
                val o = Offset(dentro.left + dentro.width * azar(e.semilla, 100 + k), dentro.top + dentro.height * azar(e.semilla, 200 + k))
                drawCircle(ESTRELLA.copy(alpha = 0.3f + 0.6f * azar(e.semilla, 300 + k)), (0.4f + 0.9f * azar(e.semilla, 400 + k)) * u, o)
            }
            // Las juntas de los paneles del fondo.
            listOf(0.08f, 0.92f).forEach { f -> drawLine(Color.White.copy(alpha = 0.05f), Offset(dentro.left + dentro.width * f, dentro.top), Offset(dentro.left + dentro.width * f, dentro.bottom), 1 * u) }
        }
        drawPath(celda, CIAN.copy(alpha = 0.4f), style = Stroke(0.8f * u))
        // La cabecera: el emblema (un triángulo ámbar; de noche, una luna), el nombre de la bitácora y la fecha.
        val em = Offset(w * 0.13f, h * 0.17f)
        if (e.noche) {
            drawCircle(ESTRELLA, 4.5f * u, em)
            drawCircle(c(CELDA), 3.8f * u, Offset(em.x + 2 * u, em.y - 1.2f * u))
        } else drawPath(Path().apply { moveTo(em.x, em.y - 4.5f * u); lineTo(em.x + 5 * u, em.y + 3.5f * u); lineTo(em.x - 5 * u, em.y + 3.5f * u); close() }, AMBAR)
        textoEn(medidor, if (e.noche) "TRANSMISIÓN NOCTURNA" else "BITÁCORA DE VUELO", Rect(w * 0.16f, h * 0.11f, w * 0.52f, h * 0.23f), letra, CIAN, 10f, peso = FontWeight.SemiBold, minimo = 6f)
        textoEn(medidor, e.fecha, Rect(w * 0.53f, h * 0.11f, w * 0.83f, h * 0.23f), Letra.codigo, CIAN.copy(alpha = 0.85f), 11f, minimo = 6f)
        drawLine(CIAN.copy(alpha = 0.3f), Offset(w * 0.1f, h * 0.245f), Offset(w * 0.83f, h * 0.245f), 0.7f * u)
        // El visor de vidrio oscuro, con sus corchetes; al abrirse se enciende desde el centro y lo barre una línea.
        val visor = Rect(w * 0.1f, h * 0.27f, w * 0.83f, h * 0.67f)
        drawRoundRect(c(VISOR), visor.topLeft, visor.size, CornerRadius(3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.35f), visor.topLeft, visor.size, CornerRadius(3 * u), style = Stroke(1 * u))
        if (a > 0.01f) {
            val ancho = visor.width * a
            drawRect(CIAN.copy(alpha = 0.16f * a), Offset(visor.center.x - ancho / 2, visor.top), Size(ancho, visor.height))
            val yb = visor.top + visor.height * a
            drawLine(CIAN.copy(alpha = 0.25f), Offset(visor.left, yb), Offset(visor.right, yb), 4 * u)
            drawLine(CIAN.copy(alpha = 0.8f), Offset(visor.left, yb), Offset(visor.right, yb), 1 * u)
        }
        val ce = 8 * u
        listOf(Triple(visor.left, visor.top, 1f to 1f), Triple(visor.right, visor.top, -1f to 1f), Triple(visor.left, visor.bottom, 1f to -1f), Triple(visor.right, visor.bottom, -1f to -1f)).forEach { (x, y, s) ->
            drawPath(Path().apply { moveTo(x + s.first * ce, y); lineTo(x, y); lineTo(x, y + s.second * ce) }, CIAN.copy(alpha = 0.85f), style = Stroke(1.4f * u))
        }
        // Las tres luces de estado, a la derecha del visor.
        listOf(VERDE, AMBAR, CIAN).forEachIndexed { j, col ->
            val o = Offset(w * 0.875f, h * (0.36f + 0.13f * j))
            val prendida = j == 0 || (j == 1 && lleno < 0.5f) || (j == 2 && a > 0.3f)
            drawRoundRect(c(CASCO_OSC), Offset(o.x - 6 * u, o.y - 4 * u), Size(12 * u, 8 * u), CornerRadius(2 * u))
            if (prendida) drawCircle(col.copy(alpha = 0.3f), 4.5f * u, o)
            drawCircle(if (prendida) col else c(CASCO), 2.2f * u, o)
        }
        // La cuna del cristal, con sus dos abrazaderas.
        val cuna = Rect(w * 0.12f, h * 0.71f, w * 0.57f, h * 0.885f)
        drawRoundRect(c(CASCO_OSC), cuna.topLeft, cuna.size, CornerRadius(3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.5f), Offset(cuna.left + 3 * u, cuna.center.y), Size(cuna.width - 6 * u, cuna.height * 0.4f), CornerRadius(2 * u))
        drawLine(CIAN.copy(alpha = 0.35f), Offset(cuna.left + 4 * u, cuna.bottom - 2 * u), Offset(cuna.right - 4 * u, cuna.bottom - 2 * u), 0.8f * u)
        // El cristal de memoria tumbado: un prisma de seis caras con su halo; al abrirse se alza y brilla más.
        val largo = cuna.width * (0.55f + 0.38f * lleno)
        val alto = cuna.height * 0.62f
        val cc = Offset(cuna.center.x, cuna.center.y - h * 0.07f * a)
        val cristal = Rect(cc.x - largo / 2, cc.y - alto / 2, cc.x + largo / 2, cc.y + alto / 2)
        val pc = alto * 0.45f
        listOf(4f to 0.08f + 0.1f * a, 2.2f to 0.14f + 0.12f * a).forEach { (crece, al) -> drawPath(hexTumbado(cristal.inflate(crece * u), pc + crece * u * 0.5f), luz.copy(alpha = al)) }
        drawPath(hexTumbado(cristal, pc), mezcla(luz, Color.Black, if (oscuro) 0.25f else 0.1f))
        drawPath(Path().apply {
            moveTo(cristal.left, cristal.center.y); lineTo(cristal.left + pc, cristal.top); lineTo(cristal.right - pc, cristal.top); lineTo(cristal.right, cristal.center.y); close()
        }, mezcla(luz, Color.White, 0.35f).copy(alpha = 0.8f))
        drawLine(Color.White.copy(alpha = 0.85f), Offset(cristal.left + 1.5f * u, cristal.center.y), Offset(cristal.right - 1.5f * u, cristal.center.y), 0.8f * u)
        drawLine(Color.White.copy(alpha = 0.6f), Offset(cristal.left + pc + 3 * u, cristal.top + alto * 0.2f), Offset(cristal.left + largo * 0.5f, cristal.top + alto * 0.2f), 1 * u)
        drawOval(luz.copy(alpha = 0.2f + 0.1f * a), Offset(cristal.left, cuna.bottom - 5 * u), Size(largo, 4 * u))
        // Las abrazaderas, por encima del cristal (se abren al alzarse).
        listOf(cuna.left + cuna.width * 0.12f, cuna.right - cuna.width * 0.12f).forEach { x ->
            drawRect(c(CASCO), Offset(x - 2 * u, cuna.top - 2 * u - 4 * u * a), Size(4 * u, cuna.height * 0.5f))
            drawRect(Color.White.copy(alpha = 0.2f), Offset(x - 2 * u, cuna.top - 2 * u - 4 * u * a), Size(1 * u, cuna.height * 0.5f))
        }
        // La placa de la celda con las palabras transmitidas y su luz.
        val placa = Rect(w * 0.6f, h * 0.725f, w * 0.88f, h * 0.87f)
        drawRoundRect(c(VISOR), placa.topLeft, placa.size, CornerRadius(2 * u))
        drawRoundRect(CIAN.copy(alpha = 0.45f), placa.topLeft, placa.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        if (!e.vacio && e.palabras > 0)
            textoEn(medidor, palabrasTapa(e.palabras), Rect(placa.left + 3 * u, placa.top, placa.right - 9 * u, placa.bottom), Letra.codigo, CIAN, 9f, minimo = 5.5f)
        else for (j in 0 until 3) drawLine(CIAN.copy(alpha = 0.4f), Offset(placa.left + 6 * u + j * 12 * u, placa.center.y), Offset(placa.left + 12 * u + j * 12 * u, placa.center.y), 1.2f * u)
        drawCircle(if (lleno > 0.5f) VERDE else AMBAR, 1.8f * u, Offset(placa.right - 5 * u, placa.center.y))
    }
}
