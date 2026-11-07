package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.sombraSuave
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /** El ojo que todo lo ve, en su círculo; [cerrado] (de noche): el párpado caído y la luna encima. [fondo] tapa la luna. */
    private fun DrawScope.ojoVigia(c: Offset, r: Float, tinta: Color, rojo: Color, fondo: Color, cerrado: Boolean) {
        val u = density
        drawCircle(tinta, r * 1.32f, c, style = Stroke(1.3f * u))
        drawCircle(tinta.copy(alpha = 0.4f), r * 1.16f, c, style = Stroke(0.6f * u))
        fun pestana(a: Double, desde: Float, hasta: Float) =
            drawLine(tinta, Offset(c.x + r * desde * cos(a).toFloat(), c.y + r * desde * 0.8f * sin(a).toFloat()),
                Offset(c.x + r * hasta * cos(a).toFloat(), c.y + r * hasta * 0.8f * sin(a).toFloat()), 1.1f * u, StrokeCap.Round)
        if (!cerrado) {
            val ojo = Path().apply { moveTo(c.x - r, c.y); quadraticTo(c.x, c.y - r * 0.9f, c.x + r, c.y); quadraticTo(c.x, c.y + r * 0.9f, c.x - r, c.y); close() }
            drawPath(ojo, fondo)
            drawPath(ojo, tinta, style = Stroke(1.5f * u, join = StrokeJoin.Round))
            drawCircle(tinta, r * 0.4f, c)
            drawCircle(rojo, r * 0.17f, c)
            drawCircle(Color.White.copy(alpha = 0.7f), r * 0.07f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
            for (k in 0 until 7) pestana(PI * (1.18 + 0.64 * k / 6.0), 0.62f, 0.86f)
        } else {
            // El párpado cerrado, las pestañas hacia abajo y la luna creciente arriba.
            drawPath(Path().apply { moveTo(c.x - r, c.y); quadraticTo(c.x, c.y + r * 0.8f, c.x + r, c.y) }, tinta, style = Stroke(1.5f * u, cap = StrokeCap.Round))
            for (k in 0 until 5) {
                val a = PI * (0.25 + 0.5 * k / 4.0)
                val o = Offset(c.x + r * 0.78f * cos(a).toFloat(), c.y + r * 0.5f * sin(a).toFloat())
                drawLine(tinta, o, Offset(o.x + r * 0.12f * cos(a).toFloat(), o.y + r * 0.3f), 1.1f * u, StrokeCap.Round)
            }
            val m = Offset(c.x + r * 0.1f, c.y - r * 0.55f)
            drawCircle(rojo, r * 0.36f, m)
            drawCircle(fondo, r * 0.3f, Offset(m.x + r * 0.16f, m.y - r * 0.08f))
        }
    }

    /**
     * La cabecera del expediente: el formulario con su recuadro doble; arriba, la casilla con el número del expediente y la
     * clase de documento en rojo; a la izquierda, la casilla del ojo que todo lo ve (de noche, cerrado bajo la luna); al
     * centro, la jornada mecanografiada en grande y el detalle en su renglón de campo; a la derecha, el sello rojo de
     * «REGISTRADO» (de noche, «ONÍRICO»), torcido, y el código de barras del folio. De noche lo cruza el haz del reflector.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta; val rojo = p.acento
        val hoja = mezcla(mezcla(p.color, if (p.oscura) Color.Black else Color.White, if (p.oscura) 0.18f else 0.35f), Color.Black, if (oscuro && !p.oscura) 0.24f else 0f)
        val caja = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 1.5f * u)
        // La sombra plana del formulario, el papel y el recuadro doble.
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(caja.left + 2 * u, caja.top + 2.5f * u), caja.size)
        drawRect(hoja, caja.topLeft, caja.size)
        drawRect(tinta.copy(alpha = 0.85f), caja.topLeft, caja.size, style = Stroke(1.5f * u))
        val dentro = caja.deflate(3 * u)
        drawRect(tinta.copy(alpha = 0.4f), dentro.topLeft, dentro.size, style = Stroke(0.6f * u))
        // El renglón de las casillas: el número del expediente y la clase de documento.
        val fila = Rect(dentro.left, dentro.top, dentro.right, dentro.top + 22 * u)
        drawRect(tinta.copy(alpha = 0.08f), fila.topLeft, fila.size)
        drawLine(tinta.copy(alpha = 0.75f), Offset(dentro.left, fila.bottom), Offset(dentro.right, fila.bottom), 1 * u)
        val partes = e.rotulo.split(" · ", limit = 2)
        if (partes.size == 2) {
            val corte = fila.left + fila.width * 0.44f
            textoEn(medidor, partes[0], Rect(fila.left + 6 * u, fila.top, corte - 4 * u, fila.bottom), ESTARCIDO, tinta, 10.5f, peso = FontWeight.Bold, espaciado = 0.2f, minimo = 6f)
            drawLine(tinta.copy(alpha = 0.6f), Offset(corte, fila.top), Offset(corte, fila.bottom), 0.8f * u)
            textoEn(medidor, partes[1], Rect(corte + 4 * u, fila.top, fila.right - 6 * u, fila.bottom), ESTARCIDO, rojo, 10.5f, peso = FontWeight.Bold, espaciado = 0.2f, minimo = 6f)
        } else textoEn(medidor, e.rotulo, Rect(fila.left + 6 * u, fila.top, fila.right - 6 * u, fila.bottom), ESTARCIDO, rojo, 12f, peso = FontWeight.Bold, espaciado = 0.8f, minimo = 7f)
        // La casilla del ojo, con sus esquinas de registro.
        val celda = Rect(dentro.left, fila.bottom, dentro.left + 58 * u, dentro.bottom)
        drawLine(tinta.copy(alpha = 0.6f), Offset(celda.right, celda.top), Offset(celda.right, celda.bottom), 0.8f * u)
        ojoVigia(Offset(celda.center.x, celda.center.y - 4 * u), minOf(celda.width, celda.height) * 0.27f, tinta, rojo, hoja, e.noche)
        textoEn(medidor, "Nº %03d".format(e.numero), Rect(celda.left + 3 * u, celda.bottom - 15 * u, celda.right - 3 * u, celda.bottom - 2 * u), Letra.codigo, tinta.copy(alpha = 0.75f), 8.5f, minimo = 6f)
        // La columna del sello y del folio, a la derecha.
        val col = Rect(dentro.right - 74 * u, fila.bottom, dentro.right, dentro.bottom)
        drawLine(tinta.copy(alpha = 0.35f), Offset(col.left, col.top + 4 * u), Offset(col.left, col.bottom - 4 * u), 0.6f * u)
        // El código de barras del folio.
        var bx = col.left + 9 * u; var k = 0
        val by = col.bottom - 22 * u
        while (bx < col.right - 9 * u) {
            val g = (if (azar(e.numero, 30 + k) > 0.55f) 1.6f else 0.8f) * u
            drawRect(tinta.copy(alpha = 0.8f), Offset(bx, by), Size(g, 13 * u))
            bx += g + (0.9f + 1.1f * azar(e.numero, 60 + k)) * u; k++
        }
        textoEn(medidor, "FOLIO %d".format(e.numero), Rect(col.left + 4 * u, col.bottom - 9 * u, col.right - 4 * u, col.bottom - 1 * u), Letra.codigo, tinta.copy(alpha = 0.6f), 7f, minimo = 5f)
        // El sello de goma, torcido, con su tinta un poco desigual.
        val so = Offset(col.center.x, col.top + 22 * u)
        rotate(-11f, so) {
            val r = Rect(so.x - 33 * u, so.y - 10 * u, so.x + 33 * u, so.y + 10 * u)
            drawRoundRect(rojo.copy(alpha = 0.8f), r.topLeft, r.size, CornerRadius(2 * u), style = Stroke(1.6f * u))
            drawRect(rojo.copy(alpha = 0.55f), r.deflate(2.6f * u).topLeft, r.deflate(2.6f * u).size, style = Stroke(0.6f * u))
            textoEn(medidor, if (e.noche) "ONÍRICO" else "REGISTRADO", r.deflate(3.5f * u), ESTARCIDO, rojo.copy(alpha = 0.85f), 9f, peso = FontWeight.Bold, espaciado = 0.2f, minimo = 5f)
            for (j in 0 until 9) drawCircle(hoja.copy(alpha = 0.55f), (0.6f + 0.8f * azar(e.numero, 90 + j)) * u, Offset(r.left + r.width * azar(e.numero, 100 + j), r.top + r.height * azar(e.numero, 110 + j)))
        }
        // El cuerpo: la jornada, en grande, sobre su raya de campo; debajo, el detalle.
        val cuerpo = Rect(celda.right + 6 * u, fila.bottom + 3 * u, col.left - 6 * u, dentro.bottom - 3 * u)
        fechaFuturo(medidor, e.fecha, Rect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.top + 50 * u), Letra.terminal, tinta, 22f, peso = FontWeight.Bold)
        var dx = cuerpo.left
        while (dx < cuerpo.right) { drawLine(tinta.copy(alpha = 0.45f), Offset(dx, cuerpo.top + 52 * u), Offset(minOf(dx + 2 * u, cuerpo.right), cuerpo.top + 52 * u), 0.7f * u); dx += 4 * u }
        fechaFuturo(medidor, e.detalle, Rect(cuerpo.left, cuerpo.top + 54 * u, cuerpo.right, cuerpo.bottom), Letra.codigo, p.tenue, 10.5f, k = 0.86f)
        // De noche, el haz del reflector entra en diagonal.
        if (e.noche) drawPath(Path().apply { moveTo(w * 0.42f, 0f); lineTo(w * 0.62f, 0f); lineTo(w * 0.5f, h); lineTo(w * 0.26f, h); close() }, Color(0xFFDCE3F0).copy(alpha = 0.07f))
    }

    /**
     * La hoja de la declaración: el doble filete del margen y los dos agujeros del archivador con su ojal de metal; las marcas
     * de registro de la imprenta en las esquinas; los renglones del formulario marcados en el margen derecho; el ojo de agua
     * del papel, apenas, al centro; la línea de corte con sus tijeras, el código del formulario y la franja roja de
     * clasificación al pie. En modo oscuro cuelga la lámpara enjaulada de la sala: su cono baja al centro y las orillas se
     * hunden. De noche, el haz del reflector cruza la hoja.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        // El margen: doble filete a la izquierda, uno a la derecha con las marcas de los renglones del formulario.
        listOf(25 * u, 28 * u).forEach { x -> drawLine(tinta.copy(alpha = 0.2f), Offset(x, 0f), Offset(x, h), 0.8f * u) }
        drawLine(tinta.copy(alpha = 0.13f), Offset(w - 16 * u, 0f), Offset(w - 16 * u, h), 0.8f * u)
        var y = 72 * u; var k = 0
        while (y < h - 60 * u) { drawLine(tinta.copy(alpha = if (k % 5 == 0) 0.28f else 0.14f), Offset(w - 16 * u, y), Offset(w - (if (k % 5 == 0) 9 else 12) * u, y), 0.8f * u); y += 24 * u; k++ }
        // Los agujeros del archivador, con su ojal de metal y la marca de centrado entre los dos.
        listOf(0.3f, 0.7f).forEach { f ->
            val o = Offset(13 * u, h * f)
            drawCircle(Color.Black.copy(alpha = 0.16f), 7.5f * u, o)
            drawCircle(t(ACERO_LUZ, oscuro, noche).copy(alpha = 0.7f), 6.2f * u, o, style = Stroke(1.8f * u))
            drawCircle(mezcla(p.color, Color.Black, 0.45f), 4.2f * u, o)
            drawArc(Color.White.copy(alpha = 0.35f), 200f, 70f, false, Offset(o.x - 6.2f * u, o.y - 6.2f * u), Size(12.4f * u, 12.4f * u), style = Stroke(0.8f * u))
        }
        drawLine(tinta.copy(alpha = 0.25f), Offset(6 * u, h * 0.5f), Offset(16 * u, h * 0.5f), 0.8f * u)
        // Las marcas de registro de la imprenta, en las esquinas.
        listOf(Offset(40 * u, 70 * u), Offset(w - 30 * u, 70 * u), Offset(40 * u, h - 22 * u), Offset(w - 30 * u, h - 22 * u)).forEach { o ->
            drawCircle(tinta.copy(alpha = 0.22f), 4 * u, o, style = Stroke(0.7f * u))
            drawLine(tinta.copy(alpha = 0.22f), Offset(o.x - 7 * u, o.y), Offset(o.x + 7 * u, o.y), 0.7f * u)
            drawLine(tinta.copy(alpha = 0.22f), Offset(o.x, o.y - 7 * u), Offset(o.x, o.y + 7 * u), 0.7f * u)
        }
        // El ojo de agua del papel, casi invisible.
        val o = Offset(w / 2, h * 0.52f); val a = w * 0.3f
        val ojo = Path().apply { moveTo(o.x - a, o.y); quadraticTo(o.x, o.y - a * 0.8f, o.x + a, o.y); quadraticTo(o.x, o.y + a * 0.8f, o.x - a, o.y); close() }
        drawPath(ojo, tinta.copy(alpha = 0.03f))
        drawPath(ojo, tinta.copy(alpha = 0.05f), style = Stroke(2 * u))
        drawCircle(tinta.copy(alpha = 0.045f), a * 0.3f, o)
        drawCircle(tinta.copy(alpha = 0.05f), a * 1.15f, o, style = Stroke(1.2f * u))
        // La línea de corte con sus tijeras, el código del formulario y la franja roja de clasificación.
        val yc = h - 44 * u
        var x = 36 * u
        while (x < w - 84 * u) { drawLine(tinta.copy(alpha = 0.3f), Offset(x, yc), Offset(x + 4 * u, yc), 0.8f * u); x += 7 * u }
        val tj = Offset(34 * u, yc)
        listOf(-1f, 1f).forEach { s ->
            drawCircle(tinta.copy(alpha = 0.35f), 2.4f * u, Offset(tj.x - 5 * u, tj.y + s * 3 * u), style = Stroke(0.8f * u))
            drawLine(tinta.copy(alpha = 0.35f), Offset(tj.x - 3 * u, tj.y + s * 2 * u), Offset(tj.x + 6 * u, tj.y - s * 1.5f * u), 0.9f * u)
        }
        var bx = 36 * u; var j = 0
        while (bx < 92 * u) {
            val g = (if (azar(41, j) > 0.5f) 1.4f else 0.7f) * u
            drawRect(tinta.copy(alpha = 0.3f), Offset(bx, h - 32 * u), Size(g, 10 * u)); bx += g + (0.8f + azar(43, j)) * u; j++
        }
        drawPath(Path().apply { moveTo(w - 74 * u, h); lineTo(w - 46 * u, h); lineTo(w, h - 46 * u); lineTo(w, h - 74 * u); close() }, p.acento.copy(alpha = 0.55f))
        drawPath(Path().apply { moveTo(w - 40 * u, h); lineTo(w - 34 * u, h); lineTo(w, h - 34 * u); lineTo(w, h - 40 * u); close() }, p.acento.copy(alpha = 0.4f))
        // En modo oscuro, la lámpara enjaulada que cuelga sobre la mesa: su cono y la penumbra alrededor.
        if (oscuro && !p.oscura) {
            drawPath(Path().apply { moveTo(w * 0.44f, 0f); lineTo(w * 0.56f, 0f); lineTo(w, h * 0.7f); lineTo(0f, h * 0.7f); close() }, BOMBILLA.copy(alpha = 0.04f))
            penumbraFuturo(Offset(w * 0.5f, h * 0.3f), BOMBILLA, velo = 0.2f, orilla = 0.62f)
        }
        // De noche, el haz del reflector.
        if (noche) drawPath(Path().apply { moveTo(w * 0.55f, 0f); lineTo(w * 0.85f, 0f); lineTo(w * 0.35f, h); lineTo(w * 0.05f, h); close() }, Color(0xFFDCE3F0).copy(alpha = 0.045f))
    }

    /** La pinza de acero del archivador por el borde de arriba de la hoja, remachada, con el filete rojo de clasificación. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(fondoBarraFuturo(p, p.tinta, 0.07f, oscuro))
        val placa = Rect(0f, h - 7 * u, w, h)
        drawRect(Brush.verticalGradient(listOf(t(ACERO_LUZ, oscuro, noche), t(ACERO, oscuro, noche), t(ACERO_OSC, oscuro, noche)), placa.top, placa.bottom), placa.topLeft, placa.size)
        drawLine(p.acento.copy(alpha = 0.75f), Offset(0f, placa.top - 1.6f * u), Offset(w, placa.top - 1.6f * u), 1.2f * u)
        var x = 14 * u
        while (x < w) {
            drawCircle(t(ACERO_OSC, oscuro, noche), 1.8f * u, Offset(x, placa.center.y))
            drawCircle(Color.White.copy(alpha = 0.35f), 0.7f * u, Offset(x - 0.5f * u, placa.center.y - 0.5f * u))
            x += 46 * u
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, h - 1 * u), Size(w, 1 * u))
    }

    /** El sello de goma rojo, entintado, con su marco de papel y los huecos donde la goma está gastada. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val rojo = mezcla(mezcla(ROJO, Color.Black, if (oscuro) 0.12f else 0f), NOCHE, if (noche) 0.15f else 0f)
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 3.5f * u)
        drawRoundRect(Color.Black.copy(alpha = 0.28f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(3 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(rojo, Color.White, 0.12f), rojo, mezcla(rojo, Color.Black, 0.22f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3 * u))
        val marco = r.deflate(3 * u)
        drawRoundRect(PAPEL.copy(alpha = 0.75f), marco.topLeft, marco.size, CornerRadius(1.5f * u), style = Stroke(1 * u))
        for (k in 0 until 16) {
            val o = Offset(r.left + r.width * azar(17, k), if (k % 2 == 0) r.top + 2 * u + 3 * u * azar(19, k) else r.bottom - 2 * u - 3 * u * azar(19, k))
            drawCircle(PAPEL.copy(alpha = 0.22f), (0.5f + 0.9f * azar(23, k)) * u, o)
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = PAPEL

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 262.dp

    /** El cartón manila de la carpeta del expediente. */
    private val MANILA = Color(0xFFCDB27A)

    /**
     * La sala de interrogatorio: la pared pintada de verde gris con su zócalo, el espejo falso (con alguien detrás que apenas
     * se adivina), la cámara del rincón con su luz roja y la rejilla del aire. Sobre la mesa de acero remachada, con su argolla,
     * la lámpara articulada apunta a la carpeta del expediente; al lado, el vaso de agua, el cenicero con el cigarro que humea
     * y el lápiz. La carpeta es de cartón manila con su pestaña numerada, la etiqueta mecanografiada con el nombre y la cuenta,
     * el sello de «CONFIDENCIAL», la franja roja, la foto sujeta con un clip y el cordel enrollado en su botón; engorda con las
     * hojas y asoman más formularios de colores por el canto. Al abrirse, el cordel se suelta y la tapa gira sobre el lomo:
     * dentro, la hoja de ruta con sus firmas y el primer folio con la foto, la huella y el sello de «VISTO». En oscuro, la sala
     * se hunde y solo queda la luz de la lámpara.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        // Lo que queda lejos de la lámpara: en oscuro, la sala se hunde de verdad.
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF060708), 0.55f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val hoja = papelTapa(oscuro, false)
        val tinta = Color(0xFF17181A)
        val fondoY = h * 0.43f
        val frenteY = h * 0.83f
        val canto = 13 * u
        // ---- La pared: pintura verde gris, el zócalo más oscuro con su filete y los desconchados.
        val pared = Color(0xFF8C9488); val zocalo = Color(0xFF5B6259)
        drawRect(f(pared), Offset.Zero, Size(w, fondoY))
        drawRect(f(zocalo), Offset(0f, fondoY - h * 0.12f), Size(w, h * 0.12f))
        drawRect(f(MURO_JUNTA), Offset(0f, fondoY - h * 0.12f - 2 * u), Size(w, 2 * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(0f, fondoY - h * 0.12f - 3 * u), Size(w, 1 * u))
        for (k in 0 until 7) {
            val o = Offset(w * azar(41, k), fondoY * (0.1f + 0.6f * azar(43, k)))
            drawPath(contornoIrregular(Rect(o.x, o.y, o.x + (6 + 10 * azar(47, k)) * u, o.y + (3 + 6 * azar(53, k)) * u), 60 + k, 7, 0.35f), f(mezcla(pared, Color.White, 0.18f)))
        }
        // La humedad que baja desde el techo.
        for (k in 0 until 4) {
            val x = w * (0.06f + 0.24f * k + 0.08f * azar(59, k))
            drawRect(Color.Black.copy(alpha = 0.05f), Offset(x, 0f), Size((8 + 10 * azar(61, k)) * u, fondoY * (0.25f + 0.4f * azar(67, k))))
        }
        // La rejilla del aire, arriba al centro.
        val rejilla = Rect(w * 0.4f, h * 0.04f, w * 0.52f, h * 0.1f)
        drawRect(f(ACERO_OSC), rejilla.topLeft, rejilla.size)
        var gy = rejilla.top + 2 * u
        while (gy < rejilla.bottom - 1 * u) { drawLine(f(NEGRO), Offset(rejilla.left + 2 * u, gy), Offset(rejilla.right - 2 * u, gy), 1.2f * u); gy += 3 * u }
        drawRect(f(ACERO_LUZ), rejilla.topLeft, rejilla.size, style = Stroke(1 * u))
        // El espejo falso: el marco de acero, el vidrio oscuro con sus reflejos y, detrás, la silueta del que mira.
        val espejo = Rect(w * 0.6f, h * 0.05f, w * 0.97f, fondoY - h * 0.15f)
        drawRect(f(ACERO_OSC), espejo.inflate(4 * u).topLeft, espejo.inflate(4 * u).size)
        drawRect(f(ACERO_LUZ), espejo.inflate(4 * u).topLeft, Size(espejo.width + 8 * u, 1.2f * u))
        drawRect(f(Color(0xFF1B2126)), espejo.topLeft, espejo.size)
        clipRect(espejo.left, espejo.top, espejo.right, espejo.bottom) {
            val sx = espejo.left + espejo.width * 0.62f
            drawCircle(Color(0xFF2C343A).copy(alpha = if (oscuro) 0.55f else 0.4f), espejo.height * 0.17f, Offset(sx, espejo.top + espejo.height * 0.5f))
            drawRoundRect(Color(0xFF2C343A).copy(alpha = if (oscuro) 0.55f else 0.4f), Offset(sx - espejo.height * 0.38f, espejo.top + espejo.height * 0.7f),
                Size(espejo.height * 0.76f, espejo.height * 0.6f), CornerRadius(espejo.height * 0.2f))
            for (k in 0 until 3) {
                val x = espejo.left + espejo.width * (0.08f + 0.3f * k)
                drawPath(Path().apply { moveTo(x, espejo.bottom); lineTo(x + espejo.width * 0.12f, espejo.bottom); lineTo(x + espejo.width * 0.32f, espejo.top); lineTo(x + espejo.width * 0.2f, espejo.top); close() },
                    Color.White.copy(alpha = 0.05f + 0.02f * k))
            }
            // El reflejo de la lámpara.
            drawCircle(BOMBILLA.copy(alpha = if (oscuro) 0.3f else 0.18f), 4 * u, Offset(espejo.left + espejo.width * 0.2f, espejo.top + espejo.height * 0.4f))
        }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(espejo.left - 4 * u, espejo.bottom + 4 * u), Offset(espejo.right + 4 * u, espejo.bottom + 4 * u), 1.4f * u)
        // La cámara del rincón, en su brazo, con la luz roja encendida.
        val cam = Offset(w * 0.07f, h * 0.08f)
        drawRect(f(ACERO_OSC), Offset(0f, cam.y - 3 * u), Size(cam.x, 6 * u))
        rotate(18f, cam) {
            drawRoundRect(f(Color(0xFFCFD2CC)), Offset(cam.x - 4 * u, cam.y - 6 * u), Size(30 * u, 13 * u), CornerRadius(2.5f * u))
            drawRect(f(ACERO), Offset(cam.x - 4 * u, cam.y + 3 * u), Size(30 * u, 4 * u))
            drawRect(f(NEGRO), Offset(cam.x + 26 * u, cam.y - 5 * u), Size(5 * u, 11 * u))
            drawCircle(f(Color(0xFF2E3A44)), 3.6f * u, Offset(cam.x + 30 * u, cam.y + 0.5f * u))
            drawCircle(Color.White.copy(alpha = 0.4f), 1 * u, Offset(cam.x + 29 * u, cam.y - 0.5f * u))
        }
        val led = Offset(cam.x + 9 * u, cam.y + 2 * u)
        drawCircle(ROJO.copy(alpha = 0.25f), 4 * u, led)
        drawCircle(Color(0xFFFF4A3A), 1.5f * u, led)

        // ---- La mesa de acero: la cubierta (un poco en perspectiva), el canto remachado y, debajo, las patas en la sombra.
        val cubierta = Path().apply { moveTo(w * 0.035f, fondoY); lineTo(w * 0.965f, fondoY); lineTo(w * 1.01f, frenteY); lineTo(-w * 0.01f, frenteY); close() }
        drawRect(f(Color(0xFF2A2D2B)), Offset(0f, frenteY), Size(w, h - frenteY))
        drawRect(f(SUELO), Offset(0f, h * 0.95f), Size(w, h * 0.05f))
        listOf(w * 0.05f, w * 0.91f).forEach { x ->
            drawRect(f(ACERO_OSC), Offset(x, frenteY + canto), Size(w * 0.04f, h - frenteY - canto))
            drawRect(f(ACERO), Offset(x, frenteY + canto), Size(w * 0.012f, h - frenteY - canto))
        }
        drawRect(f(ACERO_OSC), Offset(w * 0.09f, h * 0.93f), Size(w * 0.82f, 3 * u))
        drawPath(cubierta, Brush.verticalGradient(listOf(c(ACERO_LUZ), c(ACERO), c(mezcla(ACERO, ACERO_OSC, 0.5f))), fondoY, frenteY))
        clipPath(cubierta) {
            // El acero cepillado y los rayones.
            for (k in 0 until 46) {
                val y = fondoY + (frenteY - fondoY) * azar(71, k)
                val x = w * azar(73, k)
                drawLine(Color.White.copy(alpha = 0.05f + 0.05f * azar(79, k)), Offset(x, y), Offset(x + (20 + 60 * azar(83, k)) * u, y), 0.6f * u)
            }
            for (k in 0 until 9) {
                val o = Offset(w * azar(89, k), fondoY + (frenteY - fondoY) * azar(97, k))
                drawLine(Color.Black.copy(alpha = 0.16f), o, Offset(o.x + (6 + 8 * azar(101, k)) * u, o.y - (2 + 4 * azar(103, k)) * u), 0.6f * u)
            }
        }
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(w * 0.035f, fondoY), Offset(w * 0.965f, fondoY), 1.2f * u)
        drawRect(c(ACERO), Offset(0f, frenteY), Size(w, canto))
        drawRect(c(ACERO_LUZ), Offset(0f, frenteY), Size(w, 1.6f * u))
        drawRect(c(ACERO_OSC), Offset(0f, frenteY + canto - 2.5f * u), Size(w, 2.5f * u))
        var rx = w * 0.04f
        while (rx < w) { drawCircle(c(ACERO_OSC), 1.5f * u, Offset(rx, frenteY + canto * 0.48f)); drawCircle(Color.White.copy(alpha = 0.3f), 0.6f * u, Offset(rx - 0.4f * u, frenteY + canto * 0.42f)); rx += w * 0.075f }

        // ---- La lámpara articulada, atornillada atrás a la izquierda, que apunta a la carpeta.
        val baseL = Offset(w * 0.1f, fondoY + 16 * u)
        val codo = Offset(w * 0.055f, h * 0.17f)
        val nuca = Offset(w * 0.25f, h * 0.085f)
        val blanco = Offset(w * 0.52f, frenteY - h * 0.2f)
        val dx = blanco.x - nuca.x; val dy = blanco.y - nuca.y
        val largo = sqrt(dx * dx + dy * dy)
        val ex = dx / largo; val ey = dy / largo
        val boca = Offset(nuca.x + ex * 30 * u, nuca.y + ey * 30 * u)
        val m1 = Offset(boca.x - ey * 16 * u, boca.y + ex * 16 * u); val m2 = Offset(boca.x + ey * 16 * u, boca.y - ex * 16 * u)
        // El cono de luz, plano, y la mancha en la mesa (el primer degradado).
        drawPath(Path().apply { moveTo(m1.x, m1.y); lineTo(m2.x, m2.y); lineTo(w * 0.9f, frenteY - 4 * u); lineTo(w * 0.16f, frenteY + 2 * u); close() }, BOMBILLA.copy(alpha = if (oscuro) 0.07f else 0.05f))
        val pozo = Offset(w * 0.53f, frenteY - h * 0.17f)
        drawOval(Brush.radialGradient(listOf(BOMBILLA.copy(alpha = if (oscuro) 0.3f else 0.16f), BOMBILLA.copy(alpha = 0f)), pozo, w * 0.42f),
            Offset(pozo.x - w * 0.46f, pozo.y - h * 0.26f), Size(w * 0.92f, h * 0.5f))
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(baseL.x - 16 * u, baseL.y - 4 * u), Size(36 * u, 11 * u))
        drawOval(c(ACERO_OSC), Offset(baseL.x - 15 * u, baseL.y - 6 * u), Size(30 * u, 10 * u))
        drawOval(c(ACERO), Offset(baseL.x - 13 * u, baseL.y - 7.5f * u), Size(26 * u, 8 * u))
        drawLine(c(ACERO_OSC), baseL, codo, 3.2f * u, cap = StrokeCap.Round)
        drawLine(c(ACERO_LUZ), Offset(baseL.x - 1 * u, baseL.y), Offset(codo.x - 1 * u, codo.y), 0.8f * u)
        drawLine(c(ACERO_OSC), codo, nuca, 3 * u, cap = StrokeCap.Round)
        // Los resortes de los brazos.
        for (k in 0 until 7) {
            val f = 0.2f + 0.09f * k
            val p = Offset(baseL.x + (codo.x - baseL.x) * f + 3 * u, baseL.y + (codo.y - baseL.y) * f)
            drawLine(c(ACERO_LUZ), Offset(p.x - 1.6f * u, p.y), Offset(p.x + 1.6f * u, p.y - 1.4f * u), 0.7f * u)
        }
        drawCircle(c(ACERO), 3.6f * u, codo); drawCircle(c(ACERO_OSC), 1.4f * u, codo)
        val t1 = Offset(nuca.x - ey * 6 * u, nuca.y + ex * 6 * u); val t2 = Offset(nuca.x + ey * 6 * u, nuca.y - ex * 6 * u)
        val pantalla = Path().apply { moveTo(t1.x, t1.y); lineTo(t2.x, t2.y); lineTo(m2.x, m2.y); lineTo(m1.x, m1.y); close() }
        drawPath(pantalla, c(Color(0xFF3C4A3E)))
        drawLine(Color.White.copy(alpha = 0.22f), t2, m2, 1.2f * u)
        drawLine(c(Color(0xFF242C25)), m1, m2, 2.4f * u, cap = StrokeCap.Round)
        drawCircle(BOMBILLA.copy(alpha = 0.5f), 7 * u, Offset(boca.x + ex * 1 * u, boca.y + ey * 1 * u))
        drawCircle(mezcla(BOMBILLA, Color.White, 0.6f), 3.6f * u, Offset(boca.x - ex * 1 * u, boca.y - ey * 1 * u))
        drawCircle(c(ACERO), 3 * u, nuca)

        // ---- El vaso de agua, a la izquierda, delante.
        val vaso = Rect(w * 0.06f, frenteY - 48 * u, w * 0.06f + 22 * u, frenteY - 14 * u)
        drawOval(Color.Black.copy(alpha = 0.18f), Offset(vaso.left - 1 * u, vaso.bottom - 3 * u), Size(vaso.width + 6 * u, 7 * u))
        val cuerpoVaso = Path().apply { moveTo(vaso.left, vaso.top); lineTo(vaso.right, vaso.top); lineTo(vaso.right - 2 * u, vaso.bottom); lineTo(vaso.left + 2 * u, vaso.bottom); close() }
        drawPath(cuerpoVaso, Color.White.copy(alpha = if (oscuro) 0.08f else 0.18f))
        val agua = vaso.top + vaso.height * 0.38f
        drawPath(Path().apply { moveTo(vaso.left + 0.8f * u, agua); lineTo(vaso.right - 0.8f * u, agua); lineTo(vaso.right - 2 * u, vaso.bottom); lineTo(vaso.left + 2 * u, vaso.bottom); close() }, c(Color(0xFF9FB8BC)).copy(alpha = 0.4f))
        drawOval(c(Color(0xFFBFD3D6)).copy(alpha = 0.6f), Offset(vaso.left + 0.8f * u, agua - 2 * u), Size(vaso.width - 1.6f * u, 4 * u))
        drawOval(Color.White.copy(alpha = 0.5f), Offset(vaso.left, vaso.top - 2.5f * u), Size(vaso.width, 5 * u), style = Stroke(0.9f * u))
        drawPath(cuerpoVaso, Color.White.copy(alpha = 0.4f), style = Stroke(0.8f * u))
        drawLine(Color.White.copy(alpha = 0.55f), Offset(vaso.left + 3.5f * u, vaso.top + 3 * u), Offset(vaso.left + 4.5f * u, vaso.bottom - 4 * u), 1.4f * u)

        // ---- El cenicero de lata, atrás a la derecha, con las colillas y el cigarro que humea.
        val cen = Offset(w * 0.875f, fondoY + 30 * u)
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(cen.x - 19 * u, cen.y - 3 * u), Size(40 * u, 13 * u))
        drawOval(c(ACERO_OSC), Offset(cen.x - 18 * u, cen.y - 7 * u), Size(36 * u, 14 * u))
        drawOval(c(ACERO_LUZ), Offset(cen.x - 18 * u, cen.y - 9 * u), Size(36 * u, 13 * u))
        drawOval(c(Color(0xFF4E5250)), Offset(cen.x - 14 * u, cen.y - 7.5f * u), Size(28 * u, 9.5f * u))
        drawOval(c(Color(0xFF8A8A84)), Offset(cen.x - 8 * u, cen.y - 5 * u), Size(14 * u, 5 * u))
        listOf(-7f to 12f, 3f to -20f).forEach { (ox, g) ->
            rotate(g, Offset(cen.x + ox * u, cen.y - 3 * u)) {
                drawRect(c(Color(0xFFE9E2D0)), Offset(cen.x + ox * u - 4 * u, cen.y - 4.2f * u), Size(6 * u, 2.4f * u))
                drawRect(c(Color(0xFFC7853F)), Offset(cen.x + ox * u + 2 * u, cen.y - 4.2f * u), Size(2.6f * u, 2.4f * u))
            }
        }
        val brasa = Offset(cen.x - 24 * u, cen.y - 10 * u)
        drawLine(c(Color(0xFFEDE7D8)), Offset(cen.x - 6 * u, cen.y - 7 * u), brasa, 2.6f * u)
        drawLine(c(Color(0xFFC7853F)), Offset(cen.x - 6 * u, cen.y - 7 * u), Offset(cen.x - 10 * u, cen.y - 7.8f * u), 2.6f * u)
        drawCircle(Color(0xFFFF6A2A).copy(alpha = if (oscuro) 0.4f else 0.25f), 3.4f * u, brasa)
        drawCircle(Color(0xFFFFA040), 1.4f * u, brasa)
        drawPath(Path().apply {
            moveTo(brasa.x, brasa.y - 2 * u)
            cubicTo(brasa.x - 8 * u, brasa.y - 14 * u, brasa.x + 8 * u, brasa.y - 22 * u, brasa.x - 2 * u, brasa.y - 34 * u)
            cubicTo(brasa.x - 10 * u, brasa.y - 44 * u, brasa.x + 6 * u, brasa.y - 52 * u, brasa.x + 2 * u, brasa.y - 62 * u)
        }, Color.White.copy(alpha = if (oscuro) 0.14f else 0.3f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        // La argolla soldada para las esposas, delante a la derecha.
        val arg = Offset(w * 0.9f, frenteY - 12 * u)
        drawRect(c(ACERO_OSC), Offset(arg.x - 7 * u, arg.y - 1 * u), Size(14 * u, 5 * u))
        drawArc(c(ACERO_LUZ), 180f, 180f, false, Offset(arg.x - 6 * u, arg.y - 7 * u), Size(12 * u, 14 * u), style = Stroke(2 * u))
        drawArc(Color.Black.copy(alpha = 0.4f), 0f, 180f, false, Offset(arg.x - 6 * u, arg.y - 5 * u), Size(12 * u, 8 * u), style = Stroke(1 * u))
        // El lápiz.
        rotate(-14f, Offset(w * 0.86f, fondoY + 58 * u)) {
            val lx = w * 0.86f - 24 * u; val ly = fondoY + 58 * u
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(lx + 1 * u, ly + 2 * u), Size(48 * u, 4 * u))
            drawRect(c(Color(0xFFD9A62E)), Offset(lx, ly), Size(42 * u, 4 * u))
            drawRect(c(Color(0xFFB5861C)), Offset(lx, ly + 2.6f * u), Size(42 * u, 1.4f * u))
            drawRect(c(ACERO_LUZ), Offset(lx - 4 * u, ly), Size(4 * u, 4 * u))
            drawRect(c(Color(0xFFC86A6A)), Offset(lx - 8 * u, ly), Size(4 * u, 4 * u))
            drawPath(Path().apply { moveTo(lx + 42 * u, ly); lineTo(lx + 50 * u, ly + 2 * u); lineTo(lx + 42 * u, ly + 4 * u); close() }, c(Color(0xFFE6CFA4)))
            drawCircle(c(NEGRO), 0.9f * u, Offset(lx + 49.4f * u, ly + 2 * u))
        }

        // ---- La carpeta del expediente.
        val g = grosorLibreta(e.hojas, u)
        val ancho = w * 0.55f
        val angulo = giroTapa(a)
        val corre = ancho * 0.2f * (1f - cos(angulo)) / 2f
        val x0 = w * 0.25f + corre; val x1 = x0 + ancho
        val fy = frenteY - 11 * u
        val hondo = h * 0.25f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val manila = c(MANILA)
        val manilaOsc = mezcla(manila, Color.Black, 0.22f)
        sombraSuave(Rect(x0, fy - hondo, x1, fy), 2 * u, 9 * u, if (oscuro) 0.4f else 0.3f)
        // La tapa de atrás, con su canto.
        drawRect(manilaOsc, Offset(x0, fy - hondo), Size(ancho, hondo))
        drawRect(mezcla(manila, Color.Black, 0.32f), Offset(x0, fy - 1.6f * u), Size(ancho, 1.6f * u))
        // Los formularios que asoman por el canto: más cuanto más hojas.
        val asoman = if (e.hojas <= 0) 0 else (1 + e.hojas / 9).coerceAtMost(10)
        val colores = listOf(hoja, c(Color(0xFFE7B9B9)), c(Color(0xFFB9CCE6)), hoja, c(Color(0xFFE9DC9A)))
        for (k in 0 until asoman) {
            val y = arriba + 4 * u + (hondo - 12 * u) * azar(e.semilla, 10 + k)
            val alto = (8 + 16 * azar(e.semilla, 20 + k)) * u
            val sale = (2.5f + 7 * azar(e.semilla, 30 + k)) * u
            val col = colores[k % colores.size]
            drawRect(col, Offset(x1 - 4 * u, y), Size(sale + 4 * u, alto.coerceAtMost(fy - y - 2 * u).coerceAtLeast(2 * u)))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(x1 - 4 * u, y), Size(sale + 4 * u, alto.coerceAtMost(fy - y - 2 * u).coerceAtLeast(2 * u)), style = Stroke(0.5f * u))
        }
        // El canto de las hojas, entre las dos tapas.
        cantoHojas(Rect(x0 + 1.5f * u, tapaY + 1.6f * u, x1 - 1 * u, fy - 1.6f * u), mezcla(hoja, Color.Black, 0.06f), tinta, e.semilla)
        // El primer folio, que se ve al abrir.
        if (a > 0.02f) {
            val f = Rect(x0 + 3 * u, arriba + 3 * u, x1 - 3 * u, tapaY + 1 * u)
            drawRect(hoja, f.topLeft, f.size)
            drawRect(tinta.copy(alpha = 0.7f), Offset(f.left + f.width * 0.32f, f.top + 6 * u), Size(f.width * 0.4f, 2.2f * u))
            textoEn(medidor, "FOLIO ${e.hojas}", Rect(f.right - f.width * 0.26f, f.top + 2 * u, f.right - 4 * u, f.top + 12 * u), Letra.codigo, tinta.copy(alpha = 0.75f), 8.5f, minimo = 5f)
            drawLine(c(ROJO).copy(alpha = 0.6f), Offset(f.left + 4 * u, f.top + 14 * u), Offset(f.right - 4 * u, f.top + 14 * u), 0.8f * u)
            // La foto, sujeta con su clip.
            val foto = Rect(f.left + 7 * u, f.top + 19 * u, f.left + 7 * u + f.height * 0.42f, f.top + 19 * u + f.height * 0.52f)
            drawRect(c(Color(0xFF8C908E)), foto.topLeft, foto.size)
            drawCircle(c(Color(0xFF575C5A)), foto.width * 0.2f, Offset(foto.center.x, foto.top + foto.height * 0.4f))
            drawRoundRect(c(Color(0xFF575C5A)), Offset(foto.left + foto.width * 0.12f, foto.top + foto.height * 0.66f), Size(foto.width * 0.76f, foto.height * 0.4f), CornerRadius(foto.width * 0.3f))
            drawRect(Color.White.copy(alpha = 0.85f), foto.topLeft, foto.size, style = Stroke(1.6f * u))
            drawRoundRect(c(ACERO_LUZ), Offset(foto.left + 4 * u, foto.top - 4 * u), Size(5 * u, 13 * u), CornerRadius(2.5f * u), style = Stroke(0.9f * u))
            // Los renglones mecanografiados.
            var ry = f.top + 21 * u; var k = 0
            while (ry < f.bottom - 6 * u) {
                val iz = if (ry < foto.bottom + 3 * u) foto.right + 6 * u else f.left + 7 * u
                val fin = iz + (f.right - 8 * u - iz) * (0.45f + 0.55f * azar(e.semilla, 120 + k))
                drawLine(tinta.copy(alpha = 0.38f), Offset(iz, ry), Offset(fin, ry), 1.1f * u)
                ry += 5.5f * u; k++
            }
            // La huella y el sello de «VISTO».
            val hu = Offset(f.right - f.width * 0.2f, f.bottom - f.height * 0.3f)
            for (j in 1..5) drawOval(tinta.copy(alpha = 0.22f), Offset(hu.x - j * 1.6f * u, hu.y - j * 2.1f * u), Size(j * 3.2f * u, j * 4.2f * u), style = Stroke(0.6f * u))
            rotate(-12f, Offset(f.left + f.width * 0.55f, f.bottom - f.height * 0.25f)) {
                val r = Rect(f.left + f.width * 0.42f, f.bottom - f.height * 0.33f, f.left + f.width * 0.68f, f.bottom - f.height * 0.17f)
                drawRect(c(ROJO).copy(alpha = 0.7f), r.topLeft, r.size, style = Stroke(1.3f * u))
                textoEn(medidor, "VISTO", r.deflate(1.5f * u), ESTARCIDO, c(ROJO).copy(alpha = 0.8f), 10f, peso = FontWeight.Bold, espaciado = 0.8f, minimo = 5f)
            }
            // La sombra que la tapa echa sobre el folio mientras se levanta.
            val sombra = sin(angulo).coerceAtLeast(0f)
            if (cos(angulo) > 0f) sombraTapa(x0 + ancho * cos(angulo), arriba, ancho * 0.24f * sombra, tapaY - arriba)
        }
        // La tapa de delante, que gira sobre el lomo.
        tapaLateral(x0, angulo, 0.42f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            // La pestaña de atrás con su número.
            val pes = Rect(x0 + ancho * 0.6f, arriba - 11 * u, x0 + ancho * 0.88f, arriba + 1 * u)
            drawPath(Path().apply { moveTo(pes.left, pes.bottom); lineTo(pes.left + 5 * u, pes.top); lineTo(pes.right - 5 * u, pes.top); lineTo(pes.right, pes.bottom); close() }, manila)
            drawRect(hoja, Offset(pes.left + 7 * u, pes.top + 2.5f * u), Size(pes.width - 14 * u, 6.5f * u))
            textoEn(medidor, "Nº " + (1000 + (e.semilla and 0x7fffffff) % 9000), Rect(pes.left + 7 * u, pes.top + 2.5f * u, pes.right - 7 * u, pes.top + 9 * u), Letra.codigo, tinta, 7.5f, minimo = 4.5f)
            // La foto que asoma por arriba, con el clip.
            rotate(-6f, Offset(x0 + ancho * 0.2f, arriba)) {
                drawRect(c(Color(0xFFE6E2D8)), Offset(x0 + ancho * 0.12f, arriba - 8 * u), Size(ancho * 0.16f, 14 * u))
                drawRect(c(Color(0xFF7E8380)), Offset(x0 + ancho * 0.13f, arriba - 6.6f * u), Size(ancho * 0.14f, 10 * u))
            }
            drawRect(manila, r.topLeft, r.size)
            drawRect(mezcla(manila, Color.Black, 0.3f), Offset(x0, tapaY), Size(ancho, 1.6f * u))
            // Las fibras del cartón y el doblez del lomo.
            for (k in 0 until 60) {
                val o = Offset(r.left + r.width * azar(e.semilla, 200 + k), r.top + r.height * azar(e.semilla, 300 + k))
                drawLine((if (k % 2 == 0) Color.Black else Color.White).copy(alpha = 0.07f), o, Offset(o.x + (3 + 5 * azar(e.semilla, 400 + k)) * u, o.y + 0.5f * u), 0.7f * u)
            }
            drawLine(Color.Black.copy(alpha = 0.2f), Offset(x0 + 6 * u, r.top), Offset(x0 + 6 * u, r.bottom), 0.8f * u)
            drawLine(Color.Black.copy(alpha = 0.14f), Offset(x0 + 9 * u, r.top), Offset(x0 + 9 * u, r.bottom), 0.8f * u)
            drawRect(Color.White.copy(alpha = 0.14f), r.topLeft, Size(r.width, 1.2f * u))
            // El clip de la foto.
            drawRoundRect(c(ACERO_LUZ), Offset(x0 + ancho * 0.17f, arriba - 5 * u), Size(5 * u, 14 * u), CornerRadius(2.5f * u), style = Stroke(1 * u))
            // La franja roja de clasificación, abajo.
            drawRect(c(ROJO), Offset(r.left, r.bottom - r.height * 0.13f), Size(r.width, 3 * u))
            // La etiqueta mecanografiada: el nombre y la cuenta.
            val et = Rect(r.left + ancho * 0.12f, r.top + r.height * 0.13f, r.right - ancho * 0.1f, r.top + r.height * 0.56f)
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(et.left + 1 * u, et.top + 1.5f * u), et.size)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(tinta.copy(alpha = 0.8f), et.topLeft, et.size, style = Stroke(1.2f * u))
            drawRect(tinta.copy(alpha = 0.4f), et.deflate(2.2f * u).topLeft, et.deflate(2.2f * u).size, style = Stroke(0.5f * u))
            val corte = et.top + et.height * 0.58f
            textoEn(medidor, e.nombre, Rect(et.left + 4 * u, et.top + 3 * u, et.right - 4 * u, corte), ESTARCIDO, tinta, 14f, peso = FontWeight.Bold, minimo = 6f)
            drawLine(c(ROJO).copy(alpha = 0.7f), Offset(et.left + 5 * u, corte), Offset(et.right - 5 * u, corte), 0.8f * u)
            textoEn(medidor, e.cuenta, Rect(et.left + 5 * u, corte + 1 * u, et.right - 5 * u, et.bottom - 3 * u), Letra.codigo, tinta.copy(alpha = 0.85f), 10f, minimo = 5.5f)
            // El sello de «CONFIDENCIAL», torcido.
            val sc = Offset(r.left + r.width * 0.36f, r.top + r.height * 0.73f)
            rotate(-7f, sc) {
                val sr = Rect(sc.x - ancho * 0.2f, sc.y - r.height * 0.09f, sc.x + ancho * 0.2f, sc.y + r.height * 0.09f)
                drawRect(c(ROJO).copy(alpha = 0.72f), sr.topLeft, sr.size, style = Stroke(1.6f * u))
                textoEn(medidor, "CONFIDENCIAL", sr.deflate(2 * u), ESTARCIDO, c(ROJO).copy(alpha = 0.78f), 10.5f, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 4.5f)
            }
            // El botón del cordel, con el cordel enrollado (o suelto, si ya se abrió).
            val bo = Offset(r.right - ancho * 0.1f, r.top + r.height * 0.74f)
            drawCircle(Color.Black.copy(alpha = 0.2f), 6.5f * u, Offset(bo.x + 0.8f * u, bo.y + 1.2f * u))
            drawCircle(mezcla(manila, Color.Black, 0.4f), 6 * u, bo)
            drawCircle(mezcla(manila, Color.White, 0.1f), 4.4f * u, bo)
            drawCircle(mezcla(manila, Color.Black, 0.5f), 0.9f * u, bo)
            val cordel = c(Color(0xFFB0352B))
            if (a < 0.08f) {
                drawPath(Path().apply { moveTo(bo.x - 5 * u, bo.y - 3 * u); cubicTo(bo.x + 7 * u, bo.y - 9 * u, bo.x + 9 * u, bo.y + 7 * u, bo.x - 4 * u, bo.y + 4 * u)
                    cubicTo(bo.x - 9 * u, bo.y + 1 * u, bo.x + 3 * u, bo.y - 8 * u, bo.x + 6 * u, bo.y + 2 * u) }, cordel, style = Stroke(1.2f * u))
                drawPath(Path().apply { moveTo(bo.x + 4 * u, bo.y + 3 * u); quadraticTo(bo.x + ancho * 0.08f, bo.y + 10 * u, r.right + 2 * u, r.bottom + 1 * u) }, cordel, style = Stroke(1.2f * u))
            } else drawPath(Path().apply { moveTo(bo.x, bo.y); cubicTo(bo.x + 10 * u, bo.y + 6 * u, bo.x - 6 * u, bo.y + 12 * u, bo.x + 4 * u, r.bottom - 1 * u) }, cordel, style = Stroke(1.2f * u))
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), Offset(r.left, r.top - 11 * u), Size(r.width, r.height + 13 * u))
        }, dentro = { sombra ->
            // Por dentro: el cartón más oscuro con la hoja de ruta y sus firmas.
            val r = Rect(x0 - ancho, arriba, x0, tapaY + 1.6f * u)
            drawRect(mezcla(manila, Color.Black, 0.12f), r.topLeft, r.size)
            val ruta = Rect(r.left + ancho * 0.1f, r.top + r.height * 0.12f, r.right - ancho * 0.12f, r.bottom - r.height * 0.12f)
            drawRect(hoja, ruta.topLeft, ruta.size)
            drawRect(tinta.copy(alpha = 0.7f), ruta.topLeft, ruta.size, style = Stroke(1 * u))
            textoEn(medidor, "HOJA DE RUTA", Rect(ruta.left, ruta.top + 1 * u, ruta.right, ruta.top + ruta.height * 0.2f), ESTARCIDO, tinta, 9f, peso = FontWeight.Bold, espaciado = 0.8f, minimo = 5f)
            for (j in 1..4) drawLine(tinta.copy(alpha = 0.45f), Offset(ruta.left, ruta.top + ruta.height * (0.2f * j)), Offset(ruta.right, ruta.top + ruta.height * (0.2f * j)), 0.6f * u)
            listOf(0.34f, 0.7f).forEach { f -> drawLine(tinta.copy(alpha = 0.45f), Offset(ruta.left + ruta.width * f, ruta.top + ruta.height * 0.2f), Offset(ruta.left + ruta.width * f, ruta.bottom), 0.6f * u) }
            for (j in 1..3) {
                val y = ruta.top + ruta.height * (0.2f * j + 0.12f)
                val x = ruta.left + ruta.width * 0.4f
                drawPath(Path().apply { moveTo(x, y); cubicTo(x + 6 * u, y - 6 * u, x + 8 * u, y + 4 * u, x + 14 * u, y - 2 * u); quadraticTo(x + 18 * u, y - 5 * u, x + 22 * u, y + 1 * u) }, c(Color(0xFF2A3F7A)).copy(alpha = 0.7f), style = Stroke(0.9f * u))
            }
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size)
        })
    }

    /**
     * La pestaña de un día: la ficha de cartón manila de un archivo, con su lengüeta (cada una en otro lugar), la letra de la
     * semana en la lengüeta, la etiqueta mecanografiada con el día y la pegatina redonda del color del ánimo (vacía, si no hay).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val hoja = papelTapa(oscuro, false)
        val tinta = Color(0xFF17181A)
        val manila = c(MANILA)
        rotate(-1.5f + 3f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val lx = 3 * u + 13 * u * azar(e.semilla, 2)
            val lengua = Path().apply { moveTo(lx, 13 * u); lineTo(lx + 4 * u, 2 * u); lineTo(lx + 20 * u, 2 * u); lineTo(lx + 24 * u, 13 * u); close() }
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(2.5f * u, 13.5f * u), Size(w - 4 * u, h - 14.5f * u))
            drawPath(lengua, manila)
            drawRect(manila, Offset(1.5f * u, 12 * u), Size(w - 4 * u, h - 14 * u))
            drawLine(Color.White.copy(alpha = 0.18f), Offset(lx + 4 * u, 2.6f * u), Offset(lx + 20 * u, 2.6f * u), 0.8f * u)
            drawRect(mezcla(manila, Color.Black, 0.3f), Offset(1.5f * u, h - 3.4f * u), Size(w - 4 * u, 1.4f * u))
            textoEn(medidor, e.semana.uppercase(), Rect(lx + 4 * u, 3 * u, lx + 20 * u, 12.5f * u), ESTARCIDO, tinta.copy(alpha = 0.85f), 8f, peso = FontWeight.Bold, minimo = 5f)
            // La etiqueta con el día.
            val et = Rect(5 * u, 17 * u, w - 7.5f * u, 38 * u)
            drawRect(hoja, et.topLeft, et.size)
            drawRect(tinta.copy(alpha = 0.75f), et.topLeft, et.size, style = Stroke(0.9f * u))
            textoEn(medidor, e.dia, et.deflate(1.5f * u), Letra.codigo, tinta, 17f, peso = FontWeight.Bold, minimo = 8f)
            // La pegatina del ánimo, con su filo blanco.
            val p = Offset(w * 0.33f, 46 * u)
            if (e.tono != null) {
                drawCircle(Color.White.copy(alpha = if (oscuro) 0.75f else 0.95f), 5.6f * u, p)
                drawCircle(e.tono, 4.4f * u, p)
            } else drawCircle(tinta.copy(alpha = 0.35f), 4.4f * u, p, style = Stroke(0.8f * u))
            for (j in 0 until 3) drawLine(tinta.copy(alpha = 0.35f), Offset(w * 0.55f, (43 + j * 3) * u), Offset(w * 0.8f - j * 2 * u, (43 + j * 3) * u), 0.7f * u)
        }
    }

    /** El rótulo de un mes: el riel de acero atornillado con la cinta negra de la rotuladora y las letras en relieve. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val riel = Rect(0f, 5 * u, w, h - 5 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(riel.left, riel.top + 2 * u), riel.size)
        drawRect(Brush.verticalGradient(listOf(c(ACERO_LUZ), c(ACERO), c(ACERO_OSC)), riel.top, riel.bottom), riel.topLeft, riel.size)
        drawLine(Color.White.copy(alpha = 0.3f), Offset(0f, riel.top + 1 * u), Offset(w, riel.top + 1 * u), 0.8f * u)
        listOf(9 * u, w - 9 * u).forEach { x ->
            drawCircle(c(ACERO_OSC), 3.4f * u, Offset(x, h / 2))
            drawCircle(c(ACERO_LUZ), 3.4f * u, Offset(x, h / 2), style = Stroke(0.7f * u))
            rotate(35f, Offset(x, h / 2)) { drawLine(c(NEGRO), Offset(x - 2.4f * u, h / 2), Offset(x + 2.4f * u, h / 2), 1 * u) }
        }
        val txt = texto.uppercase()
        val tam = 11f
        val largo = (anchoTexto(medidor, txt, ESTARCIDO, tam, FontWeight.Bold, 1.4f) + 26 * u).coerceAtMost(w - 44 * u)
        val cinta = Rect(w / 2 - largo / 2, h / 2 - 8 * u, w / 2 + largo / 2, h / 2 + 8 * u)
        drawPath(Path().apply {
            moveTo(cinta.left + 3 * u, cinta.top); lineTo(cinta.right, cinta.top); lineTo(cinta.right - 3 * u, cinta.bottom); lineTo(cinta.left, cinta.bottom); close()
        }, c(Color(0xFF1C1E21)))
        drawLine(Color.White.copy(alpha = 0.12f), Offset(cinta.left + 3 * u, cinta.top + 1 * u), Offset(cinta.right - 1 * u, cinta.top + 1 * u), 0.8f * u)
        val dentro = Rect(cinta.left + 10 * u, cinta.top, cinta.right - 10 * u, cinta.bottom)
        textoEn(medidor, txt, dentro.translate(0f, 0.7f * u), ESTARCIDO, Color.Black.copy(alpha = 0.7f), tam, peso = FontWeight.Bold, espaciado = 1.4f, minimo = 6f)
        textoEn(medidor, txt, dentro, ESTARCIDO, Color(0xFFF2F2EE).copy(alpha = if (oscuro) 0.85f else 0.95f), tam, peso = FontWeight.Bold, espaciado = 1.4f, minimo = 6f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El manila del sobre de correo interno, su cara de dentro y el cordel rojo con que se ata a sus botones. */
    private val SOBRE_INT = Color(0xFFCFB47E)
    private val SOBRE_FORRO = Color(0xFF9E8556)
    private val CORDEL_ROJO = Color(0xFFB3241D)

    /**
     * El sobre de correo interno del Ministerio, de manila, con la columna de agujeros que deja ver si va vacío. La solapa
     * recta lleva impreso «CORREO INTERNO» y la franja roja de lo confidencial; abajo, la casilla de destinatario con la
     * etiqueta mecanografiada pegada encima (a quién va y las fechas), y las anteriores tachadas. El cierre es el de siempre:
     * dos botones de cartón y el cordel rojo enrollado en ocho entre ellos (al atarse, el cordel se va enrollando; suelto,
     * cuelga del botón de la solapa). El sello es el precinto de papel con el ojo, pegado a caballo sobre el borde de la
     * solapa: cuando la carta ya se puede abrir brilla en rojo, y al abrirla se rasga por el borde. Dentro va el comunicado,
     * con su cabecera gris.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val altoSol = h * 0.3f
        val borde = bis + altoSol
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val tinta = NEGRO
        val etiqueta = papelTapa(oscuro, false)
        // Los dos botones del cierre: uno en la solapa y otro en el cuerpo, a la derecha.
        val botonA = Offset(s.left + w * 0.8f, bis + altoSol * 0.55f)
        val botonB = Offset(botonA.x, borde + h * 0.12f)
        val rb = 5.2f * u * k
        fun boton(o: Offset) {
            drawCircle(Color.Black.copy(alpha = 0.3f), rb, o + Offset(0.6f * u, 1f * u))
            drawCircle(c(Color(0xFF8C7650)), rb, o)
            drawCircle(c(Color(0xFF6E5B3C)), rb, o, style = Stroke(1f * u))
            drawCircle(c(Color(0xFFB39C70)), rb * 0.45f, o)
            drawCircle(c(Color(0xFF4A3D28)), rb * 0.16f, o)
        }
        // El precinto de papel, a caballo sobre el borde de la solapa.
        val pw = w * 0.2f; val ph = h * 0.25f
        val pc = Offset(s.left + w * 0.56f, borde)
        val pr = Rect(pc.x - pw / 2, pc.y - ph / 2, pc.x + pw / 2, pc.y + ph / 2)
        fun precinto(alfa: Float) {
            fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
            val roj = mezcla(c(ROJO), Color(0xFFFF5A44), e.brillo * 0.6f)
            drawRect(a(Color.Black.copy(alpha = 0.25f)), pr.topLeft + Offset(0.8f * u, 1.2f * u), pr.size)
            drawRect(a(etiqueta), pr.topLeft, pr.size)
            drawRect(a(roj), pr.topLeft, Size(pw, ph * 0.18f))
            drawRect(a(roj), Offset(pr.left, pr.bottom - ph * 0.18f), Size(pw, ph * 0.18f))
            ojoVigia(Offset(pc.x, pc.y), ph * 0.2f, a(tinta), a(roj), a(etiqueta), false)
            // El número de serie del precinto, en rayitas.
            for (i in 0 until 9) drawRect(a(tinta.copy(alpha = 0.55f)), Offset(pr.left + pw * (0.12f + i * 0.085f), pr.bottom - ph * 0.3f), Size(pw * (if (i % 3 == 0) 0.04f else 0.02f), ph * 0.07f))
        }

        sombraSuave(s, 2 * u, 6 * u, 0.3f)
        // ---- La espalda del sobre (lo que se ve por la boca abierta).
        drawRect(c(SOBRE_FORRO), s.topLeft, s.size)
        // ---- La solapa abierta, por detrás de la carta.
        fun DrawScope.solapa(adentro: Boolean) {
            val p = Path().apply {
                moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right - w * 0.03f, borde - 2 * u); lineTo(s.right - w * 0.05f, borde)
                lineTo(s.left + w * 0.05f, borde); lineTo(s.left + w * 0.03f, borde - 2 * u); close()
            }
            if (adentro) {
                drawPath(p, c(mezcla(SOBRE_FORRO, SOBRE_INT, 0.35f)))
                drawLine(c(Color(0xFF7A6440)), Offset(s.left + w * 0.06f, borde - h * 0.04f), Offset(s.right - w * 0.06f, borde - h * 0.04f), 0.8f * u)
                return
            }
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(s.left + w * 0.05f, borde + 1.2f * u), Offset(s.right - w * 0.05f, borde + 1.2f * u), 1.4f * u)
            drawPath(p, c(SOBRE_INT))
            for (i in 0 until 14) {
                val y = bis + altoSol * azar(e.semilla, i)
                drawLine(c(Color(0xFFB09564)).copy(alpha = 0.5f), Offset(s.left + w * azar(e.semilla, i + 20), y), Offset(s.left + w * azar(e.semilla, i + 20) + 12 * u, y), 0.5f * u)
            }
            // La franja roja de lo confidencial, en la esquina izquierda.
            clipPath(p) {
                drawPath(Path().apply { moveTo(s.left, bis + altoSol * 0.15f); lineTo(s.left + w * 0.12f, bis); lineTo(s.left + w * 0.2f, bis); lineTo(s.left, bis + altoSol * 0.75f); close() }, c(ROJO))
            }
            textoEn(medidor, "CORREO INTERNO", Rect(s.left + w * 0.18f, bis + altoSol * 0.12f, s.left + w * 0.72f, bis + altoSol * 0.5f), ESTARCIDO,
                c(Color(0xFF3A3026)), 8.5f * k, peso = FontWeight.Bold, espaciado = 0.6f, minimo = 5f)
            drawLine(c(Color(0xFF3A3026)), Offset(s.left + w * 0.2f, bis + altoSol * 0.62f), Offset(s.left + w * 0.7f, bis + altoSol * 0.62f), 0.8f * u)
            boton(botonA)
            // El cordel suelto, colgando del botón de la solapa.
            if (e.cordon <= 0.001f) {
                drawPath(Path().apply {
                    moveTo(botonA.x, botonA.y); cubicTo(botonA.x + rb * 2.2f, botonA.y + rb * 0.5f, botonA.x + rb * 1.6f, botonA.y + rb * 3.5f, botonA.x + rb * 2.6f, botonA.y + rb * 4.6f)
                }, c(CORDEL_ROJO), style = Stroke(1.3f * u, cap = StrokeCap.Round))
                drawCircle(c(CORDEL_ROJO), 1.2f * u, Offset(botonA.x + rb * 2.6f, botonA.y + rb * 4.6f))
            }
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2f * u * e.romper); rotate(-5f * e.romper, pc) }) {
                clipPath(mitadRota(pr, pc.y, true, semilla = e.semilla)) { precinto(1f) }
            }
        }
        if (!cerrada) solapaCarta(bis, ab) { solapa(it) }

        // ---- El comunicado que sale.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
            val hoja = Rect(s.left + w * 0.07f, arriba0 - alza, s.right - w * 0.07f, arriba0 - alza + h * 0.9f)
            drawRect(Color.Black.copy(alpha = 0.22f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
            val papel = if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja
            drawRect(papel, hoja.topLeft, hoja.size)
            drawRect(mezcla(papel, Color(0xFF6F7479), 0.5f), hoja.topLeft, Size(hoja.width, 4 * u * k))
            drawCircle(e.renglones.copy(alpha = 0.6f), 1.6f * u * k, Offset(hoja.right - 5 * u * k, hoja.top + 2 * u * k))
            var y = hoja.top + 4 * u * k + 11 * u
            while (y < hoja.bottom - 3 * u) { drawLine(e.renglones, Offset(hoja.left + 5 * u, y), Offset(hoja.right - 5 * u, y), 0.7f * u); y += 11 * u }
            drawLine(c(ROJO).copy(alpha = 0.4f), Offset(hoja.left + 3.5f * u, hoja.top + 4 * u * k), Offset(hoja.left + 3.5f * u, hoja.bottom), 0.7f * u)
        }

        // ---- El cuerpo: el manila, la columna de agujeros y la casilla del destinatario.
        drawRect(c(SOBRE_INT), s.topLeft, s.size)
        drawRect(c(Color(0xFF7A6440)), s.topLeft, s.size, style = Stroke(0.8f * u))
        if (!cerrada) drawRect(Color.Black.copy(alpha = 0.35f), s.topLeft, Size(w, 1.6f * u))
        for (i in 0 until 4) {
            val o = Offset(s.left + w * 0.065f, s.top + h * (0.42f + i * 0.16f))
            drawCircle(c(Color(0xFF2A2418)), 2.6f * u * k, o)
            drawCircle(c(Color(0xFF7A6440)), 2.6f * u * k, o, style = Stroke(0.8f * u))
        }
        val col = c(Color(0xFF5C4C34))
        // La rejilla impresa: el encabezado de las columnas y los renglones de los destinatarios anteriores, tachados.
        val x0 = s.left + w * 0.13f; val x1 = s.right - w * 0.03f
        drawLine(col, Offset(x0, borde + h * 0.03f), Offset(x0, s.bottom - h * 0.04f), 0.7f * u)
        drawLine(col, Offset(x1, borde + h * 0.03f), Offset(x1, s.bottom - h * 0.04f), 0.7f * u)
        listOf(0.36f, 0.53f, 0.75f, 0.94f).forEach { f -> drawLine(col, Offset(x0, s.top + h * f), Offset(x1, s.top + h * f), 0.7f * u) }
        for (i in 0 until 2) {
            val y = s.top + h * (0.405f + i * 0.07f)
            val l = w * (0.24f + 0.12f * azar(e.semilla, 40 + i))
            drawRect(col.copy(alpha = 0.45f), Offset(x0 + 4 * u, y - 1 * u), Size(l, 2 * u))
            drawLine(c(ROJO).copy(alpha = 0.7f), Offset(x0 + 2 * u, y + 0.4f * u), Offset(x0 + 6 * u + l, y - 0.6f * u), 1f * u)
        }
        // La etiqueta mecanografiada, pegada sobre las dos casillas de abajo.
        val et = Rect(x0 + 2 * u, s.top + h * 0.55f, x1 - 2 * u, s.top + h * 0.925f)
        rotate(-0.6f, et.center) {
            drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft + Offset(0.6f * u, 1f * u), et.size)
            drawRect(etiqueta, et.topLeft, et.size)
            drawLine(tinta.copy(alpha = 0.18f), Offset(et.left + 3 * u, et.top + et.height * 0.6f), Offset(et.right - 3 * u, et.top + et.height * 0.6f), 0.6f * u)
            textoEn(medidor, e.linea1, Rect(et.left + 3 * u, et.top, et.right - 3 * u, et.top + et.height * 0.58f), ESTARCIDO, tinta, 11.5f, peso = FontWeight.Bold, minimo = 7f)
            textoEn(medidor, e.linea2, Rect(et.left + 3 * u, et.top + et.height * 0.6f, et.right - 3 * u, et.bottom - 1 * u), letra, tinta.copy(alpha = 0.85f), 9f, minimo = 6f)
        }
        boton(botonB)

        // ---- La solapa cerrada encima.
        if (cerrada) solapaCarta(bis, ab) { solapa(it) }
        // ---- El cordel enrollado en ocho entre los botones; los botones encima, porque el cordel pasa por debajo.
        if (e.cordon > 0f && cerrada) {
            val m = Offset(botonA.x, (botonA.y + botonB.y) / 2); val d = (botonB.y - botonA.y) / 2 + rb * 0.7f
            val puntos = (0..96).map { i ->
                val a = i / 96f * 2.5f * 2 * PI.toFloat()
                Offset(m.x + rb * 1.5f * sin(2 * a) * (1f - 0.08f * (i / 96f)), m.y - d * cos(a))
            }
            drawPath(recorrido(puntos, e.cordon), c(Color(0xFF5E120E)), style = Stroke(1.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(recorrido(puntos, e.cordon), c(CORDEL_ROJO), style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            listOf(botonA, botonB).forEach { o -> drawCircle(c(Color(0xFF8C7650)), rb * 0.55f, o); drawCircle(c(Color(0xFF4A3D28)), rb * 0.16f, o) }
        }
        // ---- El precinto: entero (y, al sellar, cayendo desde grande), o rasgado por el borde de la solapa.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(Color(0xFFFF3A2A).copy(alpha = 0.6f * e.brillo), Color.Transparent), center = pc, radius = pw * 1.1f), pw * 1.1f, pc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, pc); rotate(-5f, pc) }) { precinto(e.alfaSello.coerceIn(0f, 1f)) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2.5f * u * e.romper); rotate(-5f + 6f * e.romper, pc) }) {
                clipPath(mitadRota(pr, pc.y, false, semilla = e.semilla)) { precinto(1f) }
            }
        }
    }

    // La hoja de la carta queda arriba, entre su cabecera gris y la boca del sobre.
    override val cajaPrevia = Rect(0.12f, 0.155f, 0.88f, 0.32f)

    override val altoBuzon: Dp = 206.dp

    /**
     * El correo interno de la sección, en el muro de bloques: arriba, la placa de acero con la ranura por donde caen los
     * sobres, la cámara que la vigila y la baliza roja, que gira encendida cuando hay cartas desclasificadas. Debajo, sobre su
     * ménsula, las tres bandejas de alambre del archivo: «DESCLASIFICADAS» (los sobres con el precinto brillando),
     * «CLASIFICADAS» (atados con su cordel) y «ARCHIVADAS» (abiertos, con el comunicado asomando). Cada bandeja lleva su tarjeta
     * en el portaetiquetas con el número sellado; los sobres se apilan (hasta nueve) y, si hay más, se dice cuántos más. En
     * oscuro, el muro se hunde y solo queda el cono de la lámpara sobre las bandejas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF060708), 0.5f) else c(x)
        // ---- El muro de bloques húmedos.
        drawRect(f(MURO))
        val fila = 16 * u; val bloque = 34 * u
        var y = 0f; var n = 0
        while (y < h) {
            drawLine(f(MURO_JUNTA), Offset(0f, y), Offset(w, y), 1f * u)
            var x = if (n % 2 == 0) 0f else bloque / 2
            while (x < w) { drawLine(f(MURO_JUNTA), Offset(x, y), Offset(x, y + fila), 1f * u); x += bloque }
            y += fila; n++
        }
        for (i in 0 until 3) drawRect(Color.Black.copy(alpha = 0.08f), Offset(w * (0.1f + 0.36f * i), 0f), Size(14 * u, h * (0.4f + 0.3f * azar(7, i))))
        // ---- La placa de la ranura, con la cámara y la baliza.
        val placa = Rect(w * 0.22f, 8 * u, w * 0.78f, 48 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), placa.topLeft + Offset(2 * u, 3 * u), placa.size)
        drawRect(c(ACERO), placa.topLeft, placa.size)
        drawRect(c(ACERO_LUZ), placa.topLeft, Size(placa.width, 1.5f * u))
        drawRect(c(ACERO_OSC), Offset(placa.left, placa.bottom - 2 * u), Size(placa.width, 2 * u))
        listOf(placa.left + 5 * u, placa.right - 5 * u).forEach { x -> listOf(placa.top + 5 * u, placa.bottom - 6 * u).forEach { yy -> drawCircle(c(ACERO_OSC), 1.8f * u, Offset(x, yy)) } }
        val ranura = Rect(placa.left + placa.width * 0.12f, placa.top + placa.height * 0.5f, placa.right - placa.width * 0.12f, placa.top + placa.height * 0.72f)
        drawRoundRect(c(NEGRO), ranura.topLeft, ranura.size, CornerRadius(2 * u))
        drawRect(c(ACERO_LUZ), Offset(ranura.left, ranura.top - 3 * u), Size(ranura.width, 2.5f * u))
        textoEn(medidor, "CORREO INTERNO · SECCIÓN 4", Rect(ranura.left - 10 * u, placa.top + 3 * u, ranura.right + 10 * u, ranura.top - 4 * u), ESTARCIDO, papelTapa(oscuro, false), 9.5f, peso = FontWeight.Bold, espaciado = 1f)
        // La cámara, a la izquierda, y la baliza, a la derecha.
        val cam = Offset(placa.left - 26 * u, 18 * u)
        drawRect(c(ACERO_OSC), Offset(cam.x + 14 * u, cam.y - 1.5f * u), Size(placa.left - cam.x - 14 * u, 3 * u))
        rotate(14f, cam) {
            drawRoundRect(c(Color(0xFFCFD2CC)), Offset(cam.x - 10 * u, cam.y - 5 * u), Size(24 * u, 11 * u), CornerRadius(2 * u))
            drawRect(c(NEGRO), Offset(cam.x - 14 * u, cam.y - 4 * u), Size(5 * u, 9 * u))
            drawCircle(c(Color(0xFF2E3A44)), 3.2f * u, Offset(cam.x - 14 * u, cam.y + 0.5f * u))
        }
        drawCircle(Color(0xFFFF4A3A), 1.4f * u, Offset(cam.x + 6 * u, cam.y + 3 * u))
        val baliza = Offset(placa.right + 22 * u, 30 * u)
        drawRect(c(ACERO_OSC), Offset(placa.right, baliza.y + 6 * u), Size(baliza.x - placa.right + 6 * u, 3 * u))
        drawRect(c(ACERO), Offset(baliza.x - 9 * u, baliza.y + 6 * u), Size(18 * u, 6 * u))
        val encendida = e.listas > 0
        if (encendida) drawCircle(Brush.radialGradient(listOf(Color(0xFFFF3A2A).copy(alpha = 0.55f), Color.Transparent), center = baliza, radius = 46 * u), 46 * u, baliza)
        drawPath(Path().apply { moveTo(baliza.x - 7 * u, baliza.y + 6 * u); lineTo(baliza.x - 6 * u, baliza.y - 6 * u); quadraticTo(baliza.x, baliza.y - 12 * u, baliza.x + 6 * u, baliza.y - 6 * u); lineTo(baliza.x + 7 * u, baliza.y + 6 * u); close() },
            if (encendida) Color(0xFFFF4A36) else c(Color(0xFF6A2622)))
        for (i in 0 until 3) drawLine(c(ACERO_OSC), Offset(baliza.x - 7 * u + i * 7 * u, baliza.y + 6 * u), Offset(baliza.x - 5 * u + i * 5 * u, baliza.y - 8 * u), 1 * u)
        if (encendida) drawPath(Path().apply { moveTo(baliza.x, baliza.y - 2 * u); lineTo(baliza.x - 40 * u, baliza.y - 14 * u); lineTo(baliza.x - 40 * u, baliza.y + 6 * u); close() }, Color(0xFFFF5A44).copy(alpha = 0.18f))

        // ---- La ménsula y las tres bandejas de alambre.
        val baseY = h - 22 * u
        val luz = Offset(w / 2, 50 * u)
        if (oscuro) drawPath(Path().apply { moveTo(luz.x - 20 * u, luz.y); lineTo(luz.x + 20 * u, luz.y); lineTo(w * 0.98f, baseY); lineTo(w * 0.02f, baseY); close() }, BOMBILLA.copy(alpha = 0.07f))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(6 * u, baseY + 4 * u), Size(w - 12 * u, 6 * u))
        drawRect(c(ACERO), Offset(4 * u, baseY), Size(w - 8 * u, 6 * u))
        drawRect(c(ACERO_LUZ), Offset(4 * u, baseY), Size(w - 8 * u, 1.4f * u))
        listOf(18 * u, w - 26 * u).forEach { x ->
            drawPath(Path().apply { moveTo(x, baseY + 6 * u); lineTo(x + 8 * u, baseY + 6 * u); lineTo(x + 8 * u, h); lineTo(x, h); close() }, c(ACERO_OSC))
        }
        val grupos = listOf(Triple("DESCLASIFICADAS", e.listas, 0), Triple("CLASIFICADAS", e.selladas, 1), Triple("ARCHIVADAS", e.abiertas, 2))
        val anchoB = (w - 32 * u) / 3f
        grupos.forEachIndexed { i, (nombre, cuantas, tipo) ->
            val b = Rect(12 * u + i * (anchoB + 4 * u), baseY - 92 * u, 12 * u + i * (anchoB + 4 * u) + anchoB, baseY)
            // El fondo de la bandeja, en sombra, y los sobres apilados (de canto, en perspectiva: se ve su cara de arriba).
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(b.left + 3 * u, b.top + 30 * u), Size(b.width - 6 * u, b.height - 30 * u))
            val vis = cuantas.coerceAtMost(9)
            for (j in 0 until vis) {
                val gy = b.bottom - 16 * u - j * 6.5f * u
                val gx = b.left + 6 * u + (azar(i * 13 + 5, j) - 0.5f) * 5 * u
                val gw = b.width - 12 * u
                val manila = c(mezcla(SOBRE_INT, CARTONES[j % 3], 0.12f * (j % 3)))
                drawRect(Color.Black.copy(alpha = 0.28f), Offset(gx + 1 * u, gy + 1.5f * u), Size(gw, 6 * u))
                drawRect(manila, Offset(gx, gy), Size(gw, 6 * u))
                drawLine(c(Color(0xFF7A6440)), Offset(gx, gy + 6 * u), Offset(gx + gw, gy + 6 * u), 0.8f * u)
                when (tipo) {
                    0 -> {
                        drawRect(Color(0xFFFF4A36).copy(alpha = 0.35f), Offset(gx + gw * 0.42f - 3 * u, gy - 2 * u), Size(gw * 0.16f + 6 * u, 10 * u))
                        drawRect(Color(0xFFFF5A44), Offset(gx + gw * 0.42f, gy), Size(gw * 0.16f, 6 * u))
                        drawRect(papelTapa(oscuro, false), Offset(gx + gw * 0.45f, gy + 1.8f * u), Size(gw * 0.1f, 2.4f * u))
                    }
                    1 -> {
                        drawCircle(c(Color(0xFF8C7650)), 2.4f * u, Offset(gx + gw * 0.8f, gy + 3 * u))
                        drawLine(c(CORDEL_ROJO), Offset(gx + gw * 0.72f, gy + 0.5f * u), Offset(gx + gw * 0.88f, gy + 5.5f * u), 1.1f * u)
                        drawLine(c(CORDEL_ROJO), Offset(gx + gw * 0.72f, gy + 5.5f * u), Offset(gx + gw * 0.88f, gy + 0.5f * u), 1.1f * u)
                        drawRect(c(ROJO), Offset(gx + gw * 0.5f, gy + 1 * u), Size(gw * 0.1f, 4 * u))
                    }
                    else -> {
                        drawRect(papelTapa(oscuro, false), Offset(gx + gw * (0.1f + 0.1f * azar(31, j)), gy - 3.5f * u), Size(gw * 0.55f, 4 * u))
                        drawLine(NEGRO.copy(alpha = 0.3f), Offset(gx + gw * 0.16f, gy - 2 * u), Offset(gx + gw * 0.5f, gy - 2 * u), 0.6f * u)
                    }
                }
            }
            if (cuantas > 9) textoEn(medidor, "+${cuantas - 9}", Rect(b.left, b.top + 30 * u, b.right, b.top + 44 * u), ESTARCIDO, papelTapa(oscuro, false), 11f, peso = FontWeight.Bold)
            if (cuantas == 0) {
                // La bandeja vacía: solo la ficha de control olvidada en el fondo.
                drawRect(c(PAPEL).copy(alpha = 0.6f), Offset(b.left + b.width * 0.3f, b.bottom - 13 * u), Size(b.width * 0.4f, 5 * u))
                drawLine(NEGRO.copy(alpha = 0.3f), Offset(b.left + b.width * 0.34f, b.bottom - 10.5f * u), Offset(b.left + b.width * 0.6f, b.bottom - 10.5f * u), 0.6f * u)
            }
            // La canasta de alambre: el aro de arriba, los alambres verticales y el labio de abajo con el portaetiquetas.
            val alambre = c(ACERO_LUZ)
            drawRect(alambre, Offset(b.left, b.top + 28 * u), Size(b.width, 1.6f * u))
            var x = b.left
            while (x <= b.right + 0.5f * u) { drawLine(alambre.copy(alpha = 0.85f), Offset(x, b.top + 28 * u), Offset(x, b.bottom), 1f * u); x += maxOf(b.width / 12f, 1f) }
            drawLine(alambre, Offset(b.left, b.top + 60 * u), Offset(b.right, b.top + 60 * u), 1f * u)
            val labio = Rect(b.left - 1 * u, b.bottom - 9 * u, b.right + 1 * u, b.bottom)
            drawRect(c(ACERO), labio.topLeft, labio.size)
            drawRect(c(ACERO_LUZ), labio.topLeft, Size(labio.width, 1 * u))
            // La tarjeta: el nombre de la bandeja y el número sellado.
            val tj = Rect(b.left + b.width * 0.12f, b.top, b.right - b.width * 0.12f, b.top + 26 * u)
            drawRect(Color.Black.copy(alpha = 0.3f), tj.topLeft + Offset(1 * u, 1.5f * u), tj.size)
            drawRect(c(ACERO_OSC), tj.inflate(1.5f * u).topLeft, tj.inflate(1.5f * u).size)
            drawRect(papelTapa(oscuro, false), tj.topLeft, tj.size)
            drawRect(if (tipo == 0 && cuantas > 0) Color(0xFFD8281E) else c(ROJO), tj.topLeft, Size(tj.width, 2.5f * u))
            textoEn(medidor, nombre, Rect(tj.left + 2 * u, tj.top + 3 * u, tj.right - 2 * u, tj.top + 11 * u), letra, NEGRO, 7.5f, peso = FontWeight.Bold, espaciado = 0.5f)
            textoEn(medidor, cuantas.toString(), Rect(tj.left, tj.top + 10 * u, tj.right, tj.bottom), ESTARCIDO,
                if (tipo == 0 && cuantas > 0) Color(0xFFB0140E) else NEGRO, 15f, peso = FontWeight.Bold)
        }
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 116.dp

    /**
     * El letrerito del puesto, de tubo de neón en su caja oscura: el tazón de fideos humeante con sus palillos; de noche, la
     * luna creciente y dos estrellas en el verde de sodio.
     */
    private fun DrawScope.letreroPuesto(r: Rect, p: Pagina, noche: Boolean) {
        val u = density
        drawRoundRect(t(CARCASA_OSC, true, noche), r.topLeft, r.size, CornerRadius(5 * u))
        drawRoundRect(t(CARCASA_LUZ, true, noche), r.topLeft, r.size, CornerRadius(5 * u), style = Stroke(1 * u))
        val c = r.center; val s = minOf(r.width, r.height) * 0.5f
        // Los tornillos del letrero.
        listOf(Offset(r.left + 4 * u, r.top + 4 * u), Offset(r.right - 4 * u, r.top + 4 * u), Offset(r.left + 4 * u, r.bottom - 4 * u), Offset(r.right - 4 * u, r.bottom - 4 * u))
            .forEach { drawCircle(t(ALAMBRE, true, noche).copy(alpha = 0.5f), 1.1f * u, it) }
        if (!noche) {
            val tazon = Path().apply {
                moveTo(c.x - s * 0.62f, c.y + s * 0.05f); lineTo(c.x + s * 0.62f, c.y + s * 0.05f)
                quadraticTo(c.x + s * 0.58f, c.y + s * 0.6f, c.x, c.y + s * 0.62f)
                quadraticTo(c.x - s * 0.58f, c.y + s * 0.6f, c.x - s * 0.62f, c.y + s * 0.05f)
            }
            tubo(tazon, AMBAR, 1.2f * u)
            tubo(Path().apply { moveTo(c.x - s * 0.15f, c.y + s * 0.05f); lineTo(c.x + s * 0.55f, c.y - s * 0.62f) }, MAGENTA, 1.1f * u)
            tubo(Path().apply { moveTo(c.x + s * 0.02f, c.y + s * 0.05f); lineTo(c.x + s * 0.68f, c.y - s * 0.5f) }, MAGENTA, 1.1f * u)
            listOf(-0.32f, -0.08f).forEach { dx ->
                tubo(Path().apply {
                    moveTo(c.x + s * dx, c.y - s * 0.12f)
                    quadraticTo(c.x + s * (dx - 0.14f), c.y - s * 0.32f, c.x + s * dx, c.y - s * 0.48f)
                    quadraticTo(c.x + s * (dx + 0.14f), c.y - s * 0.62f, c.x + s * dx, c.y - s * 0.78f)
                }, CIAN, 0.9f * u)
            }
        } else {
            val luna = Path().apply {
                addOval(Rect(c.x - s * 0.55f, c.y - s * 0.55f, c.x + s * 0.55f, c.y + s * 0.55f))
            }
            val hueco = Path().apply { addOval(Rect(c.x - s * 0.3f, c.y - s * 0.72f, c.x + s * 0.72f, c.y + s * 0.3f)) }
            tubo(Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, luna, hueco), p.detalle, 1.1f * u)
            drawPath(estrella(Offset(c.x + s * 0.5f, c.y + s * 0.45f), s * 0.18f, 4, 0.35f), mezcla(p.detalle, Color.White, 0.4f))
            drawPath(estrella(Offset(c.x + s * 0.68f, c.y - s * 0.05f), s * 0.11f, 4, 0.35f), mezcla(p.detalle, Color.White, 0.4f))
        }
    }

    /**
     * La cabecera del ticket del puesto: el talón de papel térmico (oscuro, bajo la luz del letrero) con sus bordes dentados,
     * el letrerito de neón a la izquierda tras la línea de corte, el rótulo del puesto en ámbar sobre su tubo magenta, la fecha
     * grande en la tinta clara, el detalle y el código de barras de la caja. De noche el letrero es la luna en verde de sodio y
     * caen gotas sobre el talón.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val talon = mezcla(p.color, ETIQUETA, 0.06f)
        // El talón, con los dientes del corte arriba y abajo.
        val d = 6 * u; val arriba = 5 * u; val abajo = h - 5 * u
        val n = (w / d).toInt().coerceAtLeast(1); val paso = w / n
        drawPath(Path().apply {
            moveTo(0f, arriba)
            for (k in 0 until n) { lineTo(paso * (k + 0.5f), arriba - 3.5f * u); lineTo(paso * (k + 1), arriba) }
            lineTo(w, abajo)
            for (k in n - 1 downTo 0) { lineTo(paso * (k + 0.5f), abajo + 3.5f * u); lineTo(paso * k, abajo) }
            close()
        }, talon)
        // El brillo magenta del letrero sobre el papel (capas planas).
        drawCircle(MAGENTA.copy(alpha = 0.05f), 60 * u, Offset(34 * u, h / 2))
        drawCircle(MAGENTA.copy(alpha = 0.05f), 40 * u, Offset(34 * u, h / 2))
        // El letrero y la línea de corte punteada.
        letreroPuesto(Rect(6 * u, h / 2 - 28 * u, 62 * u, h / 2 + 28 * u), p, e.noche)
        var y = arriba + 4 * u
        while (y < abajo - 4 * u) { drawCircle(p.tenue.copy(alpha = 0.6f), 1 * u, Offset(70 * u, y)); y += 5 * u }
        // El código de barras de la caja, de pie a la derecha.
        val cb = Rect(w - 20 * u, arriba + 8 * u, w - 8 * u, abajo - 8 * u)
        var by = cb.top; var k = 0
        while (by < cb.bottom) {
            val g = (if (azar(e.numero, k) > 0.5f) 1.6f else 0.8f) * u
            drawRect(p.tinta.copy(alpha = 0.55f), Offset(cb.left, by), Size(cb.width, g)); by += g + (0.9f + azar(e.numero, 40 + k)) * u; k++
        }
        // El rótulo en ámbar, su tubo, la fecha y el detalle.
        val col = Rect(76 * u, arriba + 6 * u, w - 26 * u, abajo - 4 * u)
        textoEn(medidor, e.rotulo, Rect(col.left, col.top, col.right, col.top + 18 * u), letra, p.detalle, 11f, peso = FontWeight.SemiBold, espaciado = 0.4f, minimo = 6f)
        tubo(Path().apply { moveTo(col.left + 6 * u, col.top + 21 * u); lineTo(col.right - 6 * u, col.top + 21 * u) }, if (e.noche) p.acento else MAGENTA, 0.9f * u)
        fechaFuturo(medidor, e.fecha, Rect(col.left, col.top + 25 * u, col.right, col.top + 70 * u), letra, p.tinta, 21f, peso = FontWeight.SemiBold)
        fechaFuturo(medidor, e.detalle, Rect(col.left, col.top + 71 * u, col.right, col.bottom), letra, p.tenue, 10.5f, k = 0.86f)
        // De noche, gotas sobre el talón.
        if (e.noche) for (j in 0 until 18) {
            val o = Offset(w * azar(e.numero, 300 + j), arriba + (abajo - arriba) * azar(e.numero, 400 + j))
            if (o.x > col.left && o.x < col.right && o.y > col.top + 22 * u) continue
            val rg = (0.8f + 1.4f * azar(e.numero, 500 + j)) * u
            drawCircle(LLUVIA.copy(alpha = 0.25f), rg, o)
            drawCircle(Color.White.copy(alpha = 0.5f), rg * 0.35f, Offset(o.x - rg * 0.3f, o.y - rg * 0.3f))
        }
    }

    /**
     * El reverso del ticket bajo el letrero: el reflejo magenta arriba a la derecha y el verde de la calle abajo a la izquierda;
     * el margen punteado en ámbar con sus rombos, la fila de perforaciones del rollo a la derecha, la lluvia que corre en
     * diagonal (más de noche), los reflejos del charco al pie y el borde dentado del corte. El tema solo existe en oscuro:
     * el papel ya es oscuro y solo lleva los adornos.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.radialGradient(listOf(p.acento.copy(alpha = 0.13f), Color.Transparent), center = Offset(w * 0.85f, 0f), radius = w * 0.8f))
        drawRect(Brush.radialGradient(listOf((if (noche) p.detalle else SODIO).copy(alpha = 0.07f), Color.Transparent), center = Offset(0f, h), radius = w * 0.75f))
        // El margen punteado en ámbar con un rombo cada tanto.
        var y = 64 * u; var k = 0
        while (y < h - 20 * u) {
            if (k % 8 == 0) drawPath(Path().apply { moveTo(18 * u, y - 3 * u); lineTo(21 * u, y); lineTo(18 * u, y + 3 * u); lineTo(15 * u, y); close() }, AMBAR.copy(alpha = 0.35f))
            else drawLine(AMBAR.copy(alpha = 0.22f), Offset(18 * u, y - 1.5f * u), Offset(18 * u, y + 1.5f * u), 1 * u)
            y += 7 * u; k++
        }
        // La fila de perforaciones del rollo, a la derecha.
        y = 66 * u
        while (y < h - 20 * u) { drawCircle(Color.Black.copy(alpha = 0.35f), 1.8f * u, Offset(w - 10 * u, y)); drawCircle(p.tenue.copy(alpha = 0.18f), 1.8f * u, Offset(w - 10 * u, y), style = Stroke(0.6f * u)); y += 14 * u }
        // La lluvia en diagonal, por los bordes (más de noche).
        val gotas = if (noche) 70 else 34
        for (j in 0 until gotas) {
            val x0 = w * azar(91, j); val y0 = h * azar(93, j)
            val centro = x0 > w * 0.14f && x0 < w * 0.86f
            val l = (14 + 26 * azar(95, j)) * u
            drawLine(LLUVIA.copy(alpha = if (centro) 0.035f else 0.09f), Offset(x0, y0), Offset(x0 - l * 0.18f, y0 + l), 0.8f * u)
        }
        // Los reflejos del charco al pie: rayas cortas de magenta, cian y ámbar.
        for (j in 0 until 22) {
            val yy = h - (14 + 70 * azar(97, j)) * u
            val xx = w * azar(99, j); val l = (10 + 40 * azar(101, j)) * u
            val col = listOf(MAGENTA, CIAN, AMBAR)[j % 3]
            drawLine(col.copy(alpha = 0.07f + 0.06f * azar(103, j)), Offset(xx, yy), Offset(minOf(xx + l, w), yy), (1 + 1.5f * azar(105, j)) * u)
        }
        // El borde dentado del corte, abajo.
        val d = 7 * u; var x = 0f
        while (x < w) { drawPath(Path().apply { moveTo(x, h); lineTo(x + d / 2, h - d * 0.6f); lineTo(minOf(x + d, w), h); close() }, p.detalle.copy(alpha = 0.45f)); x += d }
    }

    /** La chapa de la máquina por arriba: violeta casi negra, sus tornillos y el tubo de neón que corre por el borde de abajo. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(t(CARCASA, true, noche), t(CARCASA_OSC, true, noche)), 0f, h))
        drawRect(t(CARCASA_LUZ, true, noche), Offset.Zero, Size(w, 1.2f * u))
        listOf(6 * u, w - 6 * u).forEach { x -> drawCircle(t(ALAMBRE, true, noche).copy(alpha = 0.45f), 1.4f * u, Offset(x, 7 * u)) }
        tubo(Path().apply { moveTo(4 * u, h - 3.5f * u); lineTo(w - 4 * u, h - 3.5f * u) }, if (noche) p.detalle else MAGENTA, 1 * u)
    }

    /** El botón de la máquina: una tecla oscura, retroiluminada, con su tubo magenta alrededor (de noche, verde de sodio). */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = if (noche) p.detalle else MAGENTA
        val r = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(t(CARCASA_LUZ, true, noche), luz, 0.22f), mezcla(t(CARCASA_OSC, true, noche), luz, 0.12f)), r.top, r.bottom),
            r.topLeft, r.size, CornerRadius(7 * u))
        tubo(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(7 * u))) }, luz, 1.1f * u)
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFFFF2F8)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 266.dp

    /** La madera lacada del mostrador del puesto y el papel de los tickets, que se hunde poco para que se lea. */
    private val LACA = Color(0xFF3A2420)
    private val FAROLILLO = Color(0xFFE0402A)

    /**
     * El mostrador del puesto callejero, de noche: arriba, la cortinilla de tela partida con sus emblemas; detrás, el muro del
     * callejón con el letrero de neón (un cuenco humeante y unos palillos: formas, no letras), las botellas y la lluvia; a la
     * izquierda cuelga el farolillo de papel rojo. Sobre la madera lacada y mojada, que refleja los neones, el pincho de tickets
     * en su base de metal: los tickets ensartados, cada uno un poco girado, en una pila que crece con las hojas; arriba, el
     * último ticket, ensartado por su agujero y caído hacia delante, con el nombre, la cuenta, los renglones y el código de
     * barras. Al lado, el cuenco de fideos con los palillos y su vapor, la impresora de tickets con el papel que sale y unas
     * monedas. Al abrirse, el ticket de arriba sube por la púa, se suelta y se desenrolla, y los de abajo se abren en abanico.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val a = e.abierto.coerceIn(0f, 1f)
        val papel = mezcla(ETIQUETA, Color(0xFF06050B), if (oscuro) 0.1f else 0f)
        val fondoY = h * 0.56f
        val frenteY = h * 0.86f
        // ---- El muro del callejón: bloques oscuros con sus juntas y la lluvia.
        drawRect(c(CARCASA_OSC), Offset.Zero, Size(w, fondoY))
        var fy = h * 0.2f; var fila = 0
        while (fy < fondoY) {
            drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, fy), Offset(w, fy), 1 * u)
            var fx = if (fila % 2 == 0) 0f else 22 * u
            while (fx < w) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(fx, fy), Offset(fx, fy + 16 * u), 1 * u); fx += 44 * u }
            fy += 16 * u; fila++
        }
        // El letrero de neón: el cuenco con su vapor y los palillos, en su placa.
        val ln = Rect(w * 0.6f, h * 0.2f, w * 0.95f, h * 0.44f)
        drawRoundRect(c(Color(0xFF0C0A12)), ln.topLeft, ln.size, CornerRadius(4 * u))
        listOf(ln.left + 6 * u, ln.right - 6 * u).forEach { x -> listOf(ln.top + 6 * u, ln.bottom - 6 * u).forEach { y -> drawCircle(c(ALAMBRE), 1.4f * u, Offset(x, y)) } }
        val cc = Offset(ln.center.x - ln.width * 0.08f, ln.top + ln.height * 0.62f); val rc = ln.height * 0.3f
        tubo(Path().apply { moveTo(cc.x - rc * 1.3f, cc.y); quadraticTo(cc.x - rc * 1.2f, cc.y + rc * 1.1f, cc.x, cc.y + rc * 1.05f); quadraticTo(cc.x + rc * 1.2f, cc.y + rc * 1.1f, cc.x + rc * 1.3f, cc.y); close() }, MAGENTA, 1.6f * u)
        for (k in 0 until 3) {
            val x = cc.x - rc * 0.6f + k * rc * 0.6f
            tubo(Path().apply { moveTo(x, cc.y - rc * 0.25f); cubicTo(x - rc * 0.3f, cc.y - rc * 0.6f, x + rc * 0.3f, cc.y - rc * 0.8f, x, cc.y - rc * 1.15f) }, CIAN, 1.3f * u)
        }
        tubo(Path().apply { moveTo(cc.x + rc * 0.6f, cc.y + rc * 0.2f); lineTo(cc.x + rc * 2.4f, cc.y - rc * 1.2f); moveTo(cc.x + rc * 0.95f, cc.y + rc * 0.3f); lineTo(cc.x + rc * 2.6f, cc.y - rc * 0.95f) }, AMBAR, 1.3f * u)
        // Las botellas en la repisa de atrás.
        val repisa = fondoY - 8 * u
        drawRect(c(Color(0xFF2A1A16)), Offset(w * 0.18f, repisa), Size(w * 0.36f, 3 * u))
        listOf(Triple(0.22f, Color(0xFF3A1E14), 34f), Triple(0.28f, Color(0xFF1E3A2A), 42f), Triple(0.34f, Color(0xFF2A2A3A), 30f), Triple(0.44f, Color(0xFF4A3018), 38f), Triple(0.5f, Color(0xFF203040), 28f)).forEach { (f, col, alto) ->
            val x = w * f; val bw = 9 * u
            drawRoundRect(c(col), Offset(x - bw / 2, repisa - alto * u), Size(bw, alto * u), CornerRadius(2.5f * u))
            drawRect(c(col), Offset(x - 1.8f * u, repisa - (alto + 9) * u), Size(3.6f * u, 10 * u))
            drawRect(c(ETIQUETA).copy(alpha = 0.6f), Offset(x - bw / 2 + 1 * u, repisa - alto * u * 0.6f), Size(bw - 2 * u, alto * u * 0.28f))
            drawLine(MAGENTA.copy(alpha = 0.35f), Offset(x + bw * 0.25f, repisa - alto * u + 3 * u), Offset(x + bw * 0.25f, repisa - 3 * u), 1 * u)
        }
        // La lluvia que cae por delante del muro.
        for (k in 0 until 40) {
            val x = w * azar(13, k); val y = fondoY * azar(17, k)
            drawLine(LLUVIA.copy(alpha = 0.12f + 0.12f * azar(19, k)), Offset(x, y), Offset(x - 2 * u, y + (8 + 8 * azar(23, k)) * u), 0.7f * u)
        }
        // ---- La cortinilla de tela, con sus cortes y un emblema de olas en cada paño.
        val tela = c(Color(0xFF1E2448))
        drawRect(c(Color(0xFF5A3A24)), Offset(0f, 0f), Size(w, 4 * u))
        val panos = 5
        for (k in 0 until panos) {
            val x0 = w * k / panos + 1.5f * u; val x1 = w * (k + 1) / panos - 1.5f * u
            val largo = h * 0.17f + 3 * u * azar(29, k)
            drawPath(Path().apply { moveTo(x0, 4 * u); lineTo(x1, 4 * u); lineTo(x1 + 1 * u, largo); quadraticTo((x0 + x1) / 2, largo + 3 * u, x0 - 1 * u, largo); close() }, tela)
            drawLine(Color.White.copy(alpha = 0.06f), Offset(x0 + 3 * u, 6 * u), Offset(x0 + 4 * u, largo - 2 * u), 1 * u)
            val m = Offset((x0 + x1) / 2, h * 0.1f)
            drawCircle(c(ETIQUETA).copy(alpha = 0.85f), 8 * u, m, style = Stroke(1.6f * u))
            for (j in 0 until 2) drawArc(c(ETIQUETA).copy(alpha = 0.85f), 200f, 140f, false, Offset(m.x - (5 - j * 2) * u, m.y - (2 - j * 2) * u), Size((10 - j * 4) * u, (8 - j * 2) * u), style = Stroke(1.2f * u))
        }
        // ---- El farolillo de papel rojo, que cuelga a la izquierda, con su luz (el primer degradado).
        val fa = Offset(w * 0.09f, h * 0.36f)
        drawCircle(Brush.radialGradient(listOf(FAROLILLO.copy(alpha = 0.42f), FAROLILLO.copy(alpha = 0f)), fa, w * 0.3f), w * 0.3f, fa)
        drawLine(c(Color(0xFF1A1414)), Offset(fa.x, 4 * u), Offset(fa.x, fa.y - 26 * u), 1 * u)
        drawRect(c(Color(0xFF1A1414)), Offset(fa.x - 9 * u, fa.y - 27 * u), Size(18 * u, 4 * u))
        drawRect(c(Color(0xFF1A1414)), Offset(fa.x - 9 * u, fa.y + 23 * u), Size(18 * u, 4 * u))
        drawOval(mezcla(FAROLILLO, Color(0xFFFFB070), 0.25f), Offset(fa.x - 17 * u, fa.y - 24 * u), Size(34 * u, 48 * u))
        for (k in 1..5) drawLine(Color(0xFF7A1A10).copy(alpha = 0.5f), Offset(fa.x - 17 * u * sin(PI.toFloat() * k / 6), fa.y - 24 * u + 48 * u * k / 6), Offset(fa.x + 17 * u * sin(PI.toFloat() * k / 6), fa.y - 24 * u + 48 * u * k / 6), 0.8f * u)
        drawOval(Color(0xFFFFE0A0).copy(alpha = 0.45f), Offset(fa.x - 7 * u, fa.y - 14 * u), Size(14 * u, 26 * u))
        drawLine(c(Color(0xFF1A1414)), Offset(fa.x, fa.y + 27 * u), Offset(fa.x, fa.y + 36 * u), 1.4f * u)

        // ---- El mostrador: la madera lacada y mojada, el canto y el frente de tablas.
        drawRect(c(LACA), Offset(0f, fondoY), Size(w, frenteY - fondoY))
        for (k in 0 until 26) {
            val y = fondoY + (frenteY - fondoY) * azar(31, k)
            drawLine(Color.Black.copy(alpha = 0.18f), Offset(0f, y), Offset(w, y + 2 * u * (azar(37, k) - 0.5f)), (0.5f + azar(41, k)) * u)
        }
        // Los reflejos de los neones y del farolillo en la laca mojada.
        drawRect(MAGENTA.copy(alpha = 0.12f), Offset(cc.x - rc * 1.4f, fondoY), Size(rc * 2.8f, frenteY - fondoY))
        drawRect(CIAN.copy(alpha = 0.08f), Offset(cc.x - rc * 0.7f, fondoY), Size(rc * 1.4f, frenteY - fondoY))
        drawRect(FAROLILLO.copy(alpha = 0.14f), Offset(fa.x - 12 * u, fondoY), Size(24 * u, frenteY - fondoY))
        drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, fondoY + 1 * u), Offset(w, fondoY + 1 * u), 1 * u)
        drawRect(c(mezcla(LACA, Color.Black, 0.35f)), Offset(0f, frenteY), Size(w, h - frenteY))
        drawRect(c(mezcla(LACA, Color.White, 0.15f)), Offset(0f, frenteY), Size(w, 2 * u))
        var tx = 0f
        while (tx < w) { drawLine(Color.Black.copy(alpha = 0.45f), Offset(tx, frenteY + 2 * u), Offset(tx, h), 1.2f * u); tx += 30 * u }
        // La luz de los neones sobre la pila (el segundo degradado).
        val cx = w * 0.5f
        val by = frenteY - 20 * u
        drawOval(Brush.radialGradient(listOf(MAGENTA.copy(alpha = 0.2f), CIAN.copy(alpha = 0.06f), Color.Transparent), Offset(cx, by - 30 * u), w * 0.36f),
            Offset(cx - w * 0.36f, by - 30 * u - w * 0.36f), Size(w * 0.72f, w * 0.72f))

        // ---- El cuenco de fideos con los palillos y su vapor, a la izquierda.
        val cu = Offset(w * 0.17f, frenteY - 26 * u)
        drawOval(Color.Black.copy(alpha = 0.4f), Offset(cu.x - 30 * u, cu.y + 10 * u), Size(60 * u, 10 * u))
        drawPath(Path().apply { moveTo(cu.x - 30 * u, cu.y - 6 * u); cubicTo(cu.x - 28 * u, cu.y + 16 * u, cu.x + 28 * u, cu.y + 16 * u, cu.x + 30 * u, cu.y - 6 * u); close() }, c(Color(0xFF1B1B22)))
        for (k in 0 until 6) drawCircle(c(FAROLILLO), 1.6f * u, Offset(cu.x - 20 * u + k * 8 * u, cu.y + 4 * u + (if (k in 1..4) 3 else 0) * u))
        drawOval(c(Color(0xFFE8E2D6)), Offset(cu.x - 30 * u, cu.y - 11 * u), Size(60 * u, 10 * u))
        drawOval(c(Color(0xFFB8762E)), Offset(cu.x - 27 * u, cu.y - 10 * u), Size(54 * u, 8 * u))
        for (k in 0 until 5) drawArc(c(Color(0xFFF0D890)), 0f, 180f, false, Offset(cu.x - 20 * u + k * 6 * u, cu.y - 9 * u), Size(10 * u, 4 * u), style = Stroke(1 * u))
        drawCircle(c(Color(0xFFF2EDE0)), 4 * u, Offset(cu.x + 10 * u, cu.y - 6.5f * u)); drawCircle(c(Color(0xFFE8A030)), 2 * u, Offset(cu.x + 10 * u, cu.y - 6.5f * u))
        drawLine(c(Color(0xFFC8A070)), Offset(cu.x - 34 * u, cu.y - 16 * u), Offset(cu.x + 30 * u, cu.y - 7 * u), 2 * u, cap = StrokeCap.Round)
        drawLine(c(Color(0xFFC8A070)), Offset(cu.x - 32 * u, cu.y - 20 * u), Offset(cu.x + 31 * u, cu.y - 10 * u), 2 * u, cap = StrokeCap.Round)
        for (k in 0 until 3) {
            val x = cu.x - 12 * u + k * 12 * u
            drawPath(Path().apply { moveTo(x, cu.y - 14 * u); cubicTo(x - 8 * u, cu.y - 26 * u, x + 8 * u, cu.y - 36 * u, x - 2 * u, cu.y - 50 * u) }, Color.White.copy(alpha = 0.16f), style = Stroke(3 * u, cap = StrokeCap.Round))
        }
        // ---- La impresora de tickets, a la derecha, con el papel que sale, y las monedas.
        val im = Rect(w * 0.76f, frenteY - 52 * u, w * 0.76f + 50 * u, frenteY - 14 * u)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(im.left + 2 * u, im.bottom - 2 * u), Size(im.width, 6 * u))
        drawRoundRect(c(Color(0xFF2E2A36)), im.topLeft, im.size, CornerRadius(5 * u))
        drawRoundRect(c(Color(0xFF45404F)), im.topLeft, Size(im.width, im.height * 0.42f), CornerRadius(5 * u))
        drawRect(c(Color(0xFF0E0C12)), Offset(im.left + 8 * u, im.top + im.height * 0.38f), Size(im.width - 16 * u, 2.4f * u))
        drawCircle(SODIO, 1.6f * u, Offset(im.right - 8 * u, im.bottom - 8 * u))
        drawCircle(SODIO.copy(alpha = 0.25f), 4 * u, Offset(im.right - 8 * u, im.bottom - 8 * u))
        drawPath(Path().apply {
            moveTo(im.left + 10 * u, im.top + im.height * 0.39f); lineTo(im.right - 10 * u, im.top + im.height * 0.39f)
            cubicTo(im.right - 6 * u, im.top - 18 * u, im.right + 2 * u, im.top - 26 * u, im.right + 10 * u, im.top - 20 * u)
            lineTo(im.left + 22 * u, im.top - 26 * u); cubicTo(im.left + 14 * u, im.top - 22 * u, im.left + 8 * u, im.top - 10 * u, im.left + 10 * u, im.top + im.height * 0.39f); close()
        }, papel)
        for (k in 0 until 3) drawLine(TINTA.copy(alpha = 0.35f), Offset(im.left + 16 * u + k * 2 * u, im.top - (4 + 6 * k) * u), Offset(im.right - 6 * u + k * 2 * u, im.top - (6 + 6 * k) * u), 0.8f * u)
        listOf(Offset(w * 0.7f, frenteY - 9 * u), Offset(w * 0.73f, frenteY - 7 * u), Offset(w * 0.715f, frenteY - 11 * u)).forEach { m ->
            drawOval(c(Color(0xFF8A6A2A)), Offset(m.x - 6 * u, m.y - 2 * u), Size(12 * u, 4.6f * u))
            drawOval(c(Color(0xFFC8A04A)), Offset(m.x - 6 * u, m.y - 2.8f * u), Size(12 * u, 4.6f * u))
            drawCircle(c(Color(0xFF6A501A)), 1 * u, Offset(m.x, m.y - 0.5f * u))
        }

        // ---- El pincho: la base, la pila de tickets ensartados y la púa.
        val metal = c(ALAMBRE)
        drawOval(Color.Black.copy(alpha = 0.45f), Offset(cx - 34 * u, by - 2 * u), Size(70 * u, 14 * u))
        drawOval(mezcla(metal, Color.Black, 0.45f), Offset(cx - 30 * u, by - 7 * u), Size(60 * u, 14 * u))
        drawRect(mezcla(metal, Color.Black, 0.45f), Offset(cx - 30 * u, by - 9 * u), Size(60 * u, 9 * u))
        drawOval(metal, Offset(cx - 30 * u, by - 16 * u), Size(60 * u, 14 * u))
        drawOval(Color.White.copy(alpha = 0.3f), Offset(cx - 24 * u, by - 14.5f * u), Size(30 * u, 6 * u), style = Stroke(1 * u))
        drawOval(MAGENTA.copy(alpha = 0.3f), Offset(cx + 4 * u, by - 13 * u), Size(16 * u, 5 * u))
        val n = (2 + e.hojas / 3).coerceIn(2, 60)
        val pila = ((5 + 5.2f * sqrt(e.hojas.coerceAtLeast(1).toFloat())).coerceAtMost(78f)) * u
        val paso = pila / n
        val base = by - 10 * u
        val colores = listOf(papel, mezcla(papel, Color(0xFFFFE08A), 0.35f), papel, mezcla(papel, Color(0xFFFFB8D8), 0.3f), papel, mezcla(papel, Color(0xFFB8E0FF), 0.3f))
        for (k in 0 until n) {
            val y = base - k * paso
            val giro = ((azar(e.semilla, 50 + k) - 0.5f) * 0.6f) * (1f - a * 0.4f) + a * ((k + 0.5f) / n - 0.5f) * 2.2f
            val co = cos(giro); val si = sin(giro)
            val lx = 46 * u; val ly = 26 * u; val kd = 0.36f
            fun p(x: Float, z: Float) = Offset(cx + x * co - z * si, y - (x * si + z * co) * kd)
            val q = listOf(p(-lx, -ly), p(lx, -ly), p(lx, ly), p(-lx, ly))
            val cuerpo = Path().apply { moveTo(q[0].x, q[0].y); q.drop(1).forEach { lineTo(it.x, it.y) }; close() }
            drawPath(cuerpo, colores[(k + (e.semilla and 3)) % colores.size])
            drawPath(cuerpo, Color.Black.copy(alpha = 0.22f), style = Stroke(0.6f * u))
            // Los renglones impresos del ticket, apenas.
            if (k == n - 1 || k % 3 == 0) for (j in 0 until 3) {
                val z = -ly * 0.5f + j * ly * 0.45f
                drawLine(TINTA.copy(alpha = 0.18f), p(-lx * 0.7f, z), p(lx * (0.2f + 0.4f * azar(e.semilla, 90 + k + j)), z), 0.8f * u)
            }
        }
        val cima = base - pila
        // La púa, con su punta.
        val punta = Offset(cx, frenteY - 178 * u)
        val sube = a * 30 * u
        drawLine(mezcla(metal, Color.Black, 0.3f), Offset(cx + 0.6f * u, base), Offset(cx + 0.6f * u, punta.y), 2.6f * u)
        drawLine(metal, Offset(cx - 0.4f * u, base), Offset(cx - 0.4f * u, punta.y), 1.2f * u)
        drawLine(Color.White.copy(alpha = 0.7f), Offset(cx - 0.6f * u, cima - 4 * u), Offset(cx - 0.6f * u, punta.y + 8 * u), 0.5f * u)

        // ---- El último ticket: ensartado por su agujero y caído hacia delante; al abrirse sube, se suelta y se desenrolla.
        val tw = w * 0.36f
        val agujero = Offset(cx, punta.y + 12 * u - sube)
        val largoT = (cima - agujero.y - 14 * u).coerceAtLeast(64 * u) + a * 46 * u
        rotate(-11f * a, agujero) {
            val r = Rect(cx - tw / 2, agujero.y - 9 * u, cx + tw / 2, agujero.y - 9 * u + largoT)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 3 * u, r.top + 4 * u), r.size)
            // El borde de arriba, rasgado en dientes, y el de abajo, enrollado.
            drawPath(Path().apply {
                moveTo(r.left, r.top + 3 * u)
                var x = r.left; var arribaD = true
                while (x < r.right) { x += 5 * u; lineTo(minOf(x, r.right), if (arribaD) r.top else r.top + 3 * u); arribaD = !arribaD }
                lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
            }, papel)
            drawRect(mezcla(papel, Color.Black, 0.22f), Offset(r.left, r.bottom - 5 * u), Size(r.width, 5 * u))
            drawRect(Color.White.copy(alpha = 0.35f), Offset(r.left, r.bottom - 5 * u), Size(r.width, 1 * u))
            // El agujero por donde pasa la púa.
            drawCircle(Color.Black.copy(alpha = 0.55f), 2.2f * u, agujero)
            drawCircle(TINTA.copy(alpha = 0.25f), 3.4f * u, agujero, style = Stroke(0.6f * u))
            // El nombre y la cuenta, con sus filetes.
            val m = 8 * u
            textoEn(medidor, e.nombre, Rect(r.left + m, agujero.y + 4 * u, r.right - m, agujero.y + 21 * u), letra, TINTA, 13f, peso = FontWeight.Bold, minimo = 6f)
            drawLine(TINTA.copy(alpha = 0.55f), Offset(r.left + m, agujero.y + 23 * u), Offset(r.right - m, agujero.y + 23 * u), 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.6f * u)))
            textoEn(medidor, e.cuenta, Rect(r.left + m, agujero.y + 25 * u, r.right - m, agujero.y + 37 * u), Letra.terminal, TINTA.copy(alpha = 0.85f), 9.5f, minimo = 5f)
            // Los renglones de lo pedido, con su precio, hasta el pie; y el código de barras.
            var y = agujero.y + 44 * u; var k = 0
            while (y < r.bottom - 22 * u) {
                drawLine(TINTA.copy(alpha = 0.4f), Offset(r.left + m, y), Offset(r.left + m + (r.width * 0.3f) * (0.5f + 0.5f * azar(e.semilla, 130 + k)), y), 1 * u)
                drawLine(TINTA.copy(alpha = 0.4f), Offset(r.right - m - r.width * 0.14f, y), Offset(r.right - m, y), 1 * u)
                y += 6 * u; k++
            }
            if (r.bottom - 20 * u > agujero.y + 42 * u) {
                var bx = r.left + r.width * 0.25f; var j = 0
                while (bx < r.right - r.width * 0.25f) {
                    val gr = (if (azar(e.semilla, 300 + j) > 0.5f) 1.6f else 0.8f) * u
                    drawRect(TINTA.copy(alpha = 0.8f), Offset(bx, r.bottom - 18 * u), Size(gr, 9 * u))
                    bx += gr + (0.9f + azar(e.semilla, 400 + j)) * u; j++
                }
            }
            // El sello del puesto, en magenta.
            drawCircle(MAGENTA.copy(alpha = 0.55f), 7 * u, Offset(r.right - 14 * u, agujero.y + 4 * u), style = Stroke(1.2f * u))
            drawCircle(MAGENTA.copy(alpha = 0.5f), 2.4f * u, Offset(r.right - 14 * u, agujero.y + 4 * u))
            // El reflejo magenta del letrero en el papel.
            drawRect(MAGENTA.copy(alpha = 0.06f), r.topLeft, r.size)
        }
        // La punta de la púa asoma por el agujero mientras el ticket sigue ensartado.
        if (sube < 10 * u) {
            drawLine(metal, Offset(cx, agujero.y), Offset(cx, punta.y), 1.4f * u)
            drawCircle(Color.White.copy(alpha = 0.8f), 0.9f * u, punta)
        }
    }

    /**
     * La pestaña de un día: un tiquete del puesto, con su agujero de la púa arriba, el borde de abajo rasgado en dientes, el
     * día grande, la letra de la semana y el sello de neón del color del ánimo (con su halo, sin degradados).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val papel = mezcla(ETIQUETA, Color(0xFF06050B), if (oscuro) 0.12f else 0f)
        rotate(-4f + 8f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val r = Rect(4 * u, 3 * u, w - 4 * u, h - 4 * u)
            val dientes = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 3 * u)
                var x = r.right; var abajo = true
                while (x > r.left) { x -= 4 * u; lineTo(maxOf(x, r.left), if (abajo) r.bottom else r.bottom - 3 * u); abajo = !abajo }
                close()
            }
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left + 1.5f * u, r.top + 2 * u), Size(r.width, r.height - 2 * u))
            drawPath(dientes, papel)
            drawCircle(Color.Black.copy(alpha = 0.7f), 1.8f * u, Offset(w / 2, r.top + 5 * u))
            textoEn(medidor, e.semana, Rect(r.left + 2 * u, r.top + 1 * u, r.left + 10 * u, r.top + 10 * u), Letra.terminal, TINTA.copy(alpha = 0.8f), 8f, minimo = 5f)
            drawLine(TINTA.copy(alpha = 0.5f), Offset(r.left + 3 * u, r.top + 11 * u), Offset(r.right - 3 * u, r.top + 11 * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u)))
            textoEn(medidor, e.dia, Rect(r.left + 1 * u, r.top + 12 * u, r.right - 1 * u, r.top + 32 * u), letra, TINTA, 19f, peso = FontWeight.Bold, minimo = 8f)
            val s = Offset(w / 2, r.top + 40 * u)
            if (e.tono != null) {
                drawCircle(e.tono.copy(alpha = 0.18f), 7 * u, s)
                drawCircle(e.tono.copy(alpha = 0.35f), 5.4f * u, s)
                drawCircle(e.tono, 4 * u, s)
                drawCircle(mezcla(e.tono, Color.White, 0.55f), 1.6f * u, s)
            } else drawCircle(TINTA.copy(alpha = 0.35f), 4 * u, s, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1.2f * u))))
        }
    }

    /**
     * El rótulo de un mes: un letrero de neón sobre su placa negra atornillada, con el mes escrito en tubo magenta (el halo
     * hecho de copias corridas del texto, sin degradados) y dos tubos cian a los lados.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val placa = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
        drawRoundRect(c(Color(0xFF0C0A12)), placa.topLeft, placa.size, CornerRadius(4 * u))
        drawRoundRect(c(CARCASA_LUZ), placa.topLeft, placa.size, CornerRadius(4 * u), style = Stroke(1 * u))
        listOf(7 * u, w - 7 * u).forEach { x -> drawCircle(c(ALAMBRE), 1.6f * u, Offset(x, h / 2)) }
        val tam = 15f
        val ancho = anchoTexto(medidor, texto, letra, tam, FontWeight.Bold).coerceAtMost(w - 70 * u)
        val r = Rect(w / 2 - ancho / 2 - 4 * u, 4 * u, w / 2 + ancho / 2 + 4 * u, h - 4 * u)
        for (k in 0 until 8) {
            val ang = k * PI.toFloat() / 4
            textoEn(medidor, texto, r.translate(cos(ang) * 1.6f * u, sin(ang) * 1.3f * u), letra, MAGENTA.copy(alpha = 0.16f), tam, peso = FontWeight.Bold, minimo = 7f)
        }
        textoEn(medidor, texto, r, letra, MAGENTA.copy(alpha = 0.8f), tam, peso = FontWeight.Bold, minimo = 7f)
        textoEn(medidor, texto, r.translate(0f, -0.3f * u), letra, mezcla(MAGENTA, Color.White, 0.6f), tam, peso = FontWeight.Bold, minimo = 7f)
        val iz = r.left - 8 * u; val de = r.right + 8 * u
        if (iz > 22 * u) {
            tubo(Path().apply { moveTo(14 * u, h / 2); lineTo(iz, h / 2) }, CIAN, 1.2f * u)
            tubo(Path().apply { moveTo(de, h / 2); lineTo(w - 14 * u, h / 2) }, CIAN, 1.2f * u)
        }
    }

    // ---------------------------------------------------------------- las cartas

    /** El plástico negro violáceo de la bolsa de mensajería, su forro magenta y el fleje amarillo de seguridad. */
    private val BOLSA = Color(0xFF2B1D36)
    private val BOLSA_LUZ = Color(0xFF45325A)
    private val BOLSA_FORRO = Color(0xFF4A1232)
    private val FLEJE = Color(0xFFE8D43A)

    /**
     * La bolsa de mensajería del repartidor, de plástico grueso y brillante, mojada por la lluvia (las gotas resbalan y el
     * plástico refleja los neones: magenta por un lado, cian por el otro). La solapa adhesiva tiene la tira de abre-fácil de
     * dientes; encima, la etiqueta de envío con su franja magenta, a quién va y las fechas, y el código de barras. El sello es
     * la calcomanía holográfica del puesto (los aros de colores y el rayo de entrega rápida), pegada sobre el borde de la
     * solapa: brilla cuando el pedido ya llegó y se raja al abrirlo. Las de al futuro van con el fleje de seguridad amarillo,
     * que baja por el costado hasta su hebilla. Dentro va la carta, oscura como la noche del callejón, con su filo magenta.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val borde = bis + h * 0.28f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val papel = mezcla(ETIQUETA, Color(0xFF06050B), 0.08f)
        val radio = 6 * u
        // La calcomanía holográfica, centrada sobre el borde de la solapa.
        val sc = Offset(s.left + w * 0.5f, borde)
        val sr = h * 0.105f
        fun calcomania(alfa: Float) {
            fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
            drawCircle(a(Color.Black.copy(alpha = 0.35f)), sr, sc + Offset(0.6f * u, 1.2f * u))
            drawCircle(a(Color(0xFFE9E4F0)), sr, sc)
            listOf(CIAN, MAGENTA, AMBAR, SODIO).forEachIndexed { i, col -> drawCircle(a(col.copy(alpha = 0.85f)), sr * (0.9f - i * 0.13f), sc, style = Stroke(sr * 0.11f)) }
            drawCircle(a(Color(0xFF1C0010)), sr * 0.42f, sc)
            drawPath(Path().apply {
                moveTo(sc.x + sr * 0.1f, sc.y - sr * 0.36f); lineTo(sc.x - sr * 0.2f, sc.y + sr * 0.05f); lineTo(sc.x - sr * 0.01f, sc.y + sr * 0.05f)
                lineTo(sc.x - sr * 0.1f, sc.y + sr * 0.36f); lineTo(sc.x + sr * 0.2f, sc.y - sr * 0.06f); lineTo(sc.x + sr * 0.01f, sc.y - sr * 0.06f); close()
            }, a(mezcla(AMBAR, Color.White, 0.3f + 0.5f * e.brillo)))
            drawArc(a(Color.White.copy(alpha = 0.55f)), 200f, 60f, false, Offset(sc.x - sr * 0.75f, sc.y - sr * 0.75f), Size(sr * 1.5f, sr * 1.5f), style = Stroke(sr * 0.08f))
        }

        // ---- La bolsa por detrás (el forro) y su sombra en el asfalto mojado.
        drawRoundRect(Color.Black.copy(alpha = 0.5f), Offset(s.left + 2 * u, s.top + 4 * u), s.size, CornerRadius(radio))
        drawRoundRect(c(BOLSA_FORRO), s.topLeft, s.size, CornerRadius(radio))
        // ---- La solapa adhesiva, con su tira de dientes.
        fun DrawScope.solapa(adentro: Boolean) {
            val dientes = Path().apply {
                moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right, borde - 3 * u)
                val n = 22
                for (i in n downTo 0) { val x = s.left + w * i / n; lineTo(x, if (i % 2 == 0) borde - 3 * u else borde) }
                close()
            }
            if (adentro) {
                drawPath(dientes, c(mezcla(BOLSA_FORRO, BOLSA, 0.4f)))
                // La tira del adhesivo, ya sin su papel protector.
                drawRect(c(Color(0xFF7A5A86)).copy(alpha = 0.6f), Offset(s.left + w * 0.04f, borde - h * 0.1f), Size(w * 0.92f, h * 0.05f))
                return
            }
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(s.left, borde + 1 * u), Offset(s.right, borde + 1 * u), 1.6f * u)
            drawPath(dientes, c(BOLSA_LUZ))
            // La línea de puntos de la tira abre-fácil y la flecha que dice por dónde tirar.
            drawLine(c(MAGENTA).copy(alpha = 0.8f), Offset(s.left + w * 0.04f, bis + h * 0.12f), Offset(s.right - w * 0.04f, bis + h * 0.12f), 1 * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.5f * u)))
            drawPath(Path().apply { moveTo(s.right - w * 0.1f, bis + h * 0.07f); lineTo(s.right - w * 0.05f, bis + h * 0.12f); lineTo(s.right - w * 0.1f, bis + h * 0.17f) },
                c(MAGENTA), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            if (e.romper > 0f) withTransform({ translate(-1.2f * u * e.romper, -2f * u * e.romper); rotate(-7f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, true, 5, e.semilla)) { calcomania(1f) }
            }
        }
        if (!cerrada) solapaCarta(bis, ab) { solapa(it) }

        // ---- La carta, oscura, con su filo magenta.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
            val hoja = Rect(s.left + w * 0.08f, arriba0 - alza, s.right - w * 0.08f, arriba0 - alza + h * 0.9f)
            drawRect(Color.Black.copy(alpha = 0.4f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
            drawRect(e.hoja, hoja.topLeft, hoja.size)
            drawRect(MAGENTA.copy(alpha = 0.5f), hoja.topLeft, hoja.size, style = Stroke(1 * u))
            drawRect(MAGENTA.copy(alpha = 0.7f), hoja.topLeft, Size(hoja.width, 2.5f * u))
            var y = hoja.top + 15 * u
            while (y < hoja.bottom - 3 * u) { drawLine(e.renglones, Offset(hoja.left + 5 * u, y), Offset(hoja.right - 5 * u, y), 0.7f * u); y += 11 * u }
        }

        // ---- El frente de la bolsa: el plástico, sus reflejos de neón y las gotas.
        drawRoundRect(c(BOLSA), s.topLeft, s.size, CornerRadius(radio))
        clipRect(s.left, s.top, s.right, s.bottom) {
            drawPath(Path().apply { moveTo(s.left + w * 0.55f, s.bottom); lineTo(s.left + w * 0.7f, s.bottom); lineTo(s.left + w * 0.95f, s.top); lineTo(s.left + w * 0.8f, s.top); close() },
                Color.White.copy(alpha = 0.035f))
            drawRoundRect(MAGENTA.copy(alpha = 0.55f), Offset(s.right - 2.5f * u, s.top + h * 0.1f), Size(1.6f * u, h * 0.8f), CornerRadius(1 * u))
            drawRoundRect(CIAN.copy(alpha = 0.4f), Offset(s.left + 1 * u, s.top + h * 0.2f), Size(1.4f * u, h * 0.7f), CornerRadius(1 * u))
            for (i in 0 until 16) {
                val o = Offset(s.left + w * azar(e.semilla, i), s.top + h * azar(e.semilla, i + 40))
                val l = (2 + 5 * azar(e.semilla, i + 80)) * u
                drawLine(LLUVIA.copy(alpha = 0.18f), o, o + Offset(0f, l), 1.6f * u, StrokeCap.Round)
                drawCircle(Color.White.copy(alpha = 0.35f), 0.6f * u, o + Offset(-0.3f * u, l * 0.7f))
            }
        }
        drawRoundRect(c(CARCASA_OSC), s.topLeft, s.size, CornerRadius(radio), style = Stroke(1 * u))
        if (!cerrada) drawLine(Color.Black.copy(alpha = 0.6f), Offset(s.left + 2 * u, s.top + 0.8f * u), Offset(s.right - 2 * u, s.top + 0.8f * u), 1.6f * u)
        // ---- La etiqueta de envío.
        val et = Rect(s.left + w * 0.13f, s.top + h * 0.4f, s.right - w * 0.04f, s.bottom - h * 0.06f)
        drawRect(Color.Black.copy(alpha = 0.35f), et.topLeft + Offset(0.8f * u, 1.2f * u), et.size)
        drawRect(papel, et.topLeft, et.size)
        val franja = et.height * 0.17f
        drawRect(c(MAGENTA), et.topLeft, Size(et.width, franja))
        textoEn(medidor, "REPARTO 24 H", Rect(et.left + 3 * u, et.top, et.left + et.width * 0.62f, et.top + franja), letra, Color.White, 6.5f, peso = FontWeight.Bold, espaciado = 0.8f, minimo = 5f)
        textoEn(medidor, "Nº ${(e.semilla.toLong() and 0xFFFF).toString().padStart(5, '0')}", Rect(et.left + et.width * 0.62f, et.top, et.right - 3 * u, et.top + franja),
            letra, Color.White, 6.5f, minimo = 5f)
        val textoArriba = et.top + franja + 1 * u
        textoEn(medidor, e.linea1, Rect(et.left + 3 * u, textoArriba, et.right - 3 * u, textoArriba + et.height * 0.33f), letra, TINTA, 11.5f, peso = FontWeight.Bold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(et.left + 3 * u, textoArriba + et.height * 0.33f, et.right - 3 * u, textoArriba + et.height * 0.55f), letra, TINTA.copy(alpha = 0.85f), 9f, minimo = 6f)
        // El código de barras al pie.
        var bx = et.left + 4 * u; var j = 0
        val by = et.bottom - et.height * 0.2f
        while (bx < et.right - 4 * u) {
            val g = (if (azar(e.semilla, j + 120) > 0.6f) 1.6f else 0.7f) * u
            drawRect(TINTA, Offset(bx, by), Size(g, et.height * 0.14f)); bx += g + (0.8f + 1.2f * azar(e.semilla, j + 160)) * u; j++
        }
        // Una gota sobre la etiqueta, que corre un poco la tinta del borde.
        drawCircle(LLUVIA.copy(alpha = 0.25f), 2.2f * u, Offset(et.right - et.width * 0.12f, et.top + et.height * 0.62f))
        drawCircle(Color.White.copy(alpha = 0.6f), 0.7f * u, Offset(et.right - et.width * 0.12f - 0.7f * u, et.top + et.height * 0.62f - 0.8f * u))

        // ---- La solapa cerrada encima.
        if (cerrada) solapaCarta(bis, ab) { solapa(it) }
        // ---- El fleje de seguridad: baja por el costado izquierdo y se cierra en su hebilla.
        if (e.cordon > 0f && cerrada) {
            val fx = s.left + w * 0.065f
            val largo = (s.height + 6 * u) * e.cordon
            val fw = 6.5f * u
            drawRect(Color.Black.copy(alpha = 0.4f), Offset(fx - fw / 2 + 1 * u, s.top - 3 * u + 1.5f * u), Size(fw, largo))
            drawRect(c(FLEJE), Offset(fx - fw / 2, s.top - 3 * u), Size(fw, largo))
            var yy = s.top
            while (yy < s.top - 3 * u + largo - 3 * u) { drawLine(c(Color(0xFF8A7A10)), Offset(fx - fw / 2, yy), Offset(fx + fw / 2, yy + 2 * u), 0.7f * u); yy += 4 * u }
            if (e.cordon > 0.92f) {
                val hb = Offset(fx, s.top + h * 0.62f)
                drawRoundRect(Color.Black.copy(alpha = 0.4f), Offset(hb.x - fw * 0.9f + 1 * u, hb.y - fw * 0.8f + 1.5f * u), Size(fw * 1.8f, fw * 1.6f), CornerRadius(1.5f * u))
                drawRoundRect(c(Color(0xFF1A1A1E)), Offset(hb.x - fw * 0.9f, hb.y - fw * 0.8f), Size(fw * 1.8f, fw * 1.6f), CornerRadius(1.5f * u))
                drawRect(c(FLEJE), Offset(hb.x - fw * 0.5f, hb.y - fw * 0.15f), Size(fw, fw * 0.3f))
                drawCircle(c(MAGENTA), 1 * u, Offset(hb.x + fw * 0.55f, hb.y - fw * 0.5f))
            }
        }
        // ---- La calcomanía: entera (cayendo desde grande al sellar) o rajada por el borde de la solapa.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(MAGENTA.copy(alpha = 0.6f * e.brillo), CIAN.copy(alpha = 0.18f * e.brillo), Color.Transparent),
                center = sc, radius = sr * 3f), sr * 3f, sc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc) }) { calcomania(e.alfaSello.coerceIn(0f, 1f)) }
        } else {
            withTransform({ translate(1.2f * u * e.romper, 2.5f * u * e.romper); rotate(8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, false, 5, e.semilla)) { calcomania(1f) }
            }
        }
    }

    // La carta oscura que asoma de la bolsa, bajo su filo magenta.
    override val cajaPrevia = Rect(0.13f, 0.165f, 0.87f, 0.32f)

    override val altoBuzon: Dp = 212.dp

    /**
     * Los casilleros de paquetería del callejón, abiertos toda la noche: un bloque de puertecitas de chapa con su luz de
     * estado y, al centro, la pantalla del puesto con las tres cuentas («LISTOS PARA RECOGER», «EN CAMINO», «ENTREGADOS»).
     * Cada carta es un casillero: los listos tienen la luz verde encendida y el paquete brillando tras la mirilla; los que van
     * en camino, cerrados con su luz magenta y el candado; los entregados, con la puerta abierta y la bolsa rasgada dentro. Los
     * que sobran, apagados. Arriba, el tubo de neón del letrero (una caja y una flecha: formas, no letras) y, abajo, el asfalto
     * mojado que lo refleja todo.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val verde = Color(0xFF5CFF8A)
        // ---- El muro del callejón y el asfalto.
        drawRect(c(Color(0xFF1A1424)))
        for (i in 0 until 9) drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, h * i / 9f), Offset(w, h * i / 9f), 1 * u)
        val piso = h - 26 * u
        drawRect(c(ASFALTO), Offset(0f, piso), Size(w, h - piso))
        // ---- El letrero de neón: una caja con su cinta y una flecha que baja.
        val ln = Offset(w * 0.5f, 18 * u)
        drawRoundRect(c(CARCASA_OSC), Offset(ln.x - 58 * u, 4 * u), Size(116 * u, 28 * u), CornerRadius(4 * u))
        tubo(Path().apply { moveTo(ln.x - 44 * u, ln.y - 6 * u); lineTo(ln.x - 26 * u, ln.y - 10 * u); lineTo(ln.x - 8 * u, ln.y - 6 * u); lineTo(ln.x - 8 * u, ln.y + 8 * u); lineTo(ln.x - 26 * u, ln.y + 12 * u)
            lineTo(ln.x - 44 * u, ln.y + 8 * u); close(); moveTo(ln.x - 44 * u, ln.y - 6 * u); lineTo(ln.x - 26 * u, ln.y - 2 * u); lineTo(ln.x - 8 * u, ln.y - 6 * u); moveTo(ln.x - 26 * u, ln.y - 2 * u); lineTo(ln.x - 26 * u, ln.y + 12 * u) }, CIAN, 1.3f * u)
        tubo(Path().apply { moveTo(ln.x + 10 * u, ln.y - 4 * u); lineTo(ln.x + 34 * u, ln.y - 4 * u); lineTo(ln.x + 34 * u, ln.y + 8 * u); moveTo(ln.x + 28 * u, ln.y + 2 * u); lineTo(ln.x + 34 * u, ln.y + 9 * u); lineTo(ln.x + 40 * u, ln.y + 2 * u) }, MAGENTA, 1.3f * u)
        // ---- El bloque de casilleros.
        val bloque = Rect(10 * u, 38 * u, w - 10 * u, piso - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.5f), bloque.topLeft + Offset(3 * u, 4 * u), bloque.size)
        drawRoundRect(c(CARCASA), bloque.topLeft, bloque.size, CornerRadius(4 * u))
        drawRoundRect(c(CARCASA_LUZ), bloque.topLeft, bloque.size, CornerRadius(4 * u), style = Stroke(1.2f * u))
        tubo(Path().apply { moveTo(bloque.left + 3 * u, bloque.top + 3 * u); lineTo(bloque.right - 3 * u, bloque.top + 3 * u) }, MAGENTA, 1 * u)
        // La pantalla del centro, con las tres cuentas.
        val pan = Rect(w / 2 - 50 * u, bloque.top + 10 * u, w / 2 + 50 * u, bloque.bottom - 14 * u)
        drawRect(c(CARCASA_OSC), pan.inflate(3 * u).topLeft, pan.inflate(3 * u).size)
        drawRect(Color(0xFF07060C), pan.topLeft, pan.size)
        val filas = listOf(Triple("PARA RECOGER", e.listas, verde), Triple("EN CAMINO", e.selladas, MAGENTA), Triple("ENTREGADOS", e.abiertas, AMBAR))
        filas.forEachIndexed { i, (nombre, n, col) ->
            val fy = pan.top + 4 * u + i * (pan.height - 8 * u) / 3f
            val fh = (pan.height - 8 * u) / 3f
            textoEn(medidor, nombre, Rect(pan.left + 4 * u, fy, pan.right - 4 * u, fy + fh * 0.4f), letra, col.copy(alpha = 0.85f), 7f, espaciado = 0.6f, minimo = 5f)
            textoEn(medidor, n.toString().padStart(3, '0'), Rect(pan.left + 4 * u, fy + fh * 0.36f, pan.right - 4 * u, fy + fh), letra, col, 16f, peso = FontWeight.Bold, espaciado = 3f)
            if (i < 2) drawLine(col.copy(alpha = 0.2f), Offset(pan.left + 6 * u, fy + fh), Offset(pan.right - 6 * u, fy + fh), 0.6f * u)
        }
        for (y in 0 until (pan.height / (2 * u)).toInt()) drawLine(Color.Black.copy(alpha = 0.25f), Offset(pan.left, pan.top + y * 2 * u), Offset(pan.right, pan.top + y * 2 * u), 0.5f * u)
        // Las puertas: tres filas a cada lado de la pantalla, tres columnas por lado.
        val puertas = mutableListOf<Rect>()
        val cols = 3; val filasN = 3
        listOf(bloque.left + 8 * u to pan.left - 10 * u, pan.right + 10 * u to bloque.right - 8 * u).forEach { (x0, x1) ->
            val pw = (x1 - x0 - (cols - 1) * 3 * u) / cols
            val ph = (bloque.height - 18 * u - (filasN - 1) * 3 * u) / filasN
            for (f in 0 until filasN) for (k in 0 until cols) puertas.add(Rect(x0 + k * (pw + 3 * u), bloque.top + 10 * u + f * (ph + 3 * u), x0 + k * (pw + 3 * u) + pw, bloque.top + 10 * u + f * (ph + 3 * u) + ph))
        }
        val orden = puertas.sortedWith(compareBy({ it.top }, { it.left }))
        val tipos = List(e.listas) { 0 } + List(e.selladas) { 1 } + List(e.abiertas) { 2 }
        var brilla = false
        orden.forEachIndexed { i, p ->
            val tipo = tipos.getOrNull(i) ?: -1
            val led = Offset(p.right - 5 * u, p.top + 5 * u)
            if (tipo == 2) {
                // La puerta abierta: el hueco oscuro, la bolsa rasgada dentro y la puerta girada hacia fuera.
                drawRect(Color(0xFF07060C), p.topLeft, p.size)
                drawRoundRect(c(BOLSA_LUZ), Offset(p.left + p.width * 0.18f, p.top + p.height * 0.42f), Size(p.width * 0.64f, p.height * 0.48f), CornerRadius(2 * u))
                drawRect(papelTapaNeon(), Offset(p.left + p.width * 0.3f, p.top + p.height * 0.3f), Size(p.width * 0.4f, p.height * 0.2f))
                drawPath(Path().apply { moveTo(p.left, p.top); lineTo(p.left - p.width * 0.28f, p.top + 3 * u); lineTo(p.left - p.width * 0.28f, p.bottom - 3 * u); lineTo(p.left, p.bottom); close() }, c(CARCASA_LUZ))
                drawCircle(AMBAR.copy(alpha = 0.7f), 1.4f * u, led)
            } else {
                drawRect(c(Color(0xFF2E2840)), p.topLeft, p.size)
                drawRect(c(CARCASA_LUZ), p.topLeft, Size(p.width, 1 * u))
                drawRect(Color.Black.copy(alpha = 0.4f), Offset(p.left, p.bottom - 1.2f * u), Size(p.width, 1.2f * u))
                // La mirilla y el tirador.
                val mir = Rect(p.left + p.width * 0.18f, p.top + p.height * 0.28f, p.right - p.width * 0.18f, p.top + p.height * 0.62f)
                drawRect(Color(0xFF0B0912), mir.topLeft, mir.size)
                drawRect(c(Color(0xFF8A8296)), Offset(p.left + p.width * 0.35f, p.bottom - p.height * 0.2f), Size(p.width * 0.3f, 2 * u))
                when (tipo) {
                    0 -> {
                        brilla = true
                        drawRect(verde.copy(alpha = 0.25f), mir.topLeft, mir.size)
                        drawRoundRect(BOLSA_LUZ, Offset(mir.left + mir.width * 0.15f, mir.top + mir.height * 0.25f), Size(mir.width * 0.7f, mir.height * 0.75f), CornerRadius(1.5f * u))
                        drawCircle(MAGENTA, mir.height * 0.18f, Offset(mir.center.x, mir.top + mir.height * 0.45f))
                        drawCircle(verde.copy(alpha = 0.3f), 4 * u, led); drawCircle(verde, 1.8f * u, led)
                    }
                    1 -> {
                        drawRoundRect(BOLSA, Offset(mir.left + mir.width * 0.15f, mir.top + mir.height * 0.3f), Size(mir.width * 0.7f, mir.height * 0.7f), CornerRadius(1.5f * u))
                        drawRect(FLEJE.copy(alpha = 0.6f), Offset(mir.left + mir.width * 0.25f, mir.top + mir.height * 0.3f), Size(mir.width * 0.08f, mir.height * 0.7f))
                        drawCircle(MAGENTA.copy(alpha = 0.3f), 3 * u, led); drawCircle(MAGENTA, 1.4f * u, led)
                        val cd = Offset(p.left + 5 * u, p.top + 6 * u)
                        drawArc(c(ALAMBRE), 180f, 180f, false, Offset(cd.x - 1.8f * u, cd.y - 3.2f * u), Size(3.6f * u, 4 * u), style = Stroke(0.8f * u))
                        drawRect(c(ALAMBRE), Offset(cd.x - 2.4f * u, cd.y - 1 * u), Size(4.8f * u, 3.4f * u))
                    }
                    else -> drawCircle(c(Color(0xFF3A3448)), 1.4f * u, led)
                }
            }
            drawRect(c(CARCASA_OSC), p.topLeft, p.size, style = Stroke(0.8f * u))
        }
        val total = e.listas + e.selladas + e.abiertas
        if (total > orden.size) textoEn(medidor, "+${total - orden.size} EN BODEGA", Rect(pan.left, pan.bottom + 3 * u, pan.right, bloque.bottom - 1 * u), letra, AMBAR.copy(alpha = 0.85f), 7f)
        // El resplandor verde de los listos sobre el muro.
        if (brilla) drawRect(Brush.verticalGradient(listOf(Color.Transparent, verde.copy(alpha = 0.08f)), bloque.top, bloque.bottom), bloque.topLeft, bloque.size)
        // ---- El reflejo en el asfalto mojado.
        drawRect(MAGENTA.copy(alpha = 0.18f), Offset(bloque.left, piso + 3 * u), Size(bloque.width, 1.5f * u))
        for (i in 0 until 7) drawLine(listOf(CIAN, MAGENTA, verde, AMBAR)[i % 4].copy(alpha = 0.12f), Offset(w * (0.1f + 0.12f * i), piso + 6 * u), Offset(w * (0.12f + 0.12f * i), h - 2 * u), (2 + 2 * azar(5, i)) * u)
        for (i in 0 until 14) drawLine(LLUVIA.copy(alpha = 0.15f), Offset(w * azar(9, i), h * azar(11, i)), Offset(w * azar(9, i) - 2 * u, h * azar(11, i) + 8 * u), 0.8f * u)
    }

    /** El papel de los tickets y etiquetas, que apenas se hunde para que se lea. */
    private fun papelTapaNeon(): Color = mezcla(ETIQUETA, Color(0xFF06050B), 0.12f)
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 114.dp

    /** El átomo del refugio en fósforo: tres órbitas y el núcleo; de noche, la luna creciente con dos estrellas de píxel. */
    private fun DrawScope.atomoFosforo(c: Offset, r: Float, luz: Color, nucleo: Color, fondo: Color, noche: Boolean) {
        val u = density
        if (!noche) {
            listOf(0f, 60f, 120f).forEach { g ->
                rotate(g, c) { drawOval(luz, Offset(c.x - r, c.y - r * 0.36f), Size(r * 2, r * 0.72f), style = Stroke(1.3f * u)) }
            }
            drawCircle(nucleo, r * 0.2f, c)
            listOf(0f, 60f, 120f).forEachIndexed { k, g ->
                val a = Math.toRadians(g + 35.0 * (k + 1))
                drawCircle(luz, r * 0.09f, Offset(c.x + r * cos(a).toFloat() * 0.95f, c.y + r * sin(a).toFloat() * 0.55f))
            }
        } else {
            drawCircle(luz, r * 0.7f, c)
            drawCircle(fondo, r * 0.6f, Offset(c.x + r * 0.32f, c.y - r * 0.2f))
            listOf(Offset(c.x + r * 0.75f, c.y + r * 0.45f), Offset(c.x + r * 0.95f, c.y - r * 0.55f)).forEach { o ->
                val s = r * 0.1f
                drawRect(nucleo, Offset(o.x - s / 2, o.y - s * 1.5f), Size(s, s * 3))
                drawRect(nucleo, Offset(o.x - s * 1.5f, o.y - s / 2), Size(s * 3, s))
            }
        }
    }

    /**
     * La ventana de la bitácora en la terminal: el marco doble en fósforo, la barra de título en video inverso con el rótulo y
     * los indicadores de memoria, el átomo del refugio a la izquierda (de noche, la luna), el indicador de comando «>» con la
     * fecha grande y el detalle debajo, el cursor de bloque y las líneas de barrido por encima de todo.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fosforo = p.acento
        val caja = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 1.5f * u)
        drawRect(mezcla(p.color, Color.Black, 0.3f), caja.topLeft, caja.size)
        drawRect(fosforo.copy(alpha = 0.75f), caja.topLeft, caja.size, style = Stroke(1.4f * u))
        val dentro = caja.deflate(3.5f * u)
        drawRect(fosforo.copy(alpha = 0.3f), dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
        // La barra de título en video inverso, con los indicadores a la derecha.
        val titulo = Rect(dentro.left, dentro.top, dentro.right, dentro.top + 20 * u)
        drawRect(fosforo.copy(alpha = 0.9f), titulo.topLeft, titulo.size)
        val ind = 5
        for (k in 0 until ind) {
            val x = titulo.right - 8 * u - (ind - k) * 7 * u
            val o = Offset(x, titulo.center.y - 3 * u)
            if (k < 3) drawRect(p.color, o, Size(5 * u, 6 * u)) else drawRect(p.color, o, Size(5 * u, 6 * u), style = Stroke(1 * u))
        }
        textoEn(medidor, e.rotulo, Rect(titulo.left + 8 * u, titulo.top, titulo.right - 8 * u - (ind + 1) * 7 * u, titulo.bottom), LetraExtra.vt323, p.color, 17f, minimo = 8f)
        // El átomo (o la luna) en su casilla.
        val celda = Rect(dentro.left, titulo.bottom, dentro.left + 54 * u, dentro.bottom)
        drawLine(fosforo.copy(alpha = 0.35f), Offset(celda.right, celda.top + 5 * u), Offset(celda.right, celda.bottom - 5 * u), 0.8f * u)
        atomoFosforo(celda.center, minOf(celda.width, celda.height) * 0.34f, fosforo, p.detalle, mezcla(p.color, Color.Black, 0.3f), e.noche)
        // El indicador de comando, la fecha grande y el detalle.
        val cuerpo = Rect(celda.right + 6 * u, titulo.bottom + 4 * u, dentro.right - 6 * u, dentro.bottom - 3 * u)
        textoEn(medidor, ">", Rect(cuerpo.left, cuerpo.top, cuerpo.left + 12 * u, cuerpo.top + 26 * u), LetraExtra.vt323, fosforo, 24f)
        fechaFuturo(medidor, e.fecha, Rect(cuerpo.left + 12 * u, cuerpo.top, cuerpo.right - 10 * u, cuerpo.top + 50 * u), LetraExtra.vt323, p.tinta, 30f)
        drawRect(fosforo.copy(alpha = 0.85f), Offset(cuerpo.right - 8 * u, cuerpo.top + 36 * u), Size(7 * u, 11 * u))
        fechaFuturo(medidor, e.detalle, Rect(cuerpo.left, cuerpo.top + 51 * u, cuerpo.right, cuerpo.bottom), LetraExtra.vt323, p.tenue, 15f, k = 0.86f)
        // Las líneas de barrido del tubo.
        var y = caja.top
        while (y < caja.bottom) { drawLine(Color.Black.copy(alpha = 0.16f), Offset(caja.left, y), Offset(caja.right, y), 1 * u); y += 3 * u }
    }

    /**
     * La pantalla de fósforo: el resplandor del centro y las esquinas hundidas del tubo, las líneas de barrido, el bisel de la
     * pantalla con sus esquinas redondas, la regla del margen con sus marcas, el doble filete de la línea de estado al pie con
     * la barra de memoria y, arriba, la línea de guiones bajo la barra. El papel ya es oscuro: solo los adornos.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fosforo = p.acento
        drawRect(Brush.radialGradient(listOf(fosforo.copy(alpha = 0.09f), Color.Transparent), center = Offset(w / 2, h * 0.45f), radius = h * 0.6f))
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.7f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.45f), center = Offset(w / 2, h / 2), radius = maxOf(w, h) * 0.62f))
        // El bisel de la pantalla.
        drawRoundRect(fosforo.copy(alpha = 0.16f), Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), CornerRadius(22 * u), style = Stroke(1.4f * u))
        // La regla del margen.
        drawLine(fosforo.copy(alpha = 0.16f), Offset(14 * u, 70 * u), Offset(14 * u, h - 46 * u), 0.8f * u)
        var y = 70 * u; var k = 0
        while (y < h - 46 * u) { drawLine(fosforo.copy(alpha = if (k % 4 == 0) 0.3f else 0.15f), Offset(14 * u, y), Offset((if (k % 4 == 0) 22 else 18) * u, y), 1 * u); y += 13 * u; k++ }
        // La línea de guiones arriba y la de estado al pie, con la barra de memoria.
        var x = 26 * u
        while (x < w - 26 * u) { drawLine(fosforo.copy(alpha = 0.22f), Offset(x, 62 * u), Offset(x + 6 * u, 62 * u), 1 * u); x += 10 * u }
        listOf(h - 34 * u, h - 31 * u).forEach { yy -> drawLine(fosforo.copy(alpha = 0.25f), Offset(24 * u, yy), Offset(w - 24 * u, yy), 0.9f * u) }
        for (j in 0 until 12) {
            val o = Offset(26 * u + j * 7 * u, h - 25 * u)
            if (j < 8) drawRect(fosforo.copy(alpha = 0.3f), o, Size(5 * u, 7 * u)) else drawRect(fosforo.copy(alpha = 0.2f), o, Size(5 * u, 7 * u), style = Stroke(0.8f * u))
        }
        drawRect(p.detalle.copy(alpha = 0.45f), Offset(w - 34 * u, h - 25 * u), Size(8 * u, 7 * u))
        // Las líneas de barrido.
        y = 0f
        while (y < h) { drawLine(Color.Black.copy(alpha = 0.2f), Offset(0f, y), Offset(w, y), 1.2f * u); y += 4 * u }
    }

    /** La línea de estado de la terminal: un poco más honda que la pantalla, con su doble filete de fósforo y las marcas de tabulación. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(mezcla(p.color, Color.Black, 0.45f), mezcla(p.color, Color.Black, 0.2f)), 0f, h))
        drawLine(p.acento.copy(alpha = 0.6f), Offset(0f, h - 4 * u), Offset(w, h - 4 * u), 1.2f * u)
        drawLine(p.acento.copy(alpha = 0.25f), Offset(0f, h - 1.5f * u), Offset(w, h - 1.5f * u), 0.8f * u)
        var x = 8 * u
        while (x < w) { drawLine(p.acento.copy(alpha = 0.35f), Offset(x, h - 4 * u), Offset(x, h - 8 * u), 1 * u); x += 24 * u }
    }

    /** Una tecla en video inverso: el bloque de fósforo con sus esquinas de píxel y su sombra escalonada. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val px = 2.5f * u
        val r = Rect(3 * u, 3 * u, w - 6 * u, h - 6 * u)
        drawRect(p.tenue.copy(alpha = 0.7f), Offset(r.left + px * 1.5f, r.top + px * 1.5f), r.size)
        drawRect(Brush.verticalGradient(listOf(mezcla(p.acento, Color.White, 0.15f), p.acento), r.top, r.bottom), r.topLeft, r.size)
        listOf(r.topLeft, Offset(r.right - px, r.top), Offset(r.left, r.bottom - px), Offset(r.right - px, r.bottom - px)).forEach { drawRect(mezcla(p.color, Color.Black, 0.32f), it, Size(px, px)) }
        drawRect(p.color.copy(alpha = 0.5f), r.deflate(px * 1.2f).topLeft, r.deflate(px * 1.2f).size, style = Stroke(0.8f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = p.color

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 266.dp

    /** El cartón verde azulado de la carpeta del supervisor y el naranja de su pestaña. */
    private val CARPETA = Color(0xFF1C6E6A)
    private val NARANJA = Color(0xFFD9572B)
    private val ESMALTE = Color(0xFF3F6E4E)

    /**
     * El cuarto de radio del refugio: la pared de hormigón crema con el zócalo pintado de verde azulado, el plano del refugio
     * clavado con chinchetas y el reloj; arriba cuelga la lámpara de esmalte verde. Sobre la mesa de chapa con cubierta de
     * linóleo, a la izquierda, la radio de onda corta (la escala iluminada, el dial grande con su aguja, la rejilla del
     * altavoz, las perillas, el auricular colgado con su cable rizado y la antena); a la derecha, el tazón de peltre que humea,
     * la lata de raciones y el lápiz. La carpeta del supervisor, de cartón verde azulado con esquinas reforzadas, va cerrada con
     * el broche de metal de arriba y la pestaña naranja; en su etiqueta amarilla, el átomo, el nombre y la cuenta. Engorda con
     * las hojas y asoman más informes por delante. Al abrirse, la tapa se levanta sobre el broche: por dentro, el bolsillo con la
     * tarjeta perforada; encima del montón, el informe con la gráfica de las lecturas y sus casillas. En oscuro, el cuarto se
     * hunde y quedan la lámpara y la escala de la radio.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF050808), 0.5f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val crema = papel(CREMA, oscuro, false)
        val fondoY = h * 0.47f
        val frenteY = h * 0.84f
        val estarcido = LetraExtra.estarcido
        // ---- La pared: hormigón crema arriba, zócalo pintado abajo con su filete amarillo.
        drawRect(f(MURO), Offset.Zero, Size(w, fondoY))
        var by = 14 * u; var fila = 0
        while (by < fondoY) {
            drawLine(Color.Black.copy(alpha = 0.07f), Offset(0f, by), Offset(w, by), 1 * u)
            var bx = if (fila % 2 == 0) 0f else 28 * u
            while (bx < w) { drawLine(Color.Black.copy(alpha = 0.07f), Offset(bx, by - 22 * u), Offset(bx, by), 1 * u); bx += 56 * u }
            by += 22 * u; fila++
        }
        val zoc = fondoY - h * 0.13f
        drawRect(f(PINTURA), Offset(0f, zoc), Size(w, fondoY - zoc))
        drawRect(f(AMARILLO), Offset(0f, zoc - 3 * u), Size(w, 3 * u))
        // El plano del refugio, clavado con chinchetas.
        val plano = Rect(w * 0.36f, h * 0.06f, w * 0.58f, h * 0.29f)
        rotate(-1.5f, plano.center) {
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(plano.left + 2 * u, plano.top + 2 * u), plano.size)
            drawRect(f(Color(0xFF2E5C8A)), plano.topLeft, plano.size)
            val lin = Color.White.copy(alpha = 0.6f)
            drawRect(lin, plano.deflate(4 * u).topLeft, plano.deflate(4 * u).size, style = Stroke(1 * u))
            val p = plano.deflate(4 * u)
            drawLine(lin, Offset(p.left + p.width * 0.4f, p.top), Offset(p.left + p.width * 0.4f, p.bottom), 0.8f * u)
            drawLine(lin, Offset(p.left, p.top + p.height * 0.55f), Offset(p.right, p.top + p.height * 0.55f), 0.8f * u)
            drawLine(lin, Offset(p.left + p.width * 0.7f, p.top + p.height * 0.55f), Offset(p.left + p.width * 0.7f, p.bottom), 0.8f * u)
            drawCircle(f(AMARILLO), 3 * u, Offset(p.left + p.width * 0.2f, p.top + p.height * 0.3f))
            drawLine(f(ROJO), Offset(p.left + p.width * 0.55f, p.top + p.height * 0.25f), Offset(p.left + p.width * 0.85f, p.top + p.height * 0.25f), 1.4f * u)
            listOf(plano.topLeft, Offset(plano.right, plano.top)).forEach { o -> drawCircle(f(ROJO), 2 * u, Offset(o.x + (if (o.x > plano.center.x) -4 else 4) * u, o.y + 4 * u)) }
        }
        // El reloj de pared.
        val rel = Offset(w * 0.9f, h * 0.15f)
        drawCircle(Color.Black.copy(alpha = 0.18f), 16 * u, Offset(rel.x + 1.5f * u, rel.y + 2 * u))
        drawCircle(f(TUBO_OSC), 16 * u, rel)
        drawCircle(f(CREMA), 13 * u, rel)
        for (k in 0 until 12) { val an = k * PI.toFloat() / 6; drawLine(f(NEGRO), Offset(rel.x + 10.5f * u * cos(an), rel.y + 10.5f * u * sin(an)), Offset(rel.x + 12 * u * cos(an), rel.y + 12 * u * sin(an)), 1 * u) }
        drawLine(f(NEGRO), rel, Offset(rel.x - 3 * u, rel.y - 7 * u), 1.6f * u)
        drawLine(f(NEGRO), rel, Offset(rel.x + 9 * u, rel.y + 2 * u), 1 * u)
        drawLine(f(ROJO), rel, Offset(rel.x - 6 * u, rel.y + 9 * u), 0.6f * u)
        // ---- La lámpara de esmalte que cuelga, con su cono de luz y la mancha en la mesa (el primer degradado).
        val lam = Offset(w * 0.55f, h * 0.15f)
        val luz = c(Color(0xFFFFE6A8))
        drawPath(Path().apply { moveTo(lam.x - 20 * u, lam.y + 2 * u); lineTo(lam.x + 20 * u, lam.y + 2 * u); lineTo(w * 0.86f, frenteY); lineTo(w * 0.22f, frenteY); close() },
            Color(0xFFFFE6A8).copy(alpha = if (oscuro) 0.07f else 0.05f))
        drawLine(f(NEGRO), Offset(lam.x, 0f), Offset(lam.x, lam.y - 14 * u), 1.2f * u)
        drawRect(f(TUBO_OSC), Offset(lam.x - 3 * u, lam.y - 15 * u), Size(6 * u, 6 * u))
        drawPath(Path().apply { moveTo(lam.x - 5 * u, lam.y - 10 * u); lineTo(lam.x + 5 * u, lam.y - 10 * u); lineTo(lam.x + 22 * u, lam.y + 2 * u); lineTo(lam.x - 22 * u, lam.y + 2 * u); close() }, c(ESMALTE))
        drawLine(Color.White.copy(alpha = 0.3f), Offset(lam.x - 4 * u, lam.y - 9 * u), Offset(lam.x - 19 * u, lam.y + 1 * u), 1 * u)
        drawOval(c(Color(0xFFEDE8D8)), Offset(lam.x - 22 * u, lam.y), Size(44 * u, 5 * u))
        drawCircle(Color(0xFFFFE6A8).copy(alpha = 0.5f), 7 * u, Offset(lam.x, lam.y + 4 * u))
        drawCircle(mezcla(Color(0xFFFFE6A8), Color.White, 0.6f), 4 * u, Offset(lam.x, lam.y + 4 * u))
        val pozo = Offset(w * 0.55f, frenteY - h * 0.16f)

        // ---- La mesa: la cubierta de linóleo con su canto de aluminio, el frente con el cajón y las patas.
        val lino = Color(0xFF55705E)
        drawRect(f(Color(0xFF242A28)), Offset(0f, frenteY), Size(w, h - frenteY))
        drawRect(f(PISO), Offset(0f, h * 0.95f), Size(w, h * 0.05f))
        drawRect(c(lino), Offset(0f, fondoY), Size(w, frenteY - fondoY))
        for (k in 0 until 30) {
            val o = Offset(w * azar(31, k), fondoY + (frenteY - fondoY) * azar(37, k))
            drawCircle(Color.Black.copy(alpha = 0.06f), (1 + 3 * azar(41, k)) * u, o)
        }
        drawOval(Brush.radialGradient(listOf(Color(0xFFFFE6A8).copy(alpha = if (oscuro) 0.32f else 0.16f), Color.Transparent), pozo, w * 0.4f),
            Offset(pozo.x - w * 0.42f, pozo.y - h * 0.22f), Size(w * 0.84f, h * 0.44f))
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, fondoY), Offset(w, fondoY), 1 * u)
        val canto = 9 * u
        drawRect(c(Color(0xFFB9C0BD)), Offset(0f, frenteY), Size(w, canto))
        drawRect(Color.White.copy(alpha = 0.35f), Offset(0f, frenteY), Size(w, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, frenteY + canto - 1.5f * u), Size(w, 1.5f * u))
        val frente = Rect(w * 0.04f, frenteY + canto, w * 0.96f, h * 0.95f)
        drawRect(f(Color(0xFF6E7B76)), frente.topLeft, Size(frente.width, 18 * u))
        drawRect(f(Color(0xFF4F5A56)), Offset(frente.left, frente.top + 18 * u), Size(frente.width, 1.2f * u))
        drawRoundRect(f(Color(0xFFB9C0BD)), Offset(w / 2 - 16 * u, frente.top + 7 * u), Size(32 * u, 4 * u), CornerRadius(2 * u))
        listOf(frente.left, frente.right - 8 * u).forEach { x -> drawRect(f(Color(0xFF4F5A56)), Offset(x, frente.top + 19 * u), Size(8 * u, frente.bottom - frente.top - 19 * u)) }

        // ---- La radio de onda corta, atrás a la izquierda.
        val ra = Rect(w * 0.035f, fondoY - 44 * u, w * 0.035f + maxOf(w * 0.27f, 92 * u), fondoY + 32 * u)
        // La antena, que sube en diagonal.
        drawLine(f(TUBO), Offset(ra.right - 10 * u, ra.top), Offset(ra.right + 26 * u, h * 0.02f), 1.4f * u)
        drawCircle(f(TUBO), 1.8f * u, Offset(ra.right + 26 * u, h * 0.02f))
        sombraSuave(Rect(ra.left, ra.bottom - 6 * u, ra.right, ra.bottom), 3 * u, 6 * u, 0.3f)
        drawRoundRect(c(Color(0xFF4A5A46)), ra.topLeft, ra.size, CornerRadius(4 * u))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(ra.left + 3 * u, ra.top + 1 * u), Size(ra.width - 6 * u, 1.2f * u))
        val pan = ra.deflate(5 * u)
        drawRect(c(Color(0xFF2B3329)), pan.topLeft, pan.size)
        listOf(pan.topLeft, Offset(pan.right, pan.top), Offset(pan.left, pan.bottom), Offset(pan.right, pan.bottom)).forEach { o -> drawCircle(c(TUBO), 1.2f * u, Offset(o.x + (if (o.x > pan.center.x) -3 else 3) * u, o.y + (if (o.y > pan.center.y) -3 else 3) * u)) }
        // La escala iluminada con su aguja.
        val esc = Rect(pan.left + 6 * u, pan.top + 6 * u, pan.right - 6 * u, pan.top + 20 * u)
        val ambar = Color(0xFFFFC46A)
        drawRect(if (oscuro) ambar.copy(alpha = 0.85f) else mezcla(ambar, Color(0xFFF0E6C8), 0.5f), esc.topLeft, esc.size)
        drawRect(ambar.copy(alpha = if (oscuro) 0.25f else 0.1f), esc.inflate(3 * u).topLeft, esc.inflate(3 * u).size)
        var sx = esc.left + 3 * u; var k = 0
        while (sx < esc.right - 2 * u) { drawLine(NEGRO.copy(alpha = 0.6f), Offset(sx, esc.top + 2 * u), Offset(sx, esc.top + (if (k % 4 == 0) 7 else 4) * u), 0.6f * u); sx += 3.5f * u; k++ }
        drawLine(ROJO, Offset(esc.left + esc.width * 0.62f, esc.top + 1 * u), Offset(esc.left + esc.width * 0.62f, esc.bottom - 1 * u), 1.2f * u)
        // El dial grande, la rejilla del altavoz y las perillas.
        val dial = Offset(pan.left + pan.width * 0.26f, pan.bottom - pan.height * 0.32f)
        drawCircle(c(TUBO_OSC), 12 * u, dial)
        drawCircle(c(Color(0xFF1A1F1A)), 9.5f * u, dial)
        for (j in 0 until 9) { val an = PI.toFloat() * (0.9f + 0.15f * j); drawLine(c(TUBO), Offset(dial.x + 7 * u * cos(an), dial.y + 7 * u * sin(an)), Offset(dial.x + 9 * u * cos(an), dial.y + 9 * u * sin(an)), 0.8f * u) }
        drawLine(FOSFORO, dial, Offset(dial.x + 6.5f * u * cos(-0.8f), dial.y + 6.5f * u * sin(-0.8f)), 1.2f * u)
        drawCircle(FOSFORO.copy(alpha = if (oscuro) 0.25f else 0.12f), 9.5f * u, dial)
        val rej = Rect(pan.left + pan.width * 0.5f, esc.bottom + 5 * u, pan.right - 6 * u, pan.bottom - 14 * u)
        var gy = rej.top + 2 * u
        while (gy < rej.bottom) { var gx = rej.left + 2 * u; while (gx < rej.right) { drawCircle(Color.Black.copy(alpha = 0.6f), 1 * u, Offset(gx, gy)); gx += 3.6f * u }; gy += 3.6f * u }
        listOf(0.55f, 0.72f, 0.89f).forEach { fx ->
            val p = Offset(pan.left + pan.width * fx, pan.bottom - 6 * u)
            drawCircle(c(Color(0xFF151815)), 4 * u, p); drawCircle(c(TUBO), 4 * u, p, style = Stroke(0.8f * u))
            drawLine(c(CREMA), p, Offset(p.x, p.y - 3 * u), 0.8f * u)
        }
        drawCircle(if (oscuro) Color(0xFFFF5040) else c(ROJO), 1.8f * u, Offset(pan.left + 6 * u, pan.bottom - 6 * u))
        // Las asas y el auricular colgado, con su cable rizado.
        listOf(ra.left - 3 * u, ra.right).forEach { x -> drawRoundRect(c(TUBO_OSC), Offset(x, ra.top + 10 * u), Size(3 * u, 22 * u), CornerRadius(1.5f * u)) }
        val au = Offset(ra.right + 6 * u, ra.top + 26 * u)
        drawRoundRect(c(NEGRO), Offset(au.x - 3 * u, au.y - 16 * u), Size(7 * u, 32 * u), CornerRadius(3 * u))
        drawOval(c(NEGRO), Offset(au.x - 5 * u, au.y - 20 * u), Size(11 * u, 9 * u))
        drawOval(c(NEGRO), Offset(au.x - 5 * u, au.y + 11 * u), Size(11 * u, 9 * u))
        var cy = au.y + 20 * u
        while (cy < ra.bottom + 4 * u) { drawOval(c(NEGRO), Offset(au.x - 3 * u, cy), Size(6 * u, 3 * u), style = Stroke(0.8f * u)); cy += 2 * u }

        // ---- El tazón de peltre, la lata de raciones y el lápiz, a la derecha.
        val ta = Offset(w * 0.88f, fondoY + 40 * u)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(ta.x - 13 * u, ta.y + 15 * u), Size(30 * u, 7 * u))
        drawRect(c(Color(0xFFE9E6DC)), Offset(ta.x - 12 * u, ta.y - 4 * u), Size(24 * u, 22 * u))
        drawOval(c(Color(0xFFE9E6DC)), Offset(ta.x - 12 * u, ta.y + 14 * u), Size(24 * u, 7 * u))
        drawArc(c(Color(0xFFE9E6DC)), -90f, 180f, false, Offset(ta.x + 6 * u, ta.y + 1 * u), Size(14 * u, 12 * u), style = Stroke(2.6f * u))
        drawOval(c(Color(0xFF2E4E7A)), Offset(ta.x - 12 * u, ta.y - 7 * u), Size(24 * u, 7 * u))
        drawOval(c(Color(0xFF3A2416)), Offset(ta.x - 10.5f * u, ta.y - 6 * u), Size(21 * u, 5 * u))
        drawCircle(c(NEGRO).copy(alpha = 0.7f), 2.4f * u, Offset(ta.x - 6 * u, ta.y + 8 * u))
        for (j in 0 until 2) drawPath(Path().apply { moveTo(ta.x - 3 * u + j * 6 * u, ta.y - 8 * u); cubicTo(ta.x - 9 * u + j * 6 * u, ta.y - 18 * u, ta.x + 3 * u + j * 6 * u, ta.y - 26 * u, ta.x - 2 * u + j * 6 * u, ta.y - 36 * u) },
            Color.White.copy(alpha = 0.2f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        val lt = Rect(w * 0.82f, fondoY + 2 * u, w * 0.82f + 22 * u, fondoY + 28 * u)
        drawRect(c(TUBO), lt.topLeft, lt.size)
        drawRect(c(Color(0xFF6E7F5E)), Offset(lt.left, lt.top + 6 * u), Size(lt.width, 14 * u))
        drawRect(c(AMARILLO), Offset(lt.left + 4 * u, lt.top + 10 * u), Size(lt.width - 8 * u, 6 * u))
        drawOval(c(Color(0xFFB8BEC2)), Offset(lt.left, lt.top - 3 * u), Size(lt.width, 6 * u))
        rotate(8f, Offset(w * 0.88f, frenteY - 12 * u)) {
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(w * 0.84f, frenteY - 10 * u), Size(40 * u, 3.4f * u))
            drawRect(c(AMARILLO), Offset(w * 0.84f, frenteY - 13 * u), Size(36 * u, 3.4f * u))
            drawPath(Path().apply { moveTo(w * 0.84f + 36 * u, frenteY - 13 * u); lineTo(w * 0.84f + 42 * u, frenteY - 11.3f * u); lineTo(w * 0.84f + 36 * u, frenteY - 9.6f * u); close() }, c(Color(0xFFE6CFA4)))
        }

        // ---- La carpeta del supervisor.
        val g = grosorLibreta(e.hojas, u)
        val ancho = w * 0.48f
        val x0 = w * 0.33f; val x1 = x0 + ancho
        val fy = frenteY - 12 * u
        val hondo = h * 0.22f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val carton = c(CARPETA)
        val angulo = giroTapa(a)
        sombraSuave(Rect(x0, fy - hondo, x1, fy), 3 * u, 9 * u, if (oscuro) 0.4f else 0.3f)
        drawRect(mezcla(carton, Color.Black, 0.3f), Offset(x0, fy - hondo), Size(ancho, hondo))
        drawRect(mezcla(carton, Color.Black, 0.4f), Offset(x0, fy - 1.6f * u), Size(ancho, 1.6f * u))
        // Los informes que asoman por delante, más cuanto más hojas.
        val asoman = if (e.hojas <= 0) 0 else (1 + e.hojas / 10).coerceAtMost(9)
        for (k in 0 until asoman) {
            val x = x0 + 6 * u + (ancho - 30 * u) * azar(e.semilla, 10 + k)
            val an = (14 + 22 * azar(e.semilla, 20 + k)) * u
            val sale = (1.5f + 3.5f * azar(e.semilla, 30 + k)) * u
            drawRect(if (k % 3 == 1) c(Color(0xFFF2E6A0)) else crema, Offset(x, tapaY), Size(an, g + sale - 1.6f * u))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(x, tapaY), Size(an, g + sale - 1.6f * u), style = Stroke(0.5f * u))
        }
        cantoHojas(Rect(x0 + 1.5f * u, tapaY + 1.6f * u, x1 - 1.5f * u, fy - 1.6f * u), mezcla(crema, Color.Black, 0.05f), NEGRO, e.semilla)
        // El informe de arriba, que se ve al levantar la tapa.
        if (a > 0.02f) {
            val r = Rect(x0 + 3 * u, arriba + 2 * u, x1 - 3 * u, tapaY + 1 * u)
            drawRect(crema, r.topLeft, r.size)
            textoEn(medidor, "LECTURAS", Rect(r.left + 6 * u, r.top + 3 * u, r.left + r.width * 0.34f, r.top + 13 * u), estarcido, NEGRO, 8.5f, minimo = 4.5f)
            drawLine(c(NARANJA), Offset(r.left + 6 * u, r.top + 14 * u), Offset(r.right - 6 * u, r.top + 14 * u), 1 * u)
            // La gráfica de las lecturas, en papel cuadriculado.
            val gr = Rect(r.left + 8 * u, r.top + 18 * u, r.left + r.width * 0.62f, r.bottom - 6 * u)
            var gx = gr.left; while (gx <= gr.right) { drawLine(c(PINTURA).copy(alpha = 0.3f), Offset(gx, gr.top), Offset(gx, gr.bottom), 0.5f * u); gx += 5 * u }
            var gyy = gr.top; while (gyy <= gr.bottom) { drawLine(c(PINTURA).copy(alpha = 0.3f), Offset(gr.left, gyy), Offset(gr.right, gyy), 0.5f * u); gyy += 5 * u }
            drawPath(Path().apply {
                moveTo(gr.left, gr.bottom - gr.height * 0.2f)
                for (j in 1..10) lineTo(gr.left + gr.width * j / 10f, gr.bottom - gr.height * (0.15f + 0.65f * azar(e.semilla, 200 + j)))
            }, c(ROJO), style = Stroke(1.2f * u, join = StrokeJoin.Round))
            // Las casillas marcadas.
            for (j in 0 until 4) {
                val y = r.top + 20 * u + j * 9 * u
                if (y > r.bottom - 6 * u) break
                val bx = r.left + r.width * 0.68f
                drawRect(NEGRO.copy(alpha = 0.7f), Offset(bx, y), Size(5 * u, 5 * u), style = Stroke(0.7f * u))
                if (azar(e.semilla, 220 + j) > 0.35f) drawPath(Path().apply { moveTo(bx + 1 * u, y + 2.5f * u); lineTo(bx + 2.3f * u, y + 4 * u); lineTo(bx + 5.5f * u, y - 0.5f * u) }, c(Color(0xFF2A3F7A)), style = Stroke(0.9f * u))
                drawLine(NEGRO.copy(alpha = 0.35f), Offset(bx + 8 * u, y + 2.5f * u), Offset(r.right - 8 * u - 6 * u * azar(e.semilla, 240 + j), y + 2.5f * u), 0.8f * u)
            }
        }
        // La tapa, que se levanta sobre el broche de arriba.
        tapaAlzada(arriba, tapaY - arriba, angulo, 1.5f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            drawRect(carton, r.topLeft, r.size)
            drawRect(mezcla(carton, Color.Black, 0.35f), Offset(r.left, r.bottom), Size(r.width, 1.6f * u))
            drawRect(Color.White.copy(alpha = 0.12f), r.topLeft, Size(r.width, 1 * u))
            // La pestaña naranja, delante a la derecha, para levantarla.
            drawRect(c(NARANJA), Offset(r.right - ancho * 0.26f, r.bottom - 1 * u), Size(ancho * 0.14f, 7 * u))
            // Las esquinas reforzadas.
            val es = 9 * u
            listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.right, r.bottom) to Offset(-1f, -1f), Offset(r.left, r.bottom) to Offset(1f, -1f)).forEach { (o, s) ->
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * es, o.y); lineTo(o.x, o.y + s.y * es); close() }, mezcla(carton, Color.Black, 0.45f))
            }
            // La franja de clasificación.
            drawRect(c(NARANJA), Offset(r.left, r.bottom - r.height * 0.17f), Size(r.width, 3 * u))
            // La etiqueta amarilla: el átomo, el nombre y la cuenta.
            val et = Rect(r.left + ancho * 0.1f, r.top + r.height * 0.24f, r.right - ancho * 0.1f, r.top + r.height * 0.74f)
            drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(et.left + 1.5f * u, et.top + 1.5f * u), et.size, CornerRadius(4 * u))
            drawRoundRect(papel(AMARILLO, oscuro, false), et.topLeft, et.size, CornerRadius(4 * u))
            drawRoundRect(NEGRO.copy(alpha = 0.6f), et.topLeft, et.size, CornerRadius(4 * u), style = Stroke(1.2f * u))
            val ra2 = minOf(et.height * 0.34f, 13 * u)
            atomoFosforo(Offset(et.left + ra2 + 6 * u, et.center.y), ra2, NEGRO.copy(alpha = 0.8f), c(NARANJA), Color.Transparent, false)
            val tx0 = et.left + ra2 * 2 + 10 * u
            val corte = et.top + et.height * 0.56f
            textoEn(medidor, e.nombre, Rect(tx0, et.top + 3 * u, et.right - 5 * u, corte), estarcido, NEGRO, 13f, minimo = 6f)
            drawLine(NEGRO.copy(alpha = 0.5f), Offset(tx0, corte), Offset(et.right - 6 * u, corte), 0.7f * u)
            textoEn(medidor, e.cuenta, Rect(tx0, corte + 1 * u, et.right - 5 * u, et.bottom - 3 * u), LetraExtra.vt323, NEGRO.copy(alpha = 0.85f), 11f, minimo = 6f)
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.45f * sombra), r.topLeft, Size(r.width, r.height + 8 * u))
        }, dentro = { sombra ->
            // Por dentro: el cartón con el bolsillo y la tarjeta perforada.
            val r = Rect(x0, arriba, x1, tapaY)
            drawRect(mezcla(carton, Color.Black, 0.15f), r.topLeft, r.size)
            drawRect(c(NARANJA), Offset(r.right - ancho * 0.26f, r.top - 6 * u), Size(ancho * 0.14f, 7 * u))
            val tar = Rect(r.left + ancho * 0.12f, r.top + r.height * 0.18f, r.left + ancho * 0.55f, r.bottom - r.height * 0.2f)
            drawRect(crema, tar.topLeft, tar.size)
            drawPath(Path().apply { moveTo(tar.left, tar.top + 5 * u); lineTo(tar.left + 5 * u, tar.top); lineTo(tar.left, tar.top); close() }, mezcla(carton, Color.Black, 0.15f))
            for (j in 0 until 18) {
                val x = tar.left + 5 * u + (tar.width - 10 * u) * (j / 17f)
                val y = tar.top + 5 * u + (tar.height - 12 * u) * azar(e.semilla, 500 + j)
                drawRect(NEGRO.copy(alpha = 0.75f), Offset(x, y), Size(1.4f * u, 3 * u))
            }
            val bol = Rect(r.left + 3 * u, r.top + r.height * 0.55f, r.right - 3 * u, r.bottom - 2 * u)
            drawPath(Path().apply { moveTo(bol.left, bol.top + 6 * u); lineTo(bol.right, bol.top); lineTo(bol.right, bol.bottom); lineTo(bol.left, bol.bottom); close() }, mezcla(carton, Color.Black, 0.05f))
            drawLine(Color.White.copy(alpha = 0.15f), Offset(bol.left, bol.top + 6 * u), Offset(bol.right, bol.top), 1 * u)
            textoEn(medidor, "SOLO SUPERVISIÓN", Rect(bol.left + ancho * 0.12f, bol.top + 6 * u, bol.right - 6 * u, bol.bottom - 2 * u), estarcido, c(AMARILLO), 9f, espaciado = 0.4f, minimo = 4.5f)
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size)
        })
        // El broche de metal de la bisagra, que no se mueve.
        val br = Rect(x0 + ancho * 0.36f, arriba - 6 * u, x0 + ancho * 0.64f, arriba + 9 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(br.left + 1 * u, br.top + 2 * u), br.size, CornerRadius(4 * u))
        drawRoundRect(c(TUBO), br.topLeft, br.size, CornerRadius(4 * u))
        drawRoundRect(c(Color(0xFFD5D8DC)), br.topLeft, Size(br.width, br.height * 0.45f), CornerRadius(4 * u))
        drawCircle(c(TUBO_OSC), 3 * u, Offset(br.center.x, br.center.y + 1 * u))
        listOf(br.left + 4 * u, br.right - 4 * u).forEach { x -> drawCircle(c(TUBO_OSC), 1.2f * u, Offset(x, br.center.y + 2 * u)) }
    }

    /**
     * La pestaña de un día: un trozo de tarjeta perforada de las máquinas del refugio, con la esquina cortada, los números de
     * las columnas apenas impresos y unas perforaciones; el día grande, la letra de la semana y, arriba, la franja de color
     * del ánimo (como las tarjetas marcadas por colores).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val crema = papel(CREMA, oscuro, false)
        val r = Rect(2.5f * u, 2.5f * u, w - 3.5f * u, h - 3.5f * u)
        val tarjeta = Path().apply { moveTo(r.left + 7 * u, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.top + 7 * u); close() }
        translate(1.2f * u, 1.6f * u) { drawPath(tarjeta, Color.Black.copy(alpha = 0.3f)) }
        drawPath(tarjeta, crema)
        if (e.tono != null) clipPath(tarjeta) { drawRect(e.tono, Offset(r.left, r.top), Size(r.width, 7 * u)) }
        // Los números de las columnas y las perforaciones.
        for (fila in 0 until 2) {
            val y = r.bottom - (13 - fila * 5) * u
            for (col in 0 until 6) drawRect(c2(PINTURA, oscuro).copy(alpha = 0.2f), Offset(r.left + (3 + col * 6f) * u, y), Size(2 * u, 3 * u))
        }
        for (j in 0 until 3) {
            val col = (azar(e.semilla, 10 + j) * 6).toInt(); val fila = (azar(e.semilla, 20 + j) * 2).toInt()
            drawRect(Color.Black.copy(alpha = 0.75f), Offset(r.left + (3 + col * 6f) * u, r.bottom - (13 - fila * 5) * u), Size(2 * u, 3 * u))
        }
        textoEn(medidor, e.semana, Rect(r.right - 11 * u, r.top + 7.5f * u, r.right - 1 * u, r.top + 16 * u), LetraExtra.vt323, NEGRO.copy(alpha = 0.8f), 10f, minimo = 6f)
        textoEn(medidor, e.dia, Rect(r.left, r.top + 12 * u, r.right, r.bottom - 14 * u), LetraExtra.vt323, NEGRO, 26f, minimo = 10f)
        drawRect(c2(PINTURA, oscuro), Offset(r.left, r.bottom - 2 * u), Size(r.width, 2 * u))
        if (e.tono == null) drawLine(NEGRO.copy(alpha = 0.3f), Offset(r.left + 8 * u, r.top + 3.5f * u), Offset(r.right - 3 * u, r.top + 3.5f * u), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u)))
    }

    private fun c2(x: Color, oscuro: Boolean) = t(x, oscuro, false)

    /**
     * El rótulo de un mes: una placa de chapa pintada de verde azulado, remachada, con franjas de peligro amarillas y negras en
     * las puntas y el mes estarcido en crema.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val placa = Rect(0f, 3 * u, w, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(placa.left, placa.top + 2 * u), placa.size)
        drawRect(Brush.verticalGradient(listOf(c2(PINTURA_LUZ, oscuro), c2(PINTURA, oscuro), c2(PINTURA_OSC, oscuro)), placa.top, placa.bottom), placa.topLeft, placa.size)
        val franja = 30 * u
        listOf(0f, w - franja).forEach { x0 ->
            clipRect(x0, placa.top, x0 + franja, placa.bottom) {
                drawRect(c2(AMARILLO, oscuro), Offset(x0, placa.top), Size(franja, placa.height))
                var x = x0 - placa.height
                while (x < x0 + franja) {
                    drawPath(Path().apply { moveTo(x, placa.bottom); lineTo(x + 6 * u, placa.bottom); lineTo(x + 6 * u + placa.height, placa.top); lineTo(x + placa.height, placa.top); close() }, c2(NEGRO, oscuro))
                    x += 12 * u
                }
            }
        }
        listOf(franja + 7 * u, w - franja - 7 * u).forEach { x ->
            drawCircle(c2(PINTURA_OSC, oscuro), 2.2f * u, Offset(x, h / 2))
            drawCircle(Color.White.copy(alpha = 0.3f), 0.8f * u, Offset(x - 0.6f * u, h / 2 - 0.6f * u))
        }
        val r = Rect(franja + 14 * u, placa.top, w - franja - 14 * u, placa.bottom)
        textoEn(medidor, texto.uppercase(), r, LetraExtra.estarcido, papel(CREMA, oscuro, false), 15f, espaciado = 1.5f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** La cinta de lona naranja con que se ata la caja de las cartas y el forro de goma de su tapa. */
    private val LONA = Color(0xFFD9572B)
    private val GOMA = Color(0xFF2B2F2C)

    /**
     * La caja de cartas del residente: una cajita de chapa pintada de verde azulado, de esquinas redondas, con la tapa de
     * bisagra de rodillo arriba, el emblema de la bóveda (el aro dentado de su puerta) estampado en relieve, la ventanilla del
     * portaetiquetas con sus dos tornillos y la tarjeta crema donde va, a máquina, a quién va y las fechas, y la franja de
     * aviso amarilla y negra al pie. El sello es la calcomanía del optimismo atómico (el sol dentado amarillo con el átomo y
     * su núcleo naranja), pegada sobre la junta de la tapa: brilla en ámbar cuando la carta ya se puede abrir y se parte al
     * levantar la tapa. Las de al futuro van además ceñidas con la cinta de lona naranja y su hebilla. Dentro, la carta es una
     * hoja de impresora continua, con sus agujeros de arrastre a los lados, impresa en fósforo verde.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 3 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val junta = bis + h * 0.3f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val radio = 7 * u * k
        // La calcomanía atómica sobre la junta.
        val sc = Offset(s.left + w * 0.5f, junta)
        val sr = h * 0.12f
        fun calcomania(alfa: Float) {
            fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
            drawPath(estrella(sc + Offset(0.6f * u, 1.2f * u), sr, 18, 0.82f), a(Color.Black.copy(alpha = 0.3f)))
            drawPath(estrella(sc, sr, 18, 0.82f), a(mezcla(c(AMARILLO), Color(0xFFFFE27A), e.brillo * 0.7f)))
            drawCircle(a(CREMA), sr * 0.62f, sc)
            listOf(0f, 60f, 120f).forEach { g -> rotate(g, sc) { drawOval(a(NEGRO), Offset(sc.x - sr * 0.52f, sc.y - sr * 0.18f), Size(sr * 1.04f, sr * 0.36f), style = Stroke(0.9f * u * k)) } }
            drawCircle(a(c(Color(0xFFD9572B))), sr * 0.13f, sc)
        }

        drawRoundRect(Color.Black.copy(alpha = 0.32f), Offset(s.left + 2 * u, s.top + 3.5f * u), s.size, CornerRadius(radio))
        // ---- El fondo de la caja (lo que se ve con la tapa abierta).
        drawRoundRect(c(INTERIOR), s.topLeft, s.size, CornerRadius(radio))
        // ---- La tapa, que gira sobre la bisagra de rodillo.
        fun DrawScope.tapa(adentro: Boolean) {
            val r = Rect(s.left, bis, s.right, junta)
            if (adentro) {
                drawRoundRect(c(PINTURA_OSC), r.topLeft, r.size, CornerRadius(radio))
                drawRoundRect(c(GOMA), Offset(r.left + w * 0.05f, r.top + r.height * 0.18f), Size(r.width * 0.9f, r.height * 0.64f), CornerRadius(3 * u))
                return
            }
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(r.left + 2 * u, r.bottom), Size(r.width - 4 * u, 2 * u))
            drawRoundRect(c(PINTURA_LUZ), r.topLeft, r.size, CornerRadius(radio))
            drawRect(c(PINTURA_LUZ), Offset(r.left, r.bottom - radio), Size(r.width, radio))
            drawRect(Color.White.copy(alpha = 0.18f), Offset(r.left + radio, r.top + 2.5f * u), Size(r.width - 2 * radio, 1.2f * u))
            drawLine(c(PINTURA_OSC), Offset(r.left, r.bottom - 1 * u), Offset(r.right, r.bottom - 1 * u), 1.4f * u)
            // El rodillo de la bisagra y la pestaña para levantarla.
            drawRect(c(TUBO), Offset(r.left + w * 0.12f, r.top - 1.5f * u), Size(w * 0.76f, 3.5f * u))
            for (i in 0..6) drawLine(c(TUBO_OSC), Offset(r.left + w * (0.12f + 0.76f * i / 6f), r.top - 1.5f * u), Offset(r.left + w * (0.12f + 0.76f * i / 6f), r.top + 2 * u), 0.8f * u)
            drawRoundRect(c(PINTURA_OSC), Offset(r.center.x - w * 0.36f, r.bottom - 3 * u), Size(w * 0.12f, 4.5f * u), CornerRadius(2 * u))
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2.5f * u * e.romper); rotate(-8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, true, 6, e.semilla)) { calcomania(1f) }
            }
        }
        if (!cerrada) solapaCarta(bis, ab) { tapa(it) }

        // ---- La carta: hoja de impresora continua, con los agujeros de arrastre.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.06f
            val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
            val hoja = Rect(s.left + w * 0.07f, arriba0 - alza, s.right - w * 0.07f, arriba0 - alza + h * 0.88f)
            drawRect(Color.Black.copy(alpha = 0.35f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
            drawRect(e.hoja, hoja.topLeft, hoja.size)
            val orilla = 6 * u
            listOf(hoja.left, hoja.right - orilla).forEach { x ->
                drawRect(mezcla(e.hoja, FOSFORO, 0.08f), Offset(x, hoja.top), Size(orilla, hoja.height))
                var yy = hoja.top + 4 * u
                while (yy < hoja.bottom) { drawCircle(Color.Black.copy(alpha = 0.6f), 1.3f * u, Offset(x + orilla / 2, yy)); yy += 6 * u }
            }
            drawLine(FOSFORO.copy(alpha = 0.35f), Offset(hoja.left + orilla, hoja.top), Offset(hoja.left + orilla, hoja.bottom), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
            drawLine(FOSFORO.copy(alpha = 0.35f), Offset(hoja.right - orilla, hoja.top), Offset(hoja.right - orilla, hoja.bottom), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
            var y = hoja.top + 14 * u
            while (y < hoja.bottom - 3 * u) { drawLine(e.renglones, Offset(hoja.left + orilla + 3 * u, y), Offset(hoja.right - orilla - 3 * u, y), 0.7f * u); y += 11 * u }
        }

        // ---- El cuerpo de la caja.
        val cuerpo = Rect(s.left, junta, s.right, s.bottom)
        drawRoundRect(c(PINTURA), cuerpo.topLeft, cuerpo.size, CornerRadius(radio))
        drawRect(c(PINTURA), cuerpo.topLeft, Size(cuerpo.width, radio))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(cuerpo.left + 1.5f * u, cuerpo.top + 2 * u), Size(1.4f * u, cuerpo.height - radio - 2 * u))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(cuerpo.right - 3 * u, cuerpo.top + 2 * u), Size(2 * u, cuerpo.height - radio))
        if (!cerrada) {
            drawRect(c(INTERIOR), Offset(cuerpo.left + 2 * u, cuerpo.top), Size(cuerpo.width - 4 * u, 2.2f * u))
            drawRect(c(GOMA), Offset(cuerpo.left + 2 * u, cuerpo.top), Size(cuerpo.width - 4 * u, 1 * u))
        }
        // Los arañazos de la pintura, que dejan ver el metal.
        for (i in 0 until 6) {
            val o = Offset(cuerpo.left + w * azar(e.semilla, i + 3), cuerpo.top + cuerpo.height * azar(e.semilla, i + 13))
            drawLine(c(TUBO).copy(alpha = 0.5f), o, o + Offset((3 + 5 * azar(e.semilla, i + 23)) * u, (1 - 2 * azar(e.semilla, i + 33)) * u), 0.6f * u)
        }
        // La franja de aviso al pie.
        val franja = Rect(cuerpo.left + radio * 0.6f, cuerpo.bottom - h * 0.1f, cuerpo.right - radio * 0.6f, cuerpo.bottom - h * 0.035f)
        clipRect(franja.left, franja.top, franja.right, franja.bottom) {
            drawRect(c(AMARILLO), franja.topLeft, franja.size)
            var x = franja.left - franja.height
            while (x < franja.right) { drawPath(Path().apply { moveTo(x, franja.bottom); lineTo(x + franja.height, franja.top); lineTo(x + franja.height * 1.8f, franja.top); lineTo(x + franja.height * 0.8f, franja.bottom); close() }, c(NEGRO)); x += maxOf(franja.height * 1.8f, 1f) }
        }
        // El portaetiquetas: el marco de chapa, los dos tornillos y la tarjeta crema mecanografiada.
        val marco = Rect(cuerpo.left + w * 0.06f, cuerpo.top + h * 0.1f, cuerpo.right - w * 0.17f, franja.top - h * 0.04f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), marco.topLeft + Offset(0.6f * u, 1.2f * u), marco.size, CornerRadius(2.5f * u))
        drawRoundRect(c(TUBO), marco.topLeft, marco.size, CornerRadius(2.5f * u))
        drawRoundRect(c(Color(0xFFC3C9CB)), marco.topLeft, Size(marco.width, 1.2f * u), CornerRadius(1 * u))
        val tj = Rect(marco.left + 3 * u, marco.top + 3 * u, marco.right - 3 * u, marco.bottom - 3 * u)
        drawRect(papel(CREMA, oscuro, false), tj.topLeft, tj.size)
        drawRect(c(PINTURA).copy(alpha = 0.6f), tj.topLeft, Size(tj.width, 2 * u))
        listOf(marco.left + 1.6f * u, marco.right - 1.6f * u).forEach { x ->
            val o = Offset(x, marco.center.y)
            drawCircle(c(TUBO_OSC), 1.5f * u, o); drawLine(c(Color(0xFF2E3436)), o - Offset(1 * u, 0f), o + Offset(1 * u, 0f), 0.6f * u)
        }
        textoEn(medidor, e.linea1, Rect(tj.left + 2 * u, tj.top + 2 * u, tj.right - 2 * u, tj.top + tj.height * 0.58f), letra, NEGRO, 12.5f, peso = FontWeight.Bold, minimo = 7f)
        drawLine(NEGRO.copy(alpha = 0.18f), Offset(tj.left + 4 * u, tj.top + tj.height * 0.58f), Offset(tj.right - 4 * u, tj.top + tj.height * 0.58f), 0.6f * u)
        textoEn(medidor, e.linea2, Rect(tj.left + 2 * u, tj.top + tj.height * 0.6f, tj.right - 2 * u, tj.bottom - 1 * u), letra, NEGRO.copy(alpha = 0.85f), 10f, minimo = 6f)
        // El emblema de la bóveda, estampado en relieve a la derecha.
        val em = Offset(cuerpo.right - w * 0.085f, (marco.top + marco.bottom) / 2)
        val er = minOf(w * 0.055f, marco.height * 0.36f)
        drawCircle(Color.Black.copy(alpha = 0.2f), er, em + Offset(0.5f * u, 0.8f * u))
        drawPath(estrella(em, er, 12, 0.86f), c(PINTURA_LUZ))
        drawCircle(c(PINTURA_OSC), er * 0.6f, em, style = Stroke(1 * u))
        for (i in 0 until 3) rotate(i * 60f, em) { drawLine(c(PINTURA_OSC), Offset(em.x - er * 0.5f, em.y), Offset(em.x + er * 0.5f, em.y), 0.8f * u) }

        // ---- La tapa cerrada encima.
        if (cerrada) solapaCarta(bis, ab) { tapa(it) }
        // ---- La cinta de lona: ciñe la caja por la derecha, con su hebilla.
        if (e.cordon > 0f && cerrada) {
            val cx = s.right - w * 0.085f
            val cw = 8 * u * k
            val largo = (s.height + 4 * u) * e.cordon
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(cx - cw / 2 + 1 * u, s.top - 2 * u + 1.5f * u), Size(cw, largo))
            drawRect(c(LONA), Offset(cx - cw / 2, s.top - 2 * u), Size(cw, largo))
            var yy = s.top
            while (yy < s.top - 2 * u + largo) { drawLine(c(Color(0xFFA03E1C)), Offset(cx - cw / 2, yy), Offset(cx + cw / 2, yy), 0.5f * u); yy += 2.2f * u }
            drawLine(c(Color(0xFFF08A5A)), Offset(cx - cw / 2 + 1 * u, s.top - 2 * u), Offset(cx - cw / 2 + 1 * u, s.top - 2 * u + largo), 0.6f * u)
            if (e.cordon > 0.9f) {
                val hb = Offset(cx, junta + h * 0.3f)
                drawRect(Color.Black.copy(alpha = 0.35f), Offset(hb.x - cw * 0.75f + 0.8f * u, hb.y - cw * 0.55f + 1.2f * u), Size(cw * 1.5f, cw * 1.1f))
                drawRect(c(Color(0xFFC3C9CB)), Offset(hb.x - cw * 0.75f, hb.y - cw * 0.55f), Size(cw * 1.5f, cw * 1.1f), style = Stroke(1.6f * u))
                drawLine(c(TUBO_OSC), Offset(hb.x, hb.y - cw * 0.55f), Offset(hb.x, hb.y + cw * 0.55f), 1.2f * u)
            }
        }
        // ---- La calcomanía: entera (cayendo al sellar) o partida en la junta.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(AMARILLO.copy(alpha = 0.7f * e.brillo), Color(0xFFFF8A3A).copy(alpha = 0.2f * e.brillo), Color.Transparent),
                center = sc, radius = sr * 3f), sr * 3f, sc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc) }) { calcomania(e.alfaSello.coerceIn(0f, 1f)) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2f * u * e.romper); rotate(7f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, false, 6, e.semilla)) { calcomania(1f) }
            }
        }
    }

    // La hoja continua que asoma de la caja, entre sus dos tiras de agujeros.
    override val cajaPrevia = Rect(0.15f, 0.17f, 0.85f, 0.33f)

    override val altoBuzon: Dp = 210.dp

    /**
     * El correo neumático del refugio, en el muro de hormigón crema con su zócalo verde azulado: por arriba entra el tubo de
     * acero que baja hasta la estación receptora (la campana de vidrio donde cae la cápsula, la palanca roja y la luz verde de
     * «correo», que se enciende cuando hay cartas listas). A la derecha, el portacápsulas de pared con tres rieles: arriba las
     * cápsulas listas, con sus tapas de latón brillando; en medio las selladas, con la cinta naranja; abajo las abiertas, sin
     * tapa y con la hoja asomando. Cada riel tiene su pantallita de fósforo verde con la cuenta; caben unas seis cápsulas por
     * riel y la pantalla dice cuántas hay de verdad.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF050807), 0.4f) else c(x)
        // ---- El muro y el zócalo.
        drawRect(f(MURO))
        for (i in 0 until 5) drawLine(f(mezcla(MURO, NEGRO, 0.12f)), Offset(0f, h * i / 5f), Offset(w, h * i / 5f), 0.8f * u)
        val zoc = h - 26 * u
        drawRect(f(PINTURA), Offset(0f, zoc), Size(w, h - zoc))
        drawRect(f(PINTURA_OSC), Offset(0f, zoc), Size(w, 2 * u))
        drawRect(f(PISO), Offset(0f, h - 6 * u), Size(w, 6 * u))
        // ---- El tubo que baja del techo hasta la estación.
        val tx = w * 0.17f
        val tw = 18 * u
        drawRect(c(TUBO_OSC), Offset(tx - tw / 2 + 3 * u, 0f), Size(tw, 60 * u))
        drawRect(c(TUBO), Offset(tx - tw / 2, 0f), Size(tw, 60 * u))
        drawRect(Color.White.copy(alpha = 0.25f), Offset(tx - tw / 2 + 3 * u, 0f), Size(2.5f * u, 60 * u))
        listOf(14 * u, 44 * u).forEach { y ->
            drawRect(c(TUBO_OSC), Offset(tx - tw / 2 - 3 * u, y), Size(tw + 6 * u, 6 * u))
            drawCircle(c(TUBO), 1.2f * u, Offset(tx - tw / 2, y + 3 * u)); drawCircle(c(TUBO), 1.2f * u, Offset(tx + tw / 2, y + 3 * u))
        }
        // ---- La estación receptora.
        val est = Rect(tx - 44 * u, 58 * u, tx + 44 * u, zoc + 10 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), est.topLeft + Offset(3 * u, 4 * u), est.size, CornerRadius(6 * u))
        drawRoundRect(c(PINTURA), est.topLeft, est.size, CornerRadius(6 * u))
        drawRoundRect(c(PINTURA_LUZ), est.topLeft, Size(est.width, 3 * u), CornerRadius(3 * u))
        drawRoundRect(c(PINTURA_OSC), est.topLeft, est.size, CornerRadius(6 * u), style = Stroke(1.2f * u))
        // La campana de vidrio, con una cápsula dentro si hay cartas listas.
        val campana = Rect(tx - 16 * u, est.top + 10 * u, tx + 16 * u, est.top + 76 * u)
        drawRoundRect(c(INTERIOR), campana.topLeft, campana.size, CornerRadius(14 * u))
        val listas = e.listas > 0
        if (listas) {
            drawCircle(Brush.radialGradient(listOf(FOSFORO.copy(alpha = 0.35f), Color.Transparent), center = campana.center, radius = 40 * u), 40 * u, campana.center)
            capsula(Rect(campana.left + 6 * u, campana.top + 16 * u, campana.right - 6 * u, campana.bottom - 6 * u), 0, true, oscuro, vertical = true)
        }
        drawRoundRect(Color.White.copy(alpha = 0.12f), Offset(campana.left + 3 * u, campana.top + 6 * u), Size(4 * u, campana.height - 14 * u), CornerRadius(2 * u))
        drawRoundRect(c(TUBO), campana.topLeft, campana.size, CornerRadius(14 * u), style = Stroke(3 * u))
        // La luz de «correo» y la palanca.
        val luz = Offset(est.left + 12 * u, est.top + 16 * u)
        drawCircle(c(TUBO_OSC), 6 * u, luz)
        drawCircle(if (listas) FOSFORO else c(Color(0xFF2F4A34)), 4 * u, luz)
        if (listas) drawCircle(FOSFORO.copy(alpha = 0.3f), 9 * u, luz)
        val pal = Offset(est.right - 12 * u, est.top + 50 * u)
        drawRect(c(TUBO_OSC), Offset(pal.x - 4 * u, pal.y - 3 * u), Size(8 * u, 10 * u))
        drawLine(c(TUBO), pal, pal + Offset(4 * u, -26 * u), 2.5f * u, StrokeCap.Round)
        drawCircle(c(ROJO), 4.5f * u, pal + Offset(4 * u, -27 * u))
        drawCircle(Color.White.copy(alpha = 0.35f), 1.5f * u, pal + Offset(3 * u, -28.5f * u))
        // El rótulo de la estación: una pantallita de fósforo.
        val rot = Rect(est.left + 8 * u, campana.bottom + 6 * u, est.right - 8 * u, campana.bottom + 22 * u)
        drawRect(c(NEGRO), rot.topLeft, rot.size)
        textoEn(medidor, if (listas) "¡CORREO!" else "SIN CORREO", rot.deflate(2 * u), letra, if (listas) FOSFORO else FOSFORO.copy(alpha = 0.45f), 12f)

        // ---- El portacápsulas: tres rieles.
        val por = Rect(w * 0.36f, 12 * u, w - 10 * u, zoc - 4 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), por.topLeft + Offset(3 * u, 4 * u), por.size)
        drawRect(c(PINTURA_OSC), por.topLeft, por.size)
        drawRect(c(PINTURA), por.topLeft, por.size, style = Stroke(3 * u))
        val rieles = listOf(Triple("LISTAS", e.listas, 0), Triple("SELLADAS", e.selladas, 1), Triple("ABIERTAS", e.abiertas, 2))
        val alto = (por.height - 12 * u) / 3f
        rieles.forEachIndexed { i, (nombre, n, tipo) ->
            val fila = Rect(por.left + 6 * u, por.top + 6 * u + i * alto, por.right - 6 * u, por.top + 6 * u + (i + 1) * alto)
            // La pantallita con la cuenta.
            val pan = Rect(fila.left, fila.top + 3 * u, fila.left + 52 * u, fila.bottom - 5 * u)
            drawRoundRect(c(TUBO), pan.inflate(2 * u).topLeft, pan.inflate(2 * u).size, CornerRadius(2 * u))
            drawRect(Color(0xFF031A0B), pan.topLeft, pan.size)
            textoEn(medidor, nombre, Rect(pan.left, pan.top + 1 * u, pan.right, pan.top + pan.height * 0.42f), letra, FOSFORO.copy(alpha = 0.8f), 9f)
            textoEn(medidor, n.toString().padStart(2, '0'), Rect(pan.left, pan.top + pan.height * 0.36f, pan.right, pan.bottom), letra, FOSFORO, 17f)
            // El riel y sus cápsulas tumbadas.
            val riel = fila.bottom - 4 * u
            drawRect(c(TUBO), Offset(pan.right + 6 * u, riel), Size(fila.right - pan.right - 6 * u, 3 * u))
            drawRect(Color.White.copy(alpha = 0.3f), Offset(pan.right + 6 * u, riel), Size(fila.right - pan.right - 6 * u, 0.8f * u))
            val cap = 22 * u
            val disp = ((fila.right - pan.right - 10 * u) / cap).toInt()
            val vis = n.coerceAtMost(disp)
            for (j in 0 until vis) {
                val x = pan.right + 8 * u + j * cap
                capsula(Rect(x, fila.top + 5 * u, x + cap - 4 * u, riel), tipo, tipo == 0, oscuro)
            }
            if (n == 0) for (j in 0 until 3) drawRoundRect(Color.Black.copy(alpha = 0.18f), Offset(pan.right + 10 * u + j * cap, riel - 4 * u), Size(cap - 8 * u, 3 * u), CornerRadius(1.5f * u))
        }
    }

    /**
     * Una cápsula del correo neumático dentro de [r]: el cuerpo de acero con sus anillos de fieltro y las dos tapas de latón;
     * con [vertical], la que llega a la campana (larga); si no, de pie sobre el riel. [tipo]: 0 lista (las tapas brillan), 1 sellada (la cinta naranja), 2 abierta (sin
     * tapa y con la hoja asomando).
     */
    private fun DrawScope.capsula(r: Rect, tipo: Int, brilla: Boolean, oscuro: Boolean, vertical: Boolean = false) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val laton = if (brilla) Color(0xFFFFD36A) else c(COBRE)
        if (vertical) {
            drawRoundRect(c(TUBO), r.topLeft, r.size, CornerRadius(r.width * 0.4f))
            drawRect(Color.White.copy(alpha = 0.3f), Offset(r.left + r.width * 0.2f, r.top + 4 * u), Size(r.width * 0.12f, r.height - 8 * u))
            drawRoundRect(laton, r.topLeft, Size(r.width, r.height * 0.18f), CornerRadius(r.width * 0.4f))
            drawRoundRect(laton, Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f), CornerRadius(r.width * 0.4f))
            listOf(0.3f, 0.7f).forEach { f -> drawRect(c(CINTA), Offset(r.left, r.top + r.height * f), Size(r.width, 2 * u)) }
            return
        }
        // De pie sobre el riel.
        val cuerpoAlto = r.height.coerceAtMost(r.width * 1.8f)
        val cr = Rect(r.left + r.width * 0.1f, r.bottom - cuerpoAlto, r.right - r.width * 0.1f, r.bottom)
        if (tipo == 2) {
            // La hoja enrollada que asoma por la boca de arriba.
            drawRoundRect(mezcla(CREMA, Color.Black, if (oscuro) 0.25f else 0f), Offset(cr.center.x - cr.width * 0.28f, cr.top - 6 * u), Size(cr.width * 0.56f, 14 * u), CornerRadius(2 * u))
            drawLine(NEGRO.copy(alpha = 0.3f), Offset(cr.center.x - cr.width * 0.15f, cr.top - 3 * u), Offset(cr.center.x + cr.width * 0.15f, cr.top - 3 * u), 0.6f * u)
        }
        if (brilla) drawRoundRect(Color(0xFFFFE27A).copy(alpha = 0.3f), cr.inflate(3 * u).topLeft, cr.inflate(3 * u).size, CornerRadius(cr.width * 0.5f))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), cr.topLeft + Offset(1 * u, 1 * u), cr.size, CornerRadius(cr.width * 0.45f))
        drawRoundRect(c(TUBO), cr.topLeft, cr.size, CornerRadius(cr.width * 0.45f))
        drawRect(Color.White.copy(alpha = 0.28f), Offset(cr.left + cr.width * 0.2f, cr.top + 3 * u), Size(cr.width * 0.14f, cr.height - 6 * u))
        if (tipo != 2) drawRoundRect(laton, cr.topLeft, Size(cr.width, cr.height * 0.2f), CornerRadius(cr.width * 0.45f))
        else drawOval(c(INTERIOR), Offset(cr.left + 1 * u, cr.top), Size(cr.width - 2 * u, cr.height * 0.12f))
        drawRoundRect(laton, Offset(cr.left, cr.bottom - cr.height * 0.18f), Size(cr.width, cr.height * 0.18f), CornerRadius(cr.width * 0.45f))
        drawRect(c(CINTA), Offset(cr.left, cr.top + cr.height * 0.32f), Size(cr.width, 1.6f * u))
        drawRect(c(CINTA), Offset(cr.left, cr.top + cr.height * 0.68f), Size(cr.width, 1.6f * u))
        if (tipo == 1) drawRect(c(Color(0xFFD9572B)), Offset(cr.left, cr.top + cr.height * 0.44f), Size(cr.width, cr.height * 0.14f))
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /** Un remache de latón con su brillo. */
    private fun DrawScope.remache(o: Offset, r: Float, oscuro: Boolean, noche: Boolean) {
        drawCircle(placa(LATON, oscuro, noche), r, o)
        drawCircle(Color.Black.copy(alpha = 0.35f), r, o, style = Stroke(r * 0.3f))
        drawCircle(placa(LATON_LUZ, oscuro, noche), r * 0.35f, Offset(o.x - r * 0.25f, o.y - r * 0.25f))
    }

    /**
     * La cabecera del parte de la orden: a la izquierda, la hornacina apuntada de acero con el escudo carmesí, el engrane y la
     * espada en latón (de noche, el escudo lleva la luna); a la derecha, la placa de acero con su filo de latón y cuatro
     * remaches, el rótulo de la orden estarcido en latón, la fecha grande y el detalle. De noche, el resplandor de las brasas
     * de la forja sube desde abajo.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val laton = placa(LATON, oscuro, e.noche)
        // La hornacina apuntada con el escudo.
        val nicho = Rect(2 * u, 3 * u, 60 * u, h - 3 * u)
        val arco = arcoApuntado(nicho, 30 * u)
        drawPath(arco, t(ACERO_OSC, oscuro, e.noche))
        drawPath(arco, laton, style = Stroke(1.6f * u))
        drawPath(arcoApuntado(nicho.deflate(4 * u), 26 * u), laton.copy(alpha = 0.4f), style = Stroke(0.7f * u))
        val esc = Rect(nicho.left + 11 * u, nicho.top + 34 * u, nicho.right - 11 * u, nicho.bottom - 14 * u)
        // La espada detrás del escudo.
        drawLine(placa(LATON_LUZ, oscuro, e.noche), Offset(esc.center.x, esc.top - 22 * u), Offset(esc.center.x, esc.bottom + 9 * u), 2 * u)
        drawLine(laton, Offset(esc.center.x - 10 * u, esc.top - 10 * u), Offset(esc.center.x + 10 * u, esc.top - 10 * u), 2.2f * u, StrokeCap.Round)
        drawCircle(laton, 2.2f * u, Offset(esc.center.x, esc.top - 23 * u))
        drawPath(escudo(esc), placa(CARMESI, oscuro, e.noche))
        drawPath(escudo(esc), laton, style = Stroke(1.4f * u))
        val ce = Offset(esc.center.x, esc.top + esc.height * 0.42f)
        val re = esc.width * 0.3f
        if (!e.noche) {
            drawPath(engrane(ce, re, 8), laton)
            drawCircle(placa(CARMESI, oscuro, e.noche), re * 0.4f, ce)
        } else {
            drawCircle(laton, re * 0.85f, ce)
            drawCircle(placa(CARMESI, oscuro, e.noche), re * 0.72f, Offset(ce.x + re * 0.38f, ce.y - re * 0.22f))
            drawPath(estrella(Offset(ce.x + re * 0.75f, ce.y + re * 0.75f), re * 0.3f, 4, 0.4f), laton)
        }
        // La placa de acero.
        val pl = Rect(66 * u, 8 * u, w - 2 * u, h - 8 * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(pl.left + 2 * u, pl.top + 3 * u), pl.size)
        drawRect(Brush.verticalGradient(listOf(t(ACERO_LUZ, oscuro, e.noche), t(ACERO, oscuro, e.noche), t(HIERRO, oscuro, e.noche)), pl.top, pl.bottom), pl.topLeft, pl.size)
        drawRect(laton, pl.topLeft, pl.size, style = Stroke(1.8f * u))
        drawRect(laton.copy(alpha = 0.45f), pl.deflate(4 * u).topLeft, pl.deflate(4 * u).size, style = Stroke(0.7f * u))
        listOf(Offset(pl.left + 8 * u, pl.top + 8 * u), Offset(pl.right - 8 * u, pl.top + 8 * u), Offset(pl.left + 8 * u, pl.bottom - 8 * u), Offset(pl.right - 8 * u, pl.bottom - 8 * u))
            .forEach { remache(it, 2.6f * u, oscuro, e.noche) }
        val txt = Rect(pl.left + 15 * u, pl.top + 5 * u, pl.right - 15 * u, pl.bottom - 5 * u)
        textoEn(medidor, e.rotulo, Rect(txt.left, txt.top, txt.right, txt.top + 18 * u), LetraExtra.estarcido, p.acento, 10.5f, espaciado = 0.4f, minimo = 6f)
        drawLine(laton.copy(alpha = 0.7f), Offset(txt.left + 10 * u, txt.top + 20 * u), Offset(txt.right - 10 * u, txt.top + 20 * u), 0.9f * u)
        drawPath(Path().apply {
            val c = Offset(txt.center.x, txt.top + 20 * u)
            moveTo(c.x - 4 * u, c.y); lineTo(c.x, c.y - 3 * u); lineTo(c.x + 4 * u, c.y); lineTo(c.x, c.y + 3 * u); close()
        }, laton)
        fechaFuturo(medidor, e.fecha, Rect(txt.left, txt.top + 23 * u, txt.right, txt.top + 66 * u), LetraExtra.estarcido, p.tinta, 21f, espaciado = 0.6f)
        fechaFuturo(medidor, e.detalle, Rect(txt.left, txt.top + 67 * u, txt.right, txt.bottom), LetraExtra.estarcido, p.tenue, 10f, k = 0.86f)
        // De noche, las brasas: un resplandor plano que sube desde abajo y unas chispas.
        if (e.noche) {
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFFE8743A).copy(alpha = 0.16f)), h * 0.5f, h), Offset(0f, h * 0.5f), Size(w, h * 0.5f))
            for (k in 0 until 9) drawCircle(Color(0xFFFFB070).copy(alpha = 0.6f), (0.6f + 0.8f * azar(e.numero, k)) * u, Offset(w * azar(e.numero, 20 + k), h * (0.55f + 0.4f * azar(e.numero, 40 + k))))
        }
    }

    /**
     * La hoja de acero de la orden: el marco remachado con su filo de latón, las cartelas de las esquinas con dos remaches, las
     * hileras de remaches de los costados, el arco apuntado y el engrane grabados apenas en el centro y la rejilla del zócalo al
     * pie. El papel ya es oscuro: solo los adornos; de noche, el resplandor de la forja abajo.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val laton = p.acento
        if (noche) drawRect(Brush.radialGradient(listOf(Color(0xFFE8743A).copy(alpha = 0.12f), Color.Transparent), center = Offset(w / 2, h), radius = w * 0.9f))
        // El arco apuntado y el engrane grabados en el centro.
        val ar = Rect(w * 0.16f, h * 0.2f, w * 0.84f, h * 0.78f)
        drawPath(arcoApuntado(ar, ar.width * 0.62f), laton.copy(alpha = 0.05f), style = Stroke(2.5f * u))
        drawPath(arcoApuntado(ar.deflate(10 * u), (ar.width - 20 * u) * 0.62f), laton.copy(alpha = 0.035f), style = Stroke(1.2f * u))
        drawPath(engrane(Offset(w / 2, h * 0.58f), w * 0.17f, 12), laton.copy(alpha = 0.035f))
        drawCircle(p.color.copy(alpha = 0.6f), w * 0.07f, Offset(w / 2, h * 0.58f))
        // El marco con su filo de latón.
        drawRect(laton.copy(alpha = 0.55f), Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), style = Stroke(2 * u))
        drawRect(laton.copy(alpha = 0.2f), Offset(10 * u, 10 * u), Size(w - 20 * u, h - 20 * u), style = Stroke(0.8f * u))
        // Las cartelas de las esquinas, con sus remaches.
        val k = 30 * u
        listOf(Offset(6 * u, 6 * u) to Offset(1f, 1f), Offset(w - 6 * u, 6 * u) to Offset(-1f, 1f), Offset(6 * u, h - 6 * u) to Offset(1f, -1f), Offset(w - 6 * u, h - 6 * u) to Offset(-1f, -1f)).forEach { (o, d) ->
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + d.x * k, o.y); lineTo(o.x, o.y + d.y * k); close() }, t(ACERO_LUZ, oscuro, noche).copy(alpha = 0.35f))
            drawLine(laton.copy(alpha = 0.5f), Offset(o.x + d.x * k, o.y), Offset(o.x, o.y + d.y * k), 1 * u)
            listOf(0.22f to 0.55f, 0.55f to 0.22f).forEach { (a, b) -> remache(Offset(o.x + d.x * k * a, o.y + d.y * k * b), 2 * u, oscuro, noche) }
        }
        // Las hileras de remaches de los costados.
        var y = 70 * u
        while (y < h - 50 * u) {
            listOf(15 * u, w - 15 * u).forEach { x -> drawCircle(laton.copy(alpha = 0.35f), 1.5f * u, Offset(x, y)); drawCircle(Color.Black.copy(alpha = 0.3f), 1.5f * u, Offset(x + 0.5f * u, y + 0.6f * u), style = Stroke(0.5f * u)) }
            y += 38 * u
        }
        // La rejilla del zócalo.
        val rj = Rect(w * 0.25f, h - 28 * u, w * 0.75f, h - 16 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), rj.topLeft, rj.size)
        drawRect(laton.copy(alpha = 0.35f), rj.topLeft, rj.size, style = Stroke(1 * u))
        var x = rj.left + 5 * u
        while (x < rj.right - 2 * u) { drawLine(laton.copy(alpha = 0.3f), Offset(x, rj.top + 2 * u), Offset(x, rj.bottom - 2 * u), 1.4f * u); x += 6 * u }
    }

    /** La viga de acero de arriba: con su filo de latón al pie, una hilera de remaches y la sombra que echa. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(mezcla(t(ACERO, oscuro, noche), Color.Black, 0.25f), mezcla(t(ACERO_OSC, oscuro, noche), Color.Black, 0.2f)), 0f, h))
        drawRect(t(ACERO_LUZ, oscuro, noche).copy(alpha = 0.5f), Offset.Zero, Size(w, 1 * u))
        drawRect(placa(LATON, oscuro, noche), Offset(0f, h - 3.5f * u), Size(w, 2.2f * u))
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, h - 1.3f * u), Size(w, 1.3f * u))
        var x = 10 * u
        while (x < w) { remache(Offset(x, 6 * u), 1.6f * u, oscuro, noche); x += 26 * u }
    }

    /** Una placa de latón ochavada, bruñida, con un remache en cada punta y el filete grabado. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        val ch = 6 * u
        fun ochavada(q: Rect) = Path().apply {
            moveTo(q.left + ch, q.top); lineTo(q.right - ch, q.top); lineTo(q.right, q.top + ch); lineTo(q.right, q.bottom - ch)
            lineTo(q.right - ch, q.bottom); lineTo(q.left + ch, q.bottom); lineTo(q.left, q.bottom - ch); lineTo(q.left, q.top + ch); close()
        }
        drawPath(ochavada(r.translate(1.5f * u, 2 * u)), Color.Black.copy(alpha = 0.45f))
        drawPath(ochavada(r), Brush.verticalGradient(listOf(placa(LATON_LUZ, oscuro, noche), placa(LATON, oscuro, noche), placa(COBRE, oscuro, noche)), r.top, r.bottom))
        drawPath(ochavada(r.deflate(3 * u)), NEGRO.copy(alpha = 0.35f), style = Stroke(0.8f * u))
        listOf(Offset(r.left + 7 * u, r.center.y), Offset(r.right - 7 * u, r.center.y)).forEach { remache(it, 2 * u, oscuro, noche) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFF1A1206)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 264.dp

    /** El roble del banco de trabajo. */
    private val ROBLE = Color(0xFF5E4026)
    private val LLAMA = Color(0xFFFFC45A)

    /** Una llave combinada de [largo] con el centro en [c] y girada [giro] grados: el mango, la boca abierta y el anillo. */
    private fun DrawScope.llave(c: Offset, largo: Float, giro: Float, color: Color, luz: Color, fondo: Color) {
        val u = density
        val g = largo * 0.11f
        rotate(giro, c) {
            drawRoundRect(color, Offset(c.x - g * 0.32f, c.y - largo * 0.36f), Size(g * 0.64f, largo * 0.72f), CornerRadius(g * 0.3f))
            drawLine(luz, Offset(c.x - g * 0.12f, c.y - largo * 0.3f), Offset(c.x - g * 0.12f, c.y + largo * 0.3f), 0.7f * u)
            // La boca abierta, arriba.
            val b = Offset(c.x, c.y - largo * 0.42f)
            drawCircle(color, g * 0.95f, b)
            drawRect(fondo, Offset(b.x - g * 0.35f, b.y - g * 1.05f), Size(g * 0.7f, g * 1.15f))
            drawRect(color, Offset(c.x - g * 0.32f, b.y), Size(g * 0.64f, largo * 0.1f))
            // El anillo, abajo.
            val a = Offset(c.x, c.y + largo * 0.42f)
            drawCircle(color, g * 0.85f, a)
            drawCircle(fondo, g * 0.45f, a)
            drawRect(color, Offset(c.x - g * 0.32f, c.y + largo * 0.3f), Size(g * 0.64f, largo * 0.06f))
        }
    }

    /**
     * El banco de trabajo de la orden: al fondo, la pared de chapas de acero remachadas con el tablero de las llaves colgadas
     * de menor a mayor; sobre el banco de roble con su fleje de acero, a la izquierda, el farol gótico de hierro con su ventana
     * apuntada y la llama; a la derecha, el tornillo de banco con su manivela, la aceitera de pico largo, un engrane suelto, las
     * tuercas y una llave tirada. El códice técnico va cerrado al centro: tapas de chapa de acero con los remaches del borde,
     * lomo de cuero carmesí, esquineras de latón, el escudo del engrane, la placa de latón con el nombre y la cuenta, las dos
     * bisagras que lo cierran por el canto y las cintas de registro, más cuantas más hojas. Al abrirse, las bisagras se sueltan
     * y la tapa gira sobre el lomo: dentro, el índice de pergamino remachado y la lámina con el engrane acotado. En oscuro, el
     * taller se hunde y solo alumbra el farol.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF060607), 0.5f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val fondoY = h * 0.5f
        val frenteY = h * 0.82f
        // ---- La pared de chapas remachadas.
        drawRect(f(ACERO), Offset.Zero, Size(w, fondoY))
        val chapa = 64 * u
        var cx0 = -10 * u
        while (cx0 < w) {
            drawLine(f(ACERO_OSC), Offset(cx0, 0f), Offset(cx0, fondoY), 2 * u)
            drawLine(f(ACERO_LUZ).copy(alpha = 0.5f), Offset(cx0 + 2 * u, 0f), Offset(cx0 + 2 * u, fondoY), 0.7f * u)
            var ry = 8 * u
            while (ry < fondoY) { drawCircle(f(ACERO_OSC), 1.4f * u, Offset(cx0 + 6 * u, ry)); drawCircle(f(ACERO_LUZ), 0.5f * u, Offset(cx0 + 5.6f * u, ry - 0.4f * u)); ry += 14 * u }
            cx0 += chapa
        }
        drawLine(f(ACERO_OSC), Offset(0f, h * 0.06f), Offset(w, h * 0.06f), 2 * u)
        // Las manchas de óxido que bajan de los remaches.
        for (k in 0 until 6) {
            val x = w * azar(201, k)
            drawRect(f(COBRE).copy(alpha = 0.18f), Offset(x, h * 0.06f), Size(2.5f * u, (10 + 30 * azar(203, k)) * u))
        }
        // El tablero de las llaves, colgadas de menor a mayor.
        val tab = Rect(w * 0.36f, h * 0.06f, w * 0.94f, fondoY - h * 0.08f)
        drawRect(f(mezcla(ROBLE, Color.Black, 0.25f)), tab.topLeft, tab.size)
        vetas(tab, f(Color.Black).copy(alpha = 0.3f), 211, densidad = 0.6f)
        drawRect(f(LATON), tab.topLeft, tab.size, style = Stroke(1.6f * u))
        val fondoTab = f(mezcla(ROBLE, Color.Black, 0.25f))
        val nl = 7
        for (k in 0 until nl) {
            val x = tab.left + tab.width * (k + 0.5f) / nl
            val largo = (34 + k * 5) * u
            val clavo = Offset(x, tab.top + 7 * u)
            llave(Offset(x, clavo.y + largo * 0.48f), largo, 180f, f(ACERO_LUZ), f(HOJA), fondoTab)
            drawCircle(f(LATON_LUZ), 1.6f * u, clavo)
        }
        // ---- El farol gótico, atrás a la izquierda, con su luz (el primer degradado).
        val fx = w * 0.15f
        val pie = fondoY + 18 * u
        val llama = Offset(fx, pie - 36 * u)
        drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = if (oscuro) 0.4f else 0.22f), LLAMA.copy(alpha = 0f)), llama, w * 0.5f), w * 0.5f, llama)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(fx - 16 * u, pie - 2 * u), Size(34 * u, 5 * u))
        drawRect(c(HIERRO), Offset(fx - 15 * u, pie - 6 * u), Size(30 * u, 6 * u))
        drawRect(c(HIERRO), Offset(fx - 13 * u, pie - 58 * u), Size(26 * u, 4 * u))
        val ven = Rect(fx - 11 * u, pie - 54 * u, fx + 11 * u, pie - 6 * u)
        drawPath(arcoApuntado(ven, ven.width * 0.9f), c(HIERRO))
        val vidrio = ven.deflate(3 * u)
        drawPath(arcoApuntado(vidrio, vidrio.width * 0.9f), LLAMA.copy(alpha = if (oscuro) 0.55f else 0.4f))
        drawLine(c(HIERRO), Offset(fx, vidrio.top + 2 * u), Offset(fx, vidrio.bottom), 1.2f * u)
        drawLine(c(HIERRO), Offset(vidrio.left, vidrio.top + vidrio.height * 0.55f), Offset(vidrio.right, vidrio.top + vidrio.height * 0.55f), 1.2f * u)
        drawOval(Color(0xFFFFE8B0), Offset(llama.x - 2.6f * u, llama.y - 6 * u), Size(5.2f * u, 10 * u))
        drawOval(Color.White.copy(alpha = 0.9f), Offset(llama.x - 1.2f * u, llama.y - 2 * u), Size(2.4f * u, 5 * u))
        drawPath(Path().apply { moveTo(fx - 13 * u, pie - 58 * u); lineTo(fx, pie - 76 * u); lineTo(fx + 13 * u, pie - 58 * u); close() }, c(HIERRO))
        drawLine(c(HIERRO), Offset(fx, pie - 76 * u), Offset(fx, pie - 84 * u), 1.4f * u)
        drawCircle(c(HIERRO), 3 * u, Offset(fx, pie - 87 * u), style = Stroke(1.2f * u))
        listOf(-1f, 1f).forEach { s -> drawCircle(c(LATON), 1.6f * u, Offset(fx + s * 12 * u, pie - 57 * u)) }

        // ---- El banco de roble: la cubierta, el fleje de acero del canto y, debajo, la sombra.
        drawRect(f(Color(0xFF1C1712)), Offset(0f, frenteY), Size(w, h - frenteY))
        drawRect(c(ROBLE), Offset(0f, fondoY), Size(w, frenteY - fondoY))
        vetas(Rect(0f, fondoY, w, frenteY), c(Color(0xFF2E1E10)).copy(alpha = 0.5f), 223, densidad = 0.7f)
        // Las tablas de la cubierta y las manchas de aceite.
        listOf(0.36f, 0.7f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, fondoY + (frenteY - fondoY) * f), Offset(w, fondoY + (frenteY - fondoY) * f), 1 * u) }
        for (k in 0 until 4) drawOval(Color.Black.copy(alpha = 0.12f), Offset(w * azar(227, k), fondoY + (frenteY - fondoY) * azar(229, k)), Size((14 + 16 * azar(233, k)) * u, (5 + 4 * azar(239, k)) * u))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, fondoY), Offset(w, fondoY), 1.2f * u)
        val canto = 14 * u
        drawRect(c(ROBLE), Offset(0f, frenteY), Size(w, canto))
        drawRect(c(ACERO_OSC), Offset(0f, frenteY + 3 * u), Size(w, canto - 6 * u))
        drawRect(c(ACERO_LUZ), Offset(0f, frenteY + 3 * u), Size(w, 1 * u))
        var bx = w * 0.05f
        while (bx < w) { drawCircle(c(LATON), 1.8f * u, Offset(bx, frenteY + canto / 2)); drawCircle(c(LATON_LUZ), 0.6f * u, Offset(bx - 0.5f * u, frenteY + canto / 2 - 0.5f * u)); bx += w * 0.1f }
        listOf(w * 0.03f, w * 0.92f).forEach { x -> drawRect(f(mezcla(ROBLE, Color.Black, 0.4f)), Offset(x, frenteY + canto), Size(w * 0.05f, h - frenteY - canto)) }

        // ---- El tornillo de banco, a la derecha, con su manivela.
        val vx = w * 0.88f
        val vb = fondoY + 30 * u
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(vx - 18 * u, vb + 2 * u), Size(40 * u, 6 * u))
        drawRect(c(HIERRO), Offset(vx - 16 * u, vb - 6 * u), Size(34 * u, 8 * u))
        drawRect(c(ACERO), Offset(vx - 14 * u, vb - 26 * u), Size(13 * u, 21 * u))
        drawRect(c(ACERO), Offset(vx + 3 * u, vb - 26 * u), Size(13 * u, 21 * u))
        drawRect(c(ACERO_LUZ), Offset(vx - 14 * u, vb - 26 * u), Size(30 * u, 2 * u))
        for (j in 0 until 6) drawLine(c(ACERO_OSC), Offset(vx - 13 * u + j * 2 * u, vb - 24 * u), Offset(vx - 13 * u + j * 2 * u, vb - 19 * u), 0.6f * u)
        drawLine(c(ACERO_LUZ), Offset(vx + 16 * u, vb - 14 * u), Offset(vx + 30 * u, vb - 14 * u), 3 * u)
        drawLine(c(ACERO_LUZ), Offset(vx + 30 * u, vb - 28 * u), Offset(vx + 30 * u, vb + 2 * u), 2 * u)
        listOf(vb - 28 * u, vb + 2 * u).forEach { y -> drawCircle(c(ACERO_OSC), 2.4f * u, Offset(vx + 30 * u, y)) }
        // La aceitera de pico largo.
        val ac = Offset(w * 0.78f, fondoY + 12 * u)
        drawOval(c(COBRE), Offset(ac.x - 11 * u, ac.y - 6 * u), Size(22 * u, 14 * u))
        drawRect(c(COBRE), Offset(ac.x - 11 * u, ac.y - 1 * u), Size(22 * u, 6 * u))
        drawOval(mezcla(c(COBRE), Color.White, 0.25f), Offset(ac.x - 8 * u, ac.y - 5 * u), Size(10 * u, 4 * u))
        drawLine(c(LATON), Offset(ac.x, ac.y - 5 * u), Offset(ac.x - 26 * u, ac.y - 30 * u), 1.6f * u)
        drawCircle(c(LATON), 2.4f * u, Offset(ac.x, ac.y - 5 * u))
        // Un engrane suelto, las tuercas y una llave tirada delante.
        val en = Offset(w * 0.86f, frenteY - 16 * u)
        drawPath(engrane(Offset(en.x + 1.5f * u, en.y + 2 * u), 11 * u, 10), Color.Black.copy(alpha = 0.3f))
        drawPath(engrane(en, 11 * u, 10), c(LATON))
        drawCircle(c(ROBLE), 3.4f * u, en)
        drawCircle(c(LATON_LUZ), 7 * u, en, style = Stroke(0.7f * u))
        listOf(Offset(w * 0.08f, frenteY - 12 * u), Offset(w * 0.12f, frenteY - 8 * u), Offset(w * 0.78f, frenteY - 8 * u)).forEach { o ->
            drawPath(estrella(o, 3.6f * u, 6, 0.88f, 0f), c(ACERO_LUZ))
            drawCircle(c(ACERO_OSC), 1.4f * u, o)
        }
        llave(Offset(w * 0.17f, frenteY - 22 * u), 46 * u, 78f, c(ACERO_LUZ), c(HOJA), c(ROBLE))

        // ---- El códice técnico.
        val g = grosorLibreta(e.hojas, u, minimo = 3.5f)
        val ancho = w * 0.5f
        val lomo = 12 * u
        val suelta = (a / 0.14f).coerceIn(0f, 1f)
        val angulo = giroTapa(((a - 0.12f) / 0.88f).coerceIn(0f, 1f))
        val corre = ancho * 0.2f * (1f - cos(angulo)) / 2f
        val xl = w * 0.27f + corre
        val x0 = xl + lomo; val x1 = xl + ancho
        val fy = frenteY - 10 * u
        val hondo = h * 0.24f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val hojas = c(PERGAMINO)
        sombraSuave(Rect(xl, fy - hondo, x1, fy), 2 * u, 9 * u, if (oscuro) 0.45f else 0.32f)
        // La tapa de atrás.
        drawRect(c(ACERO_OSC), Offset(xl, fy - hondo), Size(ancho, hondo))
        drawRect(c(HIERRO), Offset(xl, fy - 2.4f * u), Size(ancho, 2.4f * u))
        // Las cintas de registro que cuelgan por delante.
        val cintas = (1 + e.hojas / 40).coerceAtMost(4)
        for (k in 0 until cintas) {
            val x = x0 + ancho * (0.2f + 0.17f * k) + 6 * u * azar(e.semilla, 40 + k)
            val col = c(if (k % 2 == 0) LACRE else Color(0xFF1E3A5A))
            drawPath(Path().apply { moveTo(x, tapaY); lineTo(x + 4 * u, tapaY); lineTo(x + 4.5f * u, fy + (10 + 6 * azar(e.semilla, 50 + k)) * u); lineTo(x + 2.2f * u, fy + (7 + 6 * azar(e.semilla, 50 + k)) * u); lineTo(x - 0.5f * u, fy + (10 + 6 * azar(e.semilla, 50 + k)) * u); close() }, col)
        }
        cantoHojas(Rect(x0, tapaY + 2.4f * u, x1 - 2 * u, fy - 2.4f * u), mezcla(hojas, Color.Black, 0.04f), NEGRO, e.semilla)
        // El lomo de cuero carmesí, con sus nervios.
        drawRect(c(CARMESI), Offset(xl, arriba), Size(lomo, fy - arriba))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(xl + 2 * u, arriba), Size(1.4f * u, fy - arriba))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(xl + lomo - 2 * u, arriba), Size(2 * u, fy - arriba))
        for (j in 1..3) drawLine(c(mezcla(CARMESI, Color.Black, 0.4f)), Offset(xl, arriba + (fy - arriba) * j / 4f), Offset(xl + lomo, arriba + (fy - arriba) * j / 4f), 1.6f * u)
        // La lámina técnica, que se ve al abrir.
        if (angulo > 0.01f) {
            val r = Rect(x0 + 2 * u, arriba + 2 * u, x1 - 4 * u, tapaY + 1 * u)
            drawRect(c(HOJA), r.topLeft, r.size)
            val linea = c(Color(0xFF2B4A72))
            drawRect(linea.copy(alpha = 0.6f), r.deflate(3 * u).topLeft, r.deflate(3 * u).size, style = Stroke(0.7f * u))
            val ce = Offset(r.left + r.width * 0.34f, r.center.y + 1 * u)
            val re = minOf(r.height * 0.36f, r.width * 0.2f)
            drawPath(engrane(ce, re, 12), linea, style = Stroke(1 * u))
            drawCircle(linea, re * 0.36f, ce, style = Stroke(0.8f * u))
            drawLine(linea.copy(alpha = 0.5f), Offset(ce.x - re * 1.3f, ce.y), Offset(ce.x + re * 1.3f, ce.y), 0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 1.5f * u, 1 * u, 1.5f * u)))
            drawLine(linea.copy(alpha = 0.5f), Offset(ce.x, ce.y - re * 1.3f), Offset(ce.x, ce.y + re * 1.3f), 0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 1.5f * u, 1 * u, 1.5f * u)))
            // La cota con sus flechas y la cartela.
            val cy = r.bottom - 7 * u
            drawLine(linea, Offset(ce.x - re, cy), Offset(ce.x + re, cy), 0.6f * u)
            listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(ce.x + s * re, cy); lineTo(ce.x + s * (re - 4 * u), cy - 1.5f * u); lineTo(ce.x + s * (re - 4 * u), cy + 1.5f * u); close() }, linea) }
            val cart = Rect(r.right - r.width * 0.38f, r.bottom - r.height * 0.36f, r.right - 4 * u, r.bottom - 4 * u)
            drawRect(linea, cart.topLeft, cart.size, style = Stroke(0.8f * u))
            drawLine(linea, Offset(cart.left, cart.center.y), Offset(cart.right, cart.center.y), 0.6f * u)
            textoEn(medidor, "LÁMINA ${e.hojas}", Rect(cart.left, cart.top, cart.right, cart.center.y), letra, linea, 8f, minimo = 4.5f)
            for (j in 0 until 3) drawLine(linea.copy(alpha = 0.6f), Offset(r.right - r.width * 0.38f, r.top + 10 * u + j * 6 * u), Offset(r.right - 8 * u - j * 6 * u, r.top + 10 * u + j * 6 * u), 0.8f * u)
            val sombra = sin(angulo).coerceAtLeast(0f)
            if (cos(angulo) > 0f) sombraTapa(x0 + (x1 - x0) * cos(angulo), arriba, (x1 - x0) * 0.24f * sombra, tapaY - arriba)
        }
        // La tapa de chapa, que gira sobre el lomo.
        tapaLateral(x0, angulo, 0.42f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            drawRect(c(ACERO), r.topLeft, r.size)
            drawRect(c(ACERO_OSC), Offset(r.left, r.bottom), Size(r.width, 2.4f * u))
            drawRect(c(ACERO_LUZ), r.topLeft, Size(r.width, 1.4f * u))
            // El grano de la chapa.
            for (k in 0 until 24) {
                val y = r.top + r.height * azar(e.semilla, 60 + k)
                drawLine(Color.White.copy(alpha = 0.05f), Offset(r.left, y), Offset(r.right, y), 0.6f * u)
            }
            // Los remaches del borde.
            val m = 5 * u
            var x = r.left + m; while (x < r.right - m) { drawCircle(c(HIERRO), 1.3f * u, Offset(x, r.top + m)); drawCircle(c(HIERRO), 1.3f * u, Offset(x, r.bottom - m)); x += 9 * u }
            // Las esquineras de latón.
            val es = 12 * u
            listOf(Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.right, r.bottom) to Offset(-1f, -1f)).forEach { (o, s) ->
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * es, o.y); lineTo(o.x, o.y + s.y * es); close() }, c(LATON))
                drawCircle(c(LATON_LUZ), 1.2f * u, Offset(o.x + s.x * es * 0.28f, o.y + s.y * es * 0.28f))
            }
            // El escudo del engrane, a la izquierda.
            val esc = Rect(r.left + 9 * u, r.top + r.height * 0.22f, r.left + 9 * u + r.height * 0.42f, r.top + r.height * 0.8f)
            drawPath(escudo(esc), c(CARMESI))
            drawPath(escudo(esc), c(LATON), style = Stroke(1.4f * u))
            drawPath(engrane(Offset(esc.center.x, esc.top + esc.height * 0.42f), esc.width * 0.3f, 8), c(LATON))
            drawCircle(c(CARMESI), esc.width * 0.1f, Offset(esc.center.x, esc.top + esc.height * 0.42f))
            // La placa de latón con el nombre y la cuenta.
            val pl = Rect(esc.right + 6 * u, r.top + r.height * 0.2f, r.right - 12 * u, r.top + r.height * 0.8f)
            val lat = placa(LATON, oscuro, false)
            drawRect(Color.Black.copy(alpha = 0.35f), Offset(pl.left + 1 * u, pl.top + 1.5f * u), pl.size)
            drawRect(lat, pl.topLeft, pl.size)
            drawRect(placa(LATON_LUZ, oscuro, false), pl.topLeft, Size(pl.width, 1.2f * u))
            drawRect(mezcla(lat, Color.Black, 0.45f), pl.deflate(2.2f * u).topLeft, pl.deflate(2.2f * u).size, style = Stroke(0.7f * u))
            listOf(pl.left + 4 * u, pl.right - 4 * u).forEach { xx -> drawCircle(mezcla(lat, Color.Black, 0.5f), 1.2f * u, Offset(xx, pl.center.y)) }
            val corte = pl.top + pl.height * 0.58f
            val grabado = Color(0xFF2A1E0C)
            textoEn(medidor, e.nombre, Rect(pl.left + 7 * u, pl.top + 3 * u, pl.right - 7 * u, corte), letra, grabado, 13f, espaciado = 0.2f, minimo = 5.5f)
            drawLine(grabado.copy(alpha = 0.5f), Offset(pl.left + 10 * u, corte), Offset(pl.right - 10 * u, corte), 0.6f * u)
            textoEn(medidor, e.cuenta, Rect(pl.left + 8 * u, corte + 0.5f * u, pl.right - 8 * u, pl.bottom - 3 * u), letra, grabado.copy(alpha = 0.85f), 9.5f, minimo = 5f)
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.45f * sombra), r.topLeft, Size(r.width, r.height + 2.4f * u))
        }, dentro = { sombra ->
            // Por dentro: la chapa con sus remaches y el índice de pergamino.
            val r = Rect(x0 - (x1 - x0), arriba, x0, tapaY + 2.4f * u)
            drawRect(c(ACERO_OSC), r.topLeft, r.size)
            var x = r.left + 5 * u; while (x < r.right - 4 * u) { drawCircle(c(HIERRO), 1.2f * u, Offset(x, r.top + 5 * u)); drawCircle(c(HIERRO), 1.2f * u, Offset(x, r.bottom - 5 * u)); x += 9 * u }
            val ind = Rect(r.left + r.width * 0.12f, r.top + 11 * u, r.right - r.width * 0.12f, r.bottom - 11 * u)
            drawRect(hojas, ind.topLeft, ind.size)
            textoEn(medidor, "ÍNDICE", Rect(ind.left, ind.top + 1 * u, ind.right, ind.top + 11 * u), letra, c(CARMESI), 9f, espaciado = 1f, minimo = 5f)
            var y = ind.top + 14 * u; var k = 0
            while (y < ind.bottom - 4 * u) {
                drawLine(NEGRO.copy(alpha = 0.4f), Offset(ind.left + 6 * u, y), Offset(ind.left + ind.width * (0.45f + 0.25f * azar(e.semilla, 80 + k)), y), 0.8f * u)
                drawLine(NEGRO.copy(alpha = 0.3f), Offset(ind.right - 14 * u, y), Offset(ind.right - 6 * u, y), 0.8f * u)
                y += 6 * u; k++
            }
            listOf(ind.topLeft, Offset(ind.right, ind.top), Offset(ind.left, ind.bottom), Offset(ind.right, ind.bottom)).forEach { o -> drawCircle(c(LATON), 1.6f * u, o) }
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size)
        })
        // Las bisagras que lo cierran por el canto: al abrir, caen colgando.
        listOf(0.3f, 0.72f).forEach { fz ->
            val y = arriba + (tapaY - arriba) * fz
            val pivote = Offset(x1 + 2 * u, y)
            drawRect(c(HIERRO), Offset(x1, y), Size(4 * u, fy - y))
            drawRect(c(ACERO_LUZ), Offset(x1 + 3 * u, y), Size(1 * u, fy - y))
            rotate(-90f * suelta, pivote) {
                val brazo = Rect(x1 - 18 * u, y - 4 * u, x1 + 2 * u, y + 4 * u)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(brazo.left + 1 * u, brazo.top + 1.5f * u), brazo.size)
                drawRect(c(HIERRO), brazo.topLeft, brazo.size)
                drawRect(c(ACERO_LUZ), brazo.topLeft, Size(brazo.width, 1 * u))
                drawCircle(c(LATON), 1.6f * u, Offset(brazo.left + 4 * u, brazo.center.y))
            }
            drawCircle(c(LATON), 2.2f * u, pivote)
            drawCircle(c(LATON_LUZ), 0.8f * u, Offset(pivote.x - 0.6f * u, pivote.y - 0.6f * u))
        }
    }

    /**
     * La pestaña de un día: una correa de cuero que cuelga, con la letra de la semana estampada arriba, la placa ochavada de
     * latón remachada con el día y, abajo, el esmalte del color del ánimo engastado en su bisel (vacío, si no hay).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val cuero = c(CUEROS[(e.semilla and 0x7fffffff) % CUEROS.size])
        val co = Rect(8 * u, 0f, w - 8 * u, h - 2 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(co.left + 1.5f * u, co.top), co.size)
        drawPath(Path().apply { moveTo(co.left, co.top); lineTo(co.right, co.top); lineTo(co.right, co.bottom - 5 * u); lineTo(co.center.x, co.bottom); lineTo(co.left, co.bottom - 5 * u); close() }, cuero)
        drawLine(c(PERGAMINO).copy(alpha = 0.45f), Offset(co.left + 2 * u, 0f), Offset(co.left + 2 * u, co.bottom - 6 * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u)))
        drawLine(c(PERGAMINO).copy(alpha = 0.45f), Offset(co.right - 2 * u, 0f), Offset(co.right - 2 * u, co.bottom - 6 * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u)))
        textoEn(medidor, e.semana.uppercase(), Rect(co.left, 1 * u, co.right, 10 * u), letra, c(LATON_LUZ), 8f, minimo = 5f)
        // La placa ochavada con el día.
        val p = Rect(2 * u, 11 * u, w - 2 * u, 37 * u)
        val ch = 5 * u
        val ocho = Path().apply {
            moveTo(p.left + ch, p.top); lineTo(p.right - ch, p.top); lineTo(p.right, p.top + ch); lineTo(p.right, p.bottom - ch)
            lineTo(p.right - ch, p.bottom); lineTo(p.left + ch, p.bottom); lineTo(p.left, p.bottom - ch); lineTo(p.left, p.top + ch); close()
        }
        translate(1 * u, 1.5f * u) { drawPath(ocho, Color.Black.copy(alpha = 0.35f)) }
        val lat = placa(LATON, oscuro, false)
        drawPath(ocho, lat)
        drawPath(ocho, mezcla(lat, Color.Black, 0.45f), style = Stroke(0.9f * u))
        drawLine(placa(LATON_LUZ, oscuro, false), Offset(p.left + ch, p.top + 1 * u), Offset(p.right - ch, p.top + 1 * u), 0.8f * u)
        listOf(p.left + 3.5f * u, p.right - 3.5f * u).forEach { x -> drawCircle(mezcla(lat, Color.Black, 0.5f), 1 * u, Offset(x, p.center.y)) }
        textoEn(medidor, e.dia, Rect(p.left + 6 * u, p.top + 1 * u, p.right - 6 * u, p.bottom - 1 * u), letra, Color(0xFF2A1E0C), 18f, minimo = 8f)
        // El esmalte del ánimo, en su bisel.
        val s = Offset(w / 2, 45 * u)
        drawCircle(lat, 5.4f * u, s)
        drawCircle(mezcla(lat, Color.Black, 0.45f), 5.4f * u, s, style = Stroke(0.7f * u))
        if (e.tono != null) {
            drawCircle(e.tono, 3.8f * u, s)
            drawCircle(Color.White.copy(alpha = 0.45f), 1.1f * u, Offset(s.x - 1.4f * u, s.y - 1.4f * u))
        } else drawCircle(c(NEGRO), 3.8f * u, s)
    }

    /**
     * El rótulo de un mes: una banda de cuero carmesí con sus pespuntes, el mes estampado en latón y, en las puntas, dos
     * engranes de latón que la clavan.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val b = Rect(0f, 4 * u, w, h - 4 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, b.top + 2 * u), b.size)
        drawRect(Brush.verticalGradient(listOf(c(mezcla(CARMESI, Color.White, 0.12f)), c(CARMESI), c(mezcla(CARMESI, Color.Black, 0.35f))), b.top, b.bottom), b.topLeft, b.size)
        listOf(b.top + 3 * u, b.bottom - 3 * u).forEach { y -> drawLine(c(PERGAMINO).copy(alpha = 0.55f), Offset(4 * u, y), Offset(w - 4 * u, y), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))) }
        listOf(15 * u, w - 15 * u).forEach { x ->
            drawPath(engrane(Offset(x + 0.8f * u, h / 2 + 1.2f * u), 9 * u, 10), Color.Black.copy(alpha = 0.35f))
            drawPath(engrane(Offset(x, h / 2), 9 * u, 10), placa(LATON, oscuro, false))
            drawCircle(c(HIERRO), 2.4f * u, Offset(x, h / 2))
        }
        val r = Rect(32 * u, b.top, w - 32 * u, b.bottom)
        textoEn(medidor, texto.uppercase(), r.translate(0f, 0.8f * u), letra, Color.Black.copy(alpha = 0.45f), 15f, espaciado = 1.5f, minimo = 7f)
        textoEn(medidor, texto.uppercase(), r, letra, placa(LATON_LUZ, oscuro, false), 15f, espaciado = 1.5f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** Una cadena de eslabones de [a] a [b], hasta la fracción [f] del camino: uno de cara y otro de canto, alternados. */
    private fun DrawScope.cadena(a: Offset, b: Offset, f: Float, grueso: Float, color: Color, luz: Color) {
        val u = density
        val d = b - a
        val largo = d.getDistance()
        if (largo < 1f || f <= 0f) return
        val ang = Math.toDegrees(kotlin.math.atan2(d.y, d.x).toDouble()).toFloat()
        val paso = grueso * 1.5f
        val n = (largo * f.coerceIn(0f, 1f) / paso).toInt()
        for (i in 0 until n) {
            val o = a + d * ((i + 0.5f) * paso / largo)
            rotate(ang, o) {
                if (i % 2 == 0) {
                    drawOval(Color.Black.copy(alpha = 0.35f), Offset(o.x - grueso + 0.6f * u, o.y - grueso * 0.55f + 0.9f * u), Size(grueso * 2f, grueso * 1.1f), style = Stroke(grueso * 0.32f))
                    drawOval(color, Offset(o.x - grueso, o.y - grueso * 0.55f), Size(grueso * 2f, grueso * 1.1f), style = Stroke(grueso * 0.32f))
                    drawArc(luz, 200f, 80f, false, Offset(o.x - grueso, o.y - grueso * 0.55f), Size(grueso * 2f, grueso * 1.1f), style = Stroke(grueso * 0.14f))
                } else {
                    drawLine(color, Offset(o.x - grueso * 0.95f, o.y), Offset(o.x + grueso * 0.95f, o.y), grueso * 0.38f, StrokeCap.Round)
                    drawLine(luz, Offset(o.x - grueso * 0.7f, o.y - grueso * 0.08f), Offset(o.x + grueso * 0.5f, o.y - grueso * 0.08f), grueso * 0.1f, StrokeCap.Round)
                }
            }
        }
    }

    /** Un hexágono plano (con dos lados arriba y abajo), de radio [r]. */
    private fun hexagonoSello(c: Offset, r: Float): Path = Path().apply {
        for (j in 0 until 6) { val a = j * PI / 3; val x = c.x + (r * cos(a)).toFloat(); val y = c.y + (r * sin(a)).toFloat(); if (j == 0) moveTo(x, y) else lineTo(x, y) }
        close()
    }

    /**
     * La funda de despachos de la Orden: dos placas de acero pavonado unidas, con la orla de remaches de latón y una
     * tracería de arcos apuntados grabada en el fondo. La solapa es un gablete apuntado con su filo de latón, que baja hasta
     * la punta donde va el sello: la placa hexagonal de latón con el engrane de la Orden. Debajo, la cartela de latón con el
     * escudo a los lados donde va, estarcido, a quién va y las fechas. Las de al futuro van además encadenadas: dos cadenas
     * bajan de las argollas de las esquinas al sello. Cuando ya se puede abrir, el sello se calienta (la luz de alerta roja
     * por debajo del latón); al abrirla, el hexágono se parte y la solapa se alza. Dentro va el parte, en la placa oscura de
     * la Orden con su filete de latón.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        fun p(x: Color) = placa(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top + 2 * u
        val punta = s.top + h * 0.48f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val sc = Offset(s.center.x, punta)
        val sr = h * 0.115f
        fun sello(alfa: Float) {
            fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
            val cal = e.brillo
            drawPath(hexagonoSello(sc + Offset(0.8f * u, 1.4f * u), sr), a(Color.Black.copy(alpha = 0.4f)))
            drawPath(hexagonoSello(sc, sr), a(mezcla(p(LATON), Color(0xFFFFB070), cal * 0.55f)))
            drawPath(hexagonoSello(sc, sr), a(mezcla(c(COBRE), ALERTA, cal * 0.5f)), style = Stroke(1.2f * u))
            for (j in 0 until 6) { val ang = j * PI / 3 + PI / 6; drawCircle(a(c(COBRE)), sr * 0.07f, Offset(sc.x + (sr * 0.74f * cos(ang)).toFloat(), sc.y + (sr * 0.74f * sin(ang)).toFloat())) }
            drawPath(engrane(sc, sr * 0.52f, 8), a(mezcla(c(COBRE), ALERTA, cal * 0.7f)))
            drawCircle(a(mezcla(p(LATON_LUZ), Color.White, cal * 0.3f)), sr * 0.2f, sc)
            drawCircle(a(c(COBRE)), sr * 0.08f, sc)
            drawLine(a(p(LATON_LUZ).copy(alpha = 0.7f)), Offset(sc.x - sr * 0.6f, sc.y - sr * 0.62f), Offset(sc.x - sr * 0.1f, sc.y - sr * 0.84f), 0.8f * u)
        }
        val solapa = Path().apply {
            moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right, bis + h * 0.08f)
            cubicTo(s.right - w * 0.12f, bis + h * 0.22f, sc.x + w * 0.12f, punta - h * 0.12f, sc.x, punta)
            cubicTo(sc.x - w * 0.12f, punta - h * 0.12f, s.left + w * 0.12f, bis + h * 0.22f, s.left, bis + h * 0.08f); close()
        }

        sombraSuave(s, 3 * u, 7 * u, 0.32f)
        // ---- La placa de atrás (su cara de dentro, forrada de carmesí).
        drawRoundRect(c(ACERO_OSC), s.topLeft, s.size, CornerRadius(3 * u))
        drawRect(c(mezcla(CARMESI, NEGRO, 0.45f)), Offset(s.left + 4 * u, s.top + 3 * u), Size(w - 8 * u, h * 0.4f))
        // ---- La solapa en gablete apuntado.
        fun DrawScope.gablete(adentro: Boolean) {
            if (adentro) {
                drawPath(solapa, c(mezcla(CARMESI, NEGRO, 0.3f)))
                drawPath(solapa, c(LATON).copy(alpha = 0.6f), style = Stroke(1.2f * u))
                drawPath(escudo(Rect(sc.x - w * 0.05f, bis + h * 0.12f, sc.x + w * 0.05f, bis + h * 0.3f)), c(mezcla(CARMESI, NEGRO, 0.55f)))
                return
            }
            translate(0f, 1.6f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.4f)) }
            drawPath(solapa, c(ACERO_LUZ))
            clipPath(solapa) {
                // La tracería grabada: un arco apuntado dentro del gablete y su parteluz.
                drawPath(arcoApuntado(Rect(sc.x - w * 0.18f, bis + h * 0.06f, sc.x + w * 0.18f, punta + h * 0.2f), h * 0.2f), c(ACERO), style = Stroke(1 * u))
                drawLine(c(ACERO), Offset(sc.x, bis + h * 0.06f), Offset(sc.x, punta), 0.8f * u)
                drawRect(Color.White.copy(alpha = 0.08f), Offset(s.left, bis), Size(w, 2 * u))
            }
            drawPath(solapa, p(LATON), style = Stroke(1.6f * u * k))
            listOf(0.1f, 0.3f, 0.7f, 0.9f).forEach { f -> remache(Offset(s.left + w * f, bis + 3.5f * u), 1.6f * u * k, oscuro, false) }
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2.5f * u * e.romper); rotate(-6f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, true, 6, e.semilla)) { sello(1f) }
            }
        }
        if (!cerrada) solapaCarta(bis, ab) { gablete(it) }

        // ---- El parte: la placa oscura de la Orden con su filete de latón.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
            val hoja = Rect(s.left + w * 0.08f, arriba0 - alza, s.right - w * 0.08f, arriba0 - alza + h * 0.88f)
            drawRect(Color.Black.copy(alpha = 0.4f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
            drawRect(e.hoja, hoja.topLeft, hoja.size)
            drawRect(c(LATON).copy(alpha = 0.7f), hoja.deflate(2.5f * u).topLeft, hoja.deflate(2.5f * u).size, style = Stroke(0.8f * u))
            drawPath(escudo(Rect(hoja.center.x - 2.5f * u, hoja.top + 3.5f * u, hoja.center.x + 2.5f * u, hoja.top + 9.5f * u)), c(CARMESI))
            var y = hoja.top + 14 * u
            while (y < hoja.bottom - 4 * u) { drawLine(e.renglones, Offset(hoja.left + 6 * u, y), Offset(hoja.right - 6 * u, y), 0.7f * u); y += 11 * u }
        }

        // ---- La placa de delante: acero pavonado, la orla de remaches y la tracería del fondo.
        val frente = Rect(s.left, s.top + h * 0.06f, s.right, s.bottom)
        drawRoundRect(c(ACERO), frente.topLeft, frente.size, CornerRadius(3 * u))
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            for (i in 0 until 5) {
                val ax = frente.left + w * (0.02f + i * 0.2f)
                drawPath(arcoApuntado(Rect(ax, frente.top + h * 0.3f, ax + w * 0.16f, frente.bottom + 4 * u), h * 0.16f), c(ACERO_OSC).copy(alpha = 0.6f), style = Stroke(1 * u))
            }
            drawRect(Color.White.copy(alpha = 0.06f), Offset(frente.left, frente.top), Size(w, 2 * u))
        }
        drawRoundRect(c(HIERRO), frente.topLeft, frente.size, CornerRadius(3 * u), style = Stroke(1.2f * u))
        if (!cerrada) drawRect(Color.Black.copy(alpha = 0.5f), Offset(frente.left + 2 * u, frente.top), Size(w - 4 * u, 1.8f * u))
        var rx = frente.left + 5 * u
        while (rx < frente.right - 3 * u) { remache(Offset(rx, frente.bottom - 4 * u), 1.5f * u * k, oscuro, false); rx += 13 * u * k }
        listOf(frente.left + 4.5f * u, frente.right - 4.5f * u).forEach { x ->
            var ry = frente.top + h * 0.48f
            while (ry < frente.bottom - 9 * u) { remache(Offset(x, ry), 1.5f * u * k, oscuro, false); ry += 12 * u * k }
        }
        // La cartela de latón: el escudo a cada lado y el texto estarcido en medio.
        val cart = Rect(s.left + w * 0.08f, s.top + h * 0.6f, s.right - w * 0.08f, s.bottom - h * 0.11f)
        drawRoundRect(Color.Black.copy(alpha = 0.4f), cart.topLeft + Offset(0.8f * u, 1.4f * u), cart.size, CornerRadius(2 * u))
        drawRoundRect(p(LATON), cart.topLeft, cart.size, CornerRadius(2 * u))
        drawRoundRect(p(LATON_LUZ), cart.topLeft, Size(cart.width, 1.4f * u), CornerRadius(1 * u))
        drawRoundRect(c(COBRE), cart.deflate(2 * u).topLeft, cart.deflate(2 * u).size, CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        val ew = cart.height * 0.3f
        listOf(cart.left + ew * 0.95f, cart.right - ew * 0.95f).forEach { x ->
            val er = Rect(x - ew / 2, cart.center.y - ew * 0.6f, x + ew / 2, cart.center.y + ew * 0.6f)
            drawPath(escudo(er), c(CARMESI))
            drawPath(escudo(er), c(COBRE), style = Stroke(0.7f * u))
            drawLine(p(LATON_LUZ), Offset(er.center.x, er.top + er.height * 0.2f), Offset(er.center.x, er.bottom - er.height * 0.2f), 0.8f * u)
        }
        val tx = Rect(cart.left + ew * 1.6f, cart.top + 2 * u, cart.right - ew * 1.6f, cart.bottom - 2 * u)
        textoEn(medidor, e.linea1, Rect(tx.left, tx.top, tx.right, tx.top + tx.height * 0.58f), letra, NEGRO, 11.5f, peso = FontWeight.Bold, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(tx.left, tx.top + tx.height * 0.56f, tx.right, tx.bottom), letra, NEGRO.copy(alpha = 0.85f), 9f, minimo = 6f)

        // ---- La solapa cerrada.
        if (cerrada) solapaCarta(bis, ab) { gablete(it) }
        // ---- Las cadenas: de las argollas de las esquinas hasta el sello.
        if (e.cordon > 0f && cerrada) {
            listOf(Offset(s.left + w * 0.06f, bis + h * 0.12f), Offset(s.right - w * 0.06f, bis + h * 0.12f)).forEach { o ->
                drawCircle(c(HIERRO), 3.2f * u * k, o, style = Stroke(1.4f * u))
                cadena(o, sc, e.cordon, 2.6f * u * k, c(ACERO_LUZ), c(Color(0xFFC9D0D8)))
            }
        }
        // ---- El sello: entero (cayendo al sellar) o partido.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(ALERTA.copy(alpha = 0.6f * e.brillo), LATON_LUZ.copy(alpha = 0.15f * e.brillo), Color.Transparent),
                center = sc, radius = sr * 3f), sr * 3f, sc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc) }) { sello(e.alfaSello.coerceIn(0f, 1f)) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2.5f * u * e.romper); rotate(7f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, false, 6, e.semilla)) { sello(1f) }
            }
        }
    }

    // El parte oscuro que asoma de la funda, por dentro de su filete de latón.
    override val cajaPrevia = Rect(0.14f, 0.17f, 0.86f, 0.33f)

    override val altoBuzon: Dp = 214.dp

    /**
     * El arcón de despachos de la Orden, contra la pared de chapas remachadas: un cofre de roble forrado de hierro, con sus
     * flejes, cantoneras y la cerradura, y la tapa abierta hacia atrás (por dentro, el forro carmesí con el escudo del
     * engrane). Dentro, tres compartimentos con los despachos de pie: los de sello levantado («LEVANTADOS»), con el hexágono encendido en
     * rojo; los remachados, cruzados por su cadena; y los leídos, con el parte asomando. En el frente, tres placas de latón con
     * el nombre y la cuenta de cada compartimento. Arriba cuelga la lámpara de alerta, que se enciende cuando hay alguno listo.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF050506), 0.45f) else c(x)
        fun p(x: Color) = placa(x, oscuro, false)
        val roble = Color(0xFF5E4026)
        // ---- La pared de chapas remachadas y el piso de rejilla.
        drawRect(f(ACERO))
        val chapa = 60 * u
        var x = 0f
        while (x < w) {
            drawLine(f(ACERO_OSC), Offset(x, 0f), Offset(x, h), 1.4f * u)
            var y = 6 * u; while (y < h) { drawCircle(f(ACERO_LUZ), 1.2f * u, Offset(x + 4 * u, y)); y += 14 * u }
            x += chapa
        }
        drawLine(f(ACERO_OSC), Offset(0f, h * 0.45f), Offset(w, h * 0.45f), 1.4f * u)
        val piso = h - 16 * u
        drawRect(f(HIERRO), Offset(0f, piso), Size(w, h - piso))
        var gx = 0f; while (gx < w) { drawLine(f(NEGRO), Offset(gx, piso), Offset(gx, h), 1.6f * u); gx += 6 * u }
        // ---- La lámpara de alerta, colgada de su cadena.
        val lam = Offset(w * 0.5f, 26 * u)
        val alerta = e.listas > 0
        cadena(Offset(lam.x, 0f), Offset(lam.x, lam.y - 8 * u), 1f, 2.2f * u, f(ACERO_LUZ), f(ACERO_LUZ))
        if (alerta) drawCircle(Brush.radialGradient(listOf(ALERTA.copy(alpha = 0.5f), Color.Transparent), center = lam, radius = 70 * u), 70 * u, lam)
        drawPath(arcoApuntado(Rect(lam.x - 7 * u, lam.y - 10 * u, lam.x + 7 * u, lam.y + 8 * u), 9 * u), if (alerta) ALERTA else c(Color(0xFF5A1A18)))
        listOf(-4f, 0f, 4f).forEach { dx -> drawLine(c(HIERRO), Offset(lam.x + dx * u, lam.y - 8 * u), Offset(lam.x + dx * u, lam.y + 8 * u), 1 * u) }
        drawRect(c(HIERRO), Offset(lam.x - 9 * u, lam.y + 7 * u), Size(18 * u, 3 * u))

        // ---- El arcón.
        val cofre = Rect(w * 0.05f, h * 0.5f, w * 0.95f, piso + 4 * u)
        val tapaAlto = h * 0.3f
        // La tapa abierta hacia atrás: se ve su forro carmesí.
        val tapa = Path().apply { moveTo(cofre.left + 6 * u, cofre.top); lineTo(cofre.right - 6 * u, cofre.top); lineTo(cofre.right - 14 * u, cofre.top - tapaAlto); lineTo(cofre.left + 14 * u, cofre.top - tapaAlto); close() }
        drawPath(tapa, c(roble))
        val forro = Path().apply { moveTo(cofre.left + 14 * u, cofre.top - 4 * u); lineTo(cofre.right - 14 * u, cofre.top - 4 * u); lineTo(cofre.right - 20 * u, cofre.top - tapaAlto + 6 * u); lineTo(cofre.left + 20 * u, cofre.top - tapaAlto + 6 * u); close() }
        drawPath(forro, Brush.verticalGradient(listOf(c(mezcla(CARMESI, NEGRO, 0.5f)), c(CARMESI)), cofre.top - tapaAlto, cofre.top))
        val esc = Rect(w / 2 - 14 * u, cofre.top - tapaAlto + 14 * u, w / 2 + 14 * u, cofre.top - tapaAlto + 48 * u)
        drawPath(escudo(esc), c(mezcla(CARMESI, NEGRO, 0.25f)))
        drawPath(escudo(esc), p(LATON), style = Stroke(1.2f * u))
        drawPath(engrane(esc.center - Offset(0f, 2 * u), 7 * u, 8), p(LATON))
        drawCircle(c(mezcla(CARMESI, NEGRO, 0.25f)), 2.4f * u, esc.center - Offset(0f, 2 * u))
        drawPath(tapa, c(HIERRO), style = Stroke(2.5f * u))
        // El hueco del arcón y los despachos de pie en sus tres compartimentos.
        val boca = Rect(cofre.left + 4 * u, cofre.top - 2 * u, cofre.right - 4 * u, cofre.top + 22 * u)
        drawRect(c(NEGRO), boca.topLeft, boca.size)
        val grupos = listOf(Triple("LEVANTADOS", e.listas, 0), Triple("REMACHADOS", e.selladas, 1), Triple("LEÍDOS", e.abiertas, 2))
        val ancho = boca.width / 3f
        grupos.forEachIndexed { i, (_, n, tipo) ->
            val x0 = boca.left + i * ancho
            if (i > 0) drawRect(c(roble), Offset(x0 - 1.5f * u, boca.top - 4 * u), Size(3 * u, boca.height + 4 * u))
            val vis = n.coerceAtMost(8)
            val paso = (ancho - 16 * u) / 8f
            for (j in 0 until vis) {
                val dx = x0 + 8 * u + j * paso + paso * 0.1f
                val alto = (30 + 8 * azar(i * 7 + 3, j)) * u
                val r = Rect(dx, boca.bottom - alto, dx + paso * 0.82f, boca.bottom)
                rotate((azar(i * 5 + 1, j) - 0.5f) * 8f, Offset(r.center.x, r.bottom)) {
                    if (tipo == 2) drawRect(c(Color(0xFF1C2127)), Offset(r.left + 1.5f * u, r.top - 6 * u), Size(r.width - 3 * u, 8 * u))
                    drawRect(c(ACERO_LUZ), r.topLeft, r.size)
                    drawRect(p(LATON), r.topLeft, Size(r.width, 2 * u))
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
                    val so = Offset(r.center.x, r.top + 9 * u)
                    when (tipo) {
                        0 -> {
                            drawCircle(ALERTA.copy(alpha = 0.35f), r.width * 0.6f, so)
                            drawPath(hexagonoSello(so, r.width * 0.34f), mezcla(LATON_LUZ, ALERTA, 0.45f))
                        }
                        1 -> {
                            drawPath(hexagonoSello(so, r.width * 0.3f), p(LATON))
                            drawLine(c(Color(0xFFC9D0D8)), Offset(r.left - 1 * u, so.y - 3 * u), Offset(r.right + 1 * u, so.y + 5 * u), 1.4f * u)
                            drawLine(c(Color(0xFFC9D0D8)), Offset(r.left - 1 * u, so.y + 5 * u), Offset(r.right + 1 * u, so.y - 3 * u), 1.4f * u)
                        }
                        else -> drawPath(hexagonoSello(so + Offset(0f, 3 * u), r.width * 0.22f), p(LATON).copy(alpha = 0.6f))
                    }
                }
            }
            if (n > 8) textoEn(medidor, "+${n - 8}", Rect(x0, boca.top - 14 * u, x0 + ancho, boca.top - 2 * u), letra, p(LATON_LUZ), 9f)
        }
        // El frente del arcón: el roble, los flejes de hierro, las cantoneras y la cerradura.
        val frente = Rect(cofre.left, boca.bottom - 2 * u, cofre.right, cofre.bottom)
        drawRect(Color.Black.copy(alpha = 0.4f), frente.topLeft + Offset(3 * u, 4 * u), frente.size)
        drawRect(c(roble), frente.topLeft, frente.size)
        vetas(frente, c(mezcla(roble, NEGRO, 0.4f)), 17, densidad = 0.7f)
        drawRect(c(HIERRO), frente.topLeft, Size(frente.width, 5 * u))
        drawRect(c(ACERO_LUZ), frente.topLeft, Size(frente.width, 1 * u))
        drawRect(c(HIERRO), Offset(frente.left, frente.bottom - 6 * u), Size(frente.width, 6 * u))
        listOf(frente.left + 22 * u, frente.left + frente.width / 3f, frente.left + frente.width * 2f / 3f, frente.right - 22 * u).forEach { fx ->
            drawRect(c(HIERRO), Offset(fx - 3 * u, frente.top), Size(6 * u, frente.height))
            var yy = frente.top + 9 * u; while (yy < frente.bottom - 6 * u) { remache(Offset(fx, yy), 1.5f * u, oscuro, false); yy += 11 * u }
        }
        listOf(frente.left, frente.right).forEach { cx ->
            val sgn = if (cx == frente.left) 1f else -1f
            drawPath(Path().apply { moveTo(cx, frente.top); lineTo(cx + sgn * 14 * u, frente.top); lineTo(cx, frente.top + 14 * u); close() }, p(LATON))
            drawPath(Path().apply { moveTo(cx, frente.bottom); lineTo(cx + sgn * 14 * u, frente.bottom); lineTo(cx, frente.bottom - 14 * u); close() }, p(LATON))
        }
        // Las tres placas de latón con su nombre y su cuenta.
        grupos.forEachIndexed { i, (nombre, n, tipo) ->
            val cx = frente.left + frente.width * (i + 0.5f) / 3f
            val pl = Rect(cx - 44 * u, frente.top + 12 * u, cx + 44 * u, frente.bottom - 12 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.4f), pl.topLeft + Offset(1 * u, 1.5f * u), pl.size, CornerRadius(3 * u))
            drawRoundRect(p(LATON), pl.topLeft, pl.size, CornerRadius(3 * u))
            drawRoundRect(p(LATON_LUZ), pl.topLeft, Size(pl.width, 1.4f * u), CornerRadius(1 * u))
            drawRoundRect(c(COBRE), pl.deflate(2 * u).topLeft, pl.deflate(2 * u).size, CornerRadius(2 * u), style = Stroke(0.7f * u))
            listOf(pl.left + 4 * u, pl.right - 4 * u).forEach { rx -> remache(Offset(rx, pl.center.y), 1.4f * u, oscuro, false) }
            textoEn(medidor, nombre, Rect(pl.left + 8 * u, pl.top + 3 * u, pl.right - 8 * u, pl.top + pl.height * 0.4f), letra, NEGRO, 8f, espaciado = 0.5f, minimo = 5.5f)
            textoEn(medidor, n.toString(), Rect(pl.left + 8 * u, pl.top + pl.height * 0.36f, pl.right - 8 * u, pl.bottom - 2 * u), letra,
                if (tipo == 0 && n > 0) c(CARMESI) else NEGRO, 17f, peso = FontWeight.Bold)
        }
        // La cerradura, colgando del centro de la tapa.
        val cer = Offset(w / 2, frente.top + 2 * u)
        drawRoundRect(p(LATON), Offset(cer.x - 6 * u, cer.y - 2 * u), Size(12 * u, 9 * u), CornerRadius(2 * u))
        drawCircle(c(NEGRO), 1.6f * u, Offset(cer.x, cer.y + 2 * u))
        drawRect(c(NEGRO), Offset(cer.x - 0.6f * u, cer.y + 2 * u), Size(1.2f * u, 3.5f * u))
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /** La molécula: un átomo central y cuatro alrededor con sus enlaces (uno doble); de noche, la luna y dos estrellas fluorescentes. */
    private fun DrawScope.molecula(c: Offset, r: Float, tinta: Color, centro: Color, otros: Color, fondo: Color, noche: Boolean) {
        val u = density
        if (!noche) {
            val brazos = listOf(-150.0, -30.0, 90.0, 200.0)
            brazos.forEachIndexed { k, g ->
                val a = Math.toRadians(g)
                val o = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
                drawLine(tinta, c, o, 1.4f * u)
                if (k == 1) {
                    val n = Offset(-sin(a).toFloat(), cos(a).toFloat()) * (2.2f * u)
                    drawLine(tinta, c + n, o + n, 1 * u)
                }
                drawCircle(if (k % 2 == 0) otros else fondo, r * 0.26f, o)
                drawCircle(tinta, r * 0.26f, o, style = Stroke(1.1f * u))
            }
            drawCircle(centro, r * 0.36f, c)
            drawCircle(tinta, r * 0.36f, c, style = Stroke(1.2f * u))
            drawCircle(Color.White.copy(alpha = 0.6f), r * 0.1f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
        } else {
            drawCircle(centro, r * 0.7f, c)
            drawCircle(fondo, r * 0.6f, Offset(c.x + r * 0.34f, c.y - r * 0.2f))
            drawPath(estrella(Offset(c.x + r * 0.7f, c.y + r * 0.55f), r * 0.22f, 4, 0.35f), otros)
            drawPath(estrella(Offset(c.x + r * 0.95f, c.y - r * 0.6f), r * 0.15f, 4, 0.35f), FLUOR)
        }
    }

    /**
     * La cabecera impresa del cuaderno de laboratorio: el cuadro del formulario con la franja de color y el número del
     * protocolo, la casilla de la molécula (de noche, la luna bajo la luz negra), el campo de la fecha con su etiqueta, la
     * fecha grande y el detalle, y a la derecha las casillas de la página (el día del año) y de la firma con su garabato. De
     * noche fluorescen el marco y la molécula.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        val hoja = if (p.oscura) mezcla(p.color, Color.Black, 0.25f) else mezcla(Color.White, Color.Black, if (oscuro) 0.24f else 0f)
        val caja = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        drawRect(hoja, caja.topLeft, caja.size)
        if (e.noche) drawRect(Brush.radialGradient(listOf(UV.copy(alpha = 0.22f), Color.Transparent), center = Offset(28 * u, h * 0.6f), radius = w * 0.45f), caja.topLeft, caja.size)
        drawRect((if (e.noche) FLUOR else tinta).copy(alpha = 0.65f), caja.topLeft, caja.size, style = Stroke(1.2f * u))
        // La franja de color con el protocolo.
        val franja = Rect(caja.left, caja.top, caja.right, caja.top + 21 * u)
        drawRect(p.acento, franja.topLeft, franja.size)
        drawRect(p.detalle, Offset(franja.left, franja.bottom - 2 * u), Size(franja.width, 2 * u))
        textoEn(medidor, e.rotulo, Rect(franja.left + 8 * u, franja.top, franja.right - 8 * u, franja.bottom - 2 * u), LetraExtra.jost,
            if (p.oscura) p.color else Color.White, 9.5f, peso = FontWeight.Medium, espaciado = 0.3f, minimo = 6f)
        // La casilla de la molécula.
        val celda = Rect(caja.left, franja.bottom, caja.left + 56 * u, caja.bottom)
        drawLine(tinta.copy(alpha = 0.4f), Offset(celda.right, celda.top), Offset(celda.right, celda.bottom), 0.8f * u)
        molecula(celda.center, minOf(celda.width, celda.height) * 0.3f, tinta.copy(alpha = 0.85f), if (e.noche) FLUOR_ROSA else p.acento, p.detalle, hoja, e.noche)
        // Las casillas de la derecha: la página y la firma.
        val col = Rect(caja.right - 58 * u, franja.bottom, caja.right, caja.bottom)
        drawLine(tinta.copy(alpha = 0.4f), Offset(col.left, col.top), Offset(col.left, col.bottom), 0.8f * u)
        val mitad = col.top + col.height * 0.52f
        drawLine(tinta.copy(alpha = 0.4f), Offset(col.left, mitad), Offset(col.right, mitad), 0.8f * u)
        textoEn(medidor, "PÁG.", Rect(col.left + 4 * u, col.top + 2 * u, col.right - 4 * u, col.top + 13 * u), LetraExtra.jost, p.tenue, 7.5f, espaciado = 1f, minimo = 5f)
        textoEn(medidor, e.numero.toString(), Rect(col.left + 4 * u, col.top + 12 * u, col.right - 4 * u, mitad - 2 * u), LetraExtra.jost, p.acento, 18f, peso = FontWeight.Medium, minimo = 8f)
        textoEn(medidor, "FIRMA", Rect(col.left + 4 * u, mitad + 2 * u, col.right - 4 * u, mitad + 13 * u), LetraExtra.jost, p.tenue, 7.5f, espaciado = 1f, minimo = 5f)
        val fy = col.bottom - 10 * u
        drawPath(Path().apply {
            moveTo(col.left + 8 * u, fy); cubicTo(col.left + 14 * u, fy - 10 * u, col.left + 18 * u, fy + 4 * u, col.left + 24 * u, fy - 4 * u)
            cubicTo(col.left + 30 * u, fy - 12 * u, col.left + 32 * u, fy + 2 * u, col.left + 40 * u, fy - 3 * u); lineTo(col.right - 8 * u, fy - 1 * u)
        }, tinta.copy(alpha = 0.55f), style = Stroke(1 * u, cap = StrokeCap.Round))
        drawLine(tinta.copy(alpha = 0.3f), Offset(col.left + 6 * u, fy + 3 * u), Offset(col.right - 6 * u, fy + 3 * u), 0.6f * u)
        // El campo de la fecha.
        val cuerpo = Rect(celda.right + 8 * u, franja.bottom + 3 * u, col.left - 8 * u, caja.bottom - 3 * u)
        textoEn(medidor, if (e.noche) "NOCHE" else "FECHA", Rect(cuerpo.left, cuerpo.top, cuerpo.left + 40 * u, cuerpo.top + 11 * u), LetraExtra.jost, p.tenue, 7.5f, espaciado = 1.2f, minimo = 5f)
        fechaFuturo(medidor, e.fecha, Rect(cuerpo.left, cuerpo.top + 9 * u, cuerpo.right, cuerpo.top + 54 * u), LetraExtra.jost, tinta, 22f, peso = FontWeight.Medium)
        drawLine(tinta.copy(alpha = 0.35f), Offset(cuerpo.left, cuerpo.top + 56 * u), Offset(cuerpo.right, cuerpo.top + 56 * u), 0.6f * u)
        fechaFuturo(medidor, e.detalle, Rect(cuerpo.left, cuerpo.top + 58 * u, cuerpo.right, cuerpo.bottom), LetraExtra.jost, p.tenue, 10.5f, k = 0.86f)
    }

    /**
     * La hoja del cuaderno de laboratorio: el lomo cosido con sus puntadas, el doble margen rosa, la casilla del número de
     * página al pie, la regla milimétrica del borde derecho, el código de la muestra en una esquina y, al pie, las líneas de firma y
     * de testigo con su cruz. En modo oscuro la luz es la del flexo de la mesa, fría, desde arriba a la izquierda; de noche, la
     * luz negra: el polvo fluoresce.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El lomo cosido.
        var y = 10 * u
        while (y < h) { drawLine(p.tinta.copy(alpha = 0.2f), Offset(7 * u, y), Offset(7 * u, y + 5 * u), 1.2f * u); y += 11 * u }
        // El doble margen y la casilla del número de página.
        listOf(24 * u, 27 * u).forEach { x -> drawLine(p.detalle.copy(alpha = 0.4f), Offset(x, 0f), Offset(x, h), 1 * u) }
        drawRect(p.acento.copy(alpha = 0.45f), Offset(w - 84 * u, h - 74 * u), Size(58 * u, 22 * u), style = Stroke(1.2f * u))
        drawLine(p.acento.copy(alpha = 0.3f), Offset(w - 62 * u, h - 74 * u), Offset(w - 62 * u, h - 52 * u), 0.8f * u)
        // La regla milimétrica del borde derecho.
        y = 96 * u; var k = 0
        while (y < h - 56 * u) {
            val largo = when { k % 10 == 0 -> 9f; k % 5 == 0 -> 6f; else -> 3.5f } * u
            drawLine(p.tenue.copy(alpha = if (k % 10 == 0) 0.4f else 0.22f), Offset(w - 4 * u - largo, y), Offset(w - 4 * u, y), 0.7f * u)
            y += 3 * u; k++
        }
        // El código de la muestra, abajo a la izquierda.
        val q = Offset(34 * u, h - 74 * u); val cel = 2.6f * u
        for (i in 0 until 9) for (j in 0 until 9) {
            val borde = i == 0 || j == 0 || (i == 8 && j % 2 == 0) || (j == 8 && i % 2 == 0)
            if (borde || azar(i * 9 + j, 77) > 0.55f) drawRect(p.tinta.copy(alpha = 0.32f), Offset(q.x + i * cel, q.y + j * cel), Size(cel, cel))
        }
        // Las líneas de firma y de testigo.
        val yf = h - 30 * u
        drawLine(p.tenue.copy(alpha = 0.5f), Offset(40 * u, yf), Offset(w * 0.46f, yf), 1 * u)
        drawLine(p.tenue.copy(alpha = 0.5f), Offset(w * 0.54f, yf), Offset(w - 26 * u, yf), 1 * u)
        listOf(40 * u, w * 0.54f).forEach { x ->
            drawLine(p.tenue.copy(alpha = 0.55f), Offset(x, yf - 9 * u), Offset(x + 5 * u, yf - 4 * u), 1 * u)
            drawLine(p.tenue.copy(alpha = 0.55f), Offset(x + 5 * u, yf - 9 * u), Offset(x, yf - 4 * u), 1 * u)
        }
        // La luz.
        if (oscuro && !p.oscura) penumbraFuturo(Offset(w * 0.4f, h * 0.28f), Color(0xFFE6F4FF), velo = 0.2f, orilla = 0.6f)
        if (noche) for (j in 0 until 40) drawCircle((if (j % 3 == 0) FLUOR_ROSA else FLUOR).copy(alpha = 0.12f + 0.2f * azar(61, j)), (0.6f + 1.2f * azar(63, j)) * u, Offset(w * azar(65, j), h * azar(67, j)))
    }

    /** La regla del borde de arriba: el papel con su escala milimétrica a lo largo del pie y la franja de color del cuaderno. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(fondoBarraFuturo(p, GRIS, 0.14f, oscuro))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.06f)), h * 0.5f, h), Offset(0f, h * 0.5f), Size(w, h * 0.5f))
        var x = 0f; var k = 0
        while (x < w) {
            val largo = when { k % 10 == 0 -> 8f; k % 5 == 0 -> 5.5f; else -> 3f } * u
            drawLine(p.tenue.copy(alpha = if (k % 10 == 0) 0.55f else 0.3f), Offset(x, h - 3 * u - largo), Offset(x, h - 3 * u), 0.7f * u)
            x += 3 * u; k++
        }
        drawRect(p.acento, Offset(0f, h - 2.5f * u), Size(w, 2.5f * u))
    }

    /** La tecla de un instrumento: una píldora de color con su brillo arriba, el canto más oscuro y un piloto encendido. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val base = mezcla(p.acento, Color.Black, if (noche) 0.45f else 0.12f)
        val r = Rect(2 * u, 2 * u, w - 2 * u, h - 4 * u)
        val rad = CornerRadius(r.height / 2)
        drawRoundRect(mezcla(base, Color.Black, 0.35f), Offset(r.left, r.top + 2 * u), r.size, rad)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(base, Color.White, 0.18f), base), r.top, r.bottom), r.topLeft, r.size, rad)
        drawRoundRect(Color.White.copy(alpha = 0.3f), Offset(r.left + r.height * 0.4f, r.top + 2 * u), Size(r.width - r.height * 0.8f, 2 * u), CornerRadius(1 * u))
        drawCircle(if (noche) FLUOR else p.detalle, 2 * u, Offset(r.left + r.height * 0.32f, r.center.y))
        drawCircle(Color.White.copy(alpha = 0.6f), 0.8f * u, Offset(r.left + r.height * 0.32f - 0.6f * u, r.center.y - 0.6f * u))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color.White

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 262.dp

    /** La llama del mechero: el cono de dentro y el de fuera. */
    private val LLAMA_AZUL = Color(0xFF4A8CFF)
    private val LLAMA_LUZ = Color(0xFFB8D6FF)
    private val GOMA = Color(0xFFD2742E)

    /**
     * La mesa del laboratorio: la pared de azulejo blanco con el escurridor de clavijas donde se secan matraces y probetas boca
     * abajo y el triángulo amarillo de aviso; la cubierta de resina negra sobre el mueble de puertas blancas. A la izquierda, el
     * mechero con su llama azul bajo el trípode y la rejilla, con el vaso de precipitados que hierve y la manguera naranja que
     * va a la llave del gas; a la derecha, la gradilla con sus tubos de colores, el matraz violeta y las gafas de seguridad. El
     * cuaderno de laboratorio va al centro: tapa dura blanca con lomo de tela cian, la etiqueta con su franja (el nombre y la
     * cuenta), la molécula impresa y las tiras de cinta de muestras que asoman por el canto, más cuantas más hojas. Al abrirse,
     * la tapa gira sobre el lomo: dentro, la tabla de pictogramas de riesgo y la hoja cuadriculada con el anillo de benceno, la
     * tabla de datos y la gráfica. En oscuro, el laboratorio se apaga y alumbra la llama.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF05070A), 0.55f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val blanco = papel(oscuro, false)
        val fondoY = h * 0.48f
        val frenteY = h * 0.83f
        // ---- La pared de azulejo.
        drawRect(f(AZULEJO), Offset.Zero, Size(w, fondoY))
        val az = 18 * u
        var x = 0f; while (x < w) { drawLine(f(JUNTA), Offset(x, 0f), Offset(x, fondoY), 1 * u); x += az }
        var y = fondoY; while (y > 0f) { drawLine(f(JUNTA), Offset(0f, y), Offset(w, y), 1 * u); y -= az }
        for (k in 0 until 8) drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.5f), Offset(az * (k * 3 + 1) + 3 * u, az * (k % 4) + 3 * u), Offset(az * (k * 3 + 1) + 9 * u, az * (k % 4) + 3 * u), 1 * u)
        // El escurridor de clavijas, con el vidrio boca abajo.
        val esc = Rect(w * 0.56f, h * 0.05f, w * 0.95f, h * 0.36f)
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(esc.left + 2 * u, esc.top + 2 * u), esc.size)
        drawRect(f(Color(0xFFE9EEF1)), esc.topLeft, esc.size)
        drawRect(f(GRIS), esc.topLeft, esc.size, style = Stroke(1.2f * u))
        drawRect(f(GRIS), Offset(esc.left, esc.bottom - 6 * u), Size(esc.width, 6 * u))
        val vidrio = Color.White.copy(alpha = if (oscuro) 0.1f else 0.55f)
        val canto = f(GRIS_OSC).copy(alpha = 0.6f)
        for (fila in 0 until 2) {
            val yy = esc.top + 10 * u + fila * esc.height * 0.42f
            for (col in 0 until 6) {
                val cxp = esc.left + esc.width * (col + 0.5f) / 6f + (if (fila == 1) esc.width / 12f else 0f)
                if (cxp > esc.right - 4 * u) continue
                drawLine(f(GRIS_OSC), Offset(cxp, yy - 2 * u), Offset(cxp, yy + 8 * u), 1.4f * u)
                if ((col + fila) % 3 == 0) {
                    // Un matraz boca abajo.
                    val p = Path().apply { moveTo(cxp - 2 * u, yy); lineTo(cxp + 2 * u, yy); lineTo(cxp + 2 * u, yy + 6 * u); lineTo(cxp + 9 * u, yy + 22 * u); lineTo(cxp - 9 * u, yy + 22 * u); lineTo(cxp - 2 * u, yy + 6 * u); close() }
                    drawPath(p, vidrio); drawPath(p, canto, style = Stroke(0.8f * u))
                } else if ((col + fila) % 3 == 1) {
                    // Una probeta.
                    drawRect(vidrio, Offset(cxp - 3 * u, yy), Size(6 * u, 24 * u)); drawRect(canto, Offset(cxp - 3 * u, yy), Size(6 * u, 24 * u), style = Stroke(0.8f * u))
                    for (j in 1..4) drawLine(canto, Offset(cxp - 3 * u, yy + j * 4.5f * u), Offset(cxp - 0.5f * u, yy + j * 4.5f * u), 0.5f * u)
                }
            }
        }
        // El triángulo amarillo de aviso.
        val tri = Offset(w * 0.44f, h * 0.15f)
        drawPath(Path().apply { moveTo(tri.x, tri.y - 13 * u); lineTo(tri.x + 14 * u, tri.y + 11 * u); lineTo(tri.x - 14 * u, tri.y + 11 * u); close() }, f(Color(0xFF1A1A1A)))
        drawPath(Path().apply { moveTo(tri.x, tri.y - 9 * u); lineTo(tri.x + 10.5f * u, tri.y + 8.5f * u); lineTo(tri.x - 10.5f * u, tri.y + 8.5f * u); close() }, f(AMARILLO))
        drawRect(f(Color(0xFF1A1A1A)), Offset(tri.x - 1 * u, tri.y - 4 * u), Size(2 * u, 7 * u))
        drawCircle(f(Color(0xFF1A1A1A)), 1.2f * u, Offset(tri.x, tri.y + 5.5f * u))

        // ---- La mesa: el mueble blanco con sus puertas y la cubierta de resina negra (el primer degradado).
        drawRect(f(MUEBLE), Offset(0f, frenteY), Size(w, h - frenteY))
        val puertas = 4
        for (k in 0 until puertas) {
            val px = w * k / puertas
            drawRect(f(GRIS), Offset(px + 2 * u, frenteY + 14 * u), Size(w / puertas - 4 * u, h - frenteY - 14 * u), style = Stroke(1 * u))
            drawRoundRect(f(GRIS_OSC), Offset(px + (if (k % 2 == 0) w / puertas - 10 * u else 6 * u), frenteY + 20 * u), Size(3 * u, 12 * u), CornerRadius(1.5f * u))
        }
        drawRect(Brush.verticalGradient(listOf(c(mezcla(EPOXI, Color.White, 0.14f)), c(EPOXI)), fondoY, frenteY), Offset(0f, fondoY), Size(w, frenteY - fondoY))
        drawRect(c(mezcla(EPOXI, Color.White, 0.08f)), Offset(0f, frenteY), Size(w, 9 * u))
        drawRect(Color.White.copy(alpha = 0.2f), Offset(0f, frenteY), Size(w, 1 * u))
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(0f, frenteY + 9 * u), Size(w, 2 * u))
        drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, fondoY), Offset(w, fondoY), 1.2f * u)
        // La llave del gas, atrás a la izquierda.
        val llave = Offset(w * 0.04f, fondoY + 6 * u)
        drawRect(c(GRIS), Offset(llave.x - 3 * u, fondoY - 22 * u), Size(6 * u, 26 * u))
        drawRect(c(AMARILLO), Offset(llave.x - 2 * u, fondoY - 18 * u), Size(10 * u, 3 * u))
        drawCircle(c(GRIS_OSC), 3 * u, Offset(llave.x, fondoY - 6 * u))

        // ---- El mechero, la manguera, el trípode con la rejilla y el vaso que hierve; la luz de la llama (el segundo degradado).
        val me = Offset(w * 0.15f, frenteY - 24 * u)
        val llama = Offset(me.x, me.y - 40 * u)
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB46A).copy(alpha = if (oscuro) 0.34f else 0.14f), LLAMA_AZUL.copy(alpha = if (oscuro) 0.1f else 0.04f), Color.Transparent), llama, w * 0.55f), w * 0.55f, llama)
        drawPath(Path().apply { moveTo(llave.x + 2 * u, fondoY - 6 * u); cubicTo(llave.x + 6 * u, fondoY + 30 * u, me.x - 30 * u, me.y + 10 * u, me.x - 7 * u, me.y + 1 * u) }, c(GOMA), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(me.x - 16 * u, me.y + 4 * u), Size(32 * u, 7 * u))
        drawOval(c(GRIS_OSC), Offset(me.x - 14 * u, me.y - 2 * u), Size(28 * u, 9 * u))
        drawRect(c(GRIS), Offset(me.x - 3.5f * u, me.y - 30 * u), Size(7 * u, 30 * u))
        drawRect(Color.White.copy(alpha = 0.4f), Offset(me.x - 2.5f * u, me.y - 30 * u), Size(1.2f * u, 30 * u))
        drawRect(c(GRIS_OSC), Offset(me.x - 4.5f * u, me.y - 12 * u), Size(9 * u, 5 * u))
        drawPath(Path().apply { moveTo(me.x - 3.5f * u, me.y - 30 * u); quadraticTo(me.x - 5 * u, me.y - 42 * u, me.x, me.y - 54 * u); quadraticTo(me.x + 5 * u, me.y - 42 * u, me.x + 3.5f * u, me.y - 30 * u); close() }, LLAMA_AZUL.copy(alpha = 0.55f))
        drawPath(Path().apply { moveTo(me.x - 2.2f * u, me.y - 30 * u); quadraticTo(me.x - 2.6f * u, me.y - 37 * u, me.x, me.y - 42 * u); quadraticTo(me.x + 2.6f * u, me.y - 37 * u, me.x + 2.2f * u, me.y - 30 * u); close() }, LLAMA_LUZ)
        // El trípode y la rejilla.
        val rej = me.y - 52 * u
        drawLine(c(GRIS_OSC), Offset(me.x - 18 * u, rej), Offset(me.x - 24 * u, me.y + 2 * u), 1.6f * u)
        drawLine(c(GRIS_OSC), Offset(me.x + 18 * u, rej), Offset(me.x + 24 * u, me.y + 2 * u), 1.6f * u)
        drawLine(c(GRIS_OSC), Offset(me.x + 2 * u, rej), Offset(me.x + 3 * u, me.y + 5 * u), 1.4f * u)
        drawRect(c(GRIS), Offset(me.x - 20 * u, rej - 2 * u), Size(40 * u, 3 * u))
        drawRect(c(Color(0xFFE6E0D6)), Offset(me.x - 9 * u, rej - 2 * u), Size(18 * u, 3 * u))
        // El vaso de precipitados con el líquido cian que hierve.
        val va = Rect(me.x - 13 * u, rej - 34 * u, me.x + 13 * u, rej - 2 * u)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), va.topLeft, va.size)
        drawRect(c(CIAN).copy(alpha = 0.7f), Offset(va.left + 1 * u, va.top + va.height * 0.4f), Size(va.width - 2 * u, va.height * 0.6f - 1 * u))
        for (k in 0 until 6) drawCircle(Color.White.copy(alpha = 0.6f), (0.8f + azar(301, k)) * u, Offset(va.left + va.width * azar(303, k), va.top + va.height * (0.45f + 0.5f * azar(307, k))), style = Stroke(0.6f * u))
        drawRect(Color.White.copy(alpha = 0.7f), va.topLeft, va.size, style = Stroke(1 * u))
        drawPath(Path().apply { moveTo(va.left - 3 * u, va.top); lineTo(va.left + 3 * u, va.top); lineTo(va.left, va.top + 3 * u); close() }, Color.White.copy(alpha = 0.7f))
        for (j in 1..3) drawLine(Color.White.copy(alpha = 0.6f), Offset(va.right - 7 * u, va.top + j * 7 * u), Offset(va.right - 2 * u, va.top + j * 7 * u), 0.6f * u)
        for (j in 0 until 2) drawPath(Path().apply { moveTo(va.center.x - 4 * u + j * 8 * u, va.top - 2 * u); cubicTo(va.center.x - 10 * u + j * 8 * u, va.top - 12 * u, va.center.x + 2 * u + j * 8 * u, va.top - 20 * u, va.center.x - 3 * u + j * 8 * u, va.top - 30 * u) },
            Color.White.copy(alpha = 0.22f), style = Stroke(2.4f * u, cap = StrokeCap.Round))

        // ---- La gradilla con sus tubos, el matraz violeta y las gafas, a la derecha.
        val gr = Rect(w * 0.79f, fondoY + 8 * u, w * 0.97f, fondoY + 26 * u)
        val tubos = listOf(CIAN, ROJO, FLUOR, AMARILLO, VIOLETA)
        tubos.forEachIndexed { k, col ->
            val tx = gr.left + gr.width * (k + 0.5f) / tubos.size
            drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.45f), Offset(tx - 3 * u, gr.top - 20 * u), Size(6 * u, 34 * u), CornerRadius(3 * u))
            drawRoundRect(c(col).copy(alpha = 0.85f), Offset(tx - 2.4f * u, gr.top - 2 * u + 6 * u * azar(311, k)), Size(4.8f * u, 15 * u - 6 * u * azar(311, k)), CornerRadius(2.4f * u))
        }
        drawRect(c(BLANCO), Offset(gr.left - 2 * u, gr.top + 2 * u), Size(gr.width + 4 * u, 4 * u))
        drawRect(c(GRIS), Offset(gr.left - 2 * u, gr.bottom - 4 * u), Size(gr.width + 4 * u, 4 * u))
        listOf(gr.left - 2 * u, gr.right).forEach { xx -> drawRect(c(GRIS), Offset(xx, gr.top + 2 * u), Size(2 * u, gr.height - 2 * u)) }
        val mt = Offset(w * 0.885f, frenteY - 12 * u)
        val matraz = Path().apply { moveTo(mt.x - 4 * u, mt.y - 46 * u); lineTo(mt.x + 4 * u, mt.y - 46 * u); lineTo(mt.x + 4 * u, mt.y - 30 * u); lineTo(mt.x + 18 * u, mt.y); lineTo(mt.x - 18 * u, mt.y); lineTo(mt.x - 4 * u, mt.y - 30 * u); close() }
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(mt.x - 20 * u, mt.y - 3 * u), Size(40 * u, 7 * u))
        drawPath(matraz, Color.White.copy(alpha = if (oscuro) 0.1f else 0.35f))
        drawPath(Path().apply { moveTo(mt.x - 10.5f * u, mt.y - 16 * u); lineTo(mt.x + 10.5f * u, mt.y - 16 * u); lineTo(mt.x + 17 * u, mt.y - 1 * u); lineTo(mt.x - 17 * u, mt.y - 1 * u); close() }, c(VIOLETA).copy(alpha = 0.85f))
        drawPath(matraz, Color.White.copy(alpha = 0.75f), style = Stroke(1 * u))
        drawRect(c(CORCHO), Offset(mt.x - 4.5f * u, mt.y - 52 * u), Size(9 * u, 7 * u))
        drawLine(Color.White.copy(alpha = 0.7f), Offset(mt.x - 8 * u, mt.y - 20 * u), Offset(mt.x - 12 * u, mt.y - 8 * u), 1.4f * u)
        // Las gafas de seguridad.
        val ga = Offset(w * 0.11f, frenteY - 2 * u)
        drawLine(c(Color(0xFF2E3A44)), Offset(ga.x - 22 * u, ga.y - 2 * u), Offset(ga.x + 22 * u, ga.y - 4 * u), 2 * u)
        listOf(-9f, 9f).forEach { s ->
            drawRoundRect(c(Color(0xFF9FD8E8)).copy(alpha = 0.55f), Offset(ga.x + s * u - 8 * u, ga.y - 9 * u), Size(16 * u, 10 * u), CornerRadius(4 * u))
            drawRoundRect(c(Color(0xFF2E3A44)), Offset(ga.x + s * u - 8 * u, ga.y - 9 * u), Size(16 * u, 10 * u), CornerRadius(4 * u), style = Stroke(1.4f * u))
        }

        // ---- El cuaderno de laboratorio.
        val g = grosorLibreta(e.hojas, u, minimo = 3f)
        val ancho = w * 0.48f
        val lomo = 11 * u
        val angulo = giroTapa(a)
        val corre = ancho * 0.2f * (1f - cos(angulo)) / 2f
        val xl = w * 0.28f + corre
        val x0 = xl + lomo; val x1 = xl + ancho
        val fy = frenteY - 10 * u
        val hondo = h * 0.24f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val tinta = TINTA
        sombraSuave(Rect(xl, fy - hondo, x1, fy), 2 * u, 9 * u, if (oscuro) 0.45f else 0.3f)
        drawRect(c(GRIS), Offset(xl, fy - hondo), Size(ancho, hondo))
        // Las tiras de la cinta de muestras, por el canto: más cuantas más hojas.
        val tiras = (1 + e.hojas / 18).coerceAtMost(8)
        val colores = listOf(CIAN, VIOLETA, Color(0xFF1E8E5A), Color(0xFFD9572B), AMARILLO, ROJO)
        for (k in 0 until tiras) {
            val ty = arriba + 3 * u + (hondo - 10 * u) * ((k + 0.5f) / tiras) + 2 * u * (azar(e.semilla, 60 + k) - 0.5f)
            val sale = (5 + 4 * azar(e.semilla, 70 + k)) * u
            drawRect(c(colores[k % colores.size]), Offset(x1 - 4 * u, ty), Size(sale + 4 * u, 5 * u))
            drawLine(Color.White.copy(alpha = 0.8f), Offset(x1 + 1 * u, ty + 2.5f * u), Offset(x1 + sale - 1 * u, ty + 2.5f * u), 0.8f * u)
        }
        cantoHojas(Rect(x0, tapaY + 2 * u, x1 - 1.5f * u, fy - 2 * u), mezcla(blanco, Color.Black, 0.04f), tinta, e.semilla)
        drawRect(c(GRIS_OSC), Offset(xl, fy - 2 * u), Size(ancho, 2 * u))
        // El lomo de tela cian.
        drawRect(c(CIAN), Offset(xl, arriba), Size(lomo, fy - arriba))
        drawRect(Color.White.copy(alpha = 0.18f), Offset(xl + 2 * u, arriba), Size(1.4f * u, fy - arriba))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(xl + lomo - 2 * u, arriba), Size(2 * u, fy - arriba))
        var ty = arriba; while (ty < fy) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(xl, ty), Offset(xl + lomo, ty), 0.5f * u); ty += 2 * u }
        // La hoja cuadriculada, que se ve al abrir.
        if (angulo > 0.01f) {
            val r = Rect(x0 + 2 * u, arriba + 2 * u, x1 - 3 * u, tapaY + 1 * u)
            drawRect(blanco, r.topLeft, r.size)
            var gx = r.left + 4 * u; while (gx < r.right) { drawLine(c(CIAN).copy(alpha = 0.22f), Offset(gx, r.top), Offset(gx, r.bottom), 0.5f * u); gx += 5 * u }
            var gy = r.top + 4 * u; while (gy < r.bottom) { drawLine(c(CIAN).copy(alpha = 0.22f), Offset(r.left, gy), Offset(r.right, gy), 0.5f * u); gy += 5 * u }
            textoEn(medidor, "EXP. ${e.hojas}", Rect(r.left + 6 * u, r.top + 3 * u, r.left + r.width * 0.36f, r.top + 13 * u), letra, tinta, 9f, peso = FontWeight.Medium, minimo = 5f)
            drawLine(tinta.copy(alpha = 0.5f), Offset(r.left + r.width * 0.38f, r.top + 9 * u), Offset(r.right - 8 * u, r.top + 9 * u), 0.8f * u)
            // El anillo de benceno.
            val ce = Offset(r.left + r.width * 0.2f, r.center.y + 6 * u)
            val rb = minOf(r.height * 0.22f, 13 * u)
            val hexa = Path().apply { for (j in 0 until 6) { val an = PI.toFloat() / 3 * j - PI.toFloat() / 2; val p = Offset(ce.x + rb * cos(an), ce.y + rb * sin(an)); if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
            drawPath(hexa, tinta.copy(alpha = 0.8f), style = Stroke(1 * u))
            drawCircle(tinta.copy(alpha = 0.6f), rb * 0.55f, ce, style = Stroke(0.8f * u))
            drawLine(tinta.copy(alpha = 0.8f), Offset(ce.x, ce.y - rb), Offset(ce.x, ce.y - rb - 6 * u), 1 * u)
            // La tabla de datos y la gráfica.
            val tb = Rect(r.left + r.width * 0.38f, r.top + 16 * u, r.left + r.width * 0.64f, r.bottom - 6 * u)
            drawRect(tinta.copy(alpha = 0.6f), tb.topLeft, tb.size, style = Stroke(0.7f * u))
            drawLine(tinta.copy(alpha = 0.6f), Offset(tb.center.x, tb.top), Offset(tb.center.x, tb.bottom), 0.6f * u)
            var fyy = tb.top + 6 * u; var j = 0
            while (fyy < tb.bottom - 2 * u) { drawLine(tinta.copy(alpha = 0.3f), Offset(tb.left, fyy), Offset(tb.right, fyy), 0.5f * u); drawLine(c(Color(0xFF2A3F7A)).copy(alpha = 0.6f), Offset(tb.left + 2 * u, fyy - 2.5f * u), Offset(tb.left + 2 * u + (tb.width / 2 - 5 * u) * azar(e.semilla, 140 + j), fyy - 2.5f * u), 0.8f * u); fyy += 6 * u; j++ }
            val gf = Rect(r.left + r.width * 0.69f, r.top + 16 * u, r.right - 6 * u, r.bottom - 8 * u)
            drawLine(tinta.copy(alpha = 0.7f), Offset(gf.left, gf.top), Offset(gf.left, gf.bottom), 0.8f * u)
            drawLine(tinta.copy(alpha = 0.7f), Offset(gf.left, gf.bottom), Offset(gf.right, gf.bottom), 0.8f * u)
            drawPath(Path().apply { moveTo(gf.left, gf.bottom); quadraticTo(gf.left + gf.width * 0.5f, gf.bottom - gf.height * 0.05f, gf.right, gf.top + gf.height * 0.1f) }, c(ROJO), style = Stroke(1.1f * u))
            for (k in 0 until 5) drawCircle(c(Color(0xFF2A3F7A)), 1.2f * u, Offset(gf.left + gf.width * (k + 1) / 6f, gf.bottom - gf.height * ((k + 1) / 6f) * ((k + 1) / 6f) * 0.95f - 2 * u * (azar(e.semilla, 160 + k) - 0.5f)))
            if (cos(angulo) > 0f) sombraTapa(x0 + (x1 - x0) * cos(angulo), arriba, (x1 - x0) * 0.24f * sin(angulo), tapaY - arriba)
        }
        // La tapa blanca, que gira sobre el lomo.
        tapaLateral(x0, angulo, 0.42f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            drawRect(c(BLANCO), r.topLeft, r.size)
            drawRect(c(GRIS), Offset(r.left, r.bottom), Size(r.width, 2 * u))
            drawRect(c(GRIS).copy(alpha = 0.5f), Offset(r.right - 2 * u, r.top), Size(2 * u, r.height))
            // La etiqueta: la franja cian, el nombre y la cuenta.
            val et = Rect(r.left + 10 * u, r.top + r.height * 0.14f, r.left + r.width * 0.72f, r.top + r.height * 0.72f)
            drawRect(blanco, et.topLeft, et.size)
            drawRect(tinta.copy(alpha = 0.55f), et.topLeft, et.size, style = Stroke(1 * u))
            drawRect(c(CIAN), et.topLeft, Size(et.width, et.height * 0.16f))
            textoEn(medidor, "CUADERNO Nº " + (1 + (e.semilla and 0x7fffffff) % 40), Rect(et.left, et.top, et.right, et.top + et.height * 0.16f), letra, Color.White, 7f, peso = FontWeight.Medium, espaciado = 0.6f, minimo = 4.5f)
            val corte = et.top + et.height * 0.62f
            textoEn(medidor, e.nombre, Rect(et.left + 5 * u, et.top + et.height * 0.18f, et.right - 5 * u, corte), letra, tinta, 12.5f, peso = FontWeight.Medium, minimo = 6f)
            drawLine(tinta.copy(alpha = 0.3f), Offset(et.left + 5 * u, corte), Offset(et.right - 5 * u, corte), 0.6f * u)
            textoEn(medidor, e.cuenta, Rect(et.left + 5 * u, corte, et.right - 5 * u, et.bottom - 2 * u), letra, tinta.copy(alpha = 0.8f), 9f, minimo = 5f)
            // La molécula impresa y el rombo de riesgo.
            molecula(Offset(r.left + r.width * 0.86f, r.top + r.height * 0.36f), minOf(r.width * 0.08f, r.height * 0.2f), tinta.copy(alpha = 0.7f), c(CIAN), c(VIOLETA), c(BLANCO), false)
            val rb = Offset(r.left + r.width * 0.86f, r.top + r.height * 0.78f)
            val rr = 7 * u
            drawPath(Path().apply { moveTo(rb.x, rb.y - rr); lineTo(rb.x + rr, rb.y); lineTo(rb.x, rb.y + rr); lineTo(rb.x - rr, rb.y); close() }, c(BLANCO))
            drawPath(Path().apply { moveTo(rb.x, rb.y - rr); lineTo(rb.x + rr, rb.y); lineTo(rb.x, rb.y + rr); lineTo(rb.x - rr, rb.y); close() }, c(ROJO), style = Stroke(1.4f * u))
            drawRect(tinta, Offset(rb.x - 0.8f * u, rb.y - 3.5f * u), Size(1.6f * u, 4.5f * u))
            drawCircle(tinta, 0.9f * u, Offset(rb.x, rb.y + 2.5f * u))
            // La franja de precaución, abajo.
            clipRect(r.left, r.bottom - r.height * 0.12f, r.right, r.bottom - r.height * 0.04f) {
                drawRect(c(AMARILLO), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.08f))
                var sx = r.left - 10 * u
                while (sx < r.right) { drawPath(Path().apply { moveTo(sx, r.bottom); lineTo(sx + 3 * u, r.bottom); lineTo(sx + 13 * u, r.bottom - r.height * 0.2f); lineTo(sx + 10 * u, r.bottom - r.height * 0.2f); close() }, c(Color(0xFF1A1A1A))); sx += 6 * u }
            }
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, Size(r.width, r.height + 2 * u))
        }, dentro = { sombra ->
            // Por dentro: la guarda gris con la tabla de pictogramas de riesgo.
            val r = Rect(x0 - (x1 - x0), arriba, x0, tapaY + 2 * u)
            drawRect(c(Color(0xFFE6EBEF)), r.topLeft, r.size)
            textoEn(medidor, "RIESGOS", Rect(r.left + 8 * u, r.top + 3 * u, r.right - 8 * u, r.top + 13 * u), letra, tinta, 8f, peso = FontWeight.Medium, espaciado = 1f, minimo = 5f)
            for (j in 0 until 4) {
                val p = Offset(r.left + r.width * (0.16f + 0.22f * j), r.top + r.height * 0.48f)
                val rr = minOf(r.width * 0.08f, 11 * u)
                val rombo = Path().apply { moveTo(p.x, p.y - rr); lineTo(p.x + rr, p.y); lineTo(p.x, p.y + rr); lineTo(p.x - rr, p.y); close() }
                drawPath(rombo, Color.White); drawPath(rombo, c(ROJO), style = Stroke(1.3f * u))
                when (j) {
                    0 -> drawPath(Path().apply { moveTo(p.x, p.y - rr * 0.5f); quadraticTo(p.x + rr * 0.4f, p.y, p.x, p.y + rr * 0.45f); quadraticTo(p.x - rr * 0.4f, p.y, p.x, p.y - rr * 0.5f) }, tinta)
                    1 -> { drawRect(tinta, Offset(p.x - 0.8f * u, p.y - rr * 0.45f), Size(1.6f * u, rr * 0.55f)); drawCircle(tinta, 0.9f * u, Offset(p.x, p.y + rr * 0.3f)) }
                    2 -> drawCircle(tinta, rr * 0.3f, p, style = Stroke(1 * u))
                    else -> { drawLine(tinta, Offset(p.x - rr * 0.35f, p.y - rr * 0.35f), Offset(p.x + rr * 0.35f, p.y + rr * 0.35f), 1.2f * u); drawLine(tinta, Offset(p.x + rr * 0.35f, p.y - rr * 0.35f), Offset(p.x - rr * 0.35f, p.y + rr * 0.35f), 1.2f * u) }
                }
            }
            for (j in 0 until 2) drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + 10 * u, r.bottom - (12 - j * 6) * u), Offset(r.right - (14 + j * 20) * u, r.bottom - (12 - j * 6) * u), 0.8f * u)
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size)
        })
    }

    /**
     * La pestaña de un día: un tubo de ensayo con su tapón (la letra de la semana), la etiqueta pegada con el día y, dentro,
     * el líquido del color del ánimo (vacío, si no hay).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        rotate(-3f + 6f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val r = Rect(w / 2 - 12 * u, 9 * u, w / 2 + 12 * u, h - 2 * u)
            val tubo = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - r.width / 2)
                arcTo(Rect(r.left, r.bottom - r.width, r.right, r.bottom), 0f, 180f, false); close()
            }
            drawPath(tubo, Color.White.copy(alpha = if (oscuro) 0.12f else 0.55f))
            if (e.tono != null) clipPath(tubo) {
                val nivel = r.top + r.height * (0.42f + 0.1f * azar(e.semilla, 2))
                drawRect(e.tono, Offset(r.left, nivel), Size(r.width, r.bottom - nivel))
                drawRect(Color.White.copy(alpha = 0.35f), Offset(r.left, nivel), Size(r.width, 1.2f * u))
            }
            drawPath(tubo, (if (oscuro) Color.White.copy(alpha = 0.5f) else c(GRIS_OSC)), style = Stroke(1.1f * u))
            drawLine(Color.White.copy(alpha = 0.7f), Offset(r.left + 3 * u, r.top + 4 * u), Offset(r.left + 3 * u, r.bottom - 10 * u), 1.2f * u)
            // El tapón con la letra de la semana.
            drawRoundRect(c(CIAN), Offset(r.left - 3 * u, 1 * u), Size(r.width + 6 * u, 10 * u), CornerRadius(2 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left - 3 * u, 8 * u), Size(r.width + 6 * u, 3 * u))
            textoEn(medidor, e.semana.uppercase(), Rect(r.left - 3 * u, 1 * u, r.right + 3 * u, 9 * u), letra, Color.White, 7.5f, peso = FontWeight.Medium, minimo = 5f)
            // La etiqueta con el día.
            val et = Rect(r.left - 1 * u, r.top + 5 * u, r.right + 1 * u, r.top + 24 * u)
            drawRect(papel(oscuro, false), et.topLeft, et.size)
            drawRect(c(GRIS_OSC), et.topLeft, et.size, style = Stroke(0.7f * u))
            textoEn(medidor, e.dia, et.deflate(1 * u), letra, TINTA, 15f, peso = FontWeight.Medium, minimo = 7f)
        }
    }

    /** El rótulo de un mes: una tira de cinta de carrocero pegada de lado a lado, con los bordes rasgados y el mes a rotulador. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cinta = mezcla(Color(0xFFE8DCB4), Color(0xFF0B0E12), if (oscuro) 0.2f else 0f)
        val tam = 15f
        val largo = (anchoTexto(medidor, texto, letra, tam, FontWeight.Medium) + 40 * u).coerceIn(w * 0.4f, w - 8 * u)
        val r = Rect(w / 2 - largo / 2, 5 * u, w / 2 + largo / 2, h - 5 * u)
        val p = Path().apply {
            moveTo(r.left, r.top)
            var x = r.left; var k = 0
            while (x < r.right) { x += 6 * u; lineTo(minOf(x, r.right), r.top + (if (k % 2 == 0) 0.6f else 0f) * u); k++ }
            var y = r.top; k = 0
            while (y < r.bottom) { y += 3 * u; lineTo(r.right - (1.5f * azar(7, k)) * u, minOf(y, r.bottom)); k++ }
            x = r.right; k = 0
            while (x > r.left) { x -= 6 * u; lineTo(maxOf(x, r.left), r.bottom - (if (k % 2 == 0) 0.6f else 0f) * u); k++ }
            y = r.bottom; k = 0
            while (y > r.top) { y -= 3 * u; lineTo(r.left + (1.5f * azar(11, k)) * u, maxOf(y, r.top)); k++ }
            close()
        }
        translate(0f, 1.5f * u) { drawPath(p, Color.Black.copy(alpha = 0.18f)) }
        drawPath(p, cinta)
        for (k in 0 until 30) drawLine(Color.Black.copy(alpha = 0.04f), Offset(r.left + r.width * azar(13, k), r.top + 2 * u), Offset(r.left + r.width * azar(13, k) + 1 * u, r.bottom - 2 * u), 0.8f * u)
        textoEn(medidor, texto, r.deflate(4 * u), letra, Color(0xFF1C2A48), tam, peso = FontWeight.Medium, minimo = 7f)
        drawLine(c2Lab(CIAN, oscuro), Offset(r.left + 14 * u, r.bottom - 6 * u), Offset(r.right - 14 * u, r.bottom - 6 * u), 0.9f * u)
    }

    private fun c2Lab(x: Color, oscuro: Boolean) = t(x, oscuro, false)

    // ---------------------------------------------------------------- las cartas

    /** El papel de grado médico de la bolsa de esterilización y la película transparente, un poco azulada, de su frente. */
    private val PAPEL_MEDICO = Color(0xFFF1F4F2)
    private val PELICULA = Color(0xFFCFE6EE)

    /**
     * La bolsa de esterilización de la muestra: la espalda de papel de grado médico, con su puntillado, y el frente de película
     * transparente, por donde se ve la carta doblada dentro; los bordes van termosellados en estrías y arriba se cierra en
     * chevrón, la boca por donde se pela para abrirla. En un costado, la franja de indicadores de esterilización (cuadritos
     * que viran de color); sobre la película, la etiqueta de la muestra con su franja cian, a quién va y las fechas, y el
     * código de barras. El sello es la etiqueta de seguridad con la molécula del laboratorio, pegada a caballo sobre la boca:
     * cuando la muestra ya se puede abrir, fluoresce bajo la luz negra (y los indicadores viran a verde); al abrirla, se rasga.
     * Las de al futuro van además cerradas con una brida cian, con su cabeza de trinquete. Dentro, la carta es una hoja
     * cuadriculada del cuaderno.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top + h * 0.02f
        val pico = s.top + h * 0.3f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val etiqueta = papel(oscuro, false)
        val hojaColor = if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja
        val sc = Offset(s.center.x, pico)
        val sw = w * 0.2f; val sh = h * 0.2f
        val sr = Rect(sc.x - sw / 2, sc.y - sh / 2, sc.x + sw / 2, sc.y + sh / 2)
        fun sello(alfa: Float) {
            fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
            val fl = e.brillo
            val fondo = mezcla(etiqueta, PAPEL_UV, fl)
            drawRect(a(Color.Black.copy(alpha = 0.22f)), sr.topLeft + Offset(0.6f * u, 1f * u), sr.size)
            drawRect(a(fondo), sr.topLeft, sr.size)
            drawRect(a(mezcla(c(CIAN), FLUOR, fl)), sr.topLeft, Size(sw * 0.16f, sh))
            // Los cortes de seguridad del borde, que hacen que se rasgue y no se despegue.
            for (i in 0 until 4) drawLine(a(c(GRIS)), Offset(sr.right - 1 * u, sr.top + sh * (0.2f + i * 0.2f)), Offset(sr.right - 3.5f * u, sr.top + sh * (0.2f + i * 0.2f)), 0.6f * u)
            molecula(Offset(sr.left + sw * 0.56f, sc.y), sh * 0.3f, a(TINTA), a(mezcla(c(VIOLETA), FLUOR_ROSA, fl)), a(mezcla(c(CIAN), FLUOR, fl)), a(fondo), false)
        }
        // La boca en chevrón y la película.
        val boca = Path().apply { moveTo(s.left, bis); lineTo(sc.x, pico); lineTo(s.right, bis); lineTo(s.right, s.top); lineTo(s.left, s.top); close() }
        val pelicula = Path().apply { moveTo(s.left, bis); lineTo(sc.x, pico); lineTo(s.right, bis); lineTo(s.right, s.bottom); lineTo(s.left, s.bottom); close() }

        sombraSuave(s, 2 * u, 6 * u, 0.22f)
        // ---- La espalda de papel médico, con su puntillado.
        drawRect(c(PAPEL_MEDICO), s.topLeft, s.size)
        var py = s.top + 3 * u
        while (py < s.bottom) { var px = s.left + 3 * u + (if (((py - s.top) / (4 * u)).toInt() % 2 == 0) 0f else 2 * u); while (px < s.right) { drawCircle(c(GRIS).copy(alpha = 0.25f), 0.4f * u, Offset(px, py)); px += 4 * u }; py += 4 * u }
        // ---- La solapa de película que se pela (la punta del chevrón).
        fun DrawScope.solapa(adentro: Boolean) {
            val p = Path().apply { moveTo(s.left, s.top); lineTo(s.right, s.top); lineTo(s.right, bis); lineTo(sc.x, pico); lineTo(s.left, bis); close() }
            if (adentro) {
                drawPath(p, c(PELICULA).copy(alpha = 0.55f))
                drawPath(p, c(GRIS), style = Stroke(0.8f * u))
                return
            }
            drawPath(p, c(PELICULA).copy(alpha = 0.35f))
            // El sello térmico del chevrón: estrías a lo largo de la V.
            listOf(0f, 2.2f, 4.4f).forEach { d ->
                drawPath(Path().apply { moveTo(s.left, bis + d * u); lineTo(sc.x, pico + d * u); lineTo(s.right, bis + d * u) }, c(GRIS).copy(alpha = 0.55f), style = Stroke(0.7f * u))
            }
            textoEn(medidor, "PELAR AQUÍ", Rect(s.left + w * 0.05f, s.top + 1 * u, s.left + w * 0.3f, bis + h * 0.08f), letra, c(GRIS_OSC), 6f, minimo = 4.5f)
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2f * u * e.romper); rotate(-6f * e.romper, sc) }) {
                clipPath(mitadRota(sr, sc.y, true, 6, e.semilla)) { sello(1f) }
            }
        }
        if (!cerrada) solapaCarta(s.top, ab) { solapa(it) }

        // ---- La carta: dentro, doblada y vista por la película; al abrir, sale entera.
        val arriba0 = s.top + h * 0.12f
        val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
        val hoja = Rect(s.left + w * 0.1f, arriba0 - alza, s.right - w * 0.1f, arriba0 - alza + h * 0.82f)
        drawRect(Color.Black.copy(alpha = 0.15f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
        drawRect(hojaColor, hoja.topLeft, hoja.size)
        clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
            var gx = hoja.left + 5 * u; while (gx < hoja.right) { drawLine(e.renglones.copy(alpha = e.renglones.alpha * 0.5f), Offset(gx, hoja.top), Offset(gx, hoja.bottom), 0.5f * u); gx += 5.5f * u }
            var gy = hoja.top + 5 * u; while (gy < hoja.bottom) { drawLine(e.renglones.copy(alpha = e.renglones.alpha * 0.5f), Offset(hoja.left, gy), Offset(hoja.right, gy), 0.5f * u); gy += 5.5f * u }
        }
        drawRect(c(CIAN).copy(alpha = 0.6f), hoja.topLeft, Size(hoja.width, 2 * u))
        if (e.subir <= 0f) drawLine(c(GRIS).copy(alpha = 0.6f), Offset(hoja.left, hoja.top + hoja.height * 0.45f), Offset(hoja.right, hoja.top + hoja.height * 0.45f), 0.8f * u)

        // ---- El frente de película: el brillo, los bordes termosellados y el indicador.
        clipPath(pelicula) {
            drawRect(c(PELICULA).copy(alpha = 0.32f), s.topLeft, s.size)
            drawPath(Path().apply { moveTo(s.left + w * 0.62f, s.bottom); lineTo(s.left + w * 0.72f, s.bottom); lineTo(s.left + w * 0.9f, s.top); lineTo(s.left + w * 0.8f, s.top); close() }, Color.White.copy(alpha = 0.3f))
        }
        val sellado = c(GRIS).copy(alpha = 0.6f)
        for (i in 0 until 4) {
            val d = (1.2f + i * 1.6f) * u
            drawLine(sellado, Offset(s.left + d, bis + 3 * u), Offset(s.left + d, s.bottom - 2 * u), 0.6f * u)
            drawLine(sellado, Offset(s.right - d, bis + 3 * u), Offset(s.right - d, s.bottom - 2 * u), 0.6f * u)
            drawLine(sellado, Offset(s.left + 2 * u, s.bottom - d), Offset(s.right - 2 * u, s.bottom - d), 0.6f * u)
        }
        drawRect(c(GRIS_OSC), s.topLeft, s.size, style = Stroke(0.8f * u))
        // La franja de indicadores, en el costado derecho: viran de rosa a pardo (esterilizada) y a verde cuando está lista.
        val ind = Rect(s.right - w * 0.075f, s.top + h * 0.42f, s.right - w * 0.03f, s.bottom - h * 0.1f)
        for (i in 0 until 4) {
            val cy = ind.top + ind.height * (i + 0.5f) / 4f
            val col = mezcla(c(Color(0xFF8A5A3A)), FLUOR, e.brillo)
            drawRect(col, Offset(ind.left, cy - ind.width * 0.4f), Size(ind.width, ind.width * 0.8f))
            drawRect(c(GRIS_OSC), Offset(ind.left, cy - ind.width * 0.4f), Size(ind.width, ind.width * 0.8f), style = Stroke(0.5f * u))
        }
        // ---- La etiqueta de la muestra.
        val et = Rect(s.left + w * 0.07f, s.top + h * 0.47f, s.right - w * 0.1f, s.bottom - h * 0.07f)
        drawRect(Color.Black.copy(alpha = 0.18f), et.topLeft + Offset(0.6f * u, 1f * u), et.size)
        drawRect(etiqueta, et.topLeft, et.size)
        drawRect(c(CIAN), et.topLeft, Size(et.width, et.height * 0.14f))
        textoEn(medidor, e.linea1, Rect(et.left + 3 * u, et.top + et.height * 0.15f, et.right - 3 * u, et.top + et.height * 0.5f), letra, TINTA, 11.5f, peso = FontWeight.Medium, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(et.left + 3 * u, et.top + et.height * 0.5f, et.right - 3 * u, et.top + et.height * 0.75f), letra, TINTA.copy(alpha = 0.85f), 9f, minimo = 6f)
        var bx = et.left + 4 * u; var j = 0
        while (bx < et.left + et.width * 0.62f) {
            val g = (if (azar(e.semilla, j + 70) > 0.6f) 1.5f else 0.6f) * u
            drawRect(TINTA, Offset(bx, et.bottom - et.height * 0.21f), Size(g, et.height * 0.15f)); bx += g + (0.7f + azar(e.semilla, j + 90)) * u; j++
        }
        textoEn(medidor, "LOTE ${(e.semilla.toLong() and 0x3FF).toString().padStart(4, '0')}", Rect(et.left + et.width * 0.64f, et.bottom - et.height * 0.24f, et.right - 3 * u, et.bottom - 1 * u),
            letra, c(GRIS_OSC), 7f, minimo = 5f)

        // ---- La solapa de película cerrada.
        if (cerrada) solapaCarta(s.top, ab) { solapa(it) }
        // ---- La brida cian, que ciñe la boca bajo el chevrón.
        if (e.cordon > 0f && cerrada) {
            val by = pico + h * 0.08f
            val largo = (w + 4 * u) * e.cordon
            val bw = 4 * u * k
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(s.left - 2 * u, by - bw / 2 + 1.2f * u), Size(largo, bw))
            drawRect(c(CIAN), Offset(s.left - 2 * u, by - bw / 2), Size(largo, bw))
            var dx = s.left; while (dx < s.left - 2 * u + largo) { drawLine(c(Color(0xFF0E7C8A)), Offset(dx, by - bw / 2), Offset(dx, by + bw / 2), 0.5f * u); dx += 1.6f * u }
            if (e.cordon > 0.9f) {
                val cab = Offset(s.left + w * 0.2f, by)
                drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(cab.x - bw + 0.6f * u, cab.y - bw * 1.1f + 1 * u), Size(bw * 2f, bw * 2.2f), CornerRadius(1 * u))
                drawRoundRect(c(Color(0xFF14A5B8)), Offset(cab.x - bw, cab.y - bw * 1.1f), Size(bw * 2f, bw * 2.2f), CornerRadius(1 * u))
                drawRect(c(Color(0xFF0E7C8A)), Offset(cab.x - bw * 0.5f, cab.y - bw * 0.5f), Size(bw, bw))
                // La cola cortada que sobra.
                drawRect(c(CIAN), Offset(cab.x - bw * 0.4f, cab.y + bw * 1.1f), Size(bw * 0.8f, bw * 2.4f))
            }
        }
        // ---- La etiqueta de seguridad: entera (cayendo al sellar), fluorescente si ya se puede abrir, o rasgada.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(UV.copy(alpha = 0.55f * e.brillo), FLUOR.copy(alpha = 0.15f * e.brillo), Color.Transparent),
                center = sc, radius = sw * 1.3f), sw * 1.3f, sc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc); rotate(-4f, sc) }) { sello(e.alfaSello.coerceIn(0f, 1f)) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2f * u * e.romper); rotate(-4f + 8f * e.romper, sc) }) {
                clipPath(mitadRota(sr, sc.y, false, 6, e.semilla)) { sello(1f) }
            }
        }
    }

    // La hoja cuadriculada que asoma de la bolsa, bajo su filo cian.
    override val cajaPrevia = Rect(0.15f, 0.14f, 0.85f, 0.3f)

    override val altoBuzon: Dp = 214.dp

    /**
     * La campana de extracción del laboratorio: el marco blanco con la luz del techo, el vidrio de la guillotina a medio subir
     * (con su reflejo y la etiqueta amarilla de aviso) y, en el costado, el monitor del flujo de aire con su pantallita. Dentro,
     * sobre la cubierta de resina, tres gradillas de tubos de ensayo, cada carta enrollada en su tubo: los listos, con el
     * líquido que fluoresce bajo la luz negra; los etiquetados, con su tapón cian y la cinta de sellar; los abiertos, sin tapón
     * y con el papel asomando. Cada gradilla lleva su tira de cinta rotulada con el nombre y la cuenta. Hasta ocho tubos por
     * gradilla; si hay más, la cinta lo dice.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF05070A), 0.4f) else c(x)
        // ---- El azulejo de fondo, a los lados de la campana.
        drawRect(f(AZULEJO))
        var ay = 0f; while (ay < h) { drawLine(f(JUNTA), Offset(0f, ay), Offset(w, ay), 0.8f * u); ay += 18 * u }
        var ax = 0f; while (ax < w) { drawLine(f(JUNTA), Offset(ax, 0f), Offset(ax, h), 0.8f * u); ax += 18 * u }
        // ---- La campana: el cuerpo blanco, la luz de arriba y el hueco de trabajo.
        val cam = Rect(8 * u, 4 * u, w - 8 * u, h - 4 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), cam.topLeft + Offset(3 * u, 3 * u), cam.size)
        drawRect(c(MUEBLE), cam.topLeft, cam.size)
        drawRect(c(BLANCO), cam.topLeft, Size(cam.width, 22 * u))
        drawRect(c(GRIS), Offset(cam.left, cam.top + 22 * u), Size(cam.width, 1.2f * u))
        val hueco = Rect(cam.left + 10 * u, cam.top + 30 * u, cam.right - 44 * u, cam.bottom - 16 * u)
        val luz = e.listas > 0
        drawRect(if (oscuro) Color(0xFF16122A) else c(Color(0xFFE9EEF2)), hueco.topLeft, hueco.size)
        drawRect(Brush.verticalGradient(listOf(if (luz) UV.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.5f), Color.Transparent), hueco.top, hueco.top + hueco.height * 0.7f),
            hueco.topLeft, hueco.size)
        // El tubo de luz dentro del techo de la campana.
        drawRect(if (luz) UV else c(Color(0xFFFDFDF4)), Offset(hueco.left + 10 * u, hueco.top + 2 * u), Size(hueco.width - 20 * u, 3 * u))
        // La cubierta de resina.
        val cubierta = hueco.bottom - 10 * u
        drawRect(c(EPOXI), Offset(hueco.left, cubierta), Size(hueco.width, hueco.bottom - cubierta))
        drawRect(Color.White.copy(alpha = 0.12f), Offset(hueco.left, cubierta), Size(hueco.width, 1 * u))
        // ---- Las tres gradillas.
        val grupos = listOf(Triple("LISTAS", e.listas, 0), Triple("ETIQUETADAS", e.selladas, 1), Triple("ABIERTAS", e.abiertas, 2))
        val ancho = (hueco.width - 16 * u) / 3f
        grupos.forEachIndexed { i, (nombre, n, tipo) ->
            val g = Rect(hueco.left + 6 * u + i * (ancho + 2 * u), cubierta - 96 * u, hueco.left + 6 * u + i * (ancho + 2 * u) + ancho, cubierta + 2 * u)
            val paso = (g.width - 8 * u) / 8f
            val tubos = n.coerceAtMost(8)
            // La parte de atrás de la gradilla.
            drawRect(c(GRIS), Offset(g.left, g.top + 46 * u), Size(g.width, 3 * u))
            for (j in 0 until tubos) {
                val x = g.left + 4 * u + j * paso + paso * 0.15f
                val tw = paso * 0.7f
                val tubo = Rect(x, g.top + 8 * u + 6 * u * azar(i + 3, j), x + tw, g.bottom - 4 * u)
                if (tipo == 2) {
                    // El papel enrollado que asoma por la boca.
                    drawRect(etiquetaLab(oscuro), Offset(tubo.left + tw * 0.15f, tubo.top - 8 * u), Size(tw * 0.7f, 20 * u))
                    drawLine(TINTA.copy(alpha = 0.4f), Offset(tubo.left + tw * 0.3f, tubo.top - 6 * u), Offset(tubo.left + tw * 0.3f, tubo.top + 4 * u), 0.5f * u)
                }
                // El vidrio, con la carta enrollada dentro y el líquido al fondo.
                drawRoundRect(Color.White.copy(alpha = 0.3f), tubo.topLeft, tubo.size, CornerRadius(tw / 2))
                if (tipo != 2) drawRect(etiquetaLab(oscuro).copy(alpha = 0.9f), Offset(tubo.left + tw * 0.2f, tubo.top + 4 * u), Size(tw * 0.6f, tubo.height * 0.55f))
                val liq = when (tipo) { 0 -> FLUOR; 1 -> c(CIAN); else -> c(VIOLETA) }
                drawRoundRect(liq.copy(alpha = if (tipo == 0) 0.9f else 0.6f), Offset(tubo.left + 0.6f * u, tubo.bottom - tubo.height * 0.28f), Size(tw - 1.2f * u, tubo.height * 0.28f), CornerRadius(tw / 2))
                if (tipo == 0) drawRoundRect(FLUOR.copy(alpha = 0.25f), tubo.inflate(2.5f * u).topLeft, tubo.inflate(2.5f * u).size, CornerRadius(tw))
                drawRoundRect(c(GRIS_OSC).copy(alpha = 0.7f), tubo.topLeft, tubo.size, CornerRadius(tw / 2), style = Stroke(0.7f * u))
                drawLine(Color.White.copy(alpha = 0.7f), Offset(tubo.left + tw * 0.25f, tubo.top + 3 * u), Offset(tubo.left + tw * 0.25f, tubo.bottom - 6 * u), 0.7f * u)
                // El tapón.
                when (tipo) {
                    0 -> drawRect(c(Color(0xFF6A3FBF)), Offset(tubo.left - 1 * u, tubo.top - 4 * u), Size(tw + 2 * u, 6 * u))
                    1 -> {
                        drawRect(c(CIAN), Offset(tubo.left - 1 * u, tubo.top - 4 * u), Size(tw + 2 * u, 6 * u))
                        drawRect(Color.White.copy(alpha = 0.55f), Offset(tubo.left - 1.2f * u, tubo.top + 1 * u), Size(tw + 2.4f * u, 3 * u))
                    }
                    else -> {}
                }
            }
            // El frente de la gradilla: el alambre plastificado con sus agujeros.
            drawRect(c(BLANCO), Offset(g.left, g.bottom - 20 * u), Size(g.width, 4 * u))
            drawRect(c(GRIS), Offset(g.left, g.bottom - 16 * u), Size(g.width, 1 * u))
            listOf(g.left, g.right - 2 * u).forEach { lx -> drawRect(c(GRIS_OSC), Offset(lx, g.top + 46 * u), Size(2 * u, g.height - 46 * u)) }
            // La cinta rotulada.
            val cinta = Rect(g.left + 4 * u, g.bottom - 15 * u, g.right - 4 * u, g.bottom - 1 * u)
            rotate((i - 1) * 1.2f, cinta.center) {
                drawRect(Color.Black.copy(alpha = 0.2f), cinta.topLeft + Offset(0.5f * u, 1 * u), cinta.size)
                drawRect(if (tipo == 0) mezcla(c(AMARILLO), Color.White, 0.3f) else etiquetaLab(oscuro), cinta.topLeft, cinta.size)
                val texto = "$nombre · $n" + if (n > 8) " (+${n - 8})" else ""
                textoEn(medidor, texto, cinta.deflate(1 * u), letra, TINTA, 9f, peso = FontWeight.Medium, minimo = 6f)
            }
        }
        // ---- El vidrio de la guillotina, a medio subir: tapa la mitad de arriba del hueco.
        val vidrio = Rect(hueco.left - 3 * u, hueco.top - 2 * u, hueco.right + 3 * u, hueco.top + hueco.height * 0.42f)
        drawRect(c(PELICULA).copy(alpha = 0.18f), vidrio.topLeft, vidrio.size)
        drawPath(Path().apply { moveTo(vidrio.left + vidrio.width * 0.1f, vidrio.bottom); lineTo(vidrio.left + vidrio.width * 0.2f, vidrio.bottom); lineTo(vidrio.left + vidrio.width * 0.34f, vidrio.top); lineTo(vidrio.left + vidrio.width * 0.24f, vidrio.top); close() }, Color.White.copy(alpha = 0.18f))
        drawRect(c(GRIS), Offset(vidrio.left, vidrio.bottom - 5 * u), Size(vidrio.width, 5 * u))
        drawRect(c(BLANCO), Offset(vidrio.left, vidrio.bottom - 5 * u), Size(vidrio.width, 1.2f * u))
        drawRect(c(GRIS_OSC), Offset(vidrio.center.x - 20 * u, vidrio.bottom - 3.5f * u), Size(40 * u, 2 * u))
        val aviso = Offset(vidrio.right - 22 * u, vidrio.top + 12 * u)
        drawPath(Path().apply { moveTo(aviso.x, aviso.y - 7 * u); lineTo(aviso.x + 8 * u, aviso.y + 6 * u); lineTo(aviso.x - 8 * u, aviso.y + 6 * u); close() }, c(AMARILLO))
        drawLine(TINTA, Offset(aviso.x, aviso.y - 2.5f * u), Offset(aviso.x, aviso.y + 2 * u), 1.2f * u)
        drawCircle(TINTA, 0.8f * u, Offset(aviso.x, aviso.y + 4 * u))
        // ---- Los marcos de la campana y el monitor de flujo, con el total de cartas.
        drawRect(c(GRIS), hueco.topLeft, hueco.size, style = Stroke(3 * u))
        val mon = Rect(cam.right - 38 * u, cam.top + 34 * u, cam.right - 6 * u, cam.top + 92 * u)
        drawRoundRect(c(GRIS_OSC), mon.topLeft, mon.size, CornerRadius(3 * u))
        val pan = Rect(mon.left + 3 * u, mon.top + 4 * u, mon.right - 3 * u, mon.top + 26 * u)
        drawRect(if (luz) Color(0xFF0E2A20) else Color(0xFF14202A), pan.topLeft, pan.size)
        textoEn(medidor, "0,5 m/s", Rect(pan.left, pan.top + 1 * u, pan.right, pan.top + pan.height * 0.45f), letra, FLUOR.copy(alpha = 0.7f), 6.5f, minimo = 4.5f)
        textoEn(medidor, (e.listas + e.selladas + e.abiertas).toString(), Rect(pan.left, pan.top + pan.height * 0.4f, pan.right, pan.bottom), letra, FLUOR, 11f, peso = FontWeight.Bold)
        listOf(FLUOR to luz, c(AMARILLO) to (e.selladas > 0), c(ROJO) to false).forEachIndexed { i, (col, on) ->
            drawCircle(if (on) col else col.copy(alpha = 0.25f), 2.5f * u, Offset(mon.left + 8 * u + i * 8 * u, mon.top + 34 * u))
        }
        textoEn(medidor, "MUESTRAS", Rect(mon.left, mon.top + 40 * u, mon.right, mon.bottom - 2 * u), letra, c(BLANCO), 6f, minimo = 4.5f)
        // La rejilla del pie.
        var rx = cam.left + 16 * u; while (rx < cam.right - 16 * u) { drawLine(c(GRIS_OSC), Offset(rx, cam.bottom - 11 * u), Offset(rx, cam.bottom - 5 * u), 1 * u); rx += 4 * u }
    }

    /** El papel de las etiquetas de los tubos, que se hunde poco en oscuro. */
    private fun etiquetaLab(oscuro: Boolean): Color = papel(oscuro, false)
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 128.dp

    /** El quinqué del pueblo, de grabado: la argolla, el sombrerete, el tubo de vidrio con la llama y el depósito de latón. */
    private fun DrawScope.quinque(c: Offset, s: Float, tinta: Color, llama: Color, laton: Color) {
        val u = density
        drawCircle(tinta, s * 0.12f, Offset(c.x, c.y - s * 0.86f), style = Stroke(1 * u))
        drawPath(Path().apply { moveTo(c.x - s * 0.3f, c.y - s * 0.52f); lineTo(c.x + s * 0.3f, c.y - s * 0.52f); lineTo(c.x + s * 0.14f, c.y - s * 0.72f); lineTo(c.x - s * 0.14f, c.y - s * 0.72f); close() }, tinta)
        val vidrio = Path().apply {
            moveTo(c.x - s * 0.16f, c.y - s * 0.5f); lineTo(c.x + s * 0.16f, c.y - s * 0.5f)
            quadraticTo(c.x + s * 0.36f, c.y - s * 0.05f, c.x + s * 0.2f, c.y + s * 0.2f); lineTo(c.x - s * 0.2f, c.y + s * 0.2f)
            quadraticTo(c.x - s * 0.36f, c.y - s * 0.05f, c.x - s * 0.16f, c.y - s * 0.5f); close()
        }
        drawPath(vidrio, llama.copy(alpha = 0.18f))
        drawPath(vidrio, tinta, style = Stroke(1 * u))
        drawPath(Path().apply { moveTo(c.x, c.y - s * 0.32f); quadraticTo(c.x + s * 0.12f, c.y - s * 0.05f, c.x, c.y + s * 0.08f); quadraticTo(c.x - s * 0.12f, c.y - s * 0.05f, c.x, c.y - s * 0.32f); close() }, llama)
        drawRect(tinta, Offset(c.x - s * 0.24f, c.y + s * 0.2f), Size(s * 0.48f, s * 0.08f))
        drawOval(laton, Offset(c.x - s * 0.36f, c.y + s * 0.28f), Size(s * 0.72f, s * 0.46f))
        drawOval(tinta, Offset(c.x - s * 0.36f, c.y + s * 0.28f), Size(s * 0.72f, s * 0.46f), style = Stroke(1 * u))
        drawRect(tinta, Offset(c.x - s * 0.4f, c.y + s * 0.72f), Size(s * 0.8f, s * 0.08f))
    }

    /** Un adorno de imprenta: el rombo con dos hojitas a los lados. */
    private fun DrawScope.floron(c: Offset, s: Float, color: Color) {
        drawPath(Path().apply { moveTo(c.x, c.y - s * 0.5f); lineTo(c.x + s * 0.5f, c.y); lineTo(c.x, c.y + s * 0.5f); lineTo(c.x - s * 0.5f, c.y); close() }, color)
        listOf(-1f, 1f).forEach { d ->
            drawPath(Path().apply {
                moveTo(c.x + d * s * 0.6f, c.y); quadraticTo(c.x + d * s * 1.1f, c.y - s * 0.45f, c.x + d * s * 1.6f, c.y)
                quadraticTo(c.x + d * s * 1.1f, c.y + s * 0.45f, c.x + d * s * 0.6f, c.y); close()
            }, color)
        }
    }

    /**
     * La cabecera de la gaceta del pueblo: el filete grueso y el fino, el nombre del periódico en grande entre dos orejas (el
     * quinqué grabado a la izquierda y el número a la derecha; de noche, la luna y las estrellas), la franja del año y el número
     * entre filetes con sus florones, la fecha grande como dateline (partida en dos si es larga), el detalle y el doble
     * filete del pie.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        // Los filetes de arriba.
        drawRect(tinta, Offset(0f, 1 * u), Size(w, 2.6f * u))
        drawRect(tinta, Offset(0f, 5.4f * u), Size(w, 0.8f * u))
        // Las orejas.
        val oreja = 40 * u
        val izq = Rect(0f, 10 * u, oreja, 42 * u); val der = Rect(w - oreja, 10 * u, w, 42 * u)
        listOf(izq, der).forEach { drawRect(tinta.copy(alpha = 0.55f), it.topLeft, it.size, style = Stroke(0.8f * u)) }
        if (!e.noche) quinque(izq.center, izq.height * 0.42f, tinta.copy(alpha = 0.85f), papel(LLAMA, oscuro, false), papel(LATON, oscuro, false))
        else {
            val m = izq.center
            val luna = Path().apply { addOval(Rect(m.x - 8 * u, m.y - 8 * u, m.x + 8 * u, m.y + 8 * u)) }
            val hueco = Path().apply { addOval(Rect(m.x - 3 * u, m.y - 9.5f * u, m.x + 11 * u, m.y + 4.5f * u)) }
            drawPath(Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, luna, hueco), p.acento)
            drawPath(estrella(Offset(m.x + 10 * u, m.y + 8 * u), 2.4f * u, 5, 0.45f), p.acento)
        }
        textoEn(medidor, "Nº", Rect(der.left + 2 * u, der.top + 2 * u, der.right - 2 * u, der.top + 12 * u), LetraExtra.fell, p.tenue, 8f, minimo = 5f)
        textoEn(medidor, e.numero.toString(), Rect(der.left + 2 * u, der.top + 11 * u, der.right - 2 * u, der.bottom - 2 * u), LetraExtra.fell, tinta, 15f, minimo = 7f)
        // El nombre del periódico y, entre filetes, el resto del rótulo con sus florones.
        val partes = e.rotulo.split(" · ", limit = 2)
        textoEn(medidor, partes[0], Rect(oreja + 6 * u, 10 * u, w - oreja - 6 * u, 42 * u), LetraExtra.fell, tinta, 20f, espaciado = 0.4f, minimo = 6f)
        drawLine(tinta, Offset(0f, 45 * u), Offset(w, 45 * u), 0.8f * u)
        drawLine(tinta, Offset(0f, 60 * u), Offset(w, 60 * u), 0.8f * u)
        if (partes.size == 2) textoEn(medidor, partes[1], Rect(26 * u, 45 * u, w - 26 * u, 60 * u), LetraExtra.fell, p.tenue, 10f, espaciado = 0.6f, minimo = 6f)
        else for (k in 0 until 5) drawPath(estrella(Offset(w / 2 + (k - 2) * 16 * u, 52.5f * u), 2.6f * u, 5, 0.45f), p.acento.copy(alpha = 0.8f))
        listOf(10 * u, w - 10 * u).forEach { floron(Offset(it, 52.5f * u), 6 * u, p.acento.copy(alpha = 0.85f)) }
        // La fecha y el detalle.
        fechaFuturo(medidor, e.fecha, Rect(0f, 62 * u, w, 106 * u), LetraExtra.gaceta, tinta, 21f)
        fechaFuturo(medidor, e.detalle, Rect(10 * u, 106 * u, w - 10 * u, 122 * u), LetraExtra.gaceta, p.tenue, 10.5f, k = 0.86f)
        // El doble filete del pie.
        drawRect(tinta, Offset(0f, h - 4.6f * u), Size(w, 0.8f * u))
        drawRect(tinta, Offset(0f, h - 2.6f * u), Size(w, 1.8f * u))
    }

    /**
     * La hoja del libro de cuentas: el doble margen rojo, los dos filetes azules de la cabecera, las columnas de pesos y
     * centavos a la derecha, las pecas del papel viejo, la esquina gastada y el florón de imprenta al pie entre dos filetes. En
     * modo oscuro se lee a la luz del quinqué, tibia, desde arriba a la izquierda; las orillas se pierden en la penumbra.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        listOf(22 * u, 25 * u).forEach { x -> drawLine(p.acento.copy(alpha = 0.42f), Offset(x, 0f), Offset(x, h), 1.1f * u) }
        listOf(60 * u, 63 * u).forEach { y -> drawLine(p.detalle.copy(alpha = 0.4f), Offset(0f, y), Offset(w, y), 1.1f * u) }
        listOf(w - 20 * u, w - 10 * u).forEach { x -> drawLine(p.detalle.copy(alpha = 0.25f), Offset(x, 63 * u), Offset(x, h), 0.9f * u) }
        // Las pecas del papel viejo.
        for (k in 0 until 34) {
            val o = Offset(w * azar(311, k), h * azar(313, k))
            drawCircle(VETA.copy(alpha = 0.04f + 0.06f * azar(317, k)), (0.8f + 2.6f * azar(319, k)) * u, o)
        }
        // La esquina gastada, abajo a la derecha.
        val d = 26 * u
        drawPath(Path().apply { moveTo(w, h - d); lineTo(w, h); lineTo(w - d, h); quadraticTo(w - d * 0.4f, h - d * 0.4f, w, h - d); close() }, VETA.copy(alpha = 0.1f))
        // El florón al pie, entre dos filetes.
        val y = h - 26 * u
        floron(Offset(w / 2, y), 7 * u, p.tinta.copy(alpha = 0.32f))
        drawLine(p.tinta.copy(alpha = 0.25f), Offset(w * 0.22f, y), Offset(w / 2 - 16 * u, y), 0.9f * u)
        drawLine(p.tinta.copy(alpha = 0.25f), Offset(w / 2 + 16 * u, y), Offset(w * 0.78f, y), 0.9f * u)
        if (oscuro && !p.oscura) penumbraFuturo(Offset(w * 0.38f, h * 0.3f), LLAMA, velo = 0.2f, orilla = 0.64f)
    }

    /** El canto de la gaceta doblada: el papel de periódico, un poco más gris, con el filete grueso y el fino y un clavo en cada punta. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(fondoBarraFuturo(p, p.tenue, 0.1f, oscuro))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.05f), Color.Transparent), 0f, h * 0.4f), Offset.Zero, Size(w, h * 0.4f))
        val tinta = p.tinta.copy(alpha = if (p.oscura) 0.5f else 0.75f)
        drawRect(tinta, Offset(0f, h - 6 * u), Size(w, 2.2f * u))
        drawRect(tinta, Offset(0f, h - 2.2f * u), Size(w, 0.8f * u))
        listOf(6 * u, w - 6 * u).forEach { x ->
            drawCircle(t(HIERRO, oscuro, noche), 2.2f * u, Offset(x, 8 * u))
            drawCircle(Color.White.copy(alpha = 0.3f), 0.8f * u, Offset(x - 0.6f * u, 7.4f * u))
        }
    }

    /** La tarjeta de envío de cartulina: la punta cortada, el ojal de latón con su cordel y el doble filete, rojo y azul. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 2 * u, w - 2 * u, h - 4 * u)
        val ch = r.height * 0.32f
        fun tarjeta(q: Rect) = Path().apply {
            moveTo(q.left + ch, q.top); lineTo(q.right, q.top); lineTo(q.right, q.bottom); lineTo(q.left + ch, q.bottom)
            lineTo(q.left, q.bottom - ch); lineTo(q.left, q.top + ch); close()
        }
        drawPath(tarjeta(r.translate(1.5f * u, 2 * u)), Color.Black.copy(alpha = 0.25f))
        drawPath(tarjeta(r), Brush.verticalGradient(listOf(papel(mezcla(MANILA, Color.White, 0.12f), oscuro, noche), papel(MANILA, oscuro, noche)), r.top, r.bottom))
        drawPath(tarjeta(r), papel(TINTA, oscuro, noche).copy(alpha = 0.35f), style = Stroke(0.8f * u))
        val dentro = Rect(r.left + 14 * u, r.top + 3 * u, r.right - 3 * u, r.bottom - 3 * u)
        drawRect(papel(LADRILLO, oscuro, noche).copy(alpha = 0.6f), dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
        drawRect(papel(MARINO, oscuro, noche).copy(alpha = 0.45f), dentro.deflate(2 * u).topLeft, dentro.deflate(2 * u).size, style = Stroke(0.6f * u))
        val ojal = Offset(r.left + 7.5f * u, r.center.y)
        drawCircle(papel(LATON, oscuro, noche), 3.6f * u, ojal)
        drawCircle(mezcla(papel(LATON, oscuro, noche), Color.Black, 0.5f), 1.6f * u, ojal)
        drawPath(Path().apply { moveTo(ojal.x, ojal.y); quadraticTo(ojal.x - 4 * u, ojal.y - 10 * u, ojal.x - 6 * u, r.top - 1 * u) }, papel(CORDEL, oscuro, noche), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = TINTA

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 264.dp

    /** El cuero del cuaderno del pueblo y el dorado de sus letras. */
    private val CUERO = Color(0xFF7A4A28)
    private val DORADO = Color(0xFFE2C27A)

    /**
     * El mostrador del almacén del pueblo: detrás, la pared de tablas con la repisa de latas, frascos de caramelos y un costal
     * de harina; sobre el mostrador de tablones gastados, a la izquierda, el quinqué de latón con su depósito de petróleo, el
     * tubo de vidrio y la llama; a la derecha, el tintero de vidrio con la pluma, el timbre de mostrador y el ovillo de cordel
     * con las tijeras. El cuaderno del pueblo va al centro: cuero marrón con su pespunte, el cuadro dorado en seco con el
     * nombre y la cuenta, las esquinas con florones, la cinta de registro y los papeles sueltos que asoman por arriba (más
     * cuantas más hojas), cerrado con la correa y su hebilla. Al abrirse, la correa se suelta y cae, y la tapa gira sobre el
     * lomo: dentro, la guarda de papel jaspeado y la hoja rayada con la letra a pluma y el dibujito de la casa y el árbol. En
     * oscuro, el almacén se hunde y solo alumbra el quinqué.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF080604), 0.5f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val gaceta = papel(GACETA, oscuro, false)
        val fondoY = h * 0.5f
        val frenteY = h * 0.82f
        // ---- La pared de tablas, con la repisa de las mercancías.
        drawRect(f(PINO_OSC), Offset.Zero, Size(w, fondoY))
        var ty = 0f; var k = 0
        while (ty < fondoY) {
            drawRect(f(if (k % 2 == 0) PINO_OSC else mezcla(PINO_OSC, PINO, 0.25f)), Offset(0f, ty), Size(w, 17 * u))
            drawLine(f(RENDIJA), Offset(0f, ty), Offset(w, ty), 1.2f * u)
            vetas(Rect(0f, ty, w, ty + 17 * u), f(VETA).copy(alpha = 0.3f), 400 + k, densidad = 0.4f)
            ty += 17 * u; k++
        }
        val repisa = h * 0.3f
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, repisa + 5 * u), Size(w, 4 * u))
        drawRect(f(PINO), Offset(0f, repisa), Size(w, 5 * u))
        listOf(w * 0.3f, w * 0.75f).forEach { x -> drawPath(Path().apply { moveTo(x, repisa + 5 * u); lineTo(x + 3 * u, repisa + 5 * u); lineTo(x + 3 * u, repisa + 16 * u); close() }, f(PINO_OSC)) }
        // Las latas, los frascos de caramelos y el costal.
        var gx = w * 0.06f; var j = 0
        while (gx < w * 0.95f) {
            when (j % 4) {
                0 -> { val ah = 22 * u; drawRect(f(if (j % 8 == 0) MARINO else LADRILLO), Offset(gx, repisa - ah), Size(16 * u, ah)); drawRect(f(GACETA), Offset(gx, repisa - ah * 0.7f), Size(16 * u, ah * 0.4f)); drawRect(f(HIERRO), Offset(gx, repisa - ah - 2 * u), Size(16 * u, 2 * u)); gx += 20 * u }
                1 -> { val ah = 28 * u; drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), Offset(gx, repisa - ah), Size(18 * u, ah), CornerRadius(4 * u))
                    for (b in 0 until 7) drawCircle(f(listOf(LADRILLO, LLAMA, MARINO, Color(0xFF6A8A4A))[b % 4]), 2.2f * u, Offset(gx + 4 * u + 10 * u * azar(410 + j, b), repisa - 3 * u - 14 * u * azar(420 + j, b)))
                    drawRect(f(LATON), Offset(gx + 2 * u, repisa - ah - 3 * u), Size(14 * u, 4 * u)); gx += 22 * u }
                2 -> { drawPath(Path().apply { moveTo(gx, repisa); lineTo(gx + 2 * u, repisa - 26 * u); quadraticTo(gx + 13 * u, repisa - 32 * u, gx + 24 * u, repisa - 26 * u); lineTo(gx + 26 * u, repisa); close() }, f(COSTAL))
                    drawLine(f(CORDEL), Offset(gx + 4 * u, repisa - 24 * u), Offset(gx + 22 * u, repisa - 24 * u), 1.4f * u)
                    drawPath(estrella(Offset(gx + 13 * u, repisa - 12 * u), 4 * u, 5, 0.45f), f(MARINO)); gx += 30 * u }
                else -> { val ah = 16 * u; drawRect(f(Color(0xFF6A8A4A)), Offset(gx, repisa - ah), Size(24 * u, ah)); drawRect(f(MANILA), Offset(gx + 3 * u, repisa - ah + 4 * u), Size(18 * u, 6 * u)); gx += 28 * u }
            }
            j++
        }
        // ---- El quinqué: el pie de latón, el depósito, el tubo de vidrio y la llama, con su luz (el primer degradado).
        val qx = w * 0.13f
        val qb = fondoY + 22 * u
        val llama = Offset(qx, qb - 62 * u)
        drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = if (oscuro) 0.42f else 0.2f), LLAMA.copy(alpha = 0f)), llama, w * 0.55f), w * 0.55f, llama)

        // ---- El mostrador de tablones.
        drawRect(f(Color(0xFF2A2018)), Offset(0f, frenteY), Size(w, h - frenteY))
        val tablas = 4
        for (t in 0 until tablas) {
            val y0 = fondoY + (frenteY - fondoY) * t / tablas
            val r = Rect(0f, y0, w, fondoY + (frenteY - fondoY) * (t + 1) / tablas)
            drawRect(c(if (t % 2 == 0) TABLON else mezcla(TABLON, PINO, 0.18f)), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.45f), 430 + t, densidad = 0.6f)
            drawLine(c(RENDIJA), Offset(0f, y0), Offset(w, y0), 1 * u)
        }
        // El desgaste del centro del mostrador y las manchas.
        drawOval(Color.White.copy(alpha = 0.05f), Offset(w * 0.3f, fondoY + 10 * u), Size(w * 0.4f, frenteY - fondoY - 20 * u))
        drawOval(c(TINTA).copy(alpha = 0.18f), Offset(w * 0.8f, frenteY - 14 * u), Size(10 * u, 4 * u))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, fondoY), Offset(w, fondoY), 1.2f * u)
        drawRect(c(PINO_OSC), Offset(0f, frenteY), Size(w, 8 * u))
        drawRect(c(PINO_LUZ).copy(alpha = 0.6f), Offset(0f, frenteY), Size(w, 1.2f * u))
        var bx = 0f; while (bx < w) { drawLine(f(RENDIJA), Offset(bx, frenteY + 8 * u), Offset(bx, h), 1.4f * u); drawLine(f(PINO).copy(alpha = 0.25f), Offset(bx + 2 * u, frenteY + 8 * u), Offset(bx + 2 * u, h), 0.8f * u); bx += 14 * u }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, frenteY + 8 * u), Size(w, 4 * u))
        listOf(w * 0.2f, w * 0.5f, w * 0.8f).forEach { x -> drawCircle(c(CLAVO), 1.3f * u, Offset(x, frenteY + 4 * u)) }

        // ---- El quinqué, de frente.
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(qx - 18 * u, qb - 3 * u), Size(36 * u, 8 * u))
        drawPath(Path().apply { moveTo(qx - 15 * u, qb); lineTo(qx + 15 * u, qb); lineTo(qx + 6 * u, qb - 10 * u); lineTo(qx - 6 * u, qb - 10 * u); close() }, c(LATON))
        drawRect(c(LATON), Offset(qx - 3 * u, qb - 18 * u), Size(6 * u, 8 * u))
        drawOval(c(VIDRIO).copy(alpha = if (oscuro) 0.3f else 0.55f), Offset(qx - 15 * u, qb - 40 * u), Size(30 * u, 24 * u))
        drawOval(c(Color(0xFFC89A3A)).copy(alpha = 0.6f), Offset(qx - 13 * u, qb - 30 * u), Size(26 * u, 12 * u))
        drawOval(Color.White.copy(alpha = 0.6f), Offset(qx - 10 * u, qb - 37 * u), Size(6 * u, 9 * u), style = Stroke(1 * u))
        drawRect(c(LATON), Offset(qx - 9 * u, qb - 46 * u), Size(18 * u, 6 * u))
        drawCircle(c(LATON), 2.4f * u, Offset(qx + 11 * u, qb - 43 * u))
        val tubo = Path().apply {
            moveTo(qx - 6 * u, qb - 46 * u); cubicTo(qx - 13 * u, qb - 54 * u, qx - 12 * u, qb - 68 * u, qx - 4 * u, qb - 74 * u)
            lineTo(qx - 4 * u, qb - 92 * u); lineTo(qx + 4 * u, qb - 92 * u); lineTo(qx + 4 * u, qb - 74 * u)
            cubicTo(qx + 12 * u, qb - 68 * u, qx + 13 * u, qb - 54 * u, qx + 6 * u, qb - 46 * u); close()
        }
        drawPath(tubo, LLAMA.copy(alpha = if (oscuro) 0.22f else 0.12f))
        drawOval(Color(0xFFFFE6A8), Offset(llama.x - 3 * u, llama.y - 7 * u), Size(6 * u, 12 * u))
        drawOval(Color.White.copy(alpha = 0.9f), Offset(llama.x - 1.4f * u, llama.y - 2 * u), Size(2.8f * u, 6 * u))
        drawPath(tubo, Color.White.copy(alpha = 0.55f), style = Stroke(1 * u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(qx - 6 * u, qb - 66 * u), Offset(qx - 3 * u, qb - 88 * u), 1.2f * u)

        // ---- El tintero con la pluma, el timbre del mostrador y el ovillo con las tijeras.
        val ti = Offset(w * 0.86f, fondoY + 26 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(ti.x - 11 * u, ti.y + 2 * u), Size(26 * u, 5 * u))
        drawRect(c(VIDRIO).copy(alpha = if (oscuro) 0.3f else 0.6f), Offset(ti.x - 11 * u, ti.y - 16 * u), Size(22 * u, 18 * u))
        drawRect(c(TINTA), Offset(ti.x - 9 * u, ti.y - 8 * u), Size(18 * u, 9 * u))
        drawRect(Color.White.copy(alpha = 0.6f), Offset(ti.x - 11 * u, ti.y - 16 * u), Size(22 * u, 18 * u), style = Stroke(0.9f * u))
        drawRect(c(LATON), Offset(ti.x - 5 * u, ti.y - 21 * u), Size(10 * u, 5 * u))
        drawLine(c(Color(0xFF3A2A1C)), Offset(ti.x + 1 * u, ti.y - 18 * u), Offset(ti.x + 16 * u, ti.y - 58 * u), 2 * u, cap = StrokeCap.Round)
        drawPath(Path().apply { moveTo(ti.x + 15 * u, ti.y - 56 * u); quadraticTo(ti.x + 26 * u, ti.y - 74 * u, ti.x + 20 * u, ti.y - 88 * u); quadraticTo(ti.x + 12 * u, ti.y - 70 * u, ti.x + 15 * u, ti.y - 56 * u) }, c(Color(0xFFE8E2D0)))
        drawLine(c(PINO_OSC), Offset(ti.x + 15 * u, ti.y - 56 * u), Offset(ti.x + 20 * u, ti.y - 86 * u), 0.7f * u)
        val tb = Offset(w * 0.78f, frenteY - 12 * u)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(tb.x - 11 * u, tb.y - 1 * u), Size(24 * u, 6 * u))
        drawRect(c(HIERRO), Offset(tb.x - 10 * u, tb.y - 4 * u), Size(20 * u, 4 * u))
        drawArc(c(LATON), 180f, 180f, true, Offset(tb.x - 9 * u, tb.y - 13 * u), Size(18 * u, 18 * u))
        drawArc(Color.White.copy(alpha = 0.4f), 200f, 50f, false, Offset(tb.x - 7 * u, tb.y - 11 * u), Size(14 * u, 14 * u), style = Stroke(1 * u))
        drawCircle(c(HIERRO), 1.6f * u, Offset(tb.x, tb.y - 14 * u))
        val ov = Offset(w * 0.93f, frenteY - 14 * u)
        drawCircle(Color.Black.copy(alpha = 0.25f), 10 * u, Offset(ov.x + 1.5f * u, ov.y + 3 * u))
        drawCircle(c(CORDEL), 10 * u, ov)
        for (q in 0 until 5) drawArc(c(mezcla(CORDEL, Color.Black, 0.25f)), 20f + q * 30f, 140f, false, Offset(ov.x - 10 * u + q * 1.5f * u, ov.y - 10 * u + q * u), Size(20 * u - q * 3 * u, 20 * u - q * 2 * u), style = Stroke(0.7f * u))
        drawLine(c(CORDEL), Offset(ov.x - 8 * u, ov.y + 5 * u), Offset(ov.x - 26 * u, ov.y + 9 * u), 1 * u)

        // ---- El cuaderno del pueblo.
        val g = grosorLibreta(e.hojas, u, minimo = 3.5f)
        val ancho = w * 0.48f
        val suelta = (a / 0.14f).coerceIn(0f, 1f)
        val angulo = giroTapa(((a - 0.1f) / 0.9f).coerceIn(0f, 1f))
        val corre = ancho * 0.2f * (1f - cos(angulo)) / 2f
        val x0 = w * 0.28f + corre; val x1 = x0 + ancho
        val fy = frenteY - 10 * u
        val hondo = h * 0.24f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val cuero = c(CUERO)
        val dorado = papel(DORADO, oscuro, false)
        sombraSuave(Rect(x0, fy - hondo, x1, fy), 3 * u, 9 * u, if (oscuro) 0.45f else 0.3f)
        // Los papeles sueltos que asoman por arriba, más cuantas más hojas.
        val sueltos = if (e.hojas <= 0) 0 else (1 + e.hojas / 14).coerceAtMost(7)
        for (q in 0 until sueltos) {
            val sx = x0 + 10 * u + (ancho - 50 * u) * azar(e.semilla, 10 + q)
            val sw = (18 + 22 * azar(e.semilla, 20 + q)) * u
            val sale = (4 + 7 * azar(e.semilla, 30 + q)) * u
            rotate(-5f + 10f * azar(e.semilla, 40 + q), Offset(sx + sw / 2, arriba)) {
                drawRect(if (q % 3 == 1) c(MANILA) else gaceta, Offset(sx, arriba - sale), Size(sw, sale + 6 * u))
                drawLine(c(TINTA).copy(alpha = 0.35f), Offset(sx + 3 * u, arriba - sale + 3 * u), Offset(sx + sw - 5 * u, arriba - sale + 3 * u), 0.7f * u)
            }
        }
        drawRoundRect(mezcla(cuero, Color.Black, 0.25f), Offset(x0, fy - hondo), Size(ancho, hondo), CornerRadius(3 * u))
        cantoHojas(Rect(x0 + 2 * u, tapaY + 2 * u, x1 - 2 * u, fy - 2 * u), mezcla(gaceta, Color.Black, 0.06f), c(TINTA), e.semilla)
        drawRect(mezcla(cuero, Color.Black, 0.35f), Offset(x0, fy - 2 * u), Size(ancho, 2 * u))
        // La cinta de registro.
        drawPath(Path().apply { moveTo(x0 + ancho * 0.62f, tapaY); lineTo(x0 + ancho * 0.62f + 4 * u, tapaY); lineTo(x0 + ancho * 0.6f + 5 * u, fy + 12 * u); lineTo(x0 + ancho * 0.6f + 2.5f * u, fy + 9 * u); lineTo(x0 + ancho * 0.6f, fy + 12 * u); close() }, c(LADRILLO))
        // La hoja rayada, que se ve al abrir.
        if (angulo > 0.01f) {
            val r = Rect(x0 + 3 * u, arriba + 2 * u, x1 - 3 * u, tapaY + 1 * u)
            drawRect(gaceta, r.topLeft, r.size)
            drawLine(c(LADRILLO).copy(alpha = 0.5f), Offset(r.left + 14 * u, r.top), Offset(r.left + 14 * u, r.bottom), 0.7f * u)
            var ry = r.top + 14 * u; var q = 0
            while (ry < r.bottom - 3 * u) {
                drawLine(c(MARINO).copy(alpha = 0.18f), Offset(r.left, ry), Offset(r.right, ry), 0.6f * u)
                val fin = r.left + r.width * (if (ry < r.top + r.height * 0.6f) 0.58f else 0.92f) * (0.6f + 0.4f * azar(e.semilla, 120 + q))
                drawPath(Path().apply {
                    moveTo(r.left + 17 * u, ry - 2 * u); var xx = r.left + 17 * u
                    while (xx < fin) { quadraticTo(xx + 1.5f * u, ry - 5 * u, xx + 3 * u, ry - 2 * u); xx += 3 * u }
                }, c(TINTA).copy(alpha = 0.55f), style = Stroke(0.7f * u))
                ry += 7 * u; q++
            }
            textoEn(medidor, "Nº ${e.hojas}", Rect(r.left + 16 * u, r.top + 2 * u, r.left + r.width * 0.4f, r.top + 12 * u), letra, c(TINTA), 9f, minimo = 5f)
            // El dibujito: la casa con su chimenea y el árbol.
            val dc = Offset(r.right - r.width * 0.22f, r.top + r.height * 0.42f)
            val ds = minOf(r.height * 0.22f, 14 * u)
            val trazo = c(TINTA).copy(alpha = 0.7f)
            drawRect(trazo, Offset(dc.x - ds, dc.y - ds * 0.2f), Size(ds * 1.6f, ds), style = Stroke(0.8f * u))
            drawPath(Path().apply { moveTo(dc.x - ds * 1.15f, dc.y - ds * 0.2f); lineTo(dc.x - ds * 0.2f, dc.y - ds); lineTo(dc.x + ds * 0.75f, dc.y - ds * 0.2f) }, trazo, style = Stroke(0.8f * u))
            drawRect(trazo, Offset(dc.x - ds * 0.4f, dc.y + ds * 0.3f), Size(ds * 0.35f, ds * 0.5f), style = Stroke(0.7f * u))
            drawCircle(trazo, ds * 0.45f, Offset(dc.x + ds * 1.4f, dc.y - ds * 0.1f), style = Stroke(0.8f * u))
            drawLine(trazo, Offset(dc.x + ds * 1.4f, dc.y + ds * 0.35f), Offset(dc.x + ds * 1.4f, dc.y + ds * 0.8f), 0.8f * u)
            drawLine(trazo, Offset(dc.x - ds * 1.4f, dc.y + ds * 0.8f), Offset(dc.x + ds * 1.9f, dc.y + ds * 0.8f), 0.7f * u)
            if (cos(angulo) > 0f) sombraTapa(x0 + ancho * cos(angulo), arriba, ancho * 0.24f * sin(angulo), tapaY - arriba)
        }
        // La tapa de cuero, que gira sobre el lomo.
        tapaLateral(x0, angulo, 0.42f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            drawRoundRect(cuero, r.topLeft, Size(r.width, r.height + 2.5f * u), CornerRadius(3 * u))
            drawRect(mezcla(cuero, Color.Black, 0.3f), Offset(r.left, r.bottom), Size(r.width, 2.5f * u))
            // El grano del cuero y el desgaste de las orillas.
            for (q in 0 until 50) drawCircle(Color.Black.copy(alpha = 0.06f), (0.5f + azar(e.semilla, 200 + q)) * u, Offset(r.left + r.width * azar(e.semilla, 300 + q), r.top + r.height * azar(e.semilla, 400 + q)))
            drawRoundRect(Color.White.copy(alpha = 0.07f), r.deflate(1.5f * u).topLeft, r.deflate(1.5f * u).size, CornerRadius(2 * u), style = Stroke(2 * u))
            // El lomo, con sus nervios.
            drawRect(mezcla(cuero, Color.Black, 0.2f), r.topLeft, Size(10 * u, r.height))
            for (q in 1..3) drawLine(mezcla(cuero, Color.Black, 0.45f), Offset(r.left, r.top + r.height * q / 4f), Offset(r.left + 10 * u, r.top + r.height * q / 4f), 1.6f * u)
            // El pespunte.
            drawRoundRect(c(CORDEL).copy(alpha = 0.6f), Offset(r.left + 14 * u, r.top + 4 * u), Size(r.width - 18 * u, r.height - 8 * u), CornerRadius(2 * u),
                style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
            // El cuadro hundido con el nombre y la cuenta, en dorado.
            val cu = Rect(r.left + 24 * u, r.top + r.height * 0.18f, r.right - r.width * 0.2f, r.top + r.height * 0.78f)
            drawRect(mezcla(cuero, Color.Black, 0.22f), cu.topLeft, cu.size)
            drawRect(Color.Black.copy(alpha = 0.3f), cu.topLeft, Size(cu.width, 1.4f * u))
            drawRect(dorado.copy(alpha = 0.8f), cu.deflate(2.5f * u).topLeft, cu.deflate(2.5f * u).size, style = Stroke(0.8f * u))
            listOf(cu.topLeft, Offset(cu.right, cu.top), Offset(cu.left, cu.bottom), Offset(cu.right, cu.bottom)).forEach { o ->
                val p = Offset(o.x + (if (o.x > cu.center.x) -2.5f else 2.5f) * u, o.y + (if (o.y > cu.center.y) -2.5f else 2.5f) * u)
                drawPath(Path().apply { moveTo(p.x, p.y - 2.4f * u); lineTo(p.x + 2.4f * u, p.y); lineTo(p.x, p.y + 2.4f * u); lineTo(p.x - 2.4f * u, p.y); close() }, dorado)
            }
            val corte = cu.top + cu.height * 0.6f
            textoEn(medidor, e.nombre, Rect(cu.left + 6 * u, cu.top + 3 * u, cu.right - 6 * u, corte), letra, dorado, 14f, minimo = 6f)
            drawLine(dorado.copy(alpha = 0.6f), Offset(cu.center.x - cu.width * 0.2f, corte), Offset(cu.center.x + cu.width * 0.2f, corte), 0.6f * u)
            textoEn(medidor, e.cuenta, Rect(cu.left + 6 * u, corte + 0.5f * u, cu.right - 6 * u, cu.bottom - 3 * u), letra, dorado.copy(alpha = 0.9f), 9.5f, minimo = 5f)
            // Los florones dorados de las esquinas de fuera.
            listOf(Offset(r.right - 8 * u, r.top + 8 * u), Offset(r.right - 8 * u, r.bottom - 8 * u)).forEach { o -> drawPath(estrella(o, 3.4f * u, 4, 0.4f), dorado.copy(alpha = 0.75f)) }
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.45f * sombra), r.topLeft, Size(r.width, r.height + 2.5f * u))
        }, dentro = { sombra ->
            // Por dentro: la guarda de papel jaspeado, con su orilla de cuero.
            val r = Rect(x0 - ancho, arriba, x0, tapaY + 2.5f * u)
            drawRect(cuero, r.topLeft, r.size)
            val gu = r.deflate(4 * u)
            drawRect(c(GACETA), gu.topLeft, gu.size)
            clipRect(gu.left, gu.top, gu.right, gu.bottom) {
                val tonos = listOf(MARINO, LADRILLO, Color(0xFF6A8A4A), MANILA)
                for (q in 0 until 26) {
                    val o = Offset(gu.left + gu.width * azar(e.semilla, 500 + q), gu.top + gu.height * azar(e.semilla, 600 + q))
                    val rr = (5 + 10 * azar(e.semilla, 700 + q)) * u
                    drawArc(c(tonos[q % tonos.size]).copy(alpha = 0.55f), 360f * azar(e.semilla, 800 + q), 200f, false, Offset(o.x - rr, o.y - rr * 0.6f), Size(rr * 2, rr * 1.2f), style = Stroke(2.2f * u))
                }
            }
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size)
        })
        // La correa con su hebilla: abrochada por el canto; al abrir, se suelta y cae por delante.
        val cy = arriba + (tapaY - arriba) * 0.5f
        val correa = c(mezcla(CUERO, Color.Black, 0.35f))
        drawRect(correa, Offset(x1 - 1 * u, cy - 4 * u), Size(5 * u, fy - cy + 4 * u))
        val pivote = Offset(x1 + 2 * u, cy)
        rotate(-95f * suelta, pivote) {
            if (angulo < 0.02f || suelta > 0.5f) {
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(x1 - ancho * 0.24f + 1 * u, cy - 4 * u + 1.5f * u), Size(ancho * 0.24f + 3 * u, 8 * u))
                drawRect(correa, Offset(x1 - ancho * 0.24f, cy - 4 * u), Size(ancho * 0.24f + 3 * u, 8 * u))
                drawLine(c(CORDEL).copy(alpha = 0.5f), Offset(x1 - ancho * 0.24f, cy - 2.5f * u), Offset(x1, cy - 2.5f * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u)))
                val he = Rect(x1 - ancho * 0.2f, cy - 6.5f * u, x1 - ancho * 0.2f + 10 * u, cy + 6.5f * u)
                drawRoundRect(c(LATON), he.topLeft, he.size, CornerRadius(2 * u), style = Stroke(2 * u))
                drawLine(c(LATON), Offset(he.center.x, he.top), Offset(he.center.x, he.bottom), 1.4f * u)
            }
        }
    }

    /**
     * La pestaña de un día: una etiqueta de papel de estraza clavada con una tachuela de latón, con la orilla de abajo picada,
     * el día a pluma, la letra de la semana y el lacre del color del ánimo (sin lacre, una raya a lápiz).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val estraza = papel(MANILA, oscuro, false)
        rotate(-3f + 6f * azar(e.semilla, 1), Offset(w / 2, 6 * u)) {
            val r = Rect(4 * u, 3 * u, w - 4 * u, h - 4 * u)
            val p = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 2.5f * u)
                var x = r.right; var abajo = true
                while (x > r.left) { x -= 3 * u; lineTo(maxOf(x, r.left), if (abajo) r.bottom else r.bottom - 2.5f * u); abajo = !abajo }
                close()
            }
            translate(1.2f * u, 1.6f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
            drawPath(p, estraza)
            for (q in 0 until 10) drawLine(Color.Black.copy(alpha = 0.06f), Offset(r.left + r.width * azar(e.semilla, 20 + q), r.top + r.height * azar(e.semilla, 30 + q)), Offset(r.left + r.width * azar(e.semilla, 20 + q) + 4 * u, r.top + r.height * azar(e.semilla, 30 + q)), 0.6f * u)
            drawCircle(c(LATON), 2.6f * u, Offset(w / 2, r.top + 4 * u))
            drawCircle(Color.White.copy(alpha = 0.5f), 0.8f * u, Offset(w / 2 - 0.8f * u, r.top + 3.2f * u))
            textoEn(medidor, e.semana, Rect(r.left + 1 * u, r.top + 1 * u, r.left + 11 * u, r.top + 10 * u), letra, c(TINTA).copy(alpha = 0.8f), 8f, minimo = 5f)
            textoEn(medidor, e.dia, Rect(r.left, r.top + 9 * u, r.right, r.top + 32 * u), letra, c(TINTA), 20f, minimo = 8f)
            val s = Offset(w / 2, r.top + 40 * u)
            if (e.tono != null) {
                drawPath(contornoIrregular(Rect(s.x - 6.5f * u, s.y - 6 * u, s.x + 6.5f * u, s.y + 6 * u), e.semilla, 9, 0.18f), e.tono)
                drawCircle(Color.Black.copy(alpha = 0.18f), 3.6f * u, s, style = Stroke(0.8f * u))
                drawCircle(Color.White.copy(alpha = 0.3f), 1.2f * u, Offset(s.x - 2 * u, s.y - 2 * u))
            } else drawLine(c(TINTA).copy(alpha = 0.4f), Offset(s.x - 6 * u, s.y), Offset(s.x + 6 * u, s.y - 1 * u), 1 * u)
        }
    }

    /** El rótulo de un mes: una tabla de pino clavada, con las orillas tostadas y el mes grabado a fuego. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tam = 15f
        val largo = (anchoTexto(medidor, texto, letra, tam) + 50 * u).coerceIn(w * 0.45f, w - 6 * u)
        val r = Rect(w / 2 - largo / 2, 3 * u, w / 2 + largo / 2, h - 3 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.5f * u, r.top + 2 * u), r.size)
        drawRect(papel(PINO, oscuro, false), r.topLeft, r.size)
        vetas(r, papel(VETA, oscuro, false).copy(alpha = 0.45f), 77, densidad = 0.8f)
        drawRect(c(PINO_OSC).copy(alpha = 0.7f), r.topLeft, r.size, style = Stroke(3 * u))
        drawRect(c(RENDIJA).copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(1 * u))
        listOf(r.left + 9 * u, r.right - 9 * u).forEach { x -> drawCircle(c(CLAVO), 2 * u, Offset(x, r.center.y)); drawCircle(Color.White.copy(alpha = 0.3f), 0.7f * u, Offset(x - 0.6f * u, r.center.y - 0.6f * u)) }
        textoEn(medidor, texto, Rect(r.left + 18 * u, r.top, r.right - 18 * u, r.bottom), letra, Color(0xFF3A2414), tam, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El papel verjurado de la carta plegada, su sombra en los dobleces y el rojo de la oblea. */
    private val PLIEGO = Color(0xFFEDE4CC)
    private val PLIEGO_SOMBRA = Color(0xFFC9BC98)
    private val OBLEA = Color(0xFF9E2B25)

    /**
     * La carta plegada del correo del pueblo, sin sobre: el pliego de papel verjurado (se ven sus líneas de cadena y alguna
     * mancha de óxido) doblado en paquete, con los dobleces de los costados marcados y la solapa de arriba en pico. Sobre la
     * solapa, la marca de porte a mano («PAGADO» en su óvalo) y el matasellos de ondas del correo; abajo, la dirección
     * escrita con pluma: a quién va y las fechas. El sello es la oblea roja de borde dentado, con la marca punteada del sello
     * de mano, en la punta de la solapa: cuando llega el correo, el quinqué la enciende de ámbar; al abrir, se parte. Las de al
     * futuro van atadas con bramante en dos vueltas, con su lazo sobre la oblea. Dentro, la carta, a renglones.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        fun pp(x: Color) = papel(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val punta = s.top + h * 0.5f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val sc = Offset(s.center.x, punta - h * 0.02f)
        val sr = h * 0.1f
        fun oblea(alfa: Float) {
            fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
            val roja = mezcla(c(OBLEA), Color(0xFFE0603A), e.brillo * 0.5f)
            drawPath(estrella(sc + Offset(0.6f * u, 1.1f * u), sr, 30, 0.92f), a(Color.Black.copy(alpha = 0.3f)))
            drawPath(estrella(sc, sr, 30, 0.92f), a(roja))
            val claro = mezcla(roja, Color.White, 0.35f)
            for (j in 0 until 20) { val ang = j * PI / 10; drawCircle(a(claro), sr * 0.04f, Offset(sc.x + (sr * 0.64f * cos(ang)).toFloat(), sc.y + (sr * 0.64f * sin(ang)).toFloat())) }
            drawLine(a(claro), Offset(sc.x - sr * 0.32f, sc.y), Offset(sc.x + sr * 0.32f, sc.y), sr * 0.07f)
            drawLine(a(claro), Offset(sc.x, sc.y - sr * 0.32f), Offset(sc.x, sc.y + sr * 0.32f), sr * 0.07f)
            drawCircle(a(mezcla(roja, Color.Black, 0.25f)), sr * 0.8f, sc, style = Stroke(sr * 0.05f))
        }
        val solapa = Path().apply {
            moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right, bis + h * 0.06f)
            lineTo(sc.x + w * 0.04f, punta - h * 0.02f); quadraticTo(sc.x, punta + h * 0.02f, sc.x - w * 0.04f, punta - h * 0.02f)
            lineTo(s.left, bis + h * 0.06f); close()
        }

        sombraSuave(s, 1 * u, 5 * u, 0.26f)
        // ---- La cara de dentro del pliego (se ve al abrir).
        drawRect(c(PLIEGO_SOMBRA), s.topLeft, s.size)
        // ---- La solapa en pico.
        fun DrawScope.pico(adentro: Boolean) {
            if (adentro) {
                drawPath(solapa, pp(mezcla(PLIEGO, PLIEGO_SOMBRA, 0.4f)))
                // Por dentro, la letra de la carta traspasa el papel.
                for (i in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.12f), Offset(s.left + w * 0.2f, bis + h * (0.08f + i * 0.06f)), Offset(s.right - w * (0.2f + 0.1f * i), bis + h * (0.08f + i * 0.06f)), 0.8f * u)
                return
            }
            translate(0.8f * u, 1.6f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.18f)) }
            drawPath(solapa, pp(PLIEGO))
            clipPath(solapa) {
                var x = s.left + 6 * u; while (x < s.right) { drawLine(c(PLIEGO_SOMBRA).copy(alpha = 0.45f), Offset(x, bis), Offset(x, punta), 0.5f * u); x += 9 * u }
            }
            drawPath(solapa, c(PLIEGO_SOMBRA), style = Stroke(0.8f * u))
            // La marca de porte en su óvalo y el matasellos de ondas.
            val po = Rect(s.left + w * 0.06f, bis + h * 0.07f, s.left + w * 0.3f, bis + h * 0.25f)
            rotate(-8f, po.center) {
                drawOval(c(OBLEA).copy(alpha = 0.7f), po.topLeft, po.size, style = Stroke(1 * u))
                textoEn(medidor, "PAGADO", po.deflate(2 * u), letra, c(OBLEA).copy(alpha = 0.8f), 7f * k, minimo = 4.5f)
            }
            val mt = Offset(s.right - w * 0.2f, bis + h * 0.15f)
            drawCircle(c(TINTA).copy(alpha = 0.5f), h * 0.08f, mt, style = Stroke(1 * u))
            for (j in 0 until 3) {
                val y = mt.y + (j - 1) * h * 0.04f
                val p = Path(); var x = mt.x - w * 0.02f; p.moveTo(x, y); var sg = 1f
                while (x < s.right - w * 0.02f) { p.quadraticTo(x + w * 0.012f, y - sg * h * 0.02f, x + w * 0.024f, y); x += maxOf(w * 0.024f, 1f); sg = -sg }
                drawPath(p, c(TINTA).copy(alpha = 0.45f), style = Stroke(0.9f * u))
            }
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2f * u * e.romper); rotate(-8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, true, 6, e.semilla)) { oblea(1f) }
            }
        }
        if (!cerrada) solapaCarta(bis, ab) { pico(it) }

        // ---- La carta, a renglones.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
            val hoja = Rect(s.left + w * 0.07f, arriba0 - alza, s.right - w * 0.07f, arriba0 - alza + h * 0.88f)
            val hojaColor = if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja
            drawRect(Color.Black.copy(alpha = 0.18f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
            drawRect(hojaColor, hoja.topLeft, hoja.size)
            drawLine(c(OBLEA).copy(alpha = 0.35f), Offset(hoja.left + 8 * u, hoja.top), Offset(hoja.left + 8 * u, hoja.bottom), 0.6f * u)
            var y = hoja.top + 13 * u
            while (y < hoja.bottom - 3 * u) { drawLine(e.renglones, Offset(hoja.left + 4 * u, y), Offset(hoja.right - 4 * u, y), 0.6f * u); y += 11 * u }
        }

        // ---- El paquete: el pliego, los dobleces de los costados y la dirección.
        drawRect(pp(PLIEGO), s.topLeft, s.size)
        var x = s.left + 6 * u; while (x < s.right) { drawLine(c(PLIEGO_SOMBRA).copy(alpha = 0.4f), Offset(x, s.top), Offset(x, s.bottom), 0.5f * u); x += 9 * u }
        // Los costados doblados hacia dentro: dos triángulos de sombra que bajan desde las esquinas.
        listOf(s.left to 1f, s.right to -1f).forEach { (bx, sg) ->
            drawPath(Path().apply { moveTo(bx, s.top); lineTo(bx + sg * w * 0.1f, s.top + h * 0.45f); lineTo(bx, s.bottom); close() }, c(PLIEGO_SOMBRA).copy(alpha = 0.35f))
            drawLine(c(PLIEGO_SOMBRA), Offset(bx + sg * w * 0.1f, s.top + h * 0.45f), Offset(bx, s.bottom), 0.7f * u)
        }
        // Manchas de óxido del papel.
        for (i in 0 until 4) drawCircle(c(Color(0xFFB08A52)).copy(alpha = 0.18f), (1.5f + 2.5f * azar(e.semilla, i + 50)) * u,
            Offset(s.left + w * (0.15f + 0.7f * azar(e.semilla, i + 60)), s.top + h * (0.6f + 0.35f * azar(e.semilla, i + 70))))
        drawRect(c(PLIEGO_SOMBRA), s.topLeft, s.size, style = Stroke(0.8f * u))
        if (!cerrada) drawLine(Color.Black.copy(alpha = 0.3f), Offset(s.left, s.top + 0.8f * u), Offset(s.right, s.top + 0.8f * u), 1.4f * u)
        // La dirección, con pluma, y su rúbrica.
        val dir = Rect(s.left + w * 0.1f, s.top + h * 0.58f, s.right - w * 0.1f, s.bottom - h * 0.06f)
        textoEn(medidor, e.linea1, Rect(dir.left, dir.top, dir.right, dir.top + dir.height * 0.56f), letra, TINTA, 12f, minimo = 7f)
        drawLine(TINTA.copy(alpha = 0.5f), Offset(dir.left + dir.width * 0.12f, dir.top + dir.height * 0.56f), Offset(dir.right - dir.width * 0.12f, dir.top + dir.height * 0.54f), 0.7f * u)
        textoEn(medidor, e.linea2, Rect(dir.left, dir.top + dir.height * 0.58f, dir.right, dir.bottom), letra, TINTA.copy(alpha = 0.85f), 9.5f, minimo = 6f)

        // ---- La solapa cerrada.
        if (cerrada) solapaCarta(bis, ab) { pico(it) }
        // ---- El bramante en dos vueltas, con su lazo sobre la oblea.
        if (e.cordon > 0f && cerrada) {
            val cuerda = c(CORDEL)
            val f1 = (e.cordon * 2f).coerceIn(0f, 1f); val f2 = (e.cordon * 2f - 1f).coerceIn(0f, 1f)
            listOf(sc.y - h * 0.035f to f1, sc.y + h * 0.035f to f2).forEach { (y, fr) ->
                if (fr > 0f) {
                    val pts = listOf(Offset(s.left - 2 * u, y + 1 * u), Offset(sc.x, y), Offset(s.right + 2 * u, y - 1 * u))
                    drawPath(recorrido(pts, fr), c(Color(0xFF6E5530)), style = Stroke(1.9f * u, cap = StrokeCap.Round))
                    drawPath(recorrido(pts, fr), cuerda, style = Stroke(1.2f * u, cap = StrokeCap.Round))
                }
            }
            if (e.cordon > 0.95f) {
                // El lazo: dos orejas y dos cabos.
                listOf(-1f, 1f).forEach { sg ->
                    drawOval(cuerda, Offset(sc.x + (if (sg < 0) -sr * 1.9f else sr * 0.5f), sc.y - sr * 1.25f), Size(sr * 1.4f, sr * 0.8f), style = Stroke(1.2f * u))
                    drawLine(cuerda, Offset(sc.x, sc.y - sr * 0.6f), Offset(sc.x + sg * sr * 0.8f, sc.y + sr * 1.3f), 1.2f * u, StrokeCap.Round)
                }
            }
        }
        // ---- La oblea: entera (cayendo al sellar), encendida por el quinqué si ya llegó, o partida.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.65f * e.brillo), Color.Transparent), center = sc, radius = sr * 3.2f), sr * 3.2f, sc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc) }) { oblea(e.alfaSello.coerceIn(0f, 1f)) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2f * u * e.romper); rotate(9f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, false, 6, e.semilla)) { oblea(1f) }
            }
        }
    }

    // La carta a renglones que asoma del pliego abierto.
    override val cajaPrevia = Rect(0.15f, 0.16f, 0.86f, 0.32f)

    override val altoBuzon: Dp = 212.dp

    /**
     * El rincón del correo en el almacén: contra la pared de tablas, colgada de su clavija, la saca de lona del correo con la
     * estrella estarcida, la boca fruncida con la correa y el candado de latón, y las cartas leídas asomando; a la derecha, en
     * la repisa sobre sus escuadras, la bandeja de las que ya llegaron (bajo el quinqué, que se enciende cuando hay alguna) y
     * el fajo de las cerradas, atado con bramante. Cada montón lleva su tarjeta de envío colgada con cordel, con su nombre y
     * su cuenta a pluma. Abajo, el piso de tablones.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF070504), 0.45f) else c(x)
        fun pp(x: Color) = papel(x, oscuro, false)
        // ---- La pared de tablas y el piso.
        val piso = h - 18 * u
        var x = 0f; var k = 0
        while (x < w) {
            val tw = (30 + 10 * azar(3, k)) * u
            val r = Rect(x, 0f, x + tw, piso)
            drawRect(f(mezcla(PINO, PINO_OSC, 0.3f * azar(5, k))), r.topLeft, r.size)
            vetas(r, f(VETA).copy(alpha = 0.35f), 40 + k, densidad = 0.5f, vertical = true)
            drawLine(f(RENDIJA), Offset(x, 0f), Offset(x, piso), 1.4f * u)
            drawCircle(f(CLAVO), 1.2f * u, Offset(x + 5 * u, 12 * u)); drawCircle(f(CLAVO), 1.2f * u, Offset(x + 5 * u, piso - 12 * u))
            x += tw; k++
        }
        drawRect(f(TABLON), Offset(0f, piso), Size(w, h - piso))
        drawLine(f(RENDIJA), Offset(0f, piso), Offset(w, piso), 1.5f * u)
        val listas = e.listas > 0
        // ---- La luz del quinqué, si hay cartas que ya llegaron.
        val lq = Offset(w * 0.66f, 58 * u)
        if (listas) drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = if (oscuro) 0.4f else 0.28f), Color.Transparent), center = lq, radius = 110 * u), 110 * u, lq)

        // ---- La saca de lona, colgada de la clavija.
        val clav = Offset(w * 0.24f, 16 * u)
        drawRect(c(PINO_OSC), Offset(clav.x - 14 * u, clav.y - 4 * u), Size(28 * u, 8 * u))
        drawRoundRect(c(TABLON), Offset(clav.x - 3 * u, clav.y - 2 * u), Size(6 * u, 12 * u), CornerRadius(3 * u))
        val saca = Path().apply {
            moveTo(w * 0.14f, 52 * u); quadraticTo(w * 0.24f, 44 * u, w * 0.34f, 52 * u)
            quadraticTo(w * 0.44f, 110 * u, w * 0.42f, piso - 8 * u); quadraticTo(w * 0.24f, piso + 2 * u, w * 0.06f, piso - 8 * u)
            quadraticTo(w * 0.04f, 110 * u, w * 0.14f, 52 * u); close()
        }
        // Las cuerdas de la clavija a la boca.
        drawLine(c(CORDEL), clav + Offset(0f, 8 * u), Offset(w * 0.15f, 52 * u), 1.6f * u)
        drawLine(c(CORDEL), clav + Offset(0f, 8 * u), Offset(w * 0.33f, 52 * u), 1.6f * u)
        // Las cartas leídas que asoman por la boca.
        val abiertas = e.abiertas.coerceAtMost(8)
        for (j in 0 until abiertas) {
            val cx = w * (0.16f + 0.16f * j / 7f)
            rotate((azar(17, j) - 0.5f) * 30f, Offset(cx, 56 * u)) {
                drawRect(pp(PLIEGO), Offset(cx - 9 * u, 36 * u + 4 * u * azar(19, j)), Size(18 * u, 24 * u))
                drawRect(c(PLIEGO_SOMBRA), Offset(cx - 9 * u, 36 * u + 4 * u * azar(19, j)), Size(18 * u, 24 * u), style = Stroke(0.6f * u))
                drawLine(TINTA.copy(alpha = 0.35f), Offset(cx - 6 * u, 42 * u), Offset(cx + 5 * u, 42 * u), 0.6f * u)
            }
        }
        drawPath(saca, c(COSTAL))
        clipPath(saca) {
            for (i in 0 until 26) drawLine(c(mezcla(COSTAL, VETA, 0.25f)).copy(alpha = 0.5f), Offset(w * 0.04f + i * 5 * u, 40 * u), Offset(w * 0.04f + i * 5 * u, piso), 0.5f * u)
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(w * 0.3f, 40 * u), Size(w * 0.14f, piso))
            // La estrella estarcida y las franjas.
            drawPath(estrella(Offset(w * 0.24f, 112 * u), 18 * u, 5, 0.42f), c(MARINO).copy(alpha = 0.8f))
            listOf(140f, 148f).forEach { y -> drawRect(c(LADRILLO).copy(alpha = 0.75f), Offset(w * 0.08f, y * u), Size(w * 0.32f, 4 * u)) }
        }
        drawPath(saca, c(mezcla(COSTAL, VETA, 0.5f)), style = Stroke(1.2f * u))
        // La boca fruncida: la correa de cuero y el candado.
        drawRect(c(Color(0xFF5A3A22)), Offset(w * 0.13f, 58 * u), Size(w * 0.22f, 6 * u))
        drawRect(c(LATON), Offset(w * 0.235f - 4 * u, 57 * u), Size(8 * u, 8 * u), style = Stroke(1.4f * u))
        drawArc(c(HIERRO), 180f, 180f, false, Offset(w * 0.24f - 4 * u, 64 * u), Size(8 * u, 8 * u), style = Stroke(1.6f * u))
        drawRoundRect(c(LATON), Offset(w * 0.24f - 5 * u, 68 * u), Size(10 * u, 9 * u), CornerRadius(1.5f * u))
        drawCircle(c(HIERRO), 1.1f * u, Offset(w * 0.24f, 72 * u))
        tarjetaEnvio(Offset(w * 0.37f, 64 * u), "LEÍDAS", e.abiertas, -10f, medidor, oscuro)

        // ---- La repisa con sus escuadras.
        val rep = Rect(w * 0.48f, 128 * u, w * 0.97f, 136 * u)
        listOf(rep.left + 16 * u, rep.right - 16 * u).forEach { ex ->
            drawPath(Path().apply { moveTo(ex - 3 * u, rep.bottom); lineTo(ex + 3 * u, rep.bottom); lineTo(ex + 3 * u, rep.bottom + 28 * u); close() }, c(PINO_OSC))
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left + 2 * u, rep.bottom), Size(rep.width, 4 * u))
        drawRect(c(PINO_LUZ), rep.topLeft, rep.size)
        drawRect(c(PINO_OSC), Offset(rep.left, rep.bottom - 2 * u), Size(rep.width, 2 * u))
        // El quinqué, en la pared sobre la repisa.
        drawRect(c(HIERRO), Offset(lq.x - 2 * u, lq.y + 30 * u), Size(4 * u, 12 * u))
        drawRect(c(HIERRO), Offset(lq.x - 10 * u, lq.y + 40 * u), Size(20 * u, 2.5f * u))
        quinque(lq + Offset(0f, 12 * u), 30 * u, c(HIERRO), if (listas) LLAMA else c(Color(0xFF5A4A30)), c(LATON))
        // La bandeja de las que llegaron: las cartas en abanico, con la oblea encendida.
        val band = Rect(rep.left + 8 * u, rep.top - 8 * u, rep.left + rep.width * 0.46f, rep.top)
        val nl = e.listas.coerceAtMost(8)
        for (j in 0 until nl) {
            val o = Offset(band.center.x + (j - (nl - 1) / 2f) * 5 * u, band.top - 6 * u)
            rotate((j - (nl - 1) / 2f) * 7f, Offset(o.x, band.bottom)) {
                drawRect(pp(PLIEGO), Offset(o.x - 14 * u, o.y - 14 * u), Size(28 * u, 24 * u))
                drawRect(c(PLIEGO_SOMBRA), Offset(o.x - 14 * u, o.y - 14 * u), Size(28 * u, 24 * u), style = Stroke(0.6f * u))
                drawCircle(Color(0xFFE0603A), 3.2f * u, Offset(o.x, o.y - 3 * u))
                drawCircle(LLAMA.copy(alpha = 0.5f), 5 * u, Offset(o.x, o.y - 3 * u), style = Stroke(1 * u))
            }
        }
        drawRect(c(LATON), band.topLeft, band.size)
        drawRect(c(mezcla(LATON, Color.White, 0.3f)), band.topLeft, Size(band.width, 1.2f * u))
        tarjetaEnvio(Offset(band.left + 6 * u, rep.bottom + 2 * u), "LLEGARON", e.listas, 6f, medidor, oscuro)
        // El fajo de las cerradas, atado con bramante.
        val ns = e.selladas.coerceAtMost(8)
        val fajo = Rect(rep.right - rep.width * 0.46f, rep.top - 6 * u - ns * 4.5f * u, rep.right - 8 * u, rep.top)
        for (j in 0 until ns) {
            val y = rep.top - (j + 1) * 4.5f * u
            val dx = (azar(23, j) - 0.5f) * 5 * u
            drawRect(pp(mezcla(PLIEGO, PLIEGO_SOMBRA, 0.25f * (j % 2))), Offset(fajo.left + dx, y), Size(fajo.width, 4.5f * u))
            drawLine(c(PLIEGO_SOMBRA), Offset(fajo.left + dx, y + 4.5f * u), Offset(fajo.right + dx, y + 4.5f * u), 0.6f * u)
            drawCircle(c(OBLEA), 1.6f * u, Offset(fajo.center.x + dx, y + 2.2f * u))
        }
        if (ns > 0) {
            drawLine(c(CORDEL), Offset(fajo.left + fajo.width * 0.3f, rep.top - ns * 4.5f * u - 1 * u), Offset(fajo.left + fajo.width * 0.3f, rep.top), 1.4f * u)
            drawLine(c(CORDEL), Offset(fajo.left - 2 * u, rep.top - ns * 2.2f * u), Offset(fajo.right + 2 * u, rep.top - ns * 2.2f * u), 1.4f * u)
            val nudo = Offset(fajo.left + fajo.width * 0.3f, rep.top - ns * 4.5f * u - 1 * u)
            drawOval(c(CORDEL), Offset(nudo.x - 7 * u, nudo.y - 5 * u), Size(6 * u, 4 * u), style = Stroke(1.2f * u))
            drawOval(c(CORDEL), Offset(nudo.x + 1 * u, nudo.y - 5 * u), Size(6 * u, 4 * u), style = Stroke(1.2f * u))
        }
        tarjetaEnvio(Offset(fajo.center.x, rep.bottom + 2 * u), "CERRADAS", e.selladas, -5f, medidor, oscuro)
    }

    /** Una tarjeta de envío de cartulina colgada de su cordel desde [o], con el nombre del montón y su cuenta a pluma. */
    private fun DrawScope.tarjetaEnvio(o: Offset, nombre: String, n: Int, giro: Float, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val r = Rect(o.x - 4 * u, o.y + 8 * u, o.x + 50 * u, o.y + 40 * u)
        drawLine(t(CORDEL, oscuro, false), o, Offset(r.left + 7 * u, r.center.y), 1 * u)
        rotate(giro, Offset(r.left + 7 * u, r.center.y)) {
            val p = Path().apply { moveTo(r.left + 6 * u, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.left + 6 * u, r.bottom); lineTo(r.left, r.bottom - 6 * u); lineTo(r.left, r.top + 6 * u); close() }
            translate(1 * u, 1.5f * u) { drawPath(p, Color.Black.copy(alpha = 0.25f)) }
            drawPath(p, papel(MANILA, oscuro, false))
            drawCircle(t(PINO_OSC, oscuro, false), 2 * u, Offset(r.left + 7 * u, r.center.y))
            drawCircle(papel(MANILA, oscuro, false), 1 * u, Offset(r.left + 7 * u, r.center.y))
            textoEn(medidor, nombre, Rect(r.left + 11 * u, r.top + 2 * u, r.right - 2 * u, r.top + 12 * u), letra, TINTA, 8f, minimo = 5f)
            drawLine(TINTA.copy(alpha = 0.3f), Offset(r.left + 12 * u, r.top + 12.5f * u), Offset(r.right - 3 * u, r.top + 12.5f * u), 0.5f * u)
            textoEn(medidor, n.toString(), Rect(r.left + 11 * u, r.top + 12 * u, r.right - 2 * u, r.bottom - 1 * u), letra, if (n > 0) t(LADRILLO, oscuro, false) else TINTA, 15f)
        }
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /**
     * El semáforo apagado que la hiedra se tragó: el poste, la caja con sus tres luces (de día queda un verde débil; de noche
     * todas apagadas, con la luna arriba y dos luciérnagas) y la hiedra que trepa.
     */
    private fun DrawScope.semaforo(r: Rect, oscuro: Boolean, noche: Boolean, semilla: Int) {
        val u = density
        val poste = Rect(r.center.x - 2.2f * u, r.top + r.height * 0.5f, r.center.x + 2.2f * u, r.bottom)
        drawRect(t(HORMIGON_OSC, oscuro, noche), poste.topLeft, poste.size)
        val caja = Rect(r.center.x - 9 * u, r.top + 8 * u, r.center.x + 9 * u, r.top + r.height * 0.58f)
        drawRoundRect(t(Color(0xFF2E332C), oscuro, noche), caja.topLeft, caja.size, CornerRadius(3 * u))
        drawRoundRect(t(VARILLA, oscuro, noche).copy(alpha = 0.5f), caja.topLeft, caja.size, CornerRadius(3 * u), style = Stroke(1 * u))
        val luces = listOf(Color(0xFF7A2A20), Color(0xFF7A6020), if (noche) Color(0xFF1E3A24) else Color(0xFF8ED16A))
        luces.forEachIndexed { k, col ->
            val o = Offset(caja.center.x, caja.top + caja.height * (0.2f + 0.3f * k))
            drawCircle(t(col, oscuro, noche), 4.2f * u, o)
            drawArc(Color.Black.copy(alpha = 0.6f), 180f, 180f, true, Offset(o.x - 5.5f * u, o.y - 5.5f * u), Size(11 * u, 6 * u))
        }
        hiedra(Offset(caja.right - 1 * u, caja.top + 2 * u), r.bottom - caja.top - 4 * u, semilla, oscuro, noche)
        hiedra(Offset(caja.left + 2 * u, caja.bottom - 4 * u), r.bottom - caja.bottom, semilla + 7, oscuro, noche)
        musgo(Offset(r.center.x, r.bottom - 2 * u), 7 * u, semilla, oscuro, noche)
        if (noche) {
            val m = Offset(r.right - 4 * u, r.top + 6 * u)
            drawCircle(LUNA.copy(alpha = 0.9f), 5 * u, m)
            listOf(Offset(r.left + 4 * u, r.top + r.height * 0.7f), Offset(r.right - 2 * u, r.top + r.height * 0.55f)).forEach { o ->
                drawCircle(LUCIERNAGA.copy(alpha = 0.25f), 3.4f * u, o); drawCircle(LUCIERNAGA, 1.2f * u, o)
            }
        }
    }

    /**
     * La cabecera de la hoja: a la izquierda, el semáforo apagado con su hiedra (de noche, bajo la luna, con luciérnagas); a la
     * derecha, el letrero de calle verde, colgado de dos cordeles, con su filete blanco, sus clavos, el óxido que le chorrea y
     * una guía de hiedra en la esquina, que lleva el rótulo; debajo, escritas en la hoja, la fecha grande y el detalle, y una
     * tira de cinta plateada que sujeta el borde.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        semaforo(Rect(0f, 0f, 46 * u, h - 2 * u), oscuro, e.noche, e.numero)
        // El letrero, colgado y un poco ladeado.
        val letrero = Rect(56 * u, 12 * u, w - 4 * u, 44 * u)
        listOf(letrero.left + 14 * u, letrero.right - 14 * u).forEach { x ->
            drawLine(t(CORDEL, oscuro, e.noche), Offset(x + (if (x < w / 2) 4 else -4) * u, 0f), Offset(x, letrero.top + 4 * u), 1.1f * u)
        }
        rotate(-1.2f, letrero.center) {
            drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(letrero.left + 1.5f * u, letrero.top + 2.5f * u), letrero.size, CornerRadius(5 * u))
            drawRoundRect(p.acento, letrero.topLeft, letrero.size, CornerRadius(5 * u))
            drawRoundRect(p.color.copy(alpha = 0.85f), letrero.deflate(2.5f * u).topLeft, letrero.deflate(2.5f * u).size, CornerRadius(3.5f * u), style = Stroke(1.2f * u))
            // El óxido que chorrea desde los clavos.
            listOf(letrero.left + 14 * u, letrero.right - 14 * u).forEach { x ->
                drawLine(t(VARILLA, oscuro, e.noche).copy(alpha = 0.45f), Offset(x, letrero.top + 5 * u), Offset(x + 0.6f * u, letrero.top + 14 * u), 2 * u, StrokeCap.Round)
                drawCircle(t(HORMIGON_OSC, oscuro, e.noche), 1.6f * u, Offset(x, letrero.top + 5 * u))
            }
            for (k in 0 until 6) drawCircle(t(VARILLA, oscuro, e.noche).copy(alpha = 0.25f), (1 + 2.2f * azar(e.numero, k)) * u,
                Offset(letrero.left + letrero.width * azar(e.numero, 10 + k), if (k % 2 == 0) letrero.bottom - 3 * u else letrero.top + 3 * u))
            textoEn(medidor, e.rotulo, Rect(letrero.left + 22 * u, letrero.top + 3 * u, letrero.right - 22 * u, letrero.bottom - 3 * u), LetraExtra.letrero,
                p.color, 16f, peso = FontWeight.SemiBold, espaciado = 0.5f, minimo = 7f)
        }
        hiedra(Offset(letrero.right - 6 * u, 0f), 30 * u, e.numero + 3, oscuro, e.noche)
        // La fecha y el detalle, en la hoja.
        fechaFuturo(medidor, e.fecha, Rect(52 * u, 48 * u, w, 96 * u), LetraExtra.letrero, p.tinta, 22f, peso = FontWeight.SemiBold)
        fechaFuturo(medidor, e.detalle, Rect(52 * u, 96 * u, w, h - 2 * u), LetraExtra.overpass, p.tenue, 10.5f, k = 0.86f)
    }

    /**
     * La hoja del cuaderno rescatado: la tira de cinta plateada que la sujeta arriba, el margen a lápiz un poco tembloroso, la
     * grieta que baja desde una esquina, los cercos de agua, el musgo al pie y la hoja de hiedra prensada con sus nervaduras.
     * En modo oscuro la luz entra por el techo roto, de arriba a la derecha, en un rayo; las orillas quedan en la penumbra.
     * De noche, las luciérnagas.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El margen a lápiz.
        drawPath(Path().apply {
            moveTo(28 * u, 0f); var y = 0f; var k = 0
            while (y < h) { y += 40 * u; lineTo(28 * u + (azar(71, k) - 0.5f) * 2.4f * u, y); k++ }
        }, p.tenue.copy(alpha = 0.32f), style = Stroke(0.9f * u))
        // La cinta plateada de arriba.
        rotate(-4f, Offset(w * 0.5f, 58 * u)) {
            drawRect(Color(0xFFB8BCB8).copy(alpha = 0.5f), Offset(w * 0.5f - 42 * u, 50 * u), Size(84 * u, 16 * u))
            var x = w * 0.5f - 42 * u
            while (x < w * 0.5f + 42 * u) { drawLine(Color.White.copy(alpha = 0.2f), Offset(x, 50 * u), Offset(x, 66 * u), 0.6f * u); x += 3 * u }
        }
        // La grieta desde la esquina de arriba a la derecha.
        drawPath(Path().apply {
            moveTo(w, 70 * u); lineTo(w - 12 * u, 84 * u); lineTo(w - 9 * u, 98 * u); lineTo(w - 22 * u, 116 * u); lineTo(w - 18 * u, 132 * u)
            moveTo(w - 9 * u, 98 * u); lineTo(w - 2 * u, 110 * u)
        }, p.tinta.copy(alpha = 0.14f), style = Stroke(0.9f * u, join = StrokeJoin.Round))
        // Los cercos de agua.
        listOf(Triple(0.72f, 0.42f, 34f), Triple(0.2f, 0.7f, 26f), Triple(0.3f, 0.72f, 18f)).forEach { (fx, fy, r) ->
            drawCircle(p.tinta.copy(alpha = 0.045f), r * u, Offset(w * fx, h * fy), style = Stroke(3 * u))
            drawCircle(p.tinta.copy(alpha = 0.03f), r * u - 2 * u, Offset(w * fx + 1 * u, h * fy), style = Stroke(1 * u))
        }
        // El musgo al pie, en la esquina de la izquierda.
        for (k in 0 until 40) {
            val o = Offset(w * 0.28f * azar(81, k) * azar(83, k), h - 14 * u * azar(85, k) - 2 * u)
            drawCircle(t(if (k % 3 == 0) MUSGO_LUZ else MUSGO, oscuro, noche).copy(alpha = 0.3f), (1.2f + 2.2f * azar(87, k)) * u, o)
        }
        // La hoja de hiedra prensada, con sus nervaduras, y otra más chica.
        listOf(Triple(Offset(w - 48 * u, h - 66 * u), 40 * u, 0f), Triple(Offset(w - 86 * u, h - 40 * u), 22 * u, 30f)).forEach { (c, s, g) ->
            rotate(g, c) {
                val hoja = Path().apply {
                    moveTo(c.x, c.y + s * 0.5f); lineTo(c.x - s * 0.55f, c.y + s * 0.35f); lineTo(c.x - s * 0.8f, c.y - s * 0.05f); lineTo(c.x - s * 0.32f, c.y - s * 0.05f)
                    lineTo(c.x, c.y - s * 0.5f); lineTo(c.x + s * 0.32f, c.y - s * 0.05f); lineTo(c.x + s * 0.8f, c.y - s * 0.05f); lineTo(c.x + s * 0.55f, c.y + s * 0.35f); close()
                }
                drawPath(hoja, p.acento.copy(alpha = 0.16f))
                listOf(Offset(c.x, c.y - s * 0.45f), Offset(c.x - s * 0.7f, c.y - s * 0.02f), Offset(c.x + s * 0.7f, c.y - s * 0.02f), Offset(c.x - s * 0.45f, c.y + s * 0.32f), Offset(c.x + s * 0.45f, c.y + s * 0.32f))
                    .forEach { drawLine(p.acento.copy(alpha = 0.22f), Offset(c.x, c.y + s * 0.35f), it, 0.7f * u) }
                drawLine(p.acento.copy(alpha = 0.26f), Offset(c.x, c.y + s * 0.5f), Offset(c.x - s * 0.15f, c.y + s * 1.05f), 1.2f * u)
            }
        }
        // La luz: el rayo que entra por el techo roto y la penumbra.
        if (oscuro && !p.oscura) {
            drawPath(Path().apply { moveTo(w * 0.62f, 0f); lineTo(w, 0f); lineTo(w, h * 0.2f); lineTo(w * 0.3f, h * 0.75f); lineTo(0f, h * 0.75f); lineTo(0f, h * 0.6f); close() }, SOL.copy(alpha = 0.05f))
            penumbraFuturo(Offset(w * 0.6f, h * 0.3f), SOL, velo = 0.2f, orilla = 0.62f)
        }
        if (noche) for (k in 0 until 12) {
            val o = Offset(w * azar(91, k), h * (0.15f + 0.8f * azar(93, k)))
            drawCircle(LUCIERNAGA.copy(alpha = 0.12f), 6 * u, o)
            drawCircle(LUCIERNAGA.copy(alpha = 0.25f), 3 * u, o)
            drawCircle(LUCIERNAGA.copy(alpha = 0.9f), 1.1f * u, o)
        }
    }

    /** La tira de cartón rescatado por arriba: el borde de abajo rasgado, con la onda del cartón a la vista, y dos tiras de cinta. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = fondoBarraFuturo(p, CARTON, 0.32f, oscuro)
        val rasgo = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - 6 * u)
            var x = w; var k = 0
            while (x > 0f) { x -= (6 + 8 * azar(51, k)) * u; lineTo(maxOf(x, 0f), h - (2 + 6 * azar(53, k)) * u); k++ }
            close()
        }
        drawPath(rasgo, Brush.verticalGradient(listOf(fondo, mezcla(fondo, Color.Black, 0.08f)), 0f, h))
        // La onda del cartón, a la vista en el rasgado.
        var x = 2 * u
        while (x < w) { drawLine(mezcla(fondo, Color.Black, 0.25f).copy(alpha = 0.5f), Offset(x, h - 9 * u), Offset(x, h - 6 * u), 0.8f * u); x += 3 * u }
        drawPath(rasgo, mezcla(fondo, Color.Black, 0.3f).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        listOf(Offset(2 * u, 4 * u) to 8f, Offset(w - 30 * u, 2 * u) to -6f).forEach { (o, g) ->
            rotate(g, o) { drawRect(Color(0xFFB8BCB8).copy(alpha = if (p.oscura || oscuro) 0.25f else 0.45f), o, Size(28 * u, 9 * u)) }
        }
    }

    /** Un trozo de tabla rescatada: sus vetas, los cantos astillados, un clavo en cada punta y un poco de musgo en la esquina. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 3 * u, w - 3 * u, h - 4 * u)
        val tabla = Path().apply {
            moveTo(r.left + 2 * u, r.top); lineTo(r.right - 1 * u, r.top + 1 * u); lineTo(r.right, r.center.y - 3 * u); lineTo(r.right - 3 * u, r.center.y)
            lineTo(r.right, r.center.y + 4 * u); lineTo(r.right - 2 * u, r.bottom); lineTo(r.left, r.bottom - 1 * u); lineTo(r.left + 3 * u, r.center.y + 2 * u); lineTo(r.left, r.top + 4 * u); close()
        }
        translate(1.5f * u, 2 * u) { drawPath(tabla, Color.Black.copy(alpha = 0.3f)) }
        drawPath(tabla, Brush.verticalGradient(listOf(t(MADERA_LUZ, oscuro, noche), t(MADERA, oscuro, noche), t(MADERA_OSC, oscuro, noche)), r.top, r.bottom))
        clipPath(tabla) { vetas(r, t(VETA, oscuro, noche).copy(alpha = 0.45f), 29, densidad = 0.8f) }
        listOf(Offset(r.left + 8 * u, r.center.y), Offset(r.right - 8 * u, r.center.y)).forEach { o ->
            drawCircle(t(VARILLA, oscuro, noche).copy(alpha = 0.4f), 3.2f * u, Offset(o.x + 0.6f * u, o.y + 1.2f * u))
            drawCircle(t(HORMIGON_OSC, oscuro, noche), 2 * u, o)
            drawCircle(Color.White.copy(alpha = 0.3f), 0.7f * u, Offset(o.x - 0.6f * u, o.y - 0.6f * u))
        }
        musgo(Offset(r.left + 10 * u, r.bottom - 1 * u), 6 * u, 13, oscuro, noche)
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFF5F0DE)

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 266.dp

    /** La lona verde del cuaderno, la cinta plateada y la llama de la vela. */
    private val LONA = Color(0xFF5B6B45)
    private val CINTA_PLATA = Color(0xFFA9ADAA)
    private val LLAMA_VELA = Color(0xFFFFC66A)

    /**
     * El rincón del superviviente, en una sala en ruinas: el muro de hormigón rajado, con la varilla al aire, el musgo y la
     * hiedra que cuelga, y el hueco de la ventana rota por donde se ve el cielo y las ramas de fuera. En el suelo de tierra,
     * con hojarasca y matas de pasto, un cajón de madera puesto boca abajo hace de mesa (sus tablas, los clavos, la flecha de
     * «este lado arriba» estarcida y una tabla rota). Encima, a la izquierda, la vela en su lata oxidada, con la cera
     * chorreada y la llama; a la derecha, el frasco con un brote, la navaja y una piedra. El cuaderno del superviviente va al
     * centro: lona verde con el lomo de cinta plateada, la esquina de abajo perdida, cercos de agua, la etiqueta escrita a mano
     * con el nombre y la cuenta, una hoja de hiedra pegada con cinta, la liga que lo cierra y hojas prensadas que asoman por
     * el canto, más cuantas más hojas. Al abrirse, la liga se escurre y cae, y la tapa gira sobre el lomo: dentro, las marcas
     * de los días contados y la hoja con un helecho prensado y un mapa a lápiz. En oscuro, la sala se hunde y alumbra la vela.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF050605), 0.5f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val hoja = papel(PAPEL, oscuro, false)
        val sueloY = h * 0.84f
        // ---- El muro de hormigón, con el hueco de la ventana rota.
        drawRect(f(HORMIGON), Offset.Zero, Size(w, sueloY))
        for (k in 0 until 10) {
            val o = Offset(w * azar(501, k), sueloY * azar(503, k))
            drawPath(contornoIrregular(Rect(o.x, o.y, o.x + (14 + 30 * azar(507, k)) * u, o.y + (8 + 20 * azar(509, k)) * u), 510 + k, 8, 0.3f), f(HORMIGON_OSC).copy(alpha = 0.35f))
        }
        val ven = Path().apply {
            moveTo(w * 0.6f, h * 0.06f); lineTo(w * 0.93f, h * 0.04f); lineTo(w * 0.95f, h * 0.2f); lineTo(w * 0.9f, h * 0.27f)
            lineTo(w * 0.94f, h * 0.36f); lineTo(w * 0.66f, h * 0.38f); lineTo(w * 0.62f, h * 0.28f); lineTo(w * 0.58f, h * 0.18f); close()
        }
        drawPath(ven, if (oscuro) mezcla(CIELO_NOCHE, Color(0xFF1A2A3A), 0.3f) else c(CIELO))
        clipPath(ven) {
            // Las ramas y las hojas de fuera.
            drawPath(Path().apply { moveTo(w * 0.95f, h * 0.1f); quadraticTo(w * 0.8f, h * 0.18f, w * 0.64f, h * 0.14f) }, f(CORTEZA), style = Stroke(2.4f * u))
            for (k in 0 until 18) drawCircle(f(if (k % 3 == 0) HOJA_LUZ else HOJA), (4 + 4 * azar(521, k)) * u, Offset(w * (0.62f + 0.32f * azar(523, k)), h * (0.06f + 0.18f * azar(527, k))))
            if (oscuro) drawCircle(LUNA.copy(alpha = 0.85f), 6 * u, Offset(w * 0.86f, h * 0.29f))
        }
        drawPath(ven, f(HORMIGON_OSC), style = Stroke(2.5f * u))
        listOf(0.64f, 0.72f, 0.86f).forEachIndexed { k, fx ->
            drawPath(Path().apply { moveTo(w * fx, h * 0.37f); quadraticTo(w * fx + 4 * u, h * 0.32f, w * fx + (k - 1) * 6 * u, h * 0.27f) }, f(VARILLA), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        }
        // Las grietas.
        drawPath(Path().apply { moveTo(w * 0.58f, h * 0.2f); lineTo(w * 0.5f, h * 0.26f); lineTo(w * 0.47f, h * 0.36f); lineTo(w * 0.4f, h * 0.42f); moveTo(w * 0.47f, h * 0.36f); lineTo(w * 0.52f, h * 0.45f) }, f(Color(0xFF3A3B35)), style = Stroke(1.2f * u))
        drawPath(Path().apply { moveTo(w * 0.04f, h * 0.5f); lineTo(w * 0.1f, h * 0.44f); lineTo(w * 0.13f, h * 0.48f); lineTo(w * 0.2f, h * 0.4f) }, f(Color(0xFF3A3B35)), style = Stroke(1 * u))
        // La hiedra que cuelga desde arriba y el musgo.
        listOf(0.08f to 70f, 0.16f to 46f, 0.34f to 58f, 0.97f to 50f).forEachIndexed { k, (fx, largo) -> hiedra(Offset(w * fx, 0f), largo * u, 530 + k, oscuro, false) }
        drawRect(f(MUSGO), Offset(0f, 0f), Size(w, 3 * u))
        musgo(Offset(w * 0.62f, h * 0.38f), 12 * u, 541, oscuro, false)
        // ---- El suelo de tierra con la hojarasca.
        drawRect(f(TIERRA), Offset(0f, sueloY), Size(w, h - sueloY))
        for (k in 0 until 26) {
            val o = Offset(w * azar(551, k), sueloY + (h - sueloY) * azar(553, k))
            rotate(360f * azar(557, k), o) { drawOval(f(HOJARASCA[k % HOJARASCA.size]), Offset(o.x - 3.5f * u, o.y - 1.5f * u), Size(7 * u, 3 * u)) }
        }

        // ---- La luz de la vela (el primer degradado).
        val lata = Offset(w * 0.17f, h * 0.53f)
        val llama = Offset(lata.x, lata.y - 54 * u)
        drawCircle(Brush.radialGradient(listOf(LLAMA_VELA.copy(alpha = if (oscuro) 0.42f else 0.18f), LLAMA_VELA.copy(alpha = 0f)), llama, w * 0.55f), w * 0.55f, llama)

        // ---- El cajón boca abajo: la cara de arriba (sus tablas) y la de delante, con sus marcas.
        val arribaC = h * 0.48f
        val frenteC = h * 0.72f
        val cl = w * 0.05f; val cr = w * 0.95f
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(cl - 4 * u, sueloY - 4 * u), Size(cr - cl + 10 * u, 8 * u))
        val tablas = 4
        for (k in 0 until tablas) {
            val y0 = arribaC + (frenteC - arribaC) * k / tablas
            val r = Rect(cl, y0 + 0.8f * u, cr, arribaC + (frenteC - arribaC) * (k + 1) / tablas)
            drawRect(c(if (k % 2 == 0) MADERA_LUZ else MADERA), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.4f), 560 + k, densidad = 0.6f)
            drawLine(c(VETA), Offset(cl, y0), Offset(cr, y0), 1.6f * u)
            listOf(cl + 6 * u, cr - 6 * u).forEach { x -> drawCircle(c(Color(0xFF3A3530)), 1.2f * u, Offset(x, r.center.y)) }
        }
        val frente = Rect(cl, frenteC, cr, sueloY)
        for (k in 0 until 3) {
            val r = Rect(cl, frente.top + frente.height * k / 3, cr, frente.top + frente.height * (k + 1) / 3)
            drawRect(c(if (k == 1) MADERA_OSC else mezcla(MADERA, MADERA_OSC, 0.4f)), r.topLeft, r.size)
            vetas(r, c(VETA).copy(alpha = 0.5f), 570 + k, densidad = 0.6f)
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(cl, r.top), Offset(cr, r.top), 1.2f * u)
        }
        drawRect(c(MADERA_LUZ).copy(alpha = 0.5f), Offset(cl, frenteC), Size(cr - cl, 1.2f * u))
        listOf(cl, cr - 9 * u).forEach { x -> drawRect(c(MADERA_OSC), Offset(x, frenteC), Size(9 * u, sueloY - frenteC)); drawLine(Color.Black.copy(alpha = 0.4f), Offset(x + (if (x == cl) 9f else 0f) * u, frenteC), Offset(x + (if (x == cl) 9f else 0f) * u, sueloY), 1 * u) }
        // La flecha de «este lado arriba», estarcida al revés (el cajón está volteado), y la copa de «frágil».
        val marca = c(Color(0xFF2A2620)).copy(alpha = 0.55f)
        val fl = Offset(w * 0.2f, frente.center.y)
        listOf(-6f, 6f).forEach { s ->
            drawLine(marca, Offset(fl.x + s * u, fl.y - 9 * u), Offset(fl.x + s * u, fl.y + 6 * u), 2.4f * u)
            drawPath(Path().apply { moveTo(fl.x + s * u - 5 * u, fl.y + 5 * u); lineTo(fl.x + s * u + 5 * u, fl.y + 5 * u); lineTo(fl.x + s * u, fl.y + 11 * u); close() }, marca)
        }
        val co = Offset(w * 0.8f, frente.center.y)
        drawPath(Path().apply { moveTo(co.x - 6 * u, co.y + 9 * u); lineTo(co.x - 6 * u, co.y + 3 * u); quadraticTo(co.x - 6 * u, co.y - 3 * u, co.x, co.y - 3 * u); quadraticTo(co.x + 6 * u, co.y - 3 * u, co.x + 6 * u, co.y + 3 * u); lineTo(co.x + 6 * u, co.y + 9 * u); close() }, marca)
        drawLine(marca, Offset(co.x, co.y - 3 * u), Offset(co.x, co.y - 9 * u), 1.6f * u)
        drawLine(marca, Offset(co.x - 5 * u, co.y - 10 * u), Offset(co.x + 5 * u, co.y - 10 * u), 1.6f * u)
        // La tabla rota, con su astilla.
        drawPath(Path().apply { moveTo(w * 0.52f, frente.top + frente.height * 0.66f); lineTo(w * 0.6f, frente.top + frente.height * 0.66f); lineTo(w * 0.58f, frente.top + frente.height * 0.82f); lineTo(w * 0.555f, frente.top + frente.height * 0.72f); lineTo(w * 0.53f, sueloY); lineTo(w * 0.52f, sueloY); close() }, f(Color(0xFF1A140E)))
        // Las matas de pasto y el musgo al pie del cajón.
        listOf(0.02f, 0.11f, 0.47f, 0.9f, 0.98f).forEachIndexed { k, fx ->
            val b = Offset(w * fx, sueloY + 3 * u)
            for (q in 0 until 6) {
                val an = -PI.toFloat() / 2 + (q - 2.5f) * 0.22f
                val l = (8 + 8 * azar(580 + k, q)) * u
                drawLine(f(if (q % 2 == 0) HOJA_LUZ else HOJA), b, Offset(b.x + l * cos(an), b.y + l * sin(an)), 1.2f * u, cap = StrokeCap.Round)
            }
        }
        musgo(Offset(w * 0.3f, sueloY), 14 * u, 591, oscuro, false)
        musgo(Offset(w * 0.75f, sueloY), 10 * u, 593, oscuro, false)

        // ---- La vela en su lata oxidada.
        val lr = Rect(lata.x - 14 * u, lata.y - 27 * u, lata.x + 14 * u, lata.y)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(lr.left - 2 * u, lr.bottom - 3 * u), Size(lr.width + 8 * u, 6 * u))
        drawRect(c(Color(0xFF8A8C86)), lr.topLeft, lr.size)
        drawRect(c(Color(0xFFA0522D)), Offset(lr.left, lr.top + 5 * u), Size(lr.width, 10 * u))
        drawRect(c(PAPEL).copy(alpha = 0.6f), Offset(lr.left + 6 * u, lr.top + 7 * u), Size(10 * u, 6 * u))
        for (q in 0 until 4) drawCircle(c(VARILLA).copy(alpha = 0.5f), (1 + azar(601, q) * 2) * u, Offset(lr.left + lr.width * azar(603, q), lr.top + lr.height * azar(607, q)))
        drawOval(c(Color(0xFF6E706A)), Offset(lr.left, lr.top - 3 * u), Size(lr.width, 6 * u))
        drawRect(c(Color(0xFFEDE6D2)), Offset(lata.x - 6 * u, lr.top - 14 * u), Size(12 * u, 13 * u))
        drawPath(Path().apply { moveTo(lata.x - 5 * u, lr.top - 14 * u); lineTo(lata.x - 6.5f * u, lr.top - 4 * u); lineTo(lata.x - 4.5f * u, lr.top - 7 * u); close() }, c(Color(0xFFF5F0E0)))
        drawPath(Path().apply { moveTo(lata.x + 5 * u, lr.top - 13 * u); quadraticTo(lata.x + 9 * u, lr.top - 2 * u, lata.x + 7 * u, lr.top + 4 * u); lineTo(lata.x + 5 * u, lr.top - 2 * u); close() }, c(Color(0xFFF5F0E0)))
        drawLine(Color(0xFF2A2620), Offset(lata.x, lr.top - 14 * u), Offset(lata.x, lr.top - 16 * u), 0.8f * u)
        drawOval(LLAMA_VELA.copy(alpha = 0.35f), Offset(llama.x - 6 * u, llama.y - 5 * u), Size(12 * u, 20 * u))
        drawOval(LLAMA_VELA, Offset(llama.x - 3.5f * u, llama.y - 2 * u), Size(7 * u, 14 * u))
        drawOval(Color.White.copy(alpha = 0.85f), Offset(llama.x - 1.2f * u, llama.y + 4 * u), Size(2.4f * u, 5 * u))
        // El frasco con su brote, la navaja y la piedra, a la derecha.
        val fr = Rect(w * 0.84f, h * 0.38f, w * 0.84f + 22 * u, h * 0.53f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(fr.left - 2 * u, fr.bottom - 3 * u), Size(fr.width + 6 * u, 6 * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), fr.topLeft, fr.size, CornerRadius(4 * u))
        drawRect(c(TIERRA), Offset(fr.left + 1 * u, fr.bottom - fr.height * 0.35f), Size(fr.width - 2 * u, fr.height * 0.35f - 1 * u))
        drawPath(Path().apply { moveTo(fr.center.x, fr.bottom - fr.height * 0.35f); quadraticTo(fr.center.x - 3 * u, fr.top + 10 * u, fr.center.x + 1 * u, fr.top - 6 * u) }, c(HOJA_LUZ), style = Stroke(1.4f * u))
        listOf(-1f, 1f).forEach { s -> drawOval(c(HOJA_LUZ), Offset(fr.center.x + (if (s < 0) -9f else 1f) * u, fr.top - 6 * u + (if (s < 0) 2f else -2f) * u), Size(8 * u, 4 * u)) }
        drawRoundRect(Color.White.copy(alpha = 0.6f), fr.topLeft, fr.size, CornerRadius(4 * u), style = Stroke(0.9f * u))
        drawRect(c(VARILLA), Offset(fr.left + 1 * u, fr.top - 3 * u), Size(fr.width - 2 * u, 4 * u))
        rotate(-18f, Offset(w * 0.86f, h * 0.66f)) {
            drawRoundRect(c(Color(0xFF6A2A22)), Offset(w * 0.86f - 14 * u, h * 0.66f - 2.5f * u), Size(20 * u, 5 * u), CornerRadius(2.5f * u))
            drawPath(Path().apply { moveTo(w * 0.86f + 6 * u, h * 0.66f - 2 * u); lineTo(w * 0.86f + 22 * u, h * 0.66f - 1 * u); lineTo(w * 0.86f + 6 * u, h * 0.66f + 2.5f * u); close() }, c(Color(0xFFB8BEC2)))
        }
        drawPath(contornoIrregular(Rect(w * 0.08f, h * 0.62f, w * 0.08f + 20 * u, h * 0.62f + 11 * u), 611, 8, 0.25f), c(HORMIGON_OSC))

        // ---- El cuaderno del superviviente.
        val g = grosorLibreta(e.hojas, u, minimo = 3f)
        val ancho = w * 0.46f
        val lomo = 9 * u
        val suelta = (a / 0.16f).coerceIn(0f, 1f)
        val angulo = giroTapa(((a - 0.12f) / 0.88f).coerceIn(0f, 1f))
        val corre = ancho * 0.2f * (1f - cos(angulo)) / 2f
        val xl = w * 0.3f + corre
        val x0 = xl + lomo; val x1 = xl + ancho
        val fy = frenteC - 9 * u
        val hondo = h * 0.2f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val lona = c(LONA)
        val esquina = 16 * u
        sombraSuave(Rect(xl, fy - hondo, x1, fy), 2 * u, 8 * u, if (oscuro) 0.45f else 0.3f)
        drawRect(mezcla(lona, Color.Black, 0.3f), Offset(xl, fy - hondo), Size(ancho, hondo))
        // Las hojas prensadas que asoman por el canto: más cuantas más hojas.
        val asoman = (1 + e.hojas / 15).coerceAtMost(7)
        for (k in 0 until asoman) {
            val y = arriba + 6 * u + (hondo - 22 * u) * ((k + 0.5f) / asoman)
            val x = x1 - 2 * u
            val col = c(if (k % 3 == 0) HOJA_LUZ else if (k % 3 == 1) HOJARASCA[0] else HOJA)
            drawPath(Path().apply { moveTo(x - 6 * u, y); quadraticTo(x + 8 * u, y - 6 * u, x + (10 + 4 * azar(e.semilla, 30 + k)) * u, y + 2 * u); quadraticTo(x + 6 * u, y + 6 * u, x - 6 * u, y + 3 * u); close() }, col)
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(x - 4 * u, y + 1.5f * u), Offset(x + 9 * u, y + 1.5f * u), 0.5f * u)
        }
        cantoHojas(Rect(x0, tapaY + 1.6f * u, x1 - esquina * 0.6f, fy - 1.6f * u), mezcla(hoja, Color.Black, 0.06f), c(TINTA), e.semilla)
        drawRect(mezcla(lona, Color.Black, 0.4f), Offset(xl, fy - 1.6f * u), Size(ancho - esquina * 0.6f, 1.6f * u))
        // El lomo de cinta plateada, arrugada.
        drawRect(c(CINTA_PLATA), Offset(xl, arriba), Size(lomo + 5 * u, fy - arriba))
        for (k in 1..6) drawLine(Color.Black.copy(alpha = 0.15f), Offset(xl, arriba + (fy - arriba) * k / 7f), Offset(xl + lomo + 5 * u, arriba + (fy - arriba) * k / 7f + 2 * u), 0.7f * u)
        drawLine(Color.White.copy(alpha = 0.4f), Offset(xl + 2 * u, arriba), Offset(xl + 2 * u, fy), 1 * u)
        // La hoja de dentro, que se ve al abrir: el helecho prensado y el mapa.
        if (angulo > 0.01f) {
            val r = Rect(x0 + 5 * u, arriba + 2 * u, x1 - 3 * u, tapaY + 1 * u)
            drawPath(Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - esquina * 0.6f); lineTo(r.right - esquina * 0.6f, r.bottom); lineTo(r.left, r.bottom); close() }, hoja)
            val he = Offset(r.left + r.width * 0.24f, r.bottom - 6 * u)
            val largo = r.height * 0.82f
            drawLine(c(HOJA), he, Offset(he.x + 4 * u, he.y - largo), 1 * u)
            for (q in 1..8) {
                val p = Offset(he.x + 4 * u * q / 9f, he.y - largo * q / 9f)
                val l = (11 - q) * 1.4f * u
                listOf(-1f, 1f).forEach { s -> drawOval(c(if (q % 2 == 0) HOJA else HOJA_LUZ), Offset(p.x + (if (s < 0) -l else 0f), p.y - 1.5f * u), Size(l, 3 * u)) }
            }
            drawRect(c(CINTA_PLATA).copy(alpha = 0.6f), Offset(he.x - 7 * u, he.y - 6 * u), Size(16 * u, 5 * u))
            // El mapa a lápiz: el camino, el río y la X.
            val m = Rect(r.left + r.width * 0.48f, r.top + 6 * u, r.right - 8 * u, r.bottom - 10 * u)
            val lapiz = c(Color(0xFF4A4A48)).copy(alpha = 0.7f)
            drawPath(Path().apply { moveTo(m.left, m.bottom); cubicTo(m.left + m.width * 0.3f, m.top + m.height * 0.6f, m.left + m.width * 0.5f, m.bottom - 4 * u, m.right - 8 * u, m.top + 8 * u) }, lapiz, style = Stroke(0.9f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
            drawPath(Path().apply { moveTo(m.left + m.width * 0.6f, m.top); quadraticTo(m.left + m.width * 0.4f, m.center.y, m.right, m.bottom) }, c(Color(0xFF3A6A9A)).copy(alpha = 0.6f), style = Stroke(1.4f * u))
            val xx = Offset(m.right - 8 * u, m.top + 8 * u)
            drawLine(c(Color(0xFFB0352B)), Offset(xx.x - 3 * u, xx.y - 3 * u), Offset(xx.x + 3 * u, xx.y + 3 * u), 1.4f * u)
            drawLine(c(Color(0xFFB0352B)), Offset(xx.x + 3 * u, xx.y - 3 * u), Offset(xx.x - 3 * u, xx.y + 3 * u), 1.4f * u)
            for (q in 0 until 3) drawPath(estrella(Offset(m.left + m.width * (0.15f + 0.2f * q), m.top + m.height * 0.25f), 3 * u, 3, 0.5f), lapiz)
            if (cos(angulo) > 0f) sombraTapa(x0 + (x1 - x0) * cos(angulo), arriba, (x1 - x0) * 0.24f * sin(angulo), tapaY - arriba)
        }
        // La tapa de lona, que gira sobre el lomo.
        tapaLateral(x0, angulo, 0.42f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            val forma = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - esquina); lineTo(r.right - esquina, r.bottom); lineTo(r.left, r.bottom); close() }
            drawRect(mezcla(lona, Color.Black, 0.35f), Offset(r.left, r.bottom), Size(r.width - esquina, 1.6f * u))
            drawPath(forma, lona)
            clipPath(forma) {
                var yy = r.top; while (yy < r.bottom) { drawLine(Color.Black.copy(alpha = 0.06f), Offset(r.left, yy), Offset(r.right, yy), 0.5f * u); yy += 1.8f * u }
                // Los cercos de agua y la mancha de óxido.
                drawCircle(Color.Black.copy(alpha = 0.1f), r.height * 0.4f, Offset(r.left + r.width * 0.3f, r.bottom))
                drawCircle(c(VARILLA).copy(alpha = 0.22f), r.height * 0.2f, Offset(r.right - r.width * 0.1f, r.top + r.height * 0.2f))
                drawCircle(Color.Black.copy(alpha = 0.12f), r.height * 0.25f, Offset(r.left + r.width * 0.75f, r.top + r.height * 0.7f), style = Stroke(1.2f * u))
            }
            drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(0.8f * u))
            // La etiqueta escrita a mano: el nombre y la cuenta.
            val et = Rect(r.left + 8 * u, r.top + r.height * 0.14f, r.left + r.width * 0.82f, r.top + r.height * 0.6f)
            rotate(-2f, et.center) {
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(et.left + 1 * u, et.top + 1.5f * u), et.size)
                drawRect(hoja, et.topLeft, et.size)
                drawRect(c(CINTA_PLATA).copy(alpha = 0.7f), Offset(et.left - 4 * u, et.top - 3 * u), Size(14 * u, 6 * u))
                val corte = et.top + et.height * 0.6f
                textoEn(medidor, e.nombre, Rect(et.left + 3 * u, et.top + 3 * u, et.right - 3 * u, corte), letra, TINTA, 13f, peso = FontWeight.Medium, minimo = 5.5f)
                drawLine(TINTA.copy(alpha = 0.4f), Offset(et.left + 5 * u, corte), Offset(et.right - 5 * u, corte + 1 * u), 0.8f * u)
                textoEn(medidor, e.cuenta, Rect(et.left + 4 * u, corte + 1 * u, et.right - 4 * u, et.bottom - 2 * u), letra, TINTA.copy(alpha = 0.8f), 9f, minimo = 5f)
            }
            // La hoja de hiedra, pegada con un trozo de cinta.
            val hc = Offset(r.left + r.width * 0.6f, r.top + r.height * 0.78f)
            val s = minOf(r.height * 0.24f, 12 * u)
            drawPath(Path().apply { moveTo(hc.x, hc.y + s * 0.5f); lineTo(hc.x - s * 0.55f, hc.y + s * 0.35f); lineTo(hc.x - s * 0.8f, hc.y - s * 0.05f); lineTo(hc.x - s * 0.32f, hc.y - s * 0.05f); lineTo(hc.x, hc.y - s * 0.5f); lineTo(hc.x + s * 0.32f, hc.y - s * 0.05f); lineTo(hc.x + s * 0.8f, hc.y - s * 0.05f); lineTo(hc.x + s * 0.55f, hc.y + s * 0.35f); close() }, c(MUSGO_LUZ))
            drawLine(c(HOJA), Offset(hc.x, hc.y + s * 0.5f), Offset(hc.x, hc.y - s * 0.35f), 0.9f * u)
            drawRect(c(CINTA_PLATA).copy(alpha = 0.6f), Offset(hc.x - 3 * u, hc.y + s * 0.3f), Size(12 * u, 4 * u))
            if (sombra > 0f) drawPath(forma, Color.Black.copy(alpha = 0.45f * sombra))
        }, dentro = { sombra ->
            // Por dentro: la lona más clara con las marcas de los días contados.
            val r = Rect(x0 - (x1 - x0), arriba, x0, tapaY + 1.6f * u)
            val forma = Path().apply { moveTo(r.left + esquina, r.bottom); lineTo(r.left, r.bottom - esquina); lineTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); close() }
            drawPath(forma, mezcla(lona, Color(0xFFE6E3D6), 0.35f))
            val n = (e.hojas).coerceIn(1, 40)
            var k = 0
            val trazo = c(Color(0xFF2A2620)).copy(alpha = 0.7f)
            while (k < n) {
                val grupo = k / 5; val dentroG = k % 5
                val gx = r.left + 10 * u + (grupo % 4) * 22 * u
                val gy = r.top + 10 * u + (grupo / 4) * 16 * u
                if (gy > r.bottom - 12 * u) break
                if (dentroG < 4) drawLine(trazo, Offset(gx + dentroG * 4 * u, gy), Offset(gx + dentroG * 4 * u + 1 * u, gy + 11 * u), 1 * u)
                else drawLine(trazo, Offset(gx - 2 * u, gy + 9 * u), Offset(gx + 15 * u, gy + 2 * u), 1 * u)
                k++
            }
            if (sombra > 0f) drawPath(forma, Color.Black.copy(alpha = 0.4f * sombra))
        })
        // La liga que lo cierra: se escurre por el canto y queda tirada en el cajón.
        val lx = x0 + (x1 - x0) * 0.8f + suelta * (x1 - x0) * 0.3f
        if (suelta < 1f) {
            drawLine(c(Color(0xFF2A2A2A)), Offset(lx, arriba - 2 * u), Offset(lx, tapaY + 1.6f * u), 2.2f * u)
            drawLine(c(Color(0xFF2A2A2A)), Offset(lx, tapaY + 1.6f * u), Offset(lx, fy), 2.2f * u)
        } else {
            drawOval(c(Color(0xFF2A2A2A)), Offset(x1 + 6 * u, fy - 10 * u), Size(26 * u, 7 * u), style = Stroke(2 * u))
        }
    }

    /**
     * La pestaña de un día: un trozo de cinta plateada doblado, con las puntas rasgadas, el día a rotulador, la letra de la
     * semana y, abajo, una vuelta de cinta aislante del color del ánimo (sin ella, si no hay).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val plata = papel(CINTA_PLATA, oscuro, false)
        rotate(-4f + 8f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val r = Rect(5 * u, 3 * u, w - 5 * u, h - 3 * u)
            val p = Path().apply {
                moveTo(r.left, r.top)
                var x = r.left; var k = 0
                while (x < r.right) { x += 3 * u; lineTo(minOf(x, r.right), r.top + (1.6f * azar(e.semilla, 10 + k)) * u); k++ }
                lineTo(r.right, r.bottom)
                x = r.right; k = 0
                while (x > r.left) { x -= 3 * u; lineTo(maxOf(x, r.left), r.bottom - (1.6f * azar(e.semilla, 30 + k)) * u); k++ }
                close()
            }
            translate(1.2f * u, 1.6f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
            drawPath(p, plata)
            for (k in 0 until 8) drawLine(Color.Black.copy(alpha = 0.07f), Offset(r.left, r.top + r.height * azar(e.semilla, 50 + k)), Offset(r.right, r.top + r.height * azar(e.semilla, 60 + k)), 0.6f * u)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(r.left + 2 * u, r.top + 3 * u), Offset(r.left + 2 * u, r.bottom - 3 * u), 0.8f * u)
            textoEn(medidor, e.semana, Rect(r.left, r.top + 2 * u, r.right, r.top + 11 * u), letra, TINTA.copy(alpha = 0.75f), 8f, peso = FontWeight.Bold, minimo = 5f)
            textoEn(medidor, e.dia, Rect(r.left, r.top + 10 * u, r.right, r.top + 34 * u), letra, TINTA, 20f, peso = FontWeight.Bold, minimo = 8f)
            if (e.tono != null) {
                drawRect(e.tono, Offset(r.left - 1 * u, r.bottom - 13 * u), Size(r.width + 2 * u, 8 * u))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(r.left, r.bottom - 12 * u), Offset(r.right, r.bottom - 12 * u), 0.8f * u)
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.bottom - 5.5f * u), Offset(r.right, r.bottom - 5.5f * u), 0.8f * u)
            }
        }
    }

    /** El rótulo de un mes: una tabla rota por una punta, con el mes pintado a brocha, musgo en el canto y una ramita de hiedra. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val tam = 15f
        val largo = (anchoTexto(medidor, texto, letra, tam, FontWeight.Bold) + 56 * u).coerceIn(w * 0.5f, w - 6 * u)
        val r = Rect(w / 2 - largo / 2, 5 * u, w / 2 + largo / 2, h - 4 * u)
        val tabla = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right - 10 * u, r.top); lineTo(r.right - 4 * u, r.top + r.height * 0.3f); lineTo(r.right, r.top + r.height * 0.35f)
            lineTo(r.right - 6 * u, r.top + r.height * 0.6f); lineTo(r.right - 2 * u, r.bottom); lineTo(r.left, r.bottom); close()
        }
        translate(1.5f * u, 2 * u) { drawPath(tabla, Color.Black.copy(alpha = 0.3f)) }
        drawPath(tabla, papel(MADERA, oscuro, false))
        clipPath(tabla) { vetas(r, papel(VETA, oscuro, false).copy(alpha = 0.4f), 91, densidad = 0.7f) }
        drawPath(tabla, c(MADERA_OSC), style = Stroke(1.2f * u))
        drawCircle(c(Color(0xFF3A3530)), 1.6f * u, Offset(r.left + 7 * u, r.center.y))
        musgo(Offset(r.left + r.width * 0.3f, r.top + 1 * u), 10 * u, 93, oscuro, false)
        musgo(Offset(r.left + r.width * 0.75f, r.top + 1 * u), 7 * u, 97, oscuro, false)
        hiedra(Offset(r.left + 16 * u, r.top), 22 * u, 99, oscuro, false)
        textoEn(medidor, texto, Rect(r.left + 24 * u, r.top + 2 * u, r.right - 18 * u, r.bottom), letra, Color(0xFFF4F1E6).copy(alpha = 0.95f), tam, peso = FontWeight.Bold, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El vidrio verdoso del tarro, su filo más oscuro, la goma naranja del cierre y el rojo oxidado de la chapa. */
    private val VIDRIO = Color(0xFFB9D4C6)
    private val VIDRIO_FILO = Color(0xFF5E7E6E)
    private val GOMA = Color(0xFFC8642E)
    private val CHAPA = Color(0xFFA34A1E)
    private val CERA = Color(0xFF8A2A1E)
    private val NEGRO_RUINAS = Color(0xFF151815)

    /** El contorno de un tarro de conserva dentro de [r]: la boca de ancho [boca] arriba, el cuello, los hombros y el fondo redondeado. */
    private fun tarro(r: Rect, boca: Float, cuello: Float): Path = Path().apply {
        val cx = r.center.x
        val hombro = r.top + cuello
        val rr = r.width * 0.1f
        moveTo(cx - boca / 2, r.top); lineTo(cx - boca / 2, hombro)
        quadraticTo(r.left, hombro, r.left, hombro + r.height * 0.14f)
        lineTo(r.left, r.bottom - rr); quadraticTo(r.left, r.bottom, r.left + rr, r.bottom)
        lineTo(r.right - rr, r.bottom); quadraticTo(r.right, r.bottom, r.right, r.bottom - rr)
        lineTo(r.right, hombro + r.height * 0.14f); quadraticTo(r.right, hombro, cx + boca / 2, hombro)
        lineTo(cx + boca / 2, r.top); close()
    }

    /** Una chapa de botella oxidada, de 21 dientes, con la hoja de hiedra estampada. */
    private fun DrawScope.chapaSello(c: Offset, r: Float, color: Color, hondo: Color, claro: Color) {
        val chapa = Path().apply {
            for (i in 0 until 42) {
                val ang = 2 * PI * i / 42; val rr = if (i % 2 == 0) r else r * 0.86f
                val x = c.x + (rr * cos(ang)).toFloat(); val y = c.y + (rr * sin(ang)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(chapa, color)
        drawCircle(hondo, r * 0.72f, c, style = Stroke(r * 0.06f))
        drawCircle(hondo.copy(alpha = 0.35f), r * 0.16f, Offset(c.x + r * 0.4f, c.y - r * 0.36f))
        drawPath(estrella(c, r * 0.46f, 5, 0.55f), claro)
        drawLine(hondo, Offset(c.x, c.y + r * 0.42f), Offset(c.x, c.y - r * 0.2f), r * 0.05f)
    }

    /**
     * El tarro de conserva rescatado de las ruinas, donde se guarda el mensaje: vidrio verdoso y rayado, con musgo y piedritas
     * en el fondo, cierre de alambre oxidado y tapa de vidrio con su goma naranja. Por el vidrio se ve la carta enrollada
     * dentro; en el frente, la etiqueta de papel pegada con cinta, con su hoja de hiedra, a quién va y las fechas a
     * carboncillo. El sello es una chapa de botella oxidada aplastada en un goterón de cera sobre la junta de la tapa: cuando
     * ya se puede destapiar, se encienden dentro las luciérnagas; al abrirlo, la cera se raja y la tapa se vuelca hacia atrás
     * sobre su alambre y queda de pie detrás de la boca. Los de al futuro van además atados con bramante: tres vueltas al cuello y dos cabos por encima de la
     * tapa. La carta sale enrollándose por la boca.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        fun pp(x: Color) = papel(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 4 * u, cu.top + 2 * u, cu.right - 4 * u, cu.bottom - 5 * u)
        val w = s.width; val h = s.height
        val k = (w / (162f * u)).coerceIn(0.8f, 2.2f)
        val cx = s.center.x
        val boca = w * 0.74f
        val altoTapa = h * 0.15f
        val bis = s.top
        val labio = s.top + altoTapa
        val cuerpoT = Rect(s.left, labio, s.right, s.bottom)
        val forma = tarro(cuerpoT, boca, h * 0.1f)
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val sc = Offset(cx, labio + 1 * u)
        val sr = h * 0.085f

        // ---- La sombra en la tierra.
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(s.left + w * 0.02f, s.bottom - 5 * u), Size(w * 0.98f, 9 * u))
        // ---- El fondo del tarro: el vidrio de atrás, el musgo, las piedritas.
        drawPath(forma, c(VIDRIO).copy(alpha = 0.55f))
        clipPath(forma) {
            drawRect(c(Color(0xFF2E3A2E)).copy(alpha = 0.25f), cuerpoT.topLeft, cuerpoT.size)
            for (i in 0 until 5) drawOval(c(HORMIGON), Offset(s.left + w * (0.1f + 0.17f * i), s.bottom - h * (0.1f + 0.04f * azar(e.semilla, i))), Size(w * 0.1f, h * 0.08f))
            musgo(Offset(cx, s.bottom - h * 0.06f), w * 0.3f, e.semilla, oscuro, false)
            musgo(Offset(cx - w * 0.25f, s.bottom - h * 0.04f), w * 0.15f, e.semilla + 3, oscuro, false)
            // Las luciérnagas que se encienden cuando ya se puede abrir.
            if (e.brillo > 0f) {
                drawRect(Brush.radialGradient(listOf(LUCIERNAGA.copy(alpha = 0.55f * e.brillo), Color.Transparent), center = Offset(cx, s.top + h * 0.55f), radius = w * 0.55f), cuerpoT.topLeft, cuerpoT.size)
                for (i in 0 until 6) {
                    val o = Offset(s.left + w * (0.15f + 0.7f * azar(e.semilla, i + 7)), s.top + h * (0.3f + 0.6f * azar(e.semilla, i + 17)))
                    drawCircle(LUCIERNAGA.copy(alpha = 0.5f * e.brillo), 3 * u, o)
                    drawCircle(Color(0xFFF8FFD0).copy(alpha = e.brillo), 1.2f * u, o)
                }
            }
        }
        // ---- La tapa de vidrio abierta, volcada hacia atrás, por detrás de la carta.
        fun DrawScope.tapa(adentro: Boolean) {
            val r = Rect(cx - boca / 2 - 3 * u, bis, cx + boca / 2 + 3 * u, labio)
            if (adentro) {
                drawRoundRect(c(VIDRIO).copy(alpha = 0.7f), r.topLeft, r.size, CornerRadius(r.height * 0.4f))
                drawOval(c(GOMA), r.deflate(3 * u).topLeft, r.deflate(3 * u).size, style = Stroke(2 * u))
                drawRoundRect(c(VIDRIO_FILO), r.topLeft, r.size, CornerRadius(r.height * 0.4f), style = Stroke(0.8f * u))
                return
            }
            // La goma naranja que asoma bajo la tapa y la tapa con su cúpula.
            drawRoundRect(c(GOMA), Offset(r.left + 1 * u, r.bottom - 3 * u), Size(r.width - 2 * u, 3.5f * u), CornerRadius(1.5f * u))
            drawPath(Path().apply {
                moveTo(r.left, r.bottom - 2.5f * u); lineTo(r.left, r.top + r.height * 0.5f); quadraticTo(r.left, r.top, cx, r.top)
                quadraticTo(r.right, r.top, r.right, r.top + r.height * 0.5f); lineTo(r.right, r.bottom - 2.5f * u); close()
            }, c(VIDRIO).copy(alpha = 0.85f))
            drawLine(Color.White.copy(alpha = 0.55f), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.35f), Offset(r.left + r.width * 0.42f, r.top + r.height * 0.18f), 1.4f * u, StrokeCap.Round)
            drawRoundRect(c(VIDRIO_FILO), r.topLeft, r.size, CornerRadius(r.height * 0.5f), style = Stroke(0.8f * u))
            // El alambre del cierre, oxidado, cruzando la tapa.
            drawLine(c(VARILLA), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.15f), Offset(r.right - r.width * 0.2f, r.top + r.height * 0.15f), 1.6f * u)
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2.5f * u * e.romper); rotate(-8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr * 1.3f, sc.y - sr * 1.3f, sc.x + sr * 1.3f, sc.y + sr * 1.3f), sc.y, true, 6, e.semilla)) { sello(sc, sr, 1f, oscuro) }
            }
        }
        // La tapa se vuelca hacia atrás sobre su alambre: crece hacia arriba y, pasada la mitad, enseña su cara de abajo.
        fun DrawScope.volcar() = withTransform({ scale(1f, 1f + 1.4f * ab, Offset(cx, labio)) }) { tapa(ab >= 0.5f) }
        if (!cerrada) volcar()

        // ---- La carta: enrollada dentro, o saliendo por la boca y desenrollándose.
        val hojaColor = if (oscuro) mezcla(e.hoja, Color.Black, 0.12f) else e.hoja
        if (e.subir <= 0f) {
            val rollo = Rect(s.left + w * 0.16f, labio + h * 0.14f, s.right - w * 0.16f, labio + h * 0.27f)
            drawRoundRect(hojaColor, rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
            drawOval(c(PAPEL), Offset(rollo.right - rollo.height * 0.6f, rollo.top), Size(rollo.height * 0.6f, rollo.height))
            drawOval(c(TINTA).copy(alpha = 0.25f), Offset(rollo.right - rollo.height * 0.45f, rollo.top + rollo.height * 0.2f), Size(rollo.height * 0.3f, rollo.height * 0.6f), style = Stroke(0.6f * u))
            drawLine(c(CORDEL), Offset(rollo.center.x, rollo.top - 0.5f * u), Offset(rollo.center.x, rollo.bottom + 0.5f * u), 1.4f * u)
        } else {
            val arriba0 = labio + h * 0.08f
            val alza = alzaCarta(e.subir, h, arriba0, 8 * u)
            val hoja = Rect(cx - boca * 0.46f, arriba0 - alza, cx + boca * 0.46f, arriba0 - alza + h * 0.8f)
            drawRect(Color.Black.copy(alpha = 0.18f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size)
            drawRect(hojaColor, hoja.topLeft, hoja.size)
            var y = hoja.top + 13 * u
            while (y < hoja.bottom - 3 * u) { drawLine(e.renglones, Offset(hoja.left + 4 * u, y), Offset(hoja.right - 4 * u, y), 0.6f * u); y += 11 * u }
            // El borde de arriba aún enrollado.
            drawRoundRect(mezcla(hojaColor, PAPEL, 0.4f), Offset(hoja.left - 1 * u, hoja.top - 4 * u), Size(hoja.width + 2 * u, 7 * u), CornerRadius(3.5f * u))
            drawRoundRect(c(TINTA).copy(alpha = 0.2f), Offset(hoja.left - 1 * u, hoja.top - 4 * u), Size(hoja.width + 2 * u, 7 * u), CornerRadius(3.5f * u), style = Stroke(0.6f * u))
        }

        // ---- El vidrio de delante: el tinte, los reflejos, las rayas y el cuello con su rosca.
        drawPath(forma, c(VIDRIO).copy(alpha = 0.22f))
        clipPath(forma) {
            drawRect(Color.White.copy(alpha = 0.35f), Offset(s.left + w * 0.07f, labio + h * 0.16f), Size(w * 0.035f, h * 0.6f))
            drawRect(Color.White.copy(alpha = 0.18f), Offset(s.left + w * 0.13f, labio + h * 0.2f), Size(w * 0.015f, h * 0.5f))
            for (i in 0 until 5) {
                val o = Offset(s.left + w * azar(e.semilla, i + 31), labio + h * azar(e.semilla, i + 41))
                drawLine(Color.White.copy(alpha = 0.25f), o, o + Offset((4 + 6 * azar(e.semilla, i + 51)) * u, (2 * azar(e.semilla, i + 61) - 1) * u), 0.5f * u)
            }
        }
        drawPath(forma, c(VIDRIO_FILO), style = Stroke(1.2f * u))
        listOf(0.3f, 0.62f).forEach { f -> drawLine(c(VIDRIO_FILO).copy(alpha = 0.7f), Offset(cx - boca / 2, labio + h * 0.1f * f), Offset(cx + boca / 2, labio + h * 0.1f * f + 1 * u), 0.8f * u) }
        // ---- La etiqueta de papel, pegada con cinta, con su hoja de hiedra.
        val et = Rect(s.left + w * 0.1f, labio + h * 0.31f, s.right - w * 0.1f, s.bottom - h * 0.07f)
        rotate(-1.2f, et.center) {
            val roto = contornoIrregular(et, e.semilla, 18, 0.03f)
            translate(0.8f * u, 1.2f * u) { drawPath(roto, Color.Black.copy(alpha = 0.22f)) }
            drawPath(roto, pp(PAPEL))
            drawCircle(c(Color(0xFFB09A6A)).copy(alpha = 0.25f), et.height * 0.35f, Offset(et.right - et.width * 0.15f, et.bottom - et.height * 0.2f), style = Stroke(1.2f * u))
            listOf(et.left + et.width * 0.06f, et.right - et.width * 0.06f).forEachIndexed { i, x ->
                rotate(if (i == 0) -30f else 30f, Offset(x, et.top)) { drawRect(c(Color(0xFFE8DFC4)).copy(alpha = 0.7f), Offset(x - 6 * u, et.top - 2.5f * u), Size(12 * u, 5 * u)) }
            }
            textoEn(medidor, e.linea1, Rect(et.left + 4 * u, et.top + et.height * 0.08f, et.right - 4 * u, et.top + et.height * 0.56f), LetraExtra.letrero, TINTA, 12f, minimo = 7f)
            drawLine(TINTA.copy(alpha = 0.4f), Offset(et.left + et.width * 0.1f, et.top + et.height * 0.57f), Offset(et.right - et.width * 0.1f, et.top + et.height * 0.55f), 0.7f * u)
            textoEn(medidor, e.linea2, Rect(et.left + 4 * u, et.top + et.height * 0.58f, et.right - 4 * u, et.bottom - et.height * 0.06f), letra, TINTA.copy(alpha = 0.85f), 9f, minimo = 6f)
        }
        val hojaH = Offset(et.left + 2 * u, et.bottom - 2 * u)
        drawPath(estrella(hojaH, 6 * u * k, 5, 0.6f, giro = -110f), c(HOJA))
        drawLine(c(CORTEZA), hojaH, hojaH + Offset(-4 * u, 6 * u), 0.9f * u)

        // ---- La tapa cerrada.
        if (cerrada) volcar()
        // ---- El bramante: tres vueltas al cuello y dos cabos por encima de la tapa.
        if (e.cordon > 0f && cerrada) {
            val cuerda = c(CORDEL)
            val vueltas = 3
            for (i in 0 until vueltas) {
                val fr = (e.cordon * (vueltas + 1) - i).coerceIn(0f, 1f)
                if (fr <= 0f) continue
                val y = labio + h * (0.025f + 0.028f * i)
                val pts = listOf(Offset(cx - boca / 2 - 1 * u, y + 1 * u), Offset(cx, y + 2 * u), Offset(cx + boca / 2 + 1 * u, y + 1 * u))
                drawPath(recorrido(pts, fr), c(Color(0xFF6E5530)), style = Stroke(1.8f * u, cap = StrokeCap.Round))
                drawPath(recorrido(pts, fr), cuerda, style = Stroke(1.1f * u, cap = StrokeCap.Round))
            }
            val fr = (e.cordon * (vueltas + 1) - vueltas).coerceIn(0f, 1f)
            if (fr > 0f) listOf(cx - boca * 0.28f, cx + boca * 0.28f).forEach { x ->
                val pts = listOf(Offset(x, labio + h * 0.03f), Offset(x + (cx - x) * 0.1f, bis - 1.5f * u), Offset(cx, bis - 1 * u), Offset(cx, sc.y))
                drawPath(recorrido(pts, fr), cuerda, style = Stroke(1.1f * u, cap = StrokeCap.Round))
            }
        }
        // ---- El sello de cera con la chapa: entero (cayendo al sellar) o rajado.
        if (e.romper <= 0f) {
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc) }) { sello(sc, sr, e.alfaSello.coerceIn(0f, 1f), oscuro) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2.5f * u * e.romper); rotate(8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr * 1.3f, sc.y - sr * 1.3f, sc.x + sr * 1.3f, sc.y + sr * 1.3f), sc.y, false, 6, e.semilla)) { sello(sc, sr, 1f, oscuro) }
            }
        }
    }

    /** El sello del tarro: el goterón de cera, con sus chorreones, y la chapa aplastada encima. */
    private fun DrawScope.sello(c0: Offset, r: Float, alfa: Float, oscuro: Boolean) {
        fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
        val u = density
        val cera = t(CERA, oscuro, false)
        drawPath(contornoIrregular(Rect(c0.x - r * 1.3f, c0.y - r * 1.15f, c0.x + r * 1.3f, c0.y + r * 1.2f), 11, 9, 0.18f), a(cera))
        drawRoundRect(a(cera), Offset(c0.x + r * 0.5f, c0.y + r * 0.6f), Size(r * 0.3f, r * 1.1f), CornerRadius(r * 0.15f))
        drawRoundRect(a(cera), Offset(c0.x - r * 0.9f, c0.y + r * 0.7f), Size(r * 0.24f, r * 0.7f), CornerRadius(r * 0.12f))
        drawCircle(a(Color.Black.copy(alpha = 0.3f)), r, c0 + Offset(0.5f * u, 1 * u))
        chapaSello(c0, r, a(t(CHAPA, oscuro, false)), a(t(Color(0xFF5A2410), oscuro, false)), a(t(HOJA_LUZ, oscuro, false)))
    }

    // La carta que sale enrollándose por la boca del tarro.
    override val cajaPrevia = Rect(0.22f, 0.15f, 0.78f, 0.31f)

    override val altoBuzon: Dp = 216.dp

    /**
     * Lo que queda del correo en la ciudad verde: el buzón de calle, oxidado y con la pintura saltada, la hiedra trepándole y
     * el musgo en el techo; la puerta cuelga abierta de una bisagra y dentro se ven, apiladas, las cartas ya leídas. A su lado,
     * sobre una losa rota de hormigón, los tarros: los que ya se pueden destapiar, con sus luciérnagas encendidas, y los que
     * siguen atados con bramante. Cada grupo lleva su trozo de cartón atado con cordel, con la cuenta a carboncillo. Detrás,
     * el muro rajado con la varilla al aire; abajo, tierra y pasto.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF050605), 0.45f) else c(x)
        fun pp(x: Color) = papel(x, oscuro, false)
        // ---- El muro rajado y la tierra.
        val suelo = h - 22 * u
        drawRect(f(HORMIGON))
        for (i in 0 until 5) drawOval(Color.Black.copy(alpha = 0.07f), Offset(w * azar(3, i) - 30 * u, h * azar(5, i) - 20 * u), Size(70 * u, 60 * u))
        drawPath(Path().apply { moveTo(w * 0.55f, 0f); lineTo(w * 0.58f, 30 * u); lineTo(w * 0.54f, 58 * u); lineTo(w * 0.6f, 90 * u) }, f(HORMIGON_OSC), style = Stroke(1.4f * u))
        listOf(0.7f, 0.76f, 0.82f).forEach { fx -> drawLine(f(VARILLA), Offset(w * fx, 0f), Offset(w * fx + 4 * u, 18 * u), 2 * u, StrokeCap.Round) }
        drawRect(f(HORMIGON_OSC), Offset(w * 0.66f, 0f), Size(w * 0.22f, 10 * u))
        listOf(0.05f, 0.62f, 0.9f).forEachIndexed { i, fx -> hiedra(Offset(w * fx, 0f), (40 + 30 * azar(7, i)) * u, 70 + i, oscuro, false) }
        drawRect(f(TIERRA), Offset(0f, suelo), Size(w, h - suelo))
        val listas = e.listas > 0
        // ---- El buzón de calle.
        val bz = Rect(w * 0.06f, 36 * u, w * 0.4f, suelo + 4 * u)
        val pie = Rect(bz.left + bz.width * 0.25f, bz.bottom - 16 * u, bz.right - bz.width * 0.25f, bz.bottom)
        drawRect(c(Color(0xFF3A2A20)), pie.topLeft, pie.size)
        val caja = Rect(bz.left, bz.top, bz.right, pie.top)
        val pintura = c(Color(0xFF3E6B4E))
        drawRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(4 * u, 4 * u), caja.size)
        drawPath(arcoApuntado(Rect(caja.left, caja.top - 14 * u, caja.right, caja.bottom), 14 * u), pintura)
        // La pintura saltada, el óxido y las chorreras.
        for (i in 0 until 9) {
            val o = Offset(caja.left + caja.width * azar(13, i), caja.top + caja.height * azar(17, i))
            drawPath(contornoIrregular(Rect(o.x, o.y, o.x + (5 + 9 * azar(19, i)) * u, o.y + (4 + 7 * azar(23, i)) * u), 30 + i, 7, 0.35f), c(VARILLA).copy(alpha = 0.85f))
        }
        for (i in 0 until 4) drawRect(c(VARILLA).copy(alpha = 0.4f), Offset(caja.left + caja.width * (0.15f + 0.22f * i), caja.top + 22 * u), Size(2 * u, caja.height * (0.3f + 0.3f * azar(29, i))))
        // La ranura con su tapita, arriba.
        val ran = Rect(caja.left + caja.width * 0.2f, caja.top + 6 * u, caja.right - caja.width * 0.2f, caja.top + 11 * u)
        drawRect(c(NEGRO_RUINAS), ran.topLeft, ran.size)
        drawRect(c(mezcla(Color(0xFF3E6B4E), Color.White, 0.15f)), Offset(ran.left - 2 * u, ran.top - 4 * u), Size(ran.width + 4 * u, 4 * u))
        // El hueco de la puerta abierta y las cartas leídas apiladas dentro.
        val hueco = Rect(caja.left + 8 * u, caja.top + 18 * u, caja.right - 8 * u, caja.bottom - 8 * u)
        drawRect(c(NEGRO_RUINAS), hueco.topLeft, hueco.size)
        val na = e.abiertas.coerceAtMost(9)
        // La hojarasca que se coló por la ranura.
        for (j in 0 until 6) drawPath(estrella(Offset(hueco.left + hueco.width * (0.1f + 0.16f * j), hueco.bottom - 2 * u), 4 * u, 5, 0.5f, giro = 40f * j),
            c(HOJARASCA[j % HOJARASCA.size]))
        for (j in 0 until na) {
            val y = hueco.bottom - (j + 1) * 6 * u
            val dx = (azar(37, j) - 0.5f) * 6 * u
            val r = Rect(hueco.left + 5 * u + dx, y, hueco.right - 5 * u + dx, y + 6 * u)
            drawRect(pp(if (j % 2 == 0) PAPEL else CARTON), r.topLeft, r.size)
            drawLine(c(TINTA).copy(alpha = 0.3f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.6f * u)
            if (j % 3 == 1) drawCircle(c(CHAPA), 1.8f * u, Offset(r.center.x + dx, r.center.y))
        }
        // La puerta, colgando de su bisagra de la izquierda, girada hacia fuera.
        drawPath(Path().apply { moveTo(hueco.left, hueco.top); lineTo(hueco.left - 18 * u, hueco.top + 8 * u); lineTo(hueco.left - 22 * u, hueco.bottom + 4 * u); lineTo(hueco.left, hueco.bottom); close() }, c(mezcla(Color(0xFF3E6B4E), Color.Black, 0.25f)))
        drawPath(Path().apply { moveTo(hueco.left - 6 * u, hueco.top + 20 * u); lineTo(hueco.left - 14 * u, hueco.top + 26 * u) }, c(VARILLA), style = Stroke(2 * u))
        musgo(Offset(caja.center.x, caja.top - 10 * u), caja.width * 0.3f, 5, oscuro, false)
        hiedra(Offset(caja.right - 4 * u, caja.top), caja.height * 0.7f, 81, oscuro, false)
        carton(Offset(caja.right - 10 * u, caja.top + 40 * u), "LEÍDAS", e.abiertas, 8f, medidor, oscuro)

        // ---- La losa rota y los tarros.
        val losa = Rect(w * 0.46f, suelo - 26 * u, w * 0.98f, suelo + 2 * u)
        drawPath(Path().apply { moveTo(losa.left, losa.top + 4 * u); lineTo(losa.right - 20 * u, losa.top); lineTo(losa.right, losa.top + 10 * u); lineTo(losa.right - 6 * u, losa.bottom); lineTo(losa.left + 4 * u, losa.bottom); close() }, c(HORMIGON_OSC))
        drawLine(c(HORMIGON), Offset(losa.left, losa.top + 4 * u), Offset(losa.right - 20 * u, losa.top), 1.4f * u)
        drawLine(c(VARILLA), Offset(losa.right - 6 * u, losa.top + 12 * u), Offset(losa.right + 6 * u, losa.top + 4 * u), 1.6f * u)
        val tipos = List(e.listas) { 0 } + List(e.selladas) { 1 }
        val caben = 6
        val vis = tipos.take(caben)
        val tw = (losa.width - 20 * u) / caben
        if (listas) {
            val centro = Offset(losa.left + 10 * u + tw * minOf(e.listas, caben) / 2f, losa.top - 30 * u)
            drawCircle(Brush.radialGradient(listOf(LUCIERNAGA.copy(alpha = if (oscuro) 0.4f else 0.3f), Color.Transparent), center = centro, radius = 70 * u), 70 * u, centro)
        }
        vis.forEachIndexed { j, tipo ->
            val r = Rect(losa.left + 10 * u + j * tw + 2 * u, losa.top - 52 * u + 6 * u * azar(41, j), losa.left + 10 * u + (j + 1) * tw - 2 * u, losa.top + 4 * u)
            val bocaT = r.width * 0.72f
            val forma = tarro(Rect(r.left, r.top + 6 * u, r.right, r.bottom), bocaT, r.height * 0.12f)
            drawPath(forma, c(VIDRIO).copy(alpha = 0.45f))
            clipPath(forma) {
                drawRoundRect(pp(PAPEL), Offset(r.left + r.width * 0.25f, r.top + r.height * 0.3f), Size(r.width * 0.5f, r.height * 0.5f), CornerRadius(2 * u))
                musgo(Offset(r.center.x, r.bottom - 2 * u), r.width * 0.3f, j, oscuro, false)
                if (tipo == 0) for (i in 0 until 3) {
                    val o = Offset(r.left + r.width * (0.2f + 0.6f * azar(43, j * 3 + i)), r.top + r.height * (0.3f + 0.5f * azar(47, j * 3 + i)))
                    drawCircle(LUCIERNAGA.copy(alpha = 0.5f), 2.6f * u, o); drawCircle(Color(0xFFF8FFD0), 1 * u, o)
                }
            }
            drawPath(forma, c(VIDRIO_FILO), style = Stroke(1 * u))
            drawRect(Color.White.copy(alpha = 0.35f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.3f), Size(1.5f * u, r.height * 0.45f))
            // La tapa y, en los atados, el bramante y la cera.
            drawRoundRect(c(VIDRIO), Offset(r.center.x - bocaT / 2 - 1 * u, r.top), Size(bocaT + 2 * u, 7 * u), CornerRadius(3 * u))
            drawRect(c(GOMA), Offset(r.center.x - bocaT / 2, r.top + 6 * u), Size(bocaT, 1.6f * u))
            if (tipo == 1) {
                drawLine(c(CORDEL), Offset(r.center.x - bocaT / 2, r.top + 9 * u), Offset(r.center.x + bocaT / 2, r.top + 10 * u), 1.2f * u)
                drawLine(c(CORDEL), Offset(r.center.x - bocaT * 0.3f, r.top + 9 * u), Offset(r.center.x, r.top), 1 * u)
                drawLine(c(CORDEL), Offset(r.center.x + bocaT * 0.3f, r.top + 9 * u), Offset(r.center.x, r.top), 1 * u)
                drawCircle(c(CERA), 3 * u, Offset(r.center.x, r.top + 7 * u)); drawCircle(c(CHAPA), 2 * u, Offset(r.center.x, r.top + 7 * u))
            } else drawCircle(c(CHAPA), 2.4f * u, Offset(r.center.x, r.top + 7 * u))
        }
        if (vis.isEmpty()) {
            // Ningún tarro: solo un frasco roto y una mata de pasto.
            drawPath(Path().apply { moveTo(losa.left + 30 * u, losa.top + 2 * u); lineTo(losa.left + 36 * u, losa.top - 10 * u); lineTo(losa.left + 42 * u, losa.top - 4 * u); lineTo(losa.left + 50 * u, losa.top - 12 * u); lineTo(losa.left + 54 * u, losa.top + 2 * u); close() }, c(VIDRIO).copy(alpha = 0.5f))
        }
        val xl = losa.left + 8 * u; val xs = losa.left + losa.width * 0.55f
        carton(Offset(xl + 22 * u, losa.top - 4 * u), "DESTAPIAR", e.listas, -6f, medidor, oscuro)
        carton(Offset(xs + 34 * u, losa.top - 4 * u), "TAPIADAS", e.selladas, 5f, medidor, oscuro)
        // ---- El pasto, delante de todo.
        for (i in 0 until 40) {
            val x = w * azar(53, i)
            val l = (5 + 9 * azar(59, i)) * u
            drawLine(c(if (i % 3 == 0) MUSGO_LUZ else MUSGO), Offset(x, h), Offset(x + (azar(61, i) - 0.5f) * 6 * u, h - l), 1.1f * u, StrokeCap.Round)
        }
    }

    /** Un trozo de cartón roto, colgado de su cordel desde [o], con el nombre y la cuenta a carboncillo. */
    private fun DrawScope.carton(o: Offset, nombre: String, n: Int, giro: Float, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val r = Rect(o.x - 26 * u, o.y + 4 * u, o.x + 26 * u, o.y + 34 * u)
        drawLine(t(CORDEL, oscuro, false), o + Offset(0f, -8 * u), Offset(r.center.x, r.top + 3 * u), 1 * u)
        rotate(giro, r.center) {
            val p = contornoIrregular(r, n * 7 + nombre.length, 12, 0.06f)
            translate(1 * u, 1.5f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
            drawPath(p, papel(CARTON, oscuro, false))
            drawCircle(t(TIERRA, oscuro, false), 1.4f * u, Offset(r.center.x, r.top + 3 * u))
            textoEn(medidor, nombre, Rect(r.left + 2 * u, r.top + 5 * u, r.right - 2 * u, r.top + 14 * u), letra, TINTA, 7.5f, minimo = 5f)
            textoEn(medidor, n.toString(), Rect(r.left + 2 * u, r.top + 13 * u, r.right - 2 * u, r.bottom - 1 * u), LetraExtra.letrero, TINTA, 15f)
        }
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

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 118.dp

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]: los paneles y visores de la nave. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    /**
     * La Tierra en miniatura, con su órbita y un satélite: de día, el mar, dos tierras, una nube y el filo de la atmósfera; de
     * noche, la cara oscura con las luces de las ciudades y una creciente de luz en el borde.
     */
    private fun DrawScope.planetaMision(c: Offset, r: Float, orbita: Color, satelite: Color, noche: Boolean) {
        val u = density
        // La mitad de atrás de la órbita.
        rotate(-18f, c) { drawArc(orbita.copy(alpha = 0.35f), 180f, 180f, false, Offset(c.x - r * 1.6f, c.y - r * 0.45f), Size(r * 3.2f, r * 0.9f), style = Stroke(1 * u)) }
        val disco = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        if (!noche) {
            drawPath(disco, MAR)
            clipPath(disco) {
                drawOval(TIERRA_FIRME, Offset(c.x - r * 0.7f, c.y - r * 0.5f), Size(r * 0.8f, r * 0.6f))
                drawOval(DESIERTO, Offset(c.x + r * 0.05f, c.y + r * 0.1f), Size(r * 0.7f, r * 0.5f))
                drawOval(NUBE.copy(alpha = 0.8f), Offset(c.x - r * 0.3f, c.y + r * 0.35f), Size(r * 0.9f, r * 0.22f))
                drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.55f, c.y + r * 0.35f))
            }
        } else {
            drawPath(disco, ATMOSFERA.copy(alpha = 0.6f))
            clipPath(disco) {
                drawCircle(Color(0xFF0E1630), r, Offset(c.x + r * 0.24f, c.y + r * 0.1f))
                for (k in 0 until 14) drawCircle(AMBAR.copy(alpha = 0.85f), (0.5f + 0.6f * azar(5, k)) * u, Offset(c.x + r * (azar(7, k) * 1.6f - 0.8f), c.y + r * (azar(9, k) * 1.6f - 0.8f)))
            }
        }
        drawCircle(ATMOSFERA.copy(alpha = 0.6f), r + 1 * u, c, style = Stroke(1.2f * u))
        // La mitad de delante de la órbita y el satélite.
        rotate(-18f, c) {
            drawArc(orbita, 0f, 180f, false, Offset(c.x - r * 1.6f, c.y - r * 0.45f), Size(r * 3.2f, r * 0.9f), style = Stroke(1.2f * u))
            drawCircle(satelite, 2.2f * u, Offset(c.x + r * 1.1f, c.y + r * 0.32f))
        }
    }

    /**
     * El visor del registro de misión: el panel de vidrio oscuro con las esquinas cortadas y sus corchetes cian, la Tierra
     * con su órbita a la izquierda (de noche, la cara oscura con las ciudades encendidas), el piloto ámbar y el canal, las
     * barras de señal, la fecha juliana grande, el detalle y la regla de marcas del pie.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = p.acento
        val caja = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        val forma = ochavado(caja, 10 * u)
        drawPath(forma, Brush.verticalGradient(listOf(mezcla(VISOR, p.color, 0.3f).copy(alpha = 0.9f), mezcla(p.color, Color.Black, 0.3f).copy(alpha = 0.9f)), caja.top, caja.bottom))
        drawPath(forma, luz.copy(alpha = 0.45f), style = Stroke(1 * u))
        // Los corchetes de las esquinas.
        val k = 14 * u; val m = 4 * u
        listOf(Offset(caja.left + m, caja.top + m) to Offset(1f, 1f), Offset(caja.right - m, caja.top + m) to Offset(-1f, 1f),
            Offset(caja.left + m, caja.bottom - m) to Offset(1f, -1f), Offset(caja.right - m, caja.bottom - m) to Offset(-1f, -1f)).forEach { (o, d) ->
            drawLine(luz, o, o + Offset(d.x * k, 0f), 1.6f * u); drawLine(luz, o, o + Offset(0f, d.y * k), 1.6f * u)
        }
        // La Tierra.
        val celda = Rect(caja.left, caja.top, caja.left + 60 * u, caja.bottom)
        planetaMision(Offset(celda.center.x, celda.center.y + 4 * u), 15 * u, luz, p.detalle, e.noche)
        drawLine(luz.copy(alpha = 0.25f), Offset(celda.right, caja.top + 12 * u), Offset(celda.right, caja.bottom - 12 * u), 0.8f * u)
        // El piloto, el canal y las barras de señal.
        val fila = Rect(celda.right + 8 * u, caja.top + 6 * u, caja.right - 8 * u, caja.top + 22 * u)
        val piloto = Offset(fila.left + 4 * u, fila.center.y)
        drawCircle(p.detalle.copy(alpha = 0.25f), 4.5f * u, piloto)
        drawCircle(p.detalle, 2.4f * u, piloto)
        for (j in 0 until 4) {
            val bh = (3 + j * 2.5f) * u
            drawRect(luz.copy(alpha = if (j < 3) 0.9f else 0.25f), Offset(fila.right - (4 - j) * 5 * u, fila.bottom - 2 * u - bh), Size(3 * u, bh))
        }
        textoEn(medidor, e.rotulo, Rect(fila.left + 12 * u, fila.top, fila.right - 24 * u, fila.bottom), LetraExtra.orbita, luz, 10f, espaciado = 0.3f, minimo = 6f)
        drawLine(luz.copy(alpha = 0.3f), Offset(fila.left, fila.bottom + 1 * u), Offset(fila.right, fila.bottom + 1 * u), 0.7f * u)
        // La fecha y el detalle.
        val cuerpo = Rect(fila.left, fila.bottom + 3 * u, fila.right, caja.bottom - 10 * u)
        fechaFuturo(medidor, e.fecha, Rect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.top + 48 * u), LetraExtra.grotesk, p.tinta, 21f, peso = FontWeight.Medium, espaciado = 0.4f)
        fechaFuturo(medidor, e.detalle, Rect(cuerpo.left, cuerpo.top + 49 * u, cuerpo.right, cuerpo.bottom), LetraExtra.grotesk, p.tenue, 10.5f, k = 0.86f)
        // La regla de marcas del pie.
        var x = celda.right + 8 * u; var j = 0
        while (x < caja.right - 16 * u) {
            drawLine(luz.copy(alpha = if (j % 5 == 0) 0.55f else 0.25f), Offset(x, caja.bottom - 4 * u), Offset(x, caja.bottom - (if (j % 5 == 0) 9 else 6) * u), 0.8f * u)
            x += 5 * u; j++
        }
    }

    /**
     * La consola de la bitácora: el campo de estrellas con algún destello en cruz, los corchetes del visor en las esquinas,
     * la regla de altitud del margen con su marca, la retícula en el margen derecho, la órbita punteada que cruza la hoja y el
     * limbo de un planeta que asoma abajo con su atmósfera (de noche, con las ciudades encendidas). El papel ya es oscuro:
     * solo los adornos.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = p.acento
        // Las estrellas.
        for (k in 0 until 170) {
            val o = Offset(w * azar(201, k), h * azar(203, k))
            drawCircle(ESTRELLA.copy(alpha = 0.12f + 0.38f * azar(205, k)), (0.5f + 0.9f * azar(207, k)) * u, o)
            if (k % 29 == 0) {
                drawLine(ESTRELLA.copy(alpha = 0.35f), Offset(o.x - 4 * u, o.y), Offset(o.x + 4 * u, o.y), 0.6f * u)
                drawLine(ESTRELLA.copy(alpha = 0.35f), Offset(o.x, o.y - 4 * u), Offset(o.x, o.y + 4 * u), 0.6f * u)
            }
        }
        // El limbo del planeta, abajo a la derecha.
        val c = Offset(w * 1.05f, h + w * 0.25f); val r = w * 0.6f
        clipRect(0f, 0f, w, h) {
            drawCircle(Brush.radialGradient(0f to Color.Transparent, 0.78f to Color.Transparent, 0.84f to ATMOSFERA.copy(alpha = 0.16f), 1f to Color.Transparent, center = c, radius = r * 1.2f), r * 1.2f, c)
            drawCircle(mezcla(p.color, MAR, 0.22f), r, c)
            drawCircle(ATMOSFERA.copy(alpha = 0.35f), r, c, style = Stroke(1.5f * u))
            if (noche) for (k in 0 until 26) {
                val a = PI * (1.0 + 0.5 * azar(211, k)); val d = r * (0.82f + 0.16f * azar(213, k))
                drawCircle(AMBAR.copy(alpha = 0.4f), 0.8f * u, Offset(c.x + d * cos(a).toFloat(), c.y + d * sin(a).toFloat()))
            }
        }
        // La órbita punteada.
        val ov = Rect(-w * 0.2f, h * 0.55f, w * 1.1f, h * 0.95f)
        for (k in 0 until 40) drawArc(luz.copy(alpha = 0.12f), 190f + k * 4f, 2f, false, ov.topLeft, ov.size, style = Stroke(1 * u))
        // Los corchetes de las esquinas.
        val k = 18 * u; val m0 = 10 * u; val arriba = 64 * u
        listOf(Offset(m0, arriba) to Offset(1f, 1f), Offset(w - m0, arriba) to Offset(-1f, 1f), Offset(m0, h - m0) to Offset(1f, -1f), Offset(w - m0, h - m0) to Offset(-1f, -1f)).forEach { (o, d) ->
            drawLine(luz.copy(alpha = 0.5f), o, o + Offset(d.x * k, 0f), 1.5f * u); drawLine(luz.copy(alpha = 0.5f), o, o + Offset(0f, d.y * k), 1.5f * u)
        }
        // La regla de altitud del margen, con su marca.
        var y = arriba + 26 * u; var j = 0
        while (y < h - 40 * u) { drawLine(luz.copy(alpha = if (j % 5 == 0) 0.35f else 0.15f), Offset(8 * u, y), Offset((if (j % 5 == 0) 16 else 12) * u, y), 0.8f * u); y += 8 * u; j++ }
        val ym = h * 0.38f
        drawPath(Path().apply { moveTo(18 * u, ym); lineTo(24 * u, ym - 4 * u); lineTo(24 * u, ym + 4 * u); close() }, p.detalle.copy(alpha = 0.7f))
        // La retícula de arriba a la derecha.
        val rc = Offset(w - 16 * u, h * 0.56f)
        drawCircle(luz.copy(alpha = 0.18f), 9 * u, rc, style = Stroke(0.8f * u))
        listOf(Offset(1f, 0f), Offset(-1f, 0f), Offset(0f, 1f), Offset(0f, -1f)).forEach { d -> drawLine(luz.copy(alpha = 0.22f), rc + d * (5 * u), rc + d * (14 * u), 0.8f * u) }
    }

    /** El panel del casco por arriba: metal oscuro con sus pernos, las puntas ochavadas y la línea de luz cian del borde. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(mezcla(t(CASCO_OSC, oscuro, noche), p.color, 0.35f), mezcla(t(CELDA, oscuro, noche), p.color, 0.5f)), 0f, h))
        drawLine(Color.White.copy(alpha = 0.08f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 1 * u)
        listOf(0f, w).forEach { x ->
            val d = if (x == 0f) 1f else -1f
            drawPath(Path().apply { moveTo(x, h - 4 * u); lineTo(x + d * 10 * u, h - 4 * u); lineTo(x + d * 16 * u, h - 10 * u); lineTo(x, h - 10 * u); close() }, p.acento.copy(alpha = 0.35f))
        }
        var x = 24 * u
        while (x < w - 20 * u) { drawCircle(Color.Black.copy(alpha = 0.4f), 1.4f * u, Offset(x, 5 * u)); drawCircle(t(PANEL_OSC, oscuro, noche).copy(alpha = 0.5f), 0.8f * u, Offset(x - 0.3f * u, 4.7f * u)); x += 34 * u }
        drawLine(p.acento.copy(alpha = 0.22f), Offset(0f, h - 2.5f * u), Offset(w, h - 2.5f * u), 3.5f * u)
        drawLine(p.acento, Offset(0f, h - 2.5f * u), Offset(w, h - 2.5f * u), 1 * u)
    }

    /** El botón de la consola: un panel ochavado encendido, con su halo, el filo interior y dos muescas de luz a los lados. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 3 * u, w - 3 * u, h - 3 * u)
        drawPath(ochavado(r, 7 * u), p.acento.copy(alpha = 0.25f), style = Stroke(4 * u))
        drawPath(ochavado(r, 7 * u), Brush.verticalGradient(listOf(mezcla(p.acento, Color.White, 0.25f), p.acento), r.top, r.bottom))
        drawPath(ochavado(r.deflate(2.5f * u), 5.5f * u), p.color.copy(alpha = 0.35f), style = Stroke(0.8f * u))
        listOf(r.left + 6 * u, r.right - 8 * u).forEach { x -> drawRect(p.color.copy(alpha = 0.45f), Offset(x, r.center.y - 5 * u), Size(2 * u, 10 * u)) }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = p.color

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 266.dp

    /** La tela azul marino de la bitácora y el naranja de las misiones. */
    private val MARINO = Color(0xFF1B3A8C)
    private val NARANJA = Color(0xFFE8601C)
    private val CINTA = Color(0xFFF2F4F7)

    /**
     * El puesto de la consola, en la nave: la pared de paneles con sus juntas, el pasamanos, los parches de velcro, la
     * ventanilla con las estrellas y la Luna, y la pantalla de la traza de la órbita. Sobre la consola (las filas de
     * interruptores con sus luces), la bitácora de vuelo va sujeta por dos topes magnéticos con su luz de estado; un flexo de
     * luz blanca la alumbra. Alrededor flotan, sin peso, un bolígrafo, un perno con su tuerca, una gota de agua y una bolsa de
     * comida atada con su cordón. La bitácora es una carpeta de tela azul marino con anillas arriba, la franja reflectante, el
     * parche de la misión con su cohete, la cinta rotulada con el nombre y la cuenta, la lengüeta naranja y los separadores de
     * colores que asoman por delante, más cuantas más hojas. Al abrirse, los topes se apartan y la tapa se levanta sobre las
     * anillas: por dentro, la lista de comprobación en su bolsillo con el lápiz; encima del montón, la hoja de la órbita con el
     * planeta, la elipse y el registro de horas. En oscuro, la cabina se apaga y quedan la pantalla y el flexo.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF03050A), 0.55f) else c(x)
        val a = e.abierto.coerceIn(0f, 1f)
        val fondoY = h * 0.5f
        val frenteY = h * 0.8f
        val grot = LetraExtra.grotesk
        // ---- La pared de paneles.
        drawRect(f(PANEL), Offset.Zero, Size(w, fondoY))
        val pw = 76 * u
        var px = -20 * u
        while (px < w) {
            drawRoundRect(f(PANEL_OSC), Offset(px + 2 * u, 4 * u), Size(pw - 4 * u, fondoY * 0.55f), CornerRadius(5 * u), style = Stroke(1 * u))
            drawRoundRect(f(PANEL_OSC), Offset(px + 2 * u, fondoY * 0.55f + 8 * u), Size(pw - 4 * u, fondoY * 0.45f - 12 * u), CornerRadius(5 * u), style = Stroke(1 * u))
            listOf(Offset(px + 7 * u, 9 * u), Offset(px + pw - 7 * u, 9 * u)).forEach { o -> drawCircle(f(PANEL_OSC), 1.2f * u, o) }
            px += pw
        }
        // El pasamanos y los parches de velcro.
        val pasa = fondoY * 0.6f
        drawRoundRect(f(Color(0xFFE2C34A)), Offset(w * 0.02f, pasa - 2.5f * u), Size(w * 0.3f, 5 * u), CornerRadius(2.5f * u))
        listOf(w * 0.04f, w * 0.3f).forEach { x -> drawRect(f(CASCO), Offset(x, pasa - 2 * u), Size(5 * u, 9 * u)) }
        listOf(Offset(w * 0.4f, h * 0.06f), Offset(w * 0.47f, h * 0.36f)).forEach { o ->
            drawRect(f(Color(0xFF3A3E46)), o, Size(18 * u, 10 * u))
            var vx = o.x + 1.5f * u; while (vx < o.x + 18 * u) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(vx, o.y + 1 * u), Offset(vx, o.y + 9 * u), 0.6f * u); vx += 2 * u }
        }
        // La ventanilla con las estrellas y la Luna.
        val vc = Offset(w * 0.15f, h * 0.17f); val vr = 26 * u
        drawCircle(f(CASCO_OSC), vr + 6 * u, vc)
        for (k in 0 until 8) { val an = k * PI.toFloat() / 4; drawCircle(f(CASCO), 1.6f * u, Offset(vc.x + (vr + 3 * u) * cos(an), vc.y + (vr + 3 * u) * sin(an))) }
        drawCircle(VACIO, vr, vc)
        clipPath(Path().apply { addOval(Rect(vc.x - vr, vc.y - vr, vc.x + vr, vc.y + vr)) }) {
            for (k in 0 until 22) drawCircle(ESTRELLA.copy(alpha = 0.5f + 0.5f * azar(701, k)), (0.4f + 0.9f * azar(703, k)) * u, Offset(vc.x - vr + 2 * vr * azar(707, k), vc.y - vr + 2 * vr * azar(709, k)))
            val lu = Offset(vc.x + vr * 0.35f, vc.y - vr * 0.2f)
            drawCircle(Color(0xFFD8D6CC), vr * 0.32f, lu)
            drawCircle(VACIO, vr * 0.3f, Offset(lu.x + vr * 0.14f, lu.y - vr * 0.06f))
            drawPath(Path().apply { moveTo(vc.x - vr, vc.y - vr * 0.2f); lineTo(vc.x - vr * 0.2f, vc.y - vr); lineTo(vc.x - vr * 0.05f, vc.y - vr); lineTo(vc.x - vr, vc.y - vr * 0.05f); close() }, Color.White.copy(alpha = 0.08f))
        }
        drawCircle(f(PANEL_LUZ), vr, vc, style = Stroke(1.4f * u))
        // La pantalla con la traza de la órbita.
        val pa = Rect(w * 0.62f, h * 0.05f, w * 0.95f, h * 0.36f)
        drawRoundRect(f(CASCO_OSC), pa.inflate(4 * u).topLeft, pa.inflate(4 * u).size, CornerRadius(4 * u))
        drawRect(c(VISOR), pa.topLeft, pa.size)
        var gx = pa.left; while (gx < pa.right) { drawLine(CIAN.copy(alpha = 0.12f), Offset(gx, pa.top), Offset(gx, pa.bottom), 0.5f * u); gx += maxOf(pa.width / 8, 1f) }
        var gy = pa.top; while (gy < pa.bottom) { drawLine(CIAN.copy(alpha = 0.12f), Offset(pa.left, gy), Offset(pa.right, gy), 0.5f * u); gy += maxOf(pa.height / 4, 1f) }
        for (k in 0 until 3) drawPath(contornoIrregular(Rect(pa.left + pa.width * (0.08f + 0.3f * k), pa.top + pa.height * (0.25f + 0.15f * (k % 2)), pa.left + pa.width * (0.26f + 0.3f * k), pa.top + pa.height * (0.65f + 0.1f * (k % 2))), 711 + k, 8, 0.25f), VERDE.copy(alpha = 0.18f))
        drawPath(Path().apply { for (s in 0..40) { val xx = pa.left + pa.width * s / 40f; val yy = pa.center.y - pa.height * 0.32f * sin(s / 40f * 2 * PI.toFloat() + 0.6f); if (s == 0) moveTo(xx, yy) else lineTo(xx, yy) } }, CIAN, style = Stroke(1.2f * u))
        val nave = Offset(pa.left + pa.width * 0.62f, pa.center.y - pa.height * 0.32f * sin(0.62f * 2 * PI.toFloat() + 0.6f))
        drawCircle(AMBAR, 2.4f * u, nave); drawCircle(AMBAR.copy(alpha = 0.3f), 5 * u, nave)

        // ---- La consola: la cubierta y el frente con sus filas de interruptores.
        drawRect(f(CASCO_OSC), Offset(0f, frenteY), Size(w, h - frenteY))
        drawRect(c(CASCO), Offset(0f, fondoY), Size(w, frenteY - fondoY))
        var mx = 0f; while (mx < w) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(mx, fondoY), Offset(mx, frenteY), 0.6f * u); mx += 6 * u }
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(0f, fondoY), Offset(w, fondoY), 1.2f * u)
        drawRect(c(PANEL_OSC), Offset(0f, frenteY), Size(w, 5 * u))
        drawRect(c(PANEL_LUZ).copy(alpha = 0.6f), Offset(0f, frenteY), Size(w, 1 * u))
        val fila = frenteY + 14 * u
        var sx = 10 * u; var k = 0
        while (sx < w - 8 * u) {
            if (k % 7 == 3) { sx += 10 * u; k++; continue }
            drawRect(f(Color(0xFF1A2030)), Offset(sx - 3 * u, fila - 4 * u), Size(6 * u, 8 * u))
            drawLine(f(PANEL_LUZ), Offset(sx, fila), Offset(sx + (if (azar(721, k) > 0.5f) 2 else -2) * u, fila - 5 * u), 1.4f * u)
            val luz = if (k % 5 == 0) AMBAR else if (k % 3 == 0) VERDE else CIAN
            drawCircle(if (azar(723, k) > 0.35f) luz else f(CASCO), 1.1f * u, Offset(sx, fila + 9 * u))
            sx += 13 * u; k++
        }
        drawLine(f(PANEL_OSC), Offset(0f, fila + 16 * u), Offset(w, fila + 16 * u), 1 * u)

        // ---- El flexo de luz blanca, a la izquierda, y su luz (el primer degradado); el resplandor de la pantalla (el segundo).
        val fb = Offset(w * 0.12f, fondoY + 30 * u)
        val cab = Offset(w * 0.27f, fondoY - 22 * u)
        drawRect(Brush.radialGradient(listOf(CIAN.copy(alpha = if (oscuro) 0.16f else 0.06f), Color.Transparent), pa.center, w * 0.45f), Offset(pa.center.x - w * 0.45f, 0f), Size(w * 0.9f, h))
        val pozo = Offset(w * 0.52f, frenteY - h * 0.14f)
        drawOval(Brush.radialGradient(listOf(Color(0xFFEAF4FF).copy(alpha = if (oscuro) 0.32f else 0.14f), Color.Transparent), pozo, w * 0.36f),
            Offset(pozo.x - w * 0.4f, pozo.y - h * 0.22f), Size(w * 0.8f, h * 0.44f))
        drawRect(c(PANEL_OSC), Offset(fb.x - 9 * u, fb.y - 3 * u), Size(18 * u, 6 * u))
        drawPath(Path().apply { moveTo(fb.x, fb.y - 3 * u); cubicTo(fb.x - 6 * u, fb.y - 40 * u, cab.x - 30 * u, cab.y - 20 * u, cab.x, cab.y) }, c(PANEL_OSC), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        rotate(28f, cab) {
            drawRoundRect(c(PANEL_LUZ), Offset(cab.x - 4 * u, cab.y - 5 * u), Size(18 * u, 10 * u), CornerRadius(3 * u))
            drawRect(Color(0xFFEAF4FF), Offset(cab.x + 1 * u, cab.y + 3 * u), Size(11 * u, 2.4f * u))
        }

        // ---- La bitácora de vuelo.
        val g = grosorLibreta(e.hojas, u, minimo = 4f)
        val ancho = w * 0.48f
        val x0 = w * 0.3f; val x1 = x0 + ancho
        val fy = frenteY - 10 * u
        val hondo = h * 0.22f
        val arriba = fy - hondo - g
        val tapaY = fy - g
        val tela = c(MARINO)
        val suelta = (a / 0.14f).coerceIn(0f, 1f)
        val angulo = giroTapa(((a - 0.08f) / 0.92f).coerceIn(0f, 1f))
        val papel = mezcla(Color(0xFFF4F6FA), Color(0xFF05070D), if (oscuro) 0.16f else 0f)
        val tinta = Color(0xFF14203A)
        sombraSuave(Rect(x0, fy - hondo, x1, fy), 4 * u, 8 * u, if (oscuro) 0.45f else 0.3f)
        drawRoundRect(mezcla(tela, Color.Black, 0.3f), Offset(x0, fy - hondo), Size(ancho, hondo), CornerRadius(4 * u))
        // Los separadores de colores que asoman por delante: más cuantas más hojas.
        val sep = (1 + e.hojas / 20).coerceAtMost(7)
        val colores = listOf(NARANJA, CIAN, VERDE, AMBAR, Color(0xFFB07CFF), Color(0xFFFF6FA8))
        for (q in 0 until sep) {
            val sx2 = x0 + 8 * u + (ancho - 28 * u) * (q + 0.5f) / sep
            drawRoundRect(c(colores[q % colores.size]), Offset(sx2, tapaY), Size(14 * u, g + 4 * u), CornerRadius(2 * u))
        }
        cantoHojas(Rect(x0 + 3 * u, tapaY + 2 * u, x1 - 3 * u, fy - 2 * u), mezcla(papel, Color.Black, 0.05f), tinta, e.semilla)
        drawRect(mezcla(tela, Color.Black, 0.4f), Offset(x0 + 2 * u, fy - 2 * u), Size(ancho - 4 * u, 2 * u))
        // La hoja de la órbita, que se ve al levantar la tapa.
        if (a > 0.08f) {
            val r = Rect(x0 + 4 * u, arriba + 3 * u, x1 - 4 * u, tapaY + 1 * u)
            drawRect(papel, r.topLeft, r.size)
            listOf(0.3f, 0.7f).forEach { fx -> drawCircle(c(PANEL_OSC), 2.2f * u, Offset(r.left + r.width * fx, r.top + 4 * u)) }
            textoEn(medidor, "ÓRBITA ${e.hojas}", Rect(r.left + 6 * u, r.top + 8 * u, r.left + r.width * 0.4f, r.top + 18 * u), letra, tinta, 9f, minimo = 5f)
            val pc = Offset(r.left + r.width * 0.24f, r.top + r.height * 0.62f)
            val pr = minOf(r.height * 0.16f, 10 * u)
            drawCircle(c(MAR), pr, pc)
            drawCircle(c(TIERRA_FIRME), pr * 0.4f, Offset(pc.x - pr * 0.3f, pc.y + pr * 0.1f))
            drawOval(tinta.copy(alpha = 0.7f), Offset(pc.x - pr * 2.6f, pc.y - pr * 1.2f), Size(pr * 4.6f, pr * 2.4f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 1.5f * u))))
            drawCircle(c(NARANJA), 1.8f * u, Offset(pc.x + pr * 2f, pc.y - pr * 0.6f))
            var ly = r.top + 22 * u; var q = 0
            while (ly < r.bottom - 4 * u) {
                drawLine(tinta.copy(alpha = 0.5f), Offset(r.left + r.width * 0.5f, ly), Offset(r.left + r.width * 0.62f, ly), 1 * u)
                drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + r.width * 0.66f, ly), Offset(r.left + r.width * (0.72f + 0.22f * azar(e.semilla, 150 + q)), ly), 0.8f * u)
                ly += 6 * u; q++
            }
        }
        // La tapa de tela, que se levanta sobre las anillas.
        tapaAlzada(arriba, tapaY - arriba, angulo, 1.5f, fuera = { sombra ->
            val r = Rect(x0, arriba, x1, tapaY)
            drawRoundRect(tela, r.topLeft, Size(r.width, r.height + 2.5f * u), CornerRadius(5 * u))
            drawRect(mezcla(tela, Color.Black, 0.35f), Offset(r.left + 3 * u, r.bottom), Size(r.width - 6 * u, 2.5f * u))
            var yy = r.top; while (yy < r.bottom) { drawLine(Color.White.copy(alpha = 0.04f), Offset(r.left, yy), Offset(r.right, yy), 0.6f * u); yy += 2.2f * u }
            // La franja reflectante.
            drawRect(c(PANEL_LUZ), Offset(r.left, r.top + 7 * u), Size(r.width, 4 * u))
            drawRect(Color.White.copy(alpha = 0.5f), Offset(r.left, r.top + 7 * u), Size(r.width, 1 * u))
            // El parche de la misión, en su velcro.
            val pr = minOf(r.height * 0.3f, 15 * u)
            val pc = Offset(r.left + pr + 10 * u, r.top + r.height * 0.58f)
            drawRect(c(Color(0xFF26324F)), Offset(pc.x - pr - 3 * u, pc.y - pr - 3 * u), Size(pr * 2 + 6 * u, pr * 2 + 6 * u))
            drawCircle(c(NARANJA), pr, pc)
            drawCircle(VACIO, pr * 0.82f, pc)
            for (q in 0 until 7) drawCircle(ESTRELLA.copy(alpha = 0.8f), 0.7f * u, Offset(pc.x + (azar(e.semilla, 170 + q) - 0.5f) * pr * 1.3f, pc.y + (azar(e.semilla, 180 + q) - 0.5f) * pr * 1.3f))
            rotate(-20f, pc) { drawOval(c(ATMOSFERA), Offset(pc.x - pr * 0.7f, pc.y - pr * 0.16f), Size(pr * 1.4f, pr * 0.32f), style = Stroke(1 * u)) }
            drawPath(Path().apply { moveTo(pc.x, pc.y - pr * 0.6f); lineTo(pc.x + pr * 0.14f, pc.y - pr * 0.3f); lineTo(pc.x + pr * 0.14f, pc.y + pr * 0.3f); lineTo(pc.x + pr * 0.26f, pc.y + pr * 0.45f); lineTo(pc.x - pr * 0.26f, pc.y + pr * 0.45f); lineTo(pc.x - pr * 0.14f, pc.y + pr * 0.3f); lineTo(pc.x - pr * 0.14f, pc.y - pr * 0.3f); close() }, Color.White)
            drawCircle(c(NARANJA), pr * 0.1f, Offset(pc.x, pc.y + pr * 0.55f))
            // La cinta rotulada con el nombre y la cuenta.
            val et = Rect(pc.x + pr + 8 * u, r.top + 16 * u, r.right - 11 * u, r.bottom - 6 * u)
            val cinta = mezcla(CINTA, Color(0xFF05070D), if (oscuro) 0.14f else 0f)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(et.left + 1 * u, et.top + 1.5f * u), et.size)
            drawRect(cinta, et.topLeft, et.size)
            drawRect(c(NARANJA), et.topLeft, Size(3 * u, et.height))
            val corte = et.top + et.height * 0.58f
            textoEn(medidor, e.nombre, Rect(et.left + 6 * u, et.top + 2 * u, et.right - 4 * u, corte), letra, tinta, 12.5f, minimo = 5.5f)
            drawLine(tinta.copy(alpha = 0.3f), Offset(et.left + 6 * u, corte), Offset(et.right - 5 * u, corte), 0.6f * u)
            textoEn(medidor, e.cuenta, Rect(et.left + 6 * u, corte, et.right - 4 * u, et.bottom - 2 * u), grot, tinta.copy(alpha = 0.85f), 9.5f, minimo = 5f)
            // La lengüeta naranja.
            drawRect(c(NARANJA), Offset(r.right - 3 * u, r.top + r.height * 0.45f), Size(8 * u, 13 * u))
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.45f * sombra), r.topLeft, Size(r.width, r.height + 2.5f * u))
        }, dentro = { sombra ->
            // Por dentro: el bolsillo transparente con la lista de comprobación y el lápiz sujeto.
            val r = Rect(x0, arriba, x1, tapaY)
            drawRoundRect(mezcla(tela, Color.Black, 0.15f), r.topLeft, r.size, CornerRadius(5 * u))
            val li = Rect(r.left + ancho * 0.08f, r.top + r.height * 0.12f, r.left + ancho * 0.66f, r.bottom - r.height * 0.1f)
            drawRect(papel, li.topLeft, li.size)
            drawRect(c(NARANJA), li.topLeft, Size(li.width, 8 * u))
            textoEn(medidor, "LISTA DE COMPROBACIÓN", Rect(li.left, li.top, li.right, li.top + 8 * u), grot, Color.White, 7f, peso = FontWeight.Medium, minimo = 4f)
            var ly = li.top + 13 * u; var q = 0
            while (ly < li.bottom - 3 * u) {
                drawRect(tinta.copy(alpha = 0.7f), Offset(li.left + 5 * u, ly - 2.5f * u), Size(4 * u, 4 * u), style = Stroke(0.6f * u))
                if (azar(e.semilla, 190 + q) > 0.3f) drawLine(c(Color(0xFF1E8E5A)), Offset(li.left + 5.5f * u, ly - 0.5f * u), Offset(li.left + 9.5f * u, ly - 3.5f * u), 0.9f * u)
                drawLine(tinta.copy(alpha = 0.4f), Offset(li.left + 13 * u, ly), Offset(li.right - 6 * u - 10 * u * azar(e.semilla, 200 + q), ly), 0.8f * u)
                ly += 6.5f * u; q++
            }
            drawRect(Color.White.copy(alpha = 0.14f), Offset(r.left + 3 * u, r.top + r.height * 0.45f), Size(r.width - 6 * u, r.height * 0.52f))
            drawLine(Color.White.copy(alpha = 0.35f), Offset(r.left + 3 * u, r.top + r.height * 0.45f), Offset(r.right - 3 * u, r.top + r.height * 0.45f), 0.8f * u)
            drawRect(c(AMBAR), Offset(r.right - ancho * 0.22f, r.top + r.height * 0.2f), Size(4 * u, r.height * 0.6f))
            drawRect(c(PANEL_OSC), Offset(r.right - ancho * 0.22f - 3 * u, r.top + r.height * 0.42f), Size(10 * u, 4 * u))
            if (sombra > 0f) drawRect(Color.Black.copy(alpha = 0.4f * sombra), r.topLeft, r.size)
        })
        // Las anillas de arriba, que no se mueven.
        listOf(0.28f, 0.72f).forEach { fx ->
            val ax = x0 + ancho * fx
            drawArc(c(PANEL_LUZ), 180f, 180f, false, Offset(ax - 6 * u, arriba - 6 * u), Size(12 * u, 12 * u), style = Stroke(2.6f * u))
            drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(ax - 6 * u, arriba - 6 * u), Size(12 * u, 12 * u), style = Stroke(0.8f * u))
        }
        // Los topes magnéticos, que se apartan al abrir.
        listOf(-1f, 1f).forEach { s ->
            val bx = (if (s < 0) x0 - 10 * u else x1 - 4 * u) + s * suelta * 12 * u
            val br = Rect(bx, fy - hondo * 0.6f, bx + 14 * u, fy - hondo * 0.6f + 22 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(br.left + 1 * u, br.top + 2 * u), br.size, CornerRadius(3 * u))
            drawRoundRect(c(PANEL_OSC), br.topLeft, br.size, CornerRadius(3 * u))
            drawRoundRect(c(PANEL_LUZ), br.topLeft, Size(br.width, 3 * u), CornerRadius(3 * u))
            drawRect(c(Color(0xFFC8102E)), Offset(br.left + 3 * u, br.top + 7 * u), Size(br.width - 6 * u, 3 * u))
            drawRect(c(PANEL_LUZ), Offset(br.left + 3 * u, br.top + 10 * u), Size(br.width - 6 * u, 3 * u))
            val luz = if (suelta > 0.5f) AMBAR else VERDE
            drawCircle(luz.copy(alpha = 0.3f), 3.4f * u, Offset(br.center.x, br.bottom - 5 * u))
            drawCircle(luz, 1.5f * u, Offset(br.center.x, br.bottom - 5 * u))
        }

        // ---- Lo que flota, sin peso, delante de todo (se mece un poco al abrir).
        val mece = a * 6 * u
        // El bolígrafo.
        rotate(-32f + 10f * a, Offset(w * 0.5f, h * 0.2f - mece)) {
            val bo = Offset(w * 0.5f, h * 0.2f - mece)
            drawRoundRect(c(Color(0xFF2B3A5A)), Offset(bo.x - 22 * u, bo.y - 2.4f * u), Size(40 * u, 4.8f * u), CornerRadius(2.4f * u))
            drawRect(c(PANEL_LUZ), Offset(bo.x - 22 * u, bo.y - 2.4f * u), Size(9 * u, 4.8f * u))
            drawLine(c(PANEL_LUZ), Offset(bo.x - 20 * u, bo.y - 3.2f * u), Offset(bo.x - 8 * u, bo.y - 3.2f * u), 1 * u)
            drawPath(Path().apply { moveTo(bo.x + 18 * u, bo.y - 2.4f * u); lineTo(bo.x + 25 * u, bo.y); lineTo(bo.x + 18 * u, bo.y + 2.4f * u); close() }, c(PANEL_OSC))
        }
        // El perno con su tuerca.
        rotate(40f - 20f * a, Offset(w * 0.6f, h * 0.42f + mece)) {
            val pe = Offset(w * 0.6f, h * 0.42f + mece)
            drawRect(c(PANEL_OSC), Offset(pe.x - 1.8f * u, pe.y - 9 * u), Size(3.6f * u, 16 * u))
            for (q in 0 until 5) drawLine(c(CASCO), Offset(pe.x - 1.8f * u, pe.y - 6 * u + q * 2.6f * u), Offset(pe.x + 1.8f * u, pe.y - 5 * u + q * 2.6f * u), 0.6f * u)
            drawPath(estrella(Offset(pe.x, pe.y - 10 * u), 4.6f * u, 6, 0.88f, 0f), c(PANEL_LUZ))
        }
        drawPath(estrella(Offset(w * 0.66f, h * 0.36f - mece), 3.6f * u, 6, 0.88f, 15f), c(PANEL_OSC))
        drawCircle(c(CASCO), 1.4f * u, Offset(w * 0.66f, h * 0.36f - mece))
        // La gota de agua.
        val go = Offset(w * 0.36f, h * 0.3f + mece * 0.5f)
        drawCircle(c(ATMOSFERA).copy(alpha = 0.45f), 6 * u, go)
        drawCircle(Color.White.copy(alpha = 0.6f), 6 * u, go, style = Stroke(0.8f * u))
        drawCircle(Color.White.copy(alpha = 0.85f), 1.6f * u, Offset(go.x - 2 * u, go.y - 2 * u))
        drawCircle(c(ATMOSFERA).copy(alpha = 0.5f), 2 * u, Offset(go.x + 10 * u, go.y - 6 * u))
        // La bolsa de comida, con su cordón atado a la consola.
        val bl = Offset(w * 0.9f, h * 0.44f - mece)
        drawPath(Path().apply { moveTo(bl.x, bl.y + 12 * u); cubicTo(bl.x + 8 * u, bl.y + 30 * u, bl.x - 10 * u, bl.y + 40 * u, w * 0.88f, fondoY + 6 * u) }, c(NARANJA), style = Stroke(1 * u))
        rotate(14f + 6f * a, bl) {
            drawRoundRect(c(Color(0xFFC9CED6)), Offset(bl.x - 10 * u, bl.y - 13 * u), Size(20 * u, 26 * u), CornerRadius(3 * u))
            drawRect(c(PANEL_OSC), Offset(bl.x - 10 * u, bl.y - 13 * u), Size(20 * u, 4 * u))
            drawRect(c(NARANJA), Offset(bl.x - 7 * u, bl.y - 4 * u), Size(14 * u, 8 * u))
            drawLine(Color.White.copy(alpha = 0.5f), Offset(bl.x - 7 * u, bl.y - 8 * u), Offset(bl.x - 7 * u, bl.y + 10 * u), 0.8f * u)
        }
    }

    /**
     * La pestaña de un día: una ficha de aluminio anodizado con su agujero para la cuerda de sujeción, el día y la letra de la
     * semana grabados en claro, y el led del color del ánimo con su halo (apagado, si no hay).
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        rotate(-3f + 6f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val r = Rect(4 * u, 3 * u, w - 4 * u, h - 3 * u)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.2f * u, r.top + 1.8f * u), r.size, CornerRadius(7 * u))
            drawRoundRect(c(CASCO_OSC), r.topLeft, r.size, CornerRadius(7 * u))
            drawRoundRect(CIAN.copy(alpha = 0.55f), r.deflate(1.5f * u).topLeft, r.deflate(1.5f * u).size, CornerRadius(6 * u), style = Stroke(0.8f * u))
            drawCircle(VACIO, 2.4f * u, Offset(w / 2, r.top + 6 * u))
            drawCircle(c(PANEL_LUZ), 2.4f * u, Offset(w / 2, r.top + 6 * u), style = Stroke(0.7f * u))
            textoEn(medidor, e.semana, Rect(r.left + 2 * u, r.top + 1 * u, r.left + 12 * u, r.top + 11 * u), letra, c(PANEL_LUZ), 7.5f, minimo = 5f)
            textoEn(medidor, e.dia, Rect(r.left + 1 * u, r.top + 12 * u, r.right - 1 * u, r.top + 34 * u), letra, mezcla(PANEL_LUZ, Color.Black, if (oscuro) 0.1f else 0f), 17f, minimo = 7f)
            val s = Offset(w / 2, r.bottom - 9 * u)
            if (e.tono != null) {
                drawCircle(e.tono.copy(alpha = 0.2f), 7 * u, s)
                drawCircle(e.tono.copy(alpha = 0.4f), 5 * u, s)
                drawCircle(e.tono, 3.4f * u, s)
                drawCircle(Color.White.copy(alpha = 0.6f), 1.1f * u, Offset(s.x - 1 * u, s.y - 1 * u))
            } else {
                drawCircle(c(CASCO), 3.4f * u, s)
                drawCircle(Color.Black.copy(alpha = 0.3f), 3.4f * u, s, style = Stroke(0.6f * u))
            }
        }
    }

    /**
     * El rótulo de un mes: la placa blanca de las carpetas de vuelo, con la banda naranja de galones a la izquierda, el mes
     * impreso y el velcro en las puntas.
     */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val r = Rect(2 * u, 4 * u, w - 2 * u, h - 4 * u)
        val placa = mezcla(CINTA, Color(0xFF05070D), if (oscuro) 0.2f else 0f)
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1 * u, r.top + 2 * u), r.size, CornerRadius(4 * u))
        drawRoundRect(placa, r.topLeft, r.size, CornerRadius(4 * u))
        val banda = Rect(r.left, r.top, r.left + 44 * u, r.bottom)
        clipRect(banda.left, banda.top, banda.right, banda.bottom) {
            drawRoundRect(c(NARANJA), banda.topLeft, Size(banda.width + 6 * u, banda.height), CornerRadius(4 * u))
            for (q in 0 until 3) {
                val x = banda.left + 8 * u + q * 11 * u
                drawPath(Path().apply { moveTo(x, banda.top + 5 * u); lineTo(x + 6 * u, banda.center.y); lineTo(x, banda.bottom - 5 * u) }, Color.White.copy(alpha = 0.85f), style = Stroke(2 * u, cap = StrokeCap.Round))
            }
        }
        val vel = Rect(r.right - 26 * u, r.top + 4 * u, r.right - 5 * u, r.bottom - 4 * u)
        drawRect(c(Color(0xFF3A3E46)), vel.topLeft, vel.size)
        var vx = vel.left + 1.5f * u; while (vx < vel.right) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(vx, vel.top + 1 * u), Offset(vx, vel.bottom - 1 * u), 0.6f * u); vx += 2 * u }
        textoEn(medidor, texto.uppercase(), Rect(banda.right + 8 * u, r.top, vel.left - 8 * u, r.bottom), LetraExtra.grotesk, Color(0xFF14203A), 14f, peso = FontWeight.Medium, espaciado = 1.2f, minimo = 7f)
    }

    // ---------------------------------------------------------------- las cartas

    /** El rojo de las cintas de «retirar antes» y el blanco roto del estuche presurizado. */
    private val ROJO_CINTA = Color(0xFFD8352A)
    private val ESTUCHE = Color(0xFFE4E9F0)

    /**
     * El parche de la misión: un disco bordado con su orla, el planeta con su anillo de órbita y una nave que lo cruza, y tres
     * estrellas. [brillo] lo enciende.
     */
    private fun DrawScope.parche(c: Offset, r: Float, alfa: Float, brillo: Float, oscuro: Boolean) {
        val u = density
        fun a(x: Color) = x.copy(alpha = x.alpha * alfa)
        fun k(x: Color) = t(x, oscuro, false)
        drawCircle(a(Color.Black.copy(alpha = 0.35f)), r, c + Offset(0.6f * u, 1.2f * u))
        drawCircle(a(mezcla(k(Color(0xFF1B2A5C)), Color(0xFF2A4A9C), brillo * 0.6f)), r, c)
        drawCircle(a(mezcla(k(AMBAR), Color(0xFFFFE0A0), brillo * 0.5f)), r, c, style = Stroke(r * 0.14f))
        drawCircle(a(Color.White.copy(alpha = 0.25f)), r * 0.86f, c, style = Stroke(r * 0.03f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(r * 0.08f, r * 0.06f))))
        drawCircle(a(MAR), r * 0.36f, c + Offset(-r * 0.1f, r * 0.12f))
        drawCircle(a(TIERRA_FIRME), r * 0.14f, c + Offset(-r * 0.2f, r * 0.04f))
        drawOval(a(mezcla(CIAN, Color.White, brillo * 0.5f)), Offset(c.x - r * 0.66f, c.y - r * 0.08f), Size(r * 1.32f, r * 0.42f), style = Stroke(r * 0.06f))
        drawCircle(a(Color.White), r * 0.08f, Offset(c.x + r * 0.52f, c.y + r * 0.02f))
        listOf(Offset(-0.4f, -0.5f), Offset(0.05f, -0.62f), Offset(0.42f, -0.42f)).forEach { o -> drawPath(estrella(c + Offset(r * o.x, r * o.y), r * 0.1f, 4, 0.4f), a(ESTRELLA)) }
    }

    /**
     * El estuche presurizado de la cápsula: blanco roto, de cantos muy redondos, con sus juntas, los tornillos de las
     * esquinas y la escotilla de arriba con su pestillo. En el frente, la mirilla por donde se ve el rollo del mensaje y el
     * visor de cristal oscuro donde se leen, en cian, a quién va y las fechas; a la derecha, las tres luces de estado. El
     * sello es el parche bordado de la misión, pegado sobre la junta de la escotilla: cuando la cápsula ya se recuperó, el
     * parche y la luz verde se encienden; al abrirla, el parche se rasga y la escotilla bascula hacia atrás. Las de al futuro
     * llevan la cinta roja de «retirar antes de abrir» pasada por el pestillo. Dentro va la transmisión, en la hoja oscura con
     * su filo cian.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 3 * u, cu.top + 2 * u, cu.right - 3 * u, cu.bottom - 4 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val junta = s.top + h * 0.28f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        val radio = minOf(h * 0.22f, 18 * u * k)
        val sc = Offset(s.left + w * 0.36f, junta)
        val sr = h * 0.11f
        val pestillo = Offset(s.right - w * 0.13f, junta)

        sombraSuave(s, radio, 7 * u, 0.3f)
        // ---- El interior del estuche (lo que se ve con la escotilla abierta).
        drawRoundRect(c(CASCO_OSC), s.topLeft, s.size, CornerRadius(radio))
        // ---- La escotilla.
        fun DrawScope.escotilla(adentro: Boolean) {
            val r = Rect(s.left, bis, s.right, junta)
            if (adentro) {
                drawRoundRect(c(CASCO), r.topLeft, r.size, CornerRadius(radio))
                drawRoundRect(c(Color(0xFF1E2638)), r.deflate(4 * u).topLeft, r.deflate(4 * u).size, CornerRadius(radio * 0.7f), style = Stroke(2 * u))
                return
            }
            drawRoundRect(c(PANEL_LUZ), r.topLeft, r.size, CornerRadius(radio))
            drawRect(c(PANEL_LUZ), Offset(r.left, r.bottom - radio * 0.5f), Size(r.width, radio * 0.5f))
            drawRect(Color.White.copy(alpha = 0.5f), Offset(r.left + radio, r.top + 2.5f * u), Size(r.width - 2 * radio, 1.2f * u))
            drawLine(c(PANEL_OSC), Offset(r.left + radio * 0.4f, r.bottom - 0.5f * u), Offset(r.right - radio * 0.4f, r.bottom - 0.5f * u), 1.4f * u)
            // Las franjas de aviso negras y amarillas del borde de la escotilla.
            val fr = Rect(r.left + w * 0.56f, r.top + r.height * 0.32f, r.left + w * 0.76f, r.top + r.height * 0.62f)
            clipRect(fr.left, fr.top, fr.right, fr.bottom) {
                drawRect(c(AMBAR), fr.topLeft, fr.size)
                var x = fr.left - fr.height; while (x < fr.right) { drawPath(Path().apply { moveTo(x, fr.bottom); lineTo(x + fr.height, fr.top); lineTo(x + fr.height * 1.6f, fr.top); lineTo(x + fr.height * 0.6f, fr.bottom); close() }, c(VACIO)); x += maxOf(fr.height * 1.6f, 1f) }
            }
            listOf(r.left + radio * 0.8f, r.right - radio * 0.8f).forEach { x -> drawCircle(c(PANEL_OSC), 1.8f * u * k, Offset(x, r.top + r.height * 0.45f)); drawLine(c(CASCO_OSC), Offset(x - 1.2f * u * k, r.top + r.height * 0.45f), Offset(x + 1.2f * u * k, r.top + r.height * 0.45f), 0.6f * u) }
            // El pestillo.
            drawRoundRect(c(CASCO), Offset(pestillo.x - 7 * u * k, pestillo.y - 7 * u * k), Size(14 * u * k, 9 * u * k), CornerRadius(2 * u))
            drawRoundRect(c(PANEL_OSC), Offset(pestillo.x - 4 * u * k, pestillo.y - 4.5f * u * k), Size(8 * u * k, 4 * u * k), CornerRadius(1 * u))
            if (e.romper > 0f) withTransform({ translate(-1f * u * e.romper, -2f * u * e.romper); rotate(-8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, true, 6, e.semilla)) { parche(sc, sr, 1f, 0f, oscuro) }
            }
        }
        if (!cerrada) solapaCarta(bis, ab) { escotilla(it) }

        // ---- La transmisión, en su hoja oscura.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.06f
            val alza = alzaCarta(e.subir, h, arriba0, 4 * u)
            val hoja = Rect(s.left + w * 0.09f, arriba0 - alza, s.right - w * 0.09f, arriba0 - alza + h * 0.86f)
            drawRoundRect(Color.Black.copy(alpha = 0.4f), hoja.topLeft + Offset(1 * u, 1.5f * u), hoja.size, CornerRadius(3 * u))
            drawRoundRect(e.hoja, hoja.topLeft, hoja.size, CornerRadius(3 * u))
            drawRoundRect(CIAN.copy(alpha = 0.55f), hoja.topLeft, hoja.size, CornerRadius(3 * u), style = Stroke(0.9f * u))
            listOf(hoja.left + 3 * u, hoja.right - 3 * u).forEach { x ->
                drawLine(CIAN.copy(alpha = 0.8f), Offset(x, hoja.top + 3 * u), Offset(x, hoja.top + 7 * u), 1 * u)
            }
            drawLine(CIAN.copy(alpha = 0.8f), Offset(hoja.left + 3 * u, hoja.top + 3 * u), Offset(hoja.left + 7 * u, hoja.top + 3 * u), 1 * u)
            drawLine(CIAN.copy(alpha = 0.8f), Offset(hoja.right - 3 * u, hoja.top + 3 * u), Offset(hoja.right - 7 * u, hoja.top + 3 * u), 1 * u)
            var y = hoja.top + 14 * u
            while (y < hoja.bottom - 3 * u) { drawLine(e.renglones, Offset(hoja.left + 6 * u, y), Offset(hoja.right - 6 * u, y), 0.6f * u); y += 11 * u }
        }

        // ---- El cuerpo del estuche.
        val cuerpo = Rect(s.left, junta, s.right, s.bottom)
        drawRoundRect(c(ESTUCHE), cuerpo.topLeft, cuerpo.size, CornerRadius(radio))
        drawRect(c(ESTUCHE), cuerpo.topLeft, Size(cuerpo.width, radio))
        drawRect(Color.White.copy(alpha = 0.55f), Offset(cuerpo.left + 2.5f * u, cuerpo.top + 3 * u), Size(1.4f * u, cuerpo.height - radio - 2 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.12f), Offset(cuerpo.right - 4 * u, cuerpo.top + 3 * u), Size(2.5f * u, cuerpo.height - radio), CornerRadius(1 * u))
        if (!cerrada) {
            drawRect(c(CASCO_OSC), Offset(cuerpo.left + 3 * u, cuerpo.top), Size(cuerpo.width - 6 * u, 2.5f * u))
        }
        drawLine(c(PANEL_OSC), Offset(cuerpo.left + w * 0.82f, cuerpo.top + 4 * u), Offset(cuerpo.left + w * 0.82f, cuerpo.bottom - 4 * u), 0.8f * u)
        listOf(cuerpo.left + radio * 0.7f, cuerpo.right - radio * 0.7f).forEach { x -> drawCircle(c(PANEL_OSC), 1.8f * u * k, Offset(x, cuerpo.bottom - radio * 0.7f)) }
        // El visor, con su marco y sus corchetes cian.
        val vis = Rect(cuerpo.left + w * 0.06f, cuerpo.top + h * 0.14f, cuerpo.left + w * 0.78f, cuerpo.bottom - h * 0.12f)
        drawRoundRect(c(PANEL_OSC), vis.inflate(2 * u).topLeft, vis.inflate(2 * u).size, CornerRadius(4 * u))
        drawRoundRect(VISOR, vis.topLeft, vis.size, CornerRadius(3 * u))
        val esq = 5 * u
        listOf(vis.topLeft to Offset(1f, 1f), Offset(vis.right, vis.top) to Offset(-1f, 1f), Offset(vis.left, vis.bottom) to Offset(1f, -1f), vis.bottomRight to Offset(-1f, -1f)).forEach { (o, d) ->
            val p = o + Offset(d.x * 2 * u, d.y * 2 * u)
            drawLine(CIAN.copy(alpha = 0.7f), p, p + Offset(d.x * esq, 0f), 1 * u); drawLine(CIAN.copy(alpha = 0.7f), p, p + Offset(0f, d.y * esq), 1 * u)
        }
        drawRect(Color.White.copy(alpha = 0.05f), Offset(vis.left, vis.top), Size(vis.width, vis.height * 0.4f))
        textoEn(medidor, e.linea1, Rect(vis.left + 4 * u, vis.top + 2 * u, vis.right - 4 * u, vis.top + vis.height * 0.56f), letra, CIAN, 11f, peso = FontWeight.Bold, minimo = 6.5f)
        textoEn(medidor, e.linea2, Rect(vis.left + 4 * u, vis.top + vis.height * 0.54f, vis.right - 4 * u, vis.bottom - 2 * u), LetraExtra.grotesk, ESTRELLA.copy(alpha = 0.85f), 9f, minimo = 6f)
        // Las tres luces de estado.
        val listo = e.brillo > 0f
        listOf(VERDE to listo, AMBAR to (e.cordon > 0f), Color(0xFFFF5470) to false).forEachIndexed { i, (col, on) ->
            val o = Offset(cuerpo.left + w * 0.9f, vis.top + vis.height * (0.18f + i * 0.32f))
            drawCircle(c(PANEL_OSC), 3.6f * u * k, o)
            if (on) { drawCircle(col.copy(alpha = 0.3f), 5.5f * u * k, o); drawCircle(col, 2.4f * u * k, o) } else drawCircle(c(mezcla(col, CASCO_OSC, 0.75f)), 2.4f * u * k, o)
        }

        // ---- La escotilla cerrada.
        if (cerrada) solapaCarta(bis, ab) { escotilla(it) }
        // ---- La cinta de «retirar antes de abrir», pasada por el pestillo.
        if (e.cordon > 0f && cerrada) {
            val cw = 5 * u * k
            val largo = (h * 0.42f) * e.cordon
            val p0 = Offset(pestillo.x + 4 * u * k, pestillo.y - 2 * u)
            rotate(8f, p0) {
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(p0.x - cw / 2 + 1 * u, p0.y + 1.5f * u), Size(cw, largo))
                drawRect(c(ROJO_CINTA), Offset(p0.x - cw / 2, p0.y), Size(cw, largo))
                if (e.cordon > 0.85f) {
                    val tg = Rect(p0.x - cw * 1.9f, p0.y + largo - 2 * u, p0.x + cw * 1.9f, p0.y + largo + h * 0.17f)
                    drawRect(Color.Black.copy(alpha = 0.3f), tg.topLeft + Offset(1 * u, 1.5f * u), tg.size)
                    drawRect(c(ROJO_CINTA), tg.topLeft, tg.size)
                    drawCircle(c(PANEL_LUZ), 1.4f * u, Offset(tg.center.x, tg.top + 3 * u), style = Stroke(0.8f * u))
                    textoEn(medidor, "RETIRAR", Rect(tg.left, tg.top + tg.height * 0.3f, tg.right, tg.top + tg.height * 0.62f), letra, Color.White, 6f, peso = FontWeight.Bold, minimo = 4f)
                    textoEn(medidor, "ANTES", Rect(tg.left, tg.top + tg.height * 0.6f, tg.right, tg.bottom - 1 * u), letra, Color.White, 6f, peso = FontWeight.Bold, minimo = 4f)
                }
            }
        }
        // ---- El parche: entero (cayendo al sellar), encendido si la cápsula ya se recuperó, o rasgado.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(VERDE.copy(alpha = 0.45f * e.brillo), CIAN.copy(alpha = 0.18f * e.brillo), Color.Transparent),
                center = sc, radius = sr * 3f), sr * 3f, sc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, sc) }) { parche(sc, sr, e.alfaSello.coerceIn(0f, 1f), e.brillo, oscuro) }
        } else {
            withTransform({ translate(1f * u * e.romper, 2f * u * e.romper); rotate(8f * e.romper, sc) }) {
                clipPath(mitadRota(Rect(sc.x - sr, sc.y - sr, sc.x + sr, sc.y + sr), sc.y, false, 6, e.semilla)) { parche(sc, sr, 1f, 0f, oscuro) }
            }
        }
    }

    // La transmisión oscura que asoma del estuche, entre sus corchetes cian.
    override val cajaPrevia = Rect(0.15f, 0.17f, 0.85f, 0.33f)

    override val altoBuzon: Dp = 214.dp

    /**
     * El tambor de cápsulas de la nave, en la pared de paneles: un carrusel redondo de diez alojamientos alrededor de su eje,
     * cada uno con su luz. Las cápsulas recuperadas brillan en verde en su alojamiento; las que siguen en órbita están
     * trabadas, con la luz ámbar y la cinta roja; las abiertas tienen la tapa levantada y el alojamiento iluminado en cian. A
     * la izquierda, el panel con los tres visores («RECUPERADAS», «EN ÓRBITA», «ABIERTAS») y su cuenta, y la ventanilla con
     * las estrellas. Si hay más cápsulas que alojamientos, el visor dice cuántas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        fun f(x: Color) = if (oscuro) mezcla(c(x), Color(0xFF03050A), 0.45f) else c(x)
        // ---- La pared de paneles.
        drawRect(f(PANEL))
        var x = 0f; while (x < w) { drawLine(f(PANEL_OSC), Offset(x, 0f), Offset(x, h), 1 * u); x += 72 * u }
        drawLine(f(PANEL_OSC), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), 1 * u)
        drawRect(f(PANEL_OSC), Offset(0f, h - 12 * u), Size(w, 12 * u))
        var gx = 4 * u; while (gx < w) { drawRect(f(CASCO_OSC), Offset(gx, h - 9 * u), Size(5 * u, 6 * u)); gx += 9 * u }
        // ---- El panel de los visores y la ventanilla.
        val pan = Rect(10 * u, 12 * u, w * 0.4f, h - 20 * u)
        drawRoundRect(Color.Black.copy(alpha = 0.2f), pan.topLeft + Offset(2 * u, 3 * u), pan.size, CornerRadius(8 * u))
        drawRoundRect(c(PANEL_LUZ), pan.topLeft, pan.size, CornerRadius(8 * u))
        drawRoundRect(c(PANEL_OSC), pan.topLeft, pan.size, CornerRadius(8 * u), style = Stroke(1 * u))
        val ven = Rect(pan.left + 10 * u, pan.top + 8 * u, pan.right - 10 * u, pan.top + 36 * u)
        drawRoundRect(c(CASCO), ven.inflate(2.5f * u).topLeft, ven.inflate(2.5f * u).size, CornerRadius(14 * u))
        drawRoundRect(VACIO, ven.topLeft, ven.size, CornerRadius(12 * u))
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            for (i in 0 until 22) drawCircle(ESTRELLA.copy(alpha = 0.4f + 0.6f * azar(5, i)), (0.5f + 0.8f * azar(7, i)) * u, Offset(ven.left + ven.width * azar(9, i), ven.top + ven.height * azar(11, i)))
            drawCircle(c(DESIERTO), 9 * u, Offset(ven.right - 16 * u, ven.top + 12 * u))
            drawCircle(VACIO.copy(alpha = 0.6f), 9 * u, Offset(ven.right - 12 * u, ven.top + 10 * u))
        }
        val total = e.listas + e.selladas + e.abiertas
        val visores = listOf(Triple("RECUPERADAS", e.listas, VERDE), Triple("EN ÓRBITA", e.selladas, AMBAR), Triple("ABIERTAS", e.abiertas, CIAN))
        val altoV = (pan.bottom - 8 * u - (ven.bottom + 8 * u)) / 3f
        visores.forEachIndexed { i, (nombre, n, col) ->
            val v = Rect(pan.left + 8 * u, ven.bottom + 8 * u + i * altoV, pan.right - 8 * u, ven.bottom + 8 * u + (i + 1) * altoV - 4 * u)
            drawRoundRect(VISOR, v.topLeft, v.size, CornerRadius(3 * u))
            drawRect(col, Offset(v.left, v.top + 3 * u), Size(2.5f * u, v.height - 6 * u))
            textoEn(medidor, nombre, Rect(v.left + 6 * u, v.top, v.left + v.width * 0.66f, v.bottom), letra, ESTRELLA.copy(alpha = 0.8f), 8f, espaciado = 0.6f, minimo = 5.5f)
            textoEn(medidor, n.toString().padStart(2, '0'), Rect(v.left + v.width * 0.64f, v.top, v.right - 4 * u, v.bottom), letra, col, 15f, peso = FontWeight.Bold)
        }
        // ---- El tambor.
        val cen = Offset(w * 0.7f, h * 0.48f)
        val rad = h * 0.42f
        val listas = e.listas > 0
        drawCircle(Color.Black.copy(alpha = 0.25f), rad + 6 * u, cen + Offset(3 * u, 4 * u))
        drawCircle(c(CASCO), rad + 6 * u, cen)
        drawCircle(c(CASCO_OSC), rad + 6 * u, cen, style = Stroke(2 * u))
        drawCircle(c(PANEL_OSC), rad, cen)
        for (i in 0 until 24) {
            val a = 2 * PI * i / 24
            drawCircle(c(CASCO_OSC), 1.3f * u, Offset(cen.x + ((rad + 3 * u) * cos(a)).toFloat(), cen.y + ((rad + 3 * u) * sin(a)).toFloat()))
        }
        if (listas) drawCircle(Brush.radialGradient(listOf(VERDE.copy(alpha = 0.35f), Color.Transparent), center = cen, radius = rad * 1.1f), rad * 1.1f, cen)
        val tipos = List(e.listas) { 0 } + List(e.selladas) { 1 } + List(e.abiertas) { 2 }
        val n = 10
        for (i in 0 until n) {
            val ang = -90f + 360f * i / n
            val a = Math.toRadians(ang.toDouble())
            val o = Offset(cen.x + (rad * 0.66f * cos(a)).toFloat(), cen.y + (rad * 0.66f * sin(a)).toFloat())
            val tipo = tipos.getOrNull(i) ?: -1
            rotate(ang + 90f, o) {
                val al = Rect(o.x - rad * 0.13f, o.y - rad * 0.27f, o.x + rad * 0.13f, o.y + rad * 0.27f)
                drawRoundRect(c(CELDA), al.topLeft, al.size, CornerRadius(al.width * 0.5f))
                if (tipo == 2) drawRoundRect(CIAN.copy(alpha = 0.25f), al.topLeft, al.size, CornerRadius(al.width * 0.5f))
                if (tipo >= 0) {
                    val cap = al.deflate(2 * u)
                    if (tipo == 0) drawRoundRect(VERDE.copy(alpha = 0.35f), cap.inflate(2 * u).topLeft, cap.inflate(2 * u).size, CornerRadius(cap.width))
                    drawRoundRect(c(ESTUCHE), Offset(cap.left, cap.top + (if (tipo == 2) cap.height * 0.3f else 0f)), Size(cap.width, cap.height * (if (tipo == 2) 0.7f else 1f)), CornerRadius(cap.width * 0.5f))
                    drawRect(Color.Black.copy(alpha = 0.15f), Offset(cap.right - 1.5f * u, cap.top + cap.height * 0.3f), Size(1.5f * u, cap.height * 0.5f))
                    // La abierta: sin tapa, con la transmisión enrollada asomando.
                    if (tipo == 2) {
                        drawRoundRect(c(Color(0xFF0A1022)), Offset(cap.left + cap.width * 0.22f, cap.top), Size(cap.width * 0.56f, cap.height * 0.4f), CornerRadius(1.5f * u))
                        drawRect(CIAN.copy(alpha = 0.7f), Offset(cap.left + cap.width * 0.22f, cap.top), Size(cap.width * 0.56f, 1 * u))
                    }
                    drawRect(VISOR, Offset(cap.left + cap.width * 0.22f, cap.center.y - 1 * u), Size(cap.width * 0.56f, cap.height * 0.24f))
                    if (tipo == 1) drawRect(c(ROJO_CINTA), Offset(cap.left, cap.top + cap.height * 0.18f), Size(cap.width, 2.5f * u))
                }
                val led = Offset(o.x, al.bottom + 4 * u)
                val col = when (tipo) { 0 -> VERDE; 1 -> AMBAR; 2 -> CIAN; else -> c(CASCO_OSC) }
                if (tipo >= 0) drawCircle(col.copy(alpha = 0.3f), 3.4f * u, led)
                drawCircle(col, 1.6f * u, led)
            }
        }
        // El eje del tambor, con su tapa y la cuenta total.
        drawCircle(c(CASCO), rad * 0.26f, cen)
        drawCircle(c(PANEL_LUZ), rad * 0.26f, cen, style = Stroke(1.5f * u))
        drawCircle(VISOR, rad * 0.19f, cen)
        textoEn(medidor, if (total > n) "+${total - n}" else total.toString(), Rect(cen.x - rad * 0.18f, cen.y - rad * 0.12f, cen.x + rad * 0.18f, cen.y + rad * 0.12f), letra,
            if (listas) VERDE else CIAN, 11f, peso = FontWeight.Bold, minimo = 6f)
    }
}

// =====================================================================
//  Ayudantes de la página abierta (solo de este archivo)
// =====================================================================

/**
 * La fecha del encabezado, la protagonista, dentro de [r]: en un renglón si cabe a [tam]; si no cabe y tiene un corte
 * (« · », « // » o «, »), se parte en dos renglones por el corte más cercano a la mitad, los dos a un 80 % del tamaño (la
 * coma se queda al final del primero; [k] cambia ese 80 %); si no tiene corte, se achica hasta caber. Sirve también para el
 * detalle largo de la noche. [r] debe tener alto para los dos renglones.
 */
private fun DrawScope.fechaFuturo(medidor: TextMeasurer, fecha: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                  peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f, k: Float = 0.8f) {
    val estilo = TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp)
    val ancho = medidor.measure(fecha, estilo, maxLines = 1, softWrap = false).size.width
    val cortes = listOf(" · ", " // ", ", ").flatMap { s ->
        var i = fecha.indexOf(s); val out = mutableListOf<Pair<Int, String>>()
        while (i > 0) { out.add(i to s); i = fecha.indexOf(s, i + s.length) }
        out
    }
    if (ancho <= r.width || cortes.isEmpty()) {
        textoEn(medidor, fecha, r, letra, color, tam, peso = peso, espaciado = espaciado, minimo = tam * 0.5f)
        return
    }
    val (i, s) = cortes.minBy { kotlin.math.abs(it.first - fecha.length / 2) }
    val a = fecha.substring(0, i).trim() + (if (s == ", ") "," else "")
    val b = fecha.substring(i + s.length).trim()
    val t2 = tam * k
    val mitad = r.top + r.height / 2
    textoEn(medidor, a, Rect(r.left, r.top, r.right, mitad), letra, color, t2, peso = peso, espaciado = espaciado, minimo = tam * 0.45f)
    textoEn(medidor, b, Rect(r.left, mitad, r.right, r.bottom), letra, color, t2, peso = peso, espaciado = espaciado, minimo = tam * 0.45f)
}

/**
 * El modo oscuro sobre un papel claro: un velo parejo de [velo] y, desde la luz del tema en [luz], el círculo donde se lee,
 * que se apaga hacia las orillas (hasta [orilla] de negro encima del velo); [tinte] es el color de esa luz, que entibia el
 * centro. El texto oscuro sigue legible porque el centro queda como papel atenuado, alrededor de un 25 % más oscuro. Usa los
 * dos degradados de la página.
 */
private fun DrawScope.penumbraFuturo(luz: Offset, tinte: Color, velo: Float = 0.2f, orilla: Float = 0.6f) {
    val radio = maxOf(size.width, size.height) * 0.85f
    drawRect(Color.Black.copy(alpha = velo))
    drawRect(Brush.radialGradient(0f to Color.Transparent, 0.4f to Color.Black.copy(alpha = 0.05f), 0.75f to Color.Black.copy(alpha = orilla * 0.6f),
        1f to Color.Black.copy(alpha = orilla), center = luz, radius = radio))
    drawRect(Brush.radialGradient(listOf(tinte.copy(alpha = 0.1f), Color.Transparent), center = luz, radius = radio * 0.55f))
}

/** El fondo de la barra de arriba sobre un papel claro: el papel con un toque de [tono], hundido como el centro de la página en oscuro. */
private fun fondoBarraFuturo(p: Pagina, tono: Color, f: Float, oscuro: Boolean): Color =
    if (p.oscura) mezcla(p.color, Color.Black, 0.28f) else mezcla(mezcla(p.color, tono, f), Color.Black, if (oscuro) 0.3f else 0f)

// =====================================================================
//  Ayudantes de la libreta de momentos (solo de este archivo)
// =====================================================================

/**
 * El grosor del bloque de hojas de la libreta según cuántas tiene: crece rápido con las primeras y luego despacio (con la
 * raíz), de [minimo] dp con una hoja hasta [tope] dp con muchas.
 */
private fun grosorLibreta(hojas: Int, u: Float, minimo: Float = 2.5f, k: Float = 1.3f, tope: Float = 21f): Float =
    (minimo + k * sqrt(hojas.coerceAtLeast(0).toFloat())).coerceAtMost(tope) * u

/** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para las tapas que giran). */
private inline fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, crossinline bloque: DrawScope.() -> Unit) {
    withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
}

/**
 * Una tapa que gira sobre su lomo, a la izquierda (en [x0]), vista desde delante y un poco desde arriba: con [angulo] (en
 * radianes, de 0, cerrada, a π, abierta del todo) el canto libre se acerca al lomo y sube [alza] veces el ancho recorrido.
 * Mientras se ve por fuera (coseno positivo) dibuja [fuera] en las coordenadas de la tapa cerrada (a la derecha del lomo);
 * después, [dentro] en las de la tapa abierta del todo (a la izquierda del lomo), sin espejo. [sombra] (0 a 1) es cuánto
 * se ve de canto, para oscurecerla.
 */
private inline fun DrawScope.tapaLateral(x0: Float, angulo: Float, alza: Float,
                                         crossinline fuera: DrawScope.(sombra: Float) -> Unit, crossinline dentro: DrawScope.(sombra: Float) -> Unit) {
    val co = cos(angulo); val si = sin(angulo)
    val canto = 1f - kotlin.math.abs(co)
    if (co >= 0f) afin(co, -si * alza, 0f, 1f, x0 - x0 * co, x0 * si * alza) { fuera(canto) }
    else afin(-co, si * alza, 0f, 1f, x0 + x0 * co, -x0 * si * alza) { dentro(canto) }
}

/**
 * Una tapa que gira hacia arriba sobre la bisagra de atrás (en [y0], arriba de la tapa cerrada, de alto [alto]), vista desde
 * delante y un poco desde arriba: con [angulo] (radianes) el canto libre sube; [alza] es cuánto más se ve lo vertical que
 * lo hondo. Mientras se ve por fuera dibuja [fuera] en las coordenadas de la tapa cerrada; cuando ya se levantó, [dentro]
 * en esas mismas coordenadas, con el canto libre arriba (sin espejo). [sombra] (0 a 1) es cuánto se ve de canto.
 */
private inline fun DrawScope.tapaAlzada(y0: Float, alto: Float, angulo: Float, alza: Float,
                                        crossinline fuera: DrawScope.(sombra: Float) -> Unit, crossinline dentro: DrawScope.(sombra: Float) -> Unit) {
    val s = cos(angulo) - alza * sin(angulo)
    val canto = (1f - kotlin.math.abs(s) / maxOf(1f, alza)).coerceIn(0f, 1f)
    if (s >= 0f) afin(1f, 0f, 0f, s, 0f, y0 - y0 * s) { fuera(canto) }
    else afin(1f, 0f, 0f, -s, 0f, y0 + s * alto + y0 * s) { dentro(canto) }
}

/** Cuánto se ve la tapa alzada por encima de la bisagra, en fracción de su alto cerrada (negativo: aún tapa las hojas). */
private fun alzadoTapa(angulo: Float, alza: Float): Float = alza * sin(angulo) - cos(angulo)

/**
 * El canto del bloque de hojas en [r] (la cara de delante): rayitas de papel apretadas, alternando [papel] y su sombra, con
 * una que otra hoja que sobresale; [tinta] para las rayas.
 */
private fun DrawScope.cantoHojas(r: Rect, papel: Color, tinta: Color, semilla: Int) {
    val u = density
    if (r.height <= 0.5f) return
    drawRect(papel, r.topLeft, r.size)
    var y = r.top + 1.1f * u; var k = 0
    while (y < r.bottom - 0.3f * u) {
        val izq = r.left + (1.5f * azar(semilla, 700 + k)) * u
        val der = r.right - (1.5f * azar(semilla, 800 + k)) * u
        drawLine(tinta.copy(alpha = 0.12f + 0.16f * azar(semilla, 900 + k)), Offset(izq, y), Offset(der, y), 0.55f * u)
        y += (1.05f + 0.5f * azar(semilla, 1000 + k)) * u; k++
    }
    drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.left, r.bottom - 0.9f * u), Size(r.width, 0.9f * u))
}

/** Un ancho de texto, para ajustar cintas y placas a lo que dicen. */
private fun anchoTexto(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f): Float =
    medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

/** El ángulo de una tapa que gira según cuánto se ha abierto la libreta: rápido al principio, para que a media apertura ya se vea por dentro. */
private fun giroTapa(abierto: Float): Float = 0.92f * PI.toFloat() * Math.pow(abierto.coerceIn(0f, 1f).toDouble(), 0.7).toFloat()

/** La sombra que echa una tapa medio levantada sobre la hoja, desde [x] hacia la derecha: franjas cada vez más tenues. */
private fun DrawScope.sombraTapa(x: Float, arriba: Float, ancho: Float, alto: Float, fuerza: Float = 0.24f) {
    if (ancho <= 0.5f) return
    for (k in 0 until 5) drawRect(Color.Black.copy(alpha = fuerza / 5f), Offset(x, arriba), Size(ancho * (1f - k / 5f), alto))
}

// =====================================================================
//  Ayudantes de las cartas (solo de este archivo)
// =====================================================================

/** El trazo que sigue los [puntos] hasta la fracción [f] de su largo (para el cordón que se va atando). */
private fun recorrido(puntos: List<Offset>, f: Float): Path {
    val p = Path()
    if (puntos.size < 2 || f <= 0f) return p
    var total = 0f
    for (i in 1 until puntos.size) total += (puntos[i] - puntos[i - 1]).getDistance()
    var falta = total * f.coerceIn(0f, 1f)
    p.moveTo(puntos[0].x, puntos[0].y)
    for (i in 1 until puntos.size) {
        val a = puntos[i - 1]; val b = puntos[i]
        val l = (b - a).getDistance()
        if (l >= falta) { val q = a + (b - a) * (falta / l.coerceAtLeast(0.001f)); p.lineTo(q.x, q.y); break }
        p.lineTo(b.x, b.y); falta -= l
    }
    return p
}

/**
 * La solapa que gira sobre su bisagra horizontal en [bisagra] (y): con [abrir] en 0 cuelga hacia abajo, en 0.5 queda de canto
 * y en 1 apunta hacia arriba, mostrando su cara de dentro ([bloque] recibe si se ve por dentro).
 */
private inline fun DrawScope.solapaCarta(bisagra: Float, abrir: Float, crossinline bloque: DrawScope.(adentro: Boolean) -> Unit) {
    val k = cos(PI * abrir.coerceIn(0f, 1f)).toFloat()
    withTransform({ scale(1f, if (kotlin.math.abs(k) < 0.02f) 0.02f * (if (k < 0f) -1f else 1f) else k, Offset(size.width / 2f, bisagra)) }) { bloque(k < 0f) }
}

/** Media pieza de un sello que se parte por una línea quebrada a la altura [y]: la de arriba ([arriba]) o la de abajo, dentro de [r]. */
private fun mitadRota(r: Rect, y: Float, arriba: Boolean, dientes: Int = 7, semilla: Int = 3): Path = Path().apply {
    val amp = r.height * 0.09f
    moveTo(r.left - 2f, if (arriba) r.top - 2f else r.bottom + 2f)
    lineTo(r.left - 2f, y)
    for (i in 1..dientes) {
        val x = r.left + r.width * i / dientes
        lineTo(x - r.width / dientes * 0.5f, y + (if (i % 2 == 0) amp else -amp) * (0.6f + 0.8f * azar(semilla, i)))
        lineTo(x + if (i == dientes) 2f else 0f, y)
    }
    lineTo(r.right + 2f, if (arriba) r.top - 2f else r.bottom + 2f)
    close()
}

/** La subida de la carta: cuánto sale del sobre (con [subir] de 0 a 1) sin pasarse del borde de arriba del lienzo. */
private fun alzaCarta(subir: Float, altoCuerpo: Float, arribaHoja: Float, margen: Float): Float =
    minOf(subir.coerceIn(0f, 1f) * altoCuerpo * 0.9f, (arribaHoja - margen).coerceAtLeast(0f))
