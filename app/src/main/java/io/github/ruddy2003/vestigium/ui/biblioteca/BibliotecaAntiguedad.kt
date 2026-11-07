package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Antigüedad
// =====================================================================

/**
 * Prehistoria: el abrigo bajo la roca, a la entrada de la cueva. Las repisas son lajas de caliza apoyadas en pilas de
 * piedras; arriba, la visera de roca con sus capas, unos signos pintados con ocre, las estalactitas que gotean y un manojo de
 * hierbas colgado; detrás, el cielo de afuera. Al pie, el fogón con su cerco de piedras y la leña cruzada. Cada entrada es
 * una laja de pie, de contorno desportillado, con el día pintado en ocre, una mano en positivo (apoyada untada de ocre) y el
 * mes con carbón; las rayas de cuenta crecen con lo escrito y la laja se ensancha. De noche el fuego se vuelve brasas y en la
 * boca del abrigo salen las estrellas; en la laja, la luna en lugar de la mano. Distinto del Ideario, que es la pared honda
 * de la cueva con sus nichos tallados, las manos en negativo y el bisonte.
 */
object EstanteCueva : DisenoBiblioteca {
    private val ROCA = Color(0xFFB09A7C)
    private val ROCA_LUZ = Color(0xFFD6C6A8)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF4A3B2D)
    private val LAJA = Color(0xFFCDBB9A)
    private val LAJA_OSC = Color(0xFF9C8868)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    private val CIELO = Color(0xFFC9D3D0)
    private val LOMA = Color(0xFF8E9A86)
    private val PASTO = Color(0xFF8C8550)
    private val TIERRA = Color(0xFF6B5541)
    private val BRASA = Color(0xFFE8804F)
    private val FUEGO = Color(0xFFF4C76E)
    private val LENA = Color(0xFF4A3424)
    /** Las calizas de las lajas: de la más clara a la más tostada. */
    private val CALIZAS = listOf(Color(0xFFE6DAC2), Color(0xFFD9C8A8), Color(0xFFCDB998), Color(0xFFE0CDB0), Color(0xFFC9B9A0))
    private val NOCHE = Color(0xFF10142A)
    private val CIELO_NOCHE = Color(0xFF131A38)
    private val ESTRELLA = Color(0xFFF1EBD0)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la sombra azul de afuera. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0F0B08), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.5f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.13f
    override val lado = 0.06f
    override val altoRepisa: Dp = 108.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 4.dp
    override val letra: FontFamily = LetraExtra.abrigo

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 35).coerceAtMost(22)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.72f + 0.2f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo del abrigo: la roca honda, con sus capas onduladas, sus grietas y el hollín del fogón.
        val cuerpo = Rect(0f, d.corona.bottom - d.corona.height * 0.12f, w, d.base.top + d.base.height * 0.35f)
        drawRect(c(ROCA_HONDA), cuerpo.topLeft, cuerpo.size)
        var y = cuerpo.top + 12 * u
        var k = 0
        while (y < cuerpo.bottom) {
            val capa = Path()
            capa.moveTo(0f, y)
            var x = 0f
            var j = 0
            while (x < w) {
                val dy = (azar(k * 37 + j, 5) - 0.5f) * 7 * u
                capa.quadraticTo(x + 16 * u, y + dy, x + 32 * u, y)
                x += 32 * u; j++
            }
            drawPath(capa, c(ROCA_OSC).copy(alpha = 0.4f), style = Stroke(1f * u))
            y += (20 + 10 * azar(k, 3)) * u; k++
        }
        for (j in 0 until 4) {
            val x0 = w * (0.15f + 0.22f * j + 0.05f * azar(j, 7))
            val grieta = Path().apply {
                moveTo(x0, cuerpo.top + cuerpo.height * 0.05f * j)
                for (s in 1..6) lineTo(x0 + (azar(j * 11 + s, 8) - 0.5f) * 14 * u, cuerpo.top + cuerpo.height * (0.05f * j + 0.08f * s))
            }
            drawPath(grieta, Color.Black.copy(alpha = 0.3f), style = Stroke(0.9f * u))
        }
        listOf(0.5f to 0.02f, 0.42f to 0.08f, 0.58f to 0.13f).forEach { (fx, fy) ->
            drawOval(Color.Black.copy(alpha = 0.12f), Offset(w * fx - w * 0.16f, cuerpo.top + cuerpo.height * fy), Size(w * 0.32f, cuerpo.height * 0.1f))
        }
        // Cada repisa: la sombra de la laja de arriba.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.32f), r.topLeft, Size(r.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left, r.top + 7 * u), Size(r.width, 7 * u))
        }
        // Los costados: pilas de piedras donde se apoyan las lajas.
        listOf(d.izquierda, d.derecha).forEachIndexed { s, r ->
            var yy = r.top
            var n = 0
            while (yy < r.bottom) {
                val q = s * 50 + n
                val altoPiedra = (12 + 9 * azar(q, 11)) * u
                val piedra = Rect(r.left - 2 * u + 3 * u * azar(q, 12), yy, r.right + 2 * u - 3 * u * azar(q, 13), minOf(yy + altoPiedra, r.bottom + 2 * u))
                val forma = contornoIrregular(piedra, q, 14, 0.12f)
                drawPath(forma, c(if (n % 2 == 0) ROCA else LAJA_OSC))
                clipPath(forma) {
                    drawRect(Color.White.copy(alpha = 0.14f), piedra.topLeft, Size(piedra.width, piedra.height * 0.3f))
                    drawRect(Color.Black.copy(alpha = 0.2f), Offset(piedra.left, piedra.bottom - piedra.height * 0.3f), Size(piedra.width, piedra.height * 0.3f))
                }
                drawPath(forma, c(ROCA_OSC), style = Stroke(0.8f * u))
                yy += altoPiedra - 1.5f * u; n++
            }
        }
        // Las tablas: lajas de caliza, desportilladas, con su capa más clara arriba.
        d.tablas.forEachIndexed { q, r ->
            val laja = Rect(r.left - 5 * u, r.top - 1.5f * u, r.right + 5 * u, r.bottom + 1 * u)
            val forma = contornoIrregular(laja, 70 + q, 48, 0.16f)
            drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.bottom), Size(r.width, 4 * u))
            drawPath(forma, c(LAJA))
            clipPath(forma) {
                drawRect(c(ROCA_LUZ).copy(alpha = 0.7f), laja.topLeft, Size(laja.width, laja.height * 0.22f))
                drawLine(c(LAJA_OSC).copy(alpha = 0.6f), Offset(laja.left, laja.top + laja.height * 0.55f), Offset(laja.right, laja.top + laja.height * 0.6f), 0.7f * u)
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(laja.left, laja.bottom - laja.height * 0.25f), Size(laja.width, laja.height * 0.25f))
            }
            drawPath(forma, c(ROCA_OSC), style = Stroke(0.8f * u))
        }
        // El rótulo de cada mes: una piedra plana y clara apoyada en la laja, pintada con ocre y con sus puntos.
        d.placas.forEach { p ->
            val r = p.r
            val canto = contornoIrregular(r.inflate(1.5f * u), p.texto.hashCode(), 20, 0.1f)
            drawPath(canto, c(CALIZAS[0]))
            drawPath(canto, c(OCRE).copy(alpha = 0.7f), style = Stroke(1.1f * u))
            for (j in 0 until 3) {
                drawCircle(c(OCRE), 1.1f * u, Offset(r.left + 4 * u, r.top + r.height * (0.25f + 0.25f * j)))
                drawCircle(c(OCRE), 1.1f * u, Offset(r.right - 4 * u, r.top + r.height * (0.25f + 0.25f * j)))
            }
            textoEn(medidor, p.texto, Rect(r.left + 7 * u, r.top, r.right - 7 * u, r.bottom), letra, c(OCRE), 11f, peso = FontWeight.Bold, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el suelo de tierra con sus guijarros, las piedras planas al pie de las pilas y el fogón.
        val b = d.base
        val piso = b.top + b.height * 0.35f
        drawRect(c(TIERRA), Offset(0f, piso), Size(w, h - piso))
        for (j in 0 until 18) {
            drawOval(c(ROCA_OSC).copy(alpha = 0.6f), Offset(w * azar(j, 21), piso + (h - piso) * (0.3f + 0.6f * azar(j, 22))),
                Size((2 + 3 * azar(j, 23)) * u, (1.2f + 1.5f * azar(j, 24)) * u))
        }
        listOf(0f to 0.2f, 0.8f to 1f).forEachIndexed { j, (a, z) ->
            val p0 = Rect(w * a - 4 * u, b.top - 2 * u, w * z + 4 * u, piso + b.height * 0.2f)
            val forma = contornoIrregular(p0, 90 + j, 24, 0.14f)
            drawPath(forma, c(ROCA))
            drawPath(forma, c(ROCA_OSC), style = Stroke(0.8f * u))
        }
        fogon(Offset(w * 0.5f, b.top + b.height * 0.66f), b.height, oscuro, noche)
    }

    /** La boca del abrigo: el cielo de afuera (de noche, con estrellas), la visera de roca con sus estalactitas y el manojo colgado. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El cielo, con la loma lejana de día; de noche, las estrellas.
        val cielo = Rect(r.left, r.top, r.right, r.top + r.height * 0.45f)
        drawRect(if (noche) CIELO_NOCHE else c(CIELO), cielo.topLeft, cielo.size)
        if (noche) {
            for (j in 0 until 22) {
                val p = Offset(r.left + r.width * azar(j, 41), cielo.top + cielo.height * 0.9f * azar(j, 42))
                val e = (0.6f + 1.1f * azar(j, 43)) * u
                drawCircle(ESTRELLA.copy(alpha = 0.55f + 0.45f * azar(j, 44)), e, p)
                if (j % 5 == 0) {
                    drawLine(ESTRELLA.copy(alpha = 0.6f), Offset(p.x - e * 3, p.y), Offset(p.x + e * 3, p.y), 0.5f * u)
                    drawLine(ESTRELLA.copy(alpha = 0.6f), Offset(p.x, p.y - e * 3), Offset(p.x, p.y + e * 3), 0.5f * u)
                }
            }
        } else {
            val loma = Path().apply {
                moveTo(r.left, cielo.bottom)
                lineTo(r.left, cielo.top + cielo.height * 0.62f)
                cubicTo(r.left + r.width * 0.25f, cielo.top + cielo.height * 0.4f, r.left + r.width * 0.5f, cielo.top + cielo.height * 0.75f, r.left + r.width * 0.72f, cielo.top + cielo.height * 0.5f)
                cubicTo(r.left + r.width * 0.85f, cielo.top + cielo.height * 0.38f, r.right, cielo.top + cielo.height * 0.55f, r.right, cielo.top + cielo.height * 0.55f)
                lineTo(r.right, cielo.bottom); close()
            }
            drawPath(loma, c(LOMA).copy(alpha = 0.7f))
        }
        // Bajo la visera, la sombra honda del abrigo.
        drawRect(c(ROCA_HONDA), Offset(r.left, r.top + r.height * 0.4f), Size(r.width, r.height * 0.6f))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.top + r.height * 0.62f), Size(r.width, r.height * 0.18f))
        // La visera de roca: arriba ondulada, abajo desportillada.
        val borde = r.top + r.height * 0.64f
        val pasos = 18
        val visera = Path().apply {
            moveTo(r.left, r.top + r.height * 0.22f)
            cubicTo(r.left + r.width * 0.18f, r.top + r.height * 0.1f, r.left + r.width * 0.4f, r.top + r.height * 0.32f, r.left + r.width * 0.6f, r.top + r.height * 0.22f)
            cubicTo(r.left + r.width * 0.76f, r.top + r.height * 0.14f, r.right - r.width * 0.1f, r.top + r.height * 0.08f, r.right, r.top + r.height * 0.15f)
            for (j in pasos downTo 0) {
                val x = r.left + r.width * j / pasos
                val caida = if (j == 0 || j == pasos) r.height * 0.12f else 0f
                lineTo(x, borde + caida + (azar(j, 45) - 0.5f) * r.height * 0.07f)
            }
            close()
        }
        drawPath(visera, c(ROCA))
        clipPath(visera) {
            for (j in 0 until 6) {
                val yy = r.top + r.height * (0.18f + 0.08f * j)
                drawLine(c(ROCA_OSC).copy(alpha = 0.45f), Offset(r.left, yy + (azar(j, 46) - 0.5f) * 6 * u), Offset(r.right, yy + (azar(j, 47) - 0.5f) * 6 * u), 1f * u)
            }
            drawRect(c(ROCA_LUZ).copy(alpha = 0.5f), Offset(r.left, r.top), Size(r.width, r.height * 0.26f))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, borde - r.height * 0.1f), Size(r.width, r.height * 0.3f))
            // Signos pintados con ocre en la cara de la roca: una hilera de puntos y unas barras (formas, no figuras).
            for (j in 0 until 7) drawCircle(c(OCRE).copy(alpha = 0.75f), 1.6f * u, Offset(r.left + r.width * (0.2f + 0.035f * j), r.top + r.height * 0.42f + (j % 2) * 1.5f * u))
            for (j in 0 until 4) {
                drawLine(c(OCRE).copy(alpha = 0.7f), Offset(r.left + r.width * (0.66f + 0.03f * j), r.top + r.height * 0.36f),
                    Offset(r.left + r.width * (0.665f + 0.03f * j), r.top + r.height * 0.5f), 2.2f * u, cap = StrokeCap.Round)
            }
            drawLine(c(OCRE_AMARILLO).copy(alpha = 0.7f), Offset(r.left + r.width * 0.64f, r.top + r.height * 0.54f), Offset(r.left + r.width * 0.78f, r.top + r.height * 0.54f), 2f * u, cap = StrokeCap.Round)
        }
        drawPath(visera, c(ROCA_OSC), style = Stroke(1.2f * u))
        // Las estalactitas que cuelgan del borde, alguna con su gota.
        val n = 13
        for (j in 0 until n) {
            val x = r.left + r.width * (0.04f + 0.92f * j / (n - 1)) + (azar(j, 48) - 0.5f) * 8 * u
            val y0 = borde - r.height * 0.02f
            val largo = r.height * (0.07f + 0.2f * azar(j, 49))
            val grueso = (3 + 4 * azar(j, 50)) * u
            val punta = Path().apply {
                moveTo(x - grueso, y0); lineTo(x + grueso, y0); lineTo(x + grueso * 0.15f, y0 + largo); lineTo(x - grueso * 0.15f, y0 + largo); close()
            }
            drawPath(punta, c(if (j % 2 == 0) ROCA_LUZ else ROCA))
            drawLine(c(ROCA_OSC).copy(alpha = 0.6f), Offset(x + grueso * 0.5f, y0), Offset(x + grueso * 0.1f, y0 + largo), 0.8f * u)
            if (azar(j, 51) > 0.7f) drawCircle(c(CIELO).copy(alpha = 0.8f), 1.1f * u, Offset(x, y0 + largo + 2.5f * u))
        }
        // Un manojo de hierbas secas colgado de la roca con una fibra.
        val mx = r.left + r.width * 0.88f
        val atado = borde + r.height * 0.14f
        drawLine(c(LAJA_OSC), Offset(mx, borde), Offset(mx, atado), 1f * u)
        for (j in -3..3) drawLine(c(PASTO), Offset(mx, atado), Offset(mx + j * 2.2f * u, atado + r.height * (0.16f - 0.01f * kotlin.math.abs(j))), 1.1f * u, cap = StrokeCap.Round)
        drawRect(c(OCRE), Offset(mx - 2.4f * u, atado - 1 * u), Size(4.8f * u, 2.4f * u))
    }

    /** El fogón al pie del abrigo: el cerco de piedras, la leña cruzada y el fuego (de noche, las brasas que brillan). */
    private fun DrawScope.fogon(o: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val ra = alto * 0.75f
        // El resplandor: el único degradado del mueble.
        mancha(Offset(o.x, o.y - alto * 0.2f), ra * (if (noche) 2.6f else 1.8f), (if (noche) BRASA else FUEGO).copy(alpha = if (noche) 0.4f else 0.3f))
        drawLine(c(LENA), Offset(o.x - ra * 0.7f, o.y + alto * 0.08f), Offset(o.x + ra * 0.55f, o.y - alto * 0.12f), 4 * u, cap = StrokeCap.Round)
        drawLine(c(LENA), Offset(o.x + ra * 0.7f, o.y + alto * 0.08f), Offset(o.x - ra * 0.55f, o.y - alto * 0.12f), 4 * u, cap = StrokeCap.Round)
        if (noche) {
            // Las brasas sobre la leña y alguna chispa que sube.
            for (j in 0 until 9) {
                val p = Offset(o.x + (azar(j, 31) - 0.5f) * ra * 1.2f, o.y - alto * 0.02f + (azar(j, 32) - 0.5f) * alto * 0.16f)
                drawCircle(if (j % 3 == 0) FUEGO else BRASA, (1.2f + 1.6f * azar(j, 33)) * u, p)
            }
            drawCircle(FUEGO.copy(alpha = 0.8f), 0.9f * u, Offset(o.x + ra * 0.2f, o.y - alto * 0.55f))
            drawCircle(FUEGO.copy(alpha = 0.6f), 0.7f * u, Offset(o.x - ra * 0.15f, o.y - alto * 0.75f))
        } else {
            // Las llamas: tres lenguas, la de en medio más alta.
            listOf(-0.3f to 0.45f, 0f to 0.7f, 0.28f to 0.5f).forEach { (fx, fy) ->
                val bx = o.x + fx * ra
                val llama = Path().apply {
                    moveTo(bx - ra * 0.22f, o.y)
                    quadraticTo(bx - ra * 0.24f, o.y - alto * fy * 0.5f, bx, o.y - alto * fy)
                    quadraticTo(bx + ra * 0.24f, o.y - alto * fy * 0.5f, bx + ra * 0.22f, o.y)
                    close()
                }
                drawPath(llama, c(BRASA))
                val dentro = Path().apply {
                    moveTo(bx - ra * 0.1f, o.y)
                    quadraticTo(bx - ra * 0.1f, o.y - alto * fy * 0.35f, bx, o.y - alto * fy * 0.6f)
                    quadraticTo(bx + ra * 0.1f, o.y - alto * fy * 0.35f, bx + ra * 0.1f, o.y)
                    close()
                }
                drawPath(dentro, c(FUEGO))
            }
        }
        // El cerco de piedras.
        for (j in 0 until 7) {
            val f = j / 6f
            val p = Offset(o.x - ra + 2 * ra * f, o.y + alto * 0.12f + sin(f * 3.1416f) * alto * 0.08f)
            val pr = Rect(p.x - ra * 0.17f, p.y - alto * 0.07f, p.x + ra * 0.17f, p.y + alto * 0.07f)
            drawOval(c(ROCA), pr.topLeft, pr.size)
            drawOval(c(ROCA_OSC), pr.topLeft, pr.size, style = Stroke(0.7f * u))
        }
    }

    /** Una mano en positivo: la palma y los cinco dedos, como si la hubieran apoyado untada de ocre. */
    private fun DrawScope.mano(c0: Offset, m: Float, color: Color, semilla: Int) {
        val pc = Offset(c0.x, c0.y + m * 0.45f)
        drawOval(color, Offset(pc.x - m * 0.5f, pc.y - m * 0.55f), Size(m * 1.0f, m * 1.1f))
        listOf(-72f to 0.55f, -24f to 0.85f, -5f to 0.95f, 14f to 0.88f, 33f to 0.68f).forEachIndexed { j, (a0, largo) ->
            val a = Math.toRadians((a0 + (azar(semilla, 60 + j) - 0.5f) * 8f).toDouble())
            val dx = sin(a).toFloat(); val dy = -cos(a).toFloat()
            val r0 = m * 0.38f; val r1 = m * (0.38f + largo)
            drawLine(color, Offset(pc.x + dx * r0, pc.y + dy * r0), Offset(pc.x + dx * r1, pc.y + dy * r1), m * 0.26f, cap = StrokeCap.Round)
        }
        drawRect(color, Offset(pc.x - m * 0.32f, pc.y + m * 0.3f), Size(m * 0.64f, m * 0.45f))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = CALIZAS[(v.semilla and 0x7fffffff) % CALIZAS.size]
        val piedra = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // La laja de pie, desportillada, con luz a la izquierda y sombra a la derecha (planas, sin degradados).
        val contorno = contornoIrregular(Rect(0f, 0f, w, h), v.semilla, 22, 0.05f)
        drawPath(contorno, piedra)
        clipPath(contorno) {
            drawRect(Color.White.copy(alpha = 0.12f), Offset.Zero, Size(w * 0.18f, h))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(w * 0.82f, 0f), Size(w * 0.18f, h))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(0f, h * 0.9f), Size(w, h * 0.1f))
            // Las capas de la caliza y algún poro.
            for (j in 0 until 4) {
                val yy = h * (0.15f + 0.22f * j + 0.06f * azar(v.semilla, 10 + j))
                drawLine(c(LAJA_OSC).copy(alpha = 0.35f), Offset(0f, yy), Offset(w, yy + (azar(v.semilla, 20 + j) - 0.5f) * 6 * u), 0.7f * u)
            }
            for (j in 0 until 5) drawCircle(c(LAJA_OSC).copy(alpha = 0.4f), (0.6f + 0.8f * azar(v.semilla, 30 + j)) * u, Offset(w * azar(v.semilla, 40 + j), h * azar(v.semilla, 50 + j)))
        }
        drawPath(contorno, c(ROCA_OSC), style = Stroke(0.8f * u))
        // El día, pintado con ocre arriba.
        textoEn(medidor, v.dia, Rect(w * 0.08f, h * 0.05f, w * 0.92f, h * 0.24f), letra, c(OCRE), 15f, peso = FontWeight.Bold)
        // La mano en positivo; de noche, la luna y tres estrellas en ocre amarillo.
        val cm = Offset(w * 0.5f, h * 0.36f)
        val m = minOf(w * 0.3f, h * 0.1f)
        if (v.noche) {
            drawCircle(c(OCRE_AMARILLO), m * 0.8f, Offset(cm.x, cm.y + m * 0.4f))
            drawCircle(piedra, m * 0.68f, Offset(cm.x + m * 0.35f, cm.y + m * 0.2f))
            for (j in 0 until 3) drawCircle(c(OCRE_AMARILLO), 1f * u, Offset(w * (0.25f + 0.25f * j), cm.y - m * (0.6f + 0.3f * (j % 2))))
        } else {
            mano(cm, m, c(OCRE).copy(alpha = 0.85f), v.semilla)
        }
        // El mes, con carbón.
        textoEn(medidor, v.mes, Rect(w * 0.16f, h * 0.52f, w * 0.84f, h * 0.85f), letra, c(CARBON).copy(alpha = 0.85f), 9f, vertical = true, minimo = 4.5f)
        // Las rayas de cuenta: más cuanto más escribiste.
        val rayas = 1 + (v.lleno * 6).toInt()
        val paso = w * 0.7f / 7f
        for (j in 0 until rayas) {
            val x = w * 0.15f + paso * j
            drawLine(c(OCRE).copy(alpha = 0.8f), Offset(x, h * 0.87f), Offset(x + 0.6f * u, h * 0.95f), 1.4f * u, cap = StrokeCap.Round)
        }
    }
}


/**
 * Roma: los nichos (loculamenta) hundidos en un muro de mármol de una domus, entre dos pilastras estriadas. Cada repisa es un
 * nicho pintado de rojo pompeyano por dentro, con su filete ocre, y la tabla de mármol lleva su astrágalo de cuentas; el mes
 * va en un titulus de mármol rotulado en minio. Arriba, la cornisa con dentículos y rosetas, un busto sobre su herma, dos
 * guirnaldas de laurel y una lucerna de bronce sobre su candelabro; abajo, el zócalo negro con paneles ocres y el piso de
 * mosaico. Cada entrada es un rollo tumbado, con su umbilicus y sus perillas de hueso o de ébano, que engorda con lo escrito,
 * el mes abreviado en tinta y el sillybos rojo colgando con el día; un sello de plomo en el hilo (de noche, una lunita). De
 * noche la lucerna se enciende. Distinto del Ideario, que es el armarium de nogal con su frontón y las capsae de cuero.
 */
object EstanteRomano : DisenoBiblioteca {
    private val MARMOL = Color(0xFFECE6D9)
    private val MARMOL_LUZ = Color(0xFFF8F4EA)
    private val MARMOL_VETA = Color(0xFFB4AA98)
    private val MARMOL_SOMBRA = Color(0xFFA0957F)
    private val POMPEYANO = Color(0xFF8E2B1F)
    private val OCRE = Color(0xFFC98B3A)
    private val NEGRO = Color(0xFF1E1714)
    private val BRONCE = Color(0xFFB98B4A)
    private val BRONCE_OSC = Color(0xFF6E4E22)
    private val BRONCE_LUZ = Color(0xFFE0B878)
    private val LAUREL = Color(0xFF5E7A3A)
    private val LAUREL_LUZ = Color(0xFF86A05A)
    /** Los papiros de los rollos, de más nuevo a más tostado. */
    private val PAPIROS = listOf(Color(0xFFE8D6A4), Color(0xFFDCC48C), Color(0xFFE3CF9E), Color(0xFFD2B67C), Color(0xFFE0C894))
    private val PAPIRO_OSC = Color(0xFFAD925A)
    private val MINIO = Color(0xFFB0302A)
    private val PURPURA = Color(0xFF5B2A4E)
    private val HUESO = Color(0xFFEDE3CB)
    private val EBANO = Color(0xFF2B1C12)
    private val PLOMO = Color(0xFF6C6E70)
    private val TINTA = Color(0xFF2A1A0C)
    private val LLAMA = Color(0xFFF2C46A)
    private val NOCHE = Color(0xFF16152B)
    private val LUNA = Color(0xFFEDE6C8)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la penumbra azul del atrio. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100D0B), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.48f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.11f
    override val lado = 0.075f
    override val altoRepisa: Dp = 56.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = Letra.inscripcion

    /** Los rollos van tumbados: casi todos del mismo largo; lo que crece con las palabras es su grosor ([alto]). */
    override fun ancho(palabras: Int): Dp = (62 + (palabras / 50).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float =
        (0.42f + 0.5f * (palabras / 800f).coerceIn(0f, 1f) + 0.06f * azar(semilla, 1)).coerceAtMost(1f)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de mármol, con sus vetas.
        val cuerpo = Rect(0f, d.corona.bottom, w, d.base.top)
        drawRect(c(MARMOL), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(MARMOL_VETA).copy(alpha = 0.3f), 13, densidad = 0.18f, grosor = 0.7f, ondulacion = 4f)
        // Cada repisa es un nicho hundido, rojo por dentro, con su filete ocre y su sombra arriba y a la izquierda.
        d.repisas.forEach { r ->
            val marco = r.inflate(2 * u)
            drawRect(c(MARMOL_SOMBRA), marco.topLeft, marco.size)
            drawRect(c(POMPEYANO), r.topLeft, r.size)
            val filete = r.deflate(4 * u)
            drawRect(c(OCRE).copy(alpha = 0.55f), filete.topLeft, filete.size, style = Stroke(0.8f * u))
            drawRect(Color.Black.copy(alpha = if (noche) 0.45f else 0.35f), r.topLeft, Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(r.left, r.top + 6 * u), Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), r.topLeft, Size(5 * u, r.height))
        }
        // Las pilastras: el fuste estriado, el capitel con sus dos rosetas y la basa.
        listOf(d.izquierda, d.derecha).forEach { r ->
            val fuste = Rect(r.left + r.width * 0.12f, r.top, r.right - r.width * 0.12f, r.bottom)
            drawRect(c(MARMOL_SOMBRA), r.topLeft, r.size)
            drawRect(c(MARMOL), fuste.topLeft, fuste.size)
            for (j in 1..4) {
                val x = fuste.left + fuste.width * j / 5f
                drawLine(c(MARMOL_SOMBRA), Offset(x, fuste.top + 18 * u), Offset(x, fuste.bottom - 14 * u), 1.6f * u, cap = StrokeCap.Round)
                drawLine(c(MARMOL_LUZ), Offset(x + 1.4f * u, fuste.top + 18 * u), Offset(x + 1.4f * u, fuste.bottom - 14 * u), 0.6f * u)
            }
            drawRect(c(MARMOL_LUZ), r.topLeft, Size(r.width, 5 * u))
            drawRect(c(MARMOL), Offset(r.left + 1 * u, r.top + 5 * u), Size(r.width - 2 * u, 7 * u))
            listOf(0.3f, 0.7f).forEach { f -> drawCircle(c(MARMOL_SOMBRA), 2.4f * u, Offset(r.left + r.width * f, r.top + 8.5f * u), style = Stroke(0.9f * u)) }
            drawLine(c(MARMOL_SOMBRA), Offset(fuste.left, r.top + 14 * u), Offset(fuste.right, r.top + 14 * u), 1f * u)
            drawRoundRect(c(MARMOL_LUZ), Offset(r.left + 1 * u, r.bottom - 10 * u), Size(r.width - 2 * u, 4 * u), CornerRadius(2 * u))
            drawRect(c(MARMOL), Offset(r.left, r.bottom - 6 * u), Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(fuste.right - 2 * u, r.top), Size(2 * u, r.height))
        }
        // Las tablas de mármol, con el astrágalo de cuentas bajo el canto.
        d.tablas.forEach { r ->
            drawRect(c(MARMOL), r.topLeft, r.size)
            drawRect(c(MARMOL_LUZ), r.topLeft, Size(r.width, 2.4f * u))
            drawLine(c(MARMOL_SOMBRA), Offset(r.left, r.top + r.height * 0.7f), Offset(r.right, r.top + r.height * 0.7f), 0.8f * u)
            var x = r.left + 3 * u
            while (x < r.right) { drawCircle(c(MARMOL_SOMBRA), 1.1f * u, Offset(x, r.top + r.height * 0.86f)); x += 5 * u }
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El titulus de cada mes: una placa de mármol con doble filete y letras de minio.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(c(MARMOL_LUZ), r.topLeft, r.size)
            drawRect(c(MINIO), r.topLeft, r.size, style = Stroke(1f * u))
            val dentro = r.deflate(2 * u)
            drawRect(c(MINIO).copy(alpha = 0.6f), dentro.topLeft, dentro.size, style = Stroke(0.5f * u))
            textoEn(medidor, p.texto, Rect(r.left + 5 * u, r.top, r.right - 5 * u, r.bottom), letra, c(MINIO), 10f, peso = FontWeight.SemiBold, espaciado = 1f, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // El zócalo negro con sus paneles de filete ocre y un rombo rojo en cada uno.
        val b = d.base
        val piso = b.top + b.height * 0.58f
        drawRect(c(NEGRO), b.topLeft, Size(b.width, piso - b.top))
        drawRect(c(MARMOL_LUZ), b.topLeft, Size(b.width, 3 * u))
        drawRect(c(MARMOL_SOMBRA), Offset(b.left, b.top + 3 * u), Size(b.width, 1.2f * u))
        val paneles = 4
        for (j in 0 until paneles) {
            val pr = Rect(b.left + b.width * j / paneles + 6 * u, b.top + 8 * u, b.left + b.width * (j + 1) / paneles - 6 * u, piso - 5 * u)
            if (pr.height <= 2 * u) continue
            drawRect(c(OCRE).copy(alpha = 0.75f), pr.topLeft, pr.size, style = Stroke(0.9f * u))
            val cc = pr.center
            val rr = minOf(pr.height, pr.width) * 0.3f
            val rombo = Path().apply { moveTo(cc.x, cc.y - rr); lineTo(cc.x + rr * 1.4f, cc.y); lineTo(cc.x, cc.y + rr); lineTo(cc.x - rr * 1.4f, cc.y); close() }
            drawPath(rombo, c(POMPEYANO))
            drawPath(rombo, c(OCRE).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        }
        // El piso de mosaico: teselas blancas con su cenefa negra de cuadros.
        drawRect(c(MARMOL_LUZ), Offset(0f, piso), Size(w, h - piso))
        drawRect(c(NEGRO), Offset(0f, piso + 2 * u), Size(w, 2.4f * u))
        val tesela = 5 * u
        var tx = 0f
        var k = 0
        while (tx < w) {
            if (k % 2 == 0) drawRect(c(NEGRO), Offset(tx, piso + 6 * u), Size(tesela, tesela))
            tx += tesela; k++
        }
        var gy = piso + 6 * u + tesela + 3 * u
        while (gy < h) { drawLine(c(MARMOL_SOMBRA).copy(alpha = 0.4f), Offset(0f, gy), Offset(w, gy), 0.5f * u); gy += 3.5f * u }
    }

    /** La cornisa, el busto sobre su herma, las guirnaldas y la lucerna (encendida de noche). */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(MARMOL), r.topLeft, r.size)
        val cornisa = r.top + r.height * 0.62f
        // La moldura saliente, los dentículos, el friso con rosetas y el arquitrabe de dos fajas.
        drawRect(c(MARMOL_LUZ), Offset(r.left, cornisa), Size(r.width, r.height * 0.07f))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, cornisa + r.height * 0.07f), Size(r.width, 2 * u))
        val dientes = cornisa + r.height * 0.07f + 2 * u
        val altoDiente = r.height * 0.06f
        drawRect(c(MARMOL_SOMBRA), Offset(r.left, dientes), Size(r.width, altoDiente))
        var x = r.left + 2 * u
        while (x < r.right) { drawRect(c(MARMOL), Offset(x, dientes), Size(4 * u, altoDiente)); x += 7 * u }
        val friso = Rect(r.left, dientes + altoDiente, r.right, dientes + altoDiente + r.height * 0.11f)
        drawRect(c(MARMOL), friso.topLeft, friso.size)
        var rx = friso.left + 14 * u
        while (rx < friso.right) {
            drawCircle(c(MARMOL_SOMBRA), friso.height * 0.28f, Offset(rx, friso.center.y), style = Stroke(0.9f * u))
            drawCircle(c(MARMOL_SOMBRA), friso.height * 0.1f, Offset(rx, friso.center.y))
            rx += 28 * u
        }
        val arquitrabe = Rect(r.left, friso.bottom, r.right, r.bottom)
        drawRect(c(MARMOL_LUZ), arquitrabe.topLeft, arquitrabe.size)
        drawLine(c(MARMOL_SOMBRA), Offset(r.left, arquitrabe.top + 0.6f * u), Offset(r.right, arquitrabe.top + 0.6f * u), 1f * u)
        drawLine(c(MARMOL_SOMBRA), Offset(r.left, arquitrabe.center.y), Offset(r.right, arquitrabe.center.y), 1f * u)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom - 2 * u), Size(r.width, 2 * u))
        // Las guirnaldas de laurel colgadas de dos clavos, con sus cintas.
        listOf(0.05f to 0.27f, 0.8f to 0.97f).forEach { (a, z) ->
            val g = Rect(r.left + r.width * a, r.top + r.height * 0.1f, r.left + r.width * z, r.top + r.height * 0.42f)
            drawArc(c(LAUREL), 10f, 160f, false, g.topLeft, g.size, style = Stroke(5 * u, cap = StrokeCap.Round))
            for (j in 0 until 7) {
                val ang = Math.toRadians(25.0 + 21.0 * j)
                drawCircle(c(LAUREL_LUZ), 1.6f * u, Offset(g.center.x + g.width / 2 * cos(ang).toFloat(), g.center.y + g.height / 2 * sin(ang).toFloat()))
            }
            listOf(g.left, g.right).forEach { gx ->
                drawLine(c(MINIO), Offset(gx, g.center.y), Offset(gx - 1.5f * u, g.center.y + 11 * u), 1.2f * u)
                drawCircle(c(BRONCE_OSC), 1.5f * u, Offset(gx, g.center.y))
            }
        }
        // El busto sobre su herma.
        val bx = r.left + r.width * 0.42f
        val pilar = Rect(bx - r.width * 0.035f, cornisa - r.height * 0.2f, bx + r.width * 0.035f, cornisa)
        drawRect(c(MARMOL), pilar.topLeft, pilar.size)
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(pilar.right - pilar.width * 0.3f, pilar.top), Size(pilar.width * 0.3f, pilar.height))
        drawRect(c(MARMOL_LUZ), Offset(pilar.left - 2 * u, pilar.top - 2.4f * u), Size(pilar.width + 4 * u, 2.4f * u))
        val hombros = pilar.top - 2.4f * u
        val anchoBusto = r.width * 0.07f
        val torso = Path().apply {
            moveTo(bx - anchoBusto, hombros)
            quadraticTo(bx - anchoBusto, hombros - r.height * 0.1f, bx - anchoBusto * 0.3f, hombros - r.height * 0.12f)
            lineTo(bx + anchoBusto * 0.3f, hombros - r.height * 0.12f)
            quadraticTo(bx + anchoBusto, hombros - r.height * 0.1f, bx + anchoBusto, hombros)
            close()
        }
        drawRect(c(MARMOL), Offset(bx - anchoBusto * 0.18f, hombros - r.height * 0.16f), Size(anchoBusto * 0.36f, r.height * 0.06f))
        drawPath(torso, c(MARMOL))
        for (j in 0 until 3) {
            drawLine(c(MARMOL_SOMBRA), Offset(bx - anchoBusto * (0.7f - 0.2f * j), hombros - r.height * (0.09f - 0.02f * j)), Offset(bx + anchoBusto * (0.2f + 0.2f * j), hombros - 1 * u), 0.8f * u)
        }
        val cabeza = Rect(bx - anchoBusto * 0.32f, hombros - r.height * 0.31f, bx + anchoBusto * 0.32f, hombros - r.height * 0.14f)
        drawOval(c(MARMOL_LUZ), cabeza.topLeft, cabeza.size)
        drawArc(Color.Black.copy(alpha = 0.12f), -90f, 180f, true, cabeza.topLeft, cabeza.size)
        for (j in 0 until 5) drawCircle(c(MARMOL_SOMBRA), cabeza.width * 0.1f, Offset(cabeza.left + cabeza.width * (0.15f + 0.175f * j), cabeza.top + cabeza.height * 0.14f), style = Stroke(0.7f * u))
        drawLine(c(MARMOL_SOMBRA), Offset(bx + cabeza.width * 0.05f, cabeza.center.y - cabeza.height * 0.05f), Offset(bx + cabeza.width * 0.12f, cabeza.center.y + cabeza.height * 0.15f), 0.8f * u)
        // El candelabro de bronce con la lucerna encima.
        val lx = r.left + r.width * 0.68f
        val plato = cornisa - r.height * 0.26f
        drawLine(c(BRONCE_OSC), Offset(lx, cornisa), Offset(lx, plato), 1.8f * u)
        for (s in listOf(-1f, 1f)) drawLine(c(BRONCE_OSC), Offset(lx, cornisa - 4 * u), Offset(lx + s * 6 * u, cornisa), 1.4f * u)
        drawOval(c(BRONCE), Offset(lx - 8 * u, plato - 1.6f * u), Size(16 * u, 3.2f * u))
        val lucerna = Rect(lx - 9 * u, plato - 8 * u, lx + 5 * u, plato - 1 * u)
        val pico = Path().apply {
            moveTo(lucerna.right - 2 * u, lucerna.top + 2 * u); lineTo(lucerna.right + 6 * u, lucerna.center.y - 1 * u)
            lineTo(lucerna.right + 6 * u, lucerna.center.y + 1.6f * u); lineTo(lucerna.right - 2 * u, lucerna.bottom - 1 * u); close()
        }
        drawPath(pico, c(BRONCE))
        drawOval(c(BRONCE), lucerna.topLeft, lucerna.size)
        drawOval(c(BRONCE_LUZ).copy(alpha = 0.6f), Offset(lucerna.left + lucerna.width * 0.25f, lucerna.top + 1 * u), Size(lucerna.width * 0.4f, lucerna.height * 0.3f))
        drawCircle(c(BRONCE_OSC), 2f * u, Offset(lucerna.left - 1 * u, lucerna.center.y - 1 * u), style = Stroke(1.1f * u))
        drawCircle(c(BRONCE_OSC), 1.3f * u, lucerna.center)
        val boca = Offset(lucerna.right + 5.5f * u, lucerna.center.y - 1 * u)
        if (noche) {
            // La lucerna encendida: el único degradado del mueble.
            mancha(boca, r.height * 0.4f, LLAMA.copy(alpha = 0.45f))
            val llama = Path().apply {
                moveTo(boca.x - 2 * u, boca.y)
                quadraticTo(boca.x - 2.4f * u, boca.y - 4 * u, boca.x + 0.5f * u, boca.y - 9 * u)
                quadraticTo(boca.x + 2.6f * u, boca.y - 4 * u, boca.x + 2 * u, boca.y)
                close()
            }
            drawPath(llama, LLAMA)
            drawCircle(Color.White.copy(alpha = 0.8f), 1f * u, Offset(boca.x, boca.y - 2 * u))
        } else {
            drawLine(c(NEGRO).copy(alpha = 0.6f), boca, Offset(boca.x + 0.5f * u, boca.y - 2.5f * u), 1f * u)
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = PAPIROS[(v.semilla and 0x7fffffff) % PAPIROS.size]
        val papiro = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val asoma = minOf(w * 0.08f, 5f * u)
        val cuerpo = Rect(asoma, 0f, w - asoma, h)
        // La varilla (umbilicus) que atraviesa el rollo y sus perillas en cada punta: de hueso o de ébano.
        val perilla = c(if (azar(v.semilla, 3) > 0.5f) HUESO else EBANO)
        drawRect(c(BRONCE_OSC), Offset(0f, h * 0.5f - 1.2f * u), Size(w, 2.4f * u))
        val altoPerilla = minOf(h * 0.5f, 9f * u)
        listOf(asoma * 0.5f, w - asoma * 0.5f).forEach { px ->
            drawOval(perilla, Offset(px - asoma * 0.5f, h * 0.5f - altoPerilla / 2), Size(asoma, altoPerilla))
            drawOval(Color.White.copy(alpha = 0.25f), Offset(px - asoma * 0.3f, h * 0.5f - altoPerilla * 0.4f), Size(asoma * 0.3f, altoPerilla * 0.35f))
        }
        // El rollo tumbado, con su luz arriba y su sombra abajo (planas: en un nicho caben muchos).
        drawRoundRect(papiro, cuerpo.topLeft, cuerpo.size, CornerRadius(minOf(h * 0.12f, 3 * u)))
        drawRect(Color.White.copy(alpha = 0.16f), Offset(cuerpo.left, h * 0.12f), Size(cuerpo.width, h * 0.16f))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.left, h * 0.74f), Size(cuerpo.width, h * 0.26f))
        // Las fibras y el borde de la última vuelta (dos, si escribiste mucho).
        var y = 2 * u
        while (y < h) { drawLine(c(PAPIRO_OSC).copy(alpha = 0.25f), Offset(cuerpo.left + 1 * u, y), Offset(cuerpo.right - 1 * u, y), 0.5f * u); y += 3 * u }
        val bordes = if (v.lleno > 0.5f) 2 else 1
        for (j in 0 until bordes) {
            val ex = cuerpo.left + cuerpo.width * (0.17f + 0.04f * j)
            drawLine(c(PAPIRO_OSC), Offset(ex, 0f), Offset(ex - 1.5f * u, h), 0.9f * u)
        }
        // Algunos llevan su correa (lorum) de color.
        if (azar(v.semilla, 4) > 0.6f) drawRect(c(PURPURA).copy(alpha = 0.9f), Offset(cuerpo.left + cuerpo.width * 0.08f, 0f), Size(3 * u, h))
        // El mes en tinta, abreviado como en las fechas romanas («OCT.»).
        val mes = if (v.mes.length > 5) v.mes.take(3) + "." else v.mes
        textoEn(medidor, mes, Rect(cuerpo.left + cuerpo.width * 0.22f, h * 0.22f, cuerpo.left + cuerpo.width * 0.58f, h * 0.78f), letra, c(TINTA).copy(alpha = 0.85f), 9f, espaciado = 0.5f, minimo = 4f)
        // El sillybos: la etiqueta de pergamino rojo que cuelga con el día, un poco ladeada.
        val etiqueta = Rect(cuerpo.left + cuerpo.width * 0.62f, h * 0.2f, cuerpo.right - cuerpo.width * 0.05f, h * 0.94f)
        val amarre = Offset(cuerpo.right - 1 * u, h * 0.44f)
        drawLine(c(TINTA).copy(alpha = 0.7f), amarre, Offset(etiqueta.center.x, etiqueta.top + 1 * u), 0.7f * u)
        rotate((azar(v.semilla, 5) - 0.5f) * 8f, Offset(etiqueta.center.x, etiqueta.top)) {
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(etiqueta.left + 1 * u, etiqueta.top + 1 * u), etiqueta.size)
            drawRect(c(MINIO), etiqueta.topLeft, etiqueta.size)
            val filete = etiqueta.deflate(1.2f * u)
            drawRect(c(HUESO).copy(alpha = 0.5f), filete.topLeft, filete.size, style = Stroke(0.5f * u))
            textoEn(medidor, v.dia, etiqueta.deflate(1.5f * u), letra, c(HUESO), 12f, peso = FontWeight.SemiBold, minimo = 5f)
        }
        // El sello de plomo en el hilo; de noche, una lunita.
        val sello = Offset((amarre.x + etiqueta.center.x) / 2, (amarre.y + etiqueta.top) / 2)
        if (v.noche) {
            drawCircle(LUNA, 2.2f * u, sello)
            drawCircle(papiro, 1.8f * u, Offset(sello.x + 1.1f * u, sello.y - 0.7f * u))
        } else {
            drawCircle(c(PLOMO), 1.8f * u, sello)
            drawCircle(Color.White.copy(alpha = 0.3f), 0.6f * u, Offset(sello.x - 0.5f * u, sello.y - 0.5f * u))
        }
    }
}


/**
 * Grecia: el estante jónico. El muro pintado de rojo tierra, las columnas de los costados con su fuste estriado, su capitel
 * de dos volutas y su basa de toros, y las tablas de mármol con el cimacio de óvulos y dardos en el canto; el mes va en una
 * placa de mármol con filetes azules. Arriba, el entablamento: cornisa con dentículos, el friso con el meandro y el
 * arquitrabe de tres fajas; encima, una kylix de barniz negro con su banda de palmetas en rojo y una lámpara de aceite.
 * Abajo, un escalón de mármol sobre el piso de baldosas. Cada entrada es un pinax: un díptico de madera con la cera oscura
 * hundida, el día y el mes incisos con el estilo, el cordón que une las hojas y, al pie, un ramito de olivo inciso (de
 * noche, una luna); se ven más hojas por el canto cuanto más escribiste. De noche se enciende la lámpara y llega la lechuza
 * con sus ojos. Distinto del Ideario, que es la stoa dórica con sus ánforas y sus óstraka.
 */
object EstanteHeleno : DisenoBiblioteca {
    private val MARMOL = Color(0xFFEEE8DA)
    private val MARMOL_LUZ = Color(0xFFFAF6EC)
    private val MARMOL_OSC = Color(0xFFCBC1AB)
    private val MARMOL_SOMBRA = Color(0xFF9E947E)
    private val MURO = Color(0xFF8C3F2C)
    private val MURO_OSC = Color(0xFF6E2F20)
    private val EGEO = Color(0xFF2E5E8E)
    private val TERRACOTA = Color(0xFFC2652F)
    private val BARNIZ = Color(0xFF1A1410)
    private val BALDOSA = Color(0xFFB07A52)
    private val CERA = Color(0xFF2B2420)
    private val INCISO = Color(0xFFD9C9A6)
    private val CORDON = Color(0xFFB89A5A)
    private val LLAMA = Color(0xFFF2C46A)
    private val PLUMA = Color(0xFF6B5843)
    private val PLUMA_LUZ = Color(0xFFA89276)
    private val OJO = Color(0xFFF2C94A)
    private val PICO = Color(0xFFB08A3A)
    /** Las maderas de los pinakes: boj, tilo, olivo y nogal. */
    private val MADERAS = listOf(Color(0xFF9C7A4E), Color(0xFF86643C), Color(0xFFAE8A5C), Color(0xFF73552F), Color(0xFF957048))
    private val NOCHE = Color(0xFF141A30)
    private val LUNA = Color(0xFFEDE6C8)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo el cielo del Ática. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0F0C0A), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.5f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.07f
    override val altoRepisa: Dp = 104.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = Letra.garamond

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.72f + 0.2f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro pintado, con las manchas del revoque.
        val cuerpo = Rect(0f, d.corona.bottom, w, d.base.top)
        drawRect(c(MURO), cuerpo.topLeft, cuerpo.size)
        for (j in 0 until 10) {
            drawOval(c(MURO_OSC).copy(alpha = 0.35f), Offset(w * azar(j, 61), cuerpo.top + cuerpo.height * azar(j, 62)),
                Size((14 + 20 * azar(j, 63)) * u, (6 + 8 * azar(j, 64)) * u))
        }
        // Cada repisa: la sombra de la tabla de arriba y de las columnas.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.32f), r.topLeft, Size(r.width, 8 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left, r.top + 8 * u), Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.18f), r.topLeft, Size(5 * u, r.height))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(r.right - 4 * u, r.top), Size(4 * u, r.height))
        }
        // Las columnas jónicas de los costados.
        listOf(d.izquierda, d.derecha).forEach { r ->
            val fuste = Rect(r.left + r.width * 0.16f, r.top + r.width * 0.55f, r.right - r.width * 0.16f, r.bottom - r.width * 0.4f)
            drawRect(c(MARMOL), fuste.topLeft, fuste.size)
            for (j in 1..4) {
                val x = fuste.left + fuste.width * j / 5f
                drawLine(c(MARMOL_OSC), Offset(x, fuste.top), Offset(x, fuste.bottom), 1.4f * u)
                drawLine(c(MARMOL_LUZ), Offset(x + 1.2f * u, fuste.top), Offset(x + 1.2f * u, fuste.bottom), 0.6f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(fuste.right - fuste.width * 0.2f, fuste.top), Size(fuste.width * 0.2f, fuste.height))
            // El capitel: el ábaco, el canal, las dos volutas y los óvulos del equino.
            val capitel = Rect(r.left, r.top, r.right, fuste.top)
            drawRect(c(MARMOL_LUZ), capitel.topLeft, Size(capitel.width, capitel.height * 0.25f))
            drawRect(c(MARMOL), Offset(capitel.left, capitel.top + capitel.height * 0.25f), Size(capitel.width, capitel.height * 0.5f))
            val rv = capitel.height * 0.32f
            listOf(capitel.left + rv, capitel.right - rv).forEach { vx ->
                val vc = Offset(vx, capitel.top + capitel.height * 0.58f)
                drawCircle(c(MARMOL), rv, vc)
                drawCircle(c(MARMOL_SOMBRA), rv, vc, style = Stroke(1f * u))
                drawCircle(c(MARMOL_SOMBRA), rv * 0.62f, vc, style = Stroke(0.8f * u))
                drawCircle(c(MARMOL_SOMBRA), rv * 0.26f, vc)
            }
            var ox = capitel.left + rv * 2.4f
            while (ox < capitel.right - rv * 2.2f) {
                drawOval(c(MARMOL_OSC), Offset(ox - 1.5f * u, capitel.top + capitel.height * 0.6f), Size(3 * u, capitel.height * 0.3f))
                ox += 4.5f * u
            }
            // La basa: dos toros, el de abajo más ancho.
            val alturaBasa = r.bottom - fuste.bottom
            drawRoundRect(c(MARMOL_LUZ), Offset(r.left + r.width * 0.08f, fuste.bottom), Size(r.width * 0.84f, alturaBasa * 0.45f), CornerRadius(3 * u))
            drawRoundRect(c(MARMOL), Offset(r.left, fuste.bottom + alturaBasa * 0.45f), Size(r.width, alturaBasa * 0.55f), CornerRadius(3 * u))
            drawLine(c(MARMOL_SOMBRA), Offset(r.left, fuste.bottom + alturaBasa * 0.45f), Offset(r.right, fuste.bottom + alturaBasa * 0.45f), 0.8f * u)
        }
        // Las tablas de mármol con su cimacio de óvulos y dardos.
        d.tablas.forEach { r ->
            drawRect(c(MARMOL), r.topLeft, r.size)
            drawRect(c(MARMOL_LUZ), r.topLeft, Size(r.width, 2.2f * u))
            val oy = r.top + r.height * 0.58f
            var ox = r.left + 4 * u
            while (ox < r.right - 2 * u) {
                drawOval(c(MARMOL_OSC), Offset(ox - 2.2f * u, oy - r.height * 0.26f), Size(4.4f * u, r.height * 0.52f))
                drawOval(c(MARMOL_LUZ), Offset(ox - 1.2f * u, oy - r.height * 0.18f), Size(2.4f * u, r.height * 0.36f))
                drawLine(c(MARMOL_SOMBRA), Offset(ox + 3.8f * u, oy - r.height * 0.22f), Offset(ox + 3.8f * u, oy + r.height * 0.22f), 0.8f * u)
                ox += 7.6f * u
            }
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3.5f * u))
        }
        // El rótulo de cada mes: una placa de mármol con dos filetes azules.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(c(MARMOL_LUZ), r.topLeft, r.size)
            drawRect(c(MARMOL_SOMBRA), r.topLeft, r.size, style = Stroke(0.8f * u))
            drawLine(c(EGEO), Offset(r.left + 2 * u, r.top + 1.6f * u), Offset(r.right - 2 * u, r.top + 1.6f * u), 0.8f * u)
            drawLine(c(EGEO), Offset(r.left + 2 * u, r.bottom - 1.6f * u), Offset(r.right - 2 * u, r.bottom - 1.6f * u), 0.8f * u)
            textoEn(medidor, p.texto, Rect(r.left + 4 * u, r.top + 2 * u, r.right - 4 * u, r.bottom - 2 * u), letra, c(BARNIZ), 10.5f, peso = FontWeight.Medium, espaciado = 1f, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el escalón de mármol, con sus juntas, y el piso de baldosas de barro.
        val b = d.base
        val escalon = Rect(b.left, b.top, b.right, b.top + b.height * 0.5f)
        drawRect(c(MARMOL), escalon.topLeft, escalon.size)
        vetas(escalon, c(MARMOL_OSC).copy(alpha = 0.3f), 71, densidad = 0.25f, grosor = 0.6f, ondulacion = 3f)
        drawRect(c(MARMOL_LUZ), escalon.topLeft, Size(escalon.width, 2.4f * u))
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(escalon.left, escalon.bottom - escalon.height * 0.3f), Size(escalon.width, escalon.height * 0.3f))
        var jx = escalon.left + 30 * u
        while (jx < escalon.right) { drawLine(c(MARMOL_SOMBRA), Offset(jx, escalon.top + 2.4f * u), Offset(jx, escalon.bottom), 0.8f * u); jx += 54 * u }
        val piso = escalon.bottom
        drawRect(c(BALDOSA), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, piso), Size(w, 3 * u))
        var bx = 0f
        while (bx < w) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(bx, piso), Offset(bx, h), 0.8f * u); bx += 24 * u }
        var by = piso + 10 * u
        while (by < h) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(0f, by), Offset(w, by), 0.8f * u); by += 12 * u }
    }

    /** El meandro: un gancho cuadrado por paso, entre dos líneas que lo enmarcan. */
    private fun DrawScope.meandro(m: Rect, color: Color) {
        val u = density
        val s = m.height
        val p = Path()
        p.moveTo(m.left, m.bottom); p.lineTo(m.right, m.bottom)
        p.moveTo(m.left, m.top); p.lineTo(m.right, m.top)
        var x = m.left + s * 0.15f
        while (x + s <= m.right) {
            p.moveTo(x, m.bottom)
            p.lineTo(x, m.top + s * 0.2f)
            p.lineTo(x + s * 0.8f, m.top + s * 0.2f)
            p.lineTo(x + s * 0.8f, m.top + s * 0.75f)
            p.lineTo(x + s * 0.35f, m.top + s * 0.75f)
            p.lineTo(x + s * 0.35f, m.top + s * 0.45f)
            p.lineTo(x + s * 0.55f, m.top + s * 0.45f)
            x += s * 1.1f
        }
        drawPath(p, color, style = Stroke(maxOf(1f * u, s * 0.11f)))
    }

    /** El entablamento con el meandro, la kylix, la lámpara (encendida de noche) y, de noche, la lechuza. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(MURO), r.topLeft, r.size)
        val cornisa = r.top + r.height * 0.5f
        drawRect(c(MARMOL_LUZ), Offset(r.left, cornisa), Size(r.width, r.height * 0.06f))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, cornisa + r.height * 0.06f), Size(r.width, 1.6f * u))
        val dientes = cornisa + r.height * 0.06f + 1.6f * u
        val altoDiente = r.height * 0.06f
        drawRect(c(MARMOL_SOMBRA), Offset(r.left, dientes), Size(r.width, altoDiente))
        var x = r.left + 1.5f * u
        while (x < r.right) { drawRect(c(MARMOL), Offset(x, dientes), Size(4 * u, altoDiente)); x += 6.5f * u }
        // El friso con el meandro.
        val friso = Rect(r.left, dientes + altoDiente, r.right, dientes + altoDiente + r.height * 0.16f)
        drawRect(c(MARMOL_LUZ), friso.topLeft, friso.size)
        meandro(friso.deflate(friso.height * 0.16f), c(EGEO))
        // El arquitrabe de tres fajas.
        val arq = Rect(r.left, friso.bottom, r.right, r.bottom)
        for (j in 0 until 3) {
            val y0 = arq.top + arq.height * j / 3f
            drawRect(c(if (j % 2 == 0) MARMOL else MARMOL_LUZ), Offset(arq.left, y0), Size(arq.width, arq.height / 3f))
            drawLine(c(MARMOL_SOMBRA), Offset(arq.left, y0), Offset(arq.right, y0), 0.7f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom - 2 * u), Size(r.width, 2 * u))
        // La kylix: el pie, el tallo y la copa de barniz negro con su banda de palmetas rojas.
        val kx = r.left + r.width * 0.3f
        val kw = r.width * 0.16f
        val kh = r.height * 0.13f
        val pieTop = cornisa - r.height * 0.07f
        drawOval(c(BARNIZ), Offset(kx - kw * 0.22f, cornisa - r.height * 0.025f), Size(kw * 0.44f, r.height * 0.025f))
        drawRect(c(BARNIZ), Offset(kx - kw * 0.05f, pieTop), Size(kw * 0.1f, cornisa - pieTop - r.height * 0.012f))
        val labio = pieTop - kh
        val copa = Rect(kx - kw / 2, labio - kh, kx + kw / 2, labio + kh)
        listOf(-1f, 1f).forEach { s ->
            drawOval(c(BARNIZ), Offset(kx + s * kw * 0.5f - kw * 0.08f, labio + kh * 0.05f), Size(kw * 0.16f, kh * 0.32f), style = Stroke(1.4f * u))
        }
        drawArc(c(BARNIZ), 0f, 180f, true, copa.topLeft, copa.size)
        val cuenco = Path().apply { arcTo(copa, 0f, 180f, false); close() }
        clipPath(cuenco) {
            drawRect(c(TERRACOTA), Offset(copa.left, labio + kh * 0.18f), Size(copa.width, kh * 0.45f))
            var px = copa.left + kw * 0.12f
            while (px < copa.right) {
                val pb = Offset(px, labio + kh * 0.6f)
                for (a in -2..2) drawLine(c(BARNIZ), pb, Offset(pb.x + a * kh * 0.09f, pb.y - kh * 0.36f), 0.7f * u)
                px += kw * 0.2f
            }
        }
        drawLine(c(TERRACOTA), Offset(copa.left, labio), Offset(copa.right, labio), 1f * u)
        // La lámpara de aceite: barro con barniz arriba, su pico y su asa.
        val lx = r.left + r.width * 0.6f
        val lampara = Rect(lx - 8 * u, cornisa - 7 * u, lx + 6 * u, cornisa)
        val pico = Path().apply {
            moveTo(lampara.right - 2 * u, cornisa - 5.5f * u); lineTo(lampara.right + 7 * u, cornisa - 4.5f * u)
            lineTo(lampara.right + 7 * u, cornisa - 2 * u); lineTo(lampara.right - 2 * u, cornisa - 1 * u); close()
        }
        drawPath(pico, c(BARNIZ))
        drawOval(c(TERRACOTA), lampara.topLeft, lampara.size)
        drawArc(c(BARNIZ), 180f, 180f, true, lampara.topLeft, lampara.size)
        drawCircle(c(TERRACOTA), 1.4f * u, Offset(lx - 1 * u, cornisa - 5.5f * u))
        drawArc(c(BARNIZ), 90f, 180f, false, Offset(lampara.left - 3 * u, cornisa - 6 * u), Size(5 * u, 5 * u), style = Stroke(1.2f * u))
        val boca = Offset(lampara.right + 6.5f * u, cornisa - 4.5f * u)
        if (noche) {
            // La lámpara encendida: el único degradado del mueble.
            mancha(boca, r.height * 0.35f, LLAMA.copy(alpha = 0.4f))
            val llama = Path().apply {
                moveTo(boca.x - 1.8f * u, boca.y)
                quadraticTo(boca.x - 2.2f * u, boca.y - 4 * u, boca.x + 0.4f * u, boca.y - 8 * u)
                quadraticTo(boca.x + 2.4f * u, boca.y - 4 * u, boca.x + 1.8f * u, boca.y)
                close()
            }
            drawPath(llama, LLAMA)
            lechuza(Offset(r.left + r.width * 0.85f, cornisa), r.height * 0.3f)
        } else {
            drawLine(c(BARNIZ), boca, Offset(boca.x + 0.4f * u, boca.y - 2.4f * u), 1f * u)
        }
    }

    /** La lechuza posada en la cornisa, con sus ojos encendidos (sale de noche). */
    private fun DrawScope.lechuza(pie: Offset, alto: Float) {
        val u = density
        val pluma = mezcla(PLUMA, NOCHE, 0.3f)
        val claro = mezcla(PLUMA_LUZ, NOCHE, 0.35f)
        drawOval(pluma, Offset(pie.x - alto * 0.24f, pie.y - alto * 0.72f), Size(alto * 0.48f, alto * 0.72f))
        // Las alas plegadas y el pecho moteado.
        listOf(-1f, 1f).forEach { s ->
            drawArc(claro, if (s < 0) 100f else -20f, 100f, false, Offset(pie.x - alto * 0.2f, pie.y - alto * 0.6f), Size(alto * 0.4f, alto * 0.55f), style = Stroke(1f * u))
        }
        for (j in 0 until 6) drawCircle(claro, 0.9f * u, Offset(pie.x + (j % 3 - 1) * alto * 0.08f, pie.y - alto * (0.22f + 0.1f * (j / 3))))
        // La cabeza redonda, sin penachos, con sus ojos grandes.
        val cabeza = Rect(pie.x - alto * 0.22f, pie.y - alto * 1.0f, pie.x + alto * 0.22f, pie.y - alto * 0.6f)
        drawOval(pluma, cabeza.topLeft, cabeza.size)
        listOf(-1f, 1f).forEach { s ->
            val o = Offset(pie.x + s * alto * 0.1f, cabeza.center.y)
            drawCircle(claro, alto * 0.1f, o)
            drawCircle(OJO, alto * 0.072f, o)
            drawCircle(BARNIZ, alto * 0.034f, o)
            drawCircle(Color.White, alto * 0.014f, Offset(o.x - alto * 0.015f, o.y - alto * 0.02f))
        }
        val pico = Path().apply {
            moveTo(pie.x - alto * 0.03f, cabeza.center.y + alto * 0.06f); lineTo(pie.x + alto * 0.03f, cabeza.center.y + alto * 0.06f)
            lineTo(pie.x, cabeza.center.y + alto * 0.13f); close()
        }
        drawPath(pico, PICO)
        listOf(-1f, 1f).forEach { s -> drawLine(PICO, Offset(pie.x + s * alto * 0.07f, pie.y - 2 * u), Offset(pie.x + s * alto * 0.09f, pie.y), 1.2f * u) }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = MADERAS[(v.semilla and 0x7fffffff) % MADERAS.size]
        val madera = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        drawRoundRect(madera, Offset.Zero, size, CornerRadius(1.2f * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset.Zero, Size(w * 0.1f, h))
        // Las hojas del díptico vistas por el canto derecho: más cuanto más escribiste.
        val hojas = 2 + (v.lleno * 3).toInt()
        val canto = minOf(w * 0.22f, hojas * 2.2f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(w - canto, 0f), Size(canto, h))
        for (j in 1 until hojas) {
            val x = w - canto + canto * j / hojas
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(x, 0f), Offset(x, h), 0.6f * u)
        }
        // La cera oscura, hundida en el marco de madera.
        val cera = Rect(w * 0.16f, h * 0.06f, w - canto - w * 0.06f, h * 0.94f)
        drawRect(c(CERA), cera.topLeft, cera.size)
        drawRect(Color.Black.copy(alpha = 0.35f), cera.topLeft, Size(cera.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.2f), cera.topLeft, Size(1.6f * u, cera.height))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(cera.left, cera.bottom - 1.2f * u), Size(cera.width, 1.2f * u))
        // El día y el mes incisos con el estilo.
        textoEn(medidor, v.dia, Rect(cera.left, cera.top + 2 * u, cera.right, cera.top + cera.height * 0.26f), letra, c(INCISO), 15f, peso = FontWeight.SemiBold)
        textoEn(medidor, v.mes, Rect(cera.left + 1 * u, cera.top + cera.height * 0.29f, cera.right - 1 * u, cera.top + cera.height * 0.78f), letra, c(INCISO).copy(alpha = 0.85f), 9f, vertical = true, minimo = 4.5f)
        // Al pie, un ramito de olivo inciso; de noche, una luna.
        val cr = Offset(cera.center.x, cera.top + cera.height * 0.88f)
        val tam = minOf(cera.width * 0.32f, cera.height * 0.06f)
        if (v.noche) {
            drawCircle(LUNA, tam, cr)
            drawCircle(c(CERA), tam * 0.82f, Offset(cr.x + tam * 0.4f, cr.y - tam * 0.25f))
        } else {
            drawLine(c(INCISO).copy(alpha = 0.8f), Offset(cr.x - tam * 1.2f, cr.y + tam * 0.6f), Offset(cr.x + tam * 1.2f, cr.y - tam * 0.6f), 0.7f * u)
            for (j in 0 until 4) {
                val f = -0.8f + 0.55f * j
                val o = Offset(cr.x + tam * f, cr.y - tam * f * 0.5f)
                val lado0 = if (j % 2 == 0) -1f else 1f
                drawLine(c(INCISO).copy(alpha = 0.8f), o, Offset(o.x + tam * 0.25f, o.y + lado0 * tam * 0.6f), 1.4f * u, cap = StrokeCap.Round)
            }
        }
        // El cordón que pasa por los dos agujeros del canto izquierdo y cuelga con su nudo.
        val hx = w * 0.08f
        val a1 = Offset(hx, h * 0.3f); val a2 = Offset(hx, h * 0.7f)
        drawCircle(Color.Black.copy(alpha = 0.5f), 1.1f * u, a1)
        drawCircle(Color.Black.copy(alpha = 0.5f), 1.1f * u, a2)
        drawLine(c(CORDON), a1, a2, 1f * u)
        drawLine(c(CORDON), a2, Offset(hx + 1.5f * u, h * 0.8f), 1f * u)
        drawCircle(c(CORDON), 1.4f * u, Offset(hx + 1.5f * u, h * 0.8f))
    }
}


/**
 * Egipto: las hornacinas en la pared de arenisca del templo. Cada repisa es una hornacina honda con su marco claro; las tablas
 * de piedra llevan la banda de rectángulos azules, rojos y verdes, y el mes va en una etiqueta de lino con marco de lapislázuli.
 * A los lados, columnas lotiformes: el haz de tallos atado bajo un capitel de loto cerrado con sus sépalos verdes. Arriba,
 * el techo, el friso de uraeus (cobras erguidas con su disco) sobre la gola de hojas pintadas, el toro atado con su cinta y
 * el arquitrabe; abajo, la banda de lotos abiertos y capullos y el piso de losas. Cada entrada es un papiro de pie, con sus
 * vueltas a la vista arriba (más cuanto más escribiste), atado con lino y sellado con barro, el día dentro de un cartucho y
 * el mes debajo; de noche, una estrella de Nut en lugar del sello. De noche el techo se vuelve el cuerpo de Nut, azul con
 * estrellas amarillas. Distinto del Ideario, que es la Casa de la Vida con el friso jekeru, el disco alado y los cofres de cedro.
 */
object EstanteEgipto : DisenoBiblioteca {
    private val ARENISCA = Color(0xFFD9C08E)
    private val ARENISCA_LUZ = Color(0xFFEBD8AE)
    private val ARENISCA_OSC = Color(0xFFA8895A)
    private val HONDO = Color(0xFF7A5E3A)
    private val LAPIS = Color(0xFF1D3A8A)
    private val ROJO = Color(0xFFB0432A)
    private val VERDE = Color(0xFF3E7A4A)
    private val ORO = Color(0xFFD4AF37)
    private val AMARILLO = Color(0xFFF2CF4A)
    private val NEGRO = Color(0xFF1E160C)
    /** Los papiros de los rollos, del recién cortado al viejo. */
    private val PAPIROS = listOf(Color(0xFFE8D7A6), Color(0xFFDCC796), Color(0xFFE2CFA0), Color(0xFFD4BC88), Color(0xFFE6D2A2))
    private val PAPIRO_OSC = Color(0xFFAA905C)
    private val LINO = Color(0xFFE6D8B4)
    private val LINO_OSC = Color(0xFFBFAD84)
    private val BARRO = Color(0xFF7A5232)
    private val NUT = Color(0xFF14275C)
    private val NOCHE = Color(0xFF101833)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luz azul del cielo de Nut. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF110D08), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.48f)
        return r
    }

    override val corona = 0.32f
    override val base = 0.12f
    override val lado = 0.075f
    override val altoRepisa: Dp = 110.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = Letra.messiri

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 40).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.78f + 0.17f * azar(semilla, 1)

    /** La banda de rectángulos de colores separados por líneas claras, del borde de las pinturas. */
    private fun DrawScope.bandaColores(b: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        val colores = listOf(LAPIS, ROJO, VERDE, ROJO)
        val paso = 7 * u
        var x = b.left
        var k = 0
        while (x < b.right) {
            drawRect(t(colores[k % colores.size], oscuro, noche), Offset(x, b.top), Size(minOf(paso - 1.4f * u, b.right - x), b.height))
            x += paso; k++
        }
        drawLine(t(NEGRO, oscuro, noche), Offset(b.left, b.top), Offset(b.right, b.top), 0.6f * u)
        drawLine(t(NEGRO, oscuro, noche), Offset(b.left, b.bottom), Offset(b.right, b.bottom), 0.6f * u)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // La pared del templo: sillares de arenisca.
        val cuerpo = Rect(0f, d.corona.bottom, w, d.base.top)
        drawRect(c(ARENISCA), cuerpo.topLeft, cuerpo.size)
        var y = cuerpo.top
        var fila = 0
        while (y < cuerpo.bottom) {
            drawLine(c(ARENISCA_OSC).copy(alpha = 0.5f), Offset(0f, y), Offset(w, y), 0.8f * u)
            var x = if (fila % 2 == 0) 0f else 26 * u
            while (x < w) { drawLine(c(ARENISCA_OSC).copy(alpha = 0.5f), Offset(x, y), Offset(x, minOf(y + 24 * u, cuerpo.bottom)), 0.8f * u); x += 52 * u }
            y += 24 * u; fila++
        }
        // Cada repisa es una hornacina honda, con su marco más claro y sus sombras.
        d.repisas.forEach { r ->
            val marco = r.inflate(2.5f * u)
            drawRect(c(ARENISCA_LUZ), marco.topLeft, marco.size)
            drawRect(c(HONDO), r.topLeft, r.size)
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), r.topLeft, Size(r.width, 8 * u))
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(r.left, r.top + 8 * u), Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), r.topLeft, Size(6 * u, r.height))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(r.right - 3 * u, r.top), Size(3 * u, r.height))
        }
        // Las columnas lotiformes de los costados.
        listOf(d.izquierda, d.derecha).forEach { r ->
            val capH = r.width * 1.1f
            val fuste = Rect(r.left + r.width * 0.2f, r.top + capH, r.right - r.width * 0.2f, r.bottom - r.width * 0.25f)
            drawRect(c(ARENISCA_LUZ), fuste.topLeft, fuste.size)
            for (j in 1..3) {
                val x = fuste.left + fuste.width * j / 4f
                drawLine(c(ARENISCA_OSC), Offset(x, fuste.top), Offset(x, fuste.bottom), 1f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(fuste.right - fuste.width * 0.22f, fuste.top), Size(fuste.width * 0.22f, fuste.height))
            // Las ataduras del haz bajo el capitel.
            for (j in 0 until 5) drawRect(c(if (j % 2 == 0) ROJO else LAPIS), Offset(fuste.left, fuste.top + j * 2.2f * u), Size(fuste.width, 1.4f * u))
            // El capitel de loto cerrado, con sus sépalos verdes.
            val capitel = Path().apply {
                moveTo(fuste.left, fuste.top)
                cubicTo(r.left - r.width * 0.05f, r.top + capH * 0.6f, r.left + r.width * 0.1f, r.top + capH * 0.15f, r.left + r.width * 0.3f, r.top + capH * 0.1f)
                lineTo(r.right - r.width * 0.3f, r.top + capH * 0.1f)
                cubicTo(r.right - r.width * 0.1f, r.top + capH * 0.15f, r.right + r.width * 0.05f, r.top + capH * 0.6f, fuste.right, fuste.top)
                close()
            }
            drawPath(capitel, c(ARENISCA_LUZ))
            clipPath(capitel) {
                for (j in 0 until 3) {
                    val sx = r.left + r.width * (0.3f + 0.2f * j)
                    val sepalo = Path().apply { moveTo(sx - r.width * 0.12f, fuste.top); lineTo(sx, r.top + capH * 0.2f); lineTo(sx + r.width * 0.12f, fuste.top); close() }
                    drawPath(sepalo, c(VERDE))
                }
            }
            drawPath(capitel, c(ARENISCA_OSC), style = Stroke(0.8f * u))
            drawRect(c(ARENISCA), Offset(r.left + r.width * 0.15f, r.top), Size(r.width * 0.7f, capH * 0.1f))
            // La basa: un disco de piedra.
            drawRoundRect(c(ARENISCA_LUZ), Offset(r.left, fuste.bottom), Size(r.width, r.bottom - fuste.bottom), CornerRadius(3 * u))
            drawLine(c(ARENISCA_OSC), Offset(r.left, fuste.bottom), Offset(r.right, fuste.bottom), 0.8f * u)
        }
        // Las tablas de piedra con su banda de colores.
        d.tablas.forEach { r ->
            drawRect(c(ARENISCA_LUZ), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.2f), r.topLeft, Size(r.width, 2 * u))
            bandaColores(Rect(r.left, r.top + r.height * 0.4f, r.right, r.top + r.height * 0.72f), oscuro, noche)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
        }
        // El rótulo de cada mes: una etiqueta de lino con marco de lapislázuli y filete rojo.
        d.placas.forEach { p ->
            val r = p.r
            val marco = r.inflate(1.5f * u)
            drawRect(c(LAPIS), marco.topLeft, marco.size)
            drawRect(c(LINO), r.topLeft, r.size)
            val filete = r.deflate(1.5f * u)
            drawRect(c(ROJO), filete.topLeft, filete.size, style = Stroke(0.6f * u))
            textoEn(medidor, p.texto, Rect(r.left + 4 * u, r.top + 1 * u, r.right - 4 * u, r.bottom - 1 * u), letra, c(NEGRO), 10.5f, peso = FontWeight.Medium, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: la banda de lotos abiertos y capullos sobre su línea de agua, y el piso de losas.
        val b = d.base
        val piso = b.top + b.height * 0.7f
        drawRect(c(ARENISCA_LUZ), b.topLeft, Size(b.width, piso - b.top))
        bandaColores(Rect(b.left, b.top, b.right, b.top + 3 * u), oscuro, noche)
        val agua = piso - (piso - b.top) * 0.14f
        drawRect(c(LAPIS), Offset(b.left, agua), Size(b.width, piso - agua))
        val altoLoto = agua - (b.top + 6 * u)
        var lx = b.left + 8 * u
        var k = 0
        while (lx < b.right) {
            val tallo = Offset(lx, agua - altoLoto * 0.42f)
            drawLine(c(VERDE), Offset(lx, agua), tallo, 1.2f * u)
            if (k % 2 == 0) {
                val ab = altoLoto * 0.58f
                listOf(-1f to VERDE, 1f to VERDE, 0f to LAPIS).forEach { (s, col) ->
                    val alto0 = if (s == 0f) 1f else 0.8f
                    val petalo = Path().apply {
                        moveTo(tallo.x, tallo.y)
                        lineTo(tallo.x + s * ab * 0.55f - ab * 0.14f, tallo.y - ab * alto0)
                        lineTo(tallo.x + s * ab * 0.55f + ab * 0.14f, tallo.y - ab * alto0)
                        close()
                    }
                    drawPath(petalo, c(col))
                }
                drawCircle(c(ROJO), 1f * u, Offset(tallo.x, tallo.y - ab))
            } else {
                val capullo = Path().apply {
                    moveTo(tallo.x - 3 * u, tallo.y)
                    quadraticTo(tallo.x - 3.5f * u, tallo.y - altoLoto * 0.3f, tallo.x, tallo.y - altoLoto * 0.5f)
                    quadraticTo(tallo.x + 3.5f * u, tallo.y - altoLoto * 0.3f, tallo.x + 3 * u, tallo.y)
                    close()
                }
                drawPath(capullo, c(ROJO))
                val sepalo = Path().apply { moveTo(tallo.x - 3 * u, tallo.y); lineTo(tallo.x, tallo.y - altoLoto * 0.3f); lineTo(tallo.x + 3 * u, tallo.y); close() }
                drawPath(sepalo, c(VERDE))
            }
            lx += 14 * u; k++
        }
        drawRect(c(ARENISCA_OSC), Offset(0f, piso), Size(w, h - piso))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, piso), Size(w, 2.5f * u))
        var sx = 0f
        var j2 = 0
        while (sx < w) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(sx, piso), Offset(sx - 6 * u, h), 0.8f * u); sx += (40 + 12 * azar(j2, 5)) * u; j2++ }
    }

    /** Una cobra erguida (uraeus) con el disco solar sobre la cabeza. */
    private fun DrawScope.uraeus(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val a = alto * 0.9f
        drawOval(c(ORO), Offset(pie.x - a * 0.2f, pie.y - a * 0.08f), Size(a * 0.4f, a * 0.08f))
        val capucha = Path().apply {
            moveTo(pie.x - a * 0.16f, pie.y)
            quadraticTo(pie.x - a * 0.26f, pie.y - a * 0.45f, pie.x - a * 0.08f, pie.y - a * 0.68f)
            lineTo(pie.x + a * 0.08f, pie.y - a * 0.68f)
            quadraticTo(pie.x + a * 0.26f, pie.y - a * 0.45f, pie.x + a * 0.16f, pie.y)
            close()
        }
        drawPath(capucha, c(ORO))
        drawLine(c(LAPIS), Offset(pie.x, pie.y - a * 0.1f), Offset(pie.x, pie.y - a * 0.6f), 1.2f * u)
        for (j in 0 until 3) drawLine(c(ROJO), Offset(pie.x - a * 0.1f, pie.y - a * (0.2f + 0.14f * j)), Offset(pie.x + a * 0.1f, pie.y - a * (0.2f + 0.14f * j)), 0.7f * u)
        drawOval(c(ORO), Offset(pie.x - a * 0.07f, pie.y - a * 0.78f), Size(a * 0.14f, a * 0.12f))
        drawCircle(c(ROJO), a * 0.1f, Offset(pie.x, pie.y - a * 0.9f))
        drawCircle(c(ORO), a * 0.1f, Offset(pie.x, pie.y - a * 0.9f), style = Stroke(0.6f * u))
    }

    /** El techo (de noche, Nut con sus estrellas), el friso de uraeus, la gola, el toro y el arquitrabe. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val techo = Rect(r.left, r.top, r.right, r.top + r.height * 0.24f)
        if (noche) {
            // El cuerpo de Nut: azul, con filas de estrellas amarillas de cinco puntas.
            drawRect(NUT, techo.topLeft, techo.size)
            for (f in 0 until 2) {
                var sx = techo.left + 10 * u + (if (f % 2 == 1) 13 * u else 0f)
                while (sx < techo.right) {
                    drawPath(estrella(Offset(sx, techo.top + techo.height * (0.3f + 0.42f * f)), techo.height * 0.16f, 5, 0.42f), AMARILLO)
                    sx += 26 * u
                }
            }
        } else {
            drawRect(c(ARENISCA_OSC), techo.topLeft, techo.size)
            var vx = techo.left + 8 * u
            while (vx < techo.right) { drawRect(Color.Black.copy(alpha = 0.12f), Offset(vx, techo.top), Size(3 * u, techo.height)); vx += 34 * u }
        }
        drawRect(c(ARENISCA_LUZ), Offset(r.left, techo.bottom - 2 * u), Size(r.width, 2 * u))
        // El friso de uraeus, de pie sobre la gola.
        val friso = Rect(r.left, techo.bottom, r.right, r.top + r.height * 0.43f)
        drawRect(c(ARENISCA), friso.topLeft, friso.size)
        var ux = friso.left + 6 * u
        while (ux < friso.right) { uraeus(Offset(ux, friso.bottom), friso.height, oscuro, noche); ux += 11 * u }
        // La gola: hojas pintadas que se abren hacia arriba, con luz arriba y sombra abajo.
        val gola = Rect(r.left, friso.bottom, r.right, r.top + r.height * 0.76f)
        drawRect(c(ARENISCA_LUZ), gola.topLeft, gola.size)
        val colores = listOf(LAPIS, ROJO, VERDE)
        var gx = gola.left + 2 * u
        var k = 0
        while (gx < gola.right) {
            drawRoundRect(c(colores[k % 3]), Offset(gx, gola.top + gola.height * 0.08f), Size(5 * u, gola.height * 0.84f), CornerRadius(2.5f * u))
            drawLine(c(ARENISCA_LUZ).copy(alpha = 0.6f), Offset(gx + 2.5f * u, gola.top + gola.height * 0.2f), Offset(gx + 2.5f * u, gola.bottom - gola.height * 0.1f), 0.6f * u)
            gx += 8 * u; k++
        }
        drawRect(Color.White.copy(alpha = 0.12f), gola.topLeft, Size(gola.width, gola.height * 0.25f))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(gola.left, gola.bottom - gola.height * 0.35f), Size(gola.width, gola.height * 0.35f))
        // El toro, atado con su cinta roja y negra.
        val toro = Rect(r.left, gola.bottom, r.right, gola.bottom + r.height * 0.07f)
        drawRoundRect(c(ARENISCA_LUZ), toro.topLeft, toro.size, CornerRadius(toro.height / 2))
        var tx = toro.left
        var n = 0
        while (tx < toro.right) { drawLine(c(if (n % 2 == 0) ROJO else NEGRO), Offset(tx, toro.bottom), Offset(tx + 3 * u, toro.top), 1.2f * u); tx += 5 * u; n++ }
        // El arquitrabe con su banda de colores y la sombra sobre la primera hornacina.
        val arq = Rect(r.left, toro.bottom, r.right, r.bottom)
        drawRect(c(ARENISCA), arq.topLeft, arq.size)
        bandaColores(Rect(arq.left, arq.top + arq.height * 0.3f, arq.right, arq.top + arq.height * 0.7f), oscuro, noche)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom - 2 * u), Size(r.width, 2 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = PAPIROS[(v.semilla and 0x7fffffff) % PAPIROS.size]
        val papiro = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El rollo de pie: el cilindro con su elipse abajo, luz a la izquierda y sombra a la derecha (planas).
        val e = minOf(w * 0.3f, 5f * u)
        drawRect(papiro, Offset(0f, e / 2), Size(w, h - e))
        drawOval(papiro, Offset(0f, h - e), Size(w, e))
        drawRect(Color.White.copy(alpha = 0.14f), Offset(w * 0.12f, e / 2), Size(w * 0.14f, h - e))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(w * 0.8f, e / 2), Size(w * 0.2f, h - e * 0.6f))
        // Las fibras cruzadas del papiro y el borde de la hoja de fuera.
        var y = e
        while (y < h - e) { drawLine(c(PAPIRO_OSC).copy(alpha = 0.18f), Offset(0f, y), Offset(w, y), 0.5f * u); y += 3 * u }
        drawLine(c(PAPIRO_OSC), Offset(w * 0.64f, e / 2), Offset(w * 0.62f, h - e / 2), 0.8f * u)
        // Arriba, las vueltas del rollo: más cuanto más escribiste.
        drawOval(c(PAPIRO_OSC), Offset.Zero, Size(w, e))
        val vueltas = 2 + (v.lleno * 3).toInt()
        for (j in 1..vueltas) {
            val f = 1f - j / (vueltas + 1f)
            drawOval(papiro, Offset(w * (1 - f) / 2, e * (1 - f) / 2), Size(w * f, e * f), style = Stroke(0.6f * u))
        }
        // El cartucho con el día.
        val cartucho = Rect(w * 0.18f, h * 0.1f, w * 0.82f, h * 0.42f)
        drawRoundRect(c(LINO), cartucho.topLeft, cartucho.size, CornerRadius(cartucho.width / 2))
        drawRoundRect(c(ROJO), cartucho.topLeft, cartucho.size, CornerRadius(cartucho.width / 2), style = Stroke(1.1f * u))
        drawLine(c(ROJO), Offset(cartucho.left - 1 * u, cartucho.bottom + 1.2f * u), Offset(cartucho.right + 1 * u, cartucho.bottom + 1.2f * u), 1.4f * u)
        textoEn(medidor, v.dia, cartucho.deflate(1.6f * u), letra, c(NEGRO), 13f, peso = FontWeight.SemiBold)
        // El atado de lino, con el sello de barro; de noche, una estrella de Nut en su lugar.
        val atado = Rect(0f, h * 0.5f, w, h * 0.56f)
        drawRect(c(LINO), atado.topLeft, atado.size)
        drawLine(c(LINO_OSC), Offset(0f, atado.center.y), Offset(w, atado.center.y), 0.5f * u)
        val sc = Offset(w * 0.5f, atado.center.y)
        val rs = minOf(w * 0.2f, 4f * u)
        if (v.noche) {
            drawCircle(c(NUT), rs, sc)
            drawPath(estrella(sc, rs * 0.85f, 5, 0.42f), AMARILLO)
        } else {
            drawOval(c(BARRO), Offset(sc.x - rs, sc.y - rs * 0.8f), Size(rs * 2, rs * 1.6f))
            drawCircle(Color.Black.copy(alpha = 0.3f), rs * 0.45f, sc, style = Stroke(0.6f * u))
            drawLine(c(LINO), Offset(sc.x - rs * 0.4f, sc.y + rs * 0.8f), Offset(sc.x - rs * 0.6f, sc.y + rs * 1.8f), 0.8f * u)
        }
        // El mes, con tinta negra.
        textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.6f, w * 0.82f, h * 0.94f), letra, c(NEGRO).copy(alpha = 0.85f), 8.5f, vertical = true, minimo = 4.5f)
    }
}


/**
 * Mesoamérica: el anaquel de códices de una cámara de piedra, hecho de vigas rollizas atadas con cuerda en cada cruce, contra
 * un muro de sillares de caliza. Arriba, la fachada con su banda de columnillas, el mascarón geométrico (frente roja, ojos
 * cuadrados, nariz ganchuda, boca con colmillos y orejeras de jade) y la bóveda falsa: el vano escalonado que se cierra hacia
 * arriba y deja ver el cielo y la selva. Abajo, la plataforma de tres gradas estucadas de rojo y un incensario de tres patas.
 * El mes va en una tira de amate colgada de la viga. Cada entrada es un códice plegado (amoxtli) de pie, con su tapa de piel
 * de jaguar y sus rosetas, los pliegues del biombo por el canto (más cuanto más escribiste), el día en cifras y en barras y
 * puntos mayas sobre una etiqueta de estuco con filete rojo, el mes en una tira y el cordón con su cuenta de jade; de noche,
 * una lunita en lugar del jade. De noche sale la luna en el vano y sube el humo del copal. Distinto del Ideario, que es el
 * muro pintado con la greca escalonada, el talud-tablero y las petacas de palma.
 */
object EstanteMeso : DisenoBiblioteca {
    private val CALIZA = Color(0xFFD8CDB4)
    private val CALIZA_LUZ = Color(0xFFEDE5D2)
    private val CALIZA_OSC = Color(0xFFA8997C)
    private val ESTUCO = Color(0xFFF2E8D2)
    private val CINABRIO = Color(0xFFA3301E)
    private val JADE = Color(0xFF2F7D57)
    private val SELVA = Color(0xFF3F6B45)
    private val VIGA = Color(0xFF7A5634)
    private val VIGA_OSC = Color(0xFF4C3420)
    private val VIGA_LUZ = Color(0xFFA27A50)
    private val CUERDA = Color(0xFFC9B07A)
    private val CUERDA_OSC = Color(0xFF8E7A4E)
    private val AMATE = Color(0xFFDCC7A0)
    private val NEGRO = Color(0xFF2A1810)
    private val BARRO = Color(0xFF8A5A36)
    private val BARRO_OSC = Color(0xFF5A3A22)
    private val TIERRA = Color(0xFF6E5638)
    private val BRASA = Color(0xFFE8804F)
    private val COPAL = Color(0xFFE8E4DA)
    private val LUNA = Color(0xFFF0EAD0)
    private val CIELO = Color(0xFFB9CEC8)
    private val CIELO_NOCHE = Color(0xFF141C3A)
    /** Los tonos de la piel de jaguar de las tapas. */
    private val JAGUARES = listOf(Color(0xFFC98E3A), Color(0xFFD4A04E), Color(0xFFB97C30), Color(0xFFC2964F), Color(0xFFCB8A44))
    private val ROSETA = Color(0xFF2A1A10)
    private val NOCHE = Color(0xFF101630)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luna. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C08), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.5f)
        return r
    }

    override val corona = 0.36f
    override val base = 0.14f
    override val lado = 0.065f
    override val altoRepisa: Dp = 108.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.codice

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 40).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.74f + 0.2f * azar(semilla, 1)

    /** Una atadura de cuerda en [r]: la banda con sus vueltas cruzadas. */
    private fun DrawScope.atadura(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(t(CUERDA, oscuro, noche), r.topLeft, r.size)
        var x = r.left
        while (x < r.right) {
            drawLine(t(CUERDA_OSC, oscuro, noche), Offset(x, r.bottom), Offset(minOf(x + 3 * u, r.right), r.top), 0.8f * u)
            x += 2.4f * u
        }
        drawLine(t(CUERDA_OSC, oscuro, noche), Offset(r.left, r.top), Offset(r.right, r.bottom), 1f * u)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de sillares de caliza.
        val cuerpo = Rect(0f, d.corona.bottom, w, d.base.top)
        drawRect(c(CALIZA), cuerpo.topLeft, cuerpo.size)
        var y = cuerpo.top
        var fila = 0
        while (y < cuerpo.bottom) {
            drawLine(c(CALIZA_OSC).copy(alpha = 0.6f), Offset(0f, y), Offset(w, y), 0.9f * u)
            var x = if (fila % 2 == 0) 0f else 20 * u
            while (x < w) { drawLine(c(CALIZA_OSC).copy(alpha = 0.6f), Offset(x, y), Offset(x, minOf(y + 18 * u, cuerpo.bottom)), 0.9f * u); x += 40 * u }
            y += 18 * u; fila++
        }
        // Cada repisa: la sombra de la viga de arriba.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = 0.1f), r.topLeft, r.size)
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.3f), r.topLeft, Size(r.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(r.left, r.top + 7 * u), Size(r.width, 6 * u))
        }
        // Los costados: postes rollizos con su corteza y sus nudos.
        listOf(d.izquierda, d.derecha).forEachIndexed { s, r ->
            val poste = Rect(r.left + r.width * 0.12f, r.top - 6 * u, r.right - r.width * 0.12f, r.bottom + d.base.height * 0.3f)
            drawRoundRect(c(VIGA), poste.topLeft, poste.size, CornerRadius(poste.width / 2))
            drawRect(c(VIGA_LUZ).copy(alpha = 0.6f), Offset(poste.left + poste.width * 0.18f, poste.top + 3 * u), Size(poste.width * 0.16f, poste.height - 6 * u))
            drawRect(c(VIGA_OSC).copy(alpha = 0.8f), Offset(poste.right - poste.width * 0.28f, poste.top + 3 * u), Size(poste.width * 0.28f, poste.height - 6 * u))
            vetas(poste, c(VIGA_OSC).copy(alpha = 0.4f), 120 + s, densidad = 0.8f, vertical = true, grosor = 0.6f)
            for (j in 0 until 3) {
                val o = Offset(poste.center.x, poste.top + poste.height * (0.2f + 0.3f * j + 0.08f * azar(s * 7 + j, 2)))
                drawOval(c(VIGA_OSC), Offset(o.x - 2 * u, o.y - 3 * u), Size(4 * u, 6 * u))
            }
        }
        // Las tablas: vigas rollizas tendidas, con luz arriba y sombra abajo.
        d.tablas.forEachIndexed { q, r ->
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom), Size(r.width, 3 * u))
            drawRoundRect(c(VIGA), Offset(r.left - 6 * u, r.top), Size(r.width + 12 * u, r.height), CornerRadius(r.height / 2))
            vetas(r, c(VIGA_OSC).copy(alpha = 0.45f), 130 + q, densidad = 0.7f, grosor = 0.6f)
            drawRect(c(VIGA_LUZ).copy(alpha = 0.7f), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, r.height * 0.14f))
            drawRect(c(VIGA_OSC).copy(alpha = 0.7f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.24f))
        }
        // Las ataduras de cuerda en cada cruce de poste y viga.
        d.tablas.forEach { r ->
            atadura(Rect(d.izquierda.left + d.izquierda.width * 0.08f, r.top - 2 * u, d.izquierda.right + 6 * u, r.bottom + 2 * u), oscuro, noche)
            atadura(Rect(d.derecha.left - 6 * u, r.top - 2 * u, d.derecha.right - d.derecha.width * 0.08f, r.bottom + 2 * u), oscuro, noche)
        }
        // El rótulo de cada mes: una tira de amate colgada de la viga con dos cordeles.
        d.placas.forEach { p ->
            val r = p.r
            listOf(0.15f, 0.85f).forEach { f -> drawLine(c(CUERDA), Offset(r.left + r.width * f, r.top - 3 * u), Offset(r.left + r.width * f, r.top + 2 * u), 1.2f * u) }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(c(AMATE), r.topLeft, r.size)
            drawRect(c(CINABRIO), r.topLeft, r.size, style = Stroke(1f * u))
            val filete = r.deflate(2 * u)
            drawRect(c(CINABRIO).copy(alpha = 0.5f), filete.topLeft, filete.size, style = Stroke(0.5f * u))
            textoEn(medidor, p.texto, Rect(r.left + 4 * u, r.top + 1 * u, r.right - 4 * u, r.bottom - 1 * u), letra, c(NEGRO), 10f, peso = FontWeight.SemiBold, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: la plataforma de tres gradas, estucadas de rojo, sobre la tierra.
        val b = d.base
        drawRect(c(CALIZA), b.topLeft, b.size)
        val gradas = 3
        val altoGrada = b.height * 0.26f
        for (j in 0 until gradas) {
            val y0 = b.top + altoGrada * j
            val sangria = (gradas - 1 - j) * b.width * 0.03f
            val g = Rect(b.left + sangria, y0, b.right - sangria, y0 + altoGrada)
            drawRect(c(CINABRIO), g.topLeft, g.size)
            drawRect(c(ESTUCO), g.topLeft, Size(g.width, 2.4f * u))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(g.left, g.bottom - 2 * u), Size(g.width, 2 * u))
            for (k in 0 until 3) {
                val gx = g.left + g.width * azar(j * 5 + k, 9)
                drawLine(c(ESTUCO).copy(alpha = 0.35f), Offset(gx, g.top + 3 * u), Offset(gx + 3 * u, g.bottom - 2 * u), 0.6f * u)
            }
        }
        val piso = b.top + altoGrada * gradas
        drawRect(c(TIERRA), Offset(0f, piso), Size(w, h - piso))
        incensario(Offset(w * 0.82f, h - 1.5f * u), b.height * 0.55f, oscuro, noche)
    }

    /** El incensario de barro de tres patas; de noche, con las brasas encendidas y el humo del copal subiendo. */
    private fun DrawScope.incensario(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val boca = pie.y - alto * 0.7f
        if (noche) {
            // Las brasas: el segundo y último degradado del mueble (el primero es la luz de la luna).
            mancha(Offset(pie.x, boca), alto * 0.6f, BRASA.copy(alpha = 0.5f))
            for (j in 0 until 3) {
                val x0 = pie.x + (j - 1) * alto * 0.12f
                val humo = Path().apply {
                    moveTo(x0, boca - 2 * u)
                    cubicTo(x0 - alto * 0.5f, boca - alto * 1.2f, pie.x + alto * 0.6f, boca - alto * 2.0f, pie.x - alto * 0.1f + j * alto * 0.2f, boca - alto * (3.2f + 0.5f * j))
                    cubicTo(pie.x - alto * 0.7f, boca - alto * 4.0f, pie.x + alto * 0.3f, boca - alto * 4.4f, pie.x - alto * 0.2f, boca - alto * (5f + 0.4f * j))
                }
                drawPath(humo, COPAL.copy(alpha = 0.22f - 0.05f * j), style = Stroke((5 - j) * u, cap = StrokeCap.Round))
            }
        }
        for (s in listOf(-1f, 0f, 1f)) {
            drawLine(c(BARRO_OSC), Offset(pie.x + s * alto * 0.22f, pie.y), Offset(pie.x + s * alto * 0.15f, pie.y - alto * 0.3f), 2.2f * u, cap = StrokeCap.Round)
        }
        val cuenco = Path().apply {
            moveTo(pie.x - alto * 0.42f, boca); lineTo(pie.x + alto * 0.42f, boca)
            quadraticTo(pie.x + alto * 0.38f, pie.y - alto * 0.25f, pie.x, pie.y - alto * 0.25f)
            quadraticTo(pie.x - alto * 0.38f, pie.y - alto * 0.25f, pie.x - alto * 0.42f, boca)
            close()
        }
        drawPath(cuenco, c(BARRO))
        drawLine(c(CINABRIO), Offset(pie.x - alto * 0.38f, boca + alto * 0.08f), Offset(pie.x + alto * 0.38f, boca + alto * 0.08f), 1.2f * u)
        for (j in -1..1) drawCircle(c(NEGRO).copy(alpha = 0.6f), 1.1f * u, Offset(pie.x + j * alto * 0.16f, boca + alto * 0.2f))
        drawRect(c(BARRO_OSC), Offset(pie.x - alto * 0.45f, boca - 1.6f * u), Size(alto * 0.9f, 1.6f * u))
        if (noche) for (j in 0 until 4) drawCircle(BRASA, 1.2f * u, Offset(pie.x + (j - 1.5f) * alto * 0.12f, boca - 0.8f * u))
    }

    /** El mascarón de piedra, todo de formas geométricas: frente, ojos cuadrados, nariz ganchuda, boca con colmillos, orejeras. */
    private fun DrawScope.mascaron(m: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val cara = Rect(m.left + m.width * 0.16f, m.top, m.right - m.width * 0.16f, m.bottom)
        drawRect(c(CALIZA_LUZ), cara.topLeft, cara.size)
        drawRect(c(CALIZA_OSC), cara.topLeft, cara.size, style = Stroke(1f * u))
        val frente = Rect(cara.left, cara.top, cara.right, cara.top + cara.height * 0.22f)
        drawRect(c(CINABRIO).copy(alpha = 0.85f), frente.topLeft, frente.size)
        for (j in 0 until 3) {
            val q = frente.height * 0.6f
            drawRect(c(CALIZA_LUZ), Offset(frente.left + frente.width * (0.2f + 0.3f * j) - q / 2, frente.center.y - q / 2), Size(q, q), style = Stroke(1f * u))
        }
        for (s in listOf(-1f, 1f)) {
            val oc = Offset(cara.center.x + s * cara.width * 0.25f, cara.top + cara.height * 0.42f)
            val lo = cara.height * 0.15f
            drawRect(c(CALIZA_OSC), Offset(oc.x - lo, oc.y - lo * 0.8f), Size(lo * 2, lo * 1.6f))
            drawRect(c(CALIZA_LUZ), Offset(oc.x - lo * 0.6f, oc.y - lo * 0.45f), Size(lo * 1.2f, lo * 0.9f))
            drawRect(c(NEGRO), Offset(oc.x - lo * 0.25f, oc.y - lo * 0.2f), Size(lo * 0.5f, lo * 0.4f))
        }
        val nariz = Path().apply {
            moveTo(cara.center.x - cara.width * 0.07f, cara.top + cara.height * 0.28f)
            lineTo(cara.center.x + cara.width * 0.07f, cara.top + cara.height * 0.28f)
            lineTo(cara.center.x + cara.width * 0.07f, cara.top + cara.height * 0.6f)
            quadraticTo(cara.center.x + cara.width * 0.16f, cara.top + cara.height * 0.68f, cara.center.x + cara.width * 0.06f, cara.top + cara.height * 0.74f)
            lineTo(cara.center.x - cara.width * 0.07f, cara.top + cara.height * 0.66f)
            close()
        }
        drawPath(nariz, c(CALIZA))
        drawPath(nariz, c(CALIZA_OSC), style = Stroke(0.8f * u))
        val boca = Rect(cara.left + cara.width * 0.22f, cara.top + cara.height * 0.78f, cara.right - cara.width * 0.22f, cara.bottom - cara.height * 0.04f)
        drawRect(c(NEGRO).copy(alpha = 0.85f), boca.topLeft, boca.size)
        for (f in listOf(0.18f, 0.82f)) {
            val cx = boca.left + boca.width * f
            val colmillo = Path().apply { moveTo(cx - boca.height * 0.3f, boca.top); lineTo(cx + boca.height * 0.3f, boca.top); lineTo(cx, boca.bottom - boca.height * 0.1f); close() }
            drawPath(colmillo, c(CALIZA_LUZ))
        }
        for (s in listOf(-1f, 1f)) {
            val oc = Offset(if (s < 0) m.left + m.width * 0.08f else m.right - m.width * 0.08f, cara.top + cara.height * 0.45f)
            val ro = m.width * 0.07f
            drawCircle(c(JADE), ro, oc)
            drawCircle(c(CALIZA_LUZ), ro * 0.45f, oc)
            drawRect(c(JADE), Offset(oc.x - ro * 0.15f, oc.y - ro * 0.15f), Size(ro * 0.3f, ro * 0.3f))
        }
    }

    /** La fachada con sus columnillas, el mascarón y la bóveda falsa (de noche, con la luna en el vano). */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(CALIZA), r.topLeft, r.size)
        var y = r.top + r.height * 0.12f
        while (y < r.bottom) { drawLine(c(CALIZA_OSC).copy(alpha = 0.5f), Offset(r.left, y), Offset(r.right, y), 0.8f * u); y += 16 * u }
        // La banda de columnillas arriba, con su moldura.
        val banda = Rect(r.left, r.top, r.right, r.top + r.height * 0.07f)
        drawRect(c(CALIZA_OSC), banda.topLeft, banda.size)
        var bx = banda.left + 1.5f * u
        while (bx < banda.right) {
            drawRoundRect(c(CALIZA_LUZ), Offset(bx, banda.top + 1 * u), Size(4 * u, banda.height - 2 * u), CornerRadius(2 * u))
            drawLine(c(CALIZA_OSC), Offset(bx, banda.center.y), Offset(bx + 4 * u, banda.center.y), 0.8f * u)
            bx += 6 * u
        }
        drawRect(c(CALIZA_LUZ), Offset(r.left, banda.bottom), Size(r.width, 2.4f * u))
        // La bóveda falsa: el vano escalonado que se cierra hacia arriba.
        val cx = r.center.x
        val cima = r.top + r.height * 0.42f
        val izq = r.left + r.width * 0.08f
        val der = r.right - r.width * 0.08f
        val largo = cx - r.width * 0.05f - izq
        val gradas = 6
        val vano = Path().apply {
            moveTo(izq, r.bottom)
            for (j in 0 until gradas) {
                val f0 = j.toFloat() / gradas; val f1 = (j + 1f) / gradas
                val y1 = r.bottom - (r.bottom - cima) * f1
                lineTo(izq + largo * f0, y1)
                lineTo(izq + largo * f1, y1)
            }
            lineTo(der - largo, cima)
            for (j in gradas - 1 downTo 0) {
                val f0 = j.toFloat() / gradas; val f1 = (j + 1f) / gradas
                val y0 = r.bottom - (r.bottom - cima) * f0
                val y1 = r.bottom - (r.bottom - cima) * f1
                lineTo(der - largo * f0, y1)
                lineTo(der - largo * f0, y0)
            }
            close()
        }
        drawPath(vano, if (noche) CIELO_NOCHE else c(CIELO))
        clipPath(vano) {
            if (noche) {
                // La luna en el vano: el primer degradado del mueble.
                val luna = Offset(cx + r.width * 0.1f, cima + (r.bottom - cima) * 0.32f)
                mancha(luna, r.height * 0.3f, LUNA.copy(alpha = 0.35f))
                drawCircle(LUNA, r.height * 0.07f, luna)
                drawCircle(mezcla(LUNA, CIELO_NOCHE, 0.25f), r.height * 0.018f, Offset(luna.x - r.height * 0.02f, luna.y + r.height * 0.015f))
                for (j in 0 until 8) drawCircle(LUNA.copy(alpha = 0.7f), (0.6f + 0.6f * azar(j, 81)) * u, Offset(izq + (der - izq) * azar(j, 82), cima + (r.bottom - cima) * azar(j, 83)))
            } else {
                // La selva a lo lejos.
                for (j in 0 until 12) {
                    drawCircle(c(SELVA).copy(alpha = 0.8f), (10 + 7 * azar(j, 84)) * u, Offset(izq + (der - izq) * j / 11f, r.bottom + 3 * u))
                }
            }
        }
        drawPath(vano, c(CALIZA_OSC), style = Stroke(1.6f * u))
        mascaron(Rect(cx - r.width * 0.15f, banda.bottom + r.height * 0.03f, cx + r.width * 0.15f, cima - r.height * 0.04f), oscuro, noche)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom - 2 * u), Size(r.width, 2 * u))
    }

    /** El número [n] en la cuenta maya: barras de cinco y puntos de uno, en niveles de veinte; el cero es la concha. */
    private fun DrawScope.numeroMaya(n: Int, r: Rect, tinta: Color, concha: Color) {
        val u = density
        val niveles = if (n >= 20) listOf(n / 20, n % 20) else listOf(n)
        val altoNivel = r.height / niveles.size
        niveles.forEachIndexed { k, valor ->
            val celda = Rect(r.left, r.top + altoNivel * k, r.right, r.top + altoNivel * (k + 1)).deflate(altoNivel * 0.06f)
            if (valor == 0) {
                val cc = Rect(celda.center.x - celda.width * 0.3f, celda.center.y - celda.height * 0.22f, celda.center.x + celda.width * 0.3f, celda.center.y + celda.height * 0.22f)
                drawOval(concha, cc.topLeft, cc.size, style = Stroke(1f * u))
                drawLine(concha, Offset(cc.left + cc.width * 0.2f, cc.center.y), Offset(cc.right - cc.width * 0.2f, cc.center.y), 0.8f * u)
            } else {
                val barras = valor / 5
                val puntos = valor % 5
                val filas = barras + (if (puntos > 0) 1 else 0)
                val fila = minOf(celda.height / filas, celda.width * 0.3f)
                val y0 = celda.center.y - fila * filas / 2
                if (puntos > 0) {
                    val rp = minOf(fila * 0.3f, celda.width / (puntos * 2.6f))
                    for (j in 0 until puntos) drawCircle(tinta, rp, Offset(celda.center.x + (j - (puntos - 1) / 2f) * rp * 2.6f, y0 + fila / 2))
                }
                for (j in 0 until barras) {
                    val yb = y0 + fila * ((if (puntos > 0) 1 else 0) + j)
                    drawRoundRect(tinta, Offset(celda.left + celda.width * 0.08f, yb + fila * 0.22f), Size(celda.width * 0.84f, fila * 0.56f), CornerRadius(fila * 0.2f))
                }
            }
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = JAGUARES[(v.semilla and 0x7fffffff) % JAGUARES.size]
        val piel = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // Las páginas plegadas en biombo, por el canto: más pliegues cuanto más escribiste.
        val pliegues = 3 + (v.lleno * 6).toInt()
        val canto = minOf(w * 0.28f, 2.2f * pliegues * u)
        drawRect(c(ESTUCO), Offset(w - canto, h * 0.02f), Size(canto, h * 0.96f))
        for (j in 0 until pliegues) {
            val x = w - canto + canto * (j + 0.5f) / pliegues
            drawLine(c(CINABRIO).copy(alpha = 0.55f), Offset(x, h * 0.02f), Offset(x, h * 0.98f), 0.6f * u)
        }
        val zigzag = Path().apply {
            moveTo(w - canto, h * 0.02f)
            for (j in 0 until pliegues) lineTo(w - canto + canto * (j + 0.5f) / pliegues, h * 0.02f + (if (j % 2 == 0) 1.6f else 0f) * u)
        }
        drawPath(zigzag, c(CALIZA_OSC), style = Stroke(0.6f * u))
        // La tapa de piel de jaguar con sus rosetas.
        val tapa = Rect(0f, 0f, w - canto, h)
        drawRoundRect(piel, tapa.topLeft, tapa.size, CornerRadius(1.5f * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset.Zero, Size(tapa.width * 0.14f, h))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(tapa.right - tapa.width * 0.12f, 0f), Size(tapa.width * 0.12f, h))
        for (j in 0 until 9) {
            val rc = Offset(tapa.width * (0.1f + 0.8f * azar(v.semilla, 10 + j)), h * (0.03f + 0.94f * azar(v.semilla, 20 + j)))
            val rr = (1.3f + 1.2f * azar(v.semilla, 30 + j)) * u
            val giro = 360f * azar(v.semilla, 40 + j)
            for (s in 0 until 3) {
                drawArc(c(ROSETA).copy(alpha = 0.75f), giro + s * 120f, 80f, false, Offset(rc.x - rr, rc.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.9f * u))
            }
            drawCircle(c(ROSETA).copy(alpha = 0.5f), rr * 0.35f, rc)
        }
        // La etiqueta de estuco con filete rojo: el día en cifras y en barras y puntos.
        val etiqueta = Rect(tapa.width * 0.12f, h * 0.07f, tapa.width * 0.88f, h * 0.54f)
        drawRect(c(ESTUCO), etiqueta.topLeft, etiqueta.size)
        drawRect(c(CINABRIO), etiqueta.topLeft, etiqueta.size, style = Stroke(1f * u))
        textoEn(medidor, v.dia, Rect(etiqueta.left, etiqueta.top + 1 * u, etiqueta.right, etiqueta.top + etiqueta.height * 0.34f), letra, c(NEGRO), 13f, peso = FontWeight.SemiBold)
        drawLine(c(CINABRIO).copy(alpha = 0.6f), Offset(etiqueta.left + 2 * u, etiqueta.top + etiqueta.height * 0.36f), Offset(etiqueta.right - 2 * u, etiqueta.top + etiqueta.height * 0.36f), 0.6f * u)
        numeroMaya(v.dia.toIntOrNull() ?: 0, Rect(etiqueta.left + 2 * u, etiqueta.top + etiqueta.height * 0.4f, etiqueta.right - 2 * u, etiqueta.bottom - 2 * u), c(NEGRO), c(CINABRIO))
        // El cordón que ciñe el códice, con su cuenta de jade; de noche, una lunita en su lugar.
        val cy = h * 0.575f
        drawLine(c(CUERDA), Offset(0f, cy), Offset(w, cy), 1.2f * u)
        val cuenta = Offset(tapa.right - minOf(tapa.width * 0.16f, 4 * u), cy)
        val rcuenta = minOf(tapa.width * 0.12f, 2.6f * u)
        if (v.noche) {
            drawCircle(LUNA, rcuenta, cuenta)
            drawCircle(piel, rcuenta * 0.8f, Offset(cuenta.x + rcuenta * 0.45f, cuenta.y - rcuenta * 0.3f))
        } else {
            drawCircle(c(JADE), rcuenta, cuenta)
            drawCircle(Color.White.copy(alpha = 0.35f), rcuenta * 0.35f, Offset(cuenta.x - rcuenta * 0.3f, cuenta.y - rcuenta * 0.3f))
        }
        // El mes, en una tira de amate.
        val tira = Rect(tapa.width * 0.2f, h * 0.61f, tapa.width * 0.8f, h * 0.93f)
        drawRect(c(AMATE), tira.topLeft, tira.size)
        drawRect(c(CINABRIO).copy(alpha = 0.7f), tira.topLeft, tira.size, style = Stroke(0.6f * u))
        textoEn(medidor, v.mes, tira.deflate(1 * u), letra, c(NEGRO), 8.5f, vertical = true, minimo = 4.5f)
    }
}
